package org.BioLayoutExpress3D.Network;

import java.io.*;
import java.util.*;
import ogdf.basic.PointFactory;
import ogdf.basic.GraphAttributes;
import ogdf.energybased.FMMMLayout;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.CoreUI.Dialogs.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;
import org.BioLayoutExpress3D.Utils.Point3D;

/**
*
* @author Leon Goldovsky, full refactoring by Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*
*/

public final class NetworkRootContainer extends NetworkContainer
{
    private ArrayList<NetworkComponentContainer> componentCollection = null;
    private TilingLevelsContainer tilingLevelsContainer = null;

    public NetworkRootContainer(LayoutClassSetsManager layoutClassSetsManager, LayoutFrame layoutFrame)
    {
        super(layoutClassSetsManager, layoutFrame);

        componentCollection = new ArrayList<NetworkComponentContainer>();
        tilingLevelsContainer = new TilingLevelsContainer();
    }

    public void createNetworkComponentsContainer()
    {
        componentCollection.clear();
        tilingLevelsContainer.clear();

        // REMOVE SINGLETONS HERE FASTEST METHOD == IF DESIRED
        if (MINIMUM_COMPONENT_SIZE.get() > 1)
           removeSingletons();

        // NOW REMOVE CONNECTED COMPONENTS SMALLER THAN A SPECIFIED SIZE BUT NOT SINGLETONS
        findOrRemovePolygons( MINIMUM_COMPONENT_SIZE.get() );
    }

    private void findOrRemovePolygons(int size)
    {
        // This code partitions the graph into connected components, also removing those smaller than a certain threshold
        int counter = 0;
        Collection<Vertex> allVerticesCopy = new HashSet<Vertex>( getVertices() );
        HashSet<Vertex> vertexDone = new HashSet<Vertex>();
        int initialSize = getNumberOfVertices();
        NetworkComponentContainer ncc = null;
        Vertex vertex = null;

        LayoutProgressBarDialog layoutProgressBarDialog = layoutFrame.getLayoutProgressBar();
        layoutProgressBarDialog.prepareProgressBar(initialSize, "Finding Components");
        layoutProgressBarDialog.startProgressBar();

        while ( !allVerticesCopy.isEmpty() )
        {
            counter = initialSize - allVerticesCopy.size();

            layoutProgressBarDialog.incrementProgress(counter);

            ncc = new NetworkComponentContainer(layoutClassSetsManager, layoutFrame);
            vertex = allVerticesCopy.iterator().next();

            // has to add the keyset in a new HashSet for the tiling algorithm to work
            addToNcc( vertexDone, ncc, vertex, new HashSet<Vertex>( vertex.getEdgeConnectionsMap().keySet() ) );

            if (ncc.getNumberOfComponents() < size)
            {
                removeComponents( ncc.getVertices() );
                ncc.removeComponents();
            }
            else
            {
                componentCollection.add(ncc);
            }

            allVerticesCopy.removeAll(vertexDone);
        }

        renumberVertices();

        layoutProgressBarDialog.endProgressBar();
    }

    private void removeSingletons()
    {
        HashSet<Vertex> singletons = new HashSet<Vertex>();

        LayoutProgressBarDialog layoutProgressBarDialog = layoutFrame.getLayoutProgressBar();
        layoutProgressBarDialog.prepareProgressBar(verticesMap.size(), "Finding Singletons");
        layoutProgressBarDialog.startProgressBar();

        for ( Vertex vertex : getVertices() )
        {
            if ( vertex.getEdgeConnectionsMap().isEmpty() )
               singletons.add(vertex);
            layoutProgressBarDialog.incrementProgress();
        }

        layoutProgressBarDialog.setText("Removing " + singletons.size() + " Singletons...");
        layoutProgressBarDialog.endProgressBar();

        removeComponents(singletons);
    }

