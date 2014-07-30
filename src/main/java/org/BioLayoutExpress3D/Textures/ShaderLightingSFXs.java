package org.BioLayoutExpress3D.Textures;

import java.awt.image.*;
import javax.media.opengl.*;
import com.jogamp.opengl.util.texture.*;
import static javax.media.opengl.GL.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import static org.BioLayoutExpress3D.Textures.ShaderTextureSFXs.*;
import static org.BioLayoutExpress3D.Graph.Graph.*;
import static org.BioLayoutExpress3D.Environment.AnimationEnvironment.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
*
*  Various GLSL shader lighting operations used as special effects.
*  This class is responsible for producing lighting using various effects using the GLSL specification.
*
*
* @author Thanos Theo, 2008-2009
* @version 3.0.0.0
*
*/

public class ShaderLightingSFXs
{

    /**
    *  Available shader types.
    */
    public static enum ShaderTypes { PHONG, BUMP, TOON, GOOCH, CLOUD, LAVA, MARBLE, GRANITE, WOOD, BRICK, HATCHING, GLYPH_BOMBING, WATER, VORONOI, FRACTAL }

    /**
    *  Available number of lighting shaders.
    */
    public static final int NUMBER_OF_AVAILABLE_SHADERS = ShaderTypes.values().length;

    /**
    *  Directory of GPU Computing.
    */
    protected static final String GPU_COMPUTING_DIRECTORY = "GPUComputing/";

    /**
    *  Directory of 3D shader files.
    */
    protected static String SHADER_FILES_DIRECTORY_1 = "3D";

    /**
    *  Directory of Effects shader files.
    */
    protected static final String SHADER_FILES_DIRECTORY_2 = "Effects";

    /**
    *  Directory of Animation shader files.
    */
    protected static final String SHADER_FILES_DIRECTORY_3 = "Animation";

    /**
    *  File name of Effects shader files.
    */
    protected static final String SHADER_FILE_NAME_2 = "Effects";

    /**
    *  File name of Animation shader files.
    */
    protected static final String SHADER_FILE_NAME_3 = "Animation";

    /**
    *  Constant value needed for the pre-calculated effects 2D texture.
    */
    private static final int PRE_CALC_EFFECTS_2D_TEXTURE_SIZE = 2048;

    /**
    *  Active texture unit to be used for the water buffer 2D texture.
    */
    private static final int ACTIVE_TEXTURE_UNIT_FOR_WATER_BUFFER_2D_TEXTURE = ACTIVE_TEXTURE_UNIT_FOR_ANIMATION_SPECTRUM_2D_TEXTURE + 1;

    /**
    *  Active texture unit to be used for the bump3d 2D texture.
    */
    private static final int ACTIVE_TEXTURE_UNIT_FOR_BUMP3D_EMBOSS_2D_TEXTURE = ACTIVE_TEXTURE_UNIT_FOR_ANIMATION_SPECTRUM_2D_TEXTURE + 2;

    /**
    *  Active texture unit to be used for the noise 3D texture.
    */
    private static final int ACTIVE_TEXTURE_UNIT_FOR_PERLIN_NOISE_3D_TEXTURE = ACTIVE_TEXTURE_UNIT_FOR_ANIMATION_SPECTRUM_2D_TEXTURE + 3;

    /**
    *  Active texture unit to be used for the glyphbombing 3D texture.
    */
    private static final int ACTIVE_TEXTURE_UNIT_FOR_GLYPHBOMBING_3D_TEXTURE = ACTIVE_TEXTURE_UNIT_FOR_ANIMATION_SPECTRUM_2D_TEXTURE + 4;

    /**
    *  Vertex shader storage.
    *  3 sets of shader files per shader program.
    */
    protected int[][] VERTEX_SHADERS = new int[NUMBER_OF_AVAILABLE_SHADERS][3];

    /**
    *  Geometry shader storage.
    *  3 sets of shader files per shader program.
    */
    protected int[][] GEOMETRY_SHADERS = new int[NUMBER_OF_AVAILABLE_SHADERS][3];

    /**
    *  Fragment shader storage.
    *  3 sets of shader files per shader program.
    */
    protected int[][] FRAGMENT_SHADERS = new int[NUMBER_OF_AVAILABLE_SHADERS][3];

    /**
    *  Shader program storage.
    */
    protected int[] SHADER_PROGRAMS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Variable needed for the vertex/geometry/fragment pairs.
    */
    protected boolean[][] loadShadersPairs = null;

    /**
    *  Shader program 2D texture name.
    */
    protected static final String SHADER_PROGRAM_2D_TEXTURE_NAME = "texture";

    /**
    *  Shader program 2D texture storage.
    */
    protected int[] SHADER_PROGRAM_2D_TEXTURES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program Animation 2D texture name.
    */
    protected static final String SHADER_PROGRAM_ANIMATION_2D_TEXTURE_NAME = "Animation2DTexture";

