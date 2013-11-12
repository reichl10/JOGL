package de.joglearth.geometry;

public class Vector4 {
	
	public float x, y, z, w; 
    
    public Vector4() {
    	this(0, 0, 0, 0);
    }
    
    public Vector4(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    public Vector3 divide() {
    	return new Vector3(x/w, y/w, z/w);
    }
}
