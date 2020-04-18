package com.thetriumvirate.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

public final class SplashScreen implements Screen {

	private static final int CAM_WIDTH = Main.WINDOW_WIDTH;
	private static final int CAM_HEIGHT = Main.WINDOW_HEIGHT;
	// SplashScreen will be displayed for at least 5 seconds
	private static final int MIN_SHOWTIME = 5000;
	// Resource paths
	private static final String RES_SPLASH = "graphics/splash-test.png";

	private final Main game;
	private final OrthographicCamera cam;
	private final Texture splashImage;
	private final BitmapFont font;
	// Used to center the text
	private final GlyphLayout layout;
	
	private long showtime;

	public SplashScreen(Main game) {
		this.game = game;
		showtime = 0;
		cam = new OrthographicCamera();
		cam.setToOrtho(false, CAM_WIDTH, CAM_HEIGHT);
		cam.update();
		game.spritebatch.setProjectionMatrix(cam.combined);
		Gdx.gl.glClearColor(1, 0, 0, 1);
		layout = new GlyphLayout();
		
		// Getting all resources required for SplashScreen
		// Unload them in dispose !!!
		// These resources need be loaded synchronously
		font = game.fontloader.load(true);
		splashImage = game.assetmanager.syncGet(RES_SPLASH, Texture.class);
	}

	@Override
	public void show() {
		// TODO Replace ScreenTemplate with actual game/menu screen
		// prefetch needs to be called for every screen other than SplashScreen
		// prefetch loads all game resources asynchronously while SplashScreen is displayed
		// For example: ScreenTemplate.prefetch(game);
		GameScreen.prefetch(game);
		
		showtime = TimeUtils.millis();
	}

	@Override
	public void render(float delta) {
		checkprogress();
		String text = "Progress: " + game.assetmanager.getProgress() * 100 + '%';
		layout.setText(font, text);
		// Position for text
		final Vector2 pos = new Vector2((CAM_WIDTH / 2) - (layout.width / 2), (CAM_HEIGHT / 4) - (layout.height / 2));
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		game.spritebatch.begin();
		game.spritebatch.draw(splashImage, 0, 0, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
		font.draw(game.spritebatch, layout, pos.x, pos.y);
		game.spritebatch.end();
	}

	private void checkprogress() {
		if (TimeUtils.timeSinceMillis(showtime) >= MIN_SHOWTIME && game.assetmanager.update()) {
			// TODO Replace ScreenTemplate with actual game/menu screen
			game.screenmanager.set(new GameScreen(game), false);
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

	}

	// Unload all resources used by SplashScreen
	@Override
	public void dispose() {
		game.fontloader.unload();
		game.assetmanager.unload(RES_SPLASH);
	}
}
