package org.BioLayoutExpress3D.Models;

import java.io.*;
import java.nio.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import javax.media.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.common.nio.Buffers;
import static javax.media.opengl.GL.*;
import static javax.media.opengl.GL2.*;
import static javax.media.opengl.GL3.*;
import org.BioLayoutExpress3D.CPUParallelism.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import org.BioLayoutExpress3D.Utils.*;
import static org.BioLayoutExpress3D.Models.ModelRenderingStates.*;
import static org.BioLayoutExpress3D.Models.ModelTypes.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* A ModelShape is the abstract class that functions as a template for the various model shapes classes.
*
* @see org.BioLayoutExpress3D.Models.ModelSettings
* @see org.BioLayoutExpress3D.Models.ModelRenderingStates
* @see org.BioLayoutExpress3D.Models.Lathe3D.Lathe3DShape
* @see org.BioLayoutExpress3D.Models.SuperQuadric.SuperQuadricShape
* @see org.BioLayoutExpress3D.Models.Loaders.OBJModelLoader.OBJModelLoader
* @author Thanos Theo, 2011-2012
* @version 3.0.0.0
*
*/

public abstract class ModelShape
{

    /**
    *  Constant dummy Z texture coordinate.
    */
    public static final float DUMMY_Z_TEX_COORD = -5.0f;

    /**
    *  Constant for debug reporting, 3 decimal points.
    */
    public static final DecimalFormat DECIMAL_FORMAT = (DEBUG_BUILD) ? new DecimalFormat("0.###") : null; // 3 decimal points

    protected static final boolean USE_INTERLEAVED_ARRAY_COORDS_BUFFER = true;
    private static final boolean VERBOSE_RESULTS_REPORTING = false;

    // variables needed for N-CP
    protected final CyclicBarrierTimer cyclicBarrierTimer = (USE_MULTICORE_PROCESS) ? new CyclicBarrierTimer() : null;
    protected final CyclicBarrier threadBarrier = (USE_MULTICORE_PROCESS) ? new CyclicBarrier(NUMBER_OF_AVAILABLE_PROCESSORS + 1, cyclicBarrierTimer) : null;
    protected final CyclicBarrier internalThreadBarrier = (USE_MULTICORE_PROCESS) ? new CyclicBarrier(NUMBER_OF_AVAILABLE_PROCESSORS) : null;

    protected ModelSettings modelSettings = null;
    protected ModelDimensions modelDimensions = null;
    private float scaleFactor = 1.0f;

    /**
    *  Collection of vertices, normals &  texture coordinates for the Lathe3D & SuperQuadric shapes.
    */
    protected float[] vertices = null;
    protected float[] normals = null;
    protected float[] texCoords = null;

    /**
    *  Collection of vertices, normals &  texture coordinates for the OBJ model loader.
    */
    protected ArrayList<Point3D> point3DVertices = null;
    protected ArrayList<Point3D> point3DNormals = null;
    protected ArrayList<Point3D> point3DTexCoords = null;

    private FloatBuffer interleavedArrayCoordsBuffer = null;
    private FloatBuffer allTexture2DCoordsBuffer = null;
    private FloatBuffer allNormal3DCoordsBuffer = null;
    private FloatBuffer allVertex3DCoordsBuffer = null;

    private int modelShapeDisplayList = 0;
    protected IntBuffer VBOTexCoordsID = null;
    protected IntBuffer VBONormalsID   = null;
    protected IntBuffer VBOVerticesID  = null;

    /**
    *  The first (default) ModelShape class constructor.
    */
    public ModelShape() {}

    /**
    *  The second ModelShape class constructor.
    */
    public ModelShape(ModelSettings modelSettings)
    {
        this.modelSettings = modelSettings;
    }

    /**
    *  Reports the model shape settings.
    *  To be implemented in a sub-class.
    */
    protected abstract void reportModelShapeSettings();

    /**
    *  Creates the surface geometry.
    *  To be implemented in a sub-class.
    */
    protected abstract void performCreateGeometry(GL2 gl);

