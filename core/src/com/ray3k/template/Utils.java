package com.ray3k.template;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ShortArray;
import com.esotericsoftware.spine.SkeletonBounds;
import regexodus.Matcher;
import regexodus.Pattern;

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
    
    private static EarClippingTriangulator earClippingTriangulator = new EarClippingTriangulator();
    private static FloatArray floatArray = new FloatArray();
    
    public static float[] skeletonBoundsToTriangles(SkeletonBounds skeletonBounds) {
        floatArray.clear();
        
        for (FloatArray points : skeletonBounds.getPolygons()) {
            ShortArray shortArray = earClippingTriangulator.computeTriangles(points);
            for (int i = 0; i < shortArray.size; i++) {
                floatArray.add(shortArray.get(i));
            }
        }
        return floatArray.items;
    }
    
    public static Color inverseColor(Color color) {
        return new Color(1 - color.r, 1 - color.g, 1 - color.b, color.a);
    }
    
    public static Color blackOrWhiteBgColor(Color color) {
        return brightness(color) > .5f ? new Color(Color.BLACK) : new Color(Color.WHITE);
    }
    
    public static float brightness(Color color) {
        return (float) (Math.sqrt(0.299f * Math.pow(color.r, 2) + 0.587 * Math.pow(color.g, 2) + 0.114 * Math.pow(color.b, 2)));
    }
    
    public static int colorToInt(Color color) {
        return ((int)(255 * color.r) << 24) | ((int)(255 * color.g) << 16) | ((int)(255 * color.b) << 8) | ((int)(255 * color.a));
    }
    
    public static float floorPot(float value) {
        float returnValue = 0.0f;
        for (float newValue = 2.0f; newValue < value; newValue *= 2.0f) {
            returnValue = newValue;
        }
        
        return returnValue;
    }
    
    public static Pixmap textureRegionToPixmap(TextureRegion textureRegion) {
        Texture texture = textureRegion.getTexture();
        if (!texture.getTextureData().isPrepared()) {
            texture.getTextureData().prepare();
        }
        
        Pixmap pixmap = texture.getTextureData().consumePixmap();
        Pixmap returnValue = new Pixmap(textureRegion.getRegionWidth(), textureRegion.getRegionHeight(), Pixmap.Format.RGBA8888);
        returnValue.setBlending(Pixmap.Blending.None);
        
        for (int x = 0; x < textureRegion.getRegionWidth(); x++) {
            for (int y = 0; y < textureRegion.getRegionHeight(); y++) {
                int colorInt = pixmap.getPixel(textureRegion.getRegionX() + x, textureRegion.getRegionY() + y);
                returnValue.drawPixel(x, y, colorInt);
            }
        }
        
        pixmap.dispose();
        
        return returnValue;
    }
    
    public static Cursor textureRegionToCursor(TextureRegion textureRegion, int xHotspot, int yHotspot) {
        return Gdx.graphics.newCursor(textureRegionToPixmap(textureRegion), xHotspot, yHotspot);
    }
    
    private static Pattern fileNamePattern = new Pattern("([^/.]+)(?:\\.?[^/.])*$");
    public static String fileName(String path) {
        Matcher matcher = fileNamePattern.matcher(path);
        matcher.find();
        return matcher.group(1);
    }
    
    public static String MouseButtonToString(int button) {
        String returnValue = "Unknown";
        switch (button) {
            case Input.Buttons.LEFT:
                returnValue = "Left Click";
                break;
            case Input.Buttons.RIGHT:
                returnValue = "Right Click";
                break;
            case Input.Buttons.MIDDLE:
                returnValue = "Middle Click";
                break;
            case Input.Buttons.BACK:
                returnValue = "Back Button";
                break;
            case Input.Buttons.FORWARD:
                returnValue = "Forward Button";
                break;
        }
        
        return returnValue;
    }
    
    private static final Vector2 temp1 = new Vector2();
    
    public static float pointDistance(float x1, float y1, float x2, float y2) {
        temp1.set(x1, y1);
        return temp1.dst(x2, y2);
    }
    
    public static float pointDirection(float x1, float y1, float x2, float y2) {
        temp1.set(x2, y2);
        temp1.sub(x1, y1);
        return temp1.angle();
    }
    
    public static float approach(float start, float target, float increment) {
        increment = Math.abs(increment);
        if (start < target) {
            start += increment;
            
            if (start > target) {
                start = target;
            }
        } else {
            start -= increment;
            
            if (start < target) {
                start = target;
            }
        }
        return start;
    }
    
    public static float approach360(float start, float target, float increment) {
        float delta = ((target - start + 360 + 180) % 360) - 180;
        return (start + Math.signum(delta) * MathUtils.clamp(increment, 0.0f, Math.abs(delta)) + 360) % 360;
    }
    
    public static boolean isEqual360(float a, float b, float tolerance) {
        return MathUtils.isZero((a - b + 180 + 360) % 360 - 180, tolerance);
    }
    
    public static boolean isEqual360(float a, float b) {
        return isEqual360(a, b, MathUtils.FLOAT_ROUNDING_ERROR);
    }
    
    public static boolean rayIntersectRectangle(float x, float y, float direction, Rectangle rectangle, Vector3 intersection) {
        rectToBoundingBox(rectangle, bboxTemp);
        
        temp1.set(1,0);
        temp1.rotate(direction);
        
        rayTemp.set(x, y, 0, temp1.x, temp1.y, 0);
        return Intersector.intersectRayBounds(rayTemp, bboxTemp, intersection);
    }
    
    public static Rectangle setRectToSkeletonBounds(Rectangle rectangle, SkeletonBounds skeletonBounds) {
        rectangle.x = skeletonBounds.getMinX();
        rectangle.y = skeletonBounds.getMinY();
        rectangle.width = skeletonBounds.getWidth();
        rectangle.height = skeletonBounds.getHeight();
        return rectangle;
    }
    
    private static final Vector3 v3Temp1 = new Vector3();
    private static final Vector3 v3Temp2 = new Vector3();
    public static BoundingBox rectToBoundingBox(Rectangle rectangle, BoundingBox boundingBox) {
        v3Temp1.set(rectangle.x, rectangle.y, 0);
        v3Temp2.set(rectangle.x + rectangle.width, rectangle.y + rectangle.height, 0);
        boundingBox.set(v3Temp1, v3Temp2);
        return boundingBox;
    }
    
    private static final BoundingBox bboxTemp = new BoundingBox();
    private static final Ray rayTemp = new Ray();
}
