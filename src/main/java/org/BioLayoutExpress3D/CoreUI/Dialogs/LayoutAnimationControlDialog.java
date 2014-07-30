package org.BioLayoutExpress3D.CoreUI.Dialogs;

import java.awt.*;
import java.awt.event.*;
import java.math.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import org.BioLayoutExpress3D.Utils.*;
import static org.BioLayoutExpress3D.Environment.AnimationEnvironment.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
*
* LayoutAnimationControlDialog is the class representing the animation control dialog.
*
* @author Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public class LayoutAnimationControlDialog extends JDialog implements ActionListener, CaretListener
{

    /**
    *  Serial version UID variable for the LayoutAnimationControlDialog class.
    */
    public static final long serialVersionUID = 111222333444555987L;

    private static final String ANIMATION_CONTROL_DIALOG_TITLE = "Animation Control";
    private static final String[] TICKS_PER_SEC_STRING = { "Entity/Sec: ", "TB/Sec: " };
    private static final String NODE_SIZE_STRING = "Node Size: ";
    private static final int MAX_RATE_PER_SEC = 60;

    private LayoutFrame layoutFrame = null;
    private JFileChooser spectrumImageFileChooser = null;

    private JLabel entitiesOrTimeBlocksPerSecondLabel = null;
    private JLabel startFromEntityOrTimeBlockLabel = null;
    private JLabel setMaxNodeSizeLabel = null;
    private JLabel setMaxValueLabel = null;
    private JCheckBox setFixedMaxValueCheckBox = null;
    private JLabel minValueColorLabel = null;
    private JLabel maxValueColorLabel = null;
    private JCheckBox perNodeMaxValueOrmEPNComponentsCheckbox = null;
    private JCheckBox selectedNodesCheckbox = null;
    private JCheckBox showNodeAnimationValueCheckbox = null;
    private JRadioButton fluidTransitionOffRadioButton = null;
    private JRadioButton fluidTransitionLinearRadioButton = null;
    private JRadioButton fluidTransitionPolynomialRadioButton = null;
    private JCheckBox useColorPaletteSpectrumTransitionCheckBox = null;
    private JCheckBox useRealMaxValueForColorTransitionCheckBox = null;
    private JCheckBox useImageAsSpectrumCheckBox = null;
    private JComboBox<String> entitiesOrTimeBlocksPerSecondComboBox = null;
    private JComboBox<String> setMaxNodeSizeComboBox = null;
    private JTextField startFromEntityOrTimeBlockTextField = null;
    private FloatNumberField maxValueFoundTextField = null;
    private JComboBox<String> imageSpectrumFileNamesComboBox = null;
    private ColorButton minSpectrumColorButton = null;
    private ColorButton maxSpectrumColorButton = null;
    private JButton loadImageFileButton = null;
    private JButton defaultImageFileButton = null;
    private JButton startAnimationButton = null;
    private JButton pauseAnimationButton = null;
    private JButton stepAnimationButton = null;
    private JButton stopAnimationButton = null;
    private JButton closeButton = null;

    private boolean isExpressionProfileAnimationMode = true;
    private boolean setFixedMaxValueCheckBoxState = false;
    private String maxValueFoundString = "";
    private boolean pauseResumeButtonState = false;
    private AbstractAction animationControlDialogAction = null;

    /**
    *  The constructor of the LayoutAnimationControlDialog class.
    */
    public LayoutAnimationControlDialog(LayoutFrame layoutFrame)
    {
        super(layoutFrame, ANIMATION_CONTROL_DIALOG_TITLE, false);

        this.layoutFrame = layoutFrame;

        initSpectrumImageFileChooser();
        initActions();
        initComponents();
        initDialog();
    }

    /**
    *  This method is called from within the constructor to initialize the Spectrum Image File Chooser.
    */
    private void initSpectrumImageFileChooser()
    {
        FileNameExtensionFilter nodeTextureFileNameExtensionFilter = new FileNameExtensionFilter("Load A Spectrum Image File", "gif", "jpg", "jpeg", "png");
        String loadSpectrumImageFilePath = FILE_CHOOSER_PATH.get().substring(0, FILE_CHOOSER_PATH.get().lastIndexOf( System.getProperty("file.separator") ) + 1);
        spectrumImageFileChooser = new JFileChooser(loadSpectrumImageFilePath);
        spectrumImageFileChooser.setDialogTitle("Choose A Spectrum Image File");
        spectrumImageFileChooser.setFileFilter(nodeTextureFileNameExtensionFilter);
    }

    /**
    *  Initializes all actions.
    */
    private void initActions()
    {
        animationControlDialogAction = new AbstractAction("Animation Control")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111252333444555689L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                layoutFrame.layoutGeneralToolBarAnimationControlButtonResetRolloverState();
                if ( animationControlDialogAction.isEnabled() )
                    openDialogWindow();
                else
                    JOptionPane.showMessageDialog(layoutFrame, "Relevant Graph Data To Animate Not Provided!", "Animation Control", JOptionPane.INFORMATION_MESSAGE);
            }
        };
        animationControlDialogAction.setEnabled(false);
    }

    /**
    *  Initializes the animation control dialog components.
    */
    private void initComponents()
    {
        JPanel nodeAnimationPanel = new JPanel(true);
        perNodeMaxValueOrmEPNComponentsCheckbox = (isExpressionProfileAnimationMode) ? new JCheckBox("   Per Node Max Value") : new JCheckBox("   mEPN Components Animation Only");
        if (isExpressionProfileAnimationMode)
            perNodeMaxValueOrmEPNComponentsCheckbox.setToolTipText("Per Node Max Value");
        else
            perNodeMaxValueOrmEPNComponentsCheckbox.setToolTipText("mEPN Components Animation Only");
        perNodeMaxValueOrmEPNComponentsCheckbox.setSelected( isExpressionProfileAnimationMode ? ANIMATION_PER_NODE_MAX_VALUE.get() : ANIMATION_MEPN_COMPONENTS_ANIMATION_ONLY.get() );
        perNodeMaxValueOrmEPNComponentsCheckbox.addActionListener(this);
        selectedNodesCheckbox = new JCheckBox("   Selected Nodes Animation Only");
        selectedNodesCheckbox.setToolTipText("Selected Nodes Animation Only");
        selectedNodesCheckbox.setEnabled(false);
        selectedNodesCheckbox.setSelected( ANIMATION_SELECTED_NODES_ANIMATION_ONLY.get() );
        selectedNodesCheckbox.addActionListener(this);
        showNodeAnimationValueCheckbox = new JCheckBox("   Show Node Animation Value");
        showNodeAnimationValueCheckbox.setToolTipText("Show Node Animation Value");
        showNodeAnimationValueCheckbox.setSelected( ANIMATION_SHOW_NODE_ANIMATION_VALUE.get() );
        showNodeAnimationValueCheckbox.addActionListener(this);
        JLabel fluidTransitionLabel = new JLabel("Fluid Node Transition:");
        fluidTransitionLabel.setToolTipText("Fluid Node Transition");
        fluidTransitionOffRadioButton = new JRadioButton("  Discrete");
        fluidTransitionOffRadioButton.setToolTipText("Discrete");
        fluidTransitionOffRadioButton.addActionListener(this);
        fluidTransitionLinearRadioButton = new JRadioButton("  Linear");
        fluidTransitionLinearRadioButton.setToolTipText("Linear");
        fluidTransitionLinearRadioButton.addActionListener(this);
        fluidTransitionPolynomialRadioButton = new JRadioButton("  Polynomial");
        fluidTransitionPolynomialRadioButton.setToolTipText("Polynomial");
        fluidTransitionPolynomialRadioButton.addActionListener(this);
        ButtonGroup fluidTransitionsButtonGroup = new ButtonGroup();
        fluidTransitionsButtonGroup.add(fluidTransitionOffRadioButton);
        fluidTransitionsButtonGroup.add(fluidTransitionLinearRadioButton);
        fluidTransitionsButtonGroup.add(fluidTransitionPolynomialRadioButton);
        if (ANIMATION_FLUID_TRANSITION_TYPE.get() == 1)
            fluidTransitionOffRadioButton.setSelected(true);
        else if(ANIMATION_FLUID_TRANSITION_TYPE.get() == 2)
            fluidTransitionLinearRadioButton.setSelected(true);
        else if(ANIMATION_FLUID_TRANSITION_TYPE.get() == 3)
            fluidTransitionPolynomialRadioButton.setSelected(true);
        JPanel timingPanel = new JPanel(true);
        entitiesOrTimeBlocksPerSecondLabel = (isExpressionProfileAnimationMode) ? new JLabel("Entities Per Second:") : new JLabel("Time Blocks Per Second:");
        if (isExpressionProfileAnimationMode)
            entitiesOrTimeBlocksPerSecondLabel.setToolTipText("Entities Per Second");
        else
            entitiesOrTimeBlocksPerSecondLabel.setToolTipText("Time Blocks Per Second");
        entitiesOrTimeBlocksPerSecondComboBox = new JComboBox<String>();
        if (isExpressionProfileAnimationMode)
            entitiesOrTimeBlocksPerSecondComboBox.setToolTipText("Entity/Sec");
        else
            entitiesOrTimeBlocksPerSecondComboBox.setToolTipText("TB/Sec");
        startFromEntityOrTimeBlockLabel = (isExpressionProfileAnimationMode) ? new JLabel("Start From Entity:") : new JLabel("Start From TimeBlock:");
        if (isExpressionProfileAnimationMode)
            startFromEntityOrTimeBlockLabel.setToolTipText("Start From Entity");
        else
            startFromEntityOrTimeBlockLabel.setToolTipText("Start From TimeBlock");
        startFromEntityOrTimeBlockTextField = new JTextField();
        startFromEntityOrTimeBlockTextField.setDocument( new TextFieldFilter(TextFieldFilter.NUMERIC) );
        startFromEntityOrTimeBlockTextField.setText("1");
        if (isExpressionProfileAnimationMode)
            startFromEntityOrTimeBlockTextField.setToolTipText("Entity");
        else
            startFromEntityOrTimeBlockTextField.setToolTipText("TimeBlock");
        JPanel transitionPanel = new JPanel(true);
        setMaxNodeSizeLabel = new JLabel("          Set Max Node Size:");
        setMaxNodeSizeLabel.setToolTipText("Set Max Node Size");
        setMaxNodeSizeComboBox = new JComboBox<String>();
        setMaxNodeSizeComboBox.setToolTipText("Max Node Size");
        if (isExpressionProfileAnimationMode)
        {
            setMaxValueLabel = new JLabel("          Set Max Value:");
            setMaxValueLabel.setToolTipText("Set Max Node Size");
            setMaxValueLabel.setEnabled( !ANIMATION_PER_NODE_MAX_VALUE.get() );
        }
        else
        {
            setFixedMaxValueCheckBox = new JCheckBox("   Set (Fixed) Max Value:");
            setFixedMaxValueCheckBox.setToolTipText("Set (Fixed) Max Value");
            setFixedMaxValueCheckBox.setSelected(setFixedMaxValueCheckBoxState);
            setFixedMaxValueCheckBox.addActionListener(this);

            setFixedMaxValueCheckBoxState = false;
            if (isExpressionProfileAnimationMode)
                setFixedMaxValueCheckBox.setEnabled( !ANIMATION_PER_NODE_MAX_VALUE.get() );
        }
        maxValueFoundTextField = new FloatNumberField(0, 5);
        maxValueFoundTextField.setDocument( new TextFieldFilter(TextFieldFilter.FLOAT) );
        if (isExpressionProfileAnimationMode)
            maxValueFoundTextField.setEnabled( !ANIMATION_PER_NODE_MAX_VALUE.get() );
        else
        {
            maxValueFoundTextField.addCaretListener(this);
            maxValueFoundTextField.setText(maxValueFoundString);
        }
        maxValueFoundTextField.setToolTipText("Max Value");
        JPanel colorPalettePanel = new JPanel(true);
        useColorPaletteSpectrumTransitionCheckBox = new JCheckBox("   Use Color Palette Spectrum Transition");
        useColorPaletteSpectrumTransitionCheckBox.setToolTipText("Use Color Palette Spectrum Transition");
        useColorPaletteSpectrumTransitionCheckBox.setSelected(true);
        useColorPaletteSpectrumTransitionCheckBox.addActionListener(this);
        useRealMaxValueForColorTransitionCheckBox = new JCheckBox("   Use Real Max Value For Color Transition");
        useRealMaxValueForColorTransitionCheckBox.setToolTipText("Use Real Max Value For Color Transition");
        useRealMaxValueForColorTransitionCheckBox.addActionListener(this);
        if (isExpressionProfileAnimationMode)
            useRealMaxValueForColorTransitionCheckBox.setEnabled( !ANIMATION_PER_NODE_MAX_VALUE.get() );
        minValueColorLabel = new JLabel("Min Spectrum Color:");
        minValueColorLabel.setToolTipText("Min Spectrum Color");
        minValueColorLabel.setEnabled(false);
        maxValueColorLabel = new JLabel("Max Spectrum Color:");
        maxValueColorLabel.setToolTipText("Max Spectrum Color");
        maxValueColorLabel.setEnabled(false);
        minSpectrumColorButton = new ColorButton(" ");
        minSpectrumColorButton.setPreferredSize( new Dimension(15, 15) );
        minSpectrumColorButton.setBackground(ANIMATION_MIN_SPECTRUM_COLOR);
        minSpectrumColorButton.setToolTipText("Min Spectrum Color Button");
        minSpectrumColorButton.setEnabled(false);
        minSpectrumColorButton.addActionListener(this);
        maxSpectrumColorButton = new ColorButton(" ");
        maxSpectrumColorButton.setPreferredSize( new Dimension(15, 15) );
        maxSpectrumColorButton.setBackground(ANIMATION_MAX_SPECTRUM_COLOR);
        maxSpectrumColorButton.setToolTipText("Max Spectrum Color Button");
        maxSpectrumColorButton.setEnabled(false);
        maxSpectrumColorButton.addActionListener(this);
        useImageAsSpectrumCheckBox = new JCheckBox( "   Use Image As Spectrum");
        useImageAsSpectrumCheckBox.setToolTipText( "Use Image As Spectrum");
        useImageAsSpectrumCheckBox.setSelected(true);
        useImageAsSpectrumCheckBox.addActionListener(this);
        imageSpectrumFileNamesComboBox = new JComboBox<String>(ANIMATION_DEFAULT_SPECTRUM_IMAGE_FILES);
        imageSpectrumFileNamesComboBox.setSelectedIndex(ANIMATION_DEFAULT_SPECTRUM_IMAGE.get() - 1);
        imageSpectrumFileNamesComboBox.setToolTipText("All Default Internal Spectrum Images");
        imageSpectrumFileNamesComboBox.setEditable(false);
        imageSpectrumFileNamesComboBox.addActionListener(this);
        loadImageFileButton = new JButton("Load");
        loadImageFileButton.setToolTipText("Load");
        loadImageFileButton.addActionListener(this);
        defaultImageFileButton = new JButton("Default");
        defaultImageFileButton.setToolTipText("Default");
        defaultImageFileButton.setEnabled(false);
        defaultImageFileButton.addActionListener(this);
        JPanel animationControlPanel = new JPanel(true);
        startAnimationButton = new JButton("Start Animation");
        startAnimationButton.setToolTipText("Start Animation");
        startAnimationButton.addActionListener(this);
        pauseAnimationButton = new JButton("Pause Animation");
        pauseAnimationButton.setToolTipText("Pause Animation");
        pauseAnimationButton.setEnabled(false);
        pauseAnimationButton.addActionListener(this);
        stepAnimationButton = new JButton("Step Animation");
        stepAnimationButton.setToolTipText("Step Animation");
        stepAnimationButton.setEnabled(false);
        stepAnimationButton.addActionListener(this);
        stopAnimationButton = new JButton("Stop Animation");
        stopAnimationButton.setToolTipText("Stop Animation");
        stopAnimationButton.setEnabled(false);
        stopAnimationButton.addActionListener(this);
        closeButton = new JButton("Close Animation Control Dialog");
        closeButton.setToolTipText("Close Animation Control Dialog");
        closeButton.addActionListener(this);

        nodeAnimationPanel.setBorder( BorderFactory.createTitledBorder("Node Animation") );

        GroupLayout nodeAnimationPanelLayout = new GroupLayout(nodeAnimationPanel);
        nodeAnimationPanel.setLayout(nodeAnimationPanelLayout);
        nodeAnimationPanelLayout.setHorizontalGroup(
            nodeAnimationPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(nodeAnimationPanelLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(nodeAnimationPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(perNodeMaxValueOrmEPNComponentsCheckbox)
                    .addComponent(selectedNodesCheckbox)
                    .addComponent(showNodeAnimationValueCheckbox)
                    .addGroup(nodeAnimationPanelLayout.createSequentialGroup()
                        .addComponent(fluidTransitionLabel)
                        .addGap(18, 18, 18)
                        .addGroup(nodeAnimationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(fluidTransitionOffRadioButton)
                            .addComponent(fluidTransitionLinearRadioButton)
                            .addComponent(fluidTransitionPolynomialRadioButton))))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        nodeAnimationPanelLayout.setVerticalGroup(
            nodeAnimationPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(nodeAnimationPanelLayout.createSequentialGroup()
                // .addContainerGap()
                .addComponent(perNodeMaxValueOrmEPNComponentsCheckbox)
                // .addGap(3, 3, 3)
                .addComponent(selectedNodesCheckbox)
                // .addGap(3, 3, 3)
                .addComponent(showNodeAnimationValueCheckbox)
                // .addGap(12, 12, 12)
                .addGroup(nodeAnimationPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(fluidTransitionLabel)
                    .addComponent(fluidTransitionOffRadioButton))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(fluidTransitionLinearRadioButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(fluidTransitionPolynomialRadioButton))
                // .addContainerGap(10, Short.MAX_VALUE))
        );

        timingPanel.setBorder( BorderFactory.createTitledBorder( "Timing" + ( (USE_MULTICORE_PROCESS) ? "  (MultiCore" : "  (SingleCore" ) + " Animation & Rendering)" ) );

        int index = (isExpressionProfileAnimationMode) ? 0 : 1;
        for (int i = 1; i < 10; i++)
            entitiesOrTimeBlocksPerSecondComboBox.addItem( TICKS_PER_SEC_STRING[index] + Utils.numberFormatting(i / 10.0, 2) );
        for (int i = 1; i <= MAX_RATE_PER_SEC; i++)
            entitiesOrTimeBlocksPerSecondComboBox.addItem(TICKS_PER_SEC_STRING[index] + i);
        entitiesOrTimeBlocksPerSecondComboBox.setSelectedIndex(ANIMATION_DEFAULT_ENTITY_OR_TIMEBLOCK_PER_SECOND.get() - 1);
        entitiesOrTimeBlocksPerSecondComboBox.addActionListener(this);

        GroupLayout timingPanelLayout = new GroupLayout(timingPanel);
        timingPanel.setLayout(timingPanelLayout);
        timingPanelLayout.setHorizontalGroup(
            timingPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(timingPanelLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(timingPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(entitiesOrTimeBlocksPerSecondLabel)
                    .addComponent(startFromEntityOrTimeBlockLabel))
                .addGap(15, 15, 15)
                .addGroup(timingPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(startFromEntityOrTimeBlockTextField)
                    .addComponent(entitiesOrTimeBlocksPerSecondComboBox))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        timingPanelLayout.setVerticalGroup(
            timingPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(timingPanelLayout.createSequentialGroup()
                // .addContainerGap()
                .addGroup(timingPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(entitiesOrTimeBlocksPerSecondComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(entitiesOrTimeBlocksPerSecondLabel))
                  .addGap(8, 8, 8)
                .addGroup(timingPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(startFromEntityOrTimeBlockTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(startFromEntityOrTimeBlockLabel)))
                // .addContainerGap(20, Short.MAX_VALUE))
        );

        transitionPanel.setBorder( BorderFactory.createTitledBorder( "Size Transition" + ( (USE_SHADERS_PROCESS) ? "  (GPGPU Computing)" : "  (CPU Computing)" ) ) );

        for (int i = MIN_NODE_SIZE; i <= MAX_NODE_SIZE; i++)
            setMaxNodeSizeComboBox.addItem(NODE_SIZE_STRING + i);
        setMaxNodeSizeComboBox.setSelectedIndex(ANIMATION_DEFAULT_MAX_NODE_SIZE.get() - 1);
        setMaxNodeSizeComboBox.addActionListener(this);

        GroupLayout transitionPanelLayout = new GroupLayout(transitionPanel);
        transitionPanel.setLayout(transitionPanelLayout);
        transitionPanelLayout.setHorizontalGroup(
            transitionPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(transitionPanelLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(transitionPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(setMaxNodeSizeLabel)
                    .addComponent(isExpressionProfileAnimationMode ? setMaxValueLabel : setFixedMaxValueCheckBox))
                .addGap(8, 8, 8)
                .addGroup(transitionPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(setMaxNodeSizeComboBox)
                    .addComponent(maxValueFoundTextField))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        transitionPanelLayout.setVerticalGroup(
            transitionPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(transitionPanelLayout.createSequentialGroup()
                // .addContainerGap()
                .addGroup(transitionPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(setMaxNodeSizeComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(setMaxNodeSizeLabel))
                  .addGap(8, 8, 8)
                .addGroup(transitionPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(maxValueFoundTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(isExpressionProfileAnimationMode ? setMaxValueLabel : setFixedMaxValueCheckBox)))
                // .addContainerGap(20, Short.MAX_VALUE))
        );

        colorPalettePanel.setBorder( BorderFactory.createTitledBorder( "Color Palette Spectrum Transition" + ( (USE_SHADERS_PROCESS) ? "  (GPGPU Computing)" : "  (CPU Computing)" ) ) );

        GroupLayout colorPalettePanelLayout = new GroupLayout(colorPalettePanel);
        colorPalettePanel.setLayout(colorPalettePanelLayout);
        colorPalettePanelLayout.setHorizontalGroup(
            colorPalettePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(colorPalettePanelLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(colorPalettePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(useColorPaletteSpectrumTransitionCheckBox)
                    .addComponent(useRealMaxValueForColorTransitionCheckBox)
                    .addGroup(colorPalettePanelLayout.createSequentialGroup()
                        .addGroup(colorPalettePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(minValueColorLabel)
                            .addComponent(maxValueColorLabel))
                        .addGap(43, 43, 43)
                        .addGroup(colorPalettePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(minSpectrumColorButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(maxSpectrumColorButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(useImageAsSpectrumCheckBox)
                    .addComponent(imageSpectrumFileNamesComboBox, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addGroup(GroupLayout.Alignment.TRAILING, colorPalettePanelLayout.createSequentialGroup()
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 31, GroupLayout.PREFERRED_SIZE)
                        .addComponent(loadImageFileButton, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(defaultImageFileButton, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)))
                .addContainerGap())
        );
        colorPalettePanelLayout.setVerticalGroup(
            colorPalettePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(colorPalettePanelLayout.createSequentialGroup()
                // .addContainerGap()
                .addComponent(useColorPaletteSpectrumTransitionCheckBox)
                .addComponent(useRealMaxValueForColorTransitionCheckBox)
                 .addGap(8, 8, 8)
                .addGroup(colorPalettePanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(minValueColorLabel)
                    .addComponent(minSpectrumColorButton))
                 .addGap(8, 8, 8)
                .addGroup(colorPalettePanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(maxValueColorLabel)
                    .addComponent(maxSpectrumColorButton))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addComponent(useImageAsSpectrumCheckBox)
                // .addGap(5, 5, 5)
                .addComponent(imageSpectrumFileNamesComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(colorPalettePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(defaultImageFileButton)
                    .addComponent(loadImageFileButton)))
                // .addContainerGap())
        );

        animationControlPanel.setBorder( BorderFactory.createTitledBorder("Animation Control") );

        GroupLayout animationControlPanelLayout = new GroupLayout(animationControlPanel);
        animationControlPanel.setLayout(animationControlPanelLayout);
        animationControlPanelLayout.setHorizontalGroup(
            animationControlPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(animationControlPanelLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(animationControlPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(startAnimationButton)
                    .addComponent(stepAnimationButton))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(animationControlPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(stopAnimationButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pauseAnimationButton, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        animationControlPanelLayout.setVerticalGroup(
            animationControlPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(animationControlPanelLayout.createSequentialGroup()
                // .addContainerGap()
                .addGroup(animationControlPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(pauseAnimationButton)
                    .addGroup(animationControlPanelLayout.createSequentialGroup()
                        .addComponent(startAnimationButton)
                         .addGap(8, 8, 8)
                        .addGroup(animationControlPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(stepAnimationButton)
                            .addComponent(stopAnimationButton)))))
                // .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        GroupLayout layout = new GroupLayout( this.getContentPane() );
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(nodeAnimationPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addContainerGap())
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(timingPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addContainerGap())
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(transitionPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addContainerGap())
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(colorPalettePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addContainerGap())
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(animationControlPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addContainerGap()))
                    .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(closeButton)
                        .addGap(65, 65, 65))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(nodeAnimationPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(timingPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(transitionPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(colorPalettePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(animationControlPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(closeButton)
                .addContainerGap(19, Short.MAX_VALUE))
        );
    }

    /**
    *  This method is called from within the constructor to initialize the animation control dialog.
    */
    private void initDialog()
    {
        this.setResizable(false);
        this.pack();
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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
    *  Clears the animation control dialog components.
    */
    private void clearComponents()
    {
        perNodeMaxValueOrmEPNComponentsCheckbox.removeActionListener(this);
        selectedNodesCheckbox.removeActionListener(this);
        showNodeAnimationValueCheckbox.removeActionListener(this);
        fluidTransitionOffRadioButton.removeActionListener(this);
        fluidTransitionLinearRadioButton.removeActionListener(this);
        fluidTransitionPolynomialRadioButton.removeActionListener(this);
        if (setFixedMaxValueCheckBox != null) setFixedMaxValueCheckBox.removeActionListener(this);
        maxValueFoundTextField.removeCaretListener(this);
        useColorPaletteSpectrumTransitionCheckBox.removeActionListener(this);
        useRealMaxValueForColorTransitionCheckBox.removeActionListener(this);
        minSpectrumColorButton.removeActionListener(this);
        maxSpectrumColorButton.removeActionListener(this);
        useImageAsSpectrumCheckBox.removeActionListener(this);
        entitiesOrTimeBlocksPerSecondComboBox.removeActionListener(this);
        setMaxNodeSizeComboBox.removeActionListener(this);
        imageSpectrumFileNamesComboBox.removeActionListener(this);
        loadImageFileButton.removeActionListener(this);
        defaultImageFileButton.removeActionListener(this);
        startAnimationButton.removeActionListener(this);
        pauseAnimationButton.removeActionListener(this);
        stepAnimationButton.removeActionListener(this);
        stopAnimationButton.removeActionListener(this);
        closeButton.removeActionListener(this);

        entitiesOrTimeBlocksPerSecondLabel = null;
        startFromEntityOrTimeBlockLabel = null;
        setMaxNodeSizeLabel = null;
        setMaxValueLabel = null;
        setFixedMaxValueCheckBox = null;
        minValueColorLabel = null;
        maxValueColorLabel = null;
        perNodeMaxValueOrmEPNComponentsCheckbox = null;
        selectedNodesCheckbox = null;
        showNodeAnimationValueCheckbox = null;
        fluidTransitionOffRadioButton = null;
        fluidTransitionLinearRadioButton = null;
        fluidTransitionPolynomialRadioButton = null;
        useColorPaletteSpectrumTransitionCheckBox = null;
        useRealMaxValueForColorTransitionCheckBox = null;
        useImageAsSpectrumCheckBox = null;
        entitiesOrTimeBlocksPerSecondComboBox = null;
        setMaxNodeSizeComboBox = null;
        startFromEntityOrTimeBlockTextField = null;
        maxValueFoundTextField = null;
        imageSpectrumFileNamesComboBox = null;
        minSpectrumColorButton = null;
        maxSpectrumColorButton = null;
        loadImageFileButton = null;
        defaultImageFileButton = null;
        startAnimationButton = null;
        pauseAnimationButton = null;
        stepAnimationButton = null;
        stopAnimationButton = null;
        closeButton = null;

        // ANIMATION_PER_NODE_MAX_VALUE.set(true);
        // ANIMATION_MEPN_COMPONENTS_ANIMATION_ONLY.set(true);

        this.getContentPane().removeAll();

        System.gc();
    }

    /**
    *  Closes the SPN dialog window.
    */
    public void closeDialogWindow()
    {
        if ( closeButton.isEnabled() )
            this.setVisible(false);
    }

    /**
    *  Opens the SPN dialog window.
    */
    public void openDialogWindow()
    {
        this.setVisible(true);
    }

    /**
    *  Sets the max value in the appropriate text field.
    */
    public void setMaxValueInTextField(float value)
    {
        if ( isExpressionProfileAnimationMode || !setFixedMaxValueCheckBox.isSelected() )
        {
            String textValue = new BigDecimal(value).toPlainString();
            int index = textValue.indexOf(".");
            if ( (index != -1) && ( (textValue.length() - index) > 4 ) )
                textValue = textValue.substring(0, index + 4);

            maxValueFoundTextField.setText(textValue);
        }
    }

    /**
    *  Sets the selected nodes checkbox to enabled/disabled state.
    */
    public void setEnabledSelectedNodesCheckbox(boolean value)
    {
        selectedNodesCheckbox.setEnabled(value);
    }

    /**
    *  Sets the isExpressionProfileAnimationMode state.
    */
    public void setIsExpressionProfileAnimationMode(boolean isExpressionProfileAnimationMode)
    {
        this.isExpressionProfileAnimationMode = isExpressionProfileAnimationMode;

        clearComponents();
        initComponents();

        if (isExpressionProfileAnimationMode)
        {
            setMaxValueInTextField( layoutFrame.getExpressionData().findGlobalMaxValueFromExpressionDataArray() );
            ANIMATION_EXPRESSION_DATA_LOCAL_MAX_VALUES = layoutFrame.getExpressionData().findLocalMaxValuesFromExpressionDataArray( layoutFrame.getGraph().getGraphNodes() );
        }
    }

    /**
    *  Reads all the GUI parameters.
    */
    private int readAllGUIParameters()
    {
        int entityOrTimeBlockToStartFrom = 1;
        if (isExpressionProfileAnimationMode)
        {
            TOTAL_NUMBER_OF_ANIMATION_TICKS = layoutFrame.getExpressionData().getTotalColumns();
            ANIMATION_EXPRESSION_DATA = layoutFrame.getExpressionData();
        }
        else
        {
            TOTAL_NUMBER_OF_ANIMATION_TICKS = layoutFrame.getSignalingPetriNetSimulationDialog().getTimeBlocks();
            ANIMATION_SIMULATION_RESULTS = layoutFrame.getSignalingPetriNetSimulationDialog().getSPNSimulationResults();
        }
        String entitiesOrTimeBlocksPerSecondString = (String)entitiesOrTimeBlocksPerSecondComboBox.getSelectedItem();
        int index = (isExpressionProfileAnimationMode) ? 0 : 1;
        ANIMATION_TICKS_PER_SECOND = Float.parseFloat( entitiesOrTimeBlocksPerSecondString.substring( TICKS_PER_SEC_STRING[index].length(), entitiesOrTimeBlocksPerSecondString.length() ) );
        if (ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION)
        {
            ANIMATION_MIN_SPECTRUM_COLOR = minSpectrumColorButton.getBackground();
            ANIMATION_MAX_SPECTRUM_COLOR = maxSpectrumColorButton.getBackground();
        }

        String startFromEntityOrTimeBlockTextFieldString = startFromEntityOrTimeBlockTextField.getText();
        if ( startFromEntityOrTimeBlockTextFieldString.isEmpty() )
        {
            String entityOrTimeBlock = (isExpressionProfileAnimationMode) ? "Entity" : "TimeBlock";
            JOptionPane.showMessageDialog(this, "No value inserted in the \"Start From " + entityOrTimeBlock + "\" textbox.\nNow using the default value of 1 (First " + entityOrTimeBlock + ").", "Animation Control", JOptionPane.INFORMATION_MESSAGE);
            startFromEntityOrTimeBlockTextField.setText("1");
            entityOrTimeBlockToStartFrom = 1;
        }
        else
            entityOrTimeBlockToStartFrom = Integer.parseInt(startFromEntityOrTimeBlockTextFieldString);

        if (entityOrTimeBlockToStartFrom < 1)
        {
            String entityOrTimeBlock = (isExpressionProfileAnimationMode) ? "Entity" : "TimeBlock";
            JOptionPane.showMessageDialog(this, "The value inserted in the \"Start From " + entityOrTimeBlock + "\" textbox must be above 0.\nNow using the default value of 1 (First " + entityOrTimeBlock + ").", "Animation Control", JOptionPane.INFORMATION_MESSAGE);
            startFromEntityOrTimeBlockTextField.setText("1");
            entityOrTimeBlockToStartFrom = 1;
        }
        else if (entityOrTimeBlockToStartFrom > TOTAL_NUMBER_OF_ANIMATION_TICKS)
        {
            String entityOrTimeBlock = (isExpressionProfileAnimationMode) ? "Entity" : "TimeBlock";
            String entitiesOrTimeBlocks = (isExpressionProfileAnimationMode) ? "Entities" : "TimeBlocks";
            JOptionPane.showMessageDialog(this, "The value inserted in the \"Start From " + entityOrTimeBlock + "\" textbox exceeds the total number of " + entitiesOrTimeBlocks + ".\nNow using the default value of 1 (First " + entityOrTimeBlock + ").", "Animation Control", JOptionPane.INFORMATION_MESSAGE);
            startFromEntityOrTimeBlockTextField.setText("1");
            entityOrTimeBlockToStartFrom = 1;
        }

        String setMaxNodeSizeString = (String)setMaxNodeSizeComboBox.getSelectedItem();
        ANIMATION_MAX_NODE_SIZE = Integer.parseInt( setMaxNodeSizeString.substring( NODE_SIZE_STRING.length(), setMaxNodeSizeString.length() ) );

        float maxValueFound = 0.0f;
        if ( maxValueFoundTextField.isEmpty() )
        {
            if (!isExpressionProfileAnimationMode)
                setFixedMaxValueCheckBox.setSelected(false);
            JOptionPane.showMessageDialog(this, "No value inserted in the \"Set Max Value\" textbox.\nNow using the default max value.", "Animation Control", JOptionPane.INFORMATION_MESSAGE);
            ANIMATION_RESULTS_REAL_MAX_VALUE = ANIMATION_RESULTS_MAX_VALUE = (isExpressionProfileAnimationMode) ? layoutFrame.getExpressionData().findGlobalMaxValueFromExpressionDataArray()
                                                                                                                : layoutFrame.getSignalingPetriNetSimulationDialog().findMaxValueFromResultsArray();
            setMaxValueInTextField(ANIMATION_RESULTS_MAX_VALUE);
            maxValueFound = ANIMATION_RESULTS_MAX_VALUE;
            ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITION = !useRealMaxValueForColorTransitionCheckBox.isSelected();
        }
        else
            maxValueFound = maxValueFoundTextField.getValue();

        if (maxValueFound == 0.0f)
        {
            if (!isExpressionProfileAnimationMode)
                setFixedMaxValueCheckBox.setSelected(false);
            JOptionPane.showMessageDialog(this, "The value inserted in the \"Set Max Value\" textbox must be above 0.\nNow using the default max value.", "Animation Control", JOptionPane.INFORMATION_MESSAGE);
            ANIMATION_RESULTS_REAL_MAX_VALUE = ANIMATION_RESULTS_MAX_VALUE = (isExpressionProfileAnimationMode) ? layoutFrame.getExpressionData().findGlobalMaxValueFromExpressionDataArray()
                                                                                                                : layoutFrame.getSignalingPetriNetSimulationDialog().findMaxValueFromResultsArray();
            setMaxValueInTextField(ANIMATION_RESULTS_MAX_VALUE);
            ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITION = !useRealMaxValueForColorTransitionCheckBox.isSelected();
        }
        else
        {
            ANIMATION_RESULTS_REAL_MAX_VALUE = (isExpressionProfileAnimationMode) ? layoutFrame.getExpressionData().findGlobalMaxValueFromExpressionDataArray()
                                                                                  : layoutFrame.getSignalingPetriNetSimulationDialog().findMaxValueFromResultsArray();
            ANIMATION_RESULTS_MAX_VALUE = maxValueFound;
            ANIMATION_USE_REAL_MAX_VALUE_FOR_COLOR_TRANSITION = (ANIMATION_RESULTS_MAX_VALUE != ANIMATION_RESULTS_REAL_MAX_VALUE) && useRealMaxValueForColorTransitionCheckBox.isSelected();
        }

        return entityOrTimeBlockToStartFrom;
    }

    /**
    *  Uses a color palette as spectrum transition.
    */
    private void useColorPaletteSpectrumTransition()
    {
         ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION = useColorPaletteSpectrumTransitionCheckBox.isSelected();

        useRealMaxValueForColorTransitionCheckBox.setEnabled(ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION);
        minValueColorLabel.setEnabled(ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION);
        maxValueColorLabel.setEnabled(ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION);
        minSpectrumColorButton.setEnabled(ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION);
        maxSpectrumColorButton.setEnabled(ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION);
        useImageAsSpectrumCheckBox.setEnabled(ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION);

        imageSpectrumFileNamesComboBox.setEnabled(ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION && ANIMATION_USER_SPECTRUM_IMAGE_FILE.isEmpty() && ANIMATION_USE_IMAGE_AS_SPECTRUM);
        loadImageFileButton.setEnabled(ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION && ANIMATION_USE_IMAGE_AS_SPECTRUM);
        defaultImageFileButton.setEnabled(ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION && !ANIMATION_USER_SPECTRUM_IMAGE_FILE.isEmpty() && ANIMATION_USE_IMAGE_AS_SPECTRUM);
    }

    /**
    *  Uses provided image as spectrum.
    */
    private void useImageAsSpectrum()
    {
        ANIMATION_USE_IMAGE_AS_SPECTRUM = useImageAsSpectrumCheckBox.isSelected();

        minValueColorLabel.setEnabled(!ANIMATION_USE_IMAGE_AS_SPECTRUM);
        maxValueColorLabel.setEnabled(!ANIMATION_USE_IMAGE_AS_SPECTRUM);
        minSpectrumColorButton.setEnabled(!ANIMATION_USE_IMAGE_AS_SPECTRUM);
        maxSpectrumColorButton.setEnabled(!ANIMATION_USE_IMAGE_AS_SPECTRUM);

        imageSpectrumFileNamesComboBox.setEnabled(ANIMATION_USER_SPECTRUM_IMAGE_FILE.isEmpty() && ANIMATION_USE_IMAGE_AS_SPECTRUM);
        loadImageFileButton.setEnabled(ANIMATION_USE_IMAGE_AS_SPECTRUM);
        defaultImageFileButton.setEnabled(!ANIMATION_USER_SPECTRUM_IMAGE_FILE.isEmpty() && ANIMATION_USE_IMAGE_AS_SPECTRUM);
    }

    /**
    *  Loads the given image spectrum file.
    */
    private void loadImageFile()
    {
        if ( JFileChooser.APPROVE_OPTION == spectrumImageFileChooser.showOpenDialog(this) )
        {
            ANIMATION_USER_SPECTRUM_IMAGE_FILE = spectrumImageFileChooser.getSelectedFile().getPath();
            ANIMATION_CHANGE_SPECTRUM_TEXTURE_ENABLED = true;

            imageSpectrumFileNamesComboBox.removeAllItems();
            imageSpectrumFileNamesComboBox.addItem("External Image As Spectrum File Loaded OK.");
            imageSpectrumFileNamesComboBox.setToolTipText(ANIMATION_USER_SPECTRUM_IMAGE_FILE);
            imageSpectrumFileNamesComboBox.setEnabled(false);
            defaultImageFileButton.setEnabled(true);

            FILE_CHOOSER_PATH.set( spectrumImageFileChooser.getSelectedFile().getAbsolutePath() );
            layoutFrame.getGraph().refreshDisplay();
        }
    }

    /**
    *  Loads the default image spectrum file.
    */
    private void defaultImageFile()
    {
        imageSpectrumFileNamesComboBox.removeAllItems();
        for (int i = 0; i < ANIMATION_DEFAULT_SPECTRUM_IMAGE_FILES.length; i++)
            imageSpectrumFileNamesComboBox.addItem(ANIMATION_DEFAULT_SPECTRUM_IMAGE_FILES[i]);
        imageSpectrumFileNamesComboBox.setToolTipText("All Default Internal Spectrum Images");
        imageSpectrumFileNamesComboBox.setEnabled(true);
        imageSpectrumFileNamesComboBox.setSelectedIndex(ANIMATION_DEFAULT_SPECTRUM_IMAGE.get() - 1);
        defaultImageFileButton.setEnabled(false);

        ANIMATION_USER_SPECTRUM_IMAGE_FILE = "";
        ANIMATION_CHANGE_SPECTRUM_TEXTURE_ENABLED = true;

        layoutFrame.getGraph().refreshDisplay();
    }

    /**
    *  Starts the animation.
    */
    private void startAnimation()
    {
        int entityOrTimeBlockToStartFrom = readAllGUIParameters();

        if (isExpressionProfileAnimationMode)
            perNodeMaxValueOrmEPNComponentsCheckbox.setEnabled(false);
        entitiesOrTimeBlocksPerSecondLabel.setEnabled(false);
        entitiesOrTimeBlocksPerSecondComboBox.setEnabled(false);
        startFromEntityOrTimeBlockLabel.setEnabled(false);
        startFromEntityOrTimeBlockTextField.setEnabled(false);

        setMaxNodeSizeLabel.setEnabled(false);
        setMaxNodeSizeComboBox.setEnabled(false);
        if (isExpressionProfileAnimationMode)
            setMaxValueLabel.setEnabled(false);
        else
            setFixedMaxValueCheckBox.setEnabled(false);
        maxValueFoundTextField.setEnabled(false);

        useColorPaletteSpectrumTransitionCheckBox.setEnabled(false);
        useRealMaxValueForColorTransitionCheckBox.setEnabled(false);
        minValueColorLabel.setEnabled(false);
        minSpectrumColorButton.setEnabled(false);
        maxSpectrumColorButton.setEnabled(false);
        maxValueColorLabel.setEnabled(false);
        useImageAsSpectrumCheckBox.setEnabled(false);
        imageSpectrumFileNamesComboBox.setEnabled(false);
        loadImageFileButton.setEnabled(false);
        defaultImageFileButton.setEnabled(false);

        startAnimationButton.setEnabled(false);
        pauseAnimationButton.setEnabled(true);
        stopAnimationButton.setEnabled(true);
        closeButton.setEnabled(false);

        layoutFrame.blockExceptNavigationToolBar();
        layoutFrame.getGraph().setAnimationValues(true, entityOrTimeBlockToStartFrom);
        layoutFrame.getGraph().getGraphRendererActions().getAutoRotateAction().actionPerformed( new ActionEvent(this, 0, START_ANIMATION_EVENT_STRING) );
    }

    /**
    *  Pauses the animation.
    */
    private void pauseAnimation()
    {
        if (layoutFrame.getGraph().getCurrentTick() < TOTAL_NUMBER_OF_ANIMATION_TICKS - 1)
        {
            pauseResumeButtonState = !pauseResumeButtonState;
            String buttonText = (!pauseResumeButtonState) ? "Pause Animation" : "Resume Animation";
            pauseAnimationButton.setText(buttonText);
            pauseAnimationButton.setToolTipText(buttonText);

            if (pauseResumeButtonState)
                layoutFrame.getGraph().generalPauseRenderUpdateThread();
            else
                layoutFrame.getGraph().generalResumeRenderUpdateThread();

            stepAnimationButton.setEnabled(pauseResumeButtonState);
        }
    }

    /**
    *  Steps the animation.
    */
    private void stepAnimation()
    {
        if (layoutFrame.getGraph().getCurrentTick() < TOTAL_NUMBER_OF_ANIMATION_TICKS - 1)
        {
            layoutFrame.getGraph().setStepAnimation();
            layoutFrame.getGraph().generalResumeRenderUpdateThread();
        }
        else
            stopAnimation(true);
    }

    /**
    *  Stops the animation.
    */
    public void stopAnimation(boolean resumeRendererAndThenStopAnimation)
    {
        pauseResumeButtonState = false;
        pauseAnimationButton.setText("Pause Animation");

        if (isExpressionProfileAnimationMode)
            perNodeMaxValueOrmEPNComponentsCheckbox.setEnabled(true);
        entitiesOrTimeBlocksPerSecondLabel.setEnabled(true);
        entitiesOrTimeBlocksPerSecondComboBox.setEnabled(true);
        startFromEntityOrTimeBlockLabel.setEnabled(true);
        startFromEntityOrTimeBlockTextField.setEnabled(true);

        setMaxNodeSizeLabel.setEnabled(true);
        setMaxNodeSizeComboBox.setEnabled(true);
        if (isExpressionProfileAnimationMode)
            setMaxValueLabel.setEnabled( !isExpressionProfileAnimationMode || !ANIMATION_PER_NODE_MAX_VALUE.get() );
        else
            setFixedMaxValueCheckBox.setEnabled(true);
        maxValueFoundTextField.setEnabled( !isExpressionProfileAnimationMode || !ANIMATION_PER_NODE_MAX_VALUE.get() );

        useColorPaletteSpectrumTransitionCheckBox.setEnabled(true);
        if (ANIMATION_USE_COLOR_PALETTE_SPECTRUM_TRANSITION)
        {
            useRealMaxValueForColorTransitionCheckBox.setEnabled( !isExpressionProfileAnimationMode || !ANIMATION_PER_NODE_MAX_VALUE.get() );
            minValueColorLabel.setEnabled(!ANIMATION_USE_IMAGE_AS_SPECTRUM);
            maxValueColorLabel.setEnabled(!ANIMATION_USE_IMAGE_AS_SPECTRUM);
            minSpectrumColorButton.setEnabled(!ANIMATION_USE_IMAGE_AS_SPECTRUM);
            maxSpectrumColorButton.setEnabled(!ANIMATION_USE_IMAGE_AS_SPECTRUM);
            useImageAsSpectrumCheckBox.setEnabled(true);

            if (ANIMATION_USE_IMAGE_AS_SPECTRUM)
            {
                imageSpectrumFileNamesComboBox.setEnabled( ANIMATION_USER_SPECTRUM_IMAGE_FILE.isEmpty() );
                loadImageFileButton.setEnabled(true);
                defaultImageFileButton.setEnabled( !ANIMATION_USER_SPECTRUM_IMAGE_FILE.isEmpty() );
            }
        }

        startAnimationButton.setEnabled(true);
        pauseAnimationButton.setEnabled(false);
        stepAnimationButton.setEnabled(false);
        stopAnimationButton.setEnabled(false);
        closeButton.setEnabled(true);

        if (resumeRendererAndThenStopAnimation)
            layoutFrame.getGraph().generalResumeRenderUpdateThread();
        layoutFrame.getGraph().setAnimationValues(false, 0);
        layoutFrame.getGraph().getGraphRendererActions().getAutoRotateAction().actionPerformed( new ActionEvent(this, 0, STOP_ANIMATION_EVENT_STRING) );
        layoutFrame.unblockExceptNavigationToolBar();
    }

    /**
    *  Executes the actionPerformed() callback for the SPN dialog window.
    */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        if ( e.getSource().equals(perNodeMaxValueOrmEPNComponentsCheckbox) )
        {
            if (isExpressionProfileAnimationMode)
            {
                ANIMATION_PER_NODE_MAX_VALUE.set( perNodeMaxValueOrmEPNComponentsCheckbox.isSelected() );
                setMaxValueLabel.setEnabled( !ANIMATION_PER_NODE_MAX_VALUE.get() );
                maxValueFoundTextField.setEnabled( !ANIMATION_PER_NODE_MAX_VALUE.get() );
                useRealMaxValueForColorTransitionCheckBox.setEnabled( !ANIMATION_PER_NODE_MAX_VALUE.get() );
            }
            else
                ANIMATION_MEPN_COMPONENTS_ANIMATION_ONLY.set( perNodeMaxValueOrmEPNComponentsCheckbox.isSelected() );
            layoutFrame.getGraph().refreshDisplay();
            layoutFrame.getLayoutGraphPropertiesDialog().setHasNewPreferencesBeenApplied(true);
        }
        else if ( e.getSource().equals(selectedNodesCheckbox) )
        {
            ANIMATION_SELECTED_NODES_ANIMATION_ONLY.set( selectedNodesCheckbox.isSelected() );
            layoutFrame.getGraph().refreshDisplay();
            layoutFrame.getLayoutGraphPropertiesDialog().setHasNewPreferencesBeenApplied(true);
        }
        else if ( e.getSource().equals(showNodeAnimationValueCheckbox) )
        {
            ANIMATION_SHOW_NODE_ANIMATION_VALUE.set( showNodeAnimationValueCheckbox.isSelected() );
            layoutFrame.getGraph().refreshDisplay();
            layoutFrame.getLayoutGraphPropertiesDialog().setHasNewPreferencesBeenApplied(true);
        }
        else if ( e.getSource().equals(fluidTransitionOffRadioButton) )
        {
            ANIMATION_FLUID_TRANSITION_TYPE.set(1);
            ANIMATION_FLUID_LINEAR_TRANSITION = false;
            ANIMATION_FLUID_POLYNOMIAL_TRANSITION = false;
            layoutFrame.getGraph().refreshDisplay();
            layoutFrame.getLayoutGraphPropertiesDialog().setHasNewPreferencesBeenApplied(true);
        }
        else if ( e.getSource().equals(fluidTransitionLinearRadioButton) )
        {
            ANIMATION_FLUID_TRANSITION_TYPE.set(2);
            ANIMATION_FLUID_LINEAR_TRANSITION = true;
            ANIMATION_FLUID_POLYNOMIAL_TRANSITION = false;
            layoutFrame.getGraph().refreshDisplay();
            layoutFrame.getLayoutGraphPropertiesDialog().setHasNewPreferencesBeenApplied(true);
        }
        else if ( e.getSource().equals(fluidTransitionPolynomialRadioButton) )
        {
            ANIMATION_FLUID_TRANSITION_TYPE.set(3);
            ANIMATION_FLUID_LINEAR_TRANSITION = true;
            ANIMATION_FLUID_POLYNOMIAL_TRANSITION = true;
            layoutFrame.getGraph().refreshDisplay();
            layoutFrame.getLayoutGraphPropertiesDialog().setHasNewPreferencesBeenApplied(true);
        }
        else if ( e.getSource().equals(entitiesOrTimeBlocksPerSecondComboBox) )
        {
            ANIMATION_DEFAULT_ENTITY_OR_TIMEBLOCK_PER_SECOND.set(entitiesOrTimeBlocksPerSecondComboBox.getSelectedIndex() + 1);
            layoutFrame.getLayoutGraphPropertiesDialog().setHasNewPreferencesBeenApplied(true);
        }
        else if ( e.getSource().equals(setMaxNodeSizeComboBox) )
        {
            ANIMATION_DEFAULT_MAX_NODE_SIZE.set(setMaxNodeSizeComboBox.getSelectedIndex() + 1);
            layoutFrame.getLayoutGraphPropertiesDialog().setHasNewPreferencesBeenApplied(true);
        }
        else if ( e.getSource().equals(setFixedMaxValueCheckBox) )
        {
            setFixedMaxValueCheckBoxState = setFixedMaxValueCheckBox.isSelected();
        }
        else if ( e.getSource().equals(useColorPaletteSpectrumTransitionCheckBox) )
        {
            useColorPaletteSpectrumTransition();
        }
        else if ( e.getSource().getClass().equals(ColorButton.class) )
        {
            ColorButton.showColorChooser( (ColorButton)e.getSource(), this );
        }
        else if ( e.getSource().equals(useImageAsSpectrumCheckBox) )
        {
            useImageAsSpectrum();
        }
        else if ( e.getSource().equals(imageSpectrumFileNamesComboBox) )
        {
            ANIMATION_CHOSEN_DEFAULT_SPECTRUM_IMAGE_FILE_INDEX = ( ANIMATION_USER_SPECTRUM_IMAGE_FILE.isEmpty() ) ? imageSpectrumFileNamesComboBox.getSelectedIndex() : 0;
            ANIMATION_CHANGE_SPECTRUM_TEXTURE_ENABLED = true;
            ANIMATION_DEFAULT_SPECTRUM_IMAGE.set(ANIMATION_CHOSEN_DEFAULT_SPECTRUM_IMAGE_FILE_INDEX + 1);
            layoutFrame.getLayoutGraphPropertiesDialog().setHasNewPreferencesBeenApplied(true);
        }
        else if ( e.getSource().equals(loadImageFileButton) )
        {
            loadImageFile();
        }
        else if ( e.getSource().equals(defaultImageFileButton) )
        {
            defaultImageFile();
        }
        else if ( e.getSource().equals(startAnimationButton) )
        {
            startAnimation();
        }
        else if ( e.getSource().equals(pauseAnimationButton) )
        {
            pauseAnimation();
        }
        else if ( e.getSource().equals(stepAnimationButton) )
        {
            stepAnimation();
        }
        else if ( e.getSource().equals(stopAnimationButton) )
        {
            stopAnimation(true);
        }
        else if ( e.getSource().equals(closeButton) )
        {
            closeDialogWindow();
        }
    }

    @Override
    public void caretUpdate(CaretEvent ce)
    {
        if( ce.getSource().equals(maxValueFoundTextField) )
        {
            maxValueFoundString = maxValueFoundTextField.getText();
        }
    }

    public AbstractAction getAnimationControlDialogAction()
    {
        return animationControlDialogAction;
    }


}