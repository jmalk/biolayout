package org.BioLayoutExpress3D.Graph.ActiveRendering;

/**
*
* GraphRendererThreadUpdaterInterface interface defines the minimum method contract requirements for the GraphRendererThreadUpdater class.
*
* @author Thanos Theo, 2011
* @version 3.0.0.0
*
*/

public interface GraphRendererThreadUpdaterInterface
{

    /**
    *  Generally pauses the GraphRendererThreadUpdater.
    */
    public void generalPauseRenderUpdateThread();

    /**
    *  Generally resumes the GraphRendererThreadUpdater.
    */
    public void generalResumeRenderUpdateThread();


}
