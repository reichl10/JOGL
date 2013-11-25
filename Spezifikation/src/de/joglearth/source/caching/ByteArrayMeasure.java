package de.joglearth.source.caching;

public class ByteArrayMeasure implements ObjectMeasure<byte[]> {

	@Override
	public int getSize(byte[] t) {
		return t.length;
	}

}
