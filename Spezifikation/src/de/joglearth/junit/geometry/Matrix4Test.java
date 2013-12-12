package de.joglearth.junit.geometry;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.Test;

import de.joglearth.geometry.Matrix4;


public class Matrix4Test {

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

    }

    @Test
    public final void testClone() {
        /* it says column-first (i guess it should be column-major) */
        double[] init = { 1d, 5d, 9d, 13d, 2d, 6d, 10d, 14d, 3d, 7d, 11d, 15d, 4d, 8d, 12d, 16d };
        Matrix4 matrix4 = new Matrix4(init);
        Matrix4 clonedMatrix4 = matrix4.clone();
        assertNotSame(matrix4, clonedMatrix4);
        assertArrayEquals(matrix4.doubles(), clonedMatrix4.doubles(), 0d);
    }

    @Test
    public final void testMatrix4DoubleArray() {
        double[] init = { 1d, 5d, 9d, 13d, 2d, 6d, 10d, 14d, 3d, 7d, 11d, 15d, 4d, 8d, 12d, 16d };
        Matrix4 matrix4 = new Matrix4(init);
        assertArrayEquals(matrix4.doubles(), init, 0d);
    }

    @Test
    public final void testMultDoubleArray() {
        double[] init = { 1d, 5d, 9d, 13d, 2d, 6d, 10d, 14d, 3d, 7d, 11d, 15d, 4d, 8d, 12d, 16d };
        double[] multArray = { 3d, 3d, 3d, 3d, 3d, 3d, 3d, 3d, 3d, 3d, 3d, 3d, 3d, 3d, 3d, 3d };
        double[] result = {90.000d, 202.000d, 314.000d, 426.000d, 100.000d, 228.000d, 356.000d, 484.000d, 110.000d, 254.000d, 398.000d, 542.000d, 120.000d, 280.000d, 440.000d, 600.000d};
        // TODO: finish
        Matrix4 matrix4 = new Matrix4(init);
        matrix4.mult(multArray);
        
        double[] res = matrix4.doubles();
        /*
        System.out.print("{");
        for (int i = 0; i < res.length; i++)
            System.out.print(res[i]+",");
        System.out.println("}");
        */
        assertArrayEquals(result, matrix4.doubles(), 0.0001d);
    }

    @Test
    public final void testMultMatrix4() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public final void testAddDoubleArray() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public final void testAddMatrix4() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public final void testDoubles() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public final void testTranslateDoubleDoubleDouble() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public final void testTranslateVector3() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public final void testRotate() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public final void testScaleDoubleDoubleDouble() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public final void testScaleVector3() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public final void testInverse() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public final void testTransform() {
        fail("Not yet implemented"); // TODO
    }

}
