package org.BioLayoutExpress3D.GPUComputing.OpenCLContext.FRLayout;

import java.nio.*;
import java.text.*;
import java.util.concurrent.*;
import javax.swing.*;
import org.jocl.*;
import org.BioLayoutExpress3D.CoreUI.Dialogs.*;
import org.BioLayoutExpress3D.CPUParallelism.*;
import org.BioLayoutExpress3D.CPUParallelism.Executors.*;
import org.BioLayoutExpress3D.GPUComputing.OpenCLContext.*;
import org.BioLayoutExpress3D.Network.*;
import static java.lang.Math.*;
import static org.jocl.CL.*;
import static org.BioLayoutExpress3D.Network.FRLayout.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* FRLayoutComputing is the main OpenCL context component for FR Computing.
*
* @author Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*
*/

public class FRLayoutComputing extends OpenCLContext
{

    private static final int MAX_INDEX_NUMBER_FOR_INT_INDICES_1D_KERNEL = 65535;
    private static final int LOCAL_WORK_ITEM_SIZE_FOR_NON_INDICES_2D_VERSION = 16;

    private int N = 0;
    private int localWorkSize = 0;
    private boolean useIndices1DKernel = false;
    private boolean usePairIndices = false;

    private Pointer indexXYPointer = null;
    private Pointer vertexIndicesMatrixPointer = null;
    private Pointer cachedVertexPointCoordsMatrixPointer = null;
    private Pointer cachedVertexPointCoordsGPUCopyMatrixPointer = null;
    private Pointer cachedVertexConnectionMatrixPointer = null;
    private Pointer cachedVertexConnectionRowSkipSizeValuesMatrixPointer = null;
    private Pointer cachedPseudoVertexMatrixPointer = null;
    private Pointer cachedVertexNormalizedWeightMatrixPointer = null;
    private Pointer displacementMatrixPointer = null;
    private Pointer displacementValuesPointer = null;
    private Pointer cachedVertexNormalizedWeightIndicesToSkipPointer = null;

    private IntBuffer indexXY = null;
    private IntBuffer vertexIndicesMatrixBuffer = null;
    private FloatBuffer cachedVertexPointCoordsMatrixBuffer = null;
    private float[] cachedVertexPointCoordsMatrixArray = null;
    private FloatBuffer cachedVertexPointCoordsGPUCopyMatrixBuffer = null;
    private float[] cachedVertexPointCoordsGPUCopyMatrixArray = null;
    private IntBuffer cachedVertexConnectionMatrixBuffer = null;
    private IntBuffer cachedVertexConnectionRowSkipSizeValuesMatrixBuffer = null;
    private IntBuffer cachedPseudoVertexMatrixBuffer = null;
    private ShortBuffer cachedVertexNormalizedWeightMatrixBuffer = null;
    private FloatBuffer displacementMatrixBuffer = null;
    private IntBuffer displacementValuesBuffer = null;
    private IntBuffer cachedVertexNormalizedWeightIndicesToSkipBuffer = null;

    private FRLayout frLayout = null;
    private LayoutProgressBarDialog layoutProgressBarDialog = null;
    private NumberFormat nf1 = null;
    private NumberFormat nf2 = null;
    private boolean is2DOr3DFRLayout = false;
    private String kernelName1 = "";
    private String kernelName2 = "";
    private int kernel1ArgumentIndex = 0;
    private int kernel2ArgumentIndex = 0;

    private int numberOfVertices = 0;
    private int numberOfVerticesNextPowerOfTwo = 0;
    private int iterations = 0;
    private int lastIterationFRLayoutCalculationsNeeded = 0;
    private int numberOfOpenCLIterations = 0;
    private int lastIndexX = 0;
    private int lastIndexY = 1; // has to be one, as for the first openCLIteration, the indexX starts from 1!

    private boolean singleCoreOrNCPComparisonMethod = false;
    private boolean javaOrNativeComparisonMethod = false;
    private boolean benchmarkMode = true;
    private boolean compareResults = true;
    private boolean showDifferentResultsOnly = false;
    private boolean reportIterationsInConsole = false;
    private boolean showResults = false;

    private double errorThreshold = 0.0;
    private boolean errorThresholdExceeded = false;
    private boolean useEdgeWeights = false;

    // variables needed for N-CP
    private final CyclicBarrierTimer ncpCyclicBarrierTimer = (USE_MULTICORE_PROCESS) ? new CyclicBarrierTimer() : null;
    private final CyclicBarrier ncpThreadBarrier = (USE_MULTICORE_PROCESS) ? new CyclicBarrier(NUMBER_OF_AVAILABLE_PROCESSORS + 1, ncpCyclicBarrierTimer) : null;

    /**
    *  The first constructor of the FRLayoutComputing class.
    */
    public FRLayoutComputing(JFrame jFrame)
    {
        super(jFrame);
    }

    /**
    *  The second constructor of the FRLayoutComputing class.
    */
    public FRLayoutComputing(JFrame jFrame, boolean dialogErrorLog)
    {
        super(jFrame, dialogErrorLog);
    }

    /**
    *  The third constructor of the FRLayoutComputing class.
    */
    public FRLayoutComputing(JFrame jFrame, boolean dialogErrorLog, boolean profileCommandQueue)
    {
        super(jFrame, dialogErrorLog, profileCommandQueue);
    }

    /**
    *  The fourth constructor of the FRLayoutComputing class.
    */
    public FRLayoutComputing(JFrame jFrame, boolean dialogErrorLog, boolean profileCommandQueue, boolean openCLSupportAndExtensionsLogOnly)
    {
        super(jFrame, dialogErrorLog, profileCommandQueue, openCLSupportAndExtensionsLogOnly);
    }

