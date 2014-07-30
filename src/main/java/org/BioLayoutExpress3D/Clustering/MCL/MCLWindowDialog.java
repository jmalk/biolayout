package org.BioLayoutExpress3D.Clustering.MCL;


import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.Graph.*;
import org.BioLayoutExpress3D.Network.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import org.BioLayoutExpress3D.Environment.DataFolder;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* @author Anton Enright, full refactoring by Thanos Theo, 2008-2009
* @version 3.0.0.0
*
*/

final class MCLWindowDialog extends JDialog implements Runnable, ActionListener // package access
{
    /**
    *  Serial version UID variable for the MCLWindow class.
    */
    public static final long serialVersionUID = 111222333444555669L;

    private static final String MCL_TITLE = "MCL Graph Clustering";
    private static final int COLOR_UPPER_RANGE = (1 << 8) - 1; // 255
    private static final int[] COLOR_PRIME_NUMBERS = org.BioLayoutExpress3D.StaticLibraries.Math.findPrimeNumbersUpToThreshold(COLOR_UPPER_RANGE);
    private static final int COLOR_LOOKUP_THRESHOLD = 1000;
    private int primeNumberIndex1 = 0;
    private int primeNumberIndex2 = 0;
    private int primeNumberIndex3 = 0;

    private LayoutFrame layoutFrame = null;
    private Graph graph = null;
    private File fileInput = null;
    private Vertex[] vertexIDs = null;
    private Document MCL_text = null;
    private JTextArea textArea = null;
    private boolean cancelMCLThread = false;
    private JButton cancelMCLThreadButton = null;

    public MCLWindowDialog(LayoutFrame layoutFrame, Graph graph, File fileInput)
    {
        super(layoutFrame, MCL_TITLE, false);

        this.layoutFrame = layoutFrame;
        this.graph = graph;
        this.fileInput = fileInput;

        cancelMCLThread = false;

        initComponents();
    }

