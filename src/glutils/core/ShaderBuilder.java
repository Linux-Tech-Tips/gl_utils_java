package glutils.core;

/** Class that allows dynamic in-code shader generation */
public class ShaderBuilder {
	
	// VARIABLES
	
	/** The shader data, glsl code is appended to it using the ShaderBuilder methods */
	public StringBuilder shaderString;
	
	// For dynamic indentation with glsl generation
	private float tabNumber;
	
	// CONSTANTS
	
	public static final String SHADER_PROFILE_CORE = "core";
	public static final String SHADER_PROFILE_COMPAT = "";
	
	// Vertex shader generation
	/** Take normals from layout (location = 2), send them out of the shader */
	public static final int V_USE_NORMALS = 1;
	/** Use camera projection and view matrix uniforms (names set to camera defaults "proj" and "view") */
	public static final int V_USE_CAMERA = 2;
	/** Use a transform (model) matrix (name set to Model default "transform") */
	public static final int V_USE_MODEL_TRANSFORM = 3;
	/** Vertex shader preset using all of the vertex shader constants */
	public static final int[] V_PRESET_ALL = new int[] {1,2,3};
	// Fragment shader generation
	/** Creates a fragment shader that uses a diffuse texture (needs texCoords from vertex shader, Texture from code) */
	public static final int F_SIMPLE_TEXTURED = 1;
	/** Creates a fragment shader that uses a diffuse texture and a directional light (needs texCoords and normals from vertex shader, Texture and DirectionalLight from code) */
	public static final int F_SIMPLE_DIR_LIGHT = 2;
	/** Creates a fragment shader that uses a diffuse texture and a world light (needs texCoords and normals from vertex shader, Texture and WorldLight from code) */
	public static final int F_SIMPLE_WORLD_LIGHT = 3;
	/** Creates a fragment shader that uses a material and a directional light (needs texCoords and normals from vertex shader, Material and DirectionalLight from code) */
	public static final int F_SPECULAR_DIR_LIGHT = 4;
	/** Creates a fragment shader that uses a material and a world light (needs texCoords and normals from vertex shader, Material and WorldLight from code) */
	public static final int F_SPECULAR_WORLD_LIGHT = 5;
	
	// CONSTRUCTORS
	
	/** 
	 * Initializes the ShaderBuilder instance with the given glsl version set as the shader's glsl version and the given profile as the glsl OpenGL profile
	 * @param glslVersion the version of the glsl shader, recommended 330 or higher
	 * @param profile the OpenGL profile used in the glsl shader, set by ShaderBuilder constants, recommended core profile, leave blank for compatibility profile
	 */
	public ShaderBuilder(int glslVersion, String profile) {
		this();
		shaderString.append("#version " + glslVersion + " " + profile + "\n");
	}
	
	/** 
	 * Initializes the ShaderBuilder instance with the given glsl version set as the shader's glsl version and with the OpenGL profile set to the recommended core profile
	 * @param glslVersion the version of the glsl shader, recommended 330 or higher
	 */
	public ShaderBuilder(int glslVersion) {
		this(glslVersion, SHADER_PROFILE_CORE);
	}
	
	/** Initializes a completely empty ShaderBuilder instance */
	public ShaderBuilder() {
		shaderString = new StringBuilder();
		tabNumber = 0;
	}
	
	// PREPROCESSOR COMMANDS
	
	/** Adds an extension declaration as the next line of the shader */
	public void addExtension(String extentionName, String behavior) {
		shaderString.append("#extension " + extentionName + " : " + behavior + "\n");
	}
	
	/** Adds a preprocessor directive (input directive in between a # and a line break) as the next line of the shader */
	public void addPreprocessorDirective(String directive) {
		shaderString.append("#" + directive + "\n");
	}
	
	// VARIABLES
	
