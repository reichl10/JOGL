package de.joglearth.geometry;

/**
 * Listener interface notified when the view parameters of the camera changes.
 */
public interface CameraListener {
    /**
     * Called whenever a camera setting changes.
     */    
	void cameraViewChanged();
}