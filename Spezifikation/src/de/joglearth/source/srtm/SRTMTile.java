package de.joglearth.source.srtm;

/**
 * Provides access to two-dimensional SRTM height data.
 */
public final class SRTMTile {

    /**
     * Constructor. Decodes the height information from binary SRTM data.
     * 
     * @param raw The binary data
     */
    public SRTMTile(byte[] raw) {

    }

    /**
     * Returns a two-dimensional array containing the height data interpolated to 1/(4^lod) the
     * size.
     * 
     * @param lod The <code>LevelOfDetail</code> of the data
     * @return Two-dimensional array containing the height data
     */
    public short[][] getTile(int lod) {
        return null;
    }
}
