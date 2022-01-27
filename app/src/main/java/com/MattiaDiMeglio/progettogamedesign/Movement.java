package com.MattiaDiMeglio.progettogamedesign;

public class Movement {

    int cellX, cellY; // coordinate della cella

    public Movement(int x, int y){
        this.cellX = x;
        this.cellY = y;
    }

    public int getCellX() { return cellX; }
    public int getCellY() { return cellY; }

}
