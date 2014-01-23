package de.joglearth.height.srtm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.joglearth.source.caching.PathTranslator;


/**
 * Implements the {@link de.joglearth.source.caching.PathTranslator} interface for SRTM height data tiles.
 */
public class SRTMPathTranslator implements PathTranslator<SRTMTileName> {
    
    @Override
    public String toFileSystemPath(SRTMTileName k) {
        return k.toString() + ".hgt.zip";
    }

    private static final Pattern fileNamePattern = Pattern.compile(
            "([NS][0-9]{2}[EW][0-9]{3}).hgt.zip");
    
    @Override
    public SRTMTileName fromFileSystemPath(String path) {
        Matcher m = fileNamePattern.matcher(path);
        if (m.matches()) {
            return SRTMTileName.parseTileIndex(m.group(1));
        } else {
            return null;
        }
    }
}
