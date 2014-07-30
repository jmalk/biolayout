package org.BioLayoutExpress3D.Network;

import java.util.Collection;
import org.BioLayoutExpress3D.Network.NetworkContainer;

/**
 * Embarrassingly simple layout algorithm that just puts the nodes in a circle
 * 
 * @author Tim Angus <tim.angus@roslin.ed.ac.uk>
 */
public class CircleLayout
{
    public CircleLayout()
    {
    }

    public void layout(NetworkContainer nc)
    {
        Collection<Vertex> vertices = nc.getVertices();
        int numVertices = vertices.size();
        double angularSeparation = (2.0 * Math.PI) / (double)numVertices;
        final double BASE_SCALE = 20.0;
        double radius = (double)numVertices * BASE_SCALE / (2.0 * Math.PI);
        double angle = 0.0;
        for (Vertex vertex : vertices)
        {
            float x = (float)(Math.sin(angle) * radius);
            float y = (float)(Math.cos(angle) * radius);
            vertex.setVertexLocation(x, y, 0.0f);
            vertex.setVertexSize(3.0f);
            angle += angularSeparation;
        }
    }
}
