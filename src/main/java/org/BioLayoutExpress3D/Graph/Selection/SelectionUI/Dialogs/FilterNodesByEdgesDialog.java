package org.BioLayoutExpress3D.Graph.Selection.SelectionUI.Dialogs;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.Graph.GraphElements.*;
import org.BioLayoutExpress3D.Utils.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
*
* @author Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*
*/

public final class FilterNodesByEdgesDialog extends JDialog implements ChangeListener
{
    /**
    *  Serial version UID variable for the FilterNodesByWeight class.
    */
    public static final long serialVersionUID = 111222333444555740L;

    private LayoutFrame layoutFrame = null;
    private JButton okButton = null;
    private JButton cancelButton = null;
    private JCheckBox removeSingletonNodes = null;
    private JCheckBox previewChanges = null;
    private JSlider edgesSlider = null;

    private AbstractAction edgesFilterDialogAction = null;
    private AbstractAction okAction = null;
    private AbstractAction cancelAction = null;
    private WholeNumberField edgesField = null;

    private int currentSliderValue = 0;
    private int prevSliderValue = 0;
    private int sliderValueWhenOpenedDialog = 0;

    public FilterNodesByEdgesDialog(LayoutFrame layoutFrame)
    {
        super(layoutFrame, "Filter Nodes By Edges", true);

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
        edgesField = new WholeNumberField(0, 10);
        edgesField.setToolTipText("Edge Number");
        edgesSlider = new JSlider(JSlider.HORIZONTAL);
        edgesSlider.setMinimum(0);
        edgesSlider.setMaximum(100);
        edgesSlider.setMajorTickSpacing(10);
        edgesSlider.setMinorTickSpacing(1);
        edgesSlider.setPaintTicks(true);
        edgesSlider.setPaintLabels(true);
        edgesSlider.addChangeListener(this);
        edgesSlider.addMouseListener( new MouseAdapter()
        {
           @Override
            public void mouseReleased(MouseEvent e)
            {
                if ( ( previewChanges.isSelected() ) && (prevSliderValue != currentSliderValue) )
                {
                    filterNodes();
                    prevSliderValue = currentSliderValue;
                }
            }
        } );
        edgesSlider.addKeyListener( new KeyAdapter()
        {
           @Override
            public void keyReleased(KeyEvent e)
            {
                int keyCode = e.getKeyCode();

                if (  (keyCode == KeyEvent.VK_UP)  || (keyCode == KeyEvent.VK_DOWN) || (keyCode == KeyEvent.VK_LEFT)     || (keyCode == KeyEvent.VK_RIGHT)
                   || (keyCode == KeyEvent.VK_END) || (keyCode == KeyEvent.VK_HOME) || (keyCode == KeyEvent.VK_PAGE_UP)  || (keyCode == KeyEvent.VK_PAGE_DOWN) )
                {
                    if ( previewChanges.isSelected() )
                        filterNodes();
                }
            }
        } );
        edgesSlider.setToolTipText("Edge Number");
        edgesSlider.setValue(0);

        removeSingletonNodes = new JCheckBox("Remove Singleton Nodes");
        removeSingletonNodes.setToolTipText("Remove Singleton Nodes");
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
        JLabel text = new JLabel("Note that a value above 100 Edges can be set in the textbox manually:");
        text.setAlignmentX(Component.CENTER_ALIGNMENT);
        upPanel.add(text);

        JPanel middleUpPanel = new JPanel(true);
        middleUpPanel.add(edgesSlider);
        middleUpPanel.add( Box.createRigidArea( new Dimension(15, 5) ) );
        middleUpPanel.add(edgesField);

        JPanel middleDownPanel = new JPanel(true);
        middleDownPanel.add(removeSingletonNodes);
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
        edgesFilterDialogAction = new AbstractAction("Filter Nodes By Edges")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555741L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                initFilterNodesByEdgesDialog();
            }
        };
        edgesFilterDialogAction.setEnabled(false);

        okAction = new AbstractAction("OK")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555742L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                filterNodes();
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
                float currentSliderValueWhenOpenedDialog = edgesField.getValue();
                if (currentSliderValueWhenOpenedDialog != sliderValueWhenOpenedDialog)
                {
                    edgesSlider.setValue( (int)(100.0f * sliderValueWhenOpenedDialog) );
                    edgesField.setValue(sliderValueWhenOpenedDialog);
                    filterNodes();
                }
                setVisible(false);
            }
        };
    }

    private void initFilterNodesByEdgesDialog()
    {
        layoutFrame.getGraph().getSelectionManager().deselectAll();

        filterNodes();
        sliderValueWhenOpenedDialog = edgesField.getValue();

        setVisible(true);
    }

    private void filterNodes()
    {
        if ( !layoutFrame.getGraph().getGraphNodes().isEmpty() )
        {
            layoutFrame.getGraph().getVisibleEdges().addAll( layoutFrame.getGraph().getGraphEdges() );
            layoutFrame.getGraph().getVisibleNodes().addAll( layoutFrame.getGraph().getGraphNodes() );
            HashSet<GraphEdge> visibleEdges = layoutFrame.getGraph().getVisibleEdges();
            HashSet<GraphNode> visibleNodes = layoutFrame.getGraph().getVisibleNodes();
            HashSet<GraphNode> movedNodes = new HashSet<GraphNode>();
            HashSet<GraphEdge> movedEdges = new HashSet<GraphEdge>();

            int checkCurrentSliderValue = edgesField.getValue();
            for (GraphNode graphNode : visibleNodes)
                if (graphNode.getNodeEdges().size() >= checkCurrentSliderValue)
                    movedNodes.add(graphNode);

            // enable the unhide all and delete hidden actions if a change has been detected
            boolean flag = ( visibleNodes.size() != movedNodes.size() );
            layoutFrame.getCoreSaver().getSaveVisibleAction().setEnabled(flag);
            layoutFrame.getGraph().getSelectionManager().getUnhideAllAction().setEnabled(flag);
            layoutFrame.getGraph().getSelectionManager().getDeleteHiddenAction().setEnabled(flag);

            // better doing the inverse than doing it with < edgesField.getValue() and then visibleEdges.removeAll(movedEdges),
            // avoids an expensive loop and addAll() is using a fast System.arrayCopy()
            visibleNodes.clear();
            visibleNodes.addAll(movedNodes);

            for (GraphEdge graphEdge : visibleEdges)
                if ( (visibleNodes.contains( graphEdge.getNodeFirst() ) && visibleNodes.contains( graphEdge.getNodeSecond() ) ) ) // get rid of edges that do not connect from both sides
                    movedEdges.add(graphEdge);

            visibleEdges.clear();
            visibleEdges.addAll(movedEdges);

            if ( removeSingletonNodes.isSelected() )
            {
                movedNodes = new HashSet<GraphNode>();
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

            layoutFrame.getGraph().updateDisplayLists(true, true, false); // only update edges display list when needed, huge speed-up gain!!!
        }
    }

    public AbstractAction getFilterNodesByEdgesAction()
    {
        return edgesFilterDialogAction;
    }

    @Override
    public void stateChanged(ChangeEvent e)
    {
        currentSliderValue = ( (JSlider)e.getSource() ).getValue();
        edgesField.setValue(currentSliderValue);
    }


}