package de.joglearth.model;

public class HTTPSource<Key, Value> extends Source<Key, Value> {

	// Die Server, die der Reihe nach nach Daten gefragt werden sollen.
	// Können entwerder einfache URLs a la "domain.tld/folder/to/data/" sein,
	// oder ein Format-String a la "domain.tld/data/%s/%s", der dann mit einer 
	// Überschriebenen getURL-Methode aufgelöst wird.
	
	// Verwaltung? Sollen u.U. unerreichbare Server markiert werden?
	private String[] servers;
	
	// Owner: Wie bei Source
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
		// TODO Automatisch erstellter Methoden-StubS
		return null;
	}

	public void getWork() {
			return;
		}
	public class TileLoader implements Runnable{
		private Key key;
		TileLoader(Key k) {
			key = k;
		}
		@Override
		public void run() {
			// TODO: load sth
		}
	}
	public void tileLoaded() {
			return;
		}
}