package com.zereb.roket.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.zereb.roket.*;


public class Rocket {
    public static final float MIN_VEL_TO_LAND = 5f;
    public static final float MIN_VEL_TO_CRASH = 300f;

    public State state;

    public final Map map;
    private final int texWidth, texHeight;
    private final float G = 9.807f;
    private final float DRAG = 0.99f;
    private final TextureRegion textureRegion;
    private final Rectangle rectangle;
    public long timer = 0;
    public long lastFrame = 0;

    public Vector2 velocity = new Vector2();
    private float rotation = 0f;
    private float rotationDir = 0f;
    public com.zereb.roket.entity.Explosion explosion;

    private final ParticleEffect particleEffect = new ParticleEffect();

    private Vector2 centerCol = new Vector2();
    public Vector2 position = new Vector2();


//    public Sound hit;
//    public Sound wing;
//    public Sound point;

    public enum State {
        DEAD, GRACE, PLAYING, WON, STOP
    }

    public Rocket(Map map) {
        textureRegion = new TextureRegion(ResourseManager.INSTANCE.loadTexture("spaceRockets_004.png"));
        particleEffect.load(Gdx.files.internal("thrust"), Gdx.files.internal(""));
        particleEffect.setDuration(300);
        particleEffect.getEmitters().first().setContinuous(false);

        this.map = map;

//        hit = Flappy.resourseManager.loadSound("hit.ogg");
//        wing = Flappy.resourseManager.loadSound("wing.ogg");
//        point = Flappy.resourseManager.loadSound("point.ogg");

        texWidth = textureRegion.getRegionWidth() / 3;
        texHeight = textureRegion.getRegionHeight() / 4;
        rectangle = new Rectangle(0, 0, texWidth / 2f, texHeight - 5);
        reset();
    }

    @Override
    public String toString() {
        return "Rocket{" +
                " position=" + position.toString() +
                ", velocity=" + velocity.toString() +
                ", velocity.len=" + velocity.len() +
                ", state=" + state +
                ", timer=" + timer +
                '}';
    }

    public void update(float delta) {
        if (state == State.PLAYING){
            timer = timer + (TimeUtils.millis() - lastFrame);
            lastFrame = TimeUtils.millis();

            if (position.x < 0) position.x = map.width();
            if (position.x > map.width()) position.x = 0;


                position.y += velocity.y * delta;
            position.x += velocity.x * delta;

            for (Field field : map.fields()){
                if (!rectangle.overlaps(field.rectangle))
                    continue;
                if (field.type == Field.Type.ARROW_LEFT)
                    velocity.add(-10f, 0);
                if (field.type == Field.Type.ARROW_RIGHT)
                    velocity.add(10f, 0);
                if (field.type == Field.Type.ARROW_UP)
                    velocity.add(0, 10f);
                if (field.type == Field.Type.ARROW_DOWN)
                    velocity.add(0, -10f);
            }

            for (Rectangle danger : map.danger()){
                if (rectangle.overlaps(danger))
                    state = State.DEAD;
            }

            //collisions
            for (Rectangle colision : map.colisions()) {

                //to avoid nested if's skip current iteration if there is no collision
                if (!rectangle.overlaps(colision))
                    continue;
                //instant death if rocket fully inside collision block
                if (colision.contains(rectangle))
                    state = State.DEAD;

                if (velocity.len() > MIN_VEL_TO_CRASH)
                        state = State.DEAD;

               centerCol = colision.getCenter(centerCol);

               //janky collision filtering
                if (position.x + rectangle.width / 2 > colision.x + 5  && rectangle.x < colision.x + colision.width - 5){
                    if (position.y < centerCol.y)
                        position.y = colision.y - rectangle.height / 2f; //rocket is under
                    else if (position.y > centerCol.y)
                        position.y = colision.y + colision.height + rectangle.height / 2f; //rocket is above
                    velocity.y = -velocity.y / 2;
                }else {
                    if (position.x < centerCol.x)
                        position.x = colision.x - rectangle.width / 2f; //rocket is on the left
                    else if (position.x > centerCol.x)
                        position.x = colision.x + colision.width + rectangle.width / 2f ; //rocket is on the right
                    velocity.x = -velocity.x / 2;
                }
            }


            // gravity and drag force
            velocity.y -= G;
            velocity.scl(DRAG);

            rotationDir = (velocity.x < 0) ? 1f : -1f;
            rotationDir = Math.abs(velocity.x) * rotationDir * 8 * delta;
            rotation = rotationDir;

            rectangle.setCenter(position);

            //level exit
            if (rectangle.overlaps(map.exit()) && velocity.len() < MIN_VEL_TO_LAND){
                state = State.WON;
            }

        }// state == PLAYING

        if (state == State.WON){
            stopParticle();
            return;
        }

        if (state == State.DEAD && explosion == null){
            stopParticle();
            explosion = new Explosion(position);
        }


        if (explosion != null){
            explosion.update(delta);
            if (explosion.state == Explosion.State.END){
                state = State.STOP;
                explosion.dispose();
                explosion = null;
            }
        }
    }



    public void debug(ShapeRenderer sr){
//        Gdx.app.log("Rocket", toString());
        sr.setColor(Color.RED);
        sr.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        sr.circle(position.x, position.y, 1f);

        if (explosion != null){
            explosion.debug(sr);
        }
    }

    public void draw(SpriteBatch batch) {
        if (explosion != null)
            explosion.render(batch);

        // this is to calc x position of emmitor considering rocket rotation
        // x = tan(angle) * y
        float x = position.x + (MathUtils.sinDeg(rotation) / MathUtils.cosDeg(rotation)) * (position.y - (position.y - rectangle.height / 2f));
        particleEffect.setPosition(x, position.y - rectangle.height / 2f);
        particleEffect.draw(batch, Gdx.graphics.getDeltaTime());

        //skip drawing rocket if dead
        if (state == State.STOP || state == State.DEAD) return;
        batch.draw(textureRegion, position.x - texWidth / 2f , position.y - texHeight / 2f, texWidth / 2f, texHeight / 2f, texWidth, texHeight, 1f, 1f, rotation);
    }

    public void thrust(){
        particleEffect.getEmitters().first().setContinuous(true);
        velocity.y += 1.7 * G;
    }

    public void left(){
        if(Math.abs(velocity.y) > 11){
            particleEffect.getEmitters().first().setContinuous(true);
            velocity.x += -4f;
        }
    }

    public void right(){
        if (Math.abs(velocity.y) > 11){
            particleEffect.getEmitters().first().setContinuous(true);
            velocity.x += 4f;
        }
    }

    public void stopParticle(){
        particleEffect.getEmitters().first().setContinuous(false);
    }


    public void reset(){
        position.set(map.spawn().x, map.spawn().y);
        rectangle.setCenter(position);
        velocity.setZero();
        rotation = 0;
        timer = 0;
        state = State.GRACE;
        lastFrame = TimeUtils.millis();
    }


    public void dispose() {
        particleEffect.dispose();
    }


}
