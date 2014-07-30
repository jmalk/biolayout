package org.BioLayoutExpress3D.Textures;

import java.awt.*;
import java.awt.image.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import static java.lang.Math.*;
import static org.BioLayoutExpress3D.Textures.ImageSFXsCollateStates.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
*  Pixel effects (bitmap manipulation effects by altering pixels):
*    * produces a plasma effect by changing RGB values through time: plasmaEffect()
*    * produces a water distortion effect by using an image as a texture: waterEffect()
*    * produces a bump mapping effect by using an image as a texture: bumpEffect()
*    * produces a radial blur effect by using an image as a texture: radialBlurEffect()
*    * produces a spot circle effect with optional noise inside it: spotCircleEffect()
*
* @see org.BioLayoutExpress3D.Graph.GraphRenderer2D
* @author Thanos Theo, Michael Kargas, 2008-2009
* @version 3.0.0.0
*
*/

public class ImageSFXs
{
   /**
    *  Variable used for the plasma effect.
    */
    private BufferedImage plasmaBitmapImage = null;

    /**
    *  Variable used as the buffer for the plasma effect.
    */
    private int[] plasmaBuffer = null;

    /**
    *  Variable used for the plasma effect.
    */
    private byte[] fSin1 = null;

    /**
    *  Variable used for the plasma effect.
    */
    private byte[] fSin2 = null;

    /**
    *  Variable used for the plasma effect.
    */
    private byte[] fSin3 = null;

    /**
    *  Variable used for the plasma effect.
    */
    private int inc1 = 0;

    /**
    *  Variable used for the plasma effect.
    */
    private int inc2 = 0;

    /**
    *  Variable used for the plasma effect.
    */
    private int inc3 = 0;

    /**
    *  Variable used for the plasma effect.
    */
    private int plasmaWidth = 0;

    /**
    *  Variable used for the plasma effect.
    */
    private int plasmaHeight = 0;

    /**
    *  BufferedImage variable used to store the water effect source bitmap image.
    */
    private BufferedImage waterBitmapImage = null;

    /**
    *  Variable used as the buffer for the water effect.
    */
    private int[] waterBuffer = null;

    /**
    *  Variable used as the buffer for the water effect bitmap image.
    */
    private int[] waterBitmapBuffer = null;

    /**
    *  Variable used for the water effect.
    */
    private int[] waterPreCalcRandomNumbers = null;

    /**
    *  Variable used for the water effect.
    */
    private short[] waterBuffer1 = null;

    /**
    *  Variable used for the water effect.
    */
    private short[] waterBuffer2 = null;

    /**
    *  Variable used for the water effect.
    */
    private static final int waterPreCalcMAXRandomNumbers = 65536;

    /**
    *  Variable used for the water effect.
    */
    private int waterPreCalcRandomNumbersK = 0;

    /**
    *  Variable used for the water effect.
    */
    private int waterWidth = 0;

    /**
    *  Variable used for the water effect.
    */
    private int waterHeight = 0;

    /**
    *  Variable used for the water effect.
    */
    private int waterBufferSize = 0;

    /**
    *  Variable used for the water effect.
    */
    private int waterK = 0;

    /**
    *  BufferedImage variable used to store the bump effect source bitmap image.
    */
    private BufferedImage bumpBitmapImage = null, bumpBitmapImageToReturn = null;

    /**
    *  Variable used as a buffer for the bump effect.
    */
    private int[] bumpBuffer = null;

    /**
    *  Variable used as a buffer for the bump effect.
    */
    private int[] bumpBitmapBuffer = null;

    /**
    *  Variable used for the bump effect.
    */
    private int[] A_bumpBitmapBuffer = null;

    /**
    *  Variable used for the bump effect.
    */
    private int[] R_bumpBitmapBuffer = null;

    /**
    *  Variable used for the bump effect.
    */
    private int[] G_bumpBitmapBuffer = null;

    /**
    *  Variable used for the bump effect.
    */
    private int[] B_bumpBitmapBuffer = null;

    /**
    *  Variable used for the bump effect.
    */
    private int[] bumpLightmapBuffer = null;

    /**
    *  Variable used for the bump effect.
    */
    private int[] divPrecalc1 = null;

    /**
    *  Variable used for the bump effect.
    */
    private int[] divPrecalc2 = null;

    /**
    *  Variable used for the bump effect.
    */
    private static final int BUMP_PRECALC_VALUES = 256;

    /**
    *  Variable used for the bump effect.
    */
    private int bumpWidth = 0;

    /**
    *  Variable used for the bump effect.
    */
    private int bumpHeight = 0;

    /**
    *  Variable used for the bump effect.
    */
    private int bumpBufferSize = 0;

    /**
    *  Variable used for the bump effect.
    */
    private int bumpLightWidth = 0;

    /**
    *  Variable used for the bump effect.
    */
    private int bumpLightHeight = 0;

    /**
    *  Variable used for the bump effect.
    */
    private long bumpFrames = 0;

    /**
    *  Variable used for the bump effect.
    */
    private int darkValue = 0;

    /**
    *  BufferedImage variable used to store the radial blur effect source bitmap image.
    */
    private BufferedImage radialBlurBitmapImage = null, radialBlurBitmapImageToReturn = null;

    /**
    *  Variable used as a buffer for the radial blur effect.
    */
    private int[] radialBlurBuffer = null;

    /**
    *  Variable used as a buffer for the radial blur effect.
    */
    private int[] radialBlurBitmapBuffer = null;

    /**
    *  Variable used for the radial blur effect.
    */
    private int radialBlurWidth = 0;

    /**
    *  Variable used for the radial blur effect.
    */
    private int radialBlurHeight = 0;

    /**
    *  Variable used for the radial blur effect.
    */
    private int radialBlurBufferWidth = 0;

    /**
    *  Variable used for the radial blur effect.
    */
    private int radialBlurBufferHeight = 0;

    /**
    *  Variable used for the radial blur effect.
    */
    private int radialBlurBsize = 0;

    /**
    *  Variable used for the radial blur effect.
    */
    private int radialBlurRsize = 0;

    /**
    *  Variable used for the radial effect.
    */
    private long radialBlurFrames = 0;

    /**
    *  Variable used for the radial blur effect.
    */
    private short[] A_radialBlurBitmapBuffer = null;

    /**
    *  Variable used for the radial blur effect.
    */
    private short[] R_radialBlurBitmapBuffer = null;

    /**
    *  Variable used for the radial blur effect.
    */
    private short[] G_radialBlurBitmapBuffer = null;

    /**
    *  Variable used for the radial blur effect.
    */
    private short[] B_radialBlurBitmapBuffer = null;

    /**
    *  Variable used for the radial blur effect.
    */
    private short[] A_radialBlurBuffer = null;

    /**
    *  Variable used for the radial blur effect.
    */
    private short[] R_radialBlurBuffer = null;

    /**
    *  Variable used for the radial blur effect.
    */
    private short[] G_radialBlurBuffer = null;

    /**
    *  Variable used for the radial blur effect.
    */
    private short[] B_radialBlurBuffer = null;

    /**
    *  Variable used for the radial blur effect.
    */
    private short[] radialBlurArray0 = null;

    /**
    *  Variable used for the radial blur effect.
    */
    private short[] radialBlurArray1 = null;

    /**
    *  Variable used for the radial blur effect.
    */
    private short[] radialBlurArray2 = null;

    /**
    *  Variable used for the radial blur effect.
    */
    private short[] radialBlurArray3 = null;

    /**
    *  Variable used for the radial blur effect.
    */
    private short[] radialBlurArray4 = null;

    /**
    *  Variable used for the radial blur effect.
    */
    private short[] radialDivN = null;

    /**
    *  Variable used for the spot circle effect.
    */
    private BufferedImage spotCircleImage = null;

    /**
    *  Variable used as the buffer for the spot circle effect.
    */
    private int[] spotCircleBuffer = null;

    /**
    *  Variable used for the spot circle effect.
    */
    private int[] spotCirclePreCalcDist = null;

    /**
    *  Variable used for the spot circle effect.
    */
    private int[] spotCirclePreCalcRandomNumbers = null;

    /**
    *  Variable used for the spot circle effect.
    */
    private int spotCirclePreCalcMAXRandomNumbers = 0;

    /**
    *  Variable used for the spot circle effect.
    */
    private int[] spotCirclePreCalcAlphaValues = null;

    /**
    *  Variable used for the spot circle effect.
    */
    private int arrayIndexPreCalcAlphaValues = 0;

    /**
    *  Variable used for the spot circle effect.
    */
    private int distStep = 0;

    /**
    *  Variable used for the spot circle effect.
    */
    private boolean withNoiseEffect = false;

