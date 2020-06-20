package com.ray3k.template.desktop;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;

public class ListUpdater {
    public static void main(String args[]) {
        boolean updated = createList("skin", "json", Paths.get("core/assets/skin.txt").toFile());
        updated |= createList("spine", "json", Paths.get("core/assets/spine.txt").toFile());
        updated |= createList("textures", "atlas", Paths.get("core/assets/textures.txt").toFile());
        updated |= createList("sfx", "mp3", Paths.get("core/assets/sfx.txt").toFile());
        updated |= createList("bgm", "mp3", Paths.get("core/assets/bgm.txt").toFile());
        
        if (updated) {
            System.out.println("Updated lists.");
        }
    }
    
    private static boolean createList(String folderName, String extension, File outputPath) {
        boolean changed = false;
        try {
            MessageDigest md5Digest = MessageDigest.getInstance("MD5");
            String digest = outputPath.exists() ? getFileChecksum(md5Digest, outputPath) : "";
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
                Files.writeString(outputPath.toPath(), outputText);
            } else {
                outputPath.delete();
            }
            changed = !getFileChecksum(md5Digest, outputPath).equals(digest);
        } catch (Exception e) {
        
        }
        return changed;
    }
    
    private static Array<FileHandle> createList(File folder, String extension) {
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
    
    private static String getFileChecksum(MessageDigest digest, File file) throws IOException {
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
