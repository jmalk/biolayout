package org.BioLayoutExpress3D.Files;

import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.CoreUI.Dialogs.*;
import org.BioLayoutExpress3D.Expression.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import static java.lang.Math.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.Expression.ExpressionEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* The ExportCorrelationNodesEdgesTable class is used for exporting Correlation Nodes Edges Table As File functionality.
*
* @author Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*
*/

public final class ExportCorrelationNodesEdgesTable
{
    private LayoutFrame layoutFrame = null;
    private ExpressionData expressionData = null;

    private int[] allNodes = null;
    private int[] allEdges = null;
    private int minThreshold = 0;
    private int rangeThreshold = 0;

    private JFileChooser fileChooser = null;
    private AbstractAction exportCorrelationNodesEdgesTableAction = null;

    private FileNameExtensionFilter fileNameExtensionFilterCorrelationNodesEdgesTable = null;

    public ExportCorrelationNodesEdgesTable(LayoutFrame layoutFrame, ExpressionData expressionData)
    {
        this.layoutFrame = layoutFrame;
        this.expressionData = expressionData;

        allNodes = new int[101];
        allEdges = new int[101];

        initComponents();
    }

    private void initComponents()
    {
        fileNameExtensionFilterCorrelationNodesEdgesTable = new FileNameExtensionFilter( "Save as a Correlation Nodes Edges Table File", SupportedImportExportFileTypes.TXT.toString().toLowerCase() );

        String saveFilePath = FILE_CHOOSER_PATH.get().substring(0, FILE_CHOOSER_PATH.get().lastIndexOf( System.getProperty("file.separator") ) + 1);
        fileChooser = new JFileChooser(saveFilePath);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(fileNameExtensionFilterCorrelationNodesEdgesTable);

        exportCorrelationNodesEdgesTableAction = new AbstractAction("Correlation Nodes Edges Table As File...")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555715L;

            @Override
            public void actionPerformed(ActionEvent action)
            {
                setFileChooser("Save Correlation Nodes Edges Table As File");
                performPrecalculations();
                save();
            }
        };
        exportCorrelationNodesEdgesTableAction.setEnabled(false);
    }

    private void performPrecalculations()
    {
        int[][] counts = expressionData.getCounts();
        int totalRows = expressionData.getTotalRows();
        minThreshold = (int)floor(100.0f * STORED_CORRELATION_THRESHOLD);
        rangeThreshold = 100 - minThreshold;
        for (int i = minThreshold; i <= 100; i++)
            calculateDistances(i, counts, totalRows);
    }

    private void calculateDistances(int threshold, int[][] counts, int totalRows)
    {
        int nodesCounter = 0;
        int totalEdges = 0;
        int edgesCounter = 0;

        for (int i = 0; i < totalRows; i++)
        {
            edgesCounter = 0;
            for (int j = threshold; j <= 100; j++)
                if (counts[i][j] > 0)
                      edgesCounter += counts[i][j];

            if (edgesCounter > 0)
            {
                nodesCounter++;
                totalEdges += edgesCounter;
            }
        }

        allNodes[threshold] = nodesCounter;
        allEdges[threshold] = totalEdges / 2;
    }

    private void setFileChooser(String fileChooserTitle)
    {
        fileChooser.setDialogTitle(fileChooserTitle);
        fileChooser.setSelectedFile( new File( IOUtils.getPrefix( layoutFrame.getFileNameLoaded() ) ) );
    }

    public AbstractAction getExportCorrelationNodesEdgesTableAction()
    {
        return exportCorrelationNodesEdgesTableAction;
    }

    private void save()
    {
        int dialogReturnValue = 0;
        boolean doSaveFile = false;
        File saveFile = null;

        if (fileChooser.showSaveDialog(layoutFrame) == JFileChooser.APPROVE_OPTION)
        {
            String fileExtension = "";

            if ( fileChooser.getFileFilter().equals(fileNameExtensionFilterCorrelationNodesEdgesTable) )
            {
                fileExtension = fileNameExtensionFilterCorrelationNodesEdgesTable.getExtensions()[0];
            }
            else // default file extension will be the CorrelationNodesEdgesTable file format
            {
                fileExtension = fileNameExtensionFilterCorrelationNodesEdgesTable.getExtensions()[0];
            }

            String fileName = fileChooser.getSelectedFile().getAbsolutePath();
            fileName = IOUtils.removeMultipleExtensions(fileName, fileExtension);
            saveFile = new File(fileName + "." + fileExtension);

            if ( saveFile.exists() )
            {
                // Do you want to overwrite
                dialogReturnValue = JOptionPane.showConfirmDialog(layoutFrame, "This File Already Exists.\nDo you want to Overwrite it?", "This File Already Exists. Overwrite?", JOptionPane.YES_NO_CANCEL_OPTION);

                if (dialogReturnValue == JOptionPane.YES_OPTION)
                    doSaveFile = true;
            }
            else
            {
                doSaveFile = true;
            }
        }

        if (doSaveFile)
        {
            // saving process on its own thread, to effectively decouple it from the main GUI thread
            Thread runLightWeightThread = new Thread( new ExportCorrelationNodesEdgesTableProcess(saveFile) );
            runLightWeightThread.setPriority(Thread.NORM_PRIORITY);
            runLightWeightThread.start();
        }
    }

    private void saveCorrelationNodesEdgesTableFile(File saveFile)
    {
        LayoutProgressBarDialog layoutProgressBarDialog = layoutFrame.getLayoutProgressBar();
        FileWriter fileWriter = null;

        try
        {
            layoutProgressBarDialog.prepareProgressBar(3, "Now Saving Correlation Nodes Edges Table File..."); // 3 for three lines, as the file is being saved horizontally
            layoutProgressBarDialog.startProgressBar();

            fileWriter = new FileWriter(saveFile);
            fileWriter.write("//" + VERSION + " " + " Correlation Nodes Edges Table File\n");
            fileWriter.write("//EXPRESSION_DATA\t\"" + EXPRESSION_FILE + "\"\n");

            saveCorrelationNodesEdgesTableData(layoutProgressBarDialog, fileWriter);

            fileWriter.flush();
        }
        catch (IOException ioe)
        {
            if (DEBUG_BUILD) println("IOException in ExportCorrelationNodesEdgesTable.saveCorrelationNodesEdgesTableFile():\n" + ioe.getMessage());

            layoutProgressBarDialog.endProgressBar();
            layoutProgressBarDialog.stopProgressBar();
            JOptionPane.showMessageDialog(layoutFrame, "Something went wrong while saving the file:\n" + ioe.getMessage() + "\nPlease try again with a different file name/path/drive.", "Error with saving the file!", JOptionPane.ERROR_MESSAGE);
            save();
        }
        finally
        {
            try
            {
                if (fileWriter != null) fileWriter.close();
            }
            catch (IOException ioe)
            {
                if (DEBUG_BUILD) println("IOException while closing streams in ExportCorrelationNodesEdgesTable.saveCorrelationNodesEdgesTableFile():\n" + ioe.getMessage());
            }
            finally
            {
                layoutProgressBarDialog.endProgressBar();
                layoutProgressBarDialog.stopProgressBar();
            }
        }
    }

    private void saveCorrelationNodesEdgesTableData(LayoutProgressBarDialog layoutProgressBarDialog, FileWriter fileWriter) throws IOException
    {
        layoutProgressBarDialog.incrementProgress();
        fileWriter.append("Correlation Value:\t");
        for (int i = 100 - rangeThreshold; i <= 100; i++)
            fileWriter.append( createTextValue(i) + ( (i < 100) ? "\t" : "" ) );
        fileWriter.append("\n");

        layoutProgressBarDialog.incrementProgress();
        fileWriter.append("Number Of Nodes:\t");
        for (int i = 100 - rangeThreshold; i <= 100; i++)
            fileWriter.append(Integer.toString(allNodes[i]) + ( (i < 100) ? "\t" : "" ) );
        fileWriter.append("\n");

        layoutProgressBarDialog.incrementProgress();
        fileWriter.append("Number Of Edges:\t");
        for (int i = 100 - rangeThreshold; i <= 100; i++)
            fileWriter.append(Integer.toString(allEdges[i]) + ( (i < 100) ? "\t" : "" ) );
        fileWriter.append("\n");
    }

    private String createTextValue(int value)
    {
        String text = Utils.numberFormatting(value / 100.0, 2);
        if (text.length() == 3)
            return (text + "0");
        else if (text.length() == 1)
            return (text + DECIMAL_SEPARATOR_STRING + "00");
        else
            return text;
    }

    private class ExportCorrelationNodesEdgesTableProcess implements Runnable
    {

        private File saveFile = null;

        private ExportCorrelationNodesEdgesTableProcess(File saveFile)
        {
            this.saveFile = saveFile;
        }

        @Override
        public void run()
        {
            saveCorrelationNodesEdgesTableFile(saveFile);

            FILE_CHOOSER_PATH.set( saveFile.getAbsolutePath() );
        }


    }


}