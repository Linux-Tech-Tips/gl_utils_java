package glutils.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import glutils.core.Window;

/** Class with static methods for saving and loading files */
public class FileIO {
	
	/** 
	 * Loads internal file into String, returns empty String if failed
	 * @param c class inside the application to get relative path start
	 * @param path the path to the desired file relative to the class c 
	 */
	public static String loadStringInternal(Class<?> c, String path) {
		int r = 0;
		StringBuilder data = new StringBuilder();
		BufferedInputStream in = new BufferedInputStream(c.getResourceAsStream(path));
		try {
			while(r > -1) {
				r = in.read();
				if(r != -1) {
					data.append((char)r);
				}
			}
			in.close();
			Window.print("FileManager: String from internal file " + path + " loaded successfully", true, 2);
		} catch(Exception e) {
			e.printStackTrace();
			Window.print("\nFileManager: error reading file " + path, true, true, true, 0);
		}
		return data.toString();
	}
	
	/** 
	 * Loads internal file into byte[], returns empty array if failed
	 * @param c class inside the application to get relative path start
	 * @param path the path to the desired file relative to the class c 
	 */
	public static byte[] loadByteArrayInternal(Class<?> c, String path) {
		int r = 0;
		ArrayList<Byte> temp = new ArrayList<>();
		BufferedInputStream in = new BufferedInputStream(c.getResourceAsStream(path));
		try {
			while(r > -1) {
				r = in.read();
				if(r != -1) {
					temp.add((byte) r);
				}
			}
			in.close();
			byte[] data = new byte[temp.size()];
			for(int i = 0; i < temp.size(); i++) {
				data[i] = temp.get(i);
			}
			Window.print("FileManager: byte[] from internal file " + path + " loaded successfully", true, 2);
			return data;
		} catch(Exception e) {
			e.printStackTrace();
			Window.print("\nFileManager: error reading file " + path, true, true, true, 0);
		}
		return new byte[] {};
	}
	
	/** 
	 * Loads internal file into ByteBuffer, returns null if failed
	 * @param c class inside the application to get relative path start
	 * @param path the path to the desired file relative to the class c 
	 */
	public static ByteBuffer loadByteBufferInternal(Class<?> c, String path) {
		int r = 0;
		ArrayList<Byte> temp = new ArrayList<>();
		BufferedInputStream in = new BufferedInputStream(c.getResourceAsStream(path));
		try {
			while(r > -1) {
				r = in.read();
				if(r != -1) {
					temp.add((byte) r);
				}
			}
			in.close();
			//System.out.println("FileManager debug: Internal: " + temp.toString());
			byte[] temp2 = new byte[temp.size()];
			for(int i = 0; i < temp.size(); i++) {
				temp2[i] = temp.get(i);
			}
			ByteBuffer data = ByteBuffer.allocateDirect(temp.size());
			data.put(temp2);
			data.flip();
			Window.print("FileManager: ByteBuffer from internal file " + path + " loaded successfully", true, 2);
			return data;
		} catch(Exception e) {
			e.printStackTrace();
			Window.print("\nFileManager: error reading file " + path, true, true, true, 0);
		}
		return null;
	}
	
	/** 
	 * Loads internal file into String, returns empty String if failed
	 * @param c class inside the application to get relative path start
	 * @param path the path to the desired file relative to the class c 
	 */
	public static String loadStringExternal(String path) {
		int r = 0;
		StringBuilder data = new StringBuilder();
		try {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(path)));
			while(r > -1) {
				r = in.read();
				if(r != -1) {
					data.append((char)r);
				}
			}
			in.close();
			Window.print("FileManager: String from external file " + path + " loaded successfully", true, 2);
		} catch(Exception e) {
			e.printStackTrace();
			Window.print("\nFileManager: error reading file " + path, true, true, true, 0);
		}
		return data.toString();
	}
	
	/** 
	 * Loads internal file into byte[], returns empty array if failed
	 * @param c class inside the application to get relative path start
	 * @param path the path to the desired file relative to the class c 
	 */
	public static byte[] loadByteArrayExternal(String path) {
		int r = 0;
		ArrayList<Byte> temp = new ArrayList<>();
		try {
			BufferedInputStream in  = new BufferedInputStream(new FileInputStream(new File(path)));
			while(r > -1) {
				r = in.read();
				if(r != -1) {
					temp.add((byte) r);
				}
			}
			in.close();
			byte[] data = new byte[temp.size()];
			for(int i = 0; i < temp.size(); i++) {
				data[i] = temp.get(i);
			}
			Window.print("FileManager: byte[] from external file " + path + " loaded successfully", true, 2);
			return data;
		} catch(Exception e) {
			e.printStackTrace();
			Window.print("\nFileManager: error reading file " + path, true, true, true, 0);
		}
		return new byte[] {};
	}
	
	/** 
	 * Loads internal file into ByteBuffer, returns null if failed
	 * @param path the path to the desired file relative to the class c 
	 */
	public static ByteBuffer loadByteBufferExternal(String path) {
		int r = 0;
		ArrayList<Byte> temp = new ArrayList<>();
		try {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(path)));
			while(r > -1) {
				r = in.read();
				if(r != -1) {
					temp.add((byte) r);
				}
			}
			in.close();
			byte[] temp2 = new byte[temp.size()];
			for(int i = 0; i < temp.size(); i++) {
				temp2[i] = temp.get(i);
			}
			ByteBuffer data = ByteBuffer.allocateDirect(temp.size());
			data.put(temp2);
			data.flip();
			Window.print("FileManager: ByteBuffer from external file " + path + " loaded successfully", true, 2);
			return data;
		} catch(Exception e) {
			e.printStackTrace();
			Window.print("\nFileManager: error reading file " + path, true, true, true, 0);
		}
		return null;
	}
	
	/**
	 * Saves a string into a file next to the jar
	 * @param path path to save the file to, relative to the jar
	 * @param data data to save into the file
	 */
	public static void saveExternal(String path, String data) {
		if(!path.contains("/")) path = "./" + path;
		try {
			File file = new File(path);
			file.getParentFile().mkdirs();
			FileWriter f = new FileWriter(file);
			f.write(data);
			f.close();
			Window.print("FileManager: data written to external file " + path + " successfully", true, 2);
		} catch (IOException e) {
			e.printStackTrace();
			Window.print("\nFileManager: error writing file " + path, true, true, true, 0);
		}
	}

}
