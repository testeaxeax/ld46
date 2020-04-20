package com.thetriumvirate.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.thetriumvirate.game.TutorialManager.TutState;

public class WateringCan extends InputAdapter{
	// Resource paths
	private static final String RES_CAN = "graphics/wateringcan.png";
	// Sound cannot be longer than a few seconds
	private static final String RES_TAKE_SOUND = "audio/can-take.wav";
	private static final String RES_WATER_MUSIC = "audio/can-watering.wav";
	private static final String RES_DROP_EMPTY_SOUND = "audio/can-drop-empty.wav";
	private static final String RES_DROP_FULL_SOUND = "audio/can-drop-full.wav";
	//private static final String RES_CAN_STANDING = "graphics/wateringcanstanding.png";
	//private static final String RES_CAN_EMPTY = "graphics/canempty.png";
	//private static final String RES_CAN_FILLSTATES[] = {"graphics/canstate1.png", "graphics/canstate2.png", "graphics/canstate3.png", "graphics/canstate4.png", "graphics/canstate5.png"};

	private static final float DRAW_WIDTH = 128f / 1024f;
	private static final float DRAW_HEIGHT = 128f / 800f;
	private static final int SPRITE_WIDTH = 64;
	private static final int SPRITE_HEIGHT = 64;
	
	private boolean selected, wateringPlants, canTilted;
	
	private final GameScreen game;
	private final Texture tex_can/*, tex_can_standing, tex_can_empty, tex_can_fillstates[]*/;

	private final Sound takeSound, dropEmptySound, dropFullSound;
	private final Music wateringMusic;

	private final Tap tap;
	private TextureRegion[][] texReg_can;
	
	
	private float pos_x, pos_y;
	
	// min: 0 max: 1000 (ml?)
	private float fillState;
	
	// max fill
	private static final float MAX_FILL = 1000.0f;
	// ein Refill dauert 1000 / 100 = 10 Sekunden
	private static final float REFILL_SPEED = 300.0f;
	
	private static final float WATERING_SPEED = 150.0f;

	private static final float HEAD_WIDTH = 45f / 1024f;
	private static final float HEAD_HEIGHT = 45f / 800f;
	private static final float HEAD_OFFSET_Y = 10f / 800f;
	private Rectangle canHead;
	private boolean hasWateredThisTick = false;
	
	private final ParticleEffect wateringEffect;
	private static final String RES_PEFFECT = "particleeffects/effectcan.p";
	private static final String RES_PEFFECT_FILES = "particleeffects/";
	private static final float EFFECT_OFFSET_X = 8f / 1024f;
	private static final float EFFECT_OFFSET_Y = 10f / 800f;
	
	public WateringCan(final GameScreen gameScreen, final Tap myTap) {
		this.game = gameScreen;
		this.tap = myTap;
		
		this.selected = false;
		this.canTilted = false;
		this.wateringPlants = false;
		
		// start with full can
		this.fillState = MAX_FILL;
		
		this.pos_x = this.tap.getDockX(DRAW_WIDTH);
		this.pos_y = this.tap.getDockY();
		
		this.canHead = new Rectangle(this.pos_x, this.pos_y + HEAD_OFFSET_Y, HEAD_WIDTH, HEAD_HEIGHT);
		
		this.wateringEffect = new ParticleEffect();
		this.wateringEffect.load(Gdx.files.internal(RES_PEFFECT), Gdx.files.internal(RES_PEFFECT_FILES));
		this.wateringEffect.setPosition(this.pos_x + EFFECT_OFFSET_X, this.pos_y + EFFECT_OFFSET_Y);
		this.wateringEffect.setDuration(500);
		
		this.tex_can = this.game.getGame().assetmanager.get(RES_CAN, Texture.class);
		this.texReg_can = TextureRegion.split(tex_can, SPRITE_WIDTH, SPRITE_HEIGHT);
		takeSound = game.getGame().assetmanager.get(RES_TAKE_SOUND, Sound.class);

		this.wateringMusic = game.getGame().assetmanager.get(RES_WATER_MUSIC, Music.class);
		dropEmptySound = game.getGame().assetmanager.get(RES_DROP_EMPTY_SOUND, Sound.class);
		dropFullSound = game.getGame().assetmanager.get(RES_DROP_FULL_SOUND, Sound.class);

		//this.tex_can_standing = this.game.getGame().assetmanager.get(RES_CAN_STANDING, Texture.class);
		
		//this.tex_can_empty = this.game.getGame().assetmanager.get(RES_CAN_EMPTY, Texture.class);
		//this.tex_can_fillstates = new Texture[RES_CAN_FILLSTATES.length];
		
		//for(int i = 0; i < RES_CAN_FILLSTATES.length; i++)
		//	this.tex_can_fillstates[i] = this.game.getGame().assetmanager.get(RES_CAN_FILLSTATES[i], Texture.class);
	}
	
