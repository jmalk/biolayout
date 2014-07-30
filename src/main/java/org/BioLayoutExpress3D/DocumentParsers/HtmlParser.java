package org.BioLayoutExpress3D.DocumentParsers;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* HtmlParser class is used for containing the callback methods for html file parsing and other usefull methods.
* It uses the swing html parsing facility.
*
* @author Thanos Theo, 2008-2009
* @version 3.0.0.0
*/

public abstract class HtmlParser extends HTMLEditorKit.ParserCallback
{
    /**
    *  Variable to store the name of the html parser. Will be returned with the overriden toString() method.
    */
    protected String nameOfHtmlParser= "";

    /**
    *  The constructor of this class.
    */
    public HtmlParser() {}

    /**
    *  Small inner class that extends HTMLEditorKit to get the parser reference.
    */
    private static class Parse extends HTMLEditorKit
    {
        /**
        *  Serial version UID variable for the Parse class.
        */
        public static final long serialVersionUID = 111222333444555803L;

        /**
        * Call to obtain a HTMLEditorKit.Parser object.
        * Parser objects are instantiated by calling the getParser method of HTMLEditorKit. Unfortunately, this method does not have public access.
        * The only way to call getParser is by overriding getParser to a public member function in a subclass.
        *
        * @return A new HTMLEditorKit.Parser object.
        */
        @Override
        public Parser getParser()
        {
            return super.getParser();
        }
    }

    /**
    *  Initialize data structures method (to be overriden in subclasses).
    */
    protected void initDataStructures() {}

    /**
    *  Initializes the HTML parser classes.
    */
    private HTMLEditorKit.Parser makeHTMLParser()
    {
        return new Parse().getParser();
    }

    /**
    *  Parses a html file from an ArrayList containing Strings for each line.
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

        for (String line : arraylist)
            arraylistContent += line + "\n"; // add newline character for proper separation between lines

        if (DEBUG_BUILD) println("\nStarting html parsing from arraylist");

        try
        {
            HTMLEditorKit.Parser parser = makeHTMLParser();

            BufferedReader br = new BufferedReader( new StringReader(arraylistContent) );
            parser.parse(br, this, false);
            br.close();

            isSuccessful = true;
        }
        catch (IOException ioexc)
        {
            if (DEBUG_BUILD) println("IOException with the parseFromArrayList() method:\n" + ioexc.getMessage());
        }

        if (DEBUG_BUILD) println("Finished html parsing from arraylist\n");

        return isSuccessful;
    }


    /**
    *  Parses a html file from a url (or a local jar resource).
    */
    public boolean parseFromUrl(URL url)
    {
        boolean isSuccessful = false;

        initDataStructures();

        if (DEBUG_BUILD) println("\nStarting html parsing from url: " + url.toString());

        try
        {
            HTMLEditorKit.Parser parser = makeHTMLParser();

            BufferedReader br = new BufferedReader( new InputStreamReader( url.openStream() ) );
            parser.parse(br, this, false);
            br.close();

            isSuccessful = true;
        }
        catch (FileNotFoundException fileexc)
        {
            if (DEBUG_BUILD) println("FileNotFoundException with the parseFromUrl() method:\n" + fileexc.getMessage());
        }
        catch (IOException ioexc)
        {
            if (DEBUG_BUILD) println("IOException with the parseFromUrl() method:\n" + ioexc.getMessage());
        }

        if (DEBUG_BUILD) println("Finished html parsing from url: " + url.toString() + "\n");

        return isSuccessful;
    }

    /**
    *  Parses a html file from a local file.
    */
    public boolean parseFromFile(String fileName)
    {
        boolean isSuccessful = false;

        initDataStructures();

        if (DEBUG_BUILD) println("\nStarting html parsing from file" + fileName);

        try
        {
            HTMLEditorKit.Parser parser = makeHTMLParser();

            BufferedReader br = new BufferedReader( new FileReader(fileName) );
            parser.parse(br, this, false);
            br.close();

            isSuccessful = true;
        }
        catch (FileNotFoundException fileexc)
        {
            if (DEBUG_BUILD) println("FileNotFoundException with the parseFromFile() method:\n" + fileexc.getMessage());
        }
        catch (IOException ioexc)
        {
            if (DEBUG_BUILD) println("IOException with the parseFromFile() method:\n" + ioexc.getMessage());
        }

        if (DEBUG_BUILD) println("Finished html parsing from file" + fileName + "\n");

        return isSuccessful;
    }