    /**
    *  Variable used for the spot circle effect.
    */
    private int distMAX = 0;

    /**
    *  Variable used for the spot circle effect.
    */
    private int distMAXRatio = 0;

    /**
    *  Variable used for the spot circle effect.
    */
    private int radius = 0;

    /**
    *  Variable used for the spot circle effect.
    */
    private boolean spotCircleEffectFinished = false;

    /**
    *  Collates two images with a given orientation defined by the ImageSFXsCollateStates enumeration class (drawImage() based effect.
    */
    public static BufferedImage createCollatedImage(BufferedImage image1, BufferedImage image2, ImageSFXsCollateStates collateState, boolean qualityRendering)
    {
        return createCollatedImage(image1, image2, collateState, 1.0f, qualityRendering);
    }

    /**
    *  Collates two images with a given orientation defined by the ImageSFXsCollateStates enumeration class (drawImage() based effect.
    *  Same method with above, overloaded with a resize value.
    */
    public static BufferedImage createCollatedImage(BufferedImage image1, BufferedImage image2, ImageSFXsCollateStates collateState, float resizeRatio, boolean qualityRendering)
    {
        if (image1 == null || image2 == null)
        {
            if (DEBUG_BUILD) println("Null image supplied");
            return org.BioLayoutExpress3D.StaticLibraries.ImageProducer.createNullPointerBufferedImage();
        }

        image1 = org.BioLayoutExpress3D.StaticLibraries.ImageProducer.resizeImageByGivenRatio(image1, resizeRatio, qualityRendering);
        image2 = org.BioLayoutExpress3D.StaticLibraries.ImageProducer.resizeImageByGivenRatio(image2, resizeRatio, qualityRendering);

        int image1Width = image1.getWidth();
        int image1Height = image1.getHeight();
        int image2Width = image2.getWidth();
        int image2Height = image2.getHeight();

        int imageMaxWidth = org.BioLayoutExpress3D.StaticLibraries.Math.findMinMaxFromNumbers(image1Width, image2Width, false);
        int imageMaxHeight = org.BioLayoutExpress3D.StaticLibraries.Math.findMinMaxFromNumbers(image1Height, image2Height, false);

        int newImageWidth = 0;
        int newImageHeight = 0;

        BufferedImage tempImage = null;
        Graphics2D tempImageGC = null;
        int transparency = image1.getColorModel().getTransparency();

        if ( collateState.equals(COLLATE_NORTH) )
        {
           newImageWidth = imageMaxWidth;
           newImageHeight = image1Height + image2Height;

           tempImage = new BufferedImage(newImageWidth, newImageHeight, transparency);
           tempImageGC = tempImage.createGraphics();
           org.BioLayoutExpress3D.StaticLibraries.ImageProducer.useQuality(tempImageGC, qualityRendering);

           if (image1Width > image2Width)
           {
               tempImageGC.drawImage(image1, 0, image2Height, null);
               tempImageGC.drawImage(image2, (newImageWidth - image2Width) / 2, 0, null);
           }
           else
           {
               tempImageGC.drawImage(image1, (newImageWidth - image1Width) / 2, image2Height, null);
               tempImageGC.drawImage(image2, 0, 0, null);
           }
        }
        else if ( collateState.equals(COLLATE_EAST) )
        {
           newImageWidth = image1Width + image2Width;
           newImageHeight = imageMaxHeight;

           tempImage = new BufferedImage(newImageWidth, newImageHeight, transparency);
           tempImageGC = tempImage.createGraphics();
           org.BioLayoutExpress3D.StaticLibraries.ImageProducer.useQuality(tempImageGC, qualityRendering);

           if (image1Height > image2Height)
           {
               tempImageGC.drawImage(image1, 0, 0, null);
               tempImageGC.drawImage(image2, image1Width, (newImageHeight - image2Height) / 2, null);
           }
           else
           {
               tempImageGC.drawImage(image1, 0, (newImageHeight - image1Height) / 2, null);
               tempImageGC.drawImage(image2, image1Width, 0, null);
           }
        }
        else if ( collateState.equals(COLLATE_SOUTH) )
        {
           newImageWidth = imageMaxWidth;
           newImageHeight = image1Height + image2Height;

           tempImage = new BufferedImage(newImageWidth, newImageHeight, transparency);
           tempImageGC = tempImage.createGraphics();
           org.BioLayoutExpress3D.StaticLibraries.ImageProducer.useQuality(tempImageGC, qualityRendering);

           if (image1Width > image2Width)
           {
               tempImageGC.drawImage(image1, 0, 0, null);
               tempImageGC.drawImage(image2, (newImageWidth - image2Width) / 2, image1Height, null);
           }
           else
           {
               tempImageGC.drawImage(image1, (newImageWidth - image1Width) / 2, 0, null);
               tempImageGC.drawImage(image2, 0, image1Height, null);
           }
        }
        else if ( collateState.equals(COLLATE_WEST) )
        {
           newImageWidth = image1Width + image2Width;
           newImageHeight = imageMaxHeight;

           tempImage = new BufferedImage(newImageWidth, newImageHeight, transparency);
           tempImageGC = tempImage.createGraphics();
           org.BioLayoutExpress3D.StaticLibraries.ImageProducer.useQuality(tempImageGC, qualityRendering);

           if (image1Height > image2Height)
           {
               tempImageGC.drawImage(image1, image2Width, 0, null);
               tempImageGC.drawImage(image2, 0, (newImageHeight - image2Height) / 2, null);
           }
           else
           {
               tempImageGC.drawImage(image1, image2Width, (newImageHeight - image1Height) / 2, null);
               tempImageGC.drawImage(image2, 0, 0, null);
           }
        }

        tempImageGC.dispose();

        return tempImage;
    }

    // ------------------------- Per-pixel effects bit manipulation explanation -------------------------
    // Extract colour components from the integer pixel value using bit manipulation from a BufferedImage:
    // int pixelBuffer[] = ( (DataBufferInt)image.getRaster().getDataBuffer() ).getData();
    // int alphaValue = (pixelBuffer[i] >> 24) & 255; or another equivalent method: (pixelBuffer[i] & 0xFF000000) >> 24;
    // int redValue   = (pixelBuffer[i] >> 16) & 255; or another equivalent method: (pixelBuffer[i] & 0x00FF0000) >> 16;
    // int greenValue = (pixelBuffer[i] >>  8) & 255; or another equivalent method: (pixelBuffer[i] & 0x0000FF00) >> 8;
    // int blueValue  =     pixelBuffer[i]     & 255; or another equivalent method:  pixelBuffer[i] & 0x000000FF;

    // Similar bit manipulation in reverse
    // int pixelBuffer[i] = (alphaValue << 24) | (redValue << 16) | (greenValue << 8) | blueValue;
    // (No need to set it, when using DataBufferInt)
    // A texture creation method is provided in case a null image is inserted to the real-time effects.

    /**
    *  Creates an algorithmic based texture for the real time per pixel effects in case no source image is provided.
    *  This method produces two types of textures, a XOR texture & a RGB texture.
    */
    public static int[] textureGenerate(int type, int genTextureWidth, int genTextureHeight)
    {
        int[] generatedTexture = new int[genTextureWidth * genTextureHeight];

        int generatedTextureC = genTextureWidth * genTextureHeight;
        int generatedTextureI = genTextureWidth * genTextureHeight;

        switch (type)
        {
            case 1:
            {
                // for (int genTextureX = 0; genTextureX < genTextureWidth; genTextureX++)
                    // for (int genTextureY = 0; genTextureY < genTextureHeight; genTextureY++)
                // for (int genTextureX = genTextureWidth - 1; genTextureX >= 0 ; genTextureX--)
                    // for (int genTextureY = genTextureHeight - 1; genTextureY >= 0 ; genTextureY--)
                int genTextureX = genTextureWidth;
                while (--genTextureX >= 0)
                {
                    int genTextureY = genTextureHeight;
                    while (--genTextureY >= 0)
                    {
                        generatedTextureC = ( (genTextureX * 256) / genTextureWidth) ^ ((genTextureY * 256) / genTextureHeight );
                        generatedTexture[--generatedTextureI] = (255 << 24) | (generatedTextureC << 16) | (generatedTextureC << 8) | generatedTextureC;
                    }
                }
            }
            break;
            case 2:
            {
                // for (int genTextureX = 0; genTextureX < genTextureWidth; genTextureX++)
                    // for (int genTextureY = 0; genTextureY < genTextureHeight; genTextureY++)
                // for (int genTextureX = genTextureWidth - 1; genTextureX >= 0 ; genTextureX--)
                    // for (int genTextureY = genTextureHeight - 1; genTextureY >= 0 ; genTextureY--)
                int genTextureX = genTextureWidth;
                while (--genTextureX >= 0)
                {
                    int genTextureY = genTextureHeight;
                    while (--genTextureY >= 0)
                    {
                      generatedTexture[--generatedTextureI] = (255 << 24) | ((int)(sin((double)genTextureX / 35.0) * 127 + 128) << 16) |
                                    ((int)(sin((double)genTextureY / 45.0) * 127 + 128) << 8)  |
                                     (int)(sin((double)(genTextureX + genTextureY) / 75.0) * 127 + 128);
                    }
                }
            }
            break;
            default:
                // type 2 texture generation as default
                generatedTexture = textureGenerate(2, genTextureWidth, genTextureHeight);
            break;
        }

        return generatedTexture;
    }

