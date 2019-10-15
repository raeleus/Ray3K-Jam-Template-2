package com.ray3k.template.entities;

import com.badlogic.gdx.graphics.Color;
import com.crashinvaders.vfx.VfxManager;
import com.ray3k.template.Core;

public class VfxStartEntity extends Entity {
    private Core core;
    private VfxManager vfx;
    private final Color clearColor = new Color();
    
    public VfxStartEntity(Color clearColor) {
        clearColor.set(clearColor);
    }
    
    @Override
    public void create() {
        core = Core.core;
        vfx = core.vfxManager;
    }
    
    @Override
    public void actBefore(float delta) {
    
    }
    
    @Override
    public void act(float delta) {
    
    }
    
    @Override
    public void draw(float delta) {
        core.batch.end();
        vfx.cleanUpBuffers(clearColor);
        vfx.beginCapture();
        core.batch.begin();
    }
    
    @Override
    public void destroy() {
    
    }
}
