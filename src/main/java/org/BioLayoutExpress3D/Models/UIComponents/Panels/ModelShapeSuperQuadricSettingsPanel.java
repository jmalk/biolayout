package org.BioLayoutExpress3D.Models.UIComponents.Panels;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import org.BioLayoutExpress3D.Models.SuperQuadric.*;
import org.BioLayoutExpress3D.Models.UIComponents.*;
import static org.BioLayoutExpress3D.StaticLibraries.EnumUtils.*;
import static org.BioLayoutExpress3D.Models.SuperQuadric.SuperQuadricShapesProducer.*;
import static org.BioLayoutExpress3D.Models.SuperQuadric.SuperQuadricShapeTypes.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
*
* The ModelShapeSuperQuadricSettingsPanel class which is the UI placeholder for the SuperQuadric settings.
*
* @see org.BioLayoutExpress3D.Models.UIComponents.ModelShapeEditorParentUIDialog
* @see org.BioLayoutExpress3D.Models.UIComponents.ModelShapeRenderer
* @author Thanos Theo, 2011
* @version 3.0.0.0
*
*/

public class ModelShapeSuperQuadricSettingsPanel extends JPanel implements ActionListener, ChangeListener
{

    private static final int LOWER_BOUND_RANGE_FOR_EXPONENTS = 0;
    private static final int UPPER_BOUND_RANGE_FOR_EXPONENTS = 60;
    private static final int EXPONENTS_STEPS_DELTA = 2;
    private static final int EXPONENTS_STEPS_FACTOR = 5;
    private static final int EXPONENTS_STEPS_RATIO = 10;
    private static final int LOWER_BOUND_RANGE_FOR_V = -180;
    private static final int UPPER_BOUND_RANGE_FOR_V =  180;
    private static final int V_STEPS_DELTA = 15;
    private static final int V_STEPS_FACTOR = 12;
    private static final int LOWER_BOUND_RANGE_FOR_TOROID_RADIUS = 0;
    private static final int UPPER_BOUND_RANGE_FOR_TOROID_RADIUS = 200;
    private static final int TOROID_RADIUS_STEPS_DELTA = 5;
    private static final int TOROID_RADIUS_STEPS_FACTOR = 10;
    private static final int TOROID_RADIUS_STEPS_RATIO = 10;
    private static final int TOROID_RADIUS_FAIL_SAFE_MIN_BOUND_RANGE = 3;

    private JComboBox<String> superQuadricPresetsComboBox = null;
    private JComboBox<String> superQuadricShapeTypesComboBox = null;
    private JSlider scaleExponentESlider = null;
    private JSlider scaleExponentNSlider = null;
    private JSlider scaleValueVSlider = null;
    private JSlider scaleRadiusAlphaSlider = null;

    /**
    *  The ModelShapeRenderer reference.
    */
    private ModelShapeRenderer modelShapeRenderer = null;

    /**
    *  The ModelShapeSuperQuadricSettingsPanel class constructor.
    */
    public ModelShapeSuperQuadricSettingsPanel(ModelShapeRenderer modelShapeRenderer)
    {
        super(new BorderLayout(), true);

        this.modelShapeRenderer = modelShapeRenderer;

        initComponents();
    }

