package org.BioLayoutExpress3D.Graph;

import java.awt.image.*;
import java.nio.*;
import java.text.*;
import java.util.concurrent.*;
import javax.media.opengl.glu.*;
import com.jogamp.opengl.util.*;
import org.BioLayoutExpress3D.Graph.Selection.SelectionUI.*;
import static com.jogamp.opengl.util.gl2.GLUT.*;
import com.jogamp.opengl.util.gl2.GLUT;

/**
*
* The GraphRendererCommonFinalVariables class is the class that holds all relevant GraphRenderer2D, GraphRenderer3D & ModelShapeRenderer OpenGL variables.
*
* @see org.BioLayoutExpress3D.Graph.GraphRenderer2D
* @see org.BioLayoutExpress3D.Graph.GraphRenderer3D
* @see org.BioLayoutExpress3D.Models.UIComponents.ModelShapeRenderer
* @author Thanos Theo, 2011
* @version 3.0.0.0
*
*/

public final class GraphRendererCommonFinalVariables
{

    /**
    *
    *  GLU reference for the OpenGL renderer.
    */
    public static final GLU GLU = new GLU();

    /**
    *  GLUT reference for the renderer.
    */
    public static final GLUT GLUT = new GLUT();

    /**
    *  Constant value needed for the OpenGL renderer.
    */
    static final int MAP_SELECTION_ID = 1;

    /**
    *  Constant value needed for the OpenGL renderer.
    */
    static final int SELECTION_BUFFER_SIZE = 200000; // hit list storage, up to around 50k nodes (4 * 50k = 200k bytes)

    /**
    *  Value needed for the OpenGL renderer. Stores the picking results.
    */
    static final IntBuffer SELECTION_BUFFER = createSelectionBuffer();

    /**
    *  Constant value needed for the OpenGL renderer.
    */
    static final int EDGES_PER_DISPLAY_LIST = 1 << 18; // 4 * 65536

    /**
    *  Constant value needed for the OpenGL renderer.
    */
    static final int LEGENDS_OPENGL_FONT_TYPE  = BITMAP_HELVETICA_18;

    /**
    *  Constant value needed for the OpenGL renderer.
    */
    public static final int EDGE_NAMES_OPENGL_FONT_TYPE  = BITMAP_HELVETICA_10;

    /**
    *  Constant value needed for the OpenGL renderer.
    */
    static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance();

    /**
    *  Constant value needed for the OpenGL renderer.
    */
    public static final float SCALE_FACTOR = 1.25f;

    /**
    *  Constant value needed for the OpenGL renderer. Uses the Singleton Design Pattern with method createRenderProfileModeBackgroundImage().
    */
    static final BufferedImage RENDER_PROFILE_MODE_BACKGROUND_IMAGE = createRenderProfileModeBackgroundImage();

    /**
    *  Constant value needed for the OpenGL renderer. Use a viewport just for the selection mode, x -> 0, y -> 1, width -> 2, height -> 3. Package restricted access.
    */
    static final int[] VIEWPORT = new int[4];

    /**
    *  Constant value needed for the OpenGL renderer.
    */
    public static final float[] BACKGROUND_COLOR_ARRAY = new float[3];

    /**
    *  Value needed for the OpenGL renderer.
    */
    static final String NO_NODE_FOUND_LABEL = "Node: --";

    /**
    *  Value needed for the OpenGL renderer.
    */
    static final GraphPopupComponent GRAPH_POPUP_COMPONENT = new GraphPopupComponent();

    /**
    *  Value needed for the OpenGL renderer.
    */
    static final ScheduledExecutorService GRAPH_POPUP_SCHEDULED_EXECUTOR_SERVICE = Executors.newScheduledThreadPool(1);

    /**
    *  Constant value needed for the OpenGL renderer.
    */
    static final int COMPONENT_POPUP_DELAY_MILLISECONDS = 1000;

    /**
    *  Creates the Selection Buffer. Uses the Singleton Design Pattern along with the static variable, RENDER_PROFILE_MODE_BACKGROUND_IMAGE.
    */
    private static IntBuffer createSelectionBuffer()
    {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * SELECTION_BUFFER_SIZE);
        byteBuffer.order( ByteOrder.nativeOrder() );

        return byteBuffer.asIntBuffer();
    }

    /**
    *  Creates the render profile mode background image. Uses the Singleton Design Pattern along with the static variable, RENDER_PROFILE_MODE_BACKGROUND_IMAGE.
    */
    private static BufferedImage createRenderProfileModeBackgroundImage()
    {
        BufferedImage renderProfileModebackgroundImage = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
        int[] pixels = ( (DataBufferInt)renderProfileModebackgroundImage.getRaster().getDataBuffer() ).getData();  // connect it to the returning image buffer
        for (int i = 0; i < pixels.length; i++)
            pixels[i] = 127 << 24;

        return renderProfileModebackgroundImage;
    }

    /**
    *  Static initializer.
    */
    static
    {
        NUMBER_FORMAT.setMaximumFractionDigits(2);
    }


}
