package ogdf.basic;

public interface IPoint
{
    public boolean equals(IPoint p);
    public double norm();
    public double distance(IPoint p);
    public IPoint plus(IPoint p);
    public IPoint minus(IPoint p);
    public int getX();
    public int getY();
    public int getZ();
    public void setX(int x);
    public void setY(int y);
    public void setZ(int z);
}