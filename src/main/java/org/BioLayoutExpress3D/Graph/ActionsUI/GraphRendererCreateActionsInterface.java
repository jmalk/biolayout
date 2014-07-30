package org.BioLayoutExpress3D.Graph.ActionsUI;

import java.awt.event.*;

/**
*
* The GraphRendererCreateActionsInterface interface defines the UI Creation Action support for the GraphRenderer2D & GraphRenderer3D classes.
*
* @see org.BioLayoutExpress3D.Graph.GraphRenderer2D
* @see org.BioLayoutExpress3D.Graph.GraphRenderer3D
* @see org.BioLayoutExpress3D.Graph.ActionsUI.GraphRendererActions
* @author Thanos Theo, 2011
* @version 3.0.0.0
*
*/

public interface GraphRendererCreateActionsInterface
{

    /**
    *  Creates the undo node dragging action.
    */
    public void createUndoNodeDraggingAction(ActionEvent e);

    /**
    *  Creates the redo node dragging action.
    */
    public void createRedoNodeDraggingAction(ActionEvent e);

    /**
    *  Creates the autorotate action.
    */
    public void createAutoRotateAction(ActionEvent e);

    /**
    *  Creates the screensaver 2D mode action.
    */
    public void createAutoScreenSaver2DModeAction(ActionEvent e);

    /**
    *  Creates the pulsation 3D mode action.
    */
    public void createPulsation3DModeAction(ActionEvent e);

    /**
    *  Creates the selection action.
    */
    public void createSelectAction(ActionEvent e);

    /**
    *  Creates the translation action.
    */
    public void createTranslateAction(ActionEvent e);

    /**
    *  Creates the rotation action.
    */
    public void createRotateAction(ActionEvent e);

    /**
    *  Creates the zoom action.
    */
    public void createZoomAction(ActionEvent e);

    /**
    *  Creates the reset view action.
    */
    public void createResetViewAction(ActionEvent e);

    /**
    *  Creates the render action.
    */
    public void createRenderImageToFileAction(ActionEvent e);

    /**
    *  Creates the high resolution render action.
    */
    public void createRenderHighResImageToFileAction(ActionEvent e);


}