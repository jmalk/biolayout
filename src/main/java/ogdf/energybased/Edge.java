package ogdf.energybased;

/*
 * $Revision: 2559 $
 *
 * last checkin:
 *   $Author: gutwenger $
 *   $Date: 2012-07-06 15:04:28 +0200 (Fr, 06. Jul 2012) $
 ***************************************************************/

/** \file
 * \brief Declaration of class Edge.
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

import ogdf.basic.*;

class Edge
{
    public Edge()
    {
        e = null;
        Graph_ptr = null;
        angle = 0;
        cut_vertex = null;
    }

    public void set_Edge(edge f, Graph g_ptr)
    {
        Graph_ptr = g_ptr;
        e = f;
    }

    public void set_Edge(edge f, double i, node c)
    {
        angle = i;
        e = f;
        cut_vertex = c;
    }

    public Graph get_Graph_ptr()
    {
        return Graph_ptr;
    }

    public edge get_edge()
    {
        return e;
    }

    public double get_angle()
    {
        return angle;
    }

    public node get_cut_vertex()
    {
        return cut_vertex;
    }

    private edge e;
    private Graph Graph_ptr;
    private double angle;
    private node cut_vertex;
}

