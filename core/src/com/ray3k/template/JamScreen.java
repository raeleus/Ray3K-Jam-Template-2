package com.ray3k.template;

import com.badlogic.gdx.*;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectIntMap.Entry;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ray3k.template.Core.Binding;

import java.util.Arrays;

public abstract class JamScreen extends ScreenAdapter implements InputProcessor, ControllerListener {
    public Viewport viewport;
    public OrthographicCamera camera;
    public float mouseX;
    public float mouseY;
    public IntArray keysJustPressed = new IntArray();
    public IntArray buttonsJustPressed = new IntArray();
    public IntArray buttonsPressed = new IntArray();
    public IntArray scrollJustPressed = new IntArray();
    public IntArray controllerButtonsJustPressed = new IntArray();
    public IntArray controllerButtonsPressed = new IntArray();
    public IntArray controllerAxisJustPressed = new IntArray();
    public IntArray controllerAxisPressed = new IntArray();
    public IntArray controllerPovJustPressed = new IntArray();
    public IntArray controllerPovPressed = new IntArray();
    private static final Vector3 tempVector3 = new Vector3();
    public final static ObjectIntMap<Core.Binding> keyBindings = new ObjectIntMap<>();
    public final static ObjectIntMap<Core.Binding> buttonBindings = new ObjectIntMap<>();
    public final static ObjectIntMap<Core.Binding> scrollBindings = new ObjectIntMap<>();
    public final static ObjectIntMap<Core.Binding> controllerButtonBindings = new ObjectIntMap<>();
    public final static ObjectIntMap<Core.Binding> controllerAxisBindings = new ObjectIntMap<>();
    public final static ObjectIntMap<Core.Binding> controllerPovBindings = new ObjectIntMap<>();
    public final static ObjectSet<Core.Binding> unboundBindings = new ObjectSet<>();
    public final static Array<Core.Binding> bindings = new Array<>();
    public final static int ANY_BUTTON = -1;
    public final static int SCROLL_UP = -1;
    public final static int SCROLL_DOWN = 1;
    public final static int ANY_SCROLL = 0;
    public final static int ANY_CONTROLLER_BUTTON = -1;
    public final static int ANY_CONTROLLER_AXIS = -1;
    public final static int ANY_CONTROLLER_POV = 0;
    
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
        controllerButtonsJustPressed.clear();
        controllerAxisJustPressed.clear();
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
    
    public boolean isControllerButtonJustPressed(int buttonCode) {
        return buttonCode == ANY_CONTROLLER_BUTTON ? controllerButtonsJustPressed.size > 0 : controllerButtonsJustPressed.contains(buttonCode);
    }
    
    public boolean isControllerAxisJustPressed(int axisCode) {
        return axisCode == ANY_CONTROLLER_AXIS ? controllerAxisJustPressed.size > 0 : controllerAxisJustPressed.contains(axisCode);
    }
    
    public boolean isControllerPovJustPressed(int povCode) {
        return povCode == ANY_CONTROLLER_POV ? controllerPovJustPressed.size > 0 : controllerPovJustPressed.contains(povCode);
    }
    
