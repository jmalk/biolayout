package org.BioLayoutExpress3D.Utils;

import java.util.*;
import java.util.concurrent.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* @author Anton Enright, Full refactoring by Thanos Theo, 2008-2009
* @version 3.0.0.0
*
*/

public final class MemoryFootPrint
{
    private static long startMemory = 0;
    private static ArrayList<SummaryUnit> summary = new ArrayList<SummaryUnit>();
    private static int pause = 3000;

    public static void startMemory(String checkpointName)
    {
        if (DEBUG_BUILD)
        {
            if (DEBUG_BUILD) println("[" + checkpointName + "]");

            System.gc();

            if (DEBUG_BUILD) println("Sleeping");

            try
            {
                TimeUnit.MILLISECONDS.sleep(pause);
            }
            catch (InterruptedException ex)
            {
                // restore the interuption status after catching InterruptedException
                Thread.currentThread().interrupt();
            }

            startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        }
    }

    public static void endMemory(String endCheckPoint)
    {
        if (DEBUG_BUILD)
        {
            System.gc();

            if (DEBUG_BUILD) println("Sleeping");

            try
            {
                TimeUnit.MILLISECONDS.sleep(pause);
            }
            catch (InterruptedException e) {}

            long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long change = endMemory - startMemory;

            if (DEBUG_BUILD) println( "[" + endCheckPoint + "]" + formatBytes(change) + " " + toMegabytes(endMemory) );

            summary.add( new SummaryUnit(endCheckPoint, change, endMemory) );
        }
    }

    private static String formatBytes(long bytes)
    {
        return (bytes + "bytes " + toKilobytes(bytes) + "kb  " + toMegabytes(bytes) + "Mb");
    }

    private static long toKilobytes(long bytes)
    {
        return (bytes / (1 << 10) ); // 1024
    }

    private static long toMegabytes(long bytes)
    {
        return (bytes / (1 << 20) ); //(1024 * 1024)
    }

    public static void clear()
    {
        summary.clear();
    }
    public static void printSummary()
    {
        int total = 0;
        for (SummaryUnit su : summary)
            total += su.bytes;

        if (DEBUG_BUILD)
        {
            println("------------------------------------------");
            println("Total Monitored: " + toMegabytes(total));
            println("------------------------------------------");
        }

        int i = 0;
        for (SummaryUnit su : summary)
        {
            double percent = ( (double)su.bytes / (double)total ) * 100;
            percent = Math.rint(percent);

            if (DEBUG_BUILD)
            {
                println(su.bytes + " " + total + " " + percent);
                println("[" + ++i + "]\t" + toMegabytes(su.bytes) + "\t" + toMegabytes(su.totalBytes) + "\t" + percent + "%\t" + su.description);
            }
        }

        if (DEBUG_BUILD)
            println("------------------------------------------");
    }

    private static class SummaryUnit
    {
        public String description = "";
        public long bytes = 0;
        public long totalBytes = 0;

        private SummaryUnit(String description, long bytes, long totalBytes)
        {
             this.description = description;
             this.bytes = bytes;
             this.totalBytes = totalBytes;
        }


    }


}