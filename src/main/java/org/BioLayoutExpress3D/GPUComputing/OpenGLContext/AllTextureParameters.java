package org.BioLayoutExpress3D.GPUComputing.OpenGLContext;

import static javax.media.opengl.GL2.*;

/*
*
* AllTextureParameters is a final class containing texture parameters to be used for GPU Computing through OpenGL & GLSL.
*
* @author Dominik Göddeke, Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*
*/

public final class AllTextureParameters
{

    // Texture Rectangle parameters from here on

    /**
    *  Texture rectangle, texture_float_ARB, RGBA, 32 bit.
    */
    public static final TextureParameters TEXTURE_RECTANGLE_ARB_RGBA_32 = new TextureParameters("TEXTURE_RECTANGLE - float_ARB - RGBA - 32", GL_TEXTURE_RECTANGLE_ARB, GL_RGBA32F, GL_RGBA);

    /**
    *  Texture rectangle, texture_float_ARB, RGBA, 16 bit.
    */
    public static final TextureParameters TEXTURE_RECTANGLE_ARB_RGBA_16 = new TextureParameters("TEXTURE_RECTANGLE - float_ARB - RGBA - 16", GL_TEXTURE_RECTANGLE_ARB, GL_RGBA16F, GL_RGBA);

    /**
    *  Texture rectangle, texture_float_ARB, R, 32 bit.
    */
    public static final TextureParameters TEXTURE_RECTANGLE_ARB_R_32 = new TextureParameters("TEXTURE_RECTANGLE - float_ARB - R - 32", GL_TEXTURE_RECTANGLE_ARB, GL_LUMINANCE32F, GL_LUMINANCE);



    /**
    *  Texture rectangle, ATI_texture_float, RGBA, 32 bit.
    */
    public static final TextureParameters TEXTURE_RECTANGLE_ATI_RGBA_32 = new TextureParameters("TEXTURE_RECTANGLE - float_ATI - RGBA - 32", GL_TEXTURE_RECTANGLE_ARB, GL_RGBA_FLOAT32_ATI, GL_RGBA);

    /**
    *  Texture rectangle, ATI_texture_float, RGBA, 16 bit.
    */
    public static final TextureParameters TEXTURE_RECTANGLE_ATI_RGBA_16 = new TextureParameters("TEXTURE_RECTANGLE - float_ATI - RGBA - 16", GL_TEXTURE_RECTANGLE_ARB, GL_RGBA_FLOAT16_ATI, GL_RGBA);

    /**
    *  Texture rectangle, ATI_texture_float, R, 32 bit.
    */
    public static final TextureParameters TEXTURE_RECTANGLE_ATI_R_32 = new TextureParameters("TEXTURE_RECTANGLE - float_ATI - R - 32", GL_TEXTURE_RECTANGLE_ARB, GL_LUMINANCE_FLOAT32_ATI, GL_LUMINANCE);



    /**
    *  Texture rectangle, NV_float_buffer, RGBA, 32 bit.
    */
    public static final TextureParameters TEXTURE_RECTANGLE_NV_RGBA_32 = new TextureParameters("TEXTURE_RECTANGLE - float_NV - RGBA - 32", GL_TEXTURE_RECTANGLE_ARB, GL_FLOAT_RGBA32_NV, GL_RGBA);

    /**
    *  Texture rectangle, NV_float_buffer, RGBA, 16 bit.
    */
    public static final TextureParameters TEXTURE_RECTANGLE_NV_RGBA_16 = new TextureParameters("TEXTURE_RECTANGLE - float_NV - RGBA - 16", GL_TEXTURE_RECTANGLE_ARB, GL_FLOAT_RGBA16_NV, GL_RGBA);

    /**
    *  Texture rectangle, NV_float_buffer, R, 32 bit.
    */
    public static final TextureParameters TEXTURE_RECTANGLE_NV_R_32 = new TextureParameters("TEXTURE_RECTANGLE - float_NV - R - 32", GL_TEXTURE_RECTANGLE_ARB, GL_FLOAT_R32_NV, GL_LUMINANCE);



