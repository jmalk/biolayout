package org.BioLayoutExpress3D.Network;

import java.nio.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.CoreUI.Dialogs.*;
import org.BioLayoutExpress3D.CPUParallelism.*;
import org.BioLayoutExpress3D.CPUParallelism.Executors.*;
import org.BioLayoutExpress3D.GPUComputing.OpenCLContext.FRLayout.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import static java.lang.Math.*;
import static org.BioLayoutExpress3D.StaticLibraries.ArraysAutoBoxUtils.*;
import static org.BioLayoutExpress3D.StaticLibraries.FixedPointMath.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
*  The Fruchterman-Rheingold layout class.
*
*  Loch Tay vacations inspiration used for re-implementation of this code using advanced bitshift techniques for N-CP.
*  Many thanks to Elaine Duncan for bouncing ideas backwards & forwards for this!
*
* @author Full Fruchterman-Rheingold layout algorithm rewrite/JNI/C support/N-Core parallelization code/GPU Computing by Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public final class FRLayout
{

    public static final int BOOLEAN_PACKED_DATA_POWER_OF_TWO_VALUE = 5;
    public static final int BOOLEAN_PACKED_DATA_BIT_SIZE = (1 << BOOLEAN_PACKED_DATA_POWER_OF_TWO_VALUE) - 1; // to be used for bitshift modulo division
    private static final byte FIXED_POINT_DECIMAL_PART_LENGTH = 14; // fixed point normalized weights range 0.0-2.0, 0.0 & 2.0 inclusive thus 2 bit & 14 bit integer-decimal part chosen for fixed point
    private static final float TEMPERATURE_SCALING = 0.95f;

    private int canvasXSize = 0;
    private int canvasYSize = 0;
    private int canvasZSize = 0;
    private int displacementMatrixDimensionality = 0;
    private int currentVertexCount = 0;
    private int numberOfIterations = 0;

    private float temperature = 0.0f;
    private float kValueModifier = 0.0f;
    private float kValue = 0.0f;
    private float kSquareValue = 0.0f;
    private float kDoubled = 0.0f;
    private boolean useEdgeWeights = false;

    private IntBuffer vertexIndicesMatrixBuffer = null;
    private int[] vertexIndicesMatrixArray = null;
    private IntBuffer displacementValuesBuffer = null;
    private int[] displacementValuesArray = null;
    private FloatBuffer displacementMatrixBuffer = null;
    private float[] displacementMatrixArray = null;
    private IntBuffer cachedPseudoVertexMatrixBuffer = null;
    private int[] cachedPseudoVertexMatrixArray = null;
    private FloatBuffer cachedVertexPointCoordsMatrixBuffer = null;
    private float[] cachedVertexPointCoordsMatrixArray = null;
    private IntBuffer cachedVertexConnectionMatrixBuffer = null;
    private int[] cachedVertexConnectionMatrixArray = null;
    private IntBuffer cachedVertexConnectionRowSkipSizeValuesMatrixBuffer = null;
    private int[] cachedVertexConnectionRowSkipSizeValuesMatrixArray = null;
    private ShortBuffer cachedVertexNormalizedWeightMatrixBuffer = null;
    private short[] cachedVertexNormalizedWeightMatrixArray = null;
    private IntBuffer cachedVertexNormalizedWeightIndicesToSkipBuffer = null;
    private int[] cachedVertexNormalizedWeightIndicesToSkipArray = null;

    private Vertex[] vertexArray = null;
    private int numberOfVertices = 0;
    private LayoutFrame layoutFrame = null;
    private LayoutProgressBarDialog layoutProgressBarDialog = null;
    private NumberFormat nf1 = null;
    private NumberFormat nf2 = null;

    // variables needed for N-CP & OpenCL GPU Computing
    private static final int MINIMUM_NUMBER_OF_VERTICES_FOR_NCP_PARALLELIZATION = 1000;
    private static final int MINIMUM_NUMBER_OF_VERTICES_FOR_OPENCL_GPU_COMPUTING_PARALLELIZATION = 1000;
    private final CyclicBarrierTimer cyclicBarrierTimer = (USE_MULTICORE_PROCESS) ? new CyclicBarrierTimer() : null;
    private final CyclicBarrier threadBarrier = (USE_MULTICORE_PROCESS) ? new CyclicBarrier(NUMBER_OF_AVAILABLE_PROCESSORS + 1, cyclicBarrierTimer) : null;
    private volatile AtomicIntegerArray displacementValuesAtomic = null;

    /**
    *  The constructor of the FRLayout class. Initializes all the variables needed for the FRLayout algorithm.
    */
    public FRLayout(float canvasXSize, float canvasYSize, float canvasZSize)
    {
        this.canvasXSize = (int)floor(canvasXSize);
        this.canvasYSize = (int)floor(canvasYSize);
        this.canvasZSize = (int)floor(canvasZSize);

        this.nf1 = NumberFormat.getNumberInstance();
        this.nf1.setMaximumFractionDigits(0);

        this.nf2 = NumberFormat.getNumberInstance();
        this.nf2.setMaximumFractionDigits(2);
    }

    /**
    *  Sets the Kvalue and initializes the data structures.
    */
    public void setKvalue(LayoutFrame layoutFrame, Collection<Vertex> vertices)
    {
        this.layoutFrame = layoutFrame;

        currentVertexCount = 0;
        numberOfVertices = vertices.size();
        displacementMatrixDimensionality = (6000 * canvasXSize + 6 * canvasYSize);
        numberOfIterations = NUMBER_OF_LAYOUT_ITERATIONS.get();
        temperature = STARTING_TEMPERATURE.get();
        kValueModifier = KVALUE_MODIFIER.get();
        kValue = (numberOfVertices > 0) ? kValueModifier * (float)sqrt( (canvasXSize * canvasYSize) / numberOfVertices ) / 2.0f : 0.0f;
        kSquareValue = kValue * kValue;
        kDoubled = 2.0f * kValue;
        useEdgeWeights = WEIGHTED_EDGES && USE_EDGE_WEIGHTS_FOR_LAYOUT.get();

        initAllCachedDataStructures(vertices);
    }

    /**
    *  Initializes all the cached data structures.
    */
    private void initAllCachedDataStructures(Collection<Vertex> vertices)
    {
        vertexArray = new Vertex[numberOfVertices];
        vertices.toArray(vertexArray);
        Arrays.sort(vertexArray);

        long totalCalculationsNeeded = org.BioLayoutExpress3D.StaticLibraries.Math.totalTriangularMatrixCalculationsNeeded( (long)numberOfVertices );
        vertexIndicesMatrixBuffer = IntBuffer.allocate(numberOfVertices);
        cachedVertexConnectionMatrixBuffer = IntBuffer.allocate( (int)(totalCalculationsNeeded >> BOOLEAN_PACKED_DATA_POWER_OF_TWO_VALUE) + 1 ); // Elaine's optimum idea of defining the bitshift array!
        cachedVertexConnectionRowSkipSizeValuesMatrixBuffer = IntBuffer.allocate(numberOfVertices - 1);
        int tempRowSkipSizeValue = (int)totalCalculationsNeeded;
        int from = numberOfVertices;
        while (--from >= 1)
        {
            tempRowSkipSizeValue -= from;
            cachedVertexConnectionRowSkipSizeValuesMatrixBuffer.put(from - 1, tempRowSkipSizeValue);
        }

        if (!RENDERER_MODE_3D)
        {
            int matricesSize = numberOfVertices << 1;
            if ( ( ( USE_MULTICORE_PROCESS && USE_LAYOUT_N_CORE_PARALLELISM.get() ) || ( OPENCL_GPU_COMPUTING_ENABLED && USE_OPENCL_GPU_COMPUTING_LAYOUT_CALCULATION.get() ) ) && USE_ATOMIC_SYNCHRONIZATION_FOR_LAYOUT_N_CORE_PARALLELISM.get() )
                displacementValuesAtomic = new AtomicIntegerArray(matricesSize);
            displacementValuesBuffer = IntBuffer.allocate(matricesSize);
            cachedVertexPointCoordsMatrixBuffer = FloatBuffer.allocate(matricesSize);

            int vertexIDIndex = 0;
            for (Vertex vertex : vertexArray)
            {
                vertexIDIndex = vertex.getVertexID() << 1;
                vertexIndicesMatrixBuffer.put( vertex.getVertexID() );
                cachedVertexPointCoordsMatrixBuffer.put( vertexIDIndex    , vertex.getX() );
                cachedVertexPointCoordsMatrixBuffer.put( vertexIDIndex + 1, vertex.getY() );
            }

            initialize2DCaching();
        }
        else
        {
            int matricesSize = 3 * numberOfVertices;
            if ( ( ( USE_MULTICORE_PROCESS && USE_LAYOUT_N_CORE_PARALLELISM.get() ) || ( OPENCL_GPU_COMPUTING_ENABLED && USE_OPENCL_GPU_COMPUTING_LAYOUT_CALCULATION.get() ) ) && USE_ATOMIC_SYNCHRONIZATION_FOR_LAYOUT_N_CORE_PARALLELISM.get() )
                displacementValuesAtomic = new AtomicIntegerArray(matricesSize);
            displacementValuesBuffer = IntBuffer.allocate(matricesSize);
            cachedVertexPointCoordsMatrixBuffer = FloatBuffer.allocate(matricesSize);
            cachedPseudoVertexMatrixBuffer = IntBuffer.allocate((numberOfVertices >> BOOLEAN_PACKED_DATA_POWER_OF_TWO_VALUE) + 1); // Elaine's optimum idea of defining the bitshift array!

            int vertexIDIndex = 0;
            for (Vertex vertex : vertexArray)
            {
                vertexIDIndex = 3 * vertex.getVertexID();
                vertexIndicesMatrixBuffer.put( vertex.getVertexID() );
                if ( vertex.isPseudoVertex() )
                    cachedPseudoVertexMatrixBuffer.put( vertex.getVertexID() >> BOOLEAN_PACKED_DATA_POWER_OF_TWO_VALUE, cachedPseudoVertexMatrixBuffer.get(vertex.getVertexID() >> BOOLEAN_PACKED_DATA_POWER_OF_TWO_VALUE) | 1 << (vertex.getVertexID() & BOOLEAN_PACKED_DATA_BIT_SIZE) );
                cachedVertexPointCoordsMatrixBuffer.put( vertexIDIndex    , vertex.getX() );
                cachedVertexPointCoordsMatrixBuffer.put( vertexIDIndex + 1, vertex.getY() );
                cachedVertexPointCoordsMatrixBuffer.put( vertexIDIndex + 2, vertex.getZ() );
            }
        }

        int dimensionalityIndex = 0;
        int weightRowIndex = 0;
        ArrayList<Short> cachedVertexNormalizedWeightArrayList = (useEdgeWeights) ? new ArrayList<Short>() : null;
        cachedVertexNormalizedWeightIndicesToSkipBuffer = ( useEdgeWeights && ( ( USE_MULTICORE_PROCESS && USE_LAYOUT_N_CORE_PARALLELISM.get() ) || ( OPENCL_GPU_COMPUTING_ENABLED && USE_OPENCL_GPU_COMPUTING_LAYOUT_CALCULATION.get() ) ) ) ? IntBuffer.allocate(numberOfVertices) : null;
        from = numberOfVertices;
        int to = 0;
        while (--from >= 0)
        // for (int from = 0; from < numberOfVertices; from++)
        {
            weightRowIndex = 0;
            to = numberOfVertices;
            while (--to >= from + 1)
            // for (int to = from + 1; to < numberOfVertices; to++)
            {
                if ( vertexArray[from].getEdgeConnectionsMap().containsKey(vertexArray[to]) )
                {
                    dimensionalityIndex = cachedVertexConnectionRowSkipSizeValuesMatrixBuffer.get(vertexArray[from].getVertexID() - 1) + vertexArray[to].getVertexID();
                    cachedVertexConnectionMatrixBuffer.put( dimensionalityIndex >> BOOLEAN_PACKED_DATA_POWER_OF_TWO_VALUE, cachedVertexConnectionMatrixBuffer.get(dimensionalityIndex >> BOOLEAN_PACKED_DATA_POWER_OF_TWO_VALUE) | 1 << (dimensionalityIndex & BOOLEAN_PACKED_DATA_BIT_SIZE) );
                    if (useEdgeWeights)
                    {
                        cachedVertexNormalizedWeightArrayList.add( convertFromFloatToFixedPointShortNumber(vertexArray[from].getEdgeConnectionsMap().get(vertexArray[to]).getNormalisedWeight(), FIXED_POINT_DECIMAL_PART_LENGTH) );
                        if ( ( USE_MULTICORE_PROCESS && USE_LAYOUT_N_CORE_PARALLELISM.get() ) || ( OPENCL_GPU_COMPUTING_ENABLED && USE_OPENCL_GPU_COMPUTING_LAYOUT_CALCULATION.get() ) )
                            cachedVertexNormalizedWeightIndicesToSkipBuffer.put(from, ++weightRowIndex);
                    }
                }
            }
        }

        if (useEdgeWeights)
        {
            cachedVertexNormalizedWeightMatrixBuffer = ShortBuffer.allocate( cachedVertexNormalizedWeightArrayList.size() );
            cachedVertexNormalizedWeightMatrixBuffer.put( toPrimitiveListShort(cachedVertexNormalizedWeightArrayList) );
        }
        else
        {
            // make sure the OpenCL GPU code & the native code does not crash the JVM with a C side null pointer exception!
            cachedVertexNormalizedWeightMatrixBuffer = ShortBuffer.allocate(1);
            cachedVertexNormalizedWeightMatrixBuffer.put(new short[1]);
            if ( ( USE_MULTICORE_PROCESS && USE_LAYOUT_N_CORE_PARALLELISM.get() ) || ( OPENCL_GPU_COMPUTING_ENABLED && USE_OPENCL_GPU_COMPUTING_LAYOUT_CALCULATION.get() ) )
            {
                cachedVertexNormalizedWeightIndicesToSkipBuffer = IntBuffer.allocate(1);
                cachedVertexNormalizedWeightIndicesToSkipBuffer.put(new int[1]);
            }
        }

        vertexIndicesMatrixArray = vertexIndicesMatrixBuffer.array();
        displacementValuesArray = displacementValuesBuffer.array();
        if (RENDERER_MODE_3D)
            cachedPseudoVertexMatrixArray = cachedPseudoVertexMatrixBuffer.array();
        cachedVertexPointCoordsMatrixArray = cachedVertexPointCoordsMatrixBuffer.array();
        cachedVertexConnectionMatrixArray = cachedVertexConnectionMatrixBuffer.array();
        cachedVertexConnectionRowSkipSizeValuesMatrixArray = cachedVertexConnectionRowSkipSizeValuesMatrixBuffer.array();
        if (useEdgeWeights)
        {
            cachedVertexNormalizedWeightMatrixArray = cachedVertexNormalizedWeightMatrixBuffer.array();
            if ( ( USE_MULTICORE_PROCESS && USE_LAYOUT_N_CORE_PARALLELISM.get() ) || ( OPENCL_GPU_COMPUTING_ENABLED && USE_OPENCL_GPU_COMPUTING_LAYOUT_CALCULATION.get() ) )
                cachedVertexNormalizedWeightIndicesToSkipArray = cachedVertexNormalizedWeightIndicesToSkipBuffer.array();
        }
        cachedVertexNormalizedWeightMatrixArray = cachedVertexNormalizedWeightMatrixBuffer.array();
    }

    /**
    *  Initializes the 2D cached data structures.
    */
    private void initialize2DCaching()
    {
        displacementMatrixBuffer = FloatBuffer.allocate(displacementMatrixDimensionality + 6);

        float distance = 0.0f;
        float squaredDistance = 0.0f;
        float kDistance = 0.0f;
        for (int distX = 1; distX < canvasXSize + 1; distX++)
        {
            for (int distY = 1; distY < canvasYSize + 1; distY++)
            {
                squaredDistance = (distX * distX + distY * distY);
                distance = (float)sqrt(squaredDistance);

                kDistance = kSquareValue / distance;
                if (distance <= kDoubled)
                {
                    float distCalcX = (distX * kSquareValue / squaredDistance);
                    float distCalcY = (distY * kSquareValue / squaredDistance);

                    displacementMatrixBuffer.put(6000 * distX + 6 * distY,     distCalcX);
                    displacementMatrixBuffer.put(6000 * distX + 6 * distY + 1, distCalcY);
                    displacementMatrixBuffer.put(6000 * distX + 6 * distY + 2, distCalcX);
                    displacementMatrixBuffer.put(6000 * distX + 6 * distY + 3, distCalcY);
                    displacementMatrixBuffer.put(6000 * distX + 6 * distY + 4, distCalcX);
                    displacementMatrixBuffer.put(6000 * distX + 6 * distY + 5, distCalcY);
                }
                else
                {
                    displacementMatrixBuffer.put(6000 * distX + 6 * distY    , 0.0f);
                    displacementMatrixBuffer.put(6000 * distX + 6 * distY + 1, 0.0f);
                    displacementMatrixBuffer.put(6000 * distX + 6 * distY + 2, 0.0f);
                    displacementMatrixBuffer.put(6000 * distX + 6 * distY + 3, 0.0f);
                    displacementMatrixBuffer.put(6000 * distX + 6 * distY + 4, 0.0f);
                    displacementMatrixBuffer.put(6000 * distX + 6 * distY + 5, 0.0f);
                }

                kDistance = squaredDistance / kValue;

                displacementMatrixBuffer.put( 6000 * distX + 6 * distY    , displacementMatrixBuffer.get(6000 * distX + 6 * distY    ) - ( (distX / distance) * kDistance ) );
                displacementMatrixBuffer.put( 6000 * distX + 6 * distY + 1, displacementMatrixBuffer.get(6000 * distX + 6 * distY + 1) - ( (distY / distance) * kDistance ) );
            }
        }

        displacementMatrixArray = displacementMatrixBuffer.array();
    }

    /**
    *  Creates the vertex indices matrix.
    */
    public void createVerticesMatrices(Collection<Vertex> vertices)
    {
        numberOfVertices = vertices.size();
        vertexArray = new Vertex[numberOfVertices];
        vertices.toArray(vertexArray);
        Arrays.sort(vertexArray);

        vertexIndicesMatrixBuffer = IntBuffer.allocate(numberOfVertices);
        for (Vertex vertex : vertexArray)
            vertexIndicesMatrixBuffer.put( vertex.getVertexID() );

        int dimensionalityIndex = 0;
        int weightRowIndex = 0;
        ArrayList<Short> cachedVertexNormalizedWeightArrayList = (useEdgeWeights) ? new ArrayList<Short>() : null;
        cachedVertexNormalizedWeightIndicesToSkipBuffer = ( useEdgeWeights && ( ( USE_MULTICORE_PROCESS && USE_LAYOUT_N_CORE_PARALLELISM.get() ) || ( OPENCL_GPU_COMPUTING_ENABLED && USE_OPENCL_GPU_COMPUTING_LAYOUT_CALCULATION.get() ) ) ) ? IntBuffer.allocate(numberOfVertices) : null;
        int from = numberOfVertices;
        int to = 0;
        while (--from >= 0)
        // for (int from = 0; from < numberOfVertices; from++)
        {
            weightRowIndex = 0;
            to = numberOfVertices;
            while (--to >= from + 1)
            // for (int to = from + 1; to < numberOfVertices; to++)
            {
                dimensionalityIndex = cachedVertexConnectionRowSkipSizeValuesMatrixBuffer.get(vertexArray[from].getVertexID() - 1) + vertexArray[to].getVertexID();
                if ( useEdgeWeights && ( ( cachedVertexConnectionMatrixArray[dimensionalityIndex >> BOOLEAN_PACKED_DATA_POWER_OF_TWO_VALUE] >> (dimensionalityIndex & BOOLEAN_PACKED_DATA_BIT_SIZE) ) & 1 ) != 0 )
                {
                    cachedVertexNormalizedWeightArrayList.add( convertFromFloatToFixedPointShortNumber(vertexArray[from].getEdgeConnectionsMap().get(vertexArray[to]).getNormalisedWeight(), FIXED_POINT_DECIMAL_PART_LENGTH) );
                    if ( ( USE_MULTICORE_PROCESS && USE_LAYOUT_N_CORE_PARALLELISM.get() ) || ( OPENCL_GPU_COMPUTING_ENABLED && USE_OPENCL_GPU_COMPUTING_LAYOUT_CALCULATION.get() ) )
                        cachedVertexNormalizedWeightIndicesToSkipBuffer.put(from, ++weightRowIndex);
                }
            }
        }

        if (useEdgeWeights)
        {
            cachedVertexNormalizedWeightMatrixBuffer = ShortBuffer.allocate( cachedVertexNormalizedWeightArrayList.size() );
            cachedVertexNormalizedWeightMatrixBuffer.put( toPrimitiveListShort(cachedVertexNormalizedWeightArrayList) );
        }
        else
        {
            // make sure the OpenCL GPU code & the native code does not crash the JVM with a C side null pointer exception!
            cachedVertexNormalizedWeightMatrixBuffer = ShortBuffer.allocate(1);
            cachedVertexNormalizedWeightMatrixBuffer.put(new short[1]);
            if ( ( USE_MULTICORE_PROCESS && USE_LAYOUT_N_CORE_PARALLELISM.get() ) || ( OPENCL_GPU_COMPUTING_ENABLED && USE_OPENCL_GPU_COMPUTING_LAYOUT_CALCULATION.get() ) )
            {
                cachedVertexNormalizedWeightIndicesToSkipBuffer = IntBuffer.allocate(1);
                cachedVertexNormalizedWeightIndicesToSkipBuffer.put(new int[1]);
            }
        }

        vertexIndicesMatrixArray = vertexIndicesMatrixBuffer.array();
        if (useEdgeWeights)
        {
            cachedVertexNormalizedWeightMatrixArray = cachedVertexNormalizedWeightMatrixBuffer.array();
            if ( ( USE_MULTICORE_PROCESS && USE_LAYOUT_N_CORE_PARALLELISM.get() ) || ( OPENCL_GPU_COMPUTING_ENABLED && USE_OPENCL_GPU_COMPUTING_LAYOUT_CALCULATION.get() ) )
                cachedVertexNormalizedWeightIndicesToSkipArray = cachedVertexNormalizedWeightIndicesToSkipBuffer.array();
        }
    }

    /**
    *  Performs all iterations of the FRLayout algorithm in 2D.
    */
    public void allIterationsCalcBiDirForce2D(int iterations, int componentID, LayoutProgressBarDialog layoutProgressBarDialog)
    {
        this.layoutProgressBarDialog = layoutProgressBarDialog;

        boolean performOpenCLGPUFRLayoutCalculationGetErrorOccured = false;
        if ( OPENCL_GPU_COMPUTING_ENABLED && USE_OPENCL_GPU_COMPUTING_LAYOUT_CALCULATION.get() && (numberOfVertices > MINIMUM_NUMBER_OF_VERTICES_FOR_OPENCL_GPU_COMPUTING_PARALLELIZATION) )
            performOpenCLGPUFRLayoutCalculationGetErrorOccured = performOpenCLGPUFRLayoutCalcBiDirForce2D(iterations);

        if (performOpenCLGPUFRLayoutCalculationGetErrorOccured || !(OPENCL_GPU_COMPUTING_ENABLED && USE_OPENCL_GPU_COMPUTING_LAYOUT_CALCULATION.get()) || (numberOfVertices <= MINIMUM_NUMBER_OF_VERTICES_FOR_OPENCL_GPU_COMPUTING_PARALLELIZATION))
        {
            allIterationsCalcBiDirForce2DJava(iterations, performOpenCLGPUFRLayoutCalculationGetErrorOccured, componentID);
        }
    }

    /**
    *  Main method of the OpenCL GPU FRLayout in 2D data parallel execution code.
    */
    private boolean performOpenCLGPUFRLayoutCalcBiDirForce2D(int iterations)
    {
        FRLayoutComputing frLayoutComputingContext = new FRLayoutComputing( layoutFrame, true, COMPARE_GPU_COMPUTING_LAYOUT_CALCULATION_WITH_CPU.get() );
        frLayoutComputingContext.initializeFRLayoutComputing2DVariables(this, layoutProgressBarDialog, nf1, nf2,
                                                                        vertexIndicesMatrixBuffer, cachedVertexPointCoordsMatrixBuffer, cachedVertexConnectionMatrixBuffer, cachedVertexConnectionRowSkipSizeValuesMatrixBuffer,
                                                                        cachedVertexNormalizedWeightMatrixBuffer, displacementMatrixBuffer, displacementValuesBuffer,
                                                                        cachedVertexNormalizedWeightIndicesToSkipBuffer, numberOfVertices, iterations, LAYOUT_GPU_COMPUTING_MAX_ERROR_THRESHOLD, useEdgeWeights);
        frLayoutComputingContext.startGPUComputingProcessing();

        // CPU fail-safe mechanism if OpenCL GPU Computing fails for some reason
        return frLayoutComputingContext.getErrorOccured();
    }

    /**
    *  Performs all iterations of the FRLayout algorithm in 2D (Java method).
    */
    private void allIterationsCalcBiDirForce2DJava(int iterations, boolean performOpenCLGPUFRLayoutCalculationGetErrorOccured, int componentID)
    {
        int vertexID = 0;
        if ( !( ( USE_MULTICORE_PROCESS && USE_LAYOUT_N_CORE_PARALLELISM.get() ) || ( OPENCL_GPU_COMPUTING_ENABLED && USE_OPENCL_GPU_COMPUTING_LAYOUT_CALCULATION.get() ) ) || (numberOfVertices < MINIMUM_NUMBER_OF_VERTICES_FOR_NCP_PARALLELIZATION) )
        {
            int from = numberOfVertices;
            int to = 0;
            while (--iterations >= 0)
            {
                // done this way so as to emulate copy by reference (pointer) for value of cachedWeightIndex
                int[] cachedVertexNormalizedWeightIndex = (useEdgeWeights) ? new int[1] : null;
                from = numberOfVertices;
                to = 0;
                while (--from >= 0)
                // for (int from = 0; from < numberOfVertices; from++)
                {
                    to = numberOfVertices;
                    while (--to >= from + 1)
                    // for (int to = from + 1; to < numberOfVertices; to++)
                        calcBiDirForce2D(vertexIndicesMatrixArray[from], vertexIndicesMatrixArray[to], cachedVertexNormalizedWeightIndex);
                }

                vertexID = numberOfVertices;
                while (--vertexID >= 0)
                    set2DForceToVertex(vertexIndicesMatrixArray[vertexID]);
                temperatureHandling();
                updateGUI();

                if (layoutProgressBarDialog.userHasCancelled())
                {
                    return;
                }
            }
        }
        else
        {
            if ( performOpenCLGPUFRLayoutCalculationGetErrorOccured && (layoutProgressBarDialog != null) )
            {
                String progressBarParallelismTitle = (USE_MULTICORE_PROCESS) ? "(Utilizing " + NUMBER_OF_AVAILABLE_PROCESSORS + "-Core Parallelism)" : "";
                layoutProgressBarDialog.prepareProgressBar(numberOfIterations,
                        "Now Processing Layout Iterations " + progressBarParallelismTitle +
                        ( (componentID != 0) ? " for Graph Component: " + componentID : "" ) +
                        "   (no utilization of OpenCL GPU Computing)");
            }

            boolean isPowerOfTwo = org.BioLayoutExpress3D.StaticLibraries.Math.isPowerOfTwo(NUMBER_OF_AVAILABLE_PROCESSORS);
            while (--iterations >= 0)
            {
                LoggerThreadPoolExecutor executor = new LoggerThreadPoolExecutor(NUMBER_OF_AVAILABLE_PROCESSORS, NUMBER_OF_AVAILABLE_PROCESSORS, 0L, TimeUnit.MILLISECONDS,
                                                                                 new LinkedBlockingQueue<Runnable>(NUMBER_OF_AVAILABLE_PROCESSORS),
                                                                                 new LoggerThreadFactory("FRLayout2D"),
                                                                                 new ThreadPoolExecutor.CallerRunsPolicy() );

                cyclicBarrierTimer.clear();
                for (int threadId = 0; threadId < NUMBER_OF_AVAILABLE_PROCESSORS; threadId++)
                    executor.execute( frLayout2DProcessKernel(threadId, isPowerOfTwo) );

                try
                {
                    threadBarrier.await(); // wait for all threads to be ready
                    threadBarrier.await(); // wait for all threads to finish
                    executor.shutdown();
                }
                catch (BrokenBarrierException ex)
                {
                    if (DEBUG_BUILD) println("Problem with a broken barrier with the main FRLayout thread in allIterationsCalcBiDirForce2DJava()!:\n" + ex.getMessage());
                }
                catch (InterruptedException ex)
                {
                    // restore the interuption status after catching InterruptedException
                    Thread.currentThread().interrupt();
                    if (DEBUG_BUILD) println("Problem with pausing the main FRLayout thread in allIterationsCalcBiDirForce2DJava()!:\n" + ex.getMessage());
                }

                if (DEBUG_BUILD) println("\nTotal allIterationsCalcBiDirForce2DJava N-CP run time: " + (cyclicBarrierTimer.getTime() / 1e6) + " ms.\n");

                setForceToVertex(true);
                temperatureHandling();
                updateGUI();

                if (layoutProgressBarDialog.userHasCancelled())
                {
                    return;
                }
            }
        }
    }

    /**
    *  Iterates the FRLayout algorithm in 2D.
    */
    public void iterateCalcBiDirForce2D()
    {
        int vertexID = 0;
        if ( !( USE_MULTICORE_PROCESS && USE_LAYOUT_N_CORE_PARALLELISM.get() ) || (numberOfVertices < MINIMUM_NUMBER_OF_VERTICES_FOR_NCP_PARALLELIZATION) )
        {
            // done this way so as to emulate copy by reference (pointer) for value of cachedWeightIndex
            int[] cachedVertexNormalizedWeightIndex = (useEdgeWeights) ? new int[1] : null;
            int from = numberOfVertices;
            int to = 0;
            while (--from >= 0)
            // for (int from = 0; from < numberOfVertices; from++)
            {
                to = numberOfVertices;
                while (--to >= from + 1)
                // for (int to = from + 1; to < numberOfVertices; to++)
                    calcBiDirForce2D(vertexIndicesMatrixArray[from], vertexIndicesMatrixArray[to], cachedVertexNormalizedWeightIndex);
            }

            vertexID = numberOfVertices;
            while (--vertexID >= 0)
                set2DForceToVertex(vertexIndicesMatrixArray[vertexID]);
        }
        else
        {
            boolean isPowerOfTwo = org.BioLayoutExpress3D.StaticLibraries.Math.isPowerOfTwo(NUMBER_OF_AVAILABLE_PROCESSORS);
            LoggerThreadPoolExecutor executor = new LoggerThreadPoolExecutor(NUMBER_OF_AVAILABLE_PROCESSORS, NUMBER_OF_AVAILABLE_PROCESSORS, 0L, TimeUnit.MILLISECONDS,
                                                                             new LinkedBlockingQueue<Runnable>(NUMBER_OF_AVAILABLE_PROCESSORS),
                                                                             new LoggerThreadFactory("FRLayout2D"),
                                                                             new ThreadPoolExecutor.CallerRunsPolicy() );

            cyclicBarrierTimer.clear();
            for (int threadId = 0; threadId < NUMBER_OF_AVAILABLE_PROCESSORS; threadId++)
                executor.execute( frLayout2DProcessKernel(threadId, isPowerOfTwo) );

            try
            {
                threadBarrier.await(); // wait for all threads to be ready
                threadBarrier.await(); // wait for all threads to finish
                executor.shutdown();
            }
            catch (BrokenBarrierException ex)
            {
                if (DEBUG_BUILD) println("Problem with a broken barrier with the main FRLayout thread in iterateCalcBiDirForce2DJava()!:\n" + ex.getMessage());
            }
            catch (InterruptedException ex)
            {
                // restore the interuption status after catching InterruptedException
                Thread.currentThread().interrupt();
                if (DEBUG_BUILD) println("Problem with pausing the main FRLayout thread in iterateCalcBiDirForce2DJava()!:\n" + ex.getMessage());
            }

            if (DEBUG_BUILD) println("\nTotal iterateCalcBiDirForce2DJava N-CP run time: " + (cyclicBarrierTimer.getTime() / 1e6) + " ms.\n");

            setForceToVertex(true);
        }

        temperatureHandling();
    }

    /**
    *   Return a light-weight thread using the Adapter technique for the 2D layout FRLayout algorithm so as to avoid any load latencies.
    *   The coding style simulates an OpenCL/CUDA kernel.
    */
    private Runnable frLayout2DProcessKernel(final int threadId, final boolean isPowerOfTwo)
    {
        return frLayout2DProcessKernel(threadId, isPowerOfTwo, false, threadBarrier);
    }

    /**
    *   Return a light-weight thread using the Adapter technique for the 2D layout FRLayout algorithm so as to avoid any load latencies.
    *   The coding style simulates an OpenCL/CUDA kernel.
    *   Overloaded version that supports optional native method processing.
    */
    public Runnable frLayout2DProcessKernel(final int threadId, final boolean isPowerOfTwo, final boolean javaOrNativeComparisonMethod, final CyclicBarrier threadBarrier)
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
                        // done this way so as to emulate copy by reference (pointer) for value of cachedWeightIndex
                        int[] cachedVertexNormalizedWeightIndex = (useEdgeWeights) ? new int[1] : null;
                        int from = numberOfVertices;
                        int to = 0;
                        if ( USE_ATOMIC_SYNCHRONIZATION_FOR_LAYOUT_N_CORE_PARALLELISM.get() )
                        {
                            if (isPowerOfTwo)
                            {
                                while (--from >= 0)
                                // for (int from = 0; from < numberOfVertices; from++)
                                {
                                    // distribute every (from % NUMBER_OF_AVAILABLE_PROCESSORS) execution to the given threadId
                                    if ( ( from & (NUMBER_OF_AVAILABLE_PROCESSORS - 1) ) == threadId )
                                    {
                                        to = numberOfVertices;
                                        while (--to >= from + 1)
                                        // for (int to = from + 1; to < numberOfVertices; to++)
                                            calcBiDirForce2DAtomic(vertexIndicesMatrixArray[from], vertexIndicesMatrixArray[to], cachedVertexNormalizedWeightIndex);
                                    }
                                    else
                                    {
                                        if (useEdgeWeights)
                                            cachedVertexNormalizedWeightIndex[0] += cachedVertexNormalizedWeightIndicesToSkipArray[from];
                                    }
                                }
                            }
                            else
                            {
                                while (--from >= 0)
                                // for (int from = 0; from < numberOfVertices; from++)
                                {
                                    // distribute every (from % NUMBER_OF_AVAILABLE_PROCESSORS) execution to the given threadId
                                    if ( (from % NUMBER_OF_AVAILABLE_PROCESSORS) == threadId )
                                    {
                                        to = numberOfVertices;
                                        while (--to >= from + 1)
                                        // for (int to = from + 1; to < numberOfVertices; to++)
                                            calcBiDirForce2DAtomic(vertexIndicesMatrixArray[from], vertexIndicesMatrixArray[to], cachedVertexNormalizedWeightIndex);
                                    }
                                    else
                                    {
                                        if (useEdgeWeights)
                                            cachedVertexNormalizedWeightIndex[0] += cachedVertexNormalizedWeightIndicesToSkipArray[from];
                                    }
                                }
                            }
                        }
                        else
                        {
                            if (isPowerOfTwo)
                            {
                                while (--from >= 0)
                                // for (int from = 0; from < numberOfVertices; from++)
                                {
                                    // distribute every (from % NUMBER_OF_AVAILABLE_PROCESSORS) execution to the given threadId
                                    if ((from & (NUMBER_OF_AVAILABLE_PROCESSORS - 1)) == threadId)
                                    {
                                        to = numberOfVertices;
                                        while (--to >= from + 1)
                                        // for (int to = from + 1; to < numberOfVertices; to++)
                                        {
                                            calcBiDirForce2D(vertexIndicesMatrixArray[from], vertexIndicesMatrixArray[to], cachedVertexNormalizedWeightIndex);
                                        }
                                    }
                                    else
                                    {
                                        if (useEdgeWeights)
                                        {
                                            cachedVertexNormalizedWeightIndex[0] += cachedVertexNormalizedWeightIndicesToSkipArray[from];
                                        }
                                    }
                                }
                            }
                            else
                            {
                                while (--from >= 0)
                                // for (int from = 0; from < numberOfVertices; from++)
                                {
                                    // distribute every (from % NUMBER_OF_AVAILABLE_PROCESSORS) execution to the given threadId
                                    if ((from % NUMBER_OF_AVAILABLE_PROCESSORS) == threadId)
                                    {
                                        to = numberOfVertices;
                                        while (--to >= from + 1)
                                        // for (int to = from + 1; to < numberOfVertices; to++)
                                        {
                                            calcBiDirForce2D(vertexIndicesMatrixArray[from], vertexIndicesMatrixArray[to], cachedVertexNormalizedWeightIndex);
                                        }
                                    }
                                    else
                                    {
                                        if (useEdgeWeights)
                                        {
                                            cachedVertexNormalizedWeightIndex[0] += cachedVertexNormalizedWeightIndicesToSkipArray[from];
                                        }
                                    }
                                }
                            }
                        }
                    }
                    finally
                    {
                        threadBarrier.await();
                    }
                }
                catch (BrokenBarrierException ex)
                {
                    if (DEBUG_BUILD) println("Problem with a broken barrier with the N-Core thread with threadId " + threadId + " in frLayout2DProcessKernel()!:\n" + ex.getMessage());
                }
                catch (InterruptedException ex)
                {
                    // restore the interuption status after catching InterruptedException
                    Thread.currentThread().interrupt();
                    if (DEBUG_BUILD) println("Problem with pausing the N-Core thread with threadId " + threadId + " in frLayout2DProcessKernel()!:\n" + ex.getMessage());
                }
            }


        };
    }

    /**
    *  Calculates the 2D bi-directional force of the FRLayout algorithm.
    */
    private void calcBiDirForce2D(int vertexID1, int vertexID2, int[] cachedVertexNormalizedWeightIndex)
    {
        int vertexID1Index0 = vertexID1 << 1;
        int vertexID2Index0 = vertexID2 << 1;
        int vertexID1Index1 = vertexID1Index0 + 1;
        int vertexID2Index1 = vertexID2Index0 + 1;
        int dimensionalityIndex = cachedVertexConnectionRowSkipSizeValuesMatrixArray[vertexID1 - 1] + vertexID2;

        float distX = cachedVertexPointCoordsMatrixArray[vertexID1Index0] - cachedVertexPointCoordsMatrixArray[vertexID2Index0];
        float distY = cachedVertexPointCoordsMatrixArray[vertexID1Index1] - cachedVertexPointCoordsMatrixArray[vertexID2Index1];

        if (distX == 0.0f)
            distX = 1.0f;
        if (distY == 0.0f)
            distY = 1.0f;

        int absDistX = (int)( (distX > 0.0f) ? distX : -distX );
        int absDistY = (int)( (distY > 0.0f) ? distY : -distY );

        int signX = (distX > 0.0f) ? 1 : -1;
        int signY = (distY > 0.0f) ? 1 : -1;

        int distanceCache = (6000 * absDistX + 6 * absDistY);
        if (distanceCache >= displacementMatrixDimensionality)
            distanceCache = displacementMatrixDimensionality;
        int dispCalcX = 0, dispCalcY = 0;

        if ( ( ( cachedVertexConnectionMatrixArray[dimensionalityIndex >> BOOLEAN_PACKED_DATA_POWER_OF_TWO_VALUE] >> (dimensionalityIndex & BOOLEAN_PACKED_DATA_BIT_SIZE) ) & 1 ) != 0 )
        {
            if (useEdgeWeights)
            {
                float weight = convertFromFixedPointShortNumberToUnsignedFloat(cachedVertexNormalizedWeightMatrixArray[cachedVertexNormalizedWeightIndex[0]++], FIXED_POINT_DECIMAL_PART_LENGTH);
                dispCalcX = (int)( ( ( (displacementMatrixArray[distanceCache    ] - displacementMatrixArray[distanceCache + 4]) * weight ) + displacementMatrixArray[distanceCache + 4] ) * signX );
                dispCalcY = (int)( ( ( (displacementMatrixArray[distanceCache + 1] - displacementMatrixArray[distanceCache + 5]) * weight ) + displacementMatrixArray[distanceCache + 5] ) * signY );
            }
            else
            {
                dispCalcX = (int)(displacementMatrixArray[distanceCache    ] * signX);
                dispCalcY = (int)(displacementMatrixArray[distanceCache + 1] * signY);
            }

            displacementValuesArray[vertexID1Index0] += dispCalcX;
            displacementValuesArray[vertexID1Index1] += dispCalcY;

            displacementValuesArray[vertexID2Index0] -= dispCalcX;
            displacementValuesArray[vertexID2Index1] -= dispCalcY;
        }
        else
        {
            if ( !( (absDistX > kDoubled) && (absDistY > kDoubled) ) )
            {
                dispCalcX = (int)(displacementMatrixArray[distanceCache + 2] * signX);
                dispCalcY = (int)(displacementMatrixArray[distanceCache + 3] * signY);

                displacementValuesArray[vertexID1Index0] += dispCalcX;
                displacementValuesArray[vertexID1Index1] += dispCalcY;

                displacementValuesArray[vertexID2Index0] -= dispCalcX;
                displacementValuesArray[vertexID2Index1] -= dispCalcY;
            }
        }
    }

    /**
    *  Calculates the 2D bi-directional force of the FRLayout algorithm.
    *  Atomic version.
    */
    private void calcBiDirForce2DAtomic(int vertexID1, int vertexID2, int[] cachedVertexNormalizedWeightIndex)
    {
        int vertexID1Index0 = vertexID1 << 1;
        int vertexID2Index0 = vertexID2 << 1;
        int vertexID1Index1 = vertexID1Index0 + 1;
        int vertexID2Index1 = vertexID2Index0 + 1;
        int dimensionalityIndex = cachedVertexConnectionRowSkipSizeValuesMatrixArray[vertexID1 - 1] + vertexID2;

        float distX = cachedVertexPointCoordsMatrixArray[vertexID1Index0] - cachedVertexPointCoordsMatrixArray[vertexID2Index0];
        float distY = cachedVertexPointCoordsMatrixArray[vertexID1Index1] - cachedVertexPointCoordsMatrixArray[vertexID2Index1];

        if (distX == 0.0f)
            distX = 1.0f;
        if (distY == 0.0f)
            distY = 1.0f;

        int absDistX = (int)( (distX > 0.0f) ? distX : -distX );
        int absDistY = (int)( (distY > 0.0f) ? distY : -distY );

        int signX = (distX > 0.0f) ? 1 : -1;
        int signY = (distY > 0.0f) ? 1 : -1;

        int distanceCache = (6000 * absDistX + 6 * absDistY);
        if (distanceCache >= displacementMatrixDimensionality)
            distanceCache = displacementMatrixDimensionality;
        int dispCalcX = 0, dispCalcY = 0;

        if ( ( ( cachedVertexConnectionMatrixArray[dimensionalityIndex >> BOOLEAN_PACKED_DATA_POWER_OF_TWO_VALUE] >> (dimensionalityIndex & BOOLEAN_PACKED_DATA_BIT_SIZE) ) & 1 ) != 0 )
        {
            if (useEdgeWeights)
            {
                float weight = convertFromFixedPointShortNumberToUnsignedFloat(cachedVertexNormalizedWeightMatrixArray[cachedVertexNormalizedWeightIndex[0]++], FIXED_POINT_DECIMAL_PART_LENGTH);
                dispCalcX = (int)( ( ( (displacementMatrixArray[distanceCache    ] - displacementMatrixArray[distanceCache + 4]) * weight ) + displacementMatrixArray[distanceCache + 4] ) * signX );
                dispCalcY = (int)( ( ( (displacementMatrixArray[distanceCache + 1] - displacementMatrixArray[distanceCache + 5]) * weight ) + displacementMatrixArray[distanceCache + 5] ) * signY );
            }
            else
            {
                dispCalcX = (int)(displacementMatrixArray[distanceCache    ] * signX);
                dispCalcY = (int)(displacementMatrixArray[distanceCache + 1] * signY);
            }

            displacementValuesAtomic.getAndAdd(vertexID1Index0, dispCalcX);
            displacementValuesAtomic.getAndAdd(vertexID1Index1, dispCalcY);

            displacementValuesAtomic.getAndAdd(vertexID2Index0, -dispCalcX);
            displacementValuesAtomic.getAndAdd(vertexID2Index1, -dispCalcY);
        }
        else
        {
            if ( !( (absDistX > kDoubled) && (absDistY > kDoubled) ) )
            {
                dispCalcX = (int)(displacementMatrixArray[distanceCache + 2] * signX);
                dispCalcY = (int)(displacementMatrixArray[distanceCache + 3] * signY);

                displacementValuesAtomic.getAndAdd(vertexID1Index0, dispCalcX);
                displacementValuesAtomic.getAndAdd(vertexID1Index1, dispCalcY);

                displacementValuesAtomic.getAndAdd(vertexID2Index0, -dispCalcX);
                displacementValuesAtomic.getAndAdd(vertexID2Index1, -dispCalcY);
            }
        }
    }

    /**
    *  Sets the FRLayout algorithm 2D force to vertex.
    */
    private void set2DForceToVertex(int vertexID)
    {
        int vertexIDIndex = vertexID << 1;
        float currentDisplacementValue = displacementValuesArray[vertexIDIndex];

        // for the X axis
        float value = (currentDisplacementValue < 0.0f)
                    ? ( -temperature >= currentDisplacementValue) ? -temperature : currentDisplacementValue  // max(a, b)
                    : (  temperature <= currentDisplacementValue) ?  temperature : currentDisplacementValue; // min(a, b)
        cachedVertexPointCoordsMatrixArray[vertexIDIndex] += value;

        if (cachedVertexPointCoordsMatrixArray[vertexIDIndex] > canvasXSize)
            cachedVertexPointCoordsMatrixArray[vertexIDIndex] = canvasXSize;
        // commented out so as to avoid the relayout being bounded by the layout minimum threshold
        // else if (cachedVertexPointCoordsMatrixArray[vertexIDIndex] < 0)
        //    cachedVertexPointCoordsMatrixArray[vertexIDIndex] = 0;

        displacementValuesArray[vertexIDIndex] = 0;



        // for the Y axis
        vertexIDIndex++;
        currentDisplacementValue = displacementValuesArray[vertexIDIndex];
        value = (currentDisplacementValue < 0.0f)
                ? (-temperature >= currentDisplacementValue) ? -temperature : currentDisplacementValue  // max(a, b)
                : ( temperature <= currentDisplacementValue) ?  temperature : currentDisplacementValue; // min(a, b)
        cachedVertexPointCoordsMatrixArray[vertexIDIndex] += value;

        if (cachedVertexPointCoordsMatrixArray[vertexIDIndex] > canvasYSize)
            cachedVertexPointCoordsMatrixArray[vertexIDIndex] = canvasYSize;
        // commented out so as to avoid the relayout being bounded by the layout minimum threshold
        // else if (cachedVertexPointCoordsMatrixArray[vertexIDIndex] < 0)
        //    cachedVertexPointCoordsMatrixArray[vertexIDIndex] = 0;

        displacementValuesArray[vertexIDIndex] = 0;
    }

    /**
    *  Sets the FRLayout algorithm 2D force to vertex.
    *  Atomic version.
    */
    private void set2DForceToVertexAtomic(int vertexID)
    {
        int vertexIDIndex = vertexID << 1;
        float currentDisplacementValue = displacementValuesAtomic.get(vertexIDIndex);

        // for the X axis
        float value = (currentDisplacementValue < 0.0f)
                    ? (-temperature >= currentDisplacementValue) ? -temperature : currentDisplacementValue  // max(a, b)
                    : ( temperature <= currentDisplacementValue) ?  temperature : currentDisplacementValue; // min(a, b)
        cachedVertexPointCoordsMatrixArray[vertexIDIndex] += value;

        if (cachedVertexPointCoordsMatrixArray[vertexIDIndex] > canvasXSize)
            cachedVertexPointCoordsMatrixArray[vertexIDIndex] = canvasXSize;
        // commented out so as to avoid the relayout being bounded by the layout minimum threshold
        // else if (cachedVertexPointCoordsMatrixArray[vertexIDIndex] < 0)
        //    cachedVertexPointCoordsMatrixArray[vertexIDIndex] = 0;

        displacementValuesAtomic.set(vertexIDIndex, 0);



        // for the Y axis
        vertexIDIndex++;
        currentDisplacementValue = displacementValuesAtomic.get(vertexIDIndex);
        value = (currentDisplacementValue < 0.0f)
                ? (-temperature >= currentDisplacementValue) ? -temperature : currentDisplacementValue  // max(a, b)
                : ( temperature <= currentDisplacementValue) ?  temperature : currentDisplacementValue; // min(a, b)
        cachedVertexPointCoordsMatrixArray[vertexIDIndex] += value;

        if (cachedVertexPointCoordsMatrixArray[vertexIDIndex] > canvasYSize)
            cachedVertexPointCoordsMatrixArray[vertexIDIndex] = canvasYSize;
        // commented out so as to avoid the relayout being bounded by the layout minimum threshold
        // else if (cachedVertexPointCoordsMatrixArray[vertexIDIndex] < 0)
        //    cachedVertexPointCoordsMatrixArray[vertexIDIndex] = 0;

        displacementValuesAtomic.set(vertexIDIndex, 0);
    }

    /**
    *  Performs all iterations of the FRLayout algorithm in 3D.
    */
    public void allIterationsCalcBiDirForce3D(int iterations, int componentID, LayoutProgressBarDialog layoutProgressBarDialog)
    {
        this.layoutProgressBarDialog = layoutProgressBarDialog;

        boolean performOpenCLGPUFRLayoutCalculationGetErrorOccured = false;
        if ( OPENCL_GPU_COMPUTING_ENABLED && USE_OPENCL_GPU_COMPUTING_LAYOUT_CALCULATION.get() && (numberOfVertices > MINIMUM_NUMBER_OF_VERTICES_FOR_OPENCL_GPU_COMPUTING_PARALLELIZATION) )
            performOpenCLGPUFRLayoutCalculationGetErrorOccured = performOpenCLGPUFRLayoutCalcBiDirForce3D(iterations);

        if (performOpenCLGPUFRLayoutCalculationGetErrorOccured || !(OPENCL_GPU_COMPUTING_ENABLED && USE_OPENCL_GPU_COMPUTING_LAYOUT_CALCULATION.get()) || (numberOfVertices <= MINIMUM_NUMBER_OF_VERTICES_FOR_OPENCL_GPU_COMPUTING_PARALLELIZATION))
        {
            allIterationsCalcBiDirForce3DJava(iterations, performOpenCLGPUFRLayoutCalculationGetErrorOccured, componentID);
        }
    }

    /**
    *  Main method of the OpenCL GPU FRLayout in 3D data parallel execution code.
    */
    private boolean performOpenCLGPUFRLayoutCalcBiDirForce3D(int iterations)
    {
        FRLayoutComputing frLayoutComputingContext = new FRLayoutComputing( layoutFrame, true, COMPARE_GPU_COMPUTING_LAYOUT_CALCULATION_WITH_CPU.get() );
        frLayoutComputingContext.initializeFRLayoutComputing3DVariables(this, layoutProgressBarDialog, nf1, nf2,
                                                                        vertexIndicesMatrixBuffer, cachedVertexPointCoordsMatrixBuffer, cachedVertexConnectionMatrixBuffer, cachedVertexConnectionRowSkipSizeValuesMatrixBuffer,
                                                                        cachedPseudoVertexMatrixBuffer, cachedVertexNormalizedWeightMatrixBuffer, displacementValuesBuffer,
                                                                        cachedVertexNormalizedWeightIndicesToSkipBuffer, numberOfVertices, iterations, LAYOUT_GPU_COMPUTING_MAX_ERROR_THRESHOLD, useEdgeWeights);
        frLayoutComputingContext.startGPUComputingProcessing();

        // CPU fail-safe mechanism if OpenCL GPU Computing fails for some reason
        return frLayoutComputingContext.getErrorOccured();
    }

    /**
    *  Performs all iterations of the FRLayout algorithm in 3D (Java method).
    */
    private void allIterationsCalcBiDirForce3DJava(int iterations, boolean performOpenCLGPUFRLayoutCalculationGetErrorOccured, int componentID)
    {
        int vertexID = 0;
        if ( !( ( USE_MULTICORE_PROCESS && USE_LAYOUT_N_CORE_PARALLELISM.get() ) || ( OPENCL_GPU_COMPUTING_ENABLED && USE_OPENCL_GPU_COMPUTING_LAYOUT_CALCULATION.get() ) ) || (numberOfVertices < MINIMUM_NUMBER_OF_VERTICES_FOR_NCP_PARALLELIZATION) )
        {
            int from = numberOfVertices;
            int to = 0;
            while (--iterations >= 0)
            {
                // done this way so as to emulate copy by reference (pointer) for value of cachedWeightIndex
                int[] cachedVertexNormalizedWeightIndex = (useEdgeWeights) ? new int[1] : null;
                from = numberOfVertices;
                to = 0;
                while (--from >= 0)
                // for (int from = 0; from < numberOfVertices; from++)
                {
                    to = numberOfVertices;
                    while (--to >= from + 1)
                    // for (int to = from + 1; to < numberOfVertices; to++)
                        calcBiDirForce3D(vertexIndicesMatrixArray[from], vertexIndicesMatrixArray[to], cachedVertexNormalizedWeightIndex);
                }

                vertexID = numberOfVertices;
                while (--vertexID >= 0)
                    set3DForceToVertex(vertexIndicesMatrixArray[vertexID]);
                temperatureHandling();
                updateGUI();

                if (layoutProgressBarDialog.userHasCancelled())
                {
                    return;
                }
            }
        }
        else
        {
            if ( performOpenCLGPUFRLayoutCalculationGetErrorOccured && (layoutProgressBarDialog != null) )
            {
                String progressBarParallelismTitle = (USE_MULTICORE_PROCESS) ? "(Utilizing " + NUMBER_OF_AVAILABLE_PROCESSORS + "-Core Parallelism)" : "";
                layoutProgressBarDialog.prepareProgressBar(numberOfIterations, "Now Processing Layout Iterations " +
                        progressBarParallelismTitle + ( (componentID != 0) ? " for Graph Component: " +
                        componentID : "" ) + "   (no utilization of OpenCL GPU Computing)");
            }

            boolean isPowerOfTwo = org.BioLayoutExpress3D.StaticLibraries.Math.isPowerOfTwo(NUMBER_OF_AVAILABLE_PROCESSORS);
            while (--iterations >= 0)
            {
                LoggerThreadPoolExecutor executor = new LoggerThreadPoolExecutor(NUMBER_OF_AVAILABLE_PROCESSORS, NUMBER_OF_AVAILABLE_PROCESSORS, 0L, TimeUnit.MILLISECONDS,
                                                                                 new LinkedBlockingQueue<Runnable>(NUMBER_OF_AVAILABLE_PROCESSORS),
                                                                                 new LoggerThreadFactory("FRLayout3D"),
                                                                                 new ThreadPoolExecutor.CallerRunsPolicy() );

                cyclicBarrierTimer.clear();
                for (int threadId = 0; threadId < NUMBER_OF_AVAILABLE_PROCESSORS; threadId++)
                    executor.execute( frLayout3DProcessKernel(threadId, isPowerOfTwo) );

                try
                {
                    threadBarrier.await(); // wait for all threads to be ready
                    threadBarrier.await(); // wait for all threads to finish
                    executor.shutdown();
                }
                catch (BrokenBarrierException ex)
                {
                    if (DEBUG_BUILD) println("Problem with a broken barrier with the main FRLayout thread in allIterationsCalcBiDirForce3DJava()!:\n" + ex.getMessage());
                }
                catch (InterruptedException ex)
                {
                    // restore the interuption status after catching InterruptedException
                    Thread.currentThread().interrupt();
                    if (DEBUG_BUILD) println("Problem with pausing the main FRLayout thread in allIterationsCalcBiDirForce3DJava()!:\n" + ex.getMessage());
                }

                if (DEBUG_BUILD) println("\nTotal allIterationsCalcBiDirForce3DJava N-CP run time: " + (cyclicBarrierTimer.getTime() / 1e6) + " ms.\n");

                setForceToVertex(false);
                temperatureHandling();
                updateGUI();

                if (layoutProgressBarDialog.userHasCancelled())
                {
                    return;
                }
            }
        }
    }

    /**
    *  Iterates the FRLayout algorithm in 3D.
    */
    public void iterateCalcBiDirForce3D()
    {
        int vertexID = 0;
        if ( !( USE_MULTICORE_PROCESS && USE_LAYOUT_N_CORE_PARALLELISM.get() ) || (numberOfVertices < MINIMUM_NUMBER_OF_VERTICES_FOR_NCP_PARALLELIZATION) )
        {
            // done this way so as to emulate copy by reference (pointer) for value of cachedWeightIndex
            int[] cachedVertexNormalizedWeightIndex = (useEdgeWeights) ? new int[1] : null;
            int from = numberOfVertices;
            int to = 0;
            while (--from >= 0)
            // for (int from = 0; from < numberOfVertices; from++)
            {
                to = numberOfVertices;
                while (--to >= from + 1)
                // for (int to = from + 1; to < numberOfVertices; to++)
                    calcBiDirForce3D(vertexIndicesMatrixArray[from], vertexIndicesMatrixArray[to], cachedVertexNormalizedWeightIndex);
            }

            vertexID = numberOfVertices;
            while (--vertexID >= 0)
                set3DForceToVertex(vertexIndicesMatrixArray[vertexID]);
        }
        else
        {
            boolean isPowerOfTwo = org.BioLayoutExpress3D.StaticLibraries.Math.isPowerOfTwo(NUMBER_OF_AVAILABLE_PROCESSORS);
            LoggerThreadPoolExecutor executor = new LoggerThreadPoolExecutor(NUMBER_OF_AVAILABLE_PROCESSORS, NUMBER_OF_AVAILABLE_PROCESSORS, 0L, TimeUnit.MILLISECONDS,
                                                                             new LinkedBlockingQueue<Runnable>(NUMBER_OF_AVAILABLE_PROCESSORS),
                                                                             new LoggerThreadFactory("FRLayout3D"),
                                                                             new ThreadPoolExecutor.CallerRunsPolicy() );

            cyclicBarrierTimer.clear();
            for (int threadId = 0; threadId < NUMBER_OF_AVAILABLE_PROCESSORS; threadId++)
                executor.execute( frLayout3DProcessKernel(threadId, isPowerOfTwo) );

            try
            {
                threadBarrier.await(); // wait for all threads to be ready
                threadBarrier.await(); // wait for all threads to finish
                executor.shutdown();
            }
            catch (BrokenBarrierException ex)
            {
                if (DEBUG_BUILD) println("Problem with a broken barrier with the main FRLayout thread in iterateCalcBiDirForce3DJava()!:\n" + ex.getMessage());
            }
            catch (InterruptedException ex)
            {
                // restore the interuption status after catching InterruptedException
                Thread.currentThread().interrupt();
                if (DEBUG_BUILD) println("Problem with pausing the main FRLayout thread in iterateCalcBiDirForce3DJava()!:\n" + ex.getMessage());
            }

            if (DEBUG_BUILD) println("\nTotal iterateCalcBiDirForce3DJava N-CP run time: " + (cyclicBarrierTimer.getTime() / 1e6) + " ms.\n");

            setForceToVertex(false);
        }

        temperatureHandling();
    }

    /**
    *   Return a light-weight thread using the Adapter technique for the 3D FRLayout algorithm so as to avoid any load latencies.
    *   The coding style simulates an OpenCL/CUDA kernel.
    */
    private Runnable frLayout3DProcessKernel(final int threadId, final boolean isPowerOfTwo)
    {
        return frLayout3DProcessKernel(threadId, isPowerOfTwo, false, threadBarrier);
    }

    /**
    *   Return a light-weight thread using the Adapter technique for the 3D FRLayout algorithm so as to avoid any load latencies.
    *   The coding style simulates an OpenCL/CUDA kernel.
    *   Overloaded version that supports optional native method processing.
    */
    public Runnable frLayout3DProcessKernel(final int threadId, final boolean isPowerOfTwo, final boolean javaOrNativeComparisonMethod, final CyclicBarrier threadBarrier)
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
                        // done this way so as to emulate copy by reference (pointer) for value of cachedWeightIndex
                        int[] cachedVertexNormalizedWeightIndex = (useEdgeWeights) ? new int[1] : null;
                        int from = numberOfVertices;
                        int to = 0;
                        if ( USE_ATOMIC_SYNCHRONIZATION_FOR_LAYOUT_N_CORE_PARALLELISM.get() )
                        {
                            if (isPowerOfTwo)
                            {
                                while (--from >= 0)
                                // for (int from = 0; from < numberOfVertices; from++)
                                {
                                    // distribute every (from % NUMBER_OF_AVAILABLE_PROCESSORS) execution to the given threadId
                                    if ( ( from & (NUMBER_OF_AVAILABLE_PROCESSORS - 1) ) == threadId )
                                    {
                                        to = numberOfVertices;
                                        while (--to >= from + 1)
                                        // for (int to = from + 1; to < numberOfVertices; to++)
                                            calcBiDirForce3DAtomic(vertexIndicesMatrixArray[from], vertexIndicesMatrixArray[to], cachedVertexNormalizedWeightIndex);
                                    }
                                    else
                                    {
                                        if (useEdgeWeights)
                                            cachedVertexNormalizedWeightIndex[0] += cachedVertexNormalizedWeightIndicesToSkipArray[from];
                                    }
                                }
                            }
                            else
                            {
                                while (--from >= 0)
                                // for (int from = 0; from < numberOfVertices; from++)
                                {
                                    // distribute every (from % NUMBER_OF_AVAILABLE_PROCESSORS) execution to the given threadId
                                    if ( (from % NUMBER_OF_AVAILABLE_PROCESSORS) == threadId )
                                    {
                                        to = numberOfVertices;
                                        while (--to >= from + 1)
                                        // for (int to = from + 1; to < numberOfVertices; to++)
                                            calcBiDirForce3DAtomic(vertexIndicesMatrixArray[from], vertexIndicesMatrixArray[to], cachedVertexNormalizedWeightIndex);
                                    }
                                    else
                                    {
                                        if (useEdgeWeights)
                                            cachedVertexNormalizedWeightIndex[0] += cachedVertexNormalizedWeightIndicesToSkipArray[from];
                                    }
                                }
                            }
                        }
                        else
                        {
                            if (isPowerOfTwo)
                            {
                                while (--from >= 0)
                                // for (int from = 0; from < numberOfVertices; from++)
                                {
                                    // distribute every (from % NUMBER_OF_AVAILABLE_PROCESSORS) execution to the given threadId
                                    if ((from & (NUMBER_OF_AVAILABLE_PROCESSORS - 1)) == threadId)
                                    {
                                        to = numberOfVertices;
                                        while (--to >= from + 1)
                                        // for (int to = from + 1; to < numberOfVertices; to++)
                                        {
                                            calcBiDirForce3D(vertexIndicesMatrixArray[from], vertexIndicesMatrixArray[to], cachedVertexNormalizedWeightIndex);
                                        }
                                    }
                                    else
                                    {
                                        if (useEdgeWeights)
                                        {
                                            cachedVertexNormalizedWeightIndex[0] += cachedVertexNormalizedWeightIndicesToSkipArray[from];
                                        }
                                    }
                                }
                            }
                            else
                            {
                                while (--from >= 0)
                                // for (int from = 0; from < numberOfVertices; from++)
                                {
                                    // distribute every (from % NUMBER_OF_AVAILABLE_PROCESSORS) execution to the given threadId
                                    if ((from % NUMBER_OF_AVAILABLE_PROCESSORS) == threadId)
                                    {
                                        to = numberOfVertices;
                                        while (--to >= from + 1)
                                        // for (int to = from + 1; to < numberOfVertices; to++)
                                        {
                                            calcBiDirForce3D(vertexIndicesMatrixArray[from], vertexIndicesMatrixArray[to], cachedVertexNormalizedWeightIndex);
                                        }
                                    }
                                    else
                                    {
                                        if (useEdgeWeights)
                                        {
                                            cachedVertexNormalizedWeightIndex[0] += cachedVertexNormalizedWeightIndicesToSkipArray[from];
                                        }
                                    }
                                }
                            }
                        }
                    }
                    finally
                    {
                        threadBarrier.await();
                    }
                }
                catch (BrokenBarrierException ex)
                {
                    if (DEBUG_BUILD) println("Problem with a broken barrier with the N-Core thread with threadId " + threadId + " in frLayout3DProcessKernel()!:\n" + ex.getMessage());
                }
                catch (InterruptedException ex)
                {
                    // restore the interuption status after catching InterruptedException
                    Thread.currentThread().interrupt();
                    if (DEBUG_BUILD) println("Problem with pausing the N-Core thread with threadId " + threadId + " in frLayout3DProcessKernel()!:\n" + ex.getMessage());
                }
            }


        };
    }

    /**
    *  Calculates the 3D bi-directional force of the FRLayout algorithm.
    */
    private void calcBiDirForce3D(int vertexID1, int vertexID2, int[] cachedVertexNormalizedWeightIndex)
    {
        int vertexID1Index0 = 3 * vertexID1;
        int vertexID2Index0 = 3 * vertexID2;
        int vertexID1Index1 = vertexID1Index0 + 1;
        int vertexID2Index1 = vertexID2Index0 + 1;
        int vertexID1Index2 = vertexID1Index0 + 2;
        int vertexID2Index2 = vertexID2Index0 + 2;
        int dimensionalityIndex = cachedVertexConnectionRowSkipSizeValuesMatrixArray[vertexID1 - 1] + vertexID2;

        float distX = cachedVertexPointCoordsMatrixArray[vertexID1Index0] - cachedVertexPointCoordsMatrixArray[vertexID2Index0];
        float distY = cachedVertexPointCoordsMatrixArray[vertexID1Index1] - cachedVertexPointCoordsMatrixArray[vertexID2Index1];
        float distZ = cachedVertexPointCoordsMatrixArray[vertexID1Index2] - cachedVertexPointCoordsMatrixArray[vertexID2Index2];

        if (distX == 0.0f)
            distX = 1.0f;
        if (distY == 0.0f)
            distY = 1.0f;
        if (distZ == 0.0f)
            distZ = 1.0f;

        boolean connected = ( ( ( cachedVertexConnectionMatrixArray[dimensionalityIndex >> BOOLEAN_PACKED_DATA_POWER_OF_TWO_VALUE] >> (dimensionalityIndex & BOOLEAN_PACKED_DATA_BIT_SIZE) ) & 1 ) != 0 );
        if ( !connected && ( ( (distX < 0) ? -distX : distX ) > kDoubled ) && ( ( (distY < 0) ? -distY : distY ) > kDoubled ) ) // abs(distX) & abs(distY)
            return;

        float squaredDistance = (distX * distX + distY * distY + distZ * distZ);
        float distance = (float)sqrt(squaredDistance);

        if (distance <= kDoubled)
        {
            if ( !( ( ( ( cachedPseudoVertexMatrixArray[vertexID1 >> BOOLEAN_PACKED_DATA_POWER_OF_TWO_VALUE] >> (vertexID1 & BOOLEAN_PACKED_DATA_BIT_SIZE) ) & 1 ) != 0 ) && ( ( ( cachedPseudoVertexMatrixArray[vertexID2 >> BOOLEAN_PACKED_DATA_POWER_OF_TWO_VALUE] >> (vertexID2 & BOOLEAN_PACKED_DATA_BIT_SIZE) ) & 1 ) != 0 ) ) )
            {
                float kDist = kSquareValue / distance;
                int dispCalcX = (int)( (distX / distance) * kDist );
                int dispCalcY = (int)( (distY / distance) * kDist );
                int dispCalcZ = (int)( (distZ / distance) * kDist );

                displacementValuesArray[vertexID1Index0] += dispCalcX;
                displacementValuesArray[vertexID1Index1] += dispCalcY;
                displacementValuesArray[vertexID1Index2] += dispCalcZ;

                displacementValuesArray[vertexID2Index0] -= dispCalcX;
                displacementValuesArray[vertexID2Index1] -= dispCalcY;
                displacementValuesArray[vertexID2Index2] -= dispCalcZ;
            }
        }

        if (connected)
        {
            float kDist = squaredDistance / kValue;
            int dispCalcX = 0, dispCalcY = 0, dispCalcZ = 0;

            if (useEdgeWeights)
            {
                float kDistWeight = kDist * convertFromFixedPointShortNumberToUnsignedFloat(cachedVertexNormalizedWeightMatrixArray[cachedVertexNormalizedWeightIndex[0]++], FIXED_POINT_DECIMAL_PART_LENGTH);
                dispCalcX = (int)( (distX / distance) * kDistWeight );
                dispCalcY = (int)( (distY / distance) * kDistWeight );
                dispCalcZ = (int)( (distZ / distance) * kDistWeight );
            }
            else
            {
                dispCalcX = (int)( (distX / distance) * kDist );
                dispCalcY = (int)( (distY / distance) * kDist );
                dispCalcZ = (int)( (distZ / distance) * kDist );
            }

            displacementValuesArray[vertexID1Index0] -= dispCalcX;
            displacementValuesArray[vertexID1Index1] -= dispCalcY;
            displacementValuesArray[vertexID1Index2] -= dispCalcZ;

            displacementValuesArray[vertexID2Index0] += dispCalcX;
            displacementValuesArray[vertexID2Index1] += dispCalcY;
            displacementValuesArray[vertexID2Index2] += dispCalcZ;
        }
    }

    /**
    *  Calculates the 3D bi-directional force of the FRLayout algorithm.
    *  Atomic version.
    */
    private void calcBiDirForce3DAtomic(int vertexID1, int vertexID2, int[] cachedVertexNormalizedWeightIndex)
    {
        int vertexID1Index0 = 3 * vertexID1;
        int vertexID2Index0 = 3 * vertexID2;
        int vertexID1Index1 = vertexID1Index0 + 1;
        int vertexID2Index1 = vertexID2Index0 + 1;
        int vertexID1Index2 = vertexID1Index0 + 2;
        int vertexID2Index2 = vertexID2Index0 + 2;
        int dimensionalityIndex = cachedVertexConnectionRowSkipSizeValuesMatrixArray[vertexID1 - 1] + vertexID2;

        float distX = cachedVertexPointCoordsMatrixArray[vertexID1Index0] - cachedVertexPointCoordsMatrixArray[vertexID2Index0];
        float distY = cachedVertexPointCoordsMatrixArray[vertexID1Index1] - cachedVertexPointCoordsMatrixArray[vertexID2Index1];
        float distZ = cachedVertexPointCoordsMatrixArray[vertexID1Index2] - cachedVertexPointCoordsMatrixArray[vertexID2Index2];

        if (distX == 0.0f)
            distX = 1.0f;
        if (distY == 0.0f)
            distY = 1.0f;
        if (distZ == 0.0f)
            distZ = 1.0f;

        boolean connected = ( ( ( cachedVertexConnectionMatrixArray[dimensionalityIndex >> BOOLEAN_PACKED_DATA_POWER_OF_TWO_VALUE] >> (dimensionalityIndex & BOOLEAN_PACKED_DATA_BIT_SIZE) ) & 1 ) != 0 );
        if ( !connected && ( ( (distX < 0) ? -distX : distX ) > kDoubled ) && ( ( (distY < 0) ? -distY : distY ) > kDoubled ) ) // abs(distX) & abs(distY)
            return;

        float squaredDistance = (distX * distX + distY * distY + distZ * distZ);
        float distance = (float)sqrt(squaredDistance);

        if (distance <= kDoubled)
        {
            if ( !( ( ( ( cachedPseudoVertexMatrixArray[vertexID1 >> BOOLEAN_PACKED_DATA_POWER_OF_TWO_VALUE] >> (vertexID1 & BOOLEAN_PACKED_DATA_BIT_SIZE) ) & 1 ) != 0 ) && ( ( ( cachedPseudoVertexMatrixArray[vertexID2 >> BOOLEAN_PACKED_DATA_POWER_OF_TWO_VALUE] >> (vertexID2 & BOOLEAN_PACKED_DATA_BIT_SIZE) ) & 1 ) != 0 ) ) )
            {
                float kDist = kSquareValue / distance;
                int dispCalcX = (int)( (distX / distance) * kDist );
                int dispCalcY = (int)( (distY / distance) * kDist );
                int dispCalcZ = (int)( (distZ / distance) * kDist );

                displacementValuesAtomic.getAndAdd(vertexID1Index0, dispCalcX);
                displacementValuesAtomic.getAndAdd(vertexID1Index1, dispCalcY);
                displacementValuesAtomic.getAndAdd(vertexID1Index2, dispCalcZ);

                displacementValuesAtomic.getAndAdd(vertexID2Index0, -dispCalcX);
                displacementValuesAtomic.getAndAdd(vertexID2Index1, -dispCalcY);
                displacementValuesAtomic.getAndAdd(vertexID2Index2, -dispCalcZ);
            }
        }

        if (connected)
        {
            float kDist = squaredDistance / kValue;
            int dispCalcX = 0, dispCalcY = 0, dispCalcZ = 0;

            if (useEdgeWeights)
            {
                float kDistWeight = kDist * convertFromFixedPointShortNumberToUnsignedFloat(cachedVertexNormalizedWeightMatrixArray[cachedVertexNormalizedWeightIndex[0]++], FIXED_POINT_DECIMAL_PART_LENGTH);
                dispCalcX = (int)( (distX / distance) * kDistWeight );
                dispCalcY = (int)( (distY / distance) * kDistWeight );
                dispCalcZ = (int)( (distZ / distance) * kDistWeight );
            }
            else
            {
                dispCalcX = (int)( (distX / distance) * kDist );
                dispCalcY = (int)( (distY / distance) * kDist );
                dispCalcZ = (int)( (distZ / distance) * kDist );
            }

            displacementValuesAtomic.getAndAdd(vertexID1Index0, -dispCalcX);
            displacementValuesAtomic.getAndAdd(vertexID1Index1, -dispCalcY);
            displacementValuesAtomic.getAndAdd(vertexID1Index2, -dispCalcZ);

            displacementValuesAtomic.getAndAdd(vertexID2Index0, dispCalcX);
            displacementValuesAtomic.getAndAdd(vertexID2Index1, dispCalcY);
            displacementValuesAtomic.getAndAdd(vertexID2Index2, dispCalcZ);
        }
    }

    /**
    *  Sets the FRLayout algorithm 3D force to vertex.
    */
    private void set3DForceToVertex(int vertexID)
    {
        int vertexIDIndex = 3 * vertexID;
        float currentDisplacementValue = displacementValuesArray[vertexIDIndex];

        // for the X axis
        float value = (currentDisplacementValue < 0.0f)
                    ? (-temperature >= currentDisplacementValue) ? -temperature : currentDisplacementValue  // max(a, b)
                    : ( temperature <= currentDisplacementValue) ?  temperature : currentDisplacementValue; // min(a, b)
        cachedVertexPointCoordsMatrixArray[vertexIDIndex] += value;

        if (cachedVertexPointCoordsMatrixArray[vertexIDIndex] > canvasXSize)
            cachedVertexPointCoordsMatrixArray[vertexIDIndex] = canvasXSize;
        // commented out so as to avoid the relayout being bounded by the layout minimum threshold
        // else if (cachedVertexPointCoordsMatrixArray[vertexIDIndex] < 0)
        //    cachedVertexPointCoordsMatrixArray[vertexIDIndex] = 0;

        displacementValuesArray[vertexIDIndex] = 0;



        // for the Y axis
        vertexIDIndex++;
        currentDisplacementValue = displacementValuesArray[vertexIDIndex];
        value = (currentDisplacementValue < 0.0f)
                ? (-temperature >= currentDisplacementValue) ? -temperature : currentDisplacementValue  // max(a, b)
                : ( temperature <= currentDisplacementValue) ?  temperature : currentDisplacementValue; // min(a, b)
        cachedVertexPointCoordsMatrixArray[vertexIDIndex] += value;

        if (cachedVertexPointCoordsMatrixArray[vertexIDIndex] > canvasYSize)
            cachedVertexPointCoordsMatrixArray[vertexIDIndex] = canvasYSize;
        // commented out so as to avoid the relayout being bounded by the layout minimum threshold
        // else if (cachedVertexPointCoordsMatrixArray[vertexIDIndex] < 0)
        //    cachedVertexPointCoordsMatrixArray[vertexIDIndex] = 0;

        displacementValuesArray[vertexIDIndex] = 0;



        // for the Z axis
        vertexIDIndex++;
        currentDisplacementValue = displacementValuesArray[vertexIDIndex];
        value = (currentDisplacementValue < 0.0f)
                ? (-temperature >= currentDisplacementValue) ? -temperature : currentDisplacementValue  // max(a, b)
                : ( temperature <= currentDisplacementValue) ?  temperature : currentDisplacementValue; // min(a, b)
        cachedVertexPointCoordsMatrixArray[vertexIDIndex] += value;

        if (cachedVertexPointCoordsMatrixArray[vertexIDIndex] > canvasZSize)
            cachedVertexPointCoordsMatrixArray[vertexIDIndex] = canvasZSize;
        // commented out so as to avoid the relayout being bounded by the layout minimum threshold
        // else if (cachedVertexPointCoordsMatrixArray[vertexIDIndex] < 0)
        //    cachedVertexPointCoordsMatrixArray[vertexIDIndex] = 0;

        displacementValuesArray[vertexIDIndex] = 0;
    }

    /**
    *  Sets the FRLayout algorithm 3D force to vertex.
    *  Atomic version.
    */
    private void set3DForceToVertexAtomic(int vertexID)
    {
        int vertexIDIndex = 3 * vertexID;
        float currentDisplacementValue = displacementValuesAtomic.get(vertexIDIndex);

        // for the X axis
        float value = (currentDisplacementValue < 0.0f)
                    ? (-temperature >= currentDisplacementValue) ? -temperature : currentDisplacementValue  // max(a, b)
                    : ( temperature <= currentDisplacementValue) ?  temperature : currentDisplacementValue; // min(a, b)
        cachedVertexPointCoordsMatrixArray[vertexIDIndex] += value;

        if (cachedVertexPointCoordsMatrixArray[vertexIDIndex] > canvasXSize)
            cachedVertexPointCoordsMatrixArray[vertexIDIndex] = canvasXSize;
        // commented out so as to avoid the relayout being bounded by the layout minimum threshold
        // else if (cachedVertexPointCoordsMatrixArray[vertexIDIndex] < 0)
        //    cachedVertexPointCoordsMatrixArray[vertexIDIndex] = 0;

        displacementValuesAtomic.set(vertexIDIndex, 0);



        // for the Y axis
        vertexIDIndex++;
        currentDisplacementValue = displacementValuesAtomic.get(vertexIDIndex);
        value = (currentDisplacementValue < 0.0f)
                ? (-temperature >= currentDisplacementValue) ? -temperature : currentDisplacementValue  // max(a, b)
                : ( temperature <= currentDisplacementValue) ?  temperature : currentDisplacementValue; // min(a, b)
        cachedVertexPointCoordsMatrixArray[vertexIDIndex] += value;

        if (cachedVertexPointCoordsMatrixArray[vertexIDIndex] > canvasYSize)
            cachedVertexPointCoordsMatrixArray[vertexIDIndex] = canvasYSize;
        // commented out so as to avoid the relayout being bounded by the layout minimum threshold
        // else if (cachedVertexPointCoordsMatrixArray[vertexIDIndex] < 0)
        //    cachedVertexPointCoordsMatrixArray[vertexIDIndex] = 0;

        displacementValuesAtomic.set(vertexIDIndex, 0);



        // for the Z axis
        vertexIDIndex++;
        currentDisplacementValue = displacementValuesAtomic.get(vertexIDIndex);
        value = (currentDisplacementValue < 0.0f)
                ? (-temperature >= currentDisplacementValue) ? -temperature : currentDisplacementValue  // max(a, b)
                : ( temperature <= currentDisplacementValue) ?  temperature : currentDisplacementValue; // min(a, b)
        cachedVertexPointCoordsMatrixArray[vertexIDIndex] += value;

        if (cachedVertexPointCoordsMatrixArray[vertexIDIndex] > canvasZSize)
            cachedVertexPointCoordsMatrixArray[vertexIDIndex] = canvasZSize;
        // commented out so as to avoid the relayout being bounded by the layout minimum threshold
        // else if (cachedVertexPointCoordsMatrixArray[vertexIDIndex] < 0)
        //    cachedVertexPointCoordsMatrixArray[vertexIDIndex] = 0;

        displacementValuesAtomic.set(vertexIDIndex, 0);
    }

    /**
    *  Temperature Handling.
    */
    public void temperatureHandling()
    {
        temperature = TEMPERATURE_SCALING * temperature;
        if (temperature < 1.0f) temperature = 1.0f;
    }

    /**
    *  Updates the GUI for the layout algorithm iterations.
    */
    private void updateGUI()
    {
        if (layoutProgressBarDialog != null) layoutProgressBarDialog.incrementProgress();
    }

    /**
    *  Calculates the FRLayout algorithm using Single Core (Java version).
    */
    public void calculateSingleCoreJavaFRLayout(boolean is2DOr3DFRLayout)
    {
        int from = numberOfVertices;
        int to = 0;
        // done this way so as to emulate copy by reference (pointer) for value of cachedWeightIndex
        int[] cachedVertexNormalizedWeightIndex = (useEdgeWeights) ? new int[1] : null;
        if (is2DOr3DFRLayout)
        {
            if ( USE_ATOMIC_SYNCHRONIZATION_FOR_LAYOUT_N_CORE_PARALLELISM.get() )
            {
                while (--from >= 0)
                // for (int from = 0; from < numberOfVertices; from++)
                {
                    to = numberOfVertices;
                    while (--to >= from + 1)
                    // for (int to = from + 1; to < numberOfVertices; to++)
                        calcBiDirForce2DAtomic(vertexIndicesMatrixArray[from], vertexIndicesMatrixArray[to], cachedVertexNormalizedWeightIndex);
                }
            }
            else
            {
                while (--from >= 0)
                // for (int from = 0; from < numberOfVertices; from++)
                {
                    to = numberOfVertices;
                    while (--to >= from + 1)
                    // for (int to = from + 1; to < numberOfVertices; to++)
                        calcBiDirForce2D(vertexIndicesMatrixArray[from], vertexIndicesMatrixArray[to], cachedVertexNormalizedWeightIndex);
                }
            }
        }
        else
        {
            if ( USE_ATOMIC_SYNCHRONIZATION_FOR_LAYOUT_N_CORE_PARALLELISM.get() )
            {
                while (--from >= 0)
                // for (int from = 0; from < numberOfVertices; from++)
                {
                    to = numberOfVertices;
                    while (--to >= from + 1)
                    // for (int to = from + 1; to < numberOfVertices; to++)
                        calcBiDirForce3DAtomic(vertexIndicesMatrixArray[from], vertexIndicesMatrixArray[to], cachedVertexNormalizedWeightIndex);
                }
            }
            else
            {
                while (--from >= 0)
                // for (int from = 0; from < numberOfVertices; from++)
                {
                    to = numberOfVertices;
                    while (--to >= from + 1)
                    // for (int to = from + 1; to < numberOfVertices; to++)
                        calcBiDirForce3D(vertexIndicesMatrixArray[from], vertexIndicesMatrixArray[to], cachedVertexNormalizedWeightIndex);
                }
            }
        }

        setForceToVertex(is2DOr3DFRLayout);
    }

    /**
    *  Sets the calculated force to vertices.
    */
    public void setForceToVertex(boolean is2DOr3DFRLayout)
    {
        int vertexID = numberOfVertices;
        if (is2DOr3DFRLayout)
        {
            if ( USE_ATOMIC_SYNCHRONIZATION_FOR_LAYOUT_N_CORE_PARALLELISM.get() )
            {
                while (--vertexID >= 0)
                    set2DForceToVertexAtomic(vertexIndicesMatrixArray[vertexID]);
            }
            else
            {
                while (--vertexID >= 0)
                    set2DForceToVertex(vertexIndicesMatrixArray[vertexID]);
            }
        }
        else
        {
            if ( USE_ATOMIC_SYNCHRONIZATION_FOR_LAYOUT_N_CORE_PARALLELISM.get() )
            {
                while (--vertexID >= 0)
                    set3DForceToVertexAtomic(vertexIndicesMatrixArray[vertexID]);
            }
            else
            {
                while (--vertexID >= 0)
                    set3DForceToVertex(vertexIndicesMatrixArray[vertexID]);
            }
        }
    }

    /**
    *  Sets the FRLayout algorithm point to vertex.
    */
    public void setPointsToVertices()
    {
        int i = 0;
        int vertexIDIndex = 0;
        if (!RENDERER_MODE_3D)
        {
            for (Vertex vertex : vertexArray)
            {
                vertexIDIndex = vertexIndicesMatrixArray[i] << 1;
                vertex.setVertexLocation( cachedVertexPointCoordsMatrixArray[vertexIDIndex    ],
                                          cachedVertexPointCoordsMatrixArray[vertexIDIndex + 1],
                                          canvasZSize / 2);
                i++;
            }
        }
        else
        {
            for (Vertex vertex : vertexArray)
            {
                vertexIDIndex = 3 * vertexIndicesMatrixArray[i];
                vertex.setVertexLocation( cachedVertexPointCoordsMatrixArray[vertexIDIndex    ],
                                          cachedVertexPointCoordsMatrixArray[vertexIDIndex + 1],
                                          cachedVertexPointCoordsMatrixArray[vertexIDIndex + 2]);
                i++;
            }
        }
    }

    /**
    *  Cleans all the data structures.
    */
    public void clean()
    {
        if (displacementValuesAtomic != null)
            displacementValuesAtomic = null;
        displacementValuesBuffer.clear();
        displacementValuesBuffer = null;
        displacementValuesArray = null;
        if (displacementMatrixBuffer != null)
        {
            displacementMatrixBuffer.clear();
            displacementMatrixBuffer = null;
            displacementMatrixArray = null;
        }
        if (cachedPseudoVertexMatrixBuffer != null)
        {
            cachedPseudoVertexMatrixBuffer.clear();
            cachedPseudoVertexMatrixBuffer = null;
            cachedPseudoVertexMatrixArray = null;
        }
        cachedVertexPointCoordsMatrixBuffer.clear();
        cachedVertexPointCoordsMatrixBuffer = null;
        cachedVertexPointCoordsMatrixArray = null;
        cachedVertexConnectionMatrixBuffer.clear();
        cachedVertexConnectionMatrixBuffer = null;
        cachedVertexConnectionRowSkipSizeValuesMatrixBuffer.clear();
        cachedVertexConnectionRowSkipSizeValuesMatrixArray = null;
        cachedVertexConnectionMatrixArray = null;
        cachedVertexNormalizedWeightMatrixBuffer.clear();
        cachedVertexNormalizedWeightMatrixBuffer = null;
        cachedVertexNormalizedWeightMatrixArray = null;
        if (cachedVertexNormalizedWeightIndicesToSkipBuffer != null)
        {
            cachedVertexNormalizedWeightIndicesToSkipBuffer.clear();
            cachedVertexNormalizedWeightIndicesToSkipBuffer = null;
            cachedVertexNormalizedWeightIndicesToSkipArray = null;
        }

        System.gc();
    }

    /**
    *  Gets and increments the currentVertexCount.
    */
    public int getAndIncrementCurrentVertexCount()
    {
        return currentVertexCount++;
    }

    /**
    *  Sets the numberOfIterations.
    */
    public void setNumberOfIterations(int numberOfIterations)
    {
        this.numberOfIterations = numberOfIterations;
    }

    /**
    *  Gets the numberOfIterations.
    */
    public int getNumberOfIterations()
    {
        return numberOfIterations;
    }

    /**
    *  Gets the displacementMatrixDimensionality.
    */
    public int getDisplacementMatrixDimensionality()
    {
        return displacementMatrixDimensionality;
    }

    /**
    *  Sets the temperature.
    */
    public void setTemperature(float temperature)
    {
        this.temperature = temperature;
    }

    /**
    *  Gets the temperature.
    */
    public float getTemperature()
    {
        return temperature;
    }

    /**
    *  Sets the kValueModifier.
    */
    public void setKValueModifier(float kValueModifier)
    {
        this.kValueModifier = kValueModifier;
    }

    /**
    *  Gets the kValueModifier.
    */
    public float getKValueModifier()
    {
        return kValueModifier;
    }

    /**
    *  Sets the kValue.
    */
    public void setKValue(float kValue)
    {
        this.kValue = kValue;
    }

    /**
    *  Gets the kValue.
    */
    public float getKValue()
    {
        return kValue;
    }

    /**
    *  Gets the kDoubled.
    */
    public float getKDoubled()
    {
        return kDoubled;
    }

    /**
    *  Gets the kSquareValue.
    */
    public float getKSquareValue()
    {
        return kSquareValue;
    }

    /**
    *  Gets the canvasXSize.
    */
    public int getCanvasXSize()
    {
        return canvasXSize;
    }

    /**
    *  Gets the canvasYSize.
    */
    public int getCanvasYSize()
    {
        return canvasYSize;
    }

    /**
    *  Gets the canvasZSize.
    */
    public int getCanvasZSize()
    {
        return canvasZSize;
    }


}