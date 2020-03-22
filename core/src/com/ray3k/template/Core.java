package com.ray3k.template;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.crashinvaders.vfx.VfxManager;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.ray3k.template.screens.LoadScreen;
import com.ray3k.template.transitions.Transitions;

public class Core extends JamGame {
    public static final String PROJECT_NAME = "Template Game";
    public static Core core;
    public Skin skin;
    public SkeletonRenderer skeletonRenderer;
    public ChangeListener sndChangeListener;
    public VfxManager vfxManager;
    public CrossPlatformWorker crossPlatformWorker;
    public static enum Binding {
        LEFT, RIGHT, UP, DOWN, SHOOT, SPECIAL, SHIELD;
    }
    public float bgm;
    public float sfx;
    public Preferences preferences;
    
    @Override
    public void create() {
        super.create();
        core = this;
        
        preferences = Gdx.app.getPreferences(PROJECT_NAME);
        
        bgm = preferences.getFloat("bgm", 1.0f);
        sfx = preferences.getFloat("sfx", 1.0f);
        
        setDefaultBindings();
        JamScreen.loadBindings();
        
        crossPlatformWorker.create();
        skeletonRenderer = new SkeletonRenderer();
        skeletonRenderer.setPremultipliedAlpha(true);
        
        vfxManager = new VfxManager(Pixmap.Format.RGBA8888);
        
        sndChangeListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                assetManager.get("sfx/click.mp3", Sound.class).play();
            }
        };
        
        setScreen(new LoadScreen(() -> core.skin = core.assetManager.get("skin/shimmer-ui.json")));
        defaultTransition = Transitions.colorFade(Color.BLACK);
        defaultTransitionDuration = .5f;
    }
    
    @Override
    public void dispose() {
        vfxManager.dispose();
        assetManager.dispose();
        
        super.dispose();
    }
    
    @Override
    public void loadAssets() {
        assetManager.setLoader(SkeletonData.class, new SkeletonDataLoader(assetManager.getFileHandleResolver()));
        
        FileHandle fileHandle = Gdx.files.internal("skin.txt");
        if (fileHandle.exists()) for (String path : fileHandle.readString().split("\\n")) {
            assetManager.load(path, Skin.class);
        }
    
        fileHandle = Gdx.files.internal("bgm.txt");
        if (fileHandle.exists()) for (String path : fileHandle.readString().split("\\n")) {
            assetManager.load(path, Music.class);
        }
    
        fileHandle = Gdx.files.internal("sfx.txt");
        if (fileHandle.exists()) for (String path : fileHandle.readString().split("\\n")) {
            assetManager.load(path, Sound.class);
        }
    
        fileHandle = Gdx.files.internal("spine-atlas.txt");
        if (fileHandle.exists()) for (String path : fileHandle.readString().split("\\n")) {
            assetManager.load(path, TextureAtlas.class);
            fileHandle = Gdx.files.internal("spine.txt");
            if (fileHandle.exists()) for (String path2 : fileHandle.readString().split("\\n")) {
                assetManager.load(path2, SkeletonData.class, new SkeletonDataLoader.SkeletonDataLoaderParameter(path));
            }
            break;
        }
    
        fileHandle = Gdx.files.internal("spine-libgdx-atlas.txt");
        if (fileHandle.exists()) for (String path : fileHandle.readString().split("\\n")) {
            assetManager.load(path, TextureAtlas.class);
            fileHandle = Gdx.files.internal("spine-libgdx.txt");
            if (fileHandle.exists()) for (String path2 : fileHandle.readString().split("\\n")) {
                assetManager.load(path2, SkeletonData.class, new SkeletonDataLoader.SkeletonDataLoaderParameter(path));
            }
            break;
        }
    
        fileHandle = Gdx.files.internal("spine-ray3k-atlas.txt");
        if (fileHandle.exists()) for (String path : fileHandle.readString().split("\\n")) {
            assetManager.load(path, TextureAtlas.class);
            fileHandle = Gdx.files.internal("spine-ray3k.txt");
            if (fileHandle.exists()) for (String path2 : fileHandle.readString().split("\\n")) {
                assetManager.load(path2, SkeletonData.class, new SkeletonDataLoader.SkeletonDataLoaderParameter(path));
            }
            break;
        }
    }
    
    public void setDefaultBindings() {
        JamScreen.addKeyBinding(Binding.LEFT, Input.Keys.LEFT);
        JamScreen.addKeyBinding(Binding.RIGHT, Input.Keys.RIGHT);
        JamScreen.addKeyBinding(Binding.UP, Input.Keys.UP);
        JamScreen.addKeyBinding(Binding.DOWN, Input.Keys.DOWN);
        JamScreen.addKeyBinding(Binding.SHOOT, Input.Keys.Z);
        JamScreen.addKeyBinding(Binding.SHIELD, Input.Keys.X);
        JamScreen.addKeyBinding(Binding.SPECIAL, Input.Keys.C);
    }
}
