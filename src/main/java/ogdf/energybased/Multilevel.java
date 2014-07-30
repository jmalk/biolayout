package ogdf.energybased;

/*
 * $Revision: 2552 $
 *
 * last checkin:
 *   $Author: gutwenger $
 *   $Date: 2012-07-05 16:45:20 +0200 (Do, 05. Jul 2012) $
 ***************************************************************/

/** \file
 * \brief Implementation of class Multlevel (used by FMMMLayout).
 *
 * \author Stefan Hachul
 *
 * \par License:
 * This file is part of the Open Graph Drawing Framework (OGDF).
 *
 * \par
 * Copyright (C)<br>
 * See README.txt in the root directory of the OGDF installation for details.
 *
 * \par
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * Version 2 or 3 as published by the Free Software Foundation;
 * see the file LICENSE.txt included in the packaging of this file
 * for details.
 *
 * \par
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * \par
 * You should have received a copy of the GNU General Public
 * License along with this program; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA 02110-1301, USA.
 *
 * \see  http://www.gnu.org/copyleft/gpl.html
 ***************************************************************/

import java.util.*;
import ogdf.basic.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

public class Multilevel
{
    int bad_edgenr_counter;
    Random random;

    public Multilevel(Random random)
    {
        this.random = random;
    }

    int create_multilevel_representations(
            Graph G,
            NodeArray<NodeAttributes> A,
            EdgeArray<EdgeAttributes> E,
            int rand_seed,
            FMMMLayout.GalaxyChoice galaxy_choice,
            int min_Graph_size,
            int random_tries,
            List<Graph> G_mult_ptr,
            List<NodeArray<NodeAttributes>> A_mult_ptr,
            List<EdgeArray<EdgeAttributes>> E_mult_ptr)
    {
	//make initialisations;
	//srand(rand_seed);
	G_mult_ptr.set(0, G); //init graph at level 0 to the original undirected simple
        A_mult_ptr.set(0, A); //and loopfree connected graph G/A/E
        E_mult_ptr.set(0, E);

        bad_edgenr_counter = 0;
        int act_level = 0;
        Graph act_Graph_ptr = G_mult_ptr.get(0);

        while ((act_Graph_ptr.numberOfNodes() > min_Graph_size) &&
                edgenumbersum_of_all_levels_is_linear(G_mult_ptr, act_level))
        {
            Graph G_new = new Graph();
            NodeArray<NodeAttributes> A_new = new NodeArray<NodeAttributes>();
            EdgeArray<EdgeAttributes> E_new = new EdgeArray<EdgeAttributes>();
            G_mult_ptr.set(act_level + 1, G_new);
            A_mult_ptr.set(act_level + 1, A_new);
            E_mult_ptr.set(act_level + 1, E_new);

            init_multilevel_values(G_mult_ptr, A_mult_ptr, E_mult_ptr, act_level);
            partition_galaxy_into_solar_systems(G_mult_ptr, A_mult_ptr, E_mult_ptr, rand_seed,
                    galaxy_choice, random_tries, act_level);
            collaps_solar_systems(G_mult_ptr, A_mult_ptr, E_mult_ptr, act_level);

            act_level++;
            act_Graph_ptr = G_mult_ptr.get(act_level);
        }
        return act_level;
    }


