package org.BioLayoutExpress3D.Utils;

import java.io.*;
import java.util.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
 * Parse a file that represents a matrix whose values are delimited by a given regular expression.
 *
 * @author Tim Angus <tim.angus@roslin.ed.ac.uk>
 */
public class TextDelimitedMatrix
{
    private File file;
    private int numLines;
    private String delimiterRegex;
    private int maxColumns;
    private int maxRows;

    private List<String> data;
    private int numColumns;
    private int numRows;
    private boolean transpose;
    private boolean unparsedColumns;
    private boolean unparsedRows;

    public interface ProgressIndicator
    {
        void notify(int percent);
    }

    private ProgressIndicator progressIndicator;

    public TextDelimitedMatrix(File file, String delimiterRegex, ProgressIndicator progressIndicator) throws IOException
    {
        this(file, delimiterRegex, 0, 0);
        this.progressIndicator = progressIndicator;
    }

    public TextDelimitedMatrix(File file, String delimiterRegex, int maxColumns, int maxRows) throws IOException
    {
        this.file = file;
        this.delimiterRegex = delimiterRegex;
        this.maxColumns = maxColumns;
        this.maxRows = maxRows;

        LineNumberReader lnr = new LineNumberReader(new FileReader(file));
        lnr.skip(Long.MAX_VALUE);
        numLines = lnr.getLineNumber();

        data = new ArrayList<String>();
        numColumns = 0;
        numRows = 0;
        transpose = false;
        progressIndicator = null;
    }

    public boolean parse()
    {
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            int column = 0;
            int row = 0;
            int columnCount = -1;
            int linesRead = 0;

            while (((line = reader.readLine()) != null) && (maxRows <= 0 || row < maxRows))
            {
                linesRead++;

                if (progressIndicator != null)
                {
                    progressIndicator.notify(linesRead * 100 / numLines);
                }

                String[] split = line.split(delimiterRegex);

                if (columnCount >= 0 && split.length != columnCount)
                {
                    throw new IOException("File contains unequal column counts");
                }

                columnCount = split.length;

                int numColumnsToParse;

                if (maxColumns > 0 && maxColumns < columnCount)
                {
                    numColumnsToParse = maxColumns;
                }
                else
                {
                    numColumnsToParse = columnCount;
                }

                for (column = 0; column < numColumnsToParse; column++)
                {
                    // Strip off leading and trailing quotes
                    if (split[column].startsWith("\""))
                    {
                        split[column] = split[column].substring(1, split[column].length());
                    }

                    if (split[column].endsWith("\""))
                    {
                        split[column] = split[column].substring(0, split[column].length() - 1);
                    }

                    data.add(column + (row * numColumnsToParse), split[column]);
                }

                row++;
            }

            numColumns = column;
            numRows = row;
            unparsedColumns = numColumns < columnCount;
            unparsedRows = linesRead < numLines;

            reader.close();
        }
        catch (IOException ioe)
        {
           if (DEBUG_BUILD)
           {
               println("IOException in TextDelimitedMatrix.parse():\n" + ioe.getMessage());
               return false;
           }
        }

        return true;
    }

    public int numColumns()
    {
        if (transpose)
        {
            return numRows;
        }
        else
        {
            return numColumns;
        }
    }

    public int numRows()
    {
        if (transpose)
        {
            return numColumns;
        }
        else
        {
            return numRows;
        }
    }

    public int numLines()
    {
        return numLines;
    }

    public boolean hasUnparsedColumns()
    {
        return unparsedColumns;
    }

    public boolean hasUnparsedRows()
    {
        return unparsedRows;
    }

    public String valueAt(int column, int row)
    {
        if (transpose)
        {
            return data.get(row + (column * numColumns));
        }
        else
        {
            return data.get(column + (row * numColumns));
        }
    }

    public void setTranspose(boolean transpose)
    {
        this.transpose = transpose;
    }
}
