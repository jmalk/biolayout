package org.BioLayoutExpress3D.Network;

import java.util.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* @author Leon Goldovsky, full refactoring by Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*
*/

public class LevelNetworkComponentContainer
{
    // Has to be an arraylist for the tiling algorithm to work
    private ArrayList<NetworkComponentContainer> componentNetworkContainer = null;

    private double maxWidth = 0.0;
    private double currentWidth = 0.0;
    private double currentHeight = 0.0;

    public LevelNetworkComponentContainer(double maxWidth)
    {
        this.maxWidth = maxWidth;
        componentNetworkContainer = new ArrayList<NetworkComponentContainer>();
    }

    public boolean addNetworkComponentContainer(NetworkComponentContainer ncc)
    {
        double componentWidth = ncc.getWidth();
        boolean isAdded = false;
        if ( (currentWidth == 0) || ( (currentWidth + componentWidth) <= maxWidth ) )
        {
            componentNetworkContainer.add(ncc);
            currentWidth += ncc.getWidth();

            double height = ncc.getHeight();

            if (currentHeight < height)
                currentHeight = height;

            isAdded = true;
        }

        return isAdded;
    }

    public void setMaxWidth(double maxWidth)
    {
        this.maxWidth = maxWidth;
    }

    public double getHeight()
    {
        return currentHeight;
    }

    public void optimize(float ratio, double startHeight)
    {
        int startWidth = 0;
        for (NetworkComponentContainer ncc : componentNetworkContainer)
        {
            ncc.assignComponentDisplacement(ratio, startWidth, startHeight);
            startWidth += ncc.getWidth();
        }
    }

    public double getFreeWidth()
    {
        return maxWidth - currentWidth;
    }

    public void debug()
    {
        int i = 0;
        for (NetworkComponentContainer ncc : componentNetworkContainer)
        {
            i++;
            if (DEBUG_BUILD) println("Component: " + i + " with width: " + ncc.getWidth());
        }
    }


}