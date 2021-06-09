package glutils.advanced;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import glutils.core.Shader;
import glutils.core.Texture;
import glutils.core.VertexArray;
import glutils.utils.Mesh;

public class Model {
	
	// VA + Mesh
	public VertexArray vertexArray;
	public Mesh mesh;
	// Texture + Material
	public Texture texture;
	public Material material;
	public boolean useMaterial;
	// Transform
	public Matrix4f transform;
	
	// Does things same for material and texture constructors
	private Model(Mesh mesh) {
		vertexArray = new VertexArray();
		this.mesh = mesh;
		vertexArray.addBuffer("verts", mesh.getVerts(), 0, 3);
		vertexArray.addBuffer("texCoords", mesh.getTexCoords(), 1, 2);
		vertexArray.addBuffer("normals", mesh.getNormals(), 2, 3);
		
		transform = new Matrix4f();	
	}
	/** Creates new Model that uses a texture instead of material */
	public Model(Mesh mesh, Texture texture) {
		this(mesh);
		this.texture = texture;
		useMaterial = false;
	}
	/** Creates new Model that uses a material instead of texture */
	public Model(Mesh mesh, Material material) {
		this(mesh);
		this.material = material;
		useMaterial = true;
	}
	
	/** Renders the model, textureName is used only if the model uses a texture instead of a material */
	public void render(Shader shader, String textureName) {
		if(useMaterial) material.use(shader); else texture.use(textureName, 0, shader);
		shader.setUniformMat4("transform", false, transform);
		vertexArray.render();	
	}
	/** Renders the model, if the model uses a texture instead of a material, the texture uniform name is set to the default "tex" */
	public void render(Shader shader) {
		render(shader, "tex");
	}
	
	/** Translates the model by the given vector, using the model's local coordinate system */
	public void translateByLocal(Vector3f translation) {
		transform.translate(translation);
	}
	/** Translates the model by the given vector, using the global coordinate system */
	public void translateByGlobal(Vector3f translation) {
		Vector3f c = new Vector3f();
		transform.getTranslation(c);
		transform.setTranslation(new Vector3f(c).add(translation));
	}
	/** Translates the model to the given position on the global coordinate system */
	public void translateTo(Vector3f translation) {
		transform.setTranslation(translation);
	}
	
	public Vector3f getTranslation() {
		Vector3f pos = new Vector3f();
		transform.getTranslation(pos);
		return pos;
	}
	
	public Mesh getMeshData() {
		return mesh;
	}

}