    /**
    *  Shader program Animation 2D texture storage.
    */
    protected int[] SHADER_PROGRAM_ANIMATION_2D_TEXTURES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program water buffer 2D texture name.
    */
    private static final String SHADER_PROGRAM_WATER_BUFFER_2D_TEXTURE_NAME = "Buffer2DTexture";

    /**
    *  Shader program water buffer 2D texture storage.
    */
    private final int[] SHADER_PROGRAM_WATER_BUFFER_2D_TEXTURES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program bump3d emboss 2D texture name.
    */
    private static final String SHADER_PROGRAM_BUMP3D_EMBOSS_2D_TEXTURE_NAME = "Emboss2DTexture";

    /**
    *  Shader program bump3d emboss 2D texture storage.
    */
    private final int[] SHADER_PROGRAM_BUMP3D_EMBOSS_2D_TEXTURES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program noise 3D texture name.
    */
    private static final String SHADER_PROGRAM_PERLIN_NOISE_3D_TEXTURE_NAME = "PerlinNoise3DTexture";

    /**
    *  Shader program noise 3D texture storage.
    */
    private final int[] SHADER_PROGRAM_PERLIN_NOISE_3D_TEXTURES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program glyphbombing 3D texture name.
    */
    private static final String SHADER_PROGRAM_GLYPHBOMBING_3D_TEXTURE_NAME = "Glyphbombing3DTexture";

    /**
    *  Shader program glyphbombing 3D texture storage.
    */
    private final int[] SHADER_PROGRAM_GLYPHBOMBING_3D_TEXTURES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program fog name.
    */
    protected static final String SHADER_PROGRAM_FOG_NAME = "Fog";

    /**
    *  Shader program fog storage.
    */
    protected int[] SHADER_PROGRAM_FOGS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program texturing name.
    */
    protected static final String SHADER_PROGRAM_TEXTURING_NAME = "Texturing";

    /**
    *  Shader program texturing storage.
    */
    protected int[] SHADER_PROGRAM_TEXTURINGS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program sphrerical mapping name.
    */
    protected static final String SHADER_PROGRAM_SPHERICAL_MAPPING_NAME = "SphericalMapping";

    /**
    *  Shader program sphrerical mapping storage.
    */
    protected int[] SHADER_PROGRAM_SPHERICAL_MAPPINGS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program emboss node texture name.
    */
    protected static final String SHADER_PROGRAM_EMBOSS_NODE_TEXTURE_NAME = "embossNodeTexture";

    /**
    *  Shader program emboss node texture storage.
    */
    protected int[] SHADER_PROGRAM_EMBOSS_NODE_TEXTURES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program px name.
    */
    protected static final String SHADER_PROGRAM_PX_NAME = "px";

    /**
    *  Shader program px storage.
    */
    protected int[] SHADER_PROGRAM_PXS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program py name.
    */
    protected static final String SHADER_PROGRAM_PY_NAME = "py";

    /**
    *  Shader program py storage.
    */
    protected int[] SHADER_PROGRAM_PYS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program timer name.
    */
    protected static final String SHADER_PROGRAM_TIMER_NAME = "Timer";

    /**
    *  Shader program timer storage.
    */
    protected int[] SHADER_PROGRAM_TIMERS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program morphing name.
    */
    protected static final String SHADER_PROGRAM_MORPHING_NAME = "Morphing";

    /**
    *  Shader program morphing storage.
    */
    protected int[] SHADER_PROGRAM_MORPHINGS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program user clipping name.
    */
    protected static final String SHADER_PROGRAM_USER_CLIPPING_NAME = "UserClipping";

    /**
    *  Shader program user clipping storage.
    */
    protected int[] SHADER_PROGRAM_USER_CLIPPINGS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program antialias name.
    */
    protected static final String SHADER_PROGRAM_ANTIALIAS_NAME = "AntiAlias";

    /**
    *  Shader program antialias storage.
    */
    protected int[] SHADER_PROGRAM_ANTIALIASES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program state name.
    */
    protected static final String SHADER_PROGRAM_STATE_NAME = "State";

    /**
    *  Shader program state storage.
    */
    protected int[] SHADER_PROGRAM_STATES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program old LCD style transparency name.
    */
    protected static final String SHADER_PROGRAM_OLD_LCD_STYLE_TRANSPARENCY_NAME = "OldLCDStyleTransparency";

    /**
    *  Shader program old LCD style transparency storage.
    */
    protected int[] SHADER_PROGRAM_OLD_LCD_STYLE_TRANSPARENCIES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program erosion name.
    */
    private static final String SHADER_PROGRAM_EROSION_NAME = "Erosion";

    /**
    *  Shader program erosion storage.
    */
    private final int[] SHADER_PROGRAM_EROSIONS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program shrink triangles name.
    */
    protected static final String SHADER_PROGRAM_SHRINK_TRIANGLES_NAME = "ShrinkTriangles";

