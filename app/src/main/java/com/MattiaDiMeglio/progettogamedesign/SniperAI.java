package com.MattiaDiMeglio.progettogamedesign;

public class SniperAI extends AIComponent{

    SniperAI(){
        super(AIType.Sniper);
        aimDelay = 0.23f;
        shootDelay = 0.5f;
        reloadDelay = 1.5f;}

    public void updateAI(int playerX, int playerY, float elapsedTime, Node[][] cells, GameWorld gameWorld){
        RifleComponent weaponComponent = (RifleComponent) owner.getComponent(ComponentType.Weapon);
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
        }
        else{
            if(reloadingTimer >= reloadDelay){
                weaponComponent.reload();
                reloadingTimer = 0f;
            }
            else reloadingTimer += elapsedTime;
        }
    }
}
