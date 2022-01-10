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
        for(Node n: path){
            Movement m = new Movement(n.getPosX(),n.getPosY());
            Log.i("initializeStack","Push posizione: ("+n.getPosX()+","+n.getPosY()+")");
            movementStack.push(m);
        }
    }

    public void movement(){
        if(!movementStack.isEmpty()){
            float normalX = 0f, normalY = 0f;

            Movement nextMovement = movementStack.peek(); //prendo il top of stack movement, ovvero il prossimo
                                                          //da effettuare
            int nextX = nextMovement.getCellX();
            int nextY = nextMovement.getCellY();

            if(!(owner.worldX == nextX && owner.worldY == nextY)){
                //Log.i("AIComponent movement","Enemy position: ("+owner.worldX+","+owner.worldY+")");
                normalX = findNormalX(owner.worldX, owner.worldY, nextX, nextY);
                normalY = findNormalY(owner.worldX, owner.worldY, nextX, nextY);
            }
            else{
                Movement newMovement = movementStack.pop();
                int newX = newMovement.getCellX();
                int newY = newMovement.getCellY();
                normalX = findNormalX(owner.worldX, owner.worldY, newX, newY);
                normalY = findNormalY(owner.worldX, owner.worldY, newX, newY);
            }
            //Log.i("AIComponent movement","Vettore movimento = ("+normalX+","+normalY+")");
            owner.updatePosition(normalX,normalY,0);
        }
    }

    public float findNormalX(int startX, int startY, int targetX, int targetY){
        int deltaX = targetX - startX;
        int deltaY = targetY - startY;
        float length = (float) Math.sqrt(deltaX*deltaX + deltaY*deltaY);

        return deltaX/length;
    }

    public float findNormalY(int startX, int startY, int targetX, int targetY){
        int deltaX = targetX - startX;
        int deltaY = targetY - startY;
        float length = (float) Math.sqrt(deltaX*deltaX + deltaY*deltaY);

        return deltaY/length;
    }

    public Node findNode(int x, int y, int gridSize, Node[][] cells){ //date le coordinate worldX e worldY, ricava il
                                                                      //il relativo nodo della griglia
        int gridX = x / gridSize;
        int gridY = y / gridSize;
        return cells[gridY][gridX];
    }

    @Override
    public ComponentType getType() { return ComponentType.AI; }
}
