package org.BioLayoutExpress3D.StaticLibraries;

import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* FixedPointMath is a final class containing only static methods for fixed point mathematical calculations for 8 bit (byte), 16 bit (short) & 32 bit (int) cases of both signed & unsigned float numbers.
*
* Below are the mathematical definition rules for addition, subtraction, multiplication & division between 2 fixed point numbers of the same base:
* f1 = a * 2^N (N = same base, fn same base N as f1)
* f2 = b * 2^N (N = same base, fn same base N as f2)
* f1 + f2 = a * 2^N + b * 2^N = (a + b) * 2^N (no extra addition    method needed for implementation)
* f1 - f2 = a * 2^N - b * 2^N = (a - b) * 2^N (no extra subtraction method needed for implementation)
* f1 * f2 = a * 2^N * b * 2^N = (a * b) * 2^N * 2^N = (a * b) * 2^2N -> fn = (f1 * f2) / 2^N (extra multiplication method needed for implementation)
* f1 / f2 = a * 2^N / b * 2^N = (a / b)                              -> fn = (f1 / 2^N) / f2 (extra division       method needed for implementation)
*
* @author Thanos Theo, Michael Kargas, 2010
* @version 3.0.0.0
*/

public final class FixedPointMath
{





    /**
    *  Byte fixed point constant.
    */
    private static final byte MAX_BYTE_DECIMAL_PART_LENGTH = 8;

    /**
    *  Byte fixed point constant.
    */
    private static final short UNSIGNED_BYTE_MAX_VALUE = 1 << MAX_BYTE_DECIMAL_PART_LENGTH;

    /**
    *  Byte fixed point constant.
    */
    private static final short UNSIGNED_BYTE_MAX_VALUE_FOR_MODULO = UNSIGNED_BYTE_MAX_VALUE - 1;





    /**
    *  Short fixed point constant.
    */
    private static final byte MAX_SHORT_DECIMAL_PART_LENGTH = 16;

    /**
    *  Short fixed point constant.
    */
    private static final int UNSIGNED_SHORT_MAX_VALUE = 1 << MAX_SHORT_DECIMAL_PART_LENGTH;

    /**
    *  Short fixed point constant.
    */
    private static final int UNSIGNED_SHORT_MAX_VALUE_FOR_MODULO = UNSIGNED_SHORT_MAX_VALUE - 1;





    /**
    *  Integer fixed point constant.
    */
    private static final byte MAX_INTEGER_DECIMAL_PART_LENGTH = 32;

    /**
    *  Integer fixed point constant.
    */
    private static final long UNSIGNED_INTEGER_MAX_VALUE = (1l << MAX_INTEGER_DECIMAL_PART_LENGTH);

    /**
    *  Integer fixed point constant.
    */
    private static final long UNSIGNED_INTEGER_MAX_VALUE_FOR_MODULO = UNSIGNED_INTEGER_MAX_VALUE - 1;





    /**
    *  Converts a given float to a fixed point byte number.
    */
    public static byte convertFromFloatToFixedPointByteNumber(float number, int decimalPartLength)
    {
        if (decimalPartLength <= 0 || decimalPartLength > MAX_BYTE_DECIMAL_PART_LENGTH)
        {
            if (DEBUG_BUILD) println("Error with decimalPartLength: " + decimalPartLength + "\nIt must be between a range of 1-" + MAX_BYTE_DECIMAL_PART_LENGTH + " bits.\nNow setting it to " + MAX_BYTE_DECIMAL_PART_LENGTH + ".");
            decimalPartLength = MAX_BYTE_DECIMAL_PART_LENGTH;
        }

        return (byte)( (1 << decimalPartLength) * number );
    }

    /**
    *  Multiplies two given fixed point signed byte numbers.
    */
    public static byte multiplyTwoFixedPointSignedByteNumbers(byte number1, byte number2, int decimalPartLength)
    {
        if (decimalPartLength <= 0 || decimalPartLength > MAX_BYTE_DECIMAL_PART_LENGTH)
        {
            if (DEBUG_BUILD) println("Error with decimalPartLength: " + decimalPartLength + "\nIt must be between a range of 1-" + MAX_BYTE_DECIMAL_PART_LENGTH + " bits.\nNow setting it to " + MAX_BYTE_DECIMAL_PART_LENGTH + ".");
            decimalPartLength = MAX_BYTE_DECIMAL_PART_LENGTH;
        }

        return (byte)( (number1 * number2) >> decimalPartLength );
    }

