package com.MattiaDiMeglio.progettogamedesign;

import android.util.Log;

import java.util.List;

public class GridManager {

    private int gridWidth;
    private int gridHeight;
    private int gridSize; // cell size
    private Node[][] cells;

    public GridManager(int levelWidth, int levelHeight, int gridS){

        this.gridSize = gridS;
        this.gridWidth = levelWidth / gridSize;
        this.gridHeight = levelHeight / gridSize;
        cells = new Node[gridWidth][gridHeight];

        computeNodePositions();
    }

    public void computeNodePositions(){
        int halfGridSize = gridSize / 2;
        int h = 0;
        for(int i = 0; i < gridWidth ; i++){ // indice che gestisce le worldY
            for(int j = 0 ; j < gridHeight ; j++){ // indice che gestisce le worldX
                Node n = new Node(h,halfGridSize + (j * gridSize),
                        halfGridSize + (i * gridSize), false,false, false);

                cells[i][j] = n;
            }
        }
    }

    public void addObstacles(List<GameObject> gameObjects, GameWorld gameWorld){
        for(GameObject go : gameObjects){
            if(go.name.equals("Wall") || go.name.equals("HalfWall") || go.name.equals("Box") || go.name.equals("Enemy"))
                addSingleObstacle(go, gameWorld);
        }
        computeNeighbors(); // dopo aver aggiunto gli ostacoli, calcoliamo i vicini dei nodi
    }

    public void addSingleObstacle(GameObject obstacle, GameWorld gameWorld){

        //PhysicsComponent physicsComponent = (PhysicsComponent) obstacle.getComponent(ComponentType.Physics);

        int obstacleX = obstacle.worldX;
        int obstacleY = obstacle.worldY;

        int gridObstacleX = obstacleX / gridSize;
        int gridObstacleY = obstacleY / gridSize;

        //int wallWeight = 10000;

        switch (obstacle.name){
            case "Box":
                cells[gridObstacleY][gridObstacleX].setBox(true);
                break;
            case "Enemy":
                cells[gridObstacleY][gridObstacleX].setEnemy(true);
                break;
            default: //Wall - HalfWall
                cells[gridObstacleY][gridObstacleX].setObstacle(true);
                break;
        }
    }

    public void computeNeighbors(){

        int edgeWeight = 1;
        float obstacleNeighborWeight = 1000f; //h da assegnare ai nodi ad una cella di distanza dai muri

        //Calcoliamo i vicini di tutti i nodi, tranne quelli appartenenti al bordo,
        //poiché si presume che tutte le mappe abbiano sempre i muri perimetrali

        for(int i = 1; i < gridWidth - 1 ; i++){
            for(int j = 1; j < gridHeight -1 ; j++){

                // il nodo del quale vogliamo calcolare i vicini
                // non deve contenere un muro
                if(!(cells[i][j].isObstacle())){

                    //stesso discorso per ognuno dei vicini
                    // N = North, S = South, W = West, E = East

                    if(!(cells[i-1][j-1].isObstacle())) //NW Neighbor
                        cells[i][j].addBranch(edgeWeight, cells[i-1][j-1]);
                    else if(cells[i-1][j-1].isObstacle() && !(cells[i][j].h == obstacleNeighborWeight))
                        cells[i][j].h = obstacleNeighborWeight;

                    if(!(cells[i-1][j].isObstacle())) //N Neighbor
                        cells[i][j].addBranch(edgeWeight, cells[i-1][j]);
                    else if(cells[i-1][j].isObstacle() && !(cells[i][j].h == obstacleNeighborWeight))
                        cells[i][j].h = obstacleNeighborWeight;

                    if(!(cells[i-1][j+1].isObstacle() )) //NE Neighbor
                        cells[i][j].addBranch(edgeWeight, cells[i-1][j+1]);
                    else if(cells[i-1][j+1].isObstacle() && !(cells[i][j].h == obstacleNeighborWeight))
                        cells[i][j].h = obstacleNeighborWeight;

                    if(!(cells[i][j-1].isObstacle() )) //W Neighbor
                        cells[i][j].addBranch(edgeWeight, cells[i][j-1]);
                    else if(cells[i][j-1].isObstacle() && !(cells[i][j].h == obstacleNeighborWeight))
                        cells[i][j].h = obstacleNeighborWeight;

                    if(!(cells[i][j+1].isObstacle() )) //E Neighbor
                        cells[i][j].addBranch(edgeWeight, cells[i][j+1]);
                    else if(cells[i][j+1].isObstacle() && !(cells[i][j].h == obstacleNeighborWeight))
                        cells[i][j].h = obstacleNeighborWeight;

                    if(!(cells[i+1][j-1].isObstacle() )) //SW Neighbor
                        cells[i][j].addBranch(edgeWeight, cells[i+1][j-1]);
                    else if(cells[i+1][j-1].isObstacle() && !(cells[i][j].h == obstacleNeighborWeight))
                        cells[i][j].h = obstacleNeighborWeight;

                    if(!(cells[i+1][j].isObstacle() )) //S Neighbor
                        cells[i][j].addBranch(edgeWeight, cells[i+1][j]);
                    else if(cells[i+1][j].isObstacle() && !(cells[i][j].h == obstacleNeighborWeight))
                        cells[i][j].h = obstacleNeighborWeight;

                    if(!(cells[i+1][j+1].isObstacle() )) //SE Neighbor
                        cells[i][j].addBranch(edgeWeight, cells[i+1][j+1]);
                    else if(cells[i+1][j+1].isObstacle() && !(cells[i][j].h == obstacleNeighborWeight))
                        cells[i][j].h = obstacleNeighborWeight;

                }
            }
        }
    }

