package org.BioLayoutExpress3D;

import java.awt.Dimension;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.text.SimpleDateFormat;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.DebugConsole.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;
import org.BioLayoutExpress3D.Environment.DataFolder;
import org.BioLayoutExpress3D.Utils.Path;
import org.BioLayoutExpress3D.Utils.ThreadExceptionHandler;

/**
*
* BioLayoutExpress3D - A tool for visualisation
* and analysis of biological networks
*
* Copyright (c) 2006-2012 Genome Research Ltd.
* Authors: Thanos Theo, Anton Enright, Leon Goldovsky, Ildefonso Cases, Markus Brosch, Stijn van Dongen, Michael Kargas, Benjamin Boyer and Tom Freeman
* Contact: support@biolayout.org
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*
* @author void main, command-line & logging parameters refactoring by Thanos Theo, 2008-2009-2010-2011
* @author BioLayoutExpress3D Generics refactoring for Java 1.5/1.6 by Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public final class Layout
{

    /**
    *  Constructor of the Layout class.
    */
    private Layout(String fileName, boolean onlineConnect, String repository, String dataSets,
            boolean useDefaults, Map<String, String> preferences,
            boolean hasChosenUseShadersProcessCommandLine, boolean useShadersProcess)
    {

        if (!DEBUG_BUILD)
        {
            ReleaseLogger stdOutLogger = new ReleaseLogger(System.out);
            System.setOut(stdOutLogger);

            ReleaseLogger stdErrLogger = new ReleaseLogger(System.err);
            System.setOut(stdErrLogger);
        }

        if (hasChosenUseShadersProcessCommandLine)
            USE_SHADERS_PROCESS = useShadersProcess;

	// If not connected...
        if (!onlineConnect)
        {
	    // Make a layout frame, with or without a filename.
	    // Given a filename load that dataset.
            if ( fileName.isEmpty() )
                new LayoutFrame().initializeFrame(useDefaults, preferences, false);
            else
                new LayoutFrame().initializeFrame(useDefaults, preferences, true).loadDataSet( new File(fileName) );
        }
        else
        {
	    // Use the online dataset.
            new LayoutFrame().initializeFrame(useDefaults, preferences, true).loadOnlineDataSet(repository, dataSets);
        }
    }

    private class ReleaseLogger extends PrintStream
    {
        public ReleaseLogger(OutputStream stream)
        {
            super(stream, true);
        }

        @Override
        public void print(String s)
        {
            super.print(s);

            try
            {
                String build = BuildConfig.VERSION + "(" + BuildConfig.BUILD_TIME + ")";
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

                String dataFolder = DataFolder.get();
                String exceptionLogFileName = Path.combine(dataFolder, "ReleaseConsoleOutput.txt");
                PrintWriter logOut = new PrintWriter(new BufferedWriter(new FileWriter(exceptionLogFileName, true)));
                logOut.print(build + " " + timeStamp + ": ");
                logOut.println(s);
                logOut.close();
            }
            catch (IOException ioe)
            {
            }
        }
    }

    private static String propertiesToString(Properties properties)
    {
        String output = "";

        Set<String> keySet = properties.stringPropertyNames();
        ArrayList<String> keyList = new ArrayList<String>(keySet);
        Collections.sort(keyList);
        int longestKey = 0;

        for (Object keyObject : keyList)
        {
            String key = (String)keyObject;
            if (key.length() > longestKey)
                longestKey = key.length();
        }

        String pathSeparator = System.getProperty("path.separator");

        for (Object keyObject : keyList)
        {
            String key = (String)keyObject;
            int keyLength = key.length();
            int padding = 1 + longestKey - keyLength;

            String value = properties.getProperty(key);
            value = value.replace("\n", "\\n");
            value = value.replace("\r", "\\r");
            value = value.replace("\t", "\\t");

            output += key + ":";
            for( int i = 0; i < padding; i++)
                output += " ";

            if (key.matches("(?i).*(path|dirs)$"))
            {
                // Property is probably a list of paths, so split it up
                String[] paths = value.split(pathSeparator);
                output += paths[0] + "\n";

                padding = 2 + longestKey;
                for( int i = 1; i < paths.length; i++)
                {
                    for( int j = 0; j < padding; j++)
                        output += " ";

                    output += paths[i] + "\n";
                }
            }
            else
                output += value + "\n";
        }

        return output;
    }

    public static String getJavaPlatformCaps()
    {
        String output = "";

        int ONE_MB = 1 << 20; // (1024 * 1024);

        Properties properties = new Properties();
        properties.put("Detected JVM bitness", (IS_64BIT ? "64 bit" : "32 bit"));
        properties.put("Number or cores", Integer.toString(RUNTIME.availableProcessors()));
        properties.put("Maximum JVM memory usage", (RUNTIME.maxMemory() / ONE_MB) + " MB");
        properties.put("Total JVM memory usage", (RUNTIME.totalMemory() / ONE_MB) + " MB");
        properties.put("Settings folder", DataFolder.get());
        output += propertiesToString(properties);

        // Java properties
        output += "\n";
        output += propertiesToString(System.getProperties());

        // Environment variables
        properties.clear();
        for ( Map.Entry entry : System.getenv().entrySet() )
            properties.put( entry.getKey(), entry.getValue() );

        output += "\n";
        output += propertiesToString(properties);

        return output;
    }

    /**
    *  Reports all relevant machine settings.
    */
    private static void reportMachineSettings()
    {
        println(getJavaPlatformCaps());
    }

    /**
    *  Reports memory usage.
    */
    private static void reportMemoryUsage()
    {
        int ONE_MB = 1 << 20; // (1024 * 1024);
        int ONE_KB = 1 << 10; // (1024);

        println(VERSION);
        println("---------------------------------------------------------------------");
        println("Max Memory Set to: " + (RUNTIME.maxMemory() / ONE_MB) + " MB");
        println("Total memory: " + (RUNTIME.totalMemory() / ONE_KB) + " KB");
        println("Free memory: " + (RUNTIME.freeMemory() /ONE_KB) + " KB");
    }

    /**
    *  Static method that is used from void main to set the logging parameters.
    */
    private static void setLoggingParameters(boolean consoleOutput, boolean fileOutput)
    {
        setIsMultiCoreOn(USE_MULTICORE_PROCESS);
        setIsLoggingOn(consoleOutput);

        if (fileOutput)
            setFileOutput( new FileOutput("AllConsoleOutputMessages.log", true) );
    }

    /**
    *  Static method that is used from void main to set the application's look and feel.
    *  Supports either the native or the new Nimbus L&F for Java 1.6 Update 10 or newer.
    */
    private static void setLookAndFeel(boolean nimbusLAF)
    {
        try
        {
            if (nimbusLAF)
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
            else
                UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );

            UIManager.put("ScrollBar.minimumThumbSize", new Dimension(32, 32));
        }
        catch (UnsupportedLookAndFeelException e1)
        {
            if (DEBUG_BUILD) println("UnsupportedLookAndFeelException with setLookAndFeel():\n" + e1.getMessage());
        }
        catch (ClassNotFoundException e2)
        {
            if (DEBUG_BUILD) println("ClassNotFoundException with setLookAndFeel():\n" + e2.getMessage());
        }
        catch (InstantiationException e3)
        {
            if (DEBUG_BUILD) println("InstantiationException with setLookAndFeel():\n" + e3.getMessage());
        }
        catch (IllegalAccessException e4)
        {
            if (DEBUG_BUILD) println("IllegalAccessException with setLookAndFeel():\n" + e4.getMessage());
        }
    }

    /**
    *  Static method that is used from void main to show the command-line help page or
    *  is called if a wrong command-line option is inserted and shows again the help page.
    */
    private static void usage(String msg)
    {
        System.err.println(msg);
        System.err.println();
        if (DEBUG_BUILD)
        {
            System.err.println(" -consoleOutput [on|off]             : consoleOutput used for console output (default off)");
            System.err.println(" -fileOutput [on|off]                : fileOutput used for file logging (default off)");
        }
        System.err.println(" -loadFromRepository rep             : loadFromRepository used to load datasets");
        System.err.println("                                       from a web repository");
        System.err.println(" -loadDataSets datasets              : loadDataSets used to load datasets");
        System.err.println("                                       from a web repository");
        System.err.println(" -useShaders [on|off]                : use shaders support for the renderer (default on)");
        System.err.println(" -nimbusLAF [on|off]                 : set the Nimbus look and feel (default off)");
        System.err.println(" -useDefaultPreferences              : use the defaults for all saved preferences");
        System.err.println(" -usePreference <preference> <value> : set a specific preference to some value");
        System.err.println(" -help                               : prints out this help page");

        System.exit(0);
    }

    /**
    *  Static method to initialize the needed JOGL libraries for OpenGL rendering.
    */
    private static boolean initJOGLNativeLibraries()
    {
        // This prevents JOGL (2) from trying to load the native libraries from the official jar files, instead
        // falling back on loading extracted versions which we create from our jar
        System.setProperty("jogamp.gluegen.UseTempJarCache", "false");

        if( LoadNativeLibrary.extractNativeLibrary("gluegen-rt") == null )
            return false;

        if( LoadNativeLibrary.extractNativeLibrary("jogl_desktop") == null )
            return false;

        if( LoadNativeLibrary.extractNativeLibrary("nativewindow_awt") == null )
            return false;

        String osName = System.getProperty("os.name");

        if( osName.startsWith("Windows") )
        {
            if( LoadNativeLibrary.extractNativeLibrary("nativewindow_win32") == null )
                return false;
        }
        else if( osName.startsWith("Linux") )
        {
            if( LoadNativeLibrary.extractNativeLibrary("nativewindow_x11") == null )
                return false;
        }
        else if( osName.startsWith("Mac") || osName.startsWith("Darwin") )
        {
            if( LoadNativeLibrary.extractNativeLibrary("nativewindow_macosx") == null )
                return false;
        }
        else
            return false;

        LoadNativeLibrary.setJavaLibraryPath();

        return true;
    }

    /**
    *  Static method to initialize the needed JOCL libraries for OpenCL GPU Computing.
    */
    private static boolean initJOCLNativeLibraries()
    {
        if ( !LoadNativeLibrary.loadNativeLibrary("jocl") )
            return false;

        LoadNativeLibrary.setJavaLibraryPath();

        return true;
    }

    /**
    *  The void main entry point of the BioLayoutExpress3D framework.
    */
    public static void main(String[] args)
    {
        
	// If debugging with arguments, must be command-line.    
	if (DEBUG_BUILD)
            if (args.length > 0)
                System.out.println("Command-line command detected:\n");

        // Initialise variables for filename, output, data etc.
	String fileName = "";
        boolean consoleOutput = false;
        boolean fileOutput = false;
        String repository = "";
        String dataSets = "";
        // use this complex way for parsing the USE_SHADERS_PROCESS value so as to avoid a weird OpenGL flicker effect in Windows OSs
        // which only happens when the USE_SHADERS_PROCESS variables are being accessed within void main!!!
        boolean hasChosenUseShadersProcessCommandLine = false;
        boolean useShadersProcess = false;
        boolean nimbusLAF = false;
        boolean useDefaultSettings = false;
        
	// Set up a map to store preferences in.
	Map<String, String> preferences = new HashMap<String, String>();

        int i = 0;
        try
        {
	    // For all the arguments given...
            for (; i < args.length; i++)
            {
		// Tell the user whether console output on, file log output on, web repository on etc.
                if ("-consoleOutput".equals(args[i]))
                {
                    consoleOutput = "on".equals( args[++i].toLowerCase() );
                    if (DEBUG_BUILD) System.out.println("Now starting with console output: " + Boolean.toString(consoleOutput));
                }
                else if ("-fileOutput".equals(args[i]))
                {
                    fileOutput = "on".equals( args[++i].toLowerCase() );
                    if (DEBUG_BUILD) System.out.println("Now starting with file log output enabled: " + Boolean.toString(fileOutput));
                }
                else if ("-loadFromRepository".equals(args[i]))
                {
                    repository = args[++i];
                    if (DEBUG_BUILD) System.out.println("Now starting with a web repository:\n\"" + repository + "\"");
                }
                else if ("-loadDataSets".equals(args[i]))
                {
                    dataSets = args[++i];
                    if (DEBUG_BUILD) System.out.println("Now starting with loading dataSets:\n\"" + dataSets + "\"");
                }
                else if ("-useShaders".equals(args[i]))
                {
                    hasChosenUseShadersProcessCommandLine = true;
                    useShadersProcess = "on".equals( args[++i].toLowerCase() );
                    if (DEBUG_BUILD) System.out.println("Now starting with using shaders process: " + Boolean.toString(useShadersProcess));
                }
                else if ("-nimbusLAF".equals(args[i]))
                {
                    nimbusLAF = "on".equals( args[++i].toLowerCase() );
                    if (DEBUG_BUILD) System.out.println("Now starting with Nimbus L&F: " + Boolean.toString(nimbusLAF));
                }
                else if ("-usePreference".equals(args[i]))
                {
                    String pref = args[++i].toLowerCase();
                    String value = args[++i];

                    preferences.put(pref, value);

                    if (DEBUG_BUILD)
                    {
                        System.out.println("Setting preference " + pref + " to " + value);
                    }
                }
                else if ("-useDefaultPreferences".equals(args[i]))
                {
                    useDefaultSettings = true;
                }
                else if ("-help".equals(args[i]))
                {
                    usage("Help page requested.");
                }
                else
                {
		    // New file with arguments args[i], guessing args[i] is what is set as filename?
                    File f = new File(args[i]);
                    if (f.exists())
                    {
                        if (!fileName.isEmpty())
                        {
                            usage("Only one file may be specified on the command line.");
                        }
                        else
                        {
                            fileName = args[i];
                        }
                    }
                    else
                    {
                        usage("Invalid argument: " + args[i]);
                    }
                }
            }

            if (DEBUG_BUILD) System.out.println();
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            usage("Argument at position " + i + " missing:\n" + e.getMessage());
        }

        System.out.println(getJavaPlatformCaps());

        // to solve a  flicker bug with some hardware configurations and JOGL in Windows platforms
        // caution, it has to be enabled through command-line for the webstart version!
        if ( LoadNativeLibrary.isWin() )
            System.setProperty("sun.java2d.noddraw", "true");

        // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=7075600
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");

        if (DEBUG_BUILD)
            setLoggingParameters(consoleOutput, fileOutput);
        // Default look and feel is nimbus, I guess.
	setLookAndFeel(nimbusLAF);

	// See if JOGL libraries are working. Quit if they're not.
        if ( initJOGLNativeLibraries() )
        {
            if (DEBUG_BUILD) println("\nJOGL Libraries working!\n");
        }
        else
        {
            if (DEBUG_BUILD) println("Error: JOGL Libraries not installed or found!\n");
            JOptionPane.showMessageDialog(null, "Error: JOGL Libraries not installed or found!", "JOGL error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

	// See if JOCL libraries are working. Quit if they're not.

        if ( initJOCLNativeLibraries() )
        {
            OPENCL_GPU_COMPUTING_ENABLED = true;
            if (DEBUG_BUILD) println("JOCL Libraries working!\n");
        }
        else
        {
            if (DEBUG_BUILD) println("Error: JOCL Library not installed or found!\n");
        }

        if (DEBUG_BUILD)
        {
            reportMachineSettings();
            reportMemoryUsage();
        }

        Thread.setDefaultUncaughtExceptionHandler(new ThreadExceptionHandler());

	// Make an instance of the layout class with all the parameters.
        Layout layout = new Layout( ( !fileName.isEmpty() ) ? fileName : "", !dataSets.isEmpty(),
                repository, dataSets, useDefaultSettings, preferences,
                hasChosenUseShadersProcessCommandLine, useShadersProcess );
    }
}
