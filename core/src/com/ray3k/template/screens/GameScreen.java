package com.ray3k.template.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.EarthquakeEffect;
import com.ray3k.template.Core;
import com.ray3k.template.Core.Binding;
import com.ray3k.template.JamScreen;
import com.ray3k.template.entities.BallTestEntity;
import com.ray3k.template.entities.EntityController;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class GameScreen extends JamScreen {
    public static GameScreen gameScreen;
    public static final Color BG_COLOR = new Color();
    private Core core;
    public AssetManager assetManager;
    private Batch batch;
    public Stage stage;
    public Skin skin;
    public ShapeDrawer shapeDrawer;
    public EntityController entityController;
    private VfxManager vfxManager;
    private EarthquakeEffect vfxEffect;
    
    public GameScreen() {
        gameScreen = this;
        core = Core.core;
        assetManager = core.assetManager;
        batch = core.batch;
        vfxManager = core.vfxManager;
        vfxEffect = new EarthquakeEffect();
        
        BG_COLOR.set(Color.PINK);
        
        stage = new Stage(new ScreenViewport(), core.batch);
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Keys.ESCAPE) {
                    core.transition(new MenuScreen());
                }
                return super.keyDown(event, keycode);
            }
        });
        
        skin = assetManager.get("skin/shimmer-ui.json");
        shapeDrawer = new ShapeDrawer(core.batch, skin.getRegion("white"));
        shapeDrawer.setPixelSize(.5f);
        
        InputMultiplexer inputMultiplexer = new InputMultiplexer(stage, this);
        Gdx.input.setInputProcessor(inputMultiplexer);
        
        camera = new OrthographicCamera();
        viewport = new FitViewport(1024, 576, camera);
        
        entityController = new EntityController();
        BallTestEntity ballTestEntity = new BallTestEntity();
        ballTestEntity.moveCamera = true;
        entityController.add(ballTestEntity);
        
        for (int i = 0; i < 10; i++) {
            ballTestEntity = new BallTestEntity();
            ballTestEntity.setPosition(MathUtils.random(viewport.getWorldWidth()), MathUtils.random(viewport.getWorldHeight()));
            entityController.add(ballTestEntity);
        }
        
        vfxManager.addEffect(vfxEffect);
    }
    
    @Override
    public void act(float delta) {
        entityController.act(delta);
        stage.act(delta);
        vfxEffect.update(delta);
    }
    
    @Override
    public void draw(float delta) {
        Gdx.gl.glClearColor(BG_COLOR.r, BG_COLOR.g, BG_COLOR.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        vfxManager.cleanUpBuffers();
        vfxManager.beginCapture();
        batch.begin();
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        shapeDrawer.setColor(isBindingPressed(Binding.LEFT) ? Color.ORANGE : Color.GREEN);
        shapeDrawer.filledRectangle(0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        shapeDrawer.setColor(Color.BLUE);
        shapeDrawer.setDefaultLineWidth(10);
        shapeDrawer.rectangle(0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        entityController.draw(delta);
        batch.end();
        vfxManager.endCapture();
        vfxManager.applyEffects();
        vfxManager.renderToScreen();
        stage.draw();
    }
    
    @Override
    public void resize(int width, int height) {
        vfxManager.resize(width, height);
        viewport.update(width, height);
        
        stage.getViewport().update(width, height, true);
    }
    
    @Override
    public void dispose() {
        vfxEffect.dispose();
    }
    
    @Override
    public void hide() {
        super.hide();
        vfxEffect.dispose();
    }
}
