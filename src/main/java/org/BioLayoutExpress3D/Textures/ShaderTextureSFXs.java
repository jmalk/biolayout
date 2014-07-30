package org.BioLayoutExpress3D.Textures;

import java.awt.image.*;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.jogamp.opengl.util.texture.*;
import static java.lang.Math.*;
import static javax.media.opengl.GL2.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import static org.BioLayoutExpress3D.Textures.DrawTextureSFXs.*;
import static org.BioLayoutExpress3D.Graph.Graph.*;
import static org.BioLayoutExpress3D.Environment.AnimationEnvironment.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
*  Various GLSL shader texture operations used as special effects.
*  This class is responsible for producing textures using various effects using the GLSL specification.
*
*
* @see org.BioLayoutExpress3D.Textures.TextureSFXs
* @author Thanos Theo, 2008-2009
* @version 3.0.0.0
*
*/

public class ShaderTextureSFXs
{

    /**
    *  Available shader types.
    */
    public static enum ShaderTypes { TEXTURE_DISPLACEMENT, PLASMA, SPOT_CIRCLE, WATER, BUMP, BLUR, RADIAL_BLUR, BLOB_STARS_3D_SCROLLER, ANIMATION }

    /**
    *  Available number of texture shaders.
    */
    public static final int NUMBER_OF_AVAILABLE_SHADERS = ShaderTypes.values().length;

    /**
    *  Directory of GPU Computing.
    */
    private static final String GPU_COMPUTING_DIRECTORY = "GPUComputing/";

    /**
    *  Directory of 2D shader files.
    */
    private static final String SHADER_FILES_DIRECTORY_1 = "2D";

    /**
    *  Directory of Effects shader files.
    */
    private static final String SHADER_FILES_DIRECTORY_2 = "Effects";

    /**
    *  Directory of Animation shader files.
    */
    private static final String SHADER_FILES_DIRECTORY_3 = "Animation";

    /**
    *  File name of Effects shader files.
    */
    private static final String SHADER_FILE_NAME_2 = "Effects";

    /**
    *  File name of Animation shader files.
    */
    private static final String SHADER_FILE_NAME_3 = "Animation";

    /**
    *  Constant value needed for the spot circle random 2D texture.
    */
    private static final boolean USE_SPOT_CIRCLE_RANDOM_2D_TEXTURE = false;

    /**
    *  Constant value needed for the spot circle random 2D texture.
    */
    private static final int SPOT_CIRCLE_RANDOM_2D_TEXTURE_SIZE = 1024;

    /**
    *  Active texture unit to be used for the spot circle random 2D texture.
    */
    private static final int ACTIVE_TEXTURE_UNIT_FOR_SPOT_CIRCLE_RANDOM_2D_TEXTURE = ACTIVE_TEXTURE_UNIT_FOR_ANIMATION_SPECTRUM_2D_TEXTURE + 1;

    /**
    *  Active texture unit to be used for the water buffer 2D texture.
    */
    private static final int ACTIVE_TEXTURE_UNIT_FOR_WATER_BUFFER_2D_TEXTURE = ACTIVE_TEXTURE_UNIT_FOR_ANIMATION_SPECTRUM_2D_TEXTURE + 2;

    /**
    *  Active texture unit to be used for the bump emboss 2D texture.
    */
    private static final int ACTIVE_TEXTURE_UNIT_FOR_BUMP2D_EMBOSS_2D_TEXTURE = ACTIVE_TEXTURE_UNIT_FOR_ANIMATION_SPECTRUM_2D_TEXTURE + 3;

    /**
    *  Variable to store the support for the GL_EXT_framebuffer_object extension.
    */
    private boolean isGLExtFramebufferObjectSupported = false;

    /**
    *  Vertex shader storage.
    *  2 sets of shader files per shader program.
    */
    private final int[][] VERTEX_SHADERS = new int[NUMBER_OF_AVAILABLE_SHADERS][3];

    /**
    *  Fragmant shader storage.
    *  2 sets of shader files per shader program.
    */
    private final int[][] FRAGMENT_SHADERS = new int[NUMBER_OF_AVAILABLE_SHADERS][3];

    /**
    *  Shader program storage.
    */
    private final int[] SHADER_PROGRAMS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program 2D texture name.
    */
    private static final String SHADER_PROGRAM_2D_TEXTURE_NAME = "2DTexture";

    /**
    *  Shader program 2D texture storage.
    */
    private final int[] SHADER_PROGRAM_2D_TEXTURES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program Animation 2D texture name.
    */
    private static final String SHADER_PROGRAM_ANIMATION_2D_TEXTURE_NAME = "Animation2DTexture";

    /**
    *  Shader program Animation 2D texture storage.
    */
    private final int[] SHADER_PROGRAM_ANIMATION_2D_TEXTURES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program spot circle random 2D texture name.
    */
    private static final String SHADER_PROGRAM_SPOT_CIRCLE_RANDOM_2D_TEXTURE_NAME = "Random2DTexture";

    /**
    *  Shader program spot circle random 2D texture storage.
    */
    private final int[] SHADER_PROGRAM_SPOT_CIRCLE_RANDOM_2D_TEXTURES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program water buffer 2D texture name.
    */
    private static final String SHADER_PROGRAM_WATER_BUFFER_2D_TEXTURE_NAME = "Buffer2DTexture";

    /**
    *  Shader program water buffer 2D texture storage.
    */
    private final int[] SHADER_PROGRAM_WATER_BUFFER_2D_TEXTURES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program emboss 2D texture name.
    */
    private static final String SHADER_PROGRAM_BUMP2D_EMBOSS_2D_TEXTURE_NAME = "Emboss2DTexture";

    /**
    *  Shader program emboss 2D texture storage.
    */
    private final int[] SHADER_PROGRAM_BUMP2D_EMBOSS_2D_TEXTURES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program timer name.
    */
    private static final String SHADER_PROGRAM_TIMER_NAME = "Timer";

    /**
    *  Shader program timer storage.
    */
    private final int[] SHADER_PROGRAM_TIMERS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program transparency name.
    */
    private static final String SHADER_PROGRAM_TRANSPARENCY_NAME = "Transparency";

    /**
    *  Shader program transparency storage.
    */
    private final int[] SHADER_PROGRAM_TRANSPARENCIES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program old LCD style transparency name.
    */
    private static final String SHADER_PROGRAM_OLD_LCD_STYLE_TRANSPARENCY_NAME = "OldLCDStyleTransparency";

    /**
    *  Shader program old LCD style transparency storage.
    */
    private final int[] SHADER_PROGRAM_OLD_LCD_STYLE_TRANSPARENCIES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program center x name.
    */
    private static final String SHADER_PROGRAM_CENTER_X_NAME = "CenterX";

    /**
    *  Shader program center x storage.
    */
    private final int[] SHADER_PROGRAM_CENTER_XS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program center y name.
    */
    private static final String SHADER_PROGRAM_CENTER_Y_NAME = "CenterY";

    /**
    *  Shader program center y storage.
    */
    private final int[] SHADER_PROGRAM_CENTER_YS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program radius name.
    */
    private static final String SHADER_PROGRAM_RADIUS_NAME = "Radius";

    /**
    *  Shader program radius storage.
    */
    private final int[] SHADER_PROGRAM_RADII = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program pre calc alpha values name.
    */
    private static final String SHADER_PROGRAM_PRE_CALC_ALPHA_VALUES_NAME = "PreCalcAlphaValues";

    /**
    *  Shader program pre calc alpha values storage.
    */
    private final int[] SHADER_PROGRAM_PRE_CALC_ALPHA_VALUES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program with noise effect name.
    */
    private static final String SHADER_PROGRAM_WITH_NOISE_EFFECT_NAME = "WithNoiseEffect";

    /**
    *  Shader program with noise effect storage.
    */
    private final int[] SHADER_PROGRAM_WITH_NOISE_EFFECTS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program state name.
    */
    private static final String SHADER_PROGRAM_STATE_NAME = "State";

    /**
    *  Shader program state storage.
    */
    private final int[] SHADER_PROGRAM_STATES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program interpolation name.
    */
    private static final String SHADER_PROGRAM_INTERPOLATION_NAME = "Interpolation";

    /**
    *  Shader program interpolation storage.
    */
    private final int[] SHADER_PROGRAM_INTERPOLATIONS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program size name.
    */
    private static final String SHADER_PROGRAM_SIZE_NAME = "Size";

    /**
    *  Shader program size storage.
    */
    private final int[] SHADER_PROGRAM_SIZES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program px name.
    */
    private static final String SHADER_PROGRAM_PX_NAME = "Px";

    /**
    *  Shader program px storage.
    */
    private final int[] SHADER_PROGRAM_PXS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program py name.
    */
    private static final String SHADER_PROGRAM_PY_NAME = "Py";

    /**
    *  Shader program py storage.
    */
    private final int[] SHADER_PROGRAM_PYS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program power name.
    */
    private static final String SHADER_PROGRAM_POWER_NAME = "Power";

    /**
    *  Shader program power storage.
    */
    private final int[] SHADER_PROGRAM_POWERS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program mouse move name.
    */
    private static final String SHADER_PROGRAM_MOUSE_MOVE_NAME = "MouseMove";

    /**
    *  Shader program mouse move storage.
    */
    private final int[] SHADER_PROGRAM_MOUSE_MOVES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program blob width name.
    */
    private static final String SHADER_PROGRAM_BLOB_WIDTH_NAME = "BlobWidth";

    /**
    *  Shader program blob width storage.
    */
    private final int[] SHADER_PROGRAM_BLOB_WIDTHS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program blob height name.
    */
    private static final String SHADER_PROGRAM_BLOB_HEIGHT_NAME = "BlobHeight";

    /**
    *  Shader program blob height storage.
    */
    private final int[] SHADER_PROGRAM_BLOB_HEIGHTS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program blob scale size name.
    */
    private static final String SHADER_PROGRAM_BLOB_SCALE_SIZE_NAME = "BlobScaleSize";

    /**
    *  Shader program blob scale size storage.
    */
    private final int[] SHADER_PROGRAM_BLOB_SCALE_SIZES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program blob halo exponent name.
    */
    private static final String SHADER_PROGRAM_BLOB_HALO_EXPONENT_NAME = "BlobHaloExponent";

    /**
    *  Shader program blob halo exponent storage.
    */
    private final int[] SHADER_PROGRAM_BLOB_HALO_EXPONENTS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program scSize name.
    */
    private static final String SHADER_PROGRAM_SC_SIZE_NAME = "ScSize";

    /**
    *  Shader program scSize storage.
    */
    private final int[] SHADER_PROGRAM_SC_SIZES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program star distance Z name.
    */
    private static final String SHADER_PROGRAM_STAR_DISTANCE_Z_NAME = "StarDistanceZ";

    /**
    *  Shader program star distance Z storage.
    */
    private final int[] SHADER_PROGRAM_STAR_DISTANCE_ZS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program number of 3D stars name.
    */
    private static final String SHADER_PROGRAM_NUMBER_OF_3D_STARS_NAME = "NumberOf3DStars";

    /**
    *  Shader program number of 3D stars storage.
    */
    private final int[] SHADER_PROGRAM_NUMBER_OF_3D_STARS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program blob color name.
    */
    private static final String SHADER_PROGRAM_BLOB_COLOR_NAME = "BlobColor";

    /**
    *  Shader program blob color storage.
    */
    private final int[] SHADER_PROGRAM_BLOB_COLORS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program blob motion blur name.
    */
    private static final String SHADER_PROGRAM_BLOB_MOTION_BLUR_NAME = "BlobMotionBlur";

    /**
    *  Shader program blob motion blur storage.
    */
    private final int[] SHADER_PROGRAM_BLOB_MOTION_BLURS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program animationGPUComputingMode name.
    */
    private static final String SHADER_PROGRAM_ANIMATION_GPU_COMPUTING_MODE_NAME = "AnimationGPUComputingMode";

    /**
    *  Shader program animationGPUComputingMode storage.
    */
    private final int[] SHADER_PROGRAM_ANIMATION_GPU_COMPUTING_MODES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program nodeValue name.
    */
    private static final String SHADER_PROGRAM_NODE_VALUE_NAME = "NodeValue";

    /**
    *  Shader program nodeValue storage.
    */
    private final int[] SHADER_PROGRAM_NODE_VALUES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program processNextNodeValue name.
    */
    private static final String SHADER_PROGRAM_PPROCESS_NEXT_NODE_VALUE_NAME = "ProcessNextNodeValue";

    /**
    *  Shader program processNextNodeValue storage.
    */
    private final int[] SHADER_PROGRAM_PROCESS_NEXT_NODE_VALUES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program nextNodeValue name.
    */
    private static final String SHADER_PROGRAM_NEXT_NODE_VALUE_NAME = "NextNodeValue";

