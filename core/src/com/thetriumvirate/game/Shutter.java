package com.thetriumvirate.game;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Shutter extends InputAdapter {

	private static final String RES_SHUTTER = "graphics/shutter.png";
	private static final String RES_SHUTTER_BUTTON = "graphics/shutter-button.png";
	// Sound cannot be longer than a few seconds
	private static final String RES_SHUTTER_CLOSING_SOUND = "audio/shutter-closing.wav";
	private static final String RES_SHUTTER_OPENING_SOUND = "audio/shutter-opening.wav";
	
	public static final int MAX_OFFSET = (int)((140*4) * ((float)Main.WINDOW_HEIGHT / 800f));
	private static final int OFFSET_SPEED_CLOSING = (int)(1000f * ((float)Main.WINDOW_HEIGHT / 800f));
	private static final int OFFSET_SPEED_OPENING = (int)(300f * ((float)Main.WINDOW_HEIGHT / 800f));
	private static final int WIDTH = (int) (280f * ((float)Main.WINDOW_WIDTH / 1024f));
	private static final int HEIGHT = (int) (512f * ((float)Main.WINDOW_HEIGHT / 800f));
	private static final int BUTTON_WIDTH = (int) (32f * ((float)Main.WINDOW_WIDTH) / 1024f);
	private static final int BUTTON_HEIGHT = HEIGHT;
	private static final int OPENING_STEPS = 5;
	private static final float NEXT_STAGE_OFFSET = MAX_OFFSET / OPENING_STEPS;
	// The range is inclusive at both ends
	private static final int MIN_RAND_CLOSE = 10;
	private static final int MAX_RAND_CLOSE = 20;
	private static final float BRIGHTNESS_FACTOR = 0.4f;
	
	private static final float ANIMATIONTIME = 0.02f;
	private static final int ANIM_SPRITES_AMOUNT = 3;
	
	private final GameScreen gameScreen;
	private final Main game;
	
	private final Texture shutterTexture, buttonTexture;
	private TextureRegion[] buttonTexReg;
	private final Sound shutterOpeningSound, shutterClosingSound;
	
	private STATE state;
	private Vector2 position;
	private Vector2 startPosition;
	private Vector2 buttonPosition;
	private float offset;
	private float nextOffset;
	private int currentStep;
	private float timeLeft;
	
	private int currentAnimationIndex;
	private float animationTimer;
	
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
		
		buttonTexReg = new TextureRegion[ANIM_SPRITES_AMOUNT];
		this.currentAnimationIndex = 0;
		this.animationTimer = 0;
		
		shutterTexture = game.assetmanager.get(RES_SHUTTER, Texture.class);
		buttonTexture = game.assetmanager.get(RES_SHUTTER_BUTTON, Texture.class);
		shutterOpeningSound = game.assetmanager.get(RES_SHUTTER_OPENING_SOUND, Sound.class);
		shutterClosingSound = game.assetmanager.get(RES_SHUTTER_CLOSING_SOUND, Sound.class);
		
		buttonTexReg[0] = new TextureRegion(buttonTexture, 0,  0, 16, 256);
		buttonTexReg[1] = new TextureRegion(buttonTexture, 16, 0, 16, 256);
		buttonTexReg[2] = new TextureRegion(buttonTexture, 32, 0, 16, 256);
	}
	
	public static void prefetch(Main game) {
		game.assetmanager.load(RES_SHUTTER, Texture.class);
		game.assetmanager.load(RES_SHUTTER_BUTTON, Texture.class);
		game.assetmanager.load(RES_SHUTTER_OPENING_SOUND, Sound.class);
		game.assetmanager.load(RES_SHUTTER_CLOSING_SOUND, Sound.class);
	}
	
	public static void dispose(Main game) {
		game.assetmanager.unload(RES_SHUTTER);
		game.assetmanager.unload(RES_SHUTTER_BUTTON);
		game.assetmanager.unload(RES_SHUTTER_OPENING_SOUND);
		game.assetmanager.unload(RES_SHUTTER_CLOSING_SOUND);
	}
	
	public void update(float delta) {
		checkRandomClosing(delta);
		if(state == STATE.CLOSING) {
			float deltaoffset = delta * OFFSET_SPEED_CLOSING;
			offset += deltaoffset;
			position.set(position.x, startPosition.y - offset);
			incrementAnimationTimer(delta, -1);//-1 bcs closing, see comment above the called method
			
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
				shutterOpeningSound.stop();
			}
			position.set(position.x, startPosition.y - offset);
			incrementAnimationTimer(delta, 1);//1 bcs closing, see comment above the called method
		}
		
		if(offset > MAX_OFFSET) {
			state = STATE.CLOSED;
			shutterClosingSound.stop();
			
			TutorialManager.TutState.SHUTTER.triggerStart();
		} else if(offset < 0) {
			state = STATE.OPEN;
			shutterOpeningSound.stop();
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
//		spriteBatch.end();
	}
	
	public void renderBtn(SpriteBatch spriteBatch) {
		spriteBatch.draw(this.getButtonTextureRegion(), buttonPosition.x, buttonPosition.y, BUTTON_WIDTH, BUTTON_HEIGHT);
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(button != Buttons.LEFT || gameScreen.getMouseInUse()) {
			return false;
		}
		
		final int realY = Main.WINDOW_HEIGHT - screenY;
		
		if(buttonPosition.x < screenX && buttonPosition.x + BUTTON_WIDTH > screenX) {
			if(buttonPosition.y < realY && buttonPosition.y + HEIGHT > realY) {
				if(state == STATE.CLOSED) {
					state = STATE.OPENING_ACTIVE;
					nextOffset = MAX_OFFSET - NEXT_STAGE_OFFSET;
					shutterOpeningSound.loop();
					resetRandomClosing();
					
					TutorialManager.TutState.SHUTTER.triggerStop();
				}
				else if(state == STATE.OPENING_INACTIVE) {
					state = STATE.OPENING_ACTIVE;
					shutterOpeningSound.loop();
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
			
			if(!(TutorialManager.isShowing())) {
				timeLeft -= delta;
				if(timeLeft <= 0) {
					state = STATE.CLOSING;
					shutterClosingSound.play();
				}
			}
			
		}
	}
	
	//when called, the shutterbtn animates movement, if not, not. dir is +1 for opening, -1 for closing
	public void incrementAnimationTimer(float delta, int dir) {
		animationTimer += delta;
		if(animationTimer >= ANIMATIONTIME) {
			animationTimer = 0;
			currentAnimationIndex += dir;
			if(currentAnimationIndex >= 3) currentAnimationIndex = 0;
			if(currentAnimationIndex <= -1)currentAnimationIndex = 2;
		}
	}
	
	public TextureRegion getButtonTextureRegion() {
		return this.buttonTexReg[currentAnimationIndex];
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
