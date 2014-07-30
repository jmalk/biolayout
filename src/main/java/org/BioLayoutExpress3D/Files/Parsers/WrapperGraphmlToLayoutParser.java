package org.BioLayoutExpress3D.Files.Parsers;

import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import static java.lang.Math.*;
import org.BioLayoutExpress3D.DataStructures.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.CoreUI.Dialogs.*;
import org.BioLayoutExpress3D.Network.*;
import static org.BioLayoutExpress3D.Network.GraphmlLookUpmEPNTables.*;
import static org.BioLayoutExpress3D.Network.NetworkContainer.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.Shapes2D.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.Shapes3D.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* WrapperGraphmlToLayoutParser is the wrapper parser class used to connect the GraphML parsed xml data files with the main BioLayout Express 3D application.
*
* @see org.BioLayoutExpress3D.Files.Parsers.GraphmlParser
* @see org.BioLayoutExpress3D.DataStructures.Tuple6
* @author Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*/

public final class WrapperGraphmlToLayoutParser extends CoreParser implements GraphmlParser.GraphmlParserListener
{

    /**
    *  Constant value for defining the minimum range allowed.
    */
    private static final float MINIMUM_RANGE = 300.0f;

    /**
    *  Constant value for defining the component container regex.
    */
    private static final String COMPONENT_CONTAINER_REGEX = "*";

    /**
    *  GraphmlParser reference (to be used for graphml xml file parsing).
    */
    private GraphmlParser graphmlParser = null;

    /**
    *  String variable to store the xml file name.
    */
    private String fileName = "";

    /**
    *  Boolean variable to be used for validating the XML file.
    */
    private boolean validateXMLFile = false;

    /**
    *  LayoutProgressBarDialog reference (reference for the loading dialog).
    */
    private LayoutProgressBarDialog layoutProgressBarDialog = null;

    /**
    *  GraphmlNetworkContainer reference (reference for the grapml network container).
    */
    private GraphmlNetworkContainer gnc = null;

    /**
    *  The WrapperGraphmlToLayoutParser class constructor for graphml xml files (first constructor).
    */
    public WrapperGraphmlToLayoutParser(NetworkContainer nc, LayoutFrame layoutFrame, GraphmlParser graphmlParser, boolean validateXMLFile)
    {
        super(nc, layoutFrame);

        this.graphmlParser = graphmlParser;
        this.validateXMLFile = validateXMLFile;
    }

    /**
    *  Checks if the file actually exists.
    */
    @Override
    public boolean init(File file, String fileExtension)
    {
        try
        {
            fileReaderBuffered = new BufferedReader( new FileReader(file) );
            fileReaderBuffered.close();

            // use absolute path here so as to be able to import files from different (than current) directories, useful for drag-n-drop
            fileName = file.getAbsolutePath();
            simpleFileName = file.getName();

            return true;
        }
        catch (Exception exc)
        {
            try
            {
                fileReaderBuffered.close();
            }
            catch (IOException ioe)
            {
                if (DEBUG_BUILD) println("IOException while closing streamers in WrapperGraphmlToLayoutParser.init():\n" + ioe.getMessage());
            }
            finally
            {

            }

            return false;
        }
    }

    /**
    *  Parses the graphml file.
    */
    @Override
    public boolean parse()
    {
        isSuccessful = false;
        nc.setOptimized(false);
        layoutProgressBarDialog = layoutFrame.getLayoutProgressBar();

        if (graphmlParser != null)
        {
            graphmlParser.setXmlParserName(fileName);
            graphmlParser.setListener(this);

            isSuccessful = graphmlParser.parseFromFile(fileName, validateXMLFile);

            convertToLayoutFormat();

            graphmlParser.removeListener();
        }

        layoutProgressBarDialog.endProgressBar();
        layoutProgressBarDialog.stopProgressBar();

        return isSuccessful;
    }

    /**
    *  GraphmlParser callback to inform about the graphml details parsed.
    */
    @Override
    public void graphmlDetailsParsed()
    {
        int numberOfTotalLines = graphmlParser.getGraphmlParsedNodes() + graphmlParser.getGraphmlParsedEdges();
        layoutProgressBarDialog.prepareProgressBar(numberOfTotalLines, "Parsing " + simpleFileName + " GraphML Pathway...");
        layoutProgressBarDialog.startProgressBar();
    }

