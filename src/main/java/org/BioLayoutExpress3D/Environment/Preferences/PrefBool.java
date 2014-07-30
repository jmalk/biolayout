package org.BioLayoutExpress3D.Environment.Preferences;

import java.util.prefs.*;

/**
*
* The PrefBool class implements the PrefType abstract class to provide Boolean save/load preference capability.
*
* @author Full refactoring by Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public class PrefBool extends PrefType
{

    /**
    *  Variable to be used for this PrefType sub class.
    */
    private boolean currentValue = false;

    /**
    *  Variable to be used for this PrefType sub class.
    */
    private boolean defaultValue = false;

    /**
    *  The constructor of the PrefBool class.
    */
    public PrefBool(boolean defaultValue, String prefName, boolean isSaved)
    {
        super(prefName, isSaved);

        this.defaultValue = currentValue = defaultValue;

        prefType = PrefTypes.PREF_BOOL;
        LayoutPreferences.getLayoutPreferencesSingleton().add(this);
    }

    /**
    *  Returns this Boolean preference.
    */
    public boolean get()
    {
        return currentValue;
    }

    /**
    *  Sets the Boolean preference value.
    */
    public void set(boolean value)
    {
        currentValue = value;
    }

    /**
    *  Loads this Boolean preference. Overrides the parent class abstract method.
    */
    @Override
    public void loadPref()
    {
        if (USE_CONFIG_FILE)
        {
            usePref(loadPrefFromConfigFile());
        }
        else
            currentValue = Preferences.userRoot().getBoolean(prefName, defaultValue);
    }

    /**
    *  Saves this Boolean preference. Overrides the parent class abstract method.
    */
    @Override
    public void savePref()
    {
        if (USE_CONFIG_FILE)
            savePrefToConfigFile( Boolean.toString(currentValue) );
        else
            Preferences.userRoot().putBoolean(prefName, currentValue);
    }

    /**
    *  Restores this Boolean preference. Overrides the parent class abstract method.
    */
    @Override
    public void restorePref()
    {
        currentValue = defaultValue;
    }

    @Override
    public boolean usePref(String value)
    {
        currentValue = (value != null) ? Boolean.parseBoolean(value) : defaultValue;
        return true;
    }
}