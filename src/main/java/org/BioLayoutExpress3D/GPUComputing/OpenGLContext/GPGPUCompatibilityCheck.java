package org.BioLayoutExpress3D.GPUComputing.OpenGLContext;

import javax.media.opengl.*;
import org.BioLayoutExpress3D.DataStructures.*;
import static javax.media.opengl.GL.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/*
*
* GPGPUCompatibilityCheck is a final class to perform a GPU compatibility check for GPGPU Computing.
*
* @author Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*
*/

public final class GPGPUCompatibilityCheck
{

    /**
    *  Stores the given texture parameters.
    */
    private AllTextureParameters.TextureParameters textureParameters = null;

    /**
    *  Stores the given max texture size.
    */
    private int textureSize = 0;

    /**
    *  Stores the given X problem domain variable.
    */
    private int problemDomainX = 0;

    /**
    *  Stores the given Y problem domain variable.
    */
    private int problemDomainY = 0;

    /**
    *  The GPGPUCompatibilityCheck constructor.
    */
    public GPGPUCompatibilityCheck(AllTextureParameters.TextureParameters textureParameters, int textureSize, int problemDomainX, int problemDomainY)
    {
        this.textureParameters = textureParameters;
        this.textureSize = textureSize;
        this.problemDomainX = problemDomainX;
        this.problemDomainY = problemDomainY;
    }

    /**
    *  Checks for problem domain size compatibility, FBO, float_texture & TextureRectangle (if used in the textureParameters) availability in that order.
    */
    public Tuple2<Boolean, String> isGPUCompatible(GL2 gl)
    {
        String message = "";
        if ( ( (long)problemDomainX * (long)problemDomainY ) > ( (textureParameters.textureFormat == GL_RGBA)
                                                               ? 4 * (textureSize * textureSize) // 4 floats per texel for RGBA mode
                                                               : (textureSize * textureSize) ) ) // 1 float  per texel for R LUMINANCE mode
        {
            message = "The GPGPU Computing problem domain is too large for a textureSize of " + textureSize + "!";
            if (DEBUG_BUILD) println(message);

            return Tuples.tuple(false, message);
        }
        else if (!USE_GL_EXT_FRAMEBUFFER_OBJECT)
        {
            message = "GL_EXT_framebuffer_object (FBO offscreen rendering) not supported!";
            if (DEBUG_BUILD) println(message);

            return Tuples.tuple(false, message);
        }
        else if (textureParameters.textureName.contains("TEXTURE_RECTANGLE") && !USE_GL_ARB_TEXTURE_RECTANGLE)
        {
            message = "GL_ARB_texture_rectangle not supported!";
            if (DEBUG_BUILD) println(message);

            return Tuples.tuple(false, message);
        }
        else
        {
            boolean isARB = textureParameters.textureName.contains("ARB");
            boolean isATI = textureParameters.textureName.contains("ATI");
            boolean isNV = textureParameters.textureName.contains("NV");

            if ( isARB && !gl.isExtensionAvailable("GL_ARB_texture_float") )
            {
                message = "GL_ARB_texture_float not supported!";
                if (DEBUG_BUILD) println(message);

                return Tuples.tuple(false, message);
            }
            else if ( isATI && !gl.isExtensionAvailable("GL_ATI_texture_float") )
            {
                message = "GL_ATI_texture_float not supported!";
                if (DEBUG_BUILD) println(message);

                return Tuples.tuple(false, message);
            }
            else if ( isNV && !gl.isExtensionAvailable("GL_NV_float_buffer") )
            {
                message = "GL_NV_float_buffer not supported!";
                if (DEBUG_BUILD) println(message);

                return Tuples.tuple(false, message);
            }
            else if (!isARB && !isATI && !isNV)
            {
                message = "Unexpected OpenGL extension error!";
                if (DEBUG_BUILD) println(message);

                return Tuples.tuple(false, message);
            }
            else
            {
                message = "The " + GL_VENDOR_STRING + " GPU is compatible for GPGPU Computing with\nthe given texture parameters: " + textureParameters.textureName;
                if (DEBUG_BUILD) println(message);

                return Tuples.tuple(true, message);
            }
        }
    }


}