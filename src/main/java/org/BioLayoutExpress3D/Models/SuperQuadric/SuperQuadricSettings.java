package org.BioLayoutExpress3D.Models.SuperQuadric;

import static java.lang.Math.*;

/**
*
* SuperQuadricSettings is the class that acts as a placeholder of the SuperQuadricShape variables needed for the parametric polar coordinate equations.
*
* @see org.BioLayoutExpress3D.Models.SuperQuadric.SuperQuadricShape
* @author Jeff Molofee, 2000, rewrite for BioLayout Express3D by Thanos Theo, 2011
* @version 3.0.0.0
*
*/

public class SuperQuadricSettings
{

    /**
    *  Scaling factor for x.
    */
    public float a1 = 1.0f;

    /**
    *  Scaling factor for y.
    */
    public float a2 = 1.0f;

    /**
    *  Scaling factor for z.
    */
    public float a3 = 1.0f;

    /**
    *  For generating toroids. This is the inner radius.
    */
    public float alpha = 2.0f;

    /**
    *  North-South Roundness factor.
    */
    public float n = 1.0f;

    /**
    *  East-West Squareness factor.
    */
    public float e = 1.0f;

    /**
    *  Initial U value.
    */
    public float u1 = (float)(-PI / 2.0f);

    /**
    *  Final U value.
    */
    public float u2 = (float)( PI / 2.0f);

    /**
    *  Initial V value.
    */
    public float v1 = (float)(-PI);

    /**
    *  Final V value.
    */
    public float v2 = (float)( PI);

    /**
    *  Number of segments for U.
    */
    public int uSegments = 8;

    /**
    *  Number of segments for V.
    */
    public int vSegments = 8;

    /**
    *  Initial S value.
    */
    public float s1 = 0.0f;

    /**
    *  Final S value.
    */
    public float s2 = 1.0f;

    /**
    *  Initial T value.
    */
    public float t1 = 0.0f;

    /**
    *  Final T value.
    */
    public float t2 = 1.0f;

    /**
    *  SuperQuadricShapeType enum type.
    */
    public SuperQuadricShapeTypes superQuadricShapeType = SuperQuadricShapeTypes.ELLIPSOID;


}