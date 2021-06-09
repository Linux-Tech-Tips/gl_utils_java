package glutils.advanced;

import glutils.core.Shader;
import glutils.core.Texture;

/** Holds more properties than just texture, meant to be used with lights */
public class Material {
	
	/** Diffuse texture of the material, defines default surface color */
	public Texture diffuse;
	/** Specular (reflective) map of the material, defines where the material is and isn't shiny */
	public Texture specular;
	/** Specular intensity, defines how shiny the shiny parts of the material are */
	public int specularIntensity;
	
	/** Creates a new material using the passed textures as the diffuse and specular components. Specular intensity dictates the shininess of the material, default is 32 */
	public Material(Texture diffuseTexture, Texture specularTexture, int specularIntensity) {
		diffuse = diffuseTexture;
		specular = specularTexture;
		this.specularIntensity = specularIntensity;
	}
	
	/** Applies the material to the given shader as a struct with the given component names, into a uniform variable with the given name */
	public void use(Shader shader, String materialUniformName, String diffuseComponentName, String specularComponentName, String intensityComponentName) {
		diffuse.use(materialUniformName + "." + diffuseComponentName, 0, shader);
		specular.use(materialUniformName + "." + specularComponentName, 1, shader);
		shader.setUniformInt(materialUniformName + "." + intensityComponentName, specularIntensity);
	}
	/** Applies the material to the given shader as a struct with the default component names "diffuse" for the diffuse component, "specular" for the specular component and "shininess" for the intensity component, into a uniform variable with the default name "material" */
	public void use(Shader shader) {
		use(shader, "material", "diffuse", "specular", "shininess");
	}

}
