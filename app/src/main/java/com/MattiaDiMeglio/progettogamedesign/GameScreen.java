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
import java.util.Stack;

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

    Stack<AimLine> aimLineStack;

    float leftX = 0, leftY = 0, leftAngle = 0, leftStrength = 0;
    float rightX = 0, rightY = 0, oldRightX = 0, oldRightY = 0, rightAngle = 0, rightStrength = 0, oldRightAngle = 0, oldRightStrength = 0;

    boolean isShooting = false;

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

        aimLineStack = new Stack<>();

        leftJoystick = game.getLeftJoystick();
        rightJoystick = game.getRightJoystick();

        leftJoystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                leftX = (float) (leftJoystick.getNormalizedX() - 50) / 50;
                leftY = (float) (leftJoystick.getNormalizedY() - 50) / 50;
                leftAngle = angle;
                leftStrength = strength;
            }
        });

        rightJoystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                oldRightX = rightX;
                oldRightY = rightY;

                float rightVectorX = rightJoystick.getNormalizedX();
                float rightVectorY = rightJoystick.getNormalizedY();

                rightVectorX = (rightVectorX-50)/50;
                rightVectorY = (rightVectorY-50)/50;
                float length = (float) Math.sqrt((rightVectorX * rightVectorX) + (rightVectorY * rightVectorY));

                rightX = rightVectorX/length;
                rightY = rightVectorY/length;
                rightAngle = angle;
                rightStrength = strength;

                if(!(rightJoystick.getNormalizedX() == 50 && rightJoystick.getNormalizedY() == 50)){
                    oldRightAngle = angle;
                    oldRightStrength = strength;
                }
                else isShooting = true;
            }
        });
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
                    gameWorld.update(leftX, leftY, deltaTime, oldRightX, oldRightY, oldRightAngle, oldRightStrength, isShooting);
                    isShooting = false;
                }
                else gameWorld.update(leftX, leftY, deltaTime, rightX, rightY, rightAngle, rightStrength, isShooting);
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
        graphics.drawPixmap(AssetManager.backgroundPixmap, (int)currentBackgroundX, (int)currentBackgroundY);
        //draw the grid
        graphics.drawLine((int)gameWorld.toPixelsX(0), (int)gameWorld.toPixelsY(-15f),
                (int)gameWorld.toPixelsX(0), (int)gameWorld.toPixelsY(15), Color.WHITE);
        //draw the drawables
        if(!drawables.isEmpty()) {
            for (DrawableComponent drawable : drawables) {
                drawable.Draw(graphics);
            }
        }
        drawAimLines();
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

    public void drawAimLines(){
        while(!aimLineStack.isEmpty()){
            AimLine aimLine = aimLineStack.pop();
            graphics.drawLine(aimLine.startX, aimLine.startY, aimLine.targetX, aimLine.targetY, Color.GREEN);
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
    public void setWorldDestination(float jx, float jy, float deltaTime){
        gameWorld.player.CantMove();


        float normalizedX = -(jx);//*50);
        float normalizedY = -(jy);//*50);

        //settati i wordposX e Y del player basare il movimento sulla differenza
        //destinationX = currentBackgroundX + (int)((gameWorld.toPixelsXLengthNonBuffer(gameWorld.player.getMovedX()) * normalizedX));// * deltaTime);
        //destinationY = currentBackgroundY + (int)((gameWorld.toPixelsYLengthNonBuffer(gameWorld.player.getMovedY()) * normalizedY));// * deltaTime);

        destinationX = -(gameWorld.player.worldX - gameWorld.bufferWidth/2); //(int)(currentBackgroundX + normalizedX * 8);
        destinationY = -(gameWorld.player.worldY - gameWorld.bufferHeight/2); //(int)(currentBackgroundY + normalizedY * 8);
        //Log.d("playerWorld", "w: " + gameWorld.player.worldX + ", " +gameWorld.player.worldY);

        onBorderX = false;
        onBorderY = false;

        //check that we don't move outside the background boundaries
        /*
        if(destinationX > 0){
            destinationX = 0;
            onBorderX = true;
        }
        if(destinationY > 0){
            destinationY = 0;
            onBorderY = true;
        }
        if(destinationX < -(AssetManager.backgroundPixmap.getWidth() - graphics.getWidth())){
            destinationX = -(AssetManager.backgroundPixmap.getWidth() - graphics.getWidth());
            onBorderX = true;
        }
        if(destinationY < - (AssetManager.backgroundPixmap.getHeight() - graphics.getHeight())){
            destinationY = - (AssetManager.backgroundPixmap.getHeight() - graphics.getHeight());
            onBorderY = true;
        }*/

        if((currentBackgroundX != destinationX || currentBackgroundY != destinationY)) {
            if(!onBorderX)
                currentBackgroundX = destinationX;//movementX(currentBackgroundX, normalizedX, deltaTime);
            if(!onBorderY)
                currentBackgroundY = destinationY;//movementY(currentBackgroundY, normalizedY, deltaTime);
            for (DrawableComponent drawable : drawables) {
                GameObject gameObject = drawable.owner;
                //if (!gameObject.name.equals("Player")){
                    gameObject.updatePosition((int) (gameWorld.inViewPositionX(gameObject.worldX)),
                            (int) (gameWorld.inViewPositionY(gameObject.worldY)));
                //}
            }
        }

        gameWorld.player.CanMove();
    }

    public int movementX(float startingX, float normalizedX, float deltaTime){
        return (int) (startingX + (int)((gameWorld.toPixelsXLengthNonBuffer(gameWorld.player.getMovedX()*2) * normalizedX) ));// * deltaTime));
    }
    public int movementY(float startingY, float normalizedY, float deltaTime){
        return (int) (startingY + (int)((gameWorld.toPixelsYLengthNonBuffer(gameWorld.player.getMovedY()*2) * normalizedY)));// * deltaTime));
    }


    //background x and y
    public float getBackgroundX(){return currentBackgroundX;}
    public float getBackgroundY(){return currentBackgroundY;}
}
