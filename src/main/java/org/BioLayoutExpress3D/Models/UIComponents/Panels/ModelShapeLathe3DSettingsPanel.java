package org.BioLayoutExpress3D.Models.UIComponents.Panels;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.BioLayoutExpress3D.Models.Lathe3D.*;
import org.BioLayoutExpress3D.Models.UIComponents.*;
import org.BioLayoutExpress3D.Textures.*;
import static org.BioLayoutExpress3D.StaticLibraries.EnumUtils.*;
import static org.BioLayoutExpress3D.Models.Lathe3D.Lathe3DShapesProducer.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
*
* The ModelShapeLathe3DSettingsPanel class which is the UI placeholder for the Lathe3D settings.
*
* @see org.BioLayoutExpress3D.Models.UIComponents.ModelShapeEditorParentUIDialog
* @see org.BioLayoutExpress3D.Models.UIComponents.ModelShapeRenderer
* @author Thanos Theo, 2011
* @version 3.0.0.0
*
*/

public class ModelShapeLathe3DSettingsPanel extends JPanel implements ActionListener
{

    private static final String MODEL_SHAPE_EDITOR_DIR_NAME = IMAGE_FILES_PATH + "ModelShapeEditor/Lathe3D/";
    private static final String MODEL_SHAPE_EDITOR_FILE_NAME = "ModelShapeEditorLathe3DData.txt";
    private static final float MODEL_SHAPE_EDITOR_ICON_RESIZE_RATIO = 0.18f;

    private JComboBox<String> lathe3DPresetsComboBox = null;
    private JComboBox<String> lathe3DShapeTypesComboBox = null;
    private JRadioButton lathe3DStraightLine = null;
    private JRadioButton lathe3DCurvedLine = null;
    private JButton lathe3DDeleteLastPoint = null;
    private JButton lathe3DDeleteAllPoints = null;
    private ModelShapeLathe3DDrawLinesPanel modelShapeLathe3DDrawLinesPanel = null;

    /**
    *  The ModelShapeRenderer reference.
    */
    private ModelShapeRenderer modelShapeRenderer = null;

    /**
    *  The ModelShapeLathe3DSettingsPanel class constructor.
    */
    public ModelShapeLathe3DSettingsPanel(ModelShapeRenderer modelShapeRenderer)
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
        TexturesLoader texturesLoaderIcons = new TexturesLoader(MODEL_SHAPE_EDITOR_DIR_NAME, MODEL_SHAPE_EDITOR_FILE_NAME, false, false, true, (UIManager.getLookAndFeel().getName().equals("Nimbus") ? 0.667f : 1.0f) * MODEL_SHAPE_EDITOR_ICON_RESIZE_RATIO, false);

        lathe3DPresetsComboBox = new JComboBox<String>();
        for ( Lathe3DShapes lathe3DShape : Lathe3DShapes.values() )
            lathe3DPresetsComboBox.addItem( splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(lathe3DShape) );
        lathe3DPresetsComboBox.setSelectedIndex( getEnumIndexForName( Lathe3DShapes.class, LATHE3D_CHOSEN_PRESET_SHAPE.get() ) );
        lathe3DPresetsComboBox.addActionListener(this);
        lathe3DPresetsComboBox.setToolTipText("Lathe3D Preset");

        lathe3DShapeTypesComboBox = new JComboBox<String>();
        for ( Lathe3DShapeTypes lathe3DShapeType : Lathe3DShapeTypes.values() )
            lathe3DShapeTypesComboBox.addItem( splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(lathe3DShapeType) );
        lathe3DShapeTypesComboBox.setSelectedIndex( LATHE3D_SETTINGS.lathe3DShapeType.ordinal() );
        lathe3DShapeTypesComboBox.addActionListener(this);
        lathe3DShapeTypesComboBox.setToolTipText("Lathe3D Type");

        JPanel lathe3DPresetsPanel = new JPanel(true);
        lathe3DPresetsPanel.setLayout( new BoxLayout(lathe3DPresetsPanel, BoxLayout.X_AXIS) );
        lathe3DPresetsPanel.add( new JLabel("Lathe3D Preset:  ") );
        lathe3DPresetsPanel.add(lathe3DPresetsComboBox);

