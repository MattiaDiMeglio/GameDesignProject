package com.MattiaDiMeglio.progettogamedesign;

import android.content.Context;
//the map manager, calls the jsonparser and the factory to make the map

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

    public void generateMap(Node[][] cells, int startingX, int startingY, int endingX, int endingY){

    };

    public void makeWalls(){//makes the wall of the maps
        //makes the perimeter walls
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
        //calls the json parser to get the internal walls
        jSonParser.parseWalls();
    }
    //called by the parser. Calls the corrispondent factory method based on the wall type
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

    //calls the parser for the enemies
    public void makeEnemies(){
        jSonParser.parseEnemies();
    }

    //calls the factory to make the enemies
    public void makeEnemy(int worldX, int worldY){
        gameWorld.addGameObject(gameObjectFactory.makeEnemy(worldX, worldY));
    }
}
