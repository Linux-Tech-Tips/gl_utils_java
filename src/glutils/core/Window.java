package glutils.core;

import java.util.HashMap;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharCallbackI;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL33;

/**
 * @author NoNameDev
 */

/**  Main part of engine. Initializes OpenGL and GLFW, creates a GLFW window and sets it's context to OpenGL. */
public class Window {
	
	// Pointer variable
	/** Stores the pointer to the GLFW window, used in all GLFW methods. */
	public long window;
	
	/** Stores the time between last frame and current frame in seconds */
	private float deltaTime;
	
	// General private variables
		// Variables taking care of key/mouse input
	private HashMap<Integer, Boolean> keysPressed; // HashMap with currently pressed keys (Integer - GLFW ID of key, Boolean)
	private HashMap<Integer, Boolean> keysHeld; // HashMap with currently held keys (Integer - GLFW ID of key, Boolean)
	private HashMap<Integer, Boolean> keysReleased; // HashMap with currently released keys (Integer - GLFW ID of key, Boolean)
	private int lastKeyPressed; // Integer with GLFW ID of last pressed key
	private int lastKeyReleased; // Integer with GLFW ID of last released key
	private int lastCharTyped;
	private boolean isKeyPressed; // Boolean with whether any key was just pressed
	private boolean isKeyReleased; // Boolean with whether any key was just released
	private boolean areKeysHeld; // Boolean with whether any keys are currently held
	private Vector2f mousePos; // JOML Vector2f with current mouse position
	private Vector2f lastPos; // JOML Vector2f with previous mouse position
	private Vector2f mouseOffset; // JOML Vector2f with difference between last mouse position and current mouse position
	private int mbPressed; // Integer with GLFW ID of currently pressed mouse button
	private int mbHeld; // Integer with GLFW ID of currently held mouse button
	private int mbReleased; // Integer with GLFW ID of currently released mouse button
	private boolean captureMouse; // Boolean with whether mouse is captured or not
	private boolean firstMouse; // Boolean with whether mouse has been moved yet, used so that mouseOffset doesn't jump at the start of the engine
		// Other variables
	private float lastFrame; // Stores the time in last frame
	private boolean resized; // Whether the window has been resized in the last frame
	
