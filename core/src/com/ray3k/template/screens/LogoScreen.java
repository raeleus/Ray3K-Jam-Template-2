package com.ray3k.template.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.spine.*;
import com.esotericsoftware.spine.utils.SkeletonDrawable;
import com.ray3k.template.Core;
import com.ray3k.template.JamScreen;

public class LogoScreen extends JamScreen {
    private Action action;
    private Stage stage;
    private Skin skin;
    private Core core;
    private AssetManager assetManager;
    private Array<SkeletonDrawable> skeletonDrawables;
    private final static Color BG_COLOR = new Color(Color.BLACK);
    
    public LogoScreen(Action action) {
        this.action = action;
    }
    
    @Override
    public void show() {
        core = Core.core;
        skin = core.skin;
        assetManager = core.assetManager;
        skeletonDrawables = new Array<>();
        
        SkeletonData skeletonData = assetManager.get("spine-ray3k/ray3k.json");
        SkeletonDrawable skeletonDrawable = new SkeletonDrawable(core.skeletonRenderer, new Skeleton(skeletonData), new AnimationState(new AnimationStateData(skeletonData)));
        skeletonDrawable.setMinWidth(525);
        skeletonDrawable.setMinHeight(150);
        skeletonDrawable.getAnimationState().setAnimation(0, "stand", false);
        skeletonDrawables.add(skeletonDrawable);
        
        final Action completeAction = Actions.sequence(Actions.run(() -> {
            skeletonDrawable.getAnimationState().setTimeScale(0f);
            Gdx.input.setInputProcessor(null);
        }), Actions.color(new Color(BG_COLOR.r, BG_COLOR.g, BG_COLOR.b, 0)), Actions.fadeIn(.3f), action);
        
        stage = new Stage(new ScreenViewport(), core.batch);
        Gdx.input.setInputProcessor(stage);
        
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
        
        Image image = new Image(skeletonDrawable);
        image.setScaling(Scaling.none);
        root.add(image);
        
        final Image fg = new Image(skin, "white");
        fg.setColor(Color.WHITE);
        fg.setFillParent(true);
        stage.addActor(fg);
        fg.addAction(Actions.sequence(Actions.fadeOut(.3f), Actions.run(() -> skeletonDrawable.getAnimationState().setAnimation(0, "animation", false))));
        
        skeletonDrawable.getAnimationState().addListener(new AnimationState.AnimationStateAdapter() {
            @Override
            public void complete(AnimationState.TrackEntry entry) {
                if (entry.getAnimation().getName().equals("animation")) {
                    fg.addAction(completeAction);
                }
            }
            
            @Override
            public void event(AnimationState.TrackEntry entry, Event event) {
                if (event.getData().getAudioPath() != null && !event.getData().getAudioPath().equals("")) {
                    Sound sound = core.assetManager.get("sfx/" + event.getData().getAudioPath());
                    sound.play();
                }
            }
        });
        
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                fg.addAction(completeAction);
                return true;
            }
            
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                fg.addAction(completeAction);
                return true;
            }
        });
    }
    
    @Override
    public void act(float delta) {
        stage.act(delta);
        
        for (SkeletonDrawable skeletonDrawable : skeletonDrawables) {
            skeletonDrawable.update(delta);
        }
    }
    
    @Override
    public void draw(float delta) {
        Gdx.gl.glClearColor(BG_COLOR.r, BG_COLOR.g, BG_COLOR.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        stage.draw();
    }
    
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
}
