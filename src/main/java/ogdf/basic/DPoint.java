package ogdf.basic;

public interface DPoint
{
    public boolean equals(DPoint p);
    public double norm();
    public double distance(DPoint p);
    public DPoint plus(DPoint p);
    public DPoint minus(DPoint p);
    public DPoint scaled(double s);
    public double dot(DPoint p);
    public double angle(DPoint p);
    public DPoint rotate90InZPlane();
    public double getX();
    public double getY();
    public double getZ();
    public void setX(double x);
    public void setY(double y);
    public void setZ(double z);
}