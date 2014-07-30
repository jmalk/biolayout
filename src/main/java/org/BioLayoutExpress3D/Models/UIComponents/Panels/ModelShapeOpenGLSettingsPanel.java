package org.BioLayoutExpress3D.Models.UIComponents.Panels;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import org.BioLayoutExpress3D.Models.*;
import org.BioLayoutExpress3D.Models.UIComponents.*;
import static org.BioLayoutExpress3D.Models.ModelTypes.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
*
* The ModelShapeOpenGLSettingsPanel class which is the UI placeholder for the OpenGL settings for the OpenGL canvas & renderer of various model shapes.
*
* @see org.BioLayoutExpress3D.Models.UIComponents.ModelShapeEditorParentUIDialog
* @see org.BioLayoutExpress3D.Models.UIComponents.ModelShapeRenderer
* @author Thanos Theo, 2011
* @version 3.0.0.0
*
*/

public class ModelShapeOpenGLSettingsPanel extends JPanel implements ActionListener, ChangeListener
{

    private static final int SCALE_STEPS_MIN = -10;
    private static final int SCALE_STEPS_MAX = 20;
    private static final int SCALE_STEPS_DELTA = 1;
    private static final int SCALE_STEPS_FACTOR = 10;
    private static final int SCALE_STEPS_RATIO = 10;
    private static final int ROTATE_STEPS_MIN = 0;
    private static final int ROTATE_STEPS_MAX = 360;
    private static final int ROTATE_STEPS_DELTA = 15;
    private static final int ROTATE_STEPS_FACTOR = 12;

    private JSlider scaleXSlider = null;
    private JSlider scaleYSlider = null;
    private JSlider scaleZSlider = null;
    private JSlider rotateXSlider = null;
    private JSlider rotateYSlider = null;
    private JSlider rotateZSlider = null;

    private JCheckBox wireframeViewCheckBox = null;
    private JCheckBox normalsViewCheckBox = null; //FIXME remove this
    private JCheckBox textureViewCheckBox = null;
    private JCheckBox sphericalMappingCheckBox = null;
    private JCheckBox autoRotateViewCheckBox = null;
    private JButton resetSettingsButton = null;
    private JSlider tesselationSlider = null;
    private JLabel tesselationLabel = null;

    /**
    *  The ModelShapeRenderer reference.
    */
    private ModelShapeRenderer modelShapeRenderer = null;

    /**
    *  The ModelShapeEditorParentUIDialog reference.
    */
    private ModelShapeEditorParentUIDialog modelShapeEditorParentUIDialog = null;

    /**
    *  The ModelTypes reference.
    */
    private ModelTypes modelType = null;

    /**
    *  The ModelShapeOpenGLSettingsPanel class constructor.
    */
    public ModelShapeOpenGLSettingsPanel(ModelShapeRenderer modelShapeRenderer, ModelShapeEditorParentUIDialog modelShapeEditorParentUIDialog)
    {
        super(new BorderLayout(), true);

        this.modelShapeRenderer = modelShapeRenderer;
        this.modelShapeEditorParentUIDialog = modelShapeEditorParentUIDialog;

        initComponents();
    }

