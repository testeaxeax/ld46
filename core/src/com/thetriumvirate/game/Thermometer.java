package com.thetriumvirate.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Thermometer {

	
	private static final String RES_THERMOMETER = "graphics/thermometer.png";
	private static final String RES_MERCURY = "graphics/mercury.png";
	
	private static final float WIDTH = (float)Main.WINDOW_WIDTH/(1024f/96f);
	private static final float HEIGHT = (float)Main.WINDOW_HEIGHT/(800f/256f);
	private static final int MERCURY_WIDTH = (int) (8f * (float)Main.WINDOW_WIDTH / 1024f);//width of the mercurytube in px
	private static final int MAX_MERCURY_HEIGHT = (int) (160f * (float)Main.WINDOW_HEIGHT / 800f);//max height of mercury in the tube in px/4
	
	private final Texture thermometer_texture, mercury_texture;
	private float currentTemp = 0;

	private final Vector2 POS;
	private final Vector2 MERCURY_OFFSET = new Vector2((float)(15 *4) * (float)Main.WINDOW_WIDTH / 1024f, (float)(17 *4) * (float)Main.WINDOW_HEIGHT / 800f);
	
	public Thermometer(Main game, Vector2 pos) {
		this.POS = pos;
		
		thermometer_texture = game.assetmanager.get(RES_THERMOMETER, Texture.class);
		mercury_texture = game.assetmanager.get(RES_MERCURY, Texture.class);
	}
	
	public void update(float delta, float currentTemp) {
		this.currentTemp = currentTemp;
	}
	
	public void render(SpriteBatch sb) {
		//draw thermometer
		sb.draw(thermometer_texture, this.POS.x, this.POS.y, WIDTH, HEIGHT);
		
		//display temperature by drawing the mercury with fixed width of 8 and a hight corresponding to the current temperature
		sb.draw(this.mercury_texture, this.POS.x + this.MERCURY_OFFSET.x, this.POS.y + this.MERCURY_OFFSET.y, MERCURY_WIDTH, (currentTemp * ((float)MAX_MERCURY_HEIGHT/(float)TemperatureController.MAX_TEMP)));
	}
	
	public static void prefetch(Main game) {
		game.assetmanager.load(RES_THERMOMETER, Texture.class);
		game.assetmanager.load(RES_MERCURY, Texture.class);
	}
	
	public static void dispose(Main game) {
		game.assetmanager.unload(RES_THERMOMETER);
		game.assetmanager.unload(RES_MERCURY);
	}
}
