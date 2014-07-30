package org.BioLayoutExpress3D.Clustering.MCL;

import java.io.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* @author Leon Goldovsky, full refactoring by Thanos Theo, 2008-2009
* @version 3.0.0.0
*
*/

final class MCLStreamGrabber extends Thread // package access
{
    private InputStream istream = null;
    private MCLWindowDialog MCL_windowDialog = null;

    public MCLStreamGrabber(InputStream istream)
    {
        this.istream = istream;
    }

    public void start(MCLWindowDialog MCL_windowDialog)
    {
        this.MCL_windowDialog = MCL_windowDialog;
        this.setPriority(Thread.NORM_PRIORITY);
        this.start();
    }

    @Override
    public void run()
    {
        try
        {
            InputStreamReader isr = new InputStreamReader(istream);
            BufferedReader br = new BufferedReader(isr);
            String line = "";
            int counter = 0;
            while ( ( line = br.readLine() ) != null )
            {
                if ( (++counter % 20) == 0)
                    sleep(1);

                MCL_windowDialog.appendText(line + "\n");

                if (DEBUG_BUILD) println(line + "\n");
            }

            isr.close();
            br.close();
        }
        catch (IOException ioe)
        {
            if (DEBUG_BUILD) println("IOException in MCLStreamGrabber.run():\n" + ioe.getMessage());
        }
        catch (InterruptedException ex)
        {
            // restore the interuption status after catching InterruptedException
            Thread.currentThread().interrupt();
            if (DEBUG_BUILD) println("InterruptedException in MCLStreamGrabber.run():\n" + ex.getMessage());
        }
    }


}