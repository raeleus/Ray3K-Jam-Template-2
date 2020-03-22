package com.ray3k.template.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectIntMap.Entry;
import com.ray3k.template.Core;
import com.ray3k.template.Core.Binding;
import com.ray3k.template.JamScreen;
import com.ray3k.template.Utils;

public class DialogEditKeyBindings extends Dialog {
    private Core core;
    private Skin skin;
    private Array<Actor> focusables;
    private InputListener keysListener;
    private InputListener mouseEnterListener;
    
    public DialogEditKeyBindings(Stage stage) {
        super("", Core.core.skin);
        setStage(stage);
        
        core = Core.core;
        skin = core.skin;
    
        focusables = new Array<>();
    
        keysListener = new InputListener() {
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
                Actor focussed = getStage().getKeyboardFocus();
                if (focussed == null) {
                    getStage().setKeyboardFocus(focusables.first());
                } else {
                    int index = focusables.indexOf(focussed, true) + 1;
                    if (index >= focusables.size) index = 0;
                    getStage().setKeyboardFocus(focusables.get(index));
                }
            }
        
            public void previous() {
                Actor focussed = getStage().getKeyboardFocus();
                if (focussed == null) {
                    getStage().setKeyboardFocus(focusables.first());
                } else {
                    int index = focusables.indexOf(focussed, true) - 1;
                    if (index < 0) index = focusables.size - 1;
                    getStage().setKeyboardFocus(focusables.get(index));
                }
            }
        
            public void activate() {
                Actor focussed = getStage().getKeyboardFocus();
                if (focussed != null) {
                    focussed.fire(new ChangeEvent());
                } else {
                    getStage().setKeyboardFocus(focusables.first());
                }
            }
        };
    
        getStage().addListener(keysListener);
    
