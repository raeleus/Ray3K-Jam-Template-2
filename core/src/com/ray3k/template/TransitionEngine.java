package com.ray3k.template;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;

public class TransitionEngine implements Disposable {
    public VfxFrameBuffer frameBuffer;
    public VfxFrameBuffer nextFrameBuffer;
    public boolean inTransition;
    public float time;
    public JamGame jamGame;
    public JamScreen screen;
    public JamScreen nextScreen;
    public Transition transition;
    public float duration;
    public TextureRegion textureRegion;
    public enum Transition {
        CROSS_FADE, COLOR_FADE, BLINDS, WIPE, PUSH, DISSOLVE, CLOCK, ZOOM, FLY_THROUGH
    }
    
    public TransitionEngine(JamGame jamGame, int width, int height) {
        this.jamGame = jamGame;
        frameBuffer = new VfxFrameBuffer(Format.RGBA8888);
        nextFrameBuffer = new VfxFrameBuffer(Format.RGBA8888);
        textureRegion = new TextureRegion();
        resize(width, height);
    }
    
    public void resize(int width, int height) {
        frameBuffer.initialize(width, height);
        nextFrameBuffer.initialize(width, height);
    }
    
    public void transition(JamScreen screen, JamScreen nextScreen, Transition transition, float duration) {
        inTransition = true;
        time = 0;
        
        this.screen = screen;
        this.nextScreen = nextScreen;
        this.transition = transition;
        this.duration = duration;
        
        switch (transition) {
            case CROSS_FADE:
                frameBuffer.begin();
                screen.draw(0);
                frameBuffer.end();
    
                jamGame.setScreen(nextScreen);
                nextFrameBuffer.begin();
                nextScreen.draw(0);
                nextFrameBuffer.end();
                break;
            case BLINDS:
                break;
            case PUSH:
                break;
            case CLOCK:
                break;
            case COLOR_FADE:
                break;
            case DISSOLVE:
                break;
            case FLY_THROUGH:
                break;
            case WIPE:
                break;
            case ZOOM:
                break;
        }
    }
    
    public void update(float delta) {
        if (inTransition) {
            time += delta;
    
            if (time >= duration) {
                inTransition = false;
            } else {
                switch (transition) {
                    case CROSS_FADE:
                        break;
                    case BLINDS:
                        break;
                    case PUSH:
                        break;
                    case CLOCK:
                        break;
                    case COLOR_FADE:
                        break;
                    case DISSOLVE:
                        break;
                    case FLY_THROUGH:
                        break;
                    case WIPE:
                        break;
                    case ZOOM:
                        break;
                }
            }
        }
    }
    
    public void draw(Batch batch, float delta) {
        batch.begin();
    
        switch (transition) {
            case CROSS_FADE:
                Gdx.gl.glClearColor(0, 0, 0, 1);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                
                textureRegion.setRegion(new TextureRegion(nextFrameBuffer.getFbo().getColorBufferTexture()));
                textureRegion.flip(false, true);
    
                batch.setColor(1, 1, 1, 1f);
                batch.draw(textureRegion, 0, 0);
                
                textureRegion.setRegion(new TextureRegion(frameBuffer.getFbo().getColorBufferTexture()));
                textureRegion.flip(false, true);
    
                batch.setColor(1, 1, 1, (duration - time) / duration);
                batch.draw(textureRegion, 0, 0);
                
                break;
            case BLINDS:
                break;
            case PUSH:
                break;
            case CLOCK:
                break;
            case COLOR_FADE:
                break;
            case DISSOLVE:
                break;
            case FLY_THROUGH:
                break;
            case WIPE:
                break;
            case ZOOM:
                break;
        }
    
        batch.end();
    }
    
    public void dispose() {
        frameBuffer.dispose();
        nextFrameBuffer.dispose();
    }
}
