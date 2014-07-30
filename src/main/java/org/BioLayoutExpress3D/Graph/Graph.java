package org.BioLayoutExpress3D.Graph;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import javax.media.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.awt.ImageUtil;
import org.BioLayoutExpress3D.Utils.Path;
import javax.media.opengl.awt.GLCanvas;
import static javax.media.opengl.GL2.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.Graph.ActionsUI.*;
import org.BioLayoutExpress3D.Graph.GraphElements.*;
import org.BioLayoutExpress3D.Graph.Selection.*;
import org.BioLayoutExpress3D.Models.Lathe3D.*;
import org.BioLayoutExpress3D.Network.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import org.BioLayoutExpress3D.Environment.DataFolder;
import org.BioLayoutExpress3D.GPUComputing.OpenGLContext.*;
import static org.BioLayoutExpress3D.Graph.GraphRenderer3DFinalVariables.*;
import static org.BioLayoutExpress3D.Graph.GraphRendererCommonVariables.*;
import static org.BioLayoutExpress3D.Graph.GraphRendererCommonFinalVariables.*;
import static org.BioLayoutExpress3D.Graph.Camera.CameraUI.GraphAnaglyphGlassesTypes.*;
import static org.BioLayoutExpress3D.Models.Lathe3D.Lathe3DShapeAngleIncrements.*;
import static org.BioLayoutExpress3D.StaticLibraries.ImageProducer.*;
import static org.BioLayoutExpress3D.Textures.DrawTextureSFXs.*;
import static org.BioLayoutExpress3D.Environment.AnimationEnvironment.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* The Graph class extends GLCanvas and is the placeholder for the OpenGL canvas & renderer of BioLayoutExpress3D.
* It also provides communication support for the two renderers and the main GUI system of BioLayoutExpress3D.
*
* @see org.BioLayoutExpress3D.Graph.GraphRendererCommonVariables
* @see org.BioLayoutExpress3D.Graph.GraphRendererCommonFinalVariables
* @see org.BioLayoutExpress3D.Graph.GraphRenderer2D
* @see org.BioLayoutExpress3D.Graph.GraphRenderer3D
* @author Ildefonso Cases, total rewrite by Thanos Theo, 2008-2009
* @version 3.0.0.0
*
*/

public class Graph extends GLCanvas implements GraphInterface
{
    /**
    *  Serial version UID variable for the JLayeredPane class.
    */
    public static final long serialVersionUID = 111222333444555720L;

    /**
    *  Active texture unit to be used for the 2D texture.
    */
    public static final int ACTIVE_TEXTURE_UNIT_FOR_2D_TEXTURE = 0;

    /**
    *  Active texture unit to be used for the animation 2D texture.
    */
    public static final int ACTIVE_TEXTURE_UNIT_FOR_ANIMATION_SPECTRUM_2D_TEXTURE = ACTIVE_TEXTURE_UNIT_FOR_2D_TEXTURE + 1;

    /**
    *  Node size related variable.
    */
    public static final float NODE_SIZE_DIVIDE_RATIO = 270.0f;

    /**
    *  Constant value needed for the OpenGL renderer.
    */
    public static final IntBuffer OPENGL_INT_VALUE = Buffers.newDirectIntBuffer(1);

    /**
    *  Constant value needed for the OpenGL renderer.
    */
    private static final int START_COORD_TO_RENDER_BACKGROUND_TEXTURE = 10;

    /**
    *  Constant value needed for the OpenGL renderer.
    */
    private static final float HEIGHT_RATIO_BETWEEN_LINES = 1.3f;

    /**
    *  Constant value needed for the OpenGL renderer.
    */
    private static final int MESSAGES_FONT_STYLE = Font.ITALIC | Font.BOLD;

    /**
    *  Constant value needed for the OpenGL renderer.
    */
    private static final int MESSAGES_FONT_SIZE = 12;

    /**
    *  Constant value needed for the OpenGL renderer.
    */
    private static final float AMOUNT_OF_NODE_SIZE_INCREASE = 0.25f;

    /**
    *  Messages related variable.
    */
    private Font messagesFont = null;

    /**
    *  Value needed for the OpenGL renderer.
    */
    private Texture renderProfileModeBackgroundTexture = null;

    /**
    *  Value needed for the OpenGL renderer.
    */
    private TextRenderer profileModeTextRenderer = null;

    /**
    *  Variable to be used with the colorRotate() method.
    */
    private boolean decreaseRed = false;

    /**
    *  Variable to be used with the colorRotate() method.
    */
    private boolean decreaseGreen = false;

    /**
    *  Variable to be used with the colorRotate() method.
    */
    private boolean decreaseBlue = false;

    /**
    *  Variable to be used with the saveImageToFile() method.
    */
    private JFileChooser imageToFileChooser = null;

    /**
    *  Variable to be used with the saveImageToFile() method.
    */
    private FileNameExtensionFilter fileNameExtensionFilterPNG = null;

    /**
    *  Variable to be used with the saveImageToFile() method.
    */
    private FileNameExtensionFilter fileNameExtensionFilterJPG = null;

    /**
    *  GraphActions reference for all Graph actions.
    */
    private GraphActions graphActions = null;

    /**
    *  GraphRenderer2D reference (upcasted to a GraphInterface) for all 2D rendering.
    */
    private GraphInterface graphRenderer2D = null;

    /**
    *  GraphRenderer3D reference (upcasted to a GraphInterface) for all 3D rendering.
    */
    private GraphInterface graphRenderer3D = null;

    /**
    *  GraphInterface reference for the current graph renderer.
    */
    private GraphInterface currentGraphRenderer = null;

    /**
    *  GraphRenderer reference for all GraphRenderer2D/3D actions.
    */
    private GraphRendererActions graphRendererActions = null;

    /**
    *  Variable to be used to re-initialize a renderer mode.
    */
    private boolean reInitializeRendererMode = false;

    /**
    *  The Graph class constructor.
    */
    public Graph(LayoutFrame layoutFrame, NetworkContainer nc)
    {
        super( getCaps() );

	// Pass a layoutFrame and nc to common variables.
        GraphRendererCommonVariables.layoutFrame = layoutFrame;
        GraphRendererCommonVariables.nc = nc;
        layoutProgressBarDialog = layoutFrame.getLayoutProgressBar();

        this.setBackground(Color.BLACK);
        layoutFrame.setNodeLabel(NO_NODE_FOUND_LABEL);

        selectionManager = new SelectionManager(layoutFrame, this);
        messagesFont = layoutFrame.getFont().deriveFont(MESSAGES_FONT_STYLE, MESSAGES_FONT_SIZE);
	// Graph actions and renderers take this graph object as an argument.
        graphActions = new GraphActions(this);
        graphRenderer2D = new GraphRenderer2D(this);
        graphRenderer3D = new GraphRenderer3D(this);
        currentGraphRenderer = (RENDERER_MODE_3D) ? graphRenderer3D : graphRenderer2D;
        graphRendererActions = new GraphRendererActions(this);

        createImageToFileChooser();
    }

    /**
    *  Method to return the preferred GLCapabilities for this OpenGL canvas.
    *  Has to be static to be used in the GLCanvas constructor.
    */
    public static GLCapabilities getCaps()
    {
        GLCapabilities caps = new GLCapabilities(GLProfile.getDefault());
        caps.setAccumBlueBits(16);
        caps.setAccumGreenBits(16);
        caps.setAccumRedBits(16);
        caps.setDoubleBuffered(true);
        caps.setHardwareAccelerated(true);

        if ( NORMAL_QUALITY_ANTIALIASING.get() || HIGH_QUALITY_ANTIALIASING.get() )
        {
            caps.setSampleBuffers(true); // for FSAA
            caps.setNumSamples(HIGH_QUALITY_ANTIALIASING.get() ? 4 : 2); // for FSAA, 4 buffers to be used
        }

        return caps;
    }

    /**
    *  Creates the imageToFile Chooser UI.
    */
    private void createImageToFileChooser()
    {
        fileNameExtensionFilterPNG = new FileNameExtensionFilter("Save as a PNG File", "png");
        fileNameExtensionFilterJPG = new FileNameExtensionFilter("Save as a JPG File", "jpg");
        imageToFileChooser = new JFileChooser();
        imageToFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        imageToFileChooser.setFileFilter(fileNameExtensionFilterJPG);
        imageToFileChooser.setFileFilter(fileNameExtensionFilterPNG);
    }

    /**
    *  Requests window focus for the GLCanvas.
    */
    @Override
    public void requestFocus()
    {
        this.requestFocusInWindow();
        // requestFocus();
    }

    // Boolean returns true if event can't be performed.
    private boolean isEventNotAllowed()
    {
        return !( layoutFrame.getCursor().equals(BIOLAYOUT_NORMAL_CURSOR) || layoutFrame.getCursor().equals(BIOLAYOUT_MOVE_CURSOR) );
    }

