package org.BioLayoutExpress3D.Models.Loaders.OBJModelLoader;

import java.util.*;
import javax.media.opengl.*;
import static javax.media.opengl.GL2.*;
import static javax.media.opengl.GL3.*;
import org.BioLayoutExpress3D.DataStructures.*;
import org.BioLayoutExpress3D.Utils.*;
import static org.BioLayoutExpress3D.Models.ModelShape.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
* Faces stores the information for each face of a model.
*
*  A face is represented by three arrays of indices for
*  the vertices, normals, and texture coords used in that face.
*
* facesVerticesIdices, facesTexCoordsIdices, and facesNormalsIdices are ArrayLists of
* those arrays; one entry for each face.
*
* drawFace() is supplied with a face index, looks up the
* associated vertices, normals, and texture coords indices arrays,
* and uses those arrays to access the actual vertices, normals,
* and texture coords data for rendering the face.
*
* @author Andrew Davison, 2006, rewrite for BioLayout Express3D by Thanos Theo, 2011
* @version 3.0.0.0
*
*/

public class Faces
{

    /**
    *  References to the model's vertices, normals, and texture coords.
    */
    private ArrayList<Point3D> point3DVertices = null;
    private ArrayList<Point3D> point3DNormals = null;
    private ArrayList<Point3D> point3DTexCoords = null;

    /**
    *  Indices for vertices, texture coords, and normals used by each face.
    */
    private ArrayList<int[]> facesVerticesIdices = null;
    private ArrayList<int[]> facesTexCoordsIdices = null;
    private ArrayList<int[]> facesNormalsIdices = null;

    /**
    *  The Faces class constructor.
    */
    public Faces(ArrayList<Point3D> point3DVertices, ArrayList<Point3D> point3DNormals, ArrayList<Point3D> point3DTexCoords)
    {
        this.point3DVertices = point3DVertices;
        this.point3DNormals = point3DNormals;
        this.point3DTexCoords = point3DTexCoords;

        facesVerticesIdices = new ArrayList<int[]>();
        facesTexCoordsIdices = new ArrayList<int[]>();
        facesNormalsIdices = new ArrayList<int[]>();
    }

    /**
    *  Gets this face's indices from line "f v/vt/vn ..."
    *  with vt or vn index values perhaps being absent.
    */
    public boolean addFace(String line)
    {
        try
        {
            line = line.substring(2); // skip the "f "
            Scanner scanner = new Scanner(line);
            String[] splitFaceToken = null;
            String faceToken = "";
            int numberOfTokens = line.split("\\s+").length; // number of v/vt/vn tokens
            // create arrays to hold the v, vt, vn indices
            int v[] = new int[numberOfTokens];
            int vt[] = new int[numberOfTokens];
            int vn[] = new int[numberOfTokens];
            int numberOfSeparations = 0;

            for (int i = 0; i < numberOfTokens; i++)
            {
                faceToken = addFaceValues( scanner.next() ); // get a v/vt/vn token
                splitFaceToken = faceToken.split("\\/");
                numberOfSeparations = splitFaceToken.length; // how many '/'s are there in the token

                // add 0's if the vt or vn index values are missing;
                // 0 is a good choice since real indices start at 1
                v[i] = Integer.parseInt(splitFaceToken[0]);
                vt[i] = (numberOfSeparations > 1) ? Integer.parseInt(splitFaceToken[1]) : 0;
                vn[i] = (numberOfSeparations > 2) ? Integer.parseInt(splitFaceToken[2]) : 0;
            }

            // store the indices for this face
            facesVerticesIdices.add(v);
            facesTexCoordsIdices.add(vt);
            facesNormalsIdices.add(vn);

            return true;
        }
        catch (NumberFormatException exc)
        {
            if (DEBUG_BUILD) println("Incorrect face index in Faces.addFace(): " + exc.getMessage());

            return false;
        }
    }

