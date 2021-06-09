package glutils.core;

import org.lwjgl.opengl.GL33;

// Stores the id, data array and vertex attrib pointer number of a batch, can make and update batch.
/** Internal class used by the Batch class */
public class Buffer {
	
	public int bufferID; // ID of generated buffer
	private float[] bufferData; // array containing data of buffer
	
	/** The pointer of the vertex attribute of the current batch */
	public int vertexAttribPointer;
	
	/** Internal constructor, not intended for external use, makes and generates buffer */
	public Buffer(float[] bufferData, int vertexAttribPointer, int size) {
		this.bufferData = bufferData;
		this.vertexAttribPointer = vertexAttribPointer;
		bufferID = GL33.glGenBuffers();
		GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, bufferID);
		GL33.glBufferData(GL33.GL_ARRAY_BUFFER, this.bufferData, GL33.GL_STREAM_DRAW);
		GL33.glVertexAttribPointer(this.vertexAttribPointer, size, GL33.GL_FLOAT, false, size * Float.BYTES, 0);
		GL33.glEnableVertexAttribArray(this.vertexAttribPointer);
	}
	
	/** Internal method, updates the data of an already generated buffer */
	public void updateBuffer(float[] bufferData) {
		this.bufferData = bufferData;
		GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, bufferID);
		GL33.glBufferData(GL33.GL_ARRAY_BUFFER, this.bufferData, GL33.GL_STREAM_DRAW);
	}
	
	/** Internal method, disposes of the generated buffer object */
	public void dispose() {
		GL33.glDeleteBuffers(bufferID);
	}
}
