package org.BioLayoutExpress3D.CoreUI.Tables.TableModels;

import javax.swing.*;
import javax.swing.table.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.CoreUI.Tables.*;

/**
*
*  The LayoutClassesTableModel defines the table model for the LayoutClassesTable class.
*
* @author Thanos Theo, 2008-2009
* @version 3.0.0.0
*
*/

public final class LayoutClassesTableModel extends AbstractTableModel
{
    public static final String[] COLUMN_NAMES = { "Class ID", "Class Color", "Class Description" };

    private LayoutClassesTable layoutClassesTable = null;
    private Object[][] classesData = null;

    /**
    *  Serial version UID variable for the TableModel class.
    */
    public static final long serialVersionUID = 111222333444555677L;

    public LayoutClassesTableModel(LayoutClassesTable layoutClassesTable, Object[][] classesData)
    {
        this.layoutClassesTable = layoutClassesTable;
        this.classesData = classesData;
    }

    @Override
    public int getColumnCount()
    {
        return COLUMN_NAMES.length;
    }

    @Override
    public int getRowCount()
    {
        return (classesData != null) ? classesData.length : 0;
    }

    @Override
    public String getColumnName(int col)
    {
        return COLUMN_NAMES[col];
    }

    @Override
    public Object getValueAt(int row, int col)
    {
        return (getRowCount() == 0) ? null : classesData[row][col];
    }

    /*
    * JTable uses this method to determine the default renderer/
    * editor for each cell.  If we didn't implement this method,
    * then the last column would contain text ("true"/"false"),
    * rather than a check box.
    */
    @Override
    public Class getColumnClass(int col)
    {
        Object obj = getValueAt(0, col);
        return (obj == null) ? Object.class : obj.getClass();
    }

    /*
    * Don't need to implement this method unless your table's
    * editable.
    */
    @Override
    public boolean isCellEditable(int row, int col)
    {
        // Note that the data/cell address is constant,
        // no matter where the cell appears onscreen.
        return (col > 0);
    }

    @Override
    public void setValueAt(Object value, int row, int col)
    {
        if (col == 2)
        {
            if ( ( (String)value ).isEmpty() )
                return;

            if ( ( (String)classesData[row][2] ).equals(LayoutClasses.NO_CLASS) )
                JOptionPane.showMessageDialog(layoutClassesTable, "'No Class' Cannot Be Edited (Immutable Class Name)!", "Note: 'No Class' Non-Editable", JOptionPane.INFORMATION_MESSAGE);
            else
            {
                classesData[row][col] = value;
                layoutClassesTable.updateClassData();
            }
        }
        else
        {
            classesData[row][col] = value;
            layoutClassesTable.updateClassData();
        }
    }


}