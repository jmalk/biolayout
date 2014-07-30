package org.BioLayoutExpress3D.CPUParallelism;

/**
*
* The CyclicBarrierTimer class is a helper class that is used to create a timer object for CyclicBarriers.
*
* @author Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*/

public class CyclicBarrierTimer implements Runnable
{

    /**
    *  Auxiliary variable for BarrierTimer.
    */
    private boolean started = false;

    /**
    *  Auxiliary variable for BarrierTimer.
    */
    private long startTime = 0;

    /**
    *  Auxiliary variable for BarrierTimer.
    */
    private long endTime = 0;

    /**
    *  The main run() method.
    */
    @Override
    public synchronized void run()
    {
        long nowTime = System.nanoTime();
        if (!started)
        {
            started = true;
            startTime = nowTime;
        }
        else
            endTime = nowTime;
    }

    /**
    *  Clears the started variable.
    */
    public synchronized void clear()
    {
        started = false;
    }

    /**
    *  Returns the time the timer has logged.
    */
    public synchronized long getTime()
    {
        return (endTime - startTime);
    }


}