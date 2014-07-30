package org.BioLayoutExpress3D.Network;

import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.CoreUI.Dialogs.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
*
* @author Leon Goldovsky, full refactoring by Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*
*/

public final class NetworkComponentContainer extends NetworkContainer
{
    private double minX = 0.0;
    private double maxX = 0.0;
    private double minY = 0.0;
    private double maxY = 0.0;
    private double avgZ = 0.0;
    private double width = 0.0;
    private double height = 0.0;

    public NetworkComponentContainer(LayoutClassSetsManager layoutClassSetsManager, LayoutFrame layoutFrame)
    {
        super(layoutClassSetsManager, layoutFrame);
    }

    private void initBoundaries()
    {
        minX = 1000.0;
        maxX = 0.0;
        minY = 1000.0;
        maxY = 0.0;
    }

    public void calcBoundaries()
    {
        double tempWidth = 0.0, tempHeight = 0.0;
        for ( Vertex vertex : verticesMap.values() )
        {
            tempWidth = vertex.getX();
            tempHeight = vertex.getY();

            avgZ += vertex.getZ();
            minX = (minX > tempWidth) ? tempWidth : minX;
            maxX = (maxX < tempWidth) ? tempWidth : maxX;
            minY = (minY > tempHeight) ? tempHeight : minY;
            maxY = (maxY < tempHeight) ? tempHeight : maxY;
        }

        avgZ = avgZ / verticesMap.size();
        width = maxX - minX;
        height = maxY - minY;
    }

    public void addNetworkConnection(Vertex vertex)
    {
        verticesMap.put(vertex.getVertexName(), vertex);
    }

    @Override
    public void optimize(int componentID)
    {
        initBoundaries();

        float initialTemperature = frLayout.getTemperature();
        int numberOfIterations = 0;

        LayoutProgressBarDialog layoutProgressBarDialog = layoutFrame.getLayoutProgressBar();
        String progressBarParallelismTitle = (USE_MULTICORE_PROCESS && USE_LAYOUT_N_CORE_PARALLELISM.get() ) ? "(Utilizing " + NUMBER_OF_AVAILABLE_PROCESSORS + "-Core Parallelism)" : "";

        if (isRelayout)
        {
            numberOfIterations = BURST_LAYOUT_ITERATIONS.get();
            if (frLayout.getTemperature() == 100.0f)
                frLayout.setTemperature(4.0f);

            componentID = 0;
            layoutProgressBarDialog.prepareProgressBar(BURST_LAYOUT_ITERATIONS.get(), "Now Processing Burst Layout Iterations" + progressBarParallelismTitle + "...");
        }
        else
        {
            numberOfIterations = frLayout.getNumberOfIterations();
            layoutProgressBarDialog.prepareProgressBar(numberOfIterations,
                    "Now Processing Layout Iterations " + progressBarParallelismTitle +
                    " for Graph Component: " + componentID, true);
        }

        layoutProgressBarDialog.startProgressBar();

        if (!isOptimized)
        {
            frLayout.createVerticesMatrices( getVertices() );

            if (!RENDERER_MODE_3D)
                frLayout.allIterationsCalcBiDirForce2D(numberOfIterations, componentID, layoutProgressBarDialog);
            else
                frLayout.allIterationsCalcBiDirForce3D(numberOfIterations, componentID, layoutProgressBarDialog);

            // applying the new vertex points at the end of the layout algoprithm process
            frLayout.setPointsToVertices();
            frLayout.setTemperature(initialTemperature);
        }
        else
        {
            isOptimized = false;
        }

        layoutProgressBarDialog.endProgressBar();
        if (isRelayout) layoutProgressBarDialog.stopProgressBar();

        calcBoundaries();
    }

    public void assignComponentDisplacement(float ratio, double width, double height)
    {
        for ( Vertex vertex : getVertices() )
            vertex.setComponentDisplacement( ratio, (float)(width - minX), (float)(height - minY), (float)(vertex.getZ() - avgZ) );
    }

    public double getWidth()
    {
        return width + 100.0;
    }

    public double getHeight()
    {
        return height + 100.0;
    }

    public int getNumberOfComponents()
    {
        return verticesMap.size();
    }

    public void removeComponents()
    {
        verticesMap.clear();
        edges.clear();
    }


}