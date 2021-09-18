package com.MattiaDiMeglio.progettogamedesign;

import android.util.Log;

import com.badlogic.androidgames.framework.Graphics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//the player GO
public class PlayerGameObject extends GameObject {
    private GameWorld gameWorld;
    private PixMapComponent drawableComponent;
    private CharacterBodyComponent characterBodyComponent;
    private ControllableComponent controllableComponent;
    protected boolean canMove = false;

    public PlayerGameObject(GameWorld gameWorld){
        this.gameWorld = gameWorld;
        this.name = "Player";
    }

    @Override
    public void update() {
        controllableComponent = (ControllableComponent)components.get(ComponentType.Controllable);
        drawableComponent = (PixMapComponent) components.get(ComponentType.Drawable);
        if(canMove)
           canMove = controllableComponent.moveCharacter(drawableComponent.pixmap, drawableComponent.getPositionX(), drawableComponent.getPositionY());
    }

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

    //just for testing
    public void writePosition(){
        drawableComponent = (PixMapComponent) components.get(ComponentType.Drawable);
        characterBodyComponent = (CharacterBodyComponent) components.get(ComponentType.Physics);
        Log.d("Player", "Posizione grafica " + drawableComponent.getPositionX() + ", " +
                drawableComponent.getPositionY() + ", posizione fisica " + characterBodyComponent.getPositionX() +
                ", " + characterBodyComponent.getPositionY());
    }

    public void setDestination(int x, int y){
        controllableComponent = (ControllableComponent)components.get(ComponentType.Controllable);
        controllableComponent.setDestination(x, y);
        canMove = true;
    }


    public void draw(Graphics graphics, GameWorld gameWorld){
        characterBodyComponent = (CharacterBodyComponent) getComponent(ComponentType.Physics);
        characterBodyComponent.draw(graphics, gameWorld);
    }

    public boolean canMove(){return canMove;}

}
