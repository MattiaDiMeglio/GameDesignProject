package com.MattiaDiMeglio.progettogamedesign;

import android.util.Log;

//the enemyGo
public class EnemyGameObject extends GameObject {
    private GameWorld gameWorld;//the gameWorld,
    private PixMapComponent drawableComponent;//component saved for simplicity
    private DynamicBodyComponent dynamicBodyComponent;
    private ControllableComponent controllableComponent;
    protected boolean killed = false;//has it been killed?

    public EnemyGameObject(GameWorld gameWorld, int worldX, int worldY){//constructor
        this.gameWorld = gameWorld;//gw
        this.name = "Enemy";//name
        //gives the enemy a random position on the background
        this.worldX = worldX;//worldPos are the GO position on the map
        this.worldY = worldY;
    }

    //updates the graphical and physical positions
    @Override
    public void updatePosition(int x, int y){
        drawableComponent = (PixMapComponent) components.get(ComponentType.Drawable);
        dynamicBodyComponent = (DynamicBodyComponent) components.get(ComponentType.Physics);

        drawableComponent.setPosition(x, y);

        float touchX = gameWorld.toPixelsTouchX(x);
        float touchY = gameWorld.toPixelsTouchY(y);
        dynamicBodyComponent.setTransform(gameWorld.toMetersX(touchX),
                gameWorld.toMetersY(touchY));
    }

    public void updatePosition(float x, float y, int angle){
        controllableComponent = (ControllableComponent) components.get(ComponentType.Controllable);
        controllableComponent.moveCharacter(x,y,angle);
    }

    @Override
    public void update() {
        drawableComponent = (PixMapComponent) components.get(ComponentType.Drawable);
        dynamicBodyComponent = (DynamicBodyComponent) components.get(ComponentType.Physics);

        drawableComponent.setPosition((int)gameWorld.toPixelsX(dynamicBodyComponent.getPositionX()),
                (int)gameWorld.toPixelsY(dynamicBodyComponent.getPositionY()));

        AIComponent aiComponent = (AIComponent) components.get(ComponentType.AI);
        aiComponent.movement(); //parte solo se lo stack dei movimenti non è vuoto

        //Log.i("Enemy GO","Enemy WorldX Y = ("+worldX+","+worldY+")");
        //Log.i("Enemy GO","Pixmap X Y = ("+drawableComponent.getPositionX()+","+drawableComponent.getPositionY()+")");
    }

    @Override//puts enemy out of view
    public void outOfView() {
        dynamicBodyComponent.setTransform(40, 40);
    }

    public void killed(){
        drawableComponent.pixmap = AssetManager.enemyKilled;
        killed = true;
    }

}
