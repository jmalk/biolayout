package org.BioLayoutExpress3D.Models.SuperQuadric;

import java.util.concurrent.*;
import javax.media.opengl.*;
import static java.lang.Math.*;
import org.BioLayoutExpress3D.CPUParallelism.Executors.*;
import org.BioLayoutExpress3D.Models.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import static org.BioLayoutExpress3D.Models.SuperQuadric.SuperQuadricShapeTypes.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* A SuperQuadricShape is the class that creates SuperQuadric-based shapes using parametric polar coordinate equations.
*
* @see org.BioLayoutExpress3D.Models.ModelShape
* @see org.BioLayoutExpress3D.Models.SuperQuadric.SuperQuadricSettings
* @author Jeff Molofee, 2000, rewrite for BioLayout Express3D & JOGL with N-CP, Display Lists, Vertex Arrays, VBO & OpenGL 4.0 GL_PATCHES support by Thanos Theo, 2011-2012
* @version 3.0.0.0
*
*/

public class SuperQuadricShape extends ModelShape
{
    /**
    *  Variable needed for N-CP.
    */
    private static final int MINIMUM_NUMBER_OF_INPUT_U_SEGMENTS_FOR_PARALLELIZATION = 2 * NUMBER_OF_AVAILABLE_PROCESSORS;

    /**
    *  The SuperQuadric reference stores all SuperQuadric related variables.
    */
    private SuperQuadricSettings superQuadricSettings = null;

    /**
    *  The SuperQuadricShape class constructor.
    */
    public SuperQuadricShape(GL2 gl, SuperQuadricSettings superQuadricSettings, ModelSettings modelSettings)
    {
        super(modelSettings);
        this.superQuadricSettings = superQuadricSettings;

        if (DEBUG_BUILD)reportModelShapeSettings();
        performCreateGeometry(gl);
        // if (modelSettings.centerModel) checkVerticesWithModelDimensionsAndCenterModel(); // SuperQuadrics are alredy pre-centered
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
        println("SuperQuadricShapeType: " + superQuadricSettings.superQuadricShapeType);
        println("Slices: " + superQuadricSettings.uSegments);
        println("Segments: " + superQuadricSettings.vSegments + "\n");
    }

    /**
    *  Turned to 'final' to avoid problems with sub-classes, as it being called in the SuperQuadricShape constructor.
    */
    @Override
    protected final void performCreateGeometry(GL2 gl)
    {
        vertices = new float[6 * 3 * superQuadricSettings.uSegments * superQuadricSettings.vSegments]; // 6 -> 2 * 3 triangles vertices, 3 for 3D vertex points
        if (modelSettings.usingNormals)
            normals = new float[vertices.length];
        if (modelSettings.usingTexCoords)
            texCoords = new float[2 * (vertices.length / 3)];

        // Calculate delta variables
        float dU = (superQuadricSettings.u2 - superQuadricSettings.u1) / superQuadricSettings.uSegments;
        float dV = (superQuadricSettings.v2 - superQuadricSettings.v1) / superQuadricSettings.vSegments;
        float dS = (superQuadricSettings.s2 - superQuadricSettings.s1) / superQuadricSettings.uSegments;
        float dT = (superQuadricSettings.t2 - superQuadricSettings.t1) / superQuadricSettings.vSegments;

        if ( !USE_MULTICORE_PROCESS || (superQuadricSettings.uSegments < MINIMUM_NUMBER_OF_INPUT_U_SEGMENTS_FOR_PARALLELIZATION) )
        {
            createGeometry(dU, dV, dS, dT, 0, superQuadricSettings.uSegments);
        }
        else
        {
            int totalIterationsPerProcess = superQuadricSettings.uSegments / NUMBER_OF_AVAILABLE_PROCESSORS;
            LoggerThreadPoolExecutor executor = new LoggerThreadPoolExecutor(NUMBER_OF_AVAILABLE_PROCESSORS, NUMBER_OF_AVAILABLE_PROCESSORS, 0L, TimeUnit.MILLISECONDS,
                                                                             new LinkedBlockingQueue<Runnable>(NUMBER_OF_AVAILABLE_PROCESSORS),
                                                                             new LoggerThreadFactory("SuperQuadricShape"),
                                                                             new ThreadPoolExecutor.CallerRunsPolicy() );

            cyclicBarrierTimer.clear();
            for (int threadId = 0; threadId < NUMBER_OF_AVAILABLE_PROCESSORS; threadId++)
                executor.execute( createGeometryProcessKernel(dU, dV, dS, dT, threadId, totalIterationsPerProcess) );

            try
            {
                threadBarrier.await(); // wait for all threads to be ready
                threadBarrier.await(); // wait for all threads to finish
                executor.shutdown();
            }
            catch (BrokenBarrierException ex)
            {
                if (DEBUG_BUILD) println("Problem with a broken barrier with the main SuperQuadricShape thread in performCreateGeometry()!:\n" + ex.getMessage());
            }
            catch (InterruptedException ex)
            {
                // restore the interuption status after catching InterruptedException
                Thread.currentThread().interrupt();
                if (DEBUG_BUILD) println("Problem with pausing the main SuperQuadricShape thread in performCreateGeometry()!:\n" + ex.getMessage());
            }

            if (DEBUG_BUILD) println("\nTotal SuperQuadricShape " + this.toString() + " N-CP run time: " + (cyclicBarrierTimer.getTime() / 1e6) + " msecs.\n");
        }
    }

