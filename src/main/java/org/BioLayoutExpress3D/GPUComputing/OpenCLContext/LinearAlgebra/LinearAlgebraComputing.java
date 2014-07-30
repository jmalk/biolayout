package org.BioLayoutExpress3D.GPUComputing.OpenCLContext.LinearAlgebra;

import java.nio.*;
import java.util.concurrent.*;
import javax.swing.*;
import org.jocl.*;
import org.BioLayoutExpress3D.CPUParallelism.*;
import org.BioLayoutExpress3D.CPUParallelism.Executors.*;
import org.BioLayoutExpress3D.GPUComputing.OpenCLContext.*;
import static java.lang.Math.*;
import static org.jocl.CL.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* LinearAlgebraComputing is the main OpenCL context component for Linear Algebra GPU Computing.
* It also includes an N-CP implementation to test against the CPU.
*
* @author Marco Hutter, Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*
*/

public class LinearAlgebraComputing extends OpenCLContext
{

    /**
    *  Serial version UID variable for the OpenGLContext class.
    */
    public static final long serialVersionUID = 117222333444555670L;

    private static final int ITERATIONS = 30000;
    private static final int N = 1000000;
    private static final int LOCAL_WORK_SIZE = 64;

    private FloatBuffer srcBufferA = null;
    private FloatBuffer srcBufferB = null;
    private FloatBuffer dstGPUBuffer = null;
    private Pointer srcAPointer = null;
    private Pointer srcBPointer = null;
    private Pointer dstGPUPointer = null;
    private float alpha = 0.0f;

    // variables needed for N-CP
    private final CyclicBarrierTimer cyclicBarrierTimer = (USE_MULTICORE_PROCESS) ? new CyclicBarrierTimer() : null;
    private final CyclicBarrier threadBarrier = (USE_MULTICORE_PROCESS) ? new CyclicBarrier(NUMBER_OF_AVAILABLE_PROCESSORS + 1, cyclicBarrierTimer) : null;

    /**
    *  The first constructor of the LinearAlgebraComputing class.
    */
    public LinearAlgebraComputing(JFrame jFrame)
    {
        super(jFrame);
    }

    /**
    *  The second constructor of the LinearAlgebraComputing class.
    */
    public LinearAlgebraComputing(JFrame jFrame, boolean dialogErrorLog)
    {
        super(jFrame, dialogErrorLog);
    }

    /**
    *  The third constructor of the LinearAlgebraComputing class.
    */
    public LinearAlgebraComputing(JFrame jFrame, boolean dialogErrorLog, boolean profileCommandQueue)
    {
        super(jFrame, dialogErrorLog, profileCommandQueue);
    }

    /**
    *  The fourth constructor of the LinearAlgebraComputing class.
    */
    public LinearAlgebraComputing(JFrame jFrame, boolean dialogErrorLog, boolean profileCommandQueue, boolean openCLSupportAndExtensionsLogOnly)
    {
        super(jFrame, dialogErrorLog, profileCommandQueue, openCLSupportAndExtensionsLogOnly);
    }

    /**
    *  Initializes CPU memory.
    */
    @Override
    protected void initializeCPUMemoryImplementation() throws CLException, OutOfMemoryError
    {
        // Create input- and output data
        srcBufferA = FloatBuffer.allocate(N);
        srcBufferB = FloatBuffer.allocate(N);
        dstGPUBuffer = FloatBuffer.allocate(N);
        for (int i = 0; i < N; i++)
        {
            srcBufferA.put(i);
            srcBufferB.put(i);
        }
        srcBufferA.rewind();
        srcBufferB.rewind();
        dstGPUBuffer.rewind();
        srcAPointer = Pointer.to(srcBufferA);
        srcBPointer = Pointer.to(srcBufferB);
        dstGPUPointer = Pointer.to(dstGPUBuffer);
        alpha = 1.0f / 9.0f;
    }

