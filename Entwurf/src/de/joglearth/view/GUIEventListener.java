package de.joglearth.view;

import de.joglearth.model.*;
import de.joglearth.view.*;
import de.joglearth.controller.*;

public class GUIEventListener extends UpdateProvider {

	private Camera camera;

	public GUIEventListener(Camera camera) {
		this.camera = camera;
	}
}