    /**
    *  Initializes the plasma effect.
    */
    public void plasmaEffectInit(int plasmaEffectWidth, int plasmaEffectHeight)
    {
        plasmaWidth = plasmaEffectWidth;
        plasmaHeight = plasmaEffectHeight;

        // only one initialization for the plasma precalculations, next method call will skip this code part, as it's C equivalent function
        if (fSin1 == null && fSin2 == null && fSin3 == null)
        {
            final int arraysLength = 2048;
            fSin1 = new byte[arraysLength];
            fSin2 = new byte[arraysLength];
            fSin3 = new byte[arraysLength];

            int i = arraysLength;
            while (--i >= 0)
            {
                  fSin1[i] = (byte)(sin((double)i / 15.0) * 63 + 63);
                  fSin2[i] = (byte)(sin((double)i / 25.0) * 31 + 31);
                  fSin3[i] = (byte)(sin((double)i / 35.0) * 48 + 48);
            }
        }

        plasmaBitmapImage = new BufferedImage(plasmaEffectWidth, plasmaEffectHeight, BufferedImage.TYPE_INT_ARGB);
        plasmaBitmapImage = org.BioLayoutExpress3D.StaticLibraries.ImageProducer.cloneAndBufferImage(plasmaBitmapImage); // somehow, the Java API does a faster acceleration after using this method (using a predefined transparency and copying it)
        plasmaBuffer = ( (DataBufferInt)plasmaBitmapImage.getRaster().getDataBuffer() ).getData();  // connect it to the returning image buffer
    }

    /**
    *  Processes the plasma effect.
    */
    public void plasmaEffect()
    {
        int i = 0, r = 0, g = 0, b = 0;

        int y = plasmaHeight;
        while (--y >= 0)
        {
            int x = plasmaWidth;
            while (--x >= 0)
            {
                r = fSin1[x + inc1 + fSin2[y + fSin2[x + inc2]]] + fSin2[x + 3 * inc2 + fSin3[y + 3 * inc3]];
                g = fSin2[2 * inc2 + x + fSin3[x + y]] + fSin3[inc3 + y + fSin1[y + fSin2[x + y]]];
                b = fSin2[x + fSin1[y + inc1]] + fSin2[x + fSin1[y + 2 * inc1]];
                plasmaBuffer[i++] = (255 << 24) | r << 16 | g << 8 | b;
            }
        }

        if (inc1++ == 94) inc1 = 0;
        if (inc2++ == 157) inc2 = 0;
        if (inc3++ == 220) inc3 = 0;
    }

    /**
    *  Returns the plasma effect image.
    */
    public BufferedImage plasmaEffectImage()
    {
        return plasmaBitmapImage;
    }

    /**
    *  Sets the water effect image.
    */
    public void setWaterEffectImage(BufferedImage waterBitmapImage)
    {
        if (waterBitmapImage == null)
        {
            if (DEBUG_BUILD) println("Null waterBitmapImage supplied, now creating a randomly textured image");
            this.waterWidth = 64;
            this.waterHeight = 64;
            waterEffectInit(null);
        }
        else
        {
            this.waterWidth = waterBitmapImage.getWidth();
            this.waterHeight = waterBitmapImage.getHeight();
            waterEffectInit(waterBitmapImage);
        }
    }

    /**
    *  Initializes the water effect image (wrapper method for selecting between native and Java versions of the effect).
    */
    private void waterEffectInit(BufferedImage waterEffectBitmapImage)
    {
        if (waterEffectBitmapImage == null)
        {
            waterBitmapBuffer = textureGenerate(Random.getRandomRange(1, 2), 64, 64); // generate the random texture
            waterBitmapImage = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
            waterBitmapImage = org.BioLayoutExpress3D.StaticLibraries.ImageProducer.cloneImage(waterBitmapImage);
            waterBitmapImage.setRGB(0, 0, 64, 64, waterBitmapBuffer, 0, 64);
        }
        else
        {
            waterBitmapBuffer = new int[waterWidth * waterHeight];
            waterBitmapImage = org.BioLayoutExpress3D.StaticLibraries.ImageProducer.cloneImage(waterEffectBitmapImage);
            waterBitmapImage.getRGB(0, 0, waterWidth, waterHeight, waterBitmapBuffer, 0, waterWidth); // just get the initial bitmap buffer
        }

        waterBufferSize = waterWidth * waterHeight;

        waterPreCalcRandomNumbers = new int[2 * waterPreCalcMAXRandomNumbers];
        int i = waterPreCalcMAXRandomNumbers, j = 2 * waterPreCalcMAXRandomNumbers;
        while (--i >= 0)
        {
            j -= 2;
            waterPreCalcRandomNumbers[j] = (int)((random() * waterWidth) * 0.8f + 0.1f);
            waterPreCalcRandomNumbers[j + 1] = (int)((random() * waterHeight) * 0.8f + 0.1f);
        }

        waterBuffer1 = new short[waterBufferSize];
        waterBuffer2 = new short[waterBufferSize];

        waterBuffer = ( (DataBufferInt)waterBitmapImage.getRaster().getDataBuffer() ).getData(); // connect it to the returning image buffer
    }

    /**
    *  Writes a water drop for the water effect.
    */
    private void writeDrop(int xc, int yc, int radius)
    {
        int fxHeight = 256;
        int xp = 0, yp = 0, c = 0;

        yp = 2 * radius;
        while (--yp > 0)
        // for (yp = -radius; yp < radius; yp++)
        {
            xp = 4 * radius;
            while (--xp > 0)
            // for (xp = -radius; xp < radius; xp++)
            if ( (xc + xp) >= 0 && (xc + xp) < waterWidth && (yc + yp) >= 0 && (yc + yp) < waterHeight )
            {
                if (xp != 0 && yp != 0)
                    c = (int)(fxHeight / sqrt(xp * xp + yp * yp));
                else
                    c = fxHeight;

                waterBuffer1[xc + xp + (yc + yp) * waterWidth] = (short)c;
                // waterBuffer2[xc + xp + (yc + yp) * waterWidth] = (short)c;
            }
        }
    }

    /**
    *  Processes the water effect image.
    */
    public void waterEffect()
    {
        int xp = 0, yp = 0;
        int tx = 0, ty = 0;
        int dispX = 0, dispY = 0;

        int waterDropRadius = 2;
        waterK += 2;

        if (waterK % 16 == 0)
        {
            tx = waterPreCalcRandomNumbers[waterPreCalcRandomNumbersK];
            ty = waterPreCalcRandomNumbers[waterPreCalcRandomNumbersK + 1];

            waterPreCalcRandomNumbersK += 2;
            waterPreCalcRandomNumbersK = (waterPreCalcRandomNumbersK % (waterPreCalcMAXRandomNumbers << 1));

            writeDrop(tx, ty, waterDropRadius);
        }

        tx = (int)(waterWidth / 2 + sin((double)waterK / 96.0) * (waterWidth / 3 - 1));
        ty = (int)(waterHeight / 2 + sin((double)waterK / 64.0) * (waterHeight / 3 - 1));

        waterBuffer1[tx + ty * waterWidth] = 128;
        waterBuffer1[tx + ty * waterWidth - 1] = 64;
        waterBuffer1[tx + ty * waterWidth + 1] = 64;
        waterBuffer1[tx + (ty - 1) * waterWidth] = 64;
        waterBuffer1[tx + (ty + 1) * waterWidth] = 64;

        // swapArrays effect, in C side emulated with the double pointer swapArrays() function
        short[] temp = waterBuffer1;
        waterBuffer1 = waterBuffer2;
        waterBuffer2 = temp;

        int waterWidth2 = waterWidth << 1;
        int waterI = waterWidth2 + 2;
        int waterC = 0;

        // !!! Needed to reduce flashing points !!!
        // dispBounds controls this.
        // If too small, less flashing points but less far displacement.
        // something between 4-8 is fine. 2 or less will make the effect not so visible.
        int dispBounds = 8;

        for (int waterY = 2; waterY < waterHeight - 2; waterY++)
        {
            for (int waterX = 2; waterX < waterWidth - 2; waterX++)
            {
                waterC = ((waterBuffer1[waterI - 1] + waterBuffer1[waterI + 1] + waterBuffer1[waterI - waterWidth] + waterBuffer1[waterI + waterWidth]) >> 1) - waterBuffer2[waterI];

                waterBuffer2[waterI] = (short)(waterC - (waterC >> 4));

                dispX = (waterBuffer2[waterI - 1] - waterBuffer2[waterI + 1]) >> 2;
                dispY = (waterBuffer2[waterI - waterWidth] - waterBuffer2[waterI + waterWidth]) >> 2;

                if (dispX > dispBounds) dispX = dispBounds;
                if (dispY > dispBounds) dispX = dispBounds;
                if (dispX < -dispBounds) dispX = -dispBounds;
                if (dispY < -dispBounds) dispY = -dispBounds;

                xp = ( ( (waterX + dispX) < 0 ) ? -(waterX + dispX) : (waterX + dispX) ) % waterWidth; // instead of using Math.abs()
                yp = ( ( (waterY + dispY) < 0 ) ? -(waterY + dispY) : (waterY + dispY) ) % waterHeight; // instead of using Math.abs()

                waterC = waterBitmapBuffer[xp + yp * waterWidth];

                if ( (waterY <= 3) || (waterY >= waterHeight - 4) || (waterX <= 3) || (waterX >= waterWidth - 4) )
                   waterBuffer[waterI] = waterBitmapBuffer[waterI];
                else
                   waterBuffer[waterI] = waterC;

                waterI++;
            }

            waterI += 4;
        }
    }

