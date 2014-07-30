package org.BioLayoutExpress3D.DebugConsole;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import org.BioLayoutExpress3D.DataStructures.*;
import org.BioLayoutExpress3D.StaticLibraries.*;

/**
*
* ConsoleOutput is a class used to enable/disable console output.
*
* @author Thanos Theo, 2008-2009
* @version 3.0.0.0
*/

public final class ConsoleOutput
{

    /**
    *  Auxiliary variable for queue capacity for the logging service with multicore threading support.
    */
    private static final int CAPACITY = 1024;

    /**
    *  Auxiliary reference variable for queueing the logging service with multicore threading support.
    */
    private static BlockingQueue<Tuple2<Boolean, String>> queue = null;

    /**
    *  Auxiliary variable for the logging service with multicore threading support.
    */
    private static volatile boolean isShutDown = false;

    /**
    *  Auxiliary variable for the logging service with multicore threading support.
    */
    private static AtomicInteger reservations = null;

    /**
    *  Auxiliary reference variable for the logging service with multicore threading support.
    */
    private static ConsoleOutputThread consoleOutputThread = null;

    /**
    *  Auxiliary variable for enabling/disabling the multicore support.
    */
    private static boolean isMultiCoreOn = false;

    /**
    *  Auxiliary variable for enabling/disabling the console output.
    */
    private static boolean isLoggingOn = false;

    /**
    *  Reference for the FileOutput log file writer.
    */
    private static FileOutput fileOutput = null;

    /**
    *  Variable used for loading the native library only once (no use of re-loading the library).
    */
    private static boolean hasOnceInitConsoleOutputThreadVariables = false;

    /**
    *  Sets the isLoggingOn variable.
    */
    public static void setIsMultiCoreOn(boolean defineIsMultiCoreOn)
    {
        isMultiCoreOn = defineIsMultiCoreOn;

        if (isMultiCoreOn)
            if (!hasOnceInitConsoleOutputThreadVariables)
                hasOnceInitConsoleOutputThreadVariables = initConsoleOutputThreadVariables();
    }

    /**
    *  Gets the isMultiCoreOn variable.
    */
    public static boolean getIsMultiCoreOn()
    {
        return isMultiCoreOn;
    }

    /**
    *  Sets the isLoggingOn variable.
    */
    public static void setIsLoggingOn(boolean defineIsLoggingOn)
    {
        isLoggingOn = defineIsLoggingOn;
    }

    /**
    *  Gets the isLoggingOn variable.
    */
    public static boolean getIsLoggingOn()
    {
        return isLoggingOn;
    }

    /**
    *  Sets the fileOutput variable.
    */
    public static void setFileOutput(FileOutput defineFileOutput)
    {
        fileOutput = defineFileOutput;
    }

    /**
    *  Gets the fileOutput variable.
    */
    public static FileOutput getFileOutput()
    {
        return fileOutput;
    }

    /**
    *  Overrides System.out.print(java.lang.Boolean) with this method.
    */
    public static void print(boolean lineConsoleOutput)
    {
        if (isLoggingOn)
        {
            if (isMultiCoreOn)
            {
                if (isShutDown)
                    throw new IllegalStateException("ConsoleOuput ShutDown is Imminent!");
                reservations.incrementAndGet();

                try
                {
                    queue.put( Tuples.tuple(false, lineConsoleOutput ? "true" : "false") );
                }
                catch (InterruptedException ex)
                {
                    // restore the interuption status after catching InterruptedException
                    Thread.currentThread().interrupt();
                    System.out.println("InterruptedException with the queue.put() method in the ConsoleOutput class:\n" + ex.getMessage());
                }
            }
            else
            {
                System.out.print(lineConsoleOutput);

                if (fileOutput != null)
                    fileOutput.print(lineConsoleOutput);
            }
        }
    }

    /**
    *  Overrides System.out.print(char) with this method.
    */
    public static void print(char lineConsoleOutput)
    {
        if (isLoggingOn)
        {
            if (isMultiCoreOn)
            {
                if (isShutDown)
                    throw new IllegalStateException("ConsoleOuput ShutDown is Imminent!");
                reservations.incrementAndGet();

                try
                {
                    queue.put( Tuples.tuple( false, String.valueOf(lineConsoleOutput) ) );
                }
                catch (InterruptedException ex)
                {
                    // restore the interuption status after catching InterruptedException
                    Thread.currentThread().interrupt();
                    System.out.println("InterruptedException with the queue.put() method in the ConsoleOutput class:\n" + ex.getMessage());
                }
            }
            else
            {
                System.out.print(lineConsoleOutput);

                if (fileOutput != null)
                    fileOutput.print(lineConsoleOutput);
            }
        }
    }

