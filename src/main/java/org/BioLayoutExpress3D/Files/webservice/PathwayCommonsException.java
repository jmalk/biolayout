/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.BioLayoutExpress3D.Files.webservice;

import java.io.IOException;

/**
 * Signals that an error has occurred in the Pathway Commons search or communication with the cPath2 web service
 * May be used when the web service returns a HTTP status code with a value other than 200.
 * May also be used without a status code and passing a custom message.
 * @author Derek Wright
 */
public class PathwayCommonsException extends IOException
{
    /**
     * HTTP status code returned from server
     */
    private int statusCode = 0;
    
    /**
     * Exception message
     */
    private String message = "";

    /**
     * Constructor
     * @param statusCode - The HTTP status code returned by the cPath2 web service
     */
    public PathwayCommonsException(int statusCode)
    {
        this.statusCode = statusCode;
    }
    
    /**
     * Constructor
     * @param message - custom error message
     */
    public PathwayCommonsException(String message)
    {
        this.message = message;
    }
    
    /*
     * The HTTP status code returned by the cPath2 web service
     * @return the status code
     */
    public int getStatusCode() 
    {
        return statusCode;
    }

    @Override
    public String getMessage() 
    {
        switch(statusCode){
            case 460:
                message = "No results found";
                break;
                
            case 452:
                message = "Bad request (illegal or no arguments)";
                break;       
                
            case 500:
                message = "Internal server error";
                break;
            
            case 0:     //just use message passed in constructor in this case
                return message;

            default:
                message = "Unable to reach Pathway Commons";
                break;
        }
        message += " (status code: " + statusCode + ")";
        return message;
    }
}