    /**
    *  GraphmlParser callback to inform about the graphml node or edge parsed.
    */
    @Override
    public void nodeOrEdgeParsed()
    {
        layoutProgressBarDialog.incrementProgress();
    }

    private Color parseColor(String s)
    {
        if (s == null || s.length() == 0)
        {
            return DEFAULT_NODE_COLOR;
        }

        if (s.length() > 7)
        {
            // Assume this is an RGBA string; strip off the A
            s = s.substring(0, s.length() - 2);
        }

        return Color.decode(s);
    }

    /**
    *  Converts the xml parsed information to layout compatible format.
    */
    private void convertToLayoutFormat()
    {
        if (graphmlParser != null)
        {
            HashMap<String, Tuple6<float[], String[], String[], String[], String[], String>> allNodesMap = graphmlParser.getAllNodesMap();
            HashMap<String, Tuple6<String, Tuple2<float[], ArrayList<Point2D.Float>>, String[], String[], String[], String[]>> allEdgesMap = graphmlParser.getAllEdgesMap();

            nc.initGraphmlNetworkContainer();
            gnc = nc.getGraphmlNetworkContainer();
            gnc.initAllNodesmEPNShapeGroupTuplesMap();

            Tuple6<String, Tuple2<float[], ArrayList<Point2D.Float>>, String[], String[], String[], String[]> edgeTuple6 = null;
            String edgeName = "";
            boolean isTotalInhibitorEdge = false;
            boolean isPartialInhibitorEdge = false;
            boolean hasDualArrowHead = false;
            boolean hasNoArrowHead = false;
            String[] nodeData = null;
            Tuple6<float[], String[], String[], String[], String[], String> node1Tuple6 = null;
            Tuple6<float[], String[], String[], String[], String[], String> node2Tuple6 = null;
            String node1Name = "";
            String node2Name = "";
            float node1Width = 0.0f;
            float node1Height = 0.0f;
            float node2Width = 0.0f;
            float node2Height = 0.0f;
            Color node1Color1 = null;
            Color node1Color2 = null;
            Color node2Color1 = null;
            Color node2Color2 = null;
            String node1Shape = "";
            String node2Shape = "";
            boolean ismEPNTransition = false;
            boolean hasStandardPetriNetTransitions = false;

            Tuple5<Shapes2D, Shapes3D, Float, Tuple3<Boolean, Boolean, Boolean>, Tuple4<GraphmlShapesGroup1, GraphmlShapesGroup2, GraphmlShapesGroup3, Color>> lookUpNode1Tuple5 = null;
            Tuple5<Shapes2D, Shapes3D, Float, Tuple3<Boolean, Boolean, Boolean>, Tuple4<GraphmlShapesGroup1, GraphmlShapesGroup2, GraphmlShapesGroup3, Color>> lookUpNode2Tuple5 = null;

            for ( String edgeKey : allEdgesMap.keySet() )
            {
                edgeTuple6 = allEdgesMap.get(edgeKey);

                edgeName = edgeTuple6.fifth[17];
                isTotalInhibitorEdge = edgeTuple6.fourth[1].equals(GRAPHML_PETRI_NET_INHIBITOR_ARROWHEAD_LOOK_UP_TABLE[0]) || edgeTuple6.fourth[1].equals(GRAPHML_PETRI_NET_INHIBITOR_ARROWHEAD_LOOK_UP_TABLE[1]);
                isPartialInhibitorEdge = edgeTuple6.fourth[1].equals(GRAPHML_PETRI_NET_INHIBITOR_ARROWHEAD_LOOK_UP_TABLE[2]);
                hasDualArrowHead = !edgeTuple6.fourth[0].equals(GRAPHML_PETRI_NET_INHIBITOR_ARROWHEAD_LOOK_UP_TABLE[3]) && !edgeTuple6.fourth[1].equals(GRAPHML_PETRI_NET_INHIBITOR_ARROWHEAD_LOOK_UP_TABLE[3]);

                nodeData = edgeKey.split("\\s+");
                node1Tuple6 = allNodesMap.get(nodeData[0]);
                node2Tuple6 = allNodesMap.get(nodeData[1]);

                node1Name = node1Tuple6.fifth[0];
                node2Name = node2Tuple6.fifth[0];

                if ( node1Name.startsWith(COMPONENT_CONTAINER_REGEX) && node1Name.endsWith(COMPONENT_CONTAINER_REGEX) ) // skip Component Containers to not become nodes in the network
                    continue;
                if ( node2Name.startsWith(COMPONENT_CONTAINER_REGEX) && node2Name.endsWith(COMPONENT_CONTAINER_REGEX) ) // skip Component Containers to not become nodes in the network
                    continue;

                node1Width = node1Tuple6.first[1];  // reverse index because graphml file & parser has it in height/width format instead of width/height!
                node1Height = node1Tuple6.first[0]; // reverse index because graphml file & parser has it in height/width format instead of width/height!

                node2Width = node2Tuple6.first[1];  // reverse index because graphml file & parser has it in height/width format instead of width/height!
                node2Height = node2Tuple6.first[0]; // reverse index because graphml file & parser has it in height/width format instead of width/height!

                node1Color1 = parseColor(node1Tuple6.second[1]);
                node1Color2 = parseColor(node1Tuple6.second[2]);

                node2Color1 = parseColor(node2Tuple6.second[1]);
                node2Color2 = parseColor(node2Tuple6.second[2]);

                node1Shape = node1Tuple6.sixth;
                node2Shape = node2Tuple6.sixth;

                if (DEBUG_BUILD) println("\nedgeKey: '" + edgeKey + "'" + " edgeName: " + edgeName + " isTotalInhibitorEdge: " + isTotalInhibitorEdge + " isPartialInhibitorEdge: " + isPartialInhibitorEdge + " hasDualArrowHead: " + hasDualArrowHead + " hasNoArrowHead: " + hasNoArrowHead +
                                         "\nnode1Name: " + node1Name + " node1Width: " + node1Width + " node1Height: " + node1Height + " node1Shape: " + node1Shape + ( (node1Color1 != null) ? ( " \nColor1: " + node1Color1.toString() ) : "" ) + ( (node1Color2 != null) ? ( " Color2: " + node1Color2.toString() ) : "" ) +
                                         "\nnode2Name: " + node2Name + " node2Width: " + node2Width + " node2Height: " + node2Height + " node2Shape: " + node2Shape + ( (node2Color1 != null) ? ( " \nColor1: " + node2Color1.toString() ) : "" ) + ( (node2Color2 != null) ? ( " Color2: " + node2Color2.toString() ) : "" ) );

                lookUpNode1Tuple5 = graphmlShapeLookUpForNode(node1Name, node1Width, node1Height, node1Shape, node1Color1);
                lookUpNode2Tuple5 = graphmlShapeLookUpForNode(node2Name, node2Width, node2Height, node2Shape, node2Color1);

                if (!ismEPNTransition)
                    ismEPNTransition = (lookUpNode1Tuple5.fourth.first || lookUpNode2Tuple5.fourth.first);

                if (!hasStandardPetriNetTransitions)
                    hasStandardPetriNetTransitions = (lookUpNode1Tuple5.fourth.third || lookUpNode2Tuple5.fourth.third);

                gnc.addNetworkConnectionForGraphml(nodeData[0], lookUpNode1Tuple5.first, lookUpNode1Tuple5.second, lookUpNode1Tuple5.third, lookUpNode1Tuple5.fourth.first, lookUpNode1Tuple5.fourth.second, lookUpNode1Tuple5.fifth,
                                                   nodeData[1], lookUpNode2Tuple5.first, lookUpNode2Tuple5.second, lookUpNode2Tuple5.third, lookUpNode2Tuple5.fourth.first, lookUpNode2Tuple5.fourth.second, lookUpNode2Tuple5.fifth,
                                                   edgeName, isTotalInhibitorEdge, isPartialInhibitorEdge, hasDualArrowHead);
            }

            transformYEdStyleRenderingOfGraphmlFiles(allNodesMap, allEdgesMap);

            gnc.setIsGraphml(true);
            gnc.setIsPetriNet(ismEPNTransition);
            gnc.setHasStandardPetriNetTransitions(hasStandardPetriNetTransitions);
            gnc.initAllGraphmlNodesMap( allNodesMap, allEdgesMap, createComponentContainers(allNodesMap) );
            gnc.parsemEPNClassSetAndClasses();
        }
    }

