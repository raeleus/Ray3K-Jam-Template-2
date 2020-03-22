package com.ray3k.template.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.rafaskoberg.gdx.typinglabel.TypingConfig;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;
import com.ray3k.template.Core;
import com.ray3k.template.JamScreen;

public class CreditsScreen extends JamScreen {
    private Stage stage;
    private Skin skin;
    private Core core;
    private final static Color BG_COLOR = new Color(Color.BLACK);
    private Array<Actor> focusables;
    
    public CreditsScreen() {
        focusables = new Array<>();
    }
    
    @Override
    public void show() {
        super.show();
        
        core = Core.core;
        skin = core.skin;
        
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
        
        root.defaults().space(30);
        TypingConfig.INTERVAL_MULTIPLIERS_BY_CHAR.put('\n', .5f);
        TypingLabel typingLabel = new TypingLabel("This game was made by Raeleus.\n" +
                "Copyright Raymond \"Raeleus\" Buckley © 2019\n\n" +
                "Music by Devynn LaShure, Echo Blue Music\n" +
                "https://www.echobluemusic.com", skin);
        typingLabel.setAlignment(Align.center);
        root.add(typingLabel);
        
        root.row();
        TextButton textButton = new TextButton("OK", skin);
        root.add(textButton);
        focusables.add(textButton);
        textButton.addListener(core.sndChangeListener);
        textButton.addListener(mouseEnterListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.input.setInputProcessor(null);
                core.transition(new MenuScreen());
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
