package ogdf.energybased;

/*
 * $Revision: 2555 $
 *
 * last checkin:
 *   $Author: gutwenger $
 *   $Date: 2012-07-06 12:12:10 +0200 (Fr, 06. Jul 2012) $
 ***************************************************************/
/**
 * \file \brief Declaration of class NodeAttributes.
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
import ogdf.basic.DPoint;
import ogdf.basic.PointFactory;
import ogdf.basic.node;

public class NodeAttributes
{
    //helping data structure that stores the graphical attributes of a node
    //that are needed for the force-directed algorithms.

    public NodeAttributes()
    {
        position = PointFactory.INSTANCE.newDPoint();
        width = 0;
        height = 0;
        v_lower_level = null;
        v_higher_level = null;

        //for multilevel step
        mass = 0;
        type = 0;
        dedicated_sun_node = null;
        dedicated_sun_distance = 0;
        dedicated_pm_node = null;
        lambda = new ArrayList<Double>();
        neighbour_s_node = new ArrayList<node>();
        //lambda_List_ptr = &lambda;
        //neighbour_s_node_List_ptr = &neighbour_s_node;
        moon_List = new ArrayList<node>();
        //moon_List_ptr = &moon_List;
        placed = false;
        angle_1 = 0;
        angle_2 = 6.2831853;
    }

    public void set_NodeAttributes(double w, double h, DPoint pos, node v_low, node v_high)
    {
        width = w;
        height = h;
        position = PointFactory.INSTANCE.newDPoint(pos);
        v_lower_level = v_low;
        v_higher_level = v_high;
    }

    public void set_position(DPoint pos)
    {
        position = PointFactory.INSTANCE.newDPoint(pos);
    }

    public void set_width(double w)
    {
        width = w;
    }

    public void set_height(double h)
    {
        height = h;
    }

    public DPoint get_position()
    {
        return PointFactory.INSTANCE.newDPoint(position);
    }

    public double get_width()
    {
        return width;
    }

    public double get_height()
    {
        return height;
    }

    //for preprocessing step in FMMM
    public void set_original_node(node v)
    {
        v_lower_level = v;
    }

    public void set_copy_node(node v)
    {
        v_higher_level = v;
    }

    public node get_original_node()
    {
        return v_lower_level;
    }

    public node get_copy_node()
    {
        return v_higher_level;
    }

    //for divide et impera step in FMMM (set/get_original_node() are needed, too)
    public void set_subgraph_node(node v)
    {
        v_higher_level = v;
    }

    public node get_subgraph_node()
    {
        return v_higher_level;
    }

    //for the multilevel step in FMMM
    public void set_lower_level_node(node v)
    {
        v_lower_level = v;
    }

    public void set_higher_level_node(node v)
    {
        v_higher_level = v;
    }

    public node get_lower_level_node()
    {
        return v_lower_level;
    }

    public node get_higher_level_node()
    {
        return v_higher_level;
    }

    public void set_mass(int m)
    {
        mass = m;
    }

    public void set_type(int t)
    {
        type = t;
    }

    public void set_dedicated_sun_node(node v)
    {
        dedicated_sun_node = v;
    }

    public void set_dedicated_sun_distance(double d)
    {
        dedicated_sun_distance = d;
    }

    public void set_dedicated_pm_node(node v)
    {
        dedicated_pm_node = v;
    }

    public void place()
    {
        placed = true;
    }

    public void set_angle_1(double a)
    {
        angle_1 = a;
    }

    public void set_angle_2(double a)
    {
        angle_2 = a;
    }

    public int get_mass()
    {
        return mass;
    }

    public int get_type()
    {
        return type;
    }

    public node get_dedicated_sun_node()
    {
        return dedicated_sun_node;
    }

    public double get_dedicated_sun_distance()
    {
        return dedicated_sun_distance;
    }

    public node get_dedicated_pm_node()
    {
        return dedicated_pm_node;
    }

    public boolean is_placed()
    {
        return placed;
    }

    public double get_angle_1()
    {
        return angle_1;
    }

    public double get_angle_2()
    {
        return angle_2;
    }

    public List<Double> get_lambda_List()
    {
        return lambda;
    }

    public List<node> get_neighbour_sun_node_List()
    {
        return neighbour_s_node;
    }

    public List<node> get_dedicated_moon_node_List()
    {
        return moon_List;
    }

    //initialzes all values needed for multilevel representations
    public void init_mult_values()
    {
        type = 0;
        dedicated_sun_node = null;
        dedicated_sun_distance = 0;
        dedicated_pm_node = null;
        lambda.clear();
        neighbour_s_node.clear();
        //lambda_List_ptr =  & lambda;
        //neighbour_s_node_List_ptr =  & neighbour_s_node;
        moon_List.clear();
        //moon_List_ptr =  & moon_List;
        placed = false;
        angle_1 = 0;
        angle_2 = 6.2831853;

    }

    private DPoint position;
    private double width;
    private double height;
    //for the multilevel and divide et impera and preprocessing step
    private node v_lower_level; //the corresponding node in the lower level graph
    private node v_higher_level;//the corresponding node in the higher level graph
    //for divide et impera v_lower_level is the original graph and
    //v_higher_level is the copy of the copy of this node in the
    //maximum connected subraph
    //for the multilevel step
    private int mass; //the mass (= number of previously collapsed nodes) of this node
    private int type; //1 = sun node (s_node); 2 = planet node (p_node) without a dedicate moon
    //3 = planet node with dedicated moons (pm_node);4 = moon node (m_node)
    private node dedicated_sun_node; //the dedicates s_node of the solar system of this node
    private double dedicated_sun_distance;//the distance to the dedicated sun node of the galaxy
    //of this node
    private node dedicated_pm_node;//if type == 4 the dedicated_pm_node is saved here
    private List<Double> lambda; //the factors lambda for scaling the length of this edge
    //relative to the pass between v's sun and the sun of a
    //neighbour solar system
    private List<node> neighbour_s_node;//this is the list of the neighbour solar systems suns
    //lambda[i] corresponds to neighbour_s_node[i]
    //List<Double> lambda_List_ptr; //a pointer to the lambda list
    //List<node> neighbour_s_node_List_ptr; //a pointer to to the neighbour_s_node list
    private List<node> moon_List;//the list of all dedicated moon nodes (!= nil if type == 3)
    //List<node> moon_List_ptr;//a pointer to the moon_List
    private boolean placed;   //indicates weather an initial position has been assigned to this
    //node or not
    private double angle_1;//describes the sector where nodes that are not adjacent to other
    private double angle_2;//solar systems have to be placed
};