    /**
    *  Shader program nextNodeValue storage.
    */
    private final int[] SHADER_PROGRAM_NEXT_NODE_VALUES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program animationFrameCount name.
    */
    private static final String SHADER_PROGRAM_ANIMATION_FRAME_COUNT_NAME = "AnimationFrameCount";

    /**
    *  Shader program animationFrameCount storage.
    */
    private final int[] SHADER_PROGRAM_ANIMATION_FRAME_COUNTS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program FRAMERATE_PER_SECOND_FOR_ANIMATION name.
    */
    private static final String SHADER_PROGRAM_FRAMERATE_PER_SECOND_FOR_ANIMATION_NAME = "FRAMERATE_PER_SECOND_FOR_ANIMATION";

    /**
    *  Shader program FRAMERATE_PER_SECOND_FOR_ANIMATION storage.
    */
    private final int[] SHADER_PROGRAM_FRAMERATE_PER_SECOND_FOR_ANIMATIONS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program ANIMATION_FLUID_LINEAR_TRANSITION name.
    */
    private static final String SHADER_PROGRAM_ANIMATION_FLUID_LINEAR_TRANSITION_NAME = "ANIMATION_FLUID_LINEAR_TRANSITION";

    /**
    *  Shader program ANIMATION_FLUID_LINEAR_TRANSITION storage.
    */
    private final int[] SHADER_PROGRAM_ANIMATION_FLUID_LINEAR_TRANSITIONS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program ANIMATION_FLUID_POLYNOMIAL_TRANSITION name.
    */
    private static final String SHADER_PROGRAM_ANIMATION_FLUID_POLYNOMIAL_TRANSITION_NAME = "ANIMATION_FLUID_POLYNOMIAL_TRANSITION";

    /**
    *  Shader program ANIMATION_FLUID_POLYNOMIAL_TRANSITION storage.
    */
    private final int[] SHADER_PROGRAM_ANIMATION_FLUID_POLYNOMIAL_TRANSITIONS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program ANIMATION_TICKS_PER_SECOND name.
    */
    private static final String SHADER_PROGRAM_ANIMATION_TICKS_PER_SECOND_NAME = "ANIMATION_TICKS_PER_SECOND";

    /**
    *  Shader program ANIMATION_TICKS_PER_SECOND storage.
    */
    private final int[] SHADER_PROGRAM_ANIMATION_TICKS_PER_SECONDS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program ANIMATION_MAX_NODE_SIZE name.
    */
    private static final String SHADER_PROGRAM_ANIMATION_MAX_NODE_SIZE_NAME = "ANIMATION_MAX_NODE_SIZE";

    /**
    *  Shader program ANIMATION_MAX_NODE_SIZE storage.
    */
    private final int[] SHADER_PROGRAM_ANIMATION_MAX_NODE_SIZES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program ANIMATION_RESULTS_MAX_VALUE name.
    */
    private static final String SHADER_PROGRAM_ANIMATION_RESULTS_MAX_VALUE_NAME = "ANIMATION_RESULTS_MAX_VALUE";

    /**
    *  Shader program ANIMATION_RESULTS_MAX_VALUE storage.
    */
    private final int[] SHADER_PROGRAM_ANIMATION_RESULTS_MAX_VALUES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program ANIMATION_RESULTS_REAL_MAX_VALUE name.
    */
    private static final String SHADER_PROGRAM_ANIMATION_RESULTS_REAL_MAX_VALUE_NAME = "ANIMATION_RESULTS_REAL_MAX_VALUE";

    /**
    *  Shader program ANIMATION_RESULTS_REAL_MAX_VALUE storage.
    */
    private final int[] SHADER_PROGRAM_ANIMATION_RESULTS_REAL_MAX_VALUES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION name.
    */
    private static final String SHADER_PROGRAM_ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION_NAME = "ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION";

    /**
    *  Shader program ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION storage.
    */
    private final int[] SHADER_PROGRAM_ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITIONS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITION name.
    */
    private static final String SHADER_PROGRAM_ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITION_NAME = "ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITION";

    /**
    *  Shader program ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITION storage.
    */
    private final int[] SHADER_PROGRAM_ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITIONS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program ANIMATION_MAX_SPECTRUM_COLOR name.
    */
    private static final String SHADER_PROGRAM_ANIMATION_MAX_SPECTRUM_COLOR_NAME = "ANIMATION_MAX_SPECTRUM_COLOR";

    /**
    *  Shader program ANIMATION_MAX_SPECTRUM_COLOR storage.
    */
    private final int[] SHADER_PROGRAM_ANIMATION_MAX_SPECTRUM_COLORS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program ANIMATION_USE_IMAGE_AS_SPECTRUM name.
    */
    private static final String SHADER_PROGRAM_ANIMATION_USE_IMAGE_AS_SPECTRUM_NAME = "ANIMATION_USE_IMAGE_AS_SPECTRUM";

    /**
    *  Shader program ANIMATION_USE_IMAGE_AS_SPECTRUM storage.
    */
    private final int[] SHADER_PROGRAM_ANIMATION_USE_IMAGE_AS_SPECTRUMS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Variable to store the texture displacement timer update.
    */
    private float textureDisplacementTimerUpdate = 0.0f;

    /**
    *  Variable to store the texture displacement timer update step.
    */
    private float textureDisplacementTimeStep = 0.0f;

    /**
    *  Variable to store the plasma timer update.
    */
    private float plasmaTimerUpdate = 0.0f;

    /**
    *  Variable to store the plasma timer update step.
    */
    private float plasmaTimerUpdateStep = 0.0f;

    /**
    *  Variable to store the spot circle timer update.
    */
    private float spotCircleTimerUpdate = 0.0f;

    /**
    *  Variable to store the spot circle timer update step.
    */
    private float spotCircleTimerUpdateStep = 0.0f;

    /**
    *  Variable used for the spot circle effect.
    */
    private int spotCircleEffectUpdateFrame = 0;

    /**
    *  Variable to store the water timer update.
    */
    private float waterTimerUpdate = 0.0f;

    /**
    *  Variable to store the water timer update step.
    */
    private float waterTimeStep = 0.0f;

    /**
    *  Variable to store the water drop timer update.
    */
    private float waterDropTimerUpdate = 0.0f;

    /**
    *  Variable to store the water drop timer update step.
    */
    private float waterDropTimeStep = 0.0f;

    /**
    *  Variable to store the bump timer update.
    */
    private float bumpTimerUpdate = 0.0f;

    /**
    *  Variable to store the bump timer update step.
    */
    private float bumpTimeStep = 0.0f;

    /**
    *  Variable to store the radial blur timer update.
    */
    private float radialblurTimerUpdate = 0.0f;

    /**
    *  Variable to store the radial blur timer update step.
    */
    private float radialblurTimeStep = 0.0f;

    /**
    *  Variable to store the blob stars 3D scroller timer update.
    */
    private float blobStars3DScrollerTimerUpdate = 0.0f;

    /**
    *  Variable to store the blob stars 3D scroller timer update step.
    */
    private float blobStars3DScrollerTimerUpdateStep = 0.0f;

    /**
    *  Variable used for the spot circle effect.
    */
    private static final int SPOT_CIRCLE_FRAMES_TO_WAIT_BEFORE_UPDATE = 3;

    /**
    *  Variable used for the spot circle effect.
    */
    private float spotCircleCenterX = 0.0f;

    /**
    *  Variable used for the spot circle effect.
    */
    private float spotCircleCenterY = 0.0f;

    /**
    *  Variable used for the spot circle effect.
    */
    private float[] spotCirclePreCalcAlphaValues = null;

    /**
    *  Variable used for the spot circle effect.
    */
    private int arrayIndexPreCalcAlphaValues = 0;

    /**
    *  Variable used for the spot circle effect.
    */
    private float distStep = 0.0f;

    /**
    *  Variable used for the spot circle effect.
    */
    private boolean withNoiseEffect = false;

    /**
    *  Variable used for the spot circle effect.
    */
    private float distMAX = 0.0f;

    /**
    *  Variable used for the spot circle effect.
    */
    private float distMAXRatio = 0.0f;

    /**
    *  Variable used for the spot circle effect.
    */
    private float radius = 0.0f;

    /**
    *  Variable used for the spot circle effect.
    */
    private boolean spotCircleEffectFinished = false;

    /**
    *  Variable used for the water effect.
    */
    private static final int MAX_NUMBER_OF_WATER_DROPS = 512;

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
    private int minNumberOfWaterDrops = 0;

    /**
    *  Variable used for the water effect.
    */
    private boolean logarithmicDropAnimation = false;

    /**
    *  Variable used for the water effect.
    */
    private boolean invertStep = false;

    /**
    *  Variable used for the water effect.
    */
    private int waterDropSize = 0;

    /**
    *  Variable used for the water effect.
    */
    private boolean updateWaterBufferImageTexture = false;

    /**
    *  Variable to store the water buffer image.
    */
    private BufferedImage waterBufferImage = null;

    /**
    *  Variable to store the water buffer.
    */
    private int[] waterBuffer = null;

    /**
    *  Variable used for the water effect.
    */
    private int[] waterBuffer1 = null;

    /**
    *  Variable used for the water effect.
    */
    private int[] waterBuffer2 = null;

    /**
    *  Variable used for the water effect.
    */
    private int waterBufferWidth = 0;

    /**
    *  Variable used for the water effect.
    */
    private int waterBufferHeight = 0;

    /**
    *  Variable used for the water effect.
    */
    private int waterBufferSize = 0;

    /**
    *  Variable used for the water effect.
    */
    private float[] waterDiv1 = null;

    /**
    *  Variable used for the water effect.
    */
    private float[] waterDiv2 = null;

    /**
    *  Variable used for the water effect.
    */
    private int[] waterMul1 = null;

    /**
    *  Variable used for the water effect.
    */
    private int[] waterMul2 = null;

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
    private boolean bumpRandomEmboss = false;

    /**
    *  Variable used for the blur effect.
    */
    private ShaderTextureSFXsBlurStates shaderTextureSFXsBlurState = null;

    /**
    *  Variable used for the blur effect.
    */
    private boolean blurInterpolation = false;

    /**
    *  Variable to store the spot circle texture.
    */
    private Texture spotCircleTexture = null;

    /**
    *  Variable to store the spot circle random texture.
    */
    private Texture spotCircleRandomTexture = null;

    /**
    *  Variable to store the water texture.
    */
    private Texture waterTexture = null;

    /**
    *  Variable to store the water texture.
    */
    private Texture waterBufferTexture = null;

    /**
    *  Variable to store the bump texture.
    */
    private Texture bumpTexture = null;

    /**
    *  Variable to store the bump emboss texture.
    */
    private Texture bumpEmbossTexture = null;

    /**
    *  Variable to store the blur texture.
    */
    private Texture blurEffectTexture = null;

    /**
    *  Variable to store the blur render-to-texture width.
    */
    private int renderToBlurWidth = 0;

    /**
    *  Variable to store the blur render-to-texture width.
    */
    private int renderToBlurHeight = 0;

    /**
    *  Variable to enable the render to blur texture support.
    */
    private RenderToTexture renderToBlurTexture = null;

    /**
    *  Variable used for the blob stars 3D scroller effect.
    */
    private BlobStars3DScrollerEffectInitializer blobStars3DScrollerEffectInitializer = null;

    /**
    *  Variable used for the blob stars 3D scroller effect.
    */
    private static final int NUMBER_OF_PING_PONG_FBOS = 2;

    /**
    *  Variable to enable the render to motion blur blob stars 3D scroller effect.
    */
    private RenderToTexture[] renderToMotionBlurForBlob3DStarsTextures = null;

    /**
    *  Variable used for the blob stars 3D scroller effect.
    */
    private int currentFBOIndex = 0;

    /**
    *  Variable used for the blob stars 3D scroller effect.
    */
    private float[] blobStars3DScrollerMouseMove = new float[2];

    /**
    *  Variable used for the blob stars 3D scroller effect.
    */
    private float[] blobStars3DScrollerMouseMovePrevious = new float[2];

    /**
    *  Variable used for the blob stars 3D scroller effect.
    */
    private float blobMoveStep = 1.5f;

    /**
    *  Variable used for the blob stars 3D scroller effect.
    */
    private static final int BLOB_MOVE_RESET_VALUE = 70;

    /**
    *  Constant value needed for the blob stars 3D scroller effect.
    */
    private static final float[] BLOB_COLORS_COLOR_ARRAY = new float[4];

