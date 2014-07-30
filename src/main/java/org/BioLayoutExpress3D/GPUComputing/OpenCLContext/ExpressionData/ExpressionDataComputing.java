package org.BioLayoutExpress3D.GPUComputing.OpenCLContext.ExpressionData;

import java.io.*;
import java.nio.*;
import java.text.*;
import java.util.concurrent.*;
import javax.swing.*;
import org.jocl.*;
import org.BioLayoutExpress3D.CoreUI.Dialogs.*;
import org.BioLayoutExpress3D.CPUParallelism.*;
import org.BioLayoutExpress3D.CPUParallelism.Executors.*;
import org.BioLayoutExpress3D.GPUComputing.OpenCLContext.*;
import org.BioLayoutExpress3D.Expression.*;
import static java.lang.Math.*;
import static org.jocl.CL.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* ExpressionDataComputing is the main OpenCL context component for Expression Data Computing.
*
* @author Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*
*/

public class ExpressionDataComputing extends OpenCLContext
{

    private static final int MAX_INDEX_NUMBER_FOR_INT_INDICES = 65535;
    private static final int NUMBER_OF_COLUMNS_TO_USE_LOCAL_CACHE_FOR_AMD_ATI = 150;
    private static final int MIN_COMPUTE_UNITS_FOR_FMA_FOR_AMD_ATI = 20;
    private static final int MIN_COMPUTE_UNITS_FOR_FMA_FOR_NVIDIA = 40;
    private static final boolean USE_VECTOR8_TRANSFERS_IN_VECTORSUMS = true;
    private static final boolean USE_VECTOR16_TRANSFERS_IN_VECTORSUMS = true;

    private int N = 0;
    private int localWorkSize = 0;
    private boolean usePairIndices = false;

    private Pointer dataResultsGPUPointer = null;
    private Pointer indexXYPointer = null;
    private Pointer dataSumX_cachePointer = null;
    private Pointer dataSumX_sumX2_cachePointer = null;
    private Pointer dataSumColumns_X2_cachePointer = null;
    private Pointer dataExpressionPointer = null;

    private float[] dataResultsCPU = null;
    private FloatBuffer dataResultsGPU = null;
    private IntBuffer indexXY = null;
    private FloatBuffer dataSumX_cacheBuffer = null;
    private float[] dataSumX_cacheArray = null;
    private FloatBuffer dataSumX_sumX2_cacheBuffer = null;
    private float[] dataSumX_sumX2_cacheArray = null;
    private FloatBuffer dataSumColumns_X2_cacheBuffer = null;
    private float[] dataSumColumns_X2_cacheArray = null;
    private FloatBuffer dataExpressionBuffer = null;
    private float[] dataExpressionArray = null;

    private ExpressionData expressionData = null;
    private LayoutProgressBarDialog layoutProgressBarDialog = null;
    private NumberFormat nf1 = null;
    private NumberFormat nf2 = null;
    private NumberFormat nf3 = null;
    private String[] rowIDsArray = null;
    private float threshold = 0.0f;
    private ObjectOutputStream outOstream = null;
    private PrintWriter outPrintWriter = null;

    private int totalRows = 0;
    private int totalColumns = 0;
    private int numberOfIterations = 0;
    private int lastIndexX = 0;
    private int lastIndexY = 1; // has to be one, as for the first iteration, the indexX starts from 1!
    private int lastIndexXPrevious = 0;
    private int lastIndexYPrevious = 0;
    private int lastIndexXPreviousWriting = 0;
    private int lastIndexYPreviousWriting = 0;
    private boolean firstLoopFirstInit = true;

    private boolean singleCoreOrNCPComparisonMethod = false;
    private boolean javaOrNativeComparisonMethod = false;
    private boolean benchmarkMode = true;
    private boolean compareResults = true;
    private boolean showDifferentResultsOnly = false;
    private boolean reportIterationsInConsole = false;
    private boolean showResults = false;

    private double errorThreshold = 0.0;
    private boolean errorThresholdExceeded = false;

    // variables needed for ExpressionDataComputing Task Parallelism between OpenCL Data Parallelism on the GPU & CPU results saving
    private final CyclicBarrierTimer taskParallelismCyclicBarrierTimer = (USE_MULTICORE_PROCESS) ? new CyclicBarrierTimer() : null;
    private final CyclicBarrier taskParallelismThreadBarrier = (USE_MULTICORE_PROCESS) ? new CyclicBarrier(2, taskParallelismCyclicBarrierTimer) : null;

    // variables needed for N-CP
    private final CyclicBarrierTimer ncpCyclicBarrierTimer = (USE_MULTICORE_PROCESS) ? new CyclicBarrierTimer() : null;
    private final CyclicBarrier ncpThreadBarrier = (USE_MULTICORE_PROCESS) ? new CyclicBarrier(NUMBER_OF_AVAILABLE_PROCESSORS + 1, ncpCyclicBarrierTimer) : null;

    /**
    *  The first constructor of the ExpressionDataComputing class.
    */
    public ExpressionDataComputing(JFrame jFrame)
    {
        super(jFrame);
    }

