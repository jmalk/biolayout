package ogdf.energybased;

/*
 * $Revision: 2552 $
 *
 * last checkin:
 *   $Author: gutwenger $
 *   $Date: 2012-07-05 16:45:20 +0200 (Do, 05. Jul 2012) $
 ***************************************************************/

/** \file
 * \brief Implementation of class NMM (New Multipole Method).
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

/*import java.util.*;
import ogdf.basic.*;
import org.BioLayoutExpress3D.Utils.Complex;
import org.BioLayoutExpress3D.Utils.ref;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

public class NMM
{
    final double MIN_BOX_LENGTH = 1e-300;
    int MIN_NODE_NUMBER; //The minimum number of nodes for which the forces are
    //calculated using NMM (for lower values the exact
    //calculation is used).
    boolean using_NMM; //Indicates whether the exact method or NMM is used for force
    //calculation (value depends on MIN_NODE_NUMBER)
    FruchtermanReingold ExactMethod; //needed in case that using_NMM == false
    FMMMLayout.ReducedTreeConstruction _tree_construction_way;//1 = pathwise;2 = subtreewise
    FMMMLayout.SmallestCellFinding _find_small_cell;//0 = iterative; 1= Aluru
    int _particles_in_leaves;//max. number of particles for leaves of the quadtree
    int _precision;  //precision for p-term multipole expansion
    double boxlength;//length of drawing box
    DPoint2 down_left_corner;//down left corner of drawing box
    int[] power_of_2; //holds the powers of 2 (for speed reasons to calculate the
    //maximal boxindex (index is from 0 to max_power_of_2_index)
    int max_power_of_2_index;//holds max. index for power_of_2 (= 30)
    double[][] BK; //holds the binomial coefficients
    List<DPoint2> rep_forces;	//stores the rep. forces of the last iteration
    //(needed for error calculation)
    Random random;


    public NMM(Random random)
    {
        this.random = random;

        //set MIN_NODE_NUMBER and using_NMM
        MIN_NODE_NUMBER = 175;
        using_NMM = true;
        ExactMethod = new FruchtermanReingold();

        //setting predefined parameters
        precision(4);
        particles_in_leaves(25);

        tree_construction_way(FMMMLayout.ReducedTreeConstruction.rtcSubtreeBySubtree);
        find_sm_cell(FMMMLayout.SmallestCellFinding.scfIteratively);
    }

    void calculate_repulsive_forces(
            Graph G,
            NodeArray<NodeAttributes> A,
            NodeArray<DPoint2> F_rep)
    {
        if (using_NMM) //use NewMultipoleMethod
        {
            calculate_repulsive_forces_by_NMM(G, A, F_rep);
        }
        else //used the exact naive way
        {
            calculate_repulsive_forces_by_exact_method(G, A, F_rep);
        }
    }

    public void calculate_repulsive_forces_by_NMM(
            Graph G,
            NodeArray<NodeAttributes> A,
            NodeArray<DPoint2> F_rep)
    {
        QuadTreeNM T = new QuadTreeNM();
        node v;
        NodeArray<DPoint2> F_direct = new NodeArray<DPoint2>(G, Factory.DPOINT);
        NodeArray<DPoint2> F_local_exp = new NodeArray<DPoint2>(G, Factory.DPOINT);
        NodeArray<DPoint2> F_multipole_exp = new NodeArray<DPoint2>(G, Factory.DPOINT);
        List<QuadTreeNodeNM> quad_tree_leaves;

        //initializations

        for (Iterator<node> i = G.nodesIterator(); i.hasNext();)
        {
            v = i.next();
            F_direct.set(v, new DPoint2());
            F_local_exp.set(v, new DPoint2());
            F_multipole_exp.set(v, new DPoint2());
        }

        quad_tree_leaves = new ArrayList<QuadTreeNodeNM>();
        if (tree_construction_way() == FMMMLayout.ReducedTreeConstruction.rtcPathByPath)
        {
            build_up_red_quad_tree_path_by_path(G, A, T);
        }
        else //tree_construction_way == FMMMLayout.rtcSubtreeBySubtree
        {
            build_up_red_quad_tree_subtree_by_subtree(G, A, T);
        }

        form_multipole_expansions(A, T, quad_tree_leaves);
        calculate_local_expansions_and_WSPRLS(A, T.get_root_ptr());
        transform_local_exp_to_forces(A, quad_tree_leaves, F_local_exp);
        transform_multipole_exp_to_forces(A, quad_tree_leaves, F_multipole_exp);
        calculate_neighbourcell_forces(A, quad_tree_leaves, F_direct);
        add_rep_forces(G, F_direct, F_multipole_exp, F_local_exp, F_rep);

        delete_red_quad_tree_and_count_treenodes(T);
    }

    void calculate_repulsive_forces_by_exact_method(
            Graph G,
            NodeArray<NodeAttributes> A,
            NodeArray<DPoint2> F_rep)
    {
        ExactMethod.calculate_exact_repulsive_forces(G, A, F_rep);
    }

    void make_initialisations(
            Graph G,
            double bl,
            DPoint2 d_l_c,
            int p_i_l,
            int p,
            FMMMLayout.ReducedTreeConstruction t_c_w,
            FMMMLayout.SmallestCellFinding f_s_c)
    {
        if (G.numberOfNodes() >= MIN_NODE_NUMBER) //using_NMM
        {
            using_NMM = true; //indicate that NMM is used for force calculation

            particles_in_leaves(p_i_l);
            precision(p);
            tree_construction_way(t_c_w);
            find_sm_cell(f_s_c);
            down_left_corner = new DPoint2(d_l_c); //Export this two values from FMMM
            boxlength = bl;
            init_binko(2 * precision());
            init_power_of_2_array();
        }
        else //use exact method
        {
            using_NMM = false; //indicate that exact method is used for force calculation
            ExactMethod.make_initialisations(bl, d_l_c, 0);
        }
    }

    void update_boxlength_and_cornercoordinate(double b_l, DPoint2 d_l_c)
    {
        if (using_NMM)
        {
            boxlength = b_l;
            down_left_corner = new DPoint2(d_l_c);
        }
        else
        {
            ExactMethod.update_boxlength_and_cornercoordinate(b_l, d_l_c);
        }
    }

    void init_power_of_2_array()
    {
        int p = 1;
        max_power_of_2_index = 30;
        power_of_2 = new int[max_power_of_2_index + 1];
        for (int i = 0; i <= max_power_of_2_index; i++)
        {
            power_of_2[i] = p;
            p *= 2;
        }
    }

    int power_of_two(int i)
    {
        if (i <= max_power_of_2_index)
        {
            return power_of_2[i];
        }
        else
        {
            return (int)(Math.pow(2.0, i));
        }
    }

    int maxboxindex(int level)
    {
        if ((level < 0))
        {
            if (DEBUG_BUILD)
            {
                println("Failure maxboxindex :wrong level ");
                println("level " + level);
            }
            return -1;

        }
        else
        {
            return power_of_two(level) - 1;
        }
    }


// ************Functions needed for path by path based  treeruction**********
    void build_up_red_quad_tree_path_by_path(
            Graph G,
            NodeArray<NodeAttributes> A,
            QuadTreeNM T)
    {
        List<QuadTreeNodeNM> act_leaf_List, new_leaf_List;
        List<QuadTreeNodeNM> act_leaf_List_ptr, new_leaf_List_ptr, help_ptr;
        List<ParticleInfo> act_x_List_copy = new ArrayList<ParticleInfo>();
        List<ParticleInfo> act_y_List_copy = new ArrayList<ParticleInfo>();
        QuadTreeNodeNM act_node_ptr;

        build_up_root_node(G, A, T);

        act_leaf_List = new ArrayList<QuadTreeNodeNM>();
        new_leaf_List = new ArrayList<QuadTreeNodeNM>();
        act_leaf_List.add(T.get_root_ptr());
        act_leaf_List_ptr = act_leaf_List;
        new_leaf_List_ptr = new_leaf_List;

        while (!act_leaf_List_ptr.isEmpty())
        {
            while (!act_leaf_List_ptr.isEmpty())
            {
                act_node_ptr = act_leaf_List_ptr.remove(0);
                make_copy_and_init_Lists(act_node_ptr.get_x_List_ptr(), act_x_List_copy,
                        act_node_ptr.get_y_List_ptr(), act_y_List_copy);
                T.set_act_ptr(act_node_ptr);
                decompose_subtreenode(T, act_x_List_copy, act_y_List_copy, new_leaf_List_ptr);
            }
            help_ptr = act_leaf_List_ptr;
            act_leaf_List_ptr = new_leaf_List_ptr;
            new_leaf_List_ptr = help_ptr;
        }
    }

    void make_copy_and_init_Lists(
            List<ParticleInfo> L_x_orig,
            List<ParticleInfo> L_x_copy,
            List<ParticleInfo> L_y_orig,
            List<ParticleInfo> L_y_copy)
    {
        ListIterator<ParticleInfo> origin_x_item, copy_x_item, origin_y_item, copy_y_item;
        ParticleInfo P_x_orig, P_y_orig, P_x_copy, P_y_copy;
        boolean L_y_orig_traversed = false;

        L_x_copy.clear();
        L_y_copy.clear();

        origin_x_item = L_x_orig.listIterator();
        while (origin_x_item.hasNext())
        {
            //reset values
            P_x_orig = origin_x_item.next();
            P_x_orig.set_subList_ptr(null); //clear subList_ptr
            P_x_orig.set_copy_item(null);   //clear copy_item
            P_x_orig.unmark(); //unmark this element
            P_x_orig.set_tmp_cross_ref_item(null);//clear tmp_cross_ref_item

            //update L_x_copy
            P_x_copy = new ParticleInfo(P_x_orig);
            L_x_copy.add(P_x_copy);

            //update L_x_orig
            P_x_orig.set_copy_item(P_x_copy);
        }

        origin_y_item = L_y_orig.listIterator();
        while (origin_y_item.hasNext())
        {
            //reset values
            P_y_orig = origin_y_item.next();
            P_y_orig.set_subList_ptr(null); //clear subList_ptr
            P_y_orig.set_copy_item(null);   //clear copy_item
            P_y_orig.set_tmp_cross_ref_item(null);//clear tmp_cross_ref_item
            P_y_orig.unmark(); //unmark this element

            //update L_x(y)_copy
            P_y_copy = new ParticleInfo(P_y_orig);
            ParticleInfo new_cross_ref_item = P_y_orig.get_cross_ref_item().get_copy_item();
            P_y_copy.set_cross_ref_item(new_cross_ref_item);
            L_y_copy.add(P_y_copy);
            new_cross_ref_item.set_cross_ref_item(P_y_copy);
            P_x_copy = new ParticleInfo(new_cross_ref_item);

            //update L_y_orig
            P_y_orig.set_copy_item(P_y_copy);
        }
    }

    void build_up_root_node(
            Graph G,
            NodeArray<NodeAttributes> A,
            QuadTreeNM T)
    {
        T.init_tree();
        T.get_root_ptr().set_Sm_level(0);
        T.get_root_ptr().set_Sm_downleftcorner(down_left_corner);
        T.get_root_ptr().set_Sm_boxlength(boxlength);
        //allocate space for L_x and L_y List of the root node
        T.get_root_ptr().set_x_List_ptr(new ArrayList<ParticleInfo>());
        T.get_root_ptr().set_y_List_ptr(new ArrayList<ParticleInfo>());
        create_sorted_coordinate_Lists(G, A, T.get_root_ptr().get_x_List_ptr(), T.get_root_ptr().get_y_List_ptr());
    }

    void create_sorted_coordinate_Lists(
            Graph G,
            NodeArray<NodeAttributes> A,
            List<ParticleInfo> L_x,
            List<ParticleInfo> L_y)
    {
        ParticleInfo P_x, P_y;
        node v;

        //build up L_x,L_y and link the Lists
        for (Iterator<node> i = G.nodesIterator(); i.hasNext();)
        {
            v = i.next();

            P_x = new ParticleInfo();
            P_y = new ParticleInfo();
            P_x.set_x_y_coord(A.get(v).get_x());
            P_y.set_x_y_coord(A.get(v).get_y());
            P_x.set_vertex(v);
            P_y.set_vertex(v);
            L_x.add(P_x);
            L_y.add(P_y);
            P_x.set_cross_ref_item(P_y);
            P_y.set_cross_ref_item(P_x);
        }

        //sort L_x and update the links of L_y
        Collections.sort(L_x, ParticleInfo.comparator());

        for (ParticleInfo x_item : L_x)
        {
            ParticleInfo y_item = x_item.get_cross_ref_item();
            y_item.set_cross_ref_item(x_item);
        }

        //sort L_y and update the links of L_x
        Collections.sort(L_y, ParticleInfo.comparator());

        for (ParticleInfo y_item : L_y)
        {
            ParticleInfo x_item = y_item.get_cross_ref_item();
            x_item.set_cross_ref_item(y_item);
        }
    }


    void decompose_subtreenode(
            QuadTreeNM T,
            List<ParticleInfo> act_x_List_copy,
            List<ParticleInfo> act_y_List_copy,
            List<QuadTreeNodeNM> new_leaf_List)
    {
        QuadTreeNodeNM act_ptr = T.get_act_ptr();
        int act_particle_number = act_ptr.get_x_List_ptr().size();
        double x_min, x_max, y_min, y_max;
        ref<List<ParticleInfo>> L_x_l_ptr = new ref<List<ParticleInfo>>(null);
        ref<List<ParticleInfo>> L_x_r_ptr = new ref<List<ParticleInfo>>(null);
        ref<List<ParticleInfo>> L_x_lb_ptr = new ref<List<ParticleInfo>>(null);
        ref<List<ParticleInfo>> L_x_rb_ptr = new ref<List<ParticleInfo>>(null);
        ref<List<ParticleInfo>> L_x_lt_ptr = new ref<List<ParticleInfo>>(null);
        ref<List<ParticleInfo>> L_x_rt_ptr = new ref<List<ParticleInfo>>(null);
        ref<List<ParticleInfo>> L_y_l_ptr = new ref<List<ParticleInfo>>(null);
        ref<List<ParticleInfo>> L_y_r_ptr = new ref<List<ParticleInfo>>(null);
        ref<List<ParticleInfo>> L_y_lb_ptr = new ref<List<ParticleInfo>>(null);
        ref<List<ParticleInfo>> L_y_rb_ptr = new ref<List<ParticleInfo>>(null);
        ref<List<ParticleInfo>> L_y_lt_ptr = new ref<List<ParticleInfo>>(null);
        ref<List<ParticleInfo>> L_y_rt_ptr = new ref<List<ParticleInfo>>(null);

        ref<Double> x_min_ref = new ref<Double>();
        ref<Double> x_max_ref = new ref<Double>();
        ref<Double> y_min_ref = new ref<Double>();
        ref<Double> y_max_ref = new ref<Double>();
        calculate_boundaries_of_act_node(T.get_act_ptr(), x_min_ref, x_max_ref, y_min_ref, y_max_ref);
        x_min = x_min_ref.get();
        x_max = x_max_ref.get();
        y_min = y_min_ref.get();
        y_max = y_max_ref.get();

        if (find_sm_cell() == FMMMLayout.SmallestCellFinding.scfIteratively)
        {
            find_small_cell_iteratively(T.get_act_ptr(), x_min, x_max, y_min, y_max);
        }
        else //find_small_cell == FMMMLayout.scfAluru
        {
            find_small_cell_iteratively(T.get_act_ptr(), x_min, x_max, y_min, y_max);
        }

        if ((act_particle_number > particles_in_leaves()) &&
                ((x_max - x_min >= MIN_BOX_LENGTH) || (y_max - y_min >= MIN_BOX_LENGTH)))
        {//if0

            //recursive calls for the half of the quad that contains the most particles

            split_in_x_direction(act_ptr, L_x_l_ptr, L_y_l_ptr,
                    L_x_r_ptr, L_y_r_ptr);

            if (L_x_r_ptr.get() == null ||
                    (L_x_l_ptr.get() != null && L_x_l_ptr.get().size() > L_x_r_ptr.get().size()))
            {//if1 left half contains more particles
                split_in_y_direction(act_ptr, L_x_lb_ptr,
                        L_y_lb_ptr, L_x_lt_ptr, L_y_lt_ptr);
                if ((L_x_lt_ptr.get() == null) ||
                        (L_x_lb_ptr.get() != null && L_x_lb_ptr.get().size() > L_x_lt_ptr.get().size()))
                {//if2
                    T.create_new_lb_child(L_x_lb_ptr.get(), L_y_lb_ptr.get());
                    T.go_to_lb_child();
                    decompose_subtreenode(T, act_x_List_copy, act_y_List_copy, new_leaf_List);
                    T.go_to_father();
                }//if2
                else //L_x_lt_ptr.get() != null &&  L_x_lb_ptr.get().size() <= L_x_lt_ptr.get().size()
                {//else1
                    T.create_new_lt_child(L_x_lt_ptr.get(), L_y_lt_ptr.get());
                    T.go_to_lt_child();
                    decompose_subtreenode(T, act_x_List_copy, act_y_List_copy, new_leaf_List);
                    T.go_to_father();
                }//else1
            }//if1
            else //L_x_r_ptr.get() != null && (L_x_l_ptr.get().size() <= L_x_r_ptr.get().size())
            {//else2 right half contains more particles
                split_in_y_direction(act_ptr, L_x_rb_ptr,
                        L_y_rb_ptr, L_x_rt_ptr, L_y_rt_ptr);
                if ((L_x_rt_ptr.get() == null) ||
                        (L_x_rb_ptr.get() != null && L_x_rb_ptr.get().size() > L_x_rt_ptr.get().size()))
                {//if3
                    T.create_new_rb_child(L_x_rb_ptr.get(), L_y_rb_ptr.get());
                    T.go_to_rb_child();
                    decompose_subtreenode(T, act_x_List_copy, act_y_List_copy, new_leaf_List);
                    T.go_to_father();
                }//if3
                else// L_x_rt_ptr.get() != null && L_x_rb_ptr.get().size() <= L_x_rt_ptr.get().size()
                {//else3
                    T.create_new_rt_child(L_x_rt_ptr.get(), L_y_rt_ptr.get());
                    T.go_to_rt_child();
                    decompose_subtreenode(T, act_x_List_copy, act_y_List_copy, new_leaf_List);
                    T.go_to_father();
                }//else3
            }//else2

            //build up the rest of the quad-subLists

            if (L_x_l_ptr.get() != null && L_x_lb_ptr.get() == null && L_x_lt_ptr.get() == null &&
                    !act_ptr.child_lb_exists() && !act_ptr.child_lt_exists())
            {
                split_in_y_direction(act_ptr, L_x_l_ptr, L_x_lb_ptr, L_x_lt_ptr, L_y_l_ptr,
                        L_y_lb_ptr, L_y_lt_ptr);
            }
            else if (L_x_r_ptr.get() != null && L_x_rb_ptr.get() == null && L_x_rt_ptr.get() == null &&
                    !act_ptr.child_rb_exists() && !act_ptr.child_rt_exists())
            {
                split_in_y_direction(act_ptr, L_x_r_ptr, L_x_rb_ptr, L_x_rt_ptr, L_y_r_ptr,
                        L_y_rb_ptr, L_y_rt_ptr);
            }

            //create rest of the childnodes
            if ((!act_ptr.child_lb_exists()) && (L_x_lb_ptr.get() != null))
            {
                T.create_new_lb_child(L_x_lb_ptr.get(), L_y_lb_ptr.get());
                T.go_to_lb_child();
                new_leaf_List.add(T.get_act_ptr());
                T.go_to_father();
            }
            if ((!act_ptr.child_lt_exists()) && (L_x_lt_ptr.get() != null))
            {
                T.create_new_lt_child(L_x_lt_ptr.get(), L_y_lt_ptr.get());
                T.go_to_lt_child();
                new_leaf_List.add(T.get_act_ptr());
                T.go_to_father();
            }
            if ((!act_ptr.child_rb_exists()) && (L_x_rb_ptr.get() != null))
            {
                T.create_new_rb_child(L_x_rb_ptr.get(), L_y_rb_ptr.get());
                T.go_to_rb_child();
                new_leaf_List.add(T.get_act_ptr());
                T.go_to_father();
            }
            if ((!act_ptr.child_rt_exists()) && (L_x_rt_ptr.get() != null))
            {
                T.create_new_rt_child(L_x_rt_ptr.get(), L_y_rt_ptr.get());
                T.go_to_rt_child();
                new_leaf_List.add(T.get_act_ptr());
                T.go_to_father();
            }
            //reset  act_ptr.set_x(y)_List_ptr to avoid multiple deleting of dynamic memory;
            //(only if *act_ptr is a leaf of T the reserved space is freed (and this is
            //sufficient !!!))
            act_ptr.set_x_List_ptr(null);
            act_ptr.set_y_List_ptr(null);
        }//if0
        else
        { //else a leaf or machineprecision is reached:
            //The List contained_nodes is set for *act_ptr and the information of
            //act_x_List_copy and act_y_List_copy is used to insert particles into the
            //shorter Lists of previous touched treenodes;additionaly the dynamical allocated
            //space for *act_ptr.get_x(y)_List_ptr() is freed.

            List<node> L = new ArrayList<node>();

            //set List contained nodes

            for (ParticleInfo it : act_ptr.get_x_List_ptr())
            {
                L.add(it.get_vertex());
            }
            T.get_act_ptr().set_contained_nodes(L);

            //insert particles into previous touched Lists

            build_up_sorted_subLists(act_x_List_copy, act_y_List_copy);

            //free allocated space for *act_ptr.get_x(y)_List_ptr()
            act_ptr.get_x_List_ptr().clear();//free used space for old L_x,L_y Lists
            act_ptr.get_y_List_ptr().clear();
        }//else
    }

    void calculate_boundaries_of_act_node(
            QuadTreeNodeNM act_ptr,
            ref<Double> x_min,
            ref<Double> x_max,
            ref<Double> y_min,
            ref<Double> y_max)
    {
        List<ParticleInfo> L_x_ptr = act_ptr.get_x_List_ptr();
        List<ParticleInfo> L_y_ptr = act_ptr.get_y_List_ptr();

        x_min.set(L_x_ptr.get(0).get_x_y_coord());
        x_max.set(L_x_ptr.get(L_x_ptr.size() - 1).get_x_y_coord());
        y_min.set(L_y_ptr.get(0).get_x_y_coord());
        y_max.set(L_y_ptr.get(L_y_ptr.size() - 1).get_x_y_coord());
    }

    boolean in_lt_quad(
            QuadTreeNodeNM act_ptr,
            double x_min,
            double x_max,
            double y_min,
            double y_max)
    {
        double l = act_ptr.get_Sm_downleftcorner().m_x;
        double r = act_ptr.get_Sm_downleftcorner().m_x + act_ptr.get_Sm_boxlength() / 2;
        double b = act_ptr.get_Sm_downleftcorner().m_y + act_ptr.get_Sm_boxlength() / 2;
        double t = act_ptr.get_Sm_downleftcorner().m_y + act_ptr.get_Sm_boxlength();

        if (l <= x_min && x_max < r && b <= y_min && y_max < t)
        {
            return true;
        }
        else if (x_min == x_max && y_min == y_max && l == r && t == b && x_min == r && y_min == b)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    boolean in_rt_quad(
            QuadTreeNodeNM act_ptr,
            double x_min,
            double x_max,
            double y_min,
            double y_max)
    {
        double l = act_ptr.get_Sm_downleftcorner().m_x + act_ptr.get_Sm_boxlength() / 2;
        double r = act_ptr.get_Sm_downleftcorner().m_x + act_ptr.get_Sm_boxlength();
        double b = act_ptr.get_Sm_downleftcorner().m_y + act_ptr.get_Sm_boxlength() / 2;
        double t = act_ptr.get_Sm_downleftcorner().m_y + act_ptr.get_Sm_boxlength();

        if (l <= x_min && x_max < r && b <= y_min && y_max < t)
        {
            return true;
        }
        else if (x_min == x_max && y_min == y_max && l == r && t == b && x_min == r && y_min == b)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    boolean in_lb_quad(
            QuadTreeNodeNM act_ptr,
            double x_min,
            double x_max,
            double y_min,
            double y_max)
    {
        double l = act_ptr.get_Sm_downleftcorner().m_x;
        double r = act_ptr.get_Sm_downleftcorner().m_x + act_ptr.get_Sm_boxlength() / 2;
        double b = act_ptr.get_Sm_downleftcorner().m_y;
        double t = act_ptr.get_Sm_downleftcorner().m_y + act_ptr.get_Sm_boxlength() / 2;

        if (l <= x_min && x_max < r && b <= y_min && y_max < t)
        {
            return true;
        }
        else if (x_min == x_max && y_min == y_max && l == r && t == b && x_min == r && y_min == b)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    boolean in_rb_quad(
            QuadTreeNodeNM act_ptr,
            double x_min,
            double x_max,
            double y_min,
            double y_max)
    {
        double l = act_ptr.get_Sm_downleftcorner().m_x + act_ptr.get_Sm_boxlength() / 2;
        double r = act_ptr.get_Sm_downleftcorner().m_x + act_ptr.get_Sm_boxlength();
        double b = act_ptr.get_Sm_downleftcorner().m_y;
        double t = act_ptr.get_Sm_downleftcorner().m_y + act_ptr.get_Sm_boxlength() / 2;

        if (l <= x_min && x_max < r && b <= y_min && y_max < t)
        {
            return true;
        }
        else if (x_min == x_max && y_min == y_max && l == r && t == b && x_min == r && y_min == b)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    void split_in_x_direction(
            QuadTreeNodeNM act_ptr,
            ref<List<ParticleInfo>> L_x_left_ptr,
            ref<List<ParticleInfo>> L_y_left_ptr,
            ref<List<ParticleInfo>> L_x_right_ptr,
            ref<List<ParticleInfo>> L_y_right_ptr)
    {
        ListIterator<ParticleInfo> l_item = act_ptr.get_x_List_ptr().listIterator();
        ListIterator<ParticleInfo> r_item = act_ptr.get_x_List_ptr().listIterator(act_ptr.get_x_List_ptr().size());
        ParticleInfo last_left_item = null;
        double act_Sm_boxlength_half = act_ptr.get_Sm_boxlength() / 2;
        double x_mid_coord = act_ptr.get_Sm_downleftcorner().m_x + act_Sm_boxlength_half;
        double l_xcoord, r_xcoord;
        boolean left_particleList_empty = false;
        boolean right_particleList_empty = false;
        boolean left_particleList_larger = true;

        //traverse *act_ptr.get_x_List_ptr() from left and right

        while (l_item.hasNext() && r_item.hasPrevious())
        {//while
            l_xcoord = l_item.next().get_x_y_coord();
            r_xcoord = r_item.previous().get_x_y_coord();
            if (l_xcoord >= x_mid_coord)
            {
                left_particleList_larger = false;
                if (l_item.previousIndex() >= 0)
                {
                    last_left_item = act_ptr.get_x_List_ptr().get(l_item.previousIndex());
                }
                else
                {
                    left_particleList_empty = true;
                }
            }
            else if (r_xcoord < x_mid_coord)
            {
                if (r_item.nextIndex() < act_ptr.get_x_List_ptr().size())
                {
                    last_left_item = act_ptr.get_x_List_ptr().get(r_item.nextIndex());
                }
                else
                {
                    right_particleList_empty = true;
                }
            }
            else
            {
                break;
            }
        }//while

        //get the L_x(y) Lists of the bigger half (from *act_ptr.get_x(y)_List_ptr))
        //and make entries in L_x_copy,L_y_copy for the smaller halfs

        if (left_particleList_empty)
        {
            L_x_left_ptr.set(null);
            L_y_left_ptr.set(null);
            L_x_right_ptr.set(act_ptr.get_x_List_ptr());
            L_y_right_ptr.set(act_ptr.get_y_List_ptr());
        }
        else if (right_particleList_empty)
        {
            L_x_left_ptr.set(act_ptr.get_x_List_ptr());
            L_y_left_ptr.set(act_ptr.get_y_List_ptr());
            L_x_right_ptr.set(null);
            L_y_right_ptr.set(null);
        }
        else if (left_particleList_larger)
        {
            x_delete_right_subLists(act_ptr, L_x_left_ptr, L_y_left_ptr,
                    L_x_right_ptr, L_y_right_ptr, last_left_item);
        }
        else //left particleList is smaller or equal to right particleList
        {
            x_delete_left_subLists(act_ptr, L_x_left_ptr, L_y_left_ptr,
                    L_x_right_ptr, L_y_right_ptr, last_left_item);
        }
    }

    void split_in_y_direction(
            QuadTreeNodeNM act_ptr,
            ref<List<ParticleInfo>> L_x_left_ptr,
            ref<List<ParticleInfo>> L_y_left_ptr,
            ref<List<ParticleInfo>> L_x_right_ptr,
            ref<List<ParticleInfo>> L_y_right_ptr)
    {
        ListIterator<ParticleInfo> l_item = act_ptr.get_y_List_ptr().listIterator();
        ListIterator<ParticleInfo> r_item = act_ptr.get_y_List_ptr().listIterator(act_ptr.get_y_List_ptr().size());
        ParticleInfo last_left_item = null;
        double act_Sm_boxlength_half = act_ptr.get_Sm_boxlength() / 2;
        double y_mid_coord = act_ptr.get_Sm_downleftcorner().m_y + act_Sm_boxlength_half;
        double l_ycoord, r_ycoord;
        boolean last_left_item_found = false;
        boolean left_particleList_empty = false;
        boolean right_particleList_empty = false;
        boolean left_particleList_larger = true;
        //traverse *act_ptr.get_y_List_ptr() from left and right

        while (l_item.hasNext() && r_item.hasPrevious())
        {//while
            l_ycoord = l_item.next().get_x_y_coord();
            r_ycoord = r_item.previous().get_x_y_coord();
            if (l_ycoord >= y_mid_coord)
            {
                left_particleList_larger = false;
                if (l_item.previousIndex() >= 0)
                {
                    last_left_item = act_ptr.get_y_List_ptr().get(l_item.previousIndex());
                }
                else
                {
                    left_particleList_empty = true;
                }
            }
            else if (r_ycoord < y_mid_coord)
            {
                if (r_item.nextIndex() < act_ptr.get_y_List_ptr().size())
                {
                    last_left_item = act_ptr.get_y_List_ptr().get(r_item.nextIndex());
                }
                else
                {
                    right_particleList_empty = true;
                }
            }
            else
            {
                break;
            }
        }//while

        //get the L_x(y) Lists of the bigger half (from *act_ptr.get_x(y)_List_ptr))
        //and make entries in L_x_copy,L_y_copy for the smaller halfs

        if (left_particleList_empty)
        {
            L_x_left_ptr.set(null);
            L_y_left_ptr.set(null);
            L_x_right_ptr.set(act_ptr.get_x_List_ptr());
            L_y_right_ptr.set(act_ptr.get_y_List_ptr());
        }
        else if (right_particleList_empty)
        {
            L_x_left_ptr.set(act_ptr.get_x_List_ptr());
            L_y_left_ptr.set(act_ptr.get_y_List_ptr());
            L_x_right_ptr.set(null);
            L_y_right_ptr.set(null);
        }
        else if (left_particleList_larger)
        {
            y_delete_right_subLists(act_ptr, L_x_left_ptr, L_y_left_ptr,
                    L_x_right_ptr, L_y_right_ptr, last_left_item);
        }
        else //left particleList is smaller or equal to right particleList
        {
            y_delete_left_subLists(act_ptr, L_x_left_ptr, L_y_left_ptr,
                    L_x_right_ptr, L_y_right_ptr, last_left_item);
        }
    }

    void x_delete_right_subLists(
            QuadTreeNodeNM act_ptr,
            ref<List<ParticleInfo>> L_x_left_ptr,
            ref<List<ParticleInfo>> L_y_left_ptr,
            ref<List<ParticleInfo>> L_x_right_ptr,
            ref<List<ParticleInfo>> L_y_right_ptr,
            ParticleInfo last_left_item)
    {
        ParticleInfo act_p_info;
        ParticleInfo p_in_L_x_item, p_in_L_y_item;

        L_x_left_ptr.set(act_ptr.get_x_List_ptr());
        L_y_left_ptr.set(act_ptr.get_y_List_ptr());
        L_x_right_ptr.set(new ArrayList<ParticleInfo>());
        L_y_right_ptr.set(new ArrayList<ParticleInfo>());

        for (int i = L_x_left_ptr.get().indexOf(last_left_item) + 1; i < L_x_left_ptr.get().size(); i++)
        {//while
            act_p_info = L_x_left_ptr.get().get(i);

            //save references for *L_x(y)_right_ptr in L_x(y)_copy
            p_in_L_x_item = act_p_info.get_copy_item();
            p_in_L_x_item.set_subList_ptr(L_x_right_ptr.get());

            p_in_L_y_item = act_p_info.get_cross_ref_item().get_copy_item();
            p_in_L_y_item.set_subList_ptr(L_y_right_ptr.get());

            //create *L_x(y)_left_ptr
            L_y_left_ptr.get().remove(act_p_info.get_cross_ref_item());
            L_x_left_ptr.get().remove(act_p_info);
        }//while
    }

    void x_delete_left_subLists(
            QuadTreeNodeNM act_ptr,
            ref<List<ParticleInfo>> L_x_left_ptr,
            ref<List<ParticleInfo>> L_y_left_ptr,
            ref<List<ParticleInfo>> L_x_right_ptr,
            ref<List<ParticleInfo>> L_y_right_ptr,
            ParticleInfo last_left_item)
    {
        ParticleInfo act_p_info;
        ParticleInfo p_in_L_x_item, p_in_L_y_item;

        L_x_right_ptr.set(act_ptr.get_x_List_ptr());
        L_y_right_ptr.set(act_ptr.get_y_List_ptr());
        L_x_left_ptr.set(new ArrayList<ParticleInfo>());
        L_y_left_ptr.set(new ArrayList<ParticleInfo>());

        for (int i = 0; i <= L_x_right_ptr.get().indexOf(last_left_item); i++)
        {//while
            act_p_info = L_x_right_ptr.get().get(i);

            //save references for *L_x(y)_right_ptr in L_x(y)_copy
            p_in_L_x_item = act_p_info.get_copy_item();
            p_in_L_x_item.set_subList_ptr(L_x_left_ptr.get());

            p_in_L_y_item = act_p_info.get_cross_ref_item().get_copy_item();
            p_in_L_y_item.set_subList_ptr(L_y_left_ptr.get());

            //create *L_x(y)_right_ptr
            L_y_right_ptr.get().remove(act_p_info.get_cross_ref_item());
            L_x_right_ptr.get().remove(act_p_info);
        }//while
    }

    void y_delete_right_subLists(
            QuadTreeNodeNM act_ptr,
            ref<List<ParticleInfo>> L_x_left_ptr,
            ref<List<ParticleInfo>> L_y_left_ptr,
            ref<List<ParticleInfo>> L_x_right_ptr,
            ref<List<ParticleInfo>> L_y_right_ptr,
            ParticleInfo last_left_item)
    {
        ParticleInfo act_p_info;
        ParticleInfo p_in_L_x_item, p_in_L_y_item;

        L_x_left_ptr.set(act_ptr.get_x_List_ptr());
        L_y_left_ptr.set(act_ptr.get_y_List_ptr());
        L_x_right_ptr.set(new ArrayList<ParticleInfo>());
        L_y_right_ptr.set(new ArrayList<ParticleInfo>());

        for (int i = L_y_left_ptr.get().indexOf(last_left_item) + 1; i < L_y_left_ptr.get().size(); i++)
        {//while
            act_p_info = L_x_left_ptr.get().get(i);

            //save references for *L_x(y)_right_ptr in L_x(y)_copy
            p_in_L_y_item = act_p_info.get_copy_item();
            p_in_L_y_item.set_subList_ptr(L_y_right_ptr.get());

            p_in_L_x_item = act_p_info.get_cross_ref_item().get_copy_item();
            p_in_L_x_item.set_subList_ptr(L_x_right_ptr.get());

            //create *L_x(y)_left_ptr
            L_x_left_ptr.get().remove(act_p_info.get_cross_ref_item());
            L_y_left_ptr.get().remove(act_p_info);
        }//while
    }

    void y_delete_left_subLists(
            QuadTreeNodeNM act_ptr,
            ref<List<ParticleInfo>> L_x_left_ptr,
            ref<List<ParticleInfo>> L_y_left_ptr,
            ref<List<ParticleInfo>> L_x_right_ptr,
            ref<List<ParticleInfo>> L_y_right_ptr,
            ParticleInfo last_left_item)
    {
        ParticleInfo act_p_info;
        ParticleInfo p_in_L_x_item, p_in_L_y_item;

        L_x_right_ptr.set(act_ptr.get_x_List_ptr());
        L_y_right_ptr.set(act_ptr.get_y_List_ptr());
        L_x_left_ptr.set(new ArrayList<ParticleInfo>());
        L_y_left_ptr.set(new ArrayList<ParticleInfo>());

        for (int i = 0; i <= L_y_right_ptr.get().indexOf(last_left_item); i++)
        {//while
            act_p_info = L_x_left_ptr.get().get(i);

            //save references for *L_x(y)_right_ptr in L_x(y)_copy
            p_in_L_y_item = act_p_info.get_copy_item();
            p_in_L_y_item.set_subList_ptr(L_y_left_ptr.get());

            p_in_L_x_item = act_p_info.get_cross_ref_item().get_copy_item();
            p_in_L_x_item.set_subList_ptr(L_x_left_ptr.get());

            //create *L_x(y)_right_ptr
            L_x_right_ptr.get().remove(act_p_info.get_cross_ref_item());
            L_y_right_ptr.get().remove(act_p_info);
        }//while
    }

    void split_in_y_direction(
            QuadTreeNodeNM act_ptr,
            ref<List<ParticleInfo>> L_x_ptr,
            ref<List<ParticleInfo>> L_x_b_ptr,
            ref<List<ParticleInfo>> L_x_t_ptr,
            ref<List<ParticleInfo>> L_y_ptr,
            ref<List<ParticleInfo>> L_y_b_ptr,
            ref<List<ParticleInfo>> L_y_t_ptr)
    {
        ListIterator<ParticleInfo> l_item = L_y_ptr.get().listIterator();
        ListIterator<ParticleInfo> r_item = L_y_ptr.get().listIterator(L_y_ptr.get().size());
        ParticleInfo last_left_item = null;
        double act_Sm_boxlength_half = act_ptr.get_Sm_boxlength() / 2;
        double y_mid_coord = act_ptr.get_Sm_downleftcorner().m_y + act_Sm_boxlength_half;
        double l_ycoord, r_ycoord;
        boolean left_particleList_empty = false;
        boolean right_particleList_empty = false;
        boolean left_particleList_larger = true;

        //traverse *L_y_ptr from left and right

        while (l_item.hasNext() && r_item.hasPrevious())
        {//while
            l_ycoord = l_item.next().get_x_y_coord();
            r_ycoord = r_item.previous().get_x_y_coord();
            if (l_ycoord >= y_mid_coord)
            {
                left_particleList_larger = false;
                if (l_item.previousIndex() >= 0)
                {
                    last_left_item = L_y_ptr.get().get(l_item.previousIndex());
                }
                else
                {
                    left_particleList_empty = true;
                }
            }
            else if (r_ycoord < y_mid_coord)
            {
                if (r_item.nextIndex() < L_y_ptr.get().size())
                {
                    last_left_item = L_y_ptr.get().get(r_item.nextIndex());
                }
                else
                {
                    right_particleList_empty = true;
                }
            }
            else
            {
                break;
            }
        }//while

        //create *L_x_l(b)_ptr

        if (left_particleList_empty)
        {
            L_x_b_ptr.set(null);
            L_y_b_ptr.set(null);
            L_x_t_ptr.set(L_x_ptr.get());
            L_y_t_ptr.set(L_y_ptr.get());
        }
        else if (right_particleList_empty)
        {
            L_x_b_ptr.set(L_x_ptr.get());
            L_y_b_ptr.set(L_y_ptr.get());
            L_x_t_ptr.set(null);
            L_y_t_ptr.set(null);
        }
        else if (left_particleList_larger)
        {
            y_move_right_subLists(L_x_ptr, L_x_b_ptr, L_x_t_ptr, L_y_ptr, L_y_b_ptr, L_y_t_ptr,
                    last_left_item);
        }
        else //left particleList is smaller or equal to right particleList
        {
            y_move_left_subLists(L_x_ptr, L_x_b_ptr, L_x_t_ptr, L_y_ptr, L_y_b_ptr, L_y_t_ptr,
                    last_left_item);
        }
    }

    void y_move_left_subLists(
            ref<List<ParticleInfo>> L_x_ptr,
            ref<List<ParticleInfo>> L_x_l_ptr,
            ref<List<ParticleInfo>> L_x_r_ptr,
            ref<List<ParticleInfo>> L_y_ptr,
            ref<List<ParticleInfo>> L_y_l_ptr,
            ref<List<ParticleInfo>> L_y_r_ptr,
            ParticleInfo last_left_item)
    {
        ParticleInfo p_in_L_x_info, p_in_L_y_info;

        L_x_r_ptr.set(L_x_ptr.get());
        L_y_r_ptr.set(L_y_ptr.get());
        L_x_l_ptr.set(new ArrayList<ParticleInfo>());
        L_y_l_ptr.set(new ArrayList<ParticleInfo>());

        //build up the L_y_Lists and update crossreferences in *L_x_l_ptr
        for (int i = 0; i <= L_y_r_ptr.get().indexOf(last_left_item); i++)
        {//while
            p_in_L_y_info = L_y_r_ptr.get().get(i);

            //create *L_x(y)_l_ptr
            L_y_l_ptr.get().add(p_in_L_y_info);
            p_in_L_x_info = p_in_L_y_info.get_cross_ref_item();
            p_in_L_x_info.set_cross_ref_item(L_y_l_ptr.get().get(L_y_l_ptr.get().size() - 1));
            p_in_L_x_info.mark(); //mark this element of the List

            //create *L_y_r_ptr
            L_y_r_ptr.get().remove(p_in_L_y_info);
        }//while

        //build up the L_x Lists and update crossreferences in *L_y_l_ptr
        for (int i = 0; i < L_x_r_ptr.get().size(); i++)
        {//while
            p_in_L_x_info = L_x_r_ptr.get().get(i);

            if (p_in_L_x_info.is_marked())
            {
                p_in_L_x_info.unmark();
                L_x_l_ptr.get().add(p_in_L_x_info);
                p_in_L_y_info = p_in_L_x_info.get_cross_ref_item();
                p_in_L_y_info.set_cross_ref_item(L_x_l_ptr.get().get(L_x_l_ptr.get().size() - 1));
            }

            //create *L_x_r_ptr
            if (p_in_L_x_info.is_marked())
            {
                L_x_r_ptr.get().remove(p_in_L_x_info);
            }
        }//while
    }

    void y_move_right_subLists(
            ref<List<ParticleInfo>> L_x_ptr,
            ref<List<ParticleInfo>> L_x_l_ptr,
            ref<List<ParticleInfo>> L_x_r_ptr,
            ref<List<ParticleInfo>> L_y_ptr,
            ref<List<ParticleInfo>> L_y_l_ptr,
            ref<List<ParticleInfo>> L_y_r_ptr,
            ParticleInfo last_left_item)
    {
        ParticleInfo p_in_L_x_info, p_in_L_y_info;

        L_x_l_ptr.set(L_x_ptr.get());
        L_y_l_ptr.set(L_y_ptr.get());
        L_x_r_ptr.set(new ArrayList<ParticleInfo>());
        L_y_r_ptr.set(new ArrayList<ParticleInfo>());

        //build up the L_y_Lists and update crossreferences in *L_x_r_ptr
        for (int i = L_y_l_ptr.get().indexOf(last_left_item) + 1; i < L_y_l_ptr.get().size(); i++)
        {//while
            p_in_L_y_info = L_y_r_ptr.get().get(i);

            //create *L_x(y)_r_ptr
            L_y_r_ptr.get().add(p_in_L_y_info);
            p_in_L_x_info = p_in_L_y_info.get_cross_ref_item();
            p_in_L_x_info.set_cross_ref_item(L_y_r_ptr.get().get(L_y_r_ptr.get().size() - 1));
            p_in_L_x_info.mark(); //mark this element of the List

            //create *L_y_l_ptr
            L_y_l_ptr.get().remove(p_in_L_y_info);
        }//while

        //build up the L_x Lists and update crossreferences in *L_y_r_ptr
        for (int i = 0; i < L_x_l_ptr.get().size(); i++)
        {//while
            p_in_L_x_info = L_x_l_ptr.get().get(i);

            if (p_in_L_x_info.is_marked())
            {
                p_in_L_x_info.unmark();
                L_x_l_ptr.get().add(p_in_L_x_info);
                p_in_L_y_info = p_in_L_x_info.get_cross_ref_item();
                p_in_L_y_info.set_cross_ref_item(L_x_r_ptr.get().get(L_x_r_ptr.get().size() - 1));
            }

            //create *L_x_r_ptr
            if (p_in_L_x_info.is_marked())
            {
                L_x_l_ptr.get().remove(p_in_L_x_info);
            }
        }//while
    }

    void build_up_sorted_subLists(
            List<ParticleInfo> L_x_copy,
            List<ParticleInfo> L_y_copy)
    {
        ParticleInfo P_x, P_y;
        List<ParticleInfo> L_x_ptr = new ArrayList<ParticleInfo>();
        List<ParticleInfo> L_y_ptr = new ArrayList<ParticleInfo>();
        ListIterator<ParticleInfo> it;
        ParticleInfo new_cross_ref_item;

        it = L_x_copy.listIterator();
        while (it.hasNext())
        {
            P_x = it.next();

            if (P_x.get_subList_ptr() != null)
            {
                //reset values
                L_x_ptr = P_x.get_subList_ptr();
                P_x.set_subList_ptr(null); //clear subList_ptr
                P_x.set_copy_item(null);   //clear copy_item
                P_x.unmark(); //unmark this element
                P_x.set_tmp_cross_ref_item(null);//clear tmp_cross_ref_item

                //update *L_x_ptr
                L_x_ptr.add(P_x);

                //update L_x_copy
                P_x.set_tmp_cross_ref_item(P_x);
            }
        }

        it = L_x_copy.listIterator();
        while (it.hasNext())
        {
            P_y = it.next();

            if (P_y.get_subList_ptr() != null)
            {
                //reset values
                L_y_ptr = P_y.get_subList_ptr();
                P_y.set_subList_ptr(null); //clear subList_ptr
                P_y.set_copy_item(null);   //clear copy_item
                P_y.unmark(); //unmark this element
                P_y.set_tmp_cross_ref_item(null);//clear tmp_cross_ref_item

                //update *L_x(y)_ptr

                new_cross_ref_item = P_y.get_cross_ref_item().get_tmp_cross_ref_item();
                P_y.set_cross_ref_item(new_cross_ref_item);
                L_y_ptr.add(P_y);
                P_x = new_cross_ref_item;
                P_x.set_cross_ref_item(P_y);
            }
        }
    }

// **********Functions needed for subtree by subtree  treeruction(Begin)*********
    void build_up_red_quad_tree_subtree_by_subtree(
            Graph G,
            NodeArray<NodeAttributes> A,
            QuadTreeNM T)
    {
        List<QuadTreeNodeNM> act_subtree_root_List, new_subtree_root_List;
        List<QuadTreeNodeNM> act_subtree_root_List_ptr, new_subtree_root_List_ptr, help_ptr;
        QuadTreeNodeNM subtree_root_ptr;

        build_up_root_vertex(G, T);

        act_subtree_root_List = new ArrayList<QuadTreeNodeNM>();
        new_subtree_root_List = new ArrayList<QuadTreeNodeNM>();
        act_subtree_root_List.add(T.get_root_ptr());
        act_subtree_root_List_ptr = act_subtree_root_List;
        new_subtree_root_List_ptr = new_subtree_root_List;

        while (!act_subtree_root_List_ptr.isEmpty())
        {
            while (!act_subtree_root_List_ptr.isEmpty())
            {
                subtree_root_ptr = act_subtree_root_List_ptr.remove(0);
                construct_subtree(A, T, subtree_root_ptr, new_subtree_root_List_ptr);
            }
            help_ptr = act_subtree_root_List_ptr;
            act_subtree_root_List_ptr = new_subtree_root_List_ptr;
            new_subtree_root_List_ptr = help_ptr;
        }
    }

    void build_up_root_vertex(Graph G, QuadTreeNM T)
    {
        node v;

        T.init_tree();
        T.get_root_ptr().set_Sm_level(0);
        T.get_root_ptr().set_Sm_downleftcorner(down_left_corner);
        T.get_root_ptr().set_Sm_boxlength(boxlength);
        T.get_root_ptr().set_particlenumber_in_subtree(G.numberOfNodes());
        for (Iterator<node> i = G.nodesIterator(); i.hasNext();)
        {
            v = i.next();
            T.get_root_ptr().pushBack_contained_nodes(v);
        }
    }

    void construct_subtree(
            NodeArray<NodeAttributes> A,
            QuadTreeNM T,
            QuadTreeNodeNM subtree_root_ptr,
            List<QuadTreeNodeNM> new_subtree_root_List)
    {
        int n = subtree_root_ptr.get_particlenumber_in_subtree();
        int subtree_depth = (int) (Math.max(1.0, Math.floor(Math.log(n) / Math.log(4.0)) - 2.0));
        int maxindex = 1;

        for (int i = 1; i <= subtree_depth; i++)
        {
            maxindex *= 2;
        }
        double subtree_min_boxlength = subtree_root_ptr.get_Sm_boxlength() / maxindex;

        if (subtree_min_boxlength >= MIN_BOX_LENGTH)
        {
            QuadTreeNodeNM[][] leaf_ptr = new QuadTreeNodeNM[maxindex][maxindex];

            T.set_act_ptr(subtree_root_ptr);
            if (find_smallest_quad(A, T)) //not all nodes have the same position
            {
                construct_complete_subtree(T, subtree_depth, leaf_ptr, 0, 0, 0);
                set_contained_nodes_for_leaves(A, subtree_root_ptr, leaf_ptr, maxindex);
                T.set_act_ptr(subtree_root_ptr);
                set_particlenumber_in_subtree_entries(T);
                T.set_act_ptr(subtree_root_ptr);
                construct_reduced_subtree(A, T, new_subtree_root_List);
            }
        }
    }

    void construct_complete_subtree(
            QuadTreeNM T,
            int subtree_depth,
            QuadTreeNodeNM[][] leaf_ptr,
            int act_depth,
            int act_x_index,
            int act_y_index)
    {
        if (act_depth < subtree_depth)
        {
            T.create_new_lt_child();
            T.create_new_rt_child();
            T.create_new_lb_child();
            T.create_new_rb_child();

            T.go_to_lt_child();
            construct_complete_subtree(T, subtree_depth, leaf_ptr, act_depth + 1, 2 * act_x_index,
                    2 * act_y_index + 1);
            T.go_to_father();

            T.go_to_rt_child();
            construct_complete_subtree(T, subtree_depth, leaf_ptr, act_depth + 1, 2 * act_x_index + 1,
                    2 * act_y_index + 1);
            T.go_to_father();

            T.go_to_lb_child();
            construct_complete_subtree(T, subtree_depth, leaf_ptr, act_depth + 1, 2 * act_x_index,
                    2 * act_y_index);
            T.go_to_father();

            T.go_to_rb_child();
            construct_complete_subtree(T, subtree_depth, leaf_ptr, act_depth + 1, 2 * act_x_index + 1,
                    2 * act_y_index);
            T.go_to_father();
        }
        else if (act_depth == subtree_depth)
        {
            leaf_ptr[act_x_index][act_y_index] = T.get_act_ptr();
        }
        else if (DEBUG_BUILD)
        {
            println("Errorruct_complete_subtree()");
        }
    }

    void set_contained_nodes_for_leaves(
            NodeArray<NodeAttributes> A,
            QuadTreeNodeNM subtree_root_ptr,
            QuadTreeNodeNM[][] leaf_ptr,
            int maxindex)
    {
        node v;
        QuadTreeNodeNM act_ptr;
        double xcoord, ycoord;
        int x_index, y_index;
        double minboxlength = subtree_root_ptr.get_Sm_boxlength() / maxindex;

        while (!subtree_root_ptr.contained_nodes_empty())
        {
            v = subtree_root_ptr.pop_contained_nodes();
            xcoord = A.get(v).get_x() - subtree_root_ptr.get_Sm_downleftcorner().m_x;
            ycoord = A.get(v).get_y() - subtree_root_ptr.get_Sm_downleftcorner().m_y;;
            x_index = (int) (xcoord / minboxlength);
            y_index = (int) (ycoord / minboxlength);
            act_ptr = leaf_ptr[x_index][y_index];
            act_ptr.pushBack_contained_nodes(v);
            act_ptr.set_particlenumber_in_subtree(act_ptr.get_particlenumber_in_subtree() + 1);
        }
    }

    void set_particlenumber_in_subtree_entries(QuadTreeNM T)
    {
        int child_nr;

        if (!T.get_act_ptr().is_leaf())
        {//if
            T.get_act_ptr().set_particlenumber_in_subtree(0);

            if (T.get_act_ptr().child_lt_exists())
            {
                T.go_to_lt_child();
                set_particlenumber_in_subtree_entries(T);
                T.go_to_father();
                child_nr = T.get_act_ptr().get_child_lt_ptr().get_particlenumber_in_subtree();
                T.get_act_ptr().set_particlenumber_in_subtree(child_nr + T.get_act_ptr().
                        get_particlenumber_in_subtree());
            }
            if (T.get_act_ptr().child_rt_exists())
            {
                T.go_to_rt_child();
                set_particlenumber_in_subtree_entries(T);
                T.go_to_father();
                child_nr = T.get_act_ptr().get_child_rt_ptr().get_particlenumber_in_subtree();
                T.get_act_ptr().set_particlenumber_in_subtree(child_nr + T.get_act_ptr().
                        get_particlenumber_in_subtree());
            }
            if (T.get_act_ptr().child_lb_exists())
            {
                T.go_to_lb_child();
                set_particlenumber_in_subtree_entries(T);
                T.go_to_father();
                child_nr = T.get_act_ptr().get_child_lb_ptr().get_particlenumber_in_subtree();
                T.get_act_ptr().set_particlenumber_in_subtree(child_nr + T.get_act_ptr().
                        get_particlenumber_in_subtree());
            }
            if (T.get_act_ptr().child_rb_exists())
            {
                T.go_to_rb_child();
                set_particlenumber_in_subtree_entries(T);
                T.go_to_father();
                child_nr = T.get_act_ptr().get_child_rb_ptr().get_particlenumber_in_subtree();
                T.get_act_ptr().set_particlenumber_in_subtree(child_nr + T.get_act_ptr().
                        get_particlenumber_in_subtree());
            }
        }//if
    }

    void construct_reduced_subtree(
            NodeArray<NodeAttributes> A,
            QuadTreeNM T,
            List<QuadTreeNodeNM> new_subtree_root_List)
    {
        do
        {
            QuadTreeNodeNM act_ptr = T.get_act_ptr();
            delete_empty_subtrees(T);
            T.set_act_ptr(act_ptr);
        } while (check_and_delete_degenerated_node(T) == true);

        if (!T.get_act_ptr().is_leaf() && T.get_act_ptr().get_particlenumber_in_subtree() <=
                 particles_in_leaves())
        {
            delete_sparse_subtree(T, T.get_act_ptr());
        }

        //push leaves that contain many particles
        if (T.get_act_ptr().is_leaf() && T.get_act_ptr().
                get_particlenumber_in_subtree() > particles_in_leaves())
        {
            new_subtree_root_List.add(T.get_act_ptr());
        }
        //find smallest quad for leaves of T
        else if (T.get_act_ptr().is_leaf() && T.get_act_ptr().
                get_particlenumber_in_subtree() <= particles_in_leaves())
        {
            find_smallest_quad(A, T);
        }
        //recursive calls
        else if (!T.get_act_ptr().is_leaf())
        {//else
            if (T.get_act_ptr().child_lt_exists())
            {
                T.go_to_lt_child();
                construct_reduced_subtree(A, T, new_subtree_root_List);
                T.go_to_father();
            }
            if (T.get_act_ptr().child_rt_exists())
            {
                T.go_to_rt_child();
                construct_reduced_subtree(A, T, new_subtree_root_List);
                T.go_to_father();
            }
            if (T.get_act_ptr().child_lb_exists())
            {
                T.go_to_lb_child();
                construct_reduced_subtree(A, T, new_subtree_root_List);
                T.go_to_father();
            }
            if (T.get_act_ptr().child_rb_exists())
            {
                T.go_to_rb_child();
                construct_reduced_subtree(A, T, new_subtree_root_List);
                T.go_to_father();
            }
        }//else
    }

    void delete_empty_subtrees(QuadTreeNM T)
    {
        int child_part_nr;
        QuadTreeNodeNM act_ptr = T.get_act_ptr();

        if (act_ptr.child_lt_exists())
        {
            child_part_nr = act_ptr.get_child_lt_ptr().get_particlenumber_in_subtree();
            if (child_part_nr == 0)
            {
                T.delete_tree(act_ptr.get_child_lt_ptr());
                act_ptr.set_child_lt_ptr(null);
            }
        }

        if (act_ptr.child_rt_exists())
        {
            child_part_nr = act_ptr.get_child_rt_ptr().get_particlenumber_in_subtree();
            if (child_part_nr == 0)
            {
                T.delete_tree(act_ptr.get_child_rt_ptr());
                act_ptr.set_child_rt_ptr(null);
            }
        }

        if (act_ptr.child_lb_exists())
        {
            child_part_nr = act_ptr.get_child_lb_ptr().get_particlenumber_in_subtree();
            if (child_part_nr == 0)
            {
                T.delete_tree(act_ptr.get_child_lb_ptr());
                act_ptr.set_child_lb_ptr(null);
            }
        }

        if (act_ptr.child_rb_exists())
        {
            child_part_nr = act_ptr.get_child_rb_ptr().get_particlenumber_in_subtree();
            if (child_part_nr == 0)
            {
                T.delete_tree(act_ptr.get_child_rb_ptr());
                act_ptr.set_child_rb_ptr(null);
            }
        }
    }

    boolean check_and_delete_degenerated_node(QuadTreeNM T)
    {
        QuadTreeNodeNM delete_ptr;
        QuadTreeNodeNM father_ptr;
        QuadTreeNodeNM child_ptr;

        boolean lt_child = T.get_act_ptr().child_lt_exists();
        boolean rt_child = T.get_act_ptr().child_rt_exists();
        boolean lb_child = T.get_act_ptr().child_lb_exists();
        boolean rb_child = T.get_act_ptr().child_rb_exists();
        boolean is_degenerated = false;

        if (lt_child && !rt_child && !lb_child && !rb_child)
        {//if1
            is_degenerated = true;
            delete_ptr = T.get_act_ptr();
            child_ptr = T.get_act_ptr().get_child_lt_ptr();
            if (T.get_act_ptr() == T.get_root_ptr())//special case
            {
                T.set_root_ptr(child_ptr);
                T.set_act_ptr(T.get_root_ptr());
            }
            else//usual case
            {
                father_ptr = T.get_act_ptr().get_father_ptr();
                child_ptr.set_father_ptr(father_ptr);
                if (father_ptr.get_child_lt_ptr() == T.get_act_ptr())
                {
                    father_ptr.set_child_lt_ptr(child_ptr);
                }
                else if (father_ptr.get_child_rt_ptr() == T.get_act_ptr())
                {
                    father_ptr.set_child_rt_ptr(child_ptr);
                }
                else if (father_ptr.get_child_lb_ptr() == T.get_act_ptr())
                {
                    father_ptr.set_child_lb_ptr(child_ptr);
                }
                else if (father_ptr.get_child_rb_ptr() == T.get_act_ptr())
                {
                    father_ptr.set_child_rb_ptr(child_ptr);
                }
                else if (DEBUG_BUILD)
                {
                    println("Error delete_degenerated_node");
                }
                T.set_act_ptr(child_ptr);
            }
        }//if1
        else if (!lt_child && rt_child && !lb_child && !rb_child)
        {//if2
            is_degenerated = true;
            delete_ptr = T.get_act_ptr();
            child_ptr = T.get_act_ptr().get_child_rt_ptr();
            if (T.get_act_ptr() == T.get_root_ptr())//special case
            {
                T.set_root_ptr(child_ptr);
                T.set_act_ptr(T.get_root_ptr());
            }
            else//usual case
            {
                father_ptr = T.get_act_ptr().get_father_ptr();
                child_ptr.set_father_ptr(father_ptr);
                if (father_ptr.get_child_lt_ptr() == T.get_act_ptr())
                {
                    father_ptr.set_child_lt_ptr(child_ptr);
                }
                else if (father_ptr.get_child_rt_ptr() == T.get_act_ptr())
                {
                    father_ptr.set_child_rt_ptr(child_ptr);
                }
                else if (father_ptr.get_child_lb_ptr() == T.get_act_ptr())
                {
                    father_ptr.set_child_lb_ptr(child_ptr);
                }
                else if (father_ptr.get_child_rb_ptr() == T.get_act_ptr())
                {
                    father_ptr.set_child_rb_ptr(child_ptr);
                }
                else if (DEBUG_BUILD)
                {
                    println("Error delete_degenerated_node");
                }
                T.set_act_ptr(child_ptr);
            }
        }//if2
        else if (!lt_child && !rt_child && lb_child && !rb_child)
        {//if3
            is_degenerated = true;
            delete_ptr = T.get_act_ptr();
            child_ptr = T.get_act_ptr().get_child_lb_ptr();
            if (T.get_act_ptr() == T.get_root_ptr())//special case
            {
                T.set_root_ptr(child_ptr);
                T.set_act_ptr(T.get_root_ptr());
            }
            else//usual case
            {
                father_ptr = T.get_act_ptr().get_father_ptr();
                child_ptr.set_father_ptr(father_ptr);
                if (father_ptr.get_child_lt_ptr() == T.get_act_ptr())
                {
                    father_ptr.set_child_lt_ptr(child_ptr);
                }
                else if (father_ptr.get_child_rt_ptr() == T.get_act_ptr())
                {
                    father_ptr.set_child_rt_ptr(child_ptr);
                }
                else if (father_ptr.get_child_lb_ptr() == T.get_act_ptr())
                {
                    father_ptr.set_child_lb_ptr(child_ptr);
                }
                else if (father_ptr.get_child_rb_ptr() == T.get_act_ptr())
                {
                    father_ptr.set_child_rb_ptr(child_ptr);
                }
                else if (DEBUG_BUILD)
                {
                    println("Error delete_degenerated_node");
                }
                T.set_act_ptr(child_ptr);
            }
        }//if3
        else if (!lt_child && !rt_child && !lb_child && rb_child)
        {//if4
            is_degenerated = true;
            delete_ptr = T.get_act_ptr();
            child_ptr = T.get_act_ptr().get_child_rb_ptr();
            if (T.get_act_ptr() == T.get_root_ptr())//special case
            {
                T.set_root_ptr(child_ptr);
                T.set_act_ptr(T.get_root_ptr());
            }
            else//usual case
            {
                father_ptr = T.get_act_ptr().get_father_ptr();
                child_ptr.set_father_ptr(father_ptr);
                if (father_ptr.get_child_lt_ptr() == T.get_act_ptr())
                {
                    father_ptr.set_child_lt_ptr(child_ptr);
                }
                else if (father_ptr.get_child_rt_ptr() == T.get_act_ptr())
                {
                    father_ptr.set_child_rt_ptr(child_ptr);
                }
                else if (father_ptr.get_child_lb_ptr() == T.get_act_ptr())
                {
                    father_ptr.set_child_lb_ptr(child_ptr);
                }
                else if (father_ptr.get_child_rb_ptr() == T.get_act_ptr())
                {
                    father_ptr.set_child_rb_ptr(child_ptr);
                }
                else if (DEBUG_BUILD)
                {
                    println("Error delete_degenerated_node");
                }
                T.set_act_ptr(child_ptr);
            }
        }//if4
        return is_degenerated;
    }

    void delete_sparse_subtree(QuadTreeNM T, QuadTreeNodeNM new_leaf_ptr)
    {
        collect_contained_nodes(T, new_leaf_ptr);

        if (new_leaf_ptr.child_lt_exists())
        {
            T.delete_tree(new_leaf_ptr.get_child_lt_ptr());
            new_leaf_ptr.set_child_lt_ptr(null);
        }
        if (new_leaf_ptr.child_rt_exists())
        {
            T.delete_tree(new_leaf_ptr.get_child_rt_ptr());
            new_leaf_ptr.set_child_rt_ptr(null);
        }
        if (new_leaf_ptr.child_lb_exists())
        {
            T.delete_tree(new_leaf_ptr.get_child_lb_ptr());
            new_leaf_ptr.set_child_lb_ptr(null);
        }
        if (new_leaf_ptr.child_rb_exists())
        {
            T.delete_tree(new_leaf_ptr.get_child_rb_ptr());
            new_leaf_ptr.set_child_rb_ptr(null);
        }
    }

    void collect_contained_nodes(QuadTreeNM T, QuadTreeNodeNM new_leaf_ptr)
    {
        if (T.get_act_ptr().is_leaf())
        {
            while (!T.get_act_ptr().contained_nodes_empty())
            {
                new_leaf_ptr.pushBack_contained_nodes(T.get_act_ptr().pop_contained_nodes());
            }
        }
        else if (T.get_act_ptr().child_lt_exists())
        {
            T.go_to_lt_child();
            collect_contained_nodes(T, new_leaf_ptr);
            T.go_to_father();
        }
        if (T.get_act_ptr().child_rt_exists())
        {
            T.go_to_rt_child();
            collect_contained_nodes(T, new_leaf_ptr);
            T.go_to_father();
        }
        if (T.get_act_ptr().child_lb_exists())
        {
            T.go_to_lb_child();
            collect_contained_nodes(T, new_leaf_ptr);
            T.go_to_father();
        }
        if (T.get_act_ptr().child_rb_exists())
        {
            T.go_to_rb_child();
            collect_contained_nodes(T, new_leaf_ptr);
            T.go_to_father();
        }
    }

    boolean find_smallest_quad(NodeArray<NodeAttributes> A, QuadTreeNM T)
    {
        assert (!T.get_act_ptr().contained_nodes_empty());
        //if(T.get_act_ptr().contained_nodes_empty())
        //  cout<<"Error NMM :: find_smallest_quad()"<<endl;
        //else
        // {//else
        List<node> L = T.get_act_ptr().get_contained_nodes();
        node v = L.remove(0);
        double x_min = A.get(v).get_x();
        double x_max = x_min;
        double y_min = A.get(v).get_y();
        double y_max = y_min;

        while (!L.isEmpty())
        {
            v = L.remove(0);
            if (A.get(v).get_x() < x_min)
            {
                x_min = A.get(v).get_x();
            }
            if (A.get(v).get_x() > x_max)
            {
                x_max = A.get(v).get_x();
            }
            if (A.get(v).get_y() < y_min)
            {
                y_min = A.get(v).get_y();
            }
            if (A.get(v).get_y() > y_max)
            {
                y_max = A.get(v).get_y();
            }
        }
        if (x_min != x_max || y_min != y_max) //nodes are not all at the same position
        {
            if (find_sm_cell() == FMMMLayout.SmallestCellFinding.scfIteratively)
            {
                find_small_cell_iteratively(T.get_act_ptr(), x_min, x_max, y_min, y_max);
            }
            else //find_sm_cell == FMMMLayout.scfAluru
            {
                find_small_cell_iteratively(T.get_act_ptr(), x_min, x_max, y_min, y_max);
            }
            return true;
        }
        else
        {
            return false;
        }
        //}//else
    }

// ********Functions needed for subtree by subtree  treeruction(END)************
    void find_small_cell_iteratively(
            QuadTreeNodeNM act_ptr,
            double x_min,
            double x_max,
            double y_min,
            double y_max)
    {
        int new_level;
        double new_boxlength;
        DPoint2 new_dlc = new DPoint2();
        boolean Sm_cell_found = false;

        while (!Sm_cell_found && ((x_max - x_min >= MIN_BOX_LENGTH) ||
                (y_max - y_min >= MIN_BOX_LENGTH)))
        {
            if (in_lt_quad(act_ptr, x_min, x_max, y_min, y_max))
            {
                new_level = act_ptr.get_Sm_level() + 1;
                new_boxlength = act_ptr.get_Sm_boxlength() / 2;
                new_dlc.m_x = act_ptr.get_Sm_downleftcorner().m_x;
                new_dlc.m_y = act_ptr.get_Sm_downleftcorner().m_y + new_boxlength;
                act_ptr.set_Sm_level(new_level);
                act_ptr.set_Sm_boxlength(new_boxlength);
                act_ptr.set_Sm_downleftcorner(new_dlc);
            }
            else if (in_rt_quad(act_ptr, x_min, x_max, y_min, y_max))
            {
                new_level = act_ptr.get_Sm_level() + 1;
                new_boxlength = act_ptr.get_Sm_boxlength() / 2;
                new_dlc.m_x = act_ptr.get_Sm_downleftcorner().m_x + new_boxlength;
                new_dlc.m_y = act_ptr.get_Sm_downleftcorner().m_y + new_boxlength;
                act_ptr.set_Sm_level(new_level);
                act_ptr.set_Sm_boxlength(new_boxlength);
                act_ptr.set_Sm_downleftcorner(new_dlc);
            }
            else if (in_lb_quad(act_ptr, x_min, x_max, y_min, y_max))
            {
                new_level = act_ptr.get_Sm_level() + 1;
                new_boxlength = act_ptr.get_Sm_boxlength() / 2;
                act_ptr.set_Sm_level(new_level);
                act_ptr.set_Sm_boxlength(new_boxlength);
            }
            else if (in_rb_quad(act_ptr, x_min, x_max, y_min, y_max))
            {
                new_level = act_ptr.get_Sm_level() + 1;
                new_boxlength = act_ptr.get_Sm_boxlength() / 2;
                new_dlc.m_x = act_ptr.get_Sm_downleftcorner().m_x + new_boxlength;
                new_dlc.m_y = act_ptr.get_Sm_downleftcorner().m_y;
                act_ptr.set_Sm_level(new_level);
                act_ptr.set_Sm_boxlength(new_boxlength);
                act_ptr.set_Sm_downleftcorner(new_dlc);
            }
            else
            {
                Sm_cell_found = true;
            }
        }
    }

    void find_small_cell_by_formula(
            QuadTreeNodeNM act_ptr,
            double x_min,
            double x_max,
            double y_min,
            double y_max)
    {
        int level_offset = act_ptr.get_Sm_level();
        max_power_of_2_index = 30;//up to this level standard integer arithmetic is used
        IPoint2 Sm_position = new IPoint2();
        double Sm_dlc_x_coord, Sm_dlc_y_coord;
        double Sm_boxlength;
        int Sm_level;
        DPoint2 Sm_downleftcorner = new DPoint2();
        int j_x = max_power_of_2_index + 1;
        int j_y = max_power_of_2_index + 1;
        boolean rectangle_is_horizontal_line = false;
        boolean rectangle_is_vertical_line = false;
        boolean rectangle_is_point = false;

        //shift boundaries to the origin for easy calculations
        double x_min_old = x_min;
        double x_max_old = x_max;
        double y_min_old = y_min;
        double y_max_old = y_max;

        Sm_boxlength = act_ptr.get_Sm_boxlength();
        Sm_dlc_x_coord = act_ptr.get_Sm_downleftcorner().m_x;
        Sm_dlc_y_coord = act_ptr.get_Sm_downleftcorner().m_y;

        x_min -= Sm_dlc_x_coord;
        x_max -= Sm_dlc_x_coord;
        y_min -= Sm_dlc_y_coord;
        y_max -= Sm_dlc_y_coord;

        //check if iterative way has to be used
        if (x_min == x_max && y_min == y_max)
        {
            rectangle_is_point = true;
        }
        else if (x_min == x_max && y_min != y_max)
        {
            rectangle_is_vertical_line = true;
        }
        else //x_min != x_max
        {
            j_x = (int) (Math.ceil(Math.log(Sm_boxlength / (x_max - x_min)) / Math.log(2.0)));
        }

        if (x_min != x_max && y_min == y_max)
        {
            rectangle_is_horizontal_line = true;
        }
        else //y_min != y_max
        {
            j_y = (int) (Math.ceil(Math.log(Sm_boxlength / (y_max - y_min)) / Math.log(2.0)));
        }

        if (rectangle_is_point)
        {
            ;//keep the old values
        }
        else if (!numexcept.nearly_equal((x_min_old - x_max_old), (x_min - x_max)) ||
                !numexcept.nearly_equal((y_min_old - y_max_old), (y_min - y_max)) ||
                x_min / Sm_boxlength < MIN_BOX_LENGTH || x_max / Sm_boxlength < MIN_BOX_LENGTH ||
                y_min / Sm_boxlength < MIN_BOX_LENGTH || y_max / Sm_boxlength < MIN_BOX_LENGTH)
        {
            find_small_cell_iteratively(act_ptr, x_min_old, x_max_old, y_min_old, y_max_old);
        }
        else if (((j_x > max_power_of_2_index) && (j_y > max_power_of_2_index)) ||
                ((j_x > max_power_of_2_index) && !rectangle_is_vertical_line) ||
                ((j_y > max_power_of_2_index) && !rectangle_is_horizontal_line))
        {
            find_small_cell_iteratively(act_ptr, x_min_old, x_max_old, y_min_old, y_max_old);
        }
        else //idea of Aluru et al.
        {//else
            int k, a1, a2, A, j_minus_k;
            double h1, h2;
            int Sm_x_level = 0, Sm_y_level = 0;
            int Sm_x_position = 0, Sm_y_position = 0;

            if (x_min != x_max)
            {//if1
                //calculate Sm_x_level and Sm_x_position
                a1 = (int) (Math.ceil((x_min / Sm_boxlength) * power_of_two(j_x)));
                a2 = (int) (Math.floor((x_max / Sm_boxlength) * power_of_two(j_x)));
                h1 = (Sm_boxlength / power_of_two(j_x)) * a1;
                h2 = (Sm_boxlength / power_of_two(j_x)) * a2;

                //special cases: two tangents or left tangent and righ cutline
                if (((h1 == x_min) && (h2 == x_max)) || ((h1 == x_min) && (h2 != x_max)))
                {
                    A = a2;
                }
                else if (a1 == a2)  //only one cutline
                {
                    A = a1;
                }
                else  //two cutlines or a right tangent and a left cutline (usual case)
                {
                    if ((a1 % 2) == 0)
                    {
                        A = a1;
                    }
                    else
                    {
                        A = a2;
                    }
                }

                j_minus_k = (int) ((Math.log(1 + (A ^ (A - 1))) / Math.log(2.0)) - 1);
                k = j_x - j_minus_k;
                Sm_x_level = k - 1;
                Sm_x_position = a1 / power_of_two(j_x - Sm_x_level);
            }//if1

            if (y_min != y_max)
            {//if2
                //calculate Sm_y_level and Sm_y_position
                a1 = (int) (Math.ceil((y_min / Sm_boxlength) * power_of_two(j_y)));
                a2 = (int) (Math.floor((y_max / Sm_boxlength) * power_of_two(j_y)));
                h1 = (Sm_boxlength / power_of_two(j_y)) * a1;
                h2 = (Sm_boxlength / power_of_two(j_y)) * a2;

                //special cases: two tangents or bottom tangent and top cutline
                if (((h1 == y_min) && (h2 == y_max)) || ((h1 == y_min) && (h2 != y_max)))
                {
                    A = a2;
                }
                else if (a1 == a2)  //only one cutline
                {
                    A = a1;
                }
                else  //two cutlines or a top tangent and a bottom cutline (usual case)
                {
                    if ((a1 % 2) == 0)
                    {
                        A = a1;
                    }
                    else
                    {
                        A = a2;
                    }
                }

                j_minus_k = (int) ((Math.log(1 + (A ^ (A - 1))) / Math.log(2.0)) - 1);
                k = j_y - j_minus_k;
                Sm_y_level = k - 1;
                Sm_y_position = a1 / power_of_two(j_y - Sm_y_level);
            }//if2

            if ((x_min != x_max) && (y_min != y_max))//a box with area > 0
            {//if3
                if (Sm_x_level == Sm_y_level)
                {
                    Sm_level = Sm_x_level;
                    Sm_position.m_x = Sm_x_position;
                    Sm_position.m_y = Sm_y_position;
                }
                else if (Sm_x_level < Sm_y_level)
                {
                    Sm_level = Sm_x_level;
                    Sm_position.m_x = Sm_x_position;
                    Sm_position.m_y = Sm_y_position / power_of_two(Sm_y_level - Sm_x_level);
                }
                else //Sm_x_level > Sm_y_level
                {
                    Sm_level = Sm_y_level;
                    Sm_position.m_x = Sm_x_position / power_of_two(Sm_x_level - Sm_y_level);
                    Sm_position.m_y = Sm_y_position;
                }
            }//if3
            else if (x_min == x_max) //a vertical line
            {//if4
                Sm_level = Sm_y_level;
                Sm_position.m_x = (int) (Math.floor((x_min * power_of_two(Sm_level)) /
                        Sm_boxlength));
                Sm_position.m_y = Sm_y_position;
            }//if4
            else //y_min == y_max (a horizontal line)
            {//if5
                Sm_level = Sm_x_level;
                Sm_position.m_x = Sm_x_position;
                Sm_position.m_y = (int) (Math.floor((y_min * power_of_two(Sm_level)) /
                        Sm_boxlength));
            }//if5

            Sm_boxlength = Sm_boxlength / power_of_two(Sm_level);
            Sm_downleftcorner.m_x = Sm_dlc_x_coord + Sm_boxlength * Sm_position.m_x;
            Sm_downleftcorner.m_y = Sm_dlc_y_coord + Sm_boxlength * Sm_position.m_y;
            act_ptr.set_Sm_level(Sm_level + level_offset);
            act_ptr.set_Sm_boxlength(Sm_boxlength);
            act_ptr.set_Sm_downleftcorner(Sm_downleftcorner);
        }//else
    }

    void delete_red_quad_tree_and_count_treenodes(QuadTreeNM T)
    {
        T.delete_tree(T.get_root_ptr());
    }

    void form_multipole_expansions(
            NodeArray<NodeAttributes> A,
            QuadTreeNM T,
            List<QuadTreeNodeNM> quad_tree_leaves)
    {
        T.set_act_ptr(T.get_root_ptr());
        form_multipole_expansion_of_subtree(A, T, quad_tree_leaves);
    }

    void form_multipole_expansion_of_subtree(
            NodeArray<NodeAttributes> A,
            QuadTreeNM T,
            List<QuadTreeNodeNM> quad_tree_leaves)
    {
        init_expansion_Lists(T.get_act_ptr());
        set_center(T.get_act_ptr());

        if (T.get_act_ptr().is_leaf()) //form expansions for leaf nodes
        {//if
            quad_tree_leaves.add(T.get_act_ptr());
            form_multipole_expansion_of_leaf_node(A, T.get_act_ptr());
        }//if
        else //rekursive calls and add shifted expansions
        {//else
            if (T.get_act_ptr().child_lt_exists())
            {
                T.go_to_lt_child();
                form_multipole_expansion_of_subtree(A, T, quad_tree_leaves);
                add_shifted_expansion_to_father_expansion(T.get_act_ptr());
                T.go_to_father();
            }
            if (T.get_act_ptr().child_rt_exists())
            {
                T.go_to_rt_child();
                form_multipole_expansion_of_subtree(A, T, quad_tree_leaves);
                add_shifted_expansion_to_father_expansion(T.get_act_ptr());
                T.go_to_father();
            }
            if (T.get_act_ptr().child_lb_exists())
            {
                T.go_to_lb_child();
                form_multipole_expansion_of_subtree(A, T, quad_tree_leaves);
                add_shifted_expansion_to_father_expansion(T.get_act_ptr());
                T.go_to_father();
            }
            if (T.get_act_ptr().child_rb_exists())
            {
                T.go_to_rb_child();
                form_multipole_expansion_of_subtree(A, T, quad_tree_leaves);
                add_shifted_expansion_to_father_expansion(T.get_act_ptr());
                T.go_to_father();
            }
        }//else
    }

    void init_expansion_Lists(QuadTreeNodeNM act_ptr)
    {
        Complex[] nulList = new Complex[precision() + 1];

        for (int i = 0; i < nulList.length; i++)
        {
            nulList[i] = new Complex();
        }

        act_ptr.set_multipole_exp(nulList, precision());
        act_ptr.set_locale_exp(nulList, precision());
    }

    void set_center(QuadTreeNodeNM act_ptr)
    {
        DPoint2 Sm_downleftcorner = act_ptr.get_Sm_downleftcorner();
        double Sm_boxlength = act_ptr.get_Sm_boxlength();
        double boxcenter_x_coord, boxcenter_y_coord;
        DPoint2 Sm_dlc;
        double rand_y;

        boxcenter_x_coord = Sm_downleftcorner.m_x + Sm_boxlength * 0.5;
        boxcenter_y_coord = Sm_downleftcorner.m_y + Sm_boxlength * 0.5;

        //for use of complex logarithm: waggle the y-coordinates a little bit
        //such that the new center is really inside the actual box and near the exact center
        rand_y = random.nextDouble();//rand number in (0,1)
        boxcenter_y_coord = boxcenter_y_coord + 0.001 * Sm_boxlength * rand_y;

        Complex boxcenter = new Complex(boxcenter_x_coord, boxcenter_y_coord);
        act_ptr.set_Sm_center(boxcenter);
    }

    void form_multipole_expansion_of_leaf_node(
            NodeArray<NodeAttributes> A,
            QuadTreeNodeNM act_ptr)
    {
        int k;
        Complex Q = new Complex(0, 0);
        Complex z_0 = act_ptr.get_Sm_center();//center of actual box
        Complex[] coef = new Complex[precision() + 1];
        Complex z_v_minus_z_0_over_k;
        List<node> nodes_in_box;
        int i;

        nodes_in_box = act_ptr.get_contained_nodes();

        for (node v_it : nodes_in_box)
        {
            Q.plus(new Complex(1.0));
        }

        coef[0] = Q;

        for (i = 1; i <= precision(); i++)
        {
            coef[i] = new Complex();
        }

        for (node v_it : nodes_in_box)
        {
            Complex z_v = new Complex(A.get(v_it).get_x(), A.get(v_it).get_y());
            z_v_minus_z_0_over_k = z_v.minus(z_0);
            for (k = 1; k <= precision(); k++)
            {
                coef[k] = coef[k].plus(z_v_minus_z_0_over_k.multipliedBy(-1.0).dividedBy(k));
                z_v_minus_z_0_over_k = z_v_minus_z_0_over_k.multipliedBy(z_v.minus(z_0));
            }
        }
        act_ptr.replace_multipole_exp(coef, precision());
    }

    void add_shifted_expansion_to_father_expansion(QuadTreeNodeNM act_ptr)
    {
        QuadTreeNodeNM father_ptr = act_ptr.get_father_ptr();
        Complex sum;
        Complex z_0, z_1;
        Complex[] z_0_minus_z_1_over = new Complex[precision() + 1];

        z_1 = father_ptr.get_Sm_center();
        z_0 = act_ptr.get_Sm_center();
        father_ptr.get_multipole_exp()[0] = act_ptr.get_multipole_exp()[0].plus(act_ptr.get_multipole_exp()[0]);

        //init z_0_minus_z_1_over
        z_0_minus_z_1_over[0] = new Complex(1.0);

        for (int i = 1; i <= precision(); i++)
        {
            z_0_minus_z_1_over[i] = z_0_minus_z_1_over[i - 1].multipliedBy(z_0.minus(z_1));
        }

        for (int k = 1; k <= precision(); k++)
        {
            sum = (act_ptr.get_multipole_exp()[0].multipliedBy(-1.0)).multipliedBy(z_0_minus_z_1_over[k]).dividedBy(k);

            for (int s = 1; s <= k; s++)
            {
                sum = sum.plus(act_ptr.get_multipole_exp()[s].multipliedBy(z_0_minus_z_1_over[k - s]).multipliedBy(binko(k - 1, s - 1)));
            }
            father_ptr.get_multipole_exp()[k] = father_ptr.get_multipole_exp()[k].plus(sum);
        }
    }

    void calculate_local_expansions_and_WSPRLS(
            NodeArray<NodeAttributes> A,
            QuadTreeNodeNM act_node_ptr)
    {
        List<QuadTreeNodeNM> I, L, L2, E, D1, D2, M;
        QuadTreeNodeNM father_ptr = null, selected_node_ptr;

        //Step 0: Initializations
        if (!act_node_ptr.is_root())
        {
            father_ptr = act_node_ptr.get_father_ptr();
        }

        I = new ArrayList<QuadTreeNodeNM>();
        L = new ArrayList<QuadTreeNodeNM>();
        L2 = new ArrayList<QuadTreeNodeNM>();
        E = new ArrayList<QuadTreeNodeNM>();
        D1 = new ArrayList<QuadTreeNodeNM>();
        D2 = new ArrayList<QuadTreeNodeNM>();
        M = new ArrayList<QuadTreeNodeNM>();

        //Step 1: calculate Lists I (min. ill sep. set), L (interaction List of well sep.
        //nodes , they are used to form the Local Expansions from the multipole expansions),
        //L2 (non bordering leaves that have a larger or equal Sm-cell and  are ill separated;
        //empty if the actual node is a leaf)
        //calculate List D1(bordering leaves that have a larger or equal Sm-cell and are
        //ill separated) and D2 (non bordering leaves that have a larger or equal Sm-cell and
        //are ill separated;empty if the actual node is an interior node)

        //special case: act_node is the root of T
        if (act_node_ptr.is_root())
        {//if
            E.clear();
            if (act_node_ptr.child_lt_exists())
            {
                E.add(act_node_ptr.get_child_lt_ptr());
            }
            if (act_node_ptr.child_rt_exists())
            {
                E.add(act_node_ptr.get_child_rt_ptr());
            }
            if (act_node_ptr.child_lb_exists())
            {
                E.add(act_node_ptr.get_child_lb_ptr());
            }
            if (act_node_ptr.child_rb_exists())
            {
                E.add(act_node_ptr.get_child_rb_ptr());
            }
        }//if
        //usual case: act_node is an interior node of T
        else
        {
            E = father_ptr.get_D1(); //bordering leaves of father
            I = father_ptr.get_I();  //min ill sep. nodes of father

            for (QuadTreeNodeNM ptr_it : I)
            {
                E.add(ptr_it);
            }
            I.clear();
        }


        while (!E.isEmpty())
        {//while
            selected_node_ptr = E.remove(0);
            if (well_separated(act_node_ptr, selected_node_ptr))
            {
                L.add(selected_node_ptr);
            }
            else if (act_node_ptr.get_Sm_level() < selected_node_ptr.get_Sm_level())
            {
                I.add(selected_node_ptr);
            }
            else if (!selected_node_ptr.is_leaf())
            {
                if (selected_node_ptr.child_lt_exists())
                {
                    E.add(selected_node_ptr.get_child_lt_ptr());
                }
                if (selected_node_ptr.child_rt_exists())
                {
                    E.add(selected_node_ptr.get_child_rt_ptr());
                }
                if (selected_node_ptr.child_lb_exists())
                {
                    E.add(selected_node_ptr.get_child_lb_ptr());
                }
                if (selected_node_ptr.child_rb_exists())
                {
                    E.add(selected_node_ptr.get_child_rb_ptr());
                }
            }
            else if (bordering(act_node_ptr, selected_node_ptr))
            {
                D1.add(selected_node_ptr);
            }
            else if ((selected_node_ptr != act_node_ptr) && (act_node_ptr.is_leaf()))
            {
                D2.add(selected_node_ptr); //direct calculation (no errors produced)
            }
            else if ((selected_node_ptr != act_node_ptr) && !(act_node_ptr.is_leaf()))
            {
                L2.add(selected_node_ptr);
            }
        }//while

        act_node_ptr.set_I(I);
        act_node_ptr.set_D1(D1);
        act_node_ptr.set_D2(D2);

        //Step 2: add local expansions from father(act_node_ptr) and calculate locale
        //expansions for all nodes in L
        if (!act_node_ptr.is_root())
        {
            add_shifted_local_exp_of_parent(act_node_ptr);
        }

        for (QuadTreeNodeNM ptr_it : L)
        {
            add_local_expansion(ptr_it, act_node_ptr);
        }

        //Step 3: calculate locale expansions for all nodes in D2 (simpler than in Step 2)

        for (QuadTreeNodeNM ptr_it : L2)
        {
            add_local_expansion_of_leaf(A, ptr_it, act_node_ptr);
        }

        //Step 4: recursive calls if act_node is not a leaf
        if (!act_node_ptr.is_leaf())
        {
            if (act_node_ptr.child_lt_exists())
            {
                calculate_local_expansions_and_WSPRLS(A, act_node_ptr.get_child_lt_ptr());
            }
            if (act_node_ptr.child_rt_exists())
            {
                calculate_local_expansions_and_WSPRLS(A, act_node_ptr.get_child_rt_ptr());
            }
            if (act_node_ptr.child_lb_exists())
            {
                calculate_local_expansions_and_WSPRLS(A, act_node_ptr.get_child_lb_ptr());
            }
            if (act_node_ptr.child_rb_exists())
            {
                calculate_local_expansions_and_WSPRLS(A, act_node_ptr.get_child_rb_ptr());
            }
        }
        //Step 5: WSPRLS(Well Separateness Preserving Refinement of leaf surroundings)
        //if act_node is a leaf than calculate the list D1,D2 and M from I and D1
        else // *act_node_ptr is a leaf
        {//else
            D1 = act_node_ptr.get_D1();
            D2 = act_node_ptr.get_D2();

            while (!I.isEmpty())
            {//while
                selected_node_ptr = I.remove(0);
                if (selected_node_ptr.is_leaf())
                {
                    //here D1 contains larger AND smaller bordering leaves!
                    if (bordering(act_node_ptr, selected_node_ptr))
                    {
                        D1.add(selected_node_ptr);
                    }
                    else
                    {
                        D2.add(selected_node_ptr);
                    }
                }
                else //!selected_node_ptr.is_leaf()
                {
                    if (bordering(act_node_ptr, selected_node_ptr))
                    {
                        if (selected_node_ptr.child_lt_exists())
                        {
                            I.add(selected_node_ptr.get_child_lt_ptr());
                        }
                        if (selected_node_ptr.child_rt_exists())
                        {
                            I.add(selected_node_ptr.get_child_rt_ptr());
                        }
                        if (selected_node_ptr.child_lb_exists())
                        {
                            I.add(selected_node_ptr.get_child_lb_ptr());
                        }
                        if (selected_node_ptr.child_rb_exists())
                        {
                            I.add(selected_node_ptr.get_child_rb_ptr());
                        }
                    }
                    else
                    {
                        M.add(selected_node_ptr);
                    }
                }
            }//while
            act_node_ptr.set_D1(D1);
            act_node_ptr.set_D2(D2);
            act_node_ptr.set_M(M);
        }//else
    }

    boolean well_separated(QuadTreeNodeNM node_1_ptr, QuadTreeNodeNM node_2_ptr)
    {
        double boxlength_1 = node_1_ptr.get_Sm_boxlength();
        double boxlength_2 = node_2_ptr.get_Sm_boxlength();
        double x1_min, x1_max, y1_min, y1_max, x2_min, x2_max, y2_min, y2_max;
        boolean x_overlap, y_overlap;

        if (boxlength_1 <= boxlength_2)
        {
            x1_min = node_1_ptr.get_Sm_downleftcorner().m_x;
            x1_max = node_1_ptr.get_Sm_downleftcorner().m_x + boxlength_1;
            y1_min = node_1_ptr.get_Sm_downleftcorner().m_y;
            y1_max = node_1_ptr.get_Sm_downleftcorner().m_y + boxlength_1;

            //blow the box up
            x2_min = node_2_ptr.get_Sm_downleftcorner().m_x - boxlength_2;
            x2_max = node_2_ptr.get_Sm_downleftcorner().m_x + 2 * boxlength_2;
            y2_min = node_2_ptr.get_Sm_downleftcorner().m_y - boxlength_2;
            y2_max = node_2_ptr.get_Sm_downleftcorner().m_y + 2 * boxlength_2;
        }
        else //boxlength_1 > boxlength_2
        {
            //blow the box up
            x1_min = node_1_ptr.get_Sm_downleftcorner().m_x - boxlength_1;
            x1_max = node_1_ptr.get_Sm_downleftcorner().m_x + 2 * boxlength_1;
            y1_min = node_1_ptr.get_Sm_downleftcorner().m_y - boxlength_1;
            y1_max = node_1_ptr.get_Sm_downleftcorner().m_y + 2 * boxlength_1;

            x2_min = node_2_ptr.get_Sm_downleftcorner().m_x;
            x2_max = node_2_ptr.get_Sm_downleftcorner().m_x + boxlength_2;
            y2_min = node_2_ptr.get_Sm_downleftcorner().m_y;
            y2_max = node_2_ptr.get_Sm_downleftcorner().m_y + boxlength_2;
        }

        //test if boxes overlap
        if ((x1_max <= x2_min) || numexcept.nearly_equal(x1_max, x2_min) ||
                (x2_max <= x1_min) || numexcept.nearly_equal(x2_max, x1_min))
        {
            x_overlap = false;
        }
        else
        {
            x_overlap = true;
        }
        if ((y1_max <= y2_min) || numexcept.nearly_equal(y1_max, y2_min) ||
                (y2_max <= y1_min) || numexcept.nearly_equal(y2_max, y1_min))
        {
            y_overlap = false;
        }
        else
        {
            y_overlap = true;
        }

        if (x_overlap && y_overlap)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    boolean bordering(QuadTreeNodeNM node_1_ptr, QuadTreeNodeNM node_2_ptr)
    {
        double boxlength_1 = node_1_ptr.get_Sm_boxlength();
        double boxlength_2 = node_2_ptr.get_Sm_boxlength();
        double x1_min = node_1_ptr.get_Sm_downleftcorner().m_x;
        double x1_max = node_1_ptr.get_Sm_downleftcorner().m_x + boxlength_1;
        double y1_min = node_1_ptr.get_Sm_downleftcorner().m_y;
        double y1_max = node_1_ptr.get_Sm_downleftcorner().m_y + boxlength_1;
        double x2_min = node_2_ptr.get_Sm_downleftcorner().m_x;
        double x2_max = node_2_ptr.get_Sm_downleftcorner().m_x + boxlength_2;
        double y2_min = node_2_ptr.get_Sm_downleftcorner().m_y;
        double y2_max = node_2_ptr.get_Sm_downleftcorner().m_y + boxlength_2;

        if (((x2_min <= x1_min || numexcept.nearly_equal(x2_min, x1_min)) &&
                (x1_max <= x2_max || numexcept.nearly_equal(x1_max, x2_max)) &&
                (y2_min <= y1_min || numexcept.nearly_equal(y2_min, y1_min)) &&
                (y1_max <= y2_max || numexcept.nearly_equal(y1_max, y2_max))) ||
                ((x1_min <= x2_min || numexcept.nearly_equal(x1_min, x2_min)) &&
                (x2_max <= x1_max || numexcept.nearly_equal(x2_max, x1_max)) &&
                (y1_min <= y2_min || numexcept.nearly_equal(y1_min, y2_min)) &&
                (y2_max <= y1_max || numexcept.nearly_equal(y2_max, y1_max))))
        {
            return false; //one box contains the other box(inclusive neighbours)
        }
        else
        {//else
            if (boxlength_1 <= boxlength_2)
            { //shift box1
                if (x1_min < x2_min)
                {
                    x1_min += boxlength_1;
                    x1_max += boxlength_1;
                }
                else if (x1_max > x2_max)
                {
                    x1_min -= boxlength_1;
                    x1_max -= boxlength_1;
                }
                if (y1_min < y2_min)
                {
                    y1_min += boxlength_1;
                    y1_max += boxlength_1;
                }
                else if (y1_max > y2_max)
                {
                    y1_min -= boxlength_1;
                    y1_max -= boxlength_1;
                }
            }
            else //boxlength_1 > boxlength_2
            {//shift box2
                if (x2_min < x1_min)
                {
                    x2_min += boxlength_2;
                    x2_max += boxlength_2;
                }
                else if (x2_max > x1_max)
                {
                    x2_min -= boxlength_2;
                    x2_max -= boxlength_2;
                }
                if (y2_min < y1_min)
                {
                    y2_min += boxlength_2;
                    y2_max += boxlength_2;
                }
                else if (y2_max > y1_max)
                {
                    y2_min -= boxlength_2;
                    y2_max -= boxlength_2;
                }
            }
            if (((x2_min <= x1_min || numexcept.nearly_equal(x2_min, x1_min)) &&
                    (x1_max <= x2_max || numexcept.nearly_equal(x1_max, x2_max)) &&
                    (y2_min <= y1_min || numexcept.nearly_equal(y2_min, y1_min)) &&
                    (y1_max <= y2_max || numexcept.nearly_equal(y1_max, y2_max))) ||
                    ((x1_min <= x2_min || numexcept.nearly_equal(x1_min, x2_min)) &&
                    (x2_max <= x1_max || numexcept.nearly_equal(x2_max, x1_max)) &&
                    (y1_min <= y2_min || numexcept.nearly_equal(y1_min, y2_min)) &&
                    (y2_max <= y1_max || numexcept.nearly_equal(y2_max, y1_max))))
            {
                return true;
            }
            else
            {
                return false;
            }
        }//else
    }

    void add_shifted_local_exp_of_parent(QuadTreeNodeNM node_ptr)
    {
        QuadTreeNodeNM father_ptr = node_ptr.get_father_ptr();

        Complex z_0 = father_ptr.get_Sm_center();
        Complex z_1 = node_ptr.get_Sm_center();
        Complex[] z_1_minus_z_0_over = new Complex[precision() + 1];

        //init z_1_minus_z_0_over
        z_1_minus_z_0_over[0] = new Complex(1.0);
        for (int i = 1; i <= precision(); i++)
        {
            z_1_minus_z_0_over[i] = z_1_minus_z_0_over[i - 1].multipliedBy(z_1.minus(z_0));
        }


        for (int l = 0; l <= precision(); l++)
        {
            Complex sum = new Complex(0, 0);
            for (int k = l; k <= precision(); k++)
            {
                sum = sum.plus(father_ptr.get_local_exp()[k].multipliedBy(binko(k, l)).multipliedBy(z_1_minus_z_0_over[k - l]));
            }

            node_ptr.get_local_exp()[l] = node_ptr.get_local_exp()[l].plus(sum);
        }
    }

    void add_local_expansion(QuadTreeNodeNM ptr_0, QuadTreeNodeNM ptr_1)
    {
        Complex z_0 = ptr_0.get_Sm_center();
        Complex z_1 = ptr_1.get_Sm_center();
        Complex sum, z_error;
        Complex factor;
        Complex z_1_minus_z_0_over_k;
        Complex z_1_minus_z_0_over_s;
        Complex pow_minus_1_s_plus_1;
        Complex pow_minus_1_s;

        //Error-Handling for complex logarithm
        if ((z_1.minus(z_0).r() <= 0) && (z_1.minus(z_0).i() == 0)) //no cont. compl. log fct exists !!!
        {
            z_error = z_1.minus(z_0).plus(0.0000001).log();
            sum = ptr_0.get_multipole_exp()[0].multipliedBy(z_error);
        }
        else
        {
            sum = ptr_0.get_multipole_exp()[0].multipliedBy(z_1.minus(z_0).log());
        }


        z_1_minus_z_0_over_k = z_1.minus(z_0);
        for (int k = 1; k <= precision(); k++)
        {
            sum = sum.plus(ptr_0.get_multipole_exp()[k].dividedBy(z_1_minus_z_0_over_k));
            z_1_minus_z_0_over_k = z_1_minus_z_0_over_k.multipliedBy(z_1.minus(z_0));
        }
        ptr_1.get_local_exp()[0] = ptr_1.get_local_exp()[0].plus(sum);

        z_1_minus_z_0_over_s = z_1.minus(z_0);
        for (int s = 1; s <= precision(); s++)
        {
            pow_minus_1_s_plus_1 = new Complex(((s + 1) % 2 == 0) ? 1.0 : -1.0);
            pow_minus_1_s = new Complex((pow_minus_1_s_plus_1.r() == 1.0) ? -1 : 1);
            sum = pow_minus_1_s_plus_1.multipliedBy(ptr_0.get_multipole_exp()[0]).dividedBy(
                    (z_1_minus_z_0_over_s.multipliedBy(s)));
            factor = pow_minus_1_s.dividedBy(z_1_minus_z_0_over_s);
            z_1_minus_z_0_over_s = z_1_minus_z_0_over_s.multipliedBy(z_1.minus(z_0));
            Complex sum_2 = new Complex(0, 0);

            z_1_minus_z_0_over_k = z_1.minus(z_0);
            for (int k = 1; k <= precision(); k++)
            {
                sum_2 = sum_2.plus(ptr_0.get_multipole_exp()[k].multipliedBy(
                        binko(s + k - 1, k - 1)).dividedBy(z_1_minus_z_0_over_k));
                z_1_minus_z_0_over_k = z_1_minus_z_0_over_k.multipliedBy(z_1.minus(z_0));
            }
            ptr_1.get_local_exp()[s] = ptr_1.get_local_exp()[s].plus(sum.plus(factor.multipliedBy(sum_2)));
        }
    }

    void add_local_expansion_of_leaf(
            NodeArray<NodeAttributes> A,
            QuadTreeNodeNM ptr_0,
            QuadTreeNodeNM ptr_1)
    {
        List<node> contained_nodes;
        double multipole_0_of_v = 1;//only the first coefficient is not zero
        Complex z_1 = ptr_1.get_Sm_center();
        Complex z_error;
        Complex z_1_minus_z_0_over_s;
        Complex pow_minus_1_s_plus_1;

        contained_nodes = ptr_0.get_contained_nodes();

        for (node v_it : contained_nodes)
        {//forall
            //set position of v as center ( (1,0,....,0) are the multipole coefficients at v)
            Complex z_0 = new Complex(A.get(v_it).get_x(), A.get(v_it).get_y());

            //now transform multipole_0_of_v to the locale expansion around z_1

            //Error-Handling for complex logarithm
            if (z_1.minus(z_0).r() <= 0 && z_1.minus(z_0).i() == 0) //no cont. compl. log fct exists!
            {
                z_error = z_1.minus(z_0).plus(0.0000001).log();
                ptr_1.get_local_exp()[0] = ptr_1.get_local_exp()[0].plus(z_error.multipliedBy(multipole_0_of_v));
            }
            else
            {
                ptr_1.get_local_exp()[0] = ptr_1.get_local_exp()[0].plus(
                        z_1.minus(z_0).log()).multipliedBy(multipole_0_of_v);
            }

            z_1_minus_z_0_over_s = z_1.minus(z_0);
            for (int s = 1; s <= precision(); s++)
            {
                pow_minus_1_s_plus_1 = new Complex(((s + 1) % 2 == 0) ? 1.0 : -1.0);
                ptr_1.get_local_exp()[s] = ptr_1.get_local_exp()[s].plus(
                        pow_minus_1_s_plus_1.multipliedBy(multipole_0_of_v).dividedBy(
                        z_1_minus_z_0_over_s.multipliedBy(s)));
                z_1_minus_z_0_over_s = z_1_minus_z_0_over_s.plus(z_1.minus(z_0));
            }
        }//forall
    }

    void transform_local_exp_to_forces(
            NodeArray<NodeAttributes> A,
            List<QuadTreeNodeNM> quad_tree_leaves,
            NodeArray<DPoint2> F_local_exp)
    {
        List<node> contained_nodes;
        Complex sum;
        Complex z_0;
        Complex z_v_minus_z_0_over_k_minus_1;
        DPoint2 force_vector = new DPoint2();

        //calculate derivative of the potential polynom (= local expansion at leaf nodes)
        //and evaluate it for each node in contained_nodes()
        //and transform the complex number back to the real-world, to obtain the force

        for (QuadTreeNodeNM leaf_ptr_ptr : quad_tree_leaves)
        {
            contained_nodes = leaf_ptr_ptr.get_contained_nodes();
            z_0 = leaf_ptr_ptr.get_Sm_center();

            for (node v_ptr : contained_nodes)
            {
                Complex z_v = new Complex(A.get(v_ptr).get_x(), A.get(v_ptr).get_y());
                sum = new Complex(0.0, 0.0);
                z_v_minus_z_0_over_k_minus_1 = new Complex(1.0);
                for (int k = 1; k <= precision(); k++)
                {
                    sum = sum.plus(leaf_ptr_ptr.get_local_exp()[k].multipliedBy(k).multipliedBy(
                            z_v_minus_z_0_over_k_minus_1));
                    z_v_minus_z_0_over_k_minus_1 = z_v_minus_z_0_over_k_minus_1.multipliedBy(z_v.minus(z_0));
                }
                force_vector.m_x = sum.r();
                force_vector.m_y = (-1.0) * sum.i();
                F_local_exp.set(v_ptr, force_vector);
            }
        }
    }

    void transform_multipole_exp_to_forces(
            NodeArray<NodeAttributes> A,
            List<QuadTreeNodeNM> quad_tree_leaves,
            NodeArray<DPoint2> F_multipole_exp)
    {
        List<QuadTreeNodeNM> M;
        List<node> act_contained_nodes;
        Complex sum;
        Complex z_0;
        Complex z_v_minus_z_0_over_minus_k_minus_1;
        DPoint2 force_vector = new DPoint2();

        //for each leaf u in the M-List of an actual leaf v do:
        //calculate derivative of the multipole expansion function at u
        //and evaluate it for each node in v.get_contained_nodes()
        //and transform the complex number back to the real-world, to obtain the force

        for (QuadTreeNodeNM act_leaf_ptr_ptr : quad_tree_leaves)
        {
            act_contained_nodes = act_leaf_ptr_ptr.get_contained_nodes();
            M = act_leaf_ptr_ptr.get_M();
            for (QuadTreeNodeNM M_node_ptr_ptr : M)
            {
                z_0 = M_node_ptr_ptr.get_Sm_center();
                for (node v_ptr : act_contained_nodes)
                {
                    Complex z_v = new Complex(A.get(v_ptr).get_x(), A.get(v_ptr).get_y());
                    z_v_minus_z_0_over_minus_k_minus_1 = new Complex(1.0).dividedBy(z_v.minus(z_0));
                    sum = M_node_ptr_ptr.get_multipole_exp()[0].multipliedBy(
                            z_v_minus_z_0_over_minus_k_minus_1);

                    for (int k = 1; k <= precision(); k++)
                    {
                        z_v_minus_z_0_over_minus_k_minus_1 =
                                z_v_minus_z_0_over_minus_k_minus_1.dividedBy(z_v.minus(z_0));
                        sum = sum.minus(M_node_ptr_ptr.get_multipole_exp()[k].multipliedBy(k).multipliedBy(
                                z_v_minus_z_0_over_minus_k_minus_1));
                    }
                    force_vector.m_x = sum.r();
                    force_vector.m_y = (-1.0) * sum.i();
                    F_multipole_exp.set(v_ptr, F_multipole_exp.get(v_ptr).plus(force_vector));

                }
            }
        }
    }


    void calculate_neighbourcell_forces(
            NodeArray<NodeAttributes> A,
            List<QuadTreeNodeNM> quad_tree_leaves,
            NodeArray<DPoint2> F_direct)
    {
        List<node> act_contained_nodes, neighbour_contained_nodes, non_neighbour_contained_nodes;
        List<QuadTreeNodeNM> neighboured_leaves;
        List<QuadTreeNodeNM> non_neighboured_leaves;
        double act_leaf_boxlength, neighbour_leaf_boxlength;
        DPoint2 act_leaf_dlc, neighbour_leaf_dlc;
        DPoint2 f_rep_u_on_v = new DPoint2();
        DPoint2 vector_v_minus_u;
        DPoint2 pos_u, pos_v;
        double norm_v_minus_u, scalar;
        int length;
        node u, v;

        for (QuadTreeNodeNM act_leaf_ptr : quad_tree_leaves)
        {//forall
            act_contained_nodes = act_leaf_ptr.get_contained_nodes();

            if (act_contained_nodes.size() <= particles_in_leaves())
            {//if (usual case)

                //Step1:calculate forces inside act_contained_nodes

                length = act_contained_nodes.size();
                node[] numbered_nodes = new node[length + 1];
                int k = 1;
                for (node v_ptr : act_contained_nodes)
                {
                    numbered_nodes[k] = v_ptr;
                    k++;
                }

                for (k = 1; k < length; k++)
                {
                    for (int l = k + 1; l <= length; l++)
                    {
                        u = numbered_nodes[k];
                        v = numbered_nodes[l];
                        pos_u = A.get(u).get_position();
                        pos_v = A.get(v).get_position();
                        if (pos_u == pos_v)
                        {//if2  (Exception handling if two nodes have the same position)
                            pos_u = numexcept.choose_distinct_random_point_in_radius_epsilon(pos_u);
                        }//if2
                        vector_v_minus_u = pos_v.minus(pos_u);
                        norm_v_minus_u = vector_v_minus_u.norm();
                        if (!numexcept.f_rep_near_machine_precision(norm_v_minus_u, f_rep_u_on_v))
                        {
                            scalar = f_rep_scalar(norm_v_minus_u) / norm_v_minus_u;
                            f_rep_u_on_v.m_x = scalar * vector_v_minus_u.m_x;
                            f_rep_u_on_v.m_y = scalar * vector_v_minus_u.m_y;
                        }
                        F_direct.set(v, F_direct.get(v).plus(f_rep_u_on_v));
                        F_direct.set(u, F_direct.get(u).minus(f_rep_u_on_v));
                    }
                }

                //Step 2: calculated forces to nodes in act_contained_nodes() of
                //leaf_ptr.get_D1()

                neighboured_leaves = act_leaf_ptr.get_D1();
                act_leaf_boxlength = act_leaf_ptr.get_Sm_boxlength();
                act_leaf_dlc = act_leaf_ptr.get_Sm_downleftcorner();

                for (QuadTreeNodeNM neighbour_leaf_ptr : neighboured_leaves)
                {//forall2
                    //forget boxes that have already been looked at

                    neighbour_leaf_boxlength = neighbour_leaf_ptr.get_Sm_boxlength();
                    neighbour_leaf_dlc = neighbour_leaf_ptr.get_Sm_downleftcorner();

                    if ((act_leaf_boxlength > neighbour_leaf_boxlength) ||
                            (act_leaf_boxlength == neighbour_leaf_boxlength &&
                            act_leaf_dlc.m_x < neighbour_leaf_dlc.m_x) ||
                             (act_leaf_boxlength == neighbour_leaf_boxlength &&
                            act_leaf_dlc.m_x == neighbour_leaf_dlc.m_x &&
                            act_leaf_dlc.m_y < neighbour_leaf_dlc.m_y))
                    {//if
                        neighbour_contained_nodes = neighbour_leaf_ptr.get_contained_nodes();
                        for (node v_ptr : act_contained_nodes)
                        {
                            for (node u_ptr : neighbour_contained_nodes)
                            {//for
                                pos_u = A.get(u_ptr).get_position();
                                pos_v = A.get(v_ptr).get_position();
                                if (pos_u == pos_v)
                                {//if2  (Exception handling if two nodes have the same position)
                                    pos_u = numexcept.choose_distinct_random_point_in_radius_epsilon(pos_u);
                                }//if2
                                vector_v_minus_u = pos_v.minus(pos_u);
                                norm_v_minus_u = vector_v_minus_u.norm();
                                if (!numexcept.f_rep_near_machine_precision(norm_v_minus_u, f_rep_u_on_v))
                                {
                                    scalar = f_rep_scalar(norm_v_minus_u) / norm_v_minus_u;
                                    f_rep_u_on_v.m_x = scalar * vector_v_minus_u.m_x;
                                    f_rep_u_on_v.m_y = scalar * vector_v_minus_u.m_y;
                                }
                                F_direct.set(v_ptr, F_direct.get(v_ptr).plus(f_rep_u_on_v));
                                F_direct.set(u_ptr, F_direct.get(u_ptr).minus(f_rep_u_on_v));
                            }//for
                        }
                    }//if
                }//forall2

                //Step 3: calculated forces to nodes in act_contained_nodes() of
                //leaf_ptr.get_D2()

                non_neighboured_leaves = act_leaf_ptr.get_D2();
                for (QuadTreeNodeNM non_neighbour_leaf_ptr : non_neighboured_leaves)
                {//forall3
                    non_neighbour_contained_nodes = non_neighbour_leaf_ptr.get_contained_nodes();
                    for (node v_ptr : act_contained_nodes)
                    {
                        for (node u_ptr : non_neighbour_contained_nodes)
                        {//for
                            pos_u = A.get(u_ptr).get_position();
                            pos_v = A.get(v_ptr).get_position();
                            if (pos_u == pos_v)
                            {//if2  (Exception handling if two nodes have the same position)
                                pos_u = numexcept.choose_distinct_random_point_in_radius_epsilon(pos_u);
                            }//if2
                            vector_v_minus_u = pos_v.minus(pos_u);
                            norm_v_minus_u = vector_v_minus_u.norm();
                            if (!numexcept.f_rep_near_machine_precision(norm_v_minus_u, f_rep_u_on_v))
                            {
                                scalar = f_rep_scalar(norm_v_minus_u) / norm_v_minus_u;
                                f_rep_u_on_v.m_x = scalar * vector_v_minus_u.m_x;
                                f_rep_u_on_v.m_y = scalar * vector_v_minus_u.m_y;
                            }
                            F_direct.set(v_ptr, F_direct.get(v_ptr).plus(f_rep_u_on_v));
                        }//for
                    }
                }//forall3
            }//if(usual case)
            else //special case (more then particles_in_leaves() particles in this leaf)
            {//else
                for (node v_ptr : act_contained_nodes)
                {
                    pos_v = A.get(v_ptr).get_position();
                    pos_u = numexcept.choose_distinct_random_point_in_radius_epsilon(pos_v);
                    vector_v_minus_u = pos_v.minus(pos_u);
                    norm_v_minus_u = vector_v_minus_u.norm();
                    if (!numexcept.f_rep_near_machine_precision(norm_v_minus_u, f_rep_u_on_v))
                    {
                        scalar = f_rep_scalar(norm_v_minus_u) / norm_v_minus_u;
                        f_rep_u_on_v.m_x = scalar * vector_v_minus_u.m_x;
                        f_rep_u_on_v.m_y = scalar * vector_v_minus_u.m_y;
                    }
                    F_direct.set(v_ptr, F_direct.get(v_ptr).plus(f_rep_u_on_v));
                }
            }//else
        }//forall
    }

    void add_rep_forces(
            Graph G,
            NodeArray<DPoint2> F_direct,
            NodeArray<DPoint2> F_multipole_exp,
            NodeArray<DPoint2> F_local_exp,
            NodeArray<DPoint2> F_rep)
    {
        node v;
        for (Iterator<node> i = G.nodesIterator(); i.hasNext();)
        {
            v = i.next();
            F_rep.set(v, F_direct.get(v).plus(F_local_exp.get(v).plus(F_multipole_exp.get(v))));
        }
    }

    double f_rep_scalar(double d)
    {
        if (d > 0)
        {
            return 1 / d;
        }
        else
        {
            if (DEBUG_BUILD)
            {
                println("Error  f_rep_scalar nodes at same position");
            }
            return 0;
        }
    }

    void init_binko(int t)
    {
        BK = new double[t + 1][t + 1];

        //Pascal's triangle
        for (int i = 0; i <= t; i++)
        {
            BK[i][0] = BK[i][i] = 1;
        }

        for (int i = 2; i <= t; i++)
        {
            for (int j = 1; j < i; j++)
            {
                BK[i][j] = BK[i - 1][j - 1] + BK[i - 1][j];
            }
        }
    }

    double binko(int n, int k)
    {
        return BK[n][k];
    }

    //The way to construct the reduced tree (0) = level by level (1) path by path
    //(2) subtree by subtree
    FMMMLayout.ReducedTreeConstruction tree_construction_way()
    {
        return _tree_construction_way;
    }

    void tree_construction_way(FMMMLayout.ReducedTreeConstruction a)
    {
        _tree_construction_way = a;
    }

    //(0) means that the smallest quadratic cell that surrounds a node of the
    //quadtree is calculated iteratively in constant time (1) means that it is
    //calculated by the formula of Aluru et al. in constant time
    FMMMLayout.SmallestCellFinding find_sm_cell()
    {
        return _find_small_cell;
    }

    void find_sm_cell(FMMMLayout.SmallestCellFinding a)
    {
        _find_small_cell = a;
    }

    //Max. number of particles that are contained in a leaf of the red. quadtree.
    void particles_in_leaves(int b)
    {
        _particles_in_leaves = ((b >= 1) ? b : 1);
    }

    int particles_in_leaves()
    {
        return _particles_in_leaves;
    }

    //The precision p for the p-term multipole expansions.
    void precision(int p)
    {
        _precision = ((p >= 1) ? p : 1);
    }

    int precision()
    {
        return _precision;
    }
}*/
