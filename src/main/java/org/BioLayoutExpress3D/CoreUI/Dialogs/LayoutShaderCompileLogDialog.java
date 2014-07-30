package org.BioLayoutExpress3D.CoreUI.Dialogs;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.StringReader;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public final class LayoutShaderCompileLogDialog extends JDialog implements ActionListener
{
    private AbstractAction javaPlatformCapsAction = null;
    private JButton okButton = null;
    private JTextArea textArea = null;
    private String error = "";
    private String source = "";

    public LayoutShaderCompileLogDialog(JFrame jframe)
    {
        super(jframe, "Shader Failed To Compile", false);

        initComponents();
    }

    private void initComponents()
    {
        JPanel topPanel = new JPanel(true);
        topPanel.setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setFont(Font.decode("Monospaced"));
        textArea.setEditable(false);
        textArea.setCaretPosition(0);
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

    private void rebuildReport()
    {
        textArea.setText("");

        if (error != null && !error.isEmpty())
        {
            textArea.append(error);
            textArea.append("\n");
        }

        if (source != null && !source.isEmpty())
        {
            BufferedReader br = new BufferedReader(new StringReader(source));

            int lineNumber = 1;
            String line;
            try
            {
                while ((line = br.readLine()) != null)
                {
                    textArea.append(Integer.toString(lineNumber++) + ": " + line + "\n");
                }
            }
            catch (Exception e)
            {
                textArea.append("Failed to read source");
            }
        }
    }

    public void setShaderSource(String source)
    {
        this.source = source;
        rebuildReport();
    }

    public void setShaderError(String error)
    {
        this.error = error;
        rebuildReport();
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getActionCommand().equals("OK"))
        {
            setVisible(false);
        }
    }
}