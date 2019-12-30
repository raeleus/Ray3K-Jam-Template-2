package com.ray3k.template.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.ray3k.template.Core;
import com.ray3k.template.CrossPlatformWorker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;

public class DesktopLauncher implements CrossPlatformWorker {
	public static boolean createLists;
	
	public static void main (String[] args) {
		if (args.length > 0) createLists = args[0].equals("create-lists");
		
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(1024, 576);
		config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 3);
		Core core = new Core();
		core.crossPlatformWorker = new DesktopLauncher();
		new Lwjgl3Application(core, config);
	}
	
	@Override
	public void create() {
		if (createLists) {
			boolean updated = createList("skin", "json", Gdx.files.local("core/assets/skin.txt"));
			updated |= createList("spine", "json", Gdx.files.local("core/assets/spine.txt"));
			updated |= createList("spine", "atlas", Gdx.files.local("core/assets/spine-atlas.txt"));
			updated |= createList("spine-libgdx", "json", Gdx.files.local("core/assets/spine-libgdx.txt"));
			updated |= createList("spine-libgdx", "atlas", Gdx.files.local("core/assets/spine-libgdx-atlas.txt"));
			updated |= createList("spine-ray3k", "json", Gdx.files.local("core/assets/spine-ray3k.txt"));
			updated |= createList("spine-ray3k", "atlas", Gdx.files.local("core/assets/spine-ray3k-atlas.txt"));
			updated |= createList("sfx", "mp3", Gdx.files.local("core/assets/sfx.txt"));
			updated |= createList("bgm", "mp3", Gdx.files.local("core/assets/bgm.txt"));
			
			if (updated) {
				System.out.println("Updated lists, please run again.");
				Gdx.app.exit();
			}
		}
	}
	
	private boolean createList(String folderName, String extension, FileHandle outputPath) {
		boolean changed = false;
		try {
			MessageDigest md5Digest = MessageDigest.getInstance("MD5");
			String digest = outputPath.exists() ? getFileChecksum(md5Digest, outputPath.file()) : "";
			Array<FileHandle> files = new Array<>();
			
			File directory = new File("./core/assets/" + folderName + "/");
			files.addAll(createList(directory, extension));
			
			String outputText = "";
			for (int i = 0; i < files.size; i++) {
				FileHandle fileHandle = files.get(i);
				outputText += fileHandle.path().replace("./core/assets/", "");
				if (i < files.size - 1) {
					outputText += "\n";
				}
			}
			if (!outputText.equals("")) {
				outputPath.writeString(outputText, false);
			} else {
				outputPath.delete();
			}
			changed = !getFileChecksum(md5Digest, outputPath.file()).equals(digest);
		} catch (Exception e) {
		
		}
		return changed;
	}
	
	private Array<FileHandle> createList(File folder, String extension) {
		Array<FileHandle> files = new Array<>();
		
		if (folder.listFiles() != null) for (File file : folder.listFiles()) {
			if (file.isFile()) {
				if (file.getPath().toLowerCase().endsWith(extension.toLowerCase())) {
					files.add(new FileHandle(file));
				}
			} else {
				files.addAll(createList(file, extension));
			}
		}
		return files;
	}
	
	private static String getFileChecksum(MessageDigest digest, File file) throws IOException
	{
		//Get file input stream for reading the file content
		FileInputStream fis = new FileInputStream(file);
		
		//Create byte array to read data in chunks
		byte[] byteArray = new byte[1024];
		int bytesCount = 0;
		
		//Read file data and update in message digest
		while ((bytesCount = fis.read(byteArray)) != -1) {
			digest.update(byteArray, 0, bytesCount);
		};
		
		//close the stream; We don't need it now.
		fis.close();
		
		//Get the hash's bytes
		byte[] bytes = digest.digest();
		
		//This bytes[] has bytes in decimal format;
		//Convert it to hexadecimal format
		StringBuilder sb = new StringBuilder();
		for(int i=0; i< bytes.length ;i++)
		{
			sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		
		//return complete hash
		return sb.toString();
	}
}
