package com.MattiaDiMeglio.progettogamedesign;

//Support class for movementStack (AIComponent)

public class Movement {

    int cellX, cellY; // cell coordinates

    public Movement(int x, int y){
        this.cellX = x;
        this.cellY = y;
    }

    public int getCellX() { return cellX; }
    public int getCellY() { return cellY; }

}
