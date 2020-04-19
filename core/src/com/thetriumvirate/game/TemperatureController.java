package com.thetriumvirate.game;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

public class TemperatureController extends InputAdapter {

	private static final String RES_SWITCH_ON = "graphics/switch_on.png";
	private static final String RES_SWITCH_OFF = "graphics/switch_off.png";
	
	public static final int MIN_TEMP = 0;
	public static final int MAX_TEMP = 100;
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
	private final GlyphLayout layout;
	private final BitmapFont font;
	private static final int FONT_SIZE = 20;
	private static final int TEXT_OFFSET_X = 50;
	private static final int TEXT_OFFSET_Y = 20;

	private enum STATE {OFF, ON};
	
	private float currentTemp;
	private STATE state;
	
	public TemperatureController(GameScreen gamescreen, int difficulty) {
		this.gamescreen = gamescreen;
		game = gamescreen.getGame();
		
		// TODO We may need to change this
		this.tempLossPerSecond = TEMP_LOSS_PER_SECOND * (difficulty + 1) * 2;
		this.tempIncreasePerSecond = TEMP_INCREASE_PER_SECOND * (difficulty + 1);
		
		currentTemp = INIT_TEMP;
		state = STATE.OFF;
		
		// Initialize resources
		switch_on_texture = game.assetmanager.get(RES_SWITCH_ON, Texture.class);
		switch_off_texture = game.assetmanager.get(RES_SWITCH_OFF, Texture.class);
		
		this.layout = new GlyphLayout();
		this.font = game.fontloader.get(Main.RES_DEFAULT_FONT, FONT_SIZE, Color.BLACK);
		this.layout.setText(this.font, "" + (int) this.currentTemp);
	}
	
	public static void prefetch(Main game) {
		game.assetmanager.load(RES_SWITCH_ON, Texture.class);
		game.assetmanager.load(RES_SWITCH_OFF, Texture.class);

		game.fontloader.load(Main.RES_DEFAULT_FONT, FONT_SIZE, Color.BLACK, true);
	}
	
	public static void dispose(Main game) {
		game.assetmanager.unload(RES_SWITCH_OFF);
		game.assetmanager.unload(RES_SWITCH_ON);
	}
	
	public void update(float delta) {
		
		if(state == STATE.ON) {
			currentTemp += delta * tempIncreasePerSecond;
		} else {
			currentTemp -= delta * tempLossPerSecond;
		}
		
		if(currentTemp < MIN_TEMP) {currentTemp = MIN_TEMP;}
		else if (currentTemp > MAX_TEMP) {currentTemp = MAX_TEMP;}
		
		this.layout.setText(this.font, "" + (int) this.currentTemp);
	}

  
	public void render(SpriteBatch spritebatch) {
//		spritebatch.begin();
    
		if(state == STATE.OFF) {
			spritebatch.draw(switch_off_texture, POSITION.x, POSITION.y, WIDTH, HEIGHT);
		} else {
			spritebatch.draw(switch_on_texture, POSITION.x, POSITION.y, WIDTH, HEIGHT);
		}
		
		this.font.draw(spritebatch, this.layout, POSITION.x + TEXT_OFFSET_X, POSITION.y + TEXT_OFFSET_Y);
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
	}
	
	public float getCurrentTemp() {
		return  currentTemp;
	}
}