    /**
    *  Look up shapes method for graphml shapes for the given node.
    */
    private Tuple5<Shapes2D, Shapes3D, Float, Tuple3<Boolean, Boolean, Boolean>, Tuple4<GraphmlShapesGroup1, GraphmlShapesGroup2, GraphmlShapesGroup3, Color>>
            graphmlShapeLookUpForNode(String nodeName, float nodeWidth, float nodeHeight, String nodeShape, Color nodeColor1)
    {
        Tuple6<GraphmlShapesGroup1, Color, Float, Shapes2D, Shapes3D, Boolean> nodeGroup1Tuple6 = null;
        Tuple6<GraphmlShapesGroup2, Color, Float, Shapes2D, Shapes3D, Boolean> nodeGroup2Tuple6 = null;
        Tuple6<GraphmlShapesGroup3, Color, Float, Shapes2D, Shapes3D, Boolean> nodeGroup3Tuple6 = null;
        Color nodeColorToReturn = nodeColor1;
        Shapes2D nodeShape2DToReturn = CIRCLE;
        Shapes3D nodeShape3DToReturn = SPHERE;
        float nodeShapeSizeToReturn = 0.0f;
        boolean ismEPNTransition = false;
        boolean ismEPNComponent = false;
        boolean hasStandardPetriNetTransitions = false;

        nodeGroup1Tuple6 = graphmlShapeLookUpGroup1(nodeName, nodeShape);

        if ( nodeGroup1Tuple6.first.equals(GraphmlShapesGroup1.NONE) )
        {
            nodeGroup2Tuple6 = graphmlShapeLookUpGroup2(nodeWidth, nodeHeight, nodeShape, nodeColor1);

            if ( nodeGroup2Tuple6.first.equals(GraphmlShapesGroup2.NONE) )
            {
                nodeGroup3Tuple6 = graphmlShapeLookUpGroup3(nodeName, nodeShape);

                if ( nodeGroup3Tuple6.first.equals(GraphmlShapesGroup3.NONE) )
                {
                    // no node found, default values to be returned
                }
                else // GraphmlShapesGroup3 node found
                {
                    nodeColorToReturn   = nodeGroup3Tuple6.second;
                    nodeShapeSizeToReturn = nodeGroup3Tuple6.third;
                    nodeShape2DToReturn   = nodeGroup3Tuple6.fourth;
                    nodeShape3DToReturn   = nodeGroup3Tuple6.fifth;

                    ismEPNComponent = nodeGroup3Tuple6.sixth;
                }
            }
            else // GraphmlShapesGroup2 node found
            {
                nodeColorToReturn   = nodeGroup2Tuple6.second;
                nodeShapeSizeToReturn = nodeGroup2Tuple6.third;
                nodeShape2DToReturn = nodeGroup2Tuple6.fourth;
                nodeShape3DToReturn = nodeGroup2Tuple6.fifth;

                hasStandardPetriNetTransitions = nodeGroup2Tuple6.sixth;
            }
        }
        else // GraphmlShapesGroup1 node found
        {
            nodeColorToReturn     = nodeGroup1Tuple6.second;
            nodeShapeSizeToReturn = nodeGroup1Tuple6.third;
            nodeShape2DToReturn   = nodeGroup1Tuple6.fourth;
            nodeShape3DToReturn   = nodeGroup1Tuple6.fifth;

            ismEPNTransition = nodeGroup1Tuple6.sixth;
        }

        return Tuples.tuple( nodeShape2DToReturn, nodeShape3DToReturn, nodeShapeSizeToReturn,
                             Tuples.tuple(ismEPNTransition || hasStandardPetriNetTransitions, ismEPNComponent, hasStandardPetriNetTransitions),
                             Tuples.tuple( (nodeGroup1Tuple6 != null) ? nodeGroup1Tuple6.first : GraphmlShapesGroup1.NONE,
                                           (nodeGroup2Tuple6 != null) ? nodeGroup2Tuple6.first : GraphmlShapesGroup2.NONE,
                                           (nodeGroup3Tuple6 != null) ? nodeGroup3Tuple6.first : GraphmlShapesGroup3.NONE,
                                            nodeColorToReturn) );
    }