        mouseEnterListener = new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                getStage().setKeyboardFocus(null);
            }
        };
        
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
        focusables.add(textButton);
        textButton.addListener(mouseEnterListener);
        
        textButton = new TextButton("Defaults", skin);
        getButtonTable().add(textButton);
        focusables.add(textButton);
        textButton.addListener(mouseEnterListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                core.setDefaultBindings();
                JamScreen.saveBindings();
                refreshTable(table);
            }
        });
    
    }
    
    private void refreshTable(Table table) {
        table.clear();
        focusables.clear();
        if (getStage() != null) getStage().setKeyboardFocus(null);
        
        table.defaults().space(10).uniform().fill();
        for (Core.Binding binding : JamScreen.getBindings()) {
            String codeName;
            if (JamScreen.hasKeyBinding(binding)) {
                codeName = Input.Keys.toString(JamScreen.getBinding(binding));
            } else if (JamScreen.hasButtonBinding(binding)) {
                codeName = Utils.mouseButtonToString(JamScreen.getBinding(binding));
            } else if (JamScreen.hasScrollBinding(binding)) {
                codeName = Utils.scrollAmountToString(JamScreen.getBinding(binding));
            } else if (JamScreen.hasControllerButtonBinding(binding)) {
                codeName = Utils.controllerButtonToString(JamScreen.getBinding(binding));
            }
            
            else {
                codeName = "Unbound";
            }
            
            TextButton textButton = new TextButton(binding.toString() + " : " + codeName, skin);
            table.add(textButton);
            table.row();
            focusables.add(textButton);
            textButton.addListener(mouseEnterListener);
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    DialogKeyBinding dialog = new DialogKeyBinding(binding);
                    dialog.addListener(new BindingListener() {
                        @Override
                        public void keySelected(int key) {
                            Array<Binding> unbinds = new Array<>();
                            for (Entry<Binding> binding : JamScreen.keyBindings) {
                                if (binding.value == key) {
                                    unbinds.add(binding.key);
                                }
                            }
                            for (Binding binding : unbinds) {
                                JamScreen.addUnboundBinding(binding);
                            }
                            
                            JamScreen.addKeyBinding(binding, key);
                            JamScreen.saveBindings();
                            refreshTable(table);
                        }
    
                        @Override
                        public void buttonSelected(int button) {
                            Array<Binding> unbinds = new Array<>();
                            for (Entry<Binding> binding : JamScreen.buttonBindings) {
                                if (binding.value == button) {
                                    unbinds.add(binding.key);
                                }
                            }
                            for (Binding binding : unbinds) {
                                JamScreen.addUnboundBinding(binding);
                            }
                            
                            JamScreen.addButtonBinding(binding, button);
                            JamScreen.saveBindings();
                            refreshTable(table);
                        }
    
                        @Override
                        public void scrollSelected(int scroll) {
                            Array<Binding> unbinds = new Array<>();
                            for (Entry<Binding> binding : JamScreen.scrollBindings) {
                                if (binding.value == scroll) {
                                    unbinds.add(binding.key);
                                }
                            }
                            for (Binding binding : unbinds) {
                                JamScreen.addUnboundBinding(binding);
                            }
                            
                            JamScreen.addScrollBinding(binding, scroll);
                            JamScreen.saveBindings();
                            refreshTable(table);
                        }
    
                        @Override
                        public void controllerButtonSelected(int buttonCode) {
                            System.out.println(buttonCode);
                            Array<Binding> unbinds = new Array<>();
                            for (Entry<Binding> binding : JamScreen.controllerButtonBindings) {
                                if (binding.value == buttonCode) {
                                    unbinds.add(binding.key);
                                }
                            }
                            for (Binding binding : unbinds) {
                                JamScreen.addUnboundBinding(binding);
                            }
    
                            JamScreen.addControllerButtonBinding(binding, buttonCode);
                            JamScreen.saveBindings();
                            refreshTable(table);
                        }
    
                        @Override
                        public void cancelled() {
                        
                        }
                    });
                    dialog.show(getStage());
                }
            });
        }
        
        for (Actor actor : getButtonTable().getChildren()) {
            focusables.add(actor);
        }
    }
    
    @Override
    protected void result(Object object) {
        getStage().removeListener(keysListener);
    }
    
    private static class DialogKeyBinding extends Dialog {
        private ControllerListener controllerListener;
        
        public DialogKeyBinding(Core.Binding binding) {
            super("", Core.core.skin);
            
            setFillParent(true);
            Table root = getContentTable();
            
            text("Input any key, mouse button, scroll wheel, or controller button to set");
            root.row();
            text(binding.toString());
            root.row();
            text("(Press ESCAPE to cancel)");
            root.row();
            text("...");
            
            addListener(new InputListener() {
                @Override
                public boolean keyDown(InputEvent event, int keycode) {
                    if (keycode != Keys.ESCAPE) {
                        fire(new KeyBindingEvent(keycode));
                    } else {
                        fire(new CancelEvent());
                    }
                    hide();
                    return true;
                }
    
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    fire(new ButtonBindingEvent(button));
                    hide();
                    return true;
                }
                
                @Override
                public boolean scrolled(InputEvent event, float x, float y, int amount) {
                    fire(new ScrollBindingEvent(amount));
                    hide();
                    return true;
                }
            });
    
            controllerListener = new ControllerListener() {
                @Override
                public void connected(Controller controller) {
            
                }
        
                @Override
                public void disconnected(Controller controller) {
            
                }
        
                @Override
                public boolean buttonDown(Controller controller, int buttonCode) {
                    fire(new ControllerButtonBindingEvent(buttonCode));
                    hide();
                    return false;
                }
        
                @Override
                public boolean buttonUp(Controller controller, int buttonCode) {
                    return false;
                }
        
                @Override
                public boolean axisMoved(Controller controller, int axisCode, float value) {
                    return false;
                }
        
                @Override
                public boolean povMoved(Controller controller, int povCode, PovDirection value) {
                    return false;
                }
        
                @Override
                public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
                    return false;
                }
        
                @Override
                public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
                    return false;
                }
        
                @Override
                public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
                    return false;
                }
            };
            Controllers.addListener(controllerListener);
        }
    
        @Override
        public void hide(Action action) {
            super.hide(action);
            System.out.println("removed");
            Controllers.removeListener(controllerListener);
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
    
    private static class ScrollBindingEvent extends Event {
        private int scroll;
        
        public ScrollBindingEvent(int scroll) {
            this.scroll = scroll;
        }
    }
    
    private static class ControllerButtonBindingEvent extends Event {
        private int buttonCode;
        
        public ControllerButtonBindingEvent(int buttonCode) {
            this.buttonCode = buttonCode;
        }
    }
    
    private static class CancelEvent extends Event {
    
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
            } else if (event instanceof ScrollBindingEvent) {
                scrollSelected(((ScrollBindingEvent) event).scroll);
                return true;
            } else if (event instanceof ControllerButtonBindingEvent) {
                controllerButtonSelected(((ControllerButtonBindingEvent) event).buttonCode);
                return true;
            } else if (event instanceof CancelEvent) {
                cancelled();
                return true;
            } else {
                return false;
            }
        }
        
        public abstract void keySelected(int key);
        public abstract void buttonSelected(int button);
        public abstract void scrollSelected(int scroll);
        public abstract void controllerButtonSelected(int buttonCode);
        public abstract void cancelled();
    }
}
