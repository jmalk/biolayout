package ogdf.basic;

public class DPoint2 implements DPoint
{
    public double m_x; //!< The x-coordinate.
    public double m_y; //!< The y-coordinate.

    //! Creates a real point (0,0).
    public DPoint2()
    {
        this(0.0, 0.0);
    }

    //! Creates a real point (\a x,\a y).
    public DPoint2(double x, double y)
    {
        m_x = x;
        m_y = y;
    }

    //! Copy constructor.
    public DPoint2(DPoint2 dp)
    {
        this(dp.m_x, dp.m_y);
    }

    //! Relaxed equality operator.
    @Override
    public boolean equals(DPoint p)
    {
        DPoint2 p2 = (DPoint2)p;
        return DIsEqual(m_x, p2.m_x) && DIsEqual(m_y, p2.m_y);
    }

    //! Returns the norm of the point.
    @Override
    public double norm()
    {
        return java.lang.Math.sqrt(m_x * m_x + m_y * m_y);
    }

    // gives the euclidean distance between p and *this
    @Override
    public double distance(DPoint p)
    {
        DPoint2 p2 = (DPoint2)p;
        double dx = p2.m_x - m_x;
        double dy = p2.m_y - m_y;
        return java.lang.Math.sqrt((dx * dx) + (dy * dy));
    }

    // adds p to *this
    @Override
    public DPoint plus(DPoint p)
    {
        DPoint2 p2 = (DPoint2)p;
        return new DPoint2(m_x + p2.m_x, m_y + p2.m_y);
    }

    // subtracts p from *this
    @Override
    public DPoint minus(DPoint p)
    {
        DPoint2 p2 = (DPoint2)p;
        return new DPoint2(m_x - p2.m_x, m_y - p2.m_y);
    }

    @Override
    public DPoint scaled(double s)
    {
        return new DPoint2(m_x * s, m_y * s);
    }

    @Override
    public double dot(DPoint p)
    {
        DPoint2 p2 = (DPoint2)p;
        return m_x * p2.m_x + m_y * p2.m_y;
    }

    @Override
    public double angle(DPoint p)
    {
        return Math.acos(this.dot(p) / (this.norm() * p.norm()));
    }

    @Override
    public DPoint rotate90InZPlane()
    {
        return new DPoint2(-m_y, m_x);
    }

    @Override
    public double getX()
    {
        return m_x;
    }

    @Override
    public double getY()
    {
        return m_y;
    }

    @Override
    public double getZ()
    {
        return 0.0;
    }

    @Override
    public void setX(double x)
    {
        m_x = x;
    }

    @Override
    public void setY(double y)
    {
        m_y = y;
    }

    @Override
    public void setZ(double z)
    {
        //NOP
    }

    final static double OGDF_GEOM_EPS = 1e-06;

    public static boolean DIsEqual(double a, double b)
    {
        return (a < (b + OGDF_GEOM_EPS) && a > (b - OGDF_GEOM_EPS));
    }

    public static boolean DIsGreater(double a, double b)
    {
        return (a > (b + OGDF_GEOM_EPS));
    }

    public static boolean DIsGreaterEqual(double a, double b)
    {
        return (a > (b - OGDF_GEOM_EPS));
    }

    public static boolean DIsLess(double a, double b)
    {
        return (a < (b - OGDF_GEOM_EPS));
    }

    public static boolean DIsLessEqual(double a, double b)
    {
        return (a < (b + OGDF_GEOM_EPS));
    }
}