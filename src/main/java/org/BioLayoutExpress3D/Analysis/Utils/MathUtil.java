package org.BioLayoutExpress3D.Analysis.Utils;

import java.util.*;
import static java.lang.Math.*;
import org.BioLayoutExpress3D.Analysis.Blobs.*;

/**
*
* @author Markus Brosch (mb8[at]sanger[dot]ac[dot]uk)
* @author Full refactoring by Thanos Theo, 2008-2009
*
*/

public final class MathUtil
{
    private static final double LOG2_FACTOR = log(2);

    public static double relativeEntropy(AnnotationType selected, AnnotationType wholeChip)
    {
        Set<String> clusterNames = selected.getKeys();
        double entropy = 0.0;
        double actualCluster = 0.0;
        double background = 0.0;
        for (String clusterName : clusterNames)
        {
            actualCluster = selected.getP(clusterName);
            background = wholeChip.getP(clusterName);

            if ( (actualCluster != 0.0) && (background == 0.0) )
                entropy += Integer.MAX_VALUE;
            else if (actualCluster != 0)
                entropy += actualCluster * log(actualCluster / background) / LOG2_FACTOR;
        }

        return entropy;
    }

    public static Map<String, Double> relativeEntropyDetailed(AnnotationType selected, AnnotationType wholeChip)
    {
        if (selected != null)
        {
            Set<String> clusterNames = selected.getKeys();
            Map<String, Double> type2entropy = new HashMap<String, Double>();
            double actualCluster = 0.0;
            double background = 0.0;
            for (String clusterName : clusterNames)
            {
                actualCluster = selected.getP(clusterName);
                background = wholeChip.getP(clusterName);

                if ( (actualCluster != 0.0) && (background == 0.0) )
                    type2entropy.put (clusterName, new Double(Integer.MAX_VALUE) );
                else if (actualCluster != 0.0)
                    type2entropy.put( clusterName, new Double(actualCluster * log(actualCluster / background) / LOG2_FACTOR) );
            }

            return type2entropy;
        }
        else
            return null;
    }

    /**
    * This is an approximation of the gamma function ( for integers it is like factorial(x+1) )<p>
    * Adapted from "Numerical Recipes in C" (Press, Teukolsky, Vetterling, Flannery)
    * @param xx
    * @return the return value of the log gamma function
    */
    public static double gammaLn(double xx)
    {
        double x = 0.0, tmp = 0.0, ser = 0.0;
        double[] cof = {
                         76.18009173,
                        -86.50532033,
                         24.01409822,
                         -1.231739516,
                          0.120858003e-2,
                         -0.536382e-5
                       };

        x = xx - 1.0;
        tmp = x + 5.5;
        tmp -= (x + 0.5) * log(tmp);
        ser = 1.0;

        for (int j = 0; j <= 5; j++)
        {
            x += 1.0;
            ser += cof[j] / x;
        }

        return -tmp + log(2.50662827465 * ser);
    }

    /**
    * log of combination without repetition: log(n!/r!/(n-r)!)<br>
    * To get the value, simply use exp(logComb(n,r));
    * @param n number of objects
    * @param r number to be chosen
    * @return log(n!/r!/(n-r)!)
    */
    public static double logComb(double n, double r)
    {
        return gammaLn(n + 1) - gammaLn(r + 1) - gammaLn(n - r + 1);
    }

    public static double hyperGeometricProb(double x, double r1, double r2, double c1, double c2)
    {
        return exp( logComb(r1, x) + logComb(r2, c1 - x) - logComb( c1 + c2, c1) );
    }

    /**
    * Fisher's exact test (approximation)  <p>
    * data | group 1 | group 2 | combined  <br>
    * Fkt1 | a       | b       | a+b       <br>
    * Fkt2 | c       | d       | c+d       <br>
    * sum  | a+c     | b+d     | a+b+c+d   <br>
    * @param a
    * @param b
    * @param c
    * @param d
    * @return [0]left-sided p-Value, [1]right-sided p-Value, [2]two-sided p-Value
    */
    public static double[] fisher(int a, int b, int c, int d)
    {
        double ab = a + b;
        double cd = c + d;
        double ac = a + c;
        double bd = b + d;

        double leftPval  = 0.0;
        double rightPval = 0.0;
        double twoPval   = 0.0;

        // range of variation
        double lm = (ac < cd) ? 0.0 : ac - cd;
        double um = (ac < ab) ? ac  : ab;

        // Fisher's exact test
        double crit = hyperGeometricProb(a, ab, cd, ac, bd);

        leftPval = rightPval = twoPval = 0.0;
        for (double x = lm; x <= um; x++)
        {
            double prob = hyperGeometricProb(x, ab, cd, ac, bd);
            if (x <= a) leftPval += prob;
            if (x >= a) rightPval += prob;
            if (prob <= crit) twoPval += prob;
        }

        return new double[]{ leftPval, rightPval, twoPval };
    }

    public static double calcScore(double fishersP, int members, double entropy)
    {
        double memberScore = 0.0;
        double fisherScore = 0.0;

        if (members > 5) memberScore = 1.0;
        else if (members > 2) memberScore = 0.4;
        else memberScore = 0.01;

        if (fishersP <= 0.01) fisherScore = 1.0;
        else if (fishersP <= 0.05) fisherScore = 0.75;
        else if (fishersP <= 0.20) fisherScore = 0.25;
        else fisherScore = 0.01;

        return (memberScore * fisherScore * entropy);
    }


}