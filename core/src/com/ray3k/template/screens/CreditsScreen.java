package com.ray3k.template.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.rafaskoberg.gdx.typinglabel.TypingConfig;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;
import com.ray3k.template.Core;
import com.ray3k.template.JamScreen;

public class CreditsScreen extends JamScreen {
    private Action action;
    private Stage stage;
    private Skin skin;
    private Core core;
    private final static Color BG_COLOR = new Color(Color.BLACK);
    
    public CreditsScreen(Action action) {
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
