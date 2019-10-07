package com.ray3k.template.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ray3k.template.Core;
import com.ray3k.template.JamScreen;

public class OptionsScreen extends JamScreen {
    private Action action;
    private Stage stage;
    private Skin skin;
    private Core core;
    private final static Color BG_COLOR = new Color(Color.BLACK);
    
    public OptionsScreen(Action action) {
        this.action = action;
    }
    
    @Override
    public void show() {
        core = Core.core;
        skin = core.skin;
    
        stage = new Stage(new ScreenViewport(), core.batch);
        Gdx.input.setInputProcessor(stage);

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        final Image fg = new Image(skin, "white");
        fg.setColor(Color.BLACK);
        fg.setFillParent(true);
        fg.setTouchable(Touchable.disabled);
        stage.addActor(fg);
        fg.addAction(Actions.sequence(Actions.fadeOut(.3f)));

        root.defaults().space(30);
        Label label = new Label("Options", skin);
        root.add(label);

        root.row();
        Table table = new Table();
        root.add(table);

        table.defaults().space(3);
        label = new Label("BGM", skin);
        table.add(label).right();

        final Music bgm = core.assetManager.get("bgm/music-test.mp3");
        bgm.setLooping(true);
        
        Slider slider = new Slider(0, 1, .01f, false, skin);
        table.add(slider);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                bgm.setVolume(((Slider) actor).getValue());
            }
        });
        slider.addListener(new DragListener() {
            {
                setTapSquareSize(0);
            }
    
            @Override
            public void dragStart(InputEvent event, float x, float y, int pointer) {
                bgm.play();
            }
    
            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer) {
                bgm.pause();
            }
        });

        table.row();
        label = new Label("SFX", skin);
        table.add(label).right();
    
        final Music sfx = core.assetManager.get("bgm/audio-test.mp3");
        sfx.setLooping(true);
        
        slider = new Slider(0, 1, .01f, false, skin);
        table.add(slider);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sfx.setVolume(((Slider) actor).getValue());
            }
        });
        slider.addListener(new DragListener() {
            {
                setTapSquareSize(0);
            }
        
            @Override
            public void dragStart(InputEvent event, float x, float y, int pointer) {
                sfx.play();
            }
        
            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer) {
                sfx.pause();
            }
        });
        
        root.row();
        TextButton textButton = new TextButton("Edit Key Bindings", skin);
        root.add(textButton);
        textButton.addListener(core.sndChangeListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                DialogEditKeyBindings dialog = new DialogEditKeyBindings();
                dialog.show(stage);
            }
        });

        root.row();
        textButton = new TextButton("OK", skin);
        root.add(textButton);
        textButton.addListener(core.sndChangeListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.input.setInputProcessor(null);
                fg.addAction(Actions.sequence(Actions.fadeIn(.3f), action));
            }
        });
    }
    
    @Override
    public void act(float delta) {
        stage.act(delta);
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
