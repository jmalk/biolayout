package org.BioLayoutExpress3D.CoreUI.Tables;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.CoreUI.Tables.TableModels.*;
import org.BioLayoutExpress3D.Network.*;
import org.BioLayoutExpress3D.Utils.*;
import static org.BioLayoutExpress3D.CoreUI.Tables.LayoutClassesTable.TableSeparatorTypes.*;

/**
*
* @author Full refactoring by Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public final class LayoutClassesTable extends JPanel
{
    /**
    *  Serial version UID variable for the LayoutClassesTable class.
    */
    public static final long serialVersionUID = 111222333444555672L;

    public static enum TableSeparatorTypes { CLASS_COLOR_COLUMN, CLASS_DESCRIPTION_COLUMN }

    private LayoutClasses layoutClasses = null;
    private Component component = null;
    private Object[][] classesData = null;
    private int totalClasses = 0;
    private boolean updateNodesDisplayList = false;

    /**
    *  LayoutClassesTable listener to be used as a callback when changing a class color in the table.
    */
    private LayoutClassesTableListener listener = null;

    public LayoutClassesTable(LayoutClasses layoutClasses, Component component)
    {
        super(true);

        this.layoutClasses = layoutClasses;
        this.component = component;

        totalClasses = 0;
        classesData = new Object[layoutClasses.getTotalClasses() + 1][3];

        for ( VertexClass vertexClass : layoutClasses.getAllVertexClasses() )
        {
            classesData[totalClasses][0] = vertexClass.getClassID();
            classesData[totalClasses][1] = vertexClass.getColor();
            classesData[totalClasses][2] = vertexClass.getName();
            totalClasses++;
        }

        LayoutClassesTableModel layoutClassesTableModel = new LayoutClassesTableModel(this, classesData);
        LayoutClassTable table = new LayoutClassTable(layoutClassesTableModel, LayoutClassesTableModel.COLUMN_NAMES);
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(layoutClassesTableModel);
        table.setRowSorter(sorter); // provide a sorting mechanism to the table
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        sorter.setComparator( CLASS_COLOR_COLUMN.ordinal() + 1, new LayoutClassTable.ColorSorting() );
        sorter.setComparator( CLASS_DESCRIPTION_COLUMN.ordinal() + 1, new LayoutClassTable.VertexClassNameSorting() );
        table.sortTableByColumn(CLASS_DESCRIPTION_COLUMN.ordinal() + 1, sorter);

        this.setLayout( new BorderLayout() );
        table.setPreferredScrollableViewportSize( new Dimension(845, 495) );
        this.setLayout( new FlowLayout() );
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        //Set up renderer and editor for the favorite Class Color column.
        setUpColorRenderer(table);
        setUpColorEditor(table);

        //Set up real input validation for integer data.
        setUpIntegerEditor(table);

        //Add the scroll pane to this window.
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(scrollPane);
    }

    private void setUpColorRenderer(JTable table)
    {
        table.setDefaultRenderer( Color.class, new LayoutClassTable.ColorRenderer(true) );
    }

    // Set up the editor for the Color cells.
    private void setUpColorEditor(JTable table)
    {
        // First, set up the button that brings up the dialog.
        final JButton button = new JButton("")
        {
            /**
            *  Serial version UID variable for the JButton class.
            */
            public static final long serialVersionUID = 111222333444555674L;

            @Override
            public void setText(String s)
            {
                //Button never shows text -- only color.
            }
        };

        button.setBackground(Color.WHITE);
        button.setBorderPainted(false);
        button.setMargin(new Insets(0, 0, 0, 0) );

        //Now create an editor to encapsulate the button, and
        //set it up as the editor for all Color cells.
        final LayoutClassTable.ColorEditor colorEditor = new LayoutClassTable.ColorEditor(button);
        table.setDefaultEditor(Color.class, colorEditor);

        //Set up the dialog that the button brings up.
        final JColorChooser colorChooser = new JColorChooser();
        ActionListener okListener = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if ( updateNodesDisplayList = !colorEditor.currentColor.equals( colorChooser.getColor() ) )
                {
                    colorEditor.currentColor = colorChooser.getColor();
                    listener.classColorHasChanged();
                }
            }
        };

        final JDialog dialog = JColorChooser.createDialog(component, "Pick a Color", true, colorChooser, okListener, null);

        //Here's the code that brings up the dialog.
        button.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                button.setBackground(colorEditor.currentColor);
                colorChooser.setColor(colorEditor.currentColor);
                //Without the following line, the dialog comes up
                //in the middle of the screen.
                //dialog.setLocationRelativeTo(button);
                dialog.setVisible(true);
            }
        } );
    }

    private void setUpIntegerEditor(JTable table)
    {
        //Set up the editor for the integer cells.
        final WholeNumberField integerField = new WholeNumberField(0, 5);
        integerField.setHorizontalAlignment(WholeNumberField.RIGHT);

        DefaultCellEditor integerEditor = new DefaultCellEditor(integerField)
        {
            /**
            *  Serial version UID variable for the DefaultCellEditor class.
            */
            public static final long serialVersionUID = 111222333444555676L;

            //Override DefaultCellEditor's getCellEditorValue method
            //to return an Integer, not a String:
            @Override
            public Object getCellEditorValue()
            {
                return Integer.valueOf( integerField.getValue() );
            }
        };

        table.setDefaultEditor(Integer.class, integerEditor);
    }

    public boolean updateClassData()
    {
        for (int i = 0; i < totalClasses; i++)
        {
            int classIndex = (Integer)classesData[i][0];
            VertexClass vertexClass = layoutClasses.getClassByID(classIndex);
            if (vertexClass != null)
            {
                vertexClass.setColor( (Color)classesData[i][1] );
                vertexClass.setName( (String)classesData[i][2] );
            }
        }

        return updateNodesDisplayList;
    }

    /**
    *  Sets the LayoutClassesTableListener listener.
    */
    public void setListener(LayoutClassesTableListener listener)
    {
        this.listener = listener;
    }

    /**
    *  Removes the LayoutClassesTableListener listener.
    */
    public void removeListener()
    {
        listener = null;
    }

    /**
    *  LayoutClassesTableListener interface, used as a callback design pattern for the BioLayout Express 3D framework.
    */
    public interface LayoutClassesTableListener
    {
        /**
        *  This method is called as a callback event when a class color has changed in the table.
        */
        public void classColorHasChanged();
    }


}