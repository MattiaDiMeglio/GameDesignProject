package com.MattiaDiMeglio.progettogamedesign;

import java.util.ArrayList;
import java.util.List;

public class Node implements Comparable<Node>{

    private int posX, posY; // cell center coordinates
    private boolean isObstacle;
    private boolean isBox;
    private boolean isMovableBox;
    private boolean isEnemy;

    // Parent in the path
    public Node parent = null;
    public List<Edge> neighbors;

    // Evaluation functions
    public float f = Float.MAX_VALUE;
    public float g = Float.MAX_VALUE;

    //Heuristic
    public float h;

    Node(float h, int x, int y, boolean isObstacle, boolean isBox, boolean isEnemy){
        this.h = h;
        this.posX = x;
        this.posY = y;
        this.isObstacle = isObstacle;
        this.isBox = isBox;
        this.isEnemy = isEnemy;
        this.neighbors = new ArrayList<>();
    }

    @Override
    public int compareTo(Node n) {
        return Float.compare(this.f, n.f);
    }

    public static class Edge {
        Edge(int weight, Node node){
            this.weight = weight;
            this.node = node;
        }

        public int weight;
        public Node node;
    }

    public void addBranch(int weight, Node node){
        Edge newEdge = new Edge(weight, node);
        neighbors.add(newEdge);
    }

    public float chebyshev(Node target){ //Chebyshev distance

        float deltaX = Math.abs(this.posX - target.posX);
        float deltaY = Math.abs(this.posY - target.posY);

        return h+Math.max(deltaX, deltaY);
    }

    public float manhattan(Node target){ //Manhattan distance

        float deltaX = Math.abs(this.posX - target.posX);
        float deltaY = Math.abs(this.posY - target.posY);

        //return deltaX+deltaY;
        return h+deltaX+deltaY;
    }

    public void setPosX(int posX) { this.posX = posX; }
    public void setPosY(int posY) { this.posY = posY; }
    public void setObstacle(boolean obstacle) { isObstacle = obstacle; }
    public void setBox(boolean isBox) { this.isBox = isBox; }
    public void setMovableBox(boolean movableBox) { isMovableBox = movableBox; }
    public void setEnemy(boolean enemy) { isEnemy = enemy; }

    public int getPosX() { return posX; }
    public int getPosY() { return posY; }
    public boolean isObstacle() { return isObstacle; }
    public boolean isBox() { return isBox; }
    public boolean isEnemy() { return isEnemy; }
    public boolean isMovableBox() { return isMovableBox; }

}
