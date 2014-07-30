package org.BioLayoutExpress3D.Files.Parsers;

import java.io.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.CoreUI.Dialogs.*;
import org.BioLayoutExpress3D.Network.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* @author Leon Goldovsky, full refactoring by Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public final class BlastParser extends CoreParser
{
    private String firstVertex = "";
    private int counter = 0;

    public BlastParser(NetworkContainer nc, LayoutFrame layoutFrame)
    {
        super(nc, layoutFrame);

        counter = 0;
    }

    @Override
    public boolean parse()
    {
        int totalLines = 0;

        isSuccessful = false;
        nc.setOptimized(false);

        LayoutProgressBarDialog layoutProgressBarDialog = layoutFrame.getLayoutProgressBar();

        try
        {
            while ( ( line = fileReaderCounter.readLine() ) != null )
                totalLines++;

            layoutProgressBarDialog.prepareProgressBar(totalLines, "Parsing...");
            layoutProgressBarDialog.startProgressBar();

            while ( ( line = fileReaderBuffered.readLine() ) != null )
            {
                layoutProgressBarDialog.incrementProgress(++counter);

                tokenize(line);
                if (line.length() > 0)
                {
                    if ( line.startsWith("Query= ") )
                    {
                        getNext();
                        firstVertex = getNext();

                    }
                    else if ( line.startsWith("Sequences producing ") )
                    {
                        readNextHits();
                    }
                }
            }

            isSuccessful = true;
        }
        catch (IOException ioe)
        {
            if (DEBUG_BUILD) println("IOException in BlastParser.parse():\n" + ioe.getMessage());
        }
        finally
        {
            try
            {
                fileReaderCounter.close();
                fileReaderBuffered.close();
            }
            catch (IOException ioe)
            {
                if (DEBUG_BUILD) println("IOException while closing streams in BlastParser.parse():\n" + ioe.getMessage());
            }
            finally
            {
                layoutProgressBarDialog.endProgressBar();
            }
        }

        /*
        if (coordinatesRead)
        {
            layoutProgressBarDialog.startProgressBar();
        }
        */

        return isSuccessful;
    }

    private void readNextHits()
    {
        try
        {
            line = fileReaderBuffered.readLine();
            counter++;
            while ( ( line = fileReaderBuffered.readLine() ) != null && !line.isEmpty() )
            {
                counter++;
                createVertices();
            }

        }
        catch (IOException ioe)
        {
            if (DEBUG_BUILD) println("IOException in BlastParser.readNextHits():\n" + ioe.getMessage());
        }
    }

    private void createVertices()
    {
        int length = line.length();
        float weight = 0.0f;
        String vertex2 = getNext();
        if ( vertex2.equals(firstVertex) )
            return;

        String weightString = "";
        try
        {
            weightString = line.substring(length - 3, length);
        }
        catch (Exception exc) {}

        if (weightString.charAt(0) == '-')
            weight = Float.parseFloat( weightString.substring(1, 3) );
        else
            weight = Float.parseFloat(weightString);

        /*
        if (weight_string.length() > 0)
        {
            try
            {
                weight = Double.parseDouble(weight_string);
            }
            catch(NumberFormatException nfe) {}
        }
        */

        if ( (weight == 0.0f) || (weight > 100.0f) )
            weight = 100.0f;

        if (weight > 0.0f)
        {
            nc.addNetworkConnection(firstVertex, vertex2, weight);
            WEIGHTED_EDGES = true;
        }
        else
        {
            nc.addNetworkConnection(firstVertex, vertex2, 0.0f);
        }
    }


}