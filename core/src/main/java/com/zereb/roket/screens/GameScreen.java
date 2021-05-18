package com.zereb.roket.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zereb.roket.*;
import com.zereb.roket.entity.Rocket;

/** First screen of the application. Displayed after the application is created. */
public class GameScreen implements Screen {

	private final Main game;
	private final OrthographicCamera camera;
	private final Viewport viewport;
	private Map map;
	private final PlayerHUD hud;
	private Rocket rocket;
	private Music music;

	public GameScreen(Main game) {
		this.game = game;
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Main.WIDTH, Main.HEIGHT);
		viewport = new FitViewport(Main.WIDTH, Main.HEIGHT, camera);
		hud = new PlayerHUD(game);
		map = new Map(Save.INSTANCE.current());
		rocket = new Rocket(map);

		if (Main.musicOn) {
			music = Gdx.audio.newMusic(Gdx.files.internal("air.mp3"));
			music.setLooping(true);
			music.play();
		}
	}


	@Override
	public void show() {
		// Prepare your screen here.
	}

	@Override
	public void render(float delta) {

		//events
		while (Main.events.notEmpty()){
			if(Main.events.removeFirst() == Main.Event.NEXT_LVL){
				map.dispose();
				map = new Map(Save.INSTANCE.setCurrent(Save.INSTANCE.current() + 1));
				rocket = new Rocket(map);
			}
		}

		hud.input(viewport, rocket);
		rocket.update(delta);

		camera.position.set(rocket.position, 0);
		//camera zoom depends on speed of the rocket
        camera.zoom = 0.9f + MathUtils.clamp(rocket.velocity.len2() * 0.0000012f, 0.1f, 1);
		camera.update();

		Gdx.gl.glClearColor(0.0f, 0, 0.0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


		game.batch.setProjectionMatrix(camera.combined);
		map.renderBg(game.batch);

		map.render(camera);

		game.batch.begin();
//		game.batch.draw(texture, 0, 0);
		rocket.draw(game.batch);
		game.batch.end();


//		game.debug.setProjectionMatrix(camera.combined);
//		game.debug.begin();
//		game.debug.set(ShapeRenderer.ShapeType.Line);
//		rocket.debug(game.debug);
//		map.debug(game.debug);
//		game.debug.end();

		game.batch.setProjectionMatrix(hud.camera.combined);
		game.batch.begin();
		hud.render(rocket, Save.INSTANCE.getHighScore(map.name()), game.batch);
		game.batch.end();

//		game.debug.setProjectionMatrix(hud.camera.combined);
//		game.debug.begin();
//		game.debug.set(ShapeRenderer.ShapeType.Line);
//		hud.debug(game.debug);
//		game.debug.end();



	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
		// Resize your screen here. The parameters represent the new window size.
	}

	@Override
	public void pause() {
		// Invoked when your application is paused.
	}

	@Override
	public void resume() {
		// Invoked when your application is resumed after pause.
	}

	@Override
	public void hide() {
		dispose();
		// This method is called when another screen replaces this one.
	}

	@Override
	public void dispose() {
		rocket.dispose();
		if (music != null)
			music.dispose();
		// Destroy screen's assets here.
	}
}