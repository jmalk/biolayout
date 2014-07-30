package org.BioLayoutExpress3D.Network;

/**
*
*  org.BioLayoutExpress3D.Network.Edge
*
*  Created by CGG EBI on Wed Aug 07 2002.
*
* @author Full refactoring by Thanos Theo, 2008-2009
* @version 3.0.0.0
*
*/

public final class Edge
{
    private String edgeName = null; // initialize the string edgeName to null so as to not use any RAM for expression analysis that uses many non-named edges
    private Vertex firstVertex = null;
    private Vertex secondVertex = null;

    private float weight = 1.0f;
    private float normalisedWeight = 1.0f;
    private float scaledWeight = 0.0f;
    private byte packedBooleanFlags = 0; // packed boolean flags so as to not use 1 byte per boolean for the edge

    public Edge(Vertex firstVertex, Vertex secondVertex, float weight)
    {
        this.firstVertex = firstVertex;
        this.secondVertex = secondVertex;
        this.weight = weight;
    }

    public Edge(String edgeName, Vertex firstVertex, Vertex secondVertex, float weight, boolean isTotalInhibitorEdge, boolean isPartialInhibitorEdge, boolean hasDualArrowHead)
    {
        this.edgeName = edgeName;
        this.firstVertex = firstVertex;
        this.secondVertex = secondVertex;
        this.weight = weight;

        setIsTotalInhibitorEdge(isTotalInhibitorEdge);
        setIsPartialInhibitorEdge(isPartialInhibitorEdge);
        setHasDualArrowHead(hasDualArrowHead);
    }

    public void setEdgeName(String edgeName)
    {
        this.edgeName = edgeName;
    }

    public String getEdgeName()
    {
        return edgeName;
    }

    public Vertex getFirstVertex()
    {
        return firstVertex;
    }

    public Vertex getSecondVertex()
    {
        return secondVertex;
    }

    public void setWeight (float weight)
    {
        this.weight = weight;
    }

    public float getWeight()
    {
        return weight;
    }

    public void setNormalisedWeight(float normalisedWeight)
    {
        this.normalisedWeight = normalisedWeight;
    }

    public float getNormalisedWeight()
    {
        return normalisedWeight;
    }

    public void setScaledWeight(float scaledWeight)
    {
        this.scaledWeight = scaledWeight;
    }

    public float getScaledWeight()
    {
        return scaledWeight;
    }

    public void setShowEdgeName(boolean showEdgeName)
    {
        if (showEdgeName)
            packedBooleanFlags |= 1; //(1 << 0); // set to true
        else
        {
            if ( isShowEdgeName() ) // skip if true already to avoid XOR set errors
                packedBooleanFlags ^= 1; //(1 << 0);
        }
    }

    public boolean isShowEdgeName()
    {
        return (packedBooleanFlags & 1) == 1; //( (packedBooleanFlags >> 0) & 1 ) == 1;
    }

    public void setIsTotalInhibitorEdge(boolean isTotalInhibitorEdge)
    {
        if (isTotalInhibitorEdge)
            packedBooleanFlags |= (1 << 1); // set to true
        else
        {
            if ( isTotalInhibitorEdge() ) // skip if true already to avoid XOR set errors
                packedBooleanFlags ^= (1 << 1);
        }
    }

    public boolean isTotalInhibitorEdge()
    {
        return ( (packedBooleanFlags >> 1) & 1 ) == 1;
    }

    public void setIsPartialInhibitorEdge(boolean isPartialInhibitorEdge)
    {
        if (isPartialInhibitorEdge)
            packedBooleanFlags |= (1 << 2); // set to true
        else
        {
            if ( isPartialInhibitorEdge() ) // skip if true already to avoid XOR set errors
                packedBooleanFlags ^= (1 << 2);
        }
    }

    public boolean isPartialInhibitorEdge()
    {
        return ( (packedBooleanFlags >> 2) & 1 ) == 1;
    }

    public void setHasDualArrowHead(boolean hasDualArrowHead)
    {
        if (hasDualArrowHead)
            packedBooleanFlags |= (1 << 3); // set to true
        else
        {
            if ( hasDualArrowHead() ) // skip if true already to avoid XOR set errors
                packedBooleanFlags ^= (1 << 3);
        }
    }

    public boolean hasDualArrowHead()
    {
        return ( (packedBooleanFlags >> 3) & 1 ) == 1;
    }


}