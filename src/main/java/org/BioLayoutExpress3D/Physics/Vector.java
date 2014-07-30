package org.BioLayoutExpress3D.Physics;

import static java.lang.Math.*;

/**
*
* The Vector class is used for storing a pair of double values and all its related vector mathematical operations.
*
* @see org.BioLayoutExpress3D.Physics.Particle
* @author Thanos Theo, Michael Kargas, 2008-2009
* @version 3.0.0.0
*/

public final class Vector
{
    /**
    *  The x value of the vector.
    */
    public double x = 0.0;

    /**
    *  The y value of the vector.
    */
    public double y = 0.0;

    /**
    *  The first constructor of the vector.
    */
    public Vector()
    {
        this.x = 0.0;
        this.y = 0.0;
    }

    /**
    *  The second constructor of the vector. Initializes the vector with initial x, y values.
    */
    public Vector(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    /**
    *  Calculates the distance between two vectors.
    */
    public static double vectorDistance(Vector vect1, Vector vect2)
    {
        return sqrt( (vect2.x - vect1.x) * (vect2.x - vect1.x) + (vect2.y - vect1.y) * (vect2.y - vect1.y) );
    }

    /**
    *  Adds two vectors and puts the result to the third one.
    */
    public static void vectorAdd(Vector vect1, Vector vect2, Vector vector)
    {
        vector.x = vect1.x + vect2.x;
        vector.y = vect1.y + vect2.y;
    }

    /**
    *  Substracts two vectors and puts the result to the third one.
    */
    public static void vectorSubstract(Vector vect1, Vector vect2, Vector vector)
    {
        vector.x = vect1.x - vect2.x;
        vector.y = vect1.y - vect2.y;
    }

    /**
    *  Negates a vector and puts the result to the second one.
    */
    public static void vectorNegate(Vector vect1, Vector vector)
    {
        vector.x = - vect1.x;
        vector.y = - vect1.y;
    }

    /**
    *  Scales a vector by a given factor and puts the result to the second one.
    */
    public static void vectorScale(Vector vect1, Vector vector, double scaleFactor)
    {
        vector.x = vect1.x * scaleFactor;
        vector.y = vect1.y * scaleFactor;
    }

    /**
    *  Rotates a vector by a given factor and puts the result to the second one.
    */
    public static void vectorRotate(Vector vect1, Vector vector, double rotateFactor)
    {
        vector.x =  vect1.x * cos(rotateFactor) + vect1.y * sin(rotateFactor);
        vector.y = -vect1.x * sin(rotateFactor) + vect1.y * cos(rotateFactor);
    }

    /**
    *  Sets a vector (puts the result to the first one).
    */
    public static void vectorSet(Vector vect1, Vector vect2)
    {
        vect1.x = vect2.x;
        vect1.y = vect2.y;
    }

    /**
    *  Normalizes a vector.
    */
    public static void vectorNormalize(Vector vector)
    {
        double r = sqrt(vector.x * vector.x + vector.y * vector.y);
        if (r != 0.0)
        {
            vector.x /= r;
            vector.y /= r;
        }
    }


}