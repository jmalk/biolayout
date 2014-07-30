package org.BioLayoutExpress3D.GPUComputing.OpenGLContext.ExpressionData;

import java.io.*;
import java.nio.*;
import java.text.*;
import java.util.concurrent.*;
import javax.swing.*;
import javax.media.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.common.nio.Buffers;
import org.BioLayoutExpress3D.CoreUI.Dialogs.*;
import org.BioLayoutExpress3D.CPUParallelism.*;
import org.BioLayoutExpress3D.CPUParallelism.Executors.*;
import org.BioLayoutExpress3D.GPUComputing.OpenGLContext.*;
import org.BioLayoutExpress3D.Expression.*;
import static java.lang.Math.*;
import static javax.media.opengl.GL2.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* ExpressionDataComputing is the main OpenGL context component for Expression Data Computing.
*
* @author Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*
*/

public class ExpressionDataComputing extends OpenGLContext
{

    private static final int TEXTURE_RESULTS_TEXTURE_UNIT = 0;
    private static final int TEXTURE_X_TEXTURE_UNIT = 1;
    private static final int TEXTURE_Y_TEXTURE_UNIT = 2;
    private static final int TEXTURE_SUM_X_CACHE_TEXTURE_UNIT = 3;
    private static final int TEXTURE_SUM_X_SUM_X2_CACHE_TEXTURE_UNIT = 4;
    private static final int TEXTURE_SUM_COLUMNS_X2_CACHE_TEXTURE_UNIT = 5;
    private static final int TEXTURE_EXPRESSION_MATRIX_TEXTURE_UNIT = 6;

    private final IntBuffer TEXTURE_ID_RESULTS = (IntBuffer)Buffers.newDirectIntBuffer(1).put( new int[] { 0 } ).rewind();
    private final IntBuffer TEXTURE_ID_X = (IntBuffer)Buffers.newDirectIntBuffer(1).put( new int[] { 0 } ).rewind();
    private final IntBuffer TEXTURE_ID_Y = (IntBuffer)Buffers.newDirectIntBuffer(1).put( new int[] { 0 } ).rewind();
    private final IntBuffer TEXTURE_ID_SUM_X_CACHE = (IntBuffer)Buffers.newDirectIntBuffer(1).put( new int[] { 0 } ).rewind();
    private final IntBuffer TEXTURE_ID_SUM_X_SUM_X2_CACHE = (IntBuffer)Buffers.newDirectIntBuffer(1).put( new int[] { 0 } ).rewind();
    private final IntBuffer TEXTURE_ID_SUM_COLUMNS_X2_CACHE = (IntBuffer)Buffers.newDirectIntBuffer(1).put( new int[] { 0 } ).rewind();
    private final IntBuffer TEXTURE_ID_EXPRESSION_MATRIX = (IntBuffer)Buffers.newDirectIntBuffer(1).put( new int[] { 0 } ).rewind();

    private ExpressionDataComputingShaders expressionDataComputingShaders = null;

    private float[] dataResultsCPUArray = null;
    private FloatBuffer dataResultsGPUBuffer = null;
    private FloatBuffer indexXBuffer = null;
    private FloatBuffer indexYBuffer = null;
    private FloatBuffer dataSumX_cacheBuffer = null;
    private FloatBuffer dataSumX_sumX2_cacheBuffer = null;
    private FloatBuffer dataSumColumns_X2_cacheBuffer = null;
    private FloatBuffer dataExpressionBuffer = null;

    private ExpressionData expressionData = null;
    private LayoutProgressBarDialog layoutProgressBarDialog = null;
    private NumberFormat nf1 = null;
    private NumberFormat nf2 = null;
    private NumberFormat nf3 = null;
    private String[] rowIDsArray = null;
    private float[] dataSumX_cacheArray = null;
    private float[] dataSumX_sumX2_cacheArray = null;
    private float[] dataSumColumns_X2_cacheArray = null;
    private float[] dataExpressionArray = null;
    private float threshold = 0.0f;
    private ObjectOutputStream outOstream = null;
    private PrintWriter outPrintWriter = null;

    private int totalRows = 0;
    private int totalColumns = 0;
    private int N = 0;
    private int numberOfIterations = 0;
    private int lastIndexX = 0;
    private int lastIndexY = 1; // has to be one, as for the first iteration, the indexXBuffer starts from 1!
    private int lastIndexXPrevious = 0;
    private int lastIndexYPrevious = 0;
    private int lastIndexXPreviousWriting = 0;
    private int lastIndexYPreviousWriting = 0;
    private boolean firstLoopFirstInit = true;

