package org.BioLayoutExpress3D.Graph;

import static java.lang.Math.*;
import org.BioLayoutExpress3D.Graph.Vector5D.*;

/**
*
* Matrix5D is a class encapsulating operations for a 5x5 matrix.
*
* It has been adapted from Matrix3D, originally by
* @author Thanos Theo, Michael Kargas, 2010
* @version 3.0.0.0
*
* by Joshua Malkinson, 2014
*/

public class Matrix5D
{

    /**
    *  The float 2D array variable of the matrix.
    */
    public float[][] matrix5D = null;

    /**
    *  The first constructor of the Matrix5D class.
    */
    public Matrix5D()
    {
        matrix5D = new float[5][5];
    }

    /**
    *  The second constructor of the Matrix5D class.
    */
    public Matrix5D(float e11, float e12, float e13, float e14, float e15,
                    float e21, float e22, float e23, float e24, float e25,
                    float e31, float e32, float e33, float e34, float e35,
		    float e41, float e42, float e43, float e44, float e45,
	    	    float e51, float e52, float e53, float e54, float e55)
    {
        matrix5D = new float[][]{ {e11, e12, e13, e14, e15},
                                  {e21, e22, e23, e24, e25},
                                  {e31, e32, e33, e34, e35},
				  {e41, e42, e43, e44, e45},
				  {e51, e52, e53, e54, e55}, };
    }

    /**
    *  Sets all matrix elements to zero.
    */
    public final void setAllZeros()
    {
        for (int j = 0; j < 5; j++)
            for (int i = 0; i < 5; i++)
                matrix5D[i][j] = 0.0f;
    }

    /**
    *  Sets all matrix elements to one.
    */
    public final void setAllOnes()
    {
        for (int j = 0; j < 5; j++)
            for (int i = 0; i < 5; i++)
                matrix5D[i][j] = 1.0f;
    }

    /**
    *  Sets the identity matrix.
    */
    public final void setIdentity()
    {
        for (int j = 0; j < 5; j++)
            for (int i = 0; i < 5; i++)
                if (i == j)
                    matrix5D[i][j] = 1.0f;
                else
                    matrix5D[i][j] = 0.0f;
    }

    /**
    *  Matrix addition operation.
    */
    public final Matrix5D addWithMatrix5D(Matrix5D matrix5D)
    {
        Matrix5D newMatrix5D = new Matrix5D();
        for (int j = 0; j < 5; j++)
            for (int i = 0; i < 5; i++)
                newMatrix5D.matrix5D[i][j] = this.matrix5D[i][j] + matrix5D.matrix5D[i][j];

        return newMatrix5D;
    }

    /**
    *  Matrix by matrix multiplication operation.
    */
    public final Matrix5D multiplyWithMatrix5D(Matrix5D matrix5D)
    {
        Matrix5D newMatrix5D = new Matrix5D();
        for (int j = 0; j < 5; j++)
            for (int i = 0; i < 5; i++)
                for (int k = 0; k < 5; k++)
                    newMatrix5D.matrix5D[i][j] += this.matrix5D[i][k] * matrix5D.matrix5D[k][j];

        return newMatrix5D;
    }

    /**
    *  Matrix by vector multiplication operation.
    */
    public final Vector5D multiplyWithVector5D(Vector5D vector5D)
    {
        Vector5D newVector5D = new Vector5D();
        newVector5D.a = matrix5D[0][0] * vector5D.a + matrix5D[0][1] * vector5D.b + matrix5D[0][2] * vector5D.c + matrix5D[0][3] * vector5D.d + matrix5D[0][4] * vector5D.e;
        newVector5D.b = matrix5D[1][0] * vector5D.a + matrix5D[1][1] * vector5D.b + matrix5D[1][2] * vector5D.c + matrix5D[1][3] * vector5D.d + matrix5D[1][4] * vector5D.e;
        newVector5D.c = matrix5D[2][0] * vector5D.a + matrix5D[2][1] * vector5D.b + matrix5D[2][2] * vector5D.c + matrix5D[2][3] * vector5D.d + matrix5D[2][4] * vector5D.e;
	newVector5D.d = matrix5D[3][0] * vector5D.a + matrix5D[3][1] * vector5D.b + matrix5D[3][2] * vector5D.c + matrix5D[3][3] * vector5D.d + matrix5D[3][4] * vector5D.e;
	newVector5D.e = matrix5D[4][0] * vector5D.a + matrix5D[4][1] * vector5D.b + matrix5D[4][2] * vector5D.c + matrix5D[4][3] * vector5D.d + matrix5D[4][4] * vector5D.e;

        return newVector5D;
    }

//TODO edit rotation functions
//TODO add new rotation functions

