package org.BioLayoutExpress3D.Graph;

import java.awt.event.*;
import java.awt.image.*;
import javax.media.opengl.*;
import org.BioLayoutExpress3D.Graph.ActionsUI.*;
import org.BioLayoutExpress3D.Graph.ActiveRendering.*;

/**
*
* GraphInterface interface defines the minimum method contract requirements for the Graph class.
*
* @author Thanos Theo, 2011
* @version 3.0.0.0
*
*/

public interface GraphInterface extends GLEventListener, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener, GraphCreateActionsInterface, GraphRendererCreateActionsInterface, GraphRendererThreadUpdaterInterface, GraphRendererThreadUpdaterAnimationInterface
{

    /**
    *  Switches the renderer mode.
    */
    public void switchRendererMode();

    /**
    *  Adds all events to this Graph.
    */
    public void addAllEvents();

    /**
    *  Removes all events from this Graph.
    */
    public void removeAllEvents();

    /**
    *  Gets a BufferedImage (mainly used for printing functionality).
    */
    public BufferedImage getBufferedImage();

    /**
    *  Fully updates the display lists.
    */
    public void updateAllDisplayLists();

    /**
    *  Updates the nodes display list only.
    */
    public void updateNodesDisplayList();

    /**
    *  Updates the selected nodes display list only.
    */
    public void updateSelectedNodesDisplayList();

    /**
    *  Updates the nodes & selected nodes display lists only.
    */
    public void updateNodesAndSelectedNodesDisplayList();

    /**
    *  Updates the edges display lists only.
    */
    public void updateEdgesDisplayList();

    /**
    *  Updates the display lists selectively.
    */
    public void updateDisplayLists(boolean nodesDisplayList, boolean edgesDisplayList, boolean selectedNodesDisplayList);

    /**
    *  Refreshes the display.
    */
    public void refreshDisplay();

    /**
    *  Resets all values.
    */
    public void resetAllValues();

    /**
    *  Checks if there are more undo steps to be performed.
    */
    public boolean hasMoreUndoSteps();

    /**
    *  Checks if there are more redo steps to be performed.
    */
    public boolean hasMoreRedoSteps();

    /**
    *  The main take a screenshot process.
    */
    public void takeScreenShotProcess(boolean doHighResScreenShot);


}