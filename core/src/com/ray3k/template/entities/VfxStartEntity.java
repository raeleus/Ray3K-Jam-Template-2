package com.ray3k.template.entities;

import com.badlogic.gdx.graphics.Color;

import static com.ray3k.template.Core.*;

public class VfxStartEntity extends Entity {
    private final Color clearColor = new Color();
    
    public VfxStartEntity(Color clearColor) {
        clearColor.set(clearColor);
    }
    
    @Override
    public void create() {
    }
    
    @Override
    public void actBefore(float delta) {
    
    }
    
    @Override
    public void act(float delta) {
    
    }
    
    @Override
    public void draw(float delta) {
        batch.end();
        vfxManager.cleanUpBuffers(clearColor);
        vfxManager.beginCapture();
        batch.begin();
    }
    
    @Override
    public void destroy() {
    
    }
}
