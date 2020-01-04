package com.ray3k.template;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectIntMap.Entry;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ray3k.template.Core.Binding;

public abstract class JamScreen extends ScreenAdapter implements InputProcessor {
    public Viewport viewport;
    public OrthographicCamera camera;
    public float mouseX;
    public float mouseY;
    public IntArray keysJustPressed = new IntArray();
    public IntArray buttonsJustPressed = new IntArray();
    public IntArray buttonsPressed = new IntArray();
    public IntArray scrollJustPressed = new IntArray();
    private static final Vector3 tempVector3 = new Vector3();
    public final static ObjectIntMap<Core.Binding> keyBindings = new ObjectIntMap<>();
    public final static ObjectIntMap<Core.Binding> buttonBindings = new ObjectIntMap<>();
    public final static ObjectIntMap<Core.Binding> scrollBindings = new ObjectIntMap<>();
    public final static Array<Core.Binding> bindings = new Array<>();
    public final static int ANY_BUTTON = -1;
    public final static int SCROLL_UP = -1;
    public final static int SCROLL_DOWN = 1;
    public final static int ANY_SCROLL = 0;
    
    @Override @Deprecated
    public void render(float delta) {
    
    }
    
    public void updateMouse() {
        if (viewport != null) {
            tempVector3.x = Gdx.input.getX();
            tempVector3.y = Gdx.input.getY();
            viewport.unproject(tempVector3);
            mouseX = tempVector3.x;
            mouseY = tempVector3.y;
        } else {
            mouseX = Gdx.input.getX();
            mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        }
    }
    
    public void clearStates() {
        keysJustPressed.clear();
        buttonsJustPressed.clear();
        scrollJustPressed.clear();
    }
    
    public abstract void act(float delta);
    
    public abstract void draw(float delta);
    
    @Override
    public boolean keyDown(int keycode) {
        keysJustPressed.add(keycode);
        return false;
    }
    
    @Override
    public boolean keyUp(int keycode) {
        return false;
    }
    
