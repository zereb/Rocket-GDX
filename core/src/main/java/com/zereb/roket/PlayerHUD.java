package com.zereb.roket;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zereb.roket.entity.Rocket;
import com.zereb.roket.screens.MainMenu;

import java.util.concurrent.TimeUnit;

public class PlayerHUD {

    public OrthographicCamera camera;
    private final GlyphLayout timer;
    private final GlyphLayout record;
    private final GlyphLayout speed, finish, newRecord;
    private final Main game;
    private final Sprite back, thrust, left, right, replay, next;
    private final Vector3 touchPoint;


    public PlayerHUD(Main game){
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false,Main.WIDTH, Main.HEIGHT);
        timer = new GlyphLayout();
        record = new GlyphLayout();
        speed = new GlyphLayout();
        finish = new GlyphLayout();
        newRecord = new GlyphLayout();


        back = new Sprite(ResourseManager.INSTANCE.loadTexture("back.png"));
        left = new Sprite(ResourseManager.INSTANCE.loadTexture("left.png"));
        right = new Sprite(ResourseManager.INSTANCE.loadTexture("right.png"));
        thrust = new Sprite(ResourseManager.INSTANCE.loadTexture("up.png"));
        replay = new Sprite(ResourseManager.INSTANCE.loadTexture("replay.png"));
        next = new Sprite(ResourseManager.INSTANCE.loadTexture("next.png"));

        right.setSize(right.getWidth() * 1.5f, right.getHeight() * 1.5f);
        left.setSize(left.getWidth() * 1.5f, left.getHeight() * 1.5f);
        thrust.setSize(thrust.getWidth() * 1.5f, thrust.getHeight() * 1.5f);

        back.setPosition(20, Main.HEIGHT - 30 - back.getHeight());
        replay.setPosition(Main.WIDTH / 2f - replay.getWidth() - 5, Main.HEIGHT / 2f - replay.getHeight() / 2f);
        next.setPosition(Main.WIDTH / 2f + 5, Main.HEIGHT / 2f - replay.getHeight() / 2f);
        thrust.setPosition(20, 30);
        right.setPosition(Main.WIDTH - right.getWidth() - 20, 30);
        left.setPosition(Main.WIDTH - right.getWidth() * 2 - 25, 30);

        touchPoint = new Vector3();
    }

    public void render(Rocket rocket, long record, SpriteBatch batch){
        back.draw(batch);
        left.draw(batch);
        right.draw(batch);
        thrust.draw(batch);

        this.timer.setText(game.font, String.format("%s : %s",
                TimeUnit.MILLISECONDS.toSeconds(rocket.timer),
                (TimeUnit.MILLISECONDS.toMillis(rocket.timer) / 100) % 10
        ));

        this.record.setText(game.font, String.format("current record  %s : %s",
                TimeUnit.MILLISECONDS.toSeconds(record),
                (TimeUnit.MILLISECONDS.toMillis(record) / 100) % 10
        ));
        this.speed.setText(game.font, "Speed: " + (int) rocket.velocity.len() / 10);
        finish.setText(game.font, "FINISH!");
        newRecord.setText(game.font, "NEW RECORD!");

        game.font.setColor(Color.WHITE);

        game.font.draw(batch, this.record, Main.WIDTH / 2f - this.record.width / 2, Main.HEIGHT - 110);
        game.font.draw(batch, this.timer, Main.WIDTH / 2f - this.timer.width / 2, Main.HEIGHT - 150);
        game.font.draw(batch, this.speed, 20, thrust.getY() + thrust.getHeight() + 50);

        if (rocket.state == Rocket.State.WON){
            game.font.draw(batch, finish, Main.WIDTH / 2f - this.finish.width / 2, Main.HEIGHT - 180);
            replay.draw(batch);
            next.draw(batch);
            if (Save.INSTANCE.setHighScore(rocket.map.name(), rocket.timer)){
                game.font.setColor(Color.RED);
                game.font.draw(batch, newRecord, Main.WIDTH / 2f - this.newRecord.width / 2, Main.HEIGHT - 200);
            }

        }
    }

    public void debug(ShapeRenderer sr){
    }

    public void input(Viewport viewport, Rocket rocket){

        if (rocket.state == Rocket.State.WON && Gdx.input.justTouched()){
            touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPoint,
                    viewport.getScreenX(), viewport.getScreenY(),
                    viewport.getScreenWidth(), viewport.getScreenHeight()
            );
            if (replay.getBoundingRectangle().contains(touchPoint.x, touchPoint.y)){
                rocket.reset();
                return;
            }
            else
                Main.events.addFirst(Main.Event.NEXT_LVL);
        }

        if (rocket.state == Rocket.State.GRACE && Gdx.input.justTouched()){
            rocket.lastFrame = TimeUtils.millis();
            rocket.state = Rocket.State.PLAYING;
            return;
        }

        if (rocket.state == Rocket.State.STOP && Gdx.input.justTouched()){
               rocket.reset();
        }



        if (rocket.state != Rocket.State.PLAYING) return;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            rocket.left();
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            rocket.right();
        if (Gdx.input.isKeyPressed(Input.Keys.UP))
            rocket.thrust();
        if (!Gdx.input.isKeyPressed(Input.Keys.UP)
                && !Gdx.input.isKeyPressed(Input.Keys.RIGHT)
                && !Gdx.input.isKeyPressed(Input.Keys.LEFT)
        )
            rocket.stopParticle();


        for (int i = 0; i < 2; i++) {
            if (Gdx.input.isTouched(i)) {
                touchPoint.set(Gdx.input.getX(i), Gdx.input.getY(i), 0);
                camera.unproject(touchPoint,
                        viewport.getScreenX(), viewport.getScreenY(),
                        viewport.getScreenWidth(), viewport.getScreenHeight()
                );
                if (thrust.getBoundingRectangle().contains(touchPoint.x, touchPoint.y))
                    rocket.thrust();
                if (right.getBoundingRectangle().contains(touchPoint.x, touchPoint.y))
                    rocket.right();
                if (left.getBoundingRectangle().contains(touchPoint.x, touchPoint.y))
                    rocket.left();

//                if (!thrust.getBoundingRectangle().contains(touchPoint.x, touchPoint.y)
//                        && !thrust.getBoundingRectangle().contains(touchPoint.x, touchPoint.y)
//                        && !thrust.getBoundingRectangle().contains(touchPoint.x, touchPoint.y)
//                )
//                    rocket.stopParticle();

                if (back.getBoundingRectangle().contains(touchPoint.x, touchPoint.y)){
                    game.setScreen(new MainMenu(game));
                }

            }
        }
    }
}
