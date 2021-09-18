package com.MattiaDiMeglio.progettogamedesign;

import android.util.Log;

import java.util.Random;

//the enemies
public class EnemyGameObject extends GameObject {
    private GameWorld gameWorld;
    private PixMapComponent drawableComponent;
    private CharacterBodyComponent characterBodyComponent;
    protected boolean killed = false;

    public EnemyGameObject(GameWorld gameWorld, int worldX, int worldY){
        this.gameWorld = gameWorld;
        this.name = "Enemy";
        Random random = new Random();
        //gives the enemy a random position on the background
        this.worldX = worldX;
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
        characterBodyComponent.setTransform(gameWorld.toMetersX(touchX),
                gameWorld.toMetersY(touchY));
    }

    @Override
    public void update() {
        drawableComponent = (PixMapComponent) components.get(ComponentType.Drawable);
        characterBodyComponent = (CharacterBodyComponent) components.get(ComponentType.Physics);

        drawableComponent.setPosition((int)gameWorld.toPixelsX(characterBodyComponent.getPositionX()),
                (int)gameWorld.toPixelsY(characterBodyComponent.getPositionY()));
    }

    @Override
    public void outOfView() {
        characterBodyComponent.setTransform(40, 40);
    }

    public void killed(){
        drawableComponent.pixmap = AssetManager.enemyKilled;
        killed = true;
    }
}
