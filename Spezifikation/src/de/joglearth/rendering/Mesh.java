package de.joglearth.rendering;

/**
 * 
 * Container class to save vertices, normals and texture coordinates and other parameters to build a
 * tile.
 * 
 */
public class Mesh {

    private float[] vertices, normals, texCoords;
    private int[] indices;


    /**
     * Constructor. Initializes the {@link Mesh}
     * 
     * @param vertices
     * @param normals
     * @param texCoords
     * @param indices
     */
    public Mesh(float[] vertices, float[] normals, float[] texCoords,
            int[] indices) {
        this.vertices = vertices;
        this.normals = normals;
        this.texCoords = texCoords;
        this.indices = indices;
    }

    /**
     * Gets the vertices stored in the {@link Mesh}.
     * 
     * @return A float array of vertices.
     */
    public float[] getVertices() {
        return vertices;
    }

    /**
     * Gets the normals stored in the {@link Mesh}.
     * 
     * @return A float array of normals
     */
    public float[] getNormals() {
        return normals;
    }

    /**
     * Gets the texture coordinates stored in the {@link Mesh}.
     * 
     * @return A float array of texture coordinates
     */
    public float[] getTexCoords() {
        return texCoords;
    }

    /**
     * Gets the indices stored in the {@link Mesh}.
     * 
     * @return A int array of indices
     */
    public int[] getIndices() {
        return indices;
    }

}
