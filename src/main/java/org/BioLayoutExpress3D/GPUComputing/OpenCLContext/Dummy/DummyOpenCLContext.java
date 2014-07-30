package org.BioLayoutExpress3D.GPUComputing.OpenCLContext.Dummy;

import javax.swing.*;
import org.jocl.*;
import org.BioLayoutExpress3D.GPUComputing.OpenCLContext.*;

/**
*
* OpenCLContext is the main OpenCL context component for a Dummy OpenCL context.
*
* @author Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*
*/

public class DummyOpenCLContext extends OpenCLContext
{

    /**
    *  Serial version UID variable for the OpenGLContext class.
    */
    public static final long serialVersionUID = 117222333444555669L;

    /**
    *  The first constructor of the DummyOpenCLContext class.
    */
    public DummyOpenCLContext(JFrame jFrame)
    {
        super(jFrame);
    }

    /**
    *  The second constructor of the DummyOpenCLContext class.
    */
    public DummyOpenCLContext(JFrame jFrame, boolean dialogErrorLog)
    {
        super(jFrame, dialogErrorLog);
    }

    /**
    *  The third constructor of the DummyOpenCLContext class.
    */
    public DummyOpenCLContext(JFrame jFrame, boolean dialogErrorLog, boolean profileCommandQueue)
    {
        super(jFrame, dialogErrorLog, profileCommandQueue);
    }

    /**
    *  The fourth constructor of the DummyOpenCLContext class.
    */
    public DummyOpenCLContext(JFrame jFrame, boolean dialogErrorLog, boolean profileCommandQueue, boolean openCLSupportAndExtensionsLogOnly)
    {
        super(jFrame, dialogErrorLog, profileCommandQueue, openCLSupportAndExtensionsLogOnly);
    }

    /**
    *  Initializes CPU memory.
    */
    @Override
    protected void initializeCPUMemoryImplementation() throws CLException, OutOfMemoryError {}

    /**
    *  Initializes GPU memory.
    */
    @Override
    protected void initializeGPUMemory() throws CLException {}

    /**
    *  Performs the GPU Computing calculations.
    */
    @Override
    protected void performGPUComputingCalculations() throws CLException {}

    /**
    *  Retrieves GPU results.
    */
    @Override
    protected void retrieveGPUResultsImplementation() throws CLException, OutOfMemoryError {}

    /**
    *  Deletes the OpenGL context for GPU computing.
    */
    @Override
    protected void deleteOpenCLContextForGPUComputing() throws CLException {}


}