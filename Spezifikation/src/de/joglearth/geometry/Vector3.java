package de.joglearth.geometry;

/**
 * A structure for 3-dimensional vectors in Cartesian coordinates.
 * @author Fabian Knorr
 *
 */
public class Vector3 implements Cloneable {

    public float x, y, z;


    /**
     * Constructor. Creates a zero vector.
     */
    public Vector3() {
        this(0, 0, 0);
    }

    /**
     * Constructor. Initializes a vector with the parameters provided.
     * 
     * @param x The X (first) component.
     * @param y The Y (second) component.
     * @param z The Z (third) component.
     */
    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Creates a deep copy.
     * 
     * @return An exact copy of the vector.
     */
    public Vector3 clone() {
        return new Vector3(x, y, z);
    }

    /**
     * Calculates the difference vector from another Vector3.
     * @param other The vector to subtract from.
     * @return The difference.
     */
    public Vector3 to(Vector3 other) {
        return new Vector3(other.x - x, other.y - y, other.z - z);
    }

    /**
     * Multiplies the vector by a scalar.
     * @param c The scalar.
     * @return The product vector.
     */
    public Vector3 times(float c) {
        return new Vector3(c * x, c * y, c * z);
    }

    /**
     * Calculates the sum of this and a second vector.
     * @param v The vector to add.
     * @return The sum.
     */
    public Vector3 plus(Vector3 v) {
        return new Vector3(x + v.x, y + v.y, z + v.z);
    }

    /**
     * Calculates the difference vector to another Vecotr3.
     * @param v The vector to subtract.
     * @return The difference.
     */
    public Vector3 minus(Vector3 v) {
        return new Vector3(x - v.x, y - v.y, z - v.z);
    }

    /**
     * Calculates the vectors length, that is sqrt(x^2+y^2+z^2).
     * @return The length.
     */
    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    /**
     * Adds another vector to this vector.
     * @param v The vector to add.
     */
    public void add(Vector3 v) {
        x += v.x;
        y += v.y;
        z += v.z;
    }

    /**
     * Normalizes the vector, so that length() == 1. 
     * @return
     */
    public Vector3 normalized() {
        float l = length();
        return new Vector3(x / l, y / l, z / l);
    }

}
