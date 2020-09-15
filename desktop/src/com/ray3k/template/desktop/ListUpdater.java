package com.ray3k.template.desktop;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.SkeletonData;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;

public class ListUpdater {
    public static void main(String args[]) {
        System.out.println("Check if lists need to be updated.");
        
        var resources = new Array<ResourceDescriptor>();
        
        boolean updated = createList("skin", "json", Paths.get("core/assets/skin.txt").toFile(), Skin.class, resources);
        var spineResources = new Array<ResourceDescriptor>();
        updated |= createList("spine", "json", Paths.get("core/assets/spine.txt").toFile(), SkeletonData.class, spineResources);
        resources.addAll(spineResources);
        for (var spineResource : spineResources) {
            var resource = new ResourceDescriptor(AnimationStateData.class, spineResource.file.sibling(spineResource.file.name() + "-animation"), spineResource.variableName + "Animation");
            resources.add(resource);
        }
        updated |= createList("textures", "atlas", Paths.get("core/assets/textures.txt").toFile(), TextureAtlas.class, resources);
        updated |= createList("sfx", "mp3", Paths.get("core/assets/sfx.txt").toFile(), Sound.class, resources);
        updated |= createList("bgm", "mp3", Paths.get("core/assets/bgm.txt").toFile(), Music.class, resources);
        
        writeResources(resources, Paths.get("core/src/com/ray3k/template/Resources.java").toFile());
        
        if (updated) {
            System.out.println("Updated lists.");
        } else {
            System.out.println("Lists not updated.");
        }
    }
    
    private static boolean createList(String folderName, String extension, File outputPath, Class type, Array<ResourceDescriptor> resources) {
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
                
                resources.add(new ResourceDescriptor(type, fileHandle));
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
    
    private static void writeResources(Array<ResourceDescriptor> resources, File resourcesFile) {
        var methodSpecBuilder = MethodSpec.methodBuilder("loadResources")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(AssetManager.class, "assetManager");
        for (var resource : resources) {
            methodSpecBuilder.addStatement("$L = assetManager.get($S)", resource.variableName, sanitizePath(resource.file.path()));
        }
        var methodSpec = methodSpecBuilder.build();
    
        var typeSpecBuilder = TypeSpec.classBuilder("Resources")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(methodSpec);
        for (var resource : resources) {
            typeSpecBuilder.addField(resource.type, resource.variableName, Modifier.PUBLIC, Modifier.STATIC);
        }
        var typeSpec = typeSpecBuilder.build();
        
        var javaFile = JavaFile.builder("com.ray3k.template", typeSpec)
                .indent("    ")
                .build();
    
        try {
            Files.writeString(resourcesFile.toPath(), javaFile.toString());
        } catch (Exception e) {}
    }
    
    private static final class ResourceDescriptor {
        Class type;
        FileHandle file;
        String variableName;
    
        public ResourceDescriptor(Class type, FileHandle file) {
            this.type = type;
            this.file = file;
            variableName = sanitizeVariableName(file.pathWithoutExtension());
        }
    
        public ResourceDescriptor(Class type, FileHandle file, String variableName) {
            this.type = type;
            this.file = file;
            this.variableName = variableName;
        }
    }
    
    private static String sanitizeVariableName(String name) {
        name = name.replaceAll("^[./]*", "").replaceAll("[\\\\/\\-\\s]", "_").replaceAll("['\"]", "");
        var splits = name.split("_");
        var builder = new StringBuilder(splits[2]);
        builder.append("_");
        if (splits.length >= 4) {
            builder.append(splits[3]);
        }
        
        for (int i = 4; i < splits.length; i++) {
            var split = splits[i];
            builder.append(Character.toUpperCase(split.charAt(0)));
            builder.append(split.substring(1));
        }
        
        return builder.toString();
    }
    
    private static String sanitizePath(String path) {
        return path.replaceAll("\\./core/assets/", "");
    }
}
