package de.joglearth.source;

import de.joglearth.geometry.Tile;
import de.joglearth.rendering.*;
import de.joglearth.source.*;
import de.joglearth.ui.*;

public class OSMTileSource extends Source<Tile, byte[]> {

	// Die Server, die der Reihe nach nach Daten gefragt werden sollen.
	// K�nnen entwerder einfache URLs a la "domain.tld/folder/to/data/" sein,
	// oder ein Format-String a la "domain.tld/data/%s/%s", der dann mit einer 
	// �berschriebenen getURL-Methode aufgel�st wird.
	
	// Verwaltung? Sollen u.U. unerreichbare Server markiert werden?
	private String[] servers;
	
	// Owner: Wie bei Source
	public OSMTileSource(RequestListener<Tile, byte[]> owner, String[] servers) {
		super(owner);
		this.servers = servers;
	}
	
	protected String getURL(String server, Tile key) {
		return server + key.toString();
	}

	@Override
	public byte[] requestObject(Tile k) {
		return null;
	}
	
	public void setTileType(TileType t) {
		
	}
	
}
