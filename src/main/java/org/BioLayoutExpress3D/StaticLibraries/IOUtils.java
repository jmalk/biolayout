package org.BioLayoutExpress3D.StaticLibraries;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* IOUtils is a final class containing only static stream methods.
*
* @author Thanos Theo, 2008-2009
* @version 3.0.0.0
*/

public final class IOUtils
{

    /**
    *  Default buffer size value.
    */
    private static final int BUFFER_SIZE = 2048;

    /**
    *  IOUtilsStreamingListener listener to be used as a callback for the streaming process.
    */
    private static IOUtilsStreamingListener listener = null;

    /**
    *  Streams the input stream to the output stream.
    */
    public static void streamAndClose(InputStream in, OutputStream out) throws IOException
    {
        try
        {
            stream(in, out);
        }
        finally
        {
            try
            {
                in.close();
            }
            finally
            {
                out.close();
            }
        }
    }

    /**
    *  Streams the input stream to the output stream.
    *  In this method resides the main buffer loop.
    */
    private static void stream(InputStream in, OutputStream out) throws IOException
    {
        // Copy the input stream to the output stream
        byte[] buffer = new byte[BUFFER_SIZE];
        int len = buffer.length;
        if (listener != null)
            listener.initStreamingProcess( in.available() );
        while ( ( len = in.read(buffer) ) != -1 )
        {
            if (listener != null)
                listener.iterateStreamingProcess();
            out.write(buffer, 0, len);
        }
        if (listener != null)
            listener.finishStreamingProcess();
        out.flush();
    }

    /**
    *  Gets the prefix of the given filename. If there's none, it simply returns the original filename.
    */
    public static String getPrefix(String filename)
    {
        // extract name before '.' of filename
        int position = filename.lastIndexOf(".");
        if (position == -1)
        {
            if (DEBUG_BUILD) println("No prefix found for filename: " + filename);

            return filename;
        }
        else
            return filename.substring(0, position);
    }

    /**
    *  Recursive method to get rid of (multiple) usage of the same file extension.
    */
    public static String removeMultipleExtensions(String fileName, String fileExtension)
    {
        if (fileName.endsWith("." + fileExtension))
        {
            return removeMultipleExtensions(getPrefix(fileName), fileExtension);
        }
        else
        {
            return fileName;
        }
    }

    /**
    *  Opens and compresses a given file in the zip format.
    */
    public static void zipCompressFile(String uncompressedFileName)
    {
        zipCompressFile(uncompressedFileName, getPrefix(uncompressedFileName) + ".zip");
    }

    /**
    *  Opens and compresses a given file in the zip format.
    *  Overloaded version to choose the compressed file name.
    */
    public static void zipCompressFile(String uncompressedFileName, String compressedFileName)
    {
        try
        {
            BufferedInputStream in = new BufferedInputStream( new FileInputStream( new File(uncompressedFileName).getAbsoluteFile() ) );
            ZipOutputStream out = new ZipOutputStream( new BufferedOutputStream( new FileOutputStream( new File(compressedFileName).getAbsoluteFile() ) ) );
            out.putNextEntry( new ZipEntry( new File(uncompressedFileName).getName() ) );

            streamAndClose(in, out);
        }
        catch (FileNotFoundException ex)
        {
            if (DEBUG_BUILD) println("Problem with opening the contents of the file " + uncompressedFileName + " :\n" + ex.getMessage());
        }
        catch (IOException ex)
        {
            if (DEBUG_BUILD) println("IO exception with :\n" + ex.getMessage());
        }
    }

    /**
    *  Opens and decompresses a given zip file.
    */
    public static void zipUncompressFile(String compressedFileName)
    {
        zipUncompressFile( compressedFileName, getPrefix(compressedFileName) );
    }

    /**
    *  Opens and decompresses a given zip file.
    *  Overloaded version to choose the uncompressed file name.
    */
    public static void zipUncompressFile(String compressedFileName, String uncompressedFileName)
    {
        try
        {
            ZipInputStream in = new ZipInputStream( new BufferedInputStream( new FileInputStream( new File(compressedFileName).getAbsoluteFile() ) ) );
            BufferedOutputStream out = new BufferedOutputStream( new FileOutputStream( new File(uncompressedFileName).getAbsoluteFile() ) );
            in.getNextEntry();

            streamAndClose(in, out);
        }
        catch (FileNotFoundException ex)
        {
            if (DEBUG_BUILD) println("Problem with opening the contents of the file " + compressedFileName + " :\n" + ex.getMessage());
        }
        catch (IOException ex)
        {
            if (DEBUG_BUILD) println("IO exception with :\n" + ex.getMessage());
        }
    }

    /**
    *  Opens and compresses a given file in the GZIP format.
    */
    public static void GZIPCompressFile(String uncompressedFileName)
    {
        GZIPCompressFile(uncompressedFileName, getPrefix(uncompressedFileName) + ".gz");
    }

    /**
    *  Opens and compresses a given file in the GZIP format.
    *  Overloaded version to choose the compressed file name.
    */
    public static void GZIPCompressFile(String uncompressedFileName, String compressedFileName)
    {
        try
        {
            BufferedInputStream in = new BufferedInputStream( new FileInputStream( new File(uncompressedFileName).getAbsoluteFile() ) );
            BufferedOutputStream out = new BufferedOutputStream( new GZIPOutputStream( new FileOutputStream( new File(compressedFileName).getAbsoluteFile() ) ) );

            streamAndClose(in, out);
        }
        catch (FileNotFoundException ex)
        {
            if (DEBUG_BUILD) println("Problem with opening the contents of the file " + uncompressedFileName + " :\n" + ex.getMessage());
        }
        catch (IOException ex)
        {
            if (DEBUG_BUILD) println("IO exception with :\n" + ex.getMessage());
        }
    }

