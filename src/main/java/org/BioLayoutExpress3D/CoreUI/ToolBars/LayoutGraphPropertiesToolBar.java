package org.BioLayoutExpress3D.CoreUI.ToolBars;

import javax.swing.*;
import org.BioLayoutExpress3D.Textures.*;
import static org.BioLayoutExpress3D.StaticLibraries.EnumUtils.*;
import static org.BioLayoutExpress3D.CoreUI.ToolBars.LayoutAbstractToolBar.GraphPropertiesToolBarButtons.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
*
* The LayoutGraphPropertiesToolBar is the BioLayout toolbar responsible for Graph Properties control.
*
* @author Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public final class LayoutGraphPropertiesToolBar extends LayoutAbstractToolBar
{

    public LayoutGraphPropertiesToolBar()
    {
        this(JToolBar.VERTICAL);
    }

    public LayoutGraphPropertiesToolBar(int orientation)
    {
        super(GRAPH_PROPERTIES_TOOLBAR_TITLE, orientation);

        texturesLoaderIcons = new TexturesLoader(GRAPH_PROPERTIES_DIR_NAME, GRAPH_PROPERTIES_FILE_NAME, false, false, true, TOOLBAR_IMAGE_ICON_RESIZE_RATIO, false);
        allToolBarButtons = new JButton[NUMBER_OF_GRAPH_PROPERTIES_TOOLBAR_BUTTONS];
        ImageIcon imageIcon = new ImageIcon( texturesLoaderIcons.getImage( getFirstButtonName() ) );
        imageIconWidth = imageIcon.getIconWidth();
        imageIconHeight = imageIcon.getIconHeight();
        imageDivisor = ( IS_MAC || UIManager.getLookAndFeel().getName().equals("Nimbus") ) ? 9.0f : 6.5f;
    }

    @Override
    protected final String getFirstButtonName()
    {
        return capitalizeFirstCharacter(GENERAL);
    }

    public void setGeneralAction(AbstractAction action)
    {
        addPaddingSpace();
        String actionName = capitalizeFirstCharacter(GENERAL);
        setToolBarButtonAction( action, actionName, actionName + BUTTON_PROPERTIES, GENERAL.ordinal() );
        addEmptySpaceAndSeparator();
    }

    public void setLayoutAction(AbstractAction action)
    {
        String actionName = capitalizeFirstCharacter(LAYOUT);
        setToolBarButtonAction( action, actionName, actionName + BUTTON_PROPERTIES, LAYOUT.ordinal() );
        addEmptySpaceAndSeparator();
    }

    public void setRenderingAction(AbstractAction action)
    {
        String actionName = capitalizeFirstCharacter(RENDERING);
        setToolBarButtonAction( action, actionName, actionName + BUTTON_PROPERTIES, RENDERING.ordinal() );
        addEmptySpaceAndSeparator();
    }

    public void setMCLAction(AbstractAction action)
    {
        String actionName = MCL.toString();
        setToolBarButtonAction( action, actionName, actionName + BUTTON_PROPERTIES, MCL.ordinal() );
        addEmptySpaceAndSeparator();
    }

    public void setSimulationAction(AbstractAction action)
    {
        String actionName = capitalizeFirstCharacter(SIMULATION);
        setToolBarButtonAction( action, actionName, actionName + BUTTON_PROPERTIES, SIMULATION.ordinal() );
        addEmptySpaceAndSeparator();
    }

    public void setParallelismAction(AbstractAction action)
    {
        String actionName = capitalizeFirstCharacter(PARALLELISM);
        setToolBarButtonAction( action, actionName, actionName + BUTTON_PROPERTIES, PARALLELISM.ordinal() );
        addEmptySpaceAndSeparator();
    }

    public void setSearchAction(AbstractAction action)
    {
        String actionName = capitalizeFirstCharacter(SEARCH);
        setToolBarButtonAction( action, actionName, actionName + BUTTON_PROPERTIES, SEARCH.ordinal() );
        addEmptySpaceAndSeparator();
    }

    public void setNodesAction(AbstractAction action)
    {
        String actionName = capitalizeFirstCharacter(NODES);
        setToolBarButtonAction( action, actionName, actionName + BUTTON_PROPERTIES, NODES.ordinal() );
        addEmptySpaceAndSeparator();
    }

    public void setEdgesAction(AbstractAction action)
    {
        String actionName = capitalizeFirstCharacter(EDGES);
        setToolBarButtonAction( action, actionName, actionName + BUTTON_PROPERTIES, EDGES.ordinal() );
        addEmptySpaceAndSeparator();
    }

    public void setClassesAction(AbstractAction action)
    {
        String actionName = capitalizeFirstCharacter(CLASSES);
        setToolBarButtonAction( action, actionName, actionName + BUTTON_PROPERTIES, CLASSES.ordinal() );
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        for (int i = 0; i < NUMBER_OF_GRAPH_PROPERTIES_TOOLBAR_BUTTONS; i++)
            allToolBarButtons[i].setEnabled(enabled);
    }

    @Override
    public boolean isEnabled()
    {
        for (int i = 0; i < NUMBER_OF_GRAPH_PROPERTIES_TOOLBAR_BUTTONS; i++)
            if ( constructorInitializationFinished && !allToolBarButtons[i].isEnabled() )
                return false;

        return true;
    }


}