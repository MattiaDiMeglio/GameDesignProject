package com.MattiaDiMeglio.progettogamedesign;

import java.util.List;
import java.util.Stack;

enum AIType {Dummy, Sniper, Patrol};

public class AIComponent extends Component{

    //Pathfinding Variables
    private Pathfinder pathfinder;
    protected Stack<Movement> movementStack;
    protected int gridSize;

    //AiType
    private AIType aiType;

    //targeting
    boolean playerInRange = false;
    int lastPlayerX, lastPlayerY;
    int enemyTargetX = 0, enemyTargetY = 0;//target coordinates

    //timers variables
    float playerPositionTimer;
    float aimingTimer = 0;
    float shootingTimer = 0;
    float reloadingTimer = 0;

    //delay variables
    final float playerPositionUpdateDelay = 0.2f;
    float aimDelay;
    float shootDelay;
    float reloadDelay;

    public AIComponent(AIType aiType){
        this.aiType = aiType;
        pathfinder = new Pathfinder();
        movementStack = new Stack<>();
        lastPlayerX = 0;
        lastPlayerY = 0;
        playerPositionTimer = 0;
    }

    public void pathfind(int targetX, int targetY, Node[][] cells){
        Node start = findNode(owner.worldX, owner.worldY, gridSize,cells);
        Node target = findNode(targetX, targetY, gridSize, cells);
        Node res = pathfinder.aStar(start, target, aiType);
        List<Node> path =  pathfinder.getPath(res);
        if(path != null)
            initializeStack(path);
    }

    public void initializeStack(List<Node> path){
        if(!movementStack.isEmpty())
            emptyStack();

        // The path returned by the pathfinder is "backwards":
        // if the enemy path is something like: A -> B -> C,
        // the path will be sorted like this: C-B-A.
        // So the last element in path (index = path.size()-1)
        // will be the first node that the enemy will walk across,
        // and it's skipped to avoid strange initial movements

        for(int i = 0; i<(path.size()-1); i++){
            Node n = path.get(i);
                Movement m = new Movement(n.getPosX(),n.getPosY());
                movementStack.push(m);
        }
    }

    public void emptyStack(){
        while(!movementStack.isEmpty())
            movementStack.pop();
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

            // if the enemy has not yet reached the node, it will move to it
            if(deltaX > threshold || deltaY > threshold){
                if(deltaX > threshold)
                    normalX = findNormalX(owner.worldX, owner.worldY, nextX, nextY);
                if(deltaY > threshold)
                    normalY = findNormalY(owner.worldX, owner.worldY, nextX, nextY);
            }
            // else it will move to the next node
            else{
                Movement newMovement = movementStack.pop();
                int newX = newMovement.getCellX();
                int newY = newMovement.getCellY();
                normalX = findNormalX(owner.worldX, owner.worldY , newX, newY);
                normalY = findNormalY(owner.worldX, owner.worldY , newX, newY);
            }
            ((EnemyGameObject) owner).setFacingAngle(-(float) Math.toDegrees(Math.atan2(normalY,normalX)));
            owner.updatePosition(normalX,normalY,((EnemyGameObject) owner).getFacingAngle());

            if(movementStack.isEmpty())
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

    // To get node corresponding to xy coordinates
    public Node findNode(int x, int y, int gridSize, Node[][] cells){
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
        float distanceToPlayer = getDistanceToPlayer();

        if(distanceToPlayer <= range+18){
            enemyAim(enemyWeapon, gameWorld);
            return enemyWeapon.checkLineOfFire(gameWorld);
        }
        return false;
    }

    public void enemyAim(WeaponComponent weaponComponent, GameWorld gameWorld){
        float normalX = findNormalX(owner.worldX, owner.worldY, enemyTargetX, enemyTargetY);
        float normalY = findNormalY(owner.worldX, owner.worldY, enemyTargetX, enemyTargetY);
        ((EnemyGameObject) owner).setFacingAngle(-(float) Math.toDegrees(Math.atan2(normalY,normalX)));
        weaponComponent.aim(normalX,normalY,((EnemyGameObject) owner).getFacingAngle(), gameWorld);
    }

    public void enemyShoot(WeaponComponent weaponComponent, GameWorld gameWorld){
        targetingReset();
        weaponComponent.shoot(gameWorld);
    }

    // used when the enemy goes out screen
    public void reset(){
        targetingReset();
        playerPositionTimer = 0f;
        reloadingTimer = 0f;
        playerInRange = false;

        WeaponComponent weaponComponent = (WeaponComponent) owner.getComponent(ComponentType.Weapon);
        if(weaponComponent.bullets < weaponComponent.mag)
            weaponComponent.reload();

        if(!movementStack.isEmpty())
            emptyStack();
    }

    public void targetingReset(){
        aimingTimer = 0;
        shootingTimer = 0;
        enemyTargetX = 0;
        enemyTargetY = 0;
    }

    public float getDistanceToPlayer() {
        return (float) Math.sqrt(((lastPlayerX - getOwner().worldX) * (lastPlayerX - getOwner().worldX)) +
                ((lastPlayerY - getOwner().worldY) * (lastPlayerY - getOwner().worldY)));
    }

    public void setEnemyTarget(int targetX, int targetY){
        if(enemyTargetX == 0 && enemyTargetY == 0){
            this.enemyTargetX = targetX;
            this.enemyTargetY = targetY;
        }
    }

    @Override
    public ComponentType getType() { return ComponentType.AI; }

    public void setGridSize(int gridSize) { this.gridSize = gridSize; }
}
