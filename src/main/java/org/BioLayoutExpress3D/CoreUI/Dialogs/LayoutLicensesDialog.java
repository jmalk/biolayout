package org.BioLayoutExpress3D.CoreUI.Dialogs;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* @author Full refactoring by Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*
*/

public final class LayoutLicensesDialog extends JDialog implements ActionListener
{
    /**
    *  Serial version UID variable for the LayoutLicensesDialog class.
    */
    public static final long serialVersionUID = 111222333444555696L;

    private AbstractAction licensesAction = null;
    private JButton okButton = null;

    public LayoutLicensesDialog(JFrame frame)
    {
        super(frame, "License Agreement For " + VERSION, true);

        initActions();
        initComponents();
    }

    private void initComponents()
    {
        JPanel topPanel = new JPanel(true);
        topPanel.setLayout( new BorderLayout() );
        this.getContentPane().add(topPanel);

        JTextArea textArea1 = new JTextArea();
        textArea1.setFont( this.getFont() );
        setBioLayoutExpress3DGeneralLicense(textArea1);
        textArea1.setEditable(false);
        textArea1.setCaretPosition(0); // so as to have the vertical scrollbar resetted to position 0
        JScrollPane scrollPane1 = new JScrollPane(textArea1);

        JTextArea textArea2 = new JTextArea();
        textArea2.setFont( this.getFont() );
        setBioLayoutExpress3DDetailedLicenses(textArea2);
        textArea2.setEditable(false);
        textArea2.setCaretPosition(0); // so as to have the vertical scrollbar resetted to position 0
        JScrollPane scrollPane2 = new JScrollPane(textArea2);

        okButton = new JButton("OK");
        okButton.addActionListener(this);
        okButton.setToolTipText("OK");

        scrollPane1.setBorder( BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "General Information") );
        scrollPane2.setBorder( BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "All Licenses In Detail") );
        topPanel.add(scrollPane1, BorderLayout.NORTH);
        topPanel.add(scrollPane2, BorderLayout.CENTER);
        topPanel.add(okButton, BorderLayout.SOUTH);

        this.pack();
        this.setSize( this.getWidth() + 20, (int)(this.getHeight() * ( (SCREEN_DIMENSION.height >= 900) ? ( (SCREEN_DIMENSION.height >= 1024) ? 0.725 : 0.7 ) : 0.6 ) ) );
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initActions()
    {
        licensesAction = new AbstractAction("Licenses")
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

    private void setBioLayoutExpress3DGeneralLicense(JTextArea textArea)
    {
        textArea.append(" - " + VERSION + "\n" + readLicenseFile(LICENSES_FILES_NAMES[LicensesFiles.LICENSE_BLE3D.ordinal()], false));
    }

    private void setBioLayoutExpress3DDetailedLicenses(JTextArea textArea)
    {
        textArea.append(readLicenseFile(LICENSES_FILES_NAMES[LicensesFiles.LICENSE_GPLV3.ordinal()], true) +
                        LICENSES_SEPARATOR +
                        readLicenseFile(LICENSES_FILES_NAMES[LicensesFiles.LICENSE_JOGL.ordinal()], true) +
                        LICENSES_SEPARATOR +
                        readLicenseFile(LICENSES_FILES_NAMES[LicensesFiles.LICENSE_JOCL.ordinal()], true) +
                        "\n");
    }

    /**
    *  Reads the license file.
    */
    private String readLicenseFile(String fileName, boolean useTabAtBeginningOfLine)
    {
        BufferedReader br = null;
        StringBuilder returnLicenseFileString = new StringBuilder();

        try
        {
            br = new BufferedReader( new InputStreamReader( LayoutLicensesDialog.class.getResourceAsStream(LICENSES_FILES_PATH + fileName) ) );
            String line = "";
            while ( ( line = br.readLine() ) != null )
            {
                if (useTabAtBeginningOfLine)
                    returnLicenseFileString.append("\t").append(line).append("\n");
                else
                    returnLicenseFileString.append(line).append("\n");
            }
        }
        catch (IOException ioExc)
        {
            if (DEBUG_BUILD) println("IOException caught in readLicenseFile():\n" + ioExc.getMessage());
        }
        finally
        {
            try
            {
                if (br != null) br.close();
            }
            catch (IOException ioExc)
            {
                if (DEBUG_BUILD) println("Not managed to close the license file with 'finally' clause in readLicenseFile() method:\n" + ioExc.getMessage());
            }
        }

        return returnLicenseFileString.toString();
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if ( e.getActionCommand().equals("OK") )
            setVisible(false);
    }

    public AbstractAction getLicensesAction()
    {
        return licensesAction;
    }


}