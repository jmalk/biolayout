package ogdf.basic;

public class edge
{
    node m_src; //!< The source node of the edge.
    node m_tgt; //!< The target node of the edge.
    int m_id; // The (unique) index of the node.

    public edge(node src, node tgt, int id)
    {
        m_src = src;
        m_tgt = tgt;
        m_id = id;
    }

    public Graph graphOf()
    {
        return m_src.graphOf();
    }

    public int index()
    {
        return m_id;
    }

    public node source()
    {
        return m_src;
    }

    public node target()
    {
        return m_tgt;
    }

    public boolean isSelfLoop()
    {
        return m_src == m_tgt;
    }

    public node opposite(node n)
    {
        return (n == m_src) ? m_tgt : m_src;
    }
}
