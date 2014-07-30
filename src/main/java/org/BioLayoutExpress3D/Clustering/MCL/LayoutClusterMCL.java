package org.BioLayoutExpress3D.Clustering.MCL;

import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.Graph.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;
import org.BioLayoutExpress3D.Environment.DataFolder;

/**
*
* @author Leon Goldovsky, full refactoring by Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public final class LayoutClusterMCL
{
    private LayoutFrame layoutFrame = null;
    private Graph graph = null;
    private MCLWindowDialog MCL_windowDialog = null;
    private File tempInput = null;
    private AbstractAction clusterMCLAction = null;

    /**
    *  Variable used for loading the native library only once (no use of re-loading the library).
    */
    private static boolean hasOnceLoadedMCLExecutable = false;

    public LayoutClusterMCL(LayoutFrame layoutFrame, Graph graph)
    {
        this.layoutFrame = layoutFrame;
        this.graph = graph;

        if (!hasOnceLoadedMCLExecutable)
            hasOnceLoadedMCLExecutable = CopyMCLExecutable.copyMCLExecutable();

        initActions();
        checkMCLExecutable();
    }

    private void initActions()
    {
        clusterMCLAction = new AbstractAction("Cluster Graph Using MCL")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555668L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                // make sure all nodes are deselected first
                graph.getSelectionManager().deselectAll();

                if (MCL_windowDialog == null)
                    createAndCalcMCLWindowFrame();
                else if ( !MCL_windowDialog.isMCLWindowVisible() )
                    createAndCalcMCLWindowFrame();
                else if ( MCL_windowDialog.isMCLWindowVisible() )
                    MCL_windowDialog.MCLWindowToFront();
            }
        };
    }

    private void createAndCalcMCLWindowFrame()
    {
        try
        {
            if (DEBUG_BUILD) println("Creating MCL Temporary File");

            tempInput = ( USE_INSTALL_DIR_FOR_MCL_TEMP_FILE.get() ) ?
                    new File(DataFolder.get(), CopyMCLExecutable.EXTRACT_TO_MCL_FILE_PATH + "MCL_Input.tmp") :
                    File.createTempFile("MCL_Input", "tmp");
            tempInput.deleteOnExit();

            MCL_windowDialog = new MCLWindowDialog(layoutFrame, graph, tempInput);
            Thread MCLWindowThread = new Thread(MCL_windowDialog, "MCL_windowDialog");
            MCLWindowThread.setPriority(Thread.NORM_PRIORITY);
            MCLWindowThread.start();
        }
        catch (Exception ex)
        {
            if (DEBUG_BUILD) println("Creation of MCL Temporary File failed!\n" + ex.getMessage());
        }
    }

    public void checkMCLExecutable()
    {
        // has to use the exec(String[]) method instead of the old exec(String) so as to avoid the Tokenizer of the second one omitting all whitespaces in file paths!!!
        String[] executableFileName = { new File( DataFolder.get(), CopyMCLExecutable.EXTRACT_TO_MCL_FILE_PATH + "mcl" + ( (IS_WIN) ? ".exe" : "" ) ).getAbsolutePath() };
        checkExecutable(executableFileName);
    }

    private void checkExecutable(String[] commands)
    {
        try
        {
            // Runtime runtime = Runtime.getRuntime();
            // Process proc = runtime.exec(commands);
            Process process = new ProcessBuilder(commands).start();

            // put a BufferedReader on the ls output
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( process.getInputStream() ) );

            // read the ls output
            if (DEBUG_BUILD)
            {
                String line = "";
                while ( ( line = bufferedReader.readLine() ) != null )
                    println(line);
            }

            bufferedReader.close();

            // check for ls failure
            try
            {
                if (DEBUG_BUILD)
                    if (process.waitFor() != 0)
                        println("exit value = " + process.exitValue());
            }
            catch (InterruptedException ex)
            {
                // restore the interuption status after catching InterruptedException
                Thread.currentThread().interrupt();
                if (DEBUG_BUILD) println("InterruptedException with the waitFor() method in the LayoutClusterMCL.checkExecutable() class & method:\n" + ex.getMessage());
            }

            clusterMCLAction.setEnabled(true);
        }
        catch (IOException ioEx)
        {
            if (DEBUG_BUILD) println("MCL not found!\n" + ioEx.getMessage());

            clusterMCLAction.setEnabled(false);
        }
    }

    public AbstractAction getClusterMCLAction()
    {
        return clusterMCLAction;
    }


}