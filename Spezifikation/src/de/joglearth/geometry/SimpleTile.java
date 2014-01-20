package de.joglearth.geometry;


public class SimpleTile extends AbstractTile {

    private final double lonFrom, lonTo, latFrom, latTo;
    
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
