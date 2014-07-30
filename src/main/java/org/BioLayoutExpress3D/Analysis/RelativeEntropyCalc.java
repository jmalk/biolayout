package org.BioLayoutExpress3D.Analysis;

import java.util.*;
import org.BioLayoutExpress3D.Analysis.Blobs.*;
import org.BioLayoutExpress3D.Analysis.Utils.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.Network.*;
import static java.lang.Math.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* RelativeEntropyCalc provides statistical information. Due to complex calculations, it usually is usud inside a Runnable/Thread.
* The abortThread variable is used to silently abort that Runnable/Thread.
*
* @author Markus Brosch (mb8[at]sanger[dot]ac[dot]uk)
* @author Full refactoring by Thanos Theo, 2008-2009-2010-2011
*
*/

public class RelativeEntropyCalc
{

    private static final int NUMBER_OF_TRIALS = 1000;

    private NetworkContainer nc = null;
    private LayoutClassSetsManager layoutClassSetsManager = null;
    private Random random = null;

    private static transient Set<String> allGenes = null;
    private static transient String storedTypeName = null;
    private static transient AnnotationType cachedAnnotationType = null;

    /**
    *  The abortThread variable is used to silently abort a Runnable/Thread.
    */
    private volatile boolean abortThread = false;

    public RelativeEntropyCalc(NetworkContainer nc)
    {
        this.nc = nc;

        layoutClassSetsManager = nc.getLayoutClassSetsManager();
        random = new Random();
    }

    public Map<String, HashMap<String, String>> overRepForEachCluster(Set<String> genes, String typeName)
    {
        if (DEBUG_BUILD) println("OverRep Test for:" + genes.size() + " genes");

        HashMap<String, HashMap<String, String>> clusterNames2OverRep = new HashMap<String, HashMap<String, String>>();

        HashMap<String, String> Observed      = new HashMap<String, String>();
        HashMap<String, String> Expected      = new HashMap<String, String>();
        HashMap<String, String> ExpectedTrial = new HashMap<String, String>();
        HashMap<String, String> Fobs          = new HashMap<String, String>();
        HashMap<String, String> Fexp          = new HashMap<String, String>();
        HashMap<String, String> OverRep       = new HashMap<String, String>();
        HashMap<String, String> Zscore        = new HashMap<String, String>();

        AnnotationType bg = AnnotationTypeManagerBG.getInstanceSingleton().getType(typeName);
        AnnotationType annotationType = getCache(typeName, genes);

        if (abortThread) return null;

        int chipGenes = AnnotationTypeManagerBG.getInstanceSingleton().getChipGeneCount();
        int n = 0;
        int selectedInCategory = 0;
        int r1 = 0;
        double fobs = 0.0;
        double fexp = 0.0;
        double overRep = 0.0;
        double[] stdevs = null;
        double expectedNo = 0.0;
        double expectedDev = 0.0;
        double expectedOverrep = 0.0;
        double zScore = 0.0;

        if (abortThread) return null;

        for ( String clusterName : annotationType.getKeys() )
        {
            if (abortThread) return null;
            if (DEBUG_BUILD) println("Cluster:>" + clusterName + "<" + " " + genes.size());

            n = chipGenes;
            selectedInCategory = annotationType.getCount(clusterName);
            r1 = bg.getCount(clusterName);
            fobs = (double) selectedInCategory / (double) genes.size();
            fexp = (double) r1 / (double) n;
            overRep = fobs / fexp;
            stdevs = doRandomSampling(genes.size(), fexp);

            if (abortThread) return null;

            if (DEBUG_BUILD) println("STDEVS:" + stdevs[0] + " " + stdevs[1]);

            expectedNo = round((((double) r1 / (double) n) *(double) genes.size()), 5);
            expectedDev = round(((stdevs[0]) * (double) genes.size()), 5);
            expectedOverrep = stdevs[3];
            zScore = (overRep - expectedOverrep) / stdevs[1];

            if (DEBUG_BUILD)
            {
                println("OverRep:" + overRep + " " + " Expected:" + expectedOverrep + " Std:" + stdevs[1] + " Zscore:" + zScore + "\n" +
                        clusterName + " " + expectedNo + " " + expectedDev + " " + expectedOverrep);
            }

            Observed.put(clusterName, selectedInCategory + "/" + genes.size());
            Expected.put(clusterName, r1 + "/" + n);
            ExpectedTrial.put(clusterName, expectedNo + "/" + genes.size() + "±" + expectedDev);
            Fobs.put(clusterName, Double.toString(fobs));
            Fexp.put(clusterName, Double.toString(fexp));
            OverRep.put(clusterName, overRep + "±" + stdevs[1]);
            Zscore.put(clusterName, Double.toString(zScore));
        }

        clusterNames2OverRep.put("Obs", Observed);
        clusterNames2OverRep.put("Exp", Expected);
        clusterNames2OverRep.put("Fobs", Fobs);
        clusterNames2OverRep.put("Fexp", Fexp);
        clusterNames2OverRep.put("OverRep", OverRep);
        clusterNames2OverRep.put("ExpT", ExpectedTrial);
        clusterNames2OverRep.put("Zscore", Zscore);

        return clusterNames2OverRep;
    }

