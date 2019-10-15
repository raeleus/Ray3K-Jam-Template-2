package com.ray3k.template.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.crashinvaders.vfx.VfxManager;
import com.ray3k.template.Core;

public class VfxEndEntity extends Entity {
    private Core core;
    private VfxManager vfx;
    
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
        vfx.endCapture();
        vfx.applyEffects();
        vfx.renderToScreen();
        core.batch.begin();
    }
    
    @Override
    public void destroy() {
    
    }
}
