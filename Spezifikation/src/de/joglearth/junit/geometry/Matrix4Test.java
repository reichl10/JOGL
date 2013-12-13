package de.joglearth.junit.geometry;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.Test;

import de.joglearth.geometry.Matrix4;
import de.joglearth.geometry.Vector3;
import de.joglearth.geometry.Vector4;


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
        double[] result = {30.000000000000000d, 78.000000000000000d, 126.000000000000000d, 174.000000000000000d, 30.000000000000000d, 78.000000000000000d, 126.000000000000000d, 174.000000000000000d, 30.000000000000000d, 78.000000000000000d, 126.000000000000000d, 174.000000000000000d, 30.000000000000000d, 78.000000000000000d, 126.000000000000000d, 174.000000000000000d};
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
        double[] init = { 1d, 5d, 9d, 13d, 2d, 6d, 10d, 14d, 3d, 7d, 11d, 15d, 4d, 8d, 12d, 16d };
        double[] multArray = { 3d, 3d, 3d, 3d, 3d, 3d, 3d, 3d, 3d, 3d, 3d, 3d, 3d, 3d, 3d, 3d };
        double[] result = {30.000000000000000d, 78.000000000000000d, 126.000000000000000d, 174.000000000000000d, 30.000000000000000d, 78.000000000000000d, 126.000000000000000d, 174.000000000000000d, 30.000000000000000d, 78.000000000000000d, 126.000000000000000d, 174.000000000000000d, 30.000000000000000d, 78.000000000000000d, 126.000000000000000d, 174.000000000000000d};
        // TODO: finish
        Matrix4 matrix4 = new Matrix4(init);
        Matrix4 matrix2 = new Matrix4(multArray);
        matrix4.mult(matrix2);
        
        /*
        System.out.print("{");
        for (int i = 0; i < res.length; i++)
            System.out.print(res[i]+",");
        System.out.println("}");
        */
        assertArrayEquals(result, matrix4.doubles(), 0.0001d);
    }

    @Test
    public final void testAddDoubleArray() {
        double[] init = { 1d, 5d, 9d, 13d, 2d, 6d, 10d, 14d, 3d, 7d, 11d, 15d, 4d, 8d, 12d, 16d };
        double[] addArray = { 3d, 3d, 3d, 3d, 3d, 3d, 3d, 3d, 3d, 3d, 3d, 3d, 3d, 3d, 3d, 3d };
        double[] result = {4.000d, 8.000d, 12.000d, 16.000d, 5.000d, 9.000d, 13.000d, 17.000d, 6.000d, 10.000d, 14.000d, 18.000d, 7.000d, 11.000d, 15.000d, 19.000d};
        Matrix4 m1 = new Matrix4(init);
        m1.add(addArray);
        assertArrayEquals(result, m1.doubles(), 0.0001d);
    }

    @Test
    public final void testAddMatrix4() {
        double[] init = { 1d, 5d, 9d, 13d, 2d, 6d, 10d, 14d, 3d, 7d, 11d, 15d, 4d, 8d, 12d, 16d };
        double[] addArray = { 3d, 3d, 3d, 3d, 3d, 3d, 3d, 3d, 3d, 3d, 3d, 3d, 3d, 3d, 3d, 3d };
        double[] result = {4.000d, 8.000d, 12.000d, 16.000d, 5.000d, 9.000d, 13.000d, 17.000d, 6.000d, 10.000d, 14.000d, 18.000d, 7.000d, 11.000d, 15.000d, 19.000d};
        Matrix4 m1 = new Matrix4(init);
        Matrix4 m2 = new Matrix4(addArray);
        m1.add(m2);
        assertArrayEquals(result, m1.doubles(), 0.0001d);
    }

    @Test
    public final void testDoubles() {
        double[] init = { 1d, 5d, 9d, 13d, 2d, 6d, 10d, 14d, 3d, 7d, 11d, 15d, 4d, 8d, 12d, 16d };
        Matrix4 matrix4 = new Matrix4(init);
        assertArrayEquals(init, matrix4.doubles(), 0.0d);
    }

    @Test
    public final void testTranslateDoubleDoubleDouble() {
        double[] init = { 1d, 5d, 9d, 13d, 2d, 6d, 10d, 14d, 3d, 7d, 11d, 15d, 4d, 8d, 12d, 16d };
        double[] trans = { 0.23, 0.12, 0.1342 };
        double[] result = {1d, 5d, 9d, 13d, 2d, 6d, 10d, 14d, 3d, 7d, 11d, 15d, 4.8726d, 10.8094d, 17.4946d, 22.323d};
        Matrix4 m1 = new Matrix4(init);
        m1.translate(trans[0], trans[1], trans[2]);
        assertArrayEquals(result, m1.doubles(), 0.00001d);
    }

    @Test
    public final void testTranslateVector3() {
        double[] init = { 1d, 5d, 9d, 13d, 2d, 6d, 10d, 14d, 3d, 7d, 11d, 15d, 4d, 8d, 12d, 16d };
        double[] trans = { 0.23, 0.12, 0.1342 };
        double[] result = {1d, 5d, 9d, 13d, 2d, 6d, 10d, 14d, 3d, 7d, 11d, 15d, 4.8726d, 10.8094d, 17.4946d, 22.323d};
        Vector3 v3 = new Vector3(trans[0], trans[1], trans[2]);
        Matrix4 m1 = new Matrix4(init);
        m1.translate(v3);
        assertArrayEquals(result, m1.doubles(), 0.00001d);
    }

    @Test
    public final void testRotate() {
        fail("Not yet implemented"); // TODO
    }

    @Test
    public final void testScaleDoubleDoubleDouble() {
        double[] init = { 1d, 5d, 9d, 13d, 2d, 6d, 10d, 14d, 3d, 7d, 11d, 15d, 4d, 8d, 12d, 16d };
        double[] result = {0.300000000000000d, 1.500000000000000d, 2.700000000000000d, 3.900000000000000d, 0.200000000000000d, 0.600000000000000d, 1.000000000000000d, 1.400000000000000d, 0.600000000000000d, 1.400000000000000d, 2.200000000000000d, 3.000000000000000d, 4.000000000000000d, 8.000000000000000d, 12.000000000000000d, 16.000000000000000d};
        Matrix4 m1 = new Matrix4(init);
        m1.scale(0.3, 0.1, 0.2);
        assertArrayEquals(result, m1.doubles(), 0.000001d);
    }

    @Test
    public final void testScaleVector3() {
        double[] init = { 1d, 5d, 9d, 13d, 2d, 6d, 10d, 14d, 3d, 7d, 11d, 15d, 4d, 8d, 12d, 16d };
        double[] result = {0.300000000000000d, 1.500000000000000d, 2.700000000000000d, 3.900000000000000d, 0.200000000000000d, 0.600000000000000d, 1.000000000000000d, 1.400000000000000d, 0.600000000000000d, 1.400000000000000d, 2.200000000000000d, 3.000000000000000d, 4.000000000000000d, 8.000000000000000d, 12.000000000000000d, 16.000000000000000d};
        Matrix4 m2 = new Matrix4(init);
        m2.scale(new Vector3(0.3, 0.1, 0.2));
        assertArrayEquals(result, m2.doubles(), 0.00001d);
    }

    @Test
    public final void testInverse() {
        double[] init = { 1d, 5d, 9d, 13d, 2d, 6d, 14d, 11d, 3d, 7d, 13d, 15d, 4d, 8d, 12d, 16d };
        double[] result = {-0.833333333333334d, -0.666666666666667d, 1.833333333333340d, -0.583333333333334d, 0.666666666666668d, 1.000000000000000d, -3.000000000000000d, 1.583333333333340d, -0.166666666666667d, 0.000000000000000d, 0.500000000000001d, -0.333333333333334d, 0.000000000000000d, -0.333333333333334d, 0.666666666666668d, -0.333333333333334d };
        Matrix4 m1 = new Matrix4(init);
        Matrix4 m2 = m1.inverse();
        double[] res = m2.doubles();
        
        /*System.out.print("iverse {");
        for (int i = 0; i < res.length; i++)
            System.out.print(res[i]+",");
        System.out.println("}");*/
        assertArrayEquals(result, res, 0.00000001d);
    }

    @Test
    public final void testTransform() {
        double[] init = { 1d, 5d, 9d, 13d, 2d, 6d, 10d, 14d, 3d, 7d, 11d, 15d, 4d, 8d, 12d, 16d };
        double[] result = {5.4d, 11.8d, 19.6d, 24};
        Matrix4 m1 = new Matrix4(init);
        // transform with 0.1,0.2,0.3,1
        Vector4 vector4 = m1.transform(new Vector3(0.1,0.2,0.3));
        double[] got = {vector4.x, vector4.y, vector4.z, vector4.w};
        assertArrayEquals(result, got, 0.00001d);
    }

}