    /**
    *  Initializes the UI components for this panel.
    */
    private void initComponents()
    {
        superQuadricPresetsComboBox = new JComboBox<String>();
        for ( SuperQuadricShapes superQuadricShape : SuperQuadricShapes.values() )
            superQuadricPresetsComboBox.addItem( splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(superQuadricShape) );
        superQuadricPresetsComboBox.setSelectedIndex( getEnumIndexForName( SuperQuadricShapes.class, SUPER_QUADRIC_CHOSEN_PRESET_SHAPE.get() ) );
        superQuadricPresetsComboBox.addActionListener(this);
        superQuadricPresetsComboBox.setToolTipText("SuperQuadric Preset");

        superQuadricShapeTypesComboBox = new JComboBox<String>();
        for ( SuperQuadricShapeTypes superQuadricShapeType : SuperQuadricShapeTypes.values() )
            superQuadricShapeTypesComboBox.addItem( splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(superQuadricShapeType) );
        superQuadricShapeTypesComboBox.setSelectedIndex( SUPER_QUADRIC_SETTINGS.superQuadricShapeType.ordinal() );
        superQuadricShapeTypesComboBox.addActionListener(this);
        superQuadricShapeTypesComboBox.setToolTipText("SuperQuadric Type");

        JPanel superQuadricPresetsPanel = new JPanel(true);
        superQuadricPresetsPanel.setLayout( new BoxLayout(superQuadricPresetsPanel, BoxLayout.X_AXIS) );
        superQuadricPresetsPanel.add( new JLabel("SuperQuadric Preset:  ") );
        superQuadricPresetsPanel.add(superQuadricPresetsComboBox);

        JPanel superQuadricTypesPanel = new JPanel(true);
        superQuadricTypesPanel.setLayout( new BoxLayout(superQuadricTypesPanel, BoxLayout.X_AXIS) );
        superQuadricTypesPanel.add( new JLabel("SuperQuadric Type:    " + ( (UIManager.getLookAndFeel().getName().equals("Nimbus") || IS_MAC ) ? " " : "") ) );
        superQuadricTypesPanel.add(superQuadricShapeTypesComboBox);

        JPanel upperUIPanel = new JPanel(true);
        upperUIPanel.setLayout( new BoxLayout(upperUIPanel, BoxLayout.Y_AXIS) );
        if ( !( UIManager.getLookAndFeel().getName().equals("Nimbus") || IS_MAC ) )
            upperUIPanel.add( Box.createRigidArea( new Dimension(5, 5) ) );
        upperUIPanel.add(superQuadricPresetsPanel);
        upperUIPanel.add(superQuadricTypesPanel);

        scaleExponentESlider = new JSlider(JSlider.HORIZONTAL);
        scaleExponentESlider.setMinimum(LOWER_BOUND_RANGE_FOR_EXPONENTS);
        scaleExponentESlider.setMaximum(UPPER_BOUND_RANGE_FOR_EXPONENTS);
        scaleExponentESlider.setValue( (int)(EXPONENTS_STEPS_RATIO * SUPER_QUADRIC_SETTINGS.e) );
        scaleExponentESlider.setMajorTickSpacing(EXPONENTS_STEPS_FACTOR * EXPONENTS_STEPS_DELTA);
        scaleExponentESlider.setMinorTickSpacing(EXPONENTS_STEPS_DELTA);
        scaleExponentESlider.setPaintTicks(true);
        scaleExponentESlider.setPaintLabels(true);
        scaleExponentESlider.addChangeListener(this);
        scaleExponentESlider.setToolTipText("Exponent E");
        scaleExponentNSlider = new JSlider(JSlider.HORIZONTAL);
        scaleExponentNSlider.setMinimum(LOWER_BOUND_RANGE_FOR_EXPONENTS);
        scaleExponentNSlider.setMaximum(UPPER_BOUND_RANGE_FOR_EXPONENTS);
        scaleExponentNSlider.setValue( (int)(EXPONENTS_STEPS_RATIO * SUPER_QUADRIC_SETTINGS.n) );
        scaleExponentNSlider.setMajorTickSpacing(EXPONENTS_STEPS_FACTOR * EXPONENTS_STEPS_DELTA);
        scaleExponentNSlider.setMinorTickSpacing(EXPONENTS_STEPS_DELTA);
        scaleExponentNSlider.setPaintTicks(true);
        scaleExponentNSlider.setPaintLabels(true);
        scaleExponentNSlider.addChangeListener(this);
        scaleExponentNSlider.setToolTipText("Exponent N");
        scaleValueVSlider = new JSlider(JSlider.HORIZONTAL);
        scaleValueVSlider.setMinimum(LOWER_BOUND_RANGE_FOR_V);
        scaleValueVSlider.setMaximum(UPPER_BOUND_RANGE_FOR_V);
        scaleValueVSlider.setValue( (int)Math.toDegrees(SUPER_QUADRIC_SETTINGS.v1) );
        scaleValueVSlider.setMajorTickSpacing(V_STEPS_FACTOR * V_STEPS_DELTA);
        scaleValueVSlider.setMinorTickSpacing(V_STEPS_DELTA);
        scaleValueVSlider.setPaintTicks(true);
        scaleValueVSlider.setPaintLabels(true);
        scaleValueVSlider.addChangeListener(this);
        scaleValueVSlider.setToolTipText("Value V");
        scaleRadiusAlphaSlider = new JSlider(JSlider.HORIZONTAL);
        scaleRadiusAlphaSlider.setMinimum(LOWER_BOUND_RANGE_FOR_TOROID_RADIUS);
        scaleRadiusAlphaSlider.setMaximum(UPPER_BOUND_RANGE_FOR_TOROID_RADIUS);
        scaleRadiusAlphaSlider.setValue( (int)(TOROID_RADIUS_STEPS_RATIO * SUPER_QUADRIC_SETTINGS.alpha) );
        scaleRadiusAlphaSlider.setMajorTickSpacing(TOROID_RADIUS_STEPS_FACTOR * TOROID_RADIUS_STEPS_DELTA);
        scaleRadiusAlphaSlider.setMinorTickSpacing(TOROID_RADIUS_STEPS_DELTA);
        scaleRadiusAlphaSlider.setPaintTicks(true);
        scaleRadiusAlphaSlider.setPaintLabels(true);
        scaleRadiusAlphaSlider.addChangeListener(this);
        scaleRadiusAlphaSlider.setToolTipText("Radius Alpha");
        scaleRadiusAlphaSlider.setEnabled(false); // default SuperQuadricShapeType is the SuperEllipsoid

        JPanel lowerUIExponentSlidersPanel = new JPanel(true);
        lowerUIExponentSlidersPanel.setLayout( new BoxLayout(lowerUIExponentSlidersPanel, BoxLayout.X_AXIS) );
        lowerUIExponentSlidersPanel.add( new JLabel("Exponents E / N: ") );
        lowerUIExponentSlidersPanel.add(scaleExponentESlider);
        lowerUIExponentSlidersPanel.add( Box.createRigidArea( new Dimension(3, 3) ) );
        lowerUIExponentSlidersPanel.add(scaleExponentNSlider);

        JPanel lowerUIValueVRadiusAlphaSlidersPanel = new JPanel(true);
        lowerUIValueVRadiusAlphaSlidersPanel.setLayout( new BoxLayout(lowerUIValueVRadiusAlphaSlidersPanel, BoxLayout.X_AXIS) );
        lowerUIValueVRadiusAlphaSlidersPanel.add( new JLabel("Values V / Alpha: ") );
        lowerUIValueVRadiusAlphaSlidersPanel.add(scaleValueVSlider);
        lowerUIValueVRadiusAlphaSlidersPanel.add( Box.createRigidArea( new Dimension(3, 3) ) );
        lowerUIValueVRadiusAlphaSlidersPanel.add(scaleRadiusAlphaSlider);

        JPanel lowerUIPanel = new JPanel(true);
        lowerUIPanel.setLayout( new BoxLayout(lowerUIPanel, BoxLayout.Y_AXIS) );
        if ( UIManager.getLookAndFeel().getName().equals("Nimbus") || IS_MAC )
            lowerUIPanel.add( Box.createRigidArea( new Dimension(3, 3) ) );
        else
            lowerUIPanel.add( Box.createRigidArea( new Dimension(18, 18) ) );
        lowerUIPanel.add(lowerUIExponentSlidersPanel);
        if ( !( UIManager.getLookAndFeel().getName().equals("Nimbus") || IS_MAC ) )
            lowerUIPanel.add( Box.createRigidArea( new Dimension(3, 3) ) );
        lowerUIPanel.add(lowerUIValueVRadiusAlphaSlidersPanel);

        this.add(upperUIPanel, BorderLayout.NORTH);
        this.add(lowerUIPanel, BorderLayout.CENTER);
    }

