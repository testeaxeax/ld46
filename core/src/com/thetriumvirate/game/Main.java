package com.thetriumvirate.game;

import java.util.Random;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Main extends Game {
	
	// Globally used constants
	
	public static int WINDOW_HEIGHT = 800;
	public static int WINDOW_WIDTH = 1024;
	
	public static final Color DEFAULT_FONT_COLOR = Color.BLACK;
	// DO NOT append suffix, for example "fonts/replaceme" instead of "fonts/replaceme.ttf"
	// TODO Set default font
	public static final String RES_DEFAULT_FONT = "fonts/OpenSans-SemiBold";
	public static final Random RAND = new Random();
	
	// Window ratio
	public static final float WINDOW_RATIO = (float) WINDOW_WIDTH / (float) WINDOW_HEIGHT;
	public static final int DEFAULT_BUTTON_WIDTH = (int) ((128f/1024f) * WINDOW_WIDTH);
	public static final int DEFAULT_BUTTON_HEIGHT = (int) ((64f/800f) * WINDOW_HEIGHT);
	public static final int DEFAULT_FONTSIZE = (int) (WINDOW_WIDTH * 0.025f);
	
	public final FontLoader fontloader;
	
	// Globally used variables required for management and rendering
	public SpriteBatch spritebatch;
	public AdvancedAssetManager assetmanager;
	public ScreenManager screenmanager;
	
	public Main(FontLoader fl) {
		fontloader = fl;
		fontloader.setGame(this);
	}
	
	// Required to trigger rendering of active screen
	@Override
	public void render() {
		super.render();
	}
	
	@Override
	public void create() {
		spritebatch = new SpriteBatch();
		assetmanager = new AdvancedAssetManager();
		screenmanager = new ScreenManager(this);
		screenmanager.push(new SplashScreen(this));
	}
	
	@Override
	public void dispose() {
		screenmanager.dispose();
		spritebatch.dispose();
		assetmanager.dispose();
	}
}
