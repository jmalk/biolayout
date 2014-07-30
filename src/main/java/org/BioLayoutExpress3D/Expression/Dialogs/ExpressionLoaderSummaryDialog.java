package org.BioLayoutExpress3D.Expression.Dialogs;

import java.util.HashSet;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import org.BioLayoutExpress3D.Expression.Panels.*;
import org.BioLayoutExpress3D.Expression.ExpressionData;
import org.BioLayoutExpress3D.StaticLibraries.*;
import org.BioLayoutExpress3D.Utils.*;
import static java.lang.Math.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.Expression.ExpressionEnvironment.*;
import org.BioLayoutExpress3D.Files.Parsers.ExpressionParser;

/**
*
* @author Full refactoring by Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public final class ExpressionLoaderSummaryDialog extends JDialog implements ChangeListener, CaretListener
{
    /**
    *  Serial version UID variable for the ExpressionLoaderSummaryDialog class.
    */
    public static final long serialVersionUID = 111222333444555709L;

    private JFrame jframe = null;

    private int minThreshold = 0;
    private int currentThreshold = 0;
    private float currentThresholdFloat = 0.0f;
    private boolean proceed = false;

    private AbstractAction okAction = null;
    private AbstractAction cancelAction = null;
    private JSlider thresholdSlider = null;
    private FloatNumberField thresholdValueTextField = null;
    private ExpressionDegreePlotsPanel expressionDegreePlotsPanel = null;

    private JCheckBox filterValueCheckBox = null;
    private FloatNumberField filterValueField = null;
    private JCheckBox filterCoefVarCheckBox = null;
    private FloatNumberField filterCoefVarField = null;
    private HashSet<Integer> filteredValueRows = null;
    private HashSet<Integer> filteredCoefVarRows = null;

    private ExpressionData expressionData;
    private ExpressionParser scanner;

    public ExpressionLoaderSummaryDialog(JFrame jframe, ExpressionData expressionData, ExpressionParser scanner)
    {
        super(jframe, "Expression Graph Settings", true);

        this.jframe = jframe;
        this.expressionData = expressionData;
        this.scanner = scanner;

        initActions();
        initComponents();

        this.setSize(950, 600);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }

    private void initComponents()
    {
        minThreshold = (int)rint(100.0f * STORED_CORRELATION_THRESHOLD);
        currentThreshold = (int)rint(100.0f * CURRENT_CORRELATION_THRESHOLD);
        currentThresholdFloat = CURRENT_CORRELATION_THRESHOLD;

        JPanel topPanel = new JPanel(true);
        expressionDegreePlotsPanel = new ExpressionDegreePlotsPanel(
                expressionData.getCounts(), expressionData.getTotalRows(),
                minThreshold, currentThreshold, createCorrelationTextValue(currentThreshold) );
        JPanel downPanel = new JPanel(true);
        downPanel.setLayout(new BoxLayout(downPanel, BoxLayout.PAGE_AXIS));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, expressionDegreePlotsPanel, null);
        splitPane.setEnabled(false); // disable the split pane as we use it here just for its look & feel with the DegreePlots JPanel

        thresholdSlider = new JSlider(minThreshold, 100);
        thresholdSlider.setValue(currentThreshold);
        thresholdSlider.addChangeListener(this);
        thresholdSlider.setToolTipText("Correlation Value");
        thresholdValueTextField = new FloatNumberField(0, 5);
        thresholdValueTextField.addCaretListener(this);
        thresholdValueTextField.setDocument( new TextFieldFilter(TextFieldFilter.FLOAT) );
        thresholdValueTextField.setEditable(false);
        thresholdValueTextField.getCaret().setVisible(false);
        thresholdValueTextField.setText( createCorrelationTextValue( thresholdSlider.getValue() ) );
        thresholdValueTextField.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                currentThresholdFloat = thresholdValueTextField.getValue();
                if (currentThresholdFloat > 1.0f)
                {
                    JOptionPane.showMessageDialog(jframe, "The Correlation value cannot be bigger than 1" +
                            DECIMAL_SEPARATOR_STRING + "00" + ".\nPlease try inserting a smaller Correlation value.",
                            "Correlation value too large!", JOptionPane.INFORMATION_MESSAGE);
                    currentThreshold = 100;
                }
                else if (currentThresholdFloat < STORED_CORRELATION_THRESHOLD)
                {
                    JOptionPane.showMessageDialog(jframe, "The Correlation value cannot be smaller than " +
                            STORED_CORRELATION_THRESHOLD + ".\nPlease try inserting a bigger Correlation value.",
                            "Correlation value too small!", JOptionPane.INFORMATION_MESSAGE);
                    currentThreshold = minThreshold;
                }
                else
                {
                    currentThreshold = (int) rint(100.0f * currentThresholdFloat);
                }

                String text = createCorrelationTextValue(currentThreshold);
                thresholdValueTextField.setText(text);
                thresholdSlider.setValue(currentThreshold);
                expressionDegreePlotsPanel.updatePlots(currentThreshold, text);
            }
        });

        JPanel topLine = new JPanel();
        filterValueCheckBox = new JCheckBox(new AbstractAction("FilterValueToggle")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                filterValueField.setEnabled(filterValueCheckBox.isSelected());
                refreshFilterSet();
            }
        });

        filterValueCheckBox.setText("Filter Rows With All Values Less Than");
        filterValueCheckBox.setSelected(false);
        filterValueField = new FloatNumberField(0, 5);
        filterValueField.setDocument(new TextFieldFilter(TextFieldFilter.FLOAT));
        filterValueField.setEnabled(false);
        filterValueField.setValue(0.0f);
        filterValueField.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                refreshFilterSet();
            }
        });
        topLine.add(filterValueCheckBox);
        topLine.add(filterValueField);

        filterCoefVarCheckBox = new JCheckBox(new AbstractAction("FilterCoefVarToggle")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                filterCoefVarField.setEnabled(filterCoefVarCheckBox.isSelected());
                refreshFilterSet();
            }
        });

        filterCoefVarCheckBox.setText("Filter Rows With Coefficient of Variation Less Than");
        filterCoefVarCheckBox.setSelected(false);
        filterCoefVarField = new FloatNumberField(0, 5);
        filterCoefVarField.setDocument(new TextFieldFilter(TextFieldFilter.FLOAT));
        filterCoefVarField.setEnabled(false);
        filterCoefVarField.setValue(0.0f);
        filterCoefVarField.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                refreshFilterSet();
            }
        });
        topLine.add(filterCoefVarCheckBox);
        topLine.add(filterCoefVarField);

        JButton applyButton = new JButton(new AbstractAction("Apply")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                refreshFilterSet();
            }
        });
        topLine.add(applyButton);

        JPanel bottomLine = new JPanel();
        JButton okButton = new JButton(okAction);
        okButton.setToolTipText("OK");
        JButton cancelButton = new JButton(cancelAction);
        cancelButton.setToolTipText("Cancel");
        bottomLine.add( new JLabel("Please Select A Correlation Value: ") );
        bottomLine.add(thresholdSlider);
        bottomLine.add(thresholdValueTextField);
        bottomLine.add(okButton);
        bottomLine.add(cancelButton);

        downPanel.add(topLine);
        downPanel.add(bottomLine);

        Container container = this.getContentPane();
        container.setLayout( new BorderLayout() );
        container.add(topPanel, BorderLayout.NORTH);
        container.add(splitPane, BorderLayout.CENTER);
        container.add(downPanel, BorderLayout.SOUTH);
    }

    private void initActions()
    {
        okAction = new AbstractAction("OK")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555710L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                proceed = true;
                setVisible(false);
            }
        };

        cancelAction = new AbstractAction("Cancel")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555711L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                proceed = false;
                setVisible(false);
            }
        };
    }

    private String createCorrelationTextValue(int value)
    {
        String correlationText = Utils.numberFormatting(value / 100.0, 2);
        if (correlationText.length() == 3)
            return (correlationText + "0");
        else if (correlationText.length() == 1)
            return (correlationText + DECIMAL_SEPARATOR_STRING + "00");
        else
            return correlationText;
    }

    @Override
    public void stateChanged (ChangeEvent e)
    {
        if (e.getSource().equals(thresholdSlider) && !thresholdValueTextField.isFocusOwner())
        {
            currentThreshold = thresholdSlider.getValue();
            currentThresholdFloat = thresholdValueTextField.getValue();
            expressionDegreePlotsPanel.updatePlots(currentThreshold,
                    createCorrelationTextValue(currentThreshold));
            thresholdValueTextField.setText(createCorrelationTextValue(currentThreshold));

            if (thresholdValueTextField.isEditable())
            {
                thresholdValueTextField.setEditable(false);
                thresholdValueTextField.getCaret().setVisible(false);
            }
        }
    }

    @Override
    public void caretUpdate(CaretEvent ce)
    {
        if( ce.getSource().equals(thresholdValueTextField) && thresholdValueTextField.isFocusOwner() )
        {
            if ( !thresholdValueTextField.isEditable() )
            {
                thresholdValueTextField.setEditable(true);
                thresholdValueTextField.getCaret().setVisible(true);
            }
        }
    }

    public void refreshFilterSet()
    {
        CURRENT_FILTER_SET = new HashSet<Integer>();

        if (filterValueCheckBox.isSelected())
        {
            float valueThreshold = filterValueField.getValue();
            filteredValueRows = expressionData.filterMinValue(valueThreshold);
            CURRENT_FILTER_SET.addAll(filteredValueRows);
        }

        if (filterCoefVarCheckBox.isSelected())
        {
            float coefVarThreshold = filterCoefVarField.getValue();
            filteredCoefVarRows = expressionData.filterMinCoefficientOfVariation(coefVarThreshold);
            CURRENT_FILTER_SET.addAll(filteredCoefVarRows);
        }

        scanner.rescan();
        expressionDegreePlotsPanel.updateCounts(expressionData.getCounts());
        expressionDegreePlotsPanel.updatePlots(currentThreshold,
                createCorrelationTextValue(currentThreshold));
    }

    public boolean proceed()
    {
        CURRENT_CORRELATION_THRESHOLD = currentThresholdFloat;

        return proceed;
    }
}