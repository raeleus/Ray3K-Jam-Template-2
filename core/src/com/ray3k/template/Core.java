package com.ray3k.template;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.crashinvaders.vfx.VfxManager;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.utils.TwoColorPolygonBatch;
import com.ray3k.template.screens.*;

public class Core extends JamGame {
    private static final int MAX_VERTEX_SIZE = 32767;
    public static Core core;
    public TwoColorPolygonBatch batch;
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
    
    @Override
    public void create() {
        super.create();
    
        bgm = 1;
        sfx = 1;
        //todo: write code to load and write to preferences. Options will call method savePref() to update all settings.
        setDefaultBindings();
        
        crossPlatformWorker.create();
        core = this;
        skeletonRenderer = new SkeletonRenderer();
        skeletonRenderer.setPremultipliedAlpha(true);
        
        vfxManager = new VfxManager(Pixmap.Format.RGBA8888);
        
        batch = new TwoColorPolygonBatch(MAX_VERTEX_SIZE);
        sndChangeListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                assetManager.get("sfx/click.mp3", Sound.class).play();
            }
        };
    
        setScreen(createLoadScreen());
    }
    
    @Override
    public void dispose() {
        batch.dispose();
        
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
    
    private Screen createLoadScreen() {
        return new LoadScreen(Actions.run(() -> {
            skin = assetManager.get("skin/shimmer-ui.json");
            setScreen(createSplashScreen());
        }));
    }
    
    private Screen createSplashScreen() {
        return new SplashScreen(Actions.run(() -> setScreen(createLibgdxScreen())));
    }
    
    private Screen createLibgdxScreen() {
        return new LibgdxScreen(Actions.run(() -> setScreen(createLogoScreen())));
    }
    
    private Screen createLogoScreen() {
        return new LogoScreen(Actions.run(() -> setScreen(createMenuScreen())));
    }
    
    private Screen createMenuScreen() {
        return new MenuScreen(Actions.run(() -> setScreen(createGameScreen())),
                Actions.run(() -> setScreen(createOptionsScreen())),
                Actions.run(() -> setScreen(createCreditsScreen())));
    }
    
    private Screen createGameScreen() {
        return new GameScreen(Actions.run(() -> setScreen(createCreditsScreen())));
    }
    
    private Screen createOptionsScreen() {
        return new OptionsScreen(Actions.run(() -> setScreen(createMenuScreen())));
    }
    
    private Screen createCreditsScreen() {
        return new CreditsScreen(Actions.run(() -> setScreen(createMenuScreen())));
    }
}
