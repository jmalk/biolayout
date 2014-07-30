package ogdf.energybased;

/*
 * $Revision: 2523 $
 *
 * last checkin:
 *   $Author: gutwenger $
 *   $Date: 2012-07-02 20:59:27 +0200 (Mon, 02 Jul 2012) $
 ***************************************************************/
/**
 * \file \brief Declaration of class EdgeAttributes.
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

import ogdf.basic.edge;

public class EdgeAttributes
{
    //helping data structure that stores the graphical attributes of an edge
    //that are needed for the force-directed  algorithms.

    public EdgeAttributes()
    {
        length = 0;
        e_original = null;
        e_subgraph = null;
        moon_edge = false;
        extra_edge = false;
    }

    public void set_EdgeAttributes(double l, edge e_orig, edge e_sub)
    {
        length = l;
        e_original = e_orig;
        e_subgraph = e_sub;
    }

    public void set_length(double l)
    {
        length = l;
    }

    public double get_length()
    {
        return length;
    }

    //needed for the divide et impera step in FMMM
    public void set_original_edge(edge e)
    {
        e_original = e;
    }

    public void set_subgraph_edge(edge e)
    {
        e_subgraph = e;
    }

    public edge get_original_edge()
    {
        return e_original;
    }

    public edge get_subgraph_edge()
    {
        return e_subgraph;
    }

    //needed for the preprocessing step in FMMM (set/get_original_edge are needed, too)
    public void set_copy_edge(edge e)
    {
        e_subgraph = e;
    }

    public edge get_copy_edge()
    {
        return e_subgraph;
    }

    //needed for multilevel step
    public void set_higher_level_edge(edge e)
    {
        e_subgraph = e;
    }

    public edge get_higher_level_edge()
    {
        return e_subgraph;
    }

    public boolean is_moon_edge()
    {
        return moon_edge;
    }

    public void make_moon_edge()
    {
        moon_edge = true;
    }

    public boolean is_extra_edge()
    {
        return extra_edge;
    }

    public void make_extra_edge()
    {
        extra_edge = true;
    }

    public void mark_as_normal_edge()
    {
        extra_edge = false;
    }

    public void init_mult_values()
    {
        e_subgraph = null;
        moon_edge = false;
    }

    private double length;
    private edge e_original;
    private edge e_subgraph;
    private boolean moon_edge; //indicates if this edge is associasted with a moon node
    private boolean extra_edge;//indicates if this edge is an extra edge that is added to
    //enforce few edge crossings
}