    /**
    *  Initializes the UI components for this panel.
    */
    private void initComponents()
    {
        scaleXSlider = new JSlider(JSlider.HORIZONTAL);
        scaleXSlider.setMinimum(SCALE_STEPS_MIN);
        scaleXSlider.setMaximum(SCALE_STEPS_MAX);
        scaleXSlider.setValue(0);
        scaleXSlider.setMajorTickSpacing(SCALE_STEPS_FACTOR * SCALE_STEPS_DELTA);
        scaleXSlider.setMinorTickSpacing(SCALE_STEPS_DELTA);
        scaleXSlider.setPaintTicks(true);
        scaleXSlider.setPaintLabels(true);
        scaleXSlider.addChangeListener(this);
        scaleXSlider.setToolTipText("Scale X Axis");
        scaleYSlider = new JSlider(JSlider.HORIZONTAL);
        scaleYSlider.setMinimum(SCALE_STEPS_MIN);
        scaleYSlider.setMaximum(SCALE_STEPS_MAX);
        scaleYSlider.setValue(0);
        scaleYSlider.setMajorTickSpacing(SCALE_STEPS_FACTOR * SCALE_STEPS_DELTA);
        scaleYSlider.setMinorTickSpacing(SCALE_STEPS_DELTA);
        scaleYSlider.setPaintTicks(true);
        scaleYSlider.setPaintLabels(true);
        scaleYSlider.addChangeListener(this);
        scaleYSlider.setToolTipText("Scale Y Axis");
        scaleZSlider = new JSlider(JSlider.HORIZONTAL);
        scaleZSlider.setMinimum(SCALE_STEPS_MIN);
        scaleZSlider.setMaximum(SCALE_STEPS_MAX);
        scaleZSlider.setValue(0);
        scaleZSlider.setMajorTickSpacing(SCALE_STEPS_FACTOR * SCALE_STEPS_DELTA);
        scaleZSlider.setMinorTickSpacing(SCALE_STEPS_DELTA);
        scaleZSlider.setPaintTicks(true);
        scaleZSlider.setPaintLabels(true);
        scaleZSlider.addChangeListener(this);
        scaleZSlider.setToolTipText("Scale Z Axis");
        rotateXSlider = new JSlider(JSlider.HORIZONTAL);
        rotateXSlider.setMinimum(ROTATE_STEPS_MIN);
        rotateXSlider.setMaximum(ROTATE_STEPS_MAX);
        rotateXSlider.setValue(0);
        rotateXSlider.setMajorTickSpacing(ROTATE_STEPS_FACTOR * ROTATE_STEPS_DELTA);
        rotateXSlider.setMinorTickSpacing(ROTATE_STEPS_DELTA);
        rotateXSlider.setPaintTicks(true);
        rotateXSlider.setPaintLabels(true);
        rotateXSlider.addChangeListener(this);
        rotateXSlider.setToolTipText("Rotate X Axis");
        rotateYSlider = new JSlider(JSlider.HORIZONTAL);
        rotateYSlider.setMinimum(ROTATE_STEPS_MIN);
        rotateYSlider.setMaximum(ROTATE_STEPS_MAX);
        rotateYSlider.setValue(0);
        rotateYSlider.setMajorTickSpacing(ROTATE_STEPS_FACTOR * ROTATE_STEPS_DELTA);
        rotateYSlider.setMinorTickSpacing(ROTATE_STEPS_DELTA);
        rotateYSlider.setPaintTicks(true);
        rotateYSlider.setPaintLabels(true);
        rotateYSlider.addChangeListener(this);
        rotateYSlider.setToolTipText("Rotate Y Axis");
        rotateZSlider = new JSlider(JSlider.HORIZONTAL);
        rotateZSlider.setMinimum(ROTATE_STEPS_MIN);
        rotateZSlider.setMaximum(ROTATE_STEPS_MAX);
        rotateZSlider.setValue(0);
        rotateZSlider.setMajorTickSpacing(ROTATE_STEPS_FACTOR * ROTATE_STEPS_DELTA);
        rotateZSlider.setMinorTickSpacing(ROTATE_STEPS_DELTA);
        rotateZSlider.setPaintTicks(true);
        rotateZSlider.setPaintLabels(true);
        rotateZSlider.addChangeListener(this);
        rotateZSlider.setToolTipText("Rotate Z Axis");

        JPanel scaleSlidersPanel = new JPanel(new BorderLayout(), true);
        scaleSlidersPanel.setLayout( new BoxLayout(scaleSlidersPanel, BoxLayout.X_AXIS) );
        scaleSlidersPanel.add( new JLabel("  Scale X/Y/Z: ") );
        scaleSlidersPanel.add( Box.createRigidArea( new Dimension(10, 10) ) );
        scaleSlidersPanel.add(scaleXSlider);
        scaleSlidersPanel.add(scaleYSlider);
        scaleSlidersPanel.add(scaleZSlider);

        JPanel rotateSlidersPanel = new JPanel(new BorderLayout(), true);
        rotateSlidersPanel.setLayout( new BoxLayout(rotateSlidersPanel, BoxLayout.X_AXIS) );
        rotateSlidersPanel.add( new JLabel("Rotate X/Y/Z: ") );
        rotateSlidersPanel.add(rotateXSlider);
        rotateSlidersPanel.add(rotateYSlider);
        rotateSlidersPanel.add(rotateZSlider);

        JPanel upperUIPanel = new JPanel(true);
        upperUIPanel.setLayout( new BoxLayout(upperUIPanel, BoxLayout.Y_AXIS) );
        if (!IS_MAC) upperUIPanel.add( Box.createRigidArea( new Dimension(5, 5) ) );
        upperUIPanel.add(scaleSlidersPanel);
        upperUIPanel.add(rotateSlidersPanel);

        wireframeViewCheckBox = new JCheckBox();
        wireframeViewCheckBox.addActionListener(this);
        wireframeViewCheckBox.setText("Wireframe View");
        wireframeViewCheckBox.setToolTipText( "Wireframe View");
        normalsViewCheckBox = new JCheckBox();
        normalsViewCheckBox.addActionListener(this);
        normalsViewCheckBox.setText("Normals View");
        normalsViewCheckBox.setToolTipText("Normals View; note that Normals use Geometry Shaders Technology (if available");
        normalsViewCheckBox.setEnabled(false);
        textureViewCheckBox = new JCheckBox();
        textureViewCheckBox.addActionListener(this);
        textureViewCheckBox.setText("Texture View");
        textureViewCheckBox.setToolTipText("Texture View");
        sphericalMappingCheckBox = new JCheckBox();
        sphericalMappingCheckBox.addActionListener(this);
        sphericalMappingCheckBox.setText("Spherical Mapping");
        sphericalMappingCheckBox.setToolTipText("Spherical Mapping");
        sphericalMappingCheckBox.setEnabled(false);
        autoRotateViewCheckBox = new JCheckBox();
        autoRotateViewCheckBox.addActionListener(this);
        autoRotateViewCheckBox.setText("AutoRotate View");
        autoRotateViewCheckBox.setToolTipText("AutoRotate View");
        autoRotateViewCheckBox.setSelected( MODEL_SHAPE_EDITOR_AUTOROTATE_VIEW.get() );
        resetSettingsButton = new JButton();
        resetSettingsButton.addActionListener(this);
        resetSettingsButton.setText("Reset Settings");
        resetSettingsButton.setToolTipText("Reset Settings");
        tesselationSlider = new JSlider(JSlider.HORIZONTAL);
        tesselationSlider.setMinimum(0);
        tesselationSlider.setMaximum(NODE_TESSELATION_MAX_VALUE);
        tesselationSlider.setValue( NODE_TESSELATION.get() );
        tesselationSlider.setMajorTickSpacing(10);
        tesselationSlider.setMinorTickSpacing(2);
        tesselationSlider.setPaintTicks(true);
        tesselationSlider.setPaintLabels(true);
        tesselationSlider.addChangeListener(this);
        tesselationSlider.setToolTipText("Tesselation");
        tesselationLabel = new JLabel( "Tesselation: " + NODE_TESSELATION.get() );
        tesselationLabel.setToolTipText( "Tesselation: " + NODE_TESSELATION.get() );

        JPanel checkBoxesPanel1 = new JPanel(new BorderLayout(), true);
        checkBoxesPanel1.setLayout( new BoxLayout(checkBoxesPanel1, BoxLayout.Y_AXIS) );
        checkBoxesPanel1.add(wireframeViewCheckBox);
        checkBoxesPanel1.add(normalsViewCheckBox);

        JPanel checkBoxesPanel2 = new JPanel(new BorderLayout(), true);
        checkBoxesPanel2.setLayout( new BoxLayout(checkBoxesPanel2, BoxLayout.Y_AXIS) );
        checkBoxesPanel2.add(textureViewCheckBox);
        checkBoxesPanel2.add(sphericalMappingCheckBox);

        JPanel allCheckBoxesPanel = new JPanel(true);
        allCheckBoxesPanel.setLayout( new BoxLayout(allCheckBoxesPanel, BoxLayout.X_AXIS) );
        allCheckBoxesPanel.add(checkBoxesPanel1);
        allCheckBoxesPanel.add( Box.createRigidArea( new Dimension(55, 20) ) );
        allCheckBoxesPanel.add(checkBoxesPanel2);

        JPanel autorotateViewAndResetSettingsPanel = new JPanel(true);
        autorotateViewAndResetSettingsPanel.setLayout( new BoxLayout(autorotateViewAndResetSettingsPanel, BoxLayout.Y_AXIS) );
        autorotateViewAndResetSettingsPanel.add(autoRotateViewCheckBox);
        autorotateViewAndResetSettingsPanel.add( Box.createRigidArea( new Dimension(10, 10) ) );
        autorotateViewAndResetSettingsPanel.add(resetSettingsButton);

        JPanel sliderPanel = new JPanel(true);
        sliderPanel.setLayout( new BoxLayout(sliderPanel, BoxLayout.Y_AXIS) );
        sliderPanel.add(tesselationSlider);
        JPanel labelPanel = new JPanel(true);
        labelPanel.add(tesselationLabel);
        sliderPanel.add(labelPanel);

        JPanel checkBoxButtonAndSliderPanel = new JPanel(true);
        checkBoxButtonAndSliderPanel.setLayout( new BoxLayout(checkBoxButtonAndSliderPanel, BoxLayout.X_AXIS) );
        checkBoxButtonAndSliderPanel.add( Box.createRigidArea( new Dimension(20, 20) ) );
        checkBoxButtonAndSliderPanel.add(autorotateViewAndResetSettingsPanel);
        checkBoxButtonAndSliderPanel.add( Box.createRigidArea( new Dimension(20, 20) ) );
        checkBoxButtonAndSliderPanel.add(sliderPanel);

        JPanel lowerUIPanel = new JPanel(true);
        lowerUIPanel.setLayout( new BoxLayout(lowerUIPanel, BoxLayout.Y_AXIS) );
        lowerUIPanel.add(allCheckBoxesPanel);
        lowerUIPanel.add(checkBoxButtonAndSliderPanel);

        this.add(upperUIPanel, BorderLayout.NORTH);
        this.add(lowerUIPanel, BorderLayout.SOUTH);
    }

