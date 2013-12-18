package de.joglearth.junit.source.opengl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import javax.media.opengl.GL2;

import org.junit.Before;
import org.junit.Test;

import de.joglearth.geometry.Tile;
import de.joglearth.junit.GLTestWindow;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.SourceResponseType;
import de.joglearth.source.opengl.TextureSource;
import de.joglearth.source.osm.OSMTile;
import de.joglearth.source.osm.OSMTileManager;
import de.joglearth.source.osm.OSMTileSource;
import de.joglearth.surface.TiledMapType;


public class TextureSourceTest {

	private GLTestWindow window;

	@Before
	public void setUp() {
		window = new GLTestWindow();
	}

	@Test(timeout = 10000)
	public final void testRequestObject() throws Throwable {
		window.display(new Runnable() {
			@Override
			public void run() {
				TextureSource<OSMTile> source
					= new TextureSource<OSMTile>(window.getGL(), OSMTileManager.getInstance());
				OSMTile key = new OSMTile(new Tile(0, 0, 0), TiledMapType.SKIING);
				TestSourceListener listener = new TestSourceListener(Thread
						.currentThread());
				SourceResponse<Integer> response = source.requestObject(key,
						listener);
				if (response.response == SourceResponseType.MISSING) {
					fail("We need to Have a Texture :(");
				} else if (response.response == SourceResponseType.SYNCHRONOUS) {
					assertNotNull(response.value);
				} else {
					synchronized (this) {
						try {
							this.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
	}

	private class TestSourceListener implements SourceListener<OSMTile, Integer> {
		private Thread waiterThread;

		public TestSourceListener(Thread t) {
			waiterThread = t;
		}

		@Override
		public void requestCompleted(OSMTile key, Integer value) {
			waiterThread.notify();
		}

	}
}
