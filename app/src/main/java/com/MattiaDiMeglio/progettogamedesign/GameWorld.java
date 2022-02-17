package com.MattiaDiMeglio.progettogamedesign;

import android.content.Context;
import android.util.Log;

import com.badlogic.androidgames.framework.Game;
import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.Draw;
import com.google.fpl.liquidfun.Fixture;
import com.google.fpl.liquidfun.ParticleSystem;
import com.google.fpl.liquidfun.ParticleSystemDef;
import com.google.fpl.liquidfun.QueryCallback;
import com.google.fpl.liquidfun.RayCastCallback;
import com.google.fpl.liquidfun.Vec2;
import com.google.fpl.liquidfun.World;

import java.util.ArrayList;
import java.util.List;

//Gestione degli elementi in gioco
public class GameWorld {
    private Game game;
    protected World world;
    protected List<GameObject> gameObjects;
    protected List<GameObject> activeGameObjects;
    //private final GameObjectFactory gameObjectFactory;
    public GameScreen gameScreen;
    private PhysicsContactListener contactListener;
    protected PlayerGameObject player;
    public mRayCastCallback rayCastCallback;
    Draw draw;

    //AndroidGraphics buffer
    int bufferWidth, bufferHeight;

    Context context;

    final Box physicalSize, screenSize, currentView;

    //physics sim parameter
    private static final float TIME_STEP = 1 /60f;
    private static final int VELOCITY_ITERATIONS = 8;
    private static final int POSITION_ITERATION = 3;
    private static final int PARTICLE_ITERATION = 3;
    ParticleSystem particleSystem;
    private static final int MAXPARTICLECOUNT = 1000;
    private static final float PARTICLE_RADIUS = 0.3f;


    //for the touch and the raycast
    private Fixture touchedFixture;
    private Fixture rayCastFixture;
    private Fixture lineOfFireFixture;
    private Body castedBody;
    private final QueryCallback touchQueryCallback = new TouchQueryCallback();

    //grid variables and parameters
    GridManager levelGrid;
    int gridSize = 42;
    EnemyGameObject testEnemy, testEnemy2; // pathfinding test

    int[][]mapCells;

    int enemyNum;
    int totalEnemies;
    int level = 1;

    //to get the touched fixture
    private class TouchQueryCallback extends QueryCallback
    {
        public boolean reportFixture(Fixture fixture) {
            touchedFixture = fixture;
            return true;
        }
    }

    public GameWorld(GameScreen gameScreen, Context context, Box physicalSize, Box screenSize){
        //sizes
        this.physicalSize = physicalSize;
        this.screenSize = screenSize;
        this.currentView = physicalSize;
        this.gameScreen = gameScreen;//the main game screen
        this.world = new World(0, 0);//new phyisics world
        this.context = context;
        ParticleSystemDef ps = new ParticleSystemDef();
        particleSystem = world.createParticleSystem(ps);
        particleSystem.setRadius(PARTICLE_RADIUS);
        particleSystem.setMaxParticleCount(MAXPARTICLECOUNT);
        ps.delete();

        contactListener = new PhysicsContactListener();
        world.setContactListener(contactListener);
        rayCastCallback = new mRayCastCallback(world);
        gameObjects = new ArrayList<GameObject>();//list of all game objects
        activeGameObjects = new ArrayList<GameObject>(); //list of on-screen game objects
        //gameObjectFactory = new GameObjectFactory(this, world);//factory class for the various GO

        bufferWidth = gameScreen.graphics.getWidth();
        bufferHeight = gameScreen.graphics.getHeight();

        //JUST FOR TESTING, creates a player and some GO
        //player = (PlayerGameObject) addActiveGameObject(gameObjectFactory.makePlayer(bufferWidth/2, bufferHeight/2));
        //gameScreen.addDrawable((DrawableComponent) player.getComponent(ComponentType.Drawable));

        //int testEnemyX = 250;
        //int testEnemyY = 300;
        //testEnemy = (EnemyGameObject) gameObjectFactory.makeEnemy(testEnemyX,testEnemyY,AIType.Dummy);
        //addGameObject(testEnemy);

        //MapManager mapManager = new MapManager(this, gameObjectFactory, context);
        //mapCells = mapManager.initMapResized(mapCells, AssetManager.backgroundPixmap.getWidth()/AssetManager.WallPixmap.getWidth(),
       //         AssetManager.backgroundPixmap.getHeight()/AssetManager.WallPixmap.getWidth());
        //mapCells = mapManager.generateMapResized(mapCells, 0, 0, AssetManager.backgroundPixmap.getWidth()/AssetManager.WallPixmap.getWidth()-1,
         //       AssetManager.backgroundPixmap.getHeight()/AssetManager.WallPixmap.getWidth()-1, (Math.random() * 6) % 2 == 0);

        //mapManager.constructMap(mapCells, 50, 50);
        //mapManager.makeEnemies();

        //int levelWidth = AssetManager.backgroundPixmap.getWidth();
        //int levelHeight = AssetManager.backgroundPixmap.getHeight();
        //levelGrid = new GridManager(levelWidth, levelHeight, gridSize, this);

        /*int boxX = 63;
        int boxY = 231;
        for(int i = 0; i < 7; i++){
            addGameObject(gameObjectFactory.makeBox(boxX + (i * 42), boxY));
            if(i == 6){
                for(int j = 1; j < 5; j++)
                    if(j!=3)
                    addGameObject(gameObjectFactory.makeBox(boxX + (i * 42), boxY - (j * 42)));
            }
        }*/

        //levelGrid.addObstacles(gameObjects, this);
    }


    //Game World update, calls the world step, then responds to touch events

