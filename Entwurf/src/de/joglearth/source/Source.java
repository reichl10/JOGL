package de.joglearth.source;

import de.joglearth.geometry.Tile;

/**
 * @uml.annotations 
 *    uml_dependency="mmi:///#jsrctype^name=SourceResponse[jcu^name=SourceResponse.java[jpack^name=de.joglearth.source[jsrcroot^srcfolder=src[project^id=Entwurf]]]]$uml.Class"
 */
public interface Source<Key, Value> {
	
	// Versucht ein Objekt zu laden. Wenn es lokal verfügbar ist, 
	// zB. gecacht, wird es zurückgegeben. Ansonsten wird versucht, es asynchron
	// zu laden, und es wird null zurückgegeben. Ist der asynchrone vorgang fertig,
	// wird owner.requestCompleted aufgerufen.
	SourceResponse<Value> requestObject(Key key, SourceListener<Key, Value> sender);
}
