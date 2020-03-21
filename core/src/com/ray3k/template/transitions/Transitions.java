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
}
