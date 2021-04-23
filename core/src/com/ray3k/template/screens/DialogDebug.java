package com.ray3k.template.screens;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.ray3k.template.*;

public class DialogDebug extends Dialog {
    public DialogDebug() {
        super("", Core.skin);
        setFillParent(true);
        button("close").key(Keys.ESCAPE, null).key(Keys.ENTER, null).key(Keys.F10, null);
        
        var table = Core.crossPlatformWorker.generateDebugTable();
        if (table != null) {
            var scrollPane = new ScrollPane(table, Core.skin);
            scrollPane.setName("scrollPane");
            scrollPane.setFadeScrollBars(false);
            getContentTable().add(scrollPane);
        }
    }
    
    @Override
    public Dialog show(Stage stage, Action action) {
        super.show(stage, action);
        stage.setScrollFocus(getContentTable().findActor("scrollPane"));
        return this;
    }
    
    public static class DebugListener extends InputListener {
        DialogDebug dialog;
        @Override
        public boolean keyDown(InputEvent event, int keycode) {
            if (keycode == Keys.F10) {
                if (dialog == null) {
                    dialog = new DialogDebug();
                }
                if (dialog.getParent() == null) dialog.show(event.getStage());
                return true;
            }
            return super.keyDown(event, keycode);
        }
    }
}
