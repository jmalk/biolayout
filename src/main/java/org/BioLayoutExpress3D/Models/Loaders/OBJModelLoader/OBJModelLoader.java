package org.BioLayoutExpress3D.Models.Loaders.OBJModelLoader;

import java.awt.*;
import java.io.*;
import java.nio.*;
import java.util.*;
import javax.media.opengl.*;
import javax.swing.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.common.nio.Buffers;
import static javax.media.opengl.GL2.*;
import org.BioLayoutExpress3D.DataStructures.*;
import org.BioLayoutExpress3D.Models.*;
import org.BioLayoutExpress3D.StaticLibraries.ArraysAutoBoxUtils;
import org.BioLayoutExpress3D.Utils.*;
import static org.BioLayoutExpress3D.Models.ModelRenderingStates.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* Load the OBJ model, center and scale it.
* The scale comes from the maxSize argument in the constructor, and
* is implemented by changing the vertices of the loaded model.
*
* The model can have vertices, normals and texture coordinates, and
* refer to materials in a MTL file.
*
* The OpenGL commands for rendering the model are stored in
* a display list (modelShapeDisplayList), which is drawn by calls to draw().
*
* Information about the model is printed to the console.
*
* Based on techniques used in the OBJ loading code in the
* JautOGL multiplayer racing game by Evangelos Pournaras
* (http://today.java.net/pub/a/today/2006/10/10/
*  development-of-3d-multiplayer-racing-game.html
*  and https://jautogl.dev.java.net/),
*  and the Asteroids tutorial by Kevin Glass
* (http://www.cokeandcode.com/asteroidstutorial)
*
* @see org.BioLayoutExpress3D.Models.ModelShape
* @author Andrew Davison, 2007, rewrite for BioLayout Express3D using a ModelShape superclass with Display Lists, Vertex Arrays, VBO & OpenGL 4.0 GL_PATCHES support by Thanos Theo, 2011-2012
* @version 3.0.0.0
*
*/

public class OBJModelLoader extends ModelShape
{

    /**
    *  Model faces.
    */
    private Faces faces = null;

    /**
    *  Materials used by faces.
    */
    private FaceMaterials faceMaterials = null;

    /**
    *  Materials defined in MTL file.
    */
    private Materials materials = null;

    /**
    *  Whether the 3D model uses 3D or 2D texture coords.
    */
    private boolean has3DTexCoords = false;

    /**
    *  HashMap that stores all relevant 3D model data. To be used with Interleaved Vertex Array OpenGL support.
    */
    private HashMap<Tuple2<Integer, String>, Tuple2<Integer, FloatBuffer>> allInterleavedDataBuffersMap = null;

    /**
    *  HashMap that stores all relevant 3D model data. To be used with Non-Interleaved Vertex Arrays/VBOs OpenGL support.
    */
    private HashMap<Tuple2<Integer, String>, Tuple3<FloatBuffer, FloatBuffer, FloatBuffer>> allNonInterleavedDataBuffersMap = null;

    /**
    *  Size of allNonInterleavedDataBuffersMap data structure.
    */
    private int allNonInterleavedDataBuffersSize = 0;

    /**
    * Variable useMaterialColors used to draw material colors.
    */
    private boolean useMaterialColors = true;

    /**
    *  The first OBJModelLoader class constructor.
    */
    public OBJModelLoader(GL2 gl, Component component, String directoryFilename , String modelFilename, ModelRenderingStates modelRenderingState)
    {
        this(gl, component, directoryFilename, modelFilename, 1.0f, modelRenderingState, false, true);
    }

    /**
    *  The second OBJModelLoader class constructor.
    */
    public OBJModelLoader(GL2 gl, Component component, String directoryFilename , String modelFilename, float maxSize, ModelRenderingStates modelRenderingState)
    {
        this(gl, component, directoryFilename, modelFilename, maxSize, modelRenderingState, false, true);
    }

    /**
    *  The third OBJModelLoader class constructor.
    */
    public OBJModelLoader(GL2 gl, Component component, String directoryFilename, String modelFilename, ModelRenderingStates modelRenderingState, boolean loadFromFileOrFromJar)
    {
        this(gl, component, directoryFilename, modelFilename, 1.0f, modelRenderingState, loadFromFileOrFromJar, true);
    }

    /**
    *  The fourth OBJModelLoader class constructor.
    */
    public OBJModelLoader(GL2 gl, Component component, String directoryFilename, String modelFilename, float maxSize, ModelRenderingStates modelRenderingState, boolean loadFromFileOrFromJar)
    {
        this(gl, component, directoryFilename, modelFilename, maxSize, modelRenderingState, loadFromFileOrFromJar, true);
    }

    /**
    *  The fifth OBJModelLoader class constructor.
    */
    public OBJModelLoader(GL2 gl, Component component, String directoryFilename, String modelFilename, float maxSize, ModelRenderingStates modelRenderingState, boolean loadFromFileOrFromJar, boolean useMaterialColors)
    {
        super();

        this.useMaterialColors = useMaterialColors;
        modelSettings = new ModelSettings(false, false, modelRenderingState, modelFilename.substring( 0, modelFilename.lastIndexOf(".") ), true);

        initModelDataStructures();
        parseOBJModelFile(component, directoryFilename, modelFilename, loadFromFileOrFromJar);
        if (DEBUG_BUILD) reportModelShapeSettings();
        centerModel(maxSize);
        createGeometryStorage(gl);
    }

    /**
    *  Initializes the model's data structures.
    */
    private void initModelDataStructures()
    {
        point3DVertices = new ArrayList<Point3D>();
        point3DNormals = new ArrayList<Point3D>();
        point3DTexCoords = new ArrayList<Point3D>();

        faces = new Faces(point3DVertices, point3DNormals, point3DTexCoords);
        faceMaterials = new FaceMaterials();
        modelDimensions = new ModelDimensions(modelSettings.shapeName);
    }

    /**
    *  Parses the OBJ file line-by-line.
    */
    private boolean parseOBJModelFile(Component component, String directoryFilename, String modelFilename, boolean loadFromFileOrFromJar)
    {
        String objModelPathAndFilename = directoryFilename + modelFilename;
        BufferedReader objModelBufferedReader = null;

        boolean isLoaded = true;   // hope things will go okay
        int lineNumber = 0;
        String line = "";
        boolean isFirstVertexCoord = true;
        boolean isFirstTexCoord = true;
        int numberOfFaces = 0;

        try
        {
            objModelBufferedReader = (loadFromFileOrFromJar)
                                    ? new BufferedReader( new FileReader(objModelPathAndFilename) )
                                    : new BufferedReader( new InputStreamReader( this.getClass().getResourceAsStream(objModelPathAndFilename) ) );

            while ( ( (line = objModelBufferedReader.readLine() ) != null) && isLoaded )
            {
                lineNumber++;
                if (line.length() > 0)
                {
                    line = line.trim();

                    if ( line.startsWith("v ") ) // vertex
                    {
                        isLoaded = addVertex(line, isFirstVertexCoord);
                        if (isFirstVertexCoord)
                            isFirstVertexCoord = false;
                    }
                    else if ( line.startsWith("vt") ) // tex coord
                    {
                        isLoaded = modelSettings.usingTexCoords = addTexCoord(line, isFirstTexCoord);
                        if (isFirstTexCoord)
                            isFirstTexCoord = false;
                    }
                    else if ( line.startsWith("vn") ) // normal
                        isLoaded = modelSettings.usingNormals = addNormal(line);
                    else if ( line.startsWith("f ") ) // face
                    {
                        isLoaded = faces.addFace(line);
                        numberOfFaces++;
                    }
                    else if ( line.startsWith("mtllib ") ) // load material
                    {
                        materials = new Materials(directoryFilename + line.substring(7), loadFromFileOrFromJar);
                        if (!materials.parse())
                        {
                            materials = null;
                        }
                    }
                    else if ( line.startsWith("usemtl ") ) // use material
                        faceMaterials.addUse(line.substring(7), numberOfFaces);
                    else if (line.charAt(0) == 'g') // group name
                    {
                        // not implemented
                    }
                    else if (line.charAt(0) == 's') // smoothing group
                    {
                        // not implemented
                    }
                    else if (line.charAt(0) == '#') // comment line
                        continue;
                    else
                        if (DEBUG_BUILD) println("Ignoring line " + lineNumber + " : " + line);
                }
            }
        }
        catch (IOException ioExc)
        {
            if (DEBUG_BUILD) println("IOException while parsing the model file " + modelFilename + " in OBJModelLoader.parseOBJModelFile(): " + ioExc.getMessage());
            JOptionPane.showMessageDialog(component, "Model filename: " + ioExc.getMessage(), "Error while parsing the OBJ file!", JOptionPane.ERROR_MESSAGE);
        }
        finally
        {
            try
            {
                if (objModelBufferedReader != null) objModelBufferedReader.close();
            }
            catch (IOException ioe)
            {
                if (DEBUG_BUILD) println("IOException while closing the stream in OBJModelLoader.parseOBJModelFile():\n" + ioe.getMessage());
            }
        }

        if (materials != null) modelSettings.hasTexture = materials.hasMaterialTextures();
        if (DEBUG_BUILD) println(isLoaded ? this.toString() + " loaded successfully"  + ( (modelSettings.hasTexture) ? " with texture(s)." : "." ) : "Error while loading model: " + this.toString());

        return isLoaded;
    }

    /**
    *  Adds vertex from line "v x y z" to vertices ArrayList,
    *  and updates the model dimension's info.
    */
    private boolean addVertex(String line, boolean isFirstVertexCoord)
    {
        Point3D vertex = readPoint3D(line);
        if (vertex != null)
        {
            point3DVertices.add(vertex);
            if (isFirstVertexCoord)
                modelDimensions.set(vertex);
            else
                modelDimensions.update(vertex);

            return true;
        }
        else
            return false;
    }

    /**
    *  The line starts with an OBJ word ("v" or "vn"), followed
    *  by three floats (x, y, z) separated by spaces.
    *  Package access, also used from the Materials class.
    */
    static Point3D readPoint3D(String line)
    {
        Scanner scanner = new Scanner(line);
        scanner.next(); // skip the OBJ word

        try
        {
            float x = Float.parseFloat( scanner.next() );
            float y = Float.parseFloat( scanner.next() );
            float z = Float.parseFloat( scanner.next() );

            return new Point3D(x, y, z);
        }
        catch (NumberFormatException exc)
        {
            if (DEBUG_BUILD) println("OBJModelLoader.readPoint3D() error: " + exc.getMessage());
        }

        return null; // means an error occurred
    }

    /**
    *  Adds the texture coordinate from the line "vt x y z" to
    *  the texCoords ArrayList. There may only be two tex coords
    *  on the line, which is determined by looking at the first
    *  texture coord line.
    */
    private boolean addTexCoord(String line, boolean isFirstTexCoord)
    {
        if (isFirstTexCoord)
        {
            has3DTexCoords = checkTexCoordPoint3D(line);
            if (DEBUG_BUILD) println("\nUsing 3D texture coords for " + this.toString() + ": " + has3DTexCoords);
        }

        Point3D texCoord = readTexCoordPoint(line);
        if (texCoord != null)
        {
            point3DTexCoords.add(texCoord);

            return true;
        }
        else
            return false;
    }

    /**
    *  Checks if the line has 4 elements, which will be
    *  the "vt" token and 3 texture coords in this case.
    */
    private boolean checkTexCoordPoint3D(String line)
    {
        return (line.split("\\s+").length == 4);
    }

    /**
    *  The line starts with a "vt" OBJ word and
    *  two or three floats (x, y, z) for the tex coords separated
    *  by spaces. If there are only two coords, then the z-value
    *  is assigned a dummy value, DUMMY_Z_TC.
    */
    private Point3D readTexCoordPoint(String line)
    {
        Scanner scanner = new Scanner(line);
        scanner.next(); // skip "vt" OBJ word

        try
        {
            float x = Float.parseFloat( scanner.next() );
            float y = Float.parseFloat( scanner.next() );
            float z = (has3DTexCoords) ? Float.parseFloat( scanner.next() ) : DUMMY_Z_TEX_COORD;

            return new Point3D(x, y, z);
        }
        catch (NumberFormatException exc)
        {
            if (DEBUG_BUILD) println("OBJModelLoader.readTexCoordPoint() error: " + exc.getMessage());
        }

        return null; // means an error occurred
    }

    /**
    *  Adds normal from line "vn x y z" to the normals ArrayList.
    */
    private boolean addNormal(String line)
    {
        Point3D normalCoord = readPoint3D(line);
        if (normalCoord != null)
        {
            point3DNormals.add(normalCoord);

            return true;
        }
        else
            return false;
    }

    /**
    *  Creates the surface geometry.
    *  Not needed in OBJModelClass, as its geometry is being loaded from an OBJ file.
    */
    @Override
    protected final void performCreateGeometry(GL2 gl) {}

    /**
    *  Initializes the data structures for buffer usage.
    */
    private HashMap<Tuple2<Integer, String>, Tuple3<ArrayList<Float>, ArrayList<Float>, ArrayList<Float>>> initializeDataStructuresForBufferUsage()
    {
        // render the model face-by-face, -1 -> dummy previousPolygonType OpenGL value
        HashMap<Tuple2<Integer, String>, Tuple3<ArrayList<Float>, ArrayList<Float>, ArrayList<Float>>> allData = new HashMap<Tuple2<Integer, String>, Tuple3<ArrayList<Float>, ArrayList<Float>, ArrayList<Float>>>();
        Tuple2<Tuple2<Integer, String>, Tuple3<ArrayList<Float>, ArrayList<Float>, ArrayList<Float>>> keyDataPair = Tuples.tuple( Tuples.tuple(-1, ""), Tuples.tuple( (ArrayList<Float>)null, (ArrayList<Float>)null, (ArrayList<Float>)null ) );
        String faceMaterial = null;
        boolean applyNewMaterial = false;
        Texture texture = null;
        if (materials != null) materials.resetDrawnMaterialName(false);
        for (int index = 0; index < faces.getNumberOfFaces(); index++)
        {
            faceMaterial = faceMaterials.findMaterial(index); // get material used by face i
            applyNewMaterial = !faceMaterial.isEmpty() && (materials != null);
            if (applyNewMaterial)
            {
                // for the faces, to enforce proper glBegin() & glEnd() grouping before the glBindTexture() in drawWithMaterial()
                allData.put(keyDataPair.first, keyDataPair.second);
                if (materials != null) texture = materials.checkDrawWithMaterial(faceMaterial); // check drawing using this material
            }
            keyDataPair = faces.cacheFaceData(index, (texture != null) ? texture.getMustFlipVertically() : false, applyNewMaterial, faceMaterial, keyDataPair, allData); // draw index face
        }

        if (modelSettings.hasTexture) // implies materials != null
            materials.switchOffTexture();

        faceMaterials.clearAllFaceMaterials();
        faces.clearAllFaces();

        return allData;
    }

    /**
    *  Converts the given data structure to interleaved buffer format.
    */
    private void convertToInterleavedBuffer(HashMap<Tuple2<Integer, String>, Tuple3<ArrayList<Float>, ArrayList<Float>, ArrayList<Float>>> allData)
    {
        allInterleavedDataBuffersMap = new HashMap<Tuple2<Integer, String>, Tuple2<Integer, FloatBuffer>>();
        Tuple3<ArrayList<Float>, ArrayList<Float>, ArrayList<Float>> arrayLists = null;
        int bufferSize = 0;
        FloatBuffer interleavedDataBuffer = null;
        for ( Tuple2<Integer, String> key : allData.keySet() )
        {
            arrayLists = allData.get(key);
            // fail safe check
            if ( ( arrayLists.first == null || arrayLists.first.isEmpty() ) && ( arrayLists.second == null || arrayLists.second.isEmpty() ) && ( arrayLists.third == null || arrayLists.third.isEmpty() ) ) continue;

            bufferSize = arrayLists.third.size();
            if (modelSettings.usingNormals) bufferSize += arrayLists.second.size();
            if (modelSettings.usingTexCoords) bufferSize += arrayLists.first.size();
            interleavedDataBuffer = Buffers.newDirectFloatBuffer(bufferSize);

            int texCoordIndex = 0;
            for (int vertexIndex = 0; vertexIndex < arrayLists.third.size(); vertexIndex += 3)
            {
                if (modelSettings.usingTexCoords)
                {
                    if (has3DTexCoords)
                    {
                        interleavedDataBuffer.put( arrayLists.first.get(texCoordIndex) );
                        interleavedDataBuffer.put( arrayLists.first.get(texCoordIndex + 1) );
                        interleavedDataBuffer.put( arrayLists.first.get(texCoordIndex + 2) );
                        texCoordIndex += 3;
                    }
                    else
                    {
                        interleavedDataBuffer.put( arrayLists.first.get(texCoordIndex) );
                        interleavedDataBuffer.put( arrayLists.first.get(texCoordIndex + 1) );
                        texCoordIndex += 2;
                    }
                }
                if (modelSettings.usingNormals)
                {
                    interleavedDataBuffer.put( arrayLists.second.get(vertexIndex) );
                    interleavedDataBuffer.put( arrayLists.second.get(vertexIndex + 1) );
                    interleavedDataBuffer.put( arrayLists.second.get(vertexIndex + 2) );
                }
                interleavedDataBuffer.put( arrayLists.third.get(vertexIndex) );
                interleavedDataBuffer.put( arrayLists.third.get(vertexIndex + 1) );
                interleavedDataBuffer.put( arrayLists.third.get(vertexIndex + 2) );
            }
            interleavedDataBuffer.rewind();

            allInterleavedDataBuffersMap.put( key, Tuples.tuple(arrayLists.third.size(), interleavedDataBuffer) );

            arrayLists.first.clear();
            arrayLists.second.clear();
            arrayLists.third.clear();
        }
        allData.clear();
    }

    /**
    *  Converts the given data structure to non-interleaved buffer format.
    */
    private void convertToNonInterleavedBuffers(HashMap<Tuple2<Integer, String>, Tuple3<ArrayList<Float>, ArrayList<Float>, ArrayList<Float>>> allData)
    {
        allNonInterleavedDataBuffersMap = new HashMap<Tuple2<Integer, String>, Tuple3<FloatBuffer, FloatBuffer, FloatBuffer>>();
        Tuple3<ArrayList<Float>, ArrayList<Float>, ArrayList<Float>> arrayLists = null;
        FloatBuffer texture2DOr3DCoordsBuffer = null;
        FloatBuffer normal3DCoordsBuffer = null;
        FloatBuffer vertex3DCoordsBuffer = null;
        for ( Tuple2<Integer, String> key : allData.keySet() )
        {
            arrayLists = allData.get(key);
            // fail safe check
            if ( ( arrayLists.first == null || arrayLists.first.isEmpty() ) && ( arrayLists.second == null || arrayLists.second.isEmpty() ) && ( arrayLists.third == null || arrayLists.third.isEmpty() ) ) continue;

            texture2DOr3DCoordsBuffer = Buffers.newDirectFloatBuffer( arrayLists.first.size() );
            texture2DOr3DCoordsBuffer.put( ArraysAutoBoxUtils.toPrimitiveListFloat(arrayLists.first) ).rewind();

            normal3DCoordsBuffer = Buffers.newDirectFloatBuffer( arrayLists.second.size() );
            normal3DCoordsBuffer.put( ArraysAutoBoxUtils.toPrimitiveListFloat(arrayLists.second) ).rewind();

            vertex3DCoordsBuffer = Buffers.newDirectFloatBuffer( arrayLists.third.size() );
            vertex3DCoordsBuffer.put( ArraysAutoBoxUtils.toPrimitiveListFloat(arrayLists.third) ).rewind();

            allNonInterleavedDataBuffersMap.put( key, Tuples.tuple(texture2DOr3DCoordsBuffer, normal3DCoordsBuffer, vertex3DCoordsBuffer) );

            arrayLists.first.clear();
            arrayLists.second.clear();
            arrayLists.third.clear();
        }
        allData.clear();
    }

    /**
    *  Initializes the interleaved buffer.
    */
    @Override
    protected void initializeInterleavedBuffer(GL2 gl)
    {
        convertToInterleavedBuffer( initializeDataStructuresForBufferUsage() );
    }

    /**
    *  Initializes the non-interleaved buffers.
    */
    @Override
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

        convertToNonInterleavedBuffers( initializeDataStructuresForBufferUsage() );
    }

    /**
    *  Initializes the VBO buffers.
    */
    @Override
    protected void initializeVBOBuffers(GL2 gl)
    {
        allNonInterleavedDataBuffersSize = allNonInterleavedDataBuffersMap.size();

        if (modelSettings.usingTexCoords)
            VBOTexCoordsID = (IntBuffer)Buffers.newDirectIntBuffer(allNonInterleavedDataBuffersSize).put( new int[] { 0 } ).rewind();
        if (modelSettings.usingNormals)
            VBONormalsID = (IntBuffer)Buffers.newDirectIntBuffer(allNonInterleavedDataBuffersSize).put( new int[] { 0 } ).rewind();
        VBOVerticesID = (IntBuffer)Buffers.newDirectIntBuffer(allNonInterleavedDataBuffersSize).put( new int[] { 0 } ).rewind();

        if (modelSettings.usingTexCoords)
            gl.glGenBuffers(allNonInterleavedDataBuffersSize, VBOTexCoordsID);
        if (modelSettings.usingNormals)
            gl.glGenBuffers(allNonInterleavedDataBuffersSize, VBONormalsID);
        gl.glGenBuffers(allNonInterleavedDataBuffersSize, VBOVerticesID);

        int index = 0;
        Tuple3<FloatBuffer, FloatBuffer, FloatBuffer> nonInterleavedDataBuffers = null;
        for ( Tuple2<Integer, String> key : allNonInterleavedDataBuffersMap.keySet() )
        {
            nonInterleavedDataBuffers = allNonInterleavedDataBuffersMap.get(key);

            if (modelSettings.usingTexCoords)
            {
                gl.glBindBuffer( GL_ARRAY_BUFFER, VBOTexCoordsID.get(index) );
                gl.glBufferData(GL_ARRAY_BUFFER, nonInterleavedDataBuffers.first.capacity() * Buffers.SIZEOF_FLOAT, nonInterleavedDataBuffers.first, GL_STATIC_DRAW);
            }
            if (modelSettings.usingNormals)
            {
                gl.glBindBuffer( GL_ARRAY_BUFFER, VBONormalsID.get(index) );
                gl.glBufferData(GL_ARRAY_BUFFER, nonInterleavedDataBuffers.second.capacity() * Buffers.SIZEOF_FLOAT, nonInterleavedDataBuffers.second, GL_STATIC_DRAW);
            }
            gl.glBindBuffer( GL_ARRAY_BUFFER, VBOVerticesID.get(index) );
            gl.glBufferData(GL_ARRAY_BUFFER, nonInterleavedDataBuffers.third.capacity() * Buffers.SIZEOF_FLOAT, nonInterleavedDataBuffers.third, GL_STATIC_DRAW);

            index++;
        }
    }

    /**
    *  Draws the model in Immediate Mode.
    */
    @Override
    protected void drawModelShapeInImmediateMode(GL2 gl)
    {
        if (modelSettings.hasTexture) gl.glEnable(GL_TEXTURE_2D);

        // render the model face-by-face
        int previousPolygonType = -1; // dummy previousPolygonType OpenGL value
        String faceMaterial = "";
        boolean applyNewMaterial = false;
        Texture texture = null;
        if ( (materials != null) && useMaterialColors ) materials.resetDrawnMaterialName(true);
        for (int index = 0; index < faces.getNumberOfFaces(); index++)
        {
            faceMaterial = faceMaterials.findMaterial(index); // get material used by face i
            applyNewMaterial = !faceMaterial.isEmpty() && (materials != null);
            if (applyNewMaterial)
            {
                gl.glEnd(); // for the faces, to enforce proper glBegin() & glEnd() grouping before the glBindTexture() in drawWithMaterial()
                if (materials != null)  // draw using this material
                {
                    texture = materials.drawWithMaterial(gl, faceMaterial, useMaterialColors);
                    if (texture != null)
                        texture.bind(gl);
                }
            }
            previousPolygonType = faces.drawFaceInImmediateMode(gl, index, (texture != null) ? texture.getMustFlipVertically() : false, previousPolygonType, applyNewMaterial); // draw index face
        }
        if ( (materials != null) && useMaterialColors ) materials.resetDrawnMaterialName(true);

        if (modelSettings.hasTexture) // implies materials != null
        {
            materials.switchOffTexture();
            gl.glDisable(GL_TEXTURE_2D);
        }
    }

    /**
    *  Draws the model shape with the interleaved vertex array.
    */
    @Override
    protected void drawModelShapeWithInterleavedVertexArray(GL2 gl)
    {
        int mode = GL_V3F;
        if      ( modelSettings.usingNormals && !modelSettings.usingTexCoords)
            mode = GL_N3F_V3F;
        else if (!modelSettings.usingNormals &&  modelSettings.usingTexCoords)
            mode = (has3DTexCoords) ? GL_T2F_V3F : GL_T2F_V3F; // GL_T3F_V3F not available in OpenGL specs?
        else if ( modelSettings.usingNormals &&  modelSettings.usingTexCoords)
            mode = (has3DTexCoords) ? GL_T2F_N3F_V3F : GL_T2F_N3F_V3F; // GL_T3F_N3F_V3F not available in OpenGL specs?

        if (modelSettings.hasTexture) gl.glEnable(GL_TEXTURE_2D);

        // render the model face-by-face
        Tuple2<Integer, FloatBuffer> interleavedDataBuffer = null;
        Texture texture = null;
        if ( (materials != null) && useMaterialColors ) materials.resetDrawnMaterialName(true);
        if (allInterleavedDataBuffersMap != null)
        {
            for ( Tuple2<Integer, String> key : allInterleavedDataBuffersMap.keySet() )
            {
                interleavedDataBuffer = allInterleavedDataBuffersMap.get(key);
                if ( !key.second.isEmpty() && (materials != null) )
                {
                    texture = materials.drawWithMaterial(gl, key.second, useMaterialColors);
                    if (texture != null)
                        texture.bind(gl);
                }
                gl.glInterleavedArrays(mode, 0, interleavedDataBuffer.second);
                gl.glDrawArrays(key.first, 0, interleavedDataBuffer.first / 3);
            }
        }
        if ( (materials != null) && useMaterialColors ) materials.resetDrawnMaterialName(true);

        if (modelSettings.hasTexture) // implies materials != null
        {
            materials.switchOffTexture();
            gl.glDisable(GL_TEXTURE_2D);
        }
    }

    /**
    *  Draws the model shape with non-interleaved vertex arrays.
    */
    @Override
    protected void drawModelShapeWithNonInterleavedVertexArrays(GL2 gl)
    {
        if (modelSettings.hasTexture) gl.glEnable(GL_TEXTURE_2D);

        // render the model face-by-face
        Tuple3<FloatBuffer, FloatBuffer, FloatBuffer> nonInterleavedDataBuffers = null;
        Texture texture = null;
        if ( (materials != null) && useMaterialColors ) materials.resetDrawnMaterialName(true);
        if (allNonInterleavedDataBuffersMap != null)
        {
            for ( Tuple2<Integer, String> key : allNonInterleavedDataBuffersMap.keySet() )
            {
                nonInterleavedDataBuffers = allNonInterleavedDataBuffersMap.get(key);
                if ( !key.second.isEmpty() && (materials != null) )
                {
                    texture = materials.drawWithMaterial(gl, key.second, useMaterialColors);
                    if (texture != null)
                        texture.bind(gl);
                }

                if (modelSettings.usingTexCoords)
                    gl.glTexCoordPointer(has3DTexCoords ? 3 : 2, GL_FLOAT, 0, nonInterleavedDataBuffers.first);
                if (modelSettings.usingNormals)
                    gl.glNormalPointer(GL_FLOAT, 0, nonInterleavedDataBuffers.second);
                gl.glVertexPointer(3, GL_FLOAT, 0, nonInterleavedDataBuffers.third);
                gl.glDrawArrays(key.first, 0, nonInterleavedDataBuffers.third.capacity() / 3);
            }
        }
        if ( (materials != null) && useMaterialColors ) materials.resetDrawnMaterialName(true);

        if (modelSettings.hasTexture) // implies materials != null
        {
            materials.switchOffTexture();
            gl.glDisable(GL_TEXTURE_2D);
        }
    }

    /**
    *  Draws the model shape with VBOs.
    */
    @Override
    protected void drawModelShapeWithVBOs(GL2 gl)
    {
        if (modelSettings.hasTexture) gl.glEnable(GL_TEXTURE_2D);

        // render the model face-by-face
        int index = 0;
        Tuple3<FloatBuffer, FloatBuffer, FloatBuffer> nonInterleavedDataBuffers = null;
        Texture texture = null;
        if ( (materials != null) && useMaterialColors ) materials.resetDrawnMaterialName(true);
        if (allNonInterleavedDataBuffersMap != null)
        {
            for ( Tuple2<Integer, String> key : allNonInterleavedDataBuffersMap.keySet() )
            {
                nonInterleavedDataBuffers = allNonInterleavedDataBuffersMap.get(key);
                if ( !key.second.isEmpty() && (materials != null) )
                {
                    texture = materials.drawWithMaterial(gl, key.second, useMaterialColors);
                    if (texture != null)
                        texture.bind(gl);
                }

                if (modelSettings.usingTexCoords)
                {
                    gl.glBindBuffer( GL_ARRAY_BUFFER, VBOTexCoordsID.get(index) );
                    gl.glTexCoordPointer(has3DTexCoords ? 3 : 2, GL_FLOAT, 0, 0);
                }
                if (modelSettings.usingNormals)
                {
                    gl.glBindBuffer( GL_ARRAY_BUFFER, VBONormalsID.get(index) );
                    gl.glNormalPointer(GL_FLOAT, 0, 0);
                }
                gl.glBindBuffer( GL_ARRAY_BUFFER, VBOVerticesID.get(index) );
                gl.glVertexPointer(3, GL_FLOAT, 0, 0);
                gl.glDrawArrays(key.first, 0, nonInterleavedDataBuffers.third.capacity() / 3);

                index++;
            }
        }
        if ( (materials != null) && useMaterialColors ) materials.resetDrawnMaterialName(true);

        if (modelSettings.hasTexture) // implies materials != null
        {
            materials.switchOffTexture();
            gl.glDisable(GL_TEXTURE_2D);
        }


        // unbind VBO to let other Vertex Arrays work ok throughout BL
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    /**
    *  Disposes the interleaved vertex array buffer.
    */
    @Override
    protected void disposeInterleavedBuffer(GL2 gl)
    {
        if (allInterleavedDataBuffersMap != null)
        {
            Tuple2<Integer, FloatBuffer> interleavedDataBuffer = null;
            for ( Tuple2<Integer, String> key : allInterleavedDataBuffersMap.keySet() )
            {
                interleavedDataBuffer = allInterleavedDataBuffersMap.get(key);
                interleavedDataBuffer.second.clear();
            }
            allInterleavedDataBuffersMap.clear();
            allInterleavedDataBuffersMap = null;
        }
    }


    /**
    *  Disposes the non-interleaved vertex array buffers.
    */
    @Override
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

        if (allNonInterleavedDataBuffersMap != null)
        {
            Tuple3<FloatBuffer, FloatBuffer, FloatBuffer> nonInterleavedDataBuffers = null;
            for ( Tuple2<Integer, String> key : allNonInterleavedDataBuffersMap.keySet() )
            {
                nonInterleavedDataBuffers = allNonInterleavedDataBuffersMap.get(key);
                nonInterleavedDataBuffers.first.clear();
                nonInterleavedDataBuffers.second.clear();
                nonInterleavedDataBuffers.third.clear();
            }
            allNonInterleavedDataBuffersMap.clear();
            allNonInterleavedDataBuffersMap = null;
        }
    }

    /**
    *  Disposes the VBOs.
    */
    @Override
    protected void disposeVBOs(GL2 gl)
    {
        if (modelSettings.usingTexCoords)
            gl.glDeleteBuffers(allNonInterleavedDataBuffersSize, VBOTexCoordsID);
        if (modelSettings.usingNormals)
            gl.glDeleteBuffers(allNonInterleavedDataBuffersSize, VBONormalsID);
        gl.glDeleteBuffers(allNonInterleavedDataBuffersSize, VBOVerticesID);

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
    *  Releases additional resources.
    */
    @Override
    protected void releaseAdditionalResources(GL2 gl)
    {
        faceMaterials.clearAllFaceMaterials();
        faceMaterials = null;

        faces.clearAllFaces();
        faces = null;

        if (materials != null)
        {
            materials.clearAllMaterials(gl);
            materials = null;
        }
    }

    /**
    *  Reports the model shape settings.
    */
    @Override
    protected final void reportModelShapeSettings()
    {
        if (DEBUG_BUILD)
        {
            println("Loaded the OBJ/MTL geometry for " + this.toString() + " with shape settings:");
            println("UsingNormals: " + modelSettings.usingNormals);
            println("UsingTexCoords: " + modelSettings.usingTexCoords);
            println("ModelRenderingState: " + modelSettings.modelRenderingState);
            println("CenterModel: " + modelSettings.centerModel);
            println("HasTexture: " + modelSettings.hasTexture);
            println("Number of vertices: " + point3DVertices.size());
            println("Number of normal coords: " + point3DNormals.size());
            println("Number of texture coords: " + point3DTexCoords.size());
            println("Number of faces: " + faces.getNumberOfFaces());

            modelDimensions.reportDimensions();

            if (materials != null)
                materials.showMaterials(); // list defined materials
            faceMaterials.showUsedMaterials(); // show what materials have been used by faces
        }
    }


}
