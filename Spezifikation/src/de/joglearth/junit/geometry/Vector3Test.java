package de.joglearth.junit.geometry;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import de.joglearth.geometry.Vector3;


public class Vector3Test {

    @Test
    public final void testVector3() {
        Vector3 vector3 = new Vector3();
        assertEquals(0.0d, vector3.x, 0.00000001d);
        assertEquals(0.0d, vector3.y, 0.00000001d);
        assertEquals(0.0d, vector3.z, 0.00000001d);
    }

    @Test
    public final void testVector3DoubleDoubleDouble() {
        Vector3 vector3 = new Vector3(0.1, 0.2, 0.3);
        assertEquals(0.1d, vector3.x, 0.00000001d);
        assertEquals(0.2d, vector3.y, 0.00000001d);
        assertEquals(0.3d, vector3.z, 0.00000001d);
    }

    @Test
    public final void testTo() {
        Vector3 vector1 = new Vector3(0.1d, 0.2d, 0.3d);
        Vector3 vector2 = new Vector3(0.5424d, 0.231d, 17.74d);
        Vector3 result = vector1.to(vector2);
        double[] resultArray = {result.x, result.y, result.z};
        double[] expected = {-0.4424, -0.031, -17.44};
        assertArrayEquals(expected, resultArray, 0.0000001d);
    }

    @Test
    public final void testTimes() {
        Vector3 vector1 = new Vector3(0.1d, 0.2d, 0.3d);
        double factor = 0.231;
        double[] expected = {0.0231,0.0462,0.0693};
        Vector3 resultVector = vector1.times(factor);
        double[] resultArray = {resultVector.x, resultVector.y, resultVector.z};
        assertArrayEquals(expected, resultArray, 0.0000001d);
    }

    @Test
    public final void testPlus() {
        Vector3 vector1 = new Vector3(0.1d, 0.2d, 0.3d);
        Vector3 vector2 = new Vector3(0.0231d, 0.0462d, 0.0693);
        double[] expected = {0.1231,0.2462,0.3693};
        Vector3 resultVector = vector1.plus(vector2);
        double[] resultArray = {resultVector.x, resultVector.y, resultVector.z};
        assertArrayEquals(expected, resultArray, 0.0000001d);
    }

    @Test
    public final void testMinus() {
        Vector3 vector1 = new Vector3(0.1d, 0.2d, 0.3d);
        Vector3 vector2 = new Vector3(0.0231d, 0.0462d, 0.0693);
        double[] expected = {0.0769,0.1538,0.2307};
        Vector3 resultVector = vector1.minus(vector2);
        double[] resultArray = {resultVector.x, resultVector.y, resultVector.z};
        assertArrayEquals(expected, resultArray, 0.0000001d);
    }

    @Test
    public final void testLength() {
        Vector3 vector1 = new Vector3(1.312d, 34.23d, 42.213d);
        double expected = Math.sqrt(1.312d*1.312d + 34.23d*34.23d + 42.213d*42.213d);
        assertEquals(expected, vector1.length(), 0.000000001d);
    }

    @Test
    public final void testAdd() {
        Vector3 vector1 = new Vector3(1.312d, 34.23d, 42.213d);
        /// TODO: what is add ??? vector1.a
        fail("Not yet implemented"); // TODO
    }

    @Test
    public final void testNormalized() {
        Vector3 vector1 = new Vector3(1.312d, 34.23d, 42.213d);
        double length = Math.sqrt(1.312d*1.312d + 34.23d*34.23d + 42.213d*42.213d);
        double[] expected = {1.312d/length, 34.23d/length, 42.213d/length};
        Vector3 resultVector = vector1.normalized();
        double[] resultArray = {resultVector.x, resultVector.y, resultVector.z};
        assertArrayEquals(expected, resultArray, 0.0000001d);
    }

    @Test
    public final void testCrossProduct() {
        Vector3 vector1 = new Vector3(1.312d, 34.23d, 42.213d);
        Vector3 vector2 = new Vector3(34.3193d, 2.231d, 23.23d);
        Vector3 resultVector = vector1.crossProduct(vector2);
        double[] expected = {700.985697, 1418.242851, -1171.822567};
        double[] resultArray = {resultVector.x, resultVector.y, resultVector.z};
        assertArrayEquals(expected, resultArray, 0.000001d);
    }

    @Test
    public final void testEqualsObject() {
        Vector3 vector1 = new Vector3();
        Vector3 vector2 = new Vector3();
        assertEquals(vector1, vector2);
        double x = 0.0, y = 0.0, z = 0.0;
        for (;x <= 10.0; x=x+0.323) {
            for (;y <= 10.0; y=y+0.2211) {
                for (;z <= 10.0; z=z+0.0432) {
                    Vector3 v1 = new Vector3(x, y, z);
                    Vector3 v2 = new Vector3(10.0d-x, 10.0d-y, 10.0d-z);
                    if (10.0d-x == x && 10.0d-y == y && 10-z == z) {
                        assertEquals(v1, v2);
                    } else {
                        assertNotEquals(v1, v2);
                    }
                }
            }
        }
    }

}
