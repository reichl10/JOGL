package de.joglearth.opengl;

import com.jogamp.opengl.util.texture.Texture;

import de.joglearth.geometry.Matrix4;
import de.joglearth.geometry.TransformedTile;


/**
 * Structure that creates a texture that contains a certain transformation matrix. It is used for
 * the different transformations that exist.
 */
public final class TransformedTexture {
    

    /**
     * The texture that is given in a certain transformation.
     */
    public final Texture texture;
    
    /**
     * The transformation matrix that describes the transformation of the texture.
     */
    public final Matrix4 transformation;
    
    /**
     * Creates a {@link TransformedTexture} as it assigns values to the attributes of the tile.
     * 
     * @param texture The texture that is given under a transformation
     * @param transformation The transformation matrix of the texture
     */
    public TransformedTexture(Texture texture, Matrix4 transformation) {
        this.texture = texture;
        this.transformation = transformation;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((texture == null) ? 0 : texture.hashCode());
        result = prime * result + ((transformation == null) ? 0 : transformation.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TransformedTexture other = (TransformedTexture) obj;
        if (texture == null) {
            if (other.texture != null) {
                return false;
            }
        } else if (!texture.equals(other.texture)) {
            return false;
        }
        if (transformation == null) {
            if (other.transformation != null) {
                return false;
            }
        } else if (!transformation.equals(other.transformation)) {
            return false;
        }
        return true;
    }
}
