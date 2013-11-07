package de.joglearth.model;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public abstract class Source<Key, Value> implements RequestListener<Key, Value> {
	
	// Wird benachrichtigt, sobald ein Datum eintrifft. Evtl nicht in diese Klasse?
	protected RequestListener<Key, Value> owner;
	protected ExecutorService executor;
	
	@Override
	public void requestCompleted(Key k, Value v) {
		owner.requestCompleted(k, v);
	}
	
	public Source(RequestListener<Key, Value> owner) {
		this.owner = owner;
	}
	
	public boolean queryObject(Key k) {
		return false;
	}
	
	// Versucht ein Objekt zu laden. Wenn es lokal verfügbar ist, 
	// zB. gecacht, wird es zurückgegeben. Ansonsten wird versucht, es asynchron
	// zu laden, und es wird null zurückgegeben. Ist der asynchrone vorgang fertig,
	// wird owner.requestCompleted aufgerufen.
	public abstract Value requestObject(Key k);
	
}
