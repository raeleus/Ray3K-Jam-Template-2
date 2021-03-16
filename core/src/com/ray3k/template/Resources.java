package com.ray3k.template;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.esotericsoftware.spine.Animation;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.SkeletonData;

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
        LibgdxSpine.skeletonData = assetManager.get("spine/libgdx.json");
        LibgdxSpine.animationData = assetManager.get("spine/libgdx.json-animation");
        LibgdxSpine.animationAnimation = LibgdxSpine.skeletonData.findAnimation("animation");
        LibgdxSpine.standAnimation = LibgdxSpine.skeletonData.findAnimation("stand");
        LibgdxSpine.defaultSkin = LibgdxSpine.skeletonData.findSkin("default");
        Ray3kSpine.skeletonData = assetManager.get("spine/ray3k.json");
        Ray3kSpine.animationData = assetManager.get("spine/ray3k.json-animation");
        Ray3kSpine.animationAnimation = Ray3kSpine.skeletonData.findAnimation("animation");
        Ray3kSpine.standAnimation = Ray3kSpine.skeletonData.findAnimation("stand");
        Ray3kSpine.defaultSkin = Ray3kSpine.skeletonData.findSkin("default");
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

    public static class LibgdxSpine {
        public static SkeletonData skeletonData;

        public static AnimationStateData animationData;

        public static Animation animationAnimation;

        public static Animation standAnimation;

        public static com.esotericsoftware.spine.Skin defaultSkin;
    }

    public static class Ray3kSpine {
        public static SkeletonData skeletonData;

        public static AnimationStateData animationData;

        public static Animation animationAnimation;

        public static Animation standAnimation;

        public static com.esotericsoftware.spine.Skin defaultSkin;
    }
}
