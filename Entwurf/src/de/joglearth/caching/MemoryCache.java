package de.joglearth.caching;

import de.joglearth.rendering.*;
import de.joglearth.source.*;
import de.joglearth.ui.*;

// Cache-Reference-Typ ist Integer, da die interne Speicherung wohl
// mit Arrays oder HashMap<Integer, Value> funktioniert
public class MemoryCache<Key, Value> extends Cache<Key, Value> {
	
	// Sekundärer Cache, wird bei Fehlen eines Datums u.U. zuerst gefragt.
	// Darf null sein.
	private Source<Key, Value> secondary;
	
	// Datenquelle, z.B. HTTPSource. 
	private Source<Key, Value> source;

	
	// Owner ist das Objekt, das bei einem asynchron geladenen Datensatz 
	// benachrichtigt wird. Darf null sein.
	public MemoryCache(RequestListener<Key, Value> owner, 
			Source<Key, Value> secondary, 
			Source<Key, Value> source) {
		super(owner);
		this.secondary = secondary;
		this.source = source;
	}

	public void requestCompleted(Key k, Value v) {
		// Daten in den eigenen Cache einfügen
		super.requestCompleted(k, v);
	}
	
	@Override
	public Value requestObject(Key k) {
		return null;
	}

	@Override
	protected void addEntry(Key k, Value v) {
	}	

	@Override
	protected void removeEntry(Key k) {
		
	}

	@Override
	protected int getEntrySize(Key k) {
		return 0;
	}

}
