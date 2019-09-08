package com.ray3k.template;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
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
    public Sound sndClick;
    public ChangeListener sndChangeListener;
    public VfxManager vfxManager;
    
    @Override
    public void create() {
        super.create();
        core = this;
        skeletonRenderer = new SkeletonRenderer();
        skeletonRenderer.setPremultipliedAlpha(true);
        
        vfxManager = new VfxManager(Pixmap.Format.RGBA8888);
        
        batch = new TwoColorPolygonBatch(MAX_VERTEX_SIZE);
        sndChangeListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                core.sndClick.play();
            }
        };
    
        setScreen(createLoadScreen());
    }
    
    @Override
    public void dispose() {
        batch.dispose();
        
        vfxManager.dispose();
        
        super.dispose();
    }
    
    @Override
    public void loadAssets() {
        assetManager.load("shimmer-ui/shimmer-ui.json", Skin.class);
    
        assetManager.setLoader(SkeletonData.class, new SkeletonDataLoader(assetManager.getFileHandleResolver()));
        SkeletonDataLoader.SkeletonDataLoaderParameter parameter = new SkeletonDataLoader.SkeletonDataLoaderParameter("libgdx-logo/libgdx.atlas");
        assetManager.load("libgdx-logo/libgdx.json", SkeletonData.class, parameter);
        
        assetManager.load("libgdx-logo/ahh.mp3", Sound.class);
        assetManager.load("libgdx-logo/libgdx.mp3", Sound.class);
        assetManager.load("libgdx-logo/please don't kill me.mp3", Sound.class);
        assetManager.load("libgdx-logo/shot.mp3", Sound.class);
    
        assetManager.setLoader(SkeletonData.class, new SkeletonDataLoader(assetManager.getFileHandleResolver()));
        parameter = new SkeletonDataLoader.SkeletonDataLoaderParameter("ray3k-logo/ray3k.atlas");
        assetManager.load("ray3k-logo/ray3k.json", SkeletonData.class, parameter);
        
        assetManager.load("ray3k-logo/swoosh.mp3", Sound.class);
        assetManager.load("ray3k-logo/tv.mp3", Sound.class);
        
        assetManager.load("sfx/click.mp3", Sound.class);
        
        assetManager.load("sfx/audio-test.mp3", Music.class);
        assetManager.load("bgm/music-test.mp3", Music.class);
    }
    
    private Screen createLoadScreen() {
        return new LoadScreen(Actions.run(() -> {
            skin = assetManager.get("shimmer-ui/shimmer-ui.json");
            sndClick = assetManager.get("sfx/click.mp3");
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
