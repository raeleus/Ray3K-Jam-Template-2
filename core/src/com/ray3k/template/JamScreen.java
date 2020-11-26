package com.ray3k.template;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.ObjectIntMap.Entry;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ray3k.template.Core.*;
import com.ray3k.template.JamScreen.ControllerHandler.*;

import java.util.Iterator;
import java.util.Objects;

import static com.ray3k.template.Core.*;

public abstract class JamScreen extends ScreenAdapter implements InputProcessor, ControllerListener {
    public Viewport viewport;
    public OrthographicCamera camera;
    public float mouseX;
    public float mouseY;
    public IntArray keysJustPressed = new IntArray();
    public IntArray buttonsJustPressed = new IntArray();
    public IntArray buttonsPressed = new IntArray();
    public IntArray scrollJustPressed = new IntArray();
    private static final Vector3 tempVector3 = new Vector3();
    public final static ObjectIntMap<Binding> keyBindings = new ObjectIntMap<>();
    public final static ObjectIntMap<Binding> buttonBindings = new ObjectIntMap<>();
    public final static ObjectIntMap<Binding> scrollBindings = new ObjectIntMap<>();
    public final static ObjectMap<Binding, ControllerValue> controllerButtonBindings = new ObjectMap<>();
    public final static ObjectMap<Binding, ControllerValue> controllerAxisBindings = new ObjectMap<>();
    public final static ObjectMap<Binding, ControllerValue> controllerPovBindings = new ObjectMap<>();
    public final static ObjectSet<Binding> unboundBindings = new ObjectSet<>();
    public final static Array<Binding> bindings = new Array<>();
    public final static int ANY_BUTTON = -1;
    public final static int SCROLL_UP = -1;
    public final static int SCROLL_DOWN = 1;
    public final static int ANY_SCROLL = 0;
    public final static ControllerValue ANY_CONTROLLER_BUTTON = new ControllerValue(null, -1, 0);
    public final static ControllerValue ANY_CONTROLLER_AXIS = new ControllerValue(null, -1, 0);
    public final static ControllerValue ANY_CONTROLLER_POV = new ControllerValue(null, -1, 0);
    public final static ObjectMap<Controller, ControllerHandler> controllerMap = new ObjectMap<>();
    public boolean separatePodDiagonal = true;
    
    @Override
    public void show() {
        super.show();
        Controllers.addListener(this);
    }
    
    @Override
    public void hide() {
        super.hide();
        Controllers.removeListener(this);
    }
    
