package org.BioLayoutExpress3D.Environment;

import java.awt.*;
import java.awt.image.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import org.jocl.*;
import org.BioLayoutExpress3D.Environment.Preferences.*;
import org.BioLayoutExpress3D.Graph.Camera.CameraUI.*;
import org.BioLayoutExpress3D.Models.Lathe3D.*;
import org.BioLayoutExpress3D.Models.SuperQuadric.*;
import org.BioLayoutExpress3D.Utils.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import org.BioLayoutExpress3D.Textures.*;
import static java.lang.Math.*;
import org.BioLayoutExpress3D.BuildConfig;
import static org.BioLayoutExpress3D.StaticLibraries.ImageProducer.*;

/**
*
* @author Leon Goldovsky, full refactoring by Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public final class GlobalEnvironment
{
    public static final String TITLE = BuildConfig.NAME;
    public static final String TITLE_VERSION = " Version ";
    public static final String TITLE_VERSION_NUMBER = BuildConfig.VERSION;
    public static final boolean DEBUG_BUILD = BuildConfig.DEBUG;
    public static final String VERSION = TITLE + (TITLE_VERSION_NUMBER.equals("development") ?
            " internal development version" :
            TITLE_VERSION + TITLE_VERSION_NUMBER);
    public static final String BIOLAYOUT_EXPRESS_3D_DOMAIN_URL = BuildConfig.URL;
    public static final String BIOLAYOUT_SERVER_DATASETS_DIRECTORY = "/datasets/";
    public static final String BIOLAYOUT_DATASETS_CONTROL_FILE = "ListOfDataSets.ctr";
    public static final String BIOLAYOUT_APPLICATION_USAGE_URL = BIOLAYOUT_EXPRESS_3D_DOMAIN_URL + "/biolayout_log/index.php";

    public static final Runtime RUNTIME = Runtime.getRuntime();
    public static final boolean IS_WIN = LoadNativeLibrary.isWin();
    public static final boolean IS_LINUX = LoadNativeLibrary.isLinux();
    public static final boolean IS_MAC = LoadNativeLibrary.isMac();
    public static final boolean IS_64BIT = LoadNativeLibrary.is64bit();
    public static final int NUMBER_OF_AVAILABLE_PROCESSORS = RUNTIME.availableProcessors();
    public static final boolean USE_MULTICORE_PROCESS = (NUMBER_OF_AVAILABLE_PROCESSORS > 1);
    public static final char DECIMAL_SEPARATOR_CHARACTER = DecimalFormatSymbols.getInstance().getDecimalSeparator();
    public static final String DECIMAL_SEPARATOR_STRING = Character.valueOf(DECIMAL_SEPARATOR_CHARACTER).toString();

    // OpenGL & GLSL specific variables
    public static final PrefBool FIRST_RUN_DETECT_OPENGL_SUPPORT_AND_EXTENSIONS = new PrefBool(true, "first_run_detect_opengl_support_and_extensions", true);
    public static final boolean LOAD_SHADER_PROGRAMS_FROM_EXTERNAL_SOURCE = false;
    public static final float MINIMUM_OPENGL_VERSION_FOR_VERTEX_ARRAYS = 1.5f;
    public static final float MINIMUM_OPENGL_VERSION_FOR_QUALITY_RENDERING_AND_SHADERS = 3.0f; // could have been 2.1, but using 3.0 for excluding old drivers/hardware (mainly old Intel gfx cards)
    public static final float MINIMUM_OPENGL_VERSION_FOR_330_SHADERS = 3.3f;
    public static final float MINIMUM_OPENGL_VERSION_FOR_400_SHADERS = 4.0f;
    public static final String MINIMUM_GLSL_VERSION_FOR_120_SHADERS = "120";
    public static final String MINIMUM_GLSL_VERSION_FOR_330_SHADERS = "330";
    public static final String MINIMUM_GLSL_VERSION_FOR_400_SHADERS = "400";
    public static final String GLSL_LANGUAGE_MODE = "compatibility";
    public static final int MINIMUM_GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS = 8;
    public static boolean USE_VERTEX_ARRAYS_FOR_OPENGL_RENDERER = true;
    public static boolean USE_SHADERS_PROCESS = !IS_MAC || LoadNativeLibrary.isMacLionAndAbove(); // do not use shader processes for MacOSX before the Lion release due to OpenGL GLSL compilation driver issues
    public static boolean USE_330_SHADERS_PROCESS = false;
    public static boolean USE_GL_ARB_TEXTURE_RECTANGLE = false;
    public static boolean USE_GL_EXT_FRAMEBUFFER_OBJECT = false;
    public static boolean USE_GL_EXT_GPU_SHADER4 = false;
    public static boolean USE_GL_ARB_GPU_SHADER5 = false;
    public static boolean USE_GL_ARB_GPU_SHADER_FP64 = false;
    public static String GL_VENDOR_STRING = "";
    public static String GL_RENDERER_STRING = "";
    public static String GL_VERSION_STRING = "";
    public static boolean GL_IS_AMD_ATI = false;
    public static boolean GL_IS_NVIDIA = false;
    public static String[] GL_EXTENSIONS_STRINGS = null;
    public static String GL_SHADING_LANGUAGE_VERSION_STRING = "";
    public static int GL_MAX_DRAW_BUFFERS_INTEGER = 0;
    public static int GL_MAX_COLOR_ATTACHMENTS_INTEGER = 0;
    public static int GL_AUX_BUFFERS_INTEGER = 0;
    public static int GL_MAX_TEXTURE_UNITS_INTEGER = 0;
    public static int GL_MAX_VERTEX_ATTRIBS_INTEGER = 0;
    public static int GL_MAX_VERTEX_UNIFORM_COMPONENTS_INTEGER = 0;
    public static int GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS_INTEGER = 0;
    public static int GL_MAX_VARYING_FLOATS_INTEGER = 0;
    public static int GL_MAX_TEXTURE_IMAGE_UNITS_INTEGER = 0;
    public static int GL_MAX_TEXTURE_COORDS_INTEGER = 0;
    public static int GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS_INTEGER = 0;
    public static int GL_MAX_FRAGMENT_UNIFORM_COMPONENTS_INTEGER = 0;
    public static int GL_MAX_3D_TEXTURE_SIZE_INTEGER = 0;
    public static int GL_MAX_RECTANGLE_TEXTURE_SIZE_ARB_INTEGER = 0;
    public static int GL_MAX_TEXTURE_SIZE_INTEGER = 0;
    public static int GL_MAX_RENDERBUFFER_SIZE_EXT_INTEGER = 0;

    // OpenCL specific variables
    public static boolean OPENCL_GPU_COMPUTING_ENABLED = false;
    public static final boolean LOAD_KERNEL_PROGRAMS_FROM_EXTERNAL_SOURCE = false;
    public static String[] CL_ALL_PLATFORM_NAMES = null;
    public static boolean[] CL_IS_PLATFORM_AMD_ATI = null;
    public static cl_device_id[][] CL_ALL_PLATFORM_DEVICE_IDS = null;
    public static String[][] CL_ALL_PLATFORM_DEVICES_NAMES = null;
    public static String[][] CL_ALL_PLATFORM_DEVICES_VENDORS = null;
    public static String[][] CL_ALL_PLATFORM_DEVICES_DRIVER_VERSIONS = null;
    public static String[][] CL_ALL_PLATFORM_DEVICES_VERSIONS = null;
    public static String[][] CL_ALL_PLATFORM_DEVICES_OPENCL_C_VERSIONS = null;
    public static String[][] CL_ALL_PLATFORM_DEVICES_PROFILES = null;
    public static String[][][] CL_ALL_PLATFORM_DEVICES_TYPES = null;
    public static int[][] CL_ALL_PLATFORM_DEVICES_ENDIAN_LITTLES = null;
    public static int[][] CL_ALL_PLATFORM_DEVICES_MAX_COMPUTE_UNITS = null;
    public static long[][] CL_ALL_PLATFORM_DEVICES_MAX_WORK_ITEM_DIMENSIONS = null;
    public static int[][][] CL_ALL_PLATFORM_DEVICES_MAX_WORK_ITEM_SIZES = null;
    public static long[][] CL_ALL_PLATFORM_DEVICES_MAX_WORK_GROUP_SIZES = null;
    public static long[][] CL_ALL_PLATFORM_DEVICES_MAX_CLOCK_FREQUENCIES = null;
    public static int[][] CL_ALL_PLATFORM_DEVICES_ADDRESSES_BITS = null;
    public static long[][] CL_ALL_PLATFORM_DEVICES_MAX_MEM_ALLOC_SIZES = null;
    public static long[][] CL_ALL_PLATFORM_DEVICES_GLOBAL_MEM_SIZES = null;
    public static int[][] CL_ALL_PLATFORM_DEVICES_ERROR_CORRECTIONS_SUPPORT = null;
    public static int[][] CL_ALL_PLATFORM_DEVICES_LOCAL_MEM_TYPES = null;
    public static long[][] CL_ALL_PLATFORM_DEVICES_LOCAL_MEM_SIZES = null;
    public static long[][] CL_ALL_PLATFORM_DEVICES_MAX_CONSTANT_BUFFER_SIZES = null;
    public static String[][][] CL_ALL_PLATFORM_DEVICES_QUEUES_PROPERTIES = null;
    public static int[][] CL_ALL_PLATFORM_DEVICES_IMAGES_SUPPORT = null;
    public static int[][] CL_ALL_PLATFORM_DEVICES_CL_DEVICE_MAX_SAMPLERS = null;
    public static int[][] CL_ALL_PLATFORM_DEVICES_MAX_READ_IMAGES_ARGS = null;
    public static int[][] CL_ALL_PLATFORM_DEVICES_MAX_WRITE_IMAGES_ARGS = null;
    public static int[][] CL_ALL_PLATFORM_DEVICES_IMAGE_2D_MAX_WIDTHS = null;
    public static int[][] CL_ALL_PLATFORM_DEVICES_IMAGE_2D_MAX_HEIGHTS = null;
    public static int[][] CL_ALL_PLATFORM_DEVICES_IMAGE_3D_MAX_WIDTHS = null;
    public static int[][] CL_ALL_PLATFORM_DEVICES_IMAGE_3D_MAX_HEIGHTS = null;
    public static int[][] CL_ALL_PLATFORM_DEVICES_IMAGE_3D_MAX_DEPTHS = null;
    public static int[][][] CL_ALL_PLATFORM_DEVICES_PREFERRED_VECTOR_WIDTHS = null;
    public static String[][][] CL_ALL_PLATFORM_DEVICES_SINGLE_FP_CONFIGS = null;
    public static String[][][] CL_ALL_PLATFORM_DEVICES_EXECUTION_CAPABILITIES = null;
    public static String[][][] CL_ALL_PLATFORM_DEVICES_EXTENSIONS = null;

    // mouse cursor variables
    public static final Cursor BIOLAYOUT_NORMAL_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
    public static final Cursor BIOLAYOUT_MOVE_CURSOR = new Cursor(Cursor.MOVE_CURSOR);
    public static final Cursor BIOLAYOUT_WAIT_CURSOR = new Cursor(Cursor.WAIT_CURSOR);

    public static boolean BIOLAYOUT_USE_STATIC_COLOR = !LoadNativeLibrary.isLinux();
    public static final Color BIOLAYOUT_MENUBAR_AND_TOOLBAR_COLOR = new Color(196, 217, 249); // new Color(192, 192, 192);

    // splash screen loading timings
    public static final int MAX_TIME_IN_MSECS_TO_SLEEP_FOR_LOADING = 100;
    public static final int MAX_FILE_HISTORY = 10;

    // file input/output variables
    public static enum SupportedInputFileTypes { BLAST, OWL, EXPRESSION, GRAPHML, MEPN, LAYOUT, SIF, TGF, TXT, MATRIX, XML, GML }
    public static enum SupportedOutputFileTypes { LAYOUT, TGF }
    public static enum SupportedImportExportFileTypes { CLASSSETS, TXT }
    public static enum SupportedSimulationFileTypes { SPN, TXT }
    public static enum LicensesFiles { LICENSE_BLE3D, LICENSE_GPLV3, LICENSE_JOGL, LICENSE_JOCL }
    public static final String LICENSES_FILES_PATH = "/Resources/Licenses/";
    public static final String LICENSES_SEPARATOR = "\n\n\n\n\n";
    public static final String[] LICENSES_FILES_NAMES = { "LICENSE-BLE3D.txt",
                                                          "LICENSE-GPLv3.txt",
                                                          "LICENSE-JOGL.txt",
                                                          "LICENSE-JOCL.txt"
                                                        };
    public static final String IMAGE_FILES_PATH = "/Resources/Images/";
    public static final String MODEL_FILES_PATH = "/Resources/Models/";
    public static final String SCREENSHOTS_DIRECTORY = "Screenshots/";
    public static boolean INSTALL_DIR_FOR_SCREENSHOTS_HAS_CHANGED = false;

    public static final PrefInt MAX_HTML_TIPS = new PrefInt(5, "max_html_tips", false);
    public static final PrefString FILE_CHOOSER_PATH = new PrefString("", "file_chooser_path", true);

    public static enum DataTypes { BLAST, OWL, EXPRESSION, GRAPHML, LAYOUT, MATRIX, ONDEX, GML, NONE };
    public static DataTypes DATA_TYPE = DataTypes.NONE;
    public static final PrefBool RENDERER_MODE_START_3D = new PrefBool(true, "renderer_mode_start_3d", true);
    public static boolean RENDERER_MODE_3D = false;
    public static boolean IS_RENDERER_MODE_FIRST_SWITCH = true;
    public static final Dimension SCREEN_DIMENSION = new Dimension( Toolkit.getDefaultToolkit().getScreenSize() );
    public static final Dimension APPLICATION_SCREEN_DIMENSION = new Dimension( (int)( 0.9 * SCREEN_DIMENSION.width ), (int)( 0.9 * SCREEN_DIMENSION.height ) );
    public static final BufferedImage BIOLAYOUT_ICON_IMAGE = loadImageFromURL( GlobalEnvironment.class.getResource(IMAGE_FILES_PATH + "BioLayoutExpress3DIcon.png") );
    public static final ImageIcon BIOLAYOUT_MENU_ITEM_ICON = new ImageIcon( resizeImageByGivenRatio(BIOLAYOUT_ICON_IMAGE, 0.125f, true) );
    public static final float MENUBAR_IMAGE_ICON_RESIZE_RATIO = 0.4f;
    public static final boolean USE_NEW_DESKTOP_PRINTING_FEATURE = LoadNativeLibrary.isWinVista() || LoadNativeLibrary.isWin7();
    public static final PrefInt PRINT_COPIES = new PrefInt(0, "print_copies", false);

    public static boolean TEMPORARILY_DISABLE_ALL_GRAPH_RENDERING = false;
    public static final PrefColor BACKGROUND_COLOR = new PrefColor(Color.WHITE, "background_color", true);
    public static final PrefColor SELECTION_COLOR = new PrefColor(Color.GREEN, "selection_color", true);
    public static final PrefColor PLOT_BACKGROUND_COLOR = new PrefColor(new Color(255, 255, 255), "plot_background_color", true);
    public static final PrefColor PLOT_GRIDLINES_COLOR = new PrefColor(new Color(204, 204, 204), "plot_gridlines_color", true);
    public static final PrefBool TRIPPY_BACKGROUND = new PrefBool(false, "trippy_background", true);
    public static final PrefBool DIRECTIONAL_EDGES = new PrefBool(false, "directional_edges", true);
    public static final PrefBool SHOW_EDGES_WHEN_DRAGGING_NODES = new PrefBool(false, "show_edges_when_dragging_nodes", true);
    public static final PrefBool YED_STYLE_RENDERING_FOR_GPAPHML_FILES = new PrefBool(true, "yed_style_rendering_for_graphml_files", true);
    public static final PrefBool YED_STYLE_COMPONENT_CONTAINERS_RENDERING_FOR_GPAPHML_FILES = new PrefBool(true, "yed_style_component_containers_rendering_for_graphml_files", true);
    public static final PrefBool HIGH_QUALITY_ANTIALIASING = new PrefBool(false, "high_quality_antialiasing", true);
    public static final PrefBool NORMAL_QUALITY_ANTIALIASING = new PrefBool(false, "normal_quality_antialiasing", true);
    public static final PrefBool USE_VSYNCH = new PrefBool(true, "use_vsynch", true);
    public static final PrefBool DISABLE_NODES_RENDERING = new PrefBool(false, "disable_nodes_rendering", true);
    public static final PrefBool DISABLE_EDGES_RENDERING = new PrefBool(false, "disable_edges_rendering", true);
    public static final PrefBool SHOW_NAVIGATION_WIZARD_ON_STARTUP = new PrefBool(true, "show_navigation_wizard_on_startup", true);
    public static final PrefBool SHOW_LAYOUT_ITERATIONS = new PrefBool(false, "show_layout_iterations", true);
    public static final PrefBool VALIDATE_XML_FILES = new PrefBool(false, "validate_xml_files", true);
    public static final PrefBool USE_INSTALL_DIR_FOR_SCREENSHOTS = new PrefBool(false, "use_install_dir_for_screenshots", true);
    public static final PrefBool USE_INSTALL_DIR_FOR_MCL_TEMP_FILE = new PrefBool(false, "use_install_dir_for_mcl_temp_file", true);
    public static final PrefBool SHOW_GRAPH_PROPERTIES_TOOLBAR = new PrefBool(true, "show_graph_properties_toolbar", true);
    public static final PrefBool SHOW_NAVIGATION_TOOLBAR = new PrefBool(true, "show_navigation_toolbar", true);
    public static final PrefBool SHOW_POPUP_OVERLAY_PLOT = new PrefBool(true, "show_popup_overlay_plot", true);
    public static final PrefBool COLLAPSE_NODES_BY_VOLUME = new PrefBool(false, "collapse_nodes_by_volume", true);
    public static final PrefBool CONFIRM_PREFERENCES_SAVE  = new PrefBool(true, "confirm_preferences_save", true);

    public static enum GraphLayoutAlgorithm { FRUCHTERMAN_RHEINGOLD, FMMM, CIRCLE, ALWAYS_ASK }
    public static final PrefEnum<GraphLayoutAlgorithm> GRAPH_LAYOUT_ALGORITHM = new PrefEnum<GraphLayoutAlgorithm>(
            GraphLayoutAlgorithm.class, GraphLayoutAlgorithm.FRUCHTERMAN_RHEINGOLD, "graph_layout_algorithm", true);

    public static final boolean RANDOM_INITIAL_LAYOUT_COORDS = true;
    public static final double REFERENCE_K_VALUE = 30.0;
    public static final PrefBool TILED_LAYOUT = new PrefBool(true, "tiled_layout", true);
    public static final PrefBool USE_EDGE_WEIGHTS_FOR_LAYOUT = new PrefBool(true, "use_edge_weights_for_layout", true);
    public static final PrefFloat STARTING_TEMPERATURE = new PrefFloat(100.0f, "starting_temperature", true);
    public static final PrefInt NUMBER_OF_LAYOUT_ITERATIONS = new PrefInt(100, "number_of_layout_iterations", true);
    public static final PrefFloat KVALUE_MODIFIER = new PrefFloat(1.0f, "kvalue_modifier", true);
    public static final PrefInt BURST_LAYOUT_ITERATIONS = new PrefInt(20, "burst_layout_iterations", true);
    public static final PrefInt MINIMUM_COMPONENT_SIZE = new PrefInt(1, "minimum_component_size", true);

    public static final PrefFloat FMMM_DESIRED_EDGE_LENGTH = new PrefFloat(20.0f, "fmmm_desired_edge_length", true);
    public static enum FmmmQualityVsSpeed
    {
        VERY_HIGH_QUALITY_VERY_LOW_SPEED,
        HIGH_QUALITY_LOW_SPEED,
        MEDIUM_QUALITY_MEDIUM_SPEED,
        LOW_QUALITY_HIGH_SPEED
    }
    public static final PrefEnum<FmmmQualityVsSpeed> FMMM_QUALITY_VS_SPEED = new PrefEnum<FmmmQualityVsSpeed>(
            FmmmQualityVsSpeed.class, FmmmQualityVsSpeed.MEDIUM_QUALITY_MEDIUM_SPEED, "fmmm_quality_vs_speed", true);
    public static enum FmmmForceModel { EADES, FRUCHTERMAN_RHEINGOLD/*, NMM*/ }
    public static final PrefEnum<FmmmForceModel> FMMM_FORCE_MODEL = new PrefEnum<FmmmForceModel>(
            FmmmForceModel.class, FmmmForceModel.FRUCHTERMAN_RHEINGOLD, "fmmm_force_model", true);
    public static enum FmmmStopCriterion { FORCE_THRESHOLD_AND_FIXED_ITERATIONS, FIXED_ITERATIONS, FORCE_THRESHOLD }
    public static final PrefEnum<FmmmStopCriterion> FMMM_STOP_CRITERION = new PrefEnum<FmmmStopCriterion>(
            FmmmStopCriterion.class, FmmmStopCriterion.FORCE_THRESHOLD_AND_FIXED_ITERATIONS, "fmmm_stop_criterion", true);
    public static final PrefInt FMMM_ITERATION_LEVEL_FACTOR = new PrefInt(10, "fmmm_iteration_level_factor", true);

    public static final String DEFAULT_SURFACE_IMAGE_FILES_PATH = IMAGE_FILES_PATH + "SurfaceImages/";
    private static final String DEFAULT_SURFACE_IMAGE_FILE_NAME = "SurfaceImagesData.txt";
    public static final String[] DEFAULT_SURFACE_IMAGE_FILES = { "BioLayoutExpress3DLogo",
                                                                 "JupiterSurfaceImage1",
                                                                 "JupiterSurfaceImage2",
                                                                 "JupiterSurfaceImage3",
                                                                 "SaturnSurfaceImage1",
                                                                 "SaturnSurfaceImage2"
                                                               };
    public static final TexturesLoader DEFAULT_SURFACE_IMAGES; // initialized in the static initializer of this class
    private static final String BIOLAYOUT_EXDPRESS_3D_LOGO_NAME = IMAGE_FILES_PATH + DEFAULT_SURFACE_IMAGE_FILES[0] + ".png";
    public static final BufferedImage BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE = loadImageFromURL( GlobalEnvironment.class.getResource(BIOLAYOUT_EXDPRESS_3D_LOGO_NAME) );
    public static String USER_SURFACE_IMAGE_FILE = "";
    public static final PrefInt TEXTURE_CHOSEN = new PrefInt(0, "texture_chosen", true);
    public static final PrefBool TEXTURE_ENABLED = new PrefBool(false, "texture_enabled", true);
    public static boolean CHANGE_TEXTURE_ENABLED = false;
    public static boolean CHANGE_SPHERICAL_MAPPING_ENABLED = false;
    public static boolean CHANGE_ALL_SHAPES = false;
    public static boolean CHANGE_ALL_FAST_SELECTION_SHAPES = false;
    public static boolean CHANGE_NODE_TESSELATION = false;
    public static boolean CHANGE_GRAPHML_COMPONENT_CONTAINERS = false;
    public static enum Shapes2D { CIRCLE, RECTANGLE, ROUND_RECTANGLE, TRIANGLE, DIAMOND, PARALLELOGRAM, HEXAGON, OCTAGON, TRAPEZOID1, TRAPEZOID2, RECTANGLE_VERTICAL, RECTANGLE_HORIZONTAL }
    public static final int NUMBER_OF_2D_SHAPES = Shapes2D.values().length;
    public static enum Shapes3D { SPHERE, POINT, CUBE, TETRAHEDRON, OCTAHEDRON, DODECAHEDRON, ICOSAHEDRON, CONE_LEFT, CONE_RIGHT, TRAPEZOID_UP, TRAPEZOID_DOWN, CYLINDER, TORUS, RECTANGLE_VERTICAL, RECTANGLE_HORIZONTAL, ROUND_CUBE_LARGE, ROUND_CUBE_THIN, GENE_MODEL, PINEAPPLE_SLICE_TOROID, PINEAPPLE_SLICE_ELLIPSOID, DOUBLE_PYRAMID_THIN, DOUBLE_PYRAMID_LARGE, TORUS_8_PETALS, SAUCER_4_PETALS, LATHE_3D, SUPER_QUADRIC, OBJ_MODEL_LOADER, DUMB_BELL }
    public static final int NUMBER_OF_3D_SHAPES = Shapes3D.values().length;
    public static boolean MANUAL_SHAPE_2D = false;
    public static boolean MANUAL_SHAPE_3D = false;
    public static final PrefBool TRANSPARENT = new PrefBool(true, "transparent", true);
    public static final PrefFloat TRANSPARENT_ALPHA = new PrefFloat(0.5f, "transparent_alpha", true);
    public static final PrefInt NODE_TESSELATION = new PrefInt(20, "node_tesselation", true);
    public static final int NODE_TESSELATION_MIN_VALUE = 3;
    public static final int NODE_TESSELATION_MAX_VALUE = 30;
    public static final int NODE_TESSELATION_EXTRA_KEYS_VALUE_THRESHOLD = 20;
    public static final PrefBool SHOW_NODES = new PrefBool(true, "show_nodes", true);
    public static final PrefBool SHOW_3D_FRUSTUM = new PrefBool(false, "show_3d_frustum", true);
    public static final PrefBool SHOW_3D_SHADOWS = new PrefBool(false, "show_3d_shadows", true);
    public static final PrefBool SHOW_3D_ENVIRONMENT_MAPPING = new PrefBool(false, "show_3d_environment_mapping", true);
    public static final PrefInt TILE_SCREEN_FACTOR = new PrefInt(5, "tile_screen_factor", true);
    public static final PrefBool FAST_SELECTION_MODE = new PrefBool(true, "fast_selection_mode", true);
    public static final PrefBool WIREFRAME_SELECTION_MODE = new PrefBool(true, "wireframe_selection_mode", true);
    public static final PrefBool ADVANCED_KEYBOARD_RENDERING_CONTROL = new PrefBool(true, "advanced_keyboard_rendering_control", true);
    public static final PrefBool ANAGLYPH_STEREOSCOPIC_3D_VIEW = new PrefBool(false, "anaglyph_stereoscopic_3d_view", true);
    public static GraphAnaglyphGlassesTypes GRAPH_ANAGLYPH_GLASSES_TYPE = GraphAnaglyphGlassesTypes.RED_BLUE;
    public static final PrefString ANAGLYPH_GLASSES_TYPE = new PrefString(GraphAnaglyphGlassesTypes.RED_BLUE.toString(), "anaglyph_glasses_type", true);
    public static GraphIntraOcularDistanceTypes GRAPH_INTRA_OCULAR_DISTANCE_TYPE = GraphIntraOcularDistanceTypes._0_001;
    public static final PrefString INTRA_OCULAR_DISTANCE_TYPE = new PrefString(GraphIntraOcularDistanceTypes._0_001.toString(), "intra_ocular_distance", true);
    public static final PrefFloat[] LIGHT_POSITION = { new PrefFloat(10.0f, "light_position_x", true), new PrefFloat(10.0f, "light_position_y", true), new PrefFloat(5.0f, "light_position_z", true) };
    public static final PrefBool DEPTH_FOG = new PrefBool(true, "depth_fog", true);
    public static final PrefBool USE_MOTION_BLUR_FOR_SCENE = new PrefBool(false, "use_motion_blur_for_scene", true);
    public static final PrefFloat MOTION_BLUR_SIZE = new PrefFloat(0.6f, "motion_blur_size", true);
    public static final PrefBool MATERIAL_SPECULAR = new PrefBool(true, "material_specular", true);
    public static final PrefFloat MATERIAL_SPECULAR_SHINE = new PrefFloat(25.0f, "material_specular_shine", true);
    public static final PrefBool MATERIAL_SMOOTH_SHADING = new PrefBool(true, "material_smooth_shading", true);
    public static final PrefBool MATERIAL_SPHERICAL_MAPPING = new PrefBool(true, "material_spherical_mapping_shading", true);
    public static final PrefBool MATERIAL_EMBOSS_NODE_TEXTURE = new PrefBool(false, "material_emboss_node_texture_shading", true);
    public static final PrefBool MATERIAL_ANTIALIAS_SHADING = new PrefBool(true, "material_antialias_shading", true);
    public static final PrefBool MATERIAL_ANIMATED_SHADING = new PrefBool(true, "material_animated_shading", true);
    public static final PrefBool MATERIAL_STATE_SHADING = new PrefBool(true, "material_state_shading", true);
    public static final PrefBool MATERIAL_OLD_LCD_STYLE_TRANSPARENCY_SHADING = new PrefBool(false, "material_old_lcd_style_transparency_shading", true);
    public static final PrefBool MATERIAL_EROSION_SHADING = new PrefBool(false, "material_erosion_shading", true);
    public static final PrefBool MATERIAL_NORMALS_SELECTION_MODE = new PrefBool(false, "material_normals_selection_mode", true);
    public static final PrefBool[] ALL_SHADING_SFXS = new PrefBool[ShaderLightingSFXs.ShaderTypes.values().length];

    // Model Shape Editor variables
    public static enum Lathe3DShapes { CYLINDER, CONE, TRAPEZOID, EGG, DRIP, CUP, LIMB, ROUND_R, OVAL_R, FLOWER, CHESS_PIECE, BRANCH, TORUS, DUMB_BELL, DOME, ARMOUR, SAUCER }
    public static enum SuperQuadricShapes { SPHERE, CYLINDER, STAR, DOUBLE_PYRAMID, TORUS, PINEAPPLE_SLICE, PILLOW, SQUARE_TORUS, PINCHED_TORUS, ROUND_CUBE, HYPERBOLOID_ONE_SHEET, HYPERBOLOID_TWO_SHEETS }
    public static enum OBJModelShapes { PIG, COW, CALF, HORSE, DINO, GENE, LEAVES, APHRODITE, HELICOPTER, PENGUIN }
    public static final float[] OBJ_MODEL_SHAPE_SIZES  = { 3.0f, 3.0f, 2.5f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f, 2.8f, 2.0f };
    public static final String DEFAULT_OBJ_MODEL_SHAPE_NAME = "RoundCube";
    public static final float DEFAULT_OBJ_MODEL_SHAPE_SIZE = 2.0f;
    public static final PrefBool MODEL_SHAPE_EDITOR_AUTOROTATE_VIEW = new PrefBool(true, "model_shape_editor_autorotate_view", true);

    private static final Lathe3DShapes LATHE3D_PRESET_SHAPE = Lathe3DShapes.CYLINDER;
    public static final Lathe3DSettings LATHE3D_SETTINGS = Lathe3DShapesProducer.createLathe3DSettings( LATHE3D_PRESET_SHAPE, NODE_TESSELATION.get() );
    public static final PrefString LATHE3D_CHOSEN_PRESET_SHAPE = new PrefString(LATHE3D_PRESET_SHAPE.toString(), "lathe3D_chosen_preset_shape", true);
    public static final PrefString LATHE3D_SETTINGS_XSIN = new PrefString(LayoutPreferences.createPreferenceFloatArrayString(LATHE3D_SETTINGS.xsIn), "lathe3D_settings_xsin", true);
    public static final PrefString LATHE3D_SETTINGS_YSIN = new PrefString(LayoutPreferences.createPreferenceFloatArrayString(LATHE3D_SETTINGS.ysIn), "lathe3D_settings_ysin", true);
    public static final PrefInt LATHE3D_SETTINGS_SPLINE_STEP = new PrefInt(LATHE3D_SETTINGS.splineStep, "lathe3D_settings_spline_step", true);
    public static final PrefFloat LATHE3D_SETTINGS_K = new PrefFloat(LATHE3D_SETTINGS.k, "lathe3D_settings_k", true);
    public static final PrefInt LATHE3D_SETTINGS_LATHE3D_SHAPE_TYPE = new PrefInt(LATHE3D_SETTINGS.lathe3DShapeType.ordinal(), "lathe3D_settings_lathe3d_shape_type", true);
    public static final PrefFloat LATHE3D_SCALE_X = new PrefFloat(0.0f, "lathe3D_scale_x", true);
    public static final PrefFloat LATHE3D_SCALE_Y = new PrefFloat(0.0f, "lathe3D_scale_y", true);
    public static final PrefFloat LATHE3D_SCALE_Z = new PrefFloat(0.0f, "lathe3D_scale_z", true);
    public static final PrefFloat LATHE3D_ROTATE_X = new PrefFloat(0.0f, "lathe3D_rotate_x", true);
    public static final PrefFloat LATHE3D_ROTATE_Y = new PrefFloat(0.0f, "lathe3D_rotate_y", true);
    public static final PrefFloat LATHE3D_ROTATE_Z = new PrefFloat(0.0f, "lathe3D_rotate_z", true);

    private static final SuperQuadricShapes SUPER_QUADRIC_PRESET_SHAPE = SuperQuadricShapes.SPHERE;
    public static final SuperQuadricSettings SUPER_QUADRIC_SETTINGS = SuperQuadricShapesProducer.createSuperQuadricSettings( SUPER_QUADRIC_PRESET_SHAPE, NODE_TESSELATION.get(), NODE_TESSELATION.get() );
    public static final PrefString SUPER_QUADRIC_CHOSEN_PRESET_SHAPE = new PrefString(SUPER_QUADRIC_PRESET_SHAPE.toString(), "super_quadric_chosen_preset_shape", true);
    public static final PrefFloat SUPER_QUADRIC_SETTINGS_E = new PrefFloat(SUPER_QUADRIC_SETTINGS.e, "super_quadric_settings_e", true);
    public static final PrefFloat SUPER_QUADRIC_SETTINGS_N = new PrefFloat(SUPER_QUADRIC_SETTINGS.n, "super_quadric_settings_n", true);
    public static final PrefFloat SUPER_QUADRIC_SETTINGS_V1 = new PrefFloat(SUPER_QUADRIC_SETTINGS.v1, "super_quadric_settings_v1", true);
    public static final PrefFloat SUPER_QUADRIC_SETTINGS_ALPHA = new PrefFloat(SUPER_QUADRIC_SETTINGS.alpha, "super_quadric_settings_alpha", true);
    public static final PrefInt SUPER_QUADRIC_SETTINGS_SUPER_QUADRIC_SHAPE_TYPE = new PrefInt(SUPER_QUADRIC_SETTINGS.superQuadricShapeType.ordinal(), "super_quadric_settings_super_quadric_shape_type", true);
    public static final PrefFloat SUPER_QUADRIC_SCALE_X = new PrefFloat(0.0f, "super_quadric_scale_x", true);
    public static final PrefFloat SUPER_QUADRIC_SCALE_Y = new PrefFloat(0.0f, "super_quadric_scale_y", true);
    public static final PrefFloat SUPER_QUADRIC_SCALE_Z = new PrefFloat(0.0f, "super_quadric_scale_z", true);
    public static final PrefFloat SUPER_QUADRIC_ROTATE_X = new PrefFloat(0.0f, "super_quadric_rotate_x", true);
    public static final PrefFloat SUPER_QUADRIC_ROTATE_Y = new PrefFloat(0.0f, "super_quadric_rotate_y", true);
    public static final PrefFloat SUPER_QUADRIC_ROTATE_Z = new PrefFloat(0.0f, "super_quadric_rotate_z", true);

    private static final OBJModelShapes OBJ_MODEL_LOADER_PRESET_SHAPE = OBJModelShapes.PIG;
    public static final PrefString OBJ_MODEL_LOADER_CHOSEN_PRESET_SHAPE = new PrefString(OBJ_MODEL_LOADER_PRESET_SHAPE.toString(), "obj_model_loader_chosen_preset_shape", true);
    public static final PrefFloat OBJ_MODEL_LOADER_SHAPE_SIZE = new PrefFloat(OBJ_MODEL_SHAPE_SIZES[OBJ_MODEL_LOADER_PRESET_SHAPE.ordinal()], "obj_model_loader_shape_size", true);
    public static boolean USE_EXTERNAL_OBJ_MODEL_FILE = false;
    public static String EXTERNAL_OBJ_MODEL_FILE_PATH = "";
    public static String EXTERNAL_OBJ_MODEL_FILE_NAME = "";
    public static final PrefFloat OBJ_MODEL_LOADER_SCALE_X = new PrefFloat(0.0f, "obj_model_loader_scale_x", true);
    public static final PrefFloat OBJ_MODEL_LOADER_SCALE_Y = new PrefFloat(0.0f, "obj_model_loader_scale_y", true);
    public static final PrefFloat OBJ_MODEL_LOADER_SCALE_Z = new PrefFloat(0.0f, "obj_model_loader_scale_z", true);
    public static final PrefFloat OBJ_MODEL_LOADER_ROTATE_X = new PrefFloat(0.0f, "obj_model_loader_rotate_x", true);
    public static final PrefFloat OBJ_MODEL_LOADER_ROTATE_Y = new PrefFloat(0.0f, "obj_model_loader_rotate_y", true);
    public static final PrefFloat OBJ_MODEL_LOADER_ROTATE_Z = new PrefFloat(0.0f, "obj_model_loader_rotate_z", true);

    public static final PrefFloat MCL_INFLATION_VALUE = new PrefFloat(2.2f, "mcl_inflation_value", true);
    public static final PrefFloat MCL_PRE_INFLATION_VALUE = new PrefFloat(3.0f, "mcl_pre_inflation_value", true);
    public static final PrefInt MCL_SCHEME = new PrefInt(6, "mcl_scheme", true);
    public static final PrefInt MCL_SMALLEST_CLUSTER = new PrefInt(3, "mcl_smallest_cluster", true);
    public static final PrefBool MCL_ASSIGN_RANDOM_CLUSTER_COLOURS = new PrefBool(false, "mcl_assign_random_cluster_colours", true);
    public static final PrefString MCL_ADVANCED_OPTIONS = new PrefString("", "mcl_advanced_options", true);

    public static enum SPNDistributionTypes { UNIFORM, STANDARD_NORMAL, DETERMINISTIC_PROCESS }
    public static final SPNDistributionTypes SPN_DEFAULT_DISTRIBUTION_TYPE = SPNDistributionTypes.UNIFORM;
    public static final PrefString USE_SPN_DISTRIBUTION_TYPE = new PrefString(SPN_DEFAULT_DISTRIBUTION_TYPE.toString(), "use_spn_distribution_type", true);
    public static enum SPNTransitionTypes { CONSUMPTIVE, ORIGINAL }
    public static final SPNTransitionTypes SPN_DEFAULT_TRANSITION_TYPE = SPNTransitionTypes.CONSUMPTIVE;
    public static final PrefString USE_SPN_TRANSITION_TYPE = new PrefString(SPN_DEFAULT_TRANSITION_TYPE.toString(), "use_spn_transition_type", true);
    public static final PrefBool SAVE_SPN_RESULTS = new PrefBool(false, "save_spn_results", true);
    public static final PrefString SAVE_SPN_RESULTS_FILE_NAME = new PrefString("", "save_spn_results_file_name", true);
    public static final PrefBool AUTOMATICALLY_SAVE_SPN_RESULTS_TO_PRECHOSEN_FOLDER = new PrefBool(false, "automatically_save_spn_results_to_prechosen_folder", true);
    public static final PrefBool USE_SPN_ANIMATED_TRANSITIONS_SHADING = new PrefBool(false, "use_spn_animated_transitions_shading", true);

    public static final int[] OPENCL_GPU_COMPUTING_ITERATION_SIZES = { 1 << 20, 1 << 21, 1 << 22, 1 << 23, 1 << 24, 1 << 25, 1 << 26, 1 << 27 };
    public static final int OPENCL_DEFAULT_EXPRESSION_CORRELATION_ITERATION_SIZE = OPENCL_GPU_COMPUTING_ITERATION_SIZES[2];
    public static final int OPENCL_DEFAULT_LAYOUT_ITERATION_SIZE = OPENCL_GPU_COMPUTING_ITERATION_SIZES[0];
    public static final int GLSL_MAX_TEXTURE_SIZE = 4096;
    public static final int GLSL_TEXTURE_STEP = 512;
    public static final int GLSL_DEFAULT_TEXTURE_SIZE = GLSL_MAX_TEXTURE_SIZE - GLSL_TEXTURE_STEP;
    public static enum GLSLTextureTypes { TEXTURE_RECTANGLE_ARB_R_32, TEXTURE_2D_ARB_R_32, TEXTURE_RECTANGLE_ARB_RGBA_32, TEXTURE_2D_ARB_RGBA_32 }
    public static final GLSLTextureTypes GLSL_DEFAULT_TEXTURE_TYPE = GLSLTextureTypes.TEXTURE_RECTANGLE_ARB_R_32;
    public static final String SINGLE_CORE_STRING = "Single-Core";
    public static final String N_CP_STRING = "N-CP";
    public static final String JAVA_STRING = "Java";
    public static final String ANSI_C_STRING = "ANSI-C99";
    public static final String[] GPU_COMPUTING_CPU_COMPARISON_METHODS = { SINGLE_CORE_STRING + "-" + JAVA_STRING,
                                                                          SINGLE_CORE_STRING + "-" + ANSI_C_STRING,
                                                                          N_CP_STRING + "-" + JAVA_STRING,
                                                                          N_CP_STRING + "-" + ANSI_C_STRING };
    public static final String GPU_COMPUTING_DEFAULT_CPU_COMPARISON_METHOD = GPU_COMPUTING_CPU_COMPARISON_METHODS[0];
    public static final double EXPRESSION_DATA_GPU_COMPUTING_MAX_ERROR_THRESHOLD = 0.0001; // 1e-4f precision up to 3 decimals
    public static final double LAYOUT_GPU_COMPUTING_MAX_ERROR_THRESHOLD = 1.0; // 1e-0f precision up to 1.0 (1 integer pixel)

    public static final PrefBool USE_EXRESSION_CORRELATION_CALCULATION_N_CORE_PARALLELISM = new PrefBool(true, "use_expression_correlation_calculation_n_core_parallelism", true);
    public static final PrefBool USE_OPENCL_GPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION = new PrefBool(false, "use_opencl_gpu_computing_expression_correlation_calculation", true);
    public static final PrefInt OPENCL_GPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION_ITERATION_SIZE = new PrefInt(OPENCL_DEFAULT_EXPRESSION_CORRELATION_ITERATION_SIZE, "opencl_gpu_computing_expression_correlation_iteration_size", true);
    public static final PrefBool USE_GLSL_GPGPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION = new PrefBool(false, "use_glsl_gpgpu_computing_expression_correlation_calculation", true);
    public static final PrefInt GLSL_GPGPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION_TEXTURE_SIZE = new PrefInt(GLSL_DEFAULT_TEXTURE_SIZE, "glsl_gpgpu_computing_expression_correlation_calculation_texture_size", true);
    public static final PrefString GLSL_GPGPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION_TEXTURE_TYPE = new PrefString(GLSL_DEFAULT_TEXTURE_TYPE.toString(), "glsl_gpgpu_computing_expression_correlation_calculation_texture_type", true);
    public static final PrefBool COMPARE_GPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION_WITH_CPU = new PrefBool(false, "compare_gpu_computing_expression_correlation_calculation_with_cpu", true);
    public static final PrefString COMPARE_GPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION_WITH_CPU_DEFAULT_COMPARISON_METHOD = new PrefString(GPU_COMPUTING_DEFAULT_CPU_COMPARISON_METHOD, "compare_gpu_computing_expression_correlation_calculation_with_cpu_default_comparison_method", true);
    public static final PrefBool USE_LAYOUT_N_CORE_PARALLELISM = new PrefBool(true, "use_layout_n_core_parallelism", true);
    public static final PrefBool USE_OPENCL_GPU_COMPUTING_LAYOUT_CALCULATION = new PrefBool(false, "use_opencl_gpu_computing_layout_calculation", true);
    public static final PrefBool USE_INDICES_1D_KERNEL_WITH_ITERATIONS_FOR_OPENCL_GPU_COMPUTING_LAYOUT_CALCULATION = new PrefBool(false, "use_indices_1d_kernel_with_iterations_for_opencl_gpu_computing_layout_calculation", true);
    public static final PrefInt OPENCL_GPU_COMPUTING_LAYOUT_CALCULATION_ITERATION_SIZE = new PrefInt(OPENCL_DEFAULT_LAYOUT_ITERATION_SIZE, "opencl_gpu_computing_layout_calculation_iteration_size", true);
    public static final PrefBool COMPARE_GPU_COMPUTING_LAYOUT_CALCULATION_WITH_CPU = new PrefBool(false, "compare_gpu_computing_layout_calculation_with_cpu", true);
    public static final PrefString COMPARE_GPU_COMPUTING_LAYOUT_CALCULATION_WITH_CPU_DEFAULT_COMPARISON_METHOD = new PrefString(GPU_COMPUTING_DEFAULT_CPU_COMPARISON_METHOD, "compare_gpu_computing_layout_calculation_with_cpu_default_comparison_method", true);
    public static final PrefBool USE_ATOMIC_SYNCHRONIZATION_FOR_LAYOUT_N_CORE_PARALLELISM = new PrefBool(true, "use_atomic_synchronization_for_layout_n_core_parallelism", true);
    public static final PrefBool USE_MCL_N_CORE_PARALLELISM = new PrefBool(true, "use_mcl_n_core_parallelism", true);
    public static final PrefBool USE_SPN_N_CORE_PARALLELISM = new PrefBool(true, "use_spn_n_core_parallelism", true);

    private static final SearchURL GOOGLE_URL = new SearchURL("http://www.google.com/search?hl=en&ie=ISO-8859-1&q=", "google", "search for words at Google");
    private static final SearchURL SWISS_PROT_URL = new SearchURL("http://www.expasy.org/cgi-bin/niceprot.pl?", "swissprot", "search for proteins at SWISSPROT");
    private static final SearchURL NCBI_URL = new SearchURL("http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=search&db=protein&doptcmdl=GenPept&term=", "NCBI", "Seach proteins at NCBI");
    private static final SearchURL ECOCYC_PROT_URL = new SearchURL("http://BioCyc.org/ECOLI/substring-search?type=ENZYME&object=", "Protein Ecocyc", "Search for Enzyme at Ecocyc");
    private static final SearchURL ECOCYC_GENE_URL = new SearchURL("http://BioCyc.org/ECOLI/substring-search?type=GENE&object=", "Gene Ecocyc", "Search for genes at Ecocyc");
    private static final SearchURL ECOCYC_MOL_URL = new SearchURL("http://BioCyc.org/ECOLI/substring-search?type=COMPOUND&object=", "Compound in Ecocyc", "Search for compounds at Ecocyc");
    private static final SearchURL GO_URL = new SearchURL("http://www.ebi.ac.uk/ego/DisplayGoTerm?id=", "Go in Quick GO", "Search for Go Terms at QuickGO");
    private static final SearchURL ENSEMBL_GENE_EURL = new SearchURL("http://www.ensembl.org/Homo_sapiens/geneview?gene=", "ENSEMBL genes", "Search for genes in ENSEMBL");
    private static final SearchURL ENSEMBL_PROTEIN_URL = new SearchURL("http://www.ensembl.org/Homo_sapiens/protview?db=core;peptide=", "ENSEMBL proteins", "Search for proteins in ENSEMBL");
    public static final SearchURL[] PRESET_SEARCH_URL = { GOOGLE_URL, SWISS_PROT_URL, NCBI_URL, ECOCYC_PROT_URL, ECOCYC_GENE_URL, ECOCYC_MOL_URL, GO_URL, ENSEMBL_GENE_EURL, ENSEMBL_PROTEIN_URL };
    public static SearchURL SEARCH_URL = GOOGLE_URL;
    public static boolean CUSTOM_SEARCH = false;

    public static final int MIN_NODE_SIZE = 1;
    public static final int MAX_NODE_SIZE = 100;
    public static final int MIN_EDGE_THICKNESS = 1;
    public static final int MAX_EDGE_THICKNESS = 100;
    public static final int MIN_ARROWHEAD_SIZE = 1;
    public static final int MAX_ARROWHEAD_SIZE = 60;
    public static final float MIN_MANUAL_NODE_SIZE = 0.01f;
    public static final int MAX_EDGES_TO_RENDER_NAMES = 50000;

    public static final PrefInt ARROW_HEAD_SIZE = new PrefInt(5, "arrow_head_size", true);
    public static final double ARROW_HEAD_THETA = PI / 5.0;

    public static boolean WEIGHTED_EDGES = false;
    public static final float WEIGHT_LEVEL = 2.0f;
    public static final PrefColor DEFAULT_EDGE_COLOR = new PrefColor(new Color(170, 207, 174), "default_edge_color", true);
    public static final Color DEFAULT_NODE_COLOR = new Color(0, 0, 144);
    public static final PrefFloat DEFAULT_NODE_SIZE = new PrefFloat(5.0f, "default_node_size", false);
    public static final PrefBool COLOR_EDGES_BY_WEIGHT = new PrefBool(true, "color_edges_by_weight", true);
    public static final PrefBool COLOR_EDGES_BY_COLOR = new PrefBool(false, "color_edges_by_color", true);
    public static final PrefFloat DEFAULT_EDGE_SIZE = new PrefFloat(1.0f, "default_edge_size", true);
    public static final PrefBool PROPORTIONAL_EDGES_SIZE_TO_WEIGHT = new PrefBool(false, "proportional_edges_size_to_weight", true);

    public static final String GROUP_NAME_REG = "GroupNode";
    public static final String GROUP_DISPLAY_NAME_REG = "G";
    public static final String GROUP_NAME_COMPLETE = "CompleteGroup";
    public static final String GROUP_DISPLAY_NAME_COMPLETE = "C";

    public static final String BLOCK_ALL = "Block All";
    public static final String UNBLOCK_ALL = "Unblock All";
    public static boolean IS_BLOCKED = false;

    public static final PrefBool PLOT_GRID_LINES = new PrefBool(false, "plot_grid_lines", true);
    public static final PrefInt PLOT_CLASS_STATISTIC_TYPE = new PrefInt(0, "plot_class_statistic_type", true);
    public static final PrefInt PLOT_SELECTION_STATISTIC_TYPE = new PrefInt(0, "plot_selection_statistic_type", true);
    public static final PrefBool PLOT_AXES_LEGEND = new PrefBool(false, "plot_axes", true);
    public static final PrefInt PLOT_TRANSFORM = new PrefInt(0, "plot_transform", true);

    public static final PrefBool CV_AUTO_SIZE_COLUMNS = new PrefBool(true, "cv_auto_size_columns", true);

    public static final boolean SAVE_CUSTOMIZE_NODE_NAMES_OPTIONS = true;
    public static final PrefString CUSTOMIZE_NODE_NAMES_DELIMITER = new PrefString("", "customize_node_names_delimiter", SAVE_CUSTOMIZE_NODE_NAMES_OPTIONS);
    public static final PrefBool CUSTOMIZE_NODE_NAMES_SHOW_FULL_NAME = new PrefBool(true, "customize_node_names_show_full_name", SAVE_CUSTOMIZE_NODE_NAMES_OPTIONS);
    public static final PrefBool CUSTOMIZE_NODE_NAMES_SHOW_PARTIAL_NAME_LENGTH = new PrefBool(false, "customize_node_names_show_partial_name_length", SAVE_CUSTOMIZE_NODE_NAMES_OPTIONS);
    public static final PrefInt CUSTOMIZE_NODE_NAMES_SHOW_PARTIAL_NAME_LENGTH_NUMBER_OF_CHARACTERS = new PrefInt(10, "customize_node_names_show_partial_name_length_number_of_characters", SAVE_CUSTOMIZE_NODE_NAMES_OPTIONS);
    public static enum OpenGLFontTypes     { BITMAP_9_BY_15, BITMAP_8_BY_13, BITMAP_TIMES_ROMAN_10, BITMAP_TIMES_ROMAN_24, BITMAP_HELVETICA_10, BITMAP_HELVETICA_12, BITMAP_HELVETICA_18 }
    public static int[] OPENGL_FONT_HEIGHTS =    {       16,             14,                    11,                    25,                  11,                  13,                  19 };
    public static int[] OPENGL_FONT_DESCENDERS = {       4,               3,                     4,                    6,                    3,                   4,                  5  };
    public static OpenGLFontTypes NODE_NAMES_OPENGL_FONT_TYPE = OpenGLFontTypes.BITMAP_HELVETICA_12;
    public static final PrefString CUSTOMIZE_NODE_NAMES_OPENGL_NAME_FONT_TYPE = new PrefString(OpenGLFontTypes.BITMAP_HELVETICA_12.toString(), "customize_node_names_opengl_name_font_type", SAVE_CUSTOMIZE_NODE_NAMES_OPTIONS);
    public static final PrefInt CUSTOMIZE_NODE_NAMES_NAME_RENDERING_TYPE = new PrefInt(0, "customize_node_names_name_rendering_type", SAVE_CUSTOMIZE_NODE_NAMES_OPTIONS);

    public static final double MESSAGE_APPEARANCE_PROBABILITY = 0.75;
    public static final Calendar CALENDAR = Calendar.getInstance();
    public static final String[] ALL_EXIT_MESSAGES = {
                                                       "Rome wan't built in just one day.",
                                                       "You have yet to disprove the existence of God.",
                                                       "You may not have enough for a paper yet.",
                                                       "Remember to have enough data analyzed for that conference that you intend to go.",
                                                       "Maybe you need an extra cup of coffee instead before quitting.",
                                                       "Maybe you need an extra cup of tea instead before quitting.",
                                                       "You haven't maxed out your graphics card with your data yet.",
                                                       "You haven't submitted a Network of the Month yet.",
                                                       "Chances are that you haven't won a Nobel Prize yet.",
                                                       "Have you seen your graph pulsate today?",
                                                       "Have you tried your graph with the Gooch Shader?",
                                                       "Have you tried your graph with the Lava Shader?",
                                                       "Have you tried your graph with the Marble Shader?",
                                                       "Have you tried your graph with the Glyphbombing Shader?",
                                                       "A graph a day keeps the reviewers away.",
                                                       "A graph a day keeps the supervisors away.",
                                                       "For full effects please visit the Trippy Â® Experience.",
                                                       "Fancy some breakfast? Try our torus shape.",
                                                       "Where are the donuts? Try all of our shapes.",
                                                       "Cancer research needs your help."
                                                   };

    static
    {
        ShaderLightingSFXs.ShaderTypes[] allShaderTypes = ShaderLightingSFXs.ShaderTypes.values();
        for (int i = 0; i < ShaderLightingSFXs.NUMBER_OF_AVAILABLE_SHADERS; i++)  // First Shader (Phong) is on by default
            ALL_SHADING_SFXS[i] = new PrefBool( (i == 0), "" + allShaderTypes[i].toString().toLowerCase() + "_shading", true );

        DEFAULT_SURFACE_IMAGES = new TexturesLoader(DEFAULT_SURFACE_IMAGE_FILES_PATH, DEFAULT_SURFACE_IMAGE_FILE_NAME, false, true, true, false);
        AnimationEnvironment.ANIMATION_PER_NODE_MAX_VALUE.get(); // so as to enforce the static loading of all the AnimationEnvironment global variables
    }


}
