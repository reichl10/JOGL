package de.joglearth.geometry;

/**
 * Listener interface notified when the view parameters of a {@link de.joglearth.geometry.Camera}
 * are changed.
 */
public interface CameraListener {

    /**
     * Is called whenever a setting of the {@link de.joglearth.geometry.Camera} changes.
     */
    void cameraViewChanged();
}