    /**
    *  The second constructor of the ExpressionDataComputing class.
    */
    public ExpressionDataComputing(JFrame jFrame, boolean dialogErrorLog)
    {
        super(jFrame, dialogErrorLog);
    }

    /**
    *  The third constructor of the ExpressionDataComputing class.
    */
    public ExpressionDataComputing(JFrame jFrame, boolean dialogErrorLog, boolean profileCommandQueue)
    {
        super(jFrame, dialogErrorLog, profileCommandQueue);
    }

    /**
    *  The fourth constructor of the ExpressionDataComputing class.
    */
    public ExpressionDataComputing(JFrame jFrame, boolean dialogErrorLog, boolean profileCommandQueue, boolean openCLSupportAndExtensionsLogOnly)
    {
        super(jFrame, dialogErrorLog, profileCommandQueue, openCLSupportAndExtensionsLogOnly);
    }

    /**
    *  Initializes all expression data computing variables.
    */
    public void initializeExpressionDataComputingVariables(ExpressionData expressionData, LayoutProgressBarDialog layoutProgressBarDialog, NumberFormat nf1, NumberFormat nf2, NumberFormat nf3, int totalRows, int totalColumns, String[] rowIDsArray, FloatBuffer dataSumX_cacheBuffer, FloatBuffer dataSumX_sumX2_cacheBuffer, FloatBuffer dataSumColumns_X2_cacheBuffer, FloatBuffer dataExpressionBuffer, float threshold, ObjectOutputStream outOstream, PrintWriter outPrintWriter, double errorThreshold)
    {
        this.expressionData = expressionData;
        this.layoutProgressBarDialog = layoutProgressBarDialog;
        this.nf1 = nf1;
        this.nf2 = nf2;
        this.nf3 = nf3;
        this.totalRows = totalRows;
        this.totalColumns = totalColumns;
        this.rowIDsArray = rowIDsArray;
        this.dataSumX_cacheBuffer = dataSumX_cacheBuffer;
        this.dataSumX_cacheArray = dataSumX_cacheBuffer.array();
        this.dataSumX_sumX2_cacheBuffer = dataSumX_sumX2_cacheBuffer;
        this.dataSumX_sumX2_cacheArray = dataSumX_sumX2_cacheBuffer.array();
        this.dataSumColumns_X2_cacheBuffer = dataSumColumns_X2_cacheBuffer;
        this.dataSumColumns_X2_cacheArray = dataSumColumns_X2_cacheBuffer.array();
        this.dataExpressionBuffer = dataExpressionBuffer;
        this.dataExpressionArray = dataExpressionBuffer.array();
        this.threshold = threshold;
        this.outOstream = outOstream;
        this.outPrintWriter = outPrintWriter;
        this.errorThreshold = errorThreshold;

        N = OPENCL_GPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION_ITERATION_SIZE.get();
        long totalExpressionDataCalculationsNeeded = org.BioLayoutExpress3D.StaticLibraries.Math.totalTriangularMatrixCalculationsNeeded( (long)totalRows );
        numberOfIterations = (int)( totalExpressionDataCalculationsNeeded / N + ( ( (totalExpressionDataCalculationsNeeded % N) != 0 ) ? 1 : 0 ) );
        benchmarkMode = compareResults = showDifferentResultsOnly = reportIterationsInConsole = COMPARE_GPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION_WITH_CPU.get();
        singleCoreOrNCPComparisonMethod = COMPARE_GPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION_WITH_CPU_DEFAULT_COMPARISON_METHOD.get().contains(SINGLE_CORE_STRING);
        javaOrNativeComparisonMethod = COMPARE_GPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION_WITH_CPU_DEFAULT_COMPARISON_METHOD.get().contains(JAVA_STRING);
    }

    /**
    *  Creates all data arrays and pointers to data arrays.
    */
    private void createDataArraysAndPointers()
    {
        // create data vectors (use FloatBuffer.allocate(capacity) instead of BufferUtil.newFloatBuffer(capacity) for less Java CPU memory consumption)
        dataResultsGPU = FloatBuffer.allocate(N);
        if (compareResults || showResults)
            dataResultsCPU = new float[N];
        indexXY = IntBuffer.allocate( (!usePairIndices) ? N : 2 * N );

        dataResultsGPUPointer = Pointer.to(dataResultsGPU);
        indexXYPointer = Pointer.to(indexXY);
        dataSumX_cachePointer = Pointer.to(dataSumX_cacheBuffer);
        dataSumX_sumX2_cachePointer = Pointer.to(dataSumX_sumX2_cacheBuffer);
        dataSumColumns_X2_cachePointer = Pointer.to(dataSumColumns_X2_cacheBuffer);
        dataExpressionPointer = Pointer.to(dataExpressionBuffer);
    }

    /**
    *  Fills all index data arrays.
    */
    private void fillIndicesDataArray()
    {
        // clear data vectors
        indexXY = (IntBuffer)indexXY.clear();

        transformExpressionDataCalculationsFromUpperDiagonalMatrixToSquareMatrixInSteps();

        indexXY.rewind();
    }

