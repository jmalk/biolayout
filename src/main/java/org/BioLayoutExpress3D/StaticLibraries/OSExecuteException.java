package org.BioLayoutExpress3D.StaticLibraries;

/**
*
* OSExecuteException adds exception handling to OSExecute.
*
* @see org.BioLayoutExpress3D.StaticLibraries.OSExecute
* @author Thanos Theo, 2008-2009
* @version 3.0.0.0
*/

public final class OSExecuteException extends RuntimeException
{

    /**
    *  Serial version UID variable for the OSExecuteException class.
    */
    public static final long serialVersionUID = 111222333444555775L;


    /**
    *  Constructor for the OSExecuteException class.
    */
    public OSExecuteException(String cause)
    {
        super(cause);
    }


}