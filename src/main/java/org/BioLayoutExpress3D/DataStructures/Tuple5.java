package org.BioLayoutExpress3D.DataStructures;

/**
*
* Tuple5 class with a general generics representation for a tuple with 5 objects.
*
* @author Thanos Theo, 2008-2009
* @version 3.0.0.0
*/

public class Tuple5<A, B, C, D, E> extends Tuple4<A, B, C, D>
{

    /**
    *  Tuple's fifth object to be stored.
    */
    public final E fifth;

    /**
    *  The Tuple5 constructor, initializes all the tuple's objects.
    */
    public Tuple5(A first, B second, C third, D fourth, E fifth)
    {
       super(first, second, third, fourth);

       this.fifth = fifth;
    }

    /**
    *  Overriden equals() method for Tuple5.
    */
    @Override
    public boolean equals(Object obj)
    {
        return (obj instanceof Tuple5)
               && first.equals( ( (Tuple5)obj ).first ) && second.equals( ( (Tuple5)obj ).second ) && third.equals( ( (Tuple5)obj ).third )
               && fourth.equals( ( (Tuple5)obj ).fourth ) && fifth.equals( ( (Tuple5)obj ).fifth );
    }

    /**
    *  Overriden hashCode() method for Tuple5.
    *  Calculation is made according to Joshua Bloch's recipe.
    */
    @Override
    public int hashCode()
    {
        return RESULT_CONSTANT_VALUE * super.hashCode() + ( (fifth == null) ? 0 : fifth.hashCode() );
    }

    /**
    *  Overriden compareTo() method for Tuple5.
    *  Note, it uses a hashCode() for comparison, should be using something better (may not produce proper comparisons in relevant data structures).
    */
    @Override
    public int compareTo(Object obj)
    {
        if ( !(obj instanceof Tuple5) ) return -1;

        String className = getClass().getSimpleName();
        String argClassName = obj.getClass().getSimpleName();
        int firstCompare = className.compareTo(argClassName);
        if (firstCompare != 0)
            return firstCompare;

        return (obj.hashCode() < this.hashCode() ) ? -1 : ( ( obj.hashCode() == this.hashCode() ) ? 0 : 1);
    }

    /**
    *  Overriden toString() method for Tuple5.
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
        returnString.append(",");
        returnString.append( fifth.toString() );
        returnString.append(")");

        return returnString.toString();
    }


}