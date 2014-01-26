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

    public static final Matrix4 IDENTITY 
        = new Matrix4(new double[] { 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 });

    private final double[] m = new double[16];
    
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
    
    
    public Matrix4 multiply(double[] rhs) {
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
        return new Matrix4(r);
    }

    /**
     * Multiplies the matrix with another matrix.
     * 
     * Mathematical equivalent: this' := this * rhs
     * 
     * @param rhs The matrix to multiply with
     */
    public Matrix4 multiply(Matrix4 rhs) {
        if (rhs == null) { 
            throw new IllegalArgumentException();
        }
        return multiply(rhs.m);
    }

    /**
     * Adds another matrix, given by a double value array, to itself component-wise.
     * 
     * @param rhs The matrix to add
     */
    public Matrix4 plus(double[] rhs) {
        if (rhs == null || rhs.length != 16) {
            throw new IllegalArgumentException();
        }
        double[] r = new double[16];
        for (int i = 0; i < 16; ++i) {
            r[i] += rhs[i];
        }
        return new Matrix4(r);
    }

    /**
     * Adds another matrix to itself component-wise.
     * 
     * @param rhs The matrix to add
     */
    public Matrix4 plus(Matrix4 rhs) {
        return plus(rhs.m);
    }

    /**
     * Returns the double value array for the matrix.
     * 
     * @return The matrix values in column-first ordering
     */
    public double[] toArray() {
        return Arrays.copyOf(m, m.length);
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
    public Matrix4 translate(double x, double y, double z) {
        return multiply(new double[] { 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, x, y, z, 1 });
    }

    /**
     * Multiplies itself by a translation matrix.
     * 
     * Points transformed with this matrix will thereafter be translated by the given extents.
     * 
     * @param v Translation for all three coordinates, must not be <code>null</code>
     */
    public Matrix4 translate(Vector3 v) {
        if (v == null) {
            throw new IllegalArgumentException();
        }
        return translate(v.x, v.y, v.z);
    }

    /**
     * Multiplies itself with a rotation matrix rotating around a given axis.
     * 
     * Points transformed with this matrix will thereafter be rotated by the given angle.
     * 
     * @param axis The axis that should be rotated around, must not be <code>null</code>
     * @param rad The rotation angle, in radians
     */
    public Matrix4 rotate(Vector3 axis, double rad) {
        if (axis == null || axis.length() == 0 || isNaN(axis.length()) || isInfinite(axis.length())
                || isNaN(rad) || isInfinite(rad)) {
            throw new IllegalArgumentException();
        }
        
        axis = axis.normalized();
        final double n1 = axis.x, n2 = axis.y, n3 = axis.z, c = cos(rad), s = sin(rad);
        return multiply(new double[] { 
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
    public Matrix4 scale(double x, double y, double z) {
        return multiply(new double[] { x, 0, 0, 0, 0, y, 0, 0, 0, 0, z, 0, 0, 0, 0, 1 });
    }

    /**
     * Multiplies itself with a scale matrix.
     * 
     * Points transformed with this matrix will thereafter be scaled relative to the point of
     * origin.
     * 
     * @param v The scale in all three dimensions, must not be <code>null</code>
     */
    public Matrix4 scale(Vector3 v) {
        return scale(v.x, v.y, v.z);
    }

    /**
     * Calculates and returns the inverse of the matrix. If the matrix is singular, the result is
     * unspecified.
     * 
     * @return The inverse
     */
    public Matrix4 inverse() {
        double[] i = new double[16];

        i[0] = m[5] * m[10] * m[15] - m[5] * m[11] * m[14] - m[9] * m[6]
                * m[15] + m[9] * m[7] * m[14] + m[13] * m[6] * m[11] - m[13]
                * m[7] * m[10];

        i[1] = -m[1] * m[10] * m[15] + m[1] * m[11] * m[14] + m[9] * m[2]
                * m[15] - m[9] * m[3] * m[14] - m[13] * m[2] * m[11] + m[13]
                * m[3] * m[10];

        i[2] = m[1] * m[6] * m[15] - m[1] * m[7] * m[14] - m[5] * m[2]
                * m[15] + m[5] * m[3] * m[14] + m[13] * m[2] * m[7] - m[13]
                * m[3] * m[6];

        i[3] = -m[1] * m[6] * m[11] + m[1] * m[7] * m[10] + m[5] * m[2]
                * m[11] - m[5] * m[3] * m[10] - m[9] * m[2] * m[7] + m[9]
                * m[3] * m[6];

        i[4] = -m[4] * m[10] * m[15] + m[4] * m[11] * m[14] + m[8] * m[6]
                * m[15] - m[8] * m[7] * m[14] - m[12] * m[6] * m[11] + m[12]
                * m[7] * m[10];

        i[5] = m[0] * m[10] * m[15] - m[0] * m[11] * m[14] - m[8] * m[2]
                * m[15] + m[8] * m[3] * m[14] + m[12] * m[2] * m[11] - m[12]
                * m[3] * m[10];

        i[6] = -m[0] * m[6] * m[15] + m[0] * m[7] * m[14] + m[4] * m[2]
                * m[15] - m[4] * m[3] * m[14] - m[12] * m[2] * m[7] + m[12]
                * m[3] * m[6];

        i[7] = m[0] * m[6] * m[11] - m[0] * m[7] * m[10] - m[4] * m[2]
                * m[11] + m[4] * m[3] * m[10] + m[8] * m[2] * m[7] - m[8]
                * m[3] * m[6];

        i[8] = m[4] * m[9] * m[15] - m[4] * m[11] * m[13] - m[8] * m[5]
                * m[15] + m[8] * m[7] * m[13] + m[12] * m[5] * m[11] - m[12]
                * m[7] * m[9];

        i[9] = -m[0] * m[9] * m[15] + m[0] * m[11] * m[13] + m[8] * m[1]
                * m[15] - m[8] * m[3] * m[13] - m[12] * m[1] * m[11] + m[12]
                * m[3] * m[9];

        i[10] = m[0] * m[5] * m[15] - m[0] * m[7] * m[13] - m[4] * m[1]
                * m[15] + m[4] * m[3] * m[13] + m[12] * m[1] * m[7] - m[12]
                * m[3] * m[5];

        i[11] = -m[0] * m[5] * m[11] + m[0] * m[7] * m[9] + m[4] * m[1]
                * m[11] - m[4] * m[3] * m[9] - m[8] * m[1] * m[7] + m[8] * m[3]
                * m[5];

        i[12] = -m[4] * m[9] * m[14] + m[4] * m[10] * m[13] + m[8] * m[5]
                * m[14] - m[8] * m[6] * m[13] - m[12] * m[5] * m[10] + m[12]
                * m[6] * m[9];

        i[13] = m[0] * m[9] * m[14] - m[0] * m[10] * m[13] - m[8] * m[1]
                * m[14] + m[8] * m[2] * m[13] + m[12] * m[1] * m[10] - m[12]
                * m[2] * m[9];

        i[14] = -m[0] * m[5] * m[14] + m[0] * m[6] * m[13] + m[4] * m[1]
                * m[14] - m[4] * m[2] * m[13] - m[12] * m[1] * m[6] + m[12]
                * m[2] * m[5];

        i[15] = m[0] * m[5] * m[10] - m[0] * m[6] * m[9] - m[4] * m[1]
                * m[10] + m[4] * m[2] * m[9] + m[8] * m[1] * m[6] - m[8] * m[2]
                * m[5];

        final double det = m[0] * i[0] + m[1] * i[4] + m[2] * i[8] + m[3]
                * i[12];

        for (int j = 0; j < 16; ++j) {
            i[j] /= det;
        }

        return new Matrix4(i);
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
