package org.BioLayoutExpress3D.StaticLibraries;

import java.util.*;
import static java.lang.Math.*;

/**
*
* Math is a final class containing only static methods for some mathematical calculations.
*
* @author Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*/

public final class Math
{

    /**
    *  Returns an integer min/max number from 2 given ones.
    */
    public static int findMinMaxFromNumbers(int number1, int number2, boolean chooseMinMax)
    {
        // Min case - Max case
        return (chooseMinMax) ? ( (number1 < number2) ? number1 : number2 ) : ( (number1 > number2) ? number1 : number2 );
    }

    /**
    *  Returns a long min/max number from 2 given ones.
    */
    public static long findMinMaxFromNumbers(long number1, long number2, boolean chooseMinMax)
    {
        // Min case - Max case
        return (chooseMinMax) ? ( (number1 < number2) ? number1 : number2 ) : ( (number1 > number2) ? number1 : number2 );
    }

    /**
    *  Returns a float min/max number from 2 given ones.
    */
    public static float findMinMaxFromNumbers(float number1, float number2, boolean chooseMinMax)
    {
        // Min case - Max case
        return (chooseMinMax) ? ( (number1 < number2) ? number1 : number2 ) : ( (number1 > number2) ? number1 : number2 );
    }

    /**
    *  Returns a double min/max number from 2 given ones.
    */
    public static double findMinMaxFromNumbers(double number1, double number2, boolean chooseMinMax)
    {
        // Min case - Max case
        return (chooseMinMax) ? ( (number1 < number2) ? number1 : number2 ) : ( (number1 > number2) ? number1 : number2 );
    }

    /**
    *  Returns an integer min/max number from 4 integer given ones.
    */
    public static int findMinMaxFromNumbers(int number1, int number2, int number3, int number4, boolean chooseMinMax)
    {
        int[] allFourNumbers = { number1, number2, number3, number4 };
        int minMax = allFourNumbers[0];

        if (chooseMinMax)
        {
            for (int i = 1; i < 4; i++)
                if (minMax > allFourNumbers[i])
                    minMax = allFourNumbers[i]; // Min case
        }
        else
        {
            for (int i = 1; i < 4; i++)
                if (minMax < allFourNumbers[i])
                    minMax = allFourNumbers[i]; // Max case
        }

        return minMax;
    }

    /**
    *  Returns a long min/max number from 4 integer given ones.
    */
    public static long findMinMaxFromNumbers(long number1, long number2, long number3, long number4, boolean chooseMinMax)
    {
        long[] allFourNumbers = { number1, number2, number3, number4 };
        long minMax = allFourNumbers[0];

        if (chooseMinMax)
        {
            for (int i = 1; i < 4; i++)
                if (minMax > allFourNumbers[i])
                    minMax = allFourNumbers[i]; // Min case
        }
        else
        {
            for (int i = 1; i < 4; i++)
                if (minMax < allFourNumbers[i])
                    minMax = allFourNumbers[i]; // Max case
        }

        return minMax;
    }

    /**
    *  Returns a float min/max number from 4 float given ones.
    */
    public static float findMinMaxFromNumbers(float number1, float number2, float number3, float number4, boolean chooseMinMax)
    {
        float[] allFourNumbers = { number1, number2, number3, number4 };
        float minMax = allFourNumbers[0];

        if (chooseMinMax)
        {
            for (int i = 1; i < 4; i++)
                if (minMax > allFourNumbers[i])
                    minMax = allFourNumbers[i]; // Min case
        }
        else
        {
            for (int i = 1; i < 4; i++)
                if (minMax < allFourNumbers[i])
                    minMax = allFourNumbers[i]; // Max case
        }

        return minMax;
    }

    /**
    *  Returns a double min/max number from 4 double given ones.
    */
    public static double findMinMaxFromNumbers(double number1, double number2, double number3, double number4, boolean chooseMinMax)
    {
        double[] allFourNumbers = { number1, number2, number3, number4 };
        double minMax = allFourNumbers[0];

        if (chooseMinMax)
        {
            for (int i = 1; i < 4; i++)
                if (minMax > allFourNumbers[i])
                    minMax = allFourNumbers[i]; // Min case
        }
        else
        {
            for (int i = 1; i < 4; i++)
                if (minMax < allFourNumbers[i])
                    minMax = allFourNumbers[i]; // Max case
        }

        return minMax;
    }

