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

    public void computeNodePositions(){
        int halfGridSize = gridSize / 2;
        int h = 0;
        for(int i = 0; i < gridWidth ; i++){ // indice che gestisce le worldY
            for(int j = 0 ; j < gridHeight ; j++){ // indice che gestisce le worldX
                Node n = new Node(h,halfGridSize + (j * gridSize),
                        halfGridSize + (i * gridSize), false);

                cells[i][j] = n;
            }
        }
    }

    public void addObstacles(List<GameObject> gameObjects, GameWorld gameWorld){
        for(GameObject go : gameObjects){
            if(go.name.equals("Wall") || go.name.equals("HalfWall") || go.name.equals("Box"))
                addSingleObstacle(go, gameWorld);
        }
        computeNeighbors(); // dopo aver aggiunto gli ostacoli, calcoliamo i vicini dei nodi
    }

    public void addSingleObstacle(GameObject obstacle, GameWorld gameWorld){

        PhysicsComponent physicsComponent = (PhysicsComponent) obstacle.getComponent(ComponentType.Physics);

        float obstacleX = obstacle.worldX;
        float obstacleY = obstacle.worldY;
        float obstacleWidth = gameWorld.toPixelsXLength(physicsComponent.getWidth());
        float obstacleHeight = gameWorld.toPixelsYLength(physicsComponent.getHeight());

        /*Log.i("addSingleObstacle","Nome GO: "+obstacle.name);
        Log.i("addSingleObstacle","Coordinate: ("+obstacleX+","+obstacleY+")");
        Log.i("addSingleObstacle","Dimensioni: "+obstacleWidth+"x"+obstacleHeight);*/

        float gridObstacleX = (float) (obstacleX / gridSize); // position in grid
        float gridObstacleY = (float) (obstacleY / gridSize);
        float gridObstacleWidth = (obstacleWidth / gridSize); // size in grid
        float gridObstacleHeight = (obstacleHeight / gridSize);

        /*worldX e worldY dell'oggetto si riferiscono al centro dello stesso,
        quindi per riempire le celle della griglia col ciclo for sottostante,
        bisogna partire non dal centro, bensì dall'estremo dell'oggetto,
        sia per le X che per le Y*/

        float startX = gridObstacleX - (gridObstacleWidth/2);
        float startY = gridObstacleY - (gridObstacleHeight/2);

        /*Log.i("addSingleObstacle","gridObstacleX = "+gridObstacleX);
        Log.i("addSingleObstacle","gridObstacleY = "+gridObstacleY);
        Log.i("addSingleObstacle","gridObstacleWidth = "+gridObstacleWidth);
        Log.i("addSingleObstacle","gridObstacleHeight = "+gridObstacleHeight);
        Log.i("addSingleObstacle","startX = "+startX);
        Log.i("addSingleObstacle","startY = "+startY);*/

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

        /*Log.i("addSingleObstacle","Fixed gridObstacleWidth = "+gridObstacleWidth);
        Log.i("addSingleObstacle","Fixed gridObstacleHeight = "+gridObstacleHeight);
        Log.i("addSingleObstacle","Fixed startX = "+startX);
        Log.i("addSingleObstacle","Fixed startY = "+startY);*/

        int wallWeight = 10000;

        for (int i = 0; i < gridObstacleHeight; i++) {
            for (int j = 0; j < gridObstacleWidth; j++) {
                cells[(int) (startY + i)][(int) (startX + j)].setObstacle(true);
                if (!obstacle.name.equals("Box"))
                    cells[(int) (startY + i)][(int) (startX + j)].h = wallWeight;
            }
        }
    }

    public void computeNeighbors(){

        int edgeWeight = 1;
        float wallNeighborWeight = 1000f; //h da assegnare ai nodi ad una cella di distanza dai muri
        float wallSecondNeighborWeight = 600f; //h da assegnare ai nodi a 2 celle di distanza dai muri

        //Calcoliamo i vicini di tutti i nodi, tranne quelli appartenenti al bordo,
        //poiché si presume che tutte le mappe abbiano sempre i muri perimetrali

        for(int i = 1; i < gridWidth - 1 ; i++){
            for(int j = 1; j < gridHeight -1 ; j++){

                //Log.i("GridManager","Iterazione ["+i+"]["+j+"]");

                // il nodo del quale vogliamo calcolare i vicini
                // non deve contenere un muro
                if(!(cells[i][j].isObstacle())){

                    //stesso discorso per ognuno dei vicini
                    // N = North, S = South, W = West, E = East

                    if(!(cells[i-1][j-1].isObstacle())) //NW Neighbor
                        cells[i][j].addBranch(edgeWeight, cells[i-1][j-1]);
                    else cells[i][j].h = wallNeighborWeight;

                    if(!(cells[i-1][j].isObstacle())) //N Neighbor
                        cells[i][j].addBranch(edgeWeight, cells[i-1][j]);
                    else cells[i][j].h = wallNeighborWeight;

                    if(!(cells[i-1][j+1].isObstacle())) //NE Neighbor
                        cells[i][j].addBranch(edgeWeight, cells[i-1][j+1]);
                    else cells[i][j].h = wallNeighborWeight;

                    if(!(cells[i][j-1].isObstacle())) //W Neighbor
                        cells[i][j].addBranch(edgeWeight, cells[i][j-1]);
                    else cells[i][j].h = wallNeighborWeight;

                    if(!(cells[i][j+1].isObstacle())) //E Neighbor
                        cells[i][j].addBranch(edgeWeight, cells[i][j+1]);
                    else cells[i][j].h = wallNeighborWeight;

                    if(!(cells[i+1][j-1].isObstacle())) //SW Neighbor
                        cells[i][j].addBranch(edgeWeight, cells[i+1][j-1]);
                    else cells[i][j].h = wallNeighborWeight;

                    if(!(cells[i+1][j].isObstacle())) //S Neighbor
                        cells[i][j].addBranch(edgeWeight, cells[i+1][j]);
                    else cells[i][j].h = wallNeighborWeight;

                    if(!(cells[i+1][j+1].isObstacle())) //SE Neighbor
                        cells[i][j].addBranch(edgeWeight, cells[i+1][j+1]);
                    else cells[i][j].h = wallNeighborWeight;

                    //se il nodo non è il vicino di un muro, controlliamo se è a 2 celle di distanza
                    //da un muro
                    /*if(cells[i][j].h != wallNeighborWeight && !((i-2)<0) && !((j-2)<0)
                            && ((i+2)<gridWidth) && ((j+2)<gridHeight)){

                        if(cells[i-2][j-2].isWall()) //NW Second Neighbor
                            cells[i][j].h = wallSecondNeighborWeight;

                        else if(cells[i-2][j].isWall()) //N Second Neighbor
                            cells[i][j].h = wallSecondNeighborWeight;

                        else if(cells[i-2][j+2].isWall()) //NE Second Neighbor
                            cells[i][j].h = wallSecondNeighborWeight;

                        else if(cells[i][j-2].isWall()) //W Second Neighbor
                            cells[i][j].h = wallSecondNeighborWeight;

                        else if(cells[i][j+2].isWall()) //E Second Neighbor
                            cells[i][j].h = wallSecondNeighborWeight;

                        else if(cells[i+2][j-2].isWall()) //SW Second Neighbor
                            cells[i][j].h = wallSecondNeighborWeight;

                        else if(cells[i+2][j].isWall()) //S Second Neighbor
                            cells[i][j].h = wallSecondNeighborWeight;

                        else if(cells[i+2][j+2].isWall()) //SE Second Neighbor
                            cells[i][j].h = wallSecondNeighborWeight;

                    }*/
                }
            }
        }
    }

    public void removeObstacle(int obstacleX, int obstacleY){
        int gridObstacleX = (obstacleX - (gridSize / 2)) / gridSize;
        int gridObstacleY = (obstacleY - (gridSize / 2)) / gridSize;
        cells[gridObstacleY][gridObstacleX].setObstacle(false);
        int nodeX = cells[gridObstacleY][gridObstacleX].getPosX();
        int nodeY = cells[gridObstacleY][gridObstacleX].getPosY();
        Log.d("GridManager","Box is not an obstacle, [j][i] =  ["+gridObstacleY+"]["+gridObstacleX+"]");
        Log.d("GridManager","Node XY =  ["+nodeX+"]["+nodeY+"]");
        computeNodeNeighbor(gridObstacleY, gridObstacleX);
    }

    public void computeNodeNeighbor(int y, int x){

        int nodeX = cells[x][y].getPosX();
        int nodeY = cells[x][y].getPosY();

        int edgeWeight = 1;
        float wallNeighborWeight = 1000f;

        int nodeXNW = cells[x-1][y-1].getPosX();
        int nodeYNW = cells[x-1][y-1].getPosY();

        Log.d("computeNodeNeighbor","Node XY =  ["+nodeX+"]["+nodeY+"]");
        Log.d("computeNodeNeighbor","Node NW XY =  ["+nodeXNW+"]["+nodeYNW+"]");

        if(!(cells[x-1][y-1].isObstacle())) //NW Neighbor
            cells[x][y].addBranch(edgeWeight, cells[x-1][y-1]);
        else cells[x][y].h = wallNeighborWeight;

        if(!(cells[x-1][y].isObstacle())) //N Neighbor
            cells[x][y].addBranch(edgeWeight, cells[x-1][y]);
        else cells[x][y].h = wallNeighborWeight;

        if(!(cells[x-1][y+1].isObstacle())) //NE Neighbor
            cells[x][y].addBranch(edgeWeight, cells[x-1][y+1]);
        else cells[x][y].h = wallNeighborWeight;

        if(!(cells[x][y-1].isObstacle())) //W Neighbor
            cells[x][y].addBranch(edgeWeight, cells[x][y-1]);
        else cells[x][y].h = wallNeighborWeight;

        if(!(cells[x][y+1].isObstacle())) //E Neighbor
            cells[x][y].addBranch(edgeWeight, cells[x][y+1]);
        else cells[x][y].h = wallNeighborWeight;

        if(!(cells[x+1][y-1].isObstacle())) //SW Neighbor
            cells[x][y].addBranch(edgeWeight, cells[x+1][y-1]);
        else cells[x][y].h = wallNeighborWeight;

        if(!(cells[x+1][y].isObstacle())) //S Neighbor
            cells[x][y].addBranch(edgeWeight, cells[x+1][y]);
        else cells[x][y].h = wallNeighborWeight;

        if(!(cells[x+1][y+1].isObstacle())) //SE Neighbor
            cells[x][y].addBranch(edgeWeight, cells[x+1][y+1]);
        else cells[x][y].h = wallNeighborWeight;
    }

    public Node[][] getCells() { return cells; }

    public int getGridWidth() {return gridWidth;}
    public int getGridHeight() {return gridHeight;}

}
