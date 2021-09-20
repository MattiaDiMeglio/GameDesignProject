package com.MattiaDiMeglio.progettogamedesign;

import android.content.Context;

public class MapManager {

    private GameWorld gameWorld;
    private GameObjectFactory gameObjectFactory;
    Context context;

    public MapManager(GameWorld gameWorld, GameObjectFactory gameObjectFactory, Context context){
        this.gameWorld = gameWorld;
        this.gameObjectFactory = gameObjectFactory;
        this.context = context;

        makeOuterWalls();
        JSonParser jSonParser = new JSonParser(context);
        jSonParser.parseWalls(this);
    }

    private void makeOuterWalls(){
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
    }

    public void makeWall(String type, int worldX, int worldY){
        switch (type){
            case "horizontal":
                gameWorld.addGameObject(gameObjectFactory.makeHorizontalWall(worldX, worldY));
                break;
            case "vertical":
                gameWorld.addGameObject(gameObjectFactory.makeVerticalWall(worldX, worldY));
        }
    }
}
