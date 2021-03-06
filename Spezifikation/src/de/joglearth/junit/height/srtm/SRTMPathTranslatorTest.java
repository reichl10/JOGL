package de.joglearth.junit.height.srtm;

import static org.junit.Assert.*;

import org.junit.Test;

import de.joglearth.height.srtm.SRTMPathTranslator;
import de.joglearth.height.srtm.SRTMTileName;


public class SRTMPathTranslatorTest {

    @Test
    public void testWrite() {
        SRTMTileName index = new SRTMTileName(-91, 12);
        SRTMPathTranslator translator = new SRTMPathTranslator();
        String path = translator.toFileSystemPath(index);
        assertEquals(path, "N12W091.hgt.zip");
    }
    
    @Test
    public void testParse() {
        SRTMTileName index = new SRTMTileName(-91, 12);
        SRTMPathTranslator translator = new SRTMPathTranslator();
        String path = "N12W091.hgt.zip";
        SRTMTileName result = translator.fromFileSystemPath(path);
        assertEquals(index, result);
    }

}
