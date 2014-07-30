package org.BioLayoutExpress3D.StaticLibraries;


import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* EnumUtils is a final class containing only methods for creating strings, numbers & random selections from enum instances.
*
* @author Thanos Theo, 2008-2009-2010-2011-2012
* @version 3.0.0.0
*/

public final class EnumUtils
{

    public static final String ENUM_REGEX = "_";

    public static <T extends Enum<T>> int getEnumIndexForName(Class<T> enumType, String field)
    {
        try
        {
            return Enum.valueOf(enumType, field).ordinal();
        }
        catch (IllegalArgumentException iaExc)
        {
            if (DEBUG_BUILD) println("IllegalArgumentException with EnumUtils.getEnumIndexForName():\n" + iaExc.getMessage());

            return 0;
        }
    }

    public static <T extends Enum<T>> int extractEndingNumberString(Enum<T> enumType)
    {
        String enumName = enumType.toString();
        String[] splitNames = enumName.split(ENUM_REGEX + "+");

        try
        {
            return Integer.parseInt(splitNames[splitNames.length - 1]);
        }
        catch (NumberFormatException nfExc)
        {
            if (DEBUG_BUILD) println("NumberFormatException with EnumUtils.extractInt():\n" + nfExc.getMessage());

            return 0;
        }
    }

    private static <T extends Enum<T>> String extractNumberString(Enum<T> enumType)
    {
        String enumName = enumType.toString();
        enumName = enumName.substring( 1, enumName.length() );

        return enumName.replace(ENUM_REGEX, ".");
    }

    public static <T extends Enum<T>> int extractInt(Enum<T> enumType)
    {
        try
        {
            return Integer.parseInt( extractNumberString(enumType) );
        }
        catch (NumberFormatException nfExc)
        {
            if (DEBUG_BUILD) println("NumberFormatException with EnumUtils.extractInt():\n" + nfExc.getMessage());

            return 0;
        }
    }

    public static <T extends Enum<T>> long extractLong(Enum<T> enumType)
    {
        try
        {
            return Long.parseLong( extractNumberString(enumType) );
        }
        catch (NumberFormatException nfExc)
        {
            if (DEBUG_BUILD) println("NumberFormatException with EnumUtils.extractLong():\n" + nfExc.getMessage());

            return 0;
        }
    }

    public static <T extends Enum<T>> float extractFloat(Enum<T> enumType)
    {
        try
        {
            return Float.parseFloat( extractNumberString(enumType) );
        }
        catch (NumberFormatException nfExc)
        {
            if (DEBUG_BUILD) println("NumberFormatException with EnumUtils.extractFloat():\n" + nfExc.getMessage());

            return 0.0f;
        }
    }

    public static <T extends Enum<T>> double extractDouble(Enum<T> enumType)
    {
        try
        {
            return Double.parseDouble( extractNumberString(enumType) );
        }
        catch (NumberFormatException nfExc)
        {
            if (DEBUG_BUILD) println("NumberFormatException with EnumUtils.extractDouble():\n" + nfExc.getMessage());

            return 0.0;
        }
    }

    private static String capitalizeFirstCharacter(String enumName)
    {
        return Character.toUpperCase( enumName.charAt(0) ) + enumName.substring(1).toLowerCase();
    }

    public static <T extends Enum<T>> String capitalizeFirstCharacter(Enum<T> enumType)
    {
        return Character.toUpperCase( enumType.toString().charAt(0) ) + enumType.toString().substring(1).toLowerCase();
    }

    public static <T extends Enum<T>> String splitAndCapitalizeFirstCharacters(Enum<T> enumType)
    {
        String enumName = enumType.toString();
        String[] splitNames = enumName.split(ENUM_REGEX + "+");
        String returnName = "";
        for (int i = 0; i < splitNames.length; i++)
            returnName += capitalizeFirstCharacter(splitNames[i]);

        return returnName;
    }

    public static <T extends Enum<T>> String splitAndCapitalizeFirstCharactersForAllButFirstName(Enum<T> enumType)
    {
        String enumName = enumType.toString();
        String[] splitNames = enumName.split(ENUM_REGEX + "+");
        String returnName = "";
        for (int i = 1; i < splitNames.length; i++)
            returnName += capitalizeFirstCharacter(splitNames[i]);

        return returnName;
    }

    public static <T extends Enum<T>> String splitAndCapitalizeFirstCharactersForAllButLastName(Enum<T> enumType)
    {
        String enumName = enumType.toString();
        String[] splitNames = enumName.split(ENUM_REGEX + "+");
        String returnName = "";
        for (int i = 0; i < splitNames.length - 1; i++)
            returnName += capitalizeFirstCharacter(splitNames[i]);

        return returnName + splitNames[splitNames.length - 1];
    }

    public static <T extends Enum<T>> String splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(Enum<T> enumType)
    {
        String enumName = enumType.toString();
        String[] splitNames = enumName.split(ENUM_REGEX + "+");
        String returnName = capitalizeFirstCharacter(splitNames[0]);
        for (int i = 1; i < splitNames.length; i++)
            returnName += " " + capitalizeFirstCharacter(splitNames[i]);

        return returnName;
    }

    public static <T extends Enum<T>> String splitCapitalizeFirstCharactersForAllButFirstNameAndAddWhiteSpaceBetweenNames(Enum<T> enumType)
    {
        String enumName = enumType.toString();
        String[] splitNames = enumName.split(ENUM_REGEX + "+");
        String returnName = splitNames[0];
        for (int i = 1; i < splitNames.length; i++)
            returnName += " " + capitalizeFirstCharacter(splitNames[i]);

        return returnName;
    }

    public static <T extends Enum<T>> String splitCapitalizeFirstCharactersForAllButLastNameAndAddWhiteSpaceBetweenNames(Enum<T> enumType)
    {
        String enumName = enumType.toString();
        String[] splitNames = enumName.split(ENUM_REGEX + "+");
        String returnName = capitalizeFirstCharacter(splitNames[0]);
        for (int i = 1; i < splitNames.length - 1; i++)
            returnName += " " + capitalizeFirstCharacter(splitNames[i]);

        return returnName + " " + splitNames[splitNames.length - 1];
    }

    public static <T extends Enum<T>> String splitCapitalizeFirstCharactersInvertOrderAndAddWhiteSpaceBetweenNames(Enum<T> enumType)
    {
        String enumName = enumType.toString();
        String[] splitNames = enumName.split(ENUM_REGEX + "+");
        String returnName = capitalizeFirstCharacter(splitNames[splitNames.length - 1]);
        int i = splitNames.length - 1;
        while (--i >= 0)
            returnName += " " + capitalizeFirstCharacter(splitNames[i]);

        return returnName;
    }

    public static <T extends Enum<T>> String splitCapitalizeFirstCharactersForAllButLastNameInvertOrderAndAddWhiteSpaceBetweenNames(Enum<T> enumType)
    {
        String enumName = enumType.toString();
        String[] splitNames = enumName.split(ENUM_REGEX + "+");
        String returnName = splitNames[splitNames.length - 1];
        int i = splitNames.length - 1;
        while (--i >= 0)
            returnName += " " + capitalizeFirstCharacter(splitNames[i]);

        return returnName;
    }

    /**
    *  Returns a random selection from among enum instances. Uses the overloaded random() method with an T[] array.
    */
    public static <T extends Enum<T>> T random(Class<T> enumClass)
    {
        return random( enumClass.getEnumConstants() );
    }

    /**
    *  Returns a random value from a given T[] array.
    */
    public static <T extends Enum<T>> T random(T[] values)
    {
        return values[Random.getRandomRange(0, values.length - 1)];
    }


}