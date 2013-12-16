package de.joglearth.source.osm;

import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.Tile;
import de.joglearth.source.*;
import de.joglearth.surface.TiledMapType;
import de.joglearth.util.HTTP;


/**
 * Loads OpenStreetMap image tiles by their coordinates via HTTP.
 */
public class OSMTileSource implements Source<OSMTile, byte[]> {

    private final String[] servers;
    private int offset = 0;


    /**
     * Constructor. Initializes the {@link de.joglearth.source.osm.OSMTileSource}.
     * 
     * @param servers An array containing the server strings
     */
    public OSMTileSource(String[] servers) {
        this.servers = servers;
    }

    @Override
    public SourceResponse<byte[]> requestObject(OSMTile k, SourceListener<OSMTile, byte[]> sender) {
        return null;
    }

    private byte[] getOSMTile(Tile tile) {

        int y = (int) (((tile.getLatitudeFrom() + tile.getLatitudeTo()) / 2) / 180 * Math.PI);
        int x = (int) ((tile.getLongitudeFrom() + tile.getLongitudeTo()) / 2);

        int zoom = 0;
        int n = (int) Math.pow(2, zoom); // TODO n = 2^zoom
        int xtile = n * ((x + 180) / 360);
        int ytile = (int) (n * (1 - (Math.log(Math.tan(y) + 1 / Math.cos(y)) / Math.PI)) / 2);

        StringBuilder builder = new StringBuilder();
        byte[] response = null;
        
        int i = 0;
        while (response == null && i < servers.length) {
            builder.append(servers[offset]);
            builder.append("/");
            builder.append(zoom);
            builder.append("/");
            builder.append(xtile);
            builder.append("/");
            builder.append(ytile);
            builder.append(".png");
            
            response = HTTP.get(builder.toString(), null);
            
            i++;
            
            if (response == null) {
                offset++;
            }
            
            if (offset == servers.length) {
                offset = 0;
            } 
        }

        return null;
    }

    /**
     * Sets the type of an OpenStreetMap tile.
     * 
     * @param type <code>TiledMapType</code> of the tile
     */
    public void setTileType(TiledMapType type) {
        //TODO Zum Type passende Server besorgen.
    }
}