    private void removeComponents(Collection<Vertex> singletons)
    {
        HashSet<Edge> singletonEdges = new HashSet<Edge>();
        for (Vertex vertex : singletons)
        {
            verticesMap.remove( vertex.getVertexName() );
            singletonEdges.addAll( vertex.getEdgeConnectionsMap().values() );
            singletonEdges.add( vertex.getSelfEdge() );
        }

        edges.removeAll(singletonEdges);
    }

    private void renumberVertices()
    {
        int count = 0;
        for ( Vertex vertex : getVertices() )
            vertex.setVertexID(count++);
    }

    private void addToNcc(HashSet<Vertex> vertexDone, NetworkComponentContainer ncc, Vertex vertex, HashSet<Vertex> toDoVertices)
    {
        vertexDone.add(vertex);
        ncc.addNetworkConnection(vertex);

        Vertex currentVertex = null;
        Iterator<Vertex> iterator = toDoVertices.iterator();
        while ( iterator.hasNext() )
        {
            currentVertex = iterator.next();
            if ( !vertexDone.contains(currentVertex) )
            {
                toDoVertices.addAll( currentVertex.getEdgeConnectionsMap().keySet() );
                vertexDone.add(currentVertex);
                ncc.addNetworkConnection(currentVertex);
            }
            else
            {
                toDoVertices.remove(currentVertex);
            }

            iterator = toDoVertices.iterator(); // so as to avoid a concurrent modification exception
        }
    }

    @Override
    public void optimize(GraphLayoutAlgorithm gla)
    {
        LayoutProgressBarDialog layoutProgressBarDialog = layoutFrame.getLayoutProgressBar();

        switch (gla)
        {
            case FMMM:
                GraphAttributes GA = new GraphAttributes(this,
                        RENDERER_MODE_3D ? PointFactory.Dimensions._3 : PointFactory.Dimensions._2);
                FMMMLayout fmmm = new FMMMLayout();

                fmmm.useHighLevelOptions(false);
                fmmm.unitEdgeLength(FMMM_DESIRED_EDGE_LENGTH.get());
                fmmm.newInitialPlacement(true);

                switch (FMMM_QUALITY_VS_SPEED.get())
                {
                    case VERY_HIGH_QUALITY_VERY_LOW_SPEED:
                        fmmm.fixedIterations(200);
                        fmmm.fineTuningIterations(100);
                        break;
                    case HIGH_QUALITY_LOW_SPEED:
                        fmmm.fixedIterations(100);
                        fmmm.fineTuningIterations(50);
                        break;
                    case MEDIUM_QUALITY_MEDIUM_SPEED:
                        fmmm.fixedIterations(20);
                        fmmm.fineTuningIterations(10);
                        break;
                    case LOW_QUALITY_HIGH_SPEED:
                        fmmm.fixedIterations(3);
                        fmmm.fineTuningIterations(1);
                        break;
                }

                switch (FMMM_FORCE_MODEL.get())
                {
                    case EADES:
                        fmmm.repulsiveForcesCalculation(FMMMLayout.RepulsiveForcesMethod.rfcExact);
                        break;
                    case FRUCHTERMAN_RHEINGOLD:
                        fmmm.repulsiveForcesCalculation(FMMMLayout.RepulsiveForcesMethod.rfcGridApproximation);
                        break;
                    /*case NMM:
                        fmmm.repulsiveForcesCalculation(FMMMLayout.RepulsiveForcesMethod.rfcNMM);
                        break;*/
                }

                switch (FMMM_STOP_CRITERION.get())
                {
                    case FORCE_THRESHOLD_AND_FIXED_ITERATIONS:
                        fmmm.stopCriterion(FMMMLayout.StopCriterion.scFixedIterationsOrThreshold);
                        break;
                    case FIXED_ITERATIONS:
                        fmmm.stopCriterion(FMMMLayout.StopCriterion.scFixedIterations);
                        break;
                    case FORCE_THRESHOLD:
                        fmmm.stopCriterion(FMMMLayout.StopCriterion.scThreshold);
                        break;
                }

                fmmm.maxIterFactor(FMMM_ITERATION_LEVEL_FACTOR.get());

                fmmm.initialPlacementForces(FMMMLayout.InitialPlacementForces.ipfUniformGrid);
                fmmm.call(GA, layoutProgressBarDialog);
                GA.applyTo(this);
                break;

            case CIRCLE:
                CircleLayout ca = new CircleLayout();
                for (NetworkComponentContainer ncc : componentCollection)
                {
                    ca.layout(ncc);
                    ncc.calcBoundaries();
                }

                Collections.sort(componentCollection, new NetworkComponentSorter());

                layoutProgressBarDialog.prepareProgressBar(componentCollection.size(), "Tiling Graph Components");
                layoutProgressBarDialog.startProgressBar();

                for (NetworkComponentContainer ncc : componentCollection)
                {
                    layoutProgressBarDialog.incrementProgress();
                    tilingLevelsContainer.addNetworkComponentContainer(ncc);
                }

                tilingLevelsContainer.optimize();

                layoutProgressBarDialog.endProgressBar();
                break;

            default:
            case FRUCHTERMAN_RHEINGOLD:
                if (WEIGHTED_EDGES)
                {
                    normaliseWeights();
                }

                setKvalue();

                if (!TILED_LAYOUT.get())
                {
                    super.optimize(gla);
                }
                else
                {
                    int componentNumber = 0;
                    for (NetworkComponentContainer ncc : componentCollection)
                    {
                        if (layoutProgressBarDialog.userHasCancelled())
                        {
                            break;
                        }
                        ncc.optimize(++componentNumber);
                    }

                    if (layoutProgressBarDialog.userHasCancelled())
                    {
                        break;
                    }

                    frLayout.clean();

                    Collections.sort(componentCollection, new NetworkComponentSorter());

                    layoutProgressBarDialog.prepareProgressBar(componentCollection.size(), "Tiling Graph Components");
                    layoutProgressBarDialog.startProgressBar();

                    for (NetworkComponentContainer ncc : componentCollection)
                    {
                        layoutProgressBarDialog.incrementProgress();
                        tilingLevelsContainer.addNetworkComponentContainer(ncc);
                    }

                    tilingLevelsContainer.optimize();

                    layoutProgressBarDialog.endProgressBar();
                }

                // It's a bit wasteful to rescale here only to then call rescaleToFitCanvas,
                // but it makes the logic much more simple
                rescale((float) REFERENCE_K_VALUE / frLayout.getKValue(), new Point3D(), true, false, false);
                break;
        }

        rescaleToFitCanvas();
    }

