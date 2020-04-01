package com.ray3k.template.entities;

import static com.ray3k.template.Core.*;

public class VfxEndEntity extends Entity {
    
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
        vfxManager.endCapture();
        vfxManager.applyEffects();
        vfxManager.renderToScreen();
        batch.begin();
    }
    
    @Override
    public void destroy() {
    
    }
}
