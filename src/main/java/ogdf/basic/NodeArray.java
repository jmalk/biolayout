package ogdf.basic;

/*
 * $Revision: 2615 $
 *
 * last checkin:
 *   $Author: gutwenger $
 *   $Date: 2012-07-16 14:23:36 +0200 (Mo, 16. Jul 2012) $
 ***************************************************************/
/**
 * \file \brief Declaration and implementation of NodeArray class
 *
 * \author Carsten Gutwenger
 *
 * \par License: This file is part of the Open Graph Drawing Framework (OGDF).
 *
 * \par Copyright (C)<br> See README.txt in the root directory of the OGDF installation for details.
 *
 * \par This program is free software; you can redistribute it and/or modify it under the terms of the GNU General
 * Public License Version 2 or 3 as published by the Free Software Foundation; see the file LICENSE.txt included in the
 * packaging of this file for details.
 *
 * \par This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * \par You should have received a copy of the GNU General Public License along with this program; if not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * \see http://www.gnu.org/copyleft/gpl.html
 **************************************************************
 */
import java.util.*;
import java.lang.reflect.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

//! Dynamic arrays indexed with nodes.
/**
 * Node arrays represent a mapping from nodes to data of type \a T. They adjust their table size automatically when the
 * graph grows.
 *
 * @tparam T is the element type.
 */
public class NodeArray<T> extends ArrayList<T>
{
    /* NodeArrayBase */
    /**
     * Pointer to list element in the list of all registered node arrays which references this array.
     */
    //ListIterator<NodeArrayBase*> m_it;
    public Graph m_pGraph; //!< The associated graph.

    //! Associates the array with a new graph.
    void reregister(Graph pG, Factory<T> f)
    {
        if (m_pGraph != null)
        {
            //m_pGraph.unregisterArray(m_it);
        }
        if ((m_pGraph = pG) != null)
        {
            //m_it = pG.registerArray(this);
            createInstances(f);
        }
    }
    /* ~NodeArrayBase */

    T m_x; //!< The default value for array elements.

    //! Constructs an empty node array associated with no graph.
    public NodeArray()
    {
        super();
        m_pGraph = null;
    }

    //! Constructs a node array associated with \a G.
    public NodeArray(Graph G, Factory<T> f)
    {
        super(G.numberOfNodes());
        m_pGraph = G;
        createInstances(f);
        //if(G) m_it = pG->registerArray(this);
    }

    //! Constructs a node array associated with \a G.
    /**
     * @param G is the associated graph.
     * @param x is the default value for all array elements.
     */
    public NodeArray(Graph G, T x, Factory<T> f)
    {
        this(G, f);
        m_x = x;
    }

    private void createInstances(Factory<T> f)
    {
        if (f == null)
        {
            return;
        }

        for (int i = 0; i < m_pGraph.numberOfNodes(); i++)
        {
            super.add(f.newInstance());
        }
    }

    //! Returns true iff the array is associated with a graph.
    public boolean valid()
    {
        return m_pGraph != null;
    }

    //! Returns a pointer to the associated graph.
    public Graph graphOf()
    {
        return m_pGraph;
    }

    //! Returns a reference to the element with index \a v.
    public T get(node v)
    {
        assert v != null && v.graphOf() == m_pGraph;
        int index = v.index();
        return super.get(index);
    }

    public void set(node v, T value)
    {
        assert v != null && v.graphOf() == m_pGraph;
        super.ensureCapacity(v.index() + 1);

        if (v.index() < super.size() && super.get(v.index()) != null)
        {
            super.set(v.index(), value);
        }
        else
        {
            super.add(v.index(), value);
        }
    }

    //! Reinitializes the array. Associates the array with no graph.
    public void init()
    {
        init(null, null);
    }

    //! Reinitializes the array. Associates the array with \a G.
    public void init(Graph G, Factory<T> f)
    {
        super.clear();
        reregister(G, f);
    }

    //! Reinitializes the array. Associates the array with \a G.
    /**
     * @param G is the associated graph.
     * @param x is the default value.
     */
    public void init(Graph G, T x, Factory<T> f)
    {
        super.clear();
        m_x = x;
        reregister(G, f);
    }

    //! Sets all array elements to \a x.
    public void fill(T x)
    {
        for (int i = 0; i < size(); i++)
        {
            set(i, x);
        }
    }
}