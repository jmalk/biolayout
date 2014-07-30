package org.BioLayoutExpress3D.Graph.Selection;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import static java.lang.Math.*;
import org.BioLayoutExpress3D.CoreUI.Dialogs.*;
import org.BioLayoutExpress3D.Graph.*;
import org.BioLayoutExpress3D.Graph.GraphElements.*;
import org.BioLayoutExpress3D.Network.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* @author Leon Goldovsky, full refactoring by Thanos Theo, 2008-2009
* @version 3.0.0.0
*
*/

public final class GroupManager
{
    public static final int NUMBER_OF_NODES_TO_UPDATE_SELECTED_NODES_DISPLAY_LIST_WHILE_COLLAPSING = 10;

    private Graph graph = null;
    private SelectionManager selectionManager = null;
    private LayoutProgressBarDialog layoutProgressBarDialog = null;

    private HashMap<Integer, GraphGroupNode> groupIDs = null;
    private HashMap<GraphNode, GraphGroupNode> allGroupNodesMap = null;

    private String groupNodeName = "";
    private String groupDisplayName = "";

    private AbstractAction groupAction = null;
    private AbstractAction classGroupAction = null;
    private AbstractAction unGroupSelectedAction = null;
    private AbstractAction unGroupAllAction = null;

    public GroupManager(SelectionManager selectionManager, Graph graph)
    {
        this.selectionManager = selectionManager;
        this.graph = graph;

        groupIDs = new HashMap<Integer, GraphGroupNode>();
        allGroupNodesMap = new HashMap<GraphNode, GraphGroupNode>();
        layoutProgressBarDialog = selectionManager.getLayoutFrame().getLayoutProgressBar();

        groupNodeName = GROUP_NAME_REG;
        groupDisplayName = GROUP_DISPLAY_NAME_REG;

        createGroupAction();
        createClassGroupAction();
        createUnGroupSelectedAction();
        createUnGroupAllAction();
    }

