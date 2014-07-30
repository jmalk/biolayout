package org.BioLayoutExpress3D.Connections;

import java.io.*;
import java.net.*;
import java.util.*;
import org.BioLayoutExpress3D.StaticLibraries.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* HttpConnection class, this class connects to an online web based server.
*
* @author Thanos Theo, 2008-2009
* @version 3.0.0.0
*/

public abstract class HttpConnection
{
    /**
    *  Variable to store the proxy settings for this http connection.
    */
    private Proxy proxy = null;

    /**
    *  Variable to store the name of the http connection. Will be returned with the overriden toString() method.
    */
    protected String nameOfHttpConnection = "";

    /**
    *  Variable to store if the online web based database was successfully connected.
    */
    protected boolean managedToConnect = false;

    /**
    *  The first constructor of the HttpConnection class (without all proxy related settings).
    */
    public HttpConnection() {}

    /**
    *  The second constructor of the HttpConnection class (with all proxy related settings).
    */
    public HttpConnection(Proxy proxy)
    {
        this.proxy = proxy;
    }

    /**
    *  Initializes and retrieves the http connection with a given link.
    */
    protected HttpURLConnection retrieveHttpConnection(String urlString)
    {
        return retrieveHttpConnection(urlString, "", "", false);
    }

    /**
    *  Initializes and retrieves the http connection with a given link.
    *  Overloaded version to set an option to update or not the managedToConnect value (for application usage).
    */
    protected HttpURLConnection retrieveHttpConnection(String urlString, boolean updateManagedToConnectValue)
    {
        return retrieveHttpConnection(urlString, "", "", updateManagedToConnectValue);
    }

    /**
    *  Initializes and retrieves the http connection with a given link.
    *  Overloaded version to use a login/password for proxy authorization (if a proxy object has been created) and to set an option to update or not the managedToConnect value (for application usage).
    */
    protected HttpURLConnection retrieveHttpConnection(String urlString, String login, String password, boolean updateManagedToConnectValue)
    {
        try
        {
            if (DEBUG_BUILD)
            {
                println("Now trying to connect to URL address: " + urlString);
                println();
            }

            // create a URL and a HttpURLConnection
            URL url = new URL(urlString); // URL string
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setUseCaches(false);

            if (proxy != null)
                if ( !login.isEmpty() && !password.isEmpty() ) // if both login/password are empty, no need to set them!
                    proxy.setLoginAndPasswordForProxyAuthorization(conn, login, password);

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                if (DEBUG_BUILD)
                {
                    println();
                    println("Managed to connect to the online web based server database!");
                }

                if (updateManagedToConnectValue)
                    managedToConnect = true;

                return conn;
            }
            else
            {
                if (DEBUG_BUILD)
                {
                    println("Closing connection!");
                }

                conn.disconnect();

                if (DEBUG_BUILD)
                {
                    println("Error in connecting: " + conn.getResponseMessage());
                }

                if (updateManagedToConnectValue)
                    managedToConnect = false;
            }
        }
        catch (Exception exc)
        {
            if (DEBUG_BUILD)
            {
                println("Not managed to connect to the online web based server database in retrieveHttpConnection() method:\n" + exc.getMessage());
            }

            if (updateManagedToConnectValue)
                managedToConnect = false;
        }

