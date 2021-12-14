package com.MattiaDiMeglio.progettogamedesign;

abstract class WeaponComponent extends Component{

    int mag; //massimo numero di proiettili
    int bullets; //attuale numero di proiettili
    int range; //portata dell'arma

    public void shoot(){
        bullets--;
    }

    public void reload(){
        bullets = mag;
    }

    @Override
    public ComponentType getType() {
        return ComponentType.Weapon;
    }

}
