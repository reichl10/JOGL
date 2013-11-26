package de.joglearth.source.osm;

import de.joglearth.geometry.Tile;
import de.joglearth.rendering.*;
import de.joglearth.source.*;
import de.joglearth.surface.MapType;
import de.joglearth.ui.*;


/**
 * Loads OpenStreetMap image tiles by their coordinates via HTTP.
 * 
 */
public class OSMTileSource implements Source<OSMTile, byte[]> {

    // Die Server, die der Reihe nach nach Daten gefragt werden sollen.
    // K�nnen entwerder einfache URLs a la "domain.tld/folder/to/data/" sein,
    // oder ein Format-String a la "domain.tld/data/%s/%s", der dann mit einer
    // �berschriebenen getURL-Methode aufgel�st wird.

    // Verwaltung? Sollen u.U. unerreichbare Server markiert werden?
    private final String[] servers;


    // Owner: Wie bei Source
    /**
     * Constructor. Initializes the {@link OSMTileSource}.
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
    
    /**
     * Sets the type of an OpenStreetMap tile.
     * 
     * @param t {@link MapType} of the tile
     */
    public void setTileType(MapType type) {

    }
}
