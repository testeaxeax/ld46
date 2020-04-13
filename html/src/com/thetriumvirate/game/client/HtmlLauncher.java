package com.thetriumvirate.game.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.thetriumvirate.game.Main;
import com.google.gwt.user.client.Window;

public class HtmlLauncher extends com.badlogic.gdx.backends.gwt.GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
        }

        @Override
        public ApplicationListener createApplicationListener () {
        	int height, width;
        	if(Window.getClientHeight() < Window.getClientWidth()) {
        		height = (int) (Window.getClientHeight() * 0.95f);
        		width = (int) (Main.WINDOW_RATIO * height);
        	}else {
        		width = Window.getClientWidth();
        		height = (int) (width / Main.WINDOW_RATIO);
        	}
        	Main.WINDOW_WIDTH = width;
        	Main.WINDOW_HEIGHT = height;
                return new Main(new HtmlFontLoader());
        }
}
