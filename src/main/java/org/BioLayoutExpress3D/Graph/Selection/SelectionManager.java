package org.BioLayoutExpress3D.Graph.Selection;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.CoreUI.Dialogs.*;
import org.BioLayoutExpress3D.Graph.*;
import org.BioLayoutExpress3D.Graph.GraphElements.*;
import org.BioLayoutExpress3D.Network.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* @author Leon Goldovsky, full refactoring by Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*
*/

public final class SelectionManager
{
    private GraphUndoDelete graphUndoDelete = null;
    private GroupManager groupManager = null;
    private CompleteGroup completeGroup = null;

    private LayoutFrame layoutFrame = null;
    private Graph graph = null;
    private HashSet<GraphNode> selectedNodes = null;
    private HashSet<GraphEdge> selectedEdges = null;

    private AbstractAction deleteSelectionAction = null;
    private AbstractAction deleteHiddenAction = null;
    private AbstractAction deleteUnselectedAction = null;
    private AbstractAction undoLastDeleteAction = null;
    private AbstractAction undeleteAllNodesAction = null;
    private AbstractAction completeGroupingAction = null;
    private AbstractAction selectAllAction = null;
    private AbstractAction selectNeighbourAction = null;
    private AbstractAction selectAllNeighbourAction = null;
    private AbstractAction selectParentsAction = null;
    private AbstractAction selectAllParentsAction = null;
    private AbstractAction selectChildrenAction = null;
    private AbstractAction selectAllChildrenAction = null;
    private AbstractAction selectClassAction = null;
    private AbstractAction reverseSelectionAction = null;
    private AbstractAction deselectAllAction = null;
    private AbstractAction hideUnselectedAction = null;
    private AbstractAction hideSelectionAction = null;
    private AbstractAction unhideAllAction = null;
    private AbstractAction showAllNodeNamesAction = null;
    private AbstractAction showSelectedNodeNamesAction = null;
    private AbstractAction showAllEdgeNamesAction = null;
    private AbstractAction showSelectedNodesEdgeNamesAction = null;
    private AbstractAction hideAllNodeNamesAction = null;
    private AbstractAction hideSelectedNodeNamesAction = null;
    private AbstractAction hideAllEdgeNamesAction = null;
    private AbstractAction hideSelectedNodesEdgeNamesAction = null;

    public SelectionManager(LayoutFrame layoutFrame, Graph graph)
    {
        this.layoutFrame = layoutFrame;
        this.graph = graph;

        selectedNodes = new HashSet<GraphNode>();
        selectedEdges = new HashSet<GraphEdge>();

        groupManager = new GroupManager(this, graph);
        completeGroup = new CompleteGroup( layoutFrame, this, layoutFrame.getLayoutProgressBar() );
        graphUndoDelete = new GraphUndoDelete(this);

        createActions(layoutFrame);
        setActionsEnable(false);
    }

    public void clearAllSelection()
    {
        selectedNodes.clear();
        selectedEdges.clear();

        setActionsEnable(false);
    }

    public void clearGraphUndoDelete()
    {
        graphUndoDelete.clear();
    }

    private void setActionsEnable(boolean value)
    {
        selectNeighbourAction.setEnabled(value);
        selectAllNeighbourAction.setEnabled(value);
        selectParentsAction.setEnabled(value);
        selectAllParentsAction.setEnabled(value);
        selectChildrenAction.setEnabled(value);
        selectAllChildrenAction.setEnabled(value);
        selectClassAction.setEnabled(value);
        reverseSelectionAction.setEnabled(value);
        deleteSelectionAction.setEnabled( value && !groupManager.isCollapsedMode() );
        deleteUnselectedAction.setEnabled( value && !groupManager.isCollapsedMode() );
        groupManager.getGroupAction().setEnabled( value && !layoutFrame.getNetworkRootContainer().getIsGraphml() );
        hideSelectionAction.setEnabled(value);
        showSelectedNodeNamesAction.setEnabled(value);
        showSelectedNodesEdgeNamesAction.setEnabled(value);
        hideSelectedNodeNamesAction.setEnabled(value);
        hideSelectedNodesEdgeNamesAction.setEnabled(value);
        deselectAllAction.setEnabled(value);

        if (layoutFrame.getGraph() != null)
        {
            layoutFrame.getGraph().setEnabledUndoNodeDragging(value);
            layoutFrame.getGraph().setEnabledRedoNodeDragging(value);
            layoutFrame.getCoreSaver().getSaveSelectedAction().setEnabled( !selectedNodes.isEmpty() );
            layoutFrame.getExportClassSets().getExportClassSetsFromGraphSelectionAction().setEnabled(value);
            layoutFrame.getLayoutAnimationControlDialog().setEnabledSelectedNodesCheckbox(value);
        }
    }

