package ogdf.basic;

import java.util.*;
import ogdf.basic.*;

public class SimpleGraphAlg
{
    public static int connectedComponents(Graph G, NodeArray<Integer> component)
    {
        int nComponent = 0;
        component.fill(-1);

        Stack<node> S = new Stack<node>();

        for (Iterator<node> iter = G.nodesIterator(); iter.hasNext();)
        {
            node v = iter.next();
            if (component.get(v) != -1)
            {
                continue;
            }

            S.push(v);
            component.set(v, nComponent);

            while (!S.empty())
            {
                node w = S.pop();
                for (edge e : w.adjEdges())
                {
                    node x = e.opposite(w);
                    if (component.get(x) == -1)
                    {
                        component.set(x, nComponent);
                        S.push(x);
                    }
                }
            }

            ++nComponent;
        }

        return nComponent;
    }

    static List<edge> parallelFreeSortUndirected(Graph G,
            EdgeArray<Integer> minIndex,
            EdgeArray<Integer> maxIndex)
    {
        List<edge> edges = G.allEdges();

        for (Iterator<edge> iter = G.edgesIterator(); iter.hasNext();)
        {
            edge e = iter.next();
            int srcIndex = e.source().index(), tgtIndex = e.target().index();
            if (srcIndex <= tgtIndex)
            {
                minIndex.set(e, srcIndex);
                maxIndex.set(e, tgtIndex);
            }
            else
            {
                minIndex.set(e, tgtIndex);
                maxIndex.set(e, srcIndex);
            }
        }

        final EdgeArray<Integer> finalMinIndex = minIndex;
        final EdgeArray<Integer> finalMaxIndex = maxIndex;

        //FIXME needs reversing?
        Collections.sort(edges, new java.util.Comparator<edge>()
        {
            @Override
            public int compare(edge a, edge b)
            {
                return finalMinIndex.get(a) - finalMinIndex.get(b);
            }
        });

        Collections.sort(edges, new java.util.Comparator<edge>()
        {
            @Override
            public int compare(edge a, edge b)
            {
                return finalMaxIndex.get(a) - finalMaxIndex.get(b);
            }
        });

        return edges;
    }


    static void makeParallelFreeUndirected(Graph G)
    {
        if (G.numberOfEdges() <= 1)
        {
            return;
        }

        EdgeArray<Integer> minIndex = new EdgeArray<Integer>(G, Factory.INTEGER);
        EdgeArray<Integer> maxIndex = new EdgeArray<Integer>(G, Factory.INTEGER);
        List<edge> edges = parallelFreeSortUndirected(G, minIndex, maxIndex);
        List<edge> edgesToDelete = new ArrayList<edge>();

        ListIterator<edge> it = edges.listIterator();
        edge ePrev = it.next();
        boolean bAppend = true;
        while (it.hasNext())
        {
            edge e = it.next();
            if (minIndex.get(ePrev) == minIndex.get(e) && maxIndex.get(ePrev) == maxIndex.get(e))
            {
                if (bAppend)
                {
                    bAppend = false;
                }
            }
            else
            {
                ePrev = e;
                bAppend = true;
            }
        }

        for (edge e : edgesToDelete)
        {
            G.delEdge(e);
        }
    }


    static void makeLoopFree(Graph G)
    {
        List<edge> edgesToDelete = new ArrayList<edge>();
        for (Iterator<edge> iter = G.edgesIterator(); iter.hasNext();)
        {
            edge e = iter.next();
            if (e.isSelfLoop())
                edgesToDelete.add(e);
        }

        for (edge e : edgesToDelete)
        {
            G.delEdge(e);
        }
    }

    public static void makeSimpleUndirected(Graph G)
    {
        makeLoopFree(G);
        makeParallelFreeUndirected(G);
    }
}
