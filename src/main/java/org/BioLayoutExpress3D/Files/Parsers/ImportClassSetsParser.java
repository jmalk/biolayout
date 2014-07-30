package org.BioLayoutExpress3D.Files.Parsers;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.CoreUI.Dialogs.*;
import org.BioLayoutExpress3D.DataStructures.*;
import org.BioLayoutExpress3D.Files.*;
import org.BioLayoutExpress3D.Files.Dialogs.*;
import org.BioLayoutExpress3D.Network.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.Expression.ExpressionEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* The ImportClassSetsParser class is used for importing Class Sets functionality.
*
* @author Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public final class ImportClassSetsParser extends CoreParser implements ImportClassSetsDialog.ImportClassSetsDialogListener
{
    private AbstractAction importClassSetsAction = null;

    private BioLayoutExpress3DFileFilter bioLayoutExpress3DFileFilter = null;
    private LayoutProgressBarDialog layoutProgressBarDialog = null;
    private ImportClassSetsDialog importClassSetsDialog = null;
    private boolean cancelParse = false;

    private HashMap<String, HashMap<String, HashSet<String>>> allClassSetsVertexData = null;
    private HashMap<String, HashMap<String, Color>> allClassSetsColorData = null;

    /**
    *  The constructor of the ImportClassSetsParser class.
    */
    public ImportClassSetsParser(NetworkContainer nc, LayoutFrame layoutFrame)
    {
        super(nc, layoutFrame);

        layoutProgressBarDialog = layoutFrame.getLayoutProgressBar();

        initComponents();
    }

    /**
    *  This method is called from within the constructor to initialize the dialog.
    */
    private void initComponents()
    {
        bioLayoutExpress3DFileFilter = new BioLayoutExpress3DFileFilter( SupportedImportExportFileTypes.values() );

        importClassSetsDialog = new ImportClassSetsDialog(layoutFrame);
        importClassSetsDialog.setListener(this);

        importClassSetsAction = new AbstractAction("Class Sets...")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555716L;

            @Override
            public void actionPerformed(ActionEvent action)
            {
                importClassSets();
            }
        };
        importClassSetsAction.setEnabled(false);
    }

    /**
    *  Gets the Import Class Sets Action.
    */
    public AbstractAction getImportClassSetsAction()
    {
        return importClassSetsAction;
    }

    /**
    *  Imports the Class Sets.
    */
    private void importClassSets()
    {
        String importClassSetsFilePath = FILE_CHOOSER_PATH.get().substring(0, FILE_CHOOSER_PATH.get().lastIndexOf( System.getProperty("file.separator") ) + 1);
        JFileChooser fileChooser = new JFileChooser(importClassSetsFilePath);
        fileChooser.setDialogTitle("Import Class Sets File");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setFileFilter(bioLayoutExpress3DFileFilter);

        if (fileChooser.showOpenDialog(layoutFrame) == JFileChooser.APPROVE_OPTION)
        {
            file = fileChooser.getSelectedFile();
            FILE_CHOOSER_PATH.set( file.getAbsolutePath() );
            loadImportFile(file);
        }
    }

    /**
    *  Loads a given import file.
    */
    private void loadImportFile(File file)
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
                    initializeAllDataStructures();
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
                    openImportClassSetsDialogWindowsProcess(threadPriority);
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
    *  Opens the Import Class Sets Dialog Window.
    */
    private void openImportClassSetsDialogWindowsProcess(int threadPriority)
    {
        Thread runLightWeightThread = new Thread( new Runnable()
        {

            @Override
            public void run()
            {
                if (!cancelParse)
                    importClassSetsDialog.openDialogWindows( allClassSetsVertexData.keySet() );
            }


        } );

        runLightWeightThread.setPriority(threadPriority);
        runLightWeightThread.start();
    }

    /**
    *  Loads a given import file.
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

            layoutProgressBarDialog.prepareProgressBar(totalLines, "Parsing " + file.getName() + " Imported Class Sets...");
            layoutProgressBarDialog.startProgressBar();

            if ( !IOUtils.getPrefix( layoutFrame.getFileNameLoaded() ).equals( IOUtils.getPrefix( file.getName() ) ) )
                importClassSetsDialog.doClickDelimiter1RadioButton();
            else
                importClassSetsDialog.doClickMatchFullNameAndCase();

            while ( ( line = fileReaderBuffered.readLine() ) != null && !cancelParse )
            {
                layoutProgressBarDialog.incrementProgress(++counter);

                tokenize(line);
                if (line.length() > 0)
                    if ( line.startsWith("//") )
                        updateVertexClassProperties();
            }

            isSuccessful = true;
        }
        catch (IOException ioe)
        {
            if (DEBUG_BUILD) println("IOException in ImportClassSetsParser.parse():\n" + ioe.getMessage());
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
                if (DEBUG_BUILD) println("IOException while closing streams in ImportClassSetsParser.parse():\n" + ioe.getMessage());
            }
            finally
            {
                layoutProgressBarDialog.endProgressBar();
                layoutProgressBarDialog.stopProgressBar();
            }
        }

        return isSuccessful;
    }

    /**
    *  Updates the Vertex Class Properties.
    */
    private void updateVertexClassProperties()
    {
        String property = getNext();
        String vertex = "";
        String field1 = "", field2 = "", field3 = "";

        if ( property.equals("//EXPRESSION_DATA") )
        {
            field1 = getNext();

            if ( !EXPRESSION_FILE.equals(field1) )
            {
                cancelParse = !showConfirmationDialogForDifferentExpressionFile();
                importClassSetsDialog.doClickDelimiter1RadioButton();
            }
            else
                importClassSetsDialog.doClickMatchFullNameAndCase();
        }
        else if ( property.equals("//NODECLASS") )
        {
            vertex = getNext();
            field1 = getNext();
            field2 = getNext();

            HashMap<String, HashSet<String>> allClassesVertexData = null;
            if (field2.length() > 0)
            {
                // IF CLASS SET PROVIDED ADD THE VERTEX TO THE CLASS SET
                if ( !allClassSetsVertexData.containsKey(field2) )
                    allClassSetsVertexData.put( field2, new HashMap<String, HashSet<String>>() );
                allClassesVertexData = allClassSetsVertexData.get(field2);
            }
            /*
            else
            {
                // IF NO CLASS SET IS PROVIDED ADD TO THE DEFAULT CLASSES LayoutClasses.NO_CLASS
                if ( !allClassSetsVertexData.containsKey(LayoutClasses.NO_CLASS) )
                    allClassSetsVertexData.put( LayoutClasses.NO_CLASS, new HashMap<String, HashSet<String>>() );
                allClassesVertexData = allClassSetsVertexData.get(LayoutClasses.NO_CLASS);
            }
            */

            if ( !allClassesVertexData.containsKey(field1) )
                allClassesVertexData.put( field1, new HashSet<String>() );
            HashSet<String> allVertexData = allClassesVertexData.get(field1);
            allVertexData.add(vertex);
        }
        else if ( property.equals("//NODECLASSCOLOR") )
        {
            field1 = getNext();
            field2 = getNext();
            field3 = getNext();

            HashMap<String, Color> allClassesColorData = null;
            if (field3.length() > 0)
            {
                // IF CLASS SET PROVIDED SET THE COLOR TO THE CLASS SET
                if ( !allClassSetsColorData.containsKey(field2) )
                    allClassSetsColorData.put( field2, new HashMap<String, Color>() );
                allClassesColorData = allClassSetsColorData.get(field2);
                allClassesColorData.put( field1, Color.decode(field3) );
            }
            /*
            else
            {
                // IF NO CLASS SET IS PROVIDED ADD TO THE DEFAULT CLASSES ID 0
                if ( !allClassSetsColorData.containsKey(LayoutClasses.NO_CLASS) )
                    allClassSetsColorData.put( LayoutClasses.NO_CLASS, new HashMap<String, Color>() );
                allClassesColorData = allClassSetsColorData.get(LayoutClasses.NO_CLASS);
                allClassesColorData.put( LayoutClasses.NO_CLASS, Color.decode(field2) );
            }
            */
        }
    }

    /**
    *  Show the confirmation dialog for a different expression file.
    */
    private boolean showConfirmationDialogForDifferentExpressionFile()
    {
        int dialogReturnValue = JOptionPane.showConfirmDialog(layoutFrame, "Please be aware that this Class Sets import file is derived from a different file than the currently loaded one.\nContinue with the Class Sets import process?", "Do you want to continue?", JOptionPane.YES_NO_OPTION);
        return (dialogReturnValue == JOptionPane.YES_OPTION);
    }

    /**
    *  Initializes all data structures.
    */
    private void initializeAllDataStructures()
    {
        allClassSetsVertexData = new HashMap<String, HashMap<String, HashSet<String>>>();
        allClassSetsColorData = new HashMap<String, HashMap<String, Color>>();
    }

    /**
    *  Parses the data from data structures.
    */
    private void parseFromDataStructures(HashSet<String> selectedClassSets, String selectedDelimiter, boolean selectedMatchFullName, boolean selectedMatchCase, boolean selectedMatchEntireName)
    {
        if ( !selectedClassSets.isEmpty() )
        {
            Set<Tuple2<String, Vertex>> allCachedNames = preCacheAllNames(!selectedDelimiter.isEmpty() && !selectedMatchFullName, !selectedMatchCase);

            LayoutClasses lc = null;
            VertexClass vc = null;
            Set<Vertex> vertexSet = new HashSet<Vertex>();
            for ( String classSetKey : allClassSetsVertexData.keySet() )
            {
                if ( selectedClassSets.contains(classSetKey) )
                {
                    layoutProgressBarDialog.incrementProgress();
                    for ( String classKey : allClassSetsVertexData.get(classSetKey).keySet() )
                    {
                        for ( String vertexName : allClassSetsVertexData.get(classSetKey).get(classKey) )
                        {
                            vertexSet.clear(); // pre-clear here so as to use the isEmpty() for checks in the performSearchBasedOnTheNodeIdentifierParsingOptions() method
                            vertexSet = performSearchBasedOnTheNodeIdentifierParsingOptions(vertexSet, allCachedNames, vertexName, selectedDelimiter, selectedMatchFullName, selectedMatchCase, selectedMatchEntireName);
                            if ( !vertexSet.isEmpty() )
                            {
                                for (Vertex vertex : vertexSet)
                                {
                                    lc = nc.getLayoutClassSetsManager().getClassSet(classSetKey);
                                    vc = lc.createClass(classKey);
                                    lc.setClass(vertex, vc);

                                    if (allClassSetsColorData.containsKey(classSetKey))
                                    {
                                        // in this loop so as to not add redundant class sets / classes
                                        vc.setColor(allClassSetsColorData.get(classSetKey).get(classKey));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
    *  Pre-caches all names.
    */
    private Set<Tuple2<String, Vertex>> preCacheAllNames(boolean createDelimiterDataStructure, boolean createLowerCaseDataStructure)
    {
        Set<Tuple2<String, Vertex>> allCachedNames = new HashSet<Tuple2<String, Vertex>>();

        // pre-cache all delimiter names with their original vertex names and associated vertex object for speed efficiency in internal loops
        if (createDelimiterDataStructure)
        {
            // search only for first part of delimited name
            if (createLowerCaseDataStructure)
            {
                for ( String vertexMapName : nc.getVerticesMap().keySet() )
                    allCachedNames.add( Tuples.tuple( nc.getNodeName(vertexMapName).toLowerCase(), nc.getVerticesMap().get(vertexMapName) ) );
            }
            else
            {
                for ( String vertexMapName : nc.getVerticesMap().keySet() )
                    allCachedNames.add( Tuples.tuple( nc.getNodeName(vertexMapName), nc.getVerticesMap().get(vertexMapName) ) );
            }

            return allCachedNames;
        }
        // pre-cache all names in lower case for speed efficiency in internal loops
        else
        {
            if (createLowerCaseDataStructure)
            {
                for ( String vertexMapName : nc.getVerticesMap().keySet() )
                    allCachedNames.add( Tuples.tuple( nc.getNodeName(vertexMapName).toLowerCase(), nc.getVerticesMap().get(vertexMapName) ) );
            }
            else
            {
                for ( String vertexMapName : nc.getVerticesMap().keySet() )
                    allCachedNames.add( Tuples.tuple( nc.getNodeName(vertexMapName), nc.getVerticesMap().get(vertexMapName) ) );
            }

            return allCachedNames;
        }
    }

    /**
    *  Performs search based on the node identifier parsing options.
    */
    private Set<Vertex> performSearchBasedOnTheNodeIdentifierParsingOptions(Set<Vertex> vertexSet, Set<Tuple2<String, Vertex>> allCachedNames, String vertexName, String selectedDelimiter, boolean selectedMatchFullName, boolean selectedMatchCase, boolean selectedMatchEntireName)
    {
        if (!selectedDelimiter.isEmpty() && !selectedMatchFullName)
        {
            String[] splitDelimiterNames = vertexName.split(selectedDelimiter);
            // search only for first part of delimited name, cache the lower cache name
            if (!selectedMatchCase)
            {
                String splitDelimiterNameLowerCase = splitDelimiterNames[0].toLowerCase();
                if (!selectedMatchEntireName)
                {
                    for (Tuple2<String, Vertex> tuple2 : allCachedNames)
                        if ( tuple2.first.contains(splitDelimiterNameLowerCase) )
                            vertexSet.add(tuple2.second);
                }
                else
                {
                    for (Tuple2<String, Vertex> tuple2 : allCachedNames)
                        if ( tuple2.first.equals(splitDelimiterNameLowerCase) )
                            vertexSet.add(tuple2.second);
                }
            }
            // search only for first part of delimited name
            else
            {
                if (!selectedMatchEntireName)
                {
                    for (Tuple2<String, Vertex> tuple2 : allCachedNames)
                        if ( tuple2.first.contains(splitDelimiterNames[0]) )
                            vertexSet.add(tuple2.second);
                }
                else
                {
                    for (Tuple2<String, Vertex> tuple2 : allCachedNames)
                        if ( tuple2.first.equals(splitDelimiterNames[0]) )
                            vertexSet.add(tuple2.second);
                }
            }
        }
        else
        {
            // Only one perfect match to be found, cache the lower cache name
            if (!selectedMatchCase)
            {
                String vertexNameLowerCase = vertexName.toLowerCase();
                if (!selectedMatchEntireName)
                {
                    for (Tuple2<String, Vertex> tuple2 : allCachedNames)
                    {
                        if ( tuple2.first.contains(vertexNameLowerCase) )
                        {
                            vertexSet.add(tuple2.second);
                            break;
                        }
                    }
                }
                else
                {
                    for (Tuple2<String, Vertex> tuple2 : allCachedNames)
                    {
                        if ( tuple2.first.equals(vertexNameLowerCase) )
                        {
                            vertexSet.add(tuple2.second);
                            break;
                        }
                    }
                }
            }
            // only one perfect match to be found
            else
            {
                if (!selectedMatchCase)
                {
                    for (Tuple2<String, Vertex> tuple2 : allCachedNames)
                    {
                        if ( tuple2.first.contains(vertexName) )
                        {
                            vertexSet.add(tuple2.second);
                            break;
                        }
                    }
                }
                else
                {
                    for (Tuple2<String, Vertex> tuple2 : allCachedNames)
                    {
                        if ( tuple2.first.equals(vertexName) )
                        {
                            vertexSet.add(tuple2.second);
                            break;
                        }
                    }
                }
            }
        }

        return vertexSet;
    }

    /**
    *  Clears all data structures.
    */
    private void clearAllDataStructures()
    {
        allClassSetsVertexData = null;
        allClassSetsColorData = null;

        System.gc();
    }

    /**
    *  Gets all selected class sets.
    */
    @Override
    public void getSelectedClassSets(final HashSet<String> selectedClassSets, final String selectedDelimiter, final boolean selectedMatchFullName, final boolean selectedMatchCase, final boolean selectedMatchEntireName)
    {
        Thread runLightWeightThread = new Thread( new Runnable()
        {

            @Override
            public void run()
            {
                if (DEBUG_BUILD) println("\nselectedClassSets size: " + selectedClassSets.size() + "\nselectedDelimiter: " + selectedDelimiter + " string length: " + selectedDelimiter.length() + "\nselectedMatchFullName: " + selectedMatchFullName + "\nselectedMatchCase: " + selectedMatchCase);

                layoutProgressBarDialog.prepareProgressBar(selectedClassSets.size(), "Parsing Imported Class Sets According To Selection...");
                layoutProgressBarDialog.startProgressBar();

                parseFromDataStructures(selectedClassSets, selectedDelimiter, selectedMatchFullName, selectedMatchCase, selectedMatchEntireName);
                clearAllDataStructures();

                layoutProgressBarDialog.endProgressBar();
                layoutProgressBarDialog.stopProgressBar();
            }


        } );

        runLightWeightThread.setPriority(Thread.NORM_PRIORITY);
        runLightWeightThread.start();
    }


}