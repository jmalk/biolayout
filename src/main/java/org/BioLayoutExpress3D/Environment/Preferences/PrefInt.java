package org.BioLayoutExpress3D.Environment.Preferences;

import java.util.prefs.*;
import javax.swing.JTextField;

/**
*
* The PrefInt class implements the PrefType abstract class to provide Integer save/load preference capability.
*
* @author Full refactoring by Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public class PrefInt extends PrefType
{

    /**
    *  Variable to be used for this PrefType sub class.
    */
    private int currentValue = 0;

    /**
    *  Variable to be used for this PrefType sub class.
    */
    private int defaultValue = 0;

    /**
    *  The constructor of the PrefInt class.
    */
    public PrefInt(int defaultValue, String prefName, boolean isSaved)
    {
        super(prefName, isSaved);

        this.defaultValue = currentValue = defaultValue;

        prefType = PrefTypes.PREF_INT;
        LayoutPreferences.getLayoutPreferencesSingleton().add(this);
    }

    /**
    *  Returns this Integer preference.
    */
    public int get()
    {
        return currentValue;
    }

    /**
    *  Sets the Integer preference value.
    */
    public void set(int value)
    {
        currentValue = value;
    }

    /**
    *  Sets the Integer preference value.
    */
    public void set(JTextField value)
    {
        try
        {
            set(Integer.parseInt(value.getText()));
        }
        catch(Exception e)
        {
            value.setText(Integer.toString(defaultValue));
            set(defaultValue);
        }
    }

    /**
    *  Loads this Integer preference. Overrides the parent class abstract method.
    */
    @Override
    public void loadPref()
    {
        if (USE_CONFIG_FILE)
        {
            usePref(loadPrefFromConfigFile());
        }
        else
            currentValue = Preferences.userRoot().getInt(prefName, defaultValue);
    }

    /**
    *  Saves this Integer preference. Overrides the parent class abstract method.
    */
    @Override
    public void savePref()
    {
        if (USE_CONFIG_FILE)
            savePrefToConfigFile( Integer.toString(currentValue) );
        else
            Preferences.userRoot().putInt(prefName, currentValue);
    }

    /**
    *  Restores this Integer preference. Overrides the parent class abstract method.
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
            currentValue = Integer.parseInt(value);
            return true;
        }
        catch (Exception exc)
        {
            currentValue = defaultValue;
        }

        return false;
    }
}