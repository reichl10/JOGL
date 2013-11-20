package de.joglearth.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import de.joglearth.geometry.Camera;

/**
 * An Object of this class is used to handle user interaction that occurs on the {@link GLCanvas}. 
 */
public class ViewEventListener implements MouseWheelListener, MouseListener,
		MouseMotionListener {

	private Camera camera;

	/**
	 * Constructor taking a {@link Camera} object that the movements will be
	 * applied on.
	 * 
	 * @param camera the <code>Camera</code>
	 */
	public ViewEventListener(Camera camera) {
		this.camera = camera;
	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {

	}
}