    /**
    *  Overrides System.out.print(double) with this method.
    */
    public static void print(double lineConsoleOutput)
    {
        if (isLoggingOn)
        {
            if (isMultiCoreOn)
            {
                if (isShutDown)
                    throw new IllegalStateException("ConsoleOuput ShutDown is Imminent!");
                reservations.incrementAndGet();

                try
                {
                    queue.put( Tuples.tuple( false, String.valueOf(lineConsoleOutput) ) );
                }
                catch (InterruptedException ex)
                {
                    // restore the interuption status after catching InterruptedException
                    Thread.currentThread().interrupt();
                    System.out.println("InterruptedException with the queue.put() method in the ConsoleOutput class:\n" + ex.getMessage());
                }
            }
            else
            {
                System.out.print(lineConsoleOutput);

                if (fileOutput != null)
                    fileOutput.print(lineConsoleOutput);
            }
        }
    }

    /**
    *  Overrides System.out.print(float) with this method.
    */
    public static void print(float lineConsoleOutput)
    {
        if (isLoggingOn)
        {
            if (isMultiCoreOn)
            {
                if (isShutDown)
                    throw new IllegalStateException("ConsoleOuput ShutDown is Imminent!");
                reservations.incrementAndGet();

                try
                {
                    queue.put( Tuples.tuple( false, String.valueOf(lineConsoleOutput) ) );
                }
                catch (InterruptedException ex)
                {
                    // restore the interuption status after catching InterruptedException
                    Thread.currentThread().interrupt();
                    System.out.println("InterruptedException with the queue.put() method in the ConsoleOutput class:\n" + ex.getMessage());
                }
            }
            else
            {
                System.out.print(lineConsoleOutput);

                if (fileOutput != null)
                    fileOutput.print(lineConsoleOutput);
            }
        }
    }

    /**
    *  Overrides System.out.print(int) with this method.
    */
    public static void print(int lineConsoleOutput)
    {
        if (isLoggingOn)
        {
            if (isMultiCoreOn)
            {
                if (isShutDown)
                    throw new IllegalStateException("ConsoleOuput ShutDown is Imminent!");
                reservations.incrementAndGet();

                try
                {
                    queue.put( Tuples.tuple( false, String.valueOf(lineConsoleOutput) ) );
                }
                catch (InterruptedException ex)
                {
                    // restore the interuption status after catching InterruptedException
                    Thread.currentThread().interrupt();
                    System.out.println("InterruptedException with the queue.put() method in the ConsoleOutput class:\n" + ex.getMessage());
                }
            }
            else
            {
                System.out.print(lineConsoleOutput);

                if (fileOutput != null)
                    fileOutput.print(lineConsoleOutput);
            }
        }
    }

    /**
    *  Overrides System.out.print(long) with this method.
    */
    public static void print(long lineConsoleOutput)
    {
        if (isLoggingOn)
        {
            if (isMultiCoreOn)
            {
                if (isShutDown)
                    throw new IllegalStateException("ConsoleOuput ShutDown is Imminent!");
                reservations.incrementAndGet();

                try
                {
                    queue.put( Tuples.tuple( false, String.valueOf(lineConsoleOutput) ) );
                }
                catch (InterruptedException ex)
                {
                    // restore the interuption status after catching InterruptedException
                    Thread.currentThread().interrupt();
                    System.out.println("InterruptedException with the queue.put() method in the ConsoleOutput class:\n" + ex.getMessage());
                }
            }
            else
            {
                System.out.print(lineConsoleOutput);

                if (fileOutput != null)
                    fileOutput.print(lineConsoleOutput);
            }
        }
    }

    /**
    *  Overrides System.out.print(char[]) with this method.
    */
    public static void print(char[] lineConsoleOutput)
    {
        if (isLoggingOn)
        {
            if (isMultiCoreOn)
            {
                if (isShutDown)
                    throw new IllegalStateException("ConsoleOuput ShutDown is Imminent!");
                reservations.incrementAndGet();

                try
                {
                    queue.put( Tuples.tuple( false, new String(lineConsoleOutput) ) );
                }
                catch (InterruptedException ex)
                {
                    // restore the interuption status after catching InterruptedException
                    Thread.currentThread().interrupt();
                    System.out.println("InterruptedException with the queue.put() method in the ConsoleOutput class:\n" + ex.getMessage());
                }
            }
            else
            {
                System.out.print(lineConsoleOutput);

                if (fileOutput != null)
                    fileOutput.print(lineConsoleOutput);
            }
        }
    }

