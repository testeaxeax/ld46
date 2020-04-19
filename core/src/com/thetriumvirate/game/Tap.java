package com.thetriumvirate.game;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Tap extends InputAdapter {
	// Resource paths
	private static final String RES_TAP = "graphics/tap.png";
	//private static final String RES_TAPRUNNING = "graphics/taprunning.png";
	private static final String RES_PLANK = "graphics/tapplank.png";
	// Sound cannot be longer than a few seconds
	private static final String RES_TAP_OPENING_SOUND = "audio/tap-opening.wav";
	private static final String RES_TAP_WATER_RUNNING_SOUND = "audio/tap-running.wav";

	private static final float PLANK_WIDTH = 200f / 1024f;
	private static final float PLANK_HEIGHT = 60f / 800f;
	private static final float TAP_WIDTH = 64f / 1024f;
	private static final float TAP_HEIGHT = 64f / 800f;
	private static final float TAP_OFFSET_X = PLANK_WIDTH / 2 - TAP_WIDTH / 4;
	private static final float TAP_OFFSET_Y = 160f / 800f;

	private final Main game;
	private final Texture tex_tap;
	private TextureRegion[] texReg_tap;
	//private final Texture tex_taprunning;
	private final Texture tex_plank;
	private final Sound tapOpeningSound, tapWaterRunningSound;

	private float pos_x, pos_y;
	private boolean waterRunning;

	public Tap(final Main instance) {
		this.game = instance;

		this.waterRunning = false;
		this.pos_x = 600 / 1024f;
		this.pos_y = 300 / 800f;

		this.tex_tap = this.game.assetmanager.get(RES_TAP, Texture.class);
		texReg_tap = new TextureRegion[2];//Two states: open and closed. Increase if necessary
		texReg_tap[0] = new TextureRegion(tex_tap, 0, 0, 32, 32);//tap closed
		texReg_tap[1] = new TextureRegion(tex_tap, 0, 32, 32, 32);//tap open
		//this.tex_taprunning = this.game.assetmanager.get(RES_TAPRUNNING, Texture.class);

		this.tex_plank = this.game.assetmanager.get(RES_PLANK, Texture.class);
		tapOpeningSound = game.assetmanager.get(RES_TAP_OPENING_SOUND, Sound.class);
		tapWaterRunningSound = game.assetmanager.get(RES_TAP_WATER_RUNNING_SOUND, Sound.class);
	}

	public void render(SpriteBatch sb) {
//		sb.begin();
		sb.draw(tex_plank, this.pos_x * Main.WINDOW_WIDTH, this.pos_y * Main.WINDOW_HEIGHT, PLANK_WIDTH * Main.WINDOW_WIDTH, PLANK_HEIGHT * Main.WINDOW_HEIGHT);

		if (this.waterRunning)
			sb.draw(texReg_tap[1], (this.pos_x + TAP_OFFSET_X) * Main.WINDOW_WIDTH, (this.pos_y + TAP_OFFSET_Y) * Main.WINDOW_HEIGHT, TAP_WIDTH * Main.WINDOW_WIDTH, TAP_HEIGHT * Main.WINDOW_HEIGHT);
		else
			sb.draw(texReg_tap[0], (this.pos_x + TAP_OFFSET_X) * Main.WINDOW_WIDTH, (this.pos_y + TAP_OFFSET_Y) * Main.WINDOW_HEIGHT, TAP_WIDTH * Main.WINDOW_WIDTH, TAP_HEIGHT * Main.WINDOW_HEIGHT);

//		game.spritebatch.end();
	}

	public void setWaterRunning(boolean running) {
		if(!waterRunning && running) {
			tapOpeningSound.play();
			tapWaterRunningSound.loop();
		}
		else if(waterRunning && !running) {
			tapOpeningSound.play();
			tapWaterRunningSound.stop();
		}
		this.waterRunning = running;
	}

	public boolean checkAllClicked(int screenX, int screenY) {
		float mX = (float) screenX / Main.WINDOW_WIDTH;
		float mY = (float) screenY / Main.WINDOW_HEIGHT;
		
		return  mX >= pos_x && mX <= pos_x + PLANK_WIDTH && 
				mY >= pos_y	&& mY <= pos_y + TAP_OFFSET_Y + TAP_HEIGHT;
	}

	public boolean checkTapClick(int screenX, int screenY) {
		float mX = (float) screenX / Main.WINDOW_WIDTH;
		float mY = (float) screenY / Main.WINDOW_HEIGHT;
		
		return mX >= this.pos_x + TAP_OFFSET_X && mX <= this.pos_x + TAP_OFFSET_X + TAP_WIDTH
				&& mY >= this.pos_y + TAP_OFFSET_Y && mY <= this.pos_y + TAP_OFFSET_Y + TAP_HEIGHT;
	}

	// TODO: relativiern
	public float getDockX(float canWidth) {
		return this.pos_x + PLANK_WIDTH / 2 - canWidth / 2;
	}

	public float getDockY() {
		return this.pos_y + PLANK_HEIGHT / 2;
	}

	public static void prefetch(Main game) {
		game.assetmanager.load(RES_TAP, Texture.class);
		//game.assetmanager.load(RES_TAPRUNNING, Texture.class);
		game.assetmanager.load(RES_PLANK, Texture.class);
		game.assetmanager.load(RES_TAP_OPENING_SOUND, Sound.class);
		game.assetmanager.load(RES_TAP_WATER_RUNNING_SOUND, Sound.class);
	}

	public static void dispose(Main game) {
		game.assetmanager.unload(RES_TAP);
		game.assetmanager.unload(RES_PLANK);
		game.assetmanager.unload(RES_TAP_OPENING_SOUND);
		game.assetmanager.unload(RES_TAP_WATER_RUNNING_SOUND);
		//game.assetmanager.unload(RES_TAPRUNNING);
	}

	public boolean isWaterRunning() {
		return this.waterRunning;
	}
}