    /**
    *  Look up shapes method for graphml shapes group 1 for name & shape, also returning color.
    */
    private Tuple6<GraphmlShapesGroup1, Color, Float, Shapes2D, Shapes3D, Boolean> graphmlShapeLookUpGroup1(String nodeName, String nodeShape)
    {
        int numberOfShapes = GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_1.length;
        GraphmlShapesGroup1 currentGraphmlShape = GraphmlShapesGroup1.NONE;
        int shapeIndex = 0;
        for (int i = 0; i < numberOfShapes; i++)
        {
            if ( nodeName.equals( (String)GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_1[i].first ) && nodeShape.equals( (String)GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_1[i].second ) )
            {
                currentGraphmlShape = (GraphmlShapesGroup1)GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_1[i].third;
                shapeIndex = currentGraphmlShape.ordinal();
                return Tuples.tuple(currentGraphmlShape,                                              // return type of graphml shape
                                       (Color)GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_1[shapeIndex].fourth,  // return graphml color
                                       (Float)GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_1[shapeIndex].fifth,   // return graphml shape size
                                    (Shapes2D)GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_1[shapeIndex].sixth,   // return graphml 2D shape
                                    (Shapes3D)GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_1[shapeIndex].seventh, // return graphml 3D shape
                                    checkForTransition(currentGraphmlShape) );                        // return mEPN Transition
            }
        }

        return Tuples.tuple(GraphmlShapesGroup1.NONE, Color.BLACK, 0.0f, CIRCLE, SPHERE, false);
    }

