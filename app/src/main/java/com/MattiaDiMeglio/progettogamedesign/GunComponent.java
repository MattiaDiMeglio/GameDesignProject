package com.MattiaDiMeglio.progettogamedesign;

public class GunComponent extends WeaponComponent{

    public GunComponent(){
        mag = 10;
        bullets = mag;
        range = 3;
    }

    @Override
    public void reload() {
        super.reload();

    }
}
