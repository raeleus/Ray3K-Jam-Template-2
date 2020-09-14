package com.ray3k.template.entities;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dongbat.jbump.Rect;
import com.ray3k.template.*;
import com.ray3k.template.screens.*;
import space.earlygrey.shapedrawer.ShapeDrawer;

import static com.ray3k.template.Core.*;

public class BallTestEntity extends Entity {
    private GameScreen gameScreen;
    private Viewport viewport;
    private OrthographicCamera camera;
    private ShapeDrawer shapeDrawer;
    private final Color color = new Color();
    public boolean moveCamera;
    
    @Override
    public void create() {
        gameScreen = GameScreen.gameScreen;
        viewport = gameScreen.viewport;
        camera = gameScreen.camera;
        shapeDrawer = gameScreen.shapeDrawer;
        
        color.set(Color.RED);
        
        setMotion(100, MathUtils.random(360f));
        setCollisionBox(-50, -50, 100, 100, defaultCollisionFilter);
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
    }
    
    @Override
    public void draw(float delta) {
        shapeDrawer.setColor(color);
        shapeDrawer.filledCircle(x, y, 50);
        shapeDrawer.setDefaultLineWidth(1f);
        Rect rect = world.getRect(item);
        shapeDrawer.rectangle(rect.x, rect.y, rect.w, rect.h);
    }
    
    @Override
    public void destroy() {
    
    }
}
