package org.BioLayoutExpress3D.StaticLibraries;

import java.util.*;

/**
*
* ArraysAutoBoxUtils is a final class containing only static methods to be used for
* autoboxing & autounboxing arrays of primitives and their Object equivalents.
*
* @author Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*/

public final class ArraysAutoBoxUtils
{

    /**
    *  Method to autobox a Boolean array.
    */
    public static Boolean[] toObjectArrayBoolean(boolean[] in)
    {
        Boolean[] out = new Boolean[in.length];
        System.arraycopy(in, 0, out, 0, in.length);

        return out;
    }

    /**
    *  Method to autounbox a Boolean array.
    */
    public static boolean[] toPrimitiveArrayBoolean(Boolean[] in)
    {
        boolean[] out = new boolean[in.length];
        System.arraycopy(in, 0, out, 0, in.length);

        return out;
    }

    /**
    *  Method to autounbox a Boolean list.
    */
    public static boolean[] toPrimitiveListBoolean(List<Boolean> in)
    {
        boolean[] out = new boolean[in.size()];
        for (int i = 0; i < in.size(); i++)
            out[i] = in.get(i); // Autounboxing

        return out;
    }

    /**
    *  Method to autounbox a Boolean[] list. Overloaded version of the previous method(s).
    */
    public static boolean[][] toPrimitiveListArrayBoolean(List<Boolean[]> in)
    {
        // We need to initialize the array below with a dimention of 0 or above instead of plain []
        // Otherwise a C/JNI native version of the code will crash the JVM trying to lock the 2D array!
        boolean[][] out = new boolean[in.size()][0];
        for (int i = 0; i < in.size(); i++)
            out[i] = toPrimitiveArrayBoolean( in.get(i) ); // Autounboxing

        return out;
    }

    /**
    *  Method to autobox a Character array. Overloaded version of the previous method(s).
    */
    public static Character[] toObjectArrayCharacter(char[] in)
    {
        Character[] out = new Character[in.length];
        System.arraycopy(in, 0, out, 0, in.length);

        return out;
    }

    /**
    *  Method to autounbox a Character array. Overloaded version of the previous method(s).
    */
    public static char[] toPrimitiveArrayCharacter(Character[] in)
    {
        char[] out = new char[in.length];
        System.arraycopy(in, 0, out, 0, in.length);

        return out;
    }

    /**
    *  Method to autounbox a Character list. Overloaded version of the previous method(s).
    */
    public static char[] toPrimitiveListCharacter(List<Character> in)
    {
        char[] out = new char[in.size()];
        for (int i = 0; i < in.size(); i++)
            out[i] = in.get(i); // Autounboxing

        return out;
    }

    /**
    *  Method to autounbox a Character[] list. Overloaded version of the previous method(s).
    */
    public static char[][] toPrimitiveListArrayCharacter(List<Character[]> in)
    {
        // We need to initialize the array below with a dimention of 0 or above instead of plain []
        // Otherwise a C/JNI native version of the code will crash the JVM trying to lock the 2D array!
        char[][] out = new char[in.size()][0];
        for (int i = 0; i < in.size(); i++)
            out[i] = toPrimitiveArrayCharacter( in.get(i) ); // Autounboxing

        return out;
    }

    /**
    *  Method to autobox a Byte array. Overloaded version of the previous method(s).
    */
    public static Byte[] toObjectArrayByte(byte[] in)
    {
        Byte[] out = new Byte[in.length];
        System.arraycopy(in, 0, out, 0, in.length);

        return out;
    }

    /**
    *  Method to autounbox a Byte array. Overloaded version of the previous method(s).
    */
    public static byte[] toPrimitiveArrayByte(Byte[] in)
    {
        byte[] out = new byte[in.length];
        System.arraycopy(in, 0, out, 0, in.length);

        return out;
    }

    /**
    *  Method to autounbox a Byte list. Overloaded version of the previous method(s).
    */
    public static byte[] toPrimitiveListByte(List<Byte> in)
    {
        byte[] out = new byte[in.size()];
        for (int i = 0; i < in.size(); i++)
            out[i] = in.get(i); // Autounboxing

        return out;
    }

