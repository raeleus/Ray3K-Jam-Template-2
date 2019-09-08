package com.ray3k.template.entities;

import com.badlogic.gdx.utils.Array;

import java.util.Comparator;

public class EntityController {
    public Array<Entity> entities;
    public Comparator<Entity> depthComparator;
    private Array<Entity> sortedEntities;
    
    public EntityController() {
        entities = new Array<>();
        sortedEntities = new Array<>();
        
        depthComparator = (o1, o2) -> o2.depth - o1.depth;
    }
    
    public void add(Entity entity) {
        entities.add(entity);
        entity.create();
    }
    
    public void remove(Entity entity) {
        entities.removeValue(entity, false);
    }
    
    public void act(float delta) {
        sortedEntities.clear();
        sortedEntities.addAll(entities);
        sortedEntities.sort(depthComparator);
        
        for (Entity entity : sortedEntities) {
            entity.actBefore(delta);
        }
        
        //simulate physics and call act methods
        for (Entity entity : sortedEntities) {
            entity.deltaX += entity.gravityX * delta;
            entity.deltaY += entity.gravityY * delta;
            
            entity.x += entity.deltaX * delta;
            entity.y += entity.deltaY * delta;
    
            if (entity.skeleton != null) {
                entity.skeleton.setPosition(entity.x, entity.y);
                entity.animationState.update(delta);
                entity.skeleton.updateWorldTransform();
                entity.animationState.apply(entity.skeleton);
                entity.skeletonBounds.update(entity.skeleton, true);
            }
            
            entity.act(delta);
        }
    
        //call destroy methods and remove the entities
        for (Entity entity : sortedEntities) {
            if (entity.destroy) {
                entity.destroy();
                entities.removeValue(entity, false);
            }
        }
    }
    
    public void draw(float delta) {
        //call draw methods
        for (Entity entity : sortedEntities) {
            if (entity.visible) {
                if (entity.skeleton != null) {
                    //interpolate position
                    entity.skeleton.setPosition(entity.x + entity.deltaX * delta, entity.y + entity.deltaY * delta);
                    entity.skeleton.updateWorldTransform();
                    entity.animationState.apply(entity.skeleton);
                    
                    entity.skeletonRenderer.draw(entity.batch, entity.skeleton);
                }
                
                entity.draw(delta);
            }
        }
    }
}
