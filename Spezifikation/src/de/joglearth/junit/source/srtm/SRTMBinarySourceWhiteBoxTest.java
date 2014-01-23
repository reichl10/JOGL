package de.joglearth.junit.source.srtm;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import de.joglearth.height.srtm.SRTMBinarySource;
import de.joglearth.height.srtm.SRTMTileName;
import de.joglearth.source.SourceListener;


public class SRTMBinarySourceWhiteBoxTest {

    @Test
    public void test() throws Exception {
        SRTMBinarySource srtm = new SRTMBinarySource();
                
        class SRTMListener implements SourceListener<SRTMTileName, byte[]> {            
            public byte[] result;
            
            @Override
            public void requestCompleted(SRTMTileName key, byte[] value) {
                result = value;
                assertNotNull(result);
                
                synchronized (SRTMBinarySourceWhiteBoxTest.this) {
                    //SRTMBinarySourceWhiteBoxTest.this.notify();
                }
            }
        };
        
        SRTMListener listener = new SRTMListener();   
        srtm.requestObject(new SRTMTileName(61,  36), listener);
    }
}