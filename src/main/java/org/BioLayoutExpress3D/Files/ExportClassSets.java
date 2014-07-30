package org.BioLayoutExpress3D.Files;

import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.CoreUI.Dialogs.*;
import org.BioLayoutExpress3D.Graph.GraphElements.*;
import org.BioLayoutExpress3D.Network.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.Expression.ExpressionEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* The ExportClassSets class is used for exporting Class Sets As File functionality.
*
* @author Thanos Theo, 2008-2009
* @version 3.0.0.0
*
*/

public final class ExportClassSets
{
    private LayoutFrame layoutFrame = null;

    private JFileChooser fileChooser = null;
    private AbstractAction exportClassSetsFromGraphAction = null;
    private AbstractAction exportClassSetsFromGraphSelectionAction = null;
    private AbstractAction exportClassSetsFromVisibleGraphAction = null;

    private Collection<GraphNode> allNodesToSave = null;
    private int totalLines = 0;

    private FileNameExtensionFilter fileNameExtensionFilterClassSets = null;

    public ExportClassSets(LayoutFrame layoutFrame)
    {
        this.layoutFrame = layoutFrame;

        initComponents();
    }

    private void initComponents()
    {
        fileNameExtensionFilterClassSets = new FileNameExtensionFilter( "Save as a ClassSets File", SupportedImportExportFileTypes.CLASSSETS.toString().toLowerCase() );

        String saveFilePath = FILE_CHOOSER_PATH.get().substring(0, FILE_CHOOSER_PATH.get().lastIndexOf( System.getProperty("file.separator") ) + 1);
        fileChooser = new JFileChooser(saveFilePath);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(fileNameExtensionFilterClassSets);

        exportClassSetsFromGraphAction = new AbstractAction("From Graph...")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555715L;

            @Override
            public void actionPerformed(ActionEvent action)
            {
                setFileChooser("Save Class Sets As File From Graph");

                allNodesToSave = layoutFrame.getGraph().getGraphNodes();
                totalLines = allNodesToSave.size();

                save();
            }
        };
        exportClassSetsFromGraphAction.setEnabled(false);

        exportClassSetsFromGraphSelectionAction = new AbstractAction("From Graph Selection...")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555715L;

