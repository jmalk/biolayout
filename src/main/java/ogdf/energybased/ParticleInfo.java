package ogdf.energybased;

/*
 * $Revision: 2564 $
 *
 * last checkin:
 *   $Author: gutwenger $
 *   $Date: 2012-07-07 00:03:48 +0200 (Sa, 07. Jul 2012) $
 ***************************************************************/
/**
 * \file \brief Declaration of class ParticleInfo.
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

public class ParticleInfo
{
    public ParticleInfo()    //constructor
    {
        vertex = null;
        x_y_coord = 0;
        cross_ref_item = null;
        copy_item = null;
        subList_ptr = null;
        marked = false;
        tmp_item = null;
    }

    public ParticleInfo(ParticleInfo other)
    {
        vertex = other.vertex;
        x_y_coord = other.x_y_coord;
        cross_ref_item = other.cross_ref_item;
        copy_item = other.copy_item;
        subList_ptr = other.subList_ptr;
        marked = other.marked;
        tmp_item = other.tmp_item;
    }

    public void set_vertex(node v)
    {
        vertex = v;
    }

    public void set_x_y_coord(double c)
    {
        x_y_coord = c;
    }

    public void set_cross_ref_item(ParticleInfo it)
    {
        cross_ref_item = it;
    }

    public void set_subList_ptr(List<ParticleInfo> ptr)
    {
        subList_ptr = ptr;
    }

    public void set_copy_item(ParticleInfo it)
    {
        copy_item = it;
    }

    public void mark()
    {
        marked = true;
    }

    public void unmark()
    {
        marked = false;
    }

    public void set_tmp_cross_ref_item(ParticleInfo it)
    {
        tmp_item = it;
    }

    public node get_vertex()
    {
        return vertex;
    }

    public double get_x_y_coord()
    {
        return x_y_coord;
    }

    public ParticleInfo get_cross_ref_item()
    {
        return cross_ref_item;
    }

    public List<ParticleInfo> get_subList_ptr()
    {
        return subList_ptr;
    }

    public ParticleInfo get_copy_item()
    {
        return copy_item;
    }

    public boolean is_marked()
    {
        return marked;
    }

    public ParticleInfo get_tmp_cross_ref_item()
    {
        return tmp_item;
    }

    static public Comparator<ParticleInfo> comparator()
    {
        return new Comparator<ParticleInfo>()
        {
            @Override
            public int compare(ParticleInfo a, ParticleInfo b)
            {
                double p = a.get_x_y_coord();
                double q = b.get_x_y_coord();
                if (p < q)
                {
                    return -1;
                }
                else if (p > q)
                {
                    return 1;
                }
                else
                {
                    return 0;
                }
            }
        };
    }

    node vertex;      //the vertex of G that is associated with this attributes
    double x_y_coord; //the x (resp. y) coordinate of the actual position of the vertex
    ParticleInfo cross_ref_item;	//the Listiterator of the
    //ParticleInfo-Element that
    //containes the vertex in the List storing the other
    //coordinates (a cross reference)
    List<ParticleInfo> subList_ptr;	//points to the subList of L_x(L_y) where the
    //actual entry of ParticleInfo has to be stored
    ParticleInfo copy_item;  //the item of this entry in the copy List
    boolean marked; //indicates if this ParticleInfo object is marked or not
    ParticleInfo tmp_item;	//a temporily item that is used to construct
    //the cross references for the copy_Lists
    //and the subLists
}