    private Runnable createGeometryProcessKernel(final float dU, final float dV, final float dS, final float dT, final int threadId, final int totalIterationsPerProcess)
    {
        return new Runnable()
        {

            @Override
            public void run()
            {
                try
                {
                    threadBarrier.await();
                    try
                    {
                        int startPosition = threadId * totalIterationsPerProcess;
                        int extraIterations = ( threadId == (NUMBER_OF_AVAILABLE_PROCESSORS - 1) ) ? (superQuadricSettings.vSegments % NUMBER_OF_AVAILABLE_PROCESSORS) : 0;
                        int endPosition = (threadId + 1) * totalIterationsPerProcess + extraIterations;

                        createGeometry(dU, dV, dS, dT, startPosition, endPosition);
                    }
                    finally
                    {
                        threadBarrier.await();
                    }
                }
                catch (BrokenBarrierException ex)
                {
                    if (DEBUG_BUILD) println("Problem with a broken barrier with the N-Core thread with threadId " + threadId + " in createGeometryProcessKernel()!:\n" + ex.getMessage());
                }
                catch (InterruptedException ex)
                {
                    // restore the interuption status after catching InterruptedException
                    Thread.currentThread().interrupt();
                    if (DEBUG_BUILD) println("Problem with pausing the N-Core thread with threadId " + threadId + " in createGeometryProcessKernel()!:\n" + ex.getMessage());
                }
            }


        };
    }

    /*
    *  Generates a solid SuperQuadrics using the parameters from superQuadricSettings and optionally generates normals & texture coordinates.
    */
    private void createGeometry(float dU, float dV, float dS, float dT, int startPosition, int endPosition)
    {
        int verticesIndex  = 18 * startPosition * superQuadricSettings.vSegments;
        int texCoordsIndex = 12 * startPosition * superQuadricSettings.vSegments; // 2 / 3 of all vertices

        // Initialize variables for loop
        float U = superQuadricSettings.u1 + startPosition * dU;
        float V = 0.0f;
        float S = superQuadricSettings.s1 + startPosition * dS;
        float T = 0.0f;
        for (int Y = startPosition; Y < endPosition; Y++)
        {
            // Initialize variables for loop
            V = superQuadricSettings.v1;
            T = superQuadricSettings.t1;
            for (int X = 0; X < superQuadricSettings.vSegments; X++)
            {
                // VERTEX #1 for triangle 1
                createSuperQuadric(U,      V,      verticesIndex, S,      T     , texCoordsIndex);
                verticesIndex += 3;
                texCoordsIndex += 2;

                // VERTEX #2 for triangle 1
                createSuperQuadric(U + dU, V,      verticesIndex, S + dS, T     , texCoordsIndex);
                verticesIndex += 3;
                texCoordsIndex += 2;

                // VERTEX #3 for triangle 1
                createSuperQuadric(U + dU, V + dV, verticesIndex, S + dS, T + dT, texCoordsIndex);
                verticesIndex += 3;
                texCoordsIndex += 2;

                // VERTEX #1 for triangle 2
                createSuperQuadric(U,      V,      verticesIndex, S,      T     , texCoordsIndex);
                verticesIndex += 3;
                texCoordsIndex += 2;

                // VERTEX #2 for triangle 2
                createSuperQuadric(U + dU, V + dV, verticesIndex, S + dS, T + dT, texCoordsIndex);
                verticesIndex += 3;
                texCoordsIndex += 2;

                // VERTEX #3 for triangle 2
                createSuperQuadric(U,      V + dV, verticesIndex, S     , T + dT, texCoordsIndex);
                verticesIndex += 3;
                texCoordsIndex += 2;

                // Update variables for next loop
                V += dV;
                T += dT;
            }
            // Update variables for next loop
            U += dU;
            S += dS;
        }
    }

