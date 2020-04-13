package com.thetriumvirate.game.desktop;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;
import com.thetriumvirate.game.FontLoader;
import com.thetriumvirate.game.Main;

public final class DesktopFontLoader implements FontLoader {
	
	// DesktopFontLoader instantiates before Main
	private Main game = null;

	// If sync is set to true, load will wait for the font to load and return a valid BitmapFont
	// If sync is set to false or not set at all, load will not wait for the font to load and return null
	@Override
	public BitmapFont load(String assetpath, int font_size, Color color, boolean sync) {
		FreeTypeFontLoaderParameter loaderparam = new FreeTypeFontLoaderParameter();
		FreeTypeFontParameter param = new FreeTypeFontParameter();
		
		FileHandleResolver resolver = new InternalFileHandleResolver();
		game.assetmanager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
		game.assetmanager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
		param.size = font_size;
		param.color = color;
		loaderparam.fontFileName = "desktop/" + assetpath + "-" + font_size + "-" + color + ".ttf";
		loaderparam.fontParameters = param;
		
		if(sync) {
			return game.assetmanager.syncGet(assetpath + "-" + font_size + "-" + color, BitmapFont.class, loaderparam);
		}
		
		game.assetmanager.load(assetpath + "-" + font_size + "-" + color, BitmapFont.class, loaderparam);
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
		game.assetmanager.unload(assetpath + "-" + font_size + "-" + color);
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
	public BitmapFont get(String assetpath, int font_size, Color color) {
		return game.assetmanager.get(assetpath + "-" + font_size + "-" + color);
	}
	
	@Override
	public BitmapFont get(String assetpath, int font_size) {
		return get(assetpath, font_size, Main.DEFAULT_FONT_COLOR);
	}
	
	@Override
	public BitmapFont get(String assetpath) {
		return get(assetpath, Main.DEFAULT_FONTSIZE);
	}
	
	@Override
	public BitmapFont get() {
		return get(Main.RES_DEFAULT_FONT);
	}
	
	@Override
	public void setGame(Main p) {
		game = p;
	}
}
