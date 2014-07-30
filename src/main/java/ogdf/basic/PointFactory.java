package ogdf.basic;

/**
 *
 * @author Tim Angus <tim.angus@roslin.ed.ac.uk>
 */
public enum PointFactory
{
    INSTANCE;

    public enum Dimensions
    {
        _2,
        _3
    }

    private Dimensions d = Dimensions._2;

    public void setDimensions(Dimensions d)
    {
        this.d = d;
    }

    public Dimensions dimensions()
    {
        return d;
    }

    public DPoint newDPoint()
    {
        switch (d)
        {
            default:
            case _2:
                return new DPoint2();

            case _3:
                return new DPoint3();
        }
    }

    public DPoint newDPoint(DPoint p)
    {
        switch (d)
        {
            default:
            case _2:
                return new DPoint2((DPoint2)p);

            case _3:
                return new DPoint3((DPoint3)p);
        }
    }

    public IPoint newIPoint()
    {
        switch (d)
        {
            default:
            case _2:
                return new IPoint2();

            case _3:
                return new IPoint3();
        }
    }

    public IPoint newIPoint(IPoint p)
    {
        switch (d)
        {
            default:
            case _2:
                return new IPoint2((IPoint2)p);

            case _3:
                return new IPoint3((IPoint3)p);
        }
    }
}
