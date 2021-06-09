package glutils.core;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

/** Class holding a view and projection matrix, which can be translated and rotated using methods */
public class Camera {
	
	/** The camera view matrix */
	public Matrix4f view;
	/** The camera projection matrix */
	public Matrix4f proj;
	
	private Vector3f camPos; // The position vector of the camera
	private float centerOffset; // The offset of the camera from the center the camera rotates around, 0 for first person, higher number for orbit camera
	
	private float camRotX; // The x axis rotation of the camera view matrix
	private float camRotY; // The y axis rotation of the camera view matrix
	
	/** Constant for setting the 0,0 pos of the orthographic camera */
	public static final int CENTER = 0;
	/** Constant for setting the 0,0 pos of the orthographic camera */
	public static final int LEFT = 1;
	/** Constant for setting the 0,0 pos of the orthographic camera */
	public static final int RIGHT = 2;
	/** Constant for setting the 0,0 pos of the orthographic camera */
	public static final int BOTTOM = 3;
	/** Constant for setting the 0,0 pos of the orthographic camera */
	public static final int TOP = 4;
	
	/** Private constructor to be used by the static initialization methods */
	private Camera(Vector3f startingPos, float centerOffset, float startingRotX, float startingRotY) {
		// Declaration, init
		camPos = startingPos;
		camRotX = 0.0f + startingRotX;
		camRotY= 0.0f + startingRotY;
		this.centerOffset = centerOffset;
		
		view = new Matrix4f();
		
		// Matrix setting
		updateView();
		proj = new Matrix4f();
	}
	
	/** 
	 * Creates a camera with a perspective projection matrix
	 * @param startingPos starting position of the camera
	 * @param centerOffset the distance of the camera from the center of rotation, 0 for first person, higher number for third person
	 * @param startingRotX starting rotation of the camera along the x axis
	 * @param startingRotY starting rotation of the camera along the y axis
	 * @param fov field of view of the perspective camera
	 * @param aspect aspect ratio of the camera
	 * @param near near view distance
	 * @param far far view distance
	 */
	public static Camera PerspectiveCamera(Vector3f startingPos, float centerOffset, float startingRotX, float startingRotY, float fov, Vector2f aspect, float near, float far) {
		Camera c = new Camera(startingPos, centerOffset, startingRotX, startingRotY);
		c.proj.perspective((float)Math.toRadians(fov), (aspect.x / aspect.y), near, far);
		return c;
	}
	
	/** 
	 * Creates a camera with an orthographic projection matrix
	 * @param startingPos starting position of the camera
	 * @param centerOffset the distance of the camera from the center of rotation, 0 for first person, higher number for third person
	 * @param startingRotX starting rotation of the camera along the x axis
	 * @param startingRotY starting rotation of the camera along the y axis
	 * @param size width and height of the camera
	 * @param startCoordsX where the 0 coordinate on the x axis should be, set to Camera class constants LEFT, RIGHT or CENTER
	 * @param startCoordsY where the 0 coordinate on the y axis should be, set to Camera class constants TOP, BOTTOM or CENTER
	 * @param near near view distance
	 * @param far far view distance
	 */ 
	//public static Camera OrthographicCamera(Vector3f startingPos, float centerOffset, float startingRotX, float startingRotY, Vector2f aspect, int startCoords, float scale, float near, float far) {
	public static Camera OrthographicCamera(Vector3f startingPos, float centerOffset, float startingRotX, float startingRotY, Vector2f size, int startCoordsX, int startCoordsY, float near, float far) {
		Camera c = new Camera(startingPos, centerOffset, startingRotX, startingRotY);
		// Setting the left,right,bottom,top of the ortho cam according to the size and center pos
		float left = 0, right = 0, bottom = 0, top = 0;
		if(startCoordsX == LEFT) {
			right = size.x;
		} else if(startCoordsX == RIGHT) {
			left = -size.x;
		} else {
			left = -(size.x / 2f);
			right = size.x / 2f;
		}
		if(startCoordsY == TOP) {
			bottom = -size.y;
		} else if(startCoordsY == BOTTOM) {
			top = size.y;
		} else {
			top = size.y / 2f;
			bottom = -(size.y / 2f);
		}
		c.proj.ortho(left, right, bottom, top, near, far);
		return c;
	}
	
	/** 
	 * Updates the projection matrix to perspective projection with the arguments
	 * @param fov field of view of the perspective camera
	 * @param aspect aspect ratio of the camera
	 * @param near near view distance
	 * @param far far view distance
	 */
	public void updatePerspective(float fov, Vector2f aspect, float near, float far) {
		proj.identity();
		proj.perspective((float)Math.toRadians(fov), (aspect.x / aspect.y), near, far);
	}
	
