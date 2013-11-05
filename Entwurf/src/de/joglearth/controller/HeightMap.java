package de.joglearth.controller;

import de.joglearth.model.FileSystemCache;
import de.joglearth.model.HTTPSource;
import de.joglearth.model.MemoryCache;
import de.joglearth.model.RequestListener;
import de.joglearth.view.Renderer;


public class HeightMap {
	
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
			renderer.post();
		}
	}

	// Der Renderer, der Benachrichtigungen wegen.
	private Renderer renderer;

	// Der (primäre) Cache. Kümmert sich um den sekundären Plattencache und 
	// das Laden über HTTP (siehe Konstruktor von HeightMap)
	private MemoryCache<TileCoordinates, byte[]> cache;
	
	
	public HeightMap(Renderer r) {		
		FileSystemCache<TileCoordinates, byte[]> fsCache = null;
		HTTPSource<TileCoordinates, byte[]> source = null;
		cache = new MemoryCache<TileCoordinates, byte[]>(new TileListener(), fsCache, source);
		renderer = r;
	}
	
	// Versucht die Höhe an einem Punkt zu bestimmen, gibt 0 zurück, wenn die Höhendaten 
	// nicht im Cache sind.
	public float height(float longitude, float latitude) {
		/*längen- breitengrad auf bogensekunden runden, tileCoordinate bestimmen
		 * kachel vom cache anfordern, höhenwert interpolieren(bestimmen).
		 */
		return 0;
	}
}
