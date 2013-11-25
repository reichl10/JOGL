package de.joglearth.source.caching;

import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;

// Reference-Typ ist String, ein Dateiname.
public class FileSystemCache<Key> implements Cache<Key, byte[]> {

	private String folder;
	private PathTranslator<Key> paths;

	public FileSystemCache(String folder, PathTranslator<Key> p) {
		this.folder = folder;
		paths = p;
	}

	@Override
	public SourceResponse<byte[]> requestObject(Key key,
			SourceListener<Key, byte[]> sender) {
		// Datei wenn mögl. asynchron laden
		return null;
	}

	@Override
	public void putObject(Key k, byte[] v) {
		// Datei schreiben
	}

	@Override
	public void dropObject(Key k) {
		// Datei löschen
	}

	@Override
	public Iterable<Key> getExistingObjects() {
		// Ordner durchsuchen, Dateien auflisten, Keys generieren, zurückgeben
		return null;
	}

}
