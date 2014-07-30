package org.BioLayoutExpress3D.Connections;

import java.net.*;
import java.util.*;
import org.BioLayoutExpress3D.StaticLibraries.*;

/**
*
* Proxy class, this class adds proxy support to an online server based database connection.
*
* @see org.BioLayoutExpress3D.Connections.HttpConnection
* @author Thanos Theo, 2008-2009
* @version 3.0.0.0
*/

public class Proxy
{

    /**
    *  The constructor of the Proxy class. Sets all the proxy system settings.
    */
    public Proxy(boolean proxySet, String proxyHost, int proxyPort)
    {
        Properties props = System.getProperties();
        props.put("proxySet", Boolean.toString(proxySet));
        props.put("proxyHost", proxyHost);
        props.put("proxyPort", Integer.toString(proxyPort));
        System.setProperties(props);
    }

    /**
    *  Sets the login/password proxy authorization settings.
    */
    public void setLoginAndPasswordForProxyAuthorization(HttpURLConnection conn, String login, String password)
    {
        // encode the "login:password" string
        String encoding = Base64Converter.encode(login + ":" + password);

        // send the authorization
        conn.setRequestProperty("Proxy-Authorization", "Basic " + encoding);
    }


}