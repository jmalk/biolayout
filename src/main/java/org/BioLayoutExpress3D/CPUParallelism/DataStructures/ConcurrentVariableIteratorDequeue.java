package org.BioLayoutExpress3D.CPUParallelism.DataStructures;

import java.util.*;
import java.util.concurrent.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* The ConcurrentVariableIteratorDequeue<T> class provides functionality for (standard) iterator, descendingIterator & randomized iterations of a LinkedBlockingDeque.
*
* ConcurrentVariableIteratorDequeue<T> uses the Adapter Method Design Pattern with multiple anonymous (nested) inner classes.
*
* @author Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*/

public class ConcurrentVariableIteratorDequeue<T> implements Iterable<T>
{

    /**
    *  LinkedBlockingDeque<T> reference to be used for the Iterable<T> anonymous inner classes.
    */
    private LinkedBlockingDeque<T> storage = null;

    /**
    *  The constructor of the ConcurrentVariableIteratorDequeue<T> class.
    *  Initializes the ConcurrentVariableIteratorDequeue<T> with a LinkedBlockingDeque<T>.
    */
    public ConcurrentVariableIteratorDequeue(LinkedBlockingDeque<T> storage)
    {
        this.storage = storage;
    }

    /**
    *  Callback of the LinkedBlockingDeque<T> iterator.
    *  Automatically used in for each loops from Java (as: for (<T> t : arr) ).
    *
    */
    @Override
    public Iterator<T> iterator()
    {
        return storage.iterator();
    }

    /**
    *  Callback of the LinkedBlockingDeque<T> descendingIterator.
    *  Automatically used in for each loops from Java (as: for (<T> t : arr) ).
    *
    */
    public Iterator<T> descendingIterator()
    {
        return storage.descendingIterator();
    }

    /**
    *  Randomized iterator.
    *  Used as: for (<T> t : arr.randomizedIterator()).
    *
    */
    @SuppressWarnings("unchecked") // suppresses the "unchecked" warning inside the method's (List<T>) cast
    public Iterable<T> randomizedIterator()
    {
        return new Iterable<T>()
        {
            @Override
            public Iterator<T> iterator()
            {
                List<T> randomized = null;

                try
                {
                    randomized = (List<T>)storage.getClass().newInstance();
                }
                catch (InstantiationException instExc)
                {
                    if (DEBUG_BUILD) println("Class " + storage.getClass().getName() + " cannot be instantiated:\n" + instExc.getMessage());

                    // just return a standard iterator for storage
                    return storage.iterator();
                }
                catch (IllegalAccessException  illExc)
                {
                    if (DEBUG_BUILD) println("Class " + storage.getClass().getName() + " cannot be accessed:\n" + illExc.getMessage());

                    // just return a standard iterator for storage
                    return storage.iterator();
                }

                randomized.addAll(0, storage);
                Collections.shuffle( randomized, new Random() );

                return randomized.iterator();
            }
        };
    }


}