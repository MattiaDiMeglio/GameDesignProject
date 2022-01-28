package com.MattiaDiMeglio.progettogamedesign;

import android.util.Log;

//the player GO
public class PlayerGameObject extends GameObject {
    private GameWorld gameWorld;
    private PixMapComponent drawableComponent;
    private DynamicBodyComponent dynamicBodyComponent;
    private ControllableComponent controllableComponent;
    private WeaponComponent weaponComponent;
    protected boolean canMove = false;
    protected boolean killed = false;//has it been killed?

    public PlayerGameObject(GameWorld gameWorld){
        this.gameWorld = gameWorld;
        this.name = "Player";
        this.worldX = (int)gameWorld.screenSize.width/2;
        this.worldY = (int)gameWorld.screenSize.height/2;
    }

    @Override
    public void update() {//update
        drawableComponent = (PixMapComponent) components.get(ComponentType.Drawable);
        dynamicBodyComponent = (DynamicBodyComponent) components.get(ComponentType.Physics);

        drawableComponent.setPosition((int)gameWorld.toPixelsX(dynamicBodyComponent.getPositionX()),
                (int)gameWorld.toPixelsY(dynamicBodyComponent.getPositionY()));
    }

    public void updatePosition(float x, float y, float angle, float strength, float deltaTime){
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
            return Math.abs(component.getPositionY() - component.getLastPositionY());
        return 0f;
    }

    public boolean isInContact(){
        PhysicsComponent component = (PhysicsComponent) getComponent(ComponentType.Physics);
        com.google.fpl.liquidfun.SWIGTYPE_p_b2ContactEdge s = component.getContactList();
        return s != null;
    }

    public void killed(){
        if(!killed){
            drawableComponent.pixmap = AssetManager.playerKilled;
            killed = true;
        }
        else{
            drawableComponent.pixmap = AssetManager.player;
            killed = false;
        }
    }

    public int getWorldX(){return worldX;}
    public int getWorldY(){return worldY;}

}
