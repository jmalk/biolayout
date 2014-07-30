package org.BioLayoutExpress3D.CoreUI.Dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.CoreUI.Tables.*;
import org.BioLayoutExpress3D.CoreUI.Tables.TableModels.*;
import static org.BioLayoutExpress3D.CoreUI.Tables.LayoutClassesTable.TableSeparatorTypes.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
*
* @author Leon Goldovsky, full refactoring by Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public final class LayoutShowClassesLegendsDialog extends JDialog
{
    /**
    *  Serial version UID variable for the LayoutLegend class.
    */
    public static final long serialVersionUID = 111222333444555690L;

    private LayoutFrame layoutFrame = null;
    private LayoutClassLegendTableModel layoutClassLegendTableModel = null;
    private AbstractAction layoutShowClassesLegendsShowAction = null;
    private AbstractAction layoutShowClassesLegendsHideAction = null;

    public LayoutShowClassesLegendsDialog(final LayoutFrame layoutFrame)
    {
        super(layoutFrame, "Classes Legends", false);

        this.layoutFrame = layoutFrame;

        initActions();
        initComponents();
    }

    private void initActions()
    {
        layoutShowClassesLegendsShowAction = new AbstractAction("Show Classes Legends")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555684L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                setVisible(true);
                layoutFrame.toggleLegend(layoutShowClassesLegendsHideAction);
            }
        };
        layoutShowClassesLegendsShowAction.setEnabled(false);

        layoutShowClassesLegendsHideAction = new AbstractAction("Hide Classes Legends")
        {
            /**
            *  Serial version UID variable for the AbstractAction class.
            */
            public static final long serialVersionUID = 111222333444555685L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                setVisible(false);
                layoutFrame.toggleLegend(layoutShowClassesLegendsShowAction);
            }
        };
    }

    private void initComponents()
    {
        layoutClassLegendTableModel = new LayoutClassLegendTableModel();
        LayoutClassTable table = new LayoutClassTable(layoutClassLegendTableModel, LayoutClassLegendTableModel.COLUMN_NAMES);
        TableRowSorter<LayoutClassLegendTableModel> sorter = new TableRowSorter<LayoutClassLegendTableModel>(layoutClassLegendTableModel);
        table.setRowSorter(sorter); // provide a sorting mechanism to the table
        table.setDefaultRenderer( Color.class, new LayoutClassTable.ColorRenderer(true) );
        sorter.setComparator( CLASS_COLOR_COLUMN.ordinal(), new LayoutClassTable.ColorSorting() );
        table.sortTableByColumn(CLASS_DESCRIPTION_COLUMN.ordinal(), sorter);

        JButton hideButton = createHideButton();
        hideButton.setToolTipText("Hide Window");
        JScrollPane tableScrollPane = new JScrollPane(table);
        table.setPreferredScrollableViewportSize( new Dimension(250, 500) );
        JPanel tablePanel = new JPanel(true);
        tablePanel.setLayout( new BorderLayout() );
        // tablePanel.setPreferredSize(new Dimension(100,50));
        // tablePanel.add(table.getTableHeader(), BorderLayout.NORTH);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        tablePanel.add(hideButton, BorderLayout.SOUTH);

        this.getContentPane().add(tablePanel);
        this.setResizable(true);
        this.pack();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.addWindowListener( new WindowAdapter()
        {
           @Override
            public void windowClosing(WindowEvent e)
            {
                setVisible(false);
                layoutFrame.toggleLegend(layoutShowClassesLegendsShowAction);
            }
        } );
    }

    private JButton createHideButton()
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
                setVisible(false);
                layoutFrame.toggleLegend(layoutShowClassesLegendsShowAction);
            }
        } );
    }

    @Override
    public void setVisible(boolean state)
    {
        layoutClassLegendTableModel.updateClassLegend( layoutFrame.getLayoutClassSetsManager().getCurrentClassSetAllClasses() );
        super.setVisible(state);
    }

    public LayoutClassLegendTableModel getTableModel()
    {
        return layoutClassLegendTableModel;
    }

    public AbstractAction getShowClassesLegendsShowAction()
    {
        return layoutShowClassesLegendsShowAction;
    }

    public AbstractAction getShowClassesLegendsHideAction()
    {
        return layoutShowClassesLegendsHideAction;
    }


}