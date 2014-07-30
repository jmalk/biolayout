package org.BioLayoutExpress3D.Graph.ActionsUI;

import org.BioLayoutExpress3D.Graph.*;
import java.awt.event.*;
import javax.swing.*;

/**
*
* The GraphRendererActions class encapsulates UI Action support for the GraphRenderer2D/3D classes.
*
* @see org.BioLayoutExpress3D.Graph.ActionsUI.GraphRendererCreateActionsInterface
* @see org.BioLayoutExpress3D.Graph.GraphRenderer3D
* @author Thanos Theo, 2011
* @version 3.0.0.0
*
*/

public final class GraphRendererActions
{

    private AbstractAction undoNodeDragging = null;
    private AbstractAction redoNodeDragging = null;
    private AbstractAction autoRotateAction = null;
    private AbstractAction screenSaver2DModeAction = null;
    private AbstractAction pulsation3DModeAction = null;
    private AbstractAction selectAction = null;
    private AbstractAction translateAction = null;
    private AbstractAction rotateAction = null;
    private AbstractAction zoomAction = null;
    private AbstractAction resetViewAction = null;
    private AbstractAction renderImageToFileAction = null;
    private AbstractAction renderHighResImageToFileAction = null;

    /**
    *  The GraphRendererActions constructor.
    */
    public GraphRendererActions(Graph graph)
    {
        createActions(graph);
    }

    /**
    *  Creates all the actions.
    */
    private void createActions(final Graph graph)
    {
        undoNodeDragging = new AbstractAction("Undo Node Dragging On Selection")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555791L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                graph.createUndoNodeDraggingAction(e);
            }
        };
        undoNodeDragging.setEnabled(false);

        redoNodeDragging = new AbstractAction("Redo Node Dragging On Selection")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555792L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                graph.createRedoNodeDraggingAction(e);
            }
        };
        redoNodeDragging.setEnabled(false);

        autoRotateAction = new AbstractAction("Toggle AutoRotate / Profile")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555731L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                graph.createAutoRotateAction(e);
            }
        };

        screenSaver2DModeAction = new AbstractAction("Toggle ScreenSaver ®")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222393444555732L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                graph.createAutoScreenSaver2DModeAction(e);
            }
        };

        pulsation3DModeAction = new AbstractAction("Toggle Pulsation / Morphing ®")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555732L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                graph.createPulsation3DModeAction(e);
            }
        };

        selectAction = new AbstractAction("Selection Mode")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555733L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                graph.createSelectAction(e);
            }
        };
        selectAction.setEnabled(false);

        translateAction = new AbstractAction("Translation Mode")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555734L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                graph.createTranslateAction(e);
            }
        };
        translateAction.setEnabled(false);

        rotateAction = new AbstractAction("Rotation Mode")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555735L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                graph.createRotateAction(e);
            }
        };
        rotateAction.setEnabled(false);

        zoomAction = new AbstractAction("Zoom Mode")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555736L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                graph.createZoomAction(e);
            }
        };
        zoomAction.setEnabled(false);

        resetViewAction = new AbstractAction("Reset View")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555737L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                graph.createResetViewAction(e);
            }
        };
        resetViewAction.setEnabled(false);

        renderImageToFileAction = new AbstractAction("Render Graph Image To File As...")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555738L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                graph.createRenderImageToFileAction(e);
            }
        };
        renderImageToFileAction.setEnabled(false);

        renderHighResImageToFileAction = new AbstractAction("Render High Res Graph Image To File As...")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 101222333444555739L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                graph.createRenderHighResImageToFileAction(e);
            }
        };
        renderHighResImageToFileAction.setEnabled(false);
    }

    /**
    *  Gets the undo node dragging action.
    */
    public AbstractAction getUndoNodeDraggingAction()
    {
        return undoNodeDragging;
    }

    /**
    *  Gets the redo node dragging action.
    */
    public AbstractAction getRedoNodeDraggingAction()
    {
        return redoNodeDragging;
    }

    /**
    *  Gets the autorotate action.
    */
    public AbstractAction getAutoRotateAction()
    {
        return autoRotateAction;
    }

    /**
    *  Gets the screensaver 2D mode action.
    */
    public AbstractAction getAutoScreenSaver2DModeAction()
    {
        return screenSaver2DModeAction;
    }

    /**
    *  Gets the pulsation 3D mode action.
    */
    public AbstractAction getPulsation3DModeAction()
    {
        return pulsation3DModeAction;
    }

    /**
    *  Gets the selection action.
    */
    public AbstractAction getSelectAction()
    {
        return selectAction;
    }

    /**
    *  Gets the translation action.
    */
    public AbstractAction getTranslateAction()
    {
        return translateAction;
    }

    /**
    *  Gets the rotation action.
    */
    public AbstractAction getRotateAction()
    {
        return rotateAction;
    }

    /**
    *  Gets the zoom action.
    */
    public AbstractAction getZoomAction()
    {
        return zoomAction;
    }

    /**
    *  Gets the reset view action.
    */
    public AbstractAction getResetViewAction()
    {
        return resetViewAction;
    }

    /**
    *  Gets the render action.
    */
    public AbstractAction getRenderImageToFileAction()
    {
        return renderImageToFileAction;
    }

    /**
    *  Gets the high resolution render action.
    */
    public AbstractAction getRenderHighResImageToFileAction()
    {
        return renderHighResImageToFileAction;
    }


}