	/** 
	 * Updates the projection matrix to orthographic projection with the arguments
	 * @param aspect aspect ratio of the camera
	 * @param startCoords whether to set camera center to (0, 0) or camera bottom left to (0, 0), set by class constants
	 * @param scale camera orthographic scale - zoom
	 * @param near near view distance
	 * @param far far view distance
	 */
	public void updateOtrho(Vector2f size, int startCoordsX, int startCoordsY, float near, float far) {
		float left = 0, right = 0, bottom = 0, top = 0;
		if(startCoordsX == LEFT) {
			right = size.x;
		} else if(startCoordsX == RIGHT) {
			left = -size.x;
		} else {
			left = -(size.x / 2f);
			right = size.x / 2f;
		}
		if(startCoordsY == TOP) {
			bottom = -size.y;
		} else if(startCoordsY == BOTTOM) {
			top = size.y;
		} else {
			top = size.y / 2f;
			bottom = -(size.y / 2f);
		}
		proj.identity();
		proj.ortho(left, right, bottom, top, near, far);
	}
	
	/** 
	 * Applies transformation from transform methods to the camera and puts the view and projection matrix and camera position vector into shader uniforms
	 * @param viewMatrixName name of the view matrix uniform in the shader
	 * @param projectionMatrixName name of the projection matrix uniform in the shader
	 * @param cameraPositionName name of the camera position vector uniform in the shader
	 * @param shaderProgram shader program to apply uniforms to
	 */
	public void use(String viewMatrixName, String projectionMatrixName, String cameraPositionName, Shader shaderProgram) {
		
		// Updating the view matrix
		updateView();
		
		// Putting the cam matrices to shader uniforms
		shaderProgram.setUniformMat4(viewMatrixName, false, view);
		shaderProgram.setUniformMat4(projectionMatrixName, false, proj);
		shaderProgram.setUniformVec3(cameraPositionName, camPos);
	}
	
	/** Calls use() with the String values set to default "view", "proj" and "camPos" */
	public void use(Shader shaderProgram) {
		use("view", "proj", "camPos", shaderProgram);
	}
	
	/** Updates the view matrix of the camera, not necessary to call manually (gets called by use method) */
	public void updateView() {
		// Resetting the matrix
		view.identity();
		// Steps: 1) rotate, 2) translate, 3) offset, order of rotate/translate important, what axis to rotate first also
		// Rotate
		view.rotateX((float)Math.toRadians(-camRotX));
		view.rotateY((float)Math.toRadians(-camRotY));
		// Translate
		view.translate(new Vector3f(camPos).negate());
		// Offset
		if(centerOffset != 0) {
			Vector3f dir = new Vector3f(0, 0, -1);
			dir.rotateX((float)Math.toRadians(camRotX));
			dir.rotateY((float)Math.toRadians(camRotY));
			dir.mul(-centerOffset);
			view.translate(dir.negate());
		}
	}
	
	/** Translates the camera along the global coordinate system */
	public void translateByGlobal(Vector3f translation) {
		camPos.add(translation);
	}
	/** Translates the camera along the rotation relative axis */
	public void translateByLocal(Vector3f translation) {
		translation.rotateX((float)Math.toRadians(camRotX));
		translation.rotateY((float)Math.toRadians(camRotY));
		camPos.add(translation);
	}
	/** Translates the camera to the global coordinates */
	public void translateToGlobal(Vector3f translation) {
		camPos.set(translation);
	}
	
	/** Adds to the x and y axis camera rotation */
	public void rotate(float camRotX, float camRotY) {
		this.camRotX += camRotX;
		this.camRotY += camRotY;
	}
	/** Sets the x and y axis camera rotation */
	public void setRotation(float camRotX, float camRotY) {
		this.camRotX = camRotX;
		this.camRotY = camRotY;
	}
	
	/** Returns the current position vector of the camera */
	public Vector3f getCamPos() {
		return new Vector3f(camPos);
	}
	/** Returns the current x axis rotation of the camera */
	public float getRotX() {
		return camRotX;
	}
	/** Returns the current y axis rotation of the camera */
	public float getRotY() {
		return camRotY;
	}
	
	/** Returns the offset from the center of rotation of the camera */
	public float getCenterOffset() {
		return centerOffset;
	}
	/** Sets the offset from the center of rotation of the camera, set to 0 for first person camera, set to higher number for third person camera */
	public void setCenterOffset(float newCenterOffset) {
		centerOffset = newCenterOffset;
	}
	/** Adds to the offset from the center of rotation of the camera */
	public void addCenterOffset(float centerOffset) {
		this.centerOffset = centerOffset;
	}
	
	// TODO FOR LATER: MAKE A DIFFERENT COORD SYSTEM THAN XYZ EULER FOR Z AXIS ROTATION, LIKE QUARTER ONIONS (QUATERNIONS BUT THATS BORING LOL).

}
