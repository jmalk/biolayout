package org.BioLayoutExpress3D.Files.Parsers;

import java.io.*;
import java.util.HashSet;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.CoreUI.Dialogs.*;
import org.BioLayoutExpress3D.Expression.*;
import org.BioLayoutExpress3D.Network.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.Expression.ExpressionEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* @author Anton Enright, full refactoring by Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public class ExpressionParser extends CoreParser
{
    private ObjectInputStream iistream = null;
    File file = null;
    private ExpressionData expressionData = null;
    private int[][] counts = null;

    public ExpressionParser(NetworkContainer nc, LayoutFrame layoutFrame, ExpressionData expressionData)
    {
        super(nc, layoutFrame);

        this.expressionData = expressionData;
        this.counts = expressionData.getCounts();
    }

    @Override
    public boolean init(File file, String fileExtension)
    {
        try
        {
            this.file = file;
            iistream = new ObjectInputStream( new BufferedInputStream( new FileInputStream(file) ) );

            return true;
        }
        catch (Exception exc)
        {
            try
            {
                iistream.close();
            }
            catch (IOException ioe)
            {
                if (DEBUG_BUILD) println("IOException while closing streamers in ExpressionParser.init():\n" + ioe.getMessage());
            }
            finally
            {

            }

            return false;
        }
    }

    @Override
    public boolean parse()
    {
        int counter = 0;
        HashSet<Integer> filterSet = new HashSet<Integer>();
        LayoutProgressBarDialog layoutProgressBarDialog = layoutFrame.getLayoutProgressBar();
        layoutProgressBarDialog.prepareProgressBar(100, "Reading in Graph Data:");
        layoutProgressBarDialog.startProgressBar();

        isSuccessful = false;
        nc.setOptimized(false);

        try
        {
            iistream.readInt();    // magic number

            int nodeId = 0;
            int percent = 0;
            String nodeOne = "";
            String nodeTwo = "";
            int otherId = 0;
            float weight = 0.0f;
            while (iistream.available() != 0)
            {
                nodeId = iistream.readInt();
                percent = (int)( 100.0f * ( (float)counter / (float)expressionData.getTotalRows() ) );

                layoutProgressBarDialog.incrementProgress(percent);

                nodeOne = expressionData.getRowID(nodeId);
                for (;;) // while (true)
                {
                    otherId = iistream.readInt();
                    if (nodeId == otherId)
                    {
                        break;
                    }
                    else
                    {
                        weight = iistream.readFloat();
                        if (weight > CURRENT_CORRELATION_THRESHOLD)
                        {
                            boolean filterOne = CURRENT_FILTER_SET.contains(nodeId);
                            boolean filterTwo = CURRENT_FILTER_SET.contains(otherId);

                            if (!filterOne && !filterTwo)
                            {
                                nodeTwo = expressionData.getRowID(otherId);
                                nc.addNetworkConnection(nodeOne, nodeTwo, weight);
                            }
                        }
                    }
                }

                counter++;
            }

            WEIGHTED_EDGES = true;

            isSuccessful = true;
        }
        catch (IOException ioe)
        {
            if (DEBUG_BUILD) println("IOException in ExpressionParser.parse():\n" + ioe.getMessage());
        }
        finally
        {
            try
            {
                iistream.close();
            }
            catch (IOException ioe)
            {
                if (DEBUG_BUILD) println("IOException while closing streams in ExpressionParser.parse():\n" + ioe.getMessage());
            }
            finally
            {
                layoutProgressBarDialog.endProgressBar();
            }
        }

        return isSuccessful;
    }

    public void close()
    {
        try
        {
            iistream.close();
        }
        catch (IOException ioe)
        {
            if (DEBUG_BUILD) println("IOException while closing streams in ExpressionParser.close():\n" + ioe.getMessage());
        }
    }

    public void scan()
    {
        try
        {
            iistream.readInt();    // magic number

            int nodeId = 0;
            int otherId = 0;
            int index = 0;
            float weight = 0.0f;
            while (iistream.available() != 0)
            {
                nodeId = iistream.readInt();

                for (;;) // while (true)
                {
                    otherId = iistream.readInt();
                    if (nodeId == otherId)
                    {
                        break;
                    }
                    else
                    {
                        weight = iistream.readFloat();
                        index = (int)Math.floor(100.0f * weight);

                        boolean filterOne = CURRENT_FILTER_SET.contains(nodeId);
                        boolean filterTwo = CURRENT_FILTER_SET.contains(otherId);

                        if (!filterOne && !filterTwo)
                        {
                            counts[nodeId][index]++;
                            counts[otherId][index]++;
                        }
                    }
                }
            }
        }
        catch (IOException ioe)
        {
            if (DEBUG_BUILD) println("IOException in ExpressionParser.scan():\n" + ioe.getMessage());
        }
    }

    public void rescan()
    {
        try
        {
            counts = expressionData.clearCounts();
            iistream = new ObjectInputStream( new BufferedInputStream( new FileInputStream(file) ) );
            scan();
        }
        catch (IOException ioe)
        {
            if (DEBUG_BUILD)
            {
                println("IOException in ExpressionParser.rescan():\n" + ioe.getMessage());
            }
        }
    }

    public boolean checkFile()
    {
        int magicNumber = 0;

        try
        {
            magicNumber = iistream.readInt();
        }
        catch (IOException ioe)
        {
            if (DEBUG_BUILD) println("IOException in ExpressionParser.check_file():\n" + ioe.getMessage());

            return false;
        }

        if ( magicNumber == ExpressionData.FILE_MAGIC_NUMBER)
        {
            return true;
        }

        return false;
    }

    public int getNodeCount()
    {
        return nc.getNumberOfVertices();
    }

    public int getEdgeCount()
    {
        return nc.getEdges().size();
    }

    public void removeSingletons()
    {
        for ( Vertex vertex : nc.getVertices() )
        {
            if ( vertex.getEdgeConnectionsMap().isEmpty() )
            {
                nc.getEdges().remove( vertex.getSelfEdge() );
                nc.getVerticesMap().remove( vertex.getVertexName() );
                nc.getVertices().remove(vertex);
            }
        }
    }


}