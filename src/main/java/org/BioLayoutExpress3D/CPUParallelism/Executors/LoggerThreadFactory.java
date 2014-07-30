package org.BioLayoutExpress3D.CPUParallelism.Executors;

import java.util.concurrent.*;

/**
*
* The LoggerThreadFactory class is used to create threads that is used within a thread pool in executors.
*
* @see org.BioLayoutExpress3D.CPUParallelism.Executors.LoggerThread
* @see org.BioLayoutExpress3D.CPUParallelism.Executors.LoggerThreadPoolExecutor
* @author Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*/

public class LoggerThreadFactory implements ThreadFactory
{

    /**
    *  The pool name of a LoggerThreadFactory.
    */
    private String poolName = "";

    /**
    *  The constructor of the LoggerThreadFactory class.
    */
    public LoggerThreadFactory(String poolName)
    {
        this.poolName = poolName;
    }

    /**
    *  The main newThread() method of the ThreadFactory.
    */
    @Override
    public Thread newThread(Runnable runnable)
    {
        return new LoggerThread(runnable, poolName);
    }


}