package org.BioLayoutExpress3D.Files.Dialogs;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import org.BioLayoutExpress3D.CoreUI.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
*
* The ImportClassSetsDialog class is a JDialog to select delimiter and & matching options for the import class sets process.
*
* @see org.BioLayoutExpress3D.Files.Dialogs.ImportSelectClassSetsDialog
* @author Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*/

public final class ImportClassSetsDialog extends JDialog implements ActionListener, CaretListener
{

    /**
    *  Serial version UID variable for the FilterNodesByWeight class.
    */
    public static final long serialVersionUID = 111222333444555799L;

    private static final String[] ALL_DELIMITERS = { ";", ":", ",", "#", "" };

    private String selectedDelimiter = ALL_DELIMITERS[0];
    private boolean selectedMatchFullName = false;
    private boolean selectedMatchCase = false;
    private boolean selectedMatchEntireName = false;

    private JRadioButton[] allDelimiterRadioButtons = null;
    private JCheckBox customDelimiterCheckBox = null;
    private JTextField customDelimiterTextField = null;

    private JCheckBox matchFullNameCheckBox = null;
    private JCheckBox matchCaseCheckBox = null;
    private JCheckBox matchEntireNameCheckBox = null;

    private JButton okButton = null;
    private JButton cancelButton = null;

    private ImportSelectClassSetsDialog importSelectClassSetsDialog = null;

    /**
    *  ImportClassSetsDialogListener listener to be used as a callback with the Classes selection when closing this dialog.
    */
    private ImportClassSetsDialogListener listener = null;

