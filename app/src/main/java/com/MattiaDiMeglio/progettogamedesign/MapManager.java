package com.MattiaDiMeglio.progettogamedesign;

import android.content.Context;

public class MapManager {

    private GameWorld gameWorld;
    private GameObjectFactory gameObjectFactory;
    private JSonParser jSonParser;
    Context context;

    public MapManager(GameWorld gameWorld, GameObjectFactory gameObjectFactory, Context context){
        this.gameWorld = gameWorld;
        this.gameObjectFactory = gameObjectFactory;
        this.context = context;
        jSonParser = new JSonParser(context, this);
    }

    public void makeWalls(){
        int number = (int) AssetManager.background.getWidth() / AssetManager.horizontalWall.getWidth();
        for(int i = 0; i < number + 1; i++){
            gameWorld.addGameObject(gameObjectFactory.makeHorizontalWall(AssetManager.horizontalWall.getWidth()/2 + i * AssetManager.horizontalWall.getWidth(),
                    AssetManager.horizontalWall.getHeight()/2));
            gameWorld.addGameObject(gameObjectFactory.makeHorizontalWall(AssetManager.horizontalWall.getWidth()/2 + i * AssetManager.horizontalWall.getWidth(),
                    AssetManager.background.getHeight() - AssetManager.horizontalWall.getHeight()/2));
        }
        number = (int) AssetManager.background.getHeight() / AssetManager.verticalWall.getHeight();
        number++;
        for(int i = 0; i < number; i++){
            gameWorld.addGameObject(gameObjectFactory.makeVerticalWall(AssetManager.verticalWall.getWidth()/2,
                    AssetManager.verticalWall.getHeight()/2 + i * AssetManager.verticalWall.getHeight()));
            gameWorld.addGameObject(gameObjectFactory.makeVerticalWall(AssetManager.background.getWidth() - AssetManager.verticalWall.getWidth()/2,
                    AssetManager.verticalWall.getHeight()/2 + i * AssetManager.verticalWall.getHeight()));
        }

        jSonParser.parseWalls();
    }

    public void makeWall(String type, int worldX, int worldY){
        switch (type){
            case "horizontal":
                gameWorld.addGameObject(gameObjectFactory.makeHorizontalWall(worldX, worldY));
                break;
            case "vertical":
                gameWorld.addGameObject(gameObjectFactory.makeVerticalWall(worldX, worldY));
                break;
            case "horizontalHalf":
                gameWorld.addGameObject(gameObjectFactory.makeHorizontalHalfWall(worldX, worldY));
                break;
            case "verticalHalf":
                gameWorld.addGameObject(gameObjectFactory.makeVerticalHalfWall(worldX, worldY));
            default:
                break;
        }
    }

    public void makeEnemies(){
        jSonParser.parseEnemies();
    }

    public void makeEnemy(int worldX, int worldY){
        gameWorld.addGameObject(gameObjectFactory.makeEnemy(worldX, worldY));
    }
}
