package glutils.core;

import java.nio.ByteBuffer;

import org.joml.Vector2f;
import org.lwjgl.opengl.GL33;
import org.lwjgl.stb.STBImage;

/** Class storing an OpenGL texture */
public class Texture {
	
	/** The OpenGL id of the current texture */
	public int textureID;
	
	private ByteBuffer textureData; // Texture data converted through STBImage
	
	private int[] width;
	private int[] height;
	private int[] colorChannels;
	
	// Constants borrowed from OpenGL for ease of use
	public static final int TEXTURE_REPEAT = GL33.GL_REPEAT;
	public static final int TEXTURE_MIRRORED_REPEAT = GL33.GL_MIRRORED_REPEAT;
	public static final int TEXTURE_CLAMP_TO_EDGE = GL33.GL_CLAMP_TO_EDGE;
	public static final int FILTER_NEAREST = GL33.GL_NEAREST;
	public static final int FILTER_LINEAR = GL33.GL_LINEAR;
	public static final int FILTER_MIPMAP_LINEAR = GL33.GL_LINEAR_MIPMAP_LINEAR;
	public static final int FILTER_MIPMAP_NEAREST = GL33.GL_NEAREST_MIPMAP_NEAREST;
	
	/** Creates new texture, S and T wrap is manually defined */
	public Texture(ByteBuffer textureData, int wrapS, int wrapT, int minFilter, int magFilter, boolean genMipmap) {
		// Variable initialization + declaration
		width = new int[1];
		height = new int[1];
		colorChannels = new int[1];
		// GL texture generation
		textureID = GL33.glGenTextures();
		GL33.glBindTexture(GL33.GL_TEXTURE_2D, textureID);
		// Texture parameter setting
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_S, wrapS);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_T, wrapT);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MIN_FILTER, minFilter);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MAG_FILTER, magFilter);
		// Texture data loading
		this.textureData = STBImage.stbi_load_from_memory(textureData, width, height, colorChannels, STBImage.STBI_rgb_alpha);
		if(this.textureData != null) {
			GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGBA, width[0], height[0], 0, GL33.GL_RGBA, GL33.GL_UNSIGNED_BYTE, this.textureData);
			if(genMipmap) {
				GL33.glGenerateMipmap(GL33.GL_TEXTURE_2D);
			}
			Window.print("Texture: texture loaded", true, 2);
			textureData.clear();
			textureData = null;
			STBImage.stbi_image_free(this.textureData);
			this.textureData.clear();
			this.textureData = null;
		} else {
			Window.print("Texture: texture loading error\n  " + STBImage.stbi_failure_reason(), true, true, true, 0);
		}
	}
	/** Creates new texture, S and T wrap is set to repeat */
	public Texture(ByteBuffer textureData, int minFilter, int magFilter, boolean genMipmap) {
		this(textureData, TEXTURE_REPEAT, TEXTURE_REPEAT, minFilter, magFilter, genMipmap);
	}
	/** Creates new texture, S and T wrap is set to repeat, genMipmap is set to true */
	public Texture(ByteBuffer textureData, int minFilter, int magFilter) {
		this(textureData, TEXTURE_REPEAT, TEXTURE_REPEAT, minFilter, magFilter, true);
	}
	/** Creates a new texture, S and T wrap is set to repeat, min filter is set to linear mipmap linear, mag filter is set to linear, genMipmap is set to true */
	public Texture(ByteBuffer textureData) {
		this(textureData, TEXTURE_REPEAT, TEXTURE_REPEAT, FILTER_MIPMAP_LINEAR, FILTER_LINEAR, true);
	}
	
	/** Uses the texture in the texture slot textureSlot and sets the texture sampler2D uniform name to uniformName */
	public void use(String uniformName, int textureSlot, Shader shaderProgram) {
		// Uniform changing
		//GL33.glUniform1i(GL33.glGetUniformLocation(shaderProgram.id, uniformName), textureSlot);
		shaderProgram.setUniformInt("uniformName", textureSlot);
		// Binding texture to slot
		GL33.glActiveTexture(GL33.GL_TEXTURE0 + textureSlot);
		GL33.glBindTexture(GL33.GL_TEXTURE_2D, textureID);
	}
	
	/** Returns the size of the texture */
	public Vector2f getSize() {
		return new Vector2f(width[0], height[0]);
	}
	
	/** Returns the color channels of the texture in int form, values of the GL color channel constants */
	public int getColorChannels() {
		return colorChannels[0];
	}
	
	/** Disposes of the generated texture object */
	public void dispose() {
		GL33.glDeleteTextures(textureID);
		System.gc();
	}
}
