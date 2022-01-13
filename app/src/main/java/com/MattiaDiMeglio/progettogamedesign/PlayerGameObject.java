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
        controllableComponent = (ControllableComponent) components.get(ComponentType.Controllable);

        float normalizedX = (x-50) / 50;
        float normalizedY = (y-50) / 50;

        controllableComponent.moveCharacter(normalizedX, normalizedY, angle);
        DrawableComponent drawable = (DrawableComponent) components.get(ComponentType.Drawable);
        int addToX = 0;
        int addToY = 0;

        if(normalizedX!=0)
            addToX = (int)(Math.signum(normalizedX)*gameWorld.toPixelsXLengthNonBuffer(getMovedX()));

        if(normalizedY!=0)
            addToY = (int)(Math.signum(normalizedY)*gameWorld.toPixelsYLengthNonBuffer(getMovedY()));

        updateWorldPosition(addToX, addToY);
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

    public void updateWorldPosition(int x, int y){
        worldX += x;
        worldY += y;
        Log.d("playerPOs ", worldX + ", " + worldY);
    }

    public int getWorldX(){return worldX;}
    public int getWorldY(){return worldY;}

    public WeaponComponent getPlayerWeapon() {return (WeaponComponent) getComponent(ComponentType.Weapon);}

}
