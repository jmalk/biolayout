package org.BioLayoutExpress3D.ClassViewerUI.Tables.TableModels;

import javax.swing.table.*;
import org.BioLayoutExpress3D.ClassViewerUI.*;
import org.BioLayoutExpress3D.StaticLibraries.*;

/**
*
* The ClassViewerHideColumnsTableModel class provides all the table functionality for the ClassViewerHideColumns JDialog class.
*
* @see org.BioLayoutExpress3D.ClassViewerUI.Dialogs.ClassViewerHideColumnsDialog
* @author Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*/

public final class ClassViewerHideColumnsTableModel extends AbstractTableModel
{
    /**
    *  Serial version UID variable for the ClassViewerHideColumnsTableModel class.
    */
    public static final long serialVersionUID = 111222333444555695L;

    public static final String[] COLUMN_NAMES = { "Column Name", "Hide/Unhide" };

    private Object[][] data = null;
    private String[] prevAllColumnNames = null;
    private Boolean[] prevAllColumnBooleanValues = null;
    private int numberOfColumns = 0;

    private ClassViewerFrame classViewerFrame = null;

    public ClassViewerHideColumnsTableModel(ClassViewerFrame classViewerFrame)
    {
        this.classViewerFrame = classViewerFrame;
    }

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

        for (int i = 0; i < numberOfColumns; i++)
            prevAllColumnBooleanValues[i] = (Boolean)data[i][1];

        classViewerFrame.populateClassViewer(data);
    }

    public void updateClassViewerHideColumnsTable(ClassViewerFrame classViewerFrame, boolean enableHideColumnsAndExportButtons, boolean updateExpressionGraphViewOnly, boolean notUpdateTitleBar)
    {
        if (enableHideColumnsAndExportButtons)
        {
            String[] allColumnNames = classViewerFrame.getGeneralTableColumnNames();
            if ( !Utils.areArraysEqual(allColumnNames, prevAllColumnNames) ) // to re-initiate column names only when needed (when different)
            {
                numberOfColumns = allColumnNames.length - 2; // first two columns always on
                data = new Object[numberOfColumns][2];
                prevAllColumnBooleanValues = new Boolean[numberOfColumns];
                for (int i = 0; i < numberOfColumns; i++)
                {
                    data[i][0] = allColumnNames[i + 2];
                    data[i][1] = prevAllColumnBooleanValues[i] = Boolean.TRUE;
                }

                prevAllColumnNames = allColumnNames;
            }
            else
            {
                data = new Object[numberOfColumns][2];
                for (int i = 0; i < numberOfColumns; i++)
                {
                    data[i][0] = prevAllColumnNames[i + 2];
                    data[i][1] = prevAllColumnBooleanValues[i];
                }

                classViewerFrame.populateClassViewer(data, updateExpressionGraphViewOnly, notUpdateTitleBar, false);
            }
        }
        else
            data = new Object[0][0];

        fireTableDataChanged();
    }

    public void setSelectedAllColumns(boolean isSelected)
    {
        for (int i = 0; i < numberOfColumns; i++)
            data[i][1] = prevAllColumnBooleanValues[i] = isSelected;

        fireTableDataChanged();
        classViewerFrame.populateClassViewer(data);
    }


}