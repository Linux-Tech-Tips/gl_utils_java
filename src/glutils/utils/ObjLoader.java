package glutils.utils;

import java.util.ArrayList;

import org.joml.Vector2f;

import glutils.core.Window;

/** Class with a static method that loads vertices and tex coords from an obj file */
public class ObjLoader {
	
	/** Static method that loads a model from an internal file path with any project class as relative path origin */
	public static Mesh loadObjInternal(Class<?> c, String filePath) {
		return loadObjFromMemory(FileIO.loadStringInternal(c, filePath));
	}
	/** Static method that loads a model from an external file next to the application */
	public static Mesh loadObjExternal(String filePath) {
		return loadObjFromMemory(FileIO.loadStringExternal(filePath));
	}
	/** Static method that loads model data from string obj file data */
	public static Mesh loadObjFromMemory(String fileData) {
		
		// Processing input data
		String[] lines = fileData.split("\n");
		
		// Getting verts, texCoords, normals, indices from input data
		ArrayList<float[]> vertsAL = new ArrayList<>();
		ArrayList<float[]> texCoordsAL = new ArrayList<>();
		ArrayList<float[]> normalsAL = new ArrayList<>();
		ArrayList<String> indicesAL = new ArrayList<>();
		
		for(int i = 0; i < lines.length; i++) {
			// Splitting current line to process
			String[] line = lines[i].trim().split(" ");
			if(line[0].equals("v")) { 
				// Getting verts
				vertsAL.add(new float[] {Float.parseFloat(line[1]), Float.parseFloat(line[2]), Float.parseFloat(line[3])});
			} else if(line[0].equals("vt")) {
				// Getting tex coords
				texCoordsAL.add(new float[] {Float.parseFloat(line[1]), Float.parseFloat(line[2])});
			} else if(line[0].equals("vn")) {
				// Getting normals
				normalsAL.add(new float[] {Float.parseFloat(line[1]), Float.parseFloat(line[2]), Float.parseFloat(line[3])});
			} else if(line[0].equals("f")) {
				// Getting indices
				indicesAL.add(line[1]);
				indicesAL.add(line[2]);
				indicesAL.add(line[3]);
			}
		}
		
		// Making verts, texCoords and normals out of indices
		float[] verts = new float[indicesAL.size() * 3];
		float[] texCoords = new float[indicesAL.size() * 2];
		float[] normals = new float[indicesAL.size() * 3];
		int vI = 0;
		int tI = 0;
		int nI = 0;
		
		for(int i = 0; i < indicesAL.size(); i++) {
			String[] currentF = indicesAL.get(i).split("/");
			// Getting verts
			verts[vI] = vertsAL.get(Integer.parseInt(currentF[0]) - 1)[0];
			verts[vI+1] = vertsAL.get(Integer.parseInt(currentF[0]) - 1)[1];
			verts[vI+2] = vertsAL.get(Integer.parseInt(currentF[0]) - 1)[2];
			vI += 3;
			// Getting tex coords
			texCoords[tI] = texCoordsAL.get(Integer.parseInt(currentF[1]) - 1)[0];
			texCoords[tI+1] = 1 - texCoordsAL.get(Integer.parseInt(currentF[1]) - 1)[1]; // V tex coords have to be inverted
			tI += 2;
			// Getting normals
			normals[nI] = normalsAL.get(Integer.parseInt(currentF[2]) - 1)[0];
			normals[nI+1] = normalsAL.get(Integer.parseInt(currentF[2]) - 1)[1];
			normals[nI+2] = normalsAL.get(Integer.parseInt(currentF[2]) - 1)[2];
			nI += 3;			
		}
		
		Window.print("Model loader: model loaded", true, 2);
		return new Mesh(verts, texCoords, normals);
	}
	
	/** Convenience method that generates rectangle vertices */
	public static float[] genRectangleVerts(Vector2f pos, Vector2f size, float z) {
		return new float[] {
				-1 * (size.x / 2) + pos.x, 1 * (size.y / 2) + pos.y, z, 
				1 * (size.x / 2) + pos.x, 1 * (size.y / 2) + pos.y, z,
				-1 * (size.x / 2) + pos.x, -1 * (size.y / 2) + pos.y, z,
				1 * (size.x / 2) + pos.x, 1 * (size.y / 2) + pos.y, z,
				-1 * (size.x / 2) + pos.x, -1 * (size.y / 2) + pos.y, z,
				1 * (size.x / 2) + pos.x, -1 * (size.y / 2) + pos.y, z,
		};
	}
	/** Convenience method that generates rectangle tex coords */
	public static float[] genRectangleTexCoords(Vector2f offset, Vector2f size) {
		return new float[] {
				0 + offset.x, 0 + offset.y,
				1 * (size.x) + offset.x, 0 + offset.y,
				0 + offset.x, 1 * (size.y) + offset.y,
				1 * (size.x) + offset.x, 0 + offset.y,
				0 + offset.x, 1 * (size.y) + offset.y,
				1 * (size.x) + offset.x, 1 * (size.y) + offset.y,
		};
	}

}
