package com.MattiaDiMeglio.progettogamedesign;

public class DummyAI extends AIComponent{

    DummyAI(AIType aiType){
        super(aiType);
    }

    public void updateAI(int playerX, int playerY, float elapsedTime, int gridSize, Node[][] cells){

        int lastPlayerX = getPlayerX();
        int lastPlayerY = getPlayerY();

        super.updateAI(playerX,playerY,elapsedTime,gridSize,cells);

        /*if(getPlayerX() != lastPlayerX || getPlayerY() != lastPlayerY)
            pathfind(getPlayerX(),getPlayerY(),gridSize,cells);*/
    }

}
