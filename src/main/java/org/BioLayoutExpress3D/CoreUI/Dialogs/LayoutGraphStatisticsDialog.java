package org.BioLayoutExpress3D.CoreUI.Dialogs;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import org.BioLayoutExpress3D.Graph.*;
import org.BioLayoutExpress3D.Graph.GraphElements.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* @author Leon Goldovsky, full refactoring by Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public final class LayoutGraphStatisticsDialog extends JDialog implements Runnable
{
    /**
    *  Serial version UID variable for the GraphStatistics class.
    */
    public static final long serialVersionUID = 111222333444555739L;

    private Graph graph = null;
    private JTextArea textArea = null;
    private AbstractAction graphStatisticsDialogAction = null;

    private int diameter = 0;

    private double averageConnectivityAll = 0.0;
    private double averageConnectivityParents = 0.0;
    private double averageConnectivityChilds = 0.0;

    private int maxConnectivityAll = 0;
    private int maxConnectivityChilds = 0;
    private int maxConnectivityParents = 0;
    private int nodesWithChildren = 0;
    private int nodesWithParents = 0;

    private Collection<GraphNode> currentNodes = null;
    private Collection<GraphEdge> currentEdges = null;

    private boolean calculateDiameter = false;

    /**
    *  The abortThread variable is used to silently abort the Runnable/Thread.
    */
    private volatile boolean abortThread = false;

    public LayoutGraphStatisticsDialog(JFrame frame, Graph graph)
    {
        super(frame, false);

        this.graph = graph;

        initActions();
        initComponents(frame);
    }

    private void initActions()
    {
        graphStatisticsDialogAction = new AbstractAction("Graph Statistics")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555724L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                initGraphStatisticsDialog();
            }
        };
        graphStatisticsDialogAction.setEnabled(false);
    }

    private void initComponents(JFrame frame)
    {
        textArea = new JTextArea();
        textArea.setFont( new Font("System", Font.ITALIC | Font.BOLD, 12) );
        textArea.setRows(15);
        textArea.setColumns(17);
        textArea.setEditable(false);

        this.getContentPane().add(textArea);
        this.pack();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.addWindowListener( new WindowAdapter()
        {
           @Override
            public void windowClosing(WindowEvent e)
            {
                abortThread = true;
                setVisible(false);
            }
        } );
    }

    public void updateGraphStatistics()
    {
        if ( isVisible() )
            initGraphStatisticsDialog();
    }

    private void initGraphStatisticsDialog()
    {
        clearStatistics();

        if ( !graph.getSelectionManager().getSelectedNodes().isEmpty() )
        {
            // don't use selectionManager.getSelectedEdges() as that introduces problems!
            HashSet<GraphEdge> selectedEdges = new HashSet<GraphEdge>();
            HashSet<GraphNode> selectedNodes = graph.getSelectionManager().getSelectedNodes();
            for ( GraphEdge graphEdge : graph.getVisibleEdges() )
                if ( selectedNodes.contains( graphEdge.getNodeFirst() ) && selectedNodes.contains( graphEdge.getNodeSecond() ) )
                    selectedEdges.add(graphEdge);

            initGraphStatistics("Selected Graph Statistics", selectedNodes, selectedEdges, true);
        }
        else
            initGraphStatistics("Full Graph Statistics", graph.getGraphNodes() , graph.getGraphEdges(), false);

        Thread graphStatisticsThread = new Thread(this, "graphStatisticsThread");
        graphStatisticsThread.setPriority(Thread.NORM_PRIORITY);
        graphStatisticsThread.start();
    }

    private void initGraphStatistics(String title, Collection<GraphNode> currentNodes, Collection<GraphEdge> currentEdges, boolean calculateDiameter)
    {
        this.setTitle(title);

        this.currentNodes = currentNodes;
        this.currentEdges = currentEdges;
        this.calculateDiameter = calculateDiameter;
    }

    private void clearStatistics()
    {
        abortThread = true;

        averageConnectivityAll = 0.0;
        averageConnectivityParents = 0.0;
        averageConnectivityChilds = 0.0;
        maxConnectivityAll = 0;
        maxConnectivityChilds = 0;
        maxConnectivityParents = 0;
        nodesWithChildren = 0;
        nodesWithParents = 0;
        diameter = 0;
    }

    @Override
    public void run()
    {
        try
        {
            abortThread = false;

            this.setVisible(true);
            this.setResizable(false);

            textArea.setText("");

            textArea.append("Total Nodes              : " + currentNodes.size() + "\n");
            textArea.append("Total Edges              : " + currentEdges.size() + "\n");

            textArea.append("\nCalculating Connectivity :  ");

            getConnectivity(currentNodes);

            textArea.append("done!\n");

            textArea.append("Avg Connectivity         : " + ( (averageConnectivityAll == Double.NaN) ? 0.0 : Utils.numberFormatting(averageConnectivityAll, 3) )         + "\n");
            textArea.append("Max Connectivity         : " +   maxConnectivityAll     + "\n\n");
            textArea.append("Avg Out Degree           : " + ( (averageConnectivityParents == Double.NaN) ? 0.0 : Utils.numberFormatting(averageConnectivityParents, 3) ) + "\n");
            textArea.append("Max Out Degree           : " +   maxConnectivityParents + "\n\n");
            textArea.append("Avg In Degree            : " + ( (averageConnectivityChilds == Double.NaN) ? 0.0 : Utils.numberFormatting(averageConnectivityChilds, 3) )   + "\n");
            textArea.append("Max In Degree            : " +   maxConnectivityChilds  + "\n");

            if (calculateDiameter)
            {
                textArea.append("\nCalculating Diameter :  ");

                getDiameter(currentNodes);

                textArea.append("done!\n");
                textArea.append("Network Diameter         : " + diameter + "\n");
            }
        }
        catch (Exception exc)
        {
            if (DEBUG_BUILD) println("Exception with GraphStatistics.run() thread:\n" + exc.getMessage());
        }
    }

    private void getConnectivity(Collection<GraphNode> nodes)
    {
        for (GraphNode graphNode : nodes)
        {
            if (abortThread) return;

            if (graphNode.getNodeNeighbours().size() > maxConnectivityAll)
                maxConnectivityAll = graphNode.getNodeNeighbours().size();

            if (graphNode.getNodeChildren().size() > 0)
            {
                nodesWithChildren++;

                if (graphNode.getNodeChildren().size() > maxConnectivityChilds)
                    maxConnectivityChilds = graphNode.getNodeChildren().size();
            }

            if (graphNode.getNodeParents().size() > 0)
            {
                nodesWithParents++;

                if (graphNode.getNodeParents().size() > maxConnectivityParents)
                    maxConnectivityParents = graphNode.getNodeParents().size();
            }

            averageConnectivityAll += graphNode.getNodeNeighbours().size();
            averageConnectivityParents += graphNode.getNodeParents().size();
            averageConnectivityChilds += graphNode.getNodeChildren().size();
        }

        averageConnectivityAll /= nodes.size();
        averageConnectivityParents /= nodesWithParents;
        averageConnectivityChilds /= nodesWithChildren;
    }

    private void getDiameter(Collection<GraphNode> nodes)
    {
        Collection<GraphNode> selected = new HashSet<GraphNode>();
        for (GraphNode graphNode : nodes)
        {
            if (abortThread) return;

            selected.clear();
            selected.add(graphNode);

            getNodeDiameter(selected, 0);
        }
    }

    private void getNodeDiameter(Collection<GraphNode> selected, int diameter)
    {
        if (abortThread) return;

        Collection<GraphNode> neigbours = new HashSet<GraphNode>();
        for (GraphNode graphNode : selected)
            neigbours.addAll( graphNode.getNodeNeighbours() );

        if ( selected.containsAll(neigbours) )
        {
            if (this.diameter < diameter)
                this.diameter = diameter;

            return;
        }
        else
        {
            selected.addAll(neigbours);

            getNodeDiameter(selected, diameter + 1);
        }
    }

    public AbstractAction getGraphStatisticsDialogAction()
    {
        return graphStatisticsDialogAction;
    }


}