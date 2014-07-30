package org.BioLayoutExpress3D.Graph;

import java.awt.*;
import java.nio.*;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.*;
import static javax.media.opengl.GL.*;
import org.BioLayoutExpress3D.Graph.Camera.*;
import org.BioLayoutExpress3D.Utils.*;
import static org.BioLayoutExpress3D.Graph.Camera.GraphCameraEyeTypes.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.Shapes3D.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
*
* The GraphRenderer3DFinalVariables class is the class that holds all relevant GraphRenderer3D OpenGL variables.
* All variables in this static class have package scope and are to be used within GraphRenderer3D.
*
* @see org.BioLayoutExpress3D.Graph.GraphRenderer3D
* @author Thanos Theo, 2011
* @version 3.0.0.0
*
*/

public final class GraphRenderer3DFinalVariables
{

    /**
    *  Variable to be used for OpenGL Vertex Arrays support.
    */
    // private static final Buffer indices = Buffers.newDirectByteBuffer(24).put( new byte[] {  0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23 } ).rewind();

    /**
    *  Variable to be used for OpenGL Vertex Arrays support.
    */
    static final Buffer ALL_VERTEX_3D_COORDS_LAYOUT_CUBE_BUFFER = Buffers.newDirectFloatBuffer(3 * 24).put( new float[] {
                                                                                                                       -5.0f, -5.0f, -5.0f,
                                                                                                                       -5.0f,  5.0f, -5.0f,
                                                                                                                       -5.0f,  5.0f, -5.0f,
                                                                                                                        5.0f,  5.0f, -5.0f,
                                                                                                                        5.0f,  5.0f, -5.0f,
                                                                                                                        5.0f, -5.0f, -5.0f,
                                                                                                                        5.0f, -5.0f, -5.0f,
                                                                                                                       -5.0f, -5.0f, -5.0f,

                                                                                                                       -5.0f, -5.0f,  5.0f,
                                                                                                                       -5.0f,  5.0f,  5.0f,
                                                                                                                       -5.0f,  5.0f,  5.0f,
                                                                                                                        5.0f,  5.0f,  5.0f,
                                                                                                                        5.0f,  5.0f,  5.0f,
                                                                                                                        5.0f, -5.0f,  5.0f,
                                                                                                                        5.0f, -5.0f,  5.0f,
                                                                                                                       -5.0f, -5.0f,  5.0f,

                                                                                                                       -5.0f, -5.0f, -5.0f,
                                                                                                                       -5.0f, -5.0f,  5.0f,
                                                                                                                       -5.0f,  5.0f, -5.0f,
                                                                                                                       -5.0f,  5.0f,  5.0f,
                                                                                                                        5.0f,  5.0f, -5.0f,
                                                                                                                        5.0f,  5.0f,  5.0f,
                                                                                                                        5.0f, -5.0f, -5.0f,
                                                                                                                        5.0f, -5.0f,  5.0f
                                                                                                                       } ).rewind();

    /**
    *  Variable to be used for OpenGL Vertex Arrays support.
    */
    static final FloatBuffer ALL_VERTEX_3D_COORDS_SELECT_BOX_BUFFER = Buffers.newDirectFloatBuffer(2 * 8);

    // Lighting related variables
    public static final FloatBuffer NO_LIGHT_SPECULAR_ARRAY = (FloatBuffer)Buffers.newDirectFloatBuffer(4).put( new float[] { 0.0f, 0.0f, 0.0f, 1.0f } ).rewind();
    public static final FloatBuffer LIGHT_SPECULAR_ARRAY    = (FloatBuffer)Buffers.newDirectFloatBuffer(4).put( new float[] { 1.0f, 1.0f, 1.0f, 1.0f } ).rewind();
    public static final FloatBuffer LIGHT_AMBIENT_ARRAY     = (FloatBuffer)Buffers.newDirectFloatBuffer(4).put( new float[] { 0.0f, 0.0f, 0.0f, 1.0f } ).rewind();
    public static final FloatBuffer LIGHT_DIFFUSE_ARRAY     = (FloatBuffer)Buffers.newDirectFloatBuffer(4).put( new float[] { 1.0f, 1.0f, 1.0f, 1.0f } ).rewind();
    public static final FloatBuffer MODEL_AMBIENT_ARRAY     = (FloatBuffer)Buffers.newDirectFloatBuffer(4).put( new float[] { 0.4f, 0.4f, 0.4f, 1.0f } ).rewind();
    public static final int LOCAL_VIEWER = GL_TRUE; // GL_TRUE = infinite distance from scene, optimization trick for OpenGL lighting calculations

