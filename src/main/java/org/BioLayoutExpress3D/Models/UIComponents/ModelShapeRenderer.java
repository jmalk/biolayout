package org.BioLayoutExpress3D.Models.UIComponents;

import java.awt.event.*;
import java.io.*;
import java.nio.*;
import javax.swing.*;
import static java.lang.Math.*;
import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.common.nio.Buffers;
import java.awt.Color;
import static javax.media.opengl.GL2.*;
import org.BioLayoutExpress3D.Graph.*;
import org.BioLayoutExpress3D.Models.*;
import org.BioLayoutExpress3D.Models.Lathe3D.*;
import org.BioLayoutExpress3D.Models.SuperQuadric.*;
import org.BioLayoutExpress3D.Models.Loaders.OBJModelLoader.*;
import org.BioLayoutExpress3D.Models.ModelRenderingStates;
import org.BioLayoutExpress3D.Graph.ActiveRendering.*;
import org.BioLayoutExpress3D.Graph.ActionsUI.*;
import org.BioLayoutExpress3D.Graph.Camera.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import org.BioLayoutExpress3D.Textures.*;
import org.BioLayoutExpress3D.Utils.*;
import static org.BioLayoutExpress3D.Graph.Graph.*;
import static org.BioLayoutExpress3D.Graph.GraphRendererCommonFinalVariables.*;
import static org.BioLayoutExpress3D.Graph.GraphRenderer3DFinalVariables.*;
import static org.BioLayoutExpress3D.Graph.Camera.GraphCameraEyeTypes.*;
import static org.BioLayoutExpress3D.Graph.Camera.GraphCameraFinalVariables.*;
import static org.BioLayoutExpress3D.Models.ModelRenderingStates.*;
import static org.BioLayoutExpress3D.Models.ModelTypes.*;
import static org.BioLayoutExpress3D.StaticLibraries.EnumUtils.*;
import static org.BioLayoutExpress3D.StaticLibraries.Random.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* The ModelShapeRenderer class extends GLCanvas and is the placeholder for the OpenGL canvas & renderer of various model shapes.
*
* @see org.BioLayoutExpress3D.Models.ModelShape
* @see org.BioLayoutExpress3D.Models.ModelTypes
* @see org.BioLayoutExpress3D.Models.UIComponents.ModelShapeEditorParentUIDialog
* @author Thanos Theo, 2011
* @version 3.0.0.0
*
*/

public class ModelShapeRenderer extends GLCanvas implements GLEventListener, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener, GraphCreateActionsInterface, GraphRendererThreadUpdaterInterface, GraphRendererThreadUpdaterAnimationInterface
{

    /**
    *  The rotation increment.
    */
    private static final float ROTATION_MAX_INCREMENT = 0.45f;

    /**
    *  The initial camera position.
    */
    private static final float DEFAULT_SCALE = 4.0f;

    /**
    *  The frameskip value for the GraphRendererThreadUpdater.
    */
    private static final boolean FRAMESKIP = true;

    /**
    *  The default fps value for the GraphRendererThreadUpdater.
    */
    private static final int DEFAULT_FPS = 60;

    /**
    *  The minimum tesselation value.
    */
    private static final int MINIMUM_TESSELATION_VALUE = 2;

    /**
    *  The minimum user scale value.
    */
    private static final float MINIMUM_USER_SCALE_VALUE = -0.99f;

    /**
    *  The axis line color.
    */
    private static final float[] AXIS_LINE_COLOR = new float[3];

    /**
    *  Draw axes legends.
    */
    private static final boolean DRAW_AXES_LEGENDS = true;

    /**
    *  Value needed for the OpenGL renderer for the lighting calculations. The light is located at the right, top and back.
    */
    private static final FloatBuffer LIGHT0_POSITION = (FloatBuffer)Buffers.newDirectFloatBuffer(4).put( new float[] { 10.0f, 10.0f, -10.0f, 1.0f } ).rewind();

    /**
    *  Value needed for the OpenGL renderer for the lighting calculations. The light is located at the left, bottom and front.
    */
    private static final FloatBuffer LIGHT1_POSITION = (FloatBuffer)Buffers.newDirectFloatBuffer(4).put( new float[] { -10.0f, -10.0f, 10.0f, 1.0f } ).rewind();

    /**
    *  Value needed for the OpenGL renderer.
    */
    private static final GraphCameraEye LEFT_EYE_CAMERA = new GraphCameraEye(LEFT_EYE);

    /**
    *  Value needed for the OpenGL renderer.
    */
    private static final GraphCameraEye CENTER_VIEW_CAMERA = new GraphCameraEye(CENTER_VIEW);

    /**
    *  Value needed for the OpenGL renderer.
    */
    private static final GraphCameraEye RIGHT_EYE_CAMERA = new GraphCameraEye(RIGHT_EYE);

    /**
    *  The ModelShapeEditorParentUIDialog reference.
    */
    private ModelShapeEditorParentUIDialog modelShapeEditorParentUIDialog = null;

    /**
    *  The Graph reference.
    */
    private Graph graph = null;

    /**
    *  The GraphRendererThreadUpdater reference.
    */
    private GraphRendererThreadUpdater graphRendererThreadUpdater = null;

    /**
    *  Value needed for the OpenGL renderer.
    */
    private ModelTypes modelShapeType = SUPER_QUADRIC_SHAPE;

    /**
    *  Value needed for the OpenGL renderer.
    */
    private ModelRenderingStates modelRenderingState = null;

    /**
    *  Value needed for the OpenGL renderer.
    */
    private ModelSettings modelSettings = null;

    /**
    *  The ModelShape reference.
    */
    private ModelShape modelShape = null;

    /**
    *  The Texture reference.
    */
    private Texture nodeTexture = null;

    /**
    *  Mouse input related variable.
    */
    private int mouseButton = 0;

    /**
    *  Mouse input related variable.
    */
    private int mouseLastX = 0;

    /**
    *  Mouse input related variable.
    */
    private int mouseLastY = 0;

    /**
    *  Mouse related variable.
    */
    private float mouseRotationX = 0.0f;

    /**
    *  Mouse related variable.
    */
    private float mouseRotationY = 0.0f;

    /**
    *  Mouse related variable.
    */
    private float scaleValue = DEFAULT_SCALE;

    /**
    *  Mouse related variable.
    */
    private float translateDX = 0.0f;

    /**
    *  Mouse related variable.
    */
    private float translateDY = 0.0f;

    /**
    *  The autorotation variables.
    */
    private float autoRotationX = 0.0f, autoRotationY = 0.0f, autoRotationZ = 0.0f; // total rotations in x,y,z axes

    /**
    *  The increment variables.
    */
    private float incrementX = 0.0f, incrementY = 0.0f, incrementZ = 0.0f; // increments for x,y,z rotations

    /**
    *  Value needed for the OpenGL renderer.
    */
    private int prevGLError = GL_NO_ERROR;

    /**
    *  Value needed for the OpenGL renderer.
    */
    private int currentGLError = GL_NO_ERROR;

    /**
    *  Value needed for the OpenGL renderer.
    */
    private int width = 0;

    /**
    *  Value needed for the OpenGL renderer.
    */
    private int height = 0;

