package com.thetriumvirate.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

public final class CreditsScreen implements Screen {
	
	private static final int CAM_WIDTH = Main.WINDOW_WIDTH;
	private static final int CAM_HEIGHT = Main.WINDOW_HEIGHT;
	private static final String CREDITS = "Game developed for Ludum Dare 42 within 72 hours by:\n" 
			+ "Inzenhofer Tobias\n"
			+ "Poellinger Maximilian\n" 
			+ "Brunner Moritz\n\n\n" 
			+ "This game was made with:\n" 
			+ "libGDX";
	
	// Declare resource paths below
	// For example: private static final String RES_SOMETHING = "somewhere/something";
	private static final String RES_BACKGROUND = "graphics/credits-background.png";
	// TODO Change me
	private static final String RES_CREDITS_FONT = "fonts/credits-font";
	
	private final Main game;
	private final OrthographicCamera cam;
	private final GlyphLayout layout;
	private final CustomButton returnBtn;
	private final Vector2 position;
	
	// Declare resource variables below
	// For example: private final Texture testTexture;
	private final Texture background;
	private final BitmapFont font;
	
	
	public CreditsScreen(Main game) {
		// Initialize essentials
		this.game = game;
		cam = new OrthographicCamera();
		cam.setToOrtho(false, CAM_WIDTH, CAM_HEIGHT);
		cam.update();
		game.spritebatch.setProjectionMatrix(cam.combined);
		Gdx.gl.glClearColor(1, 0, 0, 1);
		
		// Initialize resource variables below
		// For example: testTexture = game.assetmanager.get(RES_SOMETEXTURE, Texture.class);
		background = game.assetmanager.get(RES_BACKGROUND, Texture.class);
		font = game.fontloader.get(RES_CREDITS_FONT);
		
		// Do everything else below
		layout = new GlyphLayout();
		layout.setText(font, CREDITS, Color.BLACK, Main.WINDOW_WIDTH, Align.center, true);
		position = new Vector2(0, (Main.WINDOW_HEIGHT / 2) + (layout.height / 2));
		Vector2 positionReturnBtn = new Vector2(Main.WINDOW_WIDTH / 10, Main.WINDOW_HEIGHT / 10);
		returnBtn = new CustomButton(game, positionReturnBtn, "Return", Main.DEFAULT_FONTSIZE);
	}
	
	// Load all resources for this screen in prefetch !!!
	// For example: game.assetmanager.load(RES_SOMETHING, SomeType.class);
	// or			game.fontloader.load(RES_SOMETHING_FONT);
	// Unload all resources in dispose !!!
	public static void prefetch(Main game) {
		game.assetmanager.load(RES_BACKGROUND, Texture.class);
		game.fontloader.load(RES_CREDITS_FONT);
	}
	
	// Unload all resources for this screen
	// For example: game.assetmanager.unload(RES_SOMETHING);
	// For fonts: game.fontmanager.unload(RES_SOMETHING_FONT);
	@Override
	public void dispose() {
		game.assetmanager.unload(RES_BACKGROUND);
		game.fontloader.unload(RES_CREDITS_FONT);
		returnBtn.dispose();
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(returnBtn);
		// music is already being played by MenuScreen
	}

	@Override
	public void render(float delta) {
		checkReturnButton();
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		game.spritebatch.begin();
		game.spritebatch.draw(background, 0, 0, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
		font.draw(game.spritebatch, layout, position.x, position.y);
		returnBtn.render(game.spritebatch);
		game.spritebatch.end();
	}
	
	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void hide() {
		
	}
	
	private void checkReturnButton() {
		if(returnBtn.getClicked() == true) {
			game.screenmanager.pop();
		}
	}
}
