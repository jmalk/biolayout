package org.BioLayoutExpress3D.Graph.ActiveRendering;

import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* GraphAnimationThreadUpdater class encapsulates optional multicore animation thread update functionality to the OpenGL renderer.
*
* @author Thanos Theo, 2009-2010-2011
* @version 3.0.0.0
*
*/

final class GraphAnimationThreadUpdater extends Thread implements Runnable // package access
{

    /**
    *  Used to stop the updating animation thread.
    */
    private volatile boolean updating = false;

    /**
    *  Used to suspend the updating animation thread.
    */
    private volatile boolean animationUpdateThreadSuspended = false;

    /**
    *  GraphRendererThreadUpdater reference.
    */
    private GraphRendererThreadUpdater graphRendererThreadUpdater = null;

    /**
    *  GLCanvas reference.
    */
    private GraphRendererThreadUpdaterAnimationInterface graphRendererThreadUpdaterAnimationInterface = null;

    /**
    *  The GraphAnimationThreadUpdater constructor. Package access.
    */
    GraphAnimationThreadUpdater(GraphRendererThreadUpdater graphRendererThreadUpdater, GraphRendererThreadUpdaterAnimationInterface graphRendererThreadUpdaterAnimationInterface)
    {
        this.graphRendererThreadUpdater = graphRendererThreadUpdater;
        this.graphRendererThreadUpdaterAnimationInterface = graphRendererThreadUpdaterAnimationInterface;
    }

    /**
    *  The main animation update thread code logic.
    */
    @Override
    public void run()
    {
        updating = true;

        while (updating)
        {
            try
            {
                synchronized(this)
                {
                    // render one frame and pause thread to wait for main render thread to finish rendering and re-enable this thread
                    graphRendererThreadUpdaterAnimationInterface.updateAnimation();

                    if ( (graphRendererThreadUpdater != null) && graphRendererThreadUpdater.getRenderUpdateThreadSuspended() )
                        graphRendererThreadUpdater.resumeRendererThreadUpdater();
                    else
                        pauseAnimationThreadUpdater();

                    while (animationUpdateThreadSuspended)
                        wait();
                }
            }
            catch (InterruptedException ex)
            {
                // restore the interuption status after catching InterruptedException
                Thread.currentThread().interrupt();
                if (DEBUG_BUILD) println("Problem with the GraphRendererThreadUpdater for pausing/resuming!:\n" + ex.getMessage());
            }
        }

        // out of the main GraphAnimationThreadUpdater loop, when exiting the GraphAnimationThreadUpdater
    }

    /**
    *  Sets the GraphAnimationThreadUpdater updating variable. Package access.
    */
    synchronized void setUpdating(boolean updating)
    {
        this.updating = updating;
    }

    /**
    *  Pauses the GraphAnimationThreadUpdater thread.
    */
    private synchronized void pauseAnimationThreadUpdater()
    {
        if (!animationUpdateThreadSuspended)
        {
            if (DEBUG_BUILD) println("Now pausing the animation thread updater!");
            animationUpdateThreadSuspended = true;
        }
    }

    /**
    *  Resumes the GraphAnimationThreadUpdater thread. Package access.
    */
    synchronized void resumeAnimationThreadUpdater()
    {
        if (animationUpdateThreadSuspended)
        {
            if (DEBUG_BUILD) println("Now resuming the animation thread updater!");
            animationUpdateThreadSuspended = false;
            notify();
        }
    }

    /**
    *  Gets the GraphAnimationThreadUpdater thread state. Package access.
    */
    boolean getAnimationUpdateThreadSuspended()
    {
        return animationUpdateThreadSuspended;
    }

    /**
    *  Overriden toString() method for GraphAnimationThreadUpdater.
    */
    @Override
    public String toString()
    {
        return "GraphAnimationThreadUpdater (" + this.getName() + ")";
    }


}
