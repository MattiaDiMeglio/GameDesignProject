package com.MattiaDiMeglio.progettogamedesign;

import android.util.Log;

//component that controls a game object movement
//be it the playerGO or the enemy.
//it changes the position of the go components and should work
//indipendently from which class calls it (the AI will call this class to move the enemy
//and the joystick will do the same for the playerGO
public class ControllableComponent extends Component {
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
    public void moveCharacter(float normalizedX, float normalizedY, int angle){
        PixMapComponent pixmapComp = (PixMapComponent) owner.getComponent(ComponentType.Drawable);//gets the drawable component as ref for the movement
        DynamicBodyComponent dynamicBodyComponent = (DynamicBodyComponent) owner.getComponent(ComponentType.Physics);

        /*float bodyX = 0f;
        float bodyY = 0f;

        if(owner.name == "Enemy"){
            bodyX = dynamicBodyComponent.getPositionX();
            bodyY = dynamicBodyComponent.getPositionY();
            Log.i("ControllableComponent","Enemy body position = ("+bodyX+","+bodyY+")");
        }*/

        dynamicBodyComponent.update(normalizedX, normalizedY, angle);

        /*if(owner.name == "Enemy"){
            bodyX = dynamicBodyComponent.getPositionX();
            bodyY = dynamicBodyComponent.getPositionY();
            Log.i("ControllableComponent","NEW enemy body position = ("+bodyX+","+bodyY+")");
        }*/

        int currentGX = (int)gameWorld.toPixelsX(dynamicBodyComponent.getPositionX());
        int currentGY = (int)gameWorld.toPixelsY(dynamicBodyComponent.getPositionY());
        pixmapComp.setPosition(currentGX, currentGY, angle);

        owner.worldX = gameWorld.updateWorldX(pixmapComp.getPositionX());
        owner.worldY = gameWorld.updateWorldY(pixmapComp.getPositionY());
    }

    public void setAngle(float angle){
        this.rightStickangle = angle;
    }
}
