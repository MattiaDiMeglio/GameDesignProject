package com.MattiaDiMeglio.progettogamedesign;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class Pathfinder {

    Pathfinder(){}

    public Node aStar(Node start, Node target, AIType aiType){
        PriorityQueue<Node> closedList = new PriorityQueue<>();
        PriorityQueue<Node> openList = new PriorityQueue<>();

        if(start.parent!=null)
            start.parent = null;

        if(target.parent!=null)
            target.parent = null;

        if(target.isObstacle() || ((start.getPosX() == target.getPosX()) && (start.getPosY() == target.getPosY())))
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

                if(m.isEnemy()) //skipped nodes with enemies
                    continue;

                //Dummies shoot at boxes on their path, other enemies avoid them
                if(!aiType.equals(AIType.Dummy) && m.isBox())
                    continue;

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

        while(n.parent != null){ // get the path through parents of resulting node
            path.add(n);
            n = n.parent;
        }
        path.add(n);
        //Collections.reverse(path); //if this line is commented, the path will be sorted from destination to start
        return path;
    }
}