    private void rescale(float scale, Point3D offset,
            boolean positions, boolean nodeSizes, boolean arrowHeadSizes)
    {
        for (Vertex vertex : getVertices())
        {
            if (positions)
            {
                vertex.setVertexLocation(
                        (float) (vertex.getX() + offset.getX()) * scale,
                        (float) (vertex.getY() + offset.getY()) * scale,
                        (float) (vertex.getZ() + offset.getZ()) * scale);
            }

            if (nodeSizes)
            {
                vertex.setVertexSize(vertex.getVertexSize() * (float) scale);
            }
        }

        if (arrowHeadSizes)
        {
            // This is crap, but then so is the entire concept of having a global user setting that
            // is then subsequently programatically adjusted
            int newSize = (int) (15.0f * scale);
            newSize = org.BioLayoutExpress3D.StaticLibraries.Math.clamp(newSize, MIN_ARROWHEAD_SIZE, MAX_ARROWHEAD_SIZE);

            ARROW_HEAD_SIZE.set(newSize);
        }
    }

    private void rescaleToFitCanvas()
    {
        float xMin = Float.MAX_VALUE;
        float xMax = Float.MIN_VALUE;
        float yMin = Float.MAX_VALUE;
        float yMax = Float.MIN_VALUE;
        float zMin = Float.MAX_VALUE;
        float zMax = Float.MIN_VALUE;

        for (Vertex vertex : getVertices())
        {
            float x = vertex.getX();
            float y = vertex.getY();
            float z = vertex.getZ();

            if (x < xMin)
            {
                xMin = x;
            }
            if (x > xMax)
            {
                xMax = x;
            }
            if (y < yMin)
            {
                yMin = y;
            }
            if (y > yMax)
            {
                yMax = y;
            }
            if (z < zMin)
            {
                zMin = z;
            }
            if (z > zMax)
            {
                zMax = z;
            }
        }

        float xSpan = xMax - xMin;
        float ySpan = yMax - yMin;
        float zSpan = zMax - zMin;

        float maxDimension = Math.max(Math.max(xSpan, ySpan), zSpan);
        float maxTargetDimension = Math.max(Math.max(NetworkContainer.CANVAS_X_SIZE,
                NetworkContainer.CANVAS_Y_SIZE), NetworkContainer.CANVAS_Z_SIZE);
        float scale = maxTargetDimension / maxDimension;

        float xOffset = -xMin - (xSpan * 0.5f) + ((NetworkContainer.CANVAS_X_SIZE * 0.5f) / scale);
        float yOffset = -yMin - (ySpan * 0.5f) + ((NetworkContainer.CANVAS_Y_SIZE * 0.5f) / scale);
        float zOffset = -zMin - (zSpan * 0.5f) + ((NetworkContainer.CANVAS_Z_SIZE * 0.5f) / scale);

        Point3D offset = new Point3D(xOffset, yOffset, zOffset);

        rescale((float) scale, offset, true, !isRelayout, !isRelayout);
    }

