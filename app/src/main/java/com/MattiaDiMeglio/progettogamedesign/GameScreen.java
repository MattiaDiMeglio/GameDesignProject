package com.MattiaDiMeglio.progettogamedesign;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Screen;
import com.badlogic.androidgames.framework.impl.AndroidFastRenderView;

import java.util.ArrayList;
import java.util.List;

import io.github.controlwear.virtual.joystick.android.JoystickView;


//The main game screen
//everything graphical and the actual touch input is implemented here
public class GameScreen extends Screen {
    enum GameState {//game states
        Ready,
        Running,
        Paused,
        GameOver
    }
    static GameWorld gameWorld;//GW
    Graphics graphics;
    List<DrawableComponent> drawables;
    GameState gameState = GameState.Ready;
    Box physicalSize, screenSize;
    AndroidFastRenderView renderView;
    JoystickView leftJoystick, rightJoystick;
//
    private static final float XMIN = -10, XMAX = 10, YMIN = -15, YMAX = 15;//physics world dimensions

    //background coordinates to move the world
    int destinationX = 0, destinationY = 0, currentBackgroundX = 0, currentBackgroundY = 0;
    float percentage = 0f;//movement percentage to lerp
    //division factor for the grid
    float orizontalFactor;
    float verticalFactor;
    //player looking direction
    float initialPlayerLookX, initialPlayerLookY;
    boolean onBorderX = false, onBorderY = false;
    float scale;
    Context context;//android context
    int playerx = 0, playery = 0;
    int[] targetx, targety;
    int lineAmount = 0;

    int leftX = 50, leftY = 50, leftAngle = 0, leftStrength = 0;
    int rightX = 50, rightY = 50, rightAngle = 0, rightStrength = 0, oldRightAngle = 0, oldRightStrength = 0;

    boolean isShooting = false;

    int movementDistance = 5;

