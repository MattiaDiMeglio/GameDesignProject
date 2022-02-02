package com.MattiaDiMeglio.progettogamedesign;

public class WimpAI extends AIComponent{

    WimpAI() {
        super();
        aimDelay = 0.7f;
        shootDelay = 1.2f;
        reloadDelay = 0.5f;
    }

    public void updateAI(int playerX, int playerY, float elapsedTime, Node[][] cells, GameWorld gameWorld){
        super.updateAI(playerX,playerY,elapsedTime,cells, gameWorld);
    }

}
