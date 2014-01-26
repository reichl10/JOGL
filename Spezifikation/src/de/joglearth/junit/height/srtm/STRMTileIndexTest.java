package de.joglearth.junit.height.srtm;

import static org.junit.Assert.*;

import org.junit.Test;

import de.joglearth.height.srtm.SRTMTileName;


public class STRMTileIndexTest {

    @Test
    public final void testSRTMTileIndex() {
        SRTMTileName index = new SRTMTileName(1, 2);
        assertTrue(index.latitude == 2);
        assertTrue(index.longitude == 1);
    }

    @Test
    public final void testEqualsObject() {
        SRTMTileName index = new SRTMTileName(1, 2);
        index.latitude = 2;
        index.longitude = 1;
        SRTMTileName index2 = new SRTMTileName(1, 2);
        index2.latitude = 2;
        index2.longitude = 1;
        SRTMTileName index3 = new SRTMTileName(5, 4);
        index3.latitude = 4;
        index3.longitude = 5;
        assertEquals(index, index2);
        assertFalse((index.equals(index3)));
    }

}
