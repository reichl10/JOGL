package de.joglearth.model;

import de.joglearth.model.*;
import de.joglearth.view.*;
import de.joglearth.controller.*;

public class Settings extends UpdateProvider {
	
	private String language;
	private boolean textureFilter;
	private int levelOfDetail;
	private boolean heigthMap;
	private boolean antialiasing;
	private int ramCache;
	private int hddCache;
	private Location[] userTags;
	
	public void load() {
		
	}
	
	public void save() {
		
	}

	public void addUserTag(Location location) {
		
	}
	
	public void removeUserTag(int index) {
		
	}

	public Location[] getUserTags() {
		return userTags;
	}
}