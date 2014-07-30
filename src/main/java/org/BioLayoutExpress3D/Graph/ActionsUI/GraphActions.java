package org.BioLayoutExpress3D.Graph.ActionsUI;

import java.awt.event.*;
import javax.swing.*;
import org.BioLayoutExpress3D.Graph.*;
import static org.BioLayoutExpress3D.Graph.ActionsUI.GraphCreateActionsInterface.*;

/**
*
* The GraphActions class encapsulates UI Action support for the core Graph rendering class.
*
* @see org.BioLayoutExpress3D.Graph.Graph
* @author Thanos Theo, 2011
* @version 3.0.0.0
*
*/

public final class GraphActions
{

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
    private AbstractAction printGraphAction = null;
    private AbstractAction burstLayoutIterationsAction = null;

    /**
    *  The GraphActions constructor.
    */
    public GraphActions(Graph graph)
    {
        createActions(graph);
    }

    /**
    *  Creates all the actions.
    */
    private void createActions(final Graph graph)
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
                graph.createReTranslateAction(TranslateTypes.TRANSLATE_UP, e);
            }
        };
        translateUpAction.setEnabled(false);

        translateDownAction = new AbstractAction("Down")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555721L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                graph.createReTranslateAction(TranslateTypes.TRANSLATE_DOWN, e);
            }
        };
        translateDownAction.setEnabled(false);

        translateLeftAction = new AbstractAction("Left")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555721L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                graph.createReTranslateAction(TranslateTypes.TRANSLATE_LEFT, e);
            }
        };
        translateLeftAction.setEnabled(false);

        translateRightAction = new AbstractAction("Right")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555721L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                graph.createReTranslateAction(TranslateTypes.TRANSLATE_RIGHT, e);
            }
        };
        translateRightAction.setEnabled(false);

        rotateUpAction = new AbstractAction("Rotate Up")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555721L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                graph.createReRotateAction(RotateTypes.ROTATE_UP, e);
            }
        };
        rotateUpAction.setEnabled(false);

        rotateDownAction = new AbstractAction("Rotate Down")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555721L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                graph.createReRotateAction(RotateTypes.ROTATE_DOWN, e);
            }
        };
        rotateDownAction.setEnabled(false);

        rotateLeftAction = new AbstractAction("Rotate Left")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555721L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                graph.createReRotateAction(RotateTypes.ROTATE_LEFT, e);
            }
        };
        rotateLeftAction.setEnabled(false);

        rotateRightAction = new AbstractAction("Rotate Right")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555721L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                graph.createReRotateAction(RotateTypes.ROTATE_RIGHT, e);
            }
        };
        rotateRightAction.setEnabled(false);

        zoomInAction = new AbstractAction("Zoom In")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555721L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                graph.createReScaleAction(ScaleTypes.SCALE_IN, e);
            }
        };
        zoomInAction.setEnabled(false);

        zoomOutAction = new AbstractAction("Zoom Out")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555722L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                graph.createReScaleAction(ScaleTypes.SCALE_OUT, e);
            }
        };
        zoomOutAction.setEnabled(false);

        printGraphAction = new AbstractAction("Print Graph")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555723L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                graph.printGraph();
            }
        };
        printGraphAction.setEnabled(false);

        burstLayoutIterationsAction = new AbstractAction("Burst Layout Iterations")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555725L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                graph.createBurstLayoutIterationsAction(e);
            }
        };
        burstLayoutIterationsAction.setEnabled(false);
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
    *  Gets the print graph action.
    */
    public AbstractAction getPrintGraphAction()
    {
        return printGraphAction;
    }

    /**
    *  Gets the burstLayoutIterations action.
    */
    public AbstractAction getBurstLayoutIterationsAction()
    {
        return burstLayoutIterationsAction;
    }


}