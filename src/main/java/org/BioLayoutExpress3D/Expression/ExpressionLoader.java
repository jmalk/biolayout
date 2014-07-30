package org.BioLayoutExpress3D.Expression;

import java.io.*;
import static java.lang.Math.*;
import org.BioLayoutExpress3D.Analysis.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.CoreUI.Dialogs.*;
import org.BioLayoutExpress3D.Network.*;
import org.BioLayoutExpress3D.Utils.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* @author Anton Enright, full refactoring by Thanos Theo, 2008-2009
* @version 3.0.0.0
*
*/

public final class ExpressionLoader
{
    private File file = null;
    private ExpressionData expressionData = null;
    private LayoutClassSetsManager layoutClassSetsManager = null;
    private String[] rowAnnotationLabels = null;
    private int firstDataColumn = 0;
    private int firstDataRow = 0;
    private boolean transpose = false;
    boolean isSuccessful = false;
    public String reasonForFailure = "";

    public ExpressionLoader(LayoutClassSetsManager layoutClassSetsManager)
    {
        this.layoutClassSetsManager = layoutClassSetsManager;
    }

    public boolean init(File file, ExpressionData expressionData,
            int firstDataColumn, int firstDataRow, boolean transpose)
    {
        this.firstDataColumn = firstDataColumn;
        this.firstDataRow = firstDataRow;
        this.transpose = transpose;
        this.file = file;
        this.expressionData = expressionData;

        return true;
    }

    class ParseProgressIndicator implements TextDelimitedMatrix.ProgressIndicator
    {
        private LayoutProgressBarDialog layoutProgressBarDialog;

        public ParseProgressIndicator(LayoutProgressBarDialog layoutProgressBarDialog)
        {
            this.layoutProgressBarDialog = layoutProgressBarDialog;
        }

        @Override
        public void notify(int percent)
        {
            layoutProgressBarDialog.incrementProgress(percent);
        }
    }

    private void setReasonForFailure(int row, int column, String reason)
    {
        if (row >= 0 && column >= 0)
        {
            reasonForFailure += "At row " + (row + 1) + ", column " + (column + 1) + ":\n";
        }

        reasonForFailure += reason;
    }

    public boolean parse(LayoutFrame layoutFrame)
    {
        LayoutProgressBarDialog layoutProgressBarDialog = layoutFrame.getLayoutProgressBar();

        layoutProgressBarDialog.prepareProgressBar(100, "Reading Expression Data: ");
        layoutProgressBarDialog.startProgressBar();

        ParseProgressIndicator ppi = new ParseProgressIndicator(layoutProgressBarDialog);

        TextDelimitedMatrix tdm;
        int row = -1;
        int column = -1;

        try
        {
            reasonForFailure = "";

            tdm = new TextDelimitedMatrix(file, "\t", ppi);

            layoutProgressBarDialog.setText("Parsing " + tdm.numLines() + " lines");

            if (!tdm.parse())
            {
                return false;
            }

            tdm.setTranspose(transpose);
            int numColumns = tdm.numColumns();
            int numRows = tdm.numRows();

            // - 1 because the first column is always the row ID
            rowAnnotationLabels = new String[firstDataColumn - 1];

            expressionData.initialize(
                    numRows - firstDataRow,
                    numColumns - firstDataColumn,
                    transpose);

            layoutProgressBarDialog.setText("Loading data");

            for (row = 0; row < numRows; row++)
            {
                int percent = (100 * row) / numRows;
                layoutProgressBarDialog.incrementProgress(percent);

                for (column = 0; column < numColumns; column++)
                {
                    String value = tdm.valueAt(column, row);
                    int dataColumn = column - firstDataColumn;
                    int dataRow = row - firstDataRow;

                    if (row == 0)
                    {
                        if (column >= firstDataColumn)
                        {
                            // Data column names
                            expressionData.setColumnName(dataColumn, value);
                        }
                        else if (column >= 1) // First column is always the row ID
                        {
                            // Annotation classes
                            String annotation = cleanString(value);
                            layoutClassSetsManager.createNewClassSet(annotation);
                            rowAnnotationLabels[column - 1] = annotation;
                        }
                    }
                    else if (row < firstDataRow)
                    {
                        if (column == 0)
                        {
                            // Column annotation name
                            String annotation = cleanString(value);
                            expressionData.addColumnAnnotation(row - 1, annotation);
                        }
                        else if (column >= firstDataColumn)
                        {
                            // Column annotation
                            ExpressionData.ColumnAnnotation columnAnnotation =
                                    expressionData.getColumnAnnotationByIndex(row - 1);

                            columnAnnotation.setValue(dataColumn, value);

                        }
                    }
                    else if (row >= firstDataRow)
                    {
                        if (column == 0)
                        {
                            // Row names
                            expressionData.setRowID(dataRow, value);
                        }
                        else if (column >= firstDataColumn)
                        {
                            // The data itself
                            float currentValue = Float.parseFloat(value.replace(',', '.'));
                            expressionData.setExpressionDataValue(dataRow, dataColumn, currentValue);
                        }
                    }
                }
            }

            expressionData.sumRows();
        }
        catch (NumberFormatException nfe)
        {
            if (DEBUG_BUILD)
            {
                println("NumberFormatException in ExpressionLoader.parse():\n" + nfe.getMessage());
            }

            setReasonForFailure(row, column, nfe.toString());
            return false;
        }
        catch (IOException ioe)
        {
            if (DEBUG_BUILD)
            {
                println("IOException in ExpressionLoader.parse():\n" + ioe.getMessage());
            }

            setReasonForFailure(row, column, ioe.toString());
            return false;
        }
        finally
        {
            layoutProgressBarDialog.endProgressBar();
        }

        return true;
    }

