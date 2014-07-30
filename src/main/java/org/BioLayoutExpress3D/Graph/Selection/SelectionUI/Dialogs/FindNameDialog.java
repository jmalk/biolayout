package org.BioLayoutExpress3D.Graph.Selection.SelectionUI.Dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.BioLayoutExpress3D.CoreUI.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
*
* User: icases
* Date: 03-sep-02
*
* @author Full refactoring by Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public final class FindNameDialog extends JDialog
{
    /**
    *  Serial version UID variable for the FindDialog class.
    */
    public static final long serialVersionUID = 111222333444555747L;

    private LayoutFrame layoutFrame = null;
    private JCheckBox matchCaseCheckBox = null;
    private JCheckBox matchEntireNameCheckBox = null;
    private JTextField textField = null;
    private AbstractAction findNameDialogAction = null;

    public FindNameDialog(LayoutFrame layoutFrame, JFrame jFrame)
    {
        super(jFrame, "Find By Name", true);

        this.layoutFrame = layoutFrame;

        initActions();
        initComponents(jFrame);
    }

    private void initActions()
    {
        findNameDialogAction = new AbstractAction("Find By Name")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555682L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                showDialog();
            }
        };
        findNameDialogAction.setEnabled(false);
    }

    private void initComponents(final JFrame jFrame)
    {
        JLabel textLabel = new JLabel("Please Type the Name of the Node:");
        textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        matchCaseCheckBox = new JCheckBox("Match Case", true);
        matchCaseCheckBox.setSelected(false);
        matchCaseCheckBox.setToolTipText("Match Case");
        matchEntireNameCheckBox = new JCheckBox("Match Entire Name", true);
        matchEntireNameCheckBox.setSelected(false);
        matchEntireNameCheckBox.setToolTipText("Match Entire Name");
        textField = new JTextField(10);
        textField.setMaximumSize( new Dimension(150, 20) );
        textField.setToolTipText("Name");

        AbstractAction findAction = new AbstractAction("Find")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555748L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                setVisible(false);
                layoutFrame.getGraph().getSelectionManager().findName(jFrame, textField.getText(), matchCaseCheckBox.isSelected(), matchEntireNameCheckBox.isSelected(), true);
            }
        };

        AbstractAction findMoreAction = new AbstractAction("Find More")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555749L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                setVisible(false);
                layoutFrame.getGraph().getSelectionManager().findName(jFrame, textField.getText(), matchCaseCheckBox.isSelected(), matchEntireNameCheckBox.isSelected(), false);

            }
        };

        AbstractAction cancelAction = new AbstractAction("Cancel")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555750L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                setVisible(false);
            }
        };

        JButton findButton = new JButton(findAction);
        findButton.setToolTipText("Find");
        JButton findMoreButton = new JButton(findMoreAction);
        findMoreButton.setToolTipText("Find More");
        JButton cancelButton = new JButton(cancelAction);
        cancelButton.setToolTipText("Cancel");

        JPanel inputPanel = new JPanel(true);
        inputPanel.add(textLabel);
        inputPanel.add(Box.createRigidArea( new Dimension(5, 0) ));
        inputPanel.add(textField);

        JPanel buttonsPanel = new JPanel(true);
        buttonsPanel.add(findButton);
        buttonsPanel.add(findMoreButton);
        buttonsPanel.add(cancelButton);

        JPanel optionsPanel = new JPanel(true);
        optionsPanel.add(matchCaseCheckBox);
        optionsPanel.add(matchEntireNameCheckBox);
        optionsPanel.setMinimumSize( new Dimension(400, 50) );

        JPanel containerPanel = new JPanel(true);
        containerPanel.setLayout( new BoxLayout(containerPanel, BoxLayout.Y_AXIS) );
        containerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        containerPanel.add(inputPanel);
        containerPanel.add( Box.createRigidArea( new Dimension(0, 5) ) );
        containerPanel.add(optionsPanel);
        containerPanel.add( Box.createRigidArea( new Dimension(0, 5) ) );
        containerPanel.add(buttonsPanel);

        this.getContentPane().add(containerPanel);
        this.getRootPane().setDefaultButton(findButton);
        this.setResizable(false);
        this.pack();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }

    public void showDialog()
    {
        textField.setSelectionStart(0);
        textField.setSelectionEnd( textField.getText().length() );

        this.setVisible(true);
    }

    public AbstractAction getFindNameDialogAction()
    {
        return findNameDialogAction;
    }


}