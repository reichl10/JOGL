package de.joglearth.geometry;

/**
 * Listener interface notified when the view parameters of a camera change.
 */
public interface CameraListener {
    /**
     * Called whenever a camera setting changes.
     */    
	void cameraViewChanged();
}