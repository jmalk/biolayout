package org.BioLayoutExpress3D.Graph;

import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.concurrent.*;
import com.jogamp.opengl.util.texture.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.CoreUI.Dialogs.*;
import org.BioLayoutExpress3D.Graph.ActiveRendering.*;
import org.BioLayoutExpress3D.Graph.GraphElements.*;
import org.BioLayoutExpress3D.Graph.Selection.*;
import org.BioLayoutExpress3D.Network.*;

/**
*
* The GraphRendererCommonVariables class is the class that holds all relevant Graph, GraphRenderer2D & GraphRenderer3D OpenGL variables.
* All variables in GraphRendererCommonVariables static class have package scope and are to be used within Graph, GraphRenderer2D & GraphRenderer3D.
*
* @see org.BioLayoutExpress3D.Graph.GraphRenderer2D
* @see org.BioLayoutExpress3D.Graph.GraphRenderer3D
* @author Thanos Theo, 2011
* @version 3.0.0.0
*
*/

final class GraphRendererCommonVariables // package access
{

    /**
    *  Value needed for the OpenGL renderer.
    */
    static boolean qualityRendering = false;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static int prevHowManyDisplayListsToCreate = 0;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static IntBuffer allEdgesDisplayLists = null;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static int nodesDisplayList = 0;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static int fastSelectionNodesDisplayList = 0;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static int selectedNodesDisplayList = 0;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static int pathwayComponentContainersDisplayList = 0;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static volatile boolean updateEdgesDisplayList = false;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static volatile boolean updateNodesDisplayList = false;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static volatile boolean updateSelectedNodesDisplayList = false;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static int animationFrameCount = 0;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static boolean stepAnimation = false;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static int currentTick = 0;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static boolean animationRender = false;

    /**
    *  Animation spectrum image & texture related variables.
    */
    static BufferedImage animationSpectrumImage = null;

    /**
    *  Animation spectrum image & texture related variables.
    */
    static Texture animationSpectrumTexture = null;

    /**
    *  Constant value needed for the OpenGL renderer.
    */
    static GraphRendererThreadUpdater graphRendererThreadUpdater = null;

    /**
    *  Constant value needed for the OpenGL renderer.
    */
    static boolean frameskip = true;

    /**
    *  Constant value needed for the OpenGL renderer.
    */
    static boolean fullscreen = false;

    /**
    *  Constant value needed for the OpenGL renderer.
    */
    static int targetFPS = 60;

    /**
    *  Constant value needed for the OpenGL renderer.
    */
    static int noVynchTargetFPS = 600;

    /**
    *  Constant value needed for the OpenGL renderer.
    */
    static int rate = 60;

    /**
    *  Constant value needed for the OpenGL renderer.
    */
    static int depth = 32;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static int width = 1024;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static int height = 768;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static double pickOriginX = 0.0;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static double pickOriginY = 0.0;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static double pickWidth = 0;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static double pickHeight = 0;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static boolean pickOneNode = false;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static String lastNodeNamePicked = "";

    /**
    *  Value needed for the OpenGL renderer.
    */
    static String lastNodeURLStringPicked = "";

    /**
    *  Value needed for the OpenGL renderer.
    */
    static GraphNode closestNode = null;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static ScheduledFuture graphPopupScheduledFuture = null;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static boolean graphPopupIsTiming = false;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static boolean graphPopupReset = false;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static float selectBoxStartX = 0.0f;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static float selectBoxStartY = 0.0f;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static float selectBoxEndX = 0.0f;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static float selectBoxEndY = 0.0f;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static boolean selectMode = false;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static boolean isShiftDown = false;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static boolean isShiftAltDown = false;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static boolean isInMotion = false;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static boolean mouseHasClicked = false;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static boolean hasInitiated2DNodeDragging = false;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static boolean takeScreenshot = false;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static boolean takeHighResScreenshot = false;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static boolean renderToFile = true;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static BufferedImage screenshot = null;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static boolean deAllocOpenGLMemory = false;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static boolean isFirstSwitchCheck = false;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static boolean rendererModeCheck = false;

    /**
    *  Screenshot related variable.
    */
    static File saveScreenshotFile = null;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static LayoutFrame layoutFrame = null;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static LayoutProgressBarDialog layoutProgressBarDialog = null;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static NetworkContainer nc = null;

    /**
    *  Value needed for the OpenGL renderer.
    */
    static SelectionManager selectionManager = null;

    /**
    *  Data structure that holds graph nodes information.
    */
    static HashMap<Integer, GraphNode> graphNodes = null;

    /**
    *  Data structure that holds graph edges information.
    */
    static HashSet<GraphEdge> graphEdges = null;

    /**
    *  Data structure that holds visible graph nodes information.
    */
    static HashSet<GraphNode> visibleNodes = null;

    /**
    *  Data structure that holds visible graph edges information.
    */
    static HashSet<GraphEdge> visibleEdges = null;

    /**
    *  GraphListener listener to be used as a callback for the switchRenderer process.
    */
    static GraphListener listener = null;

    /**
    *  Static initializer.
    */
    static
    {
        graphNodes = new HashMap<Integer, GraphNode>();
        graphEdges = new HashSet<GraphEdge>();
        visibleNodes = new HashSet<GraphNode>();
        visibleEdges = new HashSet<GraphEdge>();
    }


}
