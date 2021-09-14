package com.MattiaDiMeglio.progettogamedesign;

import android.util.Log;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.impl.TouchHandler;
import com.google.fpl.liquidfun.Body;
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
    private World world;
    private TouchHandler touchHandler;
    private List<GameObject> gameObjects;
    private GameObjectFactory gameObjectFactory;
    private GameScreen gameScreen;
    protected PlayerGameObject player;

    //AndroidGraphics buffer
    int bufferWidth, bufferHeight;

    final Box physicalSize, screenSize, currentView;

    //physics sim parameter
    private static final float TIME_STEP = 1 /50f;
    private static final int VELOCITY_ITERATIONS = 8;
    private static final int POSITION_ITERATION = 3;
    private static final int PARTICLE_ITERATION = 3;

    //for the touch and the raycast
    private Fixture touchedFixture;
    private Body castedBody;
    private QueryCallback touchQueryCallback = new TouchQueryCallback();

    //to get the touched fixture
    private class TouchQueryCallback extends QueryCallback
    {
        public boolean reportFixture(Fixture fixture) {
            touchedFixture = fixture;
            return true;
        }
    }

    public GameWorld(GameScreen gameScreen, Box physicalSize, Box screenSize){
        //sizes
        this.physicalSize = physicalSize;
        this.screenSize = screenSize;
        this.currentView = physicalSize;
        this.gameScreen = gameScreen;//the main game screen
        this.world = new World(0, 0);//new phyisics world

        gameObjects = new ArrayList<GameObject>();//list of active game objects
        gameObjectFactory = new GameObjectFactory(this, world);//factory class for the various GO


        bufferWidth = gameScreen.graphics.getWidth();
        bufferHeight = gameScreen.graphics.getHeight();

        //JUST FOR TESTING, creates a player and some GO
        player = (PlayerGameObject) addGameObject(gameObjectFactory.makePlayer(bufferWidth/2, bufferHeight/2));
        gameScreen.addDrawable((DrawableComponent) player.getComponent(ComponentType.Drawable));
        addGameObject(gameObjectFactory.makeEnemy());
        addGameObject(gameObjectFactory.makeWall());
    }

    //Game World update, calls the world step, then responds to touch events
    public void update(float elapsedTime, List<Input.TouchEvent> touchEvents){
        world.step(elapsedTime, VELOCITY_ITERATIONS, POSITION_ITERATION, PARTICLE_ITERATION);
        for(Input.TouchEvent touchEvent : touchEvents){//for each touchevent
            if(touchEvent.type == Input.TouchEvent.TOUCH_DOWN){//if it's a touch down
                //gets the physics coordinates of the touch down
                float touchx = toMetersX(toPixelsTouchX(touchEvent.x));
                float touchy = toMetersY(toPixelsTouchY(touchEvent.y));
                //check if the user touched a fixture
                world.queryAABB(touchQueryCallback, touchx - 0.5f, touchy - 0.5f,
                        touchx + 0.5f, touchy + 0.5f);
                if(touchedFixture != null){//if they have
                    //we get the body and the suerdata
                    Body touchedBody = touchedFixture.getBody();
                    Object userData = touchedBody.getUserData();
                    if(userData != null){//if there are any
                        PhysicsComponent touchedGO = (PhysicsComponent) userData;
                        Log.d("touchevent", "'touched game object " + touchedGO.name);
                        if(touchedGO.name.equals("Enemy")){//if the user touched an enemy
                            CharacterBodyComponent playerBody =(CharacterBodyComponent) player.getComponent(ComponentType.Physics);
                            //raycast override. if the cast gets from the player to the enemy, we destroy the enemy
                            RayCastCallback rayCastCallback = new RayCastCallback(){
                                @Override
                                public float reportFixture(Fixture fixture, Vec2 point, Vec2 normal, float fraction) {
                                        Object userData = fixture.getBody().getUserData();
                                        if(userData != null){
                                           PhysicsComponent castedGO = (PhysicsComponent) userData;
                                           if(castedGO.name.equals(touchedGO.name)){
                                               gameScreen.removeDrawable((DrawableComponent)castedGO.getOwner().getComponent(ComponentType.Drawable));
                                               removeGameObject(castedGO.getOwner());

                                           }
                                        }
                                    return 1;
                                }
                            };
                            world.rayCast(rayCastCallback,playerBody.getPositionX(), playerBody.getPositionY(),
                                    touchedBody.getPositionX(), touchedBody.getPositionY());

                        }
                    }
                    touchedFixture = null;
                } else {// if the user doens't touch a fixture, we move the world
                    float resultX = checkGridX(touchx);
                    float resultY = checkGridY(touchy);
                    Log.d("touchedBox", "point.x = " + resultX
                            + ", " + resultY);
                    gameScreen.setWorldDestination((int) toPixelsYLength(resultX), (int) toPixelsYLength(resultY));
                    //player.updatePosition((int)toPixelsX(resultX), (int)toPixelsY(resultY));
                }
            }
        }
        for (GameObject gameObject: gameObjects) {//then for each game object
            if(!gameObject.name.equals("Player")){//if it's not a player
                if(isInView(gameObject)){//we check is it's in view
                    DrawableComponent component = (DrawableComponent)gameObject.getComponent(ComponentType.Drawable);
                    if(!gameScreen.drawables.contains(component)) {//we check not to insert a drawable multiple times
                        //inits the position of the GO in view
                        gameObject.updatePosition((int) (inViewPositionX(gameObject.worldX)),
                                (int) (inViewPositionY(gameObject.worldY)));
                        gameScreen.addDrawable(component);
                    }
                    } else { //if they're not in view we remove the drawable
                        if(gameScreen.drawables.contains((DrawableComponent)gameObject.getComponent(ComponentType.Drawable)))
                            gameScreen.removeDrawable((DrawableComponent)gameObject.getComponent(ComponentType.Drawable));
                }
            }
        }

    }

    //methods to add and remove GO
    public synchronized GameObject addGameObject(GameObject gameObject){
        gameObjects.add(gameObject);
        return gameObject;
    }

    public synchronized void removeGameObject(GameObject gameObject){
        if(gameObjects.contains(gameObject)){
            gameObjects.remove(gameObject);
        }
    }

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
        if(gameObject.worldX > -gameScreen.getBackgroundX()
                && gameObject.worldX < -(gameScreen.currentBackgroundX - bufferWidth)
                && gameObject.worldY > - gameScreen.getBackgroundY()
                && gameObject.worldY < -((gameScreen.currentBackgroundY) - bufferHeight)){
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

    public float toMetersXLength(float x){return x*currentView.width/bufferWidth;}
    public float toMetersYLength(float y){return y* currentView.height/bufferHeight;}

    public float toPixelsTouchX(float x){return x / (gameScreen.graphics.getWidth()/screenSize.width);}
    public float toPixelsTouchY(float y){return y / (gameScreen.graphics.getHeight()/screenSize.height);}

    public float toPixelScaleX(float x){return x * (gameScreen.graphics.getWidth()/screenSize.width);}
    public float toPixelScaleY(float y){return y * (gameScreen.graphics.getHeight()/screenSize.height);}
}
