package org.BioLayoutExpress3D.Simulation.Dialogs;

import java.awt.event.*;
import javax.swing.*;
import org.BioLayoutExpress3D.CoreUI.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
*
* SignalingPetriNetSimulationResultsDialog is the class representing the SPN GUI results dialog for the SPN simulation.
*
* @see org.BioLayoutExpress3D.Simulation.Dialogs.SignalingPetriNetSimulationDialog
* @author Thanos Theo, 2009-2010-2011
* @version 3.0.0.0
*
*/

class SignalingPetriNetSimulationResultsDialog extends JDialog implements ActionListener // package access
{

    /**
    *  Serial version UID variable for the SignalingPetriNetSimulationResultsDialog class.
    */
    public static final long serialVersionUID = 111222333444555977L;

    private LayoutFrame layoutFrame = null;

    private JLabel runTimeResultLabel;
    private JLabel timeBlocksResultLabel;
    private JLabel runsResultLabel;
    private JCheckBox saveSPNResultsCheckBox;
    private JCheckBox automaticallySaveSPNResultsToPreChosenFolderCheckBox;
    private JButton saveNowButton;
    private JFileChooser saveSPNResultsFileChooser = null;
    private JButton runAnotherSPNSimulationButton;
    private JButton openSimulationAnimationControlButton;
    private JButton closeButton;

    /**
    *  The constructor of the SignalingPetriNetSimulationResultsDialog class.
    */
    public SignalingPetriNetSimulationResultsDialog(LayoutFrame layoutFrame)
    {
        super(layoutFrame, "SPN Simulation Results", true);

        this.layoutFrame = layoutFrame;

        initComponents();
    }

