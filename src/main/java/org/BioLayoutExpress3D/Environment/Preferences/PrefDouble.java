package org.BioLayoutExpress3D.Environment.Preferences;

import java.util.prefs.*;

/**
*
* The PrefDouble class implements the PrefType abstract class to provide Double save/load preference capability.
*
* @author Full refactoring by Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public class PrefDouble  extends PrefType
{

    /**
    *  Variable to be used for this PrefType sub class.
    */
    private double currentValue = 0.0;

    /**
    *  Variable to be used for this PrefType sub class.
    */
    private double defaultValue = 0.0;

    /**
    *  The constructor of the PrefDouble class.
    */
    public PrefDouble(double defaultValue, String prefName, boolean isSaved)
    {
        super(prefName, isSaved);

        this.defaultValue = currentValue = defaultValue;

        prefType = PrefTypes.PREF_DOUBLE;
        LayoutPreferences.getLayoutPreferencesSingleton().add(this);
    }

    /**
    *  Returns this Double preference.
    */
    public double get()
    {
        return currentValue;
    }

    /**
    *  Sets the Double preference value.
    */
    public void set(double value)
    {
        currentValue = value;
    }

    /**
    *  Loads this Double preference. Overrides the parent class abstract method.
    */
    @Override
    public void loadPref()
    {
        if (USE_CONFIG_FILE)
        {
            usePref(loadPrefFromConfigFile());
        }
        else
            currentValue = Preferences.userRoot().getDouble(prefName, defaultValue);
    }

    /**
    *  Saves this Double preference. Overrides the parent class abstract method.
    */
    @Override
    public void savePref()
    {
        if (USE_CONFIG_FILE)
            savePrefToConfigFile( Double.toString(currentValue) );
        else
            Preferences.userRoot().putDouble(prefName, currentValue);
    }

    /**
    *  Restores this Double preference. Overrides the parent class abstract method.
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
            currentValue = Double.parseDouble(value);
            return true;
        }
        catch (Exception exc)
        {
            currentValue = defaultValue;
        }

        return false;
    }
}