    /**
    *  Overrides System.out.print(java.lang.String) with this method.
    */
    public static void print(String lineConsoleOutput)
    {
        if (isLoggingOn)
        {
            if (isMultiCoreOn)
            {
                if (isShutDown)
                    throw new IllegalStateException("ConsoleOuput ShutDown is Imminent!");
                reservations.incrementAndGet();

                try
                {
                    queue.put( Tuples.tuple(false, lineConsoleOutput) );
                }
                catch (InterruptedException ex)
                {
                    // restore the interuption status after catching InterruptedException
                    Thread.currentThread().interrupt();
                    System.out.println("InterruptedException with the queue.put() method in the ConsoleOutput class:\n" + ex.getMessage());
                }
            }
            else
            {
                System.out.print(lineConsoleOutput);

                if (fileOutput != null)
                    fileOutput.print(lineConsoleOutput);
            }
        }
    }

    /**
    *  Overrides System.out.print(java.lang.Object) with this method.
    */
    public static void print(Object lineConsoleOutput)
    {
        if (isLoggingOn)
        {
            if (isMultiCoreOn)
            {
                if (isShutDown)
                    throw new IllegalStateException("ConsoleOuput ShutDown is Imminent!");
                reservations.incrementAndGet();

                try
                {
                    queue.put( Tuples.tuple( false, String.valueOf(lineConsoleOutput) ) );
                }
                catch (InterruptedException ex)
                {
                    // restore the interuption status after catching InterruptedException
                    Thread.currentThread().interrupt();
                    System.out.println("InterruptedException with the queue.put() method in the ConsoleOutput class:\n" + ex.getMessage());
                }
            }
            else
            {
                System.out.print(lineConsoleOutput);

                if (fileOutput != null)
                    fileOutput.print(lineConsoleOutput);
            }
        }
    }

    /**
    *  Overrides System.out.println with this method.
    */
    public static void println()
    {
        if (isLoggingOn)
        {
            if (isMultiCoreOn)
            {
                if (isShutDown)
                    throw new IllegalStateException("ConsoleOuput ShutDown is Imminent!");
                reservations.incrementAndGet();

                try
                {
                    queue.put( Tuples.tuple(true, "") );
                }
                catch (InterruptedException ex)
                {
                    // restore the interuption status after catching InterruptedException
                    Thread.currentThread().interrupt();
                    System.out.println("InterruptedException with the queue.put() method in the ConsoleOutput class:\n" + ex.getMessage());
                }
            }
            else
            {
                System.out.println();

                if (fileOutput != null)
                    fileOutput.println();
            }
        }
    }

    /**
    *  Overrides System.out.println(java.lang.Boolean) with this method.
    */
    public static void println(boolean lineConsoleOutput)
    {
        if (isLoggingOn)
        {
            if (isMultiCoreOn)
            {
                if (isShutDown)
                    throw new IllegalStateException("ConsoleOuput ShutDown is Imminent!");
                reservations.incrementAndGet();

                try
                {
                    queue.put( Tuples.tuple(true, lineConsoleOutput ? "true" : "false") );
                }
                catch (InterruptedException ex)
                {
                    // restore the interuption status after catching InterruptedException
                    Thread.currentThread().interrupt();
                    System.out.println("InterruptedException with the queue.put() method in the ConsoleOutput class:\n" + ex.getMessage());
                }
            }
            else
            {
                System.out.println(lineConsoleOutput);

                if (fileOutput != null)
                    fileOutput.println(lineConsoleOutput);
            }
        }
    }

    /**
    *  Overrides System.out.println(char) with this method.
    */
    public static void println(char lineConsoleOutput)
    {
        if (isLoggingOn)
        {
            if (isMultiCoreOn)
            {
                if (isShutDown)
                    throw new IllegalStateException("ConsoleOuput ShutDown is Imminent!");
                reservations.incrementAndGet();

                try
                {
                    queue.put( Tuples.tuple( true, String.valueOf(lineConsoleOutput) ) );
                }
                catch (InterruptedException ex)
                {
                    // restore the interuption status after catching InterruptedException
                    Thread.currentThread().interrupt();
                    System.out.println("InterruptedException with the queue.put() method in the ConsoleOutput class:\n" + ex.getMessage());
                }
            }
            else
            {
                System.out.println(lineConsoleOutput);

                if (fileOutput != null)
                    fileOutput.println(lineConsoleOutput);
            }
        }
    }

