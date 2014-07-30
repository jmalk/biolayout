package org.BioLayoutExpress3D.Environment.Preferences;

import java.util.prefs.*;

/**
 *
 * @author Tim Angus <tim.angus@roslin.ed.ac.uk>
 */
public class PrefEnum<T extends Enum<T>> extends PrefType
{
    private final Class<T> clazz;

    /**
     * Variable to be used for this PrefType sub class.
     */
    private T currentValue;
    /**
     * Variable to be used for this PrefType sub class.
     */
    private T defaultValue;

    /**
     * The constructor of the PrefEnum class.
     */
    public PrefEnum(Class<T> clazz, T defaultValue, String prefName, boolean isSaved)
    {
        super(prefName, isSaved);

        this.clazz = clazz;
        this.defaultValue = currentValue = defaultValue;

        prefType = PrefTypes.PREF_ENUM;
        LayoutPreferences.getLayoutPreferencesSingleton().add(this);
    }

    /**
     * Returns this Enum preference.
     */
    public T get()
    {
        return currentValue;
    }

    public int getIndex()
    {
        return currentValue.ordinal();
    }

    /**
     * Sets the Enum preference value.
     */
    public void set(T value)
    {
        currentValue = value;
    }

    /**
     * Loads this Enum preference. Overrides the parent class abstract method.
     */
    @Override
    public void loadPref()
    {
        String value;

        if (USE_CONFIG_FILE)
        {
            value = loadPrefFromConfigFile();
        }
        else
        {
            value = Preferences.userRoot().get(prefName, defaultValue.toString());
        }

        usePref(value);
    }

    /**
     * Saves this Enum preference. Overrides the parent class abstract method.
     */
    @Override
    public void savePref()
    {
        if (USE_CONFIG_FILE)
        {
            savePrefToConfigFile(currentValue.toString());
        }
        else
        {
            Preferences.userRoot().put(prefName, currentValue.toString());
        }
    }

    /**
     * Restores this Enum preference. Overrides the parent class abstract method.
     */
    @Override
    public void restorePref()
    {
        currentValue = defaultValue;
    }

    @Override
    public boolean usePref(String value)
    {
        T enumValue = defaultValue;

        if (value != null)
        {
            try
            {
                enumValue = Enum.valueOf(clazz, value);
            }
            catch (IllegalArgumentException iae)
            {
            }
        }

        currentValue = enumValue;
        return true;
    }
}