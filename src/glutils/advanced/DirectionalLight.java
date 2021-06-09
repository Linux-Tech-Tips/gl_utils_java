package glutils.advanced;

import org.joml.Vector3f;
import org.joml.Vector4f;

import glutils.core.Shader;

/** Class containing a directional light, can be used by itself, utilized by WorldLight */
public class DirectionalLight {
	
	public Vector3f direction;
	public Vector4f color;
	public float intensity;
	
	public DirectionalLight(Vector3f direction, Vector4f color, float intensity) {
		this.direction = direction;
		this.color = color;
		this.intensity = intensity;
	}
	
	/** Applies the directional light to the given shader as a struct with the given component names, into a uniform with the given name */
	public void use(Shader shader, String directionalLightUniformName, String directionComponentName, String colorComponentName, String intensityComponentName) {
		shader.setUniformVec3(directionalLightUniformName + "." + directionComponentName, direction);
		shader.setUniformVec4(directionalLightUniformName + "." + colorComponentName, color);
		shader.setUniformFloat(directionalLightUniformName + "." + intensityComponentName, intensity);
	}
	/** Applies the directional light to the given shader as a struct with the default component names "direction" for the direction component, "color" for the color component and "intensity" for the intensity component, into a uniform with the default name "directionalLight" */
	public void use(Shader shader) {
		use(shader, "directionalLight", "direction", "color", "intensity");
	}

}