    public boolean parseAnnotations(LayoutFrame layoutFrame, NetworkContainer nc)
    {
        LayoutProgressBarDialog layoutProgressBarDialog = layoutFrame.getLayoutProgressBar();

        layoutProgressBarDialog.prepareProgressBar(100, "Reading Expression Data: ");
        layoutProgressBarDialog.startProgressBar();

        ParseProgressIndicator ppi = new ParseProgressIndicator(layoutProgressBarDialog);

        TextDelimitedMatrix tdm;

        int chipGeneCount = 0;

        try
        {
            tdm = new TextDelimitedMatrix(file, "\t", ppi);

            layoutProgressBarDialog.setText("Parsing " + tdm.numLines() + " lines");

            if (!tdm.parse())
            {
                return false;
            }

            tdm.setTranspose(transpose);
            int numColumns = tdm.numColumns();
            int numRows = tdm.numRows();

            layoutProgressBarDialog.setText("Loading annotations");

            for (int row = firstDataRow; row < numRows; row++)
            {
                int percent = (100 * row) / numRows;
                layoutProgressBarDialog.incrementProgress(percent);

                Vertex vertex = null;

                for (int column = 0; column < firstDataColumn; column++)
                {
                    String value = tdm.valueAt(column, row);
                    int dataColumn = column - firstDataColumn;
                    int dataRow = row - firstDataRow;

                    if (column == 0)
                    {
                        vertex = nc.getVerticesMap().get(expressionData.getRowID(dataRow));
                    }
                    else if (vertex != null)
                    {
                        chipGeneCount++;
                        String annotation = cleanString(value);
                        LayoutClasses layoutClasses =
                                layoutClassSetsManager.getClassSetByName(rowAnnotationLabels[column - 1]);

                        if (annotation.isEmpty())
                        {
                            // if the annotation is empty set it to the noclass class
                            layoutClasses.setClass(vertex, 0);
                        }
                        else
                        {
                            VertexClass vc = layoutClasses.createClass(annotation);
                            layoutClasses.setClass(vertex, vc);
                            AnnotationTypeManagerBG.getInstanceSingleton().add(vertex.getVertexName(),
                                    layoutClasses.getClassSetName(), vc.getName());
                        }
                    }
                }
            }
        }
        catch (IOException ioe)
        {
            if (DEBUG_BUILD)
            {
                println("IOException in ExpressionLoader.parseAnnotations():\n" + ioe.getMessage());
            }

            return false;
        }
        finally
        {
            AnnotationTypeManagerBG.getInstanceSingleton().setChipGeneCount(chipGeneCount);
            layoutProgressBarDialog.endProgressBar();
        }

        return true;
    }

    private String cleanString(String string)
    {
        // This is apparently marginally faster than replaceAll("[\"\']", " ")
        return string.replace('\"', ' ').replace('\'', ' ');
    }
}