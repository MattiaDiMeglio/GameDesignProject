package com.MattiaDiMeglio.progettogamedesign;

import android.graphics.Color;
import android.util.Log;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.Screen;
import com.google.fpl.liquidfun.World;

import java.util.ArrayList;
import java.util.List;


//The main game screen
public class GameScreen extends Screen {
    enum GameState {//game states
        Ready,
        Running,
        Paused,
        GameOver
    }
    GameWorld gameWorld;
    Graphics graphics;
    List<DrawableComponent> drawables;
    GameState gameState = GameState.Ready;

    private static final float XMIN = -10, XMAX = 10, YMIN = -15, YMAX = 15;//physics world dimensions

    //background coordinates to move the world
    int destinationX = 0, destinationY = 0, currentBackgroundX = 0, currentBackgroundY = 0;
    float percentage = 0f;//movement percentage to lerp
    //division factor for the grid
    float orizontalFactor = 20f/13f;
    float verticalFactor = 30f/20f;
    //player looking direction
    float initialPlayerLookX, initialPlayerLookY;

    public GameScreen(Game game, int width, int height) {
        super(game);
        graphics = game.getGraphics();//we get the graphics from the framework, to draw on screen
        //world sizes
        Box physicalSize = new Box(XMIN, YMIN, XMAX, YMAX),
                screenSize   = new Box(0, 0, width,height);
        //list of active drawables
        drawables = new ArrayList<DrawableComponent>();
        gameWorld = new GameWorld(this, physicalSize, screenSize);//creates a new GameWorld

        //initial direction
        initialPlayerLookY = graphics.getHeight();
        initialPlayerLookX = 0;
    }

    //gamescreen update, calls the gameworld update
    @Override
    public void update(float deltaTime) {
        //gets all the touchevents
        List<Input.TouchEvent> touchEvents = game.getInput().getTouchEvents();
        game.getInput().getKeyEvents();
        switch(gameState) {
            case Ready:
                gameState = GameState.Running;
                break;
            case Running:
                gameWorld.update(deltaTime, touchEvents);
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
        worldMovement();//we move the world
        //draw the background
        graphics.drawPixmap(AssetManager.background, (int)currentBackgroundX, (int)currentBackgroundY);
        //draw the grid
        float i = XMIN;
        while(i < XMAX){
            graphics.drawRect((int)gameWorld.toPixelsX(i), 0, 2, gameWorld.bufferHeight, Color.BLACK);
            i += orizontalFactor;
        }
        i = YMIN;
        while(i < YMAX) {
            graphics.drawRect(0, (int) gameWorld.toPixelsY(i), gameWorld.bufferWidth, 2, Color.BLACK);
            i += verticalFactor;
        }
        //graphics.drawRect(resultx, resulty,(int) gameWorld.toPixelsXLength(0.5f),(int)gameWorld.toPixelsYLength(0.5f), Color.BLACK );
        //draw the drawables
        if(!drawables.isEmpty()) {
            for (DrawableComponent drawable : drawables) {
                drawable.Draw(graphics);
            }
            //for testing, draws the player body
            gameWorld.player.draw(graphics, gameWorld);
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


    public void setWorldDestination(int x, int y){
        destinationX -= x;
        destinationY -= y;
        //check that we don't move outside the background boundaries
        if(destinationX > 0){destinationX = 0;}
        if(destinationY > 0){destinationY = 0;}
        if(destinationX < -(AssetManager.background.getWidth() - graphics.getWidth())){
            destinationX = -(AssetManager.background.getWidth() - graphics.getWidth());
        }
        if(destinationY < - (AssetManager.background.getHeight() - graphics.getHeight())){
            destinationY = - (AssetManager.background.getHeight() - graphics.getHeight());
        }
        percentage = 0f;//reset the percentage for lerp
        Log.d("Destination", "x: " + destinationX + " y: " + destinationY);
    }

    //lerpx and lerp y
    public int movementX(float startingX, float destinationX, float percentage){
        return (int) (startingX + percentage * (destinationX - startingX));
    }
    public int movementY(float startingY, float destinationY, float percentage){
        return (int) (startingY + percentage * (destinationY - startingY));
    }

    //we move the background and all the drawables but the player
    public void worldMovement(){
        if(percentage < 1 && currentBackgroundX != destinationX || currentBackgroundY != destinationY) {
            percentage += 0.1f;
            float vX, vY;
            currentBackgroundX = movementX(currentBackgroundX, destinationX, percentage);
            currentBackgroundY = movementY(currentBackgroundY, destinationY, percentage);
            for (DrawableComponent drawable : drawables) {
                GameObject gameObject = drawable.owner;
                if (gameObject.name != "Player"){
                    gameObject.updatePosition((int) (gameWorld.inViewPositionX(gameObject.worldX)),
                            (int) (gameWorld.inViewPositionY(gameObject.worldY)));
                }
            }
        }
    }

    //not actually implemented, rotates the player to face the destination
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
