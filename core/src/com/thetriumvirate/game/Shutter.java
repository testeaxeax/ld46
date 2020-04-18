package com.thetriumvirate.game;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Shutter extends InputAdapter {

	private static final String RES_SHUTTER = "graphics/shutter.png";
	private static final String RES_SHUTTER_BUTTON = "graphics/shutter-button.png";
	
	private static final int MAX_OFFSET = 30;
	private static final int OFFSET_SPEED = 2;
	private static final int WIDTH = 10;
	private static final int HEIGHT = 10;
	private static final int BUTTON_WIDTH = 5;
	private static final int BUTTON_HEIGHT = 5;
	
	private final GameScreen gameScreen;
	private final Main game;
	
	private final Texture shutterTexture, buttonTexture;
	
	private STATE state;
	private Vector2 position;
	private Vector2 startPosition;
	private Vector2 buttonPosition;
	private float offset;
	
	enum STATE {OPEN, CLOSED, CLOSING, OPENING};
	
	public Shutter(GameScreen gameScreen, Vector2 startPosition, Vector2 buttonPosition) {
		this.gameScreen = gameScreen;
		game = gameScreen.getGame();
		state = STATE.OPEN;
		this.position = startPosition.cpy();
		offset = 0;
		this.buttonPosition = buttonPosition.cpy();
		
		shutterTexture = game.assetmanager.get(RES_SHUTTER, Texture.class);
		buttonTexture = game.assetmanager.get(RES_SHUTTER_BUTTON, Texture.class);
	}
	
	public static void prefetch(Main game) {
		game.assetmanager.load(RES_SHUTTER, Texture.class);
		game.assetmanager.load(RES_SHUTTER_BUTTON, Texture.class);
	}
	
	public void dispose() {
		game.assetmanager.unload(RES_SHUTTER);
		game.assetmanager.unload(RES_SHUTTER_BUTTON);
	}
	
	public void update(float delta) {
		if(state == STATE.CLOSING) {
			float deltaoffset = delta * OFFSET_SPEED;
			offset += deltaoffset;
			position.set(position.x, position.y - deltaoffset);
		} else if(state == STATE.OPENING) {
			float deltaoffset = delta * OFFSET_SPEED;
			offset -= deltaoffset;
			position.set(position.x, position.y + deltaoffset);
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
				if(state == STATE.CLOSED) {state = STATE.OPENING;}
				else if (state == STATE.OPEN) {state = STATE.CLOSING;}
				return true;
			}
		}
		return false;
	}
}
