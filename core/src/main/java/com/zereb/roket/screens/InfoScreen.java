package com.zereb.roket.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zereb.roket.Main;
import com.zereb.roket.ResourseManager;



public class InfoScreen implements Screen {
    private final Viewport viewport;
    private final Table table;
    private final Stage stage;
    private final TextButton btnBack;
    private final Main game;
    private final Texture bg;

    private Label info;

    public InfoScreen(Main game){
        this.game = game;
        viewport = new FitViewport(Main.WIDTH, Main.HEIGHT);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
        info = new Label("", new Label.LabelStyle(ResourseManager.INSTANCE.loadFont("info.fnt"), Color.ORANGE));

        bg = ResourseManager.INSTANCE.loadTexture("bg_menu.png");

        TextButton.TextButtonStyle tbs = new TextButton.TextButtonStyle();
        tbs.font = game.font;

        btnBack = new TextButton("BACK", tbs);
        info.setText("-= Land the rocket =-  \n" +
                " \n Find the exit and land or hover rocket there." +
                " \n Developed by Plett Oleg." +
                " \n Find game source code on http://github.com/zereb/rocket " +
                "\n Contact: zizereb@gmail.com " +
                "\n Music 'Air' by Alexandr Zhelanov (c)" +
                "\n Licensed under a Creative Commons Attribution (3.0) license. " +
                "\n https://opengameart.org/content/music-mix-more-music-inside" );
        info.setAlignment(Align.center);
        info.setWrap(true);

        table = new Table();
        table.setFillParent(true);


        table.row();
        table.add(info).size(Main.WIDTH - 10f);
        table.row();
        table.add(btnBack).padTop(30f).height(40);
        btnBack.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("InfoScreen", "back");
                dispose();
                game.setScreen(new MainMenu(game));
            }
        });

        table.pack();

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
        viewport.update(width, height, true);
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

    @Override
    public void dispose() {
        stage.dispose();
    }
}
