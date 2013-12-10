package de.joglearth.source.opengl;

public class VertexBuffer {

    public int vertices,
               indices;


    public VertexBuffer(int vertices, int indices) {
        this.indices = indices;
        this.vertices = vertices;
    }
    
    public VertexBuffer () {
        //yolo
    }
}
