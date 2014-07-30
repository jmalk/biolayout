package org.BioLayoutExpress3D.ClassViewerUI;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import javax.swing.table.*;
import org.BioLayoutExpress3D.ClassViewerUI.Dialogs.*;
import org.BioLayoutExpress3D.ClassViewerUI.Tables.*;
import org.BioLayoutExpress3D.ClassViewerUI.Tables.TableModels.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.Graph.GraphElements.*;
import org.BioLayoutExpress3D.Graph.Selection.SelectionUI.Dialogs.*;
import org.BioLayoutExpress3D.Network.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import org.BioLayoutExpress3D.Utils.*;
import static org.BioLayoutExpress3D.ClassViewerUI.ClassViewerFrame.ClassViewerTabTypes.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;
import org.BioLayoutExpress3D.Expression.Panels.ExpressionGraphPanel;
import org.BioLayoutExpress3D.Simulation.Panels.SimulationResultsPanel;

/**
*
* The Class Viewer Frame class.
*
* @author Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public final class ClassViewerFrame extends JFrame implements ActionListener, ListSelectionListener, ChangeListener, ItemListener
{
    /**
    *  Serial version UID variable for the ClassViewerFrame class.
    */
    public static final long serialVersionUID = 111222333444555791L;

    public static enum ClassViewerTabTypes { GENERAL_TAB, ENTROPY_TAB, ENTROPY_DETAILS_TAB }
    private static final int NAME_COLUMN = 1;
    private static final int TIME_TO_SLEEP_TO_ABORT_THREADS = 50;

    private LayoutFrame layoutFrame = null;
    private JTabbedPane tabbedPane = null;
    private HashSet<GraphNode> oldSelection = null;

    // general table
    private ClassViewerTable generalTable = null;
    private ClassViewerTableModelGeneral tableModelGeneral = null;
    private TableRowSorter<ClassViewerTableModelGeneral> generalTableSorter = null;
    private AbstractAction classViewerDialogAction = null;
    private AbstractAction okAction = null;
    private JComboBox<String> classSetsBox = null;
    private JCheckBox viewAllClassSets = null;
    private AbstractAction refreshSelectionInTableAction = null;
    private JCheckBox autoSizeColumnsCheckBox = null;
    private JButton selectDeselectAllButton = null;
    private boolean selectDeselectAllButtonModeState = false;
    private boolean updateResetSelectDeselectAllButton = true;
    private JCheckBox highlightIsSelectionCheckbox = null;

    private ClassViewerPlotPanel plotPanel = null;
    private FindNameDialog findNameDialog = null;
    private FindClassDialog findClassDialog = null;
    private FindMultipleClassesDialog findMultipleClassesDialog = null;

    private JPanel tabGeneralPanel = null;
    private JPanel generalTablePanel = null;
    private JSplitPane splitPane = null;

    private JButton renderAllCurrentClassSetPlotImagesToFilesButton = null;
    private JButton renderPlotImageToFileButton = null;

    private JButton findNameButton = null;
    private JButton findClassButton = null;
    private JButton findMultipleClassesButton = null;
    private JButton previousClassButton = null;
    private JButton nextClassButton = null;

    private AbstractAction findNameAction = null;
    private AbstractAction findClassAction = null;
    private AbstractAction findMultipleClassesAction = null;
    private AbstractAction previousClassAction = null;
    private AbstractAction nextClassAction = null;

    private ClassViewerHideColumnsDialog classViewerHideColumnsDialog = null;
    private JButton refreshSelectionInTableButton = null;
    private JButton chooseColumnsToHideButton = null;
    private JButton exportTableAsButton = null;
    private AbstractAction chooseColumnsToHideAction = null;
    private AbstractAction exportTableToFileAction = null;

    //search database
    private JButton searchDatabaseButton = null;
    //private AbstractAction searchDatabaseAction = null;

    // entropy table
    private ClassViewerTable entropyTable = null;
    private ClassViewerTableModelAnalysis entropyTableModel = null;
    private HashSet<String> selectedGenes = null;
    private String annotationClass = "";
    private boolean rebuildClassSets = false;

    private JButton detailsButton = null;
    private JButton detailsForAllButton = null;
    private AbstractAction detailsAction = null;
    private AbstractAction detailsOfAllAction = null;

    // entropy analysis details table
    private ClassViewerTableModelDetail analysisTableModel = null;

    private ClassViewerUpdateEntropyTable updateEntropyTableRunnable = null;
    private ClassViewerUpdateDetailedEntropyTable updateDetailedEntropyTableRunnable = null;

    // variables used for proper window event usage
    private boolean isWindowIconified = false;
    private boolean isWindowMaximized = false;
    private boolean windowWasMaximizedBeforeIconification = false;

    private JFileChooser exportTableViewToFileChooser = null;
    private FileNameExtensionFilter fileNameExtensionFilterText = null;

    private int classViewerWidthValue = 0;
    private int prevSplitPaneDividerLocation = 0;
    private String currentClassName = "";

    public ClassViewerFrame(LayoutFrame layoutFrame)
    {
        super("Class Viewer");

        this.layoutFrame = layoutFrame;

        oldSelection = new HashSet<GraphNode>();
        selectedGenes = new HashSet<String>();

        initFrame(this);
        initActions(this);
        initComponents();
        initExportTableViewToFileChooser();
    }

    private void initFrame(final ClassViewerFrame classViewerFrame)
    {
        this.setIconImage(BIOLAYOUT_ICON_IMAGE);
        this.addWindowListener( new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                closeClassViewerWindow();
            }

            @Override
            public void windowIconified(WindowEvent e)
            {
                isWindowIconified = true;
                windowWasMaximizedBeforeIconification = isWindowMaximized; // maximized state is not 'remembered' once frame is iconified, so has to be done manually!
            }

            @Override
            public void windowDeiconified(WindowEvent e)
            {
                isWindowIconified = false;
            }
        } );
        this.addWindowStateListener( new WindowAdapter()
        {
            @Override
            public void windowStateChanged(WindowEvent e)
            {
                isWindowMaximized = (getExtendedState() == JFrame.MAXIMIZED_VERT || getExtendedState() == JFrame.MAXIMIZED_HORIZ || getExtendedState() == JFrame.MAXIMIZED_BOTH);
                if (isWindowMaximized)
                {
                    //FIXME this whole thing smells bad
                    if ( splitPane != null && splitPane.getDividerLocation() == (classViewerWidthValue / 2) ) // only do this if the slit pane divider is in original location (classViewerWidthValue / 2) of the Class Viewer
                    {
                        validate();
                        splitPane.setDividerLocation(classViewerFrame.getWidth() / 2);
                        prevSplitPaneDividerLocation = splitPane.getDividerLocation();
                    }
                }
            }
        } );
    }

    /**
     * Displays the Class Viewer. May be called by an Action or programmatically.
     * Initializes the Class Viewer if not already visible.
     * If visible, deiconifies the frame, maximizes and brings to the front.
     */
    public void displayClassViewer()
    {
        if ( !isVisible() )
        {
            initializeCommonComponents();

            if (getExtendedState() != JFrame.NORMAL)
                setExtendedState(JFrame.NORMAL);

            classViewerWidthValue = (SCREEN_DIMENSION.width > 1280) ? (int)(0.75 * SCREEN_DIMENSION.width) : 1010;
            int classViewerHeightValue = (SCREEN_DIMENSION.height > 1024) ? (int)(0.75 * SCREEN_DIMENSION.height) : 680;
            setSize(classViewerWidthValue, classViewerHeightValue);
            setLocationRelativeTo(null);
            setVisible(true);

            if ( ( this.getWidth() + 1.5 * classViewerHideColumnsDialog.getWidth() ) > SCREEN_DIMENSION.width )
                classViewerHideColumnsDialog.setLocation( ( SCREEN_DIMENSION.width - this.getWidth() ) / 2, ( SCREEN_DIMENSION.height - classViewerHideColumnsDialog.getHeight() ) / 2 );
            else
                classViewerHideColumnsDialog.setLocation( ( SCREEN_DIMENSION.width - this.getWidth() ) / 2 - classViewerHideColumnsDialog.getWidth(), ( SCREEN_DIMENSION.height - classViewerHideColumnsDialog.getHeight() ) / 2 );

            // only if expression data is loaded, otherwise the divider location will have been already set to 0
            if (splitPane != null)
            {
                splitPane.setDividerLocation(this.getWidth() / 2);
                prevSplitPaneDividerLocation = splitPane.getDividerLocation();
            }

            // make sure to clear all plot/tables if current selection is empty
            if ( layoutFrame.getGraph().getSelectionManager().getSelectedNodes().isEmpty() )
                populateClassViewer();

            if (plotPanel != null)
            {
                plotPanel.onFirstShown();
            }
        }
        else
        {
            processAndSetWindowState();
        }
    }

    private void initActions(final ClassViewerFrame classViewerFrame)
    {
        classViewerDialogAction = new AbstractAction("Class Viewer")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555992L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                displayClassViewer();
            }
        };
        classViewerDialogAction.setEnabled(false);

        findNameAction = new AbstractAction("Find By Name")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 112222333444555993L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                findNameDialog.setVisible(true);
            }
        };

        findClassAction = new AbstractAction("Find By Class")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 112222333444555993L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                findClassDialog.setVisible(true);
            }
        };

        findMultipleClassesAction = new AbstractAction("Find By Multiple Classes")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 112222333444555993L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                findMultipleClassesDialog.openDialogWindow();
            }
        };

        previousClassAction = new AbstractAction("◄◄ (Previous Class)")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 112222333444555993L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                navigateToPreviousClass();
            }
        };

        nextClassAction = new AbstractAction("►► (Next Class)")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 112222333444555993L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                navigateToNextClass();
            }
        };

        refreshSelectionInTableAction = new AbstractAction("Hide Unselected Rows")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555993L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                populateClassViewer(false, true);
            }
        };

        chooseColumnsToHideAction = new AbstractAction("Choose Columns To Hide")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555993L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                classViewerHideColumnsDialog.setVisible(true);
            }
        };

        exportTableToFileAction = new AbstractAction("Export Table As...")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555793L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                save();
            }
        };

        okAction = new AbstractAction("Close")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555793L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                closeClassViewerWindow();
            }
        };

        detailsAction = new AbstractAction("Details")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555794L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                // disable any running thread
                checkAndAbortUpdateEntropyTableRunnable();
                checkAndAbortUpdateDetailedEntropyTableRunnable();

                updateEntropyTableRunnable = new ClassViewerUpdateEntropyTable(classViewerFrame, layoutFrame, annotationClass, analysisTableModel, selectedGenes, tabbedPane);
                executeRunnableInThread(updateEntropyTableRunnable);
            }
        };

        detailsOfAllAction = new AbstractAction("Details For All")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555795L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                // disable any running thread
                checkAndAbortUpdateEntropyTableRunnable();
                checkAndAbortUpdateDetailedEntropyTableRunnable();

                updateDetailedEntropyTableRunnable = new ClassViewerUpdateDetailedEntropyTable(classViewerFrame, layoutFrame, analysisTableModel, selectedGenes, tabbedPane);
                executeRunnableInThread(updateDetailedEntropyTableRunnable);
            }
        };
    }

    public void processAndSetWindowState()
    {
        // this process deiconifies a frame, the maximized bits are not affected
        if (isWindowIconified)
        {
            int iconifyState = this.getExtendedState();

            // set the iconified bit, inverse process
            // deIconifyState |= Frame.ICONIFIED;

            // clear the iconified bit
            iconifyState &= ~JFrame.ICONIFIED;

            // deiconify the frame
            this.setExtendedState(iconifyState);

            if (windowWasMaximizedBeforeIconification)
            {
                // this process maximizes a frame; the iconified bit is not affected
                int maximizeState = this.getExtendedState();

                // clear the maximized bits, inverse process
                // minimizeState &= ~Frame.MAXIMIZED_BOTH;

                // set the maximized bits
                maximizeState |= JFrame.MAXIMIZED_BOTH;

                // maximize the frame
                this.setExtendedState(maximizeState);
            }
        }

        this.toFront();
    }

    private void initializeCommonComponents()
    {
        if (DATA_TYPE.equals(DataTypes.EXPRESSION) && !layoutFrame.getExpressionData().isTransposed())
        {
            // Expression data
            plotPanel = new ExpressionGraphPanel(this, layoutFrame, layoutFrame.getExpressionData());
        }
        else if (DATA_TYPE.equals(DataTypes.GRAPHML) && layoutFrame.getNetworkRootContainer().getIsPetriNet())
        {
            // SPN simulation data
            plotPanel = new SimulationResultsPanel(this, layoutFrame);
        }
        else
        {
            // No plot at all
            plotPanel = null;
        }

        if (splitPane != null && Arrays.asList(tabGeneralPanel.getComponents()).contains(splitPane))
        {
            tabGeneralPanel.remove(splitPane);
        }
        else if (Arrays.asList(tabGeneralPanel.getComponents()).contains(generalTablePanel))
        {
            tabGeneralPanel.remove(generalTablePanel);
        }

        if (plotPanel != null)
        {
            plotPanel.setMinimumSize(new Dimension(300, 300));

            splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, plotPanel, generalTablePanel);
            splitPane.setOneTouchExpandable(true);
            splitPane.setContinuousLayout(false);
            tabGeneralPanel.add(splitPane, BorderLayout.CENTER);

            AbstractAction renderAllCurrentClassSetPlotImagesToFilesAction =
                    plotPanel.getRenderAllCurrentClassSetPlotImagesToFilesAction();
            if (renderAllCurrentClassSetPlotImagesToFilesAction != null)
            {
                renderAllCurrentClassSetPlotImagesToFilesAction.setEnabled(true);
                renderAllCurrentClassSetPlotImagesToFilesButton.setAction(
                        renderAllCurrentClassSetPlotImagesToFilesAction);
                renderAllCurrentClassSetPlotImagesToFilesButton.setVisible(true);
            }
            else
            {
                renderAllCurrentClassSetPlotImagesToFilesButton.setVisible(false);
            }

            AbstractAction renderPlotImageToFileAction = plotPanel.getRenderPlotImageToFileAction();
            if (renderPlotImageToFileAction != null)
            {
                renderPlotImageToFileAction.setEnabled(true);
                renderPlotImageToFileButton.setAction(renderPlotImageToFileAction);
                renderPlotImageToFileButton.setVisible(true);
            }
            else
            {
                renderPlotImageToFileButton.setVisible(false);
            }
        }
        else
        {
            tabGeneralPanel.add(generalTablePanel, BorderLayout.CENTER);
            splitPane = null;

            renderAllCurrentClassSetPlotImagesToFilesButton.setVisible(false);
            renderPlotImageToFileButton.setVisible(false);
        }

        tableModelGeneral.proccessSelected( viewAllClassSets.isSelected() );
        selectedGenes = entropyTableModel.proccessSelected();

        ClassComboBox classComboBox = new ClassComboBox(layoutFrame.getLayoutClassSetsManager().getCurrentClassSetAllClasses(), false, false);

        if ( !selectedGenes.isEmpty() )
            classSetsBox.setSelectedItem( layoutFrame.getLayoutClassSetsManager().getCurrentClassSetAllClasses().getClassSetName() );

        // Clear it out
        ClassViewerTableModelGeneral model = (ClassViewerTableModelGeneral)generalTable.getModel();
        model.clear();

        generalTable.setDefaultEditor( VertexClass.class, new DefaultCellEditor(classComboBox) );
        generalTable.setDefaultRenderer( VertexClass.class, classComboBox.getClassRenderer() );
        generalTable.setHighlightIsSelection(false);
        highlightIsSelectionCheckbox.setSelected(false);

        if (DEBUG_BUILD) println("Reinit Due to Initial Init.");

        if ( !selectedGenes.isEmpty() )
        {
            populateClassViewer(false, true); //to update the classComboBox and generalTable with the current selection
        }
    }

    private void checkAndAbortUpdateEntropyTableRunnable()
    {
        // abort previous thread & sleep before initializing a new one!
        if (updateEntropyTableRunnable != null)
        {
            if ( !updateEntropyTableRunnable.getAbortThread() )
            {
                updateEntropyTableRunnable.setAbortThread(true);

                if (updateEntropyTableRunnable.getThreadStarted())
                {
                    while (!updateEntropyTableRunnable.getThreadFinished());
                }
            }
        }
    }

    private void checkAndAbortUpdateDetailedEntropyTableRunnable()
    {
        // abort previous thread & sleep before initializing a new one!
        if (updateDetailedEntropyTableRunnable != null)
        {
            if ( !updateDetailedEntropyTableRunnable.getAbortThread() )
            {
                updateDetailedEntropyTableRunnable.setAbortThread(true);

                if (updateDetailedEntropyTableRunnable.getThreadStarted())
                {
                    while (!updateDetailedEntropyTableRunnable.getThreadFinished());
                }
            }
        }
    }

    private void executeRunnableInThread(Runnable runnable)
    {
        Thread executeThread = new Thread(runnable);
        executeThread.setPriority(Thread.NORM_PRIORITY);
        executeThread.start();
    }

    private void initComponents()
    {
        if (DEBUG_BUILD) println("Create Class Viewer Frame Elements.");

        //// GENERAL PANEL ////
        tabGeneralPanel = new JPanel(true);
        tabGeneralPanel.setLayout( new BorderLayout() );
        tabGeneralPanel.setBackground(Color.WHITE);

        // topPanel, north
        JPanel generalTopPanel = new JPanel(true);

        classSetsBox = new JComboBox<String>();
        classSetsBox.addItemListener(this);
        classSetsBox.setToolTipText("Class Set");

        viewAllClassSets = new JCheckBox("Show All Class Sets");
        viewAllClassSets.addActionListener(this);
        viewAllClassSets.setToolTipText("Show All Class Sets");

        autoSizeColumnsCheckBox = new JCheckBox("Auto Size Columns");
        autoSizeColumnsCheckBox.addActionListener(this);
        autoSizeColumnsCheckBox.setToolTipText("Auto Size Columns");
        autoSizeColumnsCheckBox.setSelected(CV_AUTO_SIZE_COLUMNS.get());

        // generalTable, center
        tableModelGeneral = new ClassViewerTableModelGeneral(layoutFrame, this);
        generalTable = new ClassViewerTable(tableModelGeneral, ClassViewerTableModelGeneral.ORIGINAL_COLUMN_NAMES, CV_AUTO_SIZE_COLUMNS.get());
        generalTableSorter = new TableRowSorter<ClassViewerTableModelGeneral>(tableModelGeneral);
        generalTable.setRowSorter(generalTableSorter); // provide a sorting mechanism to the table
        generalTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        generalTable.setAutoscrolls(true);
        generalTable.sortTableByColumn(NAME_COLUMN, generalTableSorter);

        JScrollPane scrollPane = new JScrollPane(generalTable);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        setUpStringEditor(generalTable);

        generalTablePanel = new JPanel(true);
        generalTablePanel.setLayout( new BoxLayout(generalTablePanel, BoxLayout.Y_AXIS) );
        generalTablePanel.add(scrollPane);

        selectDeselectAllButton = createSelectDeselectAllButton();
        selectDeselectAllButton.setToolTipText("Deselect All");

        highlightIsSelectionCheckbox = createHighlightIsSelectionButton();
        highlightIsSelectionCheckbox.setToolTipText("Highlight Is Selection");

        JPanel generalTableButtonPanel = new JPanel(true);
        generalTableButtonPanel.setLayout( new BoxLayout(generalTableButtonPanel, BoxLayout.X_AXIS) );
        generalTableButtonPanel.add(highlightIsSelectionCheckbox);
        generalTableButtonPanel.add(selectDeselectAllButton);

        // generalTableButtonPanel.add( Box.createRigidArea( new Dimension(10, 10) ) );
        // generalTableButtonPanel.add( new JButton("Dummy Button 2") );

        generalTablePanel.add( Box.createRigidArea( new Dimension(10, 10) ) );
        generalTablePanel.add(generalTableButtonPanel);
        generalTablePanel.add( Box.createRigidArea( new Dimension(10, 10) ) );

        // button panel, south
        JPanel generalButtonPanel = new JPanel(true);

        // expression graph GUI component
        findNameDialog = new FindNameDialog(layoutFrame, this);
        findClassDialog = new FindClassDialog(layoutFrame, this);
        findMultipleClassesDialog = new FindMultipleClassesDialog(layoutFrame, this);

        //Provide minimum sizes for the two components in the split pane
        scrollPane.setMinimumSize( new Dimension(400, 300) );

        findNameButton = new JButton(findNameAction);
        findNameButton.setToolTipText("Find By Name");
        findClassButton = new JButton(findClassAction);
        findClassButton.setToolTipText("Find By Class");
        findMultipleClassesButton = new JButton(findMultipleClassesAction);
        findMultipleClassesButton.setToolTipText("Find By Multiple Classes");
        previousClassButton = new JButton(previousClassAction);
        previousClassButton.setEnabled(false);
        previousClassButton.setToolTipText("◄◄ (Previous Class)");
        nextClassButton = new JButton(nextClassAction);
        nextClassButton.setEnabled(false);
        nextClassButton.setToolTipText("►► (Next Class)");

        renderAllCurrentClassSetPlotImagesToFilesButton = new JButton();
        renderAllCurrentClassSetPlotImagesToFilesButton.setToolTipText("Render Class Set Plots To Files...");
        generalTopPanel.add(renderAllCurrentClassSetPlotImagesToFilesButton);

        renderPlotImageToFileButton = new JButton();
        renderPlotImageToFileButton.setToolTipText("Render Plot To File...");
        generalTopPanel.add(renderPlotImageToFileButton);

        generalButtonPanel.add(findNameButton);
        generalButtonPanel.add(findClassButton);
        generalButtonPanel.add(findMultipleClassesButton);
        generalButtonPanel.add( Box.createRigidArea( new Dimension(10, 10) ) );
        generalButtonPanel.add(previousClassButton);
        generalButtonPanel.add(nextClassButton);
        generalButtonPanel.add( Box.createRigidArea( new Dimension(20, 10) ) );

        refreshSelectionInTableButton = new JButton(refreshSelectionInTableAction);
        refreshSelectionInTableButton.setEnabled(false);
        refreshSelectionInTableButton.setToolTipText("Hide Unselected Rows");

        exportTableAsButton = new JButton(exportTableToFileAction);
        exportTableAsButton.setEnabled(false);
        exportTableAsButton.setToolTipText("Export Table As...");

        chooseColumnsToHideButton = new JButton(chooseColumnsToHideAction);
        chooseColumnsToHideButton.setEnabled(false);
        chooseColumnsToHideButton.setToolTipText("Choose Columns To Hide");

        /* want to add the same Action as the Import Network menu but if try
        to add here causes NullPointerException as LayoutFrame not fully set up yet
        so add Action later when button is enabled */
        searchDatabaseButton = new JButton();
        searchDatabaseButton.setEnabled(false);
        searchDatabaseButton.setText("Search Database");
        searchDatabaseButton.setToolTipText("Search Online Database");

        // topPanel, north
        generalTopPanel.add( new JLabel("Class Set:") );
        generalTopPanel.add(classSetsBox);
        generalTopPanel.add(viewAllClassSets);
        generalTopPanel.add(refreshSelectionInTableButton);
        generalTopPanel.add(autoSizeColumnsCheckBox);

        tabGeneralPanel.add(generalTopPanel, BorderLayout.NORTH);

        // button panel, south
        generalButtonPanel.add(chooseColumnsToHideButton);
        generalButtonPanel.add(exportTableAsButton);
        generalButtonPanel.add(searchDatabaseButton);
        JButton okButton = new JButton(okAction);
        okButton.setToolTipText("Close");
        generalButtonPanel.add(okButton);

        tabGeneralPanel.add(generalButtonPanel, BorderLayout.SOUTH);

        //// ENTROPY PANEL ////
        JPanel tabEntropyPanel = new JPanel(true);
        tabEntropyPanel.setLayout( new BorderLayout() );
        tabEntropyPanel.setBackground(Color.WHITE);

        // entropy, center
        entropyTableModel = new ClassViewerTableModelAnalysis(layoutFrame);
        entropyTable = new ClassViewerTable(entropyTableModel, ClassViewerTableModelAnalysis.COLUMN_NAMES, CV_AUTO_SIZE_COLUMNS.get());
        entropyTable.setRowSorter( new TableRowSorter<ClassViewerTableModelAnalysis>(entropyTableModel) ); // provide a sorting mechanism to the table
        entropyTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        entropyTable.setAutoscrolls(true);
        entropyTable.setDefaultRenderer(Double.class, new EntropyTableCellRenderer());
        ( (DefaultTableCellRenderer)entropyTable.getTableHeader().getDefaultRenderer() ).setHorizontalAlignment(SwingConstants.CENTER);

        ListSelectionModel listModel = entropyTable.getSelectionModel();
        listModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listModel.addListSelectionListener(this);

        JScrollPane scrollPaneEntropy = new JScrollPane(entropyTable);
        scrollPaneEntropy.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPaneEntropy.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        tabEntropyPanel.add(scrollPaneEntropy, BorderLayout.CENTER);

        // ok, south
        JPanel okButtonPanel = new JPanel(true);
        detailsButton = new JButton(detailsAction);
        detailsButton.setEnabled(false);
        detailsButton.setToolTipText("Details");
        detailsForAllButton = new JButton(detailsOfAllAction);
        detailsForAllButton.setEnabled(false);
        detailsForAllButton.setToolTipText("Details For All");

        okButtonPanel.add(detailsButton);
        okButtonPanel.add(detailsForAllButton);
        okButton = new JButton(okAction);
        okButton.setToolTipText("Close");
        okButtonPanel.add(okButton);
        tabEntropyPanel.add(okButtonPanel, BorderLayout.SOUTH);

        // ANALYSIS DETAILS PANEL
        JPanel tabEntropyDetailPanel = new JPanel(true);
        tabEntropyDetailPanel.setLayout( new BorderLayout() );
        tabEntropyDetailPanel.setBackground(Color.WHITE);

        // analysis table
        analysisTableModel = new ClassViewerTableModelDetail();
        ClassViewerTable analysisDetailsTable = new ClassViewerTable(analysisTableModel, ClassViewerTableModelDetail.COLUMN_NAMES, CV_AUTO_SIZE_COLUMNS.get());
        analysisDetailsTable.setRowSorter( new TableRowSorter<ClassViewerTableModelDetail>(analysisTableModel) ); // provide a sorting mechanism to the table
        analysisDetailsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        analysisDetailsTable.setAutoscrolls(true);
        analysisDetailsTable.setDefaultRenderer(Double.class, new EntropyTableCellRenderer());
        analysisDetailsTable.setDefaultRenderer(Integer.class, new EntropyTableCellRenderer());
        ( (DefaultTableCellRenderer)analysisDetailsTable.getTableHeader().getDefaultRenderer() ).setHorizontalAlignment(SwingConstants.CENTER);

        JScrollPane scrollPaneEntropyDetails = new JScrollPane(analysisDetailsTable);
        scrollPaneEntropyDetails.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPaneEntropyDetails.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        tabEntropyDetailPanel.add(scrollPaneEntropyDetails, BorderLayout.CENTER);

        // ok, south
        JPanel okButtonPanelDetails = new JPanel(true);
        okButton = new JButton(okAction);
        okButton.setToolTipText("Close");
        okButtonPanelDetails.add(okButton);
        tabEntropyDetailPanel.add(okButtonPanelDetails, BorderLayout.SOUTH);

        // create & add to tab pane
        tabbedPane = new JTabbedPane();
        tabbedPane.insertTab( "General", null, tabGeneralPanel, "General Node Information", GENERAL_TAB.ordinal() );
        tabbedPane.insertTab( "Analysis", null, tabEntropyPanel, "Analysis Calculations", ENTROPY_TAB.ordinal() );
        tabbedPane.add("Analysis Per Term", tabEntropyDetailPanel);
        tabbedPane.insertTab( "Analysis Detailed", null, tabEntropyDetailPanel, "Shows Analysis Per Term", ENTROPY_DETAILS_TAB.ordinal() );
        tabbedPane.setEnabledAt(ENTROPY_DETAILS_TAB.ordinal(), false);
        tabbedPane.addChangeListener(this);

        // add tab pane to content pane
        this.getContentPane().add(tabbedPane);

        // this.pack();
        this.setSize(800, 680);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);

        // at end the ClassViewerHideColumns initialization, to have already initialized the ClassViewer setLocation() method
        classViewerHideColumnsDialog = new ClassViewerHideColumnsDialog(this);
    }

    private JButton createSelectDeselectAllButton()
    {
        return new JButton( new AbstractAction("Deselect All")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555691L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                selectDeselectAllButtonModeState = !selectDeselectAllButtonModeState;
                String buttonText = ( (!selectDeselectAllButtonModeState) ? "Deselect" : "Select" ) + " All" ;
                selectDeselectAllButton.setText(buttonText);
                selectDeselectAllButton.setToolTipText(buttonText);
                tableModelGeneral.setSelectedAllRows(!selectDeselectAllButtonModeState);
                generalTable.setHighlightIsSelection(highlightIsSelectionCheckbox.isSelected());
            }
        } );
    }

    private JCheckBox createHighlightIsSelectionButton()
    {
        return new JCheckBox( new AbstractAction("Highlight Is Selection")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                generalTable.setHighlightIsSelection(highlightIsSelectionCheckbox.isSelected());
            }
        } );
    }

    private void resetSelectDeselectAllButton()
    {
        this.selectDeselectAllButtonModeState = false;
        selectDeselectAllButton.setText("Deselect All");
    }

    private void initExportTableViewToFileChooser()
    {
        String saveFilePath = FILE_CHOOSER_PATH.get().substring(0, FILE_CHOOSER_PATH.get().lastIndexOf( System.getProperty("file.separator") ) + 1);
        exportTableViewToFileChooser = new JFileChooser(saveFilePath);
        fileNameExtensionFilterText = new FileNameExtensionFilter("Save as a Text File", "txt");
        exportTableViewToFileChooser.setFileFilter(fileNameExtensionFilterText);
        exportTableViewToFileChooser.setDialogTitle("Export Table View As");
    }

    public AbstractAction getClassViewerAction()
    {
        return classViewerDialogAction;
    }

    private void setUpStringEditor(JTable table)
    {
        final JTextField textField = new JTextField();

        DefaultCellEditor stringEditor = new DefaultCellEditor(textField)
        {
            /**
            *  Serial version UID variable for the DefaultCellEditor class.
            */
            public static final long serialVersionUID = 111222333444555796L;

            @Override
            public Object getCellEditorValue()
            {
                return textField.getText();
            }
        };

        table.setDefaultEditor(String.class, stringEditor);
    }

    public void populateClassViewer()
    {
        populateClassViewer(null, false, false, true);
    }

    public void populateClassViewer(Object[][] hideColumnsData)
    {
        populateClassViewer(hideColumnsData, false, false, true);
    }

    public void populateClassViewer(boolean updateExpressionGraphViewOnly, boolean notUpdateTitleBar)
    {
        populateClassViewer(null, updateExpressionGraphViewOnly, notUpdateTitleBar, true);
    }

    public void populateClassViewer(Object[][] hideColumnsData, boolean updateExpressionGraphViewOnly, boolean notUpdateTitleBar, boolean refreshPlot)
    {
        NetworkContainer nc = layoutFrame.getNetworkRootContainer();
        if (nc != null)
        {
            if (!updateExpressionGraphViewOnly)
            {
                if (DEBUG_BUILD) println("populateClassViewer(): " + layoutFrame.getLayoutClassSetsManager().getCurrentClassSetAllClasses().getClassSetName());

                HashSet<GraphNode> currentSelection = layoutFrame.getGraph().getSelectionManager().getSelectedNodes();

                // only update, if a new set of nodes is selected or it is mandatory
                if ( !oldSelection.equals(currentSelection) )
                {
                    oldSelection = new HashSet<GraphNode>(currentSelection); // don't use the reference
                    if ( !( tabbedPane.getTabCount() < ENTROPY_DETAILS_TAB.ordinal() ) )
                    {
                        tabbedPane.setEnabledAt(ENTROPY_DETAILS_TAB.ordinal(), false);
                        if ( tabbedPane.getSelectedIndex() == ENTROPY_DETAILS_TAB.ordinal() )
                            tabbedPane.setSelectedIndex( ENTROPY_TAB.ordinal() );

                        analysisTableModel.setTerm2Entropy(null, null, null, null, null, null, null, null, null, null, null);
                    }
                }

                tableModelGeneral.proccessSelected(viewAllClassSets.isSelected(), hideColumnsData);
                generalTable.updateTableColumnNames( getGeneralTableColumnNames() );

                refreshTables();

                generalTable.setAutoCreateColumnsFromModel(false);

                boolean enableHideColumnsAndExportButtons = (generalTable.getRowCount() > 0);
                refreshSelectionInTableButton.setEnabled(enableHideColumnsAndExportButtons);
                exportTableAsButton.setEnabled(enableHideColumnsAndExportButtons);
                chooseColumnsToHideButton.setEnabled( enableHideColumnsAndExportButtons || classViewerHideColumnsDialog.isVisible() );

                //reuse the Action from the Import Network menu option
                searchDatabaseButton.setAction(layoutFrame.getImportWebService().getImportWebServiceAction());
                searchDatabaseButton.setText("Search Database"); //don't want to use same text as Action here
                searchDatabaseButton.setToolTipText("Search Online Database");
                searchDatabaseButton.setEnabled(enableHideColumnsAndExportButtons);

                boolean enableDetailsForAllButton = (entropyTable.getRowCount() > 0);
                detailsForAllButton.setEnabled(enableDetailsForAllButton);

                // disable any running thread
                checkAndAbortUpdateEntropyTableRunnable();
                checkAndAbortUpdateDetailedEntropyTableRunnable();

                if (hideColumnsData == null && classViewerHideColumnsDialog != null)
                    classViewerHideColumnsDialog.updateClassViewerHideColumnsTable(this, enableHideColumnsAndExportButtons, updateExpressionGraphViewOnly, notUpdateTitleBar);

                if (generalTable.getColumnCount() < NAME_COLUMN + 1)
                {
                    System.out.println("generalTable.getColumnCount() " + generalTable.getColumnCount() + " < NAME_COLUMN + 1 " + Thread.currentThread().getStackTrace());
                    /*JOptionPane.showMessageDialog(this, "It is possible a crash is about to occur in relation to the number of columns in the class viewer (currently " +
                            generalTable.getColumnCount() + "). Please note down what you were doing up until this point.",
                            "Probable crash imminent", JOptionPane.INFORMATION_MESSAGE);*/
                }
                generalTable.sortTableByColumn(NAME_COLUMN, generalTableSorter);
            }

            if (plotPanel != null)
            {
                if (refreshPlot)
                {
                    plotPanel.refreshPlot();
                }

                plotPanel.repaint();
            }

            generalTable.repaint();

            checkClassViewerNavigationButtons();
            if (updateResetSelectDeselectAllButton)
                resetSelectDeselectAllButton();
            if (!notUpdateTitleBar)
                setCurrentClassName("");
        }
    }

    public void setCurrentClassIndex(int currentClassIndex)
    {
        findClassDialog.setCurrentClassIndex(currentClassIndex);
    }

    public int numberOfAllClasses()
    {
        return findClassDialog.numberOfAllClasses();
    }

    public int getClassIndex()
    {
        return findClassDialog.getClassIndex();
    }

    public void synchroniseHighlightWithSelection()
    {
        if (generalTable != null)
        {
            generalTable.synchroniseHighlightWithSelection();
        }
    }

    public VertexClass navigateToCurrentClass()
    {
        VertexClass currentVertexClass = findClassDialog.currentVertexClass();
        if (currentVertexClass != null)
        {
            setUpdateResetSelectDeselectAllButton(false);
            layoutFrame.getGraph().getSelectionManager().selectByClass(currentVertexClass);
            generalTable.getDefaultEditor(String.class).stopCellEditing();
            layoutFrame.getGraph().updateSelectedNodesDisplayList();
            setCurrentClassName( currentVertexClass.getName() );

            nextClassButton.setEnabled(true);
            setUpdateResetSelectDeselectAllButton(true);
            synchroniseHighlightWithSelection();
        }

        return currentVertexClass;
    }

    private VertexClass navigateToPreviousClass()
    {
        VertexClass previousVertexClass = findClassDialog.previousVertexClass();
        if (previousVertexClass != null)
        {
            setUpdateResetSelectDeselectAllButton(false);
            layoutFrame.getGraph().getSelectionManager().selectByClass(previousVertexClass);
            generalTable.getDefaultEditor(String.class).stopCellEditing();
            layoutFrame.getGraph().updateSelectedNodesDisplayList();
            setCurrentClassName( previousVertexClass.getName() );

            nextClassButton.setEnabled(true);
            setUpdateResetSelectDeselectAllButton(true);
            synchroniseHighlightWithSelection();
        }

        previousClassButton.setEnabled( findClassDialog.checkPreviousVertexClass() );

        return previousVertexClass;
    }

    private VertexClass navigateToNextClass()
    {
        return navigateToNextClass(true);
    }

    public VertexClass navigateToNextClass(boolean enableTitleBarUpdate)
    {
        VertexClass nextVertexClass = findClassDialog.nextVertexClass();
        if (nextVertexClass != null)
        {
            setUpdateResetSelectDeselectAllButton(false);
            layoutFrame.getGraph().getSelectionManager().selectByClass(nextVertexClass);
            generalTable.getDefaultEditor(String.class).stopCellEditing();
            layoutFrame.getGraph().updateSelectedNodesDisplayList();
            if (enableTitleBarUpdate)
                setCurrentClassName( nextVertexClass.getName() );

            previousClassButton.setEnabled(findClassDialog.getClassIndex() != 0);
            setUpdateResetSelectDeselectAllButton(true);
            synchroniseHighlightWithSelection();
        }

        nextClassButton.setEnabled( findClassDialog.checkNextVertexClass() );

        return nextVertexClass;
    }

    private void checkClassViewerNavigationButtons()
    {
        previousClassButton.setEnabled( findClassDialog.checkPreviousVertexClass() );
        nextClassButton.setEnabled( findClassDialog.checkNextVertexClass() );
    }

    private void refreshTables()
    {
        if ( tabbedPane.getSelectedIndex() == GENERAL_TAB.ordinal() )
        {
            if (DEBUG_BUILD) println("refreshTables() General Tab.");

            setUpdateResetSelectDeselectAllButton(false);
            rebuildClassSets();
            tableModelGeneral.fireTableStructureChanged();
            setVertexClassSortingToGeneralTable();
            setUpdateResetSelectDeselectAllButton(true);
        }
        else if ( tabbedPane.getSelectedIndex() == ENTROPY_TAB.ordinal() )
        {
            if (DEBUG_BUILD) println("refreshTables() Entropy Tab.");

            selectedGenes = entropyTableModel.proccessSelected();
            entropyTableModel.fireTableStructureChanged();
        }
    }

    private void rebuildClassSets()
    {
        if (DEBUG_BUILD) println("Rebuilding ClassSets for the Class Viewer.");

        rebuildClassSets = true;
        classSetsBox.removeAllItems();
        addClassSets(classSetsBox);

        classSetsBox.setSelectedItem( layoutFrame.getLayoutClassSetsManager().getCurrentClassSetAllClasses().getClassSetName() );

        rebuildClassSets = false;
    }

    private void setVertexClassSortingToGeneralTable()
    {
        String[] generalTableColumnNames = getGeneralTableColumnNames();
        for (int i = 0; i < generalTableColumnNames.length; i++)
            if ( !tableModelGeneral.findNonVertexClassColumnNamesInOriginalColumnNameArray(generalTableColumnNames[i]) )
                generalTableSorter.setComparator( i, new ClassViewerTable.VertexClassSorting() );
    }

    private void addClassSets(JComboBox<String> comboBox)
    {
        comboBox.removeAllItems();

        if (DEBUG_BUILD) println("Adding Class Sets to Class Viewer.");

        if (layoutFrame.getNetworkRootContainer() != null)
        {
            if (layoutFrame.getNetworkRootContainer().getLayoutClassSetsManager() != null)
            {
                for ( LayoutClasses classes : layoutFrame.getNetworkRootContainer().getLayoutClassSetsManager().getClassSetNames() )
                    comboBox.addItem( classes.getClassSetName() );
            }
        }
    }

    private void save()
    {
        int dialogReturnValue = 0;
        boolean doSaveFile = false;
        File saveFile = null;

        exportTableViewToFileChooser.setSelectedFile( new File( IOUtils.getPrefix( layoutFrame.getFileNameLoaded() ) + "_Table_View" ) );

        if (exportTableViewToFileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            String extension = fileNameExtensionFilterText.getExtensions()[0];
            String fileName = exportTableViewToFileChooser.getSelectedFile().getAbsolutePath();
            if ( fileName.endsWith(extension) ) fileName = IOUtils.getPrefix(fileName);

            saveFile = new File(fileName + "." + extension);

            if ( saveFile.exists() )
            {
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
            saveExportTableViewFile(saveFile);
            FILE_CHOOSER_PATH.set( saveFile.getAbsolutePath() );
        }
    }

    private void saveExportTableViewFile(File file)
    {
        try
        {
            FileWriter fileWriter = new FileWriter(file);
            for (int j = 1; j < generalTable.getColumnCount(); j++)
                fileWriter.write(generalTable.getModel().getColumnName(j) + "\t");
            fileWriter.write("\n");

            for (int i = 0; i < generalTable.getRowCount(); i++)
            {
                for (int j = 1; j < generalTable.getColumnCount(); j++)
                {
                    Object value = generalTable.getValueAt(i, j);

                    if (value != null)
                    {
                        fileWriter.write(value.toString() + "\t");
                    }
                    else
                    {
                        fileWriter.write("\t");
                    }
                }
                fileWriter.write("\n");
            }

            fileWriter.flush();
            fileWriter.close();

            InitDesktop.edit(file);
        }
        catch (IOException ioe)
        {
            if (DEBUG_BUILD) println("Exception in saveExportTableViewFile():\n" + ioe.getMessage());

            JOptionPane.showMessageDialog(this, "Something went wrong while saving the file:\n" + ioe.getMessage() + "\nPlease try again with a different file name/path/drive.", "Error with saving the file!", JOptionPane.ERROR_MESSAGE);
            save();
        }
    }

    public void refreshCurrentClassSetSelection()
    {
        findClassDialog.resetCurrentClassIndex();

        // nextClassButton.doClick();
        // fire actionPerformed event directly, thus effectively avoiding the JButton being pressed down for some time
        nextClassAction.actionPerformed( new ActionEvent(nextClassAction, ActionEvent.ACTION_PERFORMED, "") );

        if (plotPanel != null)
        {
            if (plotPanel.getRenderAllCurrentClassSetPlotImagesToFilesAction() != null)
            {
                plotPanel.getRenderAllCurrentClassSetPlotImagesToFilesAction().setEnabled(
                        (layoutFrame.getLayoutClassSetsManager().getCurrentClassSetAllClasses().getTotalClasses() > 0));
            }

            if (plotPanel.getRenderPlotImageToFileAction() != null)
            {
                plotPanel.getRenderPlotImageToFileAction().setEnabled(true);
            }
        }
    }

    public void closeClassViewerWindow()
    {
        // disable any running threads
        checkAndAbortUpdateEntropyTableRunnable();
        checkAndAbortUpdateDetailedEntropyTableRunnable();

        generalTable.getDefaultEditor(String.class).stopCellEditing();

        setVisible(false);
    }

    /**
    *   Process a light-weight thread using the Adapter technique to avoid any GUI latencies with the JButton setEnabled() update.
    */
    private void runLightWeightThread(int threadPriority)
    {
        Thread runLightWeightThread = new Thread( new Runnable()
        {
            @Override
            public void run()
            {
                boolean enableDetailsButton = !annotationClass.isEmpty();

                // used as an 'animation' trick to visually show JButton update when moved to another row!
                if ( detailsButton.isEnabled() )
                {
                    detailsButton.setEnabled(false);
                    LayoutFrame.sleep(TIME_TO_SLEEP_TO_ABORT_THREADS);
                }

                detailsButton.setEnabled(enableDetailsButton);
            }
        }, "runLightWeightThread" );

        runLightWeightThread.setPriority(threadPriority);
        runLightWeightThread.start();
    }

    public String[] getGeneralTableColumnNames()
    {
        return tableModelGeneral.getColumnNames();
    }

    public JButton getChooseColumnsToHideButton()
    {
        return chooseColumnsToHideButton;
    }

    public void setCurrentClassName(String currentClassName)
    {
        this.currentClassName = currentClassName;

        int numberOfSelectedNodes = layoutFrame.getGraph().getSelectionManager().getSelectedNodes().size();
        this.setTitle("Class Viewer " + ( ( !currentClassName.isEmpty() ) ? "(Current Class Selected: " + ( (numberOfSelectedNodes > 0) ? currentClassName + " with " + numberOfSelectedNodes + " nodes" : currentClassName ) + ")" : "" ) );
    }

    public String getCurrentClassName()
    {
        return currentClassName;
    }

    public void setUpdateResetSelectDeselectAllButton(boolean updateResetSelectDeselectAllButton)
    {
        this.updateResetSelectDeselectAllButton = updateResetSelectDeselectAllButton;
        generalTable.setUpdateResetSelectDeselectAllButton(updateResetSelectDeselectAllButton);
    }

    @Override
    public void itemStateChanged(ItemEvent e)
    {
        if (!rebuildClassSets)
        {
            // if ( (e.getStateChange() == ItemEvent.SELECTED) && ( e.getSource().equals(classSetsBox) ) )
            if ( e.getSource().equals(classSetsBox) )
            {
                layoutFrame.getNetworkRootContainer().getLayoutClassSetsManager().switchClassSet( (String)classSetsBox.getSelectedItem() );
                layoutFrame.getGraph().updateAllDisplayLists();
                layoutFrame.getGraph().refreshDisplay();

                refreshCurrentClassSetSelection();

                if (DEBUG_BUILD) println("Reinit Due to Action:" + e.toString());

                populateClassViewer(false, true);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if ( e.getSource().equals(viewAllClassSets) )
        {
            if (DEBUG_BUILD) println("Reinit Due to Action:" + e.toString());

            populateClassViewer(false, true);
        }
        else if ( e.getSource().equals(autoSizeColumnsCheckBox) )
        {
            CV_AUTO_SIZE_COLUMNS.set(autoSizeColumnsCheckBox.isSelected());
            generalTable.setAutoSizeColumns(CV_AUTO_SIZE_COLUMNS.get());
            populateClassViewer(false, true);
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent listSelectionEvent)
    {
        int selectedRow = entropyTable.getSelectedRow();
        boolean isDifferent = (selectedRow > -1) ? !annotationClass.equals( (String)entropyTable.getModel().getValueAt(selectedRow, 0) ) : true;
        annotationClass = (selectedRow > -1) ? (String)entropyTable.getModel().getValueAt(selectedRow, 0) : "";
        if (isDifferent)
            runLightWeightThread(Thread.NORM_PRIORITY);
    }

    @Override
    public void stateChanged(ChangeEvent changeEvent)
    {
        if (DEBUG_BUILD) println( changeEvent.toString() );

        if ( changeEvent.getSource().equals(tabbedPane) )
        {
            if (DEBUG_BUILD) println("Reinit due to change event: " + changeEvent.toString());

            populateClassViewer(false, true);
        }
    }

    public static class EntropyTableCellRenderer extends DefaultTableCellRenderer
    {
        /**
        *  Serial version UID variable for the EntropyTableCellRenderer class.
        */
        public static final long serialVersionUID = 111222333444555797L;

        public static final DecimalFormat DECIMAL_FORMAT_1 = new DecimalFormat("0.##E0");
        public static final DecimalFormat DECIMAL_FORMAT_2 = new DecimalFormat("0.####");

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value instanceof Double)
            {
                double val = ( (Double)value ).doubleValue();
                String text = ( (val > 1000.0) || (val < 0.0001) ) ? DECIMAL_FORMAT_1.format(value) : DECIMAL_FORMAT_2.format(value);
                setText(text);
                setToolTipText(text);
            }

            return this;
        }
    }
}
