package org.BioLayoutExpress3D.DataStructures;

import java.util.*;
import java.util.concurrent.*;
import static org.BioLayoutExpress3D.DataStructures.StackDataStructureTypes.*;

/**
*
* Stack<T> class with a general representation for a stack. It is a more correct Stack implementation
* in terms of design as it does not inherit all of Vector class's methods as Java's own implementation.
*
* It uses a StackDataStructureType enumeration to select what kind of data structure to use for a Stack.
* A  Vector<T> is like using Java's own implementation with thread synchronization.
* An ArrayList<T> is faster but without thread synchronization and a fast serial access.
* A  LinkedList<T> is faster but without thread synchronization and a fast random access.
* A  CopyOnWriteArrayList<T> is used for concurrent read/writes/iterations to it. It makes a fresh copy of the underlying array to be thread-safe.
*
* It also uses VariableIteratorList<T> for variable iteration to its data members with the for each loop.
*
* @see org.BioLayoutExpress3D.DataStructures.StackDataStructureTypes
* @see org.BioLayoutExpress3D.DataStructures.VariableIteratorList
* @author Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*/

public class Stack<T> implements Iterable<T>
{

    /**
    *  The stack data structure selectively is upcasted to an List<T> from either a Vector,
    *  an Arraylist, a LinkedList or an CopyOnWriteArrayList according to the enumeration selection.
    */
    private List<T> storage = null;

    /**
    *  Reference to the VariableIteratorList<T> object to implement variable iteration to the Stack<T>.
    */
    private VariableIteratorList<T> variableIterator = null;

    /**
    *  The constructor of the Stack<T> class. Initializes the stack with either a Vector,
    *  an Arraylist or a LinkedList according to the enumeration selection.
    */
    public Stack(StackDataStructureTypes stackDataStructureType)
    {
        if ( stackDataStructureType.equals(USE_VECTOR) )
            storage = new Vector<T>();
        else if ( stackDataStructureType.equals(USE_ARRAYLIST) )
            storage = new ArrayList<T>();
        else if ( stackDataStructureType.equals(USE_LINKEDLIST) )
            storage = new LinkedList<T>();
        else if ( stackDataStructureType.equals(USE_COPY_ON_WRITE_ARRAYLIST) )
            storage = new CopyOnWriteArrayList<T>();

        variableIterator = new VariableIteratorList<T>(storage);
    }

    /**
    *  Pushes an item onto the top of this stack.
    */
    public void push(T t)
    {
        storage.add(t);
    }

    /**
    *  Looks at the object at the top of this stack without removing it from the stack.
    */
    public T peek() throws EmptyStackException
    {
        if ( storage.isEmpty() )
            throw new EmptyStackException();

        return storage.get(storage.size() - 1);
    }

    /**
    *  Removes the object at the top of this stack and returns that object as the value of this function.
    */
    public T pop() throws EmptyStackException
    {
        if ( storage.isEmpty() )
            throw new EmptyStackException();

        return storage.remove(storage.size() - 1);
    }

    /**
    *  Removes the object from the stack.
    */
    public boolean remove(T o)
    {
        return storage.remove(o);
    }

    /**
    *  Returns the 1-based position where an object is in the stack.
    */
    public synchronized int search(Object o)
    {
        int i = storage.lastIndexOf(o);

        return (i >= 0) ? storage.size() - i : -1;
    }

    /**
    *  Tests if the stack is empty.
    */
    public boolean isEmpty()
    {
        return storage.isEmpty();
    }

    /**
    *  Returns the size of the stack.
    */
    public int size()
    {
        return storage.size();
    }

    /**
    *  Clears the stack.
    */
    public void clear()
    {
        storage.clear();
    }

    /**
    *  Returns an iterator for the stack. It is also used with the for each loop.
    */
    @Override
    public Iterator<T> iterator()
    {
        return variableIterator.iterator();
    }

    /**
    *  Forward iterator for the stack.
    *  Used as: for (<T> t : arr.forwardIterator()).
    *
    */
    public Iterable<T> forwardIterator()
    {
        return variableIterator.forwardIterator();
    }

    /**
    *  Reversed iterator for the stack.
    *  Used as: for (<T> t : arr.reversedIterator()).
    *
    */
    public Iterable<T> reversedIterator()
    {
        return variableIterator.reversedIterator();
    }

    /**
    *  Randomized iterator for the stack.
    *  Used as: for (<T> t : arr.randomizedIterator()).
    *
    */
    public Iterable<T> randomizedIterator()
    {
        return variableIterator.randomizedIterator();
    }

    /**
    *  Returns a string representation of the stack.
    */
    @Override
    public String toString()
    {
        return storage.toString();
    }


}