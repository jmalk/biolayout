package org.BioLayoutExpress3D.Utils;

/**
 *
 * @author Tim Angus <tim.angus@roslin.ed.ac.uk>
 */
final public class Complex
{
    private final double real;
    public double r() { return real; }
    private final double imaginary;
    public double i() { return imaginary; }

    public Complex()
    {
        this(0.0, 0.0);
    }

    public Complex(double r)
    {
        this(r, 0.0);
    }

    public Complex(double r, double i)
    {
        this.real = r;
        this.imaginary = i;
    }

    public Complex(Complex other)
    {
        this.real = other.real;
        this.imaginary = other.imaginary;
    }

    public Complex plus(Complex other)
    {
        return new Complex(this.real + other.real, this.imaginary + other.imaginary);
    }

    public Complex plus(double s)
    {
        return this.plus(new Complex(s, 0));
    }

    public Complex minus(Complex other)
    {
        return new Complex(this.real - other.real, this.imaginary - other.imaginary);
    }

    public Complex minus(double s)
    {
        return this.minus(new Complex(s, 0));
    }

    public Complex multipliedBy(Complex other)
    {
        return new Complex(
                (this.real * other.real) - (this.imaginary * other.imaginary),
                (this.real * other.imaginary) + (this.imaginary * other.real));
    }

    public Complex multipliedBy(double s)
    {
        return this.multipliedBy(new Complex(s, 0));
    }

    public Complex dividedBy(Complex other)
    {
        double a = this.real;
        double b = this.imaginary;
        double c = other.real;
        double d = other.imaginary;

        return new Complex(
                ((a * c) + (b * d)) / ((c * c) + (d * d)),
                ((b * c) - (a * d)) / ((c * c) + (d * d)));
    }

    public Complex dividedBy(double s)
    {
        return multipliedBy(1.0 / s);
    }

    public Complex log()
    {
        double rpart = 0.5 * Math.log(this.real * this.real + this.imaginary * this.imaginary);
        double ipart = Math.atan2(this.imaginary, this.real);
        if (ipart > Math.PI)
        {
            ipart = ipart - 2.0 * Math.PI;
        }

        return new Complex(rpart, ipart);
    }
}
