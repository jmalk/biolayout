package org.BioLayoutExpress3D.Math3DTransformations;

/**
*
* Matrix3D is a class encapsulating operations for a 3x3 3D matrix.
*
* @author Thanos Theo, Michael Kargas, 2010
* @version 3.0.0.0
*/

public class Matrix3D
{

    /**
    *  The float 2D array variable of the matrix.
    */
    public float[][] matrix3D = null;

    /**
    *  The first constructor of the Matrix3D class.
    */
    public Matrix3D()
    {
        matrix3D = new float[3][3];
    }

    /**
    *  The second constructor of the Matrix3D class.
    */
    public Matrix3D(float e11, float e12, float e13,
                    float e21, float e22, float e23,
                    float e31, float e32, float e33)
    {
        matrix3D = new float[][]{ {e11, e12, e13},
                                  {e21, e22, e23},
                                  {e31, e32, e33} };
    }

    /**
    *  Sets all matrix elements to zero.
    */
    public final void setAllZeros()
    {
        for (int j = 0; j < 3; j++)
            for (int i = 0; i < 3; i++)
                matrix3D[i][j] = 0.0f;
    }

    /**
    *  Sets all matrix elements to one.
    */
    public final void setAllOnes()
    {
        for (int j = 0; j < 3; j++)
            for (int i = 0; i < 3; i++)
                matrix3D[i][j] = 1.0f;
    }

    /**
    *  Sets the identity matrix.
    */
    public final void setIdentity()
    {
        for (int j = 0; j < 3; j++)
            for (int i = 0; i < 3; i++)
                if (i == j)
                    matrix3D[i][j] = 1.0f;
                else
                    matrix3D[i][j] = 0.0f;
    }

    /**
    *  Matrix addition operation.
    */
    public final Matrix3D addWithMatrix3D(Matrix3D matrix3D)
    {
        Matrix3D newMatrix3D = new Matrix3D();
        for (int j = 0; j < 3; j++)
            for (int i = 0; i < 3; i++)
                newMatrix3D.matrix3D[i][j] = this.matrix3D[i][j] + matrix3D.matrix3D[i][j];

        return newMatrix3D;
    }

    /**
    *  Matrix by matrix multiplication operation.
    */
    public final Matrix3D multiplyWithMatrix3D(Matrix3D matrix3D)
    {
        Matrix3D newMatrix3D = new Matrix3D();
        for (int j = 0; j < 3; j++)
            for (int i = 0; i < 3; i++)
                for (int k = 0; k < 3; k++)
                    newMatrix3D.matrix3D[i][j] += this.matrix3D[i][k] * matrix3D.matrix3D[k][j];

        return newMatrix3D;
    }

    /**
    *  Matrix by vector multiplication operation.
    */
    public final Vector3D multiplyWithVector3D(Vector3D vector3D)
    {
        Vector3D newVector3D = new Vector3D();
        newVector3D.x = matrix3D[0][0] * vector3D.x + matrix3D[0][1] * vector3D.y + matrix3D[0][2] * vector3D.z;
        newVector3D.y = matrix3D[1][0] * vector3D.x + matrix3D[1][1] * vector3D.y + matrix3D[1][2] * vector3D.z;
        newVector3D.z = matrix3D[2][0] * vector3D.x + matrix3D[2][1] * vector3D.y + matrix3D[2][2] * vector3D.z;

        return newVector3D;
    }

    /**
    *  Overriden toString() method for the matrix.
    */
    @Override
    public String toString()
    {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < 3; i++)
            output.append( String.format("%f   %f   %f\n", matrix3D[i][0], matrix3D[i][1], matrix3D[i][2]) );
        output.append("\n");

        return output.toString();
    }


}