package com.MattiaDiMeglio.progettogamedesign;

public class GunComponent extends WeaponComponent{

    public GunComponent(){
        mag = 10;
        bullets = mag;
        range = 5.0f;
    }

    @Override
    public void reload() {
        super.reload();

    }
}
