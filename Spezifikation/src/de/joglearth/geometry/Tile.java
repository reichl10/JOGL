package de.joglearth.geometry;


/**
 * A structure identifying a single OpenStreetMap surface tile.
 * @author Fabian Knorr
 *
 */
public class Tile implements Cloneable {
	
	public int zoom, lonIndex, latIndex;
	
	public Tile(int zoom, int lonIndex, int latIndex)
	{
	    this.zoom = zoom;
	    this.lonIndex = lonIndex;
	    this.latIndex = latIndex;
	}
	
	public float angle(int steps) {
	    return (float) (Math.pow(0.5, zoom) * steps % 1 * 2 * Math.PI);
	}
    
    public float longitudeFrom() {
        return angle(lonIndex);
    }
    
    public float longitudeTo() {
        return angle(lonIndex + 1);
    }
    
    public float latitudeFrom() {
        return angle(latIndex);
    }
    
    public float latitudeTo() {
        return angle(latIndex + 1);
    }
}

