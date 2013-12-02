package de.joglearth.geometry;

/**
 * A structure for 3-dimensional vectors in Cartesian coordinates.
 */
public final class Vector3 implements Cloneable {

    /**
     * The X (first) component of the vector.
     */
    public double x;
    
    /**
     * The Y (second) component of the vector.
     */
    public double y;
    
    /**
     * The Z (third) component of the vector.
     */
    public double z;


    /**
     * Constructor. Creates a zero vector.
     */
    public Vector3() {
        this(0, 0, 0);
    }

    /**
     * Constructor. Initializes a vector with the parameters provided.
     * 
     * @param x The X (first) component
     * @param y The Y (second) component
     * @param z The Z (third) component
     */
    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Creates a deep copy.
     * 
     * @return An exact copy of the vector
     */
    public Vector3 clone() {
        return new Vector3(x, y, z);
    }

    /**
     * Calculates the difference vector from another Vector3.
     * 
     * @param other The vector to subtract from, must not be <code>null</code>
     * @return The difference
     */
    public Vector3 to(Vector3 other) {
        return new Vector3(other.x - x, other.y - y, other.z - z);
    }

    /**
     * Multiplies the vector by a scalar.
     * 
     * @param c The scalar
     * @return The product vector
     */
    public Vector3 times(double c) {
        return new Vector3(c * x, c * y, c * z);
    }

    /**
     * Calculates the sum of this and a second vector.
     * 
     * @param v The vector to add, must not be <code>null</code>
     * @return The sum
     */
    public Vector3 plus(Vector3 v) {
        return new Vector3(x + v.x, y + v.y, z + v.z);
    }

    /**
     * Calculates the difference vector to another Vecotr3.
     * 
     * @param v The vector to subtract, must not be <code>null</code>
     * @return The difference
     */
    public Vector3 minus(Vector3 v) {
        return new Vector3(x - v.x, y - v.y, z - v.z);
    }

    /**
     * Calculates the vectors length, that is sqrt(x^2+y^2+z^2).
     * 
     * @return The length
     */
    public double length() {
        return (double) Math.sqrt(x * x + y * y + z * z);
    }

    /**
     * Adds another vector to this vector.
     * 
     * @param v The vector to add, must not be <code>null</code>
     */
    public void add(Vector3 v) {
        x += v.x;
        y += v.y;
        z += v.z;
    }

    /**
     * Normalizes the vector, so that length() == 1.
     * 
     * @return A normalized <code>Vector3</code>
     */
    public Vector3 normalized() {
        double l = length();
        return new Vector3(x / l, y / l, z / l);
    }

    /**
     * Calculates the cross product of a given {@link de.joglearth.geometry.Vector3}.
     * 
     * @param v The vector, must not be <code>null</code>
     * @return The cross product of the vector
     */
    public Vector3 crossProduct(Vector3 v) {
        return new Vector3(this.y*v.z - this.z*v.y,
                           this.z*v.x - this.x*v.z,
                           this.x*v.y - this.y*v.x);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(z);
        result = prime * result + (int) (temp ^ (temp >>> 32));
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
        Vector3 other = (Vector3) obj;
        return this.x == other.x && this.y == other.y && this.z == other.z;
    }

}
