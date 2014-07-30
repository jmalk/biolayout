package org.BioLayoutExpress3D.DataStructures;

import java.util.*;

/**
*
* EnumSets is a final class containing only methods for creating EnumSets from two given ones according to a mathematical Set Theory method.
* it is using the optimized EnumSet.clone() method as a faster way of constructing EnumSet objects.
*
* @author Thanos Theo, 2008-2009
* @version 3.0.0.0
*/

public final class EnumSets
{

    /**
    *  Produces the union of two given EnumSets.
    *  It uses the EnumSet.clone() method for faster EnumSet object creation (from EnumSet a).
    */
    public static <T extends Enum<T>> EnumSet<T> unionEnumSet(EnumSet<T> a, EnumSet<T> b)
    {
        EnumSet<T> result = a.clone();
        result.addAll(b);

        return result;
    }

    /**
    *  Produces the intersection of two given EnumSets.
    *  It uses the EnumSet.clone() method for faster EnumSet object creation (from EnumSet a).
    */
    public static <T extends Enum<T>> EnumSet<T> intersectionEnumSet(EnumSet<T> a, EnumSet<T> b)
    {
        EnumSet<T> result = a.clone();
        result.retainAll(b);

        return result;
    }

    /**
    *  Produces the difference of two given EnumSets.
    *  It uses the EnumSet.clone() method for faster EnumSet object creation (from EnumSet a).
    */
    public static <T extends Enum<T>> EnumSet<T> differenceEnumSet(EnumSet<T> a, EnumSet<T> b)
    {
        EnumSet<T> result = a.clone();
        result.removeAll(b);

        return result;
    }

    /**
    *  Produces the complement of two given EnumSets.
    *  It uses the EnumSet.clone() method for faster EnumSet object creation (from EnumSet a).
    */
    public static <T extends Enum<T>> EnumSet<T> complementEnumSet(EnumSet<T> a, EnumSet<T> b)
    {
        return differenceEnumSet( unionEnumSet(a, b), intersectionEnumSet(a , b) );
    }


}