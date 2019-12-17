package com.ray3k.template.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ray3k.template.Core;
import com.ray3k.template.Utils;
import com.ray3k.template.screens.GameScreen;
import com.talosvfx.talos.runtime.ParticleEffectDescriptor;
import com.talosvfx.talos.runtime.ParticleEffectInstance;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class BallTestEntity extends Entity {
    private Core core;
    private GameScreen gameScreen;
    private Viewport viewport;
    private OrthographicCamera camera;
    private ShapeDrawer shapeDrawer;
    private final Color color = new Color();
    public boolean moveCamera;
    private ParticleEffectInstance effect;
    private static ParticleEffectDescriptor effectDescriptor;
    
    @Override
    public void create() {
        core = Core.core;
        gameScreen = GameScreen.gameScreen;
        viewport = gameScreen.viewport;
        camera = gameScreen.camera;
        shapeDrawer = gameScreen.shapeDrawer;
        
        color.set(Color.RED);
        
        setMotion(100, MathUtils.random(360f));
        if (effectDescriptor == null) {
            effectDescriptor = new ParticleEffectDescriptor(Gdx.files.internal("talos/test.p"), core.textureAtlas);
        }
        effect = effectDescriptor.createEffectInstance();
    }
    
    @Override
    public void actBefore(float delta) {
    
    }
    
    @Override
    public void act(float delta) {
        if (x < 0) {
            x = 0;
            deltaX *= -1;
        } else if (x > viewport.getWorldWidth()) {
            x = viewport.getWorldWidth();
            deltaX *= -1;
        }
        
        if (y < 0) {
            y = 0;
            deltaY *= - 1;
        } else if (y > viewport.getWorldHeight()) {
            y = viewport.getWorldHeight();
            deltaY *= - 1;
        }
        
        if (moveCamera) {
            camera.position.x = x;
            camera.position.y = y;
        }
        
        if (gameScreen.isButtonPressed(Input.Buttons.LEFT)) {
            if (Utils.pointDistance(x, y, gameScreen.mouseX, gameScreen.mouseY) <= 50) {
                color.set(Color.BLUE);
            } else {
                color.set(Color.YELLOW);
            }
        } else {
            color.set(Color.RED);
        }
        
        effect.update(delta);
        effect.setPosition(x, y);
        effect.loopable = true;
    }
    
    @Override
    public void draw(float delta) {
        shapeDrawer.setColor(color);
        shapeDrawer.filledCircle(x, y, 50);
        effect.render(core.particleRenderer);
    }
    
    @Override
    public void destroy() {
    
    }
}
