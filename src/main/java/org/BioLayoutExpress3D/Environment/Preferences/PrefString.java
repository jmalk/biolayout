package org.BioLayoutExpress3D.Environment.Preferences;

import java.util.prefs.*;

/**
*
* The PrefString class implements the PrefType abstract class to provide String save/load preference capability.
*
* @author Full refactoring by Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public class PrefString extends PrefType
{

    /**
    *  Variable to be used for this PrefType sub class.
    */
    private String currentValue = "";

    /**
    *  Variable to be used for this PrefType sub class.
    */
    private String defaultValue = "";

    /**
    *  The constructor of the PrefString class.
    */
    public PrefString(String defaultValue, String prefName, boolean isSaved)
    {
        super(prefName, isSaved);

        this.defaultValue = currentValue = defaultValue;

        prefType = PrefTypes.PREF_STRING;
        LayoutPreferences.getLayoutPreferencesSingleton().add(this);
    }

    /**
    *  Returns this String preference.
    */
    public String get()
    {
        return currentValue.replace("%20", " ");
    }

    /**
    *  Sets the String preference value.
    */
    public void set(String value)
    {
        currentValue = value.replace(" ", "%20");
    }

    /**
    *  Loads this String preference. Overrides the parent class abstract method.
    */
    @Override
    public void loadPref()
    {
        if (USE_CONFIG_FILE)
        {
            usePref(loadPrefFromConfigFile());
        }
        else
            currentValue = Preferences.userRoot().get(prefName, defaultValue);
    }

    /**
    *  Saves this String preference. Overrides the parent class abstract method.
    */
    @Override
    public void savePref()
    {
        if (USE_CONFIG_FILE)
            savePrefToConfigFile(currentValue);
        else
            Preferences.userRoot().put(prefName, currentValue);
    }

    /**
    *  Restores this String preference. Overrides the parent class abstract method.
    */
    @Override
    public void restorePref()
    {
        currentValue = defaultValue;
    }

    @Override
    public boolean usePref(String value)
    {
        currentValue = (value != null) ? value : defaultValue;
        return true;
    }
}