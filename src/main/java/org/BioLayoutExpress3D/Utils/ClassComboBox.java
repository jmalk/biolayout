package org.BioLayoutExpress3D.Utils;

import java.awt.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.Network.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* User: icases
* Date: 04-sep-02
*
* @author Full refactoring by Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public final class ClassComboBox extends JComboBox<Object>
{
    /**
    *  Serial version UID variable for the ClassComboBox class.
    */
    public static final long serialVersionUID = 111222333444555776L;

    private boolean newOption = false;
    private boolean multiOption = false;

    private LayoutClasses layoutClasses = null;
    private ClassRenderer classRenderer = null;
    private ArrayList<VertexClass> sortedVertexClasses = null;

    public ClassComboBox(LayoutClasses layoutClasses, boolean newOption, boolean multiOption)
    {
        this.layoutClasses = layoutClasses;
        this.newOption = newOption;
        this.multiOption = multiOption;

        if (multiOption)
            newOption = true;

        classRenderer = new ClassRenderer();
        this.setRenderer(classRenderer);
        this.setPreferredSize( new Dimension(200, 22) );

        updateClasses(layoutClasses);
    }

    public ClassRenderer getClassRenderer()
    {
        return classRenderer;
    }

    public void updateClasses(LayoutClasses layoutClasses)
    {
        this.layoutClasses = layoutClasses;
        this.removeAllItems();

        if (multiOption)
        {
            this.addItem( Integer.valueOf(0) );
            // addItem( new JLabel("Multiple") );
        }

        if (newOption)
        {
            this.addItem( Integer.valueOf(-1) );
            // addItem( new JLabel("Multiple") );
        }

        sortedVertexClasses = new ArrayList<VertexClass>( layoutClasses.getClassesMap().values() );
        Collections.sort(sortedVertexClasses);

        for (VertexClass sortedVertexClass : sortedVertexClasses)
        {
            if (DEBUG_BUILD) println( "Adding: " + sortedVertexClass.getName() );
            this.addItem(sortedVertexClass);
        }
    }

    public void setMultiOption(boolean multiOption)
    {
        this.multiOption = multiOption;
        if (multiOption)
            newOption = true;

        updateClasses(layoutClasses);
    }

    public ArrayList<VertexClass> getSortedVertexClasses()
    {
        return sortedVertexClasses;
    }

    public static class ClassRenderer extends JLabel implements ListCellRenderer<Object>, TableCellRenderer
    {
        /**
        *  Serial version UID variable for the ClassRenderer class.
        */
        public static final long serialVersionUID = 111222333444555777L;

        private ClassRenderer()
        {
            setOpaque(true);
            setHorizontalAlignment(LEFT);
            setVerticalAlignment(CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
        {
            return getRenderer(isSelected, value, list.getBackground(), list.getForeground(), list.getSelectionBackground(), list.getSelectionForeground());
        }

        private Component getRenderer(boolean isSelected, Object value, Color background, Color foreground, Color selectionBackground, Color selectionForeground)
        {
            if (isSelected)
            {
                setBackground(selectionBackground);
                setForeground(selectionForeground);
            }
            else
            {
                setBackground(background);
                setForeground(foreground);
            }

            if (value != null)
            {
                if (value instanceof VertexClass)
                {
                    VertexClass vertexClass = (VertexClass)value;
                    this.setText( "  " +  vertexClass.getName() );

                    if (vertexClass.getClassID() > 0)
                    {
                        this.setIcon( getClassIcon(vertexClass) );
                        this.setIconTextGap(3);
                    }
                    else
                    {
                        this.setIcon(null);
                    }
                }
                else
                {
                    if ( value.equals( Integer.valueOf(0) ) )
                        this.setText("  ");

                    if ( value.equals( Integer.valueOf(-1) ) )
                        this.setText("  New Class");

                    this.setIcon(null);
                }
            }

            return this;
        }

        private ImageIcon getClassIcon(VertexClass vertexClass)
        {
            BufferedImage image = new BufferedImage(12, 12, BufferedImage.TYPE_INT_ARGB);
            Graphics g = image.getGraphics();
            g.setColor( vertexClass.getColor() );
            g.fillRect(1, 1, 10, 10);
            g.setColor(Color.BLACK);
            g.drawRect(1, 1, 9, 9);

            return new ImageIcon(image);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            if (value instanceof VertexClass)
            {
                VertexClass vertexClass = (VertexClass)value;
                setToolTipText( ( ( !vertexClass.getName().equals(LayoutClasses.NO_CLASS) ) ? "Class Name: " + vertexClass.getName() : LayoutClasses.NO_CLASS ) );
            }

            return getRenderer( isSelected, value, table.getBackground(), table.getForeground(), table.getSelectionBackground(), table.getSelectionForeground() );
        }


    }


}