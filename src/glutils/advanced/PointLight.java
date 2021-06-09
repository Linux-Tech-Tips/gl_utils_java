package glutils.advanced;

import org.joml.Vector3f;
import org.joml.Vector4f;

import glutils.core.Shader;

/** Class containing a point light, can be used by itself, utilized by WorldLight */
public class PointLight {
	
	public Vector3f pos;
	public Vector4f color;
	public float intensity;
	
	/** The linear falloff value, used to set the point light's falloff in the shader */
	public float falloffLinear;
	/** The quadratic falloff value, used to set the point light's falloff in the shader */
	public float falloffQuadratic;
	
	public String name;
	
	public PointLight(Vector3f pos, Vector4f color, float intensity, float falloffLinear, float falloffQuadratic, String name) {
		this.pos = pos;
		this.color = color;
		this.intensity = intensity;
		
		this.falloffLinear = falloffLinear;
		this.falloffQuadratic = falloffQuadratic;
		
		this.name = name;
	}
	
	/** Applies the point light to the given shader as a struct with the given component names, into a uniform with the given name */	
	public void use(Shader shader, String pointLightUniformName, String positionComponentName, String colorComponentName, String intensityComponentName, String falloffLinearComponentName, String falloffQuadraticComponentName) {
		shader.setUniformVec3(pointLightUniformName + "." + positionComponentName, pos);
		shader.setUniformVec4(pointLightUniformName + "." + colorComponentName, color);
		shader.setUniformFloat(pointLightUniformName + "." + intensityComponentName, intensity);
		shader.setUniformFloat(pointLightUniformName + "." + falloffLinearComponentName, falloffLinear);
		shader.setUniformFloat(pointLightUniformName + "." + falloffQuadraticComponentName, falloffQuadratic);
	}
	/** Applies the point light to the given shader as a struct with the default component names "position" for the position component, "color" for the color component name, "intensity" for the intensity component name, "falloffLinear" for the linear falloff component name and "falloffQuadratic" for the quadratic falloff component name, into a uniform with the default name "pointLight" */
	public void use(Shader shader) {
		use(shader, "pointLight", "position", "color", "intensity", "falloffLinear", "falloffQuadratic");
	}

}
