package org.BioLayoutExpress3D.Network;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.DataStructures.*;
import org.BioLayoutExpress3D.Graph.GraphElements.*;
import static org.BioLayoutExpress3D.DataStructures.StackDataStructureTypes.*;
import static org.BioLayoutExpress3D.Network.GraphmlLookUpmEPNTables.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*  The GraphmlNetworkContainer class encapsulates all the necessary information for the graphml based network.
*
*
* @author Thanos Theo, 2008-2009
* @version 3.0.0.0
*
*/

public class GraphmlNetworkContainer
{

    private NetworkContainer nc = null;
    private LayoutClassSetsManager layoutClassSetsManager = null;

    // Variables used for the graphml file format
    private boolean isGraphml = false;
    private boolean isPetriNet = false;
    private boolean hasStandardPetriNetTransitions = false;
    private HashMap<String, Tuple6<float[], String[], String[], String[], String[], String>> allGraphmlNodesMap = null;
    private HashMap<String, Tuple6<String, Tuple2<float[], ArrayList<Point2D.Float>>, String[], String[], String[], String[]>> allGraphmlEdgesMap = null;
    private HashMap<String, float[]> allOrigGraphmlMapCoordsMap = null;
    private HashMap<String, org.BioLayoutExpress3D.DataStructures.Stack<float[]>> allUndoGraphmlMapCoordsStacksMap = null;
    private HashMap<String, org.BioLayoutExpress3D.DataStructures.Stack<float[]>> allRedoGraphmlMapCoordsStacksMap = null;
    private float rangeX = 0.0f;
    private float rangeY = 0.0f;

    private HashMap<Vertex, Tuple4<GraphmlShapesGroup1, GraphmlShapesGroup2, GraphmlShapesGroup3, Color>> allNodesmEPNShapeGroupTuplesMap = null;
    private ArrayList<GraphmlComponentContainer> allPathwayComponentContainersFor3D = null;
    private ArrayList<GraphmlComponentContainer> allPathwayComponentContainersFor2D = null;

    /**
    *  The constructor of the GraphmlNetworkContainer class.
    */
    public GraphmlNetworkContainer(NetworkContainer nc, LayoutClassSetsManager layoutClassSetsManager)
    {
        this.nc = nc;
        this.layoutClassSetsManager = layoutClassSetsManager;
    }

    /**
    *  Initializes the allNodesmEPNShapeGroupTuplesMap variable.
    */
    public void initAllNodesmEPNShapeGroupTuplesMap()
    {
        allNodesmEPNShapeGroupTuplesMap = new HashMap<Vertex, Tuple4<GraphmlShapesGroup1, GraphmlShapesGroup2, GraphmlShapesGroup3, Color>>();
    }

    /**
    *  Adds a network connection for the graphml graph.
    */
    public void addNetworkConnectionForGraphml(String first, Shapes2D firstShape2D, Shapes3D firstShape3D, float firstShapeSize, boolean firstIsmEPNTransition, boolean firstIsmEPNComponent, Tuple4<GraphmlShapesGroup1, GraphmlShapesGroup2, GraphmlShapesGroup3, Color> node1mEPNShapeGroupsTuple4,
                                               String second, Shapes2D secondShape2D, Shapes3D secondShape3D, float secondShapeSize, boolean secondIsmEPNTransition, boolean secondIsmEPNComponent, Tuple4<GraphmlShapesGroup1, GraphmlShapesGroup2, GraphmlShapesGroup3, Color> node2mEPNShapeGroupsTuple4,
                                               String edgeName, boolean isTotalInhibitorEdge, boolean isPartialInhibitorEdge, boolean hasDualArrowHead)
    {
        Vertex vertex1 = null;
        Vertex vertex2 = null;

        if ( nc.verticesMap.containsKey(first) )
        {
            vertex1 = nc.verticesMap.get(first);
        }
        else
        {
            vertex1 = new Vertex(first, nc);

            if (firstShape2D != null)
                vertex1.setVertex2DShape(firstShape2D);
            if (firstShape3D != null)
                vertex1.setVertex3DShape(firstShape3D);
            if (firstShapeSize != 0.0f)
                vertex1.setVertexSize(firstShapeSize);
            if (firstIsmEPNTransition)
                vertex1.setmEPNTransition();
            if (firstIsmEPNComponent)
                vertex1.setmEPNComponent();

            nc.verticesMap.put(first, vertex1);
            allNodesmEPNShapeGroupTuplesMap.put(vertex1, node1mEPNShapeGroupsTuple4);
        }

        if ( nc.verticesMap.containsKey(second) )
        {
            vertex2 = nc.verticesMap.get(second);
        }
        else
        {
            vertex2 = new Vertex(second, nc);

            if (secondShape2D != null)
                vertex2.setVertex2DShape(secondShape2D);
            if (secondShape3D != null)
                vertex2.setVertex3DShape(secondShape3D);
            if (secondShapeSize != 0.0f)
                vertex2.setVertexSize(secondShapeSize);
            if (secondIsmEPNTransition)
                vertex2.setmEPNTransition();
            if (secondIsmEPNComponent)
                vertex2.setmEPNComponent();

            nc.verticesMap.put(second, vertex2);
            allNodesmEPNShapeGroupTuplesMap.put(vertex2, node2mEPNShapeGroupsTuple4);
        }

        if ( !vertex1.getEdgeConnectionsMap().containsKey(vertex2) )
        {
            float weight = (isTotalInhibitorEdge) ? 1.0f : (isPartialInhibitorEdge) ? 0.5f : 0.0f;
            Edge edge = new Edge(edgeName, vertex1, vertex2, weight, isTotalInhibitorEdge, isPartialInhibitorEdge, hasDualArrowHead);
            vertex1.addConnection(vertex2, edge);
            vertex2.addConnection(vertex1, edge);

            nc.edges.add(edge);
        }
    }

