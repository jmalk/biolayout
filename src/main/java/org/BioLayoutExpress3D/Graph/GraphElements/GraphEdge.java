package org.BioLayoutExpress3D.Graph.GraphElements;

import java.awt.*;
import org.BioLayoutExpress3D.Network.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
*
* This class includes descriptions for graph edges.
*
* @author Thanos Theo, 2008-2009
* @version 3.0.0.0
*
*/

public final class GraphEdge
{
    private Color edgeColor = null;
    private GraphNode graphNodeFirst = null;
    private GraphNode graphNodeSecond = null;
    private Edge edge = null;

    public GraphEdge(GraphNode graphNodeFirst, GraphNode graphNodeSecond, Edge edge, float weight)
    {
        this.graphNodeFirst = graphNodeFirst;
        this.graphNodeSecond = graphNodeSecond;
        this.edge = edge;

        if (WEIGHTED_EDGES)
        {
            int red = (int)(255.0f * weight);
            int green = 0;
            int blue = (int)( 255.0f *(1.0f - weight) );

            edgeColor = new Color(red, green, blue);
        }
        else
            edgeColor = DEFAULT_EDGE_COLOR.get();
    }

    public void setEdgeName(String edgeName)
    {
        edge.setEdgeName(edgeName);
    }

    public String getEdgeName()
    {
        return edge.getEdgeName();
    }

    public Color getColor()
    {
        return edgeColor;
    }

    public float getWeight()
    {
        return edge.getWeight();
    }

    public void setWeight(float weight)
    {
        edge.setWeight(weight);
    }

    public float getNormalisedWeight()
    {
        return edge.getNormalisedWeight();
    }

    public void setNormalisedWeight(float normalisedWeight)
    {
        edge.setNormalisedWeight(normalisedWeight);
    }

    public float getScaledWeight()
    {
        return edge.getScaledWeight();
    }

    public void setScaledWeight(float scaledWeight)
    {
        edge.setScaledWeight(scaledWeight);
    }

    public Edge getEdge()
    {
        return edge;
    }

    public GraphNode getNodeFirst()
    {
        return graphNodeFirst;
    }

    public GraphNode getNodeSecond()
    {
        return graphNodeSecond;
    }

    public void setShowEdgeName(boolean showEdgeName)
    {
        edge.setShowEdgeName(showEdgeName);
    }

    public boolean isShowEdgeName()
    {
        return edge.isShowEdgeName();
    }

    public void setIsTotalInhibitorEdge(boolean isTotalInhibitorEdge)
    {
        edge.setIsTotalInhibitorEdge(isTotalInhibitorEdge);
    }

    public boolean isTotalInhibitorEdge()
    {
        return edge.isTotalInhibitorEdge();
    }

    public void setIsPartialInhibitorEdge(boolean isPartialInhibitorEdge)
    {
        edge.setIsPartialInhibitorEdge(isPartialInhibitorEdge);
    }

    public boolean isPartialInhibitorEdge()
    {
        return edge.isPartialInhibitorEdge();
    }

    public void setHasDualArrowHead(boolean hasDualArrowHead)
    {
        edge.setHasDualArrowHead(hasDualArrowHead);
    }

    public boolean hasDualArrowHead()
    {
        return edge.hasDualArrowHead();
    }


}