    /**
    *  Checks if the given GraphmlShapesGroup1 shape should be marked as a transition.
    */
    private boolean checkForTransition(GraphmlShapesGroup1 currentGraphmlShape)
    {
        for (int i = 0; i < GRAPHML_SHAPES_TO_TRANSITIONS.length; i++)
            if ( currentGraphmlShape.equals(GRAPHML_SHAPES_TO_TRANSITIONS[i]) )
                return true;

        return false;
    }

    /**
    *  Look up shapes method for graphml shapes group 2 for shape & color, also returning color.
    */
    private Tuple6<GraphmlShapesGroup2, Color, Float, Shapes2D, Shapes3D, Boolean> graphmlShapeLookUpGroup2(float nodeWidth, float nodeHeight, String nodeShape, Color nodeColor1)
    {
        int numberOfShapes = GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_2.length;
        GraphmlShapesGroup2 currentGraphmlShape = GraphmlShapesGroup2.NONE;
        boolean ismEPNTransition = false;
        int shapeIndex = 0;
        for (int i = 0; i < numberOfShapes; i++)
        {
            if ( nodeShape.equals( (String)GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_2[i].first ) && ( (nodeColor1 != null) && nodeColor1.equals( (Color)GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_2[i].third ) ) ) // because of possible errors in glyph color definitions
            {
                currentGraphmlShape = (GraphmlShapesGroup2)GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_2[i].second;
                if (currentGraphmlShape.equals(GraphmlShapesGroup2.TRANSITION_VERTICAL) || currentGraphmlShape.equals(GraphmlShapesGroup2.TRANSITION_HORIZONTAL))
                {
                    ismEPNTransition = true;
                    currentGraphmlShape = (nodeWidth < nodeHeight) ? GraphmlShapesGroup2.TRANSITION_VERTICAL : GraphmlShapesGroup2.TRANSITION_HORIZONTAL;
                }
                else if (currentGraphmlShape.equals(GraphmlShapesGroup2.TRANSITION_DIAMOND) ||
                        currentGraphmlShape.equals(GraphmlShapesGroup2.PATHWAY_MODULE) ||
                        currentGraphmlShape.equals(GraphmlShapesGroup2.PATHWAY_OUTPUT))
                {
                    ismEPNTransition = true;
                }

                shapeIndex = currentGraphmlShape.ordinal();
                return Tuples.tuple(currentGraphmlShape,                                             // return type of graphml shape
                                       (Color)GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_2[shapeIndex].third,  // return graphml color
                                       (Float)GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_2[shapeIndex].fourth, // return graphml shape size
                                    (Shapes2D)GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_2[shapeIndex].fifth,  // return graphml 2D shape
                                    (Shapes3D)GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_2[shapeIndex].sixth,  // return graphml 3D shape
                                    ismEPNTransition);
            }
        }

        return Tuples.tuple(GraphmlShapesGroup2.NONE, Color.BLACK, 0.0f, CIRCLE, SPHERE, false);
    }

