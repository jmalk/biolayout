package ogdf.basic;

import java.util.*;

public class Graph
{
    List<node> m_nodes;
    int m_nodeIdCount;

    List<edge> m_edges;
    int m_edgeIdCount;

    public Graph()
    {
        m_nodes = new ArrayList<node>();
        m_nodeIdCount = 0;

        m_edges = new ArrayList<edge>();
        m_edgeIdCount = 0;
    }

    public int nodeArrayTableSize()
    {
        // This is only used to presize an array
        return 0;
    }

    public int numberOfNodes()
    {
        return m_nodes.size();
    }

    public int numberOfEdges()
    {
        return m_edges.size();
    }

    public node firstNode()
    {
        return m_nodes.get(0);
    }

    public Iterator<node> nodesIterator()
    {
        return m_nodes.listIterator();
    }

    public Iterator<edge> edgesIterator()
    {
        return m_edges.listIterator();
    }

    public void clear()
    {
        m_edges.clear();
        m_nodes.clear();
    }

    public node newNode()
    {
        node v = new node(this, m_nodeIdCount++);
        m_nodes.add(v);

        return v;
    }

    public edge newEdge(node v, node w)
    {
        assert v != null && w != null;
        assert v.graphOf() == this && w.graphOf() == this;

        edge e = new edge(v, w, m_edgeIdCount++);
        m_edges.add(e);

        v.adjEdges().add(e);
        w.adjEdges().add(e);

        return e;
    }

    public void delEdge(edge e)
    {
        for (node n : m_nodes)
        {
            if (n.adjEdges().contains(e))
                n.adjEdges().remove(e);
        }

        m_edges.remove(e);
    }

    public List<edge> allEdges()
    {
        return m_edges;
    }
}