package de.joglearth.rendering;

import javax.media.opengl.GL2;


/**
 * Container class to save vertices, normals and texture coordinates and other parameters to build a
 * tile.
 */
public final class Mesh {

    /**
     * The array of vertices. Elements are treated as dictated by the <code>glVertexFormat</code>.
     */
    public float[] vertices       = null;

    /**
     * The vertex format, as specified by OpenGL.
     */
    public int     glVertexFormat = GL2.GL_T2F_N3F_V3F;

    /**
     * The array of indices used to iterate over the vertex array.
     */
    public int[]   indices        = null;


    /**
     * Constructor. Initializes the {@link de.joglearth.rendering.Mesh}
     * 
     * @param vertices The array of vertices, normals and texture coordinates according to the
     *        vertex format
     * @param glVertexFormat The OpenGL vertex format used. Describes the layout of the vertex
     *        array.
     * @param indices The array of indices
     */
    public Mesh(float[] vertices, int glVertexFormat, int[] indices) {
        this.vertices = vertices;
        this.glVertexFormat = glVertexFormat;
    }

    /**
     * Default constructor. Initializes an empty {@link de.joglearth.rendering.Mesh}.
     */
    public Mesh() {

    }

}
