package de.joglearth.model;


// Key: Identifiziert Cache-Objekt, z.B. Kachelkoordinaten
// Value: Gecachter Typ
// Reference: Referenziert die tatsächlichen Daten, zB. Array-Index oder java.io.File
public abstract class Cache<Key, Value, Reference> extends Source<Key, Value>
		implements RequestListener<Key, Value> {
	
	
	// Owner ist das Objekt, das bei einem asynchron geladenen Datensatz 
	// benachrichtigt wird. Darf null sein.
	public Cache(RequestListener<Key, Value> owner) {
		super(owner);
	}

	// Versucht ein Paar in den Cache zu legen, verdrängt u.U. einen Wert.
	// Vollständig hier implementiert.
	public void putObject(Key k, Value v) {
		
	}
	
	// Versucht (asynchron) ein Objekt zu entnehmen. Kann u.U. nach Beschaffen 
	// des Objekts zu einem Aufruf von putObject führen.
	@Override
	public Value requestObject(Key k) {
		return null;
	}
	
	// Entfernt alle Einträge aus dem Cache, zB. um ein Cache-Verzeichnis zu leeren
	public void clear() {
		
	}
	
	// Speichert einen Datensatz im Cache-Medium (Datei, Array, ...). 
	// Wird von MemoryCache, FileSystemCache implementiert, die Implementierung gibt eine 
	// Referenz auf den Datensatz zurück, bei MemoryCaches Array-Indizes, 
	// bei Dateisystem-Caches Dateinamen, etc.
	protected abstract Reference addEntry(Value v);
	
	
	// Entfernt einen vorhandenen Datensatz. Wird von MemoryCache, FileSystemCache implementiert.
	protected abstract void removeEntry(Reference r);
	
	
	// Gibt den Speicherplatz in Bytes zurück, den ein existierender Datensatz auf dem 
	// Medium belegt. 
	// Wird von MemoryCache, FileSystemCache implementiert
	protected abstract int getEntrySize(Reference r);
	
}