    /**
    *  Loads the given SuperQuadric preset.
    */
    private void loadSuperQuadricPreset()
    {
        superQuadricShapeTypesComboBox.removeActionListener(this);
        scaleExponentESlider.removeChangeListener(this);
        scaleExponentNSlider.removeChangeListener(this);
        scaleValueVSlider.removeChangeListener(this);
        scaleRadiusAlphaSlider.removeChangeListener(this);

        SuperQuadricShapes superQuadricShape = SuperQuadricShapes.values()[superQuadricPresetsComboBox.getSelectedIndex()];
        SUPER_QUADRIC_CHOSEN_PRESET_SHAPE.set( superQuadricShape.toString() );
        copySuperQuadricSettings( createSuperQuadricSettings( superQuadricShape, modelShapeRenderer.getTesselation(), modelShapeRenderer.getTesselation() ) );
        superQuadricShapeTypesComboBox.setSelectedIndex( SUPER_QUADRIC_SETTINGS.superQuadricShapeType.ordinal() );
        scaleExponentESlider.setValue( (int)(EXPONENTS_STEPS_RATIO * SUPER_QUADRIC_SETTINGS.e) );
        scaleExponentNSlider.setValue( (int)(EXPONENTS_STEPS_RATIO * SUPER_QUADRIC_SETTINGS.n) );
        scaleValueVSlider.setValue( (int)Math.toDegrees(SUPER_QUADRIC_SETTINGS.v1) );
        scaleRadiusAlphaSlider.setValue( (int)(TOROID_RADIUS_STEPS_RATIO * SUPER_QUADRIC_SETTINGS.alpha) );
        scaleRadiusAlphaSlider.setEnabled( SUPER_QUADRIC_SETTINGS.superQuadricShapeType.equals(TOROID) );
        modelShapeRenderer.setShapeName( splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(superQuadricShape) );
        setSuperQuadricAndRefreshRendering(false);

        superQuadricShapeTypesComboBox.addActionListener(this);
        scaleExponentESlider.addChangeListener(this);
        scaleExponentNSlider.addChangeListener(this);
        scaleValueVSlider.addChangeListener(this);
        scaleRadiusAlphaSlider.addChangeListener(this);
    }

