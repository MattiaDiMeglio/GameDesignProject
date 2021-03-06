package com.MattiaDiMeglio.progettogamedesign;

import android.content.Context;

import com.google.fpl.liquidfun.World;

import java.util.ArrayList;
import java.util.List;

////Managing in-game elements
public class GameWorld {
    protected World world;
    public GameScreen gameScreen;
    int bufferWidth, bufferHeight;

    //GO pool
    protected List<GameObject> gameObjects;
    protected List<GameObject> activeGameObjects;

    public mRayCastCallback rayCastCallback;
    protected PlayerGameObject player;

    Context context;

    final Box physicalSize, screenSize, currentView;

    //physics sim parameter
    private static final int VELOCITY_ITERATIONS = 8;
    private static final int POSITION_ITERATION = 3;
    private static final int PARTICLE_ITERATION = 3;

    //grid variables and parameters
    GridManager levelGrid;
    int gridSize = 42;

    int[][]mapCells;

    int enemyNum;
    int totalEnemies;
    int level = 1;

    public GameWorld(GameScreen gameScreen, Context context, Box physicalSize, Box screenSize){
        //sizes
        this.physicalSize = physicalSize;
        this.screenSize = screenSize;
        this.currentView = physicalSize;
        this.gameScreen = gameScreen;//the main game screen
        this.world = new World(0, 0);//new physics world
        this.context = context;

        rayCastCallback = new mRayCastCallback(world, this);
        gameObjects = new ArrayList<>();//list of all game objects
        activeGameObjects = new ArrayList<>(); //list of on-screen game objects
        bufferWidth = gameScreen.graphics.getWidth();
        bufferHeight = gameScreen.graphics.getHeight();
    }

    //Game World update, calls the world step, then responds to touch events
    public synchronized void update(float elapsedTime){
        if(enemyNum == 0 || player.killed)
            gameScreen.levelEnded();
        world.step(elapsedTime, VELOCITY_ITERATIONS, POSITION_ITERATION, PARTICLE_ITERATION);
        for(GameObject gameObject : activeGameObjects){
            switch(gameObject.name){
                case "Player":
                    player.updatePosition(gameScreen.getLeftX(), gameScreen.getLeftY(), gameScreen.getRightAngle(), gameScreen.getLeftAngle());
                    if(gameScreen.isShooting())
                        player.update(gameScreen.getOldRightStrength(), gameScreen.getOldRightX(), gameScreen.getOldRightY(), gameScreen.getOldRightAngle(), gameScreen.isShooting(), this, elapsedTime);
                    else
                        player.update(gameScreen.getRightStrength(), gameScreen.getRightX(), gameScreen.getRightY(), gameScreen.getRightAngle(), gameScreen.isShooting(), this, elapsedTime);
                    break;
                case "Enemy":
                    EnemyGameObject enemyGameObject = (EnemyGameObject) gameObject;
                    enemyGameObject.update(player.worldX, player.worldY, elapsedTime, levelGrid.getCells(),this);
                    break;
                case "MovableBox":
                    MovableBoxGameObject movableBoxGameObject = (MovableBoxGameObject) gameObject;
                    movableBoxGameObject.update(levelGrid.getCells());
                default:
                    gameObject.update();
                    break;
            }
        }
        checkOutOfBound();
    }

    private void checkOutOfBound(){
        for(GameObject gameObject : gameObjects){//for each GO
            if(!gameObject.name.equals("Player")){//if it's not a player
                if(isInView(gameObject) ){//we check is it's in view
                    if(!activeGameObjects.contains(gameObject)) {
                        if (gameObject.name.equals("Enemy")) {
                            EnemyGameObject enemyGameObject = (EnemyGameObject) gameObject;
                            if (!enemyGameObject.isKilled())
                                addActiveGameObject(gameObject);
                        } else
                            addActiveGameObject(gameObject);

                        DrawableComponent component = (DrawableComponent) gameObject.getComponent(ComponentType.Drawable);
                        if (component != null && !gameScreen.drawables.contains(component)) {//we check not to insert a drawable multiple times
                            //inits the position of the GO in view
                            gameObject.updatePosition(inViewPositionX(gameObject.worldX),
                                    inViewPositionY(gameObject.worldY));
                            gameScreen.addDrawable(component);
                        }
                    }
                } else { //if they're not in view we remove the drawable
                    activeGameObjects.remove(gameObject);
                    if(gameObject.name.equals("Enemy")){
                        if(!((EnemyGameObject)gameObject).killed) {
                            AIComponent aiComponent = (AIComponent) gameObject.getComponent(ComponentType.AI);
                            aiComponent.reset();
                        }
                    }
                    if(gameScreen.drawables.contains(gameObject.getComponent(ComponentType.Drawable))) {
                        gameScreen.removeDrawable((DrawableComponent) gameObject.getComponent(ComponentType.Drawable));
                        gameObject.outOfView();
                    }
                }
            }
        }
    }

