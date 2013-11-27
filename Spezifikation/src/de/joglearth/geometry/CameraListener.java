package de.joglearth.geometry;

/**
 * Listener interface notified when the view parameters of a {@link Camera} are changed.
 */
public interface CameraListener {
    
    /**
     * Called whenever a setting of {@link Camera} changes.
     */    
	void cameraViewChanged();
}