    public GameScreen(Game game, int width, int height, Context context) {
        super(game);
        this.context = context;

        graphics = game.getGraphics();//we get the graphics from the framework, to draw on screen
        //world sizes
        physicalSize = new Box(XMIN, YMIN, XMAX, YMAX);
        screenSize   = new Box(0, 0, width,height);
        //list of active drawables
        drawables = new ArrayList<DrawableComponent>();
        gameWorld = new GameWorld(this, context, physicalSize, screenSize);//creates a new GameWorld

        orizontalFactor = gameWorld.toMetersXLength(AssetManager.player.getWidth());// 20f/13f;
        verticalFactor = gameWorld.toMetersYLength(AssetManager.player.getHeight());// 30f/20f;
        //initial direction
        initialPlayerLookY = graphics.getHeight();
        initialPlayerLookX = 0;
        renderView = game.getRenderView();

        leftJoystick = game.getLeftJoystick();
        rightJoystick = game.getRightJoystick();

        leftJoystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                leftX = leftJoystick.getNormalizedX();
                leftY = leftJoystick.getNormalizedY();
                leftAngle = angle;
                leftStrength = strength;
            }
        });

        rightJoystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                rightX = rightJoystick.getNormalizedX();
                rightY = rightJoystick.getNormalizedY();
                rightAngle = angle;
                rightStrength = strength;

                if(!(rightX == 50 && rightY == 50)){
                    oldRightAngle = angle;
                    oldRightStrength = strength;
                }
                else isShooting = true;

            }
        });

        targetx = new int[10];
        targety = new int[10];

        }

    //gamescreen update, calls the gameworld update
    @Override
    public void update(float deltaTime) {
        game.getInput().getKeyEvents();

        switch(gameState) {
            case Ready:
                gameState = GameState.Running;
                break;
            case Running: //if the game is running update the gameworld
                gameWorld.movePlayer(leftX, leftY, rightAngle, leftAngle, deltaTime);
                if(isShooting){
                    gameWorld.update(leftX, leftY, deltaTime, oldRightAngle, oldRightStrength, isShooting);
                    isShooting = false;
                }

                else gameWorld.update(leftX, leftY, deltaTime, rightAngle, rightStrength, isShooting);
                break;
            case Paused:
                break;
            case GameOver:
                break;
        }

    }

    //the methods to draw on screen
    @Override
    public void present(float deltaTime) {
        graphics.clear(Color.WHITE);
        //Background and other objects movements
        //if(!onBorders)
           // worldMovement();//we move the world
        //draw the background
        graphics.drawPixmap(AssetManager.background, (int)currentBackgroundX, (int)currentBackgroundY);
        //draw the grid
        graphics.drawLine((int)gameWorld.toPixelsX(0), (int)gameWorld.toPixelsY(-15f),
                (int)gameWorld.toPixelsX(0), (int)gameWorld.toPixelsY(15), Color.WHITE);
       /* float i = XMIN;
        while(i < XMAX){
            graphics.drawRect((int)gameWorld.toPixelsX(i), 0, 2, gameWorld.bufferHeight, Color.BLACK);
            i += orizontalFactor;
        }
        i = YMIN;
        while(i < YMAX) {
            graphics.drawRect(0, (int) gameWorld.toPixelsY(i), gameWorld.bufferWidth, 2, Color.BLACK);
            i += verticalFactor;
        }*/
        //graphics.drawRect(resultx, resulty,(int) gameWorld.toPixelsXLength(0.5f),(int)gameWorld.toPixelsYLength(0.5f), Color.BLACK );
        //draw the drawables
        if(!drawables.isEmpty()) {
            for (DrawableComponent drawable : drawables) {
                drawable.Draw(graphics);
            }
            //for testing, draws the player body
            //gameWorld.player.draw(graphics, gameWorld);
           // gameWorld.door.draw(graphics, gameWorld);
           // for(GameObject gameObject : gameWorld.gameObjects){
             //   PhysicsComponent comp = (PhysicsComponent) gameObject.getComponent(ComponentType.Physics);
            //    int color = Color.WHITE;
           //     switch (comp.name) {
          //         case "Enemy":
          //              color = Color.RED;
          //              break;
         //           case "Wall":
         //               color = Color.GREEN;
        //                break;
       //             case "HalfWall":
        //                color = Color.CYAN;
         //               break;
       //             case "Player":
        //                color = Color.BLUE;
        //                break;
       //         }
         //       graphics.drawRect((int)(gameWorld.toPixelsX(comp.getPositionX()) - gameWorld.toPixelsXLength(comp.getWidth())),
       //                (int) (gameWorld.toPixelsY(comp.getPositionY()) - gameWorld.toPixelsYLength(comp.getHeight()/2)),
     //                   (int) gameWorld.toPixelsXLength(comp.getWidth()),
       //                 (int) gameWorld.toPixelsYLength(comp.getHeight()), color);
       //         gameWorld.player.draw(graphics, gameWorld);
            //}
            if(rightStrength > 0){
                drawAimLines();
            }
        }
    }

    public void setLineCoordinates(int lineAmt, int px, int py, float[] targX, float[] targY){

        lineAmount = lineAmt;
        playerx = px;
        playery = py;

        for(int i = 0; i < lineAmt; i++){
            targetx[i] = (int)(targX[i] + px);
            targety[i] = (int)(targY[i] + py);
        }
    }

    public void drawAimLines(){
        for(int i = 0; i < lineAmount ; i++){
            graphics.drawLine(playerx, playery, targetx[i], targety[i], Color.RED);
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    //adds and remove drawables
    public void addDrawable(DrawableComponent drawable){
        drawables.add(drawable);
    }

    public void removeDrawable(DrawableComponent drawable){
        if(drawables.contains(drawable))
            drawables.remove(drawable);
    }

    //sets the destination for the world movement. It moves the map and all the GO other than the player
    public void setWorldDestination(int jx, int jy, float deltaTime){

        float normalizedX = -((float) jx-50);
        float normalizedY = -((float) jy-50);

        destinationX = currentBackgroundX + (int)((gameWorld.toPixelsTouchX(movementDistance) * normalizedX)  * deltaTime);
        destinationY = currentBackgroundY + (int)((gameWorld.toPixelsTouchY(movementDistance) * normalizedY) * deltaTime);

        //Log.d("setWorldDestination","curr x"+currentBackgroundX+"y"+currentBackgroundY);
        //Log.d("setWorldDestination","dest x"+destinationX+"y"+destinationY);

        onBorderX = false;
        onBorderY = false;

        //check that we don't move outside the background boundaries
        if(destinationX > 0){
            destinationX = 0;
            onBorderX = true;
        }
        if(destinationY > 0){
            destinationY = 0;
            onBorderY = true;
        }
        if(destinationX < -(AssetManager.background.getWidth() - graphics.getWidth())){
            destinationX = -(AssetManager.background.getWidth() - graphics.getWidth());
            onBorderX = true;
        }
        if(destinationY < - (AssetManager.background.getHeight() - graphics.getHeight())){
            destinationY = - (AssetManager.background.getHeight() - graphics.getHeight());
            onBorderY = true;
        }

        if((currentBackgroundX != destinationX || currentBackgroundY != destinationY)) {
            if(!onBorderX)
                currentBackgroundX = movementX(currentBackgroundX, normalizedX, deltaTime);
            if(!onBorderY)
                currentBackgroundY = movementY(currentBackgroundY, normalizedY, deltaTime);
            for (DrawableComponent drawable : drawables) {
                GameObject gameObject = drawable.owner;
                if (!gameObject.name.equals("Player")){
                    gameObject.updatePosition((int) (gameWorld.inViewPositionX(gameObject.worldX)),
                            (int) (gameWorld.inViewPositionY(gameObject.worldY)));
                }
            }
        }

    }

    public int movementX(float startingX, float normalizedX, float deltaTime){
        return (int) (startingX + ((int)((movementDistance * normalizedX)  * deltaTime)));
    }
    public int movementY(float startingY, float normalizedY, float deltaTime){
        return (int) (startingY + ((int)((movementDistance * normalizedY)  * deltaTime)));
    }

    //TODO not actually implemented, rotates the player to face the destination
    public void rotatePlayer(){
        //unico modo veloce è usare una spritesheet
        float x = destinationX - initialPlayerLookX;
        float y = destinationY - initialPlayerLookY;
        double angle = Math.toDegrees(Math.atan2(x, y));
        drawables.get(0);
    }

    //background x and y
    public float getBackgroundX(){return currentBackgroundX;}
    public float getBackgroundY(){return currentBackgroundY;}
}
