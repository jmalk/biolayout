package org.BioLayoutExpress3D.CoreUI.Dialogs;

import java.awt.*;
import java.awt.event.*;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.event.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.Utils.*;
import static org.BioLayoutExpress3D.StaticLibraries.EnumUtils.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
*
* The LayoutCustomizeNodeNamesDialog class is a JDialog to customize node names for displaying purposes from the OpenGL renderer.
*
* @author Thanos Theo, 2011
* @version 3.0.0.0
*/

public final class LayoutCustomizeNodeNamesDialog extends JDialog implements ActionListener, CaretListener
{

    /**
    *  Serial version UID variable for the FilterNodesByWeight class.
    */
    public static final long serialVersionUID = 111222333444555199L;

    private static final String[] ALL_DELIMITERS = { ";", ":", ",", "#", "" };
    private static final int NAME_RENDERING_NUMBER_OF_OPTIONS = 3;

    private String selectedDelimiter = ALL_DELIMITERS[0];
    private int nameRenderingTypeSelected = 0;
    private LayoutFrame layoutFrame = null;

    private JRadioButton[] allDelimiterRadioButtons = null;
    private JCheckBox customDelimiterCheckBox = null;
    private JTextField customDelimiterTextField = null;

    private JCheckBox showFullNameCheckBox = null;
    private JCheckBox showPartialNameLengthCheckBox = null;
    private JLabel partialNameNumberOfCharactersLabel = null;
    private WholeNumberField partialNameNumberOfCharactersField = null;

    private JComboBox<String> openGLNameFontTypesComboBox = null;

    private JRadioButton[] allNameRenderingRadioButtons = null;

    private JButton okButton = null;
    private JButton cancelButton = null;
    private JButton applyButton = null;

    private AbstractAction customizeNodeNamesAction = null;

    public LayoutCustomizeNodeNamesDialog(LayoutFrame layoutFrame)
    {
        super(layoutFrame, "Customize Node Names", false);

        this.layoutFrame = layoutFrame;

        initActions();
        initComponents();
    }

