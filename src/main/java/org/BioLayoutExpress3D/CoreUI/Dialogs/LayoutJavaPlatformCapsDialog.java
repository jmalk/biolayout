package org.BioLayoutExpress3D.CoreUI.Dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import org.BioLayoutExpress3D.Layout;

/**
*
*  A class designed to report the Java Platform Capabilities in a dialog.
*
* @author Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*/

public final class LayoutJavaPlatformCapsDialog extends JDialog implements ActionListener
{
    /**
    *  Serial version UID variable for the LayoutJavaPlatformCapsDialog class.
    */
    public static final long serialVersionUID = 111222333444555696L;

    private AbstractAction javaPlatformCapsAction = null;
    private JButton okButton = null;

    public LayoutJavaPlatformCapsDialog(JFrame jframe)
    {
        super(jframe, "Java Platform Capabilities", true);

        initActions();
        initComponents();
    }

    private void initActions()
    {
        javaPlatformCapsAction = new AbstractAction("Java Platform Caps")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555697L;

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
        JPanel topPanel = new JPanel(true);
        topPanel.setLayout( new BorderLayout() );

        JTextArea textArea = new JTextArea();
        textArea.setFont( Font.decode("Monospaced") );
        setJavaPlatformCaps(textArea);
        textArea.setEditable(false);
        textArea.setCaretPosition(0); // so as to have the vertical scrollbar reset to position 0
        JScrollPane scrollPane = new JScrollPane(textArea);

        okButton = new JButton("OK");
        okButton.addActionListener(this);
        okButton.setToolTipText("OK");

        topPanel.add(scrollPane, BorderLayout.CENTER);
        topPanel.add(okButton, BorderLayout.SOUTH);

        this.getContentPane().add(topPanel);
        this.setResizable(false);
        this.pack();
        this.setSize(550, 700);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void setJavaPlatformCaps(JTextArea textArea)
    {
        textArea.append(Layout.getJavaPlatformCaps());
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if ( e.getActionCommand().equals("OK") )
            setVisible(false);
    }

    public AbstractAction getJavaPlatformCapsAction()
    {
        return javaPlatformCapsAction;
    }


}