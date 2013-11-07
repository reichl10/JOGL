package de.joglearth.view;

import de.joglearth.model.*;
import de.joglearth.view.*;
import de.joglearth.controller.*;


public class Mesh {
	private float[] vertices, normals, texCoords;
	private int[] indices;
	private int vertexCount, normalCount, texCoordCount, indexCount;
	
	public Mesh(float[] vertices, int vertexCount, float[] normals,
			    int normalCount, float[] texCoords, int texCoordCount,
			    int[] indices, int indexCount) {
		this.vertices = vertices;
		this.vertexCount = vertexCount;
		this.normals = normals;
		this.normalCount = normalCount;
		this.texCoords = texCoords;
		this.texCoordCount = texCoordCount;
		this.indices = indices;
		this.indexCount = indexCount;
	}

	public float[] getVertices() {
		return vertices;
	}

	public float[] getNormals() {
		return normals;
	}

	public float[] getTexCoords() {
		return texCoords;
	}

	public int[] getIndices() {
		return indices;
	}

	public int getVertexCount() {
		return vertexCount;
	}

	public int getNormalCount() {
		return normalCount;
	}

	public int getTexCoordCount() {
		return texCoordCount;
	}

	public int getIndexCount() {
		return indexCount;
	}
	
}
