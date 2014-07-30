package org.BioLayoutExpress3D.Simulation;

import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.CoreUI.Dialogs.*;
import org.BioLayoutExpress3D.DataStructures.*;
import org.BioLayoutExpress3D.Files.*;
import org.BioLayoutExpress3D.Files.Parsers.*;
import org.BioLayoutExpress3D.Network.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.Simulation.SignalingPetriNetSimulation.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* SignalingPetriNetLoadSimulation is the class representing the SPN load simulation process.
*
* @see org.BioLayoutExpress3D.Simulation.SignalingPetriNetSimulation
* @author Thanos Theo, 2009-2010-2011
* @version 3.0.0.0
*
*/

public class SignalingPetriNetLoadSimulation extends CoreParser
{

    private BioLayoutExpress3DFileFilter bioLayoutExpress3DFileFilter = null;
    private LayoutProgressBarDialog layoutProgressBarDialog = null;
    private boolean cancelParse = false;
    private AbstractAction signalingPetriNetLoadSimulationAction = null;

    /**
    *  The constructor of the SignalingPetriNetLoadSimulation class.
    */
    public SignalingPetriNetLoadSimulation(NetworkContainer nc, LayoutFrame layoutFrame)
    {
        super(nc, layoutFrame);

        layoutProgressBarDialog = layoutFrame.getLayoutProgressBar();

        initActions();
        initComponents();
    }

