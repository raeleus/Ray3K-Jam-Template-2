package com.ray3k.template.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.ray3k.template.Core;
import com.ray3k.template.JamScreen;
import com.ray3k.template.Utils;

public class DialogEditKeyBindings extends Dialog {
    private Core core;
    private Skin skin;
    
    public DialogEditKeyBindings() {
        super("", Core.core.skin);
        
        core = Core.core;
        skin = core.skin;
        
        setFillParent(true);
        Table root = getContentTable();
    
        Table table = new Table();
        ScrollPane scrollPane = new ScrollPane(table, skin);
        root.add(scrollPane).grow();
        
        refreshTable(table);
        
        getButtonTable().pad(5);
        getButtonTable().defaults().uniform().fill().space(10);
        TextButton textButton = new TextButton("OK", skin);
        button(textButton);
        
        textButton = new TextButton("Defaults", skin);
        getButtonTable().add(textButton);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                core.setDefaultBindings();
                refreshTable(table);
            }
        });
    }
    
    private void refreshTable(Table table) {
        table.clear();
        
        table.defaults().space(10).uniform().fill();
        for (Core.Binding binding : JamScreen.getBindings()) {
            String codeName = JamScreen.hasKeyBinding(binding) ? Input.Keys.toString(JamScreen.getBinding(binding)) : Utils.MouseButtonToString(JamScreen.getBinding(binding));
            TextButton textButton = new TextButton(binding.toString() + " : " + codeName, skin);
            table.add(textButton);
            table.row();
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    DialogKeyBinding dialog = new DialogKeyBinding(binding);
                    dialog.addListener(new BindingListener() {
                        @Override
                        public void keySelected(int key) {
                            JamScreen.addKeyBinding(binding, key);
                            refreshTable(table);
                        }
                    
                        @Override
                        public void buttonSelected(int button) {
                            JamScreen.addButtonBinding(binding, button);
                            refreshTable(table);
                        }
                    });
                    dialog.show(getStage());
                }
            });
        }
    }
    
    @Override
    protected void result(Object object) {
    
    }
    
    private static class DialogKeyBinding extends Dialog {
        
        public DialogKeyBinding(Core.Binding binding) {
            super("", Core.core.skin);
            
            setFillParent(true);
            Table root = getContentTable();
            
            text("Press any key or mouse button to set");
            root.row();
            text(binding.toString());
            root.row();
            text("(Press ESCAPE to cancel)");
            root.row();
            text("...");
            
            addListener(new InputListener() {
                @Override
                public boolean keyDown(InputEvent event, int keycode) {
                    fire(new KeyBindingEvent(keycode));
                    hide();
                    return true;
                }
    
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    fire(new ButtonBindingEvent(button));
                    hide();
                    return true;
                }
            });
        }
    
        @Override
        protected void result(Object object) {
        
        }
    }
    
    private static class KeyBindingEvent extends Event {
        private int key;
    
        public KeyBindingEvent(int key) {
            this.key = key;
        }
    }
    
    private static class ButtonBindingEvent extends Event {
        private int button;
    
        public ButtonBindingEvent(int button) {
            this.button = button;
        }
    }
    
    private static abstract class BindingListener implements EventListener {
        @Override
        public boolean handle(Event event) {
            if (event instanceof KeyBindingEvent ) {
                keySelected(((KeyBindingEvent) event).key);
                return true;
            } else if (event instanceof ButtonBindingEvent) {
                buttonSelected(((ButtonBindingEvent) event).button);
                return true;
            } else {
                return false;
            }
        }
        
        public abstract void keySelected(int key);
        public abstract void buttonSelected(int button);
    }
}
