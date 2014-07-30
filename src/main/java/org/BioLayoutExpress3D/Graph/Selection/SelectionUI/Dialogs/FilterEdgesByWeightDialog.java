package org.BioLayoutExpress3D.Graph.Selection.SelectionUI.Dialogs;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.DataStructures.*;
import org.BioLayoutExpress3D.Graph.GraphElements.*;
import org.BioLayoutExpress3D.Network.*;
import org.BioLayoutExpress3D.Utils.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
*
* @author Leon Goldovsky, full refactoring by Thanos Theo, 2008-2009-2010-2011-2012
* @version 3.0.0.0
*
*/

public final class FilterEdgesByWeightDialog extends JDialog implements ChangeListener
{
    /**
    *  Serial version UID variable for the FilterNodesByWeight class.
    */
    public static final long serialVersionUID = 111222333444555740L;

    private LayoutFrame layoutFrame = null;
    private JSlider weightsSlider = null;
    private FloatNumberField weightsField = null;
    private JCheckBox alsoHideNodes = null;
    private JCheckBox previewChanges = null;
    private JButton okButton = null;
    private JButton cancelButton = null;

    private AbstractAction weightFilterDialogAction = null;
    private AbstractAction okAction = null;
    private AbstractAction cancelAction = null;

    private float currentSliderValue = 0.0f;
    private float prevSliderValue = 0.0f;
    private int prevMinWeight = 0;
    private int prevMaxWeight = 0;
    private float sliderValueWhenOpenedDialog = 0.0f;

    public FilterEdgesByWeightDialog(LayoutFrame layoutFrame)
    {
        super(layoutFrame, "Filter Edges By Weight", true);

        this.layoutFrame = layoutFrame;

        initActions();
        createDialogElements();
        initComponents();

        this.setResizable(false);
        this.pack();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }

    private void createDialogElements()
    {
        weightsField = new FloatNumberField(0, 10);
        weightsField.setToolTipText("Weight Number");

        weightsSlider = new JSlider(JSlider.HORIZONTAL);
        weightsSlider.addChangeListener(this);
        weightsSlider.addMouseListener( new MouseAdapter()
        {
           @Override
            public void mouseReleased(MouseEvent e)
            {
                if ( ( previewChanges.isSelected() ) && (prevSliderValue != currentSliderValue) )
                {
                    filterEdges(false);
                    prevSliderValue = currentSliderValue;
                }
            }
        } );
        weightsSlider.addKeyListener( new KeyAdapter()
        {
           @Override
            public void keyReleased(KeyEvent e)
            {
                int keyCode = e.getKeyCode();

                if (  (keyCode == KeyEvent.VK_UP)  || (keyCode == KeyEvent.VK_DOWN) || (keyCode == KeyEvent.VK_LEFT)    || (keyCode == KeyEvent.VK_RIGHT)
                   || (keyCode == KeyEvent.VK_END) || (keyCode == KeyEvent.VK_HOME) || (keyCode == KeyEvent.VK_PAGE_UP) || (keyCode == KeyEvent.VK_PAGE_DOWN) )
                {
                    if ( previewChanges.isSelected() )
                        filterEdges(false);
                }
            }
        } );
        weightsSlider.setToolTipText("Weight Number");

        alsoHideNodes = new JCheckBox("Also Hide/Unhide Nodes");
        alsoHideNodes.setToolTipText("Also Hide/Unhide Nodes");
        previewChanges = new JCheckBox("Preview");
        previewChanges.setSelected(true);
        previewChanges.setToolTipText("Preview");

        okButton = new JButton(okAction);
        okButton.setToolTipText("OK");
        cancelButton = new JButton(cancelAction);
        cancelButton.setToolTipText("Cancel");
    }