    /**
    *  Shader program shrink triangles storage.
    */
    protected int[] SHADER_PROGRAM_SHRINK_TRIANGLES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program solid wireframe name.
    */
    protected static final String SHADER_PROGRAM_SOLID_WIREFRAME_NAME = "SolidWireFrame";

    /**
    *  Shader program solid wireframe storage.
    */
    protected int[] SHADER_PROGRAM_SOLID_WIREFRAMES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program normals name.
    */
    protected static final String SHADER_PROGRAM_NORMALS_NAME = "Normals";

    /**
    *  Shader program normals storage.
    */
    protected int[] SHADER_PROGRAM_NORMALS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program pre-calculated effects 2D texture px name.
    */
    private static final String SHADER_PROGRAM_PRE_CALC_EFFECTS_2D_TEXTURE_PX_NAME = "PreCalcEffects2DTexturePx";

    /**
    *  Shader program pre-calculated effects 2D texture px storage.
    */
    private final int[] SHADER_PROGRAM_PRE_CALC_EFFECTS_2D_TEXTURE_PXS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program pre-calculated effects 2D texture py name.
    */
    private static final String SHADER_PROGRAM_PRE_CALC_EFFECTS_2D_TEXTURE_PY_NAME = "PreCalcEffects2DTexturePy";

    /**
    *  Shader program pre-calculated effects 2D texture py storage.
    */
    private final int[] SHADER_PROGRAM_PRE_CALC_EFFECTS_2D_TEXTURE_PYS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program animationGPUComputingMode name.
    */
    protected static final String SHADER_PROGRAM_ANIMATION_GPU_COMPUTING_MODE_NAME = "AnimationGPUComputingMode";

    /**
    *  Shader program animationGPUComputingMode storage.
    */
    protected int[] SHADER_PROGRAM_ANIMATION_GPU_COMPUTING_MODES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program nodeValue name.
    */
    protected static final String SHADER_PROGRAM_NODE_VALUE_NAME = "NodeValue";

    /**
    *  Shader program nodeValue storage.
    */
    protected int[] SHADER_PROGRAM_NODE_VALUES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program processNextNodeValue name.
    */
    protected static final String SHADER_PROGRAM_PPROCESS_NEXT_NODE_VALUE_NAME = "ProcessNextNodeValue";

    /**
    *  Shader program processNextNodeValue storage.
    */
    protected int[] SHADER_PROGRAM_PROCESS_NEXT_NODE_VALUES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program nextNodeValue name.
    */
    protected final String SHADER_PROGRAM_NEXT_NODE_VALUE_NAME = "NextNodeValue";

    /**
    *  Shader program nextNodeValue storage.
    */
    protected int[] SHADER_PROGRAM_NEXT_NODE_VALUES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program animationFrameCount name.
    */
    protected static final String SHADER_PROGRAM_ANIMATION_FRAME_COUNT_NAME = "AnimationFrameCount";

    /**
    *  Shader program animationFrameCount storage.
    */
    protected int[] SHADER_PROGRAM_ANIMATION_FRAME_COUNTS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program FRAMERATE_PER_SECOND_FOR_ANIMATION name.
    */
    protected static final String SHADER_PROGRAM_FRAMERATE_PER_SECOND_FOR_ANIMATION_NAME = "FRAMERATE_PER_SECOND_FOR_ANIMATION";

    /**
    *  Shader program FRAMERATE_PER_SECOND_FOR_ANIMATION storage.
    */
    protected int[] SHADER_PROGRAM_FRAMERATE_PER_SECOND_FOR_ANIMATIONS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program ANIMATION_FLUID_LINEAR_TRANSITION name.
    */
    protected static final String SHADER_PROGRAM_ANIMATION_FLUID_LINEAR_TRANSITION_NAME = "ANIMATION_FLUID_LINEAR_TRANSITION";

    /**
    *  Shader program ANIMATION_FLUID_LINEAR_TRANSITION storage.
    */
    protected int[] SHADER_PROGRAM_ANIMATION_FLUID_LINEAR_TRANSITIONS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program ANIMATION_FLUID_POLYNOMIAL_TRANSITION name.
    */
    protected static final String SHADER_PROGRAM_ANIMATION_FLUID_POLYNOMIAL_TRANSITION_NAME = "ANIMATION_FLUID_POLYNOMIAL_TRANSITION";

    /**
    *  Shader program ANIMATION_FLUID_POLYNOMIAL_TRANSITION storage.
    */
    protected int[] SHADER_PROGRAM_ANIMATION_FLUID_POLYNOMIAL_TRANSITIONS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program ANIMATION_TICKS_PER_SECOND name.
    */
    protected static final String SHADER_PROGRAM_ANIMATION_TICKS_PER_SECOND_NAME = "ANIMATION_TICKS_PER_SECOND";

    /**
    *  Shader program ANIMATION_TICKS_PER_SECOND storage.
    */
    protected int[] SHADER_PROGRAM_ANIMATION_TICKS_PER_SECONDS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program ANIMATION_MAX_NODE_SIZE name.
    */
    protected static final String SHADER_PROGRAM_ANIMATION_MAX_NODE_SIZE_NAME = "ANIMATION_MAX_NODE_SIZE";

