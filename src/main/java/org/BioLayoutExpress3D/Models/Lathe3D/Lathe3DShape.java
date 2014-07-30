package org.BioLayoutExpress3D.Models.Lathe3D;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import javax.media.opengl.*;
import static java.lang.Math.*;
import org.BioLayoutExpress3D.CPUParallelism.Executors.*;
import org.BioLayoutExpress3D.Math3DTransformations.*;
import org.BioLayoutExpress3D.Models.*;
import static org.BioLayoutExpress3D.StaticLibraries.EnumUtils.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* A LatheCurve is rotated around the y-axis to make a shape.
* The texture is wrapped around the shape and stretched to its max height.
*
* The rotation of the curve to make the shape uses code derived
* from the SurfaceOfRevolution class by Chris Buckalew.
*
* @see org.BioLayoutExpress3D.Models.ModelShape
* @see org.BioLayoutExpress3D.Models.Lathe3D.LatheCurve
* @author Andrew Davison, 2005, rewrite for BioLayout Express3D & JOGL with N-CP, custom normals generator, Display Lists, Vertex Arrays & VBOs support by Thanos Theo & Michael Kargas, 2011
* @version 3.0.0.0
*
*/

public class Lathe3DShape extends ModelShape
{
    private static final float RADIANS_DEGREE = (float)(PI / 180.0f);

    /**
    *  The angle turned through to create a face of the lathe3D solid.
    */
    private float angleIncrement = 15.0f;
    private int numberOfSlices = (int)(360.0f / angleIncrement);

    // variables needed for the Lathe3DShape
    private float[] xsOut = null;
    private float[] ysOut = null;
    private float height = 0.0f; // height of the shape

    /**
    *  The Lathe3DSettings reference stores all Lathe3DSettings related variables.
    */
    protected Lathe3DSettings lathe3DSettings = null;

    /**
    *  The Lathe3DShape class constructor.
    */
    public Lathe3DShape(GL2 gl, Lathe3DShapeAngleIncrements lathe3DShapeAngleIncrement, Lathe3DSettings lathe3DSettings, ModelSettings modelSettings)
    {
        super(modelSettings);

        angleIncrement = extractFloat(lathe3DShapeAngleIncrement);
        numberOfSlices = (int)(360.0f / angleIncrement);
        this.lathe3DSettings = lathe3DSettings;

        if (DEBUG_BUILD)reportModelShapeSettings();
        createLatheCurve();
        performCreateGeometry(gl);
        if (modelSettings.centerModel) checkVerticesWithModelDimensionsAndCenterModel();
        createGeometryStorage(gl);
        if (DEBUG_BUILD) reportOnModel();
    }

    /**
    *  Reports the model shape settings.
    */
    @Override
    protected final void reportModelShapeSettings()
    {
        println("\nNow creating the geometry for " + this.toString() + " with shape settings:");
        println("UsingNormals: " + modelSettings.usingNormals);
        println("UsingTexCoords: " + modelSettings.usingTexCoords);
        println("ModelRenderingState: " + modelSettings.modelRenderingState);
        println("CenterModel: " + modelSettings.centerModel);
        println("SplineStep: " + lathe3DSettings.splineStep);
        println("AngleIncrement: " + angleIncrement);
        println("NumberOfSlices: " + numberOfSlices + "\n");
    }

    /**
    *  Creates the Lathe Curve.
    */
    private void createLatheCurve()
    {
        LatheCurve latheCurve = new LatheCurve(lathe3DSettings.xsIn, lathe3DSettings.ysIn, lathe3DSettings.splineStep);
        this.height = latheCurve.getHeight();
        this.xsOut = latheCurve.getXs();
        this.ysOut = latheCurve.getYs();
    }