    /**
    *  Initializes the actions.
    */
    private void initActions()
    {
        signalingPetriNetLoadSimulationAction = new AbstractAction("Load Simulation Data")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111252333444555689L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                loadSignalingPetriNetSimulation();

                if ( !layoutFrame.getLayoutAnimationControlDialog().getAnimationControlDialogAction().isEnabled() )
                    layoutFrame.getLayoutAnimationControlDialog().getAnimationControlDialogAction().setEnabled(true);
            }
        };
        signalingPetriNetLoadSimulationAction.setEnabled(false);
    }

    /**
    *  This method is called from within the constructor to initialize the dialog.
    */
    private void initComponents()
    {
        bioLayoutExpress3DFileFilter = new BioLayoutExpress3DFileFilter( SupportedSimulationFileTypes.values() );
    }

    /**
    *  Loads an SPN simulation.
    */
    private void loadSignalingPetriNetSimulation()
    {
        String loadSimulationFilePath = FILE_CHOOSER_PATH.get().substring(0, FILE_CHOOSER_PATH.get().lastIndexOf( System.getProperty("file.separator") ) + 1);
        JFileChooser fileChooser = new JFileChooser(loadSimulationFilePath);
        fileChooser.setDialogTitle("Load Simulation File");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setFileFilter(bioLayoutExpress3DFileFilter);

        if (fileChooser.showOpenDialog(layoutFrame) == JFileChooser.APPROVE_OPTION)
        {
            file = fileChooser.getSelectedFile();
            FILE_CHOOSER_PATH.set( file.getAbsolutePath() );
            loadSignalingPetriNetSimulationFile(file);
        }
    }

    /**
    *  Loads a given SPN simulation file.
    */
    private void loadSignalingPetriNetSimulationFile(File file)
    {
        if ( !file.exists() )
        {
            JOptionPane.showMessageDialog(layoutFrame, "File does not exist, please check the file!", "Error: File does not exist", JOptionPane.ERROR_MESSAGE);
        }
        else
        {
            if ( bioLayoutExpress3DFileFilter.accept(file) )
            {
                runParseProcess(Thread.NORM_PRIORITY, file);
            }
            else
            {
                JOptionPane.showMessageDialog(layoutFrame, "Not supported BioLayout Express 3D import file type!\n" + bioLayoutExpress3DFileFilter.getDescription(),
                                                           "Error: Not supported import file type", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
    *   Process a light-weight thread using the Adapter technique to avoid any load latencies.
    */
    private void runParseProcess(final int threadPriority, final File loadFile)
    {
        Thread runLightWeightThread = new Thread( new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    // initializeAllDataStructures();
                    if ( !(init(loadFile, "") && parse() ) )
                        JOptionPane.showMessageDialog(layoutFrame, "Parse Error with import file " + file.getName() + ":\nPlease check the file format and retry loading!", "Parse Error", JOptionPane.ERROR_MESSAGE);
                }
                catch (OutOfMemoryError memErr)
                {
                    if (DEBUG_BUILD) println("Out of Memory Error with parsing the file in runLightWeightThread():\n" + memErr.getMessage());

                    layoutProgressBarDialog.endProgressBar();
                    layoutProgressBarDialog.stopProgressBar();

                    throwableErrorMessageDialogReport(memErr, "Out of Memory Error", loadFile.getName());
                }
                catch (Exception exc)
                {
                    if (DEBUG_BUILD) println("Exception in runLightWeightThread()\n" + exc.getMessage() );

                    layoutProgressBarDialog.endProgressBar();
                    layoutProgressBarDialog.stopProgressBar();

                    throwableErrorMessageDialogReport(exc, "File Load Error", loadFile.getName());
                }
                finally
                {
                    openSimulationAnimationControlDialogWindowProcess(threadPriority);
                }
            }


        } );

        runLightWeightThread.setPriority(threadPriority);
        runLightWeightThread.start();
    }

    /**
    *  Reports a throwable exception to the user through an UI dialog.
    */
    private void throwableErrorMessageDialogReport(Throwable thrw, String typeOfError, String fileName)
    {
        int option = JOptionPane.showConfirmDialog(layoutFrame, typeOfError + " for parsing file " + fileName + ":\n" + thrw.getMessage() + "\n\nWould you like to view a detailed StackTrace dump report?\n\n", typeOfError, JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
        if (option == JOptionPane.YES_OPTION)
        {
            StringBuilder stackTraceStringBuffer = new StringBuilder();
            for (StackTraceElement stackTraceElement : thrw.getStackTrace())
                stackTraceStringBuffer.append( stackTraceElement.toString() ).append("\n");
            JOptionPane.showMessageDialog(layoutFrame, "StackTrace Dump Report With " + typeOfError + " for parsing file " + fileName + ":\n\n" + stackTraceStringBuffer.toString() + "\n", "StackTrace Dump Report With " + typeOfError, JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
    *  Opens the Simulation Animation Control Dialog Window.
    */
    private void openSimulationAnimationControlDialogWindowProcess(int threadPriority)
    {
        Thread runLightWeightThread = new Thread( new Runnable()
        {

            @Override
            public void run()
            {
                if (!cancelParse)
                {
                    if ( layoutFrame.getLayoutAnimationControlDialog().isVisible() )
                    {
                        JOptionPane.showMessageDialog(layoutFrame, "SPN Load Simulation Process Finished.", "SPN Load Simulation Process", JOptionPane.INFORMATION_MESSAGE);
                    }
                    else
                    {
                        int dialogReturnValue = JOptionPane.showConfirmDialog(layoutFrame, "SPN Load Simulation Process Finished.\nOpen the Simulation Animation Control Dialog?", "SPN Load Simulation Process", JOptionPane.YES_NO_OPTION);
                        if (dialogReturnValue == JOptionPane.YES_OPTION)
                            layoutFrame.getLayoutAnimationControlDialog().openDialogWindow();
                    }
                }
            }


        } );

        runLightWeightThread.setPriority(threadPriority);
        runLightWeightThread.start();
    }

    /**
    *  Loads a given simulation file.
    */
    @Override
    public boolean parse()
    {
        int totalLines = 0;
        int counter = 0;

        cancelParse = false;
        isSuccessful = false;

        try
        {
            fileReaderBuffered = new BufferedReader( new FileReader(file) );
            fileReaderCounter  = new BufferedReader( new FileReader(file) );

            while ( ( line = fileReaderCounter.readLine() ) != null )
                totalLines++;

            // skip first line, versioning line
            fileReaderBuffered.readLine();
            // second line gives back SPN simulation details
            Tuple5<String, Integer, Integer, Integer, SignalingPetriNetSimulation.ErrorType> allDetailsTuple5 =
                    getSimulationAllDetails( fileReaderBuffered.readLine().split("\t") );

            if ( allDetailsTuple5.first.equals( layoutFrame.getFileNameLoaded() ) ) // the simulation results loading should be the same as the loaded graph, else abort
            {
                layoutProgressBarDialog.prepareProgressBar(totalLines - 2, "Parsing " + file.getName() + " SPN Simulation File...");
                layoutProgressBarDialog.startProgressBar();

                layoutFrame.getSignalingPetriNetSimulationDialog().initializeResultsArray(allDetailsTuple5.second,
                        allDetailsTuple5.third, allDetailsTuple5.fourth, allDetailsTuple5.fifth);

                // skip third line, column naming line
                fileReaderBuffered.readLine();
                layoutProgressBarDialog.incrementProgress(++counter);

                String[] allDetails = null;
                while ( ( line = fileReaderBuffered.readLine() ) != null )
                {
                    allDetails = line.split("\t");
                    if (allDetailsTuple5.fifth == SignalingPetriNetSimulation.ErrorType.NONE)
                    {
                        for (int i = 3; i < allDetails.length; i++)
                        {
                            // for every timeblock, which starts at column 3, column one has the nodeID
                            layoutFrame.getSignalingPetriNetSimulationDialog().addResultToResultsArray(
                                    Integer.parseInt(allDetails[0]),
                                    i - 3,
                                    Float.parseFloat(allDetails[i]),
                                    0.0f);
                        }
                    }
                    else
                    {
                        for (int i = 3; i < allDetails.length; i += 2)
                        {
                            // for every timeblock, which starts at column 3, column one has the nodeID
                            layoutFrame.getSignalingPetriNetSimulationDialog().addResultToResultsArray(
                                    Integer.parseInt(allDetails[0]),
                                    (i - 3) / 2,
                                    Float.parseFloat(allDetails[i]),
                                    Float.parseFloat(allDetails[i + 1]));
                        }
                    }

                    layoutProgressBarDialog.incrementProgress(++counter);
                }

                layoutFrame.getLayoutAnimationControlDialog().setMaxValueInTextField( layoutFrame.getSignalingPetriNetSimulationDialog().findMaxValueFromResultsArray() );
            }
            else
            {
                cancelParse = true;
                JOptionPane.showMessageDialog(layoutFrame, "This SPN Simulation results file is derived from a different SPN graph.\nSPN Load Simulation process is now aborted.", "SPN Load Simulation Aborted", JOptionPane.INFORMATION_MESSAGE);
            }

            isSuccessful = true;
        }
        catch (IOException ioe)
        {
            if (DEBUG_BUILD) println("IOException in SignalingPetriNetLoadSimulation.parse():\n" + ioe.getMessage());
        }
        finally
        {
            try
            {
                fileReaderCounter.close();
                fileReaderBuffered.close();
            }
            catch (IOException ioe)
            {
                if (DEBUG_BUILD) println("IOException while closing streams in SignalingPetriNetLoadSimulation.parse():\n" + ioe.getMessage());
            }
            finally
            {
                layoutProgressBarDialog.endProgressBar();
                layoutProgressBarDialog.stopProgressBar();
            }
        }

        return isSuccessful;
    }

    private Tuple5<String, Integer, Integer, Integer, SignalingPetriNetSimulation.ErrorType> getSimulationAllDetails(String[] allDetails)
    {
        // get rid of starting/ending " enclosing characters
        for (int i = 0; i < allDetails.length; i++)
            allDetails[i] = allDetails[i].substring(1, allDetails[i].length() - 1);

        return Tuples.tuple(allDetails[1], Integer.parseInt( allDetails[2].substring( SAVE_DETAILS_DATA_COLUMN_NAME_NODES.length(), allDetails[2].length() ) ),
                                           Integer.parseInt( allDetails[3].substring( SAVE_DETAILS_DATA_COLUMN_NAME_TIMEBLOCKS.length(), allDetails[3].length() ) ),
                                           Integer.parseInt( allDetails[4].substring( SAVE_DETAILS_DATA_COLUMN_NAME_RUNS.length(), allDetails[4].length() ) ),
                                           SignalingPetriNetSimulation.ErrorType.valueOf(allDetails[5].substring( SAVE_DETAILS_DATA_COLUMN_NAME_ERROR.length(), allDetails[5].length() ))
                );
    }

    public AbstractAction getSignalingPetriNetLoadSimulationAction()
    {
        return signalingPetriNetLoadSimulationAction;
    }


}