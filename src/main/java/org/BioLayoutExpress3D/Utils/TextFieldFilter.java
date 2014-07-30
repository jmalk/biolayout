package org.BioLayoutExpress3D.Utils;

import javax.swing.text.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/*
*
* User: cggebi
* Date: Sep 2, 2002
*
* @author Full refactoring by Thanos Theo, 2008-2009
* @version 3.0.0.0
*
*/

public final class TextFieldFilter extends PlainDocument
{
    /**
    *  Serial version UID variable for the TextFieldFilter class.
    */
    public static final long serialVersionUID = 111222333444555798L;

    public static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    public static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String ALPHA = LOWERCASE + UPPERCASE;
    public static final String NUMERIC = "0123456789";
    public static final String FLOAT = NUMERIC + DECIMAL_SEPARATOR_STRING;
    public static final String ALPHA_NUMERIC = ALPHA + NUMERIC;

    private String acceptedChars = null;
    private boolean negativeAccepted = false;

    public TextFieldFilter(String acceptedChars)
    {
        this.acceptedChars = acceptedChars;
    }

    public TextFieldFilter(String acceptedChars, boolean negativeAccepted)
    {
        this.acceptedChars = acceptedChars;

        setNegativeAccepted(negativeAccepted);
    }

    public void setNegativeAccepted(boolean negativeAccepted)
    {
        this.negativeAccepted = negativeAccepted;

        if (negativeAccepted)
            if ( acceptedChars.equals(NUMERIC) || acceptedChars.equals(FLOAT) || acceptedChars.equals(ALPHA_NUMERIC) )
                acceptedChars += "-";
    }

    @Override
    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException
    {
        if ( (str == null) || ( str.isEmpty() ) )
            return;

        if ( acceptedChars.equals(UPPERCASE) )
            str = str.toUpperCase();
        else if ( acceptedChars.equals(LOWERCASE) )
            str = str.toLowerCase();

        if ( acceptedChars.equals(FLOAT) || (acceptedChars.equals(FLOAT + "-") && negativeAccepted) )
            if ( !DECIMAL_SEPARATOR_STRING.equals(".") )
                str = str.replace(".", DECIMAL_SEPARATOR_STRING);

        for (int i = 0; i < str.length(); i++)
            if ( !acceptedChars.contains( String.valueOf( str.charAt(i) ) ) )
                return;

        if ( acceptedChars.equals(FLOAT) || (acceptedChars.equals(FLOAT + "-") && negativeAccepted) )
            if ( str.contains(DECIMAL_SEPARATOR_STRING) )
                if ( getText( 0, getLength() ).contains(DECIMAL_SEPARATOR_STRING) )
                    return;

        if ( negativeAccepted && str.contains("-") )
            if ( (str.indexOf("-") != 0) || (offset != 0) )
                return;

        super.insertString(offset, str, attr);
    }


}