    boolean edgenumbersum_of_all_levels_is_linear(
            List<Graph> G_mult_ptr,
            int act_level)
    {
        if (act_level == 0)
        {
            return true;
        }
        else
        {
            if (G_mult_ptr.get(act_level).numberOfEdges() <=
                    0.8 * (double) (G_mult_ptr.get(act_level - 1).numberOfEdges()))
            {
                return true;
            }
            else if (bad_edgenr_counter < 5)
            {
                bad_edgenr_counter++;
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    void init_multilevel_values(
            List<Graph> G_mult_ptr,
            List<NodeArray<NodeAttributes>> A_mult_ptr,
            List<EdgeArray<EdgeAttributes>> E_mult_ptr,
            int level)
    {
        node v;
        for (Iterator<node> i = G_mult_ptr.get(level).nodesIterator(); i.hasNext();)
        {
            v = i.next();
            A_mult_ptr.get(level).get(v).init_mult_values();
        }

        edge e;
        for (Iterator<edge> i = G_mult_ptr.get(level).edgesIterator(); i.hasNext();)
        {
            e = i.next();
            E_mult_ptr.get(level).get(e).init_mult_values();
        }
    }

    void partition_galaxy_into_solar_systems(
            List<Graph> G_mult_ptr,
            List<NodeArray<NodeAttributes>> A_mult_ptr,
            List<EdgeArray<EdgeAttributes>> E_mult_ptr,
            int rand_seed,
            FMMMLayout.GalaxyChoice galaxy_choice,
            int random_tries,
            int act_level)
    {
        create_suns_and_planets(G_mult_ptr, A_mult_ptr, E_mult_ptr, rand_seed, galaxy_choice,
                random_tries, act_level);
        create_moon_nodes_and_pm_nodes(G_mult_ptr, A_mult_ptr, E_mult_ptr, act_level);
    }

    void create_suns_and_planets(
            List<Graph> G_mult_ptr,
            List<NodeArray<NodeAttributes>> A_mult_ptr,
            List<EdgeArray<EdgeAttributes>> E_mult_ptr,
            int rand_seed,
            FMMMLayout.GalaxyChoice galaxy_choice,
            int random_tries,
            int act_level)
    {
        OgdfSet Node_Set = new OgdfSet(random);
        node v, sun_node, planet_node, newNode, pos_moon_node;
        edge e;
        double dist_to_sun;
        List<node> planet_nodes = new ArrayList<node>();
        List<node> sun_nodes = new ArrayList<node>();

        //make initialisations
        sun_nodes.clear();
        Node_Set.set_seed(rand_seed); //set seed for random number generator
        for (Iterator<node> i = G_mult_ptr.get(act_level).nodesIterator(); i.hasNext();)
        {
            v = i.next();
            if (act_level == 0)
            {
                A_mult_ptr.get(act_level).get(v).set_mass(1);
            }
        }

        if (galaxy_choice == FMMMLayout.GalaxyChoice.gcUniformProb)
        {
            Node_Set.init_node_set(G_mult_ptr.get(act_level));
        }
        else //galaxy_choice != gcUniformProb in FMMMLayout
        {
            Node_Set.init_node_set(G_mult_ptr.get(act_level), A_mult_ptr.get(act_level));
        }


        while (!Node_Set.empty_node_set())
        {//while
            //randomly select a sun node
            planet_nodes.clear();
            if (galaxy_choice == FMMMLayout.GalaxyChoice.gcUniformProb)
            {
                sun_node = Node_Set.get_random_node();
            }
            else if (galaxy_choice == FMMMLayout.GalaxyChoice.gcNonUniformProbLowerMass)
            {
                sun_node = Node_Set.get_random_node_with_lowest_star_mass(random_tries);
            }
            else //galaxy_choice == FMMMLayout::gcNonUniformProbHigherMass
            {
                sun_node = Node_Set.get_random_node_with_highest_star_mass(random_tries);
            }
            sun_nodes.add(sun_node);

            //create new node at higher level that represents the collapsed solar_system
            newNode = G_mult_ptr.get(act_level + 1).newNode();

            //update information for sun_node
            A_mult_ptr.get(act_level).get(sun_node).set_higher_level_node(newNode);
            A_mult_ptr.get(act_level).get(sun_node).set_type(1);
            A_mult_ptr.get(act_level).get(sun_node).set_dedicated_sun_node(sun_node);
            A_mult_ptr.get(act_level).get(sun_node).set_dedicated_sun_distance(0);

            //update information for planet_nodes
            for (edge sun_edge : sun_node.adjEdges())
            {
                dist_to_sun = E_mult_ptr.get(act_level).get(sun_edge).get_length();
                if (sun_edge.source() != sun_node)
                {
                    planet_node = sun_edge.source();
                }
                else
                {
                    planet_node = sun_edge.target();
                }
                A_mult_ptr.get(act_level).get(planet_node).set_type(2);
                A_mult_ptr.get(act_level).get(planet_node).set_dedicated_sun_node(sun_node);
                A_mult_ptr.get(act_level).get(planet_node).set_dedicated_sun_distance(dist_to_sun);
                planet_nodes.add(planet_node);
            }

            //delete all planet_nodes and possible_moon_nodes from Node_Set

            //forall_listiterators(node,planet_node_ptr,planet_nodes)
            for (node planet_node_ptr : planet_nodes)
            {
                if (!Node_Set.is_deleted(planet_node_ptr))
                {
                    Node_Set.delete_node(planet_node_ptr);
                }
            }

            for (node planet_node_ptr : planet_nodes)
            //forall_listiterators(node,planet_node_ptr,planet_nodes)
            {
                for (edge planet_edge : planet_node_ptr.adjEdges())
                {
                    if (planet_edge.source() == planet_node_ptr)
                    {
                        pos_moon_node = planet_edge.target();
                    }
                    else
                    {
                        pos_moon_node = planet_edge.source();
                    }
                    if (!Node_Set.is_deleted(pos_moon_node))
                    {
                        Node_Set.delete_node(pos_moon_node);
                    }
                }
            }
        }//while

        //init *A_mult_ptr[act_level+1] and set NodeAttributes information for new nodes
        A_mult_ptr.get(act_level + 1).init(G_mult_ptr.get(act_level + 1), Factory.NODE_ATTRIBUTES);
        for (node sun_node_ptr : sun_nodes)
        {
            newNode = A_mult_ptr.get(act_level).get(sun_node_ptr).get_higher_level_node();
            A_mult_ptr.get(act_level + 1).get(newNode).set_NodeAttributes(
                    A_mult_ptr.get(act_level).get(sun_node_ptr).get_width(),
                    A_mult_ptr.get(act_level).get(sun_node_ptr).get_height(),
                    A_mult_ptr.get(act_level).get(sun_node_ptr).get_position(),
                    sun_node_ptr, null);
            A_mult_ptr.get(act_level + 1).get(newNode).set_mass(0);
        }
    }


    void create_moon_nodes_and_pm_nodes(
            List<Graph> G_mult_ptr,
            List<NodeArray<NodeAttributes>> A_mult_ptr,
            List<EdgeArray<EdgeAttributes>> E_mult_ptr,
            int act_level)
    {
        node v, nearest_neighbour_node = null, neighbour_node, dedicated_sun_node;
        double dist_to_nearest_neighbour = 0.0, dedicated_sun_distance;
        boolean first_adj_edge;
        int neighbour_type;
        edge moon_edge = null;

        for (Iterator<node> i = G_mult_ptr.get(act_level).nodesIterator(); i.hasNext();)
        {
            v = i.next();
            if (A_mult_ptr.get(act_level).get(v).get_type() == 0) //a moon node
            {//forall
                //find nearest neighbour node
                first_adj_edge = true;
                for (edge e : v.adjEdges())
                {//forall2
                    if (v == e.source())
                    {
                        neighbour_node = e.target();
                    }
                    else
                    {
                        neighbour_node = e.source();
                    }
                    neighbour_type = A_mult_ptr.get(act_level).get(neighbour_node).get_type();
                    if ((neighbour_type == 2) || (neighbour_type == 3))
                    {//if_1
                        if (first_adj_edge)
                        {//if
                            first_adj_edge = false;
                            moon_edge = e;
                            dist_to_nearest_neighbour = E_mult_ptr.get(act_level).get(e).get_length();
                            nearest_neighbour_node = neighbour_node;
                        }//if
                        else if (dist_to_nearest_neighbour > E_mult_ptr.get(act_level).get(e).get_length())
                        {//else
                            moon_edge = e;
                            dist_to_nearest_neighbour = E_mult_ptr.get(act_level).get(e).get_length();
                            nearest_neighbour_node = neighbour_node;
                        }//else
                    }//if_1
                }//forall2
                //find dedic. solar system for v and update information in *A_mult_ptr[act_level]
                //and *E_mult_ptr[act_level]

                E_mult_ptr.get(act_level).get(moon_edge).make_moon_edge(); //mark this edge
                dedicated_sun_node = A_mult_ptr.get(act_level).get(nearest_neighbour_node).
                        get_dedicated_sun_node();
                dedicated_sun_distance = dist_to_nearest_neighbour +
                        A_mult_ptr.get(act_level).get(nearest_neighbour_node).get_dedicated_sun_distance();
                A_mult_ptr.get(act_level).get(v).set_type(4);
                A_mult_ptr.get(act_level).get(v).set_dedicated_sun_node(dedicated_sun_node);
                A_mult_ptr.get(act_level).get(v).set_dedicated_sun_distance(dedicated_sun_distance);
                A_mult_ptr.get(act_level).get(v).set_dedicated_pm_node(nearest_neighbour_node);

                //identify nearest_neighbour_node as a pm_node and update its information

                A_mult_ptr.get(act_level).get(nearest_neighbour_node).set_type(3);
                A_mult_ptr.get(act_level).get(nearest_neighbour_node).get_dedicated_moon_node_List().add(v);
            }//forall
        }
    }

    void collaps_solar_systems(
            List<Graph> G_mult_ptr,
            List<NodeArray<NodeAttributes>> A_mult_ptr,
            List<EdgeArray<EdgeAttributes>> E_mult_ptr,
            int act_level)
    {
        EdgeArray<Double> new_edgelength = new EdgeArray<Double>();
        calculate_mass_of_collapsed_nodes(G_mult_ptr, A_mult_ptr, act_level);
        create_edges_edgedistances_and_lambda_Lists(G_mult_ptr, A_mult_ptr, E_mult_ptr,
                new_edgelength, act_level);
        delete_parallel_edges_and_update_edgelength(G_mult_ptr, E_mult_ptr, new_edgelength,
                act_level);
    }

    void calculate_mass_of_collapsed_nodes(
            List<Graph> G_mult_ptr,
            List<NodeArray<NodeAttributes>> A_mult_ptr,
            int act_level)
    {
        node v;
        node dedicated_sun, high_level_node;

        for (Iterator<node> i = G_mult_ptr.get(act_level).nodesIterator(); i.hasNext();)
        {
            v = i.next();
            dedicated_sun = A_mult_ptr.get(act_level).get(v).get_dedicated_sun_node();
            high_level_node = A_mult_ptr.get(act_level).get(dedicated_sun).get_higher_level_node();
            A_mult_ptr.get(act_level + 1).get(high_level_node).set_mass(
                    A_mult_ptr.get(act_level + 1).get(high_level_node).get_mass() + 1);
        }
    }

    void create_edges_edgedistances_and_lambda_Lists(
            List<Graph> G_mult_ptr,
            List<NodeArray<NodeAttributes>> A_mult_ptr,
            List<EdgeArray<EdgeAttributes>> E_mult_ptr,
            EdgeArray<Double> new_edgelength, int act_level)
    {
        edge e, e_new;
        node s_node, t_node;
        node s_sun_node, t_sun_node;
        node high_level_sun_s, high_level_sun_t;
        double length_e, length_s_edge, length_t_edge, newlength;
        double lambda_s, lambda_t;
        List<edge> inter_solar_system_edges = new ArrayList<edge>();

        //create new edges at act_level+1 and create for each inter solar system edge  at
        //act_level a link to its corresponding edge

        for (Iterator<edge> i = G_mult_ptr.get(act_level).edgesIterator(); i.hasNext();)
        {//forall
            e = i.next();

            s_node = e.source();
            t_node = e.target();
            s_sun_node = A_mult_ptr.get(act_level).get(s_node).get_dedicated_sun_node();
            t_sun_node = A_mult_ptr.get(act_level).get(t_node).get_dedicated_sun_node();
            if (s_sun_node != t_sun_node) //a inter solar system edge
            {//if
                high_level_sun_s = A_mult_ptr.get(act_level).get(s_sun_node).get_higher_level_node();
                high_level_sun_t = A_mult_ptr.get(act_level).get(t_sun_node).get_higher_level_node();

                //create new edge in *G_mult_ptr[act_level+1]
                e_new = G_mult_ptr.get(act_level + 1).newEdge(high_level_sun_s, high_level_sun_t);
                E_mult_ptr.get(act_level).get(e).set_higher_level_edge(e_new);
                inter_solar_system_edges.add(e);
            }//if
        }//forall

        //init new_edgelength calculate the values of new_edgelength and the lambda Lists

        new_edgelength.init(G_mult_ptr.get(act_level + 1), Factory.DOUBLE);
        for (edge e_ptr : inter_solar_system_edges)
        {//forall
            s_node = e_ptr.source();
            t_node = e_ptr.target();
            s_sun_node = A_mult_ptr.get(act_level).get(s_node).get_dedicated_sun_node();
            t_sun_node = A_mult_ptr.get(act_level).get(t_node).get_dedicated_sun_node();
            length_e = E_mult_ptr.get(act_level).get(e_ptr).get_length();
            length_s_edge = A_mult_ptr.get(act_level).get(s_node).get_dedicated_sun_distance();
            length_t_edge = A_mult_ptr.get(act_level).get(t_node).get_dedicated_sun_distance();
            newlength = length_s_edge + length_e + length_t_edge;

            //set new edge_length in *G_mult_ptr[act_level+1]
            e_new = E_mult_ptr.get(act_level).get(e_ptr).get_higher_level_edge();
            new_edgelength.set(e_new, newlength);

            //create entries in lambda Lists
            lambda_s = length_s_edge / newlength;
            lambda_t = length_t_edge / newlength;
            A_mult_ptr.get(act_level).get(s_node).get_lambda_List().add(lambda_s);
            A_mult_ptr.get(act_level).get(t_node).get_lambda_List().add(lambda_t);
            A_mult_ptr.get(act_level).get(s_node).get_neighbour_sun_node_List().add(
                    t_sun_node);
            A_mult_ptr.get(act_level).get(t_node).get_neighbour_sun_node_List().add(
                    s_sun_node);
        }//forall
    }

    void delete_parallel_edges_and_update_edgelength(
            List<Graph> G_mult_ptr,
            List<EdgeArray<EdgeAttributes>> E_mult_ptr,
            EdgeArray<Double> new_edgelength, int act_level)
    {
        edge e_act, e_save = null;
        Edge f_act = new Edge();
        List<Edge> sorted_edges = new ArrayList<Edge>();
        Graph Graph_ptr = G_mult_ptr.get(act_level + 1);
        int save_s_index = 0, save_t_index = 0, act_s_index, act_t_index;
        int counter = 1;

        //make *G_mult_ptr[act_level+1] undirected
        SimpleGraphAlg.makeSimpleUndirected(G_mult_ptr.get(act_level + 1));

        //sort the List sorted_edges
        for (Iterator<edge> i = Graph_ptr.edgesIterator(); i.hasNext();)
        {
            e_act = i.next();
            f_act.set_Edge(e_act, Graph_ptr);
            sorted_edges.add(f_act);
        }

        // FIXME: may not be correct
        Collections.sort(sorted_edges, new java.util.Comparator<Edge>()
        {
            @Override
            public int compare(Edge a, Edge b)
            {
                int a_index = a.get_edge().source().index() -
                        a.get_edge().target().index();
                int b_index = b.get_edge().source().index() -
                        b.get_edge().target().index();

                return b_index - a_index;
            }
        });

        //now parallel edges are consecutive in sorted_edges
        for (Edge EdgeIterator : sorted_edges)
        {//for
            e_act = EdgeIterator.get_edge();
            act_s_index = e_act.source().index();
            act_t_index = e_act.target().index();

            if (EdgeIterator != sorted_edges.get(0))
            {//if
                if ((act_s_index == save_s_index && act_t_index == save_t_index) ||
                        (act_s_index == save_t_index && act_t_index == save_s_index))
                {
                    new_edgelength.set(e_save, new_edgelength.get(e_save) + new_edgelength.get(e_act));
                    Graph_ptr.delEdge(e_act);
                    counter++;
                }
                else
                {
                    if (counter > 1)
                    {
                        new_edgelength.set(e_save, new_edgelength.get(e_save) / counter);
                        counter = 1;
                    }
                    save_s_index = act_s_index;
                    save_t_index = act_t_index;
                    e_save = e_act;
                }
            }//if
            else //first edge
            {
                save_s_index = act_s_index;
                save_t_index = act_t_index;
                e_save = e_act;
            }
        }//for

        //treat special case (last edges were multiple edges)
        if (counter > 1)
        {
            new_edgelength.set(e_save, new_edgelength.get(e_save) / counter);
        }

        //init *E_mult_ptr[act_level+1] and import EdgeAttributes
        E_mult_ptr.get(act_level + 1).init(G_mult_ptr.get(act_level + 1), Factory.EDGE_ATTRIBUTES);
        for (Iterator<edge> i = Graph_ptr.edgesIterator(); i.hasNext();)
        {
            e_act = i.next();
            E_mult_ptr.get(act_level + 1).get(e_act).set_length(new_edgelength.get(e_act));
        }
    }

    void find_initial_placement_for_level(
            int level,
            FMMMLayout.InitialPlacementMult init_placement_way,
            List<Graph> G_mult_ptr,
            List<NodeArray<NodeAttributes>> A_mult_ptr,
            List<EdgeArray<EdgeAttributes>> E_mult_ptr)
    {
        List<node> pm_nodes = new ArrayList<node>();
        set_initial_positions_of_sun_nodes(level, G_mult_ptr, A_mult_ptr);
        set_initial_positions_of_planet_and_moon_nodes(level, init_placement_way, G_mult_ptr,
                A_mult_ptr, E_mult_ptr, pm_nodes);
        set_initial_positions_of_pm_nodes(level, init_placement_way, A_mult_ptr,
                E_mult_ptr, pm_nodes);
    }

    void set_initial_positions_of_sun_nodes(
            int level,
            List<Graph> G_mult_ptr,
            List<NodeArray<NodeAttributes>> A_mult_ptr)
    {
        node v_high, v_act;
        DPoint new_pos;
        for (Iterator<node> i = G_mult_ptr.get(level + 1).nodesIterator(); i.hasNext();)
        {
            v_high = i.next();
            v_act = A_mult_ptr.get(level + 1).get(v_high).get_lower_level_node();
            new_pos = A_mult_ptr.get(level + 1).get(v_high).get_position();
            A_mult_ptr.get(level).get(v_act).set_position(new_pos);
            A_mult_ptr.get(level).get(v_act).place();
        }
    }

    void set_initial_positions_of_planet_and_moon_nodes(
            int level,
            FMMMLayout.InitialPlacementMult init_placement_way,
            List<Graph> G_mult_ptr,
            List<NodeArray<NodeAttributes>> A_mult_ptr,
            List<EdgeArray<EdgeAttributes>> E_mult_ptr,
            List<node> pm_nodes)
    {
        double lambda = 0.0, dedicated_sun_distance;
        int node_type;
        node v, v_adj, dedicated_sun;
        DPoint new_pos, dedicated_sun_pos, adj_sun_pos;
        List<DPoint> L = new ArrayList<DPoint>();
        ListIterator<Double> lambdaIterator;

        create_all_placement_sectors(G_mult_ptr, A_mult_ptr, E_mult_ptr, level);
        for (Iterator<node> i = G_mult_ptr.get(level).nodesIterator(); i.hasNext();)
        {//for
            v = i.next();
            node_type = A_mult_ptr.get(level).get(v).get_type();
            if (node_type == 3)
            {
                pm_nodes.add(v);
            }
            else if (node_type == 2 || node_type == 4) //a planet_node or moon_node
            {//else
                L.clear();
                dedicated_sun = A_mult_ptr.get(level).get(v).get_dedicated_sun_node();
                dedicated_sun_pos = PointFactory.INSTANCE.newDPoint(A_mult_ptr.get(level).get(dedicated_sun).get_position());
                dedicated_sun_distance = A_mult_ptr.get(level).get(v).get_dedicated_sun_distance();

                if (init_placement_way == FMMMLayout.InitialPlacementMult.ipmAdvanced)
                {
                    for (edge e : v.adjEdges())
                    {
                        if (e.source() != v)
                        {
                            v_adj = e.source();
                        }
                        else
                        {
                            v_adj = e.target();
                        }
                        if ((A_mult_ptr.get(level).get(v).get_dedicated_sun_node() ==
                                A_mult_ptr.get(level).get(v_adj).get_dedicated_sun_node()) &&
                                (A_mult_ptr.get(level).get(v_adj).get_type() != 1) &&
                                (A_mult_ptr.get(level).get(v_adj).is_placed()))
                        {
                            new_pos = calculate_position(dedicated_sun_pos,
                                    A_mult_ptr.get(level).get(v_adj).get_position(), dedicated_sun_distance,
                                    E_mult_ptr.get(level).get(e).get_length());
                            L.add(new_pos);
                        }
                    }
                }
                if (A_mult_ptr.get(level).get(v).get_lambda_List().isEmpty())
                {//special case
                    if (L.isEmpty())
                    {
                        new_pos = create_random_pos(dedicated_sun_pos, A_mult_ptr.get(level).get(v).get_dedicated_sun_distance());
                        L.add(new_pos);
                    }
                }//special case
                else
                {//usual case
                    lambdaIterator = A_mult_ptr.get(level).get(v).get_lambda_List().listIterator();

                    for (node adj_sun_ptr : A_mult_ptr.get(level).get(v).get_neighbour_sun_node_List())
                    {
                        if (lambdaIterator.hasNext())
                        {
                            lambda = lambdaIterator.next();
                        }
                        adj_sun_pos = A_mult_ptr.get(level).get(adj_sun_ptr).get_position();
                        new_pos = get_waggled_inbetween_position(dedicated_sun_pos, adj_sun_pos,
                                lambda);
                        L.add(new_pos);
                    }
                }//usual case

                A_mult_ptr.get(level).get(v).set_position(get_barycenter_position(L));
                A_mult_ptr.get(level).get(v).place();
            }//else
        }//for
    }

    void create_all_placement_sectors(
            List<Graph> G_mult_ptr,
            List<NodeArray<NodeAttributes>> A_mult_ptr,
            List<EdgeArray<EdgeAttributes>> E_mult_ptr,
            int level)
    {
        node v_high, w_high, sun_node, v, ded_sun;
        List<DPoint> adj_pos = new ArrayList<DPoint>();
        double angle_1 = 0.0, angle_2 = 0.0, act_angle_1, act_angle_2, next_angle, min_next_angle = 0.0;
        DPoint start_pos, end_pos;
        int MAX = 10; //the biggest of at most MAX random selected sectors is choosen
        int steps;
        ListIterator<DPoint> it;
        boolean first_angle;


        for (Iterator<node> i = G_mult_ptr.get(level + 1).nodesIterator(); i.hasNext();)
        {//forall
            v_high = i.next();
            //find pos of adjacent nodes
            adj_pos.clear();
            DPoint v_high_pos = PointFactory.INSTANCE.newDPoint(A_mult_ptr.get(level + 1).get(v_high).get_position());

            for (edge e_high : v_high.adjEdges())
            {
                if (!E_mult_ptr.get(level + 1).get(e_high).is_extra_edge())
                {
                    if (v_high == e_high.source())
                    {
                        w_high = e_high.target();
                    }
                    else
                    {
                        w_high = e_high.source();
                    }

                    DPoint w_high_pos = PointFactory.INSTANCE.newDPoint(A_mult_ptr.get(level + 1).get(w_high).get_position());
                    adj_pos.add(w_high_pos);
                }
            }
            if (adj_pos.isEmpty()) //easy case
            {
                angle_1 = 0;
                angle_2 = 6.2831853;
            }
            else if (adj_pos.size() == 1) //special case
            {
                //create angle_1
                start_pos = adj_pos.get(0);
                DPoint x_parallel_pos = PointFactory.INSTANCE.newDPoint(v_high_pos);
                x_parallel_pos.setX(v_high_pos.getX() + 1);
                angle_1 = x_parallel_pos.minus(v_high_pos).angle(start_pos.minus(v_high_pos));
                //create angle_2
                angle_2 = angle_1 + Math.PI;
            }
            else //usual case
            {//else
                steps = 1;
                it = adj_pos.listIterator();
                do
                {
                    int adj_pos_index = it.nextIndex();
                    //create act_angle_1
                    start_pos = it.next();
                    DPoint x_parallel_pos = PointFactory.INSTANCE.newDPoint(v_high_pos);
                    x_parallel_pos.setX(v_high_pos.getX() + 1);
                    act_angle_1 = x_parallel_pos.minus(v_high_pos).angle(start_pos.minus(v_high_pos));
                    //create act_angle_2
                    first_angle = true;

                    for (DPoint next_pos_ptr : adj_pos)
                    {
                        next_angle = start_pos.minus(v_high_pos).angle(next_pos_ptr.minus(v_high_pos));

                        if (!start_pos.equals(next_pos_ptr) && (first_angle || next_angle <
                                min_next_angle))
                        {
                            min_next_angle = next_angle;
                            first_angle = false;
                        }
                    }
                    act_angle_2 = act_angle_1 + min_next_angle;
                    if (adj_pos_index == 0 || ((act_angle_2 - act_angle_1) > (angle_2 - angle_1)))
                    {
                        angle_1 = act_angle_1;
                        angle_2 = act_angle_2;
                    }
                    steps++;
                } while ((steps <= MAX) && it.hasNext());

                if (angle_1 == angle_2)
                {
                    angle_2 = angle_1 + Math.PI;
                }
            }//else

            //import angle_1 and angle_2 to the dedicated suns at level level
            sun_node = A_mult_ptr.get(level + 1).get(v_high).get_lower_level_node();
            A_mult_ptr.get(level).get(sun_node).set_angle_1(angle_1);
            A_mult_ptr.get(level).get(sun_node).set_angle_2(angle_2);
        }//forall

        //import the angle values from the values of the dedicated sun nodes
        for (Iterator<node> i = G_mult_ptr.get(level).nodesIterator(); i.hasNext();)
        {
            v = i.next();
            ded_sun = A_mult_ptr.get(level).get(v).get_dedicated_sun_node();
            A_mult_ptr.get(level).get(v).set_angle_1(A_mult_ptr.get(level).get(ded_sun).get_angle_1());
            A_mult_ptr.get(level).get(v).set_angle_2(A_mult_ptr.get(level).get(ded_sun).get_angle_2());
        }
    }

    void set_initial_positions_of_pm_nodes(
            int level,
            FMMMLayout.InitialPlacementMult init_placement_way,
            List<NodeArray<NodeAttributes>> A_mult_ptr,
            List<EdgeArray<EdgeAttributes>> E_mult_ptr,
            List<node> pm_nodes)
    {
        double moon_dist, sun_dist, lambda = 0.0;
        node v_adj, sun_node;
        DPoint sun_pos, moon_pos, new_pos, adj_sun_pos;
        List<DPoint> L = new ArrayList<DPoint>();
        ListIterator<Double> lambdaIterator;

        for (node v_ptr : pm_nodes)
        {//forall
            L.clear();
            sun_node = A_mult_ptr.get(level).get(v_ptr).get_dedicated_sun_node();
            sun_pos = A_mult_ptr.get(level).get(sun_node).get_position();
            sun_dist = A_mult_ptr.get(level).get(v_ptr).get_dedicated_sun_distance();

            if (init_placement_way == FMMMLayout.InitialPlacementMult.ipmAdvanced)
            {//if
                for (edge e : v_ptr.adjEdges())
                {
                    if (e.source() != v_ptr)
                    {
                        v_adj = e.source();
                    }
                    else
                    {
                        v_adj = e.target();
                    }
                    if ((!E_mult_ptr.get(level).get(e).is_moon_edge()) &&
                            (A_mult_ptr.get(level).get(v_ptr).get_dedicated_sun_node() ==
                            A_mult_ptr.get(level).get(v_adj).get_dedicated_sun_node()) &&
                            (A_mult_ptr.get(level).get(v_adj).get_type() != 1) &&
                            (A_mult_ptr.get(level).get(v_adj).is_placed()))
                    {
                        new_pos = calculate_position(sun_pos, A_mult_ptr.get(level).get(v_adj).
                                get_position(), sun_dist, E_mult_ptr.get(level).get(e).get_length());
                        L.add(new_pos);
                    }
                }
            }//if
            for (node moon_node_ptr : A_mult_ptr.get(level).get(v_ptr).get_dedicated_moon_node_List())
            {
                moon_pos = A_mult_ptr.get(level).get(moon_node_ptr).get_position();
                moon_dist = A_mult_ptr.get(level).get(moon_node_ptr).get_dedicated_sun_distance();
                lambda = sun_dist / moon_dist;
                new_pos = get_waggled_inbetween_position(sun_pos, moon_pos, lambda);
                L.add(new_pos);
            }

            if (!A_mult_ptr.get(level).get(v_ptr).get_lambda_List().isEmpty())
            {
                lambdaIterator = A_mult_ptr.get(level).get(v_ptr).get_lambda_List().listIterator();

                for (node adj_sun_ptr : A_mult_ptr.get(level).get(v_ptr).get_neighbour_sun_node_List())
                {
                    if (lambdaIterator.hasNext())
                    {
                        lambda = lambdaIterator.next();
                    }
                    adj_sun_pos = A_mult_ptr.get(level).get(adj_sun_ptr).get_position();
                    new_pos = get_waggled_inbetween_position(sun_pos, adj_sun_pos, lambda);
                    L.add(new_pos);
                }
            }

            A_mult_ptr.get(level).get(v_ptr).set_position(get_barycenter_position(L));
            A_mult_ptr.get(level).get(v_ptr).place();
        }//forall
    }

    DPoint create_random_pos(DPoint center, double radius)
    {
        DPoint new_point = PointFactory.INSTANCE.newDPoint();

        double r;
        r = ((random.nextDouble() * 2.0) - 1.0) * radius;
        new_point.setX(center.getX() + r);
        r = ((random.nextDouble() * 2.0) - 1.0) * radius;
        new_point.setY(center.getY() + r);
        r = ((random.nextDouble() * 2.0) - 1.0) * radius;
        new_point.setZ(center.getZ() + r);

        // The point is selected within an AABB rather than a
        // sphere, but it doesn't seem to bother anything
        // (A normalise and scale by radius would fix it)
        return new_point;
    }

    DPoint get_waggled_inbetween_position(DPoint s, DPoint t, double lambda)
    {
        double WAGGLEFACTOR = 0.05;
        DPoint inbetween_point = PointFactory.INSTANCE.newDPoint();
        inbetween_point = s.plus(t.minus(s).scaled(lambda));
        double radius = WAGGLEFACTOR * (t.minus(s)).norm();
        double rnd = random.nextDouble();//rand number in (0,1)
        double rand_radius = radius * rnd;
        return create_random_pos(inbetween_point, rand_radius);
    }

    DPoint get_barycenter_position(List<DPoint> L)
    {
        DPoint sum = PointFactory.INSTANCE.newDPoint();

        for (DPoint act_point_ptr : L)
        {
            sum = sum.plus(act_point_ptr);
        }
        return sum.scaled(1.0 / L.size());
    }

    DPoint calculate_position(DPoint P, DPoint Q, double dist_P, double dist_Q)
    {
        double dist_PQ = (P.minus(Q)).norm();
        double lambda = (dist_P + (dist_PQ - dist_P - dist_Q) / 2) / dist_PQ;
        return get_waggled_inbetween_position(P, Q, lambda);
    }
}
