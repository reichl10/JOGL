package de.joglearth.source.srtm;

import de.joglearth.source.caching.PathTranslator;


/**
 * Implements the {@link PathTranslator} interface for SRTM height data tiles.
 */
public class SRTMPathTranslator implements PathTranslator<SRTMTileIndex> {

    @Override
    public String toFileSystemPath(SRTMTileIndex k) {
        return null;
    }

    @Override
    public SRTMTileIndex fromFileSystemPath(String s) {
        return null;
    }

}
