package com.zereb.roket;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.zereb.roket.entity.Field;


public class Map {

    private final String P_HEIGHT = "height";
    private final String P_WIDTH = "width";
    private final String P_THEIGHT = "tileheight";
    private final String P_TWIDTH = "tilewidth";
    private int width, height;
    private int tileWidth, tiledHeight;
    private String name;
    private Texture bg;

    private Vector2 spawnPosition;
    private Rectangle exit;
    private final Array<Rectangle> collisions = new Array<>();
    private final Array<Rectangle> danger = new Array<>();
    private final Array<Field> fields = new Array<>();

    //map related
    private TiledMap map;
    private final TmxMapLoader loader;
    private final OrthogonalTiledMapRenderer renderer;

    public Map(int current) {
        loader = new TmxMapLoader();
        renderer = new OrthogonalTiledMapRenderer(map);
        bg = ResourseManager.INSTANCE.loadTexture("bg.jpg");
        loadMap("l" + current);
    }


    public void loadMap(String name){
        this.name = name;
        map = loader.load(name +".tmx");
        height = (int) map.getProperties().get(P_HEIGHT);
        width = (int) map.getProperties().get(P_WIDTH);
        tiledHeight = (int) map.getProperties().get(P_THEIGHT);
        tileWidth = (int) map.getProperties().get(P_TWIDTH);

        //convert to pixel size
        height = height * tiledHeight;
        width = width * tileWidth;
        renderer.setMap(map);

        for (MapObject object: map.getLayers().get("objects").getObjects()) {
            String objectName = object.getName();
            RectangleMapObject rectangleMapObject = (RectangleMapObject) object;
            Rectangle rectangle = rectangleMapObject.getRectangle();

            if (objectName.equals("player"))
                spawnPosition = new Vector2(rectangle.x, rectangle.y);
            if (objectName.equals("arrow_right"))
                fields.add(new Field(rectangle, Field.Type.ARROW_RIGHT));
            if (objectName.equals("arrow_left"))
                fields.add(new Field(rectangle, Field.Type.ARROW_LEFT));
            if (objectName.equals("arrow_up"))
                fields.add(new Field(rectangle, Field.Type.ARROW_UP));
            if (objectName.equals("arrow_down"))
                fields.add(new Field(rectangle, Field.Type.ARROW_DOWN));
            if (objectName.equals("exit"))
                exit = rectangle;
       }

        for (MapObject object: map.getLayers().get("physics").getObjects()) {
            String objectName = object.getName();
            RectangleMapObject rectangleMapObject = (RectangleMapObject) object;
            Rectangle rectangle = rectangleMapObject.getRectangle();

            if (objectName.equals("collision"))
                collisions.add(rectangle);
            if (objectName.equals("danger"))
                danger.add(rectangle);
        }

        Gdx.app.log("Map", "loaded map: " + toString());
        Gdx.app.log("Map", "collisions: " + collisions.toString());
    }

    public void render(OrthographicCamera camera){
        renderer.setView(camera);
        renderer.render();
    }

    public void renderBg(SpriteBatch batch){
        batch.begin();
        batch.draw(bg, 0, 0);
        batch.end();

    }

    public void debug(ShapeRenderer sr){
        sr.setColor(Color.GREEN);
        collisions.forEach(rectangle ->
            sr.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height)
        );
        sr.setColor(Color.RED);
        danger.forEach(rectangle ->
                sr.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height)
        );
        sr.setColor(Color.BLUE);
        fields.forEach(field ->
                sr.rect(field.rectangle.x, field.rectangle.y, field.rectangle.width, field.rectangle.height)
        );
    }

    public int height() {
        return height;
    }
    public int width() {
        return width;
    }

    public Vector2 spawn(){
        return spawnPosition;
    }
    public Rectangle exit() {return exit; }
    public Array<Rectangle> colisions() {return collisions;}
    public Array<Rectangle> danger() {return danger;}
    public Array<Field> fields() {return fields;}
    public String name(){
        return name;
    }
    public void dispose(){
        map.dispose();
        renderer.dispose();
//        bg.dispose();

    }

    @Override
    public String toString() {
        return "Map{" +
                "width=" + width +
                ", name=" + name +
                ", height=" + height +
                ", tileWidth=" + tileWidth +
                ", tiledHeight=" + tiledHeight +
                '}';
    }
}
