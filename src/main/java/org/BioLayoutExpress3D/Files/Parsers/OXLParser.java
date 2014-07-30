package org.BioLayoutExpress3D.Files.Parsers;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import javax.xml.stream.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.CoreUI.Dialogs.*;
import org.BioLayoutExpress3D.Network.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
 * Parsing of OXL Ondex files based on Stax parsing principle. Uses CoreParser as the base class.
 *
 * @author Jan Taubert, Thanos Theo 2008-2009-2010-2011
 *
 */
public class OXLParser extends CoreParser
{

    /**
    *  The constructor of the OXLParser class.
    */
	public OXLParser(NetworkContainer nc, LayoutFrame layoutFrame)
    {
        super(nc, layoutFrame);
    }

    /**
    *  Checks if the file actually exists.
    */
    @Override
    public boolean init(File file, String fileExtension)
    {
        this.file = file;

        try
        {
            // OXL files might be .xml or .xml.gz
            if ( fileExtension.endsWith(".xml.gz") )
            {
                fileReaderCounter  = new BufferedReader( new InputStreamReader(	new GZIPInputStream( new FileInputStream(file) ) ) );
                fileReaderBuffered = new BufferedReader( new InputStreamReader(	new GZIPInputStream( new FileInputStream(file) ) ) );
            }
            else
            {
                fileReaderCounter  = new BufferedReader( new FileReader(file) );
                fileReaderBuffered = new BufferedReader( new FileReader(file) );
            }

            simpleFileName = file.getName();

            return true;
        }
        catch (IOException ioExc)
        {
            try
            {
                fileReaderCounter.close();
                fileReaderBuffered.close();
            }
            catch (IOException ioe)
            {
                if (DEBUG_BUILD) println("IOException while closing streamers in OXLParser.init():\n" + ioExc.getMessage());
            }
            finally
            {

            }

            return false;
        }
    }

    /**
    *  Parses the ondex xml file.
    */
    @Override
    public boolean parse()
    {
        int totalLines = 0;
        int counter = 0;

        isSuccessful = false;
        nc.setOptimized(false);
        LayoutProgressBarDialog layoutProgressBarDialog = layoutFrame.getLayoutProgressBar();

        try
        {
            while ( ( line = fileReaderCounter.readLine() ) != null )
                totalLines++;

            layoutProgressBarDialog.prepareProgressBar(totalLines, "Parsing " + simpleFileName + " Map...");

            // get all data structures involved with vertex
            HashMap<String, Vertex> verticesMap = nc.getVerticesMap();

            // parsing of XML document via Stax
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLStreamReader xmlStreamReader = inputFactory.createXMLStreamReader(fileReaderBuffered);

            // same state variables to simplify keeping track where we are in the OXL
            boolean inConcepts = false;
            String conceptId = "";
            String local = "";
            String annotation = "";
            String conceptClass = "";
            String name = "";
            String preferred = "";
            String from = "";
            String to = "";
            String type = "";

            while ( xmlStreamReader.hasNext() )
            {
                layoutProgressBarDialog.incrementProgress(++counter);

                // start of a new element
                if (xmlStreamReader.next() == XMLStreamReader.START_ELEMENT)
                {
                    // the local name of a QName
                    local = xmlStreamReader.getName().getLocalPart();

                    // now parsing list of concepts
                    // <concepts>
                    if ( local.equals("concepts") )
                    {
                	inConcepts = true;
                    }
                    // now parsing list of relations
                    // <relations>
                    else if ( local.equals("relations") )
                    {
                	inConcepts = false;
                    }
                    // at beginning of new concept element
                    // <concept>
                    else if ( inConcepts && local.equals("concept") )
                    {
    	            	// <id>1</id>
    	            	xmlStreamReader.next();
    	            	xmlStreamReader.next();

    	                conceptId = xmlStreamReader.getText();

    	                if (DEBUG_BUILD) println("Creating vertex with id " + conceptId);

    	                // create BioLayout vertex
    	                verticesMap.put( conceptId, new Vertex(conceptId, nc) );
                    }
                    // annotation belonging to a concept, can be empty
                    // <annotation>some short text</annotation>
                    else if ( inConcepts && local.equals("annotation") )
                    {
                        // try to get text element
                        if (xmlStreamReader.next() == XMLStreamReader.CHARACTERS)
                        {
                            annotation = xmlStreamReader.getText();

                            if (DEBUG_BUILD) println("Desc for vertex with id " + conceptId + " = " + annotation);

                            verticesMap.get(conceptId).setDescription(annotation);
                        }
                    }
                    // beginning of concept class element
                    // <ofType>
                    else if ( inConcepts && local.equals("ofType") )
                    {
    	            	// <id>Thing</id>
    	            	xmlStreamReader.next();
    	            	xmlStreamReader.next();

    	            	conceptClass = xmlStreamReader.getText();

    	            	if (DEBUG_BUILD) println("Class for vertex with id " + conceptId + " = " + conceptClass);

                    // TODO: how to handle classes?
                    }
                    // there could be multiple concept names
                    // <conames><concept_name>
                    else if ( inConcepts && local.equals("concept_name") )
                    {
                        // <name>A shorter</name>
                        xmlStreamReader.next();
    	            	xmlStreamReader.next();

    	            	name = xmlStreamReader.getText();

    	            	// <isPreferred>true</isPreferred>
    	            	xmlStreamReader.next();
    	            	xmlStreamReader.next();
    	            	xmlStreamReader.next();

    	            	// only add preferred names as the vertex name for now
    	            	// if multiple preferred names, last one is chosen
    	            	preferred = xmlStreamReader.getText();
    	            	if ( preferred.equals("true") )
                        {
                            if (DEBUG_BUILD) println("Name for vertex with id " + conceptId + " = " + name);

                            // TODO: not so nice to have name and description mix up, better introduce a vertex id distinct from name
                            verticesMap.get(conceptId).setDescription(name);
    	            	}
                    }
                    // parse from and to concept of a relation
                    // <relation>
                    else if ( !inConcepts && local.equals("relation") )
                    {
                        // <fromConcept>5</fromConcept>
                        xmlStreamReader.next();
    	            	xmlStreamReader.next();

    	            	from = xmlStreamReader.getText();

    	            	// <toConcept>3</toConcept>
    	            	xmlStreamReader.next();
    	            	xmlStreamReader.next();
    	            	xmlStreamReader.next();

    	            	to = xmlStreamReader.getText();

    	            	// search for next <ofType><id>is_a</id>
    	            	while (true)
                            if ( (xmlStreamReader.next() == XMLStreamReader.START_ELEMENT) && xmlStreamReader.getName().getLocalPart().equals("ofType") )
                                    break;

    	            	xmlStreamReader.next();
    	            	xmlStreamReader.next();

    	            	type = xmlStreamReader.getText();

    	            	if (DEBUG_BUILD) println("Relation for vertex from " + from + " to " + to + " of type " + type);

    	            	// TODO: How to handle different types of relations
    	            	nc.addNetworkConnection(from, to, 0.0f);
                    }
                }
            }

            isSuccessful = true;
        }
        catch (IOException ioe)
        {
            if (DEBUG_BUILD) println("IOException in OXLParser.parse():\n" + ioe.getMessage());
        }
        catch (XMLStreamException e)
        {
        	if (DEBUG_BUILD) println("XMLStreamException in OXLParser.parse():\n" + e.getMessage());
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
                if (DEBUG_BUILD) println("IOException while closing streamers in OXLParser.parse():\n" + ioe.getMessage());
            }
            finally
            {
                layoutProgressBarDialog.endProgressBar();
            }
        }

        return isSuccessful;
    }


}