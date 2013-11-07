package de.joglearth.controller;

import de.joglearth.model.*;
import de.joglearth.view.*;
import de.joglearth.controller.*;

public class HeightMap extends UpdateProvider {

	public HeightMap() {		
		FileSystemCache<TileCoordinates, byte[]> fsCache = null;
		HTTPSource<TileCoordinates, byte[]> source = null;
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

	// Der (primäre) Cache. Kümmert sich um den sekundären Plattencache und 
	// das Laden über HTTP (siehe Konstruktor von HeightMap)
	private MemoryCache<TileCoordinates, byte[]> cache;
	
	
	// Versucht die Höhe an einem Punkt zu bestimmen, gibt 0 zurück, wenn die Höhendaten 
	// nicht im Cache sind.
	public float height(float longitude, float latitude) {
		/*längen- breitengrad auf bogensekunden runden, tileCoordinate bestimmen
		 * kachel vom cache anfordern, höhenwert interpolieren(bestimmen).
		 */
		return 0;
	}
}