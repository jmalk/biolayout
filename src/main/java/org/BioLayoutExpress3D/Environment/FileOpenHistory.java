package org.BioLayoutExpress3D.Environment;

import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.Environment.Preferences.*;

/**
*
* @author Leon Goldovsky, full refactoring by Thanos Theo, 2008-2009
* @version 3.0.0.0
*
*/

public class FileOpenHistory extends StringHistory
{
    /**
    *  Serial version UID variable for the FileOpenHistory class.
    */
    public static final long serialVersionUID = 111222333444555701L;

    private ArrayList<PrefString> preferences = null;

    public FileOpenHistory(int maxHistory)
    {
        super(maxHistory);

        preferences = new ArrayList<PrefString>(maxHistory);
        createPreferences();
        updateHistoryFromPreferences();
    }

    private void createPreferences()
    {
        PrefString value = null;
        for (int i = 0; i < maxHistory; i++)
        {
            value = new PrefString("", "biolayout_express_3d_history_file_" + i, true);
            value.loadPref();
            preferences.add(value);
        }
    }

    private void updateHistoryFromPreferences()
    {
        for (PrefString value : preferences)
        {
            if ( value.get().isEmpty() )
                break;

            history.add( value.get() );
        }
    }

    public ArrayList<AbstractAction> getActionsList(final LayoutFrame layoutFrame)
    {
        int counter = 0;
        ArrayList<AbstractAction> abstractActionsList = new ArrayList<AbstractAction>(maxHistory);
        for (final String value : history) // for inner class variable accessing
        {
            if (counter >= maxHistory)
                break;

            PrefString fileHistoryPref = preferences.get(counter);
            fileHistoryPref.set(value);
            fileHistoryPref.savePref();

            AbstractAction action = new AbstractAction(value)
            {
                /**
                *  Serial version UID variable for the AbstractAction class.
                */
                public static final long serialVersionUID = 111222333444555702L;

                @Override
                public void actionPerformed(ActionEvent e)
                {
                    layoutFrame.loadDataSet( new File(value) );
                }
            };

            abstractActionsList.add(action);
            counter++;
        }

        return abstractActionsList;
    }


}