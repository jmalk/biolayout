package org.BioLayoutExpress3D.Models.Lathe3D.Variants;

import javax.media.opengl.*;
import static java.lang.Math.*;
import org.BioLayoutExpress3D.Models.*;
import org.BioLayoutExpress3D.Models.Lathe3D.*;

/**
*
* So named because it resembles a rose. The equation is:
* r = a cos(k . angle)
*
* There will be k or 2k petals depending on if k is an odd
* or even integer.
*
* When angle == 0, r == a == radius, so we use radius as the
* 'a' value.
*
* After obtaining r, we must convert back to cartesian (x,y)
* coordinates.
*
*
* @see org.BioLayoutExpress3D.Models.Lathe3D.Lathe3DShape
* @author Andrew Davison, 2005, rewrite for BioLayout Express3D & JOGL by Thanos Theo, 2011
* @version 3.0.0.0
*
*/

public class Rhodonea3DShape extends Lathe3DShape
{

    /**
    *  The Rhodonea3DShape class constructor.
    */
    public Rhodonea3DShape(GL2 gl, Lathe3DShapeAngleIncrements lathe3DShapeAngleIncrement, Lathe3DSettings lathe3DSettings, ModelSettings modelSettings)
    {
        super(gl, lathe3DShapeAngleIncrement, lathe3DSettings, modelSettings);
    }

    /**
    *  Overriden method that rotates the radius using a petal of points.
    */
    @Override
    protected float xCoord(float radius, float angle)
    {
        return  (float)( radius * cos(lathe3DSettings.k * angle) * cos(angle) );
    }

    /**
    *  Overriden method that rotates the radius using a petal of points.
    */
    @Override
    protected float zCoord(float radius, float angle)
    {
        return  (float)( radius * cos(lathe3DSettings.k * angle) * sin(angle) );
    }

    /**
    *  The Rhodonea3DShape shapeName.
    */
    @Override
    public String toString()
    {
        return "Rhodonea3DShapeModel: " + super.getShapeName();
    }

}