    /**
    *  Copies the SuperQuadric settings.
    */
    private void copySuperQuadricSettings(SuperQuadricSettings superQuadricSettings)
    {
        SUPER_QUADRIC_SETTINGS.e = superQuadricSettings.e;
        SUPER_QUADRIC_SETTINGS.n = superQuadricSettings.n;
        SUPER_QUADRIC_SETTINGS.v1 = superQuadricSettings.v1;
        SUPER_QUADRIC_SETTINGS.alpha = superQuadricSettings.alpha;
        SUPER_QUADRIC_SETTINGS.superQuadricShapeType = superQuadricSettings.superQuadricShapeType;
        // copy rest of the SuperQuadric settings
        SUPER_QUADRIC_SETTINGS.a1 = superQuadricSettings.a1;
        SUPER_QUADRIC_SETTINGS.a2 = superQuadricSettings.a2;
        SUPER_QUADRIC_SETTINGS.a3 = superQuadricSettings.a3;
        SUPER_QUADRIC_SETTINGS.u1 = superQuadricSettings.u1;
        SUPER_QUADRIC_SETTINGS.u2 = superQuadricSettings.u2;
        SUPER_QUADRIC_SETTINGS.v2 = superQuadricSettings.v2;
        SUPER_QUADRIC_SETTINGS.uSegments = superQuadricSettings.uSegments;
        SUPER_QUADRIC_SETTINGS.vSegments = superQuadricSettings.vSegments;
        SUPER_QUADRIC_SETTINGS.s1 = superQuadricSettings.s1;
        SUPER_QUADRIC_SETTINGS.s2 = superQuadricSettings.s2;
        SUPER_QUADRIC_SETTINGS.t1 = superQuadricSettings.t1;
        SUPER_QUADRIC_SETTINGS.t2 = superQuadricSettings.t2;
        // finished copying rest of the SuperQuadric settings
    }

