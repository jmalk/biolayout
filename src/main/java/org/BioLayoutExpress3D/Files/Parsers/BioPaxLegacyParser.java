package org.BioLayoutExpress3D.Files.Parsers;

import java.io.*;
import java.util.*;
import java.util.regex.*;
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

public final class BioPaxLegacyParser extends CoreParser
{
    private int counter = 0;

    public BioPaxLegacyParser(NetworkContainer nc, LayoutFrame layoutFrame)
    {
        super(nc, layoutFrame);

        counter = 0;
    }

    @Override
    public boolean parse()
    {
        String currReaction = "";
        String currComplex = "";
        String currCat = "";
        String currParticipant = "";
        String controller = "";
        String controlled = "";
        HashMap<String, ArrayList<String>> links = new HashMap<String, ArrayList<String>>();
        HashMap<String, ArrayList<String>> cats = new HashMap<String, ArrayList<String>>();
        HashMap<String, String> participants = new HashMap<String, String>();
        Matcher matcher = null;

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

                if (line.length() > 0)
                {
                    matcher = patternMatcher(line, ".*<bp:biochemicalReaction rdf:ID=\\\"([^\\\"]+)\\\".*");
                    if ( matcher.matches() )
                        currReaction = matcher.group(1);

                    if ( patternMatcher(line, ".*<\\/bp:biochemicalReaction>.*").matches() )
                        currReaction = "";

                    if ( !currReaction.equals("") )
                    {
                        matcher = patternMatcher(line, ".*<bp:(\\S+) rdf:resource=\\\"#([^\\\"]+)\\\".*");
                        if ( matcher.matches() )
                        {
                            if ( ( matcher.group(1).equals("LEFT") ) || ( matcher.group(1).equals("LEFT") ) )
                            {
                                String type = matcher.group(1);
                                String target = matcher.group(2);
                                ArrayList<String> a = null;
                                if ( !links.containsKey(currReaction) )
                                {
                                    a = new ArrayList<String>();
                                    links.put(currReaction, a);
                                }
                                else
                                {
                                    a = links.get(currReaction);
                                }

                                a.add(type + "\t" + target);
                            }
                        }
                    }

                    matcher = patternMatcher(line, ".*<bp:complex rdf:ID=\\\"([^\\\"]+)\\\".*");
                    if ( matcher.matches() )
                        currComplex = matcher.group(1);

                    if ( patternMatcher(line, ".*<\\/bp:complex>.*").matches() )
                        currComplex = "";

                    if ( !currComplex.equals("") )
                    {
                        matcher = patternMatcher(line, ".*<bp:COMPONENTS rdf:resource=\\\"#([^\\\"]+)\\\".*");
                        if ( matcher.matches() )
                        {
                            String type = "CONTAINS";
                            String target = matcher.group(1);
                            ArrayList<String> a = null;
                            if ( !links.containsKey(currComplex) )
                            {
                                a = new ArrayList<String>();
                                links.put(currComplex, a);
                            }
                            else
                            {
                                a = links.get(currComplex);
                            }

                            a.add(type + "\t" + target);
                        }
                    }

                    matcher = patternMatcher(line, ".*<bp:catalysis rdf:ID=\\\"([^\\\"]+)\\\".*");
                    if ( matcher.matches() )
                        currCat = matcher.group(1);

                    if ( patternMatcher(line, ".*<\\/bp:catalysis>.*").matches() )
                    {
                        currCat = "";
                        controller = "";
                        controlled = "";
                    }

                    if ( !currCat.equals("") )
                    {
                        String type = "";

                        matcher = patternMatcher(line, ".*<bp:CONTROLLER rdf:resource=\\\"#([^\\\"]+)\\\".*");
                        if ( matcher.matches() )
                        {
                            type = "CONTROLLER";
                            controller = matcher.group(1);
                        }

                        matcher = patternMatcher(line, ".*<bp:CONTROLLED rdf:resource=\\\"#([^\\\"]+)\\\".*");
                        if ( matcher.matches() )
                        {
                            type = "CONTROLLED";
                            controlled = matcher.group(1);
                        }

                        matcher = patternMatcher(line, ".*<bp:CONTROL-TYPE rdf:datatype=\\\"[^\\\"]+\\\">([^<]+).*");
                        if ( matcher.matches() )
                        {
                            type = matcher.group(1);
                            ArrayList<String> a = null;
                            if ( !cats.containsKey(controller) )
                            {
                                a = new ArrayList<String>();
                                cats.put(controller, a);
                            }
                            else
                            {
                                a = cats.get(controller);
                            }

                            if (DEBUG_BUILD) println("Adding to cats:" + type + " " + controlled + " under " + controller);
                            a.add(type + "\t" + controlled);
                        }

                    }

                    matcher = patternMatcher(line, ".*<bp:\\S+Participant rdf:ID=\\\"([^\\\"]+)\\\".*");
                    if ( matcher.matches() )
                        currParticipant = matcher.group(1);

                    if ( patternMatcher(line, ".*<\\/bp:physicalEntityParticipant>.*").matches() )
                        currParticipant = "";

                    if ( !currParticipant.equals("") )
                    {
                        matcher = patternMatcher(line, ".*<bp:PHYSICAL-ENTITY rdf:resource=\\\"#([^\\\"]+)\\\".*");
                        if ( matcher.matches() )
                            participants.put(currParticipant, matcher.group(1));
                    }
                }
            }