    /**
    *  Checks the vertices model with the ModelDimensions object.
    *  Cannot be overriden by an implementing sub-class.
    */
    protected final void checkVerticesWithModelDimensionsAndCenterModel()
    {
        int numberOfVertices = vertices.length / 3;
        int vertexIndex = 0;
        modelDimensions = new ModelDimensions(modelSettings.shapeName);
        modelDimensions.set(vertices[vertexIndex    ], vertices[vertexIndex + 1], vertices[vertexIndex + 2]);
        for (int i = 1; i < numberOfVertices; i++)
        {
            vertexIndex = 3 * i;

            modelDimensions.update(vertices[vertexIndex    ], vertices[vertexIndex + 1], vertices[vertexIndex + 2]);
        }

        centerModel();
    }

    /**
    *  Positions the model so it's center is at the local coordinate origin.
    */
    private void centerModel()
    {
        // get the model's center point
        Point3D center = modelDimensions.getCenter();
        int numberOfVertices = vertices.length / 3;
        int vertexIndex = 0;
        for (int i = 0; i < numberOfVertices; i++)
        {
            vertexIndex = 3 * i;

            vertices[vertexIndex    ] -= center.getX();
            vertices[vertexIndex + 1] -= center.getY();
            vertices[vertexIndex + 2] -= center.getZ();
        }
    }

    /**
    *  Positions the model so it's center is at the local coordinate origin,
    *  and scale it so its longest dimension is no bigger than maxSize.
    *  Cannot be overriden by an implementing sub-class.
    */
    protected final void centerModel(float maxSize)
    {
        // get the model's center point
        Point3D center = modelDimensions.getCenter();

        // calculate a scale factor
        scaleFactor = 1.0f;
        float largestAxis = modelDimensions.getLargestAxis();
        if (DEBUG_BUILD) println("Model's largestAxis dimension: " + largestAxis);
        if (largestAxis != 0.0f)
            scaleFactor = (maxSize / largestAxis);
        if (DEBUG_BUILD) println("Model's scale factor: " + scaleFactor);

        // modify the model's vertices
        Point3D vertex = null;
        float x = 0.0f, y = 0.0f, z = 0.0f;
        for (int i = 0; i < point3DVertices.size(); i++)
        {
            vertex = point3DVertices.get(i);
            x = ( vertex.getX() - center.getX() ) * scaleFactor;
            vertex.setX(x);
            y = ( vertex.getY() - center.getY() ) * scaleFactor;
            vertex.setY(y);
            z = ( vertex.getZ() - center.getZ() ) * scaleFactor;
            vertex.setZ(z);
        }
    }

    /**
    *  Prints either the vertices or normals array information.
    */
    private void printFloatVertexOrNormalArray(float[] array, boolean isNormalOrVertex)
    {
        int count = 0;
        int numbersPerLine = 6; // multiple of 3
        StringBuilder outputText = new StringBuilder();
        outputText.append("No. of ").append( (isNormalOrVertex) ? "vertices" : "normals" ).append(": ").append(array.length / 3).append("\n");
        for (int i = 0; i < array.length; i += 3)
        {
            if (count == numbersPerLine)
            {
                outputText.append("\n");
                count = 0;
            }

            outputText.append("(").append(DECIMAL_FORMAT.format(array[i])).append(", ").append(DECIMAL_FORMAT.format(array[i + 1])).append(", ").append(DECIMAL_FORMAT.format(array[i + 2])).append(")  ");
            count +=3;
        }
        outputText.append("\n");

        println( outputText.toString() );
    }

    /**
    *  Prints the texture coordinates array information.
    */
    private void printTexCoordsArray(float[] texCoordsArray)
    {
        StringBuilder outputText = new StringBuilder();
        outputText.append("No. of texture coords: ").append(texCoordsArray.length / 2).append("\n");
        for (int i = 0; i < texCoordsArray.length; i += 4)
            outputText.append("(").append(DECIMAL_FORMAT.format(texCoordsArray[i])).append(", ").append(DECIMAL_FORMAT.format(texCoordsArray[i + 1])).append(") " + "(").append(DECIMAL_FORMAT.format(texCoordsArray[i + 2])).append(", ").append(DECIMAL_FORMAT.format(texCoordsArray[i + 3])).append(")");
        outputText.append("\n");

        println( outputText.toString() );
    }