    /**
    *  Initializes GPU memory.
    */
    @Override
    protected void initializeGPUMemory() throws CLException
    {
        // Allocate the memory objects for the input- and output data
        memoryObjects = new cl_mem[3];
        memoryObjects[0] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * N, srcAPointer, null);
        memoryObjects[1] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * N, srcBPointer, null);
        memoryObjects[2] = clCreateBuffer(context, CL_MEM_READ_WRITE, Sizeof.cl_float * N, null, null);

        programs = new cl_program[1];
        kernels  = new cl_kernel[1];
        KernelUtils.loadKernelFileCreateProgramAndKernel(context, "LinearAlgebra", "Linearalgebra", "main", LOAD_KERNEL_PROGRAMS_FROM_EXTERNAL_SOURCE, programs, kernels, 0, "", "-cl-mad-enable");
    }

    /**
    *  Performs the GPU Computing calculations.
    */
    @Override
    protected void performGPUComputingCalculations() throws CLException
    {
        // Set the arguments for the kernel
        clSetKernelArg(kernels[0], 0, Sizeof.cl_mem,   Pointer.to(memoryObjects[0]));
        clSetKernelArg(kernels[0], 1, Sizeof.cl_mem,   Pointer.to(memoryObjects[1]));
        clSetKernelArg(kernels[0], 2, Sizeof.cl_mem,   Pointer.to(memoryObjects[2]));
        clSetKernelArg(kernels[0], 3, Sizeof.cl_float, Pointer.to(new float[]{ alpha }));
        clSetKernelArg(kernels[0], 4, Sizeof.cl_int,   Pointer.to(new   int[]{ ITERATIONS }));

        // Set the work-item dimensions
        long localWorkSize = (CL_IS_PLATFORM_AMD_ATI[0]) ? LOCAL_WORK_SIZE : ( (CL_ALL_PLATFORM_DEVICES_MAX_WORK_ITEM_SIZES[0][deviceIndex][2] != 0)
                             ? CL_ALL_PLATFORM_DEVICES_MAX_WORK_ITEM_SIZES[0][deviceIndex][2] : LOCAL_WORK_SIZE );
        long[] globalWorkSizes = new long[]{ N };
        long[]  localWorkSizes = new long[]{ localWorkSize };
        if (DEBUG_BUILD)
        {
            println("Global Work Size for this kernel execution: " + N);
            println(" Local Work Size for this kernel execution: " + localWorkSize);
        }

        // Execute the 1D kernel
        if (profileCommandQueue)
        {
            kernelEvents = new cl_event[1][];
            kernelEvents[0] = new cl_event[]{ new cl_event() };
        }

        clEnqueueNDRangeKernel(commandQueue, kernels[0], 1, null, globalWorkSizes, localWorkSizes, 0, null, (profileCommandQueue) ? kernelEvents[0][0] : null);

        if (profileCommandQueue)
        {
            // Wait for the the event, i.e. until the kernel has completed
            clWaitForEvents(1, kernelEvents[0]);
        }
    }

    /**
    *  Retrieves GPU results.
    */
    @Override
    protected void retrieveGPUResultsImplementation() throws CLException, OutOfMemoryError
    {
        // Read the output data
        if (profileCommandQueue)
        {
            readEvents = new cl_event[1][];
            readEvents[0] = new cl_event[]{ new cl_event() };
        }

        clEnqueueReadBuffer(commandQueue, memoryObjects[2], CL_TRUE, 0, Sizeof.cl_float * N, dstGPUPointer, 0, null, (profileCommandQueue) ? readEvents[0][0] : null);

        if (profileCommandQueue)
        {
            // Wait for the the event, i.e. until the results are read
            clWaitForEvents(1, readEvents[0]);
        }

        // Flush running task
        clFlush(commandQueue);

        // Finish running task
        clFinish(commandQueue);

        compareGPUResultsWithCPU();
    }

    /**
    *  Deletes the OpenGL context for GPU computing.
    */
    @Override
    protected void deleteOpenCLContextForGPUComputing() throws CLException
    {
        srcAPointer = null;
        srcBPointer = null;
        dstGPUPointer = null;
        srcBufferA = null;
        srcBufferB = null;
        dstGPUBuffer = null;

        // Release kernel, program, and memory objects
        KernelUtils.releaseKernelAndProgram(programs, kernels, 0);
        programs = null;
        kernels = null;

        for (int i = 0; i < memoryObjects.length; i++)
        {
            if (memoryObjects[i] != null)
            {
                clReleaseMemObject(memoryObjects[i]);
                memoryObjects[i] = null;
            }
        }
        memoryObjects = null;

        System.gc();
    }

    /**
    *  Compares the the GPU results with the CPU.
    */
    private void compareGPUResultsWithCPU()
    {
        float[] srcArrayA = srcBufferA.array();
        float[] srcArrayB = srcBufferB.array();
        float[] dstCPUArray = new float[N];
        long total = 0;
        // calc on CPU
        if (USE_MULTICORE_PROCESS)
        {
            calculateNCPLinealAlgebra(srcArrayA, srcArrayB, dstCPUArray);
            total = cyclicBarrierTimer.getTime();
            if (DEBUG_BUILD) println("\nTotal time taken for Linear Algebra CPU calculations: " + (total / 1e6) + " msecs.\n");
        }
        else
        {
            long startTime = System.nanoTime();
            for (int i = 0; i < N; i++)
            {
                float sum = 0.0f;
                for (int iterations = 0; iterations < ITERATIONS; iterations++)
                    sum += srcArrayA[iterations] * srcArrayB[iterations];
                dstCPUArray[i] = sum * srcArrayA[i] + (float)sqrt(alpha * srcArrayB[i]);
            }
            total = (System.nanoTime() - startTime);
            if (DEBUG_BUILD) println("\nTotal time taken for Linear Algebra N-CP CPU calculations: " + (total / 1e6) + " msecs.\n");
        }

        // Verify the result
        boolean passed = true;
        final float epsilon = 1e-6f; // precision up to 5 decimals
        for (int i = 0; i < N; i++)
        {
            if ( !( abs(dstGPUBuffer.get(i) - dstCPUArray[i]) <= ( epsilon * abs( dstGPUBuffer.get(i) ) ) ) )
            {
                passed = false;
                break;
            }
        }

        if (DEBUG_BUILD)
        {
            println( "\nGPU vs. CPU Results Test " + ( (passed) ? "PASSED!" : "FAILED!" ) );
            // Print the results
            println("Array Results on GPU vs. CPU:\n");
            printArrayResults(dstGPUBuffer, 10);
            printArrayResults(dstCPUArray, 10);
        }

        if (profileCommandQueue)
        {
            // Print the timing information for the commands
            CLEventExecutionStatistics clEventExecutionStatistics = new CLEventExecutionStatistics();
            clEventExecutionStatistics.addEntry("kernel", kernelEvents[0][0]);
            clEventExecutionStatistics.addEntry("  read", readEvents[0][0]);
            clEventExecutionStatistics.print();

            if (DEBUG_BUILD) println("\nGPU is " + ( total / (double)clEventExecutionStatistics.getDurationTime(kernelEvents[0][0]) ) + " times faster than the CPU for this calculation.");
        }
    }

    /**
    *  Calculates the Linear Algebra algorithm using N-CP.
    */
    private void calculateNCPLinealAlgebra(float[] srcArrayA, float[] srcArrayB, float[] dstCPUArray)
    {
            int totalLoopsPerProcess = N / NUMBER_OF_AVAILABLE_PROCESSORS;
            LoggerThreadPoolExecutor executor = new LoggerThreadPoolExecutor(NUMBER_OF_AVAILABLE_PROCESSORS, NUMBER_OF_AVAILABLE_PROCESSORS, 0L, TimeUnit.MILLISECONDS,
                                                                             new LinkedBlockingQueue<Runnable>(NUMBER_OF_AVAILABLE_PROCESSORS),
                                                                             new LoggerThreadFactory("LinealAlgebra"),
                                                                             new ThreadPoolExecutor.CallerRunsPolicy() );

            cyclicBarrierTimer.clear();
            for (int threadId = 0; threadId < NUMBER_OF_AVAILABLE_PROCESSORS; threadId++)
                executor.execute( linearAlgebraProcessKernel(threadId, totalLoopsPerProcess, srcArrayA, srcArrayB, dstCPUArray) );

            try
            {
                threadBarrier.await(); // wait for all threads to be ready
                threadBarrier.await(); // wait for all threads to finish
                executor.shutdown();
            }
            catch (BrokenBarrierException ex)
            {
                if (DEBUG_BUILD) println("Problem with a broken barrier with the main LinealAlgebra thread in calculateNCPLinealAlgebra()!:\n" + ex.getMessage());
            }
            catch (InterruptedException ex)
            {
                // restore the interuption status after catching InterruptedException
                Thread.currentThread().interrupt();
                if (DEBUG_BUILD) println("Problem with pausing the main LinealAlgebra thread in calculateNCPLinealAlgebra()!:\n" + ex.getMessage());
            }
    }

    /**
    *   Return a light-weight runnable using the Adapter technique for the Linear Algebra algorithm so as to avoid any load latencies.
    *   The coding style simulates an OpenCL/CUDA kernel.
    */
    private Runnable linearAlgebraProcessKernel(final int threadId, final int totalLoopsPerProcess, final float[] srcArrayA, final float[] srcArrayB, final float[] dstCPUArray)
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
                        int extraLoops = ( threadId == (NUMBER_OF_AVAILABLE_PROCESSORS - 1) ) ? (N % NUMBER_OF_AVAILABLE_PROCESSORS) : 0;
                        for (int i = threadId * totalLoopsPerProcess; i < (threadId + 1) * totalLoopsPerProcess + extraLoops; i++)
                        {
                            float sum = 0.0f;
                            for (int iterations = 0; iterations < ITERATIONS; iterations++)
                                sum += srcArrayA[iterations] * srcArrayB[iterations];
                            dstCPUArray[i] = sum * srcArrayA[i] + (float)sqrt(alpha * srcArrayB[i]);
                        }
                    }
                    finally
                    {
                        threadBarrier.await();
                    }
                }
                catch (BrokenBarrierException ex)
                {
                    if (DEBUG_BUILD) println("Problem with a broken barrier with the N-Core thread with threadId " + threadId + " in linearAlgebraProcessKernel()!:\n" + ex.getMessage());
                }
                catch (InterruptedException ex)
                {
                    // restore the interuption status after catching InterruptedException
                    Thread.currentThread().interrupt();
                    if (DEBUG_BUILD) println("Problem with pausing the N-Core thread with threadId " + threadId + " in linearAlgebraProcessKernel()!:\n" + ex.getMessage());
                }
            }


        };
    }

    /**
    *  Print up to 'max' entries of the given array.
    *
    *  @param results The array containing the result
    *  @param max The maximum number of entries to print
    */
    private void printArrayResults(float[] results, int max)
    {
        print("Result: ");
        max = min(results.length, max);
        for (int i = 0; i < max; i++)
        {
            print(results[i]);
            if (i < max - 1)
                print(", ");
            else if (results.length > max)
                print(" ...");
        }
        println("\n");
    }

    /**
    *  Print up to 'max' entries of the given array.
    *
    *  @param results The array containing the result
    *  @param max The maximum number of entries to print
    */
    private void printArrayResults(FloatBuffer results, int max)
    {
        print("Result: ");
        max = min(results.capacity(), max);
        for (int i = 0; i < max; i++)
        {
            print( results.get(i) );
            if (i < max - 1)
                print(", ");
            else if (results.capacity() > max)
                print(" ...");
        }
        println("\n");
    }


}