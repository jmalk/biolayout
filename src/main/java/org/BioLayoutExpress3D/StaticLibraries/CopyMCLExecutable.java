package org.BioLayoutExpress3D.StaticLibraries;

import java.io.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;
import org.BioLayoutExpress3D.Environment.DataFolder;

/**
*
* CopyMCLExecutable is a final class containing only static methods for copying the MCL executable (and depending library) depending on running OS.
*
* @see org.BioLayoutExpress3D.Clustering.MCL.LayoutClusterMCL
* @author Thanos Theo, 2008-2009
* @version 3.0.0.0
*/

public final class CopyMCLExecutable
{

    /**
    *  The file path of the MCL executable to be copied from.
    */
    private static final String EXTRACT_FROM_MCL_FILE_PATH = "/Resources/MCL/";

    /**
    *  The file path of the MCL executable to be copied to.
    */
    public static final String EXTRACT_TO_MCL_FILE_PATH = "MCL" + ( ( LoadNativeLibrary.is64bit() ) ? "64" : "32" ) + "/";

    /**
    *  The file path of the MCL executable to be copied from the OS-based specific path.
    */
    private static final String[] EXTRACT_FROM_MCL_OS_SPECIFIC_PATH = { "Win32/", "Win64/", "Linux32/", "Linux64/", "Mac/" };

    /**
    *  The MCL relevant files to copy.
    */
    private static final String[] MCL_FILES_TO_COPY = { "mcl", "cygwin1.dll" };

    /**
    *  Copies the MCL executable (and depending library) depending on running OS.
    */
    public static boolean copyMCLExecutable()
    {
        boolean[] OSSpecificType = LoadNativeLibrary.checkRunningOSAndReturnOSSpecificType();
        String[] OSSpecificMCLExecutableNames = returnOSSpecificMCLExecutableName(OSSpecificType);

        int OSSPecificPathIndex = 0;
        for (boolean position : OSSpecificType)
        {
            if (position)
            {
                break;
            }

            OSSPecificPathIndex++;
        }

        String baseResourcePath = EXTRACT_FROM_MCL_FILE_PATH +
                EXTRACT_FROM_MCL_OS_SPECIFIC_PATH[OSSPecificPathIndex];

        String resourceName = baseResourcePath + OSSpecificMCLExecutableNames[0];
        String extractedFileName = LoadNativeLibrary.extractResource(resourceName, EXTRACT_TO_MCL_FILE_PATH);
        if ( extractedFileName != null)
        {
            File extractedFile = new File(extractedFileName);
            extractedFile.setExecutable(true);
        }
        else
        {
            if (DEBUG_BUILD)
            {
                println("Failed to extract " + resourceName);
            }

            return false;
        }

        if (OSSpecificMCLExecutableNames.length > 1)
        {
            resourceName = baseResourcePath + OSSpecificMCLExecutableNames[1];
            if (LoadNativeLibrary.extractResource(resourceName, EXTRACT_TO_MCL_FILE_PATH) == null)
            {
                if (DEBUG_BUILD)
                {
                    println("Failed to extract " + resourceName);
                }

                return false;
            }
        }

        return true;
    }

    /**
    *  Returns the OS specific MCL executable (and depending library).
    */
    private static String[] returnOSSpecificMCLExecutableName(boolean[] OSSpecificType)
    {
        // if Windows OS type, put a .exe at end of executable and add the Cygwin library as well
        String[] OSSpecificMCLExecutableName = (OSSpecificType[0] || OSSpecificType[1]) ? new String[] { MCL_FILES_TO_COPY[0] + ".exe", MCL_FILES_TO_COPY[1] } : new String[] { MCL_FILES_TO_COPY[0] };

        if (DEBUG_BUILD) println("OS specific MCL executable name: " + OSSpecificMCLExecutableName[0]);

        return OSSpecificMCLExecutableName;
    }


}