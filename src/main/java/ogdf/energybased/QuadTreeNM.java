package ogdf.energybased;

/*
 * $Revision: 2552 $
 *
 * last checkin:
 *   $Author: gutwenger $
 *   $Date: 2012-07-05 16:45:20 +0200 (Do, 05. Jul 2012) $
 ***************************************************************/
/**
 * \file \brief Implementation of class QuadTreeNM.
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
import org.BioLayoutExpress3D.Utils.Complex;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

public class QuadTreeNM
{
    public QuadTreeNM()
    {
        root_ptr = act_ptr = null;
    }

    public void create_new_lt_child(
            List<ParticleInfo> L_x_ptr,
            List<ParticleInfo> L_y_ptr)
    {
        QuadTreeNodeNM new_ptr = new QuadTreeNodeNM();

        DPoint2 old_Sm_dlc = act_ptr.get_Sm_downleftcorner();
        DPoint2 new_Sm_dlc = new DPoint2();
        new_Sm_dlc.m_x = old_Sm_dlc.m_x;
        new_Sm_dlc.m_y = old_Sm_dlc.m_y + act_ptr.get_Sm_boxlength() / 2;

        new_ptr.set_Sm_level(act_ptr.get_Sm_level() + 1);
        new_ptr.set_Sm_downleftcorner(new_Sm_dlc);
        new_ptr.set_Sm_boxlength((act_ptr.get_Sm_boxlength()) / 2);
        new_ptr.set_x_List_ptr(L_x_ptr);
        new_ptr.set_y_List_ptr(L_y_ptr);
        new_ptr.set_father_ptr(act_ptr);
        act_ptr.set_child_lt_ptr(new_ptr);
    }

    public void create_new_lt_child()
    {
        QuadTreeNodeNM new_ptr = new QuadTreeNodeNM();

        DPoint2 old_Sm_dlc = act_ptr.get_Sm_downleftcorner();
        DPoint2 new_Sm_dlc = new DPoint2();
        new_Sm_dlc.m_x = old_Sm_dlc.m_x;
        new_Sm_dlc.m_y = old_Sm_dlc.m_y + act_ptr.get_Sm_boxlength() / 2;

        new_ptr.set_Sm_level(act_ptr.get_Sm_level() + 1);
        new_ptr.set_Sm_downleftcorner(new_Sm_dlc);
        new_ptr.set_Sm_boxlength((act_ptr.get_Sm_boxlength()) / 2);
        new_ptr.set_father_ptr(act_ptr);
        act_ptr.set_child_lt_ptr(new_ptr);
    }

    public void create_new_rt_child(
            List<ParticleInfo> L_x_ptr,
            List<ParticleInfo> L_y_ptr)
    {
        QuadTreeNodeNM new_ptr = new QuadTreeNodeNM();

        DPoint2 old_Sm_dlc = act_ptr.get_Sm_downleftcorner();
        DPoint2 new_Sm_dlc = new DPoint2();
        new_Sm_dlc.m_x = old_Sm_dlc.m_x + act_ptr.get_Sm_boxlength() / 2;
        new_Sm_dlc.m_y = old_Sm_dlc.m_y + act_ptr.get_Sm_boxlength() / 2;

        new_ptr.set_Sm_level(act_ptr.get_Sm_level() + 1);
        new_ptr.set_Sm_downleftcorner(new_Sm_dlc);
        new_ptr.set_Sm_boxlength((act_ptr.get_Sm_boxlength()) / 2);
        new_ptr.set_x_List_ptr(L_x_ptr);
        new_ptr.set_y_List_ptr(L_y_ptr);
        new_ptr.set_father_ptr(act_ptr);
        act_ptr.set_child_rt_ptr(new_ptr);
    }

    public void create_new_rt_child()
    {
        QuadTreeNodeNM new_ptr = new QuadTreeNodeNM();

        DPoint2 old_Sm_dlc = act_ptr.get_Sm_downleftcorner();
        DPoint2 new_Sm_dlc = new DPoint2();
        new_Sm_dlc.m_x = old_Sm_dlc.m_x + act_ptr.get_Sm_boxlength() / 2;
        new_Sm_dlc.m_y = old_Sm_dlc.m_y + act_ptr.get_Sm_boxlength() / 2;

        new_ptr.set_Sm_level(act_ptr.get_Sm_level() + 1);
        new_ptr.set_Sm_downleftcorner(new_Sm_dlc);
        new_ptr.set_Sm_boxlength((act_ptr.get_Sm_boxlength()) / 2);
        new_ptr.set_father_ptr(act_ptr);
        act_ptr.set_child_rt_ptr(new_ptr);
    }

    public void create_new_lb_child(
            List<ParticleInfo> L_x_ptr,
            List<ParticleInfo> L_y_ptr)
    {
        QuadTreeNodeNM new_ptr = new QuadTreeNodeNM();

        DPoint2 old_Sm_dlc = act_ptr.get_Sm_downleftcorner();
        DPoint2 new_Sm_dlc = new DPoint2();
        new_Sm_dlc.m_x = old_Sm_dlc.m_x;
        new_Sm_dlc.m_y = old_Sm_dlc.m_y;

        new_ptr.set_Sm_level(act_ptr.get_Sm_level() + 1);
        new_ptr.set_Sm_downleftcorner(new_Sm_dlc);
        new_ptr.set_Sm_boxlength((act_ptr.get_Sm_boxlength()) / 2);
        new_ptr.set_x_List_ptr(L_x_ptr);
        new_ptr.set_y_List_ptr(L_y_ptr);
        new_ptr.set_father_ptr(act_ptr);
        act_ptr.set_child_lb_ptr(new_ptr);
    }

    public void create_new_lb_child()
    {
        QuadTreeNodeNM new_ptr = new QuadTreeNodeNM();

        DPoint2 old_Sm_dlc = act_ptr.get_Sm_downleftcorner();
        DPoint2 new_Sm_dlc = new DPoint2();
        new_Sm_dlc.m_x = old_Sm_dlc.m_x;
        new_Sm_dlc.m_y = old_Sm_dlc.m_y;

        new_ptr.set_Sm_level(act_ptr.get_Sm_level() + 1);
        new_ptr.set_Sm_downleftcorner(new_Sm_dlc);
        new_ptr.set_Sm_boxlength((act_ptr.get_Sm_boxlength()) / 2);
        new_ptr.set_father_ptr(act_ptr);
        act_ptr.set_child_lb_ptr(new_ptr);
    }

    public void create_new_rb_child(
            List<ParticleInfo> L_x_ptr,
            List<ParticleInfo> L_y_ptr)
    {
        QuadTreeNodeNM new_ptr = new QuadTreeNodeNM();

        DPoint2 old_Sm_dlc = act_ptr.get_Sm_downleftcorner();
        DPoint2 new_Sm_dlc = new DPoint2();
        new_Sm_dlc.m_x = old_Sm_dlc.m_x + act_ptr.get_Sm_boxlength() / 2;
        new_Sm_dlc.m_y = old_Sm_dlc.m_y;

        new_ptr.set_Sm_level(act_ptr.get_Sm_level() + 1);
        new_ptr.set_Sm_downleftcorner(new_Sm_dlc);
        new_ptr.set_Sm_boxlength((act_ptr.get_Sm_boxlength()) / 2);
        new_ptr.set_x_List_ptr(L_x_ptr);
        new_ptr.set_y_List_ptr(L_y_ptr);
        new_ptr.set_father_ptr(act_ptr);
        act_ptr.set_child_rb_ptr(new_ptr);
    }

    public void create_new_rb_child()
    {
        QuadTreeNodeNM new_ptr = new QuadTreeNodeNM();

        DPoint2 old_Sm_dlc = act_ptr.get_Sm_downleftcorner();
        DPoint2 new_Sm_dlc = new DPoint2();
        new_Sm_dlc.m_x = old_Sm_dlc.m_x + act_ptr.get_Sm_boxlength() / 2;
        new_Sm_dlc.m_y = old_Sm_dlc.m_y;

        new_ptr.set_Sm_level(act_ptr.get_Sm_level() + 1);
        new_ptr.set_Sm_downleftcorner(new_Sm_dlc);
        new_ptr.set_Sm_boxlength((act_ptr.get_Sm_boxlength()) / 2);
        new_ptr.set_father_ptr(act_ptr);
        act_ptr.set_child_rb_ptr(new_ptr);
    }

    public void delete_tree(QuadTreeNodeNM node_ptr)
    {
        if (node_ptr != null)
        {
            if (node_ptr.get_child_lt_ptr() != null)
            {
                delete_tree(node_ptr.get_child_lt_ptr());
            }
            if (node_ptr.get_child_rt_ptr() != null)
            {
                delete_tree(node_ptr.get_child_rt_ptr());
            }
            if (node_ptr.get_child_lb_ptr() != null)
            {
                delete_tree(node_ptr.get_child_lb_ptr());
            }
            if (node_ptr.get_child_rb_ptr() != null)
            {
                delete_tree(node_ptr.get_child_rb_ptr());
            }

            if (node_ptr == root_ptr)
            {
                root_ptr = null;
            }
        }
    }

    public int delete_tree_and_count_nodes(QuadTreeNodeNM node_ptr, int nodecounter)
    {
        if (node_ptr != null)
        {
            nodecounter++;
            if (node_ptr.get_child_lt_ptr() != null)
            {
                delete_tree_and_count_nodes(node_ptr.get_child_lt_ptr(), nodecounter);
            }
            if (node_ptr.get_child_rt_ptr() != null)
            {
                delete_tree_and_count_nodes(node_ptr.get_child_rt_ptr(), nodecounter);
            }
            if (node_ptr.get_child_lb_ptr() != null)
            {
                delete_tree_and_count_nodes(node_ptr.get_child_lb_ptr(), nodecounter);
            }
            if (node_ptr.get_child_rb_ptr() != null)
            {
                delete_tree_and_count_nodes(node_ptr.get_child_rb_ptr(), nodecounter);
            }

            if (node_ptr == root_ptr)
            {
                root_ptr = null;
            }
        }

        return nodecounter;
    }

    public void cout_preorder(QuadTreeNodeNM node_ptr)
    {
        if (!DEBUG_BUILD)
        {
            return;
        }

        if (node_ptr != null)
        {
            println(node_ptr.toString());

            if (node_ptr.get_child_lt_ptr() != null)
            {
                cout_preorder(node_ptr.get_child_lt_ptr());
            }
            if (node_ptr.get_child_rt_ptr() != null)
            {
                cout_preorder(node_ptr.get_child_rt_ptr());
            }
            if (node_ptr.get_child_lb_ptr() != null)
            {
                cout_preorder(node_ptr.get_child_lb_ptr());
            }
            if (node_ptr.get_child_rb_ptr() != null)
            {
                cout_preorder(node_ptr.get_child_rb_ptr());
            }
        }
    }

    public void cout_preorder(QuadTreeNodeNM node_ptr, int precision)
    {
        if (!DEBUG_BUILD)
        {
            return;
        }

        int i;
        if (node_ptr != null)
        {
            Complex[] L = node_ptr.get_local_exp();
            Complex[] M = node_ptr.get_multipole_exp();
            println(node_ptr.toString());

            println(" ME: ");
            for (i = 0; i <= precision; i++)
            {
                println(M[i] + " ");
            }
            println(" LE: ");
            for (i = 0; i <= precision; i++)
            {
                println(L[i] + " ");
            }

            if (node_ptr.get_child_lt_ptr() != null)
            {
                cout_preorder(node_ptr.get_child_lt_ptr(), precision);
            }
            if (node_ptr.get_child_rt_ptr() != null)
            {
                cout_preorder(node_ptr.get_child_rt_ptr(), precision);
            }
            if (node_ptr.get_child_lb_ptr() != null)
            {
                cout_preorder(node_ptr.get_child_lb_ptr(), precision);
            }
            if (node_ptr.get_child_rb_ptr() != null)
            {
                cout_preorder(node_ptr.get_child_rb_ptr(), precision);
            }
        }
    }
    //Creates the root node and lets act_ptr and root_ptr point to the root node.

    public void init_tree()
    {
        root_ptr = new QuadTreeNodeNM();
        act_ptr = root_ptr;
    }

    //Sets act_ptr to the root_ptr.
    public void start_at_root()
    {
        act_ptr = root_ptr;
    }

    //Sets act_ptr to the father_ptr.
    public void go_to_father()
    {
        if (act_ptr.get_father_ptr() != null)
        {
            act_ptr = act_ptr.get_father_ptr();
        }
        else if (DEBUG_BUILD)
        {
            println("Error QuadTreeNM: No father Node exists");
        }
    }

    //Sets act_ptr to the left_top_child_ptr.
    public void go_to_lt_child()
    {
        act_ptr = act_ptr.get_child_lt_ptr();
    }

    //Sets act_ptr to the right_top_child_ptr.
    public void go_to_rt_child()
    {
        act_ptr = act_ptr.get_child_rt_ptr();
    }

    //Sets act_ptr to the left_bottom_child_ptr.
    public void go_to_lb_child()
    {
        act_ptr = act_ptr.get_child_lb_ptr();
    }

    //Sets act_ptr to the right_bottom_child_ptr.
    public void go_to_rb_child()
    {
        act_ptr = act_ptr.get_child_rb_ptr();
    }

    //Returns the actual/root node pointer of the tree.
    public QuadTreeNodeNM get_act_ptr()
    {
        return act_ptr;
    }

    public QuadTreeNodeNM get_root_ptr()
    {
        return root_ptr;
    }

    //Sets root_ptr to r_ptr.
    public void set_root_ptr(QuadTreeNodeNM r_ptr)
    {
        root_ptr = r_ptr;
    }

    //Sets act_ptr to a_ptr.
    public void set_act_ptr(QuadTreeNodeNM a_ptr)
    {
        act_ptr = a_ptr;
    }

    //Sets the content of *root_ptr to r.
    public void set_root_node(QuadTreeNodeNM r)
    {
        root_ptr = r;
    }

    private QuadTreeNodeNM root_ptr; //points to the root node
    private QuadTreeNodeNM act_ptr;  //points to the actual node
}
