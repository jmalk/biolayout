package org.BioLayoutExpress3D.CoreUI.ToolBars;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.Textures.*;
import static org.BioLayoutExpress3D.StaticLibraries.EnumUtils.*;
import static org.BioLayoutExpress3D.CoreUI.ToolBars.LayoutAbstractToolBar.GeneralToolBarButtons.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
*
* The LayoutGeneralToolBar is the BioLayout toolbar responsible for General control.
*
* @author Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public class LayoutGeneralToolBar extends LayoutAbstractToolBar
{

    public LayoutGeneralToolBar()
    {
        this(JToolBar.HORIZONTAL);
    }

    public LayoutGeneralToolBar(int orientation)
    {
        super(GENERAL_TOOLBAR_TITLE, orientation);

        texturesLoaderIcons = new TexturesLoader(GENERAL_DIR_NAME, GENERAL_FILE_NAME, false, false, true, TOOLBAR_IMAGE_ICON_RESIZE_RATIO, false);
        allToolBarButtons = new JButton[NUMBER_OF_GENERAL_TOOLBAR_BUTTONS];
        ImageIcon imageIcon = new ImageIcon( texturesLoaderIcons.getImage( getFirstButtonName() ) );
        imageIconWidth = imageIcon.getIconWidth();
        imageIconHeight = imageIcon.getIconHeight();
        imageDivisor = ( IS_MAC || UIManager.getLookAndFeel().getName().equals("Nimbus") ) ? 6.0f : 3.0f;
    }

    @Override
    protected final String getFirstButtonName()
    {
        return splitAndCapitalizeFirstCharacters(GRAPH_OPEN);
    }

    public void setGraphOpenAction(AbstractAction action)
    {
        setToolBarButtonAction(action, splitAndCapitalizeFirstCharacters(GRAPH_OPEN), splitCapitalizeFirstCharactersInvertOrderAndAddWhiteSpaceBetweenNames(GRAPH_OPEN), GRAPH_OPEN.ordinal() );
    }

    public void setGraphLastOpenAction(ArrayList<AbstractAction> actions, LayoutFrame layoutFrame)
    {
        allToolBarButtons[GRAPH_LAST_OPEN.ordinal()] = this.add( getGraphLastOpenAction(actions, layoutFrame) );
        setGraphLastOpenActionDetails();
    }

    public void refreshGraphLastOpenAction(ArrayList<AbstractAction> actions, LayoutFrame layoutFrame)
    {
        allToolBarButtons[GRAPH_LAST_OPEN.ordinal()].setAction( getGraphLastOpenAction(actions, layoutFrame) );
        setGraphLastOpenActionDetails();
    }

    private AbstractAction getGraphLastOpenAction(ArrayList<AbstractAction> actions, final LayoutFrame layoutFrame)
    {
        if ( !actions.isEmpty() )
            return actions.get(0);
        else
        {
            AbstractAction graphLastOpen = new AbstractAction("No Open Last Graph Available")
            {
                /**
                *  Serial version UID variable for the AbstractAction class.
                */
                public static final long serialVersionUID = 111222333444555712L;

                @Override
                public void actionPerformed(ActionEvent e)
                {
                    allToolBarButtons[GRAPH_LAST_OPEN.ordinal()].getModel().setRollover(false);
                    JOptionPane.showMessageDialog(layoutFrame, "No Open Last Graph Available!", "Open Last Graph", JOptionPane.INFORMATION_MESSAGE);
                }
            };
            graphLastOpen.setEnabled(false);

            return graphLastOpen;
        }
    }

    private void setGraphLastOpenActionDetails()
    {
        setToolBarButtonAction(allToolBarButtons[GRAPH_LAST_OPEN.ordinal()], splitAndCapitalizeFirstCharacters(GRAPH_LAST_OPEN), splitCapitalizeFirstCharactersInvertOrderAndAddWhiteSpaceBetweenNames(GRAPH_LAST_OPEN) );
    }

    public void setGraphSaveAction(AbstractAction action)
    {
        setToolBarButtonAction(action, splitAndCapitalizeFirstCharacters(GRAPH_SAVE), splitCapitalizeFirstCharactersInvertOrderAndAddWhiteSpaceBetweenNames(GRAPH_SAVE), GRAPH_SAVE.ordinal() );
    }

    public void setGraphPrintAction(AbstractAction action)
    {
        setToolBarButtonAction(action, splitAndCapitalizeFirstCharacters(GRAPH_PRINT), splitCapitalizeFirstCharactersInvertOrderAndAddWhiteSpaceBetweenNames(GRAPH_PRINT), GRAPH_PRINT.ordinal() );
    }

    public void setSnapshotAction(AbstractAction action)
    {
        setToolBarButtonAction(action, splitAndCapitalizeFirstCharacters(SNAPSHOT), capitalizeFirstCharacter(SNAPSHOT), SNAPSHOT.ordinal() );

        addEmptySpaceAndSeparator();
    }

    public void setGraphInformationAction(AbstractAction action)
    {
        setToolBarButtonAction(action, splitAndCapitalizeFirstCharacters(GRAPH_STATISTICS), splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(GRAPH_STATISTICS), GRAPH_STATISTICS.ordinal() );
    }

    public void setGraphFindAction(AbstractAction action)
    {
        setToolBarButtonAction(action, splitAndCapitalizeFirstCharacters(GRAPH_FIND), "Find By Name", GRAPH_FIND.ordinal() );
    }

    public void setRunMCLAction(AbstractAction action)
    {
        setToolBarButtonAction(action, splitAndCapitalizeFirstCharactersForAllButLastName(RUN_MCL), splitCapitalizeFirstCharactersForAllButLastNameAndAddWhiteSpaceBetweenNames(RUN_MCL), RUN_MCL.ordinal() );
    }

    public void setRunSPNAction(AbstractAction action)
    {
        setToolBarButtonAction(action, splitAndCapitalizeFirstCharactersForAllButLastName(RUN_SPN), splitCapitalizeFirstCharactersForAllButLastNameAndAddWhiteSpaceBetweenNames(RUN_SPN), RUN_SPN.ordinal() );
    }

    public void setClassViewerAction(AbstractAction action)
    {
        setToolBarButtonAction(action, splitAndCapitalizeFirstCharacters(CLASS_VIEWER), splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(CLASS_VIEWER), CLASS_VIEWER.ordinal() );
    }

    public void setAnimationControlAction(AbstractAction action)
    {
        setToolBarButtonAction(action, splitAndCapitalizeFirstCharacters(ANIMATION_CONTROL), splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(ANIMATION_CONTROL), ANIMATION_CONTROL.ordinal() );
    }

    public void setBurstLayoutIterationsAction(AbstractAction action)
    {
        setToolBarButtonAction(action, splitAndCapitalizeFirstCharacters(GeneralToolBarButtons.BURST_LAYOUT_ITERATIONS), splitCapitalizeFirstCharactersAndAddWhiteSpaceBetweenNames(GeneralToolBarButtons.BURST_LAYOUT_ITERATIONS), GeneralToolBarButtons.BURST_LAYOUT_ITERATIONS.ordinal() );
    }

    public void set2D3DSwitchAction(AbstractAction action)
    {
        allToolBarButtons[_2D_3D.ordinal()] = this.add(action);
        allToolBarButtons[_2D_3D.ordinal()].setText("");
        allToolBarButtons[_2D_3D.ordinal()].setToolTipText("2D / 3D Switch");
        allToolBarButtons[_2D_3D.ordinal()].setBorderPainted(false);
        allToolBarButtons[_2D_3D.ordinal()].setMaximumSize( new Dimension(imageIconWidth, imageIconHeight) );
        set2D3DButton();
        if (BIOLAYOUT_USE_STATIC_COLOR) allToolBarButtons[_2D_3D.ordinal()].setBackground(BIOLAYOUT_MENUBAR_AND_TOOLBAR_COLOR);
        allToolBarButtons[_2D_3D.ordinal()].setContentAreaFilled(false);
        allToolBarButtons[_2D_3D.ordinal()].setFocusPainted(false);

        addEmptySpaceAndSeparator();
    }

    public void setHomeAction(AbstractAction action)
    {
        setToolBarButtonAction( action, capitalizeFirstCharacter(HOME), HOME.ordinal() );
    }

    public void set2D3DButton()
    {
        String[] actionNames = _2D_3D.toString().substring(1).split(ENUM_REGEX + "+");
        setToolBarButtonImages(allToolBarButtons[_2D_3D.ordinal()], actionNames[RENDERER_MODE_3D ? 0 : 1]);
    }

    public void runSPNButtonResetRolloverState()
    {
        allToolBarButtons[RUN_SPN.ordinal()].getModel().setRollover(false);
    }

    public void animationControlButtonResetRolloverState()
    {
        allToolBarButtons[ANIMATION_CONTROL.ordinal()].getModel().setRollover(false);
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        boolean isDataSetLoaded = !DATA_TYPE.equals(DataTypes.NONE);
        for (int i = 0; i < NUMBER_OF_GENERAL_TOOLBAR_BUTTONS; i++)
        {
            if ( ( i >= GRAPH_SAVE.ordinal() ) && ( i <= GeneralToolBarButtons.BURST_LAYOUT_ITERATIONS.ordinal() ) ) // for the Graph Save, Graph Information, Graph Find, Run MCL, Run SPN, Class Viewer, Burst Layout Iterations buttons
                allToolBarButtons[i].setEnabled(enabled && isDataSetLoaded);
            else // for the Graph Open, Graph Last Open, 2D/3D & Home buttons
                allToolBarButtons[i].setEnabled(enabled);
        }
    }

    @Override
    public boolean isEnabled()
    {
        for (int i = 0; i < NUMBER_OF_GENERAL_TOOLBAR_BUTTONS; i++)
            if ( constructorInitializationFinished && !allToolBarButtons[i].isEnabled() )
                return false;

        return true;
    }


}