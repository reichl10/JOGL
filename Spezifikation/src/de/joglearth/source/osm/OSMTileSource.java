package de.joglearth.source.osm;

import de.joglearth.geometry.Tile;
import de.joglearth.rendering.*;
import de.joglearth.source.*;
import de.joglearth.ui.*;


/**
 * Loads OpenStreetMap image tiles by their coordinates via HTTP.
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

    // /**
    // * Gets a String representation of the server and the OSM tile ID.
    // *
    // * @param server OSM tile server
    // * @param key tile ID
    // * @return String representation of the server and the OSM tile ID
    // */
    // protected String getURL(String server, Tile key) {
    // return server + key.toString();
    // }

    @Override
    public SourceResponse<byte[]> requestObject(OSMTile k, SourceListener<OSMTile, byte[]> sender) {
        return null;
    }
}