    /**
    *  Prepares the high quality rendering.
    */
    public void prepareHighQualityRendering(GL2 gl)
    {
        if (gl.isExtensionAvailable("GL_ARB_multisample"))
        {
            gl.glEnable(GL_MULTISAMPLE);
        }

        gl.glEnable(GL_POINT_SMOOTH);
        gl.glEnable(GL_LINE_SMOOTH);
        gl.glEnable(GL_POLYGON_SMOOTH);

        gl.glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
        gl.glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        gl.glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);
        gl.glHint(GL_FOG_HINT, GL_NICEST);
        if (gl.isExtensionAvailable("GL_SGIS_generate_mipmap"))
        {
            gl.glHint(GL_GENERATE_MIPMAP_HINT, GL_NICEST);
        }
        if (GL_IS_NVIDIA && USE_SHADERS_PROCESS) gl.glHint(GL_FRAGMENT_SHADER_DERIVATIVE_HINT, GL_NICEST); // warning, the AMD/ATI driver does not like this setting, and it's only for OpenGL 2.0 and above!
    }

    /**
    *  Prepares the low quality rendering.
    */
    public void prepareLowQualityRendering(GL2 gl)
    {
        if (gl.isExtensionAvailable("GL_ARB_multisample"))
        {
            gl.glDisable(GL_MULTISAMPLE);
        }

        gl.glDisable(GL_POINT_SMOOTH);
        gl.glDisable(GL_LINE_SMOOTH);
        gl.glDisable(GL_POLYGON_SMOOTH);

        gl.glHint(GL_POINT_SMOOTH_HINT, GL_FASTEST);
        gl.glHint(GL_LINE_SMOOTH_HINT, GL_FASTEST);
        gl.glHint(GL_POLYGON_SMOOTH_HINT, GL_FASTEST);
        gl.glHint(GL_FOG_HINT, GL_FASTEST);
        if (gl.isExtensionAvailable("GL_SGIS_generate_mipmap"))
        {
            gl.glHint(GL_GENERATE_MIPMAP_HINT, GL_FASTEST);
        }
        if (GL_IS_NVIDIA && USE_SHADERS_PROCESS) gl.glHint(GL_FRAGMENT_SHADER_DERIVATIVE_HINT, GL_FASTEST); // warning, the AMD/ATI driver does not like this setting, and it's only for OpenGL 2.0 and above!
    }

    /**
    *  Prepares the background color.
    *  Package access for GraphRenderer2D & GraphRenderer3D.
    */
    void prepareBackgroundColor()
    {
        BACKGROUND_COLOR_ARRAY[0] = BACKGROUND_COLOR.get().getRed()   / 255.0f;
        BACKGROUND_COLOR_ARRAY[1] = BACKGROUND_COLOR.get().getGreen() / 255.0f;
        BACKGROUND_COLOR_ARRAY[2] = BACKGROUND_COLOR.get().getBlue()  / 255.0f;

        if (TRIPPY_BACKGROUND.get() && !takeHighResScreenshot) colorCycle(BACKGROUND_COLOR_ARRAY);
    }

    /**
    *  Prepares the profile mode background.
    *  Package access for GraphRenderer2D & GraphRenderer3D.
    */
    private void prepareProfileModeBackgroundAndFont()
    {
        renderProfileModeBackgroundTexture = TextureProducer.createTextureFromBufferedImage(RENDER_PROFILE_MODE_BACKGROUND_IMAGE, qualityRendering);

        profileModeTextRenderer = new TextRenderer(messagesFont, qualityRendering, false);
        profileModeTextRenderer.setUseVertexArrays(USE_VERTEX_ARRAYS_FOR_OPENGL_RENDERER);
    }

    /**
    *  Prepares the animation spectrum texture.
    *  Package access for GraphRenderer2D & GraphRenderer3D.
    */
    void prepareAnimationSpectrumTexture(GL2 gl)
    {
        animationSpectrumImage = ( ANIMATION_USER_SPECTRUM_IMAGE_FILE.isEmpty() ) ? ANIMATION_DEFAULT_SPECTRUM_IMAGES.getImage(ANIMATION_DEFAULT_SPECTRUM_IMAGE_FILES[ANIMATION_CHOSEN_DEFAULT_SPECTRUM_IMAGE_FILE_INDEX])
                                                                                  : loadImage(ANIMATION_USER_SPECTRUM_IMAGE_FILE, false);
        if (USE_SHADERS_PROCESS)
            animationSpectrumTexture = loadTextureFromImage(gl, animationSpectrumTexture, animationSpectrumImage, ACTIVE_TEXTURE_UNIT_FOR_ANIMATION_SPECTRUM_2D_TEXTURE);
    }

    /**
    *  Draws the profile mode.
    *  Package access for GraphRenderer2D & GraphRenderer3D.
    */
    void drawProfileMode(GL2 gl, int width, int height, String profileModeText,  boolean isScreenSaver)
    {
        if (DEBUG_BUILD) println("Graph drawProfileMode()");

        String[] allMessages = {
                                 profileModeText,
                                 "Resol:    " + Integer.toString(width) + " x " + Integer.toString(height),
                                 (!isScreenSaver) ? "Color Depth: " + (fullscreen ? Integer.toString(depth) : "n/a") : "",
                                 (!isScreenSaver) ? "Refresh Rate: " + (fullscreen ? Integer.toString(rate) : "n/a") : "",
                                 (!isScreenSaver) ? "Buffer Mode: DB" : "",
                                 "MultiCore: " + (USE_MULTICORE_PROCESS ? "ON" : "OFF"),
                                 "VSynch: " + (USE_VSYNCH.get() ? "ON" : "OFF"),
                                 "Target  FPS:  " + graphRendererThreadUpdater.getTargetFPS(),
                                 "Current FPS: " + graphRendererThreadUpdater.getAverageFPS(),
                                 (graphRendererThreadUpdater.getIsUsingFrameSkip() && !animationRender) ? "FrameSkips: " + Integer.toString( graphRendererThreadUpdater.getMaxFrameSkips() ) : ""
                               };

        double maxStringWidth = 0.0;
        double maxStringHeight = 0.0;
        Rectangle2D rect = null;
        int screensaverIndex = 0;
        for (int i = 0; i < allMessages.length; i++)
        {
            rect = profileModeTextRenderer.getBounds(allMessages[i]);
            if (rect.getWidth() > maxStringWidth)
                maxStringWidth = rect.getWidth();
            if (rect.getHeight() > maxStringHeight)
                maxStringHeight = rect.getHeight();

            if ( isScreenSaver && !allMessages[i].isEmpty() )
                screensaverIndex++;
        }

        gl.glLoadIdentity(); // profile mode should always be in the same place in OpenGL canvas, no need to do any transformations with it, so loading the identity matrix
        drawTexture(gl, renderProfileModeBackgroundTexture, START_COORD_TO_RENDER_BACKGROUND_TEXTURE, START_COORD_TO_RENDER_BACKGROUND_TEXTURE, (int)maxStringWidth + START_COORD_TO_RENDER_BACKGROUND_TEXTURE, (int)(HEIGHT_RATIO_BETWEEN_LINES * maxStringHeight * ( (!isScreenSaver) ? allMessages.length : screensaverIndex ) + 0.5 * maxStringHeight) + START_COORD_TO_RENDER_BACKGROUND_TEXTURE);

        float r = Color.CYAN.getRed()   / 255.0f;
        float g = Color.CYAN.getGreen() / 255.0f;
        float b = Color.CYAN.getBlue()  / 255.0f;
        float textAlpha = 1.0f;

        int coordYToRender = START_COORD_TO_RENDER_BACKGROUND_TEXTURE + (int)(HEIGHT_RATIO_BETWEEN_LINES * maxStringHeight / 4);
        profileModeTextRenderer.setColor(r, g, b, textAlpha);
        profileModeTextRenderer.beginRendering(width, height);
        for (int i = 0; i < allMessages.length; i++)
        {
            if ( !allMessages[i].isEmpty() )
            {
                coordYToRender += (HEIGHT_RATIO_BETWEEN_LINES * maxStringHeight);
                drawString2DTexture(gl, height, profileModeTextRenderer, allMessages[i], START_COORD_TO_RENDER_BACKGROUND_TEXTURE + START_COORD_TO_RENDER_BACKGROUND_TEXTURE / 2, coordYToRender);

                if (i == 0)
                    coordYToRender += (0.3 * maxStringHeight);
            }
        }
        profileModeTextRenderer.endRendering();
        profileModeTextRenderer.flush();
    }

    /**
    *  Draws the OS compatible profile mode.
    *  Package access for GraphRenderer2D & GraphRenderer3D.
    */
    void drawOSCompatibleProfileMode(GL2 gl)
    {
        if (DEBUG_BUILD) println("Graph drawOSCompatibleProfileMode()");

        String legend = "FPS: " + graphRendererThreadUpdater.getAverageFPS();
        Color legendStringColor = null;
        gl.glLoadIdentity(); // profile mode should always be in the same place in OpenGL canvas, no need to do any transformations with it, so loading the identity matrix

        if (USE_SHADERS_PROCESS)
        {
            legendStringColor = Color.CYAN;
            int maxStringWidth = GLUT.glutBitmapLength(LEGENDS_OPENGL_FONT_TYPE, legend);
            drawTexture(gl, renderProfileModeBackgroundTexture, START_COORD_TO_RENDER_BACKGROUND_TEXTURE, START_COORD_TO_RENDER_BACKGROUND_TEXTURE, maxStringWidth + START_COORD_TO_RENDER_BACKGROUND_TEXTURE, (int)(2.7 * START_COORD_TO_RENDER_BACKGROUND_TEXTURE));
        }
        else
        {
            legendStringColor = Color.BLACK;
        }

        // old-fashioned GLUT-based FPS font rendering
        float r = legendStringColor.getRed()   / 255f;
        float g = legendStringColor.getGreen() / 255f;
        float b = legendStringColor.getBlue()  / 255f;
        float textAlpha = 0.5f;

        gl.glColor4f(r, g, b, textAlpha);
        gl.glRasterPos2f(START_COORD_TO_RENDER_BACKGROUND_TEXTURE + START_COORD_TO_RENDER_BACKGROUND_TEXTURE / 2, 3 * START_COORD_TO_RENDER_BACKGROUND_TEXTURE);
        GLUT.glutBitmapString(LEGENDS_OPENGL_FONT_TYPE, legend);
    }

    /**
    *  Draws the animation time block.
    *  Package access for GraphRenderer2D & GraphRenderer3D.
    */
    void drawAnimationCurrentTick(GL2 gl, long tick)
    {
        if (DEBUG_BUILD) println("Graph drawAnimationCurrentTick()");

        String legend = "Current " + ( DATA_TYPE.equals(DataTypes.EXPRESSION) ? "Entity : " : "TimeBlock: ") + tick + " / " + TOTAL_NUMBER_OF_ANIMATION_TICKS + ( DATA_TYPE.equals(DataTypes.EXPRESSION) ? " (" + layoutFrame.getExpressionData().getColumnName( (int)tick - 1 ) + ")" : "" );
        Color legendStringColor = null;
        gl.glLoadIdentity(); // profile mode should always be in the same place in OpenGL canvas, no need to do any transformations with it, so loading the identity matrix

        if (USE_SHADERS_PROCESS)
        {
            legendStringColor = Color.CYAN;
            int maxStringWidth = GLUT.glutBitmapLength(LEGENDS_OPENGL_FONT_TYPE, legend);
            drawTexture(gl, renderProfileModeBackgroundTexture, START_COORD_TO_RENDER_BACKGROUND_TEXTURE, START_COORD_TO_RENDER_BACKGROUND_TEXTURE + height - 40, maxStringWidth + START_COORD_TO_RENDER_BACKGROUND_TEXTURE, (int)(2.7 * START_COORD_TO_RENDER_BACKGROUND_TEXTURE));
        }
        else
        {
            legendStringColor = Color.BLACK;
            gl.glEnable(GL_COLOR_LOGIC_OP);
            gl.glLogicOp(GL_EQUIV);
        }

        // old-fashioned GLUT-based FPS font rendering
        float r = legendStringColor.getRed()   / 255.0f;
        float g = legendStringColor.getGreen() / 255.0f;
        float b = legendStringColor.getBlue()  / 255.0f;
        float textAlpha = 0.5f;

        gl.glColor4f(r, g, b, textAlpha);
        gl.glRasterPos2f(START_COORD_TO_RENDER_BACKGROUND_TEXTURE + START_COORD_TO_RENDER_BACKGROUND_TEXTURE / 2, 3 * START_COORD_TO_RENDER_BACKGROUND_TEXTURE + height - 40);
        GLUT.glutBitmapString(LEGENDS_OPENGL_FONT_TYPE, legend);

        if (!USE_SHADERS_PROCESS)
            gl.glDisable(GL_COLOR_LOGIC_OP);
    }

    /**
    *  Customizes the given node name.
    */
    public static String customizeNodeName(String originalNodeName)
    {
        if ( CUSTOMIZE_NODE_NAMES_SHOW_FULL_NAME.get() )
            return originalNodeName;
        else
        {
            String partialDelimitedName = ( !CUSTOMIZE_NODE_NAMES_DELIMITER.get().isEmpty() ) ? originalNodeName.split(CUSTOMIZE_NODE_NAMES_DELIMITER.get() + "+")[0] : originalNodeName;
            if ( CUSTOMIZE_NODE_NAMES_SHOW_PARTIAL_NAME_LENGTH.get() )
            {
                if ( CUSTOMIZE_NODE_NAMES_SHOW_PARTIAL_NAME_LENGTH_NUMBER_OF_CHARACTERS.get() >= partialDelimitedName.length() )
                    return partialDelimitedName;
                else
                    return partialDelimitedName.substring( 0, CUSTOMIZE_NODE_NAMES_SHOW_PARTIAL_NAME_LENGTH_NUMBER_OF_CHARACTERS.get() );
            }
            else
                return partialDelimitedName;
        }
    }

    /**
    *  Draws the node name background legend.
    *  Package access for GraphRenderer2D & GraphRenderer3D.
    */
    void drawNodeNameBackgroundLegend(GL2 gl, GraphNode node, String nodeName)
    {
        if (nodeName.isEmpty())
        {
            return;
        }

        float[] colorArray = new float[]{ 1.0f, 1.0f, 1.0f, 0.5f }; // default is white color with 0.5 alpha
        if (CUSTOMIZE_NODE_NAMES_NAME_RENDERING_TYPE.get() == 2)
            node.getColor().getRGBComponents(colorArray);

        // + 2 for GLUT public static variables ordering for excluding STROKE_ROMAN/STROKE_MONO_ROMAN
        int textWidth = GLUT.glutBitmapLength(NODE_NAMES_OPENGL_FONT_TYPE.ordinal() + 2, nodeName);
        int textHeight = OPENGL_FONT_HEIGHTS[NODE_NAMES_OPENGL_FONT_TYPE.ordinal()];
        int textDescender = OPENGL_FONT_DESCENDERS[NODE_NAMES_OPENGL_FONT_TYPE.ordinal()];

        final int BORDER_WIDTH = 1;
        int backgroundWidth = textWidth + (2 * BORDER_WIDTH);
        int backgroundHeight = textHeight + (2 * BORDER_WIDTH);
        ByteBuffer nodeNamebackgroundLegendImageBuffer = ByteBuffer.allocate(4 * backgroundWidth * backgroundHeight);
        for (int i = 0; i < nodeNamebackgroundLegendImageBuffer.capacity(); i += 4)
        {
            nodeNamebackgroundLegendImageBuffer.put( (byte)(colorArray[0] * 255.0f) );
            nodeNamebackgroundLegendImageBuffer.put( (byte)(colorArray[1] * 255.0f) );
            nodeNamebackgroundLegendImageBuffer.put( (byte)(colorArray[2] * 255.0f) );
            nodeNamebackgroundLegendImageBuffer.put( (byte)(colorArray[3] * 255.0f));
        }
        nodeNamebackgroundLegendImageBuffer.rewind();
        gl.glDrawPixels(backgroundWidth, backgroundHeight, GL_RGBA,
                GL_UNSIGNED_BYTE, nodeNamebackgroundLegendImageBuffer);

        // This isn't actually drawing anything; it's just being used to offset the raster position so that
        // the text is rendered in the middle of the background. This is done because the text is rendered
        // as part of the usual nodes display list using glRasterPos and perspective projection, i.e. not an
        // orthographic projection. In light of that, this is the sanest way I can think of to offset in
        // screen space. SNAFU.
        gl.glBitmap(0, 0, 0, 0, BORDER_WIDTH, textDescender + BORDER_WIDTH, nodeNamebackgroundLegendImageBuffer);
    }

    /**
    *  Increases the node size.
    *  Package access for GraphRenderer2D & GraphRenderer3D.
    */
    void increaseNodeSize(boolean increase, boolean isManualChange)
    {
        HashSet<GraphNode> nodes = selectionManager.getSelectedNodes();
        if ( nodes.isEmpty() )
            nodes = visibleNodes;

        for (GraphNode node : nodes)
            node.setMinMaxNodeSize(node.getNodeSize() + ( (increase) ? 1 : -1) * node.getNodeSize() * AMOUNT_OF_NODE_SIZE_INCREASE, isManualChange);
    }

    /**
    *  Chooses the initial screenshots' abstract path.
    */
    private void chooseInitialAbstractPath(String fileNameRemark)
    {
        String screenshotFileName = ( USE_INSTALL_DIR_FOR_SCREENSHOTS.get() ) ?
                Path.combine(DataFolder.get(), SCREENSHOTS_DIRECTORY) :
                layoutFrame.getFileNameAbsolutePathLoaded() + SCREENSHOTS_DIRECTORY;
        chooseScreenshotDirectoryName(screenshotFileName);
        screenshotFileName += "/" + layoutFrame.getFileNameLoaded() + " " + fileNameRemark + " at " + getCalendarInformation();
        imageToFileChooser.setSelectedFile( new File(screenshotFileName) );
    }

    /**
    *  Sets the screenshots' subdirectory name.
    */
    private void chooseScreenshotDirectoryName(String dirName)
    {
        File screenshotDir = new File(dirName);
        if ( !screenshotDir.isDirectory() ) screenshotDir.mkdir();
    }

    /**
    *  Sets the calendar information.
    */
    private String getCalendarInformation()
    {
        GregorianCalendar gregCalendar = new GregorianCalendar();

        String calendarYear = Integer.toString(gregCalendar.get(Calendar.YEAR));

        String calendarMonth = Integer.toString((gregCalendar.get(Calendar.MONTH) + 1));
        if (calendarMonth.length() == 1) calendarMonth = "0" + calendarMonth;

        String calendarDate = Integer.toString(gregCalendar.get(Calendar.DATE));
        if (calendarDate.length() == 1) calendarDate = "0" + calendarDate;

        String calendarHourOfDay = Integer.toString(gregCalendar.get(Calendar.HOUR_OF_DAY));
        if (calendarHourOfDay.length() == 1) calendarHourOfDay = "0" + calendarHourOfDay;

        String calendarMinute = Integer.toString(gregCalendar.get(Calendar.MINUTE));
        if (calendarMinute.length() == 1) calendarMinute = "0" + calendarMinute;

        String calendarSecond = Integer.toString(gregCalendar.get(Calendar.SECOND));
        if (calendarSecond.length() == 1) calendarSecond = "0" + calendarSecond;

        StringBuilder calendarInformation = new StringBuilder();

        calendarInformation.append(calendarYear).append("-");
        calendarInformation.append(calendarMonth).append("-");
        calendarInformation.append(calendarDate).append(" ");
        calendarInformation.append(calendarHourOfDay).append(" ");
        calendarInformation.append(calendarMinute).append(" ");
        calendarInformation.append(calendarSecond);

        return calendarInformation.toString();
    }

    /**
    *  Returns an array for color cycling. Package restricted access.
    */
    public void colorCycle(float[] color)
    {
        int red   = (int)(color[0] * 255.0f);
        int green = (int)(color[1] * 255.0f);
        int blue  = (int)(color[2] * 255.0f);

        if (red == 255 || red == 0)
            decreaseRed = !decreaseRed;

        if (decreaseRed)
            red -= org.BioLayoutExpress3D.StaticLibraries.Random.getRandomRange(0, 2);
        else
            red += org.BioLayoutExpress3D.StaticLibraries.Random.getRandomRange(0, 2);

        if (red > 255) red = 255;
        else if (red < 0) red = 0;

        if (green == 255 || green == 0)
            decreaseGreen = !decreaseGreen;

        if (decreaseGreen)
            green -= org.BioLayoutExpress3D.StaticLibraries.Random.getRandomRange(0, 2);
        else
            green += org.BioLayoutExpress3D.StaticLibraries.Random.getRandomRange(0, 2);

        if (green > 255) green = 255;
        else if (green < 0) green = 0;

        if (blue == 255 || blue == 0)
            decreaseBlue = !decreaseBlue;

        if (decreaseBlue)
            blue -= org.BioLayoutExpress3D.StaticLibraries.Random.getRandomRange(0, 2);
        else
            blue += org.BioLayoutExpress3D.StaticLibraries.Random.getRandomRange(0, 2);

        if (blue > 255) blue = 255;
        else if (blue < 0) blue = 0;

        color[0] = (float)red   / 255.0f;
        color[1] = (float)green / 255.0f;
        color[2] = (float)blue  / 255.0f;
    }

    /**
    *  Gets the Lathe3D shape angle increment.
    */
    public Lathe3DShapeAngleIncrements getLathe3DShapeAngleIncrement(int tesselation)
    {
        Lathe3DShapeAngleIncrements[] allLathe3DShapeAngleIncrements = Lathe3DShapeAngleIncrements.values();
        int index = 0;
        if (tesselation <= 30)
        {
            // leave last 4 indices for manual tesselation usage
            index = (int)( ( (tesselation / (float)NODE_TESSELATION_MAX_VALUE ) * (allLathe3DShapeAngleIncrements.length - 4) ) - 1 );
            if (index < 0) index = 0;
        }
        else // if (tesselation > 31)
        {
            index = (allLathe3DShapeAngleIncrements.length - 4) + ( tesselation - (NODE_TESSELATION_MAX_VALUE - 5) ) / 5;
            if ( index > (allLathe3DShapeAngleIncrements.length - 1) ) index = allLathe3DShapeAngleIncrements.length - 1;
        }

        return correctLathe3DShapeAngleIncrement(allLathe3DShapeAngleIncrements[index]);
    }

    /**
    *  Corrects the Lathe3D shape angle increment.
    */
    private Lathe3DShapeAngleIncrements correctLathe3DShapeAngleIncrement(Lathe3DShapeAngleIncrements lathe3DShapeAngleIncrement)
    {
        return lathe3DShapeAngleIncrement.equals(_180) ? _120 : lathe3DShapeAngleIncrement;
    }

    /**
    *  Loads the texture from a given image in a given texture unit.
    */
    private Texture loadTextureFromImage(GL2 gl, Texture texture, BufferedImage image, int textureUnit)
    {
        if (textureUnit != 0 && gl.isFunctionAvailable("glActiveTexture"))
        {
            gl.glActiveTexture(GL_TEXTURE0 + textureUnit);
        }

        disposeTexture(gl, texture);
        texture = TextureProducer.createTextureFromBufferedImage(image, qualityRendering);

        if (textureUnit != 0 && gl.isFunctionAvailable("glActiveTexture"))
        {
            gl.glActiveTexture(GL_TEXTURE0);
        }

        return texture;
    }

    /**
    *  Loads an image.
    */
    private BufferedImage loadImage(String resourceName, boolean isLocalResource)
    {
        BufferedImage image = null;

        try
        {
            image = loadImageFromURL( getResource(resourceName, isLocalResource) );
            ImageUtil.flipImageVertically(image); // used for proper spherical mapping, cone is reversed unfortunately
        }
        catch (MalformedURLException malExc)
        {
            if (DEBUG_BUILD) org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.print("MalformedURLException in Graph.loadImage() method\n" + malExc.getMessage());

            image = createNullPointerBufferedImage();
        }

        if (DEBUG_BUILD) reportTransparency( resourceName, image.getColorModel().getTransparency() );

        return image;
    }

    /**
    *  Gets a URL from the resource.
    */
    private URL getResource(String filename, boolean isLocalResource) throws MalformedURLException
    {
        return ( (isLocalResource) ? Graph.class.getResource(filename) : new URL("file", "localhost", filename) );
    }

    /**
    *  Reports image transparency.
    */
    private void reportTransparency(String resourceName, int transparency)
    {
        if (DEBUG_BUILD) org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.print(resourceName + " transparency: ");

        switch(transparency)
        {
            case Transparency.OPAQUE:
                if (DEBUG_BUILD) org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.print("opaque");
                break;
            case Transparency.BITMASK:
                if (DEBUG_BUILD) org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.print("bitmask");
                break;
            case Transparency.TRANSLUCENT:
                if (DEBUG_BUILD) org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.print("translucent");
                break;
            default:
                if (DEBUG_BUILD) org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.print("unknown");
                break;
        }
    }

    /**
    *  Disposes the node texture.
    */
    private void disposeTexture(GL2 gl, Texture texture)
    {
        texture = null;
    }

    /**
    *  Prepares the node texture.
    */
    public Texture prepareNodeTexture(GL2 gl, Texture nodeTexture)
    {
        BufferedImage image = null;
        boolean flushAtEnd = false;
        if ( USER_SURFACE_IMAGE_FILE.isEmpty() && (TEXTURE_CHOSEN.get() == 0) )
        {
            image = cloneImage(BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE);
            ImageUtil.flipImageVertically(image);
            flushAtEnd = true;
        }
        else if (USER_SURFACE_IMAGE_FILE.isEmpty() && (TEXTURE_CHOSEN.get() > 0))
        {
            image = DEFAULT_SURFACE_IMAGES.getImage(DEFAULT_SURFACE_IMAGE_FILES[TEXTURE_CHOSEN.get()]);
        }
        else if ( !USER_SURFACE_IMAGE_FILE.isEmpty() )
        {
            image = loadImage(USER_SURFACE_IMAGE_FILE, false);
        }

        nodeTexture = loadTextureFromImage(gl, nodeTexture, image, ACTIVE_TEXTURE_UNIT_FOR_2D_TEXTURE);
        gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        if (flushAtEnd)
            image.flush();
        image = null;

        return nodeTexture;
    }

    /**
    *  Creates a grayscale color based on a given RGB color.
    */
    public void createGrayScaleColor(float[] color)
    {
        color[0] = color[1] = color[2] = (0.299f * color[0] + 0.587f * color[1] + 0.114f * color[2]);
    }

    /**
    *  Chooses the anaglyph glasses color mask.
    */
    public void chooseAnaglyphGlassesColorMask(GL2 gl, boolean leftOrRightEyeMask)
    {
        if ( GRAPH_ANAGLYPH_GLASSES_TYPE.equals(RED_BLUE) )
        {
            if (leftOrRightEyeMask)
                gl.glColorMask(true, false, false, true);
            else
                gl.glColorMask(false, false, true, true);
        }
        else if ( GRAPH_ANAGLYPH_GLASSES_TYPE.equals(RED_GREEN) )
        {
            if (leftOrRightEyeMask)
                gl.glColorMask(true, false, false, true);
            else
                gl.glColorMask(false, true, false, true);
        }
        else if ( GRAPH_ANAGLYPH_GLASSES_TYPE.equals(RED_CYAN) )
        {
            if (leftOrRightEyeMask)
                gl.glColorMask(true, false, false, true);
            else
                gl.glColorMask(false, true, true, true);
        }
        else if ( GRAPH_ANAGLYPH_GLASSES_TYPE.equals(BLUE_RED) )
        {
            if (leftOrRightEyeMask)
                gl.glColorMask(false, false, true, true);
            else
                gl.glColorMask(true, false, false, true);
        }
        else if ( GRAPH_ANAGLYPH_GLASSES_TYPE.equals(GREEN_RED) )
        {
            if (leftOrRightEyeMask)
                gl.glColorMask(false, true, false, true);
            else
                gl.glColorMask(true, false, false, true);
        }
        else // if ( GRAPH_ANAGLYPH_GLASSES_TYPE.equals(CYAN_RED) )
        {
            if (leftOrRightEyeMask)
                gl.glColorMask(false, true, true, true);
            else
                gl.glColorMask(true, false, false, true);
        }
    }

    /**
    *  Sets the cameras's IntraOcularDistance & frustum shift variables.
    */
    public void setCamerasIntraOcularDistanceAndFrustumShift(double intraOcularDistance)
    {
        LEFT_EYE_CAMERA.setIntraOcularDistanceAndFrustumShift(intraOcularDistance);
        RIGHT_EYE_CAMERA.setIntraOcularDistanceAndFrustumShift(intraOcularDistance);
    }

    /**
    *  Performs the burst layout iterations.
    */
    private void burstLayoutIterations()
    {
        Thread burstThread = new Thread( new Runnable()
        {
            @Override
            public void run()
            {
                boolean hasChangedYedStyleRenderingForGraphmlFilesValue = false;
                if ( nc.getIsGraphml() && YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() )
                {
                    int option = JOptionPane.showConfirmDialog(layoutFrame, "The Burst Layout Iterations feature is only available for non-yEd style, layout rendering.\nNote that the yEd style node positions will also be resetted.\n\nDo you want to reset yEd node positions,\ntemporarily enable layout view and perform Burst Layout Iterations ?", "Burst Layout Iterations Question", JOptionPane.YES_NO_OPTION);
                    if ( hasChangedYedStyleRenderingForGraphmlFilesValue = (option == JOptionPane.YES_OPTION) )
                    {
                        YED_STYLE_RENDERING_FOR_GPAPHML_FILES.set(false);
                        updateAllDisplayLists();
                    }
                    else
                        return;
                }

                if (animationRender)
                    layoutFrame.getLayoutAnimationControlDialog().stopAnimation(true);

                if ( nc.getIsGraphml() )
                {
                    if (hasInitiated2DNodeDragging)
                        nc.getGraphmlNetworkContainer().resetAllGraphmlNodesMapCoords();
                    nc.getGraphmlNetworkContainer().resetAllGraphmlNodesMapCoordsDepthZ();
                }

                performBurstIterations();

                if (hasChangedYedStyleRenderingForGraphmlFilesValue)
                {
                    YED_STYLE_RENDERING_FOR_GPAPHML_FILES.set(true);
                    updateAllDisplayLists();
                }

                if (hasInitiated2DNodeDragging)
                {
                    hasInitiated2DNodeDragging = false;
                    setEnabledUndoNodeDragging(false);
                    setEnabledRedoNodeDragging(false);

                    updateAllDisplayLists();
                }
            }
        }, "burstLayoutIterations" );

        burstThread.setPriority(Thread.NORM_PRIORITY);
        burstThread.start();
    }

    private void performBurstIterations()
    {
        if (GRAPH_LAYOUT_ALGORITHM.get() != GraphLayoutAlgorithm.FRUCHTERMAN_RHEINGOLD)
        {
            if (JOptionPane.showConfirmDialog(layoutFrame,
                    "Burst Layout Iterations can only be performed using the " +
                    "Fruchterman-Rheingold algorithm. Do you wish to proceed?",
                    "Are You Sure?", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
            {
                return;
            }
        }

        boolean performBurstIterations = true;
        boolean hasChangedShowLayoutIterationsValue = false;

        if (!SHOW_LAYOUT_ITERATIONS.get())
        {
            int option = JOptionPane.showConfirmDialog(layoutFrame,
                    "The Show Layout Iterations option is turned off.\n" +
                    "Do you really want to perform animated Burst Layout Iterations ?",
                    "Burst Layout Iterations Question", JOptionPane.YES_NO_OPTION);
            if (performBurstIterations = hasChangedShowLayoutIterationsValue = (option == JOptionPane.YES_OPTION))
            {
                SHOW_LAYOUT_ITERATIONS.set(true);
            }
        }

        if (performBurstIterations)
        {
            // to do relayout with animation works only if tiling is off, temporarily disabling it below
            boolean originalTilingValue = false;
            if (SHOW_LAYOUT_ITERATIONS.get())
            {
                originalTilingValue = TILED_LAYOUT.get();
                TILED_LAYOUT.set(false);
            }

            nc.relayout(GraphLayoutAlgorithm.FRUCHTERMAN_RHEINGOLD);
            burstUpdate();

            if (SHOW_LAYOUT_ITERATIONS.get())
            {
                TILED_LAYOUT.set(originalTilingValue);
            }

            if (hasChangedShowLayoutIterationsValue)
            {
                SHOW_LAYOUT_ITERATIONS.set(false);
            }
        }
    }

    private void burstUpdate()
    {
        for ( GraphNode node: getGraphNodes() )
        {
            if (hasInitiated2DNodeDragging)
                node.clearPointStacks();

            if (node.getNodeID() >= 0) // for non-group nodes appearing on the graph, having a nodeID >= 0
                node.burstUpdate();
        }
    }

    /**
    *  Initiates the screenshot taking process.
    *  Package access for GraphRenderer2D & GraphRenderer3D.
    */
    void initiateTakeScreenShotProcess(final boolean doHighResScreenShot)
    {
        saveScreenshotFile = saveImageToFile(this, (doHighResScreenShot) ? "Render High Resolution Graph Image To File As" : "Render Graph Image To File As", (doHighResScreenShot) ? "high res 3D shot" : "3D shot");
        if (saveScreenshotFile != null)
        {
            Thread runHighResActionThread = new Thread( new Runnable()
            {
                @Override
                public void run()
                {
                    takeScreenShotProcess(doHighResScreenShot);
                }
            } );

            runHighResActionThread.setPriority(Thread.NORM_PRIORITY);
            runHighResActionThread.start();
        }
    }

    /**
    *  Saves the current OpenGL buffer image to a file.
    */
    public File saveImageToFile(Component component, String dialogTitle, String fileNameRemark)
    {
        int dialogReturnValue = 0;
        boolean doSaveFile = false;
        File saveScreenshotFileToReturn = null;

        imageToFileChooser.setDialogTitle(dialogTitle); // ? "Save High Resolution Image To File As" : "Save Image To File As" );

        if (INSTALL_DIR_FOR_SCREENSHOTS_HAS_CHANGED)
        {
            INSTALL_DIR_FOR_SCREENSHOTS_HAS_CHANGED = false;
            chooseInitialAbstractPath(fileNameRemark);
        }
        else
        {
            if (imageToFileChooser.getSelectedFile() == null)
            {
                chooseInitialAbstractPath(fileNameRemark);
            }
            else // get imageToFileChooser's previous abstract path
            {
                String screenshotFileName = imageToFileChooser.getSelectedFile().getAbsolutePath();
                screenshotFileName = screenshotFileName.substring(0, screenshotFileName.lastIndexOf( System.getProperty("file.separator") ) + 1);
                chooseScreenshotDirectoryName(screenshotFileName);
                screenshotFileName += "/" + layoutFrame.getFileNameLoaded() + " " + fileNameRemark + " at " + getCalendarInformation();
                imageToFileChooser.setSelectedFile( new File(screenshotFileName) );
            }
        }

        if (imageToFileChooser.showSaveDialog(component) == JFileChooser.APPROVE_OPTION)
        {
            // default file extension will be the PNG file format
            String fileExtension = ( imageToFileChooser.getFileFilter().equals(fileNameExtensionFilterPNG) ) ? fileNameExtensionFilterPNG.getExtensions()[0] : fileNameExtensionFilterJPG.getExtensions()[0];
            String fileName = imageToFileChooser.getSelectedFile().getAbsolutePath();
            fileName = IOUtils.removeMultipleExtensions(fileName, fileExtension);
            saveScreenshotFileToReturn = new File(fileName + "." + fileExtension);

            if ( saveScreenshotFileToReturn.exists() )
            {
                // Do you want to overwrite
                dialogReturnValue = JOptionPane.showConfirmDialog(component, "This File Already Exists.\nDo you want to Overwrite it?", "This File Already Exists. Overwrite?", JOptionPane.YES_NO_CANCEL_OPTION);

                if (dialogReturnValue == JOptionPane.YES_OPTION)
                    doSaveFile = true;
            }
            else
            {
                doSaveFile = true;
            }
        }

        return (doSaveFile) ? saveScreenshotFileToReturn : null;
    }

    public void printGraph()
    {
        if (USE_NEW_DESKTOP_PRINTING_FEATURE)
        {
            try
            {
                File tempGraphFile = File.createTempFile("tempGraphFile", ".png");
                org.BioLayoutExpress3D.StaticLibraries.ImageProducer.writeBufferedImageToFile(getBufferedImage(), "png", tempGraphFile);
                InitDesktop.print(tempGraphFile);
                tempGraphFile.deleteOnExit();
            }
            catch (IOException ioExc)
            {
                if (DEBUG_BUILD) println("Problem with New Desktop Printing Feature Graph.printGraph():\n" + ioExc.getMessage());

                // fallback mechanism to old printing way
                layoutFrame.getLayoutPrintServices().setComponent( getBufferedImage() );
                layoutFrame.getLayoutPrintServices().print();
            }
        }
        else
        {
            layoutFrame.getLayoutPrintServices().setComponent( getBufferedImage() );
            layoutFrame.getLayoutPrintServices().print();
        }
    }

    public void clear()
    {
        graphNodes.clear();
        graphEdges.clear();
        visibleNodes.clear();
        visibleEdges.clear();

        selectionManager.clearAllSelection();
        selectionManager.getGroupManager().resetMode();
        selectionManager.getGroupManager().resetState();

        hasInitiated2DNodeDragging = false;
    }

    public void rebuildGraph()
    {
        clear();

        if (DEBUG_BUILD) println("Rebuilding Graph Now: " + nc.getNumberOfVertices() + " " + nc.getEdges().size());

        GraphNode firstGraphNode = null;
        GraphNode secondGraphNode = null;
        for ( Edge edge : nc.getEdges() )
        {
            if ( !graphNodes.containsKey( edge.getFirstVertex().getVertexID() ) )
            {
                firstGraphNode = new GraphNode( edge.getFirstVertex() );
                graphNodes.put(firstGraphNode.getNodeID(), firstGraphNode);
            }
            else
            {
                firstGraphNode = graphNodes.get( edge.getFirstVertex().getVertexID() );
            }

            if ( !graphNodes.containsKey( edge.getSecondVertex().getVertexID() ) )
            {
                secondGraphNode = new GraphNode( edge.getSecondVertex() );
                graphNodes.put(secondGraphNode.getNodeID(), secondGraphNode);
            }
            else
            {
                secondGraphNode = graphNodes.get( edge.getSecondVertex().getVertexID() );
            }

            GraphEdge graphEdge = new GraphEdge( firstGraphNode, secondGraphNode, edge, edge.getScaledWeight() );
            graphEdges.add(graphEdge);

            firstGraphNode.addEdge(graphEdge);
            secondGraphNode.addEdge(graphEdge);

            firstGraphNode.addNodeParent(secondGraphNode);
            secondGraphNode.addNodeChild(firstGraphNode);
        }

        visibleNodes.addAll( graphNodes.values() );
        visibleEdges.addAll(graphEdges);

        updateAllDisplayLists();
        if ( nc.getIsGraphml() ) CHANGE_GRAPHML_COMPONENT_CONTAINERS = true;
    }

    public void setEnabledUndoNodeDragging(boolean enabled)
    {
        graphRendererActions.getUndoNodeDraggingAction().setEnabled(hasInitiated2DNodeDragging && hasMoreUndoSteps() && enabled);
    }

    public void setEnabledRedoNodeDragging(boolean enabled)
    {
        graphRendererActions.getRedoNodeDraggingAction().setEnabled(hasInitiated2DNodeDragging && hasMoreRedoSteps()&& enabled);
    }

    public void setAnimationValues(boolean animationRenderValue, int entityOrTimeBlockToStartFrom)
    {
        animationRender = animationRenderValue;
        animationFrameCount = (animationRenderValue) ? ( (entityOrTimeBlockToStartFrom > 1) ? (int)( (entityOrTimeBlockToStartFrom - 1) * (FRAMERATE_PER_SECOND_FOR_ANIMATION / ANIMATION_TICKS_PER_SECOND) ) : 0 ) : 0;
        currentTick = 0;
    }

    public void setStepAnimation()
    {
        stepAnimation = true;
    }

    public int getCurrentTick()
    {
        return currentTick;
    }

    public void setReInitializeRendererMode(boolean reInitializeRendererMode)
    {
        this.reInitializeRendererMode = reInitializeRendererMode;
        currentGraphRenderer = (RENDERER_MODE_3D) ? graphRenderer3D : graphRenderer2D;
    }

    public void setGraphRendererThreadUpdaterTargetFPS()
    {
        if (graphRendererThreadUpdater != null)
            graphRendererThreadUpdater.setTargetFPS(USE_VSYNCH.get() ? targetFPS : noVynchTargetFPS);
    }

    public void recreateGraphEdges(Collection<GraphEdge> edges)
    {
        graphEdges = new HashSet<GraphEdge>(edges);
    }

    public void recreateVisibleNodes(Collection<GraphNode> nodes)
    {
        visibleNodes = new HashSet<GraphNode>(nodes);
    }

    public void recreateVisibleEdges(Collection<GraphEdge> edges)
    {
        visibleEdges = new HashSet<GraphEdge>(edges);
    }

    public HashMap<Integer, GraphNode> getGraphNodesMap()
    {
        return graphNodes;
    }

    public Collection<GraphNode> getGraphNodes()
    {
        return graphNodes.values();
    }

    public HashSet<GraphEdge> getGraphEdges()
    {
        return graphEdges;
    }

    public HashSet<GraphNode> getVisibleNodes()
    {
        return visibleNodes;
    }

    public HashSet<GraphEdge> getVisibleEdges()
    {
        return visibleEdges;
    }

    public SelectionManager getSelectionManager()
    {
        return selectionManager;
    }

    public GraphActions getGraphActions()
    {
        return graphActions;
    }

    public GraphRendererActions getGraphRendererActions()
    {
        return graphRendererActions;
    }

    /**
    *  Called by the JOGL2 glDrawable immediately after the OpenGL context is initialized.
    */

    //GLAutoDrawable creates the "primary rendering context".

    @Override
    public void init(GLAutoDrawable glDrawable)
    {
        /*
        * This demonstrates the use of the DebugGL composible pipeline.  In the
        * OpenGL C API, errors are indicated by setting an error code.  When the
        * DebugGL pipeline is enabled, the error code is checked after every call
        * to a GL method.  A GLException will be thrown if an error occurs, and
        * the GLException object will contain the error code.
        */
        // glDrawable.setGL( new DebugGL( glDrawable.getGL() ) );

        /*
        * This example demonstrates the use of the TraceGL composable pipeline.
        * When the TraceGL pipeline is in place, all OpenGL calls will cause
        * debug output to be sent to the given stream.
        * This case sends all debug output to System.out
        */
        // glDrawable.setGL( new TraceGL( glDrawable.getGL(), System.out) );

        width = glDrawable.getWidth();
        height = glDrawable.getHeight();

        if (width <= 0) width = 1;
        if (height <= 0) height = 1;

        GL2 gl = glDrawable.getGL().getGL2();

        GL_VENDOR_STRING = gl.glGetString(GL_VENDOR);
        GL_RENDERER_STRING = gl.glGetString(GL_RENDERER);
        GL_VERSION_STRING = gl.glGetString(GL_VERSION);
        GL_EXTENSIONS_STRINGS = gl.glGetString(GL_EXTENSIONS).split("\\s+");
        GL_IS_AMD_ATI = ( GL_VENDOR_STRING.contains("ATI") || GL_VENDOR_STRING.contains("AMD") );
        GL_IS_NVIDIA = GL_VENDOR_STRING.contains("NVIDIA");

        if (DEBUG_BUILD)
        {
            StringBuilder output = new StringBuilder();
            output.append("\n\nOpenGL Driver Capabilities:\n\n");
            output.append("GL_VENDOR:\t").append(GL_VENDOR_STRING).append("\n");
            output.append("GL_RENDERER:\t").append(GL_RENDERER_STRING).append("\n");
            output.append("GL_VERSION:\t").append(GL_VERSION_STRING).append("\n\n");
            output.append("\n").append(GL_EXTENSIONS_STRINGS.length).append(" available GL Extensions: " + "\n\n");
            for (String glExtension : GL_EXTENSIONS_STRINGS)
                output.append(glExtension).append("\n");
            println( output.toString() );
        }

        if (USE_VERTEX_ARRAYS_FOR_OPENGL_RENDERER)
        {
            int firstIndexOfDot = GL_VERSION_STRING.indexOf(".");
            float openGLVersion = Float.parseFloat( GL_VERSION_STRING.substring(0, firstIndexOfDot + 2) );
            if ( USE_VERTEX_ARRAYS_FOR_OPENGL_RENDERER = (openGLVersion >= MINIMUM_OPENGL_VERSION_FOR_VERTEX_ARRAYS) )
            {
                // initialize OpenGL Vertex Arrays support
                gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);
                gl.glEnableClientState(GL_NORMAL_ARRAY);
                gl.glEnableClientState(GL_VERTEX_ARRAY);
            }
        }

        if (USE_SHADERS_PROCESS)
        {
            int firstIndexOfDot = GL_VERSION_STRING.indexOf(".");
            float openGLVersion = Float.parseFloat( GL_VERSION_STRING.substring(0, firstIndexOfDot + 2) );
            if ( USE_SHADERS_PROCESS = ( (openGLVersion >= MINIMUM_OPENGL_VERSION_FOR_QUALITY_RENDERING_AND_SHADERS) || LoadNativeLibrary.isMacLionAndAbove() ) )
            {
                USE_330_SHADERS_PROCESS = (openGLVersion >= MINIMUM_OPENGL_VERSION_FOR_330_SHADERS);

                GL_SHADING_LANGUAGE_VERSION_STRING = gl.glGetString(GL_SHADING_LANGUAGE_VERSION);
                gl.glGetIntegerv(GL_MAX_DRAW_BUFFERS, OPENGL_INT_VALUE);
                GL_MAX_DRAW_BUFFERS_INTEGER = OPENGL_INT_VALUE.get(0);
                gl.glGetIntegerv(GL_MAX_COLOR_ATTACHMENTS, OPENGL_INT_VALUE);
                GL_MAX_COLOR_ATTACHMENTS_INTEGER = OPENGL_INT_VALUE.get(0);
                gl.glGetIntegerv(GL_AUX_BUFFERS, OPENGL_INT_VALUE);
                GL_AUX_BUFFERS_INTEGER = OPENGL_INT_VALUE.get(0);
                gl.glGetIntegerv(GL_MAX_TEXTURE_UNITS, OPENGL_INT_VALUE);
                GL_MAX_TEXTURE_UNITS_INTEGER = OPENGL_INT_VALUE.get(0);
                gl.glGetIntegerv(GL_MAX_VERTEX_ATTRIBS, OPENGL_INT_VALUE);
                GL_MAX_VERTEX_ATTRIBS_INTEGER = OPENGL_INT_VALUE.get(0);
                gl.glGetIntegerv(GL_MAX_VERTEX_UNIFORM_COMPONENTS, OPENGL_INT_VALUE);
                GL_MAX_VERTEX_UNIFORM_COMPONENTS_INTEGER = OPENGL_INT_VALUE.get(0);
                gl.glGetIntegerv(GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS, OPENGL_INT_VALUE);
                GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS_INTEGER = OPENGL_INT_VALUE.get(0);
                gl.glGetIntegerv(GL_MAX_VARYING_FLOATS, OPENGL_INT_VALUE);
                GL_MAX_VARYING_FLOATS_INTEGER = OPENGL_INT_VALUE.get(0);
                gl.glGetIntegerv(GL_MAX_TEXTURE_IMAGE_UNITS, OPENGL_INT_VALUE);
                GL_MAX_TEXTURE_IMAGE_UNITS_INTEGER = OPENGL_INT_VALUE.get(0);
                gl.glGetIntegerv(GL_MAX_TEXTURE_COORDS, OPENGL_INT_VALUE);
                GL_MAX_TEXTURE_COORDS_INTEGER = OPENGL_INT_VALUE.get(0);
                gl.glGetIntegerv(GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS, OPENGL_INT_VALUE);
                GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS_INTEGER = OPENGL_INT_VALUE.get(0);
                gl.glGetIntegerv(GL_MAX_FRAGMENT_UNIFORM_COMPONENTS, OPENGL_INT_VALUE);
                GL_MAX_FRAGMENT_UNIFORM_COMPONENTS_INTEGER = OPENGL_INT_VALUE.get(0);
                gl.glGetIntegerv(GL_MAX_3D_TEXTURE_SIZE, OPENGL_INT_VALUE);
                GL_MAX_3D_TEXTURE_SIZE_INTEGER = OPENGL_INT_VALUE.get(0);
                if ( USE_GL_ARB_TEXTURE_RECTANGLE = gl.isExtensionAvailable("GL_ARB_texture_rectangle") )
                {
                    gl.glGetIntegerv(GL_MAX_RECTANGLE_TEXTURE_SIZE_ARB, OPENGL_INT_VALUE);
                    GL_MAX_RECTANGLE_TEXTURE_SIZE_ARB_INTEGER = OPENGL_INT_VALUE.get(0);
                }
                gl.glGetIntegerv(GL_MAX_TEXTURE_SIZE, OPENGL_INT_VALUE);
                GL_MAX_TEXTURE_SIZE_INTEGER = OPENGL_INT_VALUE.get(0);
                if ( USE_GL_EXT_FRAMEBUFFER_OBJECT = gl.isExtensionAvailable("GL_EXT_framebuffer_object") )
                {
                    gl.glGetIntegerv(GL_MAX_RENDERBUFFER_SIZE, OPENGL_INT_VALUE);
                    GL_MAX_RENDERBUFFER_SIZE_EXT_INTEGER = OPENGL_INT_VALUE.get(0);
                }
                USE_GL_EXT_GPU_SHADER4 = gl.isExtensionAvailable("GL_EXT_gpu_shader4");
                USE_GL_ARB_GPU_SHADER5 = gl.isExtensionAvailable("GL_ARB_gpu_shader5");
                USE_GL_ARB_GPU_SHADER_FP64 = gl.isExtensionAvailable("GL_ARB_gpu_shader_fp64");

                if (DEBUG_BUILD)
                {
                    StringBuilder output = new StringBuilder();
                    output.append("\nGLSL Driver Capabilities:\n\n");
                    output.append("GL_SHADING_LANGUAGE_VERSION:\t\t").append(GL_SHADING_LANGUAGE_VERSION_STRING).append("\n");
                    output.append("GL_MAX_DRAW_BUFFERS:\t\t\t").append(GL_MAX_DRAW_BUFFERS_INTEGER).append("\n");
                    output.append("GL_MAX_COLOR_ATTACHMENTS:\t\t").append(GL_MAX_COLOR_ATTACHMENTS_INTEGER).append("\n");
                    output.append("GL_AUX_BUFFERS:\t\t\t\t").append(GL_AUX_BUFFERS_INTEGER).append("\n");
                    output.append("GL_MAX_TEXTURE_UNITS:\t\t\t").append(GL_MAX_TEXTURE_UNITS_INTEGER).append("\n");
                    output.append("GL_MAX_VERTEX_ATTRIBS:\t\t\t").append(GL_MAX_VERTEX_ATTRIBS_INTEGER).append("\n");
                    output.append("GL_MAX_VERTEX_UNIFORM_COMPONENTS:\t").append(GL_MAX_VERTEX_UNIFORM_COMPONENTS_INTEGER).append("\n");
                    output.append("GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS:\t").append(GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS_INTEGER).append("\n");
                    output.append("GL_MAX_VARYING_FLOATS:\t\t\t").append(GL_MAX_VARYING_FLOATS_INTEGER).append("\n");
                    output.append("GL_MAX_TEXTURE_IMAGE_UNITS:\t\t").append(GL_MAX_TEXTURE_IMAGE_UNITS_INTEGER).append("\n");
                    output.append("GL_MAX_TEXTURE_COORDS:\t\t\t").append(GL_MAX_TEXTURE_COORDS_INTEGER).append("\n");
                    output.append("GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS:\t").append(GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS_INTEGER).append("\n");
                    output.append("GL_MAX_FRAGMENT_UNIFORM_COMPONENTS:\t").append(GL_MAX_FRAGMENT_UNIFORM_COMPONENTS_INTEGER).append("\n");
                    output.append("GL_MAX_3D_TEXTURE_SIZE:\t\t\t").append(GL_MAX_3D_TEXTURE_SIZE_INTEGER).append("\n");
                    if (USE_GL_ARB_TEXTURE_RECTANGLE)
                        output.append("GL_MAX_RECTANGLE_TEXTURE_SIZE_ARB:\t").append(GL_MAX_RECTANGLE_TEXTURE_SIZE_ARB_INTEGER).append("\n");
                    output.append("GL_MAX_TEXTURE_SIZE:\t\t\t").append(GL_MAX_TEXTURE_SIZE_INTEGER).append("\n");
                    if (USE_GL_EXT_FRAMEBUFFER_OBJECT)
                        output.append("GL_MAX_RENDERBUFFER_SIZE_EXT:\t\t").append(GL_MAX_RENDERBUFFER_SIZE_EXT_INTEGER).append("\n");
                    output.append("GL GPU SHADER MODEL 4 SUPPORT:\t\t").append(USE_GL_EXT_GPU_SHADER4 ? "YES" : "NO").append("\n");
                    output.append("GL GPU SHADER MODEL 5 SUPPORT:\t\t").append(USE_GL_ARB_GPU_SHADER5 ? "YES" : "NO").append("\n");
                    output.append("GL GPU SHADER FP64 SUPPORT:\t\t").append(USE_GL_ARB_GPU_SHADER_FP64 ? "YES" : "NO").append("\n\n");
                    println( output.toString() );
                }
            }
        }

        gl.setSwapInterval(USE_VSYNCH.get() ? 1 : 0); // 0 for no VSynch, 1 for VSynch

        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();

        // switch to model view state
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();

        // antialiasing rendering options
        if ( qualityRendering = ( NORMAL_QUALITY_ANTIALIASING.get() || HIGH_QUALITY_ANTIALIASING.get() ) )
            prepareHighQualityRendering(gl);
        else
            prepareLowQualityRendering(gl);

        prepareBackgroundColor();
        prepareProfileModeBackgroundAndFont();
        currentGraphRenderer.init(glDrawable);
        requestFocus();
        gl.glFlush();

        OpenGLContext.checkGLErrors(gl, true);
    }

    /**
    *  Called by the JOGL2 glDrawable to initiate OpenGL rendering by the client.
    */
    @Override
    public void display(GLAutoDrawable glDrawable)
    {
        GL2 gl = glDrawable.getGL().getGL2();

        boolean hasReInitializeRendererMode = false;
        if (reInitializeRendererMode)
        {
            reInitializeRendererMode = false;
            hasReInitializeRendererMode = true;
            currentGraphRenderer.init(glDrawable);
            currentGraphRenderer.reshape(glDrawable, 0, 0, width, height);
            if ( nc.getIsGraphml() ) CHANGE_GRAPHML_COMPONENT_CONTAINERS = true;
        }


        currentGraphRenderer.display(glDrawable);
        // Take the contents of the current draw buffer and copy it to the accumulation buffer with each pixel modified by a factor
        // The closer the factor is to 1.0f, the longer the trails... Don't exceed 1.0f - you get garbage.
        if (!GL_IS_AMD_ATI) // recent AMD/ATI cards lack an accumulation buffer
        {
            if (USE_MOTION_BLUR_FOR_SCENE.get() && !hasReInitializeRendererMode)
            {
                gl.glAccum(GL_MULT, MOTION_BLUR_SIZE.get());
                gl.glAccum(GL_ACCUM, 1 - MOTION_BLUR_SIZE.get());
                gl.glAccum(GL_RETURN, 1.0f);
            }
        }
        gl.glFlush();

        OpenGLContext.checkGLErrors(gl, true);
    }

    /**
    *  Called by the JOGL2 glDrawable during the first repaint after the component has been resized.
    */
    @Override
    public void reshape(GLAutoDrawable glDrawable, int x, int y, int widthCanvas, int heightCanvas)
    {
        if (widthCanvas <= 0) widthCanvas = 1;
        if (heightCanvas <= 0) heightCanvas = 1;

        width = widthCanvas;
        height = heightCanvas;

        GL2 gl = glDrawable.getGL().getGL2();
        if (!GL_IS_AMD_ATI) // recent AMD/ATI cards lack an accumulation buffer
        {
            if ( USE_MOTION_BLUR_FOR_SCENE.get() )
            {
                // Clear the accumulation buffer (we re-grab the screen into the accumulation buffer after drawing our current frame!)
                gl.glClearAccum(0.0f, 0.0f, 0.0f, 1.0f);
                gl.glClear(GL_ACCUM_BUFFER_BIT);
                gl.glAccum(GL_LOAD, 0.0f);
            }
        }
        currentGraphRenderer.reshape(glDrawable, x, y, width, height);
        gl.glFlush();

        OpenGLContext.checkGLErrors(gl, true);
    }

    /**
    *  KeyPressed keyEvent.
    */
    @Override
    public void keyPressed(KeyEvent e)
    {
        if ( isEventNotAllowed() ) return;

        currentGraphRenderer.keyPressed(e);
    }

    /**
    *  KeyReleased keyEvent.
    */
    @Override
    public void keyReleased(KeyEvent e)
    {
        if ( isEventNotAllowed() ) return;

        currentGraphRenderer.keyReleased(e);
    }

    /**
    *  KeyTyped keyEvent.
    */
    @Override
    public void keyTyped(KeyEvent e)
    {
        if ( isEventNotAllowed() || !ADVANCED_KEYBOARD_RENDERING_CONTROL.get() ) return;

        currentGraphRenderer.keyTyped(e);
    }

    /**
    *  MouseClicked mouseEvent.
    */
    @Override
    public void mouseClicked(MouseEvent e)
    {
        if ( isEventNotAllowed() ) return;

        currentGraphRenderer.mouseClicked(e);
    }

    /**
    *  MouseEntered mouseEvent.
    */
    @Override
    public void mouseEntered(MouseEvent e)
    {
        if ( isEventNotAllowed() ) return;

        if ( !hasFocus() && !layoutFrame.getClassViewerFrame().isVisible() )
            requestFocus();

        // currentGraphRenderer.mouseEntered(e);
    }

    /**
    *  MouseExited mouseEvent.
    */
    @Override
    public void mouseExited(MouseEvent e)
    {
        // currentGraphRenderer.mouseExited(e);
    }

    /**
    *  MousePressed mouseEvent.
    */
    @Override
    public void mousePressed(MouseEvent e)
    {
        if ( isEventNotAllowed() ) return;

        currentGraphRenderer.mousePressed(e);
    }

    /**
    *  MouseReleased mouseEvent.
    */
    @Override
    public void mouseReleased(MouseEvent e)
    {
        if ( isEventNotAllowed() ) return;

        currentGraphRenderer.mouseReleased(e);
    }

    /**
    *  MouseDragged mouseMotionEvent.
    */
    @Override
    public void mouseDragged(MouseEvent e)
    {
        if ( isEventNotAllowed() ) return;

        currentGraphRenderer.mouseDragged(e);
    }

    /**
    *  MouseMoved mouseMotionEvent.
    */
    @Override
    public void mouseMoved(MouseEvent e)
    {
        if ( isEventNotAllowed() ) return;

        currentGraphRenderer.mouseMoved(e);
    }

    /**
    *  MouseWheelMoved mouseWheelEvent.
    */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        if ( isEventNotAllowed() ) return;

        currentGraphRenderer.mouseWheelMoved(e);
    }

    /**
    *  Switches the renderer mode.
    */
    @Override
    public void switchRendererMode()
    {
        currentGraphRenderer.switchRendererMode();
    }

    /**
    *  Adds all events to this Graph.
    */
    @Override
    public void addAllEvents()
    {
        this.addKeyListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
        this.addGLEventListener(this);

        currentGraphRenderer.addAllEvents();
    }

    /**
    *  Removes all events from this Graph.
    */
    @Override
    public void removeAllEvents()
    {
        currentGraphRenderer.removeAllEvents();
        refreshDisplay(); // so as to give one refresh for de-allocations before the GL event is removed below

        this.removeGLEventListener(this);
        this.removeMouseWheelListener(this);
        this.removeMouseMotionListener(this);
        this.removeMouseListener(this);
        this.removeKeyListener(this);
    }

    /**
    *  Gets a BufferedImage (mainly used for printing functionality).
    */
    @Override
    public BufferedImage getBufferedImage()
    {
        return currentGraphRenderer.getBufferedImage();
    }

    /**
    *  Fully updates the display lists.
    */
    @Override
    public void updateAllDisplayLists()
    {
        currentGraphRenderer.updateAllDisplayLists();
    }

    /**
    *  Updates the nodes display list only.
    */
    @Override
    public void updateNodesDisplayList()
    {
        currentGraphRenderer.updateNodesDisplayList();
    }

    /**
    *  Updates the selected nodes display list only.
    */
    @Override
    public void updateSelectedNodesDisplayList()
    {
        currentGraphRenderer.updateSelectedNodesDisplayList();
    }

    /**
    *  Updates the nodes & selected nodes display lists only.
    */
    @Override
    public void updateNodesAndSelectedNodesDisplayList()
    {
        currentGraphRenderer.updateNodesAndSelectedNodesDisplayList();
    }

    /**
    *  Updates the edges display lists only.
    */
    @Override
    public void updateEdgesDisplayList()
    {
        currentGraphRenderer.updateEdgesDisplayList();
    }

    /**
    *  Updates the display lists selectively.
    */
    @Override
    public void updateDisplayLists(boolean nodesDisplayList, boolean edgesDisplayList, boolean selectedNodesDisplayList)
    {
        currentGraphRenderer.updateDisplayLists(nodesDisplayList, edgesDisplayList, selectedNodesDisplayList);
    }

    /**
    *  Refreshes the display.
    */
    @Override
    public void refreshDisplay()
    {
        currentGraphRenderer.refreshDisplay();
    }

    /**
    *  Resets all values.
    */
    @Override
    public void resetAllValues()
    {
        currentGraphRenderer.resetAllValues();
    }

    /**
    *  Checks if there are more undo steps to be performed.
    */
    @Override
    public boolean hasMoreUndoSteps()
    {
        return currentGraphRenderer.hasMoreUndoSteps();
    }

    /**
    *  Checks if there are more redo steps to be performed.
    */
    @Override
    public boolean hasMoreRedoSteps()
    {
        return currentGraphRenderer.hasMoreRedoSteps();
    }

    /**
    *  The main take a screenshot process.
    */
    @Override
    public void takeScreenShotProcess(boolean doHighResScreenShot)
    {
        currentGraphRenderer.takeScreenShotProcess(doHighResScreenShot);
    }

    /**
    *  Creates the reTranslate action.
    */
    @Override
    public void createReTranslateAction(TranslateTypes translateType, ActionEvent e)
    {
        currentGraphRenderer.createReTranslateAction(translateType, e);
    }

    /**
    *  Creates the reRotate action.
    */
    @Override
    public void createReRotateAction(RotateTypes rotateType, ActionEvent e)
    {
        currentGraphRenderer.createReRotateAction(rotateType, e);
    }

    /**
    *  Creates the reScale action.
    */
    @Override
    public void createReScaleAction(ScaleTypes scaleType, ActionEvent e)
    {
        currentGraphRenderer.createReScaleAction(scaleType, e);
    }

    /**
    *  Creates the Burst Layout Iterations action.
    */
    @Override
    public void createBurstLayoutIterationsAction(ActionEvent e)
    {
        burstLayoutIterations();
        // currentGraphRenderer.createBurstLayoutIterationsAction(e);
    }

    /**
    *  Gets the undo node dragging action.
    */
    @Override
    public void createUndoNodeDraggingAction(ActionEvent e)
    {
        currentGraphRenderer.createUndoNodeDraggingAction(e);
    }

    /**
    *  Gets the redo node dragging action.
    */
    @Override
    public void createRedoNodeDraggingAction(ActionEvent e)
    {
        currentGraphRenderer.createRedoNodeDraggingAction(e);
    }

    /**
    *  Gets the autorotate action.
    */
    @Override
    public void createAutoRotateAction(ActionEvent e)
    {
        currentGraphRenderer.createAutoRotateAction(e);
    }

    /**
    *  Gets the screensaver 2D mode action.
    */
    @Override
    public void createAutoScreenSaver2DModeAction(ActionEvent e)
    {
        currentGraphRenderer.createAutoScreenSaver2DModeAction(e);
    }

    /**
    *  Gets the pulsation 3D mode action.
    */
    @Override
    public void createPulsation3DModeAction(ActionEvent e)
    {
        currentGraphRenderer.createPulsation3DModeAction(e);
    }

    /**
    *  Gets the selection action.
    */
    @Override
    public void createSelectAction(ActionEvent e)
    {
        currentGraphRenderer.createSelectAction(e);
    }

    /**
    *  Gets the translation action.
    */
    @Override
    public void createTranslateAction(ActionEvent e)
    {
        currentGraphRenderer.createTranslateAction(e);
    }

    /**
    *  Gets the rotation action.
    */
    @Override
    public void createRotateAction(ActionEvent e)
    {
        currentGraphRenderer.createRotateAction(e);
    }

    /**
    *  Gets the zoom action.
    */
    @Override
    public void createZoomAction(ActionEvent e)
    {
        currentGraphRenderer.createZoomAction(e);
    }

    /**
    *  Gets the reset view action.
    */
    @Override
    public void createResetViewAction(ActionEvent e)
    {
        currentGraphRenderer.createResetViewAction(e);
    }

    /**
    *  Gets the render action.
    */
    @Override
    public void createRenderImageToFileAction(ActionEvent e)
    {
        initiateTakeScreenShotProcess(false);
        // currentGraphRenderer.createRenderImageToFileAction(e);
    }

    /**
    *  Gets the high resolution render action.
    */
    @Override
    public void createRenderHighResImageToFileAction(ActionEvent e)
    {
        initiateTakeScreenShotProcess(true);
        // currentGraphRenderer.createRenderHighResImageToFileAction(e);
    }

    /**
    *  Generally pauses the renderer.
    */
    @Override
    public void generalPauseRenderUpdateThread()
    {
        currentGraphRenderer.generalPauseRenderUpdateThread();
    }

    /**
    *  Generally resumes the renderer.
    */
    @Override
    public void generalResumeRenderUpdateThread()
    {
        currentGraphRenderer.generalResumeRenderUpdateThread();
    }

    /**
    *  Updates all animation.
    */
    @Override
    public void updateAnimation()
    {
        currentGraphRenderer.updateAnimation();
    }

    /**
    *  Sets the GraphListener listener.
    */
    public void setListener(GraphListener newListener)
    {
        listener = newListener;
    }

    /**
    *  Removes the IOUtilsLoaderListener listener.
    */
    public void removeListener()
    {
        listener = null;
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