    /**
    *  Look up shapes method for graphml shapes group 3 for shape only, also returning color.
    */
    private Tuple6<GraphmlShapesGroup3, Color, Float, Shapes2D, Shapes3D, Boolean> graphmlShapeLookUpGroup3(String nodeName, String nodeShape)
    {
        int numberOfShapes = GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_3.length;
        GraphmlShapesGroup3 currentGraphmlShape = GraphmlShapesGroup3.NONE;
        boolean ismEPNComponent = false;
        int shapeIndex = 0;
        for (int i = 0; i < numberOfShapes; i++)
        {
            if ( nodeShape.equals( (String)GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_3[i].first ) )
            {
                currentGraphmlShape = (GraphmlShapesGroup3)GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_3[i].second;
                if ( currentGraphmlShape.equals(GraphmlShapesGroup3.PROTEIN_COMPLEX) && !nodeName.contains(":") )
                    currentGraphmlShape = GraphmlShapesGroup3.PROTEIN_PEPTIDE;
                if ( currentGraphmlShape.equals(GraphmlShapesGroup3.PROTEIN_COMPLEX)    || currentGraphmlShape.equals(GraphmlShapesGroup3.PROTEIN_PEPTIDE) ||
                     currentGraphmlShape.equals(GraphmlShapesGroup3.GENE)               || currentGraphmlShape.equals(GraphmlShapesGroup3.DNA_SEQUENCE)    ||
                     currentGraphmlShape.equals(GraphmlShapesGroup3.SIMPLE_BIOCHEMICAL) || currentGraphmlShape.equals(GraphmlShapesGroup3.GENERIC_ENTITY)  ||
                     currentGraphmlShape.equals(GraphmlShapesGroup3.DRUG)               || currentGraphmlShape.equals(GraphmlShapesGroup3.ION_SIMPLE_MOLECULE) )
                    ismEPNComponent = true;

                shapeIndex = currentGraphmlShape.ordinal();
                return Tuples.tuple(currentGraphmlShape,                                             // return type of graphml shape
                                       (Color)GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_3[shapeIndex].third,  // return graphml color
                                       (Float)GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_3[shapeIndex].fourth, // return graphml shape size
                                    (Shapes2D)GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_3[shapeIndex].fifth,  // return graphml 2D shape
                                    (Shapes3D)GRAPHML_MEPN_SHAPES_LOOKUP_TABLE_3[shapeIndex].sixth,  // return graphml 3D shape
                                    ismEPNComponent);                                                // return mEPN Component
            }
        }

        return Tuples.tuple(GraphmlShapesGroup3.NONE, Color.BLACK, 0.0f, CIRCLE, SPHERE, false);
    }