    private void createClassGroupAction()
    {
        classGroupAction = new AbstractAction("Collapse Nodes By Class")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555752L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                Thread runLightWeightThread = new Thread( new Runnable()
                {

                    @Override
                    public void run()
                    {
                        if ( selectionManager.getUnhideAllAction().isEnabled() )
                        {
                            if ( selectionManager.showConfirmationDialogUnhideAll() )
                            {
                                performClassGroupAction();
                                selectionManager.getUnhideAllAction().setEnabled(false);
                            }
                        }
                        else
                        {
                            performClassGroupAction();
                        }
                    }


                } );

                runLightWeightThread.setPriority(Thread.NORM_PRIORITY);
                runLightWeightThread.start();
            }
        };
        classGroupAction.setEnabled(false);
    }

    private void performClassGroupAction()
    {
        selectionManager.unhideAll(false);

        HashSet<GraphNode> selected = null;
        GraphGroupNode graphGroupNode = null;
        float size = 0.0f;
        HashSet<GraphEdge> newEdgesSet = null;

        Collection<VertexClass> allVertexClasses = selectionManager.getLayoutFrame().getLayoutClassSetsManager().getCurrentClassSetAllClasses().getAllVertexClasses();
        layoutProgressBarDialog.prepareProgressBar(allVertexClasses.size(), "Now Collapsing Nodes By Class...");
        layoutProgressBarDialog.startProgressBar();

        for (VertexClass vc : allVertexClasses)
        {
            if (DEBUG_BUILD) println("Got Class:" + vc.getName() + vc.getClassID());
            layoutProgressBarDialog.incrementProgress();

            selectionManager.selectByClass(vc, false, false, false); // do not update viewers so as to avoid crashes!

            if (vc.getClassID() != 0)
            {
                while ( ( selected = getNextGroup( selectionManager.getSelectedNodes() ) ) != null )
                {
                    if (DEBUG_BUILD) println( "Current Diameter Size: " + selected.size() );
                    size = ( COLLAPSE_NODES_BY_VOLUME.get() ) ? (float)( pow((selected.size() * 6.0 / PI), 1.0 / 3.0) / pow((6.0 / PI), 1.0 / 3.0) ) * (DEFAULT_NODE_SIZE.get() / 2.0f)
                                                              : selected.size() / (DEFAULT_NODE_SIZE.get() / 2.0f);
                    if (DEBUG_BUILD) println("New Volume Scale is:" + size);

                    graphGroupNode = new GraphGroupNode(selected, selectionManager.getLayoutFrame().getNetworkRootContainer(), vc.getName(), selectionManager.getGroupManager(), size);
                    graphGroupNode.setGroupName( vc.getName() );
                    graphGroupNode.setVertexClass(vc);
                    insertAllNodesIntoMap(graphGroupNode);

                    groupIDs.put(graphGroupNode.getNodeID(), graphGroupNode); // autobox integer

                    proccessSelectedEdges(graphGroupNode);
                    proccessSelectedNodes(graphGroupNode);

                    newEdgesSet = graphGroupNode.setNewEdges( graph.getVisibleNodes() );
                    graph.getVisibleEdges().addAll(newEdgesSet);
                    graph.getGraphEdges().addAll(newEdgesSet);
                }
            }
        }

        selectionManager.deselectAll();
        graph.recreateVisibleEdges( graph.getGraphEdges() );
        graph.updateAllDisplayLists();

        unGroupSelectedAction.setEnabled(true);
        unGroupAllAction.setEnabled(true);
        selectionManager.setEnabledDeleteActions();

        layoutProgressBarDialog.endProgressBar();
        layoutProgressBarDialog.stopProgressBar();
    }

    private void createGroupAction()
    {
        groupAction = new AbstractAction("Collapse Selection")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555751L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                Thread runLightWeightThread = new Thread( new Runnable()
                {

                    @Override
                    public void run()
                    {
                        if ( selectionManager.getUnhideAllAction().isEnabled() )
                        {
                            if ( selectionManager.showConfirmationDialogUnhideAll() )
                            {
                                performGroupAction();
                                selectionManager.getUnhideAllAction().setEnabled(false);
                            }
                        }
                        else
                        {
                            performGroupAction();
                        }
                    }


                } );

                runLightWeightThread.setPriority(Thread.NORM_PRIORITY);
                runLightWeightThread.start();
            }
        };
        groupAction.setEnabled(false);
    }

    private void performGroupAction()
    {
        selectionManager.unhideAll(false);

        layoutProgressBarDialog.prepareProgressBar(1, "Now Collapsing Selected Nodes...");
        layoutProgressBarDialog.startProgressBar();

        // re-select with addNodesToSelected(selection, false) so as to fix an edges remainign problem
        HashSet<GraphNode> tempSelected = new HashSet<GraphNode>( selectionManager.getSelectedNodes() );
        selectionManager.clearAllSelection();
        selectionManager.addNodesToSelected(tempSelected, false, true, true, false, false, false); // do not update viewers so as to avoid crashes!

        HashSet<GraphNode> selected = null;
        GraphGroupNode graphGroupNode = null;
        float size = 0.0f;
        HashSet<GraphEdge> newEdgesSet = null;

        while ( ( selected = getNextGroup( selectionManager.getSelectedNodes() ) ) != null )
        {
            layoutProgressBarDialog.incrementProgress();

            if (DEBUG_BUILD) println( "Current Diameter Size: " + selected.size() );
            size = ( COLLAPSE_NODES_BY_VOLUME.get() ) ? (float)( pow((selected.size() * 6.0 / PI), 1.0 / 3.0) / pow((6.0 / PI), 1.0 / 3.0) ) * (DEFAULT_NODE_SIZE.get() / 2.0f)
                                                      : selected.size() / (DEFAULT_NODE_SIZE.get() / 2.0f);
            if (DEBUG_BUILD) println("New Volume Scale is:" + size);

            graphGroupNode = new GraphGroupNode(selected, selectionManager.getLayoutFrame().getNetworkRootContainer(), groupNodeName, selectionManager.getGroupManager(), size);
            graphGroupNode.setGroupName(groupDisplayName);
            insertAllNodesIntoMap(graphGroupNode);

            groupIDs.put(graphGroupNode.getNodeID(), graphGroupNode); // autobox integer

            proccessSelectedEdges(graphGroupNode);
            proccessSelectedNodes(graphGroupNode);

            newEdgesSet = graphGroupNode.setNewEdges( graph.getVisibleNodes() );
            graph.getVisibleEdges().addAll(newEdgesSet);
            graph.getGraphEdges().addAll(newEdgesSet);
        }

        selectionManager.deselectAll();
        graph.recreateVisibleEdges( graph.getGraphEdges() );
        graph.updateAllDisplayLists();

        unGroupSelectedAction.setEnabled(true);
        unGroupAllAction.setEnabled(true);
        selectionManager.setEnabledDeleteActions();

        applyCollapseGroupNaming();

        layoutProgressBarDialog.endProgressBar();
        layoutProgressBarDialog.stopProgressBar();
    }

    public void collapseSelectedNodesForCompleteGraphGrouping()
    {
        // re-select with addNodesToSelected(selection, false) so as to fix an edges remainign problem
        HashSet<GraphNode> tempSelected = new HashSet<GraphNode>( selectionManager.getSelectedNodes() );
        selectionManager.clearAllSelection();
        selectionManager.addNodesToSelected(tempSelected, false, true, true, false, false, false); // do not update viewers so as to avoid crashes!

        HashSet<GraphNode> selected = null;
        GraphGroupNode graphGroupNode = null;
        float size = 0.0f;
        HashSet<GraphEdge> newEdgesSet = null;

        while ( ( selected = getNextGroup( selectionManager.getSelectedNodes() ) ) != null )
        {
            if (DEBUG_BUILD) println( "Current Diameter Size: " + selected.size() );
            size = ( COLLAPSE_NODES_BY_VOLUME.get() ) ? (float)( pow((selected.size() * 6.0 / PI), 1.0 / 3.0) / pow((6.0 / PI), 1.0 / 3.0) ) * (DEFAULT_NODE_SIZE.get() / 2.0f)
                                                      : selected.size() / (DEFAULT_NODE_SIZE.get() / 2.0f);
            if (DEBUG_BUILD) println("New Volume Scale is:" + size);

            graphGroupNode = new GraphGroupNode(selected, selectionManager.getLayoutFrame().getNetworkRootContainer(), groupNodeName, selectionManager.getGroupManager(), size);
            graphGroupNode.setGroupName(groupDisplayName);
            insertAllNodesIntoMap(graphGroupNode);

            groupIDs.put(graphGroupNode.getNodeID(), graphGroupNode); // autobox integer

            proccessSelectedEdges(graphGroupNode);
            proccessSelectedNodes(graphGroupNode);

            newEdgesSet = graphGroupNode.setNewEdges( graph.getVisibleNodes() );
            graph.getVisibleEdges().addAll(newEdgesSet);
            graph.getGraphEdges().addAll(newEdgesSet);
        }
    }

    private void insertAllNodesIntoMap(GraphGroupNode graphGroupNode)
    {
        for ( GraphNode node : graphGroupNode.getGroupNodes() )
            allGroupNodesMap.put(node, graphGroupNode);
    }

    public GraphGroupNode getGroupNodebyID(int id)
    {
        return groupIDs.get(id); // autobox integer
    }

    public int getTotalGroups()
    {
        return groupIDs.size();
    }

    private void createUnGroupSelectedAction()
    {
        unGroupSelectedAction = new AbstractAction("UnCollapse Selected Groups")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555753L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                Thread runLightWeightThread = new Thread( new Runnable()
                {

                    @Override
                    public void run()
                    {
                        HashSet<GraphNode> selectedNodes = selectionManager.getSelectedNodes();
                        HashSet<GraphGroupNode> restoreGroups = new HashSet<GraphGroupNode>();
                        for (GraphNode graphNode : selectedNodes)
                            if (graphNode instanceof GraphGroupNode)
                                restoreGroups.add( (GraphGroupNode)graphNode );

                        layoutProgressBarDialog.prepareProgressBar(restoreGroups.size(), "Now UnCollapsing Selected Groups...");
                        layoutProgressBarDialog.startProgressBar();

                        restoreGroups(restoreGroups, layoutProgressBarDialog);
                        selectionManager.deselectAll();

                        if ( !isCollapsedMode() )
                        {
                            graph.rebuildGraph(); // so as to avoid undeletion bugs with network containers

                            unGroupSelectedAction.setEnabled(false);
                            unGroupAllAction.setEnabled(false);
                        }
                        selectionManager.setEnabledDeleteActions();

                        layoutProgressBarDialog.endProgressBar();
                        layoutProgressBarDialog.stopProgressBar();
                    }


                } );

                runLightWeightThread.setPriority(Thread.NORM_PRIORITY);
                runLightWeightThread.start();
            }
        };
        unGroupSelectedAction.setEnabled(false);
    }

    private void createUnGroupAllAction()
    {
        unGroupAllAction = new AbstractAction("UnCollapse All Groups")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555754L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                Thread runLightWeightThread = new Thread( new Runnable()
                {

                    @Override
                    public void run()
                    {
                        HashSet<GraphGroupNode> restoreGroups = new HashSet<GraphGroupNode>();
                        HashSet<GraphNode> selectedNodes = selectionManager.getSelectedNodes();

                        for (GraphNode graphNode : selectedNodes)
                            if (graphNode instanceof GraphGroupNode)
                                restoreGroups.add( (GraphGroupNode)graphNode );

                        HashSet<GraphNode> visibleNodes = graph.getVisibleNodes();
                        for (GraphNode graphNode : visibleNodes)
                            if (graphNode instanceof GraphGroupNode)
                                restoreGroups.add( (GraphGroupNode)graphNode );

                        layoutProgressBarDialog.prepareProgressBar(restoreGroups.size(), "Now UnCollapsing All Groups...");
                        layoutProgressBarDialog.startProgressBar();

                        restoreGroups(restoreGroups, layoutProgressBarDialog);
                        selectionManager.deselectAll();

                        if ( !isCollapsedMode() )
                        {
                            graph.rebuildGraph(); // so as to avoid undeletion bugs with network containers

                            unGroupSelectedAction.setEnabled(false);
                            unGroupAllAction.setEnabled(false);
                        }
                        selectionManager.setEnabledDeleteActions();

                        layoutProgressBarDialog.endProgressBar();
                        layoutProgressBarDialog.stopProgressBar();
                    }


                } );

                runLightWeightThread.setPriority(Thread.NORM_PRIORITY);
                runLightWeightThread.start();
            }
        };
        unGroupAllAction.setEnabled(false);
    }

    public void processNodes(HashSet<GraphNode> nodes)
    {
        GraphGroupNode groupNode = null;
        HashSet<GraphNode> nodesToRemove = new HashSet<GraphNode>();
        HashSet<GraphGroupNode> groupsToAdd = new HashSet<GraphGroupNode>();

        for (GraphNode graphNode : nodes)
        {
            if ( ( groupNode = extractGroupFromNode(graphNode) ) != null )
            {
                nodesToRemove.add(graphNode);
                groupsToAdd.add(groupNode);
            }
        }

        nodes.removeAll(nodesToRemove);
        nodes.addAll(groupsToAdd);
    }

    private void proccessSelectedEdges(GraphGroupNode graphGroupNode)
    {
        HashSet<GraphEdge> selectedGraphEdges = selectionManager.getSelectedEdges();
        graphGroupNode.setSelectedEdges(selectedGraphEdges);
        graph.getGraphEdges().removeAll(selectedGraphEdges);
    }

    private void proccessSelectedNodes(GraphGroupNode graphGroupNode)
    {
        HashMap<Integer, GraphNode> allNodesMap = graph.getGraphNodesMap();
        HashSet<GraphNode> groupNodes = graphGroupNode.getGroupNodes();

        for (GraphNode node : groupNodes)
            allNodesMap.remove( node.getNodeID() );

        allNodesMap.put(graphGroupNode.getNodeID(), graphGroupNode);

        graph.getVisibleNodes().removeAll(groupNodes);
        graph.getVisibleNodes().add(graphGroupNode);
    }

    private HashSet<GraphNode> getNextGroup(HashSet<GraphNode> selected)
    {
        if (selected.size() > 0)
        {
            HashSet<GraphNode> newSelectedGroup = new HashSet<GraphNode>(selected);
            graph.getVisibleNodes().removeAll(selected);
            selected.clear();

            return newSelectedGroup;
        }
        else
            return null;
    }

    public AbstractAction getClassGroupAction()
    {
        return classGroupAction;
    }

    public AbstractAction getGroupAction()
    {
        return groupAction;
    }

    public AbstractAction getUnGroupSelectedAction()
    {
        return unGroupSelectedAction;
    }

    public AbstractAction getUnGroupAllAction()
    {
        return unGroupAllAction;
    }

    private void restoreGroups(HashSet<GraphGroupNode> groups, LayoutProgressBarDialog layoutProgressBarDialog)
    {
        for (GraphGroupNode graphGroupNode : groups)
        {
            if (layoutProgressBarDialog != null) layoutProgressBarDialog.incrementProgress();
            restoreGroup(graphGroupNode);
        }

        graph.updateAllDisplayLists();
    }

    private void restoreGroup(GraphGroupNode graphGroupNode)
    {
        graphGroupNode.removeGroupEdgesOnDerivedNodes();
        graphGroupNode.unCollapseProperties();

        removeNodesFromMap(graphGroupNode);
        addAllGroupNodesToGlobal(graphGroupNode);

        graph.getVisibleEdges().removeAll( graphGroupNode.getNodeEdges() );
        graph.getGraphEdges().removeAll( graphGroupNode.getNodeEdges() );

        HashSet<GraphEdge> edgesToadd = graphGroupNode.getProcessedGroupEdges();
        graph.getVisibleEdges().addAll(edgesToadd);
        graph.getGraphEdges().addAll(edgesToadd);

        graph.getVisibleNodes().remove(graphGroupNode);
        graph.getVisibleNodes().addAll( graphGroupNode.getGroupNodes() );

        selectionManager.clearAllSelection();
        graph.getGraphNodesMap().remove( graphGroupNode.getNodeID() );

        selectionManager.getSelectedNodes().clear();
    }

    private void removeNodesFromMap(GraphGroupNode graphGroupNode)
    {
        for ( GraphNode node : graphGroupNode.getGroupNodes() )
            allGroupNodesMap.remove(node);
    }

    public GraphGroupNode extractGroupFromNode(GraphNode graphNode)
    {
        return allGroupNodesMap.get(graphNode);
    }

    private void addAllGroupNodesToGlobal(GraphGroupNode graphGroupNode)
    {
        HashMap<Integer, GraphNode> allNodesMap = graph.getGraphNodesMap();
        for ( GraphNode graphNode : graphGroupNode.getGroupNodes() )
            allNodesMap.put(graphNode.getNodeID(), graphNode);
    }

    public void applyCollapseGroupNaming()
    {
        ArrayList<GraphGroupNode> allGroupNodes = new ArrayList<GraphGroupNode>();
        for ( GraphNode graphNode : selectionManager.getGraph().getGraphNodes() )
            if (graphNode instanceof GraphGroupNode)
                allGroupNodes.add( (GraphGroupNode)graphNode );
        Collections.sort(allGroupNodes); // sorts GraphGroupNodes by size, largest to smallest

        int maxIntegerCharacters = Integer.toString( allGroupNodes.size() ).length();
        String zerosToPut = "";
        int prevIntegerCharacters = 0;
        int completeGraphGroupNumberIndex = 0;
        int currentIntegerCharacters = 0;
        for (GraphGroupNode graphGroupNode : allGroupNodes)
        {
            currentIntegerCharacters = Integer.toString(++completeGraphGroupNumberIndex).length();
            if (currentIntegerCharacters > prevIntegerCharacters)
            {
                zerosToPut = "";
                for (int i = 0; i < (maxIntegerCharacters - currentIntegerCharacters); i++)
                    zerosToPut += "0";
            }
            prevIntegerCharacters = currentIntegerCharacters;

            graphGroupNode.setNodeName(groupNodeName + zerosToPut + completeGraphGroupNumberIndex);
        }
    }

    public boolean isCollapsedMode()
    {
        return !allGroupNodesMap.isEmpty();
    }

    public Collection<GraphNode> getAllNodesInGroups()
    {
        return allGroupNodesMap.keySet();
    }

    public void setCompleteGraphMode()
    {
        groupNodeName = GROUP_NAME_COMPLETE;
        groupDisplayName = GROUP_DISPLAY_NAME_COMPLETE;
    }

    public void resetMode()
    {
        groupNodeName = GROUP_NAME_REG;
        groupDisplayName = GROUP_DISPLAY_NAME_REG;
    }

    public void resetState()
    {
        groupIDs.clear();
        allGroupNodesMap.clear();

        unGroupSelectedAction.setEnabled(false);
        unGroupAllAction.setEnabled(false);
    }


}