package org.BioLayoutExpress3D.Environment.Preferences;

import java.awt.*;
import java.util.prefs.*;
import org.BioLayoutExpress3D.StaticLibraries.*;

/**
*
* The PrefColor class implements the PrefType abstract class to provide Color save/load preference capability.
*
* @author Full refactoring by Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public class PrefColor extends PrefType
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
    *  Variable to be used for this PrefType sub class.
    */
    private Color color = null;

    /**
    *  The constructor of the PrefColor class.
    */
    public PrefColor(Color defaultValue, String prefName, boolean isSaved)
    {
        super(prefName, isSaved);

        this.defaultValue = currentValue = Utils.getHexColor(defaultValue);

        color = Color.decode(currentValue);

        prefType = PrefTypes.PREF_COLOR;
        LayoutPreferences.getLayoutPreferencesSingleton().add(this);
    }

    /**
    *  Returns this Color preference.
    */
    public Color get()
    {
        return color;
    }

    /**
    *  Sets the Color preference value.
    */
    public void set(Color value)
    {
        currentValue = Utils.getHexColor(value);
        color = value;
    }

    /**
    *  Loads this Color preference. Overrides the parent class abstract method.
    */
    @Override
    public void loadPref()
    {
        if (USE_CONFIG_FILE)
        {
            usePref( loadPrefFromConfigFile());
        }
        else
            currentValue = Preferences.userRoot().get(prefName, defaultValue);

        color = Color.decode(currentValue);
    }

    /**
    *  Saves this Color preference. Overrides the parent class abstract method.
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
    *  Restores this Color preference. Overrides the parent class abstract method.
    */
    @Override
    public void restorePref()
    {
        currentValue = defaultValue;
        color = Color.decode(currentValue);
    }

    @Override
    public boolean usePref(String value)
    {
        try
        {
            currentValue = (value != null) ? value : defaultValue;
            return true;
        }
        catch (Exception exc)
        {
            currentValue = defaultValue;
        }

        return false;
    }
}