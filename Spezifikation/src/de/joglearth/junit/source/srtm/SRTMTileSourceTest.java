package de.joglearth.junit.source.srtm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.SourceResponseType;
import de.joglearth.source.srtm.SRTMBinarySource;
import de.joglearth.source.srtm.SRTMTile;
import de.joglearth.source.srtm.SRTMTileIndex;
import de.joglearth.source.srtm.SRTMTileSource;


public class SRTMTileSourceTest {

    @Test
    public void test() {
        SRTMBinarySource binarySource = new SRTMBinarySource(); 
        SRTMTileSource testTileSource1 = new SRTMTileSource(binarySource);
        SRTMTileSource testTileSource2 = null;
        SRTMTileIndex testTileIndex1 = new SRTMTileIndex(116, 0);
        SRTMTileIndex testTileIndex2 = new SRTMTileIndex(105, -11);
        
        SourceListener<SRTMTileIndex, SRTMTile> senderFound = new SourceListener<SRTMTileIndex, SRTMTile>() {          
            
            @Override
            public void requestCompleted(SRTMTileIndex key, SRTMTile value) {
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
        
        
        SRTMTileIndex testTileIndex3 = new SRTMTileIndex(0, -80);
        
        response = testTileSource1.requestObject(testTileIndex3, null);
        assertNotNull(response);
        assertNull(response.value);
        assertEquals(SourceResponseType.MISSING, response.response);
        
    }
}