    private boolean isTwoDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo = false;
    private int oneDimensionalExpressionDataConvertedTo2DSquareTextureSize = 0;
    private int oneDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision = 0;
    private int oneDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo = 0;
    private int oneDimensionalExpressionDataConvertedTotalSize = 0;
    private int twoDimensionalExpressionDataConvertedTo2DSquareTextureSize = 0;
    private int twoDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision = 0;
    private int twoDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo = 0;
    private int twoDimensionalExpressionDataConvertedTotalSize = 0;
    private float[] texelCenters = null;

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
    public ExpressionDataComputing(AllTextureParameters.TextureParameters textureParameters, int textureSize, int problemDomainX, int problemDomainY)
    {
        super(textureParameters, textureSize, problemDomainX, problemDomainY);

        expressionDataReportAndCalculateTextureSize(textureSize, problemDomainX, problemDomainY);
    }

    /**
    *  The second constructor of the ExpressionDataComputing class.
    */
    public ExpressionDataComputing(AllTextureParameters.TextureParameters textureParameters, int textureSize, int problemDomainX, int problemDomainY, boolean dialogErrorLog)
    {
        super(textureParameters, textureSize, problemDomainX, problemDomainY, dialogErrorLog);

        expressionDataReportAndCalculateTextureSize(textureSize, problemDomainX, problemDomainY);
    }

    /**
    *  The third constructor of the ExpressionDataComputing class.
    */
    public ExpressionDataComputing(AllTextureParameters.TextureParameters textureParameters, int textureSize, int problemDomainX, int problemDomainY, boolean dialogErrorLog, boolean openGLSupportAndExtensionsLog)
    {
        super(textureParameters, textureSize, problemDomainX, problemDomainY, dialogErrorLog, openGLSupportAndExtensionsLog);

        expressionDataReportAndCalculateTextureSize(textureSize, problemDomainX, problemDomainY);
    }

