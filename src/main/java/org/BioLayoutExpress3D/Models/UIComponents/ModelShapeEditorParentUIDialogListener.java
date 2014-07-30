package org.BioLayoutExpress3D.Models.UIComponents;

import org.BioLayoutExpress3D.Models.*;

/**
*
* ModelShapeEditorParentUIDialogListener interface, used as a callback design pattern for the BioLayout Express 3D framework.
*
* @author Thanos Theo, 2011
* @version 3.0.0.0
*
*/

public interface ModelShapeEditorParentUIDialogListener
{

    /**
    *  This method is called as a callback event when updating any preferences in the ModelShapeEditorParentUIDialog.
    */
    public void updateModelShapeEditorParentUIDialogPreferencesCallBack(ModelTypes modelType);


}