    /**
    *  Returns the water effect image.
    */
    public BufferedImage waterEffectImage()
    {
        return waterBitmapImage;
    }

    /**
    *  Sets the bump effect image.
    */
    public void setBumpEffectImage(int bumpLightWidth, int bumpLightHeight, int bumpLightValue, int lightValue, int darkValue, BufferedImage bumpBitmapImage)
    {
        if (bumpBitmapImage == null)
        {
          if (DEBUG_BUILD) println("Null bumpBitmapImage supplied, now creating a randomly textured image");
          this.bumpWidth = 64;
          this.bumpHeight = 64;
          bumpEffectInit(32, 32, 100, 200, 300, null);
        }
        else
        {
          this.bumpWidth = bumpBitmapImage.getWidth();
          this.bumpHeight = bumpBitmapImage.getHeight();

          if ( !(bumpLightWidth * bumpLightHeight < bumpWidth * bumpHeight) )
          {
              if ( bumpWidth > bumpHeight )
                  bumpLightWidth = bumpLightHeight = bumpHeight;
              else
                  bumpLightWidth = bumpLightHeight = bumpWidth;
          }

          bumpEffectInit(bumpLightWidth, bumpLightHeight, bumpLightValue, lightValue, darkValue, bumpBitmapImage);
        }
    }

    /**
    *  Initializes the bump effect image.
    */
    private void bumpEffectInit(int bumpLightWidth, int bumpLightHeight, int bumpLightValue, int lightValue, int darkValue, BufferedImage bumpBitmapEffectImage)
    {
        if (bumpBitmapEffectImage == null)
        {
            bumpBitmapBuffer = textureGenerate(Random.getRandomRange(1, 2), bumpWidth, bumpHeight);  // generate the random texture
            bumpBitmapImage = new BufferedImage(bumpWidth, bumpHeight, BufferedImage.TYPE_INT_ARGB);
            bumpBitmapImage = org.BioLayoutExpress3D.StaticLibraries.ImageProducer.cloneImage(bumpBitmapImage);
            bumpBitmapImage.setRGB(0, 0, bumpWidth, bumpHeight, bumpBitmapBuffer, 0, bumpWidth);
        }
        else
        {
            bumpBitmapBuffer = new int[bumpWidth * bumpHeight];
            bumpBitmapImage = org.BioLayoutExpress3D.StaticLibraries.ImageProducer.cloneImage(bumpBitmapEffectImage);
            bumpBitmapImage.getRGB(0, 0, bumpWidth, bumpHeight, bumpBitmapBuffer, 0, bumpWidth);  // just get the initial bitmap buffer
        }

        if (USE_MULTICORE_PROCESS)
            bumpBitmapImageToReturn = org.BioLayoutExpress3D.StaticLibraries.ImageProducer.cloneImage(bumpBitmapImage);

        this.bumpLightWidth = bumpLightWidth;
        this.bumpLightHeight = bumpLightHeight;
        this.darkValue = darkValue;

        bumpFrames = 0;
        bumpBufferSize = bumpWidth * bumpHeight;

        bumpLightmapBuffer = new int[bumpBufferSize];
        int[] randomBuffer = new int[bumpBufferSize];

        int i = bumpBufferSize, j = 0;
        while (--i >= 0)
            randomBuffer[i] = (int)(random() * 127);

        j = 3;
        while (--j >= 0)
          for (i = bumpWidth + 1; i < bumpBufferSize - bumpWidth - 1; i++)
              randomBuffer[i] = (randomBuffer[i - 1] + randomBuffer[i + 1] + randomBuffer[i - bumpWidth] + randomBuffer[i + bumpWidth]) >> 2;

        int r = 0, g = 0, b = 0;

        A_bumpBitmapBuffer = new int[bumpBufferSize];
        R_bumpBitmapBuffer = new int[bumpBufferSize];
        G_bumpBitmapBuffer = new int[bumpBufferSize];
        B_bumpBitmapBuffer = new int[bumpBufferSize];

        i = bumpBufferSize;
        while (--i >= 0)
        {
            A_bumpBitmapBuffer[i] = ((bumpBitmapBuffer[i] & 0xFF000000) >> 24);
            R_bumpBitmapBuffer[i] = ((bumpBitmapBuffer[i] & 0x00FF0000) >> 16);
            G_bumpBitmapBuffer[i] = ((bumpBitmapBuffer[i] & 0x0000FF00) >> 8);
            B_bumpBitmapBuffer[i] = (bumpBitmapBuffer[i] & 0x000000FF);
        }

        i = bumpWidth + 1;
        for (int y = 1; y < bumpHeight - 1; y++)
        {
            for (int x = 1; x < bumpWidth - 1; x++)
            {
                r = ( -R_bumpBitmapBuffer[i + 1] - R_bumpBitmapBuffer[i + bumpWidth] - R_bumpBitmapBuffer[i + bumpWidth + 1] + R_bumpBitmapBuffer[i - 1] + R_bumpBitmapBuffer[i - bumpWidth] + R_bumpBitmapBuffer[i - bumpWidth - 1] ) / 9 + 128;
                g = ( -G_bumpBitmapBuffer[i + 1] - G_bumpBitmapBuffer[i + bumpWidth] - G_bumpBitmapBuffer[i + bumpWidth + 1] + G_bumpBitmapBuffer[i - 1] + G_bumpBitmapBuffer[i - bumpWidth] + G_bumpBitmapBuffer[i - bumpWidth - 1] ) / 9 + 128;
                b = ( -B_bumpBitmapBuffer[i + 1] - B_bumpBitmapBuffer[i + bumpWidth] - B_bumpBitmapBuffer[i + bumpWidth + 1] + B_bumpBitmapBuffer[i - 1] + B_bumpBitmapBuffer[i - bumpWidth] + B_bumpBitmapBuffer[i - bumpWidth - 1] ) / 9 + 128;
                bumpBitmapBuffer[i] = ((r + g + b) >> 2) + randomBuffer[i];
                if (bumpBitmapBuffer[i] > 255) bumpBitmapBuffer[i] = 255;
                R_bumpBitmapBuffer[i] = ((bumpBitmapBuffer[i] * R_bumpBitmapBuffer[i]) >> 8);
                G_bumpBitmapBuffer[i] = ((bumpBitmapBuffer[i] * G_bumpBitmapBuffer[i]) >> 8);
                B_bumpBitmapBuffer[i] = ((bumpBitmapBuffer[i] * B_bumpBitmapBuffer[i]) >> 8);
                i++;
            }
        }

        i = 0;
        for (int y = -bumpLightHeight / 2; y < bumpLightHeight / 2; y++)
        {
            for (int x = -bumpLightWidth / 2; x < bumpLightWidth / 2; x++)
            {
                bumpLightmapBuffer[i] = (int)(bumpLightValue - sqrt((double)(x * x + y * y)) * ((float)bumpLightValue / ((float)bumpLightWidth / 2.5f)));
                if (bumpLightmapBuffer[i] < 0) bumpLightmapBuffer[i] = 0;
                if (bumpLightmapBuffer[i] > 255) bumpLightmapBuffer[i] = 255;
                i++;
            }
        }

        int lightRange = lightValue - darkValue;

        divPrecalc1 = new int[BUMP_PRECALC_VALUES];
        divPrecalc2 = new int[BUMP_PRECALC_VALUES];

        i = BUMP_PRECALC_VALUES;
        while (--i >= 0)
        {
            if ( (divPrecalc1[i] = abs((int)((float)i * (float)darkValue / 255.0f))) > 255 ) divPrecalc1[i] = 255;
            if ( (divPrecalc2[i] = abs((int)((float)i * (float)lightRange / 255.0f))) > 255 ) divPrecalc2[i] = 255;
        }

        bumpBuffer = ( (DataBufferInt)bumpBitmapImage.getRaster().getDataBuffer() ).getData(); // connect it to the returning image buffer
    }

