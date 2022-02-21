package com.MattiaDiMeglio.progettogamedesign;

import android.util.Log;

import java.util.Random;

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

                //se il nemico non ha una posizione da raggiungere, ne cerchiamo una con setRandomPosition
                if(randomPositionX == 0 && randomPositionY == 0)
                    setRandomPosition(cells);

                //la funzione precedente non è detto che trovi una posizione valida, potrebbe trovare una posizione fuori mappa,
                //oppure una cella contenente un ostacolo, quindi il controllo nell'if è necessario
                if(!(randomPositionX == 0 && randomPositionY == 0))
                    pathfind(randomPositionX, randomPositionY, cells);

                //per controllare che abbia raggiunto la destinazione, così al prossimo frame ne cercherà un'altra
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
        //Il raggio è compreso tra 126 e 378
        int randomRadius = (int) (minRadius + (minRadius * 2 * Math.sqrt(random.nextFloat())));
        //L'angolo è compreso tra 0 e 2 PI
        float randomAngle = (float) (random.nextFloat()  * 2 * Math.PI);

        int centerX = owner.worldX;
        int centerY = owner.worldY;

        int x = (int) (centerX + randomRadius * Math.cos(randomAngle));
        int y = (int) (centerY + randomRadius * Math.sin(randomAngle));

        int cellX = x / gridSize;
        int cellY = y / gridSize;

        //controllo che sia all'interno della griglia
        if((cellX >= 0 && cellX < cells.length) && (cellY >= 0 && cellY < cells.length)){
            //controllo che sia una cella libera
            if(!cells[cellY][cellX].isEnemy() && !cells[cellY][cellX].isBox() && !cells[cellY][cellX].isObstacle()){
                randomPositionX = cells[cellY][cellX].getPosX();
                randomPositionY = cells[cellY][cellX].getPosY();
            }
        }
        //se non trova una posizione valida, riproverà al prossimo frame
    }

    public boolean checkPatrolDestination(){
        int patrolCellX = owner.worldX / gridSize;
        int patrolCellY = owner.worldY / gridSize;
        int randomPositionCellX = randomPositionX / gridSize;
        int randomPositionCellY = randomPositionY / gridSize;

        return patrolCellX == randomPositionCellX && patrolCellY == randomPositionCellY;
    }

}
