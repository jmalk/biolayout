package org.BioLayoutExpress3D.StaticLibraries;

import java.util.*;
import java.util.concurrent.atomic.*;
import static java.lang.Math.*;

/**
*
* Random is a final class containing mainly static random wrapper methods, used in many occasions in BioLayoutExpress3D.
* It's using an optimal XORShiftRandom RNG engine courtesy of G. Marsaglia, published in 2003 inside a public static class.
*
* @author Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*/

public final class Random
{

    /**
    *  Reference for the XORShiftRandom static inner class.
    */
    private static final XORShiftRandom XORShiftRandom = new XORShiftRandom( System.nanoTime() );

    /**
    *  random() method of the XORShiftRandom RNG.
    *  Uses the XORShiftRandom static inner class.
    */
    public static double random()
    {
        return XORShiftRandom.nextDouble();
    }

    /**
    *  nextBoolean() method of the XORShiftRandom RNG.
    *  Uses the XORShiftRandom static inner class.
    */
    public static boolean nextBoolean()
    {
	return XORShiftRandom.nextBoolean();
    }

    /**
    *  nextInt() method of the XORShiftRandom RNG.
    *  Uses the XORShiftRandom static inner class.
    */
    public static int nextInt()
    {
	return XORShiftRandom.nextInt();
    }

    /**
    *  nextLong() method of the XORShiftRandom RNG.
    *  Uses the XORShiftRandom static inner class.
    */
    public static long nextLong()
    {
        return XORShiftRandom.nextLong();
    }

    /**
    *  nextFloat() method of the XORShiftRandom RNG.
    *  Uses the XORShiftRandom static inner class.
    */
    public static float nextFloat()
    {
        return XORShiftRandom.nextFloat();
    }

    /**
    *  nextDouble() method of the XORShiftRandom RNG.
    *  Uses the XORShiftRandom static inner class.
    */
    public static double nextDouble()
    {
        return XORShiftRandom.nextDouble();
    }

    /**
    *  Returns the next pseudorandom, Gaussian ("normally") distributed double value with mean 0.0 and standard deviation 1.0 from the random number generator's sequence.
    *  This uses the polar method of G. E. P. Box, M. E. Muller, and G. Marsaglia, as described by Donald E. Knuth in The Art of Computer Programming, Volume 3: Seminumerical Algorithms,
    *  section 3.4.1, subsection C, algorithm P. Note that it generates two independent values at the cost of only one call to log() and one call to sqrt().
    *  Uses the XORShiftRandom static inner class.
    */
    public static double nextGaussian()
    {
        return XORShiftRandom.nextGaussian();
    }

    /**
    *  Integer random number generator between -x and x.
    *  Uses the XORShiftRandom static inner class.
    */
    public static int getRandomNegativePositiveRange(int x)
    {
        return XORShiftRandom.getRandomNegativePositiveRange(x);
    }

    /**
    *  Integer random number generator between 0 and x.
    *  Uses the XORShiftRandom static inner class.
    */
    public static int getRandomPositiveRange(int x)
    {
        return XORShiftRandom.getRandomPositiveRange(x);
    }

    /**
    *  Integer random number generator between minimum and maximum (may also return minimum & maximum).
    *  Uses the XORShiftRandom static inner class.
    */
    public static int getRandomRange(int minimum, int maximum)
    {
        return XORShiftRandom.getRandomRange(minimum, maximum);
    }

    /**
    *  The XORShiftRandom static inner class.
    */
    public static class XORShiftRandom
    {

        /**
        *  Seed value based on System.nanoTime() for the XORShiftRandom RNG.
        */
        private final AtomicLong seed;

        /**
        *  Value needed for nextGaussian() method.
        */
        private double nextNextGaussian = 0.0;

        /**
        *  Value needed for nextGaussian() method.
        */
        private volatile boolean haveNextNextGaussian = false;

        /**
        *  The first constructor of the XORShiftRandom class.
        */
        public XORShiftRandom()
        {
            this.seed = new AtomicLong( System.nanoTime() );
        }

        /**
        *  The second constructor of the XORShiftRandom class.
        */
        public XORShiftRandom(long seed)
        {
            this.seed = new AtomicLong( seed ^ System.nanoTime() );
        }