    /**
    *  Multiplies two given fixed point unsigned byte numbers.
    */
    public static byte multiplyTwoFixedPointUnsignedByteNumbers(byte number1, byte number2, int decimalPartLength)
    {
        if (decimalPartLength <= 0 || decimalPartLength > MAX_BYTE_DECIMAL_PART_LENGTH)
        {
            if (DEBUG_BUILD) println("Error with decimalPartLength: " + decimalPartLength + "\nIt must be between a range of 1-" + MAX_BYTE_DECIMAL_PART_LENGTH + " bits.\nNow setting it to " + MAX_BYTE_DECIMAL_PART_LENGTH + ".");
            decimalPartLength = MAX_BYTE_DECIMAL_PART_LENGTH;
        }

        // make sure to convert to unsigned byte first using two's-complement arithmetic
        return (byte)( ( ( (UNSIGNED_BYTE_MAX_VALUE + number1) & UNSIGNED_BYTE_MAX_VALUE_FOR_MODULO ) * ( (UNSIGNED_BYTE_MAX_VALUE + number2) & UNSIGNED_BYTE_MAX_VALUE_FOR_MODULO ) ) >> decimalPartLength );
    }

    /**
    *  Divides two given fixed point signed byte numbers.
    */
    public static byte divideTwoFixedPointSignedByteNumbers(byte number1, byte number2, int decimalPartLength)
    {
        if (decimalPartLength <= 0 || decimalPartLength > MAX_BYTE_DECIMAL_PART_LENGTH)
        {
            if (DEBUG_BUILD) println("Error with decimalPartLength: " + decimalPartLength + "\nIt must be between a range of 1-" + MAX_BYTE_DECIMAL_PART_LENGTH + " bits.\nNow setting it to " + MAX_BYTE_DECIMAL_PART_LENGTH + ".");
            decimalPartLength = MAX_BYTE_DECIMAL_PART_LENGTH;
        }

        return (byte)( (number1 << decimalPartLength) / number2 );
    }

    /**
    *  Divides two given fixed point unsigned byte numbers.
    */
    public static byte divideTwoFixedPointUnsignedByteNumbers(byte number1, byte number2, int decimalPartLength)
    {
        if (decimalPartLength <= 0 || decimalPartLength > MAX_BYTE_DECIMAL_PART_LENGTH)
        {
            if (DEBUG_BUILD) println("Error with decimalPartLength: " + decimalPartLength + "\nIt must be between a range of 1-" + MAX_BYTE_DECIMAL_PART_LENGTH + " bits.\nNow setting it to " + MAX_BYTE_DECIMAL_PART_LENGTH + ".");
            decimalPartLength = MAX_BYTE_DECIMAL_PART_LENGTH;
        }

        // make sure to convert to unsigned byte first using two's-complement arithmetic
        return (byte)( ( ( (UNSIGNED_BYTE_MAX_VALUE + number1) & UNSIGNED_BYTE_MAX_VALUE_FOR_MODULO ) << decimalPartLength ) / ( (UNSIGNED_BYTE_MAX_VALUE + number2) & UNSIGNED_BYTE_MAX_VALUE_FOR_MODULO ) );
    }

    /**
    *  Converts a given fixed point byte number to a signed float.
    */
    public static float convertFromFixedPointByteNumberToSignedFloat(byte number, int decimalPartLength)
    {
        if (decimalPartLength <= 0 || decimalPartLength > MAX_BYTE_DECIMAL_PART_LENGTH)
        {
            if (DEBUG_BUILD) println("Error with decimalPartLength: " + decimalPartLength + "\nIt must be between a range of 1-" + MAX_BYTE_DECIMAL_PART_LENGTH + " bits.\nNow setting it to " + MAX_BYTE_DECIMAL_PART_LENGTH + ".");
            decimalPartLength = MAX_BYTE_DECIMAL_PART_LENGTH;
        }

        return number / (float)(1 << decimalPartLength);
    }

    /**
    *  Converts a given fixed point byte number to an unsigned float.
    */
    public static float convertFromFixedPointByteNumberUnsignedFloat(byte number, int decimalPartLength)
    {
        if (decimalPartLength <= 0 || decimalPartLength > MAX_BYTE_DECIMAL_PART_LENGTH)
        {
            if (DEBUG_BUILD) println("Error with decimalPartLength: " + decimalPartLength + "\nIt must be between a range of 1-" + MAX_BYTE_DECIMAL_PART_LENGTH + " bits.\nNow setting it to " + MAX_BYTE_DECIMAL_PART_LENGTH + ".");
            decimalPartLength = MAX_BYTE_DECIMAL_PART_LENGTH;
        }

        // make sure to convert to unsigned byte first using two's-complement arithmetic
        return ( (UNSIGNED_BYTE_MAX_VALUE + number) & UNSIGNED_BYTE_MAX_VALUE_FOR_MODULO ) / (float)(1 << decimalPartLength);
    }





