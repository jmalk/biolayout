package org.BioLayoutExpress3D.DocumentParsers;

import java.io.*;
import java.net.*;
import java.util.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* XmlParser is used for containing the callback methods for xml file parsing and other usefull methods.
* It uses the SAX xml parser (included in Java 1.6).
*
* @author Thanos Theo, 2008-2009
* @version 3.0.0.0
*/

public abstract class XmlParser implements org.xml.sax.ContentHandler, org.xml.sax.ErrorHandler
{

    /**
    *  Variable to store the name of the xml parser. Will be returned with the overriden toString() method.
    */
    protected String nameOfXmlParser = "";

    /**
    *  The constructor of this class.
    */
    public XmlParser() {}

    /**
    *  Initialize data structures method (to be overriden in subclasses).
    */
    protected void initDataStructures() {}

    /**
    *  Initializes the xml parser classes.
    */
    private XMLReader makeXMLParser(boolean internallyValidateXMLFile) throws Exception
    {
        if (DEBUG_BUILD) println("Internal XML validation is: " + internallyValidateXMLFile);

        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

        if (internallyValidateXMLFile)
        {
            saxParserFactory.setValidating(true);
            saxParserFactory.setNamespaceAware(true);
        }

        SAXParser saxParser = saxParserFactory.newSAXParser();

        if (internallyValidateXMLFile) saxParser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");

        XMLReader parser = saxParser.getXMLReader();

        if (internallyValidateXMLFile)
        {
            parser.setFeature("http://xml.org/sax/features/namespaces", true);
            parser.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
            parser.setErrorHandler(this);
        }

        return parser;
    }

    /**
    *  Parses a xml file from an ArrayList containing Strings for each line.
    */
    public boolean parseFromArrayList(ArrayList<String> arraylist, boolean internallyValidateXMLFile)
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

        if (DEBUG_BUILD) println("\nStarting xml parsing from arraylist");

        try
        {
            XMLReader parser = makeXMLParser(internallyValidateXMLFile);
            parser.setContentHandler(this);

            BufferedReader br = new BufferedReader( new StringReader(arraylistContent) );
            parser.parse( new InputSource(br) );
            br.close();

            isSuccessful = true;
        }
        catch (IOException ioexc)
        {
            if (DEBUG_BUILD) println("IOException with the parseFromArrayList() method:\n" + ioexc.getMessage());
        }
        catch (Exception exc)
        {
            if (DEBUG_BUILD) println("Exception with the parseFromArrayList() method:\n" + exc.getMessage());
        }

        if (DEBUG_BUILD) println("Finished xml parsing from arraylist\n");

