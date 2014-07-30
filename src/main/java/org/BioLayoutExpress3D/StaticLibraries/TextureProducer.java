package org.BioLayoutExpress3D.StaticLibraries;

import java.awt.image.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import javax.media.opengl.*;
import org.BioLayoutExpress3D.GPUComputing.OpenGLContext.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* Static class for initializing & handling textures. The null pointer texture is using the Singleton Design Pattern.
*
* @author Thanos Theo, 2008-2009
* @version 3.0.0.0
*/

public final class TextureProducer
{

    /**
    *  Texture variable to store the null pointer texture.
    */
    private static Texture nullPointerTexture = null;

    /**
    *  Creates a texture from a given buffered image.
    */
    public static Texture createTextureFromBufferedImage(BufferedImage image)
    {
        return createTextureFromBufferedImage(image, false);
    }

    /**
    *  Creates a texture from a given buffered image.
    *  Overloaded version of the above method so as to use auto mipmap texture generation.
    */
    public static Texture createTextureFromBufferedImage(BufferedImage image, boolean useAutoMipmapGeneration)
    {
        if (image == null)
        {
            if (DEBUG_BUILD) println("Null Image supplied to TextureProducer in method createTextureFromBufferedImage()!");
            return getNullPointerTexture();
        }

        return AWTTextureIO.newTexture(GLProfile.getDefault(), image, useAutoMipmapGeneration);
    }

    /**
    *  Creates a texture from a given buffered image.
    */
    public static Texture createTextureFromBufferedImageAndDeleteOrigContext(BufferedImage image)
    {
        return createTextureFromBufferedImage(image, false);
    }

    /**
    *  Creates a texture from a given buffered image.
    *  Overloaded version of the above method so as to use auto mipmap texture generation.
    */
    public static Texture createTextureFromBufferedImageAndDeleteOrigContext(BufferedImage image, boolean useAutoMipmapGeneration)
    {
        Texture imageTexture = createTextureFromBufferedImage(image, useAutoMipmapGeneration);
        image.flush();
        image = null;

        return imageTexture;
    }

    /**
    *  This method creates a white box with red text (?!?) in it (used for when supplying null textures).
    */
    public static Texture getNullPointerTexture()
    {
        return getNullPointerTexture(false);
    }

    /**
    *  This method creates a white box with red text (?!?) in it (used for when supplying null textures).
    *  Overloaded version of the above method so as to use auto mipmap texture generation.
    */
    public static Texture getNullPointerTexture(boolean useAutoMipmapGeneration)
    {
        if (nullPointerTexture == null)
            nullPointerTexture = createTextureFromBufferedImage(org.BioLayoutExpress3D.StaticLibraries.ImageProducer.createNullPointerBufferedImage(), useAutoMipmapGeneration);

        return nullPointerTexture;
    }

    /**
    *  This method disposes the null pointer texture.
    */
    public static void disposeNullPointerTexture(GL2 gl)
    {
        nullPointerTexture = null;
    }


}
