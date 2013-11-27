package de.joglearth.source.caching;

/**
 * Implements the ObjectMeasure interface for byte arrays, treating the array length as the size.
 */
public class ByteArrayMeasure implements ObjectMeasure<byte[]> {

    @Override
    public int getSize(byte[] t) {
        return t.length;
    }
}