	public void update(float delta) {
		this.canHead.setPosition(this.pos_x, this.pos_y + HEAD_OFFSET_Y);
		this.wateringEffect.setPosition((this.pos_x + EFFECT_OFFSET_X) * Main.WINDOW_WIDTH, (this.pos_y + EFFECT_OFFSET_Y) * Main.WINDOW_HEIGHT);
		this.wateringEffect.update(delta);
		
		if(this.wateringPlants && this.wateringEffect.isComplete())
			this.wateringEffect.start();
		
		if(this.tap.isWaterRunning() && !this.isSelected()) {
			this.fillState += REFILL_SPEED * delta;
			
			if(this.fillState > MAX_FILL) {
				this.fillState = MAX_FILL;
				this.tap.setWaterRunning(false);
			}
		} else if(this.isSelected() && this.wateringPlants) {
			this.fillState -= WATERING_SPEED * delta;
			
			if(this.fillState < 0.0f) {
				TutorialManager.TutState.CAN_EMPTY.triggerStart();
				
				this.fillState = 0.0f;
				this.wateringPlants = false;
				this.wateringMusic.stop();
				this.wateringEffect.getEmitters().first().setContinuous(false);
			} else {
				// Check which plants are currently being watered(?)
				
				for(Plant p : this.game.getPlants()) {
					if(p.getBoundingBox().overlaps(this.canHead)) {
						p.addWater(WATERING_SPEED * delta);
						this.hasWateredThisTick = true;
					}
				}
			}
		}
	}
	
	public boolean checkClick(int mousex, int mousey) {
		float mX = (float) mousex / Main.WINDOW_WIDTH;
		float mY = (float) mousey / Main.WINDOW_HEIGHT;
		
		return mX >= this.pos_x && mX <= (this.pos_x + DRAW_WIDTH) && mY >= this.pos_y && mY <= (this.pos_y + DRAW_HEIGHT);
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		screenY = Main.WINDOW_HEIGHT - screenY;
		
		if(button != Input.Buttons.LEFT)
			return false;
		
		if(!this.selected) {
			if(this.tap.isWaterRunning()) {
				this.tap.setWaterRunning(false);
				
				return true;
			}
		} else {
			this.canTilted = false;
		}

		this.wateringEffect.getEmitters().first().setContinuous(false);
			

		this.wateringPlants = false;
		this.wateringMusic.stop();
		
		return false;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		screenY = Main.WINDOW_HEIGHT - screenY;
		
		if(button == Input.Buttons.LEFT) {
			
			if(!this.isSelected()) {
				if(this.checkClick(screenX, screenY)) {
					if(!(TutorialManager.isShowing() && TutorialManager.currentShowState() != TutState.WATERING)) {
						this.select(screenX, screenY);
						return true;
					}
					return false;
				} else if(this.tap.checkTapClick(screenX, screenY) && this.fillState < MAX_FILL) {
					System.out.println(TutorialManager.currentShowState());
					if(!(TutorialManager.isShowing() && TutorialManager.currentShowState() != TutState.CAN_EMPTY)){
						this.tap.setWaterRunning(true);
						TutorialManager.TutState.CAN_EMPTY.triggerStop();
					}
				}
			} else {
				if(this.tap.checkAllClicked(screenX,/* Main.WINDOW_HEIGHT - */screenY)) {
					this.unselect();
				
					return true;
				} else {
					this.canTilted = true;
					
					if(this.fillState > 0.0f) {
						this.wateringPlants = true;
						//waterSound.loop();
						if(!this.wateringMusic.isPlaying())
							this.wateringMusic.play();

						this.wateringEffect.reset();
						this.wateringEffect.setDuration(500);
						this.wateringEffect.getEmitters().first().setContinuous(true);
						this.wateringEffect.start();
					}
				}
			}
		}
		
		return false;
	}
	
