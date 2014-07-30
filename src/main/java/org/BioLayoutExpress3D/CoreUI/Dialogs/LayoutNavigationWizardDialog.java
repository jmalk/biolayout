package org.BioLayoutExpress3D.CoreUI.Dialogs;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.event.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* @author Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*
*/

public final class LayoutNavigationWizardDialog extends JDialog implements ActionListener, HyperlinkListener
{
    /**
    *  Serial version UID variable for the LayoutNavigationWizardDialog class.
    */
    public static final long serialVersionUID = 111222333444555700L;

    private LayoutFrame layoutFrame = null;

    private AbstractAction navigationWizardAction = null;
    private JEditorPane editorPane = null;
    private JCheckBox showNavigationWizardOnStartupCheckBox = null;
    private JButton closeButton = null;

    /**
    *  The constructor of the LayoutNavigationWizardDialog class.
    */
    public LayoutNavigationWizardDialog(LayoutFrame layoutFrame)
    {
        super(layoutFrame, "Navigation Wizard", false);

        this.layoutFrame = layoutFrame;

        initActions();
        initComponents();
    }

    /**
    *  This method is called from within the constructor to initialize the Navigation Wizard dialog actions.
    */
    private void initActions()
    {
        navigationWizardAction = new AbstractAction("Navigation Wizard")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555800L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                setLocationRelativeTo(null);
                openDialogWindow();
            }
        };
    }

    /**
    *  This method is called from within the constructor to initialize the Navigation Wizard dialog components.
    */
    private void initComponents()
    {
        editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.addHyperlinkListener(this);
        showNavigationWizardOnStartupCheckBox = new JCheckBox("Show Navigation Wizard On Startup");
        showNavigationWizardOnStartupCheckBox.setToolTipText("Show Navigation Wizard On Startup");
        showNavigationWizardOnStartupCheckBox.addActionListener(this);
        showNavigationWizardOnStartupCheckBox.setSelected( SHOW_NAVIGATION_WIZARD_ON_STARTUP.get() );
        closeButton = new JButton("  Close  ");
        closeButton.setToolTipText("Close");
        closeButton.addActionListener(this);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT), true);
        buttonPanel.add(showNavigationWizardOnStartupCheckBox);
        buttonPanel.add( Box.createRigidArea( new Dimension(75, 10) ) );
        buttonPanel.add(closeButton);

        URL url = LayoutTipOfTheDayDialog.class.getResource("/Resources/Html/NavigationWizard.html");

        try
        {
            editorPane.setPage(url);
        }
        catch (IOException ioe)
        {
            if (DEBUG_BUILD) println("Attempted to read a bad URL: " + url + "\n" + ioe.getMessage());
        }

        editorPane.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(editorPane);

        this.getContentPane().setLayout( new BorderLayout() );
        this.getContentPane().add( new JPanel(true), BorderLayout.NORTH );
        this.getContentPane().add( scrollPane, BorderLayout.CENTER );
        this.getContentPane().add( new JPanel(true), BorderLayout.EAST );
        this.getContentPane().add( new JPanel(true), BorderLayout.WEST );
        this.getContentPane().add( buttonPanel, BorderLayout.SOUTH );

        this.setResizable(false);
        this.pack();
        this.setSize(650, 735);
        this.setLocationRelativeTo(null);
        this.setVisible(false);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    /**
    *  Opens the Navigation Wizard dialog window.
    */
    public void openDialogWindow()
    {
        this.setVisible(true);
    }

    /**
    *  Gets the Navigation Wizard action.
    */
    public AbstractAction getNavigationWizardAction()
    {
        return navigationWizardAction;
    }

    /**
    *  Executes the actionPerformed() callback for the Navigation Wizard dialog window.
    */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        if ( e.getSource().equals(closeButton) )
        {
            setVisible(false);
        }
        else if ( e.getSource().equals(showNavigationWizardOnStartupCheckBox) )
        {
            SHOW_NAVIGATION_WIZARD_ON_STARTUP.set( showNavigationWizardOnStartupCheckBox.isSelected() );
            layoutFrame.getLayoutGraphPropertiesDialog().setHasNewPreferencesBeenApplied(true);
        }
    }

    /**
    *  Executes the hyperlinkUpdate() callback for the Navigation Wizard dialog window.
    */
    @Override
    public void hyperlinkUpdate(HyperlinkEvent e)
    {
        if (DEBUG_BUILD) println( e.getEventType() );

        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
        {
            URL url = e.getURL();
            if (DEBUG_BUILD) println(url);
            InitDesktop.browse(url);
        }
    }


}