    /**
    *  Converts a given float to a fixed point short number.
    */
    public static short convertFromFloatToFixedPointShortNumber(float number, int decimalPartLength)
    {
        if (decimalPartLength <= 0 || decimalPartLength > MAX_SHORT_DECIMAL_PART_LENGTH)
        {
            if (DEBUG_BUILD) println("Error with decimalPartLength: " + decimalPartLength + "\nIt must be between a range of 1-" + MAX_SHORT_DECIMAL_PART_LENGTH + " bits.\nNow setting it to " + MAX_SHORT_DECIMAL_PART_LENGTH + ".");
            decimalPartLength = MAX_SHORT_DECIMAL_PART_LENGTH;
        }

        return (short)( (1 << decimalPartLength) * number );
    }

    /**
    *  Multiplies two given fixed point signed short numbers.
    */
    public static short multiplyTwoFixedPointSignedShortNumbers(short number1, short number2, int decimalPartLength)
    {
        if (decimalPartLength <= 0 || decimalPartLength > MAX_SHORT_DECIMAL_PART_LENGTH)
        {
            if (DEBUG_BUILD) println("Error with decimalPartLength: " + decimalPartLength + "\nIt must be between a range of 1-" + MAX_SHORT_DECIMAL_PART_LENGTH + " bits.\nNow setting it to " + MAX_SHORT_DECIMAL_PART_LENGTH + ".");
            decimalPartLength = MAX_SHORT_DECIMAL_PART_LENGTH;
        }

        return (short)( (number1  * number2) >> decimalPartLength );
    }

    /**
    *  Multiplies two given fixed point unsigned short numbers.
    */
    public static short multiplyTwoFixedPointUnsignedShortNumbers(short number1, short number2, int decimalPartLength)
    {
        if (decimalPartLength <= 0 || decimalPartLength > MAX_SHORT_DECIMAL_PART_LENGTH)
        {
            if (DEBUG_BUILD) println("Error with decimalPartLength: " + decimalPartLength + "\nIt must be between a range of 1-" + MAX_SHORT_DECIMAL_PART_LENGTH + " bits.\nNow setting it to " + MAX_SHORT_DECIMAL_PART_LENGTH + ".");
            decimalPartLength = MAX_SHORT_DECIMAL_PART_LENGTH;
        }

        // make sure to convert to unsigned short first using two's-complement arithmetic
        return (short)( ( ( (UNSIGNED_SHORT_MAX_VALUE + number1) & UNSIGNED_SHORT_MAX_VALUE_FOR_MODULO ) * ( (UNSIGNED_SHORT_MAX_VALUE + number2) & UNSIGNED_SHORT_MAX_VALUE_FOR_MODULO ) ) >> decimalPartLength );
    }

    /**
    *  Divides two given fixed point signed short numbers.
    */
    public static short divideTwoFixedPointSignedShortNumbers(short number1, short number2, int decimalPartLength)
    {
        if (decimalPartLength <= 0 || decimalPartLength > MAX_SHORT_DECIMAL_PART_LENGTH)
        {
            if (DEBUG_BUILD) println("Error with decimalPartLength: " + decimalPartLength + "\nIt must be between a range of 1-" + MAX_SHORT_DECIMAL_PART_LENGTH + " bits.\nNow setting it to " + MAX_SHORT_DECIMAL_PART_LENGTH + ".");
            decimalPartLength = MAX_SHORT_DECIMAL_PART_LENGTH;
        }

        return (short)( (number1 << decimalPartLength) / number2 );
    }

