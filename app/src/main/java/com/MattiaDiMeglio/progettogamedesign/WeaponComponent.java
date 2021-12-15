package com.MattiaDiMeglio.progettogamedesign;

import android.util.Log;

abstract class WeaponComponent extends Component{

    int mag; //massimo numero di proiettili
    int bullets; //attuale numero di proiettili
    float range; //portata dell'arma

    public void shoot(){
        Log.d("WeaponComponent","bullets =" +bullets);
        Log.d("WeaponComponent","shooting");
        bullets--;

        if(bullets == 0)
            reload();
    }

    public void reload(){
        Log.d("WeaponComponent","reloading");
        bullets = mag;
    }

    public float getRange() {return range;}

    @Override
    public ComponentType getType() {
        return ComponentType.Weapon;
    }

}
