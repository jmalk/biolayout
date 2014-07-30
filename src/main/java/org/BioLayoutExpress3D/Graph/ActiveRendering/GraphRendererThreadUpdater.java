package org.BioLayoutExpress3D.Graph.ActiveRendering;

import java.util.concurrent.*;
import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* GraphRendererThreadUpdater class encapsulates optional multicore renderer thread update functionality to the OpenGL renderer.
*
* @author Thanos Theo, 2009-2010-2011
* @version 3.0.0.0
*
*/

public final class GraphRendererThreadUpdater extends Thread implements Runnable, GraphRendererThreadUpdaterInterface
{

    /**
    *  Value needed for the GraphRendererThreadUpdater.
    */
    private static final int NO_DELAYS_PER_YIELD = 16;

    /**
    *  Value needed for the GraphRendererThreadUpdater.
    */
    private static final int MAX_FRAME_SKIPS = 5;

    /**
    *  Value needed for the GraphRendererThreadUpdater.
    */
    private static final float UPDATE_FPS_COUNTER_EVERY_N_SECOND = 0.5f;

    /**
    *  Used to stop the rendering animation thread.
    */
    private volatile boolean rendering = false;

    /**
    *  Used to suspend the rendering animation thread.
    */
    private volatile boolean renderUpdateThreadSuspended = false;

    /**
    *  Used to generally pause/resume the renderer.
    */
    private volatile boolean generalPauseRendererUpdateThread = false;

    /**
    *  Value needed for the GraphRendererThreadUpdater.
    */
    private int targetFPS = 60;

    /**
    *  Value needed for the GraphRendererThreadUpdater.
    */
    private long period = 0;

    /**
    *  Value needed for the GraphRendererThreadUpdater.
    */
    private long startTime = 0;

    /**
    *  Value needed for the GraphRendererThreadUpdater.
    */
    private long prevStatsTime = 0;

    /**
    *  Value needed for the GraphRendererThreadUpdater.
    */
    private long frameCount = 0;

    /**
    *  Value needed for the GraphRendererThreadUpdater.
    */
    private int averageFPS = 0;

    /**
    *  GraphAnimationThreadUpdater reference.
    */
    private GraphAnimationThreadUpdater graphAnimationThreadUpdater = null;

    /**
    *  GLCanvas reference.
    */
    private GLCanvas glCanvas = null;

    /**
    *  GraphRendererThreadUpdaterAnimationInterface reference.
    */
    private GraphRendererThreadUpdaterAnimationInterface graphRendererThreadUpdaterAnimationInterface = null;

    /**
    *  Value needed for the GraphRendererThreadUpdater.
    */
    private volatile boolean frameskip = false;

    /**
    *  Value needed for the GraphRendererThreadUpdater.
    */
    private boolean fullscreen = false;

    /**
    *  Value needed for the GraphRendererThreadUpdater.
    */
    private boolean isUsingFrameSkip = false;

    /**
    *  Value needed for the GraphRendererThreadUpdater.
    */
    private long excessTime = 0L;

    /**
    *  The GraphRendererThreadUpdater constructor.
    */
    public GraphRendererThreadUpdater(GLCanvas glCanvas, GraphRendererThreadUpdaterAnimationInterface graphRendererThreadUpdaterAnimationInterface, boolean frameskip, boolean fullscreen, int targetFPS)
    {
        this.glCanvas = glCanvas;
        this.graphRendererThreadUpdaterAnimationInterface = graphRendererThreadUpdaterAnimationInterface;
        this.frameskip = frameskip;
        this.fullscreen = fullscreen;
        this.targetFPS = targetFPS;

        period = 1000000000L / targetFPS; // in msecs

        if (USE_MULTICORE_PROCESS)
        {
            graphAnimationThreadUpdater = new GraphAnimationThreadUpdater(this, graphRendererThreadUpdaterAnimationInterface);
            graphAnimationThreadUpdater.setPriority(Thread.NORM_PRIORITY);
        }
    }