    /**
    *  Shader program ANIMATION_MAX_NODE_SIZE storage.
    */
    protected int[] SHADER_PROGRAM_ANIMATION_MAX_NODE_SIZES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program ANIMATION_RESULTS_MAX_VALUE name.
    */
    protected static final String SHADER_PROGRAM_ANIMATION_RESULTS_MAX_VALUE_NAME = "ANIMATION_RESULTS_MAX_VALUE";

    /**
    *  Shader program ANIMATION_RESULTS_MAX_VALUE storage.
    */
    protected int[] SHADER_PROGRAM_ANIMATION_RESULTS_MAX_VALUES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program ANIMATION_RESULTS_REAL_MAX_VALUE name.
    */
    protected static final String SHADER_PROGRAM_ANIMATION_RESULTS_REAL_MAX_VALUE_NAME = "ANIMATION_RESULTS_REAL_MAX_VALUE";

    /**
    *  Shader program ANIMATION_RESULTS_REAL_MAX_VALUE storage.
    */
    protected int[] SHADER_PROGRAM_ANIMATION_RESULTS_REAL_MAX_VALUES = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION name.
    */
    protected static final String SHADER_PROGRAM_ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION_NAME = "ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION";

    /**
    *  Shader program ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION storage.
    */
    protected int[] SHADER_PROGRAM_ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITIONS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITION name.
    */
    protected static final String SHADER_PROGRAM_ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITION_NAME = "ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITION";

    /**
    *  Shader program ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITION storage.
    */
    protected int[] SHADER_PROGRAM_ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITIONS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program ANIMATION_MAX_SPECTRUM_COLOR name.
    */
    protected static final String SHADER_PROGRAM_ANIMATION_MAX_SPECTRUM_COLOR_NAME = "ANIMATION_MAX_SPECTRUM_COLOR";

    /**
    *  Shader program ANIMATION_MAX_SPECTRUM_COLOR storage.
    */
    protected int[] SHADER_PROGRAM_ANIMATION_MAX_SPECTRUM_COLORS = new int[NUMBER_OF_AVAILABLE_SHADERS];

    /**
    *  Shader program ANIMATION_USE_IMAGE_AS_SPECTRUM name.
    */
    protected static final String SHADER_PROGRAM_ANIMATION_USE_IMAGE_AS_SPECTRUM_NAME = "ANIMATION_USE_IMAGE_AS_SPECTRUM";

    /**
    *  Shader program ANIMATION_USE_IMAGE_AS_SPECTRUM storage.
    */
    protected int[] SHADER_PROGRAM_ANIMATION_USE_IMAGE_AS_SPECTRUMS = new int[NUMBER_OF_AVAILABLE_SHADERS];

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
    *  Variable to store the water texture.
    */
    private Texture waterBufferTexture = null;

    /**
    *  Emboss 2D texture for the Bump3d shader.
    *  Static instantiation to be used with multiple class instances.
    */
    private static Texture bump3DEmboss2DTexture = null;

    /**
    *  Perlin Noise 3D texture support for Cloud, Lava, Marble, Granite & Wood Shaders.
    *  Static instantiation to be used with multiple class instances.
    */
    private static PerlinNoise3DTexture perlinNoise3DTexture = null;

    /**
    *  Glyphbombing 3D texture support for the Glyphbombing Shader.
    *  Static instantiation to be used with multiple class instances.
    */
    private static Glyphbombing3DTexture glyphbombing3DTexture = null;

    /**
    *  Variable to store the timer update.
    */
    protected float timerUpdate = 0.0f;

    /**
    *  Variable to store the timer update step.
    */
    protected float timerUpdateStep = 0.0f;

    /**
    *  Variable to store the applyNormalsGeometry option.
    */
    protected boolean applyNormalsGeometry = false;

    /**
    *  The first constructor of the ShaderLightingSFXs class.
    */
    public ShaderLightingSFXs(GL2 gl)
    {
        this(gl, 0.01f, false, true);
    }

    /**
    *  The second constructor of the ShaderLightingSFXs class.
    */
    public ShaderLightingSFXs(GL2 gl, float timerUpdateStep)
    {
        this(gl, timerUpdateStep, false, true);
    }

    /**
    *  The fourth constructor of the ShaderLightingSFXs class.
    */
    public ShaderLightingSFXs(GL2 gl, boolean applyNormalsGeometry)
    {
        this(gl, 0.01f, applyNormalsGeometry, true);
    }

    /**
    *  The fifth constructor of the ShaderLightingSFXs class.
    */
    public ShaderLightingSFXs(GL2 gl, float timerUpdateStep, boolean applyNormalsGeometry)
    {
        this(gl, timerUpdateStep, applyNormalsGeometry, true);
    }

