package com.thetriumvirate.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class WateringCan extends InputAdapter{
	// Resource paths
	private static final String RES_CAN = "graphics/wateringcan.png";
	private static final String RES_CAN_STANDING = "graphics/wateringcanstanding.png";
	private static final String RES_CAN_EMPTY = "graphics/canempty.png";
	private static final String RES_CAN_FILLSTATES[] = {"graphics/canstate1.png", "graphics/canstate2.png", "graphics/canstate3.png", "graphics/canstate4.png", "graphics/canstate5.png"};

	private static final int DRAW_WIDTH = 120;
	private static final int DRAW_HEIGHT = 120;
	
	private boolean selected, wateringPlants;
	
	private final GameScreen game;
	private final Texture tex_can, tex_can_standing, tex_can_empty, tex_can_fillstates[];
	private final Tap tap;
	
	private int pos_x, pos_y;
	
	// min: 0 max: 1000 (ml?)
	private float fillState;
	
	// max fill
	private static final float MAX_FILL = 1000.0f;
	// ein Refill dauert 1000 / 100 = 10 Sekunden
	private static final float REFILL_SPEED = 200.0f;
	
	private static final float WATERING_SPEED = 100.0f;

	private static final int HEAD_SIZE = 45;
	private static final int HEAD_OFFSET_Y = 10;
	private Rectangle canHead;
	private boolean hasWateredThisTick = false;
	
	public WateringCan(final GameScreen gameScreen, final Tap myTap) {
		this.game = gameScreen;
		this.tap = myTap;
		
		this.selected = false;
		this.wateringPlants = false;
		
		// start with full can
		this.fillState = MAX_FILL;
		
		this.pos_x = this.tap.getDockX(DRAW_WIDTH);
		this.pos_y = this.tap.getDockY();
		
		this.canHead = new Rectangle(this.pos_x, this.pos_y + HEAD_OFFSET_Y, HEAD_SIZE, HEAD_SIZE);
		
		this.tex_can = this.game.getGame().assetmanager.syncGet(RES_CAN, Texture.class);
		this.tex_can_standing = this.game.getGame().assetmanager.syncGet(RES_CAN_STANDING, Texture.class);
		
		this.tex_can_empty = this.game.getGame().assetmanager.get(RES_CAN_EMPTY, Texture.class);
		this.tex_can_fillstates = new Texture[RES_CAN_FILLSTATES.length];
		
		for(int i = 0; i < RES_CAN_FILLSTATES.length; i++)
			this.tex_can_fillstates[i] = this.game.getGame().assetmanager.get(RES_CAN_FILLSTATES[i], Texture.class);
	}
	
	public void update(float delta) {
		this.canHead.setPosition(this.pos_x, this.pos_y + HEAD_OFFSET_Y);
		
		if(this.tap.isWaterRunning() && !this.isSelected()) {
			this.fillState += REFILL_SPEED * delta;
			
			if(this.fillState > MAX_FILL) {
				this.fillState = MAX_FILL;
				this.tap.setWaterRunning(false);
			}
		} else if(this.isSelected() && this.wateringPlants) {
			// TODO DEBUGGING --> uncomment
			
			this.fillState -= WATERING_SPEED * delta;
			
			if(this.fillState < 0.0f) {
				this.fillState = 0.0f;
				this.wateringPlants = false;
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
					if(this.fillState > 0.0f)
						this.wateringPlants = true;
				}
			}
		}
		
		return false;
	}
	
	public void render(SpriteBatch sb) {
		// TODO: add sprinkling animation
		
//		sb.begin();
//		if(this.wateringPlants)
//			sb.draw(tex_can, this.pos_x, this.pos_y, DRAW_WIDTH, DRAW_HEIGHT);
//		else {
			//sb.draw(tex_can_standing, this.pos_x, this.pos_y, DRAW_WIDTH, DRAW_HEIGHT);
			
			sb.draw(this.tex_can_empty, this.pos_x, this.pos_y, DRAW_WIDTH, DRAW_HEIGHT);
			
			if(this.fillState > 0.0f) {
				float fillPercentage = this.fillState / MAX_FILL;
				
				int selectedTexture = 0;
				
				if(this.fillState == MAX_FILL)
					selectedTexture = 4;
				else if(fillPercentage > 0.75f)
					selectedTexture = 3;
				else if(fillPercentage > 0.5f)
					selectedTexture = 2;
				else if(fillPercentage > 0.25f)
					selectedTexture = 1;
				
				
				sb.draw(this.tex_can_fillstates[selectedTexture], this.pos_x, this.pos_y, DRAW_WIDTH, DRAW_HEIGHT);
			}
			
//		}
//		sb.end();
		
		Color c = new Color(sb.getColor());
		
		if(this.fillState <= 0.0f)
			sb.setColor(1.0f, 0.0f, 0.0f, 0.5f);
		else if(this.hasWateredThisTick)
			sb.setColor(0.0f, 0.0f, 1.0f, 0.5f);
		else
			sb.setColor(0.0f, 0.0f, 0.0f, 0.3f);
		
		sb.draw(this.game.tex_debugrect, this.canHead.x, this.canHead.y, this.canHead.width, this.canHead.height);
		
		sb.setColor(c);
		this.hasWateredThisTick = false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		screenY = Main.WINDOW_HEIGHT - screenY;
		
		if(this.selected) {
			this.pos_x = screenX - DRAW_WIDTH / 2;
			this.pos_y = screenY - DRAW_HEIGHT / 2;
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		screenY = Main.WINDOW_HEIGHT - screenY;
		
		if(this.selected) {
			this.pos_x = screenX - DRAW_WIDTH / 2;
			this.pos_y = screenY - DRAW_HEIGHT / 2;
			
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
	
	public static void prefetch(Main game) {
		game.assetmanager.load(RES_CAN, Texture.class);
		game.assetmanager.load(RES_CAN_STANDING, Texture.class);
		
		game.assetmanager.load(RES_CAN_EMPTY, Texture.class);
		for(String s : RES_CAN_FILLSTATES)
			game.assetmanager.load(s, Texture.class);
	}
	
	public void dispose() {
		this.game.getGame().assetmanager.unload(RES_CAN);
		this.game.getGame().assetmanager.unload(RES_CAN_STANDING);
		
		this.game.getGame().assetmanager.unload(RES_CAN_EMPTY);
		for(String s : RES_CAN_FILLSTATES)
			this.game.getGame().assetmanager.unload(s);
	}
}
