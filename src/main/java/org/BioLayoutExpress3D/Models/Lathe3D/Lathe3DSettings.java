package org.BioLayoutExpress3D.Models.Lathe3D;

/**
*
* Lathe3DSettings is the class that acts as a placeholder of the Lathe3DShape variables needed for the lathe 3D revolution equations.
*
* @see org.BioLayoutExpress3D.Models.Lathe3D.Lathe3DShape
* @author Thanos Theo, 2011
* @version 3.0.0.0
*
*/

public class Lathe3DSettings
{

    /**
    *  Xs input array.
    */
    public float[] xsIn = { -1.0f, -1.0f };

    /**
    *  Ys input array.
    */
    public float[] ysIn = {  0.0f,  1.0f };

    /**
    *  Spline step.
    */
    public int splineStep = 2;

    /**
    *  The k value used for the rhodonea algorithm: k petals if k is odd or 2k petals if k is even.
    *  For example, for 8 petals use k = 4.0f. Default value of k = 1.0f creates a circle.
    */
    public float k = 1.0f;

    /**
    *  Lathe3DShapeTypes enum type.
    */
    public Lathe3DShapeTypes lathe3DShapeType = Lathe3DShapeTypes.CIRCLE;


}