    /**
    *  Parses a html file from a http url connection.
    */
    public boolean parseFromHttpUrlConnection(HttpURLConnection conn)
    {
        boolean isSuccessful = false;

        if (conn != null)
        {
            initDataStructures();

            if (DEBUG_BUILD) println("\nStarting html parsing from http url connection: " + conn.toString());

            try
            {
                HTMLEditorKit.Parser parser = makeHTMLParser();

                BufferedReader br = new BufferedReader( new InputStreamReader( conn.getInputStream() ) );
                parser.parse(br, this, false);
                br.close();

                isSuccessful = true;
            }
            catch (IOException ioexc)
            {
                if (DEBUG_BUILD) println("IOException with the parseFromHttpUrlConnection() method:\n" + ioexc.getMessage());
            }

            if (DEBUG_BUILD) println("Finished html parsing from http url connection: " + conn.toString() + "\n");

            conn.disconnect();
        }
        else
        {
            if (DEBUG_BUILD) println("Null HttpURLConnection provided!");
        }

        return isSuccessful;
    }

    /**
    *  Swing html parser callback method.
    */
    @Override
    public void handleComment(char[] data, int pos)
    {
        handleCommentInherit(data, pos);
    }

    /**
    *  Swing html parser callback method.
    */
    @Override
    public void handleEndOfLineString(String eol)
    {
        handleEndOfLineStringInherit(eol);
    }

    /**
    *  Swing html parser callback method.
    */
    @Override
    public void handleEndTag(HTML.Tag tag, int pos)
    {
        handleEndTagInherit(tag, pos);
    }

    /**
    *  Swing html parser callback method.
    */
    @Override
    public void handleError(String errorMsg, int pos)
    {
        handleErrorInherit(errorMsg, pos);
    }

    /**
    *  Swing html parser callback method.
    *  To get the mutable attribute set for HREF: String value = a.getAttribute(HTML.Attribute.HREF)
    */
    @Override
    public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet attrib, int pos)
    {
        handleSimpleTagInherit(tag, attrib, pos);
    }

    /**
    *  Swing html parser callback method.
    *  To get the mutable attribute set for HREF: String value = a.getAttribute(HTML.Attribute.HREF)
    */
    @Override
    public void handleStartTag(HTML.Tag tag, MutableAttributeSet attrib, int pos)
    {
        handleStartTagInherit(tag, attrib, pos);
    }

    /**
    *  Swing html parser callback method.
    */
    @Override
    public void handleText(char[] data, int pos)
    {
        handleTextInherit(data, pos);
    }

    /**
    *  Swing html parser callback method (to be overriden in subclasses).
    */
    protected void handleCommentInherit(char[] data, int pos) {}

    /**
    *  Swing html parser callback method (to be overriden in subclasses).
    */
    protected void handleEndOfLineStringInherit(String eol) {}

    /**
    *  Swing html parser callback method (to be overriden in subclasses).
    */
    protected void handleEndTagInherit(HTML.Tag tag, int pos) {}

    /**
    *  Swing html parser callback method (to be overriden in subclasses).
    */
    protected void handleErrorInherit(String errorMsg, int pos) {}

    /**
    *  Swing html parser callback method (to be overriden in subclasses).
    *  To get the mutable attribute set for HREF: String value = a.getAttribute(HTML.Attribute.HREF)
    */
    protected void handleSimpleTagInherit(HTML.Tag tag, MutableAttributeSet a, int pos) {}

    /**
    *  Swing html parser callback method (to be overriden in subclasses).
    *  To get the mutable attribute set for HREF: String value = a.getAttribute(HTML.Attribute.HREF)
    */
    protected void handleStartTagInherit(HTML.Tag tag, MutableAttributeSet a, int pos) {}

    /**
    *  Swing html parser callback method (to be overriden in subclasses).
    */
    protected void handleTextInherit(char[] data, int pos) {}

    /**
    *  Sets the name of the html parser.
    */
    public void setHtmlParserName(String nameOfHtmlParser)
    {
        this.nameOfHtmlParser = nameOfHtmlParser;
    }

    /**
    *  Gets the name of the html parser.
    */
    public String getHtmlParserName()
    {
        return nameOfHtmlParser;
    }

    /**
    *  Gets the name of the html parser. Overrides the general toString() Object method.
    */
    @Override
    public String toString()
    {
        return nameOfHtmlParser;
    }


}