    public synchronized void update(float leftX, float leftY, float elapsedTime, float rightX,
                                    float rightY, float rightAngle, float rightStrength, boolean isShooting){
        if(enemyNum == 0)
            gameScreen.levelEnded();
        world.step(elapsedTime, VELOCITY_ITERATIONS, POSITION_ITERATION, PARTICLE_ITERATION);
        //Log.d("touched", "" + player.isInContact());
        for(GameObject gameObject : activeGameObjects){
            switch(gameObject.name){
                case "Player":
                    player.update(rightStrength,rightX,rightY,rightAngle,isShooting,this, elapsedTime);
                    break;
                case "Enemy":
                    EnemyGameObject enemyGameObject = (EnemyGameObject) gameObject;
                    enemyGameObject.update(player.worldX, player.worldY, elapsedTime, levelGrid.getCells(),this);
                    break;
                case "MovableBox":
                    MovableBoxGameObject movableBoxGameObject = (MovableBoxGameObject) gameObject;
                    movableBoxGameObject.update(levelGrid.getCells(), this);
                default:
                    gameObject.update();
                    break;
            }
        }

        checkOutOfBound();

        gameScreen.setWorldDestination(leftX, leftY, elapsedTime);
    }

    private void checkOutOfBound(){
        for(GameObject gameObject : gameObjects){//for each GO
            if(!gameObject.name.equals("Player")){//if it's not a player
                if(isInView(gameObject) ){//we check is it's in view
                    if(!activeGameObjects.contains(gameObject)) {
                        if (gameObject.name.equals("Enemy")) {
                            EnemyGameObject enemyGameObject = (EnemyGameObject) gameObject;
                            if (!enemyGameObject.isKilled()) {
                                addActiveGameObject(gameObject);
                            }
                        } else {
                            addActiveGameObject(gameObject);
                        }
                        DrawableComponent component = (DrawableComponent) gameObject.getComponent(ComponentType.Drawable);
                        if (component != null && !gameScreen.drawables.contains(component)) {//we check not to insert a drawable multiple times
                            //inits the position of the GO in view
                            gameObject.updatePosition((int) (inViewPositionX(gameObject.worldX)),
                                    (int) (inViewPositionY(gameObject.worldY)));
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

                    if(gameScreen.drawables.contains((DrawableComponent)gameObject.getComponent(ComponentType.Drawable))) {
                        gameScreen.removeDrawable((DrawableComponent) gameObject.getComponent(ComponentType.Drawable));
                        gameObject.outOfView();
                    }
                }
            }
        }
    }


    //methods to add and remove GO
    public synchronized GameObject addGameObject (GameObject gameObject){
        gameObjects.add(gameObject);
        return gameObject;
    }

    public synchronized GameObject addActiveGameObject (GameObject gameObject){
        activeGameObjects.add(gameObject);
        return gameObject;
    }

    public synchronized void removeGameObject (GameObject gameObject){
        //gameScreen.removeDrawable((DrawableComponent) gameObject.getComponent(ComponentType.Drawable));
        gameObjects.remove(gameObject);
    }

    public synchronized void removeActiveGameObject (GameObject gameObject){
        activeGameObjects.remove(gameObject);
    }

    //to check if a GO is in view, based on it's world coordinates
    private boolean isInView (GameObject gameObject){
        PixMapComponent drawableComponent = (PixMapComponent) gameObject.getComponent(ComponentType.Drawable);
        if (gameObject.worldX + (drawableComponent.pixmap.getWidth() / 2) > -gameScreen.getBackgroundX()
                && gameObject.worldX - (drawableComponent.pixmap.getWidth() / 2) < -(gameScreen.currentBackgroundX - bufferWidth)
                && gameObject.worldY + (drawableComponent.pixmap.getHeight() / 2) > -gameScreen.getBackgroundY()
                && gameObject.worldY - (drawableComponent.pixmap.getHeight() / 2) < -((gameScreen.currentBackgroundY) - bufferHeight)) {
            return true;
        }
        return false;
    }

    //called by gamescreen, calls movement in playergo
    public void movePlayer (float normalizedX, float normalizedY, float rightAngle, float leftAngle, float deltaTime){
        player.updatePosition(normalizedX, normalizedY, rightAngle, leftAngle, deltaTime);
    }

    public void killPlayer(){
        player.killed();
    }

    public void addAimLine(int lineAmt, float sx, float sy, float[] aimLineX, float[] aimLineY, int color){

        int startX = (int) toPixelsX(sx);
        int startY = (int) toPixelsY(sy);
        int targetX = 0;
        int targetY = 0;

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

    public float toPixelsXLengthNonBuffer(float x){return x/currentView.width*screenSize.width;}
    public float toPixelsYLengthNonBuffer(float y){return y/currentView.height*screenSize.height;}

    public float toMetersXLength(float x){return x * currentView.width/bufferWidth;}
    public float toMetersYLength(float y){return y * currentView.height/bufferHeight;}

    public float pixelsToMetersLengthX ( float x){return x * currentView.width / screenSize.width;}
    public float pixelsToMetersLengthY ( float y){return y * currentView.height / screenSize.height;}

    public float toPixelsTouchX ( float x){return x / (gameScreen.graphics.getWidth() / screenSize.width);}
    public float toPixelsTouchY ( float y){return y / (gameScreen.graphics.getHeight() / screenSize.height);}

    public float toPixelScaleX ( float x){ return x * (gameScreen.graphics.getWidth() / screenSize.width); }
    public float toPixelScaleY ( float y){return y * (gameScreen.graphics.getHeight() / screenSize.height);}


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
        //gameScreen.drawables.clear();
        activeGameObjects.clear();
        gameObjects.clear();
    }
}

