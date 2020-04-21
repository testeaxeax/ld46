package com.thetriumvirate.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public final class GameOverScreen implements Screen {
	
	private static final int CAM_WIDTH = Main.WINDOW_WIDTH;
	private static final int CAM_HEIGHT = Main.WINDOW_HEIGHT;
	private static final int FONT_SIZE = 10;
	private static final float TEXT_WIDTH = 440f / 1024f;
	private static final float TEXT_HEIGHT = 80f/800f;
	
	
	// Declare resource paths below
	// For example: private static final String RES_SOMETHING = "somewhere/something";
	private static final String RES_GAMEOVER_MUSIC = "audio/gameover.wav";
	private static final String RES_VICTORY_MUSIC = "audio/victory.wav";
	private static final String RES_BTN_MENU_TEXTURE = "graphics/customBtn_menu.png";
  
	private static final String RES_BTN_WON_TEXTURE = "graphics/youWon.png";
	private static final String RES_BTN_LOST_TEXTURE = "graphics/youLost.png";
	private static final String RES_BACKGROUND = "graphics/gameoverbackground.png";
	
	private final Main game;
	private final OrthographicCamera cam;
	
	// Declare resource variables below
	// For example: private final Texture testTexture;
	private final Sound jingle;
	private final Music music;

	private CustomButton btnMenu;
	private TextureRegion[] btn_texReg;
	private final Texture btn_tex, tex_backGround;
	
	private Texture youWon_tex, youLost_tex;
	
	
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
		jingle = game.assetmanager.get(gameWon ? RES_VICTORY_MUSIC : RES_GAMEOVER_MUSIC, Sound.class);
		music = game.assetmanager.get(MenuScreen.RES_BACKGROUND_MUSIC, Music.class);
		this.music.setVolume(0.5f);
		
		InputMultiplexer inputmultiplexer = new InputMultiplexer();
		
		this.tex_backGround = game.assetmanager.get(RES_BACKGROUND, Texture.class);
		
		btn_tex = game.assetmanager.get(RES_BTN_MENU_TEXTURE, Texture.class);
		btn_texReg = new TextureRegion[2];
		btn_texReg[0] = new TextureRegion(btn_tex, 0, 0, Main.DEFAULT_BUTTON_WIDTH, Main.DEFAULT_BUTTON_HEIGHT);
		btn_texReg[1] = new TextureRegion(btn_tex, 0, Main.DEFAULT_BUTTON_HEIGHT, Main.DEFAULT_BUTTON_WIDTH, Main.DEFAULT_BUTTON_HEIGHT);
		
		this.btnMenu = new CustomButton(game, new Vector2((1024f - Main.DEFAULT_BUTTON_WIDTH - 40f) / 1024f * Main.WINDOW_WIDTH, (40f) / 800f * Main.WINDOW_HEIGHT), btn_texReg, "", FONT_SIZE);
		inputmultiplexer.addProcessor(btnMenu);
		
		
		this.youLost_tex = game.assetmanager.get(RES_BTN_LOST_TEXTURE, Texture.class);
		this.youWon_tex = game.assetmanager.get(RES_BTN_WON_TEXTURE, Texture.class);
		
		Gdx.input.setInputProcessor(inputmultiplexer);
		// Do everything else below
		this.gameWon = gameWon;

		
		
	}
	
	// Load all resources for this screen in prefetch !!!
	// For example: game.assetmanager.load(RES_SOMETHING, SomeType.class);
	// or			game.fontloader.load(RES_SOMETHING_FONT);
	// Unload all resources in dispose !!!
	public static void prefetch(Main game) {
		game.assetmanager.load(RES_GAMEOVER_MUSIC, Sound.class);
		game.assetmanager.load(RES_VICTORY_MUSIC, Sound.class);
		game.assetmanager.load(RES_BTN_MENU_TEXTURE, Texture.class);
		game.assetmanager.load(RES_BTN_WON_TEXTURE, Texture.class);
		game.assetmanager.load(RES_BTN_LOST_TEXTURE, Texture.class);
		game.assetmanager.load(RES_BACKGROUND, Texture.class);
		
		CustomButton.prefetch(game);
	}
	
	// Unload all resources for this screen
	// For example: game.assetmanager.unload(RES_SOMETHING);
	// For fonts: game.fontmanager.unload(RES_SOMETHING_FONT);
	@Override
	public void dispose() {
		game.assetmanager.unload(RES_GAMEOVER_MUSIC);
		game.assetmanager.unload(RES_VICTORY_MUSIC);
		game.assetmanager.unload(RES_BTN_MENU_TEXTURE);
		game.assetmanager.unload(RES_BTN_WON_TEXTURE);
		game.assetmanager.unload(RES_BTN_LOST_TEXTURE);
		game.assetmanager.unload(RES_BACKGROUND);
	}

	@Override
	public void show() {
		music.play();
		jingle.play();
	}

	@Override
	public void render(float delta) {
		
		// update part
		if(this.btnMenu.getClicked()) {
			this.game.screenmanager.pop();
		}
		
		// render part
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		Gdx.gl.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
		
		game.spritebatch.begin();
		
    game.spritebatch.draw(this.tex_backGround, 0, 0, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
		game.spritebatch.draw(gameWon ? youWon_tex : youLost_tex, (Main.WINDOW_WIDTH / 2f) - (TEXT_WIDTH * Main.WINDOW_WIDTH)/2, Main.WINDOW_HEIGHT / 8f * 5f, TEXT_WIDTH * Main.WINDOW_WIDTH, TEXT_HEIGHT * Main.WINDOW_HEIGHT);

		
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
		jingle.stop();
	}
}
