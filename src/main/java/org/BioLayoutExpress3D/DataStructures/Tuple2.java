package org.BioLayoutExpress3D.DataStructures;

/**
*
* Tuple2 class with a general generics representation for a tuple with 2 objects.
*
* @author Thanos Theo, 2008-2009
* @version 3.0.0.0
*/

public class Tuple2<A, B> implements Comparable<Object>
{

    /**
    *  Tuple's first object to be stored.
    */
    public final A first;

    /**
    *  Tuple's second object to be stored.
    */
    public final B second;

    /**
    *  Constant value to be used for the hash code calculation according to Joshua Bloch's recipe.
    */
    protected final int RESULT_CONSTANT_VALUE = 37;

    /**
    *  The Tuple2 constructor, initializes all the tuple's objects.
    */
    public Tuple2(A first, B second)
    {
       this.first = first;
       this.second = second;
    }

    /**
    *  Overriden equals() method for Tuple2.
    */
    @Override
    public boolean equals(Object obj)
    {
        return (obj instanceof Tuple2)
               && first.equals( ( (Tuple2)obj ).first ) && second.equals( ( (Tuple2)obj ).second );
    }

    /**
    *  Overriden hashCode() method for Tuple2.
    *  Calculation is made according to Joshua Bloch's recipe.
    */
    @Override
    public int hashCode()
    {
        int result = 17;
        result = RESULT_CONSTANT_VALUE * result + ( (first == null) ? 0 : first.hashCode() );
        result = RESULT_CONSTANT_VALUE * result + ( (second == null) ? 0 : second.hashCode() );

        return result;
    }

    /**
    *  Overriden compareTo() method for Tuple2.
    *  Note, it uses a hashCode() for comparison, should be using something better (may not produce proper comparisons in relevant data structures).
    */
    @Override
    public int compareTo(Object obj)
    {
        if ( !(obj instanceof Tuple2) ) return -1;

        String className = getClass().getSimpleName();
        String argClassName = obj.getClass().getSimpleName();
        int firstCompare = className.compareTo(argClassName);
        if (firstCompare != 0)
            return firstCompare;

        return (obj.hashCode() < this.hashCode() ) ? -1 : ( ( obj.hashCode() == this.hashCode() ) ? 0 : 1);
    }

    /**
    *  Overriden toString() method for Tuple2.
    */
    @Override
    public String toString()
    {
        StringBuilder returnString = new StringBuilder();

        returnString.append("(");
        returnString.append( first.toString() );
        returnString.append(",");
        returnString.append( second.toString() );
        returnString.append(")");

        return returnString.toString();
    }


}