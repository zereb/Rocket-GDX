package com.zereb.roket;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Queue;
import com.zereb.roket.screens.MainMenu;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    public SpriteBatch batch;
//    public ShapeRenderer debug;
    public BitmapFont font;
    public static int WIDTH = 480, HEIGHT = 800;
    public static int FIRST_LVL = 1, LAST_LVL = 20;
    public static boolean musicOn = true;
    public static Queue<Event> events = new Queue<>();
    public enum Event {
        NEXT_LVL
    }

    @Override
    public void create () {
        batch = new SpriteBatch();
//        debug = new ShapeRenderer();
        font = ResourseManager.INSTANCE.loadFont("16.fnt");
//        debug.setAutoShapeType(true);
        this.setScreen(new MainMenu(this));
    }

    @Override
    public void render () {
        super.render();
    }

    @Override
    public void dispose () {
        Gdx.app.log("Flappy: ", "Disposing");
        font.dispose();
        batch.dispose();
//        debug.dispose();
        ResourseManager.INSTANCE.dispose();
    }

    //TODO: UI - better font wigets in hud, maybe stage
    //TODO: MAIN menu - bg image
    //TODO: particles

}