    /**
    *  This method is called from within the constructor to initialize the SPN results dialog.
    */
    private void initComponents()
    {
        JPanel resultsPanel = new JPanel(true);
        JLabel runTimeLabel = new JLabel("SPN Simulation Run Time:");
        runTimeLabel.setToolTipText("SPN Simulation Run Time");
        runTimeResultLabel = new JLabel("0");
        runTimeResultLabel.setToolTipText("SPN Simulation Run Time");
        JLabel timeBlocksLabel = new JLabel("Number of Time Blocks:");
        timeBlocksLabel.setToolTipText("Number of Time Blocks");
        timeBlocksResultLabel = new JLabel("100");
        timeBlocksResultLabel.setToolTipText("Number of Time Blocks");
        JLabel runsLabel = new JLabel("Number of Runs:");
        runsLabel.setToolTipText("Number of Runs");
        runsResultLabel = new JLabel("500");
        runsResultLabel.setToolTipText("Number of Runs");
        JPanel resultsOptionsPanel = new JPanel(true);
        saveSPNResultsCheckBox = new JCheckBox("Save SPN Results");
        saveSPNResultsCheckBox.addActionListener(this);
        saveSPNResultsCheckBox.setToolTipText("Save SPN Results");
        saveNowButton = new JButton("Save Now");
        saveNowButton.addActionListener(this);
        saveNowButton.setToolTipText("Save Now");
        saveSPNResultsFileChooser = new JFileChooser( SAVE_SPN_RESULTS_FILE_NAME.get() );
        saveSPNResultsFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        saveSPNResultsFileChooser.setDialogTitle("Choose Where to Save the SPN Results Text File");
        automaticallySaveSPNResultsToPreChosenFolderCheckBox = new JCheckBox("Automatically save SPN Results to pre-chosen folder");
        automaticallySaveSPNResultsToPreChosenFolderCheckBox.addActionListener(this);
        automaticallySaveSPNResultsToPreChosenFolderCheckBox.setToolTipText("Automatically save SPN Results to pre-chosen folder");
        JPanel resultsActionsPanel = new JPanel(true);
        runAnotherSPNSimulationButton = new JButton("Run another SPN Simulation");
        runAnotherSPNSimulationButton.addActionListener(this);
        runAnotherSPNSimulationButton.setToolTipText("Run another SPN Simulation");
        openSimulationAnimationControlButton = new JButton("Open Simulation Animation Control");
        openSimulationAnimationControlButton.addActionListener(this);
        openSimulationAnimationControlButton.setToolTipText("Open Simulation Animation Control");
        closeButton = new JButton("Close SPN Simulation Results");
        closeButton.addActionListener(this);
        closeButton.setToolTipText("Close SPN Simulation Results");

        resultsPanel.setBorder( BorderFactory.createTitledBorder("SPN Simulation Results") );

        GroupLayout resultsPanelLayout = new GroupLayout(resultsPanel);
        resultsPanel.setLayout(resultsPanelLayout);
        resultsPanelLayout.setHorizontalGroup(
            resultsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(resultsPanelLayout.createSequentialGroup()
                .addGap(84, 84, 84)
                .addGroup(resultsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(resultsPanelLayout.createSequentialGroup()
                        .addComponent(runTimeLabel)
                        .addGap(18, 18, 18)
                        .addComponent(runTimeResultLabel))
                    .addGroup(resultsPanelLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(resultsPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(runsLabel)
                            .addComponent(timeBlocksLabel))
                        .addGroup(resultsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(resultsPanelLayout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addComponent(timeBlocksResultLabel))
                            .addGroup(resultsPanelLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(runsResultLabel)))))
                .addContainerGap(145, Short.MAX_VALUE))
        );
        resultsPanelLayout.setVerticalGroup(
            resultsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(resultsPanelLayout.createSequentialGroup()
                .addGroup(resultsPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(runTimeLabel, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                    .addComponent(runTimeResultLabel))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(resultsPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(timeBlocksResultLabel)
                    .addComponent(timeBlocksLabel))
                .addGap(13, 13, 13)
                .addGroup(resultsPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(runsLabel)
                    .addComponent(runsResultLabel))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        resultsOptionsPanel.setBorder( BorderFactory.createTitledBorder("Save SPN Simulation Results Options") );

        GroupLayout resultsOptionsPanelLayout = new GroupLayout(resultsOptionsPanel);
        resultsOptionsPanel.setLayout(resultsOptionsPanelLayout);
        resultsOptionsPanelLayout.setHorizontalGroup(
            resultsOptionsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(resultsOptionsPanelLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(resultsOptionsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(automaticallySaveSPNResultsToPreChosenFolderCheckBox)
                    .addGroup(resultsOptionsPanelLayout.createSequentialGroup()
                        .addComponent(saveSPNResultsCheckBox)
                        .addGap(18, 18, 18)
                        .addComponent(saveNowButton)))
                .addContainerGap(91, Short.MAX_VALUE))
        );
        resultsOptionsPanelLayout.setVerticalGroup(
            resultsOptionsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(resultsOptionsPanelLayout.createSequentialGroup()
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(resultsOptionsPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(saveSPNResultsCheckBox)
                    .addComponent(saveNowButton))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(automaticallySaveSPNResultsToPreChosenFolderCheckBox))
        );

        resultsActionsPanel.setBorder( BorderFactory.createTitledBorder("SPN Simulation Results Actions") );

        GroupLayout resultsActionsPanelLayout = new GroupLayout(resultsActionsPanel);
        resultsActionsPanel.setLayout(resultsActionsPanelLayout);
        resultsActionsPanelLayout.setHorizontalGroup(
            resultsActionsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(resultsActionsPanelLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(runAnotherSPNSimulationButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(openSimulationAnimationControlButton)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        resultsActionsPanelLayout.setVerticalGroup(
            resultsActionsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(resultsActionsPanelLayout.createSequentialGroup()
                .addGroup(resultsActionsPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(runAnotherSPNSimulationButton)
                    .addComponent(openSimulationAnimationControlButton))
                .addContainerGap(6, Short.MAX_VALUE))
        );

        GroupLayout layout = new GroupLayout( this.getContentPane() );
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, true)
                            .addComponent(resultsPanel,        GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(resultsOptionsPanel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(resultsActionsPanel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(117, 117, 117)
                        .addComponent(closeButton)))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(resultsPanel,        GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resultsOptionsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resultsActionsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeButton)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        this.setResizable(false);
        this.pack();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.addWindowListener( new WindowAdapter()
        {
           @Override
            public void windowClosing(WindowEvent e)
            {
                closeDialogWindow();
            }
        } );
    }

    /**
    *  Sets the results labels' texts.
    */
    public void setResultLabelsText(String text1, String text2, String text3, float value)
    {
        runTimeResultLabel.setText(text1);
        timeBlocksResultLabel.setText(text2);
        runsResultLabel.setText(text3);

        layoutFrame.getLayoutAnimationControlDialog().setMaxValueInTextField(value);
    }

    /**
    *  Closes the SPN dialog window.
    */
    private void closeDialogWindow()
    {
        this.setVisible(false);
    }

    /**
    *  Opens the SPN dialog window.
    */
    public void openDialogWindow()
    {
        saveSPNResultsCheckBox.setSelected( SAVE_SPN_RESULTS.get() );
        automaticallySaveSPNResultsToPreChosenFolderCheckBox.setSelected( AUTOMATICALLY_SAVE_SPN_RESULTS_TO_PRECHOSEN_FOLDER.get() );
        if ( SAVE_SPN_RESULTS.get() )
        {
            automaticallySaveSPNResultsToPreChosenFolderCheckBox.setEnabled(true);
            saveNowButton.setEnabled( !AUTOMATICALLY_SAVE_SPN_RESULTS_TO_PRECHOSEN_FOLDER.get() );
        }
        else
        {
            automaticallySaveSPNResultsToPreChosenFolderCheckBox.setEnabled(false);
            saveNowButton.setEnabled(false);
        }

        this.setVisible(true);
    }

    /**
    *  Executes the actionePerformed() callback for the SPN dialog window.
    */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        if ( e.getSource().equals(saveSPNResultsCheckBox) )
        {
            SAVE_SPN_RESULTS.set( saveSPNResultsCheckBox.isSelected() );
            if ( SAVE_SPN_RESULTS.get() )
            {
                automaticallySaveSPNResultsToPreChosenFolderCheckBox.setEnabled(true);
                saveNowButton.setEnabled( !AUTOMATICALLY_SAVE_SPN_RESULTS_TO_PRECHOSEN_FOLDER.get() );
            }
            else
            {
                automaticallySaveSPNResultsToPreChosenFolderCheckBox.setEnabled(false);
                saveNowButton.setEnabled(false);
            }
            layoutFrame.getLayoutGraphPropertiesDialog().setHasNewPreferencesBeenApplied(true);
        }
        else if ( e.getSource().equals(automaticallySaveSPNResultsToPreChosenFolderCheckBox) )
        {
            AUTOMATICALLY_SAVE_SPN_RESULTS_TO_PRECHOSEN_FOLDER.set( automaticallySaveSPNResultsToPreChosenFolderCheckBox.isSelected() );
            saveNowButton.setEnabled( !AUTOMATICALLY_SAVE_SPN_RESULTS_TO_PRECHOSEN_FOLDER.get() );
            layoutFrame.getLayoutGraphPropertiesDialog().setHasNewPreferencesBeenApplied(true);
        }
        else if ( e.getSource().equals(saveNowButton) )
        {
            if ( JFileChooser.APPROVE_OPTION == saveSPNResultsFileChooser.showOpenDialog(this) )
            {
                SAVE_SPN_RESULTS_FILE_NAME.set( saveSPNResultsFileChooser.getSelectedFile().getPath() );

                Thread runLightWeightThread = new Thread( new Runnable()
                {

                    @Override
                    public void run()
                    {
                        closeDialogWindow();
                        layoutFrame.getSignalingPetriNetSimulationDialog().SPNSimulationResultsWriteToFile();
                        openDialogWindow();
                    }


                } );

                runLightWeightThread.setPriority(Thread.NORM_PRIORITY);
                runLightWeightThread.start();
            }
        }
        else if ( e.getSource().equals(runAnotherSPNSimulationButton) )
        {
            closeDialogWindow();
            layoutFrame.getSignalingPetriNetSimulationDialog().openDialogWindow();

        }
        else if ( e.getSource().equals(openSimulationAnimationControlButton) )
        {
            closeDialogWindow();
            layoutFrame.getLayoutAnimationControlDialog().openDialogWindow();
            layoutFrame.checkToShowNavigationWizardOnStartup();
        }
        else if ( e.getSource().equals(closeButton) )
        {
            closeDialogWindow();
            layoutFrame.checkToShowNavigationWizardOnStartup();
        }
    }


}
