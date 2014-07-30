package org.BioLayoutExpress3D.Graph.Selection.SelectionUI.TableModels;

import java.util.*;
import javax.swing.table.*;

/**
*
* The FindMultipleClassesTableModel class provides all the table functionality for the FindMultipleClassesDialog JDialog class.
*
* @see org.BioLayoutExpress3D.Graph.Selection.SelectionUI.Dialogs.FindMultipleClassesDialog
* @author Thanos Theo, 2008-2009
* @version 3.0.0.0
*/

public final class FindMultipleClassesTableModel extends AbstractTableModel
{
    /**
    *  Serial version UID variable for the FindMultipleClassesTableModel class.
    */
    public static final long serialVersionUID = 111222333444555695L;

    public static final String[] COLUMN_NAMES = { "Class Name", "Select/Deselect" };

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

    @Override
    public boolean isCellEditable(int row, int col)
    {
        return (col == 1);
    }

    @Override
    public Class getColumnClass(int col)
    {
        Object obj = getValueAt(0, col);
        return (obj == null) ? Object.class : obj.getClass();
    }

    @Override
    public void setValueAt(Object value, int row, int col)
    {
        data[row][col] = value;
    }

    public void updateClassesTable(Set<String> allClasses)
    {
        int selectedSize = allClasses.size();
        if (selectedSize == 0)
        {
            data = new Object[0][0];
            return;
        }

        data = new Object[selectedSize][2];

        int i = 0;
        for (String thisClass : allClasses)
        {
            data[i][0] = thisClass;
            data[i][1] = Boolean.FALSE;
            i++;
        }

        fireTableDataChanged();
    }

    public void setSelectedClass(String className)
    {
        for (int i = 0; i < getRowCount(); i++)
            if ( ( (String)data[i][0] ).equals(className) )
                data[i][1] = Boolean.TRUE;

        fireTableDataChanged();
    }

    public void setSelectedAllColumns(boolean isSelected)
    {
        for (int i = 0; i < getRowCount(); i++)
            data[i][1] = isSelected;

        fireTableDataChanged();
    }

    public HashSet<String> getSelectedClasses()
    {
        HashSet<String> selectedClasses = new HashSet<String>();
        for (int i = 0; i < getRowCount(); i++)
            if ( ( (Boolean)data[i][1] ).booleanValue() )
                selectedClasses.add( (String)data[i][0] );

        return selectedClasses;
    }


}