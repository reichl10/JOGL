package de.joglearth.opengl;

import javax.media.opengl.GL2;

import static javax.media.opengl.GL2ES1.*;


/**
 * An exception class thrown if an OpenGL operation fails unexpectedly.
 */
public class GLError extends RuntimeException {

    private static final long serialVersionUID = -6553317277500865406L;

    private static String errorCodeToString(int code) {
        switch (code) {
            case GL_NO_ERROR:
                return "No error occured";
            case GL_INVALID_ENUM:
                return "Invalid enumeration parameter";
            case GL_INVALID_VALUE:
                return "Invalid parameter value";
            case GL_INVALID_OPERATION:
                return "Invalid operation";
            case GL_STACK_OVERFLOW:
                return "Stack overflow";
            case GL_STACK_UNDERFLOW:
                return "Stack underflow";
            case GL_OUT_OF_MEMORY:
                return "Out of memory";
            case GL_INVALID_FRAMEBUFFER_OPERATION:
                return "Invalid framebuffer operation";
            default:
                return "Unknown cause";
        }
    }
    
    /**
     * Constructor. Creates an exception with unknown cause.
     */
    public GLError() {
        this(-1);
    }
    
    /**
     * Constructor. 
     * @param errorCode The error code given by OpenGL
     */
    public GLError(int errorCode) {
        this(errorCodeToString(errorCode));
    }

    /**
     * Constructor. 
     * @param string The error message
     */
    public GLError(String string) {
        super("OpenGL error: " + string);
    }

    /**
     * Throws a GLError if an error is active for a given GL2 object.
     * @param gl The GL2 object
     */
    public static void throwIfActive(GL2 gl) {
        int code = gl.glGetError();
        if (code != GL_NO_ERROR) {
            throw new GLError(code);
        }
    }

}
