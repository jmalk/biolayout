package org.BioLayoutExpress3D.Models.UIComponents.Panels;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import org.BioLayoutExpress3D.Models.UIComponents.*;
import static org.BioLayoutExpress3D.StaticLibraries.EnumUtils.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
*
* The ModelShapeOBJModelLoaderSettingsPanel class which is the UI placeholder for the OBJ Model Loader settings.
*
* @see org.BioLayoutExpress3D.Models.UIComponents.ModelShapeEditorParentUIDialog
* @see org.BioLayoutExpress3D.Models.UIComponents.ModelShapeRenderer
* @author Thanos Theo, 2011
* @version 3.0.0.0
*
*/

public class ModelShapeOBJModelLoaderSettingsPanel extends JPanel implements ActionListener
{

    private JDialog parentDialog = null;
    private JComboBox<String> objModelLoaderPresetsComboBox = null;
    private JComboBox<Float> objModelLoaderSizesComboBox = null;
    private JCheckBox objModelLoaderUseExternalOBJModelCheckBox = null;
    private JButton objModelLoaderLoadButton = null;
    private JFileChooser objModelFileChooser = null;

    /**
    *  The ModelShapeRenderer reference.
    */
    private ModelShapeRenderer modelShapeRenderer = null;

    /**
    *  The ModelShapeOBJModelLoaderSettingsPanel class constructor.
    */
    public ModelShapeOBJModelLoaderSettingsPanel(JDialog parentDialog, ModelShapeRenderer modelShapeRenderer)
    {
        super(new BorderLayout(), true);

        this.parentDialog = parentDialog;
        this.modelShapeRenderer = modelShapeRenderer;

        initOBJModelFileChooser();
        initComponents();
    }

    /**
    *  This method is called from within the constructor to initialize the OBJ Model File Chooser.
    */
    private void initOBJModelFileChooser()
    {
        FileNameExtensionFilter objModelFileNameExtensionFilter = new FileNameExtensionFilter("Load An OBJ Model File", "obj");
        String objModelFilePath = FILE_CHOOSER_PATH.get().substring(0, FILE_CHOOSER_PATH.get().lastIndexOf( System.getProperty("file.separator") ) + 1);
        objModelFileChooser = new JFileChooser(objModelFilePath);
        objModelFileChooser.setDialogTitle("Choose An OBJ Model File");
        objModelFileChooser.setFileFilter(objModelFileNameExtensionFilter);
    }

    /**
    *  Initializes the UI components for this panel.
    */
    private void initComponents()
    {
        objModelLoaderPresetsComboBox = new JComboBox<String>();
        for ( OBJModelShapes objModelLoaderShape : OBJModelShapes.values() )
            objModelLoaderPresetsComboBox.addItem( capitalizeFirstCharacter(objModelLoaderShape) );
        objModelLoaderPresetsComboBox.setSelectedIndex( getEnumIndexForName( OBJModelShapes.class, OBJ_MODEL_LOADER_CHOSEN_PRESET_SHAPE.get() ) );
        objModelLoaderPresetsComboBox.addActionListener(this);
        objModelLoaderPresetsComboBox.setToolTipText("OBJ Model Loader Preset");

        objModelLoaderSizesComboBox = new JComboBox<Float>();
        for (int i = 1; i <= 100; i++)
            objModelLoaderSizesComboBox.addItem(i / 10.0f);
        objModelLoaderSizesComboBox.setSelectedItem( OBJ_MODEL_LOADER_SHAPE_SIZE.get() );
        objModelLoaderSizesComboBox.addActionListener(this);
        objModelLoaderSizesComboBox.setToolTipText("OBJ Model Loader Size");

        JPanel objModelLoaderPresetsPanel = new JPanel(true);
        objModelLoaderPresetsPanel.setLayout( new BoxLayout(objModelLoaderPresetsPanel, BoxLayout.X_AXIS) );
        objModelLoaderPresetsPanel.add( new JLabel("OBJ Model Loader Preset:  ") );
        objModelLoaderPresetsPanel.add(objModelLoaderPresetsComboBox);

        JPanel objModelLoaderSizesPanel = new JPanel(true);
        objModelLoaderSizesPanel.setLayout( new BoxLayout(objModelLoaderSizesPanel, BoxLayout.X_AXIS) );
        objModelLoaderSizesPanel.add( new JLabel("OBJ Model Loader Size:      " + ( ( UIManager.getLookAndFeel().getName().equals("Nimbus") || IS_MAC ) ? " " : "" ) ) );
        objModelLoaderSizesPanel.add(objModelLoaderSizesComboBox);

        JPanel upperUIPanel = new JPanel(true);
        upperUIPanel.setLayout( new BoxLayout(upperUIPanel, BoxLayout.Y_AXIS) );
        if ( !( UIManager.getLookAndFeel().getName().equals("Nimbus") || IS_MAC ) )
            upperUIPanel.add( Box.createRigidArea( new Dimension(5, 5) ) );
        upperUIPanel.add(objModelLoaderPresetsPanel);
        upperUIPanel.add(objModelLoaderSizesPanel);

        objModelLoaderUseExternalOBJModelCheckBox = new JCheckBox("Load External OBJ Model");
        objModelLoaderUseExternalOBJModelCheckBox.addActionListener(this);
        objModelLoaderUseExternalOBJModelCheckBox.setToolTipText("Load External OBJ Model");

        objModelLoaderLoadButton = new JButton("   Load   ");
        objModelLoaderLoadButton.setEnabled(false);
        objModelLoaderLoadButton.addActionListener(this);
        objModelLoaderLoadButton.setToolTipText("Load From File");

        JPanel lowerUIPanel = new JPanel(true);
        lowerUIPanel.add( Box.createRigidArea( new Dimension(0, ( UIManager.getLookAndFeel().getName().equals("Nimbus") || IS_MAC ) ? 80 : 120) ) );
        lowerUIPanel.add(objModelLoaderUseExternalOBJModelCheckBox);
        lowerUIPanel.add( Box.createRigidArea( new Dimension(2, 2) ) );
        lowerUIPanel.add(objModelLoaderLoadButton);

        this.add(upperUIPanel, BorderLayout.NORTH);
        this.add(lowerUIPanel, BorderLayout.CENTER);
    }

