package org.BioLayoutExpress3D.Simulation.Dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.Network.*;
import org.BioLayoutExpress3D.Simulation.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import org.BioLayoutExpress3D.Utils.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
*
* SignalingPetriNetSimulationDialog is the class representing the SPN GUI dialog for the SPN simulation.
*
* @see org.BioLayoutExpress3D.Simulation.SignalingPetriNetSimulation
* @see org.BioLayoutExpress3D.Simulation.Dialogs.SignalingPetriNetSimulationResultsDialog
* @author Benjamin Boyer, code updates/optimizations/modifications Thanos Theo, 2009-2010-2011
* @version 3.0.0.0
*
*/

public class SignalingPetriNetSimulationDialog extends JDialog implements ActionListener
{

    /**
    *  Serial version UID variable for the SignalingPetriNetSimulationDialog class.
    */
    public static final long serialVersionUID = 111222333444555777L;

    private LayoutFrame layoutFrame = null;

    private JTextField totalTimeBlocksTextField = null;
    private JTextField totalRunsField = null;
    private JCheckBox errorCheckBox = null;
    private JRadioButton stddevErrorTypeRadioButton = null;
    private JRadioButton stderrErrorTypeRadioButton = null;
    private JRadioButton useUniformDistributionRadioButton = null;
    private JRadioButton useStandardNormalDistributionRadioButton = null;
    private JRadioButton useDeterministicProcessRadioButton = null;
    private JRadioButton useConsumptiveTransitionsRadioButton = null;
    private JRadioButton useOriginalTransitionsRadioButton = null;
    private JButton runSimulationButton = null;
    private JButton cancelButton = null;

    private SignalingPetriNetSimulation SPNSimulation = null;
    private SignalingPetriNetSimulationResultsDialog SPNSimulationResultsDialog = null;

    private int totalTimeBlocks = 100;
    private int totalRuns = 500;

    private AbstractAction signalingPetriNetSimulationDialogAction = null;

    /**
    *  The constructor of the SignalingPetriNetSimulationDialog class.
    */
    public SignalingPetriNetSimulationDialog(NetworkContainer nc, LayoutFrame layoutFrame)
    {
        super(layoutFrame, "SPN Simulation", true);

        this.layoutFrame = layoutFrame;

        SPNSimulation = new SignalingPetriNetSimulation(nc, layoutFrame);
        SPNSimulationResultsDialog = new SignalingPetriNetSimulationResultsDialog(layoutFrame);

        initActions();
        initComponents();
    }