	// Debug logs
	/** Variable setting the frequency of debug logs printed to the console */
	public static int debugLogs;
	// Debug log constants
	/** No logs get printed, not even error logs */
	public static final int NONE = -1;
	/** No normal logs get printed, only error logs */
	public static final int NO_LOGS = 0;
	/** Only important core init/close logs and error logs get printed */
	public static final int REDUCED_LOGS = 1;
	/** All GL_utils debug logs get printed */
	public static final int FULL_LOGS = 2;
	
	
	/** Creates a GLFW window with the given parameters, initializes OpenGL (the OpenGL profile is set to the recommended core profile, debug logs are set to reduced logs)
	 *  @param width width of the created window
	 *  @param height height of the created window
	 *  @param resizable whether the window should be resizeable, unchangeable after window creation
	 *  @param title the title of the window
	 */
	public Window(int width, int height, boolean resizeable, String title) {
		this(width, height, resizeable, title, REDUCED_LOGS, GLFW.GLFW_OPENGL_CORE_PROFILE);
	}
	/** Creates a GLFW window with the given parameters, initializes OpenGL (the OpenGL profile is set to the recommended core profile)
	 *  @param width width of the created window
	 *  @param height height of the created window
	 *  @param resizable whether the window should be resizeable, unchangeable after window creation
	 *  @param title the title of the window
	 *  @param debugLogs whether to print GL_utils debug logs in the console or not, set by Window constants
	 */
	public Window(int width, int height, boolean resizeable, String title, int debugLogs) {
		this(width, height, resizeable, title, debugLogs, GLFW.GLFW_OPENGL_CORE_PROFILE);
	}	
	/** Creates a GLFW window with the given parameters, initializes OpenGL
	 *  @param width width of the created window
	 *  @param height height of the created window
	 *  @param resizable whether the window should be resizeable, unchangeable after window creation
	 *  @param title the title of the window
	 *  @param debugLogs whether to print GL_utils debug logs in the console or not, set by Window constants
	 *  @param glfwGlProfile the OpenGL profile of the GLFW window as defined by the GLFW constants
	 */
	public Window(int width, int height, boolean resizeable, String title, int debugLogs, int glfwGlProfile) {
		
		// General private variable initialization
			// Input variables
		keysPressed = new HashMap<>();
		keysHeld = new HashMap<>();
		keysReleased = new HashMap<>();
		lastKeyPressed = 0;
		lastKeyReleased = 0;
		lastCharTyped = 0;
		isKeyPressed = false;
		isKeyReleased = false;
		areKeysHeld = false;
		mousePos = new Vector2f();
		lastPos = new Vector2f();
		mouseOffset = new Vector2f();
		mbPressed = 0;
		mbHeld = 0;
		mbReleased = 0;
		captureMouse = false;
		firstMouse = true;
		
		// Other init
		deltaTime = 0;
		lastFrame = 0;
		resized = false;
		Window.debugLogs = debugLogs;
		
		// GLFW  initialization
		if(!GLFW.glfwInit()) {
			Window.print("Error with GLFW init, exiting engine", true, true, true, 0);
			System.exit(-1);
		}
		
		// Window hint setting - properties of window created afterwards
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, glfwGlProfile);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, (resizeable == true ? 1 : 0));
		
		// Window initialization
		window = GLFW.glfwCreateWindow(width, height, title, 0, 0);
		if(window == 0) {
			Window.print("Error with window starting, exiting engine", true, true, true, 0);
		}
		
		// OpenGL context setting and starting, OpenGL setup
		GLFW.glfwMakeContextCurrent(window); // Context of GLFW window set up for OpenGL
		GL.createCapabilities(); // Initializing OpenGL in GLFW window graphics context
		GL33.glViewport(0, 0, width, height); // Setting OpenGL viewport size to window size
		GLFW.glfwSwapInterval(1); // Setting how frequently buffers should be swapped
		
		// Printing out system info into the console for debug
		try {
			Window.print("Engine: GLFW Window and OpenGL successfully started\n -OpenGL: " + GL33.glGetString(GL33.GL_VERSION), true, 1);
			Window.print(" -System: " + System.getProperty("os.name") + " " + System.getProperty("os.version"), true, 2);
			Window.print(" -Architecture: " + System.getProperty("os.arch"), true, 2);
			Window.print(" -Java: " + System.getProperty("java.runtime.name") + " " + System.getProperty("java.runtime.version") + "\n", true, 1);
		} catch(Exception e) {
			Window.print("Engine: GLFW Window and OpenGL sucessfully started", true, 1);
			e.printStackTrace();
			Window.print("Error gathering system information", true, true, true, 0);
		}
		
		
		// Window resize callback
		GLFW.glfwSetFramebufferSizeCallback(window, new GLFWFramebufferSizeCallbackI() {
			public void invoke(long window, int width, int height) {
				GLFW.glfwSetWindowSize(window, width, height);
				GL33.glViewport(0, 0, width, height);
				resized = true;
			}
		});
		// Window key input callback
		GLFW.glfwSetKeyCallback(window, new GLFWKeyCallbackI() {
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if(action == GLFW.GLFW_PRESS) {
					keysPressed.put(key, true);
					keysHeld.put(key, true);
					lastKeyPressed = key;
					areKeysHeld = true;
					isKeyPressed = true;
				}
				if(action == GLFW.GLFW_RELEASE) {
					keysReleased.put(key, true);
					keysHeld.put(key, false);
					lastKeyReleased = key;
					areKeysHeld = false;
					isKeyReleased = true;
				}
			}
		});
		GLFW.glfwSetCharCallback(window, new GLFWCharCallbackI() {
			public void invoke(long window, int scancode) {
				lastCharTyped = scancode;
			}
		});
		// Window mouse pos callback
		GLFW.glfwSetCursorPosCallback(window, new GLFWCursorPosCallbackI() {
			public void invoke(long window, double posX, double posY) {
				if(firstMouse) {
					lastPos.set(posX, posY);
					firstMouse = false;
				}
				mousePos.set(posX, posY);
				mouseOffset = new Vector2f(mousePos.x - lastPos.x, lastPos.y - mousePos.y);
				lastPos.set(mousePos);
			}
		});
		// Window mouse press callback
		GLFW.glfwSetMouseButtonCallback(window, new GLFWMouseButtonCallbackI() {
			public void invoke(long window, int button, int action, int mods) {
				if(action == GLFW.GLFW_PRESS) {
					mbPressed = button;
					mbHeld = button;
				}
				if(action == GLFW.GLFW_RELEASE) {
					mbReleased = button;
					if(mbHeld == button) {
						mbHeld = -1;
					}
				}
			}
		});
		
		Window.print("Engine: window started successfully", true, 1);
	}
	
	// End of constructor, start of important window methods
	
	/** Updates GLFW window and inputs, necessary for the window to work */
	public void update() {
		// Delta time updating
		float currentFrame = (float)GLFW.glfwGetTime();
		deltaTime = currentFrame - lastFrame;
		lastFrame = currentFrame;
		
		// Mouse and keyboard input variable updating
		keysPressed.clear();
		keysReleased.clear();
		mbPressed = -1;
		mbReleased = -1;
		mouseOffset.zero();
		isKeyPressed = false;
		isKeyReleased = false;
		resized = false;
		
		// Polling events and swapping buffers, crucial for GLFW to work
		GLFW.glfwPollEvents();
		GLFW.glfwSwapBuffers(window);
	}
	
	/** Requests that the window should close */
	public void requestClose() {
		GLFW.glfwSetWindowShouldClose(window, true);
		Window.print("Engine: window close requested", true, 2);
	}
	
	/** Returns whether the program should be running or not, used in main while loop */
	public boolean running() {
		return !GLFW.glfwWindowShouldClose(window);
	}
	
	/** Terminates GLFW,  to be used only after all other window methods, otherwise causes problems with methods not executing properly, including the running() method */
	public void terminate() {
		GLFW.glfwTerminate();
		Window.print("Window: GLFW terminated, no GLFW using methods can be used from now on", true, 1);
	}
	
	// End of important window methods, start of generic get/set methods
	
	/** 
	 * Sets whether mouse should be captured or not
	 * @param captureMouse boolean, sets mouse capture state
	 */
	public void setMouseCapture(boolean captureMouse) {
		if(captureMouse) {
			GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
		} else {
			GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
		}
		this.captureMouse = captureMouse;
	}
	
	/** Returns if mouse is captured or not */
	public boolean isMouseCaptured() {
		return captureMouse;
	}
	
	/** Returns whether key is currently pressed */
	public boolean isKeyPressed(int key) {
		return (keysPressed.containsKey(key) && keysPressed.get(key));
	}
	/** Returns whether key is currently held */
	public boolean isKeyHeld(int key) {
		return (keysHeld.containsKey(key) && keysHeld.get(key));
	}
	/** Returns whether key is currently released */
	public boolean isKeyReleased(int key) {
		return (keysReleased.containsKey(key) && keysReleased.get(key));
	}
	/** Returns the GLFW ID of the key that was last pressed */
	public int lastKeyPressed() {
		return lastKeyPressed;
	}
	/** Returns the GLFW ID of the key that was last released */
	public int lastKeyReleased() {
		return lastKeyReleased;
	}
	/** Returns the unicode scancode of the last character typed */
	public int lastCharTyped() {
		return lastCharTyped;
	}
	/** Returns if any key was just pressed */
	public boolean anyKeyPressed() {
		return isKeyPressed;
	}
	/** Returns if any key was just released */
	public boolean anyKeyReleased() {
		return isKeyReleased;
	}
	/** Returns if any keys are currently held */
	public boolean anyKeysHeld() {
		return areKeysHeld;
	}
	
	/** Returns current mouse position in JOML Vector2f */
	public Vector2f getMousePos() {
		return mousePos;
	}
	/** Returns difference between previous mouse position and current mouse position in JOML Vector2f */
	public Vector2f getMouseOffset() {
		return mouseOffset;
	}
	
	/** Returns whether mouse button is currently pressed */
	public boolean isMbPressed(int button) {
		return (button == mbPressed);
	}
	/** Returns whether mouse button is currently held */
	public boolean isMbHeld(int button) {
		return (button == mbHeld);
	}
	/** Returns whether mouse button is currently released */
	public boolean isMbReleased(int button) {
		return (button == mbReleased);
	}
	
	/** Changes the size of the window to new width and height */
	public void setSize(int width, int height) {
		GLFW.glfwSetWindowSize(window, width, height);
	}
	/** Changes window title */
	public void setTitle(String title) {
		GLFW.glfwSetWindowTitle(window, title);
	}
	
	/** Changes the OpenGL blend function to sfactor SRC_ALPHA and dfactor ONE_MINUS_SRC_ALPHA, turns blend on or off */
	public void setTransparency(boolean on) {
		setTransparency(on, GL33.GL_SRC_ALPHA, GL33.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	/** Changes the OpenGL blend function to desired sfactor and dfactor, turns blend on or off */
	public void setTransparency(boolean on, int sfactor, int dfactor) {
		if(on) {
			GL33.glEnable(GL33.GL_BLEND);
		}
		else {
			GL33.glDisable(GL33.GL_BLEND);
		}
		GL33.glBlendFunc(sfactor, dfactor);
	}
	
	/** Sets whether to print GL_utils debug logs into the console, set by Window constants */
	public void setDebugLogs(int debugLogs) {
		Window.debugLogs = debugLogs;
	}
	
	/** Returns the GLFW frame buffer size */
	public Vector2f getFrameBufferSize() {
		int[] x = new int[1], y = new int[1];
		GLFW.glfwGetFramebufferSize(window, x, y);
		return new Vector2f(x[0], y[0]);
	}
	/** Returns the GLFW window size */
	public Vector2f getWindowSize() {
		int[] x = new int[1], y = new int[1];
		GLFW.glfwGetWindowSize(window, x, y);
		return new Vector2f(x[0], y[0]);
	}
	/** Returns whether the window was resized in the last frame */
	public boolean resized() {
		return resized;
	}
	
	/** Returns the current deltaTime, used instead of making deltaTime public to avoid users manually changing deltaTime */
	public float getDeltaTime() {
		return deltaTime;
	}
	
	// End of important window methods, start of other static methods
	
	/** Prints with settings set by args using system out or err streams
	 * @param error whether to print using the err stream or just using the out stream
	 * @param line whether to print line or just print
	 * @param debug whether to treat the print as a debug print
	 * @param debugPriority if the print is treated as debug, whether to print or not, defined by the static Window.debugLogs variable 
	 */
	public static <T> void sPrint(T toPrint, boolean error, boolean line, boolean debug, int debugPriority) {
		if(debug) {
			// Treating print like a debug log
			if(debugPriority <= Window.debugLogs) {
				sPrint(toPrint, error, line);
			}
		} else {
			// Treating print like a normal print
			sPrint(toPrint, error, line);
		}
	}
	/** Prints with settings set by args using system out stream, ends with a new line
	 * @param debug whether to treat the print as a debug print
	 * @param debugPriority if the print is treated as debug, whether to print or not, defined by the static Window.debugLogs variable 
	 */
	public static <T> void sPrint(T toPrint, boolean debug, int debugPriority) {
		sPrint(toPrint, false, true, debug, debugPriority);
	}
	/** Prints with settings set by args using system out or err streams
	 * @param line whether to print line or just print
	 * @param error whether to print using the err stream or just using the out stream
	 */
	public static <T> void sPrint(T toPrint, boolean error, boolean line) {
		if(error) {
			// Print prints in error stream
			if(line) {
				System.err.println(toPrint);
			} else {
				System.err.print(toPrint);
			}
		} else {
			// Print prints in out stream
			if(line) {
				System.out.println(toPrint);
			} else {
				System.out.print(toPrint);
			}
		}
	}
	/** Prints with settings set by args using system out or err streams, ends with a new line
	 * @param toPrint to print
	 * @param error whether to print using the err stream or just using the out stream
	 */
	public static <T> void sPrint(T toPrint, boolean error) {
		sPrint(toPrint, error, true);
	}
	/** Prints using system out stream without a newline at the end */
	@SafeVarargs
	public static <T> void print(T ... toPrint) {
		StringBuilder res = new StringBuilder();
		for(T t : toPrint) {
			res.append(t).append(" ");
		}
		sPrint(res.toString(), false, false);
	}
	/** Prints using system out stream, ends with a new line */
	@SafeVarargs
	public static <T> void println(T ... toPrint) {
		StringBuilder res = new StringBuilder();
		for(T t : toPrint) {
			res.append(t).append(" ");
		}
		sPrint(res.toString(), false, true);
	}
	/** Prints using system err stream, ends with a new line */
	@SafeVarargs
	public static <T> void printerr(T ... toPrint) {
		StringBuilder res = new StringBuilder();
		for(T t : toPrint) {
			res.append(t).append(" ");
		}
		sPrint(res.toString(), true, true);
	}
	/** Prints using system out stream, prints only if debugLogs is FULL_LOGS, ends with a new line */
	@SafeVarargs
	public static <T> void printdebug(T ... toPrint) {
		StringBuilder res = new StringBuilder();
		for(T t : toPrint) {
			res.append(t).append(" ");
		}
		sPrint(res.toString(), true, FULL_LOGS);
	}
}
