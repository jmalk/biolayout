package org.BioLayoutExpress3D.Analysis.Blobs;

import java.util.*;

/**
*
* This class represents a very simple counter which can be incremented and decremented.
*
* @author Markus Brosch (mb8[at]sanger[dot]ac[dot]uk)
* @author Full refactoring by Thanos Theo, 2008-2009
*
*/

public class Counter
{
    private int value = 0;
    private Set<String> IDs = new HashSet<String>();

    public Counter()
    {
        value = 0;
    }

    public void increment(String geneID)
    {
        value++;
        IDs.add(geneID);
    }

    public int getValue()
    {
        return value;
    }

    public Set<String> getGeneIDs()
    {
        return IDs;
    }

    @Override
    public String toString()
    {
        return Integer.toString(value);
    }


}