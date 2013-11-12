package de.joglearth.geometry;


public class Vector3 implements Cloneable {
    public float x, y, z; 
    
    public Vector3() {
    	this(0, 0, 0);
    }
    
    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vector3 clone() {
    	return new Vector3(x, y, z);
    }
    
    public Vector3 to(Vector3 other) {
    	return new Vector3(other.x - x, other.y - y, other.z - z);
    }
    
    public Vector3 times(float c) {
    	return new Vector3(c*x, c*y, c*z);
    }
    
    public Vector3 plus(Vector3 v) {
    	return new Vector3(x + v.x, y + v.y, z + v.z);
    }
    
    public Vector3 minus(Vector3 v) {
    	return new Vector3(x - v.x, y - v.y, z - v.z);
    }
    
    public float length() {
    	return (float) Math.sqrt(x*x + y*y + z*z);
    }
    
    public void add(Vector3 v) {
    	x += v.x;
    	y += v.y;
    	z += v.z;
    }
    
    public Vector3 normalized() {
    	float l = length();
    	return new Vector3(x/l, y/l, z/l);
    }
    
}

