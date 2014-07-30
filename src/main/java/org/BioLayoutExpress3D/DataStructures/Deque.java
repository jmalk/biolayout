package org.BioLayoutExpress3D.DataStructures;

import java.util.*;

/**
*
* Deque<T> class with a general representation for a deque. It uses a LinkedList<T> for implementation as it has all the relevant methods.
*
* It also uses VariableIteratorList<T> for variable iteration to its data members with the for each loop.
*
* @see org.BioLayoutExpress3D.DataStructures.VariableIteratorList
* @author Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*/

public class Deque<T> implements Iterable<T>
{

    /**
    *  The stack data structure used for the deque.
    */
    private LinkedList<T> storage = null;

    /**
    *  Reference to the VariableIteratorList<T> object to implement variable iteration to the Deque<T>.
    */
    private VariableIteratorList<T> variableIterator = null;

    /**
    *  The constructor of the Deque<T> class.
    */
    public Deque()
    {
        storage = new LinkedList<T>();
        variableIterator = new VariableIteratorList<T>(storage);
    }

    /**
    *  Adds an element to the head (beginning) of the deque.
    */
    public void addFirst(T t)
    {
        storage.addFirst(t);
    }

    /**
    *  Adds an element to the tail (end) of the deque.
    */
    public void addLast(T t)
    {
        storage.addLast(t);
    }

    /**
    *  Gets the first element from the deque.
    */
    public T getFirst()
    {
        return storage.getFirst();
    }

    /**
    *  Gets the last element from the deque.
    */
    public T getLast()
    {
        return storage.getLast();
    }

    /**
    *  Removes the first element from the deque.
    */
    public T removeFirst()
    {
        return storage.removeFirst();
    }

    /**
    *  Removes the last element from the deque.
    */
    public T removeLast()
    {
        return storage.removeLast();
    }

    /**
    *  Removes the object from the deque.
    */
    public boolean remove(T o)
    {
        return storage.remove(o);
    }

    /**
    *  Returns the 1-based position where an object is in the deque.
    */
    public synchronized int search(Object o)
    {
        int i = storage.lastIndexOf(o);

        return (i >= 0) ? storage.size() - i : -1;
    }

    /**
    *  Tests if the deque is empty.
    */
    public boolean isEmpty()
    {
        return storage.isEmpty();
    }

    /**
    *  Returns the size of the deque.
    */
    public int size()
    {
        return storage.size();
    }

    /**
    *  Clears the deque.
    */
    public void clear()
    {
        storage.clear();
    }

    /**
    *  Returns an iterator for the deque. It is also used with the for each loop.
    */
    @Override
    public Iterator<T> iterator()
    {
        return variableIterator.iterator();
    }

    /**
    *  Forward iterator for the deque.
    *  Used as: for (<T> t : arr.forwardIterator()).
    *
    */
    public Iterable<T> forwardIterator()
    {
        return variableIterator.forwardIterator();
    }

    /**
    *  Reversed iterator for the deque.
    *  Used as: for (<T> t : arr.reversedIterator()).
    *
    */
    public Iterable<T> reversedIterator()
    {
        return variableIterator.reversedIterator();
    }

    /**
    *  Randomized iterator for the deque.
    *  Used as: for (<T> t : arr.randomizedIterator()).
    *
    */
    public Iterable<T> randomizedIterator()
    {
        return variableIterator.randomizedIterator();
    }

    /**
    *  Returns a string representation of the deque.
    */
    @Override
    public String toString()
    {
        return storage.toString();
    }


}