    /**
    *  Processes the bump effect.
    */
    public void bumpEffect()
    {
        int c = 0, i = 0;
        int r = 0, g = 0, b = 0;
        int nx = 0, ny = 0;
        int lx = 0, ly = 0;

        bumpFrames++;
        lx = (int)(bumpWidth / 2 + sin(bumpFrames / 48.0f) * (bumpWidth / 2));
        ly = (int)(bumpHeight / 2 + sin(bumpFrames / 64.0f) * (bumpHeight / 2));
        //ly = bumpHeight / 2; for moving in x coords only

        int rx = lx - (bumpLightWidth >> 1);
        int ry = ly - (bumpLightHeight >> 1);

        int ni0 = 0, ni1 = 0, np = 0;

        int lx0 = 0, lx1 = bumpLightWidth, ly0 = 1, ly1 = bumpLightHeight - 1;
        int limx0 = 0, limx1 = bumpWidth - bumpLightWidth, limy0 = 0, limy1 = bumpHeight - bumpLightHeight;

        if (rx > limx1) lx1 -= (rx - limx1);
        if (rx < limx0) lx0 += (limx0 - rx);

        if (ry > limy1) ly1 -= (ry - limy1);
        if (ry < limy0) ly0 += (limy0 - ry);

        i = bumpBufferSize;
        while (--i >= 0)
            bumpBuffer[i] = (A_bumpBitmapBuffer[i] << 24) | (divPrecalc1[R_bumpBitmapBuffer[i]] << 16) | (divPrecalc1[G_bumpBitmapBuffer[i]] << 8) | divPrecalc1[B_bumpBitmapBuffer[i]];

        i = rx + lx0 + (ry + ly0) * bumpWidth;

        int y = 0, x = 0;
        for (y = ly0; y < ly1; y++)
        {
            for (x = lx0; x < lx1; x++)
            {
                ni0 = rx + x - 1 + (y + ry) * bumpWidth;
                ni1 = rx + x + 1 + (y + ry) * bumpWidth;
                nx = bumpBitmapBuffer[ni0] - bumpBitmapBuffer[ni1];

                ni0 = rx + x + (y - 1 + ry) * bumpWidth;
                ni1 = rx + x + (y + 1 + ry) * bumpWidth;
                ny = bumpBitmapBuffer[ni0] - bumpBitmapBuffer[ni1];

                nx = (((rx + x - (bumpLightWidth >> 1)) - lx + nx) < 0) ? -((rx + x - (bumpLightWidth >> 1)) - lx + nx) : ((rx + x - (bumpLightWidth >> 1)) - lx + nx); // instead of using Math.abs()
                ny = (((ry + y - (bumpLightHeight >> 1)) - ly + ny) < 0) ? -((ry + y - (bumpLightHeight >> 1)) - ly + ny) : ((ry + y - (bumpLightHeight >> 1)) - ly + ny); // instead of using Math.abs()

                if (nx > bumpLightWidth - 1) nx = bumpLightWidth - 1;
                if (ny > bumpLightHeight - 1) ny = bumpLightHeight - 1;

                c = darkValue + divPrecalc2[bumpLightmapBuffer[nx + ny * (bumpLightWidth - (bumpLightWidth & 1))]];

                np = (x + rx) + (y + ry) * bumpWidth;
                r = (c * R_bumpBitmapBuffer[np]) >> 8;
                g = (c * G_bumpBitmapBuffer[np]) >> 8;
                b = (c * B_bumpBitmapBuffer[np]) >> 8;

                bumpBuffer[i] = (A_bumpBitmapBuffer[i] << 24) | r << 16 | g << 8 | b;
                i++;
            }

            i += (bumpWidth + lx0 - lx1);
        }

        if (USE_MULTICORE_PROCESS)
            bumpBitmapImageToReturn.setRGB(0, 0, bumpWidth, bumpHeight, bumpBuffer, 0, bumpWidth);
    }

    /**
    *  Returns the bump effect image.
    */
    public BufferedImage bumpEffectImage()
    {
        return ( (USE_MULTICORE_PROCESS) ? bumpBitmapImageToReturn : bumpBitmapImage );
    }

    /**
    *  Sets the radial effect image.
    */
    public void setRadialBlurEffectImage(double radialBlurShortness, BufferedImage radialBlurBitmapImage)
    {
        if (radialBlurBitmapImage == null)
        {
            if (DEBUG_BUILD) println("Null radialBlurBitmapImage supplied");
            this.radialBlurBufferWidth = 64;
            this.radialBlurBufferHeight = 64;
            this.radialBlurWidth = 2 * radialBlurBufferWidth;
            this.radialBlurHeight = 2 * radialBlurBufferHeight;
            radialBlurEffectInit(4, null);
        }
        else
        {
            this.radialBlurBufferWidth = radialBlurBitmapImage.getWidth();
            this.radialBlurBufferHeight = radialBlurBitmapImage.getHeight();
            this.radialBlurWidth = 2 * radialBlurBufferWidth;
            this.radialBlurHeight = 2 * radialBlurBufferHeight;
            radialBlurEffectInit(radialBlurShortness, radialBlurBitmapImage);
        }
    }

    /**
    *  Initializes the radial blur effect.
    */
    private void radialBlurEffectInit(double radialBlurShortness, BufferedImage radialBlurEffectBitmapImage)
    {
        //Generate the texture
        if (radialBlurEffectBitmapImage == null)
        {
            radialBlurBitmapBuffer = textureGenerate(Random.getRandomRange(1, 2), 64, 64); // generate the random texture
            radialBlurBitmapImage = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
            radialBlurBitmapImage = org.BioLayoutExpress3D.StaticLibraries.ImageProducer.cloneImage(radialBlurBitmapImage);
            radialBlurBitmapImage.setRGB(0, 0, 64, 64, radialBlurBitmapBuffer, 0, 64);
        }
        else
        {
            radialBlurBitmapBuffer = new int [radialBlurBufferWidth * radialBlurBufferHeight];
            radialBlurBitmapImage = org.BioLayoutExpress3D.StaticLibraries.ImageProducer.cloneImage(radialBlurEffectBitmapImage); // somehow, the Java API does a faster acceleration after using this method (using a predefined transparency and copying it)
            radialBlurBitmapImage.getRGB(0, 0, radialBlurBufferWidth, radialBlurBufferHeight, radialBlurBitmapBuffer, 0, radialBlurBufferWidth); // just get the initial bitmap buffer
        }

        if (USE_MULTICORE_PROCESS)
            radialBlurBitmapImageToReturn = org.BioLayoutExpress3D.StaticLibraries.ImageProducer.cloneImage(radialBlurBitmapImage);

        radialBlurBsize = radialBlurBufferWidth * radialBlurBufferHeight;
        radialBlurRsize = radialBlurWidth * radialBlurHeight;
        radialBlurFrames = 0;

        A_radialBlurBitmapBuffer = new short[radialBlurBsize];
        R_radialBlurBitmapBuffer = new short[radialBlurBsize];
        G_radialBlurBitmapBuffer = new short[radialBlurBsize];
        B_radialBlurBitmapBuffer = new short[radialBlurBsize];

        A_radialBlurBuffer = new short[radialBlurBsize];
        R_radialBlurBuffer = new short[radialBlurBsize];
        G_radialBlurBuffer = new short[radialBlurBsize];
        B_radialBlurBuffer = new short[radialBlurBsize];

        radialBlurArray0 = new short[radialBlurRsize];
        radialBlurArray1 = new short[radialBlurRsize];
        radialBlurArray2 = new short[radialBlurRsize];
        radialBlurArray3 = new short[radialBlurRsize];
        radialBlurArray4 = new short[radialBlurRsize];

        final int PRE_CALC_VALUES = 6 * 256;
        radialDivN = new short[PRE_CALC_VALUES];

        int xi = 0, yi = 0;
        int xc = 0, yc = 0;
        int i = radialBlurHeight * radialBlurWidth;

        int y = radialBlurHeight;
        while (--y >= 0)
        {
              int x = radialBlurWidth;
              while (--x >= 0)
              {
                  xc = x - radialBlurWidth / 2;
                  yc = y - radialBlurHeight / 2;
                  xi = (int)(-((double)xc / radialBlurShortness));
                  yi = (int)(-((double)yc / radialBlurShortness));

                  i--;
                  radialBlurArray0[i] = (short)( xi +        yi       * radialBlurBufferWidth);
                  radialBlurArray1[i] = (short)((xi >> 1) + (yi >> 1) * radialBlurBufferWidth);
                  radialBlurArray2[i] = (short)((xi >> 1) + (yi >> 1) * radialBlurBufferWidth);
                  radialBlurArray3[i] = (short)((xi >> 2) + (yi >> 2) * radialBlurBufferWidth);
                  radialBlurArray4[i] = (short)((xi >> 3) + (yi >> 3) * radialBlurBufferWidth);
              }
        }

        i = PRE_CALC_VALUES;
        while (--i >= 0)
            radialDivN[i]= (short)(i / 6);

        i = radialBlurBsize;
        while (--i >= 0)
        {
            A_radialBlurBitmapBuffer[i] = (short)((radialBlurBitmapBuffer[i] & 0xFF000000) >>> 24);
            R_radialBlurBitmapBuffer[i] = (short)((radialBlurBitmapBuffer[i] & 0x00FF0000) >>> 16);
            G_radialBlurBitmapBuffer[i] = (short)((radialBlurBitmapBuffer[i] & 0x0000FF00) >>> 8);
            B_radialBlurBitmapBuffer[i] = (short)(radialBlurBitmapBuffer[i] & 0x000000FF);
        }

        radialBlurBuffer = ( (DataBufferInt)radialBlurBitmapImage.getRaster().getDataBuffer() ).getData(); // connect it to the returning image buffer
    }