    /**
    *  Sets the selected state of the autoRotateView checkbox.
    */
    public void setSelectedAutoRotateViewCheckBox(boolean flag)
    {
        autoRotateViewCheckBox.setSelected(flag);
    }

    /**
    *  Sets the tesselation value.
    */
    public void setTesselation(int tesselation)
    {
        tesselationSlider.setValue(tesselation);
        tesselationLabel.setText("Tesselation: " + tesselation);
        tesselationLabel.setToolTipText("Tesselation: " + tesselation);
    }

    /**
    *  Sets the model type settings.
    */
    public void setModelTypeSettings(ModelTypes modelType)
    {
        this.modelType = modelType;

        int scaleX = 0;
        int scaleY = 0;
        int scaleZ = 0;
        int rotateX = 0;
        int rotateY = 0;
        int rotateZ = 0;

        if ( modelType.equals(LATHE3D_SHAPE) )
        {
            scaleX = (int)( SCALE_STEPS_RATIO * LATHE3D_SCALE_X.get() );
            scaleY = (int)( SCALE_STEPS_RATIO * LATHE3D_SCALE_Y.get() );
            scaleZ = (int)( SCALE_STEPS_RATIO * LATHE3D_SCALE_Z.get() );
            rotateX = (int)LATHE3D_ROTATE_X.get();
            rotateY = (int)LATHE3D_ROTATE_Y.get();
            rotateZ = (int)LATHE3D_ROTATE_Z.get();
        }
        else if ( modelType.equals(SUPER_QUADRIC_SHAPE) )
        {
            scaleX = (int)( SCALE_STEPS_RATIO * SUPER_QUADRIC_SCALE_X.get() );
            scaleY = (int)( SCALE_STEPS_RATIO * SUPER_QUADRIC_SCALE_Y.get() );
            scaleZ = (int)( SCALE_STEPS_RATIO * SUPER_QUADRIC_SCALE_Z.get() );
            rotateX = (int)SUPER_QUADRIC_ROTATE_X.get();
            rotateY = (int)SUPER_QUADRIC_ROTATE_Y.get();
            rotateZ = (int)SUPER_QUADRIC_ROTATE_Z.get();
        }
        else // if ( modelType.equals(OBJ_MODEL_LOADER_SHAPE) )
        {
            scaleX = (int)( SCALE_STEPS_RATIO * OBJ_MODEL_LOADER_SCALE_X.get() );
            scaleY = (int)( SCALE_STEPS_RATIO * OBJ_MODEL_LOADER_SCALE_Y.get() );
            scaleZ = (int)( SCALE_STEPS_RATIO * OBJ_MODEL_LOADER_SCALE_Z.get() );
            rotateX = (int)OBJ_MODEL_LOADER_ROTATE_X.get();
            rotateY = (int)OBJ_MODEL_LOADER_ROTATE_Y.get();
            rotateZ = (int)OBJ_MODEL_LOADER_ROTATE_Z.get();
        }

        scaleXSlider.setValue(scaleX);
        scaleYSlider.setValue(scaleY);
        scaleZSlider.setValue(scaleZ);
        rotateXSlider.setValue(rotateX);
        rotateYSlider.setValue(rotateY);
        rotateZSlider.setValue(rotateZ);

        boolean flag = !modelType.equals(OBJ_MODEL_LOADER_SHAPE);
        textureViewCheckBox.setEnabled(flag);
        sphericalMappingCheckBox.setEnabled(textureViewCheckBox.isSelected() && flag);
        tesselationSlider.setEnabled(flag);
        tesselationLabel.setEnabled(flag);
    }