    private void createActions(final LayoutFrame layoutFrame)
    {
        selectAllAction = new AbstractAction("Select All")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555755L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                selectAll();
            }
        };
        selectAllAction.setEnabled(false);

        selectNeighbourAction = new AbstractAction("Select Neighbours")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555756L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                selectNeighbours(selectedNodes);
            }
        };
        selectNeighbourAction.setEnabled(false);

        selectAllNeighbourAction = new AbstractAction("Select All Neighbours")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555757L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                selectAllNeighbours(selectedNodes);
            }
        };
        selectAllNeighbourAction.setEnabled(false);

        selectParentsAction = new AbstractAction("Select Parents")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555758L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                selectParents(selectedNodes);
            }
        };
        selectParentsAction.setEnabled(false);

        selectAllParentsAction = new AbstractAction("Select All Parents")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555759L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                selectAllParents(selectedNodes);
            }
        };
        selectAllParentsAction.setEnabled(false);

        selectChildrenAction = new AbstractAction("Select Children")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555760L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                selectChildren(selectedNodes);
            }
        };
        selectChildrenAction.setEnabled(false);

        selectAllChildrenAction = new AbstractAction("Select All Children")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555761L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                selectAllChildren(selectedNodes);
            }
        };
        selectAllChildrenAction.setEnabled(false);

        selectClassAction = new AbstractAction("Select Nodes Within The Same Class")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555763L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                selectNodesWithinTheSameClass();
            }
        };
        selectClassAction.setEnabled(false);

        reverseSelectionAction = new AbstractAction("Reverse Selection")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555764L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                reverseSelection();
            }
        };
        reverseSelectionAction.setEnabled(false);

        deselectAllAction = new AbstractAction("Deselect All")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555755L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                deselectAll();
            }
        };
        deselectAllAction.setEnabled(false);

        deleteSelectionAction = new AbstractAction("Delete Selection")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555766L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                Thread runLightWeightThread = new Thread( new Runnable()
                {

                    @Override
                    public void run()
                    {
                        deleteSelection();
                    }
                } );

                runLightWeightThread.setPriority(Thread.NORM_PRIORITY);
                runLightWeightThread.start();
            }
        };
        deleteSelectionAction.setEnabled(false);

        deleteHiddenAction = new AbstractAction("Delete Hidden")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555766L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                Thread runLightWeightThread = new Thread( new Runnable()
                {

                    @Override
                    public void run()
                    {
                        deleteHidden();
                    }
                } );

                runLightWeightThread.setPriority(Thread.NORM_PRIORITY);
                runLightWeightThread.start();
            }
        };
        deleteHiddenAction.setEnabled(false);

        deleteUnselectedAction = new AbstractAction("Delete Unselected")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555765L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                Thread runLightWeightThread = new Thread( new Runnable()
                {

                    @Override
                    public void run()
                    {
                        reverseSelection();
                        deleteSelection();
                    }


                } );

                runLightWeightThread.setPriority(Thread.NORM_PRIORITY);
                runLightWeightThread.start();
            }
        };
        deleteUnselectedAction.setEnabled(false);

        undoLastDeleteAction = new AbstractAction("Undo Last Delete")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555765L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                Thread runLightWeightThread = new Thread( new Runnable()
                {

                    @Override
                    public void run()
                    {
                        undoLastDelete();
                    }


                } );

                runLightWeightThread.setPriority(Thread.NORM_PRIORITY);
                runLightWeightThread.start();
            }
        };
        undoLastDeleteAction.setEnabled(false);

        undeleteAllNodesAction = new AbstractAction("Undelete All Nodes")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555765L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                Thread runLightWeightThread = new Thread( new Runnable()
                {

                    @Override
                    public void run()
                    {
                        undeleteAllNodes();
                    }


                } );

                runLightWeightThread.setPriority(Thread.NORM_PRIORITY);
                runLightWeightThread.start();
            }
        };
        undeleteAllNodesAction.setEnabled(false);

        hideUnselectedAction = new AbstractAction("Hide Unselected")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555767L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                reverseSelection();
                hideSelected();
            }
        };
        hideUnselectedAction.setEnabled(false);

        hideSelectionAction = new AbstractAction("Hide Selection")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555768L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                hideSelected();
            }
        };
        hideSelectionAction.setEnabled(false);

        unhideAllAction = new AbstractAction("Unhide All Nodes And Edges")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555769L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                unhideAll();
            }
        };
        unhideAllAction.setEnabled(false);

        showAllNodeNamesAction = new AbstractAction("Show All Node Names")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555770L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                for ( GraphNode graphNode : graph.getVisibleNodes() )
                    graphNode.setShowNodeName(true);

                graph.updateNodesDisplayList();
            }
        };
        showAllNodeNamesAction.setEnabled(false);

        showSelectedNodeNamesAction = new AbstractAction("Show Selected Node Names")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555771L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                for (GraphNode graphNode : selectedNodes)
                    graphNode.setShowNodeName(true);

                graph.updateNodesDisplayList();
            }
        };
        showSelectedNodeNamesAction.setEnabled(false);

        showAllEdgeNamesAction = new AbstractAction("Show All Edge Names")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555770L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                boolean doProcess = true;
                if (graph.getVisibleEdges().size() > MAX_EDGES_TO_RENDER_NAMES)
                    doProcess = (JOptionPane.showConfirmDialog(layoutFrame, "There Are Lots Of Visible Edges." + "\nAre You Sure You Want To Show Their Names?", "Too ManyVisible Edges", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION);

                if (doProcess)
                    for ( GraphEdge graphEdge : graph.getVisibleEdges() )
                        graphEdge.setShowEdgeName(true);

                graph.updateEdgesDisplayList();
            }
        };
        showAllEdgeNamesAction.setEnabled(false);

        showSelectedNodesEdgeNamesAction = new AbstractAction("Show Selected Nodes Edge Names")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555770L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                // don't use selectionManager.getSelectedEdges() as that introduces problems!
                HashSet<GraphEdge> selectedEdges = new HashSet<GraphEdge>();
                for ( GraphEdge graphEdge : graph.getVisibleEdges() )
                    if ( selectedNodes.contains( graphEdge.getNodeFirst() ) && selectedNodes.contains( graphEdge.getNodeSecond() ) )
                        selectedEdges.add(graphEdge);

                boolean doProcess = true;
                if (selectedEdges.size() > MAX_EDGES_TO_RENDER_NAMES)
                    doProcess = (JOptionPane.showConfirmDialog(layoutFrame, "There Are Lots Of Selected Visible Edges." + "\nAre You Sure You Want To Show Their Names?", "Too ManyVisible Edges", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION);

                if (doProcess)
                    for (GraphEdge graphEdge : selectedEdges)
                        graphEdge.setShowEdgeName(true);

                graph.updateEdgesDisplayList();
            }
        };
        showSelectedNodesEdgeNamesAction.setEnabled(false);

        hideAllNodeNamesAction = new AbstractAction("Hide All Node Names")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555772L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                for ( GraphNode graphNode : graph.getGraphNodes() )
                    graphNode.setShowNodeName(false);

                graph.updateNodesDisplayList();
            }
        };
        hideAllNodeNamesAction.setEnabled(false);

        hideSelectedNodeNamesAction = new AbstractAction("Hide Selected Node Names")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555773L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                for (GraphNode graphNode : selectedNodes)
                    graphNode.setShowNodeName(false);

                graph.updateNodesDisplayList();
            }
        };
        hideSelectedNodeNamesAction.setEnabled(false);

        hideAllEdgeNamesAction = new AbstractAction("Hide All Edge Names")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555772L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                for ( GraphEdge graphEdge : graph.getGraphEdges() )
                    graphEdge.setShowEdgeName(false);

                graph.updateEdgesDisplayList();
            }
        };
        hideAllEdgeNamesAction.setEnabled(false);

        hideSelectedNodesEdgeNamesAction = new AbstractAction("Hide Selected Nodes Edge Names")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555772L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                for ( GraphEdge graphEdge : graph.getVisibleEdges() )
                    if ( selectedNodes.contains( graphEdge.getNodeFirst() ) && selectedNodes.contains( graphEdge.getNodeSecond() ) )
                        graphEdge.setShowEdgeName(false);

                graph.updateEdgesDisplayList();
            }
        };
        hideSelectedNodesEdgeNamesAction.setEnabled(false);

        completeGroupingAction = new AbstractAction("Perform Complete Grouping")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555774L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                Thread completeGroupThread = new Thread(completeGroup);
                completeGroupThread.setPriority(Thread.NORM_PRIORITY);
                completeGroupThread.start();
            }
        };
        completeGroupingAction.setEnabled(false);
    }

    private void selectNodesWithinTheSameClass()
    {
        HashSet<VertexClass> selectedClasses = new HashSet<VertexClass>();
        HashSet<GraphNode> foundNodes = new HashSet<GraphNode>();

        for (GraphNode graphNode : selectedNodes)
            selectedClasses.add( graphNode.getVertexClass() );

        for ( GraphNode graphNode : graph.getGraphNodes() )
            if ( selectedClasses.contains( graphNode.getVertexClass() ) )
                foundNodes.add(graphNode);

        boolean include = false;
        if ( !graph.getVisibleNodes().containsAll(foundNodes) )
            include = showConfirmationDialogSomeHidden();

        addNodesToSelectedAndNotSelectHiddenNodes(foundNodes, include);
        graph.updateSelectedNodesDisplayList();
    }

    private void deleteNodes(HashSet<GraphNode> nodes, String progressMessage)
    {
        LayoutProgressBarDialog layoutProgressBarDialog = layoutFrame.getLayoutProgressBar();
        layoutProgressBarDialog.prepareProgressBar(selectedNodes.size(), progressMessage);
        layoutProgressBarDialog.startProgressBar();

        HashSet<Vertex> undoVertices = new HashSet<Vertex>();
        HashSet<Edge> undoEdges = new HashSet<Edge>();
        for (GraphNode graphNode : nodes)
        {
            layoutProgressBarDialog.incrementProgress();

            undoVertices.add( graphNode.getVertex()  );
            layoutFrame.getNetworkRootContainer().getVerticesMap().remove( graphNode.getVertex().getVertexName() );

            for ( GraphEdge graphEdge : graphNode.getNodeEdges() )
            {
                undoEdges.add( graphEdge.getEdge() );
                layoutFrame.getNetworkRootContainer().getEdges().remove( graphEdge.getEdge() );
            }
        }

        graphUndoDelete.pushSelected(undoVertices, undoEdges);

        clearAllSelection();
        graph.rebuildGraph();

        updateViewers();
        updateGraphStatistics();
        updateFrameStatusLabel();

        layoutProgressBarDialog.endProgressBar();
        layoutProgressBarDialog.stopProgressBar();
    }

    private void deleteSelection()
    {
        deleteNodes(selectedNodes, "Now Deleting Selection...");
    }

    private void deleteHidden()
    {
        HashSet<GraphNode> hiddenNodes = new HashSet<GraphNode>(graph.getGraphNodes());
        hiddenNodes.removeAll(graph.getVisibleNodes());

        deleteNodes(hiddenNodes, "Now Deleting Hidden Nodes...");

        // Nothing is hidden at this point, but we still need to set the enable/disable state of various menu items
        unhideAll(false);
    }

    private void undoLastDelete()
    {
        LayoutProgressBarDialog layoutProgressBarDialog = layoutFrame.getLayoutProgressBar();
        layoutProgressBarDialog.prepareProgressBar(graphUndoDelete.getTotalSizeOfNodesAndEdgesToRecover(), "Now UnDeleting...");
        layoutProgressBarDialog.startProgressBar();

        graphUndoDelete.undoDelete(layoutFrame.getNetworkRootContainer(), graph, layoutProgressBarDialog);

        updateViewers();
        updateGraphStatistics();
        updateFrameStatusLabel();

        layoutProgressBarDialog.endProgressBar();
        layoutProgressBarDialog.stopProgressBar();
    }

    public void undeleteAllNodes()
    {
        LayoutProgressBarDialog layoutProgressBarDialog = layoutFrame.getLayoutProgressBar();
        layoutProgressBarDialog.prepareProgressBar(graphUndoDelete.howManyStackSteps(), "Now UnDeleting All Steps...");
        layoutProgressBarDialog.startProgressBar();

        while ( layoutFrame.getGraph().getSelectionManager().getUndoLastDeleteAction().isEnabled() )
        {
            layoutProgressBarDialog.incrementProgress();
            graphUndoDelete.undoDelete(layoutFrame.getNetworkRootContainer(), graph, null);
        }

        updateViewers();
        updateGraphStatistics();
        updateFrameStatusLabel();

        layoutProgressBarDialog.endProgressBar();
        layoutProgressBarDialog.stopProgressBar();
    }

    private void hideSelected()
    {
        if (DEBUG_BUILD) println("Re-Building Edges List");

        HashSet<GraphEdge> edges = new HashSet<GraphEdge>();
        for ( GraphEdge graphEdge : graph.getVisibleEdges() )
              if ( !(selectedNodes.contains( graphEdge.getNodeFirst() ) || selectedNodes.contains( graphEdge.getNodeSecond() ) ) )
                  edges.add(graphEdge);

        graph.recreateVisibleEdges(edges);

        if (DEBUG_BUILD) println("Removing nodes from array");

        graph.getVisibleNodes().removeAll(selectedNodes);

        clearAllSelection();

        graph.updateAllDisplayLists();

        unhideAllAction.setEnabled(true);
        deleteHiddenAction.setEnabled(true);

        if ( !layoutFrame.getCoreSaver().getSaveVisibleAction().isEnabled() )
            layoutFrame.getCoreSaver().getSaveVisibleAction().setEnabled(true);

        if ( !layoutFrame.getExportClassSets().getExportClassSetsFromVisibleGraphAction().isEnabled() )
            layoutFrame.getExportClassSets().getExportClassSetsFromVisibleGraphAction().setEnabled(true);

        updateViewers();
        updateGraphStatistics();
        updateFrameStatusLabel();
    }

    private void unhideAll()
    {
        unhideAll(true);
    }

    public void unhideAll(boolean updateViewers)
    {
        graph.recreateVisibleNodes( graph.getGraphNodes() );
        graph.recreateVisibleEdges( graph.getGraphEdges() );
        graph.updateAllDisplayLists();

        unhideAllAction.setEnabled(false);
        deleteHiddenAction.setEnabled(false);
        layoutFrame.getCoreSaver().getSaveVisibleAction().setEnabled(false);
        layoutFrame.getExportClassSets().getExportClassSetsFromVisibleGraphAction().setEnabled(false);

        if (updateViewers) updateViewers();
        updateGraphStatistics();
        updateFrameStatusLabel();
    }

    private void reverseSelection()
    {
        HashSet<GraphNode> tempSetNodes = new HashSet<GraphNode>( graph.getVisibleNodes() );
        tempSetNodes.removeAll(selectedNodes);
        clearAllSelection();
        addNodesToSelected(tempSetNodes, false, true);

        graph.updateAllDisplayLists();
        checkSetEnabledNodeNameTextFieldAndSelectNodesTab();
    }

    public void checkSetEnabledNodeNameTextFieldAndSelectNodesTab()
    {
        if (selectedNodes.size() == 1)
        {
            for (GraphNode node : selectedNodes)
                layoutFrame.setEnabledNodeNameTextFieldAndSelectNodesTab(true, node, 1);
        }
        else
            layoutFrame.setEnabledNodeNameTextFieldAndSelectNodesTab( false, null, selectedNodes.size() );
    }

    private void showInformationDialog(JFrame jFrame, String title, String message)
    {
        JOptionPane pane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION);
        JDialog dialog = pane.createDialog(jFrame, title);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    /**
     * Selects all nodes in the graph.
     */
    public void selectAll()
    {
        clearAllSelection();
        addNodesToSelected(graph.getGraphNodes(), false, true);
        graph.updateSelectedNodesDisplayList();

        layoutFrame.setEnabledNodeNameTextFieldAndSelectNodesTab( false, null, selectedNodes.size() );
    }

    public void deselectAll()
    {
        clearAllSelection();
        addNodesToSelected(new HashSet<GraphNode>(), false, true);
        graph.updateSelectedNodesDisplayList();

        layoutFrame.setEnabledNodeNameTextFieldAndSelectNodesTab(false, null, 0);
    }

    public void addNodeToSelectedUpdateExpressionGraphViewOnly(GraphNode node, boolean includeHidden, boolean notUpdateTitleBar)
    {
        HashSet<GraphNode> nodeSet = new HashSet<GraphNode>();
        nodeSet.add(node);
        addNodesToSelected(nodeSet, includeHidden, true, true, false, true, notUpdateTitleBar);
    }

    public void addNodeToSelectedUpdateExpressionGraphViewOnly(Collection<GraphNode> nodes, boolean includeHidden, boolean notUpdateTitleBar)
    {
        addNodesToSelected(nodes, includeHidden, true, true, false, true, notUpdateTitleBar);
    }

    public boolean addNodesToSelectedAndNotSelectHiddenNodes(Collection<GraphNode> nodes, boolean includeHidden)
    {
        return addNodesToSelected(nodes, includeHidden, !includeHidden, true, true, false, false);
    }

    public boolean addNodesToSelected(Collection<GraphNode> nodes)
    {
        return addNodesToSelected(nodes, false, true, false, true, false, false);
    }

    public boolean addNodesToSelected(Collection<GraphNode> nodes, boolean includeHidden, boolean addEdges)
    {
        return addNodesToSelected(nodes, includeHidden, true, addEdges, true, false, false);
    }

    public boolean addNodesToSelected(Collection<GraphNode> nodes, boolean includeHidden, boolean addSelectedNodes, boolean addEdges, boolean updateViewers, boolean updateExpressionGraphViewOnly, boolean notUpdateTitleBar)
    {
        if (DEBUG_BUILD) println("Add nodes to Selected: " + nodes.size());

        boolean hasAddedNodes = false;
        HashSet<GraphEdge> tempEdges = new HashSet<GraphEdge>();
        if ( !selectedNodes.containsAll(nodes) )
        {
            for (GraphNode graphNode : nodes)
            {
                if (graph.getVisibleNodes().contains(graphNode) || includeHidden)
                {
                    if ( includeHidden && !graph.getVisibleNodes().contains(graphNode) )
                        graph.getVisibleNodes().add(graphNode);

                    if (addSelectedNodes)
                        selectedNodes.add(graphNode);

                    if ( (includeHidden || addEdges) && !(graphNode instanceof GraphGroupNode) ) tempEdges.addAll( graphNode.getNodeEdges() );
                    hasAddedNodes = true;
                }
            }

            if (includeHidden || addEdges)
            {
                if (DEBUG_BUILD) println("Add Edges To Selected");
                addEdgesToSelected(tempEdges, includeHidden);
            }

            setActionsEnable( !selectedNodes.isEmpty() );
        }

        if (updateViewers || updateExpressionGraphViewOnly)
            updateViewers(updateExpressionGraphViewOnly, notUpdateTitleBar);
        updateGraphStatistics();
        updateFrameStatusLabel();

        return hasAddedNodes;
    }

    private void addEdgesToSelected(HashSet<GraphEdge> edges, boolean includeHidden)
    {
        if (includeHidden)
        {
            HashSet<GraphEdge> tempEdgesToAdd = new HashSet<GraphEdge>();
            for (GraphEdge edge : edges)
                if ( (selectedNodes.contains( edge.getNodeFirst() ) && selectedNodes.contains( edge.getNodeSecond() ) ) ) // get rid of edges that do not connect from both sides
                    tempEdgesToAdd.add(edge);

            edges = new HashSet<GraphEdge>(tempEdgesToAdd);
        }

        if ( !selectedEdges.containsAll(edges) )
        {
            selectedEdges.addAll(edges);
            graph.getVisibleEdges().addAll(selectedEdges);
        }
    }

    private HashSet<GraphNode> getNeighbours(HashSet<GraphNode> nodes)
    {
        HashSet<GraphNode> neighbours = new HashSet<GraphNode>();
        for (GraphNode graphNode : nodes)
        {
            for ( GraphEdge graphEdge : graphNode.getNodeEdges() )
            {
                if ( graph.getVisibleEdges().contains(graphEdge) )
                {
                    neighbours.add( graphEdge.getNodeFirst() );
                    neighbours.add( graphEdge.getNodeSecond() );
                }
            }
        }

        if ( nodes.containsAll(neighbours) )
            neighbours.clear();

        return neighbours;
    }

    private HashSet<GraphNode> getChildren(HashSet<GraphNode> nodes)
    {
        HashSet<GraphNode> neighbours = new HashSet<GraphNode>();
        HashSet<GraphEdge> nodeEdges = null;
        for (GraphNode graphNode : nodes)
        {
            nodeEdges = graphNode.getNodeEdges();
            for (GraphEdge graphEdge : nodeEdges)
                if ( graph.getVisibleEdges().contains(graphEdge) )
                    neighbours.add( graphEdge.getNodeSecond() );
        }

        if ( nodes.containsAll(neighbours) )
            neighbours.clear();

        return neighbours;
    }

    private HashSet<GraphNode> getParents(HashSet<GraphNode> nodes)
    {
        HashSet<GraphNode> neighbours = new HashSet<GraphNode>();
        HashSet<GraphEdge> nodeEdges = null;
        for (GraphNode graphNode : nodes)
        {
            nodeEdges = graphNode.getNodeEdges();
            for (GraphEdge graphEdge : nodeEdges)
                if ( graph.getVisibleEdges().contains(graphEdge) )
                    neighbours.add( graphEdge.getNodeFirst() );
        }

        if ( nodes.containsAll(neighbours) )
            neighbours.clear();

        return neighbours;
    }

    private void selectNeighbours(HashSet<GraphNode> nodes)
    {
        HashSet<GraphNode> neighbours = getNeighbours(nodes);
        if ( neighbours.isEmpty() )
        {
            showInformationDialog(layoutFrame, "Select Neighbours", "No more Neighbours found!");
        }
        else
        {
            groupManager.processNodes(neighbours);

            boolean include = false;
            if ( !graph.getVisibleNodes().containsAll(neighbours) )
                include = showConfirmationDialogSomeHidden();

            addNodesToSelectedAndNotSelectHiddenNodes(neighbours, include);
        }

        graph.updateNodesAndSelectedNodesDisplayList();
    }

    private void selectChildren(HashSet<GraphNode> nodes)
    {
        HashSet<GraphNode> neighbours = getChildren(nodes);
        if ( neighbours.isEmpty() )
        {
            showInformationDialog(layoutFrame, "Select Children", "No more Children found!");
        }
        else
        {
            groupManager.processNodes(neighbours);

            boolean include = false;
            if ( !graph.getVisibleNodes().containsAll(neighbours) )
                include = showConfirmationDialogSomeHidden();

            addNodesToSelectedAndNotSelectHiddenNodes(neighbours, include);
        }

        graph.updateNodesAndSelectedNodesDisplayList();
    }

    private void selectParents(HashSet<GraphNode> nodes)
    {
        HashSet<GraphNode> neighbours = getParents(nodes);
        if ( neighbours.isEmpty() )
        {
            showInformationDialog(layoutFrame, "Select Parents", "No more Parents found!");
        }
        else
        {
            groupManager.processNodes(neighbours);

            boolean include = false;
            if ( !graph.getVisibleNodes().containsAll(neighbours) )
                include = showConfirmationDialogSomeHidden();

            addNodesToSelectedAndNotSelectHiddenNodes(neighbours, include);
        }

        graph.updateNodesAndSelectedNodesDisplayList();
    }

    private void selectAllNeighbours(HashSet<GraphNode> nodes)
    {
        HashSet<GraphNode> neighbours = getNeighbours(nodes);
        HashSet<GraphNode> newNeighbours = getNeighbours(neighbours);

        while ( !newNeighbours.isEmpty() )
        {
            neighbours.addAll(newNeighbours);
            newNeighbours = getNeighbours(neighbours);
        }

        boolean include = false;
        if ( !graph.getVisibleNodes().containsAll(neighbours) )
            include = showConfirmationDialogSomeHidden();

        addNodesToSelectedAndNotSelectHiddenNodes(neighbours, include);
        graph.updateNodesAndSelectedNodesDisplayList();
    }

    private void selectAllChildren(HashSet<GraphNode> nodes)
    {
        HashSet<GraphNode> neighbours = getNeighbours(nodes);
        HashSet<GraphNode> newNeighbours = getParents(neighbours);

        while ( !newNeighbours.isEmpty() )
        {
            neighbours.addAll(newNeighbours);
            newNeighbours = getChildren(neighbours);
        }

        boolean include = false;
        if ( !graph.getVisibleNodes().containsAll(neighbours) )
            include = showConfirmationDialogSomeHidden();

        addNodesToSelectedAndNotSelectHiddenNodes(neighbours, include);
        graph.updateNodesAndSelectedNodesDisplayList();
    }

    private void selectAllParents(HashSet<GraphNode> nodes)
    {
        HashSet<GraphNode> neighbours = getParents(nodes);
        HashSet<GraphNode> newNeighbours = getParents(neighbours);

        while ( !newNeighbours.isEmpty() )
        {
            neighbours.addAll(newNeighbours);
            newNeighbours = getParents(neighbours);
        }

        boolean include = false;
        if ( !graph.getVisibleNodes().containsAll(neighbours) )
            include = showConfirmationDialogSomeHidden();

        addNodesToSelectedAndNotSelectHiddenNodes(neighbours, include);
        graph.updateNodesAndSelectedNodesDisplayList();
    }

    public void findName(JFrame jFrame, String givenName, boolean matchCase, boolean matchEntireName, boolean clearSelection)
    {
        HashSet<GraphNode> foundGraphNodes = findNameInCollection(graph.getGraphNodes(), givenName, matchCase, matchEntireName);
        groupManager.processNodes(foundGraphNodes);

        if (foundGraphNodes.size() > 0)
        {
            if (clearSelection)
                clearAllSelection();

            boolean include = false;
            if ( !graph.getVisibleNodes().containsAll(foundGraphNodes) )
                include = showConfirmationDialogSomeHidden();

            addNodesToSelectedAndNotSelectHiddenNodes(foundGraphNodes, include);
            layoutFrame.getClassViewerFrame().synchroniseHighlightWithSelection();
            graph.updateAllDisplayLists();
        }
        else
        {
            setActionsEnable(false);
            showInformationDialog(jFrame, "Name Not Found", "Name '" + givenName + "' not found!");
        }
    }

    private HashSet<GraphNode> findNameInCollection(Collection<GraphNode> graphNodes, String givenName, boolean matchCase, boolean matchEntireName)
    {
        String name = "";
        HashSet<GraphNode> foundGraphNodes = new HashSet<GraphNode>();
        for (GraphNode graphNode : graphNodes)
        {
            name = layoutFrame.getNetworkRootContainer().getNodeName( graphNode.getNodeName() );

            if (!matchCase)
            {
                name = name.toLowerCase();
                givenName = givenName.toLowerCase();
            }

            if (matchEntireName)
            {
                if ( givenName.equals(name) )
                    foundGraphNodes.add(graphNode);
            }
            else
            {
                if ( name.contains(givenName) )
                    foundGraphNodes.add(graphNode);
            }
        }

        return foundGraphNodes;
    }

    public void findClass(JFrame jFrame, VertexClass vertexClass)
    {
        HashSet<GraphNode> foundGraphNodes = new HashSet<GraphNode>();
        for ( GraphNode graphNode : graph.getGraphNodes() )
            if ( !(graphNode instanceof GraphGroupNode) && graphNode.getVertexClass().equals(vertexClass) )
                foundGraphNodes.add(graphNode);

        clearAllSelection();

        boolean include = false;
        if ( !graph.getVisibleNodes().containsAll(foundGraphNodes) )
            include = showConfirmationDialogSomeHidden();

        addNodesToSelected(foundGraphNodes, include, true, true, true, false, true);
        layoutFrame.getClassViewerFrame().synchroniseHighlightWithSelection();
        graph.updateAllDisplayLists();

        layoutFrame.getClassViewerFrame().setCurrentClassName( vertexClass.getName()  );

        if ( foundGraphNodes.isEmpty() )
        {
            setActionsEnable(false);
            showInformationDialog(jFrame, "No Nodes Have Been Assigned To This Class", "Be Aware That No Nodes Have Been Assigned To This Class: " + vertexClass.getName() + "!");
        }
    }

    public void findMultipleClasses(JFrame jFrame, HashSet<VertexClass> vertexClasses)
    {
        HashSet<GraphNode> foundGraphNodes = new HashSet<GraphNode>();
        for ( GraphNode graphNode : graph.getGraphNodes() )
            for (VertexClass vertexClass : vertexClasses)
                if ( !(graphNode instanceof GraphGroupNode) && graphNode.getVertexClass().equals(vertexClass) )
                    foundGraphNodes.add(graphNode);

        if ( !foundGraphNodes.isEmpty() )
        {
            clearAllSelection();

            boolean include = false;
            if ( !graph.getVisibleNodes().containsAll(foundGraphNodes) )
                include = showConfirmationDialogSomeHidden();

            addNodesToSelected(foundGraphNodes, include, true);

            if (vertexClasses.size() == 1)
            {
                String className = "";
                for (VertexClass vertexClass : vertexClasses)
                    className = vertexClass.getName();
                layoutFrame.getClassViewerFrame().setCurrentClassName(className);
            }

            layoutFrame.getClassViewerFrame().synchroniseHighlightWithSelection();
            graph.updateAllDisplayLists();
        }
        else
        {
            // since the GUI of Multiple Classes selection is a table with checkboxes, the suer can not have selected something, thus effectively clearing current selection
            deselectAll();
            setActionsEnable(false);
        }
    }

    private boolean showConfirmationDialogSomeHidden()
    {
        int dialogReturnValue = JOptionPane.showConfirmDialog(layoutFrame, "Some of the Selected Nodes are Hidden.\nDo you want to Unhide and Select them?", "Hidden Nodes", JOptionPane.YES_NO_OPTION);
        return (dialogReturnValue == JOptionPane.YES_OPTION);
    }

    public boolean showConfirmationDialogUnhideAll()
    {
        int dialogReturnValue = JOptionPane.showConfirmDialog(layoutFrame, "This will Unhide All Nodes and Edges.\nDo you want to proceed?", "Unhide All Nodes and Edges", JOptionPane.YES_NO_CANCEL_OPTION);
        return (dialogReturnValue == JOptionPane.YES_OPTION);
    }

    public HashSet<GraphNode> getExpandedSelectedNodes()
    {
        HashSet<GraphNode> expandedSelection = new HashSet<GraphNode>();
        for (GraphNode node : selectedNodes)
        {
            if (node instanceof GraphGroupNode)
            {
                GraphGroupNode groupNode = (GraphGroupNode)node;
                expandedSelection.addAll( groupNode.getGroupNodes() );
            }
            else
            {
                expandedSelection.add(node);
            }
        }

        if (DEBUG_BUILD) println("Got:" + expandedSelection.size() + " Objects");

        return expandedSelection;
    }

    public HashSet<GraphNode> getSelectedNodes()
    {
        return selectedNodes;
    }

    public HashSet<GraphEdge> getSelectedEdges()
    {
        return selectedEdges;
    }

    public AbstractAction getDeleteSelectionAction()
    {
        return deleteSelectionAction;
    }

    public AbstractAction getDeleteHiddenAction()
    {
        return deleteHiddenAction;
    }

    public AbstractAction getDeleteUnselectedAction()
    {
        return deleteUnselectedAction;
    }

    public AbstractAction getUndoLastDeleteAction()
    {
        return undoLastDeleteAction;
    }

    public AbstractAction getUndeleteAllNodesAction()
    {
        return undeleteAllNodesAction;
    }

    public AbstractAction getGroupAction()
    {
        return groupManager.getGroupAction();
    }

    public AbstractAction getClassGroupAction()
    {
        return groupManager.getClassGroupAction();
    }

    public AbstractAction getUnGroupSelectedAction()
    {
        return groupManager.getUnGroupSelectedAction();
    }

    public AbstractAction getUnGroupAllAction()
    {
        return groupManager.getUnGroupAllAction();
    }

    public AbstractAction getCompleteGroupingAction()
    {
        return completeGroupingAction;
    }

    public AbstractAction getSelectAllAction()
    {
        return selectAllAction;
    }

    public AbstractAction getSelectNeighbourAction()
    {
        return selectNeighbourAction;
    }

    public AbstractAction getSelectAllNeighbourAction()
    {
        return selectAllNeighbourAction;
    }

    public AbstractAction getSelectAllParentsAction()
    {
        return selectAllParentsAction;
    }

    public AbstractAction getSelectAllChildrenAction()
    {
        return selectAllChildrenAction;
    }

    public AbstractAction getSelectChildrenAction()
    {
        return selectChildrenAction;
    }

    public AbstractAction getSelectParentsAction()
    {
        return selectParentsAction;
    }

    public AbstractAction getSelectClassAction()
    {
        return selectClassAction;
    }

    public AbstractAction getReverseSelectionAction()
    {
        return reverseSelectionAction;
    }

    public AbstractAction getDeselectAllAction()
    {
        return deselectAllAction;
    }

    public AbstractAction getHideUnselectedAction()
    {
        return hideUnselectedAction;
    }

    public AbstractAction getHideSelectionAction()
    {
        return hideSelectionAction;
    }

    public AbstractAction getUnhideAllAction()
    {
        return unhideAllAction;
    }

    public AbstractAction getShowAllNodeNamesAction()
    {
        return showAllNodeNamesAction;
    }

    public AbstractAction getShowSelectedNodeNamesAction()
    {
        return showSelectedNodeNamesAction;
    }

    public AbstractAction getShowAllEdgeNamesAction()
    {
        return showAllEdgeNamesAction;
    }

    public AbstractAction getShowSelectedNodesEdgeNamesAction()
    {
        return showSelectedNodesEdgeNamesAction;
    }

    public AbstractAction getHideAllNodeNamesAction()
    {
        return hideAllNodeNamesAction;
    }

    public AbstractAction getHideSelectedNodeNamesAction()
    {
        return hideSelectedNodeNamesAction;
    }

    public AbstractAction getHideAllEdgeNamesAction()
    {
        return hideAllEdgeNamesAction;
    }

    public AbstractAction getHideSelectedNodesEdgeNamesAction()
    {
        return hideSelectedNodesEdgeNamesAction;
    }

    public Graph getGraph()
    {
        return graph;
    }

    public GroupManager getGroupManager()
    {
        return groupManager;
    }

    public void setEnabledDeleteActions()
    {
        graphUndoDelete.setEnabledDeleteActions();
    }

    public Collection<GraphNode> getVisibleNodes()
    {
        return graph.getVisibleNodes();
    }

    public LayoutFrame getLayoutFrame()
    {
        return layoutFrame;
    }

    public void removeNodeFromSelected(GraphNode node)
    {
        removeNodeFromSelected(node, true, false, false);
    }

    public void removeNodeFromSelected(GraphNode node, boolean updateViewers, boolean updateExpressionGraphViewOnly, boolean notUpdateTitleBar)
    {
        selectedNodes.remove(node);

        for ( GraphEdge graphEdge : node.getNodeEdges() )
            if ( !selectedNodes.contains( graphEdge.getNodeFirst() ) && !selectedNodes.contains( graphEdge.getNodeSecond() ) )
                selectedEdges.remove(graphEdge);

        setActionsEnable( !selectedNodes.isEmpty() );
        if (updateViewers || updateExpressionGraphViewOnly)
            updateViewers(updateExpressionGraphViewOnly, notUpdateTitleBar);
        updateGraphStatistics();
        updateFrameStatusLabel();
    }

    public void removeNodeFromSelected(Collection<GraphNode> nodes, boolean updateViewers, boolean updateExpressionGraphViewOnly, boolean notUpdateTitleBar)
    {
        selectedNodes.removeAll(nodes);

        for (GraphNode node :nodes)
            for ( GraphEdge graphEdge : node.getNodeEdges() )
                if ( !selectedNodes.contains( graphEdge.getNodeFirst() ) && !selectedNodes.contains( graphEdge.getNodeSecond() ) )
                    selectedEdges.remove(graphEdge);

        setActionsEnable( !selectedNodes.isEmpty() );
        if (updateViewers || updateExpressionGraphViewOnly)
            updateViewers(updateExpressionGraphViewOnly, notUpdateTitleBar);
        updateGraphStatistics();
        updateFrameStatusLabel();
    }

    private void updateViewers()
    {
        updateViewers(false, false);
    }

    private void updateViewers(boolean updateExpressionGraphViewOnly, boolean notUpdateTitleBar)
    {
        if ( layoutFrame.getClassViewerFrame().isVisible() )
            layoutFrame.getClassViewerFrame().populateClassViewer(updateExpressionGraphViewOnly, notUpdateTitleBar);
    }

    private void updateGraphStatistics()
    {
        layoutFrame.updateLayoutGraphStatisticsDialog();
    }

    private void updateFrameStatusLabel()
    {
        int nodes = selectedNodes.size();
        if (nodes > 0)
        {
            String message = Integer.toString(nodes) + ( (nodes == 1) ? " Node Selected" : " Nodes Selected" );
            layoutFrame.setStatusLabel(message);
        }
        else
            layoutFrame.setStatusLabel("Ready");
    }

    public void selectByClass(VertexClass vertexClass)
    {
        selectByClass(vertexClass, true, true, true);
    }

    public void selectByClass(VertexClass vertexClass, boolean updateViewers, boolean updateSelectedNodesDisplayList, boolean notUpdateTitleBar)
    {
        clearAllSelection();

        HashSet<GraphNode> foundGraphNodes = new HashSet<GraphNode>();
        VertexClass graphNodeVertexClass = null;
        for ( GraphNode graphNode : graph.getGraphNodes() )
        {
            graphNodeVertexClass = graphNode.getVertexClass();
            if (graphNodeVertexClass != null)
                if ( graphNodeVertexClass.equals(vertexClass) )
                    foundGraphNodes.add(graphNode);
        }

        addNodesToSelected(foundGraphNodes, false, true, true, updateViewers, false, notUpdateTitleBar);

        if (foundGraphNodes.size() > GroupManager.NUMBER_OF_NODES_TO_UPDATE_SELECTED_NODES_DISPLAY_LIST_WHILE_COLLAPSING || updateSelectedNodesDisplayList)
        {
            if (DEBUG_BUILD) println("Found size > " + GroupManager.NUMBER_OF_NODES_TO_UPDATE_SELECTED_NODES_DISPLAY_LIST_WHILE_COLLAPSING + " : " + foundGraphNodes.size() + " Now repainting");
            graph.updateSelectedNodesDisplayList();
        }
    }


}