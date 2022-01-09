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
        return Math.abs(component.getPositionX() - component.getLastPositionX());
    }

    public float getMovedY(){
        PhysicsComponent component = (PhysicsComponent) getComponent(ComponentType.Physics);
        return  Math.abs(component.getPositionY() - component.getLastPositionY());
    }

    public void setPlayerWeapon(WeaponComponent weapon){
        removeComponent(ComponentType.Weapon);
        addComponent(weapon);
    }

    public WeaponComponent getPlayerWeapon() {return (WeaponComponent) getComponent(ComponentType.Weapon);}

}