    /**
    *  Transform the yEd-style rendering of graphml files.
    */
    private void transformYEdStyleRenderingOfGraphmlFiles(HashMap<String, Tuple6<float[], String[], String[], String[], String[], String>> allNodesMap,
                                                          HashMap<String, Tuple6<String, Tuple2<float[], ArrayList<Point2D.Float>>, String[], String[], String[], String[]>> allEdgesMap)
    {
        Tuple6<float[], String[], String[], String[], String[], String> nodeTuple6 = null;
        Tuple6<String, Tuple2<float[], ArrayList<Point2D.Float>>, String[], String[], String[], String[]> edgeTuple6 = null;
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float maxY = Float.MIN_VALUE;
        float currentCoordXUpper = 0.0f;
        float currentCoordYUpper = 0.0f;
        float currentCoordXLower = 0.0f;
        float currentCoordYLower = 0.0f;

        for ( String nodeName : allNodesMap.keySet() )
        {
            nodeTuple6 = allNodesMap.get(nodeName);

            // min-max algo based on rectangle area covered by node, to also cover the component container cases
            currentCoordXUpper = nodeTuple6.first[2];
            currentCoordYUpper = nodeTuple6.first[3];
            currentCoordXLower = nodeTuple6.first[2] + nodeTuple6.first[1];
            currentCoordYLower = nodeTuple6.first[3] + nodeTuple6.first[0];

            if (currentCoordXUpper < minX) minX = currentCoordXUpper;
            if (currentCoordYUpper < minY) minY = currentCoordYUpper;
            if (currentCoordXLower < minX) minX = currentCoordXLower;
            if (currentCoordYLower < minY) minY = currentCoordYLower;

            if (currentCoordXUpper > maxX) maxX = currentCoordXUpper;
            if (currentCoordYUpper > maxY) maxY = currentCoordYUpper;
            if (currentCoordXLower > maxX) maxX = currentCoordXLower;
            if (currentCoordYLower > maxY) maxY = currentCoordYLower;

            // have to pad half the width/height in coords, as yEd does it that way
            // reverse index because graphml file & parser has it in height/width format instead of width/height!
            nodeTuple6.first[2] += nodeTuple6.first[1] / 2.0f;
            nodeTuple6.first[3] += nodeTuple6.first[0] / 2.0f;
        }

        float rangeX = abs(maxX - minX);
        float rangeY = abs(maxY - minY);
        if (rangeX < MINIMUM_RANGE) rangeX = MINIMUM_RANGE;
        if (rangeY < MINIMUM_RANGE) rangeY = MINIMUM_RANGE;
        float ratioX = rangeX / CANVAS_X_SIZE;
        float ratioY = rangeY / CANVAS_Y_SIZE;
        // convert to multiplicationFactor instead of divisionFactor for a possible speed-up (divisions are usually more slow)
        float multiplicationFactor = (ratioX > ratioY) ? (1.0f / ratioX) : (1.0f / ratioY);

        if (DEBUG_BUILD)
        {
            println("\nrangeX: " + rangeX);
            println("rangeY: " + rangeY);
            println("minX: " + minX);
            println("maxX: " + maxX);
            println("minY: " + minY);
            println("maxY: " + maxY);
            println("ratioX: " + ratioX);
            println("ratioY: " + ratioY);
            println("multiplicationFactor: " + multiplicationFactor);
        }

        rangeX *= multiplicationFactor;
        rangeY *= multiplicationFactor;
        minX *= multiplicationFactor;
        minY *= multiplicationFactor;

        if (ratioX > ratioY)
            // center the Y dimention, since the X will be exactly CANVAS_X_SIZE
            minY -= (CANVAS_Y_SIZE - rangeY) / 2;
        else
            // center the X dimention, since the Y will be exactly CANVAS_Y_SIZE
            minX -= (CANVAS_X_SIZE - rangeX) / 2;

        for ( String nodeKey : allNodesMap.keySet() )
        {
            nodeTuple6 = allNodesMap.get(nodeKey);

            // scale the height/width/x/y values
            nodeTuple6.first[0] *= multiplicationFactor;
            nodeTuple6.first[1] *= multiplicationFactor;
            nodeTuple6.first[2] *= multiplicationFactor;
            nodeTuple6.first[3] *= multiplicationFactor;
            // nodeTuple6.first[4] *= multiplicationFactorZ; // not needed since the depth (Z coordinate) is always zero when a graphml file is parsed

            // need to translate the x/y values only
            nodeTuple6.first[2] -= minX;
            nodeTuple6.first[3] -= minY;
        }

        Tuple2<float[], ArrayList<Point2D.Float>> allPathValues = null;
        String[] edgeLabelValues = null;
        int size = 0;
        float height = 0.0f, width = 0.0f, x = 0.0f, y = 0.0f;
        for ( String edgeKey : allEdgesMap.keySet() )
        {
            edgeTuple6 = allEdgesMap.get(edgeKey);

            allPathValues = edgeTuple6.second;
            edgeLabelValues = edgeTuple6.fifth;

            // scale the sx/sy/tx/ty values
            allPathValues.first[0] *= multiplicationFactor;
            allPathValues.first[1] *= multiplicationFactor;
            allPathValues.first[2] *= multiplicationFactor;
            allPathValues.first[3] *= multiplicationFactor;

            // need to translate the sx/sy/tx/ty values
            allPathValues.first[0] -= minX;
            allPathValues.first[1] -= minX;
            allPathValues.first[2] -= minX;
            allPathValues.first[3] -= minX;

            // don't use a foreach loop so as to avoid the object copy that may not register the multiplicationFactor calculation
            size = allPathValues.second.size();
            for (int i = 0; i < size; i++)
            {
                // scale the polyline point values
                allPathValues.second.get(i).x *= multiplicationFactor;
                allPathValues.second.get(i).y *= multiplicationFactor;

                // need to translate the polyline point values
                allPathValues.second.get(i).x -= minX;
                allPathValues.second.get(i).y -= minY;
            }

            if ( !edgeLabelValues[7].isEmpty() )
            {
                height = Float.parseFloat(edgeLabelValues[7]); // retrieve the height value
                height *= multiplicationFactor; // scale the height value
                edgeLabelValues[7] = Float.toString(height);

            }
            if ( !edgeLabelValues[14].isEmpty() )
            {
                width = Float.parseFloat(edgeLabelValues[14]); // retrieve the width value
                width *= multiplicationFactor; // scale the width value
                edgeLabelValues[14] = Float.toString(width);
            }

            if ( !edgeLabelValues[15].isEmpty() )
            {
                x = Float.parseFloat(edgeLabelValues[15]); // retrieve the x value
                x *= multiplicationFactor; // scale the x value
                x -= minX; // need to translate the x value only
                edgeLabelValues[15] = Float.toString(x);
            }

            if ( !edgeLabelValues[16].isEmpty() )
            {
                y = Float.parseFloat(edgeLabelValues[16]); // retrieve the y value
                y *= multiplicationFactor; // scale the y value
                y -= minY; // need to translate the y value only
                edgeLabelValues[16] = Float.toString(y);
            }
        }

        gnc.setRangeX(rangeX);
        gnc.setRangeY(rangeY);
    }

