package de.joglearth.source;

import de.joglearth.geometry.Tile;

public interface Source<Key, Value> {
	
	// Versucht ein Objekt zu laden. Wenn es lokal verfügbar ist, 
	// zB. gecacht, wird es zurückgegeben. Ansonsten wird versucht, es asynchron
	// zu laden, und es wird null zurückgegeben. Ist der asynchrone vorgang fertig,
	// wird owner.requestCompleted aufgerufen.
	SourceResponse<Value> requestObject(Key key, SourceListener<Key, Value> sender);
}
