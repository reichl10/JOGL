package de.joglearth.junit.geometry;

import static java.lang.Math.*;
import static org.junit.Assert.*;

import org.junit.Test;

import de.joglearth.geometry.Tile;


public class TileWhiteBoxTest {

    @Test
    public void test() {
        //TODO System.out.println(new Tile(4, 8, 6));
        Tile t = new Tile(4, 8, 6);
        assertTrue(t.intersects(PI, 0, -PI/2, PI/4));
    }

}