        JPanel lathe3DTypesPanel = new JPanel(true);
        lathe3DTypesPanel.setLayout( new BoxLayout(lathe3DTypesPanel, BoxLayout.X_AXIS) );
        lathe3DTypesPanel.add( new JLabel("Lathe3D Type:    " + ( ( UIManager.getLookAndFeel().getName().equals("Nimbus") || IS_MAC ) ? " " : "" ) ) );
        lathe3DTypesPanel.add(lathe3DShapeTypesComboBox);

        JPanel upperUIPanel = new JPanel(true);
        upperUIPanel.setLayout( new BoxLayout(upperUIPanel, BoxLayout.Y_AXIS) );
        if ( !( UIManager.getLookAndFeel().getName().equals("Nimbus") || IS_MAC ) )
            upperUIPanel.add( Box.createRigidArea( new Dimension(5, 5) ) );
        upperUIPanel.add(lathe3DPresetsPanel);
        upperUIPanel.add(lathe3DTypesPanel);

        lathe3DStraightLine = new JRadioButton("Straight Line");
        lathe3DStraightLine.addActionListener(this);
        lathe3DStraightLine.setToolTipText("Straight Line");
        lathe3DStraightLine.setSelected(true);

        lathe3DCurvedLine = new JRadioButton("Curved Line");
        lathe3DCurvedLine.addActionListener(this);
        lathe3DCurvedLine.setToolTipText("Curved Line");

        ButtonGroup lathe3DTypeOfLinesButtonGroup = new ButtonGroup();
        lathe3DTypeOfLinesButtonGroup.add(lathe3DStraightLine);
        lathe3DTypeOfLinesButtonGroup.add(lathe3DCurvedLine);

        lathe3DDeleteLastPoint = new JButton( new ImageIcon( texturesLoaderIcons.getImage("DeleteLast") )  );
        lathe3DDeleteLastPoint.addActionListener(this);
        lathe3DDeleteLastPoint.setToolTipText("Delete Last Point");

        lathe3DDeleteAllPoints = new JButton( new ImageIcon( texturesLoaderIcons.getImage("DeleteAll") ) );
        lathe3DDeleteAllPoints.addActionListener(this);
        lathe3DDeleteAllPoints.setToolTipText("Delete All Points");

        JPanel lathe3DDesignControlRadioButtonsPanel = new JPanel(true);
        lathe3DDesignControlRadioButtonsPanel.setLayout( new BoxLayout(lathe3DDesignControlRadioButtonsPanel, BoxLayout.Y_AXIS) );
        lathe3DDesignControlRadioButtonsPanel.add(lathe3DStraightLine);
        lathe3DDesignControlRadioButtonsPanel.add(lathe3DCurvedLine);

        JPanel lathe3DDesignControlButtonsPanel = new JPanel(true);
        lathe3DDesignControlButtonsPanel.setLayout( new BoxLayout(lathe3DDesignControlButtonsPanel, BoxLayout.X_AXIS) );
        lathe3DDesignControlButtonsPanel.add(lathe3DDeleteLastPoint);
        lathe3DDesignControlButtonsPanel.add(lathe3DDeleteAllPoints);

        JPanel lathe3DDesignControlsPanel = new JPanel(true);
        lathe3DDesignControlsPanel.setBorder( BorderFactory.createTitledBorder("Design Controls") );
        GroupLayout lathe3DDesignControlsPanelLayout = new GroupLayout(lathe3DDesignControlsPanel);
        lathe3DDesignControlsPanel.setLayout(lathe3DDesignControlsPanelLayout);