    /**
    *  Initializes all FR layout 2D computing variables.
    */
    public void initializeFRLayoutComputing2DVariables(FRLayout frLayout, LayoutProgressBarDialog layoutProgressBarDialog, NumberFormat nf1, NumberFormat nf2,
                                                       IntBuffer vertexIndicesMatrixBuffer, FloatBuffer cachedVertexPointCoordsMatrixBuffer, IntBuffer cachedVertexConnectionMatrixBuffer, IntBuffer cachedVertexConnectionRowSkipSizeValuesMatrixBuffer,
                                                       ShortBuffer cachedVertexNormalizedWeightMatrixBuffer, FloatBuffer displacementMatrixBuffer, IntBuffer displacementValuesBuffer,
                                                       IntBuffer cachedVertexNormalizedWeightIndicesToSkipBuffer, int numberOfVertices, int iterations, double errorThreshold, boolean useEdgeWeights)
    {
        this.frLayout = frLayout;
        this.layoutProgressBarDialog = layoutProgressBarDialog;
        this.nf1 = nf1;
        this.nf2 = nf2;
        this.numberOfVertices = numberOfVertices;
        this.iterations = iterations;
        this.vertexIndicesMatrixBuffer = vertexIndicesMatrixBuffer;
        this.cachedVertexPointCoordsMatrixBuffer = cachedVertexPointCoordsMatrixBuffer;
        this.cachedVertexPointCoordsMatrixArray = cachedVertexPointCoordsMatrixBuffer.array();
        this.cachedVertexConnectionMatrixBuffer = cachedVertexConnectionMatrixBuffer;
        this.cachedVertexConnectionRowSkipSizeValuesMatrixBuffer = cachedVertexConnectionRowSkipSizeValuesMatrixBuffer;
        this.cachedVertexNormalizedWeightMatrixBuffer = cachedVertexNormalizedWeightMatrixBuffer;
        this.displacementMatrixBuffer = displacementMatrixBuffer;
        this.displacementValuesBuffer = displacementValuesBuffer;
        this.cachedVertexNormalizedWeightIndicesToSkipBuffer = cachedVertexNormalizedWeightIndicesToSkipBuffer;
        this.errorThreshold = errorThreshold;
        this.useEdgeWeights = useEdgeWeights;

        kernel1ArgumentIndex = 0;
        kernel2ArgumentIndex = 0;
        is2DOr3DFRLayout = true;

        numberOfVerticesNextPowerOfTwo = org.BioLayoutExpress3D.StaticLibraries.Math.getNextPowerOfTwo(numberOfVertices);
        if ( useIndices1DKernel = USE_INDICES_1D_KERNEL_WITH_ITERATIONS_FOR_OPENCL_GPU_COMPUTING_LAYOUT_CALCULATION.get() )
        {
            N = OPENCL_GPU_COMPUTING_LAYOUT_CALCULATION_ITERATION_SIZE.get();
            long totalFRLayoutCalculationsNeeded = org.BioLayoutExpress3D.StaticLibraries.Math.totalTriangularMatrixCalculationsNeeded( (long)numberOfVertices );
            lastIterationFRLayoutCalculationsNeeded = (int)(totalFRLayoutCalculationsNeeded % N);
            numberOfOpenCLIterations = (int)( totalFRLayoutCalculationsNeeded / N + ( (lastIterationFRLayoutCalculationsNeeded != 0) ? 1 : 0 ) );
        }
        benchmarkMode = compareResults = showDifferentResultsOnly = reportIterationsInConsole = COMPARE_GPU_COMPUTING_LAYOUT_CALCULATION_WITH_CPU.get();
        singleCoreOrNCPComparisonMethod = COMPARE_GPU_COMPUTING_LAYOUT_CALCULATION_WITH_CPU_DEFAULT_COMPARISON_METHOD.get().contains(SINGLE_CORE_STRING);
        javaOrNativeComparisonMethod = COMPARE_GPU_COMPUTING_LAYOUT_CALCULATION_WITH_CPU_DEFAULT_COMPARISON_METHOD.get().contains(JAVA_STRING);
    }

    /**
    *  Initializes all FR layout 3D computing variables.
    */
    public void initializeFRLayoutComputing3DVariables(FRLayout frLayout, LayoutProgressBarDialog layoutProgressBarDialog, NumberFormat nf1, NumberFormat nf2,
                                                       IntBuffer vertexIndicesMatrixBuffer, FloatBuffer cachedVertexPointCoordsMatrixBuffer, IntBuffer cachedVertexConnectionMatrixBuffer, IntBuffer cachedVertexConnectionRowSkipSizeValuesMatrixBuffer,
                                                       IntBuffer cachedPseudoVertexMatrixBuffer, ShortBuffer cachedVertexNormalizedWeightMatrixBuffer, IntBuffer displacementValuesBuffer,
                                                       IntBuffer cachedVertexNormalizedWeightIndicesToSkipBuffer, int numberOfVertices, int iterations, double errorThreshold, boolean useEdgeWeights)
    {
        this.frLayout = frLayout;
        this.layoutProgressBarDialog = layoutProgressBarDialog;
        this.nf1 = nf1;
        this.nf2 = nf2;
        this.numberOfVertices = numberOfVertices;
        this.iterations = iterations;
        this.vertexIndicesMatrixBuffer = vertexIndicesMatrixBuffer;
        this.cachedVertexPointCoordsMatrixBuffer = cachedVertexPointCoordsMatrixBuffer;
        this.cachedVertexPointCoordsMatrixArray = cachedVertexPointCoordsMatrixBuffer.array();
        this.cachedVertexConnectionMatrixBuffer = cachedVertexConnectionMatrixBuffer;
        this.cachedVertexConnectionRowSkipSizeValuesMatrixBuffer = cachedVertexConnectionRowSkipSizeValuesMatrixBuffer;
        this.cachedPseudoVertexMatrixBuffer = cachedPseudoVertexMatrixBuffer;
        this.cachedVertexNormalizedWeightMatrixBuffer = cachedVertexNormalizedWeightMatrixBuffer;
        this.displacementValuesBuffer = displacementValuesBuffer;
        this.cachedVertexNormalizedWeightIndicesToSkipBuffer = cachedVertexNormalizedWeightIndicesToSkipBuffer;
        this.errorThreshold = errorThreshold;
        this.useEdgeWeights = useEdgeWeights;

        kernel1ArgumentIndex = 0;
        kernel2ArgumentIndex = 0;
        is2DOr3DFRLayout = false;

        numberOfVerticesNextPowerOfTwo = org.BioLayoutExpress3D.StaticLibraries.Math.getNextPowerOfTwo(numberOfVertices);
        if ( useIndices1DKernel = USE_INDICES_1D_KERNEL_WITH_ITERATIONS_FOR_OPENCL_GPU_COMPUTING_LAYOUT_CALCULATION.get() )
        {
            N = OPENCL_GPU_COMPUTING_LAYOUT_CALCULATION_ITERATION_SIZE.get();
            long totalFRLayoutCalculationsNeeded = org.BioLayoutExpress3D.StaticLibraries.Math.totalTriangularMatrixCalculationsNeeded( (long)numberOfVertices );
            lastIterationFRLayoutCalculationsNeeded = (int)(totalFRLayoutCalculationsNeeded % N);
            numberOfOpenCLIterations = (int)( totalFRLayoutCalculationsNeeded / N + ( (lastIterationFRLayoutCalculationsNeeded != 0) ? 1 : 0 ) );
        }
        benchmarkMode = compareResults = showDifferentResultsOnly = reportIterationsInConsole = COMPARE_GPU_COMPUTING_LAYOUT_CALCULATION_WITH_CPU.get();
        singleCoreOrNCPComparisonMethod = COMPARE_GPU_COMPUTING_LAYOUT_CALCULATION_WITH_CPU_DEFAULT_COMPARISON_METHOD.get().contains(SINGLE_CORE_STRING);
        javaOrNativeComparisonMethod = COMPARE_GPU_COMPUTING_LAYOUT_CALCULATION_WITH_CPU_DEFAULT_COMPARISON_METHOD.get().contains(JAVA_STRING);
    }