        /**
        *  The third constructor of the XORShiftRandom class.
        */
        public XORShiftRandom(Object obj)
        {
            this.seed = new AtomicLong( obj.hashCode() ^ System.nanoTime() );
        }

        /**
        *  Core method of the XORShiftRandom RNG.
        */
        private int next(int nbits)
        {
            /*
            // N.B. Not thread-safe!
            long x = seed;
            x ^= (x << 21);
            x ^= (x >>> 35);
            x ^= (x << 4);
            seed = x;
            x &= ( (1L << nbits) -1 );
            return (int)x;
            */

            long oldseed, nextseed;
            AtomicLong localSeed = seed;
            do
            {
                nextseed = oldseed = localSeed.get();
                nextseed ^= (nextseed << 21);
                nextseed ^= (nextseed >>> 35);
                nextseed ^= (nextseed << 4);
            }
            while ( !localSeed.compareAndSet(oldseed, nextseed) );
            return  (int)( nextseed &= ( (1L << nbits) -1 ) );
        }

        /**
        *  random() method of the XORShiftRandom RNG.
        */
        public double random()
        {
            return nextDouble();
        }

        /**
        *  nextBoolean() method of the XORShiftRandom RNG.
        */
        public boolean nextBoolean()
        {
            return next(1) != 0;
        }

        /**
        *  nextInt() method of the XORShiftRandom RNG.
        */
        public int nextInt()
        {
            return next(32);
        }

        /**
        *  nextLong() method of the XORShiftRandom RNG.
        */
        public long nextLong()
        {
            return ( (long)(next(32) ) << 32 ) + next(32);
        }

        /**
        *  nextFloat() method of the XORShiftRandom RNG.
        */
        public float nextFloat()
        {
            return next(24) / ( (float)(1 << 24) );
        }

        /**
        *  nextDouble() method of the XORShiftRandom RNG.
        */
        public double nextDouble()
        {
            return ( ( (long)(next(26) ) << 27) + next(27) ) / (double)(1L << 53);
        }

        /**
        *  Returns the next pseudorandom, Gaussian ("normally") distributed double value with mean 0.0 and standard deviation 1.0 from the random number generator's sequence.
        *  This uses the polar method of G. E. P. Box, M. E. Muller, and G. Marsaglia, as described by Donald E. Knuth in The Art of Computer Programming, Volume 3: Seminumerical Algorithms,
        *  section 3.4.1, subsection C, algorithm P. Note that it generates two independent values at the cost of only one call to log() and one call to sqrt().
        */
        public double nextGaussian()
        {
            if (haveNextNextGaussian)
            {
                haveNextNextGaussian = false;
                return nextNextGaussian;
            }
            else
            {
                double v1 = 0.0;
                double v2 = 0.0;
                double s = 0.0;
                do
                {
                    v1 = 2.0 * nextDouble() - 1.0; // between -1.0 and 1.0
                    v2 = 2.0 * nextDouble() - 1.0; // between -1.0 and 1.0
                    s = (v1 * v1) + (v2 * v2);
                }
                while ( (s >= 1.0) || (s == 0.0) );

                double multiplier = sqrt(-2.0 * log(s) / s);

                nextNextGaussian = v2 * multiplier;
                haveNextNextGaussian = true;

                return (v1 * multiplier);
            }
        }

        /**
        *  Integer random number generator between -x and x.
        */
        public int getRandomNegativePositiveRange(int x)
        {
            return ( ( (int)( 2 * x * nextDouble() ) ) - x );
        }

        /**
        *  Integer random number generator between 0 and x.
        */
        public int getRandomPositiveRange(int x)
        {
            return (int)(100 * x * nextDouble() % x);
        }

        /**
        *  Integer random number generator between minimum and maximum (may also return minimum & maximum).
        */
        public int getRandomRange(int minimum, int maximum)
        {
            return (int)(nextDouble() * (maximum - minimum + 1) + minimum);
        }


    }


