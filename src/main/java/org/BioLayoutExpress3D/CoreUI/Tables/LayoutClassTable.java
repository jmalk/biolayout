package org.BioLayoutExpress3D.CoreUI.Tables;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import org.BioLayoutExpress3D.Network.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
*  The LayoutClassTable class supports proper color sorting based on the color's hex value, initial sorting at a particular column of the table and full tooltip support for both columns and cells.
*  It is used it for the Graph Properties Classes tab and the Class Legend (GraphPropertiesDialog & LayoutClassLegend classes respectively).
*
* @author Thanos Theo, 2008-2009
* @version 3.0.0.0
*
*/

public final class LayoutClassTable extends JTable
{
    /**
    *  Serial version UID variable for the LayoutClassTable class.
    */
    public static final long serialVersionUID = 111222333444555600L;

    /**
    *  Variable to store the column names.
    */
    private String[] columnNames = null;

    /**
    *  The constructor of the LayoutClassTable class.
    */
    public LayoutClassTable(TableModel tableModel, String[] columnNames)
    {
        super(tableModel);

        this.columnNames = columnNames;
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
        Object cellData = getValueAt(rowIndex, colIndex);

        if (cellData instanceof Color)
        {
            Color color = (Color)cellData;
            return "RGB Color value: " + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue();
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
                int realIndex = columnModel.getColumn(index).getModelIndex();
                return columnNames[realIndex];
            }
        };
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
    static class VertexClassNameSorting implements Comparator<String>, Serializable // package access
    {

        /**
        *  Serial version UID variable for the VertexClassNameSorting class.
        */
        public static final long serialVersionUID = 111222333444555622L;

        @Override
        public int compare(String vertexClassName1, String vertexClassName2)
        {
            // push the NO_CLASS string at the end of a possible sorting
            return VertexClass.compare(vertexClassName1, vertexClassName2);
        }


    }

    /**
    *  Static inner class that implements a comparator for color sorting. To be used with the table.
    */
    public static class ColorSorting implements Comparator<Color>, Serializable
    {

        /**
        *  Serial version UID variable for the ColorSorting class.
        */
        public static final long serialVersionUID = 111222333444555621L;

        @Override
        public int compare(Color color1, Color color2)
        {
            // return Utils.getHexColor(color1).compareTo( Utils.getHexColor(color2) );
            // quicker way by directly comparing the integer color values instead of turning the color to a hex string color value
            return ( color1.getRGB() > color2.getRGB() ) ? 1 : ( ( color1.getRGB() < color2.getRGB() ) ? -1 : 0 );
        }


    }

    /**
    *  Static inner class that overrides the getTableCellRendererComponent() method. To be used with the table.
    */
    public static class ColorRenderer extends JLabel implements TableCellRenderer
    {
        /**
        *  Serial version UID variable for the ColorRenderer class.
        */
        public static final long serialVersionUID = 111222333444555673L;

        private boolean isBordered = true;

        private Border unselectedBorder = null;
        private Border selectedBorder = null;

        public ColorRenderer(boolean isBordered)
        {
            super();

            this.isBordered = isBordered;

            setOpaque(true); //Must do this for background to show up.
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            Color color = (Color)value;
            setBackground(color);
            setToolTipText( "RGB Color value: " + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() );

            if (isBordered)
            {
                if (isSelected)
                {
                    if (selectedBorder == null)
                        selectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5, table.getSelectionBackground());

                    setBorder(selectedBorder);
                }
                else
                {
                    if (unselectedBorder == null)
                        unselectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5, table.getBackground());

                    setBorder(unselectedBorder);
                }
            }

            return this;
        }


    }

    /**
    *  The editor button that brings up the dialog. We extend DefaultCellEditor for convenience, even though it mean we have to create a dummy
    *  check box.  Another approach would be to copy the implementation of TableCellEditor methods from the source code for DefaultCellEditor.
    */
    public static class ColorEditor extends DefaultCellEditor
    {

        /**
        *  Serial version UID variable for the ColorEditor class.
        */
        public static final long serialVersionUID = 111222333444555675L;

        Color currentColor = null;

        public ColorEditor(JButton button)
        {
            super( new JCheckBox() ); //Unfortunately, the constructor
            //expects a check box, combo box,
            //or text field.
            editorComponent = button;
            setClickCountToStart(1); //This is usually 1 or 2.

            //Must do this so that editing stops when appropriate.
            button.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    fireEditingStopped();
                }
            } );
        }

        @Override
        protected void fireEditingStopped()
        {
            super.fireEditingStopped();
        }

        @Override
        public Object getCellEditorValue()
        {
            return currentColor;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
        {
            try
            {
                ( (JButton)editorComponent ).setText( value.toString() );
            }
            catch (NullPointerException exc)
            {
                if (DEBUG_BUILD) println("Exception in ColorEditor.getTableCellEditorComponent():\n" + exc.getMessage());
            }

            currentColor = (Color)value;

            return editorComponent;
        }


    }


}