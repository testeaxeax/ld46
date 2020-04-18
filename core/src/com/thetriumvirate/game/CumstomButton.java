package com.thetriumvirate.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class CumstomButton extends InputAdapter{

	
	private static final String RES_BTN_RELEASED = "graphics/btn_released.png";
	private static final String RES_BTN_PRESSED = "graphics/btn_pressed.png";
	
	
	private Main game;
	
	private Vector2 pos;
	private int width, height;
	private Texture btn_pressed_texture, btn_released_texture;
	
	private BitmapFont font;
	private GlyphLayout layout;
	private String btn_text = "";
	
	//pressed means the btn was pressed down, mainly for determining the texture; clicked means pressed & released and therefore activated
	private boolean pressed = false;
	private boolean clicked = false;
	
	public void CustomButton(Main game, Vector2 pos, String btn_text) {
		this.game = game;
		this.pos = pos;
		this.btn_text = btn_text;
		
		initBtnText(btn_text);
		
		btn_pressed_texture = game.assetmanager.get(RES_BTN_PRESSED, Texture.class);
		btn_released_texture = game.assetmanager.get(RES_BTN_RELEASED, Texture.class);
		
		width = btn_released_texture.getWidth();
		height = btn_released_texture.getHeight();
	}
	
	
	//apply custom textures
	public void CustomButton(Main game, Vector2 pos, Texture tex_pressed, Texture tex_released, String btn_text) {
		this.game = game;
		this.pos = pos;
		this.btn_text = btn_text;
		
		initBtnText(btn_text);
		
		
		btn_pressed_texture = tex_pressed;
		btn_released_texture = tex_released;
		
		width = btn_released_texture.getWidth();
		height = btn_released_texture.getHeight();

	}
	
	private void initBtnText(String btn_text) {
		layout = new GlyphLayout();
		font = game.fontloader.load(true);
		layout.setText(font, btn_text);
	}
	
	
	//empty for now
	public void update(float delta) {
		
	}
	
	public void render(SpriteBatch spritebatch) {
		spritebatch.draw(this.getTexture(), pos.x, pos.y);
		//subtraction of 2px in y-direction when pressed to make a 3D-link effect, to make it a more realistic buttonpress
		font.draw(spritebatch, layout, pos.x + width/2 - layout.width/2, pos.y + height/2 - layout.height/2 - (this.pressed ? 2 : 0));
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(!(button == Input.Buttons.LEFT))return false;
	
		//check if btn was pressed before and then released and therefore activated
		if(checkClick(screenX, screenY) && pressed) {
			clicked = true;
		}
		pressed = false;
		
		return false;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(!(button == Input.Buttons.LEFT))return false;
		
		if(checkClick(screenX, screenY)) {
			this.pressed = true;
			return true;
		}
		
		return false;
	}
	
	public boolean checkClick(int mousex, int mousey) {
		return mousex >= this.pos.x && mousex <= (this.pos.x + this.width) && mousey >= this.pos.y && mousey <= (this.pos.y + height);
	}
	
	public static void prefetch(Main game) {
		game.assetmanager.load(RES_BTN_PRESSED, Texture.class);
		game.assetmanager.load(RES_BTN_RELEASED, Texture.class);
	}
	
	public void dispose() {
		game.assetmanager.unload(RES_BTN_PRESSED);
		game.assetmanager.unload(RES_BTN_RELEASED);
	}
	
	public Texture getTexture() {
		return this.pressed ? this.btn_pressed_texture : this.btn_released_texture;
	}
}
