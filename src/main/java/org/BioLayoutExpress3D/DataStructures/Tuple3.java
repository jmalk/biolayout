package org.BioLayoutExpress3D.DataStructures;

/**
*
* Tuple3 class with a general generics representation for a tuple with 3 objects.
*
* @author Thanos Theo, 2008-2009
* @version 3.0.0.0
*/

public class Tuple3<A, B, C> extends Tuple2<A, B>
{

    /**
    *  Tuple's third object to be stored.
    */
    public final C third;

    /**
    *  The Tuple3 constructor, initializes all the tuple's objects.
    */
    public Tuple3(A first, B second, C third)
    {
       super(first, second);

       this.third = third;
    }

    /**
    *  Overriden equals() method for Tuple3.
    */
    @Override
    public boolean equals(Object obj)
    {
        return (obj instanceof Tuple3)
               && first.equals( ( (Tuple3)obj ).first ) && second.equals( ( (Tuple3)obj ).second ) && third.equals( ( (Tuple3)obj ).third );
    }

    /**
    *  Overriden hashCode() method for Tuple3.
    *  Calculation is made according to Joshua Bloch's recipe.
    */
    @Override
    public int hashCode()
    {
        return RESULT_CONSTANT_VALUE * super.hashCode() + ( (third == null) ? 0 : third.hashCode() );
    }

    /**
    *  Overriden compareTo() method for Tuple3.
    *  Note, it uses a hashCode() for comparison, should be using something better (may not produce proper comparisons in relevant data structures).
    */
    @Override
    public int compareTo(Object obj)
    {
        if ( !(obj instanceof Tuple3) ) return -1;

        String className = getClass().getSimpleName();
        String argClassName = obj.getClass().getSimpleName();
        int firstCompare = className.compareTo(argClassName);
        if (firstCompare != 0)
            return firstCompare;

        return (obj.hashCode() < this.hashCode() ) ? -1 : ( ( obj.hashCode() == this.hashCode() ) ? 0 : 1);
    }

    /**
    *  Overriden toString() method for Tuple3.
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
        returnString.append(")");

        return returnString.toString();
    }


}