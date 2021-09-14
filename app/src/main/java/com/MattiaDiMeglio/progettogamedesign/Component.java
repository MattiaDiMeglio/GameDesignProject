package com.MattiaDiMeglio.progettogamedesign;

enum ComponentType {Physics, Joint, AI, Drawable, Controllable, Health, Weapon}
//the component class for the entity component methods
abstract class Component {
    protected GameObject owner;

    public abstract ComponentType getType();
    public void setOwner(GameObject owner){
        this.owner = owner;
    };
    public GameObject getOwner(){return owner;}
}
