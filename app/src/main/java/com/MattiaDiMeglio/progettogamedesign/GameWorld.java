package com.MattiaDiMeglio.progettogamedesign;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.impl.TouchHandler;
import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.Draw;
import com.google.fpl.liquidfun.Fixture;
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
    private TouchHandler touchHandler;
    protected List<GameObject> gameObjects;
    private final GameObjectFactory gameObjectFactory;
    private final GameScreen gameScreen;
    protected PlayerGameObject player;
    protected DoorGameObject door;
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

    //for the touch and the raycast
    private Fixture touchedFixture;
    private Fixture rayCastFixture;
    private Body castedBody;
    private final QueryCallback touchQueryCallback = new TouchQueryCallback();

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

        gameObjects = new ArrayList<GameObject>();//list of active game objects
        gameObjectFactory = new GameObjectFactory(this, world);//factory class for the various GO


        bufferWidth = gameScreen.graphics.getWidth();
        bufferHeight = gameScreen.graphics.getHeight();

        //JUST FOR TESTING, creates a player and some GO
        player = (PlayerGameObject) addGameObject(gameObjectFactory.makePlayer(bufferWidth/2, bufferHeight/2));
        gameScreen.addDrawable((DrawableComponent) player.getComponent(ComponentType.Drawable));
        MapManager mapManager = new MapManager(this, gameObjectFactory, context);
        mapManager.makeWalls();
        mapManager.makeEnemies();
    }

    //Game World update, calls the world step, then responds to touch events
    public synchronized void update(float elapsedTime, List<Input.TouchEvent> touchEvents){
        world.step(elapsedTime, VELOCITY_ITERATIONS, POSITION_ITERATION, PARTICLE_ITERATION);
        for(Input.TouchEvent touchEvent : touchEvents){//for each touchevent
            if(touchEvent.type == Input.TouchEvent.TOUCH_DOWN){//if it's a touch down
               checkTouched(touchEvent);
            }
        }//TODO probabilmente da rifare, dato il cambio del sistema di movimento
        for(GameObject gameObject : gameObjects){//for each GO
            gameObject.update();//update TODO probabilmente inutile
            if(!gameObject.name.equals("Player")){//if it's not a player
                if(isInView(gameObject)){//we check is it's in view
                    DrawableComponent component = (DrawableComponent)gameObject.getComponent(ComponentType.Drawable);
                    if(component != null && !gameScreen.drawables.contains(component)) {//we check not to insert a drawable multiple times
                        //inits the position of the GO in view
                        gameObject.updatePosition((int) (inViewPositionX(gameObject.worldX)),
                                (int) (inViewPositionY(gameObject.worldY)));
                        gameScreen.addDrawable(component);
                    }
                } else { //if they're not in view we remove the drawable
                    if(gameScreen.drawables.contains((DrawableComponent)gameObject.getComponent(ComponentType.Drawable))) {
                        gameScreen.removeDrawable((DrawableComponent) gameObject.getComponent(ComponentType.Drawable));
                        gameObject.outOfView();
                    }
                }
            }
        }
        if(!player.canMove())
            gameScreen.worldMovement();
    }

    //methods to add and remove GO
    public synchronized GameObject addGameObject(GameObject gameObject){
        gameObjects.add(gameObject);
        return gameObject;
    }

    public synchronized void removeGameObject(GameObject gameObject){gameObjects.remove(gameObject);}

    private void checkTouched(Input.TouchEvent touchEvent){//check what the player touched
        //gets the physics coordinates of the touch down
        float touchx = toMetersX(toPixelsTouchX(touchEvent.x));
        float touchy = toMetersY(toPixelsTouchY(touchEvent.y));
        //check if the user touched a fixture
        world.queryAABB(touchQueryCallback, touchx - 0.1f, touchy - 0.1f,
                touchx + 0.1f, touchy + 0.1f);
        if(touchedFixture != null){//if they have
            //we get the body and the suerdata
            Body touchedBody = touchedFixture.getBody();
            Object userData = touchedBody.getUserData();
            if(userData != null){//if there are any
                PhysicsComponent touchedGO = (PhysicsComponent) userData;
                Log.d("Touched", "touched: " + touchedGO.name);
                switch (touchedGO.name){
                    case "Enemy"://touched an enemy
                        DrawableComponent playerd = (DrawableComponent)player.getComponent(ComponentType.Drawable);
                        DrawableComponent enemyd = (DrawableComponent)touchedGO.getOwner().getComponent(ComponentType.Drawable);
                        gameScreen.playerx = playerd.getPositionX() + AssetManager.player.getWidth()/2;
                        gameScreen.playery = playerd.getPositionY() + AssetManager.player.getHeight()/2;
                        gameScreen.targetx = enemyd.getPositionX() + AssetManager.enemy.getWidth()/2;
                        gameScreen.targety = enemyd.getPositionY() + AssetManager.enemy.getHeight()/2;

                        checkRaycast(touchedBody);
                        break;
                    case "Door"://touched a door
                        DoorGameObject door = (DoorGameObject) touchedGO.getOwner();
                        Vec2 force = new Vec2();
                        force.set(0, 20000);
                        Vec2 point = new Vec2();
                        point.set(0,0);
                        door.applyForce(force, point);
                        break;
                    case "Player"://touched the player character
                        //reload?
                        break;
                    case "Wall"://touched a wall
                        checkRaycast(touchedBody);
                        break;
                    case "HalfWall":
                        checkRaycast(touchedBody);
                        break;
                    default:
                        Log.d("TouchEvent", "touched object with no name");
                        break;
                }
            }
            touchedFixture = null;
        } else {// if the user doesn't touch a fixture, we move the world
            float resultX = touchx;//checkGridX(touchx);
            float resultY = touchy;//checkGridY(touchy);
            Log.d("touchedBox", "point.x = " + resultX
                    + ", " + resultY);
            //TODO da rifare dato il cambio di sistema
            player.setDestination((int)toPixelsX(resultX), (int)toPixelsY(resultY));
           // CharacterBodyComponent characterBodyComponent = (CharacterBodyComponent) player.getComponent(ComponentType.Physics);
            gameScreen.setWorldDestination((int)(toPixelsXLength(touchx)),
                    (int) toPixelsYLength(touchy));
        }
    }

    private void checkRaycast(Body touchedBody){//does the raycast callback
        CharacterBodyComponent playerBody =(CharacterBodyComponent) player.getComponent(ComponentType.Physics);
        //raycast override. if the cast gets from the player to the enemy, we destroy the enemy
        RayCastCallback rayCastCallback = new RayCastCallback(){
            @Override
            public float reportFixture(Fixture fixture, Vec2 point, Vec2 normal, float fraction) {
                rayCastFixture = fixture;//raycast callback
                Body castedBody = fixture.getBody();
                PhysicsComponent casteduserData = (PhysicsComponent) castedBody.getUserData();


       //         if(casteduserData.name.equals("HalfWall")){
         //           return -1;
            //    } else {
                    return fraction;//stops at the first hit/
              //  }
            }
        };
        world.rayCast(rayCastCallback, playerBody.getPositionX(), playerBody.getPositionY(),
                touchedBody.getPositionX(), touchedBody.getPositionY());//calls the raycast
        if(rayCastFixture != null){//if the ray met a fixture
            Body castedBody = rayCastFixture.getBody();//we get the body
            PhysicsComponent casteduserData = (PhysicsComponent) castedBody.getUserData();//we get the component
            if(casteduserData != null){//if there's user data
                Log.d("Raycast", "hit : " + casteduserData.name);
                switch(casteduserData.name){
                    case "Enemy"://raycast met an enemy first
                        EnemyGameObject enemyGameObject = (EnemyGameObject) casteduserData.getOwner();
                        enemyGameObject.killed();
                        //destroy enemy
                        break;
                    case "Wall"://met a wall
                        //hit wall
                        break;
                    case "Door"://met a door
                        //open door
                        break;
                    case "HalfWall":
                        Object userData = touchedBody.getUserData();
                        PhysicsComponent touchedGO = (PhysicsComponent) userData;
                        if (touchedGO.name.equals("Enemy")){
                            enemyGameObject = (EnemyGameObject) touchedGO.getOwner();
                            enemyGameObject.killed();
                        }
                        break;
                    default:
                        Log.d("RaycastEvent", "raycast object with no name");
                        break;
                }
            }
        }
    }

    //TODO si possono levare direttamente dato il cambio di sistema
    //we report every touch to the center of the nearest grid block
    public float checkGridX(float x){
        float i = physicalSize.xmin;
        float sup  = i;
        while(i < (physicalSize.xmax + 0.1f)){
            if(x >= sup && x < i){
                return sup + (gameScreen.orizontalFactor/2);
            }
            sup = i;
            i += gameScreen.orizontalFactor;
        }
        return 0f;
    }
    public float checkGridY(float y){
        float i = physicalSize.ymin;
        float sup = i;
        while(i < physicalSize.ymax + 0.1f){
            if(y >= sup && y < i){
                return sup + (gameScreen.verticalFactor/2);
            }
            sup = i;
            i += gameScreen.verticalFactor;
        }
        return 0f;
    }

    //to check if a GO is in view, based on it's world coordinates
    private boolean isInView(GameObject gameObject){
        PixMapComponent drawableComponent = (PixMapComponent) gameObject.getComponent(ComponentType.Drawable);
        if(gameObject.worldX + (drawableComponent.pixmap.getWidth()/2) > -gameScreen.getBackgroundX()
                && gameObject.worldX - (drawableComponent.pixmap.getWidth()/2) < -(gameScreen.currentBackgroundX - bufferWidth)
                && gameObject.worldY + (drawableComponent.pixmap.getHeight()/2) > - gameScreen.getBackgroundY()
                && gameObject.worldY - (drawableComponent.pixmap.getHeight()/2) < -((gameScreen.currentBackgroundY) - bufferHeight)){
            return true;
        }
        return false;
    }

    //conversion methods
    public int inViewPositionX(float worldX){return(int) worldX + (gameScreen.currentBackgroundX);}
    public int inViewPositionY(float worldY){return(int) worldY + (gameScreen.currentBackgroundY);}

    public float toMetersX(float x){return currentView.xmin + x * (currentView.width/screenSize.width);}
    public float toMetersY(float y){return currentView.ymin + y * (currentView.height/screenSize.height);}

    public float toPixelsX(float x){return (x-currentView.xmin)/currentView.width*bufferWidth;}
    public float toPixelsY(float y){return (y-currentView.ymin)/currentView.height*bufferHeight;}

    public float toPixelsXLength(float x){return x/currentView.width*bufferWidth;}
    public float toPixelsYLength(float y){return y/currentView.height*bufferHeight;}

    public float toMetersXLength(float x){return x * currentView.width/bufferWidth;}
    public float toMetersYLength(float y){return y * currentView.height/bufferHeight;}

    public float toPixelsTouchX(float x){return x / (gameScreen.graphics.getWidth()/screenSize.width);}
    public float toPixelsTouchY(float y){return y / (gameScreen.graphics.getHeight()/screenSize.height);}

    public float toPixelScaleX(float x){return x * (gameScreen.graphics.getWidth()/screenSize.width);}
    public float toPixelScaleY(float y){return y * (gameScreen.graphics.getHeight()/screenSize.height);}
}
