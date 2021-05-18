package com.zereb.roket.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Explosion {

    private float size = 1;
    private final ParticleEffect particleEffect = new ParticleEffect();

    public Vector2 position = new Vector2();
    public State state = State.START;

    public Explosion(Vector2 position) {
        this.position.set(position);
        particleEffect.load(Gdx.files.internal("explosion"), Gdx.files.internal(""));
        particleEffect.setPosition(position.x, position.y);
        particleEffect.start();
    }


    public enum State {
        START, END
    }

    public void update(float delta){
        size++;
        particleEffect.update(delta);

        if (particleEffect.isComplete())
            state = State.END;

    }

    public void render(SpriteBatch batch){
        particleEffect.draw(batch);
    }

    public void debug(ShapeRenderer sr){
        Gdx.app.log("Explosion", toString());
        sr.setColor(Color.YELLOW);
        sr.circle(position.x, position.y, size);
    }

    public void dispose(){
        particleEffect.dispose();
    }

    @Override
    public String toString() {
        return "Explosion{" +
                "size=" + size +
                ", position=" + position +
                ", state=" + state +
                '}';
    }
}
