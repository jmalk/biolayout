package org.BioLayoutExpress3D.Graph.GraphElements;

import java.awt.*;
import java.util.*;
import org.BioLayoutExpress3D.Network.*;
import org.BioLayoutExpress3D.Utils.*;
import static org.BioLayoutExpress3D.DataStructures.StackDataStructureTypes.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
*
* @author Full refactoring by Thanos Theo, 2008-2009-2010-2011-2012
* @version 3.0.0.0
*
*/

public class GraphNode implements Comparable<GraphNode>
{
    private Vertex vertex = null;
    private Point3D originalPoint = null;

    // stacks used for undo/redo node dragging in 2D mode
    private org.BioLayoutExpress3D.DataStructures.Stack<Point3D> undoPointStack = null;
    private org.BioLayoutExpress3D.DataStructures.Stack<Point3D> redoPointStack = null;

    private HashSet<GraphNode> nodeChildren = null;
    private HashSet<GraphNode> nodeParents = null;
    protected HashSet<GraphEdge> nodeEdges = null;

    public GraphNode(Vertex vertex)
    {
        this.vertex = vertex;
        this.originalPoint = new Point3D( vertex.getVertexPoint() );

        nodeEdges = new HashSet<GraphEdge>(0);    // so as to minimize memory usage as default load capacity is 16
        nodeParents = new HashSet<GraphNode>(0);  // so as to minimize memory usage as default load capacity is 16
        nodeChildren = new HashSet<GraphNode>(0); // so as to minimize memory usage as default load capacity is 16
    }

    public void addNodeParent(GraphNode graphNode)
    {
        nodeParents.add(graphNode);
    }

    public void addNodeChild(GraphNode graphNode)
    {
        nodeChildren.add(graphNode);
    }

    public void addEdge(GraphEdge graphEdge)
    {
        nodeEdges.add(graphEdge);
    }

    public HashSet<GraphNode> getNodeChildren()
    {
        return nodeChildren;
    }

    public HashSet<GraphNode> getNodeParents()
    {
        return nodeParents;
    }

    public HashSet<GraphNode> getNodeNeighbours()
    {
        HashSet<GraphNode> neighbours = new HashSet<GraphNode>( nodeChildren.size() + nodeParents.size() );
        neighbours.addAll(nodeChildren);
        neighbours.addAll(nodeParents);

        return neighbours;
    }

    public Shapes2D getNode2DShape()
    {
        return vertex.getVertex2DShape();
    }

    public Shapes3D getNode3DShape()
    {
        return vertex.getVertex3DShape();
    }

    public float getTransparencyAlpha()
    {
        return vertex.getVertexTransparencyAlpha();
    }

    public String getURLString()
    {
        return vertex.getVertexURLString();
    }

    public void setNode2DShape(Shapes2D node2DShape)
    {
        vertex.setVertex2DShape(node2DShape);
    }

    public void setNode3DShape(Shapes3D node3DShape)
    {
        vertex.setVertex3DShape(node3DShape);
    }

    public void setTransparencyAlpha(float transparencyAlpha)
    {
        vertex.setVertexTransparencyAlpha(transparencyAlpha);
    }

    public void setURLString(String vertexURLString)
    {
        vertex.setVertexURLString(vertexURLString);
    }

    public void setMinMaxNodeSize(float nodeSize, boolean isManualChange)
    {
        float minNodeSize = (isManualChange) ? MIN_MANUAL_NODE_SIZE : MIN_NODE_SIZE;
        setNodeSize( (nodeSize > MAX_NODE_SIZE) ? MAX_NODE_SIZE : ( (nodeSize < minNodeSize) ? minNodeSize : nodeSize) );
    }

    public String getNodeName()
    {
        return vertex.getVertexName();
    }

    public int getNodeID()
    {
        return vertex.getVertexID();
    }

    public void setNodeName(String nodeName)
    {
        vertex.setVertexName(nodeName);
    }

    public Color getColor()
    {
        if (!vertex.getOverrideClassColor() && vertex.getVertexClass() != null)
        {
            return vertex.getVertexClass().getColor();
        }

        return vertex.getVertexColor();
    }

    public String getHexColor()
    {
       String s = Integer.toHexString( getColor().getRGB() & 0xffffff );
       if (s.length() < 6)
       {
           // pad on left with zeros
           s = "000000".substring( 0, 6 - s.length() ) + s;
       }

       return '#' + s;
    }

    public void removeColorOverride()
    {
        vertex.removeColorOverride();
    }

    public void setColor(Color color)
    {
        vertex.setVertexColor(color);
    }

    public Point3D getPoint()
    {
        return vertex.getVertexPoint();
    }

    public void clearPointStacks()
    {
        undoPointStack = null;
        redoPointStack = null;
    }

