package org.BioLayoutExpress3D.CoreUI.Dialogs;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.event.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* @author Leon Goldovsky, full refactoring by Thanos Theo, 2008-2009
* @version 3.0.0.0
*
*/

public final class LayoutAboutDialog extends JDialog implements ActionListener, HyperlinkListener
{
    /**
    *  Serial version UID variable for the LayoutAboutDialog class.
    */
    public static final long serialVersionUID = 111222333444555670L;

    private JLabel image = null;
    private JButton okButton = null;
    private JLabel actionLabel = null;
    private AbstractAction aboutAction = null;

    public LayoutAboutDialog(JFrame frame, boolean isSplash)
    {
        super(frame, "About " + VERSION, !isSplash);

        this.setIconImage(BIOLAYOUT_ICON_IMAGE);
        image = new JLabel( new ImageIcon(BIOLAYOUT_EXDPRESS_3D_LOGO_IMAGE) );

        if (isSplash)
        {
            actionLabel = new JLabel(" Loading...");
            actionLabel.setBackground(Color.WHITE);

            initComponentsForSplashDialog();
        }
        else
        {
            okButton = new JButton("OK");
            okButton.setToolTipText("OK");

            initComponentsForAboutDialog();
        }
    }

    private void initComponentsForSplashDialog()
    {
        this.setUndecorated(true);
        this.setResizable(false);
        this.getContentPane().setLayout( new BorderLayout() );
        this.getContentPane().add(image, BorderLayout.CENTER);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel layoutPanel = new JPanel(true);
        layoutPanel.setLayout( new BorderLayout() );
        layoutPanel.add(actionLabel, BorderLayout.CENTER);

        this.getContentPane().add(layoutPanel, BorderLayout.SOUTH);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void initComponentsForAboutDialog()
    {
        URL url = LayoutAboutDialog.class.getResource("/Resources/Html/About.html");

        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.addHyperlinkListener(this);

        try
        {
            editorPane.setPage(url);
        }
        catch (IOException ioExc)
        {
            if (DEBUG_BUILD) println("Attempted to read a bad URL: " + url + "\n" + ioExc.getMessage());
        }

        okButton.addActionListener(this);
        this.setLayout( new BorderLayout() );
        this.add(editorPane, BorderLayout.CENTER);
        this.add(okButton, BorderLayout.SOUTH);

        this.setResizable(false);
        this.pack();
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);

        aboutAction = new AbstractAction("About")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555671L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                setLocationRelativeTo(null);
                setVisible(true);
            }
        };
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if ( e.getActionCommand().equals("OK") )
            setVisible(false);
    }

    @Override
    public void hyperlinkUpdate(HyperlinkEvent ev)
    {
        if (DEBUG_BUILD) println( ev.getEventType() );

        if (ev.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
        {
            URL url = ev.getURL();
            if (DEBUG_BUILD) println(url);
            InitDesktop.browse(url);
        }
    }

    public void finishedLoading()
    {
        this.setVisible(false);
        this.dispose();
    }

    public void setText(String string)
    {
        actionLabel.setText(" " + string);
    }

    public AbstractAction getAboutAction()
    {
        return aboutAction;
    }


}