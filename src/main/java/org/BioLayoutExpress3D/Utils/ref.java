package org.BioLayoutExpress3D.Utils;

/**
 * Wrapper to simulate pass by reference
 *
 * @author Tim Angus <tim.angus@roslin.ed.ac.uk>
 */
public class ref<T>
{
    T v;

    public ref()
    {
        this.v = null;
    }

    public ref(T v)
    {
        this.v = v;
    }

    public T get()
    {
        return v;
    }

    public void set(T v)
    {
        this.v = v;
    }

    public String toString()
    {
        if (v != null)
        {
            return v.toString();
        }

        return "null";
    }
}