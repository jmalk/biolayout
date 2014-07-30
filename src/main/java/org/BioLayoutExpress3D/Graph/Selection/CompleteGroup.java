package org.BioLayoutExpress3D.Graph.Selection;

import java.io.*;
import java.util.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.CoreUI.Dialogs.*;
import org.BioLayoutExpress3D.Graph.GraphElements.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* @author Leon Goldovsky, full refactoring by Thanos Theo, 2008-2009
* @version 3.0.0.0
*
*/

final class CompleteGroup implements Runnable // package access
{
    private LayoutFrame layoutFrame = null;
    private SelectionManager selectionManager = null;
    private LayoutProgressBarDialog layoutProgressBarDialog = null;

    public CompleteGroup(LayoutFrame layoutFrame, SelectionManager selectionManager, LayoutProgressBarDialog layoutProgressBarDialog)
    {
        this.layoutFrame = layoutFrame;
        this.selectionManager = selectionManager;
        this.layoutProgressBarDialog = layoutProgressBarDialog;
    }

    @Override
    public void run()
    {
        if ( selectionManager.getUnhideAllAction().isEnabled() )
        {
            if ( selectionManager.showConfirmationDialogUnhideAll() )
            {
                processCompleteGraphGroup();
                selectionManager.getUnhideAllAction().setEnabled(false);
            }
        }
        else
        {
            processCompleteGraphGroup();
        }
    }

    private void processCompleteGraphGroup()
    {
        selectionManager.unhideAll(false);
        selectionManager.clearAllSelection();

        HashSet<Collection<GraphNode>> allGroups = new HashSet<Collection<GraphNode>>();
        searchGroups(allGroups);

        selectionManager.getGroupManager().setCompleteGraphMode();
        groupGroups(allGroups);
        selectionManager.getGroupManager().resetMode();

        selectionManager.deselectAll();
        layoutFrame.getGraph().recreateVisibleEdges( layoutFrame.getGraph().getGraphEdges() );
        layoutFrame.getGraph().updateAllDisplayLists();

        selectionManager.getGroupManager().getUnGroupSelectedAction().setEnabled(true);
        selectionManager.getGroupManager().getUnGroupAllAction().setEnabled(true);
        selectionManager.setEnabledDeleteActions();
    }

    private void searchGroups(HashSet<Collection<GraphNode>> allGroups)
    {
        if (DEBUG_BUILD) println("Grouping Complete Sub Graphs");

        Collection<GraphNode> visibleNodes = selectionManager.getVisibleNodes();
        GroupGraphNodeComparator groupGraphNodeComparator = new GroupGraphNodeComparator();
        List<GraphNode> group = null;

        layoutProgressBarDialog.prepareProgressBar(visibleNodes.size(), "Sorting & Grouping Complete Sub Graphs");
        layoutProgressBarDialog.startProgressBar();

        for (GraphNode visibleGraphNode : visibleNodes)
        {
            layoutProgressBarDialog.incrementProgress();

            group = new ArrayList<GraphNode>( visibleGraphNode.getNeighborsIntersection() );
            Collections.sort(group, groupGraphNodeComparator);

            if (group.size() > 2)
                if ( !allGroups.contains(group) )
                    allGroups.add(group);
        }

        layoutProgressBarDialog.endProgressBar();
        layoutProgressBarDialog.stopProgressBar();
    }

    private void groupGroups(HashSet<Collection<GraphNode>> allGroups)
    {
        if (DEBUG_BUILD) println("Searching For Complete Sub Graphs");

        layoutProgressBarDialog.prepareProgressBar(allGroups.size(), "Searching For Complete Sub Graphs");
        layoutProgressBarDialog.startProgressBar();

        for (Collection<GraphNode> groupCollection : allGroups)
        {
            layoutProgressBarDialog.incrementProgress();

            selectionManager.addNodesToSelected(groupCollection, false, true, true, false, false, false); // do not update viewers so as to avoid crashes!
            selectionManager.getGroupManager().collapseSelectedNodesForCompleteGraphGrouping();
        }

        selectionManager.getGroupManager().applyCollapseGroupNaming();

        layoutProgressBarDialog.endProgressBar();
        layoutProgressBarDialog.stopProgressBar();
    }

    private static class GroupGraphNodeComparator implements Comparator<GraphNode>, Serializable
    {

        /**
        *  Serial version UID variable for the GroupGraphNodeComparator class.
        */
        public static final long serialVersionUID = 111222333444555623L;

        /**
        *  Overriden compare() method for GroupGraphNodeComparator.
        */
        @Override
        public int compare(GraphNode graphNodeA, GraphNode graphNodeB)
        {
            return ( graphNodeA.hashCode() > graphNodeB.hashCode() ) ? 1 : ( ( graphNodeA.hashCode() < graphNodeB.hashCode() ) ? -1 : 0);
        }


    }


}