    /**
    *  Processes the radial blur effect.
    */
    public void radialBlurEffect(int positionY, boolean borderFade, int borderFadeX, int borderFadeY)
    {
        int i = radialBlurBsize;
        while (--i >= 0)
        {
            A_radialBlurBuffer[i] = A_radialBlurBitmapBuffer[i];
            R_radialBlurBuffer[i] = R_radialBlurBitmapBuffer[i];
            G_radialBlurBuffer[i] = G_radialBlurBitmapBuffer[i];
            B_radialBlurBuffer[i] = B_radialBlurBitmapBuffer[i];
        }

        int x = 0, y = 0, ri = 0;

        radialBlurFrames++;

        // Circle movement
        //      int dcx = (int)(sin((double)radialBlurFrames / 23.0) * (radialBlurBufferWidth/2 - 1));
        //      int dcy = (int)(sin((double)radialBlurFrames / 32.0) * (radialBlurBufferHeight/2 - 1));

        // Circle movement, closer to the center
        //      int dcx = (int)(sin((double)radialBlurFrames / 13.0) * (radialBlurBufferWidth/4 - 1));
        //      int dcy = (int)(sin((double)radialBlurFrames / 17.0) * (radialBlurBufferHeight/4 - 1));

        // X axis movement
        final double movementSpeed = 40.0;
        int dcx = (int)(sin((double)radialBlurFrames / movementSpeed) * (radialBlurBufferWidth/2 - 1));
        int dcy = positionY; // 0 to center the Y axis

        int dci = dcx + dcy * radialBlurBufferWidth;

        // 1st circle quadrant
        i = (radialBlurBufferWidth/2 - 1) + (radialBlurBufferHeight/2 - 1) * radialBlurBufferWidth + dci;
        ri = (radialBlurWidth/2 - 1) + (radialBlurHeight/2 - 1) * radialBlurWidth;

        for (y = radialBlurBufferHeight/2 - 1 + dcy; y >= 0; y--)
        {
            for (x = radialBlurBufferWidth/2 - 1 + dcx; x >= 0; x--)
            {
                A_radialBlurBuffer[i] = radialDivN[A_radialBlurBuffer[i] + A_radialBlurBuffer[i + radialBlurArray0[ri]] + A_radialBlurBuffer[i + radialBlurArray1[ri]] + A_radialBlurBuffer[i + radialBlurArray2[ri]] + A_radialBlurBuffer[i + radialBlurArray3[ri]] + A_radialBlurBuffer[i + radialBlurArray4[ri]]];
                R_radialBlurBuffer[i] = radialDivN[R_radialBlurBuffer[i] + R_radialBlurBuffer[i + radialBlurArray0[ri]] + R_radialBlurBuffer[i + radialBlurArray1[ri]] + R_radialBlurBuffer[i + radialBlurArray2[ri]] + R_radialBlurBuffer[i + radialBlurArray3[ri]] + R_radialBlurBuffer[i + radialBlurArray4[ri]]];
                G_radialBlurBuffer[i] = radialDivN[G_radialBlurBuffer[i] + G_radialBlurBuffer[i + radialBlurArray0[ri]] + G_radialBlurBuffer[i + radialBlurArray1[ri]] + G_radialBlurBuffer[i + radialBlurArray2[ri]] + G_radialBlurBuffer[i + radialBlurArray3[ri]] + G_radialBlurBuffer[i + radialBlurArray4[ri]]];
                B_radialBlurBuffer[i] = radialDivN[B_radialBlurBuffer[i] + B_radialBlurBuffer[i + radialBlurArray0[ri]] + B_radialBlurBuffer[i + radialBlurArray1[ri]] + B_radialBlurBuffer[i + radialBlurArray2[ri]] + B_radialBlurBuffer[i + radialBlurArray3[ri]] + B_radialBlurBuffer[i + radialBlurArray4[ri]]];

                radialBlurBuffer[i] = (A_radialBlurBuffer[i] << 24) | (R_radialBlurBuffer[i] << 16) | (G_radialBlurBuffer[i] << 8) | B_radialBlurBuffer[i];
                i--;
                ri--;
            }

            i = i - radialBlurBufferWidth/2 + dcx;
            ri = ri - radialBlurWidth + radialBlurBufferWidth/2 + dcx;
        }

        // 2nd circle quadrant
        i = radialBlurBufferWidth/2 + (radialBlurBufferHeight/2 - 1) * radialBlurBufferWidth + dci;
        ri = radialBlurWidth/2 + (radialBlurHeight/2 - 1) * radialBlurWidth;

        for (y = radialBlurBufferHeight/2 - 1 + dcy; y >= 0; y--)
        {
            for (x = radialBlurBufferWidth/2 + dcx; x < radialBlurBufferWidth; x++)
            {
                A_radialBlurBuffer[i] = radialDivN[A_radialBlurBuffer[i] + A_radialBlurBuffer[i + radialBlurArray0[ri]] + A_radialBlurBuffer[i + radialBlurArray1[ri]] + A_radialBlurBuffer[i + radialBlurArray2[ri]] + A_radialBlurBuffer[i + radialBlurArray3[ri]] + A_radialBlurBuffer[i + radialBlurArray4[ri]]];
                R_radialBlurBuffer[i] = radialDivN[R_radialBlurBuffer[i] + R_radialBlurBuffer[i + radialBlurArray0[ri]] + R_radialBlurBuffer[i + radialBlurArray1[ri]] + R_radialBlurBuffer[i + radialBlurArray2[ri]] + R_radialBlurBuffer[i + radialBlurArray3[ri]] + R_radialBlurBuffer[i + radialBlurArray4[ri]]];
                G_radialBlurBuffer[i] = radialDivN[G_radialBlurBuffer[i] + G_radialBlurBuffer[i + radialBlurArray0[ri]] + G_radialBlurBuffer[i + radialBlurArray1[ri]] + G_radialBlurBuffer[i + radialBlurArray2[ri]] + G_radialBlurBuffer[i + radialBlurArray3[ri]] + G_radialBlurBuffer[i + radialBlurArray4[ri]]];
                B_radialBlurBuffer[i] = radialDivN[B_radialBlurBuffer[i] + B_radialBlurBuffer[i + radialBlurArray0[ri]] + B_radialBlurBuffer[i + radialBlurArray1[ri]] + B_radialBlurBuffer[i + radialBlurArray2[ri]] + B_radialBlurBuffer[i + radialBlurArray3[ri]] + B_radialBlurBuffer[i + radialBlurArray4[ri]]];
                radialBlurBuffer[i] = (A_radialBlurBuffer[i] << 24) | (R_radialBlurBuffer[i] << 16) | (G_radialBlurBuffer[i] << 8) | B_radialBlurBuffer[i];
                i++;
                ri++;
            }

            i = i - 3 * (radialBlurBufferWidth/2) + dcx;
            ri = ri - radialBlurWidth - (radialBlurBufferWidth/2 - dcx);
        }

        // 3rd circle quadrant
        i = radialBlurBufferWidth/2 + (radialBlurBufferHeight/2)*radialBlurBufferWidth + dci;
        ri = radialBlurWidth/2 + (radialBlurHeight/2) * radialBlurWidth;

        for (y = radialBlurBufferHeight/2 + dcy; y < radialBlurBufferHeight; y++)
        {
            for (x = radialBlurBufferWidth/2 + dcx; x < radialBlurBufferWidth; x++)
            {
                A_radialBlurBuffer[i] = radialDivN[A_radialBlurBuffer[i] + A_radialBlurBuffer[i + radialBlurArray0[ri]] + A_radialBlurBuffer[i + radialBlurArray1[ri]] + A_radialBlurBuffer[i + radialBlurArray2[ri]] + A_radialBlurBuffer[i + radialBlurArray3[ri]] + A_radialBlurBuffer[i + radialBlurArray4[ri]]];
                R_radialBlurBuffer[i] = radialDivN[R_radialBlurBuffer[i] + R_radialBlurBuffer[i + radialBlurArray0[ri]] + R_radialBlurBuffer[i + radialBlurArray1[ri]] + R_radialBlurBuffer[i + radialBlurArray2[ri]] + R_radialBlurBuffer[i + radialBlurArray3[ri]] + R_radialBlurBuffer[i + radialBlurArray4[ri]]];
                G_radialBlurBuffer[i] = radialDivN[G_radialBlurBuffer[i] + G_radialBlurBuffer[i + radialBlurArray0[ri]] + G_radialBlurBuffer[i + radialBlurArray1[ri]] + G_radialBlurBuffer[i + radialBlurArray2[ri]] + G_radialBlurBuffer[i + radialBlurArray3[ri]] + G_radialBlurBuffer[i + radialBlurArray4[ri]]];
                B_radialBlurBuffer[i] = radialDivN[B_radialBlurBuffer[i] + B_radialBlurBuffer[i + radialBlurArray0[ri]] + B_radialBlurBuffer[i + radialBlurArray1[ri]] + B_radialBlurBuffer[i + radialBlurArray2[ri]] + B_radialBlurBuffer[i + radialBlurArray3[ri]] + B_radialBlurBuffer[i + radialBlurArray4[ri]]];
                radialBlurBuffer[i] = (A_radialBlurBuffer[i] << 24) | (R_radialBlurBuffer[i] << 16) | (G_radialBlurBuffer[i] << 8) | B_radialBlurBuffer[i];
                i++;
                ri++;
            }

            i = i + radialBlurBufferWidth/2 + dcx;
            ri = ri + radialBlurWidth - (radialBlurBufferWidth/2 - dcx);
        }

        // 4th circle quadrant
        i = (radialBlurBufferWidth/2 - 1) + (radialBlurBufferHeight/2)*radialBlurBufferWidth + dci;
        ri = (radialBlurWidth/2 - 1) + (radialBlurHeight/2) * radialBlurWidth;

        for (y = radialBlurBufferHeight/2 + dcy; y < radialBlurBufferHeight; y++)
        {
            for (x = radialBlurBufferWidth/2 - 1 + dcx; x >= 0; x--)
            {
                A_radialBlurBuffer[i] = radialDivN[A_radialBlurBuffer[i] + A_radialBlurBuffer[i + radialBlurArray0[ri]] + A_radialBlurBuffer[i + radialBlurArray1[ri]] + A_radialBlurBuffer[i + radialBlurArray2[ri]] + A_radialBlurBuffer[i + radialBlurArray3[ri]] + A_radialBlurBuffer[i + radialBlurArray4[ri]]];
                R_radialBlurBuffer[i] = radialDivN[R_radialBlurBuffer[i] + R_radialBlurBuffer[i + radialBlurArray0[ri]] + R_radialBlurBuffer[i + radialBlurArray1[ri]] + R_radialBlurBuffer[i + radialBlurArray2[ri]] + R_radialBlurBuffer[i + radialBlurArray3[ri]] + R_radialBlurBuffer[i + radialBlurArray4[ri]]];
                G_radialBlurBuffer[i] = radialDivN[G_radialBlurBuffer[i] + G_radialBlurBuffer[i + radialBlurArray0[ri]] + G_radialBlurBuffer[i + radialBlurArray1[ri]] + G_radialBlurBuffer[i + radialBlurArray2[ri]] + G_radialBlurBuffer[i + radialBlurArray3[ri]] + G_radialBlurBuffer[i + radialBlurArray4[ri]]];
                B_radialBlurBuffer[i] = radialDivN[B_radialBlurBuffer[i] + B_radialBlurBuffer[i + radialBlurArray0[ri]] + B_radialBlurBuffer[i + radialBlurArray1[ri]] + B_radialBlurBuffer[i + radialBlurArray2[ri]] + B_radialBlurBuffer[i + radialBlurArray3[ri]] + B_radialBlurBuffer[i + radialBlurArray4[ri]]];
                radialBlurBuffer[i] = (A_radialBlurBuffer[i] << 24) | (R_radialBlurBuffer[i] << 16) | (G_radialBlurBuffer[i] << 8) | B_radialBlurBuffer[i];
                i--;
                ri--;
            }

            i = i + 3 * (radialBlurBufferWidth/2) + dcx;
            ri = ri + radialBlurWidth + (radialBlurBufferWidth/2 + dcx);
        }

        // This part does a linear interpolation with the edges of the radial blurred image to produce a
        // border fade effect. Used for centered radial blurred images, not needed for screen size images
        if (borderFade)
        {
            float c0 = 0.0f;
            float c1 = 0.0f;
            float dcFadeX = 0.0f;
            float dcFadeY = 0.0f;
            float ac = 0.0f;

            for (y = 0; y < radialBlurBufferHeight; y++)
            {
                i = y * radialBlurBufferWidth;
                c0 = 0;
                c1 = 255;
                dcFadeX = (c1 - c0) / borderFadeX;
                ac = c0;

                for (x = 0; x < borderFadeX ; x++)
                {
                    A_radialBlurBuffer[i] = (short)((A_radialBlurBuffer[i] * (int)ac) >> 8);
                    radialBlurBuffer[i] = (A_radialBlurBuffer[i] << 24) | (R_radialBlurBuffer[i] << 16) | (G_radialBlurBuffer[i] << 8) | B_radialBlurBuffer[i];
                    i++;
                    ac += dcFadeX;
                }
            }

            for (y = 0; y < radialBlurBufferHeight; y++)
            {
                i = (y + 1) * radialBlurBufferWidth - borderFadeX;
                c0 = 0;
                c1 = 255;
                dcFadeX = (c1 - c0) / borderFadeX;
                ac = c1;

                for (x = radialBlurBufferWidth - borderFadeX; x < radialBlurBufferWidth ; x++)
                {
                    A_radialBlurBuffer[i] = (short)((A_radialBlurBuffer[i] * (int)ac) >> 8);
                    radialBlurBuffer[i] = (A_radialBlurBuffer[i] << 24) | (R_radialBlurBuffer[i] << 16) | (G_radialBlurBuffer[i] << 8) | B_radialBlurBuffer[i];
                    i++;
                    ac -= dcFadeX;
                }
            }

            for (x = 0; x < radialBlurBufferWidth; x++)
            {
                i = x;
                c0 = 0;
                c1 = 255;
                dcFadeY = (c1 - c0) / borderFadeY;
                ac = c0;

                for (y = 0; y < borderFadeY; y++)
                {
                    A_radialBlurBuffer[i] = (short)((A_radialBlurBuffer[i] * (int)ac) >> 8);
                    radialBlurBuffer[i] = (A_radialBlurBuffer[i] << 24) | (R_radialBlurBuffer[i] << 16) | (G_radialBlurBuffer[i] << 8) | B_radialBlurBuffer[i];
                    i += radialBlurBufferWidth;
                    ac += dcFadeY;
                }
            }

            for (x = 0; x < radialBlurBufferWidth; x++)
            {
                i = (radialBlurBufferHeight - borderFadeY) * radialBlurBufferWidth + x;
                c0 = 0;
                c1 = 255;
                dcFadeY = (c1 - c0) / borderFadeY;
                ac = c1;

                for (y = radialBlurBufferHeight - borderFadeY; y < radialBlurBufferHeight; y++)
                {
                    A_radialBlurBuffer[i] = (short)((A_radialBlurBuffer[i] * (int)ac) >> 8);
                    radialBlurBuffer[i] = (A_radialBlurBuffer[i] << 24) | (R_radialBlurBuffer[i] << 16) | (G_radialBlurBuffer[i] << 8) | B_radialBlurBuffer[i];
                    i += radialBlurBufferWidth;
                    ac -= dcFadeY;
                }
            }
        }

        if (USE_MULTICORE_PROCESS)
            radialBlurBitmapImageToReturn.setRGB(0, 0, radialBlurBufferWidth, radialBlurBufferHeight, radialBlurBuffer, 0, radialBlurBufferWidth);
    }

