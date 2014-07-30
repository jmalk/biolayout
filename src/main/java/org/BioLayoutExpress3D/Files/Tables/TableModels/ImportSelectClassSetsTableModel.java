package org.BioLayoutExpress3D.Files.Tables.TableModels;

import java.util.*;
import javax.swing.table.*;

/**
*
* The ImportSelectClassSetsTableModel class provides all the table functionality for the ImportSelectClassSetsDialog class.
*
* @see org.BioLayoutExpress3D.Files.Dialogs.ImportSelectClassSetsDialog
* @author Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*/

public final class ImportSelectClassSetsTableModel extends AbstractTableModel
{
    /**
    *  Serial version UID variable for the ImportSelectClassSetsTableModel class.
    */
    public static final long serialVersionUID = 111222333444555695L;

    public static final String[] COLUMN_NAMES = { "Class Set Name", "Select/Deselect" };

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
    public String getColumnName(int column)
    {
        return COLUMN_NAMES[column];
    }

    @Override
    public Object getValueAt(int row, int column)
    {
        return (getRowCount() == 0) ? null : data[row][column];
    }

    @Override
    public boolean isCellEditable(int row, int column)
    {
        return (column == 1);
    }

    @Override
    public Class getColumnClass(int column)
    {
        Object obj = getValueAt(0, column);
        return (obj == null) ? Object.class : obj.getClass();
    }

    @Override
    public void setValueAt(Object value, int row, int column)
    {
        data[row][column] = value;
    }

    public void updateImportSelectClassSetsTable(Set<String> allClassSets)
    {
        int selectedSize = allClassSets.size();
        if (selectedSize == 0)
        {
            data = new Object[0][0];
            return;
        }

        data = new Object[selectedSize][2];

        int i = 0;
        for (String classSet : allClassSets)
        {
            data[i][0] = classSet;
            data[i][1] = Boolean.TRUE;
            i++;
        }

        fireTableDataChanged();
    }

    public void setSelectedAllColumns(boolean isSelected)
    {
        for (int i = 0; i < getRowCount(); i++)
            data[i][1] = isSelected;

        fireTableDataChanged();
    }

    public HashSet<String> getSelectedClassSets()
    {
        HashSet<String> selectedClassSets = new HashSet<String>();
        for (int i = 0; i < getRowCount(); i++)
            if ( ( (Boolean)data[i][1] ).booleanValue() )
                selectedClassSets.add( (String)data[i][0] );

        return selectedClassSets;
    }


}