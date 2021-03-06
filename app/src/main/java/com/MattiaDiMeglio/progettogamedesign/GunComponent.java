package com.MattiaDiMeglio.progettogamedesign;

import android.graphics.Color;

public class GunComponent extends WeaponComponent{

    public GunComponent(){
        mag = 10;
        bullets = mag;
        range = 100;
        lineAmt = 1;
        aimLineX = new float[lineAmt];
        aimLineY = new float[lineAmt];
    }

    @Override
    public void shoot(GameWorld gameWorld) {
        bullets--;
        AssetManager.GunShoot.play(1);

        if(bullets == 0)
            AssetManager.GunReload.play(1);

        PhysicsComponent ownerBody = (PhysicsComponent) owner.getComponent(ComponentType.Physics);

        gameWorld.rayCastCallback.checkRaycast(ownerBody.getPositionX(),ownerBody.getPositionY(),
                aimLineX[0], aimLineY[0],shooter, gameWorld.levelGrid);
    }

    @Override
    public void aim(float normalizedX, float normalizedY, float angle, GameWorld gameWorld) {
        aimLineX[0] = gameWorld.toMetersXLength(range) * normalizedX;
        aimLineY[0] = gameWorld.toMetersYLength(range) * normalizedY;
    }

    @Override
    public void addAimLine(GameWorld gameWorld){
        PhysicsComponent physicsComponent = (PhysicsComponent) owner.getComponent(ComponentType.Physics);
        float bodyX = physicsComponent.getPositionX();
        float bodyY = physicsComponent.getPositionY();
        int aimLineColor = (shooter.equals("Player")) ? Color.BLUE: Color.RED;
        gameWorld.addAimLine(lineAmt, bodyX, bodyY, aimLineX, aimLineY, aimLineColor);
    }

    public boolean checkLineOfFire(GameWorld gameWorld){
        PhysicsComponent ownerBody = (PhysicsComponent) owner.getComponent(ComponentType.Physics);
        return gameWorld.rayCastCallback.checkLineOfFire(ownerBody.getPositionX(), ownerBody.getPositionY(), aimLineX[0], aimLineY[0]);
    }

    @Override
    public void setShooter(String shooter) {this.shooter = shooter;}
}
