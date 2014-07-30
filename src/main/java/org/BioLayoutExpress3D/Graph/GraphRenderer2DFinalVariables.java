package org.BioLayoutExpress3D.Graph;

import java.awt.geom.*;
import java.awt.image.*;
import org.BioLayoutExpress3D.Textures.*;
import static org.BioLayoutExpress3D.StaticLibraries.ImageProducer.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
*
* The GraphRenderer2DFinalVariables class is the class that holds all relevant GraphRenderer2D OpenGL variables.
* All variables in this static class have package scope and are to be used within GraphRenderer2D.
*
* @see org.BioLayoutExpress3D.Graph.GraphRenderer2D
* @author Thanos Theo, 2011
* @version 3.0.0.0
*
*/

final class GraphRenderer2DFinalVariables // package access
{

    /**
    *  Constant value needed for the 2D OpenGL renderer.
    */
    static final String DIR_NAME = IMAGE_FILES_PATH + "Skins2D/";

    /**
    *  Constant value needed for the 2D OpenGL renderer.
    */
    static final String FILE_NAME = "Skins2DData.txt";

    /**
    *  Constant value needed for the 2D OpenGL renderer.
    */
    static final int BACKGROUND_TEXTURE_SIZE = 1024;

    /**
    *  Constant value needed for the 2D OpenGL renderer. Uses the Singleton Design Pattern with method createBackgroundImage().
    */
    static final BufferedImage BACKGROUND_IMAGE = createBackgroundImage();

    /**
    *  Constant value needed for the 2D OpenGL renderer.
    */
    static final BufferedImage BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE_WITH_BORDERS = createBufferedImageWithTransparentBorders( BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE, (int)( 1.3f * BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE.getWidth() ), (int)( 1.3f * BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE.getHeight() ) );

    /**
    *  Constant enumeration nested class to choose between available logo effects.
    */
    static enum LogoEffects { WATER_EFFECT, TEXTUREDISPLACEMENT_EFFECT, BUMP_EFFECT, BLUR_EFFECT, RADIAL_BLUR_EFFECT, PLASMA_EFFECT }

    /**
    *  2D OpenGL renderer related variable.
    */
    static final float[] PLASMA_COLOR = new float[3];

    /**
    *  OpenGL specific animation variable.
    */
    static final float DEFAULT_SCALE = 0.05f;

    /**
    *  OpenGL specific animation variable.
    */
    static final float MORE_RESCALE_FACTOR = 3.0f;

    /**
    *  OpenGL specific animation variable.
    */
    static final float DEFAULT_TRANSLATION = 20.0f;

    /**
    *  Auxiliary variable to be used for nodes hierarchical modelling.
    */
    static final int[] ALL_SHAPES_2D_DISPLAY_LISTS = new int[NUMBER_OF_2D_SHAPES];

    /**
    *  Auxiliary variable to be used for focusing rotation/scaling to one particular node.
    */
    static final Point2D.Float FOCUS_POSITION_2D = new Point2D.Float(0.0f, 0.0f);

    /**
    *  OpenGL specific animation variable.
    */
    static final int NUMBER_OF_GENERATED_PARTICLE_EFFECTS = 4;

    /**
    *  OpenGL specific animation variable.
    */
    static final String PARTICLE_IMAGE_NAME = IMAGE_FILES_PATH + "BioLayoutExpress3DStar.png";

    /**
    *  OpenGL specific animation variable.
    */
    static final float PARTICLE_RESIZE_VALUE = 0.075f;

    /**
    *  Creates the background image. Uses the Singleton Design Pattern along with the static variable, BACKGROUND_IMAGE.
    */
    private static BufferedImage createBackgroundImage()
    {
        BufferedImage backgroundImage = new BufferedImage(BACKGROUND_TEXTURE_SIZE, BACKGROUND_TEXTURE_SIZE, BufferedImage.TYPE_INT_ARGB);
        int[] pixels = ImageSFXs.textureGenerate(2, BACKGROUND_TEXTURE_SIZE, BACKGROUND_TEXTURE_SIZE); // generate the random texture
        backgroundImage.setRGB(0, 0, BACKGROUND_TEXTURE_SIZE, BACKGROUND_TEXTURE_SIZE, pixels, 0, BACKGROUND_TEXTURE_SIZE);

        return backgroundImage;
    }

}