package com.MattiaDiMeglio.progettogamedesign;

import android.util.Log;

public class DummyAI extends AIComponent{

    DummyAI(){ super(); }

    public void updateAI(int playerX, int playerY, float elapsedTime, Node[][] cells, GameWorld gameWorld){

        WeaponComponent weaponComponent = (WeaponComponent) owner.getComponent(ComponentType.Weapon);

        super.updateAI(playerX,playerY,elapsedTime,cells, gameWorld);

        if(weaponComponent.bullets > 0){
            playerInRange = checkPlayerInRange();

            if(playerInRange){
                if(!movementStack.isEmpty())
                    emptyStack();

                if(aimingTimer >= aimDelay){
                    dummyAim(weaponComponent, gameWorld, getLastPlayerX(), getLastPlayerY());
                    if(shootingTimer >= shootDelay)
                        dummyShoot(weaponComponent, gameWorld);
                    else shootingTimer += elapsedTime;
                }
                else aimingTimer += elapsedTime;
            }
            else{
                aimingTimer = 0;
                shootingTimer = 0;
                pathfind(getLastPlayerX(), getLastPlayerY(),cells);
                checkBoxOnPath(weaponComponent, gameWorld, elapsedTime, cells);
            }
        }
        else{
            reloadingTimer += elapsedTime;
            if(reloadingTimer > reloadDelay){
                weaponComponent.reload();
                reloadingTimer = 0f;
            }
        }
    }

    public void dummyAim(WeaponComponent weaponComponent, GameWorld gameWorld, int targetX, int targetY){

        int lineAmt = weaponComponent.getLineAmt();

        float normalX = findNormalX(owner.worldX, owner.worldY, targetX, targetY);
        float normalY = findNormalY(owner.worldX, owner.worldY, targetX, targetY);
        float angle = 0f;

        if(lineAmt > 1)
            angle = (float) Math.toDegrees(Math.atan2(normalY, normalX));

        weaponComponent.aim(normalX,normalY,angle, gameWorld);
    }

    public void dummyShoot(WeaponComponent weaponComponent, GameWorld gameWorld){
        aimingTimer = 0f;
        shootingTimer = 0f;
        weaponComponent.shoot(gameWorld);
    }

    public void checkBoxOnPath(WeaponComponent weaponComponent, GameWorld gameWorld, float elapsedTime, Node[][] cells){
        if(!movementStack.isEmpty()){
            Movement nextMovement = movementStack.peek();
            int nextMovementX = nextMovement.cellX;
            int nextMovementY = nextMovement.cellY;
            if(findNode(nextMovementX,nextMovementY, gameWorld.gridSize, cells).isBox()){
                if(!movementStack.isEmpty())
                    emptyStack();
                aimingTimer += elapsedTime;
                if(aimingTimer >= aimDelay){
                    dummyAim(weaponComponent, gameWorld, nextMovementX, nextMovementY);
                    shootingTimer += elapsedTime;
                    if(shootingTimer >= shootDelay)
                        dummyShoot(weaponComponent, gameWorld);
                }
            }
        }
    }
}
