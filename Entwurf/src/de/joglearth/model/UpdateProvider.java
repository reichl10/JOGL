package de.joglearth.model;

public class UpdateProvider {
	private UpdateListener[] listeners;

	public void addUpdateListener(UpdateListener l) {

	}
	
	public void removeUpdateListener(UpdateListener l) {

	}

	protected void postUpdate() {
		for (UpdateListener l : listeners) {
			l.post();
		}
	}
}
