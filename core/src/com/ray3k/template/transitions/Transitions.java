package com.ray3k.template.transitions;

import com.badlogic.gdx.graphics.Color;

public class Transitions {
    public static Transition crossFade() {
        return new TransitionCrossFade();
    }
    
    public static Transition colorFade(Color backgroundColor) {
        return new TransitionColorFade(backgroundColor);
    }
}
