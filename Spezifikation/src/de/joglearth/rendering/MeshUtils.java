package de.joglearth.rendering;

import static javax.media.opengl.GL2.GL_T2F_N3F_V3F;


public final class MeshUtils {

    private MeshUtils() {}


    public static final int VERTEX_FORMAT = GL_T2F_N3F_V3F;
    public static final int VERTEX_SIZE = 8;


    public static void writeVertex(float[] vertices, int index, double vertexX, double vertexY,
            double vertexZ) {
        vertices[index + 5] = (float) vertexX;
        vertices[index + 6] = (float) vertexY;
        vertices[index + 7] = (float) vertexZ;
    }

    public static void writeNormal(float[] vertices, int index, double normalX, double normalY,
            double normalZ) {
        vertices[index + 2] = (float) normalX;
        vertices[index + 3] = (float) normalY;
        vertices[index + 4] = (float) normalZ;
    }

    public static void writeTextureCoordinates(float[] vertices, int index, double textureU,
            double textureV) {
        vertices[index + 0] = (float) textureU;
        vertices[index + 1] = (float) textureV;
    }

}
