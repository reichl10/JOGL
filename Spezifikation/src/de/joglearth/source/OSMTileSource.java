package de.joglearth.source;

import de.joglearth.geometry.Tile;
import de.joglearth.rendering.*;
import de.joglearth.source.*;
import de.joglearth.ui.*;


/**
 * @uml.annotations uml_dependency=
 *                  "mmi:///#jsrctype^name=OSMTileType[jcu^name=OSMTileType.java[jpack^name=de.joglearth.source[jsrcroot^srcfolder=src[project^id=Entwurf]]]]$uml.Enumeration"
 */
public class OSMTileSource implements Source<Tile, byte[]> {

    // Die Server, die der Reihe nach nach Daten gefragt werden sollen.
    // K�nnen entwerder einfache URLs a la "domain.tld/folder/to/data/" sein,
    // oder ein Format-String a la "domain.tld/data/%s/%s", der dann mit einer
    // �berschriebenen getURL-Methode aufgel�st wird.

    // Verwaltung? Sollen u.U. unerreichbare Server markiert werden?
    private String[] servers;


    // Owner: Wie bei Source
    /**
     * Constructor OSMTileSource.
     * 
     * @param owner
     * @param servers
     */
    public OSMTileSource(SourceListener<Tile, byte[]> owner,
            String[] servers) {
        this.servers = servers;
    }

    /**
     * Gets a String representation of the server and the OSM tile ID.
     * 
     * @param server OSM tile server
     * @param key tile ID
     * @return String representation of the server and the OSM tile ID
     */
    protected String getURL(String server, Tile key) {
        return server + key.toString();
    }

    /**
	 * 
	 */
    @Override
    public SourceResponse<byte[]> requestObject(Tile k,
            SourceListener<Tile, byte[]> sender) {
        return null;
    }

    /**
     * Sets the type of an OpenStreetMap tile.
     * 
     * @param t type of the tile
     */
    public void setTileType(OSMTileType t) {

    }
}
