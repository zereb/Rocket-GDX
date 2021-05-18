package com.zereb.roket.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zereb.roket.Main;
import com.zereb.roket.ResourseManager;

public class MainMenu implements Screen {

    private final Viewport viewport;
    private final Table table;
    private final Stage stage;
    private final TextButton btnPlay, btnInfo, btnExit, btnMusic;
    private final Image gameTitle;
    private final Texture bg;
    private final Main game;


    public MainMenu(Main game){
        this.game = game;
        viewport = new FitViewport(Main.WIDTH, Main.HEIGHT);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        gameTitle = new Image(ResourseManager.INSTANCE.loadTexture("spaceRockets_001.png"));
        bg = ResourseManager.INSTANCE.loadTexture("bg_menu.png");
        gameTitle.setOrigin(Align.center);

        TextButton.TextButtonStyle tbs = new TextButton.TextButtonStyle();
        tbs.font = game.font;

        btnPlay = new TextButton("PLAY", tbs);
        btnInfo = new TextButton("INFO", tbs);
        btnExit = new TextButton("EXIT", tbs);
        btnMusic = new TextButton("", tbs);

        if (Main.musicOn) btnMusic.setText("MUSIC ON");
        else btnMusic.setText("MUSIC OFF");


        table = new Table();
        table.setFillParent(true);
//        table.debug(); //Enables debug

        // Set table structure
        table.row();
        table.add(gameTitle).padTop(30f).expand();
        table.row();
        table.add(btnPlay).padTop(100f).expand();
        table.row();
        table.add(btnMusic).padTop(10f).expand();
        table.row();
        table.add(btnInfo).padTop(10f).expand();
        table.row();
        table.add(btnExit).padTop(10f).expand();
        table.row();
        table.padBottom(50f);

        table.pack();

        btnExit.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("MainMenu", "exit");
                dispose();
                Gdx.app.exit();
            }
        });

        btnInfo.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("MainMenu", "info");
                game.setScreen(new InfoScreen(game));
            }
        });

        btnPlay.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("MainMenu", "play");
                game.setScreen(new LevelScreen(game));
            }
        });

        btnMusic.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("MainMenu", "music");
                Main.musicOn = !Main.musicOn;

                if (Main.musicOn) btnMusic.setText("MUSIC ON");
                else btnMusic.setText("MUSIC OFF");
            }
        });


        gameTitle.addAction(new Action() {
            float rotation = 1;
            @Override
            public boolean act(float delta) {
                rotation += delta;
                gameTitle.setRotation(MathUtils.sin(rotation) * 8);
                return false;
            }
        });

        stage.addActor(table);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(stage.getCamera().combined);
        game.batch.begin();
        game.batch.draw(bg, 0f, 0f);
        game.batch.end();

        stage.act(delta);
        stage.draw();

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
