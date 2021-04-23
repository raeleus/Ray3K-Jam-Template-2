package com.ray3k.template.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.dongbat.jbump.CollisionFilter;
import com.dongbat.jbump.Collisions;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.Response.Result;
import com.esotericsoftware.spine.*;

import static com.ray3k.template.Core.*;

public abstract class Entity {
    private static final Vector2 temp1 = new Vector2();
    private static final Vector2 temp2 = new Vector2();
    public Skeleton skeleton;
    public AnimationState animationState;
    public SkeletonBounds skeletonBounds;
    public Item<Entity> item;
    public Collisions collisions;
    public CollisionFilter collisionFilter;
    public float x;
    public float y;
    public float bboxX;
    public float bboxY;
    public float bboxWidth;
    public float bboxHeight;
    public float deltaX;
    public float deltaY;
    public boolean destroy;
    public float gravityX;
    public float gravityY;
    public boolean visible;
    public float depth;
    public Color collisionBoxDebugColor;
    
    public abstract void create();
    public abstract void actBefore(float delta);
    public abstract void act(float delta);
    public abstract void draw(float delta);
    public abstract void destroy();
    public abstract void projectedCollision(Result result);
    public abstract void collision(Collisions collisions);
    
    public Entity() {
        visible = true;
    }
    
    public void setMotion(float speed, float direction) {
        temp1.set(speed, 0);
        temp1.rotateDeg(direction);
        deltaX = temp1.x;
        deltaY = temp1.y;
    }
    
    public void addMotion(float speed, float direction) {
        temp1.set(speed, 0);
        temp1.rotateDeg(direction);
        deltaX += temp1.x;
        deltaY += temp1.y;
    }
    
    public void moveTowards(float speed, float x, float y) {
        temp1.set(x, y);
        temp1.sub(this.x, this.y);
        setMotion(speed, temp1.angleDeg());
    }
    
    public void moveTowards(float speed, float x, float y, float delta) {
        temp1.set(x, y);
        temp1.sub(this.x, this.y);
        setMotion(Math.min(speed, temp1.len() / delta), temp1.angleDeg());
    }
    
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    public void setSpeed(float speed) {
        setMotion(speed, getDirection());
    }
    
    public float getSpeed() {
        temp1.set(deltaX, deltaY);
        return temp1.len();
    }
    
    public float getDirection() {
        temp1.set(deltaX, deltaY);
        return temp1.angleDeg();
    }
    
    public void setDirection(float direction) {
        setMotion(getSpeed(), direction);
    }
    
    public float getGravityDirection() {
        temp1.set(gravityX, gravityY);
        return temp1.angleDeg();
    }
    
    public float getGravitySpeed() {
        temp1.set(gravityX, gravityY);
        return temp1.len();
    }
    
    public void setGravity(float speed, float direction) {
        temp1.set(speed, 0);
        temp1.rotateDeg(direction);
        gravityX = temp1.x;
        gravityY = temp1.y;
    }
    
    public void addGravity(float speed, float direction) {
        temp1.set(speed, 0);
        temp1.rotateDeg(direction);
        gravityX += temp1.x;
        gravityY += temp1.y;
    }
    
    public void setSkeletonData(SkeletonData skeletonData, AnimationStateData animationStateData) {
        skeleton = new Skeleton(skeletonData);
        animationState = new AnimationState(animationStateData);
        skeletonBounds = new SkeletonBounds();
        
        skeleton.setPosition(x, y);
        skeleton.updateWorldTransform();
        animationState.apply(skeleton);
        skeletonBounds.update(skeleton, true);
    }
    
    public void setCollisionBox(float offsetX, float offsetY, float width, float height, CollisionFilter collisionFilter) {
        bboxX = offsetX;
        bboxY = offsetY;
        bboxWidth = width;
        bboxHeight = height;
        this.collisionFilter = collisionFilter;
        
        collisions = new Collisions();
        
        if (item == null) {
            item = new Item<>(this);
            world.add(item, x + bboxX, y + bboxY, width, height);
        } else {
            world.update(item, x + bboxX, y + bboxY, width, height);
        }
    }
    
    private static float[] verts;
    public void setCollisionBox(Slot slot, SkeletonBounds skeletonBounds, CollisionFilter collisionFilter) {
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = 0;
        float maxY = 0;
        for (var bbox : skeletonBounds.getBoundingBoxes()) {
            if (verts == null || verts.length < bbox.getWorldVerticesLength()) verts = new float[bbox.getWorldVerticesLength()];
            bbox.computeWorldVertices(slot, 0, bbox.getWorldVerticesLength(), verts, 0, 2);
            for (int i = 0; i < bbox.getWorldVerticesLength(); i += 2) {
                if (verts[i] < minX) minX = verts[i];
                if (verts[i] > maxX) maxX = verts[i];
                if (verts[i+1] < minY) minY = verts[i+1];
                if (verts[i+1] > maxY) maxY = verts[i+1];
            }
        }
        
        setCollisionBox(minX, minY, maxX - minX, maxY - minY, collisionFilter);
    }
    
    public boolean isOutside(float left, float bottom, float width, float height, float border) {
        return x < left - border || x > left + width + border || y < bottom - border || y > bottom + height + border;
    }
    
    public float getCollisionBoxLeft() {
        return x + bboxX;
    }
    
    public float getCollisionBoxRight() {
        return x + bboxX + bboxWidth;
    }
    
    public float getCollisionBoxBottom() {
        return y + bboxY;
    }
    
    public float getCollisionBoxTop() {
        return y + bboxY + bboxHeight;
    }
    
    public float getCollisionBoxCenterX() {
        return x + bboxX + bboxWidth / 2;
    }
    
    public float getCollisionBoxCenterY() {
        return y + bboxY + bboxHeight / 2;
    }
}