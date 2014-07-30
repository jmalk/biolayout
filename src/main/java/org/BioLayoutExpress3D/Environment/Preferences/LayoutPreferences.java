package org.BioLayoutExpress3D.Environment.Preferences;

import java.util.*;
import java.util.prefs.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* The LayoutPreferences class acts as a wrapper for Preferences saving/loading through Singleton Design Pattern usage.
*
* @author Full refactoring by Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public class LayoutPreferences
{

    /**
    *  Reference for the LayoutPreferences Singleton Design Pattern.
    */
    private static LayoutPreferences layoutPreferences = null;

    /**
    *  Data structure to store all the preferences to be saved/loaded.
    */
    private HashSet<PrefType> globalVariables = new HashSet<PrefType>();

    /**
    *  Saves all preferences.
    */
    public void savePreferences()
    {
        for (PrefType prefInterface : globalVariables)
            if ( prefInterface.isSaved() )
                prefInterface.savePref();
    }

    /**
    *  Loads all preferences.
    */
    public void loadPreferences()
    {
        for (PrefType prefInterface : globalVariables)
            if ( prefInterface.isSaved() )
                prefInterface.loadPref();
    }

    /**
    *  Uses specified preferences.
    */
    public boolean useSpecifiedPreferences(Map<String, String> prefs)
    {
        for (Map.Entry<String, String> entry : prefs.entrySet())
        {
            for (PrefType prefInterface : globalVariables)
            {
                if (prefInterface.getPrefName().equals(entry.getKey()))
                {
                    boolean success = prefInterface.usePref(entry.getValue());

                    if (!success)
                    {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
    *  Reverts to default preferences.
    */
    public void revertToDefaultPreferences()
    {
        for (PrefType prefInterface : globalVariables)
            prefInterface.restorePref();
    }

    /**
    *  Adds a preference.
    */
    public void add(PrefType preferencesType)
    {
        globalVariables.add(preferencesType);
    }

    /**
    *  Instantiates this class through Singleton Design Pattern usage.
    */
    public static LayoutPreferences getLayoutPreferencesSingleton()
    {
        if (layoutPreferences == null)
            layoutPreferences = new LayoutPreferences();

        return layoutPreferences;
    }

    /**
    *  Clears all preferences.
    */
    public void clearPreferences()
    {
        if (!PrefType.USE_CONFIG_FILE)
        {
            try
            {
                Preferences.userRoot().clear();
            }
            catch (BackingStoreException bstExc)
            {
                if (DEBUG_BUILD) println("BackingStoreException in LayoutPreferences.clearPreferences():\n" + bstExc.getMessage());
            }
        }
    }


    /**
    *  Used for creating a preference string out of a float array.
    */
    public static String createPreferenceFloatArrayString(float[] array)
    {
        StringBuilder stringArray = new StringBuilder( Float.toString(array[0]) );
        for (int i = 1; i < array.length; i++)
            stringArray.append("_").append( Float.toString(array[i]) );

        return stringArray.toString();
    }

    /**
    *  Used for reading a preference string out of a float array.
    */
    public static float[] readPreferenceFloatArrayString(String arrayString)
    {
        String[] strings = arrayString.split("_+");
        float[] array = new float[strings.length];
        for (int i = 0; i < array.length; i++)
            array[i] = Float.parseFloat(strings[i]);

        return array;
    }


}