    // Fog related variable
    static final FloatBuffer FOG_COLOR = (FloatBuffer)Buffers.newDirectFloatBuffer(4).put( new float[] { 0.0f, 0.0f, 0.0f, 1.0f } ).rewind(); // Fog Color

    // Point Sprite variables
    static final FloatBuffer POINT_DISTANCE_ATTENUATION_ARRAY = (FloatBuffer)Buffers.newDirectFloatBuffer(3).put( new float[] { 1.0f, 0.0f, 0.01f } ).rewind();
    static final FloatBuffer ALIASED_POINT_SIZE_RANGE = Buffers.newDirectFloatBuffer(2);

    // Shadow projection matrix
    static final FloatBuffer SHADOW_PROJECTION_MATRIX = (FloatBuffer)Buffers.newDirectFloatBuffer(16).put( new float[] { 1.0f, 0.0f, 0.0f, 0.0f,
                                                                                                                      0.0f, 1.0f, 0.0f, 0.0f,
                                                                                                                      0.0f, 0.0f, 1.0f, 0.0f,
                                                                                                                      0.0f, 0.0f, 0.0f, 0.0f
                                                                                                                    } ).rewind();

    static final float[] CURRENT_COLOR = new float[4];
    static final Color FRUSTUM_COLOR = new Color(0.2f, 0.2f, 0.8f);
    static final float FRUSTUM_LINE_WIDTH = 2.0f;
    static final float SELECTED_BOX_LINE_WIDTH = 1.0f;

    // Node texture related variables
    // Sphere, Cone Left, Cone Right, Cylinder, Torus, Lathe3D & SuperQuadric  shapes only support tesselation & texture coords
    static final Shapes3D[] SHAPES_WITH_TESSELATION_AND_TEXTURE_COORDS = { SPHERE, CONE_LEFT, CONE_RIGHT, TRAPEZOID_UP, TRAPEZOID_DOWN, CYLINDER, TORUS,
                                                                           ROUND_CUBE_THIN, ROUND_CUBE_LARGE, PINEAPPLE_SLICE_TOROID, PINEAPPLE_SLICE_ELLIPSOID,
                                                                           DOUBLE_PYRAMID_LARGE, DOUBLE_PYRAMID_THIN, TORUS_8_PETALS, SAUCER_4_PETALS,
                                                                           LATHE_3D, SUPER_QUADRIC, GENE_MODEL };
    // OBJ Model Loader shape may have texture coords only, tesselation geometry is pre-loaded from the OBJ file format
    static final Shapes3D[] SHAPES_WITH_TEXTURE_COORDS_ONLY = {OBJ_MODEL_LOADER};
    static final int FAST_SELECTION_MODE_NODE_TESSELATION = 5;

    public static enum MouseModeTypes { ROTATE, SELECT, TRANSLATE, SCALE }
    public static MouseModeTypes currentMouseMode = MouseModeTypes.ROTATE;

    // Rotation / Depth related variables
    static final float AMOUNT_OF_ROTATION = 0.5f;
    static final float AMOUNT_OF_DEPTH = 10.0f;
    static final int PULSATION_UPPER_THRESHOLD = 6;

    static final float UNIT_SHAPE_SIZE = 0.008f;
    public static final float DEFAULT_TRANSLATION = 0.5f;
    public static final float DEFAULT_ROTATION = 20.0f;
    static final float DEFAULT_SCALE = 18.0f;

    static final int[] ALL_SHAPES_3D_DISPLAY_LISTS = new int[NUMBER_OF_3D_SHAPES];
    static final int[] ALL_SHAPES_3D_FAST_SELECTION_DISPLAY_LISTS = new int[NUMBER_OF_3D_SHAPES];
    static final Point3D FOCUS_POSITION_3D = new Point3D(0.0f, 0.0f, 0.0f); // center of OpenGL coords

    static final GraphCameraEye LEFT_EYE_CAMERA = new GraphCameraEye(LEFT_EYE);
    static final GraphCameraEye CENTER_VIEW_CAMERA = new GraphCameraEye(CENTER_VIEW);
    static final GraphCameraEye RIGHT_EYE_CAMERA = new GraphCameraEye(RIGHT_EYE);

}
