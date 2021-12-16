package com.MattiaDiMeglio.progettogamedesign;

import android.util.Log;

public class GunComponent extends WeaponComponent{

    public GunComponent(){
        mag = 10;
        bullets = mag;
        range = 10.0f;
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
    public void aim(int angle, GameWorld gameWorld) {

        float normalX = 0f;
        float normalY = 0f;

        float convAngle = (float) Math.toRadians(angle);
        float cosAngle = (float) Math.cos(convAngle);
        float sinAngle = (float) Math.sin(convAngle);
        float length = (float) Math.sqrt( (cosAngle*cosAngle) + (sinAngle*sinAngle) );
        cosAngle /= length;
        sinAngle /= length;

        normalX = cosAngle;
        normalY = -sinAngle;

        aimLineX[0] = gameWorld.toPixelsXLength(range) * normalX;
        aimLineY[0] = gameWorld.toPixelsYLength(range) * normalY;

        Log.d("GunComponent","aimLineX[0] = "+aimLineX[0]+", aimLineY[0] = "+aimLineY[0]);
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