    /**
    *  Expression data report and texture size calculation.
    */
    private void expressionDataReportAndCalculateTextureSize(int maxTextureSize, int totalRows, int totalColumns)
    {
        this.totalRows = totalRows;
        this.totalColumns = totalColumns;

        long totalExpressionDataCalculationsNeeded = org.BioLayoutExpress3D.StaticLibraries.Math.totalTriangularMatrixCalculationsNeeded( (long)totalRows );

        if (textureParameters.textureFormat == GL_RGBA)
        {
            oneDimensionalExpressionDataConvertedTo2DSquareTextureSize = (int)ceil( sqrt(totalRows / 4.0) );
            oneDimensionalExpressionDataConvertedTo2DSquareTextureSize = org.BioLayoutExpress3D.StaticLibraries.Math.getNextPowerOfTwo(oneDimensionalExpressionDataConvertedTo2DSquareTextureSize);
            oneDimensionalExpressionDataConvertedTotalSize = 4 * oneDimensionalExpressionDataConvertedTo2DSquareTextureSize * oneDimensionalExpressionDataConvertedTo2DSquareTextureSize;
            twoDimensionalExpressionDataConvertedTo2DSquareTextureSize = (int)ceil( sqrt( (totalRows * totalColumns) / 4.0 ) );
            checkForTwoDimensionalExpressionDataConvertedTo2DSquareTextureSizeNextPowerOfTwo(maxTextureSize);
            twoDimensionalExpressionDataConvertedTotalSize = 4 * twoDimensionalExpressionDataConvertedTo2DSquareTextureSize * twoDimensionalExpressionDataConvertedTo2DSquareTextureSize;
            textureSize = twoDimensionalExpressionDataConvertedTo2DSquareTextureSize;
        }
        else
        {
            oneDimensionalExpressionDataConvertedTo2DSquareTextureSize = (int)ceil( sqrt(totalRows) );
            // oneDimensionalExpressionDataConvertedTo2DSquareTextureSize % 2 with bitshift for speed, make dimensions always even to avoid shader problems
            if ( (oneDimensionalExpressionDataConvertedTo2DSquareTextureSize & 1) != 0 ) oneDimensionalExpressionDataConvertedTo2DSquareTextureSize++;
            oneDimensionalExpressionDataConvertedTo2DSquareTextureSize = org.BioLayoutExpress3D.StaticLibraries.Math.getNextPowerOfTwo(oneDimensionalExpressionDataConvertedTo2DSquareTextureSize);
            oneDimensionalExpressionDataConvertedTotalSize = oneDimensionalExpressionDataConvertedTo2DSquareTextureSize * oneDimensionalExpressionDataConvertedTo2DSquareTextureSize;
            twoDimensionalExpressionDataConvertedTo2DSquareTextureSize = (int)ceil( sqrt(totalRows * totalColumns) );
            // twoDimensionalExpressionDataConvertedTo2DSquareTextureSize % 2 with bitshift for speed, make dimensions always even to avoid shader problems
            checkForTwoDimensionalExpressionDataConvertedTo2DSquareTextureSizeNextPowerOfTwo(maxTextureSize);
            twoDimensionalExpressionDataConvertedTotalSize = twoDimensionalExpressionDataConvertedTo2DSquareTextureSize * twoDimensionalExpressionDataConvertedTo2DSquareTextureSize;
            textureSize = twoDimensionalExpressionDataConvertedTo2DSquareTextureSize;
        }

        oneDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision = oneDimensionalExpressionDataConvertedTo2DSquareTextureSize - 1;
        oneDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo = (int)( log(oneDimensionalExpressionDataConvertedTo2DSquareTextureSize) / log(2) ); //log2(oneDimensionalExpressionDataConvertedTo2DSquareTextureSize)
        N = (textureParameters.textureFormat == GL_RGBA) ? 4 * (textureSize * textureSize) : (textureSize * textureSize);
        numberOfIterations = (int)( totalExpressionDataCalculationsNeeded / N + ( ( (totalExpressionDataCalculationsNeeded % N) != 0 ) ? 1 : 0 ) );
        if (textureParameters.textureTarget == GL_TEXTURE_2D)
        {
            texelCenters = new float[] { (1.0f / oneDimensionalExpressionDataConvertedTo2DSquareTextureSize) / 2.0f,
                                         (1.0f / twoDimensionalExpressionDataConvertedTo2DSquareTextureSize) / 2.0f
                                       };
        }

        benchmarkMode = compareResults = showDifferentResultsOnly = reportIterationsInConsole = COMPARE_GPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION_WITH_CPU.get();
        singleCoreOrNCPComparisonMethod = COMPARE_GPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION_WITH_CPU_DEFAULT_COMPARISON_METHOD.get().contains(SINGLE_CORE_STRING);
        javaOrNativeComparisonMethod = COMPARE_GPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION_WITH_CPU_DEFAULT_COMPARISON_METHOD.get().contains(JAVA_STRING);

        if (DEBUG_BUILD) println("\nMaxTextureSize: " + maxTextureSize + " TotalRows: " + totalRows + " TotalColumns: " + totalColumns +
                                 "\nTotal expression data calculations needed: " + totalExpressionDataCalculationsNeeded +
                                 "\nCalculated texture size: " + textureSize + " (max allowed texture size: " + maxTextureSize + ")" +
                                 "\nFloat arrays size N per iteration: " + N +
                                 "\nNumberOfIterations: " + numberOfIterations +
                                 "\nPower-Of-Two OneDimensionalExpressionDataConvertedTo2DSquareTextureSize: " + oneDimensionalExpressionDataConvertedTo2DSquareTextureSize +
                                 "\nPower-Of-Two OneDimensionalExpressionDataConvertedTotalSize: " + oneDimensionalExpressionDataConvertedTotalSize +
                                 "\nPower-Of-Two OneDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision: " + oneDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision +
                                 "\nPower-Of-Two OneDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo: " + oneDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo +
                                 ( (textureParameters.textureTarget == GL_TEXTURE_2D) ? ("\nOne Dimensional texelCenter: " + texelCenters[0]) : "" ) +
                                 ( (isTwoDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo) ? "\nPower-Of-Two" : "\nNon-Power-Of-Two" ) + " TwoDimensionalExpressionDataConvertedTo2DSquareTextureSize: " + twoDimensionalExpressionDataConvertedTo2DSquareTextureSize +
                                 ( (isTwoDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo) ? "\nPower-Of-Two" : "\nNon-Power-Of-Two" ) + " TwoDimensionalExpressionDataConvertedTotalSize: " + twoDimensionalExpressionDataConvertedTotalSize +
                                 ( (isTwoDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo) ? "\nPower-Of-Two TwoDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision: " + twoDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision : "" ) +
                                 ( (isTwoDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo) ? "\nPower-Of-Two TwoDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo: " + twoDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo : "" )+
                                 ( (textureParameters.textureTarget == GL_TEXTURE_2D) ? ("\nTwo Dimensional texelCenter: " + texelCenters[1]) : "" ) );
        if (DEBUG_BUILD) reportGPUComputingVariables();
    }

    /**
    *  Checks for the next power of two for the two dimensional expression data matrix.
    */
    private void checkForTwoDimensionalExpressionDataConvertedTo2DSquareTextureSizeNextPowerOfTwo(int maxTextureSize)
    {
        int twoDimensionalExpressionDataConvertedTo2DSquareTextureSizeNextPowerOfTwo = org.BioLayoutExpress3D.StaticLibraries.Math.getNextPowerOfTwo(twoDimensionalExpressionDataConvertedTo2DSquareTextureSize);
        if (twoDimensionalExpressionDataConvertedTo2DSquareTextureSizeNextPowerOfTwo <= maxTextureSize)
        {
            isTwoDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo = true;
            twoDimensionalExpressionDataConvertedTo2DSquareTextureSize = twoDimensionalExpressionDataConvertedTo2DSquareTextureSizeNextPowerOfTwo;
            twoDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision = twoDimensionalExpressionDataConvertedTo2DSquareTextureSize - 1;
            twoDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo = (int)( log(twoDimensionalExpressionDataConvertedTo2DSquareTextureSize) / log(2) ); //log2(twoDimensionalExpressionDataConvertedTo2DSquareTextureSize
        }
    }

