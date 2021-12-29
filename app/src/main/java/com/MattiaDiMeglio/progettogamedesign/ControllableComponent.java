package com.MattiaDiMeglio.progettogamedesign;

import android.util.Log;

import com.badlogic.androidgames.framework.Pixmap;

//component that controls a game object movement
//be it the playerGO or the enemy.
//it changes the position of the go components and should work
//indipendently from which class calls it (the AI will call this class to move the enemy
//and the joystick will do the same for the playerGO
public class ControllableComponent extends Component {
    int destX, destY;//destination
    int currX, currY;
    int movementDistance = 5;//pixels
    GameWorld gameWorld;
    float rightStickangle;

    public ControllableComponent(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    @Override
    public ComponentType getType() {
        return ComponentType.Controllable;
    }



    //New movement system
    public void moveCharacter(int x, int y, int angle, int strength, float deltaTime){

        PixMapComponent pixmapComp = (PixMapComponent) owner.getComponent(ComponentType.Drawable);//gets the drawable component as ref for the movement
        Pixmap pixmap1 = pixmapComp.pixmap;//gets the actual pixmap
        int currentGX = pixmapComp.getPositionX();//pixmap position
        int currentGY = pixmapComp.getPositionY();
        CharacterBodyComponent characterBodyComponent = (CharacterBodyComponent) owner.getComponent(ComponentType.Physics);


        float normalizedX = (float) (x-50) / 50;
        float normalizedY = (float) (y-50) / 50;
        characterBodyComponent.update(normalizedX, normalizedY, angle);

        currentGX =(int)gameWorld.toPixelsX(characterBodyComponent.getPositionX());
        currentGY =(int)gameWorld.toPixelsY(characterBodyComponent.getPositionY());
        //Log.d("Controller", "x " + currentGX + " y: " + currentGY);
        pixmapComp.setPosition(currentGX, currentGY, angle);
        float currentPX = gameWorld.toMetersX(gameWorld.toPixelsTouchX(currentGX));
        float currentPY = gameWorld.toMetersY(gameWorld.toPixelsTouchY(currentGY));
        //characterBodyComponent.setTransform(currentPX, currentPY, (int)angle);
    }

    public void setAngle(float angle){
        this.rightStickangle = angle;
    }
}
