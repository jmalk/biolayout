package org.BioLayoutExpress3D.Graph.Selection.SelectionUI.Dialogs;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import org.BioLayoutExpress3D.ClassViewerUI.Tables.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.Graph.Selection.SelectionUI.TableModels.*;
import org.BioLayoutExpress3D.Network.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
*
*
* @author Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public final class FindMultipleClassesDialog extends JDialog
{
    /**
    *  Serial version UID variable for the FindClassDialog class.
    */
    public static final long serialVersionUID = 111222333444555744L;

    private static final int CLASS_SET_NAMES_COLUMN = 0;

    private LayoutFrame layoutFrame = null;
    private JButton selectDeselectAllButton = null;
    private boolean selectDeselectAllButtonModeState = true;

    private FindMultipleClassesTableModel findMultipleClassesTableModel = null;
    private String prevClassSetName = "";
    private AbstractAction findMultipleClassesDialogAction = null;

    public FindMultipleClassesDialog(LayoutFrame layoutFrame, JFrame jFrame)
    {
        super(jFrame, "Find By Multiple Classes", true);

        this.layoutFrame = layoutFrame;

        initActions();
        initComponents(jFrame);
    }

    private void initActions()
    {
        findMultipleClassesDialogAction = new AbstractAction("Find By Multiple Classes")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555683L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                openDialogWindow();
            }
        };
        findMultipleClassesDialogAction.setEnabled(false);
    }

    private void initComponents(final JFrame jFrame)
    {
        findMultipleClassesTableModel = new FindMultipleClassesTableModel();
        ClassViewerTable table = new ClassViewerTable(findMultipleClassesTableModel, FindMultipleClassesTableModel.COLUMN_NAMES, CV_AUTO_SIZE_COLUMNS.get());
        TableRowSorter<FindMultipleClassesTableModel> sorter = new TableRowSorter<FindMultipleClassesTableModel>(findMultipleClassesTableModel);
        table.setRowSorter(sorter); // provide a sorting mechanism to the table
        table.sortTableByColumn(CLASS_SET_NAMES_COLUMN, sorter);

        selectDeselectAllButton = createSelectDeselectAllButton();
        selectDeselectAllButton.setToolTipText("Select All");

        JPanel buttonsPanel = new JPanel(true);
        buttonsPanel.setLayout( new GridLayout() );
        JButton okButton = createOkButton(jFrame);
        okButton.setToolTipText("OK");
        JButton cancelButton = createCancelButton();
        cancelButton.setToolTipText("Cancel");
        buttonsPanel.add(okButton);
        buttonsPanel.add(cancelButton);

        JScrollPane tableScrollPane = new JScrollPane(table);
        table.setPreferredScrollableViewportSize( new Dimension(240, 400) );
        JPanel tablePanel = new JPanel(true);
        tablePanel.setLayout( new BorderLayout() );
        tablePanel.add(tableScrollPane, BorderLayout.NORTH);
        tablePanel.add(selectDeselectAllButton, BorderLayout.CENTER);
        tablePanel.add(buttonsPanel, BorderLayout.SOUTH);

        this.getContentPane().add(tablePanel);
        this.setResizable(true);
        this.pack();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // window gets positioned at left of the ImportClassSetsDialog
        this.setLocationRelativeTo(null);
        this.addWindowListener( new WindowAdapter()
        {
           @Override
            public void windowClosing(WindowEvent e)
            {
                closeDialogWindow();
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
                String buttonText = ( (!selectDeselectAllButtonModeState) ? "Deselect" : "Select" ) + " All";
                selectDeselectAllButton.setText(buttonText);
                selectDeselectAllButton.setToolTipText(buttonText);
                findMultipleClassesTableModel.setSelectedAllColumns(!selectDeselectAllButtonModeState);
            }
        } );
    }

    private JButton createOkButton(final JFrame jFrame)
    {
        return new JButton( new AbstractAction("OK")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555691L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                HashSet<VertexClass> selectedVertexClasses = new HashSet<VertexClass>();
                for ( String selectedClass : findMultipleClassesTableModel.getSelectedClasses() )
                    selectedVertexClasses.add( layoutFrame.getLayoutClassSetsManager().getCurrentClassSetAllClasses().getClassesNamesMap().get(selectedClass) );
                layoutFrame.getGraph().getSelectionManager().findMultipleClasses(jFrame, selectedVertexClasses);

                closeDialogWindow();
            }
        } );
    }

    private JButton createCancelButton()
    {
        return new JButton( new AbstractAction("Cancel")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555691L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                closeDialogWindow();
            }
        } );
    }

    private void resetSelectDeselectAllButton()
    {
        this.selectDeselectAllButtonModeState = true;
        selectDeselectAllButton.setText("Select All");
    }

    private void selectCurrentClassName()
    {
        String currentClassName = layoutFrame.getClassViewerFrame().getCurrentClassName();
        if ( !currentClassName.isEmpty() )
            findMultipleClassesTableModel.setSelectedClass(currentClassName);
    }

    public void closeDialogWindow()
    {
        this.setVisible(false);
    }

    public void openDialogWindow()
    {
        // only reset enable/disable button state if a change of class sets have been detected
        String currentClassSetName = layoutFrame.getLayoutClassSetsManager().getCurrentClassSetAllClasses().getClassSetName();
        if ( !currentClassSetName.equals(prevClassSetName) )
        {
            findMultipleClassesTableModel.updateClassesTable( layoutFrame.getLayoutClassSetsManager().getCurrentClassSetAllClasses().getClassesNamesMap().keySet() );
            prevClassSetName = currentClassSetName;
            resetSelectDeselectAllButton();
            selectCurrentClassName();
        }
        else
            selectCurrentClassName();

        this.setVisible(true);
    }

    public AbstractAction getFindMultipleClassesDialogAction()
    {
        return findMultipleClassesDialogAction;
    }


}