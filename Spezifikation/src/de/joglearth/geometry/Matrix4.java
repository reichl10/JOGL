package de.joglearth.geometry;


public class Matrix4 implements Cloneable {
    
	/**
	 * Holds the matrix data in column-first ordering.
	 */
    private float[] m = { 1, 0, 0, 0,
                          0, 1, 0, 0,
                          0, 0, 1, 0,
                          0, 0, 0, 1 };

    /**
     * Constructor. Initializes an identity matrix.
     */
    public Matrix4() {        
    }

    /**
     * Creates a deep copy of the matrix.
     * @return The copied matrix.
     */
    public Matrix4 clone() {
    	Matrix4 c = new Matrix4();
        for (int i=0; i<16; ++i) {
            c.m[i] = m[i];
        }
        return c;
    }

    /**
     * Creates a matrix from a float value array.
     * @param The matrix cells in column-first ordering.
     */
    public Matrix4(float[] init) {
        for (int i=0; i<16; ++i) {
            m[i] = init[i];
        }
    }

    /**
     * Multiplies the matrix by another matrix, given by a float value array.
     * 
     * Mathematical equivalent: this' := this * rhs
     * @param rhs The matrix to multiply by.
     */
    public void mult(float[] rhs) {
        float[] r = new float[16];
        for (int i=0; i<4; ++i)
            for (int j=0; j<4; ++j)
                for (int k=0; k<4; ++k)
                    r[4*i+j] += m[4*i+k] * rhs[4*k+j];
        m = r;
    }
    
    /**
     * Multiplies the matrix by another matrix.
     * 
     * Mathematical equivalent: this' := this * rhs
     * @param rhs The matrix to multiply by.
     */
    public void mult(Matrix4 rhs) {
    	mult(rhs.m);
    }

    /**
     * Adds another matrix, given by a float value array, to itself component-wise.
     * @param rhs The matrix to add.
     */
    public void add(float[] rhs) {
        for (int i=0; i<16; ++i)
            m[i] += rhs[i];
    }

    /**
     * Adds another matrix to itself component-wise.
     * @param rhs The matrix to add.
     */
    public void add(Matrix4 rhs) {
    	add(rhs.m);
    }
    

    public float[] floats() {
        return m;
    }

        
    public void translate(float x, float y, float z) {
        mult(new float[] { 1, 0, 0, 0,
                           0, 1, 0, 0,
                           0, 0, 1, 0,
                           x, y, z, 1 });
    }
    
    public void translate(Vector3 v) {
        translate(v.x, v.y, v.z); 
    }
    

    public void rotateX(float rad) {
        float c = (float) Math.cos(rad), s = (float) Math.sin(rad);
        mult(new float[] { 1,  0,  0,  0,
                           0,  c,  s,  0,
                           0, -s,  c,  0,
                           0,  0,  0,  1 });
    }

    public void rotateY(float rad) {
        float c = (float) Math.cos(rad), s = (float) Math.sin(rad);
        mult(new float[] {  c,  0,  -s,  0,
                            0,  1,  0,  0,
                            s,  0,  c,  0,
                            0,  0,  0,  1 });
    }

    public void rotateZ(float rad) {
        float c = (float) Math.cos(rad), s = (float) Math.sin(rad);
        mult(new float[] {  c,  s,  0,  0,
                           -s,  c,  0,  0,
                            0,  0,  1,  0,
                            0,  0,  0,  1 });
    }

    
    public void scale(float x, float y, float z) {
        mult(new float[] { x, 0, 0, 0,
                           0, y, 0, 0,
                           0, 0, z, 0,
                           0, 0, 0, 1 });
    }
    
