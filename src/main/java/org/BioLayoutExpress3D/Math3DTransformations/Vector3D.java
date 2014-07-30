package org.BioLayoutExpress3D.Math3DTransformations;


import static java.lang.Math.*;

/**
*
* Vector3D is a class encapsulating operations for a 3D vector.
*
* @author Thanos Theo, Michael Kargas, 2010
* @version 3.0.0.0
*/

public class Vector3D
{

    /**
    *  The x float variable of the vector.
    */
    public float x = 0.0f;

    /**
    *  The y float variable of the vector.
    */
    public float y = 0.0f;

    /**
    *  The z float variable of the vector.
    */
    public float z = 0.0f;

    /**
    *  The first constructor of the Vector3D class.
    */
    public Vector3D()
    {
	this.x = 0.0f;
	this.y = 0.0f;
	this.z = 0.0f;
    }

    /**
    *  The second constructor of the Vector3D class.
    */
    public Vector3D(float x, float y, float z)
    {
	this.x = x;
	this.y = y;
	this.z = z;
    }

    /**
    *  Vector addition operation with a given vector.
    */
    public final Vector3D addWithVector3D(Vector3D vector3D)
    {
        Vector3D newVector3D = new Vector3D();
	newVector3D.x = this.x + vector3D.x;
	newVector3D.y = this.y + vector3D.y;
	newVector3D.z = this.z + vector3D.z;

        return newVector3D;
    }

    /**
    *  Vector subtraction operation with a given vector.
    */
    public final Vector3D subtractWithVector3D(Vector3D vector3D)
    {
        Vector3D newVector3D = new Vector3D();
	newVector3D.x = this.x - vector3D.x;
	newVector3D.y = this.y - vector3D.y;
	newVector3D.z = this.z - vector3D.z;

        return newVector3D;
    }

    /**
    *  Vector scalar operation with a given scalar.
    */
    public final Vector3D multiplyWithScalar(float scale)
    {
        Vector3D newVector3D = new Vector3D();
	newVector3D.x = this.x * scale;
	newVector3D.y = this.y * scale;
	newVector3D.z = this.z * scale;

        return newVector3D;
    }

    /**
    *  Dot product operation with a given vector.
    */
    public final float dotProductWithVector3D(Vector3D vector3D)
    {
        return this.x * vector3D.x + this.y * vector3D.y + this.z * vector3D.z;
    }

    /**
    *  Cross product operation with a given vector.
    */
    public final Vector3D crossProductWithVector3D(Vector3D vector3D)
    {
	Vector3D newVector3D = new Vector3D();
	newVector3D.x = this.y * vector3D.z - this.z * vector3D.y;
	newVector3D.y = this.z * vector3D.x - this.x * vector3D.z;
	newVector3D.z = this.x * vector3D.y - this.y * vector3D.x;

	return newVector3D;
    }

    /**
    *  Returns the length of the vector.
    */
    public final float length()
    {
        return (float)sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    /**
    *  Normalize the vector.
    */
    public final void normalize()
    {
        float length = this.length();
        if (length != 0.0f)
        {
            x /= length;
            y /= length;
            z /= length;
        }
    }

    /**
    *  Returns the normalized version of the vector.
    */
    public final Vector3D normalized()
    {
        this.normalize();

        return this;
    }

    /**
    *  Returns the negated version of the vector.
    */
    public final Vector3D negate()
    {
        Vector3D newVector3D = new Vector3D();
        newVector3D.x = -x;
        newVector3D.y = -y;
        newVector3D.z = -z;

        return newVector3D;
    }

    /**
    *  Overriden toString() method for the vector.
    */
    @Override
    public String toString()
    {
        return String.format("%f   %f   %f\n", x, y, z);
    }


}