    /**
    *  Returns the min/max number from a given arraylist of integers.
    */
    public static int findMinMaxNumberListInteger(List<Integer> list, boolean chooseMinMax)
    {
        if ( list.isEmpty() )
            return 0;

        int minMax = list.get(0);

        if (chooseMinMax)
        {
            for (Integer number : list)
                if (minMax > number)
                    minMax = number; // Min case
        }
        else
        {
            for (Integer number : list)
                if (minMax < number)
                    minMax = number; // Max case
        }

        return minMax;
    }

    /**
    *  Returns the min/max number from a given arraylist of longs.
    */
    public static long findMinMaxNumberListLong(List<Long> list, boolean chooseMinMax)
    {
        if ( list.isEmpty() )
            return 0;

        long minMax = list.get(0);

        if (chooseMinMax)
        {
            for (Long number : list)
                if (minMax > number)
                    minMax = number; // Min case
        }
        else
        {
            for (Long number : list)
                if (minMax < number)
                    minMax = number; // Max case
        }

        return minMax;
    }

    /**
    *  Returns the min/max number from a given arraylist of floats.
    */
    public static float findMinMaxNumberListFloat(List<Float> list, boolean chooseMinMax)
    {
        if ( list.isEmpty() )
            return 0.0f;

        float minMax = list.get(0);

        if (chooseMinMax)
        {
            for (Float number : list)
                if (minMax > number)
                    minMax = number; // Min case
        }
        else
        {
            for (Float number : list)
                if (minMax < number)
                    minMax = number; // Max case
        }

        return minMax;
    }

    /**
    *  Returns the min/max number from a given arraylist of doubles.
    */
    public static double findMinMaxNumberListDouble(List<Double> list, boolean chooseMinMax)
    {
        if ( list.isEmpty() )
            return 0.0;

        double minMax = list.get(0);

        if (chooseMinMax)
        {
            for (Double number : list)
                if (minMax > number)
                    minMax = number; // Min case
        }
        else
        {
            for (Double number : list)
                if (minMax < number)
                    minMax = number; // Max case
        }

        return minMax;
    }

    /**
    *  Returns how many times an integer min/max number from a given arraylist of integers has been visited.
    */
    public static int findMinMaxNumberHowManyTimesListInteger(List<Integer> list, boolean chooseMinMax)
    {
        if ( list.isEmpty() )
            return 0;

        int howManyTimes = 0;
        int minMax = list.get(0);

        if (chooseMinMax)
        {
            for (Integer number : list)
            {
                if (minMax > number)
                {
                    minMax = number; // Min case
                    howManyTimes = 1;
                }
                else if (minMax == number)
                    howManyTimes++;
            }
        }
        else
        {
            for (Integer number : list)
            {
                if (minMax < number)
                {
                    minMax = number; // Max case
                    howManyTimes = 1;
                }
                else if (minMax == number)
                    howManyTimes++;
            }
        }

        return howManyTimes;
    }

    /**
    *  Returns how many times a long min/max number from a given arraylist of longs has been visited.
    */
    public static int findMinMaxNumberHowManyTimesListLong(List<Long> list, boolean chooseMinMax)
    {
        if ( list.isEmpty() )
            return 0;

        int howManyTimes = 0;
        long minMax = list.get(0);

        if (chooseMinMax)
        {
            for (Long number : list)
            {
                if (minMax > number)
                {
                    minMax = number; // Min case
                    howManyTimes = 1;
                }
                else if (minMax == number)
                    howManyTimes++;
            }
        }
        else
        {
            for (Long number : list)
            {
                if (minMax < number)
                {
                    minMax = number; // Max case
                    howManyTimes = 1;
                }
                else if (minMax == number)
                    howManyTimes++;
            }
        }

        return howManyTimes;
    }