    //methods to add and remove GO
    public synchronized void addGameObject (GameObject gameObject){
        gameObjects.add(gameObject);
    }

    public synchronized void removeGameObject (GameObject gameObject){
        gameObjects.remove(gameObject);
    }

    public synchronized GameObject addActiveGameObject (GameObject gameObject){
        activeGameObjects.add(gameObject);
        return gameObject;
    }

    //to check if a GO is in view, based on its world coordinates
    private boolean isInView (GameObject gameObject){
        PixMapComponent drawableComponent = (PixMapComponent) gameObject.getComponent(ComponentType.Drawable);
        return gameObject.worldX + (drawableComponent.pixmap.getWidth() / 2) > -gameScreen.getBackgroundX()
                && gameObject.worldX - (drawableComponent.pixmap.getWidth() / 2) < -(gameScreen.currentBackgroundX - bufferWidth)
                && gameObject.worldY + (drawableComponent.pixmap.getHeight() / 2) > -gameScreen.getBackgroundY()
                && gameObject.worldY - (drawableComponent.pixmap.getHeight() / 2) < -((gameScreen.currentBackgroundY) - bufferHeight);
    }

    public void killPlayer(){ player.killed(); }

    public void addAimLine(int lineAmt, float sx, float sy, float[] aimLineX, float[] aimLineY, int color){

        int startX = (int) toPixelsX(sx);
        int startY = (int) toPixelsY(sy);
        int targetX;
        int targetY;

        for(int i = 0; i < lineAmt; i++){
            targetX = (int)(toPixelsX(aimLineX[i] + sx));
            targetY = (int)(toPixelsY(aimLineY[i] + sy));
            AimLine aimLine = new AimLine(startX, startY, targetX, targetY, color);
            gameScreen.aimLineStack.push(aimLine);
        }
    }

    public int updateWorldX (float pixmapX){ return (int) (pixmapX - gameScreen.getBackgroundX()); }
    public int updateWorldY (float pixmapY){ return (int) (pixmapY - gameScreen.getBackgroundY()); }

    //conversion methods
    public int inViewPositionX ( float worldX){return (int) worldX + (gameScreen.currentBackgroundX);}
    public int inViewPositionY ( float worldY){return (int) worldY + (gameScreen.currentBackgroundY);}

    public float toMetersX ( float x){return currentView.xmin + x * (currentView.width / screenSize.width);}
    public float toMetersY ( float y){return currentView.ymin + y * (currentView.height / screenSize.height);}

    public float toPixelsX ( float x){return (x - currentView.xmin) / currentView.width * bufferWidth;}
    public float toPixelsY ( float y){return (y - currentView.ymin) / currentView.height * bufferHeight;}

    public float toPixelsXLength ( float x){
        return x / currentView.width * bufferWidth;
    }
    public float toPixelsYLength ( float y){
        return y / currentView.height * bufferHeight;
    }

    public float toMetersXLength(float x){return x * currentView.width/bufferWidth;}
    public float toMetersYLength(float y){return y * currentView.height/bufferHeight;}

    public float toPixelsTouchX ( float x){return x / (gameScreen.graphics.getWidth() / screenSize.width);}
    public float toPixelsTouchY ( float y){return y / (gameScreen.graphics.getHeight() / screenSize.height);}

    public void destroyGameWorld(){
        for (GameObject gameObject: gameObjects) {
            if(gameObject.components.containsKey(ComponentType.Physics)) {
                PhysicsComponent physicsComponent = (PhysicsComponent) gameObject.getComponent(ComponentType.Physics);
                physicsComponent.body.destroyFixture(physicsComponent.body.getFixtureList());
                physicsComponent.body.delete();
            }
            gameScreen.removeDrawable((DrawableComponent) gameObject.getComponent(ComponentType.Drawable));
            gameObject.removeComponent(ComponentType.Physics);
            gameObject.removeComponent(ComponentType.Drawable);
            gameObject.removeComponent(ComponentType.Weapon);
            gameObject.removeComponent(ComponentType.AI);
            gameObject.removeComponent(ComponentType.Controllable);
            gameObject.removeComponent(ComponentType.Joint);
        }
        activeGameObjects.clear();
        gameObjects.clear();
    }
}

