package com.zereb.roket.entity;

import com.badlogic.gdx.math.Rectangle;

public class Field {
    public Field(Rectangle rectangle, Type type) {
        this.rectangle = rectangle;
        this.type = type;
    }

    public enum Type{
        ARROW_LEFT, ARROW_RIGHT, ARROW_DOWN, ARROW_UP
    }
    public final Rectangle rectangle;
    public final Type type;
}
