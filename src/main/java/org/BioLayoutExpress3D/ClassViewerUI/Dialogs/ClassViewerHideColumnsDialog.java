package org.BioLayoutExpress3D.ClassViewerUI.Dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import org.BioLayoutExpress3D.ClassViewerUI.*;
import org.BioLayoutExpress3D.ClassViewerUI.Tables.*;
import org.BioLayoutExpress3D.ClassViewerUI.Tables.TableModels.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
*
* The ClassViewerHideColumnsDialog class is a JDialog to hide/unhide any columns in the ClassViewerTabModelGeneral class appearing in ClassViewer.
*
* @see org.BioLayoutExpress3D.ClassViewerUI.ClassViewerFrame
* @author Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*/

public final class ClassViewerHideColumnsDialog extends JDialog
{
    /**
    *  Serial version UID variable for the LayoutLegend class.
    */
    public static final long serialVersionUID = 111222333444555990L;

    private JButton selectDeselectAllButton = null;
    private boolean selectDeselectAllButtonModeState = false;

    private ClassViewerHideColumnsTableModel hideColumnsTableModel = null;
    boolean enableHideColumnsAndExportButtons = false;

    public ClassViewerHideColumnsDialog(ClassViewerFrame classViewerFrame)
    {
        super(classViewerFrame, "Columns To Hide", false);

        initComponents(classViewerFrame);
    }

    private void initComponents(final ClassViewerFrame classViewerFrame)
    {
        hideColumnsTableModel = new ClassViewerHideColumnsTableModel(classViewerFrame);
        ClassViewerTable table = new ClassViewerTable(hideColumnsTableModel, ClassViewerHideColumnsTableModel.COLUMN_NAMES, CV_AUTO_SIZE_COLUMNS.get());
        table.setRowSorter( new TableRowSorter<ClassViewerHideColumnsTableModel>(hideColumnsTableModel) ); // provide a sorting mechanism to the table

        selectDeselectAllButton = createSelectDeselectAllButton();
        selectDeselectAllButton.setToolTipText("Deselect All");

        JScrollPane tableScrollPane = new JScrollPane(table);
        table.setPreferredScrollableViewportSize( new Dimension(200, 400) );
        JPanel tablePanel = new JPanel(true);
        tablePanel.setLayout( new BorderLayout() );
        tablePanel.add(tableScrollPane, BorderLayout.NORTH);
        tablePanel.add(selectDeselectAllButton, BorderLayout.CENTER);
        JButton hideWindowButton = createHideWindowButton(classViewerFrame);
        hideWindowButton.setToolTipText("Hide Window");
        tablePanel.add(hideWindowButton, BorderLayout.SOUTH);

        this.getContentPane().add(tablePanel);
        this.setResizable(true);
        this.pack();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // window gets positioned at left of layout frame main window
        if ( ( classViewerFrame.getWidth() + 1.5 * this.getWidth() ) > SCREEN_DIMENSION.width )
            this.setLocation( ( SCREEN_DIMENSION.width - classViewerFrame.getWidth() ) / 2, ( SCREEN_DIMENSION.height - this.getHeight() ) / 2 );
        else
            this.setLocation( ( SCREEN_DIMENSION.width - classViewerFrame.getWidth() ) / 2 - this.getWidth(), ( SCREEN_DIMENSION.height - this.getHeight() ) / 2 );
        this.addWindowListener( new WindowAdapter()
        {
           @Override
            public void windowClosing(WindowEvent e)
            {
                closeDialogWindow(classViewerFrame);
            }
        } );
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
                hideColumnsTableModel.setSelectedAllColumns(!selectDeselectAllButtonModeState);
            }
        } );
    }

    private JButton createHideWindowButton(final ClassViewerFrame classViewerFrame)
    {
        return new JButton( new AbstractAction("Hide Window")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555691L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                closeDialogWindow(classViewerFrame);
            }
        } );
    }

    private void resetSelectDeselectAllButton()
    {
        this.selectDeselectAllButtonModeState = false;
        selectDeselectAllButton.setText("Deselect All");
    }

    private void closeDialogWindow(ClassViewerFrame classViewerFrame)
    {
        if (!enableHideColumnsAndExportButtons) classViewerFrame.getChooseColumnsToHideButton().setEnabled(false);
        setVisible(false);
    }

    public void updateClassViewerHideColumnsTable(ClassViewerFrame classViewerFrame, boolean enableHideColumnsAndExportButtons, boolean updateExpressionGraphViewOnly, boolean notUpdateTitleBar)
    {
        resetSelectDeselectAllButton();

        this.enableHideColumnsAndExportButtons = enableHideColumnsAndExportButtons;
        hideColumnsTableModel.updateClassViewerHideColumnsTable(classViewerFrame, enableHideColumnsAndExportButtons, updateExpressionGraphViewOnly, notUpdateTitleBar);
    }


}