    /**
    *  The main animation update thread code logic.
    */
    @Override
    public void run()
    {
        long beforeTime = 0L, afterTime = 0L, timeDiff = 0L, sleepTime = 0L;
        long overSleepTime = 0L;
        int noDelays = 0;

        startTime = System.nanoTime();
        beforeTime = startTime;

        rendering = true;

        while (rendering)
        {
            // Update the animated parts
            if (!USE_MULTICORE_PROCESS)
            {
                graphRendererThreadUpdaterAnimationInterface.updateAnimation();
            }
            else
            {
                synchronized(this)
                {
                    // resume the animationThreadUpdater thread to run concurrently with the renderer thread
                    if ( (graphAnimationThreadUpdater != null) && graphAnimationThreadUpdater.getAnimationUpdateThreadSuspended() )
                        graphAnimationThreadUpdater.resumeAnimationThreadUpdater();
                    else
                        pauseRendererThreadUpdater();
                }
            }

            // Render the screen
            if (DEBUG_BUILD) println("Display() callback for:\n" + this.toString());
            glCanvas.display();

            // Update the FPS counter statistics
            reportFPSStatistics();

            afterTime = System.nanoTime();
            timeDiff = afterTime - beforeTime;
            sleepTime = (period - timeDiff) - overSleepTime;

            if (sleepTime > 0)
            {
                // some time left in this cycle
                try
                {
                    TimeUnit.NANOSECONDS.sleep(sleepTime);
                }
                catch (InterruptedException ex)
                {
                    // restore the interuption status after catching InterruptedException
                    Thread.currentThread().interrupt();
                    if (DEBUG_BUILD) println("Problem with sleeping of the GraphRendererThreadUpdater:\n" + ex.getMessage());
                }

                overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
            }
            else
            {    // sleepTime <= 0; the frame took longer than the period
                 excessTime -= sleepTime;  // store excessTime time value
                 overSleepTime = 0L;

                 if (++noDelays >= NO_DELAYS_PER_YIELD)
                 {
                     Thread.yield(); // give another thread a chance to run
                     noDelays = 0;
                 }
            }

            beforeTime = System.nanoTime();

            /* If frame animation is taking too long, update the renderer state
            without rendering it, to get the updates/sec nearer to the required FPS. */
            int skips = 0;
            while ( frameskip && (excessTime > period) && (skips < MAX_FRAME_SKIPS) )
            {
                excessTime -= period;
                // update state but don't render (frameskip)
                if (frameskip) // update renderer content
                {
                    isUsingFrameSkip = true;
                    graphRendererThreadUpdaterAnimationInterface.updateAnimation();
                }
                else
                {
                    // reset the excessTime time variable to avoid previously recorded frameskips
                    excessTime = 0L;
                    break;
                }

                skips++;
            }

            pauseRenderUpdateThread();
        }

        // out of the main GraphRendererThreadUpdater loop, when exiting the GraphRendererThreadUpdater
    }

    /**
    *  Reports the FPS statistics (momentary method).
    *  The statistics: the actual frame rate is calculated every second (1000L msecs)
    */
    private void reportFPSStatistics()
    {
        frameCount++;
        long timeNow = System.nanoTime();
        long realElapsedTime = (timeNow - prevStatsTime) / 1000000L;   // time since last stats collection
        if ( realElapsedTime > (UPDATE_FPS_COUNTER_EVERY_N_SECOND * 1000L) ) // update FPS counter once per N seconds
        {
            averageFPS = (int)( (frameCount - 1) / UPDATE_FPS_COUNTER_EVERY_N_SECOND );
            frameCount = 0;
            prevStatsTime = timeNow;
            if (isUsingFrameSkip)
                isUsingFrameSkip = false;

            if (DEBUG_BUILD && !fullscreen) println("averageFPS: " + averageFPS);
        }
    }

    /**
    *  Sets the GraphRendererThreadUpdater variable.
    */
    public synchronized void setRendering(boolean rendering)
    {
        if (USE_MULTICORE_PROCESS)
        {
            // make sure the animation thread updater is resumed so as to exit properly
            if (!rendering)
                graphAnimationThreadUpdater.resumeAnimationThreadUpdater();
            graphAnimationThreadUpdater.setUpdating(rendering);
        }

        if (!rendering)
            resumeRendererThreadUpdater();
        this.rendering = rendering;
    }

    /**
    *  Gets the frameCount variable.
    */
    public long getFrameCount()
    {
        return frameCount;
    }

    /**
    *  Gets the targetFPS variable.
    */
    public int getTargetFPS()
    {
        return targetFPS;
    }