    /**
    *  Prints all array information (vertices, normals & texture coordinates).
    *  Cannot be overriden by an implementing sub-class.
    */
    protected final void reportOnModel()
    {
        if (VERBOSE_RESULTS_REPORTING)
        {
            printFloatVertexOrNormalArray(vertices, true);
            if (modelSettings.usingNormals) printFloatVertexOrNormalArray(normals, false);
            if (modelSettings.usingTexCoords) printTexCoordsArray(texCoords);
            if (modelSettings.centerModel) modelDimensions.reportDimensions();
        }
    }

    /**
    *  Initializes the interleaved buffer.
    */
    private void initializeDisplayList(GL2 gl)
    {
        modelShapeDisplayList = gl.glGenLists(1);
        // if ( gl.glIsList(modelShapeDisplayList) ) // always delete display list, an attempt to delete a list that has never been created is ignored
        gl.glDeleteLists(modelShapeDisplayList, 1);
        gl.glNewList(modelShapeDisplayList, GL_COMPILE);
        drawModelShapeInImmediateMode(gl);
        gl.glEndList();
    }

    /**
    *  Initializes the interleaved buffer.
    *  May be potentially overriden in an implementing class (see the OBJModelLoader class).
    */
    protected void initializeInterleavedBuffer(GL2 gl)
    {
        int bufferSize = vertices.length;
        if (modelSettings.usingNormals) bufferSize += normals.length;
        if (modelSettings.usingTexCoords) bufferSize += vertices.length;
        interleavedArrayCoordsBuffer = Buffers.newDirectFloatBuffer(bufferSize);

        int texCoordIndex = 0;
        for (int vertexIndex = 0; vertexIndex < vertices.length; vertexIndex += 3)
        {
            if (modelSettings.usingTexCoords)
            {
                interleavedArrayCoordsBuffer.put(texCoords[texCoordIndex]);
                interleavedArrayCoordsBuffer.put(texCoords[texCoordIndex + 1]);
                texCoordIndex += 2;
            }
            if (modelSettings.usingNormals)
            {
                interleavedArrayCoordsBuffer.put(normals[vertexIndex]);
                interleavedArrayCoordsBuffer.put(normals[vertexIndex + 1]);
                interleavedArrayCoordsBuffer.put(normals[vertexIndex + 2]);
            }
            interleavedArrayCoordsBuffer.put(vertices[vertexIndex]);
            interleavedArrayCoordsBuffer.put(vertices[vertexIndex + 1]);
            interleavedArrayCoordsBuffer.put(vertices[vertexIndex + 2]);
        }
        interleavedArrayCoordsBuffer.rewind();
    }

    /**
    *  Initializes the non-interleaved buffers.
    *  May be potentially overriden in an implementing class (see the OBJModelLoader class).
    */
    protected void initializeNonInterleavedBuffers(GL2 gl)
    {
        // enable/disable Vertex Array state accordingly
        if (modelSettings.usingTexCoords)
            gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        else
            gl.glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        if (modelSettings.usingNormals)
            gl.glEnableClientState(GL_NORMAL_ARRAY);
        else
            gl.glDisableClientState(GL_NORMAL_ARRAY);
        gl.glEnableClientState(GL_VERTEX_ARRAY);

        if (modelSettings.usingTexCoords)
        {
            allTexture2DCoordsBuffer = Buffers.newDirectFloatBuffer(texCoords.length);
            allTexture2DCoordsBuffer.put(texCoords).rewind();
        }
        if (modelSettings.usingNormals)
        {
            allNormal3DCoordsBuffer = Buffers.newDirectFloatBuffer(normals.length);
            allNormal3DCoordsBuffer.put(normals).rewind();
        }
        allVertex3DCoordsBuffer = Buffers.newDirectFloatBuffer(vertices.length);
        allVertex3DCoordsBuffer.put(vertices).rewind();
    }

