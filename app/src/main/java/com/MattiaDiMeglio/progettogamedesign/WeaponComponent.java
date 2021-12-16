package com.MattiaDiMeglio.progettogamedesign;

import android.util.Log;

abstract class WeaponComponent extends Component{

    int mag; //massimo numero di proiettili
    int bullets; //attuale numero di proiettili
    float range; //portata dell'arma

    public abstract void shoot();
    public abstract void reload();
    public abstract float getRange();

    @Override
    public ComponentType getType() {
        return ComponentType.Weapon;
    }

}
