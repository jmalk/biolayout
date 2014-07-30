package org.BioLayoutExpress3D.Models.UIComponents.Panels;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
import static java.lang.Math.*;
import org.BioLayoutExpress3D.Models.Lathe3D.*;
import org.BioLayoutExpress3D.Models.UIComponents.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* The ModelShapeLathe3DDrawLinesPanel class which is the UI placeholder for the Lathe3D draw lines.
*
* @see org.BioLayoutExpress3D.Models.UIComponents.Panels.ModelShapeLathe3DSettingsPanel
* @author Thanos Theo, 2011
* @version 3.0.0.0
*
*/

public class ModelShapeLathe3DDrawLinesPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener
{

    private static final float MAX_OPENGL_AXIS_VALUE = 3.0f;
    private static final int LINES_BORDER_OFFSET = 1;
    private static final Color VERY_LIGHT_GRAY = new Color(240, 240, 240);
    private static final int AXES_LINE_LEGEND_LENGTH = 3;
    private static final int AXES_FONT_STYLE = Font.ITALIC | Font.BOLD;
    private static final int AXES_FONT_SIZE = 5;
    private static final BasicStroke NORMAL_BASIC_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    private static final BasicStroke NORMAL_DASHED_BASIC_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{ 6.0f, 6.0f, 6.0f, 6.0f }, 0.0f);
    private static final BasicStroke AXES_BASIC_STROKE = new BasicStroke(0.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    private static final String[] AXES_LEGENDS = { "0", "1", "2", "3" };
    private static final Rectangle2D[] AXES_LEGENDS_RECTANGLES_2D = new Rectangle2D[AXES_LEGENDS.length];

    private Rectangle activeAreaRectangle = null;
    private Rectangle lineRectangle = null;

    private ArrayList<Float> xsInList = null;
    private ArrayList<Float> ysInList = null;
    private int lineSign = -1;
    private boolean renderMouseMoveLathe3DLine = false;
    private int mouseMoveX = 0;
    private int mouseMoveY = 0;

    /**
    *  Axes related font variable.
    */
    private Font axesFont = null;

    /**
    *  The ModelShapeRenderer reference.
    */
    private ModelShapeRenderer modelShapeRenderer = null;


    /**
    *  The ModelShapeLathe3DDrawLinesPanel class constructor.
    */
    public ModelShapeLathe3DDrawLinesPanel(ModelShapeRenderer modelShapeRenderer)
    {
        super(new BorderLayout(), true);

        this.modelShapeRenderer = modelShapeRenderer;

        initComponents();
    }

    /**
    *  Initializes the UI components for this panel.
    */
    private void initComponents()
    {
        xsInList = new ArrayList<Float>();
        ysInList = new ArrayList<Float>();

        axesFont = this.getFont().deriveFont(AXES_FONT_STYLE, AXES_FONT_SIZE);

        setXsAndYsIns(LATHE3D_SETTINGS.xsIn, LATHE3D_SETTINGS.ysIn);
        addAllEvents();
    }

    /**
    *  Adds all events to this JPanel.
    */
    private void addAllEvents()
    {
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
    }

    /**
    *  Paints all UI related lines.
    */
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if ( (activeAreaRectangle == null) && (lineRectangle == null) )
            createActiveRectangle();
        drawActiveRectangle(g2d);
        drawActiveRectangleBorderAndGridLines(g2d);
        drawAxesLinesAndNumbers(g2d);
        drawLathe3DLines(g2d);
        drawLathe3DPoints(g2d);
    }

    private void createActiveRectangle()
    {
        int x = 0;
        int y = 0;
        int width = this.getWidth();
        int height = this.getHeight();
        int renctangleMaxSize = (width < height) ? width - 6 * LINES_BORDER_OFFSET : height - 6 * LINES_BORDER_OFFSET;

        if (width >= renctangleMaxSize)
        {
            x  = (width - renctangleMaxSize) / 2;
            width = renctangleMaxSize;
        }
        else
        {
            x  = LINES_BORDER_OFFSET;
            width -= (1 + 2 * LINES_BORDER_OFFSET);
        }

        if (height >= renctangleMaxSize)
        {
            y  = (height - renctangleMaxSize) / 2;
            height = renctangleMaxSize;
        }
        else
        {
            y  = LINES_BORDER_OFFSET;
            height -= (1 + 2 * LINES_BORDER_OFFSET);
        }

        activeAreaRectangle = new Rectangle(x, y, width, height);
        lineRectangle = new Rectangle(x - LINES_BORDER_OFFSET, y - LINES_BORDER_OFFSET, width + 2 * LINES_BORDER_OFFSET, height + 2 * LINES_BORDER_OFFSET);
    }

    private void drawActiveRectangle(Graphics2D g2d)
    {
        g2d.setStroke(NORMAL_BASIC_STROKE);
        g2d.setColor(Color.WHITE);
        g2d.fill(activeAreaRectangle);
    }

    private void drawActiveRectangleBorderAndGridLines(Graphics2D g2d)
    {
        g2d.setStroke(NORMAL_BASIC_STROKE);
        // render grid lines
        g2d.setColor(VERY_LIGHT_GRAY);
        // render horizontal grid lines
        for (int i = 1; i <= 9; i++)
            g2d.drawLine(lineRectangle.x + i * lineRectangle.width / 9, lineRectangle.y, lineRectangle.x + i * lineRectangle.width / 9, lineRectangle.y + lineRectangle.height);
        // render vertical grid lines
        for (int i = 1; i <= 9; i++)
            g2d.drawLine(lineRectangle.x, lineRectangle.y + i * lineRectangle.height / 9, lineRectangle.x + lineRectangle.width, lineRectangle.y + i * lineRectangle.height / 9);

        // render top-right borders
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawLine(lineRectangle.x,                       lineRectangle.y,                        lineRectangle.x + lineRectangle.width, lineRectangle.y);
        g2d.drawLine(lineRectangle.x + lineRectangle.width, lineRectangle.y,                        lineRectangle.x + lineRectangle.width, lineRectangle.y + lineRectangle.height);
        // render bottom-left border
        g2d.setColor(Color.ORANGE);
        g2d.drawLine(lineRectangle.x,                       lineRectangle.y,                        lineRectangle.x,                       lineRectangle.y + lineRectangle.height);
        g2d.drawLine(lineRectangle.x,                       lineRectangle.y + lineRectangle.height, lineRectangle.x + lineRectangle.width, lineRectangle.y + lineRectangle.height);
    }

    private void drawAxesLinesAndNumbers(Graphics2D g2d)
    {
        g2d.setStroke(NORMAL_BASIC_STROKE);
        g2d.setColor(Color.MAGENTA);
        // render horizontal axis line legend
        for (int i = 1; i <= 3; i++)
            g2d.drawLine(lineRectangle.x + i * lineRectangle.width / 3, lineRectangle.y + lineRectangle.height - AXES_LINE_LEGEND_LENGTH, lineRectangle.x + i * lineRectangle.width / 3, lineRectangle.y + lineRectangle.height);
        // render vertical axis line legend
        for (int i = 0; i < 3; i++)
            g2d.drawLine(lineRectangle.x, lineRectangle.y + i * lineRectangle.height / 3, lineRectangle.x + AXES_LINE_LEGEND_LENGTH, lineRectangle.y + i * lineRectangle.height / 3);
        // render (0,0) axis line legend
        g2d.drawLine(lineRectangle.x, lineRectangle.y + lineRectangle.height, lineRectangle.x + (AXES_LINE_LEGEND_LENGTH / 2 + 1), lineRectangle.y + lineRectangle.height - (AXES_LINE_LEGEND_LENGTH / 2 + 1));

        Font prevFont = g2d.getFont();
        g2d.setFont(axesFont);
        g2d.setStroke(AXES_BASIC_STROKE);
        for (int i = 0; i < AXES_LEGENDS.length; i++)
            AXES_LEGENDS_RECTANGLES_2D[i] = g2d.getFontMetrics(axesFont).getStringBounds(AXES_LEGENDS[i], g2d);

        // render horizontal axis number legend
        for (int i = 1; i <= 3; i++)
            g2d.drawString(AXES_LEGENDS[i], lineRectangle.x + i * lineRectangle.width / 3 - (int)(AXES_LEGENDS_RECTANGLES_2D[i].getWidth() / 2.0), lineRectangle.y + lineRectangle.height - (AXES_LINE_LEGEND_LENGTH + AXES_LINE_LEGEND_LENGTH / 2));
        // render vertical axis number legend
        for (int i = 0; i < 3; i++)
            g2d.drawString(AXES_LEGENDS[AXES_LEGENDS.length - i - 1], lineRectangle.x  + (AXES_LINE_LEGEND_LENGTH + AXES_LINE_LEGEND_LENGTH / 2), lineRectangle.y + i * lineRectangle.height / 3 + (int)(AXES_LEGENDS_RECTANGLES_2D[AXES_LEGENDS.length - i - 1].getHeight() / 2.0));
        // render (0,0) axis number legend
        g2d.drawString(AXES_LEGENDS[0], lineRectangle.x + (AXES_LINE_LEGEND_LENGTH / 2 + 1) + (int)(AXES_LEGENDS_RECTANGLES_2D[0].getWidth() / 2.0 + 1), lineRectangle.y + lineRectangle.height - (AXES_LINE_LEGEND_LENGTH / 2 + 1) - (int)(AXES_LEGENDS_RECTANGLES_2D[0].getHeight() / 2.0));

        g2d.setStroke(NORMAL_BASIC_STROKE);
        g2d.setFont(prevFont);
    }

    private void drawLathe3DLines(Graphics2D g2d)
    {
        if (xsInList.size() > 1 || renderMouseMoveLathe3DLine)
        {
            int tesselation = modelShapeRenderer.getTesselation() / 3;
            float[] xsIn = null;
            float[] ysIn = null;
            if (renderMouseMoveLathe3DLine)
            {
                xsIn = new float[xsInList.size() + 1]; // plus extra mouseMove coord
                ysIn = new float[ysInList.size() + 1]; // plus extra mouseMove coord
                float[] xsInTemp = ArraysAutoBoxUtils.toPrimitiveListFloat(xsInList);
                float[] ysInTemp = ArraysAutoBoxUtils.toPrimitiveListFloat(ysInList);
                for (int i = 0; i < xsInList.size(); i++)
                {
                    xsIn[i] = xsInTemp[i];
                    ysIn[i] = ysInTemp[i];
                }
                // add extra mouseMove coord
                xsIn[xsIn.length - 1] = convertXToOpenGLCoord(mouseMoveX);
                ysIn[ysIn.length - 1] = convertYToOpenGLCoord(mouseMoveY);
            }
            else
            {
                xsIn = ArraysAutoBoxUtils.toPrimitiveListFloat(xsInList);
                ysIn = ArraysAutoBoxUtils.toPrimitiveListFloat(ysInList);
            }

            LatheCurve latheCurve = new LatheCurve(xsIn, ysIn, tesselation);
            xsIn = latheCurve.getXs();
            ysIn = latheCurve.getYs();

            int x1 = 0;
            int y1 = 0;
            int x2 = 0;
            int y2 = 0;
            int lastListIndex = xsInList.size() - 1;
            g2d.setStroke(NORMAL_BASIC_STROKE);
            g2d.setColor(Color.GREEN);
            for (int i = 0; i < xsIn.length - 1; i++)
            {
                if ( renderMouseMoveLathe3DLine && (    ( (lineSign > 0) && ( xsIn[i] == xsInList.get(lastListIndex) ) && ( ysIn[i] == ysInList.get(lastListIndex) ) )
                                                     || ( (lineSign < 0) && (i == xsIn.length - 2) ) ) )
                    g2d.setStroke(NORMAL_DASHED_BASIC_STROKE);

                x1 = activeAreaRectangle.x + convertXToJava2DCoord(xsIn[i    ]);
                y1 = activeAreaRectangle.y + convertYToJava2DCoord(ysIn[i    ]);
                x2 = activeAreaRectangle.x + convertXToJava2DCoord(xsIn[i + 1]);
                y2 = activeAreaRectangle.y + convertYToJava2DCoord(ysIn[i + 1]);
                g2d.drawLine(x1, y1, x2, y2);
            }
        }
    }

    private void drawLathe3DPoints(Graphics2D g2d)
    {
        g2d.setStroke(NORMAL_BASIC_STROKE);
        int x = 0;
        int y = 0;
        for (int i = 0; i < xsInList.size(); i++)
        {
            x = activeAreaRectangle.x + convertXToJava2DCoord( abs( xsInList.get(i) ) );
            y = activeAreaRectangle.y + convertYToJava2DCoord(      ysInList.get(i) );
            if (xsInList.get(i) < 0.0f) // if a straight line draw a rectangle
            {
                g2d.setColor(Color.RED);
                g2d.drawRect(x - 2, y - 2, 4, 4);
            }
            else // if a curved line draw a circle
            {
                g2d.setColor(Color.BLUE);
                g2d.drawOval(x - 2, y - 2, 4, 4);
            }
        }

        if (renderMouseMoveLathe3DLine)
        {
            x = mouseMoveX;
            y = mouseMoveY;
            if (lineSign < 0) // if a straight line draw a rectangle
            {
                g2d.setColor(Color.RED);
                g2d.drawRect(x - 2, y - 2, 4, 4);
            }
            else // if a curved line draw a circle
            {
                g2d.setColor(Color.BLUE);
                g2d.drawOval(x - 2, y - 2, 4, 4);
            }
        }
    }

    private void updateModelShapeRenderer()
    {
        if (xsInList.size() > 1 || renderMouseMoveLathe3DLine)
        {
            LATHE3D_SETTINGS.xsIn = ArraysAutoBoxUtils.toPrimitiveListFloat(xsInList);
            LATHE3D_SETTINGS.ysIn = ArraysAutoBoxUtils.toPrimitiveListFloat(ysInList);
            modelShapeRenderer.setChangeDetected(true);
            modelShapeRenderer.refreshDisplay();
        }
    }

    private int convertXToJava2DCoord(float x)
    {
        return (int)( (x / MAX_OPENGL_AXIS_VALUE) * activeAreaRectangle.width );
    }

    private int convertYToJava2DCoord(float y)
    {
        return activeAreaRectangle.height - (int)( (y / MAX_OPENGL_AXIS_VALUE) * activeAreaRectangle.height );
    }

    private float convertXToOpenGLCoord(int x)
    {
        float xIn = (x - activeAreaRectangle.x) / (activeAreaRectangle.width / MAX_OPENGL_AXIS_VALUE);
        if (xIn == 0.0f) xIn = 0.001f;

        return lineSign * xIn;
    }

    private float convertYToOpenGLCoord(int y)
    {
        // The first y-coordinate must be 0.0f
        return ( ysInList.isEmpty() ) ? 0.0f : ( activeAreaRectangle.height - (y - activeAreaRectangle.y) ) / (activeAreaRectangle.height / MAX_OPENGL_AXIS_VALUE);
    }

    public void setXsAndYsIns(float[] xsIn, float[] ysIn)
    {
        xsInList.clear();
        ysInList.clear();
        for (int i = 0; i < xsIn.length; i++)
        {
            xsInList.add(xsIn[i]);
            ysInList.add(ysIn[i]);
        }

        setLineSign(lineSign < 0); // so as to update al lrelevant variables

        this.repaint();
    }

    public void setLineSign(boolean setNegative)
    {
        lineSign = (setNegative) ? -1 : 1;

        if ( !xsInList.isEmpty() )
        {
            int lastIndex = xsInList.size() - 1;
            xsInList.set( lastIndex, lineSign * abs( xsInList.get(lastIndex) ) );

            this.repaint();
        }
    }

    public void deleteLastPoint()
    {
        if ( !xsInList.isEmpty() )
        {
            xsInList.remove(xsInList.size() - 1);
            ysInList.remove(ysInList.size() - 1);

            this.repaint();
            updateModelShapeRenderer();
        }
    }

    public void deleteAllPoints()
    {
        xsInList.clear();
        ysInList.clear();

        this.repaint();
    }

    /**
    *  MouseClicked mouseEvent.
    */
    @Override
    public void mouseClicked(MouseEvent e)
    {
        int x = e.getX();
        int y = e.getY();
        if ( (activeAreaRectangle != null) && activeAreaRectangle.contains(x, y) )
        {
            if (DEBUG_BUILD) println("ModelShapeLathe3DDrawLinesPanel mouseClicked()");
        }
    }

    /**
    *  MouseEntered mouseEvent.
    */
    @Override
    public void mouseEntered(MouseEvent e)
    {
        int x = e.getX();
        int y = e.getY();
        if ( (activeAreaRectangle != null) && activeAreaRectangle.contains(x, y) )
        {
            if (DEBUG_BUILD) println("ModelShapeLathe3DDrawLinesPanel mouseEntered()");
        }
    }

    /**
    *  MouseExited mouseEvent.
    */
    @Override
    public void mouseExited(MouseEvent e)
    {
        int x = e.getX();
        int y = e.getY();
        if ( (activeAreaRectangle != null) && activeAreaRectangle.contains(x, y) )
        {
            if (DEBUG_BUILD) println("ModelShapeLathe3DDrawLinesPanel mouseExited()");
        }

        renderMouseMoveLathe3DLine = false;
        mouseMoveX = 0;
        mouseMoveY = 0;

        this.repaint();
    }

    /**
    *  MousePressed mouseEvent.
    */
    @Override
    public void mousePressed(MouseEvent e)
    {
        int x = e.getX();
        int y = e.getY();
        if ( (activeAreaRectangle != null) && activeAreaRectangle.contains(x, y) )
        {
            if (DEBUG_BUILD) println("ModelShapeLathe3DDrawLinesPanel mousePressed()");

            xsInList.add( convertXToOpenGLCoord(x) );
            ysInList.add( convertYToOpenGLCoord(y) );

            this.repaint();
            updateModelShapeRenderer();
        }
    }

    /**
    *  MouseReleased mouseEvent.
    */
    @Override
    public void mouseReleased(MouseEvent e)
    {
        int x = e.getX();
        int y = e.getY();
        if ( (activeAreaRectangle != null) && activeAreaRectangle.contains(x, y) )
        {
            if (DEBUG_BUILD) println("ModelShapeLathe3DDrawLinesPanel mouseReleased()");
        }
    }

    /**
    *  MouseDragged mouseMotionEvent.
    */
    @Override
    public void mouseDragged(MouseEvent e)
    {
        int x = e.getX();
        int y = e.getY();
        if ( (activeAreaRectangle != null) && activeAreaRectangle.contains(x, y) )
        {
            if (DEBUG_BUILD) println("ModelShapeLathe3DDrawLinesPanel mouseDragged()");
        }
    }

    /**
    *  MouseMoved mouseMotionEvent.
    */
    @Override
    public void mouseMoved(MouseEvent e)
    {
        int x = e.getX();
        int y = e.getY();
        if ( (activeAreaRectangle != null) && activeAreaRectangle.contains(x, y) )
        {
            if (DEBUG_BUILD) println("ModelShapeLathe3DDrawLinesPanel mouseMoved()");

            if ( !xsInList.isEmpty() )
            {
                renderMouseMoveLathe3DLine = true;
                mouseMoveX = x;
                mouseMoveY = y;

                this.repaint();
            }
        }
        else
        {
            renderMouseMoveLathe3DLine = false;
            mouseMoveX = 0;
            mouseMoveY = 0;

            this.repaint();
        }
    }

    /**
    *  MouseWheelMoved mouseWheelEvent.
    */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        int x = e.getX();
        int y = e.getY();
        if ( (activeAreaRectangle != null) && activeAreaRectangle.contains(x, y) )
        {
            if (DEBUG_BUILD) println("ModelShapeLathe3DDrawLinesPanel mouseWheelMoved()");
        }
    }


}
