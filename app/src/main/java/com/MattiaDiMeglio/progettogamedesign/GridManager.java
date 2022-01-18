package com.MattiaDiMeglio.progettogamedesign;

import android.util.Log;

import java.util.List;

public class GridManager {

    private int gridWidth;
    private int gridHeight;
    private int gridSize; // cell size
    private Node[][] cells;

    public GridManager(int levelWidth, int levelHeight, int gridS,GameWorld gameWorld){

        this.gridSize = gridS;
        this.gridWidth = levelWidth / gridSize;
        this.gridHeight = levelHeight / gridSize;
        cells = new Node[gridWidth][gridHeight];

        computeNodePositions();
    }

    /* Funzione che assegna ad ogni cella le relative coordinate, corrispondenti al centro,
       inoltre setta il booleano isWalkable a true e isWall a false,
       essi saranno modificati in seguito aggiungendo gli ostacoli (addObstacles).

       Per calcolare il centro della cella, considerando che ogni cella è 4x4:

       -La prima cella in alto a sx avrà centro pari a (2,2)
       -Quella alla sua destra (6,2)
       -Scorrendo sempre a destra (10,2), (14,2)... (halfGridSize + index * gridSize, 2)
       -Stesso ragionamento per le Y
     */

    public void computeNodePositions(){
        int halfGridSize = gridSize / 2;
        int h = 0;
        for(int i = 0; i < gridWidth ; i++){ // indice che gestisce le worldY
            for(int j = 0 ; j < gridHeight ; j++){ // indice che gestisce le worldX
                Node n = new Node(h,halfGridSize + (j * gridSize),
                        halfGridSize + (i * gridSize), true, false);

                cells[i][j] = n;
            }
        }
    }

    public void addObstacles(List<GameObject> gameObjects, GameWorld gameWorld){
        for(GameObject go : gameObjects){
            if(go.name.equals("Wall") || go.name.equals("HalfWall"))
                addSingleObstacle(go, gameWorld);
        }
        computeNeighbors(); // dopo aver aggiunto gli ostacoli, calcoliamo i vicini dei nodi
    }

    public void addSingleObstacle(GameObject obstacle, GameWorld gameWorld){

        PhysicsComponent physicsComponent = (PhysicsComponent) obstacle.getComponent(ComponentType.Physics);

        int obstacleX = obstacle.worldX;
        int obstacleY = obstacle.worldY;
        float obstacleWidth = gameWorld.toPixelsXLength(physicsComponent.getWidth());
        float obstacleHeight = gameWorld.toPixelsYLength(physicsComponent.getHeight());

        /*Log.i("addSingleObstacle","Nome GO: "+obstacle.name);
        Log.i("addSingleObstacle","Coordinate: ("+obstacleX+","+obstacleY+")");
        Log.i("addSingleObstacle","Dimensioni: "+obstacleWidth+"x"+obstacleHeight);*/

        float gridObstacleX = obstacleX / gridSize; // position in grid
        float gridObstacleY = obstacleY / gridSize;
        float gridObstacleWidth = (obstacleWidth / gridSize) +12; // size in grid
        float gridObstacleHeight = (obstacleHeight / gridSize) +12;

        /*worldX e worldY dell'oggetto si riferiscono al centro dello stesso,
        quindi per riempire le celle della griglia col ciclo for sottostante,
        bisogna partire non dal centro, bensì dall'estremo dell'oggetto,
        sia per le X che per le Y*/

        float startX = gridObstacleX - (gridObstacleWidth/2);
        float startY = gridObstacleY - (gridObstacleHeight/2);

        if(startX < 0)
            startX = 0;

        if(startY < 0 )
            startY = 0;

        //i 2 if servono per evitare che gli oggetti perimetrali
        //"sforino" la griglia

        if((gridObstacleHeight + startY) > gridWidth)
            gridObstacleHeight = gridWidth - startY;

        if((gridObstacleWidth + startX) > gridHeight)
            gridObstacleWidth = gridHeight - startX;

        /*Log.i("addSingleObstacle","gridObstacleX = "+gridObstacleX);
        Log.i("addSingleObstacle","gridObstacleY = "+gridObstacleY);
        Log.i("addSingleObstacle","gridObstacleWidth = "+gridObstacleWidth);
        Log.i("addSingleObstacle","gridObstacleHeight = "+gridObstacleHeight);
        Log.i("addSingleObstacle","startX = "+startX);
        Log.i("addSingleObstacle","startY = "+startY);*/

        for (int i = 0; i < gridObstacleHeight; i++) {
            for (int j = 0; j < gridObstacleWidth; j++) {
                cells[(int) (startY + i)][(int) (startX + j)].setWalkable(false);
                cells[(int) (startY + i)][(int) (startX + j)].setWall(true);
                //cells[(int) (startY + i)][(int) (startX + j)].h = 10000;
            }
        }

    }

