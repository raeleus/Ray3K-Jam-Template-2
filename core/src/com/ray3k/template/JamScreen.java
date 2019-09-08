package com.ray3k.template;

import com.badlogic.gdx.ScreenAdapter;

public abstract class JamScreen extends ScreenAdapter {
    @Override @Deprecated
    public void render(float delta) {
    
    }
    
    public abstract void act(float delta);
    
    public abstract void draw(float delta);
}
