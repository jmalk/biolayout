package org.BioLayoutExpress3D.DataStructures;

/**
*
* Tuple6 class with a general generics representation for a tuple with 6 objects.
*
* @author Thanos Theo, 2008-2009
* @version 3.0.0.0
*/

public class Tuple6<A, B, C, D, E, F> extends Tuple5<A, B, C, D, E>
{

    /**
    *  Tuple's sixth object to be stored.
    */
    public final F sixth;

    /**
    *  The Tuple6 constructor, initializes all the tuple's objects.
    */
    public Tuple6(A first, B second, C third, D fourth, E fifth, F sixth)
    {
       super(first, second, third, fourth, fifth);

       this.sixth = sixth;
    }

    /**
    *  Overriden equals() method for Tuple6.
    */
    @Override
    public boolean equals(Object obj)
    {
        return (obj instanceof Tuple6)
               && first.equals( ( (Tuple6)obj ).first ) && second.equals( ( (Tuple6)obj ).second ) && third.equals( ( (Tuple6)obj ).third )
               && fourth.equals( ( (Tuple6)obj ).fourth ) && fifth.equals( ( (Tuple6)obj ).fifth ) && sixth.equals( ( (Tuple6)obj ).sixth );
    }

    /**
    *  Overriden hashCode() method for Tuple6.
    *  Calculation is made according to Joshua Bloch's recipe.
    */
    @Override
    public int hashCode()
    {
        return RESULT_CONSTANT_VALUE * super.hashCode() + ( (sixth == null) ? 0 : sixth.hashCode() );
    }

    /**
    *  Overriden compareTo() method for Tuple6.
    *  Note, it uses a hashCode() for comparison, should be using something better (may not produce proper comparisons in relevant data structures).
    */
    @Override
    public int compareTo(Object obj)
    {
        if ( !(obj instanceof Tuple6) ) return -1;

        String className = getClass().getSimpleName();
        String argClassName = obj.getClass().getSimpleName();
        int firstCompare = className.compareTo(argClassName);
        if (firstCompare != 0)
            return firstCompare;

        return (obj.hashCode() < this.hashCode() ) ? -1 : ( ( obj.hashCode() == this.hashCode() ) ? 0 : 1);
    }

    /**
    *  Overriden toString() method for Tuple6.
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
        returnString.append(",");
        returnString.append( sixth.toString() );
        returnString.append(")");

        return returnString.toString();
    }


}