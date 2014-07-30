package ogdf.basic;

public class DPoint3 implements DPoint
{
    public double m_x; //!< The x-coordinate.
    public double m_y; //!< The y-coordinate.
    public double m_z; //!< The z-coordinate.

    //! Creates a real point (0,0).
    public DPoint3()
    {
        this(0.0, 0.0, 0.0);
    }

    //! Creates a real point (\a x,\a y).
    public DPoint3(double x, double y, double z)
    {
        m_x = x;
        m_y = y;
        m_z = z;
    }

    //! Copy constructor.
    public DPoint3(DPoint3 dp)
    {
        this(dp.m_x, dp.m_y, dp.m_z);
    }

    //! Relaxed equality operator.
    @Override
    public boolean equals(DPoint p)
    {
        DPoint3 p3 = (DPoint3)p;
        return DIsEqual(m_x, p3.m_x) && DIsEqual(m_y, p3.m_y) && DIsEqual(m_z, p3.m_z);
    }

    //! Returns the norm of the point.
    @Override
    public double norm()
    {
        return java.lang.Math.sqrt(m_x * m_x + m_y * m_y + m_z * m_z);
    }

    // gives the euclidean distance between p and *this
    @Override
    public double distance(DPoint p)
    {
        DPoint3 p3 = (DPoint3)p;
        double dx = p3.m_x - m_x;
        double dy = p3.m_y - m_y;
        double dz = p3.m_z - m_z;
        return java.lang.Math.sqrt((dx * dx) + (dy * dy) + (dz * dz));
    }

    // adds p to *this
    @Override
    public DPoint plus(DPoint p)
    {
        DPoint3 p3 = (DPoint3)p;
        return new DPoint3(m_x + p3.m_x, m_y + p3.m_y, m_z + p3.m_z);
    }

    // subtracts p from *this
    @Override
    public DPoint minus(DPoint p)
    {
        DPoint3 p3 = (DPoint3)p;
        return new DPoint3(m_x - p3.m_x, m_y - p3.m_y, m_z - p3.m_z);
    }

    @Override
    public DPoint scaled(double s)
    {
        return new DPoint3(m_x * s, m_y * s, m_z * s);
    }

    @Override
    public double dot(DPoint p)
    {
        DPoint3 p3 = (DPoint3)p;
        return m_x * p3.m_x + m_y * p3.m_y + m_z * p3.m_z;
    }

    @Override
    public double angle(DPoint p)
    {
        return Math.acos(this.dot(p) / (this.norm() * p.norm()));
    }

    @Override
    public DPoint rotate90InZPlane()
    {
        return new DPoint3(-m_y, m_x, m_z);
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
        return m_z;
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
        m_z = z;
    }

    final static double OGDF_GEOM_EPS = 1e-06;

    public static boolean DIsEqual(double a, double b)
    {
        return DPoint2.DIsEqual(a, b);
    }

    public static boolean DIsGreater(double a, double b)
    {
        return DPoint2.DIsGreater(a, b);
    }

    public static boolean DIsGreaterEqual(double a, double b)
    {
        return DPoint2.DIsGreaterEqual(a, b);
    }

    public static boolean DIsLess(double a, double b)
    {
        return DPoint2.DIsLess(a, b);
    }

    public static boolean DIsLessEqual(double a, double b)
    {
        return DPoint2.DIsLessEqual(a, b);
    }
}