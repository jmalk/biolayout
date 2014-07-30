package ogdf.basic;

import ogdf.basic.DPoint2;

/**
 * \brief Rectangles with real coordinates.
 */
class DRect
{
    DPoint2 m_p1; //!< The lower left point of the rectangle.
    DPoint2 m_p2; //!< The upper right point of the rectangle.

    //! Creates a rectangle with lower left and upper right point (0,0).
    public DRect()
    {
        this(new DPoint2(), new DPoint2());
    }

    //! Creates a rectangle with lower left point \a p1 and upper right point \a p2.
    public DRect(DPoint2 p1, DPoint2 p2)
    {
        m_p1 = new DPoint2(p1);
        m_p2 = new DPoint2(p2);
        normalize();
    }

    //! Creates a rectangle with lower left point (\a x1,\a y1) and upper right point (\a x1,\a y2).
    public DRect(double x1, double y1, double x2, double y2)
    {
        m_p1.m_x = x1;
        m_p1.m_y = y1;
        m_p2.m_x = x2;
        m_p2.m_y = y2;
        normalize();
    }

    //! Creates a rectangle defined by the end points of line \a dl.
    public DRect(DLine dl)
    {
        this(dl.start(), dl.end());
    }

    //! Copy constructor.
    public DRect(DRect dr)
    {
        this(dr.m_p1, dr.m_p2);
    }

    //! Equality operator.
    public boolean equals(DRect dr)
    {
        return m_p1.equals(dr.m_p1) && m_p2.equals(dr.m_p2);
    }

    //! Returns the width of the rectangle.
    public double width()
    {
        return m_p2.m_x - m_p1.m_x;
    }

    //! Returns the height of the rectangle.
    public double height()
    {
        return m_p2.m_y - m_p1.m_y;
    }

    /**
     * \brief Normalizes the rectangle.
     *
     * Makes sure that the lower left point lies below and left of the upper right point.
     */
    public void normalize()
    {
        if (width() < 0)
        {
            double temp = m_p2.m_x; m_p2.m_x = m_p1.m_x; m_p1.m_x = temp; // swap
        }
        if (height() < 0)
        {
            double temp = m_p2.m_y; m_p2.m_y = m_p1.m_y; m_p1.m_y = temp; // swap
        }
    }

    //! Returns the lower left point of the rectangle.
    public DPoint2 p1()
    {
        return m_p1;
    }
    //! Returns the upper right point of the rectangle.
    public DPoint2 p2()
    {
        return m_p2;
    }

    //! Returns the top side of the rectangle.
    public DLine topLine()
    {
        return new DLine(new DPoint2(m_p1.m_x, m_p2.m_y), new DPoint2(m_p2.m_x, m_p2.m_y));
    }

    //! Returns the right side of the rectangle.
    public DLine rightLine()
    {
        return new DLine(new DPoint2(m_p2.m_x, m_p2.m_y), new DPoint2(m_p2.m_x, m_p1.m_y));
    }

    //! Returns the left side of the rectangle.
    public DLine leftLine()
    {
        return new DLine(new DPoint2(m_p1.m_x, m_p1.m_y), new DPoint2(m_p1.m_x, m_p2.m_y));
    }

    //! Returns the bottom side of the rectangle.
    public DLine bottomLine()
    {
        return new DLine(new DPoint2(m_p2.m_x, m_p1.m_y), new DPoint2(m_p1.m_x, m_p1.m_y));
    }

    //! Swaps the y-coordinates of the two points.
    public void yInvert()
    {
        double temp = m_p1.m_y; m_p1.m_y = m_p2.m_y; m_p2.m_y = temp; // swap
    }

    //! Swaps the x-coordinates of the two points.
    public void xInvert()
    {
        double temp = m_p1.m_x; m_p1.m_x = m_p2.m_x; m_p2.m_x = temp; // swap
    }

    //! Returns true iff \a p lies within this rectangle.
    public boolean contains(DPoint2 p)
    {
        if (DPoint2.DIsLess(p.m_x, m_p1.m_x) ||
                DPoint2.DIsGreater(p.m_x, m_p2.m_x) ||
                DPoint2.DIsLess(p.m_y, m_p1.m_y) ||
                DPoint2.DIsGreater(p.m_y, m_p2.m_y))
        {
            return false;
        }
        return true;
    }
}
