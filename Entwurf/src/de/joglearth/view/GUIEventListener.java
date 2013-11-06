package de.joglearth.view;

import de.joglearth.controller.Camera;
import de.joglearth.model.UpdateProvider;

public class GUIEventListener extends UpdateProvider {

	private Camera camera;

	public GUIEventListener(Camera camera) {
		this.camera = camera;
	}
}
