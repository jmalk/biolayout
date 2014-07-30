package ogdf.energybased;

/*
 * $Revision: 2559 $
 *
 * last checkin:
 *   $Author: gutwenger $
 *   $Date: 2012-07-06 15:04:28 +0200 (Fr, 06. Jul 2012) $
 ***************************************************************/
/**
 * \file \brief Declaration of class Rectangle.
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
import ogdf.basic.*;

class Rectangle
{

    public Rectangle() //constructor
    {
        old_down_left_corner_position = PointFactory.INSTANCE.newDPoint();
        new_down_left_corner_position = PointFactory.INSTANCE.newDPoint();
        width = 0;
        height = 0;
        component_index = -1;
        tipped_over = false;
    }

    public void set_rectangle(double w, double h, double old_dlc_x_pos, double old_dlc_y_pos, int comp_index)
    {
        width = w;
        height = h;
        old_down_left_corner_position.setX(old_dlc_x_pos);
        old_down_left_corner_position.setY(old_dlc_y_pos);
        component_index = comp_index;
        tipped_over = false;
    }

    public void set_old_dlc_position(DPoint dlc_pos)
    {
        old_down_left_corner_position = PointFactory.INSTANCE.newDPoint(dlc_pos);
    }

    public void set_new_dlc_position(DPoint dlc_pos)
    {
        new_down_left_corner_position = PointFactory.INSTANCE.newDPoint(dlc_pos);
    }

    public void set_width(double w)
    {
        width = w;
    }

    public void set_height(double h)
    {
        height = h;
    }

    public void set_component_index(int comp_index)
    {
        component_index = comp_index;
    }

    public void tipp_over()
    {
        if (tipped_over == false)
        {
            tipped_over = true;
        }
        else
        {
            tipped_over = false;
        }
    }

    public DPoint get_old_dlc_position()
    {
        return PointFactory.INSTANCE.newDPoint(old_down_left_corner_position);
    }

    public DPoint get_new_dlc_position()
    {
        return PointFactory.INSTANCE.newDPoint(new_down_left_corner_position);
    }

    public double get_width()
    {
        return width;
    }

    public double get_height()
    {
        return height;
    }

    public int get_component_index()
    {
        return component_index;
    }

    public boolean is_tipped_over()
    {
        return tipped_over;
    }
    private DPoint old_down_left_corner_position;//down left corner of the tight surround. rect.
    private DPoint new_down_left_corner_position;//new calculated down left corner of ...
    private double width;                     //width of the surround. rect.
    private double height;                    //height of the surround. rect.
    private int component_index;  //the index of the related connected component
    private boolean tipped_over;     //indicates if this rectangle has been tipped over in the
    //packing step
};

//Needed for sorting algorithms in ogdf/List and ogdf/Array.
class RectangleComparerHeight
{

    public RectangleComparerHeight()
    {
    }

    public boolean less(Rectangle A, Rectangle B)
    {
        if (A.get_height() > B.get_height())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean leq(Rectangle A, Rectangle B)
    {
        if (A.get_height() >= B.get_height())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean equal(Rectangle A, Rectangle B)
    {
        if (A.get_height() == B.get_height())
        {
            return true;
        }
        else
        {
            return false;
        }
    }
};

class RectangleComparerWidth
{

    public RectangleComparerWidth()
    {
    }

    public boolean less(Rectangle A, Rectangle B)
    {
        if (A.get_width() > B.get_width())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean leq(Rectangle A, Rectangle B)
    {
        if (A.get_width() >= B.get_width())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean equal(Rectangle A, Rectangle B)
    {
        if (A.get_width() == B.get_width())
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}