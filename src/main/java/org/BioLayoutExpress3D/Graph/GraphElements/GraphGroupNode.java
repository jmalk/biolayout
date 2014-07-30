package org.BioLayoutExpress3D.Graph.GraphElements;

import java.util.*;
import org.BioLayoutExpress3D.Graph.Selection.*;
import org.BioLayoutExpress3D.Network.*;
import org.BioLayoutExpress3D.Utils.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* @author Leon Goldovsky, full refactoring by Thanos Theo, 2008-2009
* @version 3.0.0.0
*
*/

public final class GraphGroupNode extends GraphNode implements Comparable<GraphNode>
{
    private HashSet<GraphNode> groupNodes = null;
    private HashSet<GraphEdge> groupEdges = null;
    private GroupManager groupManager = null;
    private String groupName = "";
    private int nodeID = 0;

    public GraphGroupNode(HashSet<GraphNode> groupNodes, NetworkContainer nc, String name, GroupManager groupManager, float nodeSize)
    {
        super( new Vertex(name, nc) );

        this.groupNodes = groupNodes;
        this.groupManager = groupManager;

        setLocation( findGroupPosition() );
        nodeID = -(groupManager.getTotalGroups() + 1); // negative number to distinguish group nodes
        setNodeSize(nodeSize);
    }

    public HashSet<GraphEdge> setNewEdges(HashSet<GraphNode> visibleNodes)
    {
        GraphNode firstNode = null;
        GraphNode secondNode = null;
        GraphEdge newGraphEdge = null;
        for (GraphEdge graphEdge : groupEdges)
        {
            firstNode = graphEdge.getNodeFirst();
            secondNode = graphEdge.getNodeSecond();

            if (DEBUG_BUILD)
            {
                println(visibleNodes.contains(firstNode) + " " + visibleNodes.contains(secondNode));
                println(firstNode.getNodeName() + " " + secondNode.getNodeName());
            }

            if (groupNodes.contains(firstNode) && !groupNodes.contains(secondNode) && secondNode != this)
            {
                if ( visibleNodes.contains(secondNode) )
                {
                    newGraphEdge = new GraphEdge(this, secondNode, new Edge(getVertex(), secondNode.getVertex(), 0), 0);
                    // visibleNodes.remove(graphEdge);
                    nodeEdges.add(newGraphEdge);

                    if (DEBUG_BUILD) println("Creating Edge 1" + newGraphEdge.getNodeFirst().getNodeName() + " " + newGraphEdge.getNodeSecond().getNodeName());

                    secondNode.getNodeEdges().add(newGraphEdge);
                    addNodeChild( graphEdge.getNodeSecond() );
                }
            }
            else if (groupNodes.contains(secondNode) && !groupNodes.contains(firstNode) && firstNode != this)
            {
                if ( visibleNodes.contains(firstNode) )
                {
                    newGraphEdge = new GraphEdge(firstNode, this, new Edge(firstNode.getVertex(), getVertex(), 0), 0);
                    // visibleNodes.remove(graphEdge);

                    if (DEBUG_BUILD) println("Creating Edge 2" + newGraphEdge.getNodeFirst().getNodeName() + " " + newGraphEdge.getNodeSecond().getNodeName());

                    nodeEdges.add(newGraphEdge);
                    firstNode.getNodeEdges().add(newGraphEdge);
                    addNodeParent( graphEdge.getNodeSecond() );
                }
            }
        }

        return nodeEdges;
    }

    @Override
    public int getNodeID()
    {
        return nodeID;
    }

    private Point3D findGroupPosition()
    {
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float minZ = Float.MAX_VALUE;
        float maxX = 0.0f;
        float maxY = 0.0f;
        float maxZ = 0.0f;

        for (GraphNode graphNode : groupNodes)
        {
            if (graphNode.getX() < minX)
                minX = graphNode.getX();

            if (graphNode.getX() > maxX)
                maxX = graphNode.getX();

            if (graphNode.getY() < minY)
                minY = graphNode.getY();

            if (graphNode.getY() > maxY)
                maxY = graphNode.getY();

            if (graphNode.getZ() < minZ)
                minZ = graphNode.getZ();

            if (graphNode.getZ() > maxZ)
                maxZ = graphNode.getZ();
        }

        return new Point3D(minX + (maxX - minX) / 2, minY + (maxY - minY) / 2, minZ + (maxZ - minZ) / 2);
    }

