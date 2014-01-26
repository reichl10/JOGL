package de.joglearth.junit.height.srtm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.joglearth.height.srtm.SRTMBinarySource;
import de.joglearth.height.srtm.SRTMTile;
import de.joglearth.height.srtm.SRTMTileName;
import de.joglearth.height.srtm.SRTMTileSource;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.SourceResponseType;


public class SRTMTileSourceTest {

    @Test
    public void test() {
        SRTMBinarySource binarySource = new SRTMBinarySource(); 
        SRTMTileSource testTileSource1 = new SRTMTileSource(binarySource);
        SRTMTileSource testTileSource2 = null;
        SRTMTileName testTileIndex1 = new SRTMTileName(116, 0);
        SRTMTileName testTileIndex2 = new SRTMTileName(105, -11);
        
        SourceListener<SRTMTileName, SRTMTile> senderFound = new SourceListener<SRTMTileName, SRTMTile>() {          
            
            @Override
            public void requestCompleted(SRTMTileName key, SRTMTile value) {
                assertNotNull(key);
                assertNotNull(value);
            }};
        
        
        assertNotNull(binarySource);
        assertNotNull(testTileSource1);
        assertFalse(testTileSource1.equals(testTileSource2));
        SourceResponse<SRTMTile> response = testTileSource1.requestObject(testTileIndex1, senderFound);
        assertNotNull(response);
        assertNull(response.value);
        assertEquals(SourceResponseType.ASYNCHRONOUS, response.response);
        
        response = testTileSource1.requestObject(testTileIndex2, senderFound);
        assertNotNull(response);
        assertNull(response.value);
        assertEquals(SourceResponseType.ASYNCHRONOUS, response.response);
        
        
        SRTMTileName testTileIndex3 = new SRTMTileName(0, -80);
        
        response = testTileSource1.requestObject(testTileIndex3, null);
        assertNotNull(response);
        assertNull(response.value);
        assertEquals(SourceResponseType.MISSING, response.response);
        
    }
}