    /**
    *  Initializes the VBO buffers.
    *  May be potentially overriden in an implementing sub-class (see the OBJModelLoader class).
    */
    protected void initializeVBOBuffers(GL2 gl)
    {
        if (modelSettings.usingTexCoords)
            VBOTexCoordsID = (IntBuffer)Buffers.newDirectIntBuffer(1).put( new int[] { 0 } ).rewind();
        if (modelSettings.usingNormals)
            VBONormalsID = (IntBuffer)Buffers.newDirectIntBuffer(1).put( new int[] { 0 } ).rewind();
        VBOVerticesID = (IntBuffer)Buffers.newDirectIntBuffer(1).put( new int[] { 0 } ).rewind();

        if (modelSettings.usingTexCoords)
        {
            gl.glGenBuffers(1, VBOTexCoordsID);
            gl.glBindBuffer( GL_ARRAY_BUFFER, VBOTexCoordsID.get(0) );
            gl.glBufferData(GL_ARRAY_BUFFER, allTexture2DCoordsBuffer.capacity() * Buffers.SIZEOF_FLOAT, allTexture2DCoordsBuffer, GL_STATIC_DRAW);
        }
        if (modelSettings.usingNormals)
        {
            gl.glGenBuffers(1, VBONormalsID);
            gl.glBindBuffer( GL_ARRAY_BUFFER, VBONormalsID.get(0) );
            gl.glBufferData(GL_ARRAY_BUFFER, allNormal3DCoordsBuffer.capacity() * Buffers.SIZEOF_FLOAT, allNormal3DCoordsBuffer, GL_STATIC_DRAW);
        }
        gl.glGenBuffers(1, VBOVerticesID);
        gl.glBindBuffer( GL_ARRAY_BUFFER, VBOVerticesID.get(0) );
        gl.glBufferData(GL_ARRAY_BUFFER, allVertex3DCoordsBuffer.capacity() * Buffers.SIZEOF_FLOAT, allVertex3DCoordsBuffer, GL_STATIC_DRAW);
    }

    /**
    *  Creates the geometry OpenGL GPU storage data structures.
    *  Cannot be overriden by an implementing sub-class.
    */
    protected final void createGeometryStorage(GL2 gl)
    {
        if ( modelSettings.modelRenderingState.equals(DISPLAY_LIST) )
        {
            initializeDisplayList(gl);
        }
        else if ( modelSettings.modelRenderingState.equals(VERTEX_ARRAY) )
        {
            if (USE_INTERLEAVED_ARRAY_COORDS_BUFFER)
                initializeInterleavedBuffer(gl);
            else
                initializeNonInterleavedBuffers(gl);
        }
        else if ( modelSettings.modelRenderingState.equals(VBO) )
        {
            initializeNonInterleavedBuffers(gl);
            initializeVBOBuffers(gl);
        }
    }

    /**
    *  Draws the model shape in immediate mode.
    *  May be overriden by an implementing class (see the OBJModelLoader class).
    */
    protected void drawModelShapeInImmediateMode(GL2 gl)
    {
        gl.glBegin(GL_TRIANGLES);
        int texCoordIndex = 0;
        for (int vertexIndex = 0; vertexIndex < vertices.length; vertexIndex += 3)
        {
            if (modelSettings.usingTexCoords)
            {
                if (texCoords != null)
                    gl.glTexCoord2f(texCoords[texCoordIndex], texCoords[texCoordIndex + 1]);
                texCoordIndex += 2;
            }
            if ( modelSettings.usingNormals && (normals != null) )
                gl.glNormal3f(normals[vertexIndex], normals[vertexIndex + 1], normals[vertexIndex + 2]);
            if (vertices != null)
                gl.glVertex3f(vertices[vertexIndex], vertices[vertexIndex + 1], vertices[vertexIndex + 2]);
        }
        gl.glEnd();
    }