    /**
    *  Creates the surface geometry, using the curve defined by the (x,y) coords in xsOut[] and ysOut[].
    *
    *  The surface is a QuadArray, which is given normals so it will reflect light.
    *
    *  Texture coordinates may be defined to wrap the image around
    *  the outside of the shape, starting from the back, wrapping
    *  counter-clockwise (left to right) around the front, and back to the back.
    *
    *  Turned to 'final' to avoid problems with sub-classes, as it being called in the Lathe3DShape constructor.
    */
    @Override
    protected final void performCreateGeometry(GL2 gl)
    {
        vertices = new float[4 * 3 * numberOfSlices * (xsOut.length - 1)];
        if (modelSettings.usingNormals)
            normals = new float[vertices.length];
        if (modelSettings.usingTexCoords)
            texCoords = new float[2 * (vertices.length / 3)];

        float[] verticesTriangles = null;
        float[] normalsTriangles = null;
        float[] texCoordsTriangles = null;
        verticesTriangles = new float[6 * 3 * numberOfSlices * (xsOut.length - 1)];
        if (modelSettings.usingNormals)
            normalsTriangles = new float[verticesTriangles.length];
        if (modelSettings.usingTexCoords)
            texCoordsTriangles = new float[2 * (verticesTriangles.length / 3)];

        createGeometry(verticesTriangles, normalsTriangles, texCoordsTriangles);

        vertices = verticesTriangles;
        verticesTriangles = null;
        normals = normalsTriangles;
        normalsTriangles = null;
        texCoords = texCoordsTriangles;
        texCoordsTriangles = null;
    }

    private void createGeometry(float[] verticesTriangles, float[] normalsTriangles, float[] texCoordsTriangles)
    {
        if (DEBUG_BUILD) println("\nNow creating the geometry for shape: " + this.toString() + "\n");

        checkCoords(0, xsOut.length);
        surfaceRevolve(0, 0, xsOut.length - 1);

        if (modelSettings.usingNormals)
        {
            calculateSurfaceNormals(0, vertices.length / 12); // number of surfaces defined from each Quad, ie 4 vertices, 4 * 3 elements
            calculateVertexNormals();
            normalizeVertexNormals(0, vertices.length / 3);
        }

        if (modelSettings.usingTexCoords)
        {
            initTexCoords(0, 0, texCoords.length);
            correctTexCoords(0, texCoords.length - 4);
        }

        convertGeometryFromQuadsToTriangles(0, vertices.length / 12, verticesTriangles, normalsTriangles, texCoordsTriangles);
    }

    /**
    *  Checks all the input x coords: all x points should be >= 0, since we are revolving around the y-axis.
    */
    private void checkCoords(int startPosition, int endPosition)
    {
        // all x points should be >= 0.0f, since we are revolving around the y-axis
        for (int i = startPosition; i < endPosition; i++)
        {
            if (xsOut[i] < 0.0f)
            {
                if (DEBUG_BUILD) println("Warning: setting xsOut[" + i + "]:" + xsOut[i] + " from -ve to 0.");
                xsOut[i] = 0.0f;
            }
        }
    }

    /* --------------- surface revolution methods -----------------
       Derived from the SurfaceOfRevolution class by
       Chris Buckalew, (c) 2000-2002.
       Part of his FreeFormDef.java example.
       http://www.csc.calpoly.edu/~buckalew/474Lab6-W03.html
    */

    /**
    *  Each adjacent pairs of coords in the curve are made into one face of the surface.
    *
    *  A face is constructed in counter-clockwise order, so that its normal will face outwards.
    *
    *  The coords in the xsOut[] and ysOut[] arrays are assumed to be in increasing order.
    */
    private void surfaceRevolve(int vertexIndex, int startPosition, int endPosition)
    {
        for (int i = startPosition; i < endPosition; i++)
        {
            for (int slice = 0; slice < numberOfSlices; slice++)
            {
                addCorner(xsOut[i],     ysOut[i],     slice,     vertexIndex); // bottom right
                vertexIndex += 3;

                addCorner(xsOut[i + 1], ysOut[i + 1], slice,     vertexIndex); // top right
                vertexIndex += 3;

                addCorner(xsOut[i + 1], ysOut[i + 1], slice + 1, vertexIndex); // top left
                vertexIndex += 3;

                addCorner(xsOut[i],     ysOut[i],     slice + 1, vertexIndex); // bottom left
                vertexIndex += 3;
            }
        }
    }

