package com.ray3k.template.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ray3k.template.Core;
import com.ray3k.template.JamScreen;

public class MenuScreen extends JamScreen {
    private Action gameAction;
    private Action optionsAction;
    private Action creditsAction;
    private Stage stage;
    private Skin skin;
    private Core core;
    private final static Color BG_COLOR = new Color(Color.BLACK);
    private Array<Actor> focusables;
    
    public MenuScreen(Action gameAction, Action optionsAction, Action creditsAction) {
        this.gameAction = gameAction;
        this.optionsAction = optionsAction;
        this.creditsAction = creditsAction;
        focusables = new Array<>();
    }
    
    @Override
    public void show() {
        core = Core.core;
        skin = core.skin;
    
        final Music bgm = core.assetManager.get("bgm/music-test.mp3");
        if (!bgm.isPlaying()) {
            bgm.play();
            bgm.setVolume(core.bgm);
            bgm.setLooping(true);
        }
        
        stage = new Stage(new ScreenViewport(), core.batch);
        Gdx.input.setInputProcessor(stage);
        
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                boolean shifting = Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT);
                switch (keycode) {
                    case Keys.TAB:
                        if (shifting) {
                            previous();
                        } else {
                            next();
                        }
                        break;
                    case Keys.RIGHT:
                    case Keys.D:
                    case Keys.DOWN:
                    case Keys.S:
                        next();
                        break;
                    case Keys.LEFT:
                    case Keys.A:
                    case Keys.UP:
                    case Keys.W:
                        previous();
                        break;
                    case Keys.SPACE:
                    case Keys.ENTER:
                        activate();
                }
                return super.keyDown(event, keycode);
            }
            
            public void next() {
                Actor focussed = stage.getKeyboardFocus();
                if (focussed == null) {
                    stage.setKeyboardFocus(focusables.first());
                } else {
                    int index = focusables.indexOf(focussed, true) + 1;
                    if (index >= focusables.size) index = 0;
                    stage.setKeyboardFocus(focusables.get(index));
                }
            }
            
            public void previous() {
                Actor focussed = stage.getKeyboardFocus();
                if (focussed == null) {
                    stage.setKeyboardFocus(focusables.first());
                } else {
                    int index = focusables.indexOf(focussed, true) - 1;
                    if (index < 0) index = focusables.size - 1;
                    stage.setKeyboardFocus(focusables.get(index));
                }
            }
            
            public void activate() {
                Actor focussed = stage.getKeyboardFocus();
                if (focussed != null) {
                    focussed.fire(new ChangeEvent());
                } else {
                    stage.setKeyboardFocus(focusables.first());
                }
            }
        });
        
        InputListener mouseEnterListener = new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                stage.setKeyboardFocus(null);
            }
        };
    
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
    
        final Image fg = new Image(skin, "white");
        fg.setColor(Color.BLACK);
        fg.setFillParent(true);
        fg.setTouchable(Touchable.disabled);
        stage.addActor(fg);
        fg.addAction(Actions.sequence(Actions.fadeOut(.3f)));
    
        Image image = new Image(skin, "libgdx-animation");
        image.setScaling(Scaling.none);
        root.add(image);
        
        root.row();
        Table table = new Table();
        root.add(table);
        
        table.pad(30);
        table.defaults().uniform().space(10);
        TextButton textButton = new TextButton("Play", skin);
        table.add(textButton);
        focusables.add(textButton);
        textButton.addListener(core.sndChangeListener);
        textButton.addListener(mouseEnterListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.input.setInputProcessor(null);
                fg.addAction(Actions.sequence(Actions.fadeIn(.3f), gameAction));
            }
        });
    
        textButton = new TextButton("Options", skin);
        table.add(textButton);
        focusables.add(textButton);
        textButton.addListener(core.sndChangeListener);
        textButton.addListener(mouseEnterListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.input.setInputProcessor(null);
                fg.addAction(Actions.sequence(Actions.fadeIn(.3f), optionsAction));
            }
        });
    
        textButton = new TextButton("Credits", skin);
        table.add(textButton);
        focusables.add(textButton);
        textButton.addListener(core.sndChangeListener);
        textButton.addListener(mouseEnterListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.input.setInputProcessor(null);
                fg.addAction(Actions.sequence(Actions.fadeIn(.3f), creditsAction));
            }
        });
        
        root.row();
        Label label = new Label("Copyright Raymond \"Raeleus\" Buckley © 2019", skin);
        root.add(label);
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
