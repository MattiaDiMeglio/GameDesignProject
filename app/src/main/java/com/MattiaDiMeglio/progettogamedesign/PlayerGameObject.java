package com.MattiaDiMeglio.progettogamedesign;

import android.util.Log;

//the player GO
public class PlayerGameObject extends GameObject {
    private GameWorld gameWorld;
    private PixMapComponent drawableComponent;
    private DynamicBodyComponent dynamicBodyComponent;
    private ControllableComponent controllableComponent;
    private WeaponComponent weaponComponent;
    protected boolean canMove = true;
    protected boolean killed = false;//has it been killed?
    private boolean reloading  = false;

    float reloadingTimer = 0f;
    float reloadDelay = 0.8f;
    float lastAngle = 0f;

    public PlayerGameObject(GameWorld gameWorld){
        this.gameWorld = gameWorld;
        this.name = "Player";
        this.worldX = (int)gameWorld.bufferWidth/2;
        this.worldY = (int)gameWorld.bufferHeight/2;
    }

    public void CanMove(){
        canMove = true;
    }

    public void CantMove(){
        canMove = false;
    }

    public void update(float rightStrength, float rightX, float rightY, float rightAngle,
                       boolean isShooting, GameWorld gameWorld, float elapsedTime){

        weaponComponent = (WeaponComponent) components.get(ComponentType.Weapon);

        if(weaponComponent.bullets > 0){
            if(rightStrength > 0 && !(rightX == 0 && rightY == 0)){
                weaponComponent.aim(rightX, rightY, rightAngle,gameWorld);
                weaponComponent.addAimLine(gameWorld);
                if(isShooting)
                    weaponComponent.shoot(gameWorld);
            }
        }
        else if(reloadingTimer >= reloadDelay){
            weaponComponent.reload();
            reloadingTimer = 0;
            reloading = false;
        }
        else {
            reloadingTimer += elapsedTime;
            reloading = true;
        }

    }

    public void updatePosition(float x, float y, float rightAngle, float leftAngle){
        if(canMove) {
            controllableComponent = (ControllableComponent) components.get(ComponentType.Controllable);
            assert controllableComponent != null;

            if(rightAngle != 0){
                controllableComponent.moveCharacter(x, y, rightAngle);
                lastAngle = rightAngle;
            }

            else if(leftAngle != 0){
                controllableComponent.moveCharacter(x, y, leftAngle);
                lastAngle = leftAngle;
            }
            else controllableComponent.moveCharacter(x, y, lastAngle);
        }
    }

    public void updatePosition(int x, int y){
        if(!canMove) {
            drawableComponent = (PixMapComponent) components.get(ComponentType.Drawable);
            dynamicBodyComponent = (DynamicBodyComponent) components.get(ComponentType.Physics);

            drawableComponent.setPosition(x, y);

            float touchX = gameWorld.toPixelsTouchX(x);
            float touchY = gameWorld.toPixelsTouchY(y);
            dynamicBodyComponent.setTransform(gameWorld.toMetersX(touchX),
                    gameWorld.toMetersY(touchY));
        }
    }

    public boolean isInContact(){
        PhysicsComponent component = (PhysicsComponent) getComponent(ComponentType.Physics);
        com.google.fpl.liquidfun.SWIGTYPE_p_b2ContactEdge s = component.getContactList();
        return s != null;
    }

    public void killed(){
        PixMapComponent pixMapComponent = (PixMapComponent)components.get(ComponentType.Drawable);
        assert pixMapComponent != null;
        if(!killed){
            pixMapComponent.pixmap = AssetManager.playerKilled;
            killed = true;
        }
        else{
            pixMapComponent.pixmap = AssetManager.player;
            killed = false;
        }
    }

    public int getCurrentProjectiles(){
        weaponComponent = (WeaponComponent)getComponent(ComponentType.Weapon);
        return weaponComponent.bullets;
    }
    public int getMaxProjectiles(){
        weaponComponent = (WeaponComponent)getComponent(ComponentType.Weapon);
        return weaponComponent.mag;
    }

    public void resetProjectiles(){
        weaponComponent = (WeaponComponent)getComponent(ComponentType.Weapon);
        weaponComponent.reload();
    }

    public int getWorldX(){return worldX;}
    public int getWorldY(){return worldY;}

    public boolean isReloading() { return reloading; }
}
