package org.BioLayoutExpress3D.Models.Lathe3D;

import java.awt.geom.*;
import java.util.concurrent.*;
import static java.lang.Math.*;
import org.BioLayoutExpress3D.CPUParallelism.*;
import org.BioLayoutExpress3D.CPUParallelism.Executors.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* A Lathe Curve is a series of (x,y) points representing a
* curve made up of curve segments and straight lines.
*
* The lathe curve starts at y==0, and moves upwards. All the coords
* must be positive, since the intention is to rotate the lathe curve
* around the y-axis to make a shape.
*
* The curve segments in a Lathe curve are interpolated from (x,y) pts
* using Hermite interpolation. This requires tangents to be supplied
* for the points, which are calculated using a variant of the
* Catmull-Rom algorithm for splines.
*
* For a given pair of coordinates (e.g. (x1, y1), (x2, y2)) a curve segment
* is created by adding an additional splineStep points between them.
*
* Thus, the supplied sequence of (x, y) points is extended to make
* a sequence representing the Lathe curve. This extended sequence
* is accessible by calling getXs() and getYs().
*
* A given point (x,y) may start a curve segment or a straight line going
* to the next point. We distinguish by making the x-value negative
* to mean that it should start a straight line. The negative sign
* is removed when the resulting sequence is created.
* This is a bit of a hack, but makes a Lathe curve easy to specify
* without requiring complex data structures for the input sequence.
*
* @see org.BioLayoutExpress3D.Models.Lathe3D.Lathe3DShape
* @author Andrew Davison, 2005, rewrite for BioLayout Express 3D & JOGL with N-CP by Thanos Theo, 2011
* @version 3.0.0.0
*
*/

public class LatheCurve
{

    // variables needed for N-CP
    private static final int MINIMUM_NUMBER_OF_INPUT_VERTICES_FOR_PARALLELIZATION = 2 * NUMBER_OF_AVAILABLE_PROCESSORS;
    private final CyclicBarrierTimer cyclicBarrierTimer = (USE_MULTICORE_PROCESS) ? new CyclicBarrierTimer() : null;
    private final CyclicBarrier threadBarrier = (USE_MULTICORE_PROCESS) ? new CyclicBarrier(NUMBER_OF_AVAILABLE_PROCESSORS + 1, cyclicBarrierTimer) : null;

    /**
    *  Number of interpolation points added between two input points.
    */
    private int splineStep = 5;

    /**
    *  Sequences of (x,y)'s representing the curve.
    */
    private float[] xs = null;

    /**
    *  Sequences of (x,y)'s representing the curve.
    */
    private float[] ys = null;

    /**
    *  Number of input vertices.
    */
    private int numberOfInVertices = 0;

    /**
    *  Max height of curve (the largest y-value).
    */
    private float height = 0.0f;

    /**
    *  The first LatheCurve class constructor.
    */
    public LatheCurve(float[] xsIn, float[] ysIn, int splineStep)
    {
        this.splineStep = splineStep;
        if (DEBUG_BUILD) checkLength(xsIn, ysIn);
        if (DEBUG_BUILD) checkYs(ysIn);
        numberOfInVertices = xsIn.length;

        // Multiplying the tangents by 2 makes them stronger, which makes the curve move more in the specified direction.
        // set startTangent to be heading to the right (tangent for first point in input sequence)
        Point2D.Float startTangent = new Point2D.Float(2.0f * (abs(xsIn[1]) - abs(xsIn[0])), 0.0f);

        // Multiplying the tangents by 2 makes them stronger, which makes the curve move more in the specified direction.
        // set endTangent to be heading to the left (tangent for last point in input sequence)
        Point2D.Float endTangent = new Point2D.Float(2.0f * (abs(xsIn[numberOfInVertices - 1]) - abs(xsIn[numberOfInVertices - 2])), 0.0f);

        performMakeCurve(xsIn, ysIn, startTangent, endTangent);
    }