	public void render(SpriteBatch sb) {
		//TODO: ReDo
		sb.draw(this.getTextureRegion(), this.pos_x * Main.WINDOW_WIDTH, this.pos_y * Main.WINDOW_HEIGHT, DRAW_WIDTH * Main.WINDOW_WIDTH, DRAW_HEIGHT * Main.WINDOW_HEIGHT);
		
		this.wateringEffect.draw(sb);
			
		this.hasWateredThisTick = false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		screenY = Main.WINDOW_HEIGHT - screenY;
		
		if(this.selected) {
			this.pos_x = (float) screenX / Main.WINDOW_WIDTH - DRAW_WIDTH / 2;
			this.pos_y = (float) screenY / Main.WINDOW_HEIGHT- DRAW_HEIGHT / 2;
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		screenY = Main.WINDOW_HEIGHT - screenY;
		
		if(this.selected) {
			this.pos_x = (float) screenX / Main.WINDOW_WIDTH- DRAW_WIDTH / 2;
			this.pos_y = (float) screenY / Main.WINDOW_HEIGHT - DRAW_HEIGHT / 2;
			
			return true;
		}
		
		return false;
	}
	
	
	public TextureRegion getTextureRegion() {
		int fillStage = 0;
		for(int i = 0; i < texReg_can[0].length; i++) {
			if(fillState < MAX_FILL - (float)i * (float)(MAX_FILL / (float)texReg_can[0].length)) {
				fillStage = i;
			} 
		}
		
		if(this.canTilted && !this.wateringPlants)
			return texReg_can[1][texReg_can[1].length - 1];
		
		return texReg_can[this.wateringPlants ? 1 : 0]
						 [fillStage];
	}
	
	
	public void select(int screenX, int screenY) {
		this.selected = true;
		game.setMouseInUse(true);
		
		this.pos_x = (float) screenX / Main.WINDOW_WIDTH - DRAW_WIDTH / 2;
		this.pos_y = (float) screenY / Main.WINDOW_HEIGHT - DRAW_HEIGHT / 2;
		takeSound.play();
	}

	public void unselect() {
		this.selected = false;
		game.setMouseInUse(false);
		
		this.pos_x = this.tap.getDockX(DRAW_WIDTH);
		this.pos_y = this.tap.getDockY();
		if(this.fillState > 0)dropFullSound.play();
		else dropEmptySound.play();
	}
	
	public boolean isSelected() {
		return this.selected;
	}
	
	public static void prefetch(Main game) {
		game.assetmanager.load(RES_CAN, Texture.class);
		game.assetmanager.load(RES_TAKE_SOUND, Sound.class);
		game.assetmanager.load(RES_WATER_MUSIC, Music.class);
		game.assetmanager.load(RES_DROP_EMPTY_SOUND, Sound.class);
		game.assetmanager.load(RES_DROP_FULL_SOUND, Sound.class);
		//game.assetmanager.load(RES_CAN_STANDING, Texture.class);
		
		//game.assetmanager.load(RES_CAN_EMPTY, Texture.class);
		/*for(String s : RES_CAN_FILLSTATES)
			game.assetmanager.load(s, Texture.class);*/
	}
	
	public static void dispose(Main game) {
		game.assetmanager.unload(RES_CAN);
		game.assetmanager.unload(RES_TAKE_SOUND);
		game.assetmanager.unload(RES_WATER_MUSIC);
		game.assetmanager.unload(RES_DROP_EMPTY_SOUND);
		game.assetmanager.unload(RES_DROP_FULL_SOUND);
		/*game.assetmanager.unload(RES_CAN_STANDING);
		
		game.assetmanager.unload(RES_CAN_EMPTY);
		for(String s : RES_CAN_FILLSTATES)
			game.assetmanager.unload(s);*/
	}
}
