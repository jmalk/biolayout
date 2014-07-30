package org.BioLayoutExpress3D.GPUComputing.GLSL;

import java.awt.*;
import static java.lang.Math.*;

/*
*
* CPUEmulatedGLSLFunctions is a final class containing only static based methods, used for emulating various GLSL functions through the CPU in the case of lack of shader support.
*
* @author Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*
*/

public final class CPUEmulatedGLSLFunctions
{

    /**
    *  Mix CPU emulated GLSL function that provides linear interpolation support.
    */
    public static float mix(float x, float y, float a)
    {
        return (1.0f - a) * x + a * y;
    }

    /**
    *  Mix CPU emulated GLSL function that provides linear interpolation support.
    *  Overloaded version for direct linear color interpolation.
    */
    public static Color mix(Color colorX, Color colorY, float a)
    {
        float[] colorArrayX = new float[4];
        float[] colorArrayY = new float[4];

        colorX.getRGBComponents(colorArrayX);
        colorY.getRGBComponents(colorArrayY);

        return new Color(mix(colorArrayX[0], colorArrayY[0], a), mix(colorArrayX[1], colorArrayY[1], a),
                         mix(colorArrayX[2], colorArrayY[2], a), mix(colorArrayX[3], colorArrayY[3], a) );
    }

    /**
    *  Fract CPU emulated GLSL function that provides fract support. Also available as modf() in C/C++.
    */
    public static float fract(float x)
    {
        return (float)( x - floor(x) );
    }

    /**
    *  Clamp CPU emulated GLSL function that provides clamp support.
    */
    public static float clamp(float x, float minVal, float maxVal)
    {
        return min(max(x, minVal), maxVal);
    }

    /**
    *  Smoothstep CPU emulated GLSL function that provides Hermite cubic polynomial interpolation support.
    */
    public static float smoothstep(float edge0, float edge1, float x)
    {
        x = clamp( (x - edge0) / (edge1 - edge0), 0.0f, 1.0f );
        return x * x * (3.0f - 2.0f * x);
    }

    /**
    *  Smootherstep CPU emulated GLSL function that provides 5th order polynomial interpolation support.
    *  Prof. Ken Perlin suggests an improved version of the smoothstep function which has zero 1st and 2nd order derivatives at t=0 and t=1.
    */
    public static float smootherstep(float edge0, float edge1, float x)
    {
        x = clamp( (x - edge0) / (edge1 - edge0), 0.0f, 1.0f );
        return x * x * x * ( x * (x * 6.0f - 15.0f) + 10.0f );
    }


}