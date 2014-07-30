package org.BioLayoutExpress3D.CoreUI.Services;

import java.util.*;
import javax.swing.*;
import org.BioLayoutExpress3D.Connections.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* LayoutOnlineServices is the class used to check for updates of the BioLayout Express 3D application.
*
* @author Thanos Theo, 2008-2009
* @version 3.0.0.0
*/

public final class LayoutOnlineServices extends HttpConnection
{

    /**
    *  Constant variables used for tracking program usage.
    */
    private static final String PHP_LINK_BUILD_PART = "?build=";
    private static final String PHP_LINK_VERSION_PART = "&version=";

    /**
    *  Constant variables used for downloading.
    */
    private static final String SERVER_DOWNLOADS_DIRECTORY = "/download/";
    private static final String CURRENT_SERVER_VERSION_CONTROL_FILE = "CurrentVersion.ctr";

    /**
    *  LayoutFrame reference.
    */
    private LayoutFrame layoutFrame = null;

    /**
    *  The first constructor of the LayoutOnlineServices class (without proxy related settings).
    */
    public LayoutOnlineServices(LayoutFrame layoutFrame)
    {
        super();
        this.layoutFrame = layoutFrame;
    }

    /**
    *  The second constructor of the LayoutOnlineServices class (with proxy related settings).
    */
    public LayoutOnlineServices(Proxy proxy, LayoutFrame layoutFrame)
    {
        super(proxy);
        this.layoutFrame = layoutFrame;
    }

    /**
    *  This method is the main method for checking application usage.
    *  Runs inside its own lightweight thread so as to not stall main program code execution.
    */
    public void checkApplicationUsage()
    {
        Thread runLightWeightThread = new Thread( new Runnable()
        {

            @Override
            public void run()
            {
                sendLinkWithHttpConnection( BIOLAYOUT_APPLICATION_USAGE_URL + PHP_LINK_BUILD_PART +
                        ( (DEBUG_BUILD) ? "Debug" : "Release" ) + PHP_LINK_VERSION_PART +
                        (TITLE_VERSION + TITLE_VERSION_NUMBER).replace(" ", ""));
            }


        }, "checkApplicationUsage" );

        runLightWeightThread.setPriority(Thread.NORM_PRIORITY);
        runLightWeightThread.start();
    }

    /**
    *  This method is the main method for checking for application updates.
    *  Runs inside its own lightweight thread so as to not stall main program code execution.
    */
    public void checkNowForApplicationUpdates(final boolean runOnStartup)
    {
        Thread runLightWeightThread = new Thread( new Runnable()
        {

            @Override
            public void run()
            {
                if (DEBUG_BUILD) println("Now checking for " + TITLE + " application updates");

                try
                {
                    ArrayList<String> currentServerVersionFileData = retrieveTextDataFromHttpConnection(BIOLAYOUT_EXPRESS_3D_DOMAIN_URL + SERVER_DOWNLOADS_DIRECTORY + CURRENT_SERVER_VERSION_CONTROL_FILE, true);

                    if ( getManagedToConnect() )
                    {
                        String serverVersionLine = TITLE_VERSION.trim().equals("Update") ? currentServerVersionFileData.get(0) : currentServerVersionFileData.get(1);
                        String[] runningVersionWords = (TITLE + TITLE_VERSION + TITLE_VERSION_NUMBER).split("\\s+");
                        String[] currentServerVersionWords = serverVersionLine.split("\\s+");
                        float runningVersion = Float.parseFloat(runningVersionWords[runningVersionWords.length - 1]);
                        float currentServerVersion = Float.parseFloat(currentServerVersionWords[currentServerVersionWords.length - 1]);

                        if (runningVersion >= currentServerVersion)
                        {
                            if (!runOnStartup)
                                JOptionPane.showMessageDialog(layoutFrame, "You are running the latest version " + currentServerVersion + " of BioLayout Express 3D!", "Latest Version Running!", JOptionPane.INFORMATION_MESSAGE);
                        }
                        else
                        {
                            if (runOnStartup)
                                LayoutFrame.sleep(10000);

                            int option = JOptionPane.showConfirmDialog(layoutFrame, "A newer version " + currentServerVersion + " of BioLayout Express 3D is available!\n  Would you like to visit the Downloads page now ?", "Newer Version of BioLayout Express 3D found!", JOptionPane.YES_NO_OPTION);
                            if (option == JOptionPane.YES_OPTION)
                            {
                                InitDesktop.browse(BIOLAYOUT_EXPRESS_3D_DOMAIN_URL + SERVER_DOWNLOADS_DIRECTORY);
                            }
                        }
                    }
                }
                catch (Exception exc)
                {
                    if (DEBUG_BUILD) println("Exception in checkNowForApplicationUpdate() method:\n" + exc.getMessage());
                }

                if ( !runOnStartup && !getManagedToConnect() )
                    JOptionPane.showMessageDialog(layoutFrame, "Check For Updates failed, probable connection error!", "Error: Check For Updates failed", JOptionPane.ERROR_MESSAGE);
            }


        }, "checkForApplicationUpdates" );

        runLightWeightThread.setPriority(Thread.NORM_PRIORITY);
        runLightWeightThread.start();
    }

    /**
    *  Sets the name of the check for updates http connection.
    */
    public void setCheckForUpdatesName(String checkForUpdates)
    {
        this.nameOfHttpConnection = checkForUpdates;
    }

    /**
    *  Gets the name of the check for updates http connection.
    */
    public String getCheckForUpdatesName()
    {
        return nameOfHttpConnection;
    }


}