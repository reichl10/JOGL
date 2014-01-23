package de.joglearth.junit.source.opengl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.jogamp.opengl.util.texture.Texture;

import de.joglearth.junit.GLTestWindow;
import de.joglearth.map.osm.OSMMapConfiguration;
import de.joglearth.map.osm.OSMMapType;
import de.joglearth.map.osm.OSMTile;
import de.joglearth.rendering.TextureLoader;
import de.joglearth.source.Source;
import de.joglearth.source.SourceListener;
import de.joglearth.source.SourceResponse;
import de.joglearth.source.SourceResponseType;
import de.joglearth.util.HTTP;



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
					= new TextureLoader<OSMTile>(window.getGLContext(), new Source<OSMTile, byte[]>() {
					    SourceListener<OSMTile, byte[]> listener;
                        @Override
                        public SourceResponse<byte[]> requestObject(final OSMTile key,
                                SourceListener<OSMTile, byte[]> sender) {
                            SourceResponse<byte[]> sResponse = new SourceResponse<byte[]>(SourceResponseType.ASYNCHRONOUS, null);
                            listener = sender;
                            Thread thread = new Thread(new Runnable() {
                                
                                @Override
                                public void run() {
                                    byte[] b = HTTP.get("https://www.google.de/images/srpr/logo11w.png", null);
                                    listener.requestCompleted(key, b);
                                }
                            });
                            thread.start();
                            return sResponse;
                        }

                        @Override
                        public void dispose() {
                        }}, new OSMMapConfiguration(OSMMapType.CYCLING).getImageFormatSuffix());
				TestSourceListener listener = new TestSourceListener();
				SourceResponse<Texture> response = source.requestObject(key,
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

	private class TestSourceListener implements SourceListener<OSMTile, Texture> {

		public TestSourceListener() {
		}

		@Override
		public void requestCompleted(OSMTile key, Texture value) {
			synchronized (TextureSourceTest.this) {
                TextureSourceTest.this.notify();
            }
		}

	}
}