    public Map<String, Double> relEntropy4Selection(Set<String> genes, String typeName)
    {
        AnnotationType bg = AnnotationTypeManagerBG.getInstanceSingleton().getType(typeName);
        AnnotationType annotationType = getCache(typeName, genes);

        if (abortThread) return null;
        if (DEBUG_BUILD) println("Running on:" + typeName + " Genes:" + genes.size());

        return MathUtil.relativeEntropyDetailed(annotationType, bg);
    }

    public Map<String, Double> fisherTestForEachCluster(Set<String> genes, String typeName)
    {
        if (DEBUG_BUILD) println("Fisher Test for:" + genes.size() + " genes");

        HashMap<String, Double> clusterNames2FisherValues = new HashMap<String, Double>();

        AnnotationType bg = AnnotationTypeManagerBG.getInstanceSingleton().getType(typeName);
        AnnotationType annotationType = getCache(typeName, genes);

        if (abortThread) return null;

        int chipGenes = AnnotationTypeManagerBG.getInstanceSingleton().getChipGeneCount();
        int n = 0;
        int selectedInCategory = 0;
        int r1 = 0;
        int nonSelectedInCategory = 0;
        int c1 = 0;
        int selectedNotInCategory = 0;
        int c2 =0;
        int nonSelectedNotInCategory = 0;
        double fobs = 0.0;
        double fexp = 0.0;
        double overRep = 0.0;
        double f = 0.0;
        for ( String clusterName : annotationType.getKeys() )
        {
            if (abortThread) return null;
            if (DEBUG_BUILD) println("Cluster:>" + clusterName + "<" + " " + genes.size());

            n = chipGenes;
            selectedInCategory = annotationType.getCount(clusterName);
            r1 = bg.getCount(clusterName);
            nonSelectedInCategory = r1 - selectedInCategory;
            c1 = genes.size();
            selectedNotInCategory = c1 - selectedInCategory;
            c2 = n - c1;
            nonSelectedNotInCategory = c2 - nonSelectedInCategory;
            fobs = (double) selectedInCategory / (double)genes.size();
            fexp = (double) r1 / (double)chipGenes;
            overRep = fobs / fexp;

            if (DEBUG_BUILD)
            {
                println("Fobs:" + fobs + " Fexp: " + fexp + " OverRep: " + overRep);
                println("category\tselected\tnotSelec\tsum");
                println(clusterName + "\t" + selectedInCategory + "\t" + nonSelectedInCategory + "\t" + r1);
                println("-|" + clusterName + "\t" + selectedNotInCategory + "\t" + nonSelectedNotInCategory);
                println("sum\t" + c1 + "\t" + c2 + "\t" + n);
            }

            f = MathUtil.fisher(selectedInCategory, nonSelectedInCategory, selectedNotInCategory, nonSelectedNotInCategory)[2];

            if (DEBUG_BUILD) println("Fisher:" + f);
            clusterNames2FisherValues.put(clusterName, new Double(f));
        }

        return clusterNames2FisherValues;
    }

    public Map<String, Integer> clusterMembers(Set<String> genes, String typeName)
    {
        HashMap<String, Integer> clusterNames2MembersCount = new HashMap<String, Integer>();
        AnnotationType annotationType = getCache(typeName, genes);

        if (abortThread) return null;

        for ( String clusterName : annotationType.getKeys() )
        {
            if (abortThread) return null;
            clusterNames2MembersCount.put(clusterName, annotationType.getCount(clusterName)); // Integer autoboxing
        }

        return clusterNames2MembersCount;
    }

