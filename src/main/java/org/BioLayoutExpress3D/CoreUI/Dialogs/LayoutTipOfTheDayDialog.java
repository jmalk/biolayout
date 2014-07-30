package org.BioLayoutExpress3D.CoreUI.Dialogs;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* @author Leon Goldovsky, full refactoring by Thanos Theo, 2008-2009
* @version 3.0.0.0
*
*/

public final class LayoutTipOfTheDayDialog extends JDialog implements ActionListener
{
    /**
    *  Serial version UID variable for the LayoutTipOfTheDayDialog class.
    */
    public static final long serialVersionUID = 111222333444555799L;

    private AbstractAction tipOfTheDayAction = null;
    private JEditorPane editorPane = null;
    private Random random = null;
    private int currentSlide = 0;

    public LayoutTipOfTheDayDialog(JFrame frame)
    {
        super(frame, "Tip Of The Day", false);

        initActions();
        initComponents();
    }

    private void initActions()
    {
        random = new Random();

        tipOfTheDayAction = new AbstractAction("Tip Of The Day")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555800L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                setLocationRelativeTo(null);
                setVisible(true);
            }
        };
    }

    private void initComponents()
    {
        JButton nextButton = new JButton("Next Tip");
        nextButton.setToolTipText("Next Tip");
        JButton prevButton = new JButton("Previous Tip");
        prevButton.setToolTipText("Previous Tip");
        JButton exitButton = new JButton("Exit");
        exitButton.setToolTipText("Exit");
        editorPane = new JEditorPane();
        editorPane.setEditable(false);
        JPanel headPanel = new JPanel(true);
        JPanel headSubPanel = new JPanel(true);
        headSubPanel.setLayout( new BoxLayout(headSubPanel, BoxLayout.Y_AXIS) );
        JPanel buttonPanel = new JPanel(true);

        URL url = this.getClass().getResource(IMAGE_FILES_PATH + "TipOfTheDay/Bulb.png");
        ImageIcon image = new ImageIcon( Toolkit.getDefaultToolkit().getImage(url) );

        headSubPanel.add( new JLabel(image) );
        headSubPanel.add( Box.createRigidArea( new Dimension(10, 5) ) );
        headSubPanel.add( new JLabel("      Did you know...") );
        headPanel.add(headSubPanel);

        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(exitButton);

        currentSlide = random.nextInt( MAX_HTML_TIPS.get() ) + 1;
        url = this.getClass().getResource("/Resources/Html/" + currentSlide + ".html");

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

        exitButton.addActionListener(this);
        nextButton.addActionListener(this);
        prevButton.addActionListener(this);

        this.getContentPane().setLayout( new BorderLayout() );
        this.getContentPane().add( scrollPane, BorderLayout.CENTER );
        this.getContentPane().add( new JPanel(true), BorderLayout.EAST );
        this.getContentPane().add( new JPanel(true), BorderLayout.WEST );
        this.getContentPane().add( headPanel, BorderLayout.NORTH );
        this.getContentPane().add( buttonPanel, BorderLayout.SOUTH );

        this.setResizable(false);
        this.pack();
        this.setSize(500, 525);
        this.setLocationRelativeTo(null);
        this.setVisible(false);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void setUrl(boolean next)
    {
        currentSlide += (next ? 1 : -1);

        if ( currentSlide > MAX_HTML_TIPS.get() )
            currentSlide = 1;

        if (currentSlide == 0)
            currentSlide = MAX_HTML_TIPS.get();

        URL url = this.getClass().getResource("/Resources/Html/" + currentSlide + ".html");

        try
        {
            editorPane.setPage(url);
        }
        catch (IOException ioe)
        {
            if (DEBUG_BUILD) println("Attempted to read a bad URL: " + url + "\n" + ioe.getMessage());
        }
    }

    public AbstractAction getTipOfTheDayAction()
    {
        return tipOfTheDayAction;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if ( e.getActionCommand().equals("Exit") )
        {
            setVisible(false);
        }
        else if ( e.getActionCommand().equals("Next Tip") )
        {
            setUrl(true);
        }
        else if ( e.getActionCommand().equals("Previous Tip") )
        {
            setUrl(false);
        }
    }


}