    /**
    *  Divides two given fixed point unsigned short numbers.
    */
    public static short divideTwoFixedPointUnsignedShortNumbers(short number1, short number2, int decimalPartLength)
    {
        if (decimalPartLength <= 0 || decimalPartLength > MAX_SHORT_DECIMAL_PART_LENGTH)
        {
            if (DEBUG_BUILD) println("Error with decimalPartLength: " + decimalPartLength + "\nIt must be between a range of 1-" + MAX_SHORT_DECIMAL_PART_LENGTH + " bits.\nNow setting it to " + MAX_SHORT_DECIMAL_PART_LENGTH + ".");
            decimalPartLength = MAX_SHORT_DECIMAL_PART_LENGTH;
        }

        // make sure to convert to unsigned short first using two's-complement arithmetic
        return (short)( ( ( (UNSIGNED_SHORT_MAX_VALUE + number1) & UNSIGNED_SHORT_MAX_VALUE_FOR_MODULO ) << decimalPartLength ) / ( (UNSIGNED_SHORT_MAX_VALUE + number2) & UNSIGNED_SHORT_MAX_VALUE_FOR_MODULO ) );
    }

    /**
    *  Converts a given fixed point signed short number to a signed float.
    */
    public static float convertFromFixedPointShortNumberToSignedFloat(short number, int decimalPartLength)
    {
        if (decimalPartLength <= 0 || decimalPartLength > MAX_SHORT_DECIMAL_PART_LENGTH)
        {
            if (DEBUG_BUILD) println("Error with decimalPartLength: " + decimalPartLength + "\nIt must be between a range of 1-" + MAX_SHORT_DECIMAL_PART_LENGTH + " bits.\nNow setting it to " + MAX_SHORT_DECIMAL_PART_LENGTH + ".");
            decimalPartLength = MAX_SHORT_DECIMAL_PART_LENGTH;
        }

        return number / (float)(1 << decimalPartLength);
    }

    /**
    *  Converts a given fixed point signed short number to an unsigned float.
    */
    public static float convertFromFixedPointShortNumberToUnsignedFloat(short number, int decimalPartLength)
    {
        if (decimalPartLength <= 0 || decimalPartLength > MAX_SHORT_DECIMAL_PART_LENGTH)
        {
            if (DEBUG_BUILD) println("Error with decimalPartLength: " + decimalPartLength + "\nIt must be between a range of 1-" + MAX_SHORT_DECIMAL_PART_LENGTH + " bits.\nNow setting it to " + MAX_SHORT_DECIMAL_PART_LENGTH + ".");
            decimalPartLength = MAX_SHORT_DECIMAL_PART_LENGTH;
        }

        // make sure to convert to unsigned short first using two's-complement arithmetic
        return ( (UNSIGNED_SHORT_MAX_VALUE + number) & UNSIGNED_SHORT_MAX_VALUE_FOR_MODULO ) / (float)(1 << decimalPartLength);
    }




    /**
    *  Converts a given float to a fixed point integer number.
    */
    public static int convertFromFloatToFixedPointIntegerNumber(float number, int decimalPartLength)
    {
        if (decimalPartLength <= 0 || decimalPartLength > MAX_INTEGER_DECIMAL_PART_LENGTH)
        {
            if (DEBUG_BUILD) println("Error with decimalPartLength: " + decimalPartLength + "\nIt must be between a range of 1-" + MAX_INTEGER_DECIMAL_PART_LENGTH + " bits.\nNow setting it to " + MAX_INTEGER_DECIMAL_PART_LENGTH + ".");
            decimalPartLength = MAX_INTEGER_DECIMAL_PART_LENGTH;
        }

        return (int)( (long)( (1 << decimalPartLength) * number ) );
    }

    /**
    *  Multiplies two given fixed point signed integer numbers.
    */
    public static int multiplyTwoFixedPointSignedIntegerNumbers(int number1, int number2, int decimalPartLength)
    {
        if (decimalPartLength <= 0 || decimalPartLength > MAX_INTEGER_DECIMAL_PART_LENGTH)
        {
            if (DEBUG_BUILD) println("Error with decimalPartLength: " + decimalPartLength + "\nIt must be between a range of 1-" + MAX_INTEGER_DECIMAL_PART_LENGTH + " bits.\nNow setting it to " + MAX_INTEGER_DECIMAL_PART_LENGTH + ".");
            decimalPartLength = MAX_INTEGER_DECIMAL_PART_LENGTH;
        }

        return (int)( (number1 * number2) >> decimalPartLength );
    }

