package glutils.utils;

/** Class that holds vertices, tex coords */
public class Mesh {
	
	/** Vertices array */
	public float[] verts;
	/** Tex coords array */
	public float[] texCoords;
	/** Vertex normals array */
	public float[] normals;
	
	/** Constructor, creates class with verts, texCoords and normals */
	public Mesh(float[] verts, float[] texCoords, float[] normals) {
		this.verts = verts;
		this.texCoords = texCoords;
		this.normals = normals;
	}
	
	/** Sets the vertex array of the model */
	public void setVerts(float[] verts) {
		this.verts = verts;
	}
	/** Sets the tex coords array of the model */
	public void setTexCoords(float[] texCoords) {
		this.texCoords = texCoords;
	}
	/** Sets the vertex normals array of the model */
	public void setNormals(float[] normals) {
		this.normals = normals;
	}
	
	/** Returns the vertex array of the model */
	public float[] getVerts() {
		return verts;
	}
	/** Returns the tex coords array of the model */
	public float[] getTexCoords() {
		return texCoords;
	}
	/** Returns the normals array of the model */
	public float[] getNormals() {
		return normals;
	}

}
