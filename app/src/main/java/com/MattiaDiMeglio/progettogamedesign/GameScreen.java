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
    int rightX = 50, rightY = 50, oldRightX = 50, oldRightY = 50, rightAngle = 0, rightStrength = 0, oldRightAngle = 0, oldRightStrength = 0;

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
                oldRightX = rightX;
                oldRightY = rightY;
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
                    gameWorld.update(leftX, leftY, deltaTime, oldRightX, oldRightY, oldRightStrength, isShooting);
                    isShooting = false;
                }
                else gameWorld.update(leftX, leftY, deltaTime, rightX, rightY, rightStrength, isShooting);
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

        //draw the background
        graphics.drawPixmap(AssetManager.background, (int)currentBackgroundX, (int)currentBackgroundY);
        //draw the grid
        graphics.drawLine((int)gameWorld.toPixelsX(0), (int)gameWorld.toPixelsY(-15f),
                (int)gameWorld.toPixelsX(0), (int)gameWorld.toPixelsY(15), Color.WHITE);
        //draw the drawables
        if(!drawables.isEmpty()) {
            for (DrawableComponent drawable : drawables) {
                drawable.Draw(graphics);
            }

            if(rightStrength > 0){
                drawAimLines();
            }
        }
        //To test the body positions
        drawBodies();
    }

    private void drawBodies(){
        //for testing, draws the player body
        for(GameObject gameObject : gameWorld.gameObjects){
            PhysicsComponent comp = (PhysicsComponent) gameObject.getComponent(ComponentType.Physics);
            int color = Color.WHITE;
            switch (comp.name) {
                case "Enemy":
                    color = Color.RED;
                    break;
                case "Wall":
                    color = Color.GREEN;
                    break;
                case "HalfWall":
                    color = Color.CYAN;
                    break;
                case "Player":
                    color = Color.BLUE;
                    break;
            }
            comp.Draw(graphics, gameWorld, color);
        }
    }

    public void setLineCoordinates(int lineAmt, float px, float py, float[] targX, float[] targY){
        //ricalcolare completamente il cristo
        //bisogna passare solo il range e la direzione
        //e rifare il calcolo da 0, per evitare che non disegni un cerchio cristiano.
        //le coordinate fisiche son corrette

        lineAmount = lineAmt;
        playerx = (int)gameWorld.toPixelsX(px);
        playery = (int)gameWorld.toPixelsY(py);

        for(int i = 0; i < lineAmt; i++){
            targetx[i] = (int)(gameWorld.toPixelsX(targX[i] + px));
            targety[i] = (int)(gameWorld.toPixelsY(targY[i] + py));
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

        destinationX = currentBackgroundX + (int)((gameWorld.toPixelsXLengthNonBuffer(gameWorld.player.getMovedX()) * normalizedX)  * deltaTime);
        destinationY = currentBackgroundY + (int)((gameWorld.toPixelsYLengthNonBuffer(gameWorld.player.getMovedY()) * normalizedY) * deltaTime);

        Log.d("backg", gameWorld.toPixelsXLengthNonBuffer(gameWorld.player.getMovedX()) + ", " + gameWorld.toPixelsYLengthNonBuffer(gameWorld.player.getMovedY()));

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
        return (int) (startingX + (int)((gameWorld.toPixelsXLengthNonBuffer(gameWorld.player.getMovedX()) * normalizedX)  * deltaTime));
    }
    public int movementY(float startingY, float normalizedY, float deltaTime){
        return (int) (startingY + (int)((gameWorld.toPixelsYLengthNonBuffer(gameWorld.player.getMovedY()) * normalizedY) * deltaTime));
    }


    //background x and y
    public float getBackgroundX(){return currentBackgroundX;}
    public float getBackgroundY(){return currentBackgroundY;}
}