    /**
    *  Multiplies two given fixed point unsigned integer numbers.
    */
    public static int multiplyTwoFixedPointUnsignedIntegerNumbers(int number1, int number2, int decimalPartLength)
    {
        if (decimalPartLength <= 0 || decimalPartLength > MAX_INTEGER_DECIMAL_PART_LENGTH)
        {
            if (DEBUG_BUILD) println("Error with decimalPartLength: " + decimalPartLength + "\nIt must be between a range of 1-" + MAX_INTEGER_DECIMAL_PART_LENGTH + " bits.\nNow setting it to " + MAX_INTEGER_DECIMAL_PART_LENGTH + ".");
            decimalPartLength = MAX_INTEGER_DECIMAL_PART_LENGTH;
        }

        // make sure to convert to unsigned int first using two's-complement arithmetic
        return (int)( ( ( (UNSIGNED_INTEGER_MAX_VALUE + number1) & UNSIGNED_INTEGER_MAX_VALUE_FOR_MODULO ) * ( (UNSIGNED_INTEGER_MAX_VALUE + number2) & UNSIGNED_INTEGER_MAX_VALUE_FOR_MODULO ) ) >> decimalPartLength );
    }

    /**
    *  Divides two given fixed point signed integer numbers.
    */
    public static int divideTwoFixedPointSignedIntegerNumbers(int number1, int number2, int decimalPartLength)
    {
        if (decimalPartLength <= 0 || decimalPartLength > MAX_INTEGER_DECIMAL_PART_LENGTH)
        {
            if (DEBUG_BUILD) println("Error with decimalPartLength: " + decimalPartLength + "\nIt must be between a range of 1-" + MAX_INTEGER_DECIMAL_PART_LENGTH + " bits.\nNow setting it to " + MAX_INTEGER_DECIMAL_PART_LENGTH + ".");
            decimalPartLength = MAX_INTEGER_DECIMAL_PART_LENGTH;
        }

        return (int)( (number1 << decimalPartLength) / number2 );
    }

    /**
    *  Divides two given fixed point unsigned integer numbers.
    */
    public static int divideTwoFixedPointUnsignedIntegerNumbers(int number1, int number2, int decimalPartLength)
    {
        if (decimalPartLength <= 0 || decimalPartLength > MAX_INTEGER_DECIMAL_PART_LENGTH)
        {
            if (DEBUG_BUILD) println("Error with decimalPartLength: " + decimalPartLength + "\nIt must be between a range of 1-" + MAX_INTEGER_DECIMAL_PART_LENGTH + " bits.\nNow setting it to " + MAX_INTEGER_DECIMAL_PART_LENGTH + ".");
            decimalPartLength = MAX_INTEGER_DECIMAL_PART_LENGTH;
        }

        // make sure to convert to unsigned int first using two's-complement arithmetic
        return (int)( ( ( (UNSIGNED_INTEGER_MAX_VALUE + number1) & UNSIGNED_INTEGER_MAX_VALUE_FOR_MODULO ) << decimalPartLength ) / ( (UNSIGNED_INTEGER_MAX_VALUE + number2) & UNSIGNED_INTEGER_MAX_VALUE_FOR_MODULO ) );
    }

    /**
    *  Converts a given fixed point integer number to a signed float.
    */
    public static float convertFromFixedPointIntegerNumberToSignedFloat(int number, int decimalPartLength)
    {
        if (decimalPartLength <= 0 || decimalPartLength > MAX_INTEGER_DECIMAL_PART_LENGTH)
        {
            if (DEBUG_BUILD) println("Error with decimalPartLength: " + decimalPartLength + "\nIt must be between a range of 1-" + MAX_INTEGER_DECIMAL_PART_LENGTH + " bits.\nNow setting it to " + MAX_INTEGER_DECIMAL_PART_LENGTH + ".");
            decimalPartLength = MAX_INTEGER_DECIMAL_PART_LENGTH;
        }

        return ( number / (float)(1 << decimalPartLength) );
    }

    /**
    *  Converts a given fixed point integer number to an unsigned float.
    */
    public static float convertFromFixedPointIntegerNumberToUnsignedFloat(int number, int decimalPartLength)
    {
        if (decimalPartLength <= 0 || decimalPartLength > MAX_INTEGER_DECIMAL_PART_LENGTH)
        {
            if (DEBUG_BUILD) println("Error with decimalPartLength: " + decimalPartLength + "\nIt must be between a range of 1-" + MAX_INTEGER_DECIMAL_PART_LENGTH + " bits.\nNow setting it to " + MAX_INTEGER_DECIMAL_PART_LENGTH + ".");
            decimalPartLength = MAX_INTEGER_DECIMAL_PART_LENGTH;
        }

        // make sure to convert to unsigned int first using two's-complement arithmetic
        return ( ( (UNSIGNED_INTEGER_MAX_VALUE + number) & UNSIGNED_INTEGER_MAX_VALUE_FOR_MODULO ) / (float)(1 << decimalPartLength) );
    }


}