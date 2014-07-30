package org.BioLayoutExpress3D.Graph;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.nio.*;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.*;
import javax.media.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.GLPixelBuffer.GLPixelAttributes;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.awt.ImageUtil;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;
import static java.lang.Math.*;
import javax.imageio.ImageIO;
import static javax.media.opengl.GL2.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.DataStructures.*;
import org.BioLayoutExpress3D.GPUComputing.GLSL.Animation.*;
import org.BioLayoutExpress3D.Graph.ActiveRendering.*;
import org.BioLayoutExpress3D.Graph.GraphElements.*;
import org.BioLayoutExpress3D.Network.*;
import org.BioLayoutExpress3D.Physics.*;
import org.BioLayoutExpress3D.Textures.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import static org.BioLayoutExpress3D.Graph.Graph.*;
import static org.BioLayoutExpress3D.Graph.GraphRendererCommonVariables.*;
import static org.BioLayoutExpress3D.Graph.GraphRendererCommonFinalVariables.*;
import static org.BioLayoutExpress3D.Graph.GraphRenderer2DFinalVariables.*;
import static org.BioLayoutExpress3D.StaticLibraries.ImageProducer.*;
import static org.BioLayoutExpress3D.StaticLibraries.Random.*;
import static org.BioLayoutExpress3D.Textures.DrawTextureSFXs.*;
import static org.BioLayoutExpress3D.Environment.AnimationEnvironment.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.Shapes2D.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* The GraphRenderer2D class is the main 2D OpenGL renderer class of BioLayoutExpress3D.
* It utilizes TexturesLoader, ImageSFXs & DrawTextureSFXs for Texture loading, effects & drawing respectively.
*
* @see org.BioLayoutExpress3D.Textures.TexturesLoader
* @see org.BioLayoutExpress3D.Textures.ImageSFXs
* @see org.BioLayoutExpress3D.Textures.TextureSFXs
* @see org.BioLayoutExpress3D.Textures.ShaderTextureSFXs
* @see org.BioLayoutExpress3D.Textures.DrawTextureSFXs
* @see javax.media.opengl.GLCanvas
* @see org.BioLayoutExpress3D.Graph.Graph
* @see org.BioLayoutExpress3D.Graph.GraphRendererCommonVariables
* @see org.BioLayoutExpress3D.Graph.GraphRendererCommonFinalVariables
* @see org.BioLayoutExpress3D.Graph.GraphRenderer2DFinalVariables
* @author Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

final class GraphRenderer2D implements GraphInterface, TileRendererBase.TileRendererListener // package access
{

    /**
    *  Enum variable for setting the logo effect to be used.
    */
    private LogoEffects currentLogoEffect = LogoEffects.WATER_EFFECT;

    /**
    *  TextureLoader reference for the 2D OpenGL renderer.
    */
    private TexturesLoader texturesLoader = null;

    /**
    *  2D OpenGL renderer related variable.
    */
    private Texture backgroundImageTexture = null;

    /**
    *  2D OpenGL renderer related variable.
    */
    private Texture biolayoutLogoImageTexture = null;

    /**
    *  2D OpenGL renderer related variable.
    */
    private Texture biolayoutLogoImageTextureWithBorders = null;

    /**
    *  2D OpenGL renderer related variable.
    */
    private ImageSFXs imageSFXs = null;

    /**
    *  2D OpenGL renderer related variable.
    */
    private TextureSFXs textureSFXs = null;

    /**
    *  2D OpenGL renderer related variable.
    */
    private ShaderTextureSFXs shaderTextureSFXs = null;

    /**
    *  2D OpenGL renderer related variable.
    */
    private BlobStars3DScrollerEffectInitializer blobStars3DScrollerEffectInitializer = null;

    /**
    *  2D OpenGL renderer related variable.
    */
    private ArrayList<Integer> blobInitAnimIndex = new ArrayList<Integer>(4);

    /**
    *  Map related variable.
    */
    private volatile boolean updateDraggedEdgesDisplayList = false;

    /**
    *  OpenGL selection related variable.
    */
    private boolean isAltDown = false;

    /**
    *  OpenGL selection related variable.
    */
    private boolean isDraggingNodes = false;

    /**
    *  OpenGL selection related variable.
    */
    private int mouseDragStartX = 0;

    /**
    *  OpenGL selection related variable.
    */
    private float mouseDragStartTranslateX = 0.0f;

    /**
    *  OpenGL selection related variable.
    */
    private int mouseDragStartY = 0;

    /**
    *  OpenGL selection related variable.
    */
    private float mouseDragStartTranslateY = 0.0f;

    /**
    *  Rendering profile related variable.
    */
    private boolean renderProfileMode = false;

    /**
    *  OpenGL specific animation variable.
    */
    private float textureScaleFactor = 1.0f;

    /**
    *  OpenGL specific animation variable.
    */
    private float textureStep = 0.002f;

    /**
    *  OpenGL specific animation variable.
    */
    private float textureTheta = 0.0f;

    /**
    *  OpenGL specific animation variable.
    */
    private float blurSize = 0.0f;

    /**
    *  OpenGL specific animation variable.
    */
    private float blurSizeStep = 0.02f;

    /**
    *  OpenGL specific animation variable.
    */
    private int prevMouseXCoordForScale = 0;

    /**
    *  OpenGL specific animation variable.
    */
    private int prevMouseYCoordForScale = 0;

    /**
    *  OpenGL specific animation variable.
    */
    private float translateXValue = 0.0f;

    /**
    *  OpenGL specific animation variable.
    */
    private float translateYValue = 0.0f;

    /**
    *  OpenGL specific animation variable.
    */
    private float translateXSelectedValue = 0.0f;

    /**
    *  OpenGL specific animation variable.
    */
    private float translateYSelectedValue = 0.0f;

    /**
    *  OpenGL specific animation variable.
    */
    private float rotateValue = 0.0f;

    /**
    *  OpenGL specific animation variable.
    */
    private float amountOfRotation = 1.5f;

    /**
    *  OpenGL specific animation variable.
    */
    private float scaleValue = 0.0f;

    /**
    *  OpenGL specific animation variable.
    */
    private boolean enableProfiler = false;

    /**
    *  OpenGL specific animation variable.
    */
    private boolean enableScreenSaver = false;

    /**
    *  OpenGL specific animation variable.
    */
    private boolean negateUnit = false;

    /**
    *  OpenGL specific animation variable.
    */
    private boolean disableEdgeDrawingForMouseEvents = false;

    /**
    *  Auxiliary variable to be used with yEd specific change-of rendering.
    */
    private boolean prevYEdStyleRrenderingForGraphmlFiles = false;

    /**
    *  OpenGL specific variable.
    */
    private Shapes2D current2DShape = CIRCLE;

    /**
    *  Auxiliary variable to be used for mouse node dragging.
    */
    private HashSet<GraphEdge> remainingEdges = null;

    /**
    *  Auxiliary variable to be used for mouse node dragging.
    */
    private HashSet<GraphEdge> tempVisibleEdges = null;

    /**
    *  Auxiliary variable to be used for mouse node dragging.
    */
    private HashSet<GraphEdge> draggedEdges = null;

    /**
    *  Auxiliary variable to be used for mouse node dragging.
    */
    private IntBuffer draggedEdgesDisplayLists = null;

    /**
    *  Auxiliary variable to be used for mouse node dragging.
    */
    private int prevHowManyDisplayListsToCreateForNodeDragging = 0;

    /**
    *  Auxiliary variable to be used for mouse button navigation.
    */
    private boolean reverseNavigationMouseButtons = false;

    /**
    *  OpenGL specific animation variable.
    */
    private BufferedImage[] particleImages = null;

    /**
    *  OpenGL specific animation variable.
    */
    private ArrayList<ParticlesGenerator> allParticlesGeneratorEffects = null;

    /**
    *  OpenGL specific animation variable.
    */
    private ArrayList<Integer> particleInitAnimIndex = new ArrayList<Integer>(NUMBER_OF_GENERATED_PARTICLE_EFFECTS);

    /**
    *  OpenGL specific animation variable.
    */
    private boolean[] particleEffectToShow = null;

    /**
    *  OpenGL specific animation variable.
    */
    private boolean showAllParticleEffects = false;

    /**
    *  OpenGL specific animation variable.
    */
    private boolean rightMostPartOfScreenForGeneratorEffect3 = false;

    /**
    *  OpenGL specific animation variable.
    */
    private boolean foregroundAnimation = false;

    /**
    *  OpenGL specific animation variable.
    */
    private boolean continueRenderingForegroundAnimationWhileSwitchingRenderer = false;

    /**
    *  OpenGL specific animation variable.
    */
    private boolean switchRenderer = false;

    /**
    *  OpenGL specific animation variable.
    */
    private Graph graph = null;

    /**
    *  The GraphRenderer2D class constructor.
    */
    public GraphRenderer2D(Graph graph)
    {
        this.graph = graph;

        remainingEdges = new HashSet<GraphEdge>();
        tempVisibleEdges = new HashSet<GraphEdge>();
        draggedEdges = new HashSet<GraphEdge>();
    }

    /**
    *  Toggles the map rotation mode.
    */
    private void toggleRotation(ActionEvent e)
    {
        if ( (!USE_SHADERS_PROCESS) ? imageSFXs.hasSpotCircleEffectFinished() : shaderTextureSFXs.hasSpotCircleEffectFinished() )
        {
            if ( e.getActionCommand().equals(START_ANIMATION_EVENT_STRING) )
            {
                resetAllRelevantValues(false);
                startRotation();
            }
            else if ( e.getActionCommand().equals(STOP_ANIMATION_EVENT_STRING) )
            {
                stopRotationAndScreenSaver(false);
            }
            else
            {
                if (!enableProfiler)
                    startRotation();
                else
                    stopRotationAndScreenSaver(true);
            }
        }
    }

    /**
    *  Toggles the screensaver mode.
    */
    private void toggleScreenSaver()
    {
        if ( (!USE_SHADERS_PROCESS) ? imageSFXs.hasSpotCircleEffectFinished() : shaderTextureSFXs.hasSpotCircleEffectFinished() )
        {
            if (!enableScreenSaver)
                startScreenSaver();
            else
                stopRotationAndScreenSaver(true);
        }
    }

    /**
    *  Starts rotation.
    */
    private void startRotation()
    {
        // make sure the background color is resetted
        graph.prepareBackgroundColor();

        negateUnit = org.BioLayoutExpress3D.StaticLibraries.Random.nextBoolean();
        enableProfiler = true;
        enableScreenSaver = false;
        renderProfileMode = true;

        amountOfRotation = 1.5f;

        startRender();
    }

        /**
    *  Starts rotation.
    */
    private void startScreenSaver()
    {
        negateUnit = org.BioLayoutExpress3D.StaticLibraries.Random.nextBoolean();
        enableProfiler = false;
        enableScreenSaver = true;
        renderProfileMode = true;

        amountOfRotation = getRandomRange(0, 100) / 100f;

        startRender();
    }

    /**
    *  Stops rotation & screensaver.
    */
    private void stopRotationAndScreenSaver(boolean resetAllValues)
    {
        enableScreenSaver = false;
        amountOfRotation = 1.5f;

        resetAllRelevantValues(resetAllValues);
    }

    /**
    *  Loads the BioLayout Express3D star image. Uses the Singleton Design Pattern along with the static variable.
    */
    private BufferedImage loadBioLayoutExpress3DStarImage()
    {
        return loadImageFromURL( GraphRenderer2D.class.getResource(PARTICLE_IMAGE_NAME) );
    }

    /**
    *  Resets all relevant values.
    */
    private void resetAllRelevantValues(boolean resetAllValues)
    {
        if ( (graphRendererThreadUpdater != null) && !animationRender ) stopRender();

        if (!animationRender)
        {
            renderProfileMode = false;
            enableProfiler = false;
            enableScreenSaver = false;
        }

        rotateValue = 0.0f;
        amountOfRotation = 1.5f;

        if (resetAllValues)
        {
            FOCUS_POSITION_2D.setLocation(width / 2.0f, height / 2.0f);

            scaleValue = 0.0f;
            translateXValue = 0.0f;
            translateYValue = 0.0f;

            textureScaleFactor = 1.0f;
            textureTheta = 0.0f;
            blurSize = 0.0f;
            currentLogoEffect = LogoEffects.WATER_EFFECT;
        }

        graph.prepareBackgroundColor();

        refreshDisplay();
    }

    /**
    *  Prepares the texture loader.
    */
    private void prepareTexturesLoader()
    {
        if (texturesLoader == null) texturesLoader = new TexturesLoader(DIR_NAME, FILE_NAME, qualityRendering, false); // only instantiate it once in first switch
    }

    private void clearScreen2D(GL2 gl)
    {
        if (TRIPPY_BACKGROUND.get() && !takeHighResScreenshot)
            graph.colorCycle(BACKGROUND_COLOR_ARRAY);

        gl.glClearColor(BACKGROUND_COLOR_ARRAY[0], BACKGROUND_COLOR_ARRAY[1], BACKGROUND_COLOR_ARRAY[2], 1.0f);
        gl.glClear(GL_COLOR_BUFFER_BIT);
    }

    /**
    *  Prepares a random plasma color.
    */
    private void prepareRandomPlasmaColor()
    {
        PLASMA_COLOR[0] = getRandomRange(0, 100) / 100.0f;
        PLASMA_COLOR[1] = getRandomRange(0, 100) / 100.0f;
        PLASMA_COLOR[2] = getRandomRange(0, 100) / 100.0f;
    }

    /**
    *  Prepares the image special effects.
    */
    private void prepareImageAndTextureSFXs(GL2 gl)
    {
        if (backgroundImageTexture != null) backgroundImageTexture = null;
        backgroundImageTexture = TextureProducer.createTextureFromBufferedImage(BACKGROUND_IMAGE, qualityRendering);

        if (biolayoutLogoImageTexture != null) biolayoutLogoImageTexture = null;
        biolayoutLogoImageTexture = TextureProducer.createTextureFromBufferedImage(BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE, qualityRendering);

        if (biolayoutLogoImageTextureWithBorders != null) biolayoutLogoImageTextureWithBorders = null;
        biolayoutLogoImageTextureWithBorders = TextureProducer.createTextureFromBufferedImage(BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE_WITH_BORDERS, qualityRendering);

        if ( blobInitAnimIndex.isEmpty() )
            blobInitAnimIndex = createRandomIndexIntegerArrayList(4);
        int randomNumber = blobInitAnimIndex.get(0);
        blobInitAnimIndex.remove(0);

        Color randomBlobColor = new Color(PLASMA_COLOR[0], PLASMA_COLOR[1], PLASMA_COLOR[2]);
        blobStars3DScrollerEffectInitializer = new BlobStars3DScrollerEffectInitializer(64, 64, (randomNumber + 1) * 12, (4 - randomNumber) * 64, randomBlobColor, USE_SHADERS_PROCESS);

        if (textureSFXs == null) textureSFXs = new TextureSFXs();
        textureSFXs.blobStars3DScrollerEffectInit(gl, blobStars3DScrollerEffectInitializer, qualityRendering);

        if (!USE_SHADERS_PROCESS)
        {
            if (imageSFXs == null) imageSFXs = new ImageSFXs();
            imageSFXs.setWaterEffectImage(BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE);
            imageSFXs.plasmaEffectInit( BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE.getWidth(), BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE.getHeight() );
            imageSFXs.setBumpEffectImage(256, 256, 200, 425, 200, BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE);
            imageSFXs.setRadialBlurEffectImage(8, BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE);

            // make one update for the multicore mode image so as to avoid first time flickered frame
            if (USE_MULTICORE_PROCESS)
            {
                imageSFXs.bumpEffect();
                imageSFXs.radialBlurEffect((int)( 0.1f * BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE.getHeight() ), true, BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE.getWidth() / 4, BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE.getHeight() / 4);
            }

            textureSFXs.textureDisplacementEffectInit(0.001f);
        }
        else
        {
            if (shaderTextureSFXs == null) shaderTextureSFXs = new ShaderTextureSFXs(gl, width, height, USE_GL_EXT_FRAMEBUFFER_OBJECT); // only instantiate it once in first switch
            shaderTextureSFXs.textureDisplacementEffectInit(2.0f);
            shaderTextureSFXs.plasmaEffectInit(10.0f);
            shaderTextureSFXs.spotCircleEffectInit(0.1f, width, height, width / 2.0f, height / 2.0f, -24.0f, false, 1);
            shaderTextureSFXs.waterEffectInit(biolayoutLogoImageTextureWithBorders, 32, true, 1, 10.0f);
            shaderTextureSFXs.bumpEffectInit(gl, BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE, biolayoutLogoImageTexture, false, 0.01f);
            shaderTextureSFXs.blurEffectInit(gl, width, height, biolayoutLogoImageTexture, ShaderTextureSFXsBlurStates.FULL_BLUR, qualityRendering);
            shaderTextureSFXs.radialBlurEffectInit(0.01f);
        }
    }