    /**
    *  Draws the model shape with the display list.
    */
    private void drawModelShapeWithDisplayList(GL2 gl)
    {
        gl.glCallList(modelShapeDisplayList);
    }

    /**
    *  Draws the model shape with the interleaved vertex array.
    *  May be overriden by an implementing class (see the OBJModelLoader class).
    */
    protected void drawModelShapeWithInterleavedVertexArray(GL2 gl)
    {
        int mode = GL_V3F;
        if      ( modelSettings.usingNormals && !modelSettings.usingTexCoords)
            mode = GL_N3F_V3F;
        else if (!modelSettings.usingNormals &&  modelSettings.usingTexCoords)
            mode = GL_T2F_V3F;
        else if ( modelSettings.usingNormals &&  modelSettings.usingTexCoords)
            mode = GL_T2F_N3F_V3F;

        if (interleavedArrayCoordsBuffer != null)
        {
            gl.glInterleavedArrays(mode, 0, interleavedArrayCoordsBuffer);
            gl.glDrawArrays(GL_TRIANGLES, 0, vertices.length / 3);
        }
    }

    /**
    *  Draws the model shape with non-interleaved vertex arrays.
    *  May be overriden by an implementing class (see the OBJModelLoader class).
    */
    protected void drawModelShapeWithNonInterleavedVertexArrays(GL2 gl)
    {
        if ( modelSettings.usingTexCoords && (allTexture2DCoordsBuffer != null) )
            gl.glTexCoordPointer(2, GL_FLOAT, 0, allTexture2DCoordsBuffer);
        if ( modelSettings.usingNormals && (allNormal3DCoordsBuffer != null) )
            gl.glNormalPointer(GL_FLOAT, 0, allNormal3DCoordsBuffer);

        if (allVertex3DCoordsBuffer != null)
        {
            gl.glVertexPointer(3, GL_FLOAT, 0, allVertex3DCoordsBuffer);
            gl.glDrawArrays(GL_TRIANGLES, 0, vertices.length / 3);
        }
    }