    public void removeBox(int boxX, int boxY){
        int gridBoxX = boxX / gridSize;
        int gridBoxY = boxY / gridSize;
        cells[gridBoxY][gridBoxX].setBox(false);
        //computeNodeNeighbor(gridObstacleY, gridObstacleX);
    }

    public void computeNodeNeighbor(int x, int y){

        int edgeWeight = 1;

        // N = North, S = South, W = West, E = East

        if(!(cells[x-1][y-1].isObstacle() || cells[x-1][y-1].isBox())){//NW Neighbor
            cells[x][y].addBranch(edgeWeight, cells[x-1][y-1]);
            cells[x-1][y-1].addBranch(edgeWeight, cells[x][y]);
        }

        if(!(cells[x-1][y].isObstacle() || cells[x-1][y].isBox())){//N Neighbor
            cells[x][y].addBranch(edgeWeight, cells[x-1][y]);
            cells[x-1][y].addBranch(edgeWeight, cells[x][y]);
        }

        if(!(cells[x+1][y+1].isObstacle() || cells[x+1][y+1].isBox())){//NE Neighbor
            cells[x][y].addBranch(edgeWeight, cells[x+1][y+1]);
            cells[x+1][y+1].addBranch(edgeWeight, cells[x][y]);
        }

        if(!(cells[x][y-1].isObstacle() || cells[x][y-1].isBox())){//W Neighbor
            cells[x][y].addBranch(edgeWeight, cells[x][y-1]);
            cells[x][y-1].addBranch(edgeWeight, cells[x][y]);
        }

        if(!(cells[x][y+1].isObstacle() || cells[x][y+1].isBox())){//E Neighbor
            cells[x][y].addBranch(edgeWeight, cells[x][y+1]);
            cells[x][y+1].addBranch(edgeWeight, cells[x][y]);
        }

        if(!(cells[x+1][y-1].isObstacle() || cells[x+1][y-1].isBox())){//SW Neighbor
            cells[x][y].addBranch(edgeWeight, cells[x+1][y-1]);
            cells[x+1][y-1].addBranch(edgeWeight, cells[x][y]);
        }

        if(!(cells[x+1][y].isObstacle() || cells[x+1][y].isBox())){//S Neighbor
            cells[x][y].addBranch(edgeWeight, cells[x+1][y]);
            cells[x+1][y].addBranch(edgeWeight, cells[x][y]);
        }

        if(!(cells[x+1][y+1].isObstacle() || cells[x+1][y+1].isBox())){//SE Neighbor
            cells[x][y].addBranch(edgeWeight, cells[x+1][y+1]);
            cells[x+1][y+1].addBranch(edgeWeight, cells[x][y]);
        }

    }

    public Node[][] getCells() { return cells; }

    public int getGridWidth() {return gridWidth;}
    public int getGridHeight() {return gridHeight;}

}
