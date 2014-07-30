package ogdf.basic;

import ogdf.basic.DPoint2;

/**
 * \brief Lines with real coordinates.
 */
public class DLine
{

    DPoint2 m_start; //!< The start point of the line.
    DPoint2 m_end;   //!< The end point of the line.

    //! Creates an empty line.
    public DLine()
    {
        this(new DPoint2(), new DPoint2());
    }

    //! Creates a line with start point \a p1 and end point \a p2.
    public DLine(DPoint2 p1, DPoint2 p2)
    {
        m_start = new DPoint2(p1);
        m_end = new DPoint2(p2);
    }

    //! Copy constructor.
    public DLine(DLine dl)
    {
        this(dl.m_start, dl.m_end);
    }

    //! Creates a line with start point (\a x1,\a y1) and end point (\a x2,\a y2).
    public DLine(double x1, double y1, double x2, double y2)
    {
        m_start.m_x = x1;
        m_start.m_y = y1;
        m_end.m_x = x2;
        m_end.m_y = y2;
    }

    //! Equality operator.
    boolean equals(DLine dl)
    {
        return m_start.equals(dl.m_start) && m_end.equals(dl.m_end);
    }

    //! Returns the start point of the line.
    public DPoint2 start()
    {
        return new DPoint2(m_start);
    }

    //! Returns the end point of the line.
    public DPoint2 end()
    {
        return new DPoint2(m_end);
    }

    //! Returns the x-coordinate of the difference (end point - start point).
    public double dx()
    {
        return m_end.m_x - m_start.m_x;
    }

    //! Returns the y-coordinate of the difference (end point - start point).
    public double dy()
    {
        return m_end.m_y - m_start.m_y;
    }

    //! Returns the slope of the line.
    public double slope()
    {
        return (dx() == 0) ? Double.MAX_VALUE : dy() / dx();
    }

    //! Returns the value y' such that (0,y') lies on the unlimited straight-line define dby this line.
    public double yAbs()
    {
        return (dx() == 0) ? Double.MAX_VALUE : m_start.m_y - (slope() * m_start.m_x);
    }

    //! Returns true iff this line runs vertically.
    public boolean isVertical()
    {
        return (DPoint2.DIsEqual(dx(), 0.0));
    }

    //! Returns true iff this line runs horizontally.
    public boolean isHorizontal()
    {
        return (DPoint2.DIsEqual(dy(), 0.0));
    }

    /**
     * \brief Returns true iff \a line and this line intersect.
     *
     * @param line is the second line.
     * @param inter is assigned the intersection point if true is returned.
     * @param endpoints determines if common endpoints are treated as intersection.
     */
    public boolean intersection(DLine line, DPoint2 inter)
    {
        final boolean endpoints = true;
        double ix, iy;

        //do not return true if parallel edges are encountered
        if (slope() == line.slope())
        {
            return false;
        }

        //two possible checks:
        // only check for overlap on endpoints if option parameter set,
        // compute crossing otherwise
        // or skip computation if endpoints overlap (can't have "real" crossing)
        // (currently implemented)
        //if (endpoints) {

        if (m_start.equals(line.m_start) || m_start.equals(line.m_end))
        {
            inter = m_start;
            if (endpoints)
            {
                return true;
            }
            else
            {
                return false;
            }
        }

        if (m_end.equals(line.m_start) || m_end.equals(line.m_end))
        {
            inter = m_end;
            if (endpoints)
            {
                return true;
            }
            else
            {
                return false;
            }
        }

        //}//if endpoints

        //if the edge is vertical, we cannot compute the slope
        if (isVertical())
        {
            ix = m_start.m_x;
        }
        else if (line.isVertical())
        {
            ix = line.m_start.m_x;
        }
        else
        {
            ix = (line.yAbs() - yAbs()) / (slope() - line.slope());
        }

        //set iy to the value of the infinite line at xvalue ix
        //use a non-vertical line (can't be both, otherwise they're parallel)
        if (isVertical())
        {
            iy = line.slope() * ix + line.yAbs();
        }
        else
        {
            iy = slope() * ix + yAbs();
        }

        inter = new DPoint2(ix, iy); //the (infinite) lines cross point

        DRect tRect = new DRect(line);
        DRect mRect = new DRect(this);

        return (tRect.contains(inter) && mRect.contains(inter));
    }

    //! Returns true iff \a p lie on this line.
    public boolean contains(DPoint2 p)
    {
        if (p == start() || p == end())
        {
            return true;
        }

        // check, if outside rect
        DRect r = new DRect(start(), end());
        if (!r.contains(p))
        {
            return false;
        }

        if (dx() == 0.0)
        { // first check, if line is vertical
            if (DPoint2.DIsEqual(p.m_x, start().m_x) &&
                    DPoint2.DIsLessEqual(p.m_y, (Math.max(start().m_y, end().m_y))) &&
                    DPoint2.DIsGreaterEqual(p.m_y, (Math.min(start().m_y, end().m_y))))
            {
                return true;
            }
            return false;
        }

        double dx2p = p.m_x - start().m_x;
        double dy2p = p.m_y - start().m_y;

        if (dx2p == 0.0) // dx() != 0.0, already checked
        {
            return false;
        }

        if (DPoint2.DIsEqual(slope(), (dy2p / dx2p)))
        {
            return true;
        }
        return false;
    }

    //! Returns the length (euclidean distance between start and edn point) of this line.
    public double length()
    {
        return m_start.distance(m_end);
    }

    /**
     * \brief Computes the intersection between this line and the horizontal line through y = \a horAxis.
     *
     * @param horAxis defines the horizontal line.
     * @param crossing is assigned the x-coordinate of the intersection point.
     *
     * \return the number of intersection points (0 = none, 1 = one, 2 = this line lies on the horizontal line through y
     * = \a horAxis).
     */
    public int horIntersection(double horAxis, double crossing)
    {
        if (dy() == 0.0)
        {
            crossing = 0.0;
            if (m_start.m_y == horAxis)
            {
                return 2;
            }
            else
            {
                return 0;
            }
        }
        if (Math.min(m_start.m_y, m_end.m_y) <= horAxis && Math.max(m_start.m_y, m_end.m_y) >= horAxis)
        {
            crossing = (m_start.m_x * (m_end.m_y - horAxis) -
                    m_end.m_x * (m_start.m_y - horAxis)) / dy();
            return 1;
        }
        else
        {
            crossing = 0.0;
            return 0;
        }
    }

    // gives the intersection with the vertical axis 'verAxis', returns the number of intersections
    // 0 = no, 1 = one, 2 = infinity or both end-points, e.g. parallel on this axis
    /**
     * \brief Computes the intersection between this line and the vertical line through x = \a verAxis.
     *
     * @param verAxis defines the vertical line.
     * @param crossing is assigned the y-coordinate of the intersection point.
     *
     * \return the number of intersection points (0 = none, 1 = one, 2 = this line lies on the vertical line through x =
     * \a verAxis).
     */
    public int verIntersection(double verAxis, double crossing)
    {
        if (dx() == 0.0)
        {
            crossing = 0.0;
            if (m_start.m_x == verAxis)
            {
                return 2;
            }
            else
            {
                return 0;
            }
        }
        if (Math.min(m_start.m_x, m_end.m_x) <= verAxis && Math.max(m_start.m_x, m_end.m_x) >= verAxis)
        {
            crossing = (m_start.m_y * (m_end.m_x - verAxis) -
                    m_end.m_y * (m_start.m_x - verAxis)) / dx();
            return 1;
        }
        else
        {
            crossing = 0.0;
            return 0;
        }
    }
}
