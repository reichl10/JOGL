package de.joglearth.geometry;

/**
 * Structure representing relative screen coordinates, where (0,0) is the top-left and (1,1) is the
 * bottom-right.
 */
public final class ScreenCoordinates {

    /**
     * The horizontal position in [0.0; 1.0], where 0.0 is the left, 1.0 the right border.
     */
    public final double x;

    /**
     * The vertical position in [0.0; 1.0], where 0.0 is the top, 1.0 the bottom border.
     */
    public final double y;


    /**
     * Constructor.
     * 
     * @param x The horizontal position (this.x)
     * @param y The vertical position (this.y)
     */
    public ScreenCoordinates(double x, double y) {
        if (x < 0 || x > 1 || y < 0 || y > 1) {
            throw new IllegalArgumentException();
        }
        
        this.x = x;
        this.y = y;
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
        final ScreenCoordinates other = (ScreenCoordinates) obj;
        return this.x == other.x && this.y == other.y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