    public void scale(Vector3 v) {
        scale(v.x, v.y, v.z);
    }
    
    
    public Matrix4 inverse() {
        Matrix4 i = new Matrix4();
        
        i.m[0]  =  m[5]  * m[10] * m[15] - m[5]  * m[11] * m[14] - 
                   m[9]  * m[6]  * m[15] + m[9]  * m[7]  * m[14] +
                   m[13] * m[6]  * m[11] - m[13] * m[7]  * m[10];     

        i.m[1]  = -m[1]  * m[10] * m[15] + m[1]  * m[11] * m[14] + 
                   m[9]  * m[2]  * m[15] - m[9]  * m[3]  * m[14] - 
                   m[13] * m[2]  * m[11] + m[13] * m[3]  * m[10];  

        i.m[2]  =  m[1]  * m[6]  * m[15] - m[1]  * m[7]  * m[14] - 
                   m[5]  * m[2]  * m[15] + m[5]  * m[3]  * m[14] + 
                   m[13] * m[2]  * m[7]  - m[13] * m[3]  * m[6];     

        i.m[3]  = -m[1]  * m[6]  * m[11] + m[1]  * m[7]  * m[10] + 
                   m[5]  * m[2]  * m[11] - m[5]  * m[3]  * m[10] - 
                   m[9]  * m[2]  * m[7]  + m[9]  * m[3]  * m[6];
                 
        i.m[4]  = -m[4]  * m[10] * m[15] + m[4]  * m[11] * m[14] + 
                   m[8]  * m[6]  * m[15] - m[8]  * m[7]  * m[14] - 
                   m[12] * m[6]  * m[11] + m[12] * m[7]  * m[10];

        i.m[5]  =  m[0]  * m[10] * m[15] - m[0]  * m[11] * m[14] - 
                   m[8]  * m[2]  * m[15] + m[8]  * m[3]  * m[14] + 
                   m[12] * m[2]  * m[11] - m[12] * m[3]  * m[10];

        i.m[6]  = -m[0]  * m[6]  * m[15] + m[0]  * m[7]  * m[14] + 
                   m[4]  * m[2]  * m[15] - m[4]  * m[3]  * m[14] - 
                   m[12] * m[2]  * m[7]  + m[12] * m[3]  * m[6];

        i.m[7]  =  m[0]  * m[6]  * m[11] - m[0]  * m[7]  * m[10] - 
                   m[4]  * m[2]  * m[11] + m[4]  * m[3]  * m[10] + 
                   m[8]  * m[2]  * m[7]  - m[8]  * m[3]  * m[6];

        i.m[8]  =  m[4]  * m[9]  * m[15] - m[4]  * m[11] * m[13] - 
                   m[8]  * m[5]  * m[15] + m[8]  * m[7]  * m[13] + 
                   m[12] * m[5]  * m[11] - m[12] * m[7]  * m[9];

        i.m[9]  = -m[0]  * m[9]  * m[15] + m[0]  * m[11] * m[13] + 
                   m[8]  * m[1]  * m[15] - m[8]  * m[3]  * m[13] - 
                   m[12] * m[1]  * m[11] + m[12] * m[3]  * m[9];

        i.m[10] =  m[0]  * m[5]  * m[15] - m[0]  * m[7]  * m[13] - 
                   m[4]  * m[1]  * m[15] + m[4]  * m[3]  * m[13] + 
                   m[12] * m[1]  * m[7]  - m[12] * m[3]  * m[5];

        i.m[11] = -m[0]  * m[5]  * m[11] + m[0]  * m[7]  * m[9]  + 
                   m[4]  * m[1]  * m[11] - m[4]  * m[3]  * m[9]  - 
                   m[8]  * m[1]  * m[7]  + m[8]  * m[3]  * m[5] ;

        i.m[12] = -m[4]  * m[9]  * m[14] + m[4]  * m[10] * m[13] +
                   m[8]  * m[5]  * m[14] - m[8]  * m[6]  * m[13] - 
                   m[12] * m[5]  * m[10] + m[12] * m[6]  * m[9];

        i.m[13] =  m[0]  * m[9]  * m[14] - m[0]  * m[10] * m[13] - 
                   m[8]  * m[1]  * m[14] + m[8]  * m[2]  * m[13] + 
                   m[12] * m[1]  * m[10] - m[12] * m[2]  * m[9];

        i.m[14] = -m[0]  * m[5]  * m[14] + m[0]  * m[6]  * m[13] + 
                   m[4]  * m[1]  * m[14] - m[4]  * m[2]  * m[13] - 
                   m[12] * m[1]  * m[6]  + m[12] * m[2]  * m[5] ;

        i.m[15] =  m[0]  * m[5]  * m[10] - m[0]  * m[6]  * m[9]  - 
                   m[4]  * m[1]  * m[10] + m[4]  * m[2]  * m[9]  + 
                   m[8]  * m[1]  * m[6]  - m[8]  * m[2]  * m[5] ;   
                  
        float det = m[0] * i.m[0] + m[1] * i.m[4] + m[2] * i.m[8] 
        			+ m[3] * i.m[12];
        
        for (int j = 0; j < 16; ++j) {
            i.m[j] /= det;
        }
        
        return i;
    }
    
    
    public String toString() { 
        String s = "";
        for (int i=0; i<16; i += 4) {
            s += String.format("%.3e %.3e %.3e %.3e\n",
                               m[i], m[i+1], m[i+2], m[i+3]);
        }
        return s;
    }
    
    
    public Vector4 transform(Vector3 v3) {
    	float[] v = { v3.x, v3.y, v3.z, 1 }, w = { 0, 0, 0, 0 };
    	for (int i=0; i<4; ++i) {
    		for (int j=0; j<4; ++j) {
    			w[i] += m[4*j+i] *  v[j];
    		}
    	}
    	return new Vector4(w[0], w[1], w[2], w[3]);
    }

}

