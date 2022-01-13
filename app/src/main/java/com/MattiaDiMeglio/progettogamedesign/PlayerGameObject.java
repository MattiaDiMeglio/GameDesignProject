package com.MattiaDiMeglio.progettogamedesign;

import android.util.Log;

//the player GO
public class PlayerGameObject extends GameObject {
    private GameWorld gameWorld;
    private PixMapComponent drawableComponent;
    private ControllableComponent controllableComponent;
    private WeaponComponent playerWeapon;
    protected boolean canMove = false;

    public PlayerGameObject(GameWorld gameWorld){
        this.gameWorld = gameWorld;
        this.name = "Player";
        this.worldX = (int)gameWorld.screenSize.width/2;
        this.worldY = (int)gameWorld.screenSize.height/2;
    }

    @Override
    public void update() {//update
    }

    public void updatePosition(float x, float y, int angle, int strength, float deltaTime){

        float normalizedX = (x-50) / 50;
        float normalizedY = (y-50) / 50;

        controllableComponent = (ControllableComponent) components.get(ComponentType.Controllable);
        controllableComponent.moveCharacter(normalizedX, normalizedY, angle);
    }

    public float getMovedX(){
        PhysicsComponent component = (PhysicsComponent) getComponent(ComponentType.Physics);
        if(!isInContact())
            return Math.abs(component.getPositionX() - component.getLastPositionX());
        return 0f;
    }

    public float getMovedY(){
        PhysicsComponent component = (PhysicsComponent) getComponent(ComponentType.Physics);
        if(!isInContact())
            return Math.abs(component.getPositionY() - component.getLastPositionY());
        return 0f;
    }

    public void setPlayerWeapon(WeaponComponent weapon){
        removeComponent(ComponentType.Weapon);
        addComponent(weapon);
    }

    public boolean isInContact(){
        PhysicsComponent component = (PhysicsComponent) getComponent(ComponentType.Physics);
        com.google.fpl.liquidfun.SWIGTYPE_p_b2ContactEdge s = component.getContactList();
        return s != null;
    }

    public int getWorldX(){return worldX;}
    public int getWorldY(){return worldY;}

    public WeaponComponent getPlayerWeapon() {return (WeaponComponent) getComponent(ComponentType.Weapon);}

}
