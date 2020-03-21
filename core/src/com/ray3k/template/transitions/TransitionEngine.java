package com.ray3k.template.transitions;

import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.ray3k.template.JamGame;
import com.ray3k.template.JamScreen;

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
    
    public TransitionEngine(JamGame jamGame, int width, int height) {
        this.jamGame = jamGame;
        frameBuffer = new VfxFrameBuffer(Format.RGB888);
        nextFrameBuffer = new VfxFrameBuffer(Format.RGB888);
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
        
        transition.create();
    }
    
    public void update(float delta) {
        if (inTransition) {
            time += delta;
    
            if (time >= duration) {
                inTransition = false;
                transition.end();
            } else {
                transition.act();
            }
        }
    }
    
    public void draw(Batch batch, float delta) {
        batch.begin();
    
        transition.draw(batch, delta);
    
        batch.end();
    }
    
    public void dispose() {
        frameBuffer.dispose();
        nextFrameBuffer.dispose();
    }
}
