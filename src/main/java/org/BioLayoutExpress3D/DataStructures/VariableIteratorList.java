package org.BioLayoutExpress3D.DataStructures;

import java.util.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

/**
*
* The VariableIteratorList<T> class provides functionality for (standard) forward, reversed & randomized iterations of a List.
*
* VariableIteratorList<T> uses the Adapter Method Design Pattern with multiple anonymous (nested) inner classes.
*
* @author Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*/

public class VariableIteratorList<T> implements Iterable<T>
{

    /**
    *  List<T> reference to be used for the Iterable<T> anonymous inner classes.
    */
    private List<T> storage = null;

    /**
    *  The constructor of the VariableIteratorList<T> class.
    *  Initializes the VariableIteratorList<T> with a List<T>.
    */
    public VariableIteratorList(List<T> storage)
    {
        this.storage = storage;
    }

    /**
    *  Callback of the List<T> iterator.
    *  Automatically used in for each loops from Java (as: for (<T> t : arr) ).
    *
    */
    @Override
    public Iterator<T> iterator()
    {
        return storage.iterator();
    }

    /**
    *  Forward iterator.
    *  Used as: for (<T> t : arr.forwardIterator()).
    *
    */
    public Iterable<T> forwardIterator()
    {
        return new Iterable<T>()
        {
            @Override
            public Iterator<T> iterator()
            {
                return new Iterator<T>()
                {
                    int current = 0;

                    @Override
                    public boolean hasNext()
                    {
                        return current < storage.size();
                    }

                    @Override
                    public T next()
                    {
                        return storage.get(current++);
                    }

                    @Override
                    public void remove()
                    {
                        storage.remove(current);

                        // Not implemented
                        // throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    /**
    *  Reversed iterator.
    *  Used as: for (<T> t : arr.reversedIterator()).
    *
    */
    public Iterable<T> reversedIterator()
    {
        return new Iterable<T>()
        {
            @Override
            public Iterator<T> iterator()
            {
                return new Iterator<T>()
                {
                    int current = storage.size() - 1;

                    @Override
                    public boolean hasNext()
                    {
                        return current > -1;
                    }

                    @Override
                    public T next()
                    {
                        return storage.get(current--);
                    }

                    @Override
                    public void remove()
                    {
                        storage.remove(current);

                        // Not implemented
                        //throw new UnsupportedOperationException();
                    }
                };
            }
        };
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