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
        if(path != null)
            initializeStack(targetX,targetY);
    }

    public void initializeStack(int targetX, int targetY){

        /*int lastNodeX = path.get(0).getPosX();
        int lastNodeY = path.get(0).getPosY();

        if(!(lastNodeX == targetX && lastNodeY == targetY)){
            Movement lastMovement = new Movement(targetX, targetY);
            movementStack.push(lastMovement);
        }*/

        int i = 1;

        for(Node n: path){
            if(i == path.size()){
                i++;
                continue;
            }
            Movement m = new Movement(n.getPosX(),n.getPosY());
            movementStack.push(m);
            i++;
        }
    }

    public void movement(){
        if(!movementStack.isEmpty()){
            float normalX = 0f, normalY = 0f;

            Movement nextMovement = movementStack.peek();
            int nextX = nextMovement.getCellX();
            int nextY = nextMovement.getCellY();

            int deltaX = Math.abs(nextX - owner.worldX);
            int deltaY = Math.abs(nextY - owner.worldY);

            int threshold = 5;

            if(deltaX > threshold || deltaY > threshold){
                if(deltaX > threshold)
                    normalX = findNormalX(owner.worldX, owner.worldY, nextX, nextY);
                if(deltaY > threshold)
                    normalY = findNormalY(owner.worldX, owner.worldY, nextX, nextY);
            }
            else{
                Movement newMovement = movementStack.pop();
                int newX = newMovement.getCellX();
                int newY = newMovement.getCellY();
                normalX = findNormalX(owner.worldX, owner.worldY , newX, newY);
                normalY = findNormalY(owner.worldX, owner.worldY , newX, newY);
            }
            //Log.i("AIComponent","Vettore direzione = ("+normalX+","+normalY+")");
            owner.updatePosition(normalX,normalY,0);

            if(movementStack.isEmpty())
                owner.updatePosition(0,0,0);
        }
    }

    public float findNormalX(int startX, int startY, int targetX, int targetY){

        int deltaX = targetX - startX;
        if(deltaX == 0)
            return 0f;

        int deltaY = targetY - startY;
        float length = (float) Math.sqrt(deltaX*deltaX + deltaY*deltaY);
        return (float) deltaX/length;
    }

    public float findNormalY(int startX, int startY, int targetX, int targetY){
        int deltaY = targetY - startY;
        if(deltaY == 0)
            return 0f;

        int deltaX = targetX - startX;
        float length = (float) Math.sqrt(deltaX*deltaX + deltaY*deltaY);
        return (float) deltaY/length;
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
