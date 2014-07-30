package org.BioLayoutExpress3D.CoreUI.MenuBars;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import org.BioLayoutExpress3D.Textures.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
*
* @author Leon Goldovsky, full refactoring by Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*
*/

public final class LayoutMenuBar extends JMenuBar implements ActionListener
{
    /**
    *  Serial version UID variable for the LayoutMenuBar class.
    */
    public static final long serialVersionUID = 111222333444555692L;

    /**
    *  Constant value needed for the 2D OpenGL renderer.
    */
    private static final String DIR_NAME_1 = IMAGE_FILES_PATH + "GeneralToolBar/";

    /**
    *  Constant value needed for the 2D OpenGL renderer.
    */
    private static final String FILE_NAME_1 = "GeneralToolBarData.txt";

    /**
    *  Constant value needed for the 2D OpenGL renderer.
    */
    private static final String DIR_NAME_2 = IMAGE_FILES_PATH + "NavigationToolBar/";

    /**
    *  Constant value needed for the 2D OpenGL renderer.
    */
    private static final String FILE_NAME_2 = "NavigationToolBarData.txt";

    private TexturesLoader texturesLoaderGeneralToolBarIcons = null;
    private TexturesLoader texturesLoaderNavigationToolBarIcons = null;

    private JMenu fileMenu = null;
    private JMenu editMenu = null;
    private JMenu viewMenu = null;
    private JMenu searchMenu = null;
    private JMenu simulationMenu = null;
    private JMenu toolsMenu = null;
    private JMenu helpMenu = null;
    private JMenu _2DMenu = null;
    private JMenu _3DMenu = null;
    private JMenu fileHistorySubMenu = null;
    private JMenu fileImportSubMenu = null;
    private JMenu fileExportSubMenu = null;
    private JMenu fileExportSubMenuClassSetsAsFileSubMenu = null;
    private JMenu selectionSubMenu = null;

    private JRadioButtonMenuItem _2DTranslateAction = null;
    private JRadioButtonMenuItem _2DZoomAction = null;
    private JRadioButtonMenuItem _3DTranslateAction = null;
    private JRadioButtonMenuItem _3DRotateAction = null;
    private JRadioButtonMenuItem _3DSelectAction = null;
    private JRadioButtonMenuItem _3DZoomAction = null;

    private JMenuItem viewToggleLegend = null;

    private int mask = 0;
    private boolean constructorInitializationFinished = false;

    public LayoutMenuBar()
    {
        if (BIOLAYOUT_USE_STATIC_COLOR) this.setBackground(BIOLAYOUT_MENUBAR_AND_TOOLBAR_COLOR);

        texturesLoaderGeneralToolBarIcons = new TexturesLoader(DIR_NAME_1, FILE_NAME_1, false, false, true, MENUBAR_IMAGE_ICON_RESIZE_RATIO, false);
        texturesLoaderNavigationToolBarIcons = new TexturesLoader(DIR_NAME_2, FILE_NAME_2, false, false, true, MENUBAR_IMAGE_ICON_RESIZE_RATIO, false);

        fileMenu = new JMenu("File");
        if (BIOLAYOUT_USE_STATIC_COLOR) fileMenu.setBackground(BIOLAYOUT_MENUBAR_AND_TOOLBAR_COLOR);
        editMenu = new JMenu("Edit");
        if (BIOLAYOUT_USE_STATIC_COLOR) editMenu.setBackground(BIOLAYOUT_MENUBAR_AND_TOOLBAR_COLOR);
        viewMenu = new JMenu("View");
        if (BIOLAYOUT_USE_STATIC_COLOR) viewMenu.setBackground(BIOLAYOUT_MENUBAR_AND_TOOLBAR_COLOR);
        searchMenu = new JMenu("Search");
        if (BIOLAYOUT_USE_STATIC_COLOR) searchMenu.setBackground(BIOLAYOUT_MENUBAR_AND_TOOLBAR_COLOR);
        simulationMenu = new JMenu("Simulation");
        if (BIOLAYOUT_USE_STATIC_COLOR) simulationMenu.setBackground(BIOLAYOUT_MENUBAR_AND_TOOLBAR_COLOR);
        toolsMenu = new JMenu("Tools");
        if (BIOLAYOUT_USE_STATIC_COLOR) toolsMenu.setBackground(BIOLAYOUT_MENUBAR_AND_TOOLBAR_COLOR);
        helpMenu = new JMenu("Help");
        if (BIOLAYOUT_USE_STATIC_COLOR) helpMenu.setBackground(BIOLAYOUT_MENUBAR_AND_TOOLBAR_COLOR);
        _2DMenu = new JMenu("2D");
        if (BIOLAYOUT_USE_STATIC_COLOR) _2DMenu.setBackground(BIOLAYOUT_MENUBAR_AND_TOOLBAR_COLOR);
        _3DMenu = new JMenu("3D");
        if (BIOLAYOUT_USE_STATIC_COLOR) _3DMenu.setBackground(BIOLAYOUT_MENUBAR_AND_TOOLBAR_COLOR);

        // set all popup menus to be heavyweight so as to be visible on top of the main OpenGL heavyweight canvas
        fileMenu.getPopupMenu().setLightWeightPopupEnabled(false);
        editMenu.getPopupMenu().setLightWeightPopupEnabled(false);
        viewMenu.getPopupMenu().setLightWeightPopupEnabled(false);
        searchMenu.getPopupMenu().setLightWeightPopupEnabled(false);
        simulationMenu.getPopupMenu().setLightWeightPopupEnabled(false);
        toolsMenu.getPopupMenu().setLightWeightPopupEnabled(false);
        helpMenu.getPopupMenu().setLightWeightPopupEnabled(false);
        _2DMenu.getPopupMenu().setLightWeightPopupEnabled(false);
        _3DMenu.getPopupMenu().setLightWeightPopupEnabled(false);

        // all sub menus
        fileHistorySubMenu = new JMenu("Open Recent");
        fileHistorySubMenu.getPopupMenu().setLightWeightPopupEnabled(false);

        fileImportSubMenu = new JMenu("Import");
        fileImportSubMenu.getPopupMenu().setLightWeightPopupEnabled(false);
        fileImportSubMenu.setEnabled(true);

        fileExportSubMenu = new JMenu("Export");
        fileExportSubMenu.getPopupMenu().setLightWeightPopupEnabled(false);
        fileExportSubMenu.setEnabled(false);

        fileExportSubMenuClassSetsAsFileSubMenu = new JMenu("Class Sets As File");
        fileExportSubMenuClassSetsAsFileSubMenu.getPopupMenu().setLightWeightPopupEnabled(false);
        fileExportSubMenuClassSetsAsFileSubMenu.setEnabled(false);

        selectionSubMenu = new JMenu("Selection");
        selectionSubMenu.getPopupMenu().setLightWeightPopupEnabled(false);
        selectionSubMenu.setEnabled(false);

        initAllMenus();

        constructorInitializationFinished = true;
    }

