package com.ray3k.template;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.utils.TimeUtils;
import com.esotericsoftware.spine.utils.TwoColorPolygonBatch;
import com.ray3k.template.transitions.Transition;
import com.ray3k.template.transitions.TransitionEngine;

import static com.ray3k.template.transitions.Transitions.crossFade;

public abstract class JamGame extends Game {
    private final static long MS_PER_UPDATE = 10;
    private static final int MAX_VERTEX_SIZE = 32767;
    private long previous;
    private long lag;
    public AssetManager assetManager;
    public TransitionEngine transitionEngine;
    public TwoColorPolygonBatch batch;
    public Transition defaultTransition;
    public float defaultTransitionDuration;
    
    @Override
    public void create() {
        batch = new TwoColorPolygonBatch(MAX_VERTEX_SIZE);
        
        previous = TimeUtils.millis();
        lag = 0;
    
        assetManager = new AssetManager(new InternalFileHandleResolver());
        
        transitionEngine = new TransitionEngine(this, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        defaultTransition = crossFade();
        defaultTransitionDuration = .5f;
        
        loadAssets();
    }
    
    @Override
    public void render() {
        if (screen != null) {
            long current = TimeUtils.millis();
            long elapsed = current - previous;
            previous = current;
            lag += elapsed;
    
            while (lag >= MS_PER_UPDATE) {
                float delta = MS_PER_UPDATE / 1000.0f;
                
                if (!transitionEngine.inTransition) {
                    ((JamScreen) screen).updateMouse();
                    ((JamScreen) screen).act(delta);
                    ((JamScreen) screen).clearStates();
                } else {
                    transitionEngine.update(delta);
                }
                
                lag -= MS_PER_UPDATE;
            }
    
            if (transitionEngine.inTransition) {
                transitionEngine.draw(batch, lag / MS_PER_UPDATE);
            } else {
                ((JamScreen) screen).draw(lag / MS_PER_UPDATE);
            }
        }
    }
    
    @Override
    public void dispose() {
        super.dispose();
    
        batch.dispose();
        
        if (assetManager != null) {
            assetManager.dispose();
            assetManager = null;
        }
        
        transitionEngine.dispose();
    }
    
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        
        transitionEngine.resize(width, height);
    }
    
    public abstract void loadAssets();
    
    public void transition(JamScreen nextScreen, Transition transition, float duration) {
        transitionEngine.transition((JamScreen)getScreen(), nextScreen, transition, duration);
    }
    
    public void transition(JamScreen nextScreen) {
        transition(nextScreen, defaultTransition, defaultTransitionDuration);
    }
}