    /**
    *  Returns how many times a float min/max number from a given arraylist of floats has been visited.
    */
    public static int findMinMaxNumberHowManyTimesListFloat(List<Float> list, boolean chooseMinMax)
    {
        if ( list.isEmpty() )
            return 0;

        int howManyTimes = 0;
        float minMax = list.get(0);

        if (chooseMinMax)
        {
            for (Float number : list)
            {
                if (minMax > number)
                {
                    minMax = number; // Min case
                    howManyTimes = 1;
                }
                else if (minMax == number)
                    howManyTimes++;
            }
        }
        else
        {
            for (Float number : list)
            {
                if (minMax < number)
                {
                    minMax = number; // Max case
                    howManyTimes = 1;
                }
                else if (minMax == number)
                    howManyTimes++;
            }
        }

        return howManyTimes;
    }

    /**
    *  Returns how many times a double min/max number from a given arraylist of doubles has been visited.
    */
    public static int findMinMaxNumberHowManyTimesListDouble(List<Double> list, boolean chooseMinMax)
    {
        if ( list.isEmpty() )
            return 0;

        int howManyTimes = 0;
        double minMax = list.get(0);

        if (chooseMinMax)
        {
            for (Double number : list)
            {
                if (minMax > number)
                {
                    minMax = number; // Min case
                    howManyTimes = 1;
                }
                else if (minMax == number)
                    howManyTimes++;
            }
        }
        else
        {
            for (Double number : list)
            {
                if (minMax < number)
                {
                    minMax = number; // Max case
                    howManyTimes = 1;
                }
                else if (minMax == number)
                    howManyTimes++;
            }
        }

        return howManyTimes;
    }

    /**
    *  Returns how many times a min/max number from a given arraylist of integers has been visited and that min/max.
    */
    public static int[] findMinMaxNumberAndHowManyTimesListInteger(List<Integer> list, boolean chooseMinMax)
    {
        if ( list.isEmpty() )
            return new int[] { 0, 0 };

        int howManyTimes = 0;
        int minMax = list.get(0);

        if (chooseMinMax)
        {
            for (Integer number : list)
            {
                if (minMax > number)
                {
                    minMax = number; // Min case
                    howManyTimes = 1;
                }
                else if (minMax == number)
                    howManyTimes++;
            }
        }
        else
        {
            for (Integer number : list)
            {
                if (minMax < number)
                {
                    minMax = number; // Max case
                }
                else if (minMax == number)
                    howManyTimes++;
            }
        }

        return new int[] { howManyTimes, minMax };
    }

    /**
    *  Returns how many times a min/max number from a given arraylist of longs has been visited and that min/max.
    */
    public static long[] findMinMaxNumberAndHowManyTimesListLong(List<Long> list, boolean chooseMinMax)
    {
        if ( list.isEmpty() )
            return new long[] { 0, 0 };

        int howManyTimes = 0;
        long minMax = list.get(0);

        if (chooseMinMax)
        {
            for (Long number : list)
            {
                if (minMax > number)
                {
                    minMax = number; // Min case
                    howManyTimes = 1;
                }
                else if (minMax == number)
                    howManyTimes++;
            }
        }
        else
        {
            for (Long number : list)
            {
                if (minMax < number)
                {
                    minMax = number; // Max case
                }
                else if (minMax == number)
                    howManyTimes++;
            }
        }

        return new long[] { howManyTimes, minMax };
    }

    /**
    *  Returns how many times a min/max number from a given arraylist of floats has been visited and that min/max.
    */
    public static float[] findMinMaxNumberAndHowManyTimesListFloat(List<Float> list, boolean chooseMinMax)
    {
        if ( list.isEmpty() )
            return new float[] { 0.0f, 0.0f };

        int howManyTimes = 0;
        float minMax = list.get(0);

        if (chooseMinMax)
        {
            for (Float number : list)
            {
                if (minMax > number)
                {
                    minMax = number; // Min case
                    howManyTimes = 1;
                }
                else if (minMax == number)
                    howManyTimes++;
            }
        }
        else
        {
            for (Float number : list)
            {
                if (minMax < number)
                {
                    minMax = number; // Max case
                }
                else if (minMax == number)
                    howManyTimes++;
            }
        }

        return new float[] { howManyTimes, minMax };
    }

