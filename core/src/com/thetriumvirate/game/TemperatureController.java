package com.thetriumvirate.game;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class TemperatureController extends InputAdapter {

	private static final String RES_SWITCH_ON = "graphics/switch_on.png";
	private static final String RES_SWITCH_OFF = "graphics/switch_off.png";
	
	private static final int MIN_TEMP = 0;
	private static final int MAX_TEMP = 100;
	private static final int INIT_TEMP = 20;
	private static final Vector2 POSITION = new Vector2(10, 10);
	private static final int WIDTH = 10;
	private static final int HEIGHT = 10;
	private static final int TEMP_LOSS_PER_SECOND = 1;
	private static final int TEMP_INCREASE_PER_SECOND = 1;
	
	private final GameScreen gamescreen;
	private final Main game;
	private final int tempLossPerSecond, tempIncreasePerSecond;
	
	// Resources
	private final Texture switch_on_texture, switch_off_texture;
	
	private enum STATE {OFF, ON};
	
	private int currentTemp;
	private STATE state;
	
	public TemperatureController(GameScreen gamescreen, int difficulty) {
		this.gamescreen = gamescreen;
		game = gamescreen.getGame();
		// TODO We may need to change this
		this.tempLossPerSecond = TEMP_INCREASE_PER_SECOND * difficulty;
		this.tempIncreasePerSecond = TEMP_INCREASE_PER_SECOND * difficulty;
		currentTemp = INIT_TEMP;
		state = STATE.OFF;
		
		// Initialize resources
		switch_on_texture = game.assetmanager.get(RES_SWITCH_ON, Texture.class);
		switch_off_texture = game.assetmanager.get(RES_SWITCH_OFF, Texture.class);
	}
	
	public static void prefetch(Main game) {
		game.assetmanager.load(RES_SWITCH_ON, Texture.class);
		game.assetmanager.load(RES_SWITCH_OFF, Texture.class);
	}
	
	public void dispose() {
		game.assetmanager.unload(RES_SWITCH_OFF);
		game.assetmanager.unload(RES_SWITCH_ON);
	}
	
	public void update(float delta) {
		currentTemp -= delta * tempLossPerSecond;
		if(state == state.ON) {
			currentTemp += delta * tempIncreasePerSecond;
		}
		if(currentTemp < MIN_TEMP) {currentTemp = MIN_TEMP;}
		else if (currentTemp > MAX_TEMP) {currentTemp = MAX_TEMP;}
	}
	
	public void render(SpriteBatch spritebatch) {
		if(state == STATE.OFF) {
			spritebatch.draw(switch_off_texture, POSITION.x, POSITION.y, WIDTH, HEIGHT);
		} else {
			spritebatch.draw(switch_on_texture, POSITION.x, POSITION.y, WIDTH, HEIGHT);
		}
	}
	
	public Vector2 getPosition() {
		return POSITION.cpy();
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(button != Buttons.LEFT) {
			return false;
		}
		
		final int realY = screenY - Main.WINDOW_HEIGHT;
		
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
	}
}
