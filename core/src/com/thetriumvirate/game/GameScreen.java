package com.thetriumvirate.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

public final class GameScreen implements Screen {
	
	private static final int CAM_WIDTH = Main.WINDOW_WIDTH;
	private static final int CAM_HEIGHT = Main.WINDOW_HEIGHT;
	
	// Declare resource paths below
	// For example: private static final String RES_SOMETHING = "somewhere/something";
	
	private final Main game;
	private final OrthographicCamera cam;
	
	// Declare resource variables below
	// For example: private final Texture testTexture;
	
	private Tap tap;
	private WateringCan wateringCan;
	private final TemperatureController temperatureController;
	private final Shutter shutter;
	
	public GameScreen(Main game) {
		// Initialize essentials
		this.game = game;
		cam = new OrthographicCamera();
		cam.setToOrtho(false, CAM_WIDTH, CAM_HEIGHT);
		cam.update();
		game.spritebatch.setProjectionMatrix(cam.combined);
		Gdx.gl.glClearColor(1, 0, 0, 1);
		
		InputMultiplexer inputmultiplexer = new InputMultiplexer();

		this.tap = new Tap(game);
		inputmultiplexer.addProcessor(this.tap);
		
		this.wateringCan = new WateringCan(game, this.tap);
		inputmultiplexer.addProcessor(this.wateringCan);
		
		this.temperatureController = new TemperatureController(this, 1);
		// TODO generiere shutter sinnvoll
		this.shutter = new Shutter(this, new Vector2(22,22), new Vector2(44,44));
		
		Gdx.input.setInputProcessor(inputmultiplexer);
		
		// Initialize resource variables below
		// For example: testTexture = game.assetmanager.get(RES_SOMETEXTURE, Texture.class);
		
		// Do everything else below
	}
	
	// Load all resources for this screen in prefetch !!!
	// For example: game.assetmanager.load(RES_SOMETHING, SomeType.class);
	// or			game.fontloader.load(RES_SOMETHING_FONT);
	// Unload all resources in dispose !!!
	public static void prefetch(Main game) {
		Shutter.prefetch(game);
		TemperatureController.prefetch(game);
		Tap.prefetch(game);
		WateringCan.prefetch(game);
	}
	
	// Unload all resources for this screen
	// For example: game.assetmanager.unload(RES_SOMETHING);
	// For fonts: game.fontmanager.unload(RES_SOMETHING_FONT);
	@Override
	public void dispose() {
		this.wateringCan.unload();
		this.tap.unload();
		// TODO
		this.shutter.dispose();
	}

	@Override
	public void show() {
		
	}
	
	public void update(float delta) {
		this.wateringCan.update(delta);
		this.temperatureController.update(delta);
		// TODO
		this.shutter.update(delta);
	}

	@Override
	public void render(float delta) {
		update(delta);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		game.spritebatch.begin();
		
		this.tap.render(game.spritebatch);
		this.wateringCan.render(game.spritebatch);
		this.temperatureController.render(game.spritebatch);
		// TODO
		this.shutter.render(game.spritebatch);
		
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
	
	public Main getGame() {
		return this.game;
	}
}
