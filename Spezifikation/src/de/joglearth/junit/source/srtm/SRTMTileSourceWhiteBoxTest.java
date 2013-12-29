package de.joglearth.junit.source.srtm;

import static org.junit.Assert.*;

import org.junit.Test;

import de.joglearth.height.srtm.SRTMBinarySource;
import de.joglearth.height.srtm.SRTMTile;
import de.joglearth.height.srtm.SRTMTileName;
import de.joglearth.height.srtm.SRTMTileSource;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponseType;


public class SRTMTileSourceWhiteBoxTest {

    @Test
    public void test() throws InterruptedException {
        SRTMTileSource ts = new SRTMTileSource(new SRTMBinarySource());

        class Listener implements SourceListener<SRTMTileName, SRTMTile> {

            public SRTMTile tile = null;


            @Override
            public void requestCompleted(SRTMTileName key, SRTMTile value) {
                tile = value;
                synchronized (SRTMTileSourceWhiteBoxTest.this) {
                    SRTMTileSourceWhiteBoxTest.this.notify();
                }
            }

        }

        Listener l = new Listener();

        synchronized (this) {
            if (ts.requestObject(new SRTMTileName(61, 36), l).response 
                    == SourceResponseType.ASYNCHRONOUS) {
                this.wait();
            }
        }
            
        assertNotNull(l.tile);
        
        short[][] real = { { 241, 284 }, { 346, 375 } };
        short[][] interpolated = l.tile.getTile(9);
        
        for (int i=0; i<2; ++i) {
            for (int j=0; j<2; ++j) {
                assertEquals(real[i][j], interpolated[i][j]);
            }
        }
    }

}
