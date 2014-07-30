package org.BioLayoutExpress3D.Expression.Dialogs;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import java.util.Stack;
import org.BioLayoutExpress3D.Utils.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.Expression.ExpressionEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;
import org.BioLayoutExpress3D.StaticLibraries.Utils;

/**
*
* @author Anton Enright, full refactoring by Thanos Theo, 2008-2009
* @version 3.0.0.0
*
*/

public final class ExpressionLoaderDialog extends JDialog implements ActionListener
{
    /**
    *  Serial version UID variable for the ExpressionLoaderDialog class.
    */
    public static final long serialVersionUID = 111222333444555706L;

    private FloatNumberField correlationField = null;
    private JComboBox<String> firstDataColumn = null;
    private JComboBox<String> firstDataRow = null;
    private JComboBox<String> correlationMetric = null;
    private JCheckBox transposeCheckBox = null;
    private JComboBox<String> scaleTransformComboBox = null;
    private JEditorPane textArea = null;
    private JCheckBox saveCorrelationTextFileCheckBox = null;
    private File expressionFile = null;

    private boolean proceed = false;

    private AbstractAction okAction = null;
    private AbstractAction cancelAction = null;
    private AbstractAction transposeChangedAction = null;

    private boolean creatingDialogElements = false;

    private class DataRect
    {
        public int x;
        public int y;
        public int width;
        public int height;

        public int getArea()
        {
            return width * height;
        }
    }
    private DataRect dataRect;

    public ExpressionLoaderDialog(JFrame frame, File expressionFile)
    {
        super(frame, "Load Expression Data", true);

        this.expressionFile = expressionFile;
        this.dataRect = new DataRect();

        initActions(frame);
        initComponents();
        createDialogElements( transposeCheckBox.isSelected(), true );

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }

    private JPanel generalTab()
    {
        JPanel tab = new JPanel(true);
        JPanel tabLine1 = new JPanel();
        JPanel tabLine2 = new JPanel();

        // Minimum correlation
        correlationField = new FloatNumberField(0, 5);
        correlationField.setDocument( new TextFieldFilter(TextFieldFilter.FLOAT) );
        correlationField.setValue(STORED_CORRELATION_THRESHOLD);
        correlationField.setToolTipText("Minimum Correlation");
        tabLine1.add(new JLabel("Minimum Correlation:"));
        tabLine1.add(correlationField);

        // Correlation metric
        correlationMetric = new JComboBox<String>();
        for (CorrelationTypes type : CorrelationTypes.values())
        {
            String s = Utils.titleCaseOf(type.toString());
            correlationMetric.addItem(s);
        }
        correlationMetric.setSelectedIndex(0);
        correlationMetric.setToolTipText("Correlation Metric");
        tabLine1.add(new JLabel("Correlation Metric:"));
        tabLine1.add(correlationMetric);

        // Save text file
        saveCorrelationTextFileCheckBox = new JCheckBox();
        saveCorrelationTextFileCheckBox.setText("Save Cache As Text File");
        tabLine1.add(saveCorrelationTextFileCheckBox);

        // Data bounds
        firstDataColumn = new JComboBox<String>();
        firstDataColumn.addActionListener(this);
        firstDataColumn.setToolTipText("First Data Column");
        tabLine2.add(new JLabel("First Data Column:"));
        tabLine2.add(firstDataColumn);
        firstDataRow = new JComboBox<String>();
        firstDataRow.addActionListener(this);
        firstDataRow.setToolTipText("First Data Row");
        tabLine2.add(new JLabel("First Data Row:"));
        tabLine2.add(firstDataRow);

        tab.setLayout(new BoxLayout(tab, BoxLayout.PAGE_AXIS));
        tab.add(tabLine1);
        tab.add(tabLine2);

        return tab;
    }

