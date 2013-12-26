package de.joglearth.geometry;

import static java.lang.Math.*;


public class SingleTile implements Tile {

    @Override
    public double getLongitudeFrom() {
        return -PI;
    }

    @Override
    public double getLongitudeTo() {
        return PI;
    }

    @Override
    public double getLatitudeFrom() {
        return -PI/2;
    }

    @Override
    public double getLatitudeTo() {
        return PI/2;
    }

    @Override
    public boolean contains(GeoCoordinates coords) {
        return true;
    }

    @Override
    public boolean intersects(double lonFrom, double latFrom, double lonTo, double latTo) {
        return true;
    }

    @Override
    public GridPoint[] getCorners() {
        return new GridPoint[] { new GridPoint(0, 0), new GridPoint(0, 1) };
    }

}
