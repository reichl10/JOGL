package de.joglearth.junit.source.srtm;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.joglearth.source.srtm.SRTMPathTranslator;
import de.joglearth.source.srtm.SRTMTileIndex;


public class STRMPathTranslator {

    @Test
    public final void test() {
        SRTMTileIndex index = new SRTMTileIndex(1, 2);
        SRTMPathTranslator translator = new SRTMPathTranslator();
        String path = translator.toFileSystemPath(index);
        SRTMTileIndex indexRe = translator.fromFileSystemPath(path);
        assertTrue(index.latitude == indexRe.latitude && index.longitude == indexRe.longitude);
    }


}
