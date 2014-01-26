package de.joglearth.geometry;

/**
 * A structure for 4-dimensional vectors in homogeneous coordinates.
 */
public final class Vector4 {

    /**
     * The first (X) component.
     */
    public final double x;

    /**
     * The second (Y) component.
     */
    public final double y;

    /**
     * The third (Z) component.
     */
    public final double z;

    /**
     * The scaling factor.
     */
    public final double w;
    

    public static final Vector4 ZERO = new Vector4(0, 0, 0, 0);
    
    public static final Vector4 IDENTITY = new Vector4(0, 0, 0, 1);


    /**
     * Constructor.
     * 
     * @param x The first component (this.x)
     * @param y The second component (this.y)
     * @param z The third component (this.z)
     * @param w The scaling factor (this.w)
     */
    public Vector4(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    /**
     * Constructor copying the position from a {@link Vector3}. The scaling factor w is set to 1.
     * 
     * @param copyFrom The vector to copy from
     */
    public Vector4(Vector3 copyFrom) {
        this(copyFrom.x, copyFrom.y, copyFrom.z, 1);
    }

    /**
     * Creates a three-dimensional vector by dividing (x,y,z) by the scaling factor w.
     * 
     * @return The scaled three-dimensional vector.
     */
    public Vector3 perspectiveDivide() {
        return new Vector3(x / w, y / w, z / w);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ", " + w + ")";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(w);
        result = prime * result + (int) (temp ^ (temp >>> 32));
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
        Vector4 other = (Vector4) obj;
        return this.x == other.x && this.y == other.y && this.z == other.z && this.w == other.w;
    }

    public double[] toArray() {
        return new double[] { x, y, z, w };
    }
}
