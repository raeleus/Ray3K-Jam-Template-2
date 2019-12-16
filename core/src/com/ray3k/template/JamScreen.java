package com.ray3k.template;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.Viewport;

import javax.script.Bindings;
import java.util.Map;

public abstract class JamScreen extends ScreenAdapter implements InputProcessor {
    public Viewport viewport;
    public OrthographicCamera camera;
    public float mouseX;
    public float mouseY;
    public IntArray keysJustPressed = new IntArray();
    public IntArray buttonsJustPressed = new IntArray();
    public IntArray buttonsPressed = new IntArray();
    private static final Vector3 tempVector3 = new Vector3();
    public final static ObjectIntMap<Core.Binding> keyBindings = new ObjectIntMap<>();
    public final static ObjectIntMap<Core.Binding> buttonBindings = new ObjectIntMap<>();
    public final static Array<Core.Binding> bindings = new Array<>();
    public final static int ANY_BUTTON = -1;
    
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
        return false;
    }
    
    public boolean isKeyJustPressed(int key) {
        return key == Input.Keys.ANY_KEY ? keysJustPressed.size > 0 : keysJustPressed.contains(key);
    }
    
    /**
     * Returns true if the associated mouse button has been pressed since the last step.
     * @param button The button value or -1 for any button
     * @return
     */
    public boolean isButtonJustPressed(int button) {
        return button == ANY_BUTTON ? buttonsJustPressed.size > 0 : buttonsJustPressed.contains(button);
    }
    
    public boolean isKeyPressed(int key) {
        return Gdx.input.isKeyPressed(key);
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
    
    public boolean isBindingPressed(Core.Binding binding) {
        if (keyBindings.containsKey(binding)) {
            return isKeyPressed(keyBindings.get(binding, Input.Keys.ANY_KEY));
        } else if (buttonBindings.containsKey(binding)) {
            return isButtonPressed(keyBindings.get(binding, ANY_BUTTON));
        } else {
            return false;
        }
    }
    
    public boolean isBindingJustPressed(Core.Binding binding) {
        if (keyBindings.containsKey(binding)) {
            return isKeyPressed(keyBindings.get(binding, Input.Keys.ANY_KEY));
        } else if (buttonBindings.containsKey(binding)) {
            return isButtonPressed(keyBindings.get(binding, ANY_BUTTON));
        } else {
            return false;
        }
    }
    
    public static void clearBindings() {
        keyBindings.clear();
        buttonBindings.clear();
        bindings.clear();
    }
    
    public static void addKeyBinding(Core.Binding binding, int key) {
        buttonBindings.remove(binding, ANY_BUTTON);
        keyBindings.put(binding, key);
        if (!bindings.contains(binding, true)) {
            bindings.add(binding);
        }
    }
    
    public static void addButtonBinding(Core.Binding binding, int button) {
        keyBindings.remove(binding, Input.Keys.ANY_KEY);
        buttonBindings.put(binding, button);
        if (!bindings.contains(binding, true)) {
            bindings.add(binding);
        }
    }
    
    public static void removeBinding(Core.Binding binding) {
        keyBindings.remove(binding, Input.Keys.ANY_KEY);
        buttonBindings.remove(binding, ANY_BUTTON);
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
    
    public static Array<Core.Binding> getBindings() {
        return bindings;
    }
    
    public static int getKeyBinding(Core.Binding binding) {
        return keyBindings.get(binding, Input.Keys.ANY_KEY);
    }
    
    public static int getButtonBinding(Core.Binding binding) {
        return buttonBindings.get(binding, ANY_BUTTON);
    }
    
    public static int getBinding(Core.Binding binding) {
        return keyBindings.containsKey(binding) ? getKeyBinding(binding) : getButtonBinding(binding);
    }
}
