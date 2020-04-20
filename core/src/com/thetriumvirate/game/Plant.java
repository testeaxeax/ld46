package com.thetriumvirate.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Plant {

	private static final String RES_PLANTPOT = "graphics/pot_test.png";
	
	//Be aware to adjust SPRITEWIDTH and SPRITEHEIGHT after changing the source
	private static final String RES_PLANTSPRITES = "graphics/plant.png";
	
	
	private static final float POT_POS_Y = (float)(45*4) / 800f;
	private static final float POT_POS_X_OFFSET = (float)(38*4) / 1024f;
	private static final float POT_WIDTH = 64f / 1024f;
	private static final float POT_HEIGHT = 64f / 800f;
	private static final float SLOT_WIDTH = 100f / 1024f;
	
	//  MAX_GROWTH/GROWTH_PER_SEC is the lower bar of the levels duration in seconds
	private static final int MAX_GROWTH = 200; 
	private static final int GROWTH_PER_SEC = 1;
	
	
	private static final int MIN_WATERLEVEL = 0;
	private static final int MAX_WATERLEVEL = 1000;
	private static final int SUFFICIENT_WATERLEVEL = 200;//minimum waterlevel for the plant to grow
	
	private static final int MAX_DECAY = MAX_WATERLEVEL;
	
	// The min and max temp for the plant to grow normally
	public static final int TEMP_MAX = 65;
	public static final int TEMP_MIN = 30;
	
	private static final int GROWTHSTAGES = 8;
	private static final int DECAYSTAGES = 5;
	private static final int SPRITEWIDTH = 64;
	private static final int SPRITEHEIGHT = 128;
	private static final float SPRITEWIDTH_DRAW = (float) SPRITEWIDTH / 1024f;
	private static final float SPRITEHEIGHT_DRAW = (float) SPRITEHEIGHT / 800f;
	
	private final GameScreen gamescreen;
	private final Main game;
	
	
	private final Texture plantpot_texture;
	private final Texture plantsprites_texture;
	private TextureRegion[][] plant_sprites;
	
	
	private float growth = 0;
	private boolean fullyGrown = false;
	private boolean growing = true;
	private int growthStage = 0;
	
	private float decay = 0;
	private int decayStage = 0;
	
	private float waterlevel = 1000;
	private float waterlossPerSec = 25;
	
	private Vector2 pot_pos;
	private Vector2 plant_pos;
	
	private Rectangle boundingBox;
	
	private final ParticleEffect splasheffect;
	private static final String RES_PEFFECT_SPLASH = "particleeffects/watersplash.p";
	private static final String RES_PEFFECT_SPLASH_FILES = "particleeffects/";
	
	//posSlot: int from 0-7, or whatever fits on the screen
	public Plant(GameScreen gamescreen, int posSlot) {
		this.gamescreen = gamescreen;
		game = gamescreen.getGame();
		
		pot_pos = new Vector2(POT_POS_X_OFFSET + posSlot * SLOT_WIDTH, POT_POS_Y);
		plant_pos = new Vector2(pot_pos.x, pot_pos.y + POT_HEIGHT - (4f / 800f)); // -4 offset so the plant is "inside the pot" bcs the pot overlaps over the plant; 
		
		// TODO: Remove the scaling once the coordinates are relative
		this.boundingBox = new Rectangle(pot_pos.x, pot_pos.y, POT_WIDTH, POT_HEIGHT + (float) SPRITEHEIGHT / 128f);
		
		//init resources
		plantpot_texture = game.assetmanager.get(RES_PLANTPOT, Texture.class);
		plantsprites_texture = game.assetmanager.get(RES_PLANTSPRITES, Texture.class);
		
		this.splasheffect = new ParticleEffect();
		this.splasheffect.loadEmitters(Gdx.files.internal(RES_PEFFECT_SPLASH));
		this.splasheffect.loadEmitterImages(Gdx.files.internal(RES_PEFFECT_SPLASH_FILES));
		this.splasheffect.setPosition(plant_pos.x + SPRITEWIDTH / 2, plant_pos.y);
		
		initPlantTextures();
	}
	
	//Constructor for Plants to be used in Menuscrenn or Gameoverscreen. These plants are not intended to receive any update() calls
	public Plant(GameScreen gamescreen, int posX, int posY, int growthStage, int decayStage) {
		this.gamescreen = gamescreen;
		game = gamescreen.getGame();
		
		pot_pos = new Vector2(posX, posY);
		plant_pos = new Vector2(posX, posY + POT_HEIGHT - (4f / 800f)); // -4 offset so the plant is "inside the pot" bcs the pot overlaps over the plant; 
		
		this.boundingBox = new Rectangle(pot_pos.x, pot_pos.y, POT_WIDTH, POT_HEIGHT + SPRITEHEIGHT_DRAW);
		
		this.splasheffect = new ParticleEffect();
		this.splasheffect.loadEmitters(Gdx.files.internal(RES_PEFFECT_SPLASH));
		this.splasheffect.loadEmitterImages(Gdx.files.internal("particleeffects/watersplash/"));
		this.splasheffect.setPosition((plant_pos.x + SPRITEWIDTH_DRAW / 2f) * Main.WINDOW_WIDTH, plant_pos.y * Main.WINDOW_HEIGHT);
		
		this.decayStage = decayStage;
		this.growthStage = growthStage;
		//init resources
		plantpot_texture = game.assetmanager.get(RES_PLANTPOT, Texture.class);
		plantsprites_texture = game.assetmanager.get(RES_PLANTSPRITES, Texture.class);
		initPlantTextures();
	}
	
	public void update(float delta, float shutteralpha, float currentTemp) {
		this.splasheffect.setPosition((plant_pos.x + SPRITEWIDTH_DRAW / 2f) * Main.WINDOW_WIDTH, plant_pos.y * Main.WINDOW_HEIGHT);
		this.splasheffect.update(delta);
		
		// check the Temp and react to it; if it's...
		// too high: speed up decay process and remove part of the water
		// too low:  stop growth process
		if(currentTemp > TEMP_MAX) {
			decay += delta * 80;
			
			this.waterlevel -= this.waterlevel * 0.25 * delta;
		} else if(currentTemp < TEMP_MIN) {
			//debug, uncomment thereafter
			//this.growing = false;
		}
		
		// the darker it is, the slower plants grow		
		if(growing)growth += delta * GROWTH_PER_SEC * (1.0f - shutteralpha);
		
		waterlevel -= waterlossPerSec * delta;
		
		
		if(waterlevel <= MIN_WATERLEVEL)waterlevel = MIN_WATERLEVEL;
		
		// let the plants decay faster if there's not enough light available
		decay = MAX_DECAY - waterlevel - 2 * shutteralpha * waterlevel;
		
		// stop growth completely if its too dark
		if(waterlevel <= SUFFICIENT_WATERLEVEL || shutteralpha > 0.85f) {
			growing = false;
		}else {
			growing = true;
		}
		
		if(growth >= MAX_GROWTH) {
			growth = MAX_GROWTH;
			fullyGrown = true;
		}
		
		determineGrowthStage();
		determineDecayStage();
		
		
	}
	
	public void render(SpriteBatch spritebatch) {
		//first render the plant
		
		
		//secondly, render the pot
		spritebatch.draw(plantpot_texture, pot_pos.x * Main.WINDOW_WIDTH, pot_pos.y * Main.WINDOW_HEIGHT, POT_WIDTH * Main.WINDOW_WIDTH, POT_HEIGHT * Main.WINDOW_HEIGHT);
		
		spritebatch.draw(this.getTextureRegion(), plant_pos.x * Main.WINDOW_WIDTH, plant_pos.y * Main.WINDOW_HEIGHT, SPRITEWIDTH_DRAW * Main.WINDOW_WIDTH, SPRITEHEIGHT_DRAW * Main.WINDOW_HEIGHT);

		this.splasheffect.draw(spritebatch);
	}
	
	
	private void initPlantTextures() {
		
		/*plant_sprites = new TextureRegion[GROWTHSTAGES][];
		
		for(int i = 0; i < GROWTHSTAGES; i++) {
			plant_sprites[i] = new TextureRegion[DECAYSTAGES];
		}
		
		for(int i = 0; i < GROWTHSTAGES; i++) {
			for(int j = 0; j < DECAYSTAGES; j++) {
				plant_sprites[i][j] = new TextureRegion(plantsprites_texture, SPRITEWIDTH*i, SPRITEHEIGHT*j, SPRITEWIDTH, SPRITEHEIGHT);
			}
		}*/
		//lol, wenn mna erst danach liest dass man für den foo oben einfach auch ne Methode hätte...
		plant_sprites = TextureRegion.split(plantsprites_texture, SPRITEWIDTH, SPRITEHEIGHT);
	}
	
	private void determineGrowthStage() {
		for(int i = 0; i < GROWTHSTAGES; i++) {
			if(growth >= (MAX_GROWTH*i)/GROWTHSTAGES) growthStage = i;
			else break;
		}
	}
	
	
	private void determineDecayStage() {
		//Maybe add another scaling: not linear but at different gameplay-relevant points.
		for(int i = 0; i < DECAYSTAGES; i++) {
			if(decay >= (MAX_DECAY*i)/DECAYSTAGES) decayStage = i;
			else break;
		}
	}
	
	public static void prefetch(Main game) {
		game.assetmanager.load(RES_PLANTPOT, Texture.class);
		game.assetmanager.load(RES_PLANTSPRITES, Texture.class);
	}
	
	public static void dispose(Main game) {
		game.assetmanager.unload(RES_PLANTPOT);
		game.assetmanager.unload(RES_PLANTSPRITES);
	}

	//Getters
	public Vector2 getPosition() {
		return pot_pos.cpy();
	}
	
	public boolean isFullyGrown() {
		return fullyGrown;
	}
	
	public boolean isDecayed() {
		return (decay >= MAX_DECAY);
	}
	
	public TextureRegion getTextureRegion() {
		return plant_sprites[decayStage][growthStage];
	}
	
	//Setters
	public void setGrowing(boolean growing) {
		this.growing = growing;
	}
	
	//Add an absolute amount of water
	public void addWater(float waterAmount) {
		this.waterlevel += waterAmount;
		if(this.waterlevel >= MAX_WATERLEVEL) waterlevel = MAX_WATERLEVEL;
		
		// this assumes that the plant is being watered by the watering can
		if(this.splasheffect.isComplete()) {
			this.splasheffect.reset(false);
			this.splasheffect.start();
		}
	}


	public Rectangle getBoundingBox() {
		return this.boundingBox;
	}
}
