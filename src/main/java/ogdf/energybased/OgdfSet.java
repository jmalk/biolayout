package ogdf.energybased;

/*
 * $Revision: 2552 $
 *
 * last checkin:
 *   $Author: gutwenger $
 *   $Date: 2012-07-05 16:45:20 +0200 (Do, 05. Jul 2012) $
 ***************************************************************/

/**
 * \file \brief Implementation of class Set.
 *
 * \author Stefan Hachul
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
import ogdf.basic.*;

public class OgdfSet
{

    private boolean using_S_node; //indicates weather S_item, or S_node is used
    private List<node> S_node;       //representation of the node set S_node[0,G.number_of_nodes()-1]
    private int last_selectable_index_of_S_node;//index of the last randomly choosable element
    //in S_node (this value is decreased after each
    //select operation)
    private NodeArray<Integer> position_in_node_set;//holds for each node of G the index of its
    //position in S_node
    private NodeArray<Integer> mass_of_star; //the sum of the masses of a node and its neighbours
    private Random random;

    public OgdfSet(Random random)
    {
        last_selectable_index_of_S_node = -1;
        S_node = null;
        using_S_node = false;
        this.random = random;
    }

    public void set_seed(int rand_seed)
    {
        random.setSeed(rand_seed);
    }

    public void init_node_set(Graph G)
    {
        using_S_node = true;
        node v;

        S_node = new ArrayList<node>();
        for (int i = 0; i < G.numberOfNodes(); i++)
        {
            S_node.add(new node(G, i));
        }

        position_in_node_set = new NodeArray<Integer>(G, Factory.INTEGER);

        for (Iterator<node> i = G.nodesIterator(); i.hasNext();)
        {
            v = i.next();
            S_node.set(v.index(), v);
            position_in_node_set.set(v, v.index());
        }
        last_selectable_index_of_S_node = G.numberOfNodes() - 1;
    }

    public boolean empty_node_set()
    {
        if (last_selectable_index_of_S_node < 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean is_deleted(node v)
    {
        if (position_in_node_set.get(v) > last_selectable_index_of_S_node)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void delete_node(node del_node)
    {
        int del_node_index = position_in_node_set.get(del_node);
        node last_selectable_node = S_node.get(last_selectable_index_of_S_node);

        S_node.set(last_selectable_index_of_S_node, del_node);
        S_node.set(del_node_index, last_selectable_node);
        position_in_node_set.set(del_node, last_selectable_index_of_S_node);
        position_in_node_set.set(last_selectable_node, del_node_index);
        last_selectable_index_of_S_node -= 1;
    }

//---------------- for set of nodes with uniform probability -------------------
    public node get_random_node()
    {
        int rand_index = random.nextInt(last_selectable_index_of_S_node + 1);
        node random_node = S_node.get(rand_index);
        node last_selectable_node = S_node.get(last_selectable_index_of_S_node);

        S_node.set(last_selectable_index_of_S_node, random_node);
        S_node.set(rand_index, last_selectable_node);
        position_in_node_set.set(random_node, last_selectable_index_of_S_node);
        position_in_node_set.set(last_selectable_node, rand_index);
        last_selectable_index_of_S_node -= 1;
        return random_node;
    }

//---------------- for set of nodes with weighted  probability ------------------
    public void init_node_set(Graph G, NodeArray<NodeAttributes> A)
    {
        node v, v_adj;

        init_node_set(G);
        mass_of_star = new NodeArray<Integer>(G, Factory.INTEGER);
        for (Iterator<node> i = G.nodesIterator(); i.hasNext();)
        {
            v = i.next();
            mass_of_star.set(v, A.get(v).get_mass());
            for (edge e_adj : v.adjEdges())
            {
                if (e_adj.source() != v)
                {
                    v_adj = e_adj.source();
                }
                else
                {
                    v_adj = e_adj.target();
                }
                mass_of_star.set(v, mass_of_star.get(v) + A.get(v_adj).get_mass());
            }
        }
    }

//---------------- for set of nodes with ``lower mass'' probability --------------
    public node get_random_node_with_lowest_star_mass(int rand_tries)
    {
        int rand_index = 0, new_rand_index, min_mass = 0;
        int i = 1;
        node random_node = null, new_rand_node, last_trie_node, last_selectable_node;

        //randomly select rand_tries distinct!!! nodes from S_node and select the one
        //with the lowest mass

        int last_trie_index = last_selectable_index_of_S_node;
        while ((i <= rand_tries) && (last_trie_index >= 0))
        {//while
            last_trie_node = S_node.get(last_trie_index);
            new_rand_index = random.nextInt(last_trie_index + 1);
            new_rand_node = S_node.get(new_rand_index);
            S_node.set(last_trie_index, new_rand_node);
            S_node.set(new_rand_index, last_trie_node);
            position_in_node_set.set(new_rand_node, last_trie_index);
            position_in_node_set.set(last_trie_node, new_rand_index);

            if ((i == 1) || (min_mass > mass_of_star.get(S_node.get(last_trie_index))))
            {
                rand_index = last_trie_index;
                random_node = S_node.get(last_trie_index);
                min_mass = mass_of_star.get(random_node);
            }
            i++;
            last_trie_index -= 1;
        }//while

        //now rand_index and random_node have been fixed
        last_selectable_node = S_node.get(last_selectable_index_of_S_node);
        S_node.set(last_selectable_index_of_S_node, random_node);
        S_node.set(rand_index, last_selectable_node);
        position_in_node_set.set(random_node, last_selectable_index_of_S_node);
        position_in_node_set.set(last_selectable_node, rand_index);
        last_selectable_index_of_S_node -= 1;
        return random_node;
    }

//---------------- for set of nodes with ``higher mass'' probability --------------
    public node get_random_node_with_highest_star_mass(int rand_tries)
    {
        int rand_index = 0, new_rand_index, min_mass = 0;
        int i = 1;
        node random_node = null, new_rand_node, last_trie_node, last_selectable_node;

        //randomly select rand_tries distinct!!! nodes from S_node and select the one
        //with the lowest mass

        int last_trie_index = last_selectable_index_of_S_node;
        while ((i <= rand_tries) && (last_trie_index >= 0))
        {//while
            last_trie_node = S_node.get(last_trie_index);
            new_rand_index = random.nextInt(last_trie_index + 1);
            new_rand_node = S_node.get(new_rand_index);
            S_node.set(last_trie_index, new_rand_node);
            S_node.set(new_rand_index, last_trie_node);
            position_in_node_set.set(new_rand_node, last_trie_index);
            position_in_node_set.set(last_trie_node, new_rand_index);

            if ((i == 1) || (min_mass < mass_of_star.get(S_node.get(last_trie_index))))
            {
                rand_index = last_trie_index;
                random_node = S_node.get(last_trie_index);
                min_mass = mass_of_star.get(random_node);
            }
            i++;
            last_trie_index -= 1;
        }//while

        //now rand_index and random_node have been fixed
        last_selectable_node = S_node.get(last_selectable_index_of_S_node);
        S_node.set(last_selectable_index_of_S_node, random_node);
        S_node.set(rand_index, last_selectable_node);
        position_in_node_set.set(random_node, last_selectable_index_of_S_node);
        position_in_node_set.set(last_selectable_node, rand_index);
        last_selectable_index_of_S_node -= 1;
        return random_node;
    }
}
