package glutils.core;

import java.util.HashMap;

import org.lwjgl.opengl.GL33;

/** Class holding a VAO with multiple VBOs, has its own render call */
public class VertexArray {
	
	/** The OpenGL id of the current VAO */
	public int batchID;
	
	///** Total amount of vertices to render */
	//public int vertexArraySize;
	
	private HashMap<String, Buffer> buffers; // HashMap of buffers in VAO
	private int vertexBufferNumber; // Vertex pointer number of the buffer serving as the vertex buffer, used for amount of vertices to render
	private int vertexArraySize; // Size of array with vertices to render
	
	/** 
	 * Class initialization, actual data added through other methods
	 * @param vertexArraySize total amount of vertices to render, temporary variable, later changed dynamically through buffer
	 */
	public VertexArray() {
		batchID = GL33.glGenVertexArrays();
		buffers = new HashMap<>();
		vertexBufferNumber = 0;
	}
	
	/** 
	 * Adds a vertex buffer object into the current VAO
	 * @param name name of the buffer, used for updating buffer
	 * @param data float array vertex data
	 * @param vertexAttribPointer number of pointer in the vertex attribute array, used to access data in shader
	 * @param size how many values are in one complete part of the data array (for example each 3 values in the data array define a vertex - size 3)
	 */
	public void addBuffer(String name, float[] data, int vertexAttribPointer, int size) {
		GL33.glBindVertexArray(batchID);
		buffers.put(name, new Buffer(data, vertexAttribPointer, size));
		if(vertexAttribPointer == vertexBufferNumber) {
			vertexArraySize = data.length;
		}
	}
	
	/** 
	 * Updates an already existing vertex buffer object in the current VAO
	 * @param name name of the buffer entered when adding a new buffer
	 * @param data new data to insert into the buffer
	 */
	public void updateBuffer(String name, float[] data) {
		GL33.glBindVertexArray(batchID);
		Buffer b = buffers.get(name);
		b.updateBuffer(data);
		if(b.vertexAttribPointer == vertexBufferNumber) {
			vertexArraySize = data.length;
		}
	}
	
	/** 
	 * Sets the number of the current vertex buffer which decides where to take the number of vertices to render from 
	 * @param n number id of the vertex buffer
	 */
	public void setVertexBufferNumber(int n) {
		vertexBufferNumber = n;
	}
	
	/** Renders the triangles of the current VAO, shader has to already be used before */
	public void render() {
		GL33.glBindVertexArray(batchID);
		GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, (vertexArraySize / 3)); // divided by three makes it render the correct amount of vertices
	}
	
	/** Disposes of the generated Vertex array object */
	public void dispose() {
		GL33.glDeleteVertexArrays(batchID);
		for(String s : buffers.keySet()) {
			Buffer b = buffers.get(s);
			b.dispose();
		}
	}
}
