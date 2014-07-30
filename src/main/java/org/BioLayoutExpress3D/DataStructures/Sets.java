package org.BioLayoutExpress3D.DataStructures;

import java.util.*;

/**
*
* Sets is a final class containing only methods for creating Sets from two given ones according to a mathematical Set Theory method.
*
* @author Thanos Theo, 2008-2009
* @version 3.0.0.0
*/

public final class Sets
{

    /**
    *  Produces the union of two given Sets.
    */
    public static <T> Set<T> union(Set<T> a, Set<T> b)
    {
        Set<T> result = new HashSet<T>(a);
        result.addAll(b);

        return result;
    }

    /**
    *  Produces the intersection of two given Sets.
    */
    public static <T> Set<T> intersection(Set<T> a, Set<T> b)
    {
        Set<T> result = new HashSet<T>(a);
        result.retainAll(b);

        return result;
    }

    /**
    *  Produces the difference of two given Sets.
    */
    public static <T> Set<T> difference(Set<T> superset, Set<T> subset)
    {
        Set<T> result = new HashSet<T>(superset) ;
        result.removeAll(subset);

        return result;
    }

    /**
    *  Produces the complement of two given Sets.
    */
    public static <T> Set<T> complement(Set<T> a, Set<T> b)
    {
        return difference( union(a, b), intersection(a , b) );
    }


}