    @Override
    @Deprecated
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
        for (ControllerHandler handler : controllerMap.values()) {
            handler.controllerButtonsJustPressed.clear();
            handler.controllerPovJustPressed.clear();
            handler.controllerAxisJustPressed.clear();
        }
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
    public boolean scrolled(float amountX, float amountY) {
        scrollJustPressed.add((int) amountY);
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
     *
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
    
    public boolean isAnyKeyPressed() {
        return isKeyPressed(Keys.ANY_KEY);
    }
    
    public boolean isAnyKeyJustPressed(int... keys) {
        for (int key : keys) {
            if (isKeyJustPressed(key)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isAnyKeyJustPressed() {
        return keysJustPressed.size > 0;
    }
    
    public boolean areAllKeysPressed(int... keys) {
        for (int key : keys) {
            if (!isKeyPressed(key)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isControllerButtonJustPressed(ControllerValue... buttonCodes) {
        for (ControllerHandler handler : controllerMap.values()) {
            if (handler.isControllerButtonJustPressed(buttonCodes)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isControllerAxisJustPressed(ControllerValue... axisCodes) {
        for (ControllerHandler handler : controllerMap.values()) {
            if (handler.isControllerAxisJustPressed(axisCodes)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isControllerPovJustPressed(ControllerValue... povCodes) {
        for (ControllerHandler handler : controllerMap.values()) {
            if (handler.isControllerPovJustPressed(povCodes)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isControllerButtonPressed(ControllerValue... buttonCodes) {
        for (ControllerHandler handler : controllerMap.values()) {
            if (handler.isControllerButtonPressed(buttonCodes)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isControllerAxisPressed(ControllerValue... axisCodes) {
        for (ControllerHandler handler : controllerMap.values()) {
            if (handler.isControllerAxisPressed(axisCodes)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isControllerPovPressed(ControllerValue... povCodes) {
        for (ControllerHandler handler : controllerMap.values()) {
            if (handler.isControllerPovPressed(povCodes)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isAnyControllerButtonPressed() {
        for (ControllerHandler handler : controllerMap.values()) {
            if (handler.isAnyControllerButtonPressed()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isAnyControllerButtonJustPressed() {
        for (ControllerHandler handler : controllerMap.values()) {
            if (handler.isAnyControllerButtonJustPressed()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isAnyControllerAxisPressed() {
        for (ControllerHandler handler : controllerMap.values()) {
            if (handler.isAnyControllerAxisPressed()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isAnyControllerAxisJustPressed() {
        for (ControllerHandler handler : controllerMap.values()) {
            if (handler.isAnyControllerAxisJustPressed()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isAnyControllerPovPressed() {
        for (ControllerHandler handler : controllerMap.values()) {
            if (handler.isAnyControllerPovPressed()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isAnyControllerPovJustPressed() {
        for (ControllerHandler handler : controllerMap.values()) {
            if (handler.isAnyControllerPovJustPressed()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean areAllControllerButtonsPressed(ControllerValue... buttonCodes) {
        for (ControllerHandler handler : controllerMap.values()) {
            if (handler.areAllControllerButtonsPressed(buttonCodes)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean areAllControllerAxisPressed(ControllerValue... axisCodes) {
        for (ControllerHandler handler : controllerMap.values()) {
            if (handler.areAllControllerAxisPressed(axisCodes)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean areAllControllerPovPressed(ControllerValue... povCodes) {
        for (ControllerHandler handler : controllerMap.values()) {
            if (handler.areAllControllerPovPressed(povCodes)) {
                return true;
            }
        }
        return false;
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
    
    public boolean isAnyButtonPressed() {
        return buttonsPressed.size > 0;
    }
    
    public boolean isAnyButtonJustPressed(int... buttons) {
        for (int button : buttons) {
            if (isButtonJustPressed(button)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isAnyButtonJustPressed() {
        return buttonsJustPressed.size > 0;
    }
    
    public boolean areAllButtonsPressed(int... buttons) {
        for (int button : buttons) {
            if (!isButtonPressed(button)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isBindingPressed(Binding binding) {
        if (keyBindings.containsKey(binding)) {
            return isKeyPressed(keyBindings.get(binding, Input.Keys.ANY_KEY));
        } else if (buttonBindings.containsKey(binding)) {
            return isButtonPressed(keyBindings.get(binding, ANY_BUTTON));
        } else if (controllerButtonBindings.containsKey(binding)) {
            return isControllerButtonPressed(controllerButtonBindings.get(binding));
        } else if (controllerAxisBindings.containsKey(binding)) {
            return isControllerAxisPressed(controllerAxisBindings.get(binding, ANY_CONTROLLER_AXIS));
        } else if (controllerPovBindings.containsKey(binding)) {
            return isControllerPovPressed(controllerPovBindings.get(binding, ANY_CONTROLLER_POV));
        } else {
            return false;
        }
    }
    
    public boolean isAnyBindingJustPressed() {
        for (Binding binding : bindings) {
            if (isBindingJustPressed(binding)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isAnyBindingJustPressed(Binding... bindings) {
        for (Binding binding : bindings) {
            if (isBindingJustPressed(binding)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isAnyBindingPressed() {
        for (Binding binding : bindings) {
            if (isBindingPressed(binding)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isAnyBindingPressed(Binding... bindings) {
        for (Binding binding : bindings) {
            if (isBindingPressed(binding)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean areAllBindingsPressed(Binding... bindings) {
        for (Binding binding : bindings) {
            if (!isBindingPressed(binding)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isBindingJustPressed(Binding binding) {
        if (keyBindings.containsKey(binding)) {
            return isKeyJustPressed(keyBindings.get(binding, Input.Keys.ANY_KEY));
        } else if (buttonBindings.containsKey(binding)) {
            return isButtonJustPressed(keyBindings.get(binding, ANY_BUTTON));
        } else if (scrollBindings.containsKey(binding)) {
            return isScrollJustPressed(scrollBindings.get(binding, ANY_SCROLL));
        } else if (controllerButtonBindings.containsKey(binding)) {
            return isControllerButtonJustPressed(controllerButtonBindings.get(binding, ANY_CONTROLLER_BUTTON));
        } else if (controllerAxisBindings.containsKey(binding)) {
            return isControllerAxisJustPressed(controllerAxisBindings.get(binding, ANY_CONTROLLER_AXIS));
        } else if (controllerPovBindings.containsKey(binding)) {
            return isControllerPovJustPressed(controllerPovBindings.get(binding, ANY_CONTROLLER_POV));
        } else {
            return false;
        }
    }
    
    public boolean isBindingJustPressed(Binding... bindings) {
        for (Binding binding : bindings) {
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
        controllerButtonBindings.clear();
        controllerAxisBindings.clear();
        controllerPovBindings.clear();
        unboundBindings.clear();
        bindings.clear();
    }
    
    public static void addKeyBinding(Binding binding, int key) {
        buttonBindings.remove(binding, ANY_BUTTON);
        scrollBindings.remove(binding, ANY_SCROLL);
        controllerButtonBindings.remove(binding);
        controllerAxisBindings.remove(binding);
        controllerPovBindings.remove(binding);
        unboundBindings.remove(binding);
        keyBindings.put(binding, key);
        if (!bindings.contains(binding, true)) {
            bindings.add(binding);
        }
    }
    
    public static void addButtonBinding(Binding binding, int button) {
        keyBindings.remove(binding, Input.Keys.ANY_KEY);
        scrollBindings.remove(binding, ANY_SCROLL);
        controllerButtonBindings.remove(binding);
        controllerAxisBindings.remove(binding);
        controllerPovBindings.remove(binding);
        unboundBindings.remove(binding);
        buttonBindings.put(binding, button);
        if (!bindings.contains(binding, true)) {
            bindings.add(binding);
        }
    }
    
    public static void addScrollBinding(Binding binding, int scroll) {
        keyBindings.remove(binding, Input.Keys.ANY_KEY);
        buttonBindings.remove(binding, ANY_BUTTON);
        controllerButtonBindings.remove(binding);
        controllerAxisBindings.remove(binding);
        controllerPovBindings.remove(binding);
        unboundBindings.remove(binding);
        scrollBindings.put(binding, scroll);
        if (!bindings.contains(binding, true)) {
            bindings.add(binding);
        }
    }
    
    public static void addControllerButtonBinding(Binding binding, ControllerValue controllerValue) {
        keyBindings.remove(binding, Input.Keys.ANY_KEY);
        buttonBindings.remove(binding, ANY_BUTTON);
        scrollBindings.remove(binding, ANY_SCROLL);
        unboundBindings.remove(binding);
        controllerAxisBindings.remove(binding);
        controllerPovBindings.remove(binding);
        controllerButtonBindings.put(binding, controllerValue);
        if (!bindings.contains(binding, true)) {
            bindings.add(binding);
        }
    }
    
    public static void addControllerAxisBinding(Binding binding, ControllerValue controllerValue) {
        keyBindings.remove(binding, Input.Keys.ANY_KEY);
        buttonBindings.remove(binding, ANY_BUTTON);
        scrollBindings.remove(binding, ANY_SCROLL);
        unboundBindings.remove(binding);
        controllerButtonBindings.remove(binding);
        controllerPovBindings.remove(binding);
        controllerAxisBindings.put(binding, controllerValue);
        if (!bindings.contains(binding, true)) {
            bindings.add(binding);
        }
    }
    
    public static void addControllerPovBinding(Binding binding, ControllerValue controllerValue) {
        keyBindings.remove(binding, Input.Keys.ANY_KEY);
        buttonBindings.remove(binding, ANY_BUTTON);
        scrollBindings.remove(binding, ANY_SCROLL);
        unboundBindings.remove(binding);
        controllerButtonBindings.remove(binding);
        controllerAxisBindings.remove(binding);
        controllerPovBindings.put(binding, controllerValue);
        if (!bindings.contains(binding, true)) {
            bindings.add(binding);
        }
    }
    
    public static void addUnboundBinding(Binding binding) {
        keyBindings.remove(binding, Input.Keys.ANY_KEY);
        buttonBindings.remove(binding, ANY_BUTTON);
        scrollBindings.remove(binding, ANY_SCROLL);
        controllerButtonBindings.remove(binding);
        controllerAxisBindings.remove(binding);
        controllerPovBindings.remove(binding);
        unboundBindings.add(binding);
        if (!bindings.contains(binding, true)) {
            bindings.add(binding);
        }
    }
    
    public static void removeBinding(Binding binding) {
        keyBindings.remove(binding, Input.Keys.ANY_KEY);
        buttonBindings.remove(binding, ANY_BUTTON);
        scrollBindings.remove(binding, ANY_SCROLL);
        controllerButtonBindings.remove(binding);
        controllerAxisBindings.remove(binding);
        controllerPovBindings.remove(binding);
        bindings.removeValue(binding, true);
    }
    
    public static boolean hasBinding(Binding binding) {
        return bindings.contains(binding, true);
    }
    
    public static boolean hasKeyBinding(Binding binding) {
        return keyBindings.containsKey(binding);
    }
    
    public static boolean hasButtonBinding(Binding binding) {
        return buttonBindings.containsKey(binding);
    }
    
    public static boolean hasScrollBinding(Binding binding) {
        return scrollBindings.containsKey(binding);
    }
    
    public static boolean hasControllerButtonBinding(Binding binding) {
        return controllerButtonBindings.containsKey(binding);
    }
    
    public static boolean hasControllerAxisBinding(Binding binding) {
        return controllerAxisBindings.containsKey(binding);
    }
    
    public static boolean hasControllerPovBinding(Binding binding) {
        return controllerPovBindings.containsKey(binding);
    }
    
    public static boolean hasUnboundBinding(Binding binding) {
        return unboundBindings.contains(binding);
    }
    
    public static Array<Binding> getBindings() {
        return bindings;
    }
    
    public static int getKeyBinding(Binding binding) {
        return keyBindings.get(binding, Input.Keys.ANY_KEY);
    }
    
    public static int getButtonBinding(Binding binding) {
        return buttonBindings.get(binding, ANY_BUTTON);
    }
    
    public static int getScrollBinding(Binding binding) {
        return scrollBindings.get(binding, ANY_SCROLL);
    }
    
    public static ControllerValue getControllerButtonBinding(Binding binding) {
        return controllerButtonBindings.get(binding, ANY_CONTROLLER_BUTTON);
    }
    
    public static ControllerValue getControllerAxisBinding(Binding binding) {
        return controllerAxisBindings.get(binding, ANY_CONTROLLER_AXIS);
    }
    
    public static ControllerValue getControllerPovBinding(Binding binding) {
        return controllerPovBindings.get(binding, ANY_CONTROLLER_POV);
    }
    
    public static String getBindingCodeName(Binding binding) {
        if (keyBindings.containsKey(binding)) {
            return Input.Keys.toString(getKeyBinding(binding));
        } else if (buttonBindings.containsKey(binding)) {
            return Utils.mouseButtonToString(getButtonBinding(binding));
        } else if (controllerButtonBindings.containsKey(binding)) {
            return Utils.controllerButtonToString(getControllerButtonBinding(binding));
        } else if (controllerAxisBindings.containsKey(binding)) {
            return Utils.controllerAxisToString(getControllerAxisBinding(binding));
        } else if (controllerPovBindings.containsKey(binding)) {
            return Utils.controllerPovToString(getControllerPovBinding(binding));
        } else if (scrollBindings.containsKey(binding)) {
            return Utils.scrollAmountToString(getScrollBinding(binding));
        } else {
            return "UNBOUND";
        }
    }
    
    public static void saveBindings() {
        for (Entry<Binding> binding : keyBindings) {
            preferences.putInteger("key:" + binding.key.toString(), binding.value);
            preferences.remove("button:" + binding.key.toString());
            preferences.remove("scroll:" + binding.key.toString());
            preferences.remove("controllerbutton:" + binding.key.toString());
            preferences.remove("controlleraxis:" + binding.key.toString());
            preferences.remove("controllerpov:" + binding.key.toString());
            preferences.remove("unbound:" + binding.key.toString());
        }
        
        for (Entry<Binding> binding : buttonBindings) {
            preferences.putInteger("button:" + binding.key.toString(), binding.value);
            preferences.remove("key:" + binding.key.toString());
            preferences.remove("scroll:" + binding.key.toString());
            preferences.remove("controllerbutton:" + binding.key.toString());
            preferences.remove("controlleraxis:" + binding.key.toString());
            preferences.remove("controllerpov:" + binding.key.toString());
            preferences.remove("unbound:" + binding.key.toString());
        }
        
        for (Entry<Binding> binding : scrollBindings) {
            preferences.putInteger("scroll:" + binding.key.toString(), binding.value);
            preferences.remove("key:" + binding.key.toString());
            preferences.remove("button:" + binding.key.toString());
            preferences.remove("controllerbutton:" + binding.key.toString());
            preferences.remove("controlleraxis:" + binding.key.toString());
            preferences.remove("controllerpov:" + binding.key.toString());
            preferences.remove("unbound:" + binding.key.toString());
        }
        
        for (ObjectMap.Entry<Binding, ControllerValue> binding : controllerButtonBindings) {
            preferences.putString("controllerbutton:" + binding.key.toString(), Controllers.getControllers().indexOf(binding.value.controller, true) + " " + binding.value.axisCode + " " + binding.value.value);
            preferences.remove("key:" + binding.key.toString());
            preferences.remove("button:" + binding.key.toString());
            preferences.remove("scroll:" + binding.key.toString());
            preferences.remove("controlleraxis:" + binding.key.toString());
            preferences.remove("controllerpov:" + binding.key.toString());
            preferences.remove("unbound:" + binding.key.toString());
        }
        
        for (ObjectMap.Entry<Binding, ControllerValue> binding : controllerAxisBindings) {
            preferences.putString("controlleraxis:" + binding.key.toString(), Controllers.getControllers().indexOf(binding.value.controller, true) + " " + binding.value.axisCode + " " + binding.value.value);
            preferences.remove("key:" + binding.key.toString());
            preferences.remove("button:" + binding.key.toString());
            preferences.remove("scroll:" + binding.key.toString());
            preferences.remove("controllerbutton:" + binding.key.toString());
            preferences.remove("controllerpov:" + binding.key.toString());
            preferences.remove("unbound:" + binding.key.toString());
        }
        
        for (ObjectMap.Entry<Binding, ControllerValue> binding : controllerPovBindings) {
            preferences.putString("controllerpov:" + binding.key.toString(), Controllers.getControllers().indexOf(binding.value.controller, true) + " " + binding.value.axisCode + " " + binding.value.value);
            preferences.remove("key:" + binding.key.toString());
            preferences.remove("button:" + binding.key.toString());
            preferences.remove("scroll:" + binding.key.toString());
            preferences.remove("controllerbutton:" + binding.key.toString());
            preferences.remove("controlleraxis:" + binding.key.toString());
            preferences.remove("unbound:" + binding.key.toString());
        }
        
        for (Binding binding : unboundBindings) {
            preferences.putBoolean("unbound:" + binding.toString(), true);
            preferences.remove("key:" + binding.toString());
            preferences.remove("button:" + binding.toString());
            preferences.remove("scroll:" + binding.toString());
            preferences.remove("controllerbutton:" + binding.toString());
            preferences.remove("controlleraxis:" + binding.toString());
            preferences.remove("controllerpov:" + binding.toString());
        }
        preferences.flush();
    }
    
    public static void loadBindings() {
        for (Binding binding : bindings) {
            String key = "key:" + binding.toString();
            if (preferences.contains(key)) {
                JamScreen.addKeyBinding(binding, preferences.getInteger(key));
            }
            
            key = "button:" + binding.toString();
            if (preferences.contains(key)) {
                JamScreen.addButtonBinding(binding, preferences.getInteger(key));
            }
            
            key = "scroll:" + binding.toString();
            if (preferences.contains(key)) {
                JamScreen.addScrollBinding(binding, preferences.getInteger(key));
            }
            
            key = "controllerbutton:" + binding.toString();
            if (preferences.contains(key)) {
                ControllerValue controllerValue = new ControllerValue();
                String[] line = preferences.getString(key).split(" ");
                var controllerIndex = Integer.parseInt(line[0]);
                if (controllerIndex < Controllers.getControllers().size) {
                    controllerValue.controller = Controllers.getControllers().get(controllerIndex);
                }
                controllerValue.axisCode = Integer.parseInt(line[1]);
                controllerValue.value = Integer.parseInt(line[2]);
                JamScreen.addControllerButtonBinding(binding, controllerValue);
            }
            
            key = "controlleraxis:" + binding.toString();
            if (preferences.contains(key)) {
                ControllerValue controllerValue = new ControllerValue();
                String[] line = preferences.getString(key).split(" ");
                var controllerIndex = Integer.parseInt(line[0]);
                if (controllerIndex < Controllers.getControllers().size) {
                    controllerValue.controller = Controllers.getControllers().get(controllerIndex);
                }
                controllerValue.axisCode = Integer.parseInt(line[1]);
                controllerValue.value = Integer.parseInt(line[2]);
                JamScreen.addControllerAxisBinding(binding, controllerValue);
            }
            
            key = "controllerpov:" + binding.toString();
            if (preferences.contains(key)) {
                ControllerValue controllerValue = new ControllerValue();
                String[] line = preferences.getString(key).split(" ");
                var controllerIndex = Integer.parseInt(line[0]);
                if (controllerIndex < Controllers.getControllers().size) {
                    controllerValue.controller = Controllers.getControllers().get(controllerIndex);
                }
                controllerValue.axisCode = Integer.parseInt(line[1]);
                controllerValue.value = Integer.parseInt(line[2]);
                JamScreen.addControllerPovBinding(binding, controllerValue);
            }
            
            key = "unbound:" + binding.toString();
            if (preferences.contains(key)) {
                JamScreen.addUnboundBinding(binding);
            }
        }
    }
    
    @Override
    public void connected(Controller controller) {
        ControllerHandler controllerHandler = new ControllerHandler();
        controller.addListener(controllerHandler);
        controllerMap.put(controller, controllerHandler);
        
        //fix null controllers in bindings loaded from preferences
        for (var binding : bindings) {
            if (JamScreen.hasControllerButtonBinding(binding)) {
                var controllerValue = JamScreen.getControllerButtonBinding(binding);
                var key = "controllerbutton:" + binding.toString();
                if (preferences.contains(key)) {
                    String[] line = preferences.getString(key).split(" ");
                    var controllerIndex = Integer.parseInt(line[0]);
                    if (controllerIndex < Controllers.getControllers().size) {
                        controllerValue.controller = Controllers.getControllers().get(controllerIndex);
                    }
                }
            } else if (JamScreen.hasControllerAxisBinding(binding)) {
                var controllerValue = JamScreen.getControllerAxisBinding(binding);
                var key = "controlleraxis:" + binding.toString();
                if (preferences.contains(key)) {
                    String[] line = preferences.getString(key).split(" ");
                    var controllerIndex = Integer.parseInt(line[0]);
                    if (controllerIndex < Controllers.getControllers().size) {
                        controllerValue.controller = Controllers.getControllers().get(controllerIndex);
                    }
                }
            } else if (JamScreen.hasControllerPovBinding(binding)) {
                var controllerValue = JamScreen.getControllerPovBinding(binding);
                var key = "controllerpov:" + binding.toString();
                if (preferences.contains(key)) {
                    String[] line = preferences.getString(key).split(" ");
                    var controllerIndex = Integer.parseInt(line[0]);
                    if (controllerIndex < Controllers.getControllers().size) {
                        controllerValue.controller = Controllers.getControllers().get(controllerIndex);
                    }
                }
            }
        }
    }
    
    @Override
    public void disconnected(Controller controller) {
        controllerMap.remove(controller);
    }
    
    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        for (ControllerValue controllerValue : controllerButtonBindings.values()) {
            if (controllerValue.controller == null && buttonCode == controllerValue.value) {
                controllerValue.controller = controller;
                break;
            }
        }
        return false;
    }
    
    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        return false;
    }
    
    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        for (ControllerValue controllerValue : controllerAxisBindings.values()) {
            if (controllerValue.controller == null && axisCode == controllerValue.axisCode && MathUtils.round(value) == controllerValue.value) {
                controllerValue.controller = controller;
                break;
            }
        }
        return false;
    }
    
    @Override
    public boolean povMoved(Controller controller, int povCode, PovDirection value) {
        for (ControllerValue controllerValue : controllerPovBindings.values()) {
            if (controllerValue.controller == null && povCode == controllerValue.axisCode && value.ordinal() == controllerValue.value) {
                controllerValue.controller = controller;
                break;
            }
        }
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
    
    public static class ControllerHandler implements ControllerListener {
        public Array<ControllerValue> controllerButtonsJustPressed = new Array<>();
        public Array<ControllerValue> controllerButtonsPressed = new Array<>();
        public Array<ControllerValue> controllerAxisJustPressed = new Array<>();
        public Array<ControllerValue> controllerAxisPressed = new SnapshotArray<>();
        public Array<ControllerValue> controllerPovJustPressed = new Array<>();
        public Array<ControllerValue> controllerPovPressed = new Array<>();
        private PovDirection previousPov = PovDirection.center;
    
        //button
        
        public boolean isControllerButtonJustPressed(ControllerValue buttonCode) {
            return buttonCode == ANY_CONTROLLER_BUTTON ? controllerButtonsJustPressed.size > 0 : controllerButtonsJustPressed.contains(buttonCode, false);
        }
    
        public boolean isControllerButtonJustPressed(ControllerValue... controllerButtons) {
            for (ControllerValue controllerButton : controllerButtons) {
                if (isControllerButtonJustPressed(controllerButton)) {
                    return true;
                }
            }
            return false;
        }
    
        public boolean isAnyControllerButtonJustPressed() {
            return controllerButtonsJustPressed.size > 0;
        }
    
        public boolean isControllerButtonPressed(ControllerValue buttonCode) {
            return buttonCode == ANY_CONTROLLER_BUTTON ? controllerButtonsPressed.size > 0 : controllerButtonsPressed.contains(buttonCode, false);
        }
        
        public boolean isControllerButtonPressed(ControllerValue... buttonCodes) {
            for (ControllerValue controllerButton : buttonCodes) {
                if (isControllerButtonPressed(controllerButton)) {
                    return true;
                }
            }
            return false;
        }
    
        public boolean areAllControllerButtonsPressed(ControllerValue... buttonCodes) {
            for (ControllerValue buttonCode : buttonCodes) {
                if (!isControllerButtonPressed(buttonCode)) {
                    return false;
                }
            }
            return true;
        }
    
        public boolean isAnyControllerButtonPressed() {
            return controllerButtonsPressed.size > 0;
        }
    
        //axis
        
        public boolean isControllerAxisJustPressed(ControllerValue axisCode) {
            return axisCode == ANY_CONTROLLER_AXIS ? controllerAxisJustPressed.size > 0 : controllerAxisJustPressed.contains(axisCode, false);
        }
    
        public boolean isControllerAxisJustPressed(ControllerValue... axisCodes) {
            for (ControllerValue axisCode : axisCodes) {
                if (isControllerAxisJustPressed(axisCode)) {
                    return true;
                }
            }
            return false;
        }
    
        public boolean isAnyControllerAxisJustPressed() {
            return controllerButtonsJustPressed.size > 0;
        }
    
        public boolean isControllerAxisPressed(ControllerValue axisCode) {
            return axisCode == ANY_CONTROLLER_AXIS ? controllerAxisPressed.size > 0 : controllerAxisPressed.contains(axisCode, false);
        }
    
        public boolean isControllerAxisPressed(ControllerValue... axisCodes) {
            for (ControllerValue axisCode : axisCodes) {
                if (isControllerAxisPressed(axisCode)) {
                    return true;
                }
            }
            return false;
        }
    
        public boolean areAllControllerAxisPressed(ControllerValue... axisCodes) {
            for (ControllerValue axisCode : axisCodes) {
                if (!isControllerAxisPressed(axisCode)) {
                    return false;
                }
            }
            return true;
        }
    
        public boolean isAnyControllerAxisPressed() {
            return controllerAxisPressed.size > 0;
        }
    
        //pov
        
        public boolean isControllerPovJustPressed(ControllerValue povCode) {
            if (povCode == ANY_CONTROLLER_POV) {
                return controllerPovJustPressed.size > 0;
            } else {
                boolean returnValue = controllerPovJustPressed.contains(povCode, false);
                if (!returnValue) {
                    if (povCode.value == PovDirection.west.ordinal()) {
                        var val1 = new ControllerValue(povCode.controller, povCode.axisCode, PovDirection.northWest.ordinal());
                        var val2 = new ControllerValue(povCode.controller, povCode.axisCode, PovDirection.southWest.ordinal());
                        returnValue = controllerPovJustPressed.contains(val1, false) || controllerPovJustPressed.contains(val2, false);
                    } else if (povCode.value == PovDirection.north.ordinal()) {
                        var val1 = new ControllerValue(povCode.controller, povCode.axisCode, PovDirection.northWest.ordinal());
                        var val2 = new ControllerValue(povCode.controller, povCode.axisCode, PovDirection.northEast.ordinal());
                        returnValue = controllerPovJustPressed.contains(val1, false) || controllerPovPressed.contains(val2, false);
                    } else if (povCode.value == PovDirection.west.ordinal()) {
                        var val1 = new ControllerValue(povCode.controller, povCode.axisCode, PovDirection.northEast.ordinal());
                        var val2 = new ControllerValue(povCode.controller, povCode.axisCode, PovDirection.southEast.ordinal());
                        returnValue = controllerPovJustPressed.contains(val1, false) || controllerPovJustPressed.contains(val2, false);
                    } else if (povCode.value == PovDirection.south.ordinal()) {
                        var val1 = new ControllerValue(povCode.controller, povCode.axisCode, PovDirection.southWest.ordinal());
                        var val2 = new ControllerValue(povCode.controller, povCode.axisCode, PovDirection.southEast.ordinal());
                        returnValue = controllerPovJustPressed.contains(val1, false) || controllerPovJustPressed.contains(val2, false);
                    }
                }
        
                return returnValue;
            }
        }
    
        public boolean isControllerPovJustPressed(ControllerValue... povCodes) {
            for (ControllerValue povCode : povCodes) {
                if (isControllerPovJustPressed(povCode)) {
                    return true;
                }
            }
            return false;
        }
        
        public boolean isAnyControllerPovJustPressed() {
            return controllerPovJustPressed.size > 0;
        }
    
        public boolean isControllerPovPressed(ControllerValue povCode) {
            if (povCode == ANY_CONTROLLER_POV) {
                return controllerPovPressed.size > 0;
            } else {
                boolean returnValue = controllerPovPressed.contains(povCode, false);
                if (!returnValue) {
                    if (povCode.value == PovDirection.west.ordinal()) {
                        var val1 = new ControllerValue(povCode.controller, povCode.axisCode, PovDirection.northWest.ordinal());
                        var val2 = new ControllerValue(povCode.controller, povCode.axisCode, PovDirection.southWest.ordinal());
                        returnValue = controllerPovPressed.contains(val1, false) || controllerPovPressed.contains(val2, false);
                    } else if (povCode.value == PovDirection.north.ordinal()) {
                        var val1 = new ControllerValue(povCode.controller, povCode.axisCode, PovDirection.northWest.ordinal());
                        var val2 = new ControllerValue(povCode.controller, povCode.axisCode, PovDirection.northEast.ordinal());
                        returnValue = controllerPovPressed.contains(val1, false) || controllerPovPressed.contains(val2, false);
                    } else if (povCode.value == PovDirection.west.ordinal()) {
                        var val1 = new ControllerValue(povCode.controller, povCode.axisCode, PovDirection.northEast.ordinal());
                        var val2 = new ControllerValue(povCode.controller, povCode.axisCode, PovDirection.southEast.ordinal());
                        returnValue = controllerPovPressed.contains(val1, false) || controllerPovPressed.contains(val2, false);
                    } else if (povCode.value == PovDirection.south.ordinal()) {
                        var val1 = new ControllerValue(povCode.controller, povCode.axisCode, PovDirection.southWest.ordinal());
                        var val2 = new ControllerValue(povCode.controller, povCode.axisCode, PovDirection.southEast.ordinal());
                        returnValue = controllerPovPressed.contains(val1, false) || controllerPovPressed.contains(val2, false);
                    }
                }
                
                return returnValue;
            }
        }
    
        public boolean isControllerPovPressed(ControllerValue... povCodes) {
            for (ControllerValue povCode : povCodes) {
                if (isControllerPovPressed(povCode)) {
                    return true;
                }
            }
            return false;
        }
    
        public boolean areAllControllerPovPressed(ControllerValue... povCodes) {
            for (ControllerValue povCode : povCodes) {
                if (!isControllerPovPressed(povCode)) {
                    return false;
                }
            }
            return true;
        }
    
        public boolean isAnyControllerPovPressed() {
            return controllerPovPressed.size > 0;
        }
    
        @Override
        public void connected(Controller controller) {
        
        }
    
        @Override
        public void disconnected(Controller controller) {

        }
    
        @Override
        public boolean buttonDown(Controller controller, int buttonCode) {
            ControllerValue controllerValue = new ControllerValue(controller, 0, buttonCode);
            controllerButtonsJustPressed.add(controllerValue);
            controllerButtonsPressed.add(controllerValue);
            return false;
        }
    
        @Override
        public boolean buttonUp(Controller controller, int buttonCode) {
            ControllerValue controllerValue = new ControllerValue(controller, 0, buttonCode);
            controllerButtonsPressed.removeValue(controllerValue, false);
            return false;
        }
    
        @Override
        public boolean axisMoved(Controller controller, int axisCode, float value) {
            int roundedValue = MathUtils.round(value);
            
            ControllerValue controllerValue = new ControllerValue(controller, axisCode, roundedValue);
            if (roundedValue != 0) {
                controllerAxisJustPressed.add(controllerValue);
            }
    
            Iterator<ControllerValue> iterator = controllerAxisPressed.iterator();
            while (iterator.hasNext()) {
                ControllerValue next = iterator.next();
                if (next.axisCode == axisCode) iterator.remove();
            }
            
            if (roundedValue != 0) {
                controllerAxisPressed.add(controllerValue);
            }
            return false;
        }
    
        @Override
        public boolean povMoved(Controller controller, int povCode, PovDirection value) {
            ControllerValue controllerValue = new ControllerValue(controller, povCode, value.ordinal());
    
            if (value == PovDirection.center || value != previousPov) {
                Iterator<ControllerValue> iterator = controllerPovPressed.iterator();
                while (iterator.hasNext()) {
                    ControllerValue next = iterator.next();
                    if (next.axisCode == povCode) iterator.remove();
                }
            }
            
            if (value != PovDirection.center) {
                controllerPovJustPressed.add(controllerValue);
                controllerPovPressed.add(controllerValue);
            }
            
            previousPov = value;
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
    }
    
    public static class ControllerValue {
        public Controller controller;
        public int axisCode;
        public int value;
    
        public ControllerValue() {
        }
    
        public ControllerValue(Controller controller, int axisCode, int value) {
            this.controller = controller;
            this.axisCode = axisCode;
            this.value = value;
        }
    
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ControllerValue that = (ControllerValue) o;
            return axisCode == that.axisCode &&
                    value == that.value &&
                    Objects.equals(that.controller, controller);
        }
    
        @Override
        public int hashCode() {
            return Objects.hash(axisCode, value);
        }
    }
}