    /**
    *  Sets the given SuperQuadric and refreshes the rendering.
    */
    private void setSuperQuadricAndRefreshRendering(boolean temporaryStopUI)
    {
        if (temporaryStopUI)
        {
            superQuadricShapeTypesComboBox.removeActionListener(this);
            scaleExponentESlider.removeChangeListener(this);
            scaleExponentNSlider.removeChangeListener(this);
            scaleValueVSlider.removeChangeListener(this);
            scaleRadiusAlphaSlider.removeChangeListener(this);
        }

        modelShapeRenderer.setChangeDetected(true);
        modelShapeRenderer.refreshDisplay();
        modelShapeRenderer.refreshDisplay(); // second refresh for correct SuperQuadric rendering

        if (temporaryStopUI)
        {
            superQuadricShapeTypesComboBox.addActionListener(this);
            scaleExponentESlider.addChangeListener(this);
            scaleExponentNSlider.addChangeListener(this);
            scaleValueVSlider.addChangeListener(this);
            scaleRadiusAlphaSlider.addChangeListener(this);
        }
    }

    /**
    *  Implements all UI related actions.
    */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        if ( e.getSource().equals(superQuadricPresetsComboBox) )
        {
            loadSuperQuadricPreset();
        }
        else if ( e.getSource().equals(superQuadricShapeTypesComboBox) )
        {
            int index = superQuadricShapeTypesComboBox.getSelectedIndex();
            SUPER_QUADRIC_SETTINGS.superQuadricShapeType = SuperQuadricShapeTypes.values()[index];
            scaleRadiusAlphaSlider.setEnabled( SUPER_QUADRIC_SETTINGS.superQuadricShapeType.equals(TOROID) );
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
        if ( e.getSource().equals(scaleExponentESlider) )
        {
            SUPER_QUADRIC_SETTINGS.e = scaleExponentESlider.getValue() / (float)EXPONENTS_STEPS_RATIO;
            setSuperQuadricAndRefreshRendering(true);
        }
        else if ( e.getSource().equals(scaleExponentNSlider) )
        {
            SUPER_QUADRIC_SETTINGS.n = scaleExponentNSlider.getValue() / (float)EXPONENTS_STEPS_RATIO;
            setSuperQuadricAndRefreshRendering(true);
        }
        else if ( e.getSource().equals(scaleValueVSlider) )
        {
            SUPER_QUADRIC_SETTINGS.v1 = (float)Math.toRadians( scaleValueVSlider.getValue() );
            setSuperQuadricAndRefreshRendering(true);
        }
        else if ( e.getSource().equals(scaleRadiusAlphaSlider) )
        {
            if (scaleRadiusAlphaSlider.getValue() >= TOROID_RADIUS_FAIL_SAFE_MIN_BOUND_RANGE)
            {
                SUPER_QUADRIC_SETTINGS.alpha = scaleRadiusAlphaSlider.getValue() / (float)TOROID_RADIUS_STEPS_RATIO;
                setSuperQuadricAndRefreshRendering(true);
            }
            else
                scaleRadiusAlphaSlider.setValue(TOROID_RADIUS_FAIL_SAFE_MIN_BOUND_RANGE);
        }
    }


}
