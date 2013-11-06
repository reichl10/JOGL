package de.joglearth.model;

import de.joglearth.controller.Camera;

/*
 * cache oder lokal in klasse speichern?
 */
public /*static*/class Nominatim {
	private Location point;
	
	public Location[] request(boolean global, boolean specificPoint) {
		if (global) {
			return globalRequest();
		} else {
			if (specificPoint) {
				return pointRequest();
			} else {
				return localRequest();
			}
		}
	}
	
	private Location[] globalRequest() {
		return null;
	}
	
	//ausschnitt
	private Location[] localRequest() {
		return null;
	}
	
	//aktuelle position erfragen (alle infos zur akutellen position)
	private Location[] pointRequest() {
		return null;
	}
}
