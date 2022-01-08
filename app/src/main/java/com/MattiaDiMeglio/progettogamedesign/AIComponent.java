package com.MattiaDiMeglio.progettogamedesign;

import android.util.Log;

import java.util.List;
import java.util.Stack;

public class AIComponent extends Component{

    private Pathfinder pathfinder;
    private Stack<Movement> movementStack;
    public List<Node> path;

    public AIComponent(){
        pathfinder = new Pathfinder();
        movementStack = new Stack<>();
    }

    public void pathfind(int targetX, int targetY, int gridSize, Node[][] cells){
        Node start = findNode(owner.worldX,owner.worldY,gridSize,cells); // da wX e wY a celle
        Node target = findNode(targetX,targetY,gridSize,cells);
        Node res = pathfinder.aStar(start,target);
        path =  pathfinder.getPath(res);
    }

    public void initializeStack(){

        int[] xSet = findXSet(path); //insieme delle coordinate X dei nodi del path
        int[] ySet = findYSet(path);; //insieme delle coordinate Y dei nodi del path
        int[] vectorX = findVectorX(xSet,ySet); //vettori X
        int[] vectorY = findVectorY(xSet,ySet); //vettori Y

        //il ciclo for è al contrario perché così facendo farò le pop dei vettori da start a end

        for(int i = vectorX.length -1 ; i > -1 ; i--){
            Movement m = new Movement(xSet[i+1],ySet[i+1],vectorX[i]/2, vectorY[i]/2);
            //ho dimezzato le componenti dei vettori poiché l'update di DynamicBodyComponent,
            //come quella di CharacterBodyComponent, va a raddoppiare queste componenti
            movementStack.push(m);
        }
    }

    public void movement(){
        if(!movementStack.isEmpty()){
            Movement m = movementStack.pop();
            int x = m.getCellX();
            int y = m.getCellY();
            int normX = m.getVectorX();
            int normY = m.getVectorY();

            owner.updatePosition(normX,normY, 0);
            owner.worldX = x;
            owner.worldY = y;
        }
    }

    public Node findNode(int x, int y, int gridSize, Node[][] cells){ //date le coordinate worldX e worldY, ricava il
                                                                      //il relativo nodo della griglia
        int gridX = x / gridSize;
        int gridY = y / gridSize;
        return cells[gridY][gridX];
    }

    public int[] findXSet(List<Node> path){ //ritorna un array contenente le x di tutti i nodi del path

        //Il path che ci viene restuito dal pathfinding è ordinato
        //dalla cella di start alla cella target

        int[] xSet = new int[path.size()];
        xSet[0] = owner.worldX; //la prima coordinata è il punto di partenza del nemico, il primo nodo del path
        //contiene le coordinate della cella, che potrebbero essere diverse
        //dalle effettive coordinate del nemico
        //Serve in ogni caso tenere traccia delle coordinate iniziali per calcolare
        //le componenti del primo vettore movimento
        int i = 0;
        for(Node n: path){
            if(i > 0)
                xSet[i] = n.getPosX();
            i++;
        }
        return xSet;
    }

    public int[] findYSet(List<Node> path){
        int[] ySet = new int[path.size()];
        ySet[0] = owner.worldY;
        int i = 0;
        for(Node n: path){
            if(i > 0)
                ySet[i] = n.getPosY();
            i++;
        }
        return ySet;
    }

    public int[] findVectorX(int[] xSet, int[] ySet){
        int[] vectorX = new int[xSet.length-1]; // se i nodi del path sono N, dovrò effettuare N-1 movimenti

        //Voglio spostarmi dal nodo A al nodo B
        //la coordinata x di A sta in xSet[0]
        //la coordinata x di B sta in xSet[1]
        //vettore X = xb - xa

        for(int i = 0; i < vectorX.length; i++){
            int deltaX = (xSet[i+1] - xSet[i]);
            //int deltaY = (ySet[i+1] - ySet[i]);
            //int length = (int) Math.sqrt(deltaX*deltaX + deltaY*deltaY); //modulo del vettore
            vectorX[i] = deltaX; //così facendo i vettori non sono normalizzati
        }
        return vectorX;
    }

    public int[] findVectorY(int[] xSet, int[] ySet){
        int[] vectorY = new int[ySet.length-1];
        for(int i = 0; i < vectorY.length; i++){
            //int deltaX = (xSet[i+1] - xSet[i]);
            int deltaY = (ySet[i+1] - ySet[i]);
            //int length = (int) Math.sqrt(deltaX*deltaX + deltaY*deltaY);
            vectorY[i] = deltaY;
        }
        return vectorY;
    }

    @Override
    public ComponentType getType() { return ComponentType.AI; }
}