	/** Adds a variable with the given qualifier (in/out/uniform), data type, name and assignment (without =, just value to be assigned) as the next line of the shader, leave qualifier or assignment blank to omit it */
	public void addVariableFull(String qualifier, String dataType, String name, String assignment) {
		String start = (!qualifier.equals("") ? (qualifier + " ") : "");
		String end = (!assignment.equals("") ? (" = " + assignment + ";\n") : ";\n");
		shaderString.append(genTabs() + start + dataType + " " + name + end);
	}
	/** Adds a variable with the given data type and name (without a beginning qualifier and without an assignment) as the next line of the shader */
	public void addVariableShort(String dataType, String name) {
		addVariableFull("", dataType, name, "");
	}
	/** Adds a variable with the given qualifier, data type and name (without an assignment) as the next line of the shader */
	public void addVariableMediumQualifier(String qualifier, String dataType, String name) {
		addVariableFull(qualifier, dataType, name, "");
	}
	/** Adds a variable with the given data type, name and assignment (without a beginning qualifier) as the next line of the shader */
	public void addVariableMediumAssignment(String dataType, String name, String assignment) {
		addVariableFull("", dataType, name, assignment);
	}
	
	/** Adds a layout variable with the given qualifier (in/out), data type and name for the given location as the next line of the shader */
	public void addLayoutVariable(int location, String qualifier, String dataType, String name) {
		shaderString.append(genTabs() + "layout (location = " + location + ") " + qualifier + " " + dataType + " " + name + ";\n");
	}
	
	// STRUCTS
	
	/** Opens a struct with the given name as the next line of the shader, typically followed by adding struct code and has to be closed using closeStruct() method */
	public void openStruct(String name) {
		shaderString.append(genTabs() + "struct " + name + " {\n");
		tabNumber++;
	}
	/** Closes a struct opened by openStruct() method as the next line of the shader */
	public void closeStruct() {
		tabNumber--;
		shaderString.append(genTabs() + "};\n");
	}
	
	// FUNCTIONS
	
	/** Opens a function with the given return type, name and arguments as the next line of the shader, typically followed by adding function code and has to be closed using closeFunction() method */
	public void openFunction(String returnType, String name, String args) {
		shaderString.append(genTabs() + returnType + " " + name + " (" + args + ") {\n");
		tabNumber++;
	}
	/** Opens a function with the given return type and name (without any arguments) as the next line of the shader, typically followed by adding function code and has to be closed using closeFunction() method */
	public void openFunction(String returnType, String name) {
		openFunction(returnType, name, "");
	}
	/** Closes a function opened by openFunction() method as the next line of the shader */
	public void closeFunction() {
		tabNumber--;
		shaderString.append(genTabs() + "}\n");
	}
	
	/** Opens the main function as the next line of the shader, has to be closed using closeFunction() method */
	public void openMainFunction() {
		shaderString.append(genTabs() + "void main () {\n");
		tabNumber++;
	}
	
	/** Calls the specified function with the set arguments as the next line of the shader */
	public void callFunction(String functionName, String args, boolean ignoreTabs, boolean makeLineEnd) {
		String prefix = (ignoreTabs ? "" : genTabs());
		String suffix = (makeLineEnd ? ");\n" : ")");
		shaderString.append(prefix + functionName + "(" + args + suffix);
	}
	/** Calls the specified function with the set arguments as the next line of the shader, user sets whether function should be formatted to be inside code or a standalone call */
	public void callFunction(String functionName, String args, boolean inCode) {
		callFunction(functionName, args, inCode, !inCode);
	}
	/** Calls the specified function with the set arguments as the next line of the shader, the call is formatted as a stand alone call (tabs at the start, ; and line break at the end) */
	public void callFunction(String functionName, String args) {
		callFunction(functionName, args, false, true);
	}
	
	// STATEMENTS
	
	/** Opens an if statement with the condition as the next line of the shader, has to be closed using closeIf() method */
	public void openIf(String condition) {
		shaderString.append(genTabs() + "if (" + condition + ") {\n");
		tabNumber++;
	}
	/** Adds an else if part of an if statement, closes the if statement and opens an else if afterwards as the next line of the shader */
	public void addElseIf(String condition) {
		tabNumber--;
		shaderString.append(genTabs() + "} else if (" + condition + ") {\n");
		tabNumber++;
	}
	/** Closes an if statement as the next line of the shader, can open an else part of the statement instead */
	public void closeIf(boolean useElse) {
		tabNumber--;
		String additional = (useElse ? " else {\n" : "\n");
		shaderString.append(genTabs() + "}" + additional);
		if(useElse) tabNumber++;
	}
	
	/** Opens a while loop as the next line of the shader */
	public void openWhile(String condition) {
		shaderString.append(genTabs() + "while (" + condition + ") {\n");
		tabNumber++;
	}
	/** Closes a while loop as the next line of the shader */
	public void closeWhile() {
		closeFunction();
	}
	
