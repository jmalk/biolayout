package org.BioLayoutExpress3D.GPUComputing.OpenCLContext;

import java.util.*;
import org.jocl.*;
import static org.jocl.CL.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
*  A helper class for tracking cl_events and printing
*  timing information for the execution of the commands that
*  are associated with the events.
*
* @author Marco Hutter, Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*/

public final class CLEventExecutionStatistics
{

    /**
    *  The collection of entries.
    */
    private HashMap<cl_event, Entry> entries = new HashMap<cl_event, Entry>();

    /**
    *  Adds the specified entry to this instance.
    *
    *  @param name A name for the event
    *  @param event The event
    */
    public void addEntry(String name, cl_event event)
    {
        entries.put( event, new Entry(name, event) );
    }

    /**
    *  Removes all entries.
    */
    public void clear()
    {
        entries.clear();
    }

    /**
    *  Normalizes the entries, so that the times are relative
    *  to the time when the first event was queued.
    */
    private void normalize()
    {
        long minSubmitTime = Long.MAX_VALUE;
        for ( Entry entry : entries.values() )
            minSubmitTime = Math.min( minSubmitTime, entry.getQueuedTime() );
        for ( Entry entry : entries.values() )
            entry.normalize(minSubmitTime);
    }

    /**
    *  Prints the statistics.
    */
    public void print()
    {
        normalize();

        if (DEBUG_BUILD)
        {
            ArrayList<Entry> list = new ArrayList<Entry>( entries.values() );
            Collections.sort(list);
            for (Entry entry : list)
                entry.print();
        }
    }

    /**
    *  Prints the statistics in reverse order.
    */
    public void printReverseOrder()
    {
        normalize();

        if (DEBUG_BUILD)
        {
            ArrayList<Entry> list = new ArrayList<Entry>( entries.values() );
            Collections.sort( list, Collections.reverseOrder() );
            for (Entry entry : list)
                entry.print();
        }
    }

    /**
    *  Gets the duration time .
    */
    public long getDurationTime(cl_event event)
    {
        Entry entry  = entries.get(event);
        return (entry != null) ? entries.get(event).getDurationTime() : 0;
    }

    /**
    *  A single entry of the ExecutionStatistics.
    */
    private class Entry implements Comparable<Entry>
    {
        /**
        *  The name of the Entry.
        */
        private String name = null;

        /**
        *  The submit time of the Entry.
        */
        private long submitTime[] = new long[1];

        /**
        *  The queued time of the Entry.
        */
        private long queuedTime[] = new long[1];

        /**
        *  The start time of the Entry.
        */
        private long startTime[] = new long[1];

        /**
        *  The end time of the Entry.
        */
        private long endTime[] = new long[1];

        /**
        *  The constructor of the Entry.
        */
        private Entry(String name, cl_event event)
        {
            this.name = name;

            clGetEventProfilingInfo(event, CL_PROFILING_COMMAND_QUEUED, Sizeof.cl_ulong, Pointer.to(queuedTime), null);
            clGetEventProfilingInfo(event, CL_PROFILING_COMMAND_SUBMIT, Sizeof.cl_ulong, Pointer.to(submitTime), null);
            clGetEventProfilingInfo(event, CL_PROFILING_COMMAND_START,  Sizeof.cl_ulong, Pointer.to(startTime), null);
            clGetEventProfilingInfo(event, CL_PROFILING_COMMAND_END,    Sizeof.cl_ulong, Pointer.to(endTime), null);
        }

        /**
        *  Normalizes the Entry.
        */
        private void normalize(long baseTime)
        {
            submitTime[0] -= baseTime;
            queuedTime[0] -= baseTime;
             startTime[0] -= baseTime;
               endTime[0] -= baseTime;
        }

        /**
        *  Gets the queued time.
        */
        private long getQueuedTime()
        {
            return queuedTime[0];
        }

        /**
        *  Gets the duration time.
        */
        private long getDurationTime()
        {
            return (endTime[0] - startTime[0]);
        }

        /**
        *  Prints the Entry.
        */
        private void print()
        {
            println("Event " + name + ": ");
            println("Queued : " + String.format("%8.3f", queuedTime[0] / 1e6) + " ms");
            println("Submit : " + String.format("%8.3f", submitTime[0] / 1e6) + " ms");
            println("Start  : " + String.format("%8.3f",  startTime[0] / 1e6) + " ms");
            println("End    : " + String.format("%8.3f",    endTime[0] / 1e6) + " ms");
            println("Time   : " + String.format("%8.3f", getDurationTime() / 1e6) + " ms");
        }

        /**
        *  Compares two Entry objects by their name.
        */
        @Override
        public int compareTo(Entry entryClass)
        {
            return entryClass.name.compareTo(name);
        }


    }


}