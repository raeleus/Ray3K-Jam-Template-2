package com.ray3k.template.listupdater;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.Animation;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.attachments.*;
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
    private final static LameDuckAttachmentLoader lameDuckAttachmentLoader = new LameDuckAttachmentLoader();
    
    public static void main(String args[]) {
        System.out.println("Check if lists need to be updated.");
        
        var resources = new Array<ResourceDescriptor>();
        
        boolean updated = createList("skin", "json", Paths.get("core/assets/skin.txt").toFile(), Skin.class, resources);
        updated |= createList("spine", "json", Paths.get("core/assets/spine.txt").toFile(), SkeletonData.class, resources);
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
        var subTypes = new Array<TypeSpec>();
        for (var resource : resources) {
            methodSpecBuilder.addStatement("$L = assetManager.get($S)", resource.variableName, sanitizePath(resource.file.path()));
            
            if (resource.type.equals(SkeletonData.class)) {
                methodSpecBuilder.addStatement("$LAnimationData = assetManager.get($S)", resource.variableName, sanitizePath(resource.file.path()) + "-animation");
                
                var name = sanitizeVariableName(resource.file.nameWithoutExtension());
                name = upperCaseFirstLetter(name) + "Animation";
                var typeSpecBuilder = TypeSpec.classBuilder(name)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
                
                SkeletonJson skeletonJson = new SkeletonJson(lameDuckAttachmentLoader);
                var skeletonData = skeletonJson.readSkeletonData(resource.file);
                for (var animation : skeletonData.getAnimations()) {
                    var variableName = sanitizeVariableName(animation.getName());
                    typeSpecBuilder.addField(Animation.class, variableName, Modifier.PUBLIC, Modifier.STATIC);
                    
                    methodSpecBuilder.addStatement("$L.$L = $L.findAnimation($S)", name, variableName, resource.variableName, animation.getName());
                }
                
                subTypes.add(typeSpecBuilder.build());
            }
        }
        var methodSpec = methodSpecBuilder.build();
        
        var typeSpecBuilder = TypeSpec.classBuilder("Resources")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(methodSpec);
        for (var resource : resources) {
            typeSpecBuilder.addField(resource.type, resource.variableName, Modifier.PUBLIC, Modifier.STATIC);
            
            if (resource.type.equals(SkeletonData.class)) {
                typeSpecBuilder.addField(AnimationStateData.class, resource.variableName + "AnimationData", Modifier.PUBLIC, Modifier.STATIC);
            }
        }
        for (var subType : subTypes) {
            typeSpecBuilder.addType(subType);
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
            variableName = sanitizeResourceName(file.pathWithoutExtension());
        }
        
        public ResourceDescriptor(Class type, FileHandle file, String variableName) {
            this.type = type;
            this.file = file;
            this.variableName = variableName;
        }
    }
    
    private static String sanitizeResourceName(String name) {
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
    
    private static String sanitizeVariableName(String name) {
        name = name.replaceAll("^[./]*", "").replaceAll("[\\\\/\\-\\s]", "_").replaceAll("['\"]", "");
        var splits = name.split("_");
        var builder = new StringBuilder(splits[0]);
        for (int i = 1; i < splits.length; i++) {
            var split = splits[i];
            builder.append(Character.toUpperCase(split.charAt(0)));
            builder.append(split.substring(1));
        }
        
        return builder.toString();
    }
    
    private static String sanitizePath(String path) {
        return path.replaceAll("\\./core/assets/", "");
    }
    
    private static String upperCaseFirstLetter(String string) {
        return Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }
    
    private static class LameDuckAttachmentLoader implements AttachmentLoader {
        @Override
        public RegionAttachment newRegionAttachment(com.esotericsoftware.spine.Skin skin, String name,
                                                    String path) {
            return new RegionAttachment(name);
        }
        
        @Override
        public MeshAttachment newMeshAttachment(com.esotericsoftware.spine.Skin skin, String name,
                                                String path) {
            return new MeshAttachment(name);
        }
        
        @Override
        public BoundingBoxAttachment newBoundingBoxAttachment(com.esotericsoftware.spine.Skin skin,
                                                              String name) {
            return new BoundingBoxAttachment(name);
        }
        
        @Override
        public ClippingAttachment newClippingAttachment(com.esotericsoftware.spine.Skin skin, String name) {
            return new ClippingAttachment(name);
        }
        
        @Override
        public PathAttachment newPathAttachment(com.esotericsoftware.spine.Skin skin, String name) {
            return new PathAttachment(name);
        }
        
        @Override
        public PointAttachment newPointAttachment(com.esotericsoftware.spine.Skin skin, String name) {
            return new PointAttachment(name);
        }
    }
}