package org.BioLayoutExpress3D.Network;

import java.awt.*;
import java.awt.geom.*;

/**
*  The GraphmlComponentContainer class encapsulates all the necessary information for the graphml component container.
*
*
* @author Thanos Theo, 2011
* @version 3.0.0.0
*
*/

public class GraphmlComponentContainer implements Comparable<GraphmlComponentContainer>
{

    /**
    *  Default GraphmlComponentContainer depth.
    */
    private static final int DEFAULT_COMPONENT_CONTAINER_DEPTH = 1;

    /**
    *  Default GraphmlComponentContainer alpha.
    */
    private static final float DEFAULT_COMPONENT_CONTAINER_ALPHA = 0.2f;

    /**
    *  GraphmlComponentContainer id.
    */
    public String id = "";

    /**
    *  GraphmlComponentContainer name.
    */
    public String name = "";

    /**
    *  GraphmlComponentContainer depth.
    */
    public int depth = 0;

    /**
    *  GraphmlComponentContainer alpha.
    */
    public float alpha = 0.0f;

    /**
    *  GraphmlComponentContainer rectangle2D.
    */
    public Rectangle2D.Float rectangle2D = null;

    /**
    *  GraphmlComponentContainer color.
    */
    public Color color = null;

    /**
    *  First GraphmlComponentContainer constructor.
    */
    public GraphmlComponentContainer(String id, String name, Rectangle2D.Float rectangle2D, Color color)
    {
        this(id, name, DEFAULT_COMPONENT_CONTAINER_DEPTH, DEFAULT_COMPONENT_CONTAINER_ALPHA, rectangle2D, color);
    }

    /**
    *  Second GraphmlComponentContainer constructor.
    */
    public GraphmlComponentContainer(String id, String name, int depth, Rectangle2D.Float rectangle2D, Color color)
    {
        this(id, name, depth, DEFAULT_COMPONENT_CONTAINER_ALPHA, rectangle2D, color);
    }

    /**
    *  Third GraphmlComponentContainer constructor.
    */
    public GraphmlComponentContainer(String id, String name, int depth, float alpha, Rectangle2D.Float rectangle2D, Color color)
    {
        this.id = id;
        this.name = name;
        this.depth = depth;
        this.alpha = alpha;
        this.rectangle2D = rectangle2D;
        this.color = color;
    }

    /**
    *  Overriden toString() method for GraphmlComponentContainer.
    */
    @Override
    public String toString()
    {
        StringBuilder returnString = new StringBuilder("\nGraphml Component Container details:");
        returnString.append("\nName: ").append(name);
        returnString.append("\nDepth: ").append(depth);
        returnString.append("\nAlpha: ").append(alpha);
        returnString.append("\nRectangle2D geometry details:");
        returnString.append("\nX: ").append(rectangle2D.x);
        returnString.append("\nY: ").append(rectangle2D.x);
        returnString.append("\nWidth: ").append(rectangle2D.width);
        returnString.append("\nHeight: ").append(rectangle2D.height);
        returnString.append("\nColor: ").append("[r=").append( color.getRed() ).append(",g=").append( color.getGreen() ).append(",b=").append( color.getBlue() ).append("]");

        return returnString.toString();
    }

    /**
    *  compareTo() implementation of Comparable<GraphmlComponentContainer> for sorting in a data structure.
    *  Note: implementation enforces reverse sorting, ie from smallest to biggest rectangle volume for correct alpha transparency rendering on the OpenGL 3D mode.
    */
    @Override
    public int compareTo(GraphmlComponentContainer obj)
    {
        return     ( (obj.rectangle2D.width * obj.rectangle2D.height * obj.depth)  < (this.rectangle2D.width * this.rectangle2D.height * this.depth) ) ? 1
               : ( ( (obj.rectangle2D.width * obj.rectangle2D.height * obj.depth) == (this.rectangle2D.width * this.rectangle2D.height * this.depth) ) ? 0 : -1);
    }


}