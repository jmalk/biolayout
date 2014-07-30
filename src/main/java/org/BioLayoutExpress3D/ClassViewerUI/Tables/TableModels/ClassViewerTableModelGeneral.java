package org.BioLayoutExpress3D.ClassViewerUI.Tables.TableModels;

import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import org.BioLayoutExpress3D.ClassViewerUI.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.Graph.GraphElements.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* @author Full refactoring by Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public final class ClassViewerTableModelGeneral extends AbstractTableModel
{
    /**
    *  Serial version UID variable for the ClassViewerTableModelGeneral class.
    */
    public static final long serialVersionUID = 111222333444555790L;

    public static final String[] ORIGINAL_COLUMN_NAMES = { "Selected", "Name", "Connections"};

    private int originalNumberOfColumns = ORIGINAL_COLUMN_NAMES.length;
    private String[] columnNames = ORIGINAL_COLUMN_NAMES;
    private Object[][] data = null;

    private LayoutFrame layoutFrame = null;
    private ClassViewerFrame classViewerFrame = null;

    public ClassViewerTableModelGeneral(LayoutFrame layoutFrame, ClassViewerFrame classViewerFrame)
    {
        this.layoutFrame = layoutFrame;
        this.classViewerFrame = classViewerFrame;
    }

    @Override
    public int getColumnCount()
    {
        return columnNames.length;
    }

    @Override
    public int getRowCount()
    {
        return (data != null) ? data.length : 0;
    }

    @Override
    public String getColumnName(int col)
    {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int row, int col)
    {
        if (row >= getRowCount() || col >= getColumnCount())
        {
            return null;
        }

        return data[row][col];
    }

    @Override
    public Class getColumnClass(int col)
    {
        Object obj = getValueAt(0, col);
        return (obj == null) ? Object.class : obj.getClass();
    }

    /*
    * Don't need to implement this method unless your table is editable.
    */
    @Override
    public boolean isCellEditable(int row, int col)
    {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        return ( (col == 0) || (col == 1) );
    }

    @Override
    public void setValueAt(Object value, int row, int col)
    {
        GraphNode graphNode = (GraphNode)data[row][data[0].length - 1]; // retrieve GraphNode from last column in table

        if (DEBUG_BUILD) println(graphNode.toString() + " " + (String)data[row][1]);

        switch (col)
        {
            case 0:

                if ( !( (Boolean)value ).booleanValue() )
                {
                    layoutFrame.getClassViewerFrame().setUpdateResetSelectDeselectAllButton(false);
                    layoutFrame.getGraph().getSelectionManager().removeNodeFromSelected(graphNode, false, true, true);
                    layoutFrame.getGraph().updateSelectedNodesDisplayList();
                    layoutFrame.getClassViewerFrame().setUpdateResetSelectDeselectAllButton(true);
                }
                else
                {
                    layoutFrame.getClassViewerFrame().setUpdateResetSelectDeselectAllButton(false);
                    layoutFrame.getGraph().getSelectionManager().addNodeToSelectedUpdateExpressionGraphViewOnly(graphNode, false, true);
                    layoutFrame.getGraph().updateSelectedNodesDisplayList();
                    layoutFrame.getClassViewerFrame().setUpdateResetSelectDeselectAllButton(true);
                }

                break;

            case 1:

                if ( ( (String)value ).isEmpty() )
                    return;

                String nodeName = (String)value;
                // if not a graphml file, check for pairwise node name uniqueness
                if ( !layoutFrame.getNetworkRootContainer().getIsGraphml() )
                {
                    boolean isNodeNameUnique = true;
                    for ( GraphNode node : layoutFrame.getGraph().getGraphNodes() )
                    {
                        if ( node.getNodeName().equals(nodeName) && !node.equals(graphNode) )
                        {
                            isNodeNameUnique = false;
                            break;
                        }
                    }

                    if (isNodeNameUnique)
                        layoutFrame.getNetworkRootContainer().setNodeName(graphNode, nodeName);
                    else
                    {
                        JOptionPane.showMessageDialog(classViewerFrame, "Edited Node Name Pre-Exists In The Current Graph!\nNow Resetting Node Name To Previous One.", "Note: Node Name Pre-Exists In Graph", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                }
                else // if a graphml file, pairwise uniqueness check is not needed as a unique node-key-to-name scenario is used within graphml files
                    layoutFrame.getNetworkRootContainer().setNodeName(graphNode, nodeName);

                break;
        }

        data[row][col] = value;
    }

    public void setSelectedRows(ArrayList<Integer> rows)
    {
        for (int row = 0; row < getRowCount(); row++)
        {
            data[row][0] = false;
        }

        HashSet<GraphNode> graphNodes = new HashSet<GraphNode>();
        for (int row : rows)
        {
            graphNodes.add( (GraphNode)data[row][data[0].length - 1] ); // retrieve GraphNode from last column in table
            data[row][0] = true;
        }

        layoutFrame.getClassViewerFrame().setUpdateResetSelectDeselectAllButton(false);

        layoutFrame.getGraph().getSelectionManager().clearAllSelection();
        layoutFrame.getGraph().getSelectionManager().addNodeToSelectedUpdateExpressionGraphViewOnly(graphNodes, false, true);
        layoutFrame.getGraph().updateSelectedNodesDisplayList();

        layoutFrame.getClassViewerFrame().setUpdateResetSelectDeselectAllButton(true);
    }

    public void setSelectedAllRows(boolean isSelected)
    {
        HashSet<GraphNode> graphNodes = new HashSet<GraphNode>();
        for (int i = 0; i < getRowCount(); i++)
        {
            graphNodes.add( (GraphNode)data[i][data[0].length - 1] ); // retrieve GraphNode from last column in table
            data[i][0] = isSelected;
        }

        layoutFrame.getClassViewerFrame().setUpdateResetSelectDeselectAllButton(false);

        if (!isSelected)
            layoutFrame.getGraph().getSelectionManager().removeNodeFromSelected(graphNodes, false, true, true);
        else
            layoutFrame.getGraph().getSelectionManager().addNodeToSelectedUpdateExpressionGraphViewOnly(graphNodes, false, true);
        layoutFrame.getGraph().updateSelectedNodesDisplayList();

        fireTableDataChanged();
        layoutFrame.getClassViewerFrame().setUpdateResetSelectDeselectAllButton(true);
    }

    public void proccessSelected(boolean allClasses)
    {
        proccessSelected(allClasses, null);
    }

    public void clear()
    {
        data = new Object[0][0];
    }

    public void proccessSelected(boolean allClasses, Object[][] hideColumnsData)
    {
        HashSet<GraphNode> selectedNodes = layoutFrame.getGraph().getSelectionManager().getSelectedNodes();
        int selectedSize = selectedNodes.size();
        if (selectedSize == 0)
        {
            clear();
            return;
        }

        int howManyColumnsToHide = 0;
        ArrayList<Integer> allHiddenColumnIndices = null;
        if (hideColumnsData != null)
        {
            allHiddenColumnIndices = new ArrayList<Integer>();
            for (int i = 0; i < hideColumnsData.length; i++)
            {
                if ( !( (Boolean)hideColumnsData[i][1] ).booleanValue() )
                {
                    howManyColumnsToHide++;
                    allHiddenColumnIndices.add(i);
                }
            }
        }

        int numberOfClassColumns = (allClasses) ? layoutFrame.getNetworkRootContainer().getLayoutClassSetsManager().getClassSetNames().size() : 1;
        int numberOfColumns = originalNumberOfColumns + numberOfClassColumns - howManyColumnsToHide;
        String[] newColumnNames = new String[numberOfColumns];

        int columnIndex = 0;
        int columnsPruned = 0;
        for (columnIndex = 0; columnIndex < originalNumberOfColumns; columnIndex++)
        {
            if ( (howManyColumnsToHide == 0) || !allHiddenColumnIndices.contains(columnIndex - 2) ) // first check with || for speed-up by avoiding the contains() method
                newColumnNames[columnIndex - columnsPruned] = ORIGINAL_COLUMN_NAMES[columnIndex];
            else
                columnsPruned++;
        }

        if (allClasses)
        {
            ArrayList<LayoutClasses> classSets = layoutFrame.getNetworkRootContainer().getLayoutClassSetsManager().getClassSetNames();
            for (LayoutClasses lc : classSets)
            {
                if ( (howManyColumnsToHide == 0) || !allHiddenColumnIndices.contains(columnIndex - 2) ) // first check with || for speed-up by avoiding the contains() method
                    newColumnNames[columnIndex - columnsPruned] = lc.getClassSetName();
                else
                    columnsPruned++;

                columnIndex++;
            }
        }
        else
        {
            if ( (howManyColumnsToHide == 0) || !allHiddenColumnIndices.contains(columnIndex - 2) ) // first check with || for speed-up by avoiding the contains() method
                newColumnNames[columnIndex - columnsPruned] = layoutFrame.getNetworkRootContainer().getLayoutClassSetsManager().getCurrentClassSetAllClasses().getClassSetName();
            else
                columnsPruned++;
        }

        columnNames = newColumnNames;
        if (DEBUG_BUILD)
            for (String columnName : columnNames)
                println(columnIndex + ": " + columnName);

        numberOfColumns += 1; // +1 column for hidden GraphNode object to be stored
        data = new Object[selectedSize][numberOfColumns];

        columnIndex = 0;
        int rowIndex = 0;
        for (GraphNode node : selectedNodes)
        {
            columnsPruned = 0;
            // Selected
            data[rowIndex][0] = Boolean.TRUE;

            // Node name
            data[rowIndex][1] = layoutFrame.getNetworkRootContainer().getNodeName( node.getNodeName() );

            int incomingEdges = Integer.valueOf(node.getNodeChildren().size());
            int outgoingEdges = Integer.valueOf(node.getNodeParents().size());
            int nodeDegree = incomingEdges + outgoingEdges;

            // Edge degree
            if ((howManyColumnsToHide == 0) || !allHiddenColumnIndices.contains(0))  // index columnIndex - 2 = 2 - 2
            {
                data[rowIndex][2 - columnsPruned] = nodeDegree;
            }
            else
            {
                columnsPruned++;
            }

            // Class
            columnIndex = 3;
            if (allClasses)
            {
                ArrayList<LayoutClasses> classSets = layoutFrame.getNetworkRootContainer().getLayoutClassSetsManager().getClassSetNames();
                for (LayoutClasses lc : classSets)
                {
                    if ( (howManyColumnsToHide == 0) || !allHiddenColumnIndices.contains(columnIndex - 2) ) // first check with || for speed-up by avoiding the contains() method
                            data[rowIndex][columnIndex - columnsPruned] = lc.getVertexClass( node.getVertex() );
                    else
                        columnsPruned++;

                    columnIndex++;
                }
            }
            else
            {
                if ( (howManyColumnsToHide == 0) || !allHiddenColumnIndices.contains(columnIndex - 2) ) // first check with || for speed-up by avoiding the contains() method
                        data[rowIndex][columnIndex - columnsPruned] = layoutFrame.getNetworkRootContainer().getLayoutClassSetsManager().getCurrentClassSetAllClasses().getVertexClass( node.getVertex() );
                else
                    columnsPruned++;

                columnIndex++;
            }

            data[rowIndex][columnIndex - columnsPruned] = node;
            rowIndex++;
        }
    }

    public boolean findNonVertexClassColumnNamesInOriginalColumnNameArray(String currentColumnName)
    {
        for (String originalColumnName : ORIGINAL_COLUMN_NAMES)
            if( currentColumnName.equals(originalColumnName) )
                return true;

        return false;
    }

    public String[] getColumnNames()
    {
        return columnNames;
    }


}