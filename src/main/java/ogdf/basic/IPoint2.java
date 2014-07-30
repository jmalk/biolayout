package ogdf.basic;

public class IPoint2 implements IPoint
{
    public int m_x; //!< The x-coordinate.
    public int m_y; //!< The y-coordinate.

    //! Creates a real point (0,0).
    public IPoint2()
    {
        this(0, 0);
    }

    //! Creates a real point (\a x,\a y).
    public IPoint2(int x, int y)
    {
        m_x = x;
        m_y = y;
    }

    //! Copy constructor.
    public IPoint2(IPoint2 p)
    {
        this(p.m_x, p.m_y);
    }

    //! Relaxed equality operator.
    @Override
    public boolean equals(IPoint p)
    {
        IPoint2 p2 = (IPoint2)p;
        return m_x == p2.m_x && m_y == p2.m_y;
    }

    //! Returns the norm of the point.
    @Override
    public double norm()
    {
        return java.lang.Math.sqrt(m_x * m_x + m_y * m_y);
    }

    // gives the euclidean distance between p and *this
    @Override
    public double distance(IPoint p)
    {
        IPoint2 p2 = (IPoint2)p;
        double dx = p2.m_x - m_x;
        double dy = p2.m_y - m_y;
        return java.lang.Math.sqrt((dx * dx) + (dy * dy));
    }

    // adds p to *this
    @Override
    public IPoint2 plus(IPoint p)
    {
        IPoint2 p2 = (IPoint2)p;
        return new IPoint2(m_x + p2.m_x, m_y + p2.m_y);
    }

    // subtracts p from *this
    @Override
    public IPoint2 minus(IPoint p)
    {
        IPoint2 p2 = (IPoint2)p;
        return new IPoint2(m_x - p2.m_x, m_y - p2.m_y);
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
        return 0;
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
        //NOP
    }
}