    public boolean isEmptyUndoPointStack()
    {
        return ( (undoPointStack == null) || undoPointStack.isEmpty() );
    }

    public boolean isEmptyRedoPointStack()
    {
        return ( (redoPointStack == null) || redoPointStack.isEmpty() );
    }

    public void pushLocationInUndoPointStack()
    {
        if (undoPointStack == null)
            undoPointStack = new org.BioLayoutExpress3D.DataStructures.Stack<Point3D>(USE_LINKEDLIST);
        undoPointStack.push( new Point3D( vertex.getVertexPoint() ) );
    }

    public void pushLocationInRedoPointStack()
    {
        if (redoPointStack == null)
            redoPointStack = new org.BioLayoutExpress3D.DataStructures.Stack<Point3D>(USE_LINKEDLIST);
        redoPointStack.push( new Point3D( vertex.getVertexPoint() ) );
    }

    public void popLocationFromUndoPointStack()
    {
        if ( !isEmptyUndoPointStack() )
            vertex.setVertexPoint( undoPointStack.pop() );
    }

    public void popLocationFromRedoPointStack()
    {
        if ( !isEmptyRedoPointStack() )
            vertex.setVertexPoint( redoPointStack.pop() );
    }

    public void setLocation(Point3D point)
    {
        vertex.getVertexPoint().setLocation(point);
    }

    public void setLocation(float xPos, float yPos)
    {
        vertex.getVertexPoint().setLocation(xPos, yPos);
    }

    public void setLocation(float xPos, float yPos, float zPos)
    {
        vertex.getVertexPoint().setLocation(xPos, yPos, zPos);
    }

    public void scaleLocation(float scaleFactor)
    {
        vertex.scaleLocation(scaleFactor);
    }

    public void burstUpdate()
    {
        vertex.getVertexPoint().setLocation(originalPoint);
    }

    public float getX()
    {
        return vertex.getVertexPoint().getX();
    }

    public float getY()
    {
        return vertex.getVertexPoint().getY();
    }

    public float getZ()
    {
        return vertex.getVertexPoint().getZ();
    }

    public Vertex getVertex()
    {
        return vertex;
    }

    public void setVertexLocation(float locationX, float locationY)
    {
        vertex.setVertexLocation(locationX, locationY);
    }

    public void setVertexLocation(float locationX, float locationY, float locationZ)
    {
        vertex.setVertexLocation(locationX, locationY, locationZ);
    }

    public VertexClass getVertexClass()
    {
        return vertex.getVertexClass();
    }

    public void setVertexClass(VertexClass vertexClass)
    {
        vertex.setVertexClass(vertexClass);
    }

    public void setShowNodeName(boolean showNodeName)
    {
        vertex.setShowVertexName(showNodeName);
    }

    public boolean isShowNodeName()
    {
        return vertex.isShowVertexName();
    }

    public HashSet<GraphEdge> getNodeEdges()
    {
        return nodeEdges;
    }

    public HashSet<GraphNode> getNeighborsIntersection()
    {
        HashSet<GraphNode> nodeNeighbours = getNodeNeighbours();
        nodeNeighbours.add(this);

        return nodeNeighbours;
    }

    public boolean isOverrideClassColor()
    {
        return vertex.getOverrideClassColor();
    }

    public float getNodeSize()
    {
        return vertex.getVertexSize();
    }

    public void setNodeSize(float nodeSize)
    {
        vertex.setVertexSize(nodeSize);
    }

    public String[] getNodeDescription()
    {
        return vertex.getRawDescription().split("--");
    }

    public boolean ismEPNComponent()
    {
        return vertex.ismEPNComponent();
    }

    public boolean ismEPNTransition()
    {
        return vertex.ismEPNTransition();
    }

    /**
    *  Overriden compareTo() method for GraphNode.
    *  Note, it uses a comparison particularly tailored for saving Graphml files, omitting the first letter (the 'n')
    *  with the substring command and then do a Integer comparison with the rest of the name (the Graphml nodes have keys of 'nXXX' style, where XXX a number)
    *  See the SimpleSaver.saveFile() for usage details.
    */
    @Override
    public int compareTo(GraphNode obj)
    {
        return (Integer.parseInt( obj.getNodeName().substring( obj.getNodeName().lastIndexOf("n") + 1, obj.getNodeName().length() ) ) > Integer.parseInt( this.getNodeName().substring( obj.getNodeName().lastIndexOf("n") + 1, this.getNodeName().length() ) ) ) ? -1 : ( ( Integer.parseInt( obj.getNodeName().substring( obj.getNodeName().lastIndexOf("n") + 1, obj.getNodeName().length() ) ) == Integer.parseInt( this.getNodeName().substring( obj.getNodeName().lastIndexOf("n") + 1, this.getNodeName().length() ) ) ) ? 0 : 1);
    }
}