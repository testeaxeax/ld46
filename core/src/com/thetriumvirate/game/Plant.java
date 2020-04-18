package com.thetriumvirate.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Plant {

	private static final String RES_PLANTPOT = "graphics/pot_test.png";
	
	//Be aware to adjust SPRITEWIDTH and SPRITEHEIGHT after changing the source
	private static final String RES_PLANTSPRITES = "graphics/plant_test.png";
	
	
	private static final int POT_POS_Y = 100;
	private static final int POT_POS_X_OFFSET = 100;
	private static final int POT_WIDTH = 64;
	private static final int POT_HEIGHT = 64;
	private static final int SLOT_WIDTH = 100;
	
	//  MAX_GROWTH/GROWTH_PER_SEC is the lower bar of the levels duration in seconds
	private static final int MAX_GROWTH = 200; 
	private static final int GROWTH_PER_SEC = 1;
	
	
	private static final int MIN_WATERLEVEL = 0;
	private static final int MAX_WATERLEVEL = 1000;
	private static final int SUFFICIENT_WATERLEVEL = 200;//minimum waterlevel for the plant to grow
	
	private static final int MAX_DECAY = MAX_WATERLEVEL;
	
	private static final int GROWTHSTAGES = 8;
	private static final int DECAYSTAGES = 4;
	private static final int SPRITEWIDTH = 64;
	private static final int SPRITEHEIGHT = 128;
	
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
	
	//posSlot: int from 0-7, or whatever fits on the screen
	public Plant(GameScreen gamescreen, int posSlot) {
		this.gamescreen = gamescreen;
		game = gamescreen.getGame();
		
		pot_pos = new Vector2(POT_POS_X_OFFSET + posSlot * SLOT_WIDTH, POT_POS_Y);
		plant_pos = new Vector2(pot_pos.x, pot_pos.y + POT_HEIGHT - 4); // -4 offset so the plant is "inside the pot" bcs the pot overlaps over the plant; 
		
		this.boundingBox = new Rectangle(pot_pos.x, pot_pos.y, POT_WIDTH, POT_HEIGHT + SPRITEHEIGHT);
		
		//init resources
		plantpot_texture = game.assetmanager.syncGet(RES_PLANTPOT, Texture.class);
		plantsprites_texture = game.assetmanager.syncGet(RES_PLANTSPRITES, Texture.class);
		initPlantTextures();
	}
	
	
	public void update(float delta) {
		if(growing)growth += delta * GROWTH_PER_SEC;
		
		waterlevel -= waterlossPerSec * delta;
		if(waterlevel <= MIN_WATERLEVEL)waterlevel = MIN_WATERLEVEL;
		
		decay = MAX_DECAY - waterlevel;
		
		if(waterlevel <= SUFFICIENT_WATERLEVEL) {
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
		spritebatch.draw(plantpot_texture, pot_pos.x, pot_pos.y, POT_WIDTH, POT_HEIGHT);
		
		spritebatch.draw(this.getTextureRegion(), plant_pos.x, plant_pos.y, SPRITEWIDTH, SPRITEHEIGHT);

		
		Color before = new Color(spritebatch.getColor());
		
		spritebatch.setColor(0.0f, 1.0f, 0.0f, 0.5f);
		spritebatch.draw(this.gamescreen.tex_debugrect, this.boundingBox.x, this.boundingBox.y, this.boundingBox.width, this.boundingBox.height);
	
		spritebatch.setColor(before);
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
	
	public void dispose() {
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
	}


	public Rectangle getBoundingBox() {
		return this.boundingBox;
	}
}
