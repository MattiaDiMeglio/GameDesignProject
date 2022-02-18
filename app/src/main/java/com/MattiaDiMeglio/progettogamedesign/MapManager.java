package com.MattiaDiMeglio.progettogamedesign;

import android.content.Context;
import android.util.Log;
//the map manager, calls the jsonparser and the factory to make the map
//legenda
//0 = cella vuota
//2 muro normale
//3 half wall orizzontale
//4 half wall verticale
//5 player
//6 enemy1
//7 enemy2
//8 enemy3
//9 ostacolo distruttibile
//10 ostacolo mobile
public class MapManager {

    private GameWorld gameWorld;
    private GameObjectFactory gameObjectFactory;
    private int mapWidth;
    private int mapHeight;
    Context context;


    public MapManager(GameWorld gameWorld, GameObjectFactory gameObjectFactory, Context context){
        this.gameWorld = gameWorld;
        this.gameObjectFactory = gameObjectFactory;
        this.context = context;
    }

    public int[][] initMapResized(int[][]map, int width, int height){
        map = new int[width][height];//init
        mapWidth = width;
        mapHeight = height;
        for(int i = 0; i<width; i++){ //settiamo tutto walkable
            for(int j = 0; j<height; j++){
                map[i][j] = 0;
            }
        }
        for(int i = 0; i<width; i++){
            map[i][0] = 2;
            map[i][width-1] = 2;
        }
        for(int i = 0; i<height; i++){
            map[0][i] = 2;
            map[height-1][i] = 2;
        }
        return map;
    }
    public void printMap(int[][]map){
        String s = "";
        Log.w("mapx", " ");
        Log.w("mapx", " ");
        for(int i = 0; i<50; i++){
            for(int j = 0; j<50; j++){
                s = s.concat((map[j][i]==2)? "|": (map[j][i]==3) ? "_" : (map[j][i]==4) ? "/" : (map[i][j]== 5)?"p" :
                        (map[i][j]==6)? "e" : ".");


            }
            Log.w("map" + (int) i%10, s);
            s = "";
        }
        Log.w("mapx", " ");
        Log.w("mapx", " ");
    }

    public int[][] generateMapResized(int[][] map, int startingX, int startingY, int endingX, int endingY, boolean vertical){
        int randomIndex = 0;
        int width = endingX - startingX;
        int heigth = endingY - startingY;
        if(vertical){
            randomIndex = (int)(Math.random() * (endingX - startingX - 20)) + (startingX + 10);
            int a = 0;
            for(int i = startingY; i<endingY+1; i++){
                for(int j = randomIndex-1; j<randomIndex+2; j++){
                  if(i != 0 && i != mapHeight-1)
                      map[j][i] = 0;
                }
                    if(i>startingY + 2 && i < endingY - 3) {
                        map[randomIndex - 2][i] = randomWall(1);
                        map[randomIndex + 2][i] = randomWall(1);
                    } else {
                        map[randomIndex - 2][i] = 2;
                        map[randomIndex + 2][i] = 2;
                    }
            }
            //creiamo una porta casuale per ognuna delle due partiin un muro random che non sia un bordo della mappa
            makeDoors(map, startingX, startingY, randomIndex-2, endingY);
            makeDoors(map, randomIndex+2, startingY, endingX, endingY);
            //printMap(map);

            if(heigth > 21){
                generateMapResized(map, startingX, startingY, randomIndex-2, endingY, !vertical);
                generateMapResized(map, randomIndex+2, startingY, endingX, endingY, !vertical);
            }
        } else {
            randomIndex = (int)(Math.random() * (endingY - startingY - 20)) + (startingY + 10);
            int a = 0;
            for(int i = startingX; i<endingX+1; i++){
                for(int j = randomIndex - 1; j < randomIndex + 2; j++){
                    if(i != 0 && i != mapWidth-1)
                        map[i][j] = 0;
                }
                    if(i > startingX+2 && i < endingX - 3) {
                        map[i][randomIndex - 2] = randomWall(0);
                        map[i][randomIndex + 2] = randomWall(0);
                    } else {
                        map[i][randomIndex - 2] = 2;
                        map[i][randomIndex + 2] = 2;
                    }
            }
            makeDoors(map, startingX, startingY, endingX, randomIndex-2);
            makeDoors(map, startingX, randomIndex+2, endingX, endingY);
            //printMap(map);
            if(width > 21){
                generateMapResized(map, startingX, startingY, endingX, randomIndex-2, !vertical);
                generateMapResized(map, startingX, randomIndex+2, endingX, endingY, !vertical);
            }

        }

        return map;
    }

    private int randomWall(int horizontal){
        double random = Math.random();
        if(0.40f <= random && random <= 0.5f)
            return 3 + horizontal;
        return 2;
    }

    public void generateEnemyPos(int[][]map){
        boolean enemyInPosition = false;
        int randomX = 0, randomY = 0;
        while(!enemyInPosition){
            randomX = (int)(Math.random() * (mapWidth-2)) + 1;
            randomY = (int)(Math.random() * (mapHeight-2)) + 1;
            if(map[randomX][randomY] == 0){
                int i = Math.max(randomX - 5, 1);
                int j = Math.max(randomY - 5, 1);
                boolean playerFound = false;
                while(i<randomX + 5 && i<mapWidth &&  !playerFound){
                    while(j <randomY+5 && j<mapHeight && !playerFound) {
                        if (map[i][j] >= 5) {
                            playerFound = true;
                        }
                        j++;
                    }
                    j = Math.max(randomY - 5, 1);
                    i++;
                }
                if(!playerFound){
                    int type = (int)((Math.random() * 10) % 3) + 6;
                    map[randomX][randomY] = type;
                    enemyInPosition = true;
                }
            }

        }
    }

