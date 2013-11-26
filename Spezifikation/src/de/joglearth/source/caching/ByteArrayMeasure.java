package de.joglearth.source.caching;

/**
 * Implements the {@link ObjectMeasure} interface in such way that it can measure the size
 * of byte arrays.
 */
public class ByteArrayMeasure implements ObjectMeasure<byte[]> {

	@Override
	public int getSize(byte[] t) {
		return t.length;
	}
}