    @Override
    public boolean keyTyped(char character) {
        return false;
    }
    
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        buttonsJustPressed.add(button);
        buttonsPressed.add(button);
        return false;
    }
    
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        buttonsPressed.removeValue(button);
        return false;
    }
    
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }
    
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }
    
    @Override
    public boolean scrolled(int amount) {
        scrollJustPressed.add(amount);
        return false;
    }
    
    public boolean isKeyJustPressed(int key) {
        return key == Input.Keys.ANY_KEY ? keysJustPressed.size > 0 : keysJustPressed.contains(key);
    }
    
    public boolean isKeyJustPressed(int... keys) {
        for (int key : keys) {
            if (isKeyJustPressed(key)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns true if the associated mouse button has been pressed since the last step.
     * @param button The button value or -1 for any button
     * @return
     */
    public boolean isButtonJustPressed(int button) {
        return button == ANY_BUTTON ? buttonsJustPressed.size > 0 : buttonsJustPressed.contains(button);
    }
    
    public boolean isButtonJustPressed(int... buttons) {
        for (int button : buttons) {
            if (isButtonJustPressed(button)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isScrollJustPressed(int scroll) {
        return scroll == ANY_SCROLL ? scrollJustPressed.size > 0 : scrollJustPressed.contains(scroll);
    }
    
    public boolean isScrollJustPressed(int... scrolls) {
        for (int scroll : scrolls) {
            if (isScrollJustPressed(scroll)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isKeyPressed(int key) {
        return Gdx.input.isKeyPressed(key);
    }
    
    public boolean isAnyKeyPressed(int... keys) {
        for (int key : keys) {
            if (isKeyPressed(key)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean areAllKeysPressed(int... keys) {
        for (int key : keys) {
            if (!isKeyPressed(key)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isButtonPressed(int button) {
        if (button == ANY_BUTTON) {
            return buttonsPressed.contains(Input.Buttons.LEFT) || buttonsPressed.contains(Input.Buttons.RIGHT)
                    || buttonsPressed.contains(Input.Buttons.MIDDLE) || buttonsPressed.contains(Input.Buttons.BACK)
                    || buttonsPressed.contains(Input.Buttons.FORWARD);
        } else {
            return buttonsPressed.contains(button);
        }
    }
    
    public boolean isAnyButtonPressed(int... buttons) {
        for (int button : buttons) {
            if (isButtonPressed(button)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean areAllButtonsPressed(int... buttons) {
        for (int button : buttons) {
            if (!isButtonPressed(button)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isBindingPressed(Core.Binding binding) {
        if (keyBindings.containsKey(binding)) {
            return isKeyPressed(keyBindings.get(binding, Input.Keys.ANY_KEY));
        } else if (buttonBindings.containsKey(binding)) {
            return isButtonPressed(keyBindings.get(binding, ANY_BUTTON));
        } else {
            return false;
        }
    }
    
    public boolean isAnyBindingPressed(Core.Binding... bindings) {
        for (Core.Binding binding : bindings) {
            if (isBindingPressed(binding)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean areAllBindingsPressed(Core.Binding... bindings) {
        for (Core.Binding binding : bindings) {
            if (!isBindingPressed(binding)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isBindingJustPressed(Core.Binding binding) {
        if (keyBindings.containsKey(binding)) {
            return isKeyJustPressed(keyBindings.get(binding, Input.Keys.ANY_KEY));
        } else if (buttonBindings.containsKey(binding)) {
            return isButtonJustPressed(keyBindings.get(binding, ANY_BUTTON));
        } else if (scrollBindings.containsKey(binding)) {
            return isScrollJustPressed(scrollBindings.get(binding, ANY_SCROLL));
        } else {
            return false;
        }
    }
    
    public boolean isBindingJustPressed(Core.Binding... bindings) {
        for (Core.Binding binding : bindings) {
            if (isBindingJustPressed(binding)) {
                return true;
            }
        }
        return false;
    }
    
    public static void clearBindings() {
        keyBindings.clear();
        buttonBindings.clear();
        scrollBindings.clear();
        bindings.clear();
    }
    
    public static void addKeyBinding(Core.Binding binding, int key) {
        buttonBindings.remove(binding, ANY_BUTTON);
        scrollBindings.remove(binding, ANY_SCROLL);
        keyBindings.put(binding, key);
        if (!bindings.contains(binding, true)) {
            bindings.add(binding);
        }
    }
    
    public static void addButtonBinding(Core.Binding binding, int button) {
        keyBindings.remove(binding, Input.Keys.ANY_KEY);
        scrollBindings.remove(binding, ANY_SCROLL);
        buttonBindings.put(binding, button);
        if (!bindings.contains(binding, true)) {
            bindings.add(binding);
        }
    }
    
    public static void addScrollBinding(Core.Binding binding, int scroll) {
        keyBindings.remove(binding, Input.Keys.ANY_KEY);
        buttonBindings.remove(binding, ANY_BUTTON);
        scrollBindings.put(binding, scroll);
        if (!bindings.contains(binding, true)) {
            bindings.add(binding);
        }
    }
    
    public static void removeBinding(Core.Binding binding) {
        keyBindings.remove(binding, Input.Keys.ANY_KEY);
        buttonBindings.remove(binding, ANY_BUTTON);
        scrollBindings.remove(binding, ANY_SCROLL);
        bindings.removeValue(binding, true);
    }
    
    public static boolean hasBinding(Core.Binding binding) {
        return bindings.contains(binding, true);
    }
    
    public static boolean hasKeyBinding(Core.Binding binding) {
        return keyBindings.containsKey(binding);
    }
    
    public static boolean hasButtonBinding(Core.Binding binding) {
        return buttonBindings.containsKey(binding);
    }
    
    public static boolean hasScrollBinding(Core.Binding binding) {
        return scrollBindings.containsKey(binding);
    }
    
    public static Array<Core.Binding> getBindings() {
        return bindings;
    }
    
    public static int getKeyBinding(Core.Binding binding) {
        return keyBindings.get(binding, Input.Keys.ANY_KEY);
    }
    
    public static int getButtonBinding(Core.Binding binding) {
        return buttonBindings.get(binding, ANY_BUTTON);
    }
    
    public static int getScrollBinding(Core.Binding binding) {
        return scrollBindings.get(binding, ANY_SCROLL);
    }
    
    public static int getBinding(Core.Binding binding) {
        if (keyBindings.containsKey(binding)) {
            return getKeyBinding(binding);
        } else if (buttonBindings.containsKey(binding)) {
            return getButtonBinding(binding);
        } else {
            return getScrollBinding(binding);
        }
    }
    
    public static void saveBindings() {
        Preferences pref = Core.core.preferences;
        for (Entry<Binding> keyBinding : keyBindings) {
            pref.putInteger("key:" + keyBinding.key.toString(), keyBinding.value);
            pref.remove("button:" + keyBinding.key.toString());
            pref.remove("scroll:" + keyBinding.key.toString());
        }
        for (Entry<Binding> buttonBinding : buttonBindings) {
            pref.putInteger("button:" + buttonBinding.key.toString(), buttonBinding.value);
            pref.remove("key:" + buttonBinding.key.toString());
            pref.remove("scroll:" + buttonBinding.key.toString());
        }
        for (Entry<Binding> scrollBinding : scrollBindings) {
            pref.putInteger("scroll:" + scrollBinding.key.toString(), scrollBinding.value);
            pref.remove("key:" + scrollBinding.key.toString());
            pref.remove("button:" + scrollBinding.key.toString());
        }
        pref.flush();
    }
    
    public static void loadBindings() {
        Preferences pref = Core.core.preferences;
        for (Entry<Binding> keyBinding : keyBindings) {
            String key = "key:" + keyBinding.key.toString();
            if (pref.contains(key)) {
                JamScreen.addKeyBinding(keyBinding.key, pref.getInteger(key));
            }
        }
    
        for (Entry<Binding> buttonBinding : buttonBindings) {
            String key = "button:" + buttonBinding.key.toString();
            if (pref.contains(key)) {
                JamScreen.addButtonBinding(buttonBinding.key, pref.getInteger(key));
            }
        }
    
        for (Entry<Binding> scrollBinding : scrollBindings) {
            String key = "scroll:" + scrollBinding.key.toString();
            if (pref.contains(key)) {
                JamScreen.addScrollBinding(scrollBinding.key, pref.getInteger(key));
            }
        }
    }
}