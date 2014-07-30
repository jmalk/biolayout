package org.BioLayoutExpress3D.Models.Lathe3D.Variants;

import javax.media.opengl.*;
import static java.lang.Math.*;
import org.BioLayoutExpress3D.Models.*;
import org.BioLayoutExpress3D.Models.Lathe3D.*;

/**
*
* An ellipse has a semi-major axis of length a,
* and semi-minor axis of length b.
*
* The ellipse can be represented by the equations:
*   x = a cos(angle)
*   y = b sin(angle)
*
* The radius is the semi-major axis value (a), and the
* semi-minor axis is set to be some fraction of a.
* We hardwire b == 0.5 * radius.
*
*
* @see org.BioLayoutExpress3D.Models.Lathe3D.Lathe3DShape
* @author Andrew Davison, 2005, rewrite for BioLayout Express3D & JOGL by Thanos Theo, 2011
* @version 3.0.0.0
*
*/

public class Ellipse3DShape extends Lathe3DShape
{

    /**
    *  The Ellipse3DShape class constructor.
    */
    public Ellipse3DShape(GL2 gl, Lathe3DShapeAngleIncrements lathe3DShapeAngleIncrement, Lathe3DSettings lathe3DSettings, ModelSettings modelSettings)
    {
        super(gl, lathe3DShapeAngleIncrement, lathe3DSettings, modelSettings);
    }

    /**
    *  Overriden method that rotates the radius using an ellipse of points.
    */
    @Override
    protected float zCoord(float radius, float angle)
    {
        return (float)( 0.5f * radius * sin(angle) ); // b == a / 2.0f
    }

    /**
    *  The Ellipse3DShape shapeName.
    */
    @Override
    public String toString()
    {
        return "Ellipse3DShapeModel: " + super.getShapeName();
    }

}
