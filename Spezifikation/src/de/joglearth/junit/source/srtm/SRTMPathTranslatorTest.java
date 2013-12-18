package de.joglearth.junit.source.srtm;

import static org.junit.Assert.*;

import org.junit.Test;

import de.joglearth.source.srtm.SRTMPathTranslator;
import de.joglearth.source.srtm.SRTMTileIndex;


public class SRTMPathTranslatorTest {

    @Test
    public void testWrite() {
        SRTMTileIndex index = new SRTMTileIndex(-91, 12);
        SRTMPathTranslator translator = new SRTMPathTranslator();
        String path = translator.toFileSystemPath(index);
        assertEquals(path, "N12W091.hgt.zip");
    }
    
    @Test
    public void testParse() {
        SRTMTileIndex index = new SRTMTileIndex(-91, 12);
        SRTMPathTranslator translator = new SRTMPathTranslator();
        String path = "N12W091.hgt.zip";
        SRTMTileIndex result = translator.fromFileSystemPath(path);
        assertEquals(index, result);
    }

}