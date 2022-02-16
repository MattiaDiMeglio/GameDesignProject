package com.MattiaDiMeglio.progettogamedesign;

import android.util.Log;

import java.util.List;
import java.util.Stack;

enum AIType {Dummy, Sniper, Patrol};

public class AIComponent extends Component{

    private Pathfinder pathfinder;
    protected Stack<Movement> movementStack;
    protected int gridSize;

    private AIType aiType;

    int lastPlayerX, lastPlayerY;
    int enemyTargetX = 0, enemyTargetY = 0;

    final float playerPositionUpdateDelay = 0.2f;
    float playerPositionTimer;
    boolean playerInRange = false;

    float aimDelay;
    float shootDelay;
    float reloadDelay;

    float aimingTimer = 0;
    float shootingTimer = 0;
    float reloadingTimer = 0;

    public AIComponent(){
        pathfinder = new Pathfinder();
        movementStack = new Stack<>();
        lastPlayerX = 0;
        lastPlayerY = 0;
        playerPositionTimer = 0;
    }

    public void pathfind(int targetX, int targetY, Node[][] cells){

        Node start = findNode(owner.worldX,owner.worldY,gridSize,cells); // da wX e wY a celle
        Node target = findNode(targetX,targetY,gridSize,cells);
        Node res = pathfinder.aStar(start,target,aiType);
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
        owner.updatePosition(0,0,((EnemyGameObject) owner).getFacingAngle());

    }

    public void movement(float enemySpeed){
        if(!movementStack.isEmpty()){
            float normalX = 0f, normalY = 0f;

            Movement nextMovement = movementStack.peek();
            int nextX = nextMovement.getCellX();
            int nextY = nextMovement.getCellY();

            int deltaX = Math.abs(nextX - owner.worldX);
            int deltaY = Math.abs(nextY - owner.worldY);

            float threshold = enemySpeed + 2;

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
            ((EnemyGameObject) owner).setFacingAngle(-(int) Math.toDegrees(Math.atan2(normalY,normalX)));
            owner.updatePosition(normalX,normalY,((EnemyGameObject) owner).getFacingAngle());

            if(movementStack.isEmpty()) //se si è appena svuotato lo stack, il nemico si ferma
                owner.updatePosition(0,0,((EnemyGameObject) owner).getFacingAngle());
        }
    }

    public float findNormalX(float startX, float startY, float targetX, float targetY){
        if(startX == targetX)
            return 0f;

        float deltaX = targetX - startX;
        float deltaY = targetY - startY;
        float length = (float) Math.sqrt(deltaX*deltaX + deltaY*deltaY);
        return deltaX/length;
    }

    public float findNormalY(float startX, float startY, float targetX, float targetY){
        if(startY == targetY)
            return 0f;

        float deltaY = targetY - startY;
        float deltaX = targetX - startX;
        float length = (float) Math.sqrt(deltaX*deltaX + deltaY*deltaY);
        return deltaY/length;
    }

    public Node findNode(int x, int y, int gridSize, Node[][] cells){
        //date le coordinate worldX e worldY, ricava il il relativo nodo della griglia

        int gridX = x / gridSize;
        int gridY = y / gridSize;
        return cells[gridY][gridX];
    }

    public void updateAI(int playerX, int playerY, float elapsedTime, Node[][] cells, GameWorld gameWorld){

        playerPositionTimer += elapsedTime;

        if(playerPositionTimer > playerPositionUpdateDelay){
            lastPlayerX = playerX;
            lastPlayerY = playerY;
            playerPositionTimer = 0;
        }
    }

    public boolean checkPlayerInRange(GameWorld gameWorld){

        WeaponComponent enemyWeapon = (WeaponComponent) owner.getComponent(ComponentType.Weapon);
        float range = enemyWeapon.getRange();

        float distanceToPlayer = getDistance(lastPlayerX, lastPlayerY, owner.worldX, owner.worldY);

        if(distanceToPlayer <= range+18){
            enemyAim(enemyWeapon, gameWorld, lastPlayerX, lastPlayerY);
            if(enemyWeapon.checkLineOfFire(gameWorld))
                return true;
        }

        enemyTargetX = 0;
        enemyTargetY = 0;
        return false;
    }

    public void enemyAim(WeaponComponent weaponComponent, GameWorld gameWorld, int targetX, int targetY){

        if(enemyTargetX == 0 || enemyTargetY == 0){
            enemyTargetX = targetX;
            enemyTargetY = targetY;
        }

        float normalX = findNormalX(owner.worldX, owner.worldY, enemyTargetX, enemyTargetY);
        float normalY = findNormalY(owner.worldX, owner.worldY, enemyTargetX, enemyTargetY);

        ((EnemyGameObject) owner).setFacingAngle((int) Math.toDegrees(Math.atan2(normalY,normalX)));

        weaponComponent.aim(normalX,normalY,((EnemyGameObject) owner).getFacingAngle(), gameWorld);
        owner.updatePosition(0,0,-((EnemyGameObject) owner).getFacingAngle());
    }

    public void enemyShoot(WeaponComponent weaponComponent, GameWorld gameWorld){
        enemyTargetX = 0;
        enemyTargetY = 0;
        aimingTimer = 0f;
        shootingTimer = 0f;
        weaponComponent.shoot(gameWorld);
    }

    public void reset(){
        playerPositionTimer = 0f;
        aimingTimer = 0f;
        shootingTimer = 0f;
        reloadingTimer = 0f;
        enemyTargetX = 0;
        enemyTargetY = 0;
        playerInRange = false;

        WeaponComponent weaponComponent = (WeaponComponent) owner.getComponent(ComponentType.Weapon);
        if(weaponComponent.bullets < weaponComponent.mag)
            weaponComponent.reload();

        if(!movementStack.isEmpty())
            emptyStack();
    }

    public float getDistance(int startX, int startY, int destinationX, int destinationY) {
        return (float) Math.sqrt(((startX - destinationX) * (startX - destinationX)) +
                ((startY - destinationY) * (startY - destinationY)));
    }

    @Override
    public ComponentType getType() { return ComponentType.AI; }

    public AIType getAiType() { return aiType; }

    public void setAiType(AIType aiType) { this.aiType = aiType; }
    public void setGridSize(int gridSize) { this.gridSize = gridSize; }
}