            @Override
            public void actionPerformed(ActionEvent action)
            {
                setFileChooser("Save Class Sets As File From Graph Selection");

                allNodesToSave = layoutFrame.getGraph().getSelectionManager().getSelectedNodes();
                totalLines = allNodesToSave.size();

                save();
            }
        };
        exportClassSetsFromGraphSelectionAction.setEnabled(false);

        exportClassSetsFromVisibleGraphAction = new AbstractAction("From Visible Graph...")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555715L;

            @Override
            public void actionPerformed(ActionEvent action)
            {
                setFileChooser("Save Class Sets As File From Visible Graph");

                allNodesToSave = layoutFrame.getGraph().getVisibleNodes();
                totalLines = allNodesToSave.size();

                save();
            }
        };
        exportClassSetsFromVisibleGraphAction.setEnabled(false);
    }

    private void setFileChooser(String fileChooserTitle)
    {
        fileChooser.setDialogTitle(fileChooserTitle);
        fileChooser.setSelectedFile( new File( IOUtils.getPrefix( layoutFrame.getFileNameLoaded() ) ) );
    }

    public AbstractAction getExportClassSetsFromGraphAction()
    {
        return exportClassSetsFromGraphAction;
    }

    public AbstractAction getExportClassSetsFromGraphSelectionAction()
    {
        return exportClassSetsFromGraphSelectionAction;
    }

    public AbstractAction getExportClassSetsFromVisibleGraphAction()
    {
        return exportClassSetsFromVisibleGraphAction;
    }

    private void save()
    {
        int dialogReturnValue = 0;
        boolean doSaveFile = false;
        File saveFile = null;

        if (fileChooser.showSaveDialog(layoutFrame) == JFileChooser.APPROVE_OPTION)
        {
            String fileExtension = "";

            if ( fileChooser.getFileFilter().equals(fileNameExtensionFilterClassSets) )
            {
                fileExtension = fileNameExtensionFilterClassSets.getExtensions()[0];
            }
            else // default file extension will be the classsets file format
            {
                fileExtension = fileNameExtensionFilterClassSets.getExtensions()[0];
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
            Thread runLightWeightThread = new Thread( new ExportClassSetsProcess(saveFile) );
            runLightWeightThread.setPriority(Thread.NORM_PRIORITY);
            runLightWeightThread.start();
        }
    }

    private void saveClassSetsFile(File saveFile)
    {
        LayoutClassSetsManager layoutClassSetsManager = layoutFrame.getLayoutClassSetsManager();
        LayoutProgressBarDialog layoutProgressBarDialog = layoutFrame.getLayoutProgressBar();
        FileWriter fileWriter = null;

        try
        {
            layoutProgressBarDialog.prepareProgressBar(totalLines, "Now Saving ClassSets File...");
            layoutProgressBarDialog.startProgressBar();

            fileWriter = new FileWriter(saveFile);
            fileWriter.write("//" + VERSION + " " + " Class Sets File\n");
            if ( DATA_TYPE.equals(DataTypes.EXPRESSION) )
                fileWriter.write("//EXPRESSION_DATA\t\"" + EXPRESSION_FILE + "\"\n");

           saveClassSetsData(layoutProgressBarDialog, layoutClassSetsManager, fileWriter);

            fileWriter.flush();
        }
        catch (IOException ioe)
        {
            if (DEBUG_BUILD) println("IOException in ExportClassSets.saveClassSetsFile():\n" + ioe.getMessage());

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
                if (DEBUG_BUILD) println("IOException while closing streams in ExportClassSets.saveClassSetsFile():\n" + ioe.getMessage());
            }
            finally
            {
                layoutProgressBarDialog.endProgressBar();
                layoutProgressBarDialog.stopProgressBar();
            }
        }
    }

    private void saveClassSetsData(LayoutProgressBarDialog layoutProgressBarDialog, LayoutClassSetsManager layoutClassSetsManager, FileWriter fileWriter) throws IOException
    {
        VertexClass vertexClass = null;
        for (GraphNode graphNode : allNodesToSave)
        {
            layoutProgressBarDialog.incrementProgress();

            for ( LayoutClasses layoutClassSet : layoutClassSetsManager.getClassSetNames() )
            {
                vertexClass = layoutClassSet.getVertexClass( graphNode.getVertex() );
                if (vertexClass != null)
                {
                    if (vertexClass.getClassID() != 0)
                    {
                        if (layoutClassSet.getClassSetID() != 0)
                            fileWriter.write("//NODECLASS\t\"" + graphNode.getNodeName() + "\"\t\"" + vertexClass.getName() + "\"\t\"" + layoutClassSet.getClassSetName() + "\"\n");
                        else
                            fileWriter.write("//NODECLASS\t\"" + graphNode.getNodeName() + "\"\t\"" + vertexClass.getName() + "\"\n");
                    }
                }
            }
        }

        for ( LayoutClasses layoutClassSet : layoutClassSetsManager.getClassSetNames() )
        {
            for ( VertexClass currentVertexClass : layoutClassSet.getAllVertexClasses() )
            {
                if (layoutClassSet.getClassSetID() != 0)
                    fileWriter.write("//NODECLASSCOLOR\t\"" + currentVertexClass.getName() + "\"\t\"" + layoutClassSet.getClassSetName() + "\"\t\"" + Utils.getHexColor( currentVertexClass.getColor() ) + "\"\n");
                else
                    fileWriter.write("//NODECLASSCOLOR\t\"" + currentVertexClass.getName() + "\"\t\"" + Utils.getHexColor( currentVertexClass.getColor() ) + "\"\n");
            }
        }
    }

    private class ExportClassSetsProcess implements Runnable
    {

        private File saveFile = null;

        private ExportClassSetsProcess(File saveFile)
        {
            this.saveFile = saveFile;
        }

        @Override
        public void run()
        {
            saveClassSetsFile(saveFile);

            FILE_CHOOSER_PATH.set( saveFile.getAbsolutePath() );
        }


    }


}