package com.thetriumvirate.game;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class TemperatureController extends InputAdapter {

	//replaced by RES_KNOB
	//private static final String RES_SWITCH_ON = "graphics/switch_on.png";
	//private static final String RES_SWITCH_OFF = "graphics/switch_off.png";
	private static final String RES_KNOB = "graphics/tempBtn.png";
	private static final String RES_TEMPDISPLAY = "graphics/tempMonitor.png";
	private static final String RES_SWITCH_SOUND = "audio/switch.wav";
	
	public static final int MIN_TEMP = 0;
	public static final int MAX_TEMP = 100;
	private static final int INIT_TEMP = 20;
	private static final Vector2 POSITION = new Vector2(600, 200);
	private static final int KNOB_WIDTH = 32;
	private static final int KNOB_HEIGHT = 32;
	private static final int DISPLAY_WIDTH = 96;
	private static final int DISPLAY_HEIGHT = 96;
	private static final int TEMP_LOSS_PER_SECOND = 1;
	private static final int TEMP_INCREASE_PER_SECOND = 1;
	
	private final GameScreen gamescreen;
	private final Main game;
	private final int tempLossPerSecond, tempIncreasePerSecond;
	
	// Resources
	private final Texture switch_on_texture, switch_off_texture;
	private final BitmapFont font;
	private static final int FONT_SIZE = 20;
	private static final int TEXT_OFFSET_X = 50;
	private static final int TEXT_OFFSET_Y = 20;

	private final Texture knob_texture;
	private final Texture tempDisplay_texture;
	//private final Texture switch_on_texture, switch_off_texture;
	private TextureRegion[] knob_texReg;
	private final int SPRITE_WIDTH = 32;
	private final int SPRITE_HEIGHT = 32;
  
	private final Sound switchSound;
	
	private enum STATE {OFF, ON};
	
	private float currentTemp;
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
		this.font = game.fontloader.get(Main.RES_DEFAULT_FONT, FONT_SIZE, Color.BLACK);
    
		switchSound = game.assetmanager.get(RES_SWITCH_SOUND, Sound.class);
	
		knob_texture = game.assetmanager.get(RES_KNOB, Texture.class);
		tempDisplay_texture = game.assetmanager.get(RES_TEMPDISPLAY, Texture.class);
    switchSound = game.assetmanager.get(RES_SWITCH_SOUND, Sound.class);
		
		knob_texReg = new TextureRegion[2];
		knob_texReg[0] = new TextureRegion(knob_texture, 0, 0, SPRITE_WIDTH, SPRITE_HEIGHT);
		knob_texReg[1] = new TextureRegion(knob_texture, 0, SPRITE_HEIGHT, SPRITE_WIDT SPRITE_HEIGHT);
	}
	
	public static void prefetch(Main game) {
		//game.assetmanager.load(RES_SWITCH_ON, Texture.class);
		//game.assetmanager.load(RES_SWITCH_OFF, Texture.class);
		game.fontloader.load(Main.RES_DEFAULT_FONT, FONT_SIZE, Color.BLACK);
    
		game.assetmanager.load(RES_KNOB, Texture.class);
		game.assetmanager.load(RES_TEMPDISPLAY, Texture.class);
    
		game.assetmanager.load(RES_SWITCH_SOUND, Sound.class);
	}
	
	public static void dispose(Main game) {
		//game.assetmanager.unload(RES_SWITCH_OFF);
		//game.assetmanager.unload(RES_SWITCH_ON);
		game.assetmanager.unload(RES_KNOB);
		game.assetmanager.unload(RES_TEMPDISPLAY);
    game.assetmanager.unload(RES_SWITCH_SOUND);
	}
	
	
	
	public void update(float delta) {
		
		if(state == STATE.ON) {
			currentTemp += delta * tempIncreasePerSecond;
		} else {
			currentTemp -= delta * tempLossPerSecond;
		}
		
		if(currentTemp < MIN_TEMP) {currentTemp = MIN_TEMP;}
		else if (currentTemp > MAX_TEMP) {currentTemp = MAX_TEMP;}
	}

  
	public void render(SpriteBatch spritebatch) {
		spritebatch.draw(this.getKnobTextureRegion(), POSITION.x, POSITION.y, KNOB_WIDTH, KNOB_HEIGHT);
		
		spritebatch.draw(tempDisplay_texture, POSITION.x + 1.5f * KNOB_WIDTH, POSITION.y, DISPLAY_WIDTH, DISPLAY_HEIGHT);
    
		this.font.draw(spritebatch, "" + (int) this.currentTemp, POSITION.x + TEXT_OFFSET_X, POSITION.y + TEXT_OFFSET_Y);
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
		
		if(POSITION.x < screenX && POSITION.x + KNOB_WIDTH > screenX) {
			if(POSITION.y < realY && POSITION.y + KNOB_HEIGHT > realY) {
				toggleState();
				return true;
			}
		}
		return false;
	}
	
	public TextureRegion getKnobTextureRegion() {
		if(state == STATE.OFF) {
			return this.knob_texReg[0];
		} else {
			return this.knob_texReg[1];
		}
	}
	
	public void toggleState() {
		if(state == STATE.ON) {state = STATE.OFF;}
		else {state = STATE.ON;}
		switchSound.play();
	}
	
	public float getCurrentTemp() {
		return  currentTemp;
	}
}
