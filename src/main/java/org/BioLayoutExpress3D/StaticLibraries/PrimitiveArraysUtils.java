package org.BioLayoutExpress3D.StaticLibraries;

/**
*
* PrimitiveArraysUtils is a final class containing only static methods for primitive arrays.
*
* @author Thanos Theo, 2008-2009
* @version 3.0.0.0
*/

public final class PrimitiveArraysUtils
{

    /**
    *  Reverses the given boolean array.
    */
    public static void reverse(boolean[] array)
    {
        int left  = 0;
        int right = array.length - 1;
        boolean temp = false;
        while (left < right)
        {
            // exchange the left and right elements
            temp = array[left];
            array[left] = array[right];
            array[right] = temp;

            // move the bounds toward the center
            left++;
            right--;
        }
    }

    /**
    *  Reverses the given char array.
    */
    public static void reverse(char[] array)
    {
        int left  = 0;
        int right = array.length - 1;
        char temp = ' ';
        while (left < right)
        {
            // exchange the left and right elements
            temp = array[left];
            array[left] = array[right];
            array[right] = temp;

            // move the bounds toward the center
            left++;
            right--;
        }
    }

    /**
    *  Reverses the given byte array.
    */
    public static void reverse(byte[] array)
    {
        int left  = 0;
        int right = array.length - 1;
        byte temp = 0;
        while (left < right)
        {
            // exchange the left and right elements
            temp = array[left];
            array[left] = array[right];
            array[right] = temp;

            // move the bounds toward the center
            left++;
            right--;
        }
    }

    /**
    *  Reverses the given short array.
    */
    public static void reverse(short[] array)
    {
        int left  = 0;
        int right = array.length - 1;
        short temp = 0;
        while (left < right)
        {
            // exchange the left and right elements
            temp = array[left];
            array[left] = array[right];
            array[right] = temp;

            // move the bounds toward the center
            left++;
            right--;
        }
    }

    /**
    *  Reverses the given integer array.
    */
    public static void reverse(int[] array)
    {
        int left  = 0;
        int right = array.length - 1;
        int temp = 0;
        while (left < right)
        {
            // exchange the left and right elements
            temp = array[left];
            array[left] = array[right];
            array[right] = temp;

            // move the bounds toward the center
            left++;
            right--;
        }
    }

    /**
    *  Reverses the given long array.
    */
    public static void reverse(long[] array)
    {
        int left  = 0;
        int right = array.length - 1;
        long temp = 0;
        while (left < right)
        {
            // exchange the left and right elements
            temp = array[left];
            array[left] = array[right];
            array[right] = temp;

            // move the bounds toward the center
            left++;
            right--;
        }
    }

    /**
    *  Reverses the given float array.
    */
    public static void reverse(float[] array)
    {
        int left  = 0;
        int right = array.length - 1;
        float temp = 0.0f;
        while (left < right)
        {
            // exchange the left and right elements
            temp = array[left];
            array[left] = array[right];
            array[right] = temp;

            // move the bounds toward the center
            left++;
            right--;
        }
    }

    /**
    *  Reverses the given double array.
    */
    public static void reverse(double[] array)
    {
        int left  = 0;
        int right = array.length - 1;
        double temp = 0.0;
        while (left < right)
        {
            // exchange the left and right elements
            temp = array[left];
            array[left] = array[right];
            array[right] = temp;

            // move the bounds toward the center
            left++;
            right--;
        }
    }


}