    /**
    *  Opens and decompresses a given GZIP file.
    */
    public static void GZIPUncompressFile(String compressedFileName)
    {
        GZIPUncompressFile( compressedFileName, getPrefix(compressedFileName) );
    }

    /**
    *  Opens and decompresses a given GZIP file.
    *  Overloaded version to choose the uncompressed file name.
    */
    public static void GZIPUncompressFile(String compressedFileName, String uncompressedFileName)
    {
        try
        {
            BufferedInputStream in = new BufferedInputStream( new GZIPInputStream( new FileInputStream( new File(compressedFileName).getAbsoluteFile() ) ) );
            BufferedOutputStream out = new BufferedOutputStream( new FileOutputStream( new File(uncompressedFileName).getAbsoluteFile() ) );

            streamAndClose(in, out);
        }
        catch (FileNotFoundException ex)
        {
            if (DEBUG_BUILD) println("Problem with opening the contents of the file " + compressedFileName + " :\n" + ex.getMessage());
        }
        catch (IOException ex)
        {
            if (DEBUG_BUILD) println("IO exception with :\n" + ex.getMessage());
        }
    }

    /**
    *  Reads a whole file as a single string.
    */
    public static String readFileContents(String fileName)
    {
        StringBuilder sb = new StringBuilder();
        BufferedReader in = null;

        try
        {
            in = new BufferedReader( new FileReader( new File(fileName).getAbsoluteFile() ) );
            String s = "";

            while( ( s = in.readLine() ) != null )
                sb.append(s).append("\n");
        }
        catch (IOException ex)
        {
            if (DEBUG_BUILD) println("Problem with opening the contents of the file " + fileName + " :\n" + ex.getMessage());
        }
        finally
        {
            try
            {
                if (in != null) in.close();
            }
            catch (IOException ioExc)
            {
                if (DEBUG_BUILD) println("Not managed to close the buffered reader with 'finally' clause in readFileContents() method:\n" + ioExc.getMessage());
            }
        }

        return sb.toString();
    }

    /**
    *  Writes a whole file with a single string in one single method call.
    */
    public static void writeFile(String fileName, String text)
    {
        PrintWriter out = null;

        try
        {
            out = new PrintWriter( new File(fileName).getAbsoluteFile() );
            out.print(text);
            out.flush();
        }
        catch (IOException ex)
        {
            if (DEBUG_BUILD) println("Problem with opening the contents of the file " + fileName + " :\n" + ex.getMessage());
        }
        finally
        {
            if (out != null) out.close();
        }
    }

    /**
    *  Writes a whole file with an arraylist full of strings.
    */
    public static void writeFile(String fileName, ArrayList<String> arr)
    {
        PrintWriter out = null;

        try
        {
            out = new PrintWriter( new File(fileName).getAbsoluteFile() );
            for (String item : arr)
                out.println(item);
            out.flush();
        }
        catch (IOException ex)
        {
            if (DEBUG_BUILD) println("Problem with opening the contents of the file " + fileName + " :\n" + ex.getMessage());
        }
        finally
        {
            if (out != null) out.close();
        }
    }

    /**
    *  Reads a whole binary file and returns its contents in an byte[] array.
    */
    public static byte[] readBinaryFile(String fileName)
    {
        BufferedInputStream bIs = null;

        try
        {
            bIs = new BufferedInputStream( new FileInputStream( new File(fileName).getAbsoluteFile() ) );
            byte[] data = new byte[ bIs.available() ];
            bIs.read(data);

            return data;
        }
        catch (IOException ex)
        {
            if (DEBUG_BUILD) println("Problem with opening the contents of the file " + fileName + " :\n" + ex.getMessage());

            return null;
        }
        finally
        {
            try
            {
                if (bIs != null) bIs.close();
            }
            catch (IOException ioExc)
            {
                if (DEBUG_BUILD) println("Not managed to close the buffered input stream with 'finally' clause in readBinaryFile() method:\n" + ioExc.getMessage());
            }
        }
    }

    /**
    *  Gets the buffer size.
    */
    public static int getBufferSize()
    {
        return BUFFER_SIZE;
    }

    /**
    *  Sets the IOUtilsStreamingListener listener.
    */
    public static void setListener(IOUtilsStreamingListener newListener)
    {
        listener = newListener;
    }

    /**
    *  Removes the IOUtilsLoaderListener listener.
    */
    public static void removeListener()
    {
        listener = null;
    }

    /**
    *  IOUtilsStreamingListener interface, used as a callback design pattern for the BioLayout Express 3D framework.
    */
    public static interface IOUtilsStreamingListener
    {
        /**
        *  This method is called as a callback event when starting a streaming process.
        */
        public void initStreamingProcess(int availableDataInBytes);

        /**
        *  This method is called as a callback event when iterating through a streaming process.
        */
        public void iterateStreamingProcess();

        /**
        *  This method is called as a callback event when finishing a streaming process.
        */
        public void finishStreamingProcess();
    }


}