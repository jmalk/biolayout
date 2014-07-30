package org.BioLayoutExpress3D.Environment.Preferences;

import java.io.*;
import java.util.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;
import org.BioLayoutExpress3D.Environment.DataFolder;
import org.BioLayoutExpress3D.Utils.Path;

/**
*
* The PrefType class is the base class for all Preferences classes.
*
* @author Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public abstract class PrefType
{

    /**
    *  Constant variable to be used for using or not the config file or the registry/preferences mechanism.
    */
    protected static final boolean USE_CONFIG_FILE = true;

    /**
    *  Constant variable to be used for the config file name.
    */
    private static final String CONFIG_FILE_NAME = "BioLayoutExpress3D.cfg";

    /**
    *  Constant variable to be used for the config file first comment line.
    */
    private static final String CONFIG_FILE_FIRST_COMMENT_LINE = "# " + TITLE + TITLE_VERSION + TITLE_VERSION_NUMBER + " config file used to save various applications settings, (#) used for commenting";

    /**
    *  Constant variable to be used for the config file equals options symbol.
    */
    private static final String EQUALS_SYMBOL = " = ";

    /**
    *  Variable to be used as a Singleton Design Pattern one for config file availability.
    */
    private static boolean hasNotCheckedForConfigFile = true;

    /**
    *  Variable to be used as a Singleton Design Pattern one for config file path.
    */
    private static String configFileNamePath = "";

    /**
    *  Type of the preference variable.
    */
    protected PrefTypes prefType = null;

    /**
    *  The name of the isSaved variable.
    */
    protected String prefName = "";

    /**
    *  Variable to store is the preference is to be saved.
    */
    protected boolean isSaved = false;

    /**
    *  Constructor of PrefType to be instantiated only by a sub class.
    */
    protected PrefType(String prefName, boolean isSaved)
    {
        this.prefName = prefName;
        this.isSaved = isSaved;
    }

    /**
    *  Method to check the availability of the config file To be used with the Singleton Design Pattern.
    */
    private void hasCkeckedForSingletonConfigFile()
    {
        if (hasNotCheckedForConfigFile)
        {
            hasNotCheckedForConfigFile = false;

            configFileNamePath = Path.combine(DataFolder.get(), CONFIG_FILE_NAME);

            if ( !new File(configFileNamePath).exists() )
            {
                if (DEBUG_BUILD) println("\nNow creating the default config file\n");

                createDefaultConfigFile();
            }
            else
                if (DEBUG_BUILD) println("\nSkipping creation of the default config file\n");
        }
    }

    /**
    *  Method to create the default (empty) config file.
    */
    private void createDefaultConfigFile()
    {
        PrintWriter fileout = null;

        try
        {
            File file = new File(configFileNamePath);
            File directory = new File(file.getParent());
            if (!directory.exists() && !directory.mkdirs())
            {
                throw new IOException("Couldn't make parent directory " + directory.getAbsolutePath());
            }

            fileout = new PrintWriter(configFileNamePath);
            fileout.println(CONFIG_FILE_FIRST_COMMENT_LINE);
            fileout.println();
            fileout.flush();
        }
        catch (IOException ioe)
        {
            if (DEBUG_BUILD) println("Error writing the " + configFileNamePath + " file:\n" + ioe.getMessage());
        }
        finally
        {
            if (fileout != null) fileout.close();
        }
    }

    /**
    *  Method to load from the config file.
    *  First it checks for the config file availability through the hasCkeckedForSingletonConfigFile() method and the Singleton Design Pattern.
    */
    protected String loadPrefFromConfigFile()
    {
        hasCkeckedForSingletonConfigFile();

        BufferedReader br = null;

        try
        {
            br = new BufferedReader( new FileReader(configFileNamePath) );
            String line = "", option = "", equals = "", selection = "";
            int numberOfTokens = 0;
            Scanner scanner = null;

            while ( ( line = br.readLine() ) != null )
            {
                if (line.length() == 0)  // blank line
                    continue;
                if ( line.startsWith("#") )   // comment
                    continue;

                numberOfTokens = line.split("\\s+").length;
                scanner = new Scanner(line);

                if (numberOfTokens < 2 || numberOfTokens > 3)
                {
                    if (DEBUG_BUILD)
                    {
                        println("Wrong no. of arguments for " + line);
                        println("Creating the default config " + configFileNamePath + " file");
                    }

                    createDefaultConfigFile();
                }
                else
                {
                    option = scanner.next();
                    equals = scanner.next();

                    try
                    {
                        selection = scanner.next();
                    }
                    catch (NoSuchElementException nseExc)
                    {
                        selection = null;
                    }

                    if ( option.equals(prefName) )
                        return selection;
                }
            }

            return null;
        }
        catch (IOException ioe)
        {
            if (DEBUG_BUILD)
            {
                println("Error loading preference from config file: " + configFileNamePath + "\n" + ioe.getMessage());
                println();
            }

            createDefaultConfigFile();

            return null;
        }
        finally
        {
            try
            {
                if (br != null) br.close();
            }
            catch (IOException ioExc)
            {
                if (DEBUG_BUILD) println("Not managed to close the config file buffered reader with 'finally' clause in loadPrefFromConfigFile() method:\n" + ioExc.getMessage());
            }
        }
    }

    /**
    *  Method to save to the config file.
    *  First it checks for the config file availability through the hasCkeckedForSingletonConfigFile() method and the Singleton Design Pattern.
    */
    protected void savePrefToConfigFile(String newSelection)
    {
        hasCkeckedForSingletonConfigFile();

        BufferedReader br = null;

        try
        {
            br = new BufferedReader( new FileReader(configFileNamePath) );
            ArrayList<String> allLines = new ArrayList<String>();
            String line = "";

            while ( ( line = br.readLine() ) != null )
                allLines.add(line);

            if ( checkPrefNameExistInAnyLines(allLines) )
            {
                ArrayList<String> newAllLines = new ArrayList<String>();

                for (int i = 0; i < allLines.size(); i++)
                {
                    line = allLines.get(i);
                    if ( line.startsWith(prefName + " ") )
                    {
                        String option = "", equals = "";
                        Scanner scanner = new Scanner(line);
                        option = scanner.next();
                        equals = scanner.next();

                        line = option + " " + equals + " " + newSelection;
                        newAllLines.add(line);
                    }
                    else
                    {
                        if (i > 0)
                            newAllLines.add(line);
                    }
                }

                Collections.sort(newAllLines);
                PrintWriter fileout = null;
                try
                {
                    fileout = new PrintWriter( new BufferedWriter( new FileWriter(configFileNamePath, false) ) );
                    fileout.println(CONFIG_FILE_FIRST_COMMENT_LINE);
                    for (String newLine : newAllLines)
                        fileout.println(newLine);
                    fileout.flush();
                }
                catch (IOException ioe)
                {
                    if (DEBUG_BUILD) println("Error writing the updated " + configFileNamePath + " file:\n" + ioe.getMessage());
                }
                finally
                {
                    if (fileout != null) fileout.close();
                }
            }
            else
            {
                PrintWriter fileout = null;
                try
                {
                    fileout = new PrintWriter( new BufferedWriter( new FileWriter(configFileNamePath, true) ) );
                    fileout.println(prefName + EQUALS_SYMBOL + newSelection);
                    fileout.flush();
                }
                catch (IOException ioe)
                {
                    if (DEBUG_BUILD) println("Error writing the updated " + configFileNamePath + " file:\n" + ioe.getMessage());
                }
                finally
                {
                    if (fileout != null) fileout.close();
                }
            }
        }
        catch (IOException ioe)
        {
            if (DEBUG_BUILD) println("Error saving preference to config file " + configFileNamePath + " file:\n" + ioe.getMessage());

            createDefaultConfigFile();
        }
        finally
        {
            try
            {
                if (br != null) br.close();
            }
            catch (IOException ioExc)
            {
                if (DEBUG_BUILD) println("Not managed to close the config file buffered reader with 'finally' clause in savePrefToConfigFile() method:\n" + ioExc.getMessage());
            }
        }
    }

    /**
    *  Method to Check if the pref name exists in the arraylist collection of lines.
    */
    private boolean checkPrefNameExistInAnyLines(ArrayList<String> allLines)
    {
        for (String checkLine : allLines)
            if ( checkLine.startsWith(prefName + " ") )
                return true;

        return false;
    }

    /**
    *  Gets the preference name.
    */
    public String getPrefName()
    {
        return prefName;
    }

    /**
    *  Gets the preference type.
    */
    public PrefTypes getType()
    {
        return prefType;
    }

    /**
    *  Gets the preference if is to be saved.
    */
    public boolean isSaved()
    {
        return isSaved;
    }

    /**
    *  Abstract method to be implemented in a sub class.
    */
    public abstract void loadPref();

    /**
    *  Abstract method to be implemented in a sub class.
    */
    public abstract void savePref();

    /**
    *  Abstract method to be implemented in a sub class.
    */
    public abstract void restorePref();

    /**
    *  Abstract method to be implemented in a sub class.
    */
    public abstract boolean usePref(String value);
}