package org.BioLayoutExpress3D.CoreUI.ToolBars;

import java.awt.*;
import javax.swing.*;
import org.BioLayoutExpress3D.Textures.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
*
* LayoutAbstractToolBar is an abstract class used as a template for a BioLayout toolbar.
* It also holds all relevant static final variables of relevant instantiating sub-classes.
*
* @author Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public abstract class LayoutAbstractToolBar extends JToolBar
{
    /**
    *  Serial version UID variable for the LayoutToolBar class.
    */
    public static final long serialVersionUID = 111222333444555694L;

    protected static enum GraphPropertiesToolBarButtons { GENERAL, LAYOUT, RENDERING, MCL, SIMULATION, PARALLELISM, SEARCH, NODES, EDGES, CLASSES }
    protected static enum GeneralToolBarButtons { GRAPH_OPEN, GRAPH_LAST_OPEN, GRAPH_SAVE, GRAPH_PRINT, SNAPSHOT, GRAPH_STATISTICS, GRAPH_FIND, RUN_MCL, RUN_SPN, CLASS_VIEWER, ANIMATION_CONTROL, BURST_LAYOUT_ITERATIONS, _2D_3D, HOME }
    protected static enum NavigationToolBarButtons { UP, DOWN, LEFT, RIGHT, ROTATE_UP, ROTATE_DOWN, ROTATE_LEFT, ROTATE_RIGHT, ZOOM_IN, ZOOM_OUT, RESET_VIEW, NAVIGATION_WIZARD }
    protected static final int NUMBER_OF_GRAPH_PROPERTIES_TOOLBAR_BUTTONS = GraphPropertiesToolBarButtons.values().length;
    protected static final int NUMBER_OF_GENERAL_TOOLBAR_BUTTONS = GeneralToolBarButtons.values().length;
    protected static final int NUMBER_OF_NAVIGATION_TOOLBAR_BUTTONS = NavigationToolBarButtons.values().length;

    // based on height so as to avoid problems with extended Linux desktops on X axis
    protected static final float TOOLBAR_IMAGE_ICON_RESIZE_RATIO = SCREEN_DIMENSION.height / 1200.0f;

    protected static final String BUTTON_PROPERTIES = " Properties";
    protected static final String BUTTON_HOVER = "Hover";
    protected static final String BUTTON_PRESSED = "Pressed";

    protected static final String GRAPH_PROPERTIES_TOOLBAR_TITLE = "Graph Properties Tool Bar";
    protected static final String GRAPH_PROPERTIES_DIR_NAME = IMAGE_FILES_PATH + "GraphPropertiesToolBar/";
    protected static final String GRAPH_PROPERTIES_FILE_NAME = "GraphPropertiesToolBarData.txt";

    protected static final String GENERAL_TOOLBAR_TITLE = "General Tool Bar";
    protected static final String GENERAL_DIR_NAME = IMAGE_FILES_PATH + "GeneralToolBar/";
    protected static final String GENERAL_FILE_NAME = "GeneralToolBarData.txt";

    protected static final String NAVIGATION_TOOLBAR_TITLE = "Navigation Tool Bar";
    protected static final String NAVIGATION_DIR_NAME = IMAGE_FILES_PATH + "NavigationToolBar/";
    protected static final String NAVIGATION_FILE_NAME = "NavigationToolBarData.txt";

    protected TexturesLoader texturesLoaderIcons = null;
    protected JButton[] allToolBarButtons = null;
    protected int imageIconWidth = 0;
    protected int imageIconHeight = 0;
    protected float imageDivisor = 0.0f;
    protected boolean constructorInitializationFinished = false;

    public LayoutAbstractToolBar(String name, int orientation)
    {
        super(name);

        // set tool tip to be heavyweight so as to be visible on top of the main OpenGL heavyweight canvas
        ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
        if (BIOLAYOUT_USE_STATIC_COLOR) this.setBackground(BIOLAYOUT_MENUBAR_AND_TOOLBAR_COLOR);
        this.setOrientation(orientation);

        constructorInitializationFinished = true;
    }

    protected abstract String getFirstButtonName();

    // When tearing toolbars off we occasionally see JDK bug 8013550
    // It appears to be triggered by the size of components, so we attempt
    // to work around it here by enforcing a minimum size
    final int JAVA_BUG_8013550_MIN_COMPONENT_SIZE = 10;

    protected void addPaddingSpace()
    {
        int width = Math.max(JAVA_BUG_8013550_MIN_COMPONENT_SIZE, imageIconWidth);
        int height = Math.max(JAVA_BUG_8013550_MIN_COMPONENT_SIZE, imageIconHeight / 2);

        this.add(Box.createRigidArea(new Dimension(width, height)));
    }

    protected void addEmptySpaceAndSeparator()
    {
        int width = Math.max(JAVA_BUG_8013550_MIN_COMPONENT_SIZE, (int) (imageIconWidth / imageDivisor));
        int height = Math.max(JAVA_BUG_8013550_MIN_COMPONENT_SIZE, (int) (imageIconHeight / imageDivisor));
        Dimension dimension = new Dimension(width, height);

        this.add(Box.createRigidArea(dimension));
        this.addSeparator();
        this.add(Box.createRigidArea(dimension));
    }

    protected void setToolBarButtonImages(JButton button, String actionName)
    {
        button.setIcon( new ImageIcon( texturesLoaderIcons.getImage(actionName) ) );
        button.setRolloverIcon( new ImageIcon( texturesLoaderIcons.getImage(actionName + BUTTON_HOVER) ) );
        button.setPressedIcon( new ImageIcon( texturesLoaderIcons.getImage(actionName + BUTTON_PRESSED) ) );
        button.setDisabledIcon( new ImageIcon( texturesLoaderIcons.getImage(actionName) ) );
    }

    protected void setToolBarButtonAction(AbstractAction action, String actionName, int index)
    {
        setToolBarButtonAction(action, actionName, actionName, index);
    }

    protected void setToolBarButtonAction(AbstractAction action, String actionName, String actionToolTip, int index)
    {
        allToolBarButtons[index] = this.add(action);
        allToolBarButtons[index].setText("");
        allToolBarButtons[index].setToolTipText(actionToolTip);
        allToolBarButtons[index].setBorderPainted(false);
        allToolBarButtons[index].setMaximumSize( new Dimension(imageIconWidth, imageIconHeight) );
        setToolBarButtonImages(allToolBarButtons[index], actionName);
        if (BIOLAYOUT_USE_STATIC_COLOR) allToolBarButtons[index].setBackground(BIOLAYOUT_MENUBAR_AND_TOOLBAR_COLOR);
        allToolBarButtons[index].setContentAreaFilled(false);
        allToolBarButtons[index].setFocusPainted(false);
    }

    protected void setToolBarButtonAction(JButton button, String actionName)
    {
        setToolBarButtonAction(button, actionName, actionName);
    }

    protected void setToolBarButtonAction(JButton button, String actionName, String actionToolTip)
    {
        button.setText("");
        button.setToolTipText(actionToolTip);
        button.setBorderPainted(false);
        button.setMaximumSize( new Dimension(imageIconWidth, imageIconHeight) );
        setToolBarButtonImages(button, actionName);
        if (BIOLAYOUT_USE_STATIC_COLOR) button.setBackground(BIOLAYOUT_MENUBAR_AND_TOOLBAR_COLOR);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
    }


}