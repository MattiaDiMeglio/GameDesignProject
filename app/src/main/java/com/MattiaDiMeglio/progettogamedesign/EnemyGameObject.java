package com.MattiaDiMeglio.progettogamedesign;

import android.util.Log;

import java.util.List;
import java.util.Random;

//the enemyGo
public class EnemyGameObject extends GameObject {
    private GameWorld gameWorld;//the gameWorld,
    private PixMapComponent drawableComponent;//component saved for simplicity
    private CharacterBodyComponent characterBodyComponent;
    private PathfindingComponent pathfindingComponent;
    protected boolean killed = false;//has it been killed?

    public EnemyGameObject(GameWorld gameWorld, int worldX, int worldY){//constructor
        this.gameWorld = gameWorld;//gw
        this.name = "Enemy";//name
        //gives the enemy a random position on the background
        this.worldX = worldX;//worldPos are the GO position on the map
        this.worldY = worldY;
    }

    //updates the graphical and physical positions
    @Override
    public void updatePosition(int x, int y){
        drawableComponent = (PixMapComponent) components.get(ComponentType.Drawable);
        characterBodyComponent = (CharacterBodyComponent) components.get(ComponentType.Physics);

        drawableComponent.setPosition(x, y);

        float touchX = gameWorld.toPixelsTouchX(x);
        float touchY = gameWorld.toPixelsTouchY(y);
        characterBodyComponent.setTransform(gameWorld.toMetersX(touchX),
                gameWorld.toMetersY(touchY));
    }

    public void updatePosition(int x, int y, int angle){ //stessa funzione che ha il player (ControllableComponent)

        drawableComponent = (PixMapComponent) components.get(ComponentType.Drawable);
        characterBodyComponent = (CharacterBodyComponent) components.get(ComponentType.Physics);

        characterBodyComponent.update(x, y, angle);

        int currentGX =(int)gameWorld.toPixelsX(characterBodyComponent.getPositionX());
        int currentGY =(int)gameWorld.toPixelsY(characterBodyComponent.getPositionY());
        //Log.d("Controller", "x " + currentGX + " y: " + currentGY);
        drawableComponent.setPosition(currentGX, currentGY, angle);
    }

    @Override
    public void update() {
        drawableComponent = (PixMapComponent) components.get(ComponentType.Drawable);
        characterBodyComponent = (CharacterBodyComponent) components.get(ComponentType.Physics);

        drawableComponent.setPosition((int)gameWorld.toPixelsX(characterBodyComponent.getPositionX()),
                (int)gameWorld.toPixelsY(characterBodyComponent.getPositionY()));
    }

    @Override//puts enemy out of view
    public void outOfView() {
        characterBodyComponent.setTransform(40, 40);
    }

    public void killed(){
        drawableComponent.pixmap = AssetManager.enemyKilled;
        killed = true;
    }

    public List<Node> pathfind(int targetX, int targetY, int gridSize, Node[][] cells){

        pathfindingComponent = (PathfindingComponent) components.get(ComponentType.Pathfinder);

        Node start = pathfindingComponent.findNode(worldX,worldY,gridSize,cells);
        Node target = pathfindingComponent.findNode(targetX,targetY,gridSize,cells);
        Node res = pathfindingComponent.aStar(start,target);

        return pathfindingComponent.getPath(res);
    }

    public void moveEnemy(List<Node> path){

        int[] xSet = pathfindingComponent.findXSet(path); //insieme delle coordinate X dei nodi del path
        int[] ySet = pathfindingComponent.findYSet(path);; //insieme delle coordinate Y dei nodi del path

        int[] normalVectorX = pathfindingComponent.findVectorX(xSet,ySet); //vettori X normalizzati
        int[] normalVectorY = pathfindingComponent.findVectorY(xSet,ySet); //vettori Y normalizzati
        //float[] magnitude = pathfindingComponent.findMagnitude(xSet,ySet); //moduli dei vettori

        for(int i = 0; i < normalVectorX.length ; i++)
            updatePosition(normalVectorX[i],normalVectorY[i], 0);

        //Per provare il pathfinding con questo metodo, la factory del GW deve essere public
        //for(int i = 0; i < xSet.length ; i++)
            //gameWorld.addGameObject(gameWorld.gameObjectFactory.makeEnemy(xSet[i],ySet[i]));

        //int targetX = path.get(path.size()-1).getPosX(); //ricavo le coordinate dell'ultimo nodo del path
        //int targetY = path.get(path.size()-1).getPosY();

        //this.worldX = targetX; //setto le nuove coordinate del nemico
        //this.worldY = targetY;
    }

}
