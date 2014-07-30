package org.BioLayoutExpress3D.StaticLibraries;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.net.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* Static class initializing & handling all type of images.
*
* @author Thanos Theo, 2008-2009
* @version 3.0.0.0
*/

public final class ImageProducer
{

    /**
    *  Clones an image.
    */
    public static BufferedImage cloneImage(BufferedImage image)
    {
        if (image == null)
        {
            if (DEBUG_BUILD) println("Null BufferedImage supplied to ImageProducer in method cloneImage()!");

            return createNullPointerBufferedImage();
        }

        BufferedImage tempImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D tempImageGC = tempImage.createGraphics();
        tempImageGC.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
        tempImageGC.dispose();

        return tempImage;
    }

    /**
    *  Clones and buffers an image to VRAM.
    */
    public static BufferedImage cloneAndBufferImage(BufferedImage image)
    {
       if (image == null)
       {
            if (DEBUG_BUILD) println("Null image supplied in ImageProducer!");
            image = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
            Graphics2D tempImageGC = image.createGraphics();
            tempImageGC.setColor(Color.CYAN);
            tempImageGC.fillRect(0, 0, 40, 40);
            tempImageGC.setColor(Color.BLACK);
            tempImageGC.drawString("?!?", 12, 25);
            tempImageGC.dispose();

            return image;
        }

        int transparency = image.getColorModel().getTransparency();
        BufferedImage tempImage = new BufferedImage(image.getWidth(), image.getHeight(), transparency);
        Graphics2D tempImageGC = tempImage.createGraphics();
        tempImageGC.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
        tempImageGC.dispose();

        return tempImage;
    }

    /**
    *  Resizes an image to a given ratio.
    */
    public static Image resizeImageByGivenRatio(Image image, float ratio, boolean qualityRendering)
    {
        if (ratio == 1.0f)
            return image;

        if (image == null)
        {
            if (DEBUG_BUILD) println("Null Image supplied to ImageProducer in method resizeImageByGivenRatio()!");

            return (Image)createNullPointerBufferedImage();
        }

        int width = (int)((float)image.getWidth(null) * ratio);
        int height = (int)((float)image.getHeight(null) * ratio);
        if (width <= 0) width = 1;   // the image must be at least of a width of 1
        if (height <= 0) height = 1; // the image must be at least of a height of 1

        BufferedImage tempImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D tempImageGC = tempImage.createGraphics();
        useQuality(tempImageGC, qualityRendering);
        tempImageGC.drawImage(image, 0, 0, width, height, null);
        tempImageGC.dispose();

        return (Image)tempImage;
    }

    /**
    *  Resizes an image to a given ratio.
    *  Same method with above, overloaded with BufferedImage instead of Image.
    */
    public static BufferedImage resizeImageByGivenRatio(BufferedImage image, float ratio, boolean qualityRendering)
    {
        if (ratio == 1.0f)
            return image;

        if (image == null)
        {
            if (DEBUG_BUILD) println("Null BufferedImage supplied to ImageProducer in method resizeImageByGivenRatio()!");

            return createNullPointerBufferedImage();
        }

        int width = (int)((float)image.getWidth(null) * ratio);
        int height = (int)((float)image.getHeight(null) * ratio);
        if (width <= 0) width = 1;   // the image must be at least of a width of 1
        if (height <= 0) height = 1; // the image must be at least of a height of 1

        int transparency = image.getColorModel().getTransparency();
        BufferedImage tempImage = new BufferedImage(width, height, transparency);
        Graphics2D tempImageGC = tempImage.createGraphics();
        useQuality(tempImageGC, qualityRendering);
        tempImageGC.drawImage(image, 0, 0, width, height, null);
        tempImageGC.dispose();

        return tempImage;
    }

    /**
    *  Uses or not quality settings for rendering.
    */
    public static void useQuality(Graphics2D g2d, boolean qualityRendering)
    {
        if ( qualityRendering )
            qualityRendering(g2d);
        else
            speedRendering(g2d);
    }

