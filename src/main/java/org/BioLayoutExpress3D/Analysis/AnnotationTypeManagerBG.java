package org.BioLayoutExpress3D.Analysis;

/**
*
* This extended AnnotationTypeManager is supposed to work on the background frequency
* of annotation terms of the whole chip.
*
* This data is loaded once into the application and is represented by this singleton object.
*
* @author Markus Brosch (mb8[at]sanger[dot]ac[dot]uk)
* @author Full refactoring by Thanos Theo, 2008-2009
*
*/

public class AnnotationTypeManagerBG extends AnnotationTypeManager
{
    private static AnnotationTypeManagerBG instance = null;
    private int chipGeneCount = 0;

    private AnnotationTypeManagerBG()
    {
        super();
    }

    public static AnnotationTypeManagerBG getInstanceSingleton()
    {
        if (instance == null)
            instance = new AnnotationTypeManagerBG();

        return instance;
    }

    public void setChipGeneCount(int chipGeneCount)
    {
        this.chipGeneCount = chipGeneCount;
    }

    public int getChipGeneCount()
    {
        return chipGeneCount;
    }


}