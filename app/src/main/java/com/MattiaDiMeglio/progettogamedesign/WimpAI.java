package com.MattiaDiMeglio.progettogamedesign;

import android.util.Log;

import java.util.Collections;
import java.util.List;

public class WimpAI extends AIComponent{

    boolean escape = true;
    int escapeCellX = 0;
    int escapeCellY = 0;
    float escapeTimer = 0;
    float escapeDelay;

    WimpAI() {
        super();
        aimDelay = 0.3f;
        shootDelay = 0.3f;
        reloadDelay = 0.5f;
        escapeDelay = 0.3f;
    }

    public void updateAI(int playerX, int playerY, float elapsedTime, Node[][] cells, GameWorld gameWorld){

        super.updateAI(playerX,playerY,elapsedTime,cells, gameWorld);

        escape = findEscape(gameWorld, cells);

        if(escape){
            Movement escapeMovement = new Movement(escapeCellX, escapeCellY);
            movementStack.push(escapeMovement);
        }
        else{
            if(!movementStack.isEmpty())
                emptyStack();

            playerInRange = checkPlayerInRange();

            if(playerInRange){
                WeaponComponent weaponComponent = (WeaponComponent) owner.getComponent(ComponentType.Weapon);

                if(weaponComponent.bullets > 0){
                    if(aimingTimer >= aimDelay){
                        enemyAim(weaponComponent, gameWorld, getLastPlayerX(), getLastPlayerY());

                        if(shootingTimer >= shootDelay)
                            enemyShoot(weaponComponent, gameWorld);
                        else shootingTimer += elapsedTime;
                    }
                    else aimingTimer += elapsedTime;
                }
                else{
                    reloadingTimer += elapsedTime;
                    if(reloadingTimer > reloadDelay){
                        weaponComponent.reload();
                        reloadingTimer = 0f;
                    }
                }
            }
            else{
                aimingTimer = 0;
                shootingTimer = 0;
            }


        }
    }

    public boolean findEscape(GameWorld gameWorld, Node[][] cells){

        float distanceToPlayer = getDistance(lastPlayerX, lastPlayerY, owner.worldX, owner.worldY);
        float newDistance = 0;

        Node actualWimpCell = findNode(owner.worldX, owner.worldY, gameWorld.gridSize, cells);
        List<Node.Edge> cellNeighbors = actualWimpCell.neighbors;
        Collections.shuffle(cellNeighbors);

        for(Node.Edge edge: cellNeighbors){
            Node neighbor = edge.node;

            if(neighbor.isBox())
                continue;

            newDistance = getDistance(lastPlayerX, lastPlayerY, neighbor.getPosX(), neighbor.getPosY());

            if(newDistance > distanceToPlayer +15){
                escapeCellX = neighbor.getPosX();
                escapeCellY = neighbor.getPosY();
                return true;
            }
        }
        return false;
    }

}
