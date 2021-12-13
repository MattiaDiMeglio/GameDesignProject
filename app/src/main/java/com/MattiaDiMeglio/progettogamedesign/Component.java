package com.MattiaDiMeglio.progettogamedesign;

enum ComponentType {Physics, Joint, AI, Drawable, Controllable, Weapon}
//the component class for the entity component methods
abstract class Component {
    protected GameObject owner;

    public abstract ComponentType getType();//all component need to return their type
    public void setOwner(GameObject owner){
        this.owner = owner;
    }// all component have an owner
    public GameObject getOwner(){return owner;}//return the owner
}
