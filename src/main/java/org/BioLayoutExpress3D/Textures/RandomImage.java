package org.BioLayoutExpress3D.Textures;

import java.awt.image.*;
import java.io.*;
import com.jogamp.opengl.util.texture.*;
import org.BioLayoutExpress3D.StaticLibraries.*;


/**
*
*  This class provides support for creation of a random image to be used in multiple formats through static methods.
*
* @author Thanos Theo, 2008-2009
* @version 3.0.0.0
*
*/

final class RandomImage // package access
{

    /**
    *  Creates a random ARGB image.
    */
    public static BufferedImage createRandomImage(int width, int height)
    {
        BufferedImage randomImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int[] randomImageBuffer = ( (DataBufferInt)randomImage.getRaster().getDataBuffer() ).getData();  // connect it to the returning image buffer

        int r = 0, g = 0, b = 0, a = 0;
        int i = randomImageBuffer.length;
        while (--i >= 0)
        {
            r = Random.getRandomRange(0, 255);
            g = Random.getRandomRange(0, 255);
            b = Random.getRandomRange(0, 255);
            a = Random.getRandomRange(0, 255);
            randomImageBuffer[i] = a << 24 | r << 16 | g << 8 | b;
        }

        return randomImage;
    }

    /**
    *  Creates a random ARGB image and saves it to a PNG file.
    */
    public static void createRandomImageAndSaveToPngFile(int width, int height, String fileNamePrefix)
    {
        org.BioLayoutExpress3D.StaticLibraries.ImageProducer.writeBufferedImageToFile( createRandomImage(width, height), "png", new File(fileNamePrefix + ".png") );
    }

    /**
    *  Creates a random ARGB image and saves it to an OpenGL RGBA texture.
    */
    public static Texture createRandomTexture(int width, int height)
    {
        return TextureProducer.createTextureFromBufferedImageAndDeleteOrigContext( createRandomImage(width, height) );
    }


}