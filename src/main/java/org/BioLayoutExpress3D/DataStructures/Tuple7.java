package org.BioLayoutExpress3D.DataStructures;

/**
*
* Tuple7 class with a general generics representation for a tuple with 7 objects.
*
* @author Thanos Theo, 2008-2009-2011
* @version 3.0.0.0
*/

public class Tuple7<A, B, C, D, E, F, G> extends Tuple6<A, B, C, D, E, F>
{

    /**
    *  Tuple's seventh object to be stored.
    */
    public final G seventh;

    /**
    *  The Tuple7 constructor, initializes all the tuple's objects.
    */
    public Tuple7(A first, B second, C third, D fourth, E fifth, F sixth, G seventh)
    {
       super(first, second, third, fourth, fifth, sixth);

       this.seventh = seventh;
    }

    /**
    *  Overriden equals() method for Tuple7.
    */
    @Override
    public boolean equals(Object obj)
    {
        return (obj instanceof Tuple7)
               && first.equals( ( (Tuple7)obj ).first )   && second.equals( ( (Tuple7)obj ).second ) && third.equals( ( (Tuple7)obj ).third )
               && fourth.equals( ( (Tuple7)obj ).fourth ) && fifth.equals( ( (Tuple7)obj ).fifth )   && sixth.equals( ( (Tuple7)obj ).sixth )
               && seventh.equals( ( (Tuple7)obj ).seventh );
    }

    /**
    *  Overriden hashCode() method for Tuple7.
    *  Calculation is made according to Joshua Bloch's recipe.
    */
    @Override
    public int hashCode()
    {
        return RESULT_CONSTANT_VALUE * super.hashCode() + ( (seventh == null) ? 0 : seventh.hashCode() );
    }

    /**
    *  Overriden compareTo() method for Tuple7.
    *  Note, it uses a hashCode() for comparison, should be using something better (may not produce proper comparisons in relevant data structures).
    */
    @Override
    public int compareTo(Object obj)
    {
        if ( !(obj instanceof Tuple7) ) return -1;

        String className = getClass().getSimpleName();
        String argClassName = obj.getClass().getSimpleName();
        int firstCompare = className.compareTo(argClassName);
        if (firstCompare != 0)
            return firstCompare;

        return (obj.hashCode() < this.hashCode() ) ? -1 : ( ( obj.hashCode() == this.hashCode() ) ? 0 : 1);
    }

    /**
    *  Overriden toString() method for Tuple7.
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
        returnString.append( seventh.toString() );
        returnString.append(")");

        return returnString.toString();
    }


}