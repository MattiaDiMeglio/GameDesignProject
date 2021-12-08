package com.MattiaDiMeglio.progettogamedesign;

import android.util.Log;

import java.util.Random;

//the enemyGo
public class EnemyGameObject extends GameObject {
    private GameWorld gameWorld;//the gameWorld,
    private PixMapComponent drawableComponent;//component saved for simplicity
    private CharacterBodyComponent characterBodyComponent;
    protected boolean killed = false;//has it been killed?

    public EnemyGameObject(GameWorld gameWorld, int worldX, int worldY){//constructor
        this.gameWorld = gameWorld;//gw
        this.name = "Enemy";//name
        Random random = new Random();//?TODO probabilmente rimasuglio, vedere se si puó levare
        //gives the enemy a random position on the background
        this.worldX = worldX;//worldPos are the GO position on the map
        this.worldY = worldY;
        Log.d("Enemy", "worldx: " + this.worldX + " WorldY: " + this.worldY);
    }

    //updates the graphical and physical positions
    @Override
    public void updatePosition(int x, int y){
        drawableComponent = (PixMapComponent) components.get(ComponentType.Drawable);
        characterBodyComponent = (CharacterBodyComponent) components.get(ComponentType.Physics);

        drawableComponent.setPosition(x, y);

        float touchX = gameWorld.toPixelsTouchX(x);
        float touchY = gameWorld.toPixelsTouchY(y);
        characterBodyComponent.setTrasform(gameWorld.toMetersX(touchX),
                gameWorld.toMetersY(touchY));
    }

    @Override
    public void update() {
        drawableComponent = (PixMapComponent) components.get(ComponentType.Drawable);
        characterBodyComponent = (CharacterBodyComponent) components.get(ComponentType.Physics);

        drawableComponent.setPosition((int)gameWorld.toPixelsX(characterBodyComponent.getPositionX()),
                (int)gameWorld.toPixelsY(characterBodyComponent.getPositionY()));
    }

    @Override//puts enemy out of view
    public void outOfView() {
        characterBodyComponent.setTrasform(40, 40);
    }

    public void killed(){
        drawableComponent.pixmap = AssetManager.enemyKilled;
        killed = true;
    }
}
