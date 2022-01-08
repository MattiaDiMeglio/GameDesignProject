package com.MattiaDiMeglio.progettogamedesign;

/*
Se il path da seguire è composto da 5 nodi, ad esempio A-B-C-D-E,
dovrò effettuare 4 movimenti: AB-BC-CD-DE
normalX e normalY contengono i vettori,
cellX e cellY le coordinate della cella d'arrivo (B-C-D-E)
 */

public class Movement {

    private int cellX, cellY; // coordinate della cella
    private int vectorX, vectorY; // componenti dei vettori del movimento

    public Movement(int x, int y, int normX, int normY){
        this.cellX = x;
        this.cellY = y;
        this.vectorX = normX;
        this.vectorY = normY;
    }

    public int getCellX() { return cellX; }
    public int getCellY() { return cellY; }
    public int getVectorX() { return vectorX; }
    public int getVectorY() { return vectorY; }

}