    /**
    *  A face token (v/vt/vn) may be missing vt or vn
    *  index values; add 0's in those cases.
    */
    private String addFaceValues(String faceString)
    {
        char[] characters = faceString.toCharArray();
        StringBuilder sb = new StringBuilder();
        char previousCharacter = 'x'; // dummy value

        for (int k = 0; k < characters.length; k++)
        {
            if (characters[k] == '/' && previousCharacter == '/') // if no char between /'s
                sb.append('0');   // add a '0'
            previousCharacter = characters[k];
            sb.append(previousCharacter);
        }

        return sb.toString();
    }

    /**
    *  Draws the ith face by getting the vertex, normal, and texture
    *  coord indices for face i. Use those indices to access the
    *  actual vertex, normal, and texture coord data, and render the face.
    *
    *  Each face uses 3 array of indices; one for the vertex
    *  indices, one for the normal indices, and one for the texture
    *  coord indices.
    *
    *  If the model doesn't use normals or texture coords then the indices
    *  arrays will contain 0's.
    *
    *  If the texture coords need flipping then the t-values are changed.
    */
    public int drawFaceInImmediateMode(GL2 gl, int index, boolean flipTexCoords, int previousPolygonType, boolean applyNewMaterial)
    {
        if ( index >= facesVerticesIdices.size() ) // i out of bounds?
          return -1; // dummy previousPolygonType OpenGL value

        // get the vertex, normal and texture coords indices for face i
        int[] verticesIndices = facesVerticesIdices.get(index);
        int[] normalsIndices = facesNormalsIdices.get(index);
        int[] texCoordIndices = facesTexCoordsIdices.get(index);

        int polygonType = getPolygonType(verticesIndices);
        if ( (previousPolygonType != polygonType) || applyNewMaterial )
        {
            if (index != 0 && !applyNewMaterial) // first index should skip the glEnd() call
                gl.glEnd();

            gl.glBegin(polygonType);
        }

        // render the normals, texture coords, and vertices for face i
        // by accessing them using their indices
        float yTexCoord = 0.0f;
        Point3D vertex = null, normal = null, texCoord = null;
        for (int face = 0; face < verticesIndices.length; face++)
        {
            if (texCoordIndices[face] != 0.0f) // if there are texCoords, render them
            {
                texCoord = point3DTexCoords.get(texCoordIndices[face] - 1);
                yTexCoord = (flipTexCoords) ? 1.0f - texCoord.getY() : texCoord.getY(); // flip the y-value (the texture's t-value)

                if (texCoord.getZ() == DUMMY_Z_TEX_COORD) // using 2D texture coords
                    gl.glTexCoord2f(texCoord.getX(), yTexCoord);
                else // 3D texture coords
                    gl.glTexCoord3f( texCoord.getX(), yTexCoord, texCoord.getZ() );
            }

            if (normalsIndices[face] != 0.0f) // if there are normals, render them
            {
                normal = point3DNormals.get(normalsIndices[face] - 1);
                gl.glNormal3f( normal.getX(), normal.getY(), normal.getZ() );
            }

            vertex = point3DVertices.get(verticesIndices[face] - 1);  // render the vertices
            gl.glVertex3f( vertex.getX(), vertex.getY(), vertex.getZ() );
        }

        if (index == facesVerticesIdices.size() - 1)
            gl.glEnd();

        return polygonType;
    }

