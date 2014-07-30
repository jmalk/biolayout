package org.BioLayoutExpress3D.DataStructures;

/**
*
* Tuple4 class with a general generics representation for a tuple with 4 objects.
*
* @author Thanos Theo, 2008-2009
* @version 3.0.0.0
*/

public class Tuple4<A, B, C, D> extends Tuple3<A, B, C>
{

    /**
    *  Tuple's fourth object to be stored.
    */
    public final D fourth;

    /**
    *  The Tuple4 constructor, initializes all the tuple's objects.
    */
    public Tuple4(A first, B second, C third, D fourth)
    {
       super(first, second, third);

       this.fourth = fourth;
    }

    /**
    *  Overriden equals() method for Tuple4.
    */
    @Override
    public boolean equals(Object obj)
    {
        return (obj instanceof Tuple4)
               && first.equals( ( (Tuple4)obj ).first ) && second.equals( ( (Tuple4)obj ).second ) && third.equals( ( (Tuple4)obj ).third )
               && fourth.equals( ( (Tuple4)obj ).fourth );
    }

    /**
    *  Overriden hashCode() method for Tuple4.
    *  Calculation is made according to Joshua Bloch's recipe.
    */
    @Override
    public int hashCode()
    {
        return RESULT_CONSTANT_VALUE * super.hashCode() + ( (fourth == null) ? 0 : fourth.hashCode() );
    }

    /**
    *  Overriden compareTo() method for Tuple4.
    *  Note, it uses a hashCode() for comparison, should be using something better (may not produce proper comparisons in relevant data structures).
    */
    @Override
    public int compareTo(Object obj)
    {
        if ( !(obj instanceof Tuple4) ) return -1;

        String className = getClass().getSimpleName();
        String argClassName = obj.getClass().getSimpleName();
        int firstCompare = className.compareTo(argClassName);
        if (firstCompare != 0)
            return firstCompare;

        return (obj.hashCode() < this.hashCode() ) ? -1 : ( ( obj.hashCode() == this.hashCode() ) ? 0 : 1);
    }

    /**
    *  Overriden toString() method for Tuple4.
    */
    @Override
    public String toString()
    {
        StringBuilder returnString = new StringBuilder();

        returnString.append("(");
        returnString.append( first.toString() );
        returnString.append(",");
        returnString.append( second.toString() );
        returnString.append(",");
        returnString.append( third.toString() );
        returnString.append(",");
        returnString.append( fourth.toString() );
        returnString.append(")");

        return returnString.toString();
    }


}