package org.BioLayoutExpress3D.Files.Parsers;

import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
import org.xml.sax.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.DataStructures.*;
import org.BioLayoutExpress3D.DocumentParsers.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* GraphmlParser is the parser class used to parse graphml files. It includes an inner interface to be used as a listener using the callback design pattern.
*
* @see org.BioLayoutExpress3D.DocumentParsers.XmlParser
* @see org.BioLayoutExpress3D.DataStructures.Tuple6
* @see org.BioLayoutExpress3D.DataStructures.Tuple2
* @author Thanos Theo, edge label names parsing code additions Benjamin Boyer, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public final class GraphmlParser extends XmlParser
{
    private final boolean DEBUG_GRAPHML = false;

    /**
    *  Graph related parsing variable.
    */
    private String edgedefault = "";

    /**
    *  Graph related parsing variable.
    */
    private String id = "";

    /**
    *  Graph related parsing variable.
    */
    private int parseEdges = 0;

    /**
    *  Graph related parsing variable.
    */
    private boolean checkParseEdges = false;

    /**
    *  Graph related parsing variable.
    */
    private int parseNodes = 0;

    /**
    *  Graph related parsing variable.
    */
    private boolean checkParseNodes = false;

    /**
    *  Graph related parsing variable.
    */
    private String parseOrder = "";


    /**
    *  Node related parsing variable.
    */
    private String nodeId = "";

    /**
    *  Node related parsing variable.
    */
    private String key = "";

    /**
    *  Node related parsing variable.
    */
    private float[] geometryValues = null;

    /**
    *  Node related parsing variable.
    */
    private String[] fillValues = null;

    /**
    *  Node related parsing variable.
    */
    private String[] borderStyleValues = null;

    /**
    *  Node related parsing variable.
    */
    private String[] nodeLabelValues = null;

    /**
    *  Node related parsing variable.
    */
    private String[] nodeLabelName = null;

    /**
    *  Node related parsing variable.
    */
    private String shape = "";

    /**
    *  Node related parsing variable.
    */
    private boolean isAtNodeLabel = false;

    /**
    *  A HashMap(K, V) to put all node related data information. Key is the node id (String type).
    *  As a value type, there's a Tuple6 data structure that holds the following information about nodes:
    *
    *  Tuple6 type: <float[], String[], String[], String[], String, String>>
    *
    *  float[]  --> all geometry values     (array with  4 elements)
    *  String[] --> all fill values         (array with  4 elements)
    *  String[] --> all border style values (array with  2 elements)
    *  String[] --> all node label values   (array with 15 elements)
    *  String[] --> node label name, uses an array reference to give opportunity for potential name changes
    *  String   --> node shape
    *
    */
    private HashMap<String, Tuple6<float[], String[], String[], String[], String[], String>> allNodesMap = null;

    /**
    *  Edge related parsing variable.
    */
    private String edgeId = "";

    /**
    *  Edge related parsing variable.
    */
    private String[] edgeNodeConnectionValues = null;

    /**
    *  Edge related parsing variable.
    */
    private float[] pathValues = null;

    /**
    *  Edge related parsing variable.
    */
    private ArrayList<Point2D.Float> allPointValues = null;

    /**
    *  Edge related parsing variable.
    */
    private Tuple2<float[], ArrayList<Point2D.Float>> allPathValues = null;

    /**
    *  Edge related parsing variable.
    */
    private String[] lineStyleValues = null;

    /**
    *  Edge related parsing variable.
    */
    private String[] arrowsValues = null;

    /**
    *  Edge related parsing variable.
    */
    private String[] edgeLabelValues = null;

    /**
    *  Edge related parsing variable.
    */
    private String[] bendStyleOrArcValues = null;

    /**
    *  Edge related parsing variable.
    */
    private boolean isAtEdgeLabel = false;

    /**
    *  A HashMap(K, V) to put all edge related data information. Key is the node1-to-node2 connection name values (String type)
    *  As a value type, there's a Tuple6 data structure that holds the following information about edges:
    *
    *  Tuple6<String, Tuple2<float[], ArrayList<Point2D.Float>>, String[], String[], String[], String[]>
    *
    *  String                                    --> edge id
    *  Tuple2<float[], ArrayList<Point2D.Float>> --> all path related values             (Tuple2 with an array with  4 elements & an ArrayList optionally storing points (not always the case in Graphml files, optional data))
    *  String[]                                  --> all line style values               (array with  3 elements)
    *  String[]                                  --> all arrows label values             (array with  2 elements)
    *  String[]                                  --> all edge label values               (array with 18 elements, last element is the edge label name)
    *  String[]                                  --> all edge bend style (or arc) values (array with  4 elements)
    *
    */
    private HashMap<String, Tuple6<String, Tuple2<float[], ArrayList<Point2D.Float>>, String[], String[], String[], String[]>> allEdgesMap = null;

    /**
    *  LayoutFrame reference to be used as a parent reference for the JOptionPane.showMessageDialog().
    */
    private LayoutFrame layoutFrame = null;

    /**
    *  GraphmlParser listener to be used as a callback when parsing a graphml file.
    */
    private GraphmlParserListener listener = null;

    /**
    *  The first constructor of the GraphmlParser class.
    */
    public GraphmlParser()
    {
        super();
    }

    /**
    *  The second constructor of the GraphmlParser class.
    */
    public GraphmlParser(LayoutFrame layoutFrame)
    {
        super();

        this.layoutFrame = layoutFrame;
    }

    /**
    *  Initialize data structures method.
    */
    @Override
    protected void initDataStructures()
    {
        allNodesMap = new HashMap<String, Tuple6<float[], String[], String[], String[], String[], String>>();
        allEdgesMap = new HashMap<String, Tuple6<String, Tuple2<float[], ArrayList<Point2D.Float>>, String[], String[], String[], String[]>>();

        initAllNodesRelatedDataStructures();
        initAllEdgesRelatedDataStructures();
    }

    /**
    *  Initialize all nodes related data structures method.
    */
    private void initAllNodesRelatedDataStructures()
    {
        nodeId = "";
        geometryValues = new float[] { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f }; // fifth element reserved for Z coord (3D axis)
        fillValues = new String[] { "", "", "", "" };
        borderStyleValues = new String[] { "", "", "", "" };
        nodeLabelValues = new String[] { "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" };
        nodeLabelName = new String[] { "" };
        shape = "";
    }

    /**
    *  Initialize all edges related data structures method.
    */
    private void initAllEdgesRelatedDataStructures()
    {
        edgeId = "";
        edgeNodeConnectionValues = new String[] { "", "" };
        pathValues = new float[] { 0.0f, 0.0f, 0.0f, 0.0f };
        allPointValues = new ArrayList<Point2D.Float>();
        allPathValues = Tuples.tuple(pathValues, allPointValues);
        lineStyleValues = new String[] { "", "", "" };
        arrowsValues = new String[] { "", "" };
        edgeLabelValues = new String[] { "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" };
        bendStyleOrArcValues = new String[] { "", "", "", "" };
    }

    /**
    *  SAX inherit callback method.
    */
    @Override
    protected void startDocumentInherit() throws SAXException
    {
        if (DEBUG_BUILD && DEBUG_GRAPHML) println("\nStarting parsing the " + nameOfXmlParser + " XML document.\n");
    }

    /**
    *  SAX inherit callback method.
    */
    @Override
    protected void endDocumentInherit() throws SAXException
    {
        if (DEBUG_BUILD && DEBUG_GRAPHML) println("\nFinished parsing the " + nameOfXmlParser + " XML document.\n");
    }

    /**
    *  SAX inherit callback method.
    */
    @Override
    protected void startElementInherit(String namespace, String localname, String type, Attributes attributes)  throws SAXException
    {
        if ( type.equals("graph") )
        {
            if (DEBUG_BUILD && DEBUG_GRAPHML) println("\nGraphml tag <graph> reached");

            edgedefault = attributes.getValue("edgedefault");
            id = attributes.getValue("id");

            String tempEdges = attributes.getValue("parse.edges");
            if (tempEdges != null)
                parseEdges = Integer.parseInt(tempEdges);
            else
                checkParseEdges = true;

            String tempNodes = attributes.getValue("parse.nodes");
            if (tempNodes != null)
                parseNodes = Integer.parseInt(tempNodes);
            else
                checkParseNodes = true;

            parseOrder = attributes.getValue("parse.order");

            if (DEBUG_BUILD && DEBUG_GRAPHML)
            {
                println("edgedefault: " + edgedefault);
                println("id: " + id);
                println("parseEdges: " + parseEdges);
                println("parseNodes: " + parseNodes);
                println("parseOrder: " + parseOrder);
            }

            if (listener != null)
                listener.graphmlDetailsParsed();
        }
        // from now on, parsing node data
        else if ( type.equals("node") )
        {
            if (DEBUG_BUILD && DEBUG_GRAPHML) println("\nGraphml tag <node> reached");

            nodeId = attributes.getValue("id");

            if (DEBUG_BUILD && DEBUG_GRAPHML) println("nodeId: " + nodeId);
        }
        else if ( type.equals("data") )
        {
            if (DEBUG_BUILD && DEBUG_GRAPHML) println("\nGraphml tag <data> reached");

            key = attributes.getValue("key");

            if (DEBUG_BUILD && DEBUG_GRAPHML) println("key: " + key);
        }
        else if ( type.equals("y:ShapeNode") )
        {
            if (DEBUG_BUILD && DEBUG_GRAPHML) println("\nGraphml tag <y:ShapeNode> reached");
        }
        else if ( type.equals("y:Geometry") )
        {
            if (DEBUG_BUILD && DEBUG_GRAPHML) println("\nGraphml tag <y:Geometry> reached");

            geometryValues[0] = Float.parseFloat( attributes.getValue("height") );
            geometryValues[1] = Float.parseFloat( attributes.getValue("width") );
            geometryValues[2] = Float.parseFloat( attributes.getValue("x") );
            geometryValues[3] = Float.parseFloat( attributes.getValue("y") );

            if (DEBUG_BUILD && DEBUG_GRAPHML)
            {
                println("height: " + geometryValues[0]);
                println("width: " + geometryValues[1]);
                println("x: " + geometryValues[2]);
                println("y: " + geometryValues[3]);
            }
        }
        else if ( type.equals("y:Fill") )
        {
            if (DEBUG_BUILD && DEBUG_GRAPHML) println("\nGraphml tag <y:Fill> reached");

            // make sure the fillValues array has 4 values, as it's not always the case in the graphml file
            fillValues[0] = attributes.getValue("hasColor");
            fillValues[1] = attributes.getValue("color");
            fillValues[2] = attributes.getValue("color2");
            fillValues[3] = attributes.getValue("transparent");

            if (DEBUG_BUILD && DEBUG_GRAPHML)
            {
                println("hasColor: " + fillValues[0]);
                println("color: " + fillValues[1]);
                println("color2: " + fillValues[2]);
                println("transparent: " + fillValues[3]);
            }
        }
        else if ( type.equals("y:BorderStyle") )
        {
            if (DEBUG_BUILD && DEBUG_GRAPHML) println("\nGraphml tag <y:BorderStyle> reached");

            // make sure the borderStyleValues array has 4 values, as it's not always the case in the graphml file
            borderStyleValues[0] = attributes.getValue("hasColor");
            borderStyleValues[1] = attributes.getValue("color");
            borderStyleValues[2] = attributes.getValue("type");
            borderStyleValues[3] = attributes.getValue("width");

            if (DEBUG_BUILD && DEBUG_GRAPHML)
            {
                println("hasColor: " + borderStyleValues[0]);
                println("color: " + borderStyleValues[1]);
                println("type: " + borderStyleValues[2]);
                println("width: " + borderStyleValues[3]);
            }
        }
        else if ( type.equals("y:NodeLabel") )
        {
            if (DEBUG_BUILD && DEBUG_GRAPHML) println("\nGraphml tag <y:NodeLabel> reached");

            isAtNodeLabel = true;
            nodeLabelValues[0]  = attributes.getValue("alignment");
            nodeLabelValues[1]  = attributes.getValue("autoSizePolicy");
            nodeLabelValues[2]  = attributes.getValue("fontFamily");
            nodeLabelValues[3]  = attributes.getValue("fontSize");
            nodeLabelValues[4]  = attributes.getValue("fontStyle");
            nodeLabelValues[5]  = attributes.getValue("hasBackgroundColor");
            nodeLabelValues[6]  = attributes.getValue("hasLineColor");
            nodeLabelValues[7]  = attributes.getValue("height");
            nodeLabelValues[8]  = attributes.getValue("modelName");
            nodeLabelValues[9]  = attributes.getValue("modelPosition");
            nodeLabelValues[10] = attributes.getValue("textColor");
            nodeLabelValues[11] = attributes.getValue("visible");
            nodeLabelValues[12] = attributes.getValue("width");
            nodeLabelValues[13] = attributes.getValue("x");
            nodeLabelValues[14] = attributes.getValue("y");

            if (DEBUG_BUILD && DEBUG_GRAPHML)
            {
                println("alignment: " + nodeLabelValues[0]);
                println("autoSizePolicy: " + nodeLabelValues[1]);
                println("fontFamily: " + nodeLabelValues[2]);
                println("fontSize: " + nodeLabelValues[3]);
                println("fontStyle: " + nodeLabelValues[4]);
                println("hasBackgroundColor: " + nodeLabelValues[5]);
                println("hasLineColor: " + nodeLabelValues[6]);
                println("height: " + nodeLabelValues[7]);
                println("modelName: " + nodeLabelValues[8]);
                println("modelPosition: " + nodeLabelValues[9]);
                println("textColor: " + nodeLabelValues[10]);
                println("visible: " + nodeLabelValues[11]);
                println("width: " + nodeLabelValues[12]);
                println("x: " + nodeLabelValues[13]);
                println("y: " + nodeLabelValues[14]);
            }
        }
        else if ( type.equals("y:Shape") )
        {
            if (DEBUG_BUILD && DEBUG_GRAPHML) println("\nGraphml tag <y:Shape> reached");

            shape = attributes.getValue("type");

            if (DEBUG_BUILD && DEBUG_GRAPHML) println("shape: " + shape);
        }
        // from now on, parsing edge data
        else if ( type.equals("edge") )
        {
            if (DEBUG_BUILD && DEBUG_GRAPHML) println("\nGraphml tag <edge> reached");

            edgeId = attributes.getValue("id");

            edgeNodeConnectionValues[0] = attributes.getValue("source");
            edgeNodeConnectionValues[1] = attributes.getValue("target");

            if (DEBUG_BUILD && DEBUG_GRAPHML)
            {
                println("edgeId: " + edgeId);
                println("source: " + edgeNodeConnectionValues[0]);
                println("target: " + edgeNodeConnectionValues[1]);
            }
        }
        else if ( type.equals("y:PolyLineEdge") )
        {
            if (DEBUG_BUILD && DEBUG_GRAPHML) println("\nGraphml tag <y:PolyLineEdge> reached");
        }
        else if ( type.equals("y:ArcEdge") )
        {
            if (DEBUG_BUILD && DEBUG_GRAPHML) println("\nGraphml tag <y:ArcEdge> reached");
        }
        else if ( type.equals("y:Path") )
        {
            if (DEBUG_BUILD && DEBUG_GRAPHML) println("\nGraphml tag <y:Path> reached");

            pathValues[0] = Float.parseFloat( attributes.getValue("sx") );
            pathValues[1] = Float.parseFloat( attributes.getValue("sy") );
            pathValues[2] = Float.parseFloat( attributes.getValue("tx") );
            pathValues[3] = Float.parseFloat( attributes.getValue("ty") );

            if (DEBUG_BUILD && DEBUG_GRAPHML)
            {
                println("sx: " + pathValues[0]);
                println("sy: " + pathValues[1]);
                println("tx: " + pathValues[2]);
                println("ty: " + pathValues[3]);
            }
        }
        else if ( type.equals("y:Point") )
        {
            if (DEBUG_BUILD && DEBUG_GRAPHML) println("\nGraphml tag <y:Point> reached");

            Point2D.Float point = new Point2D.Float( Float.parseFloat( attributes.getValue("x") ),
                                                     Float.parseFloat( attributes.getValue("y") )
                                                   );
            allPointValues.add(point);

            if (DEBUG_BUILD && DEBUG_GRAPHML) println("Point: " + point.toString());
        }
        else if ( type.equals("y:LineStyle") )
        {
            if (DEBUG_BUILD && DEBUG_GRAPHML) println("\nGraphml tag <y:LineStyle> reached");

            lineStyleValues[0] = attributes.getValue("color");
            lineStyleValues[1] = attributes.getValue("type");
            lineStyleValues[2] = attributes.getValue("width");

            if (DEBUG_BUILD && DEBUG_GRAPHML)
            {
                println("color: " + lineStyleValues[0]);
                println("type: " + lineStyleValues[1]);
                println("width: " + lineStyleValues[2]);
            }
        }
        else if ( type.equals("y:Arrows") )
        {
            if (DEBUG_BUILD && DEBUG_GRAPHML) println("\nGraphml tag <y:Arrows> reached");

            arrowsValues[0] = attributes.getValue("source");
            arrowsValues[1] = attributes.getValue("target");

            if (DEBUG_BUILD && DEBUG_GRAPHML)
            {
                println("source: " + arrowsValues[0]);
                println("target: " + arrowsValues[1]);
            }
        }
        else if ( type.equals("y:EdgeLabel") )
        {
            if (DEBUG_BUILD && DEBUG_GRAPHML) println("\nGraphml tag <y:EdgeLabel> reached");

            isAtEdgeLabel = true;
            edgeLabelValues[0]  = attributes.getValue("alignment");
            edgeLabelValues[1]  = attributes.getValue("distance");
            edgeLabelValues[2]  = attributes.getValue("fontFamily");
            edgeLabelValues[3]  = attributes.getValue("fontSize");
            edgeLabelValues[4]  = attributes.getValue("fontStyle");
            edgeLabelValues[5]  = attributes.getValue("hasBackgroundColor");
            edgeLabelValues[6]  = attributes.getValue("hasLineColor");
            edgeLabelValues[7]  = attributes.getValue("height");
            edgeLabelValues[8]  = attributes.getValue("modelName");
            edgeLabelValues[9]  = attributes.getValue("modelPosition");
            edgeLabelValues[10] = attributes.getValue("preferredPlacement");
            edgeLabelValues[11] = attributes.getValue("ratio");
            edgeLabelValues[12] = attributes.getValue("textColor");
            edgeLabelValues[13] = attributes.getValue("visible");
            edgeLabelValues[14] = attributes.getValue("width");
            edgeLabelValues[15] = attributes.getValue("x");
            edgeLabelValues[16] = attributes.getValue("y");

            if (DEBUG_BUILD && DEBUG_GRAPHML)
            {
                println("alignment: " + edgeLabelValues[0]);
                println("distance: " + edgeLabelValues[1]);
                println("fontFamily: " + edgeLabelValues[2]);
                println("fontSize: " + edgeLabelValues[3]);
                println("fontStyle: " + edgeLabelValues[4]);
                println("hasBackgroundColor: " + edgeLabelValues[5]);
                println("hasLineColor: " + edgeLabelValues[6]);
                println("height: " + edgeLabelValues[7]);
                println("modelName: " + edgeLabelValues[8]);
                println("modelPosition: " + edgeLabelValues[9]);
                println("preferredPlacement: " + edgeLabelValues[10]);
                println("ratio: " + edgeLabelValues[11]);
                println("textColor: " + edgeLabelValues[12]);
                println("visible: " + edgeLabelValues[13]);
                println("width: " + edgeLabelValues[14]);
                println("x: " + edgeLabelValues[15]);
                println("y: " + edgeLabelValues[16]);
                println("edgeLabel: " + edgeLabelValues[17]);
            }
        }
        else if ( type.equals("y:Arc") )
        {
            if (DEBUG_BUILD && DEBUG_GRAPHML) println("\nGraphml tag <y:Arc> reached");

            bendStyleOrArcValues[1] = attributes.getValue("height");
            bendStyleOrArcValues[1] = attributes.getValue("ratio");
            bendStyleOrArcValues[1] = attributes.getValue("type");

            if (DEBUG_BUILD && DEBUG_GRAPHML)
            {
                println("height: " + bendStyleOrArcValues[1]);
                println("ratio: " + bendStyleOrArcValues[2]);
                println("type: " + bendStyleOrArcValues[3]);
            }
        }
        else if ( type.equals("y:BendStyle") )
        {
            if (DEBUG_BUILD && DEBUG_GRAPHML) println("\nGraphml tag <y:BendStyle> reached");

            bendStyleOrArcValues[0] = attributes.getValue("smoothed");

            if (DEBUG_BUILD && DEBUG_GRAPHML) println("smoothed: " + bendStyleOrArcValues[0]);
        }
    }

    /**
    *  SAX inherit callback method.
    */
    @Override
    protected void endElementInherit(String namespace, String localname, String type) throws SAXException
    {
        if ( type.equals("graph") )
        {
            if (DEBUG_BUILD && DEBUG_GRAPHML) println("Graphml tag <graph> ended\n");
        }
        // from now on, parsing node data
        else if ( type.equals("node") )
        {
            if (DEBUG_BUILD && DEBUG_GRAPHML) println("Graphml tag <node> ended\n");

            Tuple6<float[], String[], String[], String[], String[], String> nodeTuple6 = Tuples.tuple(geometryValues,
                                                                                                      fillValues,
                                                                                                      borderStyleValues,
                                                                                                      nodeLabelValues,
                                                                                                      nodeLabelName,
                                                                                                      shape);
            allNodesMap.put(nodeId, nodeTuple6);

            // re-init all nodes related data structures so as to avoid multiple node Tuple6 pointing to the same node data structures
            initAllNodesRelatedDataStructures();

            if (checkParseNodes) parseNodes++;

            if (listener != null)
                listener.nodeOrEdgeParsed();
        }
        else if ( type.equals("data") )
        {
            if (DEBUG_BUILD && DEBUG_GRAPHML) println("Graphml tag <data> ended\n");
        }
        else if ( type.equals("y:ShapeNode") )
        {
            if (DEBUG_BUILD && DEBUG_GRAPHML) println("Graphml tag <y:ShapeNode> ended\n");
        }
        else if ( type.equals("y:Geometry") )
        {
            if (DEBUG_BUILD && DEBUG_GRAPHML) println("Graphml tag <y:Geometry> ended\n");
        }
        else if ( type.equals("y:Fill") )
        {
            if (DEBUG_BUILD && DEBUG_GRAPHML) println("Graphml tag <y:Fill> ended\n");
        }
        else if ( type.equals("y:BorderStyle") )
        {
            if (DEBUG_BUILD && DEBUG_GRAPHML) println("Graphml tag <y:BorderStyle> ended\n");
        }
        else if ( type.equals("y:NodeLabel") )
        {
            isAtNodeLabel = false;

            nodeLabelName[0] = nodeLabelName[0].trim();
            if (DEBUG_BUILD && DEBUG_GRAPHML)
            {
                println("nodeLabelName: " + nodeLabelName[0]);
                println("Graphml tag <y:NodeLabel> ended\n");
            }
        }
        else if ( type.equals("y:Shape") )
        {
            if (DEBUG_BUILD && DEBUG_GRAPHML) println("Graphml tag <y:Shape> ended\n");
        }
        // from now on, parsing edge data
        else if ( type.equals("edge") )
        {
            if (DEBUG_BUILD && DEBUG_GRAPHML) println("Graphml tag <edge> ended\n");

            Tuple6<String, Tuple2<float[], ArrayList<Point2D.Float>>, String[], String[], String[], String[]> edgeTuple6 = Tuples.tuple(edgeId,
                                                                                                                                        allPathValues,
                                                                                                                                        lineStyleValues,
                                                                                                                                        arrowsValues,
                                                                                                                                        edgeLabelValues,
                                                                                                                                        bendStyleOrArcValues);
            allEdgesMap.put(edgeNodeConnectionValues[0] + " " + edgeNodeConnectionValues[1], edgeTuple6);

            // re-init all edges related data structures so as to avoid multiple node Tuple6 pointing to the same edge data structures
            initAllEdgesRelatedDataStructures();

            if (checkParseEdges) parseEdges++;

            if (listener != null)
                listener.nodeOrEdgeParsed();
        }
        else if ( type.equals("y:PolyLineEdge") )
        {
            if (DEBUG_BUILD && DEBUG_GRAPHML) println("Graphml tag <y:PolyLineEdge> ended\n");
        }
        else if ( type.equals("y:ArcEdge") )
        {
            if (DEBUG_BUILD && DEBUG_GRAPHML) println("Graphml tag <y:ArcEdge> ended\n");
        }
        else if ( type.equals("y:Path") )
        {
            if (DEBUG_BUILD && DEBUG_GRAPHML) println("Graphml tag <y:Path> ended\n");

            allPathValues = Tuples.tuple(pathValues, allPointValues);
        }
        else if ( type.equals("y:Point") )
        {
            if (DEBUG_BUILD && DEBUG_GRAPHML) println("Graphml tag <y:Point> ended\n");
        }
        else if ( type.equals("y:LineStyle") )
        {
            if (DEBUG_BUILD && DEBUG_GRAPHML) println("Graphml tag <y:LineStyle> ended\n");
        }
        else if ( type.equals("y:Arrows") )
        {
            if (DEBUG_BUILD && DEBUG_GRAPHML) println("Graphml tag <y:Arrows> ended\n");
        }
        else if ( type.equals("y:EdgeLabel") )
        {
            isAtEdgeLabel = false;

            edgeLabelValues[17] = edgeLabelValues[17].trim();
            if (DEBUG_BUILD && DEBUG_GRAPHML)
            {
                println("edgeLabelName: " + edgeLabelValues[17]);
                println("Graphml tag <y:EdgeLabel> ended\n");
            }
        }
        else if ( type.equals("y:BendStyle") )
        {
            if (DEBUG_BUILD && DEBUG_GRAPHML) println("Graphml tag <y:BendStyle> ended\n");
        }
    }

    /**
    *  SAX inherit callback method.
    */
    @Override
    protected void charactersInherit(char[] ch, int start, int len)
    {
        if (isAtNodeLabel)
            nodeLabelName[0] += new String(ch, start, len);

        if (isAtEdgeLabel)
            edgeLabelValues[17] += new String(ch, start, len);
    }

    /**
    *  ErrorHandler inherit callback method.
    */
    @Override
    protected void errorInherit(SAXParseException exception)
    {
        if (DEBUG_BUILD && DEBUG_GRAPHML) println( "XML validation error:\n" + exception.getMessage() );
        JOptionPane.showMessageDialog(layoutFrame, "XML validation error:\n" + exception.getMessage(), "XML validation error!", JOptionPane.ERROR_MESSAGE);
    }

    /**
    *  ErrorHandler inherit callback method.
    */
    @Override
    protected void fatalErrorInherit(SAXParseException exception)
    {
        if (DEBUG_BUILD && DEBUG_GRAPHML) println( "XML validation fatal error:\n" + exception.getMessage() );
        JOptionPane.showMessageDialog(layoutFrame, "XML validation fatal error:\n" + exception.getMessage(), "XML validation fatal error!", JOptionPane.ERROR_MESSAGE);
    }

    /**
    *  ErrorHandler inherit callback method.
    */
    @Override
    protected void warningInherit(SAXParseException exception)
    {
        if (DEBUG_BUILD && DEBUG_GRAPHML) println( "XML validation warning error:\n" + exception.getMessage() );
        JOptionPane.showMessageDialog(layoutFrame, "XML validation warning error:\n" + exception.getMessage(), "XML validation warning error!", JOptionPane.ERROR_MESSAGE);
    }

    /**
    *  Gets the graphml edgedefault value.
    */
    public String getGraphmlEdgedefault()
    {
        return edgedefault;
    }

    /**
    *  Gets the graphml id value.
    */
    public String getGraphmlId()
    {
        return id;
    }

    /**
    *  Gets the graphml parseEdges value (how many edges have been parsed).
    */
    public int getGraphmlParsedEdges()
    {
        return parseEdges;
    }

    /**
    *  Gets the graphml parseNodes value (how many nodes have been parsed).
    */
    public int getGraphmlParsedNodes()
    {
        return parseNodes;
    }

    /**
    *  Gets the graphml parseOrder value.
    */
    public String getGraphmlParsedOrder()
    {
        return parseOrder;
    }

    /**
    *  Gets the graphml allNodesMap.
    */
    public HashMap<String, Tuple6<float[], String[], String[], String[], String[], String>> getAllNodesMap()
    {
        return allNodesMap;
    }

    /**
    *  Gets the graphml allEdgesMap.
    */
    public HashMap<String, Tuple6<String, Tuple2<float[], ArrayList<Point2D.Float>>, String[], String[], String[], String[]>> getAllEdgesMap()
    {
        return allEdgesMap;
    }

    /**
    *  Sets the GraphmlParserListener listener.
    */
    public void setListener(GraphmlParserListener listener)
    {
        this.listener = listener;
    }

    /**
    *  Removes the GraphmlParserListener listener.
    */
    public void removeListener()
    {
        listener = null;
    }

    /**
    *  GraphmlParserListener interface, used as a callback design pattern for the BioLayout Express 3D framework.
    */
    public interface GraphmlParserListener
    {
        /**
        *  This method is called as a callback event when graphml details have been parsed.
        */
        public void graphmlDetailsParsed();

        /**
        *  This method is called as a callback event when a node or edge has been parsed.
        */
        public void nodeOrEdgeParsed();
    }


}