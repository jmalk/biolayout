package org.BioLayoutExpress3D.Environment;

import java.io.File;
import org.BioLayoutExpress3D.Utils.Path;
import org.BioLayoutExpress3D.StaticLibraries.LoadNativeLibrary;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
 *
 * Retrieve a folder that is writable by the current user in which we store settings and/or extract data
 *
 * @author Tim Angus
 */
public class DataFolder
{
    private static final String BASENAME = "BioLayoutExpress3D";
    private static final String BASENAME_UNIX = ".biolayout";

    public static String get()
    {
        String folder;

        if (GlobalEnvironment.IS_WIN)
        {
            folder = WindowsAppDataFolder();
        }
        else if (GlobalEnvironment.IS_MAC)
        {
            folder = MacApplicationSupportFolder();
        }
        else
        {
            folder = UnixHomeDirectory();
        }

        if (folder == null || folder.isEmpty())
        {
            folder = LoadNativeLibrary.findCurrentProgramDirectory();
        }

        File f = new File(folder);
        if (!f.exists())
        {
            f.mkdirs();
        }

        return folder;
    }

    private static String WindowsAppDataFolder()
    {
        String APPDATA = System.getenv("APPDATA");

        if (!APPDATA.isEmpty())
        {
            return Path.combine(APPDATA, BASENAME);
        }

        return null;
    }

    private static String MacApplicationSupportFolder()
    {
        String userHome = System.getProperty("user.home");

        if (!userHome.isEmpty())
        {
            return Path.combine(userHome, "Library", "Application Support", BASENAME);
        }

        return null;
    }

    private static String UnixHomeDirectory()
    {
        String userHome = System.getProperty("user.home");

        if (!userHome.isEmpty())
        {
            return Path.combine(userHome, BASENAME_UNIX);
        }

        return null;
    }
}
