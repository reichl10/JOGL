package de.joglearth.caching;

import de.joglearth.source.Source;


// Key: Identifiziert Cache-Objekt, z.B. Kachelkoordinaten
// Value: Gecachter Typ
// Reference: Referenziert die tatsächlichen Daten, zB. Array-Index oder java.io.File
public interface Cache<Key, Value>
extends Source<Key, Value> {
	
	// Versucht ein Paar in den Cache zu legen, verdrängt u.U. einen Wert.
	// Vollständig hier implementiert.
	void putObject(Key k, Value v);
	
	// Entfernt einen vorhandenen Datensatz. Wird von MemoryCache, FileSystemCache implementiert.
	void dropObject(Key k);
	
	Iterable<Key> getExistingObjects();
}

