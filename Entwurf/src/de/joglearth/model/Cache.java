package de.joglearth.model;

// Key: Identifiziert Cache-Objekt, zB Kachelkoordinaten
// Value: Gecachter Typ
// Reference: Referenziert die tatsächlichen Daten, zB. Array-Index oder java.io.File
public abstract class Cache<Key, Value, Reference> extends Source<Key, Value>
		implements RequestListener<Key, Value> {
	
	public Cache(RequestListener<Key, Value> owner) {
		super(owner);
	}

	public void putObject(Key k, Value v) {
		
	}
	
	@Override
	public Value requestObject(Key k) {
		return null;
	}
	
	protected abstract Reference addEntry(Value v);
	
	protected abstract void removeEntry(Reference r);
	
	protected abstract int getEntrySize(Reference r);
	
}
