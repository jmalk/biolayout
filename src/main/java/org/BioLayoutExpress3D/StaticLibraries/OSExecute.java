package org.BioLayoutExpress3D.StaticLibraries;

import java.io.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* OSExecute is a final class containing only static method(s) to be used for executing OS shell based commands.
* Uses OSExecuteException for exception handling.
*
* @see org.BioLayoutExpress3D.StaticLibraries.OSExecuteException
* @author Thanos Theo, 2008-2009
* @version 3.0.0.0
*/

public final class OSExecute
{

    /**
    *  Method to execute OS shell based commands. In case of failing, it throws a OSExecuteException.
    */
    public static void command(String command)
    {
        boolean error = false;

        try
        {
            Process process = new ProcessBuilder( command.split(" ") ).start();
            BufferedReader results = new BufferedReader( new InputStreamReader(process.getInputStream() ) );

            String s = "";
            if (DEBUG_BUILD)
            {
                while ( ( s = results.readLine() )!= null )
                    println(s);
            }

            results.close();

            BufferedReader errors = new BufferedReader( new InputStreamReader( process.getErrorStream() ) );

            // Report errors and return nonzero value
            // to calling process if there are problems:
            while ( (s = errors.readLine() )!= null )
            {
                println(s);
                error = true;
            }

            errors.close();
        }
        catch(Exception e)
        {
            // Compensate for Windows 2000, which throws an exception for the default command line:
            if ( !command.startsWith("CMD /C") )
                command("CMD /C " + command);
            else
                throw new RuntimeException(e);
        }

        if (error)
            throw new OSExecuteException("Errors executing " + command);
    }


}