package org.BioLayoutExpress3D.Graph.Selection.SelectionUI;

import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.Expression.*;
import org.BioLayoutExpress3D.Graph.GraphElements.*;
import org.BioLayoutExpress3D.Network.*;
import static org.BioLayoutExpress3D.Environment.AnimationEnvironment.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;
import org.BioLayoutExpress3D.Simulation.Panels.SimulationResultsPanel;
import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.DefaultCategoryItemRenderer;
import org.jfree.data.category.*;

/**
*
* GraphPopupComponent class that provides popupMenu functionality on top of the OpenGL renderer.
*
* @author Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*
*/

public final class GraphPopupComponent implements Runnable
{

    private static final Color BIOLAYOUT_MENU_ITEM_BACKGROUND_COLOR = new Color(255, 255, 204);
    private static final int POPUP_COMPONENT_PIXEL_OFFSET = 5;
    private static final String ANIMATION_X_AXIS_LABEL = "X Axis: Time Block";
    private static final String ANIMATION_Y_AXIS_LABEL = "Y Axis: Intensity";
    private static final int TIME_BLOCKS_COLUMNS_GRANULARITY = 10;
    private static final int EXPRESSION_PLOT_X_AXIS_NAMES_LENGTH_THRESHOLD = 15;
    private static final String NAME_TAIL = "...";

    private Component component = null;
    private String popupNodeName = "";
    private int popupX = 0;
    private int popupY = 0;
    private ArrayList<GraphNode> graphNodes = null;
    private boolean isPetriNet = false;
    private LayoutFrame layoutFrame = null;

    private ExpressionData expressionData = null;
    private boolean drawGridLines = false;
    private boolean drawAxesLegend = false;
    private ExpressionEnvironment.TransformType transformType = ExpressionEnvironment.TransformType.RAW;

    private JPopupMenu popupMenu = null;
    private JMenuItem popupMenuItem = null;

    public GraphPopupComponent()
    {
        initPopupMenu();
    }

    /**
    *  Initializes the JPopupMenu object to be used for the popup component functionality.
    */
    private void initPopupMenu()
    {
        popupMenu = new JPopupMenu();
        // set popupMenu to be heavyweight so as to be visible on top of the main OpenGL heavyweight canvas
        popupMenu.setLightWeightPopupEnabled(false);
        popupMenu.setLayout( new BorderLayout() );
    }

    /**
    *  Initializes the JPopupMenuItem object to be used for the popup component functionality.
    */
    private void initPopupMenuItem(String popupMenuItemName)
    {
        initPopupMenu();
        popupMenuItem = popupMenu.add(popupMenuItemName);
        popupMenuItem.setIcon(BIOLAYOUT_MENU_ITEM_ICON);
        popupMenuItem.setBackground(BIOLAYOUT_MENU_ITEM_BACKGROUND_COLOR);
        popupMenuItem.setForeground(Color.BLACK);
    }

    /**
    *  Sets the GraphPopupComponent object.
    */
    public void setPopupComponent(Component component, int popupX, int popupY,
            GraphNode graphNode, NetworkContainer nc, LayoutFrame layoutFrame)
    {
        ArrayList<GraphNode> localGraphNodes = new ArrayList<GraphNode>();
        localGraphNodes.add(graphNode);

        setPopupComponent(component, popupX, popupY, localGraphNodes, nc, layoutFrame);
    }

    /**
    *  Sets the GraphPopupComponent object.
    */
    public void setPopupComponent(Component component, int popupX, int popupY,
            HashSet<GraphNode> graphNodes, NetworkContainer nc, LayoutFrame layoutFrame)
    {
        ArrayList<GraphNode> localGraphNodes = new ArrayList<GraphNode>(graphNodes);
        setPopupComponent(component, popupX, popupY, localGraphNodes, nc, layoutFrame);
    }

    /**
    *  Sets the GraphPopupComponent object.
    */
    public void setPopupComponent(Component component, int popupX, int popupY,
                ArrayList<GraphNode> graphNodes, NetworkContainer nc, LayoutFrame layoutFrame)
    {
        this.component = component;
        this.popupX = popupX;
        this.popupY = popupY;
        this.graphNodes = graphNodes;
        this.isPetriNet = nc.getIsPetriNet();
        this.layoutFrame = layoutFrame;

        popupNodeName = "";
        for(GraphNode graphNode : graphNodes)
        {
            if(popupNodeName.length() > 64)
            {
                popupNodeName += ", ...";
                break;
            }

            String name = nc.getNodeName(graphNode.getNodeName());
            if(!name.isEmpty())
            {
                popupNodeName += ", " + name;
            }
        }

        // Strip off first ", "
        if(popupNodeName.length() >= 2)
        {
            popupNodeName = popupNodeName.substring(2);
        }

        expressionData = layoutFrame.getExpressionData();
        drawGridLines = PLOT_GRID_LINES.get();
        drawAxesLegend = PLOT_AXES_LEGEND.get();
        transformType = ExpressionEnvironment.TransformType.values()[PLOT_TRANSFORM.get()];
    }

