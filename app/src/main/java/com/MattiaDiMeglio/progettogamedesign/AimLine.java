package com.MattiaDiMeglio.progettogamedesign;

import com.google.fpl.liquidfun.Color;

public class AimLine {

    public int startX, startY, targetX, targetY;
    public int color;

    public AimLine(int sx, int sy, int tx, int ty, int color){
        this.startX = sx;
        this.startY = sy;
        this.targetX = tx;
        this.targetY = ty;
        this.color = color;
    }

}
