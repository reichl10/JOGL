package de.joglearth.geometry;

/**
 * Listener interface notified when the view parameters of a {@link Camera} are changed.
 */
public interface CameraListener {
    
    /**
     * Is called whenever a setting of the {@link Camera} changes.
     */    
	void cameraViewChanged();
}