    /**
    *  The second LatheCurve class constructor.
    *  Let the user supply the starting and ending tangents.
    */
    public LatheCurve(float[] xsIn, float[] ysIn, int splineStep, Point2D.Float startTangent, Point2D.Float endTangent)
    {
        this.splineStep = splineStep;
        checkLength(xsIn, ysIn);
        checkYs(ysIn);
        numberOfInVertices = xsIn.length;

        if (DEBUG_BUILD) checkTangent(startTangent);
        if (DEBUG_BUILD) checkTangent(endTangent);
        performMakeCurve(xsIn, ysIn, startTangent, endTangent);
    }

    /**
    *  Tests related to the sequences's length.
    */
    private void checkLength(float[] xsIn, float[] ysIn)
    {
        int numberOfVertices = xsIn.length;
        if (numberOfVertices < 2)
        {
            if (DEBUG_BUILD) println("Not enough points to make a curve");
        }
        else if (numberOfVertices != ysIn.length)
        {
            if (DEBUG_BUILD) println("xsIn[] and ysIn[] do not have the same number of points");
        }
    }

    /**
    *  Tests of the y-components of the sequence.
    *  Also find the largest y-value, which becomes the curve's height.
    */
    private void checkYs(float[] ysIn)
    {
        if (ysIn[0] != 0.0f)
        {
            if (DEBUG_BUILD) println("The first y-coordinate must be 0.0f; correcting it");
            ysIn[0] = 0.0f;
        }

        // find max height and make all y-coords >= 0
        height = ysIn[0];
        for (int i = 1; i < ysIn.length; i++)
        {
            if (ysIn[i] >= height)
                height = ysIn[i];

            if (ysIn[i] < 0.0f)
            {
                if (DEBUG_BUILD) println("Found a negative y-coord; changing it to be 0.0f");
                ysIn[i] = 0.0f;
            }
        }

        if (height != ysIn[ysIn.length - 1])
            if (DEBUG_BUILD) println("Warning: max height is not the last y-coordinate");
    }

    private void checkTangent(Point2D.Float tangent)
    {
        if ( (tangent.x == 0.0f) && (tangent.y == 0.0f) )
            println("A tangent cannot be (0.0f, 0.0f)");
    }

    private void performMakeCurve(float[] xsIn, float[] ysIn, Point2D.Float startTangent, Point2D.Float endTangent)
    {
        int numberOfOutVertices = countVertices(xsIn);
        xs = new float[numberOfOutVertices]; // resulting sequence after adding extra pts
        ys = new float[numberOfOutVertices];
        xs[0] = abs(xsIn[0]); // start of curve is initialised
        ys[0] = ysIn[0];

        if ( !USE_MULTICORE_PROCESS || ( (numberOfInVertices - 1) < MINIMUM_NUMBER_OF_INPUT_VERTICES_FOR_PARALLELIZATION ) )
        {
            makeCurve(xsIn, ysIn, startTangent, endTangent, 0, numberOfInVertices - 1);
        }
        else
        {
            int totalIterationsPerProcess = (numberOfInVertices - 1) / NUMBER_OF_AVAILABLE_PROCESSORS;
            LoggerThreadPoolExecutor executor = new LoggerThreadPoolExecutor(NUMBER_OF_AVAILABLE_PROCESSORS, NUMBER_OF_AVAILABLE_PROCESSORS, 0L, TimeUnit.MILLISECONDS,
                                                                             new LinkedBlockingQueue<Runnable>(NUMBER_OF_AVAILABLE_PROCESSORS),
                                                                             new LoggerThreadFactory("LatheCurve"),
                                                                             new ThreadPoolExecutor.CallerRunsPolicy() );

            cyclicBarrierTimer.clear();
            for (int threadId = 0; threadId < NUMBER_OF_AVAILABLE_PROCESSORS; threadId++)
                executor.execute( makeCurveProcessKernel(threadId, xsIn, ysIn, startTangent, endTangent, threadBarrier, totalIterationsPerProcess) );

            try
            {
                threadBarrier.await(); // wait for all threads to be ready
                threadBarrier.await(); // wait for all threads to finish
                executor.shutdown();
            }
            catch (BrokenBarrierException ex)
            {
                if (DEBUG_BUILD) println("Problem with a broken barrier with the main LatheCurve thread in performMakeCurve()!:\n" + ex.getMessage());
            }
            catch (InterruptedException ex)
            {
                // restore the interuption status after catching InterruptedException
                Thread.currentThread().interrupt();
                if (DEBUG_BUILD) println("Problem with pausing the main LatheCurve thread in performMakeCurve()!:\n" + ex.getMessage());
            }

            if (DEBUG_BUILD) println("\nTotal LatheCurve N-CP run time: " + (cyclicBarrierTimer.getTime() / 1e3) + " microsecs.\n");
        }
    }

