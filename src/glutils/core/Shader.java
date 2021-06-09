package glutils.core;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL33;

/** Holds a shader program, is able to compile a shader program out of String shader data */
public class Shader {
	/** ID of the shader program */
	public int id;
	
	/** 
	 * Creates a shader program with shaders compiled from data
	 * @param vertexShaderData code of vertex shader in String form
	 * @param fragmentShaderData code of fragment shader in String form
	 */
	public Shader(String vertexShaderData, String fragmentShaderData) {
		// Shader compilation
		int vertexShader = GL33.glCreateShader(GL33.GL_VERTEX_SHADER);
		GL33.glShaderSource(vertexShader, vertexShaderData);
		GL33.glCompileShader(vertexShader);
		int fragmentShader = GL33.glCreateShader(GL33.GL_FRAGMENT_SHADER);
		GL33.glShaderSource(fragmentShader, fragmentShaderData);
		GL33.glCompileShader(fragmentShader);
		// Shader error testing
		int istatusV = GL33.glGetShaderi(vertexShader, GL33.GL_COMPILE_STATUS);
		int istatusF = GL33.glGetShaderi(fragmentShader, GL33.GL_COMPILE_STATUS);
		if(istatusV == GL33.GL_TRUE) Window.print("Vertex shader compilation success", true, 2);
		else Window.print("Vertex shader compilation error: " + GL33.glGetShaderInfoLog(vertexShader), true, true, true, 0);
		if(istatusF == GL33.GL_TRUE) Window.print("Fragment shader compilation success", true, 2);
		else Window.print("Fragment shader compilation error: " + GL33.glGetShaderInfoLog(fragmentShader), true, true, true, 0);
		// Making shader program
		id = GL33.glCreateProgram();
		GL33.glAttachShader(id, vertexShader);
		GL33.glAttachShader(id, fragmentShader);
		GL33.glLinkProgram(id);
		// Garbage disposal
		GL33.glDeleteShader(vertexShader);
		GL33.glDeleteShader(fragmentShader);
	}
	
	/** Uses current shader program in OpenGL */
	public void use() {
		GL33.glUseProgram(id);
	}
	
	public void setUniformInt(String uniformName, int value) {
		GL33.glUniform1i(GL33.glGetUniformLocation(this.id, uniformName), value);
	}
	
	public void setUniformFloat(String uniformName, float value) {
		GL33.glUniform1f(GL33.glGetUniformLocation(this.id, uniformName), value);
	}
	
	public void setUniformVec2(String uniformName, Vector2f value) {
		GL33.glUniform2fv(GL33.glGetUniformLocation(this.id, uniformName), new float[] {value.x, value.y});
	}
	
	public void setUniformVec3(String uniformName, Vector3f value) {
		GL33.glUniform3fv(GL33.glGetUniformLocation(this.id, uniformName), new float[] {value.x, value.y, value.z});
	}
	
	public void setUniformVec4(String uniformName, Vector4f value) {
		GL33.glUniform4fv(GL33.glGetUniformLocation(this.id, uniformName), new float[] {value.x, value.y, value.z, value.w});
	}
	
	public void setUniformMat4(String uniformName, boolean transpose, Matrix4f value) {
		float[] mat = new float[16];
		value.get(mat);
		GL33.glUniformMatrix4fv(GL33.glGetUniformLocation(this.id, uniformName), transpose, mat);
	}

}