	/** Opens a for loop with the given string as the passed arguments as the next line of the shader */
	public void openFor(String forLoopArgs) {
		shaderString.append(genTabs() + "for (" + forLoopArgs + ") {\n");
		tabNumber++;
	}
	/** Closes a for loop as the next line of the shader */
	public void closeFor() {
		closeFunction();
	}
	
	/** Calls a statement with the given name and the given properties after the statement (for example statementName="return", statementProperties="dataToReturn") as the next line of the shader */
	public void callStatement(String statementName, String statementProperties) {
		String end = (!statementProperties.equals("") ? (" " + statementProperties + ";\n") : ";\n");
		shaderString.append(genTabs() + statementName + end);
	}
	/** Calls a statement by itself as the next line of the shader */
	public void callStatement(String statementName) {
		callStatement(statementName, "");
	}
	
	// OTHER METHODS
	
	/** Adds custom code as the next line of the shader */
	public void addCode(String code, boolean ignoreTabs, boolean endLine) {
		String prefix = (ignoreTabs ? "" : genTabs());
		String suffix = (endLine ? "\n" : "");
		shaderString.append(prefix + code + suffix);
	}
	/** Adds custom code as the next line of the shader, follows tabs at the beginning of the line, puts a line break at the end of the custom code */
	public void addCode(String code) {
		addCode(code, false, true);
	}
	
	/** Adds a comment with the given text as the next line of the shader */
	public void addComment(String text, boolean ignoreTabs) {
		String prefix = (ignoreTabs ? "" : genTabs());
		shaderString.append(prefix + "// " + text + "\n");
	}
	/** Adds a comment with the given text as the next line of the shader, tabs at the beginning of the line are automatically used */
	public void addComment(String text) {
		addComment(text, false);
	}
	
	/** Adds line break as the next line of the shader */
	public void addLineBreak() {
		shaderString.append("\n");
	}
	
	/** Returns the current shader data */
	public String build() {
		return shaderString.toString();
	}
	
	/** Generates appropriate tabs to put in front of line */
	private String genTabs() {
		String tabs = "";
		for(int i = 0; i < tabNumber; i++) tabs += "	";
		return tabs;
	}
	