    private void initActions()
    {
        signalingPetriNetSimulationDialogAction = new AbstractAction("Run SPN Simulation")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111252333444555685L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                layoutFrame.layoutGeneralToolBarRunSPNButtonResetRolloverState();
                if ( layoutFrame.getNetworkRootContainer().getIsPetriNet() )
                    openDialogWindow();
                else
                    JOptionPane.showMessageDialog(layoutFrame, "No Signaling Petri Net (SPN) Pathway Loaded!", "Signaling Petri Net (SPN) Simulation", JOptionPane.INFORMATION_MESSAGE);
            }
        };
        signalingPetriNetSimulationDialogAction.setEnabled(false);
    }

    /**
    *  This method is called from within the constructor to initialize the SPN dialog.
    */
    private void initComponents()
    {
        JPanel simulationOptionsPanel = new JPanel(true);
        JLabel timeBlocksLabel = new JLabel("Number of Time Blocks:");
        timeBlocksLabel.setToolTipText("Number of Time Blocks");
        JLabel runsLabel = new JLabel("Number of Runs:");
        runsLabel.setToolTipText("Number of Runs");
        errorCheckBox = new JCheckBox("Calculate Variance");
        errorCheckBox.setSelected(false);
        errorCheckBox.addActionListener(this);
        ButtonGroup errorTypeButtonGroup = new ButtonGroup();
        stddevErrorTypeRadioButton = new JRadioButton("Standard Deviation");
        stddevErrorTypeRadioButton.setSelected(true);
        stddevErrorTypeRadioButton.setEnabled(false);
        stderrErrorTypeRadioButton = new JRadioButton("Standard Error");
        stderrErrorTypeRadioButton.setEnabled(false);
        errorTypeButtonGroup.add(stddevErrorTypeRadioButton);
        errorTypeButtonGroup.add(stderrErrorTypeRadioButton);
        totalTimeBlocksTextField = new JTextField();
        totalTimeBlocksTextField.setDocument( new TextFieldFilter(TextFieldFilter.NUMERIC) );
        totalTimeBlocksTextField.setToolTipText("Number of Time Blocks");
        totalRunsField = new JTextField();
        totalRunsField.setDocument( new TextFieldFilter(TextFieldFilter.NUMERIC) );
        totalRunsField.setToolTipText("Number of Runs");
        JPanel simulationStochasticOptionsPanel = new JPanel(true);
        JLabel SPNDistributionLabel = new JLabel("Choose SPN Stochastic Distribution:");
        SPNDistributionLabel.setToolTipText("Choose SPN Stochastic Distribution");
        useUniformDistributionRadioButton = new JRadioButton("   Uniform Distribution");
        useUniformDistributionRadioButton.addActionListener(this);
        useUniformDistributionRadioButton.setToolTipText("Uniform Distribution");
        useStandardNormalDistributionRadioButton = new JRadioButton("   Standard Normal Distribution");
        useStandardNormalDistributionRadioButton.addActionListener(this);
        useStandardNormalDistributionRadioButton.setToolTipText("Standard Normal Distribution");
        useDeterministicProcessRadioButton = new JRadioButton("   Deterministic Process (P(A) = 0.5)");
        useDeterministicProcessRadioButton.addActionListener(this);
        useDeterministicProcessRadioButton.setToolTipText("Deterministic Process (P(A) = 0.5)");
        ButtonGroup SPNDistributionsButtonGroup = new ButtonGroup();
        SPNDistributionsButtonGroup.add(useUniformDistributionRadioButton);
        SPNDistributionsButtonGroup.add(useStandardNormalDistributionRadioButton);
        SPNDistributionsButtonGroup.add(useDeterministicProcessRadioButton);
        useUniformDistributionRadioButton.setSelected(true);
        JPanel simulationTransitionOptionsPanel = new JPanel(true);
        JLabel SPNTransitionLabel = new JLabel("Choose SPN Transition Type:");
        SPNTransitionLabel.setToolTipText("Choose SPN Transition Type");
        useConsumptiveTransitionsRadioButton = new JRadioButton("   Consumptive Transitions");
        useConsumptiveTransitionsRadioButton.addActionListener(this);
        useConsumptiveTransitionsRadioButton.setToolTipText("Consumptive Transitions");
        useOriginalTransitionsRadioButton = new JRadioButton("   Original Transitions");
        useOriginalTransitionsRadioButton.addActionListener(this);
        useOriginalTransitionsRadioButton.setToolTipText("Original Transitions");
        ButtonGroup SPNTransitionsButtonGroup = new ButtonGroup();
        SPNTransitionsButtonGroup.add(useConsumptiveTransitionsRadioButton);
        SPNTransitionsButtonGroup.add(useOriginalTransitionsRadioButton);
        useConsumptiveTransitionsRadioButton.setSelected(true);
        JPanel buttonPanel = new JPanel(true);
        runSimulationButton = new JButton("Run SPN Simulation");
        runSimulationButton.addActionListener(this);
        runSimulationButton.setToolTipText("Run SPN Simulation");
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        cancelButton.setToolTipText("Cancel");

        simulationOptionsPanel.setBorder( BorderFactory.createTitledBorder("SPN Simulation Options") );
        simulationOptionsPanel.setLayout( new BoxLayout(simulationOptionsPanel, BoxLayout.Y_AXIS) );
        GroupLayout simulationOptionsPanelLayout = new GroupLayout(simulationOptionsPanel);
        simulationOptionsPanel.setLayout(simulationOptionsPanelLayout);
        simulationOptionsPanelLayout.setHorizontalGroup(
            simulationOptionsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(simulationOptionsPanelLayout.createSequentialGroup()
                .addGroup(simulationOptionsPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(runsLabel)
                    .addComponent(timeBlocksLabel)
                )
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(simulationOptionsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(totalTimeBlocksTextField, GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                    .addComponent(totalRunsField, GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                )
                .addContainerGap(42, Short.MAX_VALUE)
            )
            .addComponent(errorCheckBox)
            .addComponent(stddevErrorTypeRadioButton)
            .addComponent(stderrErrorTypeRadioButton)
        );
        simulationOptionsPanelLayout.setVerticalGroup(
            simulationOptionsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(simulationOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(simulationOptionsPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(timeBlocksLabel)
                    .addComponent(totalTimeBlocksTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                )
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(simulationOptionsPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(runsLabel)
                    .addComponent(totalRunsField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                )
                .addContainerGap(11, Short.MAX_VALUE)
                .addComponent(errorCheckBox)
                .addContainerGap(11, Short.MAX_VALUE)
                .addComponent(stddevErrorTypeRadioButton)
                .addComponent(stderrErrorTypeRadioButton)
                .addContainerGap(11, Short.MAX_VALUE)
            )
        );

        simulationStochasticOptionsPanel.setBorder( BorderFactory.createTitledBorder("SPN Simulation Stochastic Options") );
        simulationStochasticOptionsPanel.setLayout( new BoxLayout(simulationStochasticOptionsPanel, BoxLayout.Y_AXIS) );
        simulationStochasticOptionsPanel.add( Box.createRigidArea( new Dimension(10, 10) ) );
        simulationStochasticOptionsPanel.add(SPNDistributionLabel);
        simulationStochasticOptionsPanel.add( Box.createRigidArea( new Dimension(10, 10) ) );
        simulationStochasticOptionsPanel.add(useUniformDistributionRadioButton);
        simulationStochasticOptionsPanel.add(useStandardNormalDistributionRadioButton);
        simulationStochasticOptionsPanel.add(useDeterministicProcessRadioButton);
        simulationStochasticOptionsPanel.add( Box.createRigidArea( new Dimension(10, 10) ) );

        simulationTransitionOptionsPanel.setBorder( BorderFactory.createTitledBorder("SPN Simulation Transition Types") );
        simulationTransitionOptionsPanel.setLayout( new BoxLayout(simulationTransitionOptionsPanel, BoxLayout.Y_AXIS) );
        simulationTransitionOptionsPanel.add( Box.createRigidArea( new Dimension(10, 10) ) );
        simulationTransitionOptionsPanel.add(SPNTransitionLabel);
        simulationTransitionOptionsPanel.add( Box.createRigidArea( new Dimension(10, 10) ) );
        simulationTransitionOptionsPanel.add(useConsumptiveTransitionsRadioButton);
        simulationTransitionOptionsPanel.add(useOriginalTransitionsRadioButton);
        simulationTransitionOptionsPanel.add( Box.createRigidArea( new Dimension(10, 10) ) );

        buttonPanel.setBorder( BorderFactory.createTitledBorder("SPN Simulation Actions") );
        GroupLayout buttonPanelLayout = new GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(runSimulationButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(buttonPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(runSimulationButton)
                    .addComponent(cancelButton))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        GroupLayout layout = new GroupLayout( this.getContentPane() );
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(130, 130, 130)
            .addComponent(simulationOptionsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(simulationStochasticOptionsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(simulationTransitionOptionsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(buttonPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGap(130, 130, 130)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
            .addContainerGap()
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(simulationOptionsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(simulationStochasticOptionsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(simulationTransitionOptionsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED))
        );

        totalTimeBlocksTextField.setText( Integer.toString(totalTimeBlocks) );
        totalRunsField.setText( Integer.toString(totalRuns) );

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
        if ( getSPNDistributionType( USE_SPN_DISTRIBUTION_TYPE.get() ).equals(SPNDistributionTypes.UNIFORM) )
        {
            useUniformDistributionRadioButton.setSelected(true);
            useStandardNormalDistributionRadioButton.setSelected(false);
            useDeterministicProcessRadioButton.setSelected(false);
        }
        else if ( getSPNDistributionType( USE_SPN_DISTRIBUTION_TYPE.get() ).equals(SPNDistributionTypes.STANDARD_NORMAL) )
        {
            useUniformDistributionRadioButton.setSelected(false);
            useStandardNormalDistributionRadioButton.setSelected(true);
            useDeterministicProcessRadioButton.setSelected(false);
        }
        else if ( getSPNDistributionType( USE_SPN_DISTRIBUTION_TYPE.get() ).equals(SPNDistributionTypes.DETERMINISTIC_PROCESS) )
        {
            useUniformDistributionRadioButton.setSelected(false);
            useStandardNormalDistributionRadioButton.setSelected(false);
            useDeterministicProcessRadioButton.setSelected(true);
        }

        if ( getSPNTransitionType( USE_SPN_TRANSITION_TYPE.get() ).equals(SPNTransitionTypes.CONSUMPTIVE) )
        {
            useConsumptiveTransitionsRadioButton.setSelected(true);
            useOriginalTransitionsRadioButton.setSelected(false);
        }
        else if ( getSPNTransitionType( USE_SPN_TRANSITION_TYPE.get() ).equals(SPNTransitionTypes.ORIGINAL) )
        {
            useConsumptiveTransitionsRadioButton.setSelected(false);
            useOriginalTransitionsRadioButton.setSelected(true);
        }

        this.setVisible(true);
    }

    /**
    *  Gets the SPN distribution type.
    */
    private SPNDistributionTypes getSPNDistributionType(String SPNDistributionTypeString)
    {
        SPNDistributionTypes[] allSPNDistributionTypes = SPNDistributionTypes.values();
        for (int i = 0; i < allSPNDistributionTypes.length; i++)
            if ( SPNDistributionTypeString.equals( allSPNDistributionTypes[i].toString() ) )
                return allSPNDistributionTypes[i];

        return SPN_DEFAULT_DISTRIBUTION_TYPE;
    }

    /**
    *  Gets the SPN transition type.
    */
    private SPNTransitionTypes getSPNTransitionType(String SPNTransitionTypeString)
    {
        SPNTransitionTypes[] allSPNTransitionTypes = SPNTransitionTypes.values();
        for (int i = 0; i < allSPNTransitionTypes.length; i++)
            if ( SPNTransitionTypeString.equals( allSPNTransitionTypes[i].toString() ) )
                return allSPNTransitionTypes[i];

        return SPN_DEFAULT_TRANSITION_TYPE;
    }

    /**
    *  Gets the number of time blocks.
    */
    public int getTimeBlocks()
    {
        return totalTimeBlocks;
    }

    /**
    *  Gets the number of runs.
    */
    public int getRuns()
    {
        return totalRuns;
    }

    /**
    *  Gets the simulation cloned results.
    */
    public SignalingPetriNetSimulation.SpnResult getSPNSimulationResults()
    {
        return SPNSimulation.getSPNSimulationResults();
    }

    /**
    *  Gets the max value from the results array.
    */
    public float findMaxValueFromResultsArray()
    {
        return SPNSimulation.findMaxValueFromResultsArray();
    }

    /**
    *  Initializes the results array.
    *  To be used from the SignalingPetriNetLoadSimulation class.
    */
    public void initializeResultsArray(int numberOfVertices, int totalTimeBlocks, int totalRuns,
            SignalingPetriNetSimulation.ErrorType errorType)
    {
        this.totalTimeBlocks = totalTimeBlocks;
        this.totalRuns = totalRuns;

        SPNSimulation.initializeResultsArray(numberOfVertices, totalTimeBlocks, errorType);
    }

    /**
    *  Adds a result to the result array.
    *  To be used from the SignalingPetriNetLoadSimulation class.
    */
    public void addResultToResultsArray(int nodeID, int timeBlock, float result, float error)
    {
        SPNSimulation.addResultToResultsArray(nodeID, timeBlock, result, error);
    }

    /**
    *  Writes all the SPN simulation results in a file.
    */
    public void SPNSimulationResultsWriteToFile()
    {
        SPNSimulation.SPNSimulationResultsWriteToFile(totalTimeBlocks, totalRuns);
    }

    /**
    *  Executes the actionePerformed() callback for the SPN dialog window.
    */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        if ( e.getSource().equals(useUniformDistributionRadioButton) )
        {
            USE_SPN_DISTRIBUTION_TYPE.set( SPNDistributionTypes.UNIFORM.toString() );
            layoutFrame.getLayoutGraphPropertiesDialog().setHasNewPreferencesBeenApplied(true);
        }
        else if ( e.getSource().equals(useStandardNormalDistributionRadioButton) )
        {
            USE_SPN_DISTRIBUTION_TYPE.set( SPNDistributionTypes.STANDARD_NORMAL.toString() );
            layoutFrame.getLayoutGraphPropertiesDialog().setHasNewPreferencesBeenApplied(true);
        }
        else if ( e.getSource().equals(useDeterministicProcessRadioButton) )
        {
            USE_SPN_DISTRIBUTION_TYPE.set( SPNDistributionTypes.DETERMINISTIC_PROCESS.toString() );
            layoutFrame.getLayoutGraphPropertiesDialog().setHasNewPreferencesBeenApplied(true);
        }
        else if ( e.getSource().equals(useConsumptiveTransitionsRadioButton) )
        {
            USE_SPN_TRANSITION_TYPE.set( SPNTransitionTypes.CONSUMPTIVE.toString() );
            layoutFrame.getLayoutGraphPropertiesDialog().setHasNewPreferencesBeenApplied(true);
        }
        else if ( e.getSource().equals(useOriginalTransitionsRadioButton) )
        {
            USE_SPN_TRANSITION_TYPE.set( SPNTransitionTypes.ORIGINAL.toString() );
            layoutFrame.getLayoutGraphPropertiesDialog().setHasNewPreferencesBeenApplied(true);
        }
        else if ( e.getSource().equals(errorCheckBox))
        {
            stddevErrorTypeRadioButton.setEnabled(errorCheckBox.isSelected());
            stderrErrorTypeRadioButton.setEnabled(errorCheckBox.isSelected());
        }
        else if ( e.getSource().equals(runSimulationButton) )
        {
            final String totalTimeBlocksString = totalTimeBlocksTextField.getText();
            final String totalRunsString = totalRunsField.getText();
            final SignalingPetriNetSimulation.ErrorType errorType;

            if (errorCheckBox.isSelected())
            {
                if (stddevErrorTypeRadioButton.isSelected())
                {
                    errorType = SignalingPetriNetSimulation.ErrorType.STDDEV;
                }
                else
                {
                    errorType = SignalingPetriNetSimulation.ErrorType.STDERR;
                }
            }
            else
            {
                errorType = SignalingPetriNetSimulation.ErrorType.NONE;
            }

            if ( totalTimeBlocksString.isEmpty() || totalRunsString.isEmpty() )
            {
                JOptionPane.showMessageDialog(this, "Please Enter Values In Both Textboxes.", "SPN Simulation", JOptionPane.INFORMATION_MESSAGE);
            }
            else
            {
                closeDialogWindow();

                totalTimeBlocks = Integer.parseInt(totalTimeBlocksString);
                totalRuns = Integer.parseInt(totalRunsString);

                Thread runLightWeightThread = new Thread( new Runnable()
                {

                    @Override
                    public void run()
                    {
                        SPNSimulation.executeSPNSimulation(totalTimeBlocks, totalRuns, errorType);
                        layoutFrame.getLayoutAnimationControlDialog().getAnimationControlDialogAction().setEnabled(true);
                        String[] timeResults = Time.convertMSecsToTimeString(SPNSimulation.getTimeTaken() / 1000000).split(" ");
                        SPNSimulationResultsDialog.setResultLabelsText( timeResults[0] + " secs " + timeResults[1] + " msecs", totalTimeBlocksString, totalRunsString, layoutFrame.getSignalingPetriNetSimulationDialog().findMaxValueFromResultsArray() );
                        SPNSimulationResultsDialog.openDialogWindow();
                    }


                } );

                runLightWeightThread.setPriority(Thread.NORM_PRIORITY);
                runLightWeightThread.start();

            }
        }
        else if ( e.getSource().equals(cancelButton) )
        {
            closeDialogWindow();
            layoutFrame.checkToShowNavigationWizardOnStartup();
        }
    }

    public AbstractAction getSignalingPetriNetSimulationDialogAction()
    {
        return signalingPetriNetSimulationDialogAction;
    }


}