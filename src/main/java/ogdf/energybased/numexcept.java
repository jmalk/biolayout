package ogdf.energybased;

/*
 * $Revision: 2559 $
 *
 * last checkin:
 *   $Author: gutwenger $
 *   $Date: 2012-07-06 15:04:28 +0200 (Fr, 06. Jul 2012) $
 ***************************************************************/
/**
 * \file \brief Declaration of class numexcept (handling of numeric problems).
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
//--------------------------------------------------------------------------
// this class is developed for exceptions that might occure, when nodes are
// placed at the same position and a new random position has to be found, or
// when the calculated forces are near the machine accuracy, where no
// reasonable numeric and logic calculations are possible any more
//---------------------------------------------------------------------------
import java.util.Random;
import ogdf.basic.DPoint;
import ogdf.basic.PointFactory;
import static org.BioLayoutExpress3D.Environment.GlobalEnvironment.*;
import static org.BioLayoutExpress3D.DebugConsole.ConsoleOutput.*;

public class numexcept
{
    final static double epsilon = 0.1;
    final static double POS_SMALL_DOUBLE = 1e-300;
    final static double POS_BIG_DOUBLE = 1e+300;

    static public Random random = null;

    //Returns a distinct random point within the smallest disque D with center
    //old_point that is contained in the box defined by xmin,...,ymax; The size of
    //D is shrunk by multiplying with epsilon = 0.1; Precondition:
    //old_point is contained in the box and the box is not equal to old_point.
    public static DPoint choose_distinct_random_point_in_disque(
            DPoint old_point,
            double xmin,
            double xmax,
            double ymin,
            double ymax,
            double zmin,
            double zmax)
    {
        double mindist;//minimal distance from old_point to the boundaries of the disc
        double mindist_to_xmin, mindist_to_xmax, mindist_to_ymin, mindist_to_ymax, mindist_to_zmin, mindist_to_zmax;
        double rand_x, rand_y, rand_z;
        DPoint new_point = PointFactory.INSTANCE.newDPoint();

        mindist_to_xmin = old_point.getX() - xmin;
        mindist_to_xmax = xmax - old_point.getX();
        mindist_to_ymin = old_point.getY() - ymin;
        mindist_to_ymax = ymax - old_point.getY();
        mindist_to_zmin = old_point.getZ() - zmin;
        mindist_to_zmax = zmax - old_point.getZ();

        mindist = Math.min(
                Math.min(Math.min(mindist_to_xmin, mindist_to_xmax), Math.min(mindist_to_ymin, mindist_to_ymax)),
                Math.min(mindist_to_zmin, mindist_to_zmax));

        if (mindist > 0)
        {
            do
            {
                //assign random double values in range (-1,1)
                rand_x = (random.nextDouble() - 0.5) * 2.0;
                rand_y = (random.nextDouble() - 0.5) * 2.0;
                rand_z = (random.nextDouble() - 0.5) * 2.0;
                new_point.setX(old_point.getX() + mindist * rand_x * epsilon);
                new_point.setY(old_point.getY() + mindist * rand_y * epsilon);
                new_point.setZ(old_point.getZ() + mindist * rand_z * epsilon);
            } while (old_point.equals(new_point) ||
                    (old_point.minus(new_point).norm() >= mindist * epsilon));
        }
        else if (mindist == 0) //old_point lies at the boundaries
        {//else1
            double mindist_x = 0;
            double mindist_y = 0;
            double mindist_z = 0;

            if (mindist_to_xmin > 0)
            {
                mindist_x = (-1) * mindist_to_xmin;
            }
            else if (mindist_to_xmax > 0)
            {
                mindist_x = mindist_to_xmax;
            }

            if (mindist_to_ymin > 0)
            {
                mindist_y = (-1) * mindist_to_ymin;
            }
            else if (mindist_to_ymax > 0)
            {
                mindist_y = mindist_to_ymax;
            }

            if (mindist_to_zmin > 0)
            {
                mindist_z = (-1) * mindist_to_zmin;
            }
            else if (mindist_to_zmax > 0)
            {
                mindist_z = mindist_to_zmax;
            }

            if ((mindist_x != 0) || (mindist_y != 0) || (mindist_z != 0))
            {
                do
                {
                    //assign random double values in range (0,1)
                    rand_x = random.nextDouble();
                    rand_y = random.nextDouble();
                    rand_z = random.nextDouble();
                    new_point.setX(old_point.getX() + mindist_x * rand_x * epsilon);
                    new_point.setY(old_point.getY() + mindist_y * rand_y * epsilon);
                    new_point.setZ(old_point.getZ() + mindist_z * rand_z * epsilon);
                } while (old_point == new_point);
            }
            else if (DEBUG_BUILD)
            {
                println("Error DIM2:: box is equal to old_pos");
            }
        }//else1
        else if (DEBUG_BUILD)//mindist < 0
        {//else2
            println("Error DIM2:: choose_distinct_random_point_in_disque: old_point not ");
            println("in box");
        }//else2

        return new_point;
    }

    //A random point (distinct from old_pos) on the disque around old_pos with
    //radius epsilon = 0.1 is computed.
    public static DPoint choose_distinct_random_point_in_radius_epsilon(DPoint old_pos)
    {
        double xmin = old_pos.getX() - 1 * epsilon;
        double xmax = old_pos.getX() + 1 * epsilon;
        double ymin = old_pos.getY() - 1 * epsilon;
        double ymax = old_pos.getY() + 1 * epsilon;
        double zmin = old_pos.getZ() - 1 * epsilon;
        double zmax = old_pos.getZ() + 1 * epsilon;

        return choose_distinct_random_point_in_disque(old_pos, xmin, xmax, ymin, ymax, zmin, zmax);
    }

    //If distance has a value near the machine precision the repulsive force calculation
    //is not possible (calculated values exceed the machine accuracy) in this cases
    //true is returned and force is set to a reasonable value that does
    //not cause problems; Else false is returned and force keeps unchanged.
    public static boolean f_rep_near_machine_precision(double distance, DPoint force)
    {
        double POS_BIG_LIMIT = POS_BIG_DOUBLE * 1e-190;
        double POS_SMALL_LIMIT = POS_SMALL_DOUBLE * 1e190;

        if (distance > POS_BIG_LIMIT)
        {
            //create random number in range (0,1)
            double randx = random.nextDouble();
            double randy = random.nextDouble();
            double randz = random.nextDouble();
            int rand_sign_x = random.nextInt(2);
            int rand_sign_y = random.nextInt(2);
            int rand_sign_z = random.nextInt(2);
            force.setX(POS_SMALL_LIMIT * (1 + randx) * Math.pow(-1.0, rand_sign_x));
            force.setY(POS_SMALL_LIMIT * (1 + randy) * Math.pow(-1.0, rand_sign_y));
            force.setZ(POS_SMALL_LIMIT * (1 + randz) * Math.pow(-1.0, rand_sign_z));
            return true;

        }
        else if (distance < POS_SMALL_LIMIT)
        {
            //create random number in range (0,1)
            double randx = random.nextDouble();
            double randy = random.nextDouble();
            double randz = random.nextDouble();
            int rand_sign_x = random.nextInt(2);
            int rand_sign_y = random.nextInt(2);
            int rand_sign_z = random.nextInt(2);
            force.setX(POS_BIG_LIMIT * randx * Math.pow(-1.0, rand_sign_x));
            force.setY(POS_BIG_LIMIT * randy * Math.pow(-1.0, rand_sign_y));
            force.setZ(POS_BIG_LIMIT * randz * Math.pow(-1.0, rand_sign_z));
            return true;

        }
        else
        {
            return false;
        }
    }

    //If distance has a value near the machine precision the (attractive)force
    //calculation is not possible (calculated values exceed the machine accuracy) in
    //this cases true is returned and force is set to a reasonable value that does
    //not cause problems; Else false is returned and force keeps unchanged.
    public static boolean f_near_machine_precision(double distance, DPoint force)
    {
        double POS_BIG_LIMIT = POS_BIG_DOUBLE * 1e-190;
        double POS_SMALL_LIMIT = POS_SMALL_DOUBLE * 1e190;

        if (distance < POS_SMALL_LIMIT)
        {
            //create random number in range (0,1)
            double randx = random.nextDouble();
            double randy = random.nextDouble();
            double randz = random.nextDouble();
            int rand_sign_x = random.nextInt(2);
            int rand_sign_y = random.nextInt(2);
            int rand_sign_z = random.nextInt(2);
            force.setX(POS_SMALL_LIMIT * (1 + randx) * Math.pow(-1.0, rand_sign_x));
            force.setY(POS_SMALL_LIMIT * (1 + randy) * Math.pow(-1.0, rand_sign_y));
            force.setZ(POS_SMALL_LIMIT * (1 + randz) * Math.pow(-1.0, rand_sign_z));
            return true;

        }
        else if (distance > POS_BIG_LIMIT)
        {
            //create random number in range (0,1)
            double randx = random.nextDouble();
            double randy = random.nextDouble();
            double randz = random.nextDouble();
            int rand_sign_x = random.nextInt(2);
            int rand_sign_y = random.nextInt(2);
            int rand_sign_z = random.nextInt(2);
            force.setX(POS_BIG_LIMIT * randx * Math.pow(-1.0, rand_sign_x));
            force.setY(POS_BIG_LIMIT * randy * Math.pow(-1.0, rand_sign_y));
            force.setZ(POS_BIG_LIMIT * randz * Math.pow(-1.0, rand_sign_z));
            return true;

        }
        else
        {
            return false;
        }
    }

    //Returns true if a is "nearly" equal to b (needed, when machine accuracy is
    //insufficient in functions well_seperated and bordering of NMM)
    public static boolean nearly_equal(double a, double b)
    {
        double delta = 1e-10;
        double small_b, big_b;

        if (b > 0)
        {
            small_b = b * (1 - delta);
            big_b = b * (1 + delta);

        }
        else //b <= 0
        {
            small_b = b * (1 + delta);
            big_b = b * (1 - delta);
        }

        if ((small_b <= a) && (a <= big_b))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
