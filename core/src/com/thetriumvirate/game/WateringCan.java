package com.thetriumvirate.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class WateringCan extends InputAdapter{
	// Resource paths
	private static final String RES_CAN = "graphics/wateringcan.png";
	private static final String RES_CAN_STANDING = "graphics/wateringcanstanding.png";

	private static final int DRAW_WIDTH = 120;
	private static final int DRAW_HEIGHT = 120;
	
	private boolean selected, wateringPlants;
	
	private final Main game;
	private final Texture tex_can, tex_can_standing;
	private final Tap tap;
	
	private int pos_x, pos_y;
	
	public WateringCan(final Main instance, final Tap myTap, InputMultiplexer inputmultiplexer) {
		this.game = instance;
		this.tap = myTap;
		
		inputmultiplexer.addProcessor(this);
		
		this.selected = false;
		this.wateringPlants = false;
		
		this.pos_x = this.tap.getDockX(DRAW_WIDTH);
		this.pos_y = this.tap.getDockY();
		
		this.tex_can = this.game.assetmanager.syncGet(RES_CAN, Texture.class);
		this.tex_can_standing = this.game.assetmanager.syncGet(RES_CAN_STANDING, Texture.class);
	}
	
	public void update() {
		
	}
	
	public boolean checkClick(int mousex, int mousey) {
		return mousex >= this.pos_x && mousex <= (this.pos_x + DRAW_WIDTH) && mousey >= this.pos_y && mousey <= (this.pos_y + DRAW_HEIGHT);
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
		}
		
		this.wateringPlants = false;
		
		return false;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		screenY = Main.WINDOW_HEIGHT - screenY;
		
		if(button == Input.Buttons.LEFT) {
			
			if(!this.isSelected()) {
				if(this.checkClick(screenX, screenY)) {
					this.select(screenX, screenY);
					return true;
				} else if(this.tap.checkTapClick(screenX, screenY)) {
					this.tap.setWaterRunning(true);
				}
			} else {
				if(this.tap.checkAllClicked(screenX, Main.WINDOW_HEIGHT - screenY)) {
					this.unselect();
				
					return true;
				} else {
					this.wateringPlants = true;
				}
			}
		}
		
		return false;
	}
	
	public void render(SpriteBatch sb) {
		sb.begin();
		if(this.wateringPlants)
			sb.draw(tex_can, this.pos_x, this.pos_y, DRAW_WIDTH, DRAW_HEIGHT);
		else
			sb.draw(tex_can_standing, this.pos_x, this.pos_y, DRAW_WIDTH, DRAW_HEIGHT);
		sb.end();
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		screenY = Main.WINDOW_HEIGHT - screenY;
		
		if(this.selected) {
			this.pos_x = screenX;
			this.pos_y = screenY;
			
			return true;
		}
		
		return false;
	}
	
	public void select(int screenX, int screenY) {
		this.selected = true;
		
		this.pos_x = screenX - DRAW_WIDTH / 2;
		this.pos_y = screenY - DRAW_HEIGHT / 2;
	}

	public void unselect() {
		this.selected = false;
		
		this.pos_x = this.tap.getDockX(DRAW_WIDTH);
		this.pos_y = this.tap.getDockY();
	}
	
	public boolean isSelected() {
		return this.selected;
	}
	
	public void unload() {
		this.game.assetmanager.unload(RES_CAN);
		this.game.assetmanager.unload(RES_CAN_STANDING);
	}
}
