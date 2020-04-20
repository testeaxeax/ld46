package com.thetriumvirate.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

public class CustomButton extends InputAdapter{

	
	private static final String RES_BTN_RELEASED = "graphics/btn_released.png";
	private static final String RES_BTN_PRESSED = "graphics/btn_pressed.png";
	// Sound cannot be longer than a few seconds
	private static final String RES_BTN_CLICK_SOUND = "audio/button-click.wav";
	
	
	private Main game;
	
	private Vector2 pos;
	private int width, height;
	private Texture btn_pressed_texture, btn_released_texture;
	private TextureRegion[] texReg;
	private Sound btnClick;
	
	private BitmapFont font;
	private GlyphLayout layout;
	
	//pressed means the btn was pressed down, mainly for determining the texture; clicked means pressed & released and therefore activated
	private boolean pressed = false;
	private boolean clicked = false;
	
	
	public CustomButton(Main game, Vector2 pos, String btn_text, int fontsize) {
		this.game = game;
		this.pos = pos;
		
		btn_pressed_texture = game.assetmanager.get(RES_BTN_PRESSED, Texture.class);
		btn_released_texture = game.assetmanager.get(RES_BTN_RELEASED, Texture.class);
		btnClick = game.assetmanager.get(RES_BTN_CLICK_SOUND, Sound.class);
		
		texReg = new TextureRegion[2];
		texReg[0] = new TextureRegion(btn_released_texture);
		texReg[1] = new TextureRegion(btn_pressed_texture);

		width = Main.DEFAULT_BUTTON_WIDTH;
		height = Main.DEFAULT_BUTTON_HEIGHT;
		// Needs to be called after width and height are set
		initBtnText(btn_text, fontsize);
	}
	
	
	//apply custom textures
	public CustomButton(Main game, Vector2 pos, Texture tex_pressed, Texture tex_released, String btn_text, int fontsize) {
		this.game = game;
		this.pos = pos;
		
		
		btn_pressed_texture = tex_pressed;
		btn_released_texture = tex_released;
		
		texReg = new TextureRegion[2];
		texReg[0] = new TextureRegion(btn_released_texture);
		texReg[1] = new TextureRegion(btn_pressed_texture);
		
		btnClick = game.assetmanager.get(RES_BTN_CLICK_SOUND, Sound.class);
		
		width = Main.DEFAULT_BUTTON_WIDTH;
		height = Main.DEFAULT_BUTTON_HEIGHT;
		// Needs to be called after width and height are set
		initBtnText(btn_text, fontsize);
	}
	
	public CustomButton(Main game, Vector2 pos, TextureRegion[] texReg, String btn_text, int fontsize) {
		this.game = game;
		this.pos = pos;
		
		
		//btn_pressed_texture = tex_pressed;
		//btn_released_texture = tex_released;
		this.texReg = texReg;
		btnClick = game.assetmanager.get(RES_BTN_CLICK_SOUND, Sound.class);
		
		width = Main.DEFAULT_BUTTON_WIDTH;
		height = Main.DEFAULT_BUTTON_HEIGHT;
		// Needs to be called after width and height are set
		initBtnText(btn_text, fontsize);
	}
	
	
	private void initBtnText(String btn_text, int fontsize) {
		layout = new GlyphLayout();
		// load always returns the same instance for a font
		// and the fontsize only works for the first time
		font = game.fontloader.load(Main.RES_DEFAULT_FONT, fontsize, true);
		layout.setText(font, btn_text, Color.BLACK, width, Align.center, false);
	}
	
	
	//empty for now
	public void update(float delta) {
		
	}
	
	public void render(SpriteBatch spritebatch) {
		spritebatch.draw(this.getTextureRegion(), pos.x, pos.y, width, height);
		//subtraction of 2px in y-direction when pressed to make a 3D-link effect, to make it a more realistic buttonpress
		// TODO Make 3d effect relative?
		font.draw(spritebatch, layout, pos.x + (this.pressed ? 1 : 0), pos.y + (height / 2) + (layout.height / 2) - (this.pressed ? 2 : 0));
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(!(button == Input.Buttons.LEFT))return false;
		screenY = Main.WINDOW_HEIGHT - screenY;
		
		//check if btn was pressed before and then released and therefore activated
		if(checkClick(screenX, screenY) && pressed) {
			clicked = true;
			pressed = false;
			return true;
		}
		pressed = false;
		
		return false;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(!(button == Input.Buttons.LEFT))return false;
		screenY = Main.WINDOW_HEIGHT - screenY;
		
		if(checkClick(screenX, screenY)) {
			this.pressed = true;
			btnClick.play();
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
		game.fontloader.load();
		game.assetmanager.load(RES_BTN_CLICK_SOUND, Sound.class);
	}
	
	// This dispose can't be static
	// TODO This is complete nonsense :D
	public void dispose() {
		/*
		if(!class_assets_disposed && existing_buttons == 0) {
			game.assetmanager.unload(RES_BTN_PRESSED);
			game.assetmanager.unload(RES_BTN_RELEASED);
			game.assetmanager.unload(RES_BTN_CLICK_SOUND);
			class_assets_disposed = true;
		}
		game.fontloader.unload();
		*/
	}
	
	public TextureRegion getTextureRegion() {
		return this.pressed ? this.texReg[1] : this.texReg[0];
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public boolean getClicked() {
		return this.clicked;
	}
	
	public void reset() {
		this.clicked = false;
		
	}
	
	public void setPosition(Vector2 pos) {
		this.pos = pos.cpy();
	}
}
