package org.BioLayoutExpress3D.DataStructures;

/**
*
* Tuples is a final class containing only overloaded static tuple() methods for creating tuples of 2 up to 6 objects.
* The tuples used are to transfer objects to a recipient which read their elements but not put new ones in.
* This concept is known as a Data Transfer Object (or Messenger).
*
* @author Thanos Theo, 2008-2009
* @version 3.0.0.0
*/

public final class Tuples
{

    /**
    *  Static method to create a tuple with 2 objects.
    */
    public static <A, B> Tuple2<A, B> tuple(A a, B b)
    {
        return new Tuple2<A, B>(a, b);
    }

    /**
    *  Static method to create a tuple with 3 objects.
    */
    public static <A, B, C> Tuple3<A, B, C> tuple(A a, B b, C c)
    {
        return new Tuple3<A, B, C>(a, b, c);
    }

    /**
    *  Static method to create a tuple with 4 objects.
    */
    public static <A, B, C, D> Tuple4<A, B, C, D> tuple(A a, B b, C c, D d)
    {
        return new Tuple4<A, B, C, D>(a, b, c, d);
    }

    /**
    *  Static method to create a tuple with 5 objects.
    */
    public static <A, B, C, D, E> Tuple5<A, B, C, D, E> tuple(A a, B b, C c, D d, E e)
    {
        return new Tuple5<A, B, C, D, E>(a, b, c, d, e);
    }

    /**
    *  Static method to create a tuple with 6 objects.
    */
    public static <A, B, C, D, E, F> Tuple6<A, B, C, D, E, F> tuple(A a, B b, C c, D d, E e, F f)
    {
        return new Tuple6<A, B, C, D, E, F>(a, b, c, d, e, f);
    }

    /**
    *  Static method to create a tuple with 7 objects.
    */
    public static <A, B, C, D, E, F, G> Tuple7<A, B, C, D, E, F, G> tuple(A a, B b, C c, D d, E e, F f, G g)
    {
        return new Tuple7<A, B, C, D, E, F, G>(a, b, c, d, e, f, g);
    }


}