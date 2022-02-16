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

    float reloadingTimer = 0f;
    float reloadDelay = 0.8f;

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
    @Override
    public void update() {//update
     //   drawableComponent = (PixMapComponent) components.get(ComponentType.Drawable);
     //   dynamicBodyComponent = (DynamicBodyComponent) components.get(ComponentType.Physics);

     //   drawableComponent.setPosition((int)gameWorld.toPixelsX(dynamicBodyComponent.getPositionX()),
       //         (int)gameWorld.toPixelsY(dynamicBodyComponent.getPositionY()));
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
        }
        else reloadingTimer += elapsedTime;
    }

    public void updatePosition(float x, float y, float angle, float strength, float deltaTime){
        if(canMove) {
           // Log.d("playerPos1", "world: " + worldX + ", " + worldY);
            controllableComponent = (ControllableComponent) components.get(ComponentType.Controllable);
            assert controllableComponent != null;
            controllableComponent.moveCharacter(x, y, angle);
          //  Log.d("playerPos2", "world: " + worldX + ", " + worldY);
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

}
