package de.joglearth.opengl;

import com.jogamp.opengl.util.texture.Texture;

import de.joglearth.geometry.Matrix4;

/**
 * TODO
 *
 */
public final class TransformedTexture {
    
    /**
     * TODO
     */
    public final Texture texture;
    
    /**
     * TODO
     */
    public final Matrix4 transformation;
    
    /**
     * TODO
     * @param texture
     * @param transformation
     */
    public TransformedTexture(Texture texture, Matrix4 transformation) {
        this.texture = texture;
        this.transformation = transformation;
    }

    /* (nicht-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((texture == null) ? 0 : texture.hashCode());
        result = prime * result + ((transformation == null) ? 0 : transformation.hashCode());
        return result;
    }

    /* (nicht-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
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
