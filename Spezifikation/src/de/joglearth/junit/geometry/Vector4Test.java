package de.joglearth.junit.geometry;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import de.joglearth.geometry.Vector3;
import de.joglearth.geometry.Vector4;


public class Vector4Test {

    @Test
    public final void testVector4() {
        Vector4 resultVector = new Vector4();
        double[] resultArray = {resultVector.x, resultVector.y, resultVector.z, resultVector.w};
        double[] expected = {0d,0d,0d,1d};
        assertArrayEquals(expected, resultArray, 0.0000001d);
    }

    @Test
    public final void testVector4DoubleDoubleDoubleDouble() {
        Vector4 resultVector = new Vector4(0.1234d, 234.4033d, 23.343d, 232.309d);
        double[] resultArray = {resultVector.x, resultVector.y, resultVector.z, resultVector.w};
        double[] expected = {0.1234d, 234.4033d, 23.343d, 232.309d};
        assertArrayEquals(expected, resultArray, 0.0000001d);
    }

    @Test
    public final void testVector4Vector3() {
        Vector4 resultVector = new Vector4(new Vector3(0.3434d, 0.23478d, 32.34d));
        double[] resultArray = {resultVector.x, resultVector.y, resultVector.z, resultVector.w};
        double[] expected = {0.3434d, 0.23478d, 32.34d, 1d};
        assertArrayEquals(expected, resultArray, 0.0000001d);
    }

    @Test
    public final void testDivide() {
        Vector4 v1 = new Vector4(0.1234d, 234.4033d, 23.343d, 232.309d);
        Vector3 resultVector = v1.divide();
        double[] resultArray = {resultVector.x, resultVector.y, resultVector.z};
        double[] expected = {0.1234d/232.309d, 234.4033d/232.309d, 23.343d/232.309d};
        assertArrayEquals(expected, resultArray, 0.0000001d);
    }

}
