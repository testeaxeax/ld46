package com.thetriumvirate.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class TutorialManager {
	private static final int TEXREG_WIDTH = 1024;
	private static final int TEXREG_HEIGHT = 800;
	
	private static final String RES_TEX = "graphics/tutorials.png";
	private static Texture tex;
	private static TextureRegion[] tex_regions;
	
	public static enum TutState{
		TEMP_LOW(0), TEMP_HIGH(1), SHUTTER(2), WATERING(3), CAN_EMPTY(4);
		
		private int texIndex;
		private boolean isShown, hasBeenShown;
		
		private TutState(int texIndex) {
			this.texIndex = texIndex;
			
			this.isShown = false;
			this.hasBeenShown = false;
		}
		
		public boolean isShown() {
			return this.isShown;
		}
		
		public void triggerStart() {
			if(!this.isShown && !this.hasBeenShown)
				this.isShown = true;
		}
		
		public void triggerStop() {
			if(this.isShown) {
				this.isShown = false;
				this.hasBeenShown = true;
			}
		}
	}
	
	public static void render(SpriteBatch sb) {
		for(TutState ts : TutState.values()) {
			if(ts.isShown) {
				sb.draw(tex_regions[ts.texIndex], 0, 0, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
			}
		}
	}
	
	public static void load(Main game) {
		tex = game.assetmanager.get(RES_TEX, Texture.class);
		
		tex_regions = new TextureRegion[TutState.values().length];
		for(int i = 0; i < tex_regions.length; i++) {
			tex_regions[i] = new TextureRegion(tex, i * TEXREG_WIDTH, 0, TEXREG_WIDTH, TEXREG_HEIGHT);
		}
	}
	
	public static void prefetch(Main game) {
		game.assetmanager.load(RES_TEX, Texture.class);
	}
	
	public static void dispatch(Main game) {
		game.assetmanager.unload(RES_TEX);
	}
}
