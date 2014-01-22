package de.joglearth.rendering;

import java.util.Arrays;

import static javax.media.opengl.GL2.*;


/**
 * Container class to save vertices, normals and texture coordinates and other parameters to build a
 * tile.
 */
// TODO do not use public attributes
public final class Mesh {

    @Override
    public String toString() {
        return "Mesh [vertices=" + Arrays.toString(vertices) + "\n" +", glVertexFormat="+ vertexFormat
                + "\n" + ", indices=" + Arrays.toString(indices) + "]";
    }

    /**
     * The array of vertices. Elements are treated as dictated by the <code>glVertexFormat</code>.
     */
    public float[] vertices       = null;

    /**
     * The vertex format, as specified by OpenGL.
     */
    public int     vertexFormat = GL_T2F_N3F_V3F;

    /**
     * The array of indices used to iterate over the vertex array.
     */
    public int[]   indices        = null;
    
    /**
     * The type of primitive, as given by the GL constants GL_POINTS, GL_LINE_STRIP, GL_LINE_LOOP,
     * GL_LINES, GL_TRIANGLE_STRIP, GL_TRIANGLE_FAN, GL_TRIANGLES, GL_QUAD_STRIP, GL_QUADS and
     * GL_POLYGON.
     */
    public int primitiveType = GL_TRIANGLES;
    
    /**
     * The number of primitives to draw from the index array.
     */
    public int indexCount = 0;


    /**
     * Constructor. Initializes the {@link de.joglearth.rendering.Mesh}
     * 
     * @param vertices The array of vertices, normals and texture coordinates according to the
     *        vertex format
     * @param glVertexFormat The OpenGL vertex format used. Describes the layout of the vertex
     *        array.
     * @param indices The array of indices
     */
    public Mesh(int glVertexFormat, float[] vertices, int primitiveType, int[] indices,
            int primitiveCount) {
        this.vertices = vertices;
        this.vertexFormat = glVertexFormat;
        this.indices = indices;
        this.primitiveType = primitiveType;
        this.indexCount = primitiveCount;
    }

    /**
     * Default constructor. Initializes an empty {@link de.joglearth.rendering.Mesh}.
     */
    public Mesh() {

    }
}
