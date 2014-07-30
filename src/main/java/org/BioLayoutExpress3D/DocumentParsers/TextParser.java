package org.BioLayoutExpress3D.DocumentParsers;

import java.io.*;
import java.net.*;
import java.util.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* TextParser is used for containing the methods for text file parsing and other usefull methods.
*
* @author Thanos Theo, 2008-2009
* @version 3.0.0.0
*/

public abstract class TextParser
{
    /**
    *  Variable to store the name of the XML parser. Will be returned with the overriden toString() method.
    */
    protected String nameOfTextParser= "";

    /**
    *  The constructor of this class.
    */
    public TextParser() {}

    /**
    *  Initialize data structures method (to be overriden in subclasses).
    */
    protected void initDataStructures() {}

    /**
    *  Parses a text file from an ArrayList containing Strings for each line.
    */
    public boolean parseFromArrayList(ArrayList<String> arraylist)
    {
        boolean isSuccessful = false;
        String arraylistContent = "";

        initDataStructures();

        if (arraylist == null)
        {
            if (DEBUG_BUILD) println("Null ArrayList provided in the parseFromArrayList() method!");

            return false;
        }

        if ( arraylist.isEmpty() )
        {
            if (DEBUG_BUILD) println("Empty ArrayList provided in the parseFromArrayList() method!");

            return false;
        }

        // Append each line of the arraylist to arrayListContent.
	for (String line : arraylist)
            arraylistContent += line + "\n"; // add newline character for proper separation between lines

        if (DEBUG_BUILD) println("\nStarting text parsing from arraylist");

        try
        {
            BufferedReader br = new BufferedReader( new StringReader( arraylistContent ) );
            parseText(br);
            br.close();

            isSuccessful = true;
        }
        catch (IOException ioexc)
        {
            if (DEBUG_BUILD) println("IOException with the parseFromArrayList() method:\n" + ioexc.getMessage());
        }

        if (DEBUG_BUILD) println("Finished text parsing from arraylist\n");

        return isSuccessful;
    }

    /**
    *  Parses a text file from a url (or a local jar resource).
    */
    public boolean parseFromUrl(URL url)
    {
        boolean isSuccessful = false;

        initDataStructures();

        if (DEBUG_BUILD) println("\nStarting text parsing from URL address: " + url.toString());

        try
        {
            BufferedReader br = new BufferedReader( new InputStreamReader( url.openStream() ) );
            parseText(br);
            br.close();

            isSuccessful = true;
        }
        catch (IOException ioexc)
        {
            if (DEBUG_BUILD) println("IOException with the parseFromUrl() method:\n" + ioexc.getMessage());
        }

        if (DEBUG_BUILD) println("Finished text parsing from URL address: " + url.toString() + "\n");

        return isSuccessful;
    }

    /**
    *  Parses a text file from a local file.
    */

    //The function I will most likely be using.
    public boolean parseFromFile(String fileName)
    {
        boolean isSuccessful = false;

        initDataStructures();

        if (DEBUG_BUILD) println("\nStarting text parsing from file: " + fileName);

        try
        {
            BufferedReader br = new BufferedReader( new FileReader(fileName) );
            parseText(br);
            br.close();

            isSuccessful = true;
        }
        catch (IOException ioexc)
        {
            if (DEBUG_BUILD) println("IOException with the parseFromFile() method:\n" + ioexc.getMessage());
        }

        if (DEBUG_BUILD) println("Finished text parsing from file: " + fileName + "\n");

        return isSuccessful;
    }

    /**
    *  Parses a text file from a http url connection.
    */
    public boolean parseFromHttpUrlConnection(HttpURLConnection conn)
    {
        boolean isSuccessful = false;

        if (conn != null)
        {
            initDataStructures();

            if (DEBUG_BUILD) println("\nStarting text parsing from HTTP URL connection: (undisclosed connection!)");

            try
            {
                BufferedReader br = new BufferedReader( new InputStreamReader( conn.getInputStream() ) );
                parseText(br);
                br.close();

                isSuccessful = true;
            }
            catch (IOException ioexc)
            {
                if (DEBUG_BUILD) println("IOException with the parseFromHttpUrlConnection() method:\n" + ioexc.getMessage());
            }

            if (DEBUG_BUILD) println("Finished text parsing from HTTP URL connection: " + conn.toString() + "\n");

            conn.disconnect();
        }
        else
        {
            if (DEBUG_BUILD) println("Null HttpURLConnection provided!");
        }

        return isSuccessful;
    }

    /**
    *  The main parse text method (to be overriden in subclasses).
    */
    protected abstract void parseText(BufferedReader br) throws IOException;

    /**
    *  Sets the name of the text parser.
    */
    public void setTextParserName(String nameOfTextParser)
    {
        this.nameOfTextParser = nameOfTextParser;
    }

    /**
    *  Gets the name of the text parser.
    */
    public String getTextParserName()
    {
        return nameOfTextParser;
    }

    /**
    *  Gets the name of the text parser. Overrides the general toString() Object method.
    */
    @Override
    public String toString()
    {
        return nameOfTextParser;
    }


}
