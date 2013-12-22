package de.joglearth.rendering;


public interface GLContextListener {
    void beginFrame(GLContext context);
    void endFrame(GLContext context);
    void initialize(GLContext context);
    void reshape(GLContext context, int width, int height);
    void display(GLContext context);
    void dispose(GLContext context);
}
