package org.BioLayoutExpress3D.Files;

import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.CoreUI.Dialogs.*;
import org.BioLayoutExpress3D.DataStructures.*;
import org.BioLayoutExpress3D.Graph.GraphElements.*;
import org.BioLayoutExpress3D.Network.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.Expression.ExpressionEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* User: cggebi
* Date: Aug 23, 2002
* Time: 1:24:58 PM
*
* @author Full refactoring by Thanos Theo, 2008-2009-2010-2011-2012
* @version 3.0.0.0
*
*/

public final class CoreSaver
{
    private NetworkContainer nc = null;
    private LayoutFrame layoutFrame = null;

    private JFileChooser fileChooser = null;
    private AbstractAction saveAction = null;
    private AbstractAction saveSelectedAction = null;
    private AbstractAction saveVisibleAction = null;

    private Collection<GraphNode> allNodesToSave = null;
    private Collection<GraphEdge> allEdgesToSave = null;
    private Collection<GraphEdge> allCollapsedEdgesToSave = null;
    private int totalLines = 0;

    private FileNameExtensionFilter fileNameExtensionFilterLayout = null;
    private FileNameExtensionFilter fileNameExtensionFilterTGF = null;
    private FileNameExtensionFilter fileNameExtensionFilterCollapsedLayout = null;
    private FileNameExtensionFilter fileNameExtensionFilterCollapsedTGF = null;
    private FileNameExtensionFilter fileNameExtensionFilterCollapsedClusterNodesTGF = null;
    private boolean isCollapsed = false;

    public CoreSaver(NetworkContainer nc, LayoutFrame layoutFrame)
    {
        this.nc = nc;
        this.layoutFrame = layoutFrame;

        initComponents();
    }

    private void initComponents()
    {
        fileNameExtensionFilterLayout = new FileNameExtensionFilter( "Save as a Layout File", SupportedOutputFileTypes.LAYOUT.toString().toLowerCase() );
        fileNameExtensionFilterTGF = new FileNameExtensionFilter( "Save as a TGF File", SupportedOutputFileTypes.TGF.toString().toLowerCase() );
        fileNameExtensionFilterCollapsedLayout = new FileNameExtensionFilter( "Save as a Collapsed Layout File (Remove Redundancies)", SupportedOutputFileTypes.LAYOUT.toString().toLowerCase() );
        fileNameExtensionFilterCollapsedTGF = new FileNameExtensionFilter( "Save as a Collapsed TGF File (Remove Redudancies)", SupportedOutputFileTypes.TGF.toString().toLowerCase() );
        fileNameExtensionFilterCollapsedClusterNodesTGF = new FileNameExtensionFilter( "Save as a Collapsed TGF File (With Collapsed Cluster Nodes & Remove Redundancies)", SupportedOutputFileTypes.TGF.toString().toLowerCase() );

        String saveFilePath = FILE_CHOOSER_PATH.get().substring(0, FILE_CHOOSER_PATH.get().lastIndexOf( System.getProperty("file.separator") ) + 1);
        fileChooser = new JFileChooser(saveFilePath);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        saveAction = new AbstractAction("Save Graph As...")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555716L;

            @Override
            public void actionPerformed(ActionEvent action)
            {
                isCollapsed = layoutFrame.getGraph().getSelectionManager().getGroupManager().isCollapsedMode();

                setFileChooser("Save Graph As");

                allNodesToSave = layoutFrame.getGraph().getGraphNodes();
                allEdgesToSave = layoutFrame.getGraph().getGraphEdges();
                totalLines = allNodesToSave.size() + allEdgesToSave.size();

                save(true);
            }
        };
        saveAction.setEnabled(false);

        saveSelectedAction = new AbstractAction("Save Graph Selection As...")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555717L;

