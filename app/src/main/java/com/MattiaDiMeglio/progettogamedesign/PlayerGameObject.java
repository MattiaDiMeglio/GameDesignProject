package com.MattiaDiMeglio.progettogamedesign;

import android.util.Log;

import com.badlogic.androidgames.framework.Graphics;

//the player GO
public class PlayerGameObject extends GameObject {
    private GameWorld gameWorld;
    private PixMapComponent drawableComponent;
    private CharacterBodyComponent characterBodyComponent;
    private ControllableComponent controllableComponent;
    private WeaponComponent playerWeapon;
    protected boolean canMove = false;


    public PlayerGameObject(GameWorld gameWorld){
        this.gameWorld = gameWorld;
        this.name = "Player";
    }

    @Override
    public void update() {//update
    }

    public void updatePosition(int x, int y, int angle, int strength, float deltaTime){
        controllableComponent = (ControllableComponent) components.get(ComponentType.Controllable);
        controllableComponent.moveCharacter(x, y, angle);
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
            return  Math.abs(component.getPositionY() - component.getLastPositionY());
        return 0f;
    }

    public void setPlayerWeapon(WeaponComponent weapon){
        removeComponent(ComponentType.Weapon);
        addComponent(weapon);
    }

    public boolean isInContact(){
        PhysicsComponent component = (PhysicsComponent) getComponent(ComponentType.Physics);
        com.google.fpl.liquidfun.SWIGTYPE_p_b2ContactEdge s = component.getContactList();
        if(s == null)
            return false;
        return true;
    }

    public WeaponComponent getPlayerWeapon() {return (WeaponComponent) getComponent(ComponentType.Weapon);}

}
