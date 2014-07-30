package org.BioLayoutExpress3D.Files.Parsers;

import java.io.*;
import javax.swing.*;
import org.BioLayoutExpress3D.CoreUI.*;
import org.BioLayoutExpress3D.CoreUI.Dialogs.*;
import org.BioLayoutExpress3D.Network.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* MatrixParser is the parser class used to parse data files with the main BioLayout Express 3D application.
*
* @author Thanos Theo, 2008-2009-2010-2011
* @version 3.0.0.0
*/

public final class MatrixParser extends CoreParser
{
    private static final ImageIcon BIOLAYOUT_ICON_IMAGE_SMALL = new ImageIcon( ImageProducer.resizeImageByGivenRatio(BIOLAYOUT_ICON_IMAGE, 0.35f, true) );

    private double correlationCutOffValue = 0.5;
    private String[] namesArray = null;
    private float[][] dataArray = null;

    /**
    *  The MatrixParser class constructor for matrix files.
    */
    public MatrixParser(NetworkContainer nc, LayoutFrame layoutFrame)
    {
        super(nc, layoutFrame);
    }

    public boolean proceed()
    {
        String value = (String)JOptionPane.showInputDialog(layoutFrame, "Please select a matrix cutoff value:", "Matrix CutOff Dialog", JOptionPane.PLAIN_MESSAGE, BIOLAYOUT_ICON_IMAGE_SMALL, null, "0" + DECIMAL_SEPARATOR_STRING + "5");
        if (value != null)
        {
            value = value.replace(',', '.');
            if ( isNumeric(value) )
            {
                correlationCutOffValue = Double.parseDouble(value);

                return true;
            }
            else
                return proceed();
        }
        else
            return false;
    }

    private boolean isNumeric(String value)
    {
        try
        {
            Double.parseDouble(value);

            return true;
        }
        catch (NumberFormatException nfe)
        {
            return false;
        }
    }

    /**
    *  Parses the matrix file.
    */
    @Override
    public boolean  parse()
    {
        isSuccessful = false;

        int totalLines = 0;
        int counter = 0;
        boolean hasInitializedDataArray = false;

        LayoutProgressBarDialog layoutProgressBarDialog = layoutFrame.getLayoutProgressBar();

        try
        {
            while ( ( line = fileReaderCounter.readLine() ) != null )
                totalLines++;

            layoutProgressBarDialog.prepareProgressBar(totalLines, "Parsing " + simpleFileName + " Matrix File...");
            layoutProgressBarDialog.startProgressBar();

            String[] lineSplit = null;
            while ( ( line = fileReaderBuffered.readLine() ) != null )
            {
                if (!hasInitializedDataArray)
                {
                    namesArray = new String[totalLines - 1];
                    dataArray = new float[totalLines - 1][totalLines - 1];

                    hasInitializedDataArray = true;

                    continue; // skip first line as it contains only column nameheaders
                }

                layoutProgressBarDialog.incrementProgress(++counter);
                LayoutFrame.sleep(1);

                lineSplit = line.split("\t");
                namesArray[counter - 1] = lineSplit[0].replace("\"", "");
                for (int i = 1; i < lineSplit.length; i++)
                {
                    if (DEBUG_BUILD) print(lineSplit[i] + "\t");
                    dataArray[counter - 1][i - 1] = Float.parseFloat( lineSplit[i].replace(',', '.') );
                }
                if (DEBUG_BUILD) println();
            }

            isSuccessful = true;
        }
        catch (IOException ioe)
        {
            if (DEBUG_BUILD) println("IOException in MatrixParser.parse():\n" + ioe.getMessage());
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
                if (DEBUG_BUILD) println("IOException while closing streams in MatrixParser.parse():\n" + ioe.getMessage());
            }
            finally
            {
                layoutProgressBarDialog.endProgressBar();
            }
        }

        saveLayoutFile(totalLines, file);

        return isSuccessful;
    }

    private void saveLayoutFile(int totalLines, File file)
    {
        LayoutProgressBarDialog layoutProgressBarDialog = layoutFrame.getLayoutProgressBar();
        layoutProgressBarDialog.prepareProgressBar(totalLines, "Now Saving File...");
        layoutProgressBarDialog.startProgressBar();

        try
        {
            FileWriter fileWriter = new FileWriter( new File(IOUtils.getPrefix( file.getAbsolutePath() ) + ".layout") );
            fileWriter.write("//" + VERSION + " " + " Layout File\n");

            for (int i = 0; i < totalLines - 1; i++)
            {
                layoutProgressBarDialog.incrementProgress();
                LayoutFrame.sleep(1);

                for (int j = 0; j < totalLines - 1; j++)
                    if ( !namesArray[i].equals(namesArray[j]) )
                        if (dataArray[i][j] >= correlationCutOffValue)
                            fileWriter.write("\"" + namesArray[i] + "\"\t\"" + namesArray[j] + "\"\t" + dataArray[i][j] + "\n");
            }

            fileWriter.flush();
            fileWriter.close();
        }
        catch (IOException ioe)
        {
            if (DEBUG_BUILD) println("IOException in MatrixParser.saveLayoutFile():\n" + ioe.getMessage());
        }

        layoutProgressBarDialog.endProgressBar();
        layoutProgressBarDialog.stopProgressBar();
    }

    public double getCorrelationCutOffValue()
    {
        return correlationCutOffValue;
    }


}