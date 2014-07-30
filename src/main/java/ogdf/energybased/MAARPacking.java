package ogdf.energybased;

/*
 * $Revision: 2552 $
 *
 * last checkin:
 *   $Author: gutwenger $
 *   $Date: 2012-07-05 16:45:20 +0200 (Do, 05. Jul 2012) $
 ***************************************************************/
/**
 * \file \brief Declaration of class MAARPacking (used by FMMMLayout).
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
import org.BioLayoutExpress3D.Utils.ref;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

class PackingRowInfo
{
    //Helping data structure for MAARPacking.

    public PackingRowInfo()      //constructor
    {
        total_width = 0;
        max_height = 0;
        row_index = 0;
    }

    void set_max_height(double h)
    {
        max_height = h;
    }

    void set_total_width(double w)
    {
        total_width = w;
    }

    void set_row_index(int i)
    {
        row_index = i;
    }

    double get_max_height()
    {
        return max_height;
    }

    double get_total_width()
    {
        return total_width;
    }

    int get_row_index()
    {
        return row_index;
    }
    private double max_height;  //the maximum height of a rectangle placed in this row
    private double total_width; //the sum of the width of all rectsngles in this row
    private int row_index;      //the index of the row (first row in packing has index 0)
}

//Needed for storing entries of the heap.
class HelpRecord
{

    public HelpRecord()
    {
    }

    void set_ListIterator(PackingRowInfo it)
    {
        iterator = it;
    }

    void set_value(double v)
    {
        value = v;
    }

    PackingRowInfo get_ListIterator()
    {
        return iterator;
    }

    double get_value()
    {
        return value;
    }
    private double value;
    private PackingRowInfo iterator;
}

class PQueue
{
    //Helping data structure that is a priority queue (heap) and holds  double values
    //(as comparison values) and iterators of type ListIterator<PackingRowInfo>
    //as contents. It is needed in class MAARPacking for the Best_Fit insert strategy.

    public PQueue()
    {
        P = new ArrayList<HelpRecord>();
        P.clear();
    }

    //Inserts content with value value into the priority queue and restores the heap.
    void insert(double value, PackingRowInfo iterator)
    {
        HelpRecord h = new HelpRecord();
        h.set_value(value);
        h.set_ListIterator(iterator);
        P.add(h);
        //reheap bottom up
        reheap_bottom_up(P.size() - 1);
    }

    //Deletes the element with the minimum value from the queue and restores
    //the heap.
    void del_min()
    {
        if (P.size() < 1)
        {
            if (DEBUG_BUILD)
            {
                println("Error PQueue:: del_min() ; Heap is empty");
            }
        }
        else
        {
            //last element becomes first element
            P.remove(0);
            if (!P.isEmpty())
            {
                P.add(0, P.get(P.size() - 1));
                P.remove(P.get(P.size() - 1));
                //reheap top down
                reheap_top_down(0);
            }
        }
    }

    //Returns the content with the minimum value.
    PackingRowInfo find_min()
    {
        assert (P.size() >= 1);
        //if(P.size() < 1)
        //  cout<<"Error PQueue:: find_min() ; Heap is empty"<<endl;
        //else
        return P.get(0).get_ListIterator();
    }
    private List<HelpRecord> P;//the priority queue;

    //Restores the heap property in P starting from position i bottom up.
    private void reheap_bottom_up(int i)
    {
        int parent = (i - 1) / 2;

        if ((i != 0) && ((P.get(parent)).get_value() > (P.get(i)).get_value()))
        {
            exchange(i, parent);
            reheap_bottom_up(parent);
        }
    }

    //Restores the heap property in P starting from position i top down.
    private void reheap_top_down(int i)
    {
        int smallest = i;
        int l = 2 * i + 1;
        int r = 2 * i + 2;

        if ((l <= P.size() - 1) && ((P.get(l)).get_value() < (P.get(i)).get_value()))
        {
            smallest = l;
        }
        else
        {
            smallest = i;
        }
        if ((r <= P.size() - 1) && ((P.get(r)).get_value() < (P.get(smallest)).get_value()))
        {
            smallest = r;
        }
        if (smallest != i)//exchange and recursion
        {
            exchange(i, smallest);
            reheap_top_down(smallest);
        }
    }

    //Exchanges heap entries at positions i and j.
    private void exchange(int i, int j)
    {
        HelpRecord h = P.get(i);
        P.set(i, P.get(j));
        P.set(j, h);
    }
}

public class MAARPacking
{

    private double area_height; //total height of the packing area
    private double area_width;  //total width of the packing area

    MAARPacking()
    {
        area_width = 0;
        area_height = 0;
    }

    public void pack_rectangles_using_Best_Fit_strategy(
            List<Rectangle> R,
            double aspect_ratio,
            FMMMLayout.PreSort presort,
            FMMMLayout.TipOver allow_tipping_over,
            ref<Double> aspect_ratio_area,
            ref<Double> bounding_rectangles_area)
    {
        ref<Double> area = new ref<Double>(0.0);
        PackingRowInfo B_F_item;
        List<PackingRowInfo> P = new ArrayList<PackingRowInfo>(); //represents the packing of the rectangles
        List<PackingRowInfo> row_of_rectangle = new ArrayList<PackingRowInfo>(); //stores for each rectangle
        //r at pos. i in R the ListIterator of the row in P
        //where r is placed (at pos i in row_of_rectangle)
        List<Rectangle> rectangle_order = new ArrayList<Rectangle>();//holds the order in which the
        //rectangles are touched
        PQueue total_width_of_row = new PQueue(); //stores for each row the ListIterator of the corresp. list
        //in R and its total width

        if (presort == FMMMLayout.PreSort.psDecreasingHeight)
        {
            presort_rectangles_by_height(R);
        }
        else if (presort == FMMMLayout.PreSort.psDecreasingWidth)
        {
            presort_rectangles_by_width(R);
        }

        //init rectangle_order
        for (int i = 0; i < R.size(); i++)
        {
            rectangle_order.add(R.get(i));
        }

        for (int i = 0; i < R.size(); i++)
        {
            Rectangle r = R.get(i);

            if (P.isEmpty())
            {
                if (better_tipp_rectangle_in_new_row(r, aspect_ratio, allow_tipping_over, area))
                {
                    r = tipp_over(r);
                }
                B_F_insert_rectangle_in_new_row(r, P, row_of_rectangle, total_width_of_row);
                aspect_ratio_area.set(calculate_aspect_ratio_area(r.get_width(), r.get_height(),
                        aspect_ratio));
            }
            else
            {
                B_F_item = find_Best_Fit_insert_position(r, allow_tipping_over,
                        aspect_ratio, aspect_ratio_area,
                        total_width_of_row);

                B_F_insert_rectangle(r, P, row_of_rectangle, B_F_item, total_width_of_row);
            }

            R.set(i, r);
        }
        export_new_rectangle_positions(P, row_of_rectangle, rectangle_order);
        bounding_rectangles_area.set(calculate_bounding_rectangles_area(R));
    }

    public void presort_rectangles_by_height(List<Rectangle> R)
    {
        Collections.sort(R, new java.util.Comparator<Rectangle>()
        {
            @Override
            public int compare(Rectangle a, Rectangle b)
            {
                double diff = b.get_height() - a.get_height();

                if (diff == 0.0)
                    return 0;

                return diff < 0.0 ? -1 : 1;
            }
        });
    }

    public void presort_rectangles_by_width(List<Rectangle> R)
    {
        Collections.sort(R, new java.util.Comparator<Rectangle>()
        {
            @Override
            public int compare(Rectangle a, Rectangle b)
            {
                double diff = b.get_width() - a.get_width();

                if (diff == 0.0)
                    return 0;

                return diff < 0.0 ? -1 : 1;
            }
        });
    }

    public void B_F_insert_rectangle_in_new_row(
            Rectangle r,
            List<PackingRowInfo> P,
            List<PackingRowInfo> row_of_rectangle,
            PQueue total_width_of_row)
    {
        PackingRowInfo p = new PackingRowInfo();

        //create new empty row and insert r into this row of P
        p.set_max_height(r.get_height());
        p.set_total_width(r.get_width());
        p.set_row_index(P.size());
        P.add(p);

        //remember in which row of P r is placed by updating row_of_rectangle
        row_of_rectangle.add(p);

        //update area_height,area_width
        area_width = Math.max(r.get_width(), area_width);
        area_height += r.get_height();


        //update total_width_of_row
        total_width_of_row.insert(r.get_width(), p);
    }

    public PackingRowInfo find_Best_Fit_insert_position(
            Rectangle rect_item,
            FMMMLayout.TipOver allow_tipping_over,
            double aspect_ratio,
            ref<Double> aspect_ratio_area,
            PQueue total_width_of_row)
    {
        numexcept N;
        ref<Double> area_2 = new ref<Double>(0.0);
        int best_try_index, index_2;
        Rectangle r = rect_item;

        if (better_tipp_rectangle_in_new_row(r, aspect_ratio, allow_tipping_over,
                aspect_ratio_area))
        {
            best_try_index = 2;
        }
        else
        {
            best_try_index = 1;
        }

        PackingRowInfo B_F_row = total_width_of_row.find_min();
        if (better_tipp_rectangle_in_this_row(r, aspect_ratio, allow_tipping_over, B_F_row, area_2))
        {
            index_2 = 4;
        }
        else
        {
            index_2 = 3;
        }

        if ((area_2.get() <= aspect_ratio_area.get()) || numexcept.nearly_equal(aspect_ratio_area.get(), area_2.get()))
        {
            aspect_ratio_area.set(area_2.get());
            best_try_index = index_2;
        }

        //return the row and eventually tipp the rectangle with ListIterator rect_item
        if (best_try_index == 1)
        {
            return null;
        }
        else if (best_try_index == 2)
        {
            tipp_over(rect_item);
            return null;
        }
        else if (best_try_index == 3)
        {
            return B_F_row;
        }
        else //best_try_index == 4
        {
            tipp_over(rect_item);
            return B_F_row;
        }
    }

    public void B_F_insert_rectangle(
            Rectangle r,
            List<PackingRowInfo> P,
            List<PackingRowInfo> row_of_rectangle,
            PackingRowInfo B_F_item,
            PQueue total_width_of_row)
    {
        if (B_F_item == null) //insert into a new row
        {
            B_F_insert_rectangle_in_new_row(r, P, row_of_rectangle, total_width_of_row);
        }
        else //insert into an existing row
        {
            double old_max_height;

            //update P[B_F_item]
            PackingRowInfo p = B_F_item;
            old_max_height = p.get_max_height();
            p.set_max_height(Math.max(old_max_height, r.get_height()));
            p.set_total_width(p.get_total_width() + r.get_width());

            //updating row_of_rectangle
            row_of_rectangle.add(B_F_item);

            //update area_height,area_width
            area_width = Math.max(area_width, p.get_total_width());
            area_height = Math.max(area_height, area_height - old_max_height + r.get_height());

            //update total_width_of_row

            total_width_of_row.del_min();
            total_width_of_row.insert(p.get_total_width(), B_F_item);

        }
    }

    public void export_new_rectangle_positions(
            List<PackingRowInfo> P,
            List<PackingRowInfo> row_of_rectangle,
            List<Rectangle> rectangle_order)
    {
        int i;
        Rectangle r;
        PackingRowInfo p, p_pred;
        DPoint new_dlc_pos = PointFactory.INSTANCE.newDPoint();
        double new_x, new_y;
        List<Double> row_y_min = new ArrayList<Double>(); //stores the min. y-coordinates for each row in P
        List<Double> act_row_x_max = new ArrayList<Double>(); //stores the actual rightmost x-coordinate
        //for each row in P
        //ListIterator< ListIterator<PackingRowInfo> > row_item;
        ListIterator<PackingRowInfo> row_item;
        ListIterator<Rectangle> R_item;
        ListIterator<PackingRowInfo> Rrow_item;

        //init act_row_x_max;
        for (i = 0; i < P.size(); i++)
        {
            row_y_min.add(new Double(0.0));
            act_row_x_max.add(new Double(0.0));
        }

        //calculate minimum heights of each row
        row_item = P.listIterator();
        while (row_item.hasNext())
        {
            if (row_item.previousIndex() < 0)
            {
                row_y_min.set(0, 0.0);
                row_item.next();
            }
            else
            {
                p_pred = P.get(row_item.previousIndex());
                p = row_item.next();
                row_y_min.set(p.get_row_index(), row_y_min.get(p.get_row_index() - 1) +
                        p_pred.get_max_height());
            }
        }

        //calculate for each rectangle its new down left corner coordinate
        Rrow_item = row_of_rectangle.listIterator();

        R_item = rectangle_order.listIterator();
        while (R_item.hasNext())
        {
            r = R_item.next();
            p = Rrow_item.next(); Rrow_item.previous();
            new_x = act_row_x_max.get(p.get_row_index());
            act_row_x_max.set(p.get_row_index(), act_row_x_max.get(p.get_row_index()) + r.get_width());
            new_y = row_y_min.get(p.get_row_index()) + (p.get_max_height() - r.get_height()) / 2;

            new_dlc_pos.setX(new_x);
            new_dlc_pos.setY(new_y);
            r.set_new_dlc_position(new_dlc_pos);

            if (Rrow_item.hasNext())
                Rrow_item.next();
        }
    }

    public double calculate_bounding_rectangles_area(List<Rectangle> R)
    {
        double area = 0;
        Rectangle r;

        for (Rectangle r_it : R)
        {
            area += r_it.get_width() * r_it.get_height();
        }

        return area;
    }

    public double calculate_aspect_ratio_area(
            double width,
            double height,
            double aspect_ratio)
    {
        double ratio = width / height;

        if (ratio < aspect_ratio) //scale width
        {
            return (width * height * (aspect_ratio / ratio));
        }
        else //scale height
        {
            return (width * height * (ratio / aspect_ratio));
        }
    }

    public boolean better_tipp_rectangle_in_new_row(
            Rectangle r,
            double aspect_ratio,
            FMMMLayout.TipOver allow_tipping_over,
            ref<Double> best_area)
    {
        double height, width, act_area;
        boolean rotate = false;

        //first try: new row insert position
        width = Math.max(area_width, r.get_width());
        height = area_height + r.get_height();
        best_area.set(calculate_aspect_ratio_area(width, height, aspect_ratio));


        //second try: new row insert position with tipping r over
        if (allow_tipping_over == FMMMLayout.TipOver.toNoGrowingRow ||
                allow_tipping_over == FMMMLayout.TipOver.toAlways)
        {
            width = Math.max(area_width, r.get_height());
            height = area_height + r.get_width();
            act_area = calculate_aspect_ratio_area(width, height, aspect_ratio);
            if (act_area < 0.99999 * best_area.get())
            {
                best_area.set(act_area);
                rotate = true;
            }
        }
        return rotate;
    }

    public boolean better_tipp_rectangle_in_this_row(
            Rectangle r,
            double aspect_ratio,
            FMMMLayout.TipOver allow_tipping_over,
            PackingRowInfo B_F_row,
            ref<Double> best_area)
    {
        double height, width, act_area;
        boolean rotate = false;

        //first try: BEST_FIT insert position
        width = Math.max(area_width, B_F_row.get_total_width() + r.get_width());
        height = Math.max(area_height, area_height - B_F_row.get_max_height() + r.get_height());
        best_area.set(calculate_aspect_ratio_area(width, height, aspect_ratio));

        //second try: BEST_FIT insert position  with skipping r over
        if ((allow_tipping_over == FMMMLayout.TipOver.toNoGrowingRow && r.get_width() <=
                B_F_row.get_max_height()) || allow_tipping_over == FMMMLayout.TipOver.toAlways)
        {
            width = Math.max(area_width, B_F_row.get_total_width() + r.get_height());
            height = Math.max(area_height, area_height - B_F_row.get_max_height() + r.get_width());
            act_area = calculate_aspect_ratio_area(width, height, aspect_ratio);
            if (act_area < 0.99999 * best_area.get())
            {
                best_area.set(act_area);
                rotate = true;
            }
        }
        return rotate;
    }

    public Rectangle tipp_over(Rectangle rect_item)
    {
        Rectangle r = rect_item;
        Rectangle r_tipped_over = r;
        DPoint tipped_dlc = PointFactory.INSTANCE.newDPoint();

        if (r.is_tipped_over() == false)
        {//tipp old_dlc over
            tipped_dlc.setX(r.get_old_dlc_position().getY() * (-1) - r.get_height());
            tipped_dlc.setY(r.get_old_dlc_position().getX());
        }
        else
        {//tipp old_dlc back;
            tipped_dlc.setX(r.get_old_dlc_position().getY());
            tipped_dlc.setY(r.get_old_dlc_position().getX() * (-1) - r.get_width());
        }
        r_tipped_over.set_old_dlc_position(tipped_dlc);
        r_tipped_over.set_width(r.get_height());
        r_tipped_over.set_height(r.get_width());
        r_tipped_over.tipp_over();

        return r_tipped_over;
    }
}