    /**
    *  The constructor of the ShaderTextureSFXs class.
    */
    public ShaderTextureSFXs(GL2 gl, int width, int height, boolean isGLExtFramebufferObjectSupported)
    {
        this.isGLExtFramebufferObjectSupported = isGLExtFramebufferObjectSupported;

        if (USE_SPOT_CIRCLE_RANDOM_2D_TEXTURE)
            initSpotCircle2DTextures(gl, width, height, ACTIVE_TEXTURE_UNIT_FOR_2D_TEXTURE, ACTIVE_TEXTURE_UNIT_FOR_SPOT_CIRCLE_RANDOM_2D_TEXTURE);
        loadAndCompileAllShaderPrograms(gl);
    }

    /**
    *  Initializes the spot circle 2D textures.
    */
    private void initSpotCircle2DTextures(GL2 gl, int width, int height, int firstTextureUnit, int secondTextureUnit)
    {
        spotCircleTexture = createTextureFromImage(gl, new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB), spotCircleTexture, firstTextureUnit);

        gl.glActiveTexture(GL_TEXTURE0 + secondTextureUnit);
        spotCircleRandomTexture = RandomImage.createRandomTexture(SPOT_CIRCLE_RANDOM_2D_TEXTURE_SIZE, SPOT_CIRCLE_RANDOM_2D_TEXTURE_SIZE);
        if (secondTextureUnit != 0)
            gl.glActiveTexture(GL_TEXTURE0);
    }

    /**
    *  Creates a texture from a given image at a given texture unit. Using package access.
    */
    static Texture createTextureFromImage(GL2 gl, BufferedImage image, Texture texture, int textureUnit)
    {
        gl.glActiveTexture(GL_TEXTURE0 + textureUnit);
        if (texture != null) texture = null;
        texture = TextureProducer.createTextureFromBufferedImageAndDeleteOrigContext(image);
        if (textureUnit != 0)
            gl.glActiveTexture(GL_TEXTURE0);

        return texture;
    }

    /**
    *  Loads and compiles all the shader programs.
    */
    private void loadAndCompileAllShaderPrograms(GL2 gl)
    {
        String versionString = (USE_330_SHADERS_PROCESS) ? MINIMUM_GLSL_VERSION_FOR_330_SHADERS + " " + GLSL_LANGUAGE_MODE : MINIMUM_GLSL_VERSION_FOR_120_SHADERS;
        String GLSLPreprocessorCommands = "#version " + versionString + "\n" +
                                          "#define GPU_SHADER4_COMPATIBILITY_CONDITION "          + ( USE_GL_EXT_GPU_SHADER4 ? 1 : 0 )                                                                   + "\n" +
                                          "#define ANIMATION_COMPATIBILITY_CONDITION "            + ( (GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS_INTEGER <= MINIMUM_GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS) ? 1 : 0 ) + "\n" +
                                          "#define USE_SPOT_CIRCLE_RANDOM_2D_TEXTURE_CONDITION "  + ( (USE_SPOT_CIRCLE_RANDOM_2D_TEXTURE) ? 1 : 0 )                                                      + "\n" +
                                          "#define VS_VARYING "                                   + ( (USE_330_SHADERS_PROCESS) ? "out" : "varying" )                                                    + "\n" +
                                          "#define FS_VARYING "                                   + ( (USE_330_SHADERS_PROCESS) ? "in"  : "varying" )                                                    + "\n"
                                          ;
        ShaderTypes[] allShaderTypes = ShaderTypes.values();
        String shaderEffectName = "";
        String shaderEffectFileName = "";
        for (int i = 0; i < NUMBER_OF_AVAILABLE_SHADERS; i++)
        {
            shaderEffectFileName = EnumUtils.splitAndCapitalizeFirstCharacters(allShaderTypes[i]);
            shaderEffectName = Character.toLowerCase( shaderEffectFileName.charAt(0) ) + shaderEffectFileName.substring(1);
            if ( allShaderTypes[i].equals(ShaderTypes.ANIMATION) )
            {
                ShaderUtils.loadShaderFileCompileAndLinkProgram(gl, new String[] { SHADER_FILES_DIRECTORY_1, SHADER_FILES_DIRECTORY_2, GPU_COMPUTING_DIRECTORY + SHADER_FILES_DIRECTORY_3 }, new String[]{ shaderEffectFileName, SHADER_FILE_NAME_2, SHADER_FILE_NAME_3 },
                                                                LOAD_SHADER_PROGRAMS_FROM_EXTERNAL_SOURCE, VERTEX_SHADERS, FRAGMENT_SHADERS, SHADER_PROGRAMS, i, GLSLPreprocessorCommands, DEBUG_BUILD);
            }
            else
                ShaderUtils.loadShaderFileCompileAndLinkProgram(gl, new String[] { SHADER_FILES_DIRECTORY_1, SHADER_FILES_DIRECTORY_2 }, new String[]{ shaderEffectFileName, SHADER_FILE_NAME_2 },
                                                                LOAD_SHADER_PROGRAMS_FROM_EXTERNAL_SOURCE, VERTEX_SHADERS, FRAGMENT_SHADERS, SHADER_PROGRAMS, i, GLSLPreprocessorCommands, DEBUG_BUILD);
            SHADER_PROGRAM_2D_TEXTURES[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_2D_TEXTURE_NAME);
            SHADER_PROGRAM_SPOT_CIRCLE_RANDOM_2D_TEXTURES[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_SPOT_CIRCLE_RANDOM_2D_TEXTURE_NAME);
            SHADER_PROGRAM_WATER_BUFFER_2D_TEXTURES[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_WATER_BUFFER_2D_TEXTURE_NAME);
            SHADER_PROGRAM_BUMP2D_EMBOSS_2D_TEXTURES[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_BUMP2D_EMBOSS_2D_TEXTURE_NAME);
            SHADER_PROGRAM_TIMERS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_TIMER_NAME);
            SHADER_PROGRAM_TRANSPARENCIES[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_TRANSPARENCY_NAME);
            SHADER_PROGRAM_OLD_LCD_STYLE_TRANSPARENCIES[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_OLD_LCD_STYLE_TRANSPARENCY_NAME);
            SHADER_PROGRAM_CENTER_XS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_CENTER_X_NAME);
            SHADER_PROGRAM_CENTER_YS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_CENTER_Y_NAME);
            SHADER_PROGRAM_RADII[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_RADIUS_NAME);
            SHADER_PROGRAM_PRE_CALC_ALPHA_VALUES[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_PRE_CALC_ALPHA_VALUES_NAME);
            SHADER_PROGRAM_WITH_NOISE_EFFECTS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_WITH_NOISE_EFFECT_NAME);
            SHADER_PROGRAM_STATES[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_STATE_NAME);
            SHADER_PROGRAM_INTERPOLATIONS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_INTERPOLATION_NAME);
            SHADER_PROGRAM_SIZES[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_SIZE_NAME);
            SHADER_PROGRAM_PXS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_PX_NAME);
            SHADER_PROGRAM_PYS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_PY_NAME);
            SHADER_PROGRAM_POWERS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_POWER_NAME);
            SHADER_PROGRAM_MOUSE_MOVES[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_MOUSE_MOVE_NAME);

            // blob stars 3d scroller uniform variables
            SHADER_PROGRAM_BLOB_WIDTHS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_BLOB_WIDTH_NAME);
            SHADER_PROGRAM_BLOB_HEIGHTS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_BLOB_HEIGHT_NAME);
            SHADER_PROGRAM_BLOB_SCALE_SIZES[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_BLOB_SCALE_SIZE_NAME);
            SHADER_PROGRAM_BLOB_HALO_EXPONENTS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_BLOB_HALO_EXPONENT_NAME);
            SHADER_PROGRAM_SC_SIZES[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_SC_SIZE_NAME);
            SHADER_PROGRAM_STAR_DISTANCE_ZS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_STAR_DISTANCE_Z_NAME);
            SHADER_PROGRAM_NUMBER_OF_3D_STARS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_NUMBER_OF_3D_STARS_NAME);
            SHADER_PROGRAM_BLOB_COLORS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_BLOB_COLOR_NAME);
            SHADER_PROGRAM_BLOB_MOTION_BLURS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_BLOB_MOTION_BLUR_NAME);

            // animation related GPU Computing uniform variables
            SHADER_PROGRAM_ANIMATION_2D_TEXTURES[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], SHADER_PROGRAM_ANIMATION_2D_TEXTURE_NAME);
            SHADER_PROGRAM_ANIMATION_GPU_COMPUTING_MODES[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], SHADER_PROGRAM_ANIMATION_GPU_COMPUTING_MODE_NAME);
            SHADER_PROGRAM_NODE_VALUES[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], SHADER_PROGRAM_NODE_VALUE_NAME);
            SHADER_PROGRAM_PROCESS_NEXT_NODE_VALUES[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], SHADER_PROGRAM_PPROCESS_NEXT_NODE_VALUE_NAME);
            SHADER_PROGRAM_NEXT_NODE_VALUES[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], SHADER_PROGRAM_NEXT_NODE_VALUE_NAME);
            SHADER_PROGRAM_ANIMATION_FRAME_COUNTS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], SHADER_PROGRAM_ANIMATION_FRAME_COUNT_NAME);
            SHADER_PROGRAM_FRAMERATE_PER_SECOND_FOR_ANIMATIONS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], SHADER_PROGRAM_FRAMERATE_PER_SECOND_FOR_ANIMATION_NAME);
            SHADER_PROGRAM_ANIMATION_FLUID_LINEAR_TRANSITIONS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], SHADER_PROGRAM_ANIMATION_FLUID_LINEAR_TRANSITION_NAME);
            SHADER_PROGRAM_ANIMATION_FLUID_POLYNOMIAL_TRANSITIONS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], SHADER_PROGRAM_ANIMATION_FLUID_POLYNOMIAL_TRANSITION_NAME);
            SHADER_PROGRAM_ANIMATION_TICKS_PER_SECONDS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], SHADER_PROGRAM_ANIMATION_TICKS_PER_SECOND_NAME);
            SHADER_PROGRAM_ANIMATION_MAX_NODE_SIZES[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], SHADER_PROGRAM_ANIMATION_MAX_NODE_SIZE_NAME);
            SHADER_PROGRAM_ANIMATION_RESULTS_MAX_VALUES[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], SHADER_PROGRAM_ANIMATION_RESULTS_MAX_VALUE_NAME);
            SHADER_PROGRAM_ANIMATION_RESULTS_REAL_MAX_VALUES[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], SHADER_PROGRAM_ANIMATION_RESULTS_REAL_MAX_VALUE_NAME);
            SHADER_PROGRAM_ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITIONS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], SHADER_PROGRAM_ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION_NAME);
            SHADER_PROGRAM_ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITIONS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], SHADER_PROGRAM_ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITION_NAME);
            SHADER_PROGRAM_ANIMATION_MAX_SPECTRUM_COLORS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], SHADER_PROGRAM_ANIMATION_MAX_SPECTRUM_COLOR_NAME);
            SHADER_PROGRAM_ANIMATION_USE_IMAGE_AS_SPECTRUMS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], SHADER_PROGRAM_ANIMATION_USE_IMAGE_AS_SPECTRUM_NAME);
        }
    }

    /**
    *  Uses a particular shader texture SFX program.
    */
    private void useProgramAndUniforms(GL2 gl, int effectIndex, float effectTimerUpdate, float alpha, boolean oldLCDStyleTransparency, float centerX, float centerY, float radius, float preCalcAlphaValues, boolean withNoiseEffect, int state, boolean interpolation, float size, float px, float py, float power)
    {
        gl.glUseProgram(SHADER_PROGRAMS[effectIndex]);
        gl.glUniform1i(SHADER_PROGRAM_2D_TEXTURES[effectIndex], ACTIVE_TEXTURE_UNIT_FOR_2D_TEXTURE);
        gl.glUniform1i(SHADER_PROGRAM_SPOT_CIRCLE_RANDOM_2D_TEXTURES[effectIndex], ACTIVE_TEXTURE_UNIT_FOR_SPOT_CIRCLE_RANDOM_2D_TEXTURE);
        gl.glUniform1i(SHADER_PROGRAM_WATER_BUFFER_2D_TEXTURES[effectIndex], ACTIVE_TEXTURE_UNIT_FOR_WATER_BUFFER_2D_TEXTURE);
        gl.glUniform1i(SHADER_PROGRAM_BUMP2D_EMBOSS_2D_TEXTURES[effectIndex], ACTIVE_TEXTURE_UNIT_FOR_BUMP2D_EMBOSS_2D_TEXTURE);
        gl.glUniform1f(SHADER_PROGRAM_TIMERS[effectIndex], effectTimerUpdate);
        gl.glUniform1f(SHADER_PROGRAM_TRANSPARENCIES[effectIndex], alpha);
        gl.glUniform1i(SHADER_PROGRAM_OLD_LCD_STYLE_TRANSPARENCIES[effectIndex], (oldLCDStyleTransparency) ? 1 : 0);
        gl.glUniform1f(SHADER_PROGRAM_CENTER_XS[effectIndex], centerX);
        gl.glUniform1f(SHADER_PROGRAM_CENTER_YS[effectIndex], centerY);
        gl.glUniform1f(SHADER_PROGRAM_RADII[effectIndex], radius);
        gl.glUniform1f(SHADER_PROGRAM_PRE_CALC_ALPHA_VALUES[effectIndex], preCalcAlphaValues);
        gl.glUniform1i(SHADER_PROGRAM_WITH_NOISE_EFFECTS[effectIndex], (withNoiseEffect) ? 1 : 0);
        gl.glUniform1i(SHADER_PROGRAM_STATES[effectIndex], state);
        gl.glUniform1i(SHADER_PROGRAM_INTERPOLATIONS[effectIndex], (interpolation) ? 1 : 0);
        gl.glUniform1f(SHADER_PROGRAM_SIZES[effectIndex], size);
        gl.glUniform1f(SHADER_PROGRAM_PXS[effectIndex], px);
        gl.glUniform1f(SHADER_PROGRAM_PYS[effectIndex], py);
        gl.glUniform1f(SHADER_PROGRAM_POWERS[effectIndex], power);
        gl.glUniform2fv(SHADER_PROGRAM_MOUSE_MOVES[effectIndex], 1, blobStars3DScrollerMouseMove, 0);

        if (blobStars3DScrollerEffectInitializer != null)
        {
            gl.glUniform1f(SHADER_PROGRAM_BLOB_WIDTHS[effectIndex], blobStars3DScrollerEffectInitializer.blobWidth);
            gl.glUniform1f(SHADER_PROGRAM_BLOB_HEIGHTS[effectIndex], blobStars3DScrollerEffectInitializer.blobHeight);
            gl.glUniform1f(SHADER_PROGRAM_BLOB_SCALE_SIZES[effectIndex], blobStars3DScrollerEffectInitializer.blobScaleSize);
            gl.glUniform1f(SHADER_PROGRAM_BLOB_HALO_EXPONENTS[effectIndex], blobStars3DScrollerEffectInitializer.haloExponent);
            gl.glUniform1f(SHADER_PROGRAM_SC_SIZES[effectIndex], blobStars3DScrollerEffectInitializer.scSize);
            gl.glUniform1f(SHADER_PROGRAM_STAR_DISTANCE_ZS[effectIndex], blobStars3DScrollerEffectInitializer.starDistanceZ);
            gl.glUniform1f(SHADER_PROGRAM_NUMBER_OF_3D_STARS[effectIndex], blobStars3DScrollerEffectInitializer.numberOf3DStars);
            blobStars3DScrollerEffectInitializer.blobColor.getRGBComponents(BLOB_COLORS_COLOR_ARRAY);
            gl.glUniform3fv(SHADER_PROGRAM_BLOB_COLORS[effectIndex], 1, BLOB_COLORS_COLOR_ARRAY, 0);
            gl.glUniform1f(SHADER_PROGRAM_BLOB_MOTION_BLURS[effectIndex], blobStars3DScrollerEffectInitializer.blobMotionBlur);
        }

        // animation is off
        gl.glUniform1i(SHADER_PROGRAM_ANIMATION_GPU_COMPUTING_MODES[effectIndex], 0);
    }

    /**
    *  Uses a particular shader texture SFX program.
    *  Overloaded version for also passing all the animation uniform values.
    */
    private void useProgramAndUniforms(GL2 gl, int effectIndex, float effectTimerUpdate, float alpha, boolean oldLCDStyleTransparency, float centerX, float centerY, float radius, float preCalcAlphaValues, boolean withNoiseEffect, int state, boolean interpolation, float size,
                                              float px, float py, float power, float nodeValue, boolean processNextNodeValue, float nextNodeValue, int animationFrameCount)
    {
        useProgramAndUniforms(gl, effectIndex, effectTimerUpdate, alpha, oldLCDStyleTransparency, centerX, centerY, radius, preCalcAlphaValues, withNoiseEffect, state, interpolation, size, px, py, power);

        // animation is on, pass all needed uniform values
        gl.glUniform1i(SHADER_PROGRAM_ANIMATION_GPU_COMPUTING_MODES[effectIndex], 1);
        gl.glUniform1f(SHADER_PROGRAM_NODE_VALUES[effectIndex], nodeValue);
        gl.glUniform1i(SHADER_PROGRAM_PROCESS_NEXT_NODE_VALUES[effectIndex], (processNextNodeValue) ? 1 : 0);
        gl.glUniform1f(SHADER_PROGRAM_NEXT_NODE_VALUES[effectIndex], nextNodeValue);
        gl.glUniform1i(SHADER_PROGRAM_ANIMATION_FRAME_COUNTS[effectIndex], animationFrameCount);
        gl.glUniform1i(SHADER_PROGRAM_FRAMERATE_PER_SECOND_FOR_ANIMATIONS[effectIndex], FRAMERATE_PER_SECOND_FOR_ANIMATION);
        gl.glUniform1i(SHADER_PROGRAM_ANIMATION_FLUID_LINEAR_TRANSITIONS[effectIndex], (ANIMATION_FLUID_LINEAR_TRANSITION) ? 1 : 0);
        gl.glUniform1i(SHADER_PROGRAM_ANIMATION_FLUID_POLYNOMIAL_TRANSITIONS[effectIndex], (ANIMATION_FLUID_POLYNOMIAL_TRANSITION) ? 1 : 0);
        gl.glUniform1f(SHADER_PROGRAM_ANIMATION_TICKS_PER_SECONDS[effectIndex], ANIMATION_TICKS_PER_SECOND);
        gl.glUniform1i(SHADER_PROGRAM_ANIMATION_MAX_NODE_SIZES[effectIndex], ANIMATION_MAX_NODE_SIZE);
        gl.glUniform1f(SHADER_PROGRAM_ANIMATION_RESULTS_MAX_VALUES[effectIndex], ANIMATION_RESULTS_MAX_VALUE);
        gl.glUniform1i(SHADER_PROGRAM_ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITIONS[effectIndex], (ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION) ? 1 : 0);
        gl.glUniform1i(SHADER_PROGRAM_ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITIONS[effectIndex], (ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITION) ? 1 : 0);
        gl.glUniform1i(SHADER_PROGRAM_ANIMATION_USE_IMAGE_AS_SPECTRUMS[effectIndex], (ANIMATION_USE_IMAGE_AS_SPECTRUM) ? 1 : 0);
        if (ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION)
        {
            if (ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITION)
                gl.glUniform1f(SHADER_PROGRAM_ANIMATION_RESULTS_REAL_MAX_VALUES[effectIndex], ANIMATION_RESULTS_REAL_MAX_VALUE);

            if (!ANIMATION_USE_IMAGE_AS_SPECTRUM)
            {
                ANIMATION_MAX_SPECTRUM_COLOR.getRGBComponents(ANIMATION_MAX_SPECTRUM_COLOR_ARRAY);
                gl.glUniform3fv(SHADER_PROGRAM_ANIMATION_MAX_SPECTRUM_COLORS[effectIndex], 1, ANIMATION_MAX_SPECTRUM_COLOR_ARRAY, 0);
            }
            else
            {
                gl.glUniform1i(SHADER_PROGRAM_ANIMATION_2D_TEXTURES[effectIndex], ACTIVE_TEXTURE_UNIT_FOR_ANIMATION_SPECTRUM_2D_TEXTURE);
            }
        }
    }

    /**
    *  Initializes the texture displacement effect.
    */
    public void textureDisplacementEffectInit(float textureDisplacementTimeStep)
    {
        this.textureDisplacementTimeStep = textureDisplacementTimeStep;
        textureDisplacementTimerUpdate = 0.0f;
    }

    /**
    *  Processes the texture displacement effect.
    */
    public void textureDisplacementEffect()
    {
        textureDisplacementTimerUpdate += textureDisplacementTimeStep;
    }

    /**
    *  Renders the texture displacement effect.
    */
    public void textureDisplacementEffectRender(GL2 gl, Texture textureDisplacementEffectTexture, double x1, double y1, int x2, int y2, boolean oldLCDStyleTransparency)
    {
        textureDisplacementEffectRender(gl, textureDisplacementEffectTexture, x1, y1, x2, y2, 1.0f, oldLCDStyleTransparency);
    }

    /**
    *  Renders the texture displacement effect.
    *  Overloaded version to include an alpha value.
    */
    public void textureDisplacementEffectRender(GL2 gl, Texture textureDisplacementEffectTexture, double x1, double y1, int x2, int y2, float alpha, boolean oldLCDStyleTransparency)
    {
        useProgramAndUniforms(gl, ShaderTypes.TEXTURE_DISPLACEMENT.ordinal(), textureDisplacementTimerUpdate, alpha, oldLCDStyleTransparency, 0.0f, 0.0f, 0.0f, 0.0f, false, 0, false, 0.0f, 0.0f, 0.0f, 0.0f);
        drawTexture(gl, textureDisplacementEffectTexture, x1, y1, x2, y2);
        gl.glUseProgram(0);
    }

    /**
    *  Renders the texture displacement effect with an added rotozoom effect.
    */
    public void textureDisplacementEffectRotoZoomRender(GL2 gl, Texture textureDisplacementEffectTexture, double x1, double y1, double theta, double zoomFactorX, double zoomFactorY, boolean oldLCDStyleTransparency)
    {
        textureDisplacementEffectRotoZoomRender(gl, textureDisplacementEffectTexture, x1, y1, theta, zoomFactorX, zoomFactorY, 1.0f, oldLCDStyleTransparency);
    }

    /**
    *  Renders the texture displacement effect with an added rotozoom effect.
    *  Overloaded version to include an alpha value.
    */
    public void textureDisplacementEffectRotoZoomRender(GL2 gl, Texture textureDisplacementEffectTexture, double x1, double y1, double theta, double zoomFactorX, double zoomFactorY, float alpha, boolean oldLCDStyleTransparency)
    {
        useProgramAndUniforms(gl, ShaderTypes.TEXTURE_DISPLACEMENT.ordinal(), textureDisplacementTimerUpdate, alpha, oldLCDStyleTransparency, 0.0f, 0.0f, 0.0f, 0.0f, false, 0, false, 0.0f, 0.0f, 0.0f, 0.0f);
        drawRotoZoomTexture(gl, textureDisplacementEffectTexture, x1, y1, theta, zoomFactorX, zoomFactorY);
        gl.glUseProgram(0);
    }

    /**
    *  Initializes the plasma effect.
    */
    public void plasmaEffectInit(float plasmaTimerUpdateStep)
    {
        this.plasmaTimerUpdateStep = plasmaTimerUpdateStep;
        plasmaTimerUpdate = 0.0f;
    }

    /**
    *  Processes the plasma effect.
    */
    public void plasmaEffect()
    {
        plasmaTimerUpdate += plasmaTimerUpdateStep;
    }

    /**
    *  Renders the plasma effect.
    */
    public void plasmaEffectRender(GL2 gl, double x1, double y1, int x2, int y2, boolean oldLCDStyleTransparency)
    {
        plasmaEffectRender(gl, x1, y1, x2 ,y2, 1.0f, oldLCDStyleTransparency);
    }

    /**
    *  Renders the plasma effect.
    *  Overloaded version to include an alpha value.
    */
    public void plasmaEffectRender(GL2 gl, double x1, double y1, int x2, int y2, float alpha, boolean oldLCDStyleTransparency)
    {
        useProgramAndUniforms(gl, ShaderTypes.PLASMA.ordinal(), plasmaTimerUpdate, alpha, oldLCDStyleTransparency, 0.0f, 0.0f, 0.0f, 0.0f, false, 0, false, 0.0f, 0.0f, 0.0f, 0.0f);
        drawQuad(gl, x1, y1, x2, y2);
        gl.glUseProgram(0);
    }

    /**
    *  Renders the plasma effect with an added rotozoom effect.
    */
    public void plasmaEffectRotoZoomRender(GL2 gl, double x1, double y1, int x2, int y2, double theta, double zoomFactorX, double zoomFactorY, boolean oldLCDStyleTransparency)
    {
        plasmaEffectRotoZoomRender(gl, x1, y1, x2 ,y2, theta, zoomFactorX, zoomFactorY, 1.0f, oldLCDStyleTransparency);
    }

    /**
    *  Renders the plasma effect with an added rotozoom effect.
    *  Overloaded version to include an alpha value.
    */
    public void plasmaEffectRotoZoomRender(GL2 gl, double x1, double y1, int x2, int y2, double theta, double zoomFactorX, double zoomFactorY, float alpha, boolean oldLCDStyleTransparency)
    {
        useProgramAndUniforms(gl, ShaderTypes.PLASMA.ordinal(), plasmaTimerUpdate, alpha, oldLCDStyleTransparency, 0.0f, 0.0f, 0.0f, 0.0f, false, 0, false, 0.0f, 0.0f, 0.0f, 0.0f);
        drawRotoZoomQuad(gl, x1, y1, x2, y2, theta, zoomFactorX, zoomFactorY);
        gl.glUseProgram(0);
    }

    /**
    *  Initializes the spot circle effect.
    */
    public void spotCircleEffectInit(float spotCircleTimerUpdateStep, float spotCircleWidth, float spotCircleHeight, float spotCircleCenterX, float spotCircleCenterY, float distStep, boolean withNoiseEffect, int distRatio)
    {
        this.spotCircleTimerUpdateStep = spotCircleTimerUpdateStep;
        this.spotCircleCenterX = spotCircleCenterX;
        this.spotCircleCenterY = spotCircleCenterY;
        this.distStep = distStep;
        this.withNoiseEffect = withNoiseEffect;

        this.spotCircleEffectUpdateFrame = 0;
        this.arrayIndexPreCalcAlphaValues = 0;
        this.spotCircleEffectFinished = false;

        float distMaxCornerTopLeft = (float)sqrt( pow( (spotCircleCenterX - 0.0f), 2.0) + pow( (spotCircleCenterY - 0.0f), 2.0) );
        float distMaxCornerTopRight = (float)sqrt( pow( (spotCircleCenterX - spotCircleWidth), 2.0) + pow( (spotCircleCenterY - 0.0f), 2.0) );
        float distMaxCornerBottomLeft = (float)sqrt( pow( (spotCircleCenterX - 0.0f), 2.0) + pow( (spotCircleCenterY - spotCircleHeight), 2.0) );
        float distMaxCornerBottomRight = (float)sqrt( pow( (spotCircleCenterX - spotCircleWidth), 2.0) + pow( (spotCircleCenterY - spotCircleHeight), 2.0) );

        distMAX = (float)org.BioLayoutExpress3D.StaticLibraries.Math.findMinMaxFromNumbers(distMaxCornerTopLeft, distMaxCornerTopRight, distMaxCornerBottomLeft, distMaxCornerBottomRight, false); // false for MAX distance!
        radius = (distStep > 0.0f) ? distMAX : 0.0f;

        if (distRatio < 0)
        {
            if (DEBUG_BUILD) println("distRatio must be a positive number, now converting it to positive");
            distRatio = abs(distRatio);
        }
        else if (distRatio == 0f)
        {
            if (DEBUG_BUILD) println("distRatio cannot be 0.0f, now changing it to 1.0f");
            distRatio = 1;
        }

        if (withNoiseEffect)
        {
            distMAXRatio = distMAX / distRatio;
            spotCirclePreCalcAlphaValues = new float[ (int)( distMAX / ( distRatio * abs(distStep) ) ) ];
            int i = spotCirclePreCalcAlphaValues.length;
            while (--i >= 0)
                spotCirclePreCalcAlphaValues[i] = (float)i / (distMAX / (distRatio * distStep) ) + ( (distStep < 0.0f) ? 1.0f : 0.0f );
        }
    }

    /**
    *  Processes the spot circle effect.
    */
    public void spotCircleEffect()
    {
        if (++spotCircleEffectUpdateFrame > SPOT_CIRCLE_FRAMES_TO_WAIT_BEFORE_UPDATE)
        {
            if ( (radius > (-2.0f * distStep) && distStep > 0.0f) || (radius < (distMAX - distStep) && distStep < 0.0f) )
            {
                spotCircleTimerUpdate += spotCircleTimerUpdateStep;
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
    }

    /**
    *  Renders the spot circle effect.
    */
    public void spotCircleEffectRender(GL2 gl, double x1, double y1, int x2, int y2, boolean oldLCDStyleTransparency)
    {
        spotCircleEffectRender(gl, x1, y1, x2 ,y2, 1.0f, oldLCDStyleTransparency);
    }

    /**
    *  Renders the spot circle effect.
    *  Overloaded version to include an alpha value.
    */
    public void spotCircleEffectRender(GL2 gl, double x1, double y1, int x2, int y2, float alpha, boolean oldLCDStyleTransparency)
    {
        useProgramAndUniforms(gl, ShaderTypes.SPOT_CIRCLE.ordinal(), spotCircleTimerUpdate, alpha, oldLCDStyleTransparency, spotCircleCenterX, spotCircleCenterY, radius, (spotCirclePreCalcAlphaValues != null) ? spotCirclePreCalcAlphaValues[arrayIndexPreCalcAlphaValues] : 1.0f, withNoiseEffect, 0, false, 0.0f, 0.0f, 0.0f, 0.0f);
        if (USE_SPOT_CIRCLE_RANDOM_2D_TEXTURE)
            drawTexture(gl, spotCircleTexture, x1, y1, x2, y2);
        else
            drawQuadWithTextureCoords(gl, x1, y1, x2, y2);
        gl.glUseProgram(0);
    }

    /**
    *  Renders the spot circle effect with an added rotozoom effect.
    */
    public void spotCircleEffectRotoZoomRender(GL2 gl, double x1, double y1, int x2, int y2, double theta, double zoomFactorX, double zoomFactorY, boolean oldLCDStyleTransparency)
    {
        spotCircleEffectRotoZoomRender(gl, x1, y1, x2, y2, theta, zoomFactorX, zoomFactorY, 1.0f, oldLCDStyleTransparency);
    }

    /**
    *  Renders the spot circle effect with an added rotozoom effect.
    *  Overloaded version to include an alpha value.
    */
    public void spotCircleEffectRotoZoomRender(GL2 gl, double x1, double y1, int x2, int y2, double theta, double zoomFactorX, double zoomFactorY, float alpha, boolean oldLCDStyleTransparency)
    {
        useProgramAndUniforms(gl, ShaderTypes.SPOT_CIRCLE.ordinal(), spotCircleTimerUpdate, alpha, oldLCDStyleTransparency, spotCircleCenterX, spotCircleCenterY, radius, (spotCirclePreCalcAlphaValues != null) ? spotCirclePreCalcAlphaValues[arrayIndexPreCalcAlphaValues] : 1.0f, withNoiseEffect, 0, false, 0.0f, 0.0f, 0.0f, 0.0f);
        if (USE_SPOT_CIRCLE_RANDOM_2D_TEXTURE)
            drawRotoZoomTexture(gl, spotCircleTexture, x1, y1, theta, zoomFactorX, zoomFactorY);
        else
            drawRotoZoomQuadWithTextureCoords(gl, x1, y1, x2, y2, theta, zoomFactorX, zoomFactorY);
        gl.glUseProgram(0);
    }

    /**
    *  Returns the spot circle effect status.
    */
    public boolean hasSpotCircleEffectFinished()
    {
        return spotCircleEffectFinished;
    }

    /**
    *  Initializes the water effect.
    */
    public void waterEffectInit(Texture waterTexture, int minNumberOfWaterDrops, boolean logarithmicDropAnimation, int waterDropSize, float waterTimeStep)
    {
        this.waterTexture = waterTexture;
        this.minNumberOfWaterDrops = minNumberOfWaterDrops;
        this.logarithmicDropAnimation = logarithmicDropAnimation;
        this.waterDropSize = (waterDropSize < 1) ? 1 : ( (waterDropSize >= MAX_NUMBER_OF_WATER_DROPS) ? MAX_NUMBER_OF_WATER_DROPS : waterDropSize ) ;
        this.waterTimeStep = this.waterDropTimeStep = waterTimeStep;
        waterTimerUpdate = 0.0f;

        waterWidth  = waterTexture.getImageWidth();
        waterHeight = waterTexture.getImageHeight();
        waterBufferWidth  = waterWidth >> 1;
        waterBufferHeight = waterHeight >> 1;
        waterBufferSize = waterBufferWidth * waterBufferHeight;

	waterBuffer1 = new int[waterBufferSize];
	waterBuffer2 = new int[waterBufferSize];
        waterDiv1 = new float[MAX_NUMBER_OF_WATER_DROPS];
        waterDiv2 = new float[MAX_NUMBER_OF_WATER_DROPS];
        waterMul1 = new int[MAX_NUMBER_OF_WATER_DROPS];
        waterMul2 = new int[MAX_NUMBER_OF_WATER_DROPS];

        int i = MAX_NUMBER_OF_WATER_DROPS;
	while (--i >= 0)
	{
            waterDiv1[i] = (float)( 512.0 + org.BioLayoutExpress3D.StaticLibraries.Random.getRandomRange(0, 1023) );
            waterDiv2[i] = (float)( 512.0 + org.BioLayoutExpress3D.StaticLibraries.Random.getRandomRange(0, 1023) );
            waterMul1[i] = waterBufferWidth / 4 + org.BioLayoutExpress3D.StaticLibraries.Random.getRandomRange(0, waterBufferWidth / 4);
            waterMul2[i] = waterBufferHeight / 4 + org.BioLayoutExpress3D.StaticLibraries.Random.getRandomRange(0, waterBufferHeight / 4);
	}

        waterBufferImage = new BufferedImage(waterBufferWidth, waterBufferHeight, BufferedImage.TYPE_INT_ARGB);
        waterBuffer = ( (DataBufferInt)waterBufferImage.getRaster().getDataBuffer() ).getData(); // connect it to the returning image buffer
    }

    /**
    *  Calculates one water drop for the water effect.
    */
    private static void calculateDrop(int px, int py, int waterDropSize, int waterBufferWidth, int waterBufferHeight, int[] waterBuffer)
    {
        int xpp = 0, ypp = 0;
        int loop = (waterDropSize + 1) >> 1;
        for (int y = -loop; y < loop; y++)
        {
            for (int x = -loop; x < loop; x++)
            {
                xpp = x + px;
                ypp = y + py;

                if (xpp < 0) xpp = 0;
                if (ypp < 0) ypp = 0;
                if (xpp > waterBufferWidth  - 1) xpp = waterBufferWidth  - 1;
                if (ypp > waterBufferHeight - 1) ypp = waterBufferHeight - 1;

                waterBuffer[xpp + ypp * waterBufferWidth] = 255;
            }
        }
    }

    /**
    *  Calculates the water effect.
    */
    private static void calculateWater(int waterBufferWidth, int waterBufferSize, int[] waterBuffer, int[] waterBuffer1, int[] waterBuffer2)
    {
	for (int i = 1 + waterBufferWidth; i < waterBufferSize - waterBufferWidth - 1; i++)
	{
            waterBuffer2[i] = ( (waterBuffer1[i - 1] + waterBuffer1[i + 1] + waterBuffer1[i - waterBufferWidth] + waterBuffer1[i + waterBufferWidth]) >> 1 ) - waterBuffer2[i];
            waterBuffer2[i] -= (waterBuffer2[i] >> 5);
            if (waterBuffer2[i] < 0) waterBuffer2[i] = 0;
            if (waterBuffer2[i] > 255) waterBuffer2[i] = 255;
	}

        int i = waterBufferSize;
	while (--i >= 0)
            waterBuffer[i] = (waterBuffer1[i] << 24);
    }

    /**
    *  Calculates all the water drops for the water effects.
    */
    static boolean calculateAllWaterDrops(boolean logarithmicDropAnimation, int minNumberOfWaterDrops, int MAX_NUMBER_OF_WATER_DROPS, float waterTimerUpdate, float waterDropTimerUpdate, boolean invertStep,
            int waterBufferWidth, int waterBufferHeight, int waterBufferSize, int waterDropSize, float[] waterDiv1, float[] waterDiv2, int[] waterMul1, int[] waterMul2,
            int[] waterBuffer, int[] waterBuffer1, int[] waterBuffer2)
    {
        boolean hasReachedRange = false;

        int i = 0, px = 0, py = 0;
        if (!logarithmicDropAnimation)
        {
            i = (int)(minNumberOfWaterDrops + ( (MAX_NUMBER_OF_WATER_DROPS - minNumberOfWaterDrops) >> 1 ) * (sin( (waterDropTimerUpdate / 1000.0f) - PI / 2 ) + 1));
        }
        else
        {
            double result = sin( log10( (waterDropTimerUpdate / 100.0f) - PI / 2 ) );
            // check is to avoid an NaN problem, see definition of Float.isNaN()
            i = !(result != result) ? (int)( MAX_NUMBER_OF_WATER_DROPS - ( (MAX_NUMBER_OF_WATER_DROPS - minNumberOfWaterDrops) >> 1 ) * (result + 1) ) : minNumberOfWaterDrops + 1;
            if ( ( (i == minNumberOfWaterDrops) && !invertStep ) || ( (i == MAX_NUMBER_OF_WATER_DROPS - 1) && invertStep ) )
            {
                hasReachedRange = true;
                i = org.BioLayoutExpress3D.StaticLibraries.Random.getRandomRange(minNumberOfWaterDrops, MAX_NUMBER_OF_WATER_DROPS / 4);
            }
        }

	while (--i >= 0)
        {
            px = (waterBufferWidth >> 1) + (int)(sin(waterTimerUpdate / waterDiv1[i]) * waterMul1[i]);
            py = (waterBufferHeight >> 1) + (int)(sin(waterTimerUpdate / waterDiv2[i]) * waterMul2[i]);
            calculateDrop(px, py, waterDropSize, waterBufferWidth, waterBufferHeight, waterBuffer1);
        }

        if ( org.BioLayoutExpress3D.StaticLibraries.Random.nextBoolean() )
        {
            i = org.BioLayoutExpress3D.StaticLibraries.Random.getRandomRange(0, 8);
            while (--i >= 0)
            {
                px = waterBufferWidth / 16 + org.BioLayoutExpress3D.StaticLibraries.Random.getRandomRange( 0, (int)(waterBufferWidth / 1.125) );
                py = waterBufferHeight / 16 + org.BioLayoutExpress3D.StaticLibraries.Random.getRandomRange( 0, (int)(waterBufferHeight / 1.125) );
                calculateDrop(px, py, waterDropSize, waterBufferWidth, waterBufferHeight, waterBuffer1);
            }
        }

        calculateWater(waterBufferWidth, waterBufferSize, waterBuffer, waterBuffer1, waterBuffer2);

        return hasReachedRange;
    }

    /**
    *  Processes the water effect.
    */
    public void waterEffect()
    {
        waterTimerUpdate += waterTimeStep;

        if ( calculateAllWaterDrops(logarithmicDropAnimation, minNumberOfWaterDrops, MAX_NUMBER_OF_WATER_DROPS, waterTimerUpdate, waterDropTimerUpdate, invertStep,
                                    waterBufferWidth, waterBufferHeight, waterBufferSize, waterDropSize, waterDiv1, waterDiv2, waterMul1, waterMul2,
                                    waterBuffer, waterBuffer1, waterBuffer2) )
        {
            waterDropTimeStep = -waterDropTimeStep;
            invertStep = !invertStep;
        }

        // swapArrays effect, in C side emulated with the double pointer swapArrays() function
        int[] temp = waterBuffer1;
        waterBuffer1 = waterBuffer2;
        waterBuffer2 = temp;

        waterDropTimerUpdate += waterDropTimeStep;
        updateWaterBufferImageTexture = true;
    }

    /**
    *  Renders the water effect.
    */
    public void waterEffectRender(GL2 gl, double x1, double y1, int x2, int y2, boolean oldLCDStyleTransparency, float specularWaterSize)
    {
        waterEffectRender(gl, x1, y1, x2, y2, 1.0f, oldLCDStyleTransparency, specularWaterSize);
    }

    /**
    *  Renders the water effect.
    *  Overloaded version to include an alpha value.
    */
    public void waterEffectRender(GL2 gl, double x1, double y1, int x2, int y2, float alpha, boolean oldLCDStyleTransparency, float specularWaterSize)
    {
        if (updateWaterBufferImageTexture)
        {
            updateWaterBufferImageTexture = false;
            waterBufferTexture = createTextureFromImage(gl, waterBufferImage, waterBufferTexture, ACTIVE_TEXTURE_UNIT_FOR_WATER_BUFFER_2D_TEXTURE);
        }

        useProgramAndUniforms(gl, ShaderTypes.WATER.ordinal(), waterTimerUpdate / 1000.0f, alpha, oldLCDStyleTransparency, 0.0f, 0.0f, 0.0f, 0.0f, false, 0, false, specularWaterSize, (float)waterWidth, (float)waterHeight, 0.0f);
        drawTexture(gl, waterTexture, x1, y1, x2, y2);
        gl.glUseProgram(0);
    }

    /**
    *  Renders the water effect with an added rotozoom effect.
    */
    public void waterEffectRotoZoomRender(GL2 gl, double x1, double y1, double theta, double zoomFactorX, double zoomFactorY, boolean oldLCDStyleTransparency, float specularWaterSize)
    {
        waterEffectRotoZoomRender(gl, x1, y1, theta, zoomFactorX, zoomFactorY, 1.0f, oldLCDStyleTransparency, specularWaterSize);
    }

    /**
    *  Renders the water effect with an added rotozoom effect.
    *  Overloaded version to include an alpha value.
    */
    public void waterEffectRotoZoomRender(GL2 gl, double x1, double y1, double theta, double zoomFactorX, double zoomFactorY, float alpha, boolean oldLCDStyleTransparency, float specularWaterSize)
    {
        if (updateWaterBufferImageTexture)
        {
            updateWaterBufferImageTexture = false;
            waterBufferTexture = createTextureFromImage(gl, waterBufferImage, waterBufferTexture, ACTIVE_TEXTURE_UNIT_FOR_WATER_BUFFER_2D_TEXTURE);
        }

        useProgramAndUniforms(gl, ShaderTypes.WATER.ordinal(), waterTimerUpdate / 1000.0f, alpha, oldLCDStyleTransparency, 0.0f, 0.0f, 0.0f, 0.0f, false, 0, false, specularWaterSize, (float)waterWidth, (float)waterHeight, 0.0f);
        drawRotoZoomTexture(gl, waterTexture, x1, y1, theta, zoomFactorX, zoomFactorY);
        gl.glUseProgram(0);
    }

    /**
    *  Initializes the bump effect.
    */
    public void bumpEffectInit(GL2 gl, BufferedImage bumpImage, Texture bumpTexture, boolean bumpRandomEmboss, float bumpTimeStep)
    {
        this.bumpTexture = bumpTexture;
        this.bumpRandomEmboss = bumpRandomEmboss;
        this.bumpTimeStep = bumpTimeStep;
        bumpTimerUpdate = 0.0f;

        bumpWidth = bumpTexture.getImageWidth();
        bumpHeight = bumpTexture.getImageHeight();

        int bumpBufferSize = bumpWidth * bumpHeight;

        if (bumpRandomEmboss)
        {
            bumpEmbossTexture = createRandomEmbossTexture(gl, bumpEmbossTexture, ACTIVE_TEXTURE_UNIT_FOR_BUMP2D_EMBOSS_2D_TEXTURE, bumpWidth, bumpHeight, bumpBufferSize, true);
        }
        else
        {
            // make sure the glyph image is in ARGB format by cloning it in memory
            bumpImage = org.BioLayoutExpress3D.StaticLibraries.ImageProducer.cloneImage(bumpImage);
            int[] bumpEmbossBitmapBuffer = ( (DataBufferInt)bumpImage.getRaster().getDataBuffer() ).getData(); // connect it to the returning image buffer

            int[] randomBuffer = new int[bumpBufferSize];
            int i = bumpBufferSize, j = 0;
            while (--i >= 0)
                randomBuffer[i] = org.BioLayoutExpress3D.StaticLibraries.Random.getRandomRange(0, 127);

            j = 3;
            while (--j >= 0)
              for (i = bumpWidth + 1; i < bumpBufferSize - bumpWidth - 1; i++)
                  randomBuffer[i] = (randomBuffer[i - 1] + randomBuffer[i + 1] + randomBuffer[i - bumpWidth] + randomBuffer[i + bumpWidth]) >> 2;

            int[] R_bumpBitmapBuffer = new int[bumpBufferSize];
            int[] G_bumpBitmapBuffer = new int[bumpBufferSize];
            int[] B_bumpBitmapBuffer = new int[bumpBufferSize];

            i = bumpBufferSize;
            while (--i >= 0)
            {
                R_bumpBitmapBuffer[i] = ((bumpEmbossBitmapBuffer[i] & 0x00FF0000) >> 16);
                G_bumpBitmapBuffer[i] = ((bumpEmbossBitmapBuffer[i] & 0x0000FF00) >> 8);
                B_bumpBitmapBuffer[i] = (bumpEmbossBitmapBuffer[i] & 0x000000FF);
            }

            int r = 0, g = 0, b = 0;
            int heightValue = 0;
            i = bumpWidth + 1;
            for (int y = 1; y < bumpHeight - 1; y++)
            {
                for (int x = 1; x < bumpWidth - 1; x++)
                {
                    r = (-R_bumpBitmapBuffer[i + 1] - R_bumpBitmapBuffer[i + bumpWidth] - R_bumpBitmapBuffer[i + bumpWidth + 1] + R_bumpBitmapBuffer[i - 1] + R_bumpBitmapBuffer[i - bumpWidth] + R_bumpBitmapBuffer[i - bumpWidth - 1]) / 9 + 128;
                    g = (-G_bumpBitmapBuffer[i + 1] - G_bumpBitmapBuffer[i + bumpWidth] - G_bumpBitmapBuffer[i + bumpWidth + 1] + G_bumpBitmapBuffer[i - 1] + G_bumpBitmapBuffer[i - bumpWidth] + G_bumpBitmapBuffer[i - bumpWidth - 1]) / 9 + 128;
                    b = (-B_bumpBitmapBuffer[i + 1] - B_bumpBitmapBuffer[i + bumpWidth] - B_bumpBitmapBuffer[i + bumpWidth + 1] + B_bumpBitmapBuffer[i - 1] + B_bumpBitmapBuffer[i - bumpWidth] + B_bumpBitmapBuffer[i - bumpWidth - 1]) / 9 + 128;
                    heightValue = ( (r + g + b) >> 2 ) + randomBuffer[i];
                    if (heightValue > 255) heightValue = 255;
                    bumpEmbossBitmapBuffer[i] = (heightValue << 24) | (r << 16) | (g << 8) | b;
                    i++;
                }
            }

            bumpEmbossTexture = createTextureFromImage(GLU.getCurrentGL().getGL2(), bumpImage, bumpEmbossTexture, ACTIVE_TEXTURE_UNIT_FOR_BUMP2D_EMBOSS_2D_TEXTURE);
        }
    }

    /**
    *  Creates a random emboss texture. Using package access.
    */
    static Texture createRandomEmbossTexture(GL2 gl, Texture bumpEmbossTexture, int textureUnit, int bumpWidth, int bumpHeight, int bumpBufferSize, boolean useRandomRGBColors) // package acess
    {
        BufferedImage bumpEmbossBitmap = new BufferedImage(bumpWidth, bumpHeight, BufferedImage.TYPE_INT_ARGB);
        int[] bumpEmbossBitmapBuffer = ( (DataBufferInt)bumpEmbossBitmap.getRaster().getDataBuffer() ).getData(); // connect it to the returning image buffer

        int i = bumpBufferSize, j = 0;
        while (--i >= 0)
            bumpEmbossBitmapBuffer[i] = org.BioLayoutExpress3D.StaticLibraries.Random.getRandomRange(0, 255);

        j = 3;
        while (--j >= 0)
        {
            i = bumpBufferSize;
            while (--i >= 0)
                bumpEmbossBitmapBuffer[i] = (bumpEmbossBitmapBuffer[abs( (i - 1) % bumpBufferSize )] + bumpEmbossBitmapBuffer[abs( (i + 1) % bumpBufferSize )] + bumpEmbossBitmapBuffer[abs( (i - bumpWidth) % bumpBufferSize )] + bumpEmbossBitmapBuffer[abs( (i + bumpWidth) % bumpBufferSize )]) >> 2;
        }

        int rs = (useRandomRGBColors) ? org.BioLayoutExpress3D.StaticLibraries.Random.getRandomRange(0, 7) : 0;
        int gs = (useRandomRGBColors) ? org.BioLayoutExpress3D.StaticLibraries.Random.getRandomRange(0, 7) : 0;
        int bs = (useRandomRGBColors) ? org.BioLayoutExpress3D.StaticLibraries.Random.getRandomRange(0, 7) : 0;
        int r = 0;
        int g = 0;
        int b = 0;
        i = bumpBufferSize;
        while (--i >= 0)
        {
            r = bumpEmbossBitmapBuffer[i] >> rs;
            g = bumpEmbossBitmapBuffer[i] >> gs;
            b = bumpEmbossBitmapBuffer[i] >> bs;
            bumpEmbossBitmapBuffer[i] = (bumpEmbossBitmapBuffer[i] << 24) | (r << 16) | (g << 8) | b;
        }

        return createTextureFromImage(gl, bumpEmbossBitmap, bumpEmbossTexture, textureUnit);
    }

    /**
    *  Processes the bump effect.
    */
    public void bumpEffect()
    {
        bumpTimerUpdate += bumpTimeStep;
    }

    /**
    *  Renders the bump effect.
    */
    public void bumpEffectRender(GL2 gl, double x1, double y1, int x2, int y2, boolean oldLCDStyleTransparency, float specularBumpSize)
    {
        bumpEffectRender(gl, x1, y1, x2, y2, 1.0f, oldLCDStyleTransparency, specularBumpSize);
    }

    /**
    *  Renders the bump effect.
    *  Overloaded version to include an alpha value.
    */
    public void bumpEffectRender(GL2 gl, double x1, double y1, int x2, int y2, float alpha, boolean oldLCDStyleTransparency, float specularBumpSize)
    {
        useProgramAndUniforms(gl, ShaderTypes.BUMP.ordinal(), bumpTimerUpdate, alpha, oldLCDStyleTransparency, 0.0f, 0.0f, 0.0f, 0.0f, false, (bumpRandomEmboss) ? 0 : 1, false, specularBumpSize, (float)bumpWidth, (float)bumpHeight, 0.0f);
        drawTexture(gl, bumpTexture, x1, y1, x2, y2);
        gl.glUseProgram(0);
    }

    /**
    *  Renders the bump effect with an added rotozoom effect.
    */
    public void bumpEffectRotoZoomRender(GL2 gl, double x1, double y1, double theta, double zoomFactorX, double zoomFactorY, boolean oldLCDStyleTransparency, float specularBumpSize)
    {
        bumpEffectRotoZoomRender(gl, x1, y1, theta, zoomFactorX, zoomFactorY, 1.0f, oldLCDStyleTransparency, specularBumpSize);
    }

    /**
    *  Renders the bump effect with an added rotozoom effect.
    *  Overloaded version to include an alpha value.
    */
    public void bumpEffectRotoZoomRender(GL2 gl, double x1, double y1, double theta, double zoomFactorX, double zoomFactorY, float alpha, boolean oldLCDStyleTransparency, float specularBumpSize)
    {
        useProgramAndUniforms(gl, ShaderTypes.BUMP.ordinal(), bumpTimerUpdate, alpha, oldLCDStyleTransparency, 0.0f, 0.0f, 0.0f, 0.0f, false, (bumpRandomEmboss) ? 0 : 1, false, specularBumpSize, (float)bumpWidth, (float)bumpHeight, 0.0f);
        drawRotoZoomTexture(gl, bumpTexture, x1, y1, theta, zoomFactorX, zoomFactorY);
        gl.glUseProgram(0);
    }

    /**
    *  Initializes the blur effect.
    */
    public void blurEffectInit(GL2 gl, int renderToBlurWidth, int renderToBlurHeight, Texture blurEffectTexture, ShaderTextureSFXsBlurStates shaderTextureSFXsBlurState, boolean blurInterpolation)
    {
        this.renderToBlurWidth = renderToBlurWidth;
        this.renderToBlurHeight = renderToBlurHeight;
        this.blurEffectTexture = blurEffectTexture;
        this.shaderTextureSFXsBlurState = shaderTextureSFXsBlurState;
        this.blurInterpolation = blurInterpolation;

        if (shaderTextureSFXsBlurState.equals(ShaderTextureSFXsBlurStates.FULL_BLUR) && isGLExtFramebufferObjectSupported)
        {
            if (renderToBlurTexture == null)
                renderToBlurTexture = new RenderToTexture(gl);
            else
                renderToBlurTexture.disposeAllRenderToTextureResources(gl);
            renderToBlurTexture.initAllRenderToTextureResources( gl, ACTIVE_TEXTURE_UNIT_FOR_2D_TEXTURE, blurEffectTexture.getWidth(), blurEffectTexture.getHeight() );
        }
    }

    /**
    *  Renders the first pass of blur effect in a FBO texture.
    */
    private void blurEffectFirstPassRenderToTexture(GL2 gl, float alpha, boolean oldLCDStyleTransparency, float blurSize)
    {
        renderToBlurTexture.startRender(gl);
        if ( NORMAL_QUALITY_ANTIALIASING.get() || HIGH_QUALITY_ANTIALIASING.get() )
            renderToBlurTexture.prepareLowQualityRendering(gl);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClear(GL_COLOR_BUFFER_BIT);
        useProgramAndUniforms(gl, ShaderTypes.BLUR.ordinal(), 0.0f, alpha, oldLCDStyleTransparency, 0.0f, 0.0f, 0.0f, 0.0f, false, 0, blurInterpolation, blurSize, 0.0f, 0.0f, 0.0f);
        drawTexture(gl, blurEffectTexture, 0, 0, renderToBlurWidth, renderToBlurHeight);
        gl.glUseProgram(0);
        if ( NORMAL_QUALITY_ANTIALIASING.get() || HIGH_QUALITY_ANTIALIASING.get() )
            renderToBlurTexture.prepareHighQualityRendering(gl);
        renderToBlurTexture.finishRender(gl);
    }

    /**
    *  Renders the blur effect.
    */
    public void blurEffectRender(GL2 gl, double x1, double y1, int x2, int y2, boolean oldLCDStyleTransparency, float blurSize)
    {
        blurEffectRender(gl, x1, y1, x2, y2, 1.0f, oldLCDStyleTransparency, blurSize);
    }

    /**
    *  Renders the blur effect.
    *  Overloaded version to include an alpha value.
    */
    public void blurEffectRender(GL2 gl, double x1, double y1, int x2, int y2, float alpha, boolean oldLCDStyleTransparency, float blurSize)
    {
        if (shaderTextureSFXsBlurState.equals(ShaderTextureSFXsBlurStates.FULL_BLUR) && isGLExtFramebufferObjectSupported)
        {
            blurEffectFirstPassRenderToTexture(gl, alpha, oldLCDStyleTransparency, blurSize);
            useProgramAndUniforms(gl, ShaderTypes.BLUR.ordinal(), 0.0f, alpha, oldLCDStyleTransparency, 0.0f, 0.0f, 0.0f, 0.0f, false, 1, blurInterpolation, blurSize, 0.0f, 0.0f, 0.0f);
            drawRenderToTexture(gl, renderToBlurTexture, x1, y1, x2, y2, 1.0f, null, true, ACTIVE_TEXTURE_UNIT_FOR_2D_TEXTURE);
            gl.glUseProgram(0);
        }
        else // either HORIZONTAL_BLUR or VERTICAL_BLUR, FULL_BLUR only if the GL_EXT_framebuffer_object extension is not supported with many texture look-ups (slow)
        {
            int blurState = shaderTextureSFXsBlurState.equals(ShaderTextureSFXsBlurStates.HORIZONTAL_BLUR) ? 0 : ( shaderTextureSFXsBlurState.equals(ShaderTextureSFXsBlurStates.VERTICAL_BLUR) ? 1 : 2 ); // shaderTextureSFXsBlurState.equals(ShaderTextureSFXsBlurStates.FULL_BLUR)
            useProgramAndUniforms(gl, ShaderTypes.BLUR.ordinal(), 0.0f, alpha, oldLCDStyleTransparency, 0.0f, 0.0f, 0.0f, 0.0f, false, blurState, blurInterpolation, blurSize, 0.0f, 0.0f, 0.0f);
            drawTexture(gl, blurEffectTexture, x1, y1, x2, y2);
            gl.glUseProgram(0);

        }
    }

    /**
    *  Renders the blur effect with an added rotozoom effect.
    */
    public void blurEffectRotoZoomRender(GL2 gl, double x1, double y1, double theta, double zoomFactorX, double zoomFactorY, boolean oldLCDStyleTransparency, float blurSize)
    {
        blurEffectRotoZoomRender(gl, x1, y1, theta, zoomFactorX, zoomFactorY, 1.0f, oldLCDStyleTransparency, blurSize);
    }

    /**
    *  Renders the blur effect with an added rotozoom effect.
    *  Overloaded version to include an alpha value.
    */
    public void blurEffectRotoZoomRender(GL2 gl, double x1, double y1, double theta, double zoomFactorX, double zoomFactorY, float alpha, boolean oldLCDStyleTransparency, float blurSize)
    {
        if (shaderTextureSFXsBlurState.equals(ShaderTextureSFXsBlurStates.FULL_BLUR) && isGLExtFramebufferObjectSupported)
        {
            blurEffectFirstPassRenderToTexture(gl, alpha, oldLCDStyleTransparency, blurSize);
            useProgramAndUniforms(gl, ShaderTypes.BLUR.ordinal(), 0.0f, alpha, oldLCDStyleTransparency, 0.0f, 0.0f, 0.0f, 0.0f, false, 1, blurInterpolation, blurSize, 0.0f, 0.0f, 0.0f);
            drawRotoZoomRenderToTexture(gl, renderToBlurTexture, x1, y1, theta, zoomFactorX, zoomFactorY, 1.0f, null, true, ACTIVE_TEXTURE_UNIT_FOR_2D_TEXTURE);
            gl.glUseProgram(0);
        }
        else // either HORIZONTAL_BLUR or VERTICAL_BLUR, FULL_BLUR only if the GL_EXT_framebuffer_object extension is not supported with many texture look-ups (slow)
        {
            int blurState = shaderTextureSFXsBlurState.equals(ShaderTextureSFXsBlurStates.HORIZONTAL_BLUR) ? 0 : ( shaderTextureSFXsBlurState.equals(ShaderTextureSFXsBlurStates.VERTICAL_BLUR) ? 1 : 2 ); // shaderTextureSFXsBlurState.equals(ShaderTextureSFXsBlurStates.FULL_BLUR)
            useProgramAndUniforms(gl, ShaderTypes.BLUR.ordinal(), 0.0f, alpha, oldLCDStyleTransparency, 0.0f, 0.0f, 0.0f, 0.0f, false, blurState, blurInterpolation, blurSize, 0.0f, 0.0f, 0.0f);
            drawRotoZoomTexture(gl, blurEffectTexture, x1, y1, theta, zoomFactorX, zoomFactorY);
            gl.glUseProgram(0);
        }
    }

    /**
    *  Initializes the radial blur effect.
    */
    public void radialBlurEffectInit(float radialblurTimeStep)
    {
        this.radialblurTimeStep = radialblurTimeStep;
        radialblurTimerUpdate = 0.0f;
    }

    /**
    *  Processes the radial blur effect.
    */
    public void radialBlurEffect()
    {
        radialblurTimerUpdate += radialblurTimeStep;
    }

    /**
    *  Renders the radial blur effect.
    */
    public void radialBlurEffectRender(GL2 gl, Texture radialBlurEffectTexture, double x1, double y1, int x2, int y2, boolean oldLCDStyleTransparency, float radialBlurSize, float radialBlurPower)
    {
        radialBlurEffectRender(gl, radialBlurEffectTexture, x1, y1, x2, y2, 1.0f, oldLCDStyleTransparency, radialBlurSize, radialBlurPower);
    }

    /**
    *  Renders the radial blur effect.
    *  Overloaded version to include an alpha value.
    */
    public void radialBlurEffectRender(GL2 gl, Texture radialBlurEffectTexture, double x1, double y1, int x2, int y2, float alpha, boolean oldLCDStyleTransparency, float radialBlurSize, float radialBlurPower)
    {
        useProgramAndUniforms(gl, ShaderTypes.RADIAL_BLUR.ordinal(), radialblurTimerUpdate, alpha, oldLCDStyleTransparency, 0.0f, 0.0f, 0.0f, 0.0f, false, 0, false, radialBlurSize, 0.0f, 0.0f, radialBlurPower);
        drawTexture(gl, radialBlurEffectTexture, x1, y1, x2, y2);
        gl.glUseProgram(0);
    }

    /**
    *  Renders the radial blur effect with an added rotozoom effect.
    */
    public void radialBlurEffectRotoZoomRender(GL2 gl, Texture radialBlurEffectTexture, double x1, double y1, double theta, double zoomFactorX, double zoomFactorY, boolean oldLCDStyleTransparency, float radialBlurSize, float radialBlurPower)
    {
        radialBlurEffectRotoZoomRender(gl, radialBlurEffectTexture, x1, y1, theta, zoomFactorX, zoomFactorY, 1.0f, oldLCDStyleTransparency, radialBlurSize, radialBlurPower);
    }

    /**
    *  Renders the radial blur effect with an added rotozoom effect.
    *  Overloaded version to include an alpha value.
    */
    public void radialBlurEffectRotoZoomRender(GL2 gl, Texture radialBlurEffectTexture, double x1, double y1, double theta, double zoomFactorX, double zoomFactorY, float alpha, boolean oldLCDStyleTransparency, float radialBlurSize, float radialBlurPower)
    {
        useProgramAndUniforms(gl, ShaderTypes.RADIAL_BLUR.ordinal(), radialblurTimerUpdate, alpha, oldLCDStyleTransparency, 0.0f, 0.0f, 0.0f, 0.0f, false, 0, false, radialBlurSize, 0.0f, 0.0f, radialBlurPower);
        drawRotoZoomTexture(gl, radialBlurEffectTexture, x1, y1, theta, zoomFactorX, zoomFactorY);
        gl.glUseProgram(0);
    }

    /**
    *  Moves the blob stars 3D scroller field according to the mouse move values.
    */
    public void blobStars3DScrollerEffectMouseMove(int x, int y)
    {
        if (blobStars3DScrollerMouseMovePrevious[0] != x)
        {
            if (blobStars3DScrollerMouseMovePrevious[0] < x)
                blobStars3DScrollerMouseMove[0] += blobMoveStep;
            else
                blobStars3DScrollerMouseMove[0] -= blobMoveStep;
        }

        if (blobStars3DScrollerMouseMovePrevious[1] != y)
        {
            if (blobStars3DScrollerMouseMovePrevious[1] < y)
                blobStars3DScrollerMouseMove[1] += blobMoveStep;
            else
                blobStars3DScrollerMouseMove[1] -= blobMoveStep;
        }

        if ( blobStars3DScrollerMouseMove[0] > BLOB_MOVE_RESET_VALUE || blobStars3DScrollerMouseMove[0] < -BLOB_MOVE_RESET_VALUE ||
             blobStars3DScrollerMouseMove[1] > BLOB_MOVE_RESET_VALUE || blobStars3DScrollerMouseMove[1] < -BLOB_MOVE_RESET_VALUE )
        {
            blobMoveStep = -blobMoveStep;
        }

        blobStars3DScrollerMouseMove[0] = x;
        blobStars3DScrollerMouseMove[1] = y;
    }

    /**
    *  Initializes the blob stars 3D scroller effect.
    */
    public void blobStars3DScrollerEffectInit(GL2 gl, BlobStars3DScrollerEffectInitializer blobStars3DScrollerEffectInitializer, float blobStars3DScrollerTimerUpdateStep, int width, int height)
    {
        this.blobStars3DScrollerEffectInitializer = blobStars3DScrollerEffectInitializer;
        this.blobStars3DScrollerTimerUpdateStep = blobStars3DScrollerTimerUpdateStep;
        blobStars3DScrollerTimerUpdate = 0.0f;

        blobStars3DScrollerMouseMove[0] = 0;
        blobStars3DScrollerMouseMove[1] = 0;
        blobMoveStep = abs(blobMoveStep);

        if (blobStars3DScrollerEffectInitializer.useBlobMotionBlur && isGLExtFramebufferObjectSupported)
        {
            if (renderToMotionBlurForBlob3DStarsTextures == null)
            {
                renderToMotionBlurForBlob3DStarsTextures = new RenderToTexture[NUMBER_OF_PING_PONG_FBOS];
                for (int i = 0; i < NUMBER_OF_PING_PONG_FBOS; i++)
                    renderToMotionBlurForBlob3DStarsTextures[i] = new RenderToTexture(gl);
            }
            else
            {
                for (int i = 0; i < NUMBER_OF_PING_PONG_FBOS; i++)
                    renderToMotionBlurForBlob3DStarsTextures[i].disposeAllRenderToTextureResources(gl);
            }

            for (int i = 0; i < NUMBER_OF_PING_PONG_FBOS; i++)
                renderToMotionBlurForBlob3DStarsTextures[i].initAllRenderToTextureResources(gl, ACTIVE_TEXTURE_UNIT_FOR_2D_TEXTURE, width, height);
        }
    }

    /**
    *  Processes the blob stars 3D scroller effect.
    */
    public void blobStars3DScrollerEffect()
    {
        blobStars3DScrollerTimerUpdate += blobStars3DScrollerTimerUpdateStep;
    }

    /**
    *  Renders the first pass of blob 3D stars effect in a FBO texture.
    */
    private void blob3DStarsPingPongFBOsMotionBlurRenderToTexture(GL2 gl, float alpha, boolean oldLCDStyleTransparency, double x1, double y1, int x2, int y2)
    {
        // bind first FBO texture
        renderToMotionBlurForBlob3DStarsTextures[currentFBOIndex].bind(gl, ACTIVE_TEXTURE_UNIT_FOR_2D_TEXTURE);
        // switch FBO order (ping-pong FBOs effect)
        currentFBOIndex = (++currentFBOIndex & 1);

        // bind second FBO framebuffer
        renderToMotionBlurForBlob3DStarsTextures[currentFBOIndex].startRender(gl);
        if ( NORMAL_QUALITY_ANTIALIASING.get() || HIGH_QUALITY_ANTIALIASING.get() )
            renderToMotionBlurForBlob3DStarsTextures[currentFBOIndex].prepareLowQualityRendering(gl);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClear(GL_COLOR_BUFFER_BIT);
        useProgramAndUniforms(gl, ShaderTypes.BLOB_STARS_3D_SCROLLER.ordinal(), blobStars3DScrollerTimerUpdate, alpha, oldLCDStyleTransparency, 0.0f, 0.0f, 0.0f, 0.0f, false, 1, false, 0.0f, (float)(renderToMotionBlurForBlob3DStarsTextures[currentFBOIndex].getWidth() - x1), (float)(renderToMotionBlurForBlob3DStarsTextures[currentFBOIndex].getHeight() - y1), 0.0f);
        drawQuadWithTextureCoords(gl, x1, y1, x2, y2);
        gl.glUseProgram(0);
        if ( NORMAL_QUALITY_ANTIALIASING.get() || HIGH_QUALITY_ANTIALIASING.get() )
            renderToMotionBlurForBlob3DStarsTextures[currentFBOIndex].prepareHighQualityRendering(gl);
        renderToMotionBlurForBlob3DStarsTextures[currentFBOIndex].finishRender(gl);
    }

    /**
    *  Renders the blob stars 3D scroller effect.
    */
    public void blobStars3DScrollerEffectRender(GL2 gl, double x1, double y1, int x2, int y2, boolean oldLCDStyleTransparency)
    {
        blobStars3DScrollerEffectRender(gl, x1, y1, x2 ,y2, 1.0f, oldLCDStyleTransparency);
    }

    /**
    *  Renders the blob stars 3D scroller effect.
    *  Overloaded version to include an alpha value.
    */
    public void blobStars3DScrollerEffectRender(GL2 gl, double x1, double y1, int x2, int y2, float alpha, boolean oldLCDStyleTransparency)
    {
        if (blobStars3DScrollerEffectInitializer.useBlobMotionBlur && isGLExtFramebufferObjectSupported)
        {
            blob3DStarsPingPongFBOsMotionBlurRenderToTexture(gl, alpha, oldLCDStyleTransparency, x1, y1, x2, y2);
            drawRenderToTexture(gl, renderToMotionBlurForBlob3DStarsTextures[currentFBOIndex], x1, y1, x2, y2, 1.0f, null, true, ACTIVE_TEXTURE_UNIT_FOR_2D_TEXTURE);
        }
        else
        {
            useProgramAndUniforms(gl, ShaderTypes.BLOB_STARS_3D_SCROLLER.ordinal(), blobStars3DScrollerTimerUpdate, alpha, oldLCDStyleTransparency, 0.0f, 0.0f, 0.0f, 0.0f, false, 0, false, 0.0f, (float)(x2 - x1), (float)(y2 - y1), 0.0f);
            drawQuadWithTextureCoords(gl, x1, y1, x2, y2);
            gl.glUseProgram(0);
        }
    }

    /**
    *  Renders the blob stars 3D scroller effect with an added rotozoom effect.
    */
    public void blobStars3DScrollerEffectRotoZoomRender(GL2 gl, double x1, double y1, int x2, int y2, double theta, double zoomFactorX, double zoomFactorY, boolean oldLCDStyleTransparency)
    {
        blobStars3DScrollerEffectRotoZoomRender(gl, x1, y1, x2, y2, theta, zoomFactorX, zoomFactorY, 1.0f, oldLCDStyleTransparency);
    }

    /**
    *  Renders the blob stars 3D scroller effect with an added rotozoom effect.
    *  Overloaded version to include an alpha value.
    */
    public void blobStars3DScrollerEffectRotoZoomRender(GL2 gl, double x1, double y1, int x2, int y2, double theta, double zoomFactorX, double zoomFactorY, float alpha, boolean oldLCDStyleTransparency)
    {
        if (blobStars3DScrollerEffectInitializer.useBlobMotionBlur && isGLExtFramebufferObjectSupported)
        {
            blob3DStarsPingPongFBOsMotionBlurRenderToTexture(gl, alpha, oldLCDStyleTransparency, x1, y1, x2, y2);
            drawRotoZoomRenderToTexture(gl, renderToMotionBlurForBlob3DStarsTextures[currentFBOIndex], x1, y1, theta, zoomFactorX, zoomFactorY, 1.0f, null, true, ACTIVE_TEXTURE_UNIT_FOR_2D_TEXTURE);
        }
        else
        {
            useProgramAndUniforms(gl, ShaderTypes.BLOB_STARS_3D_SCROLLER.ordinal(), blobStars3DScrollerTimerUpdate, alpha, oldLCDStyleTransparency, 0.0f, 0.0f, 0.0f, 0.0f, false, 0, false, 0.0f, (float)(renderToMotionBlurForBlob3DStarsTextures[currentFBOIndex].getWidth() - x1), (float)(renderToMotionBlurForBlob3DStarsTextures[currentFBOIndex].getHeight() - y1), 0.0f);
            drawRotoZoomQuadWithTextureCoords(gl, x1, y1, renderToMotionBlurForBlob3DStarsTextures[currentFBOIndex].getWidth(), renderToMotionBlurForBlob3DStarsTextures[currentFBOIndex].getHeight(), theta, zoomFactorX, zoomFactorY);
            gl.glUseProgram(0);
        }
    }

    /**
    *  Uses the Shader animation GPU Computing.
    */
    public void useShaderAnimationGPUComputing(GL2 gl, boolean oldLCDStyleTransparency, float textureWidth, float textureHeight, float nodeValue, boolean processNextNodeValue, float nextNodeValue, int animationFrameCount)
    {
        useShaderAnimationGPUComputing(gl, 1.0f, oldLCDStyleTransparency, textureWidth, textureHeight, nodeValue, processNextNodeValue, nextNodeValue, animationFrameCount);
    }

    /**
    *  Uses the Shader animation GPU Computing.
    *  Overloaded version to include an alpha value.
    */
    public void useShaderAnimationGPUComputing(GL2 gl, float alpha, boolean oldLCDStyleTransparency, float textureWidth, float textureHeight, float nodeValue, boolean processNextNodeValue, float nextNodeValue, int animationFrameCount)
    {
        useProgramAndUniforms(gl, ShaderTypes.ANIMATION.ordinal(), 0.0f, alpha, oldLCDStyleTransparency, 0.0f, 0.0f, 0.0f, 0.0f, false, 0, false, 0.0f, 0.0f,
                                  textureWidth, textureHeight, nodeValue, processNextNodeValue, nextNodeValue, animationFrameCount);
    }

    /**
    *  Disables the Shader animation GPU Computing.
    */
    public void disableShaderAnimationGPUComputing(GL2 gl)
    {
        gl.glUseProgram(0);
    }

    /**
    *  Destroys (de-initializes) all the effect resources.
    */
    public void destructor(GL2 gl)
    {
        for (int i = 0; i < NUMBER_OF_AVAILABLE_SHADERS; i++)
            ShaderUtils.detachAndDeleteShader(gl, VERTEX_SHADERS, FRAGMENT_SHADERS, SHADER_PROGRAMS, i);

        if (USE_SPOT_CIRCLE_RANDOM_2D_TEXTURE)
        {
            spotCircleTexture = null;
            spotCircleRandomTexture = null;
        }
        if (waterBufferImage != null) waterBufferImage.flush();
        if (waterBufferTexture != null) waterBufferTexture = null;
        if (bumpEmbossTexture != null) bumpEmbossTexture = null;
        if (isGLExtFramebufferObjectSupported && renderToBlurTexture != null) renderToBlurTexture.disposeAllRenderToTextureResources(gl);
        if (isGLExtFramebufferObjectSupported && renderToMotionBlurForBlob3DStarsTextures != null)
        {
            for (int i = 0; i < NUMBER_OF_PING_PONG_FBOS; i++)
            {
                renderToMotionBlurForBlob3DStarsTextures[i].disposeAllRenderToTextureResources(gl);
                renderToMotionBlurForBlob3DStarsTextures[i] = null;
            }
            renderToMotionBlurForBlob3DStarsTextures = null;
        }
    }


}
