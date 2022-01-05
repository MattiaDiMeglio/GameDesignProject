package com.MattiaDiMeglio.progettogamedesign;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class PathfindingComponent extends Component{

    PathfindingComponent(){}

    public Node aStar(Node start, Node target){
        PriorityQueue<Node> closedList = new PriorityQueue<>();
        PriorityQueue<Node> openList = new PriorityQueue<>();

        //Se la cella target contiene un nodo, il nemico non si muove
        //Se start e target corrispondono, quindi il nemico si è già spostato, il nemico non si muove (di nuovo)
        if(target.isWall() || ((start.getPosX() == target.getPosX()) && (start.getPosY() == target.getPosY())))
            return null;

        start.g = 0;
        start.f = start.g + start.manhattan(target);
        openList.add(start);

        while(!openList.isEmpty()){
            Node n = openList.peek();
            if(n == target)
                return n;

            for(Node.Edge edge : n.neighbors){
                Node m = edge.node;
                float totalWeight = n.g + edge.weight;

                if(!openList.contains(m) && !closedList.contains(m)){
                    m.parent = n;
                    m.g = totalWeight;
                    m.f = m.g + m.manhattan(target);
                    openList.add(m);
                } else {
                    if(totalWeight < m.g){
                        m.parent = n;
                        m.g = totalWeight;
                        m.f = m.g + m.manhattan(target);

                        if(closedList.contains(m)){
                            closedList.remove(m);
                            openList.add(m);
                        }
                    }
                }
            }
            openList.remove(n);
            closedList.add(n);
        }
        return null;
    }

    public List<Node> getPath(Node target){
        Node n = target;
        if(n==null)
            return null;

        List<Node> path = new ArrayList<>();

        while(n.parent != null){ // attraverso i parent del nodo destinazione, ricaviamo il path
            path.add(n);
            n = n.parent;
        }
        path.add(n);
        Collections.reverse(path);
        return path;
    }

    public Node findNode(int x, int y, int gridSize, Node[][] cells){ //date le coordinate worldX e worldY, ricava il
                                                                      //il relativo nodo della griglia
        int gridX = x / gridSize;
        int gridY = y / gridSize;
        return cells[gridY][gridX];
    }

    public int[] findXSet(List<Node> path){ //ritorna un array contenente le x di tutti i nodi del path

        int[] xSet = new int[path.size()];
        xSet[0] = owner.worldX; //la prima coordinata è il punto di partenza del nemico, il primo nodo del path
                                //contiene le coordinate della cella, che potrebbero essere diverse
                                //dalle effettive coordinate del nemico
        int i = 0;
        for(Node n: path){
            if(!(i == 0)) //per evitare di prendere le coordinate del primo nodo del path (cella di partenza)
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
            if(!(i == 0))
                ySet[i] = n.getPosY();
            i++;
        }
        return ySet;
    }

    public int[] findVectorX(int[] xSet, int[] ySet){

        int[] vectorX = new int[xSet.length-1]; // se i nodi del path sono N, dovrò effettuare N-1 movimenti
        for(int i = 0; i < vectorX.length; i++){
            int deltaX = Math.abs(xSet[i] - xSet[i+1]);
            int deltaY = Math.abs(ySet[i] - ySet[i+1]);
            int length = (int) Math.sqrt(deltaX*deltaX + deltaY*deltaY); //modulo del vettore
            vectorX[i] = deltaX/length; //componente normalizzata
        }
        return vectorX;
    }

    public int[] findVectorY(int[] xSet, int[] ySet){

        int[] vectorY = new int[ySet.length-1];
        for(int i = 0; i < vectorY.length; i++){
            int deltaX = Math.abs(xSet[i] - xSet[i+1]);
            int deltaY = Math.abs(ySet[i] - ySet[i+1]);
            int length = (int) Math.sqrt(deltaX*deltaX + deltaY*deltaY);
            vectorY[i] = deltaY/length;
        }
        return vectorY;
    }

    public float[] findMagnitude(int[] xSet, int[] ySet){
        float[] magnitude = new float[xSet.length-1];
        for(int i = 0; i < magnitude.length; i++){
            int deltaX = Math.abs(xSet[i] - xSet[i+1]);
            int deltaY = Math.abs(ySet[i] - ySet[i+1]);
            magnitude[i] = (float) Math.sqrt(deltaX*deltaX + deltaY*deltaY);
        }
        return magnitude;
    }

    @Override
    public ComponentType getType() { return ComponentType.Pathfinder; }
}