    /**
    *  Overrides System.out.println(double) with this method.
    */
    public static void println(double lineConsoleOutput)
    {
        if (isLoggingOn)
        {
            if (isMultiCoreOn)
            {
                if (isShutDown)
                    throw new IllegalStateException("ConsoleOuput ShutDown is Imminent!");
                reservations.incrementAndGet();

                try
                {
                    queue.put( Tuples.tuple( true, String.valueOf(lineConsoleOutput) ) );
                }
                catch (InterruptedException ex)
                {
                    // restore the interuption status after catching InterruptedException
                    Thread.currentThread().interrupt();
                    System.out.println("InterruptedException with the queue.put() method in the ConsoleOutput class:\n" + ex.getMessage());
                }
            }
            else
            {
                System.out.println(lineConsoleOutput);

                if (fileOutput != null)
                    fileOutput.println(lineConsoleOutput);
            }
        }
    }

    /**
    *  Overrides System.out.println(float) with this method.
    */
    public static void println(float lineConsoleOutput)
    {
        if (isLoggingOn)
        {
            if (isMultiCoreOn)
            {
                if (isShutDown)
                    throw new IllegalStateException("ConsoleOuput ShutDown is Imminent!");
                reservations.incrementAndGet();

                try
                {
                    queue.put( Tuples.tuple( true, String.valueOf(lineConsoleOutput) ) );
                }
                catch (InterruptedException ex)
                {
                    // restore the interuption status after catching InterruptedException
                    Thread.currentThread().interrupt();
                    System.out.println("InterruptedException with the queue.put() method in the ConsoleOutput class:\n" + ex.getMessage());
                }
            }
            else
            {
                System.out.println(lineConsoleOutput);

                if (fileOutput != null)
                    fileOutput.println(lineConsoleOutput);
            }
        }
    }

    /**
    *  Overrides System.out.println(int) with this method.
    */
    public static void println(int lineConsoleOutput)
    {
        if (isLoggingOn)
        {
            if (isMultiCoreOn)
            {
                if (isShutDown)
                    throw new IllegalStateException("ConsoleOuput ShutDown is Imminent!");
                reservations.incrementAndGet();

                try
                {
                    queue.put( Tuples.tuple( true, String.valueOf(lineConsoleOutput) ) );
                }
                catch (InterruptedException ex)
                {
                    // restore the interuption status after catching InterruptedException
                    Thread.currentThread().interrupt();
                    System.out.println("InterruptedException with the queue.put() method in the ConsoleOutput class:\n" + ex.getMessage());
                }
            }
            else
            {
                System.out.println(lineConsoleOutput);

                if (fileOutput != null)
                    fileOutput.println(lineConsoleOutput);
            }
        }
    }

    /**
    *  Overrides System.out.println(long) with this method.
    */
    public static void println(long lineConsoleOutput)
    {
        if (isLoggingOn)
        {
            if (isMultiCoreOn)
            {
                if (isShutDown)
                    throw new IllegalStateException("ConsoleOuput ShutDown is Imminent!");
                reservations.incrementAndGet();

                try
                {
                    queue.put( Tuples.tuple( true, String.valueOf(lineConsoleOutput) ) );
                }
                catch (InterruptedException ex)
                {
                    // restore the interuption status after catching InterruptedException
                    Thread.currentThread().interrupt();
                    System.out.println("InterruptedException with the queue.put() method in the ConsoleOutput class:\n" + ex.getMessage());
                }
            }
            else
            {
                System.out.println(lineConsoleOutput);

                if (fileOutput != null)
                    fileOutput.println(lineConsoleOutput);
            }
        }
    }

    /**
    *  Overrides System.out.println(char[]) with this method.
    */
    public static void println(char[] lineConsoleOutput)
    {
        if (isLoggingOn)
        {
            if (isMultiCoreOn)
            {
                if (isShutDown)
                    throw new IllegalStateException("ConsoleOuput ShutDown is Imminent!");
                reservations.incrementAndGet();

                try
                {
                    queue.put( Tuples.tuple( true, new String(lineConsoleOutput) ) );
                }
                catch (InterruptedException ex)
                {
                    // restore the interuption status after catching InterruptedException
                    Thread.currentThread().interrupt();
                    System.out.println("InterruptedException with the queue.put() method in the ConsoleOutput class:\n" + ex.getMessage());
                }
            }
            else
            {
                System.out.println(lineConsoleOutput);

                if (fileOutput != null)
                    fileOutput.println(lineConsoleOutput);
            }
        }
    }

