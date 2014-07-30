package org.BioLayoutExpress3D.CPUParallelism.Executors;

import java.util.concurrent.atomic.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* The LoggerThread class is used to create threads from a ThreadFactory that is used within a thread pool in executors.
*
* @see org.BioLayoutExpress3D.CPUParallelism.Executors.LoggerThreadFactory
* @see org.BioLayoutExpress3D.CPUParallelism.Executors.LoggerThreadPoolExecutor
* @author Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*/

public class LoggerThread extends Thread
{

    /**
    *  The default name of a LoggerThread.
    */
    private static final String DEFAULT_NAME = "LoggerThread";

    /**
    *  Variable to be used from the LoggerThread.
    */
    private static final AtomicInteger created = new AtomicInteger();

    /**
    *  Variable to be used from the LoggerThread.
    */
    private static final AtomicInteger alive = new AtomicInteger();

    /**
    *  The first constructor of the LoggerThread class.
    */
    public LoggerThread(Runnable runnable)
    {
        this(runnable, DEFAULT_NAME);
    }

    /**
    *  The second constructor of the LoggerThread class.
    */
    public LoggerThread(Runnable runnable, String name)
    {
        super(runnable, name + " thread id: " + created.incrementAndGet());
    }

    /**
    *  The run method of LoggerThread.
    */
    @Override
    @SuppressWarnings("CallToThreadRun")
    public void run()
    {
        if (DEBUG_BUILD) println("Created " + getName());

        try
        {
            alive.incrementAndGet();
            super.run();
        }
        finally
        {
            alive.decrementAndGet();

            // warning, have to run in another thread to avoid InterruptedException problems with multicore ConsoleOutput thread interruption & Executor.shutdown()!
            if (DEBUG_BUILD)
            {
                new Thread( new Runnable()
                {

                    @Override
                    public void run()
                    {
                        println("Exiting " + getName());
                    }

                } ).start();
            }
        }
    }

    /**
    *  Gets the total threads created.
    */
    public static int getThreadsCreated()
    {
        return created.get();
    }

    /**
    *  Gets the current threads alive.
    */
    public static int getThreadsAlive()
    {
        return alive.get();
    }


}