    private JPanel createExpressionPlot()
    {
        int totalColumns = expressionData.getTotalColumns();
        if (totalColumns == 0)
        {
            return null;
        }

        JFreeChart expressionGraphJFreeChart = ChartFactory.createLineChart(
                null, null, null, null,
                PlotOrientation.VERTICAL, false, false, false);
        CategoryPlot plot = (CategoryPlot) expressionGraphJFreeChart.getPlot();
        int datasetIndex = 0;

        expressionData.setTransformType(transformType);

        for (GraphNode graphNode : this.graphNodes)
        {
            Integer index = expressionData.getIdentityMap(graphNode.getNodeName());

            if (index == null)
            {
                continue;
            }

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            float[] transformedData = expressionData.getTransformedRow(index);

            for (int column = 0; column < totalColumns; column++)
            {
                String columnName = expressionData.getColumnName(column);
                dataset.addValue(transformedData[column], "Value", columnName);
            }

            plot.setDataset(datasetIndex, dataset);
            DefaultCategoryItemRenderer dcir = new DefaultCategoryItemRenderer();
            dcir.setSeriesPaint(0, graphNode.getColor());
            dcir.setSeriesShapesVisible(0, false);
            plot.setRenderer(datasetIndex, dcir);

            datasetIndex++;
        }

        plot.setBackgroundPaint(PLOT_BACKGROUND_COLOR.get());
        plot.setRangeGridlinePaint(PLOT_GRIDLINES_COLOR.get());
        plot.setDomainGridlinePaint(PLOT_GRIDLINES_COLOR.get());

        CategoryAxis axis = plot.getDomainAxis();
        axis.setLowerMargin(0.0);
        axis.setUpperMargin(0.0);
        axis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);

        ChartPanel chartPanel = new ChartPanel(expressionGraphJFreeChart);

        return chartPanel;
    }

    @Override
    /**
    *  Overrides the run() method.
    */
    public void run()
    {
        try
        {
            boolean notmEPNTransitions = false;
            for(GraphNode graphNode : graphNodes)
            {
                if(!graphNode.ismEPNTransition())
                {
                    notmEPNTransitions = true;
                }
            }

            if ( component.hasFocus() )
            {
                JPanel plot = null;
                if ( SHOW_POPUP_OVERLAY_PLOT.get() && DATA_TYPE.equals(DataTypes.EXPRESSION) &&
                        !expressionData.isTransposed() )
                {
                    initPopupMenuItem("Node Name & Expression Profile: " + popupNodeName);
                    plot = createExpressionPlot();
                }
                else if ( SHOW_POPUP_OVERLAY_PLOT.get() && isPetriNet && notmEPNTransitions && (ANIMATION_SIMULATION_RESULTS != null) )
                {
                    initPopupMenuItem("Node Name & Simulation Profile: " + popupNodeName);
                    plot = SimulationResultsPanel.createSimulationResultsPlot(layoutFrame, graphNodes);
                }
                else
                {
                    initPopupMenuItem(popupNodeName);
                }

                if (plot != null)
                {
                    popupMenu.add(popupMenuItem, BorderLayout.NORTH);
                    plot.setPreferredSize(new Dimension(APPLICATION_SCREEN_DIMENSION.width / 3,
                            APPLICATION_SCREEN_DIMENSION.height / 3));
                    popupMenu.add(plot, BorderLayout.CENTER);
                }

                popupMenu.show(component, popupX + POPUP_COMPONENT_PIXEL_OFFSET, popupY + POPUP_COMPONENT_PIXEL_OFFSET);
            }

            // re-request focus in OpenGL renderer component so as to be able to process correctly key events
            component.requestFocus();
        }
        catch (Exception exc)
        {
            if (DEBUG_BUILD) println("Exception with GraphPopupComponent:\n" + exc.getMessage());
        }
    }

    /**
    *  Sets the PopupMenu visibility.
    */
    public void setPopupMenuVisible(boolean isVisible)
    {
        popupMenu.setVisible(isVisible);
    }

    /**
    *  Checks the PopupMenu visibility.
    */
    public boolean isPopupMenuVisible()
    {
        return popupMenu.isVisible();
    }
}