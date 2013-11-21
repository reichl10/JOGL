package de.joglearth.rendering;

/**
 * 
 * Container class to save vertices, normals and texture coordinates and other parameters to build a
 * tile.
 * 
 */
public class Mesh {

    private float[] vertices, normals, texCoords;
    private int[]   indices;
    private int     vertexCount, normalCount, texCoordCount, indexCount;


    /**
     * Constructor. Initializes the {@link Mesh}
     * 
     * @param vertices
     * @param vertexCount
     * @param normals
     * @param normalCount
     * @param texCoords
     * @param texCoordCount
     * @param indices
     * @param indexCount
     */
    public Mesh(float[] vertices, int vertexCount, float[] normals,
            int normalCount, float[] texCoords, int texCoordCount,
            int[] indices, int indexCount) {
        this.vertices = vertices;
        this.vertexCount = vertexCount;
        this.normals = normals;
        this.normalCount = normalCount;
        this.texCoords = texCoords;
        this.texCoordCount = texCoordCount;
        this.indices = indices;
        this.indexCount = indexCount;
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

    //TODO Was machen die Counts
    public int getVertexCount() {
        return vertexCount;
    }

    public int getNormalCount() {
        return normalCount;
    }

    public int getTexCoordCount() {
        return texCoordCount;
    }

    public int getIndexCount() {
        return indexCount;
    }

}
