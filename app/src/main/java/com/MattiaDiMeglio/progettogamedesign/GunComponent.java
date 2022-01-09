package com.MattiaDiMeglio.progettogamedesign;

import android.util.Log;

public class GunComponent extends WeaponComponent{

    public GunComponent(){
        mag = 10;
        bullets = mag;
        range = 100;
        lineAmt = 1;
        this.aimLineX = new float[lineAmt];
        this.aimLineY = new float[lineAmt];
    }

    @Override
    public void shoot(GameWorld gameWorld) {
        Log.d("GunComponent","bullets = " +bullets);
        Log.d("GunComponent","shooting");
        bullets--;

        if(bullets == 0)
            reload();

        gameWorld.checkRaycast(aimLineX[0], aimLineY[0]);
    }

    @Override
    public void reload() {
        Log.d("GunComponent","reloading");
        bullets = mag;
    }

    @Override
    public void aim(int rightX, int rightY, GameWorld gameWorld) {

        //normalizzazione
        float normalizedX = (float) (rightX-50) / 50;
        float normalizedY = (float) (rightY-50) / 50;

        /*
        float convAngle = (float) Math.toRadians(rightY);
        float cosAngle = (float) Math.cos(convAngle);
        float sinAngle = (float) Math.sin(convAngle);
        /*float length = (float) Math.sqrt( (cosAngle*cosAngle) + (sinAngle*sinAngle) );
        cosAngle /= length;
        sinAngle /= length;

        normalX = cosAngle;
        normalY = -sinAngle;*/

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
