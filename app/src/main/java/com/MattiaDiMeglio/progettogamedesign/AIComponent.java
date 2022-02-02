package com.MattiaDiMeglio.progettogamedesign;

import java.util.List;
import java.util.Stack;

enum AIType {Dummy, Sniper, Wimp};

public class AIComponent extends Component{

    private Pathfinder pathfinder;
    protected Stack<Movement> movementStack;
    private int gridSize;

    private AIType aiType;

    private int lastPlayerX, lastPlayerY;
    private float elapsedTime;

    final float updateTime = 0.2f;
    final float aimDelay = 0.7f;
    final float shootDelay = 1.2f;
    final float reloadDelay = 0.5f;

    float aimingTimer = 0f;
    float shootingTimer = 0f;
    float reloadingTimer = 0f;
    boolean playerInRange = false;

    public AIComponent(){
        pathfinder = new Pathfinder();
        movementStack = new Stack<>();
        lastPlayerX = 0;
        lastPlayerY = 0;
        elapsedTime = 0f;
    }

    public void pathfind(int targetX, int targetY, Node[][] cells){

        Node start = findNode(owner.worldX,owner.worldY,gridSize,cells); // da wX e wY a celle
        Node target = findNode(targetX,targetY,gridSize,cells);
        Node res = pathfinder.aStar(start,target);
        List<Node> path =  pathfinder.getPath(res);
        if(path != null)
            initializeStack(targetX, targetY, path);
    }

    public void initializeStack(int targetX, int targetY, List<Node> path){

        //Il path restituito dal pathfinder è "al contrario", ossia:
        //se il percorso da far compiere al nemico è A->B->C,
        //path sarà ordinato così: C-B-A

        if(!movementStack.isEmpty())
            emptyStack(); //se c'era un vecchio path da percorrere, lo stack viene svuotato
                          //per far posto al nuovo path

        int i = 1;

        for(Node n: path){
            if(i == 1){ //cosi facendo l'ultima posizione raggiunta sarà effettivamente (tX,tY),
                        //invece di prendere le coordinate del relativo nodo
                        //NB: potrebbe portare i nemici a schiantarsi nei muri?
                Movement lastMovement = new Movement(targetX, targetY);
                movementStack.push(lastMovement);
            }
            else if(i == path.size())
                break; //viene saltato il nodo di partenza nel path, per evitare strani movimenti iniziali
            else{
                Movement m = new Movement(n.getPosX(),n.getPosY());
                movementStack.push(m);
            }
            i++;
        }
    }

    public void emptyStack(){
        while(!movementStack.isEmpty())
            movementStack.pop();
        owner.updatePosition(0,0,0);
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
            owner.updatePosition(normalX,normalY,0);

            if(movementStack.isEmpty()) //se si è appena svuotato lo stack, il nemico si ferma
                owner.updatePosition(0,0,0);
        }
    }

    public float findNormalX(float startX, float startY, float targetX, float targetY){
        float deltaX = targetX - startX;
        if(deltaX == 0)
            return 0f;

        float deltaY = targetY - startY;
        float length = (float) Math.sqrt(deltaX*deltaX + deltaY*deltaY);
        return (float) deltaX/length;
    }

    public float findNormalY(float startX, float startY, float targetX, float targetY){
        float deltaY = targetY - startY;
        if(deltaY == 0)
            return 0f;

        float deltaX = targetX - startX;
        float length = (float) Math.sqrt(deltaX*deltaX + deltaY*deltaY);
        return (float) deltaY/length;
    }

    public Node findNode(int x, int y, int gridSize, Node[][] cells){
        //date le coordinate worldX e worldY, ricava il il relativo nodo della griglia

        int gridX = x / gridSize;
        int gridY = y / gridSize;
        return cells[gridY][gridX];
    }

    public void updateAI(int playerX, int playerY, float elapsedTime, Node[][] cells, GameWorld gameWorld){

        increaseElapsedTime(elapsedTime);
        if(this.elapsedTime > updateTime ){
            setLastPlayerX(playerX);
            setLastPlayerY(playerY);
            this.elapsedTime = 0;
        }
    }

    public boolean checkPlayerInRange(){

        WeaponComponent enemyWeapon = (WeaponComponent) owner.getComponent(ComponentType.Weapon);
        int lineAmt = enemyWeapon.getLineAmt();
        float range = enemyWeapon.getRange();

        float distanceToPlayer = getDistanceToPlayer();

        for(int i = 0; i < lineAmt ; i++){
            if(distanceToPlayer <= range+18){
                return true;
            }
        }
        return false;
    }

    public void reset(){
        elapsedTime = 0f;
        aimingTimer = 0f;
        shootingTimer = 0f;
        reloadingTimer = 0f;
        playerInRange = false;
    }

    public float getDistanceToPlayer() {
        return (float) Math.sqrt(((lastPlayerX - owner.worldX) * (lastPlayerX - owner.worldX)) +
                (lastPlayerY - owner.worldY) * (lastPlayerY - owner.worldY));
    }

    @Override
    public ComponentType getType() { return ComponentType.AI; }

    public AIType getAiType() { return aiType; }

    public float getElapsedTime() { return elapsedTime; }

    public int getLastPlayerX() { return lastPlayerX; }

    public int getLastPlayerY() { return lastPlayerY; }

    public void increaseElapsedTime(float elapsedTime) { this.elapsedTime += elapsedTime; }

    public void setLastPlayerX(int lastPlayerX) { this.lastPlayerX = lastPlayerX; }

    public void setLastPlayerY(int lastPlayerY) { this.lastPlayerY = lastPlayerY; }

    public void setAiType(AIType aiType) { this.aiType = aiType; }

    public void setGridSize(int gridSize) { this.gridSize = gridSize; }
}