    /**
    *  Value needed for the OpenGL renderer.
    */
    private boolean changeDetected = false;

    /**
    *  Value needed for the OpenGL renderer.
    */
    private boolean deAllocOpenGLMemory = false;

    /**
    *  Value needed for the OpenGL renderer.
    */
    private boolean closingDialogWindow = false;

    /**
    *  Value needed for the OpenGL renderer.
    */
    private int tesselation = NODE_TESSELATION.get();

    /**
    *  Value needed for the OpenGL renderer.
    */
    private boolean wireframeView = false;

    /**
    *  Value needed for the OpenGL renderer.
    */
    private boolean normalsView = false;

    /**
    *  Value needed for the OpenGL renderer.
    */
    private boolean texturingView = false;

    /**
    *  Value needed for the OpenGL renderer.
    */
    private boolean sphericalMappingView = false;

    /**
    *  The userScale variables.
    */
    private float userScaleX = 0.0f, userScaleY = 0.0f, userScaleZ = 0.0f;

    /**
    *  The userRotation variables.
    */
    private float userRotationX = 0.0f, userRotationY = 0.0f, userRotationZ = 0.0f;

    /**
    *  Directory of Model Shape shader files.
    */
    private static final String MODEL_SHAPE_SHADER_FILES_DIRECTORY = "ModelShape";

    /**
    *  Directory of Model Shape shader name.
    */
    private static final String MODEL_SHAPE_SHADER_NAME = "PhongNLights";

    /**
    *  Number of lights.
    */
    private final int MAX_LIGHTS = 2;

    /**
    *  Use of spotlights.
    */
    private final boolean USE_SPOT_LIGHTS = false;

    /**
    *  Vertex shader storage.
    */
    private final int[] VERTEX_SHADER = new int[1];

    /**
    *  Geometry shader storage.
    */
    private final int[] GEOMETRY_SHADER = new int[1];

    /**
    *  Fragmant shader storage.
    */
    private final int[] FRAGMENT_SHADER = new int[1];

    /**
    *  Shader program storage.
    */
    private final int[] SHADER_PROGRAM = new int[1];

    /**
    *  Shader program useOrenNayarDiffuseModel name.
    */
    private static final String SHADER_PROGRAM_USE_OREN_NAYAR_DIFFUSE_MODEL_NAME = "useOrenNayarDiffuseModel";

    /**
    *  Shader program useOrenNayarDiffuseModel storage.
    */
    private final int[] SHADER_PROGRAM_USE_OREN_NAYAR_DIFFUSE_MODEL = new int[1];

    /**
    *  Shader program shrink triangles name.
    */
    private static final String SHADER_PROGRAM_SHRINK_TRIANGLES_NAME = "shrinkTriangles";

    /**
    *  Shader program shrink triangles storage.
    */
    private final int[] SHADER_PROGRAM_SHRINK_TRIANGLES = new int[1];

    /**
    *  Shader program normals name.
    */
    private static final String SHADER_PROGRAM_NORMALS_NAME = "normals";

    /**
    *  Shader program normals storage.
    */
    private final int[] SHADER_PROGRAM_NORMALS = new int[1];

    /**
    *  Shader program texturing name.
    */
    private static final String SHADER_PROGRAM_TEXTURING_NAME = "texturing";

    /**
    *  Shader program texturing storage.
    */
    private final int[] SHADER_PROGRAM_TEXTURING = new int[1];

    /**
    *  Shader program 2D texture name.
    */
    private static final String SHADER_PROGRAM_2D_TEXTURE_NAME = "texture";

    /**
    *  Shader program 2D texture storage.
    */
    private final int[] SHADER_PROGRAM_2D_TEXTURE = new int[1];

    /**
    *  Shader program sphrerical mapping name.
    */
    private static final String SHADER_PROGRAM_SPHERICAL_MAPPING_NAME = "sphericalMapping";

    /**
    *  Shader program sphrerical mapping storage.
    */
    private final int[] SHADER_PROGRAM_SPHERICAL_MAPPING = new int[1];

    /**
    *  Variable to use the Phong N Lights shader (on/off)).
    */
    private boolean usePhongNLightsShader = true;

    /**
    *  Variable to use the Oren Nayar Diffuse Model (on/off)).
    */
    private boolean useOrenNayarDiffuseModel = true;

    /**
    *  All relevant renderer Actions. To be used from the ModelShapeNavigationToolBar buttons.
    */
    private AbstractAction translateUpAction = null;
    private AbstractAction translateDownAction = null;
    private AbstractAction translateLeftAction = null;
    private AbstractAction translateRightAction = null;
    private AbstractAction rotateUpAction = null;
    private AbstractAction rotateDownAction = null;
    private AbstractAction rotateLeftAction = null;
    private AbstractAction rotateRightAction = null;
    private AbstractAction zoomInAction = null;
    private AbstractAction zoomOutAction = null;
    private AbstractAction resetViewAction = null;

    /**
    *  The ModelShapeRenderer constructor.
    */
    public ModelShapeRenderer(ModelShapeEditorParentUIDialog modelShapeEditorParentUIDialog, Graph graph)
    {
        super( getCaps() );

        this.modelShapeEditorParentUIDialog = modelShapeEditorParentUIDialog;
        this.graph = graph;

        modelRenderingState = USE_SHADERS_PROCESS ? VBO : (USE_VERTEX_ARRAYS_FOR_OPENGL_RENDERER ? VERTEX_ARRAY : IMMEDIATE_MODE);
        modelSettings = new ModelSettings(true, true, modelRenderingState);

        createActions();
    }

