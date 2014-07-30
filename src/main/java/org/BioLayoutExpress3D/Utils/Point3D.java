package org.BioLayoutExpress3D.Utils;

import static java.lang.Math.*;
import org.BioLayoutExpress3D.Graph.Matrix5D;
import org.BioLayoutExpress3D.Graph.Vector5D;

/**
*
* @author Anton Enright, full refactoring by Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public final class Point3D
{
    public float x = 0.0f;
    public float y = 0.0f;
    public float z = 0.0f;
    public float w = 0.0f;

    public Point3D()
    {
        this(0.0f, 0.0f, 0.0f);
    }

    public Point3D(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point3D(float x, float y, float z, float w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Point3D(double x, double y, double z)
    {
        this.x = (float)x;
        this.y = (float)y;
        this.z = (float)z;
    }

    public Point3D(double x, double y, double z, double w)
    {
        this.x = (float)x;
        this.y = (float)y;
        this.z = (float)z;
        this.w = (float)w;
    }

    public Point3D(Point3D point3D)
    {
        this.x = point3D.x;
        this.y = point3D.y;
        this.z = point3D.z;
        this.w = point3D.w;
    }

    public float getX()
    {
	return x;
    }
    
    public float getX(Matrix5D viewMatrix)
    {
        return viewMatrix.matrix5D[0][0] * this.x
	     + viewMatrix.matrix5D[0][1] * this.y
	     + viewMatrix.matrix5D[0][2] * this.z
	     + viewMatrix.matrix5D[0][3] * this.w
	     + viewMatrix.matrix5D[0][4] * 1.0f;
    }

    public void setX(float x)
    {
        this.x = x;
    }

    public float getY()
    {
	return y;
    }
    
    public float getY(Matrix5D viewMatrix)
    {
        return viewMatrix.matrix5D[1][0] * this.x
	     + viewMatrix.matrix5D[1][1] * this.y
	     + viewMatrix.matrix5D[1][2] * this.z
	     + viewMatrix.matrix5D[1][3] * this.w
	     + viewMatrix.matrix5D[1][4] * 1.0f;
    }

    public void setY(float y)
    {
        this.y = y;
    }

    public float getZ()
    {
	return z;
    }
    
    public float getZ(Matrix5D viewMatrix)
    {
	float apparentZ = 0.0f;
	float apparentW = 0.0f;

        apparentZ = viewMatrix.matrix5D[2][0] * this.x
	          + viewMatrix.matrix5D[2][1] * this.y
	          + viewMatrix.matrix5D[2][2] * this.z
	          + viewMatrix.matrix5D[2][3] * this.w
	          + viewMatrix.matrix5D[2][4] * 1.0f;

	apparentW = viewMatrix.matrix5D[3][0] * this.x
	          + viewMatrix.matrix5D[3][1] * this.y
	          + viewMatrix.matrix5D[3][2] * this.z
	          + viewMatrix.matrix5D[3][3] * this.w
	          + viewMatrix.matrix5D[3][4] * 1.0f;

	return (float)sqrt( apparentZ*apparentZ + apparentW*apparentW );
    }

    public void setZ(float z)
    {
        this.z = z;
    }

    public float getW()
    {
        return w;
    }

    public void setW(float w)
    {
        this.w = w;
    }

    public void setLocation(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public void setLocation(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setLocation(float x, float y, float z, float w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public void setLocation(Point3D point)
    {
        this.x = point.x;
        this.y = point.y;
        this.z = point.z;
        this.w = point.w;
    }

    public static float distance(float x1, float y1, float z1, float x2, float y2, float z2)
    {
        x1 -= x2;
        y1 -= y2;
        z1 -= z2;

        return (float)sqrt(x1 * x1 + y1 * y1 + z1 * z1);
    }

    public static float distance(float x1, float y1, float z1, float w1, float x2, float y2, float z2, float w2)
    {
        x1 -= x2;
        y1 -= y2;
        z1 -= z2;
        w1 -= w2;

        return (float)sqrt(x1 * x1 + y1 * y1 + z1 * z1 + w1 * w1);
    }

    @Override
    public String toString()
    {
	if ( !(this.w == 0.0) )
	    return "( " + x + ", " + y + ", " + z + ", " + w + " )";
	else
        return "( " + x + ", " + y + ", " + z + " )";
    }


}
