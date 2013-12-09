package de.joglearth.rendering;

import static javax.media.opengl.GL2.GL_T2F_N3F_V3F;


public final class MeshUtils {

    private MeshUtils() {}
    

    public static final int VERTEX_FORMAT = GL_T2F_N3F_V3F;
    public static final int VERTEX_SIZE = 8;
    

    public static void writeVertex(float[] vertices, int index, float vertexX, float vertexY,
            float vertexZ) {
        vertices[index + 5] = vertexX;
        vertices[index + 6] = vertexY;
        vertices[index + 7] = vertexZ;

    }

    public static void writeNormal(float[] vertices, int index, float normalX, float normalY,
            float normalZ) {
        vertices[index + 2] = normalX;
        vertices[index + 3] = normalY;
        vertices[index + 4] = normalZ;

    }

    public static void writeTextureCoordinates(float[] vertices, int index, float textureU,
            float textureV) {
        vertices[index + 0] = textureU;
        vertices[index + 1] = textureV;
    }
    
}
