package de.joglearth.source.srtm;

/**
 * Structure identifying single SRTM height data tiles.
 */
public final class SRTMTileIndex {

    /**
     * The longitude index, as described by the SRTM standard.
     */
    public int longitude;
    
    /**
     * The latitude index, as described by the SRTM standard.
     */
    public int latitude;


    /**
     * Constructor.
     * @param lon The longitude index
     * @param lat The latitude index
     */
    public SRTMTileIndex(int lon, int lat) {
        longitude = lon;
        latitude = lat;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + latitude;
        result = prime * result + longitude;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SRTMTileIndex other = (SRTMTileIndex) obj;
        return this.longitude == other.longitude && this.latitude == other.latitude;
    }

    @Override
    public String toString() {
        return "SRTMTileIndex [longitude=" + longitude + ", latitude=" + latitude + "]";
    }
}