    /*
    * a1, a2, and a3 are the x, y, and z scaling factors, respecfully.
    * For proper generation of the solid, u should be >= -PI / 2 and <= PI / 2.
    * Similarly, v should be >= -PI and <= PI.
    */
    private void sqEllipsoid(float a1, float a2, float a3, float u, float v, float n, float e, int verticesIndex)
    {
        float value = sqCos(u, n);
        vertices[verticesIndex    ] = a1 * value * sqCos(v, e);
        vertices[verticesIndex + 1] = a2 * value * sqSin(v, e);
        vertices[verticesIndex + 2] = a3 * sqSin(u, n);

        if (modelSettings.usingNormals)
        {
            value = sqCos(u, 2.0f - n);
            normals[verticesIndex    ] = value * sqCos(v, 2.0f - e) / a1;
            normals[verticesIndex + 1] = value * sqSin(v, 2.0f - e) / a2;
            normals[verticesIndex + 2] = sqSin(u, 2.0f - n) / a3;
        }
    }

    /*
    * a1, a2, and a3 are the x, y, and z scaling factors, respecfully.
    * For proper generation of the solid, u should be >= -PI / 2 and <= PI / 2.
    * Similarly, v should be >= -PI and <= PI.
    */
    private void sqHyperboloidOneSheet(float a1, float a2, float a3, float u, float v, float n, float e, int verticesIndex)
    {
        float value = sqCosH(u, n);
        vertices[verticesIndex    ] = a1 * value * sqCos(v, e);
        vertices[verticesIndex + 1] = a2 * value * sqSin(v, e);
        vertices[verticesIndex + 2] = a3 * sqSinH(u, n);

        if (modelSettings.usingNormals)
        {
            value = sqCosH(u, 2.0f - n);
            normals[verticesIndex    ] = value * sqCos(v, 2.0f - e) / a1;
            normals[verticesIndex + 1] = value * sqSin(v, 2.0f - e) / a2;
            normals[verticesIndex + 2] = sqSinH(u, 2.0f - n) / a3;
        }
    }

    /*
    * a1, a2, and a3 are the x, y, and z scaling factors, respecfully.
    * For proper generation of the solid, u should be >= -PI / 2 and <= PI / 2.
    * Similarly, v should be >= -PI / 2 and <= PI / 2.
    */
    private void sqHyperboloidTwoSheets(float a1, float a2, float a3, float u, float v, float n, float e, int verticesIndex)
    {
        float value1 = sqCosH(u, n);
        vertices[verticesIndex    ] = a1 * value1 * sqCosH(v, e);
        vertices[verticesIndex + 1] = a2 * value1 * sqSinH(v, e);
        vertices[verticesIndex + 2] = a3 * sqSinH(u, n);

        if (modelSettings.usingNormals)
        {
            value1 = sqCosH(u, 2.0f - n);
            normals[verticesIndex    ] = value1 * sqCosH(v, 2.0f - e) / a1;
            normals[verticesIndex + 1] = value1 * sqSinH(v, 2.0f - e) / a2;
            normals[verticesIndex + 2] = sqSinH(u, 2.0f - n) / a3;
        }
    }