    public ImportClassSetsDialog(LayoutFrame layoutFrame)
    {
        super(layoutFrame, "Node Identifier Parsing Options", true);

        initComponents();

        // has to be afterwards so as to let ImportClassSetsDialog to initialize its size dimensions first
        importSelectClassSetsDialog = new ImportSelectClassSetsDialog(layoutFrame, this);
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
        ButtonGroup allDelimiterRadioButtonsGroup = new ButtonGroup();
        customDelimiterTextField = new JTextField();
        customDelimiterTextField.addCaretListener(this);
        customDelimiterCheckBox = new JCheckBox();
        customDelimiterCheckBox.addActionListener(this);

        JPanel matchNameOptionsPanel = new JPanel(true);
        matchFullNameCheckBox = new JCheckBox();
        matchFullNameCheckBox.addActionListener(this);
        matchCaseCheckBox = new JCheckBox();
        matchCaseCheckBox.addActionListener(this);
        matchEntireNameCheckBox = new JCheckBox();
        matchEntireNameCheckBox.addActionListener(this);

        okButton = new JButton();
        okButton.addActionListener(this);
        cancelButton = new JButton();
        cancelButton.addActionListener(this);

        delimiterOptionsPanel.setBorder(BorderFactory.createTitledBorder(null, "Delimiter (Divider) Options", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Tahoma", 0, 12)));

        for (int i = 0; i < ALL_DELIMITERS.length; i++)
        {
            allDelimiterRadioButtonsGroup.add(allDelimiterRadioButtons[i]);

            if (i == 4)
                allDelimiterRadioButtons[4].setText("  None  ");
            else
                allDelimiterRadioButtons[i].setText("  " + ALL_DELIMITERS[i] + "  ");
        }
        allDelimiterRadioButtons[0].setSelected(true);
        allDelimiterRadioButtons[0].requestFocus();

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

        matchNameOptionsPanel.setBorder(BorderFactory.createTitledBorder(null, "Match Name Options", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Tahoma", 0, 12)));

        matchFullNameCheckBox.setText("Match Full Name (Including Delimiter)");
        matchFullNameCheckBox.setToolTipText("Match Full Name (Including Delimiter)");
        matchCaseCheckBox.setText("Match Case");
        matchCaseCheckBox.setToolTipText("Match Case");
        matchEntireNameCheckBox.setText("Match Entire Name");
        matchEntireNameCheckBox.setToolTipText("Match Entire Name");

        GroupLayout matchNameOptionsPanelLayout = new GroupLayout(matchNameOptionsPanel);
        matchNameOptionsPanel.setLayout(matchNameOptionsPanelLayout);
        matchNameOptionsPanelLayout.setHorizontalGroup(
            matchNameOptionsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(matchNameOptionsPanelLayout.createSequentialGroup()
                .addGroup(matchNameOptionsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(matchFullNameCheckBox)
                    .addComponent(matchCaseCheckBox)
                    .addComponent(matchEntireNameCheckBox))
                .addContainerGap(0, Short.MAX_VALUE))
        );
        matchNameOptionsPanelLayout.setVerticalGroup(
            matchNameOptionsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(matchNameOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(matchFullNameCheckBox)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(matchCaseCheckBox)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(matchEntireNameCheckBox)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        JPanel buttonsPanel = new JPanel(true);
        buttonsPanel.setLayout( new BoxLayout(buttonsPanel, BoxLayout.X_AXIS) );
        okButton.setText("OK");
        okButton.setToolTipText("OK");
        cancelButton.setText("Cancel");
        cancelButton.setToolTipText("Cancel");
        buttonsPanel.add(okButton);
        buttonsPanel.add(cancelButton);

        GroupLayout layout = new GroupLayout( this.getContentPane() );
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                            .addComponent(matchNameOptionsPanel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(delimiterOptionsPanel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(buttonsPanel, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(delimiterOptionsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(matchNameOptionsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buttonsPanel)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        // delimiterOptionsPanel.getAccessibleContext().setAccessibleName("Delimiter (Divider) Options");
        // matchNameOptionsPanel.getAccessibleContext().setAccessibleName("Match Name Options");

        this.setResizable(false);
        this.pack();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.addWindowListener( new WindowAdapter()
        {
           @Override
            public void windowClosing(WindowEvent e)
            {
                closeDialogWindows(false);
            }
        } );
    }

    private void closeDialogWindows(boolean closeAndParse)
    {
        importSelectClassSetsDialog.closeDialogWindow();
        this.setVisible(false);

        if (closeAndParse && listener != null)
            listener.getSelectedClassSets(importSelectClassSetsDialog.getSelectedClassSets(), selectedDelimiter, selectedMatchFullName, selectedMatchCase, selectedMatchEntireName);
    }

    public void openDialogWindows(Set<String> allClassSets)
    {
        importSelectClassSetsDialog.updateImportSelectClassSetsTable(allClassSets);
        importSelectClassSetsDialog.openDialogWindow();
        // has to be last as this dialog is the modal one, the ImportSelectClassSetsDialog one is with APPLICATION_EXCLUDE turned on
        this.setVisible(true);
    }

    private boolean checkSelectedDelimiterForRegularExpressionCompatibility()
    {
        try
        {
            "testing".split(selectedDelimiter + "+");

            return true;
        }
        catch (PatternSyntaxException exc)
        {
            if (DEBUG_BUILD) org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.println("PatternSyntaxException in checkSelectedDelimiterForRegularExpressionCompatibility():\n" + exc.getMessage());
            JOptionPane.showMessageDialog(this, "The Custom Delimiter '" + selectedDelimiter + "' Cannot Be Accepted For Pattern Matching.\nPlease Use Another Custom Delimiter.", "Custom Delimiter Not Accepted!", JOptionPane.WARNING_MESSAGE);
            customDelimiterTextField.setText(" ");
            selectedDelimiter = " ";

            return false;
        }
    }

    public void doClickDelimiter1RadioButton()
    {
        allDelimiterRadioButtons[0].doClick();
        allDelimiterRadioButtons[0].requestFocus();

        if ( customDelimiterCheckBox.isSelected() )
            customDelimiterCheckBox.setSelected(false);

        matchFullNameCheckBox.setSelected(true);
        matchFullNameCheckBox.doClick();

        matchCaseCheckBox.setSelected(false);
        selectedMatchCase = false;

        matchEntireNameCheckBox.setSelected(false);
        selectedMatchEntireName = false;
    }

    public void doClickMatchFullNameAndCase()
    {
        if ( customDelimiterCheckBox.isSelected() )
            customDelimiterCheckBox.setSelected(false);

        matchFullNameCheckBox.setSelected(false);
        matchFullNameCheckBox.doClick();
        matchFullNameCheckBox.requestFocus();

        matchCaseCheckBox.setSelected(false);
        selectedMatchCase = false;

        matchEntireNameCheckBox.setSelected(false);
        selectedMatchEntireName = false;
    }

    private void setEnabledMatchFullNameMode(boolean enabled)
    {
        customDelimiterTextField.setEnabled(!enabled);
        customDelimiterCheckBox.setEnabled(!enabled);

        matchFullNameCheckBox.setSelected(enabled);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if ( e.getSource().equals(customDelimiterCheckBox) )
        {
            boolean radioButtonState = !customDelimiterCheckBox.isSelected();
            for (int i = 0; i < ALL_DELIMITERS.length; i++)
                allDelimiterRadioButtons[i].setEnabled(radioButtonState);

            if (!radioButtonState)
                selectedDelimiter = customDelimiterTextField.getText().trim();
        }
        else if ( e.getSource().equals(matchFullNameCheckBox) )
        {
            selectedMatchFullName = matchFullNameCheckBox.isSelected();
            if ( !customDelimiterCheckBox.isSelected() )
                for (int i = 0; i < ALL_DELIMITERS.length; i++)
                    allDelimiterRadioButtons[i].setEnabled(!selectedMatchFullName);
            customDelimiterTextField.setEnabled(!selectedMatchFullName);
            customDelimiterCheckBox.setEnabled(!selectedMatchFullName);

            if ( !selectedMatchFullName && allDelimiterRadioButtons[4].isSelected() )
            {
                allDelimiterRadioButtons[0].doClick();
                allDelimiterRadioButtons[0].requestFocus();
            }
        }
        else if ( e.getSource().equals(matchCaseCheckBox) )
        {
            selectedMatchCase = matchCaseCheckBox.isSelected();
        }
        else if ( e.getSource().equals(matchEntireNameCheckBox) )
        {
            selectedMatchEntireName = matchEntireNameCheckBox.isSelected();
        }
        else if ( e.getSource().equals(okButton) )
        {
            if ( ( customDelimiterCheckBox.isSelected() && !matchFullNameCheckBox.isSelected() )? checkSelectedDelimiterForRegularExpressionCompatibility() : true )
                closeDialogWindows(true);
        }
        else if ( e.getSource().equals(cancelButton) )
        {
            closeDialogWindows(false);
        }
        else // must be the radiobuttons' listeners
        {
            for (int i = 0; i < ALL_DELIMITERS.length; i++)
            {
                if ( e.getSource().equals(allDelimiterRadioButtons[i]) )
                {
                    selectedDelimiter = ALL_DELIMITERS[i];
                    setEnabledMatchFullNameMode(i == 4);
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
            }
        }
    }

    /**
    *  Sets the ImportClassSetsListener listener.
    */
    public void setListener(ImportClassSetsDialogListener listener)
    {
        this.listener = listener;
    }

    /**
    *  Removes the LayoutClassesTableListener listener.
    */
    public void removeListener()
    {
        listener = null;
    }

    /**
    *  ImportClassSetsDialogListener interface, used as a callback design pattern for the BioLayout Express 3D framework.
    */
    public interface ImportClassSetsDialogListener
    {
        /**
        *  This method is called as a callback event when a class color has changed in the table.
        */
        public void getSelectedClassSets(HashSet<String> selectedClassSets, String selectedDelimiter, boolean selectedMatchFullName, boolean selectedMatchCase, boolean selectedMatchEntireName);
    }


}