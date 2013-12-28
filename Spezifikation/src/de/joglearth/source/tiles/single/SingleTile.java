package de.joglearth.source.tiles.single;

import static java.lang.Math.*;
import de.joglearth.geometry.AbstractTile;
import de.joglearth.geometry.GridPoint;


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

}