        return null;
    }

    /**
    *  Initializes the http connection with a given link and retrieves text data in an ArrayList data structure.
    */
    protected ArrayList<String> retrieveTextDataFromHttpConnection(String urlString)
    {
        return retrieveTextDataFromHttpConnection(urlString, "", "", false);
    }

    /**
    *  Initializes the http connection with a given link and retrieves text data in an ArrayList data structure.
    *  Overloaded version to set an option to update or not the managedToConnect value (for application usage).
    */
    protected ArrayList<String> retrieveTextDataFromHttpConnection(String urlString, boolean updateManagedToConnectValue)
    {
        return retrieveTextDataFromHttpConnection(urlString, "", "", updateManagedToConnectValue);
    }

    /**
    *  Initializes the http connection with a given link and retrieves text data in an ArrayList data structure.
    *  Overloaded version to use a login/password for proxy authorization (if a proxy object has been created) and to set an option to update or not the managedToConnect value (for application usage).
    */
    protected ArrayList<String> retrieveTextDataFromHttpConnection(String urlString, String login, String password, boolean updateManagedToConnectValue)
    {
        ArrayList<String> textData = new ArrayList<String>();
        BufferedReader br = null;

        try
        {
            if (DEBUG_BUILD)
            {
                println("Now trying to connect to URL address: " + urlString);
                println();
            }

            // create a URL and a HttpURLConnection
            URL url = new URL(urlString); // URL string
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setUseCaches(false);

            if (proxy != null)
                if ( !login.isEmpty() && !password.isEmpty() ) // if both login/password are empty, no need to set them!
                    proxy.setLoginAndPasswordForProxyAuthorization(conn, login, password);

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                br = new BufferedReader( new InputStreamReader( conn.getInputStream() ) );

                int numLines = 0;
                String line = "";
                while ( ( (line = br.readLine() ) != null) )
                {
                    if (DEBUG_BUILD)
                    {
                        println(line);
                    }

                    textData.add(line);
                    numLines++;
                }

                br.close();
                conn.disconnect();

                if (DEBUG_BUILD)
                {
                    println();
                    println("Managed to connect to the online web based server database!");
                    println("Text data consisted of " + numLines + " lines.");
                }

                if (updateManagedToConnectValue)
                    managedToConnect = true;
            }
            else
            {
                if (DEBUG_BUILD)
                {
                    println("Closing connection!");
                }

                conn.disconnect();

                if (DEBUG_BUILD)
                {
                    println("Error in connecting: " + conn.getResponseMessage());
                }

                if (updateManagedToConnectValue)
                    managedToConnect = false;
            }
        }
        catch (Exception exc)
        {
            if (DEBUG_BUILD)
            {
                println("Not managed to connect to the online web based server database in retrieveTextDataFromHttpConnection() method:\n" + exc.getMessage());
            }

            if (updateManagedToConnectValue)
                managedToConnect = false;
        }
        finally
        {
            try
            {
                if (br != null) br.close();
            }
            catch (IOException ioExc)
            {
                if (DEBUG_BUILD) println("Not managed to close the online web based server database connection with 'finally' clause in retrieveTextDataFromHttpConnection() method:\n" + ioExc.getMessage());
            }
        }

        return textData;
    }

    /**
    *  Initializes the http connection with a given link and retrieves binary data in a given filename.
    */
    protected boolean retrieveBinaryDataFromHttpConnection(String urlString, String fileName)
    {
        return retrieveBinaryDataFromHttpConnection(urlString, "", "", fileName, false);
    }

    /**
    *  Initializes the http connection with a given link and retrieves binary data in a given filename.
    *  Overloaded version to set an option to update or not the managedToConnect value (for application usage).
    */
    protected boolean retrieveBinaryDataFromHttpConnection(String urlString, String fileName, boolean updateManagedToConnectValue)
    {
        return retrieveBinaryDataFromHttpConnection(urlString, "", "", fileName, updateManagedToConnectValue);
    }

    /**
    *  Initializes the http connection with a given link and retrieves text data in a given filename.
    *  Overloaded version to use a login/password for proxy authorization (if a proxy object has been created) and to set an option to update or not the managedToConnect value (for application usage).
    */
    protected boolean retrieveBinaryDataFromHttpConnection(String urlString, String login, String password, String fileName, boolean updateManagedToConnectValue)
    {
        try
        {
            if (DEBUG_BUILD)
            {
                println("Now trying to connect to URL address: " + urlString);
                println();
            }

            // create a URL and a HttpURLConnection
            URL url = new URL(urlString); // URL string
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setUseCaches(false);

            if (proxy != null)
                if ( !login.isEmpty() && !password.isEmpty() ) // if both login/password are empty, no need to set them!
                    proxy.setLoginAndPasswordForProxyAuthorization(conn, login, password);

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                BufferedInputStream fileIn = new BufferedInputStream ( conn.getInputStream() );
                BufferedOutputStream fileOut = new BufferedOutputStream( new FileOutputStream(fileName) );

                IOUtils.streamAndClose(fileIn, fileOut);

                conn.disconnect();

                if (DEBUG_BUILD)
                {
                    println();
                    println("Managed to connect to the online web based server database!");
                    println("Managed to download the binary data.");
                }

                if (updateManagedToConnectValue)
                    managedToConnect = true;

                return true;
            }
            else
            {
                if (DEBUG_BUILD)
                {
                    println("Closing connection!");
                }

                conn.disconnect();

                if (DEBUG_BUILD)
                {
                    println("Error in connecting: " + conn.getResponseMessage());
                }

                if (updateManagedToConnectValue)
                    managedToConnect = false;
            }
        }
        catch (Exception exc)
        {
            if (DEBUG_BUILD)
            {
                println("Not managed to connect to the online web based server database in retrieveBinaryDataFromHttpConnection() method:\n" + exc.getMessage());
            }

            if (updateManagedToConnectValue)
                managedToConnect = false;
        }

        return false;
    }

    /**
    *  Retrieves text data in an ArrayList data structure from a local file.
    *  Used mainly for debugging reasons to test cases of file text/html/xml parsing directly from local storage.
    */
    protected ArrayList<String> retrieveTextDataFromFile(String fileName)
    {
        return retrieveTextDataFromFile(fileName, false);
    }

    /**
    *  Retrieves text data in an ArrayList data structure from a local file.
    *  Used mainly for debugging reasons to test cases of file text/html/xml parsing directly from local storage.
    *  Overloaded version to set an option to update or not the managedToConnect value (for application usage).
    */
    protected ArrayList<String> retrieveTextDataFromFile(String fileName, boolean updateManagedToConnectValue)
    {
        ArrayList<String> textData = new ArrayList<String>();
        BufferedReader br = null;

        try
        {
            if (DEBUG_BUILD)
            {
                println("Now trying to open local file: " + fileName);
                println();
            }

            br = new BufferedReader( new FileReader(fileName) );

            int numLines = 0;
            String line = "";
            while ( ( (line = br.readLine() ) != null) )
            {
                if (DEBUG_BUILD)
                {
                    println(line);
                }

                textData.add(line);
                numLines++;
            }

            br.close();

            if (DEBUG_BUILD)
            {
                println();
                println("Managed to open the local file!");
                println("Text data consisted of " + numLines + " lines.");
            }

            if (updateManagedToConnectValue)
                managedToConnect = true;
        }
        catch (Exception exc)
        {
            if (DEBUG_BUILD)
            {
                println("Not managed to open the local file in retrieveTextDataFromFile() method:\n" + exc.getMessage());
            }

            if (updateManagedToConnectValue)
                managedToConnect = false;
        }
        finally
        {
            try
            {
                if (br != null) br.close();
            }
            catch (IOException ioExc)
            {
                if (DEBUG_BUILD) println("Not managed to close the online web based server database connection with 'finally' clause in retrieveTextDataFromFile() method:\n" + ioExc.getMessage());
            }
        }

        return textData;
    }

    /**
    *  Initializes the http connection and sends a link.
    */
    protected boolean sendLinkWithHttpConnection(String urlString)
    {
        return sendLinkWithHttpConnection(urlString, "", "");
    }

    /**
    *  Initializes the http connection and sends a link.
    *  Overloaded version to use a login/password for proxy authorization (if a proxy object has been created).
    */
    protected boolean sendLinkWithHttpConnection(String urlString, String login, String password)
    {
        try
        {
            if (DEBUG_BUILD)
            {
                println("Now trying to connect to URL address: " + urlString);
                println();
            }

            // create a URL and a HttpURLConnection
            URL url = new URL(urlString); // URL string
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setUseCaches(false);

            if (proxy != null)
                if ( !login.isEmpty() && !password.isEmpty() ) // if both login/password are empty, no need to set them!
                    proxy.setLoginAndPasswordForProxyAuthorization(conn, login, password);

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                conn.disconnect();

                if (DEBUG_BUILD)
                {
                    println();
                    println("Managed to connect to the online web based server database!");
                }

                return true;
            }
            else
            {
                if (DEBUG_BUILD)
                {
                    println("Closing connection!");
                }

                conn.disconnect();

                if (DEBUG_BUILD)
                {
                    println("Error in connecting: " + conn.getResponseMessage());
                }
            }
        }
        catch (Exception exc)
        {
            if (DEBUG_BUILD)
            {
                println("Not managed to connect to the online web based server database in sendLinkWithHttpConnection() method:\n" + exc.getMessage());
            }
        }

        return false;
    }

    /**
    *  Gets if the online web based database was successfully connected.
    */
    public boolean getManagedToConnect()
    {
        return managedToConnect;
    }

    /**
    *  Sets the name of the http connection.
    */
    public void setHttpConnectionName(String nameOfHttpConnection)
    {
        this.nameOfHttpConnection = nameOfHttpConnection;
    }

    /**
    *  Gets the name of the http connection.
    */
    public String getHttpConnectionName()
    {
        return nameOfHttpConnection;
    }

    /**
    *  Gets the name of the http connection. Overrides the general toString() Object method.
    */
    @Override
    public String toString()
    {
        return nameOfHttpConnection;
    }


}