    private void initComponents()
    {
        JPanel upPanel = new JPanel(true);
        JLabel text = new JLabel("Note that Weighted Edges are presented in the slider times 100:");
        text.setAlignmentX(Component.CENTER_ALIGNMENT);
        upPanel.add(text);

        JPanel middleUpPanel = new JPanel(true);
        middleUpPanel.add(weightsSlider);
        middleUpPanel.add( Box.createRigidArea( new Dimension(15, 5) ) );
        middleUpPanel.add(weightsField);

        JPanel middleDownPanel = new JPanel(true);
        middleDownPanel.add(alsoHideNodes);
        middleUpPanel.add( Box.createRigidArea( new Dimension(25, 5) ) );
        middleDownPanel.add(previewChanges);

        JPanel downPanel = new JPanel(true);
        downPanel.add(okButton);
        downPanel.add(cancelButton);

        JPanel containerPanel = new JPanel(true);
        containerPanel.setLayout( new BoxLayout(containerPanel, BoxLayout.Y_AXIS) );
        containerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        containerPanel.add(upPanel);
        containerPanel.add(middleUpPanel);
        containerPanel.add( Box.createRigidArea( new Dimension(0, 5) ) );
        containerPanel.add(middleDownPanel);
        containerPanel.add( Box.createRigidArea( new Dimension(0, 5) ) );
        containerPanel.add(downPanel);

        this.getContentPane().add(containerPanel);
        this.getRootPane().setDefaultButton(okButton);
    }

