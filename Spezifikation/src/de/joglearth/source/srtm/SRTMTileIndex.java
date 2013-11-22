package de.joglearth.source.srtm;

public final class SRTMTileIndex {

    int longitude;
    int latitude;


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
        return String.format("%c%0.2d%c%0.2d", latitude >= 0 ? 'N' : 'S', Math.abs(latitude),
                longitude >= 0 ? 'E' : 'W', Math.abs(longitude));
    }

}