            @Override
            public void actionPerformed(ActionEvent action)
            {
                isCollapsed = layoutFrame.getGraph().getSelectionManager().getGroupManager().isCollapsedMode();

                setFileChooser("Save Selected Graph As");

                allNodesToSave = layoutFrame.getGraph().getSelectionManager().getSelectedNodes();
                totalLines = allNodesToSave.size();

                // has to be done this way, the 'layoutFrame.getGraph().getSelectedEdges()' data struct is empty!
                allEdgesToSave = new HashSet<GraphEdge>();
                for ( GraphEdge graphEdge : layoutFrame.getGraph().getVisibleEdges() )
                {
                    if ( layoutFrame.getGraph().getSelectionManager().getSelectedNodes().contains( graphEdge.getNodeFirst() ) && layoutFrame.getGraph().getSelectionManager().getSelectedNodes().contains( graphEdge.getNodeSecond() ) )
                    {
                        allEdgesToSave.add(graphEdge);
                        totalLines++;
                    }
                }

                save(false);
            }
        };
        saveSelectedAction.setEnabled(false);

        saveVisibleAction = new AbstractAction("Save Visible Graph As...")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555718L;

            @Override
            public void actionPerformed(ActionEvent action)
            {
                isCollapsed = layoutFrame.getGraph().getSelectionManager().getGroupManager().isCollapsedMode();

                setFileChooser("Save Visible Graph As");

                allNodesToSave = layoutFrame.getGraph().getVisibleNodes();
                allEdgesToSave = layoutFrame.getGraph().getVisibleEdges();
                totalLines = allNodesToSave.size() + allEdgesToSave.size();

                save(false);
            }
        };
        saveVisibleAction.setEnabled(false);
    }

    private void setFileChooser(String fileChooserTitle)
    {
        fileChooser.setDialogTitle(fileChooserTitle);
        fileChooser.setSelectedFile( new File( IOUtils.getPrefix( layoutFrame.getFileNameLoaded() ) ) );
        removeAllFileFilters();
        addAllFileFilters();
    }

    private void removeAllFileFilters()
    {
        fileChooser.removeChoosableFileFilter(fileNameExtensionFilterTGF);
        fileChooser.removeChoosableFileFilter(fileNameExtensionFilterLayout);
        fileChooser.removeChoosableFileFilter(fileNameExtensionFilterCollapsedTGF);
        fileChooser.removeChoosableFileFilter(fileNameExtensionFilterCollapsedClusterNodesTGF);
        fileChooser.removeChoosableFileFilter(fileNameExtensionFilterCollapsedLayout);
    }

    private void addAllFileFilters()
    {
        // last filter appears as default in save list
        fileChooser.setFileFilter(fileNameExtensionFilterTGF);
        fileChooser.setFileFilter(fileNameExtensionFilterLayout);

        if (isCollapsed)
        {
            fileChooser.setFileFilter(fileNameExtensionFilterCollapsedTGF);
            fileChooser.setFileFilter(fileNameExtensionFilterCollapsedClusterNodesTGF);
            fileChooser.setFileFilter(fileNameExtensionFilterCollapsedLayout);
        }
    }

    public AbstractAction getSaveAction()
    {
        return saveAction;
    }

    public AbstractAction getSaveSelectedAction()
    {
        return saveSelectedAction;
    }

    public AbstractAction getSaveVisibleAction()
    {
        return saveVisibleAction;
    }

    private void save(boolean saveAllGraph)
    {
        int dialogReturnValue = 0;
        boolean saveLayout = false;
        boolean saveTGF = false;
        boolean doSaveFile = false;
        File saveFile = null;

        if (fileChooser.showSaveDialog(layoutFrame) == JFileChooser.APPROVE_OPTION)
        {
            String fileExtension = "";

            if ( fileChooser.getFileFilter().equals(fileNameExtensionFilterLayout) || fileChooser.getFileFilter().equals(fileNameExtensionFilterCollapsedLayout)  )
            {
                fileExtension = fileNameExtensionFilterLayout.getExtensions()[0];
                saveLayout = true;
            }
            else if ( fileChooser.getFileFilter().equals(fileNameExtensionFilterTGF) || fileChooser.getFileFilter().equals(fileNameExtensionFilterCollapsedTGF) || fileChooser.getFileFilter().equals(fileNameExtensionFilterCollapsedClusterNodesTGF) )
            {
                fileExtension = fileNameExtensionFilterTGF.getExtensions()[0];
                saveTGF = true;

                if ( fileChooser.getFileFilter().equals(fileNameExtensionFilterCollapsedClusterNodesTGF) )
                    saveWithCollapsedClusterNodes(saveAllGraph);
            }
            else // default file extension will be the layout file format
            {
                fileExtension = fileNameExtensionFilterLayout.getExtensions()[0];
                saveLayout = true;
            }

            String fileName = fileChooser.getSelectedFile().getAbsolutePath();
            fileName = IOUtils.removeMultipleExtensions(fileName, fileExtension);
            saveFile = new File(fileName + "." + fileExtension);

            if ( saveFile.exists() )
            {
                // do we want to overwrite
                dialogReturnValue = JOptionPane.showConfirmDialog(layoutFrame, "This File Already Exists.\nDo you want to Overwrite it?", "This File Already Exists. Overwrite?", JOptionPane.YES_NO_CANCEL_OPTION);
                if (dialogReturnValue == JOptionPane.YES_OPTION)
                    doSaveFile = true;
            }
            else
            {
                doSaveFile = true;
            }

            String saveFilePath = saveFile.getAbsolutePath().substring(0, saveFile.getAbsolutePath().lastIndexOf( System.getProperty("file.separator") ) + 1);
            if ( DATA_TYPE.equals(DataTypes.EXPRESSION) && !saveFilePath.equals(EXPRESSION_FILE_PATH) )
            {
                dialogReturnValue = JOptionPane.showConfirmDialog(layoutFrame, "You have chosen to save your expression data derived layout file to a different drive/folder than its parent expression file.\nIt is advised to save the layout file in the same drive/folder as its parent expression file. Please press ok to continue.", "Layout folder/drive saving advice", JOptionPane.YES_NO_OPTION);
                doSaveFile = (dialogReturnValue == JOptionPane.YES_OPTION);
            }
        }

        if (doSaveFile)
        {
            // saving process on its own thread, to effectively decouple it from the main GUI thread
            Thread runLightWeightThread = new Thread( new CoreSaverProcess(saveLayout, saveTGF, saveFile, saveAllGraph) );
            runLightWeightThread.setPriority(Thread.NORM_PRIORITY);
            runLightWeightThread.start();
        }
    }

    private void saveWithCollapsedClusterNodes(boolean saveAllGraph)
    {
        allCollapsedEdgesToSave = new HashSet<GraphEdge>();
        GraphGroupNode graphGroupNode = null;
        Collection<GraphNode> allNodesInGroups = layoutFrame.getGraph().getSelectionManager().getGroupManager().getAllNodesInGroups();
        for (GraphNode graphNode : allNodesInGroups)
        {
            graphGroupNode = layoutFrame.getGraph().getSelectionManager().getGroupManager().extractGroupFromNode(graphNode);
            if ( saveAllGraph || allNodesToSave.contains(graphGroupNode) )
                allCollapsedEdgesToSave.add( new GraphEdge(graphNode, graphGroupNode, new Edge(graphNode.getVertex(), graphGroupNode.getVertex(), 1.0f), 1.0f) );
        }
    }

    private void saveLayoutFile(File saveFile, boolean saveAllGraph)
    {
        LayoutClassSetsManager layoutClassSetsManager = layoutFrame.getLayoutClassSetsManager();
        LayoutProgressBarDialog layoutProgressBarDialog = layoutFrame.getLayoutProgressBar();
        FileWriter fileWriter = null;

        try
        {
            layoutProgressBarDialog.startProgressBar();
            layoutProgressBarDialog.prepareProgressBar(totalLines, (isCollapsed) ? "Now Saving Collapsed Layout File..." : "Now Saving Layout File...");

            fileWriter = new FileWriter(saveFile);
            fileWriter.write("//" + VERSION + " " + " Layout File\n");
            if ( DATA_TYPE.equals(DataTypes.EXPRESSION) )
            {
                String saveFilePath = saveFile.getAbsolutePath().substring(0, saveFile.getAbsolutePath().lastIndexOf( System.getProperty("file.separator") ) + 1);
                fileWriter.write("//EXPRESSION_DATA_V2\t\"" +
                        ( !saveFilePath.equals(EXPRESSION_FILE_PATH) ? EXPRESSION_FILE_PATH : "" ) +
                        EXPRESSION_FILE + "\"\t" +
                        EXPRESSION_DATA_FIRST_COLUMN + "\t" +
                        EXPRESSION_DATA_FIRST_ROW + "\t" +
                        EXPRESSION_DATA_TRANSPOSE + "\t" +
                        Float.toString(CURRENT_CORRELATION_THRESHOLD) + "\n");
            }

            savePairWiseData(layoutProgressBarDialog, fileWriter);
            saveAllNodesAndEdgesData(layoutProgressBarDialog, layoutClassSetsManager, fileWriter);
            if ( nc.getIsGraphml() )
                saveGraphmlData(fileWriter);

            fileWriter.write("//DEFAULTSEARCH\t" + ( (CUSTOM_SEARCH) ? SEARCH_URL.getUrl(): SEARCH_URL.getName() ) + "\n");
            fileWriter.flush();
        }
        catch (IOException ioe)
        {
            if (DEBUG_BUILD) println("IOException in CoreSaver.saveLayoutFile():\n" + ioe.getMessage());

            layoutProgressBarDialog.endProgressBar();
            layoutProgressBarDialog.stopProgressBar();
            JOptionPane.showMessageDialog(layoutFrame, "Something went wrong while saving the file:\n" + ioe.getMessage() + "\nPlease try again with a different file name/path/drive.", "Error with saving the file!", JOptionPane.ERROR_MESSAGE);
            save(saveAllGraph);
        }
        finally
        {
            try
            {
                if (fileWriter != null) fileWriter.close();
            }
            catch (IOException ioe)
            {
                if (DEBUG_BUILD) println("IOException while closing streams in CoreSaver.saveLayoutFile():\n" + ioe.getMessage());
            }
            finally
            {
                layoutProgressBarDialog.endProgressBar();
                layoutProgressBarDialog.stopProgressBar();
            }
        }
    }

    private void saveTGFFile(File saveFile, boolean saveAllGraph)
    {
        LayoutProgressBarDialog layoutProgressBarDialog = layoutFrame.getLayoutProgressBar();
        FileWriter fileWriter = null;

        try
        {
            layoutProgressBarDialog.startProgressBar();
            layoutProgressBarDialog.prepareProgressBar(totalLines, (isCollapsed) ? "Now Saving Collapsed TGF File..." : "Now Saving TGF File...");

            fileWriter = new FileWriter(saveFile);

            savePairWiseData(layoutProgressBarDialog, fileWriter);

            fileWriter.flush();
        }
        catch (IOException ioe)
        {
            if (DEBUG_BUILD) println("IOException in CoreSaver.saveTGFFile():\n" + ioe.getMessage());

            layoutProgressBarDialog.endProgressBar();
            layoutProgressBarDialog.stopProgressBar();
            JOptionPane.showMessageDialog(layoutFrame, "Something went wrong while saving the file:\n" + ioe.getMessage() + "\nPlease try again with a different file name/path/drive.", "Error with saving the file!", JOptionPane.ERROR_MESSAGE);
            save(saveAllGraph);
        }
        finally
        {
            try
            {
                if (fileWriter != null) fileWriter.close();
            }
            catch (IOException ioe)
            {
                if (DEBUG_BUILD) println("IOException while closing streams in CoreSaver.saveTGFFile():\n" + ioe.getMessage());
            }
            finally
            {
                layoutProgressBarDialog.endProgressBar();
                layoutProgressBarDialog.stopProgressBar();
            }
        }
    }

    private void savePairWiseData(LayoutProgressBarDialog layoutProgressBarDialog, FileWriter fileWriter) throws IOException
    {
        Edge edge = null;
        // get rid of redundant multiple lines with the same edge pair names when in collapsed mode so as to avoid redundant edges using a HashSet
        if (isCollapsed)
        {
            ArrayList<String> edgeDescription = null;
            int numberOfDescriptions = (WEIGHTED_EDGES) ? 3 : 2;

            // here are removed normal redundant lines by usage of the HashSet's add() method that only adds distinct lines
            HashSet<ArrayList<String>> allEdgeDescriptions = new HashSet<ArrayList<String>>();
            for (GraphEdge graphEdge : allEdgesToSave)
            {
                edge = graphEdge.getEdge();

                edgeDescription = new ArrayList<String>(numberOfDescriptions);
                edgeDescription.add( edge.getFirstVertex().getVertexName() );
                edgeDescription.add( edge.getSecondVertex().getVertexName() );
                if (numberOfDescriptions == 3)
                    edgeDescription.add( Float.toString( edge.getWeight() ) );

                allEdgeDescriptions.add(edgeDescription);
            }

            if (allCollapsedEdgesToSave != null)
            {
                for (GraphEdge graphEdge : allCollapsedEdgesToSave)
                {
                    edge = graphEdge.getEdge();

                    edgeDescription = new ArrayList<String>(numberOfDescriptions);
                    edgeDescription.add( edge.getFirstVertex().getVertexName() );
                    edgeDescription.add( edge.getSecondVertex().getVertexName() );
                    if (numberOfDescriptions == 3)
                        edgeDescription.add( Float.toString( edge.getWeight() ) );

                    allEdgeDescriptions.add(edgeDescription);
                }

                allCollapsedEdgesToSave = null;
            }

            // here are checked all reversed redundant lines and stored in a separate HashSet so as to avoid a ConcurrentModification exception if removed directly
            HashSet<ArrayList<String>> allEdgeDescriptionsToRemove = new HashSet<ArrayList<String>>();
            for (ArrayList<String> currentEdgeDescription : allEdgeDescriptions)
            {
                edgeDescription = new ArrayList<String>(numberOfDescriptions);
                edgeDescription.add( currentEdgeDescription.get(1) );
                edgeDescription.add( currentEdgeDescription.get(0) );
                if (numberOfDescriptions == 3)
                    edgeDescription.add( currentEdgeDescription.get(2) );

                // Only adds one pair of original & switched redundant pairs through the first !contains() check
                if ( !allEdgeDescriptionsToRemove.contains(currentEdgeDescription) && allEdgeDescriptions.contains(edgeDescription) )
                    allEdgeDescriptionsToRemove.add(edgeDescription);
            }

            // here are removed all reversed redundant lines
            allEdgeDescriptions.removeAll(allEdgeDescriptionsToRemove);

            for (ArrayList<String> currentEdgeDescription : allEdgeDescriptions)
            {
                layoutProgressBarDialog.incrementProgress();

                if (numberOfDescriptions == 3)
                    fileWriter.write("\"" + currentEdgeDescription.get(0) + "\"\t" + "\"" + currentEdgeDescription.get(1) + "\"\t" + currentEdgeDescription.get(2) + "\n");
                else
                    fileWriter.write("\"" + currentEdgeDescription.get(0) + "\"\t" + "\"" + currentEdgeDescription.get(1) + "\"" + "\n");
            }
        }
        else // normal uncollapsed mode
        {
            String nodeType = "";
            for (GraphEdge graphEdge : allEdgesToSave)
            {
                layoutProgressBarDialog.incrementProgress();
                edge = graphEdge.getEdge();

                if (WEIGHTED_EDGES)
                {
                    if ( nc.getIsPetriNet() )
                    {
                        nodeType = "";
                        if ( (edge.getEdgeName() != null) && !edge.getEdgeName().isEmpty() )
                           nodeType += "SPN_EDGE_VALUE:" + edge.getEdgeName();
                        if ( edge.isTotalInhibitorEdge() )
                            nodeType += " SPN_IS_TOTAL_INHIBITOR_EDGE";
                        if ( edge.isPartialInhibitorEdge() )
                            nodeType += " SPN_IS_PARTIAL_INHIBITOR_EDGE";
                        if ( edge.hasDualArrowHead() )
                            nodeType += " SPN_HAS_DUAL_ARROWHEAD";
                        nodeType = nodeType.trim();
                    }

                    fileWriter.write("\"" + edge.getFirstVertex().getVertexName() + "\"\t" + "\"" + edge.getSecondVertex().getVertexName() + "\"\t" + Float.toString( edge.getWeight() ) +
                                     ( ( !nodeType.isEmpty() ) ? "\t\"" + nodeType + "\"" : "" ) + "\n");
                }
                else
                    fileWriter.write("\"" + edge.getFirstVertex().getVertexName() + "\"\t" + "\"" + edge.getSecondVertex().getVertexName() + "\"" + "\n");
            }
        }
    }

    private void saveAllNodesAndEdgesData(LayoutProgressBarDialog layoutProgressBarDialog, LayoutClassSetsManager layoutClassSetsManager, FileWriter fileWriter) throws IOException
    {
        String[] nodeDescription = null;
        VertexClass vertexClass = null;
        for (GraphNode graphNode : allNodesToSave)
        {
            layoutProgressBarDialog.incrementProgress();

            fileWriter.write("//NODECOORD\t\"" + graphNode.getVertex().getVertexName() + "\"\t" + Float.toString( graphNode.getPoint().getX() ) + "\t" + Float.toString( graphNode.getPoint().getY() ) + "\t" + Float.toString( graphNode.getPoint().getZ() ) + "\n");

            nodeDescription = graphNode.getNodeDescription();
            for (int i = 0; i < nodeDescription.length; i++)
                if ( !nodeDescription[i].isEmpty() )
                    fileWriter.write("//NODEDESC\t\"" + graphNode.getVertex().getVertexName() + "\"\t\"" + nodeDescription[i] + "\"\n");

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

            fileWriter.write("//NODESIZE\t\"" + graphNode.getNodeName() + "\"\t" + graphNode.getNodeSize() + "\n");
            // fileWriter.write("//NODECOLOR\t\"" + graphNode.getNodeName() + "\"\t" + colorToString( graphNode.getColor() ) + "\n");
            fileWriter.write("//NODESHAPE\t\"" + graphNode.getNodeName() + "\"\t" + graphNode.getNode2DShape() + "\t" + graphNode.getNode3DShape() + "\n");
            fileWriter.write("//NODEALPHA\t\"" + graphNode.getNodeName() + "\"\t" + graphNode.getTransparencyAlpha() + "\n");
            if ( !graphNode.getURLString().isEmpty() )
                fileWriter.write("//NODEURL\t\"" + graphNode.getNodeName() + "\"\t" + graphNode.getURLString() + "\n");
            if ( nc.getIsPetriNet() )
                fileWriter.write("//NODETYPE\t\"" + graphNode.getNodeName() + "\"" + ( ( graphNode.ismEPNComponent() ) ? "\tIS_MEPN_COMPONENT" : ( ( graphNode.ismEPNTransition() ) ? "\tIS_MEPN_TRANSITION" : "\tIS_MEPN_TYPE" ) ) + "\n");
        }

        fileWriter.write("//CURRENTCLASSSET\t\"" + layoutClassSetsManager.getCurrentClassSetName() + "\"\n");
        fileWriter.write("//EDGESIZE\t" + Float.toString( DEFAULT_EDGE_SIZE.get() ) + "\n");
        fileWriter.write("//EDGECOLOR\t\"" + Utils.getHexColor( DEFAULT_EDGE_COLOR.get() ) + "\"\n");
        fileWriter.write("//EDGEARROWHEADSIZE\t\"" + ARROW_HEAD_SIZE.get() + "\"\n");

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

    private void saveGraphmlData(FileWriter fileWriter) throws IOException
    {
        GraphmlNetworkContainer gnc = nc.getGraphmlNetworkContainer();
        fileWriter.write("//HAS_GRAPHML_NODE_DATA\t\"" + Float.toString( gnc.getRangeX() ) + "\"\t" + "\"" + Float.toString( gnc.getRangeY() ) + "\"" + ( ( nc.getIsPetriNet() ) ? "\tIS_SPN_MEPN_GRAPHML_GRAPH_TYPE" : "" ) + "\n");

        // graphml-style node key pattern of type *.nXXX (the 'n' character followed by as-many-as-needed arithmetic characters
        Pattern graphmlPattern = Pattern.compile(".n{1}+\\d+");
        Matcher graphmlMatcher = null;
        ArrayList<GraphNode> sortedAllGraphmlNodesToSave = new ArrayList<GraphNode>(allNodesToSave);
        for (GraphNode graphNode : allNodesToSave)
        {
            graphmlMatcher = graphmlPattern.matcher( graphNode.getNodeName() );
            if ( graphmlMatcher.matches() ) // sortedAllGraphmlNodesToSave ArrayList contains only graphml-style nodes
                sortedAllGraphmlNodesToSave.add(graphNode);
        }

        // sort first to make node key-to-names associations more clear in the saved file
        // use the Comparable<GraphNode> feature of the GraphNode class, see it's overriden compareTo() method for implementation details
        Collections.sort(sortedAllGraphmlNodesToSave);
        float[] currentNodeGraphmlMapCoord = null;
        for (GraphNode graphNode : sortedAllGraphmlNodesToSave) //replace any 'newline' & '"' characters for compatibility reasons
        {
            currentNodeGraphmlMapCoord = gnc.getAllGraphmlNodesMap().get( graphNode.getNodeName() ).first;
            fileWriter.write("//GRAPHML_NODE_DATA\t\"" + graphNode.getNodeName() + "\"\t\"" + nc.getNodeName( graphNode.getNodeName() ).replace("\n", "").replace("\"", " ").trim() + "\"\t" + Float.toString(currentNodeGraphmlMapCoord[2]) + "\t" + Float.toString(currentNodeGraphmlMapCoord[3]) + "\t" + Float.toString(currentNodeGraphmlMapCoord[4]) + "\n");
        }

        Tuple6<String, Tuple2<float[], ArrayList<Point2D.Float>>, String[], String[], String[], String[]> edgeTuple6 = null;
        for ( String edgeKey : gnc.getAllGraphmlEdgesMap().keySet() )
        {
            edgeTuple6 = gnc.getAllGraphmlEdgesMap().get(edgeKey);
            fileWriter.write("//GRAPHML_EDGE_DATA\t\"" + edgeKey + "\"\t\"" + edgeTuple6.first + "\"");
            for (Point2D.Float polylinePoint2D : edgeTuple6.second.second)
                fileWriter.write("\t" + polylinePoint2D.x + "\t" + polylinePoint2D.y);
            fileWriter.write("\n");
        }

        for ( GraphmlComponentContainer pathwayComponentContainer : gnc.getAllPathwayComponentContainersFor3D() )
        {
            fileWriter.write("//GRAPHML_COMPONENT_CONTAINER_DATA\t\"" + pathwayComponentContainer.name + "\"\t" + pathwayComponentContainer.depth + "\t" + pathwayComponentContainer.alpha + "\t"
                                                                      + pathwayComponentContainer.rectangle2D.x + "\t" + pathwayComponentContainer.rectangle2D.y + "\t" + pathwayComponentContainer.rectangle2D.width + "\t" + pathwayComponentContainer.rectangle2D.height + "\t\""
                                                                      + Utils.getHexColor(pathwayComponentContainer.color) + "\"\n");
        }
    }

    private class CoreSaverProcess implements Runnable
    {

        private boolean saveLayout = false;
        private boolean saveTGF = false;
        private File saveFile = null;
        private boolean saveAllGraph = false;

        private CoreSaverProcess(boolean saveLayout, boolean saveTGF, File saveFile, boolean saveAllGraph)
        {
            this.saveLayout = saveLayout;
            this.saveTGF = saveTGF;
            this.saveFile = saveFile;
            this.saveAllGraph = saveAllGraph;
        }

        @Override
        public void run()
        {
            if (saveLayout)
                saveLayoutFile(saveFile, saveAllGraph);
            else if (saveTGF)
                saveTGFFile(saveFile, saveAllGraph);

            FILE_CHOOSER_PATH.set( saveFile.getAbsolutePath() );
        }


    }


}