    /**
    *  Sets the isGraphml variable.
    */
    public void setIsGraphml(boolean isGraphml)
    {
        this.isGraphml = isGraphml;
    }

    /**
    *  Gets the isGraphml variable value.
    *  Package access so as to ensure nc side control.
    */
    boolean getIsGraphml()
    {
        return isGraphml;
    }

    /**
    *  Sets the isPetriNet variable.
    */
    public void setIsPetriNet(boolean isPetriNet)
    {
        this.isPetriNet = isPetriNet;
    }

    /**
    *  Gets the hasStandardPetriNetTransitions variable value.
    *  Package access so as to ensure nc side control.
    */
    boolean getIsPetriNet()
    {
        return isPetriNet;
    }

    /**
    *  Sets the isPetriNet variable.
    */
    public void setHasStandardPetriNetTransitions(boolean hasStandardPetriNetTransitions)
    {
        this.hasStandardPetriNetTransitions = hasStandardPetriNetTransitions;
    }

    /**
    *  Gets the hasStandardPetriNetTransitions variable value.
    *  Package access so as to ensure nc side control.
    */
    boolean getHasStandardPetriNetTransitions()
    {
        return hasStandardPetriNetTransitions;
    }

    /**
    *  Initializes the allGraphmlNodesMap variable.
    */
    public void initAllGraphmlNodesMap(HashMap<String, Tuple6<float[], String[], String[], String[], String[], String>> allGraphmlNodesMap,
                                       HashMap<String, Tuple6<String, Tuple2<float[], ArrayList<Point2D.Float>>, String[], String[], String[], String[]>> allGraphmlEdgesMap,
                                       ArrayList<GraphmlComponentContainer> allPathwayComponentContainers)
    {
        this.allGraphmlNodesMap = allGraphmlNodesMap;
        this.allGraphmlEdgesMap = allGraphmlEdgesMap;
        this.allPathwayComponentContainersFor3D = allPathwayComponentContainers;
        this.allPathwayComponentContainersFor2D = new ArrayList<GraphmlComponentContainer>(allPathwayComponentContainers);
        Collections.reverse(allPathwayComponentContainersFor2D);

        allOrigGraphmlMapCoordsMap = new HashMap<String, float[]>();
        float[] tempCoords = null;
        for ( String key : allGraphmlNodesMap.keySet() )
        {
            tempCoords = allGraphmlNodesMap.get(key).first;
            allOrigGraphmlMapCoordsMap.put(key, new float[]{ tempCoords[0], tempCoords[1], tempCoords[2], tempCoords[3] } );
        }
    }

