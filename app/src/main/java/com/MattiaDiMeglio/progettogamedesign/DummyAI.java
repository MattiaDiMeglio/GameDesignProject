package com.MattiaDiMeglio.progettogamedesign;

import android.util.Log;

public class DummyAI extends AIComponent{

    private static final float DEFAULT_AIM_DELAY = 0.25f;
    private static final float DEFAULT_SHOOT_DELAY = 0.1f;

    private static final float BOX_AIM_DELAY = DEFAULT_AIM_DELAY/3;
    private static final float BOX_SHOOT_DELAY = DEFAULT_SHOOT_DELAY/3;

    boolean oldBoxOnPath = false;

    DummyAI(){
        super(AIType.Dummy);
        aimDelay = DEFAULT_AIM_DELAY;
        shootDelay = DEFAULT_SHOOT_DELAY;
        reloadDelay = 1.0f;
    }

    public void updateAI(int playerX, int playerY, float elapsedTime, Node[][] cells, GameWorld gameWorld){

        WeaponComponent weaponComponent = (WeaponComponent) owner.getComponent(ComponentType.Weapon);
        super.updateAI(playerX,playerY,elapsedTime,cells, gameWorld);

        if(weaponComponent.bullets > 0){

            setEnemyTarget(lastPlayerX, lastPlayerY);
            playerInRange = checkPlayerInRange(gameWorld);

            if(playerInRange){
                if(!movementStack.isEmpty())
                    emptyStack();

                owner.updatePosition(0,0,((EnemyGameObject) owner).getFacingAngle());

                if(aimDelay != DEFAULT_AIM_DELAY)
                    aimDelay = DEFAULT_AIM_DELAY;

                if(aimingTimer >= aimDelay){
                    weaponComponent.addAimLine(gameWorld);

                    if(shootDelay != DEFAULT_SHOOT_DELAY)
                        shootDelay = DEFAULT_SHOOT_DELAY;

                    if(shootingTimer >= shootDelay)
                        enemyShoot(weaponComponent, gameWorld);
                    else shootingTimer += elapsedTime;
                }
                else aimingTimer += elapsedTime;
            }
            else{
                if(!oldBoxOnPath)
                    targetingReset();
                pathfind(lastPlayerX, lastPlayerY,cells);
                oldBoxOnPath = checkBoxOnPath(weaponComponent, gameWorld, elapsedTime, cells);
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

    public boolean checkBoxOnPath(WeaponComponent weaponComponent, GameWorld gameWorld, float elapsedTime, Node[][] cells){
        if(!movementStack.isEmpty()){
            Movement nextMovement = movementStack.peek();
            int nextCellX = nextMovement.cellX;
            int nextCellY = nextMovement.cellY;
            Node nextNode = findNode(nextCellX,nextCellY, gridSize, cells);
            if(nextNode.isBox()){

                if(!movementStack.isEmpty())
                    emptyStack();

                if(aimDelay != BOX_AIM_DELAY)
                    aimDelay = BOX_AIM_DELAY;

                setEnemyTarget(nextCellX,nextCellY);
                enemyAim(weaponComponent, gameWorld);

                if(aimingTimer >= aimDelay){
                    weaponComponent.addAimLine(gameWorld);

                    if(shootDelay != BOX_SHOOT_DELAY)
                        shootDelay = BOX_SHOOT_DELAY;

                    if(shootingTimer >= shootDelay)
                        enemyShoot(weaponComponent, gameWorld);
                    else shootingTimer += elapsedTime;
                }
                else aimingTimer += elapsedTime;
                return true;
            }
        }
        return false;
    }
}
