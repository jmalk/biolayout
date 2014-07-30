package org.BioLayoutExpress3D.Graph.Camera.CameraUI.Dialogs;

import org.BioLayoutExpress3D.Graph.Camera.CameraUI.*;

/**
*
* GraphAnaglyphGlasses3DOptionsDialogListener interface, used as a callback design pattern for the BioLayout Express 3D framework.
*
* @author Thanos Theo, 2011
* @version 3.0.0.0
*
*/

public interface GraphAnaglyphGlasses3DOptionsDialogListener
{

    /**
    *  This method is called as a callback event when updating any preferences in the GraphAnaglyphGlasses3DOptionsDialog.
    */
    public void updateGraphAnaglyphGlasses3DOptionsDialogPreferencesCallBack(GraphAnaglyphGlassesTypes graphAnaglyphGlassesType, GraphIntraOcularDistanceTypes graphIntraOcularDistanceType);


}