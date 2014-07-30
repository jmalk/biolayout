package org.BioLayoutExpress3D.CPUParallelism.Executors;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* The LoggerThreadPoolExecutor class is used to create a custom Thread Pool Executor with logging capabilities.
*
* @see org.BioLayoutExpress3D.CPUParallelism.Executors.LoggerThread
* @see org.BioLayoutExpress3D.CPUParallelism.Executors.LoggerThreadFactory
* @author Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*/

public class LoggerThreadPoolExecutor extends ThreadPoolExecutor
{

    /**
    *  Variable to be used from the LoggerThreadPoolExecutor.
    */
    private final ThreadLocal<Long> startTime = (DEBUG_BUILD) ? new ThreadLocal<Long>() : null;

    /**
    *  Variable to be used from the LoggerThreadPoolExecutor.
    */
    private final AtomicLong numberOfTasks = (DEBUG_BUILD) ? new AtomicLong() : null;

    /**
    *  Variable to be used from the LoggerThreadPoolExecutor.
    */
    private final AtomicLong totalTime = (DEBUG_BUILD) ? new AtomicLong() : null;

    /**
    *  The first constructor of the LoggerThreadPoolExecutor class.
    */
    public LoggerThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue)
    {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    /**
    *  The second constructor of the LoggerThreadPoolExecutor class.
    */
    public LoggerThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory)
    {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    /**
    *  The third constructor of the LoggerThreadPoolExecutor class.
    */
    public LoggerThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler)
    {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    /**
    *  The fourth constructor of the LoggerThreadPoolExecutor class.
    */
    public LoggerThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler)
    {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    /**
    *  Overrides the beforeExecute() method of the parent ThreadPoolExecutor class.
    */
    @Override
    protected void beforeExecute(Thread thread, Runnable runnable)
    {
        super.beforeExecute(thread, runnable);

        if (DEBUG_BUILD)
        {
            printf("%s: start %s\n", thread, runnable);
            startTime.set( System.nanoTime() );
        }
    }

    /**
    *  Overrides the afterExecute() method of the parent ThreadPoolExecutor class.
    */
    @Override
    protected void afterExecute(Runnable runnable, Throwable t)
    {
        try
        {
            if (DEBUG_BUILD)
            {
                long taskTime = System.nanoTime() - startTime.get();
                numberOfTasks.incrementAndGet();
                totalTime.addAndGet(taskTime);
                printf("Throwable %s: ended Runnable %s, with time taken: %d nsecs\n", t, runnable, taskTime);
            }
        }
        finally
        {
            super.afterExecute(runnable, t);
        }
    }

    /**
    *  Overrides the terminated() method of the parent ThreadPoolExecutor class.
    */
    @Override
    protected void terminated()
    {
        try
        {
            // warning, have to run in another thread to avoid InterruptedException problems with multicore ConsoleOutput thread interruption & Executor.shutdown()!
            if (DEBUG_BUILD)
            {
                new Thread( new Runnable()
                {

                    @Override
                    public void run()
                    {
                        printf( "Terminated Logger Thread Pool: avg time=%d nsecs\n", totalTime.get() / ( (numberOfTasks.get() == 0) ? 1 : numberOfTasks.get() ) );
                    }

                } ).start();
            }
        }
        finally
        {
            super.terminated();
        }
    }


}