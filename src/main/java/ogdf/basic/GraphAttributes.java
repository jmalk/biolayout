package ogdf.basic;

import java.util.*;
import org.BioLayoutExpress3D.Network.*;

public class GraphAttributes
{
    Graph graph;
    NodeArray<DPoint> m_position;
    NodeArray<Double> m_width;
    NodeArray<Double> m_height;
    Map<Vertex,node> m;

    public GraphAttributes(Graph graph)
    {
        this.graph = graph;
        m_position = new NodeArray<DPoint>(graph, Factory.DPOINT);
        m_width = new NodeArray<Double>(graph, Factory.DOUBLE);
        m_height = new NodeArray<Double>(graph, Factory.DOUBLE);
        m = null;
    }

    public GraphAttributes(NetworkContainer nc, PointFactory.Dimensions dimensions)
    {
        this(new Graph());
        PointFactory.INSTANCE.setDimensions(dimensions);

        m = new HashMap<Vertex,node>();

        for (Vertex vertex : nc.getVertices())
        {
            node n = graph.newNode();
            DPoint p;

            switch (dimensions)
            {
                default:
                case _2:
                    p = new DPoint2(vertex.getX(), vertex.getY());
                    break;

                case _3:
                    p = new DPoint3(vertex.getX(), vertex.getY(), vertex.getZ());
                    break;
            }

            setPosition(n, p);
            setWidth(n, vertex.getVertexSize());
            setHeight(n, vertex.getVertexSize());
            m.put(vertex, n);
        }

        for (Edge edge : nc.getEdges())
        {
            node v = (node)m.get(edge.getFirstVertex());
            node w = (node)m.get(edge.getSecondVertex());

            edge e = graph.newEdge(v, w);
        }
    }

    public void applyTo(NetworkContainer nc)
    {
        for (Vertex vertex : nc.getVertices())
        {
            node n = m.get(vertex);
            DPoint p = position(n);

            vertex.setVertexLocation((float)p.getX(), (float)p.getY(), (float)p.getZ());
        }
    }

    public Graph constGraph()
    {
        return graph;
    }

    public DPoint position(node v)
    {
        return m_position.get(v);
    }

    public final void setPosition(node v, DPoint position)
    {
        m_position.set(v, position);
    }

    public double width(node v)
    {
        return m_width.get(v);
    }

    public final void setWidth(node v, double value)
    {
        m_width.set(v, value);
    }

    public double height(node v)
    {
        return m_height.get(v);
    }

    public final void setHeight(node v, double value)
    {
        m_height.set(v, value);
    }
}
