package com.MattiaDiMeglio.progettogamedesign;

import android.util.Log;

public class DummyAI extends AIComponent{

    float aimingTimer = 0f;
    float shootingTimer = 0f;
    float reloadingTimer = 0f;
    int shootCounter = 0;

    DummyAI(){ super(); }

    public void updateAI(int playerX, int playerY, float elapsedTime, Node[][] cells, GameWorld gameWorld){

        WeaponComponent weaponComponent = (WeaponComponent) owner.getComponent(ComponentType.Weapon);

        super.updateAI(playerX,playerY,elapsedTime,cells, gameWorld);

        if(weaponComponent.bullets > 0){
            playerInRange = checkPlayerInRange();

            if(playerInRange){
                if(!movementStack.isEmpty())
                    emptyStack();

                aimingTimer += elapsedTime;

                if(aimingTimer >= aimDelay){
                    int lineAmt = weaponComponent.getLineAmt();

                    float normalX = findNormalX(owner.worldX, owner.worldY, getLastPlayerX(), getLastPlayerY());
                    float normalY = findNormalY(owner.worldX, owner.worldY, getLastPlayerX(), getLastPlayerY());
                    float angle = 0f;

                    if(lineAmt > 1)
                        angle = (float) Math.toDegrees(Math.atan2(normalY, normalX));

                    weaponComponent.aim(normalX,normalY,angle, gameWorld);
                    shootingTimer += elapsedTime;

                    if(shootingTimer >= shootDelay){
                        aimingTimer = 0f;
                        shootingTimer = 0f;
                        weaponComponent.shoot(gameWorld);
                        shootCounter++;
                        Log.d("DummyAI","Shooting "+shootCounter);
                    }
                }
            }
            else pathfind(getLastPlayerX(), getLastPlayerY(),cells);
        }
        else{
            reloadingTimer += elapsedTime;
            if(reloadingTimer > reloadDelay){
                weaponComponent.reload();
                reloadingTimer = 0f;
            }
        }
    }
}
