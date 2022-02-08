package com.MattiaDiMeglio.progettogamedesign;

public class DummyAI extends AIComponent{

    private static final float DEFAULT_AIM_DELAY = 0.7f;
    private static final float DEFAULT_SHOOT_DELAY = 0.5f;

    private static final float BOX_AIM_DELAY = DEFAULT_AIM_DELAY/4;
    private static final float BOX_SHOOT_DELAY = DEFAULT_SHOOT_DELAY/4;

    DummyAI(){
        super();
        aimDelay = DEFAULT_AIM_DELAY;
        shootDelay = DEFAULT_SHOOT_DELAY;
        reloadDelay = 1.0f;
    }

    public void updateAI(int playerX, int playerY, float elapsedTime, Node[][] cells, GameWorld gameWorld){

        WeaponComponent weaponComponent = (WeaponComponent) owner.getComponent(ComponentType.Weapon);
        boolean oldPlayerInRange = playerInRange;

        super.updateAI(playerX,playerY,elapsedTime,cells, gameWorld);

        if(weaponComponent.bullets > 0){
            /*if(aimingTimer < aimDelay)
                playerInRange = checkPlayerInRange();*/

            playerInRange = checkPlayerInRange();

            if(playerInRange){
                if(!movementStack.isEmpty())
                    emptyStack();

                if(aimDelay != DEFAULT_AIM_DELAY)
                    aimDelay = DEFAULT_AIM_DELAY;

                if(aimingTimer >= aimDelay){
                    enemyAim(weaponComponent, gameWorld, lastPlayerX, lastPlayerY);

                    if(shootDelay != DEFAULT_SHOOT_DELAY)
                        shootDelay = DEFAULT_SHOOT_DELAY;

                    if(shootingTimer >= shootDelay){
                        enemyShoot(weaponComponent, gameWorld);
                    }
                    else shootingTimer += elapsedTime;
                }
                else aimingTimer += elapsedTime;
            }
            else{
                if(oldPlayerInRange){
                    enemyTargetX = 0;
                    enemyTargetY = 0;
                    aimingTimer = 0;
                    shootingTimer = 0;
                }
                pathfind(lastPlayerX, lastPlayerY,cells);
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

    public void checkBoxOnPath(WeaponComponent weaponComponent, GameWorld gameWorld, float elapsedTime, Node[][] cells){
        if(!movementStack.isEmpty()){
            Movement nextMovement = movementStack.peek();
            int nextMovementX = nextMovement.cellX;
            int nextMovementY = nextMovement.cellY;
            if(findNode(nextMovementX,nextMovementY, gridSize, cells).isBox()){
                if(!movementStack.isEmpty())
                    emptyStack();

                if(aimDelay != BOX_AIM_DELAY)
                    aimDelay = BOX_AIM_DELAY;

                if(aimingTimer >= aimDelay){
                    enemyAim(weaponComponent, gameWorld, nextMovementX, nextMovementY);

                    if(shootDelay != BOX_SHOOT_DELAY)
                        shootDelay = BOX_SHOOT_DELAY;

                    if(shootingTimer >= shootDelay)
                        enemyShoot(weaponComponent, gameWorld);
                    else shootingTimer += elapsedTime;
                }
                else{
                    aimingTimer += elapsedTime;
                }
            }
        }
    }
}
