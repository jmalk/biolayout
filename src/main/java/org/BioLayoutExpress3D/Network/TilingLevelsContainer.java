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

public class TilingLevelsContainer
{
    // Has to be an arraylist for the tiling algorithm to work
    private ArrayList<LevelNetworkComponentContainer> allLevels = null;

    private double currentLevelWidth = 1000.0;
    private double sumAllLeveHeight = 0.0;
    private static final float RATIO = 1.0f;

    public TilingLevelsContainer()
    {
        allLevels = new ArrayList<LevelNetworkComponentContainer>();
        allLevels.add( new LevelNetworkComponentContainer(currentLevelWidth) );
    }

    public void addNetworkComponentContainer(NetworkComponentContainer ncc)
    {
        LevelNetworkComponentContainer leastUtilizedLevel = leastUtilizedLevel();
        if ( !leastUtilizedLevel.addNetworkComponentContainer(ncc) )
        {
            updateSumAllLevelHeight();

            if ( (currentLevelWidth / sumAllLeveHeight) < RATIO )
            {
                leastUtilizedLevel = expandLevelsWidth( ncc.getWidth() );
                leastUtilizedLevel.addNetworkComponentContainer(ncc);
            }
            else
            {
                addNetworkComponentContainerNewLevel(ncc);
            }
        }
    }

    private LevelNetworkComponentContainer leastUtilizedLevel()
    {
        LevelNetworkComponentContainer leastUtilizedLevel = null, currentLevel = null;
        Iterator<LevelNetworkComponentContainer> it = allLevels.iterator();
        leastUtilizedLevel = it.next();

        while ( it.hasNext() )
        {
            currentLevel = it.next();

            if ( currentLevel.getFreeWidth() > leastUtilizedLevel.getFreeWidth() )
                leastUtilizedLevel = currentLevel;
        }

        return leastUtilizedLevel;
    }

    private LevelNetworkComponentContainer expandLevelsWidth(double width)
    {
        LevelNetworkComponentContainer levelNcc = leastUtilizedLevel();
        double freeWidth = levelNcc.getFreeWidth();

        currentLevelWidth += (width - freeWidth + 1);

        for (LevelNetworkComponentContainer currentLevelNcc : allLevels)
            currentLevelNcc.setMaxWidth(currentLevelWidth);

        return levelNcc;
    }

    private void addNetworkComponentContainerNewLevel(NetworkComponentContainer ncc)
    {
        LevelNetworkComponentContainer levelNcc = new LevelNetworkComponentContainer(currentLevelWidth);
        levelNcc.addNetworkComponentContainer(ncc);
        allLevels.add(levelNcc);
    }

    private void updateSumAllLevelHeight()
    {
        sumAllLeveHeight = 0;

        for (LevelNetworkComponentContainer levelNcc : allLevels)
            sumAllLeveHeight += levelNcc.getHeight();
    }

    public void debug()
    {
        sumAllLeveHeight = 0;

        int i = 0;
        for (LevelNetworkComponentContainer levelNcc : allLevels)
        {
            i++;
            if (DEBUG_BUILD) println("Level: " + i);
            levelNcc.debug();
        }
    }

    public void optimize()
    {
        updateSumAllLevelHeight();

        float xRatio = (float)currentLevelWidth / 1000.0f;
        float yRatio = (float)sumAllLeveHeight / 1000.0f;
        float ratio = (xRatio > yRatio) ? xRatio : yRatio;
        double startHeight = 0;

        for (LevelNetworkComponentContainer levelNcc : allLevels)
        {
            levelNcc.optimize(ratio, startHeight);
            startHeight += levelNcc.getHeight();
        }
    }

    public void clear()
    {
        allLevels.clear();
        currentLevelWidth = 1000;
        sumAllLeveHeight = 0;
        allLevels.add( new LevelNetworkComponentContainer(currentLevelWidth) );
    }


}