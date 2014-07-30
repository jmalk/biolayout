package org.BioLayoutExpress3D.DebugConsole;

import java.io.*;
import java.util.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;

/**
*
* FileOutput is a class to be used for file output logging.
* It is being used in multiple instances, one for each file logging.
*
* @author Thanos Theo, 2008-2009
* @version 3.0.0.0
*/

public final class FileOutput
{

    /**
    *  Name of the file to be written.
    */
    private String fileOutName = "";

    /**
    *  Auxiliary variable for enabling/disabling file logging.
    */
    private boolean isLoggingOn = false;

    /**
    *  Auxiliary variable for appending file logging.
    */
    private boolean isAppending = false;

    /**
    *  The PrintWriter object variable.
    */
    private PrintWriter fileout = null;

    /**
    *  The first constructor of the file logging enable/disable class.
    */
    public FileOutput(String fileOutName, boolean isLoggingOn)
    {
        this(fileOutName, isLoggingOn, true);
    }

    /**
    *  The second constructor of the file logging enable/disable class.
    */
    public FileOutput(String fileOutName, boolean isLoggingOn, boolean isAppending)
    {
        this.fileOutName = fileOutName;
        this.isLoggingOn = isLoggingOn;
        this.isAppending = isAppending;

        if (!isAppending)
            initOpenFileWriter();
    }

    /**
    *  Initializes (opens) a file for writing.
    */
    private void initOpenFileWriter()
    {
        if (isLoggingOn)
        {
            try
            {
                fileout = new PrintWriter( new BufferedWriter( new FileWriter(fileOutName, false) ) );
            }
            catch (IOException e)
            {
                if (DEBUG_BUILD) System.out.println("Error opening the " + fileOutName + " file:\n" + e.getMessage());
            }
        }
    }

    /**
    *  Initializes (appends) the current file for writing.
    */
    private void initAppendFileWriter()
    {
        try
        {
            fileout = new PrintWriter( new BufferedWriter( new FileWriter(fileOutName, true) ) );
        }
        catch (IOException e)
        {
            if (DEBUG_BUILD) System.out.println("Error appending the " + fileOutName + " file:\n" + e.getMessage());
        }
    }

    /**
    *  Adds a boolean into the file.
    */
    public synchronized void print(boolean lineFileOutput)
    {
        if (isLoggingOn)
        {
            if (isAppending)
                initAppendFileWriter();

            fileout.print(lineFileOutput);

            if (isAppending)
            {
                fileout.flush();
                fileout.close();
                fileout = null;
            }
        }
    }

    /**
    *  Adds a char into the file.
    */
    public synchronized void print(char lineFileOutput)
    {
        if (isLoggingOn)
        {
            if (isAppending)
                initAppendFileWriter();

            fileout.print(lineFileOutput);

            if (isAppending)
            {
                fileout.flush();
                fileout.close();
                fileout = null;
            }
        }
    }

    /**
    *  Adds a double into the file.
    */
    public synchronized void print(double lineFileOutput)
    {
        if (isLoggingOn)
        {
            if (isAppending)
                initAppendFileWriter();

            fileout.print(lineFileOutput);

            if (isAppending)
            {
                fileout.flush();
                fileout.close();
                fileout = null;
            }
        }
    }

    /**
    *  Adds a float into the file.
    */
    public synchronized void print(float lineFileOutput)
    {
        if (isLoggingOn)
        {
            if (isAppending)
                initAppendFileWriter();

            fileout.print(lineFileOutput);

            if (isAppending)
            {
                fileout.flush();
                fileout.close();
                fileout = null;
            }
        }
    }

    /**
    *  Adds an int into the file.
    */
    public synchronized void print(int lineFileOutput)
    {
        if (isLoggingOn)
        {
            if (isAppending)
                initAppendFileWriter();

            fileout.print(lineFileOutput);

            if (isAppending)
            {
                fileout.flush();
                fileout.close();
                fileout = null;
            }
        }
    }

    /**
    *  Adds a long into the file.
    */
    public synchronized void print(long lineFileOutput)
    {
        if (isLoggingOn)
        {
            if (isAppending)
                initAppendFileWriter();

            fileout.print(lineFileOutput);

            if (isAppending)
            {
                fileout.flush();
                fileout.close();
                fileout = null;
            }
        }
    }

    /**
    *  Adds a char[] into the file.
    */
    public synchronized void print(char[] lineFileOutput)
    {
        if (isLoggingOn)
        {
            if (isAppending)
                initAppendFileWriter();

            fileout.print(lineFileOutput);

            if (isAppending)
            {
                fileout.flush();
                fileout.close();
                fileout = null;
            }
        }
    }

    /**
    *  Adds a string into the file.
    */
    public synchronized void print(String lineFileOutput)
    {
        if (isLoggingOn)
        {
            if (isAppending)
                initAppendFileWriter();

            fileout.print(lineFileOutput);

            if (isAppending)
            {
                fileout.flush();
                fileout.close();
                fileout = null;
            }
        }
    }

    /**
    *  Adds a Object into the file.
    */
    public synchronized void print(Object lineFileOutput)
    {
        if (isLoggingOn)
        {
            if (isAppending)
                initAppendFileWriter();

            fileout.print(lineFileOutput);

            if (isAppending)
            {
                fileout.flush();
                fileout.close();
                fileout = null;
            }
        }
    }

