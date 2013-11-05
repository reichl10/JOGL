package de.joglearth.model;


public abstract class Source<Key, Value> implements RequestListener<Key, Value> {
	
	// Wird benachrichtigt, sobald ein Datum eintrifft. Evtl nicht in diese Klasse?
	protected RequestListener<Key, Value> owner;
	
	@Override
	public void requestCompleted(Key k, Value v) {
		owner.requestCompleted(k, v);
	}
	
	public Source(RequestListener<Key, Value> owner) {
		this.owner = owner;
	}
	
	// Versucht ein Objekt zu laden. Wenn es lokal verfügbar ist, 
	// zB. gecacht, wird es zurückgegeben. Ansonsten wird versucht, es asynchron
	// zu laden, und es wird null zurückgegeben. Ist der asynchrone vorgang fertig,
	// wird owner.requestCompleted aufgerufen.
	public abstract Value requestObject(Key k);
	
}
