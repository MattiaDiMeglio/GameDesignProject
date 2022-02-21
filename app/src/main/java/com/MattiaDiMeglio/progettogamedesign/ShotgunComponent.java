package com.MattiaDiMeglio.progettogamedesign;

import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.Fixture;
import android.graphics.Color;

public class ShotgunComponent extends WeaponComponent{

    public ShotgunComponent(){
        mag = 3;
        bullets = mag;
        range = 60;
        lineAmt = 5;
        aimLineX = new float[lineAmt];
        aimLineY = new float[lineAmt];
    }

    @Override
    public void shoot(GameWorld gameWorld) {
        bullets--;
        AssetManager.ShotgunShoot.play(0.25f);

        if(bullets == 0)
            AssetManager.ShotgunReload.play(1);

        PhysicsComponent ownerBody = (PhysicsComponent) owner.getComponent(ComponentType.Physics);

        for(int i = 0; i < lineAmt; i++){
            gameWorld.rayCastCallback.checkRaycast(ownerBody.getPositionX(),ownerBody.getPositionY(),
                    aimLineX[i], aimLineY[i],shooter, gameWorld.levelGrid);

        }
    }

    @Override
    public void aim(float normalizedX, float normalizedY, float rightAngle, GameWorld gameWorld) {

        //con 5 pallini sparati alla volta e un offset di 15°,
        //il cono dello shotgun sarà di 60°

        //using an angle offset of 15° and 5 bullets fired at a time,
        //the shotgun cone will be 60°

        int angleOffset = 15;
        int half = lineAmt/2;
        float minAngle = rightAngle - (half * angleOffset);

        float normalX = 0, normalY = 0;

        for(int i = 0; i < lineAmt; i++) {

            float convAngle = (float) Math.toRadians(minAngle + (i * angleOffset));
            float cosAngle = (float) Math.cos(convAngle);
            float sinAngle = (float) Math.sin(convAngle);
            float length = (float) Math.sqrt((cosAngle * cosAngle) + (sinAngle * sinAngle));
            cosAngle /= length;
            sinAngle /= length;

            normalX = cosAngle;
            normalY = -sinAngle;

            aimLineX[i] = gameWorld.toMetersXLength(range) * (normalX);
            aimLineY[i] = gameWorld.toMetersYLength(range) * (normalY);
        }
    }

    @Override
    public void addAimLine(GameWorld gameWorld) {
        PhysicsComponent physicsComponent = (PhysicsComponent) owner.getComponent(ComponentType.Physics);
        float bodyX = physicsComponent.getPositionX();
        float bodyY = physicsComponent.getPositionY();
        gameWorld.addAimLine(lineAmt, bodyX, bodyY, aimLineX, aimLineY, Color.RED);
    }

    public boolean checkLineOfFire(GameWorld gameWorld){
        PhysicsComponent ownerBody = (PhysicsComponent) owner.getComponent(ComponentType.Physics);
        int centralAimLine = lineAmt/2; //check line of fire just using the central aim line

        return gameWorld.rayCastCallback.checkLineOfFire(ownerBody.getPositionX(), ownerBody.getPositionY(),
                aimLineX[centralAimLine], aimLineY[centralAimLine]);
    }

    @Override
    public void reload() {
        bullets = mag;
    }

    @Override
    public float getRange() {
        return range;
    }

    @Override
    public void setShooter(String shooter) {this.shooter = shooter;}
}