    /**
    *  Prepares the particles effects.
    */
    private void prepareParticlesEffect()
    {
        particleImages = new BufferedImage[NUMBER_OF_GENERATED_PARTICLE_EFFECTS];
        for (int i = 0; i < NUMBER_OF_GENERATED_PARTICLE_EFFECTS; i++)
            particleImages[i] = resizeImageByGivenRatio(loadBioLayoutExpress3DStarImage(), PARTICLE_RESIZE_VALUE, true);

        allParticlesGeneratorEffects = new ArrayList<ParticlesGenerator>(NUMBER_OF_GENERATED_PARTICLE_EFFECTS);
        for (int i = 0; i < NUMBER_OF_GENERATED_PARTICLE_EFFECTS; i++)
            allParticlesGeneratorEffects.add( new ParticlesGenerator(particleImages[i]) );

        particleEffectToShow = new boolean[NUMBER_OF_GENERATED_PARTICLE_EFFECTS];
    }

    /**
    *  Prepares a particle effect.
    */
    private void chooseParticleEffect()
    {
        for (int i = 0; i < NUMBER_OF_GENERATED_PARTICLE_EFFECTS; i++)
            particleEffectToShow[i] = false;

        if ( particleInitAnimIndex.isEmpty() )
            particleInitAnimIndex = createRandomIndexIntegerArrayList(NUMBER_OF_GENERATED_PARTICLE_EFFECTS);
        int randomNumb = particleInitAnimIndex.get(0);
        particleInitAnimIndex.remove(0);

        particleEffectToShow[randomNumb] = true;
        if (particleEffectToShow[2]) rightMostPartOfScreenForGeneratorEffect3 = nextBoolean();
    }

    /**
    *  Takes a screenshot.
    */
    private void takeScreenshot(GL2 gl, boolean renderToFile)
    {
        if (DEBUG_BUILD) println("GraphRenderer2D takeScreenshot()");

        try
        {
            AWTGLReadBufferUtil agrbu = new AWTGLReadBufferUtil(GLProfile.getDefault(), false);
            screenshot = agrbu.readPixelsToBufferedImage(gl, true);

            if (renderToFile)
            {
                if (DEBUG_BUILD) println("Screenshot " + saveScreenshotFile.getAbsolutePath() + " taken");
                ImageIO.write(screenshot, "png", saveScreenshotFile);
                screenshot = null;
                InitDesktop.open(saveScreenshotFile);
            }
        }
        catch (GLException glExc)
        {
            if (DEBUG_BUILD) println("GLException in GraphRenderer2D.takeScreenshot():\n" + glExc.getMessage());

            // do it here before the showMessageDialog() so as to avoid refreshes that will fire up the screenshot rendering!
            takeScreenshot = false;

            JOptionPane.showMessageDialog(graph, "Something went wrong while saving the file:\n" + glExc.getMessage(), "Error with saving the file!", JOptionPane.ERROR_MESSAGE);
        }
        catch (Exception exc)
        {
            if (DEBUG_BUILD) println("Exception in GraphRenderer2D.takeScreenshot():\n" + exc.getMessage());

            // do it here before the showMessageDialog() so as to avoid refreshes that will fire up the screenshot rendering!
            takeScreenshot = false;

            JOptionPane.showMessageDialog(graph, "Something went wrong while saving the image to file:\n" + exc.getMessage() + "\nPlease try again with a different file name/path/drive.", "Error with saving the image to file!", JOptionPane.ERROR_MESSAGE);

            graph.initiateTakeScreenShotProcess(false);
        }
        finally
        {
            takeScreenshot = false;
        }
    }

    /**
    *  Takes a high resolution screenshot.
    */
    private void takeHighResScreenshot(GL2 gl)
    {
        float originalEdgeSize = DEFAULT_EDGE_SIZE.get();
        DEFAULT_EDGE_SIZE.set(originalEdgeSize * TILE_SCREEN_FACTOR.get());
        updateEdgesDisplayList = true;
        buildAllDisplayListsAndRenderScene2D(gl);

        try
        {
            int tileWidth = width * TILE_SCREEN_FACTOR.get();
            int tileHeight = height * TILE_SCREEN_FACTOR.get();

            TileRenderer tr = new TileRenderer();

            tr.setTileSize(256, 256, 0);
            tr.setImageSize(tileWidth, tileHeight);
            //tr.trOrtho(0.0f, width, height, 0.0f, -1.0f, 1.0f);

            final GLPixelBuffer.GLPixelBufferProvider pixelBufferProvider = GLPixelBuffer.defaultProviderWithRowStride;
            final boolean[] flipVertically =
            {
                false
            };

            GLPixelAttributes pixelAttribs = pixelBufferProvider.getAttributes(gl, 3);
            GLPixelBuffer pixelBuffer = pixelBufferProvider.allocate(gl, pixelAttribs, tileWidth, tileHeight, 1, true, 0);

            tr.setImageBuffer(pixelBuffer);

            int tileCount = 0;
            this.addTileRendererNotify(tr);
            while(!tr.eot())
            {
                tr.beginTile(gl);
                this.reshape(gl,
                    tr.getParam(TileRendererBase.TR_CURRENT_TILE_X_POS),
                    tr.getParam(TileRendererBase.TR_CURRENT_TILE_Y_POS),
                    tr.getParam(TileRendererBase.TR_CURRENT_TILE_WIDTH),
                    tr.getParam(TileRendererBase.TR_CURRENT_TILE_HEIGHT),
                    tr.getParam(TileRendererBase.TR_IMAGE_WIDTH),
                    tr.getParam(TileRendererBase.TR_IMAGE_HEIGHT));

                clearScreen2D(gl);
                buildAllDisplayListsAndRenderScene2D(gl);

                layoutProgressBarDialog.incrementProgress(++tileCount);
                tr.endTile(gl);
            }
            this.removeTileRendererNotify(tr);

            if (DEBUG_BUILD) println(tileCount + " tiles drawn\n");

            layoutProgressBarDialog.incrementProgress(100);
            layoutProgressBarDialog.setText( "Writing image to: " + saveScreenshotFile.getAbsolutePath() );

            if (DEBUG_BUILD) println( "Now Writing High Res BufferedImage to File: " + saveScreenshotFile.getAbsolutePath() );

            final GLPixelBuffer imageBuffer = tr.getImageBuffer();
            final TextureData textureData = new TextureData(
                    GLProfile.getDefault(),
                    0 /* internalFormat */,
                    tileWidth, tileHeight,
                    0,
                    imageBuffer.pixelAttributes,
                    false, false,
                    flipVertically[0],
                    imageBuffer.buffer,
                    null /* Flusher */);

            TextureIO.write(textureData, saveScreenshotFile);

            System.gc();

            if (DEBUG_BUILD) println("Done Writing High Res BufferedImage to File");

            layoutProgressBarDialog.endProgressBar();
            layoutProgressBarDialog.stopProgressBar();
            layoutProgressBarDialog.setIndeterminate(false);

            InitDesktop.open(saveScreenshotFile);
        }
        catch (OutOfMemoryError memErr)
        {
            if (DEBUG_BUILD) println("Out of Memory Error with creating the High Res Image to File in GraphRenderer2D.takeHighResScreenshot():\n" + memErr.getMessage());

            // do it here before the showMessageDialog() so as to avoid refreshes that will fire up the high res screenshot rendering!
            takeHighResScreenshot = false;

            layoutProgressBarDialog.endProgressBar();
            layoutProgressBarDialog.stopProgressBar();
            layoutProgressBarDialog.setIndeterminate(false);

            JOptionPane.showMessageDialog(graph, "Out of memory while creating the high resolution image to file:\n" + memErr.getMessage() + "\nPlease try again with a smaller scale value.", "Error with creating the high resolution image to file!", JOptionPane.ERROR_MESSAGE);
        }
        catch (Exception exc)
        {
            if (DEBUG_BUILD) println("Exception with writing the High Res Image to File in GraphRenderer2D.takeHighResScreenshot():\n" + exc.getMessage());

            // do it here before the showMessageDialog() so as to avoid refreshes that will fire up the high res screenshot rendering!
            takeHighResScreenshot = false;

            layoutProgressBarDialog.endProgressBar();
            layoutProgressBarDialog.stopProgressBar();
            layoutProgressBarDialog.setIndeterminate(false);

            JOptionPane.showMessageDialog(graph, "Something went wrong while saving the high resolution image to file:\n" + exc.getMessage() + "\nPlease try again with a different file name/path/drive.", "Error with saving the high resolution image to file!", JOptionPane.ERROR_MESSAGE);

            graph.initiateTakeScreenShotProcess(true);
        }
        finally
        {
            takeHighResScreenshot = false;

            // Reset canvas size
            this.reshape(graph, 0, 0, width, height);

            // set back project matrix
            gl.glMatrixMode(GL_PROJECTION);
            gl.glLoadIdentity();
            gl.glOrtho(0.0f, width, height, 0.0f, -1.0f, 1.0f);
            // same with:
            // GLU.gluOrtho2D(0, origWidth, origHeight, 0);
            gl.glMatrixMode(GL_MODELVIEW);

            DEFAULT_EDGE_SIZE.set(originalEdgeSize);
            updateEdgesDisplayList = true;
            buildAllDisplayListsAndRenderScene2D(gl);
        }
    }

    @Override
    public void addTileRendererNotify(TileRendererBase tr)
    {
    }

    @Override
    public void removeTileRendererNotify(TileRendererBase tr)
    {
    }

    @Override
    public void startTileRendering(TileRendererBase tr)
    {
    }

    @Override
    public void endTileRendering(TileRendererBase tr)
    {
    }

    @Override
    public void reshapeTile(TileRendererBase tr,
            int tileX, int tileY, int tileWidth, int tileHeight,
            int imageWidth, int imageHeight)
    {
        final GL2 gl = tr.getAttachedDrawable().getGL().getGL2();
        gl.setSwapInterval(0);
        reshape(gl, tileX, tileY, tileWidth, tileHeight, imageWidth, imageHeight);
    }

    public void reshape(GL2 gl, int tileX, int tileY, int tileWidth, int tileHeight, int imageWidth, int imageHeight)
    {
        // Scene dimensions
        float originalLeft = 0.0f;
        float originalRight = width;
        float originalTop = 0.0f;
        float originalBottom = height;

        final float w = originalRight - originalLeft;
        final float h = originalTop - originalBottom;

        // Tile dimensions
        final float tileLeft = originalLeft + tileX * w / imageWidth;
        final float tileRight = tileLeft + tileWidth * w / imageWidth;
        final float tileBottom = originalBottom + tileY * h / imageHeight;
        final float tileTop = tileBottom + tileHeight * h / imageHeight;

        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(tileLeft, tileRight, tileBottom, tileTop, -1.0f, 1.0f);

        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();
        performOpenGLTransformations(gl);
    }

    /**
    *  Initiates the high resolution render to file process.
    */
    private void highResRenderToFile()
    {
        if (graphRendererThreadUpdater != null)
        {
            stopRender();

            renderProfileMode = false;
            enableProfiler = false;
            enableScreenSaver = false;

            graph.prepareBackgroundColor();

            // refresh display to clear renderer gfx
            refreshDisplay();
        }

        layoutProgressBarDialog.setIndeterminate(true);
        layoutProgressBarDialog.prepareProgressBar(100, "Now Performing High Resolution Render Image to file (may take some time)...");
        layoutProgressBarDialog.startProgressBar();

        // so as to show some progress bar animation, as the render thread will not manage update the progress bar for some reason
        for (int i = 0; i < 100; i++)
        {
            LayoutFrame.sleep(10);
            layoutProgressBarDialog.incrementProgress();
        }
    }

    /**
    *  Deletes all display lists.
    */
    private void deleteAllDisplayLists(GL2 gl)
    {
        for (int i = 0; i < ALL_SHAPES_2D_DISPLAY_LISTS.length; i++)
            gl.glDeleteLists(ALL_SHAPES_2D_DISPLAY_LISTS[i], 1);

        if (allEdgesDisplayLists != null) // if allEdgesDisplayLists not empty, delete all its display lists
        {
            for (int i = 0; i < allEdgesDisplayLists.capacity(); i++)
                gl.glDeleteLists(allEdgesDisplayLists.get(i), 1);

            allEdgesDisplayLists.clear();
            allEdgesDisplayLists = null;
        }

        if (draggedEdgesDisplayLists != null) // if draggedEdgesDisplayLists not empty, delete all its display lists
        {
            for (int i = 0; i < draggedEdgesDisplayLists.capacity(); i++)
                gl.glDeleteLists(draggedEdgesDisplayLists.get(i), 1);

            draggedEdgesDisplayLists.clear();
            draggedEdgesDisplayLists = null;
        }

        // if ( gl.glIsList(nodeList) ) // always delete display list, an attempt to delete a list that has never been created is ignored
        gl.glDeleteLists(nodesDisplayList, 1);

        // if ( gl.glIsList(nodeList) ) // always delete display list, an attempt to delete a list that has never been created is ignored
        gl.glDeleteLists(selectedNodesDisplayList, 1);

        // if ( gl.glIsList(pathwayComponentContainersDisplayList) ) // always delete display list, an attempt to delete a list that has never been created is ignored
        gl.glDeleteLists(pathwayComponentContainersDisplayList, 1);

        prevHowManyDisplayListsToCreate = 0;
        prevHowManyDisplayListsToCreateForNodeDragging = 0;
    }

    /**
    *  Disposes all textures.
    */
    private void disposeAllTextures(GL2 gl)
    {
        if (DEBUG_BUILD) println("Dispose all 2D textures");

        // don't destroy it, keep it for faster renderer mode switching
        /*
        if (texturesLoader != null)
        {
            texturesLoader.destructor();
            texturesLoader = null;
        }
        */

        // don't destroy it, keep it for faster renderer mode switching
        /*
        if (imageSFXs != null)
        {
            imageSFXs.destructor();
            imageSFXs = null;
        }
        */

        // don't destroy it, keep it for faster renderer mode switching
        /*
        if (textureSFXs != null)
        {
            textureSFXs.destructor(gl);
            textureSFXs = null;
        }
        */

        // don't destroy it, keep it for faster renderer mode switching
        /*
        if (shaderTextureSFXs != null)
        {
            shaderTextureSFXs.destructor(gl);
            shaderTextureSFXs = null;
        }
        */

        if (particleImages != null)
        {
            for (BufferedImage image : particleImages) image.flush();
            particleImages = null;
        }

        if (allParticlesGeneratorEffects != null)
        {
            for (ParticlesGenerator particlesGenerator : allParticlesGeneratorEffects)
                particlesGenerator.destructor(gl);

            allParticlesGeneratorEffects = null;
        }

        if (backgroundImageTexture != null)
        {
            backgroundImageTexture = null;
        }

        if (biolayoutLogoImageTexture != null)
        {
            biolayoutLogoImageTexture = null;
        }

        if (biolayoutLogoImageTextureWithBorders != null)
        {
            biolayoutLogoImageTextureWithBorders = null;
        }

        if (screenshot != null)
        {
            screenshot.flush();
            screenshot = null;
        }
    }

    /**
    *  Starts the updating/rendering thread(s).
    *  Overrides the parent's abstract method.
    */
    private void startRender()
    {
        if (graphRendererThreadUpdater == null)
        {
            graphRendererThreadUpdater = new GraphRendererThreadUpdater(graph, this, frameskip, fullscreen, targetFPS);
            graphRendererThreadUpdater.startWithPriority(Thread.NORM_PRIORITY);
        }
    }

    /**
    *  Stops updating/rendering thread(s).
    *  Overrides the parent's abstract method.
    */
    private void stopRender()
    {
        if (graphRendererThreadUpdater != null)
        {
            graphRendererThreadUpdater.setRendering(false);
            graphRendererThreadUpdater = null;
        }
    }

