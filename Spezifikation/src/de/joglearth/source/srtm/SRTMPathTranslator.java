package de.joglearth.source.srtm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.joglearth.source.caching.PathTranslator;


/**
 * Implements the {@link de.joglearth.source.caching.PathTranslator} interface for SRTM height data tiles.
 */
public class SRTMPathTranslator implements PathTranslator<SRTMTileIndex> {
    
    @Override
    public String toFileSystemPath(SRTMTileIndex k) {
        return k.toString() + ".hgt.zip";
    }

    private static final Pattern fileNamePattern = Pattern.compile(
            "([NS][0-9]{2}[EW][0-9]{3}).hgt.zip");
    
    @Override
    public SRTMTileIndex fromFileSystemPath(String path) {
        Matcher m = fileNamePattern.matcher(path);
        if (m.matches()) {
            return SRTMTileIndex.parseTileIndex(m.group(1));
        } else {
            return null;
        }
    }

}
