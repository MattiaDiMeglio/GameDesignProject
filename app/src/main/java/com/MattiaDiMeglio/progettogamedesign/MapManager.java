package com.MattiaDiMeglio.progettogamedesign;

import android.content.Context;
import android.util.Log;
//the map manager, calls the jsonparser and the factory to make the map

public class MapManager {

    private GameWorld gameWorld;
    private GameObjectFactory gameObjectFactory;
    private JSonParser jSonParser;
    private int mapWidth;
    private int mapHeight;
    Context context;


    public MapManager(GameWorld gameWorld, GameObjectFactory gameObjectFactory, Context context){
        this.gameWorld = gameWorld;
        this.gameObjectFactory = gameObjectFactory;
        this.context = context;
        jSonParser = new JSonParser(context, this);
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
                if(i>startingY) {
                    if(i>startingY + 2 && i < endingY - 3) {
                        map[randomIndex - 2][i] = randomWall(1);
                        map[randomIndex + 2][i] = randomWall(1);
                    } else {
                        map[randomIndex - 2][i] = 2;
                        map[randomIndex + 2][i] = 2;
                    }
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
                if(i > startingX) {
                    if(i > startingX+2 && i < endingX - 3) {
                        map[i][randomIndex - 2] = randomWall(0);
                        map[i][randomIndex + 2] = randomWall(0);
                    } else {
                        map[i][randomIndex - 2] = 2;
                        map[i][randomIndex + 2] = 2;
                    }
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
                    int type = (int)(Math.random() * 2) + 6;
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
                    map[randomX][randomY] = 9;
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
        for(int i = 0; i< 15; i++){
            generateBoxPosition(map);
        }
        printMap(map);
        for(int i = 0; i<width; i++){
            for(int j = 0; j<height; j++){
                switch (map[i][j]){
                    case 2:
                        makeWall("horizontal", toActualCoordX(i), toActualCoordX(j));
                        break;
                    case 3:
                        makeWall("horizontalHalf", toActualCoordX(i), toActualCoordX(j));
                        break;
                    case 4:
                        makeWall("verticalHalf", toActualCoordX(i), toActualCoordX(j));
                        break;
                    case 6:
                        makeEnemy(toActualCoordX(i), toActualCoordX(j), AIType.Dummy);
                        break;
                    case 7:
                        //makeEnemy(toActualCoordX(i), toActualCoordX(j), AIType.Sniper);
                        break;
                    case 8:
                       // makeEnemy(toActualCoordX(i), toActualCoordX(j), AIType.Patrol);
                        break;
                    case 9:
                        gameWorld.addGameObject(gameObjectFactory.makeBox(toActualCoordX(i), toActualCoordX(j)));
                        break;
                }
            }
        }

    }

    public int toActualCoordX(int x){return (AssetManager.WallPixmap.getWidth()/2 + x * AssetManager.WallPixmap.getWidth());}

    //called by the parser. Calls the corrispondent factory method based on the wall type
    public void makeWall(String type, int worldX, int worldY){
        switch (type){
            case "horizontal":
                gameWorld.addGameObject(gameObjectFactory.makeHorizontalWall(worldX, worldY));
                break;
            case "vertical":
                gameWorld.addGameObject(gameObjectFactory.makeVerticalWall(worldX, worldY));
                break;
            case "horizontalHalf":
                gameWorld.addGameObject(gameObjectFactory.makeHorizontalHalfWall(worldX, worldY));
                break;
            case "verticalHalf":
                gameWorld.addGameObject(gameObjectFactory.makeVerticalHalfWall(worldX, worldY));
            default:
                break;
        }
    }

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






    public int[][] initMap(int[][]map, int width, int height){
        map = new int[width][height];//init
        for(int i = 0; i<width; i++){ //settiamo tutto walkable
            for(int j = 0; j<height; j++){
                map[i][j] = 0;
            }
        }
        for(int i = AssetManager.WallPixmap.getHeight()/2; i <= width ; i+=AssetManager.WallPixmap.getHeight()){//aggiungiamo i muri lungo la width
            map[i][AssetManager.WallPixmap.getWidth()/2] = 2; //2 vertical wall
            map[i][height - AssetManager.WallPixmap.getWidth()/2] = 2;
        }
        for(int i = AssetManager.WallPixmap.getWidth()/2; i <= height - AssetManager.WallPixmap.getWidth()/2; i+=AssetManager.WallPixmap.getWidth()){//aggiungiamo i muri lungo la height
            map[AssetManager.WallPixmap.getHeight()/2][i] = 3; //3 horizontal wall
            map[width - AssetManager.WallPixmap.getHeight()/2][i] = 3;
        }
        return map;
    }

    //genera la mappa dividendo aree sempre piú piccole ricorsivamente
    public int[][] generateMap(int[][] mapCells, int startingX, int startingY, int endingX, int endingY, boolean vertical){
        int randomIndex = 0;
        int width = ((endingX - AssetManager.WallPixmap.getWidth()) - (startingX + AssetManager.WallPixmap.getWidth())) / AssetManager.WallPixmap.getWidth();
        int height = ((endingY - AssetManager.WallPixmap.getWidth()) - (startingY + AssetManager.WallPixmap.getWidth())) / AssetManager.WallPixmap.getWidth();


        if (vertical) {//dividiamo l'area verticalmente
            randomIndex = (int) (Math.random() * (width - 16) + 8);
            int y = 0, x = 0, x2 = 0, x3 = 0;
            x2 = startingY + ((randomIndex-1) * AssetManager.WallPixmap.getWidth() + AssetManager.WallPixmap.getWidth()/2) - AssetManager.WallPixmap.getWidth();
            x3 = startingY + ((randomIndex+1) * AssetManager.WallPixmap.getWidth() + AssetManager.WallPixmap.getWidth()/2) + AssetManager.WallPixmap.getWidth();
            int a = 0;

            for(int i = 0; i<height+2; i++){
                for(int j = randomIndex-1; j<randomIndex+2; j++){
                    y = startingX + (i * AssetManager.WallPixmap.getWidth() + AssetManager.WallPixmap.getWidth()/2);
                    x = startingY + (j * AssetManager.WallPixmap.getWidth() + AssetManager.WallPixmap.getWidth()/2);

                    mapCells[x][y] = 1;
                    if (i > 0 && i < height + 1) {
                        mapCells[x2][y] = 2;
                        mapCells[x3][y] = 2;
                    }

                }
            }
            if(height>21) {
                generateMap(mapCells, startingX, startingY, x2+(AssetManager.WallPixmap.getWidth()/2), endingY, !vertical);
                generateMap(mapCells, x3-(AssetManager.WallPixmap.getWidth()/2), startingY, endingX, endingY, !vertical);
            }

            //randomIndex = (int) ((Math.random() * (endingY - (5*AssetManager.WallPixmap.getWidth()))) + (3*AssetManager.WallPixmap.getWidth())); //indice casuale
            /*for(int i = startingX; i<endingX; i++){
                for(int j = randomIndex-AssetManager.WallPixmap.getWidth(); j<randomIndex + AssetManager.WallPixmap.getWidth(); j++){
                    mapCells[i][j]=1;
                }
            }*/
         //   int t1 = randomIndex - (AssetManager.WallPixmap.getWidth()/2 + AssetManager.WallPixmap.getWidth());
         //   int t2 = randomIndex + (int)(1.5f * AssetManager.WallPixmap.getWidth()+1);
         //   for(int i = startingX+AssetManager.WallPixmap.getHeight()/2; i< endingX; i+=AssetManager.WallPixmap.getHeight()){
         //       mapCells[i][t1] = 2;
         //       mapCells[i][t2] = 2;
         //   }
        //    if((t1-AssetManager.WallPixmap.getWidth()/2) - startingY >= 120)//se il blocco a sx della divisione è maggiore di 40 blocchi
         //       generateMap(mapCells, startingX, startingY, endingX, randomIndex-1, !vertical);
        //    if(endingY - (t2+AssetManager.WallPixmap.getWidth()/2) >= 120)//se il blocco a destra è maggiore di 40 blocchi
        //        generateMap(mapCells, startingX, randomIndex+1, endingX, endingY, !vertical);
        } else {//dividiamo orizzontalmente
            randomIndex = (int) (Math.random() * (height - 16) + 8);
            int x = 0, y = 0, y2 = 0, y3 = 0;
            y2 = startingY + ((randomIndex-1) * AssetManager.WallPixmap.getWidth() + AssetManager.WallPixmap.getWidth()/2) - AssetManager.WallPixmap.getWidth();
            y3 = startingY + ((randomIndex+1) * AssetManager.WallPixmap.getWidth() + AssetManager.WallPixmap.getWidth()/2) + AssetManager.WallPixmap.getWidth();
            int a = 0;
            for(int i = 0; i<width+2; i++){
                for(int j = randomIndex-1; j<randomIndex+2; j++){
                    x = startingX + (i * AssetManager.WallPixmap.getWidth() + AssetManager.WallPixmap.getWidth()/2);
                    y = startingY + (j * AssetManager.WallPixmap.getWidth() + AssetManager.WallPixmap.getWidth()/2);


                    mapCells[x][y] = 1;
                    if(i>0 && i<width+1){
                        mapCells[x][y2] = 2;
                        mapCells[x][y3] = 2;
                    }

                }
            }
            if(width>21) {
                generateMap(mapCells, startingX, startingY, endingX, y2+(AssetManager.WallPixmap.getWidth()/2), !vertical);
                generateMap(mapCells, startingX, y3 - (AssetManager.WallPixmap.getWidth()/2), endingX, endingY, !vertical);
            }

          /*  randomIndex = (int) ((Math.random() * (endingX - startingX - AssetManager.horizontalWall.getHeight()/2)-4) + (startingX + AssetManager.horizontalWall.getHeight()/2)+2);//indice casuale
            Log.d("s", "" +randomIndex);
            for(int i = randomIndex-2; i<randomIndex + 2; i++){
                for(int j = startingY; j<endingY; j++){
                    mapCells[i][j]=1;
                }
            }
            for(int i = startingY + AssetManager.WallPixmap.getWidth()/2; i<endingY; i+=AssetManager.horizontalWall.getWidth()){
                mapCells[(randomIndex-2) - AssetManager.WallPixmap.getHeight()/2][i] = 2;
                mapCells[(randomIndex+2) + AssetManager.WallPixmap.getHeight()/2][i] = 2;
            }
            if((randomIndex-2) - startingX >= 120)//se la parte sopra la divisione è maggiore di 40
                generateMap(mapCells, startingX, startingY, randomIndex-2, endingY, !vertical);
            if(endingX - (randomIndex+2) >= 120)//se la parte sotto la divisine è maggiore di 40
                generateMap(mapCells, randomIndex+2, startingY, endingX, endingY, !vertical);*/
        }
        return mapCells;

    };

    public void makeWalls(){//makes the wall of the maps
        //makes the perimeter walls
        int number = (int) AssetManager.backgroundPixmap.getWidth() / AssetManager.WallPixmap.getWidth();
        for(int i = 0; i < number + 1; i++){
            gameWorld.addGameObject(gameObjectFactory.makeHorizontalWall(AssetManager.WallPixmap.getWidth()/2 + i * AssetManager.WallPixmap.getWidth(),
                    AssetManager.WallPixmap.getHeight()/2));
            gameWorld.addGameObject(gameObjectFactory.makeHorizontalWall(AssetManager.WallPixmap.getWidth()/2 + i * AssetManager.WallPixmap.getWidth(),
                    AssetManager.backgroundPixmap.getHeight() - AssetManager.WallPixmap.getHeight()/2));
        }
        number = (int) AssetManager.backgroundPixmap.getHeight() / AssetManager.verticalWall.getHeight();
        number++;
        for(int i = 0; i < number; i++){
            gameWorld.addGameObject(gameObjectFactory.makeVerticalWall(AssetManager.verticalWall.getWidth()/2,
                    AssetManager.verticalWall.getHeight()/2 + i * AssetManager.verticalWall.getHeight()));
            gameWorld.addGameObject(gameObjectFactory.makeVerticalWall(AssetManager.backgroundPixmap.getWidth() - AssetManager.verticalWall.getWidth()/2,
                    AssetManager.verticalWall.getHeight()/2 + i * AssetManager.verticalWall.getHeight()));
        }
        //calls the json parser to get the internal walls
        jSonParser.parseWalls();
    }

    //calls the parser for the enemies
    public void makeEnemies(){
        jSonParser.parseEnemies();
    }

    //calls the factory to make the enemies
    public void makeEnemy(int worldX, int worldY, AIType aiType){
        gameWorld.addGameObject(gameObjectFactory.makeEnemy(worldX, worldY, aiType));
    }
}
