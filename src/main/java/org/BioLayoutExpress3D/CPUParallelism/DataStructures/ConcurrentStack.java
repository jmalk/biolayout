package org.BioLayoutExpress3D.CPUParallelism.DataStructures;

import java.util.*;
import java.util.concurrent.*;

/**
*
* ConcurrentStack<T> class with a general representation for a concurrent stack. It is backup by a LinkedBlockingDeque.
* It also uses ConcurrentVariableIteratorDequeue<T> for variable iteration to its data members with the for each loop.
*
* @see org.BioLayoutExpress3D.DataStructures.StackDataStructureTypes
* @see org.BioLayoutExpress3D.DataStructures.VariableIteratorList
* @author Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*/

public class ConcurrentStack<T> implements Iterable<T>
{

    /**
    *  The concurrent stack data structure.
    */
    private LinkedBlockingDeque<T> storage = null;

    /**
    *  Reference to the ConcurrentVariableIteratorDequeue<T> object to implement variable iteration to the ConcurrentStack<T>.
    */
    private ConcurrentVariableIteratorDequeue<T> variableIterator = null;

    /**
    *  The constructor of the ConcurrentStack<T> class.
    */
    public ConcurrentStack()
    {
        storage = new LinkedBlockingDeque<T>();
        variableIterator = new ConcurrentVariableIteratorDequeue<T>(storage);
    }

    /**
    *  Pushes an item onto the top of this concurrent stack.
    */
    public void push(T t)
    {
        storage.push(t);
    }

    /**
    *  Looks at the object at the top of this concurrent stack without removing it from the stack.
    */
    public T peek() throws EmptyStackException
    {
        if ( storage.isEmpty() )
            throw new EmptyStackException();

        return storage.peek();
    }

    /**
    *  Removes the object at the top of this concurrent stack and returns that object as the value of this function.
    */
    public T pop() throws EmptyStackException
    {
        if ( storage.isEmpty() )
            throw new EmptyStackException();

        return storage.pop();
    }

    /**
    *  Removes the object from the concurrent stack.
    */
    public boolean remove(T o)
    {
        return storage.remove(o);
    }

    /**
    *  Tests if the concurrent stack is empty.
    */
    public boolean isEmpty()
    {
        return storage.isEmpty();
    }

    /**
    *  Returns the size of the concurrent stack.
    */
    public int size()
    {
        return storage.size();
    }

    /**
    *  Clears the concurrent stack.
    */
    public void clear()
    {
        storage.clear();
    }

    /**
    *  Returns an iterator for the concurrent stack. It is also used with the for each loop.
    */
    @Override
    public Iterator<T> iterator()
    {
        return variableIterator.iterator();
    }

    /**
    *  Returns an descendingIterator for the concurrent stack. It is also used with the for each loop.
    */
    public Iterator<T> descendingIterator()
    {
        return variableIterator.descendingIterator();
    }

    /**
    *  Randomized iterator for the concurrent stack.
    *  Used as: for (<T> t : arr.randomizedIterator()).
    *
    */
    public Iterable<T> randomizedIterator()
    {
        return variableIterator.randomizedIterator();
    }

    /**
    *  Returns a string representation of the concurrent stack.
    */
    @Override
    public String toString()
    {
        return storage.toString();
    }


}