    /**
    *  Parses the mEPN Notation Class Set and Classes.
    */
    public void parsemEPNClassSetAndClasses()
    {
        LayoutClasses lc = layoutClassSetsManager.getClassSet(MEPN_NOTATION_CLASS_SET_NAME);
        layoutClassSetsManager.switchClassSet(MEPN_NOTATION_CLASS_SET_NAME);
        VertexClass vc = null;
        Tuple4<GraphmlShapesGroup1, GraphmlShapesGroup2, GraphmlShapesGroup3, Color> nodemEPNShapeGroupsTuple4 = null;
        String className = "";
        for ( Vertex vertex : allNodesmEPNShapeGroupTuplesMap.keySet() )
        {
            nodemEPNShapeGroupsTuple4 = allNodesmEPNShapeGroupTuplesMap.get(vertex);
            if ( !nodemEPNShapeGroupsTuple4.first.equals(GraphmlShapesGroup1.NONE) && nodemEPNShapeGroupsTuple4.second.equals(GraphmlShapesGroup2.NONE) && nodemEPNShapeGroupsTuple4.third.equals(GraphmlShapesGroup3.NONE) )
            {
                className = provideClassNameFromEnumeration( nodemEPNShapeGroupsTuple4.first.toString() );
                vc = lc.createClass(className);
                lc.setClass(vertex, vc);
                // the class color will be the color set for this type of vertex
                vc.setColor(nodemEPNShapeGroupsTuple4.fourth);
            }
            else if ( nodemEPNShapeGroupsTuple4.first.equals(GraphmlShapesGroup1.NONE) && !nodemEPNShapeGroupsTuple4.second.equals(GraphmlShapesGroup2.NONE) && nodemEPNShapeGroupsTuple4.third.equals(GraphmlShapesGroup3.NONE) )
            {
                className = provideClassNameFromEnumeration( nodemEPNShapeGroupsTuple4.second.toString() );
                vc = lc.createClass(className);
                lc.setClass(vertex, vc);
                // the class color will be the color set for this type of vertex
                vc.setColor(nodemEPNShapeGroupsTuple4.fourth);
            }
            else if ( nodemEPNShapeGroupsTuple4.first.equals(GraphmlShapesGroup1.NONE) && nodemEPNShapeGroupsTuple4.second.equals(GraphmlShapesGroup2.NONE) && !nodemEPNShapeGroupsTuple4.third.equals(GraphmlShapesGroup3.NONE) )
            {
                className = provideClassNameFromEnumeration( nodemEPNShapeGroupsTuple4.third.toString() );
                vc = lc.createClass(className);
                lc.setClass(vertex, vc);
                // the class color will be the color set for this type of vertex
                vc.setColor(nodemEPNShapeGroupsTuple4.fourth);
            }
            else
            {
                if (DEBUG_BUILD) println("No mEPN notation was recorded for this vertex.");
            }
        }

        allNodesmEPNShapeGroupTuplesMap.clear();
        allNodesmEPNShapeGroupTuplesMap = null;
    }

    /**
    *  Provides a Class name from the given enumeration.
    */
    private String provideClassNameFromEnumeration(String enumName)
    {
        String enumNameWithNoUnderscore = enumName.toLowerCase().replace("_", " ");

        String[] allPartsOfName = enumNameWithNoUnderscore.split("\\s+");
        StringBuilder nameToReturn = new StringBuilder();
        for (int i = 0; i < allPartsOfName.length; i++)
        {
            nameToReturn.append( Character.toUpperCase( allPartsOfName[i].charAt(0) ) ).append( allPartsOfName[i].substring(1) );
            if ( i != (allPartsOfName.length - 1) )
                nameToReturn.append(" ");
        }

        return nameToReturn.toString();
    }

    /**
    *  Gets the allGraphmlNodesMap variable.
    */
    public HashMap<String, Tuple6<float[], String[], String[], String[], String[], String>> getAllGraphmlNodesMap()
    {
        return allGraphmlNodesMap;
    }

    /**
    *  Gets the allGraphmlEdgesMap variable.
    */
    public HashMap<String, Tuple6<String, Tuple2<float[], ArrayList<Point2D.Float>>, String[], String[], String[], String[]>> getAllGraphmlEdgesMap()
    {
        return allGraphmlEdgesMap;
    }

    /**
    *  Gets the allPathwayComponentContainersFor3D variable.
    */
    public ArrayList<GraphmlComponentContainer> getAllPathwayComponentContainersFor3D()
    {
        return allPathwayComponentContainersFor3D;
    }

    /**
    *  Gets the allPathwayComponentContainersFor2D variable.
    */
    public ArrayList<GraphmlComponentContainer> getAllPathwayComponentContainersFor2D()
    {
        return allPathwayComponentContainersFor2D;
    }

