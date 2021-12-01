package com.MattiaDiMeglio.progettogamedesign;

import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class JSonParser {
    String jsonFile;
    Context context;
    MapManager mapManager;

    public JSonParser(Context context, MapManager mapManager){
        this.context = context;
        this.mapManager = mapManager;
        AssetManager assetManager = context.getAssets();
        try {
            InputStream inputStream = assetManager.open("map.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            jsonFile = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void parseWalls(){
        try {
            JSONObject jsonObject = new JSONObject(jsonFile);
            JSONArray walls = jsonObject.getJSONArray("walls");
            for(int i = 0; i < walls.length(); i++){
                JSONObject wall = walls.getJSONObject(i);
                mapManager.makeWall(wall.getString("type"), wall.getInt("worldx"), wall.getInt("worldy"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void parseEnemies(){
        try {
            JSONObject jsonObject = new JSONObject(jsonFile);
            JSONArray enemies = jsonObject.getJSONArray("enemies");
            for(int i = 0; i < enemies.length(); i++){
                JSONObject enemy = enemies.getJSONObject(i);
                mapManager.makeEnemy(enemy.getInt("worldx"),enemy.getInt("worldy"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
