package com.MattiaDiMeglio.progettogamedesign;

import android.util.Log;

public class ShotgunComponent extends WeaponComponent{

    public ShotgunComponent(){
        mag = 3;
        bullets = mag;
        range = 80.0f;
        lineAmt = 5;
        this.aimLineX = new float[lineAmt];
        this.aimLineY = new float[lineAmt];
    }

    @Override
    public void shoot(GameWorld gameWorld) {
        Log.d("ShotgunComponent","bullets = " +bullets);
        Log.d("ShotgunComponent","shooting");
        bullets--;

        if(bullets == 0)
            reload();

        for(int i = 0; i < lineAmt; i++)
        gameWorld.checkRaycast(aimLineX[i], aimLineY[i]);
    }

    @Override
    public void aim(int rightX, int rightY, GameWorld gameWorld) {

        //con 5 pallini sparati a volta e un offset di 15°,
        //il cono dello shotgun sarà di 60°

        int angleOffset = 15;
        int half = lineAmt/2;
        int minAngle = rightY - (half * angleOffset);

        for(int i = 0; i < lineAmt; i++){

            float normalX = (float) (rightX-50)/50;
            float normalY = (float) (rightY-50)/50;;

            /*float convAngle = (float) Math.toRadians(minAngle + i * angleOffset);
            float cosAngle = (float) Math.cos(convAngle);
            float sinAngle = (float) Math.sin(convAngle);*/
            /*float length = (float) Math.sqrt( (cosAngle*cosAngle) + (sinAngle*sinAngle) );
            cosAngle /= length;
            sinAngle /= length;*/
/*
            normalX = cosAngle;
            normalY = -sinAngle;*/

            aimLineX[i] = range * normalX;
            aimLineY[i] = range * normalY;
        }
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
}