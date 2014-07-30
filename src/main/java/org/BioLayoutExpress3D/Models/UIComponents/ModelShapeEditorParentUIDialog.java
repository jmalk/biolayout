package org.BioLayoutExpress3D.Models.UIComponents;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import org.BioLayoutExpress3D.Graph.*;
import org.BioLayoutExpress3D.Models.*;
import org.BioLayoutExpress3D.Models.UIComponents.Panels.*;
import org.BioLayoutExpress3D.Models.UIComponents.ToolBars.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import static org.BioLayoutExpress3D.Models.ModelTypes.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* The ModelShapeEditorParentUIDialog class is the UI placeholder for all 3D model shapes along with the OpenGL canvas & renderer of various model shapes.
*
* @see org.BioLayoutExpress3D.Models.ModelShape
* @see org.BioLayoutExpress3D.Models.ModelTypes
* @see org.BioLayoutExpress3D.Models.UIComponents.ModelShapeRenderer
* @author Thanos Theo, 2011
* @version 3.0.0.0
*
*/

public class ModelShapeEditorParentUIDialog extends JDialog implements ActionListener
{

    private static final String MODEL_SHAPE_VIEWER_NAME = "Model Shape Editor";
    private static final String[] MODEL_SHAPE_BORDER_TITLES = { "Lathe3D", "SuperQuadric", "OBJ Model Loader" };
    private static final int MODEL_SHAPE_UI_PANEL_WIDTH  = 330;
    private static final int MODEL_SHAPE_UI_SPECIFIC_SETTINGS_PANEL_HEIGHT = 200;
    private static final int MODEL_SHAPE_UI_OPENGL_SETTINGS_PANEL_HEIGHT = 252;
    private static final int MODEL_SHAPE_RENDERER_PANEL_SIZE  = 450;
    private static final int MODEL_SHAPE_TOOLBAR_PANEL_SIZE  = 100;

    private AbstractAction modelShapeLathe3DAction = null;
    private AbstractAction modelShapeSuperQuadricAction = null;
    private AbstractAction modelShapeOBJModelLoaderAction = null;

    private JButton okButton = null;
    private JButton cancelButton = null;
    private JButton exportShapeAsOBJFileButton = null;

    /**
    *  The allConstantModelShapeRelatedLeftUIPanel JPanel reference.
    */
    private JPanel allConstantModelShapeRelatedLeftUIPanel = null;

    /**
    *  The ModelShapeLathe3DSettingsPanel JPanel reference.
    */
    private JPanel modelShapeLathe3DSettingsPanel = null;

    /**
    *  The ModelShapeSuperQuadricSettingsPanel JPanel reference.
    */
    private JPanel modelShapeSuperQuadricSettingsPanel = null;

    /**
    *  The ModelShapeOBJModelLoaderSettingsPanel JPanel reference.
    */
    private JPanel modelShapeOBJModelLoaderSettingsPanel = null;

    /**
    *  The ModelShapeLathe3DSettingsPanel JPanel reference.
    */
    private ModelShapeLathe3DSettingsPanel modelShapeLathe3DSpecificSettingsPanel = null;

    /**
    *  The ModelShapeOBJModelLoaderSettingsPanel JPanel reference.
    */
    private ModelShapeOBJModelLoaderSettingsPanel modelShapeOBJModelLoaderSpecificSettingsPanel = null;

    /**
    *  The ModelShapeOpenGLSettingsPanel reference.
    */
    private ModelShapeOpenGLSettingsPanel modelShapeOpenGLSettingsPanel = null;

    /**
    *  The ModelShapeRenderer reference.
    */
    private ModelShapeRenderer modelShapeRenderer = null;

    /**
    *  The ModelTypes reference.
    */
    private ModelTypes modelType = null;

    /**
    *  The rightUIPanel JPanel reference.
    */
    private JPanel rightUIPanel = null;

    /**
    *  The JFileChooser reference.
    */
    private JFileChooser fileChooser = null;

    /**
    *  The FileNameExtensionFilter reference.
    */
    private FileNameExtensionFilter fileNameExtensionFilterOBJ = null;

