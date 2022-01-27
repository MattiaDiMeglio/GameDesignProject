package com.MattiaDiMeglio.progettogamedesign;

import android.util.Log;

import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.Fixture;

public class GunComponent extends WeaponComponent{

    public GunComponent(){
        mag = 10;
        bullets = mag;
        range = 100;
        lineAmt = 1;
        this.aimLineX = new float[lineAmt];
        this.aimLineY = new float[lineAmt];
        shooter = null;
    }

    @Override
    public void shoot(GameWorld gameWorld) {
        Log.d("GunComponent","bullets = " +bullets);
        Log.d("GunComponent","shooting");
        bullets--;

        if(bullets == 0)
            reload();

        PhysicsComponent ownerBody = (PhysicsComponent) owner.getComponent(ComponentType.Physics);

        Fixture fixture = gameWorld.checkRaycast(ownerBody.getPositionX(),ownerBody.getPositionY(),
                aimLineX[0], aimLineY[0],shooter);

        if(fixture != null){
            Body castedBody = fixture.getBody();
            PhysicsComponent casteduserData = (PhysicsComponent) castedBody.getUserData();

            if(casteduserData.name.equals("Player"))
                gameWorld.killPlayer();
        }
    }

    @Override
    public void reload() {
        Log.d("GunComponent","reloading");
        bullets = mag;
    }

    @Override
    public void aim(float normalizedX, float normalizedY, float angle, GameWorld gameWorld) {

        if(shooter == null)
            shooter = owner.name;

        aimLineX[0] = gameWorld.toMetersXLength(range) * normalizedX;
        aimLineY[0] = gameWorld.toMetersYLength(range) * normalizedY;
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
}