    /**
    *  Transforms the expression data calculations from an upper diagonal matrix to a square matrix in steps.
    */
    private void transformExpressionDataCalculationsFromUpperDiagonalMatrixToSquareMatrixInSteps()
    {
        lastIndexXPrevious = lastIndexX;
        lastIndexYPrevious = lastIndexY;

        int index = 0;
        boolean exitLoops = false;
        boolean secondLoopFirstInit = true;
        for (int i = lastIndexX; i < totalRows - 1; i++) // last row does not perform any calculations, thus skipped
        {
            for (int j = (secondLoopFirstInit) ? lastIndexY : (i + 1); j < totalRows; j++)
            {
                if (secondLoopFirstInit)
                    secondLoopFirstInit = false;

                if ( exitLoops = (++index > N) )
                {
                    lastIndexX = i;
                    lastIndexY = j;

                    break;
                }
                else
                {
                    if (!usePairIndices)
                        indexXY.put( (i << 16) + j );
                    else
                    {
                        indexXY.put(i);
                        indexXY.put(j);
                    }
                }
            }

            if (exitLoops)
                break;
        }

        if (DEBUG_BUILD && reportIterationsInConsole) println("Last index: " + (index - 1) + ", exitLoops needed: " + ( (exitLoops) ? "yes" : "no" ) +
                                                              "\nData indices not used: " + indexXY.remaining() + ", lastIndexX: " + lastIndexX + ", lastIndexY: " + lastIndexY + "\n");
    }

