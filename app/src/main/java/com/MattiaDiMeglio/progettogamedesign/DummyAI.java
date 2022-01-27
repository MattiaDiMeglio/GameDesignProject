package com.MattiaDiMeglio.progettogamedesign;

import android.util.Log;

import com.badlogic.androidgames.framework.Game;

public class DummyAI extends AIComponent{

    float aimingTimer = 0f;
    float shootingTimer = 0f;
    int shootCounter =0;

    DummyAI(){
        super();
        isAiming = false;
    }

    public void updateAI(int playerX, int playerY, float elapsedTime, Node[][] cells, GameWorld gameWorld){

        WeaponComponent weaponComponent = (WeaponComponent) owner.getComponent(ComponentType.Weapon);

        int lastPlayerX = getPlayerX();
        int lastPlayerY = getPlayerY();

        super.updateAI(playerX,playerY,elapsedTime,cells, gameWorld);

        //getPlayerX() != lastPlayerX || getPlayerY() != lastPlayerY) &&

        if(!isAiming){
            pathfind(getPlayerX(),getPlayerY(),cells);
        }

        if(!playerInRange){
            playerInRange = super.checkPlayerInRange(gameWorld);
        }

        if(playerInRange){
            isAiming = true;
            emptyStack();
        }

        if(isAiming){
            aimingTimer += elapsedTime;
        }

        if(aimingTimer >= aimDelay){

            int lineAmt = weaponComponent.getLineAmt();
            float[] aimLineX = weaponComponent.getAimLineX();
            float[] aimLineY = weaponComponent.getAimLineY();

            PhysicsComponent dummyBody = (PhysicsComponent) owner.getComponent(ComponentType.Physics);
            float dummyBodyX = dummyBody.getPositionX();
            float dummyBodyY = dummyBody.getPositionY();

            gameWorld.gameScreen.setLineCoordinates(lineAmt, dummyBodyX, dummyBodyY, aimLineX, aimLineY);

            shootingTimer += elapsedTime;
        }

        if(shootingTimer >= shootDelay){
            isAiming = false;
            playerInRange = false;
            aimingTimer = 0f;
            shootingTimer = 0f;
            weaponComponent.shoot(gameWorld);
            shootCounter++;
            Log.d("DummyAI","Shooting "+shootCounter);
        }

    }

}