    /**
    *  Initializes all expression data computing variables.
    */
    public void initializeExpressionDataComputingVariables(ExpressionData expressionData, LayoutProgressBarDialog layoutProgressBarDialog, NumberFormat nf1, NumberFormat nf2, NumberFormat nf3, String[] rowIDsArray, FloatBuffer sumX_cacheBuffer, FloatBuffer sumX_sumX2_cacheBuffer, FloatBuffer sumColumns_X2_cacheBuffer, FloatBuffer expressionBuffer, float threshold, ObjectOutputStream outOstream, PrintWriter outPrintWriter, double errorThreshold)
    {
        this.expressionData = expressionData;
        this.layoutProgressBarDialog = layoutProgressBarDialog;
        this.nf1 = nf1;
        this.nf2 = nf2;
        this.nf3 = nf3;
        this.rowIDsArray = rowIDsArray;
        this.dataSumX_cacheArray = sumX_cacheBuffer.array();
        this.dataSumX_sumX2_cacheArray = sumX_sumX2_cacheBuffer.array();
        this.dataSumColumns_X2_cacheArray = sumColumns_X2_cacheBuffer.array();
        this.dataExpressionArray = expressionBuffer.array();
        this.threshold = threshold;
        this.outOstream = outOstream;
        this.outPrintWriter = outPrintWriter;
        this.errorThreshold = errorThreshold;
    }

    /**
    *  Creates all data arrays.
    */
    private void createDataArrays()
    {
        // create data vectors (use FloatBuffer.allocate(capacity) instead of Buffers.newDirectFloatBuffer(capacity) for less Java CPU memory consumption)
        dataResultsGPUBuffer = FloatBuffer.allocate(N);
        if (compareResults || showResults)
            dataResultsCPUArray = new float[N];
        indexXBuffer = FloatBuffer.allocate(N);
        indexYBuffer = FloatBuffer.allocate(N);
        dataSumX_cacheBuffer = FloatBuffer.allocate(oneDimensionalExpressionDataConvertedTotalSize);
        dataSumX_sumX2_cacheBuffer = FloatBuffer.allocate(oneDimensionalExpressionDataConvertedTotalSize);
        dataSumColumns_X2_cacheBuffer = FloatBuffer.allocate(oneDimensionalExpressionDataConvertedTotalSize);
        dataExpressionBuffer = FloatBuffer.allocate(twoDimensionalExpressionDataConvertedTotalSize);

        dataSumX_cacheBuffer.put(dataSumX_cacheArray);
        dataSumX_sumX2_cacheBuffer.put(dataSumX_sumX2_cacheArray);
        dataSumColumns_X2_cacheBuffer.put(dataSumColumns_X2_cacheArray);
        dataExpressionBuffer.put(dataExpressionArray);

        dataSumX_cacheBuffer.rewind();
        dataSumX_sumX2_cacheBuffer.rewind();
        dataSumColumns_X2_cacheBuffer.rewind();
        dataExpressionBuffer.rewind();
    }

