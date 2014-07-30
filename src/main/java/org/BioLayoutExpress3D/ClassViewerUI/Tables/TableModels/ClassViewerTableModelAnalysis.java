package org.BioLayoutExpress3D.ClassViewerUI.Tables.TableModels;

import java.util.*;
import javax.swing.table.*;
import org.BioLayoutExpress3D.Analysis.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.Graph.GraphElements.*;
import org.BioLayoutExpress3D.Network.*;

/**
*
* @author Markus Brosch (mb8[at]sanger[dot]ac[dot]uk)
* @author Full refactoring by Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public final class ClassViewerTableModelAnalysis extends AbstractTableModel
{
    /**
    *  Serial version UID variable for the ClassViewerTableModelAnalysis class.
    */
    public static final long serialVersionUID = 111222333444555788L;

    public static final String[] COLUMN_NAMES = { "Annotation Type", "KL Entropy" };

    private String[] annotationType = null;
    private Double[] relativeEntropy = null;

    private LayoutFrame layoutFrame = null;

    public ClassViewerTableModelAnalysis(LayoutFrame layoutFrame)
    {
        this.layoutFrame = layoutFrame;
    }

    @Override
    public int getColumnCount()
    {
        return COLUMN_NAMES.length;
    }

    @Override
    public int getRowCount()
    {
        return (annotationType != null) ? annotationType.length : 0;
    }

    @Override
    public String getColumnName(int col)
    {
        return COLUMN_NAMES[col];
    }

    @Override
    public Object getValueAt(int row, int col)
    {
        if (getRowCount() == 0)
            return null;
        else
        {
            if (col == 0)
                return annotationType[row];
            if (col == 1)
                return relativeEntropy[row];
            else
                throw new IllegalArgumentException("Column " + col + " doesn't exist!");
        }
    }

    @Override
    public Class getColumnClass(int col)
    {
        Object obj = getValueAt(0, col);
        return (obj == null) ? Object.class : obj.getClass();
    }

    public HashSet<String> proccessSelected()
    {
        HashSet<GraphNode> selectedNodes = layoutFrame.getGraph().getSelectionManager().getSelectedNodes();

        if ( selectedNodes.isEmpty() )
        {
            annotationType = new String[0];
            relativeEntropy = new Double[0];

            return new HashSet<String>();
        }

        HashSet<String> genes = new HashSet<String>();
        for (GraphNode graphNode : selectedNodes)
            genes.add( graphNode.getNodeName() );

        NetworkContainer network = layoutFrame.getNetworkRootContainer();
        RelativeEntropyCalc rec = new RelativeEntropyCalc(network);

        Map<String, Double>  entropies = rec.relEntropy4Selection(genes);
        Set<String> keys = entropies.keySet();

        annotationType = new String[ keys.size() ];
        relativeEntropy = new Double[ keys.size() ];

        int i = 0;
        for (String key : keys)
        {
            Double entropy = entropies.get(key);
            // Double fisher = entropies.get(key);

            annotationType[i] = key;
            relativeEntropy[i] = entropy;
            i++;
        }

        return genes;
    }


}