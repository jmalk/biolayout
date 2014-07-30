package org.BioLayoutExpress3D.ClassViewerUI.Tables;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.ListSelectionEvent;
import org.BioLayoutExpress3D.ClassViewerUI.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.Network.*;
import org.BioLayoutExpress3D.ClassViewerUI.Tables.TableModels.ClassViewerTableModelGeneral;
import org.BioLayoutExpress3D.StaticLibraries.Utils;

/**
*
*  The ClassViewerTable class supports initial sorting at a particular column of the table and full tooltip support for both columns and cells.
*  It is used it for all the tables of the Class Viewer.
*
* @author Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public final class ClassViewerTable extends JTable
{
    /**
    *  Serial version UID variable for the ClassViewerTable class.
    */
    public static final long serialVersionUID = 111222333444555600L;

    /**
    *  Variable to store the column names.
    */
    private String[] columnNames = null;

    /**
    *  When true, automatically size the columns in prepareRenderer.
    */
    private boolean autoSizeColumns;

    /**
    *  The constructor of the ClassViewerTable class.
    */
    public ClassViewerTable(TableModel tableModel, String[] columnNames, boolean autoSizeColumns)
    {
        super(tableModel);

        this.columnNames = columnNames;
        this.autoSizeColumns = autoSizeColumns;
    }

    /**
    *  Implements table cell tool tips.
    */
    @Override
    public String getToolTipText(MouseEvent e)
    {
        Point p = e.getPoint();
        int rowIndex = rowAtPoint(p);
        int colIndex = columnAtPoint(p);
        Object cellData = null;

        if (rowIndex >= 0 && colIndex >= 0)
        {
            cellData = getValueAt(rowIndex, colIndex);
        }
        else
        {
            System.out.println("Calling ClassViewerTable.getValueAt(" + rowIndex + ", " + colIndex + ")");
        }

        if (cellData == null)
        {
            return "";
        }

        if (cellData instanceof Color)
        {
            Color color = (Color)cellData;
            return "RGB Color value: " + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue();
        }
        else if (cellData instanceof Double)
        {
            double val = ( (Double)cellData ).doubleValue();
            String text = "";

            if ( (val > 1000) || (val < 0.0001) )
                text = ClassViewerFrame.EntropyTableCellRenderer.DECIMAL_FORMAT_1.format(cellData);
            else
                text = ClassViewerFrame.EntropyTableCellRenderer.DECIMAL_FORMAT_2.format(cellData);

            return text;
        }
        else if (cellData instanceof VertexClass)
        {
            VertexClass vertexClass = (VertexClass)cellData;
            return ( ( !vertexClass.getName().equals(LayoutClasses.NO_CLASS) ) ? "Class Name: " + vertexClass.getName() : LayoutClasses.NO_CLASS );
        }
        else
            return cellData.toString();
    }

    /**
    *  Implements table header tool tips.
    */
    @Override
    protected JTableHeader createDefaultTableHeader()
    {
        return new JTableHeader(columnModel)
        {
            /**
            *  Serial version UID variable for the inner class.
            */
            public static final long serialVersionUID = 111222333444555601L;

            @Override
            public String getToolTipText(MouseEvent e)
            {
                int index = columnModel.getColumnIndexAtX( e.getPoint().x );
                if (index < 0)
                {
                    return "";
                }
                int modelIndex = columnModel.getColumn(index).getModelIndex();
                return columnNames[modelIndex];
            }
        };
    }

    public void synchroniseHighlightWithSelection()
    {
        if (!highlightIsSelection)
        {
            return;
        }

        ArrayList<Integer> selectedRows = new ArrayList<Integer>();

        for (int row = 0; row < getRowCount(); row++)
        {
            Boolean selected = (Boolean)getValueAt(row, 0);

            if (selected != null && selected.booleanValue())
            {
                selectedRows.add(row);
            }
        }

        highlightIsSelection = false;

        this.clearSelection();
        for (Integer selectedRow : selectedRows)
        {
            this.addRowSelectionInterval(selectedRow, selectedRow);
        }

        highlightIsSelection = true;
    }

    private boolean updateResetSelectDeselectAllButton = true;

    public void setUpdateResetSelectDeselectAllButton(boolean updateResetSelectDeselectAllButton)
    {
        this.updateResetSelectDeselectAllButton = updateResetSelectDeselectAllButton;
    }

    private boolean highlightIsSelection;

    public void setHighlightIsSelection(boolean highlightIsSelection)
    {
        this.highlightIsSelection = highlightIsSelection;
        synchroniseHighlightWithSelection();
    }

    /**
    *  Override so that we can disable the selection column.
    */
    @Override
    public boolean isCellEditable(int row, int column)
    {
        if (column == 0 && highlightIsSelection)
        {
            return false;
        }

        return true;
    }

    @Override
    public void valueChanged(ListSelectionEvent e)
    {
        super.valueChanged(e);

        if (highlightIsSelection && updateResetSelectDeselectAllButton && !e.getValueIsAdjusting())
        {
            ArrayList<Integer> highlightedRows = new ArrayList<Integer>();
            for (int row = 0; row < getRowCount(); row++)
            {
                if (isRowSelected(row))
                {
                    highlightedRows.add(convertRowIndexToModel(row));
                }
            }

            ((ClassViewerTableModelGeneral)dataModel).setSelectedRows(highlightedRows);
        }
    }

    /**
    *  Updates the table's column names.
    */
    public void updateTableColumnNames(String[] columnNames)
    {
        if (autoSizeColumns || !Utils.areArraysEqual(columnNames, this.columnNames))
        {
            // Only recreate the columns when autosizing or when the column names change
            setAutoCreateColumnsFromModel(true);
        }

        this.columnNames = columnNames;
    }

    /**
    *  Sorts the table by a given column.
    */
    public void sortTableByColumn(int columnIndexToSort, TableRowSorter<?> sorter)
    {
        sortTableByColumn(columnIndexToSort, sorter, true);
    }

    /**
    *  Sorts the table by a given column.
    *  Overriden version that can select ascending or descending order.
    */
    public void sortTableByColumn(int columnIndexToSort, TableRowSorter<?> sorter, boolean isAscending)
    {
        RowSorter.SortKey sortKey = new RowSorter.SortKey(columnIndexToSort, (isAscending) ? SortOrder.ASCENDING : SortOrder.DESCENDING);
        ArrayList<RowSorter.SortKey> sorterList = new ArrayList<RowSorter.SortKey>(1);
        sorterList.add(sortKey);
        sorter.setSortable(columnIndexToSort, true);
        sorter.setSortKeys(sorterList);
        sorter.sort();
    }

    /**
    *  Static inner class that implements a comparator for vertex class sorting. To be used with the table.
    */
    public static class VertexClassSorting implements Comparator<VertexClass>, Serializable
    {

        /**
        *  Serial version UID variable for the VertexClassSorting class.
        */
        public static final long serialVersionUID = 111222333444555620L;

        @Override
        public int compare(VertexClass vertexClass1, VertexClass vertexClass2)
        {
            // push the NO_CLASS string at the end of a possible sorting
            return VertexClass.compare( vertexClass1.getName(), vertexClass2.getName() );
        }


    }

    public boolean getAutoSizeColumns()
    {
        return autoSizeColumns;
    }

    public void setAutoSizeColumns(boolean autoSizeColumns)
    {
        this.autoSizeColumns = autoSizeColumns;
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
    {
        Component component = super.prepareRenderer(renderer, row, column);

        if (autoSizeColumns)
        {
            int rendererWidth = component.getPreferredSize().width;
            TableColumn tableColumn = getColumnModel().getColumn(column);
            tableColumn.setPreferredWidth(Math.max(rendererWidth +
                    getIntercellSpacing().width,
                    tableColumn.getPreferredWidth()));
        }

        return component;
    }
}