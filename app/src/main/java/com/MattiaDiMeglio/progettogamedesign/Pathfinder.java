package com.MattiaDiMeglio.progettogamedesign;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class Pathfinder {

    Pathfinder(){}

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
}
