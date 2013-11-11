package de.joglearth.ui;

import de.joglearth.UpdateProvider;
import de.joglearth.geometry.Camera;
import de.joglearth.rendering.*;
import de.joglearth.source.*;
import de.joglearth.ui.*;

public class GUIEventListener extends UpdateProvider {

	private Camera camera;

	public GUIEventListener(Camera camera) {
		this.camera = camera;
	}
}