    public void computeNeighbors(){

        //Calcoliamo i vicini di tutti i nodi, tranne quelli appartenenti al bordo,
        //poiché si presume che tutte le mappe abbiano sempre i muri perimetrali

        int weight = 1;
        /*int wallWeight = 100000;
        int wallFirstNeighborWeight = 1000;
        int wallSecondNeighborWeight = 600;*/

        for(int i = 1; i < gridWidth - 1 ; i++){
            for(int j = 1; j < gridHeight -1 ; j++){
                // il nodo del quale vogliamo calcolare i vicini
                // non deve contenere un muro
                if(!(cells[i][j].isWall()) && (cells[i][j].isWalkable())){

                    //stesso discorso per ognuno dei vicini
                    // N = North, S = South, W = West, E = East

                    if(!(cells[i-1][j-1].isWall()) && (cells[i-1][j-1].isWalkable())) //NW Neighbor
                        cells[i][j].addBranch(weight, cells[i-1][j-1]);

                    if(!(cells[i-1][j].isWall()) && (cells[i-1][j].isWalkable())) //N Neighbor
                        cells[i][j].addBranch(weight, cells[i-1][j]);

                    if(!(cells[i-1][j+1].isWall()) && (cells[i-1][j+1].isWalkable())) //NE Neighbor
                        cells[i][j].addBranch(weight, cells[i-1][j+1]);

                    if(!(cells[i][j-1].isWall()) && (cells[i][j-1].isWalkable())) //W Neighbor
                        cells[i][j].addBranch(weight, cells[i][j-1]);

                    if(!(cells[i][j+1].isWall()) && (cells[i][j+1].isWalkable())) //E Neighbor
                        cells[i][j].addBranch(weight, cells[i][j+1]);

                    if(!(cells[i+1][j-1].isWall()) && (cells[i+1][j-1].isWalkable())) //SW Neighbor
                        cells[i][j].addBranch(weight, cells[i+1][j-1]);

                    if(!(cells[i+1][j].isWall()) && (cells[i+1][j].isWalkable())) //S Neighbor
                        cells[i][j].addBranch(weight, cells[i+1][j]);

                    if(!(cells[i+1][j+1].isWall()) && (cells[i+1][j+1].isWalkable())) //SE Neighbor
                        cells[i][j].addBranch(weight, cells[i+1][j+1]);

                    //NW Neighbors
                    /*if(cells[i-1][j-1].isWall())//NW Wall Neighbor
                        cells[i][j].addBranch(wallWeight, cells[i-1][j-1]);
                    else if(cells[i-2][j-2].isWall()) //NW First Neighbor
                        cells[i][j].addBranch(wallFirstNeighborWeight, cells[i-1][j-1]);
                    else if(cells[i-3][j-3].isWall()) //NW Second Neighbor
                        cells[i][j].addBranch(wallSecondNeighborWeight,cells[i-1][j-1]);
                    else cells[i][j].addBranch(weight,cells[i-1][j-1]);

                    //North Neighbors
                    if(cells[i-1][j].isWall())
                        cells[i][j].addBranch(wallWeight, cells[i-1][j]);
                    else if(cells[i-2][j].isWall())
                        cells[i][j].addBranch(wallFirstNeighborWeight, cells[i-1][j]);
                    else if(cells[i-3][j].isWall())
                        cells[i][j].addBranch(wallSecondNeighborWeight,cells[i-1][j]);
                    else cells[i][j].addBranch(weight,cells[i-1][j]);

                    //NE Neighbors
                    if(cells[i-1][j+1].isWall())
                        cells[i][j].addBranch(wallWeight, cells[i-1][j+1]);
                    else if(cells[i-2][j+2].isWall())
                        cells[i][j].addBranch(wallFirstNeighborWeight, cells[i-1][j+1]);
                    else if(cells[i-3][j+3].isWall())
                        cells[i][j].addBranch(wallSecondNeighborWeight,cells[i-1][j+1]);
                    else cells[i][j].addBranch(weight,cells[i-1][j+1]);

                    //West Neighbors
                    if(cells[i][j-1].isWall())
                        cells[i][j].addBranch(wallWeight, cells[i][j-1]);
                    else if(cells[i][j-2].isWall())
                        cells[i][j].addBranch(wallFirstNeighborWeight, cells[i][j-1]);
                    else if(cells[i][j-3].isWall())
                        cells[i][j].addBranch(wallSecondNeighborWeight,cells[i][j-1]);
                    else cells[i][j].addBranch(weight,cells[i][j-1]);

                    //East Neighbors
                    if(cells[i][j+1].isWall())
                        cells[i][j].addBranch(wallWeight, cells[i][j+1]);
                    else if(cells[i][j+2].isWall())
                        cells[i][j].addBranch(wallFirstNeighborWeight, cells[i][j+1]);
                    else if(cells[i][j+3].isWall())
                        cells[i][j].addBranch(wallSecondNeighborWeight,cells[i][j+1]);
                    else cells[i][j].addBranch(weight,cells[i][j+1]);

                    //SW Neighbors
                    if(cells[i+1][j-1].isWall())
                        cells[i][j].addBranch(wallWeight, cells[i+1][j-1]);
                    else if(cells[i+2][j-2].isWall())
                        cells[i][j].addBranch(wallFirstNeighborWeight, cells[i+1][j-1]);
                    else if(cells[i+3][j-3].isWall())
                        cells[i][j].addBranch(wallSecondNeighborWeight,cells[i+1][j-1]);
                    else cells[i][j].addBranch(weight,cells[i+1][j-1]);

                    //South Neighbors
                    if(cells[i+1][j].isWall())
                        cells[i][j].addBranch(wallWeight, cells[i+1][j]);
                    else if(cells[i+2][j].isWall())
                        cells[i][j].addBranch(wallFirstNeighborWeight, cells[i+1][j]);
                    else if(cells[i+3][j].isWall())
                        cells[i][j].addBranch(wallSecondNeighborWeight,cells[i+1][j]);
                    else cells[i][j].addBranch(weight,cells[i+1][j]);

                    //SE Neighbors
                    if(cells[i+1][j+1].isWall())
                        cells[i][j].addBranch(wallWeight, cells[i+1][j+1]);
                    else if(cells[i+2][j+2].isWall())
                        cells[i][j].addBranch(wallFirstNeighborWeight, cells[i+1][j+1]);
                    else if(cells[i+3][j+3].isWall())
                        cells[i][j].addBranch(wallSecondNeighborWeight,cells[i+1][j+1]);
                    else cells[i][j].addBranch(weight,cells[i+1][j+1]);*/
                }
            }
        }
    }

    public Node[][] getCells() { return cells; }

    public int getGridWidth() {return gridWidth;}
    public int getGridHeight() {return gridHeight;}

}