    /**
    *  Overrides System.out.println(java.lang.String) with this method.
    */
    public static void println(String lineConsoleOutput)
    {
        if (isLoggingOn)
        {
            if (isMultiCoreOn)
            {
                if (isShutDown)
                    throw new IllegalStateException("ConsoleOuput ShutDown is Imminent!");
                reservations.incrementAndGet();

                try
                {
                    queue.put( Tuples.tuple(true, lineConsoleOutput) );
                }
                catch (InterruptedException ex)
                {
                    // restore the interuption status after catching InterruptedException
                    Thread.currentThread().interrupt();
                    System.out.println("InterruptedException with the queue.put() method in the ConsoleOutput class:\n" + ex.getMessage());
                }
            }
            else
            {
                System.out.println(lineConsoleOutput);

                if (fileOutput != null)
                    fileOutput.println(lineConsoleOutput);
            }
        }
    }

    /**
    *  Overrides System.out.println(java.lang.Object) with this method.
    */
    public static void println(Object lineConsoleOutput)
    {
        if (isLoggingOn)
        {
            if (isMultiCoreOn)
            {
                if (isShutDown)
                    throw new IllegalStateException("ConsoleOuput ShutDown is Imminent!");
                reservations.incrementAndGet();

                try
                {
                    queue.put( Tuples.tuple( true, String.valueOf(lineConsoleOutput) ) );
                }
                catch (InterruptedException ex)
                {
                    // restore the interuption status after catching InterruptedException
                    Thread.currentThread().interrupt();
                    System.out.println("InterruptedException with the queue.put() method in the ConsoleOutput class:\n" + ex.getMessage());
                }
            }
            else
            {
                System.out.println(lineConsoleOutput);

                if (fileOutput != null)
                    fileOutput.println(lineConsoleOutput);
            }
        }
    }

    /**
    *  Overrides System.out.printf(java.lang.String, Object... args) with this method.
    */
    public static void printf(String format, Object... args)
    {
        if (isLoggingOn)
        {
            if (isMultiCoreOn)
            {
                if (isShutDown)
                    throw new IllegalStateException("ConsoleOuput ShutDown is Imminent!");
                reservations.incrementAndGet();

                try
                {
                    queue.put( Tuples.tuple( false, String.format(format, args) ) );
                }
                catch (InterruptedException ex)
                {
                    // restore the interuption status after catching InterruptedException
                    Thread.currentThread().interrupt();
                    System.out.println("InterruptedException with the queue.put() method in the ConsoleOutput class:\n" + ex.getMessage());
                }
            }
            else
            {
                System.out.printf(format, args);

                if (fileOutput != null)
                    fileOutput.printf(format, args);
            }
        }
    }

    /**
    *  Overrides System.out.printf(java.util.Locale, java.lang.String, Object... args) with this method.
    */
    public static void printf(Locale locale, String format, Object... args)
    {
        if (isLoggingOn)
        {
            if (isMultiCoreOn)
            {
                if (isShutDown)
                    throw new IllegalStateException("ConsoleOuput ShutDown is Imminent!");
                reservations.incrementAndGet();

                try
                {
                    queue.put( Tuples.tuple( false, String.format(locale, format, args) ) );
                }
                catch (InterruptedException ex)
                {
                    // restore the interuption status after catching InterruptedException
                    Thread.currentThread().interrupt();
                    System.out.println("InterruptedException with the queue.put() method in the ConsoleOutput class:\n" + ex.getMessage());
                }
            }
            else
            {
                System.out.printf(locale, format, args);

                if (fileOutput != null)
                    fileOutput.printf(locale, format, args);
            }
        }
    }

