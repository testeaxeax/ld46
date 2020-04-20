package com.thetriumvirate.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public final class MenuScreen implements Screen {
	
	private static final int CAM_WIDTH = Main.WINDOW_WIDTH;
	private static final int CAM_HEIGHT = Main.WINDOW_HEIGHT;
	private static final int FONT_SIZE = Main.DEFAULT_FONTSIZE;
	
	// Declare resource paths below
	// For example: private static final String RES_SOMETHING = "somewhere/something";
	private static final String RES_BACKGROUND = "graphics/menu-background.png";
	// This is used in all screens except SplashScreen
	private static final String RES_BACKGROUND_MUSIC = "audio/background-music.wav";
	
	//all textures for the btns
	private static final String RES_BTN_EASY = "graphics/customBtn_easy.png";
	private static final String RES_BTN_MEDIUM = "graphics/customBtn_medium.png";
	private static final String RES_BTN_HARD = "graphics/customBtn_hard.png";
	private static final String RES_BTN_EXIT = "graphics/customBtn_exit.png";
	private static final String RES_BTN_CREDITS = "graphics/customBtn_credits.png";
	//does NOT need scaling for html!
	private static final int BTN_SPRITE_WIDTH = 128;
	private static final int BTN_SPRITE_HEIGHT = 64;
	//---
	
	private final Main game;
	private final OrthographicCamera cam;
	private final CustomButton creditsBtn, easyBtn, moderateBtn, difficultBtn;
	private final Texture creditsBtn_tex, easyBtn_tex, moderateBtn_tex, difficultBtn_tex;
	private TextureRegion[] creditsBtn_texReg, easyBtn_texReg, moderateBtn_texReg, difficultBtn_texReg;
	private final InputMultiplexer multiplexer;
	
	// Declare resource variables below
	// For example: private final Texture testTexture;
	private final Texture background;
	private final Music music;
	
	
	public MenuScreen(Main game) {
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
		music = game.assetmanager.get(RES_BACKGROUND_MUSIC, Music.class);
		music.setLooping(true);
		music.setVolume(0.05f);
		
		//all btn textures
		creditsBtn_tex = game.assetmanager.get(RES_BTN_CREDITS, Texture.class);
		easyBtn_tex = game.assetmanager.get(RES_BTN_EASY, Texture.class);
		moderateBtn_tex = game.assetmanager.get(RES_BTN_MEDIUM, Texture.class);
		difficultBtn_tex = game.assetmanager.get(RES_BTN_HARD, Texture.class);
		
		// Do everything else below
		creditsBtn_texReg = new TextureRegion[2];
		creditsBtn_texReg[0] = new TextureRegion(creditsBtn_tex, 0, 0, BTN_SPRITE_WIDTH, BTN_SPRITE_HEIGHT);
		creditsBtn_texReg[1] = new TextureRegion(creditsBtn_tex, 0, BTN_SPRITE_HEIGHT, BTN_SPRITE_WIDTH, BTN_SPRITE_HEIGHT);
		
		easyBtn_texReg = new TextureRegion[2];
		easyBtn_texReg[0] = new TextureRegion(easyBtn_tex, 0, 0, BTN_SPRITE_WIDTH, BTN_SPRITE_HEIGHT);
		easyBtn_texReg[1] = new TextureRegion(easyBtn_tex, 0, BTN_SPRITE_HEIGHT, BTN_SPRITE_WIDTH, BTN_SPRITE_HEIGHT);
		
		moderateBtn_texReg = new TextureRegion[2];
		moderateBtn_texReg[0] = new TextureRegion(moderateBtn_tex, 0, 0, BTN_SPRITE_WIDTH, BTN_SPRITE_HEIGHT);
		moderateBtn_texReg[1] = new TextureRegion(moderateBtn_tex, 0, BTN_SPRITE_HEIGHT, BTN_SPRITE_WIDTH, BTN_SPRITE_HEIGHT);
		
		difficultBtn_texReg = new TextureRegion[2];
		difficultBtn_texReg[0] = new TextureRegion(difficultBtn_tex, 0, 0, BTN_SPRITE_WIDTH, BTN_SPRITE_HEIGHT);
		difficultBtn_texReg[1] = new TextureRegion(difficultBtn_tex, 0, BTN_SPRITE_HEIGHT, BTN_SPRITE_WIDTH, BTN_SPRITE_HEIGHT);
		
		
		
		creditsBtn = new CustomButton(game, new Vector2(0, 0), creditsBtn_texReg,  "", FONT_SIZE);
		easyBtn = new CustomButton(game, new Vector2(0, 0), easyBtn_texReg, "", FONT_SIZE);
		moderateBtn = new CustomButton(game, new Vector2(0, 0), moderateBtn_texReg, "", FONT_SIZE);
		difficultBtn = new CustomButton(game, new Vector2(0, 0), difficultBtn_texReg, "", FONT_SIZE);
		
		// Buttons use the same Texture
		int btnheight = creditsBtn.getHeight();
		int btnwidth = creditsBtn.getWidth();
		int difficultyxpos = (Main.WINDOW_WIDTH / 2) - (btnwidth / 2);
		int difficultyyposspace = btnheight / 10;
		int difficultyyposstart = (Main.WINDOW_HEIGHT / 2) - (btnheight / 2) + btnheight + difficultyyposspace;
		
		creditsBtn.setPosition(new Vector2(0, 0));
		easyBtn.setPosition(new Vector2(difficultyxpos, difficultyyposstart));
		moderateBtn.setPosition(new Vector2(difficultyxpos, difficultyyposstart - btnheight - difficultyyposspace));
		difficultBtn.setPosition(new Vector2(difficultyxpos, difficultyyposstart - 2 * btnheight - (2 * difficultyyposspace)));
		multiplexer = new InputMultiplexer();
		
		multiplexer.addProcessor(creditsBtn);
		multiplexer.addProcessor(difficultBtn);
		multiplexer.addProcessor(easyBtn);
		multiplexer.addProcessor(moderateBtn);
		
		Gdx.input.setInputProcessor(multiplexer);
	}
	
	// Load all resources for this screen in prefetch !!!
	// For example: game.assetmanager.load(RES_SOMETHING, SomeType.class);
	// or			game.fontloader.load(RES_SOMETHING_FONT);
	// Unload all resources in dispose !!!
	public static void prefetch(Main game) {
		game.assetmanager.load(RES_BACKGROUND, Texture.class);
		game.assetmanager.load(RES_BACKGROUND_MUSIC, Music.class);
		
		game.assetmanager.load(RES_BTN_CREDITS, Texture.class);
		game.assetmanager.load(RES_BTN_EASY, Texture.class);
		game.assetmanager.load(RES_BTN_MEDIUM, Texture.class);
		game.assetmanager.load(RES_BTN_HARD, Texture.class);
		game.assetmanager.load(RES_BTN_EXIT, Texture.class);
		
	}
	
	// Unload all resources for this screen
	// For example: game.assetmanager.unload(RES_SOMETHING);
	// For fonts: game.fontmanager.unload(RES_SOMETHING_FONT);
	@Override
	public void dispose() {
		game.assetmanager.unload(RES_BACKGROUND);
		game.assetmanager.unload(RES_BACKGROUND_MUSIC);
		
		game.assetmanager.unload(RES_BTN_CREDITS);
		game.assetmanager.unload(RES_BTN_EASY);
		game.assetmanager.unload(RES_BTN_MEDIUM);
		game.assetmanager.unload(RES_BTN_HARD);
		game.assetmanager.unload(RES_BTN_EXIT);
		
		creditsBtn.dispose();
		easyBtn.dispose();
		moderateBtn.dispose();
		difficultBtn.dispose();
	}

	@Override
	public void show() {
		creditsBtn.reset();
		easyBtn.reset();
		moderateBtn.reset();
		difficultBtn.reset();
		Gdx.input.setInputProcessor(this.multiplexer);
		if(!music.isPlaying()) {
			music.play();
		}
	}

	@Override
	public void render(float delta) {
		checkButtons();
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		game.spritebatch.begin();
		game.spritebatch.draw(background, 0, 0, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
		easyBtn.render(game.spritebatch);
		creditsBtn.render(game.spritebatch);
		moderateBtn.render(game.spritebatch);
		difficultBtn.render(game.spritebatch);
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
	
	private void checkButtons() {
		if(creditsBtn.getClicked()) {
			// Music never stops
			game.screenmanager.push(new CreditsScreen(game));
		} else if(easyBtn.getClicked()) {
			game.screenmanager.push(new GameScreen(game, 0));
		} else if(moderateBtn.getClicked()) {
			game.screenmanager.push(new GameScreen(game, 1));
		} else if(difficultBtn.getClicked()) {
			game.screenmanager.push(new GameScreen(game, 2));
		}
	}
}
