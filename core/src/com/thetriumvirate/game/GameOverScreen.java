package com.thetriumvirate.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

public final class GameOverScreen implements Screen {
	
	private static final int CAM_WIDTH = Main.WINDOW_WIDTH;
	private static final int CAM_HEIGHT = Main.WINDOW_HEIGHT;
	private static final int FONT_SIZE = 10;
	
	// Declare resource paths below
	// For example: private static final String RES_SOMETHING = "somewhere/something";
	private static final String RES_GAMEOVER_MUSIC = "audio/gameover.mp3";
	private static final String RES_VICTORY_MUSIC = "audio/victory.mp3";
	
	private final Main game;
	private final OrthographicCamera cam;
	
	// Declare resource variables below
	// For example: private final Texture testTexture;
	private final Music music;

	private CustomButton btnMenu;
	
	
	private boolean gameWon = false;
	
	
	public GameOverScreen(Main game, boolean gameWon) {
		// Initialize essentials
		this.game = game;
		cam = new OrthographicCamera();
		cam.setToOrtho(false, CAM_WIDTH, CAM_HEIGHT);
		cam.update();
		game.spritebatch.setProjectionMatrix(cam.combined);
		Gdx.gl.glClearColor(0, 0, 1, 1);//just to make an obvious difference
		
		// Initialize resource variables below
		// For example: testTexture = game.assetmanager.get(RES_SOMETEXTURE, Texture.class);
		music = game.assetmanager.get(gameWon ? RES_VICTORY_MUSIC : RES_GAMEOVER_MUSIC, Music.class);
		InputMultiplexer inputmultiplexer = new InputMultiplexer();
		
		this.btnMenu = new CustomButton(game, new Vector2(100, 100), "Main Menu", FONT_SIZE);
		inputmultiplexer.addProcessor(btnMenu);
		
		Gdx.input.setInputProcessor(inputmultiplexer);
		// Do everything else below
		this.gameWon = gameWon;

		
		
	}
	
	// Load all resources for this screen in prefetch !!!
	// For example: game.assetmanager.load(RES_SOMETHING, SomeType.class);
	// or			game.fontloader.load(RES_SOMETHING_FONT);
	// Unload all resources in dispose !!!
	public static void prefetch(Main game) {
		game.assetmanager.load(RES_GAMEOVER_MUSIC, Music.class);
		game.assetmanager.load(RES_VICTORY_MUSIC, Music.class);
		
		CustomButton.prefetch(game);
	}
	
	// Unload all resources for this screen
	// For example: game.assetmanager.unload(RES_SOMETHING);
	// For fonts: game.fontmanager.unload(RES_SOMETHING_FONT);
	@Override
	public void dispose() {
		game.assetmanager.unload(RES_GAMEOVER_MUSIC);
		game.assetmanager.unload(RES_VICTORY_MUSIC);
	}

	@Override
	public void show() {
		music.play();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		game.spritebatch.begin();
		
		btnMenu.render(game.spritebatch);
		
		game.spritebatch.end();
		}
	
	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void hide() {
		music.stop();
	}
}
