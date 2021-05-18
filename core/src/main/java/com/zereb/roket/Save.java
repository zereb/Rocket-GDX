package com.zereb.roket;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.MathUtils;

import java.util.HashMap;

public class Save {

    private final Preferences prefs;
    private int current = Main.FIRST_LVL;
    private final HashMap<String, Long> levels;
    //Format of string is <String level>:<long record><coma>
    public static final Save INSTANCE = new Save();

    private Save(){
        levels = new HashMap<>();
        prefs = Gdx.app.getPreferences("save");
        load();
   }

    public long getHighScore(String level){
        if (levels.containsKey(level))
            return levels.get(level);
        else
            return 0;
    }

    public boolean setHighScore(String level, long score){
        if (!levels.containsKey(level)){
            levels.put(level, score);
            return true;
        }
        if (levels.get(level) < score)
            return false;

        levels.put(level, score);
        return true;
    }

    public int current(){
        return current;
    }

    public int setCurrent(int current){
        this.current = MathUtils.clamp(current, Main.FIRST_LVL, Main.LAST_LVL);
        save();
        return this.current;
    }


    private void save(){
        StringBuilder builder = new StringBuilder();
        levels.forEach((level, score) ->
            builder.append(level).append(":").append(score).append(",")
        );
        prefs.putInteger("current", current);
        prefs.putString("score", builder.toString());
        prefs.flush();
    }

    private void load(){
        current = prefs.getInteger("current", Main.FIRST_LVL);
        String rawLevels = prefs.getString("score", null);
        if (rawLevels != null && !rawLevels.isEmpty()){
            for (String records : rawLevels.split(",")) {
                String[] score = records.split(":");
                levels.put(score[0], Long.valueOf(score[1]));
            }
        }
    }

}
