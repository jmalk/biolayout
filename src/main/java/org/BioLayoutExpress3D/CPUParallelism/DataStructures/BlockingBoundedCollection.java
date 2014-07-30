package org.BioLayoutExpress3D.CPUParallelism.DataStructures;

import java.util.*;
import java.util.concurrent.*;
import static org.BioLayoutExpress3D.CPUParallelism.DataStructures.BlockingBoundedCollectionDataStructureTypes.*;

/**
*
* BlockingBoundedCollection<T> class with a general representation for a collection.
* It uses a Semaphore to turn the collection into a blocking bounded collection.
*
* It uses a BlockingBoundedCollectionDataStructureTypes enumeration to select what kind of data structure to use for a Collection.
* A  Vector<T>.
* An ArrayList<T>.
* A  LinkedList<T>.
* A  CopyOnWriteArrayList<T>.
* A  HashSet<T>.
* A  CopyOnWriteArraySet<T>.
*
* @see org.BioLayoutExpress3D.CPUParallelism.DataStructures.BlockingBoundedCollectionDataStructureTypes
* @author Thanos Theo, 2008-2009-2010
* @version 3.0.0.0
*/

public class BlockingBoundedCollection<T> implements Iterable<T>
{

    /**
    *  The collection data structure used for the BlockingBoundedCollection.
    */
    private Collection<T> storage = null;

    /**
    *  Reference to the BlockingBoundedIteratorCollection<T> object to implement variable iteration to the Stack<T>.
    */
    private BlockingBoundedIteratorCollection<T> variableIterator = null;

    /**
    *  The Semaphore to be used to turn the collection into a blocking bounded collection.
    */
    private Semaphore semaphore = null;

    /**
    *  The constructor of the BlockingBoundedCollection<T> class.
    */
    public BlockingBoundedCollection(BlockingBoundedCollectionDataStructureTypes blockingBoundedCollectionDataStructureType, int bound)
    {
        if ( blockingBoundedCollectionDataStructureType.equals(USE_VECTOR) )
            storage = Collections.synchronizedCollection( new Vector<T>() );
        else if ( blockingBoundedCollectionDataStructureType.equals(USE_ARRAYLIST) )
            storage = Collections.synchronizedCollection( new ArrayList<T>() );
        else if ( blockingBoundedCollectionDataStructureType.equals(USE_LINKEDLIST) )
            storage = Collections.synchronizedCollection( new LinkedList<T>() );
        else if ( blockingBoundedCollectionDataStructureType.equals(USE_COPY_ON_WRITE_ARRAYLIST) )
            storage = Collections.synchronizedCollection( new CopyOnWriteArrayList<T>() );
        else if ( blockingBoundedCollectionDataStructureType.equals(USE_HASHSET) )
            storage = Collections.synchronizedCollection( new HashSet<T>() );
        else if ( blockingBoundedCollectionDataStructureType.equals(USE_COPY_ON_WRITE_ARRAYSET) )
            storage = Collections.synchronizedCollection( new CopyOnWriteArraySet<T>() );

        variableIterator = new BlockingBoundedIteratorCollection<T>(storage);
        semaphore = new Semaphore(bound);
    }

    /**
    *  Adds the object to the BlockingBoundedCollection.
    */
    public boolean add(T o) throws InterruptedException
    {
        semaphore.acquire();
        boolean wasAdded = false;
        try
        {
            wasAdded = storage.add(o);
            return wasAdded;
        }
        finally
        {
            if (!wasAdded)
                semaphore.release();
        }
    }

    /**
    *  Removes the object from the BlockingBoundedCollection.
    */
    public boolean remove(T o)
    {
        boolean wasRemoved = storage.remove(o);
        if (wasRemoved)
            semaphore.release();
        return wasRemoved;
    }

    /**
    *  Tests if the BlockingBoundedCollection is empty.
    */
    public boolean isEmpty()
    {
        return storage.isEmpty();
    }

    /**
    *  Returns the size of the BlockingBoundedCollection.
    */
    public int size()
    {
        return storage.size();
    }

    /**
    *  Clears the BlockingBoundedCollection. and releases all available Semaphore permits.
    */
    public void clear()
    {
        storage.clear();
        semaphore.release( semaphore.availablePermits() );
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
    *  Randomized iterator for the concurrent stack.
    *  Used as: for (<T> t : arr.randomizedIterator()).
    *
    */
    public Iterable<T> randomizedIterator()
    {
        return variableIterator.randomizedIterator();
    }

    /**
    *  Returns a string representation of the BlockingBoundedCollection.
    */
    @Override
    public String toString()
    {
        return storage.toString();
    }


}