package de.joglearth.geometry;

/**
 * Defines a tile that only contains the edges as longitude and latitude values and implements
 * getters to access those values.
 */
public class SimpleTile extends AbstractTile {

    private final double lonFrom, lonTo, latFrom, latTo;
    
    /**
     * Creates a {@link SimpleTile} with the given values.
     * @param lonFrom The longitude where the tile starts
     * @param latFrom The latitude where the tile starts
     * @param lonTo The longitude where the tile ends
     * @param latTo The latitude where the tile ends
     */
    public SimpleTile(double lonFrom, double latFrom, double lonTo, double latTo) {
        this.lonFrom = lonFrom;
        this.latFrom = latFrom;
        this.lonTo = lonTo;
        this.latTo = latTo;
    }
    
    @Override
    public double getLongitudeFrom() {
        return lonFrom;
    }

    @Override
    public double getLongitudeTo() {
        return lonTo;
    }

    @Override
    public double getLatitudeFrom() {
        return latFrom;
    }

    @Override
    public double getLatitudeTo() {
        return latTo;
    }

    @Override
    public TransformedTile getScaledAlternative() {
        return null;
    }

}