    /**
    *  Creates all the actions.
    */
    private void createActions()
    {
        translateUpAction = new AbstractAction("Up")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555721L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                createReTranslateAction(TranslateTypes.TRANSLATE_UP, e);
            }
        };
        translateUpAction.setEnabled(true);

        translateDownAction = new AbstractAction("Down")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555721L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                createReTranslateAction(TranslateTypes.TRANSLATE_DOWN, e);
            }
        };
        translateDownAction.setEnabled(true);

        translateLeftAction = new AbstractAction("Left")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555721L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                createReTranslateAction(TranslateTypes.TRANSLATE_LEFT, e);
            }
        };
        translateLeftAction.setEnabled(true);

        translateRightAction = new AbstractAction("Right")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555721L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                createReTranslateAction(TranslateTypes.TRANSLATE_RIGHT, e);
            }
        };
        translateRightAction.setEnabled(true);

        rotateUpAction = new AbstractAction("Rotate Up")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555721L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                createReRotateAction(RotateTypes.ROTATE_UP, e);
            }
        };
        rotateUpAction.setEnabled(true);

        rotateDownAction = new AbstractAction("Rotate Down")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555721L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                createReRotateAction(RotateTypes.ROTATE_DOWN, e);
            }
        };
        rotateDownAction.setEnabled(true);

        rotateLeftAction = new AbstractAction("Rotate Left")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555721L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                createReRotateAction(RotateTypes.ROTATE_LEFT, e);

            }
        };
        rotateLeftAction.setEnabled(true);

        rotateRightAction = new AbstractAction("Rotate Right")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555721L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
               createReRotateAction(RotateTypes.ROTATE_RIGHT, e);
            }
        };
        rotateRightAction.setEnabled(true);

        zoomInAction = new AbstractAction("Zoom In")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555721L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                createReScaleAction(ScaleTypes.SCALE_IN, e);
            }
        };
        zoomInAction.setEnabled(true);

        zoomOutAction = new AbstractAction("Zoom Out")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555722L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                createReScaleAction(ScaleTypes.SCALE_OUT, e);
            }
        };
        zoomOutAction.setEnabled(true);

        resetViewAction = new AbstractAction("Reset View")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555737L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                modelShapeEditorParentUIDialog.setSelectedAutoRotateViewCheckBox(false);
                MODEL_SHAPE_EDITOR_AUTOROTATE_VIEW.set(false);
                if ( isAnimating() )
                    stopRender();
                resetView();
                refreshDisplay();
            }
        };
        resetViewAction.setEnabled(true);
    }

    /**
    *  Gets the translateUp action.
    */
    public AbstractAction getTranslateUpAction()
    {
        return translateUpAction;
    }

    /**
    *  Gets the translateDown action.
    */
    public AbstractAction getTranslateDownAction()
    {
        return translateDownAction;
    }

    /**
    *  Gets the translateLeft action.
    */
    public AbstractAction getTranslateLeftAction()
    {
        return translateLeftAction;
    }

    /**
    *  Gets the translateRight action.
    */
    public AbstractAction getTranslateRightAction()
    {
        return translateRightAction;
    }

    /**
    *  Gets the rotateUp action.
    */
    public AbstractAction getRotateUpAction()
    {
        return rotateUpAction;
    }

    /**
    *  Gets the rotateDown action.
    */
    public AbstractAction getRotateDownAction()
    {
        return rotateDownAction;
    }

    /**
    *  Gets the rotateLeft action.
    */
    public AbstractAction getRotateLeftAction()
    {
        return rotateLeftAction;
    }

    /**
    *  Gets the rotateRight action.
    */
    public AbstractAction getRotateRightAction()
    {
        return rotateRightAction;
    }

    /**
    *  Gets the zoomIn action.
    */
    public AbstractAction getZoomInAction()
    {
        return zoomInAction;
    }

    /**
    *  Gets the zoomOut action.
    */
    public AbstractAction getZoomOutAction()
    {
        return zoomOutAction;
    }

    /**
    *  Gets the reset view action.
    */
    public AbstractAction getResetViewAction()
    {
        return resetViewAction;
    }

    /**
    *  Adds all events to this GLCanvas.
    */
    public void addAllEvents()
    {
        this.addKeyListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
        this.addGLEventListener(this);
    }

    /**
    *  Removes all events from this GLCanvas.
    */
    public void removeAllEvents()
    {
        this.removeGLEventListener(this);
        this.removeMouseWheelListener(this);
        this.removeMouseMotionListener(this);
        this.removeMouseListener(this);
        this.removeKeyListener(this);
    }

    /**
    *  Prepares the rotation.
    */
    private void prepareRotation()
    {
        // prepares the rotation variables
        incrementX = (0.5f + nextFloat() / 2.0f) * ROTATION_MAX_INCREMENT; // ROTATION_MAX_INCREMENT / 2.0f - ROTATION_MAX_INCREMENT degrees
        incrementY = (0.5f + nextFloat() / 2.0f) * ROTATION_MAX_INCREMENT;
        incrementZ = (0.5f + nextFloat() / 2.0f) * ROTATION_MAX_INCREMENT;
    }

    /**
    *  Prepares the OpenGL lights.
    */
    private void prepareLighting(GL2 gl)
    {
        if ( !MATERIAL_SMOOTH_SHADING.get() )
            gl.glShadeModel(GL_FLAT);
        else
            gl.glShadeModel(GL_SMOOTH);

        gl.glLightfv(GL_LIGHT0, GL_AMBIENT, LIGHT_AMBIENT_ARRAY);
        gl.glLightfv(GL_LIGHT0, GL_DIFFUSE, LIGHT_DIFFUSE_ARRAY);
        gl.glLightfv(GL_LIGHT0, GL_SPECULAR, LIGHT_SPECULAR_ARRAY);
        gl.glLightfv(GL_LIGHT0, GL_POSITION, LIGHT0_POSITION);

        gl.glLightfv(GL_LIGHT1, GL_AMBIENT, LIGHT_AMBIENT_ARRAY);
        gl.glLightfv(GL_LIGHT1, GL_DIFFUSE, LIGHT_DIFFUSE_ARRAY);
        gl.glLightfv(GL_LIGHT1, GL_SPECULAR, LIGHT_SPECULAR_ARRAY);
        gl.glLightfv(GL_LIGHT1, GL_POSITION, LIGHT1_POSITION);

        // enable light sources
        gl.glEnable(GL_LIGHTING);
        gl.glEnable(GL_LIGHT0);
        gl.glEnable(GL_LIGHT1);

        gl.glLightModelfv(GL_LIGHT_MODEL_AMBIENT, MODEL_AMBIENT_ARRAY); // Setup a small white ambient light
        gl.glLightModeli(GL_LIGHT_MODEL_LOCAL_VIEWER, LOCAL_VIEWER);
        // gl.glLightModeli(GL_LIGHT_MODEL_TWO_SIDE, GL_TRUE);           // Don't enable two-sided light as it creates problems with Gouraud (fixed pipeline) lighting & SuperQuadric shapes

        if (USE_SHADERS_PROCESS)
        {
            String versionString = (USE_330_SHADERS_PROCESS) ? MINIMUM_GLSL_VERSION_FOR_330_SHADERS + " " + GLSL_LANGUAGE_MODE : MINIMUM_GLSL_VERSION_FOR_120_SHADERS;
            String GLSLPreprocessorCommands = "#version " + versionString + "\n" +
                                              "#define GPU_SHADER4_COMPATIBILITY_CONDITION "          + ( USE_GL_EXT_GPU_SHADER4 ? 1 : 0 )                + "\n" +
                                              "#define MAX_LIGHTS "                                   + MAX_LIGHTS                                        + "\n" +
                                              "#define USE_SPOT_LIGHTS "                              + ( (USE_SPOT_LIGHTS) ? 1 : 0 )                     + "\n" +
                                              "#define VS_VARYING "                                   + ( (USE_330_SHADERS_PROCESS) ? "out" : "varying" ) + "\n" +
                                              "#define GS_VARYING "                                   + ( (USE_330_SHADERS_PROCESS) ? ""    : "varying" ) + "\n" +
                                              "#define FS_VARYING "                                   + ( (USE_330_SHADERS_PROCESS) ? "in"  : "varying" ) + "\n" +
                                              "#define VS_EYE_VECTOR "                                + "EyeVector" + "\n" +
                                              "#define VS_NORMAL "                                    + "Normal"    + "\n" +
                                              "#define FS_EYE_VECTOR "                                + "EyeVector" + "\n" +
                                              "#define FS_NORMAL "                                    + "Normal"    + "\n" +
                                              "#define FS_COLOR "                                     + "Color"     + "\n"
                                              ;

            ShaderUtils.loadShaderFileCompileAndLinkProgram(gl, MODEL_SHAPE_SHADER_FILES_DIRECTORY, MODEL_SHAPE_SHADER_NAME, LOAD_SHADER_PROGRAMS_FROM_EXTERNAL_SOURCE, VERTEX_SHADER, FRAGMENT_SHADER, SHADER_PROGRAM, 0, GLSLPreprocessorCommands, DEBUG_BUILD);
            SHADER_PROGRAM_USE_OREN_NAYAR_DIFFUSE_MODEL[0] = gl.glGetUniformLocation(SHADER_PROGRAM[0], SHADER_PROGRAM_USE_OREN_NAYAR_DIFFUSE_MODEL_NAME);
            SHADER_PROGRAM_SHRINK_TRIANGLES[0] = gl.glGetUniformLocation(SHADER_PROGRAM[0], SHADER_PROGRAM_SHRINK_TRIANGLES_NAME);
            SHADER_PROGRAM_NORMALS[0] = gl.glGetUniformLocation(SHADER_PROGRAM[0], SHADER_PROGRAM_NORMALS_NAME);
            SHADER_PROGRAM_TEXTURING[0] = gl.glGetUniformLocation(SHADER_PROGRAM[0], SHADER_PROGRAM_TEXTURING_NAME);
            SHADER_PROGRAM_2D_TEXTURE[0] = gl.glGetUniformLocation(SHADER_PROGRAM[0], SHADER_PROGRAM_2D_TEXTURE_NAME);
            SHADER_PROGRAM_SPHERICAL_MAPPING[0] = gl.glGetUniformLocation(SHADER_PROGRAM[0], SHADER_PROGRAM_SPHERICAL_MAPPING_NAME);
        }
    }

    /**
    *  Uses the OpenGL scene materials.
    */
    private void useSceneMaterial(GL2 gl)
    {
        if ( MATERIAL_SPECULAR.get() )
        {
            gl.glMaterialfv(GL_FRONT, GL_SPECULAR, LIGHT_SPECULAR_ARRAY);
            gl.glMaterialf(GL_FRONT, GL_SHININESS, MATERIAL_SPECULAR_SHINE.get());
        }
        else
        {
            gl.glMaterialfv(GL_FRONT, GL_SPECULAR, NO_LIGHT_SPECULAR_ARRAY);
            gl.glMaterialf(GL_FRONT, GL_SHININESS, 0.0f);
        }
    }

    /**
    *  Creates the model shape.
    */
    private ModelShape createModelShape(GL2 gl)
    {
        if ( modelShapeType.equals(LATHE3D_SHAPE) )
        {
            modelSettings.centerModel = true; // Lathe3D Shapes will be centered
            LATHE3D_SETTINGS.splineStep = tesselation / 3;
            return Lathe3DShapesProducer.createLathe3DShape(gl, LATHE3D_SETTINGS, graph.getLathe3DShapeAngleIncrement(tesselation), modelSettings);
        }
        else if ( modelShapeType.equals(SUPER_QUADRIC_SHAPE) )
        {
            modelSettings.centerModel = false; // SuperQuadric Shapes are alredy pre-centered
            SUPER_QUADRIC_SETTINGS.uSegments = SUPER_QUADRIC_SETTINGS.vSegments = tesselation;
            return new SuperQuadricShape(gl, SUPER_QUADRIC_SETTINGS, modelSettings);
        }
        else // if ( modelShapeType.equals(OBJ_MODEL_LOADER_SHAPE) )
        {
            try
            {
                return new OBJModelLoader(gl, this, EXTERNAL_OBJ_MODEL_FILE_PATH, EXTERNAL_OBJ_MODEL_FILE_NAME + ".obj", OBJ_MODEL_LOADER_SHAPE_SIZE.get(), modelRenderingState, USE_EXTERNAL_OBJ_MODEL_FILE, false);
            }
            catch (Exception exc)
            {
                JOptionPane.showMessageDialog(modelShapeEditorParentUIDialog, "OBJ Model Loader error caught: " + exc.getMessage() + ".\nPossible OBJ file format problem.\nNow using default " + DEFAULT_OBJ_MODEL_SHAPE_NAME + " shape.", "OBJ Model Loader Problem!", JOptionPane.WARNING_MESSAGE);
                if (DEBUG_BUILD) println("OBJ Model Loader error caught: " + exc.getMessage() + ".\nPossible OBJ file format problem.\nNow using default " + DEFAULT_OBJ_MODEL_SHAPE_NAME + " shape.");

                EXTERNAL_OBJ_MODEL_FILE_PATH = MODEL_FILES_PATH;
                EXTERNAL_OBJ_MODEL_FILE_NAME = DEFAULT_OBJ_MODEL_SHAPE_NAME;
                OBJ_MODEL_LOADER_SHAPE_SIZE.set(DEFAULT_OBJ_MODEL_SHAPE_SIZE);
                USE_EXTERNAL_OBJ_MODEL_FILE = false;
                modelShapeEditorParentUIDialog.updateSizesComboBoxWithDefaultOBJModelShapeSize();

                return new OBJModelLoader(gl, this, EXTERNAL_OBJ_MODEL_FILE_PATH, EXTERNAL_OBJ_MODEL_FILE_NAME + ".obj", OBJ_MODEL_LOADER_SHAPE_SIZE.get(), modelRenderingState, USE_EXTERNAL_OBJ_MODEL_FILE, false);
            }
        }
    }

    /**
    *  Clears the screen.
    */
    private void clearScreen3D(GL2 gl)
    {
        if ( TRIPPY_BACKGROUND.get() )
            graph.colorCycle(BACKGROUND_COLOR_ARRAY);

        gl.glClearColor(BACKGROUND_COLOR_ARRAY[0], BACKGROUND_COLOR_ARRAY[1], BACKGROUND_COLOR_ARRAY[2], 1.0f);
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Clear The Screen And The Depth Buffer
    }

    /**
    *  Renders the Cartesian coordinate lines.
    */
    private void renderCartesianCoordinateLines(GL2 gl)
    {
        Point3D allAxesLengths = modelShape.getAllAxes();
        // x-axis step-by-step line length change instead of multiplying with (1.0f + userScaleX) for a continuous change
        if (userScaleX < 0.0f)
            allAxesLengths.x /= 2.0f;
        else if (userScaleX >= 0.9f)
            allAxesLengths.x *= 2.0f;
        // y-axis step-by-step line length change instead of multiplying with (1.0f + userScaleY) for a continuous change
        if (userScaleY < 0.0f)
            allAxesLengths.y /= 2.0f;
        else if (userScaleY >= 0.9f)
            allAxesLengths.y *= 2.0f;
        // z-axis step-by-step line length change instead of multiplying with (1.0f + userScaleZ) for a continuous change
        if (userScaleZ < 0.0f)
            allAxesLengths.z /= 2.0f;
        else if (userScaleZ >= 0.9f)
            allAxesLengths.z *= 2.0f;

        gl.glDisable(GL_LIGHTING);
        Color.RED.getRGBColorComponents(AXIS_LINE_COLOR);
        if ( ANAGLYPH_STEREOSCOPIC_3D_VIEW.get() ) graph.createGrayScaleColor(AXIS_LINE_COLOR);
        gl.glColor3fv(AXIS_LINE_COLOR, 0);

        gl.glBegin(GL_LINES);
            // x-axis lines
            gl.glVertex3f(0.0f, 0.0f, 0.0f);
            gl.glVertex3f( allAxesLengths.x, 0.0f, 0.0f);
            gl.glVertex3f(0.0f, 0.0f, 0.0f);
            gl.glVertex3f(-allAxesLengths.x, 0.0f, 0.0f);
            // y-axis lines
            gl.glVertex3f(0.0f, 0.0f, 0.0f);
            gl.glVertex3f(0.0f, allAxesLengths.y, 0.0f);
            gl.glVertex3f(0.0f, 0.0f, 0.0f);
            gl.glVertex3f(0.0f, -allAxesLengths.y, 0.0f);
            // z-axis lines
            gl.glVertex3f(0.0f, 0.0f, 0.0f);
            gl.glVertex3f(0.0f, 0.0f,  allAxesLengths.z);
            gl.glVertex3f(0.0f, 0.0f, 0.0f);
            gl.glVertex3f(0.0f, 0.0f, -allAxesLengths.z);
        gl.glEnd();

        if (DRAW_AXES_LEGENDS)
        {
            // x-axis legend
            gl.glRasterPos3f( allAxesLengths.x, 0.0f, 0.0f);
            GLUT.glutBitmapString(EDGE_NAMES_OPENGL_FONT_TYPE, "X");
            gl.glRasterPos3f(-allAxesLengths.x, 0.0f, 0.0f);
            GLUT.glutBitmapString(EDGE_NAMES_OPENGL_FONT_TYPE, "X");
            // y-axis legend
            gl.glRasterPos3f(0.0f,  allAxesLengths.y, 0.0f);
            GLUT.glutBitmapString(EDGE_NAMES_OPENGL_FONT_TYPE, "Y");
            gl.glRasterPos3f(0.0f, -allAxesLengths.y, 0.0f);
            GLUT.glutBitmapString(EDGE_NAMES_OPENGL_FONT_TYPE, "Y");
            // z-axis legend
            gl.glRasterPos3f(0.0f, 0.0f,  allAxesLengths.z);
            GLUT.glutBitmapString(EDGE_NAMES_OPENGL_FONT_TYPE, "Z");
            gl.glRasterPos3f(0.0f, 0.0f, -allAxesLengths.z);
            GLUT.glutBitmapString(EDGE_NAMES_OPENGL_FONT_TYPE, "Z");
        }

        gl.glEnable(GL_LIGHTING);
    }

    /**
    *  Renders the scene.
    */
    private void renderScene(GL2 gl)
    {
        if (nodeTexture == null)
        {
            if (DEBUG_BUILD)
            {
                println("ModelShapeRenderer.renderScene aborted because nodeTexture is null");
            }

            return;
        }

        if (USE_SHADERS_PROCESS && usePhongNLightsShader)
        {
            gl.glUseProgram(SHADER_PROGRAM[0]);
            gl.glUniform1i(SHADER_PROGRAM_USE_OREN_NAYAR_DIFFUSE_MODEL[0], (useOrenNayarDiffuseModel) ? 1 : 0);
            gl.glUniform1i(SHADER_PROGRAM_SHRINK_TRIANGLES[0], (wireframeView && normalsView) ? 1 : 0);
            gl.glUniform1i(SHADER_PROGRAM_NORMALS[0], (normalsView) ? 1 : 0);
            boolean enableGLSLTexturing = ( texturingView && !isOBJModelLoaderType() ) || modelShape.getHasTexture();
            gl.glUniform1i(SHADER_PROGRAM_TEXTURING[0], (enableGLSLTexturing) ? 1 : 0);
            if (enableGLSLTexturing)
            {
                gl.glUniform1i(SHADER_PROGRAM_2D_TEXTURE[0], ACTIVE_TEXTURE_UNIT_FOR_2D_TEXTURE);
                gl.glUniform1i(SHADER_PROGRAM_SPHERICAL_MAPPING[0], ( sphericalMappingView && !isOBJModelLoaderType() ) ? 1 : 0);
            }
        }

        if (changeDetected)
        {
            // temporarily stop the frameskip while the Model Shape is getting updated
            boolean flag = isAnimating() ? graphRendererThreadUpdater.getFrameskip() : false;
            if (flag) graphRendererThreadUpdater.setFrameskip(false);

            // change detected, dispose old model shape
            changeDetected = false;
            modelShape.disposeAllModelShapeResources(gl);
            modelShape = createModelShape(gl);

            if (flag) graphRendererThreadUpdater.setFrameskip(true);
        }

        useSceneMaterial(gl);

        if ( texturingView && !isOBJModelLoaderType() )
        {
            nodeTexture.bind(gl);
            nodeTexture.enable(gl);

            if (sphericalMappingView) enableGenerateSphericalTextureCoordinates(gl);
        }
        else
            nodeTexture.disable(gl);

        gl.glPushMatrix();
            gl.glScalef(1.0f + userScaleX, 1.0f + userScaleY, 1.0f + userScaleZ);
            gl.glRotatef(userRotationX, 1.0f, 0.0f, 0.0f);
            gl.glRotatef(userRotationY, 0.0f, 1.0f, 0.0f);
            gl.glRotatef(userRotationZ, 0.0f, 0.0f, 1.0f);
            modelShape.drawModelShape(gl); // draw the modelShape
        gl.glPopMatrix();

        if ( texturingView && !isOBJModelLoaderType() )
        {
            nodeTexture.disable(gl);

            if (sphericalMappingView) disableGenerateSphericalTextureCoordinates(gl);
        }

        if (USE_SHADERS_PROCESS && usePhongNLightsShader) gl.glUseProgram(0);
    }

    /**
    *  Sets the model shape.
    */
    public void setModelShapeType(ModelTypes modelShapeType)
    {
        this.modelShapeType = modelShapeType;
    }

    /**
    *  Gets if the model shape is of OBJ Model Loader type.
    */
    public boolean isOBJModelLoaderType()
    {
        return modelShapeType.equals(OBJ_MODEL_LOADER_SHAPE);
    }

    /**
    *  Sets the tesselation variable.
    */
    public void setTesselation(int tesselation)
    {
        this.tesselation = (tesselation < MINIMUM_TESSELATION_VALUE) ? MINIMUM_TESSELATION_VALUE : tesselation;
    }

    /**
    *  Gets the tesselation variable.
    */
    public int getTesselation()
    {
        return tesselation;
    }

    /**
    *  Sets the changeDetected variable.
    */
    public void setChangeDetected(boolean changeDetected)
    {
        this.changeDetected = changeDetected;
    }

    /**
    *  Sets the deAllocOpenGLMemory variable.
    */
    public void setDeAllocOpenGLMemory(boolean deAllocOpenGLMemory)
    {
        this.deAllocOpenGLMemory = deAllocOpenGLMemory;
    }

    /**
    *  Sets the closingDialogWindow variable.
    */
    public void setClosingDialogWindow(boolean closingDialogWindow)
    {
        this.closingDialogWindow = closingDialogWindow;
    }

    /**
    *  Sets the wireframeView variable.
    */
    public void setWireframeView(boolean wireframeView)
    {
        this.wireframeView = wireframeView;
    }

    /**
    *  Sets the normalsView variable.
    */
    public void setNormalsView(boolean normalsView)
    {
        this.normalsView = normalsView;
    }

    /**
    *  Sets the texturingView variable.
    */
    public void setTexturingView(boolean texturingView)
    {
        this.texturingView = texturingView;
    }

    /**
    *  Sets the sphericalMappingView variable.
    */
    public void setSphericalMappingView(boolean sphericalMappingView)
    {
        this.sphericalMappingView = sphericalMappingView;
    }

    /**
    *  Sets the userScaleX variable.
    */
    public void setUserScaleX(float userScaleX)
    {
        this.userScaleX = (userScaleX == -1.0f) ? MINIMUM_USER_SCALE_VALUE : userScaleX;
    }

    /**
    *  Sets the userScaleY variable.
    */
    public void setUserScaleY(float userScaleY)
    {
        this.userScaleY = (userScaleY == -1.0f) ? MINIMUM_USER_SCALE_VALUE : userScaleY;
    }

    /**
    *  Sets the userScaleZ variable.
    */
    public void setUserScaleZ(float userScaleZ)
    {
        this.userScaleZ = (userScaleZ == -1.0f) ? MINIMUM_USER_SCALE_VALUE : userScaleZ;
    }

    /**
    *  Sets the userRotationX variable.
    */
    public void setUserRotationX(float userRotationX)
    {
        this.userRotationX = userRotationX;
    }

    /**
    *  Sets the userRotationY variable.
    */
    public void setUserRotationY(float userRotationY)
    {
        this.userRotationY = userRotationY;
    }

    /**
    *  Sets the userRotationZ variable.
    */
    public void setUserRotationZ(float userRotationZ)
    {
        this.userRotationZ = userRotationZ;
    }

    /**
    *  Sets the shape name.
    */
    public void setShapeName(String shapeName)
    {
        this.modelShape.setShapeName(shapeName);
    }

    /**
    *  Saves the Model Shape file in OBJ file format.
    *  The ModelShape must be either a Lathe3D or a SuperQuadric one.
    */
    public void saveModelShapeOBJFile(FileWriter fileWriter, ModelTypes modelType) throws IOException
    {
        if (modelShape != null) modelShape.saveModelShapeOBJFile(fileWriter, modelType);
    }

    /**
    *  Does the same thing as GLU.gluPerspective() but in one step.
    */
    private void setPerspective(GL2 gl, double fovy, double aspect, double zNear, double zFar)
    {
        double top = zNear * tan(fovy * PI / 360.0);
        double bottom = -top;
        double right = aspect * top;
        double left = -right;

        gl.glFrustum(left, right, bottom, top, zNear, zFar);
    }

    /**
    *  Rotates the current view.
    */
    private void rotate(int startX, int startY, int x, int y)
    {
        mouseRotationX -= (startX - x); // reverted sign to emulate the main BL view behaviour
        mouseRotationY += (startY - y);
    }

    /**
    *  Scales the current view.
    */
    private void scale(int startX, int startY, int x, int y)
    {
        scaleValue += ( (scaleValue > 5.0f) ? ( ( (startX - x) - (startY - y) ) / 400.0f ) * (1.0f + scaleValue)
                                            : ( ( (startX - x) - (startY - y) ) / 40.0f ) );
    }

    /**
    *  Translates the current view.
    */
    private void translate(int startX, int startY, int x, int y)
    {
        translateDX += ( (startX - x) / (FAR_DISTANCE / 4.0f) ) * (1.0f + scaleValue);
        translateDY += ( (startY - y) / (FAR_DISTANCE / 4.0f) ) * (1.0f + scaleValue);
    }

    /**
    *  Resets the view.
    */
    public void resetView()
    {
        mouseRotationX = 0.0f;
        mouseRotationY = 0.0f;
        scaleValue = DEFAULT_SCALE;
        translateDX = 0.0f;
        translateDY = 0.0f;

        autoRotationX = 0.0f;
        autoRotationY = 0.0f;
        autoRotationZ = 0.0f;
    }

    /**
    *  Refreshes the OpenGL display.
    */
    public void refreshDisplay()
    {
        this.display();
    }

    /**
    *  Enables the spherical texture coordinates generation.
    */
    private void enableGenerateSphericalTextureCoordinates(GL2 gl)
    {
        gl.glEnable(GL_TEXTURE_GEN_S);
        gl.glEnable(GL_TEXTURE_GEN_T);
        gl.glTexGeni(GL_S, GL_TEXTURE_GEN_MODE, GL_SPHERE_MAP);
        gl.glTexGeni(GL_T, GL_TEXTURE_GEN_MODE, GL_SPHERE_MAP);
    }

    /**
    *  Disables the spherical texture coordinates generation.
    */
    private void disableGenerateSphericalTextureCoordinates(GL2 gl)
    {
        gl.glDisable(GL_TEXTURE_GEN_S);
        gl.glDisable(GL_TEXTURE_GEN_T);
    }

    /**
    *  Starts the updating/rendering thread(s).
    */
    public synchronized void startRender()
    {
        if (graphRendererThreadUpdater == null)
        {
            graphRendererThreadUpdater = new GraphRendererThreadUpdater(this, this, FRAMESKIP, false, DEFAULT_FPS);
            graphRendererThreadUpdater.startWithPriority(Thread.NORM_PRIORITY);
        }
    }

    /**
    *  Stops updating/rendering thread(s).
    */
    public synchronized void stopRender()
    {
        if (graphRendererThreadUpdater != null)
        {
            graphRendererThreadUpdater.setRendering(false);
            graphRendererThreadUpdater = null;
        }
    }

    /**
    *  ModelShapeRenderer is animating.
    */
    public boolean isAnimating()
    {
        return (graphRendererThreadUpdater != null);
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
    *  Called by the JOGL2 glDrawable immediately after the OpenGL context is initialized.
    */
    @Override
    public void init(GLAutoDrawable glDrawable)
    {
        if (DEBUG_BUILD) println("ModelShapeRenderer init()");

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
        if ( NORMAL_QUALITY_ANTIALIASING.get() || HIGH_QUALITY_ANTIALIASING.get() )
            graph.prepareHighQualityRendering(gl);
        else
            graph.prepareLowQualityRendering(gl);

        // enable the z-buffer algorithm
        gl.glClearDepth(1.0);                                  // Specifies the clear value for the depth buffer (1.0 is the initial value)
        gl.glEnable(GL_DEPTH_TEST);                            // Enables Depth Testing
        gl.glDepthFunc(GL_LEQUAL);                             // The Type Of Depth Test To Do
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);  // Really Nice Perspective Calculations

        // Disable back-face removal for convex objects, as we may need to render the inside of a shape
        gl.glDisable(GL_CULL_FACE);
        gl.glCullFace(GL_BACK);

        // for line antialiasing and blending options usage only
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        prepareRotation();
        prepareLighting(gl);

        nodeTexture = graph.prepareNodeTexture(gl, nodeTexture);
        modelShape = createModelShape(gl);
        clearScreen3D(gl);
        gl.glFlush();

        currentGLError = (closingDialogWindow) ? GL_NO_ERROR : gl.glGetError();
        boolean checkError = (DEBUG_BUILD) ? (currentGLError != GL_NO_ERROR) : (currentGLError != GL_NO_ERROR) && (currentGLError == GL_OUT_OF_MEMORY);
        if ( checkError && (currentGLError != prevGLError) )
        {
            prevGLError = currentGLError;
            String error = GLU.gluErrorString(currentGLError);
            JOptionPane.showMessageDialog(modelShapeEditorParentUIDialog, "OpenGL reported an error: " + error + "!", "OpenGL Error: " + error, JOptionPane.WARNING_MESSAGE);
            if (DEBUG_BUILD) println("ModelShapeRenderer init(GLAutoDrawable) glGetError() returned: " + error);
        }
    }

    /**
    *  Called by the JOGL2 glDrawable to initiate OpenGL rendering by the client.
    */
    @Override
    public void display(GLAutoDrawable glDrawable)
    {
        if (DEBUG_BUILD) println("ModelShapeRenderer display()");

        GL2 gl = glDrawable.getGL().getGL2();

        if (!deAllocOpenGLMemory)
        {
            gl.setSwapInterval(USE_VSYNCH.get() ? 1 : 0); // 0 for no VSynch, 1 for VSynch
            gl.glPolygonMode(GL_FRONT_AND_BACK, wireframeView ? GL_LINE : GL_FILL);
            clearScreen3D(gl);

            gl.glLoadIdentity();
            if ( !ANAGLYPH_STEREOSCOPIC_3D_VIEW.get() )
            {
                CENTER_VIEW_CAMERA.setCamera(gl, translateDX, translateDY, scaleValue, autoRotationX + mouseRotationX, autoRotationY + mouseRotationY, autoRotationZ, null, false);
                // CENTER_VIEW_CAMERA.setCamera(gl, GLU, 0.0, 0.0, Z_DISTANCE, 0.0, 0.0, 0.0); (GLU.gluLookAt(0.0, 0.0, Z_DISTANCE, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0); // position camera)
                renderCartesianCoordinateLines(gl);
                renderScene(gl);
            }
            else
            {
                graph.chooseAnaglyphGlassesColorMask(gl, true);
                LEFT_EYE_CAMERA.setProjectionAndCamera(gl, translateDX, translateDY, scaleValue, autoRotationX + mouseRotationX, autoRotationY + mouseRotationY, autoRotationZ, null, false);
                // LEFT_EYE_CAMERA.setCamera(gl, GLU, 0.0, 0.0, Z_DISTANCE, 0.0, 0.0, 0.0); (GLU.gluLookAt(0.0, 0.0, Z_DISTANCE, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0); // position camera)
                renderCartesianCoordinateLines(gl);
                renderScene(gl);

                // reset the left eye transformations to continue with the right eye
                gl.glLoadIdentity();

                graph.chooseAnaglyphGlassesColorMask(gl, false);
                RIGHT_EYE_CAMERA.setProjectionAndCamera(gl, translateDX, translateDY, scaleValue, autoRotationX + mouseRotationX, autoRotationY + mouseRotationY, autoRotationZ, null, false);
                // RIGHT_EYE_CAMERA.setCamera(gl, GLU, 0.0, 0.0, Z_DISTANCE, 0.0, 0.0, 0.0); (GLU.gluLookAt(0.0, 0.0, Z_DISTANCE, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0); // position camera)
                renderCartesianCoordinateLines(gl);
                renderScene(gl);

                gl.glColorMask(true, true, true, true); // reset color mask so as to clear screen properly
            }

            // Take the contents of the current draw buffer and copy it to the accumulation buffer with each pixel modified by a factor
            // The closer the factor is to 1.0f, the longer the trails... Don't exceed 1.0f - you get garbage.
            if (!GL_IS_AMD_ATI) // recent AMD/ATI cards lack an accumulation buffer
            {
                if ( USE_MOTION_BLUR_FOR_SCENE.get() )
                {
                    gl.glAccum(GL_MULT, MOTION_BLUR_SIZE.get());
                    gl.glAccum(GL_ACCUM, 1 - MOTION_BLUR_SIZE.get());
                    gl.glAccum(GL_RETURN, 1.0f);
                }
            }
        }
        else
        {
            deAllocOpenGLMemory = false;
            if (USE_SHADERS_PROCESS)
            {
                ShaderUtils.detachAndDeleteShader(gl, VERTEX_SHADER, FRAGMENT_SHADER, SHADER_PROGRAM, 0);
            }
            nodeTexture = null;
            modelShape.disposeAllModelShapeResources(gl);

            if (DEBUG_BUILD) println("Deallocated all ModelShapeRenderer resources.");
        }

        gl.glFlush();

        currentGLError = (closingDialogWindow) ? GL_NO_ERROR : gl.glGetError();
        boolean checkError = (DEBUG_BUILD) ? (currentGLError != GL_NO_ERROR) : (currentGLError != GL_NO_ERROR) && (currentGLError == GL_OUT_OF_MEMORY);
        if ( checkError && (currentGLError != prevGLError) )
        {
            prevGLError = currentGLError;
            String error = GLU.gluErrorString(currentGLError);
            JOptionPane.showMessageDialog(modelShapeEditorParentUIDialog, "OpenGL reported an error: " + error + "!", "OpenGL Error: " + error, JOptionPane.WARNING_MESSAGE);
            if (DEBUG_BUILD) println("ModelShapeRenderer display(GLAutoDrawable) glGetError() returned: " + error);
        }
    }

    /**
    *  Called by the JOGL2 glDrawable during the first repaint after the component has been resized.
    */
    @Override
    public void reshape(GLAutoDrawable glDrawable, int x, int y, int widthCanvas, int heightCanvas)
    {
        if (DEBUG_BUILD) println("ModelShapeRenderer reshape()");

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

        double intraOcularDistance = extractDouble(GRAPH_INTRA_OCULAR_DISTANCE_TYPE);
        LEFT_EYE_CAMERA.setIntraOcularDistanceAndFrustumShift(DEFAULT_INTRA_OCULAR_DISTANCE);
        LEFT_EYE_CAMERA.updateFrustumDimensions(width, height);
        LEFT_EYE_CAMERA.setIntraOcularDistanceAndFrustumShift(intraOcularDistance);
        CENTER_VIEW_CAMERA.updateViewPortAndFrustumDimensions(gl, x, y, width, height);
        CENTER_VIEW_CAMERA.setProjection(gl);
        RIGHT_EYE_CAMERA.setIntraOcularDistanceAndFrustumShift(DEFAULT_INTRA_OCULAR_DISTANCE);
        RIGHT_EYE_CAMERA.updateFrustumDimensions(width, height);
        RIGHT_EYE_CAMERA.setIntraOcularDistanceAndFrustumShift(intraOcularDistance);

        gl.glFlush();

        currentGLError = (closingDialogWindow) ? GL_NO_ERROR : gl.glGetError();
        boolean checkError = (DEBUG_BUILD) ? (currentGLError != GL_NO_ERROR) : (currentGLError != GL_NO_ERROR) && (currentGLError == GL_OUT_OF_MEMORY);
        if ( checkError && (currentGLError != prevGLError) )
        {
            prevGLError = currentGLError;
            String error = GLU.gluErrorString(currentGLError);
            JOptionPane.showMessageDialog(modelShapeEditorParentUIDialog, "OpenGL reported an error: " + error + "!", "OpenGL Error: " + error, JOptionPane.WARNING_MESSAGE);
            if (DEBUG_BUILD) println("ModelShapeRenderer reshape(GLAutoDrawable) glGetError() returned: " + error);
        }
    }

    /**
    *  KeyPressed keyEvent.
    */
    @Override
    public void keyPressed(KeyEvent e)
    {
        if (DEBUG_BUILD) println("ModelShapeRenderer keyPressed()");
    }

    /**
    *  KeyReleased keyEvent.
    */
    @Override
    public void keyReleased(KeyEvent e)
    {
        if (DEBUG_BUILD) println("ModelShapeRenderer keyReleased()");
    }

    /**
    *  KeyTyped keyEvent.
    */
    @Override
    public void keyTyped(KeyEvent e)
    {
        if (DEBUG_BUILD) println("ModelShapeRenderer keyTyped()");

        if (e.getKeyChar() == '1')
        {
            usePhongNLightsShader = !usePhongNLightsShader;
            refreshDisplay();
        }
        else if (e.getKeyChar() == 'd' || e.getKeyChar() == 'D')
        {
            useOrenNayarDiffuseModel = !useOrenNayarDiffuseModel;
            refreshDisplay();
        }
    }

    /**
    *  MouseClicked mouseEvent.
    */
    @Override
    public void mouseClicked(MouseEvent e)
    {
        if (DEBUG_BUILD) println("ModelShapeRenderer mouseClicked()");
    }

    /**
    *  MouseEntered mouseEvent.
    */
    @Override
    public void mouseEntered(MouseEvent e)
    {
        if (DEBUG_BUILD) println("ModelShapeRenderer mouseEntered()");
    }

    /**
    *  MouseExited mouseEvent.
    */
    @Override
    public void mouseExited(MouseEvent e)
    {
        if (DEBUG_BUILD) println("ModelShapeRenderer mouseExited()");
    }

    /**
    *  MousePressed mouseEvent.
    */
    @Override
    public void mousePressed(MouseEvent e)
    {
        if (DEBUG_BUILD) println("ModelShapeRenderer mousePressed()");

        mouseButton = e.getButton();
        mouseLastX = e.getX();
        mouseLastY = e.getY();
    }

    /**
    *  MouseReleased mouseEvent.
    */
    @Override
    public void mouseReleased(MouseEvent e)
    {
        if (DEBUG_BUILD) println("ModelShapeRenderer mouseReleased()");

        mouseButton = 0;
        mouseLastX = 0;
        mouseLastY = 0;
    }

    /**
    *  MouseDragged mouseMotionEvent.
    */
    @Override
    public void mouseDragged(MouseEvent e)
    {
        if (DEBUG_BUILD) println("ModelShapeRenderer mouseDragged()");

        if ( (mouseButton == MouseEvent.BUTTON1) && ( currentMouseMode.equals(MouseModeTypes.ROTATE) || currentMouseMode.equals(MouseModeTypes.SELECT) ) )
        {

            rotate( mouseLastX, mouseLastY, e.getX(), e.getY() );
            refreshDisplay();

            mouseLastX = e.getX();
            mouseLastY = e.getY();
        }
        else if ( (mouseButton == MouseEvent.BUTTON2) || currentMouseMode.equals(MouseModeTypes.TRANSLATE) )
        {
            translate( mouseLastX, mouseLastY, e.getX(), e.getY() );
            refreshDisplay();

            mouseLastX = e.getX();
            mouseLastY = e.getY();
        }
        else if ( (mouseButton == MouseEvent.BUTTON3) || currentMouseMode.equals(MouseModeTypes.SCALE) )
        {
            scale( mouseLastX, mouseLastY, e.getX(), e.getY() );
            refreshDisplay();

            mouseLastX = e.getX();
            mouseLastY = e.getY();
        }
    }

    /**
    *  MouseMoved mouseMotionEvent.
    */
    @Override
    public void mouseMoved(MouseEvent e)
    {
        if (DEBUG_BUILD) println("ModelShapeRenderer mouseMoved()");
    }

    /**
    *  MouseWheelMoved mouseWheelEvent.
    */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        if (DEBUG_BUILD) println("ModelShapeRenderer mouseWheelMoved()");
    }

    /**
    *  Creates the reTranslate action.
    */
    @Override
    public void createReTranslateAction(TranslateTypes translateType, ActionEvent e)
    {
        if ( translateType.equals(TranslateTypes.TRANSLATE_UP) )
            translateDY += DEFAULT_TRANSLATION;
        else if ( translateType.equals(TranslateTypes.TRANSLATE_DOWN) )
            translateDY -= DEFAULT_TRANSLATION;
        else if ( translateType.equals(TranslateTypes.TRANSLATE_LEFT) )
            translateDX += DEFAULT_TRANSLATION;
        else if ( translateType.equals(TranslateTypes.TRANSLATE_RIGHT) )
            translateDX -= DEFAULT_TRANSLATION;

        refreshDisplay();

        if (DEBUG_BUILD) println("ModelShapeRenderer reTranslate()");
    }

    /**
    *  Creates the reRotate action.
    */
    @Override
    public void createReRotateAction(RotateTypes rotateType, ActionEvent e)
    {
        if ( rotateType.equals(RotateTypes.ROTATE_UP) )
            mouseRotationY -= DEFAULT_ROTATION;
        else if ( rotateType.equals(RotateTypes.ROTATE_DOWN) )
            mouseRotationY += DEFAULT_ROTATION;
        else if ( rotateType.equals(RotateTypes.ROTATE_LEFT) )
            mouseRotationX += DEFAULT_ROTATION;
        else if ( rotateType.equals(RotateTypes.ROTATE_RIGHT) )
            mouseRotationX -= DEFAULT_ROTATION;

        refreshDisplay();

        if (DEBUG_BUILD) println("ModelShapeRenderer reRotate()");
    }

    /**
    *  Creates the reScale action.
    */
    @Override
    public void createReScaleAction(ScaleTypes scaleType, ActionEvent e)
    {
        scaleValue /= scaleType.equals(ScaleTypes.SCALE_IN) ? SCALE_FACTOR : 1.0f / SCALE_FACTOR;

        refreshDisplay();

        if (DEBUG_BUILD) println("ModelShapeRenderer reScale()");
    }

    /**
    *  Creates the Burst Layout Iterations action.
    */
    @Override
    public void createBurstLayoutIterationsAction(ActionEvent e) {}

    /**
    *  Generally pauses the renderer.
    */
    @Override
    public void generalPauseRenderUpdateThread()
    {
        if (graphRendererThreadUpdater != null)
            graphRendererThreadUpdater.generalPauseRenderUpdateThread();
    }

    /**
    *  Generally resumes the renderer.
    */
    @Override
    public void generalResumeRenderUpdateThread()
    {
        if (graphRendererThreadUpdater != null)
            graphRendererThreadUpdater.generalResumeRenderUpdateThread();
    }

    /**
    *  Updates all animation.
    */
    @Override
    public void updateAnimation()
    {
        // update the rotations
        autoRotationX = (autoRotationX + incrementX) % 360.0f;
        autoRotationY = (autoRotationY + incrementY) % 360.0f;
        autoRotationZ = (autoRotationZ + incrementZ) % 360.0f;
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