    /**
    *  Method to return an arraylist of integers from 0 to length - 1 with random positioned order in the arraylist.
    */
    public static ArrayList<Integer> createRandomIndexIntegerArrayList(int length)
    {
        ArrayList<Integer> returnArrayList = new ArrayList<Integer>(length);
        ArrayList<Integer> secondayArrayList = new ArrayList<Integer>(length);

        for (int i = 0; i < length; i++)
            secondayArrayList.add(i);

        searchIntegerArrayListRandomlyAgain(secondayArrayList, returnArrayList);

        return returnArrayList;
    }

    /**
    *  Recursive search method used in the method above.
    */
    private static void searchIntegerArrayListRandomlyAgain(ArrayList<Integer> secondayArrayList, ArrayList<Integer> returnArrayList)
    {
        if ( !secondayArrayList.isEmpty() )
        {
            int randomIndex = getRandomPositiveRange(secondayArrayList.size() - 1);
            returnArrayList.add(secondayArrayList.get(randomIndex));
            secondayArrayList.remove(randomIndex);
            searchIntegerArrayListRandomlyAgain(secondayArrayList, returnArrayList);
        }
        else // Base Case (BC): when the secondayArrayList arraylist is empty
        {
            return;
        }
    }

    /**
    *  Gets an integer value according to a distribution.
    *  If the distribution is given by the argument vector
    *  <tt>[v0,v2,...vN-1]</tt>, value <tt>I</tt> from
    *  <tt>[0,...N-1]</tt> will be
    *  returned with probability <tt>vI</tt>
    *  @param probabilities Vector of real-valued probabilities (should sum to 1).
    *  @return Index of value in the argument vector randomly chosen
    *  according to distribution given.
    */
    public static int getInteger(double[] probabilities)
    {
        double[] accumulate = new double[probabilities.length];
        double sum = 0.0;
        XORShiftRandom localXORShiftRandom = new XORShiftRandom( Double.doubleToLongBits( random() ) );
        double randomNumber = localXORShiftRandom.nextDouble();
        for (int i = 0; i < probabilities.length; i++)
        {
            sum += probabilities[i];
            accumulate[i] = sum;
            if (randomNumber <= accumulate[i])
            return i;
        }

        return (probabilities.length - 1);
    }

    /**
    *  Gets a random integer value between two boundary integer values
    *  with uniform distribution. The closed interval between these
    *  two values is assumed to be included, i.e. boundary values
    *  themselves might be picked.
    *  @param lower Lower bound of returned random value.
    *  @param upper Upper bound of returned random value.
    *  @return Random integer value between bounds.
    *  @see Random#getDouble
    */
    public static int getInteger(int lower, int upper)
    {
        XORShiftRandom localXORShiftRandom = new XORShiftRandom( Double.doubleToLongBits( random() ) );
        return ( lower + (int)round(localXORShiftRandom.nextDouble() * ( abs(upper + 1 - lower) ), 0) );
    }

    /**
    *  Gets a random double value between two boundary double values
    *  with uniform distribution. The closed interval between these
    *  two values is assumed to be included, i.e. boundary values
    *  themselves might be picked.
    *  @param lower Lower bound of returned random value.
    *  @param upper Upper bound of returned random value.
    *  @return Random value between bounds.
    *  @see Random#getInteger
    */
    public static double getDouble(double lower, double upper)
    {
        XORShiftRandom localXORShiftRandom = new XORShiftRandom( Double.doubleToLongBits( random() ) );
        return ( lower + localXORShiftRandom.nextDouble() * ( abs(upper - lower) ) );
    }

    /**
    *  Returns a random permutation of n elements (labeled 0 to n - 1)
    */
    public static int[] randomPermutation(int n)
    {
        int[] result = new int[n];

        // create identity...
        for (int i = 0; i < n; i++)
            result[i] = i;

        // ...and permute
        for (int i = 0; i < n; i++)
        {
            //swap i with a random element between i and n - 1
            int j = getInteger(i, n - 1);
            int temp = result[i];
            result[i] = result[j];
            result[j] = temp;
        }

        return result;
    }

    /**
    *  Round a double number to a given number of digits.
    */
    public static double round(double number, int digits)
    {
        return  ( (double)( (long)( number * pow(10.0, (double)digits) ) ) ) / pow(10.0, (double)digits);
    }


}