    private void initComponents()
    {
        JPanel test   = new JPanel(true);
        JPanel top    = new JPanel(true);
        JPanel bottom = new JPanel(true);

        cancelMCLThreadButton = new JButton("Cancel Clustering");
        cancelMCLThreadButton.addActionListener(this);
        cancelMCLThreadButton.setToolTipText("Cancel Clustering");

        MCL_text = new PlainDocument();
        cancelMCLThread = false;
        test.setLayout( new BorderLayout() );
        bottom.add(cancelMCLThreadButton);

        textArea = new JTextArea(MCL_text);
        textArea.setFont( new Font("Monospaced", 0, 10) );
        textArea.setAutoscrolls(true);
        textArea.setEditable(false);

        JScrollPane scrollpane = new JScrollPane(textArea);
        scrollpane.setPreferredSize( new Dimension(600, 400) );

        test.add(scrollpane, BorderLayout.CENTER);
        test.add(top, BorderLayout.NORTH);
        test.add(bottom, BorderLayout.SOUTH);

        if ( !layoutFrame.getFileNameLoaded().isEmpty() ) // check if a file has been loaded before block/unblock so as not to enable menu/toolbar items
            layoutFrame.block();

        this.addWindowListener( new WindowAdapter()
        {
           @Override
            public void windowClosing(WindowEvent e)
            {
                layoutFrame.unblock();
                closeMCLWindow();
            }
        } );
        this.getContentPane().add(test,BorderLayout.CENTER);
        this.pack();
        this.setResizable(false);
        this.setSize( new Dimension(500, 500) );
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void createMCLHeader()
    {
        try
        {
            OutputStreamWriter fosw = new OutputStreamWriter( new FileOutputStream(fileInput) );

            NetworkContainer nc = layoutFrame.getNetworkRootContainer();
            //setting header info
            int numberOfVertices = nc.getNumberOfVertices();
            int value = 0;
            String command = "";
            vertexIDs = new Vertex[numberOfVertices];

            fosw.write("(mclheader\nmcltype matrix\ndimensions " + numberOfVertices + "x" + numberOfVertices + "\n)\n");
            fosw.write("(\n(mclmatrix\nbegin\n");

            for ( Vertex vertex : nc.getVertices() )
            {
                value = vertex.getVertexID();
                vertexIDs[value] = vertex;
                fosw.write(value + " ");

                HashMap<Vertex, Edge> edgeConnection = vertex.getEdgeConnectionsMap();
                for ( Vertex vertex2 : edgeConnection.keySet() )
                {
                    value = vertex2.getVertexID();
                    // putting also Petri Net case that is a graphml file but does enable weights for proper red edge inhibitor renderering
                    command = ( WEIGHTED_EDGES && !nc.getIsPetriNet() ) ? ( value + ":" + edgeConnection.get(vertex2).getWeight() + " ") : (value + " ");
                    fosw.write(command);
                }

                fosw.write("$\n");
            }

            fosw.write(")\n");
            fosw.flush();
            fosw.close();
        }
        catch (IOException ioExc)
        {
            if (DEBUG_BUILD) println("Exception in createMCLHeader():\n" + ioExc.getMessage());
        }
    }

    private String[] createMCLcommand()
    {
            String[] temp_MCL_commands = null;
            String[] MCL_commands = {
                                      new File( DataFolder.get(), CopyMCLExecutable.EXTRACT_TO_MCL_FILE_PATH + "mcl" + ( (IS_WIN) ? ".exe" : "" ) ).getAbsolutePath(),
                                      fileInput.getAbsolutePath(),
                                      "-I", Float.toString( MCL_INFLATION_VALUE.get() )
                                    };

            if (MCL_PRE_INFLATION_VALUE.get() > 0.0)
            {
                temp_MCL_commands = new String[] { "-pi", Float.toString( MCL_PRE_INFLATION_VALUE.get() ) };
                MCL_commands = Utils.mergeArrays(MCL_commands, temp_MCL_commands);
            }

            temp_MCL_commands = new String[] { "-scheme", Integer.toString( MCL_SCHEME.get() ) };
            MCL_commands = Utils.mergeArrays(MCL_commands, temp_MCL_commands);

            if ( USE_MULTICORE_PROCESS && USE_MCL_N_CORE_PARALLELISM.get() )
            {
                temp_MCL_commands = new String[] { "-t", Integer.toString(NUMBER_OF_AVAILABLE_PROCESSORS) };
                MCL_commands = Utils.mergeArrays(MCL_commands, temp_MCL_commands);
            }

            /*
            if ( MCL_ADAPT_LOCAL.get() )
            {
                temp_MCL_commands = new String[] { "--adapt-local" };
                MCL_commands = Utils.mergeArrays(MCL_commands, temp_MCL_commands);
            }

            if ( MCL_ADAPT_SMOOTH.get() )
            {
                temp_MCL_commands = new String[] { "--adapt-smooth" };
                MCL_commands = Utils.mergeArrays(MCL_commands, temp_MCL_commands);
            }
            */

            if ( !MCL_ADVANCED_OPTIONS.get().isEmpty() )
            {
                temp_MCL_commands = MCL_ADVANCED_OPTIONS.get().split("\\s+");
                MCL_commands = Utils.mergeArrays(MCL_commands, temp_MCL_commands);
            }

            temp_MCL_commands = new String[] { "-dump", "cat,lines", "-o", "-" };
            MCL_commands = Utils.mergeArrays(MCL_commands, temp_MCL_commands);

            String full_MCL_command = "";
            for (String command : MCL_commands)
                if ( !command.isEmpty() )
                    full_MCL_command += " " + command;
            full_MCL_command = full_MCL_command.trim();
            appendText("\nNow starting " + ( ( USE_MULTICORE_PROCESS && USE_MCL_N_CORE_PARALLELISM.get() ) ? NUMBER_OF_AVAILABLE_PROCESSORS + "-Core " : " " ) + "MCL process with command:\n[" + full_MCL_command + "]\n\n");
            if (DEBUG_BUILD) println(full_MCL_command);

            return MCL_commands;
    }

    private void runMCL()
    {
        BufferedReader br = null;

        try
        {
            // Runtime runtime = Runtime.getRuntime();
            // has to use the exec(String[]) method instead of the old exec(String) so as to avoid the Tokenizer of the second one omitting all whitespaces in file paths!!!
            // Process MCL_process = runtime.exec( createMCLcommand() );
            Process MCL_process = new ProcessBuilder( createMCLcommand() ).start();
            br = new BufferedReader( new InputStreamReader( MCL_process.getInputStream() ) );
            MCLStreamGrabber errorGrabberMCL = new MCLStreamGrabber( MCL_process.getErrorStream() );
            errorGrabberMCL.start(this);

            String line = "";
            int parsingIteration = 0;
            int parsingResult = 0;
            int classNumber = 0;
            String[] param = null;
            int index1 = 0;
            int index2 = 0;
            float weight = 0.0f;
            Vertex vertex1 = null;
            Vertex vertex2 = null;
            Edge edge1 = null;
            Edge edge2 = null;
            ArrayList<String[]> MCL_paramList = new ArrayList<String[]>();
            while ( ( line = br.readLine() ) != null && !cancelMCLThread )
            {
               param = line.split("\\s+");

               if ( param[0].equals(")") )
               {
                   parsingIteration = 0;
                   parsingResult = 0;
               }

               if (parsingIteration == 1)
               {
                   // PULLS OUT EDGE WEIGHTS FROM MCL ITERANDS
                   index1 = Integer.parseInt(param[0]);
                   index2 = Integer.parseInt(param[1]);
                   weight = Float.parseFloat(param[2]);
                   vertex1 = vertexIDs[index1];
                   vertex2 = vertexIDs[index2];

                   if ( (vertex1 != null) && (vertex2 != null) )
                   {
                       edge1 = vertex1.getEdgeConnectionsMap().get(vertex2);
                       edge2 = vertex2.getEdgeConnectionsMap().get(vertex1);
                   }

                   if (edge1 != null)
                       edge1.setScaledWeight(weight);

                   if (edge2 != null)
                       edge2.setScaledWeight(weight);
               }

               if (parsingResult == 1)
               {
                   //EXTRACTS CLUSTERS TO A TEMPORARY BUFFER
                   classNumber++;
                   MCL_paramList.add(param);
               }

               if ( param[0].equals("(mcldump") )
               {
                   // SPOTS WHICH TYPE OF OUTPUT WE ARE READING
                   if ( param[1].equals("ite") )
                   {
                       parsingIteration = 1;
                   }

                   if ( param[1].equals("result") )
                   {
                       parsingResult = 1;
                   }
               }
            }

            if (!cancelMCLThread)
            {
                clearClassesAndCreateClassSet();

                LayoutClasses layoutClasses = layoutFrame.getNetworkRootContainer().getLayoutClassSetsManager().getCurrentClassSetAllClasses();
                layoutClasses.clearClasses();

                createAllLayoutClasses(layoutClasses, classNumber, MCL_paramList);
            }
        }
        catch (IOException ioExc)
        {
            if (DEBUG_BUILD) println("IOException in runMCL():\n" + ioExc.getMessage());
        }
        finally
        {
            try
            {
                if (br != null) br.close();
            }
            catch (IOException ioe)
            {
                if (DEBUG_BUILD) println("IOException while closing stream in runMCL():\n" + ioe.getMessage());
            }
            finally
            {

            }
        }
    }

    private void clearClassesAndCreateClassSet()
    {
        String classSetName = "MCL_" + MCL_INFLATION_VALUE.get() + "_" + MCL_SCHEME.get();

        if ( !layoutFrame.getNetworkRootContainer().getLayoutClassSetsManager().classSetExists(classSetName) )
            layoutFrame.getNetworkRootContainer().getLayoutClassSetsManager().createNewClassSet(classSetName);

        layoutFrame.getNetworkRootContainer().getLayoutClassSetsManager().getClassSet(classSetName).clearClasses();
        layoutFrame.getNetworkRootContainer().getLayoutClassSetsManager().switchClassSet(classSetName);
    }

    private void createAllLayoutClasses(LayoutClasses layoutClasses, int classNumber, ArrayList<String[]> MCL_paramList)
    {
        // prime numbers color generator values
        primeNumberIndex1 =  3; // start with prime number   7
        primeNumberIndex2 = 18; // start with prime number  67
        primeNumberIndex3 = 27; // start with prime number 107
        int[] colorIndicesRGB = null;
        HashSet<Color> clusterColors = null;
        Color clusterColor = null;
        int sameColours = 0;
        int colorRange = VertexClass.UPPER_THRESHOLD - VertexClass.LOWER_THRESHOLD;
        int RGBColorCombinations = colorRange * colorRange * colorRange;
        if ( !MCL_ASSIGN_RANDOM_CLUSTER_COLOURS.get() )
        {
            colorIndicesRGB = new int[]{ (VertexClass.UPPER_THRESHOLD - VertexClass.LOWER_THRESHOLD) / 2 + COLOR_PRIME_NUMBERS[primeNumberIndex1], (VertexClass.UPPER_THRESHOLD - VertexClass.LOWER_THRESHOLD) / 4 + COLOR_PRIME_NUMBERS[primeNumberIndex2], (VertexClass.UPPER_THRESHOLD - VertexClass.LOWER_THRESHOLD) / 2 + COLOR_PRIME_NUMBERS[primeNumberIndex3] };
            clusterColors = new HashSet<Color>();
        }

        //BUILDS CLASSES FROM MCL RESULT FROM TEMPORARY BUFFER
        int maxIntegerCharacters = Integer.toString(classNumber).length();
        String zerosToPut = "";
        int prevIntegerCharacters = 0;
        int classNumberIndex = 0;
        int currentIntegerCharacters = 0;
        for (String[] MCL_param : MCL_paramList)
        {
            currentIntegerCharacters = Integer.toString(++classNumberIndex).length();
            if (currentIntegerCharacters > prevIntegerCharacters)
            {
                zerosToPut = "";
                for (int i = 0; i < (maxIntegerCharacters - currentIntegerCharacters); i++)
                    zerosToPut += "0";
            }
            prevIntegerCharacters = currentIntegerCharacters;

            if ( !MCL_ASSIGN_RANDOM_CLUSTER_COLOURS.get() )
            {
                sameColours = 0;
                clusterColor = createColorBasedOnPrimeNumbers(colorIndicesRGB, false);
                while (clusterColors.contains(clusterColor) && clusterColors.size() <= RGBColorCombinations)
                {
                    clusterColor = createColorBasedOnPrimeNumbers(colorIndicesRGB, true);
                    if (++sameColours > COLOR_LOOKUP_THRESHOLD)
                    {
                        sameColours = 0;
                        clusterColor = VertexClass.createRandomColor();
                    }
                }
                clusterColors.add(clusterColor);
                layoutClasses.createClass(classNumberIndex, clusterColor, "Cluster" + zerosToPut + classNumberIndex);
            }
            else
                layoutClasses.createClass(classNumberIndex, "Cluster" + zerosToPut + classNumberIndex);

            // IF WE WANT: SET MEMBERS IN SMALL CLUSTERS TO NO CLASS
            for (int i = 0; i < MCL_param.length; i++)
                layoutClasses.setClass(vertexIDs[new Integer(MCL_param[i])], ( MCL_param.length > MCL_SMALLEST_CLUSTER.get() ) ? classNumberIndex : 0);
        }
    }

    /**
    *  Creates colors based on a prime number generator, same color hues reproducible for every run.
    *  Verified for 6023986 uniquely generated colors.
    */
    private Color createColorBasedOnPrimeNumbers(int[] colorIndicesRGB, boolean incrementPrimeNumberIndices)
    {
        if (incrementPrimeNumberIndices)
        {
            int primeNumbersIndexLength = (int)(VertexClass.COLOR_UPPER_INTENSITY_PERCENTAGE * COLOR_PRIME_NUMBERS.length);
            if (++primeNumberIndex1 == primeNumbersIndexLength)
                primeNumberIndex1 = 0;
            if (++primeNumberIndex2 == primeNumbersIndexLength)
                primeNumberIndex2 = 0;
            if (++primeNumberIndex3 == primeNumbersIndexLength)
                primeNumberIndex3 = 0;
        }

        colorIndicesRGB[0] += COLOR_PRIME_NUMBERS[primeNumberIndex1];
        colorIndicesRGB[1] += COLOR_PRIME_NUMBERS[primeNumberIndex2];
        colorIndicesRGB[2] += COLOR_PRIME_NUMBERS[primeNumberIndex3];

        if (colorIndicesRGB[0] >= VertexClass.UPPER_THRESHOLD)
            colorIndicesRGB[0] = VertexClass.LOWER_THRESHOLD + (colorIndicesRGB[0] - VertexClass.UPPER_THRESHOLD);
        if (colorIndicesRGB[1] >= VertexClass.UPPER_THRESHOLD)
            colorIndicesRGB[1] = VertexClass.LOWER_THRESHOLD + (colorIndicesRGB[1] - VertexClass.UPPER_THRESHOLD);
        if (colorIndicesRGB[2] >= VertexClass.UPPER_THRESHOLD)
            colorIndicesRGB[2] = VertexClass.LOWER_THRESHOLD + (colorIndicesRGB[2] - VertexClass.UPPER_THRESHOLD);

        return new Color(colorIndicesRGB[0], colorIndicesRGB[1], colorIndicesRGB[2]);
    }

    public void appendText(String text)
    {
        try
        {
            MCL_text.insertString(MCL_text.getLength(), text, null);
            textArea.setCaretPosition( MCL_text.getLength() );
        }
        catch (Exception exc)
        {
            if (DEBUG_BUILD) println("Exception in appendText(): " + text + "\n" + exc.getMessage());
        }
    }

    @Override
    public void run()
    {
        if (graph.getGraphNodes().size() >= 1)
        {
           this.setTitle( MCL_TITLE + ( ( USE_MULTICORE_PROCESS && USE_MCL_N_CORE_PARALLELISM.get() ) ? " (Utilizing " + NUMBER_OF_AVAILABLE_PROCESSORS + "-Core Parallelism)" : "" ) );

           appendText("Creating MCL Header: ");
           createMCLHeader();

           appendText("Done. ");
           runMCL();

           if (!cancelMCLThread)
           {
             graph.rebuildGraph();
             layoutFrame.getClassViewerFrame().refreshCurrentClassSetSelection();
             layoutFrame.getClassViewerFrame().populateClassViewer(null, false, true, true);
             cancelMCLThreadButton.setText("Close Window");
             cancelMCLThreadButton.setToolTipText("Close Window");
             vertexIDs = null;

             this.repaint();
           }
        }
        else
        {
           appendText("No Graph Loaded!");
        }

        if ( !layoutFrame.getFileNameLoaded().isEmpty() ) // check if a file has been loaded before block/unblock so as not to enable menu/toolbar items
            layoutFrame.unblock();
    }

    public boolean isMCLWindowVisible()
    {
        return this.isVisible();
    }

    public void MCLWindowToFront()
    {
        this.toFront();
    }

    private void closeMCLWindow()
    {
        cancelMCLThread = true;
        fileInput.delete();

        this.setVisible(false);
        this.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if ( e.getSource().equals(cancelMCLThreadButton) )
        {
            closeMCLWindow();
        }
    }


}