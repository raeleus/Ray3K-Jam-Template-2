package com.ray3k.template;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.utils.TimeUtils;

public abstract class JamGame extends Game {
    public final static long MS_PER_UPDATE = 10;
    private long previous;
    private long lag;
    public AssetManager assetManager;
    
    @Override
    public void create() {
        previous = TimeUtils.millis();
        lag = 0;
    
        assetManager = new AssetManager(new InternalFileHandleResolver());
        loadAssets();
    }
    
    @Override
    public void render() {
        if (screen != null && screen instanceof JamScreen) {
            JamScreen jamScreen = (JamScreen) screen;
            long current = TimeUtils.millis();
            long elapsed = current - previous;
            previous = current;
            lag += elapsed;
    
            while (lag >= MS_PER_UPDATE) {
                float delta = MS_PER_UPDATE / 1000.0f;
                jamScreen.updateMouse();
                jamScreen.act(delta);
                jamScreen.clearStates();
                lag -= MS_PER_UPDATE;
            }
    
            jamScreen.draw(lag / MS_PER_UPDATE);
        }
    }
    
    @Override
    public void dispose() {
        super.dispose();
    
        if (assetManager != null) {
            assetManager.dispose();
            assetManager = null;
        }
    }
    
    public abstract void loadAssets();
}
