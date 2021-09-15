package com.MattiaDiMeglio.progettogamedesign;

public class DoorGameObject extends GameObject {
    /*objects different from the player, will have "world coordinates"
    * that represent the point of the background in which they are.
    * depending on that the object will be drawn or not*/
    WallGameObject wall;

    public DoorGameObject(int worldX, int worldY, WallGameObject wall){
        this.worldX = worldX;
        this.worldY = worldY;
        this.wall = wall;
        this.name = "Door";
    }

    public void applyForce(float force){}
}
