package com.thetriumvirate.game;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Tap extends InputAdapter {
	// Resource paths
	private static final String RES_TAP = "graphics/tap.png";
	private static final String RES_TAPRUNNING = "graphics/taprunning.png";
	private static final String RES_PLANK = "graphics/tapplank.png";

	private static final int PLANK_WIDTH = 200;
	private static final int PLANK_HEIGHT = 60;
	private static final int TAP_WIDTH = 120;
	private static final int TAP_HEIGHT = 120;
	private static final int TAP_OFFSET_X = 0;
	private static final int TAP_OFFSET_Y = 150;

	private final Main game;
	private final Texture tex_tap;
	private final Texture tex_taprunning;
	private final Texture tex_plank;

	private int pos_x, pos_y;
	private boolean waterRunning;

	public Tap(final Main instance) {
		this.game = instance;

		this.waterRunning = false;
		this.pos_x = 600;
		this.pos_y = 300;

		this.tex_tap = this.game.assetmanager.syncGet(RES_TAP, Texture.class);
		this.tex_taprunning = this.game.assetmanager.syncGet(RES_TAPRUNNING, Texture.class);

		this.tex_plank = this.game.assetmanager.syncGet(RES_PLANK, Texture.class);
	}

	public void render(SpriteBatch sb) {
		sb.begin();
		sb.draw(tex_plank, this.pos_x, this.pos_y, PLANK_WIDTH, PLANK_HEIGHT);

		if (this.waterRunning)
			sb.draw(tex_taprunning, this.pos_x + TAP_OFFSET_X, this.pos_y + TAP_OFFSET_Y, TAP_WIDTH, TAP_HEIGHT);
		else
			sb.draw(tex_tap, this.pos_x + TAP_OFFSET_X, this.pos_y + TAP_OFFSET_Y, TAP_WIDTH, TAP_HEIGHT);

		game.spritebatch.end();
	}

	public void setWaterRunning(boolean running) {
		this.waterRunning = running;
	}

	public boolean checkAllClicked(int mouseX, int mouseY) {
		return mouseX >= pos_x && mouseX <= pos_x + PLANK_WIDTH && mouseY >= pos_y
				&& mouseY <= pos_y + TAP_OFFSET_Y + TAP_HEIGHT;
	}

	public boolean checkTapClick(int mouseX, int mouseY) {
		return mouseX >= this.pos_x + TAP_OFFSET_X && mouseX <= this.pos_x + TAP_OFFSET_X + TAP_WIDTH
				&& mouseY >= this.pos_y + TAP_OFFSET_Y && mouseY <= this.pos_y + TAP_OFFSET_Y + TAP_HEIGHT;
	}

	public int getDockX(int canWidth) {
		return this.pos_x + PLANK_WIDTH / 2 - canWidth / 2;
	}

	public int getDockY() {
		return this.pos_y + PLANK_HEIGHT;
	}

	public static void prefetch(Main game) {
		game.assetmanager.load(RES_TAP, Texture.class);
		game.assetmanager.load(RES_TAPRUNNING, Texture.class);
		game.assetmanager.load(RES_PLANK, Texture.class);
	}

	public void unload() {
		this.game.assetmanager.unload(RES_TAP);
		this.game.assetmanager.unload(RES_PLANK);
		this.game.assetmanager.unload(RES_TAPRUNNING);
	}

	public boolean isWaterRunning() {
		return this.waterRunning;
	}
}