        return isSuccessful;
    }

    /**
    *  Parses a xml file from a url (or a local jar resource).
    */
    public boolean parseFromUrl(URL url, boolean internallyValidateXMLFile)
    {
        boolean isSuccessful = false;

        initDataStructures();

        if (DEBUG_BUILD) println("\nStarting xml parsing from url: " + url.toString());

        try
        {
            XMLReader parser = makeXMLParser(internallyValidateXMLFile);
            parser.setContentHandler(this);

            BufferedReader br = new BufferedReader( new InputStreamReader( url.openStream() ) );
            parser.parse( new InputSource(br) );
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
        catch (Exception exc)
        {
            if (DEBUG_BUILD) println("Exception with the parseFromUrl() method:\n" + exc.getMessage());
        }

        if (DEBUG_BUILD) println("Finished xml parsing from url: " + url.toString() + "\n");

        return isSuccessful;
    }

    /**
    *  Parses a xml file from a local file.
    */
    public boolean parseFromFile(String fileName, boolean internallyValidateXMLFile)
    {
        boolean isSuccessful = false;

        initDataStructures();

        if (DEBUG_BUILD) println("\nStarting xml parsing from file: " + fileName);

        try
        {
            XMLReader parser = makeXMLParser(internallyValidateXMLFile);
            parser.setContentHandler(this);

            BufferedReader br = new BufferedReader( new FileReader(fileName) );
            parser.parse( new InputSource(br) );
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
        catch (Exception exc)
        {
            if (DEBUG_BUILD) println("Exception with the parseFromFile() method:\n" + exc.getMessage());
        }

        if (DEBUG_BUILD) println("Finished xml parsing from file: " + fileName + "\n");

        return isSuccessful;
    }

    /**
    *  Parses a xml file from a http url connection.
    */
    public boolean parseFromHttpUrlConnection(HttpURLConnection conn, boolean internallyValidateXMLFile)
    {
        boolean isSuccessful = false;

        if (conn != null)
        {
            initDataStructures();

            if (DEBUG_BUILD) println("\nStarting xml parsing from http url connection: " + conn.toString());

            try
            {
                XMLReader parser = makeXMLParser(internallyValidateXMLFile);
                parser.setContentHandler(this);

                BufferedReader br = new BufferedReader( new InputStreamReader( conn.getInputStream() ) );
                parser.parse( new InputSource(br) );
                br.close();

                isSuccessful = true;
            }
            catch (IOException ioexc)
            {
                if (DEBUG_BUILD) println("IOException with the parseFromHttpUrlConnection() method:\n" + ioexc.getMessage());
            }
            catch (Exception exc)
            {
                if (DEBUG_BUILD) println("Exception with the parseFromHttpUrlConnection() method:\n" + exc.getMessage());
            }

            if (DEBUG_BUILD) println("Finished xml parsing from http url connection: " + conn.toString() + "\n");

            conn.disconnect();
        }
        else
        {
            if (DEBUG_BUILD) println("Null HttpURLConnection provided!");
        }

        return isSuccessful;
    }

    /**
    *  SAX callback method.
    */
    @Override
    public void startDocument() throws SAXException
    {
        startDocumentInherit();
    }

    /**
    *  SAX callback method.
    */
    @Override
    public void endDocument() throws SAXException
    {
        endDocumentInherit();
    }

    /**
    *  SAX callback method.
    */
    @Override
    public void startElement(String namespace, String localname, String type, Attributes attributes) throws SAXException
    {
        startElementInherit(namespace, localname, type, attributes);
    }

    /**
    *  SAX callback method.
    */
    @Override
    public void endElement(String namespace, String localname, String type) throws SAXException
    {
        endElementInherit(namespace, localname, type);
    }

    /**
    *  SAX callback method.
    */
    @Override
    public void characters(char[] ch, int start, int len)
    {
        charactersInherit(ch, start, len);
    }

    /**
    *  SAX callback method.
    */
    @Override
    public void endPrefixMapping(String prefix) throws SAXException
    {
        endPrefixMappingInherit(prefix);
    }

    /**
    *  SAX callback method.
    */
    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException
    {
        ignorableWhitespaceInherit(ch, start, length);
    }

    /**
    *  SAX callback method.
    */
    @Override
    public void processingInstruction(String target, String data) throws SAXException
    {
        processingInstructionInherit(target, data);
    }

    /**
    *  SAX callback method.
    */
    @Override
    public void setDocumentLocator(Locator locator)
    {
        setDocumentLocatorInherit(locator);
    }

    /**
    *  SAX callback method.
    */
    @Override
    public void skippedEntity(String name) throws SAXException
    {
        skippedEntityInherit(name);
    }

    /**
    *  SAX callback method.
    */
    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException
    {
        startPrefixMappingInherit(prefix, uri);
    }

    /**
    *  ErrorHandler callback method.
    */
    @Override
    public void error(SAXParseException exception)
    {
        errorInherit(exception);
    }

    /**
    *  ErrorHandler callback method.
    */
    @Override
    public void fatalError(SAXParseException exception)
    {
        fatalErrorInherit(exception);
    }

    /**
    *  ErrorHandler callback method.
    */
    @Override
    public void warning(SAXParseException exception)
    {
        warningInherit(exception);
    }

    /**
    *  SAX inherit callback method (to be overriden in subclasses).
    */
    protected void startDocumentInherit() throws SAXException {}

    /**
    *  SAX inherit callback method (to be overriden in subclasses).
    */
    protected void endDocumentInherit() throws SAXException {}

    /**
    *  SAX inherit callback method (to be overriden in subclasses).
    */
    protected void startElementInherit(String namespace, String localname, String type, Attributes attributes)  throws SAXException {}

    /**
    *  SAX inherit callback method (to be overriden in subclasses).
    */
    protected void endElementInherit(String namespace, String localname, String type) throws SAXException {}

    /**
    *  SAX inherit callback method (to be overriden in subclasses).
    */
    protected void charactersInherit(char[] ch, int start, int len) {}

    /**
    *  SAX inherit callback method (to be overriden in subclasses).
    */
    protected void endPrefixMappingInherit(String prefix) throws SAXException {}

    /**
    *  SAX inherit callback method (to be overriden in subclasses).
    */
    protected void ignorableWhitespaceInherit(char[] ch, int start, int length) throws SAXException {}

    /**
    *  SAX inherit callback method (to be overriden in subclasses).
    */
    protected void processingInstructionInherit(String target, String data) throws SAXException {}

    /**
    *  SAX inherit callback method (to be overriden in subclasses).
    */
    protected void setDocumentLocatorInherit(Locator locator) {}

    /**
    *  SAX inherit callback method (to be overriden in subclasses).
    */
    protected void skippedEntityInherit(String name) throws SAXException {}

    /**
    *  SAX inherit callback method (to be overriden in subclasses).
    */
    protected void startPrefixMappingInherit(String prefix, String uri) throws SAXException {}

    /**
    *  ErrorHandler inherit callback method (to be overriden in subclasses).
    */
    protected void errorInherit(SAXParseException exception) {}

    /**
    *  ErrorHandler inherit callback method (to be overriden in subclasses).
    */
    protected void fatalErrorInherit(SAXParseException exception) {}

    /**
    *  ErrorHandler inherit callback method (to be overriden in subclasses).
    */
    protected void warningInherit(SAXParseException exception) {}

    /**
    *  Sets the name of the xml parser.
    */
    public void setXmlParserName(String nameOfXmlParser)
    {
        this.nameOfXmlParser = nameOfXmlParser;
    }

    /**
    *  Gets the name of the xml parser.
    */
    public String getXmlParserName()
    {
        return nameOfXmlParser;
    }

    /**
    *  Gets the name of the xml parser. Overrides the general toString() Object method.
    */
    @Override
    public String toString()
    {
        return nameOfXmlParser;
    }


}