package org.BioLayoutExpress3D.Utils;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import javax.swing.*;
import javax.swing.event.*;
import static java.lang.Math.*;

/**
*
* @author Full refactoring by Thanos Theo, 2008-2009-2010-2011-2012
* @version 3.0.0.0
*
*/

public class SimpleSlider extends JPanel implements ChangeListener
{
    /**
    *  Serial version UID variable for the SimpleSlider class.
    */
    public static final long serialVersionUID = 111222333444555698L;

    private JTextField     textfield = null;
    private String         actionCommand = "";
    private ActionListener actionListener = null;
    private JSlider        slider = null;
    private JLabel         label = null;
    private NumberFormat   nf = null;
    private double         maxValue = 0.0;
    private double         minValue = 0.0;
    private int            ticks = 0;
    private double         tickScale = 0.0;
    private double         value = 0.0;
    private boolean        isInteger = false;

    public SimpleSlider(int fieldSize, int sliderWidth, int sliderHeight, double initialValue, double minValue, double maxValue, int ticks, String textLabel, boolean isInteger)
    {
        super(true);

        this.minValue = minValue;
        this.maxValue = maxValue;
        this.isInteger = isInteger;
        textfield = new JTextField(Double.toString(initialValue), fieldSize);
        value = initialValue;
        this.ticks = ticks;
        tickScale = (maxValue - minValue) / ticks;

        slider = new JSlider();
        slider.addChangeListener(this);
        slider.setMajorTickSpacing( (int)rint(100.0 / ticks) );
        slider.setMinorTickSpacing( (int)rint(100.0 / ticks) );
        textfield.setEditable(false);

        nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);

        this.setLayout( new BoxLayout(this, BoxLayout.Y_AXIS) );
        this.add(textfield);
        this.add(slider);

        if ( !textLabel.isEmpty() )
        {
            label = new JLabel(textLabel);
            this.add(label);
        }

        this.setPreferredSize( new Dimension(sliderWidth, sliderHeight) );
        this.setVisible(true);
    }

    @Override
    public void stateChanged(ChangeEvent e)
    {
        if ( e.getSource().equals(slider) )
        {
            value = minValue + ( ( (double)slider.getValue() / 100.0) * tickScale * (double)ticks );

            if (isInteger)
            {
                value = rint(value);
                nf.setMaximumFractionDigits(0);
            }

            textfield.setText( nf.format(value) );

            if (actionListener != null)
                actionListener.actionPerformed( new ActionEvent(slider, 1, actionCommand) );
        }
    }


    public void addActionListener(ActionListener actionListener)
    {
        this.actionListener = actionListener;
    }

    public void setActionCommand(String actionCommand)
    {
        this.actionCommand = actionCommand;
    }

    public boolean checkSourceFromActionEvent(ActionEvent e)
    {
        return e.getSource().equals(slider);
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);

        textfield.setEnabled(enabled);
        slider.setEnabled(enabled);
        if (label != null) label.setEnabled(enabled);
    }

    @Override
    public void setToolTipText(String text)
    {
        textfield.setToolTipText(text);
        slider.setToolTipText(text);
        if (label != null) label.setToolTipText(text);
    }

    public double getValue()
    {
        return value;
    }

    public void setValue(double value)
    {
        this.value = value;

        textfield.setText( nf.format(value) );
        slider.setValue( (int)rint( 100.0 * (value - minValue) / (maxValue - minValue) ) );
    }


}