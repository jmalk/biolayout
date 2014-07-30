package org.BioLayoutExpress3D.CoreUI.Tables.TableModels;

import javax.swing.table.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.Network.*;

/**
*
* @author Leon Goldovsky, full refactoring by Thanos Theo, 2008-2009
* @version 3.0.0.0
*
*/

public final class LayoutClassLegendTableModel extends AbstractTableModel
{
    /**
    *  Serial version UID variable for the LayoutClassLegendTableModel class.
    */
    public static final long serialVersionUID = 111222333444555695L;

    public static final String[] COLUMN_NAMES = { "Class Color", "Class Description" };

    private Object[][] data = null;

    @Override
    public int getColumnCount()
    {
        return COLUMN_NAMES.length;
    }

    @Override
    public int getRowCount()
    {
        return (data != null) ? data.length : 0;
    }

    @Override
    public String getColumnName(int col)
    {
        return COLUMN_NAMES[col];
    }

    @Override
    public Object getValueAt(int row, int col)
    {
        return (getRowCount() == 0) ? null : data[row][col];
    }

    /*
    * Don't need to implement this method unless your table's
    * editable.
    */
    @Override
    public boolean isCellEditable(int row, int col)
    {
        return false;
    }

    @Override
    public Class getColumnClass(int col)
    {
        Object obj = getValueAt(0, col);
        return (obj == null) ? Object.class : obj.getClass();
    }

    public void updateClassLegend(LayoutClasses layoutClasses)
    {
        int selectedSize = layoutClasses.getTotalClasses();
        if (selectedSize == 0)
        {
            data = new Object[0][0];
            return;
        }

        data = new Object[selectedSize + 1][2];

        int i = 0;
        VertexClass vertexClass = null;
        for ( Integer key : layoutClasses.getAllClassesKeySet() )
        {
            vertexClass = layoutClasses.getClassByID(key);
            data[i][0] = vertexClass.getColor();
            data[i][1] = vertexClass.getName();
            i++;
        }

        fireTableDataChanged();
    }


}