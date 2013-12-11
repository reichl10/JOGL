package de.joglearth.source.opengl;

/**
 * Structure holding handles to OpenGL buffers for vertex and index data.
 */
public class VertexBuffer {

    @Override
    public String toString() {
        return "VertexBuffer [vertices=" + vertices + ", indices=" + indices + ", primitiveType="
                + primitiveType + ", primitiveCount=" + primitiveCount + "]";
    }

    /**
     * The vertex buffer handle.
     */
    public int vertices;

    /**
     * The index buffer handle.
     */
    public int indices;

    /**
     * The type of primitive, as given by the GL constants GL_POINTS, GL_LINE_STRIP, GL_LINE_LOOP,
     * GL_LINES, GL_TRIANGLE_STRIP, GL_TRIANGLE_FAN, GL_TRIANGLES, GL_QUAD_STRIP, GL_QUADS and
     * GL_POLYGON.
     */
    public int primitiveType;

    /**
     * The number of primitives drawn from the index buffer.
     */
    public int primitiveCount;


    /**
     * Constructor.
     * 
     * @param vertices The vertex buffer handle
     * @param indices The index buffer handle
     */
    public VertexBuffer(int type, int count, int vertices, int indices) {
        this.indices = indices;
        this.vertices = vertices;
        primitiveType = type;
        primitiveCount = count;
    }

    /**
     * Default constructor.
     */
    public VertexBuffer() {
        // yolo
    }
}