    /**
    *  Create a new (x,y,z) coordinate, except when the rotation
    *  has come back to the start. Then use the original coords.
    */
    private void addCorner(float xOriginal, float yOriginal, int slice, int vertexIndex)
    {
        float angle = RADIANS_DEGREE * (slice * angleIncrement);
        // back at start with (slice == numberOfSlices)
        vertices[vertexIndex    ] = (slice == numberOfSlices) ? xOriginal : xCoord(xOriginal, angle); // x
        vertices[vertexIndex + 1] = yOriginal;                                                        // y
        vertices[vertexIndex + 2] = (slice == numberOfSlices) ? 0.0f  : zCoord(xOriginal, angle);     // z
    }

    /**
    *  The following methods rotate the radius unchanged,
    *  creating a circle of points.
    *  These methods can be overridden to vary the radii of the
    *  points, e.g. to make an ellipse.
    */
    protected float xCoord(float radius, float angle)
    {
        return (float)( radius * cos(angle) );
    }

    /**
    *  The following methods rotate the radius unchanged,
    *  creating a circle of points.
    *  These methods can be overridden to vary the radii of the
    *  points, e.g. to make an ellipse.
    */
    protected float zCoord(float radius, float angle)
    {
        return (float)( radius * sin(angle) );
    }

    /**
    *  Calculates the surface normals of all vertex quads.
    *  Used for flat shading, normal-per-quad calculated.
    */
    private void calculateSurfaceNormals(int startPosition, int endPosition)
    {
        Vector3D v1 = new Vector3D();
        Vector3D v2 = new Vector3D();
        Vector3D v1CrossV2 = null;

        int quadIndex = 0;
        int point1Index = 3 * 0;
        int point2Index = 3 * 1;
        int point3Index = 3 * 2;
        int point4Index = 3 * 3;
        for (int i = startPosition; i < endPosition; i++)
        {
            quadIndex = 12 * i;
            v1.x = vertices[quadIndex + point3Index    ] - vertices[quadIndex + point2Index    ];
            v1.y = vertices[quadIndex + point3Index + 1] - vertices[quadIndex + point2Index + 1];
            v1.z = vertices[quadIndex + point3Index + 2] - vertices[quadIndex + point2Index + 2];
            // to cover upper pole 'hat' when quads become triangles, ie point2 coincides with point3, thus changing it to point3 with point4
            if (v1.x == 0.0f && v1.y == 0.0f && v1.z == 0.0f)
            {
                v1.x = vertices[quadIndex + point4Index    ] - vertices[quadIndex + point3Index    ];
                v1.y = vertices[quadIndex + point4Index + 1] - vertices[quadIndex + point3Index + 1];
                v1.z = vertices[quadIndex + point4Index + 2] - vertices[quadIndex + point3Index + 2];
            }

            v2.x = vertices[quadIndex + point2Index    ] - vertices[quadIndex + point1Index    ];
            v2.y = vertices[quadIndex + point2Index + 1] - vertices[quadIndex + point1Index + 1];
            v2.z = vertices[quadIndex + point2Index + 2] - vertices[quadIndex + point1Index + 2];
            // to cover lower pole 'hat' when quads become triangles, ie point1 coincides with point2, thus changing it to point3 with point4
            if (v2.x == 0.0f && v2.y == 0.0f && v2.z == 0.0f)
            {
                v2.x = vertices[quadIndex + point4Index    ] - vertices[quadIndex + point3Index    ];
                v2.y = vertices[quadIndex + point4Index + 1] - vertices[quadIndex + point3Index + 1];
                v2.z = vertices[quadIndex + point4Index + 2] - vertices[quadIndex + point3Index + 2];
            }

            v1CrossV2 = v2.crossProductWithVector3D(v1).normalized();

            normals[quadIndex + point4Index    ] = normals[quadIndex + point3Index    ] = normals[quadIndex + point2Index    ] = normals[quadIndex + point1Index    ] = v1CrossV2.x;
            normals[quadIndex + point4Index + 1] = normals[quadIndex + point3Index + 1] = normals[quadIndex + point2Index + 1] = normals[quadIndex + point1Index + 1] = v1CrossV2.y;
            normals[quadIndex + point4Index + 2] = normals[quadIndex + point3Index + 2] = normals[quadIndex + point2Index + 2] = normals[quadIndex + point1Index + 2] = v1CrossV2.z;
        }
    }

