package com.ray3k.template.transitions;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;

public class Transitions {
    public static TransitionCrossFade crossFade() {
        return new TransitionCrossFade();
    }
    
    public static TransitionColorFade colorFade(Color backgroundColor) {
        return new TransitionColorFade(backgroundColor);
    }
    
    public static TransitionPush push(float toDirection, Color backgroundColor, Interpolation interpolation) {
        return new TransitionPush(toDirection, backgroundColor, interpolation);
    }
    
    public static TransitionPush push(float toDirection, Color backgroundColor) {
        return push(toDirection, backgroundColor, Interpolation.linear);
    }
    
    public static TransitionSlide slide(float toDirection, Interpolation interpolation) {
        return new TransitionSlide(toDirection, interpolation);
    }
    
    public static TransitionSlide slide(float toDirection) {
        return slide(toDirection, Interpolation.linear);
    }
    
    public static TransitionWipe wipe(float toDirection, Interpolation interpolation) {
        return new TransitionWipe(toDirection, interpolation);
    }
    
    public static TransitionWipe wipe(float toDirection) {
        return wipe(toDirection, Interpolation.linear);
    }
}