    /**
    *  Sets the targetFPS variable.
    */
    public void setTargetFPS(int targetFPS)
    {
        this.targetFPS = targetFPS;

        period = 1000000000L / targetFPS; // in msecs
    }

    /**
    *  Gets the frameskip variable.
    */
    public boolean getFrameskip()
    {
        return frameskip;
    }

    /**
    *  Sets the frameskip variable.
    */
    public void setFrameskip(boolean frameskip)
    {
        this.frameskip = frameskip;
        // reset the excessTime time variable to avoid previously recorded frameskips
        excessTime = 0L;
    }

    /**
    *  Gets the averageFPS variable.
    */
    public int getAverageFPS()
    {
        return averageFPS;
    }

    /**
    *  Gets the isUsingFrameSkip variable.
    */
    public boolean getIsUsingFrameSkip()
    {
        return isUsingFrameSkip;
    }

    /**
    *  Gets the MAX_FRAME_SKIPS variable.
    */
    public int getMaxFrameSkips()
    {
        return MAX_FRAME_SKIPS;
    }

    /**
    *  Pauses the GraphRendererThreadUpdater thread.
    */
    private synchronized void pauseRendererThreadUpdater()
    {
        if (!renderUpdateThreadSuspended)
        {
            if (DEBUG_BUILD) println("Now pausing the renderer thread updater!");
            renderUpdateThreadSuspended = true;
        }
    }

    /**
    *  Resumes the GraphRendererThreadUpdater thread.
    */
    public synchronized void resumeRendererThreadUpdater()
    {
        if (renderUpdateThreadSuspended)
        {
            if (DEBUG_BUILD) println("Now resuming the renderer thread updater!");
            renderUpdateThreadSuspended = false;
        }
    }

    /**
    *  Gets the renderUpdateThreadSuspended state.
    */
    public boolean getRenderUpdateThreadSuspended()
    {
        return renderUpdateThreadSuspended;
    }

    /**
    *  Makes the GraphRendererThreadUpdater to generally pause.
    */
    private void pauseRenderUpdateThread()
    {
        try
        {
            //Synchronized method for thread suspend/resume operations
            if (generalPauseRendererUpdateThread)
            {
                synchronized(this)
                {
                    while (generalPauseRendererUpdateThread)
                        wait();
                }
            }
        }
        catch (InterruptedException ex)
        {
            // restore the interuption status after catching InterruptedException
            Thread.currentThread().interrupt();
            if (DEBUG_BUILD) println("Problem with the pauseRenderUpdateThread() method in the GraphRendererThreadUpdater class:\n" + ex.getMessage());
        }
    }

    /**
    *  Starts the GraphRendererThreadUpdater (and GraphAnimationThreadUpdater) thread(s).
    */
    @Override
    public void start()
    {
        if (USE_MULTICORE_PROCESS)
            graphAnimationThreadUpdater.start();
        super.start();
    }

    /**
    *  Starts the GraphRendererThreadUpdater (and GraphAnimationThreadUpdater) thread(s) with a set priority.
    */
    public void startWithPriority(int priority)
    {
        if (USE_MULTICORE_PROCESS)
            graphAnimationThreadUpdater.setPriority(priority);
        super.setPriority(priority);

        if (USE_MULTICORE_PROCESS)
            graphAnimationThreadUpdater.start();
        super.start();
    }

    /**
    *  Overriden toString() method for GraphRendererThreadUpdater.
    */
    @Override
    public String toString()
    {
        return "GraphRendererThreadUpdater (" + this.getName() + ")" + ( (USE_MULTICORE_PROCESS) ? " & " + graphAnimationThreadUpdater.toString() : "" );
    }

    /**
    *  Generally pauses the GraphRendererThreadUpdater.
    */
    @Override
    public synchronized void generalPauseRenderUpdateThread()
    {
        if (!generalPauseRendererUpdateThread)
        {
            if (DEBUG_BUILD) println("Now generally pausing the renderer thread updater!");
            generalPauseRendererUpdateThread = true;
        }
    }

    /**
    *  Generally resumes the GraphRendererThreadUpdater.
    */
    @Override
    public synchronized void generalResumeRenderUpdateThread()
    {
        if (generalPauseRendererUpdateThread)
        {
            if (DEBUG_BUILD) println("Now generally resuming the renderer thread updater!");
            generalPauseRendererUpdateThread = false;
            notify();
        }
    }


}