    /**
    *  Calculates the vertex normals (Single Core method).
    *  Used for Gouraud & Phong shading, normal-per-vertex as a mean of all other neighbouring surfaces' (quads') normals.
    */
    private void calculateVertexNormals()
    {
        int numberOfVertices = vertices.length / 3;
        boolean[] passedNormals = new boolean[numberOfVertices];
        int[] tempArrayIndices = new int[numberOfVertices];
        int tempArrayIndicesSize = 0;
        int tempArrayIndex = 0;
        int normalIndex1 = 0;
        int normalIndex2 = 0;
        // search for every normal all its similar valued normals (a (N * N - N) / 2 algorithm) and store their normal sum in all their similar valued normals
        for (int i = 0; i < numberOfVertices; i++)
        {
            if (passedNormals[i])
                continue;

            normalIndex1 = 3 * i;

            tempArrayIndicesSize = 0;
            for (int j = i + 1; j < numberOfVertices; j++)
            {
                normalIndex2 = 3 * j;

                if (vertices[normalIndex1    ] == vertices[normalIndex2    ] &&
                    vertices[normalIndex1 + 1] == vertices[normalIndex2 + 1] &&
                    vertices[normalIndex1 + 2] == vertices[normalIndex2 + 2])
                {
                    normals[normalIndex1    ] += normals[normalIndex2    ];
                    normals[normalIndex1 + 1] += normals[normalIndex2 + 1];
                    normals[normalIndex1 + 2] += normals[normalIndex2 + 2];

                    tempArrayIndices[tempArrayIndicesSize++] = j;
                }
            }

            for (int indicesFound = 0; indicesFound < tempArrayIndicesSize; indicesFound++)
            {
                tempArrayIndex = tempArrayIndices[indicesFound];
                passedNormals[tempArrayIndex] = true;

                normals[3 * tempArrayIndex    ] = normals[normalIndex1    ];
                normals[3 * tempArrayIndex + 1] = normals[normalIndex1 + 1];
                normals[3 * tempArrayIndex + 2] = normals[normalIndex1 + 2];
            }
        }
    }

    private void accumulateNormalsArrayToAtomicNormals(int startPosition, int endPosition, AtomicIntegerArray atomicNormals)
    {
        int normalIndex = 0;
        for (int i = startPosition; i < endPosition; i++)
        {
            normalIndex = 3 * i;

            atomicNormals.set(normalIndex    , (int)(1000.0f * normals[normalIndex    ]));
            atomicNormals.set(normalIndex + 1, (int)(1000.0f * normals[normalIndex + 1]));
            atomicNormals.set(normalIndex + 2, (int)(1000.0f * normals[normalIndex + 2]));
        }
    }

    private void accumulateAtomicNormalsToNormalsArray(int startPosition, int endPosition, AtomicIntegerArray atomicNormals)
    {
        int normalIndex = 0;
        for (int i = startPosition; i < endPosition; i++)
        {
            normalIndex = 3 * i;

            normals[normalIndex    ] = atomicNormals.get(normalIndex    ) / 1000.0f;
            normals[normalIndex + 1] = atomicNormals.get(normalIndex + 1) / 1000.0f;
            normals[normalIndex + 2] = atomicNormals.get(normalIndex + 2) / 1000.0f;
        }
    }

