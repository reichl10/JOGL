package de.joglearth.rendering;

import de.joglearth.geometry.Tile;
import static java.lang.Math.*;
import static javax.media.opengl.GL2.*;


/**
 * Generates {@link de.joglearth.rendering.Mesh}es for tiles on the globe.
 * 
 */
public class SphereTessellator implements Tessellator {

    // Each vertex consists of 8 floats:
    // texture coordinates (u, v), normal (x, y), position (x, y, z)
    private final static int vertexFormat = GL_T2F_N3F_V3F;


    private void writeVertex(float[] vertices, Integer index, float vertexX, float vertexY,
            float vertexZ, float normalX, float normalY, float normalZ, float textureU,
            float textureV) {
        // The layout is determined by vertexFormat
        vertices[index + 0] = textureU;
        vertices[index + 1] = textureV;
        vertices[index + 2] = normalX;
        vertices[index + 3] = normalY;
        vertices[index + 4] = normalZ;
        vertices[index + 5] = vertexX;
        vertices[index + 6] = vertexY;
        vertices[index + 7] = vertexZ;
        index += 8;
    }
    
    @Override
    public Mesh tessellateTile(Tile tile, int subdivisions, boolean heightMap) {
        return null;
    }

}
