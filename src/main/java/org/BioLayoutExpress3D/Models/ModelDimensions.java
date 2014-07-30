package org.BioLayoutExpress3D.Models;

import org.BioLayoutExpress3D.Utils.*;
import static org.BioLayoutExpress3D.Models.ModelShape.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/*
*
* This class calculates the 'edge' coordinates for the model
* along its three dimensions.
*
* The edge coords are used to calculate the model's:
*     * width, height, depth
*     * its largest dimension (width, height, or depth)
*     * (x, y, z) center point
*
* @author Andrew Davison, 2006, rewrite for BioLayout Express3D by Thanos Theo, 2011
* @version 3.0.0.0
*
*/

public class ModelDimensions
{

    /**
    *  X-axis edge coordinates.
    */
    private float leftPoint = 0.0f,  rightPoint = 0.0f;

    /**
    *  Y-axis edge coordinates.
    */
    private float  topPoint = 0.0f, bottomPoint = 0.0f;

    /**
    *  Z-axis edge coordinates.
    */
    private float  farPoint = 0.0f,   nearPoint = 0.0f;

    /**
    *  Model name variable.
    */
    private String modelName = "";

    /**
    *  The ModelDimensions class constructor.
    */
    public ModelDimensions(String modelName)
    {
        this.modelName = modelName;
    }

    /**
    *  Initializes the model's edge coordinates.
    */
    public void set(Point3D vertex)
    {
        leftPoint = rightPoint = vertex.getX();

        bottomPoint = topPoint = vertex.getY();

        farPoint = nearPoint = vertex.getZ();
    }

    /**
    *  Initializes the model's edge coordinates.
    *  Overloaded version of the above method to use x/y/z coords directly.
    */
    public void set(float x, float y, float z)
    {
        rightPoint = leftPoint = x;

        topPoint = bottomPoint = y;

        nearPoint = farPoint = z;
    }

    /**
    *  Updates the edge coordinates using the supplied vertex.
    */
    public void update(Point3D vertex)
    {
        if (vertex.getX() > rightPoint)
          rightPoint = vertex.getX();
        if (vertex.getX() < leftPoint)
          leftPoint = vertex.getX();

        if (vertex.getY() > topPoint)
          topPoint = vertex.getY();
        if (vertex.getY() < bottomPoint)
          bottomPoint = vertex.getY();

        if (vertex.getZ() > nearPoint)
          nearPoint = vertex.getZ();
        if (vertex.getZ() < farPoint)
          farPoint = vertex.getZ();
    }

    /**
    *  Updates the edge coordinates using the supplied vertex.
    *  Overloaded version of the above method to use x/y/z coords directly.
    */
    public void update(float x, float y, float z)
    {
        if (x > rightPoint)
          rightPoint = x;
        if (x < leftPoint)
          leftPoint = x;

        if (y > topPoint)
          topPoint = y;
        if (y < bottomPoint)
          bottomPoint = y;

        if (z > nearPoint)
          nearPoint = z;
        if (z < farPoint)
          farPoint = z;
    }

    // ------------- use the edge coordinates ----------------------------

    /**
    *  Gets the model's width.
    */
    public float getWidth()
    {
        return (rightPoint - leftPoint);
    }

    /**
    *  Gets the model's height.
    */
    public float getHeight()
    {
        return (topPoint - bottomPoint);
    }

    /**
    *  Gets the model's depth.
    */
    public float getDepth()
    {
        return (nearPoint - farPoint);
    }

    /**
    *  Gets the model's largest axis.
    */
    public float getLargestAxis()
    {
        float largest =  getWidth();
        float height  = getHeight();
        float depth   =  getDepth();

        if (height > largest)
            largest = height;
        if (depth > largest)
            largest = depth;

        return largest;
    }

    /**
    *  Gets the model's center.
    */
    public Point3D getCenter()
    {
        float x = (rightPoint +   leftPoint) / 2.0f;
        float y = (  topPoint + bottomPoint) / 2.0f;
        float z = (nearPoint  +    farPoint) / 2.0f;

        return new Point3D(x, y, z);
    }

    /**
    *  Reports on the model's dimensions.
    */
    public void reportDimensions()
    {
        if (DEBUG_BUILD)
        {
            Point3D center = getCenter();

            println("\nModel " + modelName + " dimensions:");
            println("x Coords: " + DECIMAL_FORMAT.format(leftPoint)   + " to " + DECIMAL_FORMAT.format(rightPoint) );
            println("  Center: " + DECIMAL_FORMAT.format( center.getX() ) + "; Width: "  + DECIMAL_FORMAT.format( getWidth() ) );
            println("y Coords: " + DECIMAL_FORMAT.format(bottomPoint) + " to " + DECIMAL_FORMAT.format(topPoint));
            println("  Center: " + DECIMAL_FORMAT.format( center.getY() ) + "; Height: " + DECIMAL_FORMAT.format( getHeight() ) );
            println("z Coords: " + DECIMAL_FORMAT.format(nearPoint)   + " to " + DECIMAL_FORMAT.format(farPoint));
            println("  Center: " + DECIMAL_FORMAT.format( center.getZ() ) + "; Depth: "  + DECIMAL_FORMAT.format( getDepth() ) );
        }
    }


}