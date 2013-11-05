package de.joglearth.model;

public class HTTPSource<Key, Value> extends Source<Key, Value> {

	private String[] servers;
	
	public HTTPSource(RequestListener<Key, Value> owner, String[] servers) {
		super(owner);
		this.servers = servers;
	}
	
	// Kombiniert eine URL aus Server und Key
	protected String getURL(String server, Key key) {
		return server + key.toString();
	}

	@Override
	public Value requestObject(Key k) {
		// TODO Automatisch erstellter Methoden-Stub
		return null;
	}
	
}
