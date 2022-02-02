package com.MattiaDiMeglio.progettogamedesign;

import android.util.Log;

public class DummyAI extends AIComponent{

    private static final float DEFAULT_AIM_DELAY = 0.7f;
    private static final float DEFAULT_SHOOT_DELAY = 1.2f;

    private static final float BOX_AIM_DELAY = DEFAULT_AIM_DELAY/4;
    private static final float BOX_SHOOT_DELAY = DEFAULT_SHOOT_DELAY/4;

    DummyAI(){
        super();
        aimDelay = DEFAULT_AIM_DELAY;
        shootDelay = DEFAULT_SHOOT_DELAY;
        reloadDelay = 0.5f;
    }

    public void updateAI(int playerX, int playerY, float elapsedTime, Node[][] cells, GameWorld gameWorld){

        WeaponComponent weaponComponent = (WeaponComponent) owner.getComponent(ComponentType.Weapon);
        boolean oldPlayerInRange = playerInRange;

        super.updateAI(playerX,playerY,elapsedTime,cells, gameWorld);

        if(weaponComponent.bullets > 0){
            playerInRange = checkPlayerInRange();

            if(playerInRange){
                if(!movementStack.isEmpty())
                    emptyStack();

                if(aimDelay != DEFAULT_AIM_DELAY)
                    aimDelay = DEFAULT_AIM_DELAY;

                if(aimingTimer >= aimDelay){
                    dummyAim(weaponComponent, gameWorld, getLastPlayerX(), getLastPlayerY());

                    if(shootDelay != DEFAULT_SHOOT_DELAY)
                        shootDelay = DEFAULT_SHOOT_DELAY;

                    if(shootingTimer >= shootDelay)
                        dummyShoot(weaponComponent, gameWorld);
                    else shootingTimer += elapsedTime;
                }
                else aimingTimer += elapsedTime;
            }
            else{
                if(oldPlayerInRange){
                    aimingTimer = 0;
                    shootingTimer = 0;
                }
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

                if(aimDelay != BOX_AIM_DELAY)
                    aimDelay = BOX_AIM_DELAY;

                Log.d("DummyAI","aimDelay = "+aimDelay);
                Log.d("DummyAI","aimingTimer = "+aimingTimer);

                if(aimingTimer >= aimDelay){
                    Log.d("DummyAI","Dummy is aiming to a box");
                    dummyAim(weaponComponent, gameWorld, nextMovementX, nextMovementY);

                    if(shootDelay != BOX_SHOOT_DELAY)
                        shootDelay = BOX_SHOOT_DELAY;

                    if(shootingTimer >= shootDelay)
                        dummyShoot(weaponComponent, gameWorld);
                    else shootingTimer += elapsedTime;
                }
                else{
                    Log.d("DummyAI","Dummy is waiting to aim to a box");
                    aimingTimer += elapsedTime;
                }
            }
        }
    }
}
