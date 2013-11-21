package de.joglearth.ui;

import de.joglearth.geometry.Camera;
import de.joglearth.geometry.Geometry;

/**
 * An object of this class is used to apply changes made to the swing gui to the {@link de.joglearth.geometry.Camera} object.
 */
public class GUIEventListener {

	private Camera camera;

	/**
	 * Constructor that takes the {@link de.joglearth.geometry.Camera} object the changes should be applyed to.
	 * @param camera
	 */
	public GUIEventListener(Camera camera) {
		this.camera = camera;
	}
	
	/**
	 * Takes the {@link de.joglearth.geometry.Geometry} to set the {@link de.joglearth.geometry.Camera} to.
	 * @param g the <code>Geometry</code> to set
	 */
	public void setGeometry(Geometry g) {
	}
	
	/**
	 * Takes the position to move the {@link de.joglearth.geometry.Camera} to as Degrees.
	 * @param latitude the latitude to move the <code>Camera</code> to
	 * @param longitude the longitude to move the <code>Camera</code> to
	 */
	public void setPosition(double latitude, double longitude) {
	}
}