    public final void rotateAboutXY(float angle)
    {
	double angleDouble = (double)angle;
	double theta = toRadians(angleDouble);

	float cosTheta = (float)Math.cos(theta);
	float sinTheta = (float)Math.sin(theta);
	
	// Create a rotation matrix given the angle.	    
	Matrix5D rotateX = new Matrix5D ( 1.0f, 0.0f, 0.0f,     0.0f,        0.0f,
					  0.0f, 1.0f, 0.0f,     0.0f,        0.0f,
					  0.0f, 0.0f, cosTheta, -(sinTheta), 0.0f,
					  0.0f, 0.0f, sinTheta, cosTheta,    0.0f,
					  0.0f, 0.0f, 0.0f,     0.0f,        1.0f );

	// Create a new temporary matrix to hold the result of multiplying the rotation matrix by the current matrix.
	Matrix5D newMatrix5D = new Matrix5D();

	// Multiply the rotation matrix by the current matrix.
        for (int j = 0; j < 5; j++)
            for (int i = 0; i < 5; i++)
                for (int k = 0; k < 5; k++)
                    newMatrix5D.matrix5D[i][j] += rotateX.matrix5D[i][k] * this.matrix5D[k][j];

	// Set the current matrix equal to the result of the multiplication.
	this.matrix5D = newMatrix5D.matrix5D;
    }

    public final void rotateAboutXZ(float angle)
    {
	double angleDouble = (double)angle;
	double theta = toRadians(angleDouble);

	float cosTheta = (float)Math.cos(theta);
	float sinTheta = (float)Math.sin(theta);
	
	// Create a rotation matrix given the angle.	    
	Matrix5D rotateX = new Matrix5D ( 1.0f, 0.0f,     0.0f, 0.0f,        0.0f,
					  0.0f, cosTheta, 0.0f, -(sinTheta), 0.0f,
					  0.0f, 0.0f,     1.0f, 0.0f,        0.0f,
					  0.0f, sinTheta, 0.0f, cosTheta,    0.0f,
					  0.0f, 0.0f,     0.0f, 0.0f,        1.0f );
	// Create a new temporary matrix to hold the result of multiplying the rotation matrix by the current matrix.
	Matrix5D newMatrix5D = new Matrix5D();

	// Multiply the rotation matrix by the current matrix.
        for (int j = 0; j < 5; j++)
            for (int i = 0; i < 5; i++)
                for (int k = 0; k < 5; k++)
                    newMatrix5D.matrix5D[i][j] += rotateX.matrix5D[i][k] * this.matrix5D[k][j];

	// Set the current matrix equal to the result of the multiplication.
	this.matrix5D = newMatrix5D.matrix5D;
    }

    public final void rotateAboutXW(float angle)
    {
	double angleDouble = (double)angle;
	double theta = toRadians(angleDouble);

	float cosTheta = (float)Math.cos(theta);
	float sinTheta = (float)Math.sin(theta);
	
	// Create a rotation matrix given the angle.	    
	Matrix5D rotateY = new Matrix5D ( 1.0f, 0.0f,     0.0f,        0.0f, 0.0f,
					  0.0f, cosTheta, -(sinTheta), 0.0f, 0.0f,
					  0.0f, sinTheta, cosTheta,    0.0f, 0.0f,
					  0.0f, 0.0f,     0.0f,        1.0f, 0.0f,
					  0.0f, 0.0f,     0.0f,        0.0f, 1.0f );

	// Create a new temporary matrix to hold the result of multiplying the rotation matrix by the current matrix.
	Matrix5D newMatrix5D = new Matrix5D();

	// Multiply the rotation matrix by the current matrix.
        for (int j = 0; j < 5; j++)
            for (int i = 0; i < 5; i++)
                for (int k = 0; k < 5; k++)
                    newMatrix5D.matrix5D[i][j] += rotateY.matrix5D[i][k] * this.matrix5D[k][j];

	// Set the current matrix equal to the result of the multiplication.
	this.matrix5D = newMatrix5D.matrix5D;
    }

