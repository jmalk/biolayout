package org.BioLayoutExpress3D.StaticLibraries;

import java.awt.*;
import java.io.*;
import java.net.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* InitDesktop is a final wrapper class containing only static desktop related methods. It uses the Java 1.6 Desktop API.
* Using a Singleton design pattern so as to instantiate the Desktop object only once.
*
* @author Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*/

public final class InitDesktop
{

    /**
    *  Reference for the InitDesktop class.
    */
    private static final Desktop desktop = getDesktop();

    /**
    *  Returns a Desktop reference to be used with all the static methods.
    *  It instantiates the Desktop only once (following the Singleton design pattern).
    */
    private static Desktop getDesktop()
    {
        return Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
    }

    /**
    *  Launches the default browser to display a URI.
    */
    public static void browse(URI uri)
    {
        if ( (desktop != null) && desktop.isSupported(Desktop.Action.BROWSE) )
        {
            try
            {
                desktop.browse(uri);
            }
            catch(Exception exc)
            {
                if (DEBUG_BUILD) println("Problem with InitDesktop.browse(URI):\n" + exc.getMessage());
            }
        }
        else
        {
            if (DEBUG_BUILD) println("Desktop.browse() is not supported in InitDesktop.browse(URI) for this OS platform.");
        }
    }

    /**
    *  Launches the default browser to display a URL.
    */
    public static void browse(URL url)
    {
        if ( (desktop != null) && desktop.isSupported(Desktop.Action.BROWSE) )
        {
            try
            {
                desktop.browse( url.toURI() );
            }
            catch(Exception exc)
            {
                if (DEBUG_BUILD) println("Problem with InitDesktop.browse(URL):\n" + exc.getMessage());
            }
        }
        else
        {
            if (DEBUG_BUILD) println("Desktop.browse() is not supported in InitDesktop.browse(URL) for this OS platform.");
        }
    }

    /**
    *  Launches the default browser to display a URL string.
    */
    public static void browse(String urlString)
    {
        if ( (desktop != null) && desktop.isSupported(Desktop.Action.BROWSE) )
        {
            try
            {
                desktop.browse( URI.create(urlString) );
            }
            catch(Exception exc)
            {
                if (DEBUG_BUILD) println("Problem with InitDesktop.browse(String):\n" + exc.getMessage());
            }
        }
        else
        {
            if (DEBUG_BUILD) println("Desktop.browse() is not supported in InitDesktop.browse(String) for this OS platform.");
        }
    }

    /**
    *  Launches the associated editor application and opens a file for editing.
    */
    public static void edit(File file)
    {
        if ( (desktop != null) && desktop.isSupported(Desktop.Action.EDIT) )
        {
            try
            {
                desktop.edit(file);
            }
            catch(Exception exc)
            {
                if (DEBUG_BUILD) println("Problem with InitDesktop.edit(File):\n" + exc.getMessage());
            }
        }
        else
        {
            if (DEBUG_BUILD) println("Desktop.browse() is not supported in InitDesktop.edit(File) for this OS platform.");
        }
    }

    /**
    *  Launches the mail composing window of the user default mail client.
    */
    public static void mail()
    {
        if ( (desktop != null) && desktop.isSupported(Desktop.Action.MAIL) )
        {
            try
            {
                desktop.mail();
            }
            catch(Exception exc)
            {
                if (DEBUG_BUILD) println("Problem with InitDesktop.mail():\n" + exc.getMessage());
            }
        }
        else
        {
            if (DEBUG_BUILD) println("Desktop.browse() is not supported in InitDesktop.mail() for this OS platform.");
        }
    }

    /**
    *  Launches the mail composing window of the user default mail client, filling the message fields specified by a mailto: URI.
    */
    public static void mail(URI uri)
    {
        if ( (desktop != null) && desktop.isSupported(Desktop.Action.MAIL) )
        {
            try
            {
                desktop.mail(uri);
            }
            catch(Exception exc)
            {
                if (DEBUG_BUILD) println("Problem with InitDesktop.mail(URI):\n" + exc.getMessage());
            }
        }
        else
        {
            if (DEBUG_BUILD) println("Desktop.browse() is not supported in InitDesktop.mail(URI) for this OS platform.");
        }
    }

    /**
    *  Launches the mail composing window of the user default mail client, filling the message fields specified by a mailto: URL.
    */
    public static void mail(URL url)
    {
        if ( (desktop != null) && desktop.isSupported(Desktop.Action.MAIL) )
        {
            try
            {
                desktop.mail( url.toURI() );
            }
            catch(Exception exc)
            {
                if (DEBUG_BUILD) println("Problem with InitDesktop.mail(URL):\n" + exc.getMessage());
            }
        }
        else
        {
            if (DEBUG_BUILD) println("Desktop.browse() is not supported in InitDesktop.mail(URL) for this OS platform.");
        }
    }

    /**
    *  Launches the mail composing window of the user default mail client, filling the message fields specified by a mailto: URLString.
    */
    public static void mail(String urlString)
    {
        if ( (desktop != null) && desktop.isSupported(Desktop.Action.MAIL) )
        {
            try
            {
                desktop.mail( URI.create(urlString) );
            }
            catch(Exception exc)
            {
                if (DEBUG_BUILD) println("Problem with InitDesktop.mail(String):\n" + exc.getMessage());
            }
        }
        else
        {
            if (DEBUG_BUILD) println("Desktop.browse() is not supported in InitDesktop.mail(String) for this OS platform.");
        }
    }

    /**
    *  Launches the associated application to open the file.
    */
    public static void open(File file)
    {
        if ( (desktop != null) && desktop.isSupported(Desktop.Action.OPEN) )
        {
            try
            {
                desktop.open(file);
            }
            catch(Exception exc)
            {
                if (DEBUG_BUILD) println("Problem with InitDesktop.open(File):\n" + exc.getMessage());
            }
        }
        else
        {
            if (DEBUG_BUILD) println("Desktop.browse() is not supported in InitDesktop.open(File) for this OS platform.");
        }
    }

    /**
    *  Prints a file with the native desktop printing facility, using the associated application's print command.
    */
    public static void print(File file)
    {
        if ( (desktop != null) && desktop.isSupported(Desktop.Action.PRINT) )
        {
            try
            {
                desktop.print(file);
            }
            catch(Exception exc)
            {
                if (DEBUG_BUILD) println("Problem with InitDesktop.print(File):\n" + exc.getMessage());
            }
        }
        else
        {
            if (DEBUG_BUILD) println("Desktop.browse() is not supported in InitDesktop.print(File) for this OS platform.");
        }
    }


}