    /**
    *  Settings for quality rendering.
    */
    public static void qualityRendering(Graphics2D g2d)
    {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    /**
    *  Settings for speed rendering.
    */
    public static void speedRendering(Graphics2D g2d)
    {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
    }

    /**
    *  Loads the image from a file.
    */
    public static BufferedImage loadImageFromFile(File input)
    {
        try
        {
            return ImageIO.read(input);
        }
        catch (IOException e)
        {
            if (DEBUG_BUILD) println("loadImageFromFile() IOException error when loading image " + input + ":\n" + e.getMessage());

            return createNullPointerBufferedImage();
        }
    }

    /**
    *  Loads the image from an InputStream.
    */
    public static BufferedImage loadImageFromInputStream(InputStream input)
    {
        try
        {
            return ImageIO.read(input);
        }
        catch (IOException e)
        {
            if (DEBUG_BUILD) println("loadImageFromInputStream() IOException error when loading image " + input + ":\n" + e.getMessage());

            return createNullPointerBufferedImage();
        }
    }

    /**
    *  Loads the image from a URL.
    */
    public static BufferedImage loadImageFromURL(URL input)
    {
        try
        {
            return ImageIO.read(input);
        }
        catch (IOException e)
        {
            if (DEBUG_BUILD) println("loadImageFromURL() IOException error when loading image " + input + ":\n" + e.getMessage());

            return createNullPointerBufferedImage();
        }
    }

    /**
    *  Creates an image from a given byte buffer.
    */
    public static BufferedImage createImageFromBuffer(byte[] imageBuffer, int width, int height, int bytesPerPixel, boolean clampWithByteMaxValue)
    {
        int imageType = (bytesPerPixel == 4) ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
        boolean hasTransparency = (bytesPerPixel == 4);
        int[] pixelBuffer = new int[imageBuffer.length / bytesPerPixel];

        int r = 0, g = 0, b = 0, a = 0;
        int index = 0, clampValue = clampWithByteMaxValue ? Byte.MAX_VALUE : 0;
        for (int i = 0; i < imageBuffer.length; i += bytesPerPixel)
        {
            r = imageBuffer[i    ] + clampValue;
            g = imageBuffer[i + 1] + clampValue;
            b = imageBuffer[i + 2] + clampValue;
            if (hasTransparency)
                a = imageBuffer[i + 3] + clampValue;

            pixelBuffer[index++] = a << 24 | r << 16 | g << 8 | b;
        }

        BufferedImage image = new BufferedImage(width, height, imageType);
        image.setRGB(0, 0, width, height, pixelBuffer, 0, width);

        return image;
    }

    /**
    *  Writes the image to disk as a file.
    *  Same method with above, overloaded with BufferedImage instead of Image.
    */
    public static boolean writeBufferedImageToFile(BufferedImage image, String imageFormat, File imageFile)
    {
        return writeBufferedImageToFile(image, imageFormat, imageFile, 1.0f, true);
    }

    /**
    *  Writes the image to disk as a file with a given ratio.
    *  Same method with above, overloaded with BufferedImage instead of Image.
    */
    public static boolean writeBufferedImageToFile(BufferedImage image, String imageFormat, File imageFile, float ratio, boolean qualityRendering)
    {
        if (image == null)
        {
            if (DEBUG_BUILD) println("Null BufferedImage supplied to ImageProducer in method writeBufferedImageToDisk()!");

            return false;
        }

        image = resizeImageByGivenRatio(image, ratio, qualityRendering);

        try
        {
            ImageIO.write(image, imageFormat, imageFile);

            return true;
        }
        catch (IOException e)
        {
            if (DEBUG_BUILD) println("Could not write the image file:\n" + e.getMessage());

            return false;
        }
    }

    /**
    *  Creates a transparent image with a given width & height.
    */
    public static BufferedImage createTransparentBufferedImage(int width, int height)
    {
        return createVariableTransparentBufferedImage(width, height, 0);
    }

    /**
    *  Creates a semi transparent image with a given width & height.
    */
    public static BufferedImage createSemiTransparentBufferedImage(int width, int height)
    {
        return createVariableTransparentBufferedImage(width, height, 127);
    }

    /**
    *  Creates an opaque image with a given width & height.
    */
    public static BufferedImage createOpaqueBufferedImage(int width, int height)
    {
        return createVariableTransparentBufferedImage(width, height, 255);
    }

    /**
    *  Creates a variable transparent image with a given width, height & alpha value.
    */
    public static BufferedImage createVariableTransparentBufferedImage(int width, int height, int alphaValue)
    {
        if (width <= 0) width = 1;   // the image must be at least of a width of 1
        if (height <= 0) height = 1; // the image must be at least of a height of 1

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        image = cloneAndBufferImage(image);
        int[] pixels =  ( (DataBufferInt)image.getRaster().getDataBuffer() ).getData();  // connect it to the returning image buffer

        if ( (alphaValue < 0) || (alphaValue > 255) )
        {
            if (DEBUG_BUILD) println("The alpha value must be in the range of 0-255 to ImageProducer  in method createVariableTransparentBufferedImage()!");
            alphaValue = 0;
        }

        if (alphaValue > 0)
        {
            int i = pixels.length;
            while (--i >= 0)
                pixels[i] = alphaValue << 24;
        }

        return image;
    }

    /**
    *  Creates an image with transparent borders with a given ratio.
    */
    public static BufferedImage createBufferedImageWithTransparentBorders(BufferedImage image, float ratio)
    {
        if (ratio == 1.0f)
        {
            if (DEBUG_BUILD) println("Ratio <= 1.0 supplied to ImageProducer in method createBufferedImageWithTransparentBorders()!");

            return image;
        }

        return createBufferedImageWithTransparentBorders(image, (int)(ratio * image.getWidth()), (int)(ratio * image.getHeight()));
    }

    /**
    *  Creates an image with transparent borders with a given width & height.
    */
    public static BufferedImage createBufferedImageWithTransparentBorders(BufferedImage image, int newWidth, int newHeight)
    {
        if (image == null)
        {
            if (DEBUG_BUILD) println("Null BufferedImage supplied to ImageProducer in method createBufferedImageWithTransparentBorders()!");

            return createNullPointerBufferedImage();
        }

        if ( (image.getWidth() >= newWidth) || (image.getHeight() >= newHeight) )
        {
            if (DEBUG_BUILD) println("BufferedImage Width/Height supplied to ImageProducer is bigger than newWidth/newHeight in method createBufferedImageWithTransparentBorders()!");

            return image;
        }

        BufferedImage transparentImage = createTransparentBufferedImage(newWidth, newHeight);
        Graphics2D tempImageGC = transparentImage.createGraphics();
        tempImageGC.drawImage(image, (newWidth - image.getWidth()) / 2, (newHeight - image.getHeight()) / 2, null);
        tempImageGC.dispose();

        return transparentImage;
    }

    /**
    *  This method creates a white box with red text (?!?) in it (used for when supplying null images).
    */
    public static BufferedImage createNullPointerBufferedImage()
    {
        BufferedImage image = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
        Graphics2D tempImageGC = image.createGraphics();
        tempImageGC.setColor(Color.WHITE);
        tempImageGC.fillRect(0, 0, 40, 40);
        tempImageGC.setColor(Color.RED);
        tempImageGC.drawString("?!?", 12, 25);
        tempImageGC.dispose();

        return image;
    }


}