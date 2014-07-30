package org.BioLayoutExpress3D.Textures;

import java.awt.*;
import java.awt.image.*;
import java.nio.*;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.*;

/**
*
* Texture image converter class that converts Java2D BufferedImages into a data structure that can be easily passed to OpenGL.
*
* @author Thanos Theo, 2008-2009
* @version 3.0.0.0
*
*/

public final class TextureConverter
{
    /**
    *  Stores the texture image pixel data.
    */
    private final ByteBuffer pixels;

    /**
    *  Stores the texture image width.
    */
    private final int width;

    /**
    *  Stores the texture image height.
    */
    private final int height;

    /**
    *  The constructor of the TextureConverter class.
    */
    public TextureConverter(ByteBuffer pixels, int width, int height)
    {
        this.height = height;
        this.pixels = pixels;
        this.width = width;
    }

    /**
    *  Gets the texture image height.
    */
    public int getHeight()
    {
        return height;
    }

    /**
    *  Gets the texture image width.
    */
    public int getWidth()
    {
        return width;
    }

    /**
    *  Gets the texture image pixel data.
    */
    public ByteBuffer getPixels()
    {
        return pixels;
    }

    /**
    *  Disposes the texture image pixel data.
    */
    public void dispose()
    {
        pixels.clear();
    }

    /**
    *  Creates the texture image.
    */
    public static TextureConverter createTexture(BufferedImage image)
    {
        return createTexture( image, (image.getColorModel().getTransparency() != Transparency.OPAQUE) );
    }

    /**
    *  Creates the texture image.
    *  Overloaded version that chooses if to preserve or not the transparency information.
    */
    public static TextureConverter createTexture(BufferedImage image, boolean storeAlphaChannel)
    {
        return readPixels(image, storeAlphaChannel);
    }

    /**
    *  Reads the texture image pixel data from a Java2D buffered image.
    *  Conversion of Java2D ARGB format to OpenGL RGBA format, if transparency is available.
    */
    private static TextureConverter readPixels(BufferedImage image, boolean storeAlphaChannel)
    {
        int[] packedPixels = new int[ image.getWidth() * image.getHeight() ];
        image.getRGB( 0, 0, image.getWidth(), image.getHeight(), packedPixels, 0, image.getWidth() ); // just get the initial bitmap buffer

        int bytesPerPixel = storeAlphaChannel ? 4 : 3;
        ByteBuffer unpackedPixels = Buffers.newDirectByteBuffer(packedPixels.length * bytesPerPixel);
        int packedPixel = 0;

        for (int row = image.getHeight() - 1; row >= 0; row--)
        {
            for (int col = 0; col < image.getWidth(); col++)
            {
                packedPixel = packedPixels[row * image.getWidth() + col];
                unpackedPixels.put( (byte)( (packedPixel >> 16) & 0xFF) );
                unpackedPixels.put( (byte)( (packedPixel >>  8) & 0xFF) );
                unpackedPixels.put( (byte)( (packedPixel >>  0) & 0xFF) );

                // OpenGL uses the RGBA texture format, so the alpha value goes last
                if (storeAlphaChannel)
                    unpackedPixels.put( (byte)( (packedPixel >> 24) & 0xFF) );
            }
        }

        unpackedPixels.flip();

        return new TextureConverter( unpackedPixels, image.getWidth(), image.getHeight() );
    }


}
