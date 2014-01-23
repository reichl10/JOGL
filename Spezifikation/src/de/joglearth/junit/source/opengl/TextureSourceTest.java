package de.joglearth.junit.source.opengl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import javax.media.opengl.GL2;

import org.junit.Before;
import org.junit.Test;

import de.joglearth.geometry.Tile;
import de.joglearth.junit.GLTestWindow;
import de.joglearth.map.TileName;
import de.joglearth.map.osm.OSMMapType;
import de.joglearth.map.osm.OSMTile;
import de.joglearth.map.osm.OSMTileManager;
import de.joglearth.map.osm.OSMTileSource;
import de.joglearth.rendering.TextureLoader;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.SourceResponseType;
import de.joglearth.source.osm.OSMTileName;


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
                OSMTile key = new OSMTile(0, 0, 0);
				TextureLoader<OSMTile> source
					= new TextureLoader<OSMTile>(window.getGLContext(), Source<key, byte[]>(), OSMMapType.CYCLING);
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

	private class TestSourceListener implements SourceListener<TileName, Integer> {
		private Thread waiterThread;

		public TestSourceListener(Thread t) {
			waiterThread = t;
		}

		@Override
		public void requestCompleted(TileName key, Integer value) {
			waiterThread.notify();
		}

	}
}
