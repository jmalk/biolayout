package org.BioLayoutExpress3D.CoreUI.Dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
*
*  A class designed to report the OpenGL Driver Capabilities in a dialog.
*
* @author Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*/

public final class LayoutOpenGLDriverCapsDialog extends JDialog implements ActionListener
{
    /**
    *  Serial version UID variable for the LayoutOpenGLDriverCapsDialog class.
    */
    public static final long serialVersionUID = 111222333444555696L;

    private AbstractAction openGLDriverCapsAction = null;
    private JButton okButton = null;
    private boolean doneOneInit = false;

    public LayoutOpenGLDriverCapsDialog(JFrame jframe)
    {
        super(jframe, "OpenGL Driver Capabilities", true);

        initActions();
    }

    private void initActions()
    {
        openGLDriverCapsAction = new AbstractAction("OpenGL Driver Caps")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555697L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (GL_EXTENSIONS_STRINGS != null && !doneOneInit)
                {
                    doneOneInit = true;
                    initComponents();
                }
                setLocationRelativeTo(null);
                setVisible(true);
            }
        };
    }

    private void initComponents()
    {
        JPanel topPanel = new JPanel(true);
        topPanel.setLayout( new BorderLayout() );

        JScrollPane scrollPane1 = null;
        if (USE_SHADERS_PROCESS)
        {
            JTextArea textArea1 = new JTextArea();
            textArea1.setFont( this.getFont() );
            setGLSLCaps(textArea1);
            textArea1.setEditable(false);
            textArea1.setCaretPosition(0); // so as to have the vertical scrollbar resetted to position 0
            scrollPane1 = new JScrollPane(textArea1);
        }

        JTextArea textArea2 = new JTextArea();
        textArea2.setFont( this.getFont() );
        setOpenGLDriverCaps(textArea2);
        textArea2.setEditable(false);
        textArea2.setCaretPosition(0); // so as to have the vertical scrollbar resetted to position 0
        JScrollPane scrollPane2 = new JScrollPane(textArea2);

        okButton = new JButton("OK");
        okButton.addActionListener(this);
        okButton.setToolTipText("OK");

        if (USE_SHADERS_PROCESS)
        {
            scrollPane1.setBorder( BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "GLSL Driver Capabilities") );
            topPanel.add(scrollPane1, BorderLayout.NORTH);
        }
        scrollPane2.setBorder( BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "OpenGL Driver Capabilities") );
        topPanel.add(scrollPane2, BorderLayout.CENTER);
        topPanel.add(okButton, BorderLayout.SOUTH);

        int dialogSizeX = 500;
        int dialogSizeY = 380;
        if (USE_SHADERS_PROCESS)
        {
            dialogSizeX = 550;
            dialogSizeY = 680;
            if (USE_GL_ARB_TEXTURE_RECTANGLE)
                dialogSizeY += 20;
            if (USE_GL_EXT_FRAMEBUFFER_OBJECT)
                dialogSizeY += 20;
        }

        this.getContentPane().add(topPanel);
        this.setResizable(false);
        this.pack();
        this.setSize(dialogSizeX, dialogSizeY);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void setGLSLCaps(JTextArea textArea)
    {
        textArea.append("\nGL_SHADING_LANGUAGE_VERSION:\t\t" + GL_SHADING_LANGUAGE_VERSION_STRING + "\n");
        textArea.append("GL_MAX_DRAW_BUFFERS:\t\t\t" + GL_MAX_DRAW_BUFFERS_INTEGER + "\n");
        textArea.append("GL_MAX_COLOR_ATTACHMENTS:\t\t" + GL_MAX_COLOR_ATTACHMENTS_INTEGER + "\n");
        textArea.append("GL_AUX_BUFFERS:\t\t\t" + GL_AUX_BUFFERS_INTEGER + "\n");
        textArea.append("GL_MAX_TEXTURE_UNITS:\t\t\t" + GL_MAX_TEXTURE_UNITS_INTEGER + "\n");
        textArea.append("GL_MAX_VERTEX_ATTRIBS:\t\t\t" + GL_MAX_VERTEX_ATTRIBS_INTEGER + "\n");
        textArea.append("GL_MAX_VERTEX_UNIFORM_COMPONENTS:\t\t" + GL_MAX_VERTEX_UNIFORM_COMPONENTS_INTEGER + "\n");
        textArea.append("GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS:\t\t" + GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS_INTEGER + "\n");
        textArea.append("GL_MAX_VARYING_FLOATS:\t\t\t" + GL_MAX_VARYING_FLOATS_INTEGER + "\n");
        textArea.append("GL_MAX_TEXTURE_IMAGE_UNITS:\t\t" + GL_MAX_TEXTURE_IMAGE_UNITS_INTEGER + "\n");
        textArea.append("GL_MAX_TEXTURE_COORDS:\t\t\t" + GL_MAX_TEXTURE_COORDS_INTEGER + "\n");
        textArea.append("GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS:\t" + GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS_INTEGER + "\n");
        textArea.append("GL_MAX_FRAGMENT_UNIFORM_COMPONENTS:\t" + GL_MAX_FRAGMENT_UNIFORM_COMPONENTS_INTEGER + "\n");
        textArea.append("GL_MAX_3D_TEXTURE_SIZE:\t\t\t" + GL_MAX_3D_TEXTURE_SIZE_INTEGER + "\n");
        if (USE_GL_ARB_TEXTURE_RECTANGLE)
            textArea.append("GL_MAX_RECTANGLE_TEXTURE_SIZE_ARB:\t\t" + GL_MAX_RECTANGLE_TEXTURE_SIZE_ARB_INTEGER + "\n");
        textArea.append("GL_MAX_TEXTURE_SIZE:\t\t\t" + GL_MAX_TEXTURE_SIZE_INTEGER + "\n");
        if (USE_GL_EXT_FRAMEBUFFER_OBJECT)
            textArea.append("GL_MAX_RENDERBUFFER_SIZE_EXT:\t\t" + GL_MAX_RENDERBUFFER_SIZE_EXT_INTEGER + "\n");
        textArea.append("\n");
        textArea.append("GL GPU SHADER MODEL 4 SUPPORT:\t\t" + ( (USE_GL_EXT_GPU_SHADER4) ? "YES" : "NO" ) + "\n");
        textArea.append("GL GPU SHADER MODEL 5 SUPPORT:\t\t" + ( (USE_GL_ARB_GPU_SHADER5) ? "YES" : "NO" ) + "\n");
        textArea.append("GL GPU SHADER FP64 SUPPORT:\t\t" + ( (USE_GL_ARB_GPU_SHADER_FP64) ? "YES" : "NO" ) + "\n");
    }

    private void setOpenGLDriverCaps(JTextArea textArea)
    {
        textArea.append("\nGL_VENDOR:\t\t" + GL_VENDOR_STRING + "\n");
        textArea.append("GL_RENDERER:\t" + ( (IS_MAC) ? "\t" : "" ) + GL_RENDERER_STRING + "\n");
        textArea.append("GL_VERSION:\t\t"  + GL_VERSION_STRING + "\n");
        textArea.append("\n");
        textArea.append("\n" + GL_EXTENSIONS_STRINGS.length + " available GL Extensions: " + "\n");
        textArea.append("\n");
        for (String glExtension : GL_EXTENSIONS_STRINGS)
            textArea.append(glExtension + "\n");
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if ( e.getActionCommand().equals("OK") )
            setVisible(false);
    }

    public AbstractAction getOpenGLDriverCapsAction()
    {
        return openGLDriverCapsAction;
    }


}