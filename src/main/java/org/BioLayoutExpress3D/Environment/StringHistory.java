package org.BioLayoutExpress3D.Environment;

import java.util.*;

/**
*
* @author Leon Goldovsky, full refactoring by Thanos Theo, 2008-2009
* @version 3.0.0.0
*
*/

public class StringHistory
{
    protected ArrayList<String> history = null;
    protected int maxHistory = 0;

    public StringHistory(int maxHistory)
    {
        this.maxHistory = maxHistory;
        history = new ArrayList<String>(maxHistory);
    }

    private void checkValueExists(String value)
    {
        for (String currentValue : history)
        {
            if ( currentValue.equals(value) )
            {
                history.remove(value);
                break;
            }
        }
    }

    public void addToHistory(String value)
    {
        checkValueExists(value);
        history.add(0, value);
    }


}