    /**
    *  Renders the background layer.
    */
    private void renderBackgroundLayer(GL2 gl)
    {
        if (DEBUG_BUILD) println("Rendering 2D Background Layer");

        if (!USE_SHADERS_PROCESS)
            textureSFXs.textureDisplacementRender(gl, backgroundImageTexture, 0, 0, width, height);
        else
            shaderTextureSFXs.textureDisplacementEffectRender( gl, backgroundImageTexture, 0, 0, width, height, MATERIAL_OLD_LCD_STYLE_TRANSPARENCY_SHADING.get() );

        drawRotoZoomTexture(gl, backgroundImageTexture, 0, 0, (textureTheta += 0.333f) % 360.0f, 2.5 + (width / BACKGROUND_TEXTURE_SIZE), 2.5 + (height / BACKGROUND_TEXTURE_SIZE), 0.6f);

        textureTheta = (textureTheta += 0.333f) % 360.0f;

        if ( currentLogoEffect.equals(LogoEffects.WATER_EFFECT) )
        {
            if (!USE_SHADERS_PROCESS)
                drawRotoZoomImageTexture(gl, imageSFXs.waterEffectImage(), (width - BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE.getWidth()) / 2, (height - BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE.getHeight()) / 2, textureTheta, textureScaleFactor, textureScaleFactor, 1.0f);
            else
                shaderTextureSFXs.waterEffectRotoZoomRender(gl, (width - BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE_WITH_BORDERS.getWidth()) / 2, (height - BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE_WITH_BORDERS.getHeight()) / 2, textureTheta, textureScaleFactor, textureScaleFactor, MATERIAL_OLD_LCD_STYLE_TRANSPARENCY_SHADING.get(), 24.0f);
        }
        else if ( currentLogoEffect.equals(LogoEffects.TEXTUREDISPLACEMENT_EFFECT) )
            shaderTextureSFXs.textureDisplacementEffectRotoZoomRender( gl, biolayoutLogoImageTextureWithBorders, (width - BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE_WITH_BORDERS.getWidth()) / 2, (height - BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE_WITH_BORDERS.getHeight()) / 2, textureTheta, textureScaleFactor, textureScaleFactor, MATERIAL_OLD_LCD_STYLE_TRANSPARENCY_SHADING.get() );
        else if ( currentLogoEffect.equals(LogoEffects.BUMP_EFFECT) )
        {
            if (!USE_SHADERS_PROCESS)
                drawRotoZoomImageTexture(gl, imageSFXs.bumpEffectImage(), (width - BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE.getWidth()) / 2, (height - BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE.getHeight()) / 2, textureTheta, textureScaleFactor, textureScaleFactor, 1.0f);
            else
                shaderTextureSFXs.bumpEffectRotoZoomRender(gl, (width - BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE.getWidth()) / 2, (height - BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE.getHeight()) / 2, textureTheta, textureScaleFactor, textureScaleFactor, MATERIAL_OLD_LCD_STYLE_TRANSPARENCY_SHADING.get(), 16.0f);
        }
        else if ( currentLogoEffect.equals(LogoEffects.BLUR_EFFECT) )
            shaderTextureSFXs.blurEffectRotoZoomRender(gl, (width - BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE.getWidth()) / 2, (height - BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE.getHeight()) / 2, textureTheta, textureScaleFactor, textureScaleFactor, MATERIAL_OLD_LCD_STYLE_TRANSPARENCY_SHADING.get(), blurSize);
        else if ( currentLogoEffect.equals(LogoEffects.RADIAL_BLUR_EFFECT) )
        {
            if (!USE_SHADERS_PROCESS)
                drawRotoZoomImageTexture(gl, imageSFXs.radialBlurEffectImage(), (width - BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE.getWidth()) / 2, (height - BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE.getHeight()) / 2, textureTheta, textureScaleFactor, textureScaleFactor, 1.0f);
            else
                shaderTextureSFXs.radialBlurEffectRotoZoomRender(gl, biolayoutLogoImageTextureWithBorders, (width - BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE_WITH_BORDERS.getWidth()) / 2, (height - BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE_WITH_BORDERS.getHeight()) / 2, textureTheta, textureScaleFactor, textureScaleFactor, MATERIAL_OLD_LCD_STYLE_TRANSPARENCY_SHADING.get(), 0.1f, 0.85f);
        }
        else if ( currentLogoEffect.equals(LogoEffects.PLASMA_EFFECT) )
        {
            if (!USE_SHADERS_PROCESS)
                drawRotoZoomImageTexture(gl, imageSFXs.plasmaEffectImage(), (width - BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE.getWidth()) / 2, (height - BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE.getHeight()) / 2, textureTheta, textureScaleFactor, textureScaleFactor, 0.6f, new Color(PLASMA_COLOR[0], PLASMA_COLOR[1], PLASMA_COLOR[2]));
            else
                shaderTextureSFXs.plasmaEffectRotoZoomRender( gl, (width - BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE.getWidth()) / 2, (height - BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE.getHeight()) / 2, BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE.getWidth(), BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE.getHeight(), textureTheta, textureScaleFactor, textureScaleFactor, 0.5f, MATERIAL_OLD_LCD_STYLE_TRANSPARENCY_SHADING.get() );
        }

        if ( !USE_SHADERS_PROCESS || !( NORMAL_QUALITY_ANTIALIASING.get() || HIGH_QUALITY_ANTIALIASING.get() ) )
            textureSFXs.blobStars3DScrollerRender(gl, width, height);
        else
            shaderTextureSFXs.blobStars3DScrollerEffectRender( gl, 0, 0, width, height, MATERIAL_OLD_LCD_STYLE_TRANSPARENCY_SHADING.get() );

        for (int i = 0; i < NUMBER_OF_GENERATED_PARTICLE_EFFECTS; i++)
            if (particleEffectToShow[i])
                allParticlesGeneratorEffects.get(i).rotoZoomRenderAllGeneratedParticles(gl, 0.25f);
    }

    /**
    *  Renders the foreground layer.
    */
    private void renderForegroundLayer(GL2 gl)
    {
        // foreground layer resets all previous scene transformations
        gl.glLoadIdentity();
        if (!USE_SHADERS_PROCESS)
            drawImageTexture(gl, imageSFXs.spotCircleEffectImage(), 0, 0, width, height);
        else
            shaderTextureSFXs.spotCircleEffectRender( gl, 0, 0, width, height, MATERIAL_OLD_LCD_STYLE_TRANSPARENCY_SHADING.get() );
    }

    /**
    *  Performs all OpenGL related transformations.
    */
    private void performOpenGLTransformations(GL2 gl)
    {
        // center the rotation to the FOCUS_POSITION_2D
        gl.glTranslatef(FOCUS_POSITION_2D.x, FOCUS_POSITION_2D.y, 0.0f);

        gl.glScalef(1.0f + scaleValue, 1.0f + scaleValue, 0.0f);
        gl.glTranslatef(translateXValue, translateYValue, 0.0f);

        if (!enableProfiler || animationRender)
            gl.glRotatef(rotateValue, 0.0f, 0.0f, 1.0f);
        else
            gl.glRotatef(rotateValue, 1.0f, 1.0f, 1.0f);

        // reset centered transformation
        gl.glTranslatef(-FOCUS_POSITION_2D.x, -FOCUS_POSITION_2D.y, 0.0f);
    }

    /**
    *  Builds all display lists and renders the 2D OpenGL scene.
    */
    private void buildAllDisplayListsAndRenderScene2D(GL2 gl)
    {
        if (DEBUG_BUILD) println("Rendering 2D Map");

        // code for map rendering from here on, using display lists for edges & nodes

        if (ANIMATION_CHANGE_SPECTRUM_TEXTURE_ENABLED)
        {
            graph.prepareAnimationSpectrumTexture(gl);
            ANIMATION_CHANGE_SPECTRUM_TEXTURE_ENABLED = false;
        }

        if (CHANGE_GRAPHML_COMPONENT_CONTAINERS)
        {
            // if ( gl.glIsList(pathwayComponentContainersDisplayList) ) // always delete display list, an attempt to delete a list that has never been created is ignored
            gl.glDeleteLists(pathwayComponentContainersDisplayList, 1);
            gl.glNewList(pathwayComponentContainersDisplayList, GL_COMPILE);
            drawPathwayComponentContainers2DMode(gl);
            gl.glEndList();

            CHANGE_GRAPHML_COMPONENT_CONTAINERS = false;
        }

        if (!TEMPORARILY_DISABLE_ALL_GRAPH_RENDERING)
            if ( !DISABLE_NODES_RENDERING.get() && (SHOW_NODES.get() || !isInMotion) )
                if ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() && YED_STYLE_COMPONENT_CONTAINERS_RENDERING_FOR_GPAPHML_FILES.get() )
                    gl.glCallList(pathwayComponentContainersDisplayList);

        if (updateEdgesDisplayList)
        {
            if (DEBUG_BUILD) println("Updating edges display list");

            // if allEdgesDisplayLists not empty, delete all its display lists
            if (allEdgesDisplayLists != null)
                for (int i = 0; i < allEdgesDisplayLists.capacity(); i++)
                    gl.glDeleteLists(allEdgesDisplayLists.get(i), 1);

            if ( !DISABLE_EDGES_RENDERING.get() )
                allEdgesDisplayLists = drawAllVisibleEdges(gl, allEdgesDisplayLists, visibleEdges, false);

            updateEdgesDisplayList = false;
        }

        if (!TEMPORARILY_DISABLE_ALL_GRAPH_RENDERING)
            if ( !DISABLE_EDGES_RENDERING.get() && (allEdgesDisplayLists != null) )
                gl.glCallLists(allEdgesDisplayLists.capacity(), GL_INT, allEdgesDisplayLists);

        if (isDraggingNodes)
        {
            if ( SHOW_EDGES_WHEN_DRAGGING_NODES.get() )
            {
                if (updateDraggedEdgesDisplayList)
                {
                    if (DEBUG_BUILD) println("Updating dragged edges display list");

                    // if draggedEdgesDisplayLists not empty, delete all its display lists
                    if (draggedEdgesDisplayLists != null)
                        for (int i = 0; i < draggedEdgesDisplayLists.capacity(); i++)
                            gl.glDeleteLists(draggedEdgesDisplayLists.get(i), 1);

                    if ( !DISABLE_EDGES_RENDERING.get() )
                        draggedEdgesDisplayLists = drawAllVisibleEdges(gl, draggedEdgesDisplayLists, draggedEdges, true);

                    updateDraggedEdgesDisplayList = false;
                }

                if (!TEMPORARILY_DISABLE_ALL_GRAPH_RENDERING)
                {
                    if (disableEdgeDrawingForMouseEvents && !selectionManager.getSelectedNodes().isEmpty() && !DISABLE_EDGES_RENDERING.get() && draggedEdgesDisplayLists != null)
                    {
                        gl.glPushMatrix();
                        gl.glTranslatef(translateXSelectedValue, translateYSelectedValue, 0.0f);
                        gl.glCallLists(draggedEdgesDisplayLists.capacity(), GL_INT, draggedEdgesDisplayLists);
                        gl.glPopMatrix();
                    }
                }
            }
        }

        if (updateNodesDisplayList)
        {
            if (DEBUG_BUILD) println("Updating nodes display list");

            // if ( gl.glIsList(nodeList) ) // always delete display list, an attempt to delete a list that has never been created is ignored
            gl.glDeleteLists(nodesDisplayList, 1);

            if ( !DISABLE_NODES_RENDERING.get() )
            {
                gl.glNewList(nodesDisplayList, GL_COMPILE);
                drawAllVisibleNodes(gl);
                gl.glEndList();
            }

            updateNodesDisplayList = false;
        }

        if (!TEMPORARILY_DISABLE_ALL_GRAPH_RENDERING)
        {
            if ( !DISABLE_NODES_RENDERING.get() && (SHOW_NODES.get() || !isInMotion) )
            {
                if (!animationRender)
                    gl.glCallList(nodesDisplayList);
                else
                    drawAllVisibleNodes(gl);
            }
        }

        if (updateSelectedNodesDisplayList)
        {
            if (DEBUG_BUILD) println("Updating selected nodes display list");

            // if ( gl.glIsList(nodeList) ) // always delete display list, an attempt to delete a list that has never been created is ignored
            gl.glDeleteLists(selectedNodesDisplayList, 1);

            if ( !DISABLE_NODES_RENDERING.get() )
            {
                gl.glNewList(selectedNodesDisplayList, GL_COMPILE);
                drawAllSelectedNodes(gl);
                gl.glEndList();
            }

            updateSelectedNodesDisplayList = false;
        }

        if (!TEMPORARILY_DISABLE_ALL_GRAPH_RENDERING)
        {
            if (isDraggingNodes) // no actual need for push-pop tricks here as it's the last thing to be rendered
                gl.glTranslatef(translateXSelectedValue, translateYSelectedValue, 0.0f);
            if ( ( !DISABLE_NODES_RENDERING.get() && (SHOW_NODES.get() || !isInMotion) ) && !renderProfileMode )
                gl.glCallList(selectedNodesDisplayList);
        }