    private void initAllMenus()
    {
        mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        fileMenu.setMnemonic(KeyEvent.VK_F);
        add(fileMenu);
        editMenu.setMnemonic(KeyEvent.VK_E);
        add(editMenu);
        viewMenu.setMnemonic(KeyEvent.VK_V);
        add(viewMenu);
        searchMenu.setMnemonic(KeyEvent.VK_S);
        add(searchMenu);
        simulationMenu.setMnemonic(KeyEvent.VK_I);
        add(simulationMenu);
        toolsMenu.setMnemonic(KeyEvent.VK_T);
        add(toolsMenu);
        helpMenu.setMnemonic(KeyEvent.VK_H);
        add(helpMenu);
        _2DMenu.setMnemonic(KeyEvent.VK_2);
        add(_2DMenu);
        _3DMenu.setMnemonic(KeyEvent.VK_3);
        add(_3DMenu);

        editMenu.add(selectionSubMenu);
    }

    private void addSeparator(JMenu menu)
    {
        JSeparator separator = new JSeparator();
        separator.setBackground(BIOLAYOUT_MENUBAR_AND_TOOLBAR_COLOR);
        menu.add(separator);
    }

    public void setFileMenuOpenAction(AbstractAction loadFileAction)
    {
        JMenuItem fileOpen = fileMenu.add(loadFileAction);
        fileOpen.setIcon( new ImageIcon( texturesLoaderGeneralToolBarIcons.getImage("GraphOpen") ) );
        fileOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, mask));
        fileOpen.setMnemonic(KeyEvent.VK_O);

        setFileMenuOpenRecentSubMenu();
    }

    private void setFileMenuOpenRecentSubMenu()
    {
        JMenuItem fileMenuOpenRecent = fileMenu.add(fileHistorySubMenu);
        fileMenuOpenRecent.setIcon( new ImageIcon( texturesLoaderGeneralToolBarIcons.getImage("GraphLastOpen") ) );
        fileMenuOpenRecent.setMnemonic(KeyEvent.VK_R);
        JSeparator separator = new JSeparator();
        separator.setBackground(BIOLAYOUT_MENUBAR_AND_TOOLBAR_COLOR);
        fileMenu.add(separator);
    }

    public void setFileMenuOpenRecentAction(ArrayList<AbstractAction> loadFileActions)
    {
        fileHistorySubMenu.removeAll();
        for (AbstractAction fileOpenAction : loadFileActions)
            fileHistorySubMenu.add(fileOpenAction).setIcon(null);
    }

    public void setFileMenuSaveGraphAsAction(AbstractAction saveFileAction)
    {
        JMenuItem fileSave = fileMenu.add(saveFileAction);
        fileSave.setIcon( new ImageIcon( texturesLoaderGeneralToolBarIcons.getImage("GraphSave") ) );
        fileSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, mask));
        fileSave.setMnemonic(KeyEvent.VK_S);
    }

    public void setFileMenuSaveGraphSelectionAsAction(AbstractAction saveSelectedFileAction)
    {
        JMenuItem fileSaveSelected = fileMenu.add(saveSelectedFileAction);
        fileSaveSelected.setIcon(null);
        fileSaveSelected.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, mask + ActionEvent.SHIFT_MASK));
        fileSaveSelected.setMnemonic(KeyEvent.VK_L);
    }

    public void setFileMenuSaveVisibleGraphAsAction(AbstractAction saveVisibleFileAction)
    {
        JMenuItem fileSaveVisible = fileMenu.add(saveVisibleFileAction);
        fileSaveVisible.setIcon(null);
        fileSaveVisible.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, mask + ActionEvent.ALT_MASK));
        fileSaveVisible.setMnemonic(KeyEvent.VK_V);
        addSeparator(fileMenu);

        setFileMenuImportExportSubMenus();
        setFileMenuExportSubMenuClassSetsAsFileSubMenu();
    }

    private void setFileMenuImportExportSubMenus()
    {
        JMenuItem fileMenuImport = fileMenu.add(fileImportSubMenu);
        fileMenuImport.setMnemonic(KeyEvent.VK_I);
        JMenuItem fileMenuExport = fileMenu.add(fileExportSubMenu);
        fileMenuExport.setMnemonic(KeyEvent.VK_E);
        addSeparator(fileMenu);
    }

    private void setFileMenuExportSubMenuClassSetsAsFileSubMenu()
    {
        fileExportSubMenu.add(fileExportSubMenuClassSetsAsFileSubMenu).setIcon(null);
    }

    /**
     * Import Network from Public Database
     * Mnemonic C
     * Keystroke Cmd+I
     * @param importClassSetsAction - Action for this submenu item
     */
    public void setFileSubMenuImportClassSetsAction(AbstractAction importClassSetsAction)
    {
        JMenuItem item = fileImportSubMenu.add(importClassSetsAction);
        item.setIcon(null);
        item.setMnemonic(KeyEvent.VK_C);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, mask));
    }

    /**
     * Import Network from Public Database
     * Mnemonic N
     * Keystroke Cmd+Shift+I
     * @param importNetworkAction - Action for this submenu item
     */
    public void setFileSubMenuImportNetworkAction(AbstractAction importNetworkAction)
    {
        JMenuItem item = fileImportSubMenu.add(importNetworkAction);
        item.setIcon(null);        
        item.setMnemonic(KeyEvent.VK_N);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, mask + ActionEvent.SHIFT_MASK));
    }
    
    public void setFileMenuExportAction(AbstractAction exportAction)
    {
        fileExportSubMenu.add(exportAction).setIcon(null);
    }

    public void setFileMenuExportClassSetsAsFileAction(AbstractAction exportAction)
    {
        fileExportSubMenuClassSetsAsFileSubMenu.add(exportAction).setIcon(null);
    }

    public void setFileMenuPrintPageSetupAction(AbstractAction pageAction)
    {
        JMenuItem filePrintPageSetup = fileMenu.add(pageAction);
        filePrintPageSetup.setIcon(null);
        filePrintPageSetup.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, mask + ActionEvent.SHIFT_MASK));
        filePrintPageSetup.setMnemonic(KeyEvent.VK_A);
    }

    public void setFileMenuPrintGraphAction(AbstractAction printAction)
    {
        JMenuItem filePrint = fileMenu.add(printAction);
        filePrint.setIcon( new ImageIcon( texturesLoaderGeneralToolBarIcons.getImage("GraphPrint") ) );
        filePrint.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, mask));
        filePrint.setMnemonic(KeyEvent.VK_P);
    }

    public void setFileMenuExitAction(AbstractAction exitAction)
    {
        addSeparator(fileMenu);
        JMenuItem fileExit = fileMenu.add(exitAction);
        fileExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, mask));
        fileExit.setMnemonic(KeyEvent.VK_X);
    }

    public void setEditMenuUndoNodeDraggingOnSelectionAction(AbstractAction undoNodeDraggingNodesAction)
    {
        addSeparator(editMenu);
        JMenuItem editUndoNodeDraggingOnSelection = editMenu.add(undoNodeDraggingNodesAction);
        editUndoNodeDraggingOnSelection.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, mask));
        editUndoNodeDraggingOnSelection.setMnemonic(KeyEvent.VK_Z);
    }

    public void setEditMenuRedoNodeDraggingOnSelectionAction(AbstractAction redoNodeDraggingNodesAction)
    {
        JMenuItem editRedoNodeDraggingOnSelection = editMenu.add(redoNodeDraggingNodesAction);
        editRedoNodeDraggingOnSelection.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, mask));
        editRedoNodeDraggingOnSelection.setMnemonic(KeyEvent.VK_Y);
    }

    public void setEditMenuDeleteSelectionAction(AbstractAction deleteSelectionAction)
    {
        addSeparator(editMenu);
        JMenuItem editDeleteSelectionNode = editMenu.add(deleteSelectionAction);
        editDeleteSelectionNode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, mask + ActionEvent.ALT_MASK));
        editDeleteSelectionNode.setMnemonic(KeyEvent.VK_D);
    }

    public void setEditMenuDeleteHiddenAction(AbstractAction deleteHiddenAction)
    {
        JMenuItem editDeleteHiddenNode = editMenu.add(deleteHiddenAction);
    }

    public void setEditMenuDeleteUnselectedAction(AbstractAction deleteUnselectedAction)
    {
        JMenuItem editDeleteUnselectedNode = editMenu.add(deleteUnselectedAction);
        editDeleteUnselectedNode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, mask + ActionEvent.ALT_MASK + ActionEvent.SHIFT_MASK));
        editDeleteUnselectedNode.setMnemonic(KeyEvent.VK_D);
    }

    public void setEditMenuUndoLastDeleteAction(AbstractAction undoLastDeleteAction)
    {
        JMenuItem editUndoLastDeleteNode = editMenu.add(undoLastDeleteAction);
        editUndoLastDeleteNode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, mask + ActionEvent.ALT_MASK));
        editUndoLastDeleteNode.setMnemonic(KeyEvent.VK_Z);
    }

    public void setEditMenuUndeleteAllNodesAction(AbstractAction undeleteAllNodesAction)
    {
        JMenuItem editUndeleteAllNodes = editMenu.add(undeleteAllNodesAction);
        editUndeleteAllNodes.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, mask + ActionEvent.ALT_MASK + ActionEvent.SHIFT_MASK));
        editUndeleteAllNodes.setMnemonic(KeyEvent.VK_Z);
    }

    public void setEditMenuCollapseNodesByClassAction(AbstractAction classGroupAction)
    {
        addSeparator(editMenu);
        JMenuItem editClassGroupNodes = editMenu.add(classGroupAction);
        editClassGroupNodes.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, mask + ActionEvent.ALT_MASK + ActionEvent.SHIFT_MASK));
        editClassGroupNodes.setMnemonic(KeyEvent.VK_G);
    }

    public void setEditMenuCollapseSelectionAction(AbstractAction groupAction)
    {
        addSeparator(editMenu);
        JMenuItem editGroupNodes = editMenu.add(groupAction);
        editGroupNodes.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, mask + ActionEvent.ALT_MASK));
        editGroupNodes.setMnemonic(KeyEvent.VK_G);
    }

    public void setEditMenuPerformCompleteGrouping(AbstractAction completeGroupingAction)
    {
        JMenuItem editCompleteGroupNodes = editMenu.add(completeGroupingAction);
        editCompleteGroupNodes.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, mask + ActionEvent.ALT_MASK));
        editCompleteGroupNodes.setMnemonic(KeyEvent.VK_C);
    }

    public void setEditMenuUnCollapseSelectedGroupsAction(AbstractAction groupAction)
    {
        JMenuItem editUnGroupSelectedNodes = editMenu.add(groupAction);
        editUnGroupSelectedNodes.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, mask + ActionEvent.ALT_MASK + ActionEvent.SHIFT_MASK));
        editUnGroupSelectedNodes.setMnemonic(KeyEvent.VK_S);
    }

    public void setEditMenuUnCollapseAllGroupsAction(AbstractAction groupAction)
    {
        JMenuItem editUnGroupAllNodes = editMenu.add(groupAction);
        editUnGroupAllNodes.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, mask + ActionEvent.ALT_MASK + ActionEvent.SHIFT_MASK));
        editUnGroupAllNodes.setMnemonic(KeyEvent.VK_U);
    }

    public void setEditMenuFilterNodesByEdgesAction(AbstractAction filterNodesByEdgesAction)
    {
        addSeparator(editMenu);
        JMenuItem editFilterByWeight = editMenu.add(filterNodesByEdgesAction);
        editFilterByWeight.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, mask));
        editFilterByWeight.setMnemonic(KeyEvent.VK_W);
    }

    public void setEditMenuFilterEdgesByWeightAction(AbstractAction filterEdgesByWeightAction)
    {
        JMenuItem editFilterByWeight = editMenu.add(filterEdgesByWeightAction);
        editFilterByWeight.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, mask + ActionEvent.ALT_MASK));
        editFilterByWeight.setMnemonic(KeyEvent.VK_W);
    }

    public void setEditSubMenuSelectAllAction(AbstractAction selectAction)
    {
        JMenuItem editSelectAll = selectionSubMenu.add(selectAction);
        editSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, mask));
        editSelectAll.setMnemonic(KeyEvent.VK_A);
    }

    public void setEditSubMenuSelectNeighbours(AbstractAction selectNeighbourAction)
    {
        addSeparator(selectionSubMenu);
        JMenuItem editSelectNeighbours = selectionSubMenu.add(selectNeighbourAction);
        editSelectNeighbours.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, mask));
        editSelectNeighbours.setMnemonic(KeyEvent.VK_L);
    }

    public void setEditSubMenuSelectAllNeighbours(AbstractAction selectNeighbourAction)
    {
        JMenuItem editSelectAllNeighbours = selectionSubMenu.add(selectNeighbourAction);
        editSelectAllNeighbours.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, mask + ActionEvent.SHIFT_MASK));
    }

    public void setEditSubMenuSelectParents(AbstractAction selectParentsAction)
    {
        addSeparator(selectionSubMenu);
        JMenuItem editSelectParents = selectionSubMenu.add(selectParentsAction);
        editSelectParents.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, mask + ActionEvent.ALT_MASK));
        editSelectParents.setMnemonic(KeyEvent.VK_P);
    }

    public void setEditSubMenuSelectAllPArents(AbstractAction selectAllParentsAction)
    {
        JMenuItem editSelectAllParents = selectionSubMenu.add(selectAllParentsAction);
        editSelectAllParents.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, mask + ActionEvent.SHIFT_MASK + ActionEvent.ALT_MASK));
        editSelectAllParents.setMnemonic(KeyEvent.VK_R);
    }

    public void setEditSubMenuSelectChildren(AbstractAction selectChildrenAction)
    {
        JMenuItem editSelectChildren = selectionSubMenu.add(selectChildrenAction);
        editSelectChildren.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, mask + ActionEvent.ALT_MASK));
        editSelectChildren.setMnemonic(KeyEvent.VK_C);
    }

    public void setEditSubMenuSelectAllChildren(AbstractAction selectAllChildrenAction)
    {
        JMenuItem editSelectAllChildren = selectionSubMenu.add(selectAllChildrenAction);
        editSelectAllChildren.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, mask + ActionEvent.SHIFT_MASK + ActionEvent.ALT_MASK));
        editSelectAllChildren.setMnemonic(KeyEvent.VK_H);
    }

    public void setEditSubMenuSelectNodesWithinTheSameClassAction(AbstractAction selectClassAction)
    {
        addSeparator(selectionSubMenu);
        JMenuItem editSelectClass = selectionSubMenu.add(selectClassAction);
        editSelectClass.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, mask + ActionEvent.ALT_MASK));
        editSelectClass.setMnemonic(KeyEvent.VK_S);
    }

    public void setEditSubMenuReverseSelectionAction(AbstractAction reverseSelectionAction)
    {
        addSeparator(selectionSubMenu);
        JMenuItem editReverseSelection = selectionSubMenu.add(reverseSelectionAction);
        editReverseSelection.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, mask));
        editReverseSelection.setMnemonic(KeyEvent.VK_R);
    }

    public void setEditSubMenuDeselectAllAction(AbstractAction deselectAction)
    {
        JMenuItem editSelectAll = selectionSubMenu.add(deselectAction);
        editSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, mask + ActionEvent.ALT_MASK));
        editSelectAll.setMnemonic(KeyEvent.VK_D);
    }

    public void setViewMenuToggle2D3DAction(AbstractAction toggle2D3DAction)
    {
        JMenuItem viewHideSelection = viewMenu.add(toggle2D3DAction);
    }

    public void setViewMenuHideSelectionAction(AbstractAction hideSelectionAction)
    {
        JMenuItem viewHideSelection = viewMenu.add(hideSelectionAction);
        viewHideSelection.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, mask));
        viewHideSelection.setMnemonic(KeyEvent.VK_H);
    }

    public void setViewMenuHideUnselectedAction(AbstractAction hideUnselectedAction)
    {
        JMenuItem viewHideUnselected = viewMenu.add(hideUnselectedAction);
        viewHideUnselected.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, mask + ActionEvent.SHIFT_MASK));
        viewHideUnselected.setMnemonic(KeyEvent.VK_H);
    }

    public void setViewMenuUnhideAllNodesAction(AbstractAction unHideAllAction)
    {
        JMenuItem viewUnhideAll = viewMenu.add(unHideAllAction);
        viewUnhideAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, mask));
        viewUnhideAll.setMnemonic(KeyEvent.VK_U);
    }

    public void setViewMenuShowAllNodeNamesAction(AbstractAction showAllNodeNamesAction)
    {
        addSeparator(viewMenu);
        JMenuItem viewShowAllNodeNames = viewMenu.add(showAllNodeNamesAction);
        viewShowAllNodeNames.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, mask + ActionEvent.SHIFT_MASK));
        viewShowAllNodeNames.setMnemonic(KeyEvent.VK_L);
    }

    public void setViewMenuShowSelectedNodeNamesAction(AbstractAction showSelectedNodeNamesAction)
    {
        JMenuItem viewShowSelectedNodeNames = viewMenu.add(showSelectedNodeNamesAction);
        viewShowSelectedNodeNames.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, mask));
        viewShowSelectedNodeNames.setMnemonic(KeyEvent.VK_E);
    }

    public void setViewMenuShowAllEdgeNamesAction(AbstractAction showEdgeNamesAction)
    {
        JMenuItem viewShowEdgeNames = viewMenu.add(showEdgeNamesAction);
        viewShowEdgeNames.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, mask + ActionEvent.SHIFT_MASK));
        viewShowEdgeNames.setMnemonic(KeyEvent.VK_E);
    }

    public void setViewMenuShowSelectedNodesEdgeNamesAction(AbstractAction showSelectedNodesEdgeNamesAction)
    {
        JMenuItem viewShowSelectedNodesEdgeNames = viewMenu.add(showSelectedNodesEdgeNamesAction);
        viewShowSelectedNodesEdgeNames.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, mask));
        viewShowSelectedNodesEdgeNames.setMnemonic(KeyEvent.VK_E);
    }

    public void setViewMenuHideAllNodeNamesAction(AbstractAction hideAllNodeNamesAction)
    {
        addSeparator(viewMenu);
        JMenuItem viewHideAllNodeNames = viewMenu.add(hideAllNodeNamesAction);
        viewHideAllNodeNames.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, mask + ActionEvent.ALT_MASK + ActionEvent.SHIFT_MASK));
        viewHideAllNodeNames.setMnemonic(KeyEvent.VK_H);
    }

    public void setViewMenuHideSelectedNodeNamesAction(AbstractAction hideSelectedNodeNamesAction)
    {
        JMenuItem viewHideSelectedNodeNames = viewMenu.add(hideSelectedNodeNamesAction);
        viewHideSelectedNodeNames.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, mask + ActionEvent.ALT_MASK));
        viewHideSelectedNodeNames.setMnemonic(KeyEvent.VK_E);
    }

    public void setViewMenuHideAllEdgeNamesAction(AbstractAction hideEdgeNamesAction)
    {
        JMenuItem viewHideEdgeNames = viewMenu.add(hideEdgeNamesAction);
        viewHideEdgeNames.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, mask + ActionEvent.ALT_MASK + ActionEvent.SHIFT_MASK));
        viewHideEdgeNames.setMnemonic(KeyEvent.VK_E);
    }

    public void setViewMenuHideSelectedNodesEdgeNamesAction(AbstractAction hideSelectedNodesEdgeNamesAction)
    {
        JMenuItem viewHideSelectedNodesEdgeNames = viewMenu.add(hideSelectedNodesEdgeNamesAction);
        viewHideSelectedNodesEdgeNames.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, mask + ActionEvent.ALT_MASK));
        viewHideSelectedNodesEdgeNames.setMnemonic(KeyEvent.VK_E);
    }

    public void setViewMenuCustomizeNodeNamesAction(AbstractAction customizeNodeNamesAction)
    {
        addSeparator(viewMenu);
        viewToggleLegend = viewMenu.add(customizeNodeNamesAction);
        viewToggleLegend.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, mask + ActionEvent.ALT_MASK));
        viewToggleLegend.setMnemonic(KeyEvent.VK_N);
    }

    public void setViewMenuShowClassesLegendsAction(AbstractAction showClassesLegendsAction)
    {
        addSeparator(viewMenu);
        viewToggleLegend = viewMenu.add(showClassesLegendsAction);
        viewToggleLegend.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, mask + ActionEvent.ALT_MASK));
        viewToggleLegend.setMnemonic(KeyEvent.VK_L);
    }

    public void setSearchMenuFindByNameAction(AbstractAction findNameAction)
    {
        JMenuItem searchFindName = searchMenu.add(findNameAction);
        searchFindName.setIcon( new ImageIcon( texturesLoaderGeneralToolBarIcons.getImage("GraphFind") ) );
        searchFindName.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, mask));
        searchFindName.setMnemonic(KeyEvent.VK_N);
    }

    public void setSearchMenuFindByClassAction(AbstractAction findClassAction)
    {
        JMenuItem searchFindClass = searchMenu.add(findClassAction);
        searchFindClass.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, mask + ActionEvent.SHIFT_MASK));
        searchFindClass.setMnemonic(KeyEvent.VK_C);
    }

    public void setSearchMenuFindByMultipleClassesAction(AbstractAction findMultipleClassesAction)
    {
        JMenuItem searchFindClass = searchMenu.add(findMultipleClassesAction);
        searchFindClass.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, mask + ActionEvent.ALT_MASK + ActionEvent.SHIFT_MASK));
        searchFindClass.setMnemonic(KeyEvent.VK_M);
    }

    public void setSimulationMenuSPNDialogAction(AbstractAction SPNDialogAction)
    {
        JMenuItem SPNDialog = simulationMenu.add(SPNDialogAction);
        SPNDialog.setIcon( new ImageIcon( texturesLoaderGeneralToolBarIcons.getImage("RunSPN") ) );
        SPNDialog.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
        SPNDialog.setMnemonic(KeyEvent.VK_S);
    }

    public void setSimulationMenuLoadSimulationDataAction(AbstractAction loadSimulationDataAction)
    {
        addSeparator(simulationMenu);
        JMenuItem loadSimulationData = simulationMenu.add(loadSimulationDataAction);
        loadSimulationData.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.ALT_MASK));
        loadSimulationData.setMnemonic(KeyEvent.VK_L);
    }

    public void setToolsMenuGraphPropertiesAction(AbstractAction propertiesAction)
    {
        JMenuItem toolsProperties = toolsMenu.add(propertiesAction);
        toolsProperties.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.SHIFT_MASK));
        toolsProperties.setMnemonic(KeyEvent.VK_P);
    }

    public void setToolsMenuSavePreferences(AbstractAction toolsSavePreferencesAction)
    {
        JMenuItem toolsSavePreferences = toolsMenu.add(toolsSavePreferencesAction);
        toolsSavePreferences.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.ALT_MASK));
        toolsSavePreferences.setMnemonic(KeyEvent.VK_P);
    }

    public void setToolsMenuRevertToDefaultPreferences(AbstractAction toolsRevertDefaultPreferencesAction)
    {
        JMenuItem toolsRevertDefaultPreferences = toolsMenu.add(toolsRevertDefaultPreferencesAction);
        toolsRevertDefaultPreferences.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.ALT_MASK + ActionEvent.SHIFT_MASK));
        toolsRevertDefaultPreferences.setMnemonic(KeyEvent.VK_P);
    }

    public void setToolsMenuGraphStatisticsAction(AbstractAction statisticsAction)
    {
        addSeparator(toolsMenu);
        JMenuItem toolsStatistics = toolsMenu.add(statisticsAction);
        toolsStatistics.setIcon( new ImageIcon( texturesLoaderGeneralToolBarIcons.getImage("GraphStatistics") ) );
        toolsStatistics.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.SHIFT_MASK));
        toolsStatistics.setMnemonic(KeyEvent.VK_S);
    }

    public void setToolsMenuClassViewerAction(AbstractAction classViewerAction)
    {
        JMenuItem editClassViewer = toolsMenu.add(classViewerAction);
        editClassViewer.setIcon( new ImageIcon( texturesLoaderGeneralToolBarIcons.getImage("ClassViewer") ) );
        editClassViewer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, mask));
        editClassViewer.setMnemonic(KeyEvent.VK_C);
    }

    public void setToolsMenuAnimationControlDialogAction(AbstractAction animationControlDialogAction)
    {
        JMenuItem animationControlDialog = toolsMenu.add(animationControlDialogAction);
        animationControlDialog.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.ALT_MASK));
        animationControlDialog.setMnemonic(KeyEvent.VK_A);
    }

    public void setToolsMenuClusterUsingMCL(AbstractAction clusterMCLAction)
    {
        addSeparator(toolsMenu);
        JMenuItem editClusterMCL = toolsMenu.add(clusterMCLAction);
        editClusterMCL.setIcon( new ImageIcon( texturesLoaderGeneralToolBarIcons.getImage("RunMCL") ) );
        editClusterMCL.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, mask + ActionEvent.ALT_MASK));
        editClusterMCL.setMnemonic(KeyEvent.VK_M);
    }

    public void setHelpMenuNavigationWizardAction(AbstractAction navigationWizardAction)
    {
        JMenuItem helpNavigationWizard = helpMenu.add(navigationWizardAction);
        helpNavigationWizard.setIcon( new ImageIcon( texturesLoaderNavigationToolBarIcons.getImage("NavigationWizard") ) );
        helpNavigationWizard.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.SHIFT_MASK));
        helpNavigationWizard.setMnemonic(KeyEvent.VK_N);
    }

    public void setHelpMenuTipOfTheDayAction(AbstractAction tipOfTheDayAction)
    {
        JMenuItem helpTipOfTheDay = helpMenu.add(tipOfTheDayAction);
        helpTipOfTheDay.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.SHIFT_MASK));
        helpTipOfTheDay.setMnemonic(KeyEvent.VK_T);
    }

    public void setHelpMenuLicensesAction(AbstractAction licenseAction)
    {
        JMenuItem helpAbout = helpMenu.add(licenseAction);
        helpAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.SHIFT_MASK));
        helpAbout.setMnemonic(KeyEvent.VK_L);
    }

    public void setHelpMenuOpenGLDriverCapsAction(AbstractAction openGLDriverCapsAction)
    {
        addSeparator(helpMenu);
        JMenuItem openGLDriverCaps = helpMenu.add(openGLDriverCapsAction);
        openGLDriverCaps.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.SHIFT_MASK));
        openGLDriverCaps.setMnemonic(KeyEvent.VK_G);
    }

    public void setHelpMenuOpenCLDriverCapsAction(AbstractAction openCLDriverCapsAction)
    {
        JMenuItem openGLDriverCaps = helpMenu.add(openCLDriverCapsAction);
        openGLDriverCaps.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.SHIFT_MASK));
        openGLDriverCaps.setMnemonic(KeyEvent.VK_C);
    }

    public void setHelpMenuJavaPlatformCapsAction(AbstractAction javaPlatformCapsAction)
    {
        JMenuItem javaPlatformCaps = helpMenu.add(javaPlatformCapsAction);
        javaPlatformCaps.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, ActionEvent.SHIFT_MASK));
        javaPlatformCaps.setMnemonic(KeyEvent.VK_J);
    }

    public void setHelpMenuCheckForUpdatesAction(AbstractAction checkForUpdatesAction)
    {
        addSeparator(helpMenu);
        JMenuItem checkForUpdates = helpMenu.add(checkForUpdatesAction);
        checkForUpdates.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.SHIFT_MASK));
        checkForUpdates.setMnemonic(KeyEvent.VK_U);
    }

    public void setHelpMenuAboutAction(AbstractAction aboutAction)
    {
        addSeparator(helpMenu);
        JMenuItem helpAbout = helpMenu.add(aboutAction);
        helpAbout.setIcon(BIOLAYOUT_MENU_ITEM_ICON);
        helpAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.SHIFT_MASK));
        helpAbout.setMnemonic(KeyEvent.VK_A);
    }

    public void set2DMenuEnabled(boolean state)
    {
        _2DMenu.setVisible(state);
    }

    public void set2DMenuAutoRotateAction(AbstractAction autoRotateAction)
    {
        _2DMenu.add(autoRotateAction).setIcon(null);
    }

    public void set2DMenuScreenSaver2DModeAction(AbstractAction screenSaver2DModeAction)
    {
        _2DMenu.add(screenSaver2DModeAction).setIcon(null);
        addSeparator(_2DMenu);
    }

    public void set2DMenuTranslateAction(AbstractAction translateAction)
    {
        _2DTranslateAction = new JRadioButtonMenuItem(translateAction);
        _2DTranslateAction.addActionListener(this);
        _2DTranslateAction.setSelected(true);
        _2DMenu.add(_2DTranslateAction);
        _2DTranslateAction.setIcon( new ImageIcon( texturesLoaderNavigationToolBarIcons.getImage("Up") ) );
    }

    public void set2DMenuZoomAction(AbstractAction zoomAction)
    {
        _2DZoomAction = new JRadioButtonMenuItem(zoomAction);
        _2DZoomAction.addActionListener(this);
        _2DMenu.add(_2DZoomAction);
        _2DZoomAction.setIcon( new ImageIcon( texturesLoaderNavigationToolBarIcons.getImage("ZoomIn") ) );
        addSeparator(_2DMenu);
    }

    public void set2DMenuResetViewAction(AbstractAction resetViewAction)
    {
        _2DMenu.add(resetViewAction).setIcon( new ImageIcon( texturesLoaderNavigationToolBarIcons.getImage("ResetView") ) );
    }

    public void set2DMenuRenderAction(AbstractAction renderAction)
    {
        _2DMenu.add(renderAction).setIcon(null);
    }

    public void set2DMenuHighResRenderAction(AbstractAction highResRenderAction)
    {
        _2DMenu.add(highResRenderAction).setIcon(null);
    }

    public void set3DMenuEnabled(boolean state)
    {
        _3DMenu.setVisible(state);
    }

    public void set3DMenuAutoRotateAction(AbstractAction autoRotateAction)
    {
        _3DMenu.add(autoRotateAction).setIcon(null);
    }

    public void set3DMenuPulsation3DModeAction(AbstractAction pulsation3DMode)
    {
        _3DMenu.add(pulsation3DMode).setIcon(null);
        addSeparator(_3DMenu);
    }

    public void set3DMenuRotateAction(AbstractAction rotateAction)
    {
        _3DRotateAction = new JRadioButtonMenuItem(rotateAction);
        _3DRotateAction.addActionListener(this);
        _3DRotateAction.setSelected(true);
        _3DMenu.add(_3DRotateAction);
        _3DRotateAction.setIcon( new ImageIcon( texturesLoaderNavigationToolBarIcons.getImage("RotateUp") ) );
    }

    public void set3DMenuSelectAction(AbstractAction selectAction)
    {
        _3DSelectAction = new JRadioButtonMenuItem(selectAction);
        _3DSelectAction.addActionListener(this);
        _3DMenu.add(_3DSelectAction);
        _3DSelectAction.setIcon(null);
    }

    public void set3DMenuTranslateAction(AbstractAction translateAction)
    {
        _3DTranslateAction = new JRadioButtonMenuItem(translateAction);
        _3DTranslateAction.addActionListener(this);
        _3DMenu.add(_3DTranslateAction);
        _3DTranslateAction.setIcon( new ImageIcon( texturesLoaderNavigationToolBarIcons.getImage("Up") ) );
    }

    public void set3DMenuZoomAction(AbstractAction zoomAction)
    {
        _3DZoomAction = new JRadioButtonMenuItem(zoomAction);
        _3DZoomAction.addActionListener(this);
        _3DMenu.add(_3DZoomAction);
        _3DZoomAction.setIcon( new ImageIcon( texturesLoaderNavigationToolBarIcons.getImage("ZoomIn") ) );
        addSeparator(_3DMenu);
    }

    public void set3DMenuResetViewAction(AbstractAction resetViewAction)
    {
        _3DMenu.add(resetViewAction).setIcon( new ImageIcon( texturesLoaderNavigationToolBarIcons.getImage("ResetView") ) );
    }

    public void set3DMenuRenderAction(AbstractAction renderAction)
    {
        _3DMenu.add(renderAction).setIcon(null);
    }

    public void set3DMenuHighResRenderAction(AbstractAction highResRenderAction)
    {
        _3DMenu.add(highResRenderAction).setIcon(null);
    }

    public void toggleLegend(AbstractAction toggleLegendAction)
    {
        viewMenu.remove(viewToggleLegend);
        viewToggleLegend = viewMenu.add(toggleLegendAction);
        viewToggleLegend.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, mask + ActionEvent.ALT_MASK));
        viewToggleLegend.setMnemonic(KeyEvent.VK_L);
    }

    @Override
    public void setEnabled(boolean flag)
    {
        fileMenu.setEnabled(flag);
        fileImportSubMenu.setEnabled(flag);
        fileExportSubMenu.setEnabled(flag);
        fileExportSubMenuClassSetsAsFileSubMenu.setEnabled(flag);
        editMenu.setEnabled(flag);
        selectionSubMenu.setEnabled(flag);
        viewMenu.setEnabled(flag);
        searchMenu.setEnabled(flag);
        simulationMenu.setEnabled(flag);
        toolsMenu.setEnabled(flag);
        helpMenu.setEnabled(flag);
        _2DMenu.setEnabled(flag);
        _3DMenu.setEnabled(flag);
    }

    @Override
    public boolean isEnabled()
    {
        return ( constructorInitializationFinished ? ( fileMenu.isEnabled() && editMenu.isEnabled() && viewMenu.isEnabled() && searchMenu.isEnabled() && simulationMenu.isEnabled() && toolsMenu.isEnabled() && helpMenu.isEnabled() && _2DMenu.isEnabled() && _3DMenu.isEnabled() ) : false );
    }

    public void cleanAllMenus()
    {
        fileMenu.removeAll();
        fileHistorySubMenu.removeAll();
        fileImportSubMenu.removeAll();
        fileExportSubMenu.removeAll();
        fileExportSubMenuClassSetsAsFileSubMenu.removeAll();
        editMenu.removeAll();
        selectionSubMenu.removeAll();
        viewMenu.removeAll();
        searchMenu.removeAll();
        simulationMenu.removeAll();
        toolsMenu.removeAll();
        helpMenu.removeAll();
        _2DMenu.removeAll();
        _3DMenu.removeAll();

        initAllMenus();
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if ( e.getSource().equals(_2DTranslateAction) )
        {
            _2DTranslateAction.setSelected(true);
            _2DZoomAction.setSelected(false);
        }
        else if ( e.getSource().equals(_2DZoomAction) )
        {
            _2DTranslateAction.setSelected(false);
            _2DZoomAction.setSelected(true);
        }
        else if ( e.getSource().equals(_3DRotateAction) )
        {
            _3DSelectAction.setSelected(false);
            _3DTranslateAction.setSelected(false);
            _3DZoomAction.setSelected(false);
            _3DRotateAction.setSelected(true);
        }
        else if ( e.getSource().equals(_3DTranslateAction) )
        {
            _3DSelectAction.setSelected(false);
            _3DRotateAction.setSelected(false);
            _3DZoomAction.setSelected(false);
            _3DTranslateAction.setSelected(true);
        }
        else if ( e.getSource().equals(_3DZoomAction) )
        {
            _3DSelectAction.setSelected(false);
            _3DRotateAction.setSelected(false);
            _3DTranslateAction.setSelected(false);
            _3DZoomAction.setSelected(true);
        }
        else if ( e.getSource().equals(_3DSelectAction) )
        {
            _3DRotateAction.setSelected(false);
            _3DTranslateAction.setSelected(false);
            _3DZoomAction.setSelected(false);
            _3DSelectAction.setSelected(true);
        }
    }


}