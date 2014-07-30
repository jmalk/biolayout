package org.BioLayoutExpress3D.Files.Dialogs;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.ClassViewerUI.Tables.*;
import org.BioLayoutExpress3D.Files.Tables.TableModels.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
*
* The ImportSelectClassSets class is a JDialog to select/deselect class sets for the import class sets process.
*
* @see org.BioLayoutExpress3D.Files.Dialogs.ImportClassSetsDialog
* @author Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*/

final class ImportSelectClassSetsDialog extends JDialog // package access
{
    /**
    *  Serial version UID variable for the LayoutLegend class.
    */
    public static final long serialVersionUID = 111222333444555990L;

    private static final int CLASS_SET_NAMES_COLUMN = 0;

    private JButton selectDeselectAllButton = null;
    private boolean selectDeselectAllButtonModeState = false;

    private ImportSelectClassSetsTableModel importSelectClassSetsTableModel = null;
    boolean enableHideColumnsAndExportButtons = false;

    public ImportSelectClassSetsDialog(LayoutFrame layoutFrame, ImportClassSetsDialog importClassSetsDialog)
    {
        super(layoutFrame, "Class Sets To Parse", false);

        initComponents(importClassSetsDialog);
    }

    private void initComponents(ImportClassSetsDialog importClassSetsDialog)
    {
        importSelectClassSetsTableModel = new ImportSelectClassSetsTableModel();
        ClassViewerTable table = new ClassViewerTable(importSelectClassSetsTableModel, ImportSelectClassSetsTableModel.COLUMN_NAMES, CV_AUTO_SIZE_COLUMNS.get());
        TableRowSorter<ImportSelectClassSetsTableModel> sorter = new TableRowSorter<ImportSelectClassSetsTableModel>(importSelectClassSetsTableModel);
        table.setRowSorter(sorter); // provide a sorting mechanism to the table
        table.sortTableByColumn(CLASS_SET_NAMES_COLUMN, sorter);

        selectDeselectAllButton = createSelectDeselectAllButton();
        selectDeselectAllButton.setToolTipText("Deselect All");

        JScrollPane tableScrollPane = new JScrollPane(table);
        table.setPreferredScrollableViewportSize( new Dimension(240, 400) );
        JPanel tablePanel = new JPanel(true);
        tablePanel.setLayout( new BorderLayout() );
        tablePanel.add(tableScrollPane, BorderLayout.NORTH);
        tablePanel.add(selectDeselectAllButton, BorderLayout.CENTER);

        this.getContentPane().add(tablePanel);
        this.setResizable(true);
        this.pack();
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // so as to be controlled from within ImportClassSetsDialog only
        this.setLocationRelativeTo(null);

        // so as to be excluded from NOT being able to be used while the ImportClassSetsDialog is on
        this.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
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
                importSelectClassSetsTableModel.setSelectedAllColumns(!selectDeselectAllButtonModeState);
            }
        } );
    }

    private void resetSelectDeselectAllButton()
    {
        this.selectDeselectAllButtonModeState = false;
        selectDeselectAllButton.setText("Deselect All");
    }

    public void closeDialogWindow()
    {
        this.setVisible(false);
    }

    public void openDialogWindow()
    {
        this.setVisible(true);
    }

    public void updateImportSelectClassSetsTable(Set<String> allClassSets)
    {
        resetSelectDeselectAllButton();
        importSelectClassSetsTableModel.updateImportSelectClassSetsTable(allClassSets);
    }

    public HashSet<String> getSelectedClassSets()
    {
       return importSelectClassSetsTableModel.getSelectedClassSets();
    }


}