    private Runnable makeCurveProcessKernel(final int threadId, final float[] xsIn, final float[] ysIn, final Point2D.Float startTangent, final Point2D.Float endTangent, final CyclicBarrier threadBarrier, final int totalIterationsPerProcess)
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
                        int extraIterations = ( threadId == (NUMBER_OF_AVAILABLE_PROCESSORS - 1) ) ? ( (numberOfInVertices - 1) % NUMBER_OF_AVAILABLE_PROCESSORS ) : 0;
                        int endPosition = (threadId + 1) * totalIterationsPerProcess + extraIterations;

                        makeCurve(xsIn, ysIn, startTangent, endTangent, startPosition, endPosition);
                    }
                    finally
                    {
                        threadBarrier.await();
                    }
                }
                catch (BrokenBarrierException ex)
                {
                    if (DEBUG_BUILD) println("Problem with a broken barrier with the N-Core thread with threadId " + threadId + " in makeCurveProcessKernel()!:\n" + ex.getMessage());
                }
                catch (InterruptedException ex)
                {
                    // restore the interuption status after catching InterruptedException
                    Thread.currentThread().interrupt();
                    if (DEBUG_BUILD) println("Problem with pausing the N-Core thread with threadId " + threadId + " in makeCurveProcessKernel()!:\n" + ex.getMessage());
                }
            }


        };
    }

    private void makeCurve(float[] xsIn, float[] ysIn, Point2D.Float startTangent, Point2D.Float endTangent, int startPosition, int endPosition)
    {
        // holds the tangents for the current curve segment between two points
        int splineIndex = 1;
        Point2D.Float t0 = new Point2D.Float();
        Point2D.Float t1 = new Point2D.Float();

        if (startPosition != 0)
        {
            splineIndex = findSplineIndex(xsIn, startPosition);
            setTangent(t1, xsIn, ysIn, startPosition + 1); // tangent at point startPosition + 1
        }

        for (int i = startPosition; i < endPosition; i++)
        {
            if (i == 0)
                t0.setLocation(startTangent.x, startTangent.y);
            else // use previous t1 tangent
                t0.setLocation(t1.x, t1.y);

            if (i == numberOfInVertices - 2) // next point is the last one
                t1.setLocation(endTangent.x, endTangent.y);
            else
                setTangent(t1, xsIn, ysIn, i + 1); // tangent at point i + 1

            // if xsIn[i] < 0.0f then use a straight line to link (x, y) to next (x, y)
            if (xsIn[i] < 0.0f)
            {
                xs[splineIndex] = abs(xsIn[i + 1]); // in case the next pt is -ve also
                ys[splineIndex] = ysIn[i + 1];
                splineIndex++;
            }
            else
            {
                // make a Hermite curve between the two points by adding splineStep new pts
                makeHermite(splineIndex, xsIn[i], ysIn[i], xsIn[i + 1], ysIn[i + 1], t0, t1);
                splineIndex += (splineStep + 1);
            }
        }
    }

    private int findSplineIndex(float[] xsIn, int startPosition)
    {
        int splineIndex = 1;
        for (int i = 0; i < startPosition; i++)
            splineIndex += (xsIn[i] < 0.0f) ? 1 : (splineStep + 1);

        return splineIndex;
    }

    /**
    *  Tests related to the sequences's length.
    *  The number of points in the new sequence depends on how many
    *  curve segments are to be added. Each curve segment between two points adds
    *  splineStep points to the sequence.
    *
    *  A (x,y) point where x is negative starts a straight line, so no
    *  new points are added. If s is positive, then splineStep points will
    *  be added.
    */
    private int countVertices(float[] xsIn)
    {
        int numberOfOutVertices = 1; // counting last coord
        for (int i = 0; i < numberOfInVertices - 1; i++)
        {
            if (xsIn[i] < 0.0f) // straight line starts here
                numberOfOutVertices++;
            else             // curve segment starts here
                numberOfOutVertices += (splineStep + 1);
        }

        return numberOfOutVertices;
    }

    /**
    *  Calculate the tangent at position i using Catmull-Rom spline-based interpolation.
    */
    private void setTangent(Point2D.Float tangent, float[] xsIn, float[] ysIn, int i)
    {
        double xLen = abs(xsIn[i + 1]) - abs(xsIn[i - 1]); // ignore any -ve
        double yLen = ysIn[i + 1] - ysIn[i - 1];
        tangent.setLocation(xLen / 2.0f, yLen / 2.0f);
    }

    /**
    *  Calculate the Hermite curve's (x,y) coordinates between points
    *  (x0,y0) and (x1,y1). t0 and t1 are the tangents for those points.
    *
    *  The (x0,y0) point is not included, since it was added at the
    *  end of the previous call to makeHermite().
    *
    *  Store the coordinates in the xs[] an ys[] arrays, starting
    *  at index splineIndex.
    */
    private void makeHermite(int splineIndex, float x0, float y0, float x1, float y1, Point2D.Float t0, Point2D.Float t1)
    {
        float xCoord = 0.0f;
        float yCoord = 0.0f;
        float tStep = 1.0f / (splineStep + 1.0f);
        float t = 0.0f;

        if (x1 < 0.0f)   // next point is negative to draw a line, make it
            x1 = -x1; // +ve while making the curve

        for (int i = 0; i < splineStep; i++)
        {
            t = tStep * (i + 1);
            xCoord = (fh1(t) * x0) + (fh2(t) * x1) + (fh3(t) * t0.x) + (fh4(t) * t1.x);
            xs[splineIndex + i] = xCoord;

            yCoord = (fh1(t) * y0) + (fh2(t) * y1) + (fh3(t) * t0.y) + (fh4(t) * t1.y);
            ys[splineIndex + i] = yCoord;
        }

        xs[splineIndex + splineStep] = x1;
        ys[splineIndex + splineStep] = y1;
    }

    /**
    *  Hermite blending functions.
    *  The first two functions blend the two points, the last two blend the two tangents.
    *  For an explanation of how the functions are derived, see any
    *  good computer graphics text, e.g. Foley and Van Dam.
    */
    private float fh1(float t)
    {
        return 2.0f * (t * t * t) - 3.0f * (t * t) + 1.0f;
    }

    /**
    *  Hermite blending functions.
    *  The first two functions blend the two points, the last two blend the two tangents.
    *  For an explanation of how the functions are derived, see any
    *  good computer graphics text, e.g. Foley and Van Dam.
    */
    private float fh2(float t)
    {
        return -2.0f * (t * t * t) + 3.0f * (t * t);
    }

    /**
    *  Hermite blending functions.
    *  The first two functions blend the two points, the last two blend the two tangents.
    *  For an explanation of how the functions are derived, see any
    *  good computer graphics text, e.g. Foley and Van Dam.
    */
    private float fh3(float t)
    {
        return (t * t * t) - 2.0f * (t * t) + t;
    }

    /**
    *  Hermite blending functions.
    *  The first two functions blend the two points, the last two blend the two tangents.
    *  For an explanation of how the functions are derived, see any
    *  good computer graphics text, e.g. Foley and Van Dam.
    */
    private float fh4(float t)
    {
        return (t * t * t) - (t * t);
    }

    /**
    *  Access generated coords and height.
    */
    public float[] getXs()
    {
        return xs;
    }

    /**
    *  Access generated coords and height.
    */
    public float[] getYs()
    {
        return ys;
    }

    /**
    *  Access generated coords and height.
    */
    public float getHeight()
    {
        return height;
    }


}