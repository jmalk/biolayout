package org.BioLayoutExpress3D.Graph;

import static java.lang.Math.*;

/**
*
*
* It has been adapted from Vector3D, originally by
* @author Thanos Theo, Michael Kargas, 2010
* @version 3.0.0.0
*
* by Joshua Malkinson, 2014
*/

public class Vector5D
{
    /**
    *  The a float variable of the vector.
    */
    public float a = 0.0f;

    /**
    *  The b float variable of the vector.
    */
    public float b = 0.0f;

    /**
    *  The c float variable of the vector.
    */
    public float c = 0.0f;

    /**
    *  The d float variable of the vector.
    */
    public float d = 0.0f;
   
    /**
    *  The e float variable of the vector.
    */
    public float e = 0.0f;
   
    /**
    *  The first constructor of the Vector5D class.
    */
    public Vector5D()
    {
	this.a = 0.0f;
        this.b = 0.0f;
	this.c = 0.0f;
	this.d = 0.0f;
	this.e = 0.0f;
    }

    /**
    *  The second constructor of the Vector5D class.
    */
    public Vector5D(float a, float b, float c, float d, float e)
    {
	this.a = a;
	this.b = b;
	this.c = c;
	this.d = d;
	this.e = e;
    }

    /**
    *  Vector addition operation with a given vector.
    */
    public final Vector5D addWithVector5D(Vector5D vector5D)
    {
        Vector5D newVector5D = new Vector5D();
	newVector5D.a = this.a + vector5D.a;
	newVector5D.b = this.b + vector5D.b;
	newVector5D.c = this.c + vector5D.c;
	newVector5D.d = this.d + vector5D.d;
	newVector5D.e = this.e + vector5D.e;

        return newVector5D;
    }

    /**
    *  Vector subtraction operation with a given vector.
    */
    public final Vector5D subtractWithVector5D(Vector5D vector5D)
    {
        Vector5D newVector5D = new Vector5D();
	newVector5D.a = this.a - vector5D.a;
	newVector5D.b = this.b - vector5D.b;
	newVector5D.c = this.c - vector5D.c;
	newVector5D.d = this.d - vector5D.d;
	newVector5D.e = this.e - vector5D.e;

        return newVector5D;
    }

    /**
    *  Vector scalar operation with a given scalar.
    */
    public final Vector5D multiplyWithScalar(float scale)
    {
        Vector5D newVector5D = new Vector5D();
	newVector5D.a = this.a * scale;    
	newVector5D.b = this.b * scale;
	newVector5D.c = this.c * scale;
	newVector5D.d = this.d * scale;
	newVector5D.e = this.e * scale;	

        return newVector5D;
    }

    /**
    *  Dot product operation with a given vector.
    */
    public final float dotProductWithVector5D(Vector5D vector5D)
    {
        return this.a * vector5D.a + this.b * vector5D.b + this.c * vector5D.c + this.d * vector5D.d + this.e * vector5D.e;
    }

    /**
    *  Returns the length of the vector.
    */
    public final float length()
    {
        return (float)sqrt(this.a * this.a + this.b * this.b + this.c * this.c + this.d * this.d + this.e * this.e);
    }

    /**
    *  Normalize the vector.
    */
    public final void normalize()
    {
        float length = this.length();
        if (length != 0.0f)
        {
            a /= length;
            b /= length;
            c /= length;
	    d /= length;
	    e /= length;
        }
    }

    /**
    *  Returns the normalized version of the vector.
    */
    public final Vector5D normalized()
    {
        this.normalize();

        return this;
    }

    /**
    *  Returns the negated version of the vector.
    */
    public final Vector5D negate()
    {
        Vector5D newVector5D = new Vector5D();
        newVector5D.a = -a;
	newVector5D.b = -b;
	newVector5D.c = -c;
        newVector5D.d = -d;
	newVector5D.e = -e;

        return newVector5D;
    }

    /**
    *  Overriden toString() method for the vector.
    */
    @Override
    public String toString()
    {
        return String.format("%f   %f   %f   %f   %f\n", a, b, c, d, e);
    }


}
