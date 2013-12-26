package de.joglearth.geometry;

import static java.lang.Math.*;


public class SingleTile extends AbstractTile {

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
    public GridPoint[] getCorners() {
        return new GridPoint[] { };
    }

}
