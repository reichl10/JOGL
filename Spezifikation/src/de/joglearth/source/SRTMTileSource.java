package de.joglearth.source;

import de.joglearth.geometry.Tile;


/**
 * The class SRTMTileSource is necessary, when the HightProfile is activated. This class uses the
 * HTTPUtils to get the SRTM data from NASA server. The size of the SRTM tiles is 90 x 90 meters.
 * The SRTM tiles include all information of a required point about the height over the sea level.
 * 
 */
public class SRTMTileSource implements Source<Tile, byte[]> {

    // Die Server, die der Reihe nach nach Daten gefragt werden sollen.
    // K�nnen entwerder einfache URLs a la "domain.tld/folder/to/data/" sein,
    // oder ein Format-String a la "domain.tld/data/%s/%s", der dann mit einer
    // �berschriebenen getURL-Methode aufgel�st wird.

    // Verwaltung? Sollen u.U. unerreichbare Server markiert werden?
    private String[] servers;


    // Owner: Wie bei Source
    /**
     * Constructor SRTMTileSource.
     * @param owner
     * @param servers
     */
    public SRTMTileSource(SourceListener<Tile, byte[]> owner, String[] servers) {
        this.servers = servers;
    }

    /**
     * 
     * @param server
     * @param key
     * @return
     */
    protected String getURL(String server, Tile key) {
        return server + key.toString();
    }

    /**
	 * 
	 */
    @Override
    public SourceResponse<byte[]> requestObject(Tile k, SourceListener<Tile, byte[]> sender) {
        return null;
    }
}