    /**
    *  Creates the component containers.
    */
    private ArrayList<GraphmlComponentContainer> createComponentContainers(HashMap<String, Tuple6<float[], String[], String[], String[], String[], String>> allNodesMap)
    {
        Tuple6<float[], String[], String[], String[], String[], String> nodeTuple6 = null;
        String nodeLabelName = "";
        String[] nodeLabelNameElements = null;
        float[] geometryValues = null;
        Rectangle2D.Float rectangle2D = null;
        String nodeColorString = "";
        Color nodeColor = null;
        ArrayList<GraphmlComponentContainer> allPathwayComponentContainersFor3D = new ArrayList<GraphmlComponentContainer>();

        try
        {
            for ( String nodeName : allNodesMap.keySet() )
            {
                nodeTuple6 = allNodesMap.get(nodeName);
                nodeLabelName = nodeTuple6.fifth[0].trim();
                if ( nodeLabelName.startsWith(COMPONENT_CONTAINER_REGEX) && nodeLabelName.endsWith(COMPONENT_CONTAINER_REGEX) )
                {
                    nodeLabelNameElements = nodeLabelName.substring( 1, nodeLabelName.length() ).split("\\" + COMPONENT_CONTAINER_REGEX + "+"); // skip first regex character
                    geometryValues = nodeTuple6.first;
                    rectangle2D = new Rectangle2D.Float(geometryValues[2], geometryValues[3], geometryValues[1], geometryValues[0]); // reverse index because graphml file & parser has it in height/width format instead of width/height!
                    nodeColorString = nodeTuple6.second[1];
                    nodeColor = (nodeColorString != null) ? Color.decode(nodeColorString) : DEFAULT_NODE_COLOR;

                    if (nodeLabelNameElements.length == 1)
                        allPathwayComponentContainersFor3D.add( new GraphmlComponentContainer(nodeName, nodeLabelNameElements[0], rectangle2D, nodeColor) );
                    else if (nodeLabelNameElements.length == 2)
                        allPathwayComponentContainersFor3D.add( new GraphmlComponentContainer(nodeName, nodeLabelNameElements[0], Integer.parseInt(nodeLabelNameElements[1]), rectangle2D, nodeColor) );
                    else if (nodeLabelNameElements.length == 3)
                        allPathwayComponentContainersFor3D.add( new GraphmlComponentContainer(nodeName, nodeLabelNameElements[0], Integer.parseInt(nodeLabelNameElements[1]), Float.parseFloat(nodeLabelNameElements[2]), rectangle2D, nodeColor) );
                }
            }

            Collections.sort(allPathwayComponentContainersFor3D);
        }
        catch (Exception exc)
        {
            JOptionPane.showMessageDialog(layoutFrame, "Parsing the Graphml Component Containers reported an exception:\n" + nodeLabelName + " " + exc.getMessage(), "Graphml Component Containers Parser Error", JOptionPane.WARNING_MESSAGE);
            if (DEBUG_BUILD) println("Parsing the Graphml Component Containers reported an exception:\n" + nodeLabelName + " " + exc.getMessage());
        }

        return allPathwayComponentContainersFor3D;
    }


}