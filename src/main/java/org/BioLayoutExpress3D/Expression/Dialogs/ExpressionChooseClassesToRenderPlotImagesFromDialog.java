package org.BioLayoutExpress3D.Expression.Dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.Expression.Panels.*;
import org.BioLayoutExpress3D.Utils.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
*
*  A class designed to let the user choose the current ClassSet Classes' plots to be rendered to images.
*
* @author Thanos Theo, 2011
* @version 3.0.0.0
*/

public final class ExpressionChooseClassesToRenderPlotImagesFromDialog extends JDialog implements ActionListener
{
    /**
    *  Serial version UID variable for the LayoutJavaPlatformCapsDialog class.
    */
    public static final long serialVersionUID = 111222333444555696L;

    private JButton okButton = null;
    private JButton cancelButton = null;

    private LayoutFrame layoutFrame = null;
    private ExpressionGraphPanel expressionGraphPanel = null;

    private ClassComboBox startingClassIndexComboBox = null;
    private ClassComboBox endingClassIndexComboBox = null;
    private int startingClassIndex = 0;
    private int endingClassIndex = 0;

    public ExpressionChooseClassesToRenderPlotImagesFromDialog(JFrame jframe, LayoutFrame layoutFrame, ExpressionGraphPanel expressionGraphPanel)
    {
        super(jframe, "Choose Classes To Render Plot Images From", true);

        this.layoutFrame = layoutFrame;
        this.expressionGraphPanel = expressionGraphPanel;

        initComponents();
    }