    /**
    *  Normalizes the vertex normals.
    */
    private void normalizeVertexNormals(int startPosition, int endPosition)
    {
        int normalIndex = 0;
        Vector3D tempVector = new Vector3D();
        for (int i = startPosition; i < endPosition; i++)
        {
            normalIndex = 3 * i;

            tempVector.x = normals[normalIndex    ];
            tempVector.y = normals[normalIndex + 1];
            tempVector.z = normals[normalIndex + 2];
            tempVector.normalize();

            normals[normalIndex    ] = tempVector.x;
            normals[normalIndex + 1] = tempVector.y;
            normals[normalIndex + 2] = tempVector.z;
        }
    }

    /**
    *  Wrap the texture around the shape, the left edge starting at
    *  the back, going counter-clockwise round the front.
    *  The texture is stretched along the y-axis so a t value of 1
    *  equals the max height of the shape.
    *
    *  s is obtained from the angle made by the (x,z) coordinate;
    *  t is the scaled height of a coordinate.
    */
    private void initTexCoords(int texCoordIndex, int startPosition, int endPosition)
    {
        float x = 0.0f, y = 0.0f, z = 0.0f;
        float sValue = 0.0f, tValue = 0.0f;
        float angle = 0.0f, frac = 0.0f;
        for (int i = startPosition; i < endPosition; i += 2)
        {
            x = vertices[texCoordIndex    ];
            y = vertices[texCoordIndex + 1];
            z = vertices[texCoordIndex + 2];

            angle = (float)atan2(x, z);  // -PI to PI
            frac = (float)(angle / PI);  // -1.0f to 1.0f
            sValue = 0.5f + frac / 2.0f; // 0.0f to 1.0f
            tValue = y / height;         // 0.0f to 1.0f; uses global height value

            texCoords[i    ] = sValue;
            texCoords[i + 1] = tValue;
            texCoordIndex += 3;
        }
    }

    /**
    *  Find texture squares where the texture coords are reversed, and un-reverse them.
    *
    *  A reversal occurs at the junction between -PI and PI of tan(x/z) (at the back of the shape).
    *
    *  The s coords on the -PI side will be near 0, the ones on the PI side
    *  will be near 1, which will make the square show a reversed texture.
    *
    *  The correction is to change the 0 values to 1's, which will make
    *  the square show the texture near the s value of 1.
    */
    private void correctTexCoords(int startPosition, int endPosition)
    {
        for (int i = startPosition; i < endPosition; i += 4)
        {
            if ( (texCoords[i] < texCoords[i + 6]) && (texCoords[i + 2] < texCoords[i + 4]) ) // should not increase
            {
                // texCoords[i] = 1.0f;
                texCoords[i    ] = (1.0f + texCoords[i + 6]) / 2.0f; // between x and 1.0f
                // texCoords[i + 2] = 1.0f;
                texCoords[i + 2] = (1.0f + texCoords[i + 4]) / 2.0f; // between x and 1.0f
            }
        }
    }

