package de.joglearth.opengl;

/**
 * Structure holding handles to OpenGL buffers for vertex and index data.
 */
// TODO Do not use public attributes
public final class VertexBuffer {

    @Override
    public String toString() {
        return "VertexBuffer [vertices=" + vertices + ", indices=" + indices + ", primitiveType="
                + primitiveType + ", primitiveCount=" + indexCount + "]";
    }

    /**
     * The vertex buffer handle.
     */
    private int vertices;

    /**
     * The index buffer handle.
     */
    private int indices;

    /**
     * The type of primitive, as given by the GL constants GL_POINTS, GL_LINE_STRIP, GL_LINE_LOOP,
     * GL_LINES, GL_TRIANGLE_STRIP, GL_TRIANGLE_FAN, GL_TRIANGLES, GL_QUAD_STRIP, GL_QUADS and
     * GL_POLYGON.
     */
    private int primitiveType;

    private int indexCount;

    
    /**
     * Returns the OpenGL vertex buffer ID.
     * @return vertices
     */
    public int getVertices() {
        return vertices;
    }

    
    /**
     * Returns the OpenGL index buffer ID.
     * @return The buffer ID
     */
    public int getIndices() {
        return indices;
    }

    
    /**
     * Returns the type of primitives described by the index buffer
     * @return The type of primitives
     */
    public int getPrimitiveType() {
        return primitiveType;
    }

    
    /**
     * Returns the number of indices contained in the index buffer
     * @return The number of indices
     */
    public int getIndexCount() {
        return indexCount;
    }


    /**
     * Constructor.
     * 
     * @param vertices The vertex buffer handle
     * @param indices The index buffer handle
     */
    public VertexBuffer(int type, int count, int vertices, int indices) {
        if (count < 0 || vertices <= 0 || indices <= 0) {
            throw new IllegalArgumentException();
        }
        
        this.indices = indices;
        this.vertices = vertices;
        primitiveType = type;
        indexCount = count;
    }
}