    /**
    *  Method to autounbox a Byte[] list. Overloaded version of the previous method(s).
    */
    public static byte[][] toPrimitiveListArrayByte(List<Byte[]> in)
    {
        // We need to initialize the array below with a dimention of 0 or above instead of plain []
        // Otherwise a C/JNI native version of the code will crash the JVM trying to lock the 2D array!
        byte[][] out = new byte[in.size()][0];
        for (int i = 0; i < in.size(); i++)
            out[i] = toPrimitiveArrayByte( in.get(i) ); // Autounboxing

        return out;
    }

    /**
    *  Method to autobox a Short array. Overloaded version of the previous method(s).
    */
    public static Short[] toObjectArrayShort(short[] in)
    {
        Short[] out = new Short[in.length];
        System.arraycopy(in, 0, out, 0, in.length);

        return out;
    }

    /**
    *  Method to autounbox a Short array. Overloaded version of the previous method(s).
    */
    public static short[] toPrimitiveArrayShort(Short[] in)
    {
        short[] out = new short[in.length];
        System.arraycopy(in, 0, out, 0, in.length);

        return out;
    }

    /**
    *  Method to autounbox a Short list. Overloaded version of the previous method(s).
    */
    public static short[] toPrimitiveListShort(List<Short> in)
    {
        short[] out = new short[in.size()];
        for (int i = 0; i < in.size(); i++)
            out[i] = in.get(i); // Autounboxing

        return out;
    }

    /**
    *  Method to autounbox a Short[] list. Overloaded version of the previous method(s).
    */
    public static short[][] toPrimitiveListArrayShort(List<Short[]> in)
    {
        // We need to initialize the array below with a dimention of 0 or above instead of plain []
        // Otherwise a C/JNI native version of the code will crash the JVM trying to lock the 2D array!
        short[][] out = new short[in.size()][0];
        for (int i = 0; i < in.size(); i++)
            out[i] = toPrimitiveArrayShort( in.get(i) ); // Autounboxing

        return out;
    }

    /**
    *  Method to autobox a Integer array. Overloaded version of the previous method(s).
    */
    public static Integer[] toObjectArrayInteger(int[] in)
    {
        Integer[] out = new Integer[in.length];
        System.arraycopy(in, 0, out, 0, in.length);

        return out;
    }

    /**
    *  Method to autounbox a Integer array. Overloaded version of the previous method(s).
    */
    public static int[] toPrimitiveArrayInteger(Integer[] in)
    {
        int[] out = new int[in.length];
        System.arraycopy(in, 0, out, 0, in.length);

        return out;
    }

    /**
    *  Method to autounbox a Integer list. Overloaded version of the previous method(s).
    */
    public static int[] toPrimitiveListInteger(List<Integer> in)
    {
        int[] out = new int[in.size()];
        for (int i = 0; i < in.size(); i++)
            out[i] = in.get(i); // Autounboxing

        return out;
    }

    /**
    *  Method to autounbox a Integer[] list. Overloaded version of the previous method(s).
    */
    public static int[][] toPrimitiveListArrayInteger(List<Integer[]> in)
    {
        // We need to initialize the array below with a dimention of 0 or above instead of plain []
        // Otherwise a C/JNI native version of the code will crash the JVM trying to lock the 2D array!
        int[][] out = new int[in.size()][0];
        for (int i = 0; i < in.size(); i++)
            out[i] = toPrimitiveArrayInteger( in.get(i) ); // Autounboxing

        return out;
    }

    /**
    *  Method to autobox a Long array. Overloaded version of the previous method(s).
    */
    public static Long[] toObjectArrayLong(long[] in)
    {
        Long[] out = new Long[in.length];
        System.arraycopy(in, 0, out, 0, in.length);

        return out;
    }

