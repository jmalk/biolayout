package org.BioLayoutExpress3D.Graph.Selection;

import java.util.*;
import org.BioLayoutExpress3D.CoreUI.Dialogs.*;
import org.BioLayoutExpress3D.Graph.*;
import org.BioLayoutExpress3D.Network.*;
import static org.BioLayoutExpress3D.DataStructures.StackDataStructureTypes.*;

/**
*
* Re-implemented GraphUndoDelete with custom Stack data structure usage.
*
*
* @author Thanos Theo, 2008-2009
* @version 3.0.0.0
*
*/

final class GraphUndoDelete // package access
{
    private SelectionManager selectionManager = null;
    private org.BioLayoutExpress3D.DataStructures.Stack<HashSet<Vertex>> nodeSelectedNodesStack = null;
    private org.BioLayoutExpress3D.DataStructures.Stack<HashSet<Edge>> nodeSelectedEdgesStack = null;
    private int howManyNodesAndEdges = 0;

    public GraphUndoDelete(SelectionManager selectionManager)
    {
        this.selectionManager = selectionManager;

        nodeSelectedNodesStack = new org.BioLayoutExpress3D.DataStructures.Stack<HashSet<Vertex>>(USE_LINKEDLIST);
        nodeSelectedEdgesStack = new org.BioLayoutExpress3D.DataStructures.Stack<HashSet<Edge>>(USE_LINKEDLIST);
    }

    public void pushSelected(HashSet<Vertex> selectedNodes, HashSet<Edge> selectedEdges)
    {
        nodeSelectedNodesStack.push(selectedNodes);
        nodeSelectedEdgesStack.push(selectedEdges);
        howManyNodesAndEdges = selectedNodes.size() + selectedEdges.size();

        setEnabledDeleteActions();
    }

    public int getTotalSizeOfNodesAndEdgesToRecover()
    {
        return howManyNodesAndEdges;
    }

    public int howManyStackSteps()
    {
        return nodeSelectedNodesStack.size();
    }

    public void setEnabledDeleteActions()
    {
        boolean enabled = (nodeSelectedNodesStack.size() > 0) &&  !selectionManager.getGroupManager().isCollapsedMode();
        selectionManager.getUndoLastDeleteAction().setEnabled(enabled);
        selectionManager.getUndeleteAllNodesAction().setEnabled(enabled);
    }

    public void undoDelete(NetworkContainer nc, Graph graph, LayoutProgressBarDialog layoutProgressBarDialog)
    {
        if (nodeSelectedNodesStack.size() > 0)
        {
            for ( Vertex vertex : nodeSelectedNodesStack.pop() )
            {
                if (layoutProgressBarDialog != null) layoutProgressBarDialog.incrementProgress();
                howManyNodesAndEdges--;

                nc.getVerticesMap().put(vertex.getVertexName(), vertex);
            }

            for ( Edge edge : nodeSelectedEdgesStack.pop() )
            {
                if (layoutProgressBarDialog != null) layoutProgressBarDialog.incrementProgress();
                howManyNodesAndEdges--;

                nc.getEdges().add(edge);
            }

            graph.rebuildGraph();
        }

        setEnabledDeleteActions();
    }

    public void clear()
    {
        nodeSelectedNodesStack.clear();
        nodeSelectedEdgesStack.clear();
        howManyNodesAndEdges = 0;

        setEnabledDeleteActions();
    }


}