    private JPanel preprocessingTab()
    {
        JPanel tab = new JPanel(true);
        JPanel tabLine1 = new JPanel();
        JPanel tabLine2 = new JPanel();

        // Scale transform
        scaleTransformComboBox = new JComboBox<String>();
        for (ScaleTransformType type : ScaleTransformType.values())
        {
            String s = Utils.titleCaseOf(type.toString());
            scaleTransformComboBox.addItem(s);
        }
        scaleTransformComboBox.setSelectedIndex(0);
        scaleTransformComboBox.setToolTipText("Scale Transform");
        tabLine1.add(new JLabel("Scale Transform:"));
        tabLine1.add(scaleTransformComboBox);

        // Transpose
        transposeCheckBox = new JCheckBox(transposeChangedAction);
        transposeCheckBox.setText("Transpose");
        tabLine1.add(transposeCheckBox);

        tab.setLayout(new BoxLayout(tab, BoxLayout.PAGE_AXIS));
        tab.add(tabLine1);
        tab.add(tabLine2);

        return tab;
    }

    private void initComponents()
    {
        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel centrePanel = new JPanel(true);
        JPanel bottomPanel = new JPanel(true);

        Container container = this.getContentPane();
        container.setLayout( new BorderLayout() );

        tabbedPane.addTab("General", generalTab());
        tabbedPane.addTab("Preprocessing", preprocessingTab());

        centrePanel.setLayout(new BorderLayout());
        textArea = new JEditorPane("text/html", "");
        textArea.setToolTipText("Expression Data");

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize( new Dimension(500, 400) );
        centrePanel.add(scrollPane, BorderLayout.CENTER);

        JButton okButton = new JButton(okAction);
        okButton.setToolTipText("OK");
        JButton cancelButton = new JButton(cancelAction);
        cancelButton.setToolTipText("Cancel");
        bottomPanel.add(okButton);
        bottomPanel.add(cancelButton);

        container.add(tabbedPane, BorderLayout.NORTH);
        container.add(centrePanel, BorderLayout.CENTER);
        container.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void initActions(final JFrame frame)
    {
        okAction = new AbstractAction("OK")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555707L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                if ( correlationField.isEmpty() )
                {
                    JOptionPane.showMessageDialog(frame, "A Correlation value must be given.\nPlease try inserting a Correlation value (default 0.7).", "Correlation value not given!", JOptionPane.INFORMATION_MESSAGE);
                    correlationField.setValue(DEFAULT_STORED_CORRELATION_THRESHOLD);
                    return;
                }
                else if ( ( STORED_CORRELATION_THRESHOLD = correlationField.getValue() ) <= 0.0f )
                {
                    JOptionPane.showMessageDialog(frame, "The Correlation value cannot be 0.0.\nPlease try inserting a Correlation value larger than 0.0.", "Correlation value of 0.0!", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                else if ( ( STORED_CORRELATION_THRESHOLD = correlationField.getValue() ) >= 1.0f )
                {
                    JOptionPane.showMessageDialog(frame, "The Correlation value cannot be equal or bigger than 1.0.\nPlease try inserting a smaller Correlation value.", "Correlation value too large!", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                CURRENT_METRIC = CorrelationTypes.values()[correlationMetric.getSelectedIndex()];
                CURRENT_SCALE_TRANSFORM = ScaleTransformType.values()[scaleTransformComboBox.getSelectedIndex()];
                proceed = true;
                setVisible(false);
            }
        };

        cancelAction = new AbstractAction("Cancel")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555708L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                proceed = false;
                setVisible(false);
            }
        };

        transposeChangedAction = new AbstractAction("TransposeToggle")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                refreshDataPreview(true);
            }
        };
    }

    private DataRect findLargestDataRect(TextDelimitedMatrix tdm)
    {
        int[] heightHistogram = new int[tdm.numColumns()];

        for (int column = 0; column < tdm.numColumns(); column++)
        {
            for (int row = tdm.numRows() - 1; row >= 0; row--)
            {
                String value = tdm.valueAt(column, row);
                if (isNumeric(value))
                {
                    heightHistogram[column]++;
                }
                else
                {
                    break;
                }
            }
        }

        Stack<Integer> heights = new Stack<Integer>();
        Stack<Integer> indexes = new Stack<Integer>();
        DataRect dataRect = new DataRect();

        for (int index = 0; index < heightHistogram.length; index++)
        {
            if (heights.isEmpty() || heightHistogram[index] > heights.peek())
            {
                heights.push(heightHistogram[index]);
                indexes.push(index);
            }
            else if (heightHistogram[index] < heights.peek())
            {
                int lastIndex = 0;

                while (!heights.isEmpty() && heightHistogram[index] < heights.peek())
                {
                    lastIndex = indexes.pop();
                    int height = heights.pop();
                    int width = (index - lastIndex);
                    int area = height * width;
                    if (area > dataRect.getArea())
                    {
                        dataRect.x = lastIndex;
                        dataRect.y = tdm.numRows() - height;
                        dataRect.width = width;
                        dataRect.height = height;
                    }
                }

                heights.push(heightHistogram[index]);
                indexes.push(lastIndex);
            }
        }

        while (!heights.isEmpty())
        {
            int lastIndex = indexes.pop();
            int height = heights.pop();
            int width = (heightHistogram.length - lastIndex);
            int area = height * width;
            if (area > dataRect.getArea())
            {
                dataRect.x = lastIndex;
                dataRect.y = tdm.numRows() - height;
                dataRect.width = width;
                dataRect.height = height;
            }
        }

        return dataRect;
    }

    private void guessDataBounds(TextDelimitedMatrix tdm)
    {
        firstDataColumn.removeAllItems();
        for (int column = 0; column < tdm.numColumns(); column++)
        {
            firstDataColumn.addItem((column + 1) + ": " + tdm.valueAt(column, 0));
        }

        firstDataRow.removeAllItems();
        for (int row = 0; row < tdm.numRows(); row++)
        {
            firstDataRow.addItem((row + 1) + ": " + tdm.valueAt(0, row));
        }

        DataRect guessedDataRect = findLargestDataRect(tdm);

        firstDataColumn.setSelectedIndex(guessedDataRect.x);
        firstDataRow.setSelectedIndex(guessedDataRect.y);

        dataRect = guessedDataRect;
    }

    private void createDialogElements(boolean transpose, boolean guessDataBounds)
    {
        if (creatingDialogElements)
        {
            // Already doing this
            return;
        }

        final int NUM_PREVIEW_COLUMNS = 128;
        final int NUM_PREVIEW_ROWS = 32;

        TextDelimitedMatrix tdm;

        try
        {
            creatingDialogElements = true;
            setCursor(BIOLAYOUT_WAIT_CURSOR);

            if (transpose)
            {
                tdm = new TextDelimitedMatrix(expressionFile, "\t",
                        NUM_PREVIEW_ROWS, NUM_PREVIEW_COLUMNS);
            } else
            {
                tdm = new TextDelimitedMatrix(expressionFile, "\t",
                        NUM_PREVIEW_COLUMNS, NUM_PREVIEW_ROWS);
            }


            if (!tdm.parse())
            {
                return;
            }

            tdm.setTranspose(transpose);
            int numColumns = tdm.numColumns();
            int numRows = tdm.numRows();

            if (guessDataBounds)
            {
                guessDataBounds(tdm);
            }

            StringBuilder pageText = new StringBuilder();
            pageText.append("<HTML>");

            Font font = this.getContentPane().getFont();
            String fontFamily = font.getFamily();
            int fontSize = font.getSize();
            pageText.append("<BODY STYLE=\"");
            pageText.append("font-family: " + fontFamily + ";");
            pageText.append("font-size: 10px;");
            pageText.append("\">");

            pageText.append("<TABLE STYLE=\"background-color: black\" " +
                    "WIDTH=\"100%\" CELLSPACING=\"1\" CELLPADDING=\"2\">\n");

            pageText.append("<TR><TD NOWRAP BGCOLOR=\"#FFFFFF\"></TD>");
            for (int column = 0; column < numColumns; column++)
            {
                pageText.append("<TD NOWRAP ");
                pageText.append("BGCOLOR=\"#FFFFFF\"");
                pageText.append(">" + "Column ");
                pageText.append(Integer.toString(column + 1));
                pageText.append("</TD>");
            }

            if (tdm.hasUnparsedColumns())
            {
                pageText.append("<TD NOWRAP BGCOLOR=\"#FFFFFF\">More columns</TD>");
            }
            pageText.append("</TR>\n");

            for (int row = 0; row < numRows; row++)
            {
                pageText.append("<TR>");

                pageText.append("<TD NOWRAP ");
                pageText.append("BGCOLOR=\"#FFFFFF\"");
                pageText.append(">" + "Row ");
                pageText.append(Integer.toString(row + 1));
                pageText.append("</TD>");

                for (int column = 0; column < numColumns; column++)
                {
                    String options;

                    if (column >= dataRect.x && row >= dataRect.y)
                    {
                        options = "BGCOLOR=\"#CCCCFF\"";
                    }
                    else
                    {
                        if (row == 0)
                        {
                            options = "BGCOLOR=\"#6699FF\"";
                        }
                        else if (column == 0)
                        {
                            options = "BGCOLOR=\"#FFCCCC\"";
                        }
                        else
                        {
                            options = "BGCOLOR=\"#CCFFCC\"";
                        }
                    }

                    String value = tdm.valueAt(column, row);

                    pageText.append("<TD NOWRAP ");
                    pageText.append(options);
                    pageText.append(">");
                    pageText.append(value);
                    pageText.append("</TD>");
                }

                if (tdm.hasUnparsedColumns())
                {
                    pageText.append("<TD NOWRAP BGCOLOR=\"#FFFFFF\">...</TD>");
                }

                pageText.append("</TR>\n");
            }

            if (tdm.hasUnparsedRows())
            {
                pageText.append("<TR>");

                // Row label column
                pageText.append("<TD NOWRAP BGCOLOR=\"#FFFFFF\">More rows</TD>");

                // Data columns
                for (int column = 0; column < numColumns; column++)
                {
                    pageText.append("<TD NOWRAP BGCOLOR=\"#FFFFFF\">...</TD>");
                }

                // Bottom right corner
                if (tdm.hasUnparsedColumns())
                {
                    pageText.append("<TD NOWRAP BGCOLOR=\"#FFFFFF\">...</TD>");
                }

                pageText.append("</TR>\n");
            }

            pageText.append("</TABLE>");
            pageText.append("</BODY>");
            pageText.append("</HTML>");

            textArea.setText(pageText.toString());
            textArea.setCaretPosition(0);
        }
        catch (IOException ioe)
        {
           if (DEBUG_BUILD)
           {
               println("IOException when parsing expression file:\n" + ioe.getMessage());
           }
        }
        finally
        {
            if (guessDataBounds)
            {
                this.pack();
            }

            setCursor(BIOLAYOUT_NORMAL_CURSOR);
            creatingDialogElements = false;
        }
    }

    private boolean isNumeric(String value)
    {
        try
        {
            // instead of numberFormatter.parse(value).floatValue() so as to avoid problems with header columns & parse(value)
            Float.parseFloat( value.replace(',', '.') );

            return true;
        }
        catch (NumberFormatException nfe)
        {
            return false;
        }
    }

    private void refreshDataPreview(boolean guessDataBounds)
    {
        createDialogElements(transposeCheckBox.isSelected(), guessDataBounds);
    }

    @Override
    public void actionPerformed (ActionEvent e)
    {
        if (e.getSource().equals(firstDataColumn) || e.getSource().equals(firstDataRow))
        {
            DataRect newDataRect = new DataRect();
            newDataRect.x = firstDataColumn.getSelectedIndex();
            newDataRect.y = firstDataRow.getSelectedIndex();
            newDataRect.width -= (newDataRect.x - dataRect.x);
            newDataRect.height -= (newDataRect.y - dataRect.y);

            dataRect = newDataRect;
            refreshDataPreview(false);
        }
    }

    public boolean proceed()
    {
        return proceed;
    }

    public int getFirstDataColumn()
    {
        return firstDataColumn.getSelectedIndex();
    }

    public int getFirstDataRow()
    {
        return firstDataRow.getSelectedIndex();
    }

    public boolean transpose()
    {
        return transposeCheckBox.isSelected();
    }

    public boolean saveCorrelationTextFile()
    {
        return saveCorrelationTextFileCheckBox.isSelected();
    }
}