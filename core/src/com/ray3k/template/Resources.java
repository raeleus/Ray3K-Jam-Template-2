package com.ray3k.template;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.esotericsoftware.spine.Animation;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.SkeletonData;
import java.lang.String;

public class Resources {
    public static Skin skin_skin;

    public static TextureAtlas textures_textures;

    public static Sound sfx_ahh;

    public static Sound sfx_click;

    public static Sound sfx_libgdx;

    public static Sound sfx_pleaseDontKillMe;

    public static Sound sfx_shot;

    public static Sound sfx_swoosh;

    public static Sound sfx_tv;

    public static Music bgm_audioSample;

    public static Music bgm_menu;

    public static void loadResources(AssetManager assetManager) {
        skin_skin = assetManager.get("skin/skin.json");
        SpineLibgdx.skeletonData = assetManager.get("spine/libgdx.json");
        SpineLibgdx.animationData = assetManager.get("spine/libgdx.json-animation");
        SpineLibgdx.animationAnimation = SpineLibgdx.skeletonData.findAnimation("animation");
        SpineLibgdx.animationStand = SpineLibgdx.skeletonData.findAnimation("stand");
        SpineLibgdx.skinDefault = SpineLibgdx.skeletonData.findSkin("default");
        SpineRay3k.skeletonData = assetManager.get("spine/ray3k.json");
        SpineRay3k.animationData = assetManager.get("spine/ray3k.json-animation");
        SpineRay3k.animationAnimation = SpineRay3k.skeletonData.findAnimation("animation");
        SpineRay3k.animationStand = SpineRay3k.skeletonData.findAnimation("stand");
        SpineRay3k.skinDefault = SpineRay3k.skeletonData.findSkin("default");
        textures_textures = assetManager.get("textures/textures.atlas");
        sfx_ahh = assetManager.get("sfx/ahh.mp3");
        sfx_click = assetManager.get("sfx/click.mp3");
        sfx_libgdx = assetManager.get("sfx/libgdx.mp3");
        sfx_pleaseDontKillMe = assetManager.get("sfx/please don't kill me.mp3");
        sfx_shot = assetManager.get("sfx/shot.mp3");
        sfx_swoosh = assetManager.get("sfx/swoosh.mp3");
        sfx_tv = assetManager.get("sfx/tv.mp3");
        bgm_audioSample = assetManager.get("bgm/audio-sample.mp3");
        bgm_menu = assetManager.get("bgm/menu.mp3");
    }

    public static class SpineLibgdx {
        public static SkeletonData skeletonData;

        public static AnimationStateData animationData;

        public static Animation animationAnimation;

        public static Animation animationStand;

        public static com.esotericsoftware.spine.Skin skinDefault;
    }

    public static class SpineRay3k {
        public static SkeletonData skeletonData;

        public static AnimationStateData animationData;

        public static Animation animationAnimation;

        public static Animation animationStand;

        public static com.esotericsoftware.spine.Skin skinDefault;
    }

    public static class Values {
        public static float jumpVelocity = 10.0f;

        public static String name = "Raeleus";

        public static boolean godMode = true;

        public static int id = 10;

        public static Range speedLimitRange = new Range(0.0f, 10.0f);

        public static float speedLimit = 5.0f;
    }

    public static class Range {
        public float min;

        public float max;

        Range(float min, float max) {
            this.min = min;
            this.max = max;
        }
    }
}