    /**
    *  Resets all the graphml nodes map coordinates.
    */
    public void resetAllGraphmlNodesMapCoords()
    {
        float[] tempCoords = null;
        float[] allGraphmlNodesMapCoords = null;
        for ( String key : allGraphmlNodesMap.keySet() )
        {
            tempCoords = allOrigGraphmlMapCoordsMap.get(key);
            allGraphmlNodesMapCoords = allGraphmlNodesMap.get(key).first;

            allGraphmlNodesMapCoords[0] = tempCoords[0];
            allGraphmlNodesMapCoords[1] = tempCoords[1];
            allGraphmlNodesMapCoords[2] = tempCoords[2];
            allGraphmlNodesMapCoords[3] = tempCoords[3];
        }

        allUndoGraphmlMapCoordsStacksMap = null;
        allUndoGraphmlMapCoordsStacksMap = null;
    }

    /**
    *  Resets all the graphml nodes map coordinate depths (Z coordinates).
    */
    public void resetAllGraphmlNodesMapCoordsDepthZ()
    {
        for ( String key : allGraphmlNodesMap.keySet() )
            allGraphmlNodesMap.get(key).first[4] = 0.0f;
    }

    /**
    *  Gets the node name from the node.
    *  Package access so as to ensure nc side control.
    */
    String getNodeName(String nodeName)
    {
        String[] nodeNameArray = allGraphmlNodesMap.get(nodeName).fifth;
        return (nodeNameArray != null) ? nodeNameArray[0] : nodeName;
    }

    /**
    *  Sets the node name for the node.
    *  Package access so as to ensure nc side control.
    */
    void setNodeName(GraphNode node, String newNodeName)
    {
        // for non-group nodes appearing on the graph, having a nodeID > 0, that does not associate to the graphml names hashmap
        String[] nodeNameArray = nc.getGraphmlNetworkContainer().getAllGraphmlNodesMap().get( node.getNodeName() ).fifth;
        if (nodeNameArray != null)
            nodeNameArray[0] = newNodeName;
        else
            node.setNodeName(newNodeName);
    }

    /**
    *  Checks if the Undo stack is empty.
    */
    public boolean isEmptyUndoPointStack(GraphNode node)
    {
        if (allUndoGraphmlMapCoordsStacksMap == null)
            return true;
        else
        {
            org.BioLayoutExpress3D.DataStructures.Stack<float[]> undoGraphmlMapCoordsStack = allUndoGraphmlMapCoordsStacksMap.get( node.getNodeName() );
            return ( (undoGraphmlMapCoordsStack == null) || undoGraphmlMapCoordsStack.isEmpty() );
        }
    }

    /**
    *  Checks if the Redo stack is empty.
    */
    public boolean isEmptyRedoPointStack(GraphNode node)
    {
        if (allRedoGraphmlMapCoordsStacksMap == null)
            return true;
        else
        {
            org.BioLayoutExpress3D.DataStructures.Stack<float[]> redoGraphmlMapCoordsStack = allRedoGraphmlMapCoordsStacksMap.get( node.getNodeName() );
            return ( (redoGraphmlMapCoordsStack == null) || allRedoGraphmlMapCoordsStacksMap.get( node.getNodeName() ).isEmpty() );
        }
    }

    /**
    *  Pushes the given node location in the Undo stack.
    */
    public void pushLocationInUndoGraphmlMapCoordsStack(GraphNode node)
    {
        if (allUndoGraphmlMapCoordsStacksMap == null)
            allUndoGraphmlMapCoordsStacksMap = new HashMap<String, org.BioLayoutExpress3D.DataStructures.Stack<float[]>>();
        org.BioLayoutExpress3D.DataStructures.Stack<float[]> undoGraphmlMapCoordsStack = allUndoGraphmlMapCoordsStacksMap.get( node.getNodeName() );

        if (undoGraphmlMapCoordsStack == null)
        {
            undoGraphmlMapCoordsStack = new org.BioLayoutExpress3D.DataStructures.Stack<float[]>(USE_LINKEDLIST);
            allUndoGraphmlMapCoordsStacksMap.put(node.getNodeName(), undoGraphmlMapCoordsStack);
        }
        float[] currentNodeGraphmlMapCoord = allGraphmlNodesMap.get( node.getNodeName() ).first;
        undoGraphmlMapCoordsStack.push( new float[] { currentNodeGraphmlMapCoord[2], currentNodeGraphmlMapCoord[3] } );
    }

