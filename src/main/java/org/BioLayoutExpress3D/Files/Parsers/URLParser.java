package org.BioLayoutExpress3D.Files.Parsers;

import java.awt.*;
import java.io.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.CoreUI.Dialogs.*;
import org.BioLayoutExpress3D.Network.*;
import org.BioLayoutExpress3D.Utils.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* @author Leon Goldovsky, full refactoring by Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*
*/

public final class URLParser
{
    private NetworkContainer nc = null;
    private LayoutClassSetsManager layoutClassSetsManager = null;
    private LayoutFrame layoutFrame = null;
    protected BufferedReader fileReaderBuffered = null;
    protected BufferedReader fileReaderCounter = null;
    private String line = "";
    private int length = 0;
    private int currentPos = 0;
    private DataInputStream dis = null;

    public URLParser(NetworkContainer nc, LayoutClassSetsManager layoutClassSetsManager, LayoutFrame layoutFrame)
    {
        this.nc = nc;
        this.layoutClassSetsManager = layoutClassSetsManager;
        this.layoutFrame = layoutFrame;
    }

    public boolean init(InputStream is)
    {
        dis = new DataInputStream(is);

        return true;
    }

    public boolean parse()
    {
        boolean isSuccessful = false;
        int totalLines = 0;

        nc.setOptimized(false);
        LayoutProgressBarDialog layoutProgressBarDialog = layoutFrame.getLayoutProgressBar();

        try
        {
            layoutProgressBarDialog.prepareProgressBar(totalLines, "Parsing...");
            layoutProgressBarDialog.startProgressBar();

            while ( ( line = dis.readUTF() ) != null )
            {
                layoutProgressBarDialog.incrementProgress();
                length = line.length();
                currentPos = 0;

                if (length > 0)
                {
                    if ( line.startsWith("//") )
                        updateVertexProps();
                    else
                        createVertices();
                }
            }

            isSuccessful = true;
        }
        catch (IOException ioe)
        {
            if (DEBUG_BUILD) println("IOException in URLParser.parse():\n" + ioe.getMessage());
        }
        finally
        {
            try
            {
                dis.close();
            }
            catch (IOException ioe)
            {
                if (DEBUG_BUILD) println("IOException while closing streams in URLParser.parse():\n" + ioe.getMessage());
            }
            finally
            {
                layoutProgressBarDialog.endProgressBar();
            }
        }

        // if (coordinatesRead)
        //     layoutFrame.startProgressBar();

        return isSuccessful;
    }

    private void updateVertexProps()
    {
        String property = getNext();
        String vertex = "";
        String field1 = "", field2 = "", field3 = "", field4 = "";

        if ( property.equals("//NODECLASS") )
        {
            vertex = getNext();
            field1 = getNext();

            if ( nc.getVerticesMap().containsKey(vertex) )
                layoutClassSetsManager.getCurrentClassSetAllClasses().updateClass(nc.getVerticesMap().get(vertex), Integer.parseInt(field1), "");
        }

        if ( property.equals("//NODESIZE") )
        {
            vertex = getNext();
            field1 = getNext();

            if ( nc.getVerticesMap().containsKey(vertex) )
                nc.getVerticesMap().get(vertex).setVertexSize( Float.parseFloat(field1) );
        }

        if ( property.equals("//NODECOLOR") )
        {
            vertex = getNext();
            field1 = getNext();

            if ( nc.getVerticesMap().containsKey(vertex) )
                nc.getVerticesMap().get(vertex).setVertexColor( Color.decode(field1) );
        }

        if ( property.equals("//CLASSNAME") )
        {
            field1 = getNext();
            String thisWord = "";
            String className = "";

            while ( !( thisWord = getNext() ).isEmpty() )
                className += (" " + thisWord);

            layoutClassSetsManager.getCurrentClassSetAllClasses().setClassName(Integer.parseInt(field1), className);
        }

        if ( property.equals("//CLASSCOLOR") )
        {
            field1 = getNext();
            field2 = getNext();
            layoutClassSetsManager.getCurrentClassSetAllClasses().setClassColor( Integer.parseInt(field1), Color.decode(field2) );
        }
        else if ( property.equals("//COORD") )
        {
            // nc.setReadwithCoords(true);

            field1 = getNext();
            field2 = getNext();
            field3 = getNext();
            field4 = getNext();
            if ( field4.isEmpty() )
                field4 = "0";

            if (DEBUG_BUILD) println("name: " + field1 + " coord1: " + field2 + " coord2: " + field2);

            nc.updateVertexLocation( field1, (int) Double.parseDouble(field2), (int) Double.parseDouble(field3), (int) Double.parseDouble(field4) );
        }
        else if ( property.equals("//EDGESIZE") )
        {
            DEFAULT_EDGE_SIZE.set( Float.parseFloat( getNext() ) );
        }
        else if ( property.equals("//EDGECOLOR") )
        {
            DEFAULT_EDGE_COLOR.set( Color.decode( getNext() ) );
        }
        else if ( property.equals("//DEFAULTSEARCH") )
        {
            if (DEBUG_BUILD) println("search default found!!");

            String str = getNext();
            boolean preset = false;
            for (int i = 0; i < PRESET_SEARCH_URL.length; i++)
            {
                if ( str.equals( PRESET_SEARCH_URL[i].getName() ) )
                {
                    if (DEBUG_BUILD) println("is a preset one");

                    SEARCH_URL = PRESET_SEARCH_URL[i];

                    if (DEBUG_BUILD) println( SEARCH_URL.getUrl() );

                    preset = true;
                    break;
                }
            }

            if (!preset)
            {
                if (DEBUG_BUILD) println("is a custom one");

                SearchURL customSearchURL = new SearchURL(str);
                SEARCH_URL = customSearchURL;

                if (DEBUG_BUILD) println( SEARCH_URL.getUrl() );

                CUSTOM_SEARCH = true;
            }
        }
        else if ( property.equals("//") )
        {

        }
    }

    private void createVertices()
    {
        String vertex1 = getNext();
        String vertex2 = getNext();
        String weightString = getNext();
        float weight = 0.0f;

        if (weightString.length() > 0)
        {
            try
            {
                weight = Float.parseFloat(weightString);
            }
            catch (NumberFormatException nfe) {}
        }

        if (weight > 0.0f)
        {
            nc.addNetworkConnection(vertex1, vertex2, weight);
            WEIGHTED_EDGES = true;
        }
        else
        {
            nc.addNetworkConnection(vertex1, vertex2, 0.0f);
        }
    }

    private String getNext()
    {
        String word = "";
        String separatorString = "\r\n\t ";
        char tempChar = ' ';
        // for (currentPos = currentPos; currentPos < length; ++currentPos)
        while (currentPos < length)
        {
            tempChar = line.charAt(currentPos);
            if ( separatorString.contains( String.valueOf(tempChar) ) )
                currentPos++;
            else
                break;
        }

        if (tempChar == '"')
        {
            currentPos++;
            separatorString = "\"";
        }

        while (currentPos < length)
        {
            tempChar = line.charAt(currentPos);
            if ( separatorString.contains( String.valueOf(tempChar) ) )
                break;

            word += tempChar;
            currentPos++;
        }

        if ( separatorString.contains("\"") )
            currentPos++;

        return word;
    }


}