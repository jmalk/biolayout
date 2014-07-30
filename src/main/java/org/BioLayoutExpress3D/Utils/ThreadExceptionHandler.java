package org.BioLayoutExpress3D.Utils;

/**
 * @author Tim Angus <tim.angus@roslin.ed.ac.uk>
 */

import javax.swing.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.BioLayoutExpress3D.BuildConfig;
import org.BioLayoutExpress3D.Utils.Path;
import org.BioLayoutExpress3D.Environment.DataFolder;

public class ThreadExceptionHandler implements
        Thread.UncaughtExceptionHandler
{
    // If the exception occurs in the EDT, calling JOptionPane.showMessageDialog
    // can cause a repaint and another throw, so we have this guard to avoid that
    // case or others like it
    boolean handlingThreadException = false;

    @Override
    public void uncaughtException(Thread thread, Throwable e)
    {
        String build = BuildConfig.VERSION + "(" + BuildConfig.BUILD_TIME + ")";
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

        String exceptionText = "Exception \"" + e.toString() + "\" occurred in thread ID " +
                    thread.getId() + "(" + thread.getName() + ")";

        String logText = build + "\n" + timeStamp + ": " + exceptionText + "\n" + stackTraceForThrowable(e);
        String shortLogText = exceptionText + ":\n\n" + stackTraceForThrowable(e, 5) + "...";

        while (e.getCause() != null)
        {
            e = e.getCause();
            logText += "\n...caused by: " + e.toString() + "\n" + stackTraceForThrowable(e);
            shortLogText += "\n...caused by: " + e.toString() + "\n" + stackTraceForThrowable(e, 5) + "...";
        }

        System.out.println(logText);

        try
        {
            String dataFolder = DataFolder.get();
            String exceptionLogFileName = Path.combine(dataFolder, "UncaughtExceptions.txt");
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(exceptionLogFileName, true)));
            out.println(logText);
            out.close();
        }
        catch (IOException ioe)
        {
        }

        if (!handlingThreadException)
        {
            handlingThreadException = true;
            JOptionPane.showMessageDialog(null, shortLogText, "Thread exception", JOptionPane.ERROR_MESSAGE);
        }

        handlingThreadException = false;
    }

    private String stackTraceForThrowable(Throwable e)
    {
        return stackTraceForThrowable(e, -1);
    }

    private String stackTraceForThrowable(Throwable e, int numFrames)
    {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement stackTraceElement : e.getStackTrace())
        {
            if (numFrames == 0)
            {
                break;
            }
            else if (numFrames > 0)
            {
                numFrames--;
            }

            sb.append(stackTraceElement.toString()).append("\n");
        }

        return sb.toString();
    }
}