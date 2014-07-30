package org.BioLayoutExpress3D.CoreUI.Dialogs;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.CoreUI.Tables.*;
import org.BioLayoutExpress3D.Environment.Preferences.*;
import org.BioLayoutExpress3D.Files.*;
import org.BioLayoutExpress3D.Graph.Camera.CameraUI.*;
import org.BioLayoutExpress3D.Graph.Camera.CameraUI.Dialogs.*;
import org.BioLayoutExpress3D.Graph.GraphElements.*;
import org.BioLayoutExpress3D.Models.*;
import org.BioLayoutExpress3D.Models.UIComponents.*;
import org.BioLayoutExpress3D.Network.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import org.BioLayoutExpress3D.Textures.*;
import org.BioLayoutExpress3D.Utils.*;
import static org.BioLayoutExpress3D.CoreUI.Dialogs.LayoutGraphPropertiesDialog.LayoutGraphPropertiesTabTypes.*;
import static org.BioLayoutExpress3D.Models.ModelTypes.*;
import static org.BioLayoutExpress3D.StaticLibraries.EnumUtils.*;
import static org.BioLayoutExpress3D.Environment.AnimationEnvironment.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;
import org.BioLayoutExpress3D.Environment.DataFolder;

/**
*
* @author Anton Enright, full refactoring and extended by Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public class LayoutGraphPropertiesDialog extends JDialog implements LayoutClassesTable.LayoutClassesTableListener, CaretListener, GraphAnaglyphGlasses3DOptionsDialogListener, ModelShapeEditorParentUIDialogListener, ActionListener
{
    /**
    *  Serial version UID variable for the PropertiesDialog class.
    */
    public static final long serialVersionUID = 111222333444555699L;

    public static enum LayoutGraphPropertiesTabTypes { GENERAL, LAYOUT, RENDERING, MCL, SIMULATION, PARALLELISM, SEARCH, NODES, EDGES, CLASSES }
    private static final int NUMBER_OF_TOTAL_TABS = LayoutGraphPropertiesTabTypes.values().length;
    private static final int NUMBER_OF_SHADER_CHECKBOXES_PER_COLUMN = 3;
    private static final String SHADER_CHECKBOX_MESSAGE = "Shader";

    private JTabbedPane tabbedPane = null;
    private JPanel generalPropertiesPanel = null;
    private JPanel layoutPropertiesPanel = null;
    private JPanel renderingPropertiesPanel = null;
    private JPanel MCL_propertiesPanel = null;
    private JPanel simulationPropertiesPanel = null;
    private JPanel parallelismPropertiesPanel = null;
    private JPanel searchPropertiesPanel = null;
    private JPanel nodesPropertiesPanel = null;
    private JPanel edgesPropertiesPanel = null;
    private JPanel classesPropertiesPanel = null;

    private AbstractAction generalPropertiesAction = null;
    private AbstractAction layoutPropertiesAction = null;
    private AbstractAction renderingPropertiesAction = null;
    private AbstractAction MCL_propertiesAction = null;
    private AbstractAction simulationPropertiesAction = null;
    private AbstractAction parallelismPropertiesAction = null;
    private AbstractAction searchPropertiesAction = null;
    private AbstractAction nodesPropertiesAction = null;
    private AbstractAction edgesPropertiesAction = null;
    private AbstractAction classesPropertiesAction = null;

    private static final Border ETCHED = BorderFactory.createEtchedBorder();
    private static final Border EMPTY  = BorderFactory.createEmptyBorder();

    private final static String CHANGE_ACTION_COMMAND       = "CHANGE_ACTION_COMMAND";
    private final static String CHANGE_ACTION_COMMAND_NODES = "CHANGE_ACTION_COMMAND_NODES";
    private final static String CHANGE_ACTION_COMMAND_EDGES = "CHANGE_ACTION_COMMAND_EDGES";
    private final static String CHANGE_ACTION_COMMAND_ALL   = "CHANGE_ACTION_COMMAND_ALL";

    private LayoutFrame layoutFrame = null;
    private LayoutClassSetsManager layoutClassSetsManager = null;
    private NetworkContainer nc = null;

    private JButton[] allApplyButtons = null;
    private boolean hasNewPreferencesBeenApplied = false;

    private boolean nodeClassChange = false;
    private boolean nodeColorChange = false;
    private boolean nodeSizeChange = false;
    private boolean node2DShapeChange = false;
    private boolean node3DShapeChange = false;
    private boolean node3DTransparencyAlphaChange = false;
    private boolean watchForChanges = false;
    private boolean isChangingClasses = false;
    private boolean _3DRebuildNodes = false;
    private boolean _3DRebuildEdges = false;
    private boolean optionsMCLChanged = false;
    private boolean isChangingClassSet = false;

    private ColorButton generalColor = null;
    private ColorButton generalColorSelection = null;
    private ColorButton generalColorPlotBackground = null;
    private ColorButton generalColorPlotGridlines = null;
    private JCheckBox generalTrippyBackground = null;

    private JCheckBox generalDisableNodesRendering = null;
    private JCheckBox generalDisableEdgesRendering = null;
    private JCheckBox generalDirectional = null;
    private JCheckBox generalDragShowEdgesWhileDraggingNodes = null;
    private JCheckBox generalyEdStyleRenderingForGraphmlFiles = null;
    private JCheckBox generalyEdStyleComponentContainersRenderingForGraphmlFiles = null;

    private JRadioButton generalHighQualityAntiAlias = null;
    private JRadioButton generalNormalQualityAntiAlias = null;
    private JRadioButton generalNoAntiAlias = null;
    private JCheckBox useVSynch = null;

    private JCheckBox generalShowNavigationWizardOnStartup = null;
    private JCheckBox generalShowLayoutIterations = null;
    private JCheckBox generalValidateXMLFiles = null;
    private JCheckBox generalUseInstallDirForScreenshots = null;
    private JCheckBox generalUseInstallDirForMCLTempFile = null;
    private JCheckBox generalShowGraphPropertiesToolBar = null;
    private JCheckBox generalShowNavigationToolBar = null;
    private JCheckBox generalShowPopupOverlayPlot = null;
    private JCheckBox generalCollapseNodesByVolume = null;
    private JCheckBox generalConfirmPreferencesSave = null;

    private JRadioButton frRadioButton = null;
    private JRadioButton fmmmRadioButton = null;
    private JRadioButton circleRadioButton = null;
    private JRadioButton askRadioButton = null;

    private JCheckBox layoutUseEdgeWeightsForLayout = null;
    private JCheckBox layoutTiledLayout = null;
    private FloatNumberField layoutKvalueField = null;
    private FloatNumberField layoutStartingTemperatureField = null;
    private JTextField layoutIterationsField = null;
    private JTextField layoutBurstIterationsField = null;
    private JTextField layoutMinimumComponentSizeField = null;

    private FloatNumberField fmmmDesiredEdgeLength = null;
    private JComboBox<String> fmmmForceModel = null;
    private JComboBox<String> fmmmQualityVsSpeed = null;
    private JComboBox<String> fmmmStopCriterion = null;
    private FloatNumberField fmmmIterationLevelFactor = null;

    private SimpleSlider _3DNodeTesselationSlider = null;
    private JCheckBox showNodes = null;
    private JCheckBox show3DFrustum = null;
    private JCheckBox show3DShadows = null;
    private JCheckBox show3DEnvironmentMapping = null;
    private SimpleSlider highResImageRenderScaleSlider = null;
    private JCheckBox fastSelectionMode = null;
    private JCheckBox wireframeSelectionMode = null;
    private JCheckBox advancedKeyboardRenderingControl = null;
    private JCheckBox anaglyphStereoscopic3DView = null;
    private GraphAnaglyphGlasses3DOptionsDialog graphAnaglyphGlasses3DOptionsDialog = null;
    private JButton anaglyphStereoscopic3DViewOptions = null;
    private SimpleSlider lightingPositionXSlider = null;
    private SimpleSlider lightingPositionYSlider = null;
    private SimpleSlider lightingPositionZSlider = null;
    private JCheckBox depthFog = null;
    private JCheckBox useMotionBlurForScene = null;
    private SimpleSlider motionBlurSize = null;
    private JCheckBox  materialSpecular = null;
    private SimpleSlider materialShininess = null;
    private JCheckBox materialGouraudLighting = null;
    private JCheckBox materialSphericalMapping = null;
    private JCheckBox materialEmbossNodeTexture = null;
    private JCheckBox materialAntiAliasShading = null;
    private JCheckBox materialAnimatedShading = null;
    private JCheckBox materialStateShading = null;
    private JCheckBox materialOldLCDStyleTransparencyShading = null;
    private JCheckBox materialErosionShading = null;
    private JCheckBox materialNormalsSelectionMode = null; //FIXME remove?
    private JCheckBox nodeSurfaceImageTextureCheckBox = null;
    private JComboBox nodeSurfaceImageTextureComboBox = null;
    private JTextField nodeSurfaceImageTextureFileTextField = null;
    private JButton nodeSurfaceImageTextureFileLoadButton = null;
    private JButton nodeSurfaceImageTextureFileClearButton = null;
    private JFileChooser nodeSurfaceImageTextureFileChooser = null;
    private JCheckBox[] allShadings = null;

    private FloatNumberField MCL_inflationField = null;
    private JSlider MCL_inflationSlider = null;
    private FloatNumberField MCL_preInflationField = null;
    private JSlider MCL_pre_inflationSlider = null;
    private JTextField MCL_schemeField = null;
    private JSlider MCL_SchemeSlider = null;
    private JTextField MCL_smallestClusterAllowedField = null;
    private JCheckBox MCL_assignRandomClusterColorsCheckBox = null;
    private JTextField MCL_advancedOptionsTextField = null;
    private JButton MCL_clusterGraphUsingMCLButton = null;

    private JCheckBox saveSPNResultsCheckBox = null;
    private JCheckBox automaticallySaveSPNResultsToPreChosenFolderCheckBox = null;
    private JTextField saveSPNResultsTextField = null;
    private JButton saveSPNResultsAtFolderButton = null;
    private JButton saveSPNResultsClearButton = null;
    private JFileChooser saveSPNResultsFileChooser = null;
    private JCheckBox useSPNAnimatedTransitionsShadingCheckBox = null;
    private JButton runSPNSimulationButton = null;

    private JRadioButton parallelismUseExpressionCorrelationCalculationNCoreParallelism = null;
    private JRadioButton parallelismUseExpressionCorrelationCalculationOpenCLGPUComputing = null;
    private JLabel parallelismUseExpressionCorrelationCalculationOpenCLGPUComputingIterationSizeLabel = null;
    private JComboBox<Integer> parallelismUseExpressionCorrelationCalculationOpenCLGPUComputingIterationSize = null;
    private JRadioButton parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputing = null;
    private JLabel parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputingTextureSizeAndTypeLabel = null;
    private JComboBox<Integer> parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputingTextureSize = null;
    private JComboBox<GLSLTextureTypes> parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputingTextureType = null;
    private JCheckBox parallelismUseGPUComputingExpressionCorrelationCalculationCompareGPUWithCPU = null;
    private JLabel parallelismUseGPUComputingExpressionCorrelationCalculationCompareGPUWithCPUComparisonMethodsLabel = null;
    private JComboBox parallelismUseGPUComputingExpressionCorrelationCalculationCompareGPUWithCPUComparisonMethods = null;
    private JRadioButton parallelismUseLayoutNCoreParallelism = null;
    private JRadioButton parallelismUseLayoutOpenCLGPUComputing = null;
    private JCheckBox parallelismUseIndices1DKernelWithIterationsForLayoutOpenCLGPUComputing = null;
    private JLabel parallelismUseLayoutOpenCLGPUComputingIterationSizeLabel = null;
    private JComboBox<Integer> parallelismUseLayoutOpenCLGPUComputingIterationSize = null;
    private JCheckBox parallelismUseGPUComputingLayoutCompareGPUWithCPU = null;
    private JLabel parallelismUseGPUComputingLayoutCompareGPUWithCPUComparisonMethodsLabel = null;
    private JComboBox<String> parallelismUseGPUComputingLayoutCompareGPUWithCPUComparisonMethods = null;
    private JCheckBox parallelismUseAtomicSynchronizationForLayoutNCoreParallelism = null;
    private JCheckBox parallelismUseMCLNCoreParallelism = null;
    private JCheckBox parallelismUseSPNNCoreParallelism = null;

    private JComboBox<SearchURL> searchURLComboBox = null;
    private JTextField customURLTextField = null;
    private JRadioButton presetRadioButton = null;
    private JRadioButton customRadioButton = null;
    private JTextArea presetURLTextField = null;

    private JTextField nodeNameTextField = null;
    private JComboBox<String> node2DShape = null;
    private JComboBox<String> node3DShape = null;
    private JCheckBox  nodeTransparency = null;
    private SimpleSlider nodeTransparencyAlphaSlider = null;
    private JButton nodeRevertOverride = null;
    private ColorButton nodeColorButton = null;
    private ModelShapeEditorParentUIDialog modelShapeEditorParentUIDialog = null;
    private JComboBox<Integer> nodeSizeComboBox = null;
    private ClassComboBox nodeClassComboBox = null;
    private FloatNumberField graphmlViewNodeDepthPositioningTextField = null;
    private JLabel graphmlViewNodeDepthZPositioningLabel = null;
    private JButton graphmlAllViewNodeDepthResetPositioningsButton = null;
    private JTextField nodeClassSetName = null;
    private JTextField nodeClassName = null;
    private JButton newClassInClassSetButton = null;

    private ColorButton edgesColor = null;
    private JComboBox<String> edgeThicknessComboBox = null;
    private JCheckBox proportionalEdgesSizeToWeight = null;
    private JComboBox<String> arrowHeadSizeComboBox = null;
    private JRadioButton edgesColorByColor = null;
    private JRadioButton edgesColorByWeight = null;

    private LayoutClassesTable layoutClassesTable = null;
    private JComboBox<String> classesChooser = null;
    private JButton newClassSetButton = null;

    private boolean generalChange = false;
    private boolean updateGraphAnaglyphGlasses3DOptionsDialogPreferencesCallBackChange = false;
    private boolean updateModelShapeEditorParentUIDialogPreferencesCallBackChange = false;
    private boolean refreshClassViewer = false;
    private boolean wireframeSelectionModeWasNotSelected = false;
    private boolean sphericalMappingWasNotSelected = false;
    private boolean texturingWasNotSelected = false;
    private int howManyNodesSelected = 0;
    private GraphAnaglyphGlassesTypes graphAnaglyphGlassesType = GraphAnaglyphGlassesTypes.RED_BLUE;
    private GraphIntraOcularDistanceTypes graphIntraOcularDistanceType = GraphIntraOcularDistanceTypes._0_001;
    private ModelTypes modelType = ModelTypes.LATHE3D_SHAPE;
    private boolean originalFastSelectionModeState = false;
    private boolean original3DShadowsState = false;

    public LayoutGraphPropertiesDialog(LayoutFrame layoutFrame, LayoutClassSetsManager layoutClassSetsManager, NetworkContainer nc)
    {
        super(layoutFrame, "Graph Properties", false);

        this.layoutFrame = layoutFrame;
        this.layoutClassSetsManager = layoutClassSetsManager;
        this.nc = nc;

        initActions();
        initComponents();
    }

    private void initActions()
    {
        generalPropertiesAction = new AbstractAction("General Properties")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 112222333444555682L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                showPropertiesDialog(GENERAL);
            }
        };

        layoutPropertiesAction = new AbstractAction("Layout Properties")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 113222333444555682L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                showPropertiesDialog(LAYOUT);
            }
        };

        renderingPropertiesAction = new AbstractAction("Rendering Properties")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 114222333444555682L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                showPropertiesDialog(RENDERING);
            }
        };

        MCL_propertiesAction  = new AbstractAction("MCL Properties")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 115222333444555682L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                showPropertiesDialog(MCL);
            }
        };

        simulationPropertiesAction = new AbstractAction("Simulation Properties")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 116222333444555682L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                showPropertiesDialog(SIMULATION);
            }
        };

        parallelismPropertiesAction = new AbstractAction("Parallelism Properties")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 117222333444555682L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                showPropertiesDialog(PARALLELISM);
            }
        };

        searchPropertiesAction = new AbstractAction("Search Properties")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 118222333444555682L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                showPropertiesDialog(SEARCH);
            }
        };

        nodesPropertiesAction = new AbstractAction("Nodes Properties")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 119222333444555682L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                showPropertiesDialog(NODES);
            }
        };

        edgesPropertiesAction = new AbstractAction("Edges Properties")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 110222333444555682L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                showPropertiesDialog(EDGES);
            }
        };

        classesPropertiesAction = new AbstractAction("Classes Properties")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555600L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                showPropertiesDialog(CLASSES);
            }
        };
    }

    private void initComponents()
    {
        tabbedPane = new JTabbedPane();

        generalPropertiesPanel = createPropertiesPanel();
        layoutPropertiesPanel = createPropertiesPanel();
        renderingPropertiesPanel = createPropertiesPanel();
        MCL_propertiesPanel = createPropertiesPanel();
        simulationPropertiesPanel = createPropertiesPanel();
        parallelismPropertiesPanel = createPropertiesPanel();
        searchPropertiesPanel = createPropertiesPanel();
        nodesPropertiesPanel = createPropertiesPanel();
        edgesPropertiesPanel = createPropertiesPanel();
        classesPropertiesPanel = createPropertiesPanel();

        allApplyButtons = new JButton[NUMBER_OF_TOTAL_TABS];

        graphAnaglyphGlasses3DOptionsDialog = new GraphAnaglyphGlasses3DOptionsDialog(this);
        graphAnaglyphGlasses3DOptionsDialog.setListener(this);
        modelShapeEditorParentUIDialog = new ModelShapeEditorParentUIDialog( this, layoutFrame.getGraph() );
        modelShapeEditorParentUIDialog.setListener(this);

        createGeneralPropertiesTab( generalPropertiesPanel, tabbedPane, GENERAL.ordinal() );
        createLayoutPropertiesTab( layoutPropertiesPanel, tabbedPane, LAYOUT.ordinal() );
        createRenderingPropertiesTab( renderingPropertiesPanel, tabbedPane, RENDERING.ordinal() );
        createMCLPropertiesTab( MCL_propertiesPanel, tabbedPane, MCL.ordinal() );
        createSimulationPropertiesTab( simulationPropertiesPanel, tabbedPane, SIMULATION.ordinal() );
        createParallelismPropertiesTab( parallelismPropertiesPanel, tabbedPane, PARALLELISM.ordinal() );
        createSearchPropertiesTab( searchPropertiesPanel, tabbedPane, SEARCH.ordinal() );
        createNodesPropertiesTab( nodesPropertiesPanel, tabbedPane, NODES.ordinal() );
        createEdgesPropertiesTab( edgesPropertiesPanel, tabbedPane, EDGES.ordinal() );
        createClassesPropertiesTab( classesPropertiesPanel, tabbedPane, CLASSES.ordinal() );

        tabbedPane.setSelectedComponent(generalPropertiesPanel);
        JScrollPane scrollPane = new JScrollPane(tabbedPane);

        this.getContentPane().add(scrollPane, BorderLayout.CENTER);

        int stringIndexToCut = 0;
        for (int i = MIN_EDGE_THICKNESS; i <= MAX_EDGE_THICKNESS; i++)
        {
            stringIndexToCut = (i < MAX_EDGE_THICKNESS) ? 3 : 4;
            edgeThicknessComboBox.addItem( Double.toString( (double)i / 10.0 ).substring(0, stringIndexToCut) );
        }

        for (int i = MIN_ARROWHEAD_SIZE; i <= MAX_ARROWHEAD_SIZE; i++)
            arrowHeadSizeComboBox.addItem( Integer.toString(i) );

        this.setResizable(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
    }

    private void showPropertiesDialog(LayoutGraphPropertiesTabTypes layoutGraphPropertiesTabType)
    {
        if ( layoutGraphPropertiesTabType.equals(NODES) &&  (howManyNodesSelected <= 0) )
            JOptionPane.showMessageDialog(this, "Please select some nodes first to be able to access the Nodes Properties!", "No Nodes Selected", JOptionPane.INFORMATION_MESSAGE);
        else
        {
            watchForChanges = false;
            generalChange = false;
            refreshClassViewer = false;
            _3DRebuildEdges = false;
            _3DRebuildNodes = false;

            Iterator<GraphNode> graphIterator = layoutFrame.getGraph().getSelectionManager().getSelectedNodes().iterator();
            GraphNode graphNode = null;
            GraphNode graphNodeNext = null;

            boolean selectedNodes = false;
            boolean multipleText = false;
            boolean multipleColor = false;
            boolean multipleSize = false;
            boolean multipleGraphmlNodeDepth = false;
            boolean multiple2DShape = false;
            boolean multiple3DShape = false;
            boolean multipleClass = false;

            nodeClassChange = false;
            nodeColorChange = false;
            nodeSizeChange = false;
            node2DShapeChange = false;
            node3DShapeChange = false;
            node3DTransparencyAlphaChange = false;

            _3DRebuildEdges = false;
            _3DRebuildNodes = false;

            readGeneralProperties();
            updateSearchPanel();

            classesPropertiesPanel.removeAll();
            tabbedPane.remove(classesPropertiesPanel);
            createClassesPropertiesTab( classesPropertiesPanel, tabbedPane, CLASSES.ordinal() );

            nodeClassComboBox.setMultiOption(false);

            if ( graphIterator.hasNext() )
            {
                selectedNodes = true;

                while ( graphIterator.hasNext() )
                {
                    graphNode = graphIterator.next();

                    if (graphNode.getVertexClass() != null)
                        if (graphNode.getVertexClass().getClassID() != 0)
                            if ( !graphNode.isOverrideClassColor() )
                                multipleColor = true;

                    if ( graphIterator.hasNext() )
                    {
                        graphNodeNext = graphIterator.next();

                        if ( !graphNode.getColor().equals( graphNodeNext.getColor() ) )
                            multipleColor = true;

                        if (graphNode.getVertexClass() != null)
                            if (graphNode.getVertexClass().getClassID() != 0)
                                if ( !graphNode.isOverrideClassColor() )
                                    multipleColor = true;

                        if ( !graphNode.getNodeName().equals( graphNodeNext.getNodeName() ) )
                            multipleText = true;

                        if ( graphNode.getNode2DShape() != graphNodeNext.getNode2DShape() )
                            multiple2DShape = true;

                        if ( graphNode.getNode3DShape() != graphNodeNext.getNode3DShape() )
                            multiple3DShape = true;

                        if ( graphNode.getNodeSize() != graphNodeNext.getNodeSize() )
                            multipleSize = true;

                        if ( nc.getIsGraphml() && graphmlViewNodeDepthPositioningTextField.isEnabled() )
                            if ( nc.getGraphmlNetworkContainer().getAllGraphmlNodesMap().get( graphNode.getNodeName() ).first[4] != nc.getGraphmlNetworkContainer().getAllGraphmlNodesMap().get( graphNodeNext.getNodeName() ).first[4] )
                                multipleGraphmlNodeDepth = true;

                        if ( (graphNode.getVertexClass() != null) && (graphNodeNext.getVertexClass() != null) )
                            if ( graphNode.getVertexClass().getClassID() != graphNodeNext.getVertexClass().getClassID() )
                                multipleClass = true;
                    }
                }

                tabbedPane.setEnabledAt(tabbedPane.indexOfComponent(nodesPropertiesPanel), true);
                tabbedPane.setSelectedComponent(nodesPropertiesPanel);
            }
            else
            {
                tabbedPane.setEnabledAt(tabbedPane.indexOfComponent(nodesPropertiesPanel), false);
                tabbedPane.setSelectedComponent(generalPropertiesPanel);
            }

            if (selectedNodes)
            {
                if (multipleText)
                {
                    nodeNameTextField.setText("N/A");
                    nodeNameTextField.setEnabled(false);
                }
                else
                {
                    nodeNameTextField.setText( nc.getNodeName( graphNode.getNodeName() ) );
                    nodeNameTextField.setEnabled(true);
                }

                if (multipleColor)
                {
                    nodeColorButton.setBackground(null);
                    nodeColorButton.isBlank = true;
                }
                else
                {
                    nodeColorButton.isBlank = false;
                    nodeColorButton.setBackground( graphNode.getColor() );
                }

                if (!multipleSize)
                {
                    int selectedNodeSize = (int) graphNode.getNodeSize();
                    int index = selectedNodeSize - 1;
                    nodeSizeComboBox.setSelectedIndex(index < nodeSizeComboBox.getItemCount() ? index : 0);
                }
                else
                {
                    nodeSizeComboBox.setSelectedIndex(0);
                }

                if ( nc.getIsGraphml() && graphmlViewNodeDepthPositioningTextField.isEnabled() )
                {
                    if (multipleGraphmlNodeDepth)
                        graphmlViewNodeDepthPositioningTextField.setText("0.0");
                    else
                    {
                        // inverse value to give the user the impression that a positive depth (Z) value is nearer to the viewer
                        float graphmlNodeViewDepth = -nc.getGraphmlNetworkContainer().getAllGraphmlNodesMap().get( graphNode.getNodeName() ).first[4];
                        // also get rid of - (negative sign) if value is -0.0f (yes, floats were keeping the sign!!!)
                        if (graphmlNodeViewDepth == 0.0f) graphmlNodeViewDepth = 0.0f;
                        graphmlViewNodeDepthPositioningTextField.setText( Float.toString(graphmlNodeViewDepth) );
                    }
                }

                if (multiple2DShape)
                    node2DShape.setSelectedIndex(NUMBER_OF_2D_SHAPES);
                else
                    node2DShape.setSelectedIndex( graphNode.getNode2DShape().ordinal() );

                if (multiple3DShape)
                    node3DShape.setSelectedIndex(NUMBER_OF_3D_SHAPES);
                else
                    node3DShape.setSelectedIndex( graphNode.getNode3DShape().ordinal() );

                if (multipleClass)
                {
                    nodeClassComboBox.setMultiOption(true);
                }
                else
                {
                    nodeClassComboBox.setSelectedItem( graphNode.getVertexClass() );
                }
            }

            watchForChanges = true;
            _3DRebuildEdges = false;
            _3DRebuildNodes = false;

            selectTab(layoutGraphPropertiesTabType);
            setEnabledAllApplyButtons(false);

            if ( !this.isVisible() )
                this.setLocationRelativeTo(null);
            this.setVisible(true);
        }
    }

    private void selectTab(LayoutGraphPropertiesTabTypes layoutGraphPropertiesTabType)
    {
        if ( layoutGraphPropertiesTabType.equals(GENERAL) )
            tabbedPane.setSelectedComponent(generalPropertiesPanel);
        else if ( layoutGraphPropertiesTabType.equals(LAYOUT) )
            tabbedPane.setSelectedComponent(layoutPropertiesPanel);
        else if ( layoutGraphPropertiesTabType.equals(RENDERING) )
            tabbedPane.setSelectedComponent(renderingPropertiesPanel);
        else if ( layoutGraphPropertiesTabType.equals(MCL) )
            tabbedPane.setSelectedComponent(MCL_propertiesPanel);
        else if ( layoutGraphPropertiesTabType.equals(SIMULATION) )
            tabbedPane.setSelectedComponent(simulationPropertiesPanel);
        else if ( layoutGraphPropertiesTabType.equals(PARALLELISM) )
            tabbedPane.setSelectedComponent(parallelismPropertiesPanel);
        else if ( layoutGraphPropertiesTabType.equals(SEARCH) )
            tabbedPane.setSelectedComponent(searchPropertiesPanel);
        else if ( layoutGraphPropertiesTabType.equals(NODES) )
            tabbedPane.setSelectedComponent(nodesPropertiesPanel);
        else if ( layoutGraphPropertiesTabType.equals(EDGES) )
            tabbedPane.setSelectedComponent(edgesPropertiesPanel);
        else if ( layoutGraphPropertiesTabType.equals(CLASSES) )
            tabbedPane.setSelectedComponent(classesPropertiesPanel);
    }

    private void updateSearchPanel()
    {
        if (CUSTOM_SEARCH)
        {
            customRadioButton.setSelected(true);
            customURLTextField.setText( SEARCH_URL.getUrl() );
        }
        else
        {
            presetRadioButton.setSelected(true);
            searchURLComboBox.setSelectedItem(SEARCH_URL);
        }
    }

    private JComponent addLegentToRightOfComponent(JComponent component, String string)
    {
        JPanel newComponent = new JPanel(new FlowLayout(FlowLayout.LEADING), true);
        JLabel label = new JLabel(string, JLabel.RIGHT);

        newComponent.add(component);
        newComponent.add( Box.createRigidArea( new Dimension(10, 10) ) );
        newComponent.add(label);

        return newComponent;
    }

    private void addTitledButtonBorder(TitledBorder border, JComponent component, String string, int justification, int position, Container container)
    {
        border.setTitleJustification(justification);
        border.setTitlePosition(position);

        addButtonBorder(border, component, string, container);
    }

    private void addButtonBorder(Border border, JComponent component, String string, Container container)
    {
        JPanel newComponent = new JPanel(new FlowLayout(FlowLayout.LEADING), true);
        JLabel label = new JLabel(string, JLabel.RIGHT);

        newComponent.add(component);
        newComponent.add( Box.createRigidArea( new Dimension(10, 10) ) );
        newComponent.add(label);
        newComponent.setBorder(border);

        container.add( Box.createRigidArea( new Dimension(0, 10) ) );
        container.add(newComponent);
    }

    private void addTitledButtonBorderLarge(TitledBorder border, JComponent component, int justification, int position, Container container)
    {
        border.setTitleJustification(justification);
        border.setTitlePosition(position);

        addButtonBorderLarge(border, component, container);
    }

    private void addButtonBorderLarge(Border border, JComponent component, Container container)
    {
        JPanel newComponent = new JPanel(new FlowLayout(FlowLayout.LEADING), true);
        newComponent.add(component);
        newComponent.setBorder(border);

        container.add( Box.createRigidArea( new Dimension(0, 10) ) );
        container.add(newComponent);
    }

    private void addPanelToGrid(String title, JComponent component, Container container,
            int x, int y, int xSpan, int ySpan)
    {
        TitledBorder border = BorderFactory.createTitledBorder(ETCHED, title);
        border.setTitleJustification(TitledBorder.DEFAULT_JUSTIFICATION);
        border.setTitlePosition(TitledBorder.DEFAULT_POSITION);

        JPanel newPanel = new JPanel(new FlowLayout(FlowLayout.LEADING), true);
        newPanel.add(component);
        newPanel.setBorder(border);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = xSpan;
        c.gridheight = ySpan;
        c.fill = GridBagConstraints.BOTH;

        container.add(newPanel, c);
    }

    private void addShaderColumnPanel(JComponent component, int index, boolean isLastShaderColumnPanel)
    {
        JPanel advancedShadersPanelTemp = new JPanel(true);
        advancedShadersPanelTemp.setLayout( new BoxLayout(advancedShadersPanelTemp, BoxLayout.Y_AXIS) );
        for (int j = 0; j < NUMBER_OF_SHADER_CHECKBOXES_PER_COLUMN; j++)
        {
            if ( (index + j) < allShadings.length )
            {
                advancedShadersPanelTemp.add(allShadings[index + j]);
                if ( j != (NUMBER_OF_SHADER_CHECKBOXES_PER_COLUMN - 1) ) advancedShadersPanelTemp.add( Box.createRigidArea( new Dimension(0, 15) ) );
            }
        }

        component.add(advancedShadersPanelTemp);
        if (!isLastShaderColumnPanel) component.add( Box.createRigidArea( new Dimension(80, 0) ) );
    }

    private JPanel createPropertiesPanel()
    {
        JPanel propertiesPanel = new JPanel(true);

        Border paneEdge = BorderFactory.createEmptyBorder(0, 10, 10, 10);

        propertiesPanel.setBorder(paneEdge);
        propertiesPanel.setLayout( new BoxLayout(propertiesPanel, BoxLayout.Y_AXIS) );
        propertiesPanel.setBorder(paneEdge);

        return propertiesPanel;
    }

    private void addCommandButtonsToTab(JPanel panel, int tabNumber)
    {
        Border paneEdge = BorderFactory.createEmptyBorder(0, 10, 10, 10);
        JButton okButton = new JButton("OK");
        okButton.setToolTipText("OK");
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setToolTipText("Cancel");
        JButton applyButton = new JButton("Apply");
        applyButton.setToolTipText("Apply");

        applyButton.setEnabled(false);
        allApplyButtons[tabNumber] = applyButton;

        okButton.addActionListener(this);
        applyButton.addActionListener(this);
        cancelButton.addActionListener(this);

        JPanel commandBorder = new JPanel(true);
        commandBorder.setBorder(paneEdge);
        commandBorder.setLayout( new BoxLayout(commandBorder, BoxLayout.X_AXIS) );

        commandBorder.setBorder(paneEdge);
        commandBorder.add(okButton);
        commandBorder.add(cancelButton);
        commandBorder.add(applyButton);

        panel.add( Box.createRigidArea( new Dimension(15, 15) ) );
        panel.add(commandBorder);
    }

    private void createGeneralPropertiesTab(JPanel panel, JTabbedPane tabbedPane, int tabNumber)
    {
        TitledBorder generalPropertiesPanelBorder = null;

        JPanel colorOptionsPanel = new JPanel(true);
        JPanel colorPanel1 = new JPanel(true);
        JPanel colorPanel2 = new JPanel(true);
        JPanel colorPanel3 = new JPanel(true);
        JPanel colorPanel4 = new JPanel(true);
        JPanel colorPanel5 = new JPanel(true);
        JPanel graphOptionsPanel = new JPanel(true);
        JPanel graphOptionsPanel1 = new JPanel(true);
        JPanel graphOptionsPanel2 = new JPanel(true);
        JPanel graphOptionsPanel3 = new JPanel(true);
        JPanel graphicsRenderingOptionsPanel = new JPanel(true);
        JPanel generalOptionsPanel = new JPanel(true);

        generalColor = new ColorButton(" ");
        generalColor.setActionCommand(CHANGE_ACTION_COMMAND);
        generalColor.addActionListener(this);
        generalColor.setPreferredSize( new Dimension(15, 15) );
        generalColor.setToolTipText("Background Color");
        generalColorSelection = new ColorButton(" ");
        generalColorSelection.setActionCommand(CHANGE_ACTION_COMMAND_NODES);
        generalColorSelection.addActionListener(this);
        generalColorSelection.setPreferredSize( new Dimension(15, 15) );
        generalColorSelection.setToolTipText("Selection Color");
        generalColorPlotBackground = new ColorButton(" ");
        generalColorPlotBackground.setActionCommand(CHANGE_ACTION_COMMAND);
        generalColorPlotBackground.addActionListener(this);
        generalColorPlotBackground.setPreferredSize( new Dimension(15, 15) );
        generalColorPlotBackground.setToolTipText("Plot Background Color");
        generalColorPlotGridlines = new ColorButton(" ");
        generalColorPlotGridlines.setActionCommand(CHANGE_ACTION_COMMAND);
        generalColorPlotGridlines.addActionListener(this);
        generalColorPlotGridlines.setPreferredSize( new Dimension(15, 15) );
        generalColorPlotGridlines.setToolTipText("Plot Grid Lines Color");
        generalTrippyBackground = new JCheckBox("Trippy Background");
        generalTrippyBackground.setActionCommand(CHANGE_ACTION_COMMAND);
        generalTrippyBackground.addActionListener(this);
        generalTrippyBackground.setToolTipText("Trippy Background");

        generalDisableNodesRendering = new JCheckBox("Disable Nodes Rendering For Graph");
        generalDisableNodesRendering.setActionCommand(CHANGE_ACTION_COMMAND_NODES);
        generalDisableNodesRendering.addActionListener(this);
        generalDisableNodesRendering.setToolTipText("Disable Nodes Rendering For Graph (useful for very large graphs)");
        generalDisableEdgesRendering = new JCheckBox("Disable Edges Rendering For Graph");
        generalDisableEdgesRendering.setActionCommand(CHANGE_ACTION_COMMAND_EDGES);
        generalDisableEdgesRendering.addActionListener(this);
        generalDisableEdgesRendering.setToolTipText("Disable Edges Rendering For Graph (useful for very large graphs)");
        generalDirectional = new JCheckBox("Directional Edges (2D graphs only)");
        generalDirectional.setActionCommand(CHANGE_ACTION_COMMAND_EDGES);
        generalDirectional.addActionListener(this);
        generalDirectional.setToolTipText("Directional Edges (2D graphs only)");
        generalDragShowEdgesWhileDraggingNodes = new JCheckBox("Show Edges when Dragging Nodes (2D graphs only)");
        generalDragShowEdgesWhileDraggingNodes.setActionCommand(CHANGE_ACTION_COMMAND);
        generalDragShowEdgesWhileDraggingNodes.addActionListener(this);
        generalDragShowEdgesWhileDraggingNodes.setToolTipText("Show Edges when Dragging Nodes (2D graphs only)");
        generalyEdStyleRenderingForGraphmlFiles = new JCheckBox("yEd-style Rendering for Graphml files");
        generalyEdStyleRenderingForGraphmlFiles.setActionCommand(CHANGE_ACTION_COMMAND_ALL);
        generalyEdStyleRenderingForGraphmlFiles.addActionListener(this);
        generalyEdStyleRenderingForGraphmlFiles.setEnabled(false);
        generalyEdStyleRenderingForGraphmlFiles.setToolTipText("yEd-style Rendering for Graphml files");
        generalyEdStyleComponentContainersRenderingForGraphmlFiles = new JCheckBox("yEd-style Component Containers Rendering for Graphml files");
        generalyEdStyleComponentContainersRenderingForGraphmlFiles.setActionCommand(CHANGE_ACTION_COMMAND);
        generalyEdStyleComponentContainersRenderingForGraphmlFiles.addActionListener(this);
        generalyEdStyleComponentContainersRenderingForGraphmlFiles.setEnabled(false);
        generalyEdStyleComponentContainersRenderingForGraphmlFiles.setToolTipText("yEd-style Component Containers Rendering for Graphml files");

        generalHighQualityAntiAlias = new JRadioButton("Texture MipMapping & High Quality FullScreen Anti-Aliasing (FSAA 4x)");
        generalHighQualityAntiAlias.setActionCommand(CHANGE_ACTION_COMMAND);
        generalHighQualityAntiAlias.addActionListener(this);
        generalHighQualityAntiAlias.setToolTipText("Texture MipMapping & High Quality FullScreen Anti-Aliasing (FSAA 4x)");
        generalNormalQualityAntiAlias = new JRadioButton("Texture MipMapping & Normal Quality FullScreen Anti-Aliasing (FSAA 2x)");
        generalNormalQualityAntiAlias.setActionCommand(CHANGE_ACTION_COMMAND);
        generalNormalQualityAntiAlias.addActionListener(this);
        generalNormalQualityAntiAlias.setToolTipText("Texture MipMapping & Normal Quality FullScreen Anti-Aliasing (FSAA 2x)");
        generalNoAntiAlias = new JRadioButton("No Texture MipMapping & No FullScreen Anti-Aliasing");
        generalNoAntiAlias.setActionCommand(CHANGE_ACTION_COMMAND);
        generalNoAntiAlias.addActionListener(this);
        generalNoAntiAlias.setToolTipText("No Texture MipMapping & No FullScreen Anti-Aliasing");
        useVSynch = new JCheckBox("Use VSynch for rendering");
        useVSynch.setActionCommand(CHANGE_ACTION_COMMAND);
        useVSynch.addActionListener(this);
        useVSynch.setToolTipText("Use VSynch for rendering");

        generalUseInstallDirForScreenshots = new JCheckBox("Use application's install directory for Render Image To File screenshots");
        generalUseInstallDirForScreenshots.setActionCommand(CHANGE_ACTION_COMMAND);
        generalUseInstallDirForScreenshots.addActionListener(this);
        generalUseInstallDirForScreenshots.setToolTipText("Use application's install directory for Render Image To File screenshots");
        generalUseInstallDirForMCLTempFile = new JCheckBox("Use application's install directory for MCL temporary file (in MCL folder)");
        generalUseInstallDirForMCLTempFile.setActionCommand(CHANGE_ACTION_COMMAND);
        generalUseInstallDirForMCLTempFile.addActionListener(this);
        generalUseInstallDirForMCLTempFile.setToolTipText("Use application's install directory for MCL temporary file (in MCL folder)");
        generalShowGraphPropertiesToolBar = new JCheckBox("Show Graph Properties ToolBar");
        generalShowGraphPropertiesToolBar.setActionCommand(CHANGE_ACTION_COMMAND);
        generalShowGraphPropertiesToolBar.addActionListener(this);
        generalShowGraphPropertiesToolBar.setToolTipText("Show Graph Properties ToolBar");
        generalShowNavigationToolBar = new JCheckBox("Show Navigation ToolBar");
        generalShowNavigationToolBar.setActionCommand(CHANGE_ACTION_COMMAND);
        generalShowNavigationToolBar.addActionListener(this);
        generalShowNavigationToolBar.setToolTipText("Show Navigation ToolBar");
        generalShowPopupOverlayPlot = new JCheckBox("Show Popup Overlay Plot");
        generalShowPopupOverlayPlot.setActionCommand(CHANGE_ACTION_COMMAND);
        generalShowPopupOverlayPlot.addActionListener(this);
        generalShowPopupOverlayPlot.setToolTipText("Show Popup Overlay Plot");
        generalShowNavigationWizardOnStartup = new JCheckBox("Show Navigation Wizard On Startup");
        generalShowNavigationWizardOnStartup.setActionCommand(CHANGE_ACTION_COMMAND);
        generalShowNavigationWizardOnStartup.addActionListener(this);
        generalShowNavigationWizardOnStartup.setToolTipText("Show Navigation Wizard On Startup");
        generalShowLayoutIterations = new JCheckBox("Show Layout Iterations while loading graph");
        generalShowLayoutIterations.setActionCommand(CHANGE_ACTION_COMMAND);
        generalShowLayoutIterations.addActionListener(this);
        generalShowLayoutIterations.setToolTipText("Show Layout Iterations while loading graph");
        generalValidateXMLFiles = new JCheckBox("Validate XML files when loading");
        generalValidateXMLFiles.setActionCommand(CHANGE_ACTION_COMMAND);
        generalValidateXMLFiles.addActionListener(this);
        generalValidateXMLFiles.setToolTipText("Validate XML files when loading");
        generalCollapseNodesByVolume = new JCheckBox("Collapse Nodes By Volume");
        generalCollapseNodesByVolume.setActionCommand(CHANGE_ACTION_COMMAND);
        generalCollapseNodesByVolume.addActionListener(this);
        generalCollapseNodesByVolume.setToolTipText("Collapse Nodes By Volume");
        generalConfirmPreferencesSave = new JCheckBox("Confirm Preferences Save On Exit");
        generalConfirmPreferencesSave.setActionCommand(CHANGE_ACTION_COMMAND);
        generalConfirmPreferencesSave.addActionListener(this);
        generalConfirmPreferencesSave.setToolTipText("Confirm Preferences Save On Exit");

        Border paneEdge = BorderFactory.createEmptyBorder(0, 10, 10, 10);

        ButtonGroup antiAliasGroup = new ButtonGroup();
        antiAliasGroup.add(generalHighQualityAntiAlias);
        antiAliasGroup.add(generalNormalQualityAntiAlias);
        antiAliasGroup.add(generalNoAntiAlias);

        colorPanel2.add(generalColor);
        colorPanel2.add( new JLabel("Background Color") );
        colorPanel3.add(generalColorSelection);
        colorPanel3.add( new JLabel("Selection Color") );
        colorPanel4.add(generalColorPlotBackground);
        colorPanel4.add( new JLabel("Plot Background Color") );
        colorPanel5.add(generalColorPlotGridlines);
        colorPanel5.add( new JLabel("Plot Grid Lines Color") );

        colorOptionsPanel.setBorder(paneEdge);
        colorOptionsPanel.setLayout( new BoxLayout(colorOptionsPanel, BoxLayout.X_AXIS) );
        colorOptionsPanel.add( Box.createRigidArea( new Dimension(10, 10) ) );
        colorOptionsPanel.add(colorPanel1);
        colorOptionsPanel.add( Box.createRigidArea( new Dimension(15, 15) ) );
        colorOptionsPanel.add(colorPanel2);
        colorOptionsPanel.add( Box.createRigidArea( new Dimension(15, 15) ) );
        colorOptionsPanel.add(colorPanel3);
        colorOptionsPanel.add( Box.createRigidArea( new Dimension(15, 15) ) );
        colorOptionsPanel.add(colorPanel4);
        colorOptionsPanel.add( Box.createRigidArea( new Dimension(15, 15) ) );
        colorOptionsPanel.add(colorPanel5);
        colorOptionsPanel.add( Box.createRigidArea( new Dimension(15, 15) ) );
        colorOptionsPanel.add(generalTrippyBackground);

        graphOptionsPanel1.setLayout( new BoxLayout(graphOptionsPanel1, BoxLayout.Y_AXIS) );
        graphOptionsPanel1.add(generalDisableNodesRendering);
        graphOptionsPanel1.add( Box.createRigidArea( new Dimension(10, 10) ) );
        graphOptionsPanel1.add(generalDisableEdgesRendering);

        graphOptionsPanel2.setLayout( new BoxLayout(graphOptionsPanel2, BoxLayout.Y_AXIS) );
        graphOptionsPanel2.add(generalDirectional);
        graphOptionsPanel2.add( Box.createRigidArea( new Dimension(10, 10) ) );
        graphOptionsPanel2.add(generalDragShowEdgesWhileDraggingNodes);

        graphOptionsPanel3.setLayout( new BoxLayout(graphOptionsPanel3, BoxLayout.Y_AXIS) );
        graphOptionsPanel3.add(generalyEdStyleRenderingForGraphmlFiles);
        graphOptionsPanel3.add( Box.createRigidArea( new Dimension(10, 10) ) );
        graphOptionsPanel3.add(generalyEdStyleComponentContainersRenderingForGraphmlFiles);

        graphOptionsPanel.setBorder(paneEdge);
        graphOptionsPanel.setLayout( new BoxLayout(graphOptionsPanel, BoxLayout.X_AXIS) );
        graphOptionsPanel.add(graphOptionsPanel1);
        graphOptionsPanel.add( Box.createRigidArea( new Dimension(5, 5) ) );
        graphOptionsPanel.add(graphOptionsPanel2);
        graphOptionsPanel.add( Box.createRigidArea( new Dimension(5, 5) ) );
        graphOptionsPanel.add(graphOptionsPanel3);

        graphicsRenderingOptionsPanel.setBorder(paneEdge);
        graphicsRenderingOptionsPanel.setLayout( new BoxLayout(graphicsRenderingOptionsPanel, BoxLayout.Y_AXIS) );
        graphicsRenderingOptionsPanel.add( Box.createRigidArea( new Dimension(10, 10) ) );
        graphicsRenderingOptionsPanel.add(generalHighQualityAntiAlias);
        graphicsRenderingOptionsPanel.add( Box.createRigidArea( new Dimension(4, 4) ) );
        graphicsRenderingOptionsPanel.add(generalNormalQualityAntiAlias);
        graphicsRenderingOptionsPanel.add( Box.createRigidArea( new Dimension(4, 4) ) );
        graphicsRenderingOptionsPanel.add(generalNoAntiAlias);
        graphicsRenderingOptionsPanel.add( Box.createRigidArea( new Dimension(10, 10) ) );
        graphicsRenderingOptionsPanel.add(useVSynch);

        generalOptionsPanel.setBorder(paneEdge);
        generalOptionsPanel.setLayout( new BoxLayout(generalOptionsPanel, BoxLayout.Y_AXIS) );

        JPanel generalOptionsTopPanel = new JPanel(true);
        generalOptionsTopPanel.setLayout( new BoxLayout(generalOptionsTopPanel, BoxLayout.X_AXIS) );

        JPanel generalOptionsPanel1 = new JPanel(true);
        generalOptionsPanel1.setLayout( new BoxLayout(generalOptionsPanel1, BoxLayout.Y_AXIS) );
        generalOptionsPanel1.add( Box.createRigidArea( new Dimension(10, 10) ) );
        generalOptionsPanel1.add(generalUseInstallDirForScreenshots);
        generalOptionsPanel1.add( Box.createRigidArea( new Dimension(10, 10) ) );
        generalOptionsPanel1.add(generalUseInstallDirForMCLTempFile);
        generalOptionsPanel1.add( Box.createRigidArea( new Dimension(10, 10) ) );
        generalOptionsPanel1.add(generalShowGraphPropertiesToolBar);
        generalOptionsPanel1.add( Box.createRigidArea( new Dimension(10, 10) ) );
        generalOptionsPanel1.add(generalShowNavigationToolBar);
        generalOptionsPanel1.add( Box.createRigidArea( new Dimension(10, 10) ) );
        generalOptionsPanel1.add(generalShowPopupOverlayPlot);
        generalOptionsPanel1.add( Box.createRigidArea( new Dimension(10, 10) ) );
        generalOptionsPanel1.add(generalConfirmPreferencesSave);

        JPanel generalOptionsPanel2 = new JPanel(true);
        generalOptionsPanel2.setLayout( new BoxLayout(generalOptionsPanel2, BoxLayout.Y_AXIS) );
        generalOptionsPanel2.add( Box.createRigidArea( new Dimension(10, 10) ) );
        generalOptionsPanel2.add(generalShowNavigationWizardOnStartup);
        generalOptionsPanel2.add( Box.createRigidArea( new Dimension(10, 10) ) );
        generalOptionsPanel2.add(generalShowLayoutIterations);
        generalOptionsPanel2.add( Box.createRigidArea( new Dimension(10, 10) ) );
        generalOptionsPanel2.add(generalValidateXMLFiles);
        generalOptionsPanel2.add( Box.createRigidArea( new Dimension(10, 10) ) );
        generalOptionsPanel2.add(generalCollapseNodesByVolume);

        generalOptionsTopPanel.add(generalOptionsPanel1);
        generalOptionsTopPanel.add( Box.createRigidArea( new Dimension(15, 15) ) );
        generalOptionsTopPanel.add(generalOptionsPanel2);

        JPanel generalOptionsBottomPanel = new JPanel(true);
        generalOptionsPanel.add(generalOptionsTopPanel);
        generalOptionsPanel.add( Box.createRigidArea( new Dimension(15, 15) ) );
        generalOptionsPanel.add(generalOptionsBottomPanel);

        generalPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "Background Color Options");
        addTitledButtonBorder(generalPropertiesPanelBorder, colorOptionsPanel, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, panel);

        generalPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "General Graph Options");
        addTitledButtonBorder(generalPropertiesPanelBorder, graphOptionsPanel, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, panel);

        generalPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "General Graphics Quality Rendering Options  (Texture MipMapping & FSAA options below need to Save Preferences & restart the application to apply changes)");
        addTitledButtonBorder(generalPropertiesPanelBorder, graphicsRenderingOptionsPanel, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, panel);

        generalPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "General Options");
        addTitledButtonBorder(generalPropertiesPanelBorder, generalOptionsPanel, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, panel);

        addCommandButtonsToTab(panel, tabNumber);
        tabbedPane.addTab("General", null, panel, "General Properties");
    }

    private void createLayoutPropertiesTab(JPanel panel, JTabbedPane tabbedPane, int tabNumber)
    {
        TitledBorder layoutPropertiesPanelBorder = null;

        layoutUseEdgeWeightsForLayout = new JCheckBox("Use Edge Weights For Layout");
        layoutUseEdgeWeightsForLayout.setToolTipText("Use Edge Weights For Layout");
        layoutTiledLayout = new JCheckBox("Tiled Layout");
        layoutTiledLayout.setToolTipText("Tiled Layout");
        layoutUseEdgeWeightsForLayout.addActionListener(this);
        layoutUseEdgeWeightsForLayout.setActionCommand(CHANGE_ACTION_COMMAND);
        layoutTiledLayout.addActionListener(this);
        layoutTiledLayout.setActionCommand(CHANGE_ACTION_COMMAND);

        layoutStartingTemperatureField = new FloatNumberField(0, 10);
        layoutStartingTemperatureField.addCaretListener(this);
        layoutStartingTemperatureField.setToolTipText("Starting Temperature");
        layoutIterationsField = new JTextField("", 10);
        layoutIterationsField.addCaretListener(this);
        layoutIterationsField.setToolTipText("Number Of Layout Iterations");
        layoutKvalueField = new FloatNumberField(0, 10);
        layoutKvalueField.addCaretListener(this);
        layoutKvalueField.setToolTipText("K-Value Modifier");
        layoutBurstIterationsField = new JTextField("", 10);
        layoutBurstIterationsField.addCaretListener(this);
        layoutBurstIterationsField.setToolTipText("Burst Layout Iterations");

        layoutStartingTemperatureField.setDocument( new TextFieldFilter(TextFieldFilter.FLOAT) );
        layoutIterationsField.setDocument( new TextFieldFilter(TextFieldFilter.NUMERIC) );
        layoutKvalueField.setDocument( new TextFieldFilter(TextFieldFilter.FLOAT) );
        layoutBurstIterationsField.setDocument( new TextFieldFilter(TextFieldFilter.NUMERIC) );

        // Algorithm selection
        JPanel algorithmPanel = new JPanel(true);
        algorithmPanel.setLayout(new BoxLayout(algorithmPanel, BoxLayout.X_AXIS));

        ButtonGroup layoutAlgorithmGroup = new ButtonGroup();

        frRadioButton = new JRadioButton("Fruchterman-Rheingold");
        frRadioButton.setToolTipText("Fruchterman-Rheingold");
        layoutAlgorithmGroup.add(frRadioButton);
        algorithmPanel.add(frRadioButton);

        fmmmRadioButton = new JRadioButton("FMMM");
        fmmmRadioButton.setToolTipText("FMMM");
        layoutAlgorithmGroup.add(fmmmRadioButton);
        algorithmPanel.add(fmmmRadioButton);

        circleRadioButton = new JRadioButton("Circle");
        circleRadioButton.setToolTipText("Circle");
        layoutAlgorithmGroup.add(circleRadioButton);
        algorithmPanel.add(circleRadioButton);

        askRadioButton = new JRadioButton("Always Ask");
        askRadioButton.setToolTipText("Always Ask");
        layoutAlgorithmGroup.add(askRadioButton);
        algorithmPanel.add(askRadioButton);

        // Minimum Component Size
        JPanel minimumComponentSizePanel = new JPanel(true);
        algorithmPanel.setLayout(new BoxLayout(algorithmPanel, BoxLayout.X_AXIS));
        layoutMinimumComponentSizeField = new JTextField("", 10);
        layoutMinimumComponentSizeField.addCaretListener(this);
        layoutMinimumComponentSizeField.setToolTipText("Minimum Component Size");
        layoutMinimumComponentSizeField.setDocument( new TextFieldFilter(TextFieldFilter.NUMERIC) );
        minimumComponentSizePanel.add(layoutMinimumComponentSizeField);

        // FR options
        JPanel fruchtermanRheingoldPanel = new JPanel(true);
        fruchtermanRheingoldPanel.setLayout( new BoxLayout(fruchtermanRheingoldPanel, BoxLayout.Y_AXIS) );

        layoutPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "Use Edge Weights For Layout");
        addTitledButtonBorder(layoutPropertiesPanelBorder, layoutUseEdgeWeightsForLayout, "             (e.g. ON)", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, fruchtermanRheingoldPanel);

        layoutPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "Tiled Layout");
        addTitledButtonBorder(layoutPropertiesPanelBorder, layoutTiledLayout, "            (e.g. ON)", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, fruchtermanRheingoldPanel);

        layoutPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "Starting Temperature");
        addTitledButtonBorder(layoutPropertiesPanelBorder, layoutStartingTemperatureField, "           (e.g. 100.0)", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, fruchtermanRheingoldPanel);

        layoutPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "Number Of Layout Iterations");
        addTitledButtonBorder(layoutPropertiesPanelBorder, layoutIterationsField, "           (e.g. 100)", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, fruchtermanRheingoldPanel);

        layoutPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "K-Value Modifier");
        addTitledButtonBorder(layoutPropertiesPanelBorder, layoutKvalueField, "          (e.g. 1.0)", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, fruchtermanRheingoldPanel);

        layoutPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "Burst Layout Iterations");
        addTitledButtonBorder(layoutPropertiesPanelBorder, layoutBurstIterationsField, "           (e.g. 20)", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, fruchtermanRheingoldPanel);

        // FMMM options
        JPanel fmmmPanel = new JPanel(true);
        fmmmPanel.setLayout(new BoxLayout(fmmmPanel, BoxLayout.Y_AXIS));

        // FMMM edge length
        fmmmDesiredEdgeLength = new FloatNumberField(20.0f, 10);
        fmmmDesiredEdgeLength.addCaretListener(this);
        fmmmDesiredEdgeLength.setToolTipText("Desired Edge Length");
        fmmmDesiredEdgeLength.setDocument( new TextFieldFilter(TextFieldFilter.FLOAT) );
        layoutPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "Desired Edge Length");
        addTitledButtonBorder(layoutPropertiesPanelBorder, fmmmDesiredEdgeLength, "",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, fmmmPanel);

        // FMMM force model
        fmmmForceModel = new JComboBox<String>();
        for (FmmmForceModel fm : FmmmForceModel.values())
        {
            String s = Utils.titleCaseOf(fm.toString());
            fmmmForceModel.addItem(s);
        }
        fmmmForceModel.addActionListener(this);
        fmmmForceModel.setActionCommand(CHANGE_ACTION_COMMAND);
        fmmmForceModel.setToolTipText("Force Model");
        layoutPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "Force Model");
        addTitledButtonBorder(layoutPropertiesPanelBorder, fmmmForceModel, "",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, fmmmPanel);

        // FMMM quality
        fmmmQualityVsSpeed = new JComboBox<String>();
        for (FmmmQualityVsSpeed qvs : FmmmQualityVsSpeed.values())
        {
            String s = Utils.titleCaseOf(qvs.toString());
            fmmmQualityVsSpeed.addItem(s);
        }
        fmmmQualityVsSpeed.addActionListener(this);
        fmmmQualityVsSpeed.setActionCommand(CHANGE_ACTION_COMMAND);
        fmmmQualityVsSpeed.setToolTipText("Quality vs. Speed");
        layoutPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "Quality vs. Speed");
        addTitledButtonBorder(layoutPropertiesPanelBorder, fmmmQualityVsSpeed, "",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, fmmmPanel);

        // FMMM stop criterion
        fmmmStopCriterion = new JComboBox<String>();
        for (FmmmStopCriterion sc : FmmmStopCriterion.values())
        {
            String s = Utils.titleCaseOf(sc.toString());
            fmmmStopCriterion.addItem(s);
        }
        fmmmStopCriterion.addActionListener(this);
        fmmmStopCriterion.setActionCommand(CHANGE_ACTION_COMMAND);
        fmmmStopCriterion.setToolTipText("Stop Criterion");
        layoutPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "Stop Criterion");
        addTitledButtonBorder(layoutPropertiesPanelBorder, fmmmStopCriterion, "",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, fmmmPanel);

        fmmmIterationLevelFactor = new FloatNumberField(10.0f, 10);
        fmmmIterationLevelFactor.addCaretListener(this);
        fmmmIterationLevelFactor.setToolTipText("Iteration Level Factor");
        fmmmIterationLevelFactor.setDocument( new TextFieldFilter(TextFieldFilter.NUMERIC) );
        layoutPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "Iteration Level Factor");
        addTitledButtonBorder(layoutPropertiesPanelBorder, fmmmIterationLevelFactor, "",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, fmmmPanel);

        // Layout algorithm option panels
        JPanel layoutLargePanel = new JPanel(true);
        layoutLargePanel.setLayout( new GridBagLayout() );

        addPanelToGrid("Algorithm", algorithmPanel, layoutLargePanel, 0, 0, 1, 1);
        addPanelToGrid("Minimum Component Size", minimumComponentSizePanel, layoutLargePanel, 1, 0, 1, 1);
        addPanelToGrid("Fruchterman-Rheingold", fruchtermanRheingoldPanel, layoutLargePanel, 0, 1, 1, 1);
        addPanelToGrid("FMMM", fmmmPanel, layoutLargePanel, 1, 1, 1, 1);

        layoutPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "Layout Options");
        addTitledButtonBorderLarge(layoutPropertiesPanelBorder, layoutLargePanel,
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, panel);

        addCommandButtonsToTab(panel, tabNumber);
        tabbedPane.addTab("Layout", null, panel, "Layout Properties");
    }

    private void createRenderingPropertiesTab(JPanel panel, JTabbedPane tabbedPane, int tabNumber)
    {
        TitledBorder renderingPropertiesPanelBorder = null;

        JPanel nodeOptionsPanel = new JPanel(true);

        _3DNodeTesselationSlider = new SimpleSlider(7, 140, 75, 0.0f, NODE_TESSELATION_MIN_VALUE, NODE_TESSELATION_MAX_VALUE, NODE_TESSELATION_MAX_VALUE - NODE_TESSELATION_MIN_VALUE, "3D Nodes Tesselation", true);
        _3DNodeTesselationSlider.setActionCommand(CHANGE_ACTION_COMMAND_NODES);
        _3DNodeTesselationSlider.addActionListener(this);
        _3DNodeTesselationSlider.setToolTipText("3D Nodes Tesselation");

        highResImageRenderScaleSlider = new SimpleSlider(3, 170, 75, 0.0f, 1.0f, 20.0f, 9, "High Res Graph Image Scale", true);
        highResImageRenderScaleSlider.setActionCommand(CHANGE_ACTION_COMMAND);
        highResImageRenderScaleSlider.addActionListener(this);
        highResImageRenderScaleSlider.setToolTipText("High Res Graph Image Scale");

        showNodes = new JCheckBox("Show Nodes During Graph Navigation");
        showNodes.setActionCommand(CHANGE_ACTION_COMMAND);
        showNodes.addActionListener(this);
        showNodes.setToolTipText("Show Nodes During Graph Navigation");
        show3DFrustum = new JCheckBox("Show 3D Frustum");
        show3DFrustum.setActionCommand(CHANGE_ACTION_COMMAND);
        show3DFrustum.addActionListener(this);
        show3DFrustum.setToolTipText("Show 3D Frustum");
        show3DShadows = new JCheckBox("Show 3D Node Projection Shadows");
        show3DShadows.setActionCommand(CHANGE_ACTION_COMMAND);
        show3DShadows.addActionListener(this);
        show3DShadows.setToolTipText("Show 3D Node Projection Shadows");
        show3DEnvironmentMapping = new JCheckBox("Show 3D Node Environment Mapping");
        show3DEnvironmentMapping.setActionCommand(CHANGE_ACTION_COMMAND_NODES);
        show3DEnvironmentMapping.addActionListener(this);
        show3DEnvironmentMapping.setToolTipText("Show 3D Node Environment Mapping");
        fastSelectionMode = new JCheckBox("Fast Selection Mode");
        fastSelectionMode.setActionCommand(CHANGE_ACTION_COMMAND_NODES);
        fastSelectionMode.addActionListener(this);
        fastSelectionMode.setToolTipText("Fast Selection Mode (performs better on newer graphics hardware)");
        wireframeSelectionMode = new JCheckBox("Wireframe Selection Mode");
        wireframeSelectionMode.setActionCommand(CHANGE_ACTION_COMMAND_NODES);
        wireframeSelectionMode.addActionListener(this);
        wireframeSelectionMode.setToolTipText("Wireframe Selection Mode");
        advancedKeyboardRenderingControl = new JCheckBox("Advanced Keyboard Rendering Control");
        advancedKeyboardRenderingControl.setActionCommand(CHANGE_ACTION_COMMAND);
        advancedKeyboardRenderingControl.addActionListener(this);
        advancedKeyboardRenderingControl.setToolTipText("Advanced Keyboard Rendering Control");
        anaglyphStereoscopic3DView = new JCheckBox("Anaglyph Stereoscopic 3D View");
        anaglyphStereoscopic3DView.setActionCommand(CHANGE_ACTION_COMMAND_ALL);
        anaglyphStereoscopic3DView.addActionListener(this);
        anaglyphStereoscopic3DView.setToolTipText("Anaglyph Stereoscopic 3D View");
        anaglyphStereoscopic3DViewOptions = new JButton( graphAnaglyphGlasses3DOptionsDialog.getGraphAnaglyphGlasses3DOptionsOpenDialogAction() );
        anaglyphStereoscopic3DViewOptions.setToolTipText("Anaglyph Stereoscopic 3D View Options");
        graphAnaglyphGlasses3DOptionsDialog.getGraphAnaglyphGlasses3DOptionsOpenDialogAction().setEnabled(false);
        anaglyphStereoscopic3DViewOptions.setEnabled(false);

        JPanel showOptionsPanel = new JPanel(true);
        showOptionsPanel.setLayout( new BoxLayout(showOptionsPanel, BoxLayout.Y_AXIS) );
        showOptionsPanel.add(showNodes);
        showOptionsPanel.add(show3DFrustum);
        showOptionsPanel.add(show3DShadows);
        showOptionsPanel.add(show3DEnvironmentMapping);

        JPanel anaglyphStereoscopicPanel = new JPanel(true);
        anaglyphStereoscopicPanel.setLayout( new BoxLayout(anaglyphStereoscopicPanel, BoxLayout.X_AXIS) );
        anaglyphStereoscopicPanel.add(anaglyphStereoscopic3DView);
        anaglyphStereoscopicPanel.add( Box.createRigidArea( new Dimension(3, 3) ) );
        anaglyphStereoscopicPanel.add(anaglyphStereoscopic3DViewOptions);

        JPanel set2OptionsPanel = new JPanel(true);
        set2OptionsPanel.setLayout( new GridLayout(4, 1) );
        set2OptionsPanel.add(fastSelectionMode);
        set2OptionsPanel.add(wireframeSelectionMode);
        set2OptionsPanel.add(advancedKeyboardRenderingControl);
        set2OptionsPanel.add(anaglyphStereoscopicPanel);

        nodeOptionsPanel.add(_3DNodeTesselationSlider);
        nodeOptionsPanel.add( Box.createRigidArea( new Dimension(3, 3) ) );
        nodeOptionsPanel.add(showOptionsPanel);
        nodeOptionsPanel.add( Box.createRigidArea( new Dimension(3, 3) ) );
        nodeOptionsPanel.add(highResImageRenderScaleSlider);
        nodeOptionsPanel.add( Box.createRigidArea( new Dimension(3, 3) ) );
        nodeOptionsPanel.add(set2OptionsPanel);

        JPanel lightingFogMotionBlurSetup = new JPanel(true);

        lightingPositionXSlider = new SimpleSlider(7, 90, 75, 0.0f, -10.0f, 10.0f, 20, "Light Position X", false);
        lightingPositionYSlider = new SimpleSlider(7, 90, 75, 0.0f, -10.0f, 10.0f, 20, "Light Position Y", false);
        lightingPositionZSlider = new SimpleSlider(7, 90, 75, 0.0f, -10.0f, 10.0f, 20, "Light Position Z", false);

        lightingPositionXSlider.setActionCommand(CHANGE_ACTION_COMMAND);
        lightingPositionYSlider.setActionCommand(CHANGE_ACTION_COMMAND);
        lightingPositionZSlider.setActionCommand(CHANGE_ACTION_COMMAND);

        lightingPositionXSlider.addActionListener(this);
        lightingPositionYSlider.addActionListener(this);
        lightingPositionZSlider.addActionListener(this);

        lightingPositionXSlider.setToolTipText("Light Position X");
        lightingPositionYSlider.setToolTipText("Light Position Y");
        lightingPositionZSlider.setToolTipText("Light Position Z");

        depthFog = new JCheckBox("Depth Fog (Per-Pixel with Advanced Shaders)");
        depthFog.setActionCommand(CHANGE_ACTION_COMMAND_ALL);
        depthFog.addActionListener(this);
        depthFog.setToolTipText("Depth Fog (Per-Pixel with Advanced Shaders)");

        useMotionBlurForScene = new JCheckBox("Use Motion Blur For Scene");
        useMotionBlurForScene.setActionCommand(CHANGE_ACTION_COMMAND_ALL);
        useMotionBlurForScene.addActionListener(this);
        useMotionBlurForScene.setToolTipText("Use Motion Blur For Scene (Available only for non-ATI GPUs)");

        motionBlurSize = new SimpleSlider(7, 100, 75, 0.0f, 0.0f, 100.0f, 100, "Motion Blur Size", true);
        motionBlurSize.setActionCommand(CHANGE_ACTION_COMMAND_ALL);
        motionBlurSize.addActionListener(this);
        motionBlurSize.setToolTipText("Motion Blur Size (Available only for non-ATI GPUs)");

        JPanel motionBlurPanel = new JPanel(true);
        motionBlurPanel.setLayout( new GridLayout(4, 1) );
        motionBlurPanel.add(useMotionBlurForScene);
        motionBlurPanel.add(motionBlurSize);

        lightingFogMotionBlurSetup.add(lightingPositionXSlider);
        lightingFogMotionBlurSetup.add(lightingPositionYSlider);
        lightingFogMotionBlurSetup.add(lightingPositionZSlider);
        lightingFogMotionBlurSetup.add( Box.createRigidArea( new Dimension(10, 15) ) );
        lightingFogMotionBlurSetup.add(depthFog);
        lightingFogMotionBlurSetup.add( Box.createRigidArea( new Dimension(10, 15) ) );
        lightingFogMotionBlurSetup.add(useMotionBlurForScene);
        lightingFogMotionBlurSetup.add(motionBlurSize);

        JPanel materialOptionsAndShadingPanel = new JPanel(true);

        materialSpecular = new JCheckBox("Specular");
        materialSpecular.setActionCommand(CHANGE_ACTION_COMMAND_NODES);
        materialSpecular.addActionListener(this);
        materialSpecular.setToolTipText("Specular");

        materialShininess = new SimpleSlider(7, 90, 75, 0.0f, 0.0f, 100.0f, 100, "Shininess", true);
        materialShininess.setActionCommand(CHANGE_ACTION_COMMAND_NODES);
        materialShininess.addActionListener(this);
        materialShininess.setToolTipText("Shininess");

        materialGouraudLighting = new JCheckBox("Gouraud (Per-Vertex) Smooth Shading");
        materialGouraudLighting.setActionCommand(CHANGE_ACTION_COMMAND_NODES);
        materialGouraudLighting.addActionListener(this);
        materialGouraudLighting.setToolTipText("Gouraud (Per-Vertex) Smooth Shading");

        materialSphericalMapping = new JCheckBox("Spherical Mapping");
        materialSphericalMapping.setActionCommand(CHANGE_ACTION_COMMAND_NODES);
        materialSphericalMapping.addActionListener(this);
        materialSphericalMapping.setToolTipText("Spherical Mapping");

        materialEmbossNodeTexture = new JCheckBox("Emboss Node Texture");
        materialEmbossNodeTexture.setActionCommand(CHANGE_ACTION_COMMAND);
        materialEmbossNodeTexture.addActionListener(this);
        materialEmbossNodeTexture.setToolTipText("Emboss Node Texture");

        materialAntiAliasShading = new JCheckBox("AntiAlias Shading");
        materialAntiAliasShading.setActionCommand(CHANGE_ACTION_COMMAND);
        materialAntiAliasShading.addActionListener(this);
        materialAntiAliasShading.setToolTipText("AntiAlias Shading (For Toon, Gooch & Brick Shaders)");

        materialAnimatedShading = new JCheckBox("Animated Shading");
        materialAnimatedShading.setActionCommand(CHANGE_ACTION_COMMAND);
        materialAnimatedShading.addActionListener(this);
        materialAnimatedShading.setToolTipText("Animated Shading (For Bump3D, Cloud, Lava, Marble, Granite, Wood, Hatching, Glyphbombing, Water3D, Voronoi & Fractal Shaders)");

        materialStateShading = new JCheckBox("Alternate Shader Rendering");
        materialStateShading.setActionCommand(CHANGE_ACTION_COMMAND);
        materialStateShading.addActionListener(this);
        materialStateShading.setToolTipText("Alternate Shader Rendering (For all shaders using the Oren-Nayar diffure model, Bump3D/Water3D Second Light Source, Toon/Gooch Colored Silhouette, Random Rotate Glyphbombing, Voronoi Blobs & Fractal Mandelbrot/Julia Set)");

        materialOldLCDStyleTransparencyShading = new JCheckBox("Old LCD Style Transparency Advanced Shading");
        materialOldLCDStyleTransparencyShading.setActionCommand(CHANGE_ACTION_COMMAND);
        materialOldLCDStyleTransparencyShading.addActionListener(this);
        materialOldLCDStyleTransparencyShading.setToolTipText("Old LCD Style Transparency Advanced Shading");

        materialErosionShading = new JCheckBox("Erosion Shading");
        materialErosionShading.setActionCommand(CHANGE_ACTION_COMMAND);
        materialErosionShading.addActionListener(this);
        materialErosionShading.setToolTipText("Erosion Shading (For Cloud, Lava, Marble, Granite & Wood Shaders)");

        materialNormalsSelectionMode = new JCheckBox("Normals Selection Mode");
        materialNormalsSelectionMode.setActionCommand(CHANGE_ACTION_COMMAND_NODES);
        materialNormalsSelectionMode.addActionListener(this);
        materialNormalsSelectionMode.setToolTipText("Normals Selection Mode (Using Geometry Shaders Technology utilizing OpenGL 3.0 capable hardware & needs the WireFrame Selection Mode turned on)");

        JPanel materialOptionsAndShading1Panel = new JPanel(true);
        materialOptionsAndShading1Panel.setLayout( new BoxLayout(materialOptionsAndShading1Panel, BoxLayout.Y_AXIS) );
        materialOptionsAndShading1Panel.add(materialSpecular);
        materialOptionsAndShading1Panel.add(materialGouraudLighting);
        materialOptionsAndShading1Panel.add(materialSphericalMapping);
        materialOptionsAndShading1Panel.add(materialEmbossNodeTexture);

        JPanel materialOptionsAndShading2Panel = new JPanel(true);
        materialOptionsAndShading2Panel.setLayout( new BoxLayout(materialOptionsAndShading2Panel, BoxLayout.Y_AXIS) );
        materialOptionsAndShading2Panel.add(materialAntiAliasShading);
        materialOptionsAndShading2Panel.add(materialAnimatedShading);
        materialOptionsAndShading2Panel.add(materialStateShading);
        materialOptionsAndShading2Panel.add(materialOldLCDStyleTransparencyShading);

        JPanel materialOptionsAndShading3Panel = new JPanel(true);
        materialOptionsAndShading3Panel.setLayout( new BoxLayout(materialOptionsAndShading3Panel, BoxLayout.Y_AXIS) );
        materialOptionsAndShading3Panel.add(materialErosionShading);
        materialOptionsAndShading3Panel.add(materialNormalsSelectionMode);

        materialOptionsAndShadingPanel.add(materialOptionsAndShading1Panel);
        materialOptionsAndShadingPanel.add( Box.createRigidArea( new Dimension(30, 10) ) );
        materialOptionsAndShadingPanel.add(materialShininess);
        materialOptionsAndShadingPanel.add( Box.createRigidArea( new Dimension(30, 10) ) );
        materialOptionsAndShadingPanel.add(materialOptionsAndShading2Panel);
        materialOptionsAndShadingPanel.add( Box.createRigidArea( new Dimension(10, 10) ) );
        materialOptionsAndShadingPanel.add(materialOptionsAndShading3Panel);

        JPanel textureOptionsPanel = new JPanel(true);

        nodeSurfaceImageTextureCheckBox = new JCheckBox("Node Surface Image Texture");
        nodeSurfaceImageTextureCheckBox.addActionListener(this);
        nodeSurfaceImageTextureCheckBox.setActionCommand(CHANGE_ACTION_COMMAND_NODES);
        nodeSurfaceImageTextureCheckBox.setToolTipText("Node Texture");

        nodeSurfaceImageTextureComboBox = new JComboBox<String>(DEFAULT_SURFACE_IMAGE_FILES);
        nodeSurfaceImageTextureComboBox.addActionListener(this);
        nodeSurfaceImageTextureComboBox.setEnabled(false);
        nodeSurfaceImageTextureComboBox.setActionCommand(CHANGE_ACTION_COMMAND_NODES);
        nodeSurfaceImageTextureComboBox.setToolTipText("Node Surface Image Textures");

        nodeSurfaceImageTextureFileTextField = new JTextField("", 30);
        nodeSurfaceImageTextureFileTextField.setEditable(false);
        nodeSurfaceImageTextureFileTextField.setEnabled(false);
        nodeSurfaceImageTextureFileTextField.addActionListener(this);
        nodeSurfaceImageTextureFileTextField.setActionCommand(CHANGE_ACTION_COMMAND_NODES);
        nodeSurfaceImageTextureFileTextField.setToolTipText("Loaded Node Surface Image Texture");
        nodeSurfaceImageTextureFileLoadButton = new JButton("Load");
        nodeSurfaceImageTextureFileLoadButton.addActionListener(this);
        nodeSurfaceImageTextureFileLoadButton.setActionCommand(CHANGE_ACTION_COMMAND_NODES);
        nodeSurfaceImageTextureFileLoadButton.setEnabled(false);
        nodeSurfaceImageTextureFileLoadButton.setToolTipText("Load");
        nodeSurfaceImageTextureFileClearButton = new JButton("Clear");
        nodeSurfaceImageTextureFileClearButton.setActionCommand(CHANGE_ACTION_COMMAND_NODES);
        nodeSurfaceImageTextureFileClearButton.addActionListener(this);
        nodeSurfaceImageTextureFileClearButton.setEnabled(false);
        nodeSurfaceImageTextureFileClearButton.setToolTipText("Clear");

        textureOptionsPanel.add(nodeSurfaceImageTextureCheckBox);
        textureOptionsPanel.add(nodeSurfaceImageTextureComboBox);
        textureOptionsPanel.add(nodeSurfaceImageTextureFileTextField);
        textureOptionsPanel.add(nodeSurfaceImageTextureFileLoadButton);
        textureOptionsPanel.add(nodeSurfaceImageTextureFileClearButton);

        String saveFilePath = FILE_CHOOSER_PATH.get().substring(0, FILE_CHOOSER_PATH.get().lastIndexOf( System.getProperty("file.separator") ) + 1);
        FileNameExtensionFilter nodeTextureFileNameExtensionFilter = new FileNameExtensionFilter("Load An Image File", "gif", "jpg", "jpeg", "png");
        nodeSurfaceImageTextureFileChooser = new JFileChooser(saveFilePath);
        nodeSurfaceImageTextureFileChooser.setDialogTitle("Choose An Image File For Node Texture");
        nodeSurfaceImageTextureFileChooser.setFileFilter(nodeTextureFileNameExtensionFilter);

        JPanel materialOptionsShadingAndTexturePanel = new JPanel(true);
        materialOptionsShadingAndTexturePanel.setLayout( new BoxLayout(materialOptionsShadingAndTexturePanel, BoxLayout.Y_AXIS) );
        materialOptionsShadingAndTexturePanel.add(materialOptionsAndShadingPanel);
        materialOptionsShadingAndTexturePanel.add(textureOptionsPanel);

        JPanel advancedShadersPanel = new JPanel(true);
        advancedShadersPanel.setLayout( new BoxLayout(advancedShadersPanel, BoxLayout.X_AXIS) );

        ShaderLightingSFXs.ShaderTypes[] allShaderTypes = ShaderLightingSFXs.ShaderTypes.values();
        String shaderEffectName = "";
        allShadings = new JCheckBox[ShaderLightingSFXs.NUMBER_OF_AVAILABLE_SHADERS];
        for (int i = 0; i < ShaderLightingSFXs.NUMBER_OF_AVAILABLE_SHADERS; i++)
        {
            shaderEffectName = allShaderTypes[i].toString().toLowerCase();
            shaderEffectName = Character.toUpperCase( shaderEffectName.charAt(0) ) + shaderEffectName.substring(1);
            allShadings[i] = new JCheckBox(shaderEffectName + " " + SHADER_CHECKBOX_MESSAGE);
            allShadings[i].setActionCommand(CHANGE_ACTION_COMMAND);
            allShadings[i].addActionListener(this);
            allShadings[i].setToolTipText(shaderEffectName + " " + SHADER_CHECKBOX_MESSAGE);
        }

        int index = 0;
        int numberOfColumns = ShaderLightingSFXs.NUMBER_OF_AVAILABLE_SHADERS / NUMBER_OF_SHADER_CHECKBOXES_PER_COLUMN + ( ( (ShaderLightingSFXs.NUMBER_OF_AVAILABLE_SHADERS % NUMBER_OF_SHADER_CHECKBOXES_PER_COLUMN) != 0) ? 1 : 0 );
        for (int i = 0; i < numberOfColumns; i++)
        {
            addShaderColumnPanel( advancedShadersPanel, index, ( i == (numberOfColumns - 1) ) );
            index += NUMBER_OF_SHADER_CHECKBOXES_PER_COLUMN;
        }

        JPanel renderingLargePanel = new JPanel(true);
        renderingLargePanel.setLayout( new BoxLayout(renderingLargePanel, BoxLayout.Y_AXIS) );

        renderingPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "General OpenGL Rendering Options");
        addTitledButtonBorder(renderingPropertiesPanelBorder, nodeOptionsPanel, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, renderingLargePanel);

        renderingPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "3D Lighting, Fog & Motion Blur Options");
        addTitledButtonBorder(renderingPropertiesPanelBorder, lightingFogMotionBlurSetup, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, renderingLargePanel);

        renderingPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "3D Node Material & Shading Options");
        addTitledButtonBorder(renderingPropertiesPanelBorder, materialOptionsShadingAndTexturePanel, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, renderingLargePanel);

        renderingPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "3D Node Advanced (Per-Pixel) GLSL Shaders Options" + ( (USE_SHADERS_PROCESS) ? "" : " (not available with the current configuration)") );
        addTitledButtonBorder(renderingPropertiesPanelBorder, advancedShadersPanel, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, renderingLargePanel);

        renderingPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "OpenGL Rendering Options");
        addTitledButtonBorderLarge(renderingPropertiesPanelBorder, renderingLargePanel, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, panel);

        addCommandButtonsToTab(panel, tabNumber);
        tabbedPane.addTab("Rendering", null, panel, "Rendering Properties");
    }

    private void createMCLPropertiesTab(JPanel panel, JTabbedPane tabbedPane, int tabNumber)
    {
        TitledBorder MCLPropertiesPanelBorder = null;

        MCL_inflationField = new FloatNumberField(0, 5);
        MCL_inflationField.setToolTipText("Inflation");

        MCL_preInflationField = new FloatNumberField(0, 5);
        MCL_preInflationField.setToolTipText("Pre Inflation");

        MCL_schemeField = new JTextField("", 10);
        MCL_schemeField.setToolTipText("Scheme");

        MCL_smallestClusterAllowedField = new JTextField("", 10);
        MCL_smallestClusterAllowedField.setDocument( new TextFieldFilter(TextFieldFilter.NUMERIC) );
        MCL_smallestClusterAllowedField.addCaretListener(this);
        MCL_smallestClusterAllowedField.setToolTipText("Smallest Cluster Allowed");

        MCL_assignRandomClusterColorsCheckBox = new JCheckBox("Assign Random Cluster Colors");
        MCL_assignRandomClusterColorsCheckBox.setActionCommand(CHANGE_ACTION_COMMAND);
        MCL_assignRandomClusterColorsCheckBox.addActionListener(this);
        MCL_assignRandomClusterColorsCheckBox.setToolTipText("Assign Random Cluster Colors");

        MCL_advancedOptionsTextField = new JTextField("", 20);
        MCL_advancedOptionsTextField.addCaretListener(this);
        MCL_advancedOptionsTextField.setToolTipText("MCL Advanced Options");

        MCL_clusterGraphUsingMCLButton = new JButton( layoutFrame.getLayoutClusterMCL().getClusterMCLAction() );
        MCL_clusterGraphUsingMCLButton.addActionListener(this);
        MCL_clusterGraphUsingMCLButton.setToolTipText("Cluster Graph Using MCL");

        JPanel MCL_largePanel = new JPanel(true);
        MCL_largePanel.setLayout( new BoxLayout(MCL_largePanel, BoxLayout.Y_AXIS) );
        MCL_inflationField.setEditable(false);
        MCL_preInflationField.setEditable(false);
        MCL_schemeField.setEditable(false);

        MCL_inflationField.setDocument( new TextFieldFilter(TextFieldFilter.FLOAT) );
        MCL_inflationSlider = new JSlider();
        MCL_inflationSlider.setMajorTickSpacing(20);
        MCL_inflationSlider.setMinorTickSpacing(10);
        MCL_inflationSlider.setMinimum(11);
        MCL_inflationSlider.setMaximum(200);
        MCL_inflationSlider.setPaintTicks(true);
        MCL_inflationSlider.addChangeListener( new SliderListener(MCL_inflationField, 10.0f) );
        MCL_inflationSlider.setToolTipText("Inflation");

        MCL_preInflationField.setDocument( new TextFieldFilter(TextFieldFilter.FLOAT) );
        MCL_pre_inflationSlider = new JSlider();
        MCL_pre_inflationSlider.setMajorTickSpacing(20);
        MCL_pre_inflationSlider.setMinorTickSpacing(10);
        MCL_pre_inflationSlider.setMinimum(0);
        MCL_pre_inflationSlider.setMaximum(200);
        MCL_pre_inflationSlider.setPaintTicks(true);
        MCL_pre_inflationSlider.addChangeListener( new SliderListener(MCL_preInflationField, 10.0f) );
        MCL_pre_inflationSlider.setToolTipText("Pre Inflation");

        MCL_schemeField.setDocument( new TextFieldFilter(TextFieldFilter.NUMERIC) );
        MCL_SchemeSlider = new JSlider();
        MCL_SchemeSlider.setMajorTickSpacing(2);
        MCL_SchemeSlider.setMinorTickSpacing(1);
        MCL_SchemeSlider.setMinimum(1);
        MCL_SchemeSlider.setMaximum(7);
        MCL_SchemeSlider.setPaintTicks(true);
        MCL_SchemeSlider.addChangeListener( new SliderListener(MCL_schemeField) );
        MCL_SchemeSlider.setToolTipText("Scheme");

        JPanel MCL_inflationPanel = new JPanel(true);
        MCL_inflationPanel.setLayout( new BoxLayout(MCL_inflationPanel, BoxLayout.Y_AXIS) );
        MCL_inflationPanel.add(MCL_inflationField);
        MCL_inflationPanel.add(MCL_inflationSlider);

        JPanel MCL_pre_inflationPanel = new JPanel(true);
        MCL_pre_inflationPanel.setLayout( new BoxLayout(MCL_pre_inflationPanel, BoxLayout.Y_AXIS) );
        MCL_pre_inflationPanel.add(MCL_preInflationField);
        MCL_pre_inflationPanel.add(MCL_pre_inflationSlider);

        JPanel MCL_SchemePanel = new JPanel(true);
        MCL_SchemePanel.setLayout( new BoxLayout(MCL_SchemePanel, BoxLayout.Y_AXIS) );
        MCL_SchemePanel.add(MCL_schemeField);
        MCL_SchemePanel.add(MCL_SchemeSlider);

        MCLPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "Inflation");
        addTitledButtonBorder(MCLPropertiesPanelBorder, MCL_inflationPanel, "           (e.g. 3.0)", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, MCL_largePanel);

        MCLPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "Pre Inflation");
        addTitledButtonBorder(MCLPropertiesPanelBorder, MCL_pre_inflationPanel, "           (e.g. 3.0)", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, MCL_largePanel);

        MCLPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "Scheme");
        addTitledButtonBorder(MCLPropertiesPanelBorder, MCL_SchemePanel, "           (e.g. 6)", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, MCL_largePanel);

        MCLPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "Smallest Cluster Allowed");
        addTitledButtonBorder(MCLPropertiesPanelBorder, MCL_smallestClusterAllowedField, "                              (e.g. 3)", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, MCL_largePanel);

        MCLPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "Assign Random Cluster Colors");
        addTitledButtonBorder(MCLPropertiesPanelBorder, MCL_assignRandomClusterColorsCheckBox, "  (e.g. OFF)", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, MCL_largePanel);

        MCLPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "MCL Advanced Options");
        addTitledButtonBorder(MCLPropertiesPanelBorder, MCL_advancedOptionsTextField, "(expert users only, else leave empty)", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, MCL_largePanel);

        MCLPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "Cluster Graph Using MCL");
        addTitledButtonBorder(MCLPropertiesPanelBorder, MCL_clusterGraphUsingMCLButton, "    (cluster graph using current MCL options)", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, MCL_largePanel);

        MCLPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "Markov Clustering (MCL) Options");
        addTitledButtonBorderLarge(MCLPropertiesPanelBorder, MCL_largePanel, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, panel);

        addCommandButtonsToTab(panel, tabNumber);
        tabbedPane.addTab("MCL", null, panel, "MCL Properties");
    }

    private void createSimulationPropertiesTab(JPanel panel, JTabbedPane tabbedPane, int tabNumber)
    {
        TitledBorder simulationPropertiesPanelBorder = null;

        JPanel simulationSPNPanel = new JPanel(true);
        simulationSPNPanel.setLayout( new BoxLayout(simulationSPNPanel, BoxLayout.Y_AXIS) );

        JPanel saveSPNResultsPanel = new JPanel(true);
        GridBagLayout saveSPNResultsPanelLayout = new GridBagLayout();
        GridBagConstraints saveSPNResultsPanelConstraints = new GridBagConstraints();
        saveSPNResultsPanel.setLayout(saveSPNResultsPanelLayout);

        JPanel saveSPNResultsSubPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT), true);
        saveSPNResultsCheckBox = new JCheckBox("Save SPN Results");
        saveSPNResultsCheckBox.setActionCommand(CHANGE_ACTION_COMMAND);
        saveSPNResultsCheckBox.addActionListener(this);
        saveSPNResultsCheckBox.setToolTipText("Save SPN Results");
        saveSPNResultsTextField = new JTextField("", 30);
        saveSPNResultsTextField.setEditable(false);
        saveSPNResultsTextField.setEnabled(false);
        saveSPNResultsTextField.setToolTipText("Save SPN Results");
        saveSPNResultsAtFolderButton = new JButton("Save SPN Results At Folder");
        saveSPNResultsAtFolderButton.setActionCommand(CHANGE_ACTION_COMMAND);
        saveSPNResultsAtFolderButton.addActionListener(this);
        saveSPNResultsAtFolderButton.setEnabled(false);
        saveSPNResultsAtFolderButton.setToolTipText("Save SPN Results At Folder");
        saveSPNResultsClearButton = new JButton("Clear");
        saveSPNResultsClearButton.setActionCommand(CHANGE_ACTION_COMMAND);
        saveSPNResultsClearButton.addActionListener(this);
        saveSPNResultsClearButton.setEnabled(false);
        saveSPNResultsClearButton.setToolTipText("Clear");
        saveSPNResultsSubPanel1.add(saveSPNResultsCheckBox);
        saveSPNResultsSubPanel1.add(saveSPNResultsTextField);
        saveSPNResultsSubPanel1.add(saveSPNResultsAtFolderButton);
        saveSPNResultsSubPanel1.add(saveSPNResultsClearButton);

        JPanel saveSPNResultsSubPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT), true);
        automaticallySaveSPNResultsToPreChosenFolderCheckBox = new JCheckBox("Automatically save SPN Results to pre-chosen folder");
        automaticallySaveSPNResultsToPreChosenFolderCheckBox.setActionCommand(CHANGE_ACTION_COMMAND);
        automaticallySaveSPNResultsToPreChosenFolderCheckBox.addActionListener(this);
        automaticallySaveSPNResultsToPreChosenFolderCheckBox.setEnabled(false);
        automaticallySaveSPNResultsToPreChosenFolderCheckBox.setToolTipText("Automatically save SPN Results to pre-chosen folder");
        saveSPNResultsSubPanel2.add(automaticallySaveSPNResultsToPreChosenFolderCheckBox);

        saveSPNResultsPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
        saveSPNResultsPanelConstraints.gridx = 0;
        saveSPNResultsPanelConstraints.gridy = 0;
        saveSPNResultsPanelLayout.setConstraints(saveSPNResultsSubPanel1, saveSPNResultsPanelConstraints);
        saveSPNResultsPanel.add(saveSPNResultsSubPanel1);
        saveSPNResultsPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
        saveSPNResultsPanelConstraints.gridx = 0;
        saveSPNResultsPanelConstraints.gridy = 1;
        saveSPNResultsPanelLayout.setConstraints(saveSPNResultsSubPanel2, saveSPNResultsPanelConstraints);
        saveSPNResultsPanel.add(saveSPNResultsSubPanel2);

        saveSPNResultsFileChooser = new JFileChooser( SAVE_SPN_RESULTS_FILE_NAME.get() );
        saveSPNResultsFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        saveSPNResultsFileChooser.setDialogTitle("Choose Where to Save the SPN Results Text File");

        useSPNAnimatedTransitionsShadingCheckBox = new JCheckBox("Use SPN Animated Transitions Shading (3D graphs only)");
        useSPNAnimatedTransitionsShadingCheckBox.setActionCommand(CHANGE_ACTION_COMMAND);
        useSPNAnimatedTransitionsShadingCheckBox.addActionListener(this);
        useSPNAnimatedTransitionsShadingCheckBox.setEnabled(USE_SHADERS_PROCESS);
        useSPNAnimatedTransitionsShadingCheckBox.setToolTipText("Use SPN Animated Transitions Shading (3D graphs only)");

        runSPNSimulationButton = new JButton( layoutFrame.getSignalingPetriNetSimulationDialog().getSignalingPetriNetSimulationDialogAction() );
        runSPNSimulationButton.addActionListener(this);
        runSPNSimulationButton.setToolTipText("Run SPN Simulation");

        simulationPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "Save SPN Results Options");
        addTitledButtonBorder(simulationPropertiesPanelBorder, saveSPNResultsPanel, "            (e.g. OFF)", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, simulationSPNPanel);

        simulationPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "Use SPN Animated Transitions Shading (Advanced (Per-Pixel) GLSL Shader Option)");
        addTitledButtonBorder(simulationPropertiesPanelBorder, useSPNAnimatedTransitionsShadingCheckBox, "(e.g. OFF)", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, simulationSPNPanel);

        simulationPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "Run SPN Simulation");
        addTitledButtonBorder(simulationPropertiesPanelBorder, runSPNSimulationButton, "(run SPN simulation using current options)", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, simulationSPNPanel);

        simulationPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "Signaling Petri Net (SPN) Simulation Options");
        addTitledButtonBorderLarge(simulationPropertiesPanelBorder, simulationSPNPanel, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, panel);

        addCommandButtonsToTab(panel, tabNumber);
        tabbedPane.addTab("Simulation", null, panel, "Simulation Properties");
    }

    private void createParallelismPropertiesTab(JPanel panel, JTabbedPane tabbedPane, int tabNumber)
    {
        TitledBorder parallelismPropertiesPanelBorder = null;

        JPanel parallelismPanel = new JPanel(true);
        parallelismPanel.setLayout( new BoxLayout(parallelismPanel, BoxLayout.Y_AXIS) );

        final String[] SELECTED_CORRELATION_CALCULATION_GPU_COMPUTING_CPU_COMPARISON_METHODS = (!USE_MULTICORE_PROCESS)
                                                                                               ? ( new String[]{ GPU_COMPUTING_CPU_COMPARISON_METHODS[0] } )
                                                                                               : ( new String[]{ GPU_COMPUTING_CPU_COMPARISON_METHODS[0], GPU_COMPUTING_CPU_COMPARISON_METHODS[2] } );

        parallelismUseExpressionCorrelationCalculationNCoreParallelism = new JRadioButton("Use Expression Correlation Calculation CPU N-Core Parallelism");
        parallelismUseExpressionCorrelationCalculationNCoreParallelism.setActionCommand(CHANGE_ACTION_COMMAND);
        parallelismUseExpressionCorrelationCalculationNCoreParallelism.addActionListener(this);
        parallelismUseExpressionCorrelationCalculationNCoreParallelism.setToolTipText("Use Expression Correlation Calculation CPU N-Core Parallelism");
        parallelismUseExpressionCorrelationCalculationOpenCLGPUComputing = new JRadioButton("Use Expression Correlation Calculation OpenCL GPU Computing");
        parallelismUseExpressionCorrelationCalculationOpenCLGPUComputing.setActionCommand(CHANGE_ACTION_COMMAND);
        parallelismUseExpressionCorrelationCalculationOpenCLGPUComputing.addActionListener(this);
        parallelismUseExpressionCorrelationCalculationOpenCLGPUComputing.setToolTipText("Use Expression Correlation Calculation OpenCL GPU Computing. A high-end GPU is needed, recommended hardware is the NVidia Geforce GTX 285 or Tesla series & the ATI Radeon HD 5800 series and above with at least 1GB of VRAM.");
        parallelismUseExpressionCorrelationCalculationOpenCLGPUComputingIterationSizeLabel = new JLabel("OpenCL Iteration Size:");
        parallelismUseExpressionCorrelationCalculationOpenCLGPUComputingIterationSize = new JComboBox<Integer>();
        for (int i = OPENCL_GPU_COMPUTING_ITERATION_SIZES.length - 1; i >= 0; i--)
            parallelismUseExpressionCorrelationCalculationOpenCLGPUComputingIterationSize.addItem(OPENCL_GPU_COMPUTING_ITERATION_SIZES[i]);
        parallelismUseExpressionCorrelationCalculationOpenCLGPUComputingIterationSize.setSelectedItem( Integer.toString(OPENCL_DEFAULT_EXPRESSION_CORRELATION_ITERATION_SIZE) );
        parallelismUseExpressionCorrelationCalculationOpenCLGPUComputingIterationSize.addActionListener(this);
        parallelismUseExpressionCorrelationCalculationOpenCLGPUComputingIterationSize.setActionCommand(CHANGE_ACTION_COMMAND);
        parallelismUseExpressionCorrelationCalculationOpenCLGPUComputingIterationSize.setToolTipText("OpenCL Iteration Size");
        parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputing = new JRadioButton("Use Expression Correlation Calculation GLSL GPGPU Computing");
        parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputing.setActionCommand(CHANGE_ACTION_COMMAND);
        parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputing.addActionListener(this);
        parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputing.setToolTipText("Use Expression Correlation Calculation GLSL GPGPU Computing. A high-end GPU is needed. Use this option at your own risk. Recommended hardware is the NVidia Geforce GTX 285 or Tesla series & the ATI Radeon HD 5800 series and above with at least 1GB of VRAM.");
        parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputingTextureSizeAndTypeLabel = new JLabel("GLSL Texture Size & Type:");
        parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputingTextureSize = new JComboBox<Integer>();
        for (int i = GLSL_MAX_TEXTURE_SIZE; i >= GLSL_TEXTURE_STEP; i -= GLSL_TEXTURE_STEP)
            parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputingTextureSize.addItem(i);
        parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputingTextureSize.setSelectedItem( Integer.toString(GLSL_DEFAULT_TEXTURE_SIZE) );
        parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputingTextureSize.addActionListener(this);
        parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputingTextureSize.setActionCommand(CHANGE_ACTION_COMMAND);
        parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputingTextureSize.setToolTipText("GLSL Texture Size");
        parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputingTextureType = new JComboBox<GLSLTextureTypes>( GLSLTextureTypes.values() );
        parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputingTextureType.addActionListener(this);
        parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputingTextureType.setActionCommand(CHANGE_ACTION_COMMAND);
        parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputingTextureType.setToolTipText("GLSL Texture Type");
        parallelismUseGPUComputingExpressionCorrelationCalculationCompareGPUWithCPU = new JCheckBox("Compare GPU With CPU   (Shows GPU Calc & GPU Calc With Transfers vs CPU Time)");
        parallelismUseGPUComputingExpressionCorrelationCalculationCompareGPUWithCPU.setActionCommand(CHANGE_ACTION_COMMAND);
        parallelismUseGPUComputingExpressionCorrelationCalculationCompareGPUWithCPU.addActionListener(this);
        parallelismUseGPUComputingExpressionCorrelationCalculationCompareGPUWithCPU.setToolTipText("Compare GPU Computing Expression Correlation Calculations with the CPU (the Correlation Calculations are run on both the GPU & the CPU and it shows GPU Calc & GPU Calc With Transfers vs CPU Time)");
        parallelismUseGPUComputingExpressionCorrelationCalculationCompareGPUWithCPUComparisonMethodsLabel = new JLabel("CPU Comparison Method:");
        parallelismUseGPUComputingExpressionCorrelationCalculationCompareGPUWithCPUComparisonMethods = new JComboBox<String>(SELECTED_CORRELATION_CALCULATION_GPU_COMPUTING_CPU_COMPARISON_METHODS);
        parallelismUseGPUComputingExpressionCorrelationCalculationCompareGPUWithCPUComparisonMethods.addActionListener(this);
        parallelismUseGPUComputingExpressionCorrelationCalculationCompareGPUWithCPUComparisonMethods.setActionCommand(CHANGE_ACTION_COMMAND);
        parallelismUseGPUComputingExpressionCorrelationCalculationCompareGPUWithCPUComparisonMethods.setToolTipText("CPU Comparison Method");

        ButtonGroup expressionParallelismGroup = new ButtonGroup();
        expressionParallelismGroup.add(parallelismUseExpressionCorrelationCalculationNCoreParallelism);
        expressionParallelismGroup.add(parallelismUseExpressionCorrelationCalculationOpenCLGPUComputing);
        expressionParallelismGroup.add(parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputing);

        final String[] SELECTED_LAYOUT_GPU_COMPUTING_CPU_COMPARISON_METHODS = (!USE_MULTICORE_PROCESS)
                                                                              ? ( new String[]{ GPU_COMPUTING_CPU_COMPARISON_METHODS[0] } )
                                                                              : ( new String[]{ GPU_COMPUTING_CPU_COMPARISON_METHODS[0], GPU_COMPUTING_CPU_COMPARISON_METHODS[2] } );

        parallelismUseLayoutNCoreParallelism = new JRadioButton("Use Layout CPU N-Core Parallelism");
        parallelismUseLayoutNCoreParallelism.setToolTipText("Use Layout CPU N-Core Parallelism");
        parallelismUseLayoutNCoreParallelism.addActionListener(this);
        parallelismUseLayoutNCoreParallelism.setActionCommand(CHANGE_ACTION_COMMAND);
        parallelismUseLayoutOpenCLGPUComputing = new JRadioButton("Use Layout OpenCL GPU Computing");
        parallelismUseLayoutOpenCLGPUComputing.setToolTipText("Use Layout OpenCL GPU Computing. A high-end GPU is needed, recommended hardware is the NVidia Geforce GTX 285 or Tesla series & the ATI Radeon HD 5800 series and above with at least 1GB of VRAM.");
        parallelismUseLayoutOpenCLGPUComputing.addActionListener(this);
        parallelismUseLayoutOpenCLGPUComputing.setActionCommand(CHANGE_ACTION_COMMAND);
        parallelismUseIndices1DKernelWithIterationsForLayoutOpenCLGPUComputing = new JCheckBox("Use Indices 1D Kernel With Iterations");
        parallelismUseIndices1DKernelWithIterationsForLayoutOpenCLGPUComputing.setActionCommand(CHANGE_ACTION_COMMAND);
        parallelismUseIndices1DKernelWithIterationsForLayoutOpenCLGPUComputing.addActionListener(this);
        parallelismUseIndices1DKernelWithIterationsForLayoutOpenCLGPUComputing.setToolTipText("Use Indices 1D Kernel With Iterations (Used as a safety measure for very large networks that may freeze the GPU if processed in one go)");
        parallelismUseLayoutOpenCLGPUComputingIterationSizeLabel = new JLabel("OpenCL Iteration Size:");
        parallelismUseLayoutOpenCLGPUComputingIterationSize = new JComboBox<Integer>();
        for (int i = OPENCL_GPU_COMPUTING_ITERATION_SIZES.length - 1; i >= 0; i--)
            parallelismUseLayoutOpenCLGPUComputingIterationSize.addItem(OPENCL_GPU_COMPUTING_ITERATION_SIZES[i]);
        parallelismUseLayoutOpenCLGPUComputingIterationSize.setSelectedItem( Integer.toString(OPENCL_DEFAULT_LAYOUT_ITERATION_SIZE) );
        parallelismUseLayoutOpenCLGPUComputingIterationSize.addActionListener(this);
        parallelismUseLayoutOpenCLGPUComputingIterationSize.setActionCommand(CHANGE_ACTION_COMMAND);
        parallelismUseLayoutOpenCLGPUComputingIterationSize.setToolTipText("OpenCL Iteration Size");
        parallelismUseGPUComputingLayoutCompareGPUWithCPU = new JCheckBox("Compare GPU With CPU   (Shows GPU Calc & GPU Calc With Transfers vs CPU time)");
        parallelismUseGPUComputingLayoutCompareGPUWithCPU.setActionCommand(CHANGE_ACTION_COMMAND);
        parallelismUseGPUComputingLayoutCompareGPUWithCPU.addActionListener(this);
        parallelismUseGPUComputingLayoutCompareGPUWithCPU.setToolTipText("Compare GPU Computing Expression Correlation Calculations with the CPU (the Correlation Calculations are run on both the GPU & the CPU and it shows GPU Calc & GPU Calc With Transfers vs CPU time)");
        parallelismUseGPUComputingLayoutCompareGPUWithCPUComparisonMethodsLabel = new JLabel("CPU Comparison Method:");
        parallelismUseGPUComputingLayoutCompareGPUWithCPUComparisonMethods = new JComboBox<String>(SELECTED_LAYOUT_GPU_COMPUTING_CPU_COMPARISON_METHODS);
        parallelismUseGPUComputingLayoutCompareGPUWithCPUComparisonMethods.addActionListener(this);
        parallelismUseGPUComputingLayoutCompareGPUWithCPUComparisonMethods.setActionCommand(CHANGE_ACTION_COMMAND);
        parallelismUseGPUComputingLayoutCompareGPUWithCPUComparisonMethods.setToolTipText("CPU Comparison Method");
        parallelismUseAtomicSynchronizationForLayoutNCoreParallelism = new JCheckBox("Use Atomic Synchronization For Layout Parallelism");
        parallelismUseAtomicSynchronizationForLayoutNCoreParallelism.setToolTipText("Use Atomic Synchronization For Layout Parallelism. Used for both N-CP & OpenCL GPU Computing options.");
        parallelismUseAtomicSynchronizationForLayoutNCoreParallelism.addActionListener(this);
        parallelismUseAtomicSynchronizationForLayoutNCoreParallelism.setActionCommand(CHANGE_ACTION_COMMAND);

        ButtonGroup layoutParallelismGroup = new ButtonGroup();
        layoutParallelismGroup.add(parallelismUseLayoutNCoreParallelism);
        layoutParallelismGroup.add(parallelismUseLayoutOpenCLGPUComputing);

        parallelismUseMCLNCoreParallelism = new JCheckBox("Use MCL Parallelism");
        parallelismUseMCLNCoreParallelism.setActionCommand(CHANGE_ACTION_COMMAND);
        parallelismUseMCLNCoreParallelism.addActionListener(this);
        parallelismUseMCLNCoreParallelism.setToolTipText("Use MCL Parallelism");

        parallelismUseSPNNCoreParallelism = new JCheckBox("Use SPN CPU N-Core Parallelism");
        parallelismUseSPNNCoreParallelism.setActionCommand(CHANGE_ACTION_COMMAND);
        parallelismUseSPNNCoreParallelism.addActionListener(this);
        parallelismUseSPNNCoreParallelism.setToolTipText("Use SPN CPU N-Core Parallelism");

        JPanel generalSmallPanelExpressionCorrelationCalculation = new JPanel(true);
        generalSmallPanelExpressionCorrelationCalculation.setLayout( new BoxLayout(generalSmallPanelExpressionCorrelationCalculation, BoxLayout.Y_AXIS) );
        JPanel generalSmallPanelExpressionCorrelationCalculationOpenCLGPUComputing = new JPanel(true);
        generalSmallPanelExpressionCorrelationCalculationOpenCLGPUComputing.setLayout( new BoxLayout(generalSmallPanelExpressionCorrelationCalculationOpenCLGPUComputing, BoxLayout.X_AXIS) );
        JPanel generalSmallPanelExpressionCorrelationCalculationGLSLGPGPUComputing = new JPanel(true);
        generalSmallPanelExpressionCorrelationCalculationGLSLGPGPUComputing.setLayout( new BoxLayout(generalSmallPanelExpressionCorrelationCalculationGLSLGPGPUComputing, BoxLayout.X_AXIS) );
        JPanel generalSmallPanelExpressionCorrelationCalculationCPUComparisonMethods = new JPanel(true);
        generalSmallPanelExpressionCorrelationCalculationCPUComparisonMethods.setLayout( new BoxLayout(generalSmallPanelExpressionCorrelationCalculationCPUComparisonMethods, BoxLayout.X_AXIS) );

        parallelismPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "Expression Correlation Calculation Parallelism");
        generalSmallPanelExpressionCorrelationCalculation.add( addLegentToRightOfComponent(parallelismUseExpressionCorrelationCalculationNCoreParallelism, "      (e.g. ON)") );
        generalSmallPanelExpressionCorrelationCalculation.add( Box.createRigidArea( new Dimension(5, 1) ) );
        generalSmallPanelExpressionCorrelationCalculation.add( addLegentToRightOfComponent(parallelismUseExpressionCorrelationCalculationOpenCLGPUComputing, "    (e.g. OFF)") );
        generalSmallPanelExpressionCorrelationCalculationOpenCLGPUComputing.add( Box.createRigidArea( new Dimension(320, 0) ) );
        generalSmallPanelExpressionCorrelationCalculationOpenCLGPUComputing.add(parallelismUseExpressionCorrelationCalculationOpenCLGPUComputingIterationSizeLabel);
        generalSmallPanelExpressionCorrelationCalculationOpenCLGPUComputing.add( Box.createRigidArea( new Dimension(5, 0) ) );
        generalSmallPanelExpressionCorrelationCalculationOpenCLGPUComputing.add(parallelismUseExpressionCorrelationCalculationOpenCLGPUComputingIterationSize);
        generalSmallPanelExpressionCorrelationCalculation.add(generalSmallPanelExpressionCorrelationCalculationOpenCLGPUComputing);
        generalSmallPanelExpressionCorrelationCalculation.add( Box.createRigidArea( new Dimension(5, 1) ) );
        generalSmallPanelExpressionCorrelationCalculation.add( addLegentToRightOfComponent(parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputing, "     (e.g. OFF)") );
        generalSmallPanelExpressionCorrelationCalculationGLSLGPGPUComputing.add( Box.createRigidArea( new Dimension(25, 0) ) );
        generalSmallPanelExpressionCorrelationCalculationGLSLGPGPUComputing.add(parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputingTextureSizeAndTypeLabel);
        generalSmallPanelExpressionCorrelationCalculationGLSLGPGPUComputing.add( Box.createRigidArea( new Dimension(5, 0) ) );
        generalSmallPanelExpressionCorrelationCalculationGLSLGPGPUComputing.add(parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputingTextureSize);
        generalSmallPanelExpressionCorrelationCalculationGLSLGPGPUComputing.add(parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputingTextureType);
        generalSmallPanelExpressionCorrelationCalculation.add(generalSmallPanelExpressionCorrelationCalculationGLSLGPGPUComputing);
        generalSmallPanelExpressionCorrelationCalculation.add( Box.createRigidArea( new Dimension(5, 1) ) );
        generalSmallPanelExpressionCorrelationCalculation.add( addLegentToRightOfComponent(parallelismUseGPUComputingExpressionCorrelationCalculationCompareGPUWithCPU, "         (e.g. OFF)")  );
        generalSmallPanelExpressionCorrelationCalculationCPUComparisonMethods.add( Box.createRigidArea( new Dimension(320, 0) ) );
        generalSmallPanelExpressionCorrelationCalculationCPUComparisonMethods.add(parallelismUseGPUComputingExpressionCorrelationCalculationCompareGPUWithCPUComparisonMethodsLabel);
        generalSmallPanelExpressionCorrelationCalculationCPUComparisonMethods.add( Box.createRigidArea( new Dimension(5, 0) ) );
        generalSmallPanelExpressionCorrelationCalculationCPUComparisonMethods.add(parallelismUseGPUComputingExpressionCorrelationCalculationCompareGPUWithCPUComparisonMethods);
        generalSmallPanelExpressionCorrelationCalculation.add(generalSmallPanelExpressionCorrelationCalculationCPUComparisonMethods);
        addTitledButtonBorder(parallelismPropertiesPanelBorder, generalSmallPanelExpressionCorrelationCalculation, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, parallelismPanel);

        JPanel generalSmallPanelLayout = new JPanel(true);
        generalSmallPanelLayout.setLayout( new BoxLayout(generalSmallPanelLayout, BoxLayout.Y_AXIS) );
        JPanel generalSmallPanelLayoutOpenCLGPUComputing = new JPanel(true);
        generalSmallPanelLayoutOpenCLGPUComputing.setLayout( new BoxLayout(generalSmallPanelLayoutOpenCLGPUComputing, BoxLayout.X_AXIS) );
        JPanel generalSmallPanelLayoutCPUComparisonMethods = new JPanel(true);
        generalSmallPanelLayoutCPUComparisonMethods.setLayout( new BoxLayout(generalSmallPanelLayoutCPUComparisonMethods, BoxLayout.X_AXIS) );

        parallelismPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "Layout Parallelism");
        generalSmallPanelLayout.add( addLegentToRightOfComponent(parallelismUseLayoutNCoreParallelism, "      (e.g. ON)") );
        generalSmallPanelLayout.add( Box.createRigidArea( new Dimension(5, 1) ) );
        generalSmallPanelLayout.add( addLegentToRightOfComponent(parallelismUseLayoutOpenCLGPUComputing, "    (e.g. OFF)") );
        generalSmallPanelLayoutOpenCLGPUComputing.add( Box.createRigidArea( new Dimension(80, 0) ) );
        generalSmallPanelLayoutOpenCLGPUComputing.add(parallelismUseIndices1DKernelWithIterationsForLayoutOpenCLGPUComputing);
        generalSmallPanelLayoutOpenCLGPUComputing.add( Box.createRigidArea( new Dimension(50, 0) ) );
        generalSmallPanelLayoutOpenCLGPUComputing.add(parallelismUseLayoutOpenCLGPUComputingIterationSizeLabel);
        generalSmallPanelLayoutOpenCLGPUComputing.add( Box.createRigidArea( new Dimension(5, 0) ) );
        generalSmallPanelLayoutOpenCLGPUComputing.add(parallelismUseLayoutOpenCLGPUComputingIterationSize);
        generalSmallPanelLayout.add(generalSmallPanelLayoutOpenCLGPUComputing);
        generalSmallPanelLayout.add( Box.createRigidArea( new Dimension(5, 1) ) );
        generalSmallPanelLayout.add( addLegentToRightOfComponent(parallelismUseGPUComputingLayoutCompareGPUWithCPU, "         (e.g. OFF)")  );
        generalSmallPanelLayoutCPUComparisonMethods.add( Box.createRigidArea( new Dimension(320, 0) ) );
        generalSmallPanelLayoutCPUComparisonMethods.add(parallelismUseGPUComputingLayoutCompareGPUWithCPUComparisonMethodsLabel);
        generalSmallPanelLayoutCPUComparisonMethods.add( Box.createRigidArea( new Dimension(5, 0) ) );
        generalSmallPanelLayoutCPUComparisonMethods.add(parallelismUseGPUComputingLayoutCompareGPUWithCPUComparisonMethods);
        generalSmallPanelLayout.add(generalSmallPanelLayoutCPUComparisonMethods);
        generalSmallPanelLayout.add( Box.createRigidArea( new Dimension(5, 1) ) );
        generalSmallPanelLayout.add( addLegentToRightOfComponent(parallelismUseAtomicSynchronizationForLayoutNCoreParallelism, "(e.g. ON)") );
        addTitledButtonBorder(parallelismPropertiesPanelBorder, generalSmallPanelLayout, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, parallelismPanel);

        parallelismPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "MCL Parallelism");
        addTitledButtonBorder(parallelismPropertiesPanelBorder, parallelismUseMCLNCoreParallelism, "                   (e.g. ON)", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, parallelismPanel);

        parallelismPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "SPN Parallelism");
        addTitledButtonBorder(parallelismPropertiesPanelBorder, parallelismUseSPNNCoreParallelism, "        (e.g. ON)", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, parallelismPanel);

        parallelismPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "CPU N-Core & GPU Computing Parallelism Options");
        addTitledButtonBorderLarge(parallelismPropertiesPanelBorder, parallelismPanel, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, panel);

        addCommandButtonsToTab(panel, tabNumber);
        tabbedPane.addTab("Parallelism", null, panel, "Parallelism Properties");
    }

    private void createSearchPropertiesTab(JPanel panel, JTabbedPane tabbedPane, int tabNumber)
    {
        TitledBorder searchPropertiesPanelBorder = null;

        searchURLComboBox = new JComboBox<SearchURL>(PRESET_SEARCH_URL);
        searchURLComboBox.setSelectedItem(SEARCH_URL);
        searchURLComboBox.addActionListener(this);
        searchURLComboBox.setActionCommand(CHANGE_ACTION_COMMAND);
        searchURLComboBox.setToolTipText("Use Preset Search Sites");

        presetURLTextField = new JTextArea( ( (SearchURL)searchURLComboBox.getSelectedItem() ).getUrl() );
        presetURLTextField.setBorder(BorderFactory.createEtchedBorder() );
        presetURLTextField.setPreferredSize( new Dimension(580, 100) );
        presetURLTextField.setEditable(false);
        presetURLTextField.setWrapStyleWord(false);
        presetURLTextField.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        presetURLTextField.setToolTipText("Use Preset Search Sites");

        customURLTextField = new JTextField(50);
        customURLTextField.setText("http://");
        customURLTextField.setEnabled(false);
        customURLTextField.addCaretListener(this);
        customURLTextField.setToolTipText("Use Custom Search Sites");

        presetRadioButton = new JRadioButton();
        presetRadioButton.setSelected(true);
        presetRadioButton.addActionListener(this);
        presetRadioButton.setActionCommand(CHANGE_ACTION_COMMAND);
        presetRadioButton.setToolTipText("Use Preset Search Sites");

        customRadioButton = new JRadioButton();
        customRadioButton.addActionListener(this);
        customRadioButton.setActionCommand(CHANGE_ACTION_COMMAND);
        customRadioButton.setToolTipText("Use Custom Search Sites");

        ButtonGroup searchButtonGroup = new ButtonGroup();
        searchButtonGroup.add(presetRadioButton);
        searchButtonGroup.add(customRadioButton);

        JPanel topSubPanel = new JPanel(new FlowLayout(FlowLayout.LEFT), true);
        topSubPanel.add(presetRadioButton);
        topSubPanel.add(searchURLComboBox);
        topSubPanel.setBorder(BorderFactory.createEtchedBorder() );

        JPanel LabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT), true);
        LabelPanel.setBorder(BorderFactory.createEtchedBorder() );
        LabelPanel.add(presetURLTextField);

        JPanel topPanel = new JPanel(true);
        topPanel.setLayout( new BoxLayout(topPanel, BoxLayout.Y_AXIS) );
        //topPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        topPanel.add(topSubPanel);
        topPanel.add( Box.createRigidArea( new Dimension(10, 20) ) );
        topPanel.add(LabelPanel);
        searchPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "Use Preset Search Sites Options");
        addTitledButtonBorderLarge(searchPropertiesPanelBorder, topPanel, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, panel);

        JPanel lowPanel = new JPanel(true);
        lowPanel.add(customRadioButton);
        lowPanel.add(customURLTextField);
        searchPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "Use Custom Search Sites Options");
        addTitledButtonBorderLarge(searchPropertiesPanelBorder, lowPanel, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, panel);

        addCommandButtonsToTab(panel, tabNumber);
        tabbedPane.addTab("Search", null, panel, "Search Properties");
    }

    private void createNodesPropertiesTab(JPanel panel, JTabbedPane tabbedPane, int tabNumber)
    {
        TitledBorder nodePropertiesPanelBorder = null;

        nodeNameTextField = new JTextField(10);
        nodeNameTextField.setAutoscrolls(true);
        nodeNameTextField.setColumns(15);
        nodeNameTextField.setText("N/A");
        nodeNameTextField.addCaretListener(this);
        nodeNameTextField.setToolTipText("Node Identifier");

        graphmlViewNodeDepthPositioningTextField = new FloatNumberField(0, 10);
        graphmlViewNodeDepthPositioningTextField.setDocument( new TextFieldFilter(TextFieldFilter.FLOAT, true) );
        graphmlViewNodeDepthPositioningTextField.setAutoscrolls(true);
        graphmlViewNodeDepthPositioningTextField.setColumns(15);
        graphmlViewNodeDepthPositioningTextField.addCaretListener(this);
        graphmlViewNodeDepthPositioningTextField.setText("0.0");
        graphmlViewNodeDepthPositioningTextField.setEnabled(false);
        graphmlViewNodeDepthPositioningTextField.setToolTipText("Graphml View Node Depth Positioning");
        graphmlViewNodeDepthZPositioningLabel = new JLabel("Graphml View Node Depth Positioning");
        graphmlAllViewNodeDepthResetPositioningsButton = new JButton("Reset All Graphml View Node Depth Positionings");
        graphmlAllViewNodeDepthResetPositioningsButton.setActionCommand(CHANGE_ACTION_COMMAND_ALL);
        graphmlAllViewNodeDepthResetPositioningsButton.addActionListener(this);
        graphmlAllViewNodeDepthResetPositioningsButton.setToolTipText("Reset All Graphml View Node Depth Positionings");

        nodeRevertOverride = new JButton("Revert");
        nodeRevertOverride.setActionCommand(CHANGE_ACTION_COMMAND_NODES);
        nodeRevertOverride.addActionListener(this);
        nodeRevertOverride.setToolTipText("Revert");

        nodeClassComboBox = new ClassComboBox(layoutClassSetsManager.getCurrentClassSetAllClasses(), true, false);
        nodeClassComboBox.setActionCommand(CHANGE_ACTION_COMMAND_NODES);
        nodeClassComboBox.addActionListener(this);
        nodeClassComboBox.setToolTipText("Containing Class");

        node2DShape = new JComboBox<String>();
        String node2DShapeName = "";
        for ( Shapes2D shape2D : Shapes2D.values() )
        {
            node2DShapeName = splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(shape2D);
            node2DShape.addItem(node2DShapeName);
        }
        node2DShape.addItem(""); // dummy empty shape for the select-multiple-2D-nodes case
        node2DShape.setActionCommand(CHANGE_ACTION_COMMAND_NODES);
        node2DShape.addActionListener(this);
        node2DShape.setToolTipText("2D Shape");

        node3DShape = new JComboBox<String>();
        String node3DShapeName = "";
        for ( Shapes3D shape3D : Shapes3D.values() )
        {
            if ( shape3D.equals(Shapes3D.LATHE_3D) )
                    node3DShapeName = splitAndCapitalizeFirstCharactersForAllButLastName(shape3D);
            else if ( shape3D.equals(Shapes3D.SUPER_QUADRIC) )
                node3DShapeName = splitAndCapitalizeFirstCharacters(shape3D);
            else if ( shape3D.equals(Shapes3D.OBJ_MODEL_LOADER) )
                node3DShapeName = splitCapitalizeFirstCharactersForAllButFirstNameAndAddWhiteSpaceBetweenNames(shape3D);
            else
                node3DShapeName = splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(shape3D);

            node3DShape.addItem(node3DShapeName);
        }
        node3DShape.addItem(""); // dummy empty shape for the select-multiple-3D-nodes case
        node3DShape.setActionCommand(CHANGE_ACTION_COMMAND_NODES);
        node3DShape.addActionListener(this);
        node3DShape.setToolTipText("3D Shape");

        nodeTransparency = new JCheckBox("Transparency");
        nodeTransparency.setActionCommand(CHANGE_ACTION_COMMAND_NODES);
        nodeTransparency.addActionListener(this);
        nodeTransparency.setToolTipText("Transparency");

        nodeTransparencyAlphaSlider = new SimpleSlider(7, 80, 75, 0.0f, 0.0f, 1.0f, 20, "Alpha", false);
        nodeTransparencyAlphaSlider.setActionCommand(CHANGE_ACTION_COMMAND_NODES);
        nodeTransparencyAlphaSlider.addActionListener(this);
        nodeTransparencyAlphaSlider.setToolTipText("Alpha");

        nodeColorButton = new ColorButton(" ");
        nodeColorButton.setActionCommand(CHANGE_ACTION_COMMAND_NODES);
        nodeColorButton.addActionListener(this);
        nodeColorButton.setToolTipText("Color");

        nodeSizeComboBox = new JComboBox<Integer>();
        nodeSizeComboBox.setActionCommand(CHANGE_ACTION_COMMAND_NODES);
        nodeSizeComboBox.addActionListener(this);
        nodeSizeComboBox.setToolTipText("Node Size");

        for (int i = MIN_NODE_SIZE; i <= MAX_NODE_SIZE; i++)
            nodeSizeComboBox.addItem(i);

        JButton lathe3DViewer = new JButton("Lathe3D Shape Editor");
        lathe3DViewer.setAction( modelShapeEditorParentUIDialog.getModelShapeLathe3DAction() );
        lathe3DViewer.setToolTipText("Lathe3D Interactive Shape Editor");

        JButton superQuadricViewer = new JButton("SuperQuadric Shape Editor");
        superQuadricViewer.setAction( modelShapeEditorParentUIDialog.getModelShapeSuperQuadricAction() );
        superQuadricViewer.setToolTipText("SuperQuadric Interactive Shape Editor");

        JButton objModelLoaderViewer = new JButton("OBJ Model Loader Shape Viewer");
        objModelLoaderViewer.setAction( modelShapeEditorParentUIDialog.getModelShapeOBJModelLoaderAction() );
        objModelLoaderViewer.setToolTipText("OBJ Model Loader Interactive Shape Editor");

        JPanel nodeSettingsPanel = new JPanel(true);
        nodeSettingsPanel.setLayout( new BoxLayout(nodeSettingsPanel, BoxLayout.Y_AXIS) );

        JPanel nodeSettingsSubPanel1 = new JPanel(true);
        nodeSettingsSubPanel1.add(node2DShape);
        nodeSettingsSubPanel1.add( new JLabel("2D Shape") );
        nodeSettingsSubPanel1.add( Box.createRigidArea( new Dimension(20, 10) ) );
        nodeSettingsSubPanel1.add(node3DShape);
        nodeSettingsSubPanel1.add( new JLabel("3D Shape") );
        nodeSettingsSubPanel1.add( Box.createRigidArea( new Dimension(20, 10) ) );
        nodeSettingsSubPanel1.add(nodeTransparency);
        nodeSettingsSubPanel1.add( Box.createRigidArea( new Dimension(5, 10) ) );
        nodeSettingsSubPanel1.add(nodeTransparencyAlphaSlider);
        nodeSettingsSubPanel1.add( Box.createRigidArea( new Dimension(20, 10) ) );
        nodeSettingsSubPanel1.add(nodeRevertOverride);
        nodeSettingsSubPanel1.add( Box.createRigidArea( new Dimension(10, 10) ) );
        nodeSettingsSubPanel1.add(nodeColorButton);
        nodeSettingsSubPanel1.add( new JLabel("Color") );

        // JPanel nodeSettingsSubPanel2 = new JPanel(new FlowLayout(FlowLayout.LEADING), true);
        JPanel nodeSettingsSubPanel2 = new JPanel(true);
        nodeSettingsSubPanel2.add( new JLabel("Customize Shape:") );
        nodeSettingsSubPanel2.add(lathe3DViewer);
        nodeSettingsSubPanel2.add( Box.createRigidArea( new Dimension(20, 10) ) );
        nodeSettingsSubPanel2.add( new JLabel("Customize Shape:") );
        nodeSettingsSubPanel2.add(superQuadricViewer);
        nodeSettingsSubPanel2.add( Box.createRigidArea( new Dimension(20, 10) ) );
        nodeSettingsSubPanel2.add( new JLabel("Customize Shape:") );
        nodeSettingsSubPanel2.add(objModelLoaderViewer);

        nodeSettingsPanel.add(nodeSettingsSubPanel1);
        nodeSettingsPanel.add( Box.createRigidArea( new Dimension(10, 30) ) );
        nodeSettingsPanel.add(nodeSettingsSubPanel2);

        JPanel nodeSizePanel = new JPanel(true);
        nodeSizePanel.add(nodeSizeComboBox);
        nodeSizePanel.add( new JLabel("Node Size") );
        nodeSizePanel.add( Box.createRigidArea( new Dimension(50, 10) ) );
        nodeSizePanel.add(graphmlViewNodeDepthPositioningTextField);
        nodeSizePanel.add(graphmlViewNodeDepthZPositioningLabel);
        nodeSizePanel.add( Box.createRigidArea( new Dimension(25, 10) ) );
        nodeSizePanel.add(graphmlAllViewNodeDepthResetPositioningsButton);

        nodePropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "Node Identifier");
        addTitledButtonBorder(nodePropertiesPanelBorder, nodeNameTextField, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, panel);

        nodePropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "2D & 3D Node Shape, Transparency & Color (Overrides Class Colors)");
        addTitledButtonBorder(nodePropertiesPanelBorder, nodeSettingsPanel, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, panel);

        nodePropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "Node Size & Graphml View Node Depth (Z Coordinate) Positioning");
        addTitledButtonBorder(nodePropertiesPanelBorder, nodeSizePanel, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, panel);

        nodePropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "Node Class");
        addTitledButtonBorder(nodePropertiesPanelBorder, nodeClassComboBox, "Containing Class", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, panel);

        nodePropertiesPanelBorder = BorderFactory.createTitledBorder(EMPTY, "");
        addTitledButtonBorder(nodePropertiesPanelBorder, new JLabel(""), "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, panel);

        addCommandButtonsToTab(panel, tabNumber);
        tabbedPane.addTab("Nodes", null, panel, "Nodes Properties");
    }

    private void createEdgesPropertiesTab(JPanel panel, JTabbedPane tabbedPane, int tabNumber)
    {
        TitledBorder edgePropertiesPanelBorder = null;

        edgesColor = new ColorButton("");
        edgesColor.addActionListener(this);
        edgesColor.setToolTipText("Edge Color");
        edgeThicknessComboBox = new JComboBox<String>();
        edgeThicknessComboBox.setToolTipText("Edge Thickness");
        proportionalEdgesSizeToWeight = new JCheckBox("Proportional Edges Size To Weight");
        proportionalEdgesSizeToWeight.setToolTipText("Proportional Edges Size To Weight");
        arrowHeadSizeComboBox = new JComboBox<String>();
        arrowHeadSizeComboBox.setToolTipText("Arrowhead Size");
        ButtonGroup edgeColorSource = new ButtonGroup();
        edgesColorByColor = new JRadioButton();
        edgesColorByColor.setToolTipText("Color");
        edgesColorByWeight = new JRadioButton();
        edgesColorByWeight.setToolTipText("Weight");

        edgeColorSource.add(edgesColorByColor);
        edgeColorSource.add(edgesColorByWeight);

        // Make sure these fire events to change edge display on application
        edgeThicknessComboBox.setActionCommand(CHANGE_ACTION_COMMAND_EDGES);
        proportionalEdgesSizeToWeight.setActionCommand(CHANGE_ACTION_COMMAND_EDGES);
        arrowHeadSizeComboBox.setActionCommand(CHANGE_ACTION_COMMAND_EDGES);
        edgesColor.setActionCommand(CHANGE_ACTION_COMMAND_EDGES);
        edgesColorByColor.setActionCommand(CHANGE_ACTION_COMMAND_EDGES);
        edgesColorByWeight.setActionCommand(CHANGE_ACTION_COMMAND_EDGES);
        edgeThicknessComboBox.addActionListener(this);
        proportionalEdgesSizeToWeight.addActionListener(this);
        arrowHeadSizeComboBox.addActionListener(this);
        edgesColorByColor.addActionListener(this);
        edgesColorByWeight.addActionListener(this);

        proportionalEdgesSizeToWeight.setEnabled(false);

        JPanel edgeThicknessPanel = new JPanel(true);
        edgeThicknessPanel.setLayout( new BoxLayout(edgeThicknessPanel, BoxLayout.Y_AXIS) );

        edgePropertiesPanelBorder = BorderFactory.createTitledBorder(EMPTY, "");
        addTitledButtonBorder(edgePropertiesPanelBorder, edgeThicknessComboBox, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, edgeThicknessPanel);
        addTitledButtonBorder(edgePropertiesPanelBorder, proportionalEdgesSizeToWeight, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, edgeThicknessPanel);

        JPanel edgesSmallPanel = new JPanel(true);
        edgesSmallPanel.setLayout( new BoxLayout(edgesSmallPanel, BoxLayout.Y_AXIS) );

        edgePropertiesPanelBorder = BorderFactory.createTitledBorder(EMPTY, "");
        addTitledButtonBorder(edgePropertiesPanelBorder, edgesColorByColor, "Color", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, edgesSmallPanel);
        addTitledButtonBorder(edgePropertiesPanelBorder, edgesColorByWeight, "Weight", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, edgesSmallPanel);

        edgePropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "Edge Color");
        addTitledButtonBorder(edgePropertiesPanelBorder, edgesColor, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, panel);

        edgePropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "Edge Thickness");
        addTitledButtonBorder(edgePropertiesPanelBorder, edgeThicknessPanel, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, panel);

        edgePropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "Arrowhead Size (2D graphs only)");
        addTitledButtonBorder(edgePropertiesPanelBorder, arrowHeadSizeComboBox, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, panel);

        edgePropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "Color Edges By");
        addTitledButtonBorder(edgePropertiesPanelBorder, edgesSmallPanel, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, panel);

        JPanel spacer = new JPanel(true);
        spacer.add( Box.createRigidArea( new Dimension(10, 20) ) );
        edgePropertiesPanelBorder = BorderFactory.createTitledBorder(EMPTY, "");
        addTitledButtonBorder(edgePropertiesPanelBorder, spacer, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, panel);

        addCommandButtonsToTab(panel, tabNumber);
        tabbedPane.addTab("Edges", null, panel, "Edges Properties");
    }

    private void createClassesPropertiesTab(JPanel panel, JTabbedPane tabbedPane, int tabNumber)
    {
        TitledBorder classesPropertiesPanelBorder = null;

        classesChooser = new JComboBox<String>();
        for ( LayoutClasses classes : layoutClassSetsManager.getClassSetNames() )
        {
            if (DEBUG_BUILD) println("Adding: " + classes.getClassSetName() );
            classesChooser.addItem( classes.getClassSetName() );
        }

        classesChooser.setSelectedIndex( layoutClassSetsManager.getCurrentClassSetID() );
        classesChooser.addActionListener(this);
        classesChooser.setActionCommand(CHANGE_ACTION_COMMAND);
        classesChooser.setToolTipText("Select Current Class Set");

        newClassSetButton = new JButton("Add Class Set");
        newClassSetButton.addActionListener(this);
        newClassSetButton.setToolTipText("Add Class Set");

        JPanel nodeClassPanel = new JPanel(true);
        nodeClassPanel.add( new JLabel("Select Current Class Set:") );
        nodeClassPanel.add(classesChooser);
        nodeClassPanel.add( Box.createRigidArea( new Dimension(5, 5) ) );
        nodeClassSetName = new JTextField("", 10);
        nodeClassSetName.setToolTipText("Create Class Set");
        nodeClassPanel.add( new JLabel("Create Class Set:") );
        nodeClassPanel.add(nodeClassSetName);
        nodeClassPanel.add(newClassSetButton);

        JPanel layoutClassesTablePanel = new JPanel(true);
        nodeClassName = new JTextField("", 10);
        nodeClassName.setToolTipText("Create Class");
        newClassInClassSetButton = new JButton("Add Class");
        newClassInClassSetButton.addActionListener(this);
        newClassInClassSetButton.setToolTipText("Add Class");

        layoutClassesTablePanel.setLayout( new BorderLayout() );

        nodeClassPanel.add( Box.createRigidArea( new Dimension(5, 5) ) );
        nodeClassPanel.add( new JLabel("Create Class:") );
        nodeClassPanel.add(nodeClassName);
        nodeClassPanel.add(newClassInClassSetButton);

        if (layoutClassSetsManager.getCurrentClassSetAllClasses().getTotalClasses() >= 1)
        {
            layoutClassesTable = new LayoutClassesTable(layoutClassSetsManager.getCurrentClassSetAllClasses(), this);
            layoutClassesTable.setListener(this);

            classesPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "Class Set Browser");
            addTitledButtonBorderLarge(classesPropertiesPanelBorder, layoutClassesTable, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, layoutClassesTablePanel);
        }

        classesPropertiesPanelBorder = BorderFactory.createTitledBorder(ETCHED, "Select Current or Create New Class Set & Classes");
        addTitledButtonBorder(classesPropertiesPanelBorder, nodeClassPanel, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, panel);

        panel.add(layoutClassesTablePanel, BorderLayout.CENTER);

        addCommandButtonsToTab(panel, tabNumber);
        tabbedPane.addTab("Classes", null, panel, "Classes Properties");
    }

    @Override
    public void classColorHasChanged()
    {
        refreshClassViewer = true;
        generalChange = true;
        setEnabledAllApplyButtons(true);
    }

    @Override
    public void caretUpdate(CaretEvent ce)
    {
        if( ce.getSource().equals(graphmlViewNodeDepthPositioningTextField) )
        {
            _3DRebuildEdges = true;
            _3DRebuildNodes = true;
        }

        generalChange = true;
        setEnabledAllApplyButtons(true);
    }

    @Override
    public void updateGraphAnaglyphGlasses3DOptionsDialogPreferencesCallBack(GraphAnaglyphGlassesTypes graphAnaglyphGlassesType, GraphIntraOcularDistanceTypes graphIntraOcularDistanceType)
    {
        this.graphAnaglyphGlassesType = graphAnaglyphGlassesType;
        this.graphIntraOcularDistanceType = graphIntraOcularDistanceType;

        generalChange = true;
        updateGraphAnaglyphGlasses3DOptionsDialogPreferencesCallBackChange = true;
        setEnabledAllApplyButtons(true);
    }

    @Override
    public void updateModelShapeEditorParentUIDialogPreferencesCallBack(final ModelTypes modelType)
    {
        this.modelType = modelType;

        node3DShape.removeActionListener(this);
        if ( modelType.equals(LATHE3D_SHAPE) )
        {
            node3DShape.setSelectedIndex( Shapes3D.LATHE_3D.ordinal() );
        }
        else if ( modelType.equals(SUPER_QUADRIC_SHAPE) )
        {
            node3DShape.setSelectedIndex( Shapes3D.SUPER_QUADRIC.ordinal() );
        }
        else // if ( modelType.equals(OBJ_MODEL_LOADER_SHAPE) )
        {
            node3DShape.setSelectedIndex( Shapes3D.OBJ_MODEL_LOADER.ordinal() );
        }
        node3DShape.addActionListener(this);
        node3DShapeChange = true;
        MANUAL_SHAPE_3D = false;

        generalChange = true;
        _3DRebuildNodes = true;
        updateModelShapeEditorParentUIDialogPreferencesCallBackChange = true;
        setEnabledAllApplyButtons(true);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (DEBUG_BUILD) println("\nEvent:" + e.getActionCommand() + "\n" + e.getSource());

        if (watchForChanges)
        {
            if ( e.getActionCommand().contains(CHANGE_ACTION_COMMAND) )
            {
                generalChange = true;
                setEnabledAllApplyButtons(true);

                if ( e.getActionCommand().equals(CHANGE_ACTION_COMMAND_NODES) )
                {
                    _3DRebuildNodes = true;
                }
                else if ( e.getActionCommand().equals(CHANGE_ACTION_COMMAND_EDGES) )
                {
                    _3DRebuildEdges = true;
                }
                else if ( e.getActionCommand().equals(CHANGE_ACTION_COMMAND_ALL) )
                {
                    _3DRebuildEdges = true;
                    _3DRebuildNodes = true;
                }
            }

            if ( !tabbedPane.getSelectedComponent().equals(classesPropertiesPanel) )
            {
                if ( e.getSource().equals(nodeClassComboBox) )
                {
                    if ( nodeClassComboBox.getSelectedItem().equals( Integer.valueOf(-1) ) )
                    {
                        isChangingClasses = true;
                        tabbedPane.setSelectedComponent(classesPropertiesPanel);
                    }
                    else
                    {
                        nodeClassChange = true;
                    }
                }
            }

            if ( e.getSource().equals(nodeSizeComboBox) )
            {
                nodeSizeChange = true;
            }
            else if ( e.getSource().equals(nodeColorButton) )
            {
                nodeColorChange = true;
            }
            else if ( e.getSource().equals(node2DShape) )
            {
                node2DShapeChange = true;
                MANUAL_SHAPE_2D = false;
            }
            else if ( e.getSource().equals(node3DShape) )
            {
                int index = node3DShape.getSelectedIndex();
                if ( ( index == Shapes3D.LATHE_3D.ordinal() ) )
                    modelShapeEditorParentUIDialog.getModelShapeLathe3DAction().actionPerformed( new ActionEvent( this, index, Shapes3D.LATHE_3D.toString() ) );
                else if ( ( index == Shapes3D.SUPER_QUADRIC.ordinal() ) )
                    modelShapeEditorParentUIDialog.getModelShapeSuperQuadricAction().actionPerformed( new ActionEvent( this, index, Shapes3D.SUPER_QUADRIC.toString() ) );
                else if ( ( index == Shapes3D.OBJ_MODEL_LOADER.ordinal() ) )
                    modelShapeEditorParentUIDialog.getModelShapeOBJModelLoaderAction().actionPerformed( new ActionEvent( this, index, Shapes3D.OBJ_MODEL_LOADER.toString() ) );

                node3DShapeChange = true;
                MANUAL_SHAPE_3D = false;
            }
            else if ( e.getSource().equals(graphmlAllViewNodeDepthResetPositioningsButton) )
            {
                int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to Reset all the Depth Positioning Values ?", "Reset All Depth Positioning Values", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION)
                {
                    graphmlViewNodeDepthPositioningTextField.setText("0.0");
                    nc.getGraphmlNetworkContainer().resetAllGraphmlNodesMapCoordsDepthZ();
                }
            }
            else if ( e.getSource().equals(nodeTransparency) || nodeTransparencyAlphaSlider.checkSourceFromActionEvent(e) )
            {
                if ( e.getSource().equals(nodeTransparency) )
                    nodeTransparencyAlphaSlider.setEnabled( nodeTransparency.isSelected() ); // disable transparency alpha value if transparency turned off

                node3DTransparencyAlphaChange = true;
            }
            else if ( e.getSource().equals(anaglyphStereoscopic3DView) )
            {
                boolean flag = anaglyphStereoscopic3DView.isSelected();
                graphAnaglyphGlasses3DOptionsDialog.getGraphAnaglyphGlasses3DOptionsOpenDialogAction().setEnabled(flag);
                anaglyphStereoscopic3DViewOptions.setEnabled(flag);

                if ( nc.getIsGraphml() ) CHANGE_GRAPHML_COMPONENT_CONTAINERS = true; // to re-color the Component Containers to grayscale or back to normal colors
            }
            else if ( e.getSource().equals(materialSpecular) )
            {
                materialShininess.setEnabled( materialSpecular.isSelected() ); // disable shininess value if specular turned off

                if (USE_SHADERS_PROCESS)
                {
                    boolean flag = materialSpecular.isSelected();

                    setEnabledAllShadingSFXsUIs(flag); // disable all shadings if specular turned off
                    materialEmbossNodeTexture.setEnabled(flag); // disable all shadings if specular turned off
                    materialAntiAliasShading.setEnabled(flag); // disable all shadings if specular turned off
                    materialAnimatedShading.setEnabled(flag); // disable all shadings if specular turned off
                    materialStateShading.setEnabled(flag); // disable all shadings if specular turned off
                    materialOldLCDStyleTransparencyShading.setEnabled(flag); // disable all shadings if specular turned off
                    materialErosionShading.setEnabled(flag); // disable all shadings if specular turned off
                    materialNormalsSelectionMode.setEnabled(false); // disable all shadings if specular turned off
                }
            }
        }

        if ( e.getSource().getClass().equals(ColorButton.class) )
        {
            ColorButton.showColorChooser( (ColorButton)e.getSource(), this );

            if ( e.getSource().equals(generalColorPlotBackground) || e.getSource().equals(generalColorPlotGridlines) ) // update Class Viewer
                refreshClassViewer = true;
        }
        else if ( e.getSource().equals(newClassInClassSetButton) )
        {
            if (DEBUG_BUILD) println("New Class In Current Class Set");

            String className = nodeClassName.getText();
            String classSetName = nodeClassSetName.getText();
            if ( !className.isEmpty() )
            {
                String[] allClassNames = nodeClassName.getText().split(",+");
                boolean refresh = false;
                VertexClass newClass = null;
                for (String name : allClassNames)
                {
                    if ( !name.isEmpty() )
                    {
                        refresh = true;
                        className = name;
                        newClass = layoutClassSetsManager.getCurrentClassSetAllClasses().createClass(layoutClassSetsManager.getCurrentClassSetAllClasses().getTotalClasses() + 1, className);
                    }
                }

                if (refresh)
                {
                    nodeClassComboBox.updateClasses( layoutClassSetsManager.getCurrentClassSetAllClasses() );

                    refreshClasses();

                    if (isChangingClasses)
                    {
                        tabbedPane.setSelectedComponent(nodesPropertiesPanel);
                        nodeClassComboBox.setSelectedItem(newClass);
                        isChangingClasses = false;
                    }

                    // keep the Class Set / Class inserted names in textboxes
                    // nodeClassName.setText(className);
                    nodeClassSetName.setText(classSetName);

                    // inform user for Class addition
                    layoutFrame.getClassLegendTableModel().updateClassLegend( layoutFrame.getLayoutClassSetsManager().getCurrentClassSetAllClasses() );
                }
            }
            else
                JOptionPane.showMessageDialog(this, "Please provide a valid Class name to be added.", "No Class name provided!", JOptionPane.INFORMATION_MESSAGE);
        }
        else if ( e.getSource().equals(newClassSetButton) )
        {
            if (DEBUG_BUILD) println("Create New Class Set in Graph Properties");

            // String classSetName = "Class Set " + (layoutClassSetsManager.getTotalSets() + 1); // old way of doing things
            String className = nodeClassName.getText();
            String classSetName = nodeClassSetName.getText().replaceAll(",+", ""); // replace any commas
            if ( !classSetName.isEmpty() )
            {
                boolean classSetIsNew = true;
                for ( LayoutClasses classes : layoutClassSetsManager.getClassSetNames() )
                {
                    if ( classes.getClassSetName().equals(classSetName) )
                    {
                        classSetIsNew = false;
                        break;
                    }
                }

                if (classSetIsNew)
                {
                    layoutClassSetsManager.createNewClassSet(classSetName);
                    classesChooser.addItem(classSetName);

                    // inform user for Class Set addition
                    if (!isChangingClassSet)
                        setClassSetInComboBoxAndRefreshDisplay(classSetName);

                    // keep the Class Set / Class inserted names in textboxes
                   nodeClassName.setText(className);
                   // nodeClassSetName.setText(classSetName);

                    JOptionPane.showMessageDialog(this, " Class Set '" + classSetName + "' added.", "Class Set added!", JOptionPane.INFORMATION_MESSAGE);
                }
                else
                    JOptionPane.showMessageDialog(this, " Class Set '" + classSetName + "' exists already and cannot be re-added.", "Class Set exists already!", JOptionPane.WARNING_MESSAGE);
            }
            else
                JOptionPane.showMessageDialog(this, "Please provide a valid Class Set name to be added.", "No Class Set name provided!", JOptionPane.INFORMATION_MESSAGE);
        }
        else if ( e.getSource().equals(classesChooser) )
        {
            if ( e.getActionCommand().equals(CHANGE_ACTION_COMMAND) )
            {
                String className = nodeClassName.getText();
                String classSetName = nodeClassSetName.getText();

                if (!isChangingClassSet)
                    setClassSetInComboBoxAndRefreshDisplay( (String)classesChooser.getSelectedItem() );

                // keep the Class Set / Class inserted names in textboxes
                nodeClassName.setText(className);
                nodeClassSetName.setText(classSetName);
            }
        }
        else if ( e.getSource().equals(nodeRevertOverride) )
        {
            for ( GraphNode graphNode : layoutFrame.getGraph().getSelectionManager().getSelectedNodes() )
            {
                // if (graphNode.getVertexClass().getClassID() != 0)
                // {
                graphNode.removeColorOverride();
                // }
            }

            nodeColorButton.isBlank = true;
            nodeColorButton.setBackground(null);
        }
        else if ( e.getSource().equals(searchURLComboBox) )
        {
            presetRadioButton.setSelected(true);
            presetURLTextField.setText( ( (SearchURL)searchURLComboBox.getSelectedItem() ).getUrl() );
        }
        else if ( e.getSource().equals(show3DEnvironmentMapping) )
        {
            if ( show3DEnvironmentMapping.isSelected() )
            {
                if ( !materialSphericalMapping.isSelected() )
                {
                    sphericalMappingWasNotSelected = true;
                    materialSphericalMapping.setSelected(true);
                }
                else
                    sphericalMappingWasNotSelected = false;

                if ( !nodeSurfaceImageTextureCheckBox.isSelected() )
                {
                    texturingWasNotSelected = true;
                    nodeSurfaceImageTextureCheckBox.setSelected(true);
                }
                else
                    texturingWasNotSelected = false;

                materialSphericalMapping.setEnabled(false);
                nodeSurfaceImageTextureCheckBox.setEnabled(false);
            }
            else
            {
                materialSphericalMapping.setSelected(!sphericalMappingWasNotSelected);
                materialSphericalMapping.setEnabled(true);

                nodeSurfaceImageTextureCheckBox.setSelected(!texturingWasNotSelected);
                nodeSurfaceImageTextureCheckBox.setEnabled(true);

                // make sure the spectrum texture is updated when the spherical mapping state is disabled
                ANIMATION_CHANGE_SPECTRUM_TEXTURE_ENABLED = true;
            }

            CHANGE_SPHERICAL_MAPPING_ENABLED = true;
        }
        else if ( e.getSource().equals(useMotionBlurForScene) )
        {
            motionBlurSize.setEnabled( useMotionBlurForScene.isSelected() );
        }
        else if ( e.getSource().equals(wireframeSelectionMode) )
        {
            materialNormalsSelectionMode.setEnabled(false);
        }
        else if ( e.getSource().equals(materialSphericalMapping) )
        {
            CHANGE_SPHERICAL_MAPPING_ENABLED = true;
        }
        else if ( e.getSource().equals(materialNormalsSelectionMode) )
        {
            if ( materialNormalsSelectionMode.isSelected() )
            {
                if ( !wireframeSelectionMode.isSelected() )
                {
                    wireframeSelectionModeWasNotSelected = true;
                    wireframeSelectionMode.setSelected(true);
                }
                else
                    wireframeSelectionModeWasNotSelected = false;

                wireframeSelectionMode.setEnabled(false);
            }
            else
            {
                wireframeSelectionMode.setSelected(!wireframeSelectionModeWasNotSelected);
                wireframeSelectionMode.setEnabled(true);
            }
        }
        else if ( e.getSource().equals(nodeSurfaceImageTextureCheckBox) )
        {
            if ( nodeSurfaceImageTextureCheckBox.isSelected() )
            {
                CHANGE_TEXTURE_ENABLED = true;

                if ( show3DEnvironmentMapping.isSelected() )
                    show3DEnvironmentMapping.setSelected(false);

                nodeSurfaceImageTextureComboBox.setEnabled( nodeSurfaceImageTextureFileTextField.getText().isEmpty() );
                nodeSurfaceImageTextureFileTextField.setEnabled( !nodeSurfaceImageTextureFileTextField.getText().isEmpty() );
                nodeSurfaceImageTextureFileLoadButton.setEnabled(true);
                nodeSurfaceImageTextureFileClearButton.setEnabled( !nodeSurfaceImageTextureFileTextField.getText().isEmpty() );
            }
            else
            {
                // make sure the spectrum texture is updated when the surface node texture is disabled
                ANIMATION_CHANGE_SPECTRUM_TEXTURE_ENABLED = true;

                nodeSurfaceImageTextureComboBox.setEnabled(false);
                nodeSurfaceImageTextureFileTextField.setEnabled(false);
                nodeSurfaceImageTextureFileLoadButton.setEnabled(false);
                nodeSurfaceImageTextureFileClearButton.setEnabled(false);
            }
        }
        else if ( e.getSource().equals(nodeSurfaceImageTextureComboBox) )
        {
            CHANGE_TEXTURE_ENABLED = true;
        }
        else if ( e.getSource().equals(nodeSurfaceImageTextureFileLoadButton) )
        {
            loadNodeSurfaceImageFile();
        }
        else if ( e.getSource().equals(nodeSurfaceImageTextureFileClearButton) )
        {
            clearNodeSurfaceImageFile();
        }
        else if ( isAllShadingSFXSActionEventEnabled(e) )
        {
            int index = indexOfAllShadingSFXSActionEventEnabled(e);
            boolean value = allShadings[index].isSelected();
            for (int i = 0; i < allShadings.length; i++)
                allShadings[i].setSelected(false);

            allShadings[index].setSelected(value);
            materialGouraudLighting.setEnabled(!value);
            materialEmbossNodeTexture.setEnabled(value);
            materialAntiAliasShading.setEnabled(value);
            materialAnimatedShading.setEnabled(value);
            materialStateShading.setEnabled(value);
            materialOldLCDStyleTransparencyShading.setEnabled(value);
            materialErosionShading.setEnabled(value);
            materialNormalsSelectionMode.setEnabled(false);
        }
        else if ( e.getSource().equals(parallelismUseExpressionCorrelationCalculationNCoreParallelism) )
        {
            parallelismUseExpressionCorrelationCalculationOpenCLGPUComputingIterationSizeLabel.setEnabled(false);
            parallelismUseExpressionCorrelationCalculationOpenCLGPUComputingIterationSize.setEnabled(false);
            parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputingTextureSizeAndTypeLabel.setEnabled(false);
            parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputingTextureSize.setEnabled(false);
            parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputingTextureType.setEnabled(false);
            parallelismUseGPUComputingExpressionCorrelationCalculationCompareGPUWithCPU.setEnabled(false);
            parallelismUseGPUComputingExpressionCorrelationCalculationCompareGPUWithCPUComparisonMethods.setEnabled(false);
            parallelismUseGPUComputingExpressionCorrelationCalculationCompareGPUWithCPUComparisonMethodsLabel.setEnabled(false);
        }
        else if ( e.getSource().equals(parallelismUseExpressionCorrelationCalculationOpenCLGPUComputing) )
        {
            parallelismUseExpressionCorrelationCalculationOpenCLGPUComputingIterationSizeLabel.setEnabled(true);
            parallelismUseExpressionCorrelationCalculationOpenCLGPUComputingIterationSize.setEnabled(true);
            parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputingTextureSizeAndTypeLabel.setEnabled(false);
            parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputingTextureSize.setEnabled(false);
            parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputingTextureType.setEnabled(false);
            parallelismUseGPUComputingExpressionCorrelationCalculationCompareGPUWithCPU.setEnabled(true);
            if ( parallelismUseGPUComputingExpressionCorrelationCalculationCompareGPUWithCPU.isSelected() )
            {
                parallelismUseGPUComputingExpressionCorrelationCalculationCompareGPUWithCPUComparisonMethods.setEnabled(true);
                parallelismUseGPUComputingExpressionCorrelationCalculationCompareGPUWithCPUComparisonMethodsLabel.setEnabled(true);
            }
        }
        else if ( e.getSource().equals(parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputing) )
        {
            parallelismUseExpressionCorrelationCalculationOpenCLGPUComputingIterationSizeLabel.setEnabled(false);
            parallelismUseExpressionCorrelationCalculationOpenCLGPUComputingIterationSize.setEnabled(false);
            parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputingTextureSizeAndTypeLabel.setEnabled(true);
            parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputingTextureSize.setEnabled(true);
            parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputingTextureType.setEnabled(true);
            parallelismUseGPUComputingExpressionCorrelationCalculationCompareGPUWithCPU.setEnabled(true);
            if ( parallelismUseGPUComputingExpressionCorrelationCalculationCompareGPUWithCPU.isSelected() )
            {
                parallelismUseGPUComputingExpressionCorrelationCalculationCompareGPUWithCPUComparisonMethods.setEnabled(true);
                parallelismUseGPUComputingExpressionCorrelationCalculationCompareGPUWithCPUComparisonMethodsLabel.setEnabled(true);
            }
        }
        else if ( e.getSource().equals(parallelismUseGPUComputingExpressionCorrelationCalculationCompareGPUWithCPU) )
        {
            boolean flag = parallelismUseGPUComputingExpressionCorrelationCalculationCompareGPUWithCPU.isSelected();
            parallelismUseGPUComputingExpressionCorrelationCalculationCompareGPUWithCPUComparisonMethods.setEnabled(flag);
            parallelismUseGPUComputingExpressionCorrelationCalculationCompareGPUWithCPUComparisonMethodsLabel.setEnabled(flag);

        }
        else if ( e.getSource().equals(parallelismUseLayoutNCoreParallelism) )
        {
            parallelismUseIndices1DKernelWithIterationsForLayoutOpenCLGPUComputing.setEnabled(false);
            parallelismUseLayoutOpenCLGPUComputingIterationSizeLabel.setEnabled(false);
            parallelismUseLayoutOpenCLGPUComputingIterationSize.setEnabled(false);
            parallelismUseGPUComputingLayoutCompareGPUWithCPU.setEnabled(false);
            parallelismUseGPUComputingLayoutCompareGPUWithCPUComparisonMethods.setEnabled(false);
            parallelismUseGPUComputingLayoutCompareGPUWithCPUComparisonMethodsLabel.setEnabled(false);
            parallelismUseAtomicSynchronizationForLayoutNCoreParallelism.setEnabled( parallelismUseLayoutNCoreParallelism.isSelected() );
        }
        else if ( e.getSource().equals(parallelismUseLayoutOpenCLGPUComputing) )
        {
            parallelismUseIndices1DKernelWithIterationsForLayoutOpenCLGPUComputing.setEnabled(true);
            if ( parallelismUseIndices1DKernelWithIterationsForLayoutOpenCLGPUComputing.isSelected() )
            {
                parallelismUseLayoutOpenCLGPUComputingIterationSizeLabel.setEnabled(true);
                parallelismUseLayoutOpenCLGPUComputingIterationSize.setEnabled(true);
            }
            parallelismUseGPUComputingLayoutCompareGPUWithCPU.setEnabled(true);
            parallelismUseAtomicSynchronizationForLayoutNCoreParallelism.setEnabled( parallelismUseLayoutOpenCLGPUComputing.isSelected() );
            if ( parallelismUseGPUComputingLayoutCompareGPUWithCPU.isSelected() )
            {
                parallelismUseGPUComputingLayoutCompareGPUWithCPUComparisonMethods.setEnabled(true);
                parallelismUseGPUComputingLayoutCompareGPUWithCPUComparisonMethodsLabel.setEnabled(true);
            }
        }
        else if ( e.getSource().equals(parallelismUseIndices1DKernelWithIterationsForLayoutOpenCLGPUComputing) )
        {
            boolean flag = parallelismUseIndices1DKernelWithIterationsForLayoutOpenCLGPUComputing.isSelected();
            parallelismUseLayoutOpenCLGPUComputingIterationSizeLabel.setEnabled(flag);
            parallelismUseLayoutOpenCLGPUComputingIterationSize.setEnabled(flag);
        }
        else if ( e.getSource().equals(parallelismUseGPUComputingLayoutCompareGPUWithCPU) )
        {
            boolean flag = parallelismUseGPUComputingLayoutCompareGPUWithCPU.isSelected();
            parallelismUseGPUComputingLayoutCompareGPUWithCPUComparisonMethods.setEnabled(flag);
            parallelismUseGPUComputingLayoutCompareGPUWithCPUComparisonMethodsLabel.setEnabled(flag);

        }
        else if ( e.getSource().equals(parallelismUseAtomicSynchronizationForLayoutNCoreParallelism) )
        {
            final String[] SELECTED_LAYOUT_GPU_COMPUTING_CPU_COMPARISON_METHODS = (!USE_MULTICORE_PROCESS)
                                                                                  ? ( new String[]{ GPU_COMPUTING_CPU_COMPARISON_METHODS[0] } )
                                                                                  : ( new String[]{ GPU_COMPUTING_CPU_COMPARISON_METHODS[0], GPU_COMPUTING_CPU_COMPARISON_METHODS[2] } );

            parallelismUseGPUComputingLayoutCompareGPUWithCPUComparisonMethods.removeAllItems();
            for (int i = 0;i < SELECTED_LAYOUT_GPU_COMPUTING_CPU_COMPARISON_METHODS.length; i++)
                parallelismUseGPUComputingLayoutCompareGPUWithCPUComparisonMethods.addItem(SELECTED_LAYOUT_GPU_COMPUTING_CPU_COMPARISON_METHODS[i]);
        }
        else if ( e.getSource().equals(saveSPNResultsAtFolderButton) )
        {
            saveSPNResultsAtFolder();
        }
        else if ( e.getSource().equals(saveSPNResultsClearButton) )
        {
            saveSPNResultsAtFolderClear();
        }
        else if ( e.getSource().equals(saveSPNResultsCheckBox) )
        {
            if ( saveSPNResultsCheckBox.isSelected() )
            {
                saveSPNResultsTextField.setEnabled(true);
                saveSPNResultsAtFolderButton.setEnabled(true);
                saveSPNResultsClearButton.setEnabled(true);
                automaticallySaveSPNResultsToPreChosenFolderCheckBox.setEnabled(true);
            }
            else
            {
                saveSPNResultsTextField.setEnabled(false);
                saveSPNResultsAtFolderButton.setEnabled(false);
                saveSPNResultsClearButton.setEnabled(false);
                automaticallySaveSPNResultsToPreChosenFolderCheckBox.setEnabled(false);
            }
        }
        else if ( e.getSource().equals(customRadioButton) )
        {
            if (DEBUG_BUILD) println("Using custom URL");

            customURLTextField.setEnabled(true);
        }
        else if ( e.getSource().equals(presetRadioButton) )
        {
            if (DEBUG_BUILD) println("Using preset URL");

            customURLTextField.setEnabled(false);
        }
        else if ( e.getSource().equals(generalDisableNodesRendering) || e.getSource().equals(generalDisableEdgesRendering) )
        {
            if ( generalDisableNodesRendering.isSelected() && generalDisableEdgesRendering.isSelected() )
                JOptionPane.showMessageDialog(this, "Both Nodes And Edges Rendering are disabled for the main graph view.\n" +
                                                    "Please disable either option to have a graph being visibly rendered.",
                                                    "Both Nodes And Edges Rendering Disabled",
                                                    JOptionPane.INFORMATION_MESSAGE);
        }
        else if ( e.getActionCommand().equals("OK") )
        {
            hasNewPreferencesBeenApplied = hasNewPreferencesBeenApplied || isEnabledAnyApplyButton();
            applyChanges();

            this.setVisible(false);
        }
        else if ( e.getActionCommand().equals("Cancel") )
        {
            this.setVisible(false);
        }
        else if ( e.getActionCommand().equals("Apply") )
        {
            hasNewPreferencesBeenApplied = true;
            applyChanges();
        }
    }

    private void loadNodeSurfaceImageFile()
    {
        if ( JFileChooser.APPROVE_OPTION == nodeSurfaceImageTextureFileChooser.showOpenDialog(this) )
        {
            nodeSurfaceImageTextureFileTextField.setText( nodeSurfaceImageTextureFileChooser.getSelectedFile().getPath() );
            nodeSurfaceImageTextureFileTextField.setToolTipText( nodeSurfaceImageTextureFileChooser.getSelectedFile().getPath() );
            nodeSurfaceImageTextureComboBox.setEnabled(false);
            nodeSurfaceImageTextureFileTextField.setEnabled(true);
            nodeSurfaceImageTextureFileClearButton.setEnabled(true);
            CHANGE_TEXTURE_ENABLED = true;

            USER_SURFACE_IMAGE_FILE = nodeSurfaceImageTextureFileChooser.getSelectedFile().getPath();
            FILE_CHOOSER_PATH.set( nodeSurfaceImageTextureFileChooser.getSelectedFile().getAbsolutePath() );
        }
    }

    private void clearNodeSurfaceImageFile()
    {
        nodeSurfaceImageTextureFileTextField.setText("");
        nodeSurfaceImageTextureFileTextField.setToolTipText("Loaded Node Surface Image Texture");
        nodeSurfaceImageTextureComboBox.setEnabled(true);
        nodeSurfaceImageTextureFileTextField.setEnabled(false);
        nodeSurfaceImageTextureFileClearButton.setEnabled(false);
        USER_SURFACE_IMAGE_FILE = "";
        CHANGE_TEXTURE_ENABLED = true;
    }

    private void saveSPNResultsAtFolder()
    {
        if ( JFileChooser.APPROVE_OPTION == saveSPNResultsFileChooser.showOpenDialog(this) )
        {
            SAVE_SPN_RESULTS_FILE_NAME.set( saveSPNResultsFileChooser.getSelectedFile().getPath() );
            saveSPNResultsTextField.setText( SAVE_SPN_RESULTS_FILE_NAME.get() );
        }
    }

    private void saveSPNResultsAtFolderClear()
    {
        SAVE_SPN_RESULTS_FILE_NAME.set("");
        saveSPNResultsTextField.setText("");
    }

    private void setClassSetInComboBoxAndRefreshDisplay(String classSetName)
    {
        layoutClassSetsManager.switchClassSet(classSetName);
        nodeClassComboBox.updateClasses( layoutClassSetsManager.getCurrentClassSetAllClasses() );
        isChangingClassSet = true;

        refreshClasses();

        layoutFrame.getClassViewerFrame().populateClassViewer();
        layoutFrame.getGraph().updateDisplayLists(_3DRebuildNodes, _3DRebuildEdges, _3DRebuildNodes);
        layoutFrame.getGraph().refreshDisplay();
        isChangingClassSet = false;
        layoutFrame.getClassLegendTableModel().updateClassLegend( layoutFrame.getLayoutClassSetsManager().getCurrentClassSetAllClasses() );
    }

    private void setEnabledAllApplyButtons(boolean value)
    {
        for (int i = 0; i < NUMBER_OF_TOTAL_TABS; i++)
            allApplyButtons[i].setEnabled(value);
    }

    private boolean isEnabledAnyApplyButton()
    {
        for (int i = 0; i < NUMBER_OF_TOTAL_TABS; i++)
            if ( allApplyButtons[i].isEnabled() )
                return true;

        return false;
    }

    private void applySearchProperties()
    {
        if ( presetRadioButton.isSelected() )
        {
            SEARCH_URL = (SearchURL) searchURLComboBox.getSelectedItem();
        }
        else
        {
            SearchURL customSearchURL = new SearchURL( customURLTextField.getText() );
            CUSTOM_SEARCH = true;
            SEARCH_URL = customSearchURL;
        }
    }

    private void readGeneralProperties()
    {
        generalColor.setBackground( BACKGROUND_COLOR.get() );
        generalColorSelection.setBackground( SELECTION_COLOR.get() );
        generalColorPlotBackground.setBackground( PLOT_BACKGROUND_COLOR.get() );
        generalColorPlotGridlines.setBackground( PLOT_GRIDLINES_COLOR.get() );
        generalTrippyBackground.setSelected( TRIPPY_BACKGROUND.get() );

        generalDirectional.setSelected( DIRECTIONAL_EDGES.get() );
        generalDragShowEdgesWhileDraggingNodes.setSelected( SHOW_EDGES_WHEN_DRAGGING_NODES.get() );
        generalyEdStyleRenderingForGraphmlFiles.setSelected( YED_STYLE_RENDERING_FOR_GPAPHML_FILES.get() );
        generalyEdStyleComponentContainersRenderingForGraphmlFiles.setSelected( YED_STYLE_COMPONENT_CONTAINERS_RENDERING_FOR_GPAPHML_FILES.get() );
        if ( generalyEdStyleRenderingForGraphmlFiles.isEnabled() )
            setEnabledGraphmlRenderingAndDepthRelatedOptions( generalyEdStyleRenderingForGraphmlFiles.isSelected() );

        if ( HIGH_QUALITY_ANTIALIASING.get() )
        {
            generalHighQualityAntiAlias.setSelected(true);
            generalNormalQualityAntiAlias.setSelected(false);
            generalNoAntiAlias.setSelected(false);
        }
        else if ( NORMAL_QUALITY_ANTIALIASING.get() )
        {
            generalHighQualityAntiAlias.setSelected(false);
            generalNormalQualityAntiAlias.setSelected(true);
            generalNoAntiAlias.setSelected(false);
        }
        else
        {
            generalHighQualityAntiAlias.setSelected(false);
            generalNormalQualityAntiAlias.setSelected(false);
            generalNoAntiAlias.setSelected(true);
        }

        useVSynch.setSelected( USE_VSYNCH.get() );

        generalDisableNodesRendering.setSelected( DISABLE_NODES_RENDERING.get() );
        generalDisableEdgesRendering.setSelected( DISABLE_EDGES_RENDERING.get() );
        generalUseInstallDirForScreenshots.setSelected( USE_INSTALL_DIR_FOR_SCREENSHOTS.get() );
        generalUseInstallDirForMCLTempFile.setSelected( USE_INSTALL_DIR_FOR_MCL_TEMP_FILE.get() );
        generalShowNavigationWizardOnStartup.setSelected( SHOW_NAVIGATION_WIZARD_ON_STARTUP.get() );
        generalShowLayoutIterations.setSelected( SHOW_LAYOUT_ITERATIONS.get() );
        generalValidateXMLFiles.setSelected( VALIDATE_XML_FILES.get() );
        generalShowGraphPropertiesToolBar.setSelected( SHOW_GRAPH_PROPERTIES_TOOLBAR.get() );
        generalShowNavigationToolBar.setSelected( SHOW_NAVIGATION_TOOLBAR.get() );
        generalShowPopupOverlayPlot.setSelected( SHOW_POPUP_OVERLAY_PLOT.get() );
        generalCollapseNodesByVolume.setSelected( COLLAPSE_NODES_BY_VOLUME.get() );
        generalConfirmPreferencesSave.setSelected( CONFIRM_PREFERENCES_SAVE.get() );
        parallelismUseExpressionCorrelationCalculationNCoreParallelism.setSelected( USE_EXRESSION_CORRELATION_CALCULATION_N_CORE_PARALLELISM.get() );
        parallelismUseExpressionCorrelationCalculationNCoreParallelism.setEnabled(USE_MULTICORE_PROCESS);
        parallelismUseExpressionCorrelationCalculationOpenCLGPUComputing.setSelected( USE_OPENCL_GPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION.get() );
        parallelismUseExpressionCorrelationCalculationOpenCLGPUComputing.setEnabled(OPENCL_GPU_COMPUTING_ENABLED);
        parallelismUseExpressionCorrelationCalculationOpenCLGPUComputingIterationSizeLabel.setEnabled(USE_OPENCL_GPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION.get() && OPENCL_GPU_COMPUTING_ENABLED);
        parallelismUseExpressionCorrelationCalculationOpenCLGPUComputingIterationSize.setSelectedItem( OPENCL_GPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION_ITERATION_SIZE.get() );
        parallelismUseExpressionCorrelationCalculationOpenCLGPUComputingIterationSize.setEnabled(USE_OPENCL_GPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION.get() && OPENCL_GPU_COMPUTING_ENABLED);
        parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputing.setSelected( USE_GLSL_GPGPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION.get() );
        parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputing.setEnabled(USE_SHADERS_PROCESS);
        parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputingTextureSizeAndTypeLabel.setEnabled(USE_GLSL_GPGPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION.get() && USE_SHADERS_PROCESS);
        parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputingTextureSize.setSelectedItem( GLSL_GPGPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION_TEXTURE_SIZE.get() );
        parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputingTextureSize.setEnabled(USE_GLSL_GPGPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION.get() && USE_SHADERS_PROCESS);
        parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputingTextureType.setSelectedItem( getGLSLTextureType( GLSL_GPGPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION_TEXTURE_TYPE.get() ) );
        parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputingTextureType.setEnabled(USE_GLSL_GPGPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION.get() && USE_SHADERS_PROCESS);
        parallelismUseGPUComputingExpressionCorrelationCalculationCompareGPUWithCPU.setSelected( COMPARE_GPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION_WITH_CPU.get() );
        parallelismUseGPUComputingExpressionCorrelationCalculationCompareGPUWithCPU.setEnabled( (USE_OPENCL_GPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION.get() && OPENCL_GPU_COMPUTING_ENABLED) || (USE_GLSL_GPGPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION.get() && USE_SHADERS_PROCESS) );
        parallelismUseGPUComputingExpressionCorrelationCalculationCompareGPUWithCPUComparisonMethodsLabel.setEnabled( parallelismUseGPUComputingExpressionCorrelationCalculationCompareGPUWithCPU.isEnabled() && COMPARE_GPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION_WITH_CPU.get() );
        parallelismUseGPUComputingExpressionCorrelationCalculationCompareGPUWithCPUComparisonMethods.setSelectedItem( COMPARE_GPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION_WITH_CPU_DEFAULT_COMPARISON_METHOD.get() );
        parallelismUseGPUComputingExpressionCorrelationCalculationCompareGPUWithCPUComparisonMethods.setEnabled( parallelismUseGPUComputingExpressionCorrelationCalculationCompareGPUWithCPU.isEnabled() && COMPARE_GPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION_WITH_CPU.get() );

        parallelismUseLayoutNCoreParallelism.setSelected( USE_LAYOUT_N_CORE_PARALLELISM.get() );
        parallelismUseLayoutNCoreParallelism.setEnabled(USE_MULTICORE_PROCESS);
        parallelismUseLayoutOpenCLGPUComputing.setSelected( USE_OPENCL_GPU_COMPUTING_LAYOUT_CALCULATION.get() );
        parallelismUseLayoutOpenCLGPUComputing.setEnabled(OPENCL_GPU_COMPUTING_ENABLED);
        parallelismUseIndices1DKernelWithIterationsForLayoutOpenCLGPUComputing.setSelected( USE_INDICES_1D_KERNEL_WITH_ITERATIONS_FOR_OPENCL_GPU_COMPUTING_LAYOUT_CALCULATION.get() );
        parallelismUseIndices1DKernelWithIterationsForLayoutOpenCLGPUComputing.setEnabled(USE_OPENCL_GPU_COMPUTING_LAYOUT_CALCULATION.get() && OPENCL_GPU_COMPUTING_ENABLED);
        parallelismUseLayoutOpenCLGPUComputingIterationSizeLabel.setEnabled(USE_INDICES_1D_KERNEL_WITH_ITERATIONS_FOR_OPENCL_GPU_COMPUTING_LAYOUT_CALCULATION.get() && USE_OPENCL_GPU_COMPUTING_LAYOUT_CALCULATION.get() && OPENCL_GPU_COMPUTING_ENABLED);
        parallelismUseLayoutOpenCLGPUComputingIterationSize.setSelectedItem( OPENCL_GPU_COMPUTING_LAYOUT_CALCULATION_ITERATION_SIZE.get() );
        parallelismUseLayoutOpenCLGPUComputingIterationSize.setEnabled(USE_INDICES_1D_KERNEL_WITH_ITERATIONS_FOR_OPENCL_GPU_COMPUTING_LAYOUT_CALCULATION.get() && USE_OPENCL_GPU_COMPUTING_LAYOUT_CALCULATION.get() && OPENCL_GPU_COMPUTING_ENABLED);
        parallelismUseGPUComputingLayoutCompareGPUWithCPU.setSelected( COMPARE_GPU_COMPUTING_LAYOUT_CALCULATION_WITH_CPU.get() );
        parallelismUseGPUComputingLayoutCompareGPUWithCPU.setEnabled(USE_OPENCL_GPU_COMPUTING_LAYOUT_CALCULATION.get() && OPENCL_GPU_COMPUTING_ENABLED);
        parallelismUseGPUComputingLayoutCompareGPUWithCPUComparisonMethodsLabel.setEnabled( parallelismUseGPUComputingLayoutCompareGPUWithCPU.isEnabled() && COMPARE_GPU_COMPUTING_LAYOUT_CALCULATION_WITH_CPU.get() );
        parallelismUseGPUComputingLayoutCompareGPUWithCPUComparisonMethods.setSelectedItem( COMPARE_GPU_COMPUTING_LAYOUT_CALCULATION_WITH_CPU_DEFAULT_COMPARISON_METHOD.get() );
        parallelismUseGPUComputingLayoutCompareGPUWithCPUComparisonMethods.setEnabled( parallelismUseGPUComputingLayoutCompareGPUWithCPU.isEnabled() && COMPARE_GPU_COMPUTING_LAYOUT_CALCULATION_WITH_CPU.get() );
        parallelismUseAtomicSynchronizationForLayoutNCoreParallelism.setSelected( USE_ATOMIC_SYNCHRONIZATION_FOR_LAYOUT_N_CORE_PARALLELISM.get() );
        parallelismUseAtomicSynchronizationForLayoutNCoreParallelism.setEnabled(USE_MULTICORE_PROCESS);

        GraphLayoutAlgorithm gla = GRAPH_LAYOUT_ALGORITHM.get();
        frRadioButton.setSelected(gla == GraphLayoutAlgorithm.FRUCHTERMAN_RHEINGOLD);
        fmmmRadioButton.setSelected(gla == GraphLayoutAlgorithm.FMMM);
        circleRadioButton.setSelected(gla == GraphLayoutAlgorithm.CIRCLE);
        askRadioButton.setSelected(gla == GraphLayoutAlgorithm.ALWAYS_ASK);
        fmmmDesiredEdgeLength.setValue(FMMM_DESIRED_EDGE_LENGTH.get());
        fmmmForceModel.setSelectedIndex(FMMM_FORCE_MODEL.getIndex());
        fmmmQualityVsSpeed.setSelectedIndex(FMMM_QUALITY_VS_SPEED.getIndex());
        fmmmStopCriterion.setSelectedIndex(FMMM_STOP_CRITERION.getIndex());
        fmmmIterationLevelFactor.setValue(FMMM_ITERATION_LEVEL_FACTOR.get());

        layoutUseEdgeWeightsForLayout.setSelected( USE_EDGE_WEIGHTS_FOR_LAYOUT.get() );
        layoutTiledLayout.setSelected( TILED_LAYOUT.get() );
        layoutStartingTemperatureField.setText( Float.toString( STARTING_TEMPERATURE.get() ) );
        layoutIterationsField.setText( Integer.toString( NUMBER_OF_LAYOUT_ITERATIONS.get() ) );
        layoutKvalueField.setText( Float.toString( KVALUE_MODIFIER.get() ) );
        layoutBurstIterationsField.setText( Integer.toString( BURST_LAYOUT_ITERATIONS.get() ) );
        layoutMinimumComponentSizeField.setText( Integer.toString( MINIMUM_COMPONENT_SIZE.get() ) );

        MCL_inflationField.setText( Float.toString( MCL_INFLATION_VALUE.get() ) );
        MCL_inflationSlider.setValue( (int)( 10.0f * MCL_INFLATION_VALUE.get() ) );
        MCL_preInflationField.setText( Float.toString( MCL_PRE_INFLATION_VALUE.get() ) );
        MCL_pre_inflationSlider.setValue( (int)(10.0f * MCL_PRE_INFLATION_VALUE.get() ) );
        MCL_schemeField.setText( Integer.toString( MCL_SCHEME.get() ) );
        MCL_SchemeSlider.setValue( MCL_SCHEME.get() );
        MCL_assignRandomClusterColorsCheckBox.setSelected( MCL_ASSIGN_RANDOM_CLUSTER_COLOURS.get() );
        parallelismUseMCLNCoreParallelism.setSelected( USE_MCL_N_CORE_PARALLELISM.get() );
        parallelismUseMCLNCoreParallelism.setEnabled(USE_MULTICORE_PROCESS);
        MCL_advancedOptionsTextField.setText( MCL_ADVANCED_OPTIONS.get() );
        MCL_smallestClusterAllowedField.setText( Integer.toString( MCL_SMALLEST_CLUSTER.get() ) );
        optionsMCLChanged = false;

        saveSPNResultsCheckBox.setSelected( SAVE_SPN_RESULTS.get() );
        if ( SAVE_SPN_RESULTS.get() )
        {
            saveSPNResultsTextField.setEnabled(true);
            saveSPNResultsAtFolderButton.setEnabled(true);
            saveSPNResultsClearButton.setEnabled(true);
            automaticallySaveSPNResultsToPreChosenFolderCheckBox.setEnabled(true);
        }
        else
        {
            saveSPNResultsTextField.setEnabled(false);
            saveSPNResultsAtFolderButton.setEnabled(false);
            saveSPNResultsClearButton.setEnabled(false);
            automaticallySaveSPNResultsToPreChosenFolderCheckBox.setEnabled(false);
        }
        saveSPNResultsTextField.setText( SAVE_SPN_RESULTS_FILE_NAME.get() );
        automaticallySaveSPNResultsToPreChosenFolderCheckBox.setSelected( AUTOMATICALLY_SAVE_SPN_RESULTS_TO_PRECHOSEN_FOLDER.get() );
        useSPNAnimatedTransitionsShadingCheckBox.setSelected( USE_SHADERS_PROCESS && USE_SPN_ANIMATED_TRANSITIONS_SHADING.get() );
        parallelismUseSPNNCoreParallelism.setSelected( USE_SPN_N_CORE_PARALLELISM.get() );
        parallelismUseSPNNCoreParallelism.setEnabled(USE_MULTICORE_PROCESS);

        materialSpecular.setSelected( MATERIAL_SPECULAR.get() );
        materialShininess.setValue( MATERIAL_SPECULAR_SHINE.get() );
        materialShininess.setEnabled( MATERIAL_SPECULAR.get() ); // disable shininess value if specular turned off
        materialGouraudLighting.setSelected( MATERIAL_SMOOTH_SHADING.get() );
        materialSphericalMapping.setSelected( MATERIAL_SPHERICAL_MAPPING.get() );
        materialEmbossNodeTexture.setSelected( MATERIAL_EMBOSS_NODE_TEXTURE.get() );
        materialAntiAliasShading.setSelected( MATERIAL_ANTIALIAS_SHADING.get() );
        materialAnimatedShading.setSelected( MATERIAL_ANIMATED_SHADING.get() );
        materialStateShading.setSelected( MATERIAL_STATE_SHADING.get() );
        materialOldLCDStyleTransparencyShading.setSelected( MATERIAL_OLD_LCD_STYLE_TRANSPARENCY_SHADING.get() );
        materialErosionShading.setSelected( MATERIAL_EROSION_SHADING.get() );
        materialNormalsSelectionMode.setSelected( MATERIAL_NORMALS_SELECTION_MODE.get() );
        materialNormalsSelectionMode.setEnabled(false);
        lightingPositionXSlider.setValue( LIGHT_POSITION[0].get() );
        lightingPositionYSlider.setValue( LIGHT_POSITION[1].get() );
        lightingPositionZSlider.setValue( LIGHT_POSITION[2].get() );
        depthFog.setSelected( DEPTH_FOG.get() );
        useMotionBlurForScene.setSelected( USE_MOTION_BLUR_FOR_SCENE.get() );
        useMotionBlurForScene.setEnabled(!GL_IS_AMD_ATI);
        motionBlurSize.setValue( 100.0f * MOTION_BLUR_SIZE.get() );
        motionBlurSize.setEnabled(USE_MOTION_BLUR_FOR_SCENE.get() && !GL_IS_AMD_ATI);
        _3DNodeTesselationSlider.setValue( NODE_TESSELATION.get() );
        showNodes.setSelected( SHOW_NODES.get() );
        show3DFrustum.setSelected( SHOW_3D_FRUSTUM.get() );
        show3DShadows.setSelected( SHOW_3D_SHADOWS.get() );
        show3DEnvironmentMapping.setSelected( USE_GL_EXT_FRAMEBUFFER_OBJECT && SHOW_3D_ENVIRONMENT_MAPPING.get() );
        highResImageRenderScaleSlider.setValue( TILE_SCREEN_FACTOR.get() );
        fastSelectionMode.setSelected( FAST_SELECTION_MODE.get() );
        wireframeSelectionMode.setSelected( WIREFRAME_SELECTION_MODE.get() );
        materialNormalsSelectionMode.setEnabled(false);
        advancedKeyboardRenderingControl.setSelected( ADVANCED_KEYBOARD_RENDERING_CONTROL.get() );
        anaglyphStereoscopic3DView.setSelected( ANAGLYPH_STEREOSCOPIC_3D_VIEW.get() );
        graphAnaglyphGlasses3DOptionsDialog.getGraphAnaglyphGlasses3DOptionsOpenDialogAction().setEnabled( ANAGLYPH_STEREOSCOPIC_3D_VIEW.get() );
        anaglyphStereoscopic3DViewOptions.setEnabled( ANAGLYPH_STEREOSCOPIC_3D_VIEW.get() ); // disable 3D options if anaglyphStereoscopic3DViewOptions turned off

        if (USE_SHADERS_PROCESS)
        {
            for (int i = 0; i < allShadings.length; i++)
                allShadings[i].setSelected( ALL_SHADING_SFXS[i].get() );

            boolean value = isAllShadingSFXSValueEnabled();
            materialGouraudLighting.setEnabled(!value);
            materialEmbossNodeTexture.setEnabled(value);
            materialAntiAliasShading.setEnabled(value);
            materialAnimatedShading.setEnabled(value);
            materialStateShading.setEnabled(value);
            materialOldLCDStyleTransparencyShading.setEnabled(value);
            materialErosionShading.setEnabled(value);
            materialNormalsSelectionMode.setEnabled(false);
        }
        else
        {
            setEnabledAllShadingSFXsUIs(false);
            materialEmbossNodeTexture.setEnabled(false);
            materialAntiAliasShading.setEnabled(false);
            materialAnimatedShading.setEnabled(false);
            materialStateShading.setEnabled(false);
            materialOldLCDStyleTransparencyShading.setEnabled(false);
            materialErosionShading.setEnabled(false);
            materialNormalsSelectionMode.setEnabled(false);
        }

        show3DEnvironmentMapping.setEnabled(USE_GL_EXT_FRAMEBUFFER_OBJECT);

        nodeTransparency.setSelected( TRANSPARENT.get() );
        nodeTransparencyAlphaSlider.setValue( TRANSPARENT_ALPHA.get() );
        nodeTransparencyAlphaSlider.setEnabled( TRANSPARENT.get() ); // disable transparency alpha value if transparency turned off

        nodeSurfaceImageTextureCheckBox.setSelected( TEXTURE_ENABLED.get() );
        if ( TEXTURE_ENABLED.get() )
        {
            nodeSurfaceImageTextureComboBox.setEnabled( nodeSurfaceImageTextureFileTextField.getText().isEmpty() );
            nodeSurfaceImageTextureFileTextField.setEnabled( !nodeSurfaceImageTextureFileTextField.getText().isEmpty() );
            nodeSurfaceImageTextureFileLoadButton.setEnabled(true);
            nodeSurfaceImageTextureFileClearButton.setEnabled( !nodeSurfaceImageTextureFileTextField.getText().isEmpty() );
        }
        nodeSurfaceImageTextureComboBox.setSelectedIndex( TEXTURE_CHOSEN.get() );
        nodeSurfaceImageTextureFileTextField.setText(USER_SURFACE_IMAGE_FILE);

        edgesColor.setBackground( DEFAULT_EDGE_COLOR.get() );
        edgeThicknessComboBox.setSelectedIndex( (int)( ( ( 10 * DEFAULT_EDGE_SIZE.get() ) - 1) ) );
        proportionalEdgesSizeToWeight.setSelected( PROPORTIONAL_EDGES_SIZE_TO_WEIGHT.get() );
        arrowHeadSizeComboBox.setSelectedIndex(ARROW_HEAD_SIZE.get() - 1);

        if ( COLOR_EDGES_BY_COLOR.get() )
        {
            edgesColorByColor.setSelected(true);
            edgesColorByWeight.setSelected(false);
        }
        else
        {
            edgesColorByColor.setSelected(false);
            edgesColorByWeight.setSelected(true);
        }
    }

    private GLSLTextureTypes getGLSLTextureType(String GLSLTextureTypeString)
    {
        GLSLTextureTypes[] allGLSLTextureTypes = GLSLTextureTypes.values();
        for (int i = 0; i < allGLSLTextureTypes.length; i++)
            if ( GLSLTextureTypeString.equals( allGLSLTextureTypes[i].toString() ) )
                return allGLSLTextureTypes[i];

        return GLSL_DEFAULT_TEXTURE_TYPE;
    }

    private class SliderListener implements ChangeListener
    {
        private JTextField numberField = null;
        private float divideRatio = 0.0f;

        public SliderListener(JTextField numberField)
        {
            this(numberField, 1.0f);
        }

        public SliderListener(JTextField numberField, float divideRatio)
        {
            this.numberField = numberField;
            this.divideRatio = divideRatio;
        }

        @Override
        public void stateChanged(ChangeEvent e)
        {
            if (divideRatio != 1.0f)
            {
                float value = ( (JSlider)e.getSource() ).getValue() / divideRatio;
                if ( !numberField.getText().equals( String.valueOf(value) ) )
                {
                    numberField.setText( String.valueOf(value) );
                    optionsMCLChanged = true;
                    setEnabledAllApplyButtons(true);
                }
            }
            else
            {
                int value = ( (JSlider)e.getSource() ).getValue();
                if ( !numberField.getText().equals( Integer.toString(value) ) )
                {
                    numberField.setText( String.valueOf(value) );
                    optionsMCLChanged = true;
                    setEnabledAllApplyButtons(true);
                }
            }
        }


    }

    private void applyChanges()
    {
        int proceedClassChange = 1;
        boolean alreadyWarned = false;
        boolean updateNodesDisplayList = false;

        if (layoutClassesTable != null)
            updateNodesDisplayList = layoutClassesTable.updateClassData();

        applyGeneralProperties();
        applySearchProperties();

        HashSet<GraphNode> selectedNodes = layoutFrame.getGraph().getSelectionManager().getSelectedNodes();
        for (GraphNode graphNode : selectedNodes)
        {
            if (nodeColorChange)
            {
                if (!nodeColorButton.isBlank)
                {
                    if (!alreadyWarned)
                    {
                        proceedClassChange = JOptionPane.showConfirmDialog(this,
                                                                           "Are you sure you want to Override Class Colors ?",
                                                                           "Override Class Colors",
                                                                           JOptionPane.YES_NO_OPTION);

                        alreadyWarned = true;
                    }

                    if (proceedClassChange == JOptionPane.YES_OPTION)
                        graphNode.setColor( nodeColorButton.getBackground() );
                }
            }

            if (nodeSizeChange)
                graphNode.setNodeSize(nodeSizeComboBox.getSelectedIndex() + 1);

            if (node2DShapeChange)
            {
                if (node2DShape.getSelectedIndex() != NUMBER_OF_2D_SHAPES)
                    graphNode.setNode2DShape(Shapes2D.values()[node2DShape.getSelectedIndex()]);
                else
                    graphNode.setNode2DShape(Shapes2D.CIRCLE);
            }

            if (node3DShapeChange)
            {
                if (node3DShape.getSelectedIndex() != NUMBER_OF_3D_SHAPES)
                    graphNode.setNode3DShape(Shapes3D.values()[node3DShape.getSelectedIndex()]);
                else
                    graphNode.setNode3DShape(Shapes3D.SPHERE);
            }

            if (node3DTransparencyAlphaChange)
            {
                graphNode.setTransparencyAlpha( TRANSPARENT_ALPHA.get() );
            }

            if ( nodeNameTextField.isEnabled() )
            {
                nc.setNodeName( graphNode, nodeNameTextField.getText().replace("\"", " ").trim() );
            }

            if ( nc.getIsGraphml() && graphmlViewNodeDepthPositioningTextField.isEnabled() )
            {
                // inverse value to give the user the impression that a positive depth (Z) value is nearer to the viewer
                nc.getGraphmlNetworkContainer().getAllGraphmlNodesMap().get( graphNode.getNodeName() ).first[4] = -graphmlViewNodeDepthPositioningTextField.getValue();
            }

            if (nodeClassChange)
            {
                if (nodeClassComboBox.getSelectedIndex() != 0)
                {
                    if (DEBUG_BUILD) println("Applying a Class Change");

                    layoutClassSetsManager.getCurrentClassSetAllClasses().setClass( graphNode.getVertex(), (VertexClass)nodeClassComboBox.getSelectedItem() );
                    graphNode.removeColorOverride();
                }
            }
        }

        nodeClassChange = false;
        nodeColorChange = false;
        nodeSizeChange = false;
        node2DShapeChange = false;
        node3DShapeChange = false;
        node3DTransparencyAlphaChange = false;

        if (generalChange || updateNodesDisplayList)
            layoutFrame.getGraph().updateDisplayLists(_3DRebuildNodes || updateNodesDisplayList, _3DRebuildEdges, _3DRebuildNodes);

        if (refreshClassViewer)
        {
            if ( layoutFrame.getClassViewerFrame().isVisible() )
                layoutFrame.getClassViewerFrame().populateClassViewer(false, true);
        }

        if (optionsMCLChanged)
            layoutFrame.getLayoutClusterMCL().checkMCLExecutable();

        optionsMCLChanged = false;
        generalChange = false;
        refreshClassViewer = false;
        setEnabledAllApplyButtons(false);
    }

    private void applyGeneralProperties()
    {
        layoutFrame.getNetworkRootContainer().getFRLayout().setNumberOfIterations( NUMBER_OF_LAYOUT_ITERATIONS.get() );
        layoutFrame.getNetworkRootContainer().getFRLayout().setTemperature( STARTING_TEMPERATURE.get() );
        layoutFrame.getNetworkRootContainer().getFRLayout().setKValueModifier( KVALUE_MODIFIER.get() );

        if (frRadioButton.isSelected())
        {
            GRAPH_LAYOUT_ALGORITHM.set(GraphLayoutAlgorithm.FRUCHTERMAN_RHEINGOLD);
        }
        else if (fmmmRadioButton.isSelected())
        {
            GRAPH_LAYOUT_ALGORITHM.set(GraphLayoutAlgorithm.FMMM);
        }
        else if (circleRadioButton.isSelected())
        {
            GRAPH_LAYOUT_ALGORITHM.set(GraphLayoutAlgorithm.CIRCLE);
        }
        else if (askRadioButton.isSelected())
        {
            GRAPH_LAYOUT_ALGORITHM.set(GraphLayoutAlgorithm.ALWAYS_ASK);
        }

        USE_EDGE_WEIGHTS_FOR_LAYOUT.set( layoutUseEdgeWeightsForLayout.isSelected() );
        TILED_LAYOUT.set( layoutTiledLayout.isSelected() );
        STARTING_TEMPERATURE.set( layoutStartingTemperatureField.getValue() );
        NUMBER_OF_LAYOUT_ITERATIONS.set(layoutIterationsField);
        KVALUE_MODIFIER.set( layoutKvalueField.getValue() );
        BURST_LAYOUT_ITERATIONS.set(layoutBurstIterationsField);
        MINIMUM_COMPONENT_SIZE.set(layoutMinimumComponentSizeField);

        FMMM_DESIRED_EDGE_LENGTH.set(fmmmDesiredEdgeLength.getValue());
        FMMM_FORCE_MODEL.set(FmmmForceModel.values()[fmmmForceModel.getSelectedIndex()]);
        FMMM_QUALITY_VS_SPEED.set(FmmmQualityVsSpeed.values()[fmmmQualityVsSpeed.getSelectedIndex()]);
        FMMM_STOP_CRITERION.set(FmmmStopCriterion.values()[fmmmStopCriterion.getSelectedIndex()]);
        FMMM_ITERATION_LEVEL_FACTOR.set((int)fmmmIterationLevelFactor.getValue());

        MCL_INFLATION_VALUE.set( MCL_inflationField.getValue() );
        MCL_PRE_INFLATION_VALUE.set( MCL_preInflationField.getValue() );
        MCL_SCHEME.set(MCL_schemeField);
        MCL_ASSIGN_RANDOM_CLUSTER_COLOURS.set( MCL_assignRandomClusterColorsCheckBox.isSelected() );
        USE_MCL_N_CORE_PARALLELISM.set( parallelismUseMCLNCoreParallelism.isSelected() );
        MCL_ADVANCED_OPTIONS.set( MCL_advancedOptionsTextField.getText() );
        MCL_SMALLEST_CLUSTER.set(MCL_smallestClusterAllowedField);

        SAVE_SPN_RESULTS.set( saveSPNResultsCheckBox.isSelected() );
        AUTOMATICALLY_SAVE_SPN_RESULTS_TO_PRECHOSEN_FOLDER.set( automaticallySaveSPNResultsToPreChosenFolderCheckBox.isSelected() );
        USE_SPN_ANIMATED_TRANSITIONS_SHADING.set( useSPNAnimatedTransitionsShadingCheckBox.isSelected() );
        USE_SPN_N_CORE_PARALLELISM.set( parallelismUseSPNNCoreParallelism.isSelected() );

        COLOR_EDGES_BY_COLOR.set( edgesColorByColor.isSelected() );
        COLOR_EDGES_BY_WEIGHT.set( edgesColorByWeight.isSelected() );
        DEFAULT_EDGE_COLOR.set( edgesColor.getBackground() );
        DEFAULT_EDGE_SIZE.set( ( (float)edgeThicknessComboBox.getSelectedIndex() / 10.0f ) + 0.1f );
        PROPORTIONAL_EDGES_SIZE_TO_WEIGHT.set( proportionalEdgesSizeToWeight.isSelected() );
        ARROW_HEAD_SIZE.set(arrowHeadSizeComboBox.getSelectedIndex() + 1);

        if (generalChange)
        {
            BACKGROUND_COLOR.set( generalColor.getBackground() );
            BACKGROUND_COLOR.set( generalColor.getBackground() );
            SELECTION_COLOR.set( generalColorSelection.getBackground() );
            PLOT_BACKGROUND_COLOR.set( generalColorPlotBackground.getBackground() );
            PLOT_GRIDLINES_COLOR.set( generalColorPlotGridlines.getBackground() );
            TRIPPY_BACKGROUND.set( generalTrippyBackground.isSelected() );

            DIRECTIONAL_EDGES.set( generalDirectional.isSelected() );
            SHOW_EDGES_WHEN_DRAGGING_NODES.set( generalDragShowEdgesWhileDraggingNodes.isSelected() );
            YED_STYLE_RENDERING_FOR_GPAPHML_FILES.set( generalyEdStyleRenderingForGraphmlFiles.isSelected() );
            YED_STYLE_COMPONENT_CONTAINERS_RENDERING_FOR_GPAPHML_FILES.set( generalyEdStyleComponentContainersRenderingForGraphmlFiles.isSelected() );
            if ( generalyEdStyleRenderingForGraphmlFiles.isEnabled() )
                setEnabledGraphmlRenderingAndDepthRelatedOptions( generalyEdStyleRenderingForGraphmlFiles.isSelected() );

            if ( generalHighQualityAntiAlias.isSelected() )
            {
                HIGH_QUALITY_ANTIALIASING.set(true);
                NORMAL_QUALITY_ANTIALIASING.set(true);
            }
            else if ( generalNormalQualityAntiAlias.isSelected() )
            {
                HIGH_QUALITY_ANTIALIASING.set(false);
                NORMAL_QUALITY_ANTIALIASING.set(true);
            }
            else
            {
                HIGH_QUALITY_ANTIALIASING.set(false);
                NORMAL_QUALITY_ANTIALIASING.set(false);
            }

            USE_VSYNCH.set( useVSynch.isSelected() );
            layoutFrame.getGraph().setGraphRendererThreadUpdaterTargetFPS();

            DISABLE_NODES_RENDERING.set( generalDisableNodesRendering.isSelected() );
            DISABLE_EDGES_RENDERING.set( generalDisableEdgesRendering.isSelected() );
            INSTALL_DIR_FOR_SCREENSHOTS_HAS_CHANGED = ( generalUseInstallDirForScreenshots.isSelected() != USE_INSTALL_DIR_FOR_SCREENSHOTS.get() );
            USE_INSTALL_DIR_FOR_SCREENSHOTS.set( generalUseInstallDirForScreenshots.isSelected() );
            USE_INSTALL_DIR_FOR_MCL_TEMP_FILE.set( generalUseInstallDirForMCLTempFile.isSelected() );
            SHOW_NAVIGATION_WIZARD_ON_STARTUP.set( generalShowNavigationWizardOnStartup.isSelected() );
            SHOW_LAYOUT_ITERATIONS.set( generalShowLayoutIterations.isSelected() );
            VALIDATE_XML_FILES.set( generalValidateXMLFiles.isSelected() );
            if ( generalShowGraphPropertiesToolBar.isSelected() != SHOW_GRAPH_PROPERTIES_TOOLBAR.get() )
                layoutFrame.removeAddGraphPropertiesToolBar( !generalShowGraphPropertiesToolBar.isSelected() );
            SHOW_GRAPH_PROPERTIES_TOOLBAR.set( generalShowGraphPropertiesToolBar.isSelected() );
            if ( generalShowNavigationToolBar.isSelected() != SHOW_NAVIGATION_TOOLBAR.get() )
                layoutFrame.removeAddNavigationToolBar( !generalShowNavigationToolBar.isSelected() );
            SHOW_NAVIGATION_TOOLBAR.set( generalShowNavigationToolBar.isSelected() );
            SHOW_POPUP_OVERLAY_PLOT.set( generalShowPopupOverlayPlot.isSelected() );
            COLLAPSE_NODES_BY_VOLUME.set( generalCollapseNodesByVolume.isSelected() );
            CONFIRM_PREFERENCES_SAVE.set( generalConfirmPreferencesSave.isSelected() );
            USE_EXRESSION_CORRELATION_CALCULATION_N_CORE_PARALLELISM.set( parallelismUseExpressionCorrelationCalculationNCoreParallelism.isSelected() );
            USE_OPENCL_GPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION.set( parallelismUseExpressionCorrelationCalculationOpenCLGPUComputing.isSelected() );
            USE_GLSL_GPGPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION.set( parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputing.isSelected() );
            OPENCL_GPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION_ITERATION_SIZE.set( (Integer)parallelismUseExpressionCorrelationCalculationOpenCLGPUComputingIterationSize.getSelectedItem() );
            GLSL_GPGPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION_TEXTURE_SIZE.set( (Integer)parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputingTextureSize.getSelectedItem() );
            GLSL_GPGPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION_TEXTURE_TYPE.set( parallelismUseExpressionCorrelationCalculationGLSLGPGPUComputingTextureType.getSelectedItem().toString() );
            COMPARE_GPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION_WITH_CPU.set( parallelismUseGPUComputingExpressionCorrelationCalculationCompareGPUWithCPU.isSelected() );
            COMPARE_GPU_COMPUTING_EXRESSION_CORRELATION_CALCULATION_WITH_CPU_DEFAULT_COMPARISON_METHOD.set( (String)parallelismUseGPUComputingExpressionCorrelationCalculationCompareGPUWithCPUComparisonMethods.getSelectedItem() );
            USE_LAYOUT_N_CORE_PARALLELISM.set( parallelismUseLayoutNCoreParallelism.isSelected() );
            USE_OPENCL_GPU_COMPUTING_LAYOUT_CALCULATION.set( parallelismUseLayoutOpenCLGPUComputing.isSelected() );
            USE_INDICES_1D_KERNEL_WITH_ITERATIONS_FOR_OPENCL_GPU_COMPUTING_LAYOUT_CALCULATION.set( parallelismUseIndices1DKernelWithIterationsForLayoutOpenCLGPUComputing.isSelected() );
            OPENCL_GPU_COMPUTING_LAYOUT_CALCULATION_ITERATION_SIZE.set( (Integer)parallelismUseLayoutOpenCLGPUComputingIterationSize.getSelectedItem() );
            COMPARE_GPU_COMPUTING_LAYOUT_CALCULATION_WITH_CPU.set( parallelismUseGPUComputingLayoutCompareGPUWithCPU.isSelected() );
            COMPARE_GPU_COMPUTING_LAYOUT_CALCULATION_WITH_CPU_DEFAULT_COMPARISON_METHOD.set( (String)parallelismUseGPUComputingLayoutCompareGPUWithCPUComparisonMethods.getSelectedItem() );
            USE_ATOMIC_SYNCHRONIZATION_FOR_LAYOUT_N_CORE_PARALLELISM.set( parallelismUseAtomicSynchronizationForLayoutNCoreParallelism.isSelected() );

            LIGHT_POSITION[0].set( (float)lightingPositionXSlider.getValue() );
            LIGHT_POSITION[1].set( (float)lightingPositionYSlider.getValue() );
            LIGHT_POSITION[2].set( (float)lightingPositionZSlider.getValue() );

            SHOW_NODES.set( showNodes.isSelected() );
            SHOW_3D_FRUSTUM.set( show3DFrustum.isSelected() );
            SHOW_3D_SHADOWS.set( show3DShadows.isSelected() );
            SHOW_3D_ENVIRONMENT_MAPPING.set( show3DEnvironmentMapping.isSelected() );
            CHANGE_NODE_TESSELATION  = ( NODE_TESSELATION.get() != (int)_3DNodeTesselationSlider.getValue() );
            NODE_TESSELATION.set( (int)_3DNodeTesselationSlider.getValue() );
            TILE_SCREEN_FACTOR.set( (int)highResImageRenderScaleSlider.getValue() );
            FAST_SELECTION_MODE.set( fastSelectionMode.isSelected() );
            WIREFRAME_SELECTION_MODE.set( wireframeSelectionMode.isSelected() );
            ADVANCED_KEYBOARD_RENDERING_CONTROL.set( advancedKeyboardRenderingControl.isSelected() );
            ANAGLYPH_STEREOSCOPIC_3D_VIEW.set( anaglyphStereoscopic3DView.isSelected() );
            DEPTH_FOG.set( depthFog.isSelected() );
            USE_MOTION_BLUR_FOR_SCENE.set( useMotionBlurForScene.isSelected() );
            MOTION_BLUR_SIZE.set( (float)motionBlurSize.getValue() / 100.0f );
            MATERIAL_SPECULAR.set( materialSpecular.isSelected() );
            MATERIAL_SPECULAR_SHINE.set( (float)materialShininess.getValue() );
            MATERIAL_SMOOTH_SHADING.set( materialGouraudLighting.isSelected() );
            MATERIAL_SPHERICAL_MAPPING.set( materialSphericalMapping.isSelected() );
            MATERIAL_EMBOSS_NODE_TEXTURE.set( materialEmbossNodeTexture.isSelected() );
            MATERIAL_ANTIALIAS_SHADING.set( materialAntiAliasShading.isSelected() );
            MATERIAL_ANIMATED_SHADING.set( materialAnimatedShading.isSelected() );
            MATERIAL_STATE_SHADING.set( materialStateShading.isSelected() );
            MATERIAL_OLD_LCD_STYLE_TRANSPARENCY_SHADING.set( materialOldLCDStyleTransparencyShading.isSelected() );
            MATERIAL_EROSION_SHADING.set( materialErosionShading.isSelected() );
            MATERIAL_NORMALS_SELECTION_MODE.set( materialNormalsSelectionMode.isSelected() );

            if (USE_SHADERS_PROCESS)
            {
                for (int i = 0; i < allShadings.length; i++)
                    ALL_SHADING_SFXS[i].set( allShadings[i].isSelected() );

                boolean value = isAllShadingSFXSValueEnabled();
                materialGouraudLighting.setEnabled(!value);
                materialEmbossNodeTexture.setEnabled(value);
                materialAntiAliasShading.setEnabled(value);
                materialAnimatedShading.setEnabled(value);
                materialStateShading.setEnabled(value);
                materialOldLCDStyleTransparencyShading.setEnabled(value);
                materialErosionShading.setEnabled(value);
                materialNormalsSelectionMode.setEnabled(false);
            }

            TRANSPARENT.set( nodeTransparency.isSelected() );
            TRANSPARENT_ALPHA.set( (float)nodeTransparencyAlphaSlider.getValue() );

            TEXTURE_ENABLED.set( nodeSurfaceImageTextureCheckBox.isSelected() );
            TEXTURE_CHOSEN.set( nodeSurfaceImageTextureComboBox.getSelectedIndex() );
        }

        if (updateGraphAnaglyphGlasses3DOptionsDialogPreferencesCallBackChange)
        {
            GRAPH_ANAGLYPH_GLASSES_TYPE = graphAnaglyphGlassesType;
            ANAGLYPH_GLASSES_TYPE.set( graphAnaglyphGlassesType.toString() );
            GRAPH_INTRA_OCULAR_DISTANCE_TYPE = graphIntraOcularDistanceType;
            INTRA_OCULAR_DISTANCE_TYPE.set( graphIntraOcularDistanceType.toString() );

            double intraOcularDistance = extractDouble(graphIntraOcularDistanceType);
            layoutFrame.getGraph().setCamerasIntraOcularDistanceAndFrustumShift(intraOcularDistance);
            modelShapeEditorParentUIDialog.setCamerasIntraOcularDistanceAndFrustumShift(intraOcularDistance);

            updateGraphAnaglyphGlasses3DOptionsDialogPreferencesCallBackChange = false;
        }

        if (updateModelShapeEditorParentUIDialogPreferencesCallBackChange)
        {
            if ( modelType.equals(LATHE3D_SHAPE) )
            {
                LATHE3D_SETTINGS_XSIN.set( LayoutPreferences.createPreferenceFloatArrayString(LATHE3D_SETTINGS.xsIn) );
                LATHE3D_SETTINGS_YSIN.set( LayoutPreferences.createPreferenceFloatArrayString(LATHE3D_SETTINGS.ysIn) );
                LATHE3D_SETTINGS_SPLINE_STEP.set(LATHE3D_SETTINGS.splineStep);
                LATHE3D_SETTINGS_K.set(LATHE3D_SETTINGS.k);
                LATHE3D_SETTINGS_LATHE3D_SHAPE_TYPE.set( LATHE3D_SETTINGS.lathe3DShapeType.ordinal() );
            }
            else if ( modelType.equals(SUPER_QUADRIC_SHAPE) )
            {
                SUPER_QUADRIC_SETTINGS_E.set(SUPER_QUADRIC_SETTINGS.e);
                SUPER_QUADRIC_SETTINGS_N.set(SUPER_QUADRIC_SETTINGS.n);
                SUPER_QUADRIC_SETTINGS_V1.set(SUPER_QUADRIC_SETTINGS.v1);
                SUPER_QUADRIC_SETTINGS_ALPHA.set(SUPER_QUADRIC_SETTINGS.alpha);
                SUPER_QUADRIC_SETTINGS_SUPER_QUADRIC_SHAPE_TYPE.set( SUPER_QUADRIC_SETTINGS.superQuadricShapeType.ordinal() );
            }
            /*
            else // if ( modelType.equals(OBJ_MODEL_LOADER_SHAPE) )
            {

            }
            */

            CHANGE_ALL_SHAPES = CHANGE_ALL_FAST_SELECTION_SHAPES = true; // to enforce update of the Model Editor Shapes (amongst the rest)
            updateModelShapeEditorParentUIDialogPreferencesCallBackChange = false;
        }
    }

    private void refreshClasses()
    {
        tabbedPane.setSelectedComponent(generalPropertiesPanel);
        tabbedPane.remove(classesPropertiesPanel);
        classesPropertiesPanel.removeAll();
        createClassesPropertiesTab( classesPropertiesPanel, tabbedPane, CLASSES.ordinal() );
        tabbedPane.setSelectedComponent(classesPropertiesPanel);
        classesChooser.setSelectedIndex( layoutClassSetsManager.getCurrentClassSetID() );
    }

    public void setEmbossNodeTexture(boolean selected)
    {
        materialEmbossNodeTexture.setSelected(selected);
    }

    public void setMaterialAntiAliasShading(boolean selected)
    {
        materialAntiAliasShading.setSelected(selected);
    }

    public void setMaterialAnimatedPerlinShading(boolean selected)
    {
        materialAnimatedShading.setSelected(selected);
    }

    public void setMaterialStateShading(boolean selected)
    {
        materialStateShading.setSelected(selected);
    }

    public void setMaterialOldLCDStyleTransparencyShading(boolean selected)
    {
        materialOldLCDStyleTransparencyShading.setSelected(selected);
    }

    public void setShow3DFrustum(boolean selected)
    {
        show3DFrustum.setSelected(selected);
    }

    public void setShow3DShadows(boolean selected)
    {
        show3DShadows.setSelected(selected);
    }

    public void setShow3DEnvironmentMapping(boolean selected)
    {
        show3DEnvironmentMapping.setSelected(selected);

        if ( show3DEnvironmentMapping.isSelected() )
        {
            if ( !materialSphericalMapping.isSelected() )
            {
                sphericalMappingWasNotSelected = true;
                materialSphericalMapping.setSelected(true);
            }
            else
                sphericalMappingWasNotSelected = false;

            if ( !nodeSurfaceImageTextureCheckBox.isSelected() )
            {
                texturingWasNotSelected = true;
                nodeSurfaceImageTextureCheckBox.setSelected(true);
            }
            else
                texturingWasNotSelected = false;

            materialSphericalMapping.setEnabled(false);
            nodeSurfaceImageTextureCheckBox.setEnabled(false);
        }
        else
        {
            materialSphericalMapping.setSelected(!sphericalMappingWasNotSelected);
            materialSphericalMapping.setEnabled(true);

            nodeSurfaceImageTextureCheckBox.setSelected(!texturingWasNotSelected);
            nodeSurfaceImageTextureCheckBox.setEnabled(true);
        }
    }

    public void setWireframeSelectionMode(boolean selected)
    {
        wireframeSelectionMode.setSelected(selected);
    }

    public void setDepthFog(boolean selected)
    {
        depthFog.setSelected(selected);
    }

    public void setSphericalMapping(boolean selected)
    {
        materialSphericalMapping.setSelected(selected);
    }

    public void setNodeSurfaceImageTexture(boolean selected)
    {
        nodeSurfaceImageTextureCheckBox.setSelected(selected);

        if ( nodeSurfaceImageTextureCheckBox.isSelected() )
        {
            if ( show3DEnvironmentMapping.isSelected() )
                show3DEnvironmentMapping.setSelected(false);

            nodeSurfaceImageTextureComboBox.setEnabled( nodeSurfaceImageTextureFileTextField.getText().isEmpty() );
            nodeSurfaceImageTextureFileTextField.setEnabled( !nodeSurfaceImageTextureFileTextField.getText().isEmpty() );
            nodeSurfaceImageTextureFileLoadButton.setEnabled(true);
            nodeSurfaceImageTextureFileClearButton.setEnabled( !nodeSurfaceImageTextureFileTextField.getText().isEmpty() );
        }
        else
        {
            nodeSurfaceImageTextureComboBox.setEnabled(false);
            nodeSurfaceImageTextureFileTextField.setEnabled(false);
            nodeSurfaceImageTextureFileLoadButton.setEnabled(false);
            nodeSurfaceImageTextureFileClearButton.setEnabled(false);
        }

        if (selected)
        {
            if ( SHOW_3D_ENVIRONMENT_MAPPING.get() )
            {
                show3DEnvironmentMapping.setSelected(false);
                SHOW_3D_ENVIRONMENT_MAPPING.set(false);
            }
        }
    }

    public void setTrippyBackground(boolean selected)
    {
        generalTrippyBackground.setSelected(selected);
    }

    public void setUseVSynch(boolean selected)
    {
        useVSynch.setSelected(selected);
    }

    public boolean getHasNewPreferencesBeenApplied()
    {
        return hasNewPreferencesBeenApplied;
    }

    public void setHasNewPreferencesBeenApplied(boolean hasNewPreferencesBeenApplied)
    {
        this.hasNewPreferencesBeenApplied = hasNewPreferencesBeenApplied;
    }

    /**
    *  Checks if a value in the shaderLightingSFXValues action event array is enabled.
    */
    private boolean isAllShadingSFXSActionEventEnabled(ActionEvent e)
    {
        for (int i = 0; i < allShadings.length; i++)
            if ( e.getSource().equals(allShadings[i]) )
                return true;

        return false;
    }

    /**
    *  Returns the index of the enabled shaderLightingSFXValues action event array.
    */
    private int indexOfAllShadingSFXSActionEventEnabled(ActionEvent e)
    {
        for (int i = 0; i < allShadings.length; i++)
            if ( e.getSource().equals(allShadings[i]) )
                return i;

        return 0;
    }

    private void setEnabledAllShadingSFXsUIs(boolean flag)
    {
        for (int i = 0; i < allShadings.length; i++)
            allShadings[i].setEnabled(flag);
    }

    /**
    *  Checks if a value in the ALL_SHADING_SFXS PrefBool array is enabled.
    */
    public boolean isAllShadingSFXSValueEnabled()
    {
        for (int i = 0; i < allShadings.length; i++)
            if ( ALL_SHADING_SFXS[i].get() )
                return true;

        return false;
    }

    public void setShaderLightingSFXValue(ShaderLightingSFXs.ShaderTypes shaderType)
    {
        if (USE_SHADERS_PROCESS)
        {
            int effectIndex = shaderType.ordinal();
            boolean value = !allShadings[effectIndex].isSelected();
            for (int i = 0; i < allShadings.length; i++)
            {
                allShadings[i].setSelected(false);
                ALL_SHADING_SFXS[i].set(false);
            }

            allShadings[effectIndex].setSelected(value);
            ALL_SHADING_SFXS[effectIndex].set(value);

            value = isAllShadingSFXSValueEnabled();
            materialGouraudLighting.setEnabled(!value);
            materialEmbossNodeTexture.setEnabled(value);
            materialAntiAliasShading.setEnabled(value);
            materialAnimatedShading.setEnabled(value);
            materialOldLCDStyleTransparencyShading.setEnabled(value);
            materialErosionShading.setEnabled(value);
            materialNormalsSelectionMode.setEnabled(false);
        }
    }

    public void setEnabledProportionalEdgesSizeToWeight(boolean enabled)
    {
        proportionalEdgesSizeToWeight.setEnabled(enabled);
    }

    public void setEnabledGraphmlRelatedOptions(boolean enabled)
    {
        generalyEdStyleRenderingForGraphmlFiles.setEnabled(enabled);
        setEnabledGraphmlRenderingAndDepthRelatedOptions(enabled);
    }

    private void setEnabledGraphmlRenderingAndDepthRelatedOptions(boolean enabled)
    {
        generalyEdStyleComponentContainersRenderingForGraphmlFiles.setEnabled(enabled);
        graphmlViewNodeDepthPositioningTextField.setEnabled(enabled);
        graphmlViewNodeDepthZPositioningLabel.setEnabled(enabled);
        graphmlAllViewNodeDepthResetPositioningsButton.setEnabled(enabled);
    }

    public void setEnabledNodeNameTextFieldAndSelectNodesTab(boolean enabled, GraphNode node, int howManyNodesSelected)
    {
        this.howManyNodesSelected = howManyNodesSelected;

        if (!enabled)
        {
            nodeNameTextField.setText("N/A");
            nodeNameTextField.setEnabled(false);
        }
        else
        {
            nodeNameTextField.setText( nc.getNodeName( node.getNodeName() ) );
            nodeNameTextField.setEnabled(true);
        }

        if (howManyNodesSelected <= 0)
        {
            tabbedPane.setEnabledAt(tabbedPane.indexOfComponent(nodesPropertiesPanel), false);
            tabbedPane.setSelectedComponent(generalPropertiesPanel);
        }
        else
        {
            tabbedPane.setEnabledAt(tabbedPane.indexOfComponent(nodesPropertiesPanel), true);
            tabbedPane.setSelectedComponent(nodesPropertiesPanel);
        }
    }

    public AbstractAction getGeneralPropertiesAction()
    {
        return generalPropertiesAction;
    }

    public AbstractAction getLayoutPropertiesAction()
    {
        return layoutPropertiesAction;
    }

    public AbstractAction getRenderingPropertiesAction()
    {
        return renderingPropertiesAction;
    }

    public AbstractAction getMCLPropertiesAction()
    {
        return MCL_propertiesAction;
    }

    public AbstractAction getSimulationPropertiesAction()
    {
        return simulationPropertiesAction;
    }

    public AbstractAction getParallelismPropertiesAction()
    {
        return parallelismPropertiesAction ;
    }

    public AbstractAction getSearchPropertiesAction()
    {
        return searchPropertiesAction;
    }

    public AbstractAction getNodesPropertiesAction()
    {
        return nodesPropertiesAction;
    }

    public AbstractAction getEdgesPropertiesAction()
    {
        return edgesPropertiesAction;
    }

    public AbstractAction getClassesPropertiesAction()
    {
        return classesPropertiesAction;
    }


}