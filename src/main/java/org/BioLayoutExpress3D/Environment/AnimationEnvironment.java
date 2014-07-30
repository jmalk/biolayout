package org.BioLayoutExpress3D.Environment;

import java.awt.*;
import org.BioLayoutExpress3D.Environment.Preferences.*;
import org.BioLayoutExpress3D.Expression.*;
import org.BioLayoutExpress3D.Textures.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import org.BioLayoutExpress3D.Simulation.SignalingPetriNetSimulation;

/**
*
* AnimationEnvironment is the class holding all the animation related environment variables.
*
* @author Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*
*/

public class AnimationEnvironment
{

    /**
    *  Constant value needed for the animation.
    */
    public static final String START_ANIMATION_EVENT_STRING = "StartAnimation";

    /**
    *  Constant value needed for the animation.
    */
    public static final String STOP_ANIMATION_EVENT_STRING = "StopAnimation";

    /**
    *  Constant value needed for the animation.
    */
    public static final int FRAMERATE_PER_SECOND_FOR_ANIMATION = 60;

    /**
    *  Constant value needed for the animation.
    */
    public static final float[] ANIMATION_MAX_SPECTRUM_COLOR_ARRAY = new float[4];

    /**
    *  Constant value needed for the animation.
    */
    public static int TOTAL_NUMBER_OF_ANIMATION_TICKS = 0;

    /**
    *  Constant value needed for the animation.
    */
    public static ExpressionData ANIMATION_EXPRESSION_DATA = null;

    /**
    *  Constant value needed for the animation.
    */
    public static float[] ANIMATION_EXPRESSION_DATA_LOCAL_MAX_VALUES = null;

    /**
    *  Constant value needed for the animation.
    */
    public static SignalingPetriNetSimulation.SpnResult ANIMATION_SIMULATION_RESULTS = null;

    /**
    *  Constant value needed for the animation.
    */
    public static boolean ANIMATION_FLUID_LINEAR_TRANSITION = true;

    /**
    *  Constant value needed for the animation.
    */
    public static boolean ANIMATION_FLUID_POLYNOMIAL_TRANSITION = false;

    /**
    *  Constant value needed for the animation.
    */
    public static float ANIMATION_TICKS_PER_SECOND = 0.0f;

    /**
    *  Constant value needed for the animation.
    */
    public static int ANIMATION_MAX_NODE_SIZE = 1;

    /**
    *  Constant value needed for the animation.
    */
    public static float ANIMATION_RESULTS_MAX_VALUE = 0.0f;

    /**
    *  Constant value needed for the animation.
    */
    public static float ANIMATION_RESULTS_REAL_MAX_VALUE = 0.0f;

    /**
    *  Constant value needed for the animation.
    */
    public static boolean ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION = true;

    /**
    *  Constant value needed for the animation.
    */
    public static boolean ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITION = false;

    /**
    *  Constant value needed for the animation.
    */
    public static Color ANIMATION_MIN_SPECTRUM_COLOR = Color.GREEN;

    /**
    *  Constant value needed for the animation.
    */
    public static Color ANIMATION_MAX_SPECTRUM_COLOR = Color.RED;

    /**
    *  Constant value needed for the animation.
    */
    public static final String ANIMATION_DEFAULT_SPECTRUM_IMAGE_FILES_PATH = IMAGE_FILES_PATH + "SpectrumImages/";

    /**
    *  Constant value needed for the animation.
    */
    private static final String ANIMATION_DEFAULT_SPECTRUM_IMAGE_FILE_NAME = "SpectrumImagesData.txt";

    /**
    *  Constant value needed for the animation.
    */
    public static int ANIMATION_CHOSEN_DEFAULT_SPECTRUM_IMAGE_FILE_INDEX = 0;

    /**
    *  Constant value needed for the animation.
    */
    public static final String[] ANIMATION_DEFAULT_SPECTRUM_IMAGE_FILES = { "BioLayoutExpress3DSpectrumImage1",
                                                                            "BioLayoutExpress3DSpectrumImage2",
                                                                            "BioLayoutExpress3DSpectrumImage3",
                                                                            "ColourBrewerSpectrumYellowRed",
                                                                            "ColourBrewerSpectrumPurple"
                                                                           };

    /**
    *  Constant value needed for the animation.
    */
    public static final TexturesLoader ANIMATION_DEFAULT_SPECTRUM_IMAGES = new TexturesLoader(ANIMATION_DEFAULT_SPECTRUM_IMAGE_FILES_PATH, ANIMATION_DEFAULT_SPECTRUM_IMAGE_FILE_NAME, false, true, true, false);

    /**
    *  Constant value needed for the animation.
    */
    public static boolean ANIMATION_USE_IMAGE_AS_SPECTRUM = true;

    /**
    *  Constant value needed for the animation.
    */
    public static String ANIMATION_USER_SPECTRUM_IMAGE_FILE = "";

    /**
    *  Constant value needed for the animation.
    */
    public static boolean ANIMATION_CHANGE_SPECTRUM_TEXTURE_ENABLED = true;

    /**
    *  Constant value needed for the animation.
    */
    public static boolean ANIMATION_INITIATE_END_OF_ANIMATION = false;

    /**
    *  Constant value needed for the animation.
    */
    public static final PrefBool ANIMATION_PER_NODE_MAX_VALUE = new PrefBool(true, "animation_per_node_max_value", true);

    /**
    *  Constant value needed for the animation.
    */
    public static final PrefBool ANIMATION_MEPN_COMPONENTS_ANIMATION_ONLY = new PrefBool(true, "animation_mepn_components_animation_only", true);

    /**
    *  Constant value needed for the animation.
    */
    public static final PrefBool ANIMATION_SELECTED_NODES_ANIMATION_ONLY = new PrefBool(false, "animation_selected_nodes_animation_only", true);

    /**
    *  Constant value needed for the animation.
    */
    public static final PrefBool ANIMATION_SHOW_NODE_ANIMATION_VALUE = new PrefBool(false, "animation_show_node_animation_value", true);

    /**
    *  Constant value needed for the animation.
    */
    public static final PrefInt ANIMATION_FLUID_TRANSITION_TYPE = new PrefInt(2, "animation_fluid_transition_type", true);

    /**
    *  Constant value needed for the animation.
    */
    public static final PrefInt ANIMATION_DEFAULT_ENTITY_OR_TIMEBLOCK_PER_SECOND = new PrefInt(10, "animation_default_entity_or_timeblock_per_second", true);

    /**
    *  Constant value needed for the animation.
    */
    public static final PrefInt ANIMATION_DEFAULT_MAX_NODE_SIZE = new PrefInt(15, "animation_default_max_node_size", true);

    /**
    *  Constant value needed for the animation.
    */
    public static final PrefInt ANIMATION_DEFAULT_SPECTRUM_IMAGE = new PrefInt(1, "animation_default_spectrum_image", true);


}