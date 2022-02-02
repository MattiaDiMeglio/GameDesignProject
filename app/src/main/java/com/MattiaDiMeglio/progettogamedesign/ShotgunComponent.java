package com.MattiaDiMeglio.progettogamedesign;

import android.util.Log;

import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.Fixture;

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
        Log.d("ShotgunComponent","bullets = " +bullets);
        Log.d("ShotgunComponent","shooting");
        bullets--;

        if(bullets == 0 && shooter.equals("Player"))
            reload();

        PhysicsComponent ownerBody = (PhysicsComponent) owner.getComponent(ComponentType.Physics);

        for(int i = 0; i < lineAmt; i++){
            Fixture fixture = gameWorld.checkRaycast(ownerBody.getPositionX(),ownerBody.getPositionY(),
                    aimLineX[i], aimLineY[i],shooter);

            if(shooter.equals("Enemy") && fixture != null){
                Body castedBody = fixture.getBody();
                if (castedBody != null) {
                    PhysicsComponent casteduserData = (PhysicsComponent) castedBody.getUserData();
                    if (casteduserData.name.equals("Player"))
                        gameWorld.killPlayer();
                }
            }
        }
    }

    @Override
    public void aim(float normalizedX, float normalizedY, float rightAngle, GameWorld gameWorld) {

        //con 5 pallini sparati a volta e un offset di 15°,
        //il cono dello shotgun sarà di 60°

        int angleOffset = 15;
        int half = lineAmt/2;
        float minAngle = rightAngle - (half * angleOffset);

        float normalX = 0, normalY = 0;

        for(int i = 0; i < lineAmt; i++) {

            float convAngle = (float) Math.toRadians(minAngle + i * angleOffset);
            float cosAngle = (float) Math.cos(convAngle);
            float sinAngle = (float) Math.sin(convAngle);
            float length = (float) Math.sqrt((cosAngle * cosAngle) + (sinAngle * sinAngle));
            cosAngle /= length;
            sinAngle /= length;

            normalX = cosAngle;
            if(!owner.name.equals("Player"))
                normalY = sinAngle;
            else normalY = -sinAngle;

            aimLineX[i] = gameWorld.toMetersXLength(range) * (normalX);
            aimLineY[i] = gameWorld.toMetersYLength(range) * (normalY);
        }

        PhysicsComponent physicsComponent = (PhysicsComponent) owner.getComponent(ComponentType.Physics);
        float bodyX = physicsComponent.getPositionX();
        float bodyY = physicsComponent.getPositionY();

        gameWorld.addAimLine(lineAmt, bodyX, bodyY, aimLineX, aimLineY);
    }



    @Override
    public void reload() {
        Log.d("ShotgunComponent","reloading");
        bullets = mag;
    }

    @Override
    public float getRange() {
        return range;
    }

    @Override
    public int getLineAmt() { return lineAmt; }

    @Override
    public float[] getAimLineX() { return aimLineX; }

    @Override
    public float[] getAimLineY() { return aimLineY;}

    @Override
    public void setShooter(String shooter) {this.shooter = shooter;}
}