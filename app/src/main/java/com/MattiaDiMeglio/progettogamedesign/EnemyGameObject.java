package com.MattiaDiMeglio.progettogamedesign;

//the enemyGo
public class EnemyGameObject extends GameObject {
    private GameWorld gameWorld;//the gameWorld
    private DrawableComponent drawableComponent;//component saved for simplicity
    private DynamicBodyComponent dynamicBodyComponent;
    private ControllableComponent controllableComponent;
    protected boolean killed = false;//has it been killed?

    private float facingAngle = 0f;
    private int previousCellX, previousCellY, currentCellX, currentCellY;

    public EnemyGameObject(GameWorld gameWorld, int worldX, int worldY){//constructor
        this.gameWorld = gameWorld;//gw
        this.name = "Enemy";//name
        //gives the enemy a random position on the background
        this.worldX = worldX;//worldPos are the GO position on the map
        this.worldY = worldY;

        currentCellX = worldX / gameWorld.gridSize;
        currentCellY = worldY / gameWorld.gridSize;
        previousCellX = currentCellX;
        previousCellY = currentCellY;
    }

    //updates the graphical and physical positions
    @Override
    public void updatePosition(int x, int y){
            drawableComponent = (DrawableComponent) components.get(ComponentType.Drawable);
            drawableComponent.setPosition(x, y);
        if(!killed) {
            dynamicBodyComponent = (DynamicBodyComponent) components.get(ComponentType.Physics);

            float touchX = gameWorld.toPixelsTouchX(x);
            float touchY = gameWorld.toPixelsTouchY(y);
            dynamicBodyComponent.setTransform(gameWorld.toMetersX(touchX),
                    gameWorld.toMetersY(touchY));
        }
    }

    public void updatePosition(float x, float y, float angle){
        controllableComponent = (ControllableComponent) components.get(ComponentType.Controllable);
        controllableComponent.moveCharacter(x,y,angle);
    }

    @Override
    public void update() { }

    public void update(int playerX, int playerY, float elapsedTime, Node[][] cells, GameWorld gWorld){
        AIComponent aiComponent = (AIComponent) components.get(ComponentType.AI);

        if(!killed){
            dynamicBodyComponent.update(0, 0);

            dynamicBodyComponent = (DynamicBodyComponent) components.get(ComponentType.Physics);
            float enemySpeed = dynamicBodyComponent.getSpeed();

            updateCells(cells, gWorld);

            aiComponent.updateAI(playerX, playerY, elapsedTime, cells, gWorld);
            aiComponent.movement(enemySpeed, gameWorld);
        }
    }

    @Override//puts enemy out of view
    public void outOfView() {
        if(!isKilled())
            dynamicBodyComponent.setTransform(40, 40);
    }

    public void killed(Node[][] cells){
        if(!killed) {
            gameWorld.enemyNum--;
            AIComponent aiComponent = (AIComponent) getComponent(ComponentType.AI);
            aiComponent.emptyStack();
            freeCurrentCell(cells);
            killed = true;
            PhysicsComponent physicsComponent = (PhysicsComponent) getComponent(ComponentType.Physics);
            physicsComponent.body.destroyFixture(physicsComponent.body.getFixtureList());
            physicsComponent.body.delete();
            removeComponent(ComponentType.Physics);
            removeComponent(ComponentType.Controllable);
            removeComponent(ComponentType.AI);
            ((PixMapComponent)getComponent(ComponentType.Drawable)).pixmap = AssetManager.enemyKilled;
        }
    }

    public void updateCells(Node[][] cells, GameWorld gameWorld){

        currentCellX = worldX / gameWorld.gridSize;
        currentCellY = worldY / gameWorld.gridSize;

        if(!((previousCellX == currentCellX) && (previousCellY == currentCellY))){
            cells[previousCellY][previousCellX].setEnemy(false);
            cells[currentCellY][currentCellX].setEnemy(true);
            previousCellX = currentCellX;
            previousCellY = currentCellY;
        }
    }

    public void freeCurrentCell(Node[][] cells){ cells[currentCellY][currentCellX].setEnemy(false); }

    public boolean isKilled(){return killed;}
    public float getFacingAngle() { return facingAngle; }

    public void setFacingAngle(float facingAngle) { this.facingAngle = facingAngle; }

}
