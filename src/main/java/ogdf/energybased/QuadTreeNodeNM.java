package ogdf.energybased;

/*
 * $Revision: 2555 $
 *
 * last checkin:
 *   $Author: gutwenger $
 *   $Date: 2012-07-06 12:12:10 +0200 (Fr, 06. Jul 2012) $
 ***************************************************************/
/**
 * \file \brief Implementation of class QuadTreeNodeNM.
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

public class QuadTreeNodeNM
{

    int Sm_level;                     //level of the small cell
    DPoint2 Sm_downleftcorner;          //coords of the down left corner of the small cell
    double Sm_boxlength;               //length of small cell
    List<ParticleInfo> L_x_ptr;       //points to the lists that contain each Particle
    //of G with its x(y)coordinate in increasing order
    List<ParticleInfo> L_y_ptr;       //and a cross reference to the list_item in the
    //list  with the other coordinate
    int subtreeparticlenumber;         //the number of particles in the subtree rooted
    //at this node
    Complex Sm_center;        //center of the small cell
    Complex[] ME;               //Multipole Expansion terms
    Complex[] LE;               //Locale Expansion terms
    List<node> contained_nodes;      //list of nodes of G that are contained in this
    //QuadTreeNode  (emty if it is not a leave of
    //the ModQuadTree
    List<QuadTreeNodeNM> I;          //the list of min. ill sep. nodes in DIM2
    List<QuadTreeNodeNM> D1, D2;      //list of neighbouring(=D1) and not adjacent(=D2)
    //leaves for direct force calculation in DIM2
    List<QuadTreeNodeNM> M;          //list of nodes with multipole force contribution
    //like in DIM2
    QuadTreeNodeNM father_ptr;   //points to the father node
    QuadTreeNodeNM child_lt_ptr; //points to left top child
    QuadTreeNodeNM child_rt_ptr; //points to right bottom child
    QuadTreeNodeNM child_lb_ptr; //points to left bottom child
    QuadTreeNodeNM child_rb_ptr; //points to right bottom child

    public QuadTreeNodeNM()
    {
        L_x_ptr = null;
        L_y_ptr = null;
        subtreeparticlenumber = 0;
        Sm_level = 0;
        Sm_downleftcorner = new DPoint2();
        Sm_boxlength = 0;
        Sm_center = new Complex();
        ME = null;
        LE = null;
        contained_nodes = new ArrayList<node>();
        I = new ArrayList<QuadTreeNodeNM>();
        D1 = new ArrayList<QuadTreeNodeNM>();
        D2 = new ArrayList<QuadTreeNodeNM>();
        M = new ArrayList<QuadTreeNodeNM>();
        father_ptr = null;
        child_lt_ptr = child_rt_ptr = child_lb_ptr = child_rb_ptr = null;
    }

    public void set_Sm_level(int l)
    {
        Sm_level = l;
    }

    public void set_Sm_downleftcorner(DPoint2 dlc)
    {
        Sm_downleftcorner = new DPoint2(dlc);
    }

    public void set_Sm_boxlength(double l)
    {
        Sm_boxlength = l;
    }

    public void set_x_List_ptr(List<ParticleInfo> x_ptr)
    {
        L_x_ptr = x_ptr;
    }

    public void set_y_List_ptr(List<ParticleInfo> y_ptr)
    {
        L_y_ptr = y_ptr;
    }

    public void set_particlenumber_in_subtree(int p)
    {
        subtreeparticlenumber = p;
    }

    public void set_Sm_center(Complex c)
    {
        Sm_center = c;
    }

    public void set_contained_nodes(List<node> L)
    {
        contained_nodes = L;
    }

    public void pushBack_contained_nodes(node v)
    {
        contained_nodes.add(v);
    }

    public node pop_contained_nodes()
    {
        return contained_nodes.remove(0);
    }

    public boolean contained_nodes_empty()
    {
        return contained_nodes.isEmpty();
    }

    public void set_I(List<QuadTreeNodeNM> l)
    {
        I = l;
    }

    public void set_D1(List<QuadTreeNodeNM> l)
    {
        D1 = l;
    }

    public void set_D2(List<QuadTreeNodeNM> l)
    {
        D2 = l;
    }

    public void set_M(List<QuadTreeNodeNM> l)
    {
        M = l;
    }

    //LE[i] is set to local[i] for i = 0 to precision and space for LE is reserved.
    public void set_locale_exp(Complex[] local, int precision)
    {
        int i;
        LE = new Complex[precision + 1];
        for (i = 0; i <= precision; i++)
        {
            LE[i] = local[i];
        }
    }

    //ME[i] is set to multi[i] for i = 0 to precision and space for LE is reserved.
    public void set_multipole_exp(Complex[] multi, int precision)
    {
        int i;
        ME = new Complex[precision + 1];
        for (i = 0; i <= precision; i++)
        {
            ME[i] = multi[i];
        }
    }

    //ME[i] is set to multi[i] for i = 0 to precision and no space for LE is reserved.
    public void replace_multipole_exp(Complex[] multi, int precision)
    {
        int i;
        for (i = 0; i <= precision; i++)
        {
            ME[i] = multi[i];
        }
    }

    public void set_father_ptr(QuadTreeNodeNM f)
    {
        father_ptr = f;
    }

    public void set_child_lt_ptr(QuadTreeNodeNM c)
    {
        child_lt_ptr = c;
    }

    public void set_child_rt_ptr(QuadTreeNodeNM c)
    {
        child_rt_ptr = c;
    }

    public void set_child_lb_ptr(QuadTreeNodeNM c)
    {
        child_lb_ptr = c;
    }

    public void set_child_rb_ptr(QuadTreeNodeNM c)
    {
        child_rb_ptr = c;
    }

    public boolean is_root()
    {
        if (father_ptr == null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean is_leaf()
    {
        if ((child_lt_ptr == null) && (child_rt_ptr == null) && (child_lb_ptr ==
                null) && (child_rb_ptr == null))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean child_lt_exists()
    {
        if (child_lt_ptr != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean child_rt_exists()
    {
        if (child_rt_ptr != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean child_lb_exists()
    {
        if (child_lb_ptr != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean child_rb_exists()
    {
        if (child_rb_ptr != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public int get_Sm_level()
    {
        return Sm_level;
    }

    public DPoint2 get_Sm_downleftcorner()
    {
        return new DPoint2(Sm_downleftcorner);
    }

    public double get_Sm_boxlength()
    {
        return Sm_boxlength;
    }

    public List<ParticleInfo> get_x_List_ptr()
    {
        return L_x_ptr;
    }

    public List<ParticleInfo> get_y_List_ptr()
    {
        return L_y_ptr;
    }

    public int get_particlenumber_in_subtree()
    {
        return subtreeparticlenumber;
    }

    public Complex get_Sm_center()
    {
        return Sm_center;
    }

    public Complex[] get_local_exp()
    {
        return LE;
    }

    public Complex[] get_multipole_exp()
    {
        return ME;
    }

    public List<node> get_contained_nodes()
    {
        return new ArrayList<node>(contained_nodes);
    }

    public List<QuadTreeNodeNM> get_I()
    {
        return new ArrayList<QuadTreeNodeNM>(I);
    }

    public List<QuadTreeNodeNM> get_D1()
    {
        return new ArrayList<QuadTreeNodeNM>(D1);
    }

    public List<QuadTreeNodeNM> get_D2()
    {
        return new ArrayList<QuadTreeNodeNM>(D2);
    }

    public List<QuadTreeNodeNM> get_M()
    {
        return new ArrayList<QuadTreeNodeNM>(M);
    }

    public QuadTreeNodeNM get_father_ptr()
    {
        return father_ptr;
    }

    public QuadTreeNodeNM get_child_lt_ptr()
    {
        return child_lt_ptr;
    }

    public QuadTreeNodeNM get_child_rt_ptr()
    {
        return child_rt_ptr;
    }

    public QuadTreeNodeNM get_child_lb_ptr()
    {
        return child_lb_ptr;
    }

    public QuadTreeNodeNM get_child_rb_ptr()
    {
        return child_rb_ptr;
    }
}