    /**
    *  Draws the model shape with VBOs.
    *  May be overriden by an implementing class (see the OBJModelLoader class).
    */
    protected void drawModelShapeWithVBOs(GL2 gl)
    {
        if (modelSettings.usingTexCoords)
        {
            if (VBOTexCoordsID != null)
            {
                gl.glBindBuffer( GL_ARRAY_BUFFER, VBOTexCoordsID.get(0) );
                gl.glTexCoordPointer(2, GL_FLOAT, 0, 0);
            }
        }
        if (modelSettings.usingNormals)
        {
            if (VBONormalsID != null)
            {
                gl.glBindBuffer( GL_ARRAY_BUFFER, VBONormalsID.get(0) );
                gl.glNormalPointer(GL_FLOAT, 0, 0);
            }
        }
        if (VBOVerticesID != null)
        {
            gl.glBindBuffer( GL_ARRAY_BUFFER, VBOVerticesID.get(0) );
            gl.glVertexPointer(3, GL_FLOAT, 0, 0);
            gl.glDrawArrays(GL_TRIANGLES, 0, vertices.length / 3);
        }

        // unbind VBO to let other Vertex Arrays work ok throughout BL
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    /**
    *  Draws the model shape.
    *  Cannot be overriden by an implementing sub-class.
    */
    public final void drawModelShape(GL2 gl)
    {
        if ( modelSettings.modelRenderingState.equals(IMMEDIATE_MODE) )
        {
            drawModelShapeInImmediateMode(gl);
        }
        else if ( modelSettings.modelRenderingState.equals(DISPLAY_LIST) )
        {
            drawModelShapeWithDisplayList(gl);
        }
        else if ( modelSettings.modelRenderingState.equals(VERTEX_ARRAY) )
        {
            if (USE_INTERLEAVED_ARRAY_COORDS_BUFFER)
                drawModelShapeWithInterleavedVertexArray(gl);
            else
                drawModelShapeWithNonInterleavedVertexArrays(gl);
        }
        else if ( modelSettings.modelRenderingState.equals(VBO) )
        {
            drawModelShapeWithVBOs(gl);
        }
    }

    /**
    *  Disposes the display list.
    */
    private void disposeDisplayList(GL2 gl)
    {
        // if ( gl.glIsList(nodesDisplayList) ) // always delete display list, an attempt to delete a list that has never been created is ignored
        gl.glDeleteLists(modelShapeDisplayList, 1);
    }

    /**
    *  Disposes the interleaved vertex array buffer.
    *  May be overriden by an implementing class (see the OBJModelLoader class).
    */
    protected void disposeInterleavedBuffer(GL2 gl)
    {
        if (interleavedArrayCoordsBuffer != null)
        {
            interleavedArrayCoordsBuffer.clear();
            interleavedArrayCoordsBuffer = null;
        }
    }

    /**
    *  Disposes the non-interleaved vertex array buffers.
    *  May be overriden by an implementing class (see the OBJModelLoader class).
    */
    protected void disposeNonInterleavedBuffers(GL2 gl)
    {
        if (   (modelSettings.modelRenderingState.equals(VERTEX_ARRAY) && !USE_INTERLEAVED_ARRAY_COORDS_BUFFER)
             || modelSettings.modelRenderingState.equals(VBO) )
        {
            // restore Vertex Array state, default is on in BioLayout OpenGL renderer
            if (!modelSettings.usingTexCoords)
                gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);
            if (!modelSettings.usingNormals)
                gl.glEnableClientState(GL_NORMAL_ARRAY);
        }

        if (modelSettings.usingTexCoords)
        {
            if (allTexture2DCoordsBuffer != null)
            {
                allTexture2DCoordsBuffer.clear();
                allTexture2DCoordsBuffer = null;
            }
        }

        if (modelSettings.usingNormals)
        {
            if (allNormal3DCoordsBuffer != null)
            {
                allNormal3DCoordsBuffer.clear();
                allNormal3DCoordsBuffer = null;
            }
        }

        if (allVertex3DCoordsBuffer != null)
        {
            allVertex3DCoordsBuffer.clear();
            allVertex3DCoordsBuffer = null;
        }
    }

    /**
    *  Disposes the VBOs.
    *  May be overriden by an implementing class (see the OBJModelLoader class).
    */
    protected void disposeVBOs(GL2 gl)
    {
        if (modelSettings.usingTexCoords)
            gl.glDeleteBuffers(1, VBOTexCoordsID);
        if (modelSettings.usingNormals)
            gl.glDeleteBuffers(1, VBONormalsID);
        gl.glDeleteBuffers(1, VBOVerticesID);

        if (modelSettings.usingTexCoords)
        {
            VBOTexCoordsID.clear();
            VBOTexCoordsID = null;
        }

        if (modelSettings.usingNormals)
        {
            VBONormalsID.clear();
            VBONormalsID = null;
        }

        VBOVerticesID.clear();
        VBOVerticesID = null;
    }

    /**
    *  Disposes all model shape resources.
    *  Cannot be overriden by an implementing sub-class.
    */
    public final void disposeAllModelShapeResources(GL2 gl)
    {
        if ( modelSettings.modelRenderingState.equals(DISPLAY_LIST) )
        {
            disposeDisplayList(gl);
        }
        else if ( modelSettings.modelRenderingState.equals(VERTEX_ARRAY) )
        {
            if (USE_INTERLEAVED_ARRAY_COORDS_BUFFER)
                disposeInterleavedBuffer(gl);
            else
                disposeNonInterleavedBuffers(gl);
        }
        else if ( modelSettings.modelRenderingState.equals(VBO) )
        {
            disposeNonInterleavedBuffers(gl);
            disposeVBOs(gl);
        }

        if (modelSettings.usingTexCoords)
        {
            if (point3DTexCoords != null)
            {
                point3DTexCoords.clear();
                point3DTexCoords = null;
            }
            texCoords = null;
        }

        if (modelSettings.usingNormals)
        {
            if (point3DNormals != null)
            {
                point3DNormals.clear();
                point3DNormals = null;
            }
            normals = null;
        }

        if (point3DVertices != null)
        {
            point3DVertices.clear();
            point3DVertices = null;
        }
        vertices = null;

        releaseAdditionalResources(gl);
    }