    /**
    *  Adds a blank line into the file.
    */
    public synchronized void println()
    {
        if (isLoggingOn)
        {
            if (isAppending)
                initAppendFileWriter();

            fileout.println();

            if (isAppending)
            {
                fileout.flush();
                fileout.close();
                fileout = null;
            }
        }
    }

    /**
    *  Adds a boolean line into the file.
    */
    public synchronized void println(boolean lineFileOutput)
    {
        if (isLoggingOn)
        {
            if (isAppending)
                initAppendFileWriter();

            fileout.println(lineFileOutput);

            if (isAppending)
            {
                fileout.flush();
                fileout.close();
                fileout = null;
            }
        }
    }

    /**
    *  Adds a char line into the file.
    */
    public synchronized void println(char lineFileOutput)
    {
        if (isLoggingOn)
        {
            if (isAppending)
                initAppendFileWriter();

            fileout.println(lineFileOutput);

            if (isAppending)
            {
                fileout.flush();
                fileout.close();
                fileout = null;
            }
        }
    }

    /**
    *  Adds a double line into the file.
    */
    public synchronized void println(double lineFileOutput)
    {
        if (isLoggingOn)
        {
            if (isAppending)
                initAppendFileWriter();

            fileout.println(lineFileOutput);

            if (isAppending)
            {
                fileout.flush();
                fileout.close();
                fileout = null;
            }
        }
    }

    /**
    *  Adds a float line into the file.
    */
    public synchronized void println(float lineFileOutput)
    {
        if (isLoggingOn)
        {
            if (isAppending)
                initAppendFileWriter();

            fileout.println(lineFileOutput);

            if (isAppending)
            {
                fileout.flush();
                fileout.close();
                fileout = null;
            }
        }
    }

    /**
    *  Adds an int line into the file.
    */
    public synchronized void println(int lineFileOutput)
    {
        if (isLoggingOn)
        {
            if (isAppending)
                initAppendFileWriter();

            fileout.println(lineFileOutput);

            if (isAppending)
            {
                fileout.flush();
                fileout.close();
                fileout = null;
            }
        }
    }

    /**
    *  Adds a long line into the file.
    */
    public synchronized void println(long lineFileOutput)
    {
        if (isLoggingOn)
        {
            if (isAppending)
                initAppendFileWriter();

            fileout.println(lineFileOutput);

            if (isAppending)
            {
                fileout.flush();
                fileout.close();
                fileout = null;
            }
        }
    }

    /**
    *  Adds a char[] line into the file.
    */
    public synchronized void println(char[] lineFileOutput)
    {
        if (isLoggingOn)
        {
            if (isAppending)
                initAppendFileWriter();

            fileout.println(lineFileOutput);

            if (isAppending)
            {
                fileout.flush();
                fileout.close();
                fileout = null;
            }
        }
    }

    /**
    *  Adds a string line into the file.
    */
    public synchronized void println(String lineFileOutput)
    {
        if (isLoggingOn)
        {
            if (isAppending)
                initAppendFileWriter();

            fileout.println(lineFileOutput);

            if (isAppending)
            {
                fileout.flush();
                fileout.close();
                fileout = null;
            }
        }
    }

    /**
    *  Adds a Object line into the file.
    */
    public synchronized void println(Object lineFileOutput)
    {
        if (isLoggingOn)
        {
            if (isAppending)
                initAppendFileWriter();

            fileout.println(lineFileOutput);

            if (isAppending)
            {
                fileout.flush();
                fileout.close();
                fileout = null;
            }
        }
    }

    /**
    *  Adds a line into the file using the printf C-style method.
    */
    public synchronized void printf(String format, Object... args)
    {
        if (isLoggingOn)
        {
            if (isAppending)
                initAppendFileWriter();

            fileout.printf(format, args);

            if (isAppending)
            {
                fileout.flush();
                fileout.close();
                fileout = null;
            }
        }
    }

    /**
    *  Adds a line into the file using the printf C-style method (overloaded with Locale).
    */
    public synchronized void printf(Locale locale, String format, Object... args)
    {
        if (isLoggingOn)
        {
            if (isAppending)
                initAppendFileWriter();

            fileout.printf(locale, format, args);

            if (isAppending)
            {
                fileout.flush();
                fileout.close();
                fileout = null;
            }
        }
    }

    /**
    *  Adds a line into the file using the format C-style method.
    */
    public synchronized void format(String format, Object... args)
    {
        if (isLoggingOn)
        {
            if (isAppending)
                initAppendFileWriter();

            fileout.format(format, args);

            if (isAppending)
            {
                fileout.flush();
                fileout.close();
                fileout = null;
            }
        }
    }

    /**
    *  Adds a line into the file using the format C-style method (overloaded with Locale).
    */
    public synchronized void format(Locale locale, String format, Object... args)
    {
        if (isLoggingOn)
        {
            if (isAppending)
                initAppendFileWriter();

            fileout.format(locale, format, args);

            if (isAppending)
            {
                fileout.flush();
                fileout.close();
                fileout = null;
            }
        }
    }

    /**
    *  Prints a String representing the hex format of a byte[] array.
    */
    public synchronized void printByteArrayHexFormat(byte[] data)
    {
        if (isLoggingOn)
        {
            if (isAppending)
                initAppendFileWriter();

            fileout.println( Utils.byteArrayHexFormat(data) );

            if (isAppending)
            {
                fileout.flush();
                fileout.close();
                fileout = null;
            }
        }
    }

    /**
    *  Closes the file.
    */
    public synchronized void close()
    {
        if (fileout != null)
        {
            fileout.flush();
            fileout.close();
            fileout = null;
        }
    }


}