    /**
    *  Converts the geometry from the original Quads representation to Triangles.
    */
    private void convertGeometryFromQuadsToTriangles(int startPosition, int endPosition, float[] verticesTriangles, float[] normalsTriangles, float[] texCoordsTriangles)
    {
        int quadIndex = 0;
        int quadTexCoordIndex = 0;
        int triangle1Index = 0;
        int triangle2Index = 0;
        int triangleOffset = 3 * 3;
        int triangle1NormalIndex = 0;
        int triangle2NormalIndex = 0;
        int triangle1TexCoordsIndex = 0;
        int triangle2TexCoordsIndex = 0;
        int texCoordOffset = 2 * 3;
        int point1Index = 3 * 0;
        int point2Index = 3 * 1;
        int point3Index = 3 * 2;
        int point4Index = 3 * 3;
        int point1TexCoordIndex = 2 * 0;
        int point2TexCoordIndex = 2 * 1;
        int point3TexCoordIndex = 2 * 2;
        int point4TexCoordIndex = 2 * 3;
        for (int i = startPosition; i < endPosition; i++)
        {
            quadIndex = 12 * i;
            quadTexCoordIndex = 8 * i;
            triangle1Index = 18 * i;
            triangle2Index = triangle1Index + triangleOffset;

            // VERTEX #1 for triangle 1
            verticesTriangles[triangle1Index    ] = vertices[quadIndex + point1Index    ];
            verticesTriangles[triangle1Index + 1] = vertices[quadIndex + point1Index + 1];
            verticesTriangles[triangle1Index + 2] = vertices[quadIndex + point1Index + 2];
            triangle1Index += 3;
            // VERTEX #2 for triangle 1
            verticesTriangles[triangle1Index    ] = vertices[quadIndex + point2Index    ];
            verticesTriangles[triangle1Index + 1] = vertices[quadIndex + point2Index + 1];
            verticesTriangles[triangle1Index + 2] = vertices[quadIndex + point2Index + 2];
            triangle1Index += 3;
            // VERTEX #3 for triangle 1
            verticesTriangles[triangle1Index    ] = vertices[quadIndex + point3Index    ];
            verticesTriangles[triangle1Index + 1] = vertices[quadIndex + point3Index + 1];
            verticesTriangles[triangle1Index + 2] = vertices[quadIndex + point3Index + 2];

            // VERTEX #1 for triangle 2
            verticesTriangles[triangle2Index    ] = vertices[quadIndex + point1Index    ];
            verticesTriangles[triangle2Index + 1] = vertices[quadIndex + point1Index + 1];
            verticesTriangles[triangle2Index + 2] = vertices[quadIndex + point1Index + 2];
            triangle2Index += 3;
            // VERTEX #2 for triangle 2
            verticesTriangles[triangle2Index    ] = vertices[quadIndex + point3Index    ];
            verticesTriangles[triangle2Index + 1] = vertices[quadIndex + point3Index + 1];
            verticesTriangles[triangle2Index + 2] = vertices[quadIndex + point3Index + 2];
            triangle2Index += 3;
            // VERTEX #3 for triangle 2
            verticesTriangles[triangle2Index    ] = vertices[quadIndex + point4Index    ];
            verticesTriangles[triangle2Index + 1] = vertices[quadIndex + point4Index + 1];
            verticesTriangles[triangle2Index + 2] = vertices[quadIndex + point4Index + 2];

            if (modelSettings.usingNormals)
            {
                triangle1NormalIndex = 18 * i;
                triangle2NormalIndex = triangle1NormalIndex + triangleOffset;

                // NORMAL #1 for triangle 1
                normalsTriangles[triangle1NormalIndex    ] = normals[quadIndex + point1Index    ];
                normalsTriangles[triangle1NormalIndex + 1] = normals[quadIndex + point1Index + 1];
                normalsTriangles[triangle1NormalIndex + 2] = normals[quadIndex + point1Index + 2];
                triangle1NormalIndex += 3;
                // NORMAL #2 for triangle 1
                normalsTriangles[triangle1NormalIndex    ] = normals[quadIndex + point2Index    ];
                normalsTriangles[triangle1NormalIndex + 1] = normals[quadIndex + point2Index + 1];
                normalsTriangles[triangle1NormalIndex + 2] = normals[quadIndex + point2Index + 2];
                triangle1NormalIndex += 3;
                // NORMAL #3 for triangle 1
                normalsTriangles[triangle1NormalIndex    ] = normals[quadIndex + point3Index    ];
                normalsTriangles[triangle1NormalIndex + 1] = normals[quadIndex + point3Index + 1];
                normalsTriangles[triangle1NormalIndex + 2] = normals[quadIndex + point3Index + 2];

                // NORMAL #1 for triangle 2
                normalsTriangles[triangle2NormalIndex    ] = normals[quadIndex + point1Index    ];
                normalsTriangles[triangle2NormalIndex + 1] = normals[quadIndex + point1Index + 1];
                normalsTriangles[triangle2NormalIndex + 2] = normals[quadIndex + point1Index + 2];
                triangle2NormalIndex += 3;
                // NORMAL #2 for triangle 2
                normalsTriangles[triangle2NormalIndex    ] = normals[quadIndex + point3Index    ];
                normalsTriangles[triangle2NormalIndex + 1] = normals[quadIndex + point3Index + 1];
                normalsTriangles[triangle2NormalIndex + 2] = normals[quadIndex + point3Index + 2];
                triangle2NormalIndex += 3;
                // NORMAL #3 for triangle 2
                normalsTriangles[triangle2NormalIndex    ] = normals[quadIndex + point4Index    ];
                normalsTriangles[triangle2NormalIndex + 1] = normals[quadIndex + point4Index + 1];
                normalsTriangles[triangle2NormalIndex + 2] = normals[quadIndex + point4Index + 2];
            }

            if (modelSettings.usingTexCoords)
            {
                triangle1TexCoordsIndex = 12 * i;
                triangle2TexCoordsIndex = triangle1TexCoordsIndex + texCoordOffset;

                // TEXCOORD #1 for triangle 1
                texCoordsTriangles[triangle1TexCoordsIndex    ] = texCoords[quadTexCoordIndex + point1TexCoordIndex    ];
                texCoordsTriangles[triangle1TexCoordsIndex + 1] = texCoords[quadTexCoordIndex + point1TexCoordIndex + 1];
                triangle1TexCoordsIndex += 2;
                // TEXCOORD #2 for triangle 1
                texCoordsTriangles[triangle1TexCoordsIndex    ] = texCoords[quadTexCoordIndex + point2TexCoordIndex    ];
                texCoordsTriangles[triangle1TexCoordsIndex + 1] = texCoords[quadTexCoordIndex + point2TexCoordIndex + 1];
                triangle1TexCoordsIndex += 2;
                // TEXCOORD #3 for triangle 1
                texCoordsTriangles[triangle1TexCoordsIndex    ] = texCoords[quadTexCoordIndex + point3TexCoordIndex    ];
                texCoordsTriangles[triangle1TexCoordsIndex + 1] = texCoords[quadTexCoordIndex + point3TexCoordIndex + 1];

                // TEXCOORD #1 for triangle 2
                texCoordsTriangles[triangle2TexCoordsIndex    ] = texCoords[quadTexCoordIndex + point1TexCoordIndex    ];
                texCoordsTriangles[triangle2TexCoordsIndex + 1] = texCoords[quadTexCoordIndex + point1TexCoordIndex + 1];
                triangle2TexCoordsIndex += 2;
                // TEXCOORD #2 for triangle 2
                texCoordsTriangles[triangle2TexCoordsIndex    ] = texCoords[quadTexCoordIndex + point3TexCoordIndex    ];
                texCoordsTriangles[triangle2TexCoordsIndex + 1] = texCoords[quadTexCoordIndex + point3TexCoordIndex + 1];
                triangle2TexCoordsIndex += 2;
                // TEXCOORD #3 for triangle 2
                texCoordsTriangles[triangle2TexCoordsIndex    ] = texCoords[quadTexCoordIndex + point4TexCoordIndex    ];
                texCoordsTriangles[triangle2TexCoordsIndex + 1] = texCoords[quadTexCoordIndex + point4TexCoordIndex + 1];

            }
        }
    }

    /**
    *  Releases additional resources.
    *  No usage here.
    */
    @Override
    protected void releaseAdditionalResources(GL2 gl){}

    /**
    *  The Lathe3D shapeName.
    */
    @Override
    public String toString()
    {
        return "Lathe3DShape" + super.toString();
    }


}
