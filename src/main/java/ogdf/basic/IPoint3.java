package ogdf.basic;

public class IPoint3 implements IPoint
{
    public int m_x; //!< The x-coordinate.
    public int m_y; //!< The y-coordinate.
    public int m_z; //!< The y-coordinate.

    //! Creates a real point (0,0).
    public IPoint3()
    {
        this(0, 0, 0);
    }

    //! Creates a real point (\a x,\a y,\a z).
    public IPoint3(int x, int y, int z)
    {
        m_x = x;
        m_y = y;
        m_z = z;
    }

    //! Copy constructor.
    public IPoint3(IPoint3 p)
    {
        this(p.m_x, p.m_y, p.m_z);
    }

    //! Relaxed equality operator.
    @Override
    public boolean equals(IPoint p)
    {
        IPoint3 p3 = (IPoint3)p;
        return m_x == p3.m_x && m_y == p3.m_y && m_z == p3.m_z;
    }

    //! Returns the norm of the point.
    @Override
    public double norm()
    {
        return java.lang.Math.sqrt(m_x * m_x + m_y * m_y + m_z * m_z);
    }

    // gives the euclidean distance between p and *this
    @Override
    public double distance(IPoint p)
    {
        IPoint3 p3 = (IPoint3)p;
        double dx = p3.m_x - m_x;
        double dy = p3.m_y - m_y;
        double dz = p3.m_y - m_y;
        return java.lang.Math.sqrt((dx * dx) + (dy * dy) + (dz * dz));
    }

    // adds p to *this
    @Override
    public IPoint plus(IPoint p)
    {
        IPoint3 p3 = (IPoint3)p;
        return new IPoint3(m_x + p3.m_x, m_y + p3.m_y, m_z + p3.m_z);
    }

    // subtracts p from *this
    @Override
    public IPoint minus(IPoint p)
    {
        IPoint3 p3 = (IPoint3)p;
        return new IPoint3(m_x - p3.m_x, m_y - p3.m_y, m_z - p3.m_z);
    }

    @Override
    public int getX()
    {
        return m_x;
    }

    @Override
    public int getY()
    {
        return m_y;
    }

    @Override
    public int getZ()
    {
        return m_z;
    }

    @Override
    public void setX(int x)
    {
        m_x = x;
    }

    @Override
    public void setY(int y)
    {
        m_y = y;
    }

    @Override
    public void setZ(int z)
    {
        m_z = z;
    }
}