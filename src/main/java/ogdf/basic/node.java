package ogdf.basic;

import java.util.*;

public class node
{
    List<edge> m_edges;
    int m_id;
    Graph m_pGraph;

    public node(Graph pGraph, int id)
    {
        m_id = id;
        m_pGraph = pGraph;
        m_edges = new ArrayList<edge>();
    }

    public Graph graphOf()
    {
        return m_pGraph;
    }

    public int index()
    {
        return m_id;
    }

    public List<edge> adjEdges()
    {
        return m_edges;
    }
}
