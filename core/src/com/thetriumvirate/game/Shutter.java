package com.thetriumvirate.game;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Shutter extends InputAdapter {

	private static final String RES_SHUTTER = "graphics/shutter.png";
	private static final String RES_SHUTTER_BUTTON = "graphics/shutter-button.png";
	
	private static final int MAX_OFFSET = 100;
	private static final int OFFSET_SPEED_CLOSING = 40;
	private static final int OFFSET_SPEED_OPENING = 10;
	private static final int WIDTH = 100;
	private static final int HEIGHT = 500;
	private static final int BUTTON_WIDTH = 50;
	private static final int BUTTON_HEIGHT = 50;
	private static final int OPENING_STEPS = 5;
	private static final float NEXT_STAGE_OFFSET = MAX_OFFSET / OPENING_STEPS;
	// The range is inclusive at both ends
	private static final int MIN_RAND_CLOSE = 10;
	private static final int MAX_RAND_CLOSE = 20;
	private static final float BRIGHTNESS_FACTOR = 0.4f;
	
	private final GameScreen gameScreen;
	private final Main game;
	
	private final Texture shutterTexture, buttonTexture;
	
	private STATE state;
	private Vector2 position;
	private Vector2 startPosition;
	private Vector2 buttonPosition;
	private float offset;
	private float nextOffset;
	private int currentStep;
	private float timeLeft;
	
	enum STATE {OPEN, CLOSED, CLOSING, OPENING_ACTIVE, OPENING_INACTIVE};
	
	public Shutter(GameScreen gameScreen, Vector2 startPosition, Vector2 buttonPosition) {
		this.gameScreen = gameScreen;
		game = gameScreen.getGame();
		state = STATE.OPEN;
		resetRandomClosing();
		this.position = startPosition.cpy();
		this.startPosition = startPosition.cpy();
		offset = 0;
		this.buttonPosition = buttonPosition.cpy();
		currentStep = 0;
		
		shutterTexture = game.assetmanager.get(RES_SHUTTER, Texture.class);
		buttonTexture = game.assetmanager.get(RES_SHUTTER_BUTTON, Texture.class);
	}
	
	public static void prefetch(Main game) {
		game.assetmanager.load(RES_SHUTTER, Texture.class);
		game.assetmanager.load(RES_SHUTTER_BUTTON, Texture.class);
	}
	
	public static void dispose(Main game) {
		game.assetmanager.unload(RES_SHUTTER);
		game.assetmanager.unload(RES_SHUTTER_BUTTON);
	}
	
	public void update(float delta) {
		checkRandomClosing(delta);
		if(state == STATE.CLOSING) {
			float deltaoffset = delta * OFFSET_SPEED_CLOSING;
			offset += deltaoffset;
			position.set(position.x, startPosition.y - offset);
		} else if(state == STATE.OPENING_ACTIVE) {
			float deltaoffset = delta * OFFSET_SPEED_OPENING;
			offset -= deltaoffset;
			if(offset < nextOffset) {
				offset = nextOffset;
				nextOffset -= NEXT_STAGE_OFFSET;
				currentStep++;
				if(currentStep == OPENING_STEPS) {
					state = STATE.OPEN;
				} else {
					state = STATE.OPENING_INACTIVE;
				}
			}
			position.set(position.x, startPosition.y - offset);
		}
		
		if(offset > MAX_OFFSET) {
			state = STATE.CLOSED;
		} else if(offset < 0) {
			state = STATE.OPEN;
		}
		
		if(state == STATE.OPEN) {
			offset = 0;
			position.set(position.x, startPosition.y);
		} else if(state == STATE.CLOSED) {
			offset = MAX_OFFSET;
			position.set(position.x, startPosition.y - offset);
		}
	}
	
	public void render(SpriteBatch spriteBatch) {
//		spriteBatch.begin();
		spriteBatch.draw(shutterTexture, position.x, position.y, WIDTH, HEIGHT);
		spriteBatch.draw(buttonTexture, buttonPosition.x, buttonPosition.y, BUTTON_WIDTH, BUTTON_HEIGHT);
//		spriteBatch.end();
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(button != Buttons.LEFT) {
			return false;
		}
		
		final int realY = Main.WINDOW_HEIGHT - screenY;
		
		if(buttonPosition.x < screenX && buttonPosition.x + WIDTH > screenX) {
			if(buttonPosition.y < realY && buttonPosition.y + HEIGHT > realY) {
				if(state == STATE.CLOSED) {
					state = STATE.OPENING_ACTIVE;
					nextOffset = MAX_OFFSET - NEXT_STAGE_OFFSET;
					resetRandomClosing();
				}
				else if(state == STATE.OPEN) {state = STATE.CLOSING;}
				else if(state == STATE.OPENING_INACTIVE) {
					state = STATE.OPENING_ACTIVE;
				}
				return true;
			}
		}
		return false;
	}
	
	public void resetRandomClosing() {
		timeLeft = Main.RAND.nextInt(MAX_RAND_CLOSE + 1 - MIN_RAND_CLOSE) + MIN_RAND_CLOSE;
	}
	
	public void checkRandomClosing(float delta) {
		if(state == STATE.OPEN || state == STATE.OPENING_ACTIVE || state == STATE.OPENING_INACTIVE) {
			timeLeft -= delta;
			if(timeLeft <= 0) {
				state = STATE.CLOSING;
			}
		}
	}
	
	public float getBrightnessOverlayAlpha() {
		return ((offset / MAX_OFFSET) * BRIGHTNESS_FACTOR);
	}
	
	public float getPercentage() {
		return offset / MAX_OFFSET;
	}
	
	public static int getWidth() {return WIDTH;}
	public static int getHeight() {return HEIGHT;}
	public static int getButtonWidth() {return BUTTON_WIDTH;}
	public static int getButtonHeight() {return BUTTON_HEIGHT;}
}