    /**
    *  Creates all permament VRAM arrays.
    */
    private void createPermanentVRAMArrays()
    {
        // Allocate the memory objects for the input- and output data
        memoryObjects = new cl_mem[6]; // create here the mem objects for all arrays, including the indices array ones which will explicitly send new data per iteration
        memoryObjects[0] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_int * ( (!usePairIndices) ? N : 2 * N ), indexXYPointer, null);
        memoryObjects[1] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * totalRows, dataSumX_cachePointer, null);
        memoryObjects[2] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * totalRows, dataSumX_sumX2_cachePointer, null);
        memoryObjects[3] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * totalRows, dataSumColumns_X2_cachePointer, null);
        memoryObjects[4] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * totalRows * totalColumns, dataExpressionPointer, null);
        memoryObjects[5] = clCreateBuffer(context, CL_MEM_WRITE_ONLY, Sizeof.cl_float * N, null, null);
    }

    /**
    *  Initializes the Expression data OpenCL kernel and set its arguments.
    */
    private void initializeKernelAndArguments()
    {
        localWorkSize = CL_IS_PLATFORM_AMD_ATI[0] ? CL_ALL_PLATFORM_DEVICES_MAX_WORK_ITEM_SIZES[0][deviceIndex][0] : CL_ALL_PLATFORM_DEVICES_MAX_WORK_ITEM_SIZES[0][deviceIndex][0] / 2;
        if (localWorkSize == 0)
            throw new CLException("\nWork Item Size of 0 detected!\nOpenCL GPU Computing now aborting.");
        usePairIndices = (totalRows > MAX_INDEX_NUMBER_FOR_INT_INDICES);
        boolean useLocalCache = ( !CL_IS_PLATFORM_AMD_ATI[0] || (totalColumns > NUMBER_OF_COLUMNS_TO_USE_LOCAL_CACHE_FOR_AMD_ATI) ) && ( (localWorkSize & 15) == 0 ); // localWorkSize has to be a multiple of 16 for the localCache to work ok with Int6
        boolean useFMAFunctionInsteadOfMADFunction = false;

        int vectorSize = 0;
        int localIndices = 0;
        if (useLocalCache)
        {
            vectorSize = (totalColumns / localWorkSize);
            if (vectorSize == 0)
                vectorSize++;
            vectorSize = org.BioLayoutExpress3D.StaticLibraries.Math.getNextPowerOfTwo(vectorSize);
            localIndices = (totalColumns / vectorSize) + ( ( (totalColumns % vectorSize) > 0 ) ? 1 : 0 );

            // make sure the local cache used per work group is not more than total local cache / 2
            // also the localIndices should not be more than the local work size
            // and the vectorSize is up to 16
            if (    (4 * localIndices * vectorSize) > (CL_ALL_PLATFORM_DEVICES_LOCAL_MEM_SIZES[0][deviceIndex] / 2)
                 || (localIndices > localWorkSize)
                 || (vectorSize > 16) )
                useLocalCache = false;

            if (DEBUG_BUILD)
            {
                println("VectorSize: " + vectorSize);
                println("LocalIndices: " + localIndices);
                println("Local Cache Size: " + CL_ALL_PLATFORM_DEVICES_LOCAL_MEM_SIZES[0][deviceIndex]);
                println("UseLocalCache: " + useLocalCache);
            }
        }

        if (     ( CL_IS_PLATFORM_AMD_ATI[0] && CL_ALL_PLATFORM_DEVICES_MAX_COMPUTE_UNITS[0][deviceIndex] >= MIN_COMPUTE_UNITS_FOR_FMA_FOR_AMD_ATI)
             || ( !CL_IS_PLATFORM_AMD_ATI[0] && CL_ALL_PLATFORM_DEVICES_MAX_COMPUTE_UNITS[0][deviceIndex] >= MIN_COMPUTE_UNITS_FOR_FMA_FOR_NVIDIA) )
            for (int i = 0; i < CL_ALL_PLATFORM_DEVICES_SINGLE_FP_CONFIGS[0][deviceIndex].length; i++)
                if ( CL_ALL_PLATFORM_DEVICES_SINGLE_FP_CONFIGS[0][deviceIndex][i].equals("CL_FP_FMA") )
                    useFMAFunctionInsteadOfMADFunction = true;

        if (DEBUG_BUILD)
            println("useFMAFunctionInsteadOfMADFunction: " + useFMAFunctionInsteadOfMADFunction);

        // init kernels runtime
        programs = new cl_program[1];
        kernels  = new cl_kernel[1];
        String openCLPreprocessorCommands = "#define USE_PAIR_INDICES " + ( (usePairIndices) ? 1 : 0 ) + "\n" +
                                            "#define USE_LOCAL_CACHE_FOR_FIRSTROW " + ( (useLocalCache) ? 1 : 0 ) + "\n" +
                                            ( (useLocalCache) ? "#define __USE_VECTOR" + vectorSize + "_COPY__" + "\n" : "" ) +
                                            ( (useFMAFunctionInsteadOfMADFunction) ? "#define FP_FAST_FMAF FP_FAST_FMA" + "\n" : "" ) +
                                            ( (useFMAFunctionInsteadOfMADFunction) ? "#define USE_MAD_OR_FMA_FUNCTION(x, y, z) fma((x), (y), (z))" + "\n" : "#define USE_MAD_OR_FMA_FUNCTION(x, y, z) mad((x), (y), (z))" + "\n" ) +
                                            "#define USE_VECTOR8_TRANSFERS_IN_VECTORSUMS " + ( (USE_VECTOR8_TRANSFERS_IN_VECTORSUMS) ? 1 : 0 ) + "\n" +
                                            "#define USE_VECTOR16_TRANSFERS_IN_VECTORSUMS " + ( (USE_VECTOR16_TRANSFERS_IN_VECTORSUMS) ? 1 : 0 ) + "\n";
        KernelUtils.loadKernelFileCreateProgramAndKernel(context, "ExpressionData", "Expressiondata", "calculateCorrelation", LOAD_KERNEL_PROGRAMS_FROM_EXTERNAL_SOURCE, programs, kernels, 0, openCLPreprocessorCommands, "-cl-mad-enable");

        // Set the arguments for the kernel
        clSetKernelArg(kernels[0], 0, Sizeof.cl_mem, Pointer.to(memoryObjects[0]));
        clSetKernelArg(kernels[0], 1, Sizeof.cl_mem, Pointer.to(memoryObjects[1]));
        clSetKernelArg(kernels[0], 2, Sizeof.cl_mem, Pointer.to(memoryObjects[2]));
        clSetKernelArg(kernels[0], 3, Sizeof.cl_mem, Pointer.to(memoryObjects[3]));
        clSetKernelArg(kernels[0], 4, Sizeof.cl_mem, Pointer.to(memoryObjects[4]));
        clSetKernelArg(kernels[0], 5, Sizeof.cl_mem, Pointer.to(memoryObjects[5]));
        clSetKernelArg(kernels[0], 6, Sizeof.cl_int, Pointer.to(new int[]{ totalColumns }));
        clSetKernelArg(kernels[0], 7, Sizeof.cl_int, Pointer.to(new int[]{ totalColumns >> 2 })); // divide by 4
        clSetKernelArg(kernels[0], 8, Sizeof.cl_int, Pointer.to(new int[]{ totalColumns & 3 }));  // modulo by 4
        if (useLocalCache)
        {
            clSetKernelArg(kernels[0], 9, Sizeof.cl_float * vectorSize * localIndices, null);
            clSetKernelArg(kernels[0], 10, Sizeof.cl_int, Pointer.to(new int[]{ localIndices }));
            clSetKernelArg(kernels[0], 11, Sizeof.cl_int * localWorkSize, null);
            clSetKernelArg(kernels[0], 12, Sizeof.cl_int, Pointer.to(new int[]{ localWorkSize }));
            clSetKernelArg(kernels[0], 13, Sizeof.cl_int * 1, null);
        }
    }

    /**
    *  Performs the actual calculation.
    */
    private void performComputation()
    {
        try
        {
            layoutProgressBarDialog.prepareProgressBar(numberOfIterations, "Calculating Number Of Iterations: " + numberOfIterations + " (Now Calculating First Iteration)");
            layoutProgressBarDialog.startProgressBar();

            // Set the work-item dimensions
            long[] globalWorkSizes = new long[]{ N };
            long[]  localWorkSizes = new long[]{ localWorkSize };
            if (DEBUG_BUILD)
            {
                println("Global Work Size for this kernel execution: " + N);
                println(" Local Work Size for this kernel execution: " + localWorkSize);
            }

            CLEventExecutionStatistics clEventExecutionStatistics = (benchmarkMode) ? new CLEventExecutionStatistics() : null;

            if (profileCommandQueue)
            {
                writeEvents = new cl_event[1][];
                writeEvents[0] = new cl_event[]{ new cl_event() };

                kernelEvents = new cl_event[1][];
                kernelEvents[0] = new cl_event[]{ new cl_event() };

                readEvents = new cl_event[1][];
                readEvents[0] = new cl_event[]{ new cl_event() };
            }

            double totalTimeGPU = 0.0;
            double totalTimeGPUWithTransfers = 0.0;
            double totalTimeCPU = 0.0;

            if (USE_MULTICORE_PROCESS)
            {
                initializeTaskParallelismForWriteIterationResultsToFile();
                taskParallelismThreadBarrier.await(); // synchronized start of Task Parallelism
            }

            for (int iteration = 0; iteration < numberOfIterations; iteration++)
            {
                fillIndicesDataArray();

                // Write the input indices data
                clEnqueueWriteBuffer(commandQueue, memoryObjects[0], CL_TRUE, 0, Sizeof.cl_int * ( (!usePairIndices) ? N : 2 * N ), indexXYPointer, 0, null, (profileCommandQueue) ? writeEvents[0][0] : null);

                if (profileCommandQueue)
                {
                    // Wait for the the event, i.e. until the results are read
                    clWaitForEvents(1, writeEvents[0]);
                }

                // Execute the 1D OpenCL kernel
                clEnqueueNDRangeKernel(commandQueue, kernels[0], 1, null, globalWorkSizes, localWorkSizes, 0, null, (profileCommandQueue) ? kernelEvents[0][0] : null);

                if (profileCommandQueue)
                {
                    // Wait for the the event, i.e. until the kernel has completed
                    clWaitForEvents(1, kernelEvents[0]);
                }

                if ( USE_MULTICORE_PROCESS && (iteration > 0) ) // barrier point for previous writing to file iteration
                    taskParallelismThreadBarrier.await();

                // Read the output data
                clEnqueueReadBuffer(commandQueue, memoryObjects[5], CL_TRUE, 0, Sizeof.cl_float * N, dataResultsGPUPointer, 0, null, (profileCommandQueue) ? readEvents[0][0] : null);

                if (profileCommandQueue)
                {
                    // Wait for the the event, i.e. until the results are read
                    clWaitForEvents(1, readEvents[0]);
                }

                // Flush running task
                clFlush(commandQueue);

                // Finish running task
                clFinish(commandQueue);

                // done, calc timer
                if (benchmarkMode)
                {
                    // Print the timing information for the event commands
                    clEventExecutionStatistics.clear();
                    clEventExecutionStatistics.addEntry("write indices", writeEvents[0][0]);
                    clEventExecutionStatistics.addEntry("kernel", kernelEvents[0][0]);
                    clEventExecutionStatistics.addEntry("read results", readEvents[0][0]);
                    clEventExecutionStatistics.print();

                    double totalTimeGPUTransfers = ( (double)clEventExecutionStatistics.getDurationTime(writeEvents[0][0]) + (double)clEventExecutionStatistics.getDurationTime(readEvents[0][0]) ) / 1000000000.0; // for secs
                    totalTimeGPU = (double)clEventExecutionStatistics.getDurationTime(kernelEvents[0][0]) / 1000000000.0; // for secs
                    totalTimeGPUWithTransfers = totalTimeGPUTransfers + totalTimeGPU;
                    if (DEBUG_BUILD)
                    {
                        println("\nTotal time taken for GPU calcs: " + totalTimeGPU + " secs.");
                        println("Total time taken for GPU calcs with transfers: " + totalTimeGPUWithTransfers + " secs.");
                        println("Transfers are taking " + nf2.format( 100.0 * (totalTimeGPUTransfers / totalTimeGPUWithTransfers) )  + "% of total GPU process time.");
                    }
                }

                if (compareResults || showResults)
                    totalTimeCPU = compareWithCPU(totalTimeGPU);

                if (!compareResults)
                {
                    layoutProgressBarDialog.setText("Expression Data GPU Calculations Iterations Done: " + (iteration + 1) + "/" + numberOfIterations + " (Expression Data GPU Calculations: " + nf1.format( N * (iteration + 1) ) + ")");
                    if (DEBUG_BUILD && reportIterationsInConsole) println("\nExpression Data GPU Calculations Iterations: " + (iteration + 1) + "/" + numberOfIterations + "\n(Expression Data GPU Calculations: " + nf1.format( N * (iteration + 1) ) + ")");
                }
                else
                {
                    layoutProgressBarDialog.setText("Expression Data GPU Calculations Iterations Done: " + (iteration + 1) + "/" + numberOfIterations + " (GPU Calculations: " + nf1.format( N * (iteration + 1) ) + ", speedup: " + nf2.format(totalTimeCPU / totalTimeGPU) + "x & " + nf2.format(totalTimeCPU / totalTimeGPUWithTransfers) + "x, Errors: " + ( (errorThresholdExceeded) ? "yes" : "no" ) + ")");
                    if (DEBUG_BUILD && reportIterationsInConsole) println("\nExpression Data GPU Calculations Iterations: " + (iteration + 1) + "/" + numberOfIterations + "\n(GPU Calculations: " + nf1.format( N * (iteration + 1) ) + ", speedup: " + nf2.format(totalTimeCPU / totalTimeGPU) + "x & " + nf2.format(totalTimeCPU / totalTimeGPUWithTransfers) + "x, Errors: " + ( (errorThresholdExceeded) ? "yes" : "no" ) + ")");
                }

                lastIndexXPreviousWriting = lastIndexXPrevious;
                lastIndexYPreviousWriting = lastIndexYPrevious;
                if (!USE_MULTICORE_PROCESS)
                    writeIterationResultsToFile();
                else
                    taskParallelismThreadBarrier.await();

                layoutProgressBarDialog.incrementProgress();
            }

            if (USE_MULTICORE_PROCESS)
            {
                taskParallelismThreadBarrier.await(); // synchronized end of Task Parallelism
                if (DEBUG_BUILD) println("\nTotal ExpressionDataComputing Task Parallelism run time: " + (taskParallelismCyclicBarrierTimer.getTime() / 1e6) + " msecs.\n");
            }

            layoutProgressBarDialog.endProgressBar();
            layoutProgressBarDialog.stopProgressBar();
        }
        catch (IOException ioe)
        {
            if (DEBUG_BUILD) println("IOException in ExpressionDataComputing.performComputation()\n" + ioe.getMessage());
            JOptionPane.showMessageDialog(jFrame, "IOException in building the Correlation network with the GPU.\n" + ioe.getMessage(), "Error: IOException in building the Correlation network with the GPU", JOptionPane.ERROR_MESSAGE);
        }
        catch (BrokenBarrierException ex)
        {
            if (DEBUG_BUILD) println("Problem with a broken barrier with the Task Parallelism thread in performComputation()!:\n" + ex.getMessage());
        }
        catch (InterruptedException ex)
        {
            // restore the interuption status after catching InterruptedException
            Thread.currentThread().interrupt();
            if (DEBUG_BUILD) println("Problem with pausing the N-Core thread with the Task Parallelism thread in performComputation()!:\n" + ex.getMessage());
        }
        // finally part performed in the ExpressionData class
    }

    /**
    *  Initializes Task Parallelism for writing iteration results to file.
    */
    private void initializeTaskParallelismForWriteIterationResultsToFile()
    {
        Thread taskParallelThread = new Thread ( new Runnable()
        {

            @Override
            public void run()
            {
                try
                {
                    taskParallelismThreadBarrier.await(); // synchronized start of Task Parallelism
                    for (int iteration = 0; iteration < numberOfIterations; iteration++)
                    {
                        if (iteration > 0) // barrier point for previous writing to file iteration
                            taskParallelismThreadBarrier.await();
                        taskParallelismThreadBarrier.await();
                        writeIterationResultsToFile(); // synchronized end of Task Parallelism
                    }
                    taskParallelismThreadBarrier.await();
                }
                catch (IOException ioe)
                {
                    if (DEBUG_BUILD) println("IOException in ExpressionDataComputing.initializeTaskParallelismForWriteIterationResultsToFile()\n" + ioe.getMessage());
                    JOptionPane.showMessageDialog(jFrame, "IOException in building the Correlation network with the GPU.\n" + ioe.getMessage(), "Error: IOException in building the Correlation network with the GPU", JOptionPane.ERROR_MESSAGE);

                    taskParallelismThreadBarrier.reset();
                }
                catch (BrokenBarrierException ex)
                {
                    if (DEBUG_BUILD) println("Problem with a broken barrier with the Task Parallelism thread in initializeTaskParallelismForWriteIterationResultsToFile()!:\n" + ex.getMessage());
                }
                catch (InterruptedException ex)
                {
                    // restore the interuption status after catching InterruptedException
                    Thread.currentThread().interrupt();
                    if (DEBUG_BUILD) println("Problem with pausing the N-Core thread with the Task Parallelism thread in initializeTaskParallelismForWriteIterationResultsToFile()!:\n" + ex.getMessage());
                }
            }

        }, "initializeTaskParallelismForWriteIterationResultsToFile" );

        taskParallelThread.setPriority(Thread.NORM_PRIORITY);
        taskParallelThread.start();
    }

    /**
    *  Prints out given results array for debugging purposes.
    *  Float array version.
    */
    private void printArrayResults(float[] data)
    {
        for (int i = 0; i < N; i++)
            println(i + ":\t" + data[i]);
    }

    /**
    *  Prints out given results array for debugging purposes.
    *  FloatBuffer version.
    */
    private void printArrayResults(FloatBuffer data)
    {
        for (int i = 0; i < N; i++)
            println( i + ":\t" + data.get(i) );
    }

    /**
    *  Compares results with the CPU.
    */
    private double compareWithCPU(double totalTimeGPU)
    {
        double totalTimeCPU = 0.0;

        // store CPU results
        if (compareResults)
        {
            // calc on CPU
            int[] indexXYArray = indexXY.array();
            if (singleCoreOrNCPComparisonMethod)
            {
                long startTime = 0, endTime = 0;

                startTime = System.nanoTime();
                if (!usePairIndices)
                {
                    int index = 0;
                    for (int i = 0; i < N; i++)
                    {
                        index = indexXYArray[i];
                        dataResultsCPU[i] = expressionData.calculateCorrelation(((index >> 16) & 0xFFFF), (index & 0xFFFF), dataExpressionArray);
                    }
                }
                else // use Java code
                {
                    int index = 0;
                    for (int i = 0; i < N; i++)
                    {
                        index = i + i;
                        dataResultsCPU[i] = expressionData.calculateCorrelation(indexXYArray[index], indexXYArray[index + 1], dataExpressionArray);
                    }
                }
                endTime = System.nanoTime();
                totalTimeCPU = (endTime - startTime) / 1000000000.0; // for secs
            }
            else
            {
                calculateNCPExpressionData(indexXYArray);
                totalTimeCPU = ncpCyclicBarrierTimer.getTime() / 1000000000.0; // for secs
            }

            if (DEBUG_BUILD) println("\nTotal time taken for CPU calcs: " + totalTimeCPU + " secs.");

            // and compare results
            double maxError = Double.NEGATIVE_INFINITY;
            double avgError = 0.0;
            double difference = 0.0;
            for (int i = 0; i < N; i++)
            {
                difference = abs(dataResultsGPU.get(i) - dataResultsCPU[i]);
                if (difference > maxError)
                {
                    maxError = difference;
                    if (DEBUG_BUILD)
                        println( "Difference of " + difference + " between CPU & GPU data:\t" + dataResultsCPU[i] + " & " + dataResultsGPU.get(i) );
                }
                avgError += difference;
            }

            avgError /= (double)N;
            errorThresholdExceeded = (maxError > errorThreshold);

            if (DEBUG_BUILD && showDifferentResultsOnly)
            {
                printf("\nMax Error: \t\t\t%e\n", maxError);
                printf("Avg Error: \t\t\t%e\n", avgError);
            }

            if (DEBUG_BUILD) println("GPU is " + (totalTimeCPU / totalTimeGPU) + " times faster than the CPU for this calculation.");

            if (showResults)
            {
                if (DEBUG_BUILD)
                {
                    println("\nCPU RESULTS:");
                    printArrayResults(dataResultsCPU);
                }
            }
        }

        if (showResults)
        {
            // print out results
            if (DEBUG_BUILD)
            {
                println("\nGPU RESULTS:");
                printArrayResults(dataResultsGPU);
            }
        }

        return totalTimeCPU;
    }

    /**
    *  Calculates the Expression Data algorithm using N-CP.
    */
    private void calculateNCPExpressionData(int[] indexXYArray)
    {
        int totalLoopsPerProcess = N / NUMBER_OF_AVAILABLE_PROCESSORS;
        LoggerThreadPoolExecutor executor = new LoggerThreadPoolExecutor(NUMBER_OF_AVAILABLE_PROCESSORS, NUMBER_OF_AVAILABLE_PROCESSORS, 0L, TimeUnit.MILLISECONDS,
                                                                         new LinkedBlockingQueue<Runnable>(NUMBER_OF_AVAILABLE_PROCESSORS),
                                                                         new LoggerThreadFactory("ExpressionData"),
                                                                         new ThreadPoolExecutor.CallerRunsPolicy() );

        ncpCyclicBarrierTimer.clear();
        for (int threadId = 0; threadId < NUMBER_OF_AVAILABLE_PROCESSORS; threadId++)
            executor.execute( expressionDataProcessKernel(threadId, totalLoopsPerProcess, indexXYArray) );

        try
        {
            ncpThreadBarrier.await(); // wait for all threads to be ready
            ncpThreadBarrier.await(); // wait for all threads to finish
            executor.shutdown();
        }
        catch (BrokenBarrierException ex)
        {
            if (DEBUG_BUILD) println("Problem with a broken barrier with the main ExpressionData thread in calculateNCPExpressionData()!:\n" + ex.getMessage());
        }
        catch (InterruptedException ex)
        {
            // restore the interuption status after catching InterruptedException
            Thread.currentThread().interrupt();
            if (DEBUG_BUILD) println("Problem with pausing the main ExpressionData thread in calculateNCPExpressionData()!:\n" + ex.getMessage());
        }
    }

    /**
    *   Return a light-weight runnable using the Adapter technique for the Expression Data algorithm so as to avoid any load latencies.
    *   The coding style simulates an OpenCL/CUDA kernel.
    */
    private Runnable expressionDataProcessKernel(final int threadId, final int totalLoopsPerProcess, final int[] indexXYArray)
    {
        return new Runnable()
        {

            @Override
            public void run()
            {
                try
                {
                    ncpThreadBarrier.await();
                    try
                    {
                        int extraLoops = (threadId == (NUMBER_OF_AVAILABLE_PROCESSORS - 1)) ? (N % NUMBER_OF_AVAILABLE_PROCESSORS) : 0;
                        int index = 0;
                        if (!usePairIndices)
                        {
                            for (int i = threadId * totalLoopsPerProcess; i < (threadId + 1) * totalLoopsPerProcess + extraLoops; i++)
                            {
                                index = indexXYArray[i];
                                dataResultsCPU[i] = expressionData.calculateCorrelation(((index >> 16) & 0xFFFF), (index & 0xFFFF), dataExpressionArray);
                            }
                        }
                        else
                        {
                            for (int i = threadId * totalLoopsPerProcess; i < (threadId + 1) * totalLoopsPerProcess + extraLoops; i++)
                            {
                                index = i + i;
                                dataResultsCPU[i] = expressionData.calculateCorrelation(indexXYArray[index], indexXYArray[index + 1], dataExpressionArray);
                            }
                        }
                    }
                    finally
                    {
                        ncpThreadBarrier.await();
                    }
                }
                catch (BrokenBarrierException ex)
                {
                    if (DEBUG_BUILD) println("Problem with a broken barrier with the N-Core thread with threadId " + threadId + " in expressionDataProcessKernel()!:\n" + ex.getMessage());
                }
                catch (InterruptedException ex)
                {
                    // restore the interuption status after catching InterruptedException
                    Thread.currentThread().interrupt();
                    if (DEBUG_BUILD) println("Problem with pausing the N-Core thread with threadId " + threadId + " in expressionDataProcessKernel()!:\n" + ex.getMessage());
                }
            }


        };
    }

    /**
    *  Writes iteration results to a binary file.
    */
    private void writeIterationResultsToFile() throws IOException
    {
        int index = 0;
        float correlation = 0.0f;
        boolean exitLoops = false;
        boolean secondLoopFirstInit = true;
        for (int i = lastIndexXPreviousWriting; i < totalRows - 1; i++) // last row does not perform any calculations, thus skipped
        {
            if (firstLoopFirstInit)
            {
                firstLoopFirstInit = false;
                outOstream.writeInt(i);
            }

            for (int j = (secondLoopFirstInit) ? lastIndexYPreviousWriting : (i + 1); j < totalRows; j++)
            {
                if (secondLoopFirstInit)
                    secondLoopFirstInit = false;

                if ( exitLoops = (++index > N) )
                {
                    break;
                }
                else
                {
                    correlation = dataResultsGPU.get(index - 1); // since the index is already incremented by (++index > N)
                    if (correlation >= threshold)
                    {
                        outOstream.writeInt(j);
                        outOstream.writeFloat(correlation);
                    }
                }
            }

            if (exitLoops)
                break;

            firstLoopFirstInit = true;
            outOstream.writeInt(i);
        }
    }

    /**
    *  Deletes all data arrays.
    */
    private void deleteDataArrays()
    {
        dataResultsGPUPointer = null;
        indexXYPointer = null;
        dataSumX_cachePointer = null;
        dataSumX_sumX2_cachePointer = null;
        dataSumColumns_X2_cachePointer = null;
        dataExpressionPointer = null;
        dataResultsGPU.clear();
        dataResultsGPU = null;
        if (compareResults || showResults)
            dataResultsCPU = null;
        indexXY.clear();
        indexXY = null;
        dataSumX_cacheBuffer.clear();
        dataSumX_cacheBuffer = null;
        dataSumX_cacheArray = null;
        dataSumX_sumX2_cacheBuffer.clear();
        dataSumX_sumX2_cacheBuffer = null;
        dataSumColumns_X2_cacheBuffer.clear();
        dataSumColumns_X2_cacheBuffer = null;
        dataExpressionBuffer.clear();
        dataExpressionBuffer = null;
        rowIDsArray = null;
        dataSumX_sumX2_cacheArray = null;
        dataSumColumns_X2_cacheArray = null;
        dataExpressionArray = null;
        outOstream = null;
        outPrintWriter = null;
        nf1 = null;
        nf2 = null;
        nf3 = null;

        System.gc();
    }

    /**
    *  Deletes the OpenCL kernel.
    */
    private void deleteKernel()
    {
        KernelUtils.releaseKernelAndProgram(programs, kernels, 0);
        programs = null;
        kernels = null;
    }

    /**
    *  Deletes all VRAM arrays.
    */
    private void deleteVRAMArrays()
    {
        for (int i = 0; i < memoryObjects.length; i++)
        {
            if (memoryObjects[i] != null)
            {
                clReleaseMemObject(memoryObjects[i]);
                memoryObjects[i] = null;
            }
        }
        memoryObjects = null;
    }

    /**
    *  Initializes CPU memory.
    */
    @Override
    protected void initializeCPUMemoryImplementation() throws CLException, OutOfMemoryError
    {
        createDataArraysAndPointers();
    }

    /**
    *  Initializes GPU memory.
    */
    @Override
    protected void initializeGPUMemory() throws CLException
    {
        createPermanentVRAMArrays();
        initializeKernelAndArguments();
    }

    /**
    *  Performs the GPU Computing calculations.
    */
    @Override
    protected void performGPUComputingCalculations() throws CLException
    {
        performComputation();
    }

    /**
    *  Retrieves GPU results.
    */
    @Override
    protected void retrieveGPUResultsImplementation() throws CLException, OutOfMemoryError
    {
        // compareWithCPU();
    }

    /**
    *  Deletes the OpenCL context for GPU computing.
    */
    @Override
    protected void deleteOpenCLContextForGPUComputing() throws CLException
    {
        deleteDataArrays();
        deleteKernel();
        deleteVRAMArrays();
    }


}