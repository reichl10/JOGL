package de.joglearth.junit.source.srtm;

import static org.junit.Assert.*;

import org.junit.Test;

import de.joglearth.source.SourceListener;
import de.joglearth.source.srtm.SRTMBinarySource;
import de.joglearth.source.srtm.SRTMTileName;


public class SRTMBinarySourceWhiteBoxTest {

    @Test
    public void test() throws Exception {
        SRTMBinarySource srtm = new SRTMBinarySource();
        
        class SRTMListener implements SourceListener<SRTMTileName, byte[]> {            
            public byte[] result;
            
            @Override
            public void requestCompleted(SRTMTileName key, byte[] value) {
                result = value;
                
                synchronized (SRTMBinarySourceWhiteBoxTest.this) {
                    //SRTMBinarySourceWhiteBoxTest.this.notify();
                }
            }
        };
        
        SRTMListener listener = new SRTMListener();
        
        srtm.requestObject(new SRTMTileName(61,  36), listener);
        
        synchronized (this) {
            //wait();
        }
        
        byte[] bytes = listener.result;
        
        assertNotNull(bytes);
    }

}
