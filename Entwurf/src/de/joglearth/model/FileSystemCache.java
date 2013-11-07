package de.joglearth.model;


// Reference-Typ ist String, ein Dateiname.
public class FileSystemCache<Key> extends Cache<Key, byte[]> {

	// Der Ordner, in dem die Cache-Dateien liegen sollen. 
	// Evtl. bessere möglichkeit als String, irgendeine Directory-Klasse?!
	private String folder;
	

	// Owner ist das Objekt, das bei einem asynchron geladenen Datensatz 
	// benachrichtigt wird. Darf null sein.
	public FileSystemCache(RequestListener<Key, byte[]> owner, String folder) {
		super(owner);
		this.folder = folder;
	}
	
	@Override
	public byte[] requestObject(Key k) {
		return null;
	}

	@Override
	protected void addEntry(Key k, byte[] v) {
		// Datei erstellen
	}

	@Override
	protected void removeEntry(Key k) {
		// Datei löschen
	}

	@Override
	protected int getEntrySize(Key k) {
		// Dateigröße zurückgeben
		return 0;
	}

	
}