    //Gene names as strings or GraphNodes
    public Map<String, Double> relEntropy4Selection(Set<String> genes)
    {
        Map<String, Double> stringType2DoubleEntropy = new HashMap<String, Double>();
        AnnotationTypeManager subTypes4clusterName = getSubType(genes);

        if (abortThread) return null;

        AnnotationType annotationType = null;
        AnnotationType tm = null;
        double relEntropy = 0.0;
        for ( String typeName : subTypes4clusterName.getAllTypes() )
        {
            if (abortThread) return null;
            annotationType = subTypes4clusterName.getType(typeName);
            tm = AnnotationTypeManagerBG.getInstanceSingleton().getType(typeName);
            if (tm != null)
            {   // because there are now more classes/types than on the original chip/dataset (e.g. MCL)
                relEntropy = MathUtil.relativeEntropy(annotationType, tm);
                stringType2DoubleEntropy.put( typeName, new Double(relEntropy) );
            }
        }

        return stringType2DoubleEntropy;
    }

    private AnnotationTypeManager getSubType(Set<String> genes)
    {
        AnnotationTypeManager annotationTypeManager = new AnnotationTypeManager();

        String gene = "";
        VertexClass vc = null;
        for ( Vertex vertex : nc.getVertices() )
        {
            if (abortThread) return null;

            gene = vertex.getVertexName();
            if ( genes.contains(gene) )
            {
                for ( LayoutClasses layoutClass : layoutClassSetsManager.getClassSetNames() )
                {
                    vc = layoutClass.getVertexClass(vertex);
                    if (vc != null)
                        annotationTypeManager.add( gene, layoutClass.getClassSetName(), vc.getName() );
                }
            }
        }

        return annotationTypeManager;
    }

    private void setCache(Set<String> genes, String typeName, AnnotationType annotationType)
    {
        allGenes = genes;
        storedTypeName = typeName;
        cachedAnnotationType = annotationType;
    }

    private AnnotationType getCache(String typeName, Set<String> genes)
    {
        if (allGenes != null && storedTypeName != null && cachedAnnotationType != null)
            if ( storedTypeName.equals(typeName) && allGenes.equals(genes) )
                return cachedAnnotationType;

        AnnotationTypeManager subTypes4clusterName = getSubType(genes);

        if (abortThread) return null;

        AnnotationType annotationType = subTypes4clusterName.getType(typeName);
        setCache(genes, typeName, annotationType);

        return annotationType;
    }

    private double[] doRandomSampling(int totalGenes, double expectedFrequency)
    {
        double[] trialObs = new double[NUMBER_OF_TRIALS];
        double[] trialOverRep = new double[NUMBER_OF_TRIALS];
        double   trialObsAvg = 0;
        double   trialOverRepAvg = 0;
        double   trialObsStdev = 0;
        double   trialOverRepStdev = 0;

        for (int i = 0; i < NUMBER_OF_TRIALS; i++)
        {
            if (abortThread) return null;

            int hits = 0;
            for (int j = 0; j < totalGenes; j++)
                if (random.nextDouble() <= expectedFrequency)
                    hits++;

            if (DEBUG_BUILD) println("Got " + hits + " hits");

            trialObs[i] = hits / (double)totalGenes;
            trialOverRep[i] = trialObs[i] / expectedFrequency;
            trialObsAvg += trialObs[i];
            trialOverRepAvg += trialOverRep[i];
        }

        trialObsAvg = trialObsAvg / (double)NUMBER_OF_TRIALS;
        trialOverRepAvg = trialOverRepAvg / (double)NUMBER_OF_TRIALS;

        if (DEBUG_BUILD) println("Observed a Freq of:" + trialObsAvg + " Over Rep of:" + trialOverRepAvg);

        for (int i = 0; i < NUMBER_OF_TRIALS; i++)
        {
            if (abortThread) return null;

            trialObsStdev += (trialObs[i] - trialObsAvg) * (trialObs[i] - trialObsAvg);
            trialOverRepStdev += (trialOverRep[i] - trialOverRepAvg) * (trialOverRep[i] - trialOverRepAvg);
        }

        trialObsStdev = sqrt(trialObsStdev / (double)NUMBER_OF_TRIALS);
        trialOverRepStdev = sqrt(trialOverRepStdev / (double)NUMBER_OF_TRIALS);

        if (DEBUG_BUILD) println("STDEVS>>" + trialObsStdev + " " + trialOverRepStdev);

        return new double[]{ trialObsStdev, trialOverRepStdev, trialObsAvg, trialOverRepAvg };
    }

    private double round(double val, int precision)
    {
        // Multiply by 10 to the power of precision and add 0.5 for rounding up
        // Take the nearest integer smaller than this value
        // Divide it by 10**precision to get the rounded value
        return  floor(val * pow(10, precision) + 0.5) / pow(10, precision);
    }

    public void setAbortThread(boolean abortThread)
    {
        this.abortThread = abortThread;
    }


}