package org.BioLayoutExpress3D.Expression;

import java.io.*;
import java.nio.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.*;
import static java.lang.Math.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.CoreUI.Dialogs.*;
import org.BioLayoutExpress3D.CPUParallelism.*;
import org.BioLayoutExpress3D.CPUParallelism.Executors.*;
import org.BioLayoutExpress3D.GPUComputing.OpenGLContext.*;
import org.BioLayoutExpress3D.Graph.GraphElements.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.Expression.ExpressionEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* The ExpressionData conveys the core of the correlation data calculations.
*
* @author Anton Enright, code updates/heavy optimizations/modifications/N-Core parallelization support/GPU Computing Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public final class ExpressionData
{
    public class ColumnAnnotation
    {
        private int index;
        private String name;
        private String[] values;

        private int[] sortedColumnMap;

        public ColumnAnnotation(int index, String name, int numColumns)
        {
            this.index = index;
            this.name = name;
            this.values = new String[numColumns];

            sortedColumnMap = null;
        }

        public void setValue(int column, String value)
        {
            values[column] = value;
            sortedColumnMap = null;
        }

        public String getValue(int column)
        {
            return values[column];
        }

        public String getFullyQualifiedValue(int column)
        {
            return getName() + " " + getValue(column);
        }

        public String getName()
        {
            return name;
        }

        class StringValueComparator implements Comparator<Integer>
        {
            Map<Integer, String> base;

            public StringValueComparator(Map<Integer, String> base)
            {
                this.base = base;
            }

            @Override
            public int compare(Integer a, Integer b)
            {
                int stringDiff = base.get(a).compareTo(base.get(b));

                if (stringDiff == 0)
                {
                    return a - b;
                }

                return stringDiff;
            }
        }

        public int[] getSortedColumnMap()
        {
            if (sortedColumnMap == null)
            {
                HashMap<Integer, String> map = new HashMap<Integer, String>();

                for (int i = 0; i < values.length; i++)
                {
                    map.put(i, values[i]);
                }

                StringValueComparator svc = new StringValueComparator(map);
                TreeMap<Integer, String> sortedMap = new TreeMap<Integer, String>(svc);
                sortedMap.putAll(map);

                sortedColumnMap = new int[values.length];

                int i = 0;
                for (Entry<Integer, String> entry : sortedMap.entrySet())
                {
                    sortedColumnMap[i++] = entry.getKey();
                }
            }

            return sortedColumnMap;
        }
    }

    /**
    *  Constant used defining the amount of RAM to be used in the N-Core Parallelization algorithm.
    *  Here, 128Mb (2^27) RAM will be allocated for the float results array.
    */
    private static final int MAX_ARRAY_RAM_USAGE = (1 << 27);

    /**
    *  Constant used defining the max array size for the float results array.
    */
    private static final int MAX_ARRAY_SIZE = MAX_ARRAY_RAM_USAGE / 4;

    public static final int FILE_MAGIC_NUMBER = 0xB73D0004;

    private LayoutFrame layoutFrame = null;
    private LayoutProgressBarDialog layoutProgressBarDialog = null;
    private int rowIndex = 0;
    private long searchSpace = 0;
    private String metricName = "";
    private NumberFormat nf1 = null;
    private NumberFormat nf2 = null;
    private NumberFormat nf3 = null;

    private int totalRows = 0;
    private int totalColumns = 0;
    private boolean transpose = false;
    private String[] columnNamesArray = null;
    private String[] rowIDsArray = null;
    private FloatBuffer sumX_cacheBuffer = null;
    private float[] sumX_cacheArray = null;
    private float[] sumX2_cacheArray = null;
    private FloatBuffer sumX_sumX2_cacheBuffer = null;
    private float[] sumX_sumX2_cacheArray = null;
    private FloatBuffer sumColumns_X2_cacheBuffer = null;
    private float[] sumColumns_X2_cacheArray = null;
    private FloatBuffer expressionDataBuffer = null;
    private float[] expressionDataArray = null;
    private FloatBuffer expressionRanksBuffer = null;
    private float[] expressionRanksArray = null;
    private HashMap<String, Integer> identityMap = null;
    private HashMap<String, Integer> columnNameMap = null;
    private int[][] countsArray = null;

    private float[] minValueCache = null;
    private boolean minValueCached = false;
    private float[] maxValueCache = null;
    private boolean maxValueCached = false;
    private float minValue = Float.MAX_VALUE;
    private float maxValue = Float.MIN_VALUE;
    private float[] stddevCache = null;
    private boolean stddevCached = false;
    private float minStddev = Float.MAX_VALUE;
    private float maxStddev = Float.MIN_VALUE;
    private float[] meanCache = null;
    private boolean meanCached = false;

    private HashMap<Integer, ColumnAnnotation> columnAnnotations;

    // variables needed for N-CP
    private final CyclicBarrierTimer cyclicBarrierTimer = (USE_MULTICORE_PROCESS) ? new CyclicBarrierTimer() : null;
    private final CyclicBarrier threadBarrier = (USE_MULTICORE_PROCESS) ? new CyclicBarrier(NUMBER_OF_AVAILABLE_PROCESSORS + 1, cyclicBarrierTimer) : null;

    /**
    *  The constructor of the ExpressionData class.
    */
    public ExpressionData(LayoutFrame layoutFrame)
    {
        this.layoutFrame = layoutFrame;

        identityMap = new HashMap<String, Integer>();
        columnNameMap = new HashMap<String, Integer>();
        columnAnnotations = new HashMap<Integer, ColumnAnnotation>();
    }

    /**
    *  Initalizes all the data structures.
    */
    public void initialize(int totalRows, int totalColumns, boolean transpose)
    {
        this.totalRows = totalRows;
        this.totalColumns = totalColumns;
        this.transpose = transpose;

        columnNamesArray = new String[totalColumns];
        rowIDsArray = new String[totalRows];
        sumX_cacheBuffer = FloatBuffer.allocate(totalRows);
        sumX_cacheArray = sumX_cacheBuffer.array();
        sumX2_cacheArray = new float[totalRows];
        sumX_sumX2_cacheBuffer = FloatBuffer.allocate(totalRows);
        sumX_sumX2_cacheArray = sumX_sumX2_cacheBuffer.array();
        sumColumns_X2_cacheBuffer = FloatBuffer.allocate(totalRows);
        sumColumns_X2_cacheArray = sumColumns_X2_cacheBuffer.array();
        expressionDataBuffer = FloatBuffer.allocate(totalRows * totalColumns);
        expressionDataArray = expressionDataBuffer.array();

        identityMap.clear();
        columnNameMap.clear();
        columnAnnotations.clear();
        clearCounts();

        minValueCache = null;
        minValueCached = false;
        maxValueCache = null;
        maxValueCached = false;
        minValue = Float.MAX_VALUE;
        maxValue = Float.MIN_VALUE;
        stddevCache = null;
        stddevCached = false;
        minStddev = Float.MAX_VALUE;
        maxStddev = Float.MIN_VALUE;
        meanCache = null;
        meanCached = false;
    }

    public void addColumnAnnotation(int index, String name)
    {
        ColumnAnnotation columnAnnotation = new ColumnAnnotation(index, name, totalColumns);
        columnAnnotations.put(index, columnAnnotation);
    }

    public ColumnAnnotation getColumnAnnotationByIndex(int index)
    {
        if(columnAnnotations.containsKey(index))
        {
            return columnAnnotations.get(index);
        }

        return null;
    }

    public ColumnAnnotation getColumnAnnotationByName(String name)
    {
        for(ColumnAnnotation columnAnnotation : columnAnnotations.values())
        {
            if (columnAnnotation.getName().equals(name))
            {
                return columnAnnotation;
            }
        }

        return null;
    }

    public Collection<ColumnAnnotation> getColumnAnnotations()
    {
        return columnAnnotations.values();
    }

    /**
    *  Converts data to Spearman Rank order.
    */
    private void convertToSpearmanRankOrder()
    {
        expressionRanksBuffer = FloatBuffer.allocate(totalRows * totalColumns);
        expressionRanksArray = expressionRanksBuffer.array();

        for (int i = 0; i < totalRows; i++)
        {
            sumX_cacheArray[i] = 0.0f;
            sumX2_cacheArray[i] = 0.0f;
        }

        float[] rowValues  = new float[totalColumns];
        float[] originalValues = new float[totalColumns];
        HashMap<Float, Float> ranksMap = new HashMap<Float, Float>();
        float value = 0.0f;
        for (int i = 0; i < totalRows; i++)
        {
            ranksMap.clear();

            for (int j = 0; j < totalColumns; j++)
            {
                value = expressionDataArray[i * totalColumns + j];
                rowValues[j] = value;
                originalValues[j] = value;
            }

            Arrays.sort(rowValues);
            PrimitiveArraysUtils.reverse(rowValues);

            float currentRank = 0.0f;
            float tied = 0.0f;
            for (int j = 0; j < totalColumns; j++)
            {
                currentRank++;
                if ( ( j < (totalColumns - 1 ) ) && (rowValues[j] == rowValues[j + 1]) )
                    tied++;
                else
                {
                    ranksMap.put(rowValues[j], (tied == 0.0f) ? currentRank : currentRank - ( currentRank - (currentRank - tied) ) / 2.0f);
                    tied = 0.0f;
                }
            }

            for (int j = 0; j < totalColumns; j++)
               expressionRanksArray[i * totalColumns + j] = ranksMap.get(originalValues[j]);
        }

        // rebuild caches for rank order values, not raw values
        for (int i = 0; i < totalRows; i++)
        {
            for (int j = 0; j < totalColumns; j++)
            {
                value = expressionRanksArray[i * totalColumns + j];
                sumX_cacheArray[i] += value;
                sumX2_cacheArray[i] += (value * value);
            }
        }
    }

    /**
    *  Builds the correlation network.
    */
    public void buildCorrelationNetwork(LayoutProgressBarDialog layoutProgressBarDialog, File correlationFile,
            String metricName, float threshold, boolean writeCorrelationTextFile)
    {
        this.layoutProgressBarDialog = layoutProgressBarDialog;
        this.rowIndex = 0;
        this.searchSpace = (long)totalRows * (long)totalRows; // has to be cast like this so as to not lose the long conversion and result in an overflow after the multiplication
        this.metricName = Character.toUpperCase( metricName.charAt(0) ) + metricName.substring(1);

        this.nf1 = NumberFormat.getNumberInstance();
        this.nf1.setMaximumFractionDigits(0);

        this.nf2 = NumberFormat.getNumberInstance();
        this.nf2.setMaximumFractionDigits(2);

        if ( writeCorrelationTextFile )
        {
            this.nf3 = NumberFormat.getNumberInstance();
            this.nf3.setMaximumFractionDigits(5);
        }

        if ( CURRENT_METRIC.equals(CorrelationTypes.SPEARMAN) )
            convertToSpearmanRankOrder();

        File correlationFileTmp = new File(correlationFile.getAbsolutePath() + ".tmp");
        File correlationFileTextTmp = new File(correlationFile.getAbsolutePath() + ".txt.tmp");
        ObjectOutputStream outOstream = null;
        PrintWriter outPrintWriter = null;

        try
        {
            outOstream = new ObjectOutputStream( new BufferedOutputStream( new FileOutputStream(correlationFileTmp) ) );
            if ( writeCorrelationTextFile )
            {
                outPrintWriter = new PrintWriter(correlationFileTextTmp);
            }

            WEIGHTED_EDGES = true;
            layoutProgressBarDialog.prepareProgressBar(100, "Calculating " + metricName + " Graph:", true);
            layoutProgressBarDialog.startProgressBar();
            layoutProgressBarDialog.setText("Caching...");

            for (int i = 0; i < totalRows; i++)
            {
                sumX_sumX2_cacheArray[i] = (sumX_cacheArray[i] * sumX_cacheArray[i]);
                sumColumns_X2_cacheArray[i] = (totalColumns * sumX2_cacheArray[i]);
            }

            outOstream.writeInt(FILE_MAGIC_NUMBER);

            if (USE_EXRESSION_CORRELATION_CALCULATION_N_CORE_PARALLELISM.get() && USE_MULTICORE_PROCESS)
            {
                calculateStepsAndMemoryAllocatedForNCoreParallelismAndExecuteCorrelationCalculation(threshold,
                        outOstream, outPrintWriter, writeCorrelationTextFile);
            }
            else if (USE_OPENCL_GPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION.get() && OPENCL_GPU_COMPUTING_ENABLED)
            {
                FloatBuffer expressionData = CURRENT_METRIC.equals(CorrelationTypes.PEARSON) ? expressionDataBuffer : ( ( CURRENT_METRIC.equals(CorrelationTypes.SPEARMAN) ) ? expressionRanksBuffer : expressionDataBuffer );
                performOpenCLGPUComputingCorrelationCalculation(expressionData, threshold, outOstream,
                        outPrintWriter, writeCorrelationTextFile);
            }
            else if (USE_GLSL_GPGPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION.get() && USE_SHADERS_PROCESS)
            {
                FloatBuffer expressionData = CURRENT_METRIC.equals(CorrelationTypes.PEARSON) ? expressionDataBuffer : ( ( CURRENT_METRIC.equals(CorrelationTypes.SPEARMAN) ) ? expressionRanksBuffer : expressionDataBuffer );
                performGLSLGPUComputingCorrelationCalculation(expressionData, threshold, outOstream,
                        outPrintWriter, writeCorrelationTextFile);
            }
            else
            {
                if (!USE_MULTICORE_PROCESS)
                {
                    performSingleCoreCorrelationCalculationAndWriteToFile(threshold, outOstream,
                            outPrintWriter, writeCorrelationTextFile);
                }
                else
                {
                    calculateStepsAndMemoryAllocatedForNCoreParallelismAndExecuteCorrelationCalculation(threshold,
                            outOstream, outPrintWriter, writeCorrelationTextFile);
                }
            }

            outOstream.flush();
            if ( writeCorrelationTextFile )
                outPrintWriter.flush();
        }
        catch (IOException ioe)
        {
            if (DEBUG_BUILD) println("IOException in ExpressionData.buildCorrelationNetwork()\n" + ioe.getMessage());
            JOptionPane.showMessageDialog(layoutFrame, "IOException in building the Correlation network\n" + ioe.getMessage(), "Error: IOException in building the Correlation network", JOptionPane.ERROR_MESSAGE);
        }
        finally
        {
            try
            {
                if (outOstream != null) outOstream.close();
            }
            catch (IOException ioe)
            {
                if (DEBUG_BUILD) println("IOException in ExpressionData.buildCorrelationNetwork() closing the outOstream stream\n" + ioe.getMessage());
                JOptionPane.showMessageDialog(layoutFrame, "IOException in closing the Correlation network outOstream stream\n" + ioe.getMessage(), "Error: IOException in closing the Correlation network outOstream stream", JOptionPane.ERROR_MESSAGE);
            }

            if ( writeCorrelationTextFile )
            {
                if (outPrintWriter != null)
                {
                    outPrintWriter.close();
                }
            }

            // good, we are done
            if (!layoutProgressBarDialog.userHasCancelled())
            {
                correlationFileTmp.renameTo(correlationFile);
                if (writeCorrelationTextFile)
                {
                    correlationFileTextTmp.renameTo(new File(correlationFile.getAbsolutePath() + ".txt"));
                }
            }
            else
            {
                correlationFileTmp.delete();
                if (writeCorrelationTextFile)
                {
                    correlationFileTextTmp.delete();
                }
            }

            clearAllCachedDataStructures();
            layoutProgressBarDialog.endProgressBar();
        }
    }

    /**
    *  Calculates the correlation values in a single thread and writes them to a binary file.
    */
    private void performSingleCoreCorrelationCalculationAndWriteToFile(float threshold, ObjectOutputStream outOstream,
            PrintWriter outPrintWriter, boolean writeCorrelationTextFile) throws IOException
    {
        float[] expressionData = CURRENT_METRIC.equals(CorrelationTypes.PEARSON) ? expressionDataArray : ( ( CURRENT_METRIC.equals(CorrelationTypes.SPEARMAN) ) ? expressionRanksArray : expressionDataArray );
        float correlation = 0.0f;
        for (int i = 0; i < totalRows - 1; i++) // last row does not perform any calculations, thus skipped
        {
            updateSingleCoreGUI();

            outOstream.writeInt(i);

            for (int j = (i + 1); j < totalRows; j++)
            {
                correlation = calculateCorrelation(i, j, expressionData);
                if (correlation >= threshold)
                {
                    outOstream.writeInt(j);
                    outOstream.writeFloat(correlation);
                }

                if (writeCorrelationTextFile)
                {
                    outPrintWriter.println(rowIDsArray[i] + "\t" + rowIDsArray[j] + "\t" + nf3.format(correlation));
                }
            }

            outOstream.writeInt(i);
        }
    }

    /**
    *  Calculates the steps needed, memory allocated per step and executes the correlation calculation with N-Core parallelism.
    */
    private void calculateStepsAndMemoryAllocatedForNCoreParallelismAndExecuteCorrelationCalculation(float threshold,
            ObjectOutputStream outOstream, PrintWriter outPrintWriter, boolean writeCorrelationTextFile) throws IOException
    {
        // below is code to break the correlation calculation into steps according to how much memory we are allocating for the intermediate step results
        boolean isPowerOfTwo = org.BioLayoutExpress3D.StaticLibraries.Math.isPowerOfTwo(NUMBER_OF_AVAILABLE_PROCESSORS);
        int arraySize = 0;
        int startRow = 0;
        int endRow = -1; // has to init at -1 for 'startRow = endRow + 1' line, so as to start at row 0
        int stepNumber = 0;
        int[] cachedRowsResultsIndicesToSkip = new int[totalRows - 1];
        float[] stepResults = null;
        boolean rowsSearchProcessedStopped = false;
        while (!rowsSearchProcessedStopped)
        {
            arraySize = 0;
            startRow = endRow + 1;

            // last row does not need to be checked as it does not perform any calculations
            for (int i = startRow; i < totalRows - 1; i++)
            {
                cachedRowsResultsIndicesToSkip[i] = totalRows - (i + 1);
                arraySize += cachedRowsResultsIndicesToSkip[i];

                // stop if MAX_ARRAY_SIZE not enough for all calculations
                if (arraySize >= MAX_ARRAY_SIZE)
                {
                    // if last row to be checked, stop the process
                    if ( i == (totalRows - 2) )
                      rowsSearchProcessedStopped = true;
                    endRow = i;
                    break;
                }

                // if last row to be checked, stop the process
                if ( i == (totalRows - 2) )
                {
                    endRow = i;
                    rowsSearchProcessedStopped = true;
                }
            }

            stepNumber++;
            stepResults = new float[arraySize];

            if (DEBUG_BUILD) println("Now starting the N-Core parallelization process with the variables below:\nstartRow: " + (startRow + 1) + " endRow: " + (endRow + 1) + " arraySize: " + arraySize + " rowsSearchProcessedStopped: " + rowsSearchProcessedStopped);
            performMultiCoreCorrelationCalculation(isPowerOfTwo, startRow, endRow, stepResults, cachedRowsResultsIndicesToSkip);

            if (layoutProgressBarDialog.userHasCancelled())
            {
                break;
            }

            writeAllStepResultsToFile(threshold, startRow, endRow, stepNumber, stepResults,
                    outOstream, outPrintWriter, writeCorrelationTextFile);

            // clean memory before continuing
            stepResults = null;
            System.gc();
        }

        // clean memory at end of all calculations
        stepResults = null;
        cachedRowsResultsIndicesToSkip = null;
        System.gc();
    }

    /**
    *  Main method of the correlation calculation execution code. Uses an N-Core paralellism algorithm in case of multiple core availability.
    */
    private void performMultiCoreCorrelationCalculation(boolean isPowerOfTwo, int startRow, int endRow, float[] stepResults, int[] cachedRowsResultsIndicesToSkip)
    {
        LoggerThreadPoolExecutor executor = new LoggerThreadPoolExecutor(NUMBER_OF_AVAILABLE_PROCESSORS, NUMBER_OF_AVAILABLE_PROCESSORS, 0L, TimeUnit.MILLISECONDS,
                                                                         new LinkedBlockingQueue<Runnable>(NUMBER_OF_AVAILABLE_PROCESSORS),
                                                                         new LoggerThreadFactory("ExpressionData"),
                                                                         new ThreadPoolExecutor.CallerRunsPolicy() );

        cyclicBarrierTimer.clear();
        for (int threadId = 0; threadId < NUMBER_OF_AVAILABLE_PROCESSORS; threadId++)
            executor.execute( correlationCalculationProcessKernel(threadId, isPowerOfTwo, startRow, endRow, stepResults, cachedRowsResultsIndicesToSkip) );

        try
        {
            threadBarrier.await(); // wait for all threads to be ready
            threadBarrier.await(); // wait for all threads to finish
            executor.shutdown();
        }
        catch (BrokenBarrierException ex)
        {
            if (DEBUG_BUILD) println("Problem with a broken barrier with the main correlation calculation thread in performMultiCoreCorrelationCalculation()!:\n" + ex.getMessage());
        }
        catch (InterruptedException ex)
        {
            // restore the interuption status after catching InterruptedException
            Thread.currentThread().interrupt();
            if (DEBUG_BUILD) println("Problem with pausing the main correlation calculation thread in performMultiCoreCorrelationCalculation()!:\n" + ex.getMessage());
        }

        if (DEBUG_BUILD) println("\nTotal ExpressionData N-CP run time: " + (cyclicBarrierTimer.getTime() / 1e6) + " ms.\n");
    }

    /**
    *  Performs all correlation calculations.
    */
    private void allCorrelationCalculations(int threadId, boolean isPowerOfTwo, int startRow, int endRow, float[] stepResults, int[] cachedRowsResultsIndicesToSkip)
    {
        float[] expressionData = CURRENT_METRIC.equals(CorrelationTypes.PEARSON) ? expressionDataArray : ( ( CURRENT_METRIC.equals(CorrelationTypes.SPEARMAN) ) ? expressionRanksArray : expressionDataArray );
        int rowResultIndex = 0;
        if (isPowerOfTwo)
        {
            for (int i = startRow; i <= endRow; i++)
            {
                if ( ( i & (NUMBER_OF_AVAILABLE_PROCESSORS - 1) ) == threadId )
                {
                    updateMultiCoreGUI();

                    for (int j = (i + 1); j < totalRows; j++)
                        stepResults[rowResultIndex++] = calculateCorrelation(i, j, expressionData);
                }
                else
                {
                    rowResultIndex += cachedRowsResultsIndicesToSkip[i];
                }

                if (layoutProgressBarDialog.userHasCancelled())
                {
                    break;
                }
            }
        }
        else
        {
            for (int i = startRow; i <= endRow; i++)
            {
                if ( (i % NUMBER_OF_AVAILABLE_PROCESSORS) == threadId )
                {
                    updateMultiCoreGUI();

                    for (int j = (i + 1); j < totalRows; j++)
                        stepResults[rowResultIndex++] = calculateCorrelation(i, j, expressionData);
                }
                else
                {
                    rowResultIndex += cachedRowsResultsIndicesToSkip[i];
                }

                if (layoutProgressBarDialog.userHasCancelled())
                {
                    break;
                }
            }
        }
    }

    /**
    *   Return a light-weight runnable using the Adapter technique for the correlation calculation so as to avoid any load latencies.
    *   The coding style simulates an OpenCL/CUDA kernel.
    */
    private Runnable correlationCalculationProcessKernel(final int threadId, final boolean isPowerOfTwo, final int startRow, final int endRow, final float[] stepResults, final int[] cachedRowsResultsIndicesToSkip)
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
                        allCorrelationCalculations(threadId, isPowerOfTwo, startRow, endRow, stepResults, cachedRowsResultsIndicesToSkip);
                    }
                    finally
                    {
                        threadBarrier.await();
                    }
                }
                catch (BrokenBarrierException ex)
                {
                    if (DEBUG_BUILD) println("Problem with a broken barrier with the N-Core thread with threadId " + threadId + " in correlationCalculationProcessKernel()!:\n" + ex.getMessage());
                }
                catch (InterruptedException ex)
                {
                    // restore the interuption status after catching InterruptedException
                    Thread.currentThread().interrupt();
                    if (DEBUG_BUILD) println("Problem with pausing the N-Core thread with threadId " + threadId + " in correlationCalculationProcessKernel()!:\n" + ex.getMessage());
                }
            }


        };
    }

    /**
    *  Updates the GUI for the single core correlation calculation.
    */
    private void updateSingleCoreGUI()
    {
        double calculation = ( (double)(++rowIndex) * (double)totalRows ); // has to be cast like this so as to not lose the double conversion and result in an overflow after the multiplication
        double percent = 100.0 * (calculation / searchSpace);

        layoutProgressBarDialog.incrementProgress( (int)percent );
        layoutProgressBarDialog.setText("Done " + nf1.format(calculation) + " " + metricName + " calculations (" + createProgressBarTextValue( percent, nf2.format(percent) ) + "%)");
    }

    /**
    *  Updates the GUI for the correlation calculation iterations.
    */
    private void updateMultiCoreGUI()
    {
        double calculation = ( (double)(++rowIndex) * (double)totalRows ); // has to be cast like this so as to not lose the double conversion and result in an overflow after the multiplication
        double percent = 100.0 * (calculation / searchSpace);

        layoutProgressBarDialog.incrementProgress( (int)percent );
        layoutProgressBarDialog.setText("Done " + nf1.format(calculation) + " " + metricName + " calculations (" + createProgressBarTextValue( percent, nf2.format(percent) ) + "%)" +
                                        "  (Utilizing " + NUMBER_OF_AVAILABLE_PROCESSORS + "-Core Parallelism)");
    }

    /**
    *  Creates the progress bar's text value.
    */
    private String createProgressBarTextValue(double percent, String progressBarText)
    {
        int addCheckIndex = ( (percent < 10.0) ? 0 : 1 );
        if ( progressBarText.length() == (3 + addCheckIndex) )
            return (progressBarText + "0");
        else if ( progressBarText.length() == (1 + addCheckIndex) )
            return (progressBarText + DECIMAL_SEPARATOR_STRING + "00");
        else
            return progressBarText;
    }

    /**
    *  Writes all step results to a binary file.
    */
    private void writeAllStepResultsToFile(float threshold, int startRow, int endRow,
            int stepNumber, float[] stepResults, ObjectOutputStream outOstream,
            PrintWriter outPrintWriter, boolean writeCorrelationTextFile) throws IOException
    {
        String currentLayoutProgressBarText = layoutProgressBarDialog.getText();
        currentLayoutProgressBarText = currentLayoutProgressBarText.substring(1,
                currentLayoutProgressBarText.indexOf(")") + 1);

        int index = 0;
        float correlation = 0.0f;
        for (int i = startRow; i <= endRow; i++)
        {
            int percent = ((i - startRow) * 100) / (endRow - startRow);

            outOstream.writeInt(i);

            for (int j = (i + 1); j < totalRows; j++)
            {
                correlation = stepResults[index++];
                if (correlation >= threshold)
                {
                    outOstream.writeInt(j);
                    outOstream.writeFloat(correlation);
                }

                if (writeCorrelationTextFile)
                {
                    outPrintWriter.println(rowIDsArray[i] + "\t" + rowIDsArray[j] +
                            "\t" + nf3.format(correlation));
                }
            }

            outOstream.writeInt(i);

            layoutProgressBarDialog.setText(currentLayoutProgressBarText +
                    "  (Saving " + percent + "%)");
        }
    }

    /**
    *  Main method of the OpenCL GPU correlation calculation data parallel execution code.
    */
    private void performOpenCLGPUComputingCorrelationCalculation(FloatBuffer expressionBuffer, float threshold,
            ObjectOutputStream outOstream, PrintWriter outPrintWriter, boolean writeCorrelationTextFile) throws IOException
    {
        org.BioLayoutExpress3D.GPUComputing.OpenCLContext.ExpressionData.ExpressionDataComputing expressionDataComputingContext = new org.BioLayoutExpress3D.GPUComputing.OpenCLContext.ExpressionData.ExpressionDataComputing( layoutFrame, true, COMPARE_GPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION_WITH_CPU.get() );
        expressionDataComputingContext.initializeExpressionDataComputingVariables(this, layoutProgressBarDialog, nf1, nf2, nf3, totalRows, totalColumns, rowIDsArray, sumX_cacheBuffer, sumX_sumX2_cacheBuffer, sumColumns_X2_cacheBuffer, expressionBuffer, threshold, outOstream, outPrintWriter, EXPRESSION_DATA_GPU_COMPUTING_MAX_ERROR_THRESHOLD);
        expressionDataComputingContext.startGPUComputingProcessing();

        // CPU fail-safe mechanism if OpenCL GPU Computing fails for some reason
        if ( expressionDataComputingContext.getErrorOccured() )
        {
            if (USE_MULTICORE_PROCESS)
            {
                calculateStepsAndMemoryAllocatedForNCoreParallelismAndExecuteCorrelationCalculation(threshold,
                        outOstream, outPrintWriter, writeCorrelationTextFile);
            }
            else
            {
                performSingleCoreCorrelationCalculationAndWriteToFile(threshold, outOstream,
                        outPrintWriter, writeCorrelationTextFile);
            }
        }

        expressionDataComputingContext = null;
    }

    /**
    *  Main method of the GLSL GPU correlation calculation data parallel execution code.
    */
    private void performGLSLGPUComputingCorrelationCalculation(FloatBuffer expressionBuffer, float threshold,
            ObjectOutputStream outOstream, PrintWriter outPrintWriter, boolean writeCorrelationTextFile) throws IOException
    {
        // MAX_ALLOWED_TEXTURE_SIZE = 3584
        // TEXTURE_2D_ARB_R_32, MAX_ALLOWED_TEXTURE_SIZE, 14182, 52
        // TEXTURE_2D_ARB_R_32, MAX_ALLOWED_TEXTURE_SIZE, 26182, 92
        // TEXTURE_2D_ARB_R_32, MAX_ALLOWED_TEXTURE_SIZE, 36182, 122
        //
        // MAX_ALLOWED_TEXTURE_SIZE = 1536
        // TEXTURE_2D_ARB_RGBA_32, MAX_ALLOWED_TEXTURE_SIZE, 14182, 52
        // TEXTURE_2D_ARB_RGBA_32, MAX_ALLOWED_TEXTURE_SIZE, 26182, 92
        // TEXTURE_2D_ARB_RGBA_32, MAX_ALLOWED_TEXTURE_SIZE, 36182, 122
        //
        //
        // MAX_ALLOWED_TEXTURE_SIZE = 3584
        // TEXTURE_RECTANGLE_ARB_R_32, MAX_ALLOWED_TEXTURE_SIZE, 14182, 52
        // TEXTURE_RECTANGLE_ARB_R_32, MAX_ALLOWED_TEXTURE_SIZE, 26182, 92
        // TEXTURE_RECTANGLE_ARB_R_32, MAX_ALLOWED_TEXTURE_SIZE, 36182, 122
        //
        // MAX_ALLOWED_TEXTURE_SIZE = 1536
        // TEXTURE_RECTANGLE_ARB_RGBA_32, MAX_ALLOWED_TEXTURE_SIZE, 14182, 52
        // TEXTURE_RECTANGLE_ARB_RGBA_32, MAX_ALLOWED_TEXTURE_SIZE, 26182, 92
        // TEXTURE_RECTANGLE_ARB_RGBA_32, MAX_ALLOWED_TEXTURE_SIZE, 36182, 122

        int textureSize = GLSL_GPGPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION_TEXTURE_SIZE.get();
        AllTextureParameters.TextureParameters textureParameters = AllTextureParameters.TEXTURE_RECTANGLE_ARB_R_32;
        String textureTypeString = GLSL_GPGPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION_TEXTURE_TYPE.get();
        if ( textureTypeString.equals( GLSLTextureTypes.TEXTURE_RECTANGLE_ARB_R_32.toString() ) )
            textureParameters = AllTextureParameters.TEXTURE_RECTANGLE_ARB_R_32;
        else if ( textureTypeString.equals( GLSLTextureTypes.TEXTURE_2D_ARB_R_32.toString() ) )
            textureParameters = AllTextureParameters.TEXTURE_2D_ARB_R_32;
        else if ( textureTypeString.equals( GLSLTextureTypes.TEXTURE_RECTANGLE_ARB_RGBA_32.toString() ) )
        {
            textureParameters = AllTextureParameters.TEXTURE_RECTANGLE_ARB_RGBA_32;
            textureSize /= 2;
        }
        else if ( textureTypeString.equals( GLSLTextureTypes.TEXTURE_2D_ARB_RGBA_32.toString() ) )
        {
            textureParameters = AllTextureParameters.TEXTURE_2D_ARB_RGBA_32;
            textureSize /= 2;
        }

        org.BioLayoutExpress3D.GPUComputing.OpenGLContext.ExpressionData.ExpressionDataComputing expressionDataComputingContext = new org.BioLayoutExpress3D.GPUComputing.OpenGLContext.ExpressionData.ExpressionDataComputing(textureParameters, textureSize, totalRows, totalColumns, true);
        expressionDataComputingContext.initializeExpressionDataComputingVariables(this, layoutProgressBarDialog, nf1, nf2, nf3, rowIDsArray, sumX_cacheBuffer, sumX_sumX2_cacheBuffer, sumColumns_X2_cacheBuffer, expressionBuffer, threshold, outOstream, outPrintWriter, EXPRESSION_DATA_GPU_COMPUTING_MAX_ERROR_THRESHOLD);
        expressionDataComputingContext.doGPUComputingProcessing();

        // CPU fail-safe mechanism if GLSL GPU Computing fails for some reason
        if ( expressionDataComputingContext.getErrorOccured() )
        {
            if (USE_MULTICORE_PROCESS)
            {
                calculateStepsAndMemoryAllocatedForNCoreParallelismAndExecuteCorrelationCalculation(threshold,
                        outOstream, outPrintWriter, writeCorrelationTextFile);
            }
            else
            {
                performSingleCoreCorrelationCalculationAndWriteToFile(threshold, outOstream,
                        outPrintWriter, writeCorrelationTextFile);
            }
        }

        expressionDataComputingContext = null;
    }

    /**
    *  Calculates the correlation value.
    */
    public float calculateCorrelation(int firstRow, int secondRow, float[] matrix)
    {
        float denominator = (float)sqrt( (sumColumns_X2_cacheArray[firstRow] - sumX_sumX2_cacheArray[firstRow]) * (sumColumns_X2_cacheArray[secondRow] - sumX_sumX2_cacheArray[secondRow]) );
        if ( (denominator != 0.0f) && !(denominator != denominator) ) // second check is to avoid an NaN problem, see definition of Float.isNaN()
        {
            int indexFirstRowDimension = firstRow * totalColumns;
            int indexSecondRowDimension = secondRow * totalColumns;
            float sumXY = 0.0f;
            for (int i = 0; i < totalColumns; i++)
                sumXY += (matrix[indexFirstRowDimension + i] * matrix[indexSecondRowDimension + i]);

            float result = ( (totalColumns * sumXY) - (sumX_cacheArray[firstRow] * sumX_cacheArray[secondRow]) ) / denominator;
            return (result > 1.0f) ? 1.0f : ( (result < -1.0f) ? -1.0f : result );
        }
        else
            return -1.0f;
    }

    /**
    *  Clears all the cached data structures.
    */
    private void clearAllCachedDataStructures()
    {
        sumX_cacheBuffer.clear();
        sumX_cacheBuffer = null;
        sumX_cacheArray = null;
        sumX2_cacheArray = null;
        sumX_sumX2_cacheBuffer.clear();
        sumX_sumX2_cacheBuffer = null;
        sumX_sumX2_cacheArray = null;
        sumColumns_X2_cacheBuffer.clear();
        sumColumns_X2_cacheBuffer = null;
        sumColumns_X2_cacheArray = null;
        if (expressionRanksBuffer != null)
        {
            expressionRanksBuffer.clear();
            expressionRanksBuffer = null;
            expressionRanksArray = null;
        }

        nf1 = null;
        nf2 = null;
        nf3 = null;

        System.gc();
    }

    public void sumRows()
    {
        for (int row = 0; row < totalRows; row++)
        {
            sumX_cacheArray[row] = 0.0f;
            sumX2_cacheArray[row] = 0.0f;

            for (int column = 0; column < totalColumns; column++)
            {
                float value = getExpressionDataValue(row, column);

                sumX_cacheArray[row] += value;
                sumX2_cacheArray[row] += (value * value);
            }
        }
    }

    /**
    *  Gets the countsArray data structure.
    */
    public int[][] getCounts()
    {
        return countsArray;
    }

    /**
    *  Clears the countsArray data structure.
    */
    public int[][] clearCounts()
    {
        countsArray = new int[totalRows][101];
        return countsArray;
    }

    /**
    *  Gets a value from the expression data structure.
    */
    public float getExpressionDataValue(int i, int j)
    {
        return expressionDataArray[i * totalColumns + j];
    }

    /**
    *  Sets a value to the expression data structure.
    */
    public void setExpressionDataValue(int i, int j, float value)
    {
        expressionDataArray[i * totalColumns + j] = value;
    }

    /**
    *  Finds the max value from the expression data array.
    */
    public float findGlobalMaxValueFromExpressionDataArray()
    {
        float maxValue = Float.MIN_VALUE;
        for (int i = 0; i < expressionDataArray.length; i++)
            if (maxValue < expressionDataArray[i])
                maxValue = expressionDataArray[i];

        return maxValue;
    }

    /**
    *  Finds the local (per-node) max values from the expression data array.
    */
    public float[] findLocalMaxValuesFromExpressionDataArray(Collection<GraphNode> allGraphNodes)
    {
        float[] localMaxValues = new float[allGraphNodes.size()];
        float localMaxValue = 0.0f;
        float tempValue = 0.0f;
        int index = 0;
        for (GraphNode node : allGraphNodes)
        {
            localMaxValue = Float.MIN_VALUE;
            index = getIdentityMap( node.getNodeName() );
            for (int currentTick = 0; currentTick < getTotalColumns(); currentTick++)
            {
                tempValue = getExpressionDataValue(index, currentTick);
                if (localMaxValue < tempValue)
                    localMaxValue = tempValue;
            }
            localMaxValues[node.getNodeID()] = localMaxValue;
        }

        return localMaxValues;
    }

    /**
    *  Gets the row ID data structure.
    */
    public String getRowID(int index)
    {
        return rowIDsArray[index];
    }

    private String uniqueID(String id)
    {
        String originalId = id;
        int collisionAvoidanceSuffix = 1;
        while (identityMap.containsKey(id))
        {
            id = originalId + "." + collisionAvoidanceSuffix;
            collisionAvoidanceSuffix++;
        }

        return id;
    }

    /**
    *  Sets the row ID data structure.
    */
    public void setRowID(int index, String id)
    {
        id = uniqueID(id);
        rowIDsArray[index] = id;
        identityMap.put(id, index);
    }

    /**
    *  Gets the identityMap data structure.
    */
    public int getIdentityMap(String key)
    {
        Integer value = identityMap.get(key);
        return (value == null) ? 0 : value;
    }

    /**
    *  Gets a column name by a given index.
    */
    public String getColumnName(int index)
    {
        return columnNamesArray[index];
    }

    private String uniqueColumnName(String name)
    {
        String originalId = name;
        int collisionAvoidanceSuffix = 1;
        while (columnNameMap.containsKey(name))
        {
            name = originalId + "." + collisionAvoidanceSuffix;
            collisionAvoidanceSuffix++;
        }

        return name;
    }

    /**
    *  Sets a column name by a given index.
    */
    public void setColumnName(int index, String name)
    {
        name = uniqueColumnName(name);
        columnNamesArray[index] = name;
        columnNameMap.put(name, index);
    }

    /**
    *  Gets the total rows.
    */
    public int getTotalRows()
    {
        return totalRows;
    }

    /**
    *  Gets the total columns.
    */
    public int getTotalColumns()
    {
        return totalColumns;
    }

    public boolean isTransposed()
    {
        return transpose;
    }

    public float getMaxValueForRow(int row)
    {
        if (maxValueCached)
        {
            return maxValueCache[row];
        }

        float value = Float.MIN_VALUE;
        for (int column = 0; column < totalColumns; column++)
        {
            value = java.lang.Math.max(value, getExpressionDataValue(row, column));
        }

        return value;
    }

    public void cacheMaxValues()
    {
        if (maxValueCached)
        {
            return;
        }

        maxValueCache = new float[totalRows];

        for (int row = 0; row < totalRows; row++)
        {
            maxValueCache[row] = getMaxValueForRow(row);

            if (maxValueCache[row] > maxValue)
            {
                maxValue = maxValueCache[row];
            }
        }

        maxValueCached = true;
    }

    public float getMaxValue()
    {
        cacheMaxValues();
        return maxValue;
    }

    public float getMinValueForRow(int row)
    {
        if (minValueCached)
        {
            return minValueCache[row];
        }

        float value = Float.MAX_VALUE;
        for (int column = 0; column < totalColumns; column++)
        {
            value = java.lang.Math.min(value, getExpressionDataValue(row, column));
        }

        return value;
    }

    public void cacheMinValues()
    {
        if (minValueCached)
        {
            return;
        }

        minValueCache = new float[totalRows];

        for (int row = 0; row < totalRows; row++)
        {
            minValueCache[row] = getMinValueForRow(row);

            if (minValueCache[row] < minValue)
            {
                minValue = minValueCache[row];
            }
        }

        minValueCached = true;
    }

    public float getMinValue()
    {
        cacheMinValues();
        return minValue;
    }

    private TransformType transformType;
    public void setTransformType(TransformType transformType)
    {
        this.transformType = transformType;
    }

    private ColumnAnnotation sortColumnAnnotation;
    public void setSortColumnAnnotation(String sortColumnAnnotationName)
    {
        if (sortColumnAnnotationName != null)
        {
            sortColumnAnnotation = getColumnAnnotationByName(sortColumnAnnotationName);
        }
        else
        {
            sortColumnAnnotation = null;
        }
    }

    public float getIQRForRow(int row)
    {
        float[] values = new float[totalColumns];
        for (int column = 0; column < totalColumns; column++)
        {
            values[column] = getExpressionDataValue(row, column);
        }

        Arrays.sort(values);

        int _25Column = (int)java.lang.Math.round(totalColumns * 0.25);
        int _75Column = (int)java.lang.Math.round(totalColumns * 0.75);
        float iqr = values[_75Column] - values[_25Column];

        return iqr;
    }

    public float getMeanForRow(int row)
    {
        if (meanCached)
        {
            return meanCache[row];
        }

        float rowSum = 0.0f;

        for (int column = 0; column < totalColumns; column++)
        {
            rowSum += getExpressionDataValue(row, column);
        }

        return rowSum / totalColumns;
    }

    public void cacheMeanValues()
    {
        if (meanCached)
        {
            return;
        }

        meanCache = new float[totalRows];

        for (int row = 0; row < totalRows; row++)
        {
            meanCache[row] = getMeanForRow(row);
        }

        meanCached = true;
    }

    public float getVarianceForRow(int row, float mean)
    {
        float variance = 0.0f;
        for (int column = 0; column < totalColumns; column++)
        {
            float x = getExpressionDataValue(row, column);
            variance += ((x - mean) * (x - mean));
        }

        return variance / totalColumns;
    }

    public float getVarianceForRow(int row)
    {
        return getVarianceForRow(row, getMeanForRow(row));
    }

    public float getStddevForRow(int row)
    {
        if (stddevCached)
        {
            return stddevCache[row];
        }

        return (float)sqrt(getVarianceForRow(row));
    }

    public void cacheStddevValues()
    {
        if (stddevCached)
        {
            return;
        }

        stddevCache = new float[totalRows];

        for (int row = 0; row < totalRows; row++)
        {
            stddevCache[row] = getStddevForRow(row);

            if (stddevCache[row] < minStddev)
            {
                minStddev = stddevCache[row];
            }

            if (stddevCache[row] > maxStddev)
            {
                maxStddev = stddevCache[row];
            }
        }

        stddevCached = true;
    }

    public float getCoefficientOfVariationForRow(int row)
    {
        return getStddevForRow(row) / getMeanForRow(row);
    }

    public float getMinStddev()
    {
        cacheStddevValues();
        return minStddev;
    }

    public float getMaxStddev()
    {
        cacheStddevValues();
        return maxStddev;
    }

    public float getParetoForRow(int row)
    {
        return (float)sqrt(getStddevForRow(row));
    }

    public float[] getTransformedRow(int row)
    {
        float[] out = new float[totalColumns];

        float mean = getMeanForRow(row);
        float variance = getVarianceForRow(row, mean);

        float stddev = (float)sqrt(variance);
        float pareto = (float)sqrt(stddev);

        int[] sortedColumnMap = null;

        if (sortColumnAnnotation != null)
        {
            sortedColumnMap = sortColumnAnnotation.getSortedColumnMap();
        }

        for (int column = 0; column < totalColumns; column++)
        {
            float value;

            if (sortedColumnMap != null)
            {
                value = getExpressionDataValue(row, sortedColumnMap[column]);
            }
            else
            {
                value = getExpressionDataValue(row, column);
            }

            switch (transformType)
            {
                default:
                case RAW:
                    break;

                case LOG_SCALE:
                    value = (float) java.lang.Math.log(value);
                    break;

                case MEAN_CENTRED:
                    value = value - mean;
                    break;

                case UNIT_VARIANCE_SCALED:
                    value = (value - mean) / stddev;
                    break;

                case PARETO_SCALED:
                    value = (value - mean) / pareto;
                    break;
            }

            out[column] = value;
        }

        return out;
    }

    public float[] getMeanForRows(List<Integer> rows)
    {
        float mean[] = new float[totalColumns];
        for (int index = 0; index < rows.size(); index++)
        {
            float row[] = getTransformedRow(rows.get(index));

            for (int column = 0; column < totalColumns; column++)
            {
                float x = row[column];
                mean[column] += x / rows.size();
            }
        }

        return mean;
    }

    public float[] getVarianceForRows(List<Integer> rows, float[] mean)
    {
        float variance[] = new float[totalColumns];
        for (int index = 0; index < rows.size(); index++)
        {
            float row[] = getTransformedRow(rows.get(index));

            for (int column = 0; column < totalColumns; column++)
            {
                float x = row[column];
                variance[column] += ((x - mean[column]) * (x - mean[column])) / rows.size();
            }
        }

        return variance;
    }

    public float[] getVarianceForRows(List<Integer> rows)
    {
        return getVarianceForRows(rows, getMeanForRows(rows));
    }

    public float[] getStddevForRows(List<Integer> rows)
    {
        float variance[] = getVarianceForRows(rows);
        float stddev[] = new float[totalColumns];
        for (int column = 0; column < totalColumns; column++)
        {
            stddev[column] = (float)sqrt(variance[column]);
        }

        return stddev;
    }

    public float[] getStderrForRows(List<Integer> rows)
    {
        float stddev[] = getStddevForRows(rows);
        float stderr[] = new float[totalColumns];
        float sqrtOfSampleSize = (float)sqrt(totalColumns);
        for (int column = 0; column < totalColumns; column++)
        {
            stderr[column] = (float)stddev[column] / sqrtOfSampleSize;
        }

        return stderr;
    }

    public float[] getParetoForRows(List<Integer> rows)
    {
        float stddev[] = getStddevForRows(rows);
        float pareto[] = new float[totalColumns];
        for (int column = 0; column < totalColumns; column++)
        {
            stddev[column] = (float)sqrt(stddev[column]);
        }

        return pareto;
    }

    interface IRescaleDelegate { public float f(float x); }
    class RescaleLog2 implements IRescaleDelegate
    {
        @Override public float f(float x) { return (float)(log(x) / log(2)); }
    }

    class RescaleLog10 implements IRescaleDelegate
    {
        @Override public float f(float x) { return (float)(log(x) / log(10)); }
    }

    class RescaleAntiLog2 implements IRescaleDelegate
    {
        @Override public float f(float x) { return (float)pow(2.0, x); }
    }

    class RescaleAntiLog10 implements IRescaleDelegate
    {
        @Override public float f(float x) { return (float)pow(10.0, x); }
    }

    private void rescale(LayoutProgressBarDialog layoutProgressBarDialog, IRescaleDelegate d)
    {
        for (int row = 0; row < totalRows; row++)
        {
            for (int column = 0; column < totalColumns; column++)
            {
                float value = getExpressionDataValue(row, column);
                value = d.f(value);

                if (value == Float.POSITIVE_INFINITY)
                {
                    value = Float.MAX_VALUE;
                }
                else if (value == Float.NEGATIVE_INFINITY)
                {
                    value = Float.MIN_VALUE;
                }
                else if (Float.isNaN(value))
                {
                    value = 0.0f;
                }

                setExpressionDataValue(row, column, value);
            }

            int percent = (100 * row) / totalRows;
            layoutProgressBarDialog.incrementProgress(percent);
        }
    }

    public HashSet<Integer> filterMinValue(float minValue)
    {
        cacheMaxValues();
        HashSet<Integer> filtered = new HashSet<Integer>();

        for (int row = 0; row < totalRows; row++)
        {
            if (getMaxValueForRow(row) < minValue)
            {
                filtered.add(row);
            }
        }

        return filtered;
    }

    public HashSet<Integer> filterMinStddev(float minStddev)
    {
        cacheStddevValues();
        HashSet<Integer> filtered = new HashSet<Integer>();

        for (int row = 0; row < totalRows; row++)
        {
            if (getStddevForRow(row) < minStddev)
            {
                filtered.add(row);
            }
        }

        return filtered;
    }

    public HashSet<Integer> filterMinCoefficientOfVariation(float minCoefVar)
    {
        cacheMeanValues();
        cacheStddevValues();
        HashSet<Integer> filtered = new HashSet<Integer>();

        for (int row = 0; row < totalRows; row++)
        {
            if (getCoefficientOfVariationForRow(row) < minCoefVar)
            {
                filtered.add(row);
            }
        }

        return filtered;
    }

    public void preprocess(LayoutProgressBarDialog layoutProgressBarDialog,
            ScaleTransformType scaleTransformType)
    {
        layoutProgressBarDialog.prepareProgressBar(100, "Preprocessing");
        layoutProgressBarDialog.startProgressBar();

        switch (scaleTransformType)
        {
            default:
            case NONE:
                break;

            case LOG2:
                rescale(layoutProgressBarDialog, new RescaleLog2());
                break;

            case LOG10:
                rescale(layoutProgressBarDialog, new RescaleLog10());
                break;

            case ANTILOG2:
                rescale(layoutProgressBarDialog, new RescaleAntiLog2());
                break;

            case ANTILOG10:
                rescale(layoutProgressBarDialog, new RescaleAntiLog10());
                break;
        }

        layoutProgressBarDialog.setText("Summing");
        sumRows();

        layoutProgressBarDialog.endProgressBar();
    }
}