package de.joglearth.geometry;

import java.util.Arrays;

import static java.lang.Math.*;
import static java.lang.Double.*;


/**
 * A class for 4x4 matrices used in rendering contexts.
 * 
 * Provides methods for translation, rotation and scaling of transformed vectors.
 */
public final class Matrix4 implements Cloneable {

    // Holds the matrix data in column-first ordering.
    private double[] m = { 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 };


    /**
     * Constructor. Initializes an identity matrix.
     */
    public Matrix4() {}

    /**
     * Creates a deep copy of the matrix.
     * 
     * @return The copied matrix
     */
    @Override
    public Matrix4 clone() {
        Matrix4 c = new Matrix4();
        System.arraycopy(m, 0, c.m, 0, 16);
        return c;
    }

    /**
     * Creates a matrix from a double value array.
     * 
     * @param init The matrix cells in column-first ordering
     */
    public Matrix4(double[] init) {
        if (init == null || init.length != 16) {
            throw new IllegalArgumentException();
        }
        System.arraycopy(init, 0, m, 0, 16);
    }

    /**
     * Replaces the matrix by an identity matrix.
     */
    public void reset() {
        double[] identity = { 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 };
        System.arraycopy(identity, 0, m, 0, 16);
    }
    
    /**
     * Multiplies the matrix with another matrix, given by a double value array.
     * 
     * Mathematical equivalent: this' := this * rhs
     * @param rhs The matrix to multiply with
     */
    public void mult(double[] rhs) {
        if (rhs == null || rhs.length != 16) {
            throw new IllegalArgumentException();
        }
        final double[] r = new double[16];
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                for (int k = 0; k < 4; ++k) {
                    r[4 * j + i] += m[4 * k + i] * rhs[4 * j + k];
                }
            }
        }
        m = r;
    }

    /**
     * Multiplies the matrix with another matrix.
     * 
     * Mathematical equivalent: this' := this * rhs
     * 
     * @param rhs The matrix to multiply with
     */
    public void mult(Matrix4 rhs) {
        if (rhs == null) { 
            throw new IllegalArgumentException();
        }
        mult(rhs.m);
    }

    /**
     * Adds another matrix, given by a double value array, to itself component-wise.
     * 
     * @param rhs The matrix to add
     */
    public void add(double[] rhs) {
        if (rhs == null || rhs.length != 16) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < 16; ++i) {
            m[i] += rhs[i];
        }
    }

    /**
     * Adds another matrix to itself component-wise.
     * 
     * @param rhs The matrix to add
     */
    public void add(Matrix4 rhs) {
        add(rhs.m);
    }

    /**
     * Returns the double value array for the matrix.
     * 
     * @return The matrix values in column-first ordering
     */
    public double[] doubles() {
        return m;
    }

    /**
     * Multiplies itself by a translation matrix.
     * 
     * Points transformed with this matrix will thereafter be translated by the given extents.
     * 
     * @param x Translation by the X (first) coordinate
     * @param y Translation by the Y (second) coordinate
     * @param z Translation by the Z (third) coordinate
     */
    public void translate(double x, double y, double z) {
        mult(new double[] { 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, x, y, z, 1 });
    }

    /**
     * Multiplies itself by a translation matrix.
     * 
     * Points transformed with this matrix will thereafter be translated by the given extents.
     * 
     * @param v Translation for all three coordinates, must not be <code>null</code>
     */
    public void translate(Vector3 v) {
        if (v == null) {
            throw new IllegalArgumentException();
        }
        translate(v.x, v.y, v.z);
    }

    /**
     * Multiplies itself with a rotation matrix rotating around a given axis.
     * 
     * Points transformed with this matrix will thereafter be rotated by the given angle.
     * 
     * @param axis The axis that should be rotated around, must not be <code>null</code>
     * @param rad The rotation angle, in radians
     */
    public void rotate(Vector3 axis, double rad) {
        if (axis == null || axis.length() == 0 || isNaN(axis.length()) || isInfinite(axis.length())
                || isNaN(rad) || isInfinite(rad)) {
            throw new IllegalArgumentException();
        }
        
        axis = axis.normalized();
        final double n1 = axis.x, n2 = axis.y, n3 = axis.z, c = cos(rad), s = sin(rad);
        mult(new double[] { 
                n1*n1*(1-c)+c, n2*n1*(1-c)+n3*s, n3*n1*(1-c)-n2*s, 0,
                n1*n2*(1-c)-n3*s, n2*n2*(1-c)+c, n3*n2*(1-c)+n1*s, 0,
                n1*n3*(1-c)+n2*s, n2*n3*(1-c)-n1*s, n3*n3*(1-c)+c, 0,
                0, 0, 0, 1
        });
    }

    /**
     * Multiplies itself with a scale matrix.
     * 
     * Points transformed with this matrix will thereafter be scaled relative to the point of
     * origin.
     * 
     * @param x The scale in X direction (The first axis)
     * @param y The scale in Y direction (The second axis)
     * @param z The scale in Z direction (The third axis)
     */
    public void scale(double x, double y, double z) {
        mult(new double[] { x, 0, 0, 0, 0, y, 0, 0, 0, 0, z, 0, 0, 0, 0, 1 });
    }

    /**
     * Multiplies itself with a scale matrix.
     * 
     * Points transformed with this matrix will thereafter be scaled relative to the point of
     * origin.
     * 
     * @param v The scale in all three dimensions, must not be <code>null</code>
     */
    public void scale(Vector3 v) {
        scale(v.x, v.y, v.z);
    }

    /**
     * Calculates and returns the inverse of the matrix. If the matrix is singular, the result is
     * unspecified.
     * 
     * @return The inverse
     */
    public Matrix4 inverse() {
        final Matrix4 i = new Matrix4();

        i.m[0] = m[5] * m[10] * m[15] - m[5] * m[11] * m[14] - m[9] * m[6]
                * m[15] + m[9] * m[7] * m[14] + m[13] * m[6] * m[11] - m[13]
                * m[7] * m[10];

        i.m[1] = -m[1] * m[10] * m[15] + m[1] * m[11] * m[14] + m[9] * m[2]
                * m[15] - m[9] * m[3] * m[14] - m[13] * m[2] * m[11] + m[13]
                * m[3] * m[10];

        i.m[2] = m[1] * m[6] * m[15] - m[1] * m[7] * m[14] - m[5] * m[2]
                * m[15] + m[5] * m[3] * m[14] + m[13] * m[2] * m[7] - m[13]
                * m[3] * m[6];

        i.m[3] = -m[1] * m[6] * m[11] + m[1] * m[7] * m[10] + m[5] * m[2]
                * m[11] - m[5] * m[3] * m[10] - m[9] * m[2] * m[7] + m[9]
                * m[3] * m[6];

        i.m[4] = -m[4] * m[10] * m[15] + m[4] * m[11] * m[14] + m[8] * m[6]
                * m[15] - m[8] * m[7] * m[14] - m[12] * m[6] * m[11] + m[12]
                * m[7] * m[10];

        i.m[5] = m[0] * m[10] * m[15] - m[0] * m[11] * m[14] - m[8] * m[2]
                * m[15] + m[8] * m[3] * m[14] + m[12] * m[2] * m[11] - m[12]
                * m[3] * m[10];

        i.m[6] = -m[0] * m[6] * m[15] + m[0] * m[7] * m[14] + m[4] * m[2]
                * m[15] - m[4] * m[3] * m[14] - m[12] * m[2] * m[7] + m[12]
                * m[3] * m[6];

        i.m[7] = m[0] * m[6] * m[11] - m[0] * m[7] * m[10] - m[4] * m[2]
                * m[11] + m[4] * m[3] * m[10] + m[8] * m[2] * m[7] - m[8]
                * m[3] * m[6];

        i.m[8] = m[4] * m[9] * m[15] - m[4] * m[11] * m[13] - m[8] * m[5]
                * m[15] + m[8] * m[7] * m[13] + m[12] * m[5] * m[11] - m[12]
                * m[7] * m[9];

        i.m[9] = -m[0] * m[9] * m[15] + m[0] * m[11] * m[13] + m[8] * m[1]
                * m[15] - m[8] * m[3] * m[13] - m[12] * m[1] * m[11] + m[12]
                * m[3] * m[9];

        i.m[10] = m[0] * m[5] * m[15] - m[0] * m[7] * m[13] - m[4] * m[1]
                * m[15] + m[4] * m[3] * m[13] + m[12] * m[1] * m[7] - m[12]
                * m[3] * m[5];

        i.m[11] = -m[0] * m[5] * m[11] + m[0] * m[7] * m[9] + m[4] * m[1]
                * m[11] - m[4] * m[3] * m[9] - m[8] * m[1] * m[7] + m[8] * m[3]
                * m[5];

        i.m[12] = -m[4] * m[9] * m[14] + m[4] * m[10] * m[13] + m[8] * m[5]
                * m[14] - m[8] * m[6] * m[13] - m[12] * m[5] * m[10] + m[12]
                * m[6] * m[9];

        i.m[13] = m[0] * m[9] * m[14] - m[0] * m[10] * m[13] - m[8] * m[1]
                * m[14] + m[8] * m[2] * m[13] + m[12] * m[1] * m[10] - m[12]
                * m[2] * m[9];

        i.m[14] = -m[0] * m[5] * m[14] + m[0] * m[6] * m[13] + m[4] * m[1]
                * m[14] - m[4] * m[2] * m[13] - m[12] * m[1] * m[6] + m[12]
                * m[2] * m[5];

        i.m[15] = m[0] * m[5] * m[10] - m[0] * m[6] * m[9] - m[4] * m[1]
                * m[10] + m[4] * m[2] * m[9] + m[8] * m[1] * m[6] - m[8] * m[2]
                * m[5];

        final double det = m[0] * i.m[0] + m[1] * i.m[4] + m[2] * i.m[8] + m[3]
                * i.m[12];

        for (int j = 0; j < 16; ++j) {
            i.m[j] /= det;
        }

        return i;
    }

    /**
     * Creates a string representation of the matrix.
     * 
     * @return The matrix in a human-readable string
     */
    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < 4; ++i) {
            s += String.format("%.3e %.3e %.3e %.3e\n", m[i], m[i + 4],
                    m[i + 8], m[i + 12]);
        }
        return s;
    }

    /**
     * Transforms a three-dimensional vector of Cartesian coordinates into a four-dimensional vector
     * of homogeneous coordinates by matrix-vector multiplication.
     * 
     * @param v3 The vector to transform, must not be <code>null</code>
     * @return The transformed vector
     */
    public Vector4 transform(Vector3 v3) {
        if (v3 == null) {
            throw new IllegalArgumentException();
        }
        final double[] v = { v3.x, v3.y, v3.z, 1 }, w = { 0, 0, 0, 0 };
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                w[i] += m[4 * j + i] * v[j];
            }
        }
        return new Vector4(w[0], w[1], w[2], w[3]);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(m);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Matrix4 other = (Matrix4) obj;
        return Arrays.equals(m, other.m);
    }
}