	// STATIC METHODS TO GENERATE SHADER STRING WITHOUT GLSL
	/** Generates a vertex shader based on the modifier arguments (set by ShaderBuilder constants) */
	public static String genDynamicVertexShader(int ... modifiers) {
		
		// Setting booleans to build shader by
		boolean normals = false, camera = false, transform = false;
		for(int i : modifiers) {
			if(i == V_USE_NORMALS) normals = true;
			if(i == V_USE_CAMERA) camera = true;
			if(i == V_USE_MODEL_TRANSFORM) transform = true;
		}
		
		// ShaderBuilder start
		ShaderBuilder glsl = new ShaderBuilder(330);
		
		// Ins and Outs
		glsl.addLayoutVariable(0, "in", "vec3", "vPos");
		glsl.addVariableMediumQualifier("out", "vec4", "worldPos");
		glsl.addLineBreak();
		
		glsl.addLayoutVariable(1, "in", "vec2", "vTexCoords");
		glsl.addVariableMediumQualifier("out", "vec2", "texCoords");
		glsl.addLineBreak();
		
		if(normals) {
			glsl.addLayoutVariable(2, "in", "vec3", "vNormal");
			glsl.addVariableMediumQualifier("out", "vec3", "normal");
			glsl.addLineBreak();
		}
		
		// Uniforms
		if(transform) glsl.addVariableMediumQualifier("uniform", "mat4", "transform");
		if(camera) {
			glsl.addVariableMediumQualifier("uniform", "mat4", "view");
			glsl.addVariableMediumQualifier("uniform", "mat4", "proj");
		}
		glsl.addLineBreak();
		
		// Main
		glsl.openMainFunction();
		glsl.addCode("worldPos = " + (transform ? "transform * " : "") + " vec4(vPos, 1.0);");
		glsl.addCode("gl_Position = " + (camera ? "proj * view * " : "") + " worldPos;");
		glsl.addCode("texCoords = vTexCoords;");
		if(normals) glsl.addCode("normal = vNormal;");
		glsl.closeFunction();
		
		return glsl.build();
		
	}
	/** Generates a fragment shader based on the type argument (set by ShaderBuilder constants) */
	public static String genDynamicFragmentShader(int type) {
		
		// ShaderBuilder start
		ShaderBuilder glsl = new ShaderBuilder(330);
		boolean simpleLight = type == F_SIMPLE_DIR_LIGHT || type == F_SIMPLE_WORLD_LIGHT;
		boolean specularLight = type == F_SPECULAR_DIR_LIGHT || type == F_SPECULAR_WORLD_LIGHT;
		boolean anyLight = simpleLight || specularLight;
		//boolean directionalLight = type == F_SIMPLE_DIR_LIGHT || type == F_SPECULAR_DIR_LIGHT;
		boolean worldLight = type == F_SIMPLE_WORLD_LIGHT || type == F_SPECULAR_WORLD_LIGHT;
		
		// Preprocessor
		if(worldLight) glsl.addPreprocessorDirective("define NUM_POINT_LIGHTS 12"); 
		glsl.addLineBreak();
		
		// Structs
		if(specularLight) {
			// MATERIAL STRUCT
			glsl.openStruct("Material");
			glsl.addVariableShort("sampler2D", "diffuse");
			glsl.addVariableShort("sampler2D", "specular");
			glsl.addVariableShort("int", "shininess");
			glsl.closeStruct();
		}
		if(anyLight) {
			// DIRECTIONAL LIGHT STRUCT
			glsl.openStruct("DirectionalLight");
			glsl.addVariableShort("vec3", "direction");
			glsl.addVariableShort("vec4", "color");
			glsl.addVariableShort("float", "intensity");
			glsl.closeStruct();
		}
		if(worldLight) {
			// POINT LIGHT STRUCT
			glsl.openStruct("PointLight");
			glsl.addVariableShort("vec3", "position");
			glsl.addVariableShort("vec4", "color");
			glsl.addVariableShort("float", "intensity");
			glsl.addVariableShort("float", "falloffLinear");
			glsl.addVariableShort("float", "falloffQuadratic");
			glsl.closeStruct();
		}
		glsl.addLineBreak();
		
		// Ins and Outs
		glsl.addVariableMediumQualifier("in", "vec4", "worldPos");
		glsl.addVariableMediumQualifier("in", "vec2", "texCoords");
		if(anyLight) glsl.addVariableMediumQualifier("in", "vec3", "normal");
		glsl.addLineBreak();
		glsl.addVariableMediumQualifier("out", "vec4", "FragColor");
		glsl.addLineBreak();
		
		// Uniforms
		if(specularLight) 
			glsl.addVariableMediumQualifier("uniform", "Material", "material");
		else
			glsl.addVariableMediumQualifier("uniform", "sampler2D", "tex");
		
		if(anyLight) {
			glsl.addVariableMediumQualifier("uniform", "DirectionalLight", "directionalLight");
			glsl.addVariableMediumQualifier("uniform", "vec4", "ambientLight");
		}
		if(worldLight) {
			glsl.addVariableMediumQualifier("uniform", "PointLight", "pointLight[NUM_POINT_LIGHTS]");
			glsl.addVariableMediumQualifier("uniform", "int", "pointLightsUsed");
		}
		glsl.addVariableMediumQualifier("uniform", "vec3", "camPos");
		
		// Functions
		if(simpleLight) {
			glsl.addCode("// Calculates the color of the object with the directional light and texture\n"
					+ "vec4 calculateWorldLight(DirectionalLight dirLight, vec4 ambient, sampler2D tex, vec2 texCoords, vec3 normal) \n"
					+ "{\n"
					+ "	// Checking whether to calculate light (discard if frag is transparent to allow full texture transparency)\n"
					+ "	vec4 diffuseTexture = texture(tex, texCoords);\n"
					+ "	if(diffuseTexture.a == 0) \n"
					+ "	{\n"
					+ "		discard;\n"
					+ "	}\n"
					+ "	\n"
					+ "	// Calculating diffuse multiplier\n"
					+ "	vec3 nLightDir = normalize(dirLight.direction);\n"
					+ "	float diffuseValue = max(dot(-nLightDir, normal), 0.0);\n"
					+ "	\n"
					+ "	// Getting color values using multiplier, texture and light color\n"
					+ "	vec4 ambientColor = ambient * diffuseTexture;\n"
					+ "	vec4 directionalColor = dirLight.color * diffuseValue * diffuseTexture;\n"
					+ "	\n"
					+ "	// Returning sum of all color values\n"
					+ "	return vec4(ambientColor + directionalColor);\n"
					+ "}", true, true);
			if(worldLight) {
				glsl.addCode("// Calculates the color of the object with the point light and material\n"
						+ "vec4 calculatePointLight(PointLight pointLight, sampler2D tex, vec2 texCoords, vec4 pos, vec3 normal)\n"
						+ "{\n"
						+ "	// Checking whether to calculate light (discard if frag is transparent to allow full texture transparency)\n"
						+ "	vec4 diffuseTexture = texture(tex, texCoords);\n"
						+ "	if(diffuseTexture.a == 0) \n"
						+ "	{\n"
						+ "		discard;\n"
						+ "	}\n"
						+ "	\n"
						+ "	// Calculating diffuse multiplier\n"
						+ "	vec3 lightDir = normalize(vec3(pos.xyz) - pointLight.position);\n"
						+ "	float diffuseValue = max(dot(-lightDir, normal), 0.0);\n"
						+ "	\n"
						+ "	// Calculating attenuation (falloff)\n"
						+ "	float distance = length(pointLight.position - vec3(pos.xyz));\n"
						+ "	float falloff = 1.0 / (1.0 + pointLight.falloffLinear * distance + pointLight.falloffQuadratic * (distance * distance)); \n"
						+ "	\n"
						+ "	// Getting color values using multiplier, texture, light color and attenuation\n"
						+ "	vec4 ambientColor = pointLight.color * diffuseTexture * falloff;\n"
						+ "	vec4 directionalColor = pointLight.color * diffuseValue * diffuseTexture * falloff;\n"
						+ "	\n"
						+ "	// Returning sum of all color values\n"
						+ "	return vec4(ambientColor + directionalColor);\n"
						+ "}\n"
						+ "\n"
						+ "// Adds up all the colors from the directional light and point light calculations to make the final fragment colors\n"
						+ "vec4 calculateLight(DirectionalLight dirLight, PointLight pointLight[NUM_POINT_LIGHTS], vec4 ambient, sampler2D tex, vec2 texCoords, vec4 pos, vec3 normal) \n"
						+ "{\n"
						+ "	vec4 f = calculateWorldLight(dirLight, ambient, tex, texCoords, normal);\n"
						+ "	for(int i = 0; i < min(NUM_POINT_LIGHTS, pointLightsUsed); i++) \n"
						+ "	{\n"
						+ "		f += calculatePointLight(pointLight[i], tex, texCoords, pos, normal);\n"
						+ "	}\n"
						+ "	return f;\n"
						+ "}", true, true);
			}
		} else if(specularLight) {
			glsl.addCode("// Calculates the color of the object with the directional light and material\n"
					+ "vec4 calculateWorldLight(DirectionalLight dirLight, vec4 ambient, Material material, vec2 texCoords, vec3 normal, vec3 viewDir) \n"
					+ "{\n"
					+ "	// Checking whether to lightCalc (discard if frag is transparent to allow full texture transparency)\n"
					+ "	vec4 diffuseTexture = texture(material.diffuse, texCoords);\n"
					+ "	vec4 specularTexture = texture(material.specular, texCoords);\n"
					+ "	if(diffuseTexture.a == 0) \n"
					+ "	{\n"
					+ "		discard;\n"
					+ "	}\n"
					+ "	\n"
					+ "	// Calculating diffuse multiplier\n"
					+ "	vec3 nLightDir = normalize(dirLight.direction);\n"
					+ "	float diffuseValue = max(dot(-nLightDir, normal), 0.0);\n"
					+ "	// Calculating specular multiplier\n"
					+ "	vec3 reflectDir = reflect(nLightDir, normal);\n"
					+ "	float specularValue = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);\n"
					+ "	\n"
					+ "	// Getting color values using multiplier, textures and light color\n"
					+ "	vec4 ambientColor = ambient * diffuseTexture;\n"
					+ "	vec4 directionalColor = dirLight.color * diffuseValue * diffuseTexture;\n"
					+ "	vec4 specularColor = dirLight.color * specularValue * specularTexture;\n"
					+ "	\n"
					+ "	// Returning sum of all color values\n"
					+ "	return vec4(ambientColor + directionalColor + specularColor);\n"
					+ "}", true, true);
			if(worldLight) {
				glsl.addCode("// Calculates the color of the object with the point light and material\n"
						+ "vec4 calculatePointLight(PointLight pointLight, Material material, vec2 texCoords, vec4 pos, vec3 normal, vec3 viewDir)\n"
						+ "{\n"
						+ "	// Checking whether to lightCalc (discard if frag is transparent to allow full texture transparency)\n"
						+ "	vec4 diffuseTexture = texture(material.diffuse, texCoords);\n"
						+ "	vec4 specularTexture = texture(material.specular, texCoords);\n"
						+ "	if(diffuseTexture.a == 0) \n"
						+ "	{\n"
						+ "		discard;\n"
						+ "	}\n"
						+ "	\n"
						+ "	// Calculating diffuse multiplier\n"
						+ "	vec3 lightDir = normalize(vec3(pos.xyz) - pointLight.position);\n"
						+ "	float diffuseValue = max(dot(-lightDir, normal), 0.0);\n"
						+ "	// Calculating specular multiplier\n"
						+ "	vec3 reflectDir = reflect(lightDir, normal);\n"
						+ "	float specularValue = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);\n"
						+ "	\n"
						+ "	// Calculating attenuation (falloff)\n"
						+ "	float distance = length(pointLight.position - vec3(pos.xyz));\n"
						+ "	float falloff = 1.0 / (1.0 + pointLight.falloffLinear * distance + pointLight.falloffQuadratic * (distance * distance)); \n"
						+ "	\n"
						+ "	// Getting color values using multiplier, textures, light color and attenuation\n"
						+ "	vec4 ambientColor = pointLight.color * diffuseTexture * falloff;\n"
						+ "	vec4 directionalColor = pointLight.color * diffuseValue * diffuseTexture * falloff;\n"
						+ "	vec4 specularColor = pointLight.color * specularValue * specularTexture * falloff;\n"
						+ "	\n"
						+ "	// Returning sum of all color values\n"
						+ "	return vec4(ambientColor + directionalColor + specularColor);\n"
						+ "}\n"
						+ "\n"
						+ "// Adds up all the colors from the directional light and point light calculations to make the final fragment colors\n"
						+ "vec4 calculateLight(DirectionalLight dirLight, PointLight pointLight[NUM_POINT_LIGHTS], vec4 ambient, Material material, vec2 texCoords, vec4 pos, vec3 normal, vec3 viewDir) \n"
						+ "{\n"
						+ "	vec4 f = calculateWorldLight(dirLight, ambient, material, texCoords, normal, viewDir);\n"
						+ "	for(int i = 0; i < min(NUM_POINT_LIGHTS, pointLightsUsed); i++) \n"
						+ "	{\n"
						+ "		f += calculatePointLight(pointLight[i], material, texCoords, pos, normal, viewDir);\n"
						+ "	}\n"
						+ "	return f;\n"
						+ "}", true, true);
			}
		}
		
		// Main
		glsl.openMainFunction();
		
		if(simpleLight)
			glsl.addVariableMediumAssignment("vec4", "fCol", (worldLight ? "calculateLight(directionalLight, pointLight, ambientLight, tex, texCoords, worldPos, normalize(normal))" : "calculateWorldLight(directionalLight, directionalLight.color, tex, texCoords, normalize(normal))"));
		else if(specularLight)
			glsl.addVariableMediumAssignment("vec4", "fCol", (worldLight ? "calculateLight(directionalLight, pointLight, ambientLight, material, texCoords, worldPos, normalize(normal), normalize(camPos - vec3(worldPos.xyz)))" : "calculateWorldLight(directionalLight, directionalLight.color, material, texCoords, normalize(normal), normalize(camPos - vec3(worldPos.xyz)))"));
		else if(type == F_SIMPLE_TEXTURED) {
			glsl.addVariableMediumAssignment("vec4", "fCol", "texture(tex, texCoords)");
			glsl.openIf("fCol.a == 0");
			glsl.callStatement("discard");
			glsl.closeIf(false);
		}
		
		glsl.addCode("FragColor = fCol;");
		
		glsl.closeFunction();
		
		return glsl.build();
	}
}
