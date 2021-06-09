package glutils.advanced;

import java.util.ArrayList;
import java.util.Comparator;

import org.joml.Vector3f;
import org.joml.Vector4f;

import glutils.core.Shader;

/** Class containing a world light system with a directional light, ambient light and an array of point lights */
public class WorldLight {
	
	public DirectionalLight directionalLight;
	public ArrayList<PointLight> pointLights;
	public Vector4f ambientLight;
	
	public WorldLight(DirectionalLight dirLight, Vector4f ambientLightColor) {
		
		directionalLight = dirLight;
		pointLights = new ArrayList<>();
		ambientLight = ambientLightColor;
		
	}
	
	public void addPointLight(PointLight pointLight) {
		pointLights.add(pointLight);
	}
	public void removePointLight(String name) {
		for(PointLight p : pointLights) {
			if(p.name.equals(name)) {
				pointLights.remove(p);
			}
		}
	}
	
	/** Applies the world light to the given shader with the set parameters of all the light types. If the amount of point lights is bigger than pointLightsToUse, only the specified amount will be applied to the shader, this amount should also be the pointLight[] size */
	public void use(Shader shader, int pointLightsToUse, String pointLightsUsedName,
			String directionalLightUniformName, String directionalLightDirectionComponentName, String directionalLightColorComponentName, String directionalLightIntensityComponentName, 
			String pointLightUniformName, String pointLightPositionComponentName, String pointLightColorComponentName, String pointLightIntensityComponentName, String pointLightFalloffLinearComponentName, String pointLightFalloffQuadraticComponentName,
			String ambientLightName, Vector3f playerPos) {
		
		directionalLight.use(shader, directionalLightUniformName, directionalLightDirectionComponentName, directionalLightColorComponentName, directionalLightIntensityComponentName);
		shader.setUniformVec4(ambientLightName, ambientLight);
		
		
		pointLights.sort(new Comparator<PointLight>() {
			// + if if first bigger
			public int compare(PointLight p0, PointLight p1) {
				return (p0.pos.distance(playerPos) > p1.pos.distance(playerPos) ? 1 : -1);
			}
		});
		for(int i = 0; i < Math.min(pointLightsToUse, pointLights.size()); i++) {
			PointLight p = pointLights.get(i);
			p.use(shader, pointLightUniformName + "[" + i + "]", pointLightPositionComponentName, pointLightColorComponentName, pointLightIntensityComponentName, pointLightFalloffLinearComponentName, pointLightFalloffQuadraticComponentName);
		}
		shader.setUniformInt(pointLightsUsedName, Math.min(pointLightsToUse, pointLights.size()));
		
	}
	/** Applies the world light to the given shader with the default parameters of all the light types (specified in the use() methods of the light types). If the amount of point lights is bigger than pointLightsToUse, only the specified amount will be applied to the shader, this amount should also be the pointLight[] size */
	public void use(Shader shader, int pointLightsToUse, Vector3f playerPos) {
		use(shader, pointLightsToUse, "pointLightsUsed", "directionalLight", "direction", "color", "intensity", "pointLight", "position", "color", "intensity", "falloffLinear", "falloffQuadratic", "ambientLight", playerPos);
	}
	/** Applies the world light to the given shader with the default parameters of all the light types (specified in the use() methods of the light types), pointLightsToUse is set to 12. If the amount of point lights is bigger than pointLightsToUse, only the specified amount will be applied to the shader, this amount should also be the pointLight[] size */
	public void use(Shader shader, Vector3f playerPos) {
		use(shader, 12, playerPos);
	}

}
