package org.BioLayoutExpress3D.Analysis.Blobs;

import java.util.*;

/**
*
* This class counts equal Objects of type T. Also the frequency/probability of a certain object is computed.
*
* @author Markus Brosch (mb8[at]sanger[dot]ac[dot]uk)
* @author Full refactoring by Thanos Theo, 2008-2009
*
*/

public class AnnotationType
{
    private HashMap<String, Counter> anno2count = new HashMap<String, Counter>();
    private double overallCount = 0;

    /**
    * This method adds an Element of type T and will be accounted.
    *
    * @param clusterName Object of type T which is counted.
    */
    public void add(String clusterName, String id)
    {
        if ( clusterName.trim().equals("") )
            return;

        Counter counter = anno2count.get(clusterName);
        if (counter == null)
        {
            counter = new Counter();
            anno2count.put(clusterName, counter);
        }

        counter.increment(id);
        overallCount++;
    }

    /**
    * This method returns how often the certain Object clusterName was counted/added.
    *
    * @param clusterName Interested Object
    * @return How often this Object clusterName was counted.
    */
    public int getCount(String clusterName)
    {
        Counter si = anno2count.get(clusterName);
        if (si == null)
            return 0;

        return si.getValue();
    }

    /**
    * Get the probability of this Object to be emitted within the collection of all added and accounted Objects.
    *
    * @param clusterName Interested Object
    * @return count of Object clusterName divided by the total number of accounted objects.
    */
    public double getP(String clusterName)
    {
        return this.getCount(clusterName) / overallCount;
    }

    public Set<String> getIDs(String clusterName)
    {
        Counter si = anno2count.get(clusterName);

        if (si == null)
            return new HashSet<String>();

        return si.getGeneIDs();
    }

    public Set<String> getKeys()
    {
        return anno2count.keySet();
    }

    @Override
    public String toString()
    {
        Counter counter = null;
        StringBuilder sb = new StringBuilder();
        for ( String key : anno2count.keySet() )
        {
            counter = anno2count.get(key);
            sb.append("\nkey:").append(key).append(" counter:").append(counter.toString());
        }

        return sb.toString();
    }


}