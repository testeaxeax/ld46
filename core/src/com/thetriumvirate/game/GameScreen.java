package com.thetriumvirate.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public final class GameScreen implements Screen {
	
	private static final int CAM_WIDTH = Main.WINDOW_WIDTH;
	private static final int CAM_HEIGHT = Main.WINDOW_HEIGHT;
	
	// Declare resource paths below
	// For example: private static final String RES_SOMETHING = "somewhere/something";

	private static final String RES_DEBUG_RECT = "graphics/debugrec.png";
	
	private static final String RES_BACKGROUND = "graphics/background.png";
	private static final String RES_SKY = "graphics/sky.png";
	private static final int SHUTTER1POSX = (int)((float)(32*4) * (float)Main.WINDOW_WIDTH / 1024f);
	private static final int SHUTTER1POSY = (int)((float)(60*4  + Shutter.MAX_OFFSET) * (float)Main.WINDOW_HEIGHT / 800f);
	private static final int SHUTTER2POSX = (int)((float)(174*4 - Shutter.getWidth()) * (float)Main.WINDOW_WIDTH / 1024f);
	private static final int SHUTTER2POSY = (int)((float)(60*4  + Shutter.MAX_OFFSET) * (float)Main.WINDOW_HEIGHT / 800f);
	private static final int SHUTTERBTN1_OFFSET_X = (int)((float)(- Shutter.getButtonWidth() * 2f) * (float)Main.WINDOW_WIDTH / 1024f);
	private static final int SHUTTERBTN2_OFFSET_X = (int)((float)(Shutter.getWidth() + Shutter.getButtonWidth()) * (float)Main.WINDOW_WIDTH / 1024f);
	private static final int SHUTTERBTN1_OFFSET_Y = (int)((float)(- Shutter.MAX_OFFSET) * (float)Main.WINDOW_WIDTH / 1024f);
	private static final int SHUTTERBTN2_OFFSET_Y = (int)((float)(- Shutter.MAX_OFFSET) * (float)Main.WINDOW_WIDTH / 1024f);
	
	public final Texture tex_debugrect;
	public final Pixmap brightnessOverlayPixmap;
	
	private enum TemperatureOverlayStatus {NONE, COLD, HOT};
	private TemperatureOverlayStatus temperatureOverlayStatus;
	private float temperatureOverlayAlpha;
	private static final String RES_TEMPERATUREOVERLAY = "graphics/temperatureoverlay.png";
	private final Texture tex_temperatureOverlay;
	private final TextureRegion[] tex_temperatureOverlayRegions;
	
	private final Main game;
	private final OrthographicCamera cam;
	
	// Declare resource variables below
	// For example: private final Texture testTexture;
	
	private Tap tap;
	private WateringCan wateringCan;
	private final TemperatureController temperatureController;
	private final List<Shutter> shutters;
	
	private final Texture background_texture, sky_texture;
	
	private Thermometer thermometer;
	
	private final int difficulty;

	private final List<Plant> plants;
	
	private boolean mouseInUse;

	
	public GameScreen(Main game, int difficulty) {
		// Initialize essentials
		this.game = game;
		this.difficulty = difficulty;
		mouseInUse = false;
		cam = new OrthographicCamera();
		cam.setToOrtho(false, CAM_WIDTH, CAM_HEIGHT);
		cam.update();
		game.spritebatch.setProjectionMatrix(cam.combined);
		Gdx.gl.glClearColor(1, 1, 1, 1);
		
		InputMultiplexer inputmultiplexer = new InputMultiplexer();

		inputmultiplexer.addProcessor(TutorialManager.getInputAdapter());
		
		this.tap = new Tap(game);
		inputmultiplexer.addProcessor(this.tap);
		
		this.wateringCan = new WateringCan(this, this.tap);
		inputmultiplexer.addProcessor(this.wateringCan);
		
		this.temperatureController = new TemperatureController(this, 1);
		inputmultiplexer.addProcessor(this.temperatureController);
		
		
		shutters = new ArrayList<Shutter>();
		//replaced by hardcoded shutters below to fit the background
		/*for(int i = 0; i < 4; i++) {
			Shutter shutter = new Shutter(this,
					new Vector2(100 + Shutter.getWidth() * 2 * i, 400),
					new Vector2(235 + Shutter.getWidth() * 2 * i, 300));
			this.shutters.add(shutter);
			inputmultiplexer.addProcessor(shutter);
		}*/
		Shutter shutter1 = new Shutter(this, new Vector2(SHUTTER1POSX, SHUTTER1POSY), new Vector2(SHUTTER1POSX + SHUTTERBTN1_OFFSET_X, SHUTTER1POSY + SHUTTERBTN1_OFFSET_Y));
		this.shutters.add(shutter1);
		inputmultiplexer.addProcessor(shutter1);
		Shutter shutter2 = new Shutter(this,  new Vector2(SHUTTER2POSX, SHUTTER2POSY),  new Vector2(SHUTTER2POSX + SHUTTERBTN2_OFFSET_X, SHUTTER2POSY + SHUTTERBTN2_OFFSET_Y));
		this.shutters.add(shutter2);
		inputmultiplexer.addProcessor(shutter2);
		
		this.plants = new ArrayList<Plant>();
		
		for(int i = 0; i < 3; i++)
			this.plants.add(new Plant(this, i));
		
		
		this.thermometer = new Thermometer(game, new Vector2((int)((float)(828) * (float)Main.WINDOW_WIDTH / 1024f), (int)((float)(508) * (float)Main.WINDOW_HEIGHT / 800f)));
		
		
		Gdx.input.setInputProcessor(inputmultiplexer);
		
		// Initialize resource variables below
		// For example: testTexture = game.assetmanager.get(RES_SOMETEXTURE, Texture.class);
		this.tex_debugrect = this.game.assetmanager.get(RES_DEBUG_RECT, Texture.class);
		
		//backgroundtextures
		this.background_texture = this.game.assetmanager.get(RES_BACKGROUND, Texture.class);
		this.sky_texture = this.game.assetmanager.get(RES_SKY, Texture.class);
		
		// Do everything else below

		TutorialManager.load(game);
		
		this.brightnessOverlayPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);

		this.tex_temperatureOverlay = this.game.assetmanager.get(RES_TEMPERATUREOVERLAY, Texture.class);
		this.tex_temperatureOverlayRegions = TextureRegion.split(this.tex_temperatureOverlay, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT)[0];
		this.temperatureOverlayStatus = TemperatureOverlayStatus.NONE;
		this.temperatureOverlayAlpha = 0.0f;
	}
	
	// Load all resources for this screen in prefetch !!!
	// For example: game.assetmanager.load(RES_SOMETHING, SomeType.class);
	// or			game.fontloader.load(RES_SOMETHING_FONT);
	// Unload all resources in dispose !!!
	public static void prefetch(Main game) {
		game.assetmanager.load(GameScreen.RES_DEBUG_RECT, Texture.class);
		game.assetmanager.load(GameScreen.RES_TEMPERATUREOVERLAY, Texture.class);
		game.assetmanager.load(GameScreen.RES_BACKGROUND, Texture.class);
		game.assetmanager.load(GameScreen.RES_SKY, Texture.class);

		TutorialManager.prefetch(game);
		
		Plant.prefetch(game);
		Shutter.prefetch(game);
		TemperatureController.prefetch(game);
		Tap.prefetch(game);
		WateringCan.prefetch(game);
		Thermometer.prefetch(game);
		
	}
	
	// Unload all resources for this screen
	// For example: game.assetmanager.unload(RES_SOMETHING);
	// For fonts: game.fontmanager.unload(RES_SOMETHING_FONT);
	@Override
	public void dispose() {
		game.assetmanager.unload(RES_DEBUG_RECT);
		game.assetmanager.unload(RES_TEMPERATUREOVERLAY);
		game.assetmanager.unload(RES_BACKGROUND);
		game.assetmanager.unload(RES_SKY);
		
		TutorialManager.dispatch(game);
		
		WateringCan.dispose(game);
		Tap.dispose(game);
		TemperatureController.dispose(game);
		Shutter.dispose(game);
		Plant.dispose(game);
		Thermometer.dispose(game);
	}

	@Override
	public void show() {

	}
	
	public void update(float delta) {
		this.wateringCan.update(delta);
		this.tap.update(delta);
		this.temperatureController.update(delta);
		this.thermometer.update(delta, this.temperatureController.getCurrentTemp());
		
		if(this.temperatureController.getCurrentTemp() > Plant.TEMP_MAX) {
			// it's too hot for the plants
			this.temperatureOverlayStatus = TemperatureOverlayStatus.HOT;
			
			float toohotness = (this.temperatureController.getCurrentTemp() - Plant.TEMP_MAX)/ (TemperatureController.MAX_TEMP - Plant.TEMP_MAX);
			
			this.temperatureOverlayAlpha = toohotness;
			
			TutorialManager.TutState.TEMP_HIGH.triggerStart();
		} else if(this.temperatureController.getCurrentTemp() < Plant.TEMP_MIN) {
			// it's too cold for the plants
			this.temperatureOverlayStatus = TemperatureOverlayStatus.COLD;
						
			float toocoldness = (Plant.TEMP_MIN - this.temperatureController.getCurrentTemp()) / Plant.TEMP_MIN;
			
			this.temperatureOverlayAlpha = toocoldness;
			TutorialManager.TutState.TEMP_LOW.triggerStart();
		} else {
			// the plants can grow just fine
			
			this.temperatureOverlayStatus = TemperatureOverlayStatus.NONE;
			this.temperatureOverlayAlpha = 0.0f;
		}
		
		
		for(Shutter shutter : shutters) {
			shutter.update(delta);
		}
		

		float alpha = 0;
		for(Shutter shutter : shutters) {
			alpha += shutter.getBrightnessOverlayAlpha();
		}
		alpha /= shutters.size();
		
		// last thing to be updated should be the plants
		for(Plant p : this.plants)
			p.update(delta, alpha, this.temperatureController.getCurrentTemp());

		brightnessOverlayPixmap.setColor(0f, 0f, 0f, alpha);
		brightnessOverlayPixmap.fill();

    //uncomment to enable getting a game over!
		//checkIfGameOver();

	}

	@Override
	public void render(float delta) {
		update(delta);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		game.spritebatch.begin();
		
		//use correct render order: sky, then shutter, then background, then shutterbtn!
		
		game.spritebatch.draw(sky_texture, 0, 0, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
		
		for(Shutter shutter : shutters) {
			shutter.render(game.spritebatch);
		}
		
		game.spritebatch.draw(background_texture, 0, 0, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
		
		for(Shutter shutter : shutters) {
			shutter.renderBtn(game.spritebatch);
		}
		
		this.tap.render(game.spritebatch);
		this.temperatureController.render(game.spritebatch);
		this.thermometer.render(game.spritebatch);
		
		for(Plant p : this.plants)
			p.render(game.spritebatch);
		
		this.wateringCan.render(game.spritebatch);
		
		// drawing the temperature overlay
		Color batchColor = game.spritebatch.getColor();
		
		Color before = new Color(batchColor);
		// TODO: check if / 2f is still necessary with good texture
		batchColor.a = this.temperatureOverlayAlpha / 2f;
		game.spritebatch.setColor(batchColor);
		
		if(this.temperatureOverlayStatus == TemperatureOverlayStatus.HOT)
			game.spritebatch.draw(this.tex_temperatureOverlayRegions[0], 0, 0);
		else if(this.temperatureOverlayStatus == TemperatureOverlayStatus.COLD)
			game.spritebatch.draw(this.tex_temperatureOverlayRegions[1], 0, 0);
		
		game.spritebatch.setColor(before);
		
		
		game.spritebatch.draw(new Texture(brightnessOverlayPixmap), 0, 0, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
		
		TutorialManager.render(game.spritebatch);
		
		game.spritebatch.end();
	}

	private void checkIfGameOver() {
		//check if one plant is dead or all plants blossom
		
		for(Plant p :this.plants) {
			if(p.isDecayed()) {
				game.screenmanager.set(new GameOverScreen(game, false), true);//game lost; keeping assets for replay
				break;
			}
		}
		
		boolean allBlossom = true;
		for(Plant p : this.plants) {
			if(!p.isFullyGrown()) {
				allBlossom = false;
				break;
			}
		}
		if(allBlossom) {
			game.screenmanager.set(new GameOverScreen(game, true), true);//game is won; keeping assets for replay
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
	
	public List<Plant> getPlants(){
		return this.plants;
	}
	
	public Main getGame() {
		return this.game;
	}
	
	public boolean getMouseInUse() {
		return mouseInUse;
	}
	
	public void setMouseInUse(boolean mouseInUse) {
		this.mouseInUse = mouseInUse;
	}
}