    public boolean isKeyJustPressed(int... keys) {
        for (int key : keys) {
            if (isKeyJustPressed(key)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isControllerButtonJustPressed(int... controllerButtons) {
        for (int controllerButton : controllerButtons) {
            if (isControllerButtonJustPressed(controllerButton)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isControllerAxisJustPressed(int... axisCodes) {
        for (int axisCode : axisCodes) {
            if (isControllerAxisJustPressed(axisCode)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isControllerPovJustPressed(int... povCodes) {
        for (int povCode : povCodes) {
            if (isControllerPovJustPressed(povCode)) {
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
    
    public boolean isControllerButtonPressed(int buttonCode) {
        return buttonCode == ANY_CONTROLLER_BUTTON ? controllerButtonsPressed.size > 0 : controllerButtonsPressed.contains(buttonCode);
    }
    
    public boolean isControllerAxisPressed(int axisCode) {
        return axisCode == ANY_CONTROLLER_AXIS ? controllerAxisPressed.size > 0 : controllerAxisPressed.contains(axisCode);
    }
    
    public boolean isControllerPovPressed(int povCode) {
        return povCode == ANY_CONTROLLER_POV ? controllerPovPressed.size > 0 : controllerPovPressed.contains(povCode);
    }
    
    public boolean isAnyKeyPressed(int... keys) {
        for (int key : keys) {
            if (isKeyPressed(key)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isAnyControllerButtonPressed(int... buttonCodes) {
        for (int buttonCode : buttonCodes) {
            if (isControllerButtonPressed(buttonCode)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isAnyControllerAxisPressed(int... axisCodes) {
        for (int axisCode : axisCodes) {
            if (isControllerAxisPressed(axisCode)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isAnyControllerPovPressed(int... povCodes) {
        for (int povCode : povCodes) {
            if (isControllerPovPressed(povCode)) {
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
    
    public boolean areAllControllerButtonsPressed(int... buttonCodes) {
        for (int buttonCode : buttonCodes) {
            if (!isControllerButtonPressed(buttonCode)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean areAllControllerAxisPressed(int... axisCodes) {
        for (int axisCode : axisCodes) {
            if (!isControllerAxisPressed(axisCode)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean areAllControllerPovPressed(int... povCodes) {
        for (int povCode : povCodes) {
            if (!isControllerPovPressed(povCode)) {
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
        } else if (controllerButtonBindings.containsKey(binding)) {
            return isControllerButtonPressed(controllerButtonBindings.get(binding, ANY_CONTROLLER_BUTTON));
        } else if (controllerAxisBindings.containsKey(binding)) {
            return isControllerAxisPressed(controllerAxisBindings.get(binding, ANY_CONTROLLER_AXIS));
        } else if (controllerPovBindings.containsKey(binding)) {
            return isControllerPovPressed(controllerPovBindings.get(binding, ANY_CONTROLLER_POV));
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
        controllerButtonBindings.clear();
        controllerAxisBindings.clear();
        controllerPovBindings.clear();
        unboundBindings.clear();
        bindings.clear();
    }
    
    public static void addKeyBinding(Core.Binding binding, int key) {
        buttonBindings.remove(binding, ANY_BUTTON);
        scrollBindings.remove(binding, ANY_SCROLL);
        controllerButtonBindings.remove(binding, ANY_CONTROLLER_BUTTON);
        controllerAxisBindings.remove(binding, ANY_CONTROLLER_AXIS);
        controllerPovBindings.remove(binding, ANY_CONTROLLER_POV);
        unboundBindings.remove(binding);
        keyBindings.put(binding, key);
        if (!bindings.contains(binding, true)) {
            bindings.add(binding);
        }
    }
    
    public static void addButtonBinding(Core.Binding binding, int button) {
        keyBindings.remove(binding, Input.Keys.ANY_KEY);
        scrollBindings.remove(binding, ANY_SCROLL);
        controllerButtonBindings.remove(binding, ANY_CONTROLLER_BUTTON);
        controllerAxisBindings.remove(binding, ANY_CONTROLLER_AXIS);
        controllerPovBindings.remove(binding, ANY_CONTROLLER_POV);
        unboundBindings.remove(binding);
        buttonBindings.put(binding, button);
        if (!bindings.contains(binding, true)) {
            bindings.add(binding);
        }
    }
    
    public static void addScrollBinding(Core.Binding binding, int scroll) {
        keyBindings.remove(binding, Input.Keys.ANY_KEY);
        buttonBindings.remove(binding, ANY_BUTTON);
        controllerButtonBindings.remove(binding, ANY_CONTROLLER_BUTTON);
        controllerAxisBindings.remove(binding, ANY_CONTROLLER_AXIS);
        controllerPovBindings.remove(binding, ANY_CONTROLLER_POV);
        unboundBindings.remove(binding);
        scrollBindings.put(binding, scroll);
        if (!bindings.contains(binding, true)) {
            bindings.add(binding);
        }
    }
    
    public static void addControllerButtonBinding(Core.Binding binding, int buttonCode) {
        keyBindings.remove(binding, Input.Keys.ANY_KEY);
        buttonBindings.remove(binding, ANY_BUTTON);
        scrollBindings.remove(binding, ANY_SCROLL);
        unboundBindings.remove(binding);
        controllerAxisBindings.remove(binding, ANY_CONTROLLER_AXIS);
        controllerPovBindings.remove(binding, ANY_CONTROLLER_POV);
        controllerButtonBindings.put(binding, buttonCode);
        if (!bindings.contains(binding, true)) {
            bindings.add(binding);
        }
    }
    
    public static void addControllerAxisBinding(Core.Binding binding, int axisCode) {
        keyBindings.remove(binding, Input.Keys.ANY_KEY);
        buttonBindings.remove(binding, ANY_BUTTON);
        scrollBindings.remove(binding, ANY_SCROLL);
        unboundBindings.remove(binding);
        controllerButtonBindings.remove(binding, ANY_CONTROLLER_BUTTON);
        controllerPovBindings.remove(binding, ANY_CONTROLLER_POV);
        controllerAxisBindings.put(binding, axisCode);
        if (!bindings.contains(binding, true)) {
            bindings.add(binding);
        }
    }
    
    public static void addControllerPovBinding(Core.Binding binding, int povCode) {
        keyBindings.remove(binding, Input.Keys.ANY_KEY);
        buttonBindings.remove(binding, ANY_BUTTON);
        scrollBindings.remove(binding, ANY_SCROLL);
        unboundBindings.remove(binding);
        controllerButtonBindings.remove(binding, ANY_CONTROLLER_BUTTON);
        controllerAxisBindings.remove(binding, ANY_CONTROLLER_AXIS);
        controllerPovBindings.put(binding, povCode);
        if (!bindings.contains(binding, true)) {
            bindings.add(binding);
        }
    }
    
    public static void addUnboundBinding(Core.Binding binding) {
        keyBindings.remove(binding, Input.Keys.ANY_KEY);
        buttonBindings.remove(binding, ANY_BUTTON);
        scrollBindings.remove(binding, ANY_SCROLL);
        controllerButtonBindings.remove(binding, ANY_CONTROLLER_BUTTON);
        controllerAxisBindings.remove(binding, ANY_CONTROLLER_AXIS);
        controllerPovBindings.remove(binding, ANY_CONTROLLER_POV);
        unboundBindings.add(binding);
        if (!bindings.contains(binding, true)) {
            bindings.add(binding);
        }
    }
    
    public static void removeBinding(Core.Binding binding) {
        keyBindings.remove(binding, Input.Keys.ANY_KEY);
        buttonBindings.remove(binding, ANY_BUTTON);
        scrollBindings.remove(binding, ANY_SCROLL);
        controllerButtonBindings.remove(binding, ANY_CONTROLLER_BUTTON);
        controllerAxisBindings.remove(binding, ANY_CONTROLLER_AXIS);
        controllerPovBindings.remove(binding, ANY_CONTROLLER_POV);
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
    
    public static boolean hasControllerButtonBinding(Core.Binding binding) {
        return controllerButtonBindings.containsKey(binding);
    }
    
    public static boolean hasControllerAxisBinding(Core.Binding binding) {
        return controllerAxisBindings.containsKey(binding);
    }
    
    public static boolean hasControllerPovBinding(Core.Binding binding) {
        return controllerPovBindings.containsKey(binding);
    }
    
    public static boolean hasUnboundBinding(Core.Binding binding) {
        return unboundBindings.contains(binding);
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
    
    public static int getControllerButtonBinding(Core.Binding binding) {
        return controllerButtonBindings.get(binding, ANY_CONTROLLER_BUTTON);
    }
    
    public static int getControllerAxisBinding(Core.Binding binding) {
        return controllerAxisBindings.get(binding, ANY_CONTROLLER_AXIS);
    }
    
    public static int getControllerPovBinding(Core.Binding binding) {
        return controllerPovBindings.get(binding, ANY_CONTROLLER_POV);
    }
    
    public static int getBinding(Core.Binding binding) {
        if (keyBindings.containsKey(binding)) {
            return getKeyBinding(binding);
        } else if (buttonBindings.containsKey(binding)) {
            return getButtonBinding(binding);
        } else if (controllerButtonBindings.containsKey(binding)) {
            return getControllerButtonBinding(binding);
        } else if (controllerAxisBindings.containsKey(binding)) {
            return getControllerAxisBinding(binding);
        } else if (controllerPovBindings.containsKey(binding)) {
            return getControllerPovBinding(binding);
        } else {
            return getScrollBinding(binding);
        }
    }
    
    public static void saveBindings() {
        Preferences pref = Core.core.preferences;
        for (Entry<Binding> binding : keyBindings) {
            pref.putInteger("key:" + binding.key.toString(), binding.value);
            pref.remove("button:" + binding.key.toString());
            pref.remove("scroll:" + binding.key.toString());
            pref.remove("controllerbutton:" + binding.key.toString());
            pref.remove("controlleraxis:" + binding.key.toString());
            pref.remove("controllerpov:" + binding.key.toString());
            pref.remove("unbound:" + binding.key.toString());
        }
        
        for (Entry<Binding> binding : buttonBindings) {
            pref.putInteger("button:" + binding.key.toString(), binding.value);
            pref.remove("key:" + binding.key.toString());
            pref.remove("scroll:" + binding.key.toString());
            pref.remove("controllerbutton:" + binding.key.toString());
            pref.remove("controlleraxis:" + binding.key.toString());
            pref.remove("controllerpov:" + binding.key.toString());
            pref.remove("unbound:" + binding.key.toString());
        }
        
        for (Entry<Binding> binding : scrollBindings) {
            pref.putInteger("scroll:" + binding.key.toString(), binding.value);
            pref.remove("key:" + binding.key.toString());
            pref.remove("button:" + binding.key.toString());
            pref.remove("controllerbutton:" + binding.key.toString());
            pref.remove("controlleraxis:" + binding.key.toString());
            pref.remove("controllerpov:" + binding.key.toString());
            pref.remove("unbound:" + binding.key.toString());
        }
        
        for (Entry<Binding> binding : controllerButtonBindings) {
            pref.putInteger("controllerbutton:" + binding.key.toString(), binding.value);
            pref.remove("key:" + binding.key.toString());
            pref.remove("button:" + binding.key.toString());
            pref.remove("scroll:" + binding.key.toString());
            pref.remove("controlleraxis:" + binding.key.toString());
            pref.remove("controllerpov:" + binding.key.toString());
            pref.remove("unbound:" + binding.key.toString());
        }
    
        for (Entry<Binding> binding : controllerAxisBindings) {
            pref.putInteger("controlleraxis:" + binding.key.toString(), binding.value);
            pref.remove("key:" + binding.key.toString());
            pref.remove("button:" + binding.key.toString());
            pref.remove("scroll:" + binding.key.toString());
            pref.remove("controllerbutton:" + binding.key.toString());
            pref.remove("controllerpov:" + binding.key.toString());
            pref.remove("unbound:" + binding.key.toString());
        }
    
        for (Entry<Binding> binding : controllerPovBindings) {
            pref.putInteger("controllerpov:" + binding.key.toString(), binding.value);
            pref.remove("key:" + binding.key.toString());
            pref.remove("button:" + binding.key.toString());
            pref.remove("scroll:" + binding.key.toString());
            pref.remove("controllerbutton:" + binding.key.toString());
            pref.remove("controlleraxis:" + binding.key.toString());
            pref.remove("unbound:" + binding.key.toString());
        }
        
        for (Binding binding : unboundBindings) {
            pref.putBoolean("unbound:" + binding.toString(), true);
            pref.remove("key:" + binding.toString());
            pref.remove("button:" + binding.toString());
            pref.remove("scroll:" + binding.toString());
            pref.remove("controllerbutton:" + binding.toString());
            pref.remove("controlleraxis:" + binding.toString());
            pref.remove("controllerpov:" + binding.toString());
        }
        pref.flush();
    }
    
    public static void loadBindings() {
        Preferences pref = Core.core.preferences;
        for (Binding binding : bindings) {
            String key = "key:" + binding.toString();
            if (pref.contains(key)) {
                JamScreen.addKeyBinding(binding, pref.getInteger(key));
            }
    
            key = "button:" + binding.toString();
            if (pref.contains(key)) {
                JamScreen.addButtonBinding(binding, pref.getInteger(key));
            }
    
            key = "scroll:" + binding.toString();
            if (pref.contains(key)) {
                JamScreen.addScrollBinding(binding, pref.getInteger(key));
            }
            
            key = "controllerbutton:" + binding.toString();
            if (pref.contains(key)) {
                JamScreen.addControllerButtonBinding(binding, pref.getInteger(key));
            }
    
            key = "controlleraxis:" + binding.toString();
            if (pref.contains(key)) {
                JamScreen.addControllerAxisBinding(binding, pref.getInteger(key));
            }
    
            key = "controllerpov:" + binding.toString();
            if (pref.contains(key)) {
                JamScreen.addControllerPovBinding(binding, pref.getInteger(key));
            }
    
            key = "unbound:" + binding.toString();
            if (pref.contains(key)) {
                JamScreen.addUnboundBinding(binding);
            }
        }
    }
    
    @Override
    public void connected(Controller controller) {
    
    }
    
    @Override
    public void disconnected(Controller controller) {
    
    }
    
    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        controllerButtonsJustPressed.add(buttonCode);
        controllerButtonsPressed.add(buttonCode);
        return false;
    }
    
    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        controllerButtonsPressed.removeValue(buttonCode);
        return false;
    }
    
    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        if (MathUtils.isEqual(value, 1) || MathUtils.isEqual(value, -1)) {
            int code = Integer.parseInt("" + MathUtils.round(value) + axisCode);
            controllerAxisJustPressed.add(code);
            controllerAxisPressed.add(code);
        } else {
            int code = (Integer.parseInt("" + -1 + axisCode));
            controllerAxisPressed.removeValue(code);
            code = (Integer.parseInt("" + 1 + axisCode));
            controllerAxisPressed.removeValue(code);
        }
        return false;
    }
    
    @Override
    public boolean povMoved(Controller controller, int povCode, PovDirection value) {
        if (value != PovDirection.center) {
            int code = Integer.parseInt("" + value.ordinal() + povCode);
            controllerPovJustPressed.add(code);
            controllerPovPressed.add(code);
        } else {
            for (PovDirection povDirection : PovDirection.values()) {
                int code = (Integer.parseInt("" + povDirection.ordinal() + povCode));
                controllerPovPressed.removeValue(code);
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
}