    private void initActions()
    {
        weightFilterDialogAction = new AbstractAction("Filter Edges By Weight")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555741L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                initFilterEdgesByWeightDialog();
            }
        };
        weightFilterDialogAction.setEnabled(false);

        okAction = new AbstractAction("OK")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555742L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                filterEdges(false);
                setVisible(false);
            }
        };

        cancelAction = new AbstractAction("Cancel")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555743L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                float currentSliderValueWhenOpenedDialog = weightsField.getValue();
                if (currentSliderValueWhenOpenedDialog != sliderValueWhenOpenedDialog)
                {
                    weightsSlider.setValue( (int)(100.0f * sliderValueWhenOpenedDialog) );
                    weightsField.setValue(sliderValueWhenOpenedDialog);
                    filterEdges(false);
                }
                setVisible(false);
            }
        };
    }

    private void initFilterEdgesByWeightDialog()
    {
        layoutFrame.getGraph().getSelectionManager().deselectAll();

        updateSliderToMinMaxWeights();
        filterEdges(true);
        sliderValueWhenOpenedDialog = weightsField.getValue();

        setVisible(true);
    }

    private void updateSliderToMinMaxWeights()
    {
        Tuple2<Integer, Integer> tuple2 = findMinMaxWeightFromNetwork();
        int minWeight = tuple2.first;
        int maxWeight = tuple2.second;
        if ( (minWeight != prevMinWeight) || (maxWeight != prevMaxWeight) )
        {
            int majorTickSpacing = 1;
            int minorTickSpacing = 1;
            int range = maxWeight - minWeight;
            if (range > 100)
            {
                majorTickSpacing = range;
                minorTickSpacing = majorTickSpacing / 10;
            }
            else if (range >= 20)
            {
                majorTickSpacing = 10;
                minorTickSpacing = 1;
            }
            else if (range >= 10)
            {
                majorTickSpacing = 5;
                minorTickSpacing = 1;
            }
            else if (range >= 6)
            {
                majorTickSpacing = 2;
                minorTickSpacing = 1;
            }

            initWeightsSliderDetails(minWeight, maxWeight, minorTickSpacing, majorTickSpacing);

            prevMinWeight = minWeight;
            prevMaxWeight = maxWeight;
        }
    }

    private Tuple2<Integer, Integer> findMinMaxWeightFromNetwork()
    {
        float minWeight = 1.0f;
        float maxWeight = 0.0f;
        float currentWeight = 0.0f;
        ArrayList<Edge> allAvailableEdges = layoutFrame.getNetworkRootContainer().getEdges();
        for (Edge edge : allAvailableEdges)
        {
            currentWeight = edge.getWeight();
            if (minWeight > currentWeight)
                minWeight = currentWeight;
            if (maxWeight < currentWeight)
                maxWeight = currentWeight;
        }

        return Tuples.tuple( (int)(100 * minWeight), (int)(100 * maxWeight) );
    }

    private void initWeightsSliderDetails(int min, int max, int minorTickSpacing, int majorTickSpacing)
    {
        // avoid setMinimum() & setMaximum() because of a strange JSlider massive memory allocation bug!!!
        weightsSlider.setModel( new DefaultBoundedRangeModel(min, 0, min, max) );
        weightsSlider.setMinorTickSpacing(minorTickSpacing);
        weightsSlider.setMajorTickSpacing(majorTickSpacing);
        weightsSlider.setPaintTicks(true);
        weightsSlider.setLabelTable( weightsSlider.createStandardLabels(majorTickSpacing) );
        weightsSlider.setPaintLabels(true);
        weightsSlider.setValue(min);
        weightsField.setValue(min / 100.0f);

        this.pack();
    }

    private void filterEdges(boolean buildNodesDisplayList)
    {
        if ( !layoutFrame.getGraph().getGraphNodes().isEmpty() )
        {
            layoutFrame.getGraph().getVisibleEdges().addAll( layoutFrame.getGraph().getGraphEdges() );
            layoutFrame.getGraph().getVisibleNodes().addAll( layoutFrame.getGraph().getGraphNodes() );
            HashSet<GraphEdge> visibleEdges = layoutFrame.getGraph().getVisibleEdges();
            HashSet<GraphNode> visibleNodes = layoutFrame.getGraph().getVisibleNodes();
            HashSet<GraphEdge> movedEdges = new HashSet<GraphEdge>();
            HashSet<GraphNode> movedNodes = null;

            if ( alsoHideNodes.isSelected() )
                movedNodes = new HashSet<GraphNode>();

            float checkCurrentSliderValue = weightsField.getValue();
            for (GraphEdge graphEdge : visibleEdges)
                if (graphEdge.getEdge().getWeight() >= checkCurrentSliderValue)
                    movedEdges.add(graphEdge);

            // enable the unhide all and delete hidden actions if a change has been detected
            boolean flag = ( visibleEdges.size() != movedEdges.size() );
            layoutFrame.getCoreSaver().getSaveVisibleAction().setEnabled(flag);
            layoutFrame.getGraph().getSelectionManager().getUnhideAllAction().setEnabled(flag);
            layoutFrame.getGraph().getSelectionManager().getDeleteHiddenAction().setEnabled(flag);

            // better doing the inverse than doing it with < weightsField.getValue() and then visibleEdges.removeAll(movedEdges),
            // avoids an expensive loop and addAll() is using a fast System.arrayCopy()
            visibleEdges.clear();
            visibleEdges.addAll(movedEdges);

            if ( alsoHideNodes.isSelected() )
            {
                for (GraphNode graphNode : visibleNodes)
                {
                    for ( GraphEdge graphEdge : graphNode.getNodeEdges() )
                    {
                        if ( visibleEdges.contains(graphEdge) )
                        {
                            movedNodes.add(graphNode);
                            break; // jump out of the inner foreach loop, no more checks needed as even one edge means node has to be present in visible list
                        }
                    }
                }

                visibleNodes.clear();
                visibleNodes.addAll(movedNodes);
            }

            layoutFrame.getGraph().updateDisplayLists(buildNodesDisplayList || alsoHideNodes.isSelected(), true, false); // only update edges display list when needed, huge speed-up gain!!!
        }
    }

    public AbstractAction getFilterEdgesByWeightAction()
    {
        return weightFilterDialogAction;
    }

    @Override
    public void stateChanged(ChangeEvent e)
    {
        currentSliderValue = ( (JSlider)e.getSource() ).getValue() / 100.0f;
        weightsField.setValue(currentSliderValue);
    }


}