    /**
    *  Creates all data arrays and pointers to data arrays.
    */
    private void createDataArraysAndPointers()
    {
        // create data vectors (use FloatBuffer.allocate(capacity) instead of BufferUtil.newFloatBuffer(capacity) for less Java CPU memory consumption)
        if (useIndices1DKernel)
        {
            indexXY = IntBuffer.allocate( (!usePairIndices) ? N : 2 * N );
            indexXYPointer = Pointer.to(indexXY);
        }
        vertexIndicesMatrixPointer = Pointer.to(vertexIndicesMatrixBuffer);
        displacementValuesPointer = Pointer.to(displacementValuesBuffer);
        if (is2DOr3DFRLayout)
            displacementMatrixPointer = Pointer.to(displacementMatrixBuffer);
        else
            cachedPseudoVertexMatrixPointer = Pointer.to(cachedPseudoVertexMatrixBuffer);
        if (compareResults || showResults)
        {
            cachedVertexPointCoordsGPUCopyMatrixBuffer = FloatBuffer.allocate( cachedVertexPointCoordsMatrixBuffer.capacity() );
            cachedVertexPointCoordsGPUCopyMatrixBuffer.put(cachedVertexPointCoordsMatrixBuffer);
            cachedVertexPointCoordsGPUCopyMatrixBuffer.rewind();
            cachedVertexPointCoordsGPUCopyMatrixArray = cachedVertexPointCoordsGPUCopyMatrixBuffer.array();
            cachedVertexPointCoordsGPUCopyMatrixPointer = Pointer.to(cachedVertexPointCoordsGPUCopyMatrixBuffer);
        }
        else
            cachedVertexPointCoordsMatrixPointer = Pointer.to(cachedVertexPointCoordsMatrixBuffer);
        cachedVertexConnectionMatrixPointer = Pointer.to(cachedVertexConnectionMatrixBuffer);
        cachedVertexConnectionRowSkipSizeValuesMatrixPointer = Pointer.to(cachedVertexConnectionRowSkipSizeValuesMatrixBuffer);
        cachedVertexNormalizedWeightMatrixPointer = Pointer.to(cachedVertexNormalizedWeightMatrixBuffer);
        cachedVertexNormalizedWeightIndicesToSkipPointer = Pointer.to(cachedVertexNormalizedWeightIndicesToSkipBuffer);
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
        int index = 0;
        boolean exitLoops = false;
        boolean secondLoopFirstInit = true;
        for (int i = lastIndexX; i < numberOfVertices - 1; i++) // last row does not perform any calculations, thus skipped
        {
            for (int j = (secondLoopFirstInit) ? lastIndexY : (i + 1); j < numberOfVertices; j++)
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
    *  Creates all permanent VRAM arrays.
    */
    private void createPermanentVRAMArrays()
    {
        // Allocate the memory objects for the input- and output data
        memoryObjects = new cl_mem[9]; // create here the mem objects for all arrays, including the indices array ones which will explicitly send new data per openCLIteration
         if (useIndices1DKernel)
            memoryObjects[0] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_int * ( (!usePairIndices) ? N : 2 * N ), indexXYPointer, null);
        memoryObjects[1] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_int * vertexIndicesMatrixBuffer.capacity(), vertexIndicesMatrixPointer, null);
        if (compareResults || showResults)
            memoryObjects[2] = clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * cachedVertexPointCoordsGPUCopyMatrixBuffer.capacity(), cachedVertexPointCoordsGPUCopyMatrixPointer, null);
        else
            memoryObjects[2] = clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * cachedVertexPointCoordsMatrixBuffer.capacity(), cachedVertexPointCoordsMatrixPointer, null);
        memoryObjects[3] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_int * cachedVertexConnectionMatrixBuffer.capacity(), cachedVertexConnectionMatrixPointer, null);
        memoryObjects[4] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_int * cachedVertexConnectionRowSkipSizeValuesMatrixBuffer.capacity(), cachedVertexConnectionRowSkipSizeValuesMatrixPointer, null);
        if (is2DOr3DFRLayout)
            memoryObjects[5] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * displacementMatrixBuffer.capacity(), displacementMatrixPointer, null);
        else
            memoryObjects[5] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_int * cachedPseudoVertexMatrixBuffer.capacity(), cachedPseudoVertexMatrixPointer, null);
        memoryObjects[6] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_short * cachedVertexNormalizedWeightMatrixBuffer.capacity(), cachedVertexNormalizedWeightMatrixPointer, null);
        memoryObjects[7] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_int * cachedVertexNormalizedWeightIndicesToSkipBuffer.capacity(), cachedVertexNormalizedWeightIndicesToSkipPointer, null);
        memoryObjects[8] = clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, Sizeof.cl_int * displacementValuesBuffer.capacity(), displacementValuesPointer, null);
    }

    /**
    *  Initializes the Expression data OpenCL kernel and set its arguments.
    */
    private void initializeKernelAndArguments()
    {
        localWorkSize = CL_IS_PLATFORM_AMD_ATI[0] ? CL_ALL_PLATFORM_DEVICES_MAX_WORK_ITEM_SIZES[0][deviceIndex][0] : CL_ALL_PLATFORM_DEVICES_MAX_WORK_ITEM_SIZES[0][deviceIndex][0] / 2;
        if (localWorkSize == 0)
            throw new CLException("\nWork Item Size of 0 detected!\nOpenCL GPU Computing now aborting.");
        usePairIndices = (numberOfVertices > MAX_INDEX_NUMBER_FOR_INT_INDICES_1D_KERNEL);

        // init the two kernels runtime
        programs = new cl_program[2];
        kernels  = new cl_kernel[2];
        kernelName1 = "calcBiDirForce" + ( (is2DOr3DFRLayout) ? "2D" : "3D" ) + "Kernel" + ( (useIndices1DKernel) ? "1D" : "2D" );
        kernelName2 = "set" + ( (is2DOr3DFRLayout) ? "2D" : "3D" ) + "ForceToVertex";
        String kernelFileName = "Frlayout" + ( (is2DOr3DFRLayout) ? "2d" : "3d" );
        String openCLPreprocessorCommandsKernel1 = ( (useIndices1DKernel) ? "#define USE_PAIR_INDICES " + ( (usePairIndices) ? 1 : 0 ) + "\n"  : "") +
                                                   "#define WEIGHTED_EDGES " + ( (useEdgeWeights) ? 1 : 0 ) + "\n" +
                                                   "#define USE_ATOMICS " + ( ( USE_ATOMIC_SYNCHRONIZATION_FOR_LAYOUT_N_CORE_PARALLELISM.get() ) ? 1 : 0 ) + "\n";
        String openCLPreprocessorCommandsKernel2 = "";
        KernelUtils.loadKernelFileCreateProgramAndKernel(context, "FRLayout", kernelFileName, kernelName1, LOAD_KERNEL_PROGRAMS_FROM_EXTERNAL_SOURCE, programs, kernels, 0, openCLPreprocessorCommandsKernel1, "-cl-mad-enable");
        KernelUtils.loadKernelFileCreateProgramAndKernel(context, "FRLayout", kernelFileName, kernelName2, LOAD_KERNEL_PROGRAMS_FROM_EXTERNAL_SOURCE, programs, kernels, 1, openCLPreprocessorCommandsKernel2, "-cl-mad-enable");

        // Set the arguments for the kernel 1
        if (useIndices1DKernel)
            clSetKernelArg(kernels[0], kernel1ArgumentIndex++, Sizeof.cl_mem, Pointer.to(memoryObjects[0]));
        clSetKernelArg(kernels[0], kernel1ArgumentIndex++, Sizeof.cl_mem, Pointer.to(memoryObjects[1]));
        clSetKernelArg(kernels[0], kernel1ArgumentIndex++, Sizeof.cl_mem, Pointer.to(memoryObjects[2]));
        clSetKernelArg(kernels[0], kernel1ArgumentIndex++, Sizeof.cl_mem, Pointer.to(memoryObjects[3]));
        clSetKernelArg(kernels[0], kernel1ArgumentIndex++, Sizeof.cl_mem, Pointer.to(memoryObjects[4]));
        clSetKernelArg(kernels[0], kernel1ArgumentIndex++, Sizeof.cl_mem, Pointer.to(memoryObjects[5]));
        clSetKernelArg(kernels[0], kernel1ArgumentIndex++, Sizeof.cl_mem, Pointer.to(memoryObjects[6]));
        clSetKernelArg(kernels[0], kernel1ArgumentIndex++, Sizeof.cl_mem, Pointer.to(memoryObjects[7]));
        clSetKernelArg(kernels[0], kernel1ArgumentIndex++, Sizeof.cl_mem, Pointer.to(memoryObjects[8]));
        if (is2DOr3DFRLayout)
        {
            clSetKernelArg(kernels[0], kernel1ArgumentIndex++, Sizeof.cl_int, Pointer.to(new int[]{ frLayout.getDisplacementMatrixDimensionality() }));
            clSetKernelArg(kernels[0], kernel1ArgumentIndex++, Sizeof.cl_float, Pointer.to(new float[]{ frLayout.getKDoubled() }));
        }
        else
        {
            clSetKernelArg(kernels[0], kernel1ArgumentIndex++, Sizeof.cl_float, Pointer.to(new float[]{ frLayout.getKDoubled() }));
            clSetKernelArg(kernels[0], kernel1ArgumentIndex++, Sizeof.cl_float, Pointer.to(new float[]{ frLayout.getKValue() }));
            clSetKernelArg(kernels[0], kernel1ArgumentIndex++, Sizeof.cl_float, Pointer.to(new float[]{ frLayout.getKSquareValue() }));
        }
        clSetKernelArg(kernels[0], kernel1ArgumentIndex++, Sizeof.cl_int, Pointer.to(new int[]{ BOOLEAN_PACKED_DATA_POWER_OF_TWO_VALUE }));
        clSetKernelArg(kernels[0], kernel1ArgumentIndex++, Sizeof.cl_int, Pointer.to(new int[]{ BOOLEAN_PACKED_DATA_BIT_SIZE }));
        if (useIndices1DKernel)
            clSetKernelArg(kernels[0], kernel1ArgumentIndex++, Sizeof.cl_int, Pointer.to(new int[]{ 0 }));
        else
            clSetKernelArg(kernels[0], kernel1ArgumentIndex++, Sizeof.cl_int, Pointer.to(new int[]{ numberOfVertices }));

        // Set the arguments for the kernel 2
        clSetKernelArg(kernels[1], kernel2ArgumentIndex++, Sizeof.cl_mem, Pointer.to(memoryObjects[1]));
        clSetKernelArg(kernels[1], kernel2ArgumentIndex++, Sizeof.cl_mem, Pointer.to(memoryObjects[2]));
        clSetKernelArg(kernels[1], kernel2ArgumentIndex++, Sizeof.cl_mem, Pointer.to(memoryObjects[8]));
        clSetKernelArg(kernels[1], kernel2ArgumentIndex++, Sizeof.cl_float, Pointer.to(new float[]{ frLayout.getTemperature() }));
        clSetKernelArg(kernels[1], kernel2ArgumentIndex++, Sizeof.cl_int, Pointer.to(new int[]{ numberOfVertices }));
        clSetKernelArg(kernels[1], kernel2ArgumentIndex++, Sizeof.cl_int, Pointer.to(new int[]{ frLayout.getCanvasXSize() }));
        clSetKernelArg(kernels[1], kernel2ArgumentIndex++, Sizeof.cl_int, Pointer.to(new int[]{ frLayout.getCanvasYSize() }));
        if (!is2DOr3DFRLayout)
            clSetKernelArg(kernels[1], kernel2ArgumentIndex++, Sizeof.cl_int, Pointer.to(new int[]{ frLayout.getCanvasZSize() }));
    }

    /**
    *  Performs the actual calculation.
    */
    private void performComputation()
    {
        if (layoutProgressBarDialog != null)
        {
            layoutProgressBarDialog.prepareProgressBar(iterations, "Calculating Number Of Iterations: " + iterations + " (Now Calculating First Iteration)");
            layoutProgressBarDialog.startProgressBar();
        }

        // Set the work-item dimensions for kernel 1
        long[] globalWorkSizesKernel1 = (useIndices1DKernel) ? new long[]{ N } : new long[]{ numberOfVerticesNextPowerOfTwo, numberOfVerticesNextPowerOfTwo };
        long[]  localWorkSizesKernel1 = (useIndices1DKernel) ? new long[]{ localWorkSize } : new long[]{ LOCAL_WORK_ITEM_SIZE_FOR_NON_INDICES_2D_VERSION, LOCAL_WORK_ITEM_SIZE_FOR_NON_INDICES_2D_VERSION };
        if (DEBUG_BUILD)
        {
            if (useIndices1DKernel)
            {
                println("Global Work Size for kernel 1 execution: " + N);
                println(" Local Work Size for kernel 1 execution: " + localWorkSize);
            }
            else
            {
                println("Global Work Size for kernel 1 execution: " + numberOfVerticesNextPowerOfTwo + " x " + numberOfVerticesNextPowerOfTwo);
                println(" Local Work Size for kernel 1 execution: " + LOCAL_WORK_ITEM_SIZE_FOR_NON_INDICES_2D_VERSION + " x " + LOCAL_WORK_ITEM_SIZE_FOR_NON_INDICES_2D_VERSION);
            }
        }

        // Set the work-item dimensions for kernel 2
        long[] globalWorkSizesKernel2 = new long[]{ numberOfVerticesNextPowerOfTwo };
        long[]  localWorkSizesKernel2 = new long[]{ localWorkSize };
        if (DEBUG_BUILD)
        {
            println("Global Work Size for kernel 2 execution: " + numberOfVerticesNextPowerOfTwo);
            println(" Local Work Size for kernel 2 execution: " + localWorkSize);
        }

        CLEventExecutionStatistics clEventExecutionStatistics = (benchmarkMode) ? new CLEventExecutionStatistics() : null;

        if (profileCommandQueue)
        {
            if (useIndices1DKernel)
            {
                writeEvents = new cl_event[1][];
                writeEvents[0] = new cl_event[]{ new cl_event() };
            }

            kernelEvents = new cl_event[2][];
            kernelEvents[0] = new cl_event[]{ new cl_event() };
            kernelEvents[1] = new cl_event[]{ new cl_event() };

            readEvents = new cl_event[1][];
            readEvents[0] = new cl_event[]{ new cl_event() };
        }

        double totalTimeGPUKernel1 = 0.0;
        double totalTimeGPUKernel1Sum = 0.0;
        double totalTimeGPUKernel2 = 0.0;
        double totalTimeGPUKernel1WithTransfers = 0.0;
        double totalTimeGPUKernel1WithTransfersSum = 0.0;
        double totalTimeCPU = 0.0;
        double totalTimeGPU = 0.0;

        for (int layoutIteration = 0; layoutIteration < iterations; layoutIteration++)
        {
            totalTimeGPUKernel1Sum = 0.0;
            totalTimeGPUKernel1WithTransfersSum = 0.0;

            if (useIndices1DKernel)
            {
                lastIndexX = 0;
                lastIndexY = 1; // has to be one, as for the first openCLIteration, the indexX starts from 1!

                if (layoutIteration > 0) // already initialized for first iteration!
                    clSetKernelArg(kernels[0], kernel1ArgumentIndex - 1, Sizeof.cl_int, Pointer.to(new int[]{ 0 }));

                for (int openCLIteration = 0; openCLIteration < numberOfOpenCLIterations; openCLIteration++)
                {
                    fillIndicesDataArray();

                    // Write the input indices data
                    clEnqueueWriteBuffer(commandQueue, memoryObjects[0], CL_TRUE, 0, Sizeof.cl_int * ( (!usePairIndices) ? N : 2 * N ), indexXYPointer, 0, null, (profileCommandQueue) ? writeEvents[0][0] : null);

                    if (profileCommandQueue)
                    {
                        // Wait for the the event, i.e. until the results are read
                        clWaitForEvents(1, writeEvents[0]);
                    }

                    if ( openCLIteration == (numberOfOpenCLIterations - 1) ) // last OpenCL iteration
                        clSetKernelArg(kernels[0], kernel1ArgumentIndex - 1, Sizeof.cl_int, Pointer.to(new int[]{ lastIterationFRLayoutCalculationsNeeded }));

                    // Execute the 1D OpenCL kernel
                    clEnqueueNDRangeKernel(commandQueue, kernels[0], 1, null, globalWorkSizesKernel1, localWorkSizesKernel1, 0, null, (profileCommandQueue) ? kernelEvents[0][0] : null);

                    if (profileCommandQueue)
                    {
                        // Wait for the the event, i.e. until the kernel has completed
                        clWaitForEvents(1, kernelEvents[0]);
                    }

                    // done, calc timer
                    if (benchmarkMode)
                    {
                        // Print the timing information for the event commands
                        clEventExecutionStatistics.clear();
                        clEventExecutionStatistics.addEntry("write indices", writeEvents[0][0]);
                        clEventExecutionStatistics.addEntry("kernel 1 (the '" + kernelName1 + "' kernel)", kernelEvents[0][0]);
                        clEventExecutionStatistics.print();

                        double totalTimeGPUTransfers = (double)clEventExecutionStatistics.getDurationTime(writeEvents[0][0]) / 1000000000.0; // for secs
                        totalTimeGPUKernel1 = (double)clEventExecutionStatistics.getDurationTime(kernelEvents[0][0]) / 1000000000.0; // for secs
                        totalTimeGPUKernel1WithTransfers = totalTimeGPUTransfers + totalTimeGPUKernel1;
                        totalTimeGPUKernel1Sum += totalTimeGPUKernel1;
                        totalTimeGPUKernel1WithTransfersSum += totalTimeGPUKernel1WithTransfers;
                        if (DEBUG_BUILD)
                        {
                            println("\nTotal time taken for GPU calcs kernel 1: " + totalTimeGPUKernel1 + " secs.");
                            println("Total time taken for GPU calcs with transfers: " + totalTimeGPUKernel1WithTransfersSum + " secs.");
                            println("Transfers are taking " + nf2.format( 100.0 * (totalTimeGPUTransfers / totalTimeGPUKernel1WithTransfersSum) )  + "% of total GPU process time.\n");
                        }
                    }
                }
            }
            else
            {
                // Execute the OpenCL kernel
                clEnqueueNDRangeKernel(commandQueue, kernels[0], 2, null, globalWorkSizesKernel1, localWorkSizesKernel1, 0, null, (profileCommandQueue) ? kernelEvents[0][0] : null);

                if (profileCommandQueue)
                {
                    // Wait for the the event, i.e. until the kernel has completed
                    clWaitForEvents(1, kernelEvents[0]);
                }
            }

            // execute the second kernel to read/write data directly in GPU memory for every iteration
            clSetKernelArg(kernels[1], 3, Sizeof.cl_float, Pointer.to(new float[]{ frLayout.getTemperature() }));

            // Execute the 2D OpenCL kernel
            clEnqueueNDRangeKernel(commandQueue, kernels[1], 1, null, globalWorkSizesKernel2, localWorkSizesKernel2, 0, null, (profileCommandQueue) ? kernelEvents[1][0] : null);

            if (profileCommandQueue)
            {
                // Wait for the the event, i.e. until the kernel has completed
                clWaitForEvents(1, kernelEvents[1]);
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
                if (!useIndices1DKernel) clEventExecutionStatistics.addEntry("kernel 1 (the '" + kernelName1 + "' kernel)", kernelEvents[0][0]);
                clEventExecutionStatistics.addEntry("kernel 2 (the '" + kernelName2 + "' kernel)", kernelEvents[1][0]);
                if (!useIndices1DKernel)
                    clEventExecutionStatistics.printReverseOrder();
                else
                    clEventExecutionStatistics.print();

                if (!useIndices1DKernel) totalTimeGPUKernel1 = (double)clEventExecutionStatistics.getDurationTime(kernelEvents[0][0]) / 1000000000.0; // for secs
                totalTimeGPUKernel2 = (double)clEventExecutionStatistics.getDurationTime(kernelEvents[1][0]) / 1000000000.0; // for secs
                if (DEBUG_BUILD)
                {
                    if (!useIndices1DKernel) println("\nTotal time taken for GPU calcs kernel 1: " + totalTimeGPUKernel1 + " secs.");
                    println("Total time taken for GPU calcs kernel 2: " + totalTimeGPUKernel2 + " secs.");
                }
            }

            if (compareResults || showResults)
            {
                // Read the output data
                clEnqueueReadBuffer(commandQueue, memoryObjects[2], CL_TRUE, 0, Sizeof.cl_float * cachedVertexPointCoordsGPUCopyMatrixBuffer.capacity(), cachedVertexPointCoordsGPUCopyMatrixPointer, 0, null, (profileCommandQueue) ? readEvents[0][0] : null);

                if (profileCommandQueue)
                {
                    // Wait for the the event, i.e. until the results are read
                    clWaitForEvents(1, readEvents[0]);
                }

                // Flush running task
                clFlush(commandQueue);

                // Finish running task
                clFinish(commandQueue);

                totalTimeGPU = (!useIndices1DKernel) ? totalTimeGPUKernel1 + totalTimeGPUKernel2
                                                     : totalTimeGPUKernel1WithTransfersSum + totalTimeGPUKernel2;
                totalTimeCPU = compareWithCPU(totalTimeGPU);
            }

            if (!useIndices1DKernel)
            {
                if (!compareResults)
                {
                    if (layoutProgressBarDialog != null) layoutProgressBarDialog.setText("FR " + ( (is2DOr3DFRLayout) ? "2D" : "3D" ) + " Layout Iteration " + (layoutIteration + 1) + " OpenCL GPU Calculations 2D Kernel Done.");
                    if (DEBUG_BUILD && reportIterationsInConsole) println("\nFR " + ( (is2DOr3DFRLayout) ? "2D" : "3D" ) + " Layout Iteration " + (layoutIteration + 1) + " OpenCL GPU Calculations 2D Kernel Done.");
                }
                else
                {
                    if (layoutProgressBarDialog != null) layoutProgressBarDialog.setText("FR " + ( (is2DOr3DFRLayout) ? "2D" : "3D" ) + " Layout Iteration " + (layoutIteration + 1) + " OpenCL GPU Calculations 2D Kernel Done: (Speedup: " + nf2.format(totalTimeCPU / totalTimeGPU) + "x, Errors: " + ( (errorThresholdExceeded) ? "yes" : "no" ) + ")");
                    if (DEBUG_BUILD && reportIterationsInConsole) println("\nFR " + ( (is2DOr3DFRLayout) ? "2D" : "3D" ) + " Layout Iteration " + (layoutIteration + 1) + " OpenCL GPU Calculations 2D Kernel Done: (Speedup: " + nf2.format(totalTimeCPU / totalTimeGPU) + "x, Errors: " + ( (errorThresholdExceeded) ? "yes" : "no" ) + ")");
                }
            }
            else
            {
                if (!compareResults)
                {
                    if (layoutProgressBarDialog != null) layoutProgressBarDialog.setText("FR " + ( (is2DOr3DFRLayout) ? "2D" : "3D" ) + " Layout Iteration " + (layoutIteration + 1) + " OpenCL GPU Calculations 1D Kernel Done: (GPU Calculations: " + nf1.format(N * numberOfOpenCLIterations) + ")");
                    if (DEBUG_BUILD && reportIterationsInConsole) println("\nFR " + ( (is2DOr3DFRLayout) ? "2D" : "3D" ) + " Layout Iteration " + (layoutIteration + 1) + " OpenCL GPU Calculations 1D Kernel Done:\n(GPU Calculations: " + nf1.format(N * numberOfOpenCLIterations) + ")");
                }
                else
                {
                    if (layoutProgressBarDialog != null) layoutProgressBarDialog.setText("FR " + ( (is2DOr3DFRLayout) ? "2D" : "3D" ) + " Layout Iteration " + (layoutIteration + 1) + " OpenCL GPU Calculations 1D Kernel Done: (GPU Calculations: " + nf1.format(N * numberOfOpenCLIterations) + ", speedup: " + nf2.format( totalTimeCPU / (totalTimeGPUKernel1Sum + totalTimeGPUKernel2) ) + "x & " + nf2.format(totalTimeCPU / totalTimeGPU) + "x, Errors: " + ( (errorThresholdExceeded) ? "yes" : "no" ) + ")");
                    if (DEBUG_BUILD && reportIterationsInConsole) println("\nFR " + ( (is2DOr3DFRLayout) ? "2D" : "3D" ) + " Layout Iteration " + (layoutIteration + 1) + " OpenCL GPU Calculations 1D Kernel Done:\n(GPU Calculations: " + nf1.format(N * numberOfOpenCLIterations) + ", speedup: " + nf2.format( totalTimeCPU / (totalTimeGPUKernel1Sum + totalTimeGPUKernel2) ) + "x & " + nf2.format(totalTimeCPU / totalTimeGPU) + "x, Errors: " + ( (errorThresholdExceeded) ? "yes" : "no" ) + ")");
                }
            }

            frLayout.temperatureHandling();
            if (layoutProgressBarDialog != null) layoutProgressBarDialog.incrementProgress();
        }

        // Read the output data
        if (compareResults || showResults)
        {
            clEnqueueReadBuffer(commandQueue, memoryObjects[2], CL_TRUE, 0, Sizeof.cl_float * cachedVertexPointCoordsGPUCopyMatrixBuffer.capacity(), cachedVertexPointCoordsGPUCopyMatrixPointer, 0, null, (profileCommandQueue) ? readEvents[0][0] : null);

            // copy all GPU results coord data to CPU side
            cachedVertexPointCoordsMatrixBuffer.clear();
            cachedVertexPointCoordsMatrixBuffer.put(cachedVertexPointCoordsGPUCopyMatrixBuffer);
            cachedVertexPointCoordsMatrixBuffer.rewind();
        }
        else
            clEnqueueReadBuffer(commandQueue, memoryObjects[2], CL_TRUE, 0, Sizeof.cl_float * cachedVertexPointCoordsMatrixBuffer.capacity(), cachedVertexPointCoordsMatrixPointer, 0, null, (profileCommandQueue) ? readEvents[0][0] : null);

        if (profileCommandQueue)
        {
            // Wait for the the event, i.e. until the results are read
            clWaitForEvents(1, readEvents[0]);
        }

        // Flush running task
        clFlush(commandQueue);

        // Finish running task
        clFinish(commandQueue);

        if (benchmarkMode)
        {
            if (DEBUG_BUILD)
            {
                // Print the timing information for the event command
                clEventExecutionStatistics.clear();
                clEventExecutionStatistics.addEntry("  read", readEvents[0][0]);
                double totalTimeGPUTransfers = (double)clEventExecutionStatistics.getDurationTime(readEvents[0][0]) / 1000000000.0; // for secs
                clEventExecutionStatistics.print();
                println("\nTotal time taken for GPU final coord transfer: " + totalTimeGPUTransfers + " secs.");
            }
        }

        if (layoutProgressBarDialog != null)
        {
            layoutProgressBarDialog.endProgressBar();
            layoutProgressBarDialog.stopProgressBar();
        }
    }

    /**
    *  Prints out given results array for debugging purposes.
    *  Float array version.
    */
    private void printArrayResults(float[] data)
    {
        for (int i = 0; i < data.length; i++)
            println(i + ":\t" + data[i]);
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
            long startTime = 0, endTime = 0;
            // calc on CPU
            if (singleCoreOrNCPComparisonMethod)
            {
                startTime = System.nanoTime();
                frLayout.calculateSingleCoreJavaFRLayout(is2DOr3DFRLayout);
                endTime = System.nanoTime();

                totalTimeCPU = (endTime - startTime) / 1000000000.0; // for secs
            }
            else
            {
                // N-CP for kernel 1
                calculateNCPFRLayout();
                totalTimeCPU = ncpCyclicBarrierTimer.getTime() / 1000000000.0; // for secs

                // serial processing for kernel 2
                startTime = System.nanoTime();
                frLayout.setForceToVertex(is2DOr3DFRLayout);
                endTime = System.nanoTime();
                totalTimeCPU += ( (endTime - startTime) / 1000000000.0 ); // for secs
            }

            if (DEBUG_BUILD) println("Total time taken for CPU calcs: " + totalTimeCPU + " secs.");

            // and compare results
            double maxError = Double.NEGATIVE_INFINITY;
            double avgError = 0.0;
            double difference = 0.0;
            for (int i = 0; i < cachedVertexPointCoordsMatrixArray.length; i++)
            {
                difference = abs(cachedVertexPointCoordsMatrixArray[i] - cachedVertexPointCoordsGPUCopyMatrixArray[i]);
                if (difference > maxError)
                {
                    maxError = difference;
                    if (DEBUG_BUILD)
                        println("Difference of " + difference + " between GPU & GPU data:\t" + cachedVertexPointCoordsMatrixArray[i] + " & " + cachedVertexPointCoordsGPUCopyMatrixArray[i]);
                }
                avgError += difference;
            }

            avgError /= (double)cachedVertexPointCoordsMatrixArray.length;
            errorThresholdExceeded = (maxError > errorThreshold);

            // for (int i = 0; i < cachedVertexPointCoordsMatrixArray.length; i++)
            //     if (cachedVertexPointCoordsMatrixArray[i] != cachedVertexPointCoordsGPUCopyMatrixArray[i])
            //         System.out.println("i: " + i + " with CPU value: " + cachedVertexPointCoordsMatrixArray[i] + " & GPU value: " + cachedVertexPointCoordsGPUCopyMatrixArray[i]);

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
                    printArrayResults(cachedVertexPointCoordsMatrixArray);
                }
            }
        }

        if (showResults)
        {
            // print out results
            if (DEBUG_BUILD)
            {
                println("\nGPU RESULTS:");
                printArrayResults(cachedVertexPointCoordsGPUCopyMatrixArray);
            }
        }

        return totalTimeCPU;
    }

    /**
    *  Calculates the FRLayout algorithm using N-CP.
    */
    private void calculateNCPFRLayout()
    {
        boolean isPowerOfTwo = org.BioLayoutExpress3D.StaticLibraries.Math.isPowerOfTwo(NUMBER_OF_AVAILABLE_PROCESSORS);
        LoggerThreadPoolExecutor executor = new LoggerThreadPoolExecutor(NUMBER_OF_AVAILABLE_PROCESSORS, NUMBER_OF_AVAILABLE_PROCESSORS, 0L, TimeUnit.MILLISECONDS,
                                                                         new LinkedBlockingQueue<Runnable>(NUMBER_OF_AVAILABLE_PROCESSORS),
                                                                         new LoggerThreadFactory("FRLayout"),
                                                                         new ThreadPoolExecutor.CallerRunsPolicy() );

        ncpCyclicBarrierTimer.clear();
        if (is2DOr3DFRLayout)
        {
            for (int threadId = 0; threadId < NUMBER_OF_AVAILABLE_PROCESSORS; threadId++)
                executor.execute( frLayout.frLayout2DProcessKernel(threadId, isPowerOfTwo, javaOrNativeComparisonMethod, ncpThreadBarrier) );
        }
        else
        {
            for (int threadId = 0; threadId < NUMBER_OF_AVAILABLE_PROCESSORS; threadId++)
                executor.execute( frLayout.frLayout3DProcessKernel(threadId, isPowerOfTwo, javaOrNativeComparisonMethod, ncpThreadBarrier) );
        }

        try
        {
            ncpThreadBarrier.await(); // wait for all threads to be ready
            ncpThreadBarrier.await(); // wait for all threads to finish
            executor.shutdown();
        }
        catch (BrokenBarrierException ex)
        {
            if (DEBUG_BUILD) println("Problem with a broken barrier with the main FRLayout thread in calculateNCPExpressionData()!:\n" + ex.getMessage());
        }
        catch (InterruptedException ex)
        {
            // restore the interuption status after catching InterruptedException
            Thread.currentThread().interrupt();
            if (DEBUG_BUILD) println("Problem with pausing the main FRLayout thread in calculateNCPExpressionData()!:\n" + ex.getMessage());
        }
    }

    /**
    *  Deletes all data arrays.
    */
    private void deleteDataArrays()
    {
        indexXYPointer = null;
        vertexIndicesMatrixPointer = null;
        displacementValuesPointer = null;
        displacementMatrixPointer = null;
        cachedPseudoVertexMatrixPointer = null;
        cachedVertexPointCoordsMatrixPointer = null;
        cachedVertexConnectionRowSkipSizeValuesMatrixPointer = null;
        cachedVertexConnectionMatrixPointer = null;
        cachedVertexNormalizedWeightMatrixPointer = null;
        cachedVertexNormalizedWeightIndicesToSkipPointer = null;
        if (useIndices1DKernel)
        {
            indexXY.clear();
            indexXY = null;
        }
        vertexIndicesMatrixBuffer.clear();
        vertexIndicesMatrixBuffer = null;
        displacementValuesBuffer.clear();
        displacementValuesBuffer = null;
        if (is2DOr3DFRLayout)
        {
            displacementMatrixBuffer.clear();
            displacementMatrixBuffer = null;
        }
        else
        {
            cachedPseudoVertexMatrixBuffer.clear();
            cachedPseudoVertexMatrixBuffer = null;
        }
        if (compareResults || showResults)
        {
            cachedVertexPointCoordsGPUCopyMatrixBuffer.clear();
            cachedVertexPointCoordsGPUCopyMatrixBuffer = null;
        }
        cachedVertexPointCoordsMatrixBuffer.clear();
        cachedVertexPointCoordsMatrixBuffer = null;
        cachedVertexPointCoordsMatrixArray = null;
        cachedVertexConnectionMatrixBuffer.clear();
        cachedVertexConnectionMatrixBuffer = null;
        cachedVertexConnectionRowSkipSizeValuesMatrixBuffer.clear();
        cachedVertexConnectionRowSkipSizeValuesMatrixBuffer = null;
        cachedVertexNormalizedWeightMatrixBuffer.clear();
        cachedVertexNormalizedWeightMatrixBuffer = null;
        cachedVertexNormalizedWeightIndicesToSkipBuffer.clear();
        cachedVertexNormalizedWeightIndicesToSkipBuffer = null;
        nf1 = null;
        nf2 = null;

        System.gc();
    }

    /**
    *  Deletes the OpenCL kernel.
    */
    private void deleteKernels()
    {
        KernelUtils.releaseAllKernelsAndPrograms(programs, kernels);
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
        deleteKernels();
        deleteVRAMArrays();
    }


}