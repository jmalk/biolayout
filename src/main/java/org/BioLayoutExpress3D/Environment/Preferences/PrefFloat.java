package org.BioLayoutExpress3D.Environment.Preferences;

import java.util.prefs.*;

/**
*
* The PrefFloat class implements the PrefType abstract class to provide Float save/load preference capability.
*
* @author Full refactoring by Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public class PrefFloat extends PrefType
{

    /**
    *  Variable to be used for this PrefType sub class.
    */
    private float currentValue = 0.0f;

    /**
    *  Variable to be used for this PrefType sub class.
    */
    private float defaultValue = 0.0f;

    /**
    *  The constructor of the PrefFloat class.
    */
    public PrefFloat(float defaultValue, String prefName, boolean isSaved)
    {
        super(prefName, isSaved);

        this.defaultValue = currentValue = defaultValue;

        prefType = PrefTypes.PREF_FLOAT;
        LayoutPreferences.getLayoutPreferencesSingleton().add(this);
    }

    /**
    *  Returns this Float preference.
    */
    public float get()
    {
        return currentValue;
    }

    /**
    *  Sets the Float preference value.
    */
    public void set(float value)
    {
        currentValue = value;
    }

    /**
    *  Loads this Float preference. Overrides the parent class abstract method.
    */
    @Override
    public void loadPref()
    {
        if (USE_CONFIG_FILE)
        {
            usePref(loadPrefFromConfigFile());
        }
        else
            currentValue = Preferences.userRoot().getFloat(prefName, defaultValue);
    }

    /**
    *  Saves this Float preference. Overrides the parent class abstract method.
    */
    @Override
    public void savePref()
    {
        if (USE_CONFIG_FILE)
            savePrefToConfigFile( Float.toString(currentValue) );
        else
            Preferences.userRoot().putFloat(prefName, currentValue);
    }

    /**
    *  Restores this Float preference. Overrides the parent class abstract method.
    */
    @Override
    public void restorePref()
    {
        currentValue = defaultValue;
    }

    @Override
    public boolean usePref(String value)
    {
        try
        {
            currentValue = Float.parseFloat(value);
            return true;
        }
        catch (Exception exc)
        {
            currentValue = defaultValue;
        }

        return false;
    }
}