    // Texture 2D parameters from here on

    /**
    *  Texture 2D, texture_float_ARB, RGBA, 32 bit.
    */
    public static final TextureParameters TEXTURE_2D_ARB_RGBA_32 = new TextureParameters("TEXTURE_2D - float_ARB - RGBA - 32", GL_TEXTURE_2D, GL_RGBA32F, GL_RGBA);

    /**
    *  Texture 2D, texture_float_ARB, RGBA, 16 bit.
    */
    public static final TextureParameters TEXTURE_2D_ARB_RGBA_16 = new TextureParameters("TEXTURE_2D - float_ARB - RGBA - 16", GL_TEXTURE_2D, GL_RGBA16F, GL_RGBA);

    /**
    *  Texture 2D, texture_float_ARB, R, 32 bit.
    */
    public static final TextureParameters TEXTURE_2D_ARB_R_32 = new TextureParameters("TEXTURE_2D - float_ARB - R - 32", GL_TEXTURE_2D, GL_LUMINANCE32F, GL_LUMINANCE);



    /**
    *  Texture 2D, ATI_texture_float, RGBA, 32 bit.
    */
    public static final TextureParameters TEXTURE_2D_ATI_RGBA_32 = new TextureParameters("TEXTURE_2D - float_ATI - RGBA - 32", GL_TEXTURE_2D, GL_RGBA_FLOAT32_ATI, GL_RGBA);

    /**
    *  Texture 2D, ATI_texture_float, RGBA, 16 bit.
    */
    public static final TextureParameters TEXTURE_2D_ATI_RGBA_16 = new TextureParameters("TEXTURE_2D - float_ATI - RGBA - 16", GL_TEXTURE_2D, GL_RGBA_FLOAT16_ATI, GL_RGBA);

    /**
    *  Texture 2D, ATI_texture_float, R, 32 bit.
    */
    public static final TextureParameters TEXTURE_2D_ATI_R_32 = new TextureParameters("TEXTURE_2D - float_ATI - R - 32", GL_TEXTURE_2D, GL_LUMINANCE_FLOAT32_ATI, GL_LUMINANCE);



    /**
    *  Texture 2D, NV_float_buffer, RGBA, 32 bit.
    */
    public static final TextureParameters TEXTURE_2D_NV_RGBA_32 = new TextureParameters("TEXTURE_2D - float_NV - RGBA - 32", GL_TEXTURE_2D, GL_FLOAT_RGBA32_NV, GL_RGBA);

    /**
    *  Texture 2D, NV_float_buffer, RGBA, 16 bit.
    */
    public static final TextureParameters TEXTURE_2D_NV_RGBA_16 = new TextureParameters("TEXTURE_2D - float_NV - RGBA - 16", GL_TEXTURE_2D, GL_FLOAT_RGBA16_NV, GL_RGBA);

    /**
    *  Texture 2D, NV_float_buffer, R, 32 bit.
    */
    public static final TextureParameters TEXTURE_2D_NV_R_32 = new TextureParameters("TEXTURE_2D - float_NV - R - 32", GL_TEXTURE_2D, GL_FLOAT_R32_NV, GL_LUMINANCE);



    /**
    *  Static inner class that holds all texture parameter information.
    */
    public static class TextureParameters
    {

        /**
        *  Stores the texture name.
        */
        public String textureName = "";

        /**
        *  Stores the texture target.
        */
        public int textureTarget = 0;

        /**
        *  Stores the texture internal format.
        */
        public int textureInternalFormat = 0;

        /**
        *  Stores the texture format.
        */
        public int textureFormat = 0;

        /**
        *  The constructor of the TextureParameters inner class.
        */
        private TextureParameters(String textureName, int textureTarget, int textureInternalFormat, int textureFormat)
        {
            this.textureName = textureName;
            this.textureTarget = textureTarget;
            this.textureInternalFormat = textureInternalFormat;
            this.textureFormat = textureFormat;
        }

        /**
        *  Overrides the toString() method for the TextureParameters inner class.
        */
        @Override
        public String toString()
        {
            return textureName;
        }


    }


}
