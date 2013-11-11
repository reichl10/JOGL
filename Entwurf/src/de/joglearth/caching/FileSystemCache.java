package de.joglearth.caching;

import de.joglearth.source.RequestListener;
import de.joglearth.source.SourceResponse;


// Reference-Typ ist String, ein Dateiname.
public class FileSystemCache<Key>
extends Cache<Key, CacheableBuffer> {

	// Der Ordner, in dem die Cache-Dateien liegen sollen. 
	// Evtl. bessere möglichkeit als String, irgendeine Directory-Klasse?!
	private String folder;
	

	// Owner ist das Objekt, das bei einem asynchron geladenen Datensatz 
	// benachrichtigt wird. Darf null sein.
	public FileSystemCache(RequestListener<Key, CacheableBuffer> owner, String folder) {
		super(owner);
		this.folder = folder;
	}
	
	@Override
	public SourceResponse<CacheableBuffer> requestObject(Key k) {
		return null;
	}

	@Override
	protected void addEntry(Key k, CacheableBuffer v) {
		// Datei erstellen
	}

	@Override
	protected void removeEntry(Key k) {
		// Datei löschen
	}


	
}
