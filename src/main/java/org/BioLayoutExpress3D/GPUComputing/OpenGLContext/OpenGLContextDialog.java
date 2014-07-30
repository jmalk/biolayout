package org.BioLayoutExpress3D.GPUComputing.OpenGLContext;

import javax.swing.*;

/**
*
* OpenGLContextDialog is the main OpenGL context dialog window to encapsulate the OpenGL context for GPU Computing.
*
* @author Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*
*/

public class OpenGLContextDialog extends JFrame
{
    /**
    *  Serial version UID variable for the OpenGLContextDialog class.
    */
    public static final long serialVersionUID = 118222333444555669L;

    /**
    *  Constructor of the OpenGLContextDialog class.
    */
    public OpenGLContextDialog(OpenGLContext openGLContext)
    {
        super("GPU Computing OpenGL Context");

        this.getContentPane().add(openGLContext);

        this.pack();
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}