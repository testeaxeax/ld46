package com.thetriumvirate.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

public final class SplashScreen implements Screen {

	private static final int CAM_WIDTH = Main.WINDOW_WIDTH;
	private static final int CAM_HEIGHT = Main.WINDOW_HEIGHT;
	// SplashScreen will be displayed for at least 5 seconds
	private static final int MIN_SHOWTIME = 10000;
	// Resource paths
	private static final String RES_LOAD_MUSIC = "audio/splash-music.mp3";

	private final Main game;
	private final OrthographicCamera cam;
	private final Music loadMusic;
	private final BitmapFont font;
	// Used to center the text
	private final GlyphLayout layout;
	
	private long showtime;
	private float animDelta;
	private int animCounter;
	
	private static final String RES_PLANTSTAGES = "graphics/plantloading.png";
	private static final String RES_PLANTPOT = "graphics/pot_test.png";
	
	private final Texture plantStages, plantPot;
	private final TextureRegion[][] plantStagesReg;
	private static final int SPRITEWIDTH = 64;
	private static final int SPRITEHEIGHT = 128;
	
	private static final int DRAW_PLANT_X = Main.WINDOW_WIDTH / 2 - (int) (SPRITEWIDTH / 1024f / 2f * Main.WINDOW_WIDTH);
	private static final int DRAW_PLANT_Y = Main.WINDOW_HEIGHT / 2 - (int) (SPRITEHEIGHT / 800f / 2f * Main.WINDOW_HEIGHT);
	private static final int DRAW_WIDTH = (int) ((SPRITEWIDTH / 1024f) * Main.WINDOW_WIDTH);
	private static final int DRAW_HEIGHT = (int) ((SPRITEHEIGHT / 1024f) * Main.WINDOW_HEIGHT);
	
	private static final int DRAW_POT_X = DRAW_PLANT_X;
	private static final int DRAW_POT_Y = DRAW_PLANT_Y - DRAW_WIDTH + (int) (4f / 800f * Main.WINDOW_HEIGHT);

	public SplashScreen(Main game) {
		this.game = game;
		showtime = 0;
		this.animDelta = 0.0f;
		this.animCounter = 0;
		
		cam = new OrthographicCamera();
		cam.setToOrtho(false, CAM_WIDTH, CAM_HEIGHT);
		cam.update();
		game.spritebatch.setProjectionMatrix(cam.combined);
		Gdx.gl.glClearColor(1, 1, 1, 1);
		layout = new GlyphLayout();
		
		// Getting all resources required for SplashScreen
		// Unload them in dispose !!!
		// These resources need be loaded synchronously
		font = game.fontloader.load(true);
		
		this.plantStages = game.assetmanager.syncGet(RES_PLANTSTAGES, Texture.class);
		this.plantStagesReg = TextureRegion.split(this.plantStages, SPRITEWIDTH, SPRITEHEIGHT);
		this.plantPot = game.assetmanager.syncGet(RES_PLANTPOT, Texture.class);
		
		loadMusic = game.assetmanager.syncGet(RES_LOAD_MUSIC, Music.class);
		loadMusic.setLooping(true);
		loadMusic.setVolume(0.05f);
	}

	@Override
	public void show() {
		// TODO Replace ScreenTemplate with actual game/menu screen
		// prefetch needs to be called for every screen other than SplashScreen
		// prefetch loads all game resources asynchronously while SplashScreen is displayed
		// For example: ScreenTemplate.prefetch(game);
		loadMusic.play();
		
		GameOverScreen.prefetch(game);
		GameScreen.prefetch(game);
		CreditsScreen.prefetch(game);
		MenuScreen.prefetch(game);
		
		showtime = TimeUtils.millis();
	}

	@Override
	public void render(float delta) {
		checkprogress();
//		String text = "Progress: " + (int) (game.assetmanager.getProgress() * 100) + '%';
//		layout.setText(font, text);
		// Position for text
		final Vector2 pos = new Vector2((CAM_WIDTH / 2) - (layout.width / 2), (CAM_HEIGHT / 4) - (layout.height / 2));
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		game.spritebatch.begin();
		//game.spritebatch.draw(splashImage, 0, 0, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
//		font.draw(game.spritebatch, layout, pos.x, pos.y);
		
		if(this.animDelta > 0.3f) {
			this.animDelta -= 0.3f;
			this.animCounter++;
		}
		
		int drawX = 0;
		int drawY = 0;
		
		if(animCounter < 8) {
			drawX = animCounter;
		} else if(animCounter == 8) {
			drawX = 7;
			drawY = 1;
		} else if(animCounter > 8 && animCounter <= 12) {
			drawX = 8 - (animCounter - 8) * 2;
			drawY = 4 - (animCounter - 8);
		}
		
		if(animCounter > 12)
			animCounter = 0;
		
		game.spritebatch.draw(this.plantStagesReg[drawY][drawX], DRAW_PLANT_X, DRAW_PLANT_Y, DRAW_WIDTH, DRAW_HEIGHT);
		game.spritebatch.draw(this.plantPot, DRAW_POT_X, DRAW_POT_Y, DRAW_WIDTH, DRAW_WIDTH);
		game.spritebatch.end();
		
		this.animDelta += delta;
	}

	private void checkprogress() {
		if (TimeUtils.timeSinceMillis(showtime) >= MIN_SHOWTIME && game.assetmanager.update()) {
			// TODO Replace ScreenTemplate with actual game/menu screen
			//game.screenmanager.set(new GameScreen(game, 0), false);
			//game.screenmanager.push(new CreditsScreen(game));

			game.screenmanager.set(new MenuScreen(game), false);
			//game.screenmanager.push(new GameOverScreen(game, false));
		}
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
		loadMusic.stop();
	}

	// Unload all resources used by SplashScreen
	@Override
	public void dispose() {
		game.fontloader.unload();
		game.assetmanager.unload(RES_LOAD_MUSIC);
		game.assetmanager.unload(RES_PLANTSTAGES);
		game.assetmanager.unload(RES_PLANTPOT);
	}
}