    /**
    *  Updates the sizesComboBox with the default OBJ Model Shape size.
    */
    public void updateSizesComboBoxWithDefaultOBJModelShapeSize()
    {
        objModelLoaderSizesComboBox.removeActionListener(this);
        objModelLoaderSizesComboBox.setSelectedItem(DEFAULT_OBJ_MODEL_SHAPE_SIZE);
        objModelLoaderSizesComboBox.addActionListener(this);
    }

    /**
    *  Loads the given OBJ Model preset.
    */
    private void loadOBJModelPreset()
    {
        objModelLoaderSizesComboBox.removeActionListener(this);

        OBJModelShapes OBJModelShape = OBJModelShapes.values()[objModelLoaderPresetsComboBox.getSelectedIndex()];
        OBJ_MODEL_LOADER_CHOSEN_PRESET_SHAPE.set( OBJModelShape.toString() );
        objModelLoaderSizesComboBox.setSelectedItem(OBJ_MODEL_SHAPE_SIZES[OBJModelShape.ordinal()]);
        OBJ_MODEL_LOADER_SHAPE_SIZE.set(OBJ_MODEL_SHAPE_SIZES[OBJModelShape.ordinal()]);
        EXTERNAL_OBJ_MODEL_FILE_PATH = MODEL_FILES_PATH;
        EXTERNAL_OBJ_MODEL_FILE_NAME = (String)objModelLoaderPresetsComboBox.getSelectedItem();
        USE_EXTERNAL_OBJ_MODEL_FILE = false;
        modelShapeRenderer.setShapeName( (String)objModelLoaderPresetsComboBox.getSelectedItem() );
        modelShapeRenderer.setChangeDetected(true);
        modelShapeRenderer.refreshDisplay();
        modelShapeRenderer.refreshDisplay(); // second refresh for correct OBJ Model Loader rendering

        objModelLoaderSizesComboBox.addActionListener(this);
    }

    /**
    *  Refreshes the given OBJ Model preset.
    */
    private void refreshOBJModelPreset()
    {
        USE_EXTERNAL_OBJ_MODEL_FILE = false;
        modelShapeRenderer.setChangeDetected(true);
        modelShapeRenderer.refreshDisplay();
        modelShapeRenderer.refreshDisplay(); // second refresh for correct OBJ Model Loader rendering
    }

    /**
    *  Loads the given OBJ Model file.
    */
    private void loadOBJModelFile()
    {
        if ( JFileChooser.APPROVE_OPTION == objModelFileChooser.showOpenDialog(parentDialog) )
        {
            String objModelFullFileName = objModelFileChooser.getSelectedFile().getPath();
            EXTERNAL_OBJ_MODEL_FILE_PATH = objModelFullFileName.substring(0, objModelFullFileName.lastIndexOf( System.getProperty("file.separator") ) + 1);
            EXTERNAL_OBJ_MODEL_FILE_NAME = objModelFullFileName.substring( objModelFullFileName.lastIndexOf( System.getProperty("file.separator") ) + 1, objModelFullFileName.lastIndexOf(".") );

            OBJ_MODEL_LOADER_SHAPE_SIZE.set( (Float)objModelLoaderSizesComboBox.getSelectedItem() );
            USE_EXTERNAL_OBJ_MODEL_FILE = true;
            modelShapeRenderer.setShapeName(EXTERNAL_OBJ_MODEL_FILE_NAME);
            modelShapeRenderer.setChangeDetected(true);
            modelShapeRenderer.refreshDisplay();
            modelShapeRenderer.refreshDisplay(); // second refresh for correct OBJ Model Loader rendering

            FILE_CHOOSER_PATH.set( objModelFileChooser.getSelectedFile().getAbsolutePath() );
        }
    }

    /**
    *  Implements all UI related actions.
    */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        if ( e.getSource().equals(objModelLoaderPresetsComboBox) )
        {
            loadOBJModelPreset();
        }
        else if ( e.getSource().equals(objModelLoaderSizesComboBox) )
        {
            OBJ_MODEL_LOADER_SHAPE_SIZE.set( (Float)objModelLoaderSizesComboBox.getSelectedItem() );
            modelShapeRenderer.setChangeDetected(true);
            modelShapeRenderer.refreshDisplay();
            modelShapeRenderer.refreshDisplay(); // second refresh for correct OBJ Model Loader rendering
        }
        else if ( e.getSource().equals(objModelLoaderUseExternalOBJModelCheckBox) )
        {
            boolean flag = objModelLoaderUseExternalOBJModelCheckBox.isSelected();
            objModelLoaderPresetsComboBox.setEnabled(!flag);
            objModelLoaderLoadButton.setEnabled(flag);
            if (!flag && USE_EXTERNAL_OBJ_MODEL_FILE)
                loadOBJModelPreset();
            else
                refreshOBJModelPreset();
        }
        else if ( e.getSource().equals(objModelLoaderLoadButton) )
        {
            loadOBJModelFile();
        }
    }


}