    private void initComponents()
    {
        JPanel expressionChooseClassesToRenderToPlotImagesDialogPanel = new JPanel(true);
        expressionChooseClassesToRenderToPlotImagesDialogPanel.setLayout( new BoxLayout(expressionChooseClassesToRenderToPlotImagesDialogPanel, BoxLayout.Y_AXIS) );
        TitledBorder chooseStartingEndingClasses = BorderFactory.createTitledBorder("Please Choose Starting & Ending Classes");

        JPanel startingClassIndexPanel = new JPanel(true);
        startingClassIndexPanel.setLayout( new BoxLayout(startingClassIndexPanel, BoxLayout.X_AXIS) );

        JLabel startingClassIndexLabel = new JLabel("Set Starting Class:");
        startingClassIndexLabel.setToolTipText("Set Starting Class");
        startingClassIndexComboBox = new ClassComboBox(layoutFrame.getLayoutClassSetsManager().getCurrentClassSetAllClasses(), false, false); // new WholeNumberField(0, 5);
        startingClassIndexComboBox.setToolTipText("Starting Class");

        startingClassIndexPanel.add(startingClassIndexLabel);
        startingClassIndexPanel.add( Box.createRigidArea( new Dimension(10, 10) ) );
        startingClassIndexPanel.add(startingClassIndexComboBox);

        JPanel endingClassIndexPanel = new JPanel(true);
        endingClassIndexPanel.setLayout( new BoxLayout(endingClassIndexPanel, BoxLayout.X_AXIS) );

        JLabel endingClassIndexLabel = new JLabel("Set Ending Class:  ");
        endingClassIndexLabel.setToolTipText("Set Ending Class");
        endingClassIndexComboBox = new ClassComboBox(layoutFrame.getLayoutClassSetsManager().getCurrentClassSetAllClasses(), false, false); // new WholeNumberField(0, 5);
        endingClassIndexComboBox.setToolTipText("Ending Class");

        endingClassIndexPanel.add(endingClassIndexLabel);
        endingClassIndexPanel.add( Box.createRigidArea( new Dimension(10, 10) ) );
        endingClassIndexPanel.add(endingClassIndexComboBox);

        JPanel chooseStartingEndingClassesPanel = new JPanel(true);
        chooseStartingEndingClassesPanel.setLayout( new BoxLayout(chooseStartingEndingClassesPanel, BoxLayout.Y_AXIS) );
        chooseStartingEndingClassesPanel.add(startingClassIndexPanel);
        chooseStartingEndingClassesPanel.add( Box.createRigidArea( new Dimension(10, 10) ) );
        chooseStartingEndingClassesPanel.add(endingClassIndexPanel);

        okButton = new JButton("OK");
        okButton.addActionListener(this);
        okButton.setToolTipText("OK");
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        cancelButton.setToolTipText("Cancel");

        JPanel okCancelButtonsPanel = new JPanel(true);
        okCancelButtonsPanel.setLayout( new FlowLayout() );
        okCancelButtonsPanel.add(okButton);
        okCancelButtonsPanel.add(cancelButton);

        expressionChooseClassesToRenderToPlotImagesDialogPanel.add( Box.createRigidArea( new Dimension(5, 5) ) );
        addTitledButtonBorder(chooseStartingEndingClasses, chooseStartingEndingClassesPanel, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_JUSTIFICATION, expressionChooseClassesToRenderToPlotImagesDialogPanel);
        expressionChooseClassesToRenderToPlotImagesDialogPanel.add( Box.createRigidArea( new Dimension(5, 5) ) );
        expressionChooseClassesToRenderToPlotImagesDialogPanel.add(okCancelButtonsPanel);
        expressionChooseClassesToRenderToPlotImagesDialogPanel.add( Box.createRigidArea( new Dimension(5, 5) ) );

        this.getContentPane().add(expressionChooseClassesToRenderToPlotImagesDialogPanel);
        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void addTitledButtonBorder(TitledBorder border, JComponent component, String string, int justification, int position, Container container)
    {
        border.setTitleJustification(justification);
        border.setTitlePosition(position);
        addButtonBorder(border, component, string, container);
    }

    private void addButtonBorder(Border border, JComponent component, String string, Container container)
    {
        JPanel newComponent = new JPanel(new FlowLayout(FlowLayout.CENTER), true);
        JLabel label = new JLabel(string, JLabel.RIGHT);
        newComponent.add(component);
        newComponent.add(label);
        newComponent.setBorder(border);
        container.add(newComponent);
    }

    public int getStartingClassIndex()
    {
        return startingClassIndex;
    }

    public int getEndingClassIndex()
    {
        return endingClassIndex;
    }

    @Override
    public void setVisible(boolean state)
    {
        startingClassIndexComboBox.updateClasses( layoutFrame.getLayoutClassSetsManager().getCurrentClassSetAllClasses() );
        startingClassIndexComboBox.setSelectedIndex( layoutFrame.getClassViewerFrame().getClassIndex() );
        endingClassIndexComboBox.updateClasses( layoutFrame.getLayoutClassSetsManager().getCurrentClassSetAllClasses() );
        endingClassIndexComboBox.setSelectedIndex(layoutFrame.getClassViewerFrame().numberOfAllClasses() - 2);

        super.setVisible(state);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if ( e.getActionCommand().equals("OK") )
        {
            startingClassIndex = startingClassIndexComboBox.getSelectedIndex();
            endingClassIndex = endingClassIndexComboBox.getSelectedIndex();
            if (endingClassIndex == layoutFrame.getClassViewerFrame().numberOfAllClasses() - 1)
                endingClassIndex = layoutFrame.getClassViewerFrame().numberOfAllClasses() - 2; // 'No Class' cannot be used!

            if (startingClassIndex > endingClassIndex)
            {
                JOptionPane.showMessageDialog(this, "The Ending Class has to be a number equal or greater than the Starting Class.", "Choose Classes To Render Plot Images From", JOptionPane.INFORMATION_MESSAGE);
                startingClassIndexComboBox.setSelectedIndex( layoutFrame.getClassViewerFrame().getClassIndex() );
                endingClassIndexComboBox.setSelectedIndex(layoutFrame.getClassViewerFrame().numberOfAllClasses() - 2);
            }
            else
            {
                setVisible(false);
                expressionGraphPanel.initiateTakeMultipleClassesScreenShotsProcess();
            }
        }
        else if ( e.getActionCommand().equals("Cancel") )
        {
            setVisible(false);
        }
    }


}