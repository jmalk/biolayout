package ogdf.energybased;

/*
 * $Revision: 2552 $
 *
 * last checkin:
 *   $Author: gutwenger $
 *   $Date: 2012-07-05 16:45:20 +0200 (Do, 05. Jul 2012) $
 ***************************************************************/
/**
 * \file \brief Implementation of class FruchtermanReingold (computation of forces).
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
import java.util.concurrent.*;
import ogdf.basic.*;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

class FruchtermanReingold
{
    final int MULTITHREAD_NODE_COUNT_THRESHOLD = 256;
    final int NUMBER_OF_THREADS = RUNTIME.availableProcessors();
    final boolean PERFORMANCE_METRICS = false;
    ExecutorService executor;

    //Import updated information of the drawing area.
    public void update_boxlength_and_cornercoordinate(double b_l, DPoint d_l_c)
    {
        boxlength = b_l;
        down_left_corner = PointFactory.INSTANCE.newDPoint(d_l_c);
    }
    private int _grid_quotient;//for coarsening the FrRe-grid
    private int max_gridindex; //maximum index of a grid row/column
    private double boxlength;  //length of drawing box
    private DPoint down_left_corner;//down left corner of drawing box

    //The number k of rows and colums of the grid is sqrt(|V|) / frGridQuotient()
    //(Note that in [FrRe] frGridQuotient() is 2.)
    private void grid_quotient(int p)
    {
        _grid_quotient = ((0 <= p) ? p : 2);
    }

    private int grid_quotient()
    {
        return _grid_quotient;
    }

    public FruchtermanReingold()
    {
        grid_quotient(2);
        executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    }

    public void shutdown()
    {
        executor.shutdown();
    }

    private void calculate_repulsive_force_on_node(node n, List<node> others,
            NodeArray<NodeAttributes> A, NodeArray<DPoint> F_rep)
    {
        DPoint f_rep_u_on_v = PointFactory.INSTANCE.newDPoint();

        for (node v : others)
        {
            DPoint pos_u = A.get(n).get_position();
            DPoint pos_v = A.get(v).get_position();
            if (pos_u.equals(pos_v))
            {//if2  (Exception handling if two nodes have the same position)
                pos_u = numexcept.choose_distinct_random_point_in_radius_epsilon(pos_u);
            }//if2
            DPoint vector_v_minus_u = pos_v.minus(pos_u);
            double norm_v_minus_u = vector_v_minus_u.norm();
            if (!numexcept.f_rep_near_machine_precision(norm_v_minus_u, f_rep_u_on_v))
            {
                double scalar = f_rep_scalar(norm_v_minus_u) / norm_v_minus_u;
                f_rep_u_on_v = vector_v_minus_u.scaled(scalar);
            }
            F_rep.set(v, F_rep.get(v).plus(f_rep_u_on_v));
            F_rep.set(n, F_rep.get(n).minus(f_rep_u_on_v));
        }
    }

    public void calculate_exact_repulsive_forces_multithreaded(
            final Graph G,
            final List<node> nodes,
            final List<node> others,
            final NodeArray<NodeAttributes> A,
            NodeArray<DPoint> F_rep)
    {
        ArrayList<FutureTask<NodeArray<DPoint>>> futures = new ArrayList<FutureTask<NodeArray<DPoint>>>();

        int total_interactions = ((nodes.size() - 1) * nodes.size()) / 2;
        int interactions_per_thread = (int) Math.ceil((double) total_interactions / NUMBER_OF_THREADS);
        int last_node_index = 0;

        for (int thread = 0; thread < NUMBER_OF_THREADS; thread++)
        {
            final int first_node_index = last_node_index;

            int interactions = 0;
            while (interactions < interactions_per_thread)
            {
                interactions += (nodes.size() - (last_node_index + 1));
                last_node_index++;
            }

            final int number_of_nodes;

            if ((last_node_index - first_node_index) > nodes.size())
            {
                number_of_nodes = nodes.size() - first_node_index;
            }
            else
            {
                number_of_nodes = last_node_index - first_node_index;
            }

            futures.add(thread, new FutureTask<NodeArray<DPoint>>(
                    new Callable<NodeArray<DPoint>>()
                    {
                        @Override
                        public NodeArray<DPoint> call()
                        {
                            NodeArray<DPoint> F_rep_thread = new NodeArray<DPoint>(G, Factory.DPOINT);

                            for (int i = first_node_index; i < first_node_index + number_of_nodes; i++)
                            {
                                calculate_repulsive_force_on_node(
                                        nodes.get(i),
                                        others != null ? others : nodes.subList(i + 1, nodes.size()),
                                        A, F_rep_thread);
                            }

                            return F_rep_thread;
                        }
                    }));
            executor.submit(futures.get(thread));
        }

        // Recombine results from threads
        try
        {
            for (node v : nodes)
            {
                for (FutureTask<NodeArray<DPoint>> future : futures)
                {
                    F_rep.set(v, F_rep.get(v).plus(future.get().get(v)));
                }
            }
        }
        catch (InterruptedException e)
        {
            if (DEBUG_BUILD)
            {
                println("calculate_exact_repulsive_forces_multithreaded, InterruptedException " + e.getMessage());
            }
        }
        catch (ExecutionException e)
        {
            if (DEBUG_BUILD)
            {
                println("calculate_exact_repulsive_forces_multithreaded, ExecutionException " + e.getMessage());
            }
        }
    }

    public void calculate_exact_repulsive_forces(
            Graph G,
            NodeArray<NodeAttributes> A,
            NodeArray<DPoint> F_rep)
    {
        List<node> array_of_the_nodes = new ArrayList<node>();

        for (Iterator<node> iter = G.nodesIterator(); iter.hasNext();)
        {
            node v = iter.next();
            F_rep.set(v, PointFactory.INSTANCE.newDPoint());
            array_of_the_nodes.add(v);
        }

        int number_of_nodes = array_of_the_nodes.size();
        long startTime = System.nanoTime();

        if (number_of_nodes < MULTITHREAD_NODE_COUNT_THRESHOLD || NUMBER_OF_THREADS == 1)
        {
            for (int i = 0; i < number_of_nodes; i++)
            {
                calculate_repulsive_force_on_node(
                        array_of_the_nodes.get(i),
                        array_of_the_nodes.subList(i + 1, number_of_nodes),
                        A, F_rep);
            }
        }
        else
        {
            calculate_exact_repulsive_forces_multithreaded(G, array_of_the_nodes, null, A, F_rep);
        }

        if (DEBUG_BUILD && PERFORMANCE_METRICS)
        {
            println("calculate_exact_repulsive_forces " + number_of_nodes + " nodes, " +
                    ((double)(System.nanoTime() - startTime) / 1000000000.0) + " seconds");
        }
    }

    public void calculate_approx_repulsive_forces_for_cell(
            NodeArray<NodeAttributes> A,
            NodeArray<DPoint> F_rep,
            List<node>[][][] contained_nodes, int i, int j, int k)
    {
        int length = contained_nodes[i][j][k].size();
        if (contained_nodes[i][j][k] == null || length == 0)
        {
            // Should never happen
            return;
        }

        int i_num_grid_cells = contained_nodes.length;
        int j_num_grid_cells = contained_nodes[0].length;
        int k_num_grid_cells = contained_nodes[0][0].length;

        //step1: calculate forces inside contained_nodes(i,j,k)
        List<node> nodearray_i_j_k = new ArrayList<node>();
        for (node n : contained_nodes[i][j][k])
        {
            nodearray_i_j_k.add(n);
        }

        for (int uIndex = 0; uIndex < length; uIndex++)
        {
            calculate_repulsive_force_on_node(
                    nodearray_i_j_k.get(uIndex),
                    nodearray_i_j_k.subList(uIndex + 1, length),
                    A, F_rep);
        }

        //step 2: calculated forces to nodes in neighbour boxes

        //find_neighbour_boxes

        List<IPoint> neighbour_boxes = new ArrayList<IPoint>();
        for (int i_n = i - 1; i_n <= i + 1; i_n++)
        {
            for (int j_n = j - 1; j_n <= j + 1; j_n++)
            {
                for (int k_n = k - 1; k_n <= k + 1; k_n++)
                {
                    if ((i_n >= 0) && (j_n >= 0) && (k_n >= 0) &&
                            (i_n < i_num_grid_cells) && (j_n < j_num_grid_cells) && (k_n < k_num_grid_cells))
                    {
                        IPoint neighbour = PointFactory.INSTANCE.newIPoint();
                        neighbour.setX(i_n);
                        neighbour.setY(j_n);
                        neighbour.setZ(k_n);

                        if ((i_n != i) || (j_n != j) || (k_n != k))
                        {
                            neighbour_boxes.add(neighbour);
                        }
                    }
                }
            }
        }


        //forget neighbour_boxes that already had access to this box
        for (IPoint act_neighbour_box_it : neighbour_boxes)
        {//forall
            int act_i = act_neighbour_box_it.getX();
            int act_j = act_neighbour_box_it.getY();
            int act_k = act_neighbour_box_it.getZ();

            boolean top = (act_k == k - 1 && !(act_i == i - 1 && act_j == j - 1));
            boolean middle = (act_k == k && (act_j == j + 1 || (act_j == j && act_i == i + 1)));
            boolean bottom = (act_k == k + 1 && (act_i == i + 1 && act_j == j + 1));

            if ((top || middle || bottom) &&
                    (contained_nodes[act_i][act_j][act_k] != null &&
                    contained_nodes[act_i][act_j][act_k].size() > 0))
            {//if1
                for (node v_it : contained_nodes[i][j][k])
                {
                    calculate_repulsive_force_on_node(
                            v_it,
                            contained_nodes[act_i][act_j][act_k],
                            A, F_rep);
                }
            }//if1
        }//forall
    }

    public void calculate_approx_repulsive_forces(
            final Graph G,
            final NodeArray<NodeAttributes> A,
            NodeArray<DPoint> F_rep)
    {
        //GRID algorithm by Fruchterman & Reingold

        int i, j, k;

        //init F_rep
        for (Iterator<node> iter = G.nodesIterator(); iter.hasNext();)
        {
            node v = iter.next();
            F_rep.set(v, PointFactory.INSTANCE.newDPoint());
        }

        //init max_gridindex and set contained_nodes;

        max_gridindex = (int) (Math.sqrt((double) (G.numberOfNodes())) / grid_quotient());
        max_gridindex = ((max_gridindex > 0) ? max_gridindex : 1);
        int i_num_grid_cells = max_gridindex;
        int j_num_grid_cells = max_gridindex;
        int k_num_grid_cells = PointFactory.INSTANCE.dimensions() == PointFactory.Dimensions._2 ? 1 : max_gridindex;

        @SuppressWarnings("unchecked")
        final List<node>[][][] contained_nodes = new ArrayList[i_num_grid_cells][j_num_grid_cells][k_num_grid_cells];
        List<Integer[]> non_empty_cell_list = new ArrayList<Integer[]>();

        double gridboxlength = boxlength / max_gridindex;
        for (Iterator<node> iter = G.nodesIterator(); iter.hasNext();)
        {
            node v = iter.next();
            DPoint offset = A.get(v).get_position().minus(down_left_corner);
            int x_index = (int) (offset.getX() / gridboxlength);
            int y_index = (int) (offset.getY() / gridboxlength);
            int z_index = (int) (offset.getZ() / gridboxlength);

            if (contained_nodes[x_index][y_index][z_index] == null)
            {
                contained_nodes[x_index][y_index][z_index] = new ArrayList<node>();
                non_empty_cell_list.add(new Integer[] {x_index, y_index, z_index});
            }
            contained_nodes[x_index][y_index][z_index].add(v);
        }

        @SuppressWarnings("unchecked")
        final List<Integer[]>[] per_thread_cell_list = new ArrayList[NUMBER_OF_THREADS];
        int cells_per_thread = (int) Math.ceil((double) non_empty_cell_list.size() / NUMBER_OF_THREADS);
        int thread = 0;

        // Distribute cells over threads
        for (Integer[] cell : non_empty_cell_list)
        {
            if (per_thread_cell_list[thread] == null)
            {
                per_thread_cell_list[thread] = new ArrayList<Integer[]>();
            }

            per_thread_cell_list[thread].add(new Integer[]
                    {
                        cell[0], cell[1], cell[2]
                    });

            if (per_thread_cell_list[thread].size() >= cells_per_thread)
            {
                thread++;
            }
        }

        long startTime = System.nanoTime();

        ArrayList<FutureTask<NodeArray<DPoint>>> futures = new ArrayList<FutureTask<NodeArray<DPoint>>>();

        //force calculation
        for (thread = 0; thread < NUMBER_OF_THREADS; thread++)
        {
            final List<Integer[]> cell_list = per_thread_cell_list[thread];

            if (cell_list == null)
            {
                continue;
            }

            futures.add(thread, new FutureTask<NodeArray<DPoint>>(
                    new Callable<NodeArray<DPoint>>()
                    {
                        @Override
                        public NodeArray<DPoint> call()
                        {
                            NodeArray<DPoint> F_rep_thread = new NodeArray<DPoint>(G, Factory.DPOINT);

                            for (Integer[] cell : cell_list)
                            {
                                calculate_approx_repulsive_forces_for_cell(A, F_rep_thread,
                                        contained_nodes, cell[0], cell[1], cell[2]);
                            }

                            return F_rep_thread;
                        }
                    }));
            executor.submit(futures.get(thread));
        }

        // Recombine results from threads
        try
        {
            for (Iterator<node> iter = G.nodesIterator(); iter.hasNext();)
            {
                node v = iter.next();
                for (FutureTask<NodeArray<DPoint>> future : futures)
                {
                    F_rep.set(v, F_rep.get(v).plus(future.get().get(v)));
                }
            }

            if (DEBUG_BUILD && PERFORMANCE_METRICS)
            {
                println("calculate_approx_repulsive_forces " + non_empty_cell_list.size() + " cells, " +
                        ((double)(System.nanoTime() - startTime) / 1000000000.0) + " seconds");
            }
        }
        catch (InterruptedException e)
        {
            if (DEBUG_BUILD)
            {
                println("calculate_approx_repulsive_forces, InterruptedException " + e.getMessage());
            }
        }
        catch (ExecutionException e)
        {
            if (DEBUG_BUILD)
            {
                println("calculate_approx_repulsive_forces, ExecutionException " + e.getMessage());
            }
        }
    }

    public void make_initialisations(double bl, DPoint d_l_c, int grid_quot)
    {
        grid_quotient(grid_quot);
        down_left_corner = PointFactory.INSTANCE.newDPoint(d_l_c); //export this two values from FMMM
        boxlength = bl;
    }

    public double f_rep_scalar(double d)
    {
        if (d > 0.0)
        {
            return 1.0 / d;
        }
        else
        {
            if (DEBUG_BUILD)
            {
                println("Error  f_rep_scalar nodes at same position");
            }
            return 0.0;
        }
    }
}
