package com.thetriumvirate.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public interface FontLoader {

	public BitmapFont load(String assetpath, int font_size, Color color, boolean sync);
	public BitmapFont load(String assetpath, int font_size, boolean sync);
	public BitmapFont load(String assetpath, boolean sync);
	public BitmapFont load(boolean sync);
	public BitmapFont load(String assetpath, int font_size, Color color);
	public BitmapFont load(String assetpath, int font_size);
	public BitmapFont load(String assetpath);
	public BitmapFont load();
	public void unload(String assetpath, int font_size, Color color);
	public void unload(String assetpath, int font_size);
	public void unload(String assetpath);
	public void unload();
	public BitmapFont get(String assetpath, int font_size, Color color);
	public BitmapFont get(String assetpath, int font_size);
	public BitmapFont get(String assetpath);
	public BitmapFont get();
	public void setGame(Main p);
}
