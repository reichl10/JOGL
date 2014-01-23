package de.joglearth.rendering;

import static javax.media.opengl.GL2.*;
import de.joglearth.geometry.Vector3;


/**
 * Provides utility functions for constructing {@link Mesh} structures.
 */
public final class MeshUtils {

    private MeshUtils() {}

    /**
     * The vertex attribute layout assumed by the <code>write*</code> methods.
     */
    public static final int VERTEX_FORMAT = GL_T2F_N3F_V3F;
    
    /**
     * The number of floats per vertex in the layout assumed.
     */
    public static final int VERTEX_SIZE = 8;


    /**
     * Writes a vertex position.
     * @param vertices The vertex array.
     * @param index The offset of the current vertex in the vertex array
     * @param vertexX The x coordinate
     * @param vertexY The y coordinate
     * @param vertexZ The z coordinate
     */
    public static void writeVertex(float[] vertices, int index, double vertexX, double vertexY,
            double vertexZ) {
        vertices[index + 5] = (float) vertexX;
        vertices[index + 6] = (float) vertexY;
        vertices[index + 7] = (float) vertexZ;
    }
    
    
    /**
     * Writes a normal vector.
     * @param vertices The vertex array.
     * @param index The offset of the current vertex in the vertex array
     * @param normalX The x component
     * @param normalY The y component
     * @param normalZ The z component
     */
    public static void writeNormal(float[] vertices, int index, double normalX, double normalY,
            double normalZ) {
        vertices[index + 2] = (float) normalX;
        vertices[index + 3] = (float) normalY;
        vertices[index + 4] = (float) normalZ;
    }
    
    /**
     * Writes a pair of texture coordinates.
     * @param vertices The vertex array.
     * @param index The offset of the current vertex in the vertex array
     * @param textureU The horizontal component
     * @param textureV The vertical component
     */
    public static void writeTextureCoordinates(float[] vertices, int index, double textureU,
            double textureV) {
        vertices[index + 0] = (float) textureU;
        vertices[index + 1] = (float) textureV;
    }

    /**
     * Writes the interpolated values of two vertices at a new position.
     * The result is left*leftFactor + right*(1-leftFactor).
     * @param vertices The vertex array.
     * @param iLeft The offset of the left source vertex
     * @param iRight The offset of the right source vertex
     * @param iOut The offset of the target vertex
     * @param leftFactor The factor of the left vertex. 
     */
    public static void interpolateVertex(float[] vertices, int iLeft, int iRight, int iOut,
            double leftFactor) {
        for (int i = 0; i < VERTEX_SIZE; ++i) {
            vertices[iOut + i] = vertices[iLeft + i] * (float) leftFactor + vertices[iRight + i]
                    * (float) (1 - leftFactor);
        }
        
        // Normalize normal
        Vector3 normal = new Vector3(vertices[iOut + 2], vertices[iOut + 3], vertices[iOut + 4])
                .normalized();
        vertices[iOut + 2] = (float) normal.x;
        vertices[iOut + 3] = (float) normal.y;
        vertices[iOut + 4] = (float) normal.z;
    }
}
