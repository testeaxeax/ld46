package com.thetriumvirate.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;

public final class MenuScreen implements Screen {
	
	private static final int CAM_WIDTH = Main.WINDOW_WIDTH;
	private static final int CAM_HEIGHT = Main.WINDOW_HEIGHT;
	private static final int FONT_SIZE = 20;
	
	// Declare resource paths below
	// For example: private static final String RES_SOMETHING = "somewhere/something";
	private static final String RES_BACKGROUND = "graphics/menu-background.png";
	// This is shared with CreditsScreen
	public static final String RES_BACKGROUND_MUSIC = "audio/menu-music.mp3";
	
	private final Main game;
	private final OrthographicCamera cam;
	private final CustomButton creditsBtn, easyBtn, moderateBtn, difficultBtn;
	private final InputMultiplexer multiplexer;
	
	// Declare resource variables below
	// For example: private final Texture testTexture;
	private final Texture background;
	private final Music music;
	
	
	public MenuScreen(Main game) {
		// Initialize essentials
		this.game = game;
		cam = new OrthographicCamera();
		cam.setToOrtho(false, CAM_WIDTH, CAM_HEIGHT);
		cam.update();
		game.spritebatch.setProjectionMatrix(cam.combined);
		Gdx.gl.glClearColor(1, 0, 0, 1);
		
		// Initialize resource variables below
		// For example: testTexture = game.assetmanager.get(RES_SOMETEXTURE, Texture.class);
		background = game.assetmanager.get(RES_BACKGROUND, Texture.class);
		music = game.assetmanager.get(RES_BACKGROUND_MUSIC, Music.class);
		music.setLooping(true);
		
		// Do everything else below
		creditsBtn = new CustomButton(game, new Vector2(0, 0), "Credits", FONT_SIZE);
		easyBtn = new CustomButton(game, new Vector2(0, 0), "Easy", FONT_SIZE);
		moderateBtn = new CustomButton(game, new Vector2(0, 0), "Moderate", FONT_SIZE);
		difficultBtn = new CustomButton(game, new Vector2(0, 0), "Difficult", FONT_SIZE);
		
		// Buttons use the same Texture
		int btnheight = creditsBtn.getHeight();
		int btnwidth = creditsBtn.getWidth();
		int difficultyxpos = (Main.WINDOW_WIDTH / 2) - (btnwidth / 2);
		int difficultyyposspace = 10;
		int difficultyyposstart = (Main.WINDOW_HEIGHT / 2) - (btnheight / 2) + btnheight + difficultyyposspace;
		
		creditsBtn.setPosition(new Vector2(0, 0));
		easyBtn.setPosition(new Vector2(difficultyxpos, difficultyyposstart));
		moderateBtn.setPosition(new Vector2(difficultyxpos, difficultyyposstart - btnheight - difficultyyposspace));
		difficultBtn.setPosition(new Vector2(difficultyxpos, difficultyyposstart - 2 * btnheight - (2 * difficultyyposspace)));
		multiplexer = new InputMultiplexer();
		
		multiplexer.addProcessor(creditsBtn);
		multiplexer.addProcessor(difficultBtn);
		multiplexer.addProcessor(easyBtn);
		multiplexer.addProcessor(moderateBtn);
		
		Gdx.input.setInputProcessor(multiplexer);
	}
	
	// Load all resources for this screen in prefetch !!!
	// For example: game.assetmanager.load(RES_SOMETHING, SomeType.class);
	// or			game.fontloader.load(RES_SOMETHING_FONT);
	// Unload all resources in dispose !!!
	public static void prefetch(Main game) {
		game.assetmanager.load(RES_BACKGROUND, Texture.class);
		game.assetmanager.load(RES_BACKGROUND_MUSIC, Music.class);
	}
	
	// Unload all resources for this screen
	// For example: game.assetmanager.unload(RES_SOMETHING);
	// For fonts: game.fontmanager.unload(RES_SOMETHING_FONT);
	@Override
	public void dispose() {
		game.assetmanager.unload(RES_BACKGROUND);
		game.assetmanager.unload(RES_BACKGROUND_MUSIC);
		creditsBtn.dispose();
		easyBtn.dispose();
		moderateBtn.dispose();
		difficultBtn.dispose();
	}

	@Override
	public void show() {
		creditsBtn.reset();
		easyBtn.reset();
		moderateBtn.reset();
		difficultBtn.reset();
		Gdx.input.setInputProcessor(this.multiplexer);
		music.play();
	}

	@Override
	public void render(float delta) {
		checkButtons();
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		game.spritebatch.begin();
		game.spritebatch.draw(background, 0, 0, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
		creditsBtn.render(game.spritebatch);
		easyBtn.render(game.spritebatch);
		moderateBtn.render(game.spritebatch);
		difficultBtn.render(game.spritebatch);
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
		
	}
	
	private void checkButtons() {
		if(creditsBtn.getClicked()) {
			// Do not stop the music for CreditsScreen
			game.screenmanager.push(new CreditsScreen(game));
		} else if(easyBtn.getClicked()) {
			music.stop();
			game.screenmanager.push(new GameScreen(game, 0));
		} else if(moderateBtn.getClicked()) {
			music.stop();
			game.screenmanager.push(new GameScreen(game, 1));
		} else if(difficultBtn.getClicked()) {
			music.stop();
			game.screenmanager.push(new GameScreen(game, 2));
		}
	}
}
