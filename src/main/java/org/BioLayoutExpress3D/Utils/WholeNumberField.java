package org.BioLayoutExpress3D.Utils;

import java.awt.*;
import java.text.*;
import javax.swing.*;
import javax.swing.text.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* @author Anton Enright, full refactoring by Thanos Theo, 2008-2009
* @version 3.0.0.0
*
*/

public final class WholeNumberField extends JTextField
{
    /**
    *  Serial version UID variable for the WholeNumberField class.
    */
    public static final long serialVersionUID = 111222333444555801L;

    private NumberFormat integerFormatter = null;

    public WholeNumberField(int value, int columns)
    {
        super(columns);

        integerFormatter = NumberFormat.getInstance();
        integerFormatter.setParseIntegerOnly(true);

        setValue(value);
    }

    public void setParseIntegerOnly(boolean flag)
    {
        integerFormatter.setParseIntegerOnly(flag);
    }

    public boolean isEmpty()
    {
        return getText().isEmpty();
    }

    public int getValue()
    {
        try
        {
            return integerFormatter.parse( getText() ).intValue();
        }
        catch (ParseException parseExc)
        {
            // This should never happen because insertString allows
            // only properly formatted data to get in the field.
            Toolkit.getDefaultToolkit().beep();

            if (DEBUG_BUILD) println("WholeNumberField.getValue() ParseException:\n" + parseExc.getMessage());

            return 0;
        }
    }

    public void setValue(int value)
    {
        setText( integerFormatter.format(value) );
    }

    @Override
    protected Document createDefaultModel()
    {
        return new WholeNumberDocument();
    }

    private static class WholeNumberDocument extends PlainDocument
    {
        /**
        *  Serial version UID variable for the WholeNumberDocument class.
        */
        public static final long serialVersionUID = 111222333444555802L;

        @Override
        public void insertString(int offset, String str, AttributeSet a) throws BadLocationException
        {
            if ( (str == null) || ( str.isEmpty() ) )
                return;

            char[] source = str.toCharArray();
            char[] result = new char[source.length];

            int j = 0;
            for (int i = 0; i < result.length; i++)
                if ( Character.isDigit(source[i]) )
                    result[j++] = source[i];


            super.insertString(offset, new String(result, 0, j), a);
        }


    }


}