    /**
    *  ModelShapeEditorParentUIDialogListener listener to be used as a callback for registering a a preference change through the UI.
    */
    private ModelShapeEditorParentUIDialogListener listener = null;

    /**
    *  The ModelShapeEditorParentUIDialog class constructor.
    */
    public ModelShapeEditorParentUIDialog(JDialog dialog, Graph graph)
    {
        super(dialog, MODEL_SHAPE_VIEWER_NAME, true);

        initActions();
        initComponents(graph);
    }

    /**
    *  Initializes the UI actions for this dialog.
    */
    private void initActions()
    {
        modelShapeLathe3DAction = new AbstractAction("Lathe3D Shape Editor")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555697L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                openDialogWindowInLightWeightThread(LATHE3D_SHAPE, "Lathe3D Shape");
            }
        };
        modelShapeLathe3DAction.setEnabled(true);

        modelShapeSuperQuadricAction = new AbstractAction("SuperQuadric Shape Editor")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555698L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                openDialogWindowInLightWeightThread(SUPER_QUADRIC_SHAPE, "SuperQuadric Shape");
            }
        };
        modelShapeSuperQuadricAction.setEnabled(true);

        modelShapeOBJModelLoaderAction = new AbstractAction("OBJ Model Loader Shape Editor")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555699L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                openDialogWindowInLightWeightThread(OBJ_MODEL_LOADER_SHAPE, "OBJ Model Loader Shape");
            }
        };
        modelShapeOBJModelLoaderAction.setEnabled(true);
    }

    /**
    *  Initializes the UI components for this dialog.
    */
    private void initComponents(Graph graph)
    {
        fileNameExtensionFilterOBJ = new FileNameExtensionFilter( "Export Model Shape As An OBJ File", OBJ_MODEL_LOADER_SHAPE.toString().split("_+")[0].toLowerCase() );

        String saveFilePath = FILE_CHOOSER_PATH.get().substring(0, FILE_CHOOSER_PATH.get().lastIndexOf( System.getProperty("file.separator") ) + 1);
        fileChooser = new JFileChooser(saveFilePath);
        fileChooser.setFileFilter(fileNameExtensionFilterOBJ);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        modelShapeRenderer = new ModelShapeRenderer(this, graph);
        modelShapeRenderer.addAllEvents(); // GL events have to be registered only once (permanently with the Model Shape Editor UI) to avoid nasty init() issues

        modelShapeLathe3DSettingsPanel = initializeModelShapeSpecificSettingsPanel(LATHE3D_SHAPE);
        modelShapeSuperQuadricSettingsPanel = initializeModelShapeSpecificSettingsPanel(SUPER_QUADRIC_SHAPE);
        modelShapeOBJModelLoaderSettingsPanel = initializeModelShapeSpecificSettingsPanel(OBJ_MODEL_LOADER_SHAPE);

        JPanel modelShapeOpenGLSettingsRendererPanel = new JPanel(true);
        modelShapeOpenGLSettingsRendererPanel.setLayout( new BorderLayout() );
        modelShapeOpenGLSettingsRendererPanel.setPreferredSize( new Dimension(MODEL_SHAPE_UI_PANEL_WIDTH, MODEL_SHAPE_UI_OPENGL_SETTINGS_PANEL_HEIGHT) );
        modelShapeOpenGLSettingsPanel = new ModelShapeOpenGLSettingsPanel(modelShapeRenderer, this);
        modelShapeOpenGLSettingsRendererPanel.add(modelShapeOpenGLSettingsPanel, BorderLayout.CENTER);
        modelShapeOpenGLSettingsRendererPanel.setBorder( BorderFactory.createTitledBorder("Model Shape OpenGL Settings") );

        JPanel buttonsPanel = new JPanel(true);
        okButton = new JButton();
        okButton.addActionListener(this);
        okButton.setText("OK");
        okButton.setToolTipText("OK");
        exportShapeAsOBJFileButton = new JButton();
        exportShapeAsOBJFileButton.addActionListener(this);
        exportShapeAsOBJFileButton.setText("Export Shape As OBJ File");
        exportShapeAsOBJFileButton.setToolTipText("Export Shape As OBJ File");
        cancelButton = new JButton();
        cancelButton.addActionListener(this);
        cancelButton.setText("Cancel");
        cancelButton.setToolTipText("Cancel");
        buttonsPanel.add(okButton);
        buttonsPanel.add(exportShapeAsOBJFileButton);
        buttonsPanel.add(cancelButton);

        allConstantModelShapeRelatedLeftUIPanel = new JPanel(true);
        allConstantModelShapeRelatedLeftUIPanel.setLayout( new BoxLayout(allConstantModelShapeRelatedLeftUIPanel, BoxLayout.Y_AXIS) );
        allConstantModelShapeRelatedLeftUIPanel.add(modelShapeOpenGLSettingsRendererPanel);
        allConstantModelShapeRelatedLeftUIPanel.add(buttonsPanel);

        JPanel modelShapeRenderViewPanel = new JPanel(true);
        modelShapeRenderViewPanel.setLayout( new BorderLayout() );
        modelShapeRenderViewPanel.setOpaque(false);
        modelShapeRenderViewPanel.setPreferredSize( new Dimension(MODEL_SHAPE_RENDERER_PANEL_SIZE, MODEL_SHAPE_RENDERER_PANEL_SIZE) );
        modelShapeRenderViewPanel.add(modelShapeRenderer, BorderLayout.CENTER);
        modelShapeRenderViewPanel.setBorder( BorderFactory.createTitledBorder("Model Shape Render View ") );

        JPanel modelShapeNavigationToolBarPanel = new JPanel(true);
        modelShapeNavigationToolBarPanel.setLayout( new BorderLayout() );
        modelShapeNavigationToolBarPanel.add(initializeModelShapeNavigationToolBar(), BorderLayout.SOUTH);

        rightUIPanel = new JPanel(true);
        rightUIPanel.setLayout( new BoxLayout(rightUIPanel, BoxLayout.X_AXIS) );
        rightUIPanel.add(modelShapeRenderViewPanel);
        rightUIPanel.add(modelShapeNavigationToolBarPanel);

        this.setResizable(false);
        this.setSize(MODEL_SHAPE_UI_PANEL_WIDTH + MODEL_SHAPE_RENDERER_PANEL_SIZE + MODEL_SHAPE_TOOLBAR_PANEL_SIZE, MODEL_SHAPE_RENDERER_PANEL_SIZE);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.addWindowListener( new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                closeDialogWindow(false);
            }
        } );
    }

    /**
    *  Initializes the model shape specific settings panel.
    */
    private JPanel initializeModelShapeSpecificSettingsPanel(ModelTypes modelType)
    {
        JPanel modelShapeSpecificSettingsUIPanel = new JPanel(true);
        modelShapeSpecificSettingsUIPanel.setLayout( new BorderLayout() );
        modelShapeSpecificSettingsUIPanel.setPreferredSize( new Dimension(MODEL_SHAPE_UI_PANEL_WIDTH, MODEL_SHAPE_UI_SPECIFIC_SETTINGS_PANEL_HEIGHT) );
        JPanel modelShapeSpecificSettingsPanel = null;
        String borderTitle = "";
        if ( modelType.equals(LATHE3D_SHAPE) )
        {
            modelShapeSpecificSettingsPanel = modelShapeLathe3DSpecificSettingsPanel = new ModelShapeLathe3DSettingsPanel(modelShapeRenderer);
            borderTitle = MODEL_SHAPE_BORDER_TITLES[LATHE3D_SHAPE.ordinal()];
        }
        else if ( modelType.equals(SUPER_QUADRIC_SHAPE) )
        {
            modelShapeSpecificSettingsPanel = new ModelShapeSuperQuadricSettingsPanel(modelShapeRenderer);
            borderTitle = MODEL_SHAPE_BORDER_TITLES[SUPER_QUADRIC_SHAPE.ordinal()];
        }
        else // if ( modelType.equals(OBJ_MODEL_LOADER_SHAPE) )
        {
            modelShapeSpecificSettingsPanel = modelShapeOBJModelLoaderSpecificSettingsPanel = new ModelShapeOBJModelLoaderSettingsPanel(this, modelShapeRenderer);
            borderTitle = MODEL_SHAPE_BORDER_TITLES[OBJ_MODEL_LOADER_SHAPE.ordinal()];
        }
        modelShapeSpecificSettingsUIPanel.add(modelShapeSpecificSettingsPanel, BorderLayout.CENTER);
        modelShapeSpecificSettingsUIPanel.setBorder( BorderFactory.createTitledBorder("Model Shape " + borderTitle + " Settings") );

        return modelShapeSpecificSettingsUIPanel;
    }

    /**
    *  Initializes the model shape navigation toolbar.
    */
    private JToolBar initializeModelShapeNavigationToolBar()
    {
        ModelShapeNavigationToolBar modelShapeNavigationToolBar = new ModelShapeNavigationToolBar();
        modelShapeNavigationToolBar.setUpAction( modelShapeRenderer.getTranslateUpAction() );
        modelShapeNavigationToolBar.setDownAction( modelShapeRenderer.getTranslateDownAction() );
        modelShapeNavigationToolBar.setLeftAction( modelShapeRenderer.getTranslateLeftAction() );
        modelShapeNavigationToolBar.setRightAction( modelShapeRenderer.getTranslateRightAction() );
        modelShapeNavigationToolBar.setRotateUpAction( modelShapeRenderer.getRotateUpAction() );
        modelShapeNavigationToolBar.setRotateDownAction( modelShapeRenderer.getRotateDownAction() );
        modelShapeNavigationToolBar.setRotateLeftAction( modelShapeRenderer.getRotateLeftAction() );
        modelShapeNavigationToolBar.setRotateRightAction( modelShapeRenderer.getRotateRightAction() );
        modelShapeNavigationToolBar.setZoomInAction( modelShapeRenderer.getZoomInAction() );
        modelShapeNavigationToolBar.setZoomOutAction( modelShapeRenderer.getZoomOutAction() );
        modelShapeNavigationToolBar.setResetViewAction(modelShapeRenderer.getResetViewAction(), false);

        return modelShapeNavigationToolBar;
    }

    /**
    *  Resets and adds all UI components to the dialog.
    */
    private void resetAndAddAllUIComponentsToDialog()
    {
        JPanel leftUIPanel = new JPanel(true);
        leftUIPanel.setLayout( new BoxLayout(leftUIPanel, BoxLayout.Y_AXIS) );
        if ( modelType.equals(LATHE3D_SHAPE) )
        {
            leftUIPanel.add(modelShapeLathe3DSettingsPanel);
        }
        else if ( modelType.equals(SUPER_QUADRIC_SHAPE) )
        {
            leftUIPanel.add(modelShapeSuperQuadricSettingsPanel);
        }
        else // if ( modelType.equals(OBJ_MODEL_LOADER_SHAPE) )
        {
            leftUIPanel.add(modelShapeOBJModelLoaderSettingsPanel);
        }
        leftUIPanel.add(allConstantModelShapeRelatedLeftUIPanel);

        Container modelShapeEditorParentUIDialogContainer = this.getContentPane();
        modelShapeEditorParentUIDialogContainer.removeAll();
        modelShapeEditorParentUIDialogContainer.setLayout( new BoxLayout(modelShapeEditorParentUIDialogContainer, BoxLayout.X_AXIS) );
        modelShapeEditorParentUIDialogContainer.add(leftUIPanel);
        modelShapeEditorParentUIDialogContainer.add(rightUIPanel);

        this.pack();
    }

    /**
    *  Sets the selected state of the autoRotateView checkbox.
    */
    public void setSelectedAutoRotateViewCheckBox(boolean flag)
    {
        modelShapeOpenGLSettingsPanel.setSelectedAutoRotateViewCheckBox(flag);
    }

    /**
    *  Refreshes the modelShapeLathe3DSpecificSettingsPanel panel.
    */
    public void refreshModelShapeLathe3DDrawLinesPanel()
    {
        if ( modelType.equals(LATHE3D_SHAPE) )
            modelShapeLathe3DSpecificSettingsPanel.repaint();
    }

    /**
    *  Updates the sizesComboBox with the default OBJ Model Shape size.
    */
    public void updateSizesComboBoxWithDefaultOBJModelShapeSize()
    {
        if ( modelType.equals(OBJ_MODEL_LOADER_SHAPE) )
        {
            modelShapeOBJModelLoaderSpecificSettingsPanel.updateSizesComboBoxWithDefaultOBJModelShapeSize();
            modelShapeOBJModelLoaderSpecificSettingsPanel.repaint();
        }
    }

    /**
    *  Sets the cameras's IntraOcularDistance & frustum shift variables.
    */
    public void setCamerasIntraOcularDistanceAndFrustumShift(double intraOcularDistance)
    {
        modelShapeRenderer.setCamerasIntraOcularDistanceAndFrustumShift(intraOcularDistance);
    }

    /**
    *  Opens the ModelShapeEditorParentUIDialog dialog window in a lightweight thread.
    */
    private void openDialogWindowInLightWeightThread(final ModelTypes modelType, final String shapeTypeTitle)
    {
        Thread runLightWeightThread = new Thread( new Runnable()
        {

            @Override
            public void run()
            {
                openDialogWindow(modelType, shapeTypeTitle);
            }


        } );

        runLightWeightThread.setPriority(Thread.MAX_PRIORITY);
        runLightWeightThread.start();
    }

    /**
    *  Opens the ModelShapeEditorParentUIDialog dialog window.
    */
    public void openDialogWindow(ModelTypes modelType, String shapeTypeTitle)
    {
        this.modelType = modelType;

        resetAndAddAllUIComponentsToDialog();
        modelShapeOpenGLSettingsPanel.setModelTypeSettings(modelType);
        modelShapeOpenGLSettingsPanel.setTesselation( NODE_TESSELATION.get() );
        exportShapeAsOBJFileButton.setEnabled( !modelType.equals(OBJ_MODEL_LOADER_SHAPE) );

        modelShapeRenderer.setModelShapeType(modelType);
        modelShapeRenderer.setTesselation( NODE_TESSELATION.get() );
        modelShapeRenderer.setChangeDetected(true);
        modelShapeRenderer.setClosingDialogWindow(false);
        modelShapeRenderer.resetView();
        modelShapeRenderer.refreshDisplay();
        if ( MODEL_SHAPE_EDITOR_AUTOROTATE_VIEW.get() )
        {
            modelShapeRenderer.startRender();
            setSelectedAutoRotateViewCheckBox(true);
        }

        this.setTitle(MODEL_SHAPE_VIEWER_NAME + ": " + shapeTypeTitle);
        this.setVisible(true);
    }

    /**
    *  Closes the ModelShapeEditorParentUIDialog dialog window.
    */
    public void closeDialogWindow(boolean usePreferencesCallBackListener)
    {
        if ( modelShapeRenderer.isAnimating() )
        {
            modelShapeRenderer.stopRender();
            setSelectedAutoRotateViewCheckBox(false);
        }
        modelShapeRenderer.setDeAllocOpenGLMemory(true);
        modelShapeRenderer.setClosingDialogWindow(true);
        modelShapeRenderer.refreshDisplay();
        // modelShapeRenderer.removeAllEvents(); // GL events have to be registered only once (permanently with the Model Shape Editor UI) to avoid nasty init() issues

        if (usePreferencesCallBackListener && listener != null) listener.updateModelShapeEditorParentUIDialogPreferencesCallBack(modelType);
        this.setVisible(false);
    }

    /**
    *  Implements all UI related actions.
    */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        if ( e.getSource().equals(okButton) )
        {
            closeDialogWindow(true);
        }
        else if ( e.getSource().equals(cancelButton) )
        {
            closeDialogWindow(false);
        }
        else if ( e.getSource().equals(exportShapeAsOBJFileButton) )
        {
            String fileType = modelType.equals(LATHE3D_SHAPE) ? "Lathe3D" : "SuperQuadric";
            fileChooser.setDialogTitle("Export " + fileType + " Model Shape As An OBJ File" );
            fileChooser.setSelectedFile( new File(fileType) );

            save();
        }
    }

    /**
    *  Gets the modelShapeLathe3DAction action.
    */
    public AbstractAction getModelShapeLathe3DAction()
    {
        return modelShapeLathe3DAction;
    }

    /**
    *  Gets the modelShapeSuperQuadricAction action.
    */
    public AbstractAction getModelShapeSuperQuadricAction()
    {
        return modelShapeSuperQuadricAction;
    }

    /**
    *  Gets the modelShapeOBJModelLoaderAction action.
    */
    public AbstractAction getModelShapeOBJModelLoaderAction()
    {
        return modelShapeOBJModelLoaderAction;
    }

    private void save()
    {
        int dialogReturnValue = 0;
        boolean doSaveFile = false;
        File saveFile = null;

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            String fileExtension = "";

            if ( fileChooser.getFileFilter().equals(fileNameExtensionFilterOBJ) )
            {
                fileExtension = fileNameExtensionFilterOBJ.getExtensions()[0];
            }
            else // default file extension will be the OBJ file format
            {
                fileExtension = fileNameExtensionFilterOBJ.getExtensions()[0];
            }

            String fileName = fileChooser.getSelectedFile().getAbsolutePath();
            fileName = IOUtils.removeMultipleExtensions(fileName, fileExtension);
            saveFile = new File(fileName + "." + fileExtension);

            if ( saveFile.exists() )
            {
                // Do you want to overwrite
                dialogReturnValue = JOptionPane.showConfirmDialog(this, "This File Already Exists.\nDo you want to Overwrite it?", "This File Already Exists. Overwrite?", JOptionPane.YES_NO_CANCEL_OPTION);

                if (dialogReturnValue == JOptionPane.YES_OPTION)
                    doSaveFile = true;
            }
            else
            {
                doSaveFile = true;
            }
        }

        if (doSaveFile)
        {
            // saving process on its own thread, to effectively decouple it from the main GUI thread
            Thread runLightWeightThread = new Thread( new ExportModelShapeProcess(saveFile) );
            runLightWeightThread.setPriority(Thread.NORM_PRIORITY);
            runLightWeightThread.start();
        }
    }

    private void saveModelShapeFile(File saveFile)
    {
        FileWriter fileWriter = null;

        try
        {
            fileWriter = new FileWriter(saveFile);
            modelShapeRenderer.saveModelShapeOBJFile(fileWriter, modelType);
            fileWriter.flush();
        }
        catch (IOException ioe)
        {
            if (DEBUG_BUILD) println("IOException in ModelShapeEditorParentUIDialog.saveModelShapeFile():\n" + ioe.getMessage());

            JOptionPane.showMessageDialog(this, "Something went wrong while saving the file:\n" + ioe.getMessage() + "\nPlease try again with a different file name/path/drive.", "Error with saving the file!", JOptionPane.ERROR_MESSAGE);
            save();
        }
        finally
        {
            try
            {
                if (fileWriter != null) fileWriter.close();
            }
            catch (IOException ioe)
            {
                if (DEBUG_BUILD) println("IOException while closing streams in ModelShapeEditorParentUIDialog.saveModelShapeFile():\n" + ioe.getMessage());
            }
        }
    }

    private class ExportModelShapeProcess implements Runnable
    {

        private File saveFile = null;

        private ExportModelShapeProcess(File saveFile)
        {
            this.saveFile = saveFile;
        }

        @Override
        public void run()
        {
            saveModelShapeFile(saveFile);

            FILE_CHOOSER_PATH.set( saveFile.getAbsolutePath() );
        }


    }

    /**
    *  Sets the ModelShapeEditorParentUIDialogListener listener.
    */
    public void setListener(ModelShapeEditorParentUIDialogListener listener)
    {
        this.listener = listener;
    }

    /**
    *  Removes the ModelShapeEditorParentUIDialogListener listener.
    */
    public void removeListener()
    {
        listener = null;
    }


}