    public void generateBoxPosition(int[][]map){
        boolean boxInPosition = false;
        int randomX = 0, randomY = 0;
        while(!boxInPosition){
            randomX = (int)(Math.random() * (mapWidth-2)) + 1;
            randomY = (int)(Math.random() * (mapHeight-2)) + 1;
            if(map[randomX][randomY] == 0){
                int i = Math.max(randomX - 2, 1);
                int j = Math.max(randomY - 2, 1);
                boolean playerFound = false;
                while(i<randomX + 2 && i<mapWidth &&  !playerFound){
                    while(j <randomY + 2 && j<mapHeight && !playerFound) {
                        if (map[i][j] >= 2) {
                            playerFound = true;
                        }
                        j++;
                    }
                    j = Math.max(randomY - 2, 1);
                    i++;
                }
                if(!playerFound){
                    map[randomX][randomY] = (int)((Math.random() * 10) % 2) + 9;
                    boxInPosition = true;
                }
            }
        }
    }

    public void constructMap(int[][]map, int width, int height){
        map[5][5] = 5;
        for(int i = 0; i< gameWorld.enemyNum; i++){
            generateEnemyPos(map);
        }
        for(int i = 0; i< 20; i++){
            generateBoxPosition(map);
        }
        printMap(map);
        for(int i = 0; i<width; i++){
            for(int j = 0; j<height; j++){
                switch (map[i][j]){
                    case 2:
                        gameWorld.addGameObject(gameObjectFactory.makeHorizontalWall(toActualCoord(i), toActualCoord(j)));
                        break;
                    case 3:
                        gameWorld.addGameObject(gameObjectFactory.makeHorizontalHalfWall(toActualCoord(i), toActualCoord(j)));
                        break;
                    case 4:
                        gameWorld.addGameObject(gameObjectFactory.makeVerticalHalfWall(toActualCoord(i), toActualCoord(j)));
                        break;
                    case 6:
                        gameWorld.addGameObject(gameObjectFactory.makeEnemy(toActualCoord(i), toActualCoord(j), AIType.Dummy));
                        break;
                    case 7:
                        gameWorld.addGameObject(gameObjectFactory.makeEnemy(toActualCoord(i), toActualCoord(j), AIType.Sniper));
                        break;
                    case 8:
                        gameWorld.addGameObject(gameObjectFactory.makeEnemy(toActualCoord(i), toActualCoord(j), AIType.Patrol));                        break;
                    case 9:
                        gameWorld.addGameObject(gameObjectFactory.makeBox(toActualCoord(i), toActualCoord(j)));
                        break;
                    case 10:
                        gameWorld.addGameObject(gameObjectFactory.makeMovableBox(toActualCoord(i), toActualCoord(j)));
                }
            }
        }

    }

    public int toActualCoord(int x){return (AssetManager.WallPixmap.getWidth()/2 + x * AssetManager.WallPixmap.getWidth());}

    public void makeDoors(int[][] map, int startingX, int startingY, int endingX, int endingY){
        //lato della stanza dove mettere la porta
        //se il lato ha indice 0 o mapwidth/mapheight, cerchiamo un'altro lato
        boolean doorMade = false;
        int doorPosition = 0;
        while(!doorMade) {
            int randomSide = (int) ((Math.random() * 100) % 4);
            switch (randomSide) {
                case 0://sx
                    if (startingX > 0){
                        doorPosition = (int)((Math.random() * (endingY - (startingY + 8))) + (startingY + 4));
                        map[startingX][doorPosition-1] = 0;
                        map[startingX][doorPosition] = 0;
                        map[startingX][doorPosition+1] = 0;
                        doorMade = true;
                    }
                    break;
                case 1://down
                    if(endingY < mapHeight-1){
                        doorPosition = (int)((Math.random() * (endingX - (startingX + 8))) + (startingX + 4));
                        map[doorPosition-1][endingY] = 0;
                        map[doorPosition][endingY] = 0;
                        map[doorPosition+1][endingY] = 0;
                        doorMade = true;
                    }
                    break;
                case 2://dx
                    if(endingX < mapWidth-1){
                        doorPosition = (int)((Math.random() * (endingY - (startingY + 8))) + (startingY + 4));
                        map[endingX][doorPosition-1] = 0;
                        map[endingX][doorPosition] = 0;
                        map[endingX][doorPosition+1] = 0;
                        doorMade = true;
                    }
                    break;
                case 3://up
                    if(startingY > 0){
                        doorPosition = (int)((Math.random() * (endingX - (startingX + 8))) + startingX + 4);
                        map[doorPosition-1][startingY] = 0;
                        map[doorPosition][startingY] = 0;
                        map[doorPosition+1][startingY] = 0;
                        doorMade = true;
                    }
                    break;
            }
        }
    }
}
