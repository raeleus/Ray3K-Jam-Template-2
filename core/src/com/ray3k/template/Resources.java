package com.ray3k.template;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;

public class Resources {
    public static Music bgm_music;
    public static Music bgm_audio_sample;
    
    public static void loadResources(AssetManager assetManager) {
        bgm_music = assetManager.get("bgm/menu.mp3");
        bgm_audio_sample = assetManager.get("bgm/audio-sample.mp3");
    }
}
