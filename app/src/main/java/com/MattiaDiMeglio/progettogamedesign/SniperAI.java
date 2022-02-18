package com.MattiaDiMeglio.progettogamedesign;

public class SniperAI extends AIComponent{

    SniperAI(){
        super(AIType.Sniper);
        aimDelay = 0.3f;
        shootDelay = 0.8f;
        reloadDelay = 1.5f;}

    public void updateAI(int playerX, int playerY, float elapsedTime, Node[][] cells, GameWorld gameWorld){
        RifleComponent weaponComponent = (RifleComponent) owner.getComponent(ComponentType.Weapon);
        boolean oldPlayerInRange = playerInRange;

        super.updateAI(playerX,playerY,elapsedTime,cells, gameWorld);

        if(weaponComponent.bullets > 0){

            if(aimingTimer >= aimDelay){

                setEnemyTarget(lastPlayerX, lastPlayerY);
                weaponComponent.setFixedRange(weaponComponent.getRange());
                playerInRange = checkPlayerInRange(gameWorld);

                if(playerInRange){
                    owner.updatePosition(0,0,((EnemyGameObject) owner).getFacingAngle());
                    weaponComponent.setFixedRange(getDistanceToPlayer());
                    enemyAim(weaponComponent,gameWorld);
                    weaponComponent.addAimLine(gameWorld);
                    if(shootingTimer >= shootDelay)
                        enemyShoot(weaponComponent, gameWorld);
                    else shootingTimer += elapsedTime;
                }
                else targetingReset();
            }else aimingTimer += elapsedTime;

            /*setEnemyTarget(lastPlayerX, lastPlayerY);
            weaponComponent.setFixedRange(weaponComponent.getRange());
            playerInRange = checkPlayerInRange(gameWorld);

            if(playerInRange){
                weaponComponent.setFixedRange(getPlayerDistance()-13);
                owner.updatePosition(0,0,((EnemyGameObject) owner).getFacingAngle());
                if(aimingTimer >= aimDelay){
                    weaponComponent.addAimLine(gameWorld);

                    if(shootingTimer >= shootDelay)
                        enemyShoot(weaponComponent, gameWorld);
                    else shootingTimer += elapsedTime;
                } else aimingTimer += elapsedTime;
            }
            else{
                if(oldPlayerInRange){
                    aimingTimer = 0;
                    shootingTimer = 0;
                    enemyTargetX = 0;
                    enemyTargetY = 0;
                }
            }*/
        }
        else{
            if(reloadingTimer >= reloadDelay){
                weaponComponent.reload();
                reloadingTimer = 0f;
            }
            else reloadingTimer += elapsedTime;
        }
    }

   /* public boolean checkPlayerInRange(GameWorld gameWorld){

        RifleComponent sniperWeapon = (RifleComponent) owner.getComponent(ComponentType.Weapon);
        float range = sniperWeapon.getRange();
        float distanceToPlayer = getDistance(lastPlayerX, lastPlayerY, owner.worldX, owner.worldY);

        if(distanceToPlayer <= range+18){
            sniperWeapon.setFixedRange(distanceToPlayer-13);
            enemyAim(sniperWeapon, gameWorld, lastPlayerX, lastPlayerY);
            if(sniperWeapon.checkLineOfFire(gameWorld)){
                return true;
            }
        }

        enemyTargetX = 0;
        enemyTargetY = 0;
        return false;
    }*/
}