    /**
    *  Caches drawing the ith face by getting the vertex, normal, and texture
    *  coord indices for face i. Use those indices to access the
    *  actual vertex, normal, and texture coord data, and render the face.
    *
    *  Each face uses 3 array of indices; one for the vertex
    *  indices, one for the normal indices, and one for the texture
    *  coord indices.
    *
    *  If the model doesn't use normals or texture coords then the indices
    *  arrays will contain 0's.
    *
    *  If the texture coords need flipping then the t-values are changed.
    */
    public Tuple2<Tuple2<Integer, String>, Tuple3<ArrayList<Float>, ArrayList<Float>, ArrayList<Float>>>
           cacheFaceData(int index, boolean flipTexCoords, boolean applyNewMaterial,
                         String faceMaterial, Tuple2<Tuple2<Integer, String>, Tuple3<ArrayList<Float>, ArrayList<Float>, ArrayList<Float>>> keyDataPair,
                         HashMap<Tuple2<Integer, String>, Tuple3<ArrayList<Float>, ArrayList<Float>, ArrayList<Float>>> allData)
    {
        if ( index >= facesVerticesIdices.size() ) // i out of bounds?
          return keyDataPair; // dummy previousPolygonType OpenGL value

        // get the vertex, normal and texture coords indices for face i
        int[] verticesIndices = facesVerticesIdices.get(index);
        int[] normalsIndices = facesNormalsIdices.get(index);
        int[] texCoordIndices = facesTexCoordsIdices.get(index);

        int polygonType = getPolygonType(verticesIndices);
        if ( (keyDataPair.first.first != polygonType) || applyNewMaterial )
        {
            if (index != 0 && !applyNewMaterial) // first index should skip the glEnd() call
                allData.put(keyDataPair.first, keyDataPair.second);

            keyDataPair = Tuples.tuple( Tuples.tuple(polygonType, faceMaterial), Tuples.tuple( new ArrayList<Float>(), new ArrayList<Float>(), new ArrayList<Float>() ) );
        }

        // render the normals, texture coords, and vertices for face i
        // by accessing them using their indices
        float yTexCoord = 0.0f;
        Point3D vertex = null, normal = null, texCoord = null;
        for (int face = 0; face < verticesIndices.length; face++)
        {
            if (texCoordIndices[face] != 0.0f) // if there are texCoords, render them
            {
                texCoord = point3DTexCoords.get(texCoordIndices[face] - 1);
                yTexCoord = (flipTexCoords) ? 1.0f - texCoord.getY() : texCoord.getY(); // flip the y-value (the texture's t-value)

                if (texCoord.getZ() == DUMMY_Z_TEX_COORD) // using 2D texture coords
                {
                    // gl.glTexCoord2f(texCoord.getX(), yTexCoord);
                    keyDataPair.second.first.add( texCoord.getX() );
                    keyDataPair.second.first.add( yTexCoord );
                }
                else // 3D texture coords
                {
                    // gl.glTexCoord3f( texCoord.getX(), yTexCoord, texCoord.getZ() );
                    keyDataPair.second.first.add( texCoord.getX() );
                    keyDataPair.second.first.add( yTexCoord );
                    keyDataPair.second.first.add( texCoord.getZ() );
                }
            }

            if (normalsIndices[face] != 0.0f) // if there are normals, render them
            {
                normal = point3DNormals.get(normalsIndices[face] - 1);
                // gl.glNormal3f( normal.getX(), normal.getY(), normal.getZ() );
                keyDataPair.second.second.add( normal.getX() );
                keyDataPair.second.second.add( normal.getY() );
                keyDataPair.second.second.add( normal.getZ() );
            }

            vertex = point3DVertices.get(verticesIndices[face] - 1);  // render the vertices
            // gl.glVertex3f( vertex.getX(), vertex.getY(), vertex.getZ() );
            keyDataPair.second.third.add( vertex.getX() );
            keyDataPair.second.third.add( vertex.getY() );
            keyDataPair.second.third.add( vertex.getZ() );
        }

        if (index == facesVerticesIdices.size() - 1)
            allData.put(keyDataPair.first, keyDataPair.second);

        // return keyDataPair
        return keyDataPair;
    }

    /**
    *  Gets the polygon type of the given face.
    *
    */
    private int getPolygonType(int[] verticesIndices)
    {
        int polygonType = GL_POLYGON;
        if (verticesIndices.length == 3)
          polygonType = GL_TRIANGLES;
        else if (verticesIndices.length == 4)
          polygonType = GL_QUADS;

        return polygonType;
    }

    /**
    *  Gets the number of available faces.
    */
    public int getNumberOfFaces()
    {
        return facesVerticesIdices.size();
    }

    /**
    *  Clears all faces.
    */
    public void clearAllFaces()
    {
        point3DVertices.clear();
        point3DNormals.clear();
        point3DTexCoords.clear();

        facesVerticesIdices.clear();
        facesTexCoordsIdices.clear();
        facesNormalsIdices.clear();
    }


}