    /**
    *  Method to autounbox a Long array. Overloaded version of the previous method(s).
    */
    public static long[] toPrimitiveArrayLong(Long[] in)
    {
        long[] out = new long[in.length];
        System.arraycopy(in, 0, out, 0, in.length);

        return out;
    }

    /**
    *  Method to autounbox a Long list. Overloaded version of the previous method(s).
    */
    public static long[] toPrimitiveListLong(List<Long> in)
    {
        long[] out = new long[in.size()];
        for (int i = 0; i < in.size(); i++)
            out[i] = in.get(i); // Autounboxing

        return out;
    }

    /**
    *  Method to autounbox a Long[] list. Overloaded version of the previous method(s).
    */
    public static long[][] toPrimitiveListArrayLong(List<Long[]> in)
    {
        // We need to initialize the array below with a dimention of 0 or above instead of plain []
        // Otherwise a C/JNI native version of the code will crash the JVM trying to lock the 2D array!
        long[][] out = new long[in.size()][0];
        for (int i = 0; i < in.size(); i++)
            out[i] = toPrimitiveArrayLong( in.get(i) ); // Autounboxing

        return out;
    }

    /**
    *  Method to autobox a Float array. Overloaded version of the previous method(s).
    */
    public static Float[] toObjectArrayFloat(float[] in)
    {
        Float[] out = new Float[in.length];
        System.arraycopy(in, 0, out, 0, in.length);

        return out;
    }

    /**
    *  Method to autounbox a Float array. Overloaded version of the previous method(s).
    */
    public static float[] toPrimitiveArrayFloat(Float[] in)
    {
        float[] out = new float[in.length];
        System.arraycopy(in, 0, out, 0, in.length);

        return out;
    }

    /**
    *  Method to autounbox a Float list. Overloaded version of the previous method(s).
    */
    public static float[] toPrimitiveListFloat(List<Float> in)
    {
        float[] out = new float[in.size()];
        for (int i = 0; i < in.size(); i++)
            out[i] = in.get(i); // Autounboxing

        return out;
    }

    /**
    *  Method to autounbox a Float[] list. Overloaded version of the previous method(s).
    */
    public static float[][] toPrimitiveListArrayFloat(List<Float[]> in)
    {
        // We need to initialize the array below with a dimention of 0 or above instead of plain []
        // Otherwise a C/JNI native version of the code will crash the JVM trying to lock the 2D array!
        float[][] out = new float[in.size()][0];
        for (int i = 0; i < in.size(); i++)
            out[i] = toPrimitiveArrayFloat( in.get(i) ); // Autounboxing

        return out;
    }

    /**
    *  Method to autobox a Double array. Overloaded version of the previous method(s).
    */
    public static Double[] toObjectArrayDouble(double[] in)
    {
        Double[] out = new Double[in.length];
        System.arraycopy(in, 0, out, 0, in.length);

        return out;
    }


    /**
    *  Method to autounbox a Double array. Overloaded version of the previous method(s).
    */
    public static double[] toPrimitiveArrayDouble(Double[] in)
    {
        double[] out = new double[in.length];
        System.arraycopy(in, 0, out, 0, in.length);

        return out;
    }

    /**
    *  Method to autounbox a Double list. Overloaded version of the previous method(s).
    */
    public static double[] toPrimitiveListDouble(List<Double> in)
    {
        double[] out = new double[in.size()];
        for (int i = 0; i < in.size(); i++)
            out[i] = in.get(i); // Autounboxing

        return out;
    }

    /**
    *  Method to autounbox a Double[] list. Overloaded version of the previous method(s).
    */
    public static double[][] toPrimitiveListArrayDouble(List<Double[]> in)
    {
        // We need to initialize the array below with a dimention of 0 or above instead of plain []
        // Otherwise a C/JNI native version of the code will crash the JVM trying to lock the 2D array!
        double[][] out = new double[in.size()][0];
        for (int i = 0; i < in.size(); i++)
            out[i] = toPrimitiveArrayDouble( in.get(i) ); // Autounboxing

        return out;
    }


}