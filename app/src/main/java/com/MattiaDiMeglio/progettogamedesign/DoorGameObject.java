package com.MattiaDiMeglio.progettogamedesign;

public class DoorGameObject extends GameObject {
    /*objects different from the player, will have "world coordinates"
    * that represent the point of the background in which they are.
    * depending on that the object will be drawn or not*/
    float worldX,  worldY;

    public DoorGameObject(float worldX, float worldY){
        this.worldX = worldX;
        this.worldY = worldY;
    }

    public void applyForce(float force){}
}