    /**
    *  Returns the radialBlur effect image.
    */
    public BufferedImage radialBlurEffectImage()
    {
        return ( (USE_MULTICORE_PROCESS) ? radialBlurBitmapImageToReturn : radialBlurBitmapImage );
    }

    /**
    *  Initializes the spot circle effect (wrapper method for selecting between native and Java versions of the effect).
    */
    public void spotCircleEffectInit(int spotCircleWidth, int spotCircleHeight, int spotCircleCenterX, int spotCircleCenterY, int distStep, boolean withNoiseEffect, int distRatio)
    {
        this.distStep = distStep;
        this.withNoiseEffect = withNoiseEffect;

        arrayIndexPreCalcAlphaValues = 0;

        int distMaxCornerTopLeft = (int)sqrt( pow( (double)(spotCircleCenterX - 0), 2) + pow( (double)(spotCircleCenterY - 0), 2) );
        int distMaxCornerTopRight = (int)sqrt( pow( (double)(spotCircleCenterX - spotCircleWidth), 2) + pow( (double)(spotCircleCenterY - 0), 2) );
        int distMaxCornerBottomLeft = (int)sqrt( pow( (double)(spotCircleCenterX - 0), 2) + pow( (double)(spotCircleCenterY - spotCircleHeight), 2) );
        int distMaxCornerBottomRight = (int)sqrt( pow( (double)(spotCircleCenterX - spotCircleWidth), 2) + pow( (double)(spotCircleCenterY - spotCircleHeight), 2) );

        distMAX = org.BioLayoutExpress3D.StaticLibraries.Math.findMinMaxFromNumbers(distMaxCornerTopLeft, distMaxCornerTopRight, distMaxCornerBottomLeft, distMaxCornerBottomRight, false); // false for MAX distance!

        if (distStep > 0)
            radius = distMAX;
        else
            radius = 0;

        if (distRatio < 0)
        {
            if (DEBUG_BUILD) println("distRatio must be a positive number, now converting it to positive");
            distRatio = abs(distRatio);
        }
        else if (distRatio == 0)
        {
            if (DEBUG_BUILD) println("distRatio cannot be 0, now changing it to 1");
            distRatio = 1;
        }

        spotCirclePreCalcDist = new int[2 * (spotCircleWidth * spotCircleHeight)];

        int i = 0;
        for (int y = 0; y < spotCircleHeight; y++) // in this way, to scan the matrix in a horizontal way (compatible with Java's way)
        {
            for (int x = 0; x < spotCircleWidth; x++)
            {
                double distance = sqrt( pow( (double)(spotCircleCenterX - x), 2) + pow( (double)(spotCircleCenterY - y), 2) );
                int decimal = (int)(((distance - (int)distance)) * 255); // keeps only the decimal * 255 (if 187.75, (int)(0.75 * 255))
                spotCirclePreCalcDist[i++] = (int)distance;
                spotCirclePreCalcDist[i++] = decimal;
            }
        }

        // initializing the precalculated random numbers
        spotCirclePreCalcMAXRandomNumbers = 65536;
        spotCirclePreCalcRandomNumbers = new int[spotCirclePreCalcMAXRandomNumbers];
        i = spotCirclePreCalcMAXRandomNumbers;
        while (--i >= 0)
          spotCirclePreCalcRandomNumbers[i] = Random.getRandomRange(0, 255);
        spotCirclePreCalcMAXRandomNumbers -= 1;

        if (withNoiseEffect)
        {
            distMAXRatio = distMAX / distRatio;
            spotCirclePreCalcAlphaValues = new int[ distMAX / ( distRatio * abs(distStep) ) ];
            i = spotCirclePreCalcAlphaValues.length;
            while (--i >= 0)
                spotCirclePreCalcAlphaValues[i] = (int)( 255 * (double)i / (double)( (distMAX / (distRatio * distStep) - 1) ) );
        }

        spotCircleEffectFinished = false;

        spotCircleImage = new BufferedImage(spotCircleWidth, spotCircleHeight, BufferedImage.TYPE_INT_ARGB);
        // the below Java API trick somehow screws up the effect bigtime when random noise is involved!
        // spotCircleImage = GameImageProducer.cloneAndBufferImage(spotCircleImage); // somehow, the Java API does a faster acceleration after using this method (using a predefined transparency and copying it)
        spotCircleBuffer = ( (DataBufferInt) spotCircleImage.getRaster().getDataBuffer() ).getData();
    }