    /**
    *  Fills all index data arrays.
    */
    private void fillIndexDataArrays()
    {
        // clear data vectors
        indexXBuffer = (FloatBuffer)indexXBuffer.clear();
        indexYBuffer = (FloatBuffer)indexYBuffer.clear();

        transformExpressionDataCalculationsFromUpperDiagonalMatrixToSquareMatrixInSteps();

        indexXBuffer.rewind();
        indexYBuffer.rewind();
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
                    indexXBuffer.put(i);
                    indexYBuffer.put(j);
                }
            }

            if (exitLoops)
                break;
        }

        if (DEBUG_BUILD && reportIterationsInConsole) println("Last index: " + (index - 1) + ", exitLoops needed: " + ( (exitLoops) ? "yes" : "no" ) +
                                                              "\nData indices not used: " + indexXBuffer.remaining() + ", lastIndexX: " + lastIndexX + ", lastIndexY: " + lastIndexY + "\n");
    }

    /**
    *  Creates all permanent textures.
    */
    private void createPermanentTextures(GL2 gl)
    {
        // create textures
        gl.glGenTextures(1, TEXTURE_ID_RESULTS);
        gl.glGenTextures(1, TEXTURE_ID_SUM_X_CACHE);
        gl.glGenTextures(1, TEXTURE_ID_SUM_X_SUM_X2_CACHE);
        gl.glGenTextures(1, TEXTURE_ID_SUM_COLUMNS_X2_CACHE);
        gl.glGenTextures(1, TEXTURE_ID_EXPRESSION_MATRIX);

        // set up textures
        setupTexture(gl, TEXTURE_ID_RESULTS.get(0), TEXTURE_RESULTS_TEXTURE_UNIT);
        transferToTexture( gl, dataResultsGPUBuffer, TEXTURE_ID_RESULTS.get(0) );

        setupTexture(gl, TEXTURE_ID_SUM_X_CACHE.get(0), oneDimensionalExpressionDataConvertedTo2DSquareTextureSize, TEXTURE_SUM_X_CACHE_TEXTURE_UNIT);
        transferToTexture(gl, dataSumX_cacheBuffer, TEXTURE_ID_SUM_X_CACHE.get(0), oneDimensionalExpressionDataConvertedTo2DSquareTextureSize);

        setupTexture(gl, TEXTURE_ID_SUM_X_SUM_X2_CACHE.get(0), oneDimensionalExpressionDataConvertedTo2DSquareTextureSize, TEXTURE_SUM_X_SUM_X2_CACHE_TEXTURE_UNIT);
        transferToTexture(gl, dataSumX_sumX2_cacheBuffer, TEXTURE_ID_SUM_X_SUM_X2_CACHE.get(0), oneDimensionalExpressionDataConvertedTo2DSquareTextureSize);

        setupTexture(gl, TEXTURE_ID_SUM_COLUMNS_X2_CACHE.get(0), oneDimensionalExpressionDataConvertedTo2DSquareTextureSize, TEXTURE_SUM_COLUMNS_X2_CACHE_TEXTURE_UNIT);
        transferToTexture(gl, dataSumColumns_X2_cacheBuffer, TEXTURE_ID_SUM_COLUMNS_X2_CACHE.get(0), oneDimensionalExpressionDataConvertedTo2DSquareTextureSize);

        setupTexture(gl, TEXTURE_ID_EXPRESSION_MATRIX.get(0), twoDimensionalExpressionDataConvertedTo2DSquareTextureSize, TEXTURE_EXPRESSION_MATRIX_TEXTURE_UNIT);
        transferToTexture(gl, dataExpressionBuffer, TEXTURE_ID_EXPRESSION_MATRIX.get(0), twoDimensionalExpressionDataConvertedTo2DSquareTextureSize);

        // set texenv mode from modulate (the default) to replace
        gl.glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);
    }

    /**
    *  Creates all temporary index textures.
    */
    private void createTemporaryIndexTextures(GL2 gl)
    {
        // create textures
        gl.glGenTextures(1, TEXTURE_ID_X);
        gl.glGenTextures(1, TEXTURE_ID_Y);

        // set up textures
        setupTexture(gl, TEXTURE_ID_X.get(0), TEXTURE_X_TEXTURE_UNIT);
        transferToTexture( gl, indexXBuffer, TEXTURE_ID_X.get(0) );

        setupTexture(gl, TEXTURE_ID_Y.get(0), TEXTURE_Y_TEXTURE_UNIT);
        transferToTexture( gl, indexYBuffer, TEXTURE_ID_Y.get(0) );

        // set texenv mode from modulate (the default) to replace
        gl.glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);
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
    *  Performs the actual calculation.
    */
    private void performComputation(GL2 gl)
    {
        try
        {
            layoutProgressBarDialog.prepareProgressBar(numberOfIterations, "Calculating Number Of Iterations: " + numberOfIterations + " (Now Calculating First Iteration)");
            layoutProgressBarDialog.startProgressBar();

            // attach texture x to FBO for proper FBO completion
            gl.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, textureParameters.textureTarget, TEXTURE_ID_RESULTS.get(0), 0);

            // check if that worked
            if ( !checkFrameBufferStatus(gl) )
            {
                if (DEBUG_BUILD) println("FBO glFramebufferTexture2DEXT() Status: Failed!");
            }
            else
            {
                if (DEBUG_BUILD) println("FBO glFramebufferTexture2DEXT() Status: Passed!");
            }

            // Calling glFinish() is only neccessary to get accurate timings,
            // and we need a high number of iterations to avoid timing noise.
            gl.glFinish();

            if (USE_MULTICORE_PROCESS)
            {
                initializeTaskParallelismForWriteIterationResultsToFile(this);
                taskParallelismThreadBarrier.await(); // synchronized start of Task Parallelism
            }

            double totalTimeGPU = 0.0;
            double totalTimeCPU = 0.0;

            long startTime = 0;
            for (int iteration = 0; iteration < numberOfIterations; iteration++)
            {
                if (DEBUG_BUILD)
                    println("GPGPU ExpressionData starting iteration " + iteration + "/" + numberOfIterations);

                layoutProgressBarDialog.incrementProgress();

                deleteTemporaryIndexTextures(gl);
                fillIndexDataArrays();
                createTemporaryIndexTextures(gl);

                if (benchmarkMode)
                    startTime = System.nanoTime();

                // set render destination
                gl.glDrawBuffer(GL_COLOR_ATTACHMENT0);

                if (textureParameters.textureFormat == GL_RGBA)
                {
                    if (textureParameters.textureTarget == GL_TEXTURE_2D)
                        expressionDataComputingShaders.useRGBATexture2DShaderForExpressionData(gl, TEXTURE_X_TEXTURE_UNIT, TEXTURE_Y_TEXTURE_UNIT,
                                                                                               TEXTURE_SUM_X_CACHE_TEXTURE_UNIT, TEXTURE_SUM_X_SUM_X2_CACHE_TEXTURE_UNIT, TEXTURE_SUM_COLUMNS_X2_CACHE_TEXTURE_UNIT, TEXTURE_EXPRESSION_MATRIX_TEXTURE_UNIT,
                                                                                               oneDimensionalExpressionDataConvertedTo2DSquareTextureSize, oneDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision, oneDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo,
                                                                                               twoDimensionalExpressionDataConvertedTo2DSquareTextureSize, twoDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision, twoDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo,
                                                                                               texelCenters, totalColumns);
                    else
                        expressionDataComputingShaders.useRGBATextureRectangleShaderForExpressionData(gl, TEXTURE_X_TEXTURE_UNIT, TEXTURE_Y_TEXTURE_UNIT,
                                                                                                      TEXTURE_SUM_X_CACHE_TEXTURE_UNIT, TEXTURE_SUM_X_SUM_X2_CACHE_TEXTURE_UNIT, TEXTURE_SUM_COLUMNS_X2_CACHE_TEXTURE_UNIT, TEXTURE_EXPRESSION_MATRIX_TEXTURE_UNIT,
                                                                                                      oneDimensionalExpressionDataConvertedTo2DSquareTextureSize, oneDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision, oneDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo,
                                                                                                      twoDimensionalExpressionDataConvertedTo2DSquareTextureSize, twoDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision, twoDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo,
                                                                                                      totalColumns);
                }
                else
                {
                    if (textureParameters.textureTarget == GL_TEXTURE_2D)
                        expressionDataComputingShaders.useRTexture2DShaderForExpressionData(gl, TEXTURE_X_TEXTURE_UNIT, TEXTURE_Y_TEXTURE_UNIT,
                                                                                            TEXTURE_SUM_X_CACHE_TEXTURE_UNIT, TEXTURE_SUM_X_SUM_X2_CACHE_TEXTURE_UNIT, TEXTURE_SUM_COLUMNS_X2_CACHE_TEXTURE_UNIT, TEXTURE_EXPRESSION_MATRIX_TEXTURE_UNIT,
                                                                                            oneDimensionalExpressionDataConvertedTo2DSquareTextureSize, oneDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision, oneDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo,
                                                                                            twoDimensionalExpressionDataConvertedTo2DSquareTextureSize, twoDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision, twoDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo,
                                                                                            texelCenters, totalColumns);
                    else
                        expressionDataComputingShaders.useRTextureRectangleShaderForExpressionData(gl, TEXTURE_X_TEXTURE_UNIT, TEXTURE_Y_TEXTURE_UNIT,
                                                                                                  TEXTURE_SUM_X_CACHE_TEXTURE_UNIT, TEXTURE_SUM_X_SUM_X2_CACHE_TEXTURE_UNIT, TEXTURE_SUM_COLUMNS_X2_CACHE_TEXTURE_UNIT, TEXTURE_EXPRESSION_MATRIX_TEXTURE_UNIT,
                                                                                                  oneDimensionalExpressionDataConvertedTo2DSquareTextureSize, oneDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision, oneDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo,
                                                                                                  twoDimensionalExpressionDataConvertedTo2DSquareTextureSize, twoDimensionalExpressionDataConvertedTo2DSquareTextureSizeModuloDivision, twoDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo,
                                                                                                  totalColumns);
                }

                renderQuad(gl);

                expressionDataComputingShaders.disableExpressionDataComputing(gl);

                if ( USE_MULTICORE_PROCESS && (iteration > 0) ) // barrier point for previous writing to file iteration, make sure to not calc barrier waiting time
                {
                    if (benchmarkMode)
                        startTime = (System.nanoTime() - startTime);
                    taskParallelismThreadBarrier.await();
                    if (benchmarkMode)
                        startTime += System.nanoTime();
                }

                // get GPU results
                // dataResultsGPUBuffer.clear();
                transferFromTexture(gl, GL_COLOR_ATTACHMENT0, dataResultsGPUBuffer);

                gl.glFlush();
                gl.glFinish();

                // done, calc timer
                if (benchmarkMode)
                {
                    totalTimeGPU = (System.nanoTime() - startTime) / 1000000000.0; // for secs
                    if (DEBUG_BUILD) println("\nTotal time taken for GPU calcs: " + totalTimeGPU + " secs");
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
                    layoutProgressBarDialog.setText("Expression Data GPU Calculations Iterations Done: " + (iteration + 1) + "/" + numberOfIterations + " (GPU Calculations: " + nf1.format( N * (iteration + 1) ) + ", speedup: " + nf2.format(totalTimeCPU / totalTimeGPU) + "x, Errors: " + ( (errorThresholdExceeded) ? "yes" : "no" ) + ")");
                    if (DEBUG_BUILD && reportIterationsInConsole) println("\nExpression Data GPU Calculations Iterations: " + (iteration + 1) + "/" + numberOfIterations + "\n(GPU Calculations: " + nf1.format( N * (iteration + 1) ) + ", speedup: " + nf2.format(totalTimeCPU / totalTimeGPU) + "x, Errors: " + ( (errorThresholdExceeded) ? "yes" : "no" ) + ")");
                }

                lastIndexXPreviousWriting = lastIndexXPrevious;
                lastIndexYPreviousWriting = lastIndexYPrevious;
                if (!USE_MULTICORE_PROCESS)
                    writeIterationResultsToFile();
                else
                    taskParallelismThreadBarrier.await();
            }

            if (USE_MULTICORE_PROCESS)
            {
                taskParallelismThreadBarrier.await(); // synchronized end of Task Parallelism
                if (DEBUG_BUILD) println("\nTotal ExpressionDataComputing Task Parallelism run time: " + (taskParallelismCyclicBarrierTimer.getTime() / 1e6) + " ms.\n");
            }

            layoutProgressBarDialog.endProgressBar();
            layoutProgressBarDialog.stopProgressBar();
        }
        catch (IOException ioe)
        {
            if (DEBUG_BUILD) println("IOException in ExpressionDataComputing.performComputation()\n" + ioe.getMessage());
            JOptionPane.showMessageDialog(this, "IOException in building the Correlation network with the GPU.\n" + ioe.getMessage(), "Error: IOException in building the Correlation network with the GPU", JOptionPane.ERROR_MESSAGE);
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
    private void initializeTaskParallelismForWriteIterationResultsToFile(final OpenGLContext component)
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
                        writeIterationResultsToFile();
                    }
                    taskParallelismThreadBarrier.await(); // synchronized end of Task Parallelism
                }
                catch (IOException ioe)
                {
                    if (DEBUG_BUILD) println("IOException in ExpressionDataComputing.initializeTaskParallelismForWriteIterationResultsToFile()\n" + ioe.getMessage());
                    JOptionPane.showMessageDialog(component, "IOException in building the Correlation network with the GPU.\n" + ioe.getMessage(), "Error: IOException in building the Correlation network with the GPU", JOptionPane.ERROR_MESSAGE);

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
    *  Compares results with the CPU.
    */
    private double compareWithCPU(double totalTimeGPU)
    {
        double totalTimeCPU = 0.0;

        // store CPU results
        if (compareResults)
        {
            float[] indexXArray = indexXBuffer.array();
            float[] indexYArray = indexYBuffer.array();
            // calc on CPU
            if (singleCoreOrNCPComparisonMethod)
            {
                long startTime = 0, endTime = 0;

                startTime = System.nanoTime();
                for (int i = 0; i < N; i++)
                {
                    dataResultsCPUArray[i] = expressionData.calculateCorrelation((int) indexXArray[i], (int) indexYArray[i], dataExpressionArray);
                }
                endTime = System.nanoTime();

                totalTimeCPU = (endTime - startTime) / 1000000000.0; // for secs
            }
            else
            {
                calculateNCPExpressionData(indexXArray, indexYArray);
                totalTimeCPU = ncpCyclicBarrierTimer.getTime() / 1000000000.0; // for secs
            }
            if (DEBUG_BUILD) println("\nTotal time taken for CPU calcs: " + totalTimeCPU + " secs");

            // and compare results
            double maxError = Double.NEGATIVE_INFINITY;
            double avgError = 0.0;
            double difference = 0.0;
            for (int i = 0; i < N; i++)
            {
                difference = abs(dataResultsGPUBuffer.get(i) - dataResultsCPUArray[i]);
                if (difference > maxError)
                {
                    maxError = difference;
                    if (DEBUG_BUILD)
                        println( "Difference of " + difference + " between CPU & GPU data:\t" + dataResultsCPUArray[i] + " & " + dataResultsGPUBuffer.get(i) );
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
                    printArrayResults(dataResultsCPUArray);
                }
            }
        }

        if (showResults)
        {
            // print out results
            if (DEBUG_BUILD)
            {
                println("\nGPU RESULTS:");
                printArrayResults(dataResultsGPUBuffer);
            }
        }

        return totalTimeCPU;
    }

    /**
    *  Calculates the Expression Data algorithm using N-CP.
    */
    private void calculateNCPExpressionData(float[] indexXArray, float[] indexYArray)
    {
        int totalLoopsPerProcess = N / NUMBER_OF_AVAILABLE_PROCESSORS;
        LoggerThreadPoolExecutor executor = new LoggerThreadPoolExecutor(NUMBER_OF_AVAILABLE_PROCESSORS, NUMBER_OF_AVAILABLE_PROCESSORS, 0L, TimeUnit.MILLISECONDS,
                                                                         new LinkedBlockingQueue<Runnable>(NUMBER_OF_AVAILABLE_PROCESSORS),
                                                                         new LoggerThreadFactory("ExpressionData"),
                                                                         new ThreadPoolExecutor.CallerRunsPolicy() );

        ncpCyclicBarrierTimer.clear();
        for (int threadId = 0; threadId < NUMBER_OF_AVAILABLE_PROCESSORS; threadId++)
            executor.execute( expressionDataProcessKernel(threadId, totalLoopsPerProcess, indexXArray, indexYArray) );

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
    private Runnable expressionDataProcessKernel(final int threadId, final int totalLoopsPerProcess, final float[] indexXArray, final float[] indexYArray)
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
                        for (int i = threadId * totalLoopsPerProcess; i < (threadId + 1) * totalLoopsPerProcess + extraLoops; i++)
                        {
                            dataResultsCPUArray[i] = expressionData.calculateCorrelation((int) indexXArray[i], (int) indexYArray[i], dataExpressionArray);
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
                    correlation = dataResultsGPUBuffer.get(index - 1); // since the index is already incremented by (++index > N)
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
        dataResultsGPUBuffer.clear();
        dataResultsGPUBuffer = null;
        if (compareResults || showResults)
            dataResultsCPUArray = null;
        indexXBuffer.clear();
        indexXBuffer = null;
        indexYBuffer.clear();
        indexYBuffer = null;
        dataSumX_cacheBuffer.clear();
        dataSumX_cacheBuffer = null;
        dataSumX_sumX2_cacheBuffer.clear();
        dataSumX_sumX2_cacheBuffer = null;
        dataSumColumns_X2_cacheBuffer.clear();
        dataSumColumns_X2_cacheBuffer = null;
        dataExpressionBuffer.clear();
        dataExpressionBuffer = null;
        rowIDsArray = null;
        dataSumX_cacheArray = null;
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
    *  Deletes all temporary index textures.
    */
    private void deleteTemporaryIndexTextures(GL2 gl)
    {
        if (gl.glIsTexture( TEXTURE_ID_X.get(0) ) )
            gl.glDeleteTextures(1, TEXTURE_ID_X);
        if (gl.glIsTexture( TEXTURE_ID_Y.get(0) ) )
            gl.glDeleteTextures(1, TEXTURE_ID_Y);
    }

    /**
    *  Deletes all permament textures.
    */
    private void deletePermamentTextures(GL2 gl)
    {
        if (gl.glIsTexture( TEXTURE_ID_RESULTS.get(0) ) )
            gl.glDeleteTextures(1, TEXTURE_ID_RESULTS);
        if (gl.glIsTexture( TEXTURE_ID_SUM_X_CACHE.get(0) ) )
            gl.glDeleteTextures(1, TEXTURE_ID_SUM_X_CACHE);
        if (gl.glIsTexture( TEXTURE_ID_SUM_X_SUM_X2_CACHE.get(0) ) )
            gl.glDeleteTextures(1, TEXTURE_ID_SUM_X_SUM_X2_CACHE);
        if (gl.glIsTexture( TEXTURE_ID_SUM_COLUMNS_X2_CACHE.get(0) ) )
            gl.glDeleteTextures(1, TEXTURE_ID_SUM_COLUMNS_X2_CACHE);
        if (gl.glIsTexture( TEXTURE_ID_EXPRESSION_MATRIX.get(0) ) )
            gl.glDeleteTextures(1, TEXTURE_ID_EXPRESSION_MATRIX);
    }

    /**
    *  Initializes CPU memory.
    */
    @Override
    protected void initializeCPUMemoryImplementation(GL2 gl) throws OutOfMemoryError
    {
        createDataArrays();
    }

    /**
    *  Initializes GPU memory.
    */
    @Override
    protected void initializeGPUMemoryImplementation(GL2 gl)
    {
        createPermanentTextures(gl);
        // init shaders runtime
        expressionDataComputingShaders = new ExpressionDataComputingShaders(gl, isTwoDimensionalExpressionDataConvertedTo2DSquareTextureSizePowerOfTwo);
    }

    /**
    *  Performs the GPU Computing calculations.
    */
    @Override
    protected void performGPUComputingCalculationsImplementation(GL2 gl)
    {
        performComputation(gl);
    }

    /**
    *  Retrieves GPU results.
    */
    @Override
    protected void retrieveGPUResultsImplementation(GL2 gl) throws OutOfMemoryError
    {
        // compareWithCPU();
    }

    /**
    *  Deletes the OpenGL context for GPU computing.
    */
    @Override
    protected void deleteOpenGLContextForGPUComputing(GL2 gl)
    {
        deleteDataArrays();
        expressionDataComputingShaders.destructor(gl);
        deleteTemporaryIndexTextures(gl);
        deletePermamentTextures(gl);
    }


}