    /**
    *  The sixth constructor of the ShaderLightingSFXs class. Package access only.
    */
    ShaderLightingSFXs(GL2 gl, float timerUpdateStep, boolean applyNormalsGeometry, boolean useInitMethods)
    {
        this.timerUpdateStep = timerUpdateStep;
        this.applyNormalsGeometry = applyNormalsGeometry;

        if (useInitMethods)
            initAllMethods(gl);
    }

    /**
    *  Initializes all relevant methods.
    */
    private void initAllMethods(GL2 gl)
    {
        initWaterEffect(32, true, 1, 10.0f);
        initBump3D2DTexture(gl, ACTIVE_TEXTURE_UNIT_FOR_BUMP3D_EMBOSS_2D_TEXTURE);
        initPerlinNoise3DTexture(gl, ACTIVE_TEXTURE_UNIT_FOR_PERLIN_NOISE_3D_TEXTURE);
        initGlyphbombing3DTexture(gl, ACTIVE_TEXTURE_UNIT_FOR_GLYPHBOMBING_3D_TEXTURE);
        loadAndCompileAllShaderPrograms(gl);
    }

    /**
    *  Initializes the water effect.
    */
    private void initWaterEffect(int minNumberOfWaterDrops, boolean logarithmicDropAnimation, int waterDropSize, float waterTimeStep)
    {
        this.minNumberOfWaterDrops = minNumberOfWaterDrops;
        this.logarithmicDropAnimation = logarithmicDropAnimation;
        this.waterDropSize = (waterDropSize < 1) ? 1 : ( (waterDropSize >= MAX_NUMBER_OF_WATER_DROPS) ? MAX_NUMBER_OF_WATER_DROPS : waterDropSize ) ;
        this.waterTimeStep = this.waterDropTimeStep = waterTimeStep;
        waterTimerUpdate = 0.0f;

        waterWidth = PRE_CALC_EFFECTS_2D_TEXTURE_SIZE;
        waterHeight = PRE_CALC_EFFECTS_2D_TEXTURE_SIZE;
        waterBufferWidth = waterWidth / 4;
        waterBufferHeight = waterHeight / 4;
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
    *  Initializes the bump3d 2D texture.
    */
    private void initBump3D2DTexture(GL2 gl, int textureUnit)
    {
        if (bump3DEmboss2DTexture == null)
            bump3DEmboss2DTexture = createRandomEmbossTexture(gl, bump3DEmboss2DTexture, textureUnit, PRE_CALC_EFFECTS_2D_TEXTURE_SIZE, PRE_CALC_EFFECTS_2D_TEXTURE_SIZE, PRE_CALC_EFFECTS_2D_TEXTURE_SIZE * PRE_CALC_EFFECTS_2D_TEXTURE_SIZE, false);
    }

    /**
    *  Initializes the Perlin noise 3D texture.
    */
    private void initPerlinNoise3DTexture(GL2 gl, int textureUnit)
    {
        if (perlinNoise3DTexture == null)
        {
            perlinNoise3DTexture = new PerlinNoise3DTexture();
            perlinNoise3DTexture.makePerlinNoise3DTexture();
            perlinNoise3DTexture.initPerlinNoise3DTexture(gl, textureUnit);
        }
    }

    /**
    *  Initializes the glyphbombing 3D texture.
    */
    private void initGlyphbombing3DTexture(GL2 gl, int textureUnit)
    {
        if (glyphbombing3DTexture == null)
        {
            glyphbombing3DTexture = new Glyphbombing3DTexture(gl);
            glyphbombing3DTexture.initGlyphbombing3DTexture(gl, textureUnit);
        }
    }

    /**
    *  Loads and compiles all the shader programs.
    */
    private void loadAndCompileAllShaderPrograms(GL2 gl)
    {
        String versionString = (USE_330_SHADERS_PROCESS) ? MINIMUM_GLSL_VERSION_FOR_330_SHADERS + " " + GLSL_LANGUAGE_MODE : MINIMUM_GLSL_VERSION_FOR_120_SHADERS;
        String GLSLPreprocessorCommands = "#version " + versionString + "\n" +
                                          "#define GPU_SHADER_FP64_COMPATIBILITY_CONDITION "      + ( (USE_GL_ARB_GPU_SHADER_FP64 && GL_IS_NVIDIA) ? 1 : 0 )                                             + "\n" +
                                          "#define GPU_SHADER4_COMPATIBILITY_CONDITION "          + ( USE_GL_EXT_GPU_SHADER4 ? 1 : 0 )                                                                   + "\n" +
                                          "#define ANIMATION_COMPATIBILITY_CONDITION "            + ( (GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS_INTEGER <= MINIMUM_GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS) ? 1 : 0 ) + "\n" +
                                          "#define VS_VARYING "                                   + ( (USE_330_SHADERS_PROCESS) ? "out" : "varying" )                                                    + "\n" +
                                          "#define GS_VARYING "                                   + ( (USE_330_SHADERS_PROCESS) ? ""    : "varying" )                                                    + "\n" +
                                          "#define FS_VARYING "                                   + ( (USE_330_SHADERS_PROCESS) ? "in"  : "varying" )                                                    + "\n" +
                                          "#define VS_POSITION "                                  + "Position"   + "\n" +
                                          "#define VS_MC_POSITION "                               + "MCPosition" + "\n" +
                                          "#define VS_NORMAL "                                    + "Normal"     + "\n" +
                                          "#define VS_SCENE_COLOR "                               + "SceneColor" + "\n" +
                                          "#define VS_TEX_COORD "                                 + "TexCoord"   + "\n" +
                                          "#define VS_V "                                         + "V"          + "\n" +
                                          "#define FS_POSITION "                                  + "Position"   + "\n" +
                                          "#define FS_MC_POSITION "                               + "MCPosition" + "\n" +
                                          "#define FS_NORMAL "                                    + "Normal"     + "\n" +
                                          "#define FS_SCENE_COLOR "                               + "SceneColor" + "\n" +
                                          "#define FS_TEX_COORD "                                 + "TexCoord"   + "\n" +
                                          "#define FS_V "                                         + "V"          + "\n"
                                          ;
        ShaderTypes[] allShaderTypes = ShaderTypes.values();
        String shaderEffectName = "";
        String shaderEffectFileName = "";
        for (int i = 0; i < NUMBER_OF_AVAILABLE_SHADERS; i++)
        {
            shaderEffectFileName = EnumUtils.splitAndCapitalizeFirstCharacters(allShaderTypes[i]);
            shaderEffectName = Character.toLowerCase( shaderEffectFileName.charAt(0) ) + shaderEffectFileName.substring(1);
            ShaderUtils.loadShaderFileCompileAndLinkProgram(gl, new String[]
                    {
                        SHADER_FILES_DIRECTORY_1, SHADER_FILES_DIRECTORY_2, GPU_COMPUTING_DIRECTORY + SHADER_FILES_DIRECTORY_3
                    }, new String[]
                    {
                        shaderEffectFileName, SHADER_FILE_NAME_2, SHADER_FILE_NAME_3
                    },
                    LOAD_SHADER_PROGRAMS_FROM_EXTERNAL_SOURCE, VERTEX_SHADERS, FRAGMENT_SHADERS, SHADER_PROGRAMS, i, GLSLPreprocessorCommands, DEBUG_BUILD);

            // common effects uniform variables
            SHADER_PROGRAM_2D_TEXTURES[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], SHADER_PROGRAM_2D_TEXTURE_NAME);
            SHADER_PROGRAM_EMBOSS_NODE_TEXTURES[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], SHADER_PROGRAM_EMBOSS_NODE_TEXTURE_NAME);
            SHADER_PROGRAM_PXS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], SHADER_PROGRAM_PX_NAME);
            SHADER_PROGRAM_PYS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], SHADER_PROGRAM_PY_NAME);

            // per shader naming uniform variables
            SHADER_PROGRAM_WATER_BUFFER_2D_TEXTURES[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_WATER_BUFFER_2D_TEXTURE_NAME);
            SHADER_PROGRAM_BUMP3D_EMBOSS_2D_TEXTURES[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_BUMP3D_EMBOSS_2D_TEXTURE_NAME);
            SHADER_PROGRAM_PERLIN_NOISE_3D_TEXTURES[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_PERLIN_NOISE_3D_TEXTURE_NAME);
            SHADER_PROGRAM_GLYPHBOMBING_3D_TEXTURES[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_GLYPHBOMBING_3D_TEXTURE_NAME);
            SHADER_PROGRAM_FOGS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_FOG_NAME);
            SHADER_PROGRAM_TEXTURINGS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_TEXTURING_NAME);
            SHADER_PROGRAM_SPHERICAL_MAPPINGS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_SPHERICAL_MAPPING_NAME);
            SHADER_PROGRAM_TIMERS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_TIMER_NAME);
            SHADER_PROGRAM_MORPHINGS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_MORPHING_NAME);
            SHADER_PROGRAM_USER_CLIPPINGS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_USER_CLIPPING_NAME);
            SHADER_PROGRAM_ANTIALIASES[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_ANTIALIAS_NAME);
            SHADER_PROGRAM_STATES[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_STATE_NAME);
            SHADER_PROGRAM_OLD_LCD_STYLE_TRANSPARENCIES[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_OLD_LCD_STYLE_TRANSPARENCY_NAME);
            SHADER_PROGRAM_EROSIONS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_EROSION_NAME);
            SHADER_PROGRAM_SHRINK_TRIANGLES[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_SHRINK_TRIANGLES_NAME);
            SHADER_PROGRAM_SOLID_WIREFRAMES[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_SOLID_WIREFRAME_NAME);
            SHADER_PROGRAM_NORMALS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_NORMALS_NAME);
            SHADER_PROGRAM_PRE_CALC_EFFECTS_2D_TEXTURE_PXS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_PRE_CALC_EFFECTS_2D_TEXTURE_PX_NAME);
            SHADER_PROGRAM_PRE_CALC_EFFECTS_2D_TEXTURE_PYS[i] = gl.glGetUniformLocation(SHADER_PROGRAMS[i], shaderEffectName + SHADER_PROGRAM_PRE_CALC_EFFECTS_2D_TEXTURE_PY_NAME);

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
    *  Processes the water effect.
    */
    private void waterEffect()
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
    *  Processes the timer effect.
    */
    public void timerEffect()
    {
        timerUpdate += timerUpdateStep;
    }

    /**
    *  Processes the timer effect.
    */
    public void timerEffect(boolean updateWaterEffect)
    {
        timerUpdate += timerUpdateStep;

        if (updateWaterEffect)
            waterEffect();
    }

    /**
    *  Resets the timer effect.
    */
    public void resetTimerEffect()
    {
        timerUpdate = 0.0f;
        waterTimerUpdate = 0.0f;
    }

    /**
    *  Uses a particular shader program with given texturing & fog variables.
    */
    private void useProgramAndUniforms(GL2 gl, int effectIndex, boolean useTexturing, boolean useSphericalMapping, boolean useEmbossBioLayoutLogoName, boolean useFog, float morphingValue, boolean useUserClipping, boolean useAntiAlias, boolean state, boolean oldLCDStyleTransparency, boolean erosion, boolean shrinkTriangles, boolean solidWireFrame, boolean normals, float px, float py)
    {
        if (updateWaterBufferImageTexture)
        {
            updateWaterBufferImageTexture = false;
            waterBufferTexture = createTextureFromImage(gl, waterBufferImage, waterBufferTexture, ACTIVE_TEXTURE_UNIT_FOR_WATER_BUFFER_2D_TEXTURE);
        }

        gl.glUseProgram(SHADER_PROGRAMS[effectIndex]);
        gl.glUniform1i(SHADER_PROGRAM_2D_TEXTURES[effectIndex], ACTIVE_TEXTURE_UNIT_FOR_2D_TEXTURE);
        gl.glUniform1i(SHADER_PROGRAM_WATER_BUFFER_2D_TEXTURES[effectIndex], ACTIVE_TEXTURE_UNIT_FOR_WATER_BUFFER_2D_TEXTURE);
        gl.glUniform1i(SHADER_PROGRAM_BUMP3D_EMBOSS_2D_TEXTURES[effectIndex], ACTIVE_TEXTURE_UNIT_FOR_BUMP3D_EMBOSS_2D_TEXTURE);
        gl.glUniform1i(SHADER_PROGRAM_PERLIN_NOISE_3D_TEXTURES[effectIndex], ACTIVE_TEXTURE_UNIT_FOR_PERLIN_NOISE_3D_TEXTURE);
        gl.glUniform1i(SHADER_PROGRAM_GLYPHBOMBING_3D_TEXTURES[effectIndex], ACTIVE_TEXTURE_UNIT_FOR_GLYPHBOMBING_3D_TEXTURE);
        gl.glUniform1i(SHADER_PROGRAM_FOGS[effectIndex], (useFog) ? 1 : 0);
        gl.glUniform1i(SHADER_PROGRAM_TEXTURINGS[effectIndex], (useTexturing) ? 1 : 0);
        gl.glUniform1i(SHADER_PROGRAM_SPHERICAL_MAPPINGS[effectIndex], (useSphericalMapping) ? 1 : 0);
        gl.glUniform1i(SHADER_PROGRAM_EMBOSS_NODE_TEXTURES[effectIndex], (useEmbossBioLayoutLogoName) ? 1 : 0);
        gl.glUniform1f(SHADER_PROGRAM_PXS[effectIndex], px);
        gl.glUniform1f(SHADER_PROGRAM_PYS[effectIndex], py);
        gl.glUniform1f(SHADER_PROGRAM_TIMERS[effectIndex], timerUpdate);
        gl.glUniform1f(SHADER_PROGRAM_MORPHINGS[effectIndex], morphingValue);
        gl.glUniform1i(SHADER_PROGRAM_USER_CLIPPINGS[effectIndex], (useUserClipping) ? 1 : 0);
        gl.glUniform1i(SHADER_PROGRAM_ANTIALIASES[effectIndex], (useAntiAlias) ? 1 : 0);
        gl.glUniform1i(SHADER_PROGRAM_STATES[effectIndex], (state) ? 1 : 0);
        gl.glUniform1i(SHADER_PROGRAM_OLD_LCD_STYLE_TRANSPARENCIES[effectIndex], (oldLCDStyleTransparency) ? 1 : 0);
        gl.glUniform1i(SHADER_PROGRAM_EROSIONS[effectIndex], (erosion) ? 1 : 0);
        gl.glUniform1i(SHADER_PROGRAM_SHRINK_TRIANGLES[effectIndex], (shrinkTriangles) ? 1 : 0);
        gl.glUniform1i(SHADER_PROGRAM_SOLID_WIREFRAMES[effectIndex], (solidWireFrame) ? 1 : 0);
        gl.glUniform1i(SHADER_PROGRAM_NORMALS[effectIndex], (normals) ? 1 : 0);
        gl.glUniform1f(SHADER_PROGRAM_PRE_CALC_EFFECTS_2D_TEXTURE_PXS[effectIndex], PRE_CALC_EFFECTS_2D_TEXTURE_SIZE);
        gl.glUniform1f(SHADER_PROGRAM_PRE_CALC_EFFECTS_2D_TEXTURE_PYS[effectIndex], PRE_CALC_EFFECTS_2D_TEXTURE_SIZE);

        // animation is off
        gl.glUniform1i(SHADER_PROGRAM_ANIMATION_GPU_COMPUTING_MODES[effectIndex], 0);
    }

    /**
    *  Uses a particular shader program with given texturing & fog variables.
    *  Overloaded version for also passing all the animation uniform values.
    */
    private void useProgramAndUniforms(GL2 gl, int effectIndex, boolean useTexturing, boolean useSphericalMapping, boolean useEmbossBioLayoutLogoName, boolean useFog, float morphingValue, boolean useUserClipping, boolean useAntiAlias, boolean state, boolean oldLCDStyleTransparency, boolean erosion, boolean shrinkTriangles, boolean solidWireFrame, boolean normals, float px, float py,
                                              float nodeValue, boolean processNextNodeValue, float nextNodeValue, int animationFrameCount)
    {
        useProgramAndUniforms(gl, effectIndex, useTexturing, useSphericalMapping, useEmbossBioLayoutLogoName, useFog, morphingValue, useUserClipping, useAntiAlias, state, oldLCDStyleTransparency, erosion, shrinkTriangles, solidWireFrame, normals, px, py);

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
    *  Uses the given shader SFX lighting program.
    */
    public void useShaderLightingSFX(GL2 gl, ShaderTypes shaderType, boolean useTexturing, boolean useSphericalMapping, boolean useEmbossBioLayoutLogoName, boolean useFog, float morphingValue, boolean useUserClipping, boolean useAntiAlias, boolean state, boolean oldLCDStyleTransparency, boolean erosion, boolean shrinkTriangles, boolean solidWireFrame, boolean normals, float px, float py)
    {
        useProgramAndUniforms(gl, shaderType.ordinal(), useTexturing, useSphericalMapping, useEmbossBioLayoutLogoName, useFog, morphingValue, useUserClipping, useAntiAlias, state, oldLCDStyleTransparency, erosion, shrinkTriangles, solidWireFrame, normals, px, py);
    }

    /**
    *  Uses the given shader SFX lighting program.
    *  Overloaded version for also passing all the animation uniform values.
    */
    public void useShaderLightingSFX(GL2 gl, ShaderTypes shaderType, boolean useTexturing, boolean useSphericalMapping, boolean useEmbossBioLayoutLogoName, boolean useFog, float morphingValue, boolean useUserClipping, boolean useAntiAlias, boolean state, boolean oldLCDStyleTransparency, boolean erosion, boolean shrinkTriangles, boolean solidWireFrame, boolean normals, float px, float py,
                                            float nodeValue, boolean processNextNodeValue, float nextNodeValue, int animationFrameCount)
    {
        useProgramAndUniforms(gl, shaderType.ordinal(), useTexturing, useSphericalMapping, useEmbossBioLayoutLogoName, useFog, morphingValue, useUserClipping, useAntiAlias, state, oldLCDStyleTransparency, erosion, shrinkTriangles, solidWireFrame, normals, px, py,
                                  nodeValue, processNextNodeValue, nextNodeValue, animationFrameCount);
    }

    /**
    *  Disabled the shader programs.
    */
    public void disableShaders(GL2 gl)
    {
        gl.glUseProgram(0);
    }

    /**
    *  Destroys (de-initializes) all the effect resources.
    */
    public void destructor(GL2 gl)
    {
        for (int i = 0; i < NUMBER_OF_AVAILABLE_SHADERS; i++)
        {
            ShaderUtils.detachAndDeleteShader(gl, VERTEX_SHADERS, FRAGMENT_SHADERS, SHADER_PROGRAMS, i);
        }

        if (glyphbombing3DTexture != null)
        {
            glyphbombing3DTexture.disposeAllGlyphbombing3DTextureResources(gl);
            glyphbombing3DTexture = null;
        }
        if (perlinNoise3DTexture != null)
        {
            perlinNoise3DTexture.disposeAllPerlinNoise3DTextureResources(gl);
            perlinNoise3DTexture = null;
        }
        if (bump3DEmboss2DTexture != null)
        {
            bump3DEmboss2DTexture = null;
        }
        if (waterBufferTexture != null) waterBufferTexture = null;
    }


}