        if (isShiftDown && !isDraggingNodes) drawSelectBox(gl);
    } // End of buildAllDisplayListsAndRenderScene2D

    /**
    *  Builds all 2D shapes display lists.
    */
    private void buildAllShapes2DDisplayLists(GL2 gl)
    {
        Texture nodeTexture = null;
        // FloatBuffer allTex2DCoordsBuffer = null;
        // FloatBuffer allVertex2DCoordsBuffer = null;
        FloatBuffer interleavedArrayCoordsBuffer = null;
        // Buffer indices = null;

        if (USE_VERTEX_ARRAYS_FOR_OPENGL_RENDERER)
        {
            // indices = Buffers.newDirectByteBuffer(4).put( new byte[] { 0, 1, 2, 3 } ).rewind();
            // allTex2DCoordsBuffer = Buffers.newDirectFloatBuffer(8);
            // allVertex2DCoordsBuffer = Buffers.newDirectFloatBuffer(8);
            interleavedArrayCoordsBuffer = Buffers.newDirectFloatBuffer(2 * 8 + 4); // add 4 dummy values for GL_T2F_V3F V3F part
        }

        int shapeIndex = CIRCLE.ordinal();
        for ( Shapes2D shape2D : Shapes2D.values() )
        {
            shapeIndex = shape2D.ordinal();
            nodeTexture = texturesLoader.getTexture( getTextureFromNode2DShape(shape2D) );

            TextureCoords tc = nodeTexture.getImageTexCoords();
            float tx1 = tc.left();
            float ty1 = tc.top();
            float tx2 = tc.right();
            float ty2 = tc.bottom();

            float nodeWidth = nodeTexture.getImageWidth();
            float nodeHeight = nodeTexture.getImageHeight();

            // if ( gl.glIsList(ALL_SHAPES_2D_DISPLAY_LISTS[shape]) ) // always delete display list, an attempt to delete a list that has never been created is ignored
            gl.glDeleteLists(ALL_SHAPES_2D_DISPLAY_LISTS[shapeIndex], 1);
            gl.glNewList(ALL_SHAPES_2D_DISPLAY_LISTS[shapeIndex], GL_COMPILE);

            if (USE_VERTEX_ARRAYS_FOR_OPENGL_RENDERER)
            {
                // Using Vertex Arrays for passing the data to OpenGL
                // allTex2DCoordsBuffer.put( new float[] { tx1, ty2, tx2, ty2, tx2, ty1, tx1, ty1 } ).rewind();
                // allVertex2DCoordsBuffer.put( new float[] { 0, nodeHeight, nodeWidth, nodeHeight, nodeWidth, 0, 0, 0 } ).rewind();
                interleavedArrayCoordsBuffer.put( new float[] { tx1, ty2, 0,         nodeHeight, 0,
                                                                tx2, ty2, nodeWidth, nodeHeight, 0,
                                                                tx2, ty1, nodeWidth, 0,          0,
                                                                tx1, ty1, 0,         0,          0 } ).rewind();

                // gl.glTexCoordPointer(2, GL_FLOAT, 0, allTex2DCoordsBuffer);
                // gl.glVertexPointer(2, GL_FLOAT, 0, allVertex2DCoordsBuffer);
                gl.glInterleavedArrays(GL_T2F_V3F, 0, interleavedArrayCoordsBuffer);
                gl.glDrawArrays(GL_QUADS, 0, 4);
                // gl.glDrawElements(GL_QUADS, 4, GL_UNSIGNED_BYTE, indices);
            }
            else
            {
                gl.glBegin(GL_QUADS);
                gl.glTexCoord2f(tx1, ty2); gl.glVertex2f(0.0f,      nodeHeight); // Bottom Left Of The Texture and Quad
                gl.glTexCoord2f(tx2, ty2); gl.glVertex2f(nodeWidth, nodeHeight); // Bottom Right Of The Texture and Quad
                gl.glTexCoord2f(tx2, ty1); gl.glVertex2f(nodeWidth, 0.0f);       // Top Right Of The Texture and Quad
                gl.glTexCoord2f(tx1, ty1); gl.glVertex2f(0.0f,      0.0f);       // Top Left Of The Texture and Quad
                gl.glEnd();
             }

            // manually done above to use all available OpenGL optimization tricks
            // drawTexture(gl, nodeTexture, 0, 0);

            gl.glEndList();
        }
    }

    /**
    *  Draws all visible edges nodes.
    */
    private IntBuffer drawAllVisibleEdges(GL2 gl, IntBuffer edgesDisplayLists, HashSet<GraphEdge> edgesToRender, boolean isNodeDragMode)
    {
        if (DEBUG_BUILD) println("GraphRenderer2D drawAllVisibleEdges()");

        boolean useProportionalEdgesSizeToWeightRendering = WEIGHTED_EDGES && PROPORTIONAL_EDGES_SIZE_TO_WEIGHT.get();
        Texture nodeTexture = null;
        float lineWidth = DEFAULT_EDGE_SIZE.get();
        GraphNode node1 = null;
        GraphNode node2 = null;
        double x1 = 0.0;
        double y1 = 0.0;
        double x2 = 0.0;
        double y2 = 0.0;
        double lineTheta1 = 0.0;
        double lineTheta2 = 0.0;
        Color color = null;
        double nodeScaleValue = 0.0;
        double nodeTextureWidthCenter = 0.0;
        double nodeTextureHeightCenter = 0.0;
        GraphmlNetworkContainer gnc = nc.getGraphmlNetworkContainer();
	// Only need the following two ratios if dealing with graph markup language
        float ratioX = ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() ) ? (float)width  / gnc.getRangeX() : 0.0f;
        float ratioY = ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() ) ? (float)height / gnc.getRangeY() : 0.0f;
        float extraSizeAmoutValueForYEdStyleRendering = ( 1.0f / ( (ratioX > ratioY) ? ratioX : ratioY ) );
        float[] currentNode1GraphmlMapCoord = null;
        float[] currentNode2GraphmlMapCoord = null;
        ArrayList<Point2D.Double> allPoints = null;
        int previousToLastIndex = 0;

        if (DEBUG_BUILD) println("GraphRenderer2D visibleEdges size: " + visibleEdges.size());

        // create more display lists if arrow heads are on
        int edgesPerDisplayListFor2DMode = ( DIRECTIONAL_EDGES.get() ) ? EDGES_PER_DISPLAY_LIST / 2 : EDGES_PER_DISPLAY_LIST;
        int howManyDisplayListsToCreate = edgesToRender.size() / edgesPerDisplayListFor2DMode;
        // add one extra display list if not perfect integer division (most of the cases) and not let it be zero
        if ( ( (edgesToRender.size() % edgesPerDisplayListFor2DMode) != 0 ) || (howManyDisplayListsToCreate == 0) )
            howManyDisplayListsToCreate++;

        if ( (allEdgesDisplayLists == null) || (howManyDisplayListsToCreate != ( (!isNodeDragMode) ? prevHowManyDisplayListsToCreate : prevHowManyDisplayListsToCreateForNodeDragging) ) )
        {
            if (edgesDisplayLists != null)
                edgesDisplayLists.clear();

            // allocate new edge display lists
            edgesDisplayLists = Buffers.newDirectIntBuffer(howManyDisplayListsToCreate);
            for (int i = 0; i < howManyDisplayListsToCreate; i++)
                edgesDisplayLists.put( gl.glGenLists(1) );
            edgesDisplayLists.rewind();
        }

        if (!isNodeDragMode)
            prevHowManyDisplayListsToCreate = howManyDisplayListsToCreate;
        else
            prevHowManyDisplayListsToCreateForNodeDragging = howManyDisplayListsToCreate;

        int edgeIndex = 0;
        int displayListIndex = 0;
        gl.glNewList(edgesDisplayLists.get(displayListIndex), GL_COMPILE);
        // for line antialiasing and blending options usage only
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        if (!useProportionalEdgesSizeToWeightRendering)
        {
            gl.glLineWidth(lineWidth);
            gl.glBegin(GL_LINES);
        }

	// for each edge in the list of edges to render...
        for (GraphEdge edge : edgesToRender)
        {
	    // If adding one to the edge index hits end of edges for display list
            if ( (++edgeIndex % edgesPerDisplayListFor2DMode) == 0 )
            {
		// Stop adding items to the list and...
                gl.glEndList();
		// then make a new list with the next displayListIndex.
                gl.glNewList(edgesDisplayLists.get(++displayListIndex), GL_COMPILE);
            }

            if (useProportionalEdgesSizeToWeightRendering)
                lineWidth = DEFAULT_EDGE_SIZE.get() * edge.getScaledWeight();

            node1 = edge.getNodeFirst();
            node2 = edge.getNodeSecond();

            if ( !node1.equals(node2) ) // don't need to draw an arrow to itself (for now)
            {
                if ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() )
                {
                    currentNode1GraphmlMapCoord = gnc.getAllGraphmlNodesMap().get( node1.getNodeName() ).first;
                    currentNode2GraphmlMapCoord = gnc.getAllGraphmlNodesMap().get( node2.getNodeName() ).first;
                    x1 = currentNode1GraphmlMapCoord[2];
                    y1 = currentNode1GraphmlMapCoord[3];
                    x2 = currentNode2GraphmlMapCoord[2];
                    y2 = currentNode2GraphmlMapCoord[3];
                }
                else
                {
		    // So long as you're not dealing with graphml Get the x and y coordinates of the nodes.
                    x1 = node1.getX();
                    y1 = node1.getY();
                    x2 = node2.getX();
                    y2 = node2.getY();
                }

                color = ( WEIGHTED_EDGES && COLOR_EDGES_BY_WEIGHT.get() ) ? edge.getColor() : DEFAULT_EDGE_COLOR.get();
		//allPoints was initialised as null, so now we put in the two 2D points.
                allPoints = new ArrayList<Point2D.Double>(2); // default capacity of 2
                allPoints.add( new Point.Double(x1, y1) );
                if ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() )
                    addPolylines(gnc, node1, node2, allPoints);
                allPoints.add( new Point.Double(x2, y2) );

                previousToLastIndex = allPoints.size() - 1;
                // lastY - nextToLastY, lastX - nextToLastX
                lineTheta1 = atan2(allPoints.get(previousToLastIndex).y - allPoints.get(previousToLastIndex - 1).y,
                                   allPoints.get(previousToLastIndex).x - allPoints.get(previousToLastIndex - 1).x);

                nodeScaleValue = node2.getNodeSize() / NODE_SIZE_DIVIDE_RATIO;
                if ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() )
                    nodeScaleValue *= extraSizeAmoutValueForYEdStyleRendering;
                nodeTexture = texturesLoader.getTexture( getTextureFromNode2DShape( choose2DShape( node2.getNode2DShape() ) ) );
                nodeTextureWidthCenter  = nodeTexture.getImageWidth()  / 2.0;
                nodeTextureHeightCenter = nodeTexture.getImageHeight() / 2.0;

                allPoints.get(previousToLastIndex).x -= ( nodeScaleValue * nodeTextureWidthCenter  * cos(lineTheta1) );
                allPoints.get(previousToLastIndex).y -= ( nodeScaleValue * nodeTextureHeightCenter * sin(lineTheta1) );

                // firstY - secondY, firstX - secondY
                lineTheta2 = atan2(allPoints.get(0).y - allPoints.get(1).y,
                                   allPoints.get(0).x - allPoints.get(1).x);

                nodeScaleValue = node1.getNodeSize() / NODE_SIZE_DIVIDE_RATIO;
                if ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() )
                    nodeScaleValue *= extraSizeAmoutValueForYEdStyleRendering;
                nodeTexture = texturesLoader.getTexture( getTextureFromNode2DShape( choose2DShape( node1.getNode2DShape() ) ) );
                nodeTextureWidthCenter  = nodeTexture.getImageWidth()  / 2.0;
                nodeTextureHeightCenter = nodeTexture.getImageHeight() / 2.0;

                allPoints.get(0).x -= ( nodeScaleValue * nodeTextureWidthCenter  * cos(lineTheta2) );
                allPoints.get(0).y -= ( nodeScaleValue * nodeTextureHeightCenter * sin(lineTheta2) );

                drawLines(gl, allPoints, lineWidth, color, useProportionalEdgesSizeToWeightRendering);

                if ( DIRECTIONAL_EDGES.get() )
                    drawArrowHeads(gl, allPoints.get(previousToLastIndex).x, allPoints.get(previousToLastIndex).y, lineWidth, color, lineTheta1, useProportionalEdgesSizeToWeightRendering);

                if ( edge.hasDualArrowHead() )
                    drawArrowHeads(gl, allPoints.get(0).x,                   allPoints.get(0).y,                   lineWidth, color, lineTheta2, useProportionalEdgesSizeToWeightRendering);
            }
        }

        if (!useProportionalEdgesSizeToWeightRendering) gl.glEnd();

        boolean defineOnce = false;
        for (GraphEdge edge : edgesToRender)
        {
            if ( edge.isShowEdgeName() )
            {
                node1 = edge.getNodeFirst();
                node2 = edge.getNodeSecond();

                if ( !node1.equals(node2) ) // don't need to draw an arrow to itself (for now)
                {
                    if (!defineOnce)
                    {
                        defineOnce = true;

                        gl.glEnable(GL_COLOR_LOGIC_OP);
                        gl.glLogicOp(GL_EQUIV);

                        // Enable blending, using the SrcOver rule
                        gl.glEnable(GL_BLEND);
                        gl.glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

                        // determine which areas of the polygon are to be renderered
                        gl.glEnable(GL_ALPHA_TEST);
                        gl.glAlphaFunc(GL_GREATER, 0); // only render if alpha > 0
                    }

                    if ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() )
                    {
                        currentNode1GraphmlMapCoord = gnc.getAllGraphmlNodesMap().get( node1.getNodeName() ).first;
                        currentNode2GraphmlMapCoord = gnc.getAllGraphmlNodesMap().get( node2.getNodeName() ).first;
                        x1 = currentNode1GraphmlMapCoord[2];
                        y1 = currentNode1GraphmlMapCoord[3];
                        x2 = currentNode2GraphmlMapCoord[2];
                        y2 = currentNode2GraphmlMapCoord[3];
                    }
                    else
                    {
                        x1 = node1.getX();
                        y1 = node1.getY();
                        x2 = node2.getX();
                        y2 = node2.getY();
                    }

                    color = ( WEIGHTED_EDGES && COLOR_EDGES_BY_WEIGHT.get() ) ? edge.getColor() : DEFAULT_EDGE_COLOR.get();

                    if ( nc.getIsGraphml() )
                    {
                        if (edge.getEdgeName() != null)
                        {
                            gl.glColor3f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);
                            gl.glRasterPos2d(x1 - (x1 - x2) / 2.0, y1 - (y1 - y2) / 2.0 - 0.75);
                            GLUT.glutBitmapString( EDGE_NAMES_OPENGL_FONT_TYPE, edge.getEdgeName() );
                        }
                    }
                    else
                    {
                        gl.glColor3f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);
                        gl.glRasterPos2d(x1 - (x1 - x2) / 2.0, y1 - (y1 - y2) / 2.0 - 0.75);
                        GLUT.glutBitmapString( EDGE_NAMES_OPENGL_FONT_TYPE, NUMBER_FORMAT.format( edge.getWeight() ) );
                    }
                }
            }
        }

        if (defineOnce)
        {
            gl.glDisable(GL_ALPHA_TEST);
            gl.glDisable(GL_BLEND);

            gl.glDisable(GL_COLOR_LOGIC_OP);
        }

        gl.glDisable(GL_BLEND);
        gl.glEndList();

        return edgesDisplayLists;
    }

    /**
    *  Draws an arrowhead.
    */
    private void drawArrowHeads(GL2 gl, double x3, double y3, float lineWidth, Color color, double lineTheta, boolean useIndividualGLCommands)
    {
        // upper line flap
        double xArrow1 = x3 - ( 0.5 * ARROW_HEAD_SIZE.get() * cos(ARROW_HEAD_THETA  + lineTheta) );
        double yArrow1 = y3 - ( 0.5 * ARROW_HEAD_SIZE.get() * sin(ARROW_HEAD_THETA  + lineTheta) );
        drawLine(gl, x3, y3, xArrow1, yArrow1, lineWidth, color, useIndividualGLCommands);

        // lower line flap
        double xArrow2 = x3 - ( 0.5 * ARROW_HEAD_SIZE.get() * cos(ARROW_HEAD_THETA  - lineTheta) );
        double yArrow2 = y3 + ( 0.5 * ARROW_HEAD_SIZE.get() * sin(ARROW_HEAD_THETA  - lineTheta) );
        drawLine(gl, x3, y3, xArrow2, yArrow2, lineWidth, color, useIndividualGLCommands);
    }

    /**
    *  Adds all polylines points (if found) to the ArrayList<Point2D.Double> collection.
    */
    private ArrayList<Point2D.Double> addPolylines(GraphmlNetworkContainer gnc, GraphNode node1, GraphNode node2, ArrayList<Point2D.Double> allPoints)
    {
        Tuple6<String, Tuple2<float[], ArrayList<Point2D.Float>>, String[], String[], String[], String[]> edgeTuple6 = gnc.getAllGraphmlEdgesMap().get( node1.getNodeName() + " " + node2.getNodeName() );
        if (edgeTuple6 != null)
        {
            Point.Double point = null;
            for (Point2D.Float polylinePoint2D : edgeTuple6.second.second)
            {
                point = new Point.Double(polylinePoint2D.x, polylinePoint2D.y);
                // end of line
                allPoints.add(point);
                // beginning of next line
                allPoints.add(point);
            }
        }

        return allPoints;
    }

    /**
    *  Draws all visible nodes. Uses a texture non-binding-if-not-necessary optimization technique.
    */
    private void drawAllVisibleNodes(GL2 gl)
    {
        if (DEBUG_BUILD) println("GraphRenderer2D drawAllVisibleNodes()");

        Texture prevNodeTexture = null;
        Texture currentNodeTexture = null;
        Color nodeColor = null;
        float nodeScaleValue = 0.0f;
        float nodeTextureWidthCenter = 0.0f;
        float nodeTextureHeightCenter = 0.0f;
        float coordX = 0.0f;
        float coordY = 0.0f;
        GraphmlNetworkContainer gnc = nc.getGraphmlNetworkContainer();
        float ratioX = ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() ) ? (float)width  / gnc.getRangeX() : 0.0f;
        float ratioY = ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() ) ? (float)height / gnc.getRangeY() : 0.0f;
        float extraSizeAmoutValueForYEdStyleRendering = ( 1.0f / ( (ratioX > ratioY) ? ratioX : ratioY ) );

        // animation render related values
        Tuple6<Float, Color, Boolean, Float, Boolean, Float> tuple6 = null;
        boolean useShaderAnimationGPUComputing = false;

        // Enable blending, using the SrcOver rule
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        // determine which areas of the polygon are to be renderered
        gl.glEnable(GL_ALPHA_TEST);
        gl.glAlphaFunc(GL_GREATER, 0); // only render if alpha > 0

        // Use the GL_MODULATE texture function to effectively multiply
        // each pixel in the texture by the current alpha value
        gl.glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);

        if (DEBUG_BUILD) println("GraphRenderer2D visibleNodes size: " + visibleNodes.size());
        for (GraphNode node : visibleNodes)
        {
            currentNodeTexture = texturesLoader.getTexture( getTextureFromNode2DShape( choose2DShape( node.getNode2DShape() ) ) );
            nodeScaleValue = node.getNodeSize() / NODE_SIZE_DIVIDE_RATIO;

            nodeTextureWidthCenter  = currentNodeTexture.getImageWidth() / 2.0f;
            nodeTextureHeightCenter = currentNodeTexture.getImageHeight() / 2.0f;

            if ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() )
                nodeScaleValue *= extraSizeAmoutValueForYEdStyleRendering;

            nodeColor = node.getColor();

            gl.glLoadName( node.getNodeID() );

            if (prevNodeTexture != currentNodeTexture)
            {
                if (DEBUG_BUILD) println("Texture change for visible node with nodeID: " + node.getNodeID());

                if (prevNodeTexture != null) prevNodeTexture.disable(gl);

                currentNodeTexture.bind(gl);
                currentNodeTexture.enable(gl);
            }

            if ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() )
            {
                float[] currentNodeGraphmlMapCoord = gnc.getAllGraphmlNodesMap().get( node.getNodeName() ).first;
                coordX = currentNodeGraphmlMapCoord[2];
                coordY = currentNodeGraphmlMapCoord[3];
            }
            else
            {
                coordX = node.getX();
                coordY = node.getY();
            }

            if (animationRender)
            {
                if ( ( ( ANIMATION_SELECTED_NODES_ANIMATION_ONLY.get() ) ? ( ( !selectionManager.getSelectedNodes().isEmpty() ) ? selectionManager.getSelectedNodes().contains(node) : true ) : true ) )
                {
                    if ( !node.ismEPNTransition() )
                    {
                        if ( ( !DATA_TYPE.equals(DataTypes.EXPRESSION) && ANIMATION_MEPN_COMPONENTS_ANIMATION_ONLY.get() ) ? node.ismEPNComponent() : true)
                        {
                            tuple6 = AnimationVisualization.performAnimationVisualization(false, node.getNodeID(), node.getNodeName(), layoutFrame.isAllShadingSFXSValueEnabled(), nodeColor, currentTick, animationFrameCount, animationSpectrumImage, DATA_TYPE.equals(DataTypes.EXPRESSION));
                            nodeScaleValue = tuple6.first;
                            nodeColor = tuple6.second;
                            useShaderAnimationGPUComputing = tuple6.third;
                        }
                    }
                }
            }

            float r = nodeColor.getRed()   / 255.0f;
            float g = nodeColor.getGreen() / 255.0f;
            float b = nodeColor.getBlue()  / 255.0f;
            gl.glColor4f(r, g, b, (TRANSPARENT.get() ) ? node.getTransparencyAlpha() : 1.0f);

            // hierarchical display lists code
            gl.glPushMatrix();
            // center all the transformations to image's center
            gl.glTranslatef(coordX - nodeTextureWidthCenter + currentNodeTexture.getImageWidth() / 2.0f, coordY - nodeTextureHeightCenter + currentNodeTexture.getImageHeight() / 2.0f, 0.0f);
            gl.glScalef(nodeScaleValue, nodeScaleValue, 0.0f);

            if (!useShaderAnimationGPUComputing)
            {
                // reset centered transformation, GPU Computing does not need it as it does it within the GLSL shader code
                gl.glTranslatef(-(currentNodeTexture.getImageWidth() / 2.0f), -(currentNodeTexture.getImageHeight() / 2.0f), 0.0f);
            }
            else
                shaderTextureSFXs.useShaderAnimationGPUComputing(gl, (TRANSPARENT.get() ) ? node.getTransparencyAlpha() : 1.0f, MATERIAL_OLD_LCD_STYLE_TRANSPARENCY_SHADING.get(), currentNodeTexture.getImageWidth(), currentNodeTexture.getImageHeight(), tuple6.fourth, tuple6.fifth, tuple6.sixth, animationFrameCount);

            gl.glCallList(ALL_SHAPES_2D_DISPLAY_LISTS[choose2DShape( node.getNode2DShape() ).ordinal()]);

            if (animationRender)
            {
                if (useShaderAnimationGPUComputing)
                {
                    useShaderAnimationGPUComputing = false;
                    shaderTextureSFXs.disableShaderAnimationGPUComputing(gl);
                }
            }

            gl.glPopMatrix();

            // old-fashioned rendering
            // drawRotoZoomTexture(gl, currentNodeTexture, coordX - nodeTextureWidthCenter, coordY - nodeTextureHeightCenter, 0.0, nodeScaleValue, nodeScaleValue, ( (TRANSPARENT.get() ) ? node.getTransparencyAlpha() : 1.0f), nodeColor, false);

            prevNodeTexture = currentNodeTexture;
        }

        if (prevNodeTexture != null) prevNodeTexture.disable(gl);

        String nodeName = "";
        boolean isSelectedNodesAnimation = false;
        boolean defineOnce = false;
        for (GraphNode node : visibleNodes)
        {
            if (!animationRender)
            {
                if ( node.isShowNodeName() )
                {
                    if (!defineOnce)
                    {
                        defineOnce = true;

                        if (CUSTOMIZE_NODE_NAMES_NAME_RENDERING_TYPE.get() == 0)
                        {
                            gl.glEnable(GL_COLOR_LOGIC_OP);
                            gl.glLogicOp(GL_EQUIV);
                        }

                        // Enable blending, using the SrcOver rule
                        gl.glEnable(GL_BLEND);
                        gl.glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

                        // determine which areas of the polygon are to be renderered
                        gl.glEnable(GL_ALPHA_TEST);
                        gl.glAlphaFunc(GL_GREATER, 0); // only render if alpha > 0
                    }

                    if ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() )
                    {
                        float[] currentNodeGraphmlMapCoord = gnc.getAllGraphmlNodesMap().get( node.getNodeName() ).first;
                        coordX = currentNodeGraphmlMapCoord[2];
                        coordY = currentNodeGraphmlMapCoord[3];
                    }
                    else
                    {
                        coordX = node.getX();
                        coordY = node.getY();
                    }

                    gl.glColor3f(Color.BLACK.getRed() / 255.0f, Color.BLACK.getGreen() / 255.0f, Color.BLACK.getBlue() / 255.0f);
                    gl.glRasterPos2d(coordX, coordY);
                    nodeName = Graph.customizeNodeName( nc.getNodeName( node.getNodeName() ) );
                    if (CUSTOMIZE_NODE_NAMES_NAME_RENDERING_TYPE.get() != 0)
                        graph.drawNodeNameBackgroundLegend(gl, node, nodeName);
                    GLUT.glutBitmapString(NODE_NAMES_OPENGL_FONT_TYPE.ordinal() + 2, nodeName); // + 2 for GLUT public static variables ordering for excluding STROKE_ROMAN/STROKE_MONO_ROMAN
                }
            }
            else
            {
                isSelectedNodesAnimation = ( ( ANIMATION_SELECTED_NODES_ANIMATION_ONLY.get() ) ? ( ( !selectionManager.getSelectedNodes().isEmpty() ) ? selectionManager.getSelectedNodes().contains(node) : true ) : true );
                if ( node.isShowNodeName() || (!node.ismEPNTransition() && isSelectedNodesAnimation) )
                {
                    if (!defineOnce)
                    {
                        defineOnce = true;

                        if (CUSTOMIZE_NODE_NAMES_NAME_RENDERING_TYPE.get() == 0)
                        {
                            gl.glEnable(GL_COLOR_LOGIC_OP);
                            gl.glLogicOp(GL_EQUIV);
                        }

                        // Enable blending, using the SrcOver rule
                        gl.glEnable(GL_BLEND);
                        gl.glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

                        // determine which areas of the polygon are to be renderered
                        gl.glEnable(GL_ALPHA_TEST);
                        gl.glAlphaFunc(GL_GREATER, 0); // only render if alpha > 0
                    }

                    if ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() )
                    {
                        float[] currentNodeGraphmlMapCoord = gnc.getAllGraphmlNodesMap().get( node.getNodeName() ).first;
                        coordX = currentNodeGraphmlMapCoord[2];
                        coordY = currentNodeGraphmlMapCoord[3];
                    }
                    else
                    {
                        coordX = node.getX();
                        coordY = node.getY();
                    }

                    gl.glColor3f(Color.BLACK.getRed() / 255.0f, Color.BLACK.getGreen() / 255.0f, Color.BLACK.getBlue() / 255.0f);
                    gl.glRasterPos2d(coordX, coordY);
                    nodeName = Graph.customizeNodeName( nc.getNodeName( node.getNodeName() ) );
                    // if-else commands below have to be like this to be able to properly selectively render names
                    if ( node.isShowNodeName() && !ANIMATION_SHOW_NODE_ANIMATION_VALUE.get() )
                    {
                        if (CUSTOMIZE_NODE_NAMES_NAME_RENDERING_TYPE.get() != 0)
                            graph.drawNodeNameBackgroundLegend(gl, node, nodeName);
                        GLUT.glutBitmapString(NODE_NAMES_OPENGL_FONT_TYPE.ordinal() + 2, nodeName); // + 2 for GLUT public static variables ordering for excluding STROKE_ROMAN/STROKE_MONO_ROMAN
                    }
                    else if ( !node.ismEPNTransition() && isSelectedNodesAnimation && ANIMATION_SHOW_NODE_ANIMATION_VALUE.get() )
                    {
                        if ( !nodeName.isEmpty() )
                            nodeName += ": ";
                        nodeName += NUMBER_FORMAT.format( AnimationVisualization.getAnimationVisualizationNodeValue(node.getNodeID(), node.getNodeName(), currentTick, animationFrameCount, DATA_TYPE.equals(DataTypes.EXPRESSION)) );
                        if (CUSTOMIZE_NODE_NAMES_NAME_RENDERING_TYPE.get() != 0)
                            graph.drawNodeNameBackgroundLegend(gl, node, nodeName);
                        GLUT.glutBitmapString(NODE_NAMES_OPENGL_FONT_TYPE.ordinal() + 2, nodeName); // + 2 for GLUT public static variables ordering for excluding STROKE_ROMAN/STROKE_MONO_ROMAN
                    }
                }
            }
        }

        if (defineOnce)
        {
            gl.glDisable(GL_ALPHA_TEST);
            gl.glDisable(GL_BLEND);
            if (CUSTOMIZE_NODE_NAMES_NAME_RENDERING_TYPE.get() == 0)
               gl.glDisable(GL_COLOR_LOGIC_OP);
        }

        gl.glDisable(GL_ALPHA_TEST);
        gl.glDisable(GL_BLEND);
    }

    /**
    *  Draws all selected nodes. Uses a texture non-binding-if-not-necessary optimization technique.
    */
    private void drawAllSelectedNodes(GL2 gl)
    {
        if (DEBUG_BUILD) println("GraphRenderer2D drawAllSelectedNodes()");

        Texture prevNodeTexture = null;
        Texture currentNodeTexture = null;
        float nodeScaleValue = 0.0f;
        float nodeTextureWidthCenter = 0.0f;
        float nodeTextureHeightCenter = 0.0f;
        float coordX = 0.0f;
        float coordY = 0.0f;
        GraphmlNetworkContainer gnc = nc.getGraphmlNetworkContainer();
        float ratioX = ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() ) ? (float)width  / gnc.getRangeX() : 0.0f;
        float ratioY = ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() ) ? (float)height / gnc.getRangeY() : 0.0f;
        float extraSizeAmoutValueForYEdStyleRendering = ( 1.0f / ( (ratioX > ratioY) ? ratioX : ratioY ) );

        // Enable blending, using the SrcOver rule
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        // determine which areas of the polygon are to be renderered
        gl.glEnable(GL_ALPHA_TEST);
        gl.glAlphaFunc(GL_GREATER, 0); // only render if alpha > 0

        // Use the GL_MODULATE texture function to effectively multiply
        // each pixel in the texture by the current alpha value
        gl.glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);

        HashSet<GraphNode> selectedNodes = selectionManager.getSelectedNodes();
        if (DEBUG_BUILD) println("GraphRenderer2D selectedNodes size: " + selectedNodes.size());
        for (GraphNode node : selectedNodes)
        {
            currentNodeTexture = texturesLoader.getTexture( getTextureFromNode2DShape( choose2DShape( node.getNode2DShape() ) ) );
            nodeScaleValue = node.getNodeSize() / NODE_SIZE_DIVIDE_RATIO;

            nodeTextureWidthCenter  = currentNodeTexture.getImageWidth() / 2.0f;
            nodeTextureHeightCenter = currentNodeTexture.getImageHeight() / 2.0f;

            if ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() )
                nodeScaleValue *= extraSizeAmoutValueForYEdStyleRendering;

            if (prevNodeTexture != currentNodeTexture)
            {
                if (DEBUG_BUILD) println("Texture change for selected node with nodeID: " + node.getNodeID());

                if (prevNodeTexture != null) prevNodeTexture.disable(gl);

                currentNodeTexture.bind(gl);
                currentNodeTexture.enable(gl);
            }

            if ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() )
            {
                float[] currentNodeGraphmlMapCoord = gnc.getAllGraphmlNodesMap().get( node.getNodeName() ).first;
                coordX = currentNodeGraphmlMapCoord[2];
                coordY = currentNodeGraphmlMapCoord[3];
            }
            else
            {
                coordX = node.getX();
                coordY = node.getY();
            }

            // add an nodeScaleValue offset for emulating a 2D 'shadow' transparency effect

            float r = SELECTION_COLOR.get().getRed()   / 255.0f;
            float g = SELECTION_COLOR.get().getGreen() / 255.0f;
            float b = SELECTION_COLOR.get().getBlue()  / 255.0f;
            gl.glColor4f(r, g, b, 0.15f);

            // hierarchical display lists code
            gl.glPushMatrix();
            // center all the transformations to image's center
            gl.glTranslatef(coordX - nodeTextureWidthCenter + nodeScaleValue + currentNodeTexture.getImageWidth() / 2.0f, coordY - nodeTextureHeightCenter - nodeScaleValue + currentNodeTexture.getImageHeight() / 2.0f, 0.0f);
            gl.glScalef(nodeScaleValue, nodeScaleValue, 0.0f);
            // reset centered transformation
            gl.glTranslatef(-(currentNodeTexture.getImageWidth() / 2.0f), -(currentNodeTexture.getImageHeight() / 2.0f), 0.0f);

            gl.glCallList(ALL_SHAPES_2D_DISPLAY_LISTS[choose2DShape( node.getNode2DShape() ).ordinal()]);

            gl.glPopMatrix();

            // old-fashioned rendering
            // drawRotoZoomTexture(gl, currentNodeTexture, coordX - nodeTextureWidthCenter + nodeScaleValue, coordY - nodeTextureHeightCenter - nodeScaleValue, 0.0, nodeScaleValue, nodeScaleValue, 0.15f, SELECTION_COLOR.get(), false);

            prevNodeTexture = currentNodeTexture;
        }

        if (prevNodeTexture != null) prevNodeTexture.disable(gl);

        gl.glDisable(GL_ALPHA_TEST);
        gl.glDisable(GL_BLEND);
    }

    /**
    *  Draws the pathway component containers in 3D mode.
    */
    private void drawPathwayComponentContainers2DMode(GL2 gl)
    {
        Rectangle2D.Float rectangle2D = null;
        gl.glBegin(GL_QUADS);
        for ( GraphmlComponentContainer pathwayComponentContainer : nc.getGraphmlNetworkContainer().getAllPathwayComponentContainersFor2D() )
        {
            float r = pathwayComponentContainer.color.getRed()   / 255.0f;
            float g = pathwayComponentContainer.color.getGreen() / 255.0f;
            float b = pathwayComponentContainer.color.getBlue()  / 255.0f;
            gl.glColor4f(r, g, b, 1.0f);
            rectangle2D = pathwayComponentContainer.rectangle2D;
            gl.glVertex2f(rectangle2D.x - rectangle2D.width / 2.0f, rectangle2D.y - rectangle2D.height / 2.0f); // Bottom Left Of The Quad
            gl.glVertex2f(rectangle2D.x + rectangle2D.width / 2.0f, rectangle2D.y - rectangle2D.height / 2.0f); // Bottom Right Of The Quad
            gl.glVertex2f(rectangle2D.x + rectangle2D.width / 2.0f, rectangle2D.y + rectangle2D.height / 2.0f); // Top Right Of The Quad
            gl.glVertex2f(rectangle2D.x - rectangle2D.width / 2.0f, rectangle2D.y + rectangle2D.height / 2.0f); // Top Left Of The Quad
        }
        gl.glEnd();
    }

    /**
    *  Draws the selection box.
    */
    private void drawSelectBox(GL2 gl)
    {
        if (DEBUG_BUILD) println("GraphRenderer2D drawSelectBox()");

        gl.glLoadIdentity(); // select box mode should always not be displaced/transformed in OpenGL canvas, just on its original mouse coords, so loading the identity matrix
        gl.glEnable(GL_COLOR_LOGIC_OP);
        gl.glLogicOp(GL_XOR);

        gl.glPushMatrix();
        gl.glTranslatef(-1.0f, 1.0f, 0.0f); // Translate to Java2D coords
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glLineWidth(1.0f);
        gl.glBegin(GL_LINES);

        // draw a rectangle as a selection box
        drawLine(gl, selectBoxStartX, selectBoxStartY, selectBoxStartX, selectBoxEndY, false);
        drawLine(gl, selectBoxStartX, selectBoxEndY, selectBoxEndX, selectBoxEndY, false);
        drawLine(gl, selectBoxEndX, selectBoxEndY, selectBoxEndX, selectBoxStartY, false);
        drawLine(gl, selectBoxEndX, selectBoxStartY, selectBoxStartX, selectBoxStartY, false);

        gl.glEnd();
        gl.glPopMatrix();

        gl.glDisable(GL_COLOR_LOGIC_OP);
    }

    /**
    *  Selects the 2D OpenGL scene.
    */
    private void selectScene(GL2 gl)
    {
        if (DEBUG_BUILD) println("GraphRenderer2D selectScene()");

        SELECTION_BUFFER.clear(); // Prepare buffer for reading

        // tell OpenGL to use our array for selection
        gl.glSelectBuffer(SELECTION_BUFFER.capacity(), SELECTION_BUFFER);

        // put openGL in GL_SELECT mode
        gl.glRenderMode(GL_SELECT);

        // initialize the name stack
        gl.glInitNames();

        // Now we're restricting drawing to just under the cursor.  We need the projection matrix
        // for this. Push it to the stack then reset the new matrix with load identity
        gl.glMatrixMode(GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();

        // set the VIEWPORT to the size and location of the screen
        gl.glGetIntegerv(GL_VIEWPORT, VIEWPORT, 0);

        // Now restrict drawing using gluPickMatrix().  The first parameter is our current
        // mouse position on the x-axis, the second is the current mouse y axis.  Then the width
        // and height of the picking region.  Finally the current VIEWPORT indicates the current boundries.
        // mouse_x and y are the center of the picking region.
        GLU.gluPickMatrix(pickOriginX, VIEWPORT[3] - pickOriginY, pickWidth, pickHeight, VIEWPORT, 0);

        // set projection (perspective or orthogonal) exactly as it is in normal renderering
        gl.glOrtho(0.0f, width, height, 0.0f, -1.0f, 1.0f);
        // same with:
        // GLU.gluOrtho2D(0, width, height, 0);

        // set the modelview to render our targets properly
        gl.glMatrixMode(GL_MODELVIEW);

        // push at least one entry on to the stack
        gl.glPushName(MAP_SELECTION_ID);

        // "render" the targets to our modelview
        gl.glCallList(nodesDisplayList);

        // pop the entry from the stack
        gl.glPopName();

        // restore original projection matrix
        gl.glMatrixMode(GL_PROJECTION);
        gl.glPopMatrix();
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glFlush();

        // find out how many objects we hit & back to GL_RENDER mode
        int hits = gl.glRenderMode(GL_RENDER);

        if (DEBUG_BUILD) println("GraphRenderer2D hits: " + hits);

        // next display() call will render normally
        selectMode = false;

        // visible result again
        if ( (!isShiftAltDown && !pickOneNode) || (mouseHasClicked && !isAltDown) )
            selectionManager.clearAllSelection();

        processHits(hits);
    }

    /**
    *  Processes the hits from selectScene().
    */
    private void processHits(int hits)
    {
        int numberNames = 0;
        int nameID = 0;
        int offset = 0;
        closestNode = null;

        HashSet<GraphNode> nodesToAdd = new HashSet<GraphNode>();
        for (int i = 0; i < hits; i++)
        {
            numberNames = SELECTION_BUFFER.get(offset++);
            offset += 2; // skip minZ/maxZ offets since we have a 2D plane

            for (int j = 0; j < numberNames; j++)
            {
                nameID = SELECTION_BUFFER.get(offset++);
                // for group nodes appearing on the graph, having a nodeID < 0
                GraphNode node = (nameID < 0) ? selectionManager.getGroupManager().getGroupNodebyID(nameID) : graphNodes.get(nameID); // Integer autoboxing

                if (!pickOneNode)
                {
                    if ( selectionManager.getSelectedNodes().contains(node) )
                    {
                        selectionManager.removeNodeFromSelected(node, false, false, false); // do not need to do any viewer updates here
                    }
                    else
                    {
                        nodesToAdd.add(node);
                    }
                }
                else
                {
                    closestNode = node;
                }
            }
        }

        if ( pickOneNode && (closestNode != null) )
        {
            if (mouseHasClicked)
            {
                nodesToAdd.add(closestNode);
                lastNodeURLStringPicked = closestNode.getURLString();

                if ( isAltDown && selectionManager.getSelectedNodes().isEmpty() )
                {
                    if (scaleValue == 0.0f)
                    {
                        if ( !( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() ) )
                        {
                            FOCUS_POSITION_2D.setLocation( closestNode.getX(), closestNode.getY() );
                        }
                        else
                        {
                            GraphmlNetworkContainer gnc = nc.getGraphmlNetworkContainer();
                            float[] currentNodeGraphmlMapCoord = gnc.getAllGraphmlNodesMap().get( closestNode.getNodeName() ).first;
                            FOCUS_POSITION_2D.setLocation(currentNodeGraphmlMapCoord[2], currentNodeGraphmlMapCoord[3]);
                        }
                    }
                }
            }

            lastNodeNamePicked = nc.getNodeName( closestNode.getNodeName() );

            if (DEBUG_BUILD) println("Found node name: " + lastNodeNamePicked + " with node id: "+ closestNode.getNodeID());
        }
        else
        {
            lastNodeNamePicked = "";
            lastNodeURLStringPicked = "";

            if (DEBUG_BUILD) println("No node name found!");
        }

        if (!pickOneNode || mouseHasClicked)
        {
            // check to deselect the same clicked node
            if (mouseHasClicked)
            {
                if ( selectionManager.getSelectedNodes().contains(closestNode) )
                    selectionManager.removeNodeFromSelected(closestNode);
                else
                    selectionManager.addNodesToSelected(nodesToAdd);
            }
            else
                selectionManager.addNodesToSelected(nodesToAdd);

            mouseHasClicked = false;
            updateSelectedNodesDisplayList = true;
        }

        if ( !nodesToAdd.isEmpty() )
            selectionManager.checkSetEnabledNodeNameTextFieldAndSelectNodesTab();
    }

    /**
    *  Initial starting point of the selection box.
    */
    private void selectBox(int startX, int startY, int endX, int endY)
    {
        selectBoxStartX = startX;
        selectBoxStartY = startY;
        selectBoxEndX = endX;
        selectBoxEndY = endY;
    }

    /**
    *  Resets the select box values.
    */
    private void resetSelectBoxValues()
    {
        selectBoxStartX = 0;
        selectBoxStartY = 0;
        selectBoxEndX = 0;
        selectBoxEndY = 0;
    }

    /**
    *  Updates all selected the nodes and edges.
    */
    private void updateVisibleNodesAndEdgesAccordingToSelection(HashSet<GraphNode> selectedNodes)
    {
        visibleNodes.removeAll(selectedNodes);

        tempVisibleEdges = new HashSet<GraphEdge>(visibleEdges);
        remainingEdges = new HashSet<GraphEdge>();

        for (GraphEdge graphEdge : visibleEdges)
            if ( (visibleNodes.contains( graphEdge.getNodeFirst() ) && visibleNodes.contains( graphEdge.getNodeSecond() ) ) ) // get rid of edges that do not connect from both sides
                remainingEdges.add(graphEdge);

        if ( SHOW_EDGES_WHEN_DRAGGING_NODES.get() )
        {
            draggedEdges = new HashSet<GraphEdge>(visibleEdges);
            draggedEdges.removeAll(remainingEdges);
            updateDraggedEdgesDisplayList = true;
        }

        visibleEdges = new HashSet<GraphEdge>(remainingEdges);
    }

    /**
    *  Updates the nodes positions.
    */
    private void updateSelectedNodesPositionsAndReAddToVisibleNodes(HashSet<GraphNode> selectedNodes)
    {
        if ( (translateXSelectedValue != 0.0f) || (translateYSelectedValue != 0.0f) )
        {
            if ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() )
            {
                GraphmlNetworkContainer gnc = nc.getGraphmlNetworkContainer();
                float[] currentNodeGraphmlMapCoord = null;
                for (GraphNode selectedNode : selectedNodes)
                {
                    gnc.pushLocationInUndoGraphmlMapCoordsStack(selectedNode);
                    currentNodeGraphmlMapCoord = gnc.getAllGraphmlNodesMap().get( selectedNode.getNodeName() ).first;
                    currentNodeGraphmlMapCoord[2] += translateXSelectedValue;
                    currentNodeGraphmlMapCoord[3] += translateYSelectedValue;
                }
            }
            else
            {
                for (GraphNode selectedNode : selectedNodes)
                {
                    selectedNode.pushLocationInUndoPointStack();
                    selectedNode.setLocation(selectedNode.getX() + translateXSelectedValue, selectedNode.getY() + translateYSelectedValue);
                }
            }

            if (!hasInitiated2DNodeDragging) hasInitiated2DNodeDragging = true;
            graph.getGraphRendererActions().getUndoNodeDraggingAction().setEnabled(true);
        }

        visibleNodes.addAll(selectedNodes);
    }

    /**
    *  Starts the dragged nodes process.
    */
    private void startDraggedNodesProcess()
    {
        HashSet<GraphNode> selectedNodes = selectionManager.getSelectedNodes();
        if ( !selectedNodes.isEmpty() )
        {
            updateVisibleNodesAndEdgesAccordingToSelection(selectedNodes);
            updateNodesDisplayList = true;
            updateEdgesDisplayList = true;

            layoutFrame.setCursor(BIOLAYOUT_MOVE_CURSOR);

            refreshDisplay();
        }
    }

    /**
    *  Ends the dragged nodes process.
    */
    private void endDraggedNodesProcess()
    {
        HashSet<GraphNode> selectedNodes = selectionManager.getSelectedNodes();
        if ( !selectedNodes.isEmpty() )
        {
            updateSelectedNodesPositionsAndReAddToVisibleNodes(selectedNodes);

            updateSelectedNodesDisplayList = true;
            updateNodesDisplayList = true;

            visibleEdges = tempVisibleEdges;
            updateEdgesDisplayList = true;

            layoutFrame.setCursor(BIOLAYOUT_NORMAL_CURSOR);
        }
    }

    /**
    *  Processes the undo event.
    */
    private void processUndoEvent()
    {
        HashSet<GraphNode> selectedNodes = selectionManager.getSelectedNodes();
        if ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() )
        {
            GraphmlNetworkContainer gnc = nc.getGraphmlNetworkContainer();
            for (GraphNode selectedNode : selectedNodes)
            {
                gnc.pushLocationInRedoGraphmlMapCoordsStack(selectedNode);
                gnc.popLocationFromUndoGraphmlMapCoordsStack(selectedNode);
            }
        }
        else
        {
            for (GraphNode selectedNode : selectedNodes)
            {
                selectedNode.pushLocationInRedoPointStack();
                selectedNode.popLocationFromUndoPointStack();
            }
        }
    }

    /**
    *  Processes the undo event.
    */
    private void processRedoEvent()
    {
        HashSet<GraphNode> selectedNodes = selectionManager.getSelectedNodes();
        if ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() )
        {
            GraphmlNetworkContainer gnc = nc.getGraphmlNetworkContainer();
            for (GraphNode selectedNode : selectedNodes)
            {
                gnc.pushLocationInUndoGraphmlMapCoordsStack(selectedNode);
                gnc.popLocationFromRedoGraphmlMapCoordsStack(selectedNode);
            }
        }
        else
        {
            for (GraphNode selectedNode : selectedNodes)
            {
                selectedNode.pushLocationInUndoPointStack();
                selectedNode.popLocationFromRedoPointStack();
            }
        }
    }

    /**
    *  Finds a particular node name by giving initial mouse coordinate positioning.
    */
    private void findNode(int startX, int startY, boolean hasMouseClicked)
    {
        mouseHasClicked = hasMouseClicked;

        pickOriginX = startX;
        pickOriginY = startY;
        pickWidth  = 1.0;
        pickHeight = 1.0;
        pickOneNode = true;
    }

    /**
    *  Returns a texture name to be used as a 2D node shape.
    */
    private String getTextureFromNode2DShape(Shapes2D shape2D)
    {
        switch (shape2D)
        {
            case CIRCLE:
                return "SimpleCircleNode256x256";
            case RECTANGLE:
                return "SimpleRectangleNode256x256";
            case ROUND_RECTANGLE:
                return "SimpleRoundRectangleNode256x256";
            case TRIANGLE:
                return "SimpleTriangleNode256x256";
            case DIAMOND:
                return "SimpleDiamondNode256x256";
            case PARALLELOGRAM:
                return "SimpleParallelogramNode256x256";
            case HEXAGON:
                return "SimpleHexagonNode256x256";
            case OCTAGON:
                return "SimpleOctagonNode256x256";
            case TRAPEZOID1:
                return "SimpleTrapezoid1Node256x256";
            case TRAPEZOID2:
                return "SimpleTrapezoid2Node256x256";
            case RECTANGLE_VERTICAL:
                return "SimpleTransitionVerticalNode46x256";
            case RECTANGLE_HORIZONTAL:
                return "SimpleTransitionHorizontalNode256x46";
            default:
                return "SimpleCircleNode256x256";
        }
    }

    /**
    *  Selects the next 2D shape.
    */
    private void nextShape()
    {
        current2DShape = (current2DShape.ordinal() < NUMBER_OF_2D_SHAPES - 1) ? Shapes2D.values()[current2DShape.ordinal() + 1] : CIRCLE;
    }

    /**
    *  Chooses the 2D shape.
    */
    private Shapes2D choose2DShape(Shapes2D originalShape)
    {
        return (MANUAL_SHAPE_2D) ? current2DShape : originalShape;
    }

    /**
    *  Called by the JOGL2 glDrawable immediately after the OpenGL context is initialized.
    */
    @Override
    public void init(GLAutoDrawable glDrawable)
    {
        if (DEBUG_BUILD) println("GraphRenderer2D init()");

        GL2 gl = glDrawable.getGL().getGL2();
        clearScreen2D(gl);
        gl.glDisable(GL_DEPTH_TEST); //disables the depth test for the 2D mode (hidden surface removal)

        nodesDisplayList = gl.glGenLists(1);
        selectedNodesDisplayList = gl.glGenLists(1);
        pathwayComponentContainersDisplayList = gl.glGenLists(1);
        for (int i = 0; i < ALL_SHAPES_2D_DISPLAY_LISTS.length; i++)
            ALL_SHAPES_2D_DISPLAY_LISTS[i] = gl.glGenLists(1);

        ANIMATION_CHANGE_SPECTRUM_TEXTURE_ENABLED = true;

        prepareTexturesLoader();
        prepareRandomPlasmaColor();
        prepareImageAndTextureSFXs(gl);
        prepareParticlesEffect();
        chooseParticleEffect();

        buildAllShapes2DDisplayLists(gl);

        if ( IS_RENDERER_MODE_FIRST_SWITCH && !RENDERER_MODE_START_3D.get() )
            IS_RENDERER_MODE_FIRST_SWITCH = false;
        else
            foregroundAnimation = true;
    }

    /**
    *  Called by the JOGL2 glDrawable to initiate OpenGL rendering by the client.
    */
    @Override
    public void display(GLAutoDrawable glDrawable)
    {
        if (DEBUG_BUILD) println("GraphRenderer2D display()");

        GL2 gl = glDrawable.getGL().getGL2();

        if (deAllocOpenGLMemory)
        {
            if (DEBUG_BUILD) println("GraphRenderer2D display: delete all display lists & destroy all textures");

            /*
            if (USE_VERTEX_ARRAYS_FOR_OPENGL_RENDERER)
            {
                // de-initialize OpenGL Vertex Arrays support
                gl.glDisableClientState(GL_TEXTURE_COORD_ARRAY);
                gl.glDisableClientState(GL_VERTEX_ARRAY);
            }
            */

            pickOneNode = false;
            animationFrameCount = 0;
            stepAnimation = false;

            deleteAllDisplayLists(gl);
            disposeAllTextures(gl);
        }
        else
        {
            gl.setSwapInterval(USE_VSYNCH.get() ? 1 : 0); // 0 for no VSynch, 1 for VSynch

            clearScreen2D(gl);

            // switch to model view state
            gl.glMatrixMode(GL_MODELVIEW);
            gl.glLoadIdentity();

            if (enableScreenSaver && !selectMode) renderBackgroundLayer(gl);
            performOpenGLTransformations(gl);
            if (!takeHighResScreenshot) buildAllDisplayListsAndRenderScene2D(gl);
            if ( !(renderProfileMode || animationRender) && (selectMode || pickOneNode) ) selectScene(gl);
            else if (renderProfileMode)
            {
                if (graphRendererThreadUpdater != null)
                {
                    if (IS_WIN && USE_SHADERS_PROCESS)
                        graph.drawProfileMode(gl, width, height,(animationRender) ? "- 3D Animation Mode -" : "- 3D Profile Mode -", animationRender);
                    else
                        graph.drawOSCompatibleProfileMode(gl);
                }

                if (animationRender)
                    graph.drawAnimationCurrentTick(gl, currentTick + 1);
            }
            if (foregroundAnimation || continueRenderingForegroundAnimationWhileSwitchingRenderer) renderForegroundLayer(gl);

            if (takeScreenshot) takeScreenshot(gl, renderToFile);
            if (takeHighResScreenshot) takeHighResScreenshot(gl);
            if (ANIMATION_INITIATE_END_OF_ANIMATION)
            {
                ANIMATION_INITIATE_END_OF_ANIMATION = false;
                layoutFrame.getLayoutAnimationControlDialog().stopAnimation(false);
                refreshDisplay();
            }
        }
    }

    /**
    *  Called by the JOGL2 glDrawable during the first repaint after the component has been resized.
    */
    @Override
    public void reshape(GLAutoDrawable glDrawable, int x, int y, int widthCanvas, int heightCanvas)
    {
        if (DEBUG_BUILD) println("GraphRenderer2D reshape()");

        GL2 gl = glDrawable.getGL().getGL2();

        gl.glViewport(x, y, width, height); // update the viewport

        // update the projection (camera) mode, orthographic in this case
        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0.0f, width, height, 0.0f, -1.0f, 1.0f);
        // same with:
        // GLU.gluOrtho2D(0, width, height, 0);
        gl.glMatrixMode(GL_MODELVIEW);

        // the launching of a Swing UI popup menu issues a reshape(), no need to re-init the effects though
        if (!graphPopupIsTiming)
        {
            if (foregroundAnimation)
            {
                if (!USE_SHADERS_PROCESS)
                {
                    // be careful to have width/height initialized before this!
                    imageSFXs.spotCircleEffectInit((int)(0.25f * width), (int)(0.25f * height), (int)(0.25f * width / 2), (int)(0.25f * height / 2), -12, false, 1);
                }
                else
                    shaderTextureSFXs.spotCircleEffectInit(0.1f, width, height, width / 2.0f, height / 2.0f, -24.0f, false, 1);

                startRender();
            }

            if (USE_SHADERS_PROCESS)
            {
                shaderTextureSFXs.blurEffectInit(gl, width, height, biolayoutLogoImageTexture, ShaderTextureSFXsBlurStates.FULL_BLUR, qualityRendering);
                shaderTextureSFXs.blobStars3DScrollerEffectInit(gl, blobStars3DScrollerEffectInitializer, 1.5f, width / 2, height / 2);
            }
        }
    }

    /**
    *  KeyPressed keyEvent.
    */
    @Override
    public void keyPressed(KeyEvent e)
    {
        if (DEBUG_BUILD) println("GraphRenderer2D keyPressed()");

        int keyCode = e.getKeyCode();
        boolean isCtrlDown = (!IS_MAC) ? e.isControlDown() : e.isMetaDown();

        if ( e.isShiftDown() )
        {
            // only hwen animations are off selection/dragging mode (through shift key) will be available
            if (!renderProfileMode)
            {
                isShiftDown = true;
                isShiftAltDown = e.isAltDown();
            }

            return;
        }
        else if ( e.isAltDown() )
        {
            // only when animations are off node focusing (through alt key) will be available
            if (!renderProfileMode)
                isAltDown = true;

            return;
        }
        else if ( ADVANCED_KEYBOARD_RENDERING_CONTROL.get() )
        {
            if ( (keyCode == KeyEvent.VK_UP) && isCtrlDown )
            {
                scaleValue += DEFAULT_SCALE * (1.0f + scaleValue);
            }
            else if ( (keyCode == KeyEvent.VK_DOWN) && isCtrlDown )
            {
                scaleValue -= DEFAULT_SCALE * (1.0f + scaleValue);
            }
            else if ( (keyCode == KeyEvent.VK_LEFT) && isCtrlDown )
            {
                rotateValue -= amountOfRotation;
            }
            else if ( (keyCode == KeyEvent.VK_RIGHT) && isCtrlDown )
            {
                rotateValue += amountOfRotation;
            }
            else if ( keyCode == KeyEvent.VK_UP )
            {
                translateYValue -= DEFAULT_TRANSLATION / (1.0f + scaleValue);
            }
            else if ( keyCode == KeyEvent.VK_DOWN )
            {
                translateYValue += DEFAULT_TRANSLATION / (1.0f + scaleValue);
            }
            else if ( keyCode == KeyEvent.VK_LEFT )
            {
                translateXValue -= DEFAULT_TRANSLATION / (1.0f + scaleValue);
            }
            else if ( keyCode == KeyEvent.VK_RIGHT )
            {
                translateXValue += DEFAULT_TRANSLATION / (1.0f + scaleValue);
            }
            else if ( (keyCode == KeyEvent.VK_1) && enableScreenSaver )
            {
                particleEffectToShow[0] = !particleEffectToShow[0];
                return;
            }
            else if ( (keyCode == KeyEvent.VK_2) && enableScreenSaver )
            {
                particleEffectToShow[1] = !particleEffectToShow[1];
                return;
            }
            else if ( (keyCode == KeyEvent.VK_3) && enableScreenSaver )
            {
                particleEffectToShow[2] = !particleEffectToShow[2];
                if (particleEffectToShow[2]) rightMostPartOfScreenForGeneratorEffect3 = nextBoolean();
                return;
            }
            else if ( (keyCode == KeyEvent.VK_4) && enableScreenSaver )
            {
                particleEffectToShow[3] = !particleEffectToShow[3];
                return;
            }
            else if ( (keyCode == KeyEvent.VK_5) && enableScreenSaver )
            {
                showAllParticleEffects = !showAllParticleEffects;

                for (int i = 0; i < particleEffectToShow.length; i++)
                    particleEffectToShow[i] = showAllParticleEffects;

                return;
            }
        }

        if (!renderProfileMode || animationRender) refreshDisplay();
    }

    /**
    *  KeyReleased keyEvent.
    */
    @Override
    public void keyReleased(KeyEvent e)
    {
        if (DEBUG_BUILD) println("GraphRenderer2D keyReleased()");

        isShiftDown = false;
        isShiftAltDown = false;
        isAltDown = false;

        resetSelectBoxValues();
    }

    /**
    *  KeyTyped keyEvent.
    */
    @Override
    public void keyTyped(KeyEvent e)
    {
        if (DEBUG_BUILD) println("GraphRenderer2D keyTyped()");

        boolean isCtrlDown = (!IS_MAC) ? e.isControlDown() : e.isMetaDown();
        if (!e.isAltDown() && !isCtrlDown)
        {
            if (e.getKeyChar() == '<')
            {
                graph.increaseNodeSize(false, true);
                updateNodesAndSelectedNodesDisplayList();
                refreshDisplay();
            }
            else if (e.getKeyChar() == '>')
            {
                graph.increaseNodeSize(true, true);
                updateNodesAndSelectedNodesDisplayList();
                refreshDisplay();
            }
            else if ( !e.isShiftDown() )
            {
                if (e.getKeyChar() == 'e' || e.getKeyChar() == 'E')
                {
                    if ( !layoutFrame.getFileNameLoaded().isEmpty() )
                        graph.initiateTakeScreenShotProcess(false);
                }
                else if (e.getKeyChar() == 'w' || e.getKeyChar() == 'W')
                {
                    if ( !layoutFrame.getFileNameLoaded().isEmpty() )
                        graph.initiateTakeScreenShotProcess(true);
                }
                else if (e.getKeyChar() == 's' || e.getKeyChar() == 'S')
                {
                    MANUAL_SHAPE_2D = true;
                    nextShape();
                    updateNodesAndSelectedNodesDisplayList();
                }
                else if (e.getKeyChar() == 'o' || e.getKeyChar() == 'O')
                {
                    resetAllRelevantValues(true);
                }
                else if (e.getKeyChar() == 'p' || e.getKeyChar() == 'P')
                {
                    if (!animationRender)
                        toggleScreenSaver();
                }
                else if (e.getKeyChar() == 'r' || e.getKeyChar() == 'R')
                {
                    if (!animationRender)
                        toggleRotation( new ActionEvent(this, 0, "ManualEvent") );
                }
                else if (e.getKeyChar() == 'v' || e.getKeyChar() == 'V')
                {
                    USE_VSYNCH.set( !USE_VSYNCH.get() );
                    layoutFrame.setUseVSynch( USE_VSYNCH.get() );
                    graph.setGraphRendererThreadUpdaterTargetFPS();
                    refreshDisplay();
                }
                else if (e.getKeyChar() == 'l' || e.getKeyChar() == 'L')
                {
                    MATERIAL_OLD_LCD_STYLE_TRANSPARENCY_SHADING.set( !MATERIAL_OLD_LCD_STYLE_TRANSPARENCY_SHADING.get() );
                    layoutFrame.setMaterialOldLCDStyleTransparencyShading( MATERIAL_OLD_LCD_STYLE_TRANSPARENCY_SHADING.get() );
                    refreshDisplay();
                }
                else if (e.getKeyChar() == 'j' || e.getKeyChar() == 'J')
                {
                    TRIPPY_BACKGROUND.set( !TRIPPY_BACKGROUND.get() );
                    layoutFrame.setTrippyBackground( TRIPPY_BACKGROUND.get() );
                    graph.prepareBackgroundColor();
                    refreshDisplay();
                }
                else if (e.getKeyChar() == ' ')
                {
                    currentLogoEffect = LogoEffects.values()[ (currentLogoEffect.ordinal() + 1) % LogoEffects.values().length ];
                    if (!USE_SHADERS_PROCESS)
                    {
                        if (currentLogoEffect.equals(LogoEffects.TEXTUREDISPLACEMENT_EFFECT) )
                            currentLogoEffect = LogoEffects.BUMP_EFFECT;
                        else if (currentLogoEffect.equals(LogoEffects.BLUR_EFFECT) )
                            currentLogoEffect = LogoEffects.RADIAL_BLUR_EFFECT;
                    }
                    chooseParticleEffect();
                }
            }
        }
    }

    /**
    *  MouseClicked mouseEvent.
    */
    @Override
    public void	mouseClicked(MouseEvent e)
    {
        if (DEBUG_BUILD) println("GraphRenderer2D mouseClicked()");

        if ( SwingUtilities.isLeftMouseButton(e) )
        {
            boolean isCtrlDown = (!IS_MAC) ? e.isControlDown() : e.isMetaDown();
            boolean flag = !isShiftDown && isCtrlDown;

            findNode(e.getX(), e.getY(), !flag);

            refreshDisplay();
            refreshDisplay(); // perform two updates to show the selected node

            if (flag)
            {
                if ( !lastNodeURLStringPicked.isEmpty() )
                    InitDesktop.browse(lastNodeURLStringPicked);
                else if ( !lastNodeNamePicked.isEmpty() )
                    InitDesktop.browse(SEARCH_URL.getUrl() + lastNodeNamePicked.split("\\s+")[0]);
            }
        }
    }

    /**
    *  MouseEntered mouseEvent.
    */
    @Override
    public void mouseEntered(MouseEvent e)
    {
        if (DEBUG_BUILD) println("GraphRenderer2D mouseEntered()");
    }

    /**
    *  MouseExited mouseEvent.
    */
    @Override
    public void mouseExited(MouseEvent e)
    {
        if (DEBUG_BUILD) println("GraphRenderer2D mouseExited()");
    }

    /**
    *  MousePressed mouseEvent.
    */
    @Override
    public void mousePressed(MouseEvent e)
    {
        if (DEBUG_BUILD) println("GraphRenderer2D mousePressed()");

        boolean doTranslate = (!reverseNavigationMouseButtons) ? SwingUtilities.isLeftMouseButton(e) : SwingUtilities.isRightMouseButton(e);
        if ( doTranslate || SwingUtilities.isLeftMouseButton(e) )
        {
            mouseDragStartX = e.getX();
            mouseDragStartY = e.getY();
        }

        if (doTranslate)
        {
            mouseDragStartTranslateX = mouseDragStartX - ( translateXValue * (1.0f + scaleValue) );
            mouseDragStartTranslateY = mouseDragStartY - ( translateYValue * (1.0f + scaleValue) );
        }

        if ( SwingUtilities.isLeftMouseButton(e) )
        {
            isDraggingNodes = (!selectionManager.getSelectedNodes().isEmpty() && !isShiftDown) && !isAltDown;

            if (isDraggingNodes && !disableEdgeDrawingForMouseEvents)
            {
                if ( graphPopupIsTiming || GRAPH_POPUP_COMPONENT.isPopupMenuVisible() )
                {
                    graphPopupIsTiming = false;
                    GRAPH_POPUP_COMPONENT.setPopupMenuVisible(false);
                    graphPopupScheduledFuture.cancel(true);
                }

                // make sure dragging coords are resetted
                translateXSelectedValue = 0.0f;
                translateYSelectedValue = 0.0f;

                disableEdgeDrawingForMouseEvents = true;

                startDraggedNodesProcess();
            }
        }
    }

    /**
    *  MouseReleased mouseEvent.
    */
    @Override
    public void mouseReleased(MouseEvent e)
    {
        if (DEBUG_BUILD) println("GraphRenderer2D mouseReleased()");

        isInMotion = false;

        if ( SwingUtilities.isLeftMouseButton(e) )
        {
            if (isShiftDown && !isDraggingNodes)
            {
                pickOriginX = rint( ( mouseDragStartX + e.getX() ) / 2.0 ); // origin X must be centered for OpenGL
                pickOriginY = rint( ( mouseDragStartY + e.getY() ) / 2.0 ); // origin Y must be centered for OpenGL

                pickWidth  = abs(e.getX() - mouseDragStartX);
                pickHeight = abs(e.getY() - mouseDragStartY);

                if (DEBUG_BUILD)
                {
                    println("pickOriginX: " + pickOriginX);
                    println("pickOriginY: " + pickOriginY);
                    println("pickWidth:    " + pickWidth);
                    println("pickHeight:   " + pickHeight);
                }

                selectMode = true;
                isShiftDown = false;

                resetSelectBoxValues();
            }

            if (isDraggingNodes && disableEdgeDrawingForMouseEvents)
            {
                // first update display lists then reset coords
                endDraggedNodesProcess();

                translateXSelectedValue = 0.0f;
                translateYSelectedValue = 0.0f;

                isDraggingNodes = false;
                disableEdgeDrawingForMouseEvents = false;
            }

            if (!renderProfileMode || animationRender)
            {
                refreshDisplay();
                refreshDisplay();
            }
        }
    }

    /**
    *  MouseDragged mouseMotionEvent.
    */
    @Override
    public void mouseDragged(MouseEvent e)
    {
        if (DEBUG_BUILD) println("GraphRenderer2D mouseDragged()");

        if (pickOneNode)
        {
            pickOneNode = false;

            if ( graphPopupIsTiming || GRAPH_POPUP_COMPONENT.isPopupMenuVisible() )
            {
                graphPopupIsTiming = false;
                GRAPH_POPUP_COMPONENT.setPopupMenuVisible(false);
                graphPopupScheduledFuture.cancel(true);
            }
        }

        int mouseXCoord = e.getX();
        int mouseYCoord = e.getY();

        if (!isShiftDown && !isDraggingNodes)
        {
            isInMotion = true;
            boolean doTranslate = (!reverseNavigationMouseButtons) ? SwingUtilities.isLeftMouseButton(e) : SwingUtilities.isRightMouseButton(e);
            boolean doZoom = (!reverseNavigationMouseButtons) ? SwingUtilities.isRightMouseButton(e) : SwingUtilities.isLeftMouseButton(e);

            if (doTranslate)
            {
                translateXValue = (mouseXCoord - mouseDragStartTranslateX) / (1.0f + scaleValue);
                translateYValue = (mouseYCoord - mouseDragStartTranslateY) / (1.0f + scaleValue);
            }
            else if (doZoom)
            {
                if (mouseXCoord > prevMouseXCoordForScale) scaleValue += DEFAULT_SCALE * (1.0f + scaleValue);
                else if (mouseXCoord < prevMouseXCoordForScale) scaleValue -= DEFAULT_SCALE * (1.0f + scaleValue);

                if (mouseYCoord < prevMouseYCoordForScale) scaleValue += DEFAULT_SCALE * (1.0f + scaleValue);
                else if (mouseYCoord > prevMouseYCoordForScale) scaleValue -= DEFAULT_SCALE * (1.0f + scaleValue);

                prevMouseXCoordForScale = mouseXCoord;
                prevMouseYCoordForScale = mouseYCoord;
            }
        }
        else // if (isShiftDown)
        {
            if ( SwingUtilities.isLeftMouseButton(e) )
            {
                if (!isDraggingNodes)
                {
                    selectBox(mouseDragStartX, mouseDragStartY, mouseXCoord, mouseYCoord);
                }
                else
                {
                    // make sure the background edges are different before translating/moving around
                    if ( !( visibleEdges.size() == tempVisibleEdges.size() ) || selectionManager.getSelectedNodes().size() == 1 )
                    {
                        translateXSelectedValue = (mouseXCoord - mouseDragStartX) / (1.0f + scaleValue);
                        translateYSelectedValue = (mouseYCoord - mouseDragStartY) / (1.0f + scaleValue);
                    }
                }
            }
        }

        if (!renderProfileMode || animationRender) refreshDisplay();
    }

    /**
    *  MouseMoved mouseMotionEvent.
    */
    @Override
    public void mouseMoved(MouseEvent e)
    {
        if (DEBUG_BUILD) println("GraphRenderer2D mouseMoved()");

        if (!renderProfileMode || animationRender)
        {
            if ( graphPopupIsTiming || GRAPH_POPUP_COMPONENT.isPopupMenuVisible() )
            {
                graphPopupIsTiming = false;
                GRAPH_POPUP_COMPONENT.setPopupMenuVisible(false);
                graphPopupScheduledFuture.cancel(true);
            }

            if (!isShiftDown)
            {
                findNode(e.getX(), e.getY(), false);

                refreshDisplay();

                if ( !lastNodeNamePicked.isEmpty() )
                {
                    layoutFrame.setNodeLabel(lastNodeNamePicked);
                    if (!selectionManager.getSelectedNodes().isEmpty() && selectionManager.getSelectedNodes().contains(closestNode))
                    {
                        GRAPH_POPUP_COMPONENT.setPopupComponent(graph, e.getX(), e.getY(),
                                selectionManager.getSelectedNodes(), nc, layoutFrame);
                    }
                    else
                    {
                        GRAPH_POPUP_COMPONENT.setPopupComponent(graph, e.getX(), e.getY(), closestNode, nc, layoutFrame);
                    }
                    graphPopupScheduledFuture = GRAPH_POPUP_SCHEDULED_EXECUTOR_SERVICE.schedule(GRAPH_POPUP_COMPONENT, (int)(1.5 * COMPONENT_POPUP_DELAY_MILLISECONDS), TimeUnit.MILLISECONDS);
                    graphPopupIsTiming = true;
                    graphPopupReset = true;
                }
                else
                {
                    if (graphPopupReset)
                    {
                        graphPopupReset = false;
                        layoutFrame.setNodeLabel(NO_NODE_FOUND_LABEL);
                    }
                }
            }
        }

        if (enableScreenSaver)
        {
            if ( !USE_SHADERS_PROCESS || !( NORMAL_QUALITY_ANTIALIASING.get() || HIGH_QUALITY_ANTIALIASING.get() ) )
            {
                if (textureSFXs != null) textureSFXs.blobStars3DScrollerEffectMouseMove( e.getX(), e.getY() );
            }
            else
            {
                if (shaderTextureSFXs != null) shaderTextureSFXs.blobStars3DScrollerEffectMouseMove( e.getX(), e.getY() );
            }
        }
    }

    /**
    *  MouseWheelMoved mouseWheelEvent.
    */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        if (DEBUG_BUILD) println("GraphRenderer2D mouseWheelMoved()");

        rotateValue += (e.getWheelRotation() < 0) ? amountOfRotation : -amountOfRotation;
        if (!renderProfileMode || animationRender) refreshDisplay();
    }

    /**
    *  Switches the renderer mode for GraphRenderer2D.
    */
    @Override
    public void switchRendererMode()
    {
        if (DEBUG_BUILD) println("GraphRenderer2D switchRendererMode()");

        if (!foregroundAnimation)
        {
            layoutFrame.setEnabledAllToolBars(false);
            if ( (!USE_SHADERS_PROCESS) ? (imageSFXs != null) : (shaderTextureSFXs != null) )
            {
                switchRenderer = true;

                if (!USE_SHADERS_PROCESS)
                    imageSFXs.spotCircleEffectInit((int)(0.25f * width), (int)(0.25f * height), (int)(0.25f * width / 2), (int)(0.25f * height / 2), 12, false, 1);
                else
                    shaderTextureSFXs.spotCircleEffectInit(0.1f, width, height, width / 2.0f, height / 2.0f, 24.0f, false, 1);

                foregroundAnimation = true;
                continueRenderingForegroundAnimationWhileSwitchingRenderer = true;
                startRender();
            }
            else
                listener.switchRendererModeCallBack();
        }
    }

    /**
    *  Adds all events to GraphRenderer2D.
    */
    @Override
    public void addAllEvents()
    {
        if (DEBUG_BUILD) println("GraphRenderer2D addAllEvents()");

        renderProfileMode = false;
        enableProfiler = false;
        enableScreenSaver = false;
        deAllocOpenGLMemory = false;

        refreshDisplay();
    }

    /**
    *  Removes all events from GraphRenderer2D.
    */
    @Override
    public void removeAllEvents()
    {
        if (DEBUG_BUILD) println("GraphRenderer2D removeAllEvents()");

        if (graphRendererThreadUpdater != null) stopRender();

        deAllocOpenGLMemory = true;
        renderProfileMode = false;
        enableProfiler = false;
        enableScreenSaver = false;
        continueRenderingForegroundAnimationWhileSwitchingRenderer = false;

        refreshDisplay();
    }

    /**
    *  Gets a BufferedImage (mainly used for printing functionality).
    */
    @Override
    public BufferedImage getBufferedImage()
    {
        renderToFile = false;
        if (!takeScreenshot) takeScreenshot = true;
        refreshDisplay();
        renderToFile = true;

        return screenshot;
    }

    /**
    *  Fully updates the display lists.
    */
    @Override
    public void updateAllDisplayLists()
    {
        if (DEBUG_BUILD) println("updateAllDisplayLists() for 2D mode");

        updateDisplayLists(true, true, true);
    }

    /**
    *  Updates the nodes display list only.
    */
    @Override
    public void updateNodesDisplayList()
    {
        if (DEBUG_BUILD) println("updateNodesDisplayList() for 2D mode");

        updateDisplayLists(true, false, false);
    }

    /**
    *  Updates the selected nodes display list only.
    */
    @Override
    public void updateSelectedNodesDisplayList()
    {
        if (DEBUG_BUILD) println("updateSelectedNodesDisplayList() for 2D mode");

        updateDisplayLists(false, false, true);
    }

    /**
    *  Updates the nodes & selected nodes display lists only.
    */
    @Override
    public void updateNodesAndSelectedNodesDisplayList()
    {
        if (DEBUG_BUILD) println("updateNodesAndSelectedNodesDisplayList() for 2D mode");

        updateDisplayLists(true, false, true);
    }

    /**
    *  Updates the edges display lists only.
    */
    @Override
    public void updateEdgesDisplayList()
    {
        if (DEBUG_BUILD) println("updateEdgesDisplayList() for 2D mode");

        updateDisplayLists(false, true, false);
    }

    /**
    *  Updates the display lists selectively.
    */
    @Override
    public void updateDisplayLists(boolean nodesDisplayList, boolean edgesDisplayList, boolean selectedNodesDisplayList)
    {
        if (DEBUG_BUILD) println("updateDisplayLists(" + nodesDisplayList + ", " + edgesDisplayList + ", " + selectedNodesDisplayList + ") for 2D mode");

        if ( prevYEdStyleRrenderingForGraphmlFiles == YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() )
        {
            updateNodesDisplayList = nodesDisplayList;
            updateEdgesDisplayList = nodesDisplayList ? true : edgesDisplayList; // always update the edges when nodes are being updated, to properly re-calculate the edge-to-node connections
            updateSelectedNodesDisplayList = selectedNodesDisplayList;
        }
        else
        {
            updateNodesDisplayList = true;
            updateEdgesDisplayList = true;
            updateSelectedNodesDisplayList = true;

            resetAllRelevantValues(true);
        }

        graph.prepareBackgroundColor();
        prevYEdStyleRrenderingForGraphmlFiles = YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get();

        refreshDisplay();
    }

    /**
    *  Updates all animation.
    */
    @Override
    public void updateAnimation()
    {
        if (DEBUG_BUILD) println("Updating 2D Scene");

        if (renderProfileMode && !animationRender)
            rotateValue += (negateUnit) ? -amountOfRotation : amountOfRotation;

        if (enableScreenSaver)
        {
            if (!USE_SHADERS_PROCESS)
                textureSFXs.textureDisplacementEffect();
            else
                shaderTextureSFXs.textureDisplacementEffect();

            if ( currentLogoEffect.equals(LogoEffects.WATER_EFFECT) )
            {
                if (!USE_SHADERS_PROCESS)
                    imageSFXs.waterEffect();
                else
                    shaderTextureSFXs.waterEffect();
            }
            else if ( currentLogoEffect.equals(LogoEffects.TEXTUREDISPLACEMENT_EFFECT) );
                // shaderTextureSFXs.textureDisplacementEffect();
            else if ( currentLogoEffect.equals(LogoEffects.BUMP_EFFECT) )
            {
                if (!USE_SHADERS_PROCESS)
                    imageSFXs.bumpEffect();
                else
                    shaderTextureSFXs.bumpEffect();
            }
            else if ( currentLogoEffect.equals(LogoEffects.BLUR_EFFECT) )
            {
                if ( (blurSize < 0.0f) || (blurSize > 5.0f) )
                    blurSizeStep = -blurSizeStep;
                blurSize += blurSizeStep;
            }
            else if ( currentLogoEffect.equals(LogoEffects.RADIAL_BLUR_EFFECT) )
            {
                if (!USE_SHADERS_PROCESS)
                    imageSFXs.radialBlurEffect((int)( 0.1f * BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE.getHeight() ), true, BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE.getWidth() / 4, BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE.getHeight() / 4);
                else
                    shaderTextureSFXs.radialBlurEffect();
            }
            else if ( currentLogoEffect.equals(LogoEffects.PLASMA_EFFECT) )
            {
                if (!USE_SHADERS_PROCESS)
                {
                    graph.colorCycle(PLASMA_COLOR);
                    imageSFXs.plasmaEffect();
                }
                else
                {
                    shaderTextureSFXs.plasmaEffect();
                }
            }

            if ( !USE_SHADERS_PROCESS || !( NORMAL_QUALITY_ANTIALIASING.get() || HIGH_QUALITY_ANTIALIASING.get() ) )
                textureSFXs.blobStars3DScrollerEffect();
            else
                shaderTextureSFXs.blobStars3DScrollerEffect();

            for (int i = 0; i < NUMBER_OF_GENERATED_PARTICLE_EFFECTS; i++)
            {
                if (particleEffectToShow[i])
                {
                    if (i == 0) allParticlesGeneratorEffects.get(0).particlesGenerator1(width / 2, height / 2);
                    else if (i == 1) allParticlesGeneratorEffects.get(1).particlesGenerator2(width, height);
                    else if (i == 2) allParticlesGeneratorEffects.get(2).particlesGenerator3(width, height, rightMostPartOfScreenForGeneratorEffect3);
                    else if (i == 3) allParticlesGeneratorEffects.get(3).particlesGenerator4(width, height);

                    allParticlesGeneratorEffects.get(i).updateAllGeneratedParticles();
                }
            }

            textureScaleFactor += textureStep;
            if ( (textureScaleFactor < 0) || (textureScaleFactor > 2.0) )
            {
                if (textureScaleFactor < 0)
                {
                    currentLogoEffect = LogoEffects.values()[ (currentLogoEffect.ordinal() + 1) % LogoEffects.values().length ];
                    if (!USE_SHADERS_PROCESS)
                    {
                        if (currentLogoEffect.equals(LogoEffects.TEXTUREDISPLACEMENT_EFFECT) )
                            currentLogoEffect = LogoEffects.BUMP_EFFECT;
                        else if (currentLogoEffect.equals(LogoEffects.BLUR_EFFECT) )
                            currentLogoEffect = LogoEffects.RADIAL_BLUR_EFFECT;
                    }
                    chooseParticleEffect();
                }

                textureStep = -textureStep;
            }
        }

        if (foregroundAnimation)
        {
            if ( (!USE_SHADERS_PROCESS) ? !imageSFXs.hasSpotCircleEffectFinished() : !shaderTextureSFXs.hasSpotCircleEffectFinished() )
            {
                if (!USE_SHADERS_PROCESS)
                    imageSFXs.spotCircleEffect();
                else
                    shaderTextureSFXs.spotCircleEffect();
            }
            else
            {
                foregroundAnimation = false;
                if (!renderProfileMode && !enableScreenSaver)
                    stopRender();

                if (switchRenderer)
                {
                    switchRenderer = false;
                    renderProfileMode = false;
                    enableProfiler = false;
                    enableScreenSaver = false;

                    listener.switchRendererModeCallBack();
                }
            }
        }

        if (animationRender)
        {
            currentTick = (int)( ( ++animationFrameCount / (FRAMERATE_PER_SECOND_FOR_ANIMATION / ANIMATION_TICKS_PER_SECOND) ) );
            if (currentTick >= TOTAL_NUMBER_OF_ANIMATION_TICKS)
            {
                currentTick = TOTAL_NUMBER_OF_ANIMATION_TICKS - 1;
                ANIMATION_INITIATE_END_OF_ANIMATION = true;
            }

            if (stepAnimation)
            {
                stepAnimation = false;
                generalPauseRenderUpdateThread();
            }
        }
    }

    /**
    *  Refreshes the display.
    */
    @Override
    public void refreshDisplay()
    {
        if (DEBUG_BUILD) println("GraphRenderer2D refreshDisplay()");

        if (graph != null) graph.display();
    }

    /**
    *  Resets all values.
    */
    @Override
    public void resetAllValues()
    {
        if (DEBUG_BUILD) println("GraphRenderer2D resetAllValues()");

        resetAllRelevantValues(true);
    }

    /**
    *  Checks if there are more undo steps to be performed.
    */
    @Override
    public boolean hasMoreUndoSteps()
    {
        HashSet<GraphNode> selectedNodes = selectionManager.getSelectedNodes();
        if ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() )
        {
            GraphmlNetworkContainer gnc = nc.getGraphmlNetworkContainer();
            for (GraphNode selectedNode : selectedNodes)
                if ( !gnc.isEmptyUndoPointStack(selectedNode) )
                    return true;

            return false;
        }
        else
        {
            for (GraphNode selectedNode : selectedNodes)
                if ( !selectedNode.isEmptyUndoPointStack() )
                    return true;

            return false;
        }
    }

    /**
    *  Checks if there are more redo steps to be performed.
    */
    @Override
    public boolean hasMoreRedoSteps()
    {
        HashSet<GraphNode> selectedNodes = selectionManager.getSelectedNodes();
        if ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() )
        {
            GraphmlNetworkContainer gnc = nc.getGraphmlNetworkContainer();
            for (GraphNode selectedNode : selectedNodes)
                if ( !gnc.isEmptyRedoPointStack(selectedNode) )
                    return true;

            return false;
        }
        else
        {
            for (GraphNode selectedNode : selectedNodes)
                if ( !selectedNode.isEmptyRedoPointStack() )
                    return true;

            return false;
        }
    }

    /**
    *  The main take a screenshot process.
    */
    @Override
    public void takeScreenShotProcess(boolean doHighResScreenShot)
    {
        //give time for menubar to hide
        refreshDisplay();

        if (doHighResScreenShot)
        {
            highResRenderToFile();

            if (!takeHighResScreenshot) takeHighResScreenshot = true;
        }
        else
        {
            if (!takeScreenshot) takeScreenshot = true;
        }

        refreshDisplay();
        // second refresh for cleaning behind InitDesktop.open(savedImage)
        if (doHighResScreenShot)
            refreshDisplay();
    }

    /**
    *  Creates the reTranslate action.
    */
    @Override
    public void createReTranslateAction(TranslateTypes translateType, ActionEvent e)
    {
        if ( translateType.equals(TranslateTypes.TRANSLATE_UP) )
            translateYValue -= DEFAULT_TRANSLATION / (1.0f + scaleValue);
        else if ( translateType.equals(TranslateTypes.TRANSLATE_DOWN) )
            translateYValue += DEFAULT_TRANSLATION / (1.0f + scaleValue);
        else if ( translateType.equals(TranslateTypes.TRANSLATE_LEFT) )
            translateXValue -= DEFAULT_TRANSLATION / (1.0f + scaleValue);
        else if ( translateType.equals(TranslateTypes.TRANSLATE_RIGHT) )
            translateXValue += DEFAULT_TRANSLATION / (1.0f + scaleValue);

        refreshDisplay();

        if (DEBUG_BUILD) println("GraphRenderer2D reTranslate()");
    }

    /**
    *  Creates the reRotate action.
    */
    @Override
    public void createReRotateAction(RotateTypes rotateType, ActionEvent e)
    {
        if ( rotateType.equals(RotateTypes.ROTATE_LEFT) )
            rotateValue -= amountOfRotation;
        else if ( rotateType.equals(RotateTypes.ROTATE_RIGHT) )
            rotateValue += amountOfRotation;

        refreshDisplay();

        if (DEBUG_BUILD) println("GraphRenderer2D reRotate()");
    }

    /**
    *  Creates the reScale action.
    */
    @Override
    public void createReScaleAction(ScaleTypes scaleType, ActionEvent e)
    {
        float factor = scaleType.equals(ScaleTypes.SCALE_IN) ? SCALE_FACTOR : 1.0f / SCALE_FACTOR;
        if (factor > 1.0f)
        {
            if (scaleValue == 0.0f) scaleValue = 0.002f;
            factor *= MORE_RESCALE_FACTOR;
        }
        else
        {
            if (scaleValue == 0.0f) scaleValue = -0.002f;
            factor /= MORE_RESCALE_FACTOR;
        }

        scaleValue = (scaleValue > 0.0f) ? (scaleValue * factor) : (scaleValue / factor);
        if (scaleValue >= -0.002f && scaleValue <= 0.002f) scaleValue = 0.0f;

        refreshDisplay();

        if (DEBUG_BUILD) println("GraphRenderer2D reScale() By:" + factor + " New Zoom:" + scaleValue);
    }

    /**
    *  Creates the Burst Layout Iterations action.
    */
    @Override
    public void createBurstLayoutIterationsAction(ActionEvent e) {}

    /**
    *  Gets the undo node dragging action.
    */
    @Override
    public void createUndoNodeDraggingAction(ActionEvent e)
    {
        processUndoEvent();
        updateAllDisplayLists();
        refreshDisplay();

        graph.setEnabledUndoNodeDragging(true);
        graph.setEnabledRedoNodeDragging(true);
    }

    /**
    *  Gets the redo node dragging action.
    */
    @Override
    public void createRedoNodeDraggingAction(ActionEvent e)
    {
        processRedoEvent();
        updateAllDisplayLists();
        refreshDisplay();

        graph.setEnabledUndoNodeDragging(true);
        graph.setEnabledRedoNodeDragging(true);
    }

    /**
    *  Gets the autorotate action.
    */
    @Override
    public void createAutoRotateAction(ActionEvent e)
    {
        toggleRotation(e);
    }

    /**
    *  Gets the screensaver 2D mode action.
    */
    @Override
    public void createAutoScreenSaver2DModeAction(ActionEvent e)
    {
        toggleScreenSaver();
    }

    /**
    *  Gets the pulsation 3D mode action.
    */
    @Override
    public void createPulsation3DModeAction(ActionEvent e) {}


    /**
    *  Gets the selection action.
    */
    @Override
    public void createSelectAction(ActionEvent e) {}

    /**
    *  Gets the translation action.
    */
    @Override
    public void createTranslateAction(ActionEvent e)
    {
        reverseNavigationMouseButtons = false;
    }

    /**
    *  Gets the rotation action.
    */
    @Override
    public void createRotateAction(ActionEvent e) {}

    /**
    *  Gets the zoom action.
    */
    @Override
    public void createZoomAction(ActionEvent e)
    {
        reverseNavigationMouseButtons = true;
    }

    /**
    *  Gets the reset view action.
    */
    @Override
    public void createResetViewAction(ActionEvent e)
    {
        resetAllRelevantValues(true);
    }

    /**
    *  Gets the render action.
    */
    @Override
    public void createRenderImageToFileAction(ActionEvent e) {}

    /**
    *  Gets the high resolution render action.
    */
    @Override
    public void createRenderHighResImageToFileAction(ActionEvent e) {}

    /**
    *  Generally pauses the renderer.
    */
    @Override
    public void generalPauseRenderUpdateThread()
    {
        graphRendererThreadUpdater.generalPauseRenderUpdateThread();
    }

    /**
    *  Generally resumes the renderer.
    */
    @Override
    public void generalResumeRenderUpdateThread()
    {
        graphRendererThreadUpdater.generalResumeRenderUpdateThread();
    }

    /**
    *  The name of the GraphRenderer2D object.
    */
    @Override
    public String toString()
    {
        return "2D OpenGL renderer";
    }

    /**
    *  Clean up resources
    */
    @Override
    public void dispose(GLAutoDrawable glAutoDrawable)
    {
      //FIXME this was added during the move to JOGL 2
      //TODO check if resources need to be freed here
    }
}