    public void setSelectedEdges(HashSet<GraphEdge> selectedGraphEdges)
    {
        groupEdges = new HashSet<GraphEdge>(selectedGraphEdges);
    }

    public HashSet<GraphEdge> getGroupEdges()
    {
        return groupEdges;
    }

    public HashSet<GraphNode> getGroupNodes()
    {
        return groupNodes;
    }

    @Override
    public void setLocation(float xPos, float yPos, float zPos)
    {
        float deltaX = xPos - getX();
        float deltaY = yPos - getY();
        float deltaZ = zPos - getZ();

        for (GraphNode graphNode : groupNodes)
            graphNode.setLocation(graphNode.getX() + deltaX, graphNode.getY() + deltaY,graphNode.getZ() + deltaZ);

        super.setLocation(xPos, yPos, zPos);
    }

    public void removeGroupEdgesOnDerivedNodes()
    {
        for ( GraphNode graphNode : getNodeNeighbours() )
        {
            graphNode.getNodeEdges().removeAll(nodeEdges);
            graphNode.getNodeNeighbours().remove(this);
            graphNode.getNodeParents().remove(this);
            graphNode.getNodeChildren().remove(this);
        }
    }

    private void collectGroupEdges()
    {
        for (GraphNode graphNode : groupNodes)
            groupEdges.addAll( graphNode.getNodeEdges() );
    }

    public HashSet<GraphEdge> getProcessedGroupEdges()
    {
        HashSet<GraphEdge> returningEdges = new HashSet<GraphEdge>();

        collectGroupEdges();

        GraphEdge newGraphEdge = null;
        GraphNode firstNode = null;
        GraphNode secondNode = null;
        GraphGroupNode groupNode = null;

        for (GraphEdge graphEdge : groupEdges)
        {
            firstNode = graphEdge.getNodeFirst();
            secondNode = graphEdge.getNodeSecond();

            if ( groupNodes.contains(firstNode) && !groupNodes.contains(secondNode) )
            {
                if ( ( groupNode = groupManager.extractGroupFromNode(secondNode) ) != null )
                {
                    newGraphEdge = new GraphEdge(firstNode, groupNode, new Edge(firstNode.getVertex(), groupNode.getVertex(), 0), 0);
                    returningEdges.add(newGraphEdge);
                    firstNode.addEdge(newGraphEdge);
                    groupNode.addEdge(newGraphEdge);
                    // addNeighbor(graphEdge.getNodeSecond());
                    addNodeParent(graphEdge.getNodeSecond());
                }
                else if ( !(secondNode instanceof GraphGroupNode) )
                {
                    returningEdges.add(graphEdge);
                }
            }
            else if ( !groupNodes.contains(firstNode) && groupNodes.contains(secondNode) )
            {
                if ( ( groupNode = groupManager.extractGroupFromNode(firstNode) ) != null )
                {
                    newGraphEdge = new GraphEdge(groupNode, secondNode, new Edge(groupNode.getVertex(), firstNode.getVertex(), 0), 0);
                    returningEdges.add(newGraphEdge);
                    secondNode.addEdge(newGraphEdge);
                    groupNode.addEdge(newGraphEdge);
                    // addNeighbor(graphEdge.getNodeSecond());
                    addNodeParent(graphEdge.getNodeSecond());
                }
                else if ( !(firstNode instanceof GraphGroupNode) )
                {
                    returningEdges.add(graphEdge);
                }
            }
            else
            {
                returningEdges.add(graphEdge);
            }
        }

        return returningEdges;
    }

    public void unCollapseProperties()
    {
        if ( (getVertexClass() != null) && (getVertexClass().getClassID() != 0) )
            for (GraphNode graphNode : groupNodes)
                graphNode.setVertexClass( getVertexClass() );
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    public String getGroupName()
    {
        return groupName;
    }

    /**
    *  Overriden compareTo() method for GraphGroupNode.
    *  Note, it uses a comparison based on the size value particularly tailored for graph group nodes created by the collapse processes.
    *  See the GroupManager.applyCollapseGroupNaming() for usage details.
    */
    @Override
    public int compareTo(GraphNode obj)
    {
        return ( this.getNodeSize() < obj.getNodeSize() ) ? 1 : ( this.getNodeSize() > obj.getNodeSize() ) ? -1 : 0;
    }


}