    /**
    *  Implements all UI related actions.
    */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        if ( e.getSource().equals(wireframeViewCheckBox) )
        {
            boolean flag = wireframeViewCheckBox.isSelected();
            normalsViewCheckBox.setEnabled(false);
            modelShapeRenderer.setWireframeView(flag);
            modelShapeRenderer.refreshDisplay();
        }
        else if ( e.getSource().equals(normalsViewCheckBox) )
        {
            modelShapeRenderer.setNormalsView( normalsViewCheckBox.isSelected() );
            modelShapeRenderer.refreshDisplay();
        }
        else if ( e.getSource().equals(textureViewCheckBox) )
        {
            boolean flag = textureViewCheckBox.isSelected();
            sphericalMappingCheckBox.setEnabled(flag);
            modelShapeRenderer.setTexturingView(flag);
            modelShapeRenderer.refreshDisplay();
        }
        else if ( e.getSource().equals(sphericalMappingCheckBox) )
        {
            modelShapeRenderer.setSphericalMappingView( sphericalMappingCheckBox.isSelected() );
            modelShapeRenderer.refreshDisplay();
        }
        else if ( e.getSource().equals(autoRotateViewCheckBox) )
        {
            if ( autoRotateViewCheckBox.isSelected() )
                modelShapeRenderer.startRender();
            else
                modelShapeRenderer.stopRender();
            modelShapeRenderer.refreshDisplay();
            MODEL_SHAPE_EDITOR_AUTOROTATE_VIEW.set( autoRotateViewCheckBox.isSelected() );
        }
        else if ( e.getSource().equals(resetSettingsButton) )
        {
            scaleXSlider.setValue(0);
            modelShapeRenderer.setUserScaleX(0.0f);
            scaleYSlider.setValue(0);
            modelShapeRenderer.setUserScaleY(0.0f);
            scaleZSlider.setValue(0);
            modelShapeRenderer.setUserScaleZ(0.0f);

            rotateXSlider.setValue(0);
            modelShapeRenderer.setUserRotationX(0);
            rotateYSlider.setValue(0);
            modelShapeRenderer.setUserRotationY(0);
            rotateZSlider.setValue(0);
            modelShapeRenderer.setUserRotationZ(0);

            wireframeViewCheckBox.setSelected(false);
            modelShapeRenderer.setWireframeView(false);

            textureViewCheckBox.setSelected(false);
            sphericalMappingCheckBox.setSelected(false);
            sphericalMappingCheckBox.setEnabled(false);
            modelShapeRenderer.setTexturingView(false);
            modelShapeRenderer.setSphericalMappingView(false);

            setTesselation( NODE_TESSELATION.get() );
            modelShapeRenderer.setTesselation( NODE_TESSELATION.get() );

            if ( !modelShapeRenderer.isOBJModelLoaderType() )
                modelShapeRenderer.setChangeDetected(true);
            modelShapeRenderer.refreshDisplay();
        }
    }

    /**
    *  Implements all UI related states.
    */
    @Override
    public void stateChanged(ChangeEvent e)
    {
        if ( e.getSource().equals(scaleXSlider) )
        {
            float scaleX = scaleXSlider.getValue() / (float)SCALE_STEPS_RATIO;
            modelShapeRenderer.setUserScaleX(scaleX);
            if ( modelType.equals(LATHE3D_SHAPE) )
                LATHE3D_SCALE_X.set(scaleX);
            else if ( modelType.equals(SUPER_QUADRIC_SHAPE) )
                SUPER_QUADRIC_SCALE_X.set(scaleX);
            else // if ( modelType.equals(OBJ_MODEL_LOADER_SHAPE) )
                OBJ_MODEL_LOADER_SCALE_X.set(scaleX);
            modelShapeRenderer.refreshDisplay();
        }
        else if ( e.getSource().equals(scaleYSlider) )
        {
            float scaleY = scaleYSlider.getValue() / (float)SCALE_STEPS_RATIO;
            modelShapeRenderer.setUserScaleY(scaleY);
            if ( modelType.equals(LATHE3D_SHAPE) )
                LATHE3D_SCALE_Y.set(scaleY);
            else if ( modelType.equals(SUPER_QUADRIC_SHAPE) )
                SUPER_QUADRIC_SCALE_Y.set(scaleY);
            else // if ( modelType.equals(OBJ_MODEL_LOADER_SHAPE) )
                OBJ_MODEL_LOADER_SCALE_Y.set(scaleY);
            modelShapeRenderer.refreshDisplay();
        }
        else if ( e.getSource().equals(scaleZSlider) )
        {
            float scaleZ = scaleZSlider.getValue() / (float)SCALE_STEPS_RATIO;
            modelShapeRenderer.setUserScaleZ(scaleZ);
            if ( modelType.equals(LATHE3D_SHAPE) )
                LATHE3D_SCALE_Z.set(scaleZ);
            else if ( modelType.equals(SUPER_QUADRIC_SHAPE) )
                SUPER_QUADRIC_SCALE_Z.set(scaleZ);
            else // if ( modelType.equals(OBJ_MODEL_LOADER_SHAPE) )
                OBJ_MODEL_LOADER_SCALE_Z.set(scaleZ);
            modelShapeRenderer.refreshDisplay();
        }
        else if ( e.getSource().equals(rotateXSlider) )
        {
            int rotateX = rotateXSlider.getValue();
            modelShapeRenderer.setUserRotationX(rotateX);
            if ( modelType.equals(LATHE3D_SHAPE) )
                LATHE3D_ROTATE_X.set(rotateX);
            else if ( modelType.equals(SUPER_QUADRIC_SHAPE) )
                SUPER_QUADRIC_ROTATE_X.set(rotateX);
            else // if ( modelType.equals(OBJ_MODEL_LOADER_SHAPE) )
                OBJ_MODEL_LOADER_ROTATE_X.set(rotateX);
            modelShapeRenderer.refreshDisplay();
        }
        else if ( e.getSource().equals(rotateYSlider) )
        {
            int rotateY = rotateYSlider.getValue();
            modelShapeRenderer.setUserRotationY(rotateY);
            if ( modelType.equals(LATHE3D_SHAPE) )
                LATHE3D_ROTATE_Y.set(rotateY);
            else if ( modelType.equals(SUPER_QUADRIC_SHAPE) )
                SUPER_QUADRIC_ROTATE_Y.set(rotateY);
            else // if ( modelType.equals(OBJ_MODEL_LOADER_SHAPE) )
                OBJ_MODEL_LOADER_ROTATE_Y.set(rotateY);
            modelShapeRenderer.refreshDisplay();
        }
        else if ( e.getSource().equals(rotateZSlider) )
        {
            int rotateZ = rotateZSlider.getValue();
            modelShapeRenderer.setUserRotationZ(rotateZ);
            if ( modelType.equals(LATHE3D_SHAPE) )
                LATHE3D_ROTATE_Z.set(rotateZ);
            else if ( modelType.equals(SUPER_QUADRIC_SHAPE) )
                SUPER_QUADRIC_ROTATE_Z.set(rotateZ);
            else // if ( modelType.equals(OBJ_MODEL_LOADER_SHAPE) )
                OBJ_MODEL_LOADER_ROTATE_Z.set(rotateZ);
            modelShapeRenderer.refreshDisplay();
        }
        else if ( e.getSource().equals(tesselationSlider) )
        {
            int tesselationValue = tesselationSlider.getValue();
            setTesselation(tesselationValue);
            modelShapeRenderer.setTesselation(tesselationValue);
            modelShapeRenderer.setChangeDetected(true);
            modelShapeRenderer.refreshDisplay();
            modelShapeEditorParentUIDialog.refreshModelShapeLathe3DDrawLinesPanel();
        }
    }


}
