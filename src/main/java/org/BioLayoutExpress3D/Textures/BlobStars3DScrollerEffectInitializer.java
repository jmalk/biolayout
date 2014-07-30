package org.BioLayoutExpress3D.Textures;

import java.awt.*;

/**
*
* A simple class used for initializing various variables for the blob stars 3D scroller effect in the TextureSFXs & ShaderTextureSFXs class.
*
* @see org.BioLayoutExpress3D.Textures.TextureSFXs
* @author Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*/

public class BlobStars3DScrollerEffectInitializer
{
    /**
    *  Constant value used for the 3D star blob motion blur size.
    */
    private static final float DEFAULT_MOTION_BLUR_SIZE = 0.85f;

    /**
    *  Auxiliary value used for the blob's width.
    */
    public int blobWidth = 0;

    /**
    *  Auxiliary value used for the blob's height.
    */
    public int blobHeight = 0;

    /**
    *  Auxiliary value used for the blob's scale size.
    */
    public int blobScaleSize = 0;

    /**
    *  Auxiliary value used for the 3D world (star field) halo exponent.
    */
    public float haloExponent = 0.0f;

    /**
    *  Auxiliary value used for the 3D world (star field) parallelogram size.
    */
    public int scSize = 0;

    /**
    *  Auxiliary value used for the 3D star maximum Z distance.
    */
    public int starDistanceZ = 0;

    /**
    *  Auxiliary value used for defining the number of 3D stars (blobs) available in the star field.
    */
    public int numberOf3DStars = 0;

    /**
    *  Auxiliary value used for the 3D star blob color.
    */
    public Color blobColor = null;

    /**
    *  Auxiliary value used for the 3D star blob motion blur.
    */
    public boolean useBlobMotionBlur = false;

    /**
    *  Auxiliary value used for the 3D star blob motion blur size.
    */
    public float blobMotionBlur = DEFAULT_MOTION_BLUR_SIZE;

    /**
    *  The constructor of the BlobStars3DScrollerEffectInitializer class. Initializes all variables to default values.
    */
    public BlobStars3DScrollerEffectInitializer()
    {
        blobWidth = 64;
        blobHeight = 64;
        blobScaleSize = 12;
        haloExponent = 1.0f;
        scSize = 4096;
        starDistanceZ = 2048;
        numberOf3DStars = 384;
        blobColor = Color.WHITE;
        useBlobMotionBlur = false;
        blobMotionBlur = 0.0f;
    }

    /**
    *  The second constructor of the BlobStars3DScrollerEffectInitializer class. Initializes some variables to user selected values.
    */
    public BlobStars3DScrollerEffectInitializer(int blobWidth, int blobHeight, int blobScaleSize, int numberOf3DStars)
    {
        this.blobWidth = blobWidth;
        this.blobHeight = blobHeight;
        this.blobScaleSize = blobScaleSize;
        haloExponent = 1.0f;
        scSize = 4096;
        starDistanceZ = 2048;
        this.numberOf3DStars = numberOf3DStars;
        blobColor = Color.WHITE;
        useBlobMotionBlur = false;
        blobMotionBlur = 0.0f;
    }

    /**
    *  The third constructor of the BlobStars3DScrollerEffectInitializer class. Initializes some variables to user selected values.
    */
    public BlobStars3DScrollerEffectInitializer(int blobWidth, int blobHeight, int blobScaleSize, int numberOf3DStars, Color blobColor, boolean useBlobMotionBlur)
    {
        this.blobWidth = blobWidth;
        this.blobHeight = blobHeight;
        this.blobScaleSize = blobScaleSize;
        haloExponent = 1.0f;
        scSize = 4096;
        starDistanceZ = 2048;
        this.numberOf3DStars = numberOf3DStars;
        this.blobColor = blobColor;
        this.useBlobMotionBlur = useBlobMotionBlur;
        blobMotionBlur = DEFAULT_MOTION_BLUR_SIZE;
    }

    /**
    *  The fourth constructor of the BlobStars3DScrollerEffectInitializer class. Initializes all variables to user selected values.
    */
    public BlobStars3DScrollerEffectInitializer(int blobWidth, int blobHeight, int blobScaleSize, float haloExponent, int scSize, int starDistanceZ, int numberOf3DStars, Color blobColor, boolean useBlobMotionBlur)
    {
        this.blobWidth = blobWidth;
        this.blobHeight = blobHeight;
        this.blobScaleSize = blobScaleSize;
        this.haloExponent = haloExponent;
        this.scSize = scSize;
        this.starDistanceZ = starDistanceZ;
        this.numberOf3DStars = numberOf3DStars;
        this.blobColor = blobColor;
        this.useBlobMotionBlur = useBlobMotionBlur;
        blobMotionBlur = DEFAULT_MOTION_BLUR_SIZE;
    }

    /**
    *  The fifth constructor of the BlobStars3DScrollerEffectInitializer class. Initializes all variables to user selected values.
    */
    public BlobStars3DScrollerEffectInitializer(int blobWidth, int blobHeight, int blobScaleSize, float haloExponent, int scSize, int starDistanceZ, int numberOf3DStars, Color blobColor, boolean useBlobMotionBlur, float blobMotionBlur)
    {
        this.blobWidth = blobWidth;
        this.blobHeight = blobHeight;
        this.blobScaleSize = blobScaleSize;
        this.haloExponent = haloExponent;
        this.scSize = scSize;
        this.starDistanceZ = starDistanceZ;
        this.numberOf3DStars = numberOf3DStars;
        this.blobColor = blobColor;
        this.useBlobMotionBlur = useBlobMotionBlur;
        this.blobMotionBlur = blobMotionBlur;
    }


}