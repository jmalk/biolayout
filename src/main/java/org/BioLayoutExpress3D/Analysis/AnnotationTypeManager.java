package org.BioLayoutExpress3D.Analysis;

import java.util.*;
import org.BioLayoutExpress3D.Analysis.Blobs.*;
import static org.BioLayoutExpress3D.CoreUI.LayoutClasses.*;

/**
*
* This class keeps track of the classes/annotations and their corresponding statistics.
* An annotation-type (e.g. GO) can have various terms (e.g. describing various biological functions).
*
* For each annotation type ALL terms are counted. In addition each distinct term is accounted
* according to its frequency to describe the overall frequency distribution of this term in this annotation type.
*
* @author Markus Brosch (mb8[at]sanger[dot]ac[dot]uk)
* @author Full refactoring by Thanos Theo, 2008-2009
*
*/

public class AnnotationTypeManager
{
    private HashMap<String, AnnotationType> annotationType2Cluster = null;

    public AnnotationTypeManager()
    {
        this.resetAll();
    }

    public void resetAll()
    {
        annotationType2Cluster = new HashMap<String, AnnotationType>();
    }

    public void reset(String annotationType)
    {
        annotationType2Cluster.remove(annotationType);
    }

    public void add(String geneID, String annoTypeName, String term)
    {
        if ( !term.equals(NO_CLASS) )
        {
            AnnotationType as = annotationType2Cluster.get(annoTypeName);

            if (as == null)
            {
                as = new AnnotationType();
                annotationType2Cluster.put(annoTypeName, as);
            }

            as.add(term, geneID);
        }
    }

    public float getP(String annoTypeName, String term)
    {
        AnnotationType ceo = annotationType2Cluster.get(annoTypeName);

        if (ceo == null)
            throw new IllegalArgumentException(annoTypeName + " was not registered before!");

        return ceo.getCount(term);
    }

    public AnnotationType getType(String annoTypeName)
    {
        return annotationType2Cluster.get(annoTypeName);
    }

    public Set<String> getAllTypes()
    {
        return annotationType2Cluster.keySet();
    }

    @Override
    public String toString()
    {
        return annotationType2Cluster.toString();
    }


}