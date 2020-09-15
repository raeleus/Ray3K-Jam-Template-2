package com.ray3k.template;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.utils.SkeletonDrawable;

public class SpineDrawable extends SkeletonDrawable {
    public SpineDrawable() {
        calcMinSize();
    }
    
    public SpineDrawable(SkeletonRenderer renderer, Skeleton skeleton,
                         AnimationState state) {
        super(renderer, skeleton, state);
        calcMinSize();
    }
    
    private void calcMinSize() {
        setMinSize(getSkeleton().getData().getWidth(), getSkeleton().getData().getHeight());
    }
    
    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        getSkeleton().setScale(width / getMinWidth(), height / getMinHeight());
        super.draw(batch, x - getSkeleton().getData().getX(), y - getSkeleton().getData().getY(), width, height);
    }
}