    /**
    *  Pushes the given node location in the Redo stack.
    */
    public void pushLocationInRedoGraphmlMapCoordsStack(GraphNode node)
    {
        if (allRedoGraphmlMapCoordsStacksMap == null)
            allRedoGraphmlMapCoordsStacksMap = new HashMap<String, org.BioLayoutExpress3D.DataStructures.Stack<float[]>>();
        org.BioLayoutExpress3D.DataStructures.Stack<float[]> redoGraphmlMapCoordsStack = allRedoGraphmlMapCoordsStacksMap.get( node.getNodeName() );

        if (redoGraphmlMapCoordsStack == null)
        {
            redoGraphmlMapCoordsStack = new org.BioLayoutExpress3D.DataStructures.Stack<float[]>(USE_LINKEDLIST);
            allRedoGraphmlMapCoordsStacksMap.put(node.getNodeName(), redoGraphmlMapCoordsStack);
        }
        float[] currentNodeGraphmlMapCoord = allGraphmlNodesMap.get( node.getNodeName() ).first;
        redoGraphmlMapCoordsStack.push( new float[] { currentNodeGraphmlMapCoord[2], currentNodeGraphmlMapCoord[3] } );
    }

    /**
    *  Pops the given node location from the Undo stack.
    */
    public void popLocationFromUndoGraphmlMapCoordsStack(GraphNode node)
    {
        org.BioLayoutExpress3D.DataStructures.Stack<float[]> undoGraphmlMapCoordsStack = allUndoGraphmlMapCoordsStacksMap.get( node.getNodeName() );
        if ( !isEmptyUndoPointStack(node) )
        {
            float[] nodeGraphmlMapCoord = undoGraphmlMapCoordsStack.pop();
            float[] currentNodeGraphmlMapCoord = allGraphmlNodesMap.get( node.getNodeName() ).first;
            currentNodeGraphmlMapCoord[2] = nodeGraphmlMapCoord[0];
            currentNodeGraphmlMapCoord[3] = nodeGraphmlMapCoord[1];
        }
    }

    /**
    *  Pops the given node location from the Redo stack.
    */
    public void popLocationFromRedoGraphmlMapCoordsStack(GraphNode node)
    {
        org.BioLayoutExpress3D.DataStructures.Stack<float[]> redoGraphmlMapCoordsStack = allRedoGraphmlMapCoordsStacksMap.get( node.getNodeName() );
        if ( !isEmptyRedoPointStack(node) )
        {
            float[] nodeGraphmlMapCoord = redoGraphmlMapCoordsStack.pop();
            float[] currentNodeGraphmlMapCoord = allGraphmlNodesMap.get( node.getNodeName() ).first;
            currentNodeGraphmlMapCoord[2] = nodeGraphmlMapCoord[0];
            currentNodeGraphmlMapCoord[3] = nodeGraphmlMapCoord[1];
        }
    }

    /**
    *  Sets the rangeX variable.
    */
    public void setRangeX(float rangeX)
    {
        this.rangeX = rangeX;
    }

    /**
    *  Sets the rangeY variable.
    */
    public void setRangeY(float rangeY)
    {
        this.rangeY = rangeY;
    }

    /**
    *  Gets the rangeX variable.
    */
    public float getRangeX()
    {
        return rangeX;
    }

    /**
    *  Gets the rangeY variable.
    */
    public float getRangeY()
    {
        return rangeY;
    }

    /**
    *  Clears all data structures.
    */
    public void clear()
    {
        isGraphml = false;
        isPetriNet = false;
        allGraphmlNodesMap.clear();
        allGraphmlNodesMap = null;
        allGraphmlEdgesMap.clear();
        allGraphmlEdgesMap = null;
        allPathwayComponentContainersFor3D.clear();
        allPathwayComponentContainersFor3D = null;
        allPathwayComponentContainersFor2D.clear();
        allPathwayComponentContainersFor2D = null;
        allOrigGraphmlMapCoordsMap.clear();
        allOrigGraphmlMapCoordsMap = null;
        if (allUndoGraphmlMapCoordsStacksMap != null) allUndoGraphmlMapCoordsStacksMap.clear();
        allUndoGraphmlMapCoordsStacksMap = null;
        if (allRedoGraphmlMapCoordsStacksMap != null) allRedoGraphmlMapCoordsStacksMap.clear();
        allRedoGraphmlMapCoordsStacksMap = null;
        rangeX = 0.0f;
        rangeY = 0.0f;
    }


}