    /*
    *  a1, a2, and a3 are the x, y, and z scaling factors, respecfully.
    *  For proper generation of the solid, u should be >= -PI and <= PI.
    *  Similarly, v should be >= -PI and <= PI.
    *  Also, alpha should be > 1.
    */
    private void sqToroid(float a1, float a2, float a3, float u, float v, float n, float e, float alpha, int verticesIndex)
    {
        float A1 = 1.0f / (a1 + alpha);
        float A2 = 1.0f / (a2 + alpha);
        float A3 = 1.0f / (a3 + alpha);

        float value = sqCosPlusAlpha(u, n, alpha);
        vertices[verticesIndex    ] = A1 * value * sqCos(v, e);
        vertices[verticesIndex + 1] = A2 * value * sqSin(v, e);
        vertices[verticesIndex + 2] = A3 * sqSin(u, n);

        if (modelSettings.usingNormals)
        {
            value = sqCos(u, 2.0f - n);
            normals[verticesIndex    ] = value * sqCos(v, 2.0f - e) / A1;
            normals[verticesIndex + 1] = value * sqSin(v, 2.0f - e) / A2;
            normals[verticesIndex + 2] = sqSin(u, 2.0f - n) / A3;
        }
    }

    /*
    *  Creates the SuperQuadric shape.
    */
    private void createSuperQuadric(float u, float v, int verticesIndex, float s, float t, int texCoordsIndex)
    {
        if ( superQuadricSettings.superQuadricShapeType.equals(ELLIPSOID) )
            sqEllipsoid(superQuadricSettings.a1, superQuadricSettings.a2, superQuadricSettings.a3, u, v, superQuadricSettings.n, superQuadricSettings.e, verticesIndex);
        else if ( superQuadricSettings.superQuadricShapeType.equals(HYPERBOLOID_ONE_SHEET) )
            sqHyperboloidOneSheet(superQuadricSettings.a1, superQuadricSettings.a2, superQuadricSettings.a3, u, v, superQuadricSettings.n, superQuadricSettings.e, verticesIndex);
        else if ( superQuadricSettings.superQuadricShapeType.equals(HYPERBOLOID_TWO_SHEETS) )
            sqHyperboloidTwoSheets(superQuadricSettings.a1, superQuadricSettings.a2, superQuadricSettings.a3, u, v, superQuadricSettings.n, superQuadricSettings.e, verticesIndex);
        else // if ( superQuadricSettings.superQuadricShapeType.equals(TOROID) )
            sqToroid(superQuadricSettings.a1, superQuadricSettings.a2, superQuadricSettings.a3, u, v, superQuadricSettings.n, superQuadricSettings.e, superQuadricSettings.alpha, verticesIndex);

        if (modelSettings.usingTexCoords)
        {
            texCoords[texCoordsIndex    ] = t; // was originally s, had to reverse s with t
            texCoords[texCoordsIndex + 1] = s; // was originally t, had to reverse t with s
        }
    }

    /*
    *  This function implements the Cos(v,n) utility function:
    *  Cos(v,n) = sign(cos(v)) * |cos(v)|^n
    */
    private float sqCos(float v, float n)
    {
        double cosValue = cos(v);
        return sign(cosValue) * (float)pow(abs(cosValue), n);
    }

    /*
    *  This function implements the CosPlusAlpha(v,n,alpha) utility function:
    *  CosPlusAlpha(v,n,alpha) = alpha + Cos(v,n)
    */
    private float sqCosPlusAlpha(float v, float n, float alpha)
    {
        return alpha + sqCos(v, n);
    }

    /*
    *  This function implements the Sin(v,n) utility function:
    *  Sin(v,n) = sign(sin(v)) * |sin(v)|^n
    */
    private float sqSin(float v, float n)
    {
        double sinValue = sin(v);
        return sign(sinValue) * (float)pow(abs(sinValue), n);
    }