    /**
    *  Overrides System.out.format(java.lang.String, Object... args) with this method.
    */
    public static void format(String format, Object... args)
    {
        if (isLoggingOn)
        {
            if (isMultiCoreOn)
            {
                if (isShutDown)
                    throw new IllegalStateException("ConsoleOuput ShutDown is Imminent!");
                reservations.incrementAndGet();

                try
                {
                    queue.put( Tuples.tuple( false, String.format(format, args) ) );
                }
                catch (InterruptedException ex)
                {
                    // restore the interuption status after catching InterruptedException
                    Thread.currentThread().interrupt();
                    System.out.println("InterruptedException with the queue.put() method in the ConsoleOutput class:\n" + ex.getMessage());
                }
            }
            else
            {
                System.out.format(format, args);

                if (fileOutput != null)
                    fileOutput.format(format, args);
            }
        }
    }

    /**
    *  Overrides System.out.format(java.util.Locale, java.lang.String, Object... args) with this method.
    */
    public static void format(Locale locale, String format, Object... args)
    {
        if (isLoggingOn)
        {
            if (isMultiCoreOn)
            {
                if (isShutDown)
                    throw new IllegalStateException("ConsoleOuput ShutDown is Imminent!");
                reservations.incrementAndGet();

                try
                {
                    queue.put( Tuples.tuple( false, String.format(locale, format, args) ) );
                }
                catch (InterruptedException ex)
                {
                    // restore the interuption status after catching InterruptedException
                    Thread.currentThread().interrupt();
                    System.out.println("InterruptedException with the queue.put() method in the ConsoleOutput class:\n" + ex.getMessage());
                }
            }
            else
            {
                System.out.format(locale, format, args);

                if (fileOutput != null)
                    fileOutput.format(locale, format, args);
            }
        }
    }

    /**
    *  Prints a String representing the hex format of a byte[] array.
    */
    public static void printByteArrayHexFormat(byte[] data)
    {
        if (isLoggingOn)
        {
            if (isMultiCoreOn)
            {
                if (isShutDown)
                    throw new IllegalStateException("ConsoleOuput ShutDown is Imminent!");
                reservations.incrementAndGet();

                try
                {
                    queue.put( Tuples.tuple( true, Utils.byteArrayHexFormat(data) ) );
                }
                catch (InterruptedException ex)
                {
                    // restore the interuption status after catching InterruptedException
                    Thread.currentThread().interrupt();
                    System.out.println("InterruptedException with the queue.put() method in the ConsoleOutput class:\n" + ex.getMessage());
                }
            }
            else
            {
                System.out.println( Utils.byteArrayHexFormat(data) );

                if (fileOutput != null)
                    fileOutput.println( Utils.byteArrayHexFormat(data) );
            }
        }
    }

    /**
    * The ConsoleOutputThread static inner class.
    */
    private static class ConsoleOutputThread extends Thread
    {

        @Override
        public void run()
        {
            try
            {
                this.setName("ConsoleOutputThread");
                
                while (true)
                {
                    try
                    {
                        if (isShutDown && reservations.get() == 0)
                            break;
                        Tuple2<Boolean, String> tuple2 = queue.take();
                        reservations.decrementAndGet();

                        if (tuple2.first)
                        {
                            System.out.println(tuple2.second);

                            if (fileOutput != null)
                                fileOutput.println(tuple2.second);
                        }
                        else
                        {
                            System.out.print(tuple2.second);

                            if (fileOutput != null)
                                fileOutput.print(tuple2.second);
                        }
                    }
                    catch (InterruptedException ex)
                    {
                        // restore the interuption status after catching InterruptedException
                        Thread.currentThread().interrupt();
                        if (!isShutDown)
                            System.out.println("InterruptedException with the queue.put() method in the ConsoleOutput class:\n" + ex.getMessage());
                        else
                            System.out.println("ConsoleOutput MultiCore Service Normal ShutDown.");
                    }
                }
            }
            finally
            {
                if (fileOutput != null)
                    fileOutput.close();
            }
        }

    }

    /**
    *  Initializes the console output thread variables.
    */
    private static boolean initConsoleOutputThreadVariables()
    {
        queue = new LinkedBlockingQueue<Tuple2<Boolean, String>>(CAPACITY);
        reservations = new AtomicInteger();
        consoleOutputThread = new ConsoleOutputThread();
        Runtime.getRuntime().addShutdownHook(new Thread()
        {

            @Override
            public void run()
            {
                ConsoleOutput.stop();
            }

        } );
        consoleOutputThread.setPriority(Thread.NORM_PRIORITY);
        consoleOutputThread.start();

        return true;
    }

    /**
    *  Stops the ConsoleOutputThread.
    */
    private static void stop()
    {
        isShutDown = true;
        consoleOutputThread.interrupt();
    }


}