package com.thetriumvirate.game.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.thetriumvirate.game.FontLoader;
import com.thetriumvirate.game.Main;

public class HtmlFontLoader implements FontLoader {
	
	// This entire class does not work as intended and should not be used for future LDs
	
	private Main game;
	
	public HtmlFontLoader() {
		game = null;
	}

	@Override
	public BitmapFont load(String assetpath, int font_size, Color color, boolean sync) {
		if(sync) {
			float scaleXY;
			BitmapFont font;
			font = game.assetmanager.syncGet("html/" + assetpath + "-" + color + ".fnt", BitmapFont.class);
			// Trying to scale Bitmap to correct fontsize
			scaleXY = (font_size / font.getAscent()) * 0.4f;
			// This only scales the font one time as a quick workaround
			if(font.getScaleX() == 1 && font.getScaleY() == 1) {
				font.getData().setScale(scaleXY);
			}
			return font;
		}
		game.assetmanager.load("html/" + assetpath + "-" + color + ".fnt", BitmapFont.class);
		return null;
	}
	
	@Override
	public BitmapFont load(String assetpath, int font_size, Color color) {
		return load(assetpath, font_size, color, false);
	}

	@Override
	public BitmapFont load(String assetpath, int font_size, boolean sync) {
		return load(assetpath, font_size, Main.DEFAULT_FONT_COLOR, sync);
	}
	
	@Override
	public BitmapFont load(String assetpath, int font_size) {
		return load(assetpath, font_size, Main.DEFAULT_FONT_COLOR, false);
	}

	@Override
	public BitmapFont load(String assetpath, boolean sync) {
		return load(assetpath, Main.DEFAULT_FONTSIZE, sync);
	}
	
	@Override
	public BitmapFont load(String assetpath) {
		return load(assetpath, Main.DEFAULT_FONTSIZE, false);
	}
	
	@Override
	public BitmapFont load(boolean sync) {
		return load(Main.RES_DEFAULT_FONT, sync);
	}
	
	@Override
	public BitmapFont load() {
		return load(Main.RES_DEFAULT_FONT, false);
	}
	
	@Override
	public void unload(String assetpath, int font_size, Color color) {
		game.assetmanager.unload("html/" + assetpath + "-" + color + ".fnt");
	}
	
	@Override
	public void unload(String assetpath, int font_size) {
		unload(assetpath, font_size, Main.DEFAULT_FONT_COLOR);
	}
	
	@Override
	public void unload(String assetpath) {
		unload(assetpath, Main.DEFAULT_FONTSIZE);
	}
	
	@Override
	public void unload() {
		unload(Main.RES_DEFAULT_FONT);
	}

	@Override
	public void setGame(Main p) {
		game = p;
	}
	
	public BitmapFont get(String assetpath, int font_size, Color color) {
		float scaleXY;
		BitmapFont font;
		
		font = game.assetmanager.get("html/" + assetpath + "-" + color + ".fnt", BitmapFont.class);
		scaleXY = (font_size / font.getAscent()) * 0.4f;
		// This only scales the font one time as a quick workaround
		if(font.getScaleX() == 1 && font.getScaleY() == 1) {
			font.getData().setScale(scaleXY);
		}
		return font;
	}
	
	public BitmapFont get(String assetpath, int font_size) {
		return get(assetpath, font_size, Main.DEFAULT_FONT_COLOR);
	}
	
	public BitmapFont get(String assetpath) {
		return get(assetpath, Main.DEFAULT_FONTSIZE);
	}
	
	public BitmapFont get() {
		return get(Main.RES_DEFAULT_FONT);
	}
}
