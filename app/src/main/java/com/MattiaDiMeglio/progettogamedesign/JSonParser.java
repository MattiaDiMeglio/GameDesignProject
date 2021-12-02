package com.MattiaDiMeglio.progettogamedesign;

import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
//just a json parser. Retrieves everything from the map file and calls
//called by the map manager

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
            JSONObject jsonObject = new JSONObject(jsonFile);//in the file
            JSONArray walls = jsonObject.getJSONArray("walls");//checks the walls array
            for(int i = 0; i < walls.length(); i++){
                JSONObject wall = walls.getJSONObject(i);//for each one
                //calls the map manager to make a new wall TODO map manager chiama questo che chiama map manager. Si puó pulire
                mapManager.makeWall(wall.getString("type"), wall.getInt("worldx"), wall.getInt("worldy"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void parseEnemies(){
        try {
            JSONObject jsonObject = new JSONObject(jsonFile);//in the file
            JSONArray enemies = jsonObject.getJSONArray("enemies");//checks the enemies array
            for(int i = 0; i < enemies.length(); i++){
                JSONObject enemy = enemies.getJSONObject(i);//for eachone
                //calls the map manager to make a new enemy
                mapManager.makeEnemy(enemy.getInt("worldx"),enemy.getInt("worldy"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
