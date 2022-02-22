package com.MattiaDiMeglio.progettogamedesign;

/*
Snipers stand still and shoot at the player if it's in range;
When they are aiming, the aimline length is set to the distance between
the Sniper and the player, so the maximum range of its weapon (RifleComponent)
is hidden to the player
 */

public class SniperAI extends AIComponent{

    SniperAI(){
        super(AIType.Sniper);
        aimDelay = 0.23f;
        shootDelay = 0.6f;
        reloadDelay = 1.5f;}

    public void updateAI(int playerX, int playerY, float elapsedTime, Node[][] cells, GameWorld gameWorld){
        RifleComponent weaponComponent = (RifleComponent) owner.getComponent(ComponentType.Weapon);
        super.updateAI(playerX,playerY,elapsedTime,cells, gameWorld);

        if(weaponComponent.bullets > 0){
            if(aimingTimer >= aimDelay){
                setEnemyTarget(lastPlayerX, lastPlayerY);
                weaponComponent.setFixedRange(weaponComponent.range);
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
