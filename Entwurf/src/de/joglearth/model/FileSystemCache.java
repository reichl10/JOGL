package de.joglearth.model;

public class FileSystemCache<Key, Value> extends Cache<Key, Value, String> {

	private String folder;
	
	public FileSystemCache(RequestListener<Key, Value> owner, String folder) {
		super(owner);
		this.folder = folder;
	}
	
	@Override
	public Value requestObject(Key k) {
		return null;
	}

	@Override
	protected String addEntry(Value v) {
		return null;
	}

	@Override
	protected void removeEntry(String r) {
		
	}

	@Override
	protected int getEntrySize(String r) {
		return 0;
	}

	
}
