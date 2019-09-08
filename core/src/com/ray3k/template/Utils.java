package com.ray3k.template;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;

public class Utils {
    public static Array<Actor> getActorsRecursive(Actor actor) {
        Array<Actor> actors = new Array<>();
        if (actor instanceof Group) {
            actors.addAll(((Group) actor).getChildren());
            
            for (int i = 0; i < ((Group) actor).getChildren().size; i++) {
                Actor child = ((Group) actor).getChild(i);
                Array<Actor> newActors = getActorsRecursive(child);
                actors.addAll(newActors);
            }
        }
        
        return actors;
    }
}
