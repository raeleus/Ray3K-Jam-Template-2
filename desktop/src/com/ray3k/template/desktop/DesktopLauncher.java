package com.ray3k.template.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.ray3k.template.Core;
import com.ray3k.template.CrossPlatformWorker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;

import static com.ray3k.template.Core.*;

public class DesktopLauncher implements CrossPlatformWorker {
	public static void main (String[] args) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(1024, 576);
		config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 3);
		Core core = new Core();
		crossPlatformWorker = new DesktopLauncher();
		new Lwjgl3Application(core, config);
	}
}