    /**
    *  Initializes the UI actions for this dialog.
    */
    private void initActions()
    {
        customizeNodeNamesAction = new AbstractAction("Customize Node Names")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555697L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                setVisible(true);

                readCustomizeNodeNamesProperties();
                for (int i = 0; i < ALL_DELIMITERS.length; i++)
                    if ( allDelimiterRadioButtons[i].isEnabled() && allDelimiterRadioButtons[i].isSelected() )
                        allDelimiterRadioButtons[i].requestFocus();
                for (int i = 0; i < NAME_RENDERING_NUMBER_OF_OPTIONS; i++)
                    if ( allNameRenderingRadioButtons[i].isEnabled() && allNameRenderingRadioButtons[i].isSelected() )
                        allNameRenderingRadioButtons[i].requestFocus();
                applyButton.setEnabled(false);
            }
        };
        customizeNodeNamesAction.setEnabled(false);
    }

    /**
    *  Initializes the UI components for this dialog.
    */
    private void initComponents()
    {
        JPanel delimiterOptionsPanel = new JPanel(true);
        allDelimiterRadioButtons = new JRadioButton[ALL_DELIMITERS.length];
        for (int i = 0; i < ALL_DELIMITERS.length; i++)
        {
            allDelimiterRadioButtons[i] = new JRadioButton();
            allDelimiterRadioButtons[i].addActionListener(this);
            if (i == 4)
                allDelimiterRadioButtons[4].setToolTipText("Delimiter:   None");
            else
                allDelimiterRadioButtons[i].setToolTipText("Delimiter:   " + ALL_DELIMITERS[i]);
        }
        allDelimiterRadioButtons[0].setSelected(true);
        ButtonGroup allDelimiterRadioButtonsGroup = new ButtonGroup();
        customDelimiterTextField = new JTextField();
        customDelimiterTextField.addCaretListener(this);
        customDelimiterCheckBox = new JCheckBox();
        customDelimiterCheckBox.addActionListener(this);

        JPanel nameLengthOptionsPanel = new JPanel(true);
        showFullNameCheckBox = new JCheckBox();
        showFullNameCheckBox.addActionListener(this);
        showPartialNameLengthCheckBox = new JCheckBox();
        showPartialNameLengthCheckBox.addActionListener(this);

        JPanel partialNameNumberOfCharactersPanel = new JPanel(true);
        partialNameNumberOfCharactersPanel.setLayout( new BoxLayout(partialNameNumberOfCharactersPanel, BoxLayout.X_AXIS) );
        partialNameNumberOfCharactersLabel = new JLabel("Number Of Characters:");
        partialNameNumberOfCharactersField = new WholeNumberField(0, 3);
        partialNameNumberOfCharactersField.addCaretListener(this);

        JPanel openGLNameFontTypesComboBoxOptionsPanel = new JPanel(true);
        openGLNameFontTypesComboBox = new JComboBox<String>();
        for ( OpenGLFontTypes openGLFontType : OpenGLFontTypes.values() )
            openGLNameFontTypesComboBox.addItem( splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(openGLFontType) );
        openGLNameFontTypesComboBox.setSelectedIndex( getEnumIndexForName( OpenGLFontTypes.class, CUSTOMIZE_NODE_NAMES_OPENGL_NAME_FONT_TYPE.get() ) );
        openGLNameFontTypesComboBox.addActionListener(this);
        openGLNameFontTypesComboBox.setToolTipText("OpenGL Name Font Types");

        JPanel nameRenderingOptionsPanel = new JPanel(true);
        allNameRenderingRadioButtons = new JRadioButton[NAME_RENDERING_NUMBER_OF_OPTIONS];
        for (int i = 0; i < NAME_RENDERING_NUMBER_OF_OPTIONS; i++)
        {
            allNameRenderingRadioButtons[i] = new JRadioButton();
            allNameRenderingRadioButtons[i].addActionListener(this);
            if (i == 0)
                allNameRenderingRadioButtons[i].setToolTipText("Logic Op Name Rendering");
            else if (i == 1)
                allNameRenderingRadioButtons[i].setToolTipText(" B/W Name Background Legends");
            else if (i == 2)
                allNameRenderingRadioButtons[i].setToolTipText(" Colored Name Background Legends");
        }
        allNameRenderingRadioButtons[nameRenderingTypeSelected].setSelected(true);
        ButtonGroup allNameRenderingRadioButtonsGroup = new ButtonGroup();

        JPanel buttonsPanel = new JPanel(true);
        buttonsPanel.setLayout( new BoxLayout(buttonsPanel, BoxLayout.X_AXIS) );
        okButton = new JButton();
        okButton.addActionListener(this);
        cancelButton = new JButton();
        cancelButton.addActionListener(this);
        applyButton = new JButton();
        applyButton.addActionListener(this);
        applyButton.setEnabled(false);

        delimiterOptionsPanel.setBorder( BorderFactory.createTitledBorder("Delimiter (Divider) Options") );

        for (int i = 0; i < ALL_DELIMITERS.length; i++)
        {
            allDelimiterRadioButtonsGroup.add(allDelimiterRadioButtons[i]);
            if (i == 4)
                allDelimiterRadioButtons[4].setText("  None  ");
            else
                allDelimiterRadioButtons[i].setText("  " + ALL_DELIMITERS[i] + "  ");
        }

        customDelimiterTextField.setText(" ");
        customDelimiterTextField.setToolTipText("Other (Custom Delimiter)");
        customDelimiterCheckBox.setText("Other (Custom Delimiter)");
        customDelimiterCheckBox.setToolTipText("Other (Custom Delimiter)");

        GroupLayout delimiterOptionsPanelLayout = new GroupLayout(delimiterOptionsPanel);
        delimiterOptionsPanel.setLayout(delimiterOptionsPanelLayout);
        delimiterOptionsPanelLayout.setHorizontalGroup(
            delimiterOptionsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(delimiterOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(delimiterOptionsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(delimiterOptionsPanelLayout.createSequentialGroup()
                        .addComponent(customDelimiterTextField, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(customDelimiterCheckBox))
                    .addComponent(allDelimiterRadioButtons[0])
                    .addComponent(allDelimiterRadioButtons[1])
                    .addComponent(allDelimiterRadioButtons[2])
                    .addComponent(allDelimiterRadioButtons[3])
                    .addComponent(allDelimiterRadioButtons[4]))
                .addContainerGap(0, Short.MAX_VALUE))
        );
        delimiterOptionsPanelLayout.setVerticalGroup(
            delimiterOptionsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(delimiterOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(allDelimiterRadioButtons[0])
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(allDelimiterRadioButtons[1])
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(allDelimiterRadioButtons[2])
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(allDelimiterRadioButtons[3])
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(allDelimiterRadioButtons[4])
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(delimiterOptionsPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(customDelimiterTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(customDelimiterCheckBox))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        nameLengthOptionsPanel.setBorder( BorderFactory.createTitledBorder("Name Length Options") );

        showFullNameCheckBox.setText("Show Full Name (Including Delimiter)");
        showFullNameCheckBox.setToolTipText("Show Full Name (Including Delimiter)");
        showFullNameCheckBox.setSelected(true);
        showPartialNameLengthCheckBox.setText("Show Partial Name Length");
        showPartialNameLengthCheckBox.setToolTipText("Show Partial Name Length");
        showPartialNameLengthCheckBox.setEnabled(false);

        partialNameNumberOfCharactersLabel.setToolTipText("Partial Name Number Of Characters");
        partialNameNumberOfCharactersLabel.setEnabled(false);
        partialNameNumberOfCharactersField.setToolTipText("Partial Name Number Of Characters");
        partialNameNumberOfCharactersField.setEnabled(false);
        partialNameNumberOfCharactersPanel.add( Box.createRigidArea( new Dimension(22, 5) ) );
        partialNameNumberOfCharactersPanel.add(partialNameNumberOfCharactersLabel);
        partialNameNumberOfCharactersPanel.add( Box.createRigidArea( new Dimension(10, 5) ) );
        partialNameNumberOfCharactersPanel.add(partialNameNumberOfCharactersField);

        GroupLayout matchNameOptionsPanelLayout = new GroupLayout(nameLengthOptionsPanel);
        nameLengthOptionsPanel.setLayout(matchNameOptionsPanelLayout);
        matchNameOptionsPanelLayout.setHorizontalGroup(
            matchNameOptionsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(matchNameOptionsPanelLayout.createSequentialGroup()
                .addGroup(matchNameOptionsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(showFullNameCheckBox)
                    .addComponent(showPartialNameLengthCheckBox)
                    .addComponent(partialNameNumberOfCharactersPanel))
                .addContainerGap(0, Short.MAX_VALUE))
        );
        matchNameOptionsPanelLayout.setVerticalGroup(
            matchNameOptionsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(matchNameOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(showFullNameCheckBox)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(showPartialNameLengthCheckBox)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(partialNameNumberOfCharactersPanel)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        openGLNameFontTypesComboBoxOptionsPanel.setBorder( BorderFactory.createTitledBorder("OpenGL Name Font Types") );

        GroupLayout openGLNameFontTypesComboBoxOptionsPanelLayout = new GroupLayout(openGLNameFontTypesComboBoxOptionsPanel);
        nameRenderingOptionsPanel.setLayout(openGLNameFontTypesComboBoxOptionsPanelLayout);
        openGLNameFontTypesComboBoxOptionsPanelLayout.setHorizontalGroup(
            openGLNameFontTypesComboBoxOptionsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(openGLNameFontTypesComboBoxOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(openGLNameFontTypesComboBoxOptionsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(openGLNameFontTypesComboBoxOptionsPanelLayout.createSequentialGroup())
                    .addComponent(openGLNameFontTypesComboBox))
                .addContainerGap(0, Short.MAX_VALUE))
        );
        openGLNameFontTypesComboBoxOptionsPanelLayout.setVerticalGroup(
            openGLNameFontTypesComboBoxOptionsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(openGLNameFontTypesComboBoxOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(openGLNameFontTypesComboBox)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        nameRenderingOptionsPanel.setBorder( BorderFactory.createTitledBorder("Name Rendering Types") );

        for (int i = 0; i < NAME_RENDERING_NUMBER_OF_OPTIONS; i++)
        {
            allNameRenderingRadioButtonsGroup.add(allNameRenderingRadioButtons[i]);
            if (i == 0)
                allNameRenderingRadioButtons[i].setText("  Logic Op Name Rendering");
            else if (i == 1)
                allNameRenderingRadioButtons[i].setText("  B/W Name Background Legends");
            else if (i == 2)
                allNameRenderingRadioButtons[i].setText("  Colored Name Background Legends");
        }

        GroupLayout nameRenderingOptionsPanelLayout = new GroupLayout(nameRenderingOptionsPanel);
        nameRenderingOptionsPanel.setLayout(nameRenderingOptionsPanelLayout);
        nameRenderingOptionsPanelLayout.setHorizontalGroup(
            nameRenderingOptionsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(nameRenderingOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(nameRenderingOptionsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(nameRenderingOptionsPanelLayout.createSequentialGroup())
                    .addComponent(allNameRenderingRadioButtons[0])
                    .addComponent(allNameRenderingRadioButtons[1])
                    .addComponent(allNameRenderingRadioButtons[2]))
                .addContainerGap(0, Short.MAX_VALUE))
        );
        nameRenderingOptionsPanelLayout.setVerticalGroup(
            nameRenderingOptionsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(nameRenderingOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(allNameRenderingRadioButtons[0])
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(allNameRenderingRadioButtons[1])
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(allNameRenderingRadioButtons[2])
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        okButton.setText("OK");
        okButton.setToolTipText("OK");
        cancelButton.setText("Cancel");
        cancelButton.setToolTipText("Cancel");
        applyButton.setText("Apply");
        applyButton.setToolTipText("Apply");
        buttonsPanel.add(okButton);
        buttonsPanel.add(cancelButton);
        buttonsPanel.add(applyButton);

        GroupLayout layout = new GroupLayout( this.getContentPane() );
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                            .addComponent(delimiterOptionsPanel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(nameLengthOptionsPanel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(openGLNameFontTypesComboBoxOptionsPanel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(nameRenderingOptionsPanel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(buttonsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(delimiterOptionsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nameLengthOptionsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(openGLNameFontTypesComboBoxOptionsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(nameRenderingOptionsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buttonsPanel)
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
                setVisible(false);
            }
        } );
    }

    private void readCustomizeNodeNamesProperties()
    {
        int foundIndex = -1;
        for (int i = 0; i < ALL_DELIMITERS.length; i++)
        {
            if ( ALL_DELIMITERS[i].equals( CUSTOMIZE_NODE_NAMES_DELIMITER.get() ) )
            {
                foundIndex = i;
                break;
            }
        }

        if (foundIndex != -1)
        {
            allDelimiterRadioButtons[foundIndex].setSelected(true);
            allDelimiterRadioButtons[foundIndex].doClick();
            allDelimiterRadioButtons[foundIndex].requestFocus();
        }
        else
        {
            customDelimiterTextField.setText( CUSTOMIZE_NODE_NAMES_DELIMITER.get() );
            if (customDelimiterCheckBox.isSelected() )
                customDelimiterCheckBox.setSelected(false);
            customDelimiterCheckBox.doClick();
            customDelimiterCheckBox.requestFocus();
        }

        showFullNameCheckBox.setSelected( CUSTOMIZE_NODE_NAMES_SHOW_FULL_NAME.get() );
        showFullNameCheckBox.setEnabled( CUSTOMIZE_NODE_NAMES_SHOW_FULL_NAME.get() );

        showPartialNameLengthCheckBox.setSelected( CUSTOMIZE_NODE_NAMES_SHOW_PARTIAL_NAME_LENGTH.get() );
        showPartialNameLengthCheckBox.setEnabled( CUSTOMIZE_NODE_NAMES_SHOW_PARTIAL_NAME_LENGTH.get() );

        partialNameNumberOfCharactersLabel.setEnabled( CUSTOMIZE_NODE_NAMES_SHOW_PARTIAL_NAME_LENGTH.get() );
        partialNameNumberOfCharactersField.setEnabled( CUSTOMIZE_NODE_NAMES_SHOW_PARTIAL_NAME_LENGTH.get() );
        partialNameNumberOfCharactersField.setText( Integer.toString( CUSTOMIZE_NODE_NAMES_SHOW_PARTIAL_NAME_LENGTH_NUMBER_OF_CHARACTERS.get() ) );

        if ( !showFullNameCheckBox.isEnabled() && !showPartialNameLengthCheckBox.isEnabled() )
        {
            showFullNameCheckBox.setEnabled(true);
            showPartialNameLengthCheckBox.setEnabled(true);
            partialNameNumberOfCharactersLabel.setEnabled(true);
            partialNameNumberOfCharactersField.setEnabled(true);
        }

        foundIndex = CUSTOMIZE_NODE_NAMES_NAME_RENDERING_TYPE.get();
        allNameRenderingRadioButtons[foundIndex].setSelected(true);
        allNameRenderingRadioButtons[foundIndex].doClick();
        allNameRenderingRadioButtons[foundIndex].requestFocus();
    }

    private void applyCustomizeNodeNamesProperties()
    {
        if ( customDelimiterCheckBox.isSelected() && !showFullNameCheckBox.isSelected() )
            checkSelectedDelimiterForRegularExpressionCompatibility();

        NODE_NAMES_OPENGL_FONT_TYPE = OpenGLFontTypes.values()[openGLNameFontTypesComboBox.getSelectedIndex()];
        CUSTOMIZE_NODE_NAMES_OPENGL_NAME_FONT_TYPE.set( NODE_NAMES_OPENGL_FONT_TYPE.toString() );

        CUSTOMIZE_NODE_NAMES_DELIMITER.set(selectedDelimiter);
        CUSTOMIZE_NODE_NAMES_SHOW_FULL_NAME.set( showFullNameCheckBox.isSelected() );
        CUSTOMIZE_NODE_NAMES_SHOW_PARTIAL_NAME_LENGTH.set( showPartialNameLengthCheckBox.isSelected() );
        CUSTOMIZE_NODE_NAMES_SHOW_PARTIAL_NAME_LENGTH_NUMBER_OF_CHARACTERS.set( partialNameNumberOfCharactersField.getValue() );
        CUSTOMIZE_NODE_NAMES_NAME_RENDERING_TYPE.set(nameRenderingTypeSelected);

        layoutFrame.getGraph().updateNodesDisplayList();
        if (SAVE_CUSTOMIZE_NODE_NAMES_OPTIONS)
            layoutFrame.getLayoutGraphPropertiesDialog().setHasNewPreferencesBeenApplied(true);
    }

    private void checkSelectedDelimiterForRegularExpressionCompatibility()
    {
        try
        {
            "testing".split(selectedDelimiter + "+");
        }
        catch (PatternSyntaxException exc)
        {
            if (DEBUG_BUILD) org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.println("PatternSyntaxException in checkSelectedDelimiterForRegularExpressionCompatibility():\n" + exc.getMessage());
            JOptionPane.showMessageDialog(this, "The Custom Delimiter '" + selectedDelimiter + "' Cannot Be Accepted For Pattern Matching.\nPlease Use Another Custom Delimiter.", "Custom Delimiter Not Accepted!", JOptionPane.WARNING_MESSAGE);
            customDelimiterTextField.setText(" ");
            selectedDelimiter = " ";
        }
    }

    private void setEnabledShowFullNameMode(boolean enabled)
    {
        customDelimiterTextField.setEnabled(!enabled);
        customDelimiterCheckBox.setEnabled(!enabled);

        showFullNameCheckBox.setSelected(enabled);
        showPartialNameLengthCheckBox.setEnabled(!enabled);
        partialNameNumberOfCharactersLabel.setEnabled(!enabled);
        partialNameNumberOfCharactersField.setEnabled(!enabled);

        applyButton.setEnabled(true);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if ( e.getSource().equals(customDelimiterCheckBox) )
        {
            boolean radioButtonState = !customDelimiterCheckBox.isSelected();
            for (int i = 0; i < ALL_DELIMITERS.length; i++)
            {
                allDelimiterRadioButtons[i].setEnabled(radioButtonState);
                if ( radioButtonState && allDelimiterRadioButtons[i].isSelected() )
                    selectedDelimiter = ALL_DELIMITERS[i];
            }

            if (!radioButtonState)
                selectedDelimiter = customDelimiterTextField.getText().trim();

            applyButton.setEnabled(true);
        }
        else if ( e.getSource().equals(showFullNameCheckBox) )
        {
            boolean checkBoxState = showFullNameCheckBox.isSelected();
            if ( !customDelimiterCheckBox.isSelected() )
                for (int i = 0; i < ALL_DELIMITERS.length; i++)
                    allDelimiterRadioButtons[i].setEnabled(!checkBoxState);
            customDelimiterTextField.setEnabled(!checkBoxState);
            customDelimiterCheckBox.setEnabled(!checkBoxState);
            showPartialNameLengthCheckBox.setEnabled(!checkBoxState);
            partialNameNumberOfCharactersLabel.setEnabled(!checkBoxState);
            partialNameNumberOfCharactersField.setEnabled(!checkBoxState);

            if ( !checkBoxState && allDelimiterRadioButtons[4].isSelected() )
            {
                allDelimiterRadioButtons[0].doClick();
                allDelimiterRadioButtons[0].requestFocus();
            }

            applyButton.setEnabled(true);
        }
        else if ( e.getSource().equals(showPartialNameLengthCheckBox) )
        {
            boolean checkBoxState = showPartialNameLengthCheckBox.isSelected();
            showFullNameCheckBox.setEnabled(!checkBoxState);
            partialNameNumberOfCharactersLabel.setEnabled(checkBoxState);
            partialNameNumberOfCharactersField.setEnabled(checkBoxState);

            applyButton.setEnabled(true);
        }
        else if ( e.getSource().equals(openGLNameFontTypesComboBox) )
        {
            applyButton.setEnabled(true);
        }
        else if ( e.getSource().equals(okButton) )
        {
            applyCustomizeNodeNamesProperties();
            this.setVisible(false);
        }
        else if ( e.getSource().equals(cancelButton) )
        {
            this.setVisible(false);
        }
        else if ( e.getSource().equals(applyButton) )
        {
            applyCustomizeNodeNamesProperties();
            applyButton.setEnabled(false);
        }
        else // must be the radiobuttons' listeners
        {
            for (int i = 0; i < ALL_DELIMITERS.length; i++)
            {
                if ( e.getSource().equals(allDelimiterRadioButtons[i]) )
                {
                    selectedDelimiter = ALL_DELIMITERS[i];
                    setEnabledShowFullNameMode(i == 4);
                    return;
                }
            }

            for (int i = 0; i < NAME_RENDERING_NUMBER_OF_OPTIONS; i++)
            {
                if ( e.getSource().equals(allNameRenderingRadioButtons[i]) )
                {
                    nameRenderingTypeSelected = i;
                    applyButton.setEnabled(true);
                    return;
                }
            }
        }
    }

    @Override
    public void caretUpdate(CaretEvent ce)
    {
        if ( ce.getSource().equals(customDelimiterTextField) )
        {
            if ( customDelimiterTextField.isEnabled() )
            {
                for (int i = 0; i < ALL_DELIMITERS.length; i++)
                    allDelimiterRadioButtons[i].setEnabled(false);
                customDelimiterCheckBox.setSelected(true);

                selectedDelimiter = customDelimiterTextField.getText().trim();
                applyButton.setEnabled(true);
            }
        }
        else if ( ce.getSource().equals(partialNameNumberOfCharactersField) )
        {
            if ( showPartialNameLengthCheckBox.isEnabled() )
                applyButton.setEnabled(true);
        }
    }

    public AbstractAction getCustomizeNodeNamesAction()
    {
        return customizeNodeNamesAction;
    }


}