    /*
    *  This function implements the CosH(v,n) (hyperbolic cos) utility function:
    *  CosH(v,n) = sign(CosH(v)) * |CosH(v)|^n
    */
    private float sqCosH(float v, float n)
    {
        double coshValue = cosh(v);
        return sign(coshValue) * (float)pow(abs(coshValue), n);
    }

    /*
    *  This function implements the SinH(v,n) (hyperbolic sin) utility function:
    *  SinH(v,n) = sign(SinH(v)) * |SinH(v)|^n
    */
    private float sqSinH(float v, float n)
    {
        double sinhValue = sinh(v);
        return sign(sinhValue) * (float)pow(abs(sinhValue), n);
    }

    /*
    *  Returns the sign of x.
    */
    private int sign(double x)
    {
        return (x < 0) ? -1 : ( (x > 0) ?  1 : 0 );
    }

    private float sqEllipsoidInsideOut(float x, float y, float z)
    {
        return (float)( pow(pow(x / superQuadricSettings.a1, 2.0 / superQuadricSettings.e) + pow(y / superQuadricSettings.a2, 2.0 / superQuadricSettings.e), superQuadricSettings.e / superQuadricSettings.n) + pow(z / superQuadricSettings.a3, 2.0 / superQuadricSettings.n) );
    }

    private float sqHyperboloidOneSheetInsideOut(float x, float y, float z)
    {
        return (float)( pow(pow(x / superQuadricSettings.a1, 2.0 / superQuadricSettings.e) + pow(y / superQuadricSettings.a2, 2.0 / superQuadricSettings.e), superQuadricSettings.e / superQuadricSettings.n) - pow(z / superQuadricSettings.a3, 2.0 / superQuadricSettings.n) );
    }

    private float sqHyperboloidTwoSheetsInsideOut(float x, float y, float z)
    {
        return (float)( pow(pow(x / superQuadricSettings.a1, 2.0 / superQuadricSettings.e) - pow(y / superQuadricSettings.a2, 2.0 / superQuadricSettings.e), superQuadricSettings.e / superQuadricSettings.n) - pow(z / superQuadricSettings.a3, 2.0 / superQuadricSettings.n) );
    }

    private float sqToroidInsideOut(float x, float y, float z)
    {
        return (float)( pow(pow(pow(x / superQuadricSettings.a1, 2.0 / superQuadricSettings.e) + pow(y / superQuadricSettings.a2, 2.0 / superQuadricSettings.e), superQuadricSettings.e / 2.0) - superQuadricSettings.alpha, 2.0 / superQuadricSettings.n) + pow(z / superQuadricSettings.a3, 2.0 / superQuadricSettings.n) );
    }

    /*
    *  Tests to see if point P<x,y,z> is inside the SuperQuadric superQuadricSettings.
    *  Returns 1 if on the surface, > 1 if outside the surface, or
    *  < 1 if inside the surface
    */
    public float insideOut(float x, float y, float z)
    {
        if ( superQuadricSettings.superQuadricShapeType.equals(ELLIPSOID) )
            return sqEllipsoidInsideOut(x, y, z);
        else if ( superQuadricSettings.superQuadricShapeType.equals(HYPERBOLOID_ONE_SHEET) )
            return sqHyperboloidOneSheetInsideOut(x, y, z);
        else if ( superQuadricSettings.superQuadricShapeType.equals(HYPERBOLOID_TWO_SHEETS) )
            return sqHyperboloidTwoSheetsInsideOut(x, y, z);
        else // if ( superQuadricSettings.superQuadricShapeType.equals(TOROID) )
            return sqToroidInsideOut(x, y, z);
    }

    /**
    *  Releases additional resources.
    *  No usage here.
    */
    @Override
    protected void releaseAdditionalResources(GL2 gl){}

    /**
    *  The SuperQuadric shapeName.
    */
    @Override
    public String toString()
    {
        return "SuperQuadric" + super.toString();
    }


}