        lathe3DDesignControlsPanelLayout.setHorizontalGroup(
            lathe3DDesignControlsPanelLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
            .addGroup(lathe3DDesignControlsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lathe3DDesignControlsPanelLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addGroup(lathe3DDesignControlsPanelLayout.createSequentialGroup())
                    .addComponent(lathe3DDesignControlRadioButtonsPanel)
                    .addComponent(lathe3DDesignControlButtonsPanel))
                .addContainerGap(0, Short.MAX_VALUE))
        );
        lathe3DDesignControlsPanelLayout.setVerticalGroup(
            lathe3DDesignControlsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(lathe3DDesignControlsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lathe3DDesignControlRadioButtonsPanel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lathe3DDesignControlButtonsPanel)
                .addGroup(lathe3DDesignControlsPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        modelShapeLathe3DDrawLinesPanel = new ModelShapeLathe3DDrawLinesPanel(modelShapeRenderer);

        JPanel lowerUIPanel = new JPanel(true);
        lowerUIPanel.setLayout( new BoxLayout(lowerUIPanel, BoxLayout.X_AXIS) );
        lowerUIPanel.add( Box.createRigidArea( new Dimension(10, 10) ) );
        lowerUIPanel.add(lathe3DDesignControlsPanel);
        lowerUIPanel.add(modelShapeLathe3DDrawLinesPanel);

        this.add(upperUIPanel, BorderLayout.NORTH);
        this.add(lowerUIPanel, BorderLayout.CENTER);
    }

    /**
    *  Loads the given Lathe3D preset.
    */
    private void loadLathe3DPreset()
    {
        lathe3DShapeTypesComboBox.removeActionListener(this);

        Lathe3DShapes lathe3DShape = Lathe3DShapes.values()[lathe3DPresetsComboBox.getSelectedIndex()];
        LATHE3D_CHOSEN_PRESET_SHAPE.set( lathe3DShape.toString() );
        copyLathe3DSettings( createLathe3DSettings( lathe3DShape, modelShapeRenderer.getTesselation() ) );
        lathe3DShapeTypesComboBox.setSelectedIndex( LATHE3D_SETTINGS.lathe3DShapeType.ordinal() );
        modelShapeLathe3DDrawLinesPanel.setXsAndYsIns(LATHE3D_SETTINGS.xsIn, LATHE3D_SETTINGS.ysIn);
        if (LATHE3D_SETTINGS.xsIn[LATHE3D_SETTINGS.xsIn.length - 1] < 0.0f)
        {
            lathe3DStraightLine.setSelected(true);
            modelShapeLathe3DDrawLinesPanel.setLineSign(true);
        }
        else
        {
            lathe3DCurvedLine.setSelected(true);
            modelShapeLathe3DDrawLinesPanel.setLineSign(false);
        }
        modelShapeRenderer.setShapeName( splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(lathe3DShape) );
        modelShapeRenderer.setChangeDetected(true);
        modelShapeRenderer.refreshDisplay();
        modelShapeRenderer.refreshDisplay(); // second refresh for correct Lathe3D rendering

        lathe3DShapeTypesComboBox.addActionListener(this);
    }

    /**
    *  Copies the Lathe3D settings.
    */
    private void copyLathe3DSettings(Lathe3DSettings lathe3DSettings)
    {
        LATHE3D_SETTINGS.xsIn = lathe3DSettings.xsIn;
        LATHE3D_SETTINGS.ysIn = lathe3DSettings.ysIn;
        LATHE3D_SETTINGS.splineStep = lathe3DSettings.splineStep;
        LATHE3D_SETTINGS.k = lathe3DSettings.k;
        LATHE3D_SETTINGS.lathe3DShapeType = lathe3DSettings.lathe3DShapeType;
    }

    /**
    *  Implements all UI related actions.
    */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        if ( e.getSource().equals(lathe3DPresetsComboBox) )
        {
            loadLathe3DPreset();
        }
        else if ( e.getSource().equals(lathe3DShapeTypesComboBox) )
        {
            int index = lathe3DShapeTypesComboBox.getSelectedIndex();
            LATHE3D_SETTINGS.lathe3DShapeType = Lathe3DShapeTypes.values()[index];
            modelShapeRenderer.setChangeDetected(true);
            modelShapeRenderer.refreshDisplay();
        }
        else if ( e.getSource().equals(lathe3DStraightLine) )
        {
            modelShapeLathe3DDrawLinesPanel.setLineSign(true);
        }
        else if ( e.getSource().equals(lathe3DCurvedLine) )
        {
            modelShapeLathe3DDrawLinesPanel.setLineSign(false);
        }
        else if ( e.getSource().equals(lathe3DDeleteLastPoint) )
        {
            modelShapeLathe3DDrawLinesPanel.deleteLastPoint();
        }
        else if ( e.getSource().equals(lathe3DDeleteAllPoints) )
        {
            modelShapeLathe3DDrawLinesPanel.deleteAllPoints();
        }
    }


}
