package com.MattiaDiMeglio.progettogamedesign;

import java.util.Random;

/*
Major behaviours:

-If the player is in range: aims and shoots at it (note: it has the same weapon as the player)
-Else it moves to a random position in the level
 */

public class PatrolAI extends AIComponent{

    int randomPositionX = 0;
    int randomPositionY = 0;

    PatrolAI() {
        super(AIType.Patrol);
        aimDelay = 0.3f;
        shootDelay = 0.15f;
        reloadDelay = 0.5f;
    }

    public void updateAI(int playerX, int playerY, float elapsedTime, Node[][] cells, GameWorld gameWorld){

        WeaponComponent weaponComponent = (WeaponComponent) owner.getComponent(ComponentType.Weapon);
        boolean oldPlayerInRange = playerInRange;

        super.updateAI(playerX,playerY,elapsedTime,cells, gameWorld);

        if(weaponComponent.bullets > 0){

            setEnemyTarget(lastPlayerX, lastPlayerY);
            playerInRange = checkPlayerInRange(gameWorld);

            if(playerInRange){
                if(!movementStack.isEmpty())
                    emptyStack();

                owner.updatePosition(0,0,((EnemyGameObject) owner).getFacingAngle());

                if(aimingTimer >= aimDelay){
                    weaponComponent.addAimLine(gameWorld);

                    if(shootingTimer >= shootDelay)
                        enemyShoot(weaponComponent, gameWorld);
                    else shootingTimer += elapsedTime;

                } else aimingTimer += elapsedTime;
            }
            else{
                targetingReset();
                if(oldPlayerInRange){
                    randomPositionX = 0;
                    randomPositionY = 0;
                }

                //if Patrol doesn't have a position to reach, search it using setRandomPosition
                if(randomPositionX == 0 && randomPositionY == 0)
                    setRandomPosition(cells);

                //setRandomPosition might find an invalid position, such as out of map or a cell containing an obstacle,
                //so before moving Patrol to the position, do a check on it
                if(!(randomPositionX == 0 && randomPositionY == 0))
                    pathfind(randomPositionX, randomPositionY, cells);

                //if Patrol reached position, reset the position
                if(checkPatrolDestination()){
                    randomPositionX = 0;
                    randomPositionY = 0;
                }
            }
        }
        else{
            if(reloadingTimer > reloadDelay){
                weaponComponent.reload();
                reloadingTimer = 0f;
            }
            else reloadingTimer += elapsedTime;
        }
    }

    public void setRandomPosition(Node[][] cells){

        Random random = new Random();

        int minRadius = gridSize * 3; // 42 * 3 = 126
        //126 < randomRadius < 378
        int randomRadius = (int) (minRadius + (minRadius * 2 * Math.sqrt(random.nextFloat())));
        //0 < randomAngle < 2 * PI
        float randomAngle = (float) (random.nextFloat()  * 2 * Math.PI);

        int centerX = owner.worldX;
        int centerY = owner.worldY;

        int x = (int) (centerX + randomRadius * Math.cos(randomAngle));
        int y = (int) (centerY + randomRadius * Math.sin(randomAngle));

        int cellX = x / gridSize;
        int cellY = y / gridSize;

        //check that the cell is inside the map
        if((cellX >= 0 && cellX < cells.length) && (cellY >= 0 && cellY < cells.length)){
            //check that the cell is free
            if(!cells[cellY][cellX].isEnemy() && !cells[cellY][cellX].isBox() && !cells[cellY][cellX].isObstacle()){
                randomPositionX = cells[cellY][cellX].getPosX();
                randomPositionY = cells[cellY][cellX].getPosY();
            }
        }
        //if it doesn't find a valid position, it will try again at next frame
    }

    public boolean checkPatrolDestination(){
        int patrolCellX = owner.worldX / gridSize;
        int patrolCellY = owner.worldY / gridSize;
        int randomPositionCellX = randomPositionX / gridSize;
        int randomPositionCellY = randomPositionY / gridSize;

        return patrolCellX == randomPositionCellX && patrolCellY == randomPositionCellY;
    }

}
