package com.MattiaDiMeglio.progettogamedesign;

import android.util.Log;

import com.badlogic.androidgames.framework.Game;
import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.Fixture;

import java.util.List;
import java.util.Stack;

enum AIType {Dummy, Sniper, Wimp};

public class AIComponent extends Component{

    private Pathfinder pathfinder;
    private Stack<Movement> movementStack;
    private int gridSize;

    private AIType aiType;

    private int playerX, playerY;
    private float elapsedTime;

    final float updateTime = 0.2f;
    final float aimDelay = 1.0f;
    final float shootDelay = 2f;

    boolean playerInRange;
    boolean isAiming;

    public AIComponent(){
        pathfinder = new Pathfinder();
        movementStack = new Stack<>();
        playerX = 0;
        playerY = 0;
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
            setPlayerX(playerX);
            setPlayerY(playerY);
            this.elapsedTime = 0;
        }
    }

    public boolean checkPlayerInRange(GameWorld gameWorld){

        WeaponComponent enemyWeapon = (WeaponComponent) owner.getComponent(ComponentType.Weapon);
        int lineAmt = enemyWeapon.getLineAmt();
        float range = enemyWeapon.getRange();
        /*float rangeX = gameWorld.toMetersXLength(range);
        float rangeY = gameWorld.toMetersYLength(range);
        float totalRange = (float) Math.sqrt((rangeX * rangeX) + (rangeY * rangeY));*/

        PhysicsComponent enemyBody = (PhysicsComponent) owner.getComponent(ComponentType.Physics);

        float physX = enemyBody.getPositionX();
        float physY = enemyBody.getPositionY();
        /*float playerBodyX = gameWorld.toMetersX(getPlayerX());
        float playerBodyY = gameWorld.toMetersY(getPlayerY());*/

        float normalX = findNormalX(owner.worldX, owner.worldY, getPlayerX(), getPlayerY());
        float normalY = findNormalY(owner.worldX, owner.worldY, getPlayerX(), getPlayerY());
        float angle = 0;

        //Log.i("AIComponent","Normal XY = ("+normalX+","+normalY+")");

        if(lineAmt > 1){
            angle = (float) Math.atan2(normalY,normalX);
            angle = (float) Math.toDegrees(angle);
        }

        enemyWeapon.aim(normalX,normalY,angle,gameWorld);
        float[] aimLineX = enemyWeapon.getAimLineX();
        float[] aimLineY = enemyWeapon.getAimLineY();

        float distanceToPlayer = getDistanceToPlayer();

        for(int i = 0; i < lineAmt ; i++){
            float rangeX = gameWorld.toPixelsXLength(aimLineX[i]);
            float rangeY = gameWorld.toPixelsYLength(aimLineY[i]);
            float distance = (float) Math.sqrt((rangeX * rangeX) + (rangeY * rangeY));
            distance += 20;
            /*Log.d("AIComponent","distance = "+distance);
            Log.d("AIComponent","distanceToPlayer = "+distanceToPlayer);*/
            if(distanceToPlayer <= distance){
                return true;
            }
        }
        /*for(int i = 0; i < lineAmt ; i++){
            Fixture hitFixture = gameWorld.checkRaycast(physX,physY,aimLineX[i],aimLineY[i]);
            if(hitFixture == null){
                //Log.d("AIComponent","Fixture vuota");
                break;
            }

            Body castedBody = hitFixture.getBody();
            PhysicsComponent casteduserData = (PhysicsComponent) castedBody.getUserData();

            if(casteduserData.name.equals("Player")){
                return true;
            }

        }*/
        return false;
    }

    public void reset(){
        elapsedTime = 0f;
    }

    public float getDistanceToPlayer() {

        return (float) Math.sqrt(((playerX - owner.worldX) * (playerX - owner.worldX)) +
                (playerY - owner.worldY) * (playerY - owner.worldY));

        /*float playerBodyX = gameWorld.toMetersX(playerX);
        float playerBodyY = gameWorld.toMetersY(playerY);

        Log.d("getDistanceToPlayer","enemy body XY = ("+x+","+y+")");
        Log.d("getDistanceToPlayer","player body XY = ("+playerBodyX+","+playerBodyY+")");

        return (float) Math.sqrt(((playerBodyX - x) * (playerBodyX - x)) + ((playerBodyY - y) * (playerBodyY - y)));*/
    }

    @Override
    public ComponentType getType() { return ComponentType.AI; }

    public AIType getAiType() { return aiType; }

    public float getElapsedTime() { return elapsedTime; }

    public int getPlayerX() { return playerX; }

    public int getPlayerY() { return playerY; }

    //public List<Node> getPath() { return path; }

    public void increaseElapsedTime(float elapsedTime) { this.elapsedTime += elapsedTime; }

    public void setPlayerX(int playerX) { this.playerX = playerX; }

    public void setPlayerY(int playerY) { this.playerY = playerY; }

    public void setAiType(AIType aiType) { this.aiType = aiType; }

    public void setGridSize(int gridSize) { this.gridSize = gridSize; }
}
