package de.joglearth.junit.geometry;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import de.joglearth.geometry.CameraUtils.TileWalker;
import de.joglearth.geometry.GridPoint;
import de.joglearth.geometry.Tile;
import de.joglearth.geometry.TileLayout;
import de.joglearth.source.tiles.osm.OSMPole;
import de.joglearth.source.tiles.osm.OSMTileLayout;


public class TileWalkerWhiteBoxTest {

    @Test
    public void testSouthPolePoint() {
        Set<GridPoint> points = new HashSet<>();
        points.add(new GridPoint(0, 1));
        TileWalker walker = new TileWalker(points, new GridPoint(0, 1), new OSMTileLayout(1));
        Set<Tile> tiles = new HashSet<>();
        do {
            System.out.println(walker.getTile());
            tiles.add(walker.getTile());
        } while (walker.step());

        System.out.println(tiles);
        assertEquals(tiles.size(), 3);
        TileLayout lay = new OSMTileLayout(1);
        assertTrue(tiles.contains(lay.createTile(new GridPoint(0, 0))));
        assertTrue(tiles.contains(lay.createTile(new GridPoint(1, 0))));
        assertTrue(tiles.contains(new OSMPole(1, OSMPole.SOUTH)));
    }

    //@Test
    public void testTwoPoints() {
        Set<GridPoint> points = new HashSet<>();
        points.add(new GridPoint(0, 0));
        points.add(new GridPoint(0, 1));
        TileWalker walker = new TileWalker(points, new GridPoint(0, 1), new OSMTileLayout(3));
        Set<Tile> tiles = new HashSet<>();
        do {
            tiles.add(walker.getTile());
        } while (walker.step());

        assertEquals(tiles.size(), 6);
        TileLayout lay = new OSMTileLayout(3);
        assertTrue(tiles.contains(lay.createTile(new GridPoint(0, 1))));
        assertTrue(tiles.contains(lay.createTile(new GridPoint(0, 0))));
        assertTrue(tiles.contains(lay.createTile(new GridPoint(0, -1))));
        assertTrue(tiles.contains(lay.createTile(new GridPoint(-1, -1))));
        assertTrue(tiles.contains(lay.createTile(new GridPoint(-1, 0))));
        assertTrue(tiles.contains(lay.createTile(new GridPoint(-1, 1))));
    }

}
