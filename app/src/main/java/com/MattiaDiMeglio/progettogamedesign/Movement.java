package com.MattiaDiMeglio.progettogamedesign;

/*
Se il path da seguire è composto da 5 nodi, ad esempio A-B-C-D-E,
dovrò effettuare 4 movimenti: AB-BC-CD-DE
normalX e normalY contengono i vettori,
cellX e cellY le coordinate della cella d'arrivo (B-C-D-E)
 */

public class Movement {

    private int cellX, cellY; // coordinate della cella

    public Movement(int x, int y){
        this.cellX = x;
        this.cellY = y;
    }

    public int getCellX() { return cellX; }
    public int getCellY() { return cellY; }

}
