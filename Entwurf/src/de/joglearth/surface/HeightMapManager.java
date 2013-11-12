package de.joglearth.surface;

import de.joglearth.caching.FileSystemCache;
import de.joglearth.caching.MemoryCache;
import de.joglearth.geometry.GeoCoordinates;
import de.joglearth.geometry.Tile;
import de.joglearth.settings.SettingsListener;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SRTMTileSource;


public class HeightMapManager implements SettingsListener{

	// Der (prim�re) Cache. K�mmert sich um den sekund�ren Plattencache und 
	// das Laden �ber HTTP (siehe Konstruktor von HeightMap)
	private MemoryCache<Tile, byte[]> cache;
	
	private SurfaceNotifier notifier;
	

	public HeightMapManager() {		
		FileSystemCache<Tile> fsCache = null;
		SRTMTileSource source = null;
		cache = new MemoryCache<Tile, byte[]>();
	}
	
	// Versucht die H�he an einem Punkt zu bestimmen, gibt 0 zur�ck, wenn die H�hendaten 
	// nicht im Cache sind.
	public float getHeight(GeoCoordinates coords) {
		/*l�ngen- breitengrad auf bogensekunden runden, tileCoordinate bestimmen
		 * kachel vom cache anfordern, h�henwert interpolieren(bestimmen).
		 */
		return 0;
	}

	public void addSurfaceListener(SurfaceListener l) {
		notifier.addSurfaceListener(l);
	}
	
	public void removeSurfaceListener(SurfaceListener l) {
		notifier.removeSurfaceListener(l);
	}

	@Override
	public void settingsChanged(String key, Object valOld, Object valNew) {
		// TODO Automatisch erstellter Methoden-Stub
		
	}
}
