package com.MattiaDiMeglio.progettogamedesign;

import android.graphics.Color;

public class RifleComponent extends WeaponComponent{

    private float fixedRange; //used by Snipers, to shoot without revealing the maximum range of the rifle

    public RifleComponent(){
        mag = 1;
        bullets = mag;
        range = 250;
        fixedRange = range;
        lineAmt = 1;
        aimLineX = new float[lineAmt];
        aimLineY = new float[lineAmt];
    }

    @Override
    public void shoot(GameWorld gameWorld) {
        bullets--;
        AssetManager.RifleShoot.play(1);

        if(bullets == 0)
            AssetManager.RifleReload.play(1);

        PhysicsComponent ownerBody = (PhysicsComponent) owner.getComponent(ComponentType.Physics);
        gameWorld.rayCastCallback.checkRaycast(ownerBody.getPositionX(),ownerBody.getPositionY(),
                aimLineX[0], aimLineY[0],shooter, gameWorld.levelGrid);
    }

    @Override
    public void aim(float normalizedX, float normalizedY, float angle, GameWorld gameWorld) {
        aimLineX[0] = gameWorld.toMetersXLength(fixedRange) * normalizedX;
        aimLineY[0] = gameWorld.toMetersYLength(fixedRange) * normalizedY;
    }

    @Override
    public void addAimLine(GameWorld gameWorld){
        PhysicsComponent physicsComponent = (PhysicsComponent) owner.getComponent(ComponentType.Physics);
        float bodyX = physicsComponent.getPositionX();
        float bodyY = physicsComponent.getPositionY();
        gameWorld.addAimLine(lineAmt, bodyX, bodyY, aimLineX, aimLineY, Color.RED);
    }

    public boolean checkLineOfFire(GameWorld gameWorld){
        PhysicsComponent ownerBody = (PhysicsComponent) owner.getComponent(ComponentType.Physics);
        return gameWorld.rayCastCallback.checkLineOfFire(ownerBody.getPositionX(), ownerBody.getPositionY(), aimLineX[0], aimLineY[0]);
    }

    public void setFixedRange(float fixedRange) { this.fixedRange = fixedRange; }
}