    /**
    *  Returns how many times a min/max number from a given arraylist of integers has been visited and that min/max.
    */
    public static double[] findMinMaxNumberAndHowManyTimesListDouble(List<Double> list, boolean chooseMinMax)
    {
        if ( list.isEmpty() )
            return new double[] { 0.0, 0.0 };

        int howManyTimes = 0;
        double minMax = list.get(0);

        if (chooseMinMax)
        {
            for (Double number : list)
            {
                if (minMax > number)
                {
                    minMax = number; // Min case
                    howManyTimes = 1;
                }
                else if (minMax == number)
                    howManyTimes++;
            }
        }
        else
        {
            for (Double number : list)
            {
                if (minMax < number)
                {
                    minMax = number; // Max case
                }
                else if (minMax == number)
                    howManyTimes++;
            }
        }

        return new double[] { howManyTimes, minMax };
    }

    /**
    *  Returns if a given value is a power of two using a fast bitshift check.
    */
    public static boolean isPowerOfTwo(int n)
    {
        return ( (n & -n) == n );
    }

    /**
    *  Calculates the next power-of-two integer from a given integer.
    */
    public static int getNextPowerOfTwo(int number)
    {
        if ( isPowerOfTwo(number) )
        {
            return number;
        }
        else
        {
            String bits = Integer.toBinaryString(number);
            return (int)pow( 2, bits.length() - bits.indexOf("1") );
        }
    }

    /**
    *  Formula of total triangular matrix calculations needed.
    */
    public static long totalTriangularMatrixCalculationsNeeded(long number)
    {
        return ( number * (number - 1) ) / 2;
    }

    /**
    *  Calculates all the prime numbers up to given threshold.
    */
    public static int[] findPrimeNumbersUpToThreshold(int threshold)
    {
        ArrayList<Integer> allPrimeNumbers = new ArrayList<Integer>();
        int x = 0, y = 0;
        for (x = 2; x < threshold; x++)
        {
            if ( ( (x & 1) != 0 ) || (x == 2) )
            {
                for (y = 2; y <= x / 2; y++)
                    if ( (x % y) == 0 )
                        break;

                if (y > x / 2)
                    allPrimeNumbers.add(x);
            }
        }

        return ArraysAutoBoxUtils.toPrimitiveListInteger(allPrimeNumbers);
    }

    /**
    *  Calculates the first prime numbers up to a given point.
    */
    public static int[] findFirstPrimeNumbers(int howManyPrimeNumbers)
    {
        int[] allPrimeNumbers = new int[howManyPrimeNumbers];
        int x = 0, y = 0, c = 0;
        for (x = 2; x < Integer.MAX_VALUE; x++)
        {
            if ( ( (x & 1) != 0 ) || (x == 2) )
            {
                for (y = 2; y <= x / 2; y++)
                    if ( (x % y) == 0 )
                        break;

                if (y > x / 2)
                    allPrimeNumbers[c++] = x;

                if (c == howManyPrimeNumbers)
                    return allPrimeNumbers;
            }
        }

        // will probably never reach this limit, fail-safe exit from function
        return allPrimeNumbers;
    }

    public static int clamp(int v, int min, int max)
    {
        if (v < min)
        {
            return min;
        }
        else if (v > max)
        {
            return max;
        }
        else
        {
            return v;
        }
    }

    public static float clamp(float v, float min, float max)
    {
        if (v < min)
        {
            return min;
        }
        else if (v > max)
        {
            return max;
        }
        else
        {
            return v;
        }
    }

    public static double clamp(double v, double min, double max)
    {
        if (v < min)
        {
            return min;
        }
        else if (v > max)
        {
            return max;
        }
        else
        {
            return v;
        }
    }
}