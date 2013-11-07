package de.joglearth.controller;

import de.joglearth.model.*;
import de.joglearth.view.*;
import de.joglearth.controller.*;

public class HeightMap extends UpdateProvider {

	public HeightMap() {		
		FileSystemCache<TileCoordinates, byte[]> fsCache = null;
		OSMTileSource<TileCoordinates, byte[]> source = null;
		cache = new MemoryCache<TileCoordinates, byte[]>(new TileListener(),
				fsCache, source);
	}
	
	// Gerundete Koordinaten in 2-Bogensekunden, zur SRTM-Indizierung.
	// Evtl. Format in Wirklichkeit anders.
	private static class TileCoordinates {
		public int longitude;
		public int latitude;
	}
	
	// Benachrichtigt den Renderer wenn eine neue Kachel ankommt.
	private class TileListener implements RequestListener<TileCoordinates, byte[]> {
		
		@Override
		public void requestCompleted(TileCoordinates k, byte[] v) {
			postUpdate();
		}
	}

	// Der (prim�re) Cache. K�mmert sich um den sekund�ren Plattencache und 
	// das Laden �ber HTTP (siehe Konstruktor von HeightMap)
	private MemoryCache<TileCoordinates, byte[]> cache;
	
	
	// Versucht die H�he an einem Punkt zu bestimmen, gibt 0 zur�ck, wenn die H�hendaten 
	// nicht im Cache sind.
	public float height(float longitude, float latitude) {
		/*l�ngen- breitengrad auf bogensekunden runden, tileCoordinate bestimmen
		 * kachel vom cache anfordern, h�henwert interpolieren(bestimmen).
		 */
		return 0;
	}
}