    public final void rotateAboutYZ(float angle)
    {
	double angleDouble = (double)angle;
	double theta = toRadians(angleDouble);

	float cosTheta = (float)Math.cos(theta);
	float sinTheta = (float)Math.sin(theta);
	
	// Create a rotation matrix given the angle.	    
	Matrix5D rotateZ = new Matrix5D ( cosTheta, 0.0f, 0.0f, -(sinTheta), 0.0f,
					  0.0f,     1.0f, 0.0f, 0.0f,        0.0f,
					  0.0f,     0.0f, 1.0f, 0.0f,        0.0f,
					  sinTheta, 0.0f, 0.0f, cosTheta,    0.0f,
					  0.0f,     0.0f, 0.0f, 0.0f,        1.0f );

	// Create a new temporary matrix to hold the result of multiplying the rotation matrix by the current matrix.
	Matrix5D newMatrix5D = new Matrix5D();

	// Multiply the rotation matrix by the current matrix.
        for (int j = 0; j < 5; j++)
            for (int i = 0; i < 5; i++)
                for (int k = 0; k < 5; k++)
                    newMatrix5D.matrix5D[i][j] += rotateZ.matrix5D[i][k] * this.matrix5D[k][j];

	// Set the current matrix equal to the result of the multiplication.
	this.matrix5D = newMatrix5D.matrix5D;
    }

    public final void rotateAboutYW(float angle)
    {
	double angleDouble = (double)angle;
	double theta = toRadians(angleDouble);

	float cosTheta = (float)Math.cos(theta);
	float sinTheta = (float)Math.sin(theta);
	
	// Create a rotation matrix given the angle.	    
	Matrix5D rotateZ = new Matrix5D ( cosTheta, 0.0f, -(sinTheta), 0.0f, 0.0f,
					  0.0f,     1.0f, 0.0f,        0.0f, 0.0f,
					  sinTheta, 0.0f, cosTheta,    0.0f, 0.0f,
					  0.0f,     0.0f, 0.0f,        1.0f, 0.0f,
					  0.0f,     0.0f, 0.0f,        0.0f, 1.0f );

	// Create a new temporary matrix to hold the result of multiplying the rotation matrix by the current matrix.
	Matrix5D newMatrix5D = new Matrix5D();

	// Multiply the rotation matrix by the current matrix.
        for (int j = 0; j < 5; j++)
            for (int i = 0; i < 5; i++)
                for (int k = 0; k < 5; k++)
                    newMatrix5D.matrix5D[i][j] += rotateZ.matrix5D[i][k] * this.matrix5D[k][j];

	// Set the current matrix equal to the result of the multiplication.
	this.matrix5D = newMatrix5D.matrix5D;
    }

    public final void rotateAboutWZ(float angle)
    {
	double angleDouble = (double)angle;
	double theta = toRadians(angleDouble);

	float cosTheta = (float)Math.cos(theta);
	float sinTheta = (float)Math.sin(theta);
	
	// Create a rotation matrix given the angle.	    
	Matrix5D rotateZ = new Matrix5D ( cosTheta, -(sinTheta), 0.0f, 0.0f, 0.0f,
					  sinTheta, cosTheta,    0.0f, 0.0f, 0.0f,
					  0.0f,      0.0f,       1.0f, 0.0f, 0.0f,
					  0.0f,      0.0f,       0.0f, 1.0f, 0.0f,
					  0.0f,      0.0f,       0.0f, 0.0f, 1.0f );

	// Create a new temporary matrix to hold the result of multiplying the rotation matrix by the current matrix.
	Matrix5D newMatrix5D = new Matrix5D();

	// Multiply the rotation matrix by the current matrix.
        for (int j = 0; j < 5; j++)
            for (int i = 0; i < 5; i++)
                for (int k = 0; k < 5; k++)
                    newMatrix5D.matrix5D[i][j] += rotateZ.matrix5D[i][k] * this.matrix5D[k][j];

	// Set the current matrix equal to the result of the multiplication.
	this.matrix5D = newMatrix5D.matrix5D;
    }


    /**
    *  Overriden toString() method for the matrix.
    */
    @Override
    public String toString()
    {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < 5; i++)
            output.append( String.format("%f   %f   %f   %f   %f\n", matrix5D[i][0], matrix5D[i][1], matrix5D[i][2], matrix5D[i][3], matrix5D[i][4]) );
        output.append("\n");

        return output.toString();
    }


}