    @Override
    public void optimize(int componentID) {}

    @Override
    public void relayout(GraphLayoutAlgorithm gla)
    {
        isOptimized = false;
        isRelayout = true;

        if ( !TILED_LAYOUT.get() )
        {
            super.relayout(gla);
        }
        else
        {
            LayoutProgressBarDialog layoutProgressBarDialog = layoutFrame.getLayoutProgressBar();
            layoutProgressBarDialog.prepareProgressBar(componentCollection.size(), "Now Processing Burst Layout Iterations...");
            layoutProgressBarDialog.startProgressBar();

            tilingLevelsContainer.clear();

            if (DEBUG_BUILD) println("Number of groups: " + componentCollection.size());

            Collections.sort( componentCollection, new NetworkComponentSorter() );

            for (NetworkComponentContainer ncc : componentCollection)
            {
                layoutProgressBarDialog.incrementProgress();

                if (DEBUG_BUILD) println("Adding Group Dimension with width: " + ncc.getWidth() + " and height: " + ncc.getHeight());

                tilingLevelsContainer.addNetworkComponentContainer(ncc);
            }

            tilingLevelsContainer.optimize();
            tilingLevelsContainer.debug();

            layoutProgressBarDialog.endProgressBar();
            layoutProgressBarDialog.stopProgressBar();
        }

        isRelayout = false;
    }

    public void setKvalue()
    {
        frLayout.setKvalue( layoutFrame, getVertices() );
    }

    public boolean isOptimized()
    {
        return isOptimized;
    }

    @Override
    public void clear()
    {
        super.clear();

        componentCollection.clear();
        tilingLevelsContainer.clear();
    }

    public void clearRoot()
    {
        componentCollection.clear();
        tilingLevelsContainer.clear();
    }

    private static class NetworkComponentSorter implements Comparator<NetworkComponentContainer>, Serializable
    {

        /**
        *  Serial version UID variable for the NetworkComponentSorter class.
        */
        public static final long serialVersionUID = 111222333444555624L;

        @Override
        public int compare(NetworkComponentContainer ncc1, NetworkComponentContainer ncc2)
        {
            return ( ncc1.getWidth() < ncc2.getWidth() ) ? 1 : ( ncc1.getWidth() > ncc2.getWidth() ) ? -1 : 0;
        }


    }


}