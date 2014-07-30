package org.BioLayoutExpress3D.Utils;

import java.awt.*;
import javax.swing.*;

/*
*
* The ColorButton class provides a Color Selection Button funtionality to the code framework.
*
* @author Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*
*/

public final class ColorButton extends JButton
{
    /**
    *  Serial version UID variable for the ColorButton class.
    */
    public static final long serialVersionUID = 111222333444555700L;

    public boolean isBlank = false;

    public ColorButton(String label)
    {
        super(label);

        this.setBorder( BorderFactory.createLineBorder(Color.BLACK) );
        this.setPreferredSize( new Dimension(20, 20) );
    }

    /**
    *  Shows the Color Chooser dialog and sets a color for the given ColorButton.
    */
    public static void showColorChooser(ColorButton colorButton, JDialog dialog)
    {
        Color color = colorButton.getBackground();
        if (color == null)
            color = new Color(0, 0, 0);

        Color newColor = null;
        if ( (newColor = JColorChooser.showDialog(dialog, "Color Chooser", color) ) != null )
        {
            colorButton.isBlank = false;
            colorButton.setBackground(newColor);
        }
    }


}