    /**
    *  Releases additional resources.
    *  To be implemented in a sub-class.
    */
    protected abstract void releaseAdditionalResources(GL2 gl);

    /**
    *  Gets the usingNormals variable.
    */
    public boolean getUsingNormals()
    {
        return modelSettings.usingNormals;
    }

    /**
    *  Gets the usingTexCoords variable.
    */
    public boolean getUsingTexCoords()
    {
        return modelSettings.usingTexCoords;
    }

    /**
    *  Gets the shapeName variable.
    */
    public String getShapeName()
    {
        return modelSettings.shapeName;
    }

    /**
    *  Sets the shapeName variable.
    */
    public void setShapeName(String shapeName)
    {
        this.modelSettings.shapeName = shapeName;
    }

    /**
    *  Checks if the model has any textures.
    */
    public boolean getHasTexture()
    {
        return modelSettings.hasTexture;
    }

    /**
    *  Saves the Model Shape file in OBJ file format.
    *  The ModelShape must be either a Lathe3D or a SuperQuadric one.
    */
    public void saveModelShapeOBJFile(FileWriter fileWriter, ModelTypes modelShapeType) throws IOException
    {
        if ( !modelShapeType.equals(OBJ_MODEL_LOADER_SHAPE) )
        {
            String fileType = EnumUtils.splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(modelShapeType);
            fileWriter.write("# Generated by " + VERSION + " (" + BIOLAYOUT_EXPRESS_3D_DOMAIN_URL + ") from Model Shape Editor: " + fileType + "\n");
            fileWriter.write("\n");

            int texCoordIndex = 0;
            for (int vertexIndex = 0; vertexIndex < vertices.length; vertexIndex += 3)
            {
                fileWriter.write("v " + vertices[vertexIndex] + " " + vertices[vertexIndex + 1] + " " + vertices[vertexIndex + 2] + "\n");
                if (modelSettings.usingTexCoords)
                {
                    fileWriter.write("vt " + texCoords[texCoordIndex] + " " + texCoords[texCoordIndex + 1] + "\n");
                    texCoordIndex += 2;
                }
                if (modelSettings.usingNormals)
                    fileWriter.write("vn " + normals[vertexIndex] + " " + normals[vertexIndex + 1] + " " + normals[vertexIndex + 2] + "\n");
            }
            fileWriter.write("\n");

            int numberOfFaces = vertices.length / (3 * 3); // Lathe3D/SuperQuadric shapes use a GL_TRIANGLE for geometry
            int vertexIndex = 0;
            for (int face = 0; face < numberOfFaces; face++)
            {
                vertexIndex++;
                fileWriter.write("f " + vertexIndex + "/" + vertexIndex + "/" + vertexIndex);
                vertexIndex++;
                fileWriter.write(" " + vertexIndex + "/" + vertexIndex + "/" + vertexIndex);
                vertexIndex++;
                fileWriter.write(" " + vertexIndex + "/" + vertexIndex + "/" + vertexIndex + "\n");
            }
            fileWriter.write("\n");
        }
    }

    /**
    *  Gets the model shape all axes.
    */
    public Point3D getAllAxes()
    {
        // has to compesate for the scaleFactor used by the OBJ Model Loader below
        return (modelDimensions != null) ? new Point3D(modelDimensions.getWidth() * scaleFactor, modelDimensions.getHeight() * scaleFactor, modelDimensions.getDepth() * scaleFactor) : new Point3D(2.0f, 2.0f, 2.0f);
    }

    /**
    *  The SuperQuadric shapeName.
    */
    @Override
    public String toString()
    {
        return "Model: " + modelSettings.shapeName;
    }


}
