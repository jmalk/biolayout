package org.BioLayoutExpress3D.CPUParallelism.Executors;

import java.util.concurrent.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* The BlockingBoundedExecutor class is used to bound the task injection rate in an executor.
*
* In such an approach, use an unbounded queue for the executor as there's no reason to bound both the queue size and the injection rate.
* Then, set the bound on the semaphore to be equal to the thread pool size plus the number of queued tasks you want to allow,
* since the semaphore is bounding the number of tasks both currently executing and awaiting execution.
*
* @author Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*/

public class BlockingBoundedExecutor
{

    /**
    *  The executor used for the BlockingBoundedExecutor.
    */
    private Executor executor = null;

    /**
    *  The Semaphore to be used to turn the executor into a blocking bounded executor.
    */
    private Semaphore semaphore = null;

    /**
    *  The constructor of the BlockingBoundedExecutor class.
    */
    public BlockingBoundedExecutor(Executor executor, int bound)
    {
        this.executor = executor;
        this.semaphore = new Semaphore(bound);
    }

    /**
    *  Executes the runnable if the semaphore permits the execution of the given runnable.
    */
    public void submitTask(final Runnable runnable)
    {
        try
        {
            semaphore.acquire();
            try
            {
                executor.execute(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        try
                        {
                            runnable.run();
                        }
                        finally
                        {
                            semaphore.release();
                        }
                    }

                } );
            }
            catch (RejectedExecutionException ignored)
            {
                semaphore.release();
            }
        }
        catch (InterruptedException ex)
        {
            // restore the interuption status after catching InterruptedException
            Thread.currentThread().interrupt();
            if (DEBUG_BUILD) println("InterruptedException with the semaphore.acquire() method in the BlockingBoundedExecutor class:\n" + ex.getMessage() );
        }
    }


}