package org.BioLayoutExpress3D.Graph.ActionsUI;

import java.awt.event.*;

/**
*
* The GraphCreateActionsInterface interface defines the UI Creation Action support for the Graph class.
*
* @see org.BioLayoutExpress3D.Graph.Graph
* @see org.BioLayoutExpress3D.Graph.ActionsUI.GraphActions
* @author Thanos Theo, 2011
* @version 3.0.0.0
*
*/

public interface GraphCreateActionsInterface
{

    /**
    *  Enumeration of all available Translate Types.
    */
    public static enum TranslateTypes { TRANSLATE_UP, TRANSLATE_DOWN, TRANSLATE_LEFT, TRANSLATE_RIGHT }

    /**
    *  Enumeration of all available Rotate Types.
    */
    public static enum RotateTypes { ROTATE_UP, ROTATE_DOWN, ROTATE_LEFT, ROTATE_RIGHT }

    /**
    *  Enumeration of all available Scale Types.
    */
    public static enum ScaleTypes { SCALE_IN, SCALE_OUT }

    /**
    *  Creates the reTranslate action.
    */
    public void createReTranslateAction(TranslateTypes translateType, ActionEvent e);

    /**
    *  Creates the reRotate action.
    */
    public void createReRotateAction(RotateTypes rotateType, ActionEvent e);

    /**
    *  Creates the reScale action.
    */
    public void createReScaleAction(ScaleTypes zoomType, ActionEvent e);

    /**
    *  Creates the Burst Layout Iterations action.
    */
    public void createBurstLayoutIterationsAction(ActionEvent e);


}