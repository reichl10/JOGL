package de.joglearth.model;


public abstract class Source<Key, Value> implements RequestListener<Key, Value> {
	
	protected RequestListener<Key, Value> owner;
	
	@Override
	public void requestCompleted(Key k, Value v) {
		owner.requestCompleted(k, v);
	}
	
	public Source(RequestListener<Key, Value> owner) {
		this.owner = owner;
	}
	
	public abstract Value requestObject(Key k);
	
}
