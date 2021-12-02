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
    public void update() {//update
        //for now it just advances the player movement. TODO si pu; cambiare per il cambio di sistema di movimento
        controllableComponent = (ControllableComponent)components.get(ComponentType.Controllable);
        drawableComponent = (PixMapComponent) components.get(ComponentType.Drawable);
        if(canMove)
           canMove = controllableComponent.moveCharacter();
    }

    @Override
    public void updatePosition(int x, int y){//set the GO position//TODO cambiare
        drawableComponent = (PixMapComponent) components.get(ComponentType.Drawable);
        characterBodyComponent = (CharacterBodyComponent) components.get(ComponentType.Physics);

        drawableComponent.setPosition(x, y);

        float touchX = gameWorld.toPixelsTouchX(x);
        float touchY = gameWorld.toPixelsTouchY(y);
        characterBodyComponent.setTransform(gameWorld.toMetersX(touchX),
                gameWorld.toMetersY(touchY));
    }
    //when the world moves the player moves in reverse to it to stay on center //TODO si puó levare, sempre per gli stessi motivi
    public void reverseWorldMovement(int x, int y, boolean onBorderX, boolean onBorderY){
        drawableComponent = (PixMapComponent) components.get(ComponentType.Drawable);
        int destX = x, destY = y;
        if(onBorderX)
            destX = drawableComponent.getPositionX() + drawableComponent.pixmap.getWidth()/2;
        if(onBorderY)
            destY = drawableComponent.getPositionY() + drawableComponent.pixmap.getHeight()/2;
        updatePosition(destX, destY);

    }

    //just for testing
    public void writePosition(){
        drawableComponent = (PixMapComponent) components.get(ComponentType.Drawable);
        characterBodyComponent = (CharacterBodyComponent) components.get(ComponentType.Physics);
        Log.d("Player", "Posizione grafica " + drawableComponent.getPositionX() + ", " +
                drawableComponent.getPositionY() + ", posizione fisica " + characterBodyComponent.getPositionX() +
                ", " + characterBodyComponent.getPositionY());
    }

    public void setDestination(int x, int y){//sets the movement destination TODO si puó togliere
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
