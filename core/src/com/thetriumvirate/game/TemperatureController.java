package com.thetriumvirate.game;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class TemperatureController extends InputAdapter {

	private static final String RES_SWITCH_ON = "graphics/switch_on.png";
	private static final String RES_SWITCH_OFF = "graphics/switch_off.png";
	// Sound cannot be longer than a few seconds
	private static final String RES_SWITCH_SOUND = "audio/switch.wav";
	
	private static final int MIN_TEMP = 0;
	private static final int MAX_TEMP = 100;
	private static final int INIT_TEMP = 20;
	private static final Vector2 POSITION = new Vector2(30, 50);
	private static final int WIDTH = 40;
	private static final int HEIGHT = 40;
	private static final int TEMP_LOSS_PER_SECOND = 1;
	private static final int TEMP_INCREASE_PER_SECOND = 1;
	
	private final GameScreen gamescreen;
	private final Main game;
	private final int tempLossPerSecond, tempIncreasePerSecond;
	
	// Resources
	private final Texture switch_on_texture, switch_off_texture;
	private final Sound switchSound;
	
	private enum STATE {OFF, ON};
	
	private int currentTemp;
	private STATE state;
	
	public TemperatureController(GameScreen gamescreen, int difficulty) {
		this.gamescreen = gamescreen;
		game = gamescreen.getGame();
		// TODO We may need to change this
		this.tempLossPerSecond = TEMP_LOSS_PER_SECOND * difficulty;
		this.tempIncreasePerSecond = TEMP_INCREASE_PER_SECOND * difficulty;
		currentTemp = INIT_TEMP;
		state = STATE.OFF;
		
		// Initialize resources
		switch_on_texture = game.assetmanager.get(RES_SWITCH_ON, Texture.class);
		switch_off_texture = game.assetmanager.get(RES_SWITCH_OFF, Texture.class);
		switchSound = game.assetmanager.get(RES_SWITCH_SOUND, Sound.class);
	}
	
	public static void prefetch(Main game) {
		game.assetmanager.load(RES_SWITCH_ON, Texture.class);
		game.assetmanager.load(RES_SWITCH_OFF, Texture.class);
		game.assetmanager.load(RES_SWITCH_SOUND, Sound.class);
	}
	
	public static void dispose(Main game) {
		game.assetmanager.unload(RES_SWITCH_OFF);
		game.assetmanager.unload(RES_SWITCH_ON);
		game.assetmanager.unload(RES_SWITCH_SOUND);
	}
	
	public void update(float delta) {
		currentTemp -= delta * tempLossPerSecond;
		if(state == STATE.ON) {
			currentTemp += delta * tempIncreasePerSecond;
		}
		if(currentTemp < MIN_TEMP) {currentTemp = MIN_TEMP;}
		else if (currentTemp > MAX_TEMP) {currentTemp = MAX_TEMP;}
	}

  
	public void render(SpriteBatch spritebatch) {
//		spritebatch.begin();
    
		if(state == STATE.OFF) {
			spritebatch.draw(switch_off_texture, POSITION.x, POSITION.y, WIDTH, HEIGHT);
		} else {
			spritebatch.draw(switch_on_texture, POSITION.x, POSITION.y, WIDTH, HEIGHT);
		}
    
//		spritebatch.end();
	}
	
	public Vector2 getPosition() {
		return POSITION.cpy();
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(button != Buttons.LEFT || gamescreen.getMouseInUse()) {
			return false;
		}
		
		final int realY = Main.WINDOW_HEIGHT - screenY;
		
		if(POSITION.x < screenX && POSITION.x + WIDTH > screenX) {
			if(POSITION.y < realY && POSITION.y + HEIGHT > realY) {
				toggleState();
				return true;
			}
		}
		return false;
	}
	
	public void toggleState() {
		if(state == STATE.ON) {state = STATE.OFF;}
		else {state = STATE.ON;}
		switchSound.play();
	}
	
	public int getCurrentTemp() {
		return currentTemp;
	}
}
