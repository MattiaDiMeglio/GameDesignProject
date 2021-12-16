package com.MattiaDiMeglio.progettogamedesign;

import android.util.Log;

public class GunComponent extends WeaponComponent{

    public GunComponent(){
        mag = 10;
        bullets = mag;
        range = 5.0f;
    }

    @Override
    public void shoot() {
        Log.d("GunComponent","bullets = " +bullets);
        Log.d("GunComponent","shooting");
        bullets--;

        if(bullets == 0)
            reload();
    }

    @Override
    public void reload() {
        Log.d("GunComponent","reloading");
        bullets = mag;
    }

    @Override
    public float getRange() {
        return range;
    }
}
