package org.BioLayoutExpress3D.Models;

/**
*
* ModelSettings class which holds information for the settings of a 3D model.
*
* @see org.BioLayoutExpress3D.Models.ModelRenderingStates
* @see org.BioLayoutExpress3D.Models.ModelShape
* @author Thanos Theo, 2011-2012
* @version 3.0.0.0
*/
public class ModelSettings
{

    /**
    *  ModelSettings usingNormals variable.
    */
    public boolean usingNormals = false;

    /**
    *  ModelSettings usingTexCoords variable.
    */
    public boolean usingTexCoords = false;

    /**
    *  ModelSettings modelRenderingState variable.
    */
    public ModelRenderingStates modelRenderingState = ModelRenderingStates.IMMEDIATE_MODE;

    /**
    *  ModelSettings shapeName variable.
    */
    public String shapeName = "";

    /**
    *  ModelSettings centerModel variable.
    */
    public boolean centerModel = false;

    /**
    *  Whether the model uses (a) texture(s).
    */
    public boolean hasTexture = false;

    /**
    *  The ModelSettings class first constructor.
    */
    public ModelSettings(boolean usingNormals)
    {
        this(usingNormals, false, ModelRenderingStates.IMMEDIATE_MODE, "");
    }

    /**
    *  The ModelSettings class second constructor.
    */
    public ModelSettings(boolean usingNormals, boolean usingTexCoords)
    {
        this(usingNormals, usingTexCoords, ModelRenderingStates.IMMEDIATE_MODE, "");
    }

    /**
    *  The ModelSettings class fourth constructor.
    */
    public ModelSettings(boolean usingNormals, boolean usingTexCoords, ModelRenderingStates modelRenderingState)
    {
        this(usingNormals, usingTexCoords, modelRenderingState, "");
    }

    /**
    *  The ModelSettings class fifth constructor.
    */
    public ModelSettings(boolean usingNormals, boolean usingTexCoords, ModelRenderingStates modelRenderingState, String shapeName)
    {
        this(usingNormals, usingTexCoords, modelRenderingState, shapeName, false);
    }

    /**
    *  The ModelSettings class sixth constructor.
    */
    public ModelSettings(boolean usingNormals, boolean usingTexCoords, ModelRenderingStates modelRenderingState, String shapeName, boolean centerModel)
    {
        this.usingNormals = usingNormals;
        this.usingTexCoords = usingTexCoords;
        this.modelRenderingState = modelRenderingState;
        this.shapeName = shapeName;
        this.centerModel = centerModel;
    }


}