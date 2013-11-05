package de.joglearth.model;


// Reference-Typ ist String, ein Dateiname.
public class FileSystemCache<Key, Value> extends Cache<Key, Value, String> {

	// Der Ordner, in dem die Cache-Dateien liegen sollen. 
	// Evtl. bessere möglichkeit als String, irgendeine Directory-Klasse?!
	private String folder;
	

	// Owner ist das Objekt, das bei einem asynchron geladenen Datensatz 
	// benachrichtigt wird. Darf null sein.
	public FileSystemCache(RequestListener<Key, Value> owner, String folder) {
		super(owner);
		this.folder = folder;
	}
	
	@Override
	public Value requestObject(Key k) {
		return null;
	}

	@Override
	protected String addEntry(Value v) {
		// Datei erstellen
		return null;
	}

	@Override
	protected void removeEntry(String r) {
		// Datei löschen
	}

	@Override
	protected int getEntrySize(String r) {
		// Dateigröße zurückgeben
		return 0;
	}

	
}