    /**
    *  Processes the spot circle effect (wrapper method for selecting between native and Java versions of the effect).
    */
    public void spotCircleEffect()
    {
        if ( (radius > (-2 * distStep) && distStep > 0) || (radius < (distMAX - distStep) && distStep < 0) )
        {
            int randomIndex = (withNoiseEffect) ? Random.getRandomRange(0, spotCirclePreCalcMAXRandomNumbers) : 0;
            int arrayRandomNumber = 0;

            int iPreCalcDist = 0;
            int i = spotCircleBuffer.length;
            while (--i >= 0)
            {
              iPreCalcDist = i << 1;
              if (spotCirclePreCalcDist[iPreCalcDist] < radius)
              {
                  if (withNoiseEffect)
                  {
                      if (radius < distMAXRatio)
                      {
                          arrayRandomNumber = spotCirclePreCalcRandomNumbers[randomIndex++ & spotCirclePreCalcMAXRandomNumbers]; // & used here for modulo effect
                          spotCircleBuffer[i] = (spotCirclePreCalcAlphaValues[arrayIndexPreCalcAlphaValues] << 24) | (arrayRandomNumber << 16) | (arrayRandomNumber << 8) | arrayRandomNumber;
                      }
                      else
                          spotCircleBuffer[i] = (0 << 24);
                  }
                  else
                      spotCircleBuffer[i] = (0 << 24);
              }
              else if (spotCirclePreCalcDist[iPreCalcDist] > radius)
                  spotCircleBuffer[i] = (255 << 24);
              else // if (spotCirclePreCalcDist[iPreCalcDist] == radius) // for the anti-alias effect!
                  spotCircleBuffer[i] = (spotCirclePreCalcDist[iPreCalcDist + 1] << 24);
            }

            radius -= distStep;

            if (withNoiseEffect)
            {
                if (radius < distMAXRatio)
                {
                  if (arrayIndexPreCalcAlphaValues < spotCirclePreCalcAlphaValues.length - 1)
                    arrayIndexPreCalcAlphaValues++;
                }
            }
        }
        else
            spotCircleEffectFinished = true;
    }

    /**
    *  Returns the spot circle effect image.
    */
    public BufferedImage spotCircleEffectImage()
    {
        return spotCircleImage;
    }

    /**
    *  Returns the spot circle effect status.
    */
    public boolean hasSpotCircleEffectFinished()
    {
        return spotCircleEffectFinished;
    }

    /**
    *  Returns if an image has an alpha channel (support method).
    */
    public static boolean hasAlpha(BufferedImage image)
    {
        if (image == null)
        {
            return false;
        }
        else
        {
            int transparency = image.getColorModel().getTransparency();

            return ( (transparency == Transparency.BITMASK) || (transparency == Transparency.TRANSLUCENT) );
        }
    }

    /**
    *  Destroys (de-initializes) the effects.
    */
    public void destructor()
    {
        if (plasmaBitmapImage != null) plasmaBitmapImage.flush();
        plasmaBitmapImage = null;
        plasmaBuffer = null;
        fSin1 = null;
        fSin2 = null;
        fSin3 = null;
        if (waterBitmapImage != null) waterBitmapImage.flush();
        waterBitmapImage = null;
        waterBuffer = null;
        waterBitmapBuffer = null;
        waterBuffer1 = null;
        waterBuffer2 = null;
        if (bumpBitmapImage != null) bumpBitmapImage.flush();
        bumpBitmapImage = null;
        bumpBuffer = null;
        bumpBitmapBuffer = null;
        A_bumpBitmapBuffer = null;
        R_bumpBitmapBuffer = null;
        G_bumpBitmapBuffer = null;
        B_bumpBitmapBuffer = null;
        bumpLightmapBuffer = null;
        if (spotCircleImage != null) spotCircleImage.flush();
        spotCircleImage = null;
        spotCircleBuffer = null;
        spotCirclePreCalcDist = null;
        spotCirclePreCalcRandomNumbers = null;
    }

}