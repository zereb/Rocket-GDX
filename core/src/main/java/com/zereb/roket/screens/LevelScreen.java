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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zereb.roket.Main;
import com.zereb.roket.ResourseManager;
import com.zereb.roket.Save;

public class LevelScreen implements Screen {
    private final Viewport viewport;
    private final Table table;
    private final Stage stage;
    private final Array<TextButton> buttons;
    private final TextButton btnBack;
    private final Main game;
    private final Texture bg;


    public LevelScreen(Main game){
        this.game = game;
        viewport = new FitViewport(Main.WIDTH, Main.HEIGHT);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
        bg = ResourseManager.INSTANCE.loadTexture("bg_menu.png");
        buttons = new Array<>();

        TextButton.TextButtonStyle tbs = new TextButton.TextButtonStyle();
        tbs.font = game.font;

        btnBack = new TextButton("BACK", tbs);

        table = new Table();
        table.setFillParent(true);
//        table.debug(); //Enables debug

        // Set table structure
        for (int i = Main.FIRST_LVL; i <= Main.LAST_LVL; i++) {
            //cant choose levels if not finished last one
            if (i > Main.FIRST_LVL && Save.INSTANCE.getHighScore("l" + (i - 1)) == 0)
                continue;

            TextButton btn = new TextButton("lvl " + i, tbs);
            int finalI = i;
            btn.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.app.log("LevelScreen", "button " + finalI);
                    Save.INSTANCE.setCurrent(finalI);
                    dispose();
                    game.setScreen(new GameScreen(game));
                }
            });

            buttons.add(btn);
            table.add(btn).width(110).height(50).pad(8f);
            if (i % 4 == 0) table.row();
        }



        table.row();
        table.add(btnBack).padTop(70f).height(40).colspan(5);
        btnBack.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("LevelScreen", "back");
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