            int count = 0;
            Set<String> keys = links.keySet();
            for (String key : keys)
            {
                if (DEBUG_BUILD) println(key);

                ArrayList<String> a = links.get(key);
                for (String entry : a)
                {
                    String type = entry.split("\t")[0];
                    String target = entry.split("\t")[1];

                    if (DEBUG_BUILD) println(key + " " + participants.get(target) + " " + type);

                    nc.addNetworkConnection(key, type + counter, 0.0f);
                    nc.addNetworkConnection(type + counter, participants.get(target), 0.0f);

                    Vertex vertexMap = nc.getVerticesMap().get(type + counter);
                    vertexMap.setVertexSize(vertexMap.getVertexSize() / 2);
                    vertexMap.setPseudoVertex();

                    LayoutClasses lc = null;
                    VertexClass vc = null;

                    if ( type.equals("CONTAINS") )
                    {
                        lc = nc.getLayoutClassSetsManager().getClassSet("COMPLEXES");
                        vc = lc.createClass(key);
                        lc.setClass(nc.getVerticesMap().get(participants.get(target)), vc);
                    }

                    lc = nc.getLayoutClassSetsManager().getClassSet(0);
                    vc = lc.createClass(type);
                    lc.setClass(nc.getVerticesMap().get(type + ++count), vc);
                }
            }

            keys = cats.keySet();
            for (String key : keys)
            {
                if (DEBUG_BUILD) println(key);

                ArrayList<String> a = cats.get(key);
                for (String entry : a)
                {
                    counter++;
                    String type = entry.split("\t")[0];
                    String target = entry.split("\t")[1];

                    if (DEBUG_BUILD) println(participants.get(key) + "\t" + target + "\t1.0\t" + type);

                    nc.addNetworkConnection(participants.get(key), target + counter, 0.0f);
                    nc.addNetworkConnection(target + counter, type, 0.0f);

                    Vertex vertexMap = nc.getVerticesMap().get(target + counter);
                    vertexMap.setVertexSize(vertexMap.getVertexSize() / 2);
                    vertexMap.setPseudoVertex();

                    LayoutClasses lc = null;
                    VertexClass vc = null;

                    lc = nc.getLayoutClassSetsManager().getClassSet(0);
                    vc = lc.createClass(target);
                    lc.setClass(nc.getVerticesMap().get(target + counter), vc);
                }
            }

            isSuccessful = true;
        }
        catch (IOException ioe)
        {
            if (DEBUG_BUILD) println("IOException in BioPaxParser.parse():\n" + ioe.getMessage());
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
                if (DEBUG_BUILD) println("IOException while closing streams in BioPaxParser.parse():\n" + ioe.getMessage());
            }
            finally
            {
                layoutProgressBarDialog.endProgressBar();
            }
        }

        return isSuccessful;
    }

    private Matcher patternMatcher(String string, String patrn)
    {
        Pattern pattern = Pattern.compile(patrn);
        Matcher matcher = pattern.matcher(string);

        return matcher;
    }


}